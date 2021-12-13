import Model from '../../Core/data/Model.js';
import Localizable from '../../Core/localization/Localizable.js';
import DomHelper from '../../Core/helper/DomHelper.js';
import Events from '../../Core/mixin/Events.js';
import Widget from '../../Core/widget/Widget.js';
import WidgetHelper from '../../Core/helper/WidgetHelper.js';
import ObjectHelper from '../../Core/helper/ObjectHelper.js';

/**
 * @module Grid/column/Column
 */

/**
 * Base class for other column types, used if no type is specified on a column.
 *
 * ```javascript
 * const grid = new Grid({
 *   columns : [
 *      { field : 'name', text : 'Name' }, // Will use Column
 *      { type : 'number', field : 'age', text : 'Age' } // Will use NumberColumn
 *   ]
 * });
 * ```
 *
 * @extends Core/data/Model
 * @classType column
 * @mixes Core/mixin/Events
 * @mixes Core/localization/Localizable
 */
export default class Column extends Events(Localizable(Model)) {
    //region Config

    static get fields() {
        return [
            //region Common

            /**
             * Get/set header text
             * @member {String} text
             */

            /**
             * Text to display in the header
             * @config {String} text
             * @category Common
             */
            'text',

            /**
             * The {@link Core.data.Model Model} field name to read data from
             * @config {String} field
             * @category Common
             */
            'field',

            /**
             * Renderer function, used to format and style the content displayed in the cell. Return the cell text you want to display.
             * Can also affect other aspects of the cell, such as styling.
             * @param {Object} renderData Object containing renderer parameters
             * @param {HTMLElement} [renderData.cellElement] Cell element, for adding CSS classes, styling etc. Can be
             * null in case of export
             * @param {*} renderData.value Value to be displayed in the cell
             * @param {Core.data.Model} renderData.record Record for the row
             * @param {Grid.column.Column} renderData.column This column
             * @param {Grid.view.Grid} renderData.grid This grid
             * @param {Grid.row.Row} [renderData.row] Row object. Can be null in case of export
             * @param {Object} [renderData.size] Set `size.height` to specify the desired row height for the current row.
             * Largest specified height will be used, including the configured {@link Grid/view/Grid#config-rowHeight}.
             * Can be null in case of export
             * @param {Boolean} [renderData.isExport] True if record is being exported to allow special handling during export
             * @config {Function} renderer
             * @category Common
             */
            'renderer',

            /**
             * Column width. If value is Number then width is in pixels
             * @config {Number|String} width
             * @category Common
             */
            'width',

            /**
             * Gets or sets the column flex weight
             * @member {String} flex
             */

            /**
             * Column width as a flex weight. All columns with flex specified divide the available space (after
             * subtracting fixed widths) between them according to the flex value. Columns that have flex 2 will be
             * twice as wide as those with flex 1 (and so on)
             * @config {Number} flex
             * @category Common
             */
            'flex',

            //endregion

            //region Interaction

            /**
             * Specifies if this column should be editable, and define which editor to use for editing cells in the
             * column (if {@link Grid/feature/CellEdit CellEdit} feature is enabled). Editor refers to {@link #config-field} for a data source. If
             * record has method set + capitalized field, method will be called, e.g. if record has method named
             * `setFoobar` and this config is `foobar`, then instead of `record.foobar = value`,
             * `record.setFoobar(value)` will be called.
             * @config {String|Object|Boolean} editor
             * @category Interaction
             */
            { name : 'editor', defaultValue : {} },

            /**
             * A function which is called when a cell edit is requested to finish.
             *
             * This may be an `async` function which performs complex validation. The edit will not
             * complete until it returns `false` to mean the edit cannot be finished, or `true` to go
             * ahead and complete.
             *
             * @param {Object} context An object describing the state of the edit at completion request time.
             * @param {Core.widget.Field} context.inputField The field configured as the column's `editor`.
             * @param {Core.data.Model} context.record The record being edited.
             * @param {*} context.oldValue The old value of the cell.
             * @param {*} context.value The new value of the cell.
             * @param {Grid.view.Grid} context.grid The host grid.
             * @param {Object} context.editorContext The {@link Grid.feature.CellEdit CellEdit} context object.
             * @param {Grid.column.Column} context.editorContext.column The column being edited.
             * @param {Core.data.Model} context.editorContext.record The record being edited.
             * @param {HTMLElement} context.editorContext.cell The cell element hosting the editor.
             * @param {Core.widget.Editor} context.editorContext.editor The floating Editor widget which is hosting the input field.
             * @config {Function} finalizeCellEdit
             * @category Interaction
             */
            'finalizeCellEdit',

            /**
             * Setting this option means that pressing the `ESCAPE` key after editing the field will
             * revert the field to the value it had when the edit began. If the value is _not_ changed
             * from when the edit started, the input field's {@link Core.widget.Field#config-clearable}
             * behaviour will be activated. Finally, the edit will be canceled.
             * @config {Boolean} revertOnEscape
             * @default true
             * @category Interaction
             */
            { name : 'revertOnEscape', defaultValue : true },

            /**
             * How to handle a request to complete a cell edit in this column if the field is invalid.
             * There are three choices:
             *  - `block` The default. The edit is not exited, the field remains focused.
             *  - `allow` Allow the edit to be completed.
             *  - `revert` The field value is reverted and the edit is completed.
             * @config {String} invalidAction
             * @default 'block'
             * @category Interaction
             */
            { name : 'invalidAction', defaultValue : 'block' },

            /**
             * Allow sorting of data in the column. You can pass true/false to enable/disable sorting, or provide a
             * custom sorting function, or a config object for a {@link Core.util.CollectionSorter}
             *
             * ```javascript
             * const grid = new Grid({
             *     columns : [
             *          {
             *              // Disable sorting for this column
             *              sortable : false
             *          },
             *          {
             *              // Custom sorting for this column
             *              sortable : function(user1, user2) {
             *                  return user1.name < user2.name ? -1 : 1;
             *              }
             *          },
             *          {
             *              // A config object for a Core.util.CollectionSorter
             *              sortable : {
             *                  property         : 'someField',
             *                  direction        : 'DESC',
             *                  useLocaleCompare : 'sv-SE'
             *              }
             *          }
             *     ]
             * });
             * ```
             *
             * @config {Boolean/Function/Object} sortable
             * @default true
             * @category Interaction
             */
            { name : 'sortable', defaultValue : true },

            /**
             * Allow searching in the column (respected by QuickFind and Search features)
             * @config {Boolean} searchable
             * @default true
             * @category Interaction
             */
            { name : 'searchable', defaultValue : true },

            /**
             * Allow filtering data in the column (if Filter or FilterBar feature is enabled). Also allows passing a
             * custom filtering function that will be called for each record with a single argument of format
             * { value, record, [operator] }. Returning `true` from the function includes the record in the filtered set.
             *
             * ```
             * const grid = new Grid({
             *     columns : [
             *          {
             *              field : 'name',
             *              // Disable filtering for this column
             *              filterable : false
             *          },
             *          {
             *              field : 'age',
             *              // Custom filtering for this column
             *              filterable: ({ value, record }) => Math.abs(record.age - value) < 10
             *          },
             *          {
             *              field : 'city',
             *              // Filtering for a value out of a list of values
             *              filterable: {
             *                  filterField : {
             *                      type  : 'combo',
             *                      value : '',
             *                      items : [
             *                          'Paris',
             *                          'Dubai',
             *                          'Moscow',
             *                          'London',
             *                          'New York'
             *                      ]
             *                  }
             *              }
             *          }
             *     ]
             * });
             * ```
             *
             * @config {Boolean/Function} filterable
             * @default true
             * @category Interaction
             */
            { name : 'filterable', defaultValue : true },

            /**
             * Allow column visibility to be toggled through UI
             * @config {Boolean} hideable
             * @default true
             * @category Interaction
             */
            { name : 'hideable', defaultValue : true },

            /**
             * Set to false to prevent this column header from being dragged
             * @config {Boolean} draggable
             * @category Interaction
             */
            { name : 'draggable', defaultValue : true },

            /**
             * Set to false to prevent grouping by this column
             * @config {Boolean} groupable
             * @category Interaction
             */
            { name : 'groupable', defaultValue : true },

            /**
             * Set to `false` to prevent the column from being drag-resized when the ColumnResize plugin is enabled.
             * @config {Boolean} resizable
             * @default true
             * @category Interaction
             */
            { name : 'resizable', defaultValue : true },

            //endregion

            //region Rendering

            /**
             * Renderer function for group headers (when using Group feature).
             * @param {Object} renderData
             * @param {HTMLElement} renderData.cellElement Cell element, for adding CSS classes, styling etc.
             * @param {*} renderData.groupRowFor Current group value
             * @param {Core.data.Model} renderData.record Record for the row
             * @param {Core.data.Model[]} renderData.groupRecords Records in the group
             * @param {Grid.column.Column} renderData.column Current rendering column
             * @param {Grid.column.Column} renderData.groupColumn Column that the grid is grouped by
             * @param {Number} renderData.count Number of records in the group
             * @param {Grid.view.Grid} renderData.grid This grid
             * @config {Function} groupRenderer
             * @returns {String} The header grouping text
             * @category Rendering
             */
            'groupRenderer',

            /**
             * Renderer function for the column header.
             * @param {Object} renderData
             * @param {Grid.column.Column} renderData.column This column
             * @param {HTMLElement} renderData.headerElement The header element
             * @config {Function} headerRenderer
             * @category Rendering
             */
            'headerRenderer',

            /**
             * Renderer function for cell tooltips header (used with CellTooltip feature). Specify false to prevent
             * tooltip for that column.
             * @param {HTMLElement} cellElement Cell element
             * @param {Core.data.Model} record Record for cell row
             * @param {Grid.column.Column} column Cell column
             * @param {CellTooltip} cellTooltip Feature instance, used to set tooltip content async
             * @param {MouseEvent} event The event that triggered the tooltip
             * @config {Function} tooltipRenderer
             * @category Rendering
             */
            'tooltipRenderer',

            /**
             * CSS class added to each cell in this column
             * @config {String} cellCls
             * @category Rendering
             */
            'cellCls',

            /**
             * CSS class added to the header of this column
             * @config {String} cls
             * @category Rendering
             */
            'cls',

            /**
             * Get/set header icon class
             * @member {String} icon
             */

            /**
             * Icon to display in header. Specifying an icon will render a `<i>` element with the icon as value for the
             * class attribute
             * @config {String} icon
             * @category Rendering
             */
            'icon',

            //endregion

            //region Layout

            /**
             * Text align (left, center, right)
             * @config {String} align
             * @category Layout
             */
            'align',

            /**
             * Column minimal width. If value is Number then minimal width is in pixels
             * @config {Number|String} minWidth
             * @default 60
             * @category Layout
             */
            { name : 'minWidth', defaultValue : 60 },

            /**
             * Get/set columns hidden state. Specify `true` to hide the column, `false` to show it.
             * @member {Boolean} hidden
             */

            /**
             * Hide the column from start
             * @config {Boolean} hidden
             * @category Layout
             */
            { name : 'hidden', defaultValue : false },

            /**
             * Convenient way of putting a column in the "locked" region. Same effect as specifying region: 'locked'.
             * If you have defined your own regions (using {@link Grid.view.Grid#config-subGridConfigs}) you should use
             * {@link #config-region} instead of this one.
             * @config {Boolean} locked
             * @default false
             * @category Layout
             */
            { name : 'locked' },

            /**
             * Region (part of the grid, it can be configured with multiple) where to display the column. Defaults to
             * {@link Grid.view.Grid#config-defaultRegion}.
             * @config {String} region
             * @category Layout
             */
            { name : 'region' },

            //endregion

            // region Menu

            /**
             * Show column picker for the column
             * @config {Boolean} showColumnPicker
             * @default true
             * @category Menu
             */
            { name : 'showColumnPicker', defaultValue : true },

            /**
             * false to prevent showing a context menu on the column header element
             * @config {Boolean} enableHeaderContextMenu
             * @default true
             * @category Menu
             */
            { name : 'enableHeaderContextMenu', defaultValue : true },

            /**
             * false to prevent showing a context menu on the cell elements in this column
             * @config {Boolean} enableCellContextMenu
             * @default true
             * @category Menu
             */
            { name : 'enableCellContextMenu', defaultValue : true },

            /**
             * Extra items to show in the header context menu for this column
             * @config {Object[]} headerMenuItems
             * @category Menu
             */
            'headerMenuItems',

            /**
             * Extra items to show in the cell context menu for this column
             * @config {Object[]} cellMenuItems
             * @category Menu
             */
            'cellMenuItems',

            //endregion

            //region Summary

            /**
             * Summary type (when using Summary feature). Valid types are:
             * <dl class="wide">
             * <dt>sum <dd>Sum of all values in the column
             * <dt>add <dd>Alias for sum
             * <dt>count <dd>Number of rows
             * <dt>countNotEmpty <dd>Number of rows containing a value
             * <dt>average <dd>Average of all values in the column
             * <dt>function <dd>A custom function, used with store.reduce. Should take arguments (sum, record)
             * </dl>
             * @config {String} sum
             * @category Summary
             */
            'sum',

            /**
             * Summary configs, use if you need multiple summaries per column. Replaces {@link #config-sum} and
             * {@link #config-summaryRenderer} configs. Accepts an array of objects with the following fields:
             * * sum - Matching {@link #config-sum}
             * * renderer - Matching {@link #config-summaryRenderer}
             * * seed - Initial value when using a function as `sum`
             * @config {Object[]} summaries
             * @category Summary
             */
            'summaries',

            /**
             * Renderer function for summary (when using Summary feature). The renderer is called with the calculated
             * summary as only argument.
             * @config {Function} summaryRenderer
             * @category Summary
             */
            'summaryRenderer',

            //region Misc

            /**
             * Column settings at different responsive levels, see responsive demo under examples/
             * @config {Object} responsiveLevels
             * @category Misc
             */
            'responsiveLevels',

            /**
             * Tags, may be used by ColumnPicker feature for grouping columns by tag in the menu
             * @config {String[]} tags
             * @category Misc
             */
            'tags',

            /**
             * Column config to apply to normal config if viewed on a touch device
             * @config {Object} touchConfig
             * @category Misc
             */
            'touchConfig',

            /**
             * When using the tree feature, exactly one column should specify { tree: true }
             * @config {Boolean} tree
             * @category Misc
             */
            'tree',

            /**
             * Determines which type of filtering to use for the column. Usually determined by the column type used,
             * but may be overridden by setting this field.
             * @config {String} filterType
             * @category Misc
             */
            'filterType',

            /**
             * By default, any rendered column text content is HTML-encoded. Set this flag to `false` disable this and allow rendering html elements
             * @config {Boolean} htmlEncode
             * @default true
             * @category Misc
             */
            { name : 'htmlEncode', defaultValue : true },

            /**
             * Set to `true`to automatically call DomHelper.sync for html returned from a renderer. Should in most cases
             * be more performant than replacing entire innerHTML of cell and also allows CSS transitions to work. Has
             * no effect unless `htmlEncode` is enabled. Returned html must contain a single root element (that can have
             * multiple children). See PercentColumn for example usage.
             * @config {Boolean} autoSyncHtml
             * @default false
             * @category Misc
             */
            { name : 'autoSyncHtml', defaultValue : false },

            'type',

            /**
             * Set to `true` to have the {@link Grid.feature.CellEdit CellEdit} feature update the record being
             * edited live upon field edit instead of when editing is finished by using `TAB` or `ENTER`
             */
            { name : 'instantUpdate', defaultValue : false },

            { name : 'repaintOnResize', defaultValue : false },

            /**
            * An optional query selector to select a sub element within the cell being
            * edited to align a cell editor's `X` position and `width` to.
            * @config {String} editTargetSelector
            */
            'editTargetSelector',

            /**
             * Used by the Export feature. Set to `false` to omit a column from an exported dataset
             * @config {Boolean} exportable
             * @default true
             */
            { name : 'exportable', defaultValue : true },

            /**
             * Column type which will be used by {@link Grid.util.TableExporter}. See list of available types in TableExporter
             * doc. Returns undefined by default, which means column type should be read from the record field.
             * @config {String} exportedType
             */
            { name : 'exportedType' }
            //endregion
        ];
    }

