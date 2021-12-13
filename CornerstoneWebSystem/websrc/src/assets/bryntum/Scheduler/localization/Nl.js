import LocaleManager from '../../Core/localization/LocaleManager.js';
import gridLocale from '../../Grid/localization/Nl.js';

// extends locale from grid
//const locale = Object.assign(gridLocale, {
const locale = {

    SchedulerCommon : {
        // SS              : 'GB',
        // SF              : 'BE',
        // FS              : 'EB',
        // FF              : 'GE',
        // StartToStart    : 'Gelijk-Begin',
        // StartToEnd      : 'Begin-na-Einde',
        // EndToStart      : 'Einde-na-Begin',
        // EndToEnd        : 'Gelijk-Einde',
        // dependencyTypes : [
        //     'GB',
        //     'BE',
        //     'EB',
        //     'GE'
        // ],
        // dependencyTypesLong : [
        //     'Gelijk-Begin',
        //     'Begin-na-Einde',
        //     'Einde-na-Begin',
        //     'Gelijk-Einde'
        // ]
    },

    ExcelExporter : {
        'No resource assigned' : 'Geen resource toegewezen'
    },

    ResourceInfoColumn : {
        eventCountText : function(data) {
            return data + ' evenement' + (data !== 1 ? 'en' : '');
        }
    },

    Dependencies : {
        from     : 'Van',
        to       : 'Naar',
        valid    : 'Geldig',
        invalid  : 'Ongeldig',
        Checking : 'Controleren…'
    },

    EventEdit : {
        Name         : 'Naam',
        Resource     : 'Resource',
        Start        : 'Start',
        End          : 'Eind',
        Save         : 'Bewaar',
        Delete       : 'Verwijder',
        Cancel       : 'Annuleer',
        'Edit Event' : 'Wijzig item',
        Repeat       : 'Herhaal'
    },

    DependencyEdit : {
        From              : 'Van',
        To                : 'Tot',
        Type              : 'Type',
        Lag               : 'Achterstand',
        'Edit dependency' : 'Afhankelijkheid bewerken',
        Save              : 'Bewaar',
        Delete            : 'Verwijder',
        Cancel            : 'Annuleer',
        StartToStart      : 'Begin-Tot-Begin',
        StartToEnd        : 'Begin-Tot-Einde',
        EndToStart        : 'Einde-Tot-Start',
        EndToEnd          : 'Einde-Tot-Einde'
    },

    EventDrag : {
        eventOverlapsExisting : 'Gebeurtenis overlapt bestaande gebeurtenis voor deze bron',
        noDropOutsideTimeline : 'Evenement kan niet volledig buiten de tijdlijn worden verwijderd'
    },

    Scheduler : {
        'Add event'      : 'Voeg evenement toe',
        'Delete event'   : 'Verwijder item',
        'Unassign event' : 'Gebeurtenis ongedaan maken'
    },

    HeaderContextMenu : {
        pickZoomLevel   : 'Zoom in',
        activeDateRange : 'Datum bereik',
        startText       : 'Start datum',
        endText         : 'Eind datum',
        todayText       : 'Vandaag'
    },

    EventFilter : {
        filterEvents : 'Filter items',
        byName       : 'Op naam'
    },

    TimeRanges : {
        showCurrentTimeLine : 'Maak huidige tijdlijn zichtbaar'
    },

    PresetManager : {
        minuteAndHour : {
            topDateFormat : 'ddd DD-MM, hh'
        },
        hourAndDay : {
            topDateFormat : 'ddd DD-MM'
        },
        weekAndDay : {
            displayDateFormat : 'hh:mm'
        }
    },

    RecurrenceConfirmationPopup : {
        'delete-title'              : 'U verwijdert een plan item',
        'delete-all-message'        : 'Wilt u alle herhaalde afspraken van dit item verwijderen?',
        'delete-further-message'    : 'Wilt u het geselecteerde en alle toekomstige gebeurtenissen van dit item verwijderen, of aleen het geselecteerde item?',
        'delete-further-btn-text'   : 'Verwijder alleen de toekomstige gebeurtenissen',
        'delete-only-this-btn-text' : 'Verwijder alleen deze gebeurtenis',

        'update-title'              : 'U verandert een herhaald item',
        'update-all-message'        : 'Wilt u alle herhaalde afspraken van dit item verwijderen?',
        'update-further-message'    : 'Wilt u het geselecteerde en alle toekomstige gebeurtenissen van dit item wijzigen, of aleen het geselecteerde item?',
        'update-further-btn-text'   : 'Wijzig alle toekomstige items',
        'update-only-this-btn-text' : 'Wijzig alleen dit item',

        'Yes'    : 'Ja',
        'Cancel' : 'Annuleer',

        width : 600
    },

    RecurrenceLegend : {
        ' and '                         : ' en ',
        // frequency patterns
        'Daily'                         : 'Dagelijks',
        'Weekly on {1}'                 : ({ days }) => `Wekelijks op ${days}`,
        'Monthly on {1}'                : ({ days }) => `Maandelijks op ${days}`,
        'Yearly on {1} of {2}'          : ({ days, months }) => `Jaarlijks op ${days} ${months}`,
        'Every {0} days'                : ({ interval }) => `Elke ${interval} dagen`,
        'Every {0} weeks on {1}'        : ({ interval, days }) => `Elke ${interval} weken op ${days}`,
        'Every {0} months on {1}'       : ({ interval, days }) => `Elke ${interval} maanden in ${days}`,
        'Every {0} years on {1} of {2}' : ({ interval, days, months }) => `Elke ${interval} jaar op ${days} ${months}`,
        // day position translations
        'position1'                     : 'de eerste',
        'position2'                     : 'de tweede',
        'position3'                     : 'de derde',
        'position4'                     : 'de vierde',
        'position5'                     : 'de vijfde',
        'position-1'                    : 'laatste',
        // day options
        'day'                           : 'dag',
        'weekday'                       : 'weekdag',
        'weekend day'                   : 'weekend dag',
        // {0} - day position info ("the last"/"the first"/...)
        // {1} - day info ("Sunday"/"Monday"/.../"day"/"weekday"/"weekend day")
        // For example:
        //  "the last Sunday"
        //  "the first weekday"
        //  "the second weekend day"
        'daysFormat'                    : ({ position, days }) => `${position} ${days}`
    },

    RecurrenceEditor : {
        'Repeat event'        : 'Herhaal gebeurtenis',
        'Cancel'              : 'Annuleer',
        'Save'                : 'Bewaar',
        'Frequency'           : 'Frequentie',
        'Every'               : 'Elke',
        'DAILYintervalUnit'   : 'dag(en)',
        'WEEKLYintervalUnit'  : 'week(en) op:',
        'MONTHLYintervalUnit' : 'maand(en)',
        'YEARLYintervalUnit'  : 'jaren(en) in:',
        'Each'                : 'Elke',
        'On the'              : 'Op de',
        'End repeat'          : 'Einde herhaling',
        'time(s)'             : 'tijd(en)'
    },

    RecurrenceDaysCombo : {
        'day'         : 'dag',
        'weekday'     : 'weekdag',
        'weekend day' : 'weekend dag'
    },

    RecurrencePositionsCombo : {
        'position1'  : 'eerste',
        'position2'  : 'tweede',
        'position3'  : 'derde',
        'position4'  : 'vierde',
        'position5'  : 'vijfde',
        'position-1' : 'laatste'
    },

    RecurrenceStopConditionCombo : {
        'Never'   : 'Nooit',
        'After'   : 'Na',
        'On date' : 'Op datum'
    },

    RecurrenceFrequencyCombo : {
        'Daily'   : 'Dagelijks',
        'Weekly'  : 'Wekelijks',
        'Monthly' : 'Maandelijks',
        'Yearly'  : 'Jaarlijks'
    },

    RecurrenceCombo : {
        'None'      : 'Geen',
        'Custom...' : 'Aangepast...'
    },
    
    //region Export
    
    ScheduleRangeCombo : {
        completeview : 'Compleet schema',
        currentview  : 'Huidige weergave',
        daterange    : 'Periode',
        completedata : 'Alle data (events)'
    },
    
    SchedulerExportDialog : {
        'Schedule range' : 'Scheduler bereik',
        'Export from'    : 'Vanaf',
        'Export to'      : 'Naar'
    }
    
    //endregion

};

// cannot use Object.assign above in IE11. also dont want to have polyfill in locale
for (let i in gridLocale) {
    locale[i] = gridLocale[i];
}

export default locale;

LocaleManager.registerLocale('Nl', { desc : 'Nederlands', locale : locale });
