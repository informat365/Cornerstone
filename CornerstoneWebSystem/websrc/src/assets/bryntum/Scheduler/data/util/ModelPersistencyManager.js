import Base from '../../../Core/Base.js';

/**
 * @module Scheduler/data/util/ModelPersistencyManager
 */

/**
 * This class manages model persistency, it listens to model stores' beforesync event and removes all non persistable
 * records from sync operation. The logic has meaning only for CRUD-less sync operations.
 *
 * @private
 */
export default class ModelPersistencyManager extends Base {
    // region Event attachers

    set eventStore(newEventStore) {
        const me = this;

        me.eventStoreDetacher && me.eventStoreDetacher();
        me._eventStore = newEventStore;

        if (newEventStore && newEventStore.autoCommit) {
            me.eventStoreDetacher = newEventStore.on({
                beforecommit : me.onEventStoreBeforeSync,
                thisObj      : me,
                detachable   : true,
                // Just in case
                prio         : 100
            });
        }
    }

    get eventStore() {
        return this._eventStore;
    }

    set resourceStore(newResourceStore) {
        const me = this;

        me.resourceStoreDetacher && me.resourceStoreDetacher();
        me._resourceStore = newResourceStore;

        if (newResourceStore && newResourceStore.autoCommit) {
            me.resourceStoreDetacher = newResourceStore.on({
                beforecommit : me.onResourceStoreBeforeSync,
                thisObj      : me,
                detachable   : true,
                // Just in case
                prio         : 100
            });
        }
    }

    get resourceStore() {
        return this._resourceStore;
    }

    set assignmentStore(newAssignmentStore) {
        const me = this;

        me.assignmentStoreDetacher && me.assignmentStoreDetacher();
        me._assignmentStore = newAssignmentStore;

        if (newAssignmentStore && newAssignmentStore.autoSync) {
            me.assignmentStoreDetacher = newAssignmentStore.on({
                beforecommit : me.onAssignmentStoreBeforeSync,
                thisObj      : me,
                detachable   : true,
                // Just in case
                prio         : 100
            });
        }
    }

    get assignmentStore() {
        return this._assignmentStore;
    }

    set dependencyStore(newDependencyStore) {
        const me = this;

        me.dependencyStoreDetacher && me.dependencyStoreDetacher();
        me._dependencyStore = newDependencyStore;

        if (newDependencyStore && newDependencyStore.autoSync) {
            me.dependencyStoreDetacher = newDependencyStore.on({
                beforecommit : me.onDependencyStoreBeforeSync,
                thisObj      : me,
                detachable   : true,
                // Just in case
                prio         : 100
            });
        }
    }

    get dependencyStore() {
        return this._dependencyStore;
    }

    // endregion

    // region Event handlers

    onEventStoreBeforeSync({changes}) {
        const me = this;
        me.removeNonPersistableRecordsToCreate(changes);
        return me.shallContinueSync(changes);
    }

    onResourceStoreBeforeSync({changes}) {
        const me = this;
        me.removeNonPersistableRecordsToCreate(changes);
        return me.shallContinueSync(changes);
    }

    onAssignmentStoreBeforeSync({changes}) {
        const me = this;
        me.removeNonPersistableRecordsToCreate(changes);
        return me.shallContinueSync(changes);
    }

    onDependencyStoreBeforeSync({changes}) {
        const me = this;
        me.removeNonPersistableRecordsToCreate(changes);
        return me.shallContinueSync(changes);
    }

    // endregion

    // region Management rules

    removeNonPersistableRecordsToCreate(changes) {
        let recordsToCreate = changes.added || [],
            r, i;

        // We remove from the array we iterate thus we iterate from end to start
        for (i = recordsToCreate.length - 1; i >= 0; --i) {
            r = recordsToCreate[i];
            if (!r.isPersistable) {
                recordsToCreate.splice(recordsToCreate.indexOf(r), 1);
            }
        }

        // Prevent empty create request
        if (recordsToCreate.length === 0) {
            changes.added.length = 0;
        }
    }

    shallContinueSync(options) {
        return Boolean((options.added && options.added.length > 0) ||
            (options.modified && options.modified.length > 0) ||
            (options.removed && options.removed.length > 0));
    }

    // endregion
}
