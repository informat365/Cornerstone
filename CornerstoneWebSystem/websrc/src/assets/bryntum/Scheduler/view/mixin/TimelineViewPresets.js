import Base from '../../../Core/Base.js';
import PresetManager from '../../preset/PresetManager.js';
import ViewPreset from '../../preset/ViewPreset.js';
import ObjectHelper from '../../../Core/helper/ObjectHelper.js';
import PresetStore from '../../preset/PresetStore.js';

/**
 * @module Scheduler/view/mixin/TimelineViewPresets
 */

/**
 * View preset handling.
 *
 * A Scheduler's {@link #config-presets} are loaded with a default set of {@link Scheduler.preset.ViewPreset ViewPresets}
 * which are defined by the system in the {@link Scheduler.preset.PresetManager}.
 * 
 * The zooming feature works by reconfiguring the Scheduler with a new {@link Scheduler.preset.ViewPreset ViewPreset} selected
 * from the {@link #config-presets} store.
 *
 * {@link Scheduler.preset.ViewPreset ViewPresets} can be added and removed from the store to change the amount of available steps.
 * Range of zooming in/out can be also modified with {@link Scheduler.view.mixin.TimelineZoomable#config-maxZoomLevel} / {@link Scheduler.view.mixin.TimelineZoomable#config-minZoomLevel} properties.
 *
 * This mixin adds additional methods to the column : {@link Scheduler.view.mixin.TimelineZoomable#property-maxZoomLevel}, {@link Scheduler.view.mixin.TimelineZoomable#property-minZoomLevel}, {@link Scheduler.view.mixin.TimelineZoomable#function-zoomToLevel}, {@link Scheduler.view.mixin.TimelineZoomable#function-zoomIn},
 * {@link Scheduler.view.mixin.TimelineZoomable#function-zoomOut}, {@link Scheduler.view.mixin.TimelineZoomable#function-zoomInFull}, {@link Scheduler.view.mixin.TimelineZoomable#function-zoomOutFull}.
 *
 * **Notice**: Zooming doesn't work properly when `forceFit` option is set to true for the Schedulker or for filtered timeaxis.
 *
 * @mixin
 */
