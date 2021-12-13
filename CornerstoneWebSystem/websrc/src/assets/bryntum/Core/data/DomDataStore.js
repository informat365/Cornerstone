/**
 * @module Core/data/DomDataStore
 */

/**
 * Stores data on a dom element (by setting element._domData).
 * Instead of using HTML5:s element.dataset, which turned out to be slow.
 * @private
 */
export default class DomDataStore {
    /**
     * Get data that is connected to specified element.
     * @param {HTMLElement} element DOM element
     * @param {String} [key] The name of the property in the element data to return;
     * @returns {Object} Data
     */
    static get(element, key) {
        const result = (element._domData || (element._domData = {}));

        if (key != null) {
            return result[key];
        }
        return result;
    }

    /**
     * Set data connected to specified element (completely replacing any existing).
     * To update data, use DomDataStore#assign instead.
     * @param {HTMLElement} element DOM element
     * @param {Object|String} data Data object to set, or property name to set
     * @param {*} [value] If the previous parameter was a property name, this is the value to set.
     */
    static set(element, data, value) {
        if (arguments.length === 3) {
            (element._domData || (element._domData = {}))[data] = value;
        }
        else {
            element._domData = data;
        }
    }

    /**
     * Updates data connected to specified element.
     * @param element DOM element
     * @param data Data to assign
     */
    static assign(element, data) {
        Object.assign((element._domData || (element._domData = {})), data);
    }
}
