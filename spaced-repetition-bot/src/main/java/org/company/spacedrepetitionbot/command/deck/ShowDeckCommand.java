package org.company.spacedrepetitionbot.command.deck;

import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.command.general.SpacedRepetitionCommand;
import org.company.spacedrepetitionbot.service.DeckService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.company.spacedrepetitionbot.command.constant.Command.SHOW_DECK;

@Slf4j
@Component
public class ShowDeckCommand extends SpacedRepetitionCommand {
    private final DeckService deckService;

    public ShowDeckCommand(DeckService deckService) {
        super(
                SHOW_DECK.getAlias(),
                SHOW_DECK.getDescription(),
                SHOW_DECK.getExtendedDescription(),
                SHOW_DECK.getValidArgumentCounts());
        this.deckService = deckService;
    }

    @Override
    protected void performAction(TelegramClient telegramClient, String[] arguments) {
        String deckName = arguments[0];
        String result = deckService.getDeckDetails(chatId, deckName);
        sendMessage(telegramClient, result);
    }
}
