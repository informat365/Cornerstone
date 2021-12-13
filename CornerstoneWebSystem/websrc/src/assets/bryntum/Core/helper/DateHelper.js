import Localizable from '../localization/Localizable.js';
import LocaleManager from '../localization/LocaleManager.js';
import BrowserHelper from './BrowserHelper.js';

/*
Not ported:
-----------
Week of Year w 1 2 ... 52 53
wo 1st 2nd ... 52nd 53rd
ww 01 02 ... 52 53
Week Year gg 70 71 ... 29 30
gggg 1970 1971 ... 2029 2030
Week Year (ISO) GG 70 71 ... 29 30
GGGG 1970 1971 ... 2029 2030
Time Zone z or zz EST CST ... MST PST
Note: as of 1.6.0, the z/zz format tokens have been deprecated from plain moment objects. Read more about it here. However, they do work if you are using a specific time zone with the moment-timezone addon.
Z -07:00 -06:00 ... +06:00 +07:00
ZZ -0700 -0600 ... +0600 +0700
Unix Timestamp X 1360013296
Unix Millisecond Timestamp x 1360013296123
*/

const enOrdinalSuffix = number => number + ({ '1' : 'st', '2' : 'nd', '3' : 'rd' }[number[number.length - 1]] || 'th');
// Left-to-right unicode mark
// https://www.codetable.net/decimal/8206
const LTRSymbolRegexp = new RegExp('\u200E');

// These vars are set when changing locale

let locale        = 'en-US',
    ordinalSuffix = enOrdinalSuffix,
    // Used to cache used formats, to not have to parse format string each time
    formatCache   = {},
    intlFormatterCache = {},
    parserCache   = {};

function useIntlFormat(name, options, date) {
    const formatter = intlFormatterCache[name] || (intlFormatterCache[name] = new Intl.DateTimeFormat(locale, options));

    let result = formatter.format(date);

    // IE11 inserts direction control characters to the start of the string
    // This breaks substring(0, 2). Skipping first character in the row for I
    // https://stackoverflow.com/questions/25574963/ies-tolocalestring-has-strange-characters-in-results
    if (BrowserHelper.isIE11) {
        result = result.replace(LTRSymbolRegexp, '');
    }

    return result;
}

