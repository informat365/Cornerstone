import Base from '../../../Core/Base.js';
import DomHelper from '../../../Core/helper/DomHelper.js';
import Rectangle from '../../../Core/helper/util/Rectangle.js';
import ObjectHelper from '../../../Core/helper/ObjectHelper.js';

/**
 * @module Scheduler/view/mixin/SchedulerScroll
 */

const immediatePromise = new Promise((resolve) => resolve()),
    defaultScrollOptions = {
        block      : 'nearest',
        edgeOffset : 20
    };

/**
 * Functions for scrolling to events, dates etc.
 *
 * @mixin
 */
export default Target => class SchedulerScroll extends (Target || Base) {
    //region Scroll to event

    /**
     * Scrolls an event record into the viewport.
     * If the resource store is a tree store, this method will also expand all relevant parent nodes to locate the event.
     *
     * @param {Scheduler.model.EventModel} eventRec the event record to scroll into view
     * @param {Object} [options] How to scroll.
     * @param {String} [options.block=nearest] How far to scroll the event: `start/end/center/nearest`.
     * @param {Number} [options.edgeOffset=20] edgeOffset A margin *in pixels* around the event to bring into view.
     * @param {Boolean/Number} [options.animate] Set to `true` to animate the scroll, or the number of milliseconds to animate over.
     * @param {Boolean} [options.highlight] Set to `true` to highlight the event element when it is in view.
     * @param {Boolean} [options.focus] Set to `true` to focus the event element when it is in view.
     * @returns {Promise} A Promise which resolves when the scrolling is complete.
     *
     * This function is not applicable for events with multiple assignments, please use #scrollResourceEventIntoView instead.
     */
    scrollEventIntoView(eventRec, options = defaultScrollOptions) {
        const
            me        = this,
            resources = eventRec.resources || [eventRec];

        if (resources.length > 1) {
            throw new Error('scrollEventIntoView() is not applicable for events with multiple assignments, please use scrollResourceEventIntoView() instead.');
        }

        if (!resources.length) {
            console.warn('You have asked to scroll to an event which is not assigned to a resource');
            return immediatePromise;
        }

        return me.scrollResourceEventIntoView(resources[0], eventRec, null, options);
    }

    /**
     * Scrolls a resource event record into the viewport.
     *
     * If the resource store is a tree store, this method will also expand all relevant parent nodes
     * to locate the event.
     *
     * @param {Scheduler.model.ResourceModel} resourceRec A resource record an event record is assigned to
     * @param {Scheduler.model.EventModel} eventRec An event record to scroll into view
     * @param {Number} index DOM node index, applicable only for weekview
     * @param {Object} [options] How to scroll.
     * @param {String} [options.block=nearest] How far to scroll the event: `start/end/center/nearest`.
     * @param {Number} [options.edgeOffset=20] edgeOffset A margin *in pixels* around the event to bring into view.
     * @param {Boolean/Number} [options.animate] Set to `true` to animate the scroll, or the number of milliseconds to animate over.
     * @param {Boolean} [options.extendTimeAxis=true] By default, if the requested event is outside the time axis, the time axis is extended.
     * @param {Boolean} [options.highlight] Set to `true` to highlight the event element when it is in view.
     * @param {Boolean} [options.focus] Set to `true` to focus the event element when it is in view.
     * @returns {Promise} A Promise which resolves when the scrolling is complete.
     */
    scrollResourceEventIntoView(resourceRec, eventRec, index, options = defaultScrollOptions) {
        const
            me             = this,
            eventStart     = eventRec.startDate,
            eventEnd       = eventRec.endDate,
            eventIsOutside = eventStart < me.timeAxis.startDate | ((eventEnd > me.timeAxis.endDate) << 1);

        let el;

        if (options.edgeOffset == null) {
            options.edgeOffset = 20;
        }

        // Make sure event is within TimeAxis time span unless extendTimeAxis passed as false.
        // The EventEdit feature passes false because it must not mutate the TimeAxis.
        // Bitwise flag:
        //  1 === start is before TimeAxis start.
        //  2 === end is after TimeAxis end.
        if (eventIsOutside && options.extendTimeAxis !== false) {
            let currentTimeSpanRange = me.timeAxis.endDate - me.timeAxis.startDate,
                startAnchorPoint,
                endAnchorPoint;

            // Event is too wide, expand the range to encompass it.
            if (eventIsOutside === 3) {
                me.timeAxis.setTimeSpan(
                    new Date(eventStart.valueOf() - currentTimeSpanRange / 2),
                    new Date(eventEnd.getTime() + currentTimeSpanRange / 2)
                );
            }
            // Event is partially or wholly outside but will fit.
            // Move the TimeAxis to include it. Attempt to maintain visual position.
            else {
                startAnchorPoint = (eventIsOutside & 1)
                    ? me.getCoordinateFromDate(eventRec.endDate)
                    : me.getCoordinateFromDate(eventRec.startDate);

                // Event starts before
                if (eventIsOutside & 1) {
                    me.timeAxis.setTimeSpan(
                        new Date(eventStart),
                        new Date(eventStart.valueOf() + currentTimeSpanRange)
                    );
                }
                // Event ends after
                else {
                    me.timeAxis.setTimeSpan(
                        new Date(eventEnd.valueOf() - currentTimeSpanRange),
                        new Date(eventEnd)
                    );
                }
                // Restore view to same relative scroll position.
                endAnchorPoint = (eventIsOutside & 1)
                    ? me.getCoordinateFromDate(eventRec.endDate)
                    : me.getCoordinateFromDate(eventRec.startDate);

                me.timeAxisSubGrid.scrollable.scrollBy(endAnchorPoint - startAnchorPoint);
            }
        }

        // Establishing element to scroll to
        el = me.getElementFromEventRecord(eventRec, resourceRec);

        if (el) {
            // It's usually the event wrapper that holds focus
            if (!DomHelper.isFocusable(el)) {
                el = el.parentNode;
            }

            const scroller = me.timeAxisSubGrid.scrollable;

            // Force horizontalscroll to be triggered directly on scroll instead of on next frame, to have events
            // already drawn when promise resolves
            me.timeAxisSubGrid.forceScrollUpdate = true;
            // Scroll into view with animation and highlighting if needed.
            return scroller.scrollIntoView(el, options);
        }
        else if (eventIsOutside && options.extendTimeAxis === false) {
            console.warn('You have asked to scroll to an event which is outside the current view and extending timeaxis is disabled');
            return immediatePromise;
        }
        else if (!me.eventStore.isAvailable(eventRec)) {
            console.warn('You have asked to scroll to an event which is not available');
            return immediatePromise;
        }
        else {
            // Event not rendered, scroll to calculated location
            return me.scrollUnrenderedEventIntoView(resourceRec, eventRec, options);
        }
    }

    /**
     * Scrolls an unrendered event into view. Internal function used from #scrollResourceEventIntoView.
     * @private
     */
    scrollUnrenderedEventIntoView(resourceRec, eventRec, options = defaultScrollOptions) {
        if (options.edgeOffset == null) {
            options.edgeOffset = 20;
        }

        // We must only resolve when the event's element has been painted.
        return new Promise(resolve => {
            const me             = this,
                scroller         = me.timeAxisSubGrid.scrollable,
                box              = me.getResourceEventBox(eventRec, resourceRec), // TODO: have all "box" type objects use Rectangle
                scrollerViewport = scroller.viewport,
                targetRect       = new Rectangle(box.start, box.top, box.end - box.start, box.bottom - box.top).translate(scrollerViewport.x - scroller.x, scrollerViewport.y - scroller.y),
                onEventPaint     = ({ scheduler, eventRecord, resourceRecord, element }) => {
                    if (eventRecord === eventRec) {
                        detatcher();
                        options.highlight && DomHelper.highlight(element);
                        options.focus && element.focus();
                        resolve();
                    }
                };

            // On either paint or repaint of the event, resolve the scroll promise and detach the listeners.
            const detatcher = me.on({
                eventrepaint : onEventPaint,
                eventpaint   : onEventPaint
            });

            scroller.scrollIntoView(targetRect, Object.assign({}, options, { highlight : false }));
        });
    }

    /**
     * Scrolls the specified resource into view, works for both horizontal and vertical modes.
     * @param {Scheduler.model.ResourceModel} resourceRecord A resource record an event record is assigned to
     * @param {Object} [options] How to scroll.
     * @param {String} [options.column] Field name or ID of the column, or the Column instance to scroll to (in horizontal mode).
     * @param {String} [options.block] How far to scroll the element: `start/end/center/nearest`.
     * @param {Number} [options.edgeOffset] edgeOffset A margin around the element or rectangle to bring into view.
     * @param {Boolean|Number} [options.animate] Set to `true` to animate the scroll, or the number of milliseconds to animate over.
     * @param {Boolean} [options.highlight] Set to `true` to highlight the element when it is in view.
     * @returns {Promise} A promise which is resolved when the scrolling has finished.
     */
    scrollResourceIntoView(resourceRecord, options = defaultScrollOptions) {
        if (this.isVertical) {
            return this.currentOrientation.scrollResourceIntoView(resourceRecord, options);
        }
        else {
            return this.scrollRowIntoView(resourceRecord, options);
        }
    }

    //endregion

    // This does not need a className on Widgets.
    // Each *Class* which doesn't need 'b-' + constructor.name.toLowerCase() automatically adding
    // to the Widget it's mixed in to should implement thus.
    get widgetClass() {}
};
