//region Import

import Base from '../../Core/Base.js';

import AjaxStore from '../../Core/data/AjaxStore.js';
import DomDataStore from '../../Core/data/DomDataStore.js';
import Store from '../../Core/data/Store.js';

import BrowserHelper from '../../Core/helper/BrowserHelper.js';
import DomHelper from '../../Core/helper/DomHelper.js';
import EventHelper from '../../Core/helper/EventHelper.js';
import { base } from '../../Core/helper/MixinHelper.js';
import ObjectHelper from '../../Core/helper/ObjectHelper.js';
import TemplateHelper from '../../Core/helper/TemplateHelper.js';
import Rectangle from '../../Core/helper/util/Rectangle.js';

import BryntumWidgetAdapterRegister from '../../Core/adapter/widget/util/BryntumWidgetAdapterRegister.js';
import Mask from '../../Core/widget/Mask.js';
import Container from '../../Core/widget/Container.js';
import GlobalEvents from '../../Core/GlobalEvents.js';

import LocaleManager from '../../Core/localization/LocaleManager.js';
import Pluggable from '../../Core/mixin/Pluggable.js';
import State from '../../Core/mixin/State.js';
import ColumnStore, { columnResizeEvent } from '../data/ColumnStore.js';
import GridRowModel from '../data/GridRowModel.js';
import RowManager from '../row/RowManager.js';
import ScrollManager from '../util/ScrollManager.js';
import GridScroller from '../util/GridScroller.js';
import Header from './Header.js';

import GridElementEvents from './mixin/GridElementEvents.js';
import GridFeatures from './mixin/GridFeatures.js';
import GridNavigation from './mixin/GridNavigation.js';
import GridResponsive from './mixin/GridResponsive.js';
import GridSelection from './mixin/GridSelection.js';
import GridState from './mixin/GridState.js';
import GridSubGrids from './mixin/GridSubGrids.js';

import Column from '../column/Column.js';

// Needed since Grid now has its own localization
import '../localization/En.js';

//endregion

/**
 * @module Grid/view/GridBase
 */

const
    resolvedPromise = new Promise(resolve => resolve()),
    defaultScrollOptions = {
        block  : 'nearest',
        inline : 'nearest'
    },
    preserveScroll = {
        dataset : 1,
        filter  : 1
    };

/**
 * A thin base class for {@link Grid.view.Grid}. Does not include any features by default, allowing smaller custom built bundles if used
 * in place of {@link Grid.view.Grid}.
 *
 * **NOTE:** In most scenarios you probably want to use Grid instead of GridBase.

 * @extends Core/widget/Widget
 * @mixes Core/mixin/Events
 * @mixes Core/mixin/Pluggable
 * @mixes Core/mixin/State
 * @mixes Grid/view/mixin/GridElementEvents
 * @mixes Grid/view/mixin/GridFeatures
 * @mixes Grid/view/mixin/GridResponsive
 * @mixes Grid/view/mixin/GridSelection
 * @mixes Grid/view/mixin/GridState
 * @mixes Grid/view/mixin/GridSubGrids
 */