export default Target => class TimelineViewPresets extends (Target || Base) {
    //region Default config

    static get defaultConfig() {
        return {
            /**
             * A string key used to lookup a predefined {@link Scheduler.preset.ViewPreset} (e.g. 'weekAndDay', 'hourAndDay'),
             * managed by {@link Scheduler.preset.PresetManager}. See {@link Scheduler.preset.PresetManager} for more information.
             * Or a config object for a viewPreset.
             *
             * Options:
             * - 'secondAndMinute'
             * - 'minuteAndHour'
             * - 'hourAndDay'
             * - 'dayAndWeek'
             * - 'weekAndDay'
             * - 'weekAndMonth',
             * - 'monthAndYear'
             * - 'year'
             * - 'manyYears'
             * - 'weekAndDayLetter'
             * - 'weekDateAndMonth'
             * - 'day'
             * - 'week'
             *
             * If passed as a config object, the settings from the viewPreset with the provided `base` property will be used along
             * with any overridden values in your object.
             *
             * To override:
             * ```javascript
             * viewPreset : {
             *   base    : 'hourAndDay',
             *   id      : 'myHourAndDayPreset',
             *   headers : [
             *       {
             *           unit      : "hour",
             *           increment : 12,
             *           renderer  : (startDate, endDate, headerConfig, cellIdx) => {
             *               return "";
             *           }
             *       }
             *   ]
             * }
             * ```
             * or set a new valid preset config if the preset is not registered in the {@link Scheduler.preset.PresetManager}.
             *
             * When you use scheduler in weekview mode, this config is used to pick view preset. If passed view preset is not
             * supported by weekview (only 2 supported by default - 'day' and 'week') default preset will be used - 'week'.
             * @config {String|Object}
             * @default
             * @category Common
             */
            viewPreset : 'weekAndDayLetter',

            /**
             * Defines how dates will be formatted in tooltips etc. This config has priority over similar config on the
             * view preset. For allowed values see {@link Core.helper.DateHelper#function-format-static}.
             * @config {String}
             * @category Scheduled events
             */
            displayDateFormat : null,

            /**
             * An array of {@link Scheduler.preset.ViewPreset} config objects
             * which describes the available timeline layouts for this scheduler.
             *
             * By default, a predefined set is loaded from the {@link Scheduler.preset.PresetManager PresetManager}.
             *
             * A {@link Scheduler.preset.ViewPreset ViewPreset} describes the granularity of the 
             * timeline view and the layout and subdivisions of the timeline header.
             * @config {Object[]} presets
             *
             * @category Common
             */
            presets : true
        };
    }

    //endregion

    //region Get/set
    set presets(presets) {
        if (presets === true) {
            presets = PresetManager.allRecords;
        }

        this._presets = new PresetStore({
            data : presets
        });
    }

    get presets() {
        return this._presets;
    }

    /**
     * Get/set the current view preset
     * @property {Scheduler.preset.ViewPreset|String}
     * @category Common
    */
    get viewPreset() {
        return this._viewPreset;
    }

    set viewPreset(preset) {
        if (preset.name && !preset.base) {
            preset.base = preset.name;
        }
        if (!this._viewPreset || !this._viewPreset.equals(preset)) {
            this.setViewPreset(preset);
        }
    }

    normalizePreset(preset) {
        const
            me          = this,
            input       = preset,
            { presets } = me;

        // They may have passed a string id, an number index, or ViewPreset config object.
        if (!(preset instanceof ViewPreset)) {
            // A config of a preset, instantiate it based upon any base that may be requested.
            // This may be one that's supplied by Bryntum in the PresetManager, or one that has been
            // previously added to this Scheduler's preset store.
            if (typeof preset === 'object') {
                // Look up any existing ViewPreset that it is based upon.
                if (preset.base) {
                    // Look locally first, then in the PresetManager
                    const base = presets.getById(preset.base) || PresetManager.getById(preset.base);

                    if (!base) {
                        throw new Error(`ViewPreset base '${preset.base}' does not exist`);
                    }
                    // The config is based upon the base's data with the new config object merged in.
                    preset = ObjectHelper.merge(ObjectHelper.clone(base.data), preset);
                }

                // Ensure the new ViewPreset has a legible, logical id which does not already
                // exist in our store.
                if (preset.id) {
                    preset = presets.createRecord(preset);
                }
                else {
                    preset = presets.createRecord(ObjectHelper.assign({}, preset));
                    preset.id = preset.generateId(presets);
                }
            }
            // Must be an index or id
            else {
                if (typeof preset === 'number') {
                    preset = presets.getAt(preset);
                }
                else {
                    preset = presets.getById(preset) || PresetManager.getById(preset);
                }
            }

            if (!preset) {
                throw new Error(`Invalid ViewPreset requested: ${input}`);
            }
        }

        // An instance.
        // If an existing id is used, this will replace it.
        return presets.add(preset)[0];
    }

    /**
     * Sets the current view preset. See the {@link Scheduler.preset.PresetManager} class for details.
     *
     * @param {String|Object|Scheduler.preset.ViewPreset} preset The id of the new preset (see {@link Scheduler.preset.PresetManager} for details)
     * @param {Date} [startDate] A new start date for the time axis
     * @param {Date} [endDate] A new end date for the time axis
     * @private
     */
    setViewPreset(preset, startDate, endDate, initial, options = {}) {
        const
            me               = this,
            {
                isHorizontal,
                _timeAxis : timeAxis    // Do not tickle the getter, we are just peeking to see if it's there yet.
            } = me,
            event            = {
                startDate,
                endDate,
                from : me.viewPreset
            };

        // normalize preset (applies preset customizations or gets a predefined preset)
        preset = event.to = event.preset = me.normalizePreset(preset);

        // No extra options, and the current one specified, or one that is identical in configuration: Do nothing to the UI.
        if (!Object.keys(options).length && me._viewPreset && (me._viewPreset === preset || me._viewPreset.equals(preset))) {
            return;
        }

        // This is set if the object is inside the Base constructor's configure method.
        initial = initial || me.isConfiguring;

        let centerDate = options.centerDate;

        if (initial || me.trigger('beforePresetChange', event) !== false) {
            me._viewPreset = preset;
            
            // Raise flag to prevent partner from changing view preset if one is in progress
            me._viewPresetChanging = true;

            // prefer to use displayDateFormat configured on the panel
            me.displayDateFormat = me.config.displayDateFormat || preset.displayDateFormat;

            if (timeAxis) {
                // None of this reconfiguring should cause a refresh
                me.suspendRefresh();

                // Timeaxis may already be configured (in case of sharing with the timeline partner), no need to reconfigure it
                if (!(initial && timeAxis.isConfigured)) {
                    const timeAxisCfg = {
                        weekStartDay : me.weekStartDay,
                        startTime    : me.startTime,
                        endTime      : me.endTime
                    };

                    if (initial) {
                        if (timeAxis.count === 0 || startDate) {
                            timeAxisCfg.startDate = startDate || new Date();
                            timeAxisCfg.endDate = endDate;
                        }
                    }
                    else {
                        // if startDate is provided we use it and the provided endDate
                        if (startDate) {
                            timeAxisCfg.startDate = startDate;
                            timeAxisCfg.endDate = endDate;

                            // if both dates are provided we can calculate centerDate for the viewport
                            if (!centerDate && endDate) {
                                // TODO: PORT infitieScroll stuff
                                // if (me.infiniteScroll && view.cachedScrollDate && view.cachedScrollDateIsCentered) {
                                //     centerDate = view.cachedScrollDate;
                                // } else {
                                centerDate = new Date((startDate.getTime() + endDate.getTime()) / 2);
                                //}
                            }

                            // when no start/end dates are provided we use the current timespan
                        }
                        else {
                            timeAxisCfg.startDate = timeAxis.startDate;
                            timeAxisCfg.endDate = endDate || timeAxis.endDate;

                            if (!centerDate) {
                                // TODO: PORT inifiteScroll stuff
                                // if (me.infiniteScroll && view.cachedScrollDate && view.cachedScrollDateIsCentered) {
                                //     centerDate = view.cachedScrollDate;
                                // } else {
                                // TODO: PORT needed?
                                //centerDate = me.getViewportCenterDateCached();
                                centerDate = me.viewportCenterDate;
                                //}
                            }
                        }
                    }

                    timeAxis.isConfigured = false;
                    timeAxis.viewPreset = preset;
                    timeAxis.reconfigure(timeAxisCfg, true);

                    me.timeAxisViewModel.reconfigure({
                        viewPreset : me.viewPreset,
                        headers    : preset.headers,

                        // This was hardcoded to 'bottom', so is now length - 1.
                        // Is this correct @mats?
                        columnLinesFor      : preset.headers.length - 1,
                        // TODO: PORT rowHeightHorizontal??
                        rowHeightHorizontal : me.readRowHeightFromPreset ? preset.rowHeight : me.rowHeight,
                        tickSize            : isHorizontal ? preset.tickWidth : preset.tickHeight || preset.tickWidth || 60
                    });

                    // Allow refresh to run after the reconfiguring
                    me.resumeRefresh();
                }

                // TODO: PORT vertical later
                // if (isVertical) {
                //     me.setColumnWidth(me.resourceColumnWidth || preset.resourceColumnWidth || 100, true);
                // }

                me.refresh();

                // if view is rendered and scroll is not disabled by "notScroll" option
                if (!options.notScroll && me.isPainted) {
                    // and we have centerDate to scroll to
                    if (centerDate) {
                        // remember the central date we scroll to (it gets reset after user scroll)
                        me.cachedCenterDate = centerDate;

                        let x = null;

                        // TODO: PORT vertical later
                        // if (isVertical) {
                        //     y = Math.max(Math.floor(view.getCoordinateFromDate(centerDate, true) - view.getViewContainerHeight() / 2), 0);
                        //     me.viewPresetActiveScroll = { top : y };
                        //     view.scrollVerticallyTo(y);
                        // } else {
                        x = Math.max(Math.floor(me.getCoordinateFromDate(centerDate, true) - me.timeAxisSubGrid.width / 2), 0);
                        me.viewPresetActiveScroll = { left : x };

                        // The horizontal scroll handler must not invalidate the cached center
                        // when this scroll event rolls round on the next frame.
                        me.scrollingToCenter = true;

                        me.scrollHorizontallyTo(x, false);

                        // Release the lock on scrolling invalidating the cached center.
                        me.setTimeout(() => {
                            me.scrollingToCenter = false;
                        }, 100);
                        //}

                        // if we don't have a central date to scroll at we reset scroll (this is bw compatible behavior)
                    }
                    else {
                        // TODO: PORT vertical later
                        // if (isHorizontal) {
                        me.scrollHorizontallyTo(0, false);
                        //} else {
                        //    view.scrollVerticallyTo(0);
                        //}
                    }
                }
            }

            me.trigger('presetChange', event);
        }
        
        me._viewPresetChanging = false;
    }

    //endregion

    // This does not need a className on Widgets.
    // Each *Class* which doesn't need 'b-' + constructor.name.toLowerCase() automatically adding
    // to the Widget it's mixed in to should implement thus.
    get widgetClass() {}
};
