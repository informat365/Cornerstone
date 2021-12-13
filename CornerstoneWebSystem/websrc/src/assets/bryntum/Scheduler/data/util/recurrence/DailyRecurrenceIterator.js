import AbstractRecurrenceIterator from './AbstractRecurrenceIterator.js';
import DateHelper from './../../../../Core/helper/DateHelper.js';

export default class DailyRecurrenceIterator extends AbstractRecurrenceIterator {

    static get frequency() {
        return 'DAILY';
    }

    static forEachDate(config) {
        const
            me         = this,
            { recurrence, fn, scope = me } = config,
            { timeSpan, endDate : until, interval } = recurrence,
            timeSpanStart = timeSpan.startDate;

        let startDate  = config.startDate || timeSpanStart,
            endDate    = config.endDate || until,
            count      = recurrence.count,
            counter    = 0;

        if (until && endDate && endDate > until) {
            endDate = until;
        }

        // iteration should not start before the event starts
        if (timeSpanStart > startDate) {
            startDate = timeSpanStart;
        }

        const
            delay            = startDate - timeSpanStart,
            // recurrence interval duration in ms (86400000 is a single day duration in ms)
            intervalDuration = interval * 86400000,
            delayInIntervals = Math.floor(delay / intervalDuration);

        if (!endDate && !count) {
            count = me.MAX_OCCURRENCES_COUNT;
        }

        let date = DateHelper.add(timeSpanStart, delayInIntervals, 'day');

        while (!endDate || date <= endDate) {

            counter++;

            if (date >= startDate &&
                ((endDate && date > endDate) || fn.call(scope, date, counter) === false || (count && counter >= count))
            ) {
                break;
            }

            // shift to the next day
            date = DateHelper.add(date, interval, 'day');
        }
    }
}
