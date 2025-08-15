package org.company.spacedrepetitionbot.handler.handlers.callback.strategy.edit_message.learn;

import jakarta.persistence.EntityNotFoundException;
import org.company.spacedrepetitionbot.handler.handlers.callback.Callback;
import org.company.spacedrepetitionbot.handler.handlers.callback.strategy.edit_message.BaseEditCallbackStrategy;
import org.company.spacedrepetitionbot.model.Card;
import org.company.spacedrepetitionbot.service.CardService;
import org.company.spacedrepetitionbot.service.LearningService;
import org.company.spacedrepetitionbot.service.MessageStateService;
import org.company.spacedrepetitionbot.utils.KeyboardManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class ShowAnswerStrategy extends BaseEditCallbackStrategy {
    private final LearningService learningService;
    private final KeyboardManager keyboardManager;
    private final CardService cardService;

    public ShowAnswerStrategy(
            TelegramClient telegramClient,
            MessageStateService messageStateService,
            LearningService learningService,
            KeyboardManager keyboardManager,
            CardService cardService) {
        super(telegramClient, messageStateService);
        this.learningService = learningService;
        this.keyboardManager = keyboardManager;
        this.cardService = cardService;
    }

    @Override
    public void executeCallbackQuery(CallbackQuery callbackQuery) {
        messageStateService.clearUserState(callbackQuery.getMessage()
                .getChatId());
        super.executeCallbackQuery(callbackQuery);
    }

    @Override
    protected String getMessageText(CallbackQuery callbackQuery) {
        Long cardId = Long.valueOf(getCallbackDataByIndex(callbackQuery.getData(), 1));

        try {
            return "Ответ:\n" + learningService.getCardAnswerById(cardId);
        } catch (EntityNotFoundException e) {
            return "Карточка не найдена";
        }
    }

    @Override
    protected InlineKeyboardMarkup getKeyboard(CallbackQuery callbackQuery) {
        Long cardId = Long.valueOf(getCallbackDataByIndex(callbackQuery.getData(), 1));
        Long deckId = Long.valueOf(getCallbackDataByIndex(callbackQuery.getData(), 2));
        Card card = cardService.getCardById(cardId);
        return keyboardManager.getShowAnswerKeyboard(cardId, deckId, card.getStatus());
    }

    @Override
    public Callback getPrefix() {
        return Callback.SHOW_ANSWER;
    }
}
