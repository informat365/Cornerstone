import AbstractRecurrenceIterator from './AbstractRecurrenceIterator.js';
import DateHelper from './../../../../Core/helper/DateHelper.js';
import DayRuleEncoder from './RecurrenceDayRuleEncoder.js';

export default class MonthlyRecurrenceIterator extends AbstractRecurrenceIterator {

    static get frequency() {
        return 'MONTHLY';
    }

    static getNthDayOfMonth(date, dayNum) {
        const daysInMonth = DateHelper.daysInMonth(date);
        let result = null;

        if (dayNum && Math.abs(dayNum) <= daysInMonth) {
            result = new Date(date.getFullYear(), date.getMonth(), dayNum < 0 ? daysInMonth + dayNum + 1 : dayNum);
        }

        return result;
    }

    static isValidPosition(position) {
        return position && Math.abs(position) > 0 && Math.abs(position) <= 31;
    }

    static forEachDate(config) {
        const
            me             = this,
            { fn, recurrence, scope = me } = config,
            {
                timeSpan,
                interval,
                days,
                endDate : until,
                count,
                positions
            } = recurrence,
            eventStart     = timeSpan.startDate,
            weekDays       = DayRuleEncoder.decode(days),
            hasPositions   = positions && positions.length,
            processedDate  = {};

        let
            startDate      = config.startDate || eventStart,
            endDate        = config.endDate || until,
            { monthDays }  = recurrence,
            counter        = 0,
            weekDayPosition,
            monthStartDate, monthEndDate,
            dates, date, i;

        if (until && endDate && endDate > until) {
            endDate = until;
        }

        // iteration should not start before the event starts
        if (eventStart > startDate) {
            startDate = eventStart;
        }

        // if the recurrence is limited w/ "Count"
        // we need to 1st count passed occurrences so we always start iteration from the event start date
        monthStartDate = DateHelper.startOf(count ? eventStart : startDate, 'month');
        monthEndDate   = new Date(DateHelper.getNext(monthStartDate, 'month', 1) - 1);

        // if no month days nor week days are provided let's use event start date month day
        if (!(monthDays && monthDays.length) && !(weekDays && weekDays.length)) {
            monthDays = [eventStart.getDate()];
        }

        if (weekDays && weekDays.length) {
            // Collect hash of positions indexed by week days
            weekDays.forEach(day => {
                if (day[1]) {
                    weekDayPosition         = weekDayPosition || {};
                    weekDayPosition[day[0]] = day[1];
                }
            });
        }

        while ((!endDate || endDate >= monthStartDate) && (!count || counter < count)) {

            dates = [];

            if (weekDays && weekDays.length) {

                weekDays.forEach(day => {
                    const weekDay = day[0];

                    let from    = 1,
                        till    = 53;

                    // if position provided
                    if (day[1]) {
                        from = till = day[1];
                    }

                    for (i = from; i <= till; i++) {
                        if ((date = me.getNthDayInPeriod(monthStartDate, monthEndDate, weekDay, i))) {
                            date = DateHelper.copyTimeValues(date, eventStart);

                            if (!processedDate[date.getTime()]) {
                                // remember we processed the date
                                processedDate[date.getTime()] = true;

                                dates.push(date);
                            }
                        }
                    }
                });

                dates.sort((a, b) => a - b);

                if (!hasPositions) {
                    for (i = 0; i < dates.length; i++) {
                        date = dates[i];

                        if (date >= eventStart) {
                            counter++;

                            if (date >= startDate &&
                                ((endDate && date > endDate) || (fn.call(scope, date, counter) === false) || (count && counter >= count))
                            ) {
                                return false;
                            }
                        }
                    }
                }

            }
            else {
                const sortedMonthDates = [];

                for (i = 0; i < monthDays.length; i++) {
                    // check if the date wasn't iterated over yet
                    if ((date = me.getNthDayOfMonth(monthStartDate, monthDays[i])) && !processedDate[date.getTime()]) {
                        processedDate[date.getTime()] = true;
                        sortedMonthDates.push(date);
                    }
                }

                // it's important to sort the dates to iterate over them in the proper order
                sortedMonthDates.sort((a, b) => a - b);

                for (i = 0; i < sortedMonthDates.length; i++) {
                    date = DateHelper.copyTimeValues(sortedMonthDates[i], eventStart);

                    if (hasPositions) {
                        dates.push(date);
                    }
                    else if (date >= eventStart) {
                        counter++;

                        if (date >= startDate &&
                            // eslint-disable-next-line no-labels
                            ((endDate && date > endDate) || (fn.call(scope, date, counter) === false) || (count && counter >= count))
                        ) {
                            return;
                        }
                    }
                }
            }

            if (hasPositions && dates.length) {
                me.forEachDateAtPositions(dates, positions, date => {
                    if (date >= eventStart) {
                        counter++;
                        // Ignore dates outside of the [startDate, endDate] range
                        if (date >= startDate && (!endDate || date <= endDate) &&
                            // return false if it's time to stop recurring
                            (fn.call(scope, date, counter) === false || (count && counter >= count))
                        ) {
                            return false;
                        }
                    }
                });
            }

            // get next month start
            monthStartDate = DateHelper.getNext(monthStartDate, 'month', interval);
            monthEndDate   = new Date(DateHelper.getNext(monthStartDate, 'month', 1) - 1);
        }

    }

}