const
    MS_PER_DAY                = 864e5,
    MS_PER_WEEK               = MS_PER_DAY * 7,

    formats                   = {
        // 1, 2, ... 11, 12
        'M'    : date => date.getMonth() + 1, //date.toLocaleDateString(locale, { month : 'numeric' }),
        // 1st, 2nd, 3rd, 4th, ... 11th, 12th
        'Mo'   : date => ordinalSuffix(formats['M'](date).toString()),
        // 01, 02, ...
        'MM'   : date => (date.getMonth() + 1).toString().padStart(2, '0'), //date.toLocaleDateString(locale, { month : '2-digit' }),
        // Jan, Feb, ...
        'MMM'  : date => useIntlFormat('MMM', { month : 'short' }, date),
        // January, February, ...
        'MMMM' : date => useIntlFormat('MMMM', { month : 'long' }, date),

        // 1, 2, ...
        'Q'  : date => Math.ceil((date.getMonth() + 1) / 3),
        // 1st, 2nd, ...
        'Qo' : date => ordinalSuffix(formats['Q'](date).toString()),

        // 1, 2, ...
        'D'  : date => date.getDate(), //date.toLocaleDateString(locale, { day : 'numeric' }),
        // 1st, 2nd, ...
        'Do' : date => ordinalSuffix(formats['D'](date).toString()),
        // 01, 02, ...
        'DD' : date => date.getDate().toString().padStart(2, '0'), //date.toLocaleDateString(locale, { day : '2-digit' }),

        // 1, 2, ..., 365, 365
        'DDD' : date => Math.ceil(
            (
                new Date(date.getFullYear(), date.getMonth(), date.getDate(), 12, 0, 0) -
                new Date(date.getFullYear(), 0, 0, 12, 0, 0)
            ) / MS_PER_DAY),
        // 1st, 2nd, ...
        'DDDo' : date => ordinalSuffix(formats['DDD'](date).toString()),
        // 001, 002, ...
        'DDDD' : date => formats['DDD'](date).toString().padStart(3, '0'),

        // 0, 1, ..., 6
        'd'    : date => date.getDay(),
        // 0th, 1st, ...
        'do'   : date => ordinalSuffix(date.getDay().toString()),
        // S, M, ...
        'd1'   : date => formats['ddd'](date).substring(0, 1),
        // Su, Mo, ...
        'dd'   : date => formats['ddd'](date).substring(0, 2),
        // Sun, Mon, ...
        'ddd'  : date => useIntlFormat('ddd', { weekday : 'short' }, date),
        // Sunday, Monday, ...
        'dddd' : date => useIntlFormat('dddd', { weekday : 'long' }, date),

        'e' : date => date.getDay(),
        'E' : date => date.getDay() + 1,

        // ISO week, 1, 2, ...
        'W' : date => {
            // Adapted from https://www.epochconverter.com/weeknumbers
            const target = new Date(date.valueOf());
            target.setDate(target.getDate() - (date.getDay() + 6) % 7 + 3);
            const firstThursday = target.valueOf();
            target.setMonth(0, 1);
            if (target.getDay() !== 4) {
                target.setMonth(0, 1 + ((4 - target.getDay()) + 7) % 7);
            }
            return 1 + Math.ceil((firstThursday - target) / MS_PER_WEEK);
        },
        'Wo' : date => ordinalSuffix(formats['W'](date).toString()),
        'WW' : date => formats['W'](date).toString().padStart(2, '0'),

        // 1979, 2018
        'Y'    : date => date.getFullYear(), //date.toLocaleDateString(locale, { year : 'numeric' }),
        // 79, 18
        'YY'   : date => date.getFullYear() % 100, //date.toLocaleDateString(locale, { year : '2-digit' }),
        // 1979, 2018
        'YYYY' : date => date.getFullYear(), //date.toLocaleDateString(locale, { year : 'numeric' }),

        // AM, PM
        'A' : date => date.getHours() < 12 ? 'AM' : 'PM',
        'a' : date => date.getHours() < 12 ? 'am' : 'pm',

        // 0, 1, ... 23
        'H'  : date => date.getHours(),
        // 00, 01, ...
        'HH' : date => date.getHours().toString().padStart(2, '0'),
        // 1, 2, ... 12
        'h'  : date => (date.getHours() % 12) || 12,
        // 01, 02, ...
        'hh' : date => formats['h'](date).toString().padStart(2, '0'),
        // 1, 2, ... 24
        'k'  : date => date.getHours() + 1,
        // 01, 02, ...
        'kk' : date => formats['k'](date).toString().padStart(2, '0'),
        // Locale specific (0 -> 24 or 1 AM -> 12 PM)
        'K'  : date => useIntlFormat('K', { hour : 'numeric' }, date),
        // Locale specific (00 -> 24 or 1 AM -> 12 PM)
        'KK' : date => useIntlFormat('KK', { hour : '2-digit' }, date),

        // 0, 1, ... 59
        'm'  : date => date.getMinutes(), //date.toLocaleTimeString(locale, { minute : 'numeric' }),
        // 00, 01, ...
        'mm' : date => formats['m'](date).toString().padStart(2, '0'),

        // 0, 1, ... 59
        's'  : date => date.getSeconds(), //date.toLocaleTimeString(locale, { second : 'numeric' }),
        // 00, 01, ...
        'ss' : date => formats['s'](date).toString().padStart(2, '0'),

        // 0, 1, ... 9
        'S'   : date => date.getMilliseconds().toString().substr(0, 1),
        // 00, 01, ... 99
        'SS'  : date => date.getMilliseconds().toString().substr(0, 2).padEnd(2, '0'),
        // 000, 001, ... 999
        'SSS' : date => date.getMilliseconds().toString().substr(0, 3).padEnd(3, '0'),

        'z'  : date => useIntlFormat('z', { timeZoneName : 'short' }, date),
        'zz' : date => useIntlFormat('zz', { timeZoneName : 'long' }, date),

        'LT'  : date => useIntlFormat('LT', { hour : '2-digit', minute : '2-digit' }, date),
        'LTS' : date => useIntlFormat('LTS', { hour : '2-digit', minute : '2-digit', second : '2-digit' }, date),
        'L'   : date => useIntlFormat('L', { year : 'numeric', month : '2-digit', day : '2-digit' }, date),
        'l'   : date => useIntlFormat('l', { year : 'numeric', month : 'numeric', day : 'numeric' }, date),
        'LL'  : date => useIntlFormat('LL', { year : 'numeric', month : 'long', day : 'numeric' }, date),
        'll'  : date => useIntlFormat('ll', { year : 'numeric', month : 'short', day : 'numeric' }, date),
        'LLL' : date => useIntlFormat('LLL', {
            year   : 'numeric',
            month  : 'long',
            day    : 'numeric',
            hour   : 'numeric',
            minute : '2-digit'
        }, date),
        'lll' : date => useIntlFormat('lll', {
            year   : 'numeric',
            month  : 'short',
            day    : 'numeric',
            hour   : 'numeric',
            minute : '2-digit'
        }, date),
        'LLLL' : date => useIntlFormat('LLLL', {
            year    : 'numeric',
            month   : 'long',
            day     : 'numeric',
            hour    : 'numeric',
            minute  : '2-digit',
            weekday : 'long'
        }, date),
        'llll' : date => useIntlFormat('llll', {
            year    : 'numeric',
            month   : 'short',
            day     : 'numeric',
            hour    : 'numeric',
            minute  : '2-digit',
            weekday : 'short'
        }, date)
    },
    // Want longest keys first, to not stop match at L of LTS etc.
    formatKeys                = Object.keys(formats).sort((a, b) => b.length - a.length),
    formatRegexp              = `^(?:${formatKeys.join('|')})`,

    // return empty object, meaning value cannot be processed to a valuable date part
    emptyFn                   = () => ({}),
    parsers                   = {
        'YYYY' : str => ({ year : parseInt(str) }),
        'Y'    : str => ({ year : parseInt(str) }),
        'YY'   : str => {
            const year = parseInt(str);
            return { year : year + (year > 1968 ? 1900 : 2000) };
        },
        'MM'   : str => ({ month : parseInt(str) - 1 }),
        'Mo'   : str => ({ month : parseInt(str) - 1 }),
        'DD'   : str => ({ date : parseInt(str) }),
        'M'    : str => ({ month : parseInt(str) - 1 }),
        'D'    : str => ({ date : parseInt(str) }),
        'Do'   : str => ({ date : parseInt(str) }),
        'DDD'  : emptyFn,
        'MMM'  : emptyFn,
        'MMMM' : emptyFn,
        'DDDo' : emptyFn,
        'DDDD' : emptyFn,
        'd'    : emptyFn,
        'do'   : emptyFn,
        'd1'   : emptyFn,
        'dd'   : emptyFn,
        'ddd'  : emptyFn,
        'dddd' : emptyFn,
        'Q'    : emptyFn,
        'Qo'   : emptyFn,
        'W'    : emptyFn,
        'Wo'   : emptyFn,
        'WW'   : emptyFn,
        'e'    : emptyFn,
        'E'    : emptyFn,
        'HH'   : str => ({ hours : parseInt(str) }),
        'hh'   : str => ({ hours : parseInt(str) }),
        'mm'   : str => ({ minutes : parseInt(str) }),
        'H'    : str => ({ hours : parseInt(str) }),
        'm'    : str => ({ minutes : parseInt(str) }),
        'ss'   : str => ({ seconds : parseInt(str) }),
        's'    : str => ({ seconds : parseInt(str) }),
        'S'    : str => ({ milliseconds : parseInt(str) }),
        'SS'   : str => ({ milliseconds : parseInt(str) }),
        'SSS'  : str => ({ milliseconds : parseInt(str) }),

        'A' : str => ({ amPm : str.toLowerCase() }),
        'a' : str => ({ amPm : str.toLowerCase() }),

        'L'  : 'MM/DD/YYYY',
        'LT' : 'HH:mm A',

        // Can either be Z (=UTC, 0) or +-HH:MM
        'Z' : str => {
            if (!str) {
                return {};
            }

            let timeZone = 0;
            // If string being parsed is more "detailed" than the format specified we can have more chars left,
            // thus check the last (for example HH:mmZ with input HH:mm:ssZ -> ssZ)
            if (!str.endsWith('Z')) {
                const matches = timeZoneRegEx.exec(str);

                // If timezone regexp matches, sting has time zone offset like '+02:00'
                if (matches) {
                    const
                        sign    = matches[1] === '+' ? 1 : -1,
                        hours   = parseInt(matches[2]) || 0,
                        minutes = parseInt(matches[3]) || 0;

                    timeZone = sign * (hours * 60 + minutes);
                }
                // otherwise we just return current time zone, because there's a Z key in the input
                else {
                    timeZone = -1 * new Date().getTimezoneOffset();
                }
            }
            return { timeZone };
        }
    },
    parserKeys                = Object.keys(parsers).sort((a, b) => b.length - a.length),
    parserRegexp              = new RegExp(`(${parserKeys.join('|')})`),
    // Following regexp includes all formats that should be handled by Date class
    localeStrRegExp           = new RegExp('(l|LL|ll|LLL|lll|LLLL|llll)'),
    //    ISODateRegExp             = new RegExp('YYYY-MM-DD[T ]HH:mm:ss(.s+)?Z'),

    // Some validConversions are negative to show that it's not an exact conversion, just an estimate.
    validConversions          = {
        // The units below assume:
        // 30 days in a month, 91 days for a quarter and 365 for a year
        // 52 weeks per year, 4 per month, 13 per quarter
        'year' : {
            year        : 1,
            quarter     : 4,
            month       : 12,
            week        : 52,
            day         : 365,
            hour        : 24 * 365,
            minute      : 1440 * 365,
            second      : 86400 * 365,
            millisecond : 86400000 * 365
        },
        'quarter' : {
            year        : 1 / 4,
            quarter     : 1,
            month       : 3,
            week        : 4,
            day         : 91,
            hour        : 24 * 91,
            minute      : 1440 * 91,
            second      : 86400 * 91,
            millisecond : 86400000 * 91
        },
        'month' : {
            year        : 1 / 12,
            quarter     : 1 / 3,
            month       : 1,
            week        : 4,
            day         : -30,
            hour        : -24 * 30,
            minute      : -1440 * 30,
            second      : -86400 * 30,
            millisecond : -86400000 * 30
        },
        'week' : {
            year        : -1 / 52,
            quarter     : -1 / 13,
            month       : -1 / 4,
            day         : 7,
            hour        : 168,
            minute      : 10080,
            second      : 604800,
            millisecond : 604800000
        },
        'day' : {
            year        : -1 / 365,
            quarter     : -1 / 91,
            month       : -1 / 30,
            week        : 1 / 7,
            hour        : 24,
            minute      : 1440,
            second      : 86400,
            millisecond : 86400000
        },
        'hour' : {
            year        : -1 / (365 * 24),
            quarter     : -1 / (91 * 24),
            month       : -1 / (30 * 24),
            week        : 1 / 168,
            day         : 1 / 24,
            minute      : 60,
            second      : 3600,
            millisecond : 3600000
        },
        'minute' : {
            year        : -1 / (365 * 1440),
            quarter     : -1 / (91 * 1440),
            month       : -1 / (30 * 1440),
            week        : 1 / 10080,
            day         : 1 / 1440,
            hour        : 1 / 60,
            second      : 60,
            millisecond : 60000
        },
        'second' : {
            year        : -1 / (365 * 86400),
            quarter     : -1 / (91 * 86400),
            month       : -1 / (30 * 86400),
            week        : 1 / 604800,
            day         : 1 / 86400,
            hour        : 1 / 3600,
            minute      : 1 / 60,
            millisecond : 1000
        },
        'millisecond' : {
            year    : -1 / (365 * 86400000),
            quarter : -1 / (91 * 86400000),
            month   : -1 / (30 * 86400000),
            week    : 1 / 604800000,
            day     : 1 / 86400000,
            hour    : 1 / 3600000,
            minute  : 1 / 60000,
            second  : 1 / 1000
        }
    },

    normalizedUnits           = {
        'ms'           : 'millisecond',
        'milliseconds' : 'millisecond',
        's'            : 'second',
        'seconds'      : 'second',
        'm'            : 'minute',
        'minutes'      : 'minute',
        'h'            : 'hour',
        'hours'        : 'hour',
        'd'            : 'day',
        'days'         : 'day',
        'w'            : 'week',
        'weeks'        : 'week',
        'M'            : 'month',
        'months'       : 'month',
        'q'            : 'quarter',
        'quarters'     : 'quarter',
        'y'            : 'year',
        'years'        : 'year'

    },

    withDecimalsDurationRegex = /^\s*([-+]?\d+(?:[.,]\d*)?|[-+]?(?:[.,]\d+))\s*([^\s]+)?/i,
    noDecimalsDurationRegex   = /^\s*([-+]?\d+)(?![.,])\s*([^\s]+)?/i,
    canonicalUnitNames        = [
        'millisecond',
        'second',
        'minute',
        'hour',
        'day',
        'week',
        'month',
        'quarter',
        'year'
    ],
    deltaUnits = [
        'year',
        'month',
        'week',
        'day',
        'hour',
        'minute',
        'second',
        'millisecond'
    ],
    // Used when creating a date from an object, to fill in any blanks
    dateProperties            = [
        'milliseconds',
        'seconds',
        'minutes',
        'hours',
        'date',
        'month',
        'year'
    ],
    // TODO: Should we provide special number parsing?
    parseNumber               = (n) => {
        const result = parseFloat(n);
        return isNaN(result) ? null : result;
    },
    timeZoneRegEx             = /([+-])(\d\d):*(\d\d)*$/,
    unitMagnitudes = {
        millisecond : 0,
        second      : 1,
        minute      : 2,
        hour        : 3,
        day         : 4,
        week        : 5,
        month       : 6,
        quarter     : 7,
        year        : 8
    };

