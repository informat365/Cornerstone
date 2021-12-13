import BryntumWidgetAdapterRegister from '../../../Core/adapter/widget/util/BryntumWidgetAdapterRegister.js';
import RecurrenceDayRuleEncoder from '../../data/util/recurrence/RecurrenceDayRuleEncoder.js';
import Popup from '../../../Core/widget/Popup.js';
import '../../../Core/widget/Widget.js';
import '../../../Core/widget/Button.js';
import '../../../Core/widget/Checkbox.js';
import '../../../Core/widget/DateField.js';
import '../../../Core/widget/NumberField.js';
import './field/RecurrenceFrequencyCombo.js';
import './field/RecurrenceDaysCombo.js';
import './field/RecurrenceDaysButtonGroup.js';
import './field/RecurrenceMonthDaysButtonGroup.js';
import './field/RecurrenceMonthsButtonGroup.js';
import './field/RecurrenceStopConditionCombo.js';
import './field/RecurrencePositionsCombo.js';

/**
 * @module Scheduler/view/recurrence/RecurrenceEditor
 */

/**
 * Class implementing a dialog to edit {@link Scheduler.model.RecurrenceModel Recurrence model}.
 * The class is used by the {@link Scheduler.feature.RecurringEvents recurring events} feature and you don't need to instantiate it normally.
 * Before showing the dialog need to use {@link #property-record} to load a {@link Scheduler.model.RecurrenceModel Recurrence model}
 * data into the editor fields. For example:
 *
 * ```javascript
 * // make the editor instance
 * const editor = new RecurrenceEditor();
 * // load recurrnce model into it
 * editor.record = new RecurrenceModel({ frequency : "WEEKLY" });
 * // display the editor
 * editor.show();
 * ```
 *
 * @extends Core/widget/Popup
 * @classType recurrenceeditor
 */
export default class RecurrenceEditor extends Popup {

    static get $name() {
        return 'RecurrenceEditor';
    }

