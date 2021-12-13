//TODO: Handle paths? Like expandPath('Parent A/Sub B/Leaf')
//TODO: Dblclick on row (or at least tree cell) should expand/collapse (if not using celleditor)
//TODO: Arrows

import TreeColumn from '../column/TreeColumn.js';
import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import GridFeatureManager from '../feature/GridFeatureManager.js';
import Delayable from '../../Core/mixin/Delayable.js';

/**
 * @module Grid/feature/Tree
 */

const immediatePromise = new Promise((resolve) => resolve());

/**
 * Feature that makes the grid work more like a tree. Included by default in {@link Grid.view.TreeGrid}. Requires
 * exactly one {@link Grid.column.TreeColumn} among grids columns. That column will have its renderer replaced with a
 * tree renderer that adds padding and icon to give the appearance of a tree. The original renderer is preserved and
 * also called.
 *
 * This feature is <strong>disabled</strong> by default. When enabled, the feature cannot be disabled during runtime.
 *
 * @extends Core/mixin/InstancePlugin
 *
 * @demo Grid/tree
 * @classtype tree
 * @externalexample feature/Tree.js
 */
export default class Tree extends Delayable(InstancePlugin) {
    //region Config

    static get $name() {
        return 'Tree';
    }

    static get defaultConfig() {
        return {
            /**
             * Expand parent nodes when clicking on their cell
             * @config {Boolean}
             * @default
             */
            expandOnCellClick : false
        };
    }

    // Plugin configuration. This plugin chains some of the functions in Grid.
    static get pluginConfig() {
        return {
            assign : ['collapseAll', 'expandAll', 'collapse', 'expand', 'expandTo', 'toggleCollapse'],
            before : ['navigateRight', 'navigateLeft'],
            chain  : ['onElementClick', 'onElementKeyDown']
        };
    }

    //endregion

    //region Init

    construct(grid, config) {
        const me = this;

        super.construct(grid, config);

        // find column
        const treeColumn = grid.columns.find(col => col instanceof TreeColumn);

        //<debug>
        if (!treeColumn) {
            throw new Error(me.L('noTreeColumn'));
        }
        //</debug>

        Object.assign(me, {
            grid       : grid,
            store      : grid.store,
            rowManager : grid.rowManager,
            treeColumn : treeColumn
        });

        me.store.on({
            beforeLoadChildren    : 'onBeforeLoadChildren',
            loadChildren          : 'onLoadChildren',
            loadChildrenException : 'onLoadChildrenException',
            beforeToggleNode      : 'onBeforeToggleNode',
            thisObj               : me
        });
    }

    doDisable(disable) {
        //<debug>
        if (disable) {
            throw new Error('Tree feature cannot be disabled');
        }
        //</debug>
    }

    //endregion

    //region Expand & collapse

    /**
     * Collapse an expanded node or expand a collapsed. Optionally forcing a certain state.
     * @param {String|Number|Core.data.Model} idOrRecord Record (the node itself) or id of a node to toggle
     * @param {Boolean} [collapse] Force collapse (true) or expand (false)
     * @param {Boolean} [skipRefresh] Set to true to not refresh rows (if calling in batch)
     * @returns {Promise}
     * @async
     */
    async toggleCollapse(idOrRecord, collapse, skipRefresh = false) {
        //<debug>
        if (idOrRecord == null) {
            throw new Error('Tree#toggleCollapse must be passed a record');
        }
        //</debug>

        const
            me                 = this,
            { store, grid }    = me,
            rowManager         = grid.rowManager,
            record             = store.getById(idOrRecord),
            meta               = record.instanceMeta(store);

        if (await store.toggleCollapse(record, collapse)) {
            const row = rowManager.getRowFor(record);
            if (row) {
                const cell = row.getCell(me.treeColumn.id);

                // Toggle cell's expanded/collapsed state
                row.renderCell(cell, record);
            }

            // Add a temporary cls, used by Scheduler & Gantt to prevent transitions on events/tasks
            // Block multiple applications in the case of a recursive collapseAll operation
            if (!me.isTogglingNode) {
                grid.element.classList.add('b-toggling-node');
                me.isTogglingNode = true;
                me.requestAnimationFrame(() => {
                    grid.element.classList.remove('b-toggling-node');
                    me.isTogglingNode = false;
                });
            }

            grid.trigger(meta.collapsed ? 'collapseNode' : 'expandNode', { source : grid, record });
            grid.trigger('toggleNode', { source : grid, record, collapse });
        }
    }

    /**
     * Collapse a single node. This function is exposed on Grid and can thus be called as `grid.collapse()`
     * @param {String|Number|Core.data.Model} idOrRecord Record (the node itself) or id of a node to collapse
     * @returns {Promise}
     */
    async collapse(idOrRecord) {
        return this.toggleCollapse(idOrRecord, true);
    }

    /**
     * Expand a single node. This function is exposed on Grid and can thus be called as `grid.expand()`
     * @param {String|Number|Core.data.Model} idOrRecord Record (the node itself) or id of a node to expand
     * @returns {Promise}
     */
    async expand(idOrRecord) {
        return this.toggleCollapse(idOrRecord, false);
    }

    onBeforeToggleNode({ record, collapse }) {
        this.grid.trigger('beforeToggleNode', { record, collapse });
    }

    onBeforeLoadChildren({ source : store, params }) {
        const
            parent = store.getById(params[store.modelClass.idField]),
            row = this.grid.rowManager.getRowFor(parent);

        if (row) {
            row.addCls('b-loading-children');
        }
    }

    onLoadChildren({ source : store, params }) {
        const
            parent = store.getById(params[store.modelClass.idField]),
            row = this.grid.rowManager.getRowFor(parent);

        if (row) {
            row.removeCls('b-loading-children');
        }
    }

