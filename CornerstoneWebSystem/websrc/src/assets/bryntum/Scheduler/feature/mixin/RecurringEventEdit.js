import WidgetHelper from '../../../Core/helper/WidgetHelper.js';
import Base from '../../../Core/Base.js';
import '../../view/recurrence/RecurrenceConfirmationPopup.js';
import '../../view/recurrence/field/RecurrenceCombo.js';
import '../../view/recurrence/RecurrenceLegendButton.js';
import '../../view/recurrence/RecurrenceEditor.js';

/**
 * @module Scheduler/feature/mixin/RecurringEventEdit
 */

/**
 * This mixin class provides recurring events functionality to the {@link Scheduler.feature.EventEdit event editor}.
 * @mixin
 */
export default Target => class RecurringEventEdit extends (Target || Base) {

    static get $name() {
        return 'RecurringEventEdit';
    }

    static get defaultConfig() {
        return {
            recurringEventsItems : [
                /**
                 * Reference to the `Repeat` event field, if used
                 * @member {Scheduler.view.recurrence.field.RecurrenceCombo} recurrenceCombo
                 * @readonly
                 */
                {
                    type  : 'recurrencecombo',
                    label : 'L{Repeat}',
                    ref   : 'recurrenceCombo'
                },
                /**
                 * Reference to the button that opens the event repeat settings dialog, if used
                 * @member {Scheduler.view.recurrence.RecurrenceLegendButton} editRecurrenceButton
                 * @readonly
                 */
                {
                    type : 'recurrencelegendbutton',
                    ref  : 'editRecurrenceButton',
                    name : 'recurrenceRule',
                    flex : '1'
                }
            ],

            /**
             * Set to `false` to hide recurring fields in event editor,
             * even if the {@link Scheduler.feature.RecurringEvents Recurring Events} feature is enabled
             * @config {Boolean}
             * @category Recurring
             */
            showRecurringUI : null
        };
    }

    insertExtraWidgetsIntoDefaultWidgets(editorConfig) {
        // inject recurring events widgets first
        this.processEditorConfig(editorConfig);

        super.insertExtraWidgetsIntoDefaultWidgets(editorConfig);
    }

    processEditorConfig(editorConfig) {
        const { items } = editorConfig;

        // find extraItems positions in the array
        const extraItemsIndex = items.findIndex(item => item.type === 'extraItems');

        // if no extraItems in the array ..append
        if (extraItemsIndex < 0) {
            items.push(...this.recurringEventsItems);
        }
        // otherwise insert before extraItems
        else {
            items.splice(extraItemsIndex, 0, ...this.recurringEventsItems);
        }
    }

    construct(scheduler, config) {
        const me = this;

        super.construct(scheduler, config);

        me.scheduler.on({
            beforeEventDelete : me.onRecurrableEventBeforeDelete,
            beforeEventSave   : me.onRecurrableEventBeforeSave,
            thisObj           : me
        });
    }

    get isRecurringEnabled() {
        return this.scheduler.features.recurringEvents && !this.scheduler.features.recurringEvents.disabled;
    }

    onEditorConstructed(editor) {
        const me = this;

        editor.on('hide', me.onRecurringEventEditorHide, me);

        me.editRecurrenceButton && me.editRecurrenceButton.on('click', me.onEditRecurrenceClick, me);
    }

    internalShowEditor() {
        this.toggleRecurringFieldsVisibility(this.isRecurringEnabled && this.showRecurringUI !== false);
    }

    toggleRecurringFieldsVisibility(show = true) {
        const methodName = show ? 'show' : 'hide';

        this.editRecurrenceButton && this.editRecurrenceButton[methodName]();
        this.recurrenceCombo && this.recurrenceCombo[methodName]();
    }

    onRecurringEventEditorHide({ source }) {
        const { recurrenceEditor, recurrenceConfirmation } = this;

        if (recurrenceEditor && recurrenceEditor.isVisible) {
            recurrenceEditor.hide();
        }
        if (recurrenceConfirmation && recurrenceConfirmation.isVisible) {
            recurrenceConfirmation.hide();
        }
    }

    // Builds RecurrenceModel to load into the recurrenceEditor
    // It builds the model based on either:
    // - recurrence rule string (if provided)
    // - or the event being edited recurrence (if the event is repeating)
    // - or simply make a recurrence model w/ default state (by default means: Frequency=Daily, Interval=1)
    makeRecurrence(rule) {
        const
            event     = this.eventRecord,
            eventCopy = event.copy();

        let recurrence = event.recurrence;

        if (!rule && recurrence) {
            recurrence = recurrence.copy();
        }
        else {
            recurrence = new event.recurrenceModel({ rule });
        }

        // bind cloned recurrence to the cloned event
        recurrence.timeSpan = eventCopy;
        // update cloned event w/ start date from the UI field
        eventCopy.setStartDate(this.values.startDate);

        recurrence.suspendTimeSpanNotifying();

        return recurrence;
    }

    onRecurrableEventBeforeSave({ eventRecord, context }) {
        const me = this;

        if (eventRecord.supportsRecurring && (eventRecord.isRecurring || eventRecord.isOccurrence)) {
            me.recurrenceConfirmation.confirm({
                actionType : 'update',
                eventRecord,
                changerFn() {
                    context.finalize(true);
                },
                cancelFn() {
                    context.finalize(false);
                }
            });

            // signalizes that we plan to decide save or not asynchronously
            context.async = true;

            return false;
        }
    }

    showDisplayConfirmationOnEventDelete(eventRecord) {
        return this.isEditing && this.eventRecord === eventRecord && eventRecord.supportsRecurring && (eventRecord.isRecurring || eventRecord.isOccurrence);
    }

    onRecurrableEventBeforeDelete({ eventRecords, context }) {
        const [eventRecord] = eventRecords;

        if (this.showDisplayConfirmationOnEventDelete(eventRecord)) {
            this.recurrenceConfirmation.confirm({
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

    set recurrenceConfirmation(recurrenceConfirmation) {
        this._recurrenceConfirmation = recurrenceConfirmation;
    }

    get recurrenceConfirmation() {
        const me = this;

        let recurrenceConfirmation = me._recurrenceConfirmation;

        if (!recurrenceConfirmation || !recurrenceConfirmation.$name) {
            recurrenceConfirmation = WidgetHelper.createWidget(Object.assign({
                type  : 'recurrenceconfirmation',
                owner : me.getEditor()
            }, recurrenceConfirmation));

            me._recurrenceConfirmation = recurrenceConfirmation;
        }

        return recurrenceConfirmation;
    }

    set recurrenceEditor(recurrenceEditor) {
        this._recurrenceEditor = recurrenceEditor;
    }

    get recurrenceEditor() {
        const me = this;

        let recurrenceEditor = me._recurrenceEditor;

        if (!recurrenceEditor || !recurrenceEditor.$name) {
            recurrenceEditor = WidgetHelper.createWidget(Object.assign({
                type         : 'recurrenceeditor',
                autoShow     : false,
                owner        : me.getEditor(),
                scrollAction : 'realign',
                clippedBy    : [me.scheduler.timeAxisSubGridElement, me.scheduler.bodyContainer],
                constrainTo  : window,
                anchor       : true,
                saveHandler  : me.recurrenceEditorSaveHandler,
                thisObj      : me
            }, recurrenceEditor));

            me._recurrenceEditor = recurrenceEditor;
        }

        return recurrenceEditor;
    }

    showRecurrenceEditor() {
        const
            me = this,
            { recurrenceEditor, eventRecord } = me;

        if (recurrenceEditor && eventRecord && eventRecord.supportsRecurring) {

            // if the event has no recurrence yet ..initialize it before showing recurrence editor
            if (!me.recurrence) {
                me.recurrence = me.makeRecurrence();
            }

            // update the cloned recurrence w/ up to date start date value
            me.recurrence.timeSpan.setStartDate(me.values.startDate);

            // load RecurrenceModel record into the recurrence editor
            recurrenceEditor.record = me.recurrence;

            recurrenceEditor.showBy(me.recurrenceCombo.element);
        }
    }

    onEditRecurrenceClick() {
        this.showRecurrenceEditor();
    }

    loadRecurrenceData(recurrence) {
        this.recurrence = recurrence;

        this.updateRecurrenceFields(recurrence);
    }

    updateRecurrenceFields(recurrence) {
        const me = this;

        if (me.recurrenceCombo) {
            me.recurrenceCombo.recurrence = recurrence;
        }

        // update the recurrence legend
        if (me.editRecurrenceButton) {
            me.editRecurrenceButton.recurrence = recurrence;
            me.editRecurrenceButton.value      = recurrence ? recurrence.rule : null;

            if (recurrence && me.isRecurringEnabled && me.showRecurringUI !== false) {
                me.editRecurrenceButton.show();
            }
            else {
                me.editRecurrenceButton.hide();
            }
        }
    }

    onRecurrenceComboChange({ source, value, userAction }) {
        const me = this;

        if (value == source.customValue) {
            // if user picked "Custom" - show recurrence editor
            if (userAction) {
                me.showRecurrenceEditor();
            }
        }
        // user has picked some frequency -> make a new recurrence based on it
        else {
            me.loadRecurrenceData(value ? me.makeRecurrence(`FREQ=${value}`) : null);
        }
    }

    recurrenceEditorSaveHandler(editor, recurrence) {
        // apply changes to the kept recurrence
        editor.updateRecord(recurrence);

        // update the recurrence related UI
        this.updateRecurrenceFields(recurrence);

        editor.close();
    }

    onDatesChange(...args) {
        super.onDatesChange(...args);

        const
            me = this,
            { editRecurrenceButton } = me;

        if (!me.loadingRecord && editRecurrenceButton) {
            const { startDate } = me.values;
            if (startDate) editRecurrenceButton.eventStartDate = startDate;
        }
    }

    internalLoadRecord(eventRecord, resourceRecord) {
        if (this.recurrenceCombo && eventRecord && eventRecord.supportsRecurring) {
            this.loadRecurrenceData(eventRecord.recurrence ? this.makeRecurrence() : null);
        }
    }
};
