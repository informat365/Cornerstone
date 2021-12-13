// @tag dependencies

import AjaxStore from '../../Core/data/AjaxStore.js';
import DependencyModel from '../model/DependencyModel.js';
import Model from '../../Core/data/Model.js';

/**
 * @module Scheduler/data/DependencyStore
 */

/**
 * A class representing a collection of dependencies between events in the {@link Scheduler.data.EventStore}.
 * Contains a collection of {@link Scheduler.model.DependencyModel} records.
 *
 * @extends Core/data/AjaxStore
 */
export default class DependencyStore extends AjaxStore {
    static get defaultConfig() {
        return {
            /**
             * CrudManager must load stores in the correct order. Lowest first.
             * @private
             */
            loadPriority : 400,
            /**
             * CrudManager must sync stores in the correct order. Lowest first.
             * @private
             */
            syncPriority : 400,
            modelClass   : DependencyModel,
            storeId      : 'dependencies'
        };
    }

    //region Init & destroy

    doDestroy() {
        const me = this;
        me.eventStoreDetacher && me.eventStoreDetacher();
        super.doDestroy();
    }

    //endregion

    //region Stores

    /**
     * Get/set the associated event store instance.
     *
     * @property {Scheduler.data.EventStore}
     */
    get eventStore() {
        return this._eventStore;
    }

    set eventStore(eventStore) {
        const me       = this,
            oldStore = me._eventStore;

        me._eventStore = eventStore;

        if (eventStore) {
            me.attachToEventStore(eventStore);

            // If store is assigned after configuration we need to init relations
            if (!me.isConfiguring) {
                me.initRelations(true);
            }
        }

        if ((oldStore || eventStore) && oldStore !== eventStore) {
            /**
             * Fires when a new event store is set via the {@link #property-eventStore} property.
             * @event eventstorechange
             * @param {Scheduler.data.DependencyStore} this
             * @param {Scheduler.data.EventStore} newEventStore
             * @param {Scheduler.data.EventStore} oldEventStore
             */
            me.trigger('eventStoreChange', { newEventStore : eventStore, oldEventStore : oldStore });
        }
    }

    attachToEventStore(eventStore) {
        const me = this;

        me.eventStoreDetacher && me.eventStoreDetacher();

        me.eventStoreDetacher = eventStore.on({
            'remove'   : me.onEventRemove,
            thisObj    : me,
            detachable : true
        });
    }

    //endregion

    onEventRemove({ records, isMove = false, isCollapse = false }) {
        if (!isMove && !isCollapse) {
            records.forEach(record => {
                // traversing in a flat structure will only call fn on self, no need to handle tree case differently
                record.traverse(eventRecord => this.removeEventDependencies(eventRecord, false));
            });
        }
    }

    // TODO: document
    reduceEventDependencies(event, reduceFn, result, flat = true, depsGetterFn) {
        // const me       = this,
        //     relation = me.relationCache;

        depsGetterFn = depsGetterFn || (event => {
            return  this.getEventDependencies(event);

            // const eventId      = DependencyModel.asId(event),
            //     sourceEvents = (relation.sourceEvent && relation.sourceEvent[eventId]) || [],
            //     targetEvents = (relation.targetEvent && relation.targetEvent[eventId]) || [];
            //
            // return sourceEvents.concat(targetEvents);

            // return me.eventDependencyCache.get(event, () =>
            //     // Full scan, but cache makes everything possible to avoid it
            //     me.records.filter(dependency =>
            //         dependency.to == eventId || dependency.from == eventId
            //     )
            // );
        });

        event = Array.isArray(event) ? event : [event];

        event.reduce((result, event) => {
            if (event.children && !flat) {
                event.traverse(evt => {
                    result = depsGetterFn(evt).reduce(reduceFn, result);
                });
            }
            else {
                result = depsGetterFn(event).reduce(reduceFn, result);
            }
        }, result);

        return result;
    }

    // TODO: document
    reduceEventPredecessors(event, reduceFn, result, flat) {
        const me = this;

        return me.reduceEventDependencies(event, reduceFn, result, flat, evt => {
            // const eventId = Model.asId(evt);
            // return me.eventDependencyCache.getPredecessors(evt, () =>
            //     // Full scan, but cache makes everything possible to avoid it
            //     me.records.filter(dependency =>
            //         dependency.to == eventId
            //     )
            // );
        });
    }

    // TODO: document
    reduceEventSuccessors(event, reduceFn, result, flat) {
        const me = this;

        return me.reduceEventDependencies(event, reduceFn, result, flat, evt => {
            // const eventId = Model.asId(evt);
            // return me.eventDependencyCache.getSuccessors(evt, () =>
            //     // Full scan, but cache makes everything possible to avoid it
            //     me.records.filter(dependency =>
            //         dependency.getSourceId() == eventId
            //     )
            // );
        });
    }

    // TODO: document
    mapEventDependencies(event, fn, filterFn, flat, depsGetterFn) {
        return this.reduceEventDependencies(event, (result, dependency) => {
            filterFn(dependency) && result.push(dependency);
            return result;
        }, [], flat, depsGetterFn);
    }

    // TODO: document
    mapEventPredecessors(event, fn, filterFn, flat) {
        return this.reduceEventPredecessors(event, (result, dependency) => {
            filterFn(dependency) && result.push(dependency);
            return result;
        }, [], flat);
    }

