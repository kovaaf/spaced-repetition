package org.company.spacedrepetitionbot.constants;

import lombok.Getter;

@Getter
public enum MessageConstants {
    ERROR_MESSAGE("Что-то пошло не так\n"),

    WELCOME_MESSAGE("Приветствую, %s! 🚀\n\nЯ бот для изучения с помощью интервальных повторений.\n\n"),

    USER_INITIALIZATION_ERROR("⚠️ Ошибка инициализации пользователя: %s\n"
            + "Попробуйте снова или обратитесь в поддержку"),

    WRONG_ARGUMENTS_COUNT_MULTIPLE(
            """
                    ⚠️ Неверное количество аргументов: %d
                    Ожидается: %s аргументов
                    Попробуйте:
                    %s"""
    ),
    CARD_DETAILS_TEMPLATE(
            """
                    📊 Статистика карточки:
                    ➖ Повторений: %d
                    ➖ Следующий повтор через: %s
                    ➖ Дата повтора: %s
                    ➖ Статус: %s
                    
                    ❓ Вопрос:
                    %s
                    
                    ❗ Ответ:
                    %s"""
    ),
    INTERVAL_DAYS("%d дней"),
    INTERVAL_HOURS("%d часов"),
    INTERVAL_MINUTES("%d минут"),
    DUE_TODAY("сегодня"),
    NOT_SCHEDULED("не запланировано"),
    DATE_TIME_FORMAT("dd.MM.yyyy HH:mm"),
    WRONG_ARGUMENTS_TEMPLATE_MESSAGE("%s\nОжидается: %d аргументов\nПопробуйте:\n%s"),
    EMPTY_ARGUMENT_TEMPLATE_MESSAGE("%sАргумент #%d не может быть пустым"),
    DEBUG_EXECUTION_TEMPLATE_MESSAGE("Исполняется команда:\n{} Для пользователя:\n{} В чате:\n{} С аргументами:\n{}"),
    DECK_NOT_FOUND_TEMPLATE("Колода '%s' не найдена\nПопробуйте:\n%s"),
    CARD_NOT_FOUND_IN_DECK_TEMPLATE("Карта '%s' не найдена в колоде '%s'\nПопробуйте:\n%s"),
    DECK_ADDED_SUCCESSFULLY("Колода '%s' успешно добавлена."),
    DECK_DELETED_SUCCESSFULLY("Колода '%s' успешно удалена."),
    DECK_RENAMED_SUCCESSFULLY("Название колоды изменено с '%s' на '%s'"),
    CARD_ADDED_SUCCESSFULLY("Карточка '%s' успешно добавлена в колоду '%s'"),
    CARD_DELETED_SUCCESSFULLY("Карточка '%s' удалена из колоды '%s'"),
    CARD_FRONT_UPDATED("Название карточки изменено с '%s' на '%s'"),
    CARD_BACK_UPDATED("Обновлена обратная сторона карточки '%s'"),
    CARD_MOVED_SUCCESSFULLY("Карта '%s' перемещена из колоды '%s' в колоду '%s'"),
    CARD_FORMAT("• %s - %s"),
    CARD_ALREADY_IN_DECK("Карта '%s' уже находится в колоде '%s'"),
    CARD_ALREADY_EXISTS_TEMPLATE("Карта '%s' уже существует в колоде '%s'"),
    DECK_ALREADY_EXISTS_TEMPLATE("Колода '%s' уже существует\nПопробуйте:\n%s"),
    USER_NOT_FOUND("Пользователь %s не найден. Вызовите команду /start для инициализации"),
    DECK_LIST_TEMPLATE("Ваш список колод:\n%s"),
    DECK_NOT_FOUND_SIMPLE("Колода '%s' не найдена"),
    CARD_NOT_FOUND_SIMPLE("Карточка '%s' не найдена в колоде '%s'"),
    DECK_ALREADY_EXISTS_SIMPLE("Колода с названием '%s' уже существует"),
    CARD_ALREADY_EXISTS_SIMPLE("Карточка с названием '%s' уже существует в колоде '%s'"),
    NO_DECKS_FOUND("📭 У вас пока нет ни одной колоды"),
    DECK_LIST_HEADER("📚 Ваши колоды (%d):\n\n"),
    DECK_ITEM_FORMAT("%d. %s (%d карточек)"),
    DECK_DETAILS_TEMPLATE(
            """
                    📘 Колода: %s
                    🔢 Количество карточек: %d
                    
                    📋 Карточки:
                    %s"""
    ),
    DECK_NOT_FOUND_MESSAGE("❌ Колода '%s' не найдена"),
    CARD_ITEM_FORMAT("• %s"),
    NO_CARDS_IN_DECK("ℹ️ В колоде пока нет карточек"),
    CARD_WITH_MINIMUM_INTERVAL("""
        <b>%s</b>
        
        <b>Возможные действия:</b>
        Показать ответ:
        <code>/%s %s %s</code>
        Предыдущая карточка:
        <code>/%s</code>"""),
    CARD_BACK("""
        <b>%s</b>
        
        <b>Возможные действия:</b>
        Повторить карточку:
        <code>/%s %s %s</code> (повтор через: %s)
        Сложная карточка:
        <code>/%s %s %s</code> (повтор через: %s)
        Средняя карточка:
        <code>/%s %s %s</code> (повтор через: %s)
        Легкая карточка:
        <code>/%s %s %s</code> (повтор через: %s)
        Предыдущая карточка:
        <code>/%s</code>"""),
    ANSWER("""
        <b>%s</b>
        
        <b>Возможные действия:</b>
        Изучить следующую карточку:
        <code>/%s %s</code>
        Предыдущая карточка:
        <code>/%s</code>"""),

    // Ошибки
    ERROR_ADDING_DECK("Ошибка добавления колоды: "),
    ERROR_DELETING_DECK("Ошибка удаления колоды: "),
    ERROR_RENAMING_DECK("Ошибка переименования колоды: "),
    ERROR_ADDING_CARD("Ошибка добавления карточки: "),
    ERROR_DELETING_CARD("Ошибка удаления карточки: "),
    ERROR_UPDATING_CARD_FRONT("Ошибка обновления названия карточки: "),
    ERROR_UPDATING_CARD_BACK("Ошибка обновления обратной стороны карточки: "),
    ERROR_MOVING_CARD("Ошибка перемещения карты: "),
    ERROR_RETRIEVING_CARD("Ошибка получения карточки: "),
    ERROR_RETRIEVING_CARDS("Ошибка получения карточек: ");

    private final String message;

    MessageConstants(String message) {
        this.message = message;
    }
}
