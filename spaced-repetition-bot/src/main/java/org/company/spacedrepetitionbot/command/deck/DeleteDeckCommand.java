package org.company.spacedrepetitionbot.command.deck;

import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.command.general.SpacedRepetitionCommand;
import org.company.spacedrepetitionbot.service.DeckService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.company.spacedrepetitionbot.command.constant.Command.DELETE_DECK;

@Slf4j
@Component
public class DeleteDeckCommand extends SpacedRepetitionCommand {
    private final DeckService deckService;

    public DeleteDeckCommand(DeckService deckService) {
        super(
                DELETE_DECK.getAlias(),
                DELETE_DECK.getDescription(),
                DELETE_DECK.getExtendedDescription(),
                DELETE_DECK.getValidArgumentCounts());
        this.deckService = deckService;
    }

    @Override
    protected void performAction(TelegramClient telegramClient, String[] arguments) {
        // TODO исправить удаление непустых колод
        // TODO добавить удаление нескольких колод одной командой
        String deckName = arguments[0];
        String result = deckService.deleteDeck(chatId, deckName);
        sendMessage(telegramClient, result);
    }
}
