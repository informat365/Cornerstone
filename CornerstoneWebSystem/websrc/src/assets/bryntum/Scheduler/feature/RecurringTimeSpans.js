import GridFeatureManager from '../../Grid/feature/GridFeatureManager.js';
import InstancePlugin from '../../Core/mixin/InstancePlugin.js';

/**
 * @module Scheduler/feature/RecurringTimeSpans
 */

/**
 * Class that adds recurring timespans functionality to the Scheduler.
 *
 * The main purpose of the class is generating occurrences of timespan models that should recur
 * for the displayed time range of the scheduler.
 * The feature tracks changes on the {@link #property-store} that contains timespan records
 * in order to propagate changes to all recurring record occurrences.
 * The feature also tracks the Scheduler visible date range to make sure it is properly populated with occurrences.
 *
 * This feature is **disabled** by default.
 *
 * @extends Core/mixin/InstancePlugin
 * @classtype recurringTimeSpans
 */
export default class RecurringTimeSpans extends InstancePlugin {

    static get $name() {
        return 'RecurringTimeSpans';
    }

    //region Config

    static get defaultConfig() {
        return {
            /**
             * Store to attach the feature to.
             * By default you don't need to provide the config since the store is taken from the scheduler.
             * @config {Object|Core.data.Store} [store]
             */
            store : undefined
        };
    }

    //endregion

    construct(plugInto, config) {
        this.isConstructing = true;

        super.construct(plugInto, config);

        this.isConstructing = false;
    }

    afterConstruct() {
        const me = this;

        // if feature is disabled or cannot function properly
        if (me.disabled || !me.isRecurrenceSupported) {
            me.disableRecurringTimeSpans();
        }
        else {
            me.enableRecurringTimeSpans();
        }
    }

    //region Init & destroy

    get disabled() {
        return super.disabled;
    }

    set disabled(disabled) {
        super.disabled = disabled;

        const me = this;

        // if we are not on construction step (that case is handled in construct() method)
        if (!me.isConstructing) {
            // if feature is disabled or cannot function properly
            if (disabled || !me.isRecurrenceSupported) {
                me.disableRecurringTimeSpans();
            }
            else {
                me.enableRecurringTimeSpans();
            }
        }
    }

    enableRecurringTimeSpans() {
        const { client, store } = this;

        if (client && store) {
            this.bindClient(client);
            this.bindStore(store);
            this.refreshOccurrences();
        }
    }

    disableRecurringTimeSpans() {
        this.bindClient(null);
        this.bindStore(null);

        if (this.store) {
            this.removeOccurrences();
        }
    }

    get trackingSuspended() {
        return Boolean(this._trackingSuspended);
    }

    suspendTracking() {
        if (isNaN(this._trackingSuspended)) {
            this._trackingSuspended = 0;
        }
        this._trackingSuspended++;
    }

    resumeTracking() {
        if (isNaN(this._trackingSuspended)) {
            this._trackingSuspended = 0;
        }
        else {
            this._trackingSuspended--;
        }
    }

    get client() {
        return super.client;
    }

    set client(client) {
        super.client = client;

        if (!this.isConstructing) {
            this.bindClient(client);
        }
    }

    /**
     * Setups event listeners to the provided scheduler panel or destroys existing listeners if `null` provided.
     * The method is called when setting {@link #property-client} property.
     * Override this to setup custom event listeners to the associated panel.
     * @param {Scheduler.view.Scheduler} [panel] Panel to listen to. Provide `null` or skip the argument to remove the current listeners.
     * @protected
     */
    bindClient(client) {
        this.bindTimeAxis(client && client.timeAxis);
    }

    /**
     * The timespan store associated with the feature.
     * @property {Core.data.Store}
     */
    set store(store) {
        const me        = this,
            isSupported = store && store.supportsRecurringTimeSpans;

        // the feature supports only stores having RecurringEventsMixin mixed in
        store = isSupported ? store : null;

        me._store = store;

        if (!me.isConstructing) {
            me.bindStore(store);

            // if feature is enabled and has everything it needs to work properly
            if (me.isRecurrenceSupported && !me.disabled) {
                me.enableRecurringTimeSpans();
            }
            else {
                me.disableRecurringTimeSpans();
            }
        }
    }

    get store() {
        return this._store;
    }

    // Indicates if the feature has everything set to work properly
    get isRecurrenceSupported() {
        const { store, startDate, endDate } = this;

        return Boolean(store && store.supportsRecurringTimeSpans && startDate && endDate);
    }