export { unitMagnitudes };

/**
 * @module Core/helper/DateHelper
 */

/**
 * Helps with date manipulation, comparison, parsing, formatting etc.
 *
 * ## Parsing strings
 * Use `DateHelper.parse()` to parse strings into dates. It accepts a date string and a format specifier.
 * The format specifier is string built up using the following tokens:
 *
 * | Unit        | Token | Description                |
 * |-------------|-------|----------------------------|
 * | Year        | YYYY  | 2018                       |
 * |             | YY    | < 68 -> 2000, > 68 -> 1900 |
 * | Month       | MM    | 01 - 12                    |
 * | Date        | DD    | 01 - 31                    |
 * | Hour        | HH    | 00 - 23 or 1 - 12          |
 * | Minute      | mm    | 00 - 59                    |
 * | Second      | ss    | 00 - 59                    |
 * | Millisecond | S     | 0 - 9                      |
 * |             | SS    | 00 - 99                    |
 * |             | SSS   | 000 - 999                  |
 * | AM/PM       | A     | AM or PM                   |
 * |             | a     | am or pm                   |
 * | TimeZone    | Z     | Z for UTC or +-HH:mm       |
 * | Predefined  | L     | Long date, MM/DD/YYYY      |
 * |             | LT    | Long time, HH:mm A         |
 *
 * For example:
 * ```
 * DateHelper.parse('2018-11-06', 'YYYY-MM-DD');
 * DateHelper.parse('13:14', 'HH:mm');
 * DateHelper.parse('6/11/18', 'DD/MM/YY');
 * ```
 *
 * ## Formatting dates
 * Use `DateHelper.format()` to create a string from a date using a format specifier. The format specifier is similar to
 * that used when parsing strings. It can use the following tokens (input used for output below is
 * `new Date(2018,8,9,18,7,8,145)`):
 *
 * | Unit                  | Token | Description & output                  |
 * |-----------------------|-------|---------------------------------------|
 * | Year                  | YYYY  | 2018                                  |
 * |                       | YY    | 18                                    |
 * |                       | Y     | 2018                                  |
 * | Quarter               | Q     | 3                                     |
 * |                       | Qo    | 3rd                                   |
 * | Month                 | MMMM  | September                             |
 * |                       | MMM   | Sep                                   |
 * |                       | MM    | 09                                    |
 * |                       | Mo    | 9th                                   |
 * |                       | M     | 9                                     |
 * | Week (iso)            | WW    | 36 (2 digit)                          |
 * |                       | Wo    | 36th                                  |
 * |                       | W     | 36                                    |
 * | Date                  | DDDD  | Day of year, 3 digits                 |
 * |                       | DDDo  | Day of year, ordinal                  |
 * |                       | DDD   | Day of year                           |
 * |                       | DD    | 09                                    |
 * |                       | Do    | 9th                                   |
 * |                       | D     | 9                                     |
 * | Weekday               | dddd  | Sunday                                |
 * |                       | ddd   | Sun                                   |
 * |                       | dd    | Su                                    |
 * |                       | do    | 0th                                   |
 * |                       | d     | 0                                     |
 * | Hour                  | HH    | 18 (00 - 23)                          |
 * |                       | H     | 18 (0 - 23)                           |
 * |                       | hh    | 06 (00 - 12)                          |
 * |                       | h     | 6 (0 - 12)                            |
 * |                       | KK    | 19 (01 - 24)                          |
 * |                       | K     | 19 (1 - 24)                           |
 * |                       | kk    | 06 or 18, locale determines           |
 * |                       | k     | 6 or 18, locale determines            |
 * | Minute                | mm    | 07                                    |
 * |                       | m     | 7                                     |
 * | Second                | ss    | 08                                    |
 * |                       | s     | 8                                     |
 * | Millisecond           | S     | 1                                     |
 * |                       | SS    | 14                                    |
 * |                       | SSS   | 145                                   |
 * | AM/PM                 | A     | AM or PM                              |
 * |                       | a     | am or pm                              |
 * | Predefined            | LT    | H: 2-digit (2d), m: 2d                |
 * | (uses browser locale) | LTS   | H: 2d, m: 2d, s : 2d                  |
 * |                       | L     | Y: numeric (n), M : 2d, D : 2d        |
 * |                       | l     | Y: n, M : n, D : n                    |
 * |                       | LL    | Y: n, M : long (l), D : n             |
 * |                       | ll    | Y: n, M : short (s), D : n            |
 * |                       | LLL   | Y: n, M : l, D : n, H: n, m: 2d       |
 * |                       | lll   | Y: n, M : s, D : n, H: n, m: 2d       |
 * |                       | LLLL  | Y: n, M : l, D : n, H: n, m: 2d, d: l |
 * |                       | llll  | Y: n, M : s, D : n, H: n, m: 2d, d: s |
 *
 * For example:
 *
 * ```javascript
 * DateHelper.format(new Date(2018,10,6), 'YYYY-MM-DD'); // 2018-11-06
 * DateHelper.format(new Date(2018,10,6), 'M/D/YY'); // 11/6/18
 * ```
 *
 * Arbitrary text can be embedded in the format string by wrapping it with {}:
 *
 * ```javascript
 * DateHelper.format(new Date(2019, 7, 16), '{It is }dddd{, yay!}') -> It is Friday, yay!
 * ```
 *
 * ## Unit names
 * Many DateHelper functions (for example add, as, set) accepts a unit among their params. The following units are
 * available:
 *
 * | Unit        | Aliases                       |
 * |-------------|-------------------------------|
 * | millisecond | millisecond, milliseconds, ms |
 * | second      | second, seconds, s            |
 * | minute      | minute, minutes, m            |
 * | hour        | hour, hours, h                |
 * | day         | day, days, d                  |
 * | week        | week, weeks, w                |
 * | month       | month, months, M              |
 * | quarter     | quarter, quarters, q          |
 * | year        | year, years, y                |
 *
 * For example:
 * ```javascript
 * DateHelper.add(date, 2, 'days');
 * DateHelper.as('hour', 7200, 'seconds');
 * ```
 */
export default class DateHelper extends Localizable() {
    //region Parse & format
    /**
     * Get/set the default format used by `format()` and `parse()`. Defaults to `'YYYY-MM-DDTHH:mm:ssZ'`
     * (~ISO 8601 Date and time, `'1962-06-17T09:21:34Z'`).
     * @member {String}
     */
    static set defaultFormat(format) {
        this._defaultFormat = format;
    }

    static get defaultFormat() {
        return this._defaultFormat || 'YYYY-MM-DDTHH:mm:ssZ';
    }

    static buildParser(format) {

        // Split input format by regexp, which includes predefined patterns. Normally format would have some
        // splitters, like 'YYYY-MM-DD' or 'D/M YYYY' so output will contain matched patterns as well as splitters
        // which would serve as anchors. E.g. provided format is 'D/M!YYYY' and input is `11/6!2019` algorithm would work like:
        // 1. split format by regexp                // ['', 'D', '/', 'M', '!', 'YYYY', '']
        // 2. find splitters                        // ['/', '!']
        // 3. split input by seps, step by step     // ['11', ['6', ['2019']]]

        // Inputs like 'YYYYY' (5*Y) means 'YYYY' + 'Y', because it matches patterns from longer to shorter,
        // but if few patterns describe same unit the last one is applied, for example
        // DH.parse('20182015', 'YYYYY') equals to new Date(2015, 0, 0)

        const
            parts = format.split(parserRegexp),
            parser = [];

        // if length of the parts array is 1 - there are no regexps in the input string. thus - no parsers
        // do same if there are patterns matching locale strings (l, ll, LLLL etc.)
        // returning empty array to use new Date() as parser
        if (parts.length === 1 || localeStrRegExp.test(format)) {
            return [];
        }
        else {
            parts.reduce((prev, curr, index, array) => {

                // ignore first and last empty string
                if (index !== 0 || curr !== '') {

                    // if current element matches parser regexp store it as a parser
                    if (parserRegexp.test(curr)) {
                        const
                            localeParsers = this.L('parsers') !== 'parsers' && this.L('parsers') || {},
                            fn            = localeParsers[curr] || parsers[curr];

                        // Z should be last element in the string that matches regexp. Last array element is always either
                        // an empty string (if format ends with Z) or splitter (everything that doesn't match regexp after Z)
                        // If there is a pattern after Z, then Z index will be lower than length - 2
                        if (curr === 'Z' && index < array.length - 2) {
                            throw new Error(`Invalid format ${format} TimeZone (Z) must be last token`);
                        }

                        // If fn is a string, we found an alias (L, LLL, l etc.).
                        // Need to build parsers from mapped format and merge with existing
                        if (typeof fn === 'string') {

                            // we are going to merge nested parsers with current, some cleanup required:
                            // 1. last element is no longer last
                            // 2. need to pass last parser to the next step
                            const
                                nestedParsers = DateHelper.buildParser(fn),
                                lastItem      = nestedParsers.pop();
                            delete lastItem.last;

                            // elevate nested parsers
                            parser.push(...nestedParsers);

                            prev = lastItem;
                        }
                        else {
                            prev.pattern = curr;
                            prev.fn = parsers[curr];
                        }

                    }
                    // if it doesn't match - we've found a splitter
                    else {
                        prev.splitter = curr;
                        parser.push(prev);
                        prev = {};
                    }
                }
                else if (prev.hasOwnProperty('pattern')) {
                    parser.push(prev);
                }
                return prev;
            }, {});
        }

        parser[parser.length - 1].last = true;

        return parser;
    }

