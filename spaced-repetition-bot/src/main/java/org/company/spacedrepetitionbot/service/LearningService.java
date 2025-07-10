package org.company.spacedrepetitionbot.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.constants.Quality;
import org.company.spacedrepetitionbot.constants.Status;
import org.company.spacedrepetitionbot.model.Card;
import org.company.spacedrepetitionbot.model.Deck;
import org.company.spacedrepetitionbot.model.UserInfo;
import org.company.spacedrepetitionbot.repository.CardRepository;
import org.company.spacedrepetitionbot.repository.DeckRepository;
import org.company.spacedrepetitionbot.repository.UserInfoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.company.spacedrepetitionbot.command.constant.Command.LEARN_NEXT;
import static org.company.spacedrepetitionbot.command.constant.Command.PREV_CARD;
import static org.company.spacedrepetitionbot.constants.MessageConstants.*;

@Slf4j
@Service
public class LearningService {
    private final UserInfoRepository userInfoRepository;
    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final SM2Algorithm SM2Algorithm;

    public LearningService(UserInfoRepository userInfoRepository, DeckRepository deckRepository, CardRepository cardRepository, SM2Algorithm SM2Algorithm) {
        this.userInfoRepository = userInfoRepository;
        this.deckRepository = deckRepository;
        this.cardRepository = cardRepository;
        this.SM2Algorithm = SM2Algorithm;
    }

    @Transactional(readOnly = true)
    public Card getCardFromDeckWithMinimumInterval(Long userChatId, String deckName) {
        Deck deck = getDeckByOwnerIdAndDeckNameOrThrow(userChatId, deckName);
        return cardRepository.findCardByDeckWithNearestNextReviewTime(deck, List.of(Status.SUSPENDED, Status.BURIED))
                .orElseThrow(() -> new EntityNotFoundException("No cards found"));
    }

    @Transactional(readOnly = true)
    public Card getCardAnswerByFront(Long chatId, String deckName, String cardFront) {
        return getCardByFrontAndDeckNameAndUserIdOrThrow(chatId, deckName, cardFront);
    }

    @Transactional
    public String updateCardWithAnswer(Long chatId, String deckName, String cardFront, Quality quality) {
        Card card = getCardByFrontAndDeckNameAndUserIdOrThrow(chatId, deckName, cardFront);
        if (card.getStatus() == Status.BURIED || card.getStatus() == Status.SUSPENDED) {
            return "Карточка приостановлена"; // Игнорируем обработку
        }
        String front = card.getFront();

        card = SM2Algorithm.updateCardWithSMTwoAlgorithm(card, quality);
        cardRepository.save(card);

        return String.format(ANSWER.getMessage(),
                front,
                LEARN_NEXT.getAlias(), deckName,
                PREV_CARD.getAlias());
    }

    @Transactional(readOnly = true)
    public String getCardAnswerById(Long cardId) {
        return cardRepository.findById(cardId)
                .map(Card::getBack)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));
    }

    @Transactional
    public void updateCardWithAnswer(Long cardId, Quality quality) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));
        if (card.getStatus() == Status.BURIED || card.getStatus() == Status.SUSPENDED) {
            return; // Игнорируем обработку
        }
        SM2Algorithm.updateCardWithSMTwoAlgorithm(card, quality);
        cardRepository.save(card);
    }

    @Transactional(readOnly = true)
    public Card getNextCard(Long deckId) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new EntityNotFoundException("Deck not found"));

        return cardRepository.findCardByDeckWithNearestNextReviewTime(deck, List.of(Status.SUSPENDED, Status.BURIED))
                .orElseThrow(() -> new EntityNotFoundException("No cards found"));
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void resetBuriedCards() {
        cardRepository.findByStatus(Status.BURIED).forEach(card -> {
            // TODO назначать статус в зависимости от старости как в алгоритме
            card.setStatus(Status.REVIEW_YOUNG);
        });
    }

    private Deck getDeckByOwnerIdAndDeckNameOrThrow(Long userChatId, String deckName) {
        UserInfo owner = getUserIdOrThrow(userChatId);
        return getDeckOwnerInfoAndDeckNameOrThrow(owner, deckName);
    }

    private Card getCardByFrontAndDeckNameAndUserIdOrThrow(Long userChatId, String deckName, String cardFront) {
        Deck deck = getDeckByOwnerIdAndDeckNameOrThrow(userChatId, deckName);
        return getCardByFrontAndDeckOrThrow(deck, cardFront);
    }

    private UserInfo getUserIdOrThrow(Long userChatId) {
        return userInfoRepository.findById(userChatId)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format(USER_NOT_FOUND.getMessage(), userChatId)
                ));
    }

    private Deck getDeckOwnerInfoAndDeckNameOrThrow(UserInfo owner, String deckName) {
        return deckRepository.findByNameIgnoreCaseAndOwner(deckName, owner)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format(DECK_NOT_FOUND_SIMPLE.getMessage(), deckName)
                ));
    }

    private Card getCardByFrontAndDeckOrThrow(Deck deck, String cardFront) {
        return cardRepository.findByDeckAndFrontIgnoreCase(deck, cardFront)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format(CARD_NOT_FOUND_SIMPLE.getMessage(), cardFront, deck.getName())
                ));
    }
}
