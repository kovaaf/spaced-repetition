package org.company.spacedrepetitionbot.repository;

import org.company.spacedrepetitionbot.constants.Status;
import org.company.spacedrepetitionbot.model.Card;
import org.company.spacedrepetitionbot.model.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByDeckAndFrontIgnoreCase(Deck deck, String front);

    List<Card> findByDeck(Deck deck);

    boolean existsByDeckAndFrontIgnoreCase(Deck targetDeck, String cardFront);

    @Query("SELECT c FROM Card c WHERE c.deck = :deck AND " +
            "c.status NOT IN :excludedStatuses ORDER BY c.nextReviewTime ASC LIMIT 1")
    Optional<Card> findCardByDeckWithNearestNextReviewTime(Deck deck, List<Status> excludedStatuses);

    List<Card> findByStatus(Status status);

    Optional<Card> findBySourceFilePathAndSourceHeading(String sourceFilePath, String sourceHeading);

    int countByDeck(Deck deck);

    @Modifying
    @Query("DELETE FROM Card c WHERE c.deck = :deck AND c.sourceFilePath NOT IN :processedFilePaths")
    int deleteByDeckAndSourceFilePathNotIn(Deck deck, Set<String> processedFilePaths);

    void deleteByDeckAndSourceFilePathAndFrontNotIn(Deck deck, String relativePath, List<String> validFronts);

    void deleteByDeckAndSourceFilePath(Deck deck, String relativePath);

    int countByDeckAndSourceFilePath(Deck deck, String filePath);
}
