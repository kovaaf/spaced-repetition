package org.company.spacedrepetitionbot.handler.handlers.callback.strategy.edit_message.learn.answer;

import org.company.spacedrepetitionbot.constants.Quality;
import org.company.spacedrepetitionbot.handler.handlers.callback.Callback;
import org.company.spacedrepetitionbot.service.MessageStateService;
import org.company.spacedrepetitionbot.service.learning.LearningSessionService;
import org.company.spacedrepetitionbot.utils.KeyboardManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.company.spacedrepetitionbot.handler.handlers.callback.Callback.EASY;

@Component
public class AnswerEasyStrategy extends BaseAnswerStrategy {
    protected AnswerEasyStrategy(
            TelegramClient telegramClient,
            MessageStateService messageStateService,
            LearningSessionService learningSessionService,
            KeyboardManager keyboardManager) {
        super(telegramClient, messageStateService, learningSessionService, keyboardManager);
    }

    @Override
    protected Quality getQuality() {
        return Quality.EASY;
    }

    @Override
    public Callback getPrefix() {
        return EASY;
    }
}
