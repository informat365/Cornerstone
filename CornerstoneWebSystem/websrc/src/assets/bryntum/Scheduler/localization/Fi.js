import LocaleManager from '../../Core/localization/LocaleManager.js';
import gridLocale from '../../Grid/localization/Fi.js';

// extends locale from grid
//const locale = Object.assign(gridLocale, {
const locale = {

    Dependencies : {
        from     : 'Från',
        to       : 'Till',
        valid    : 'Giltig',
        invalid  : 'Ogiltig',
        checking : 'Tarkkailun…'
    },

    EventEdit : {
        Name         : 'Namn',
        Resource     : 'Resurs',
        Start        : 'Start',
        End          : 'Slut',
        Save         : 'Spara',
        Delete       : 'Ta bort',
        Cancel       : 'Avbryt',
        'Edit Event' : 'Redigera bokning'
    },

    DependencyEdit : {
        From              : 'From',
        To                : 'To',
        Type              : 'Type',
        Lag               : 'Lag',
        'Edit dependency' : 'Edit dependency',
        Save              : 'Save',
        Delete            : 'Delete',
        Cancel            : 'Cancel',
        StartToStart      : 'Start to Start',
        StartToEnd        : 'Start to End',
        EndToStart        : 'End to Start',
        EndToEnd          : 'End to End'
    },

    EventDrag : {
        eventOverlapsExisting : 'Tapahtuma päällekkäinen tämän resurssin olemassa olevan tapahtuman kanssa',
        noDropOutsideTimeline : 'Tapahtumaa ei saa pudottaa kokonaan aikajanan ulkopuolelle'
    },

    Scheduler : {
        'Delete event' : 'Ta bort bokning'
    },

    HeaderContextMenu : {
        pickZoomLevel   : 'Välj zoomnivå',
        activeDateRange : 'Aktivt datumintervall',
        startText       : 'Start datum',
        endText         : 'Slut datum',
        todayText       : 'I dag'
    },

    EventFilter : {
        filterEvents : 'Filtrera händelser',
        byName       : 'Med namn'
    },

    TimeRanges : {
        showCurrentTimeLine : 'Visa aktuell tidslinje'
    },

    PresetManager : {
        minuteAndHour : {
            topDateFormat : 'ddd, hh/DD'
        }
    },

    RecurrenceLegend : {
        ' and '                         : ' and ',
        // frequency patterns
        'Daily'                         : 'Daily',

        // Weekly on Sunday
        // Weekly on Sun, Mon and Tue
        'Weekly on {1}'                 : 'Weekly on {1}',

        // Monthly on 16
        // Monthly on the last weekday
        'Monthly on {1}'                : 'Monthly on {1}',

        // Yearly on 16 of January
        // Yearly on the last weekday of January and February
        'Yearly on {1} of {2}'          : 'Yearly on {1} of {2}',

        // Every 11 days
        'Every {0} days'                : 'Every {0} days',

        // Every 2 weeks on Sunday
        // Every 2 weeks on Sun, Mon and Tue
        'Every {0} weeks on {1}'        : 'Every {0} weeks on {1}',

        // Every 2 months on 16
        // Every 2 months on the last weekday
        'Every {0} months on {1}'       : 'Every {0} months on {1}',

        // Every 2 years on 16 of January
        // Every 2 years on the last weekday of January and February
        'Every {0} years on {1} of {2}' : 'Every {0} years on {1} of {2}',

        // day position translations
        'position1'                     : 'the first',
        'position2'                     : 'the second',
        'position3'                     : 'the third',
        'position4'                     : 'the fourth',
        'position5'                     : 'the fifth',
        'position-1'                    : 'the last',
        // day options
        'day'                           : 'day',
        'weekday'                       : 'weekday',
        'weekend day'                   : 'weekend day',
        // {0} - day position info ("the last"/"the first"/...)
        // {1} - day info ("Sunday"/"Monday"/.../"day"/"weekday"/"weekend day")
        // For example:
        //  "the last Sunday"
        //  "the first weekday"
        //  "the second weekend day"
        'daysFormat'                    : '{0} {1}'
    }
};

// cannot use Object.assign above in IE11. also dont want to have polyfill in locale
for (let i in gridLocale) {
    locale[i] = gridLocale[i];
}

export default locale;

LocaleManager.registerLocale('Fi', { desc : 'Soumi', locale : locale });