    /**
     * Returns a date created from the supplied string using the specified format. Will try to create even if format
     * is left out, by first using the default format (see {@link #property-defaultFormat-static}, by default
     * `YYYY-MM-DDTHH:mm:ssZ`) and then using `new Date(dateString)`.
     * Supported tokens:
     *
     * | Unit        | Token | Description                |
     * |-------------|-------|----------------------------|
     * | Year        | YYYY  | 2018                       |
     * |             | YY    | < 68 -> 2000, > 68 -> 1900 |
     * | Month       | MM    | 01 - 12                    |
     * | Date        | DD    | 01 - 31                    |
     * | Hour        | HH    | 00 - 23 or 1 - 12          |
     * | Minute      | mm    | 00 - 59                    |
     * | Second      | ss    | 00 - 59                    |
     * | Millisecond | S     | 0 - 9                      |
     * |             | SS    | 00 - 99                    |
     * |             | SSS   | 000 - 999                  |
     * | AM/PM       | A     | AM or PM                   |
     * |             | a     | am or pm                   |
     * | TimeZone    | Z     | Z for UTC or +-HH:mm       |
     * | Predefined  | L     | Long date, MM/DD/YYYY      |
     * |             | LT    | Long time, HH:mm A         |
     *
     * Predefined formats and functions used to parse tokens can be localized, see for example the swedish locale SvSE.js
     *
     * @param {String} dateString Date string
     * @param {String} format Date format (or {@link #property-defaultFormat-static} if left out)
     * @returns {Date}
     * @category Parse & format
     */
    static parse(dateString, format = this.defaultFormat) {
        if (!dateString) {
            return null;
        }

        if (dateString instanceof Date) {
            return dateString;
        }

        // // For ISO 8601 native is faster, but not very forgiving
        // if (format === defaultFormat) {
        //     const dt = new Date(dateString);
        //     if (!isNaN(dt)) {
        //         return dt;
        //     }
        // }

        let config = { year : 0, month : 0, date : 0, hours : 0, minutes : 0, seconds : 0, milliseconds : 0 },
            parser = parserCache[format],
            result;

        if (!parser) {
            parser = parserCache[format] = DateHelper.buildParser(format);
        }

        // Each parser knows its pattern and splitter. It looks for splitter in the
        // input string, takes first substring and tries to process it. Remaining string
        // is passed to the next parser.
        parser.reduce((dateString, parser) => {
            if (parser.last) {
                Object.assign(config, parser.fn(dateString));
            }
            else {
                let splitAt;

                // ISO 8601 says that T symbol can be replaced with a space
                if (parser.splitter === 'T' && dateString.indexOf('T') === -1) {
                    splitAt = dateString.indexOf(' ');
                }
                else {
                    // If splitter specified find its position, otherwise try to determine pattern length
                    splitAt = parser.splitter !== '' ? dateString.indexOf(parser.splitter) : parser.pattern && parser.pattern.length || -1;
                }

                let part, rest;

                // If splitter is not found in the current string we may be dealing with
                // 1. partial input - in that case we just feed all string to current parser and move on
                // 2. time zone (ssZ - splitter is empty string) and pattern is not specified, see comment below
                if (splitAt === -1) {
                    // NOTE: parantheses are required here as + and - signs hold valuable information
                    // with parantheses we get array like ['00','+','01:00'], omitting them we won't get
                    // regexp match in result, loosing information
                    let chunks = dateString.split(/([Z\-+])/);

                    // If splitter is not found in the string, we may be dealing with string that contains info about TZ.
                    // For instance, if format contains Z as last arg which is not separated (normally it is not indeed),
                    // like 'YYYY-MM-DD HH:mm:ssZ', then second to last parser will have string that it cannot just parse, like
                    // '2010-01-01 10:00:00'        -> '00'
                    // '2010-01-01 10:00:00Z'       -> '00Z'
                    // '2010-01-01 10:00:00-01'     -> '00-01'
                    // '2010-01-01 10:00:00+01:30'  -> '00+01:30'
                    // this cannot be processed by date parsers, so we need to process that additionally. So we
                    // split string by symbols that can be found around timezone info: Z,-,+
                    if (chunks.length === 1) {
                        part = dateString;
                        rest = '';
                    }
                    else {
                        part = chunks[0];
                        rest = `${chunks[1]}${chunks[2]}`;
                    }
                }
                else {
                    part = dateString.substring(0, splitAt) || dateString;
                    rest = dateString.substring(splitAt + parser.splitter.length);
                }

                parser.fn && Object.assign(config, parser.fn(part));

                return rest;
            }
        }, dateString);

        // If year is specified date has to be greater than 0
        if (config.year && !config.date) {
            config.date = 1;
        }

        const date = this.create(config);

        if (date) {
            result = date;
        }
        else {
            // Last resort, try if native passing can do it
            result = new Date(dateString);
        }

        return result;
    }

    /**
     * Creates a date from a date definition object. The object can have the following properties:
     * - year
     * - month
     * - date (day in month)
     * - hours
     * - minutes
     * - seconds
     * - milliseconds
     * - amPm : 'am' or 'pm', implies 12 hour clock
     * - timeZone : offset from UTC in minutes
     * @param {Object} definition
     * @returns {Date}
     * @category Parse & format
     */
    static create(definition) {
        // Shallow clone to not alter input
        const def = Object.assign({}, definition);

        // Not much validation yet, only considered invalid if all properties are 0
        let invalid = true,
            useUTC  = false;

        // Fill in blanks and replace any NaN with 0
        dateProperties.forEach(property => {
            if (!(property in def) || isNaN(def[property])) {
                def[property] = 0;
            }

            if (def[property] > 0) invalid = false;
        });

        if (def.amPm === 'pm') {
            def.hours = (def.hours % 12) + 12;
        }

        if ('timeZone' in def) {
            useUTC = true;

            def.minutes -= def.timeZone;
        }

        if (invalid) {
            return null;
        }

        const args = [def.year, def.month, def.date, def.hours, def.minutes, def.seconds, def.milliseconds];

        return useUTC ? new Date(Date.UTC(...args)) : new Date(...args);
    }

