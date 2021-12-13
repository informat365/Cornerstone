// TODO: Make this work for grouped columns

import DomHelper from '../../Core/helper/DomHelper.js';
import DragHelper from '../../Core/helper/DragHelper.js';
import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import GridFeatureManager from '../feature/GridFeatureManager.js';
import Delayable from '../../Core/mixin/Delayable.js';
import IdHelper from '../../Core/helper/IdHelper.js';

/**
 * @module Grid/feature/ColumnReorder
 */

/**
 * Allows user to reorder columns by dragging headers. To get notified about column reorder listen to `change` event
 * on {@link Grid.data.ColumnStore columns} store.
 *
 * This feature is <strong>enabled</strong> by default.
 *
 * @extends Core/mixin/InstancePlugin
 *
 * @demo Grid/columns
 * @classtype columnReorder
 * @externalexample feature/ColumnReorder.js
 */
export default class ColumnReorder extends Delayable(InstancePlugin) {
    //region Init

    static get $name() {
        return 'ColumnReorder';
    }

    construct(grid, config) {
        this.ignoreSelectors = [
            '.b-grid-header-resize-handle',
            '.b-field'
        ];

        this.grid = grid;

        super.construct(grid, config);
    }

    doDestroy() {
        this.dragHelper && this.dragHelper.destroy();

        super.doDestroy();
    }

    /**
     * Initialize drag & drop (called from render)
     * @private
     */
    init() {
        const
            me         = this,
            { grid }   = me,
            gridEl     = grid.element,
            containers = DomHelper.children(gridEl, '.b-grid-headers');

        containers.push(...DomHelper.children(gridEl, '.b-grid-header-children'));

        if (me.dragHelper) {
            // update the dragHelper with the new set of containers it should operate upon
            me.dragHelper.containers = containers;
        }
        else {
            me.dragHelper = new DragHelper({
                name           : 'columnReorder',
                mode           : 'container',
                dragThreshold  : 10,
                targetSelector : '.b-grid-header',
                outerElement   : gridEl.querySelector('header.b-grid-header-container'),
                containers,
                isElementDraggable(element) {
                    const abort = Boolean(DomHelper.up(element, me.ignoreSelectors.join(',')));

                    if (abort || me.disabled) {
                        return false;
                    }

                    const
                        columnEl = DomHelper.up(element, this.targetSelector),
                        column   = columnEl && grid.columns.getById(columnEl.dataset.columnId),
                        isLast   = column && column.childLevel === 0 && grid.subGrids[column.region].columns.count === 1;

                    // TODO: If we want to prevent dragging last column out of group we can use the code below...
                    /*isLast = column.level !== 0
                            // In grouped header, do not allow dragging last remaining child
                            ? column.parent.children.length === 1
                            // Not in a grouped header, do not allow dragging last remaining column
                            : grid.subGrids[column.region].columns.count === 1;*/

                    return Boolean(column) && column.draggable !== false && !isLast;
                },
                ignoreSelector : '.b-filter-icon,.b-grid-header-resize-handle',
                listeners      : {
                    dragstart : me.onDragStart,
                    drag      : me.onDrag,
                    drop      : me.onDrop,
                    thisObj   : me
                }
            });

            me.relayEvents(me.dragHelper, ['dragStart', 'drag', 'drop', 'abort'], 'gridHeader');
        }
    }

    //endregion

    //region Plugin config

    // Plugin configuration. This plugin chains some of the functions in Grid
    static get pluginConfig() {
        return {
            after : ['render', 'renderContents']
        };
    }

    //endregion

    //region Events (drop)

    onDrag({ context, event }) {
        const
            me           = this,
            targetHeader = IdHelper.fromElement(event.target, 'header');

        // If SubGrid is configured with a sealed column set, do not allow moving into it
        if (targetHeader && targetHeader.subGrid.sealedColumns) {
            context.valid = false;
            return;
        }

        // Require that we drag inside grid header while dragging if we don't have a drag toolbar
        if (!me.grid.features.columnDragToolbar) {
            context.valid = Boolean(event.target.closest('.b-grid-headers'));
        }
    }

    onDragStart() {
        const me = this;

        if (!me.grid.features.columnDragToolbar) {
            const headerContainerBox = me.grid.element.querySelector('.b-grid-header-container').getBoundingClientRect();

            me.dragHelper.minY = headerContainerBox.top;
            me.dragHelper.maxY = headerContainerBox.bottom;
        }

        this.grid.headerContainer.classList.add('b-dragging-header');
    }

    /**
     * Handle drop
     * @private
     */
    onDrop({ context }) {
        if (!context.valid) {
            return this.onInvalidDrop({ context });
        }

        const
            me           = this,
            grid         = me.grid,
            element      = context.dragging,
            onHeader     = DomHelper.up(context.target, '.b-grid-header'),
            onColumn     = grid.columns.get(onHeader.dataset.column),
            toRegion     = context.draggedTo.dataset.region || onColumn.region,
            sibling      = context.insertBefore,
            column       = grid.columns.getById(element.dataset.columnId),
            insertBefore = sibling ? grid.columns.getById(sibling.dataset.columnId) : grid.subGrids[toRegion].columns.last.nextSibling,
            newParent    = insertBefore ? insertBefore.parent : grid.columns.rootNode;

        grid.headerContainer.classList.remove('b-dragging-header');

        // Dropped into its current position in the same SubGrid - abort
        if (toRegion === column.region && (onColumn === column.previousSibling || insertBefore === column.nextSibling)) {
            me.dragHelper.abort();
            return;
        }

        // Check if we should remove last child
        const emptyParent = column.parent && column.parent.children.length === 1 && column.parent;

        if (emptyParent) {
            emptyParent.parent.removeChild(emptyParent);
        }

        // Clean up element used during drag drop as it will not be removed by Grid when it refreshes its header elements
        element.remove();

        column.region = toRegion;

        // Insert the column into its new place
        newParent.insertChild(column, insertBefore);
    }

    /**
     * Handle invalid drop
     * @private
     */
    onInvalidDrop() {
        this.grid.headerContainer.classList.remove('b-dragging-header');
    }

    //endregion

    //region Render

    /**
     * Updates DragHelper with updated headers when grid contents is rerendered
     * @private
     */
    renderContents() {
        // columns shown, hidden or reordered
        this.init();
    }

    /**
     * Initializes this feature on grid render.
     * @private
     */
    render() {
        // always reinit on render
        this.init();
    }

    //endregion
}

ColumnReorder.featureClass = 'b-column-reorder';

GridFeatureManager.registerFeature(ColumnReorder, true);
