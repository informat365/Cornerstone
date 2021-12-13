import LocaleManager from '../../Core/localization/LocaleManager.js';
import coreLocale from '../../Core/localization/Ru.js';

// Extends locale from grid
const locale = {

    //region Columns

    TemplateColumn : {
        noTemplate : 'TemplateColumn необходим шаблон',
        noFunction : 'TemplateColumn.template должен быть функцией'
    },

    ColumnStore : {
        columnTypeNotFound : data => `Тип колонки '${data.type}' не зарегистрирован`
    },

    //endregion

    //region Features

    ColumnPicker : {
        columnsMenu     : 'Колонки',
        hideColumn      : 'Спрятать колонку',
        hideColumnShort : 'Спрятать'
    },

    Filter : {
        applyFilter  : 'Применить фильтр',
        filter       : 'Фильтр',
        editFilter   : 'Изменить фильтр',
        on           : 'В этот день',
        before       : 'До',
        after        : 'После',
        equals       : 'Равно',
        lessThan     : 'Меньше, чем',
        moreThan     : 'Больше, чем',
        removeFilter : 'Убрать фильтр'
    },

    FilterBar : {
        enableFilterBar  : 'Показать панель фильтров',
        disableFilterBar : 'Спрятать панель фильтров'
    },

    Group : {
        groupAscending       : 'Группа по возрастанию',
        groupDescending      : 'Группа по убыванию',
        groupAscendingShort  : 'Возрастание',
        groupDescendingShort : 'Убывание',
        stopGrouping         : 'Убрать группу',
        stopGroupingShort    : 'Убрать'
    },

    Search : {
        searchForValue : 'Найти значение'
    },

    Sort : {
        'sortAscending'          : 'Сортировать по возрастанию',
        'sortDescending'         : 'Сортировать по убыванию',
        'multiSort'              : 'Сложная сортировка',
        'removeSorter'           : 'Убрать сортировку',
        'addSortAscending'       : 'Добавить сортировку по возрастанию',
        'addSortDescending'      : 'Добавить сортировку по убыванию',
        'toggleSortAscending'    : 'Сортировать по возрастанию',
        'toggleSortDescending'   : 'Сортировать по убыванию',
        'sortAscendingShort'     : 'Возрастание',
        'sortDescendingShort'    : 'Убывание',
        'removeSorterShort'      : 'Убрать',
        'addSortAscendingShort'  : '+ Возраст...',
        'addSortDescendingShort' : '+ Убыв...'

    },

    Tree : {
        noTreeColumn : 'Чтобы использовать дерево необходимо чтобы одна колонка имела настройку tree: true'
    },

    //endregion

    //region Grid

    Grid : {
        featureNotFound          : data => `Опция '${data}' недоступна, убедитесь что она импортирована`,
        invalidFeatureNameFormat : data => `Неверное имя функциональности '${data}', так как оно должно начинаться с маленькой буквы`,
        removeRow                : 'Удалить запись',
        removeRows               : 'Удалить записи',
        loadFailedMessage        : 'Не удалось загрузить',
        moveColumnLeft           : 'Передвинуть в левую секцию',
        moveColumnRight          : 'Передвинуть в правую секцию'
    },

    GridBase : {
        loadMask : 'Загрузка...',
        noRows   : 'Нет записей для отображения'
    },

    //endregion

    //region Export
    
    PdfExport : {
        'Waiting for response from server...' : 'Ожидание ответа от сервера...'
    },
    
    ExportDialog : {
        width          : '40em',
        labelWidth     : '13em',
        exportSettings : 'Настройки',
        export         : 'Экспорт',
        exporterType   : 'Разбивка на страницы',
        cancel         : 'Отмена',
        fileFormat     : 'Формат файла',
        rows           : 'Строки',
        alignRows      : 'Выровнять строки',
        columns        : 'Колонки',
        paperFormat    : 'Размер листа',
        orientation    : 'Ориентация'
    },
    
    ExportRowsCombo : {
        all     : 'Все строки',
        visible : 'Видимые строки'
    },
    
    ExportOrientationCombo : {
        portrait  : 'Портретная',
        landscape : 'Ландшафтная'
    },
    
    SinglePageExporter : {
        singlepage : 'Одна страница'
    },
    
    MultiPageExporter : {
        multipage     : 'Многостраничный',
        exportingPage : ({ currentPage, totalPages }) => `Экспорт страницы ${currentPage}/${totalPages}`
    }
    
    //endregion
};

// Cannot use Object.assign above in IE11. also dont want to have polyfill in locale
for (let i in coreLocale) {
    locale[i] = coreLocale[i];
}

export default locale;

LocaleManager.registerLocale('Ru', { desc : 'Русский', locale : locale });
