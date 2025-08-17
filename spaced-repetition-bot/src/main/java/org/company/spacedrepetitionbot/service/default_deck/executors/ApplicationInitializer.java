package org.company.spacedrepetitionbot.service.default_deck.executors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.config.AppProperties;
import org.company.spacedrepetitionbot.model.Deck;
import org.company.spacedrepetitionbot.service.DeckService;
import org.company.spacedrepetitionbot.service.default_deck.event.SyncEventDTO;
import org.company.spacedrepetitionbot.service.default_deck.processors.SyncEventProcessor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationInitializer {
    private final AppProperties appProperties;
    private final DeckService deckService;
    private final SyncEventProcessor syncEventProcessor;

    @EventListener(ContextRefreshedEvent.class)
    public void initialize() {
        if (appProperties.getDefaultDeck().getSync().isInitialEnabled()) {
            Deck deck = deckService.initializeDefaultDeck();
            syncEventProcessor.processSyncEvent(new SyncEventDTO(deck.getDeckId(), true, null));
        }
    }
}
