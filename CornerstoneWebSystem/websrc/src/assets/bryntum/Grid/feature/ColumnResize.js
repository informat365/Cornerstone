import ResizeHelper from '../../Core/helper/ResizeHelper.js';
import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import GridFeatureManager from '../feature/GridFeatureManager.js';
import BrowserHelper from '../../Core/helper/BrowserHelper.js';

/**
 * @module Grid/feature/ColumnResize
 */

/**
 * Enables user to resize columns by dragging a handle on the righthand side of the header. To get notified about column
 * resize listen to `change` event on {@link Grid.data.ColumnStore columns} store.
 *
 * This feature is <strong>enabled</strong> by default.
 *
 * @extends Core/mixin/InstancePlugin
 *
 * @demo Grid/columns
 * @classtype columnResize
 * @externalexample feature/ColumnResize.js
 */
export default class ColumnResize extends InstancePlugin {

    static get $name() {
        return 'ColumnResize';
    }

    static get defaultConfig() {
        return {
            /**
             * Resize all cells below a resizing header during dragging.
             * `'auto'` means `true` on non-mobile platforms.
             * @config {String/Boolean}
             * @default
             */
            liveResize : 'auto'
        };
    }

    //region Init

    construct(grid, config) {
        const me = this;

        me.grid = grid;

        super.construct(grid, config);

        me.resizer = new ResizeHelper({
            name           : 'columnResize',
            targetSelector : '.b-grid-header',
            handleSelector : '.b-grid-header-resize-handle',
            outerElement   : grid.element,
            listeners      : {
                beforeresizestart : me.onBeforeResizeStart,
                resizestart       : me.onResizeStart,
                resizing          : me.onResizing,
                resize            : me.onResize,
                thisObj           : me
            }
        });
    }

    //endregion

    // This plugin needs no functions chaining into the client grid
    static get pluginConfig() {
        return [];
    }

    set liveResize(liveResize) {
        if (liveResize === 'auto') {
            liveResize = !BrowserHelper.isMobileSafari;
        }
        this._liveResize = liveResize;
    }

    get liveResize() {
        return this._liveResize;
    }

    doDestroy() {
        this.resizer && this.resizer.destroy();
        super.doDestroy();
    }

    //region Events

    onBeforeResizeStart() {
        return !this.disabled;
    }

    onResizeStart({ context }) {
        const
            { grid, resizer } = this,
            column = context.column = grid.columns.getById(context.element.dataset.columnId);

        resizer.minWidth = column.minWidth;

        // remove minWidth value as it's used as a rendering workaround for IE flexbox bugs
        context.element.style.minWidth = '';

        grid.element.classList.add('b-column-resizing');
    }

    /**
     * Handle drag event - resize the column live unless it's a touch gesture
     * @private
     */
    onResizing({ context }) {
        if (context.valid && this.liveResize) {
            this.grid.dragResizing = true;
            context.column.width = context.newWidth;
        }
    }

    /**
     * Handle drop event (only used for touch)
     * @private
     */
    onResize({ context }) {
        const
            { grid } = this,
            { column } = context;

        grid.element.classList.remove('b-column-resizing');

        if (context.valid) {
            if (this.liveResize) {
                grid.dragResizing = false;
                grid.afterColumnsResized();
            }
            else {
                column.width = context.newWidth;
            }

            // In case of IE 11 we should calculate flex basis to fix header width
            // covered by ColumnResize.t
            if (BrowserHelper.isIE11 && column.parent && column.parent.flex) {
                const parent = column.parent,
                    headerEl = grid.getHeaderElement(parent.id);

                headerEl.style.flexBasis = parent.children.reduce((result, column) => {
                    return result + grid.getHeaderElement(column.id).offsetWidth;
                }, 0) + 'px';
            }
        }
    }

    //endregion
}

GridFeatureManager.registerFeature(ColumnResize, true);
