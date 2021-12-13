import DH from '../../../Core/helper/DateHelper.js';
import Events from '../../../Core/mixin/Events.js';
import PresetManager from '../../preset/PresetManager.js';
import ViewPreset from '../../preset/ViewPreset.js';
import '../../data/TimeAxis.js';

/**
 * @module Scheduler/view/model/TimeAxisViewModel
 */

/**
 * This class is an internal view model class, describing the visual representation of a {@link Scheduler.data.TimeAxis}.
 * The config for the header rows is described in the {@link Scheduler.preset.ViewPreset#field-headers headers}.
 * To calculate the size of each cell in the time axis, this class requires:
 *
 * - availableSpace  - The total width or height available for the rendering
 * - tickSize       - The fixed width or height of each cell in the lowest header row. This value is normally read from the
 * {@link Scheduler.preset.ViewPreset viewPreset} but this can also be updated programmatically using the {@link #property-tickSize} setter
 *
 * Normally you should not interact with this class directly.
 *
 * @extends Core/mixin/Events
 */
export default class TimeAxisViewModel extends Events() {
    //region Default config

    // TODO: replace _availableSpace etc with availableSpace in config and make setters work during config phase

    static get defaultConfig() {
        return {
            /**
             * The time axis providing the underlying data to be visualized
             * @config {Scheduler.data.TimeAxis}
             * @internal
             */
            timeAxis : null,

            /**
             * The available width/height, this is normally not known by the consuming UI component using this model
             * class until it has been fully rendered. The consumer of this model should set
             * {@link #property-availableSpace} when its width has changed.
             * @config {Number}
             * @internal
             */
            availableSpace : null,

            /**
             * The "tick width" for horizontal mode or "tick height" for vertical mode, to use for the cells in the
             * bottom most header row.
             * This value is normally read from the {@link Scheduler.preset.ViewPreset viewPreset}
             * @config {Number}
             * @default
             * @internal
             */
            tickSize : 100,

            /**
             * true if there is a requirement to be able to snap events to a certain view resolution.
             * This has implications of the {@link #config-tickSize} that can be used, since all widths must be in even pixels.
             * @config {Boolean}
             * @default
             * @internal
             */
            snap : false,

            /**
             * true if cells in the bottom-most row should be fitted to the {@link #property-availableSpace available space}.
             * @config {Boolean}
             * @default
             * @internal
             */
            forceFit : false,

            headers : null,

            calendar : null,

            mode : 'horizontal', // or 'vertical'

            //used for Exporting. Make sure the tick columns are not recalculated when resizing.
            suppressFit : false,

            // cache of the config currently used.
            columnConfig : [],

            // the view preset name to apply initially
            viewPreset : null,

            // The default header level to draw column lines for
            columnLinesFor : null,

            // To give a link to the eventStore inside the header renderer if provided
            eventStore : null,

            originalTickSize : null,

            headersDatesCache : []
        };
    }

    //endregion

    //region Init & destroy

    construct(config) {
        const me = this;

        // getSingleUnitInPixels results are memoized because of frequent calls during rendering.
        me.unitToPixelsCache = {};

        super.construct(config);

        let viewPreset = me.timeAxis.viewPreset || me.viewPreset;

        if (viewPreset) {
            if (viewPreset instanceof ViewPreset) {
                me.consumeViewPreset(viewPreset);
            }
            else {
                let preset = PresetManager.getPreset(viewPreset);
                preset && me.consumeViewPreset(preset);
            }
        }

        // When time axis is changed, reconfigure the model
        me.timeAxis.on('reconfigure', me.onTimeAxisReconfigure, me);

        me.configured = true;
    }

    doDestroy() {
        this.timeAxis.un('reconfigure', this.onTimeAxisReconfigure, this);
    }

    /**
     * Returns an array representing the headers of the current timeAxis. Each element is an array representing the cells for that level in the header.
     * @return {Object[]} An array of headers, each element being an array representing each cell (with start date and end date) in the timeline representation.
     * @internal
     */
    get columnConfig() {
        return this._columnConfig;
    }

