import LocaleManager from '../../Core/localization/LocaleManager.js';

const locale = {

    localeName : 'Ru',
    localeDesc : 'Russian',

    // Translations for common words and phrases which are used by all classes.
    Object : {
        Yes    : 'Да',
        No     : 'Нет',
        Cancel : 'Отмена'
    },

    //region Mixins

    InstancePlugin : {
        fnMissing         : data => `Пытаемся связать метод ${data.plugIntoName}#${data.fnName}, но в плагине не был найден метод ${data.pluginName}#${data.fnName}`,
        overrideFnMissing : data => `Пытаемся перегрузить метод ${data.plugIntoName}#${data.fnName}, но в плагине не был найден метод ${data.pluginName}#${data.fnName}`
    },
    
    //endregion

    //region Widgets

    Field : {
        invalidValue          : 'Недопустимое значение поля',
        minimumValueViolation : 'Нарушение минимального значения',
        maximumValueViolation : 'Нарушение максимального значения',
        fieldRequired         : 'Поле не может быть пустым',
        validateFilter        : 'Выберите значение из списка'
    },

    DateField : {
        invalidDate : 'Невернывй формат даты'
    },

    TimeField : {
        invalidTime : 'Неверный формат времени'
    },

    //endregion

    //region Others

    DateHelper : {
        locale       : 'ru',
        shortWeek    : 'нед',
        shortQuarter : 'квар',
        week         : 'Hеделя',
        weekStartDay : 1,
        unitNames    : [
            { single : 'миллисек', plural : 'миллисек',  abbrev : 'мс' },
            { single : 'секунда',  plural : 'секунд',    abbrev : 'с' },
            { single : 'минута',   plural : 'минут',     abbrev : 'мин' },
            { single : 'час',      plural : 'часов',     abbrev : 'ч' },
            { single : 'день',     plural : 'дней',      abbrev : 'д' },
            { single : 'неделя',   plural : 'недели',    abbrev : 'нед' },
            { single : 'месяц',    plural : 'месяцев',   abbrev : 'мес' },
            { single : 'квартал',  plural : 'кварталов', abbrev : 'квар' },
            { single : 'год',      plural : 'лет',       abbrev : 'г' }
        ],
        // Used to build a RegExp for parsing time units.
        // The full names from above are added into the generated Regexp.
        // So you may type "2 н" or "2 нед" or "2 неделя" or "2 недели" into a DurationField.
        // When generating its display value though, it uses the full localized names above.
        unitAbbreviations : [
            ['мс', 'мил'],
            ['с', 'сек'],
            ['м', 'мин'],
            ['ч'],
            ['д', 'ден', 'дне'],
            ['н', 'нед'],
            ['мес'],
            ['к', 'квар', 'квр'],
            ['г']
        ],
        parsers : {
            'L'  : 'DD.MM.YYYY',
            'LT' : 'HH:mm'
        },
        ordinalSuffix : number => `${number}-й`
    },

    PagingToolbar : {
        firstPage         : 'Перейти на первую страницу',
        prevPage          : 'Перейти на предыдущую страницу',
        page              : 'страница',
        nextPage          : 'Перейти на следующую страницу',
        lastPage          : 'Перейти на последнюю страницу',
        reload            : 'Перезагрузить текущую страницу',
        noRecords         : 'Нет записей для отображения',
        pageCountTemplate : data => `из ${data.lastPage}`,
        summaryTemplate   : data => `Показаны записи ${data.start} - ${data.end} из ${data.allCount}`
    },

    List : {
        loading : 'Загрузка...'
    }

    //endregion
};

export default locale;

LocaleManager.registerLocale('Ru', { desc : 'Русский', path : 'lib/Core/localization/Ru.js', locale : locale });
