import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';
import Widget from './Widget.js';
import WidgetHelper from '../helper/WidgetHelper.js';
import ObjectHelper from '../helper/ObjectHelper.js';
import Layout from './layout/Layout.js';
import DomHelper from '../helper/DomHelper.js';
import './Ripple.js';
import Bag from '../util/Bag.js';

/**
 * @module Core/widget/Container
 */

const
    emptyObject = Object.freeze({}),
    returnWeight = i => i.weight,
    sortByWeight = (a, b) => ((a.weight || 0) - (b.weight || 0));

/**
 * Widget that can contain other widgets. Layout is flexbox by default, see the {@link #config-layout} config.
 *
 * ```javascript
 * // create a container with two widgets
 * let container = new Container({
 *     items : [
 *         { type : 'text', label : 'Name' },
 *         { type : 'number', label : 'Score' }
 *     ]
 * });
 * ```
 *
 * Containers can have child widgets added, or removed during their lifecycle to accommodate business needs.
 *
 * For example:
 *
 *  ```javascript
 *  myTaskPopup.on({
 *      beforeShow() {
 *          if (task.type === task.MASTER) {
 *              // Insert the childTask multiselect before the masterTask field
 *              myPopyup.insert(childTaskMultiselect, masterTaskField)
 *
 *              // We don't need this for master tasks
 *              myPopup.remove(masterTaskField);
 *          }
 *          else {
 *              // Insert the masterTask combo before the childTask multiselect
 *              myPopyup.insert(masterTaskField, childTaskMultiselect)
 *
 *              // We don't need this for child tasks
 *              myPopup.remove(childTaskMultiselect);
 *          }
 *      }
 *  });
 * ```
 *
 * @extends Core/widget/Widget
 * @classType container
 * @externalexample widget/Container.js
 */
export default class Container extends Widget {

    static get $name() {
        return 'Container';
    }

    static get defaultConfig() {
        return {
            /**
             * An array of Widgets or typed Widget config objects.
             *
             * If configured as an Object, the property names are used as the child component's
             * {@link Core.widget.Widget#config-ref ref} name, and the value is the child component's config object.
             *
             * ```javascript
             *  new Panel({
             *      title    : 'Test Panel',
             *      floating : true,
             *      centered : true,
             *      width    : 600,
             *      height   : 400,
             *      layout   : 'fit',
             *      items    : {
             *          tabs : {
             *              type : 'tabpanel',
             *              items : {
             *                  general : {
             *                      title : 'General',
             *                      html  : 'General content'
             *                  },
             *                  details : {
             *                      title : 'Details',
             *                      html  : 'Details content'
             *                  }
             *              }
             *          }
             *      }
             *  }).show();
             * ```
             *
             * @config {Object[]|Core.widget.Widget[]|Object}
             */
            items : null,

            /**
             * Synonym for the {@link #config-items} config option.
             * @config {Object[]|Core.widget.Widget[]|Object}
             * @deprecated 2.1
             */
            widgets : null,

            /**
             * A config object containing default settings to apply to all child widgets.
             * @config {Object}
             */
            defaults : null,

            defaultType : 'widget',

            /**
             * The CSS style properties to apply to the {@link Core.widget.Widget#property-contentElement}.
             *
             * By default, a Container's {@link Core.widget.Widget#property-contentElement} uses flexbox layout, so this config
             * may contain the following properties:
             *
             * - <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/flex-direction">flexDirection</a> default '`row`'
             * - <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/flex-wrap">flexWrap</a>
             * - <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/flex-flow">flexFlow</a>
             * - <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/justify-content">justifyContent</a>
             * - <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/align-items">alignItems</a>
             * - <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/align-content">alignContent</a>
             * - <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/place-content">placeContent</a>
             * @config {Object}
             */
            layoutStyle : null,

            /**
             * An optional CSS class to add to child items of this container.
             * @config {String}
             */
            itemCls : null,

            /**
             * The short name of a helper class which manages rendering and styling of child items.
             *
             * By default, the only special processing that is applied is that the Container class's
             * {@link #config-itemCls} is added to child items.
             *
             * Containers use CSS flexbox in its default configuration to arrange child items. You may either
             * use the {@link #config-layoutStyle} configuration to tune how child items are layed out,
             * or use one of the built in helper classes which include:
             *
             *  - `card` Child items are displayed one at a time, size to fit the {@link Core.widget.Widget#property-contentElement}
             * and are slid in from the side when activated.
             * @config {String}
             */
            layout : 'default',

            /**
             * An object containing named config objects which may be referenced by name in any {@link #config-items}
             * object. For example, a specialized {@link Core.widget.Menu Menu} subclass may have a `namedItems`
             * default value defined like this:
             *
             * ```javascript
             *  namedItems : {
             *      removeRow : {
             *          text : 'Remove row',
             *          onItem() {
             *              this.ownerGrid.remove(this.ownerGrid.selectedRecord);
             *          }
             *      }
             *  }
             * ```
             *
             * Then whenever that subclass is instantiated and configured with an {@link #config-items}
             * object, the items may be configured like this:
             *
             * ```javascript
             *  items : {
             *      removeRow : true,   // The referenced namedItem will be applied to this
             *      otherItemRef : {
             *          text : 'Option 2',
             *          onItem() {
             *          }
             *      }
             * }
             * ```
             * @config {Object}
             */
            namedItems : null
        };
    }

