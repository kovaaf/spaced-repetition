package org.company.spacedrepetitionbot.handler.handlers.callback.strategy.edit_message.learn.answer;

import org.company.spacedrepetitionbot.constants.Quality;
import org.company.spacedrepetitionbot.handler.handlers.callback.Callback;
import org.company.spacedrepetitionbot.service.DeckService;
import org.company.spacedrepetitionbot.service.LearningService;
import org.company.spacedrepetitionbot.service.MessageStateService;
import org.company.spacedrepetitionbot.utils.KeyboardManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.company.spacedrepetitionbot.handler.handlers.callback.Callback.HARD;

@Component
public class AnswerHardStrategy extends BaseAnswerStrategy {
    protected AnswerHardStrategy(TelegramClient telegramClient, MessageStateService messageStateService, LearningService learningService, DeckService deckService, KeyboardManager keyboardManager) {
        super(telegramClient, messageStateService, learningService, deckService, keyboardManager);
    }

    @Override
    protected Quality getQuality() {
        return Quality.HARD;
    }

    @Override
    public Callback getPrefix() {
        return HARD;
    }
}
