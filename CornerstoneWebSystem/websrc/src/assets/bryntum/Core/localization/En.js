import LocaleManager from '../../Core/localization/LocaleManager.js';

const locale = {

    localeName : 'En',
    localeDesc : 'English',

    // Translations for common words and phrases which are used by all classes.
    Object : {
        Yes    : 'Yes',
        No     : 'No',
        Cancel : 'Cancel'
    },

    //region Mixins

    InstancePlugin : {
        fnMissing         : data => `Trying to chain fn ${data.plugIntoName}#${data.fnName}, but plugin fn ${data.pluginName}#${data.fnName} does not exist`,
        overrideFnMissing : data => `Trying to override fn ${data.plugIntoName}#${data.fnName}, but plugin fn ${data.pluginName}#${data.fnName} does not exist`
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
        locale       : 'en-US',
        shortWeek    : 'W',
        shortQuarter : 'q',
        week         : 'Week',
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
            'L'  : 'MM/DD/YYYY',
            'LT' : 'HH:mm A'
        },
        ordinalSuffix : number => number + ({ '1' : 'st', '2' : 'nd', '3' : 'rd' }[number[number.length - 1]] || 'th')
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

LocaleManager.registerLocale('En', { desc : 'English', locale : locale });
