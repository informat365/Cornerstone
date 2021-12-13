import DomHelper from '../../Core/helper/DomHelper.js';
import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import GridFeatureManager from '../feature/GridFeatureManager.js';

/**
 * @module Grid/feature/RegionResize
 */

/**
 * Makes the splitter between grid section draggable so you can resize grid sections.
 *
 * This feature is <strong>disabled</strong> by default.
 *
 * @extends Core/mixin/InstancePlugin
 *
 * @example
 * // enable RegionResize
 * let grid = new Grid({
 *   features: {
 *     regionResize: true
 *   }
 * });
 *
 * @demo Grid/features
 * @classtype regionResize
 * @externalexample feature/RegionResize.js
 */
export default class RegionResize extends InstancePlugin {
    // region Init
    static get $name() {
        return 'RegionResize';
    }

    construct(grid, config) {
        this.grid = grid;

        super.construct(grid, config);
    }

    doDestroy() {
        // TODO: Cleanup
        super.doDestroy();
    }

    //endregion

    //region Plugin config

    // Plugin configuration. This plugin chains some of the functions in Grid.
    static get pluginConfig() {
        return {
            chain : ['onElementTouchStart', 'onElementTouchMove', 'onElementTouchEnd', 'onElementMouseDown',
                'onElementMouseMove', 'onElementDblClick', 'onElementMouseUp', 'onSubGridCollapse', 'onSubGridExpand',
                'render']
        };
    }

    //endregion

    onElementDblClick(event) {
        const me       = this,
            grid       = me.grid,
            splitterEl = DomHelper.up(event.target, '.b-grid-splitter-collapsed');

        // If collapsed splitter is dblclicked and region is not expanding
        // It is unlikely that user might dblclick splitter twice and even if he does, nothing should happen.
        // But just in case lets not expand twice.
        if (splitterEl && !me.expanding) {
            me.expanding = true;

            let region = splitterEl.dataset.region,
                subGrid = grid.getSubGrid(region);

            // Usually collapsed splitter means corresponding region is collapsed. But in case of last two regions one
            // splitter can be collapsed in two directions. So, if corresponding region is expanded then last one is collapsed
            if (!subGrid.collapsed) {
                region = grid.getLastRegions()[1];
                subGrid = grid.getSubGrid(region);
            }

            subGrid.expand().then(() => me.expanding = false);
        }
    }

    //region Move splitter

    /**
     * Begin moving splitter.
     * @private
     * @param splitterElement Splitter element
     * @param clientX Initial x position from which new width will be calculated on move
     */
    startMove(splitterElement, clientX) {
        let me      = this,
            { grid } = me,
            region  = splitterElement.dataset.region,
            gridEl  = grid.element,
            subGrid = grid.getSubGrid(region),
            nextRegion = grid.regions[grid.regions.indexOf(region) + 1],
            nextSubGrid = grid.getSubGrid(nextRegion),
            flip = 1;

        if (subGrid.flex != null) {
            // If subgrid has flex, check if next one does not
            if (nextSubGrid.flex == null) {
                subGrid = nextSubGrid;
                flip = -1;
            }
        }

        if (splitterElement.classList.contains('b-grid-splitter-collapsed')) {
            return;
        }

        const availableWidth = subGrid.element.offsetWidth + nextSubGrid.element.offsetWidth;

        me.dragContext = {
            element       : splitterElement,
            headerEl      : subGrid.header.element,
            subGridEl     : subGrid.element,
            subGrid       : subGrid,
            originalWidth : subGrid.element.offsetWidth,
            originalX     : clientX,
            minWidth      : subGrid.minWidth || 0,
            maxWidth      : Math.min(availableWidth, subGrid.maxWidth || availableWidth),
            flip
        };

        gridEl.classList.add('b-moving-splitter');

        splitterElement.classList.add('b-moving');
    }

    onCollapseClick(subGrid, splitterEl) {
        const me    = this,
            grid    = me.grid,
            region  = splitterEl.dataset.region,
            regions = grid.getLastRegions();

        // Last splitter in the grid is responsible for collapsing/expanding last 2 regions and is always related to the
        // left one. Check if we are working with last splitter
        if (regions[0] === region) {
            const lastSubGrid = grid.getSubGrid(regions[1]);
            if (lastSubGrid.collapsed) {
                lastSubGrid.expand();
                return;
            }
        }

        subGrid.collapse();
    }

