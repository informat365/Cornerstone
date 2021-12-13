import Base from '../../../Core/Base.js';
import DateHelper from '../../../Core/helper/DateHelper.js';

// Used to avoid having to create huge amounts of Date objects
const tempDate = new Date();

/**
 * @module Scheduler/view/mixin/TimelineDateMapper
 */

/**
 * Mixin that contains functionality to convert between coordinates and dates etc.
 *
 * @mixin
 */
export default Target => class TimelineDateMapper extends (Target || Base) {
    //region Coordinate <-> Date

    /**
     * Gets the date for an X or Y coordinate, either local to the view element or the page based on the 3rd argument.
     * If the coordinate is not in the currently rendered view, null will be returned unless the `allowOutOfRange`
     * parameter is passed a `true`.
     * @param {Number} coordinate The X or Y coordinate
     * @param {String} [roundingMethod] The rounding method to use
     * @param {Boolean} [local] true if the coordinate is local to the scheduler view element
     * @param {Boolean} [allowOutOfRange] By default, this returns `null` if the position is outside
     * of the time axis. Pass `true` to attempt to calculate a date outside of the time axis.
     * @returns {Date} The Date corresponding to the X or Y coordinate
     * @category Dates
     */
    getDateFromCoordinate(coordinate, roundingMethod, local = true, allowOutOfRange = false) {
        if (!local) {
            coordinate = this.currentOrientation.translateToScheduleCoordinate(coordinate);
        }

        return this.timeAxisViewModel.getDateFromPosition(coordinate, roundingMethod, allowOutOfRange);
    }

    /**
     * Gets the date for the passed X coordinate.
     * If the coordinate is not in the currently rendered view, null will be returned.
     * @param {Number} x The X coordinate
     * @param {String} roundingMethod The rounding method to use
     * @returns {Date} the Date corresponding to the x coordinate
     * @param {Boolean} [local] true if the coordinate is local to the scheduler element
     * @category Dates
     * @deprecated 3.0 Use {@link #function-getDateFromXY} if you have a coordinate pair, or
     * {@link #function-getDateFromCoordinate} if you have the correct position depending upon orientation.
     */
    getDateFromX(x, roundingMethod, local = true) {
        return this.getDateFromCoordinate(x, roundingMethod, local);
    }

    /**
     * Gets the date for an XY coordinate regardless of the orientation of the time axis.
     * @param {Array} xy The page X and Y coordinates
     * @param {String} [roundingMethod] Optional, 'floor' to floor the value or 'round' to round the value to nearest increment
     * @param {Boolean} [local] true if the coordinate is local to the scheduler element
     * @param {Boolean} [allowOutOfRange] By default, this returns `null` if the position is outside
     * of the time axis. Pass `true` to attempt to calculate a date outside of the time axis.
     * @returns {Date} the Date corresponding to the xy coordinate
     * @category Dates
     */
    getDateFromXY(xy, roundingMethod, local = true, allowOutOfRange = false) {
        return this.currentOrientation.getDateFromXY(xy, roundingMethod, local, allowOutOfRange);
    }

    /**
     * Gets the time for a DOM event such as 'mousemove' or 'click' regardless of the orientation of the time axis.
     * @param {Event} e the Event instance
     * @param {String} [roundingMethod] Optional, 'floor' to floor the value or 'round' to round the value to nearest increment
     * @param {Boolean} [allowOutOfRange] By default, this returns `null` if the position is outside
     * of the time axis. Pass `true` to attempt to calculate a date outside of the time axis.
     * @returns {Date} The date corresponding to the EventObject's position along the orientation of the time axis.
     * @category Dates
     */
    getDateFromDomEvent(e, roundingMethod, allowOutOfRange = false) {
        return this.getDateFromXY([e.x, e.y], roundingMethod, false, allowOutOfRange);
    }

    /**
     * Gets the start and end dates for an element Region
     * @param {Core.helper.util.Rectangle} rect The rectangle to map to start and end dates
     * @param {String} roundingMethod The rounding method to use
     * @param {Number} duration The duration in MS of the underlying event
     * @returns {Object} an object containing start/end properties
     */
    getStartEndDatesFromRectangle(rect, roundingMethod, duration) {
        const
            me               = this,
            { isHorizontal } = me,
            timeSpanEnd      = isHorizontal ? me.timeAxisSubGrid.width : me.timeAxisSubGrid.height,
            startPos         = isHorizontal ? rect.x : rect.top,
            endPos           = isHorizontal ? rect.right : rect.bottom;

        let start, end;

        // Element within bounds
        if (startPos >= 0 && endPos < timeSpanEnd) {
            start = me.getDateFromCoordinate(startPos, null, true);
            end = me.getDateFromCoordinate(endPos, null, true);
        }
        // Starts before, start is worked backwards from end
        else if (startPos < 0) {
            end = me.getDateFromCoordinate(endPos, roundingMethod, true);
            start = DateHelper.add(end, -duration, 'ms');
        }
        // Ends after, end is calculated from the start
        else {
            start = me.getDateFromCoordinate(startPos, roundingMethod, true);
            end = DateHelper.add(end, duration, 'ms');
        }

        return {
            start, end
        };
    }
    //endregion

    //region Date display

    /**
     * Get/set format to use when displaying dates. Usually set by specifying a view preset
     * @property {String}
     * @category Dates
     */
    get displayDateFormat() {
        return this._displayDateFormat;
    }

    set displayDateFormat(format) {
        this._displayDateFormat = format;

        // Start/EndDateColumn listens for this to change their format to match
        this.trigger('displayDateFormatChange', { format });
    }

    /**
     * Method to get a formatted display date
     * @private
     * @param {Date} date The date
     * @return {String} The formatted date
     */
    getFormattedDate(date) {
        return DateHelper.format(date, this.displayDateFormat);
    }

    /**
     * Method to get a displayed end date value, see {@link #function-getFormattedEndDate} for more info.
     * @private
     * @param {Date} endDate The date to format
     * @param {Date} startDate The start date
     * @return {Date} The date value to display
     */
    getDisplayEndDate(endDate, startDate) {
        if (
            // If time is midnight,
            endDate.getHours() === 0 && endDate.getMinutes() === 0 &&

            // and end date is greater then start date
            (!startDate || !(endDate.getYear() === startDate.getYear() && endDate.getMonth() === startDate.getMonth() && endDate.getDate() === startDate.getDate())) &&

            // and UI display format doesn't contain hour info (in this case we'll just display the exact date)
            !DateHelper.formatContainsHourInfo(this.displayDateFormat)
        ) {
            // format the date inclusively as 'the whole previous day'.
            endDate = DateHelper.add(endDate, -1, 'day');
        }

        return endDate;
    }

    /**
     * Method to get a formatted end date for a scheduled event, the grid uses the "displayDateFormat" property defined in the current view preset.
     * End dates are formatted as 'inclusive', meaning when an end date falls on midnight and the date format doesn't involve any hour/minute information,
     * 1ms will be subtracted (e.g. 2010-01-08T00:00:00 will first be modified to 2010-01-07 before being formatted).
     * @private
     * @param {Date} endDate The date to format
     * @param {Date} startDate The start date
     * @return {String} The formatted date
     */
    getFormattedEndDate(endDate, startDate) {
        return this.getFormattedDate(this.getDisplayEndDate(endDate, startDate));
    }

    //endregion

    //region Other date functions

    /**
     * Gets the x or y coordinate relative to the scheduler element, or page coordinate (based on the 'local' flag)
     * If the coordinate is not in the currently rendered view, -1 will be returned.
     * @param {Date|Number} date the date to query for (or a date as ms)
     * @param {Boolean|Object} options true to return a coordinate local to the scheduler view element (defaults to true),
     * also accepts a config object like { local : true }.
     * @returns {Number} the x or y position representing the date on the time axis
     * @category Dates
     */
    getCoordinateFromDate(date, options = true) {
        const me = this,
            { timeAxisViewModel } = me,
            { isContinuous, startMS, endMS, startDate, unit } = me.timeAxis,
            dateMS = date.valueOf();

        // Avoiding to break the API while allowing passing options through to getPositionFromDate()
        if (options === true) {
            options = {
                local : true
            };
        }
        else if (!options) {
            options = {
                local : false
            };
        }
        else if (!('local' in options)) {
            options.local = true;
        }

        let pos;

        // TODO for 2.0 try to normalize and just use dates as input for this method,
        // then this if-statement would not be needed
        if (!(date instanceof Date)) {
            tempDate.setTime(date);
            date = tempDate;
        }

        // Shortcut for continuous time axis that is using a unit that can be reliably translated to days (or smaller)
        if (isContinuous && date.getTimezoneOffset() === startDate.getTimezoneOffset() && DateHelper.getUnitToBaseUnitRatio(unit, 'day') !== -1) {

            if (dateMS < startMS || dateMS > endMS) {
                return -1;
            }
            pos = Math.round((dateMS - startMS) / (endMS - startMS) * timeAxisViewModel.totalSize);
        }
        // Non-continuous or using for example months (vary in length)
        else {
            pos = timeAxisViewModel.getPositionFromDate(date, options);
        }

        if (!options.local) {
            pos = me.currentOrientation.translateToPageCoordinate(pos);
        }

        return pos;
    }

    /**
     * Returns the distance in pixels for the time span in the view.
     * @param {Date} startDate The start date of the span
     * @param {Date} endDate The end date of the span
     * @return {Number} The distance in pixels
     * @category Dates
     */
    getTimeSpanDistance(startDate, endDate) {
        return this.timeAxisViewModel.getDistanceBetweenDates(startDate, endDate);
    }

    /**
     * Returns the center date of the currently visible timespan of scheduler.
     *
     * @return {Date} date Center date for the viewport.
     * @readonly
     * @category Dates
     */
    get viewportCenterDate() {
        const me       = this,
            timeAxis = me.timeAxis,
            subGrid  = me.timeAxisSubGrid,
            scroller = subGrid.scrollable,
            centerX  = scroller.x + subGrid.width / 2,
            centerY  = scroller.y + subGrid.height / 2;

        if (timeAxis.isContinuous) {
            // Calculate center pixel in the viewport.
            // Then Calculate how far through the axis range that is.
            const scrollCenter = me.isHorizontal ? (centerX / scroller.scrollWidth) : (centerY / scroller.scrollHeight),
                centerMilli = timeAxis.startMS + (timeAxis.endMS - timeAxis.startMS) * scrollCenter;

            return new Date(centerMilli);
        }
        else {
            const xy = me.isHorizontal ? [centerX, 0] : [0, centerY];

            return me.getDateFromXY(xy, null, true);
        }
    }

    get viewportCenterDateCached() {
        return this.cachedCenterDate || (this.cachedCenterDate = this.viewportCenterDate);
    }

    //endregion

    //region TimeAxis getters/setters

    /**
     * Gets/sets the current time resolution object, which contains a unit identifier and an increment count { unit, increment}
     * @property {Object}
     * @category Dates
     */
    get timeResolution() {
        return this.timeAxis.resolution;
    }

    set timeResolution(increment) {
        this.timeAxis.resolution = {
            increment,
            unit : this.timeAxis.resolution.unit
        };
    }

    //endregion

    //region Snap

    /**
     * Controls whether the scheduler should snap to the resolution when interacting with it
     * @property {Boolean}
     * @category Dates
     */
    set snap(enabled) {
        // timeAxisViewModel is not created yet during configuration
        if (!this.isConfiguring) {
            this.timeAxisViewModel.snap = enabled;
        }
        else {
            this._snap = enabled;
        }
    }

    get snap() {
        // timeAxisViewModel is not created yet during configuration
        if (this.isConfiguring) {
            return this._snap;
        }
        return this.timeAxisViewModel.snap;
    }

    //endregion

    onSchedulerHorizontalScroll({ subGrid, scrollLeft }) {
        // Invalidate cached center date unless we are scrolling to center on it.
        if (!this.scrollingToCenter) {
            this.cachedCenterDate = null;
        }
    }

    // This does not need a className on Widgets.
    // Each *Class* which doesn't need 'b-' + constructor.name.toLowerCase() automatically adding
    // to the Widget it's mixed in to should implement thus.
    get widgetClass() {}
};