    set columnConfig(config) {
        this._columnConfig = config;
    }

    get isTimeAxisViewModel() {
        return true;
    }

    //endregion

    //region Events

    /**
     * Fires after the model has been updated.
     * @event update
     * @param {Scheduler.view.model.TimeAxisViewModel} source The model instance
     */

    /**
     * Fires after the model has been reconfigured.
     * @event reconfigure
     * @param {Scheduler.view.model.TimeAxisViewModel} source The model instance
     */

    //endregion

    //region Mode

    /**
     * Using horizontal mode?
     * @returns {Boolean}
     * @readonly
     * @internal
     */
    get isHorizontal() {
        return this.mode !== 'vertical';
    }

    /**
     * Using vertical mode?
     * @returns {Boolean}
     * @readonly
     * @internal
     */
    get isVertical() {
        return this.mode === 'vertical';
    }

    /**
     * Gets/sets the forceFit value for the model. Setting it will cause it to update its contents and fire the
     * {@link #event-update} event.
     * @property {Boolean}
     * @internal
     */
    set forceFit(value) {
        if (value !== this._forceFit) {
            this._forceFit = value;
            this.update();
        }
    }

    //endregion

    //region Reconfigure & update

    reconfigure(config) {
        // clear the cached headers
        const me = this;

        me._headers = null;

        // Ensure correct ordering
        me.setConfig(config);

        me.trigger('reconfigure');
    }

    onTimeAxisReconfigure({ source : timeAxis, suppressRefresh }) {
        if (this.viewPreset !== timeAxis.viewPreset) {
            this.consumeViewPreset(timeAxis.viewPreset);
        }
        if (!suppressRefresh) {
            this.update();
        }
    }

    /**
     * Updates the view model current timeAxis configuration and available space.
     * @param {Number} [availableSpace] The available space for the rendering of the axis (used in forceFit mode)
     * @param {Boolean} [silent] Pass `true` to suppress the firing of the `update` event.
     * @param {Boolean} [forceUpdate] Pass `true` to fire the `update` event even if the size has not changed.
     * @internal
     */
    update(availableSpace, silent = false, forceUpdate = false) {
        const
            me           = this,
            { timeAxis, headers } = me;

        // We're in configuration, or no change, quit
        if (me.isConfiguring || me._availableSpace === availableSpace) {
            if (forceUpdate) {
                me.trigger('update');
            }
            return;
        }

        me._availableSpace = Math.max(availableSpace || me.availableSpace || 0, 0);

        if (typeof me.availableSpace !== 'number') {
            throw new Error('Invalid available space provided to TimeAxisModel');
        }

        // Despite available space is 0, we still should generate column config in order for scheduler to render
        if (me.availableSpace <= 0) {
            console.warn('No available space provided for time axis. Make sure you have set width for horizontal mode and height for vertical');
        }

        me.columnConfig = [];

        // The "column width" is considered to be the width of each tick in the lowest header row and this width
        // has to be same for all cells in the lowest row.
        const tickSize = me._tickSize = me.calculateTickSize(me.originalTickSize);

        if (typeof tickSize !== 'number' || tickSize <= 0) {
            throw new Error('Invalid timeAxis tick size');
        }

        // getSingleUnitInPixels results are memoized because of frequent calls during rendering.
        me.unitToPixelsCache = {};

        // totalSize is cached because of frequent calls which calculate it.
        me._totalSize = null;

        // Generate the underlying date ranges for each header row, which will provide input to the cell rendering
        for (let pos = 0, { length } = headers; pos < length; pos++) {
            const header = headers[pos];

            if (header.cellGenerator) {
                me.columnConfig[pos] = header.cellGenerator.call(me, timeAxis.startDate, timeAxis.endDate);
            }
            else {
                me.columnConfig[pos] = me.createHeaderRow(pos, header);
            }
        }

        if (!silent) {
            me.trigger('update');
        }
    }

    //endregion

    //region CalendarColumn

