package org.company.spacedrepetitionbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.model.Deck;
import org.company.spacedrepetitionbot.config.AppProperties;
import org.company.spacedrepetitionbot.dto.WebhookPayload;
import org.company.spacedrepetitionbot.kafka.consumer_producer.KafkaSyncEventProducer;
import org.company.spacedrepetitionbot.kafka.event.SyncEventDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GitHubWebhookService {
    private static final String REF_PREFIX = "refs/heads/";
    private static final String PUSH_EVENT = "push";
    private final AppProperties appProperties;
    private final RepoUrlNormalizer repoUrlNormalizer;
    private final ChangedFilesProcessor changedFilesProcessor;
    private final KafkaSyncEventProducer syncEventProducer;
    private final DeckService deckService;
    private final WebhookValidator webhookValidator;

    public void processWebhook(String event, WebhookPayload payload, String signature) {
        webhookValidator.validateSignature(payload, signature); // Валидация подписи
        handleEvent(event, payload);
    }

    private void handleEvent(String event, WebhookPayload payload) {
        AppProperties.DefaultDeckConfig defaultDeck = appProperties.getDefaultDeck();

        if (!isExpectedRepoAndBranch(payload, defaultDeck)) {
            log.debug("Webhook ignored for repository: {}, branch: {}",
                    payload.repository().fullName(),
                    payload.ref());
            return;
        }

        if (PUSH_EVENT.equals(event)) {
            handlePushEvent(payload, defaultDeck);
        } else {
            log.debug("Unhandled event type: {}", event);
        }
    }

    private void handlePushEvent(WebhookPayload payload, AppProperties.DefaultDeckConfig deckConfig) {
        Deck deck = deckService.getDeckByName(deckConfig.getName());
        List<String> changedFiles = changedFilesProcessor.getChangedFiles(payload, deckConfig);

        syncEventProducer.sendSyncEvent(new SyncEventDTO(deck.getDeckId(), false, changedFiles));
    }

    private boolean isExpectedRepoAndBranch(WebhookPayload payload, AppProperties.DefaultDeckConfig defaultDeck) {
        String expectedRepo = repoUrlNormalizer.normalize(defaultDeck.getRepo().getUrl());
        String expectedBranch = REF_PREFIX + defaultDeck.getRepo().getBranch();

        return expectedRepo.equals(payload.repository().fullName()) &&
                expectedBranch.equals(payload.ref());
    }
}
