package org.company.spacedrepetitionbot.command.card;

import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.command.general.SpacedRepetitionCommand;
import org.company.spacedrepetitionbot.service.CardService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.company.spacedrepetitionbot.command.constant.Command.EDIT_CARD_BACK;

@Slf4j
@Component
public class EditCardBackCommand extends SpacedRepetitionCommand {
    private final CardService cardService;

    public EditCardBackCommand(CardService cardService) {
        super(
                EDIT_CARD_BACK.getAlias(),
                EDIT_CARD_BACK.getDescription(),
                EDIT_CARD_BACK.getExtendedDescription(),
                EDIT_CARD_BACK.getValidArgumentCounts()
        );
        this.cardService = cardService;
    }

    @Override
    protected void performAction(TelegramClient telegramClient, String[] arguments) {
        String deckName = arguments[0];
        String front = arguments[1];
        String newBack = arguments[2];
        String result = cardService.updateCardBack(chatId, deckName, front, newBack);
        sendMessage(telegramClient, result);
    }
}