    // prevent undefined fields from being exposed, to simplify spotting errors
    static get autoExposeFields() {
        return false;
    }

    //endregion

    //region Init

    construct(data, store) {
        const me = this;

        me.masterStore = store;

        // Store might be an array
        if (store) {
            me._grid = Array.isArray(store) ? store[0].grid : store.grid;
        }

        me.localizableProperties = data.localizableProperties || ['text'];

        if (data.localeClass) {
            me.localeClass = data.localeClass;
        }

        super.construct(data, store, null, false);

        me.processConfiguredListeners(data.listeners);

        // Default value for region is assigned by the ColumnStore in createRecord(), same for `locked`

        // Allow field : null if the column does not rely on a record field.
        // For example the CheckColumn when used by GridSelection.
        if (!('field' in me.data)) {
            me.field = '_' + (me.type || '') + (++Column.emptyCount);
            me.noFieldSpecified = true;
        }

        // If our field is a dot separated path, we must use ObjectHelper.getPath to extract our value
        me.hasComplexMapping = me.field && me.field.includes('.');

        if (!me.width && !me.flex && !me.children) {
            // Set the width silently because we're in construction.
            me.set({
                width : Column.defaultWidth,
                flex  : null
            }, null, true);
        }
    }

    remove() {
        const
            { subGrid, grid } = this,
            focusedCell       = subGrid && grid && grid.focusedCell;

        // Prevent errors when removing the column that the owning grid has registered as focused.
        if (focusedCell && focusedCell.columnId === this.id) {

            // Focus is in the grid, navigate before column is removed
            if (document.activeElement === grid) {
                grid.navigateRight();
            }
            // Focus not in the grid, bump the focused cell pointer to the ext visible column.
            else {
                focusedCell.columnId = subGrid.columns.getAdjacentVisibleLeafColumn(this.id, true, true).id;
            }
        }
        super.remove();
    }

