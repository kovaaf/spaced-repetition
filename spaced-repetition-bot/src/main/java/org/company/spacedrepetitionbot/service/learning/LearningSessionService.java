package org.company.spacedrepetitionbot.service.learning;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.company.spacedrepetitionbot.config.SessionProperties;
import org.company.spacedrepetitionbot.constants.Quality;
import org.company.spacedrepetitionbot.constants.Status;
import org.company.spacedrepetitionbot.exception.DeckNotFoundException;
import org.company.spacedrepetitionbot.exception.SessionCompletedException;
import org.company.spacedrepetitionbot.model.Card;
import org.company.spacedrepetitionbot.model.Deck;
import org.company.spacedrepetitionbot.model.LearningSession;
import org.company.spacedrepetitionbot.repository.CardRepository;
import org.company.spacedrepetitionbot.repository.DeckRepository;
import org.company.spacedrepetitionbot.repository.LearningSessionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class LearningSessionService {
    private final LearningSessionRepository learningSessionRepository;
    private final CardRepository cardRepository;
    private final SessionProperties sessionProperties;
    private final SM2Algorithm sm2Algorithm;
    private final DeckRepository deckRepository;

    //    @Transactional(readOnly = true)
    //    public Card getCardFromDeckWithMinimumInterval(Long userChatId, String deckName) {
    //        Deck deck = getDeckByOwnerIdAndDeckNameOrThrow(userChatId, deckName);
    //        return cardRepository.findCardByDeckWithNearestNextReviewTime(deck, List.of(Status.SUSPENDED, Status
    //        .BURIED))
    //                .orElseThrow(() -> new EntityNotFoundException("No cards found"));
    //    }

    //    @Transactional
    //    public String updateCardWithAnswer(Long chatId, String deckName, String cardFront, Quality quality) {
    //        Card card = getCardByFrontAndDeckNameAndUserIdOrThrow(chatId, deckName, cardFront);
    //        if (card.getStatus() == Status.BURIED || card.getStatus() == Status.SUSPENDED) {
    //            return "Карточка приостановлена"; // Игнорируем обработку
    //        }
    //        String front = card.getFront();
    //
    //        card = sm2Algorithm.updateCardWithSMTwoAlgorithm(card, quality);
    //        cardRepository.save(card);
    //
    //        return String.format(ANSWER.getMessage(), front, LEARN_NEXT.getAlias(), deckName, PREV_CARD.getAlias());
    //    }

    //    @Scheduled(cron = "0 0 0 * * ?")
    //    public void resetBuriedCards() {
    //        cardRepository.findByStatus(Status.BURIED).forEach(card -> {
    //            // TODO назначать статус в зависимости от старости как в алгоритме
    //            card.setStatus(Status.REVIEW_YOUNG);
    //            cardRepository.save(card);
    //        });
    //    }

    @Transactional(readOnly = true)
    public String getCardAnswerById(Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new EntityNotFoundException("Card not found"));
        return "Вопрос:\n" + card.getFront() + "\n\nОтвет:\n" + card.getBack();
    }

    @Transactional
    public void updateCardWithAnswer(Long cardId, Quality quality) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new EntityNotFoundException("Card not found"));

        if (card.getStatus() == Status.BURIED || card.getStatus() == Status.SUSPENDED) {
            return; // Игнорируем обработку
        }

        sm2Algorithm.updateCardWithSMTwoAlgorithm(card, quality);
        cardRepository.save(card);
    }

    @Transactional(readOnly = true)
    public Card getNextCard(Long deckId) {
        LearningSession session = getOrCreateSession(deckId);
        return getNextCardInSession(session.getSessionId());
    }

    @Transactional
    public LearningSession getOrCreateSession(Long deckId) {
        LocalDateTime now = LocalDateTime.now();
        Deck deck = deckRepository.findById(deckId).orElseThrow(() -> new DeckNotFoundException("Колода не найдена"));

        return learningSessionRepository.findByDeck(deck).orElseGet(() -> createNewSession(deck, now));
    }

    private LearningSession createNewSession(Deck deck, LocalDateTime now) {
        LearningSession session = new LearningSession();
        session.setDeck(deck);
        session.setCreatedAt(now);

        // Получаем карточки для изучения с приоритетом: LEARNING/RELEARNING -> NEW
        List<Card> learningCards = cardRepository.findCardsForSession(
                deck,
                List.of(Status.LEARNING, Status.RELEARNING),
                PageRequest.of(0, sessionProperties.getMaxNewCards()));

        // Если не набрали достаточно, добавляем NEW
        if (learningCards.size() < sessionProperties.getMaxNewCards()) {
            int remaining = sessionProperties.getMaxNewCards() - learningCards.size();
            List<Card> newCards = cardRepository.findCardsForSession(
                    deck,
                    List.of(Status.NEW),
                    PageRequest.of(0, remaining));
            learningCards.addAll(newCards);
        }

        List<Card> reviewCards = cardRepository.findCardsForSession(
                deck,
                List.of(Status.REVIEW_YOUNG, Status.REVIEW_MATURE),
                PageRequest.of(0, sessionProperties.getMaxReviewCards()));

        List<Card> allCards = Stream.concat(learningCards.stream(), reviewCards.stream()).collect(Collectors.toList());

        session.setCards(allCards);
        return learningSessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public Card getNextCardInSession(Long sessionId) {
        return learningSessionRepository.findById(sessionId).map(session -> {
            if (session.getCards().isEmpty()) {
                throw new SessionCompletedException("Сессия завершена! Все карточки изучены.");
            }
            // Возвращаем первую карточку в списке
            return session.getCards().get(0);
        }).orElseThrow(() -> new EntityNotFoundException("Session not found"));
    }

    @Scheduled(cron = "${app.session.reset-cron}")
    @Transactional
    public void resetExpiredSessions() {
        learningSessionRepository.deleteAll();
    }

    @Transactional
    public void removeCardFromSession(Long sessionId, Long cardId) {
        learningSessionRepository.findById(sessionId).ifPresent(session -> {
            if (!session.getCards().isEmpty() && session.getCards().get(0).getCardId().equals(cardId)) {
                session.getCards().remove(0);
                learningSessionRepository.save(session);
            }
        });
    }

    @Transactional
    public int getRemainingCardsCount(Long sessionId) {
        return learningSessionRepository.findById(sessionId).map(session -> session.getCards().size()).orElse(0);
    }

    @Transactional(readOnly = true)
    public int countNewCardsInSession(Long sessionId) {
        return learningSessionRepository.findById(sessionId)
                .map(session -> (int) session.getCards()
                        .stream()
                        .filter(card -> EnumSet.of(Status.NEW, Status.LEARNING, Status.RELEARNING)
                                .contains(card.getStatus()))
                        .count())
                .orElse(0);
    }

    @Transactional(readOnly = true)
    public int countReviewCardsInSession(Long sessionId) {
        return learningSessionRepository.findById(sessionId)
                .map(session -> (int) session.getCards()
                        .stream()
                        .filter(card -> EnumSet.of(Status.REVIEW_YOUNG, Status.REVIEW_MATURE)
                                .contains(card.getStatus()))
                        .count())
                .orElse(0);
    }
}