    /**
     * Setups event listeners to the provided timespan store (or destroys set listeners if `null` provided).
     * The method is called when setting {@link #property-store} property.
     * Override this to setup custom listeners to the store.
     * @param {Core.data.Store} [store] Timespan store to listen to. Provide `null` or skip the argument to remove the current listeners.
     * @protected
     */
    bindStore(store) {
        const me = this;

        me.storeDetacher && me.storeDetacher();

        if (store) {
            me.storeDetacher = store.on({
                'refresh'                           : me.onTimeSpansRefresh,
                'add'                               : me.onTimeSpanAdd,
                'update'                            : me.onTimeSpanUpdate,
                'remove'                            : me.onTimeSpanRemove,
                'delayedgenerateoccurrencesstart'   : me.onGenerateOccurrencesStart,
                'delayedgenerateoccurrencesend'     : me.onGenerateOccurrencesEnd,
                'delayedregenerateoccurrencesstart' : me.onGenerateOccurrencesStart,
                'delayedregenerateoccurrencesend'   : me.onGenerateOccurrencesEnd,
                thisObj                             : me
            });
        }
    }

    get timeAxis() {
        return this.client && this.client.timeAxis;
    }

    bindTimeAxis(timeAxis) {
        this.timeAxisDetacher && this.timeAxisDetacher();

        if (timeAxis) {
            this.timeAxisDetacher = timeAxis.on({
                'reconfigure' : this.onTimeAxisReconfigure,
                thisObj       : this
            });
        }
    }

    onTimeAxisReconfigure() {
        if (!this.trackingSuspended) {
            this.refreshOccurrences();
        }
    }

    get startDate() {
        return this.timeAxis && this.timeAxis.startDate;
    }

    get endDate() {
        return this.timeAxis && this.timeAxis.endDate;
    }

    onGenerateOccurrencesStart() {
        this.suspendTracking();
    }

    onGenerateOccurrencesEnd() {
        this.resumeTracking();
    }

    refreshOccurrences() {
        const { store, startDate, endDate } = this;

        if (store && startDate && endDate) {
            store.generateOccurrencesForTimeSpansBuffered(store.getRecurringTimeSpans(), startDate, endDate);
        }
    }

    removeOccurrences() {
        const { store } = this;

        if (store) {
            store.removeOccurrencesForAll();
        }
    }

    onTimeSpansRefresh({ source, action, records }) {
        if (action == 'dataset' && !this.trackingSuspended) {
            const recurringTimeSpans = records.filter(record => record.isRecurring);

            if (recurringTimeSpans.length) {
                // schedule occurrences generation
                source.generateOccurrencesForTimeSpansBuffered(recurringTimeSpans, this.startDate, this.endDate);
            }
        }
    }

    onTimeSpanAdd({ source, records, allRecords }) {
        if (!this.trackingSuspended) {
            const recurringTimeSpans = (allRecords || records).filter(record => record.isRecurring);

            if (recurringTimeSpans.length) {
                // schedule occurrences generation
                source.generateOccurrencesForTimeSpansBuffered(recurringTimeSpans, this.startDate, this.endDate);
            }
        }
    }

    onTimeSpanUpdate({ source, record, changes }) {
        if (!this.trackingSuspended && this.isRecurrenceRelatedFieldChange(record, changes)) {

            const { startDate, endDate }                   = this,
                { startDate : timeSpanStartDate, recurrence } = record;

            // the record is no longer recurring
            if (!recurrence) {
                source.removeOccurrencesForTimeSpans(record);
            }
            // If we have start & end dates and the recurrence intersects the range
            else if (startDate && endDate && timeSpanStartDate && (!recurrence.endDate || (recurrence.endDate >= startDate && timeSpanStartDate <= endDate))) {
                // schedule occurrences regeneration
                source.regenerateOccurrencesForTimeSpansBuffered(record, startDate, endDate);
            }
        }
    }

    onTimeSpanRemove({ source, allRecords, records }) {
        if (!this.trackingSuspended) {
            const recurringTimeSpans = (allRecords || records).filter(record => record.isRecurring);

            if (recurringTimeSpans.length) {
                source.removeOccurrencesForTimeSpans(recurringTimeSpans);
            }
        }
    }

    /**
     * The method restricts which field modifications should trigger timespan occurrences building.
     * By default any field change of a recurring timespan causes the rebuilding.
     * @param  {Scheduler.model.TimeSpan} timeSpan The modified timespan.
     * @param  {Object} toSet Object containing changed fields.
     * @return {Boolean} `True` if the fields modification should trigger the timespan occurrences rebuilding.
     * @protected
     */
    isRecurrenceRelatedFieldChange(timeSpan, toSet) {
        return timeSpan.isRecurring || 'recurrenceRule' in toSet;
    }

};

GridFeatureManager.registerFeature(RecurringTimeSpans, false, 'Scheduler');
