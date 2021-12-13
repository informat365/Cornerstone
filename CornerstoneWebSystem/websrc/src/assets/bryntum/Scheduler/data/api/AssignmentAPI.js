import Base from '../../../Core/Base.js';

/**
 * @module Scheduler/data/api/AssignmentAPI
 */

/**
 * Assignment model data API mixin
 *
 * The mixin should be mixed alongside with other API mixins, because it might rely on them.
 *
 * @mixin
 */
export default Target => class AssignmentAPI extends (Target || Base) {

    isAssignmentEventAssigned({ assignment }) {
        return !!assignment.event;
    }

    isAssignmentResourceAssigned({ assignment }) {
        return !!assignment.resource;
    }

    isAssignmentAssigned({ assignment }) {
        return !!assignment.event && !!assignment.resource;
    }

    isAssignmentForResource({ assignment, resource, resourceStore }) {
        return assignment.resource === resource;
    }

    getAssignmentResource({ assignment, resourceStore }) {
        return assignment.resource;
    }

    getAssignmentEvent({ assignment, eventStore }) {
        return assignment.event;
    }

    getAssignmentDependencies({ assignment, dependencyStore }) {
        const event = assignment.event;
        return event ? this.getEventDependencies({ event, dependencyStore }) : [];
    }

    addAssignment({ event, resource, assignmentStore, assignmentConfig }) {
        return {
            assignment : assignmentStore.add(Object.assign({}, assignmentConfig, {
                eventId    : event.id,
                resourceId : resource.id
            }))[0]
        };
    }
};
