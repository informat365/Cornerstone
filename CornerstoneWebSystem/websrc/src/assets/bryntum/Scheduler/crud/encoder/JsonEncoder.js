import Base from '../../../Core/Base.js';
import StringHelper from '../../../Core/helper/StringHelper.js';

/**
 * @module Scheduler/crud/encoder/JsonEncoder
 */

/**
 * Implements data encoding functional that should be mixed to a {@link Scheduler.crud.AbstractCrudManager} sub-class.
 * Uses _JSON_ as an encoding system.
 *
 * @example
 * // create a new CrudManager using AJAX as a transport system and JSON for encoding
 * class MyCrudManager extends JsonEncode(AjaxTransport(AbstractCrudManager)) {}
 *
 * @mixin
 */
export default Target => class JsonEncoder extends (Target || Base) {
    //format  : 'json',

    /**
     * Encodes an request object to _JSON_ encoded string. If encoding fails (due to circular structure), it returns null.
     * @param {Object} request The request to encode.
     * @returns {String} The encoded request.
     */
    encode(requestConfig) {
        return StringHelper.safeJsonStringify(requestConfig);
    }

    /**
     * Decodes (parses) a _JSON_ response string to an object. If parsing fails, it returns null.
     * @param {String} responseText The response text to decode.
     * @returns {Object} The decoded response.
     */
    decode(responseText) {
        return StringHelper.safeJsonParse(responseText);
    }
};