    // /**
    //  * Will update columns start/end dates to perform date/coordinate lookups
    //  * @param {Object[]} columns Array of column configs including start/end dates
    //  * @private
    //  */
    // updateCalendarColumnDates(columns) {
    //     this.calendarColumnDates = [];
    //
    //     for (let i = 0; i < columns.length; i++) {
    //         let obj = columns[i];
    //         this.calendarColumnDates.push([obj.start, obj.end]);
    //     }
    // }

    // // TODO: make getter
    // /**
    //  * Returns current column start/end dates array
    //  * @returns {Date[]}
    //  * @private
    //  */
    // getCalendarColumnDates() {
    //     return this.calendarColumnDates;
    // }

    //endregion

    //region Date / position mapping

    /**
     * Returns the distance in pixels for a timespan with the given start and end date.
     * @param {Date} start start date
     * @param {Date} end end date
     * @return {Number} The length of the time span
     * @category Date mapping
     */
    getDistanceBetweenDates(start, end) {
        return Math.round(this.getPositionFromDate(end) - this.getPositionFromDate(start));
    }

    /**
     * Returns the distance in pixels for a time span
     * @param {Number} durationMS Time span duration in ms
     * @return {Number} The length of the time span
     * @category Date mapping
     */
    getDistanceForDuration(durationMs) {
        let me      = this,
            tick    = this.timeAxis.getAt(0),
            pxPerMs = me.tickSize / (tick.endDate - tick.startDate);

        return pxPerMs * durationMs;
    }

    /**
     * Gets the position of a date on the projected time axis or -1 if the date is not in the timeAxis.
     * @param {Date} date the date to query for.
     * @returns {Number} the coordinate representing the date
     * @category Date mapping
     */
    getPositionFromDate(date, options = {}) {
        const me = this,
            tick = me.getScaledTick(date, options);

        if (tick === -1) {
            return -1;
        }

        return Math.round(me.tickSize * (tick - me.timeAxis.visibleTickStart));
    }

    // Translates a tick along the time axis to facilitate scaling events when excluding certain days or hours
    getScaledTick(date, { respectExclusion, snapToNextIncluded, isEnd, min, max }) {
        const
            { timeAxis } = this,
            { include, unit, weekStartDay }  = timeAxis;

        let tick = timeAxis.getTickFromDate(date);

        if (tick !== -1 && respectExclusion && include) {
            let tickChanged = false;

            // Stretch if we are using a larger unit than 'hour', except if it is 'day'. If so, it is already handled
            // by a cheaper reconfiguration of the ticks in `generateTicks`
            if (include.hour && DH.compareUnits(unit, 'hour') > 0 && unit !== 'day') {
                const { from, to, lengthFactor, center } = include.hour,
                    // Original hours
                    originalHours = date.getHours(),
                    // Crop to included hours
                    croppedHours = Math.min(Math.max(originalHours, from), to);

                // If we are not asked to snap (when other part of span is not included) any cropped away hour
                // should be considered excluded
                if (!snapToNextIncluded && croppedHours !== originalHours) {
                    return -1;
                }

                const
                    // Should scale hour and smaller units (seconds will hardly affect visible result...)
                    fractionalHours = croppedHours + date.getMinutes() / 60,
                    // Number of hours from the center    |xxxx|123c----|xxx|
                    hoursFromCenter = center - fractionalHours,
                    // Step from center to stretch event  |x|112233c----|xxx|
                    newHours = center - hoursFromCenter * lengthFactor;

                // Adding instead of setting to get a clone of the date, to not affect the original
                date = DH.add(date, newHours - originalHours, 'h');

                tickChanged = true;
            }

            if (include.day && DH.compareUnits(unit, 'day') > 0) {
                const { from, to, lengthFactor, center } = include.day;

                //region Crop
                let checkDay = date.getDay();

                // End date is exclusive, check the day before if at 00:00
                if (isEnd && date.getHours() === 0 && date.getMinutes() === 0 && date.getSeconds() === 0 && date.getMilliseconds() === 0) {
                    if (--checkDay < 0) {
                        checkDay = 6;
                    }
                }
                let addDays = 0;

                if (checkDay < from || checkDay >= to) {
                    // If end date is in view but start date is excluded, snap to next included day
                    if (snapToNextIncluded) {

                        // Step back to "to-1" (not inclusive) for end date
                        if (isEnd) {
                            addDays = (to - checkDay - 8) % 7;
                        }
                        // Step forward to "from" for start date
                        else {
                            addDays = (from - checkDay + 7) % 7;
                        }

                        date = DH.add(date, addDays, 'd');
                        date = DH.startOf(date, 'd', false);

                        // Keep end after start and vice versa
                        if (
                            (max && date.getTime() >= max) ||
                            (min && date.getTime() <= min)
                        ) {
                            return -1;
                        }
                    }
                    else {
                        // day excluded at not snapping to next
                        return -1;
                    }
                }
                //endregion

                const
                    // Center to stretch around, for some reason pre-calculated cannot be used for sundays :)
                    fixedCenter = date.getDay() === 0 ? 0 : center,
                    // Should scale day and smaller units (minutes will hardly affect visible result...)
                    fractionalDay = date.getDay() + date.getHours() / 24, //+ dateClone.getMinutes() / (24 * 1440),
                    // Number of days from the calculated center
                    daysFromCenter = fixedCenter - fractionalDay,
                    // Step from center to stretch event
                    newDay = fixedCenter - daysFromCenter * lengthFactor;

                // Adding instead of setting to get a clone of the date, to not affect the original
                date = DH.add(date, newDay - fractionalDay + weekStartDay, 'd');

                tickChanged = true;
            }

            // Now the date might start somewhere else (fraction of ticks)
            if (tickChanged) {
                // When stretching date might end up outside of time axis, making it invalid to use. Clip it to time axis
                // to circumvent this
                date = DH.constrain(date, timeAxis.startDate, timeAxis.endDate);

                // Get a new tick based on the "scaled" date
                tick = timeAxis.getTickFromDate(date);
            }
        }

        return tick;
    }

