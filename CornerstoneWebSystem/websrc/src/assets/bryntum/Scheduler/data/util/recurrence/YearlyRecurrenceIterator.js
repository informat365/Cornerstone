import AbstractRecurrenceIterator from './AbstractRecurrenceIterator.js';
import DateHelper from './../../../../Core/helper/DateHelper.js';
import DayRuleEncoder from './RecurrenceDayRuleEncoder.js';

export default class YearlyRecurrenceIterator extends AbstractRecurrenceIterator {

    static get frequency() {
        return 'YEARLY';
    }

    static forEachDate(config) {
        const
            me           = this,
            { fn, recurrence, scope = me } = config,
            {
                timeSpan,
                interval,
                days,
                count,
                positions,
                endDate : until
            } = recurrence,
            timeSpanStart  = timeSpan.startDate,
            weekDays       = DayRuleEncoder.decode(days),
            hasPositions   = positions && positions.length,
            processedDate  = {};

        let
            startDate      = config.startDate || timeSpanStart,
            endDate        = config.endDate || until,
            { months }     = recurrence,
            counter        = 0,
            i, date, dates, yearStartDate, yearEndDate,
            weekDayPosition;

        if (until && endDate && endDate > until) {
            endDate = until;
        }

        // iteration should not start before the event starts
        if (timeSpanStart > startDate) {
            startDate = timeSpanStart;
        }

        // if the recurrence is limited w/ "Count"
        // we need to 1st count passed occurrences so we always start iteration from the event start date
        yearStartDate = DateHelper.startOf(count ? timeSpanStart : startDate, 'year');
        yearEndDate   = new Date(DateHelper.getNext(yearStartDate, 'year', 1) - 1);

        months && months.sort((a, b) => a - b);

        // if no months provided let's use the event month
        if (!(months && months.length) && !(weekDays && weekDays.length)) {
            months = [ timeSpanStart.getMonth() + 1 ];
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

        while ((!endDate || endDate >= yearStartDate) && (!count || counter < count)) {

            dates = [];

            if (weekDays && weekDays.length) {

                weekDays.forEach(day => {
                    let weekDay = day[0],
                        from    = 1,
                        till    = 53;

                    // if position provided
                    if (day[1]) {
                        from = till = day[1];
                    }

                    for (i = from; i <= till; i++) {
                        if ((date = me.getNthDayInPeriod(yearStartDate, yearEndDate, weekDay, i))) {
                            date = DateHelper.copyTimeValues(date, timeSpanStart);

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

                        if (date >= timeSpanStart) {
                            counter++;

                            if (date >= startDate &&
                                ((endDate && date > endDate) ||
                                (fn.call(scope, date, counter) === false) ||
                                (count && counter >= count))
                            ) {
                                return;
                            }
                        }
                    }
                }

            }
            else {
                for (i = 0; i < months.length; i++) {

                    if ((date = me.buildDate(yearStartDate.getFullYear(), months[i] - 1, timeSpanStart.getDate()))) {
                        date = DateHelper.copyTimeValues(date, timeSpanStart);

                        // check if the date wasn't iterated over yet
                        if (!processedDate[date.getTime()]) {
                            processedDate[date.getTime()] = true;

                            if (hasPositions) {
                                dates.push(date);
                            }
                            else if (date >= timeSpanStart) {
                                counter++;

                                if (date >= startDate &&
                                    ((endDate && date > endDate) ||
                                    (fn.call(scope, date, counter) === false) ||
                                    (count && counter >= count))
                                ) {
                                    return;
                                }
                            }
                        }
                    }
                }
            }

            if (hasPositions && dates.length) {
                me.forEachDateAtPositions(dates, positions, date => {
                    if (date >= timeSpanStart) {
                        counter++;
                        // Ignore dates outside of the [startDate, endDate] range
                        if (date >= startDate && (!endDate || date <= endDate)) {
                            // return false if it's time to stop recurring
                            if (fn.call(scope, date, counter) === false || (count && counter >= count)) {
                                return false;
                            }
                        }
                    }
                });
            }

            // get next month start
            yearStartDate = DateHelper.getNext(yearStartDate, 'year', interval);
            yearEndDate   = new Date(DateHelper.getNext(yearStartDate, 'year', 1) - 1);
        }

    }
}
