import Base from '../../../../Core/Base.js';

export default class AbstractRecurrenceIterator extends Base {

    static get frequency() {
        return 'NONE';
    }

    static get MAX_OCCURRENCES_COUNT() {
        return 1000000;
    }

    /**
     * Returns Nth occurrence of a week day in the provided period of time.
     * @param  {Date} startDate Period start date.
     * @param  {Date} endDate   Period end date.
     * @param  {Integer} day    Week day (0 - Sunday, 1 - Monday, 2 - Tuesday, etc.)
     * @param  {Integer} index  Index to find.
     * @return {Date}           Returns the found date or null if there is no `index`th entry.
     * @private
     */
    static getNthDayInPeriod(startDate, endDate, day, index) {
        let result, sign, borderDate;

        if (index) {
            const dayDurationInMs = 86400000,
                weekDurationInMs  = 604800000;

            if (index > 0) {
                sign = 1;
                borderDate = startDate;
            } else {
                sign = -1;
                borderDate = endDate;
            }

            // delta between requested day and border day
            const delta = day - borderDate.getDay();

            // if the requested day goes after (before, depending on borderDate used (start/end))
            // we adjust index +/-1
            if (sign * delta < 0) {
                index += sign;
            }

            // measure "index" weeks forward (or backward) ..take delta into account
            result = new Date(borderDate.getTime() + (index - sign) * weekDurationInMs + delta * dayDurationInMs);

            // if resulting date is outside of the provided range there is no "index"-th entry
            // of the day
            if (result < startDate || result > endDate) {
                result = null;
            }
        }

        return result;
    }

    static buildDate(year, month, date) {
        const dt = new Date(year, month, date);

        if (dt.getFullYear() == year && dt.getMonth() == month && dt.getDate() == date) {
            return dt;
        }
    }

    static isValidPosition(position) {
        return Boolean(position);
    }

    static forEachDateAtPositions(dates, positions, fn, scope) {
        const datesLength = dates.length,
            processed     = {};

        for (let i = 0; i < positions.length; i++) {

            const index = positions[i];

            if (this.isValidPosition(index)) {
                const date = index > 0 ? dates[index - 1] : dates[datesLength + index];

                if (date && !processed[date.getTime()]) {

                    // remember that we've returned the date
                    processed[date.getTime()] = true;

                    // return false if it's time to stop recurring
                    if (fn.call(scope, date) === false) {
                        return false;
                    }
                }
            }
        }
    }
}
