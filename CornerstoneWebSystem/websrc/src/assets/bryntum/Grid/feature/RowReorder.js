/**
 * @module Grid/feature/RowReorder
 */

import GridFeatureManager from './GridFeatureManager.js';
import DragHelper from '../../Core/helper/DragHelper.js';
import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import DomHelper from '../../Core/helper/DomHelper.js';
import Delayable from '../../Core/mixin/Delayable.js';

/**
 * Allows user to reorder rows by dragging them. To get notified about row reorder listen to `change` event
 * on the grid {@link Core.data.Store store}.
 *
 * This feature is **disabled** by default.
 * This feature is **enabled** by default for Gantt.
 *
 * If the grid is set to {@link Grid.view.Grid#config-readOnly}, reordering is disabled. Inside all event listeners you have access a `context` object which has a `record` property (the dragged record).
 *
 * You can validate the drag drop flow by listening to the `gridrowdrag` event. Inside this listener you have access the `index` property which is the target drop position.
 * For trees you get access to the `parent` record and `index`, where index means the child index inside the parent.
 *
 * ```
 * features : {
 *     rowReorder : {
 *         listeners : {
 *             gridRowDrag : ({ context }) => {
 *                // Here you have access to context.insertBefore, and additionally context.parent for trees
 *
 *                context.valid = false;
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * @extends Core/mixin/InstancePlugin
 * @demo Grid/rowreorder
 * @classtype rowReorder
 */
export default class RowReorder extends Delayable(InstancePlugin) {
    //region Events
    /**
     * Fired before dragging starts, return false to prevent the drag operation.
     * @preventable
     * @event gridRowBeforeDragStart
     * @param {DragHelper} source
     * @param {Object} context
     * @param {Core.data.Model} context.record The dragged row record
     * @param {MouseEvent|TouchEvent} event
     */

    /**
     * Fired when dragging starts.
     * @event gridRowDragStart
     * @param {DragHelper} source
     * @param {Object} context
     * @param {Core.data.Model} context.record The dragged row record
     * @param {MouseEvent|TouchEvent} event
     */

    /**
     * Fired while the row is being dragged, in the listener function you have access to `context.insertBefore` a grid / tree record, and additionally `context.parent` (a TreeNode) for trees. You can
     * signal that the drop position is valid or invalid by setting `context.valid = false;`
     * @event gridRowDrag
     * @param {DragHelper} source
     * @param {Object} context
     * @param {Boolean} context.valid Set this to true or false to indicate whether the drop position is valid.
     * @param {Core.data.Model} context.insertBefore The record to insert before (`null` if inserting at last position of a parent node)
     * @param {Core.data.Model} context.parent The parent record of the current drop position (only applicable for trees)
     * @param {Core.data.Model} context.record The dragged row record
     * @param {MouseEvent} event
     */

    /**
     * Fired on row drop
     * @event gridRowDrop
     * @param {DragHelper} source
     * @param {Object} context
     * @param {Boolean} context.valid Set this to true or false to indicate whether the drop position is valid.
     * @param {Core.data.Model} context.insertBefore The record to insert before (`null` if inserting at last position of a parent node)
     * @param {Core.data.Model} context.parent The parent record of the current drop position (only applicable for trees)
     * @param {Core.data.Model} context.record The dragged row record
     * @param {MouseEvent} event
     */

    /**
     * Fired when a row drag operation is aborted
     * @event gridRowAbort
     * @param {DragHelper} source
     * @param {Object} context
     * @param {MouseEvent} event
     */
    //endregion

    //region Init

    static get $name() {
        return 'RowReorder';
    }

    static get defaultConfig() {
        return {
            /**
             * If hovering over a parent node for this period of a time in a tree, the node will expand
             * @config {Number}
             */
            hoverExpandTimeout : 1000
        };
    }

    construct(grid, config) {
        this.grid = grid;

        super.construct(...arguments);
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
            me       = this,
            { grid } = me;

        me.dragHelper = new DragHelper({
            name               : 'rowReorder',
            mode               : 'translateXY',
            cloneTarget        : true,
            dragThreshold      : 10,
            targetSelector     : '.b-grid-row',
            lockX              : true,
            transitionDuration : grid.transitionDuration,
            scrollManager      : grid.scrollManager,
            dragWithin         : grid.verticalScroller,
            outerElement       : grid.verticalScroller,

            // Since parent nodes can expand after hovering, meaning original drag start position now refers to a different point in the tree
            ignoreSamePositionDrop : false,

            createProxy(element) {
                const clone = element.cloneNode(true),
                      container = document.createElement('div');

                clone.removeAttribute('id');
                // The containing element will be positioned instead
                clone.style.transform = '';

                container.appendChild(clone);

                return container;
            },

            listeners : {
                beforedragstart : me.onBeforeDragStart,
                dragstart       : me.onDragStart,
                drag            : me.onDrag,
                drop            : me.onDrop,
                reset           : me.onReset,
                prio            : 10000, // To ensure our listener is run before the relayed listeners (for the outside world)
                thisObj         : me
            }
        });

        me.dropIndicator = DomHelper.createElement({
            parent    : grid.bodyContainer,
            className : 'b-row-drop-indicator'
        });

        me.relayEvents(me.dragHelper, ['beforeDragStart', 'dragStart', 'drag', 'drop', 'abort'], 'gridRow');
    }

    //endregion

    //region Plugin config

