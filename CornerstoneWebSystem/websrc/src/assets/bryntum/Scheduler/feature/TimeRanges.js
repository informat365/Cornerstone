import AbstractTimeRanges from './AbstractTimeRanges.js';
import TimeSpan from '../model/TimeSpan.js';
import GridFeatureManager from '../../Grid/feature/GridFeatureManager.js';
import DateHelper from '../../Core/helper/DateHelper.js';

/**
 * @module Scheduler/feature/TimeRanges
 */

/**
 * Feature that renders global ranges of time in the timeline. Use this feature to visualize a `range` like a 1 hr lunch or some important point in time
 * (a `line`, i.e. a range with 0 duration). This feature can also show a current time indicator if you set {@link #config-showCurrentTimeLine} to true. To style
 * the rendered elements, use the {@link Scheduler.model.TimeSpan#field-cls cls} field of the `TimeSpan` class.
 *
 * Each time range is represented by an instances of {@link Scheduler.model.TimeSpan}, held in a simple {@link Core.data.Store}.
 * If {@link Scheduler.data.CrudManager} is specified for the scheduler, the {@link #config-store} will be added at the end of the CrudManager
 * {@link Scheduler.crud.AbstractCrudManager#property-stores} collection.
 *
 * This feature is **disabled** by default
 *
 * @extends Scheduler/feature/AbstractTimeRanges
 * @classtype timeRanges
 * @demo Scheduler/timeranges
 * @externalexample scheduler/TimeRanges.js
 */
export default class TimeRanges extends AbstractTimeRanges {
    //region Config

    static get $name() {
        return 'TimeRanges';
    }

    static get defaultConfig() {
        return {
            /**
             * Store that holds timeRanges (using the {@link Scheduler.model.TimeSpan} model or subclass thereof).
             * A store will be automatically created if none is specified
             * @config {Object|Core.data.Store}
             */
            store : {
                modelClass : TimeSpan,
                storeId    : 'timeRanges'
            },

            updateCurrentTimeLineInterval : 10000,

            /**
             * The date format to show in the header for the current time line, see {@link #config-showCurrentTimeLine}
             * @config {String}
             * @default
             */
            currentDateFormat : 'HH:mm',
            /**
             * Range definitions (data to {@link Scheduler.model.TimeSpan} models). Will be added to store.
             * @config {Scheduler.model.TimeSpan[]|Object[]}
             */
            timeRanges        : null,

            /**
             * Show a line indicating current time
             * @config {Boolean}
             * @default
             */
            showCurrentTimeLine : false

        };
    }

    //endregion

    //region Init & destroy

    construct(client, config) {
        const me = this;

        super.construct(client, config);

        if (!client._timeRangesExposed) {
            // expose getter/setter for timeRanges on scheduler/gantt
            Object.defineProperty(client, 'timeRanges', {
                get : () => me.store.records,
                set : timeRanges => me.store.data = timeRanges
            });
            client._timeRangesExposed = true;
        }
    }

    startConfigure(config) {
        const { client } = this;

        // If the client's project has a timeRangeStore, we must use that
        if (client.project) {
            const store = client.project.timeRangeStore;

            if (store) {
                config.store = store;
            }
        }
    }

    //endregion

    //region Current time line

    initCurrentTimeLine() {
        const me  = this,
            now = new Date();

        if (me.currentTimeLine || !me.showCurrentTimeLine) {
            return;
        }

        me.currentTimeLine = new me.store.modelClass({
            'id'      : 'currentTime',
            cls       : 'b-sch-current-time',
            startDate : now,
            name      : DateHelper.format(now, me.currentDateFormat)
        });

        me.updateCurrentTimeLine = me.updateCurrentTimeLine.bind(me);

        me.currentTimeInterval = me.setInterval(me.updateCurrentTimeLine, me.updateCurrentTimeLineInterval);

        if (me.client.isPainted) {
            me.renderRanges();
        }
    }

    updateCurrentTimeLine() {
        const me = this;

        me.currentTimeLine.startDate = new Date();
        me.currentTimeLine.name      = DateHelper.format(me.currentTimeLine.startDate, me.currentDateFormat);
        me.onStoreChanged({ action : 'update', record : me.currentTimeLine, changes : {} });
    }

    hideCurrentTimeLine() {
        const me = this;

        if (!me.currentTimeLine) {
            return;
        }

        me.clearInterval(me.currentTimeInterval);
        me.currentTimeLine = null;

        if (me.client.isPainted) {
            me.renderRanges();
        }
    }

    renderRanges() {
        const me = this;

        super.renderRanges();

        if (me.showCurrentTimeLine && !me.disabled) {
            me.renderRange(me.currentTimeLine, true);
        }
    }

    /**
     * Get/set the current time line display state
     * @property {boolean}
     */
    get showCurrentTimeLine() {
        return this._showCurrentTimeLine;
    }

    set showCurrentTimeLine(show) {
        this._showCurrentTimeLine = show;

        if (show) {
            this.initCurrentTimeLine();
        }
        else {
            this.hideCurrentTimeLine();
        }
    }

    //endregion

    //region Menu items

    /**
     * Adds a menu item to show/hide current time line.
     * @param column Column
     * @param items Menu items
     * @returns {Object[]} Menu items
     * @private
     */
    getHeaderMenuItems(column, items) {
        const me = this;

        if (!items.some(item => item.isCurrentTimeline) && column.type === 'timeAxis') {
            items.push({
                isCurrentTimeline : true,
                text              : me.L('showCurrentTimeLine'),
                checked           : me.showCurrentTimeLine,
                onToggle          : ({ checked }) => {
                    me.showCurrentTimeLine = checked;
                }
            });
        }
    }

    //endregion

    //region Disable

    /**
     * Get/set the features disabled state
     * @property {Boolean}
     */
    get disabled() {
        return this._disabled;
    }

    set disabled(disabled) {
        this._disabled = disabled;

        if (this.client.isPainted) {
            this.renderRanges();
        }
    }

    /**
     * Returns the {@link Core.data.Store store} used by this feature
     * @property {Core.data.Store}
     */
    get store() {
        return this._store;
    }

    set store(store) {
        const
            me     = this,
            client = me.client;

        if (!store.storeId) {
            store.storeId = 'timeRanges';
        }

        super.store = store;

        // timeRanges can be set on scheduler/gantt or feature, for convenience. Should only be processed by the TimeRanges
        // and not any subclasses
        if (client.timeRanges && !client._timeRangesExposed) {
            me._store.add(client.timeRanges);
            delete client.timeRanges;
        }

        if (me.timeRanges) {
            me._store.add(me.timeRanges);
            delete me.timeRanges;
        }
    }

    //endregion
}

GridFeatureManager.registerFeature(TimeRanges, false, ['Scheduler', 'Gantt']);