    /**
     * Extracts the value from the record specified by this Column's {@link #config-field} specification.
     *
     * This will work if the field is a dot-separated path to access fields in associated records, eg
     *
     * ```javascript
     *  field : 'resource.calendar.name'
     * ```
     *
     * **Note:** This is the raw field value, not the value returned by the {@link #config-renderer}.
     * @param {Core.data.Model} record The record from which to extract the field value.
     * @returns {*} The value of the referenced field if any.
     */
    getRawValue(record) {
        const me = this;

        if (me.hasComplexMapping) {
            return ObjectHelper.getPath(record, me.field);
        }
        return record[me.field] || record.get(me.field);
    }

    // Create an ownership hierarchy which links columns up to their SubGrid if no owner injected.
    get owner() {
        return this._owner || this.subGrid;
    }

    set owner(owner) {
        this._owner = owner;
    }

    get nextVisibleSibling() {
        // During move from one region to another, nextSibling might not be wired up to the new next sibling in region.
        // (Because the order in master store did not change)
        const region = this.region;

        let next = this.nextSibling;
        while (next && (next.hidden || next.region !== region)) {
            next = next.nextSibling;
        }
        return next;
    }

    get isLastInSubGrid() {
        return !this.nextVisibleSibling && (!this.parent || this.parent.isLastInSubGrid);
    }