    onExpandClick(subGrid, splitterEl) {
        const me    = this,
            grid    = me.grid,
            region  = splitterEl.dataset.region,
            regions = grid.getLastRegions();

        // Last splitter in the grid is responsible for collapsing/expanding last 2 regions and is always related to the
        // left one. Check if we are working with last splitter
        if (regions[0] === region) {
            if (!subGrid.collapsed) {
                const lastSubGrid = grid.getSubGrid(regions[1]);
                lastSubGrid.collapse();
                return;
            }
        }

        subGrid.expand();
    }

    /**
     * Update splitter position.
     * @private
     * @param newClientX
     */
    updateMove(newClientX) {
        const { dragContext } = this;

        if (dragContext) {
            const difX = newClientX - dragContext.originalX,
                newWidth =  Math.min(dragContext.maxWidth, dragContext.originalWidth + difX * dragContext.flip);

            // SubGrids monitor their own size and keep any splitters synced
            dragContext.subGrid.width = Math.max(newWidth, dragContext.minWidth);
        }
    }

    /**
     * Stop moving splitter.
     * @private
     */
    endMove() {
        const me      = this,
            dragContext = me.dragContext;

        if (dragContext) {
            me.grid.element.classList.remove('b-moving-splitter');
            dragContext.element.classList.remove('b-moving');
            me.dragContext = null;
        }
    }

    //endregion

    //region Events

    /**
     * Start moving splitter on touch start.
     * @private
     * @param event
     */
    onElementTouchStart(event) {
        const me     = this,
            target = event.target;

        if (target.classList.contains('b-grid-splitter')) {
            me.startMove(target, event.touches[0].clientX);
            event.preventDefault();
        }
    }

    /**
     * Move splitter on touch move.
     * @private
     * @param event
     */
    onElementTouchMove(event) {
        if (this.dragContext) {
            this.updateMove(event.touches[0].clientX);
            event.preventDefault();
        }
    }

    /**
     * Stop moving splitter on touch end.
     * @private
     * @param event
     */
    onElementTouchEnd(event) {
        if (this.dragContext) {
            this.endMove();
            event.preventDefault();
        }
    }

    /**
     * Start moving splitter on mouse down (on splitter).
     * @private
     * @param event
     */
    onElementMouseDown(event) {
        const me     = this,
            target   = event.target,
            splitter = event.target.closest(':not(.b-row-reordering):not(.b-dragging-event):not(.b-dragging-task):not(.b-dragging-header):not(.b-dragselecting) .b-grid-splitter'),
            subGrid  = splitter && me.grid.getSubGrid(splitter.dataset.region);

        // Only care about left clicks, avoids a bug found by monkeys
        if (event.button === 0) {
            // In case of touch screen inner splitter has 100% height and we should handle
            // it as target too
            if (target.classList.contains('b-grid-splitter') || target.classList.contains('b-grid-splitter-inner')) {
                me.startMove(splitter, event.clientX);
                event.preventDefault();
            }
            else if (target.classList.contains('b-icon-collapse-gridregion')) {
                me.onCollapseClick(subGrid, splitter);
            }
            else if (target.classList.contains('b-icon-expand-gridregion')) {
                me.onExpandClick(subGrid, splitter);
            }
        }
    }

    /**
     * Move splitter on mouse move.
     * @private
     * @param event
     */
    onElementMouseMove(event) {
        if (this.dragContext) {
            this.updateMove(event.clientX);
            event.preventDefault();
        }
    }

    /**
     * Stop moving splitter on mouse up.
     * @private
     * @param event
     */
    onElementMouseUp(event) {
        if (this.dragContext) {
            this.endMove();
            event.preventDefault();
        }
    }

    onSubGridCollapse(subGrid) {
        const splitterEl = this.grid.resolveSplitter(subGrid),
            regions      = this.grid.getLastRegions();

        // if last region was collapsed
        if (regions[1] === subGrid.region) {
            splitterEl.classList.add('b-grid-splitter-allow-collapse');
        }
    }

    onSubGridExpand(subGrid) {
        const splitterEl = this.grid.resolveSplitter(subGrid);
        splitterEl.classList.remove('b-grid-splitter-allow-collapse');
    }

    //endregion

    render() {
        const { regions, subGrids } = this.grid;

        // Multiple regions, only allow collapsing to the edges by hiding buttons
        if (regions.length > 2) {
            // Only works in a 3 subgrid scenario. To support more subgrids we have to merge splitters or something
            // on collapse. Not going down that path currently...
            subGrids[regions[0]].splitterElement.classList.add('b-left-only');
            subGrids[regions[1]].splitterElement.classList.add('b-right-only');
        }
    }
}

RegionResize.featureClass = 'b-split';

GridFeatureManager.registerFeature(RegionResize);
