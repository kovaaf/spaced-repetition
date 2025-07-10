package org.company.spacedrepetitionbot.command.card;

import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.command.general.SpacedRepetitionCommand;
import org.company.spacedrepetitionbot.service.CardService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.company.spacedrepetitionbot.command.constant.Command.DELETE_CARD;

@Slf4j
@Component
public class DeleteCardCommand extends SpacedRepetitionCommand {
    private final CardService cardService;

    public DeleteCardCommand(CardService cardService) {
        super(
                DELETE_CARD.getAlias(),
                DELETE_CARD.getDescription(),
                DELETE_CARD.getExtendedDescription(),
                DELETE_CARD.getValidArgumentCounts()
        );
        this.cardService = cardService;
    }

    @Override
    protected void performAction(TelegramClient telegramClient, String[] arguments) {
        String deckName = arguments[0];
        String front = arguments[1];
        String result = cardService.deleteCard(chatId, deckName, front);
        sendMessage(telegramClient, result);
    }
}