    /**
     * The header element for this Column. *Only available after the grid has been rendered*.
     *
     * **Note that column headers are rerendered upon mutation of Column values, so this
     * value is volatile and should not be cached, but should be read whenever needed.**
     * @property {HTMLElement}
     * @readonly
     */
    get element() {
        return this.grid.getHeaderElement(this);
    }

    /**
     * The text wrapping element for this Column. *Only available after the grid has been rendered*.
     *
     * This is the full-width element which *contains* the text-bearing element and any icons.
     *
     * **Note that column headers are rerendered upon mutation of Column values, so this
     * value is volatile and should not be cached, but should be read whenever needed.**
     * @property {HTMLElement}
     * @readonly
     */
    get textWrapper() {
        return DomHelper.getChild(this.element, '.b-grid-header-text');
    }

    /**
     * The text containing element for this Column. *Only available after the grid has been rendered*.
     *
     * **Note that column headers are rerendered upon mutation of Column values, so this
     * value is volatile and should not be cached, but should be read whenever needed.**
     * @property {HTMLElement}
     * @readonly
     */
    get textElement() {
        return DomHelper.down(this.element, '.b-grid-header-text-content');
    }

    /**
     * The child element into which content should be placed. This means where any
     * contained widgets such as filter input fields should be rendered. *Only available after the grid has been rendered*.
     *
     * **Note that column headers are rerendered upon mutation of Column values, so this
     * value is volatile and should not be cached, but should be read whenever needed.**
     * @property {HTMLElement}
     * @readonly
     */
    get contentElement() {
        return DomHelper.down(this.element, '.b-grid-header-children');
    }