    /**
     * Converts a date to string with the specified format. Formats heavily inspired by https://momentjs.com.
     * Available formats (input used for output below is `new Date(2018,8,9,18,7,8,145)`):
     *
     * | Unit                  | Token | Description & output                  |
     * |-----------------------|-------|---------------------------------------|
     * | Year                  | YYYY  | 2018                                  |
     * |                       | YY    | 18                                    |
     * |                       | Y     | 2018                                  |
     * | Quarter               | Q     | 3                                     |
     * |                       | Qo    | 3rd                                   |
     * | Month                 | MMMM  | September                             |
     * |                       | MMM   | Sep                                   |
     * |                       | MM    | 09                                    |
     * |                       | Mo    | 9th                                   |
     * |                       | M     | 9                                     |
     * | Week (iso)            | WW    | 36 (2 digit)                          |
     * |                       | Wo    | 36th                                  |
     * |                       | W     | 36                                    |
     * | Date                  | DDDD  | Day of year, 3 digits                 |
     * |                       | DDDo  | Day of year, ordinal                  |
     * |                       | DDD   | Day of year                           |
     * |                       | DD    | 09                                    |
     * |                       | Do    | 9th                                   |
     * |                       | D     | 9                                     |
     * | Weekday               | dddd  | Sunday                                |
     * |                       | ddd   | Sun                                   |
     * |                       | dd    | Su                                    |
     * |                       | do    | 0th                                   |
     * |                       | d     | 0                                     |
     * | Hour                  | HH    | 18 (00 - 23)                          |
     * |                       | H     | 18 (0 - 23)                           |
     * |                       | hh    | 06 (00 - 12)                          |
     * |                       | h     | 6 (0 - 12)                            |
     * |                       | KK    | 19 (01 - 24)                          |
     * |                       | K     | 19 (1 - 24)                           |
     * |                       | kk    | 06 or 18, locale determines           |
     * |                       | k     | 6 or 18, locale determines            |
     * | Minute                | mm    | 07                                    |
     * |                       | m     | 7                                     |
     * | Second                | ss    | 08                                    |
     * |                       | s     | 8                                     |
     * | Millisecond           | S     | 1                                     |
     * |                       | SS    | 14                                    |
     * |                       | SSS   | 145                                   |
     * | AM/PM                 | A     | AM or PM                              |
     * |                       | a     | am or pm                              |
     * | Predefined            | LT    | H: 2-digit (2d), m: 2d                |
     * | (uses browser locale) | LTS   | H: 2d, m: 2d, s : 2d                  |
     * |                       | L     | Y: numeric (n), M : 2d, D : 2d        |
     * |                       | l     | Y: n, M : n, D : n                    |
     * |                       | LL    | Y: n, M : long (l), D : n             |
     * |                       | ll    | Y: n, M : short (s), D : n            |
     * |                       | LLL   | Y: n, M : l, D : n, H: n, m: 2d       |
     * |                       | lll   | Y: n, M : s, D : n, H: n, m: 2d       |
     * |                       | LLLL  | Y: n, M : l, D : n, H: n, m: 2d, d: l |
     * |                       | llll  | Y: n, M : s, D : n, H: n, m: 2d, d: s |
     *
     * Some examples:
     *
     * ```
     * DateHelper.format(new Date(2019, 7, 16), 'dddd') -> Friday
     * DateHelper.format(new Date(2019, 7, 16, 14, 27), 'HH:mm') --> 14:27
     * DateHelper.format(new Date(2019, 7, 16, 14, 27), 'L HH') --> 2019-07-16 14
     * ```
     *
     * Arbitrary text can be embedded in the format string by wrapping it with {}:
     *
     * ```
     * DateHelper.format(new Date(2019, 7, 16), '{It is }dddd{, yay!}') -> It is Friday, yay!
     * ```
     *
     * @param {Date} date Date
     * @param {String} format Desired format (uses `defaultFormat` if left out)
     * @returns {String} Formatted string
     * @category Parse & format
     */
    static format(date, format = this.defaultFormat) {
        // Bail out if no date or invalid date
        if (!date || isNaN(date)) {
            return null;
        }

        let formatter = formatCache[format],
            output    = '';

        if (!formatter) {
            formatter = formatCache[format] = [];

            // Build formatter array with the steps needed to format the date
            for (let i = 0; i < format.length; i++) {
                // Matches a predefined format?
                const
                    formatMatch = format.substr(i).match(formatRegexp),
                    predefined = formatMatch && formatMatch[0];

                if (predefined) {
                    const localeFormats = this.L('formats') !== 'formats' && this.L('formats') || {},
                        fn            = localeFormats[predefined] || formats[predefined];
                    formatter.push(fn);
                    i += predefined.length - 1;
                }
                // Start of text block? Append it
                else if (format[i] === '{') {
                    // Find closing brace
                    const index = format.indexOf('}', i + 1);

                    // No closing brace, grab rest of string
                    if (index === -1) {
                        formatter.push(format.substr(i + 1));
                        i = format.length;
                    }
                    // Closing brace found
                    else {
                        formatter.push(format.substring(i + 1, index));
                        // Carry on after closing brace
                        i = index;
                    }
                }
                // Otherwise append to output (for example - / : etc)
                else {
                    formatter.push(format[i]);
                }
            }
        }

        formatter.forEach(step => {
            if (typeof step === 'string') {
                output += step;
            }
            else {
                output += step(date);
            }
        });

        // MS inserts a Left-to-right control char between localized date parts, remove it to have the expected string
        // output. Otherwise comparision with typed strings will fail
        if (BrowserHelper.isEdge || BrowserHelper.isIE11) {
            output = output.replace(/\u200E/g, '');
        }

        return output;
    }

    /**
     * Converts the specified amount of desired unit into milliseconds. Can be called by only specifying a unit as the
     * first argument, it then uses amount = 1. For example: asMilliseconds('hour') == asMilliseconds(1, 'hour')
     * @param {Number/String} amount Amount, what of is decided by specifying unit (also takes a unit which implies an amount of 1)
     * @param {String} unit Time unit (s, hour, months etc.)
     * @returns {Number}
     * @category Parse & format
     */
    static asMilliseconds(amount, unit = null) {
        if (typeof amount === 'string') {
            unit = amount;
            amount = 1;
        }

        return this.as('millisecond', amount, unit);
    }

    /**
     * Converts a millisecond time delta to a human readable form. For example `1000 * 60 * 60 * 50`
     * milliseconds would be rendered as "2 days, 2 hours"
     * @param {Number} delta The millisecond delta value.
     * @param {Boolean} [abbrev] Pass `true` to use abbreviated unit names, eg "2d, 2h" for the above example.
     * @category Parse & format
     */
    static formatDelta(delta, abbrev = false) {
        const result = [],
            getUnit = abbrev ? this.getShortNameOfUnit : this.getLocalizedNameOfUnit,
            sep = abbrev ? '' : ' ';

        // Loop downwards through the magnitude of units from year -> ms
        for (const unitName of deltaUnits) {
            const d = Math.floor(this.as(unitName, delta));

            // If there's a non-zero integer quantity of this unit, add it to result
            // and subtract from the delta, then go round to next unit down.
            if (d) {
                result.push(`${d}${sep}${getUnit.call(this, unitName, d > 1)}`);
                delta -= this.as('ms', d, unitName);
            }
            if (!delta) {
                break;
            }
        }

        return result.join(', ');
    }

    /**
     * Converts the specified amount of one unit (fromUnit) into an amount of another unit (toUnit)
     * @param toUnit
     * @param amount
     * @param fromUnit
     * @returns {Number}
     * @category Parse & format
     */
    static as(toUnit, amount, fromUnit = 'ms') {
        if (toUnit === fromUnit) return amount;

        toUnit = this.normalizeUnit(toUnit);
        fromUnit = this.normalizeUnit(fromUnit);

        // validConversions[][] can be negative to signal that conversion is not exact, ignore sign here
        if (unitMagnitudes[fromUnit] > unitMagnitudes[toUnit]) {
            return amount * Math.abs(validConversions[fromUnit][toUnit]);
        }
        else {
            return amount / Math.abs(validConversions[toUnit][fromUnit]);
        }
    }

    static formatContainsHourInfo(format) {
        const
            stripEscapeRe = /(\\.)/g,
            hourInfoRe    = /([HhKkmSsAa]|LT|L{3,}|l{3,})/;

        return hourInfoRe.test(format.replace(stripEscapeRe, ''));
    }

    /**
     * Returns true for 24 hour format.
     * @param {String} format Date format
     * @returns {Boolean}
     * @category Parse & format
     */
    static is24HourFormat(format) {
        return this.format(new Date(1970, 0, 0, 13, 0, 0), format).includes('13');
    }

    //endregion

    //region Manipulate

    /**
     * Add days, hours etc. to a date. Always clones the date, original will be left unaffected.
     * @param {Date} date Original date
     * @param {Number} amount Amount of days, hours etc
     * @param {String} unit Unit for amount
     * @returns {Date} New calculated date
     * @category Manipulate
     */
    static add(date, amount, unit = 'ms') {
        const me = this;

        let d;

        if (typeof date === 'string') {
            d = me.parse(date);
        }
        else {
            d = new Date(date.getTime());
        }

        if (!unit || amount === 0) return d;

        unit = DateHelper.normalizeUnit(unit);

        switch (unit) {
            case 'millisecond':
                d = new Date(d.getTime() + amount);
                break;
            case 'second':
                d = new Date(d.getTime() + (amount * 1000));
                break;
            case 'minute':
                d = new Date(d.getTime() + (amount * 60000));
                break;
            case 'hour':
                d = new Date(d.getTime() + (amount * 3600000));
                break;
            case 'day':
                // Integer value added, do calendar calculation to correctly handle DST etc.
                if (amount % 1 === 0) {
                    d.setDate(d.getDate() + amount);
                }
                // No browsers support fractional values for dates any longer, do time based calculation
                else {
                    d = new Date(d.getTime() + (amount * 86400000));
                }

                if (d.getHours() === 23 && d.getHours() === 0) {
                    d = me.add(d, 1, 'hour');
                }
                break;
            case 'week':
                d.setDate(d.getDate() + amount * 7);
                break;
            case 'month':
                let day = d.getDate();
                if (day > 28) {
                    day = Math.min(day, me.getLastDateOfMonth(me.add(me.getFirstDateOfMonth(d), amount, 'month')).getDate());
                }
                d.setDate(day);
                d.setMonth(d.getMonth() + amount);
                break;
            case 'quarter':
                d = me.add(d, amount * 3, 'month');
                break;
            case 'year':
                d.setFullYear(d.getFullYear() + amount);
                break;
        }
        return d;
    }

