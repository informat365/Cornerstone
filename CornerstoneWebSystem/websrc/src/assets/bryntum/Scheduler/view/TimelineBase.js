import GridBase from '../../Grid/view/GridBase.js';
import BrowserHelper from '../../Core/helper/BrowserHelper.js';
import DateHelper from '../../Core/helper/DateHelper.js';
import DomHelper from '../../Core/helper/DomHelper.js';
import StringHelper from '../../Core/helper/StringHelper.js';
import FunctionHelper from '../../Core/helper/FunctionHelper.js';
import { base } from '../../Core/helper/MixinHelper.js';
import Collection from '../../Core/util/Collection.js';
import ObjectHelper from '../../Core/helper/ObjectHelper.js';
import Header from './Header.js';
import TimeAxis from '../data/TimeAxis.js';
import TimeAxisViewModel from './model/TimeAxisViewModel.js';

import TimelineDateMapper from './mixin/TimelineDateMapper.js';
import TimelineDomEvents from './mixin/TimelineDomEvents.js';
import TimelineViewPresets from './mixin/TimelineViewPresets.js';
import TimelineZoomable from './mixin/TimelineZoomable.js';
import TimelineEventRendering from './mixin/TimelineEventRendering.js';
import TimelineScroll from './mixin/TimelineScroll.js';
import IdHelper from '../../Core/helper/IdHelper.js';

const
    timeAxisColumnConfigs = [
        'viewPreset',
        'eventBarTextField',
        'eventRenderer',
        'eventRendererThisObj',
        'eventBodyTemplate'
    ],
    exitTransition = {
        fn                : 'exitTransition',
        delay             : 0,
        cancelOutstanding : true
    };

/**
 * @module Scheduler/view/TimelineBase
 */

/**
 * Abstract base class used by timeline based components such as Scheduler and Gantt. Based on Grid, supplies a "locked"
 * region for columns and a "normal" for rendering of events etc.
 * @abstract
 *
 * @mixes Scheduler/view/mixin/TimelineDateMapper
 * @mixes Scheduler/view/mixin/TimelineDomEvents
 * @mixes Scheduler/view/mixin/TimelineEventRendering
 * @mixes Scheduler/view/mixin/TimelineScroll
 * @mixes Scheduler/view/mixin/TimelineViewPresets
 * @mixes Scheduler/view/mixin/TimelineZoomable
 *
 * @extends Grid/view/Grid
 */
