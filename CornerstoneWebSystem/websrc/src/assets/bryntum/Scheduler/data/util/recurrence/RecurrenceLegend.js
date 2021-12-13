import Localizable from '../../../../Core/localization/Localizable.js';
import DayRuleEncoder from './RecurrenceDayRuleEncoder.js';
import DateHelper from '../../../../Core/helper/DateHelper.js';

/**
 * @module Scheduler/data/util/recurrence/RecurrenceLegend
 */

/**
 * A static class allowing to get a human readable description of the provided recurrence.
 *
 * ```javascript
 * const event = new EventModel({ startDate : new Date(2018, 6, 3), endDate : new Date(2018, 6, 4) });
 * const recurrence = new RecurrenceModel({ frequency : 'WEEKLY', days : ['MO', 'TU', 'WE'] });
 * event.recurrence = recurrence;
 * // "Weekly on  Mon, Tue and Wed"
 * RecurrenceLegend.getLegend(recurrence);
 * ```
 * @mixes Core/localization/Localizable
 */
export default class RecurrenceLegend extends Localizable() {

    static get $name() {
        return 'RecurrenceLegend';
    }

    static get allDaysValue() {
        return 'SU,MO,TU,WE,TH,FR,SA';
    }

    static get workingDaysValue() {
        return 'MO,TU,WE,TH,FR';
    }

    static get nonWorkingDaysValue() {
        return 'SU,SA';
    }

    /**
     * Returns the provided recurrence description. The recurrence might be assigned to a timespan model,
     * in this case the timespan start date should be provided in the second argument.
     * @param  {Scheduler.model.RecurrenceModel} recurrence         Recurrence model.
     * @param  {Date}                           [timeSpanStartDate] The recurring timespan start date. Can be omitted if the recurrence is assigned to a timespan model
     *                                                              (and the timespan has {@link Scheduler.model.TimeSpan#field-startDate} filled).
     *                                                              Then start date will be retrieved from the model.
     * @return {String}                                             The recurrence description.
     */
    static getLegend(recurrence, timeSpanStartDate) {
        const
            me = this,
            { timeSpan, interval, days, monthDays, months, positions } = recurrence,
            startDate = timeSpanStartDate || timeSpan.startDate,
            tplData   = { interval };

        let fn;

        switch (recurrence.frequency) {
            case 'DAILY':
                return interval == 1 ? me.L('Daily') : me.L('Every {0} days', tplData);

            case 'WEEKLY':
                if (days && days.length) {
                    tplData.days = me.getDaysLegend(days);
                }
                else if (startDate) {
                    tplData.days = DateHelper.getDayName(startDate.getDay());
                }

                return me.L(interval == 1 ? 'Weekly on {1}' : 'Every {0} weeks on {1}', tplData);

            case 'MONTHLY':
                if (days && days.length && positions && positions.length) {
                    tplData.days = me.getDaysLegend(days, positions);
                }
                else if (monthDays && monthDays.length) {
                    // sort dates to output in a proper order
                    monthDays.sort((a, b) => a - b);

                    tplData.days = me.arrayToText(monthDays);
                }
                else if (startDate) {
                    tplData.days = startDate.getDate();
                }

                return me.L(interval == 1 ? 'Monthly on {1}' : 'Every {0} months on {1}', tplData);

            case 'YEARLY':

                if (days && days.length && positions && positions.length) {
                    tplData.days = me.getDaysLegend(days, positions);
                }
                else {
                    tplData.days = startDate.getDate();
                }

                if (months && months.length) {
                    // sort months to output in a proper order
                    months.sort((a, b) => a - b);

                    if (months.length > 2) {
                        fn = month => DateHelper.getMonthShortName(month - 1);
                    }
                    else {
                        fn = month => DateHelper.getMonthName(month - 1);
                    }

                    tplData.months = me.arrayToText(months, fn);
                }
                else {
                    tplData.months = DateHelper.getMonthName(startDate.getMonth());
                }

                return me.L(interval == 1 ? 'Yearly on {1} of {2}' : 'Every {0} years on {1} of {2}', tplData);
        }
    }

    static getDaysLegend(days, positions) {
        const me = this;

        let tplData = { position : '' },
            fn;

        if (positions && positions.length) {
            // the following lines are added to satisfy the 904_unused localization test
            // to let it know that these locales are used:
            // me.L('position1')
            // me.L('position2')
            // me.L('position3')
            // me.L('position4')
            // me.L('position5')
            // me.L('position-1')
            tplData.position = me.arrayToText(positions, position => me.L(`position${position}`));
        }

        if (days.length) {
            days.sort((a, b) => DayRuleEncoder.decodeDay(a)[0] - DayRuleEncoder.decodeDay(b)[0]);

            switch (days.join(',')) {
                case me.allDaysValue :
                    tplData.days = me.L('day');
                    break;

                case me.workingDaysValue :
                    tplData.days = me.L('weekday');
                    break;

                case me.nonWorkingDaysValue :
                    tplData.days = me.L('weekend day');
                    break;

                default :
                    if (days.length > 2) {
                        fn = day => DateHelper.getDayShortName(DayRuleEncoder.decodeDay(day)[0]);
                    }
                    else {
                        fn = day => DateHelper.getDayName(DayRuleEncoder.decodeDay(day)[0]);
                    }

                    tplData.days = me.arrayToText(days, fn);
            }
        }

        return me.L('daysFormat', tplData);
    }

    // Converts array of items to a human readable list.
    // For example: [1,2,3,4]
    // to: "1, 2, 3 and 4"
    static arrayToText(array, fn) {
        if (fn) {
            array = array.map(fn);
        }

        return array.join(', ').replace(/,(?=[^,]*$)/, this.L(' and '));
    }

}