    onLoadChildrenException({ record }) {
        const row = this.grid.rowManager.getRowFor(record);

        if (row) {
            row.removeCls('b-loading-children');
        }
    }

    /**
     * Expand or collapse all nodes, as specified by param, starting at the passed node (which defaults to the root node)
     * @param {Boolean} [collapse] Set to true to collapse, false to expand (defaults to true)
     * @param {Core.data.Model} [topNode] The topmost node from which to cascade a collapse.
     * Defaults to the {@link Core.data.Store#property-rootNode}. Not included in the cascade if
     * the root node is being used.
     * @returns {Promise}
     */
    expandOrCollapseAll(collapse = true, topNode = this.store.rootNode) {
        // TODO: Some logic here and some in the store, keep in same place maybe
        const
            { grid, store } = this,
            promises = [];

        grid.trigger('beforeToggleAllNodes', { source : grid, collapse });

        // Each collapse/expand will trigger events on store, avoid that by suspending
        store.suspendEvents();
        store.traverse(record => {
            const gridMeta = record.instanceMeta(store);
            if (!record.isLeaf) {
                if (collapse && !gridMeta.collapsed) {
                    this.toggleCollapse(record, true, true);
                }
                else if (!collapse && gridMeta.collapsed) {
                    promises.push(this.toggleCollapse(record, false, true));
                }
            }
        }, topNode, topNode === store.rootNode);
        store.resumeEvents();

        return (collapse ? immediatePromise : Promise.all(promises)).then(() => {
            // Return to top when collapsing all
            grid.refreshRows(collapse);

            grid.trigger('toggleAllNodes', { source : grid, collapse });
        });
    }

    /**
     * Collapse all nodes. This function is exposed on Grid and can thus be called as `grid.collapseAll()`
     * @returns {Promise}
     */
    async collapseAll() {
        return this.expandOrCollapseAll(true);
    }

    /**
     * Expand all nodes. This function is exposed on Grid and can thus be called as `grid.expandAll()`
     * @returns {Promise}
     */
    async expandAll() {
        return this.expandOrCollapseAll(false);
    }

    /**
     * Expands parent nodes to make this node "visible". This function is exposed on Grid and can thus be called as
     * `grid.expandTo()`
     * @param {String|Number|Core.data.Model} idOrRecord Record (the node itself) or id of a node
     * @returns {Promise}
     */
    async expandTo(idOrRecord) {
        const me         = this,
            record     = me.store.getById(idOrRecord),
            recordMeta = record.instanceMeta(me.store);

        // Already visible? abort
        if (recordMeta.hiddenByCollapse === false) return;

        // Expand each parent
        let refreshNeeded = false,
            parent        = record.parent,
            parents       = [];

        // To expand path correctly we need to do it from top
        // so parent record for each record will be in the store already
        while (parent && !parent.isRoot) {
            parents.push(parent);
            parent = parent.parent;
        }

        await parents.reverse().forEach(async parent => {
            if (parent.instanceMeta(me.store).collapsed) {
                refreshNeeded = true;
            }
            await me.toggleCollapse(parent, false, true);
        });

        if (refreshNeeded) {
            me.grid.refreshRows();
        }

        await me.grid.scrollRowIntoView(record);
    }

    //endregion

    //region Renderer

    //endregion

    //region Events

    /**
     * Called when user clicks somewhere in the grid. Expand/collapse node on icon click.
     * @private
     */
    onElementClick(event) {
        const me       = this,
            target   = event.target,
            cellData = me.grid.getEventData(event);

        // Checks if click is on node expander icon, then toggles expand/collapse. Also toggles on entire cell if expandOnCellClick is true
        if (target.classList.contains('b-tree-expander') || (me.expandOnCellClick && cellData && cellData.record.isParent)) {
            me.toggleCollapse(cellData.record);
        }
    }

    /**
     * Called on key down in grid. Expand/collapse node on [space]
     * @private
     */
    onElementKeyDown(event) {
        // only catch space on grid element, not in editors etc...
        if (event.target === this.client.element && event.key === ' ') {
            event.preventDefault();

            this.toggleCollapse(this.grid.focusedCell.id);
        }
    }

    //endregion

    navigateRight(event) {
        const me           = this,
            grid         = me.grid,
            cellSelector = grid.focusedCell;

        if (cellSelector) {
            const record = grid.store.getById(cellSelector.id);

            // shift triggers tree navigation behaviour, also used by default for single column which is tree
            if (
                grid.columns.getById(cellSelector.columnId).tree &&
                (event.shiftKey || grid.columns.count === 1)
            ) {
                // on collapsed parent, expand
                if (record.isParent && record.instanceMeta(grid.store).collapsed) {
                    me.expand(record);
                }
                // otherwise go down
                else {
                    grid.navigateDown(event);
                }
                return false;
            }
        }
    }

    navigateLeft(event) {
        const me           = this,
            grid         = me.grid,
            cellSelector = grid.focusedCell;

        if (cellSelector) {
            const record = grid.store.getById(cellSelector.id);

            if (
                grid.columns.getById(cellSelector.columnId).tree &&
                (event.shiftKey || grid.columns.count === 1)
            ) {
                // on expanded parent, collapse
                if (record.isParent && !record.instanceMeta(grid.store).collapsed) {
                    me.collapse(record);
                }
                // otherwise go to parent
                else if (record.parent) {
                    grid.focusCell({
                        id       : record.parent.id,
                        columnId : cellSelector.columnId
                    });
                }
                return false;
            }
        }
    }
}

Tree.featureClass = 'b-tree';

GridFeatureManager.registerFeature(Tree, false, 'Grid');
GridFeatureManager.registerFeature(Tree, true, 'TreeGrid');