    /**
     * The Field to use as editor for this column
     * @private
     * @readonly
     */
    get editor() {
        let editor = this.data.editor;

        if (editor && !(editor instanceof Widget)) {
            // Give frameworks a shot at injecting their own editor, wrapped as a widget
            const result = this.grid.processCellEditor({ editor, field : this.field });

            if (result) {
                // Use framework editor
                editor = this.data.editor = result.editor;
            }
            else {
                if (typeof editor === 'string') {
                    editor = {
                        type : editor
                    };
                }

                editor = this.data.editor = WidgetHelper.createWidget(Object.assign(this.defaultEditor, editor, {
                    owner : this.grid
                }));
            }
        }

        return editor;
    }

    set editor(editor) {
        this.data.editor = editor;
    }

    /**
     * A config object specifying the editor to use to edit this column.
     * @private
     * @readonly
     */
    get defaultEditor() {
        return {
            type : 'textfield',
            name : this.field
        };
    }

    /**
     * Default settings for the column, applied in constructor. None by default, override in subclass.
     * @member {Object} defaults
     * @returns {Object}
     * @readonly
     */
    //get defaults() {
    //    return {};
    //}
    //endregion

    //region Properties

    static get type() {
        return 'column';
    }

    static get text() {
        return this.defaultValues.text;
    }