    static get pluginConfig() {
        return {
            after : ['render']
        };
    }

    //endregion

    //region Events (drop)

    onBeforeDragStart({ context }) {
        const
            grid          = this.grid,
            targetSubGrid = grid.regions[0],
            subGridEl     = grid.subGrids[targetSubGrid].element;

        // Disabled for touch devices until implemented fully. https://app.assembla.com/spaces/bryntum/tickets/8185-fix-row-reorder-for-touch-devices/details#
        // Only dragging enabled in the leftmost grid section
        if (this.disabled || grid.readOnly || DomHelper.isTouchEvent || !subGridEl.contains(context.element)) {
            return false;
        }

        const record = context.record = this.grid.getRecordFromElement(context.element);

        return !record.meta.specialRow;
    }

    onDragStart({ context }) {
        const
            me       = this,
            cellEdit = me.grid.features.cellEdit,
            record = me.grid.getRecordFromElement(context.grabbed);

        if (!record) {
            throw new Error('Failed to find record for dragged element');
        }

        if (cellEdit) {
            me.cellEditDisabledState = cellEdit.disabled;
            cellEdit.disabled = true; // prevent editing from being started through keystroke during row reordering
        }

        if (me.grid.features.contextMenu) {
            me.grid.features.contextMenu.hideContextMenu(false);
        }

        me.grid.element.classList.add('b-row-reordering');

        const focusedCell = context.element.querySelector('.b-focused');
        focusedCell && focusedCell.classList.remove('b-focused');

        DomHelper.removeClasses(context.element.firstElementChild, ['b-selected', 'b-hover']);
        me.record = record;
    }

    onDrag({ context, event }) {
        const
            me                    = this,
            { store, rowManager } = me.grid;

        // Ignore if user drags outside grid area
        if (!me.dragHelper.outerElement.contains(event.target) || !event.target.closest('.b-grid-subgrid')) {
            context.valid = false;
            return;
        }

        let valid = context.valid,
            row   = me.grid.rowManager.getRowAt(event.clientY),
            overRecord,
            dataIndex,
            after,
            insertBefore;

        if (row) {
            const
                rowTop  = row.top + me.grid._bodyRectangle.y - me.grid.scrollable.y,
                middleY = rowTop + (row.height / 2);

            dataIndex = row.dataIndex;
            overRecord = row && store.getAt(dataIndex);

            // Drop after row below if mouse is in bottom half of hovered row
            after = event.clientY > middleY;
        }
        // Below the rows. Drop after last row
        else {
            dataIndex = store.count - 1;
            overRecord = store.last;
            row = me.grid.rowManager.getRow(dataIndex);
            after = true;
        }

        // Hovering the dragged record. This is a no-op.
        // But still gather the contextual data.
        if (overRecord === me.record) {
            valid = false;
        }

        if (store.tree) {
            DomHelper.removeClsGlobally(me.grid.element, 'b-row-reordering-target-parent');

            insertBefore = after ? overRecord.nextSibling : overRecord;

            // For trees, prevent moving a parent into its own hierarchy
            if (me.record.contains(overRecord)) {
                valid = false;
            }

            context.parent = overRecord.parent;

            if (!context.parent.isRoot) {
                const parentRow = rowManager.getRowById(context.parent);

                if (parentRow) {
                    parentRow.addCls('b-row-reordering-target-parent');
                }
            }

            me.clearTimeout(me.hoverTimer);

            if (overRecord && overRecord.isParent && !overRecord.isExpanded(store)) {
                me.hoverTimer = me.setTimeout(() => me.grid.expand(overRecord), me.hoverExpandTimeout);
            }
        }
        else {
            // Public property used for validation
            insertBefore = after ? store.getAt(dataIndex + 1) : overRecord;
        }

        // Provide visual clue to user of the drop position
        DomHelper.setTranslateY(me.dropIndicator, row.top + (after ? row.height : 0));

        context.insertBefore = insertBefore;

        context.valid = valid;
    }

    /**
     * Handle drop
     * @private
     */
    onDrop({ context }) {
        const
            me    = this,
            grid  = me.grid,
            store = grid.store;

        context.asyncCleanup = context.async = true;
        context.element.classList.add('b-dropping');

        me.setTimeout(() => {

            grid.element.classList.remove('b-row-reordering');

            if (context.valid) {
                if (store.tree) {
                    context.parent.insertChild(me.record, context.insertBefore);
                }
                else {
                    store.insert(context.insertBefore ? store.indexOf(context.insertBefore) : store.count, me.record);
                }

                store.clearSorters();

                context.finalize();
            }
        }, 300);
    }

    /**
     * Clean up on reset
     * @private
     */
    onReset() {
        const
            me = this,
            cellEdit = me.grid.features.cellEdit;

        me.grid.element.classList.remove('b-row-reordering');

        if (cellEdit) {
            cellEdit.disabled = me.cellEditDisabledState;
        }

        DomHelper.removeClsGlobally(
            me.grid.element,
            'b-row-reordering-target-parent'
        );
    }

    //endregion

    //region Render

    /**
     * Updates DragHelper with updated headers when grid contents is rerendered
     * @private
     */
    render() {
        // columns shown, hidden or reordered
        this.init();
    }

    //endregion
}

RowReorder.featureClass = '';

GridFeatureManager.registerFeature(RowReorder, false);
GridFeatureManager.registerFeature(RowReorder, true, 'Gantt');
