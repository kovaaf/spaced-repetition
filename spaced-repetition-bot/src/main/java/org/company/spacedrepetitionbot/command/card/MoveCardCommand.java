package org.company.spacedrepetitionbot.command.card;

import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.command.general.SpacedRepetitionCommand;
import org.company.spacedrepetitionbot.service.CardService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.company.spacedrepetitionbot.command.constant.Command.MOVE_CARD;

@Slf4j
@Component
public class MoveCardCommand extends SpacedRepetitionCommand {
    private final CardService cardService;

    public MoveCardCommand(CardService cardService) {
        super(
                MOVE_CARD.getAlias(),
                MOVE_CARD.getDescription(),
                MOVE_CARD.getExtendedDescription(),
                MOVE_CARD.getValidArgumentCounts());
        this.cardService = cardService;
    }

    @Override
    protected void performAction(TelegramClient telegramClient, String[] arguments) {
        String sourceDeckName = arguments[0];
        String cardFront = arguments[1];
        String targetDeckName = arguments[2];

        String result = cardService.moveCard(chatId, sourceDeckName, cardFront, targetDeckName);
        sendMessage(telegramClient, result);
    }
}