    // TODO: document
    mapEventSuccessors(event, fn, filterFn, flat) {
        return this.reduceEventSuccessors(event, (result, dependency) => {
            filterFn(dependency) && result.push(dependency);
            return result;
        }, [], flat);
    }

    /**
     * Returns all dependencies for a certain event (both incoming and outgoing)
     *
     * @param {Scheduler.model.EventModel} event
     * @param {Boolean} [flat]
     * @return {Scheduler.model.DependencyModel[]}
     */
    getEventDependencies(event, flat = false) {
        return [].concat(event.predecessors || [], event.successors || []);
    }

    /**
     * Returns all incoming dependencies of the given event
     *
     * @param {Scheduler.model.EventModel} event
     * @param {Boolean} [flat]
     * @return {Scheduler.model.DependencyModel[]}
     */
    getEventPredecessors(event, flat = false) {
        //return this.mapEventPredecessors(event, o => o, o => true, flat);
        return event.predecessors;
    }

    /**
     * Returns all outcoming dependencies of a event
     *
     * @param {Scheduler.model.EventModel} event
     * @param {Boolean} [flat]
     * @return {Scheduler.model.DependencyModel[]}
     */
    getEventSuccessors(event, flat = false) {
        //return this.mapEventSuccessors(event, o => o, o => true, flat);
        return event.successors;
    }

    getUnique(array) {
        return [...new Set(array)];
    }

    // TODO: document
    removeEventDependencies(event, flat) {
        const me           = this,
            dependencies = me.getEventDependencies(event, flat);

        dependencies.length && me.remove(me.getUnique(dependencies));
    }

    // TODO: document
    removeEventPredecessors(event, flat) {
        const me           = this,
            dependencies = me.getEventPredecessors(event, flat);

        dependencies.length && me.remove(me.getUnique(dependencies));
    }

    // TODO: document
    removeEventSuccessors(event, flat) {
        const me           = this,
            dependencies = me.getEventSuccessors(event, flat);

        dependencies.length && me.remove(me.getUnique(dependencies));
    }

    getBySourceTargetId(key) {
        //TODO: in original code this uses a keymap
        return this.records.find(r =>
            key = DependencyStore.makeDependencySourceTargetCompositeKey(r.from, r.to)
        );
    }

    /**
     * Returns dependency model instance linking tasks with given ids. The dependency can be forward (from 1st
     * task to 2nd) or backward (from 2nd to 1st).
     *
     * @param {Scheduler.model.EventModel|String} sourceEvent 1st event
     * @param {Scheduler.model.EventModel|String} targetEvent 2nd event
     * @return {Scheduler.model.DependencyModel}
     */
    getDependencyForSourceAndTargetEvents(sourceEvent, targetEvent) {
        // NOTE: In case this will not work switch to cache get and linear search
        const me = this;

        sourceEvent = Model.asId(sourceEvent);
        targetEvent = Model.asId(targetEvent);

        return me.getBySourceTargetId(DependencyStore.makeDependencySourceTargetCompositeKey(sourceEvent, targetEvent));
    }

    /**
     * Returns a dependency model instance linking given events if such dependency exists in the store.
     * The dependency can be forward (from 1st event to 2nd) or backward (from 2nd to 1st).
     *
     * @param {Scheduler.model.EventModel|String} sourceEvent
     * @param {Scheduler.model.EventModel|String} targetEvent
     * @return {Scheduler.model.DependencyModel}
     */
    getEventsLinkingDependency(sourceEvent, targetEvent) {
        const me = this;
        return me.getDependencyForSourceAndTargetEvents(sourceEvent, targetEvent) ||
            me.getDependencyForSourceAndTargetEvents(targetEvent, sourceEvent);
    }

    /**
     * Validation method used to validate a dependency. Override and return `true` to indicate that an
     * existing dependency (or a new dependency being created) between two tasks is valid.
     *
     * @param {Scheduler.model.DependencyModel|Number|String} dependencyOrFromId The dependency model or from event id
     * @param {Number|String} [toId] To event id if the first parameter is not a dependency model instance
     * @param {Number} [type] Dependency {@link Scheduler.model.DependencyBaseModel#property-Type-static}  if the first parameter is not a dependency model instance.
     * @return {Boolean}
     */
    isValidDependency(dependencyOrFromId, toId, type) {
        if (arguments.length === 1) {
            type = dependencyOrFromId.type;
            toId = dependencyOrFromId.to;
            dependencyOrFromId = dependencyOrFromId.from;
        }

        return dependencyOrFromId != null && toId != null && dependencyOrFromId !== toId;
    }

    /**
     * Returns all dependencies highlighted with the given CSS class
     *
     * @param {String} cls
     * @return {Scheduler.model.DependencyBaseModel[]}
     */
    getHighlightedDependencies(cls) {
        return this.records.reduce((result, dep) => {
            if (dep.isHighlightedWith(cls)) result.push(dep);
            return result;
        }, []);
    }

    static makeDependencySourceTargetCompositeKey(from, to) {
        return `source(${from})-target(${to})`;
    }

    //region Product neutral

    getTimeSpanDependencies(record) {
        return this.getEventDependencies(record);
    }

    //endregion
}
