import Base from '../../../Core/Base.js';
// eslint-disable-next-line import/no-named-default
import { default as DH } from '../../../Core/helper/DateHelper.js';

/**
 * @module Scheduler/view/mixin/SchedulerRegions
 */

/**
 * Functions to get regions (bounding boxes) for scheduler, events etc.
 *
 * @mixin
 */
export default Target => class SchedulerRegions extends (Target || Base) {
    //region Orientation depended regions

    /**
     * Gets the region represented by the schedule and optionally only for a single resource. The view will ask the scheduler for
     * the resource availability by calling getResourceAvailability. By overriding that method you can constrain events differently for
     * different resources.
     * @param {Scheduler.model.ResourceModel} resourceRecord (optional) The resource record
     * @param {Scheduler.model.EventModel} eventRecord (optional) The event record
     * @return {Object} The region of the schedule
     */
    getScheduleRegion(resourceRecord, eventRecord, local = true) {
        return this.currentOrientation.getScheduleRegion(resourceRecord, eventRecord, local);
    }

    /**
     * Gets the region representing the passed resource and optionally just for a certain date interval.
     * @param {Scheduler.model.ResourceModel} resourceRecord The resource record
     * @param {Date} startDate A start date constraining the region
     * @param {Date} endDate An end date constraining the region
     * @return {Core.helper.util.Rectangle} A Rectangle which encapsulates the resource time span
     */
    getResourceRegion(resourceRecord, startDate, endDate) {
        return this.currentOrientation.getRowRegion(resourceRecord, startDate, endDate);
    }

    //endregion

    //region ResourceEventBox

    /**
     * Get the region for a specified resources specified event.
     * @param {Scheduler.model.EventModel} eventRecord
     * @param {Scheduler.model.ResourceModel} resourceRecord
     * @param {Boolean} includeOutside Specify true to get boxes for events outside of the rendered zone in both dimensions. This option is used when calculating
     * dependency lines, and we need to include routes from events which may be outside the rendered zone.
     * @returns {*}
     */
    getResourceEventBox(eventRecord, resourceRecord, includeOutside = false) {
        // event caches its position when rendered, return it if available
        const
            me = this,
            // Vertical always returns a box here
            cached = me.currentOrientation.getResourceEventBox(eventRecord.id, resourceRecord.id);

        if (cached) {
            return cached;
        }

        // TODO: Move to HorizontalEventMapper
        const
            viewStartDate  = me.timeAxis.startDate,
            viewEndDate    = me.timeAxis.endDate,
            eventStartDate = eventRecord.startDate,
            eventEndDate   = eventRecord.endDate;

        /* eslint-disable */
        let result         = null,
            eventLayout, rowEventsLayoutData, eventRecordData,
            rowElement, rowIndex, rowTop, rowHeight, rowNbrOfBands,
            eventElements, eventElement, eventElOffsets, eventElBox,
            firstRowIndex, firstRowRecord, lastRowIndex, lastRowRecord, lastRowEl,
            foundTplData;
        /* eslint-enable */

        // Checking if event record is within current time axis timespan and is visible, i.e. it's not rendered
        // within a collapsed row (scheduler supports resource tree store as well as flat resource store).
        if (
            eventStartDate && eventEndDate && (includeOutside || DH.intersectSpans(eventStartDate, eventEndDate, viewStartDate, viewEndDate)) &&
            this.isRowVisible(resourceRecord)
        ) {
            rowElement = me.getRowFor(resourceRecord);

            // If resource row is rendered and displayed
            if (rowElement) {
                // Managed event sizing means that the view is responsible for event height setting, the oposite case
                // is when event height is controlled by CSS's top and height properties.

                // Fast case: managed event sizing on, querying the view for box position and dimensions
                if (me.managedEventSizing) {
                    eventLayout = me.currentEventLayout;

                    // Preparing events layout data for event layout instance to process
                    rowEventsLayoutData = me.eventStore.getEventsForResource(resourceRecord).reduce((result, event) => {
                        const eventBox =  me[me.mode].getTimeSpanRenderData(event, resourceRecord, includeOutside);

                        if (eventBox) {
                            result.push(eventBox);
                        }
                        return result;
                    }, []);

                    // Processing event layout data injecting event vertical position into each item of `rowEventsLayoutData`
                    // This layout application takes into account view's `dynamicRowHeight` property
                    eventLayout && eventLayout.applyLayout(rowEventsLayoutData, resourceRecord);

                    // Now we are to find our particular event data inside all events data for the given row record
                    eventRecordData = rowEventsLayoutData.find(eventData => eventData.event == eventRecord);

                    // We must find our event record corresponding layout data object here, but just to make sure
                    if (eventRecordData) {
                        // We have event record data with coordinates within the row node, but we need
                        // those coordinates to be translated relative to view's viewport top.

                        rowTop = me.getRecordCoords(resourceRecord, true).y;

                        // Finally we have all the data needed to calculated the event record box
                        result = {
                            layout : true,
                            start  : (eventRecordData.hasOwnProperty('left') ? eventRecordData.left : eventRecordData.right), // it depends on view's `rtl` configuration
                            end    : (eventRecordData.hasOwnProperty('left') ? eventRecordData.left : eventRecordData.right) + eventRecordData.width,
                            top    : rowTop + eventRecordData.top,
                            bottom : rowTop + eventRecordData.top + eventRecordData.height
                        };
                    }
                }
                // Slow case: managed event sizing off, querying the DOM for box position and dimensions
                else {
                    eventElements = me.getElementsFromEventRecord(eventRecord, eventRecord !== resourceRecord && resourceRecord);

                    // We must have at one and only one element here, but just to make sure
                    if (eventElements.length) {
                        eventElement        = eventElements[0];
                        // TODO: PORT getOffsetsTo and getBox
                        eventElOffsets = eventElement.getOffsetsTo(me.getEl());
                        eventElBox     = eventElement.getBox();

                        result = {
                            layout : true,
                            start  : eventElOffsets[0],
                            end    : eventElOffsets[0] + eventElBox.width,
                            top    : eventElOffsets[1],
                            bottom : eventElOffsets[1] + eventElBox.height
                        };
                    }
                }
            }
            // Resource row is not rendered, and it's not collapsed. We are to calculate event record box approximately.
            else {
                result = {
                    layout : false,
                    start  : me.getCoordinateFromDate(Math.max(eventRecord.startDateMS, me.timeAxis.startMS)),
                    end    : me.getCoordinateFromDate(Math.min(eventRecord.endDateMS, me.timeAxis.endMS))
                    // top and bottom to go
                };

                // Request local record coordinates within the scroll range.
                const recordCoords = me.rowManager.getRecordCoords(resourceRecord, true);
                // faster than Object.assign...
                result.top       = recordCoords.y + me.barMargin;
                result.height    = Math.max(me.rowManager.rowHeight - (2 * me.barMargin), 1);
                result.bottom    = result.top + result.height;
            }

            // Some boxes might need special adjustments
            if (result) {
                result = me.adjustItemBox(eventRecord, result);
            }
        }

        return result;
    }

    //endregion

    //region Item box

    /**
     * Gets box for displayed item designated by the record. If several boxes are displayed for the given item
     * then the method returns all of them. Box coordinates are in view coordinate system.
     *
     * Boxes outside scheduling view timeaxis timespan and inside collapsed rows (if row defining store is a tree store)
     * will not be returned. Boxes outside scheduling view vertical visible area (i.e. boxes above currently visible
     * top row or below currently visible bottom row) will be calculated approximately.
     *
     * @param {Scheduler.model.EventModel} event
     * @return {Object|Object[]}
     * @return {Boolean} return.isPainted Whether the box was calculated for the rendered scheduled record or was
     *                                   approximatelly calculated for the scheduled record outside of the current
     *                                   vertical view area.
     * @return {Number} return.top
     * @return {Number} return.bottom
     * @return {Number} return.start
     * @return {Number} return.end
     * @return {String} return.relPos if the item is not rendered then provides a view relative position one of 'before', 'after'
     * @internal
     */
    getItemBox(event, includeOutside = false) {
        return event.resources.map(resource => this.getResourceEventBox(event, resource, includeOutside));
    }

    /**
     * Adjusts event record box if needed
     *
     * @param {Scheduler.model.EventModel} eventRecord
     * @param {Object} eventRecordBox
     * @return {Number} eventRecordBox.top
     * @return {Number} eventRecordBox.bottom
     * @return {Number} eventRecordBox.start
     * @return {Number} eventRecordBox.end
     * @return {Object}
     * @return {Number} return.top
     * @return {Number} return.bottom
     * @return {Number} return.start
     * @return {Number} return.end
     * @internal
     */
    adjustItemBox(eventRecord, eventRecordBox) {
        const
            viewStartMS         = this.timeAxis.startMS,
            viewEndMS           = this.timeAxis.endMS,
            eventStartMS        = eventRecord.startDateMS,
            eventEndMS          = eventRecord.endDateMS,
            width               = this.timeAxisViewModel.totalSize,
            OUTSIDE_VIEW_OFFSET = 40; // To make sure non-relevant dependency lines aren't seen

        // adjust event box to render dependency lines for events that are outside of the view
        if (viewStartMS > eventStartMS) eventRecordBox.start = -OUTSIDE_VIEW_OFFSET;
        if (viewStartMS > eventEndMS)   eventRecordBox.end   = -OUTSIDE_VIEW_OFFSET;
        if (viewEndMS   < eventStartMS) eventRecordBox.start = width + OUTSIDE_VIEW_OFFSET;
        if (viewEndMS   < eventEndMS)   eventRecordBox.end   = width + OUTSIDE_VIEW_OFFSET;

        return eventRecordBox;
    }

    //endregion

    // This does not need a className on Widgets.
    // Each *Class* which doesn't need 'b-' + constructor.name.toLowerCase() automatically adding
    // to the Widget it's mixed in to should implement thus.
    get widgetClass() {}
};
