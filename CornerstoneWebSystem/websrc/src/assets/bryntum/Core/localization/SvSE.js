import LocaleManager from '../../Core/localization/LocaleManager.js';

const locale = {

    localeName : 'SvSE',
    localeDesc : 'Svenska',

    // Translations for common words and phrases which are used by all classes.
    Object : {
        Yes    : 'Ja',
        No     : 'Nej',
        Cancel : 'Avbryt'
    },

    //region Mixins

    InstancePlugin : {
        fnMissing         : data => `Försöker att länka fn ${data.plugIntoName}#${data.fnName}, men plugin fn ${data.pluginName}#${data.fnName} finns inte`,
        overrideFnMissing : data => `Försöker att skriva över fn ${data.plugIntoName}#${data.fnName}, men plugin fn ${data.pluginName}#${data.fnName} finns inte`
    },
    
    //endregion

    //region Widgets

    Field : {
        invalidValue          : 'Ogiltigt värde',
        minimumValueViolation : 'För lågt värde',
        maximumValueViolation : 'För högt värde',
        fieldRequired         : 'Detta fält är obligatoriskt',
        validateFilter        : 'Värdet måste väljas från listan'
    },

    DateField : {
        invalidDate : 'Ogiltigt datum'
    },

    TimeField : {
        invalidTime : 'Ogiltig tid'
    },

    //endregion

    //region Others

    DateHelper : {
        locale       : 'sv-SE',
        shortWeek    : 'V',
        shortQuarter : 'q',
        week         : 'Vecka',
        weekStartDay : 1,
        unitNames    : [
            { single : 'millisekund', plural : 'millisekunder', abbrev : 'ms' },
            { single : 'sekund', plural : 'sekunder', abbrev : 's' },
            { single : 'minut', plural : 'minuter', abbrev : 'min' },
            { single : 'timme', plural : 'timmar', abbrev : 'tim' },
            { single : 'dag', plural : 'dagar', abbrev : 'd' },
            { single : 'vecka', plural : 'vecka', abbrev : 'v' },
            { single : 'månad', plural : 'månader', abbrev : 'mån' },
            { single : 'kvartal', plural : 'kvartal', abbrev : 'kv' },
            { single : 'år', plural : 'år', abbrev : 'år' }
        ],
        // Used to build a RegExp for parsing time units.
        // The full names from above are added into the generated Regexp.
        // So you may type "2 v" or "2 ve" or "2 vecka" or "2 vecka" into a DurationField.
        // When generating its display value though, it uses the full localized names above.
        unitAbbreviations : [
            ['ms', 'mil'],
            ['s', 'sek'],
            ['m', 'min'],
            ['t', 'tim', 'h'],
            ['d'],
            ['v', 've'],
            ['må', 'mån'],
            ['kv', 'kva'],
            []
        ],
        ordinalSuffix : number => {
            const lastDigit = number[number.length - 1];
            return number + (number !== '11' && number !== '12' && (lastDigit === '1' || lastDigit === '2') ? 'a' : 'e');
        },
        parsers : {
            'L'  : 'YYYY-MM-DD',
            'LT' : 'HH:mm'
        }
    },

    PagingToolbar : {
        firstPage         : 'Gå till första sidan',
        prevPage          : 'Gå till föregående sida',
        page              : 'Sida',
        nextPage          : 'Gå till nästa sida',
        lastPage          : 'Gå till sista sidan',
        reload            : 'Ladda om den aktuella sidan',
        noRecords         : 'Inga rader att visa',
        pageCountTemplate : data => `av ${data.lastPage}`,
        summaryTemplate   : data => `Visar poster ${data.start} - ${data.end} av ${data.allCount}`
    },

    List : {
        loading : 'Laddar...'
    }

    //endregion
};

export default locale;

LocaleManager.registerLocale('SvSE', { desc : 'Svenska', path : 'lib/Core/localization/SvSE.js', locale : locale });
