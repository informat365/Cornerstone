import Model from '../../Core/data/Model.js';

/**
 * @module Grid/data/GridRowModel
 */

/**
 * Model extended with some fields related to grid rendering. Used as default model type in the grids store if nothing
 * else is specified.
 *
 * Using this model is optional. If you use a custom model instead and need the functionality of any of the fields
 * below, you just have to remember to add fields with the same name to your model.
 *
 * @extends Core/data/Model
 */
export default class GridRowModel extends Model {
    static get fields() {
        return [
            /**
             * Icon for row (used automatically in tree, feel free to use it in renderer in other cases)
             * @field {String} iconCls
             */
            'iconCls',

            /**
             * Start expanded or not (only valid for tree data)
             * @field {Boolean} expanded
             */
            'expanded',

            /**
             * CSS class (or several classes divided by space) to append to row elements
             * @field {String} cls
             */
            'cls',

            /**
             * Row height, set it to use another height then the default for a row
             * @field {Number} rowHeight
             */
            'rowHeight',

            /**
             * A link to use for this record when rendered into a {@link Grid.column.TreeColumn}.
             * @field {String} href
             */
            'href',

            /**
             * The target to use if this tree node provides a value for the {@link #field-href} field.
             * @field {String} target
             */
            'target'
        ];
    }
}

GridRowModel.exposeProperties();
