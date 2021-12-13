import Store from '../../Core/data/Store.js';
import Column from '../column/Column.js';
import Localizable from '../../Core/localization/Localizable.js';

/**
 * @module Grid/data/ColumnStore
 */

/**
 * A store specialized in handling columns. Used by the Grid to hold its columns and used as a chained store by each SubGrid
 * to hold theirs. Should not be instanced directly, instead access it through `grid.columns` or `subGrid.columns`
 *
 * ```
 * // resize first column
 * grid.columns.first.width = 200;
 *
 * // remove city column
 * grid.columns.get('city').remove();
 *
 * // add new column
 * grid.columns.add({text : 'New column'});
 *
 * // add new column to specific region (SubGrid)
 * grid.columns.add({text : 'New column', region : 'locked'});
 *
 * // add new column to 'locked' region (SubGrid)
 * grid.columns.add({text : 'New column', locked : true});
 * ```
 *
 * @extends Core/data/Store
 */
export default class ColumnStore extends Localizable(Store) {
    static get defaultConfig() {
        return {
            modelClass : Column,
            tree       : true,

            /**
             * Automatically adds a field definition to the store used by the Grid when adding a new Column displaying a
             * non-existing field.
             *
             * To enable this behaviour:
             *
             * ```javascript
             * const grid = new Grid({
             *     columns : {
             *         autoAddField : true,
             *         data         : [
             *             // Column definitions here
             *         ]
             *     }
             * }
             *
             * @config {Boolean}
             * @default
             */
            autoAddField : false
        };
    }

    construct(config) {
        const me = this;

        // Consequences of ColumnStore construction can cause reading of grid.columns
        // so set the property early.
        if (config.grid) {
            config.grid._columnStore = me;
            me.id = `${config.grid.id}-columns`;

            // Visible columns must be invalidated on expand/collapse
            config.grid.on({
                subGridCollapse : 'clearCaches',
                subGridExpand   : 'clearCaches',
                thisObj         : me
            });
        }
        super.construct(config);

        // So that we can invalidate cached collections which take computing so that we compute them
        // only when necessary. For example when asking for the visible leaf columns, we do not want
        // to compute that each time.
        me.on({
            change  : me.clearCaches,
            thisObj : me,
            prio    : 1
        });
    }

    get modelClass() {
        return this._modelClass;
    }

    set modelClass(ClassDef) {
        this._modelClass = ClassDef;
    }

    doDestroy() {
        const allColumns = [];

        this.traverse(column => allColumns.push(column));

        super.doDestroy();

        // Store's destroy unjoins all records. Destroy all columns *after* that.
        allColumns.forEach(column => column.destroy());
    }

    // Overridden because the flat collection only contains top level columns,
    // not leaves - group columns are *not* expanded.
    getById(id) {
        return super.getById(id) || this.idRegister[id];
    }

    forEach(fn, thisObj = this) {
        // Override to omit root
        this.traverseWhile((n, i) => fn.call(thisObj, n, i), true);
    }

    get totalFixedWidth() {
        let result = 0;
        for (let col of this) {
            if (!col.hidden) {
                if (col.flex) {
                    result += col.measureSize(Column.defaultWidth);
                }
                else {
                    result += Math.max(col.measureSize(col.width), col.measureSize(col.minWidth));
                }
            }
        }
        return result;
    }

    /**
     * Returns the top level columns. If using grouped columns, this is the top level columns. If no grouped
     * columns are being used, this is the leaf columns.
     * @property {Grid.column.Column[]}
     * @readonly
     */
    get topColumns() {
        return this.isChained ? this.masterStore.rootNode.children.filter(this.chainedFilterFn) : this.rootNode.children;
    }

    /**
     * Returns the visible leaf headers which drive the rows' cell content.
     * @property {Grid.column.Column[]}
     * @readonly
     */
    get visibleColumns() {
        const me = this;

        if (!me._visibleColumns) {
            me._visibleColumns = me.leaves.filter(column => !column.hidden && (!column.subGrid || !column.subGrid.collapsed));
        }

        return me._visibleColumns;
    }

    clearCaches() {
        this._visibleColumns = null;
    }

    onMasterDataChanged(event) {
        super.onMasterDataChanged(event);

        // If master store has changes we also need to clear cached columns, in case a column was hidden
        this.clearCaches();
    }