    get grid() {
        return this._grid || this.parent && this.parent.grid;
    }

    get locked() {
        return this.data.region === 'locked';
    }

    set locked(locked) {
        this.region = locked ? 'locked' : 'normal';
    }

    // parent headers cannot be sorted by
    get sortable() {
        return this.isLeaf && this.data.sortable;
    }

    set sortable(sortable) {
        this.set('sortable', sortable);
    }

    // parent headers cannot be grouped by
    get groupable() {
        return this.isLeaf && this.data.groupable;
    }

    set groupable(groupable) {
        this.set('groupable', groupable);
    }

    //endregion

    //region Show/hide

    /**
     * Hides this column.
     */
    hide(silent = false) {
        const
            me     = this,
            parent = me.parent;

        // Reject non-change
        if (!me.hidden) {
            me.hidden = true;

            if (parent && !parent.isRoot) {
                // check if all sub columns are hidden, if so hide parent
                const anyVisible = parent.children.some(child => child.hidden !== true);
                if (!anyVisible && !parent.hidden) {
                    silent = true; // hiding parent will trigger event
                    parent.hide();
                }
            }

            if (me.children) {
                me.children.forEach(child => child.hide(true));
            }

            if (!silent) {
                me.stores.forEach(store => store.trigger('hideColumn'));
            }
        }
    }

    /**
     * Shows this column.
     */
    show(silent = false) {
        const me     = this,
            parent = me.parent;

        // Reject non-change
        if (me.hidden) {
            me.hidden = false;

            if (parent && parent.hidden) {
                parent.show();
            }

            if (me.children) {
                me.children.forEach(child => child.show(true));
            }

            // event is triggered on chained stores
            if (!silent) {
                me.stores.forEach(store => store.trigger('showColumn'));
            }
        }
    }

    /**
     * Toggles the column visibility.
     * @param {Boolean} force Set to true (visible) or false (hidden) to force a certain state
     */
    toggle(force = null) {
        if ((this.hidden && force === undefined) || force === true) return this.show();
        if ((!this.hidden && force === undefined) || force === false) return this.hide();
    }

