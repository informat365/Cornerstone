import Base from '../../../Core/Base.js';
import Navigator from '../../../Core/helper/util/Navigator.js';
import DomHelper from '../../../Core/helper/DomHelper.js';
import FunctionHelper from '../../../Core/helper/FunctionHelper.js';
import ObjectHelper from '../../../Core/helper/ObjectHelper.js';

/**
 * @module Scheduler/view/mixin/EventNavigation
 */

const
    sortByStartDate = (l, r) => l.startDate - r.startDate,
    preventDefault = e => e.preventDefault();

/**
 * Mixin that tracks event or assignment selection by clicking on one or more events in the scheduler.
 * @mixin
 */
export default Target => class EventNavigation extends (Target || Base) {
    //region Default config

    static get defaultConfig() {
        return {
            /**
             * A config object to use when creating the {@link Core.helper.util.Navigator}
             * to use to perform keyboard navigation in the timeline.
             * @config {Object}
             * @default
             * @category Misc
             */
            navigator : null,

            /**
             * A CSS class name to add to focused events.
             * @config {String}
             * @default
             * @category CSS
             * @private
             */
            focusCls : 'b-active',

            /**
             * Allow using [Delete] and [Backspace] to remove events/assignments
             * @config {Boolean}
             * @default
             * @category Misc
             */
            enableDeleteKey : true
        };
    }

    //endregion

    //region Events

    //endregion

    construct(config) {
        const me = this,
            onDeleteKey = FunctionHelper.createThrottled(me.onDeleteKey, 500, me);

        me.isInTimeAxis = me.isInTimeAxis.bind(me);

        super.construct(config);

        const navigatorConfig = ObjectHelper.merge({
            ownerCmp         : me,
            target           : me.timeAxisSubGridElement,
            processEvent     : me.processEvent,
            itemSelector     : `.${me.eventCls}-wrap`,
            focusCls         : me.focusCls,
            navigatePrevious : FunctionHelper.createThrottled(me.navigatePrevious, 200, me, null, preventDefault),
            navigateNext     : FunctionHelper.createThrottled(me.navigateNext, 200, me, null, preventDefault),
            allowCtrlKey     : true,
            scrollSilently   : true,
            keys             : {
                Space     : 'onEventSpaceKey',
                Enter     : 'onEventEnterKey',
                Delete    : onDeleteKey,
                Backspace : onDeleteKey
            }
        }, me.navigator);

        me.navigator = new Navigator(navigatorConfig);
    }

    doDestroy() {
        this.navigator.destroy();
        super.doDestroy();
    }

    isInTimeAxis(record) {
        // If event is hidden by workingTime configs, horizontal mapper would raise a flag on instance meta
        // We still need to check if time span is included in axis
        return !record.instanceMeta(this).excluded && this.timeAxis.isTimeSpanInAxis(record);
    }

    /*
     * Override of GridNavigation#focusCell method to handle the TimeAxisColumn.
     * Not needed until we implement full keyboard accessibiliy.
     */
    accessibleFocusCell(cellSelector, options) {
        const me                     = this;

        cellSelector = me.normalizeCellContext(cellSelector);

        if (cellSelector.columnId === me.timeAxisColumn.id) {
            // const lastFocusedCell        = me.lastFocusedCell = me._focusedCell,
            //     lastFocusedCellElement = lastFocusedCell && me.getCell(lastFocusedCell),
            //     newCell = me.getCell(cellSelector),
            //     // Flag if the lastFocusedCellElement is DOCUMENT_POSITION_FOLLOWING newCell
            //     backwards = !!(lastFocusedCellElement && (newCell.compareDocumentPosition(lastFocusedCellElement) & 4));

            // // Navigating into the Sheduler, need to enable this back (for situations where we know focus was requested as a result of a keyboard input)...
            // let newEvent = me.getRecordFromElement(newCell);

            // me._focusedCell = cellSelector;

            // // Scheduler where row is a Resource which might have many events
            // // TODO: https://app.assembla.com/spaces/bryntum/tickets/6526 this class should
            // // not know about Gantt.
            // if (!newEvent.isTask) {
            //     const resourceEvents = newEvent.getEvents().filter(me.isInTimeAxis).sort(sortByStartDate);
            //     newEvent = resourceEvents[backwards ? resourceEvents.length - 1 : 0];
            // }

            // options.event.eventRecord = newEvent;

            // if (newEvent && me.activeEvent !== newEvent) {
            //     lastFocusedCellElement && lastFocusedCellElement.classList.remove('b-focused');
            //     me.scrollResourceEventIntoView(me.store.getById(cellSelector.id), newEvent, null, {
            //         animate : 100
            //     }).then(() => {
            //         me.activeEvent = newEvent;
            //     });
            // }
        }
        else {
            return super.focusCell(cellSelector, options);
        }
    }

    getPrevious(eventOrAssignmentRecord, isDelete) {
        const me = this,
            resourceStore = me.resourceStore,
            isAssignment = eventOrAssignmentRecord.isAssignment;

        // TODO: https://app.assembla.com/spaces/bryntum/tickets/6526 this class should not know about Gantt.
        if (eventOrAssignmentRecord.isTask) {
            return me.eventStore.getAt(me.eventStore.indexOf(eventOrAssignmentRecord) - 1);
        }

        let resourceRecord = eventOrAssignmentRecord.resource || me.eventStore.getResourcesForEvent(eventOrAssignmentRecord)[0],
            resourceEvents = resourceRecord.getEvents().filter(me.isInTimeAxis).sort(sortByStartDate),
            eventRecord = isAssignment ? eventOrAssignmentRecord.event : eventOrAssignmentRecord,
            previousEvent = resourceEvents[resourceEvents.indexOf(eventRecord) - 1];

        // At first event for resource, traverse up the resource store.
        if (!previousEvent) {
            // If we are deleting an event, skip other instances of the event which we may encounter
            // due to multi-assignment.
            for (let rowIdx = resourceStore.indexOf(resourceRecord) - 1; (!previousEvent || (isDelete && previousEvent === eventRecord)) && rowIdx >= 0; rowIdx--) {
                resourceRecord = resourceStore.getAt(rowIdx);
                let events = resourceRecord.getEvents().filter(me.isInTimeAxis).sort(sortByStartDate);
                previousEvent = events.length && events[events.length - 1];
            }
        }

        // If an assignment was passed, return one
        return isAssignment
            ? me.assignmentStore.getAssignmentForEventAndResource(previousEvent, resourceRecord)
            : previousEvent;
    }

    navigatePrevious(keyEvent) {
        const me = this,
            previousEvent = me.getPrevious(keyEvent.assignmentRecord || keyEvent.eventRecord);

        keyEvent.preventDefault();
        if (previousEvent) {
            if (!keyEvent.ctrlKey) {
                me.clearEventSelection();
            }
            me.navigateTo(previousEvent, keyEvent);
        }
    }

    getNext(eventOrAssignmentRecord, isDelete) {
        const me = this,
            resourceStore = me.resourceStore,
            isAssignment = eventOrAssignmentRecord.isAssignment;

        // TODO: https://app.assembla.com/spaces/bryntum/tickets/6526 this class should not know about Gantt.
        if (eventOrAssignmentRecord.isTask) {
            return me.eventStore.getAt(me.eventStore.indexOf(eventOrAssignmentRecord) + 1);
        }

        let resourceRecord = isAssignment ? eventOrAssignmentRecord.resource : eventOrAssignmentRecord.resources[0], //|| me.eventStore.getResourcesForEvent(eventOrAssignmentRecord)[0],
            resourceEvents = resourceRecord.getEvents().filter(me.isInTimeAxis).sort(sortByStartDate),
            eventRecord = isAssignment ? eventOrAssignmentRecord.event : eventOrAssignmentRecord,
            nextEvent = resourceEvents[resourceEvents.indexOf(eventRecord) + 1];

        // At last event for resource, traverse down the resource store
        if (!nextEvent) {
            // If we are deleting an event, skip other instances of the event which we may encounter
            // due to multi-assignment.
            for (let rowIdx = resourceStore.indexOf(resourceRecord) + 1; (!nextEvent || (isDelete && nextEvent === eventRecord)) && rowIdx < resourceStore.count; rowIdx++) {
                resourceRecord = resourceStore.getAt(rowIdx);
                nextEvent = resourceRecord.getEvents().filter(me.isInTimeAxis).sort(sortByStartDate)[0];
            }
        }

        // If an assignment was passed, return one
        return isAssignment
            ? me.assignmentStore.getAssignmentForEventAndResource(nextEvent, resourceRecord)
            : nextEvent;
    }

    navigateNext(keyEvent) {
        const me = this,
            nextEvent = me.getNext(keyEvent.assignmentRecord || keyEvent.eventRecord);

        keyEvent.preventDefault();
        if (nextEvent) {
            if (!keyEvent.ctrlKey) {
                me.clearEventSelection();
            }
            me.navigateTo(nextEvent, keyEvent);
        }
    }

    navigateTo(targetEvent, uiEvent = {}) {
        const me = this;

        if (targetEvent) {
            // No key processing during scroll
            me.navigator.disabled = true;

            const resource = targetEvent.resource;
            me.scrollResourceEventIntoView(
                resource,
                targetEvent.isAssignment ? targetEvent.event : targetEvent,
                null,
                {
                    animate : 100
                }
            ).then(() => {
                // Panel can be destroyed before promise is resolved
                if (!me.isDestroyed) {
                    me.navigator.disabled = false;
                    me.activeEvent = targetEvent;
                    me.navigator.trigger('navigate', {
                        event : uiEvent,
                        item  : DomHelper.up(me.getElementFromEventRecord(targetEvent, resource), me.navigator.itemSelector)
                    });
                }
            });
        }
    }

    set activeEvent(eventOrAssignmentRec) {
        const eventEl = this.getElementFromEventRecord(
            eventOrAssignmentRec.isAssignment ? eventOrAssignmentRec.event : eventOrAssignmentRec,
            eventOrAssignmentRec.resource
        );

        this.navigator.activeItem = eventEl.parentNode;
    }

    get activeEvent() {
        const { activeItem } = this.navigator;

        if (activeItem) {
            return this.resolveEventRecord(activeItem);
        }
    }

    get previousActiveEvent() {
        const { previousActiveItem } = this.navigator;

        if (previousActiveItem) {
            return this.resolveEventRecord(previousActiveItem);
        }
    }

    processEvent(keyEvent) {
        const me = this,
            eventElement = DomHelper.up(keyEvent.target, me.eventSelector);

        if (!me.navigator.disabled && eventElement) {
            keyEvent.assignmentRecord = me.resolveAssignmentRecord(eventElement);
            keyEvent.eventRecord = me.resolveEventRecord(eventElement);
            keyEvent.resourceRecord = me.resolveResourceRecord(eventElement);
        }
        return keyEvent;
    }

    onDeleteKey(keyEvent) {
        const record = keyEvent.assignmentRecord || keyEvent.eventRecord;

        if (!this.readOnly && this.enableDeleteKey && record) {
            this.removeRecords([record]);
        }
    }

    /**
     * Internal utility function to remove events. Used when pressing [DELETE] or [BACKSPACE] or when clicking the
     * delete button in the event editor. Triggers a preventable `beforeEventDelete` event.
     * @param {Scheduler.model.EventModel[]} eventRecords Records to remove
     * @param {Function} [callback] Optional callback executed after triggering the event but before deletion
     * @returns {Boolean} Returns `false` if the operation was prevented, otherwise `true`
     * @internal
     * @fires beforeEventDelete
     */
    removeRecords(eventRecords, callback = null) {
        if (!this.readOnly && eventRecords.length) {
            const context = {
                finalize(removeRecord = true) {
                    if (callback) {
                        callback(removeRecord);
                    }
                    if (removeRecord !== false) {
                        eventRecords.forEach(r => r.remove());
                    }
                }
            };
            /**
             * Fires before an event is removed. Can be triggered by user pressing [DELETE] or [BACKSPACE] or by the
             * event editor. Can for example be used to display a custom dialog to confirm deletion, in which case
             * records should be "manually" removed after confirmation:
             *
             * ```javascript
             * scheduler.on({
             *    beforeEventDelete({ eventRecords, context }) {
             *        // Show custom confirmation dialog (pseudo code)
             *        confirm.show({
             *            listeners : {
             *                onOk() {
             *                    // Remove the events on confirmation
             *                    context.finalize(true);
             *                },
             *                onCancel() {
             *                    // do not remove the events if "Cancel" clicked
             *                    context.finalize(false);
             *                }
             *            }
             *        });
             *
             *        // Prevent default behaviour
             *        return false;
             *    }
             * });
             * ```
             *
             * @event beforeEventDelete
             * @param {Scheduler.view.Scheduler}     source                                  The Scheduler instance
             * @param {Scheduler.model.EventModel[]} eventRecords                            The records about to be deleted
             * @param {Object}                       context                                 Additional removal context:
             * @param {Function}                     context.finalize                        Function to call to finalize the removal.
             *      Used to asynchronously decide to remove the records or not. Provide `false` to the function to prevent the removal.
             * @param {Boolean}                      [context.finalize.removeRecords = true] Provide `false` to the function to prevent the removal.
             * @preventable
             */
            if (this.trigger('beforeEventDelete', { eventRecords, context }) !== false) {
                context.finalize();
                return true;
            }
        }

        return false;
    }

    onEventSpaceKey(keyEvent) {
        // Empty, to be chained by features (used by TimeSpanRecordContextMenuBase)
    }

    onEventEnterKey(keyEvent) {
        // Empty, to be chained by features (used by EventEdit)
    }

    get isActionableLocation() {
        // Override from grid if the Navigator's location is an event (or task if we're in Gantt)
        // Being focused on a task/event means that it's *not* actionable. It's not valid to report
        // that we're "inside" the cell in a TimeLine, so ESC must not attempt to focus the cell.
        if (!this.navigator.activeItem) {
            return super.isActionableLocation;
        }
    }

    // This does not need a className on Widgets.
    // Each *Class* which doesn't need 'b-' + constructor.name.toLowerCase() automatically adding
    // to the Widget it's mixed in to should implement thus.
    get widgetClass() {}
};
