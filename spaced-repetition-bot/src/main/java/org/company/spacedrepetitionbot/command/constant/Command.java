package org.company.spacedrepetitionbot.command.constant;

import lombok.Getter;

import java.util.Set;

@Getter
public enum Command {
    // TODO:
    //  create DB table for commands, add functionality to edit commands by admin
    //  add field for command if it available to user by user type
    START("start", "Команда для запуска бота", "/start", Set.of(0)),
    HELP(
            "help",
            "Показывает все команды.\n/help [название команды]\nдля подробной информации",
            "/help\n/help [название команды]",
            Set.of(0, 1)),

    LEARN_NEXT("next", "Учить следующую карту", "/next [название колоды]", Set.of(1)),
    SHOW_ANSWER("answer", "Показать ответ", "/answer [название колоды] [вопрос карточки]", Set.of(2)),
    AGAIN("again", "Повторить карточку", "/again [название колоды] [вопрос карточки]", Set.of(2)),
    HARD("hard", "Сложная карточка", "/hard [название колоды] [вопрос карточки]", Set.of(2)),
    GOOD("good", "Средняя карточка", "/good [название колоды] [вопрос карточки]", Set.of(2)),
    EASY("easy", "Легкая карточка", "/easy [название колоды] [вопрос карточки]", Set.of(2)),
    PREV_CARD("prev", "Предыдущая карточка", "/prev", Set.of(0)),

    MENU("menu", "Открыть меню", "/menu - открыть меню", Set.of(0)),


    UNDO(
            "undo",
            "Отменить последнюю команду",
            "/undo - чтобы удалить последнюю выполненную команду\n/undo [идентификатор команды] - чтобы удалить " +
                    "последнюю выполненную команду с указанным идентификатором",
            Set.of(0, 1)),

    ADD_CARD("addCard", "Добавить карточку в колоду", "/addCard [название колоды] [вопрос] [ответ]", Set.of(3)),
    DELETE_CARD(
            "deleteCard",
            "Удалить карточку из колоды",
            "/deleteCard [название колоды] [начало вопроса]",
            Set.of(2)),
    EDIT_CARD_FRONT(
            "editCardFront",
            "Редактировать вопрос карточки",
            "/editCardFront [название колоды] [текущий вопрос] [новый вопрос]\n",
            Set.of(3)),
    EDIT_CARD_BACK(
            "editCardBack",
            "Редактировать ответ карточки",
            "/editCardBack [название колоды] [начало вопроса] [новый ответ]\n",
            Set.of(3)),
    MOVE_CARD(
            "moveCard",
            "Переместить карточку в другую колоду",
            "/moveCard [название колоды] [начало вопроса] [новая колода]\n",
            Set.of(3)),
    SHOW_CARD("showCard", "Получить карточку из колоды", "/showCard [название колоды] [начало вопроса]", Set.of(2)),
    SHOW_ALL_CARDS("showAllCards", "Получить все карточки из колоды", "/showAllCards [название колоды]", Set.of(1)),

    ADD_DECK("addDeck", "Добавить колоду", "/addDeck [название колоды]", Set.of(1)),
    DELETE_DECK("deleteDeck", "Удалить колоду", "/deleteDeck [название колоды]", Set.of(1)),
    EDIT_DECK("editDeck", "Редактировать колоду", "/editDeck [текущее название колоды] [новое название]", Set.of(2)),
    SHOW_DECK("showDeck", "Показать колоду", "/showDeck [название колоды]", Set.of(1)),
    SHOW_ALL_DECKS("showAllDecks", "Показать все колоды", "/showAllDecks", Set.of(0));

    private final String alias;
    private final String description;
    private final String extendedDescription;
    private final Set<Integer> validArgumentCounts;

    Command(String alias, String description, String extendedDescription, Set<Integer> validArgumentCounts) {
        this.alias = alias;
        this.description = description;
        this.extendedDescription = extendedDescription;
        this.validArgumentCounts = validArgumentCounts;
    }
}
