package org.company.spacedrepetitionbot.command.learning;

import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.command.general.SpacedRepetitionCommand;
import org.company.spacedrepetitionbot.constants.Quality;
import org.company.spacedrepetitionbot.service.LearningService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.company.spacedrepetitionbot.command.constant.Command.GOOD;

/**
 * Устанавливает качество ответа как "Хорошо" (GOOD) для указанной карточки в колоде.
 */
@Slf4j
@Component
public class GoodCommand extends SpacedRepetitionCommand {
    private final LearningService learningService;

    public GoodCommand(LearningService learningService) {
        super(GOOD.getAlias(), GOOD.getDescription(), GOOD.getExtendedDescription(), GOOD.getValidArgumentCounts());
        this.learningService = learningService;
    }

    /**
     * Выполняет действие команды: отмечает карточку как изученную с качеством "Хорошо".
     *
     * @param arguments массив аргументов команды, где:
     *                  arguments[0] - название колоды
     *                  arguments[1] - лицевая сторона карточки
     */
    @Override
    protected void performAction(TelegramClient telegramClient, String[] arguments) {
        String deckName = arguments[0];
        String cardFront = arguments[1];
        String result = learningService.updateCardWithAnswer(chatId, deckName, cardFront, Quality.GOOD);
        sendMessage(telegramClient, result);
    }
}
