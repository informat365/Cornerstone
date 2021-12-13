//TODO: Prevent right click while resizing (do it in ResizeHelper?)
//TODO: Scroll

import ResizeBase from './base/ResizeBase.js';
import GridFeatureManager from '../../Grid/feature/GridFeatureManager.js';
import ResizeHelper from '../../Core/helper/ResizeHelper.js';

/**
 * @module Scheduler/feature/EventResize
 */

/**
 * Feature that allows resizing an event by dragging its end.
 *
 * By default it displays a tooltip with the new start and end dates, formatted using
 * {@link Scheduler/view/mixin/TimelineViewPresets#config-displayDateFormat}.
 *
 * This feature is **enabled** by default
 *
 * @extends Scheduler/feature/base/ResizeBase
 * @demo Scheduler/basic
 * @externalexample scheduler/EventResize.js
 */
export default class EventResize extends ResizeBase {
    //region Events

    /**
     * Fired on the owning Scheduler before resizing starts. Return false to prevent operation
     * @event beforeEventResize
     * @param {Scheduler.model.EventModel} eventRecord
     * @param {Event} event
     */

    /**
     * Fires on the owning Scheduler when event resizing starts
     * @event eventResizeStart
     * @param {Scheduler.model.EventModel} eventRecord
     * @param {Event} event
     */

    /**
     * Fires on the owning Scheduler on each resize move event
     * @event eventPartialResize
     * @param {Scheduler.model.EventModel} eventRecord
     * @param {Date} startDate
     * @param {Date} endDate
     * @param {HTMLElement} element
     */

    /**
     * Fired on the owning Scheduler to allow implementer to prevent immediate finalization by setting `data.context.async = true`
     * in the listener, to show a confirmation popup etc
     * ```
     *  scheduler.on('beforeeventresizefinalize', ({context}) => {
     *      context.async = true;
     *      setTimeout(() => {
     *          // async code don't forget to call finalize
     *          context.finalize();
     *      }, 1000);
     *  })
     * ```
     * @event beforeEventResizeFinalize
     * @param {Object} data
     * @param {Scheduler.view.Scheduler} data.source Scheduler instance
     * @param {Object} data.context
     * @param {Boolean} data.context.async Set true to handle resize asynchronously (e.g. to wait for user
     * confirmation)
     * @param {Function} data.context.finalize Call this method to finalize resize. This method accepts one
     * argument: pass true to update records, or false, to ignore changes
     */

    /**
     * Fires on the owning Scheduler after the resizing gesture has finished.
     * @event eventResizeEnd
     * @param {Boolean} wasChanged
     * @param {Scheduler.model.EventModel} eventRecord
     */

    //endregion

    //region Scheduler specifics

    static get $name() {
        return 'EventResize';
    }

    createResizeHelper() {
        const
            me = this,
            client = me.client;

        return new ResizeHelper({
            direction               : client.isVertical ? 'vertical' : 'horizontal',
            name                    : me.constructor.$name, // for debugging
            isElementResizable      : (el, event) => me.isElementResizable(el, event),
            targetSelector          : client.eventSelector,
            handleContainerSelector : client.eventInnerSelector,
            resizingCls             : 'b-sch-event-wrap-resizing',
            allowResize             : me.isElementResizable.bind(me),
            outerElement            : client.timeAxisSubGridElement, // constrain resize to view
            scrollManager           : client.scrollManager,
            dragThreshold           : 0,
            dynamicHandleSize       : true,
            reservedSpace           : 5,
            scroller                : client.timeAxisSubGrid.scrollable, // allow reading correct x & y scroll
            listeners               : {
                beforeresizestart : me.onBeforeResizeStart,
                resizestart       : me.onResizeStart,
                resizing          : me.onResizing,
                resize            : me.onFinishResize,
                cancel            : me.onCancelResize,
                thisObj           : me
            }
        });
    }

    // Store used by ResizeBase to detect updates on dropped record
    get store() {
        return this.client.eventStore;
    }

    // Used by ResizeBase to get an eventRecord from the drag context
    getTimespanRecord(context) {
        return context.eventRecord;
    }

    getRowRecord(context) {
        return context.resourceRecord;
    }

    // Injects Scheduler specific data into the drag context
    setupProductResizeContext(context, event) {
        const
            scheduler      = this.client,
            eventRecord    = scheduler.resolveEventRecord(context.element),
            resourceRecord = scheduler.resolveResourceRecord(context.element);

        Object.assign(context, {
            eventRecord,
            resourceRecord,
            dateConstraints : scheduler.getDateConstraints(resourceRecord, eventRecord)
        });
    }

    onResizeStart({ context, event }) {
        super.onResizeStart({ context, event });

        // Hide terminals when resizing starts
        if (this.client.features.dependencies) {
            this.client.features.dependencies.hideTerminals(context.element);
        }

        // Add resizing cls to inner element, as expected by styling
        context.element.querySelector('.b-sch-event').classList.add('b-sch-event-resizing');
    }

    checkValidity(context, event) {
        return (
            this.client.allowOverlap ||
            this.client.isDateRangeAvailable(context.startDate, context.endDate, context.eventRecord, context.resourceRecord)
        ) && super.checkValidity(context, event);
    }

    onFinishResize({ source, context, event }) {
        super.onFinishResize({ source, context, event });

        // When resizing is done successfully, mouse should be over element, so we show terminals
        if (this.client.features.dependencies) {
            this.client.features.dependencies.showTerminals(context.eventRecord, context.element);
        }
    }

    internalUpdateRecord(context, timespanRecord) {
        const
            { store } = this,
            { generation } = timespanRecord;

        if (context.edge === 'left' || context.edge === 'top') {
            timespanRecord.setStartDate(context.startDate, false, store.skipWeekendsDuringDragDrop);
        }
        else {
            timespanRecord.setEndDate(context.endDate, false, store.skipWeekendsDuringDragDrop);
        }

        // The record has been changed
        if (timespanRecord.generation !== generation) {
            return true;
        }

        // The record has not been changed
        this.client.repaintEventsForResource(context.resourceRecord);
        return false;
    }

    finalize(updateRecord) {
        this.resize.context.element.querySelector('.b-sch-event').classList.remove('b-sch-event-resizing');
        super.finalize(updateRecord);
    }

    //endregion
}

GridFeatureManager.registerFeature(EventResize, true, 'Scheduler');
