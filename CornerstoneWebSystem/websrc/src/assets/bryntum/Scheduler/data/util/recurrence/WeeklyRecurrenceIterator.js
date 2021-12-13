import AbstractRecurrenceIterator from './AbstractRecurrenceIterator.js';
import DateHelper from './../../../../Core/helper/DateHelper.js';
import DayRuleEncoder from './RecurrenceDayRuleEncoder.js';

export default class WeeklyRecurrenceIterator extends AbstractRecurrenceIterator {

    static get frequency() {
        return 'WEEKLY';
    }

    static forEachDate(config) {
        const
            me         = this,
            { fn, recurrence, scope = me } = config,
            {
                timeSpan,
                interval,
                days,
                endDate : until
            } = recurrence,
            timeSpanStart = timeSpan.startDate;

        let counter    = 0,
            startDate  = config.startDate || timeSpanStart,
            endDate    = config.endDate || until,
            { count }  = recurrence,
            weekDays   = DayRuleEncoder.decode(days),
            weekStartDate, date;

        if (until && endDate && endDate > until) {
            endDate = until;
        }

        // days could be provided in any order so it's important to sort them
        if (weekDays && weekDays.length) {
            weekDays.sort((a, b) => a[0] - b[0]);
        }
        // "Days" might be skipped then we use the event start day
        else {
            weekDays = [[ timeSpanStart.getDay() ]];
        }

        // iteration should not start before the event starts
        if (timeSpanStart > startDate) {
            startDate = timeSpanStart;
        }

        // if the recurrence is limited w/ "Count"
        // we need to 1st count passed occurrences so we always start iteration from the event start date
        weekStartDate = DateHelper.getNext(count ? timeSpanStart : startDate, 'week', 0, 0);

        if (!endDate && !count) {
            count = me.MAX_OCCURRENCES_COUNT;
        }

        while (!endDate || weekStartDate <= endDate) {

            for (let i = 0; i < weekDays.length; i++) {

                date = DateHelper.copyTimeValues(DateHelper.add(weekStartDate, weekDays[i][0], 'day'), timeSpanStart);

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

            // get next week start
            weekStartDate = DateHelper.getNext(weekStartDate, 'week', interval, 0);
        }
    }

}
