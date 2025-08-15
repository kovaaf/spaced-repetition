package org.company.spacedrepetitionbot.command.card;

import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.command.general.SpacedRepetitionCommand;
import org.company.spacedrepetitionbot.service.CardService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.company.spacedrepetitionbot.command.constant.Command.SHOW_CARD;

@Slf4j
@Component
public class ShowCardCommand extends SpacedRepetitionCommand {
    private final CardService cardService;

    public ShowCardCommand(CardService cardService) {
        super(
                SHOW_CARD.getAlias(),
                SHOW_CARD.getDescription(),
                SHOW_CARD.getExtendedDescription(),
                SHOW_CARD.getValidArgumentCounts());
        this.cardService = cardService;
    }

    @Override
    protected void performAction(TelegramClient telegramClient, String[] arguments) {
        String front = arguments[0];
        String deckName = arguments[1];
        String result = cardService.getCardDetails(chatId, front, deckName);
        sendMessage(telegramClient, result);
    }
}
