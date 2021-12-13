/**
 * @module Scheduler/preset/ViewPresetHeaderRow
 */

/**
 * A part of the {@link Scheduler.preset.ViewPreset} declaration. Not used directly, but the properties below are instead provided inline
 * as seen in sources of {@link Scheduler.preset.PresetManager}. This class is just provided for documentation purposes.
 *
 * A sample header configuration can look like below
 * ```javascript
 * headers    : {
 *     {
 *         unit        : "month",
 *         renderer : function(start, end, headerConfig, index) {
 *             var month = start.getMonth();
 *             // Simple alternating month in bold
 *             if (start.getMonth() % 2) {
 *                 return '<strong>' + month + '</strong>';
 *             }
 *             return month;
 *         },
 *         align       : 'start' // `start` or `end`, omit to center content (default)
 *     },
 *     {
 *         unit        : "week",
 *         increment   : 1,
 *         renderer    : function(start, end, headerConfig, index) {
 *             return 'foo';
 *         }
 *     },
 * }
 * ```
 */
export default class ViewPresetHeaderRow {

    /**
     * The text alignment for the cell. Valid values are `start` or `end`, omit this to center text content (default)
     * @config {String} align
     */

    /**
     * The unit of time represented by each cell in this header row. See also increment property.
     * Valid values are "millisecond", "second", "minute", "hour", "day", "week", "month", "quarter", "year".
     * @config {String} unit
     */

    /**
     * A CSS class to add to the cells in the time axis header row. Can also be added programmatically in the {@link #config-renderer}
     * @config {String} headerCellCls
     */

    /**
     * The number of units each header cell will represent (e.g. 30 together with unit: "minute" for 30 minute cells)
     * @config {Number} increment
     */

    /**
     * Defines how the cell date will be formatted
     * @config {String} dateFormat
     */

    /**
     * A custom renderer function used to render the cell contents. It should return the text to put in the header cell.
     * The render function is called with the following parameters:
     *
     * - `startDate` : Date - The start date of the cell.
     * - `endDate` : Date - The end date of the cell
     * - `headerConfig` : Object - An object containing the header config object. You can set 'align' (for text-align)
     *   and headerCls (a CSS class added to the cell) properties on it.
     * - `i` : Int - The index of the cell in the row.
     *
     * Example :
     * ```javascript
     * function (startDate, endDate, headerConfig, i) {
     *   headerConfig.align = "left";
     *   headerConfig.headerCls = "myClass"; // will be added as a CSS class of the header cell DOM element
     *
     *   return DateHelper.format(startDate, 'YYYY-MM-DD');
     * }
     * ```
     * @config {function} renderer
     */

    /**
     * `this` reference for the renderer function
     * @config {Object} thisObj
     */

    /**
     * A function that should return an array of objects containing 'start', 'end' and 'header' properties.
     * Use this if you want full control over how the header rows are generated. This is not applicable for the lowest
     * row in your configuration.
     * @config {function} cellGenerator
     */
}
