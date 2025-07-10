package org.company.spacedrepetitionbot.command.deck;

import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.command.general.SpacedRepetitionCommand;
import org.company.spacedrepetitionbot.service.DeckService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.company.spacedrepetitionbot.command.constant.Command.ADD_DECK;

@Slf4j
@Component
public class AddDeckCommand extends SpacedRepetitionCommand {
    private final DeckService deckService;

    public AddDeckCommand(DeckService deckService) {
        super(
                ADD_DECK.getAlias(),
                ADD_DECK.getDescription(),
                ADD_DECK.getExtendedDescription(),
                ADD_DECK.getValidArgumentCounts()
        );
        this.deckService = deckService;
    }

    @Override
    protected void performAction(TelegramClient telegramClient, String[] arguments) {
        String deckName = arguments[0];
        String result = deckService.addDeck(chatId, deckName);
        sendMessage(telegramClient, result);
    }
}
