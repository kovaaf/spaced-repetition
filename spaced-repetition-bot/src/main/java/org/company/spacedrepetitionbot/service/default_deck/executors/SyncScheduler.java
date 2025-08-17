package org.company.spacedrepetitionbot.service.default_deck.executors;

import lombok.RequiredArgsConstructor;
import org.company.spacedrepetitionbot.config.AppProperties;
import org.company.spacedrepetitionbot.service.DeckService;
import org.company.spacedrepetitionbot.service.default_deck.event.SyncEventDTO;
import org.company.spacedrepetitionbot.service.default_deck.processors.SyncEventProcessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class SyncScheduler {
    private final SyncEventProcessor syncEventProcessor;
    private final DeckService deckService;
    private final AppProperties appProperties;

    @Scheduled(cron = "${app.default-deck.sync.cron}")
    public void scheduledSync() {
        deckService.getOptionalDeckByName(appProperties.getDefaultDeck().getName()).ifPresentOrElse(
                deck -> syncEventProcessor.processSyncEvent(new SyncEventDTO(
                        deck.getDeckId(),
                        false,
                        Collections.emptyList())),
                () -> syncEventProcessor.processSyncEvent(new SyncEventDTO(null, true, Collections.emptyList())));
    }
}
