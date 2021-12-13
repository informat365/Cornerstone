import TimelineBase from './TimelineBase.js';
import DateHelper from '../../Core/helper/DateHelper.js';
import BryntumWidgetAdapterRegister from '../../Core/adapter/widget/util/BryntumWidgetAdapterRegister.js';
import DomHelper from '../../Core/helper/DomHelper.js';
import { base } from '../../Core/helper/MixinHelper.js';

import '../localization/En.js';

import SchedulerDom from './mixin/SchedulerDom.js';
import SchedulerDomEvents from './mixin/SchedulerDomEvents.js';
import SchedulerDragResize from './mixin/SchedulerDragResize.js';
import SchedulerEventRendering from './mixin/SchedulerEventRendering.js';
import SchedulerStores from './mixin/SchedulerStores.js';
import SchedulerScroll from './mixin/SchedulerScroll.js';
import SchedulerRegions from './mixin/SchedulerRegions.js';
import SchedulerState from './mixin/SchedulerState.js';
import EventSelection from './mixin/EventSelection.js';
import EventNavigation from './mixin/EventNavigation.js';
import HorizontalEventMapper from './orientation/HorizontalEventMapper.js';
import VerticalEventMapper from './orientation/VerticalEventMapper.js';
import '../column/TimeAxisColumn.js';
import '../column/VerticalTimeAxisColumn.js';

// Should always be present in Scheduler
import '../../Grid/feature/RegionResize.js';

/**
 * @module Scheduler/view/SchedulerBase
 */

/**
 * A thin base class for {@link Scheduler.view.Scheduler}. Does not include any features by default, allowing smaller
 * custom built bundles if used in place of {@link Scheduler.view.Scheduler}.
 *
 * **NOTE:** In most scenarios you do probably want to use Scheduler instead of SchedulerBase.
 *
 * @mixes Scheduler/view/mixin/EventNavigation
 * @mixes Scheduler/view/mixin/EventSelection
 * @mixes Scheduler/view/mixin/SchedulerDom
 * @mixes Scheduler/view/mixin/SchedulerDomEvents
 * @mixes Scheduler/view/mixin/SchedulerEventRendering
 * @mixes Scheduler/view/mixin/SchedulerRegions
 * @mixes Scheduler/view/mixin/SchedulerScroll
 * @mixes Scheduler/view/mixin/SchedulerState
 * @mixes Scheduler/view/mixin/SchedulerStores
 * @mixes Scheduler/view/mixin/TimelineDateMapper
 * @mixes Scheduler/view/mixin/TimelineDomEvents
 * @mixes Scheduler/view/mixin/TimelineEventRendering
 * @mixes Scheduler/view/mixin/TimelineScroll
 * @mixes Scheduler/view/mixin/TimelineViewPresets
 * @mixes Scheduler/view/mixin/TimelineZoomable
 *
 * @extends Scheduler/view/TimelineBase
 */
