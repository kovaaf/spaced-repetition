package org.company.spacedrepetitionbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.config.AppProperties;
import org.company.spacedrepetitionbot.kafka.consumer_producer.KafkaSyncEventProducer;
import org.company.spacedrepetitionbot.kafka.event.SyncEventDTO;
import org.company.spacedrepetitionbot.model.Deck;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationInitializer {
    private final AppProperties appProperties;
    private final DeckService deckService;
    private final KafkaSyncEventProducer syncEventProducer;

    @EventListener(ContextRefreshedEvent.class)
    public void initialize() {
        if (appProperties.getDefaultDeck()
                .getSync()
                .isInitialEnabled()) {
            Deck deck = deckService.initializeDefaultDeck();
            syncEventProducer.sendSyncEvent(new SyncEventDTO(deck.getDeckId(), true, null));
        }
    }
}