    /**
     * Gets the date for a position on the time axis
     * @param {Number} position The page X or Y coordinate
     * @param {String} [roundingMethod] The rounding method to use
     * @param {Boolean} [allowOutOfRange=false] By default, this returns `null` if the position is outside
     * of the time axis. Pass `true` to attempt to calculate a date outside of the time axis.
     * @returns {Date} the Date corresponding to the xy coordinate
     * @category Date mapping
     */
    getDateFromPosition(position, roundingMethod, allowOutOfRange = false) {
        const me       = this,
            timeAxis = me.timeAxis,
            tick     = me.getScaledPosition(position) / me.tickSize + timeAxis.visibleTickStart;

        if (tick < 0 || tick > timeAxis.count) {
            if (allowOutOfRange) {
                // Subtract the correct number of tick units from the start date
                if (tick < 0) {
                    return DH.add(timeAxis.startDate, tick, timeAxis.unit);
                }
                // Add the correct number of tick units to the end date
                return DH.add(timeAxis.endDate, tick - timeAxis.count, timeAxis.unit);
            }
            return null;
        }

        return timeAxis.getDateFromTick(tick, roundingMethod);
    }

    // Translates a position along the time axis to facilitate scaling events when excluding certain days or hours
    getScaledPosition(position) {
        const
            { include, unit, weekStartDay }  = this.timeAxis;

        // Calculations are

        if (include) {
            const dayWidth = this.getSingleUnitInPixels('day');

            // Have to calculate day before hour to get end result correct
            if (include.day && DH.compareUnits(unit, 'day') > 0) {
                const { from, lengthFactor } = include.day,
                    // Scaling happens within a week, determine position within it
                    positionInWeek = position % (dayWidth * 7),
                    // Store were the week starts to be able to re-add it after scale
                    weekStartPosition = position - positionInWeek;
                // Scale position using calculated length per day factor, adding the width of excluded days
                position = positionInWeek / lengthFactor + (from - weekStartDay) * dayWidth + weekStartPosition;
            }

            // Hours are not taken into account when viewing days, since the day ticks are reconfigured in
            // `generateTicks` instead
            if (include.hour && DH.compareUnits(unit, 'hour') > 0 && unit !== 'day') {
                const { from, lengthFactorExcl } = include.hour,
                    hourWidth = this.getSingleUnitInPixels('hour'),
                    // Scaling happens within a day, determine position within it
                    positionInDay = position % dayWidth,
                    // Store were the day starts to be able to re-add it after scale
                    dayStartPosition = position - positionInDay;
                // Scale position using calculated length per day factor, adding the width of excluded hours
                position = positionInDay / lengthFactorExcl + from * hourWidth + dayStartPosition;
            }
        }

        return position;
    }

