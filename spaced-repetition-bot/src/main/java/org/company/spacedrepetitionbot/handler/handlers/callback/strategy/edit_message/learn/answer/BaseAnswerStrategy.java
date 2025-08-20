package org.company.spacedrepetitionbot.handler.handlers.callback.strategy.edit_message.learn.answer;

import org.company.spacedrepetitionbot.constants.Quality;
import org.company.spacedrepetitionbot.constants.Status;
import org.company.spacedrepetitionbot.exception.SessionCompletedException;
import org.company.spacedrepetitionbot.handler.handlers.callback.Callback;
import org.company.spacedrepetitionbot.handler.handlers.callback.strategy.edit_message.BaseEditCallbackStrategy;
import org.company.spacedrepetitionbot.model.Card;
import org.company.spacedrepetitionbot.service.MessageStateService;
import org.company.spacedrepetitionbot.service.learning.LearningSessionService;
import org.company.spacedrepetitionbot.utils.KeyboardManager;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public abstract class BaseAnswerStrategy extends BaseEditCallbackStrategy {
    private final LearningSessionService learningSessionService;
    private final KeyboardManager keyboardManager;

    protected BaseAnswerStrategy(
            TelegramClient telegramClient,
            MessageStateService messageStateService,
            LearningSessionService learningSessionService,
            KeyboardManager keyboardManager) {
        super(telegramClient, messageStateService);
        this.learningSessionService = learningSessionService;
        this.keyboardManager = keyboardManager;
    }

    protected abstract Quality getQuality();

    public abstract Callback getPrefix();

    @Override
    public void executeCallbackQuery(CallbackQuery callbackQuery) {
        Long cardId = Long.valueOf(getCallbackDataByIndex(callbackQuery.getData(), 1));
        Long sessionId = Long.valueOf(getCallbackDataByIndex(callbackQuery.getData(), 3));

        Card updatedCard = learningSessionService.updateCardWithAnswer(cardId, getQuality());
        if (updatedCard.getStatus() != Status.NEW &&
                updatedCard.getStatus() != Status.LEARNING &&
                updatedCard.getStatus() != Status.RELEARNING) {
            learningSessionService.removeCardFromSession(sessionId, cardId);
        }

        super.executeCallbackQuery(callbackQuery);
    }

    @Override
    protected String getMessageText(CallbackQuery callbackQuery) {
        Long sessionId = Long.valueOf(getCallbackDataByIndex(callbackQuery.getData(), 3));

        try {
            int remaining = learningSessionService.getRemainingCardsCount(sessionId);
            if (remaining == 0) {
                return "Сессия завершена! Все карточки изучены.";
            }
            Card nextCard = learningSessionService.getNextCardInSession(sessionId);
            return String.format("Осталось карт: %d\n\nВопрос:\n%s", remaining, nextCard.getFront());
        } catch (SessionCompletedException e) {
            return e.getMessage();
        }
    }

    @Override
    protected InlineKeyboardMarkup getKeyboard(CallbackQuery callbackQuery) {
        Long deckId = Long.valueOf(getCallbackDataByIndex(callbackQuery.getData(), 2));
        Long sessionId = Long.valueOf(getCallbackDataByIndex(callbackQuery.getData(), 3));
        Card nextCard = learningSessionService.getNextCard(deckId);
        Long nextCardId = nextCard.getCardId();
        Status status = nextCard.getStatus();
        return keyboardManager.getLearnDeckKeyboard(nextCardId, deckId, sessionId, status);
    }
}
