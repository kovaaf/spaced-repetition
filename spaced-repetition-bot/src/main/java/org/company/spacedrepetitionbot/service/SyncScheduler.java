package org.company.spacedrepetitionbot.service;

import lombok.RequiredArgsConstructor;
import org.company.spacedrepetitionbot.config.AppProperties;
import org.company.spacedrepetitionbot.kafka.consumer_producer.KafkaSyncEventProducer;
import org.company.spacedrepetitionbot.kafka.event.SyncEventDTO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class SyncScheduler {
    private final KafkaSyncEventProducer syncEventProducer;
    private final DeckService deckService;
    private final AppProperties appProperties;

    @Scheduled(cron = "${app.default-deck.sync.cron}")
    public void scheduledSync() {
        deckService.getOptionalDeckByName(appProperties.getDefaultDeck()
                        .getName())
                .ifPresentOrElse(
                        deck -> syncEventProducer.sendSyncEvent(new SyncEventDTO(
                                deck.getDeckId(),
                                false,
                                Collections.emptyList())),
                        () -> syncEventProducer.sendSyncEvent(new SyncEventDTO(null, true, Collections.emptyList())));
    }
}