    /**
     * Calculates the difference between two dates, in the specified unit.
     * @param {Date} start First date
     * @param {Date} end Second date
     * @param {String} unit Unit to calculate difference in
     * @param {Boolean} fractional Specify false to round result
     * @returns {Number} Difference in the specified unit
     * @category Manipulate
     */
    static diff(start, end, unit = 'ms', fractional = true) {
        unit = DateHelper.normalizeUnit(unit);

        if (!start || !end) return 0;

        let amount;

        switch (unit) {
            case 'year':
                amount = this.diff(start, end, 'month') / 12;
                break;

            case 'quarter':
                amount = this.diff(start, end, 'month') / 3;
                break;

            case 'month':
                amount = ((end.getFullYear() - start.getFullYear()) * 12) + (end.getMonth() - start.getMonth());
                break;

            case 'week':
                amount = this.diff(start, end, 'day') / 7;
                break;

            case 'day':
                const dstDiff = start.getTimezoneOffset() - end.getTimezoneOffset();
                amount = (end - start + dstDiff * 60 * 1000) / 86400000;
                break;

            case 'hour':
                amount = (end - start) / 3600000;
                break;

            case 'minute':
                amount = (end - start) / 60000;
                break;

            case 'second':
                amount = (end - start) / 1000;
                break;

            case 'millisecond':
                amount = (end - start);
                break;
        }

        return fractional ? amount : Math.round(amount);
    }

    /**
     * Sets the date to the start of the specified unit, by default returning a clone of the date instead of changing it
     * inplace.
     * @param {Date} date Original date
     * @param {String} unit Start of this unit, 'day', 'month' etc.
     * @param {Boolean} clone Manipulate a copy of the date
     * @returns {Date} Manipulated date
     * @category Manipulate
     */
    static startOf(date, unit = 'day', clone = true) {
        if (!date) {
            return null;
        }

        unit = this.normalizeUnit(unit);

        if (clone) date = this.clone(date);

        const toSet = {},
            index = unitMagnitudes[unit];

        for (let i = 0; i < index; i++) {
            const currentUnit = canonicalUnitNames[i];
            // Week and quarter are not part of the date
            if (currentUnit !== 'quarter' && currentUnit !== 'week') {
                toSet[currentUnit] = currentUnit === 'day' ? 1 : 0; // day is 1 based
            }
        }

        // Special handling of quarter and week
        if (unit === 'quarter') {
            toSet.month = (this.get(date, 'quarter') - 1) * 3;
        }
        else if (unit === 'week') {
            const delta = date.getDay() - this.weekStartDay;
            toSet.day = date.getDate() - delta;
        }

        return this.set(date, toSet);
    }

    /**
     * Creates a clone of the specified date
     * @param {Date} date Original date
     * @returns {Date} Cloned date
     * @category Manipulate
     */
    static clone(date) {
        return new Date(date.getTime());
    }

    /**
     * Removes time from a date (same as calling #startOf(date, 'day'))
     * @param {Date} date Date to remove time from
     * @param {Boolean} clone Manipulate a copy of the date
     * @returns {Date} Manipulated date
     * @category Manipulate
     */
    static clearTime(date, clone = true) {
        return DateHelper.startOf(date, 'day', clone);
    }

    /**
     * Sets a part of a date (in place)
     * @param {Date} date Date to manipulate
     * @param {String|Object} unit Part of date to set, for example 'minute'. Or an object like { second: 1, minute: 1 }
     * @param {Number} amount Value to set
     * @returns {Date} Modified date
     * @category Manipulate
     */
    static set(date, unit, amount) {
        if (!unit) {
            return date;
        }

        if (typeof unit === 'string') {
            switch (this.normalizeUnit(unit)) {
                case 'millisecond':
                    // Setting value to 0 when it is 0 at DST crossing messes it up
                    if (amount !== 0 || date.getMilliseconds() > 0) {
                        date.setMilliseconds(amount);
                    }
                    break;
                case 'second':
                    // Setting value to 0 when it is 0 at DST crossing messes it up
                    if (amount !== 0 || date.getSeconds() > 0) {
                        date.setSeconds(amount);
                    }
                    break;
                case 'minute':
                    // Setting value to 0 when it is 0 at DST crossing messes it up
                    if (amount !== 0 || date.getMinutes() > 0) {
                        date.setMinutes(amount);
                    }
                    break;
                case 'hour':
                    date.setHours(amount);
                    break;
                case 'day':
                case 'date':
                    date.setDate(amount);
                    break;
                case 'week':
                    throw new Error('week not implemented');
                case 'month':
                    date.setMonth(amount);
                    break;
                case 'quarter':
                    // Setting quarter = first day of first month of that quarter
                    date.setDate(1);
                    date.setMonth((amount - 1) * 3);
                    break;
                case 'year':
                    date.setFullYear(amount);
                    break;
            }
        }
        else {
            Object.entries(unit)
            // Make sure smallest unit goes first, to not change month before changing day
                .sort((a, b) => unitMagnitudes[a[0]] - unitMagnitudes[b[0]])
                .forEach(([unit, amount]) => {
                    this.set(date, unit, amount);
                });
        }

        return date;
    }

    /**
     * Constrains the date within a min and a max date
     * @param {Date} date The date to constrain
     * @param {Date} [min] Min date
     * @param {Date} [max] Max date
     * @return {Date} The constrained date
     * @category Manipulate
     */
    static constrain(date, min, max) {
        if (min != null) {
            date = this.max(date, min);
        }
        return max == null ? date : this.min(date, max);
    }

    /**
     * Returns time only (same as calling new Date(1970, 0, 1, hours, minutes, seconds))
     * @param {Number} hours Hours value
     * @param {Number} minutes Minutes value
     * @param {Number} seconds Seconds value
     * @returns {Date} Date value for input hours, minutes, seconds parameters
     * @category Manipulate
     */
    static getTime(hours, minutes = 0, seconds = 0) {
        return new Date(1970, 0, 1, hours, minutes, seconds);
    }

    /**
     * Copies hours, minutes, seconds, milliseconds from one date to another
     *
     * @param {Date} targetDate The target date
     * @param {Date} sourceDate The source date
     * @return {Date} The adjusted target date
     * @category Manipulate
     * @static
     */
    static copyTimeValues(targetDate, sourceDate) {
        targetDate.setHours(sourceDate.getHours());
        targetDate.setMinutes(sourceDate.getMinutes());
        targetDate.setSeconds(sourceDate.getSeconds());
        targetDate.setMilliseconds(sourceDate.getMilliseconds());
        return targetDate;
    }

    //endregion

    //region Comparison

    static isDST(date) {
        // from https://stackoverflow.com/questions/11887934/how-to-check-if-the-dst-daylight-saving-time-is-in-effect-and-if-it-is-whats
        const jan = new Date(date.getFullYear(), 0, 1),
            jul = new Date(date.getFullYear(), 6, 1);
        return date.getTimezoneOffset() < Math.max(jan.getTimezoneOffset(), jul.getTimezoneOffset());
    }

    /**
     * Determines if a date precedes another.
     * @param first First date
     * @param second Second date
     * @returns {Boolean} true if first precedes second, otherwise false
     * @category Comparison
     */
    static isBefore(first, second) {
        return first < second;
    }

    /**
     * Determines if a date succeeds another.
     * @param first First date
     * @param second Second date
     * @returns {Boolean} true if first succeeds second, otherwise false
     * @category Comparison
     */
    static isAfter(first, second) {
        return first > second;
    }

    /**
     * Checks if two dates are equal.
     * @param first First date
     * @param second Second date
     * @param unit If not given, the comparison will be done up to a millisecond
     * @returns {Boolean} true if the dates are equal
     * @category Comparison
     */
    static isEqual(first, second, unit = null) {
        if (unit === null) {
            // https://jsbench.me/3jk2bom2r3/1
            return first && second && first.getTime() === second.getTime();
        }

        return this.startOf(first, unit) - this.startOf(second, unit) === 0;
    }

    /**
     * Compares two dates using the specified precision
     * @param {Date} first
     * @param {Date} second
     * @param {String} unit
     * @returns {Number} 0 = equal, -1 first before second, 1 first after second
     * @category Comparison
     */
    static compare(first, second, unit = null) {
        const DH = this;

        // Unit specified, cut the rest out
        if (unit) {
            first = DH.startOf(first, unit);
            second = DH.startOf(second, unit);
        }

        // Comparision on ms level
        if (DH.isBefore(first, second)) return -1;
        if (DH.isAfter(first, second)) return 1;
        return 0;
    }

    /**
     * Checks if date is the start of specified unit
     * @param {Date} date
     * @param {String} unit
     * @returns {Boolean}
     * @category Comparison
     */
    static isStartOf(date, unit) {
        return this.isEqual(date, this.startOf(date, unit));
    }

    /**
     * Checks if this date is >= start and < end.
     * @param {Date} date The source date
     * @param {Date} start Start date
     * @param {Date} end End date
     * @return {Boolean} true if this date falls on or between the given start and end dates.
     * @category Comparison
     */
    static betweenLesser(date, start, end) {
        //return start <= date && date < end;
        return start.getTime() <= date.getTime() && date.getTime() < end.getTime();
    }

    /**
     * Checks if this date is >= start and <= end.
     * @param {Date} date The source date
     * @param {Date} start Start date
     * @param {Date} end End date
     * @return {Boolean} true if this date falls on or between the given start and end dates.
     * @category Comparison
     */
    static betweenLesserEqual(date, start, end) {
        return start.getTime() <= date.getTime() && date.getTime() <= end.getTime();
    }

