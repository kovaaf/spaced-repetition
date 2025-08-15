package org.company.spacedrepetitionbot.command.learning;

import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.command.general.SpacedRepetitionCommand;
import org.company.spacedrepetitionbot.constants.Quality;
import org.company.spacedrepetitionbot.service.LearningService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.company.spacedrepetitionbot.command.constant.Command.AGAIN;

/**
 * Команда для отметки карточки как неправильно угаданной и повторного её изучения.
 * Устанавливает качество ответа как "снова" (AGAIN) для указанной карточки в колоде.
 */
@Slf4j
@Component
public class AgainCommand extends SpacedRepetitionCommand {
    private final LearningService learningService;

    public AgainCommand(LearningService learningService) {
        super(AGAIN.getAlias(), AGAIN.getDescription(), AGAIN.getExtendedDescription(), AGAIN.getValidArgumentCounts());
        this.learningService = learningService;
    }

    /**
     * Выполняет действие команды: отмечает карточку как изученную с качеством "снова".
     *
     * @param arguments массив аргументов команды, где:
     *                  arguments[0] - название колоды
     *                  arguments[1] - лицевая сторона карточки
     */
    @Override
    protected void performAction(TelegramClient telegramClient, String[] arguments) {
        String deckName = arguments[0];
        String cardFront = arguments[1];
        String result = learningService.updateCardWithAnswer(chatId, deckName, cardFront, Quality.AGAIN);
        sendMessage(telegramClient, result);
    }
}
