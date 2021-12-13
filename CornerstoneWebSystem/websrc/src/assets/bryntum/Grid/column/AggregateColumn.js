import ColumnStore from '../data/ColumnStore.js';
import Column from './Column.js';

/**
 * @module Grid/column/AggregateColumn
 */

/**
 * A column, which, when used as part of a Tree, aggregates ths values of this column's descendants using
 * a configured function which defaults to `sum`.
 *
 * ```
 * const grid = new TreeGrid({
 *   // Custome aggregation handler.
 *   // For test purposes, this just does "sum"
 *   myAggregator(...values) {
 *       let result = 0;
 * 
 *       for (let i = 0, { length } = args; i < length; i++) {
 *           result += parseInt(args[i], 10);
 *       }
 *       return result;
 *   },
 *   columns : [
 *      { field : 'name', text : 'Name' },
 *
 *      // Will sum the ages of leaf nodes. This is the default.
 *      { type : 'aggregate', field : 'age', text : 'Age', renderer : ({ value }) => `<b>${value}<b>` },
 * 
 *      // Will use AggregateColumn's built-in avg of scores of leaf nodes
 *      { type : 'aggregate', field : 'score', text : 'Score', function : 'avg' },
 * 
 *      // Will use the grid's myAggregator function
 *      { type : 'aggregate', field : 'revenue', text : 'Revenue', function : 'up.myAggregator' },
 *   ]
 * });
 * ```
 *
 * @extends Grid/column/Column
 * @classType aggregate
 */
export default class AggregateColumn extends Column {
    //region Config
    static get type() {
        return 'aggregate';
    }

    static get fields() {
        return [
            'function'
        ];
    }

    static get defaults() {
        return {
            /**
             * Math Function name, or function name prepended by `"up."` that is resolveable in an
             * ancestor component (such as the owning Grid, or a hight Container), or a function to
             * use to aggregate child record values for this column, or a function.
             *
             * This Column is provided with a `sum` and `avg` function. The default function is `sum`
             * which is used for the aggregation.
             * @config {Function|String}
             * @category Common
             */
            function : 'sum'
        };
    }

    construct(data, columnStore) {
        this.configuredAlign = 'align' in data;
        this.configuredEditor = 'editor' in data;

        super.construct(...arguments);

        const { grid } = columnStore;

        if (grid) {
            this.owner = grid;
            grid.on({
                paint   : 'onGridPaint',
                thisObj : this,
                prio    : 1000
            });
        }
    }

    onGridPaint({ source : grid }) {
        this.store = grid.store;
    }

    set store(store) {
        const
            me             = this,
            storeListeners = {
                update  : 'onRecordUpdate',
                thisObj : me,
                prio    : 1000
            },
            oldStore = me._store;

        if (store !== oldStore) {
            if (oldStore) {
                oldStore.un(storeListeners);
            }

            me._store = store;

            const
                { modelClass } = store,
                field = modelClass.fieldMap[me.field];

            // It's *likely*, but not certain that this will be used for a numeric field.
            // Use numeric defaults unless configured otherwise if so.
            if (field && field.type === 'number') {
                if (!me.configuredAlign) {
                    me.align = 'end';
                }
                if (!me.configuredEditor) {
                    me.editor = 'number';
                }
            }
    
            store.on(storeListeners);
        }
    }

    canEdit(record) {
        return record.isLeaf;
    }

    get store() {
        return this._store;
    }

    sum(...args) {
        let result = 0;

        for (let i = 0, { length } = args; i < length; i++) {
            result += parseInt(args[i], 10);
        }
        return result;
    }

    avg(...args) {
        let result = 0;
        const { length } = args;

        for (let i = 0; i < length; i++) {
            result += parseInt(args[i], 10);
        }
        return result / length;
    }

    onRecordUpdate({ record, changes }) {
        const
            me = this,
            { rowManager } = me.grid;

        if (me.field in changes) {
            if (record.isLeaf) {
                record.bubble(rec => {
                    const row = rowManager.getRowFor(rec);

                    if (row) {
                        const cell = row.getCell(me.field);

                        if (cell) {
                            row.renderCell(cell, rec);
                        }
                    }
                }, true);
            }
        }
    }

    getRawValue(record) {
        let value = 0;

        if (record.children) {
            const
                me       = this,
                fn       = me.function,
                isMathFn = typeof fn === 'string' && typeof Math[fn] === 'function',
                {
                    handler,
                    thisObj
                } = isMathFn ? {
                    handler : Math[fn],
                    thisObj : Math
                } : me.resolveCallback(fn);

            for (let i = 0, { length } = record.children; i < length; i++) {
                value = handler.call(thisObj, value, me.getRawValue(record.children[i]));
            }
            record.setData(me.field, value);
        }
        else {
            value = record[this.field];
        }
        return value;
    }
}

ColumnStore.registerColumnType(AggregateColumn, true);
AggregateColumn.exposeProperties();