    /**
     * Returns the amount of pixels for a single unit
     * @internal
     * @return {Number} The unit in pixel
     */
    getSingleUnitInPixels(unit) {
        const me = this;

        return me.unitToPixelsCache[unit] || (me.unitToPixelsCache[unit] = DH.getUnitToBaseUnitRatio(me.timeAxis.unit, unit, true) * me.tickSize / me.timeAxis.increment);
    }

    /**
     * [Experimental] Returns the pixel increment for the current view resolution.
     * @internal
     * @return {Number} The increment
     */
    get snapPixelAmount() {
        if (this.snap) {
            const { resolution } = this.timeAxis;
            return (resolution.increment || 1) * this.getSingleUnitInPixels(resolution.unit);
        }
        else {
            return 1;
        }
    }

    //endregion

    //region Sizes

    /**
     * Get/set the current time column size (the width or height of a cell in the bottom-most time axis header row,
     * depending on mode)
     * @internal
     * @property {Number}
     */
    get tickSize() {
        return this._tickSize;
    }

    set tickSize(size) {
        this.setTickSize(size, false);
    }

    setTickSize(size, suppressEvent) {
        this._tickSize = this.originalTickSize = size;

        this.update(undefined, suppressEvent);
    }

    get timeResolution() {
        return this.timeAxis.resolution;
    }

    // Calculates the time column width/height based on the value defined viewPreset "tickWidth/tickHeight". It also
    // checks for the forceFit view option and the snap, both of which impose constraints on the time column width
    // configuration.
    calculateTickSize(proposedSize) {
        const
            me = this,
            { forceFit, timeAxis } = me,
            timelineUnit = timeAxis.unit;

        let size   = 0,
            ratio  = 1; //Number.MAX_VALUE;

        if (me.snap) {
            const resolution = timeAxis.resolution;
            ratio = DH.getUnitToBaseUnitRatio(timelineUnit, resolution.unit) * resolution.increment;
        }
        // else {
        //     const measuringUnit = DH.getMeasuringUnit(timelineUnit);
        //     Math.min(ratio, DH.getUnitToBaseUnitRatio(timelineUnit, measuringUnit));
        // }

        if (!me.suppressFit) {
            const
                ticks        = timeAxis.visibleTickTimeSpan,
                // TODO: change this to always floor if we want to prevent scheduler scroll when not really needed
                fittingSize = Math[forceFit ? 'floor' : 'round'](me.availableSpace / ticks);

            size = (forceFit || proposedSize < fittingSize) ? fittingSize : proposedSize;

            if (ratio > 0 && (!forceFit || ratio < 1)) {
                // For touch, make sure we always fill the available space with forceFit (to not show edges of side-time-pickers)
                const method = (forceFit ? 'floor' : 'round');
                size = Math.round(Math.max(1, Math[method](ratio * size)) / ratio);
            }
        }
        else {
            size = proposedSize;
        }

        return size;
    }

    /**
     * Returns the total width/height of the time axis representation, depending on mode.
     * @return {Number} The width or height
     * @internal
     * @readonly
     */
    get totalSize() {
        return this._totalSize || (this._totalSize = Math.round(this.tickSize * this.timeAxis.visibleTickTimeSpan));
    }

    /**
     * Get/set the available space for the time axis representation. If size changes it will cause it to update its
     * contents and fire the {@link #event-update} event.
     * @internal
     * @property {Number}
     */
    get availableSpace() {
        return this._availableSpace;
    }