    // TODO: Remove when `widgets` is removed.
    setConfig(config, isConstructing) {
        // Assign deprecated widgets to items as early as possible to not have to have special handling in getters/setters
        if (config.widgets) {
            config.items = config.widgets;
        }

        super.setConfig(config, isConstructing);
    }

    startConfigure(config) {
        // Set a flag so that code can test for presence of items without tickling
        // any initial getter.
        const { items } = this;
        this.hasItems = Boolean(items && items.length);
        super.startConfigure(config);
    }

    /**
     * Removes the passed child/children from this Container.
     * @param  {...Core.widget.Widget} toRemove The child or children to remove.
     * @returns {Core.widget.Widget|Core.widget.Widget[]} All the removed items. An array if multiple items
     * were removed, otherwise, just the item removed.
     */
    remove(...toRemove) {
        let returnArray = true;

        if (toRemove.length === 1) {
            if (Array.isArray(toRemove[0])) {
                toRemove = toRemove[0];
            }
            else {
                returnArray = false;
            }
        }

        const
            me     = this,
            result = [];

        for (let i = 0; i < toRemove.length; i++) {
            const childToRemove = toRemove[i];

            if (me._items.includes(childToRemove)) {
                me._items.remove(childToRemove);
                me.layout.removeChild(childToRemove);
                result.push(childToRemove);
                me.unregisterReference(childToRemove);
            }
        }

        return returnArray ? result : result[0];
    }

    /**
     * Removes all children from this Container.
     * @returns {Core.widget.Widget[]} All the removed items.
     */
    removeAll() {
        return this.remove(this.items);
    }

    /**
     * Appends the passed widget/widgets to this Container.
     * @param  {...Core.widget.Widget} toAdd The child or children to add.
     * @returns {Core.widget.Widget|Core.widget.Widget[]} All the added widgets. An array if multiple items
     * were added, otherwise, just the item added.
     */
    add(...toAdd) {
        let returnArray = true;

        if (toAdd.length === 1) {
            if (Array.isArray(toAdd[0])) {
                toAdd = toAdd[0];
            }
            else {
                returnArray = false;
            }
        }

        const
            me     = this,
            result = [];

        for (let i = 0; i < toAdd.length; i++) {
            let childToAdd = toAdd[i];

            if (!(childToAdd instanceof Widget)) {
                childToAdd = me.createWidget(childToAdd);
            }
            else {
                childToAdd.parent = me;
            }

            if (!me._items.includes(childToAdd)) {
                me.registerReference(childToAdd);
                me._items.add(childToAdd);
                me.layout.appendChild(childToAdd);
                result.push(childToAdd);
            }
        }

        return returnArray ? result : result[0];
    }