export default class TimelineBase extends base(GridBase).mixes(
    TimelineDateMapper,
    TimelineDomEvents,
    TimelineEventRendering,
    TimelineScroll,
    TimelineViewPresets,
    TimelineZoomable
) {
    //region Config

    static get $name() {
        return 'TimelineBase';
    }

    static get defaultConfig() {
        return {
            /**
             * A valid JS day index between 0-6 (0: Sunday, 1: Monday etc.) to be considered the start day of the week.
             * When omitted, the week start day is retrieved from the active locale class.
             * @config {Number}
             * @category Time axis
             */
            weekStartDay : DateHelper.weekStartDay,

            /**
             * An object with format `{ fromDay, toDay, fromHour, toHour }` that describes the working days and hours.
             * This object will be used to populate TimeAxis {@link Scheduler.data.TimeAxis#config-include} property.
             *
             * Using it results in a non-continuous time axis. Any ticks not covered by the working days and hours will
             * be excluded. Events within larger ticks (for example if using week as the unit for ticks) will be
             * stretched to fill the gap otherwise left by the non working hours.
             *
             * As with end dates, `toDay` and `toHour` are exclusive. Thus `toDay : 6` means that day 6 (saturday) will
             * not be included.
             *
             *
             * **NOTE:** When this feature is enabled {@link Scheduler.view.mixin.TimelineZoomable Zooming feature} is
             * not supported. It's recommended to disable zooming controls:
             *
             * ```javascript
             * new Scheduler({
             *     zoomOnMouseWheel          : false,
             *     zoomOnTimeAxisDoubleClick : false,
             *     ...
             * });
             * ```
             *
             * @config {Object}
             * @category Time axis
             */
            workingTime : null,

            /**
             * The backing store providing the input date data for the timeline panel. Created automatically if none
             * supplied.
             * @config {Scheduler.data.TimeAxis}
             * @category Time axis
             */
            timeAxis : null,

            /**
             * The backing view model for the visual representation of the time axis.
             * Either a real instance or a simple config object.
             * @private
             * @config {Scheduler.view.model.TimeAxisViewModel|Object}
             * @category Time axis
             */
            timeAxisViewModel : null,

            /**
             * You can set this option to `false` to make the timeline panel start and end on the exact provided
             * {@link #config-startDate}/{@link #config-endDate} w/o adjusting them.
             * @config {Boolean}
             * @default
             * @category Time axis
             */
            autoAdjustTimeAxis : true,

            /**
             * The start date of the timeline. If omitted, and a TimeAxis has been set, the start date of the provided
             * {@link Scheduler.data.TimeAxis} will be used. If no TimeAxis has been configured, it'll use the start/end
             * dates of the loaded event dataset. If no date information exists in the event data set, it defaults to
             * the current date and time.
             *
             * If a string is supplied, it will be parsed using
             * {@link Core/helper/DateHelper#property-defaultFormat-static DateHelper.defaultFormat}
             *
             * **Note:** If you need to set start and end date at the same time, use
             * {@link Scheduler.view.mixin.TimelineZoomable#function-setTimeSpan} method.
             * @config {Date|String}
             * @category Common
             */
            startDate : null,

            /**
             * The end date of the timeline. If omitted, it will be calculated based on the {@link #config-startDate}
             * setting and the 'defaultSpan' property of the current
             * {@link Scheduler.view.mixin.TimelineViewPresets#config-viewPreset}.
             *
             * If a string is supplied, it will be parsed using
             * {@link Core/helper/DateHelper#property-defaultFormat-static DateHelper.defaultFormat}
             *
             * **Note:** If you need to set start and end date at the same time, use
             * {@link Scheduler.view.mixin.TimelineZoomable#function-setTimeSpan} method.
             * @config {Date|String}
             * @category Common
             */
            endDate : null,

            /**
             * true to snap to resolution increment while interacting with scheduled events.
             * @config {Boolean}
             * @default
             * @category Scheduled events
             */
            snap : false,

            /**
             * Affects drag drop and resizing of events when {@link #config-snap} is enabled. If set to `true`, dates
             * will be snapped relative to event start. e.g. for a zoom level with timeResolution = { unit: "s",
             * increment: "20" }, an event that starts at 10:00:03 and is dragged would snap its start date to 10:00:23,
             * 10:00:43 etc. When set to `false`, dates will be snapped relative to the timeAxis startDate (tick start)
             * - 10:00:03, 10:00:20, 10:00:40 etc.
             * @config {Boolean}
             * @default
             * @category Scheduled events
             */
            snapRelativeToEventStartDate : false,

            /**
             * Set to true to force the time columns to fit to the available horizontal space.
             * @config {Boolean}
             * @default
             * @category Time axis
             */
            forceFit : false,

            /**
             * CSS class to add to rendered events
             * @config {String}
             * @category CSS
             * @private
             */
            eventCls : null,

            /**
             * Returns dates that will constrain resize and drag operations. The method will be called with the
             * Resource, and for operations on existing events - the event. For drag create operation, the mousedown
             * date will be passed as the second parameter
             * @return {Object} Constraining object containing `start` and `end` constraints. Omitting either
             * will mean that end is not constrained. So you can prevent a resize or move from moving *before*
             * a certain time while not constraininhg the end date.
             * @return {Date} [return.start] Start date
             * @return {Date} [return.end] End date
             * @config {Function}
             * @category Scheduled events
             */
            getDateConstraints : null,

            /**
             * CSS class to add to cells in the timeaxis column
             * @config {String}
             * @category CSS
             * @private
             */
            timeCellCls : null,

            timeCellSelector : null,

            scheduledEventName : null,

            /**
             * Create event on double click if scheduler is not in read only mode.
             * Set to false to turn creating off.
             * @config {Boolean}
             * @default
             * @category Scheduled events
             */
            createEventOnDblClick : true,

            //dblClickTime : 200,

            /**
             * A CSS class to apply to each event in the view on mouseover.
             * @config {String}
             * @category CSS
             * @private
             */
            overScheduledEventClass : null,

            // allow the panel to prevent adding the hover CSS class in some cases - during drag drop operations
            preventOverCls : false,

            // The last hovered over event bar HTML node
            hoveredEventNode : null,

            // This setting is set to true by features that need it
            useBackgroundCanvas : false,

            /**
             * Set to `false` if you don't want event bar DOM updates to animate. Animations are disabled in IE11 by default.
             * @config {Boolean}
             * @default true
             * @category Scheduled events
             */
            enableEventAnimations : !BrowserHelper.isIE11,

            disableGridRowModelWarning : true,

            // does not look good with locked columns and also interferes with event animations
            animateRemovingRows : false,

            /**
             * Partners this Timeline panel with another Timeline in order to sync their region sizes (sub-grids like locked, normal will get the same width),
             * start and end dates, view preset, zoom level and scrolling position. All these values will be synced with the timeline defined as the `partner`.
             * @config {Scheduler.view.TimelineBase}
             * @category Time axis
             */
            partner : null,

            schedulerRegion : 'normal',

            transitionDuration : 200,
            // internal timer id reference
            animationTimeout   : null,

            /**
             * How to handle milestones during event layout. Options are:
             * * default - Milestones do not affect event layout
             * * estimate - Milestone width is estimated by multiplying text length with Scheduler#milestoneCharWidth
             * * data - Milestone width is determined by checking EventModel#milestoneWidth
             * * measure - Milestone width is determined by measuring label width
             * Please note that currently text width is always determined using EventModel#name.
             * Also note that only 'default' is supported by eventStyles line, dashed and minimal.
             * @config {String}
             * @default
             * @category Scheduled events
             */
            milestoneLayoutMode : 'default',

            /**
             * Region to which columns are added when they have none specified
             * @config {string}
             * @default
             * @category Misc
             */
            defaultRegion : 'locked'
        };
    }

    //endregion

    //region Init

    construct(config = {}) {
        const me = this,
            region = config.schedulerRegion || 'normal', // Cannot rely on default value, too early
            { subGridConfigs = {} } = config;

        config.subGridConfigs = subGridConfigs;

        subGridConfigs[region] = subGridConfigs[region] || {};
        subGridConfigs[region].headerClass = Header;

        // If user have not specified a width or flex for scheduler region, default to flex=1
        if (!('flex' in subGridConfigs[region] || 'width' in subGridConfigs[region])) {
            subGridConfigs[region].flex = 1;
        }

        // Zooming falls back on config.startDate/endDate, make sure they are dates
        if (config.startDate) {
            config.startDate = DateHelper.parse(config.startDate);
        }
        if (config.endDate) {
            config.endDate = DateHelper.parse(config.endDate);
        }

        super.construct(config);

        // Buffer resize, since resize does full redraw it is costly
        me.onSchedulerViewportResize = me.throttle(me.onSchedulerViewportResize, 250);

        me.initDomEvents();

        me.currentOrientation.init();

        me.rowManager.on('refresh', () => {
            me.forceLayout = false;
        });

        if (me.loadMask && me.crudManager && me.crudManager.isLoading) {
            // Show loadmask if crud manager is already loading
            me.onStoreBeforeRequest();
        }
    }

    doDestroy() {
        const me = this,
            { currentOrientation } = this;

        if (currentOrientation) {
            currentOrientation.destroy();
        }

        // Break links between this TimeLine and any partners.
        if (me.partneredWith) {
            me.partneredWith.forEach(p => {
                me.removePartner(p);
            });
            me.partneredWith.destroy();
        }
        super.doDestroy();
    }

    startConfigure(config) {
        super.startConfigure(config);

        // partner needs to be initialized first so that the various shared
        // configs are assigned first before we default them in.
        this._thisIsAUsedExpression(this.partner);
    }

    onPaint({ firstPaint }) {
        // Upon first paint we need to pass the forceUpdate flag in case we are sharing the TimAxisViewModel
        // with another Timeline which will already have done this.
        if (firstPaint) {
            // subGrid in IE11 doesn't have height at this point, but container element does so we take it from there
            // TODO: make flex element inherit height from parent (min-height: 100% doesn't work)
            const
                { timeAxisSubGrid } = this,
                availableSpace      = this.isVertical ? (BrowserHelper.isIE11 ? timeAxisSubGrid.element.parentElement.offsetHeight : timeAxisSubGrid.height) : timeAxisSubGrid.width;

            this.timeAxisViewModel.update(availableSpace, false, true);
        }

        super.onPaint([...arguments]);
    }

    initSubGrids() {
        super.initSubGrids();

        const
            me              = this,
            timeAxisSubGrid = me.timeAxisSubGrid = me.subGrids[me.timeAxisColumn.region];

        // Scheduler SubGrid doesn't accept external columns moving in
        timeAxisSubGrid.sealedColumns = true;

        timeAxisSubGrid.on({
            resize  : me.onTimeAxisSubGridResize,
            thisObj : me
        });
    }

    onSchedulerHorizontalScroll(subGrid, scrollLeft) {
        // rerender cells in scheduler column on horizontal scroll to display events in view
        this.currentOrientation.updateFromHorizontalScroll(scrollLeft);

        super.onSchedulerHorizontalScroll(subGrid, scrollLeft);
    }

    /**
     * Overrides initScroll from Grid, listens for horizontal scroll to do virtual event rendering
     * @private
     */
    initScroll() {
        let me         = this,
            frameCount = 0;

        super.initScroll();

        me.on('horizontalscroll', ({ subGrid, scrollLeft }) => {
            if (me.isPainted && subGrid === me.timeAxisSubGrid && !me.isDestroying) {
                me.onSchedulerHorizontalScroll(subGrid, scrollLeft);
            }
            frameCount++;
        });

        if (me.testPerformance === 'horizontal') {
            setTimeout(() => {
                let start       = performance.now(), // eslint-disable-line no-undef
                    scrollSpeed = 5,
                    direction   = 1;

                let scrollInterval = setInterval(() => {
                    scrollSpeed = scrollSpeed + 5;

                    me.scrollLeft += (10 + Math.floor(scrollSpeed)) * direction;

                    if (direction === 1 && me.scrollLeft > 5500) {
                        direction = -1;
                        scrollSpeed = 5;
                    }

                    if (direction === -1 && me.scrollLeft <= 0) {
                        let done    = performance.now(), // eslint-disable-line no-undef
                            elapsed = done - start;

                        let timePerFrame = elapsed / frameCount,
                            fps          = 1000 / timePerFrame;

                        fps = Math.round(fps * 10) / 10;

                        clearInterval(scrollInterval);

                        console.log(me.eventPositionMode, me.eventScrollMode, fps + 'fps');
                    }
                }, 0);
            }, 500);
        }
    }

    //endregion

    //region Config getters/setters

    /**
     * Returns `true` if any of the events/tasks or feature injected elements (such as ResourceTimeRanges) are within
     * the {@link #config-timeAxis}
     * @property {Boolean}
     * @readonly
     */
    get hasVisibleEvents() {
        return !this.noFeatureElementsInAxis() || this.eventStore.storage.values.some(t => this.timeAxis.isTimeSpanInAxis(t));
    }

    // Template function to be chained in features to determine if any elements are in time axis (needed since we cannot
    // currently chain getters). Negated to not break chain. First feature that has elements visible returns false,
    // which prevents other features from being queried.
    noFeatureElementsInAxis() { }

    // Private getter used to piece togheter event names such as beforeEventDrag / beforeTaskDrag. Could also be used
    // in templates.
    get capitalizedEventName() {
        if (!this._capitalizedEventName) {
            this._capitalizedEventName = StringHelper.capitalizeFirstLetter(this.scheduledEventName);
        }

        return this._capitalizedEventName;
    }

    set partner(partner) {
        const me = this,
            partneredWith = me.partneredWith || (me.partneredWith = new Collection());

        me._partner = partner;

        if (!partneredWith.includes(partner)) {
            // Each must know about the other so that they can sync others upon region resize
            partneredWith.add(partner);

            (partner.partneredWith || (partner.partneredWith = new Collection())).add(me);

            partner.on({
                presetchange : 'onPartnerPresetChange',
                thisObj      : me
            });
            me.on({
                presetchange : 'onPartnerPresetChange',
                thisObj      : partner
            });

            me.setConfig({
                viewPreset        : partner.viewPreset,
                timeAxis          : partner.timeAxis,
                timeAxisViewModel : partner.timeAxisViewModel
            });

            // When initScroll comes round, make sure it syncs with the partner
            me.initScroll = FunctionHelper.createSequence(me.initScroll, () => {
                me.scrollable.addPartner(partner.scrollable, 'x');
            }, me);
        }
    }

    removePartner(partner) {
        const me = this,
            partneredWith = me.partneredWith;

        if (partneredWith && partneredWith.includes(partner)) {
            partneredWith.remove(partner);
            partner.partneredWith.remove(me);
            partner.scrollable.removePartner(me.scrollable);
            partner.un({
                presetchange : 'onPartnerPresetChange',
                thisObj      : me
            });
            me.un({
                presetchange : 'onPartnerPresetChange',
                thisObj      : partner
            });
        }
    }

    onPartnerPresetChange({ preset, startDate, endDate, centerDate }) {
        if (!this._viewPresetChanging && this.viewPreset !== preset) {
            // Passing more params directly to make zoom behave identically. See #8764
            this.setViewPreset(preset, startDate, endDate, false, { centerDate });
        }
    }

    get partner() {
        return this._partner;
    }

    get timeAxisColumn() {
        return this.columns && this._timeAxisColumn;
    }

    get columns() {
        // Maintainer. If we do not implement a getter to go along with our setter, this
        // property becomes unreadable at this class level.
        return super.columns;
    }

    set columns(columns) {
        const me = this;

        let timeAxisColumnIndex = columns && columns.length,
            timeAxisColumnConfig, timeAxisColumn, value;

        // No columns means destroy
        if (columns) {

            columns.forEach((col, index) => {
                if (col.type === 'timeAxis') {
                    timeAxisColumnIndex = index;
                    timeAxisColumnConfig = col;
                }
            });

            // TODO : This is scheduler specific, override `set columns` instead
            // No additional columns allowed in vertical mode, but store the specified set in case we toggle mode later
            if (me.isVertical) {
                me._horizontalColumns = columns;

                columns = [
                    {
                        type     : 'verticalTimeAxis',
                        locked   : true,
                        timeline : me
                    },
                    // Make space for a regular TimeAxisColumn after the VerticalTimeAxisColumn
                    columns[timeAxisColumnIndex]
                ];

                timeAxisColumnIndex = 1;
            }
            else {
                // We're going to mutate this array which we do not own, so copy it first.
                columns = columns.slice();
            }

            // Fix up the timeAxisColumn config in place
            timeAxisColumnConfig = columns[timeAxisColumnIndex] = Object.assign({
                type     : 'timeAxis',
                locked   : false,
                timeline : me,
                cellCls  : me.timeCellCls,
                mode     : me.mode
            }, timeAxisColumnConfig);

            // Pass these configs into the timeAxisColumnConfig
            timeAxisColumnConfigs.forEach(configName => {
                value = me[configName];
                if (value != null) {
                    timeAxisColumnConfig[configName] = value;
                }
            });
        }

        // Invoke Grid's setter. Will create a ColumnStore which is returned by the columns setter.
        super.columns = columns;

        if (columns) {
            timeAxisColumn = me._timeAxisColumn = me.columns.getAt(timeAxisColumnIndex);

            if (me.isVertical) {
                me.verticalTimeAxisColumn = me.columns.getAt(timeAxisColumnIndex - 1);
            }

            // Set up event relaying early
            timeAxisColumn.relayAll(me);
        }
    }

    get timeView() {
        const me = this;
        // Maintainer, we need to ensure that the columns property is initialized
        // if this getter is called at configuration time before columns have been ingested.
        return me.columns && me.isVertical
            ? (me.verticalTimeAxisColumn && me.verticalTimeAxisColumn.view)
            : (me.timeAxisColumn && me.timeAxisColumn.timeAxisView);
    }

    set eventCls(eventCls) {
        const me = this;

        me._eventCls = eventCls;
        if (!me.eventSelector) {
            me.eventSelector = `.${eventCls}-wrap`;
            me.unreleasedEventSelector = `${me.eventSelector}:not(.b-sch-released)`;
        }
        if (!me.eventInnerSelector) {
            me.eventInnerSelector = '.' + eventCls;
        }
    }

    get eventCls() {
        return this._eventCls;
    }

    set timeAxisViewModel(timeAxisViewModel) {
        const me = this,
            tavmListeners = {
                update  : 'onTimeAxisViewModelUpdate',
                prio    : 100,
                thisObj : me
            };

        if (me.partner && !timeAxisViewModel) {
            return;
        }

        // Getting rid of instanceof check to allow using code from different bundles
        if (timeAxisViewModel && timeAxisViewModel.isTimeAxisViewModel) {
            timeAxisViewModel.on(tavmListeners);
        }
        else {
            timeAxisViewModel = Object.assign({
                mode      : me._mode,
                snap      : me.snap,
                forceFit  : me.forceFit,
                timeAxis  : me.timeAxis,
                listeners : tavmListeners
            }, timeAxisViewModel);

            timeAxisViewModel = new TimeAxisViewModel(timeAxisViewModel);
        }

        me._timeAxisViewModel = timeAxisViewModel;
    }

    /**
     * The internal view model, describing the visual representation of the time axis.
     * @property {Scheduler.view.model.TimeAxisViewModel}
     * @readonly
     */
    get timeAxisViewModel() {
        if (!this._timeAxisViewModel) {
            this.timeAxisViewModel = null;
        }
        return this._timeAxisViewModel;
    }

    set timeAxis(timeAxis) {
        const me = this;

        if (me.partner && !timeAxis) {
            return;
        }

        me.timeAxisDetacher && me.timeAxisDetacher();

        // Getting rid of instanceof check to allow using code from different bundles
        if (!(timeAxis && timeAxis.isTimeAxis)) {
            timeAxis = ObjectHelper.assign({
                viewPreset   : me.viewPreset,
                autoAdjust   : me.autoAdjustTimeAxis,
                mode         : 'plain',
                weekStartDay : me.weekStartDay
            }, timeAxis);

            if (me.startDate) {
                timeAxis.startDate = me.startDate;
            }
            if (me.endDate) {
                timeAxis.endDate = me.endDate;
            }

            if (me.workingTime) {
                me.applyWorkingTime(timeAxis);
            }

            timeAxis = new TimeAxis(timeAxis);
        }

        // Inform about reconfiguring the timeaxis, to allow users to react to start & end date changes
        me.timeAxisDetacher = timeAxis.on({
            thisObj : me,
            reconfigure({ config }) {
                /**
                 * Fired when the timeaxis has changed, for example by zooming or configuring a new time span.
                 * @event timeAxisChange
                 * @param {Scheduler.view.Scheduler} source - This Scheduler
                 * @param {Object} config Config object used to reconfigure the time axis.
                 * @param {Date} config.start New start date (if supplied)
                 * @param {Date} config.end New end date (if supplied)
                 */
                me.trigger('timeAxisChange', { config });
            }
        });

        me._timeAxis = timeAxis;
    }

    get timeAxis() {
        if (!this._timeAxis) {
            this.timeAxis = null;
        }
        return this._timeAxis;
    }

    /**
     * Get/set working time. Assign `null` to stop using working time. See {@link #config-workingTime} config for details.
     * @property {Object}
     */
    set workingTime(config) {
        this._workingTime = config;

        if (!this.isConfiguring) {
            this.applyWorkingTime(this.timeAxis);
        }
    }

    get workingTime() {
        return this._workingTime;
    }

    // Translates the workingTime configs into TimeAxis#include rules, applies them and then refreshes the header and
    // redraws the events
    applyWorkingTime(timeAxis) {
        const me = this,
            config = me._workingTime;

        if (config) {
            let hour = null;
            // Only use valid values
            if (config.fromHour >= 0 && config.fromHour < 24 && config.toHour > config.fromHour && config.toHour <= 24 && config.toHour - config.fromHour < 24) {
                hour = { from : config.fromHour, to : config.toHour };
            }

            let day = null;
            // Only use valid values
            if (config.fromDay >= 0 && config.fromDay < 7 && config.toDay > config.fromDay && config.toDay <= 7 && config.toDay - config.fromDay < 7) {
                day = { from : config.fromDay, to : config.toDay };
            }

            if (hour || day) {
                timeAxis.include = {
                    hour,
                    day
                };
            }
            else {
                // No valid rules, restore timeAxis
                timeAxis.include = null;
            }
        }
        else {
            // No rules, restore timeAxis
            timeAxis.include = null;
        }

        if (me.isPainted) {
            // Refreshing header, which also recalculate tickSize and header data
            me.timeAxisColumn.refreshHeader();
            // Update column lines
            if (me.features.columnLines) {
                me.features.columnLines.drawLines();
            }

            // Animate event changes
            me.refreshWithTransition();
        }
    }

    /**
     * Get/set startDate. Defaults to current date if none specified.
     *
     * **Note:** If you need to set start and end date at the same time, use {@link Scheduler.view.mixin.TimelineZoomable#function-setTimeSpan} method.
     * @property {Date}
     * @category Common
     */
    set startDate(date) {
        this.setStartDate(date);
    }

    /**
     * Sets the timeline start date.
     *
     * **Note:**
     * - If you need to set start and end date at the same time, use {@link Scheduler.view.mixin.TimelineZoomable#function-setTimeSpan} method.
     * - If keepDuration is false and new start date is greater than end date, it will throw an exception.
     *
     * @param {Date} date The new start date
     * @param {Boolean} keepDuration Pass `true` to keep the duration of the timeline ("move" the timeline),
     * `false` to change the duration ("resize" the timeline). Defaults to `true`.
     */
    setStartDate(date, keepDuration = true) {
        const
            me = this,
            ta = me._timeAxis || {},
            {
                startDate,
                endDate,
                mainUnit
            } = ta;

        if (typeof date === 'string') {
            date = DateHelper.parse(date);
        }

        if (me._timeAxis && endDate) {
            if (date) {
                let calcEndDate = endDate;

                if (keepDuration && startDate) {
                    const diff = DateHelper.diff(startDate, endDate, mainUnit, true);
                    calcEndDate = DateHelper.add(date, diff, mainUnit);
                }

                ta.setTimeSpan(date, calcEndDate);
            }
        }
        else {
            me._tempStartDate = date;
        }
    }

    get startDate() {
        const me = this;

        if (me._timeAxis) {
            return me._timeAxis.startDate;
        }

        return me._tempStartDate || new Date();
    }

    /**
     * Get/set endDate. Defaults to startDate + default span of the used ViewPreset.
     *
     * **Note:** If you need to set start and end date at the same time, use {@link Scheduler.view.mixin.TimelineZoomable#function-setTimeSpan} method.
     * @property {Date}
     * @category Common
     */
    set endDate(date) {
        this.setEndDate(date);
    }

    /**
     * Sets the timeline end date
     *
     * **Note:**
     * - If you need to set start and end date at the same time, use {@link Scheduler.view.mixin.TimelineZoomable#function-setTimeSpan} method.
     * - If keepDuration is false and new end date is less than start date, it will throw an exception.
     *
     * @param {Date} date The new end date
     * @param {Boolean} keepDuration Pass `true` to keep the duration of the timeline ("move" the timeline),
     * `false` to change the duration ("resize" the timeline). Defaults to `false`.
     */
    setEndDate(date, keepDuration = false) {
        const
            me = this,
            ta = me._timeAxis || {},
            {
                startDate,
                endDate,
                mainUnit
            } = ta;

        if (typeof date === 'string') {
            date = DateHelper.parse(date);
        }

        if (me._timeAxis && startDate) {
            if (date) {
                let calcStartDate = startDate;

                if (keepDuration && endDate) {
                    const diff = DateHelper.diff(startDate, endDate, mainUnit, true);
                    calcStartDate = DateHelper.add(date, -diff, mainUnit);
                }

                ta.setTimeSpan(calcStartDate, date);
            }
        }
        else {
            me._tempEndDate = date;
        }
    }

    get endDate() {
        const me = this;

        if (me._timeAxis) {
            return me._timeAxis.endDate;
        }

        return me._tempEndDate || DateHelper.add(me.startDate, me.viewPreset.defaultSpan, me.viewPreset.mainHeader.unit);
    }

    get features() {
        return super.features;
    }

    // add region resize by default
    set features(features) {
        features = features === true ? {} : features;

        if (!('regionResize' in features)) {
            features.regionResize = true;
        }

        super.features = features;
    }

    get eventStyle() {
        return this._eventStyle;
    }

    set eventStyle(style) {
        const me = this;

        me._eventStyle = style;

        if (me.isPainted) {
            me.refreshWithTransition();
        }
    }

    get eventColor() {
        return this._eventColor;
    }

    set eventColor(color) {
        const me = this;

        me._eventColor = color;

        if (me.isPainted) {
            me.refreshWithTransition();
        }
    }

    //endregion

    //region Event handlers

    onLocaleChange() {
        // Clear events in case they use date as part of displayed info
        //        this.currentOrientation.cache.clear();

        super.onLocaleChange();
        this.timeAxisColumn.refreshHeader();
    }

    /**
     * This is a template method called by Widget when the encapsulating Scheduler SubGrid size.
     * Width changes *might* affect the Scheduler SubGrid, and *that* will trigger its
     * handler below, if that is the case. Here, we are only interested in the height changing because the
     * encapsulating grid is what dictates the scrolling viewport height.
     * @param {HTMLElement} element
     * @param {Number} width
     * @param {Number} height
     * @param {Number} oldWidth
     * @param {Number} oldHeight
     * @private
     */
    onInternalResize(element, width, height, oldWidth, oldHeight) {
        // Cache before its updated by super call.
        const oldSchedulerBodyRect = this._bodyRectangle;

        super.onInternalResize(element, width, height, oldWidth, oldHeight);

        // The Scheduler (The Grid) dictates the viewport height.
        if (oldSchedulerBodyRect && height !== oldHeight) {
            this.onSchedulerViewportResize(oldSchedulerBodyRect.width, this.bodyContainer.offsetHeight, oldSchedulerBodyRect.width, oldSchedulerBodyRect.height);
        }
    }

    /**
     * This is an event handler triggered when the Scheduler SubGrid changes size.
     * Its height changes when content height changes, and that is not what we are
     * interested in here. If the *width* changes, that means the visible viewport
     * has changed size.
     * @param {HTMLElement} element
     * @param {Number} width
     * @param {Number} height
     * @param {Number} oldWidth
     * @param {Number} oldHeight
     * @private
     */
    onTimeAxisSubGridResize({ width, height, oldWidth, oldHeight }) {
        // The timeAxisSubGrid dictates the viewport width.
        if (this.isPainted && width !== oldWidth) {
            const schedulerBodyRect = this._bodyRectangle;

            this.onSchedulerViewportResize(width, schedulerBodyRect.height, oldWidth, schedulerBodyRect.height);
        }
    }

    // Note: This function is throttled in construct(), since it will do a full redraw per call
    onSchedulerViewportResize(width, height, oldWidth, oldHeight) {
        if (this.isPainted) {
            const
                me      = this,
                subGrid = me.timeAxisSubGrid;

            me.currentOrientation.onViewportResize(width, height, oldWidth, oldHeight);

            // Ignore resize caused by toggling vertical scrollbar visibility, since those might otherwise lead to an
            // infinite loop when the difference between on/off causes horizontal scrolling to also toggle
            if (me._lastOverflow == null || me._lastOverflow === subGrid.overflowingHorizontally) {
                // Responding by triggering a layout in the same frame seems to trigger an infinite resize
                // loop when using ResizeObserver, so push the response out into the next frame.
                me.setTimeout(() => {
                    me.timeAxisViewModel.availableSpace = me.isHorizontal ? subGrid.width : subGrid.height;
                }, 0);

                me.partneredWith && !me.isSyncingFromPartner && me.partneredWith.forEach(p => {
                    if (!p.isSyncingFromPartner) {
                        p.isSyncingFromPartner = true;
                        me.eachSubGrid(subGrid => {
                            const partnerSubGrid = p.subGrids[subGrid.region];

                            // If there is a difference, sync the partner SubGrid state
                            if (partnerSubGrid.width !== subGrid.width) {
                                if (subGrid.collapsed) {
                                    partnerSubGrid.collapse();
                                }
                                else {
                                    if (partnerSubGrid.collapsed) {
                                        partnerSubGrid.expand();
                                    }
                                    // When using flexed subgrid, make sure flex values has prio over width
                                    if (subGrid.flex) {
                                        // If flex values match, resize should be fine without changing anything
                                        if (subGrid.flex !== partnerSubGrid.flex) {
                                            partnerSubGrid.flex = subGrid.flex;
                                        }
                                    }
                                    else {
                                        partnerSubGrid.width = subGrid.width;
                                    }
                                }
                            }
                        });
                        p.isSyncingFromPartner = false;
                    }
                });
            }

            // NOTE: Moved out of the if-statement above since that prevented event from being triggered in all
            // scenarios where size has changed

            /**
             * Fired when the *scheduler* viewport (not the overall Scheduler element) changes size.
             * This happens when the grid changes height, or when the subgrid which encapsulates the
             * scheduler column changes width.
             * @event timelineViewportResize
             * @param {Core.widget.Widget} source - This Scheduler
             * @param {Number} width The new width
             * @param {Number} height The new height
             * @param {Number} oldWidth The old width
             * @param {Number} oldHeight The old height
             */
            me.trigger('timelineViewportResize', { width, height, oldWidth, oldHeight });

            me._lastOverflow = subGrid.overflowingHorizontally;
        }
    }

    onTimeAxisViewModelUpdate() {
        this.updateCanvasSize();

        if (this.isHorizontal) {
            // might also have changed total width of timeaxiscolumn, make sure scrollers are in sync
            // TODO: should this be detected by resizeobserver instead? but it is the actual column changing size and not the subgrid so might not make sense
            this.callEachSubGrid('refreshFakeScroll');
            this.refreshVirtualScrollbars();
        }

        this.currentOrientation.onTimeAxisViewModelUpdate();
    }

    //endregion

    //region Mode

    get currentOrientation() {
        throw new Error('Implement in subclass');
    }

    // Horizontal is the default, overridden in scheduler
    get isHorizontal() {
        return true;
    }

    //endregion

    //region Canvases and elements

    get backgroundCanvas() {
        return this._backgroundCanvas;
    }

    get foregroundCanvas() {
        return this._foregroundCanvas;
    }

    get svgCanvas() {
        const me = this;
        if (!me._svgCanvas) {
            const svg = me._svgCanvas = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
            svg.setAttribute('id', IdHelper.generateId('svg'));
            // To not be recycled by DomSync
            svg.retainElement = true;
            me.foregroundCanvas.appendChild(svg);
            me.trigger('svgCanvasCreated');
        }
        return me._svgCanvas;
    }

    get timeAxisSubGridElement() {
        return this.timeAxisColumn && this.timeAxisColumn.subGridElement;
    }

    updateCanvasSize() {
        const me = this;

        if (me.isHorizontal) {
            if (me.backgroundCanvas) {
                me.backgroundCanvas.style.width = `${me.timeAxisViewModel.totalSize}px`;
            }

            me.foregroundCanvas.style.width = `${me.timeAxisViewModel.totalSize}px`;
        }
        else {
            if (me.backgroundCanvas) {
                me.backgroundCanvas.style.width = DomHelper.setLength(me.timeAxisColumn.width);
            }

            me.foregroundCanvas.style.width = DomHelper.setLength(me.timeAxisColumn.width);
        }
    }

    /**
     * A chainable function which Features may hook to add their own content to the timeaxis header.
     * @param {Array} configs An array of domConfigs, append to it to have the config applied to the header
     */
    getHeaderDomConfigs(configs) {}

    /**
     * A chainable function which Features may hook to add their own content to the foreground canvas
     * @param {Array} configs An array of domConfigs, append to it to have the config applied to the foreground canvas
     */
    getForegroundDomConfigs(configs) {}

    //endregion

    //region Grid overrides

    refresh(forceLayout = true, refreshMoreFn = null) {
        const me = this;

        if (me.isPainted && !me.refreshSuspended) {
            if (me.hasVisibleEvents) {
                me.refreshRows(false, forceLayout);
                if (refreshMoreFn) {
                    refreshMoreFn();
                }
            }
            // Even if there are no events in our timeline, Features
            // assume there will be a refresh event from the RowManager
            // after a refresh request so fire it here.
            else {
                me.rowManager.trigger('refresh');
            }
        }
    }

    render() {
        const me          = this,
            schedulerEl = me.timeAxisSubGridElement;

        if (me.useBackgroundCanvas) {
            me._backgroundCanvas = DomHelper.createElement({
                className   : 'b-sch-background-canvas',
                parent      : schedulerEl,
                nextSibling : schedulerEl.firstElementChild
            });
        }

        const fgCanvas = me._foregroundCanvas = DomHelper.createElement({
            className : 'b-sch-foreground-canvas',
            style     : `font-size:${(me.rowHeight - me.barMargin * 2)}px`,
            parent    : schedulerEl
        });

        me.timeAxisSubGrid.insertRowsBefore = fgCanvas;

        super.render(...arguments);
    }

    // TODO: refreshRows -> refresh in grid?
    refreshRows(returnToTop = false, reLayoutEvents = true) {
        const me = this;

        if (me.isConfiguring) {
            return;
        }

        me.currentOrientation.refreshRows(reLayoutEvents);

        super.refreshRows(returnToTop);
    }

    //endregion

    //region Other

    getDateConstraints() {}

    // duration = false prevents transition
    runWithTransition(fn, duration) {
        const me = this;

        // Do not attempt to enter animating state if we are not visible
        if (me.isVisible) {
            // Allow calling with true/false to keep code simpler in other places
            if (duration == null || duration === true) {
                duration = me.transitionDuration;
            }

            // Ask Grid superclass to enter the animated state if requested and enabled.
            if (duration && me.enableEventAnimations) {
                me.isAnimating = true;

                // Exit animating state in duration milliseconds.
                // Cancel any previous outstanding exit timer.
                exitTransition.delay = duration;
                me.setTimeout(exitTransition);
            }
        }

        fn();
    }

    exitTransition() {
        this.isAnimating = false;
        this.trigger('transitionend');
    }

    /**
     * Refreshes the grid with transitions enabled.
     */
    refreshWithTransition(forceLayout) {
        this.runWithTransition(() => this.refresh(forceLayout));
    }

    /**
     * Returns an object representing the visible date range, with `startDate` and `endDate` properties
     * @return {Object} The date range
     * @return {Date} return.startDate Start date
     * @return {Date} return.endDate End date
     */
    getVisibleDateRange() {
        return this.currentOrientation.getVisibleDateRange();
    }
    //endregion
}
