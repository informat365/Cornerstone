import Base from '../../../../Core/Base.js';

const dayParseRegExp = /^([+-]?[0-9])?(SU|MO|TU|WE|TH|FR|SA)$/;

const days = ['SU', 'MO', 'TU', 'WE', 'TH', 'FR', 'SA'];

export default class RecurrenceDayRuleEncoder extends Base {

    static decodeDay(rawDay) {
        let parsedDay,
            result;

        if ((parsedDay = dayParseRegExp.exec(rawDay))) {

            result = [days.indexOf(parsedDay[2])];

            // optional position number
            if (parsedDay[1]) {
                parsedDay[1] = parseInt(parsedDay[1], 10);
                result.push(parsedDay[1]);
            }
        }

        return result;
    }

    static encodeDay(day) {
        let position;

        // support decodeDay() result format
        if (Array.isArray(day)) {
            [day, position] = day;
        }

        return (position ? position.toString() : '') + days[day];
    }

    // Turns days values provided as an array of strings (like [`-1MO`, `SU`, `+3FR`])
    // into an array of [ dayIndex, position ] elements, where:
    //
    // - `dayIndex` - zero-based week day index value (0 - Sunday, 1 - Monday, 2 - Tuesday, etc.)
    // - `position` - (optional) 1-based position of the day (integer value (can be both positive and negative))
    static decode(rawDays) {
        let result = [],
            parsedDay;

        if (rawDays) {
            for (let i = 0; i < rawDays.length; i++) {
                if ((parsedDay = this.decodeDay(rawDays[i]))) {
                    result.push(parsedDay);
                }
            }
        }

        return result;
    }

    static encode(days) {
        let result = [],
            day;

        if (days) {
            for (let i = 0; i < days.length; i++) {
                if ((day = this.encodeDay(days[i]))) {
                    result.push(day);
                }
            }
        }

        return result;
    }
};
