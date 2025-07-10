package org.company.spacedrepetitionbot.command.deck;

import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.command.general.SpacedRepetitionCommand;
import org.company.spacedrepetitionbot.service.DeckService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.company.spacedrepetitionbot.command.constant.Command.EDIT_DECK;

@Slf4j
@Component
public class EditDeckCommand extends SpacedRepetitionCommand {
    private final DeckService deckService;

    public EditDeckCommand(DeckService deckService) {
        super(
                EDIT_DECK.getAlias(),
                EDIT_DECK.getDescription(),
                EDIT_DECK.getExtendedDescription(),
                EDIT_DECK.getValidArgumentCounts()
        );
        this.deckService = deckService;
    }

    @Override
    protected void performAction(TelegramClient telegramClient, String[] arguments) {
        String currentDeckName = arguments[0];
        String newDeckName = arguments[1];
        String result = deckService.renameDeck(chatId, currentDeckName, newDeckName);
        sendMessage(telegramClient, result);
    }
}
