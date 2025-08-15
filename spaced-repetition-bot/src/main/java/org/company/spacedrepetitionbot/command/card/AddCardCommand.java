package org.company.spacedrepetitionbot.command.card;

import lombok.extern.slf4j.Slf4j;
import org.company.spacedrepetitionbot.command.general.UndoableSpacedRepetitionCommand;
import org.company.spacedrepetitionbot.model.UserInfo;
import org.company.spacedrepetitionbot.service.CardService;
import org.company.spacedrepetitionbot.service.CommandHistoryService;
import org.company.spacedrepetitionbot.service.UserInfoService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.company.spacedrepetitionbot.command.constant.Command.ADD_CARD;

@Slf4j
@Component
public class AddCardCommand extends UndoableSpacedRepetitionCommand {
    private final CardService cardService;
    private final CommandHistoryService commandHistoryService;
    private final UserInfoService userInfoService;

    public AddCardCommand(
            CardService cardService,
            CommandHistoryService commandHistoryService,
            UserInfoService userInfoService) {
        super(
                ADD_CARD.getAlias(),
                ADD_CARD.getDescription(),
                ADD_CARD.getExtendedDescription(),
                ADD_CARD.getValidArgumentCounts());
        this.cardService = cardService;
        this.commandHistoryService = commandHistoryService;
        this.userInfoService = userInfoService;
    }

    @Override
    public String undo(String[] arguments) {
        try {
            String deckName = arguments[0];
            String front = arguments[1];
            return cardService.deleteCard(chatId, deckName, front);
        } catch (Exception e) {
            log.error("Ошибка при отмене добавления карточки", e);
            return "Ошибка при отмене: " + e.getMessage();
        }
    }

    @Override
    protected void saveToCommandHistory(String[] arguments) {
        UserInfo userInfo = userInfoService.getOrCreate(getCurrentUser());
        commandHistoryService.saveCommand(userInfo, getCommandIdentifier(), arguments);
    }

    @Override
    protected void performAction(TelegramClient telegramClient, String[] arguments) {
        String deckName = arguments[0];
        String front = arguments[1];
        String back = arguments[2];
        String result = cardService.addCard(chatId, deckName, front, back);
        sendMessage(telegramClient, result);
    }
}
