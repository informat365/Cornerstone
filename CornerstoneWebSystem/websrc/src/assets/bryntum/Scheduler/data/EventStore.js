import AjaxStore from '../../Core/data/AjaxStore.js';
import EventStoreMixin from './mixin/EventStoreMixin.js';
import RecurringEventsMixin from './mixin/RecurringEventsMixin.js';
import EventModel from '../model/EventModel.js';

/**
 * @module Scheduler/data/EventStore
 */

/**
 * This is a class holding all the {@link Scheduler.model.EventModel events} to be rendered into a {@link Scheduler.view.Scheduler Scheduler}.
 * This class only accepts a model class inheriting from {@link Scheduler.model.EventModel}.
 *
 * @mixes Scheduler/data/mixin/EventStoreMixin
 * @extends Core/data/AjaxStore
 */
export default class EventStore extends RecurringEventsMixin(EventStoreMixin(AjaxStore)) {
    static get defaultConfig() {
        return {
            /**
             * CrudManager must load stores in the correct order. Lowest first.
             * @private
             */
            loadPriority : 100,
            /**
             * CrudManager must sync stores in the correct order. Lowest first.
             * @private
             */
            syncPriority : 200,
            modelClass   : EventModel,
            storeId      : 'events'
        };
    }

    construct(config) {
        super.construct(config, true);

        this.setupRecurringEvents();

        if (this.modelClass !== EventModel && !(this.modelClass.prototype instanceof EventModel)) {
            throw new Error('The model for the EventStore must subclass EventModel');
        }
    }

    /**
     * Appends a new record to the store
     * @param {Scheduler.model.EventModel} record The record to append to the store
     */
    append(record) {
        this.add(record);
    }
}