    static get defaultConfig() {
        return {
            draggable : true,
            closable  : true,
            floating  : true,
            cls       : 'b-recurrenceeditor',
            title     : 'L{Repeat event}',
            autoClose : true,
            width     : 400,
            items     : {
                frequencyField           : true,
                daysButtonField          : true,
                monthDaysRadioField      : true,
                monthDaysButtonField     : true,
                monthsButtonField        : true,
                positionAndDayRadioField : true,
                stopRecurrenceField      : true,
                countField               : true,
                endDateField             : true,
                intervalField            : true,
                positionsCombo           : true,
                daysCombo                : true
            },
            namedItems : {
                frequencyField : {
                    type        : 'recurrencefrequencycombo',
                    name        : 'frequency',
                    localeClass : this,
                    label       : 'L{Frequency}',
                    weight      : 10,
                    onChange    : 'up.onFrequencyFieldChange'
                },
                intervalField : {
                    type        : 'numberfield',
                    weight      : 15,
                    name        : 'interval',
                    localeClass : this,
                    label       : 'L{Every}',
                    labels      : [
                        {
                            align     : 'end',
                            reference : 'intervalUnit',
                            style     : {
                                width : '50em'
                            }
                        }
                    ],
                    min        : 1,
                    allowBlank : false
                },
                daysButtonField : {
                    type         : 'recurrencedaysbuttongroup',
                    weight       : 20,
                    name         : 'days',
                    forFrequency : 'WEEKLY'
                },
                // the radio button enabling "monthDaysButtonField" in MONTHLY mode
                monthDaysRadioField : {
                    type         : 'checkbox',
                    weight       : 30,
                    toggleGroup  : 'radio',
                    forFrequency : 'MONTHLY',
                    localeClass  : this,
                    label        : 'L{Each}',
                    checked      : true,
                    onChange     : 'up.onMonthDaysRadioFieldChange'
                },
                monthDaysButtonField : {
                    type         : 'recurrencemonthdaysbuttongroup',
                    weight       : 40,
                    name         : 'monthDays',
                    forFrequency : 'MONTHLY'
                },
                monthsButtonField : {
                    type         : 'recurrencemonthsbuttongroup',
                    weight       : 50,
                    name         : 'months',
                    forFrequency : 'YEARLY'
                },
                // the radio button enabling positions & days combos in MONTLY & YEARLY modes
                positionAndDayRadioField : {
                    type         : 'checkbox',
                    weight       : 60,
                    toggleGroup  : 'radio',
                    forFrequency : 'MONTHLY|YEARLY',
                    localeClass  : this,
                    label        : 'L{On the}',
                    onChange     : 'up.onPositionAndDayRadioFieldChange'
                },
                positionsCombo : {
                    type         : 'recurrencepositionscombo',
                    weight       : 80,
                    name         : 'positions',
                    forFrequency : 'MONTHLY|YEARLY'
                },
                daysCombo : {
                    type         : 'recurrencedayscombo',
                    weight       : 90,
                    name         : 'days',
                    forFrequency : 'MONTHLY|YEARLY',
                    flex         : 1
                },
                stopRecurrenceField : {
                    type        : 'recurrencestopconditioncombo',
                    weight      : 100,
                    localeClass : this,
                    label       : 'L{End repeat}',
                    onChange    : 'up.onStopRecurrenceFieldChange'
                },
                countField : {
                    type        : 'numberfield',
                    weight      : 110,
                    name        : 'count',
                    min         : 2,
                    localeClass : this,
                    allowBlank  : false,
                    disabled    : true,
                    label       : ' ',
                    labels      : [
                        {
                            align     : 'end',
                            reference : 'countUnit',
                            label     : 'L{time(s)}'
                        }
                    ]

                },
                endDateField : {
                    type       : 'datefield',
                    weight     : 120,
                    name       : 'endDate',
                    hidden     : true,
                    disabled   : true,
                    label      : ' ',
                    allowBlank : false
                }
            },
            bbar : [
                {
                    type : 'widget',
                    cls  : 'b-label-filler'
                },
                {
                    type        : 'button',
                    color       : 'b-green',
                    ref         : 'saveButton',
                    localeClass : this,
                    text        : 'L{Save}',
                    onClick     : 'up.onSaveClick'
                },
                {
                    type        : 'button',
                    color       : 'b-gray',
                    ref         : 'cancelButton',
                    localeClass : this,
                    text        : 'L{Cancel}',
                    onClick     : 'up.onCancelClick'
                }

            ]
        };
    }

    /**
     * Recurrence record loaded into the editor.
     * @property {Scheduler.model.RecurrenceModel}
     */
    get record() {
        return super.record;
    }

    set record(record) {
        super.record = record;

        const
            me        = this,
            event     = record.timeSpan,
            startDate = event && event.startDate,
            {
                daysButtonField,
                monthDaysButtonField,
                monthsButtonField,
                monthDaysRadioField,
                positionAndDayRadioField,
                stopRecurrenceField
            } = me.widgetMap;

        // some fields default values are calculated based on event "startDate" value
        if (startDate) {
            // if no "days" value provided
            if (!record.days || !record.days.length) {
                daysButtonField.value = [RecurrenceDayRuleEncoder.encodeDay(startDate.getDay())];
            }

            // if no "monthDays" value provided
            if (!record.monthDays || !record.monthDays.length) {
                monthDaysButtonField.value = startDate.getDate();
            }

            // if no "months" value provided
            if (!record.months || !record.months.length) {
                monthsButtonField.value = startDate.getMonth() + 1;
            }
        }

        // if the record has both "days" & "positions" fields set check "On the" checkbox
        if (record.days && record.positions) {
            positionAndDayRadioField.check();
            // TODO: if toggleGroup members are not painted automatic unchecking doesn't work
            if (!me.isPainted) {
                monthDaysRadioField.uncheck();
            }
        }
        else {
            monthDaysRadioField.check();
            // TODO: if toggleGroup members are not painted automatic unchecking doesn't work
            if (!me.isPainted) {
                positionAndDayRadioField.uncheck();
            }
        }

        stopRecurrenceField.recurrence = record;
    }

