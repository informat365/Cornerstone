import LocaleManager from '../../Core/localization/LocaleManager.js';
import gridLocale from '../../Core/localization/Cn.js';

// extends locale from grid
//const locale = Object.assign(gridLocale, {
const locale = {

    Dependencies : {
        from     : '开始',
        to       : '结束',
        valid    : '有效',
        invalid  : '无效',
        checking : '检查'
    },

    EventEdit : {
        Name         : '名称',
        Resource     : '资源',
        Start        : '开始',
        End          : '结束',
        Save         : '保存',
        Delete       : '删除',
        Cancel       : '取消',
        'Edit Event' : '编辑'
    },
    ResourceInfoColumn : {
        eventCountText : function(data) {
            return data + '条数据';
        }
    },
    DependencyEdit : {
        From              : '开始',
        To                : '结束',
        Type              : '类型',
        Lag               : 'Lag',
        'Edit dependency' : '编辑',
        Save              : '保存',
        Delete            : '删除',
        Cancel            : '取消',
        StartToStart      : '开始-开始',
        StartToEnd        : '开始-结束',
        EndToStart        : '结束-开始',
        EndToEnd          : '结束-结束'
    },

    Scheduler : {
        'Delete event' : '删除'
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
            topDateFormat : 'ddd DD.MM, HH:mm'
        },
        hourAndDay : {
            topDateFormat : 'ddd DD.MM'
        },
        weekAndDay : {
            displayDateFormat : 'HH:mm'
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

LocaleManager.registerLocale('Cn', { desc : '中文', locale : locale });
