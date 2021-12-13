import Grid from './Grid.js';
import Store from '../../Core/data/Store.js';
import BryntumWidgetAdapterRegister from '../../Core/adapter/widget/util/BryntumWidgetAdapterRegister.js';
// tree feature will be added by default to TreeGrid, but needs to be imported
import '../feature/Tree.js';
// not used here but enables using `type : tree` in a column config (exactly one required)
import '../column/TreeColumn.js';

/**
 * @module Grid/view/TreeGrid
 */

/**
 * A TreeGrid, a Tree combined with a Grid. Must be configured with exactly one {@link Grid.column.TreeColumn} (`type: tree`), but can also have an
 * arbitrary number of other columns. Most features that can be used with Grid also works with TreeGrid, with the
 * exception of the Group feature.
 * @extends Grid/view/Grid
 *
 * @classtype treegrid
 * @externalexample grid/TreeGrid.js
 */
export default class TreeGrid extends Grid {

    static get $name() {
        return 'TreeGrid';
    }

    //region Plugged in functions / inherited configs

    /**
     * Store that holds records to display in the TreeGrid, or a store config object.
     * If you supply a Store, make sure it is configured with `tree : true` to handle tree data.
     * A store will be created if none is specified
     * @config {Core.data.Store|Object} store
     * @default
     */

    /**
     * Collapse an expanded node or expand a collapsed. Optionally forcing a certain state.
     *
     * @function toggleCollapse
     * @param {String|Number|Core.data.Model} idOrRecord Record (the node itself) or id of a node to toggle
     * @param {Boolean} [collapse] Force collapse (true) or expand (false)
     * @param {Boolean} [skipRefresh] Set to true to not refresh rows (if calling in batch)
     * @returns {Promise}
     * @category Feature shortcuts
     */

    /**
     * Collapse a single node.
     *
     * @function collapse
     * @param {String|Number|Core.data.Model} idOrRecord Record (the node itself) or id of a node to collapse
     * @returns {Promise}
     * @category Feature shortcuts
     */

    /**
     * Expand a single node.
     *
     * @function expand
     * @param {String|Number|Core.data.Model} idOrRecord Record (the node itself) or id of a node to expand
     * @returns {Promise}
     * @category Feature shortcuts
     */

    /**
     * Expands parent nodes to make this node "visible".
     *
     * @function expandTo
     * @param {String|Number|Core.data.Model} idOrRecord Record (the node itself) or id of a node
     * @returns {Promise}
     * @category Feature shortcuts
     */

    //endregion

    //region Store

    /**
     * Get/set the store used by this TreeGrid. Accepts a config or a Store. If assigning an already existing Store,
     * it must be configured with `tree: true`
     * @property {Core.data.Store|Object}
     */
    set store(store) {
        if (store && !store.tree) {
            // existing store instance, not much we can do about it
            if (store instanceof Store) {
                throw new Error('TreeGrid requires a Store configured with tree : true');
            }
            // store config, enable tree
            else {
                store.tree = true;
            }
        }

        super.store = store;
    }

    get store() {
        return super.store;
    }

    //endregion
}

BryntumWidgetAdapterRegister.register('treegrid', TreeGrid);
