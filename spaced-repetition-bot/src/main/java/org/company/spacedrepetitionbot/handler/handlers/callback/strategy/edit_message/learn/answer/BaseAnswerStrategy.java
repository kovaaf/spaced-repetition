package org.company.spacedrepetitionbot.handler.handlers.callback.strategy.edit_message.learn.answer;

import org.company.spacedrepetitionbot.constants.Quality;
import org.company.spacedrepetitionbot.handler.handlers.callback.Callback;
import org.company.spacedrepetitionbot.handler.handlers.callback.strategy.edit_message.BaseEditCallbackStrategy;
import org.company.spacedrepetitionbot.model.Card;
import org.company.spacedrepetitionbot.model.Deck;
import org.company.spacedrepetitionbot.service.DeckService;
import org.company.spacedrepetitionbot.service.LearningService;
import org.company.spacedrepetitionbot.service.MessageStateService;
import org.company.spacedrepetitionbot.utils.KeyboardManager;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public abstract class BaseAnswerStrategy extends BaseEditCallbackStrategy {
    private final LearningService learningService;
    private final DeckService deckService;
    private final KeyboardManager keyboardManager;

    protected BaseAnswerStrategy(
            TelegramClient telegramClient,
            MessageStateService messageStateService,
            LearningService learningService,
            DeckService deckService,
            KeyboardManager keyboardManager) {
        super(telegramClient, messageStateService);
        this.learningService = learningService;
        this.deckService = deckService;
        this.keyboardManager = keyboardManager;
    }

    protected abstract Quality getQuality();

    public abstract Callback getPrefix();

    @Override
    public void executeCallbackQuery(CallbackQuery callbackQuery) {
        Long cardId = Long.valueOf(getCallbackDataByIndex(callbackQuery.getData(), 1));
        learningService.updateCardWithAnswer(cardId, getQuality());
        super.executeCallbackQuery(callbackQuery);
    }

    @Override
    protected String getMessageText(CallbackQuery callbackQuery) {
        Long deckId = Long.valueOf(getCallbackDataByIndex(callbackQuery.getData(), 2));
        Card nextCard = learningService.getNextCard(deckId);

        if (nextCard == null) {
            return getDeckName(deckId) + " не содержит карточек для изучения";
        }

        return String.format("Карточка для изучения:\n\nВопрос:\n%s", nextCard.getFront());
    }

    @Override
    protected InlineKeyboardMarkup getKeyboard(CallbackQuery callbackQuery) {
        Long deckId = Long.valueOf(getCallbackDataByIndex(callbackQuery.getData(), 2));
        Card nextCard = learningService.getNextCard(deckId);
        Long nextCardId = nextCard.getCardId();
        return keyboardManager.getLearnDeckKeyboard(nextCardId, deckId, nextCard.getStatus());
    }

    protected String getDeckName(Long deckId) {
        return deckService.getDeckById(deckId)
                .map(Deck::getName)
                .orElse("Эта колода");
    }
}
