package org.company.spacedrepetitionbot.command.learning;

import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.command.general.SpacedRepetitionCommand;
import org.company.spacedrepetitionbot.constants.Quality;
import org.company.spacedrepetitionbot.model.Card;
import org.company.spacedrepetitionbot.service.LearningService;
import org.company.spacedrepetitionbot.service.SM2Algorithm;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.company.spacedrepetitionbot.command.constant.Command.*;
import static org.company.spacedrepetitionbot.constants.MessageConstants.CARD_BACK;

/**
 * Команда для отображения ответа на текущую карточку.
 * Показывает обратную сторону карточки с правильным ответом.
 */
@Slf4j
@Component
public class ShowAnswerCommand extends SpacedRepetitionCommand {
    private final LearningService learningService;
    private final SM2Algorithm sm2Algorithm;

    public ShowAnswerCommand(LearningService learningService, SM2Algorithm sm2Algorithm) {
        super(
                SHOW_ANSWER.getAlias(),
                SHOW_ANSWER.getDescription(),
                SHOW_ANSWER.getExtendedDescription(),
                SHOW_ANSWER.getValidArgumentCounts());
        this.learningService = learningService;
        this.sm2Algorithm = sm2Algorithm;
    }

    /**
     * Возвращает ответ для указанной карточки.
     *
     * @param arguments массив аргументов команды, где:
     *                  arguments[0] - название колоды
     *                  arguments[1] - лицевая сторона карточки
     */
    @Override
    protected void performAction(TelegramClient telegramClient, String[] arguments) {
        String deckName = arguments[0];
        String cardFront = arguments[1];
        Card card = learningService.getCardAnswerByFront(chatId, deckName, cardFront);
        String cardBack = card.getBack();
        String front = card.getFront();

        String result = String.format(
                CARD_BACK.getMessage(),
                cardBack,
                AGAIN.getAlias(),
                deckName,
                front,
                sm2Algorithm.getNextReviewTimeAsString(card, Quality.AGAIN),
                HARD.getAlias(),
                deckName,
                front,
                sm2Algorithm.getNextReviewTimeAsString(card, Quality.HARD),
                GOOD.getAlias(),
                deckName,
                front,
                sm2Algorithm.getNextReviewTimeAsString(card, Quality.GOOD),
                EASY.getAlias(),
                deckName,
                front,
                sm2Algorithm.getNextReviewTimeAsString(card, Quality.EASY),
                PREV_CARD.getAlias());

        sendMessage(telegramClient, result);
    }
}
