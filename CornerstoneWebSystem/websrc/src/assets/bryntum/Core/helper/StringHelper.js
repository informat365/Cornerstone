/**
 * @module Core/helper/StringHelper
 */

const camelLettersRe    = /([a-z])([A-Z])/g,
    escapeRegExpRe      = /[.*+?^${}()|[\]\\]/g,
    idRe                = /(^[^a-z]+|[^\w]+)/gi,
    replaceCamelLetters = (all, g1, g2) => {
        return g1 + '-' + g2.toLowerCase();
    },
    replaceNonIdChar    = c => {
        if (c) {
            return `_x${c.charCodeAt(0).toString(16)}`;
        }
        return '__blank__';
    },
    hyphenateCache      = {};

/**
 * Helper for string manipulation.
 */
export default class StringHelper {
    /**
     * Capitalizes the first letter of a string, myString -> MyString.
     * Doesn't alter the original string, use return value
     * @param string String to capitalize
     * @returns {String} Capitalized string
     */
    static capitalizeFirstLetter(string) {
        if (!string) return null;
        return string[0].toUpperCase() + string.substr(1);
    }

    /**
     * Makes the first letter of a string lowercase, MyString -> myString.
     * Doesn't alter the original string, use return value
     * @param string String to alter
     * @returns {String} Altered string
     */
    static lowercaseFirstLetter(string) {
        if (!string) return null;
        return string[0].toLowerCase() + string.substr(1);
    }

    /**
     * Converts the passed camelCased string to a hyphen-separated string. eg "minWidth" -> "min-width"
     * @param string The string to convert.
     * @return {String} The string with adjoining lower and upper case letters
     * separated by hyphens and converted to lower case.
     */
    static hyphenate(string) {
        // Cached since it is used heavily with DomHelper.sync()
        const cached = hyphenateCache[string];
        if (cached) {
            return cached;
        }
        return hyphenateCache[string] = string.replace(camelLettersRe, replaceCamelLetters);
    }

    /**
     * Parses JSON within a try-catch.
     * @param {String} string String to parse
     * @returns {Object} Resulting object or null if parse failed
     */
    static safeJsonParse(string) {
        let parsed = null;

        try {
            parsed = JSON.parse(string);
        }
        catch (e) {
            console.error(e);
        }

        return parsed;
    }

    /**
     * Stringifies an object within a try-catch.
     * @param {Object} object The object to stringify
     * @returns {Object} Resulting object or null if stringify failed
     */
    static safeJsonStringify(obj) {
        let result = null;

        try {
            result = JSON.stringify(obj);
        }
        catch (e) {
            console.error(e);
        }

        return result;
    }

    /**
     * Creates an alphanuneric identifier from any passed string. Encodes spaces and non-alpha characters.
     * @param inString The string from which to strip non-identifier characters.
     * @return {String}
     */
    static createId(inString) {
        return String(inString).replace(idRe, replaceNonIdChar);
    }

    // https://stackoverflow.com/questions/3446170/escape-string-for-use-in-javascript-regex
    static escapeRegExp(string) {
        // $& means the whole matched string
        return string.replace(escapeRegExpRe, '\\$&');
    }

    /**
     * Joins all given paths together using the separator as a delimiter and normalizes the resulting path.
     * @param paths {Array} array of paths to join
     * @param pathSeparator [{String}] path separator. Default value is '/'
     * @return {String}
     */
    static joinPaths(paths, pathSeparator = '/') {
        return paths.join(pathSeparator).replace(new RegExp('\\' + pathSeparator + '+', 'g'), pathSeparator);
    }

}
