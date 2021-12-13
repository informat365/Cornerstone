import Base from '../../../Core/Base.js';
import DateHelper from '../../../Core/helper/DateHelper.js';
import EventHelper from '../../../Core/helper/EventHelper.js';

/**
 * @module Scheduler/view/mixin/TimelineZoomable
 */

/**
 * Mixin providing "zooming" functionality.
 *
 * The zoom levels are stored as instances of {@link Scheduler.preset.ViewPreset ViewPreset}s, and are
 * cached centrally in the {@link Scheduler.preset.PresetManager PresetManager}.
 *
 * The default presets are loaded into the {@link Scheduler.view.mixin.TimelineViewPresets#config-presets}
 * store upon Schedukler instantiation. Preset selection is covered in the
 * {@link Scheduler.view.mixin.TimelineViewPresets TimelineViewPresets} mixin.
 *
 * @mixin
 */
export default Target => class TimelineZoomable extends (Target || Base) {
    static get defaultConfig() {
        return {
            /**
             * If true, you can zoom in and out on the the time axis using CTRL-key + mouse wheel.
             * @config {Boolean}
             * @default
             * @category Zoom
             */
            zoomOnMouseWheel : true,

            /**
             * True to zoom to time span when double clicking a time axis cell.
             * @config {Boolean}
             * @default
             * @category Zoom
             */
            zoomOnTimeAxisDoubleClick : true,

            preventScrollZoom : null,

            /**
             * Minimal zoom level to which {@link #function-zoomOut} will work
             * @config {Number}
             * @category Zoom
             */
            minZoomLevel : null,

            /**
             * Maximal zoom level to which {@link #function-zoomIn} will work
             * @config {Number}
             * @category Zoom
             */
            maxZoomLevel : null,

            /**
             * Integer number indicating the size of timespan during zooming. When zooming, the timespan is adjusted to make the scrolling area `visibleZoomFactor` times
             * wider than the timeline area itself. Used in {@link #function-zoomToSpan} and {@link #function-zoomToLevel} functions.
             * @config {Number}
             * @default
             * @category Zoom
             */
            visibleZoomFactor : 5,

            /**
             * Whether the originally rendered timespan should be preserved while zooming. By default it is set to `false`,
             * meaning the timeline panel will adjust the currently rendered timespan to limit the amount of HTML content to render. When setting this option
             * to `true`, be careful not to allow to zoom a big timespan in seconds resolution for example. That will cause **a lot** of HTML content
             * to be rendered and affect performance. You can use {@link #config-minZoomLevel} and {@link #config-maxZoomLevel} config options for that.
             * @config {Boolean}
             * @default
             * @category Zoom
             */
            zoomKeepsOriginalTimespan : null
        };
    }

    construct(config) {
        const me = this;

        super.construct(config);

        if (me.zoomOnMouseWheel) {
            EventHelper.on({
                element : me.timeAxisSubGridElement,
                wheel   : 'onWheel',
                thisObj : me,
                capture : true,
                passive : false
            });
        }

        if (me.zoomOnTimeAxisDoubleClick) {
            me.on('timeaxisheaderdblclick', ({ startDate, endDate }) => {
                if (!me.isVertical) {
                    me.zoomToSpan({
                        startDate,
                        endDate
                    });
                }
            });
        }
    }

    get maxZoomLevel() {
        return this._maxZoomLevel || (this.presets.count - 1);
    }

    /**
     * Get/set the {@link #config-maxZoomLevel} value
     * @property {Number}
     * @category Zoom
     */
    set maxZoomLevel(level) {
        if (typeof level !== 'number') {
            level = this.presets.count - 1;
        }

        if (level < 0 || level >= this.presets.count) {
            throw new Error('Invalid range for `setMinZoomLevel`');
        }

        this._maxZoomLevel = level;
    }

    get minZoomLevel() {
        return this._minZoomLevel || 0;
    }

    /**
     * Sets the {@link #config-minZoomLevel} value
     * @property {Number}
     * @category Zoom
     */
    set minZoomLevel(level) {
        if (typeof level !== 'number') {
            level = 0;
        }

        if (level < 0 || level >= this.presets.count) {
            throw new Error('Invalid range for `minZoomLevel`');
        }

        this._minZoomLevel = level;
    }

    /**
     * Get/set current zoom level. Since zoom can happen to a preset or to a timespan,
     * getter may return float number which is intended to be close to index of one of the existing {@link Scheduler.view.mixin.TimelineViewPresets#config-presets zoom levels}.
     * @property {Number}
     * @category Zoom
     */
    get zoomLevel() {
        return this.presets.indexOf(this.viewPreset);
    }

    // noinspection JSAnnotator
    set zoomLevel(level) {
        this.zoomToLevel(level);
    }

    /*
     * @private
     * Returns number of milliseconds per pixel.
     * @param {Object} level Element from array of {@link Scheduler.view.mixin.TimelineViewPresets#config-presets}.
     * @param {Boolean} ignoreActualWidth If true, then density will be calculated using default zoom level settings.
     * Otherwise density will be calculated for actual tick width.
     * @return {Number} Return number of milliseconds per pixel.
     */
    getMilliSecondsPerPixelForZoomLevel(preset, ignoreActualWidth) {
        const
            { bottomHeader } = preset,
            width            = this.isHorizontal ? preset.tickWidth : preset.tickHeight;

        // trying to convert the unit + increment to a number of milliseconds
        // this number is not fixed (month can be 28, 30 or 31 day), but at least this conversion
        // will be consistent (should be no DST changes at year 1)
        return Math.round(
            (DateHelper.add(new Date(1, 0, 1), bottomHeader.increment || 1, bottomHeader.unit) - new Date(1, 0, 1)) /
            // `actualWidth` is a column width after view adjustments applied to it (see `calculateTickWidth`)
            // we use it if available to return the precise index value from `getCurrentZoomLevelIndex`
            (ignoreActualWidth ? width : preset.actualWidth || width)
        );
    }

    /**
     * Zooms to passed view preset, saving center date. Method accepts config object as a first argument, which can be
     * reduced to primitive type (string,number) when no additional options required. e.g.:
     * ```
     * // zooming to preset
     * scheduler.zoomTo({ preset : 'hourAndDay' })
     * // shorthand
     * scheduler.zoomTo('hourAndDay')
     *
     * // zooming to level
     * scheduler.zoomTo({ level : 0 })
     * // shorthand
     * scheduler.zoomTo(0)
     * ```
     *
     * It is also possible to zoom to a time span by omitting `preset` and `level` configs, in which case scheduler sets
     * the time frame to a specified range and applies zoom level which allows to fit all columns to this range. The
     * given time span will be centered in the scheduling view (unless `centerDate` config provided). In the same time,
     * the start/end date of the whole time axis will be extended to allow scrolling for user.
     * ```
     * // zooming to time span
     * scheduler.zoomTo({ startDate : new Date(..), endDate : new Date(...) })
     *
     * ```
     *
     * @param {Object|String|Number} config Config object, preset name or zoom level index.
     * @param {String} config.preset Preset name to zoom to. Ignores level config in this case
     * @param {Number} config.level Zoom level to zoom to. Is ignored, if preset config is provided
     * @param {Date} config.startDate New time frame start. If provided along with end, view will be centered in this time
     * interval (unless `centerDate` is present)
     * @param {Date} config.endDate New time frame end
     * @param {Date} config.centerDate Date that should be kept in the center. Has priority over start and end params
     * @param {Number} config.width Lowest tick width. Might be increased automatically
     * @param {Number} [config.leftMargin] Amount of pixels to extend span start on (used, when zooming to span)
     * @param {Number} [config.rightMargin] Amount of pixels to extend span end on (used, when zooming to span)
     * @param {Number} [config.adjustStart] Amount of units to extend span start on (used, when zooming to span)
     * @param {Number} [config.adjustEnd] Amount of units to extend span end on (used, when zooming to span)
     * @category Zoom
     */
    zoomTo(config) {
        const me = this;

        if (typeof config === 'object') {
            if (config.preset) {
                me.zoomToLevel(config.preset, config);
            }
            else if (config.level != null) {
                me.zoomToLevel(config.level, config);
            }
            else {
                me.zoomToSpan(config);
            }
        }
        else {
            me.zoomToLevel(config);
        }
    }

    /**
     * Allows zooming to certain level of {@link Scheduler.view.mixin.TimelineViewPresets#config-presets} array. Automatically limits zooming between {@link #config-maxZoomLevel}
     * and {@link #config-minZoomLevel}. Can also set time axis timespan to the supplied start and end dates.
     *
     * @param {Number} preset Level to zoom to.
     * @param {Object} [options] Object, containing options for this method
     * @param {Date} options.startDate New time frame start. If provided along with end, view will be centered in this time
     * interval, ignoring centerDate config.
     * @param {Date} options.endDate New time frame end.
     * @param {Date} options.centerDate Date that should be kept in center. Is ignored when start and end are provided.
     * @param {Number} options.width Lowest tick width. Might be increased automatically
     * @return {Number} level Current zoom level or null if it hasn't changed.
     * @category Zoom
     */
    zoomToLevel(preset, options = {}) {
        // Sanitize numeric zooming.
        if (typeof preset === 'number') {
            preset = Math.min(Math.max(preset, this.minZoomLevel), this.maxZoomLevel);
        }

        const
            me                 = this,
            { config }         = me,
            tickSizeProp       = me.isVertical ? 'tickHeight' : 'tickWidth',
            newPreset          = me.normalizePreset(preset),
            configuredTickSize = newPreset[tickSizeProp];

        let startDate    = options.startDate,
            endDate      = options.endDate,
            span         = startDate && endDate ? { startDate, endDate } : null,
            centerDate   = options.centerDate || (span ? new Date((startDate.getTime() + endDate.getTime()) / 2) : me.viewportCenterDateCached),
            // // eslint-disable-next-line no-undef
            panelSize    = me.timeAxisSubGrid.width;

        if (!span) {
            // If we revert back to the original settings, and we had an original span
            // then revert back to that span.
            if (config.startDate && config.endDate && config.viewPreset && newPreset.equals(me.normalizePreset(config.viewPreset))) {
                span = {
                    startDate : config.startDate,
                    endDate   : config.endDate
                };
            }
            else {
                span = me.calculateOptimalDateRange(centerDate, panelSize, newPreset);
            }
        }

        // Temporarily override tick size while reconfiguring the TimeAxisViewModel
        if ('width' in options) {
            newPreset.setData(tickSizeProp, options.width);
        }

        me.isZooming = true;

        me.setViewPreset(newPreset, span.startDate || me.startDate, span.endDate || me.endDate, false, { centerDate : centerDate });

        // after switching the view preset the `width` config of the zoom level may change, because of adjustments
        // we will save the real value in the `actualWidth` property, so that `getCurrentZoomLevelIndex` method
        // will return the exact level index after zooming
        newPreset.actualWidth = me.timeAxisViewModel.tickSize;

        me.isZooming = false;

        // Restore the tick size because the default presets are shared.
        newPreset.setData(tickSizeProp, configuredTickSize);
    }

    /**
     * Changes the range of the scheduling chart to fit all the events in its event store.
     * @param {Object} [options] Options object for the zooming operation.
     * @param {Number} [options.leftMargin] Defines margin in pixel between the first event start date and first visible date
     * @param {Number} [options.rightMargin] Defines margin in pixel between the last event end date and last visible date
     */
    zoomToFit(options) {
        const eventStore = this.eventStore,
            span       = eventStore.getTotalTimeSpan();

        options = Object.assign({
            leftMargin  : 0,
            rightMargin : 0
        }, options, span);

        // Make sure we received a time span, event store might be empty
        if (options.startDate && options.endDate) {
            this.zoomToSpan(options);
        }
    }

    /**
     * Sets time frame to specified range and applies zoom level which allows to fit all columns to this range.
     *
     * The given time span will be centered in the scheduling view, in the same time, the start/end date of the whole time axis
     * will be extended in the same way as {@link #function-zoomToLevel} method does, to allow scrolling for user.
     *
     * @param {Object} config The time frame.
     * @param {Date} config.startDate The time frame start.
     * @param {Date} config.endDate The time frame end.
     * @param {Date} [config.centerDate] Date that should be kept in the center. Has priority over start and end params
     * @param {Number} [config.leftMargin] Amount of pixels to extend span start on
     * @param {Number} [config.rightMargin] Amount of pixels to extend span end on
     * @param {Number} [config.adjustStart] Amount of units to extend span start on
     * @param {Number} [config.adjustEnd] Amount of units to extend span end on
     *
     * @return {Number} level Current zoom level or null if it hasn't changed.
     * @category Zoom
     */
    zoomToSpan(config = {}) {
        if (config.leftMargin || config.rightMargin) {
            config.adjustStart = 0;
            config.adjustEnd = 0;
        }

        if (!config.leftMargin) config.leftMargin = 0;
        if (!config.rightMargin) config.rightMargin = 0;

        if (!config.startDate || !config.endDate) throw new Error('zoomToSpan: must provide startDate + endDate dates');

        let me           = this,
            startDate    = config.startDate,
            endDate      = config.endDate,
            // this config enables old zoomToSpan behavior which we want to use for zoomToFit in Gantt
            needToAdjust = config.adjustStart >= 0 || config.adjustEnd >= 0;

        if (needToAdjust) {
            startDate = DateHelper.add(startDate, -config.adjustStart, me.timeAxis.mainUnit);
            endDate   = DateHelper.add(endDate, config.adjustEnd, me.timeAxis.mainUnit);
        }

        if (startDate <= endDate) {
            // get scheduling view width
            const { availableSpace } = me.timeAxisViewModel;

            // if potential width of col is less than col width provided by zoom level
            //   - we'll zoom out panel until col width fit into width from zoom level
            // and if width of column is more than width from zoom level
            //   - we'll zoom in until col width fit won't fit into width from zoom level

            let currLevel = Math.floor(me.zoomLevel);

            // if we zoomed out even more than the highest zoom level - limit it to the highest zoom level
            if (currLevel === -1) currLevel = 0;

            const presets = me.presets.allRecords;

            let diffMS                 = endDate - startDate || 1,
                msPerPixel             = me.getMilliSecondsPerPixelForZoomLevel(presets[currLevel], true),
                // increment to get next zoom level:
                // -1 means that given timespan won't fit the available width in the current zoom level, we need to zoom out,
                // so that more content will "fit" into 1 px
                //
                // +1 mean that given timespan will already fit into available width in the current zoom level, but,
                // perhaps if we'll zoom in a bit more, the fitting will be better
                inc                    = diffMS / msPerPixel + config.leftMargin + config.rightMargin > availableSpace ? -1 : 1,
                candidateLevel         = currLevel + inc,
                zoomLevel, levelToZoom = null;

            // loop over zoom levels
            while (candidateLevel >= 0 && candidateLevel <= presets.length - 1) {
                // get zoom level
                zoomLevel = presets[candidateLevel];

                msPerPixel = me.getMilliSecondsPerPixelForZoomLevel(zoomLevel, true);
                const spanWidth = diffMS / msPerPixel + config.leftMargin + config.rightMargin;

                // if zooming out
                if (inc === -1) {
                    // if columns fit into available space, then all is fine, we've found appropriate zoom level
                    if (spanWidth <= availableSpace) {
                        levelToZoom = candidateLevel;
                        // stop searching
                        break;
                    }
                    // if zooming in
                }
                else {
                    // if columns still fits into available space, we need to remember the candidate zoom level as a potential
                    // resulting zoom level, the indication that we've found correct zoom level will be that timespan won't fit
                    // into available view
                    if (spanWidth <= availableSpace) {
                        // if it's not currently active level
                        if (currLevel !== candidateLevel - inc) {
                            // remember this level as applicable
                            levelToZoom = candidateLevel;
                        }
                    }
                    else {
                        // Sanity check to find the following case:
                        // If we're already zoomed in at the appropriate level, but the current zoomLevel is "too small" to fit and had to be expanded,
                        // there is an edge case where we should actually just stop and use the currently selected zoomLevel
                        break;
                    }
                }

                candidateLevel += inc;
            }

            // If we didn't find a large/small enough zoom level, use the lowest/highest level
            levelToZoom = levelToZoom != null ? levelToZoom : candidateLevel - inc;

            // presets is the array of all ViewPresets this Scheduler is using
            zoomLevel = presets[levelToZoom];

            const unitToZoom = zoomLevel.bottomHeader.unit;

            if (config.leftMargin || config.rightMargin) {
                // time axis doesn't yet know about new view preset (zoom level) so it cannot round/ceil date correctly
                startDate = new Date(startDate.getTime() - msPerPixel * config.leftMargin);
                endDate   = new Date(endDate.getTime() + msPerPixel * config.rightMargin);
            }

            const tickCount = DateHelper.getDurationInUnit(startDate, endDate, unitToZoom, true) / zoomLevel.bottomHeader.increment;

            if (tickCount === 0) {
                return null;
            }

            let customWidth = Math.floor(availableSpace / tickCount),
                centerDate  = config.centerDate || new Date((startDate.getTime() + endDate.getTime()) / 2),
                range;

            if (needToAdjust) {
                range = {
                    startDate,
                    endDate
                };
            }
            else {
                range = me.calculateOptimalDateRange(centerDate, availableSpace, zoomLevel);
            }

            return me.zoomToLevel(levelToZoom,
                Object.assign(range, {
                    width : customWidth,
                    centerDate
                })
            );
        }

        return null;
    }

    /**
     * Zooms in the timeline according to the array of zoom levels. If the amount of levels to zoom is given, the view will zoom in by this value.
     * Otherwise a value of `1` will be used.
     *
     * @param {Number} levels (optional) amount of levels to zoom in
     *
     * @return {Number} currentLevel New zoom level of the panel or null if level hasn't changed.
     * @category Zoom
     */
    zoomIn(levels = 1) {
        const currentZoomLevelIndex = this.zoomLevel;

        if (currentZoomLevelIndex >= this.presets.count - 1) return null;

        return this.zoomToLevel(Math.floor(currentZoomLevelIndex) + levels);
    }

    /**
     * Zooms out the timeline according to the array of zoom levels. If the amount of levels to zoom is given, the view will zoom out by this value.
     * Otherwise a value of `1` will be used.
     *
     * @param {Number} levels (optional) amount of levels to zoom out
     *
     * @return {Number} currentLevel New zoom level of the panel or null if level hasn't changed.
     * @category Zoom
     */
    zoomOut(levels = 1) {
        const currentZoomLevelIndex = this.zoomLevel;

        if (currentZoomLevelIndex <= 0) return null;

        return this.zoomToLevel(Math.ceil(currentZoomLevelIndex) - levels);
    }

    /**
     * Zooms in the timeline to the {@link #config-maxZoomLevel} according to the array of zoom levels.
     *
     * @return {Number} currentLevel New zoom level of the panel or null if level hasn't changed.
     * @category Zoom
     */
    zoomInFull() {
        return this.zoomToLevel(this.maxZoomLevel);
    }

    /**
     * Zooms out the timeline to the {@link #config-minZoomLevel} according to the array of zoom levels.
     *
     * @return {Number} currentLevel New zoom level of the panel or null if level hasn't changed.
     * @category Zoom
     */
    zoomOutFull() {
        return this.zoomToLevel(this.minZoomLevel);
    }

    /*
     * Adjusts the timespan of the panel to the new zoom level. Used for performance reasons,
     * as rendering too many columns takes noticeable amount of time so their number is limited.
     * @category Zoom
     * @private
     */
    calculateOptimalDateRange(centerDate, panelSize, viewPreset, userProvidedSpan) {
        // this line allows us to always use the `calculateOptimalDateRange` method when calculating date range for zooming
        // (even in case when user has provided own interval)
        // other methods may override/hook into `calculateOptimalDateRange` to insert own processing
        // (infinite scrolling feature does)
        if (userProvidedSpan) return userProvidedSpan;

        const
            me               = this,
            { timeAxis }     = me,
            { bottomHeader } = viewPreset,
            tickWidth        = me.isHorizontal ? viewPreset.tickWidth : viewPreset.tickHeight;

        if (me.zoomKeepsOriginalTimespan) {
            return {
                startDate : timeAxis.startDate,
                endDate   : timeAxis.endDate
            };
        }

        const
            unit       = bottomHeader.unit,
            difference = Math.ceil(panelSize / tickWidth * bottomHeader.increment * me.visibleZoomFactor / 2),
            startDate  = DateHelper.add(centerDate, -difference, unit),
            endDate    = DateHelper.add(centerDate, difference, unit);

        return {
            startDate : timeAxis.floorDate(startDate, false, unit, bottomHeader.increment),
            endDate   : timeAxis.ceilDate(endDate, false, unit, bottomHeader.increment)
        };
    }

    onWheel(event) {
        const me = this;

        if (event.ctrlKey) {
            event.preventDefault();

            if (!me.preventScrollZoom) {
                if (event.deltaY > 0) {
                    me.zoomOut();
                }
                else if (event.deltaY < 0) {
                    me.zoomIn();
                }
                me.preventScrollZoom = true;
                me.setTimeout(() => me.preventScrollZoom = false, 30);
            }
        }
    }

    /**
     * Changes the time axis timespan to the supplied start and end dates.
     * @param {Date} startDate The new start date
     * @param {Date} endDate The new end date. If not supplied, the {@link Scheduler.preset.ViewPreset#field-defaultSpan} property of the current view preset will be used to calculate the new end date.
     */
    setTimeSpan(startDate, endDate) {
        this.timeAxis.setTimeSpan(startDate, endDate);
    }

    /**
     * Moves the time axis by the passed amount and unit.
     *
     * NOTE: If using a filtered time axis, see {@link Scheduler.data.TimeAxis#function-shift} for more information.
     *
     * @param {Number} amount The number of units to jump
     * @param {String} [unit] The unit (Day, Week etc)
     */
    shift(amount, unit) {
        this.timeAxis.shift(amount, unit);
    }

    /**
     * Moves the time axis forward in time in units specified by the view preset `shiftUnit`, and by the amount specified by the `shiftIncrement`
     * config of the current view preset.
     *
     * NOTE: If using a filtered time axis, see {@link Scheduler.data.TimeAxis#function-shiftNext} for more information.
     *
     * @param {Number} [amount] The number of units to jump forward
     */
    shiftNext(amount) {
        this.timeAxis.shiftNext(amount);
    }

    /**
     * Moves the time axis backward in time in units specified by the view preset `shiftUnit`, and by the amount specified by the `shiftIncrement` config of the current view preset.
     *
     * NOTE: If using a filtered time axis, see {@link Scheduler.data.TimeAxis#function-shiftPrevious} for more information.
     *
     * @param {Number} [amount] The number of units to jump backward
     */
    shiftPrevious(amount) {
        this.timeAxis.shiftPrevious(amount);
    }

    // This does not need a className on Widgets.
    // Each *Class* which doesn't need 'b-' + constructor.name.toLowerCase() automatically adding
    // to the Widget it's mixed in to should implement thus.
    get widgetClass() {}
};
