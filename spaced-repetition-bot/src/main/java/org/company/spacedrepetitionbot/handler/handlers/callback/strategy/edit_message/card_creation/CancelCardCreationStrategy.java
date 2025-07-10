package org.company.spacedrepetitionbot.handler.handlers.callback.strategy.edit_message.card_creation;

import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.handler.handlers.callback.Callback;
import org.company.spacedrepetitionbot.handler.handlers.callback.strategy.edit_message.BaseEditCallbackStrategy;
import org.company.spacedrepetitionbot.service.CardDraftService;
import org.company.spacedrepetitionbot.service.MessageStateService;
import org.company.spacedrepetitionbot.utils.KeyboardManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
public class CancelCardCreationStrategy extends BaseEditCallbackStrategy {
    private final CardDraftService cardDraftService;
    private final KeyboardManager keyboardManager;

    public CancelCardCreationStrategy(TelegramClient telegramClient,
                                      MessageStateService messageStateService,
                                      CardDraftService cardDraftService,
                                      KeyboardManager keyboardManager) {
        super(telegramClient, messageStateService);
        this.cardDraftService = cardDraftService;
        this.keyboardManager = keyboardManager;
    }

    @Override
    protected String getMessageText(CallbackQuery callbackQuery) {
        return "❌ Создание карты отменено";
    }

    @Override
    protected InlineKeyboardMarkup getKeyboard(CallbackQuery callbackQuery) {
        Long deckId = getLastDataElementFromCallback(callbackQuery.getData());
        return keyboardManager.getDeckMenuKeyboard(deckId);
    }

    @Override
    public void executeCallbackQuery(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();

        try {
            cardDraftService.clearDraft(chatId);
            messageStateService.clearUserState(chatId);
            super.executeCallbackQuery(callbackQuery);
        } catch (Exception e) {
            log.error("Ошибка отмены создания карты: {}", e.getMessage());
        }
    }

    @Override
    public Callback getPrefix() {
        return Callback.CANCEL_CARD_CREATION;
    }
}