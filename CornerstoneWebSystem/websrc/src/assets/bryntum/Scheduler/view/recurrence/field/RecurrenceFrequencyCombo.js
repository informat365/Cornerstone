import BryntumWidgetAdapterRegister from '../../../../Core/adapter/widget/util/BryntumWidgetAdapterRegister.js';
import Combo from '../../../../Core/widget/Combo.js';
import LocalizableComboItems from '../../../../Core/widget/mixin/LocalizableComboItems.js';

/**
 * @module Scheduler/view/recurrence/field/RecurrenceFrequencyCombo
 */

/**
 * A combobox field allowing to pick frequency in the {@link Scheduler.view.recurrence.RecurrenceEditor recurrence dialog}.
 *
 * @extends Core/widget/Combo
 * @classType recurrencefrequencycombo
 */
export default class RecurrenceFrequencyCombo extends LocalizableComboItems(Combo) {

    static get $name() {
        return 'RecurrenceFrequencyCombo';
    }

    static get defaultConfig() {
        return {
            editable     : false,
            displayField : 'text',
            valueField   : 'value'
        };
    }

    buildLocalizedItems() {
        return [
            { value : 'DAILY',   text : this.L('Daily') },
            { value : 'WEEKLY',  text : this.L('Weekly') },
            { value : 'MONTHLY', text : this.L('Monthly') },
            { value : 'YEARLY',  text : this.L('Yearly') }
        ];
    }
};

BryntumWidgetAdapterRegister.register('recurrencefrequencycombo', RecurrenceFrequencyCombo);
