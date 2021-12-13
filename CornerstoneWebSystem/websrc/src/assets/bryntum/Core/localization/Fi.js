import LocaleManager from '../../Core/localization/LocaleManager.js';

const locale = {

    localeName : 'Fi',
    localeDesc : 'Suomi',

    // Translations for common words and phrases which are used by all classes.
    Object : {
        Yes,
        No,
        Cancel
    },

    //region Mixins

    InstancePlugin : {
        fnMissing         : data => `Försöker att länka fn ${data.plugIntoName}#${data.fnName}, men plugin fn ${data.pluginName}#${data.fnName} finns inte`,
        overrideFnMissing : data => `Försöker att skriva över fn ${data.plugIntoName}#${data.fnName}, men plugin fn ${data.pluginName}#${data.fnName} finns inte`
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

    Tooltip : {
        'Loading...' : 'Laddar...'
    },

    //endregion

    //region Others

    // TODO: Correct this locale, it's copied from SvSE
    DateHelper : {
        locale       : 'fi',
        shortWeek    : 'V',
        shortQuarter : 'q',
        unitNames    : [
            { single : 'ms',      plural : 'ms',       abbrev : 'ms' },
            { single : 'sekund',  plural : 'sekunder', abbrev : 's' },
            { single : 'minut',   plural : 'minuter',  abbrev : 'min' },
            { single : 'timme',   plural : 'timmar',   abbrev : 'tim' },
            { single : 'dag',     plural : 'dagar',    abbrev : 'd' },
            { single : 'vecka',   plural : 'veckor',   abbrev : 'v' },
            { single : 'månad',   plural : 'månader',  abbrev : 'mån' },
            { single : 'kvartal', plural : 'kvartal',  abbrev : 'kv' },
            { single : 'år',      plural : 'år',       abbrev : 'år' }
        ],
        // Used to build a RegExp for parsing time units.
        // The full names from above are added into the generated Regexp.
        // So you may type "2 v" or "2 ve" or "2 vecka" or "2 veckor" into a DurationField.
        // When generating its display value though, it uses the full localized names above.
        unitAbbreviations : [
            ['mil'],
            ['s', 'sek'],
            ['m', 'min'],
            ['t', 'tim'],
            ['d'],
            ['v', 've'],
            ['må', 'mån'],
            ['kv', 'kva'],
            []
        ]
    },

    PagingToolbar : {
        firstPage         : 'Siirry ensimmäiselle sivulle',
        prevPage          : 'Siirry edelliselle sivulle',
        page              : 'Sivu',
        nextPage          : 'Siirry seuraavalle sivulle',
        lastPage          : 'Siirry viimeiselle sivulle',
        reload            : 'Lataa nykyinen sivu uudelleen',
        noRecords         : 'Ei näytettäviä rivejä',
        pageCountTemplate : data => `of ${data.lastPage}`,
        summaryTemplate   : data => `Näyttää tietueet ${data.start} - ${data.end}/${data.allCount}`
    }

    //endregion
};

export default locale;

LocaleManager.registerLocale('Fi', { desc : 'Suomi',  path : 'lib/Core/localization/Fi.js', locale : locale });
