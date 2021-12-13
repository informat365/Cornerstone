import LocaleManager from '../../Core/localization/LocaleManager.js';

const locale = {

    localeName : 'Nl',
    localeDesc : 'Nederlands',

    // Translations for common words and phrases which are used by all classes.
    Object : {
        Yes    : 'Ja',
        No     : 'Nee',
        Cancel : 'Annuleren'
    },

    //region Mixins

    InstancePlugin : {
        fnMissing         : data => `Het lukt niet fn ${data.plugIntoName}#${data.fnName} te schakelen, de plugin fn ${data.pluginName}#${data.fnName} bestaat niet`,
        overrideFnMissing : data => `Het lukt niet fn te overerven ${data.plugIntoName}#${data.fnName}, de plugin fn ${data.pluginName}#${data.fnName} bestaat niet`
    },
    
    //endregion

    //region Widgets

    Field : {
        invalidValue          : 'Ongeldige veldwaarde',
        minimumValueViolation : 'Minimale waarde schending',
        maximumValueViolation : 'Maximale waarde schending',
        fieldRequired         : 'Dit veld is verplicht',
        validateFilter        : 'Waarde moet worden geselecteerd in de lijst'
    },

    DateField : {
        invalidDate : 'Ongeldige datuminvoer'
    },

    TimeField : {
        invalidTime : 'Ongeldige tijdsinvoer'
    },

    //endregion

    //region Others

    DateHelper : {
        locale       : 'nl',
        shortWeek    : 'w',
        shortQuarter : 'kw',
        week         : 'Week',
        weekStartDay : 1,
        unitNames    : [
            { single : 'ms',       plural : 'ms',        abbrev : 'ms' },
            { single : 'seconde',  plural : 'seconden',  abbrev : 's' },
            { single : 'minuut',   plural : 'minuten',   abbrev : 'm' },
            { single : 'uur',      plural : 'uren',      abbrev : 'u' },
            { single : 'dag',      plural : 'dagen',     abbrev : 'd' },
            { single : 'week',     plural : 'weken',     abbrev : 'w' },
            { single : 'maand',    plural : 'maanden',   abbrev : 'ma' },
            { single : 'kwartaal', plural : 'kwartalen', abbrev : 'kw' },
            { single : 'jaar',     plural : 'jaren',     abbrev : 'j' }
        ],
        // Used to build a RegExp for parsing time units.
        // The full names from above are added into the generated Regexp.
        // So you may type "2 w" or "2 wk" or "2 week" or "2 weken" into a DurationField.
        // When generating its display value though, it uses the full localized names above.
        unitAbbreviations : [
            ['mil'],
            ['s', 'sec'],
            ['m', 'min'],
            ['u'],
            ['d'],
            ['w', 'wk'],
            ['ma', 'mnd', 'm'],
            ['k', 'kwar', 'kwt', 'kw'],
            ['j', 'jr']
        ],
        parsers : {
            'L'  : 'DD-MM-YYYY',
            'LT' : 'HH:mm'
        },
        ordinalSuffix : number => number
    },

    PagingToolbar : {
        firstPage         : 'Ga naar de eerste pagina',
        prevPage          : 'Ga naar de vorige pagina',
        page              : 'Pagina',
        nextPage          : 'Ga naar de volgende pagina',
        lastPage          : 'Ga naar de laatste pagina',
        reload            : 'Laad huidige pagina opnieuw',
        noRecords         : 'Geen rijen om weer te geven',
        pageCountTemplate : data => `van ${data.lastPage}`,
        summaryTemplate   : data => `Records ${data.start} - ${data.end} van ${data.allCount} worden weergegeven`
    },

    List : {
        loading : 'Laden...'
    }

    //endregion
};

export default locale;

LocaleManager.registerLocale('Nl', { desc : 'Nederlands', locale : locale });