    set availableSpace(space) {
        const me = this;
        // We should only need to repaint fully if the tick width has changed (which will happen if forceFit is set, or if the full size of the time axis doesn't
        // occupy the available space - and gets stretched
        me._availableSpace = Math.max(0, space);

        if (me._availableSpace > 0) {
            const newTickSize = me.calculateTickSize(me.originalTickSize);

            if (newTickSize > 0 && newTickSize !== me.tickSize) {
                me.update();
            }
        }
    }

    //endregion

    //region Fitting & snapping

    /**
     * Returns start dates for ticks at the specified level in format { date, isMajor }.
     * @param {Number} level Level in headers array, `0` meaning the topmost...
     * @param {Boolean} useLowestHeader Use lowest level
     * @param getEnd
     * @returns {Array}
     * @internal
     */
    getDates(level = this.columnLinesFor, useLowestHeader = false, getEnd = false) {
        const me            = this,
            ticks         = [],
            linesForLevel = useLowestHeader ? me.lowestHeader : level,
            majorLevel    = me.majorHeaderLevel,
            levelUnit     = me.headers && me.headers[level].unit,
            majorUnit     = majorLevel != null && me.headers && me.headers[majorLevel].unit,
            validMajor    = majorLevel != null && DH.doesUnitsAlign(majorUnit, levelUnit),
            hasGenerator  = !!(me.headers && me.headers[linesForLevel].cellGenerator);

        if (hasGenerator) {
            const cells = me.columnConfig[linesForLevel];

            for (let i = 1, l = cells.length; i < l; i++) {
                ticks.push({ date : cells[i].startDate });
            }
        }
        else {
            me.forEachInterval(linesForLevel, (start, end) => {
                ticks.push({
                    date    : getEnd ? end : start,
                    // do not want to consider tick to be major tick, hence the check for majorHeaderLevel
                    isMajor : majorLevel !== level && validMajor && me.isMajorTick(getEnd ? end : start)
                });
            });
        }

        return ticks;
    }

    get forceFit() {
        return this._forceFit;
    }

    /**
     * This function fits the time columns into the available space in the time axis column.
     * @param {Boolean} suppressEvent `true` to skip firing the 'update' event.
     * @internal
     */
    fitToAvailableSpace(suppressEvent) {
        const proposedSize = Math.floor(this.availableSpace / this.timeAxis.visibleTickTimeSpan);
        this.setTickSize(proposedSize, suppressEvent);
    }

    get snap() {
        return this._snap;
    }

    /**
     * Gets/sets the snap value for the model. Setting it will cause it to update its contents and fire the
     * {@link #event-update} event.
     * @property {Boolean}
     * @internal
     */
    set snap(value) {
        if (value !== this._snap) {
            this._snap = value;
            if (this.configured) this.update();
        }
    }

    //endregion

    //region Headers

    // private
    createHeaderRow(position, headerRowConfig) {
        const me    = this,
            cells = [],
            align = headerRowConfig.align,
            today = DH.clearTime(new Date()),
            headerCellCls = headerRowConfig.headerCellCls || '';

        let size = 0;

        me.headersDatesCache[position] = {};

        me.forEachInterval(position, (start, end, i) => {
            let value    = DH.format(start, headerRowConfig.dateFormat),
                cellData = {
                    align,
                    start,
                    end,
                    value,
                    headerCellCls,
                    width : this.getDistanceBetweenDates(start, end),
                    index : i
                };

            if (cellData.width === 0) {
                return;
            }

            // Vertical mode uses absolute positioning for header cells
            cellData.coord = size;
            size += cellData.width;

            me.headersDatesCache[position][start.getTime()] = 1;

            if (headerRowConfig.renderer) {
                value = headerRowConfig.renderer.call(headerRowConfig.thisObj || me, start, end, cellData, i, me.eventStore);

                cellData.value = value == null ? '' : value;
            }

            // To be able to style individual day cells, weekends or other important days
            if (headerRowConfig.unit === 'day' && (!headerRowConfig.increment || headerRowConfig.increment === 1)) {
                cellData.headerCellCls += ' b-sch-dayheadercell-' + start.getDay();

                if (me.calendar && me.calendar.isWeekend(start)) {
                    cellData.headerCellCls += ' b-sch-dayheadercell-nonworking';
                }

                if (DH.clearTime(start, true) - today === 0) {
                    cellData.headerCellCls += ' b-sch-dayheadercell-today';
                }
            }

            cells.push(cellData);
        });

        return cells;
    }

