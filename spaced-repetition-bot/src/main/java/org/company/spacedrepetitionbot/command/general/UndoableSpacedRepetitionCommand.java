package org.company.spacedrepetitionbot.command.general;

import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Set;

// TODO реализовать для
//  delete,
//  editcardback,
//  editcardfront,
//  movecard, adddeck,
//  deletedeck,
//  editdeck,
//  again,
//  easy,
//  good,
//  hard,
//  learn
public abstract class UndoableSpacedRepetitionCommand extends SpacedRepetitionCommand {
    public UndoableSpacedRepetitionCommand(String commandIdentifier, String description, String extendedDescription, Set<Integer> validArgumentCounts) {
        super(commandIdentifier, description, extendedDescription, validArgumentCounts);
    }

    public abstract String undo(String[] arguments);

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, Integer messageId, String[] arguments) {
        super.execute(telegramClient, user, chat, messageId, arguments);
        saveToCommandHistory(arguments);
    }

    protected abstract void saveToCommandHistory(String[] arguments);
}