    /**
     * Inserts the passed widget into this Container at the specified position.
     * @param  {Core.widget.Widget} toAdd The child to insert.
     * @param {Number|Core.widget.Widget} The index to insert at or the existing child to insert before.
     * @returns {Core.widget.Widget} The added widget.
     */
    insert(toAdd, index) {
        const
            me         = this,
            { _items } = me;

        if (toAdd instanceof Widget) {
            toAdd.parent = me;
        }
        else {
            toAdd = me.createWidget(toAdd);
        }

        if (_items.includes(index)) {
            index = me.indexOfChild(index);
        }

        index = Math.min(index, _items.count);

        const newValues = _items.values;
        newValues.splice(index, 0, toAdd);
        _items.values = newValues;

        // Register inserted item
        me.registerReference(toAdd);

        me.layout.insertChild(toAdd, index);

        return toAdd;
    }

    indexOfChild(child) {
        return this._items.indexOf(child);
    }

    set widgets(widgets) {
        console.warn('`widgets` was deprecated in 2.1, please change your code to use `items`');
        // Does nothing on purpose
    }

    get widgets() {
        console.warn('`widgets` was deprecated in 2.1, please change your code to use `items`');
        return this.items;
    }

    set items(items) {
        //<debug>
        if (!this.isConfiguring) {
            throw new Error('Child items may not be configured dynamically');
        }
        //</debug>

        this.configuredItems = items;
    }

    /**
     * The array of instantiated child Widgets.
     * @property {Core.widget.Widget[]}
     * @readonly
     */
    get items() {
        const
            me = this,
            items = me._items || (me._items = new Bag()),
            { configuredItems } = me;

        // Only convert the widget config objects into widgets
        // when we first access the widgets. This is more efficient
        // if this Container is never rendered.
        if (configuredItems) {
            me.configuredItems = false;

            if (Array.isArray(configuredItems)) {
                me.processItemsArray(configuredItems, items);
            }
            else if (configuredItems) {
                me.processItemsObject(configuredItems, me.namedItems, items);
            }

            // Allow child items to have a weight to establish their order
            if (items.some(returnWeight)) {
                items.sort(sortByWeight);
            }
            items.forEach(me.registerReference, me);
        }

        return items.values;
    }

    processItemsArray(items, result) {
        const len = items.length;

        let i, item;

        for (i = 0; i < len; i++) {
            item = items[i];

            if (item instanceof Widget) {
                item.parent = this;
            }
            else {
                item = this.createWidget(item);
            }

            // If the widget creation function returns null, nothing to add
            if (item) {
                result.add(item);
            }
        }
    }

    processItemsObject(items, namedItems = emptyObject, result) {
        let item, ref;

        for (ref in items) {
            item = items[ref];

            // It might come in as itemRef : false
            if (item) {
                // If this class or instance has a "namedItems" object
                // named by this ref, then use it as the basis for the item
                if (ref in namedItems) {
                    item = typeof item === 'object' ? ObjectHelper.merge(ObjectHelper.clone(namedItems[ref]), item) : namedItems[ref];
                }

                // Allow namedItems to be overridden with itemKey : false to indicate unavailability of an item
                if (item) {
                    if (item instanceof Widget) {
                        item.parent = this;
                    }
                    else {
                        item = this.createWidget(item);
                    }

                    // If the widget creation function returns null, nothing to add
                    if (item) {
                        //<debug>
                        if (item.ref) {
                            throw new Error('Named child items must not contain ref config. Its property name is its ref');
                        }
                        //</debug>

                        item.ref = ref;
                        result.add(item);
                    }
                }
            }
        }
    }

    registerReference(item) {
        const ref = item.ref || item.id;

        if (ref) {
            for (let current = this; current; current = current.parent) {
                if (!current.widgetMap[ref]) {
                    current.widgetMap[ref] = item;
                }
            }
        }
    }

    unregisterReference(item) {
        const ref = item.ref || item.id;

        if (ref) {
            for (let current = this; current; current = current.parent) {
                if (current.widgetMap[ref] === item) {
                    delete current.widgetMap[ref];
                }
            }
        }
    }

