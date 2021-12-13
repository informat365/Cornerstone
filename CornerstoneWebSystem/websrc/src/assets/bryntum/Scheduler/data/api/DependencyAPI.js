import Base from '../../../Core/Base.js';

/**
 * @module Scheduler/data/api/DependencyAPI
 */

/**
 * Dependency model data API mixin
 *
 * The mixin should be mixed alongside with other API mixins, because it might rely on them.
 *
 * @mixin
 */
export default Target => class DependencyAPI extends (Target || Base) {

    getDependencySourceEvent({ dependency, eventStore }) {
        return dependency.sourceEvent;
    }

    getDependencyTargetEvent({ dependency, eventStore }) {
        return dependency.targetEvent;
    }

    getDependencySourceAssignments({ dependency, assignmentStore }) {
        const event = dependency.sourceEvent;
        return event ? this.getEventAssignments({ event, assignmentStore }) : [];
    }

    getDependencyTargetAssignments({ dependency, assignmentStore }) {
        const event = dependency.targetEvent;
        return event ? this.getEventAssignments({ event, assignmentStore }) : [];
    }

    getDependencyAssignments({ dependency, assignmentStore }) {
        [].concat(
            this.getDependencySourceAssignments({ dependency, assignmentStore }),
            this.getDependencyTargetAssignments({ dependency, assignmentStore })
        );
    }

    getDependencyAssignmentsAsFromToArray({ dependency, assignmentStore }) {
        const
            fromAssignments = this.getDependencySourceAssignments({ dependency, assignmentStore }),
            toAssignments   = this.getDependencyTargetAssignments({ dependency, assignmentStore });

        return fromAssignments.reduce((assignments, from) => {
            return toAssignments.reduce((assignments, to) => {
                assignments.push({ from, to });
                return assignments;
            }, assignments);
        }, []);
    }

    isDependencySourceAssignment({ dependency, assignment }) {
        return dependency.from === assignment.eventId;
    }

    isDependnecyTargetAssignment({ dependency, assignment }) {
        return dependency.to === assignment.eventId;
    }

    isValidDependency({ sourceEvent, targetEvent, type, dependencyStore }) {
        return dependencyStore.isValidDependency(
            sourceEvent.id,
            targetEvent.id,
            type
        );
    }

    createDependency({ sourceEvent, targetEvent, type, fromSide, toSide, dependencyStore }) {
        return dependencyStore.add({
            from : sourceEvent.id,
            to   : targetEvent.id,
            type,
            fromSide,
            toSide
        })[0];
    }
};
