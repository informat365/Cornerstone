import LocaleManager from '../../Core/localization/LocaleManager.js';

const locale = {

    localeName : 'Cn',
    localeDesc : '中文',

    //region Columns

    TemplateColumn : {
        noTemplate : 'TemplateColumn needs a template',
        noFunction : 'TemplateColumn.template must be a function'
    },

    ColumnStore : {
        columnTypeNotFound : data => `Column type '${data.type}' not registered`
    },

    //endregion

    //region Mixins

    InstancePlugin : {
        fnMissing         : data => `Trying to chain fn ${data.plugIntoName}#${data.fnName}, but plugin fn ${data.pluginName}#${data.fnName} does not exist`,
        overrideFnMissing : data => `Trying to override fn ${data.plugIntoName}#${data.fnName}, but plugin fn ${data.pluginName}#${data.fnName} does not exist`
    },

    //endregion

    //region Features

    ColumnPicker : {
        columnsMenu     : 'Columns',
        hideColumn      : 'Hide column',
        hideColumnShort : 'Hide'
    },

    Filter : {
        applyFilter  : 'Apply filter',
        filter       : 'Filter',
        editFilter   : 'Edit filter',
        on           : 'On',
        before       : 'Before',
        after        : 'After',
        equals       : 'Equals',
        lessThan     : 'Less than',
        moreThan     : 'More than',
        removeFilter : 'Remove filter'
    },

    FilterBar : {
        enableFilterBar  : 'Show filter bar',
        disableFilterBar : 'Hide filter bar'
    },

    Group : {
        groupAscending       : 'Group ascending',
        groupDescending      : 'Group descending',
        groupAscendingShort  : 'Ascending',
        groupDescendingShort : 'Descending',
        stopGrouping         : 'Stop grouping',
        stopGroupingShort    : 'Stop'
    },

    Search : {
        searchForValue : 'Search for value'
    },

    Sort : {
        'sortAscending'          : 'Sort ascending',
        'sortDescending'         : 'Sort descending',
        'multiSort'              : 'Multi sort',
        'removeSorter'           : 'Remove sorter',
        'addSortAscending'       : 'Add ascending sorter',
        'addSortDescending'      : 'Add descending sorter',
        'toggleSortAscending'    : 'Change to ascending',
        'toggleSortDescending'   : 'Change to descending',
        'sortAscendingShort'     : 'Ascending',
        'sortDescendingShort'    : 'Descending',
        'removeSorterShort'      : 'Remove',
        'addSortAscendingShort'  : '+ Ascending',
        'addSortDescendingShort' : '+ Descending'
    },

    Tree : {
        noTreeColumn : 'To use the tree feature one column must be configured with tree: true'
    },

    //endregion

    //region Grid

    Grid : {
        featureNotFound          : data => `Feature '${data}' not available, make sure you have imported it`,
        invalidFeatureNameFormat : data => `Invalid feature name '${data}', must start with a lowercase letter`,
        removeRow                : 'Delete record',
        removeRows               : 'Delete records',
        loadFailedMessage        : 'Data loading failed.',
        moveColumnLeft           : 'Move to left section',
        moveColumnRight          : 'Move to right section'
    },

    GridBase : {
        loadMask : 'Loading...',
        noRows   : 'No records to display'
    },

    //endregion

    //region Widgets

    Field : {
        invalidValue          : 'Invalid field value',
        minimumValueViolation : 'Minimum value violation',
        maximumValueViolation : 'Maximum value violation',
        fieldRequired         : 'This field is required',
        validateFilter        : 'Value must be selected from the list'
    },

    DateField : {
        invalidDate : 'Invalid date input'
    },

    TimeField : {
        invalidTime : 'Invalid time input'
    },

    //endregion

    //region Others

    DateHelper : {
        locale       : 'zh-CN',
        shortWeek    : '周',
        shortQuarter : 'q',
        week         : '周',
        weekStartDay : 0,
        unitNames    : [
            { single : 'millisecond', plural : 'ms',       abbrev : 'ms' },
            { single : 'second',      plural : 'seconds',  abbrev : 's' },
            { single : 'minute',      plural : 'minutes',  abbrev : 'min' },
            { single : 'hour',        plural : 'hours',    abbrev : 'h' },
            { single : 'day',         plural : 'days',     abbrev : 'd' },
            { single : 'week',        plural : 'weeks',    abbrev : 'w' },
            { single : 'month',       plural : 'months',   abbrev : 'mon' },
            { single : 'quarter',     plural : 'quarters', abbrev : 'q' },
            { single : 'year',        plural : 'years',    abbrev : 'yr' }
        ],
        // Used to build a RegExp for parsing time units.
        // The full names from above are added into the generated Regexp.
        // So you may type "2 w" or "2 wk" or "2 week" or "2 weeks" into a DurationField.
        // When generating its display value though, it uses the full localized names above.
        unitAbbreviations : [
            ['mil'],
            ['s', 'sec'],
            ['m', 'min'],
            ['h', 'hr'],
            ['d'],
            ['w', 'wk'],
            ['mo', 'mon', 'mnt'],
            ['q', 'quar', 'qrt'],
            ['y', 'yr']
        ],
        parsers : {
            'L'  : 'YYYY/MM/DD',
            'LT' : 'HH:mm A'
        },
        ordinalSuffix : number => number + ({ '1' : 'st', '2' : 'nd', '3' : 'rd' }[number[number.length - 1]] || 'th')
    },

    BooleanCombo : {
        'Yes' : 'Yes',
        'No'  : 'No'
    },

    PagingToolbar : {
        firstPage         : 'Go to first page',
        prevPage          : 'Go to previous page',
        page              : 'Page',
        nextPage          : 'Go to next page',
        lastPage          : 'Go to last page',
        reload            : 'Reload current page',
        noRecords         : 'No records to display',
        pageCountTemplate : data => `of ${data.lastPage}`,
        summaryTemplate   : data => `Displaying records ${data.start} - ${data.end} of ${data.allCount}`
    },

    List : {
        loading : 'Loading...'
    }

    //endregion
};

export default locale;

LocaleManager.registerLocale('Cn', { desc : '中文', locale : locale });
