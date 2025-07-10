package org.company.spacedrepetitionbot.command.general;

import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.model.ExecutedCommand;
import org.company.spacedrepetitionbot.service.CommandHistoryService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Optional;

import static org.company.spacedrepetitionbot.command.constant.Command.UNDO;

@Slf4j
@Component
public class UndoCommand extends SpacedRepetitionCommand {
    private final CommandHistoryService commandHistoryService;
    private final ICommandRegistry commandRegistry;

    public UndoCommand(CommandHistoryService commandHistoryService, @Lazy ICommandRegistry commandRegistry) {
        super(
                UNDO.getAlias(),
                UNDO.getDescription(),
                UNDO.getExtendedDescription(),
                UNDO.getValidArgumentCounts()
        );
        this.commandHistoryService = commandHistoryService;
        this.commandRegistry = commandRegistry;
    }

    @Override
    protected void performAction(TelegramClient telegramClient, String[] arguments) {
        Optional<ExecutedCommand> lastCommand = getCommandFromHistory(arguments);
        if (lastCommand.isEmpty()) {
            String result = "История команд пуста";
            sendMessage(telegramClient, result);
        } else {
            ExecutedCommand command = lastCommand.get();

            String result = executeUndo(command);
            sendMessage(telegramClient, result);
        }
    }

    private Optional<ExecutedCommand> getCommandFromHistory(String[] arguments) {
        Optional<ExecutedCommand> lastCommand;
        if (arguments.length == 0) {
            lastCommand = commandHistoryService.getLastCommand(chatId);
        } else {
            lastCommand = commandHistoryService.getLastCommandByIdentifier(chatId, arguments[0]);
        }
        return lastCommand;
    }

    public String executeUndo(ExecutedCommand command) {
        UndoableSpacedRepetitionCommand undoableCommand = (UndoableSpacedRepetitionCommand) commandRegistry.getRegisteredCommand(
                command.getCommandIdentifier()
        );

        String[] arguments = command.getArguments();
        String undoResult = undoableCommand.undo(arguments);
        commandHistoryService.deleteCommand(command.getId());

        return String.format("Отмена команды: %s\n%s",
                command.getCommandIdentifier(),
                undoResult);
    }
}
