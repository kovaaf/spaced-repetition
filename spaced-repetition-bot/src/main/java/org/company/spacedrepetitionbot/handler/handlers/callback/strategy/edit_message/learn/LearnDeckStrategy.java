package org.company.spacedrepetitionbot.handler.handlers.callback.strategy.edit_message.learn;

import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.handler.handlers.callback.Callback;
import org.company.spacedrepetitionbot.handler.handlers.callback.strategy.edit_message.BaseEditCallbackStrategy;
import org.company.spacedrepetitionbot.model.Card;
import org.company.spacedrepetitionbot.model.Deck;
import org.company.spacedrepetitionbot.service.DeckService;
import org.company.spacedrepetitionbot.service.MessageStateService;
import org.company.spacedrepetitionbot.service.learning.LearningService;
import org.company.spacedrepetitionbot.utils.KeyboardManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
public class LearnDeckStrategy extends BaseEditCallbackStrategy {
    private final LearningService learningService;
    private final KeyboardManager keyboardManager;
    private final DeckService deckService;

    public LearnDeckStrategy(
            TelegramClient telegramClient,
            MessageStateService messageStateService,
            LearningService learningService,
            KeyboardManager keyboardManager,
            DeckService deckService) {
        super(telegramClient, messageStateService);
        this.learningService = learningService;
        this.keyboardManager = keyboardManager;
        this.deckService = deckService;
    }

    @Override
    public void executeCallbackQuery(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        Long deckId = getLastDataElementFromCallback(callbackQuery.getData());
        if (isDeckEmpty(deckId)) {
            handleEmptyDeck(chatId, deckId, callbackQuery);
            return;
        }

        messageStateService.clearUserState(callbackQuery.getMessage().getChatId());
        super.executeCallbackQuery(callbackQuery);
    }

    // TODO fix format
    @Override
    protected String getMessageText(CallbackQuery callbackQuery) {
        Long deckId = getLastDataElementFromCallback(callbackQuery.getData());
        Card nextCard = learningService.getNextCard(deckId);

        if (nextCard == null) {
            return getDeckName(deckId) + " не содержит карточек для изучения";
        }

        return String.format("Карточка для изучения:\n\nВопрос:\n%s", nextCard.getFront());
    }

    @Override
    protected InlineKeyboardMarkup getKeyboard(CallbackQuery callbackQuery) {
        Long deckId = getLastDataElementFromCallback(callbackQuery.getData());
        Card nextCard = learningService.getNextCard(deckId);
        Long nextCardId = nextCard.getCardId();
        return keyboardManager.getLearnDeckKeyboard(nextCardId, deckId, nextCard.getStatus());
    }

    @Override
    public Callback getPrefix() {
        return Callback.LEARN_DECK;
    }

    private boolean isDeckEmpty(Long deckId) {
        return deckService.getDeckByIdWithCards(deckId).map(deck -> deck.getCards().isEmpty()).orElse(true);
    }

    private void handleEmptyDeck(Long chatId, Long deckId, CallbackQuery callbackQuery) {
        try {
            String deckName = getDeckName(deckId);
            String message = deckName + " не содержит карточек для изучения";

            telegramClient.execute(EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .text(message)
                    .replyMarkup(keyboardManager.getDeckMenuKeyboard(deckId))
                    .build());
        } catch (TelegramApiException e) {
            log.error("Ошибка при обработке пустой колоды: {}", e.getMessage());
        }
    }

    private String getDeckName(Long deckId) {
        return deckService.getDeckById(deckId).map(Deck::getName).orElse("Эта колода");
    }
}
