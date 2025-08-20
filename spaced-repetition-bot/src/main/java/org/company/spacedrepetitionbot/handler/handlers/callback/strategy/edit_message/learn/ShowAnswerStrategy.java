package org.company.spacedrepetitionbot.handler.handlers.callback.strategy.edit_message.learn;

import jakarta.persistence.EntityNotFoundException;
import org.company.spacedrepetitionbot.handler.handlers.callback.Callback;
import org.company.spacedrepetitionbot.handler.handlers.callback.strategy.edit_message.BaseEditCallbackStrategy;
import org.company.spacedrepetitionbot.model.Card;
import org.company.spacedrepetitionbot.service.CardService;
import org.company.spacedrepetitionbot.service.MessageStateService;
import org.company.spacedrepetitionbot.service.learning.LearningSessionService;
import org.company.spacedrepetitionbot.utils.KeyboardManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ShowAnswerStrategy extends BaseEditCallbackStrategy {
    private final LearningSessionService learningSessionService;
    private final KeyboardManager keyboardManager;
    private final CardService cardService;

    public ShowAnswerStrategy(
            TelegramClient telegramClient,
            MessageStateService messageStateService,
            LearningSessionService learningSessionService,
            KeyboardManager keyboardManager,
            CardService cardService) {
        super(telegramClient, messageStateService);
        this.learningSessionService = learningSessionService;
        this.keyboardManager = keyboardManager;
        this.cardService = cardService;
    }

    @Override
    public void executeCallbackQuery(CallbackQuery callbackQuery) {
        messageStateService.clearUserState(callbackQuery.getMessage().getChatId());
        super.executeCallbackQuery(callbackQuery);
    }

    @Override
    protected String getMessageText(CallbackQuery callbackQuery) {
        Long cardId = Long.valueOf(getCallbackDataByIndex(callbackQuery.getData(), 1));

        try {
            String answer = learningSessionService.getCardAnswerById(cardId);
            // Экранируем специальные символы Markdown

            if (answer.length() > 4096) {
                // TODO добавить обработчик
                String errorMessage = "Сообщение слишком длинное для отображения в Telegram\n\n";
                return errorMessage + answer.substring(0, 4096 - errorMessage.length());
            }

            return escapeMarkdownOutsideCodeBlocks(answer);
        } catch (EntityNotFoundException e) {
            return "Карточка не найдена";
        }
    }

    private String escapeMarkdownOutsideCodeBlocks(String text) {
        // Разделяем текст на части: code blocks и обычный текст
        Pattern pattern = Pattern.compile("(```.*?```)|([^`]+)");
        Matcher matcher = pattern.matcher(text);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                // Это code block - оставляем как есть
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(1)));
            } else {
                // Это обычный текст - экранируем специальные символы
                String escaped = matcher.group(2)
                        .replace("*", "\\*")
                        .replace("_", "\\_")
                        .replace("[", "\\[")
                        .replace("]", "\\]");
                matcher.appendReplacement(result, Matcher.quoteReplacement(escaped));
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }

    @Override
    protected InlineKeyboardMarkup getKeyboard(CallbackQuery callbackQuery) {
        Long cardId = Long.valueOf(getCallbackDataByIndex(callbackQuery.getData(), 1));
        Long deckId = Long.valueOf(getCallbackDataByIndex(callbackQuery.getData(), 2));
        Long sessionId = Long.valueOf(getCallbackDataByIndex(callbackQuery.getData(), 3));
        Card card = cardService.getCardById(cardId);
        return keyboardManager.getShowAnswerKeyboard(cardId, deckId, sessionId, card.getStatus());
    }

    @Override
    public Callback getPrefix() {
        return Callback.SHOW_ANSWER;
    }
}
