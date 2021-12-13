import Base from '../../../Core/Base.js';
import Column from '../../column/Column.js';
import GridBase from '../GridBase.js';
import SubGrid from '../SubGrid.js';
import ObjectHelper from '../../../Core/helper/ObjectHelper.js';

/**
 * @module Grid/view/mixin/GridSubGrids
 */

/**
 * Mixin for grid that handles SubGrids. Each SubGrid is scrollable horizontally separately from the other SubGrids.
 * Having two SubGrids allows you to achieve what is usually called locked or frozen columns.
 *
 * By default a Grid has two SubGrids, one named 'locked' and one 'normal'. The `locked` region has fixed width, while
 * the `normal` region grows to fill all available width (flex).
 *
 * Which SubGrid a column belongs to is
 * determined using its {@link Grid.column.Column#config-region} config. For example to put a column into the locked
 * region, specify `{ region: 'locked' }`. For convenience, a column can be put in the locked region using
 * `{ locked: true }`.
 *
 * @example
 * { data: 'name', text: 'Name', locked: true }
 * { data: 'age', text: 'Age', region: 'locked' }
 *
 * @demo Grid/features
 * @mixin
 */
export default Target => class GridSubGrids extends (Target || Base) {
    //region Init

    /**
     * Initializes subGrids from subGrid configs in config.subGridConfigs. Regions specified on columns but not The default config.subGridConfigs is:
     * { normal: { flex: 1 } }
     * @private
     * @category SubGrid
     */
    initSubGrids() {
        const
            me          = this,
            oldRegions  = me._regions,
            usedRegions = new Set();

        let first = true,
            hasCalculatedWidth,
            subGridConfig,
            subGridColumns;

        // TODO: move into subgrid or columnmanager
        for (const column of me.columns.records) {
            if (column.region && !me.subGridConfigs[column.region]) {
                me.subGridConfigs[column.region] = {};
            }
            usedRegions.add(column.region);
        }

        // If "reconfiguring" columns, we are going to create new subgrids so destroy any existing
        if (oldRegions) {
            oldRegions.forEach(region => me.subGrids[region].destroy());
        }

        /**
         * An object containing the {@link Grid.view.SubGrid} region instances, indexed by subGrid id ('locked', normal'...)
         * @property {Object} subGrids
         * @readonly
         */
        me.subGrids = {};

        me._regions = Array.from(usedRegions);
        me._regions.sort();

        // Implementer has provided configs for other subGrids but not normal, put defaults in place
        if (me.subGridConfigs.normal && Object.keys(me.subGridConfigs.normal).length === 0) {
            me.subGridConfigs.normal = GridBase.defaultConfig.subGridConfigs.normal;
        }

        for (const region of me._regions) {
            subGridColumns = me.columns.makeChained(column => column.region === region, ['region']);
            subGridConfig = ObjectHelper.assign({
                parent      : me,
                grid        : me,
                store       : me.store,
                rowManager  : me.rowManager,
                region      : region,
                headerClass : me.subGridConfigs[region].headerClass || me.headerClass,
                columns     : subGridColumns,
                hideHeaders : me.hideHeaders
            }, me.subGridConfigs[region]);

            hasCalculatedWidth = false;

            if (!subGridConfig.flex && !subGridConfig.width) {
                subGridConfig.width = subGridColumns.totalFixedWidth;
                hasCalculatedWidth = true;
            }

            me.subGrids[region] = new SubGrid(subGridConfig);

            // Must be set after creation, otherwise reset in SubGrid#set width
            me.subGrids[region].hasCalculatedWidth = hasCalculatedWidth;

            if (first) {
                // Have already done lookups for this in a couple of places, might as well store it...
                me.subGrids[region].isFirstRegion = true;
                first                             = false;
            }
        }
    }

    get items() {
        // Return ths SubGrids in Region order.
        // This property is referenced by Container#startConfigure, so the regions and SubGrids
        // are initialized early.
        return this.regions.map(r => this.subGrids[r]);
    }

    //endregion

    //region Iteration & calling

    /**
     * Iterate over all subGrids, calling the supplied function for each.
     * @param {Function} fn Function to call for each instance
     * @param {Object} thisObj `this` reference to call the function in, defaults to the subGrid itself
     * @category SubGrid
     * @internal
     */
    eachSubGrid(fn, thisObj = null) {
        const me = this;
        //for (let subGrid of Object.values(this.subGrids)) {
        me.regions.forEach((region, i) => {
            const subGrid = me.subGrids[region];
            fn.call(thisObj || subGrid, subGrid, i++);
        });
    }

    eachWidget(fn, deep = true) {
        const me = this;
        me.regions.forEach((region) => {
            const widget = me.subGrids[region];
            if (fn(widget) === false) {
                return;
            }
            if (deep && widget.eachWidget) {
                widget.eachWidget(fn, deep);
            }
        });
    }

    /**
     * Call a function by name for all subGrids (that have the function).
     * @param {String} fnName Name of function to call, uses the subGrid itself as `this` reference
     * @param params Parameters to call the function with
     * @return {*} Return value from first SubGrid is returned
     * @category SubGrid
     * @internal
     */
    callEachSubGrid(fnName, ...params) {
        // TODO: make object { normal: retval, locked: retval } to return? or store. revisit when needed
        let me          = this,
            returnValue = null;
        //Object.values(this.subGrids).forEach((subGrid, i) => {
        me.regions.forEach((region, i) => {
            const subGrid = me.subGrids[region];
            if (subGrid[fnName]) {
                const partialReturnValue = subGrid[fnName](...params);
                if (i === 0) returnValue = partialReturnValue;
            }
        });
        return returnValue;
    }

    //endregion

    //region Getters

    /**
     * This method should return names of the two last regions in the grid as they are visible in the UI. In case
     * `regions` property cannot be trusted, use different approach. Used by SubGrid and RegionResize to figure out
     * which region should collapse or expand.
     * @returns {String[]}
     * @private
     * @category SubGrid
     */
    getLastRegions() {
        const result = this.regions.slice(-2);
        // ALWAYS return array of length 2 in order to avoid extra conditions. Normally should not be called with 1 region
        return result.length === 2 ? result : [result[0], result[0]];
    }

    /**
     * This method should return right neighbour for passed region, or left neighbour in case last visible region is passed.
     * This method is used to decide which subgrid should take space of the collapsed one.
     * @param {String} region
     * @returns {String}
     * @private
     * @category SubGrid
     */
    getNextRegion(region) {
        const regions = this.regions;

        // return next region or next to last
        return regions[regions.indexOf(region) + 1] || regions[regions.length - 2];
    }

    getPreviousRegion(region) {
        return this.regions[this.regions.indexOf(region) - 1];
    }

    /**
     * Returns the subGrid for the specified region.
     * @param {String} region Region, eg. locked or normal (per default)
     * @returns {Grid.view.SubGrid} A subGrid
     * @category SubGrid
     */
    getSubGrid(region) {
        return this.subGrids[region];
    }

    /**
     * Get the SubGrid that contains specified column
     * @param {String|Grid.column.Column} column Column "name" or column object
     * @returns {Grid.view.SubGrid}
     * @category SubGrid
     */
    getSubGridFromColumn(column) {
        column = column instanceof Column ? column : this.columns.get(column) || this.columns.getById(column);

        return this.getSubGrid(column.region);
    }

    get regions() {
        if (!this._regions) {
            this.initSubGrids();
        }
        return this._regions;
    }

    //endregion

    /**
     * Returns splitter element for subgrid
     * @param {Grid.view.SubGrid|String} subGrid
     * @returns {HTMLElement}
     * @private
     * @category SubGrid
     */
    resolveSplitter(subGrid) {
        const me = this,
            regions = me.getLastRegions();

        let region = subGrid instanceof SubGrid ? subGrid.region : subGrid;

        if (regions[1] === region) {
            region = regions[0];
        }

        return this.subGrids[region].splitterElement;
    }

    // This does not need a className on Widgets.
    // Each *Class* which doesn't need 'b-' + constructor.name.toLowerCase() automatically adding
    // to the Widget it's mixed in to should implement thus.
    get widgetClass() {}
};
