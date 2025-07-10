package org.company.spacedrepetitionbot.handler.handlers.callback.strategy;

import org.company.spacedrepetitionbot.constants.Status;
import org.company.spacedrepetitionbot.handler.handlers.callback.Callback;
import org.company.spacedrepetitionbot.handler.handlers.callback.strategy.edit_message.BaseEditCallbackStrategy;
import org.company.spacedrepetitionbot.model.Card;
import org.company.spacedrepetitionbot.service.CardService;
import org.company.spacedrepetitionbot.service.MessageStateService;
import org.company.spacedrepetitionbot.utils.KeyboardManager;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;

//@Component
public class SuspendCardStrategy extends BaseEditCallbackStrategy {
    private final CardService cardService;
    private final KeyboardManager keyboardManager;

    public SuspendCardStrategy(TelegramClient telegramClient,
                               MessageStateService messageStateService,
                               CardService cardService,
                               KeyboardManager keyboardManager) {
        super(telegramClient, messageStateService);
        this.keyboardManager = keyboardManager;
        this.cardService = cardService;
    }

    @Override
    protected String getMessageText(CallbackQuery callbackQuery) {
        Long cardId = Long.valueOf(getCallbackDataByIndex(callbackQuery.getData(), 1));
        Card card = cardService.getCardById(cardId);

        return "Статус карточки: " +
                (card.getStatus() == Status.SUSPENDED ? "Возобновлена" : "Приостановлена");
    }

    @Override
    protected InlineKeyboardMarkup getKeyboard(CallbackQuery callbackQuery) {
        Long cardId = Long.valueOf(getCallbackDataByIndex(callbackQuery.getData(), 1));
        Long deckId = Long.valueOf(getCallbackDataByIndex(callbackQuery.getData(), 2));
        Card card = cardService.getCardById(cardId);

        // Обновляем статус
        card.setStatus(
                card.getStatus() == Status.SUSPENDED ?
                        Status.REVIEW_YOUNG :
                        Status.SUSPENDED
        );
        cardService.save(card);

        // Возвращаемся в меню колоды
        return keyboardManager.getDeckMenuKeyboard(deckId);
    }

    @Override
    public Callback getPrefix() {
        return Callback.SUSPEND;
    }
}