package org.company.spacedrepetitionbot.command.card;

import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.command.general.SpacedRepetitionCommand;
import org.company.spacedrepetitionbot.service.CardService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.company.spacedrepetitionbot.command.constant.Command.SHOW_ALL_CARDS;

@Slf4j
@Component
public class ShowAllCardsCommand extends SpacedRepetitionCommand {
    private final CardService cardService;

    public ShowAllCardsCommand(CardService cardService) {
        super(
                SHOW_ALL_CARDS.getAlias(),
                SHOW_ALL_CARDS.getDescription(),
                SHOW_ALL_CARDS.getExtendedDescription(),
                SHOW_ALL_CARDS.getValidArgumentCounts()
        );
        this.cardService = cardService;
    }

    @Override
    protected void performAction(TelegramClient telegramClient, String[] arguments) {
        String deckName = arguments[0];
        String result = cardService.getAllCardsInDeck(chatId, deckName);
        sendMessage(telegramClient, result);
    }
}
