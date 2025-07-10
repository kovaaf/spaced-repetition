package org.company.spacedrepetitionbot.command.card;

import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.command.general.SpacedRepetitionCommand;
import org.company.spacedrepetitionbot.service.CardService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.company.spacedrepetitionbot.command.constant.Command.EDIT_CARD_FRONT;

@Slf4j
@Component
public class EditCardFrontCommand extends SpacedRepetitionCommand {
    private final CardService cardService;

    public EditCardFrontCommand(CardService cardService) {
        super(
                EDIT_CARD_FRONT.getAlias(),
                EDIT_CARD_FRONT.getDescription(),
                EDIT_CARD_FRONT.getExtendedDescription(),
                EDIT_CARD_FRONT.getValidArgumentCounts()
        );
        this.cardService = cardService;
    }

    @Override
    protected void performAction(TelegramClient telegramClient, String[] arguments) {
        String deckName = arguments[0];
        String currentFront = arguments[1];
        String newFront = arguments[2];
        String result = cardService.updateCardFront(chatId, deckName, currentFront, newFront);
        sendMessage(telegramClient, result);
    }
}
