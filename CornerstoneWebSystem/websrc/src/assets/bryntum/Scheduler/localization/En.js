import LocaleManager from '../../Core/localization/LocaleManager.js';
import gridLocale from '../../Grid/localization/En.js';

// extends locale from grid
//const locale = Object.assign(gridLocale, {
const locale = {

    SchedulerCommon : {
        // SS              : 'SS',
        // SF              : 'SF',
        // FS              : 'FS',
        // FF              : 'FF',
        // StartToStart    : 'Start-to-Start',
        // StartToEnd      : 'Start-to-End',
        // EndToStart      : 'End-to-Start',
        // EndToEnd        : 'End-to-End',
        // dependencyTypes : [
        //     'SS',
        //     'SF',
        //     'FS',
        //     'FF'
        // ],
        // dependencyTypesLong : [
        //     'Start-to-Start',
        //     'Start-to-End',
        //     'End-to-Start',
        //     'End-to-End'
        // ]
    },

    ExcelExporter : {
        'No resource assigned' : 'No resource assigned'
    },

    ResourceInfoColumn : {
        eventCountText : function(data) {
            return data + ' event' + (data !== 1 ? 's' : '');
        }
    },

    Dependencies : {
        from     : 'From',
        to       : 'To',
        valid    : 'Valid',
        invalid  : 'Invalid',
        Checking : 'Checking…'
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

    EventEdit : {
        Name         : 'Name',
        Resource     : 'Resource',
        Start        : 'Start',
        End          : 'End',
        Save         : 'Save',
        Delete       : 'Delete',
        Cancel       : 'Cancel',
        'Edit Event' : 'Edit event',
        Repeat       : 'Repeat'
    },

    EventDrag : {
        eventOverlapsExisting : 'Event overlaps existing event for this resource',
        noDropOutsideTimeline : 'Event may not be dropped completely outside the timeline'
    },

    Scheduler : {
        'Add event'      : 'Add event',
        'Delete event'   : 'Delete event',
        'Unassign event' : 'Unassign event'
    },

    HeaderContextMenu : {
        pickZoomLevel   : 'Zoom',
        activeDateRange : 'Date range',
        startText       : 'Start date',
        endText         : 'End date',
        todayText       : 'Today'
    },

    EventFilter : {
        filterEvents : 'Filter tasks',
        byName       : 'By name'
    },

    TimeRanges : {
        showCurrentTimeLine : 'Show current timeline'
    },

    PresetManager : {
        minuteAndHour : {
            topDateFormat : 'ddd MM/DD, hA'
        },
        hourAndDay : {
            topDateFormat : 'ddd MM/DD'
        },
        weekAndDay : {
            displayDateFormat : 'hh:mm A'
        }
    },

    RecurrenceConfirmationPopup : {
        'delete-title'              : 'You’re deleting an event',
        'delete-all-message'        : 'Do you want to delete all occurrences of this event?',
        'delete-further-message'    : 'Do you want to delete this and all future occurrences of this event, or only the selected occurrence?',
        'delete-further-btn-text'   : 'Delete All Future Events',
        'delete-only-this-btn-text' : 'Delete Only This Event',

        'update-title'              : 'You’re changing a repeating event',
        'update-all-message'        : 'Do you want to change all occurrences of this event?',
        'update-further-message'    : 'Do you want to change only this occurrence of the event, or this and all future occurrences?',
        'update-further-btn-text'   : 'All Future Events',
        'update-only-this-btn-text' : 'Only This Event',

        'Yes'    : 'Yes',
        'Cancel' : 'Cancel',

        width : 600
    },

    RecurrenceLegend : {
        ' and ' : ' and ',
        // frequency patterns
        'Daily' : 'Daily',

        // Weekly on Sunday
        // Weekly on Sun, Mon and Tue
        'Weekly on {1}' : ({ days }) => `Weekly on ${days}`,

        // Monthly on 16
        // Monthly on the last weekday
        'Monthly on {1}' : ({ days }) => `Monthly on ${days}`,

        // Yearly on 16 of January
        // Yearly on the last weekday of January and February
        'Yearly on {1} of {2}' : ({ days, months }) => `Yearly on ${days} of ${months}`,

        // Every 11 days
        'Every {0} days' : ({ interval }) => `Every ${interval} days`,

        // Every 2 weeks on Sunday
        // Every 2 weeks on Sun, Mon and Tue
        'Every {0} weeks on {1}' : ({ interval, days }) => `Every ${interval} weeks on ${days}`,

        // Every 2 months on 16
        // Every 2 months on the last weekday
        'Every {0} months on {1}' : ({ interval, days }) => `Every ${interval} months on ${days}`,

        // Every 2 years on 16 of January
        // Every 2 years on the last weekday of January and February
        'Every {0} years on {1} of {2}' : ({ interval, days, months }) => `Every ${interval} years on ${days} of ${months}`,

        // day position translations
        'position1'   : 'the first',
        'position2'   : 'the second',
        'position3'   : 'the third',
        'position4'   : 'the fourth',
        'position5'   : 'the fifth',
        'position-1'  : 'the last',
        // day options
        'day'         : 'day',
        'weekday'     : 'weekday',
        'weekend day' : 'weekend day',
        // {0} - day position info ("the last"/"the first"/...)
        // {1} - day info ("Sunday"/"Monday"/.../"day"/"weekday"/"weekend day")
        // For example:
        //  "the last Sunday"
        //  "the first weekday"
        //  "the second weekend day"
        'daysFormat'  : ({ position, days }) => `${position} ${days}`
    },

    RecurrenceEditor : {
        'Repeat event'        : 'Repeat event',
        'Cancel'              : 'Cancel',
        'Save'                : 'Save',
        'Frequency'           : 'Frequency',
        'Every'               : 'Every',
        'DAILYintervalUnit'   : 'day(s)',
        'WEEKLYintervalUnit'  : 'week(s) on:',
        'MONTHLYintervalUnit' : 'month(s)',
        'YEARLYintervalUnit'  : 'year(s) in:',
        'Each'                : 'Each',
        'On the'              : 'On the',
        'End repeat'          : 'End repeat',
        'time(s)'             : 'time(s)'
    },

    RecurrenceDaysCombo : {
        'day'         : 'day',
        'weekday'     : 'weekday',
        'weekend day' : 'weekend day'
    },

    RecurrencePositionsCombo : {
        'position1'  : 'first',
        'position2'  : 'second',
        'position3'  : 'third',
        'position4'  : 'fourth',
        'position5'  : 'fifth',
        'position-1' : 'last'
    },

    RecurrenceStopConditionCombo : {
        'Never'   : 'Never',
        'After'   : 'After',
        'On date' : 'On date'
    },

    RecurrenceFrequencyCombo : {
        'Daily'   : 'Daily',
        'Weekly'  : 'Weekly',
        'Monthly' : 'Monthly',
        'Yearly'  : 'Yearly'
    },

    RecurrenceCombo : {
        'None'      : 'None',
        'Custom...' : 'Custom...'
    },
    
    //region Export
    
    ScheduleRangeCombo : {
        completeview : 'Complete schedule',
        currentview  : 'Visible schedule',
        daterange    : 'Date range',
        completedata : 'Complete schedule (for all events)'
    },
    
    SchedulerExportDialog : {
        'Schedule range' : 'Schedule range',
        'Export from'    : 'From',
        'Export to'      : 'To'
    }
    
    //endregion
};

// cannot use Object.assign above in IE11. also dont want to have polyfill in locale
for (let i in gridLocale) {
    locale[i] = gridLocale[i];
}

export default locale;

LocaleManager.registerLocale('En', { desc : 'English', locale : locale });
