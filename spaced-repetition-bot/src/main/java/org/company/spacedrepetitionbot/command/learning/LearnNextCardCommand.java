package org.company.spacedrepetitionbot.command.learning;

import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.command.general.SpacedRepetitionCommand;
import org.company.spacedrepetitionbot.model.Card;
import org.company.spacedrepetitionbot.service.LearningService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.company.spacedrepetitionbot.command.constant.Command.*;
import static org.company.spacedrepetitionbot.constants.MessageConstants.CARD_WITH_MINIMUM_INTERVAL;

/**
 * Команда для перехода к следующей карточке в процессе изучения.
 * Возвращает следующую карточку с минимальным интервалом повторения.
 */
@Slf4j
@Component
public class LearnNextCardCommand extends SpacedRepetitionCommand {
    private final LearningService learningService;

    public LearnNextCardCommand(LearningService learningService) {
        super(
                LEARN_NEXT.getAlias(),
                LEARN_NEXT.getDescription(),
                LEARN_NEXT.getExtendedDescription(),
                LEARN_NEXT.getValidArgumentCounts());
        this.learningService = learningService;
    }

    /**
     * Возвращает следующую карточку для изучения из указанной колоды.
     *
     * @param arguments массив аргументов команды, где:
     *                  arguments[0] - название колоды
     */
    @Override
    protected void performAction(TelegramClient telegramClient, String[] arguments) {
        String deckName = arguments[0];
        Card card = learningService.getCardFromDeckWithMinimumInterval(chatId, deckName);
        String front = card.getFront();

        String result = String.format(
                CARD_WITH_MINIMUM_INTERVAL.getMessage(),
                front,
                SHOW_ANSWER.getAlias(),
                deckName,
                front,
                PREV_CARD.getAlias());

        sendMessage(telegramClient, result);
    }
}
