import IdHelper from './IdHelper.js';

/**
 * @module Core/helper/WidgetHelper
 */

/**
 * Helper for creating widgets. Needs an adapter to do the actual work, see
 * {@link Core.adapter.widget.BryntumWidgetAdapter} which is used in examples.
 *
 * NOTE: This concept is not yet fully realized.
 */
export default class WidgetHelper {
    //region Adapter

    /**
     * Gets/sets the adapter used for widgets. If not specified, it will default to {@link Core.adapter.widget.BryntumWidgetAdapter BryntumWidgetAdapter}
     * automatically as soon as this class is imported on the page. It's enough to use nameless import:
     *
     * ```javascript
     * import '../../lib/Core/adapter/widget/BryntumWidgetAdapter.js';
     * ```
     *
     * **Note:** If you try to operate on widgets before adapter is set, you'll get an error.
     *
     * @param adapterClass Adapter class
     * @category Adapter
     */
    static set adapter(adapterClass) {
        this._adapter = new adapterClass();
    }

    static get adapter() {
        if (!this._adapter) {
            throw new Error('Widget adapter is required. Please import BryntumWidgetAdapter class. See docs here: https://www.bryntum.com/docs/grid/#Core/adapter/widget/BryntumWidgetAdapter');
        }

        return this._adapter;
    }

    /**
     * Checks if an adapter is assigned
     * @returns {Boolean}
     * @category Adapter
     * @readonly
     */
    static get hasAdapter() {
        return !!this._adapter;
    }

    //endregion

    //region Querying

    /**
     * Returns the widget with the specified id.
     * @param id Id of widget to find
     * @returns {Core.widget.Widget} The widget if any
     * @category Querying
     */
    static getById(id) {
        return IdHelper.get(id);
    }

    /**
     * Returns the Widget which owns the passed element (or event).
     * @param {HTMLElement|Event} element The element or event to start from
     * @param {String|Function} [type] The type of Widget to scan upwards for. The lowercase
     * class name. Or a filter function which returns `true` for the required Widget.
     * @param {HTMLElement|Number} [limit] The number of components to traverse upwards to find a
     * match of the type parameter, or the element to stop at.
     * @returns {Core.widget.Widget} The found Widget or null.
     * @category Querying
     * @typings any
     */
    static fromElement(element, type, limit) {
        return IdHelper.fromElement(element, type, limit);
    }

    //endregion

    //region Widgets

    /**
     * Create a widget.
     * @example
     * WidgetHelper.createWidget({
     *   type: 'button',
     *   icon: 'user',
     *   text: 'Edit user'
     * });
     * @param config Widget config
     * @returns {Object} The widget
     * @category Widgets
     */
    static createWidget(config = {}) {
        return this.adapter.createWidget(config);
    }

    /**
     * Appends a widget (array of widgets) to the DOM tree. If config is empty, widgets are appended to the DOM. To
     * append widget to certain position you can pass HTMLElement or its id as config, or as a config, that will be
     * applied to all passed widgets.
     *
     * Usage:
     *
     * ```javascript
     * // Will append button as last item to element with id 'container'
     * let [button] = WidgetHelper.append({ type : 'button' }, 'container');
     *
     * // Same as above, but will add two buttons
     * let [button1, button2] = WidgetHelper.append([
     *     { type : 'button' },
     *     { type : 'button' }
     *     ], { appendTo : 'container' });
     *
     * // Will append two buttons before element with id 'someElement'. Order will be preserved and all widgets will have
     * // additional class 'my-cls'
     * let [button1, button2] = WidgetHelper.append([
     *     { type : 'button' },
     *     { type : 'button' }
     *     ], {
     *         insertBefore : 'someElement',
     *         cls          : 'my-cls'
     *     });
     * ```
     *
     * @param {Object|Object[]} widget Widget config or array of such configs
     * @param {HTMLElement|String|Object} [config] Element (or element id) to which to append the widget or config to apply to all passed widgets
     * @returns {Core.widget.Widget[]} Array or widgets
     * @category Widgets
     */
    static append(widget, config) {
        widget = Array.isArray(widget) && widget || [widget];

        if (config instanceof HTMLElement || typeof config === 'string') {
            config = {
                appendTo : config
            };
        }

        // We want to fix position to insert into to keep order of passed widgets
        if (config.insertFirst) {
            const target = typeof config.insertFirst === 'string' ? document.getElementById(config.insertFirst) : config.insertFirst;

            if (target.firstChild) {
                config.insertBefore = target.firstChild;
            }
            else {
                config.appendTo = target;
            }
        }

        return this.adapter.appendWidgets(widget, config);
    }