    onSaveClick() {
        const me = this;

        if (me.saveHandler) {
            me.saveHandler.call(me.thisObj || me, me, me.record);
        }
        else {
            me.updateRecord();
            me.close();
        }
    }

    onCancelClick() {
        const me = this;

        if (me.cancelHandler) {
            me.cancelHandler.call(me.thisObj || me, me, me.record);
        }
        else {
            me.close();
        }
    }

    /**
     * Updates the provided recurrence model with the contained form data.
     * If recurrence model is not provided updates the last loaded recurrence model.
     */
    updateRecord(recurrence) {
        // get values relevant to the RecurrenceModel (from enabled fields only)
        const values = this.getValues((w) => w.name in recurrence && !w.disabled);

        recurrence.set(values);
    }

    toggleStopFields() {
        const
            me = this,
            { countField, endDateField } = me.widgetMap;

        switch (me.widgetMap.stopRecurrenceField.value) {

            case 'count' :
                countField.show();
                countField.enable();
                endDateField.hide();
                endDateField.disable();
                break;

            case 'date' :
                countField.hide();
                countField.disable();
                endDateField.show();
                endDateField.enable();
                break;

            default :
                countField.hide();
                endDateField.hide();
                countField.disable();
                endDateField.disable();
        }
    }

    onMonthDaysRadioFieldChange({ checked }) {
        const { monthDaysButtonField } = this.widgetMap;

        monthDaysButtonField.disabled = !checked || !this.isWidgetAvailableForFrequency(monthDaysButtonField);
    }

    onPositionAndDayRadioFieldChange({ checked }) {
        const { daysCombo, positionsCombo } = this.widgetMap;

        // toggle day & positions combos
        daysCombo.disabled = positionsCombo.disabled = !checked || !this.isWidgetAvailableForFrequency(daysCombo);
    }

    onStopRecurrenceFieldChange() {
        this.toggleStopFields();
    }

    isWidgetAvailableForFrequency(widget, frequency = this.widgetMap.frequencyField.value) {
        return !widget.forFrequency || widget.forFrequency.indexOf(frequency) > -1;
    }

    onFrequencyFieldChange({ value, valid }) {
        const
            me  = this,
            items = me.queryAll((w) => 'forFrequency' in w);

        if (valid && value) {
            for (let i = 0; i < items.length; i++) {
                const item = items[i];

                if (me.isWidgetAvailableForFrequency(item, value)) {
                    item.show();
                    item.enable();
                }
                else {
                    item.hide();
                    item.disable();
                }
            }

            // The following lines are added to satisfy the 904_unused localization test
            // to let it know that these locales are used:
            // this.L('DAILYintervalUnit')
            // this.L('WEEKLYintervalUnit')
            // this.L('MONTHLYintervalUnit')
            // this.L('YEARLYintervalUnit')
            me.widgetMap.intervalField.intervalUnit.innerHTML = me.L(`${value}intervalUnit`);

            me.toggleFieldsState();
        }
    }

    toggleFieldsState() {
        const me = this,
            { widgetMap } = this;

        me.onMonthDaysRadioFieldChange({ checked : widgetMap.monthDaysRadioField.checked });
        me.onPositionAndDayRadioFieldChange({ checked : widgetMap.positionAndDayRadioField.checked });
        me.onStopRecurrenceFieldChange();
    }

    updateLocalization() {
        // do extra labels translation (not auto-translated yet)
        const { countField, intervalField, frequencyField } = this.widgetMap;

        countField.countUnit.innerHTML = this.L('time(s)');
        intervalField.intervalUnit.innerHTML = this.L(`${frequencyField.value}intervalUnit`);

        super.updateLocalization();
    }

}

BryntumWidgetAdapterRegister.register('recurrenceeditor', RecurrenceEditor);
