import Base from '../../../Core/Base.js';
import Collection from '../../../Core/util/Collection.js';

/**
 * @module Scheduler/view/mixin/EventSelection
 */

/**
 * Mixin that tracks event or assignment selection by clicking on one or more events in the scheduler.
 * @mixin
 */
export default Target => class EventSelection extends (Target || Base) {
    //region Default config

    static get defaultConfig() {
        return {
            /**
             * Configure as `true` to allow `CTRL+click` to select multiple events in the scheduler.
             * @config {Boolean}
             * @category Selection
             */
            multiEventSelect : false,

            /**
             * Configure as `true`, or set property to `true` to disable event selection.
             * @config {Boolean}
             * @default
             * @category Selection
             */
            eventSelectionDisabled : false,

            /**
             * CSS class to add to selected events.
             * @config {String}
             * @default
             * @category CSS
             * @private
             */
            eventSelectedCls : 'b-sch-event-selected',

            /**
             * Configure as `true` to trigger `selectionChange` when removing a selected event/assignment.
             * @config {Boolean}
             * @default
             * @category Selection
             */
            triggerSelectionChangeOnRemove : false,

            /**
             * This flag controls whether Scheduler should maintain its selection of events when loading a new dataset (if selected event ids are included in the newly loaded dataset).
             * @config {Boolean}
             * @default
             * @category Selection
             */
            maintainSelectionOnDatasetChange : true,

            /**
             * CSS class to add to other instances of a selected event, to highlight them.
             * @config {String}
             * @default
             * @category CSS
             * @private
             */
            eventAssignHighlightCls : 'b-sch-event-assign-selected',

            /**
             * Collection to store selection.
             * @config {Core.util.Collection}
             * @private
             */
            selectedCollection : {}
        };
    }

    //endregion

    //region Events

    /**
     * Fired any time there is a change to the events selected in the Scheduler.
     * @event eventSelectionChange
     * @param {String} action One of the actions 'select', 'deselect', 'update', 'clear'
     * @param {Scheduler.model.EventModel[]|Scheduler.model.AssignmentModel[]} selected An array of the Events or Assignments added to the selection.
     * @param {Scheduler.model.EventModel[]|Scheduler.model.AssignmentModel[]} deselected An array of the Events or Assignments removed from the selection.
     * @param {Scheduler.model.EventModel[]|Scheduler.model.AssignmentModel[]} selection The new selection.
     */

    //endregion

    //region Init

    afterConstruct() {
        super.afterConstruct();

        this.navigator.on({
            navigate : 'onEventNavigate',
            thisObj  : this
        });
    }

    //endregion

    //region Selected Collection

    set selectedCollection(selectedCollection) {
        if (!(selectedCollection instanceof Collection)) {
            selectedCollection = new Collection(selectedCollection);
        }
        this._selectedCollection = selectedCollection;

        // Fire row change events from onSelectedCollectionChange
        selectedCollection.on({
            change  : 'onSelectedCollectionChange',
            thisObj : this
        });
    }

    get selectedCollection() {
        return this._selectedCollection;
    }

    //endregion

    //region Modify selection

    /**
     * The {@link Scheduler.model.EventModel events} or {@link Scheduler.model.AssignmentModel assignments} which are selected.
     * @returns {Scheduler.model.EventModel[]|Scheduler.model.AssignmentModel[]}
     * @category Selection
     */
    get selectedEvents() {
        return this.selectedCollection.values;
    }

    set selectedEvents(events) {
        // Replace the entire selected collection with the new record set
        this.selectedCollection.splice(0, this.selectedCollection.count, events || []);
    }

    /**
     * Returns `true` if the {@link Scheduler.model.EventModel event} or {@link Scheduler.model.AssignmentModel assignment} is selected.
     * @param {Scheduler.model.EventModel|Scheduler.model.AssignmentModel} event The event or assignment
     * @category Selection
     */
    isEventSelected(event) {
        return this.selectedCollection.includes(event);
    }

    /**
     * Selects the passed {@link Scheduler.model.EventModel event} or {@link Scheduler.model.AssignmentModel assignment}
     * *if it is not selected*.
     * @param {Scheduler.model.EventModel|Scheduler.model.AssignmentModel} event The event or assignment to select. When using multi assignment, supply an AssignmentModel
     * @param {Boolean} [preserveSelection=false] Pass `true` to preserve any other selected events or assignments
     * @category Selection
     */
    selectEvent(event, preserveSelection = false) {
        // If there event is already selected, this is a no-op.
        // In this case, selection must not be cleared even in the absence of preserveSelection
        if (!this.isEventSelected(event)) {
            preserveSelection ? this.selectedCollection.add(event) : this.selectedEvents = event;
        }
    }

    /**
     * Deselects the passed {@link Scheduler.model.EventModel event} or {@link Scheduler.model.AssignmentModel assignment}
     * *if it is selected*.
     * @param {Scheduler.model.EventModel|Scheduler.model.AssignmentModel} event The event or assignment to deselect. When using multi assignment, supply an AssignmentModel
     * @category Selection
     */
    deselectEvent(event, preserveSelection = false) {
        if (this.isEventSelected(event)) {
            this.selectedCollection.remove(event);
        }
    }

    /**
     * Adds {@link Scheduler.model.EventModel events} or {@link Scheduler.model.AssignmentModel assignments} to the selection.
     * @param {Scheduler.model.EventModel[]|Scheduler.model.AssignmentModel[]} events Events or assignments to be deselected
     * @category Selection
     */
    selectEvents(events) {
        this.selectedCollection.add(events);
    }

    /**
     * Removes {@link Scheduler.model.EventModel events} or {@link Scheduler.model.AssignmentModel assignments} from the selection.
     * @param {Scheduler.model.EventModel[]|Scheduler.model.AssignmentModel[]} events Events or assignments  to be deselected
     * @category Selection
     */
    deselectEvents(events) {
        this.selectedCollection.remove(events);
    }

    /**
     * Deselects all {@link Scheduler.model.EventModel events} or {@link Scheduler.model.AssignmentModel assignments}.
     * @category Selection
     */
    clearEventSelection() {
        this.selectedEvents = [];
    }

    //endregion

    //region Events

    /**
     * Responds to mutations of the underlying selection Collection.
     * Keeps the UI synced, eventSelectionChange event is fired when `me.silent` is falsy.
     * @private
     */
    onSelectedCollectionChange({ added, removed }) {
        const
            me           = this,
            selection    = me.selectedEvents,
            selected     = added || [],
            deselected   = removed || [];

        function updateSelection(record, select) {
            const
                resourceRecord = record.isTask ? record : record.resource,
                eventRecord    = record.isAssignment ? record.event : record;

            if (eventRecord) {
                const element = me.getElementFromEventRecord(eventRecord, resourceRecord);

                me.currentOrientation.toggleCls(eventRecord, resourceRecord, me.eventSelectedCls, select);

                if (record.isAssignment) {
                    me.getElementsFromEventRecord(eventRecord).forEach(el => {
                        if (el !== element) {
                            el.classList[select ? 'add' : 'remove'](me.eventAssignHighlightCls);
                        }
                    });
                }
            }
        }

        selected.forEach(record => updateSelection(record, true));
        deselected.forEach(record => updateSelection(record, false));

        if (!me.silent) {
            me.trigger('eventSelectionChange', {
                action : (selection.length > 0) ? ((selected.length > 0 && deselected.length > 0)
                    ? 'update' : (selected.length > 0 ? 'select' : 'deselect')) : 'clear',
                selection,
                selected,
                deselected
            });
        }
    }

    /**
     * Store data change listener to remove events from selection which are no longer in the store.
     * @private
     */
    onInternalEventStoreChange({ action, records : events, source : eventStore }) {
        super.onInternalEventStoreChange(...arguments);

        const me = this;
        me.silent = !me.triggerSelectionChangeOnRemove;

        if (action === 'remove') {
            me.deselectEvents(events);
        }
        else if (action === 'dataset') {
            if (!me.maintainSelectionOnDatasetChange) {
                me.clearEventSelection();
            }
            else {
                me.selectedEvents = me.selectedEvents.filter(event => eventStore.includes(event));
            }
        }
        me.silent = false;
    }

    /**
     * Assignment change listener to remove events from selection which are no longer in the assignments.
     * @private
     */
    onAssignmentRemove({ records : assignments }) {
        super.onAssignmentRemove(...arguments);

        const me = this;
        me.silent = !me.triggerSelectionChangeOnRemove;
        me.deselectEvents(assignments);
        me.silent = false;
    }

    /**
     * Mouse listener to update selection.
     * @private
     */
    onEventSelectionClick(event, clickedRecord) {
        const me = this;

        // Multi selection: CTRL means preserve selection, just add or remove the event.
        // Single selection: CTRL deselects already selected event
        if (me.isEventSelected(clickedRecord)) {
            event.ctrlKey && me.deselectEvent(clickedRecord, me.multiEventSelect);
        }
        else {
            me.selectEvent(clickedRecord, event.ctrlKey && me.multiEventSelect);
        }
    }

    /**
     * Navigation listener to update selection.
     * @private
     */
    onEventNavigate({ event, item }) {
        if (!this.eventSelectionDisabled) {
            const
                me     = this,
                record = item && (item.nodeType === 1
                    ? (me.assignmentStore ? me.resolveAssignmentRecord(item) : me.resolveEventRecord(item)) : item);
            if (record) {
                me.onEventSelectionClick(event, record);
            }
            // Click outside of an event/assignment;
            else {
                me.clearEventSelection();
            }
        }
    }

    //endregion

    //region Getters/Setters

    // This does not need a className on Widgets.
    // Each *Class* which doesn't need 'b-' + constructor.name.toLowerCase() automatically adding
    // to the Widget it's mixed in to should implement thus.
    get widgetClass() {}

    //endregion
};
