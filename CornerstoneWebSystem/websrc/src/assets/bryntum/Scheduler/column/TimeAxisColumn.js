import StringHelper from '../../Core/helper/StringHelper.js';
import Column from '../../Grid/column/Column.js';
import ColumnStore from '../../Grid/data/ColumnStore.js';
import Events from '../../Core/mixin/Events.js';
import HorizontalTimeAxis from '../view/HorizontalTimeAxis.js';
import EventHelper from '../../Core/helper/EventHelper.js';
import ResourceHeader from '../view/ResourceHeader.js';
import DomHelper from '../../Core/helper/DomHelper.js';
import ObjectHelper from '../../Core/helper/ObjectHelper.js';

/**
 * @module Scheduler/column/TimeAxisColumn
 */

/**
 * A column containing the timeline "viewport", in which events, dependencies etc are drawn. Normally you do not need
 * to interact with or create this column, it is handled by Scheduler/Gantt.
 *
 * @extends Grid/column/Column
 */
export default class TimeAxisColumn extends Events(Column) {
    // region Events
    /**
     * Fires after a click on a time axis cell
     * @event timeAxisHeaderClick
     * @param {Scheduler.column.TimeAxisColumn} column The column object
     * @param {Date} startDate The start date of the header cell
     * @param {Date} endDate The start date of the header cell
     * @param {Event} event The event object
     */

    /**
     * Fires after a double click on a time axis cell
     * @event timeAxisHeaderDblClick
     * @param {Scheduler.column.TimeAxisColumn} column The column object
     * @param {Date} startDate The start date of the header cell
     * @param {Date} endDate The end date of the header cell
     * @param {Event} event The event object
     */

    /**
     * Fires after a right click on a time axis cell
     * @event timeAxisHeaderContextMenu
     * @param {Scheduler.column.TimeAxisColumn} column The column object
     * @param {Date} startDate The start date of the header cell
     * @param {Date} endDate The start date of the header cell
     * @param {Event} event The event object
     */
    //endregion

    //region Init

    construct() {
        const me = this;

        super.construct(...arguments);

        me.initialRender           = true;
        me.thisObj                 = me;
        me.timeAxisViewModel       = me.timeline.timeAxisViewModel;
        // A bit hacky, because mode is a field and not a config
        // eslint-disable-next-line no-self-assign
        me.mode                    = me.mode;

        me.grid.on({
            paint   : 'onTimelinePaint',
            thisObj : me,
            once    : true
        });

        me.timeAxisViewModel.on({
            update  : me.onViewModelUpdate,
            thisObj : me
        });
    }

    static get autoExposeFields() {
        return true;
    }

    // endregion

    //region Config

    static get fields() {
        return [
            'mode'
        ];
    }

    static get defaults() {
        return {
            /**
             * Set to false to prevent this column header from being dragged.
             * @config {Boolean} draggable
             * @category Interaction
             * @default false
             */
            draggable : false,

            /**
             * Set to false to prevent grouping by this column.
             * @config {Boolean} groupable
             * @category Interaction
             * @default false
             */
            groupable : false,

            /**
             * Allow column visibility to be toggled through UI.
             * @config {Boolean} hideable
             * @default false
             * @category Interaction
             */
            hideable : false,

            /**
             * Show column picker for the column.
             * @config {Boolean} showColumnPicker
             * @default false
             * @category Menu
             */
            showColumnPicker : false,

            /**
             * Allow filtering data in the column (if Filter feature is enabled)
             * @config {Boolean} filterable
             * @default false
             * @category Interaction
             */
            filterable : false,

            /**
             * Allow sorting of data in the column
             * @config {Boolean} sortable
             * @category Interaction
             * @default false
             */
            sortable : false,

            /**
             * Set to `false` to prevent the column from being drag-resized when the ColumnResize plugin is enabled.
             * @config {Boolean} resizable
             * @default false
             * @category Interaction
             */
            resizable : false,

            /**
             * Allow searching in the column (respected by QuickFind and Search features)
             * @config {Boolean} searchable
             * @default false
             * @category Interaction
             */
            searchable : false,

            /**
             * Specifies if this column should be editable, and define which editor to use for editing cells in the column (if CellEdit feature is enabled)
             * @config {String} editor
             * @default false
             * @category Interaction
             */
            editor : false,

            /**
             * false to prevent showing a context menu on the cell elements in this column
             * @config {Boolean} enableCellContextMenu
             * @default false
             * @category Menu
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
             */
            tooltipRenderer : false,

            /**
             * CSS class added to the header of this column
             * @config {String} cls
             * @category Rendering
             * @default 'b-sch-timeaxiscolumn'
             */
            cls : 'b-sch-timeaxiscolumn',

            // needs to have width specified, flex-basis messes measurements up
            needWidth : true,

            mode : null,

            region : 'normal',

            exportable : false
        };
    }