// SchedulerDragResize not included as @mixes above on purpose, since it is private
export default class SchedulerBase extends base(TimelineBase).mixes(
    SchedulerDom,
    SchedulerDomEvents,
    SchedulerDragResize,
    SchedulerStores,
    SchedulerScroll,
    SchedulerState,
    SchedulerEventRendering,
    SchedulerRegions,
    EventSelection,
    EventNavigation
) {
    //region Config

    static get $name() {
        return 'SchedulerBase';
    }

    static get defaultConfig() {
        return {
            /**
             * Scheduler mode. Supported values: horizontal, vertical
             * @config {String} mode
             * @default
             */
            mode : 'horizontal',

            /**
             * CSS class to add to rendered events
             * @config {String}
             * @category CSS
             * @private
             * @default
             */
            eventCls : 'b-sch-event',

            /**
             * CSS class to add to cells in the timeaxis column
             * @config {String}
             * @category CSS
             * @private
             * @default
             */
            timeCellCls : 'b-sch-timeaxis-cell',

            timeCellSelector : '.b-sch-timeaxis-cell',

            scheduledEventName : 'event',

            /**
             * A CSS class to apply to each event in the view on mouseover (defaults to 'b-sch-event-hover').
             * @config {String}
             * @default
             * @category CSS
             * @private
             */
            overScheduledEventClass : 'b-sch-event-hover',

            /**
             * Set to false if you don't want to allow events overlapping (defaults to true).
             * @config {Boolean}
             * @default
             * @category Scheduled events
             */
            allowOverlap : true,

            /**
             * The height in pixels of Scheduler rows.
             * @config {Boolean}
             * @default
             */
            rowHeight : 60,

            /**
             * Factor representing the avarge char width in pixels used to determine milestone width when configured
             * with `milestoneLayoutMode: 'estimate'`.
             * @config {Number}
             * @default
             */
            milestoneCharWidth : 10,

            /**
             * How to align milestones in relation to their startDate. Only applies when using a `milestoneLayoutMode`
             * other than `default`. Valid values are:
             * * start
             * * center (default)
             * * end
             */
            milestoneAlign : 'center',

            // This is determined by styling, in the future it should be measured
            milestoneMinWidth : 40
        };
    }

    //endregion

    //region Events

    /**
     * Fired after rendering an event, when its element is available in DOM.
     * @event renderEvent
     * @param {Scheduler.view.Scheduler} source This Scheduler
     * @param {Scheduler.model.EventModel} eventRecord The event record
     * @param {Scheduler.model.ResourceModel} resourceRecord The resource record
     * @param {Scheduler.model.AssignmentModel} assignmentRecord The assignment record, if using an AssignmentStore
     * @param {Object} tplData An object containing details about the event rendering, see {@link Scheduler.view.mixin.SchedulerEventRendering#config-eventRenderer} for details
     * @param {HTMLElement} element The events element
     */

    //endregion

    //region Functions injected by features

    // For documentation & typings purposes

    /**
     * Opens an {@link Scheduler.view.EventEditor EventEditor} to edit the passed event.
     *
     * *NOTE: Only available when the {@link Scheduler/feature/EventEdit EventEdit} feature is enabled.*
     *
     * @function editEvent
     * @param {Scheduler.model.EventModel} eventRecord Event to edit
     * @param {Scheduler.model.ResourceModel} [resourceRecord] The Resource record for the event.
     * This parameter is needed if the event is newly created for a resource and has not been assigned, or when using
     * multi assignment.
     * @param {HTMLElement} [element] Element to anchor editor to (defaults to events element)
     * @category Feature shortcuts
     */

    //endregion

    //region Init

    construct(config = {}) {
        const me = this;

        super.construct(config);

        if (me.createEventOnDblClick) {
            me.on('scheduledblclick', me.onSchedulerDblClick);
        }
    }

    //endregion

    //region Config getters/setters

    // Overrides TimelineBase to supply eventStore as its store (which is only used in passed events)
    set timeAxisViewModel(timeAxisViewModel) {
        super.timeAxisViewModel = timeAxisViewModel;

        if (this.eventStore) {
            this.timeAxisViewModel.eventStore = this.eventStore;
        }
    }

    get timeAxisViewModel() {
        return super.timeAxisViewModel;
    }

    // Placeholder getter/setter for mixins, please make any changes needed to SchedulerStores#store instead
    get store() {
        return super.store;
    }

    set store(store) {
        super.store = store;
    }

    //endregion

    //region Event handlers

    onLocaleChange() {
        this.currentOrientation.onLocaleChange();

        super.onLocaleChange();
    }

    onSchedulerDblClick({ date : startDate, resourceRecord, row }) {
        const me = this;

        if (me.readOnly || resourceRecord.meta.specialRow) {
            return;
        }

        me.internalAddEvent(startDate, resourceRecord, row);
    }

    onColumnsChanged({ action, changes, record : column }) {
        // TODO: Have ResourceHeader call this directly instead of relying on event?
        if (column === this.timeAxisColumn && 'width' in changes) {
            this.updateCanvasSize();
        }

        super.onColumnsChanged(...arguments);
    }

    // Only used in vertical mode
    onVerticalScroll({ scrollTop }) {
        this.currentOrientation.updateFromVerticalScroll(scrollTop);
    }

    /**
     * Called when new event is created.
     * Сan be overriden to supply default record values etc.
     * @param {Scheduler.model.EventModel} eventRecord Newly created event
    */
    onEventCreated(eventRecord) {
    }

    //endregion

    //region Mode

    /**
     * Checks if scheduler is in horizontal mode
     * @returns {Boolean}
     * @readonly
     * @category Common
     * @private
     */
    get isHorizontal() {
        return this.mode === 'horizontal';
    }

    /**
     * Checks if scheduler is in vertical mode
     * @returns {Boolean}
     * @readonly
     * @category Common
     * @private
     */
    get isVertical() {
        return this.mode === 'vertical';
    }

    /**
     * Get/set mode (horizontal/vertical)
     * @property {String}
     * @private
     * @category Common
     */
    get mode() {
        return this._mode;
    }

    set mode(mode) {
        const me = this;

        me._mode = mode;

        if (!me[mode]) {
            if (mode === 'horizontal') {
                me.horizontal = new HorizontalEventMapper(me);
                if (me.isPainted) {
                    me.horizontal.init();
                }

                me.un('scroll', me.onVerticalScroll, me);
            }
            else if (mode === 'vertical') {
                // Zooming is not yet supported in vertical mode, disable it
                me.zoomOnTimeAxisDoubleClick = me.zoomOnMouseWheel = false;

                me.vertical = new VerticalEventMapper(me);
                if (me.rendered) {
                    me.vertical.init();
                }

                me.on('scroll', me.onVerticalScroll, me);
            }
        }
    }

    get currentOrientation() {
        return this[this.mode];
    }

    //endregion

    //region Dom event dummies

    // this is ugly, but needed since super cannot be called from SchedulerDomEvents mixin...

    onElementKeyDown(event) {
        super.onElementKeyDown(event);
    }

    onElementKeyUp(event) {
        super.onElementKeyUp(event);
    }

    onElementMouseOver(event) {
        super.onElementMouseOver(event);
    }

    onElementMouseOut(event) {
        super.onElementMouseOut(event);
    }

    //endregion

    //region Feature hooks

    /**
     * A chainable function which Features may hook to add their own event context menu items
     * when context menu is invoked on an event.
     * @param {Object} params An object containing the available contextual information.
     * @param {Scheduler.model.EventModel} params.eventRecord The context event.
     * @param {Scheduler.model.ResourceModel} params.resourceRecord The context resource
     * @param {Scheduler.model.AssignmentModel} params.assignmentRecord The context assignment if any.
     * @param {HTMLElement} params.eventElement The context event's DOM element.
     * @param {Event} params.event The triggering DOM `contextmenu` event.
     */
    getEventMenuItems() {}

    /**
     * A chainable function which Features may hook to add their own Scheduler context menu items
     * when context menu is invoked on the scheduler, but not on an event.
     * @param {Object} params An object containing the available contextual information.
     * @param {Scheduler.model.ResourceModel} params.resourceRecord The context resource
     * @param {Date} params.date The Date corresponding to the mouse position in the time axis.
     * @param {HTMLElement} params.eventElement The context event's DOM element.
     * @param {Event} params.event The triggering DOM `contextmenu` event.
     */
    getScheduleMenuItems() {}

    //endregion

    //region Scheduler specific date mapping functions

    internalAddEvent(startDate, resourceRecord, row) {
        const
            me              = this,
            resourceRecords = [resourceRecord],
            eventRecord     = new me.eventStore.modelClass({
                startDate,
                endDate : DateHelper.add(startDate, 1, me.timeAxis.unit)
            });

        me.onEventCreated(eventRecord);

        if (me.eventEdit) {
            const
                eventData = me.currentOrientation.getTimeSpanRenderData(eventRecord, resourceRecord),
                proxyEl   = me.eventEdit.dragProxyElement = DomHelper.createElement({
                    parent    : me.foregroundCanvas,
                    className : 'b-sch-dragcreator-proxy',
                    style     : `width:${eventData.width}px;height:${eventData.height}px`
                });

            DomHelper.setTranslateXY(proxyEl, eventData.left, (row && row.top || 0) + eventData.top);

            me.editEvent(eventRecord, resourceRecord, proxyEl);
            return;
        }

        /**
         * Fires before an event is added. Can be triggered by schedule double click, drag create action, or by the event editor.
         * @event beforeEventAdd
         * @param {Scheduler.view.Scheduler} source The Scheduler instance
         * @param {Scheduler.model.EventModel} eventRecord The record about to be added
         * @param {Scheduler.model.ResourceModel[]} resources **Deprecated** Use `resourceRecords` instead
         * @param {Scheduler.model.ResourceModel[]} resourceRecords Resources that the record is assigned to
         * @preventable
         */
        if (me.trigger('beforeEventAdd', { eventRecord, resourceRecords, resources : resourceRecords }) !== false) {
            me.eventStore.add(eventRecord);
            me.eventStore.assignEventToResource(eventRecord, resourceRecord);
        }
    }

    /**
     * Checks if a date range is allocated or not for a given resource.
     * @param {Date} start The start date
     * @param {Date} end The end date
     * @param {Scheduler.model.EventModel} excludeEvent An event to exclude from the check (or null)
     * @param {Scheduler.model.ResourceModel} resource The resource
     * @return {Boolean} True if the timespan is available for the resource
     * @category Dates
     */
    isDateRangeAvailable(start, end, excludeEvent, resource) {
        return this.eventStore.isDateRangeAvailable(start, end, excludeEvent, resource);
    }

    //endregion
}

SchedulerBase.localeClass = 'Scheduler';

BryntumWidgetAdapterRegister.register('schedulerbase', SchedulerBase);
