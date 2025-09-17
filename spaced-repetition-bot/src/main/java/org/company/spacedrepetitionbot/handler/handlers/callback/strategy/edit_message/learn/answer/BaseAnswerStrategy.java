package org.company.spacedrepetitionbot.handler.handlers.callback.strategy.edit_message.learn.answer;

import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.constants.Quality;
import org.company.spacedrepetitionbot.constants.Status;
import org.company.spacedrepetitionbot.exception.SessionCompletedException;
import org.company.spacedrepetitionbot.handler.handlers.callback.Callback;
import org.company.spacedrepetitionbot.handler.handlers.callback.strategy.edit_message.BaseEditCallbackStrategy;
import org.company.spacedrepetitionbot.model.Card;
import org.company.spacedrepetitionbot.model.LearningSession;
import org.company.spacedrepetitionbot.service.MessageStateService;
import org.company.spacedrepetitionbot.service.learning.LearningSessionService;
import org.company.spacedrepetitionbot.utils.KeyboardManager;
import org.company.spacedrepetitionbot.utils.MarkdownEscaper;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
public abstract class BaseAnswerStrategy extends BaseEditCallbackStrategy {
    private final LearningSessionService learningSessionService;
    private final KeyboardManager keyboardManager;

    protected BaseAnswerStrategy(
            TelegramClient telegramClient,
            MessageStateService messageStateService,
            MarkdownEscaper markdownEscaper,
            LearningSessionService learningSessionService,
            KeyboardManager keyboardManager) {
        super(telegramClient, messageStateService, markdownEscaper);
        this.learningSessionService = learningSessionService;
        this.keyboardManager = keyboardManager;
    }

    protected abstract Quality getQuality();

    public abstract Callback getPrefix();

    @Override
    public void executeCallbackQuery(CallbackQuery callbackQuery) {
        try {
            Long cardId = Long.valueOf(getCallbackDataByIndex(callbackQuery.getData(), 1));
            Long sessionId = Long.valueOf(getCallbackDataByIndex(callbackQuery.getData(), 3));

            Card updatedCard = learningSessionService.updateCardWithAnswer(cardId, getQuality());
            if (updatedCard.getStatus() != Status.NEW &&
                    updatedCard.getStatus() != Status.LEARNING &&
                    updatedCard.getStatus() != Status.RELEARNING) {
                learningSessionService.removeCardFromSession(sessionId, cardId);
            }

            // Проверяем, завершена ли сессия после удаления карты
            int remaining = learningSessionService.getRemainingCardsCount(sessionId);
            if (remaining == 0) {
                // Завершаем сессию и возвращаемся в меню колоды
                handleSessionCompleted(callbackQuery);
                return;
            }

            super.executeCallbackQuery(callbackQuery);
        } catch (Exception  e) {
            log.error("Ошибка обработки ответа: {}", e.getMessage());
            sendErrorMessage(callbackQuery.getMessage().getChatId(), "Ошибка обработки ответа");
        }
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

        // Проверяем, завершена ли сессия
        int remaining = learningSessionService.getRemainingCardsCount(sessionId);
        if (remaining == 0) {
            // Создаем новую сессию для получения актуальных счетчиков
            LearningSession newSession = learningSessionService.getOrCreateSession(deckId);
            int newCards = learningSessionService.countNewCardsInSession(newSession.getSessionId());
            int reviewCards = learningSessionService.countReviewCardsInSession(newSession.getSessionId());
            return keyboardManager.getDeckMenuKeyboard(deckId, newCards, reviewCards);
        }

        // Если сессия не завершена, получаем следующую карту
        try {
            Card nextCard = learningSessionService.getNextCardInSession(sessionId);
            Long nextCardId = nextCard.getCardId();
            Status status = nextCard.getStatus();
            return keyboardManager.getLearnDeckKeyboard(nextCardId, deckId, sessionId, status);
        } catch (SessionCompletedException e) {
            // На случай, если между проверкой и получением карты сессия стала пустой
            LearningSession newSession = learningSessionService.getOrCreateSession(deckId);
            int newCards = learningSessionService.countNewCardsInSession(newSession.getSessionId());
            int reviewCards = learningSessionService.countReviewCardsInSession(newSession.getSessionId());
            return keyboardManager.getDeckMenuKeyboard(deckId, newCards, reviewCards);
        }
    }

    private void handleSessionCompleted(CallbackQuery callbackQuery) {
        Long deckId = Long.valueOf(getCallbackDataByIndex(callbackQuery.getData(), 2));
        Long chatId = callbackQuery.getMessage().getChatId();

        try {
            // Получаем актуальные счетчики для меню колоды
            LearningSession newSession = learningSessionService.getOrCreateSession(deckId);
            int newCards = learningSessionService.countNewCardsInSession(newSession.getSessionId());
            int reviewCards = learningSessionService.countReviewCardsInSession(newSession.getSessionId());

            telegramClient.execute(EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .text("🎉 Сессия завершена! Все карточки изучены.")
                    .replyMarkup(keyboardManager.getDeckMenuKeyboard(deckId, newCards, reviewCards))
                    .build());
        } catch (Exception e) {
            log.error("Ошибка обработки завершения сессии: {}", e.getMessage());
            sendErrorMessage(chatId, "Ошибка завершения сессии");
        }
    }
}
