import BryntumWidgetAdapterRegister from '../../../../Core/adapter/widget/util/BryntumWidgetAdapterRegister.js';
import DateHelper from '../../../../Core/helper/DateHelper.js';
import Combo from '../../../../Core/widget/Combo.js';
import RecurrenceDayRuleEncoder from '../../../data/util/recurrence/RecurrenceDayRuleEncoder.js';
import LocalizableComboItems from '../../../../Core/widget/mixin/LocalizableComboItems.js';

/**
 * @module Scheduler/view/recurrence/field/RecurrenceDaysCombo
 */

/**
 * A combobox field allowing to pick days for the `Monthly` and `Yearly` mode in the {@link Scheduler.view.recurrence.RecurrenceEditor recurrence dialog}.
 *
 * @extends Core/widget/Combo
 * @classType recurrencedayscombo
 */
export default class RecurrenceDaysCombo extends LocalizableComboItems(Combo) {

    static get $name() {
        return 'RecurrenceDaysCombo';
    }

    static get defaultConfig() {
        const allDaysValue = 'SU,MO,TU,WE,TH,FR,SA';

        return {
            allDaysValue,
            editable            : false,
            defaultValue        : allDaysValue,
            workingDaysValue    : 'MO,TU,WE,TH,FR',
            nonWorkingDaysValue : 'SU,SA',
            splitCls            : 'b-recurrencedays-split',
            displayField        : 'text',
            valueField          : 'value'
        };
    }

    buildLocalizedItems() {
        const me = this;

        me._weekDays = null;

        return me.weekDays.concat([
            { value : me.allDaysValue,        text : me.L('day'), cls : me.splitCls },
            { value : me.workingDaysValue,    text : me.L('weekday') },
            { value : me.nonWorkingDaysValue, text : me.L('weekend day') }
        ]);
    }

    get weekDays() {
        const me = this;

        if (!me._weekDays) {
            const weekStartDay = DateHelper.weekStartDay;

            const dayNames = DateHelper.getDayNames().map((text, index) => ({ text, value : RecurrenceDayRuleEncoder.encodeDay(index) }));

            // we should start week w/ weekStartDay
            me._weekDays = dayNames.slice(weekStartDay).concat(dayNames.slice(0, weekStartDay));
        }

        return me._weekDays;
    }

    set value(value) {
        const me = this;

        if (value && Array.isArray(value)) {
            value = value.join(',');
        }

        // if the value has no matching option in the store we need to use default value
        if (!value || !me.store.findRecord('value', value)) {
            value = me.defaultValue;
        }

        super.value = value;
    }

    get value() {
        let value = super.value;

        if (value && Array.isArray(value)) {
            value = value.join(',');
        }

        return value;
    }
}

BryntumWidgetAdapterRegister.register('recurrencedayscombo', RecurrenceDaysCombo);
