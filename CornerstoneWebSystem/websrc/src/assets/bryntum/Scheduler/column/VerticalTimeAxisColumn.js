import Column from '../../Grid/column/Column.js';
import ColumnStore from '../../Grid/data/ColumnStore.js';
import VerticalTimeAxis from '../view/VerticalTimeAxis.js';

export default class VerticalTimeAxisColumn extends Column {
    static get type() {
        return 'verticalTimeAxis';
    }

    static get defaults() {
        return {
            /**
             * Set to false to prevent this column header from being dragged.
             * @config {Boolean} draggable
             * @category Interaction
             * @default false
             * @hide
             */
            draggable : false,

            /**
             * Set to false to prevent grouping by this column.
             * @config {Boolean} groupable
             * @category Interaction
             * @default false
             * @hide
             */
            groupable : false,

            /**
             * Allow column visibility to be toggled through UI.
             * @config {Boolean} hideable
             * @default false
             * @category Interaction
             * @hide
             */
            hideable : false,

            /**
             * Show column picker for the column.
             * @config {Boolean} showColumnPicker
             * @default false
             * @category Menu
             * @hide
             */
            showColumnPicker : false,

            /**
             * Allow filtering data in the column (if Filter feature is enabled)
             * @config {Boolean} filterable
             * @default false
             * @category Interaction
             * @hide
             */
            filterable : false,

            /**
             * Allow sorting of data in the column
             * @config {Boolean} sortable
             * @category Interaction
             * @default false
             * @hide
             */
            sortable : false,

            // /**
            //  * Set to `false` to prevent the column from being drag-resized when the ColumnResize plugin is enabled.
            //  * @config {Boolean} resizable
            //  * @default false
            //  * @category Interaction
            //  * @hide
            //  */
            // resizable : false,

            /**
             * Allow searching in the column (respected by QuickFind and Search features)
             * @config {Boolean} searchable
             * @default false
             * @category Interaction
             * @hide
             */
            searchable : false,

            /**
             * Specifies if this column should be editable, and define which editor to use for editing cells in the column (if CellEdit feature is enabled)
             * @config {String} editor
             * @default false
             * @category Interaction
             * @hide
             */
            editor : false,

            /**
             * false to prevent showing a context menu on the cell elements in this column
             * @config {Boolean} enableCellContextMenu
             * @default false
             * @category Menu
             * @hide
             */
            enableCellContextMenu : false,

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
             * @default false
             * @hide
             */
            tooltipRenderer : false,

            cellCls : 'b-verticaltimeaxiscolumn'
        };
    }

    construct(data) {
        const me = this;

        super.construct(...arguments);

        me.timeAxisViewModel = data.timeline.timeAxisViewModel;

        me.view = new VerticalTimeAxis({
            model  : me.timeAxisViewModel,
            client : data.timeline
        });
    }

    renderer({ cellElement, size }) {
        this.view.render(cellElement);

        size.height = this.view.height;
    }
}

ColumnStore.registerColumnType(VerticalTimeAxisColumn);