    static get type() {
        return 'timeAxis';
    }

    set mode(mode) {
        const
            me           = this,
            { timeline } = me;

        me.set('mode', mode);

        // In horizontal mode this column has a time axis header on top, with timeline ticks
        if (mode === 'horizontal') {
            me.timeAxisView = new HorizontalTimeAxis({
                model                     : me.timeAxisViewModel,
                compactCellWidthThreshold : me.compactCellWidthThreshold,
                owner                     : me.grid,
                client                    : me.grid
            });
        }
        // In vertical mode, it instead displays resources at top
        else if (mode === 'vertical') {
            // TODO: Most other vertical stuff is handled in VerticalEventMapper, move there?
            me.resourceColumns = new ResourceHeader(ObjectHelper.assign({
                column           : me,
                resourceStore    : timeline.resourceStore,
                imagePath        : timeline.resourceImagePath,
                defaultImageName : timeline.defaultResourceImageName
            }, timeline.resourceColumns || {}));
        }
    }

    get mode() {
        return this.get('mode');
    }

    // TODO: define all configs as fields and set below to false...

    //region Events

    onViewModelUpdate({ source : viewModel }) {
        const me = this;

        if (me.mode === 'horizontal') {
            // render the time axis view into the column header element
            me.refreshHeader(true);

            me.width = viewModel.totalSize;

            me.timeline.refresh();
        }
        else if (me.mode === 'vertical') {
            // Refresh to rerender cells, in the process updating the vertical timeaxis to reflect view model changes
            me.timeline.refreshRows();
        }
    }

    // Called on paint. SubGrid has its width so this is the earliest time to configure the TimeAxisViewModel with
    // correct width
    onTimelinePaint({ firstPaint }) {
        const me = this;

        if (!me.subGrid.insertRowsBefore) {
            return;
        }

        if (firstPaint) {
            me.subGridElement.classList.add('b-timeline-subgrid');

            // If the owning Scheduler has hideHeaders: true, element won't be available.
            if (me.element) {
                EventHelper.on({
                    element     : me.element,
                    thisObj     : me,
                    click       : 'onContainerElementClick',
                    dblclick    : 'onContainerElementClick',
                    contextmenu : 'onContainerElementClick'
                });
            }

            if (me.mode === 'vertical') {
                me.refreshHeader();
            }
        }
    }

    //endregion

    //region Rendering

    /**
     * Refreshes the columns header contents (which is either a HorizontalTimeAxis or a ResourceHeader). Useful if you
     * have rendered some extra meta data that depends on external data such as the EventStore or ResourceStore.
     */
    refreshHeader(internal) {
        const me = this,
            { element } = me;

        if (element) {

            if (me.mode === 'horizontal') {
                // Force timeAxisViewModel to regenerate its column config, which calls header renderers etc.
                !internal && me.timeAxisViewModel.update(null, true);

                if (!me.timeAxisView.rendered) {
                    // Do not need the normal header markup
                    element.innerHTML = '';

                    me.timeAxisView.render(element);
                }
                else {
                    // Force rebuild of cells in case external data has changed (cheap since it still syncs to DOM)
                    me.timeAxisView.refresh(true);
                }
            }
            else if (me.mode === 'vertical') {
                if (!me.resourceColumns.currentElement) {
                    // Do not need the normal header markup
                    element.innerHTML = '';

                    me.resourceColumns.render(element);
                }

                // Vertical's resourceColumns is redrawn with the events, no need here
            }
        }
    }

    renderer(renderData) {
        return this.timeline.currentOrientation.renderer(renderData);
    }

    //endregion

    // region DOM events
    onContainerElementClick(event) {
        const target = DomHelper.up(event.target, '.b-sch-header-timeaxis-cell');

        if (target) {
            const
                index        = target.dataset.tickIndex,
                position     = target.parentElement.dataset.headerPosition,
                columnConfig = this.timeAxisViewModel.columnConfig[position][index];

            // Skip same events with Grid context menu triggerEvent
            const contextMenu = this.grid.features.contextMenu;
            if (!contextMenu || event.type !== contextMenu.triggerEvent) {
                this.trigger('timeAxisHeader' + StringHelper.capitalizeFirstLetter(event.type), {
                    startDate : columnConfig.start,
                    endDate   : columnConfig.end,
                    event
                });
            }
        }
    }
    // endregion
}

ColumnStore.registerColumnType(TimeAxisColumn);
