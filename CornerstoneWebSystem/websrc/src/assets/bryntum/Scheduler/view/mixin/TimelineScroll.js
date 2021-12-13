import Base from '../../../Core/Base.js';
import Rectangle from '../../../Core/helper/util/Rectangle.js';

/**
 * @module Scheduler/view/mixin/TimelineScroll
 */

/**
 * Functions for scrolling to events, dates etc.
 *
 * @mixin
 */
export default Target => class TimelineScroll extends (Target || Base) {
    //region Scroll to date

    /**
     * Scrolls the time line "tick" encapsulating the passed `Date` into view according to the passed options.
     * @param {Date} date The date to which to scroll the time line
     * @param {Object} [options] How to scroll.
     * @param {String} [options.block=nearest] How far to scroll the tick: `start/end/center/nearest`.
     * @param {Number} [options.edgeOffset=20] edgeOffset A margin *in pixels* around the tick to bring into view.
     * @param {Boolean/Number} [options.animate] Set to `true` to animate the scroll, or the number of milliseconds to animate over.
     * @returns {Promise} A Promise which resolves when the scrolling is complete.
     */
    scrollToDate(date, options = {}) {
        const
            me               = this,
            scroller         = me.timeAxisSubGrid.scrollable,
            scrollerViewport = scroller.viewport,
            localCoordinate  = me.getCoordinateFromDate(date, true),
            target           = me.isHorizontal
                ? new Rectangle(me.getCoordinateFromDate(date, false), scrollerViewport.y, me.timeAxisViewModel.tickSize, scrollerViewport.height)
                : new Rectangle(scrollerViewport.x, me.getCoordinateFromDate(date, false), scrollerViewport.width, me.timeAxisViewModel.tickSize);

        return me.scrollToCoordinate(localCoordinate, target, date, options);
    }

    /**
     * Scrolls to current time.
     * @param {Object} [options] How to scroll.
     * @param {String} [options.block=nearest] How far to scroll the tick: `start/end/center/nearest`.
     * @param {Number} [options.edgeOffset=20] edgeOffset A margin *in pixels* around the tick to bring into view.
     * @param {Boolean/Number} [options.animate] Set to `true` to animate the scroll, or the number of milliseconds to animate over.
     * @returns {Promise} A Promise which resolves when the scrolling is complete.
     */
    scrollToNow(options = {}) {
        return this.scrollToDate(new Date(), options);
    }

    /**
     * Used by {@link #function-scrollToDate} to scroll to correct coordinate.
     * @param {Number} localCoordinate Coordinate to scroll to
     * @param {Date} date Date to scroll to, used for reconfiguring the time axis
     * @param {Object} [options] How to scroll.
     * @param {String} [options.block=nearest] How far to scroll the tick: `start/end/center/nearest`.
     * @param {Number} [options.edgeOffset] edgeOffset A margin *in pixels* around the tick to bring into view.
     * @param {Boolean/Number} [options.animate] Set to `true` to animate the scroll, or the number of milliseconds to animate over.
     * @returns {Promise} A Promise which resolves when the scrolling is complete.
     * @private
     */
    scrollToCoordinate(localCoordinate, target, date, options = {}) {
        const me = this;

        // Not currently have this date in a timeaxis. Ignore negative scroll in weekview, it can be just 'filtered' with
        // startTime/endTime config
        if (localCoordinate < 0) {
            // adjust the timeaxis first
            const halfVisibleSpan = (me.timeAxis.endDate - me.timeAxis.startDate) / 2,
                newStartDate    = new Date(date.getTime() - halfVisibleSpan),
                newEndDate      = new Date(date.getTime() + halfVisibleSpan);

            // We're trying to reconfigure time span to current dates, which means we are as close to center as it
            // could be. Do nothing then.
            // covered by 1102_panel_api
            if (newStartDate - me.startDate !== 0 && newEndDate - me.endDate !== 0) {
                me.setTimeSpan(newStartDate, newEndDate);

                return me.scrollToDate(date, options);
            }

            return;
        }

        return me.timeAxisSubGrid.scrollable.scrollIntoView(target, options);
    }

    //endregion

    //region Relative scrolling
    // These methods are important to users because although they are mixed into the top level Grid/Scheduler,
    // for X scrolling the explicitly target the SubGrid that holds the scheduler.

    /**
     * Get/set horizontal scroll. Applies to the SubGrid that holds the scheduler
     * @property {Number}
     * @category Scrolling
     */
    set scrollLeft(left) {
        this.timeAxisSubGrid.scrollable.x = left;
    }

    /**
     * Get/set vertical scroll
     * @property {Number}
     * @category Scrolling
     */
    set scrollTop(top) {
        this.scrollable.y = top;
    }

    get scrollLeft() {
        return this.timeAxisSubGrid.scrollable.x;
    }

    get scrollTop() {
        return this.scrollable.y;
    }

    /**
     * Horizontal scrolling. Applies to the SubGrid that holds the scheduler
     * @param {Number} x
     * @param {Object|Boolean} [options] How to scroll. May be passed as `true` to animate.
     * @param {Object|Boolean|Number} [options.animate] Set to `true` to animate the scroll by 300ms,
     * or the number of milliseconds to animate over, or an animation config object.
     * @param {Number} [options.animate.duration] The number of milliseconds to animate over.
     * @param {String} [options.animate.easing] The name of an easing function.
     * @returns {Promise} A promise which is resolved when the scrolling has finished.
     */
    scrollHorizontallyTo(coordinate, options = true) {
        return this.timeAxisSubGrid.scrollable.scrollTo(coordinate, null, options);
    }

    /**
     * Vertical scrolling
     * @param {Number} y
     * @param {Object|Boolean} [options] How to scroll. May be passed as `true` to animate.
     * @param {Object|Boolean|Number} [options.animate] Set to `true` to animate the scroll by 300ms,
     * or the number of milliseconds to animate over, or an animation config object.
     * @param {Number} [options.animate.duration] The number of milliseconds to animate over.
     * @param {String} [options.animate.easing] The name of an easing function.
     * @returns {Promise} A promise which is resolved when the scrolling has finished.
     */
    scrollVerticallyTo(y, options = true) {
        return this.scrollable.scrollTo(null, y, options);
    }

    /**
     * Scrolls the subgrid that contains the scheduler
     * @param {Number} x
     * @param {Object|Boolean} [options] How to scroll. May be passed as `true` to animate.
     * @param {Object|Boolean|Number} [options.animate] Set to `true` to animate the scroll by 300ms,
     * or the number of milliseconds to animate over, or an animation config object.
     * @param {Number} [options.animate.duration] The number of milliseconds to animate over.
     * @param {String} [options.animate.easing] The name of an easing function.
     * @returns {Promise} A promise which is resolved when the scrolling has finished.
     */
    scrollTo(x, options = true) {
        return this.timeAxisSubGrid.scrollable.scrollTo(x, null, options);
    }

    //endregion

    // This does not need a className on Widgets.
    // Each *Class* which doesn't need 'b-' + constructor.name.toLowerCase() automatically adding
    // to the Widget it's mixed in to should implement thus.
    get widgetClass() {}
};
