package org.company.spacedrepetitionbot.command.deck;

import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.command.general.SpacedRepetitionCommand;
import org.company.spacedrepetitionbot.service.DeckService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.company.spacedrepetitionbot.command.constant.Command.SHOW_ALL_DECKS;

@Slf4j
@Component
public class ShowAllDecksCommand extends SpacedRepetitionCommand {
    private final DeckService deckService;

    public ShowAllDecksCommand(DeckService deckService) {
        super(
                SHOW_ALL_DECKS.getAlias(),
                SHOW_ALL_DECKS.getDescription(),
                SHOW_ALL_DECKS.getExtendedDescription(),
                SHOW_ALL_DECKS.getValidArgumentCounts()
        );
        this.deckService = deckService;
    }

    @Override
    protected void performAction(TelegramClient telegramClient, String[] arguments) {
        String result = deckService.formatUserDecks(chatId);
        sendMessage(telegramClient, result);
    }
}
