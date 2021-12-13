import Base from '../../../Core/Base.js';

/**
 * @module Scheduler/data/api/EventAPI
 */

/**
 * Event model data API mixin
 *
 * The mixin should be mixed alongside with other API mixins, because it might rely on them.
 *
 * @mixin
 */
export default Target => class EventAPI extends (Target || Base) {

    addEventToResource({ event, resource, eventStore, assignmentStore }) {
        event = eventStore.add(event)[0];

        const { assignment } = this.addAssignment({ event, resource, assignmentStore });

        return {
            event,
            assignment : assignment !== event ? assignment : null
        };
    }

    getEventDependencies({ event, dependencyStore }) {
        return dependencyStore.getEventDependencies(event);
    }

    getEventAssignments({ event, assignmentStore }) {
        return event.assignments || [];
    }

    getEventResource({ event, resourceStore }) {
        return event.resource;
    }

    isEventAssignedToResource({ event, resource, resourceStore, assignmentStore }) {
        let result = false;

        if (!assignmentStore) {
            result = event.resource === resource;
        }
        else {
            result = this.getEventAssignments({ event, assignmentStore }).some(assignment => this.isAssignmentForResource({ assignment, resource, resourceStore }));
        }

        return result;
    }

    assignEventToResource({ event, resource, assignmentStore }) {
        let assignment;

        if (assignmentStore) {
            assignment = assignmentStore.add({ eventId : event.id, resourceId : resource.id })[0];
        }
        else {
            event.resourceId = resource.id;
        }

        return assignment || event;
    }
};