    get mainHeader() {
        return ('mainHeaderLevel' in this) ? this.headers[this.mainHeaderLevel] : this.bottomHeader;
    }

    get bottomHeader() {
        return this.headers[this.headers.length - 1];
    }

    get lowestHeader() {
        return this.headers.length - 1;
    }

    /**
     * This method is meant to return the level of the header which 2nd lowest.
     * It is used for {@link #function-isMajorTick} method
     * @return {String}
     * @private
     */
    get majorHeaderLevel() {
        const { headers } = this;

        if (headers) {
            return Math.max(headers.length - 2, 0);
        }

        return null;
    }

    //endregion

    //region Ticks

    /**
     * For vertical view (and column lines plugin) we sometimes want to know if current tick starts along with the
     * upper header level.
     * @param {Date} date
     * @return {Boolean}
     * @private
     */
    isMajorTick(date) {
        const nextLevel = this.majorHeaderLevel;
        // if forceFit is used headersDatesCache won´t have been generated yet on the first call here,
        // since no size is set yet
        return nextLevel != null && this.headersDatesCache[nextLevel] && this.headersDatesCache[nextLevel][date.getTime()] || false;
    }

    /**
     * Calls the supplied iterator function once per interval. The function will be called with three parameters, start date and end date and an index.
     * Return false to break the iteration.
     * @param {Number} position The index of the header in the headers array.
     * @param {Function} iteratorFn The function to call, will be called with start date, end date and "tick index"
     * @param {Object} [thisObj] `this` reference for the function
     * @internal
     */
    forEachInterval(position, iteratorFn, thisObj = this) {
        const { headers } = this;

        if (headers) {
            // This is the lowest header row, which should be fed the data in the tickStore (or a row above using same unit)
            if (position === headers.length - 1) {
                this.timeAxis.forEach((r, index) =>
                    iteratorFn.call(thisObj, r.startDate, r.endDate, index)
                );
            }
            // All other rows
            else {
                const header = headers[position];

                this.timeAxis.forEachAuxInterval(header.unit, header.increment, iteratorFn, thisObj);
            }
        }
    }

    /**
     * Calls the supplied iterator function once per interval. The function will be called with three parameters, start date and end date and an index.
     * Return false to break the iteration.
     * @internal
     * @param {Function} iteratorFn The function to call
     * @param {Object} [thisObj] `this` reference for the function
     */
    forEachMainInterval(iteratorFn, thisObj) {
        this.forEachInterval(this.mainHeaderLevel, iteratorFn, thisObj);
    }

    //endregion

    //region ViewPreset

    consumeViewPreset(preset) {
        const me = this;

        // clear the cached headers
        me._headers = null;

        me._thisIsAUsedExpression(me.tickSize);

        // Since we are bypassing the tickSize setter below, ensure that
        // the config initial setter has been removed by referencing the property.
        // We only do this to avoid multiple updates from this. TODO: Fix this issue,
        // and do not bypass setters.
        me.viewPreset = preset;

        Object.assign(me, {
            headers         : preset.headers,
            columnLinesFor  : preset.columnLinesFor,
            mainHeaderLevel : preset.mainHeaderLevel,
            _tickSize       : me.isHorizontal ? preset.tickWidth : preset.tickHeight
        });

        me.originalTickSize = me.tickSize;
    }

    //endregion

    // //region Calendar
    //
    // setCalendar(cal) {
    //     if (cal !== this.calendar) {
    //         this.calendar = cal;
    //
    //         if (cal) {
    //             this.update();
    //         }
    //     }
    // }
    //
    // //endregion
}
