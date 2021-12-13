import TimeSpan from './TimeSpan.js';
/**
 * @module Scheduler/model/ResourceTimeRangeModel
 */

/**
 * This class represent a single resource time range in your schedule. To style
 * the rendered elements, use the {@link Scheduler.model.TimeSpan#field-cls cls} field of the `TimeSpan` class, or use the {@link #field-timeRangeColor} field.
 *
 * @extends Scheduler/model/TimeSpan
 */
export default class ResourceTimeRangeModel extends TimeSpan {
    //region Fields

    static get fields() {
        return [
            /**
             * Id of the resource this time range is associated with
             * @field {String|Number} resourceId
             */
            'resourceId',

            /**
             * Controls this time ranges primary color, defaults to using current themes default time range color.
             * @field {String} timeRangeColor
             */
            'timeRangeColor'
        ];
    }

    static get relationConfig() {
        return [
            /**
             * The associated resource, retrieved using a relation to a ResourceStore determined by the value assigned
             * to `resourceId`. The relation also lets you access all time ranges on a resource through
             * `ResourceModel#timeRanges`.
             * @property {Scheduler.model.ResourceModel} resource
             */
            { relationName : 'resource', fieldName : 'resourceId', store : 'resourceStore', collectionName : 'timeRanges', nullFieldOnRemove : true }
        ];
    }

    //endregion

    // Used internally to differentiate between Event and ResourceTimeRange
    get isResourceTimeRange() {
        return true;
    }

    // To match EventModel API
    get resources() {
        return [this.resource];
    }
}