    getAdjacentVisibleLeafColumn(columnOrId, next = true, wrap = false) {
        let columns = this.visibleColumns,
            column = (columnOrId instanceof Column) ? columnOrId : this.getById(columnOrId),
            idx = columns.indexOf(column) + (next ? 1 : -1);

        // If we walked off either end, wrap if directed to do so,
        // otherwise, return null;
        if (!columns[idx]) {
            if (wrap) {
                idx = next ? 0 : columns.length - 1;
            }
            else {
                return null;
            }
        }

        return columns[idx];
    }

    /**
     * Bottom columns are the ones displayed in the bottom row of a grouped header, or all columns if not using a grouped
     * header. They are the columns that actually display any data.
     * @returns {Grid.column.Column[]}
     * @readonly
     */
    get bottomColumns() {
        return this.leaves;
    }

    /**
     * Get column by field. To be sure that you are getting exactly the intended column, use {@link Core.data.Store#function-getById Store#getById()} with the
     * columns id instead.
     * @param {String} field Field name
     * @returns {Grid.column.Column}
     */
    get(field) {
        return this.findRecord('field', field, true);
    }

    /**
     * Used internally to create a new record in the store. Creates a column of the correct type by looking up the
     * specified type among registered columns.
     * @private
     */
    createRecord(data) {
        const
            { grid = {} } = this, // Some ColumnStore tests lacks Grid
            { store } = grid;

        let columnClass = this.modelClass;

        if (data.type) {
            columnClass = ColumnStore.getColumnClass(data.type);
            if (!columnClass) {
                throw new Error(this.L('columnTypeNotFound', data));
            }
        }

        if (data.locked) {
            data.region = 'locked';
            delete data.locked;
        }

        const column = new columnClass(data, this);

        // Doing this after construction, in case the columnClass has a default value for region (Schedulers
        // TimeAxisColumn has)
        if (!column.data.region) {
            column.data.region = grid.defaultRegion || 'normal';
        }

        // Add missing fields to Grids stores model
        if (this.autoAddField && !column.noFieldSpecified && store && !store.modelClass.getFieldDefinition(column.field)) {
            let fieldDefinition = column.field;

            // Some columns define the type to use for new fields (date, number etc)
            if (column.constructor.fieldType) {
                fieldDefinition = {
                    name : column.field,
                    type : column.constructor.fieldType
                };
            }

            store.modelClass.addField(fieldDefinition);
        }

        return column;
    }

    /**
     * indexOf extended to also accept a columns field, for backward compatibility.
     * ```
     * grid.columns.indexOf('name');
     * ```
     * @param recordOrId
     * @returns {Number}
     */
    indexOf(recordOrId) {
        // TODO: build the need for field away
        let index = super.indexOf(recordOrId);
        if (index > -1) return index;
        // no record found by id, find by field since old code relies on that instead of id
        // TODO: replace such cases with columns id
        return this.records.findIndex(r => r.field === recordOrId);
    }

    //region Column types

    /**
     * Call from custom column to register it with ColumnStore. Required to be able to specify type in column config.
     * @param {Class} columnClass The {@link Grid.column.Column} subclass to register.
     * @param {Boolean} simpleRenderer Pass `true` if its default renderer does *not* use other fields from the passed
     * record than its configured {@link Grid.column.Column#config-field}. This enables more granular cell updating
     * upon record mutation.
     * @example
     * // create and register custom column
     * class CustomColumn {
     *  static get type() {
     *      return 'custom';
     *  }
     * }
     * ColumnStore.registerColumnType(CustomColumn, true);
     * // now possible to specify in column config
     * let grid = new Grid({
     *   columns: [
     *     { type: 'custom', field: 'id' }
     *   ]
     * });
     */
    static registerColumnType(columnClass, simpleRenderer = false) {
        if (!ColumnStore.columnTypes) ColumnStore.columnTypes = {};
        columnClass.simpleRenderer = simpleRenderer;
        ColumnStore.columnTypes[columnClass.type] = columnClass;
    }

    /**
     * Returns registered column class for specified type.
     * @param type Type name
     * @returns {Grid.column.Column}
     * @internal
     */
    static getColumnClass(type) {
        return ColumnStore.columnTypes && ColumnStore.columnTypes[type];
    }

    //endregion
}

/**
 * Custom {@link Grid.data.ColumnStore} event which triggers when a column is resized, i.e. its width has been changed
 *
 * @param {Function} handler
 * @param {Object} [thisObj]
 */
export const columnResizeEvent = (handler, thisObj) => ({
    'update' : ({ store, record, changes }) => {
        let result = true;

        if ('width' in changes || 'minWidth' in changes || 'flex' in changes) {
            result = handler.call(thisObj, { store, record, changes });
        }

        return result;
    }
});
// Can't have this in Column due to circular dependencies
ColumnStore.registerColumnType(Column, true);