    //endregion

    //region Popups

    // TODO: Implement openWindow
    // /**
    //  * Opens a window with specified widgets. Not implemented yet...
    //  * @param config
    //  * @returns {Object}
    //  * @category Popups
    //  */
    // static openWindow(config) {
    //     return this.adapter.openWindow(config);
    // }

    /**
     * Shows a popup (~tooltip) containing widgets connected to specified element.
     * @example
     * WidgetHelper.openPopup(element, {
     *   position: 'bottom center',
     *   items: [
     *      { widgetConfig }
     *   ]
     * });
     * @param element Element to connect popup to
     * @param config Config object, or string to use as html in popup
     * @returns {*|{close, widgets}}
     * @category Popups
     */
    static openPopup(element, config) {
        return this.adapter.openPopup(element, config);
    }

    /**
     * Shows a context menu connected to the specified element.
     * @example
     * WidgetHelper.showContextMenu(element, {
     *   items: [
     *      { id: 'addItem', icon: 'add', text: 'Add' },
     *      ...
     *   ],
     *   onItem: item => alert('Clicked ' + item.text)
     * });
     * @param {HTMLElement|Number[]} element Element (or a coordinate) to show the context menu for
     * @param {Object} config Context menu config, see example
     * @returns {*|{close}}
     * @category Popups
     */
    static showContextMenu(element, config) {
        return this.adapter.showContextMenu(element, config);
    }

    /**
     * Attached a tooltip to the specified element.
     * @example
     * WidgetHelper.attachTooltip(element, {
     *   text: 'Useful information goes here'
     * });
     * @param element Element to attach tooltip for
     * @param configOrText Tooltip config or tooltip string, see example and source
     * @returns {Object}
     * @category Popups
     */
    static attachTooltip(element, configOrText) {
        return this.adapter.attachTooltip(element, configOrText);
    }

    /**
     * Checks if element has tooltip attached
     *
     * @param element Element to check
     * @return {Boolean}
     * @category Popups
     */
    static hasTooltipAttached(element) {
        return this.adapter.hasTooltipAttached(element);
    }

    /**
     * Destroys any tooltip attached to an element, removes it from the DOM and unregisters any tip related listeners
     * on the element.
     *
     * @param element Element to remove tooltip from
     * @category Popups
     */
    static destroyTooltipAttached(element) {
        return this.adapter.destroyTooltipAttached(element);
    }

    //endregion

    //region Mask

    /**
     * Masks the specified element, showing a message in the mask.
     * @param element Element to mask
     * @param msg Message to show in the mask
     * @category Mask
     */
    static mask(element, msg) {
        return this.adapter.mask(element, msg);
    }

    /**
     * Unmask the specified element.
     * @param element
     * @category Mask
     */
    static unmask(element, close = true) {
        this.adapter.unmask(element);
    }

    //endregion

    //region Toast

    /**
     * Show a toast
     * @param {String} msg message to show in the toast
     * @category Mask
     */
    static toast(msg) {
        this.adapter.toast(msg);
    }

    //endregion

    //region Style

    static set defaultStyle(style) {
        this.adapter.defaultStyle = style;
    }

    static get defaultStyle() {
        return this.adapter.defaultStyle;
    }

    //endregion
}
