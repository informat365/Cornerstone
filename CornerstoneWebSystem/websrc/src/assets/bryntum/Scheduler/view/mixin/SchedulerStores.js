import Base from '../../../Core/Base.js';
import Store from '../../../Core/data/Store.js';
import DateHelper from '../../../Core/helper/DateHelper.js';
import CrudManager from '../../data/CrudManager.js';
import EventStore from '../../data/EventStore.js';
import ResourceStore from '../../data/ResourceStore.js';
import AssignmentStore from '../../data/AssignmentStore.js';
import GlobalEvents from '../../../Core/GlobalEvents.js';
import '../../data/DependencyStore.js';

/**
 * @module Scheduler/view/mixin/SchedulerStores
 */

const MAX_VALUE = Math.MAX_SAFE_INTEGER || Math.pow(2, 53) - 1,
    checkResources = {
        add    : 1,
        update : 1,
        remove : 1,
        filter : 1
    };

/**
 * Functions for store assignment and store event listeners
 *
 * @mixin
 */
export default Target => class SchedulerStores extends (Target || Base) {
    //region Default config

    static get defaultConfig() {
        return {
            /**
             * The {@link Scheduler.data.EventStore} holding the events to be rendered into the scheduler (required)
             * @config {Scheduler.data.EventStore}
             * @category Data
             */
            eventStore : null,

            /**
             * The {@link Scheduler.data.ResourceStore} holding the resources to be rendered into the scheduler (required)
             * @config {Scheduler.data.ResourceStore}
             * @category Data
             */
            resourceStore : null,

            /**
             * The optional {@link Scheduler.data.AssignmentStore}, holding assigments between resources and events.
             * Required for multi assignments.
             * @config {Scheduler.data.AssignmentStore}
             * @category Data
             */
            assignmentStore : null,

            dependencyStore : null,

            /**
             * Overridden to *not* auto create a store at the Scheduler level.
             * The store is the {@link #config-resourceStore} which is either configured in
             * or acquired from the {@link #config-crudManager}.
             * @config {Core.data.Store}
             * @private
             */
            store : null,

            /**
             * The name of the start date parameter that will be passed to in every `eventStore` load request.
             * @config {String}
             * @category Data
             */
            startParamName : 'startDate',

            /**
             * The name of the end date parameter that will be passed to in every `eventStore` load request.
             * @config {String}
             * @category Data
             */
            endParamName : 'endDate',

            /**
             * true to apply start and end dates of the current view to any `eventStore` load requests.
             * @config {Boolean}
             * @category Data
             */
            passStartEndParameters : false,

            /**
             * Supply a CrudManager config object if you want to use CrudManager for handling data
             * @config {Object}
             * @category Data
             */
            crudManager : null,

            /**
             * Inline events, will be loaded into an internally created EventStore
             * @config {Scheduler.model.EventModel[]|Object[]}
             * @category Data
             */
            events : null,

            /**
             * Inline resources, will be loaded into an internally created ResourceStore
             * @config {Scheduler.model.ResourceModel[]|Object[]}
             * @category Data
             */
            resources : null,

            /**
             * Inline assignments, will be loaded into an internally created AssignmentStore
             * @config {Scheduler.model.AssignmentModel[]|Object[]}
             * @category Data
             */
            assignments : null,

            /**
             * Configure with `true` to also remove the event when removing the last assignment
             * @config {Boolean}
             * @default
             * @category Misc
             */
            removeUnassignedEvent : true
        };
    }

    //endregion

    onStoreDataChange({ source : store, action }) {
        this.currentOrientation.clearEvents();

        super.onStoreDataChange(...arguments);
    }

    // Wrap w/ transition refreshFromRowOnStoreAdd() inherited from Grid
    refreshFromRowOnStoreAdd(row, { isExpand }) {
        const args = arguments;

        this.runWithTransition(() => super.refreshFromRowOnStoreAdd(row, ...args), !isExpand);
    }

    onStoreAdd({ source : store, records, isChild }) {
        const me         = this,
            { rowManager } = me;

        // If it was an appendChild to a parent that is not expanded,
        // do nothing
        if (isChild && !records[0].parent.isExpanded(me)) {
            return;
        }

        rowManager.calculateRowCount(false, true, true);

        const  endIndex   = Math.max(rowManager.topIndex + rowManager.rowCount, Math.min(rowManager.visibleRowCount, store.count));

        // Need to find topmost resource because we have to update from there downwards
        records = records.sort((lhs, rhs) => {
            return store.indexOf(lhs) - store.indexOf(rhs);
        });
        let firstUpdatedIndex = store.indexOf(records[0]);

        // If the update is below the rendered block, ignore it
        if (firstUpdatedIndex < endIndex) {
            // Start updating from the first affected record, or the rendered block
            // top if the record is above the rendered block.
            firstUpdatedIndex = Math.max(firstUpdatedIndex, rowManager.topIndex);

            // Clear events on every row below the first record
            for (let i = firstUpdatedIndex; i < endIndex; i++) {
                me.currentOrientation.onRowRecordUpdate({ record : store.getAt(i) });
            }
        }
        else {
            // Still might have changed total height
            rowManager.estimateTotalHeight();
        }

        super.onStoreAdd(...arguments);
    }

    /**
     * Overrides event handler in Grid, to pass on to current orientation class
     * @private
     */
    onStoreUpdateRecord() {
        // need to update events when resource changes (might use data in renderers, templates)
        if (!this.suspendStoreRedraw) {
            this.currentOrientation.onRowRecordUpdate(...arguments);
            super.onStoreUpdateRecord(...arguments);
        }
    }

    onStoreRemove() {
        this.currentOrientation.onRowRecordRemove(...arguments);
        super.onStoreRemove(...arguments);
    }

    onStoreRemoveAll() {
        this.currentOrientation.clearEvents();
        super.onStoreRemoveAll();
    }

    //region destroy

    // Cleanup, destroys stores if Scheduler.destroyStores is true.
    doDestroy() {
        super.doDestroy();

        const me = this;
        if (me.destroyStores) {
            me.constructor.destroy(
                me.assignmentStore,
                me.resourceStore,
                me.dependencyStore,
                me.eventStore
            );
        }
    }

    //endregion

    //region Row store

    get store() {
        const me = this;
        // Spin up the resourceStore if possible.
        // If there are configured resources, this will load them too.
        me._thisIsAUsedExpression(me.resources);

        // Vertical uses a dummy store
        if (!me._store && me.isVertical) {
            // TODO: Make this store readonly, since we are using single cell approach
            me._store = new Store({
                data : [
                    { id : 'verticalTimeAxisRow' }
                ]
            });
        }

        return super.store;
    }

    set store(store) {
        super.store = store;
    }

    //endregion

    //region ResourceStore

    /**
     * Get/set resources, applies to the assigned ResourceStore
     * @property {Scheduler.model.ResourceModel[]|Object[]}
     * @category Data
     */
    get resources() {
        const resourceStore = this.resourceStore;

        return resourceStore && resourceStore.records;
    }

    set resources(resources) {
        this.resourceStore.data = resources;
    }

    /**
     * Get/set the resource store instance
     * @property {Scheduler.data.ResourceStore}
     * @category Data
     */
    get resourceStore() {
        const me = this;

        // If there's a CrudManager, it injects its resourceStore.
        // If not, we create our own instance.
        if (!me._resourceStore) {
            if (!me.crudManager || !me.crudManager.resourceStore) {
                me.resourceStore = new ResourceStore();
            }
        }

        return me._resourceStore;
    }

    set resourceStore(newResourceStore) {
        const
            me               = this,
            oldResourceStore = me._resourceStore;

        if (newResourceStore === oldResourceStore) {
            return;
        }

        if (!(newResourceStore instanceof ResourceStore)) {
            newResourceStore = new ResourceStore(newResourceStore);
        }

        if (oldResourceStore) {
            const isBackingRowStore = oldResourceStore === me.store;

            me._resourceStore = newResourceStore;

            // Reconfigure grid if resourceStore is backing the rows
            if (newResourceStore && me.isHorizontal) {
                if (isBackingRowStore) {
                    me.store = me.resourceStore;
                    me.store.metaMapId = me.id;
                }
                else {
                    me.refresh();
                }
            }
        }
        else {
            me._resourceStore = newResourceStore;
        }

        if (newResourceStore) {
            // In vertical, resource store is not the row store but should toggle the load mask
            if (me.isVertical) {
                newResourceStore.on('load', () => me.unmaskBody());
            }

            me.trigger('resourceStoreChange', { newResourceStore, oldResourceStore });
        }

        if (me.isHorizontal && !me._store) {
            me.store = newResourceStore;
        }
    }

    getResourceStoreListenerConfig() {
        return {};
    }
    //endregion

    //region EventStore

    /**
     * Get/set events, applies to the assigned EventStore
     * @property {Scheduler.model.EventModel[]|Object[]}
     * @category Data
     */
    get events() {
        return this._eventStore.records;
    }

    set events(events) {
        this.eventStore.data = events;
    }

    /**
     * Get/set the event store instance
     * @property {Scheduler.data.EventStore}
     * @category Data
     */
    get eventStore() {
        const me = this;

        // If there's a CrudManager, we use its eventStore.
        // If not, we create our own instance.
        if (!me._eventStore) {
            if (!me.crudManager || !me.crudManager.eventStore) {
                me.eventStore = new EventStore({
                    resourceStore : me.resourceStore
                });
            }
        }

        return this._eventStore;
    }

    set eventStore(newEventStore) {
        const me            = this,
            oldEventStore = me._eventStore;

        let triggerLoad = false;

        if (newEventStore === oldEventStore) {
            return;
        }

        let resourceStore = me.resourceStore;

        // Reconfiguring
        if (oldEventStore) {
            const
                oldEventStore   = me.eventStore,
                { assignmentStore, dependencyStore } = me;

            if (newEventStore === oldEventStore) {
                return;
            }

            me.eventStoreDetacher && me.eventStoreDetacher();

            me._eventStore = newEventStore;

            me.timeAxisViewModel.eventStore = newEventStore;

            if (newEventStore) {
                if (resourceStore && !newEventStore.resourceStore) {
                    newEventStore.resourceStore = resourceStore;
                }

                if (assignmentStore && !newEventStore.assignmentStore) {
                    newEventStore.assignmentStore = assignmentStore;
                }

                if (dependencyStore && !newEventStore.dependencyStore) {
                    newEventStore.dependencyStore = dependencyStore;
                }

                me.trigger('eventStoreChange', { newEventStore, oldEventStore });

                me.refresh();
            }
        }
        else {
            if (newEventStore.isEventStore) {
                if (resourceStore && newEventStore.resourceStore !== resourceStore) {
                    // Sanity check.
                    // An eventStore cannot be shared between two Schedulers with different
                    // resourceStores. Prevent this Scheduler being handed an eventStore that
                    // is already attached to another resourceStore
                    if (newEventStore.resourceStore) {
                        throw new Error(`eventStore ${newEventStore.id} is already linked to resourceStore ${newEventStore.resourceStore.id}`);
                    }
                    newEventStore.resourceStore = resourceStore;
                }
            }
            else {
                triggerLoad = newEventStore.autoLoad;
                newEventStore.autoLoad = false;

                newEventStore = new EventStore(Object.assign({
                    resourceStore : me.resourceStore
                }, newEventStore));
            }
            me._eventStore = newEventStore;
        }

        if (newEventStore) {
            const listenerCfg = this.getEventStoreListenerConfig();

            if (me.passStartEndParameters) {
                listenerCfg.beforeload = me.applyStartEndParameters;
            }

            me.eventStoreDetacher && me.eventStoreDetacher();

            // TODO: PORT don't have autoDestroy yet
            /*if (me.eventStore) {
             if (eventStore !== me.eventStore && me.eventStore.autoDestroy) {
             me.eventStore.destroy();
             }
             }*/

            me.eventStoreDetacher = newEventStore.on(listenerCfg);

            if (newEventStore.assignmentStore) {
                me.assignmentStore = newEventStore.assignmentStore;
            }

            if (triggerLoad) {
                newEventStore.load().catch(() => {});
            }
        }

        // flag checked when adding events, to trigger full render first time
        if (me.eventStore.count > 0) {
            me.eventsRendered = true;
        }
    }

    getEventStoreListenerConfig() {
        return {
            thisObj    : this,
            detachable : true,

            // There is no separate dataset event any more.
            // It's a refresh event with action: 'dataset'
            change       : 'onInternalEventStoreChange',
            clearchanges : 'onEventClearChanges',
            beforecommit : 'onEventBeforeCommit',
            commit       : 'onEventCommit',
            beforeRemove : 'onEventStoreBeforeRemove',
            refresh      : 'onEventStoreRefresh',
            exception    : 'onEventException',
            idchange     : 'onEventIdChange'

            // TODO: PORT saving tree for later
            // If the eventStore is a TreeStore
            //nodeinsert : me.onEventAdd,
            //nodeappend : me.onEventAdd
        };
    }

    //endregion

    //region AssignmentStore

    /**
     * Get/set assignments, applies to the assigned AssignmentStore
     * @property {Scheduler.model.AssignmentModel[]|Object[]}
     * @category Data
     */
    get assignments() {
        return this.assignmentStore && this.assignmentStore.records;
    }

    set assignments(assignments) {
        // Creating AssignmentStore here and not in `get assignmentStore` since it is optional, but if user assigns
        // assignments we can be sure one is needed.
        if (!this.assignmentStore) {
            this.eventStore.assignmentStore = this.assignmentStore = new AssignmentStore();
        }

        this.assignmentStore.data = assignments;
    }

    /**
     * Get/set the assignment store instance
     * @property {Scheduler.data.AssignmentStore}
     * @category Data
     */
    get assignmentStore() {
        const me         = this,
            eventStore = me.eventStore,
            crudManager = me.crudManager;

        return crudManager && crudManager.assignmentStore || eventStore && eventStore.assignmentStore;
    }

    set assignmentStore(newAssignmentStore) {
        const
            me                 = this,
            oldAssignmentStore = me.assignmentStore;

        if (me.eventStore.assignmentStore !== newAssignmentStore) {
            me.eventStore.assignmentStore = newAssignmentStore;
        }

        me.assignmentStoreDetacher && me.assignmentStoreDetacher();

        if (newAssignmentStore) {
            // In case there is an assignment store used
            me.assignmentStoreDetacher = newAssignmentStore.on(
                me.getAssignmentStoreListenerConfig()
            );

            me.trigger('assignmentStoreChange', { newAssignmentStore, oldAssignmentStore });
            me.refresh();
        }
    }

    getAssignmentStoreListenerConfig() {
        const me = this;

        return {
            thisObj      : me,
            //refresh    : me.onEventDataRefresh,
            refresh      : me.onAssignmentRefresh,
            update       : me.onAssignmentUpdate,
            add          : me.onAssignmentAdd,
            beforeRemove : {
                fn   : me.onBeforeAssignmentRemove,
                // We must go last in case an app vetoes a remove
                // by returning false from a handler.
                prio : -1000
            },
            remove    : me.onAssignmentRemove,
            filter    : me.onAssignmentFilter,
            removeall : me.onAssignmentRemoveAll
        };
    }

    //endregion

    //region DependencyStore

    /**
     * Get/set the dependency store instance
     * @property {Scheduler.data.DependencyStore}
     * @category Data
     */
    get dependencyStore() {
        const me         = this,
            eventStore = me.eventStore,
            crudManager = me.crudManager;

        return crudManager && crudManager.dependencyStore || eventStore && eventStore.dependencyStore || me._dependencyStore;
    }

    set dependencyStore(newDependencyStore) {
        const me = this;

        if (!me.isConfigured) {
            me._dependencyStore = newDependencyStore;
        }
        else {
            const oldDependencyStore = me.dependencyStore;

            me.eventStore.dependencyStore = newDependencyStore;

            if (newDependencyStore) {
                me.trigger('dependencyStoreChange', { newDependencyStore, oldDependencyStore });
                me.refresh();
            }
        }
    }

    getDependencyStoreListenerConfig() {
        return {};
    }
    //endregion

    //region CrudManager

    /**
     * Get/set the CrudManager instance
     * @property {Scheduler.data.CrudManager}
     * @category Data
     */
    get crudManager() {
        return this._crudManager;
    }

    set crudManager(crudManager) {
        const me = this;

        if (!(crudManager instanceof CrudManager)) {
            // CrudManager injects itself into is Scheduler's _crudManager property
            // because code it triggers needs to access it through its getter.
            crudManager = new CrudManager(Object.assign({
                scheduler : me
            }, crudManager));
        }
        else {
            me._crudManager = crudManager;
        }
    }

    //endregion

    //region Events

    onEventIdChange(params) {
        this.currentOrientation.onEventStoreIdChange && this.currentOrientation.onEventStoreIdChange(params);
    }

    /**
     * Calls appropriate functions for current event layout when the event store is modified.
     * @private
     */
    // Named as Internal to avoid naming collision with wrappers that relay events
    onInternalEventStoreChange(params) {
        const me = this;

        // Too early, bail out
        if (!me._mode) {
            return;
        }

        if (me.isVertical) {
            me.currentOrientation.onEventStoreChange(params);
        }
        else {
            // TODO: Move this to horizontal

            const
                layout = me.currentOrientation,
                // ResourceTimeRanges also calls this fn, using its store as source. It is "compatible" with eventStore
                eventStore = params.source,
                {
                    rowManager,
                    resourceStore
                } = me,
                {
                    action,
                    changes,
                    isCollapse
                } = params,
                events = params.records || (params.record ? [params.record] : null),
                resources = [];

            let rows = new Set(),
                useTransition = false,
                len, i;

            if (!me.isPainted) {
                return;
            }

            // Ignore update caused by collapse or removing associated resource, will be handled by resource removal code
            if (isCollapse || (action === 'update' && events.length && events[0].meta.removingResource)) {
                return;
            }

            // resource timeranges feature embeds into regular events drawing procedure
            // which means in some cases we should repaint all rows
            const skipRows = action === 'filter' && me.hasFeature('resourceTimeRanges');

            // If events were changed
            if (!skipRows && checkResources[action] && events) {
                // For event resource change, the "from" resource is part of the changed resource set.
                if (changes && ('resourceId' in changes) && changes.resourceId.oldValue != null) {
                    const
                        prevResource = resourceStore.getById(changes.resourceId.oldValue),
                        prevRow = prevResource && rowManager.getRowFor(prevResource);

                    // Old resource might not exist in store, https://app.assembla.com/spaces/bryntum/tickets/7070.
                    // Happens for example when dropping from another scheduler.
                    if (prevRow) {
                        resources.push(prevResource);
                        rows.add(prevRow);
                    }
                }

                // We are only interested in associated resources which exist in the store and are in the rendered block.
                for (i = 0, len = events.length; i < len; i++) {
                    resources.push(...eventStore.getResourcesForEvent(events[i]).filter(resource => {
                        // Skip row if the resource is in a collapsed node and it's not available
                        // Note: Use `resource.id` since resource is associated record and it can be a placeHolder object
                        // ({ id : keyValue, placeHolder : true }) in case it's missing in the store, see Model.initRelation
                        if (resource && me.resourceStore.isAvailable(resource.id)) {
                            const row = rowManager.getRowFor(resource);
                            if (row) {
                                rows.add(row);
                                return true;
                            }
                        }
                        return false;
                    }));
                }

                if (resources.length) {
                    // Sort rows if more than one
                    if (rows.size > 1) {
                        rows = new Set([...rows].sort((a, b) => a.dataIndex - b.dataIndex));
                    }
                    // If all affected rows are outside of the rendered range, do nothing
                    else if (!rows.size) {
                        return;
                    }
                }
                // No resources in the rendered block were visible (or all events filtered out, in which case a full redraw
                // is performed). Nothing to update in the UI, but the dataset height might have changed.
                else if (!(action === 'filter' && !events.length)) {
                    rowManager.estimateTotalHeight();
                    return;
                }
                params.resources = resources;
            }

            switch (action) {
                case 'dataset':
                    layout.onEventDataset();
                    if (!eventStore.count) {
                        return;
                    }
                    break;
                case 'add':
                    layout.onEventAdd(params);
                    useTransition = true;
                    break;
                case  'update':
                    layout.onEventUpdate(params);
                    useTransition = true;
                    break;
                case 'remove':
                    layout.onEventRemove(params);
                    useTransition = true;
                    break;
                case 'removeall':
                    layout.onEventRemoveAll();
                    break;
                case 'filter':
                    layout.onEventFilter(params);
                    break;
                case 'clearchanges':
                    layout.onEventClearChanges(params);
                    break;
            }

            me.runWithTransition(() => {
                if (rows.size) {

                    // Render the affected rows.
                    rowManager.renderRows(rows);
                }
                // No specific rows affected, for example a dataset. Draw all
                else {
                    // TODO: change to refresh() when merged to master
                    rowManager.renderFromRow();
                }
            }, useTransition);
        }
    }

    /**
     * If events are changed in batch, endBatch fires refresh, so we must refresh the view
     * @private
     */
    onEventStoreRefresh({ action }) {
        if (action === 'batch' && this.rowManager.rowCount) {
            this.currentOrientation.onEventDataset && this.currentOrientation.onEventDataset();
            // TODO: Run with transition?
            this.refresh();
        }
    }

    /**
     * Moves focus before the currently active event is removed.
     * @private
     */
    onEventStoreBeforeRemove({ records }) {
        const me = this;

        // Active event is being removed.
        if (me.activeEvent && records.includes(me.activeEvent)) {
            let moveTo;

            // If being done by a keyboard gesture then look for a close target
            // until we find an existing record, not scheduled for removal.
            // Otherwise, per Mats, push focus outside of the Scheduler.
            if (GlobalEvents.lastInteractionType === 'key') {
                for (let i = 0, l = records.length; i < l && (!moveTo || records.includes(moveTo)); i++) {
                    if (me.eventStore.getResourcesForEvent(records[i]).length) {
                        const event = records[i],
                            from = event.assignments && event.assignments.length ? event.assignments[0] : event;
                        moveTo = me.getNext(from, true) || me.getPrevious(from, true);
                    }
                }
            }

            // Move focus away from the element which will soon have no
            // backing data.
            if (moveTo) {
                me.navigateTo(moveTo);
            }
            // Focus must exit the Scheduler's subgrid, otherwise, if a navigation
            // key gesture is delivered before the outgoing event's element has faded
            // out and been removed, navigation will be attempted from a deleted
            // event. Animated hiding is problematic.
            //
            // We cannot just revertFocus() because that might move focus back to an
            // element in a floating EventEditor which is not yet faded out and
            // been removed. Animated hiding is problematic.
            //
            // We cannot focus scheduler.timeAxisColumn.element because the browser
            // would scroll it in some way if we have horizontal overflow.
            //
            // The only thing we can know about to focus here is the Scheduler itself.
            else {
                me.element.focus();
            }
        }
    }

    /**
     * Refreshes committed events, to remove dirty/committing flag.
     * CSS is added
     * @private
     */
    onEventCommit({ changes }) {
        let resourcesToRepaint = [...changes.added, ...changes.modified].map(eventRecord => this.eventStore.getResourcesForEvent(eventRecord));

        // flatten
        resourcesToRepaint = Array.prototype.concat.apply([], resourcesToRepaint);

        // repaint relevant resource rows
        new Set(resourcesToRepaint).forEach(resourceRecord => this.repaintEventsForResource(resourceRecord));
    }

    /**
     * Adds the committing flag to changed events before commit.
     * @private
     */
    onEventBeforeCommit({ changes }) {
        // Committing sets a flag in meta that during eventrendering applies a CSS class. But to not mess up drag and
        // drop between resources no redraw is performed before committing, so class is never applied to the element(s).
        // Applying here instead
        [...changes.added, ...changes.modified].forEach(eventRecord =>
            this.getElementsFromEventRecord(eventRecord).forEach(element => element.classList.add(this.committingCls))
        );
    }

    // Clear committing flag
    onEventException({ action }) {
        if (action === 'commit') {
            const { changes } = this.eventStore;
            [...changes.added, ...changes.modified, ...changes.removed].forEach(eventRecord =>
                this.repaintEvent(eventRecord)
            );
        }
    }

    /**
     * Refreshes scheduler when event changes are cleared.
     * @private
     */
    onEventClearChanges() {
        this.refresh();
    }

    /**
     * Refreshes scheduler when data is assigned to the assigment store
     * @private
     */
    onAssignmentRefresh(event) {
        if (this.isHorizontal && (event.action === 'dataset' || event.action === 'batch')) {
            this.refresh();
        }
    }

    /**
     * Repaints affected resources when assignments are added.
     * @private
     */
    onAssignmentAdd({ records }) {
        if (this.isHorizontal) {
            records.forEach(assignment => {
                const resource = assignment.resource;
                resource && this.repaintEventsForResource(resource);
            });
        }
    }

    /**
     * Repaints affected resources when assignments are updated.
     * @private
     */
    onAssignmentUpdate({ record, changes }) {
        if (this.isHorizontal) {
            // TODO: Move to HorizontalEventMapper
            const
                { rowManager } = this,
                layoutCache = this.currentOrientation.cache,
                oldResourceId = 'resourceId' in changes && changes.resourceId.oldValue,
                newResourceId = record.resourceId;

            let row,
                fromRow = MAX_VALUE;

            if (oldResourceId != null && (row = rowManager.getRowFor(oldResourceId))) {
                layoutCache.clearRow(oldResourceId);
                fromRow = Math.min(fromRow, row.index);
            }

            if (newResourceId && (row = rowManager.getRowFor(newResourceId))) {
                layoutCache.clearRow(newResourceId);
                fromRow = Math.min(fromRow, row.index);
            }

            // Render from the first mutated row in the rendered block;
            if (fromRow !== MAX_VALUE) {
                rowManager.renderFromRow(rowManager.rows[fromRow]);
            }
        }
    }

    onBeforeAssignmentRemove({ source, records, doRemoveLastEvent }) {
        // Bail out if caused by the remove below
        if (source.isRemovingEvent) {
            return;
        }

        const
            me = this,
            events = records.reduce((result, assignment) => {
                const event = assignment.event;

                if (event && !event.placeHolder && !result.includes(event)) {
                    result.push(event);
                }

                return result;
            }, []);

        let moveTo;

        // Deassigning the active assignment
        if (me.activeEvent && events.includes(me.activeEvent)) {
            // If being done by a keyboard gesture then look for a close target
            // until we find an existing record, not scheduled for removal.
            // Otherwise, per Mats, push focus outside of the Scheduler.
            if (GlobalEvents.lastInteractionType === 'key') {
                // Look for a close target until we find an existing record, not scheduled for removal
                for (let i = 0, l = records.length; i < l && (!moveTo || records.includes(moveTo)); i++) {
                    if (me.eventStore.getResourcesForEvent(records[i].event).length) {
                        const event = records[i],
                            from = event.assignments && event.assignments.length ? event.assignments[0] : event;

                        moveTo = me.getNext(from) || me.getPrevious(from);
                    }
                }
            }

            // Move focus away from the element which will soon have no
            // backing data.
            if (moveTo) {
                me.navigateTo(moveTo);
            }
            // Focus must exit the Scheduler's subgrid, otherwise, if a navigation
            // key gesture is delivered before the outgoing event's element has faded
            // out and been removed, navigation will be attempted from a deleted
            // event. Animated hiding is problematic.
            //
            // We cannot just revertFocus() because that might move focus back to an
            // element in a floating EventEditor which is not yet faded out and
            // been removed. Animated hiding is problematic.
            //
            // We cannot focus scheduler.timeAxisColumn.element because the browser
            // would scroll it in some way if we have horizontal overflow.
            //
            // The only thing we can know about to focus here is the Scheduler itself.
            else {
                me.element.focus();
            }
        }

        if (me.removeUnassignedEvent && !me.isRemoving && doRemoveLastEvent !== false && (!source.stm || !source.stm.isRestoring)) {
            // Collect all events that will unassigned after the remove
            const toRemove = events.reduce((result, eventRecord) => {
                let assignmentCount = eventRecord.assignments ? eventRecord.assignments.length : 0;

                assignmentCount -= records.filter(r => r.event === eventRecord).length;

                if (!assignmentCount) {
                    result.push(eventRecord);
                }

                return result;
            }, []);

            // And remove them
            me.eventStore.remove(toRemove);
        }
    }

    /**
     * Repaints affected resources when assignments are removed.
     * @private
     */
    onAssignmentRemove({ records }) {
        if (this.isHorizontal) {
            records.forEach(assignment => {
                const resource = assignment.resource;
                resource && this.repaintEventsForResource(resource);
            });
        }
    }

    /**
     * Repaints resources when all assignments are removed.
     * @private
     */
    onAssignmentRemoveAll() {
        if (this.isHorizontal) {
            this.refresh();
        }
    }

    /**
     * Refreshes scheduler when assignment store is filtered.
     */
    onAssignmentFilter() {
        if (this.isHorizontal) {
            this.refresh();
        }
    }

    //endregion

    //region Other functions

    /**
     * Applies the start and end date to each event store request (formatted in the same way as the start date, defined in the EventStore Model class).
     * @category Data
     */
    applyStartEndParameters({ source : eventStore, params }) {
        const me = this,
            dateFormat = eventStore.modelClass.fieldMap.startDate.dateFormat;

        params[me.startParamName] = DateHelper.format(me.startDate, dateFormat);
        params[me.endParamName] = DateHelper.format(me.endDate, dateFormat);
    }

    //endregion

    // This does not need a className on Widgets.
    // Each *Class* which doesn't need 'b-' + constructor.name.toLowerCase() automatically adding
    // to the Widget it's mixed in to should implement thus.
    get widgetClass() {}
};