    /**
     * An object which contains a map of descendant widgets keyed by their {@link Core.widget.Widget#config-ref ref}.
     * All descendant widgets will be available in the `widgetMap`.
     * @property {Object}
     * @readonly
     * @typings any
     */
    get widgetMap() {
        if (!this._widgetMap) {
            this._widgetMap = {};
        }

        // Force evaluation of the configured items array by the getter
        // so that configs are promoted to widgets and the widgetMap
        // is created, and if there are widgets, populated.
        this._thisIsAUsedExpression(this.items);

        return this._widgetMap;
    }

    set record(record) {
        this._record = record;
        this.setValues(record, true);
    }

    /**
     * The {@link Core.data.Model record} to be applied to the fields contained in this Container.
     * Any descendant widgets of this Container with a `name` property will have its value set to the
     * value of that named property of the record. If no record is passed, the widget has its value
     * set to `null`.
     * @property {Core.data.Model}
     */
    get record() {
        return this._record;
    }

    getValues(filterFn) {
        const
            me      = this,
            widgets = me.queryAll(w => w.name),
            len     = widgets.length;

        const result = {};

        for (let i = 0; i < len; i++) {
            const widget = widgets[i],
                name     = widget.name;

            if (!filterFn || filterFn(widget)) {
                result[name] = widget.value;
            }
        }

        return result;
    }

    /**
     * Sets multiple flexbox settings which affect how child widgets are arranged.
     *
     * By default, Containers use flexbox layout, so this property
     * may contain the following properties:
     *
     * - <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/flex-direction">flexDirection</a> default '`row`'
     * - <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/flex-wrap">flexWrap</a>
     * - <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/flex-flow">flexFlow</a>
     * - <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/justify-content">justifyContent</a>
     * - <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/align-items">alignItems</a>
     * - <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/align-content">alignContent</a>
     * - <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/place-content">placeContent</a>
     * @property {Object}
     * @category Layout
     */
    set layoutStyle(layoutStyle) {
        DomHelper.applyStyle(this.contentElement, layoutStyle);
        this._layoutStyle = layoutStyle;
    }

    get layoutStyle() {
        return this._layoutStyle;
    }

    set layout(layout) {
        this._layout = Layout.getLayout(layout, this);
    }

    get layout() {
        return this._layout || (this._layout = new Layout());
    }

    // Items to iterate over
    get childItems() {
        return this.items;
    }