export default class GridBase extends base(Container).mixes(
    Pluggable,
    State,
    GridElementEvents,
    GridFeatures,
    GridNavigation,
    GridResponsive,
    GridSelection,
    GridState,
    GridSubGrids
) {
    //region Config

    static get $name() {
        return 'GridBase';
    }

    // Default settings, applied in grids constructor.
    static get defaultConfig() {
        return {
            /**
             * Row height in pixels. When set to null, an empty row will be measured and its height will be used as
             * default row height, enabling it to be controlled using CSS
             * @config {Number}
             * @default null
             * @category Common
             */
            rowHeight : null,

            // used if no rowHeight specified and none found in CSS. not public since our themes have row height
            // specified and this is more of an internal failsafe
            defaultRowHeight : 45,

            /**
             * Text to display when there is no data to display in the grid
             * @config {String}
             * @default
             * @category Common
             */
            emptyText : 'L{noRows}',

            /**
             * Refresh entire row when a record changes (`true`) or, if possible, only the cells affected (`false`).
             *
             * When this is set to `false`, then if a column uses a renderer, cells in that column will still
             * be updated because it is impossible to know whether the cells value will be affected.
             *
             * If a standard, provided Column class is used with no custom renderer, its cells will only be updated
             * if the column's {@link Grid.column.Column#config-field} is changed.
             * @config {Boolean}
             * @default
             * @category Misc
             */
            fullRowRefresh : true,

            /**
             * Read only or not
             * @config {Boolean}
             * @default false
             * @category Common
             */
            readOnly : null,

            /**
             * True to not create any grid column headers
             * @config {Boolean}
             * @default false
             * @category Misc
             */
            hideHeaders : null,

            /**
             * Show "Remove row" item in context menu (if enabled and grid not read only)
             * @config {Boolean}
             * @default
             * @category Misc
             */
            showRemoveRowInContextMenu : true,

            /**
             * Automatically set grids height to fit all rows (no scrolling in the grid). In general you should avoid
             * using `autoHeight: true`, since it will bypass Grids virtual rendering and render all rows at once, which
             * in a larger grid is really bad for performance.
             * @config {Boolean}
             * @default false
             * @category Layout
             */
            autoHeight : null,

            /**
             * Store that holds records to display in the grid, or a store config object.
             * A store will be created if none is specified
             * @config {Core.data.Store/Object}
             * @category Common
             */
            store : {},

            /**
             * Data to set in grids store (a Store will be created if none is specified)
             * @config {Object[]}
             * @category Common
             */
            data : null,

            /**
             * Column definitions for the grid, will be used to create Column instances that are added to a ColumnStore:
             *
             * ```
             * new Grid({
             *   columns : [
             *     { text : 'Alias', field : 'alias' },
             *     { text : 'Superpower', field : 'power' }
             *   ]
             * });
             * ```
             *
             * Also accepts a store config object:
             *
             * ```
             * new Grid({
             *   columns : {
             *     data : [
             *       { text : 'Alias', field : 'alias' },
             *       { text : 'Superpower', field : 'power' }
             *     ],
             *     listeners : {
             *       update() {
             *         // Some update happened
             *       }
             *     }
             *   }
             * });
             * ```
             *
             * This store can be accessed using {@link #property-columns}:
             *
             * ```
             * grid.columns.add({ field : 'column', text : 'New column' });
             * ```
             * @config {Object[]|Object}
             * @category Common
             */
            columns : [],

            /**
             * Region to which columns are added when they have none specified
             * @config {String}
             * @default
             * @category Misc
             */
            defaultRegion : 'normal',

            /**
             * true to destroy the store when the grid is destroyed
             * @config {Boolean}
             * @default false
             * @category Misc
             */
            destroyStore : null,

            /**
             * Set to true to allow text selection in the grid cells
             * @config {Boolean}
             * @default false
             * @category Selection
             */
            enableTextSelection : null,

            /**
             * A message to be shown when a store is performing a remote operation.
             * @config {String}
             * @default "Loading..."
             * @category Misc
             */
            loadMask : 'L{loadMask}',

            /**
             * Set to `false` to inhibit column lines
             * @config {Boolean}
             * @default
             * @category Misc
             */
            columnLines : true,

            /**
             * Set to `true` to stretch the last column in a grid with all fixed width columns
             * to fill extra available space if the grid's width is wider than the sum of all
             * configured column widths.
             * @config {Boolean}
             * @default
             * @category Layout
             */
            fillLastColumn : true,

            /**
             * Set to `false` to only measure cell contents when double clicking the edge between column headers.
             * @config {Boolean}
             * @default
             * @category Layout
             */
            resizeToFitIncludesHeader : true,

            /**
             * Set to `false` to prevent remove row animation and remove the delay related to that.
             * @config {Boolean}
             * @default
             * @category Misc
             */
            animateRemovingRows : !BrowserHelper.isIE11, // IE11 doesn't have reliable firing of transitionend

            /**
             * Set to `true` to not get a warning when using another base class than GridRowModel for your grid data. If
             * you do, and would like to use the full feature set of the grid then include the fields from GridRowModel
             * in your model definition.
             * @config {Boolean}
             * @default false
             * @category Misc
             */
            disableGridRowModelWarning : null,

            loadMaskErrorIcon : 'b-icon b-icon-warning',

            headerClass : Header,

            testPerformance : false,
            // TODO: break out as strategies
            positionMode    : 'translate', // translate, translate3d, position
            rowScrollMode   : 'move', // move, dom, all

            /**
             * Grid monitors window resize by default.
             * @config {Boolean}
             * @default true
             * @category Misc
             */
            monitorResize : true,

            /**
             * An object containing Feature configuration objects (or `true` if no configuration is required)
             * keyed by the Feature class name in all lowercase.
             * @config {Object}
             * @category Common
             * @typings any
             */
            features : true,

            /**
             * An object containing sub grid configuration objects keyed by a `region` property.
             * By default, grid has a 'locked' region (if configured with locked columns) and a 'normal' region.
             * The 'normal' region defaults to use `flex: 1`.
             *
             * This config can be used to reconfigure the "built in" sub grids or to define your own.
             * ```
             * // Redefining the "built in" regions
             * new Grid({
             *   subGridConfigs : {
             *     locked : { flex : 1 },
             *     normal : { width : 100 }
             *   }
             * });
             *
             * // Defining your own multi region sub grids
             * new Grid({
             *   subGridConfigs : {
             *     left   : { width : 100 },
             *     middle : { flex : 1 },
             *     right  : { width  : 100 }
             *   },
             *
             *   columns : {
             *     { field : 'manufacturer', text: 'Manufacturer', region : 'left' },
             *     { field : 'model', text: 'Model', region : 'middle' },
             *     { field : 'year', text: 'Year', region : 'middle' },
             *     { field : 'sales', text: 'Sales', region : 'right' }
             *   }
             * });
             * ```
             * @config {Object}
             * @category Misc
             */
            subGridConfigs : {
                normal : { flex : 1 }
            },

            /**
             * Configures whether the grid is scrollable in the `Y` axis. This is used to configure a {@link Grid.util.GridScroller}.
             * See the {@link #config-scrollerClass} config option.
             * @config {Object}
             * @category Scrolling
             */
            scrollable : {
                // Just Y for now until we implement a special grid.view.Scroller subclass
                // Which handles the X scrolling of subgrids.
                overflowY : true
            },

            /**
             * The class to instantiate to use as the {@link #config-scrollable}. Defaults to {@link Grid.util.GridScroller}.
             * @config {Core.helper.util.Scroller}
             * @internal
             * @category Scrolling
             */
            scrollerClass : GridScroller,

            /**
             * Configure as `true` to have the grid show a red "changed" tag in cells who's
             * field value has changed and not yet been committed.
             * @config {Boolean}
             * @default false
             * @category Misc
             */
            showDirty : null,

            loadMaskHideTimeout : 3000,

            refreshSuspended : 0,

            transitionDuration : 500,

            /**
             * Event which is used to show context menus.
             * Available options are: 'contextmenu', 'click', 'dblclick'.
             * Default value is 'contextmenu'
             * @config {String}
             * @category Misc
             */
            contextMenuTriggerEvent : 'contextmenu',

            localizableProperties : ['emptyText', 'loadMask'],

            localeClass : 'Grid'

            // Grid requires a size to be considered visible
            //requireSize : true
        };
    }

    static getLKey() {
        return 'b95e748d-14b8-11ea-9c13-d094663d5c88';
    }

    static get properties() {
        return {
            _selectedRecords      : [],
            _verticalScrollHeight : 0,
            virtualScrollHeight   : 0,
            _scrollTop            : null
        };
    }

    //endregion

    //region Init-destroy

    finishConfigure(config) {
        const me = this;

        super.finishConfigure(config);

        me.scrollManager = new ScrollManager({
            grid            : me,
            // Horizontal scrolling might happen on different subgrids, but vertical is always on same element
            verticalElement : me.scrollable.element
        });

        // When locale is applied columns react and change, which triggers `change` event on columns store for each
        // changed column, and every change normally triggers rendering view. This overhead becomes noticeable with
        // larger amount of columns. So we set two listeners to locale events: prioritized listener to be executed first
        // and suspend renderContents method and unprioritized one to resume method and call it immediately.
        LocaleManager.on({
            locale  : 'onBeforeLocaleChange',
            prio    : 1,
            thisObj : me
        });

        LocaleManager.on({
            locale  : 'onLocaleChange',
            prio    : -1,
            thisObj : me
        });

        GlobalEvents.on({
            theme   : 'onThemeChange',
            thisObj : me
        });

        // Add the extra grid classes to the element
        me.setGridClassList(me.element.classList);
    }

    onBeforeLocaleChange() {
        this._suspendRenderContentsOnColumnsChanged = true;
    }

    onLocaleChange() {
        this._suspendRenderContentsOnColumnsChanged = false;
        if (this.isPainted) {
            this.renderContents();
        }
    }

    finalizeInit() {
        super.finalizeInit();

        if (this.store.isLoading) {
            // Maybe show loadmask if store is already loading when grid is constructed
            this.onStoreBeforeRequest();
        }
    }

    /**
     * Cleanup
     * @private
     */
    doDestroy() {
        const me = this;

        me.storeDetacher && me.storeDetacher();

        for (const feature of Object.values(me.features)) {
            feature.destroy && feature.destroy();
        }

        if (me.columns) {
            me.columns = null;
        }

        if (me.store) {
            me.store = null;
        }

        super.doDestroy();
    }

    /**
     * Adds extra classes to the Grid element after it's been configured.
     * Also iterates through features, thus ensuring they have been initialized.
     * @private
     */
    setGridClassList(classList) {
        const me = this;

        classList.add(`b-grid-${me.positionMode}`);

        // TODO: enableTextSelection should be a setter, enabling toggling at any time
        if (!me.enableTextSelection) {
            classList.add('b-grid-notextselection');
        }

        if (me.autoHeight) {
            classList.add('b-autoheight');
        }

        if (me.readOnly) {
            classList.add('b-readonly');
        }

        if (me.fillLastColumn) {
            classList.add('b-fill-last-column');
        }

        if (me.showDirty) {
            classList.add('b-show-dirty');
        }

        for (const featureName in me.features) {
            let feature = me.features[featureName],
                featureClass;

            if (feature.constructor.hasOwnProperty('featureClass')) {
                featureClass = feature.constructor.featureClass;
            }
            else {
                featureClass = `b-${(feature instanceof Base ? feature.$name : feature.constructor.name)}`;
            }

            if (featureClass) {
                classList.add(featureClass.toLowerCase());
            }
        }
    }

    //endregion

    //region Functions & events injected by features

    // For documentation & typings purposes

    //region Feature events

    /**
     * *Only when the {@link Grid.feature.Tree} feature is enabled*.
     * <p>Fired before a record toggles its collapsed state.
     * @event beforeToggleNode
     * @param {Core.data.Model} record The record being toggled.
     * @param {Boolean} collapse `true` if the node is being collapsed.
     */
    /**
     * *Only when the {@link Grid.feature.Tree} feature is enabled*.
     * <p>Fired after a record has been collapsed.
     * @event collapseNode
     * @param {Core.data.Model} record The record which has been collapsed.
     */
    /**
     * *Only when the {@link Grid.feature.Tree} feature is enabled*.
     * <p>Fired after a record has been expanded.
     * @event expandNode
     * @param {Core.data.Model} record The record which has been expanded.
     */
    /**
     * *Only when the {@link Grid.feature.Tree} feature is enabled*.
     * <p>Fired after a record toggles its collapsed state.
     * @event toggleNode
     * @param {Core.data.Model} record The record being toggled.
     * @param {Boolean} collapse `true` if the node is being collapsed.
     */

    //endregion

    /**
     * Collapse all groups/parent nodes.
     *
     * *NOTE: Only available when the {@link Grid/feature/Group Group} or the {@link Grid/feature/Tree Tree} feature is enabled.*
     *
     * @function collapseAll
     * @category Feature shortcuts
     */

    /**
     * Expand all groups/parent nodes.
     *
     * *NOTE: Only available when the {@link Grid/feature/Group Group} or the {@link Grid/feature/Tree Tree} feature is enabled.*
     *
     * @function expandAll
     * @category Feature shortcuts
     */

    /**
     * Start editing specified cell. If no cellContext is given it starts with the first cell in the first row.
     *
     * *NOTE: Only available when the {@link Grid/feature/CellEdit CellEdit} feature is enabled.*
     *
     * @function startEditing
     * @param {Object} cellContext Cell specified in format { id: 'x', columnId/column/field: 'xxx' }. See {@link Grid.view.Grid#function-getCell} for details.
     * @returns {Boolean}
     * @category Feature shortcuts
     */

    /**
     * Collapse an expanded node or expand a collapsed. Optionally forcing a certain state.
     *
     * *NOTE: Only available when the {@link Grid/feature/Tree Tree} feature is enabled.*
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
     * *NOTE: Only available when the {@link Grid/feature/Tree Tree} feature is enabled.*
     *
     * @function collapse
     * @param {String|Number|Core.data.Model} idOrRecord Record (the node itself) or id of a node to collapse
     * @returns {Promise}
     * @category Feature shortcuts
     */

    /**
     * Expand a single node.
     *
     * *NOTE: Only available when the {@link Grid/feature/Tree Tree} feature is enabled.*
     *
     * @function expand
     * @param {String|Number|Core.data.Model} idOrRecord Record (the node itself) or id of a node to expand
     * @returns {Promise}
     * @category Feature shortcuts
     */

    /**
     * Expands parent nodes to make this node "visible".
     *
     * *NOTE: Only available when the {@link Grid/feature/Tree Tree} feature is enabled.*
     *
     * @function expandTo
     * @param {String|Number|Core.data.Model} idOrRecord Record (the node itself) or id of a node
     * @returns {Promise}
     * @category Feature shortcuts
     */

    //endregion

    //region Grid template & elements

    template(data) {
        const virtualScrollerStyle = BrowserHelper.isFirefox ? `height:${DomHelper.scrollBarWidth}px` : '';

        return TemplateHelper.tpl`
            <div tabindex="-1">
                <header reference="headerContainer" class="b-grid-header-container ${this.hideHeaders ? 'b-hidden' : ''}"></header>
                <div reference="bodyContainer" class="b-grid-body-container">
                    <div reference="verticalScroller" class="b-grid-vertical-scroller"></div>
                </div>
                <div reference="virtualScrollers" class="b-virtual-scrollers ${DomHelper.scrollBarWidth ? '' : 'b-overlay-scrollbar'}" style="${virtualScrollerStyle}"></div>
                <footer reference="footerContainer" class="b-grid-footer-container b-hidden"></footer>
            </div>
        `;
    }

    get contentElement() {
        return this.verticalScroller;
    }

    get overflowElement() {
        return this.bodyContainer;
    }

    get focusElement() {
        return this.element;
    }

    //endregion

    //region Columns

    set columns(columns) {
        const me = this;

        if (me._columnStore) {
            if (columns) {
                // TODO: @johan: reconfiguring, ie changing whole column set should work.
                // That could mean a total recalculation of subGrids.
                // That's not possible right now, so
                //throw new Error('Cannot reconfigure column set');
                // me._columnStore.clear();
                me._columnStore.data = columns;
            }
            else {
                me._columnStore.destroy();
            }
        }
        else {
            // ColumnStore instance
            if (columns instanceof ColumnStore) {
                if (!columns.grid) {
                    columns.grid = me;
                }
                me._columnStore = columns;
            }
            // Array of columns
            else if (Array.isArray(columns)) {
                me._columnStore = new ColumnStore({
                    grid : me,
                    data : columns
                });
            }
            // Store config object
            else if (columns) {
                me._columnStore = new ColumnStore(Object.assign({ grid : me }, columns));
            }

            // changes might be triggered when applying state, before grid is rendered
            // TODO: have this run a lighter weight, non-destructive response.
            // onColumnsChanged is a start, but lots of machinery is hooked to render.
            me._columnStore.on('change', me.onColumnsChanged, me);
            me._columnStore.on(columnResizeEvent(me.onColumnsResized, me));

            // Add touch class for touch devices
            if (BrowserHelper.isTouchDevice) {
                me.touch = true;

                // apply touchConfig for columns that defines it
                me._columnStore.forEach(column => {
                    const touchConfig = column.touchConfig;
                    if (touchConfig) {
                        column.applyState(touchConfig);
                    }
                });
            }
        }
    }

    /**
     * Get the {@link Grid.data.ColumnStore ColumnStore} used by this Grid.
     *
     * @property {Grid.data.ColumnStore}
     * @category Common
     * @readonly
     */
    get columns() {
        return this._columnStore;
    }

    onColumnsChanged({ action, changes, record : column }) {
        const me = this;

        // this.onPaint will handle changes caused by updateResponsive
        if (!me.isPainted) {
            return;
        }

        if (action === 'update') {
            // Just updating width is already handled in a minimal way.
            if ('width' in changes || 'minWidth' in changes || 'flex' in changes) {
                // Update any leaf columns that want to be repainted on size change
                if (me.isPainted) {
                    const region = column.region;

                    me.columns.visibleColumns.forEach((col) => {
                        if (col.region === region && col.repaintOnResize) {
                            me.refreshColumn(col);
                        }
                    });
                }
                return;
            }

            // Column toggled, need to recheck if any visible column has flex
            if ('hidden' in changes) {
                const subGrid = me.getSubGridFromColumn(column.id);
                subGrid.header.fixHeaderWidths();
                if (subGrid.footer) {
                    subGrid.footer.fixFooterWidths();
                }
                subGrid.updateHasFlex();
            }
        }

        // New columns set ("reconfiguring"), or moved to previously not available region
        if (action === 'dataset' || (changes && ('region' in changes) && !me.regions.includes(changes.region.value))) {
            // Create required subgrids (removing existing)
            me.initSubGrids();
            // Render and jump start them
            me.eachSubGrid(subGrid => {
                subGrid.render(me.verticalScroller);
                subGrid.initScroll();
            });
        }

        if (!me._suspendRenderContentsOnColumnsChanged) {
            me.renderContents();
        }
    }

    onColumnsResized({ changes, record : column }) {
        const
            me       = this,
            setWidth = changes.width && column.flex == null,
            setMinWidth = changes.minWidth && column.flex == null,
            setFlex  = changes.flex && column.width == null,
            domWidth = DomHelper.setLength(column.width),
            domMinWidth = DomHelper.setLength(column.minWidth),
            subGrid  = me.getSubGridFromColumn(column.id);

        // Let header and footer fix their own widths
        subGrid.header.fixHeaderWidths();
        if (subGrid.footer) {
            subGrid.footer.fixFooterWidths();
        }
        subGrid.updateHasFlex();

        if (!me.cellEls || column !== me.lastColumnResized) {
            me.cellEls = DomHelper.children(
                me.element,
                `.b-grid-cell[data-column-id=${column.id}]`
            );
            me.lastColumnResized = column;
        }

        for (const cell of me.cellEls) {
            if (setWidth) {
                // https://app.assembla.com/spaces/bryntum/tickets/8041
                // Although header and footer elements must be sized
                // using flex-basis to avoid the busting out problem,
                // grid cells MUST be sized using width since rows are absolutely
                // positioned and will not cause the busting out problem,
                // and rows will not stretch to shrinkwrap the cells
                // unless they are widthed with width.
                cell.style.width = domWidth;
                cell.style.flex = '';

                // IE11 calculates flexbox container width based on min-width rather than actual width. When column
                // has width defined greater than minWidth, row may have incorrect width
                if (BrowserHelper.isIE11) {
                    cell.style.minWidth = domWidth;
                }
            }
            else if (setMinWidth) {
                cell.style.minWidth = domMinWidth;
            }
            else if (setFlex) {
                cell.style.flex = column.flex;
                cell.style.width = '';
            }
            else {
                cell.style.flex = cell.style.width = cell.style.minWidth = '';
            }
        }

        // If we're being driven by the ColumnResizer, it will
        // call afterColumnsResized.
        if (!me.dragResizing) {
            me.afterColumnsResized();
        }
    }

    afterColumnsResized() {
        const me = this;

        me.refreshVirtualScrollbars();
        me.eachSubGrid(subGrid => {
            if (!subGrid.collapsed) {
                subGrid.fixWidths();
                subGrid.fixRowWidthsInSafariEdge();
                subGrid.refreshFakeScroll();
            }
        });
        me.onHeightChange();
        me.lastColumnResized = me.cellEls = null;
    }

    // Hook that can be overridden to prepare custom editors, can be used by framework wrappers
    processCellEditor(editorConfig) {}

    //endregion

    //region Rows

    /**
     * Get the Row that is currently displayed at top.
     * @member {Grid.row.Row} topRow
     * @readonly
     * @category Rows
     * @private
     */

    /**
     * Get the Row currently displayed furthest down.
     * @member {Grid.row.Row} bottomRow
     * @readonly
     * @category Rows
     * @private
     */

    /**
     * Get Row for specified record id.
     * @function getRowById
     * @param {Core.data.Model|String|Number} recordOrId Record id (or a record)
     * @returns {Grid.row.Row} Found Row or null if record not rendered
     * @category Rows
     * @private
     */

    /**
     * Returns top and bottom for rendered row or estimated coordinates for unrendered.
     * @function getRecordCoords
     * @param {Core.data.Model|string|Number} recordOrId Record or record id
     * @returns {Object} Record bounds with format { top, height, bottom }
     * @category Calculations
     * @private
     */

    /**
     * Get the Row at specified index. "Wraps" index if larger than available rows.
     * @function getRow
     * @param {Number} index
     * @returns {Grid.row.Row}
     * @category Rows
     * @private
     */

    /**
     * Get a Row for either a record, a record id or an HTMLElement
     * @function getRowFor
     * @param {HTMLElement|Core.data.Model|String|Number} recordOrId Record or record id or HTMLElement
     * @returns {Grid.row.Row} Found Row or null if record not rendered
     * @category Rows
     * @private
     */

    /**
     * Get a Row from an HTMLElement
     * @function getRowFromElement
     * @param {HTMLElement} element
     * @returns {Grid.row.Row} Found Row or null if record not rendered
     * @category Rows
     * @private
     */

    get rowManager() {
        const me = this;

        // Use row height from CSS if not specified in config. Did not want to turn this into a getter/setter for
        // rowHeight since RowManager will plug its implementation into Grid when created below, and after initial
        // configuration that is what should be used
        if (!me._isRowMeasured) {
            me.measureRowHeight();
        }

        // RowManager is a plugin, it is configured with its grid as its "client".
        // It uses client.store as its record source.

        return me._rowManager || (me._rowManager = new RowManager({
            grid          : me,
            rowHeight     : me.rowHeight,
            rowScrollMode : me.rowScrollMode || 'move',
            autoHeight    : me.autoHeight,
            listeners     : {
                changetotalheight   : me.onRowManagerChangeTotalHeight,
                requestscrollchange : me.onRowManagerRequestScrollChange,
                thisObj             : me
            }
        }));
    }

    toggleEmptyText() {
        this.bodyContainer && this.bodyContainer.classList[this.rowManager.rowCount || this.store.isLoading ? 'remove' : 'add']('b-grid-empty');
    }

    //endregion

    //region Store

    /**
     * Hooks up data store listeners
     * @private
     * @category Store
     */
    bindStore(store) {
        if (store) {
            const me = this;

            me.storeDetacher = store.on({
                refresh       : me.onStoreDataChange,
                update        : me.onStoreUpdateRecord,
                add           : me.onStoreAdd,
                remove        : me.onStoreRemove,
                move          : me.onStoreMove,
                replace       : me.onStoreReplace,
                removeall     : me.onStoreRemoveAll,
                beforerequest : me.onStoreBeforeRequest,
                afterrequest  : me.onStoreAfterRequest,
                clearchanges  : me.onStoreDataChange,
                exception     : me.onStoreException,
                commit        : me.onStoreCommit
            }, me);
        }
    }

    get store() {
        return this._store;
    }

    /**
     * Get/set the store used by this Grid. The setter accepts Store or a configuration object for a store.
     * If the configuration contains a `readUrl`, an AjaxStore will be created.
     * @property {Core.data.Store|Object}
     * @category Common
     */
    set store(store) {
        const
            me             = this,
            { rowManager } = me,  // We must force instantiation of the RowManager when the store is set
            features       = me.initialConfig.features;

        if (store !== me._store) {
            if (me.storeDetacher) {
                me.storeDetacher();
                me.storeDetacher = null;
            }

            if (store) {

                if (store instanceof Store) {
                    //<debug>
                    if (store.modelClass !== GridRowModel && !GridRowModel.isPrototypeOf(store.modelClass) && !me.disableGridRowModelWarning) {
                        console.warn('It is recommended to use a subclass of GridRowModel for data in Grids store, for better feature support');
                    }
                    //</debug>
                }
                else {
                    const storeCfg = {};
                    if (me.data) {
                        storeCfg.data = me.data;
                    }
                    if (features && features.tree) {
                        storeCfg.tree = true;
                    }
                    // extend GridRowModel to not pollute it with custom fields (if we have multiple grids on page)
                    if (!store.modelClass) {
                        storeCfg.modelClass = class extends GridRowModel {};
                    }

                    store = new (store.readUrl ? AjaxStore : Store)(Object.assign(storeCfg, store));
                }

                me._store = store;

                me.bindStore(store);

                // Changing store when painted -> refresh rows to reflect new data
                if (me.isPainted) {
                    rowManager.reinitialize();
                }
            }
            else {
                if (me.destroyStore) {
                    me._store.destroy();
                }
                me._store = null;
            }
        }
    }

    /**
     * Rerenders a cell if a record is updated in the store
     * @private
     * @category Store
     */
    onStoreUpdateRecord({ source : store, record, changes }) {
        const me = this;

        if (me.forceFullRefresh) {
            // flagged to need full refresh (probably from using GroupSummary)
            me.rowManager.refresh();

            me.forceFullRefresh = false;
        }
        else {
            let row;
            // Search for old row if id was changed
            if (record.isFieldModified('id')) {
                row = me.getRowFor(record.meta.modified.id);
            }

            row = row || me.getRowFor(record);
            // not rendered, bail out
            if (!row) return;

            // We must refresh the full row if it's a special row which has signalled
            // an update because it has no cells.
            if (me.fullRowRefresh || record.meta.specialRow) {
                const index = store.indexOf(record);
                if (index !== -1) {
                    row.render(index, record);
                }
            }
            else {
                me.columns.visibleColumns.forEach(column => {
                    const
                        field = column.field,
                        isSafe = column.constructor.simpleRenderer && !(column.data.hasOwnProperty('renderer'));

                    // If there's a  non-safe renderer, that is a renderer which draws values from elsewhere
                    // than just its configured field, that column must be refreshed on every record update.
                    // Obviously, if the column's configured field is changed that also means it's refreshed.
                    if (!isSafe || changes[field]) {
                        const cell = row.getCell(field);
                        if (cell) {
                            row.renderCell(cell, record);
                        }
                    }
                });
            }
        }
    }

    refreshFromRowOnStoreAdd(row, context) {
        const
            me             = this,
            { rowManager } = me;

        rowManager.renderFromRow(row);
        rowManager.trigger('changeTotalHeight', { totalHeight : rowManager.totalHeight });

        // First record? Also update fake scrollers
        // TODO: Consider making empty grid scrollable to not have to do this
        if (me.store.count === 1) {
            me.callEachSubGrid('refreshFakeScroll');
        }
    }

    /**
     * Refreshes rows when data is added to the store
     * @private
     * @category Store
     */
    onStoreAdd({ source : store, records, index, oldIndex, isChild, oldParent, isMove }) {
        // Do not react if the content has not been rendered
        if (!this.isPainted) {
            return;
        }

        // If it's the addition of a child to a collapsed zone, the UI does not change.
        if (isChild && !records[0].ancestorsExpanded(store)) {
            return;
        }

        this.rowManager.calculateRowCount(false, true, true);

        const
            me             = this,
            { rowManager } = me,
            {
                topIndex,
                rows,
                rowCount
            }              = rowManager,
            bottomIndex    = rowManager.topIndex + rowManager.rowCount - 1,
            dataStart      = index,
            dataEnd        = index + records.length - 1,
            atEnd          = bottomIndex >= store.count - records.length - 1;

        // When moving a node within a tree we might need the redraw to include its old parent and its children. Not worth
        // the complexity of trying to do a partial render for this, rerender all rows to be safe.
        // Moving records within a flat store is handled elsewhere, in onStoreMove
        // TODO: Moving within a tree should also trigger 'move' (https://app.assembla.com/spaces/bryntum/tickets/7270)
        if (oldParent || oldIndex > -1 || (isChild && isMove)) {
            rowManager.refresh();
        }
        // Added block starts in our visible block. Render from there downwards.
        else if (dataStart >= topIndex && dataStart < topIndex + rowCount) {
            me.refreshFromRowOnStoreAdd(rows[dataStart - topIndex], ...arguments);
        }
        // Added block ends in our visible block, render block
        else if (dataEnd >= topIndex && dataEnd < topIndex + rowCount) {
            rowManager.refresh();
        }
        // If added block is outside of the visible area, no visible change
        // but potentially a change in total dataset height.
        else {
            // If we are against the end of the dataset, and have appended records
            // ensure they are rendered below
            if (atEnd && index > bottomIndex) {
                rowManager.fillBelow(me._scrollTop);
            }
            rowManager.trigger('changeTotalHeight', { totalHeight : rowManager.totalHeight });
        }
    }

    /**
     * Responds to exceptions signalled by the store
     * @private
     * @category Store
     */
    onStoreException(event) {
        const me = this;

        let message;

        switch (event.type) {
            case 'server':
                message = (event.response.message || 'Unspecified failure');
                break;

            case 'exception':
                if (event.exceptionType === 'network') {
                    message = 'Network error';
                }
                else {
                    // Server sent something that couldn't be parsed
                    message =  event.error && event.error.message || 'Failed to parse server response';
                }
                break;

            default:
                message = ((event.response.status + ' - ' + event.response.statusText) || 'Unknown error');
        }

        // eslint-disable-next-line
        const messageHTML = `<div class="b-grid-load-failure">
                <div class="b-grid-load-fail">${me.L('loadFailedMessage')}</div>
                <div class="b-grid-load-fail">${event.response.url ? (event.response.url + ' responded with') : ''}</div>
                <div class="b-grid-load-fail">${message}</div>
            </div>`;

        if (me.activeMask) {
            me.activeMask.icon = me.loadMaskErrorIcon;
            me.activeMask.text = messageHTML;

            me.loadmaskHideTimer = me.setTimeout(() => {
                me.unmaskBody();
            }, me.loadMaskHideTimeout);
        }
    }

    /**
     * Refreshes rows when data is changed in the store
     * @private
     * @category Store
     */
    onStoreDataChange({ action, changes, source : store }) {
        // If the next mixin up the inheritance chain has an implementation, call it
        super.onStoreDataChange && super.onStoreDataChange(...arguments);

        const
            me = this,
            isGroupFieldChange = store.isGrouped && changes && store.groupers.some(grouper => grouper.field in changes);

        // If it's new data, the old calculation is invalidated.
        if (action === 'dataset') {
            me.rowManager.averageRowHeight = null;
        }
        // No need to rerender if it's a change of the value of the group field which
        // will be responded to by StoreGroup
        if (me.isPainted && !isGroupFieldChange) {
            // Return to top if setting new data or is filtering
            me.renderRows(null, preserveScroll[action]);
        }

        me.toggleEmptyText();
    }

    /**
     * Shows a load mask while the connected store is loading
     * @private
     * @category Store
     */
    onStoreBeforeRequest() {
        if (this.loadMask) {
            this.maskBody(this.loadMask);
        }
    }

    /**
     * Hides load mask after a load request ends either in success or failure
     * @private
     * @category Store
     */
    onStoreAfterRequest(event) {
        if (this.activeMask && !event.exception) {
            this.unmaskBody();
            this.toggleEmptyText();
        }
    }

    /**
     * Animates removal of record.
     * @private
     * @category Store
     */
    onStoreRemove({ records, isCollapse, isChild, isMove }) {
        // Do not react if the content has not been rendered,
        // or if it is a move, which will be handled by onStoreAdd
        if (!this.isPainted || isMove) {
            return;
        }

        // GridSelection mixin does its job on records removing
        super.onStoreRemove && super.onStoreRemove(...arguments);

        let topRowIndex = (2 ** 53) - 1;

        const
            me             = this,
            { rowManager } = this,
            // Gather all visible rows which need to be removed.
            rowsToRemove   = records.reduce((result, record) => {
                const row = rowManager.getRowById(record.id);
                if (row) {
                    result.push(row);
                    // Rows are repositioned in the array, it matches visual order. Need to find actual index in it
                    topRowIndex = Math.min(topRowIndex, rowManager.rows.indexOf(row));
                }
                return result;
            }, []);

        if (me.animateRemovingRows && rowsToRemove.length && !isCollapse && !isChild) {
            const topRow = rowsToRemove[0];

            me.isAnimating = true;
            // As soon as first row has disappeared, rerender the view
            EventHelper.on({
                element       : topRow._elementsArray[0],
                transitionend : e => {
                    me.isAnimating = false;
                    // hovering triggers background-color transitions, ignore those
                    if (e.propertyName !== 'background-color' && e.propertyName !== 'width') {
                        rowsToRemove.forEach(row => row.removeCls('b-removing'));
                        rowManager.refresh();
                        // undocumented internal event for scheduler
                        me.trigger('rowRemove');
                    }
                },
                once : true
            });
            rowsToRemove.forEach(row => row.addCls('b-removing'));
        }
        else {
            // Potentially remove rows and change dataset height
            rowManager.calculateRowCount(false, true, true);

            // If there were rows below which have moved up into place
            // then repurpose them with their new records
            if (rowManager.rows[topRowIndex]) {
                rowManager.renderFromRow(rowManager.rows[topRowIndex]);
            }
            // If nothing to render below, just update dataset height
            else {
                rowManager.trigger('changeTotalHeight', { totalHeight : rowManager.totalHeight });
            }
            me.trigger('rowRemove', { isCollapse });
        }
    }

    onStoreMove({ from, to }) {
        const
            { rowManager }       = this,
            {
                topIndex,
                rowCount
            }                    = rowManager,
            [dataStart, dataEnd] = [from, to].sort();

        // Changed block starts in our visible block. Render from there downwards.
        if (dataStart >= topIndex && dataStart < topIndex + rowCount) {
            rowManager.renderFromRow(rowManager.rows[dataStart - topIndex]);
        }
        // Changed block ends in our visible block, render block
        else if (dataEnd >= topIndex && dataEnd < topIndex + rowCount) {
            rowManager.refresh();
        }
        // If changed block is outside of the visible area, this is a no-op
    }

    onStoreReplace({ records, all }) {
        if (all) {
            this.rowManager.refresh();
        }
        else {
            const rows = records.reduce((rows, [, record]) => {
                const row = this.getRowFor(record);
                if (row) {
                    rows.push(row);
                }
                return rows;
            }, []);

            this.rowManager.renderRows(rows);
        }
    }

    /**
     * Rerenders grid when all records have been removed
     * @private
     * @category Store
     */
    onStoreRemoveAll() {
        // GridSelection mixin does its job on records removing
        super.onStoreRemoveAll && super.onStoreRemoveAll(...arguments);

        if (this.isPainted) {
            this.renderRows();
            this.toggleEmptyText();
        }
    }

    // Refresh dirty cells on commit
    onStoreCommit({ changes }) {
        if (this.showDirty && changes.modified.length) {
            const rows = [];
            changes.modified.forEach(record => {
                const row = this.rowManager.getRowFor(record);
                row && rows.push(row);
            });
            this.rowManager.renderRows(rows);
        }
    }

    /**
     * Convenience functions for getting/setting data in related store
     * @property {Object[]}
     * @category Common
     */
    get data() {
        if (this._store) {
            return this._store.records;
        }
        else {
            return this._data;
        }
    }

    set data(data) {
        if (this._store) {
            this._store.data = data;
        }
        else {
            this._data = data;
        }
    }

    get emptyText() {
        return this._emptyText;
    }

    set emptyText(text) {
        this._emptyText = text;
        this.eachSubGrid(subGrid => subGrid.emptyText = text);
    }

    //endregion

    //region Context menu items

    /**
     * Populates the header context menu. Chained in features to add menu items.
     * @param column Column for which the menu will be shown
     * @param items Array of menu items, add to it and return it
     * @category Menu items
     * @internal
     */
    getHeaderMenuItems(column, items) {
        const me       = this,
            { subGrids, regions } = me;

        let first = true;

        Object.entries(subGrids).forEach(([region, subGrid]) => {
            // If SubGrid is configured with a sealed column set, do not allow moving into it
            if (subGrid.sealedColumns) {
                return;
            }

            if (column.draggable &&
                region !== column.region &&
                (!column.parent && subGrids[column.region].columns.count > 1 ||
                    column.parent && column.parent.children.length > 1)
            ) {
                const moveRight = subGrid.element.compareDocumentPosition(subGrids[column.region].element) === document.DOCUMENT_POSITION_PRECEDING,
                    // With 2 regions, use Move left, Move right. With multiple, include region name
                    text = regions.length > 2
                        ? me.L('Move column to ') + region
                        : me.L(moveRight ? 'moveColumnRight' : 'moveColumnLeft');

                items.push({
                    targetSubGrid : region,
                    text,
                    icon          : 'b-fw-icon ' + (moveRight ? 'b-icon-column-move-right' : 'b-icon-column-move-left'),
                    name          : 'moveColumn',
                    cls           : first ? 'b-separator' : '',
                    onItem        : ({ item }) => {
                        const { column } = item;

                        column.traverse(col => col.region = region);

                        // Changing region will move the column to the correct SubGrid, but we want it to go last
                        me.columns.insert(me.columns.indexOf(subGrids[item.targetSubGrid].columns.last) + 1, column);

                        me.scrollColumnIntoView(column);
                    }
                });

                first = false;
            }
        });
    }

    /**
     * Populates the cell context menu. Chained in features to add menu items.
     * @param column {Grid.column.Column} Column for which the menu will be shown
     * @param record {Core.data.Model} Record (row) for which the menu will be shown
     * @param items {Object[]} Array of menu items, add to it and return it
     * @category Menu items
     * @internal
     */
    getCellMenuItems(column, record, items) {
        const me = this;

        if (me.showRemoveRowInContextMenu && !me.readOnly && record && !record.meta.specialRow) {
            if (me.selectedRecords.length > 1) {
                items.push(
                    {
                        text   : me.L('removeRows'),
                        icon   : 'b-fw-icon b-icon-trash',
                        name   : 'removeRows',
                        onItem : () => me.store.remove(me.selectedRecords)
                    }
                );
            }
            else {
                items.push(
                    {
                        text   : me.L('removeRow'),
                        icon   : 'b-fw-icon b-icon-trash',
                        name   : 'removeRow',
                        onItem : () => me.store.remove(record)
                    }
                );
            }
        }
    }

    getColumnDragToolbarItems(column, items) {
        return items;
    }

    //endregion

    //region Getters

    normalizeCellContext(cellContext) {
        const { columns, store } = this;

        // TODO: should clone instead of modify?
        // TODO: The answer is to use the Grid/util/Location class to robustly encapsulate a record/column intersection
        // And have them immutable, so that to change is to clone, as explained by MaximGB,
        // we want to use columnId for precision, but allow user to specify column name for ease of use...
        // modify cellContext to include columnId in those cases
        if (cellContext instanceof store.modelClass) {
            return {
                record   : cellContext,
                id       : cellContext.id,
                columnId : columns.bottomColumns[0].id
            };
        }
        if (!('columnId' in cellContext)) {
            if ('field' in cellContext) {
                const column = columns.get(cellContext.field);
                cellContext.columnId = column && column.id;
            }
            else if ('column' in cellContext) {
                const column = (typeof cellContext.column === 'number') ? columns.bottomColumns[cellContext.column] : cellContext.column;
                cellContext.columnId = column && column.id;
            }

            // Fall back to first leaf column
            if (!('columnId' in cellContext)) {
                cellContext.columnId = columns.bottomColumns[0].id;
            }
        }

        if ('id' in cellContext) {
            // If the context is for an element, but it's stale (for a removed record)
            // then fix it up to refer to the record id at the same index.
            if (cellContext.element && (!store.getById(cellContext.id))) {
                // This uses the data-index property to get the row at that index.
                const newRec = this.getRecordFromElement(cellContext.element);

                // We have a record at the same index.
                if (newRec) {
                    cellContext.id = newRec.id;
                }
            }
        }
        else {
            if ('row' in cellContext) {
                cellContext.id = store.getAt(cellContext.row).id;
            }
            else if ('record' in cellContext) {
                cellContext.id = cellContext.record.id;
            }
        }

        return cellContext;
    }

    // TODO: move to RowManager? Or create a CellManager?
    /**
     * Returns a cell if rendered.
     * @param {Object} cellContext { id: rowId, columnId: columnId [,column: column number, field: column field] }
     * @param {Number} [cellContext.row] The row index of the row to access. Exclusive with `id` and 'record'.
     * @param {String|Number} [cellContext.id] The record id of the row to access. Exclusive with `row` and 'record'.
     * @param {Core.data.Model} [cellContext.record] The record of the row to access. Exclusive with `id` and 'row'.
     * @param {Number} [cellContext.column] The column instance or the index of the cell to access.  Exclusive with `columnId`.
     * @param {String|Number} [cellContext.columnId] The column id of the column to access. Exclusive with `column`.
     * @param {String} [cellContext.field] The field of the column to access. Exclusive with `column`.
     * @returns {HTMLElement}
     * @category Getters
     */
    getCell(cellContext) {
        let row,
            result = null;

        cellContext = this.normalizeCellContext(cellContext);

        if ('id' in cellContext) {
            row = this.getRowById(cellContext.id);
        }

        if (row && ('columnId' in cellContext)) {
            result = row.getCell(cellContext.columnId);
        }

        return result;
    }

    //TODO: Should move to ColumnManager? Or Header?
    /**
     * Returns the header element for the column
     * @param {String|Number|Grid.column.Column} columnId or Column instance
     * @returns {HTMLElement} Header element
     * @category Getters
     */
    getHeaderElement(columnId) {
        if (typeof columnId !== 'string') {
            columnId = columnId.id;
        }

        return this.fromCache(`.b-grid-header[data-column-id="${columnId}"]`);
    }

    getHeaderElementByField(field) {
        const column = this.columns.get(field);

        return column ? this.getHeaderElement(column) : null;
    }

    /**
     * Body height
     * @property {Number}
     * @readonly
     * @category Layout
     */
    get bodyHeight() {
        return this._bodyHeight;
    }

    /**
     * Header height
     * @property {Number}
     * @readonly
     * @category Layout
     */
    get headerHeight() {
        const me = this;
        // measure header if rendered and not stored
        if (me.isPainted && !me._headerHeight) {
            me._headerHeight = me.headerContainer.offsetHeight;
        }

        return me._headerHeight;
    }

    /**
     * Searches up from the specified element for a grid row and returns the record associated with that row.
     * @param {HTMLElement} element Element somewhere within a row or the row container element
     * @returns {Core.data.Model} Record for the row
     * @category Getters
     */
    getRecordFromElement(element) {
        const el = element.closest('.b-grid-row');

        if (!el) return null;

        return this.store.getAt(el.dataset.index);
    }

    /**
     * Searches up from specified element for a grid cell or an header and returns the column which the cell belongs to
     * @param {HTMLElement} element Element somewhere in a cell
     * @returns {Grid.column.Column} Column to which the cell belongs
     * @category Getters
     */
    getColumnFromElement(element) {
        const cell = DomHelper.up(element, '.b-grid-cell, .b-grid-header');
        if (!cell) return null;

        if (cell.matches('.b-grid-header')) {
            return this.columns.getById(cell.dataset.columnId);
        }

        const cellData = DomDataStore.get(cell);
        return this.columns.getById(cellData.columnId);
    }

    // Getter and setter for autoHeight only added for type checking, since it seems common to get it wrong in react/angular
    get autoHeight() {
        return this._autoHeight;
    }

    set autoHeight(autoHeight) {
        ObjectHelper.assertBoolean(autoHeight, 'autoHeight');

        this._autoHeight = autoHeight;
    }

    /**
     * Toggle column line visibility. End result might be overruled by/differ between themes.
     * @property {Boolean}
     */
    get columnLines() {
        return this._columnLines;
    }

    set columnLines(columnLines) {
        ObjectHelper.assertBoolean(columnLines, 'columnLines');

        DomHelper.toggleClasses(this.element, 'b-no-column-lines', !columnLines);

        this._columnLines = columnLines;
    }

    //endregion

    //region ReadOnly

    /**
     * Get/set read only mode, which prevents cell editing etc.
     * Exactly what is prevented is up to each feature.
     * @property {Boolean}
     * @fires readonly
     * @category Common
     */
    set readOnly(readOnly) {
        const me = this;
        me._readOnly = readOnly;
        if (me.isPainted) {
            /**
             * Fired when grids read only state is toggled
             * @event readOnly
             * @param {Boolean} readOnly Read only or not
             */
            me.trigger('readOnly', { readOnly });

            // IE11 doesnt support this
            //me.element.classList.toggle('b-readonly', readOnly);
            if (readOnly) {
                me.element.classList.add('b-readonly');
            }
            else {
                me.element.classList.remove('b-readonly');
            }
        }
    }

    get readOnly() {
        return this._readOnly;
    }

    //endregion

    //region Fix width & height

    /**
     * Sets widths and heights for headers, rows and other parts of the grid as needed
     * @private
     * @category Width & height
     */
    fixSizes() {
        // subGrid width
        this.callEachSubGrid('fixWidths');
    }

    onRowManagerChangeTotalHeight({ totalHeight }) {
        return this.refreshTotalHeight(totalHeight);
    }

    /**
     * Makes height of vertical scroller match estimated total height of grid. Called when scrolling vertically and
     * when showing/hiding rows.
     * @param {Number} height
     * @private
     * @category Width & height
     */
    refreshTotalHeight(height = this.rowManager.totalHeight) {
        const me = this;

        // Veto change of estimated total height while rendering rows or if triggered while in a hidden state
        if (me.renderingRows || !me.isVisible) {
            return false;
        }
        if (me.rowManager.bottomRow) {
            height = Math.max(height, me.rowManager.bottomRow.bottom);
        }

        const
            scroller     = me.scrollable,
            delta        = Math.abs(me.virtualScrollHeight - height),
            clientHeight = me._bodyRectangle.height,
            newMaxY      = height - clientHeight;

        if (delta) {
            const
                // We must update immediately if we are nearing the end of the scroll range.
                isCritical = (newMaxY - me._scrollTop < clientHeight * 2) ||
                    // Or if we have scrolled pass visual height
                    (me._verticalScrollHeight && (me._verticalScrollHeight - clientHeight < me._scrollTop));

            // Update the true scroll range using the scroller. This will not cause a repaint.
            scroller.scrollHeight = me.virtualScrollHeight = height;

            // If we are scrolling, put this off because it causes
            // a full document layout and paint.
            if (me.scrolling && !isCritical) {
                if (!me.virtualScrollHeightDirty) {
                    me.virtualScrollHeightDirty = scroller.on({
                        scrollend : me.fixElementHeights,
                        thisObj   : me,
                        once      : true
                    });
                }
            }
            else {
                me.virtualScrollHeightDirty && me.virtualScrollHeightDirty();
                me.fixElementHeights(height);
            }
        }
    }

    fixElementHeights() {
        const
            me         = this,
            height     = me.virtualScrollHeight,
            heightInPx = `${height}px`;

        me._verticalScrollHeight = height;
        me.verticalScroller.style.height = heightInPx;
        me.virtualScrollHeightDirty = false;

        if (me.autoHeight) {
            me.bodyContainer.style.height = heightInPx;
            me._bodyHeight = height;
            me._bodyRectangle = Rectangle.client(me.bodyContainer);
        }

        me.refreshVirtualScrollbars();
    }

    //endregion

    //region Scroll & virtual rendering

    set scrolling(scrolling) {
        this._scrolling = scrolling;
        this.bodyContainer.classList[scrolling ? 'add' : 'remove']('b-scrolling');
    }

    get scrolling() {
        return this._scrolling;
    }

    /**
     * Responds to request from RowManager to adjust scroll position. Happens when jumping to a scroll position with
     * variable row height.
     * @param {Number} bottomMostRowY
     * @private
     * @category Scrolling
     */
    onRowManagerRequestScrollChange({ bottom }) {
        this.scrollable.y = bottom - this.bodyHeight;
    }

    //<debug>
    runPerformanceTest(count = 5, direction = 'vertical') {
        const
            me      = this,
            body    = me.bodyContainer,
            fpsList = me.fpsList || (me.fpsList = []);

        me.frameCount = 0;

        if (!me.testPerformance) {
            me.testPerformance = direction;
        }

        me.setTimeout(() => {
            let start       = performance.now(),
                scrollSpeed = 5,
                direction   = 1;

            const scrollInterval = me.setInterval(() => {
                scrollSpeed = scrollSpeed + 5;
                //if (scrollSpeed > 30) scrollSpeed = 0;

                body.scrollTop += (10 + Math.floor(scrollSpeed)) * direction;

                if (direction === 1 && body.scrollTop > 30000) {
                    direction = -1;
                    scrollSpeed = 5;
                }

                if (direction === -1 && body.scrollTop <= 0) {
                    let done         = performance.now(),
                        elapsed      = done - start,
                        timePerFrame = elapsed / me.frameCount,
                        fps          = 1000 / timePerFrame;

                    fps = Math.round(fps * 10) / 10;

                    clearInterval(scrollInterval);

                    //console.log(me.positionMode, me.rowScrollMode, fps + 'fps');
                    fpsList.push(fps);

                    if (fpsList.length < count) {
                        me.runPerformanceTest(count);
                    }
                    else {
                        console.log(fpsList, fpsList.reduce((result, fps) => result += fps / fpsList.length, 0));
                        me.fpsList.length = 0;
                    }
                }
            }, 0);
        }, fpsList.length ? 0 : 2500);
    }

    //</debug>

    /**
     * Scroll syncing for normal headers & grid + triggers virtual rendering for vertical scroll
     * @private
     * @fires scroll
     * @category Scrolling
     */
    initScroll() {
        const me = this;
        // This method may be called early, before render calls it, so ensure that it's
        // only executed once.
        if (!me.scrollInitialized) {
            let scrollTop,
                onScroll = me.createOnFrame(() => {
                    scrollTop = me.scrollable.y;

                    // Was getting scroll events in FF where scrollTop was unchanged, ignore those
                    if (scrollTop !== me._scrollTop) {
                        me._scrollTop = scrollTop;

                        if (!me.scrolling) {
                            me.scrolling = true;
                            me.eachSubGrid(s => s.suspendResizeMonitor = true);
                        }

                        //<debug>
                        if (me.testPerformance === 'vertical') {
                            me.frameCount++;
                        }
                        //</debug>

                        me.rowManager.updateRenderedRows(scrollTop);

                        /**
                        * Grid has scrolled vertically
                        * @event scroll
                        * @param {Grid.view.Grid} grid
                        * @param {Number} scrollTop
                        */
                        me.trigger('scroll', { scrollTop });
                    }
                });

            me.scrollInitialized = true;

            me.scrollable.on({
                scroll : onScroll,
                scrollend() {
                    me.scrolling = false;
                    me.eachSubGrid(s => s.suspendResizeMonitor = false);
                }
            });

            me.callEachSubGrid('initScroll');

            //<debug>
            // TODO: break out to mixin or such
            if (me.testPerformance === 'vertical') {
                me.runPerformanceTest();
            }
            //</debug>
        }
    }

    // TODO: rename to scrollRecordIntoView? Or have an alias?
    /**
     * Scrolls a row into view. If row isn't rendered it tries to calculate position
     * @param {Core.data.Model|String|Number} recordOrId Record or record id
     * @param {Object} [options] How to scroll.
     * @param {String} [options.column] Field name or ID of the column, or the Column instance to scroll to.
     * @param {String} [options.block] How far to scroll the element: `start/end/center/nearest`.
     * @param {Number} [options.edgeOffset] edgeOffset A margin around the element or rectangle to bring into view.
     * @param {Boolean|Number} [options.animate] Set to `true` to animate the scroll, or the number of milliseconds to animate over.
     * @param {Boolean} [options.highlight] Set to `true` to highlight the element when it is in view.
     * @category Scrolling
     * @returns {Promise} A promise which resolves when the specified row has been scrolled into view.
     */
    scrollRowIntoView(recordOrId, options = defaultScrollOptions) {
        const
            me            = this,
            blockPosition = options.block || 'nearest',
            rowManager    = me.rowManager;

        recordOrId = me.store.getById(recordOrId);

        if (recordOrId) {
            // check that record is "displayable", not filtered out or hidden by collapse
            if (me.store.indexOf(recordOrId) === -1) {
                return resolvedPromise;
            }

            let scroller   = me.scrollable,
                recordRect = me.getRecordCoords(recordOrId);

            const scrollerRect = Rectangle.from(scroller.element);

            // If it was calculated from the index, update the rendered rowScrollMode
            // and scroll to the actual element. Note that this should only be necessary
            // for variableRowHeight.
            // But to "make the tests green", this is a workaround for a buffered rendering
            // bug when teleporting scroll. It does not render the rows at their correct
            // positions. Please do not try to "fix" this. I will do it. NGW
            if (recordRect.virtual) {
                const virtualBlock = recordRect.block;

                // Scroll the calculated position to the center of the scrollingViewport
                // and then update the rendered block while asking the RowManager to
                // display the required recordOrId.
                scroller.scrollIntoView(recordRect, {
                    block : 'center'
                });
                rowManager.scrollTargetRecordId = recordOrId;
                rowManager.updateRenderedRows(scroller.y, true);
                recordRect = me.getRecordCoords(recordOrId);
                rowManager.lastScrollTop = scroller.y;

                if (recordRect.virtual) {
                    //<debug>
                    throw new Error(`Unable to scroll ${recordOrId.id} into view`);
                    //</debug>
                    // bail out to not get caught in infinite loop, since code above is cut out of bundle
                    // eslint-disable-next-line no-useless-return,no-unreachable
                    return resolvedPromise;
                }

                const innerOptions = blockPosition !== 'nearest' ? options : {
                    block : virtualBlock
                };

                // Scroll the target just less than append/prepend buffer height out of view so that the animation looks good
                if (options.animate) {
                    // Do not fire scroll events during this scroll sequence - it's a purely cosmetic operation.
                    // We are scrolling the desired row out of view merely to *animate scroll* it to the requested position.
                    scroller.suspendEvents();

                    // Scroll to its final position
                    if (blockPosition === 'end' || blockPosition === 'nearest' && virtualBlock === 'end') {
                        scroller.y -= (scrollerRect.bottom - recordRect.bottom);
                    }
                    else if (blockPosition === 'start' || blockPosition === 'nearest' && virtualBlock === 'start') {
                        scroller.y += (recordRect.y - scrollerRect.y);
                    }

                    // Ensure rendered block is correct at that position
                    rowManager.updateRenderedRows(scroller.y, false, true);

                    // Scroll away from final position to enable a cosmetic scroll to final position
                    if (virtualBlock === 'end') {
                        scroller.y -= (rowManager.appendRowBuffer * rowManager.rowHeight - 1);
                    }
                    else {
                        scroller.y += (rowManager.prependRowBuffer * rowManager.rowHeight - 1);
                    }

                    // The row will still be rendered, so scroll it using the scroller directly
                    const result = scroller.scrollIntoView(me.getRecordCoords(recordOrId), Object.assign({}, options, innerOptions));

                    // Now we're at the required position, resume events
                    return result.then(() => scroller.resumeEvents());
                }
                else {
                    return me.scrollRowIntoView(recordOrId, Object.assign({}, options, innerOptions));
                }
            }
            else {
                let { column } = options;

                if (column) {
                    if (typeof column === 'string') {
                        column = me.columns.getById(column) || me.columns.get(column);
                    }

                    // If we are targetting a column, we must use the scroller of that column's SubGrid
                    if (column) {
                        scroller = me.getSubGridFromColumn(column).scrollable;

                        const cellRect = Rectangle.from(rowManager.getRowFor(recordOrId).getCell(column.id));

                        recordRect.x = cellRect.x;
                        recordRect.width = cellRect.width;
                    }
                }
                // No column, then tell the scroller not to scroll in the X axis
                else {
                    options.x = false;
                }
                return scroller.scrollIntoView(recordRect, options);
            }
        }
    }

    /**
     * Scrolls a column into view (if it is not already)
     * @param {Grid.column.Column|String|Number} column Column name (data) or column index or actual column object.
     * @param {Object} [options] How to scroll.
     * @param {String} [options.block] How far to scroll the element: `start/end/center/nearest`.
     * @param {Number} [options.edgeOffset] edgeOffset A margin around the element or rectangle to bring into view.
     * @param {Object|Boolean|Number} [options.animate] Set to `true` to animate the scroll by 300ms,
     * or the number of milliseconds to animate over, or an animation config object.
     * @param {Number} [options.animate.duration] The number of milliseconds to animate over.
     * @param {String} [options.animate.easing] The name of an easing function.
     * @param {Boolean} [options.highlight] Set to `true` to highlight the element when it is in view.
     * @param {Boolean} [options.focus] Set to `true` to focus the element when it is in view.
     * @returns {Promise} If the column exists, a promise which is resolved when the column header element has been scrolled into view.
     * @category Scrolling
     */
    scrollColumnIntoView(column, options) {
        column = (column instanceof Column) ? column : this.columns.get(column) || this.columns.getById(column) || this.columns.getAt(column);

        return this.getSubGridFromColumn(column).scrollColumnIntoView(column, options);
    }

    // TODO The API { id: recordId, column: 'columnName' } is not clear: id has to be renamed to `record` or `recordId` to be self-explanatory;
    /**
     * Scrolls a cell into view (if it is not already)
     * @param {Object} cellContext Cell selector { id: recordId, column: 'columnName' }
     * @category Scrolling
     */
    scrollCellIntoView(cellContext, options) {
        return this.scrollRowIntoView(cellContext.id, Object.assign({
            column : cellContext.columnId
        }, typeof options === 'boolean' ? { animate : options } : options));
    }

    /**
     * Scroll all the way down
     * @returns {Promise} A promise which resolves when the bottom is reached.
     * @category Scrolling
     */
    scrollToBottom(options) {
        // triggers scroll to last record. not using current scroller height because we do not know if it is correct
        return this.scrollRowIntoView(this.store.last, options);
    }

    /**
     * Scroll all the way up
     * @returns {Promise} A promise which resolves when the top is reached.
     * @category Scrolling
     */
    scrollToTop(options) {
        return this.scrollable.scrollBy(0, -this.scrollable.y, options);
    }

    /**
     * Store scroll state (scrollTop for entire grid and scrollLeft per sub grid)
     * @returns {{scrollTop: (*|string|number), scrollLeft: {}}}
     * @category Scrolling
     */
    storeScroll() {
        const
            me    = this,
            state = me.storedScrollState = {
                scrollTop  : me.scrollable.y,
                scrollLeft : {}
            };

        // TODO: Implement special multi-element Scroller subclass for Grids which
        // encapsulates the x axis only Scrollers of all its SubGrids.
        me.eachSubGrid(subGrid => {
            state.scrollLeft[subGrid.region] = subGrid.scrollable.x;
        });

        return state;
    }

    /**
     * Restore scroll state. If state is not specified, restores the last stored state.
     * @param state Scroll state, optional
     * @category Scrolling
     */
    restoreScroll(state = this.storedScrollState) {
        const me = this;

        // TODO: Implement special multi-element Scroller subclass for Grids which
        // encapsulates the x axis only Scrollers of all its SubGrids.
        me.eachSubGrid(subGrid => {
            subGrid.scrollable.x = state.scrollLeft[subGrid.region];
        });

        me.scrollable.y = state.scrollTop;
    }

    //endregion

    //region Theme & measuring

    /**
     * Creates a fake subgrid with one row and mesaures its height. Result is used as rowHeight.
     * @private
     */
    measureRowHeight() {
        const
            me                = this,
            // Create a fake subgrid with one row, since styling for row is specified on .b-grid-subgrid .b-grid-row
            rowMeasureElement = DomHelper.createElement({
                tag       : 'div',
                // TODO: should either get correct widgetClassList or query features for measure classes
                className : 'b-grid ' + (me.features.stripe ? 'b-stripe' : ''),
                style     : 'position: absolute; visibility: hidden',
                html      : '<div class="b-grid-subgrid"><div class="b-grid-row"></div></div>',
                parent    : document.getElementById(me.appendTo) || document.body
            });

        // Use style height or default height from config.
        // Not using clientHeight since it will have some value even if no height specified in CSS
        const
            rowEl        = rowMeasureElement.firstElementChild.firstElementChild,
            styleHeight  = parseInt(DomHelper.getStyleValue(rowEl, 'height')),
            borderTop    = parseInt(DomHelper.getStyleValue(rowEl, 'border-top-width')),
            borderBottom = parseInt(DomHelper.getStyleValue(rowEl, 'border-bottom-width'));

        // Change rowHeight if specified in styling, also remember that value to replace later if theme changes and
        // user has not explicitly set some other height
        if (me.rowHeight == null || me.rowHeight === me._rowHeightFromStyle) {
            me.rowHeight = !isNaN(styleHeight) && styleHeight ? styleHeight : me.defaultRowHeight;
            me._rowHeightFromStyle = me.rowHeight;
        }

        // this measurement will be added to rowHeight during rendering, to get correct cell height
        me._rowBorderHeight = borderTop + borderBottom;

        me._isRowMeasured = true;

        rowMeasureElement.remove();

        // There is a ticket about measuring the actual first row instead:
        // https://app.assembla.com/spaces/bryntum/tickets/5735-measure-first-real-rendered-row-for-rowheight/details
    }

    /**
     * Handler for global theme change event (triggered by shared.js). Remeasures row height.
     * @private
     */
    onThemeChange({ theme }) {
        this.measureRowHeight();
        this.trigger('theme', { theme });
    }

    //endregion

    //region Rendering of rows

    /**
     * Triggers a render of records to all row elements. Call after changing order, grouping etc to reflect changes
     * visually. Preserves scroll.
     * @category Rendering
     */
    refreshRows(returnToTop = false) {
        this.element.classList.add('b-notransition');

        if (returnToTop) {
            this.rowManager.returnToTop();
        }
        else {
            this.rowManager.refresh();
        }

        this.element.classList.remove('b-notransition');
    }

    /**
     * Triggers a render of all the cells in a column.
     * @param {Grid.column.Column} column
     * @category Rendering
     */
    refreshColumn(column) {
        const field = column.field;

        this.rowManager.forEach(row => {
            const cell = row.getCell(field);

            row.renderCell(cell);
        });
    }
    //endregion

    //region Render the grid

    /**
     * Recalculates virtual scrollbars widths and scrollWidth
     * @private
     */
    refreshVirtualScrollbars() {
        // NOTE: This was at some point changed to only run on platforms with width-occupying scrollbars, but it needs
        // to run with overlayed scrollbars also to make them show/hide as they should.

        const
            me                    = this,
            {
                headerContainer,
                footerContainer,
                virtualScrollers,
                scrollable
            }                     = me,
            hasVerticalOverflow   = BrowserHelper.isEdge || BrowserHelper.isIE11
            // IE11 and Edge report 1px scroll when no scroll actually exist, which breaks header margin
            // caught by FilterBar test
                ? Math.abs(scrollable.scrollHeight - scrollable.clientHeight) > 1
                : scrollable.scrollHeight > scrollable.clientHeight,
            // We need to ask each subGrid if it has horizontal overflow.
            // If any do, we show the virtual scroller, otherwise we hide it.
            hasHorizontalOverflow = Object.values(me.subGrids).some(subGrid => subGrid.overflowingHorizontally),
            method = hasVerticalOverflow ? 'add' : 'remove';

        if (hasHorizontalOverflow) {
            virtualScrollers.classList.remove('b-hide-display');
        }
        else {
            virtualScrollers.classList.add('b-hide-display');
        }

        headerContainer.classList[method]('b-grid-vertical-overflow');
        footerContainer.classList[method]('b-grid-vertical-overflow');
        virtualScrollers.classList[method]('b-grid-vertical-overflow');

        // Change of scrollbar status means height change
        me.onHeightChange();

        // Create margin after the headerContainer if the grid has a visible vertical scrollbar
        if (DomHelper.scrollBarWidth) {
            if (hasVerticalOverflow) {
                headerContainer.style.marginRight = footerContainer.style.paddingRight = `${DomHelper.scrollBarWidth - 1}px`;
            }
            else {
                headerContainer.style.marginRight =  footerContainer.style.paddingRight = 0;
            }
        }
    }

    /**
     * Returns content height calculated from row manager
     * @private
     */
    get contentHeight() {
        const rowManager = this.rowManager;
        return Math.max(rowManager.totalHeight, rowManager.bottomRow ? rowManager.bottomRow.bottom : 0);
    }

    onContentChange() {
        const
            me         = this,
            rowManager = me.rowManager;

        if (me.isVisible) {
            rowManager.estimateTotalHeight();
            me.paintListener = null;
            me.refreshTotalHeight(me.contentHeight);
            me.callEachSubGrid('refreshFakeScroll');
            me.onHeightChange();
        }
        // If not visible, this operation MUST be done when we become visible.
        // This is announced by the paint event which is triggered when a Widget
        // really gains visibility, ie is shown or rendered, or it's not hidden,
        // and a hidden/non-rendered ancestor is shown or rendered.
        // See Widget#triggerPaint.
        else if (!me.paintListener) {
            me.paintListener = me.on({
                paint   : 'onContentChange',
                once    : true,
                thisObj : me
            });
        }
    }

    onHeightChange() {
        const
            me = this;
        // cache to avoid recalculations in the middle of rendering code (RowManger#getRecordCoords())
        me._bodyRectangle = Rectangle.client(me.bodyContainer);
        me._bodyHeight = me.autoHeight ? me.contentHeight : me.bodyContainer.offsetHeight;
    }

    /**
     * Called after headers have been rendered to the headerContainer.
     * This does not do anything, it's just for Features to hook in to.
     * @param {HTMLElement} headerContainer DOM element which contains the headers.
     * @param {HTMLElement} element Grid element
     * @private
     * @category Rendering
     */
    renderHeader(headerContainer, element) {}

    /**
     * Called after footers have been rendered to the footerContainer.
     * This does not do anything, it's just for Features to hook in to.
     * @param {HTMLElement} footerContainer DOM element which contains the footers.
     * @param {HTMLElement} element Grid element
     * @private
     * @category Rendering
     */
    renderFooter(footerContainer, element) {}

    suspendRefresh() {
        this.refreshSuspended++;
    }

    resumeRefresh(trigger) {
        if (this.refreshSuspended && !--this.refreshSuspended) {
            if (trigger) {
                this.refreshRows();
            }
        }
    }

    /**
     * Rerenders all grid rows, completely replacing all row elements with new ones
     * @category Rendering
     */
    renderRows(keepScroll = true, returnToTop = false) {
        const
            me          = this,
            scrollState = keepScroll && me.storeScroll();

        if (me.refreshSuspended) {
            return;
        }

        /**
         * Grid rows are about to be rendered
         * @event beforeRenderRows
         * @param {Grid.view.Grid} source This grid.
         */
        me.trigger('beforeRenderRows');
        me.renderingRows = true;

        // This allows us to do things like disable animations on a refresh
        me.element.classList.add('b-grid-refreshing');

        if (returnToTop) {
            me.scrollable.y = me._scrollTop = 0;
        }
        me.rowManager.reinitialize(returnToTop);

        /**
         * Grid rows have been rendered
         * @event renderRows
         * @param {Grid.view.Grid} source This grid.
         */
        me.trigger('renderRows');

        me.renderingRows = false;
        me.onContentChange();

        if (keepScroll) {
            me.restoreScroll(scrollState);
        }

        me.element.classList.remove('b-grid-refreshing');
    }

    /**
     * Rerenders the grids rows, headers and footers, completely replacing all row elements with new ones
     * @category Rendering
     */
    renderContents() {
        const
            me = this,
            { element, headerContainer, footerContainer, rowManager } = me;

        me.emptyCache();

        // columns will be "drawn" on render anyway, bail out
        if (me.isPainted) {
            // reset measured header height, to make next call to get headerHeight measure it
            me._headerHeight = null;

            me.callEachSubGrid('refreshHeader', headerContainer);
            me.callEachSubGrid('refreshFooter', footerContainer);

            // Note that these are hook methods for features to plug in to. They do not do anything.
            me.renderHeader(headerContainer, element);
            me.renderFooter(footerContainer, element);

            me.fixSizes();

            // any elements currently used for rows should be released.
            // actual removal of elements is done in SubGrid#clearRows
            const refreshContext = rowManager.removeAllRows();

            rowManager.calculateRowCount(false, true, true);

            if (rowManager.rowCount) {
                // Sets up the RowManager's position for when renderRows calls RowManager#reinitialize
                // so that it renders the correct data block at the correct position.
                rowManager.setPosition(refreshContext);

                me.renderRows();
            }
        }
    }

    // Render rows etc. on first paint, to make sure Grids element has been laid out
    onPaint() {
        const
            me = this,
            {
                rowManager,
                store,
                element,
                headerContainer,
                bodyContainer,
                footerContainer
            } = me;

        if (me.isPainted) {
            return;
        }

        let columnsChanged,
            maxDepth = 0;

        // See if updateResponsive changed any columns.
        me.columns.on({
            change : () => columnsChanged = true,
            single : true
        });

        // Cached, updated on resize. Used by RowManager and by the subgrids upon their render
        me._bodyRectangle = Rectangle.client(me.bodyContainer);
        const bodyOffsetHeight = me.bodyContainer.offsetHeight;

        // Apply any responsive configs before rendering rows.
        me.updateResponsive(me.width, 0);

        // If there were any column changes, apply them
        if (columnsChanged) {
            me.callEachSubGrid('refreshHeader', headerContainer);
            me.callEachSubGrid('refreshFooter', footerContainer);
        }

        // Note that these are hook methods for features to plug in to. They do not do anything.
        // SubGrids take care of their own rendering.
        me.renderHeader(headerContainer, element);
        me.renderFooter(footerContainer, element);

        if (me.autoHeight) {
            me._bodyHeight = rowManager.initWithHeight(element.offsetHeight - headerContainer.offsetHeight - footerContainer.offsetHeight, true);
            bodyContainer.style.height = me.bodyHeight + 'px';
        }
        else {
            me._bodyHeight = bodyOffsetHeight;
            rowManager.initWithHeight(me._bodyHeight, true);
        }

        me.eachSubGrid(subGrid => {
            if (subGrid.header.maxDepth > maxDepth) {
                maxDepth = subGrid.header.maxDepth;
            }
        });

        headerContainer.dataset.maxDepth = maxDepth;

        me.fixSizes();

        if (store.count || !store.isLoading) {
            me.renderRows(false, false);
        }

        me.initScroll();

        me.initInternalEvents();
    }

    render() {
        const me = this;

        // When displayed inside one of our containers, require a size to be considered visible. Ensures it is painted
        // on display when for example in a tab
        me.requireSize = Boolean(me.owner);

        // Render as a container. This renders the child SubGrids
        super.render(...arguments);

        // Sanity check that main element has been given some sizing styles, unless autoHeight is used in which case
        // it will be sized programmatically instead
        if (!me.autoHeight && me.headerContainer.offsetHeight && !me.bodyContainer.offsetHeight) {
            console.warn('Grid element not sized correctly, please check your CSS styles and review how you size the widget');
        }
    }

    //endregion

    // region Masking

    /**
     * Show a load mask with a spinner and the specified message. When using an AjaxStore masking and unmasking is
     * handled automatically, but if you are loading data in other ways you can call this function manually when your
     * load starts.
     * ```
     * myLoadFunction() {
     *   // Show mask before initiating loading
     *   grid.maskBody('Loading data');
     *   // Your custom loading code
     *   load.then(() => {
     *      // Hide the mask when loading is finished
     *      grid.unmaskBody();
     *   });
     * }
     * ```
     * @param {String} loadMask Message to show next to the spinner
     * @returns {Core.widget.Mask}
     */
    maskBody(loadMask) {
        const me = this;

        if (!me.bodyContainer) {
            return;
        }

        // remove any existing mask
        me.unmaskBody();

        const { maskElement } = (me.activeMask = Mask.mask(loadMask, me.element));
        maskElement.style.marginTop = `${me.bodyContainer.offsetTop}px`;
        maskElement.style.height = `${me.virtualScrollers.offsetTop + me.virtualScrollers.offsetHeight - me.bodyContainer.offsetTop}px`;

        return me.activeMask;
    }

    /**
     * Hide the load mask.
     */
    unmaskBody() {
        const me = this;

        me.loadmaskHideTimer && me.clearTimeout(me.loadmaskHideTimer);
        me.loadmaskHideTimer = null;

        me.activeMask && me.activeMask.destroy();
        me.activeMask = null;
    }

    // endregion

    get isAnimating() {
        return this._animating;
    }

    set isAnimating(value) {
        const me = this;
        if (me.isPainted && value !== me.isAnimating) {
            if (value) {
                me.element.classList.add('b-animating');
            }
            else {
                me.element.classList.remove('b-animating');
            }
            me._animating = value;
        }
    }
}

// To not break all translations
GridBase.localeClass = 'Grid';

BryntumWidgetAdapterRegister.register('gridbase', GridBase);