    //endregion

    //region Index & id

    /**
     * Generates an id for the column when none is set. Generated ids are 'col1', 'col2' and so on. If a field is
     * specified (as it should be in most cases) the field name is used instead: 'name1', 'age2' ...
     * @private
     * @returns {String}
     */
    generateId() {
        if (!Column.generatedIdIndex) Column.generatedIdIndex = 0;

        return (this.field ? this.field.replace(/\./g, '-') : 'col') + (++Column.generatedIdIndex);
    }

    /**
     * Index among all flattened columns
     * @property {Number}
     * @readOnly
     * @internal
     */
    get allIndex() {
        return this.masterStore.indexOf(this);
    }

    //endregion

    //region Width

    /**
     * Get/set columns width in px. If column uses flex, width will be undefined.
     * Setting a width on a flex column cancels out flex.
     *
     * **NOTE:** Grid might be configured to always stretch the last column, in which case the columns actual width
     * might deviate from the configured width.
     *
     * ```javascript
     * let grid = new Grid({
     *     appendTo : 'container',
     *     height   : 200,
     *     width    : 400,
     *     columns  : [{
     *         text  : 'First column',
     *         width : 100
     *     }, {
     *         text  : 'Last column',
     *         width : 100 // last column in the grid is always stretched to fill the free space
     *     }]
     * });
     *
     * grid.columns.last.element.offsetWidth; // 300 -> this points to the real element width
     * ```
     * @property {Number|String}
     */
    get width() {
        return this.data.width;
    }

    set width(width) {
        const data = { width };
        if (width && ('flex' in this.data)) {
            data.flex = null; // remove flex when setting width to enable resizing flex columns
        }
        this.set(data);
    }

    // Private, only used in tests where standalone Headers are created with no grid
    // from which to lookup the associate SubGrid.
    set subGrid(subGrid) {
        this._subGrid = subGrid;
    }

    /**
     * Get the SubGrid to which this column belongs
     * @property {Grid.view.SubGrid}
     * @readonly
     */
    get subGrid() {
        return  this._subGrid || (this.grid ? this.grid.getSubGridFromColumn(this) : undefined);
    }

    /**
     * Get the element for the SubGrid to which this column belongs
     * @property {HTMLElement}
     * @readonly
     * @private
     */
    get subGridElement() {
        return this.subGrid.element;
    }

    // Returns size in pixels for measured value
    measureSize(value) {
        return DomHelper.measureSize(value, this.subGrid ? this.subGrid.element : undefined);
    }

    // This method is used to calculate minimum row width for edge and safari
    // It calculates minimum width of the row taking column hierarchy into account
    calculateMinWidth() {
        const
            me       = this,
            width    = me.measureSize(me.width),
            minWidth = me.measureSize(me.minWidth);

        let minChildWidth = 0;

        if (me.children) {
            minChildWidth = me.children.reduce((result, column) => {
                return result + column.calculateMinWidth();
            }, 0);
        }

        return Math.max(width, minWidth, minChildWidth);
    }