    /**
     * Returns true if dates intersect
     * @param {Date} date1Start
     * @param {Date} date1End
     * @param {Date} date2Start
     * @param {Date} date2End
     * @return {Boolean} Returns true if dates intersect
     * @category Comparison
     */
    static intersectSpans(date1Start, date1End, date2Start, date2End) {
        return this.betweenLesser(date1Start, date2Start, date2End) ||
            this.betweenLesser(date2Start, date1Start, date1End);
    }

    /**
     * Returns 1 if first param is a greater unit than second param, -1 if the opposite is true or 0 if they're equal
     * @param {String} unit1 The 1st unit
     * @param {String} unit2 The 2nd unit
     * @category Comparison
     */
    static compareUnits(unit1, unit2) {
        return Math.sign(unitMagnitudes[unit1] - unitMagnitudes[unit2]);
    }

    /**
     * Returns true if the first time span completely 'covers' the second time span.
     * @example
     * DateHelper.timeSpanContains(new Date(2010, 1, 2), new Date(2010, 1, 5), new Date(2010, 1, 3), new Date(2010, 1, 4)) ==> true
     * DateHelper.timeSpanContains(new Date(2010, 1, 2), new Date(2010, 1, 5), new Date(2010, 1, 3), new Date(2010, 1, 6)) ==> false
     * @param {Date} spanStart The start date for initial time span
     * @param {Date} spanEnd The end date for initial time span
     * @param {Date} otherSpanStart The start date for the 2nd time span
     * @param {Date} otherSpanEnd The end date for the 2nd time span
     * @return {Boolean}
     * @category Comparison
     */
    static timeSpanContains(spanStart, spanEnd, otherSpanStart, otherSpanEnd) {
        return (otherSpanStart - spanStart) >= 0 && (spanEnd - otherSpanEnd) >= 0;
    }

    //endregion

    //region Query

    /**
     * Get the first day of week, 0-6 (Sunday-Saturday). This is determined by the used locale.
     * @readonly
     */
    static get weekStartDay() {
        // Try locale first
        let weekStartDay = this.L('weekStartDay');
        if (weekStartDay !== 'weekStartDay') {
            return weekStartDay;
        }

        // Default to 0, should not need to happen in real world scenarios when a locale is always loaded
        return 0;
    }

    /**
     * Get the specified part of a date
     * @param {Date} date
     * @param {String} unit Part of date, hour, minute etc.
     * @category Query
     */
    static get(date, unit) {
        switch (this.normalizeUnit(unit)) {
            case 'millisecond':
                return date.getMilliseconds();
            case 'second':
                return date.getSeconds();
            case 'minute':
                return date.getMinutes();
            case 'hour':
                return date.getHours();
            case 'date':
            case 'day': // Scheduler has a lot of calculations expecting this to work
                return date.getDate();
            case 'week':
                return formats.W(date);
            case 'month':
                return date.getMonth();
            case 'quarter':
                return Math.floor(date.getMonth() / 3) + 1;
            case 'year':
                return date.getFullYear();
        }

        return null;
    }

    /**
     * Get number of days in the current month for the supplied date
     * @param {Date} date Date which month should be checked
     * @returns {Number} Days in month
     * @category Query
     */
    static daysInMonth(date) {
        const d = this.clone(date);
        d.setDate(1);
        d.setMonth(date.getMonth() + 1);
        d.setDate(0);
        return d.getDate();
    }

    /**
     * Get the first date of the month for the supplied date
     * @param {Date} date
     * @returns {Date}
     * @category Query
     */
    static getFirstDateOfMonth(date) {
        return new Date(date.getFullYear(), date.getMonth(), 1);
    }

    /**
     * Get the last date of the month for the supplied date
     * @param {Date} date
     * @returns {Date}
     * @category Query
     */
    static getLastDateOfMonth(date) {
        return new Date(date.getFullYear(), date.getMonth() + 1, 0);
    }

    /**
     * Get the earliest of two dates
     * @param {Date} first
     * @param {Date} second
     * @returns {Date} Earliest date
     * @category Query
     */
    static min(first, second) {
        return first.getTime() < second.getTime() ? first : second;
    }

    /**
     * Get the latest of two dates
     * @param {Date} first
     * @param {Date} second
     * @returns {Date} Latest date
     * @category Query
     */
    static max(first, second) {
        return first.getTime() > second.getTime() ? first : second;
    }

    /**
     * Get an incremented date. Incrementation based on specified unit and optional amount
     * @param {Date} date
     * @param {String} unit
     * @param {Number} [increment]
     * @param {Number} [weekStartDay] Will default to what is set in locale
     * @returns {Date}
     * @category Query
     */
    static getNext(date, unit, increment = 1, weekStartDay = this.weekStartDay) {
        const me = this;

        if (unit === 'week') {
            let dt  = me.clone(date),
                day = dt.getDay();
            dt = me.startOf(dt, 'day');
            dt = me.add(dt, weekStartDay - day + 7 * (increment - (weekStartDay <= day ? 0 : 1)), 'day');

            // For south american timezones, midnight does not exist on DST transitions, adjust...
            if (dt.getDay() !== weekStartDay) me.add(dt, 1, 'hour');

            return dt;
        }

        return me.startOf(me.add(date, increment, unit), unit);
    }

    /**
     * Get the start of the next day
     * @param {Date} date
     * @param {Boolean} clone
     * @param {Boolean} noNeedToClearTime
     * @returns {Date}
     * @category Query
     */
    static getStartOfNextDay(date, clone, noNeedToClearTime) {
        let nextDay = DateHelper.add(noNeedToClearTime ? date : DateHelper.clearTime(date, clone), 1, 'day');

        // DST case
        if (nextDay.getDate() == date.getDate()) {
            let offsetNextDay = DateHelper.add(DateHelper.clearTime(date, clone), 'day', 2).getTimezoneOffset(),
                offsetDate    = date.getTimezoneOffset();

            nextDay = DateHelper.add(nextDay, offsetDate - offsetNextDay, 'minute');
        }

        return nextDay;
    }

    /**
     * Get the end of previous day
     * @param {Date} date
     * @param {Boolean} noNeedToClearTime
     * @returns {Date}
     * @category Query
     */
    static getEndOfPreviousDay(date, noNeedToClearTime) {
        let dateOnly = noNeedToClearTime ? date : DateHelper.clearTime(date, true);

        // dates are different
        if (dateOnly - date) {
            return dateOnly;
        }
        else {
            return this.add(dateOnly, -1, 'day');
        }
    }

    //endregion

    //region Unit helpers

    /**
     * Turns (10, 'day') into '10 days' etc.
     * @param {Number} count Amount of unit
     * @param {String} unit Unit, will be normalizes (days, d -> day etc.)
     * @returns {String}
     * @category Unit helpers
     */
    static formatCount(count, unit) {
        unit = DateHelper.normalizeUnit(unit);
        if (count !== 1) unit += 's';
        return count + ' ' + unit;
    }

    /**
     * Get the ratio between two units ( year, month -> 1/12 )
     * @param {String} baseUnit
     * @param {String} unit
     * @param {Boolean} acceptEstimate If true, process negative values of validConversions. Defaults to false.
     * @returns {Number} Ratio
     * @category Unit helpers
     */
    static getUnitToBaseUnitRatio(baseUnit, unit, acceptEstimate = false) {
        baseUnit = DateHelper.normalizeUnit(baseUnit);
        unit = DateHelper.normalizeUnit(unit);

        if (baseUnit === unit) return 1;

        // Some validConversions have negative sign to signal that it is not an exact conversion.
        // Ignore those here unless acceptEstimate is provided.
        if (validConversions[baseUnit] && validConversions[baseUnit][unit] && (acceptEstimate || validConversions[baseUnit][unit] > 0)) {
            return 1 / DateHelper.as(unit, 1, baseUnit);
        }

        if (validConversions[unit] && validConversions[unit][baseUnit] && (acceptEstimate || validConversions[unit][baseUnit] > 0)) {
            return DateHelper.as(baseUnit, 1, unit);
        }

        return -1;
    }

    /**
     * Get unit suitable to use in calculations (?)
     * @param unit
     * @returns {*}
     * @category Unit helpers
     */
    static getMeasuringUnit(unit) {
        if (unit === 'week') return 'day';
        return unit;
    }

    /**
     * Returns a localized abbreviated form of the name of the duration unit.
     * For example in the `EN` locale, for `"qrt"` it will return `"q"`.
     * @param {String} unit Duration unit
     * @return {String}
     * @category Unit helpers
     */
    static getShortNameOfUnit(unit) {
        const me = this;

        // Convert abbreviations to the canonical name.
        // See locale file and the applyLocale method below.
        unit = me.parseTimeUnit(unit);

        // unitLookup is keyed by eg 'DAY', 'day', 'MILLISECOND', 'millisecond' etc
        return me.unitLookup[unit].abbrev;
    }