    /**
     * Iterate over all widgets in this container and below.
     *
     * *Note*: Due to this method aborting when the function returns
     * `false`, beware of using short form arrow functions. If the expression
     * executed evaluates to `false`, iteration will terminate.
     * @param {Function} fn A function to execute upon all descendant widgets.
     * Iteration terminates if this function returns `false`.
     * @param {Boolean} [deep=true] Pass as `false` to only consider immediate child widgets.
     * @returns {Boolean} Returns `true` if iteration was not aborted by a step returning `false`
     */
    eachWidget(fn, deep = true) {
        const
            widgets = this.childItems,
            length = widgets ? widgets.length : 0;

        for (let i = 0; i < length; i++) {
            const widget = widgets[i];

            // Abort if a call returns false
            if (fn(widget) === false) {
                return false;
            }
            if (deep && widget.eachWidget) {
                // Abort if a deep call returns false
                if (widget.eachWidget(fn, deep) === false) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Returns an array of all descendant widgets which the passed
     * filter function returns `true` for.
     * @param {Function} filter A function which, when passed a widget,
     * returns `true` to include the widget in the results.
     * @returns {Core.widget.Widget[]} All matching descendant widgets.
     */
    queryAll(filter) {
        const result = [];

        this.eachWidget(w => {
            if (filter(w)) {
                result.push(w);
            }
        });

        return result;
    }

    /**
     * Returns the first descendant widgets which the passed
     * filter function returns `true` for.
     * @param {Function} filter A function which, when passed a widget,
     * returns `true` to return the widget as the sole result.
     * @returns {Core.widget.Widget} The first matching descendant widget.
     */
    query(filter) {
        let result = null;

        this.eachWidget(w => {
            if (filter(w)) {
                result = w;
                return false;
            }
        });

        return result;
    }

    /**
     * Returns a directly contained widget by id
     * @param {String} id The widget id
     * @returns {Core.widget.Widget}
     */
    getWidgetById(id) {
        return this.widgetMap[id];
    }

    /**
     * This function is called prior to creating widgets, override it in subclasses to allow containers to modify the
     * configuration of each widget. When adding a widget to a container hierarchy each parent containers
     * `processWidgetConfig` will be called. Returning false from the function prevents the widget from being added at
     * all.
     */
    processWidgetConfig(widget) {

    }

    /**
     * This function converts a Widget config object into a Widget.
     * @param {Object} widget A Widget config object.
     * @internal
     */
    createWidget(widget) {
        const me = this;

        // A string becomes the defaultType (see below) with the html set to the string.
        if (typeof widget === 'string') {
            widget = {
                html : widget
            };
        }
        // An element is encapsulated by a Widget
        else if (widget.nodeType === 1) {
            widget = {
                element : widget,
                id      : widget.id
            };
        }

        // A contained Widget must know its parent, and knowing it during construction
        // is important, but we must not mutate incoming config objects.
        widget = Object.setPrototypeOf({
            parent : me
        }, widget);

        if (!widget.type) {
            widget.type = me.defaultType;
        }

        for (let ancestor = widget.parent; ancestor; ancestor = ancestor.parent) {
            if (ancestor.processWidgetConfig(widget) === false) {
                return null;
            }
        }

        if (me.trigger('beforeWidgetCreate', { widget }) === false) {
            return null;
        }

        return WidgetHelper.createWidget(ObjectHelper.assign({}, me.defaults, widget), me.defaultType || 'widget');
    }

    render() {
        this.layout.renderChildren();

        super.render(...arguments);
    }

    get focusElement() {
        const firstFocusable = this.query(this.defaultFocus || (w => w.isFocusable));

        if (firstFocusable) {
            return firstFocusable.focusElement;
        }
        return super.focusElement;
    }

    doDestroy() {
        // Only destroy the widgets if they have been instanced.
        if (!this.configuredItems && this.items) {
            this.items.forEach(widget => widget.destroy && widget.destroy());
        }

        super.doDestroy();
    }

    /**
     * Checks that all descendant fields are valid.
     * @returns {Boolean} Returns `true` if all contained fields are valid, otherwise `false`
     */
    get isValid() {
        let valid = true;

        this.eachWidget(widget => {
            if ('isValid' in widget && !widget.isValid) {
                return (valid = false);
            }
        }, true);

        return valid;
    }

    /**
     * Retrieves or sets all values from/to contained fields.
     * Accepts and returns a map, using name, ref or id (in that order) as keys.
     *
     * ```javascript
     * container.values = {
     *     firstName : 'Clark',
     *     surname : 'Kent'
     * };
     * ```
     *
     * @property {Object}
     */
    get values() {
        const values = {};

        this.eachWidget(widget => {
            if ('value' in widget) {
                values[widget.name || widget.ref || widget.id] = widget.value;
            }
        }, true);

        return values;
    }

    set values(values) {
        this.setValues(values);
    }

    /**
     * Returns `true` if currently setting values. Allows fields change highlighting to distinguishing between initially
     * setting values and later on changing values.
     * @property {boolean}
     */
    get isSettingValues() {
        // Fields query their parent, pass the question up in case containers are nested
        return this._isSettingValues || this.parent && this.parent.isSettingValues;
    }

    setValues(values, onlyName = false) {
        // Flag checked by Field to determine if it should highlight change or not (it should not in this case)
        this._isSettingValues = true;

        this.eachWidget(widget => {
            const
                hec = widget.highlightExternalChange,
                key = onlyName ? widget.name : (widget.name || widget.ref || widget.id);

            if ('value' in widget && key) {
                // Don't want a field highlight on mass change
                widget.highlightExternalChange = false;

                // Setting to null when value not matched clears field
                widget.value = (values && (key in values)) ? values[key] : null;

                widget.highlightExternalChange = hec;
            }

        }, true);

        this._isSettingValues = false;
    }
}

BryntumWidgetAdapterRegister.register('container', Container);
