package org.company.spacedrepetitionbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.exception.SyncException;
import org.company.spacedrepetitionbot.kafka.event.SyncEventDTO;
import org.company.spacedrepetitionbot.model.Deck;
import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RepoSynchronizer {
    private final DeckService deckService;
    private final FileSyncProcessor fileSyncProcessor;
    private final GitSyncOperator gitSyncOperator;

    @Transactional
    public void sync(SyncEventDTO event, Deck deck) {
        try {
            if (deck == null) {
                deck = deckService.initializeDefaultDeck();
            }

            try (Git git = gitSyncOperator.prepareRepository()) {
                if (gitSyncOperator.isSyncRequired(git, deck, event)) {
                    executeSync(git, deck, event);
                }
            }
        } catch (Exception e) {
            handleSyncError(deck, e);
        }
    }

    private void executeSync(Git git, Deck deck, SyncEventDTO event) throws Exception {
        log.debug("Sync started for deck: {}", deck.getName());
        String latestCommit = gitSyncOperator.getLatestCommit(git);

        if (event.isForceFullSync()) {
            fileSyncProcessor.processAllMarkdownFiles(deck);
        } else {
            fileSyncProcessor.processChangedFiles(deck, event.getChangedFiles());
        }

        updateDeckMetadata(deck, latestCommit);
    }

    private void updateDeckMetadata(Deck deck, String commitHash) {
        log.debug("Deck hash {} replaced with {}", deck.getLastSyncCommit(), commitHash);
        deck.setLastSyncCommit(commitHash);
        deckService.save(deck);
    }

    private void handleSyncError(Deck deck, Exception e) {
        String deckInfo = (deck != null) ? "deckId: " + deck.getDeckId() : "global sync";
        log.error("Sync failed for {}", deckInfo, e);
        throw new SyncException("Sync failed: " + deckInfo, e);
    }
}
