/**
 * @module Core/helper/TemplateHelper
 */

const
    tagSpaceRe = />[ \t\r\n]+</g,
    multiSpaceRe = /\s\s+/g;

/**
 * Helper for template strings, use as tag function
 * @example
 * TemplateHelper.tpl`...`
 * @internal
 */
export default class TemplateHelper {
    /**
     * Tag function for template literals that does some basic cleanup
     * @private
     */
    static tpl(strings, ...values) {
        let count  = 0,
            output = values.map((val, i) => {
                // TODO: pad output nicely instead of just trimming away
                let ret = strings[i];

                if (Array.isArray(val)) {
                    ret += val.reduce((ack, item) => ack += (typeof item === 'string' ? item.trim() : item) + '\n', '');
                }
                else {
                    if (val === undefined) {
                        val = '';
                    }
                    ret += typeof val === 'string' ? val.trim() : val;
                }

                count++;

                return ret;
            }).join('');

        if (count <= strings.length) {
            output += strings[strings.length - 1];
        }

        // Excise unnecessary *soft* whitespace textNodes. We cannot use \s because that
        // matches non-breaking spaces which need to be preserved.
        // Also reduce any usage of multiple spaces to single, since they have no value in html
        return output.replace(tagSpaceRe, '><').replace(multiSpaceRe, ' ').trim();
    }

    /**
     * Tag function for template literals that does some basic cleanup. Version for docs that do not remove blank space,
     * to keep code snippets formatting intact.
     * @private
     */
    static docsTpl(strings, ...values) {
        let count  = 0,
            output = values.map((val, i) => {
                // TODO: pad output nicely instead of just trimming away

                let str = strings[i],
                    ret = str;

                if (Array.isArray(val)) {
                    ret += val.reduce((ack, item) => ack += (typeof item === 'string' ? item.trim() : item) + '\n', '');
                }
                else {
                    if (val === undefined) {
                        val = '';
                    }
                    ret += typeof val === 'string' ? val.trim() : val;
                }

                count++;

                return ret;
            }).join('');

        if (count <= strings.length) {
            output += strings[strings.length - 1];
        }

        // Excise unnecessary *soft* whitespace textNodes. We cannot use \s because that
        // matches non-breaking spaces which need to be preserved.
        return output.replace(tagSpaceRe, '><').trim();
    }

    static repeat(times) {
        return function(...args) {
            return TemplateHelper.tpl(...args).repeat(times);
        };
    }

    static loop(times) {

    }
}
