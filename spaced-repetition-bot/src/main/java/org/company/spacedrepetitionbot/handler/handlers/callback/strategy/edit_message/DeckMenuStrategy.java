package org.company.spacedrepetitionbot.handler.handlers.callback.strategy.edit_message;

import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.handler.handlers.callback.Callback;
import org.company.spacedrepetitionbot.model.Deck;
import org.company.spacedrepetitionbot.service.DeckService;
import org.company.spacedrepetitionbot.service.MessageStateService;
import org.company.spacedrepetitionbot.utils.KeyboardManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
public class DeckMenuStrategy extends BaseEditCallbackStrategy {
    private final DeckService deckService;
    private final KeyboardManager keyboardManager;

    public DeckMenuStrategy(
            TelegramClient telegramClient,
            MessageStateService messageStateService,
            DeckService deckService,
            KeyboardManager keyboardManager) {
        super(telegramClient, messageStateService);
        this.deckService = deckService;
        this.keyboardManager = keyboardManager;
    }

    @Override
    protected String getMessageText(CallbackQuery callbackQuery) {
        long deckId = getLastDataElementFromCallback(callbackQuery.getData());
        String deckName = deckService.getDeckById(deckId)
                .map(Deck::getName)
                .orElse("Неизвестная колода");

        return String.format("Меню колоды \"%s\":", deckName);
    }

    @Override
    protected InlineKeyboardMarkup getKeyboard(CallbackQuery callbackQuery) {
        long deckId = getLastDataElementFromCallback(callbackQuery.getData());
        return keyboardManager.getDeckMenuKeyboard(deckId);
    }

    @Override
    public Callback getPrefix() {
        return Callback.DECK_MENU;
    }
}