    /**
     * Returns a localized full name of the duration unit.
     * For for example in the `EN` locale, for `"d"` it will return either
     * `"day"` or `"days"`, depending from the `plural` argument
     * @static
     * @param {String} unit Time unit
     * @param {Boolean} plural Whether to return a plural name or singular
     * @return {String}
     * @category Unit helpers
     */
    static getLocalizedNameOfUnit(unit, plural) {
        const me = this;

        // Normalize to not have to have translations for each variation used in code
        unit = me.normalizeUnit(unit);

        // Convert abbreviations to the canonical name.
        // See locale file and the applyLocale method below.
        unit = me.parseTimeUnit(unit);

        // unitLookup is keyed by eg 'DAY', 'day', 'MILLISECOND', 'millisecond' etc
        return me.unitLookup[unit][plural ? 'plural' : 'single'];
    }

    /**
     * Normalizes a unit for easier usage in conditionals. For example year, years, y -> year
     * @param {String} unit
     * @returns {String}
     * @category Unit helpers
     */
    static normalizeUnit(unit) {
        if (!unit) {
            return null;
        }

        if (unit.toLowerCase() === 'date') {
            return unit.toLowerCase();
        }

        return canonicalUnitNames.includes(unit.toLowerCase())
            // Already valid
            ? unit.toLowerCase()
            // Trying specified case first, since we have both "M" for month and "m" for minute
            : normalizedUnits[unit] || normalizedUnits[unit.toLowerCase()];
    }

    static getUnitByName(name) {
        // Allow either a canonical name to be passed, or, if that fails, parse it as a localized name or abbreviation.
        return DateHelper.normalizeUnit(name) || DateHelper.normalizeUnit(this.parseTimeUnit(name));
    }

    /**
     * Returns a duration of the timeframe in the given unit.
     * @param {Date} start The start date of the timeframe
     * @param {Date} end The end date of the timeframe
     * @param {String} unit Duration unit
     * @return {Number} The duration in the units
     * @category Unit helpers
     * @ignore
     */
    static getDurationInUnit(start, end, unit, doNotRound) {
        return this.diff(start, end, unit, doNotRound);
    }

    /**
     * Checks if two date units align
     * @private
     * @param {String} majorUnit
     * @param {String} minorUnit
     * @returns {Boolean}
     * @category Unit helpers
     */
    static doesUnitsAlign(majorUnit, minorUnit) {
        // TODO: probably needs some fleshing out to be generally useful, otherwise move to TimeAxisViewModel?
        // Maybe also use getUnitToBaseUnitRatio() for assertion?
        if (majorUnit !== minorUnit && minorUnit === 'week') return false;
        return true;
    }

    static getSmallerUnit(unit) {
        return canonicalUnitNames[unitMagnitudes[this.normalizeUnit(unit)] - 1] || null;
    }

    static getLargerUnit(unit) {
        return canonicalUnitNames[unitMagnitudes[this.normalizeUnit(unit)] + 1] || null;
    }

    //endregion

    //region Date picker format

    /**
     * Parses a typed duration value according to locale rules.
     *
     * The value is taken to be a string consisting of the numeric magnitude and the units.
     * The units may be a recognised unit abbreviation of this locale or the full local unit name.
     *
     * For example: "2d", "2 d", "2 day", "2 days" will be turned into `{ magnitude : 2, unit : "day" }`
     *
     * **NOTE:** Doesn't work with complex values like "2 days, 2 hours"
     *
     * @param {String} value The value to parse.
     * @param {Boolean} [allowDecimals=true] Decimals are allowed in the magnitude.
     * @param {String} [defaultUnit] Default unit to use if only magnitude passed.
     * @returns {Object} If successfully parsed, the result contains two properties,
     * `magnitude` being a number, and `unit` being the canonical unit name, *NOT*
     * a localized name. If parsing was unsuccessful, `null` is returned.
     * @category Parse & format
     */
    static parseDuration(value, allowDecimals = true, defaultUnit) {
        const
            me            = this,
            durationRegEx = allowDecimals ? withDecimalsDurationRegex : noDecimalsDurationRegex,
            match         = durationRegEx.exec(value);

        if (value == null || !match) {
            return null;
        }

        const
            magnitude = parseNumber(match[1]),
            unit      = me.parseTimeUnit(match[2]) || defaultUnit;

        if (!unit) {
            return null;
        }

        return {
            magnitude,
            unit
        };
    }

    /**
     * Parses a typed unit name, for example `"ms"` or `"hr"` or `"yr"` into the
     * canonical form of the unit name which may be passed to {@link #function-add-static}
     * or {@link #function-diff-static}
     * @param {*} unitName
     * @category Parse & format
     */
    static parseTimeUnit(unitName) {
        // NOTE: In case you get a crash here when running tests, it is caused by missing locale. Build locales
        // using `scripts/build.js locales` to resolve.
        const unitMatch = unitName == null ? null : this.durationRegEx.exec(unitName.toLowerCase());

        if (!unitMatch) {
            return null;
        }

        // See which group in the unitAbbrRegEx matched match[2]
        for (let unitOrdinal = 0; unitOrdinal < canonicalUnitNames.length; unitOrdinal++) {
            if (unitMatch[unitOrdinal + 1]) {
                return canonicalUnitNames[unitOrdinal];
            }
        }
    }

    //endregion

    static fillDayNames() {
        const
            me            = this,
            tempDate      = new Date('2000-01-01T00:00:00'),
            dayNames      = me._dayNames || [],
            dayShortNames = me._dayShortNames || [];

        dayNames.length = 0;
        dayShortNames.length = 0;

        for (let day = 2; day < 9; day++) {
            tempDate.setDate(day);
            dayNames.push(me.format(tempDate, 'dddd'));
            dayShortNames.push(me.format(tempDate, 'ddd'));
        }

        me._dayNames = dayNames;
        me._dayShortNames = dayShortNames;
    }

    static getDayNames() {
        return this._dayNames;
    }

    static getDayName(day) {
        return this._dayNames[day];
    }

    static getDayShortNames() {
        return this._dayShortNames;
    }

    static getDayShortName(day) {
        return this._dayShortNames[day];
    }

    static fillMonthNames() {
        const
            me = this,
            tempDate = new Date('2000-01-01T00:00:00'),
            monthNames = me._monthNames || [],
            monthShortNames = me._monthShortNames || [];

        monthNames.length      = 0;
        monthShortNames.length = 0;

        for (let month = 0; month < 12; month++) {
            tempDate.setMonth(month);
            monthNames.push(me.format(tempDate, 'MMMM'));
            monthShortNames.push(me.format(tempDate, 'MMM'));
        }

        me._monthNames = monthNames;
        me._monthShortNames = monthShortNames;
    }

    static getMonthShortNames() {
        return this._monthShortNames;
    }

    static getMonthShortName(month) {
        return this._monthShortNames[month];
    }

    static getMonthNames() {
        return this._monthNames;
    }

    static getMonthName(month) {
        return this._monthNames[month];
    }

    static set locale(name) {
        locale = name;
    }

    static get locale() {
        return locale;
    }

    static applyLocale() {
        const me                = this,
            unitAbbreviations = me.L('unitAbbreviations'),
            unitNames         = me.unitNames = me.L('unitNames');

        // This happens when applying an incomplete locale, as done in Localizable.t.js.
        // Invalid usecase, but return to prevent a crash in that test.
        if (unitNames === 'unitNames') {
            return;
        }

        locale = me.L('locale') || 'en-US';
        if (locale === 'en-US') {
            // TODO: Include in En locale instead?
            ordinalSuffix = enOrdinalSuffix;
        }
        else if (me.L('ordinalSuffix') !== 'ordinalSuffix') {
            ordinalSuffix = me.L('ordinalSuffix');
        }
        formatCache = {};
        parserCache = {};
        intlFormatterCache = {};

        let unitAbbrRegEx = '';

        me.unitLookup = {};

        for (let i = 0; i < unitAbbreviations.length; i++) {
            const a = unitAbbreviations[i],
                n = unitNames[i];

            n.canonicalUnitName = canonicalUnitNames[i];

            // Create a unitLookup object keyed by unit full names
            // both lower and upper case to be able to look up plurals or abbreviations
            // also always include english names, since those are used in sources
            me.unitLookup[n.single] = me.unitLookup[n.single.toUpperCase()] =
                me.unitLookup[n.canonicalUnitName] = me.unitLookup[n.canonicalUnitName.toUpperCase()] = n;

            unitAbbrRegEx += `${i ? '|' : ''}(`;
            for (let j = 0; j < a.length; j++) {
                unitAbbrRegEx += `${a[j]}|`;
            }
            unitAbbrRegEx += `${n.single}|${n.plural}|${n.canonicalUnitName})`;
        }
        me.durationRegEx = new RegExp(`^(?:${unitAbbrRegEx})$`);

        // rebuild day/month names cache
        me.fillDayNames();
        me.fillMonthNames();
    }
}

/*  */
DateHelper._$name = 'DateHelper';

// Update when changing locale
LocaleManager.on({
    locale  : 'applyLocale',
    thisObj : DateHelper
});

// Apply default locale
if (LocaleManager.locale) DateHelper.applyLocale();
