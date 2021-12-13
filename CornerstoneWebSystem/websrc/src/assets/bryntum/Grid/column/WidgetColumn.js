//TODO: Currently widgets reuse elements already in cell, but performance would improve if entire widget was reused

//TODO: Leaking widget on rerender of row, since the old one is not destroyed

import WidgetHelper from '../../Core/helper/WidgetHelper.js';
import Column from './Column.js';
import ColumnStore from '../data/ColumnStore.js';

/**
 * @module Grid/column/WidgetColumn
 */

/**
 * A column that displays widgets in the cells
 *
 * @extends Grid/column/Column
 *
 * @example
 * new Grid({
 *     appendTo : document.body,
 *
 *     columns : [
 *         { type: 'widget', text: 'Increase age', widget: { type: 'button', icon: 'add' }, data: 'age' }
 *     ]
 * });
 *
 * @classType widget
 * @externalexample column/WidgetColumn.js
 */
export default class WidgetColumn extends Column {

    //region Config

    static get type() {
        return 'widget';
    }

    static get fields() {
        return [
            /**
             * An array of widget config objects
             * @config {Object[]} widgets
             * @category Common
             */
            'widgets'
        ];
    }

    static get defaults() {
        return {
            filterable : false,
            sortable   : false,
            editor     : false,
            searchable : false
        };
    }

    //endregion

    //region Init / Destroy

    construct(config, store) {
        this.widgetMap = {};
        this.internalCellCls = 'b-widget-cell';

        super.construct(...arguments);
    }

    doDestroy() {
        // Destroy all the widgets we created.
        for (const widget of Object.values(this.widgetMap)) {
            widget.destroy && widget.destroy();
        }
        super.doDestroy();
    }

    //endregion

    //region Render

    /**
     * Renderer that displays a widget in the cell.
     * @param {Object} event Render event
     * @private
     */
    renderer(event) {
        const
            me = this,
            { cellElement, column, value, record, isExport } = event,
            widgets = column.widgets;

        // This renderer might be called from subclasses by accident
        // This condition saves us from investigating bug reports
        if (!isExport && widgets) {
            // If there is no widgets yet and we're going to add them,
            // need to make sure there is no content left in the cell after its previous usage
            // by grid features such as grouping feature or so.
            if (!cellElement.widgets) {
                // Reset cell content
                me.clearCell(cellElement);
            }
            cellElement.widgets = widgets.map((widgetCfg, i) => {
                let widget, widgetNextSibling;

                // If cell element already has widgets, check if we need to destroy/remove one
                if (cellElement.widgets) {
                    // Current widget
                    widget = cellElement.widgets[i];

                    // Store next element sibling to insert widget to correct position later
                    widgetNextSibling = widget.element.nextElementSibling;

                    // If we are not syncing content for present widget, remove it from cell and render again later
                    if (widgetCfg.recreate && widget) {
                        // destroy widget and remove reference to it
                        delete me.widgetMap[widget.id];
                        widget.destroy();
                        cellElement.widgets[i] = null;
                    }
                }

                // Ensure widget is created if first time through
                if (!widget) {
                    me.onBeforeWidgetCreate(widgetCfg, event);
                    widget = WidgetHelper.append(widgetCfg, widgetNextSibling ? { insertBefore : widgetNextSibling } : cellElement)[0];
                    me.widgetMap[widget.id] = widget;
                    me.onAfterWidgetCreate(widget, event);
                }

                widget.cellInfo = {
                    cellElement,
                    value,
                    record,
                    column
                };

                if (me.grid) {
                    widget.readOnly = me.grid.readOnly;
                }

                if (me.onBeforeWidgetSetValue(widget, event) !== false) {
                    if (!widgetCfg.noValueOnRender) {
                        if (widgetCfg.valueProperty) {
                            widget[widgetCfg.valueProperty] = value;
                        }
                        else if (widget.defaultBindProperty) {
                            widget[widget.defaultBindProperty] = value;
                        }
                        else {
                            widget.text = widget.value = value;
                        }
                    }
                }

                me.onAfterWidgetSetValue(widget, event);

                return widget;
            });
        }

        if (isExport) {
            return null;
        }
    }

    //endregion

    //region Other

    /**
     * Called before widget is created on rendering
     * @param {Object} widgetCfg Widget config
     * @param {Object} event Render event
     * @private
     */
    onBeforeWidgetCreate(widgetCfg, event) {}

    /**
     * Called after widget is created on rendering
     * @param {Core.widget.Widget} widget Created widget
     * @param {Object} event Render event
     * @private
     */
    onAfterWidgetCreate(widget, event) {}

    /**
     * Called before widget gets value on rendering. Pass `false` to skip value setting while rendering
     * @param {Core.widget.Widget} widget Created widget
     * @param {Object} event Render event
     * @private
     */
    onBeforeWidgetSetValue(widget, renderEvent) {}

    /**
     * Called after widget gets value on rendering.
     * @param {Core.widget.Widget} widget Created widget
     * @param {Object} event Render event
     * @private
     */
    onAfterWidgetSetValue(widget, renderEvent) {}

    // Overrides base implementation to cleanup widgets, for example when a cell is reused as part of group header
    clearCell(cellElement) {
        if (cellElement.widgets) {
            cellElement.widgets.forEach(widget => {
                // Destroy widget and remove reference to it
                delete this.widgetMap[widget.id];
                widget.destroy();
            });
            cellElement.widgets = null;
        }

        // Even if there is no widgets need to make sure there is no content left, for example after a cell has been reused as part of group header
        super.clearCell(cellElement);
    }

    // Null implementation because there is no way of ascertaining whether the widgets get their width from
    // the column, or the column shrinkwraps the Widget.
    // Remember that the widget could have a width from a CSS rule which we cannot read.
    // It might have width: 100%, or a flex which would mean it is sized by us, but we cannot read that -
    // getComputedStyle would return the numeric width.
    resizeToFitContent() {
    }
    //endregion
}

ColumnStore.registerColumnType(WidgetColumn);
WidgetColumn.exposeProperties();
