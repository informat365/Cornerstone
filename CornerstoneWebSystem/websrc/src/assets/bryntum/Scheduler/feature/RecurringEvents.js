import GridFeatureManager from '../../Grid/feature/GridFeatureManager.js';
import RecurringTimeSpans from './RecurringTimeSpans.js';
import RecurrenceConfirmationPopup from '../view/recurrence/RecurrenceConfirmationPopup.js';

/**
 * @module Scheduler/feature/RecurringEvents
 */

/**
 * A feature that adds recurring events functionality to the Scheduler.
 *
 * The main purpose of the class is generating occurrences of the repeating events for the visible timespan.
 * To achieve this it tracks changes on the {@link Scheduler.view.mixin.SchedulerStores#config-eventStore event store} to apply a repeating event changes
 * to its visible occurrences.
 * The feature also tracks the Scheduler visible timespan changes to make sure the new timespan is populated
 * with corresponding event occurrences.
 *
 * Additionally the class implements displaying of a {@link Scheduler.view.recurrence.RecurrenceConfirmationPopup special confirmation}
 * on user mouse dragging/resizing repeating events and its occurrences.
 *
 * This feature is **disabled** by default.
 *
 * @extends Scheduler/feature/RecurringTimeSpans
 * @classtype recurringEvents
 * @demo Scheduler/recurrence
 */
export default class RecurringEvents extends RecurringTimeSpans {

    static get $name() {
        return 'RecurringEvents';
    }

    get client() {
        return super.client;
    }

    set client(client) {
        super.client = client;

        if (client) {
            this._onEventStoreChangeDetacher && this._onEventStoreChangeDetacher();
            // set listener to "eventstorechange" here to catch switching from a store not-supporting recurrences to a supporting one
            // listeners set in bindStore() got destroyed when assigning a store w/o recurrence support
            this._onEventStoreChangeDetacher = client.on('eventstorechange', this.onEventStoreChange, this);
        }

        this.store = client ? client.eventStore : null;
    }

    bindClient(client) {
        const me = this;

        super.bindClient(client);

        me._eventPanelDetacher && me._eventPanelDetacher();

        if (client) {
            me._eventPanelDetacher = client.on({
                'beforeeventdropfinalize'   : me.onBeforeEventDropFinalize,
                'beforeeventresizefinalize' : me.onBeforeEventResizeFinalize,
                'beforeEventDelete'         : me.onRecurrableEventBeforeDelete,

                thisObj : me
            });
        }
    }

    get confirmationPopup() {
        if (!this._confirmationPopup) {
            this._confirmationPopup = new RecurrenceConfirmationPopup();
        }

        return this._confirmationPopup;
    }

    set confirmationPopup(popup) {
        this._confirmationPopup = popup;
    }

    onEventStoreChange({ newEventStore, oldEventStore }) {
        this.store = newEventStore;
    }

    showDisplayConfirmationOnEventDelete(eventRecord) {
        const eventEdit = this.client.features.eventEdit;

        // show confirmation if we deal with a recurring event (or its occurrence)
        // and if the record is not being edited by event editor (since event editor has its own confirmation)
        return (eventRecord.supportsRecurring && (eventRecord.isRecurring || eventRecord.isOccurrence) &&
            (!eventEdit || !eventEdit.isEditing || eventEdit.eventRecord !== eventRecord));
    }

    onRecurrableEventBeforeDelete({ eventRecords, context }) {
        const [eventRecord] = eventRecords;

        if (this.showDisplayConfirmationOnEventDelete(eventRecord)) {
            this.confirmationPopup.confirm({
                actionType : 'delete',
                eventRecord,
                changerFn() {
                    context.finalize(true);
                },
                cancelFn() {
                    context.finalize(false);
                }
            });

            return false;
        }
    }

    onBeforeEventDropFinalize({ context }) {
        const { record : eventRecord } = context;

        if (eventRecord.supportsRecurring && (eventRecord.isRecurring || eventRecord.isOccurrence)) {
            context.async = true;

            this.confirmationPopup.confirm({
                actionType : 'update',
                eventRecord,
                changerFn() {
                    context.finalize(true);
                },
                cancelFn() {
                    context.finalize(false);
                }
            });
        }
    }

    onBeforeEventResizeFinalize({ context }) {
        const { eventRecord } = context;

        if (eventRecord.supportsRecurring && (eventRecord.isRecurring || eventRecord.isOccurrence)) {
            context.async = true;

            this.confirmationPopup.confirm({
                actionType : 'update',
                eventRecord,
                changerFn() {
                    context.finalize(true);
                },
                cancelFn() {
                    context.finalize(false);
                }
            });
        }
    }

};

GridFeatureManager.registerFeature(RecurringEvents, false, 'Scheduler');
