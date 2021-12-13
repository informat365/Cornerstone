import Base from '../../Core/Base.js';

/**
 * @module Scheduler/eventlayout/HorizontalLayout
 */

/**
 * Base class for HorizontalLayoutPack & HorizontalLayoutStack. Should not be used directly, instead specify
 * {@link Scheduler.view.mixin.SchedulerEventRendering#config-eventLayout} in Scheduler config (stack, pack or none):
 * @example
 * let scheduler = new Scheduler({
 *   eventLayout: 'stack'
 * });
 * @abstract
 * @private
 */
export default class HorizontalLayout extends Base {
    static get defaultConfig() {
        return {
            nbrOfBandsByResource        : {},
            bandIndexToPxConvertFn      : null,
            bandIndexToPxConvertThisObj : null
        };
    }

    clearCache(resource) {
        if (resource) {
            delete this.nbrOfBandsByResource[resource.id];
        }
        else {
            this.nbrOfBandsByResource = {};
        }
    }

    // Input:
    // 1. Resource record
    // 2. Array of Event models, or a function to call to receive such event records lazily
    getNumberOfBands(resource, resourceEventsOrFn) {
        const nbrOfBandsByResource = this.nbrOfBandsByResource;

        if (nbrOfBandsByResource.hasOwnProperty(resource.id)) {
            return nbrOfBandsByResource[resource.id];
        }

        const
            resourceEvents = typeof resourceEventsOrFn === 'function' ? resourceEventsOrFn() : resourceEventsOrFn,
            eventsData     = resourceEvents.map(event => ({
                start : event.startDate,
                end   : event.endDate,
                event : event
            }));

        return this.applyLayout(eventsData, resource);
    }

    // TODO DOC
    applyLayout(events, resource) {
        const
            rowEvents = events.slice(),
            // return a number of bands required
            newNbrBands = this.layoutEventsInBands(rowEvents);

        return this.nbrOfBandsByResource[resource.id] = newNbrBands;
    }
}
