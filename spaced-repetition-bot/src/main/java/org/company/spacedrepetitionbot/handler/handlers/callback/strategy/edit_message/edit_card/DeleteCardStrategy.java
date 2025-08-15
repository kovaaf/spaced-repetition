package org.company.spacedrepetitionbot.handler.handlers.callback.strategy.edit_message.edit_card;

import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.handler.handlers.callback.Callback;
import org.company.spacedrepetitionbot.handler.handlers.callback.strategy.edit_message.BaseEditCallbackStrategy;
import org.company.spacedrepetitionbot.service.CardService;
import org.company.spacedrepetitionbot.service.MessageStateService;
import org.company.spacedrepetitionbot.utils.KeyboardManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
public class DeleteCardStrategy extends BaseEditCallbackStrategy {
    private final CardService cardService;
    private final KeyboardManager keyboardManager;

    public DeleteCardStrategy(
            TelegramClient telegramClient,
            MessageStateService messageStateService,
            CardService cardService,
            KeyboardManager keyboardManager) {
        super(telegramClient, messageStateService);
        this.cardService = cardService;
        this.keyboardManager = keyboardManager;
    }

    @Override
    protected String getMessageText(CallbackQuery callbackQuery) {
        Long cardId = Long.valueOf(getCallbackDataByIndex(callbackQuery.getData(), 1));
        boolean deleted = cardService.deleteCard(cardId);
        if (deleted) {
            return "✅ Карточка успешно удалена";
        } else {
            return "❌ Не удалось удалить карточку";
        }
    }

    @Override
    protected InlineKeyboardMarkup getKeyboard(CallbackQuery callbackQuery) {
        Long deckId = Long.valueOf(getCallbackDataByIndex(callbackQuery.getData(), 2));
        return keyboardManager.getDeckMenuKeyboard(deckId);
    }

    @Override
    public void executeCallbackQuery(CallbackQuery callbackQuery) {
        try {
            super.executeCallbackQuery(callbackQuery);
        } catch (Exception e) {
            log.error("Ошибка удаления карты: {}", e.getMessage(), e);
            Long chatId = callbackQuery.getMessage()
                    .getChatId();
            sendErrorMessage(chatId, "Ошибка при удалении карты");
        }
    }

    @Override
    public Callback getPrefix() {
        return Callback.DELETE_CARD;
    }
}