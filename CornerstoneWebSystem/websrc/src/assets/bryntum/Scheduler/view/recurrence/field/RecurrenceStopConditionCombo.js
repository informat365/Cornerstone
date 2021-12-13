import BryntumWidgetAdapterRegister from '../../../../Core/adapter/widget/util/BryntumWidgetAdapterRegister.js';
import Combo from '../../../../Core/widget/Combo.js';
import LocalizableComboItems from '../../../../Core/widget/mixin/LocalizableComboItems.js';

/**
 * @module Scheduler/view/recurrence/field/RecurrenceStopConditionCombo
 */

/**
 * A combobox field allowing to choose stop condition for the recurrence in the {@link Scheduler.view.recurrence.RecurrenceEditor recurrence dialog}.
 *
 * @extends Core/widget/Combo
 * @classType recurrencestopconditioncombo
 */
export default class RecurrenceStopConditionCombo extends LocalizableComboItems(Combo) {

    static get $name() {
        return 'RecurrenceStopConditionCombo';
    }

    static get defaultConfig() {
        return {
            editable     : false,
            placeholder  : 'Never',
            displayField : 'text',
            valueField   : 'value'
        };
    }

    buildLocalizedItems() {
        return [
            { value : 'never', text : this.L('Never') },
            { value : 'count', text : this.L('After') },
            { value : 'date',  text : this.L('On date') }
        ];
    }

    set value(value) {
        // Use 'never' instead of falsy value
        value = value || 'never';

        super.value = value;
    }

    get value() {
        return super.value;
    }

    get recurrence() {
        return this._recurrence;
    }

    set recurrence(recurrence) {
        let value = null;

        if (recurrence.endDate) {
            value = 'date';
        }
        else if (recurrence.count) {
            value = 'count';
        }

        this._recurrence = recurrence;

        this.value = value;
    }
};

BryntumWidgetAdapterRegister.register('recurrencestopconditioncombo', RecurrenceStopConditionCombo);
