package org.company.spacedrepetitionbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.model.Deck;
import org.company.spacedrepetitionbot.kafka.event.SyncEventDTO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SyncEventProcessor {
    private final DeckService deckService;
    private final RepoSynchronizer repoSynchronizer;
    private final UserDeckSynchronizer userDeckSynchronizer;

    @Transactional
    public void processSyncEvent(SyncEventDTO event) {
        Deck deck = event.getDeckId() != null ?
                deckService.getDeckById(event.getDeckId()).orElse(null) :
                null;

        repoSynchronizer.sync(event, deck);

        if (deck != null && deck.isDefault()) {
            userDeckSynchronizer.syncUserDecks(deck);
        }

        log.info("Sync event processed: {}", event);
    }
}
