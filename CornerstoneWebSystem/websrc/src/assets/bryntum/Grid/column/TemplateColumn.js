import Column from './Column.js';
import ColumnStore from '../data/ColumnStore.js';

/**
 * @module Grid/column/TemplateColumn
 */

/**
 * A column that uses a template for cell content. Any function can be used as template, and the function is passed { value, record, field } properties.
 * It should return a string which will be rendered in the cell
 *
 * @extends Grid/column/Column
 *
 * @example
 * new Grid({
 *     appendTo : document.body,
 *
 *     columns : [
 *         { type: 'template', field: 'age', template: value => `${value} years old` }
 *     ]
 * });
 *
 * @classType template
 * @externalexample column/TemplateColumn.js
 */
export default class TemplateColumn extends Column {
    static get fields() {
        return [
            /**
             * Template function used to generate a value displayed in the cell. Called with arguments { value, record, field }
             * @config {Function} template
             * @category Common
             */
            'template'
        ];
    }

    static get defaults() {
        return {
            htmlEncode : false
        };
    }

    constructor(config, store) {
        super(...arguments);

        const me = this;

        if (!me.template) throw new Error(me.L('noTemplate'));
        if (typeof me.template !== 'function') throw new Error(me.L('noFunction'));
    }

    static get type() {
        return 'template';
    }

    /**
     * Renderer that uses a template for cell content.
     * @private
     */
    renderer(renderData) {
        // If it's a special row, such as a group row, we can't use the user's template
        if (!renderData.record.meta.specialRow) {
            return this.template({
                value  : renderData.value,
                record : renderData.record,
                field  : renderData.column.field
            });
        }
    }
}

ColumnStore.registerColumnType(TemplateColumn, true);
TemplateColumn.exposeProperties();