    /**
     * Resizes the column to match the widest string in it. By default it also measures the column header, this
     * behaviour can be configured by setting {@link Grid.view.Grid#config-resizeToFitIncludesHeader}.
     *
     * Called internally when you double click the edge between
     * column headers, but can also be called programmatically. For performance reasons it is limited to checking 1000
     * rows surrounding the current viewport.
     */
    resizeToFitContent() {
        const me         = this,
            {
                grid,
                renderer,
                defaultRenderer,
                element
            } = me,
            rowManager  = grid.rowManager,
            store       = grid.store,
            count       = store.count,
            useRenderer = renderer || defaultRenderer;

        if (count <= 0) return;

        const cellElement       = grid.element.querySelector(`.b-grid-cell[data-column-id=${me.id}]`),
            originalStyle     = cellElement.cssText,    // Renderer might overwrite
            originalClassName = cellElement.className;  // Renderer might overwrite

        let maxWidth = 0,
            start, end, i, record, value;

        // Measure header unless configured not to
        if (grid.resizeToFitIncludesHeader) {
            const style = window.getComputedStyle(element);
            // Header always in view, use its element
            maxWidth = DomHelper.measureText(
                element.innerText,
                element,
                false,
                element.parentElement
            ) + parseInt(style.paddingLeft); // Seems to miss the padding, have not found why

            // Remove measuring element, it messes some styling rules up if left in DOM
            element.parentElement.offScreenDiv.parentElement.remove();
        }

        // If it's a very large dataset, measure the maxWidth of the field in the 1000 rows
        // surrounding the rendered block.
        if (count > 1000) {
            start = Math.max(Math.min(rowManager.topIndex + rowManager.rowCount / 2 - 500, count - 1000), 0);
            end = start + 1000;
        }
        else {
            start = 0;
            end = count;
        }

        for (i = start; i < end; i++) {
            record = store.getAt(i);
            value = me.getRawValue(record);

            if (useRenderer) {
                value = useRenderer.call(me, {
                    cellElement,
                    rowElement        : cellElement.parentNode,
                    value,
                    record,
                    column            : me,
                    size              : { height : record.rowHeight || 0 },
                    grid,
                    row               : {},
                    updatingSingleRow : false
                });

                // Cell renderer is allowed to direction manipulate the cell element, if so it wont return a value
                value = value == null ? cellElement.innerHTML : String(value);
            }

            if (value) {
                // Measure withing the SubGrid, allows more css rules to apply. Not measuring inside rows sine that might get to costly performance wise
                maxWidth = Math.max(
                    maxWidth,
                    DomHelper.measureText(
                        value,
                        cellElement,
                        !(me.htmlEncode && !me.disableHtmlEncode),
                        me.subGrid.element
                    )
                );
            }
        }

        // Restore top cell which may be mutated by the repeated call of the renderer.
        cellElement.className = originalClassName;
        cellElement.cssText = originalStyle;

        return me.width = maxWidth;
    }

    //endregion

    //region State

    /**
     * Get column state, used by State mixin
     * @private
     */
    getState() {
        // TODO: exclude those with value = default?
        const me    = this,
            state = {
                [me.flex ? 'flex' : 'width'] : me.flex ? me.flex : me.width,
                id                           : me.id,
                hidden                       : me.hidden,
                index                        : me.allIndex,
                region                       : me.region,
                filterable                   : me.filterable,
                text                         : me.text,
                locked                       : me.locked
            };

        if (me.children) state.children = me.children.map(child => child.getState());

        return state;
    }

    /**
     * Apply state to column, used by State mixin
     * @private
     */
    applyState(state) {
        const me = this;

        me.beginBatch();

        if ('locked' in state) {
            me.locked = state.locked;
        }

        if ('minWidth' in state) {
            me.minWidth = state.minWidth;
        }

        if ('width' in state) {
            me.width = state.width;
        }

        if ('flex' in state) {
            me.flex = state.flex;
        }

        if ('width' in state && me.flex) {
            me.flex = undefined;
        }
        else if ('flex' in state && me.width) {
            me.width = undefined;
        }

        if ('text' in state) {
            me.text = state.text;
        }

        if ('region' in state) {
            me.region = state.region;
        }

        if ('renderer' in state) {
            me.renderer = state.renderer;
        }

        if ('filterable' in state) {
            me.filterable = state.filterable;
        }

        me.endBatch();

        if ('hidden' in state) {
            me.toggle(state.hidden !== true);
        }
    }

    //endregion

    //region Other

    /**
     * Clear cell contents. Base implementation which just sets innerHTML to blank string.
     * Should be overridden in subclasses to clean up for examples widgets.
     * @param {HTMLElement} cellElement
     * @internal
     */
    clearCell(cellElement) {
        cellElement.innerHTML = '';
    }

    /**
     * Override in subclasses to allow/prevent editing of certain rows.
     * @param {Core.data.Model} record
     * @internal
     */
    canEdit(record) {
        return true;
    }

    //endregion
}
// Registered in ColumnStore as we can't have this in Column due to circular dependencies
// ColumnStore.registerColumnType(Column);

Column.emptyCount = 0;
Column.defaultWidth = 100;
Column.exposeProperties();
