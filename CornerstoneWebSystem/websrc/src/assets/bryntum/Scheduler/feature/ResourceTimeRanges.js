import GridFeatureManager from '../../Grid/feature/GridFeatureManager.js';
import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import ResourceTimeRangeStore from '../data/ResourceTimeRangeStore.js';

/**
 * @module Scheduler/feature/ResourceTimeRanges
 */

/**
 * Feature that draws resource time ranges, shaded areas displayed behind events. These zones are similar to events in
 * that they have a start and end date but different in that they do not take part in the event layout and they always
 * occupy full row height.
 *
 * Each time range is represented by an instances of {@link Scheduler.model.ResourceTimeRangeModel}, held in a
 * {@link Scheduler.data.ResourceTimeRangeStore}. Currently the they are readonly UI-wise, but can be manipulated on
 * the data level. To style the rendered elements, use the {@link Scheduler.model.TimeSpan#field-cls cls} field or use the {@link Scheduler.model.ResourceTimeRangeModel#field-timeRangeColor} field.
 *
 * Data can be provided either using the {@link #config-store} config:
 *
 * ```javascript
 * new Scheduler({
 *   features :  {
 *       resourceTimeRanges : {
 *          store : new Scheduler.data.ResourceTimeRangeStore({
 *              readUrl : './resourceTimeRanges/'
 *          })
 *       }
 *   }
 * })
 * ```
 *
 * or the `resourceTimeRanges` config on the Scheduler config object:
 *
 * ```javascript
 * new Scheduler({
 *   features :  {
 *       resourceTimeRanges : true
 *   },
 *
 *   // Data specified directly on the Scheduler instance
 *   resourceTimeRanges : [
 *     // Either specify startDate & endDate or startDate & duration when defining a range
 *     { startDate : new Date(2019,0,1), endDate : new Date(2019,0,3), name : 'Occupied', timeRangeColor : 'red' },
 *     { startDate : new Date(2019,0,3), duration : 2, durationUnit : 'd', name : 'Available' },
 *   ]
 * })
 * ```
 *
 * This feature is **disabled** by default
 *
 * @extends Core/mixin/InstancePlugin
 * @demo Scheduler/resourcetimeranges
 * @externalexample scheduler/ResourceTimeRanges.js
 */
export default class ResourceTimeRanges extends InstancePlugin {
    //region Config

    static get $name() {
        return 'ResourceTimeRanges';
    }

    static get defaultConfig() {
        return {
            idPrefix : 'resourcetimerange',
            rangeCls : 'b-sch-resourcetimerange',

            /**
             * Store that holds resource time ranges (using ResourceTimeRangeModel or subclass thereof). A store will be
             * automatically created if none is specified
             * @config {Scheduler.data.ResourceTimeRangeStore}
             */
            store : false,

            /**
             * Time range definitions (data to ResourceTimeRangeModels). Will be added to store. Can also be specified
             * on Scheduler for convenience
             * @config {Scheduler.model.ResourceTimeRangeModel[]|Object[]}
             */
            resourceTimeRanges : null
        };
    }

    // Plugin configuration. This plugin chains some of the functions in Grid.
    static get pluginConfig() {
        return {
            chain : ['getEventsToRender', 'onEventDataGenerated', 'noFeatureElementsInAxis']
        };
    }

    // Let Scheduler know if we have ResourceTimeRanges in view or not
    noFeatureElementsInAxis() {
        const { timeAxis } = this.scheduler;
        return !this.store.storage.values.some(t => timeAxis.isTimeSpanInAxis(t));
    }

    //endregion

    //region Init

    construct(scheduler, config) {
        const me = this;

        me.scheduler = scheduler;

        super.construct(scheduler, config);

        // expose getter/setter for resourceTimeRanges on scheduler
        Object.defineProperty(scheduler, 'resourceTimeRanges', {
            get : () => me.store.records,
            set : resourceTimeRanges => me.store.data = resourceTimeRanges
        });
    }

    doDisable(disable) {
        if (this.client.isPainted) {
            this.client.refresh();
        }

        super.doDisable(disable);
    }

    /**
     * Called during construction to do product specific store setup
     * @private
     */
    set store(store) {
        const me       = this,
            { scheduler } = me;

        me._store = store = store || new ResourceTimeRangeStore();

        if (!scheduler.resourceTimeRangeStore) {
            scheduler.resourceTimeRangeStore = store;
        }

        // ResourceZones can be set on scheduler or feature, for convenience
        if (scheduler.resourceTimeRanges) {
            store.add(scheduler.resourceTimeRanges);
            delete scheduler.resourceTimeRanges;
        }

        // Link to schedulers resourceStore if not already linked to one
        if (!store.resourceStore) {
            store.resourceStore = scheduler.resourceStore;
        }

        me.storeDetacher && me.storeDetacher();

        me.storeDetacher = me.store.on({
            change  : me.onStoreChange,
            thisObj : me
        });
    }

    get store() {
        return this._store;
    }

    //endregion

    // Called on render of resources events to get events to render. Add any ranges
    // (chained function from Scheduler)
    getEventsToRender(resource, events) {
        if (resource.timeRanges && resource.timeRanges.length && !this.disabled) {
            events.push(...resource.timeRanges);
        }
        return events;
    }

    // Called for each event during render, allows manipulation of render data. Adjust any resource time ranges
    // (chained function from Scheduler)
    onEventDataGenerated(renderData) {
        const
            me = this,
            record = renderData.event || renderData.eventRecord; // Differs by mode

        if (record.isResourceTimeRange) {
            if (me.scheduler.isVertical) {
                renderData.width = me.scheduler.resourceColumnWidth;
            }
            else {
                renderData.top = 0;
                // Avoid colliding ids by using a prefix
                renderData.id = `${me.scheduler.id}-${me.idPrefix}-${record.id}`;
            }

            // Flag that we should fill entire row/col
            renderData.fillSize = true;
            // Needed for caching
            renderData.eventId = `${me.idPrefix}-${record.id}`;
            // Add our own cls
            renderData.wrapperCls[me.rangeCls] = 1;
            renderData.wrapperCls[`b-sch-color-${record.timeRangeColor}`] = record.timeRangeColor;
            // Add label
            renderData.body = document.createDocumentFragment();
            renderData.body.textContent = record.name;
        }
    }

    // Called when a ResourceTimeRangeModel is manipulated, relays to Scheduler#onInternalEventStoreChange which updates to UI
    onStoreChange(event) {
        this.scheduler.onInternalEventStoreChange(event);
    }
}

// No feature based styling needed, do not add a cls to Scheduler
ResourceTimeRanges.featureClass = '';

GridFeatureManager.registerFeature(ResourceTimeRanges, false, 'Scheduler');
