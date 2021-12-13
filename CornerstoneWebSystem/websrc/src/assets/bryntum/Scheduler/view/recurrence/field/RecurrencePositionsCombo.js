import BryntumWidgetAdapterRegister from '../../../../Core/adapter/widget/util/BryntumWidgetAdapterRegister.js';
import ArrayHelper from '../../../../Core/helper/ArrayHelper.js';
import Combo from '../../../../Core/widget/Combo.js';
import LocalizableComboItems from '../../../../Core/widget/mixin/LocalizableComboItems.js';

/**
 * @module Scheduler/view/recurrence/field/RecurrencePositionsCombo
 */

/**
 * A combobox field allowing to specify day positions in the {@link Scheduler.view.recurrence.RecurrenceEditor recurrence editor}.
 *
 * @extends Core/widget/Combo
 * @classType recurrencepositionscombo
 */
export default class RecurrencePositionsCombo extends LocalizableComboItems(Combo) {

    static get $name() {
        return 'RecurrencePositionsCombo';
    }

    static get defaultConfig() {
        return {
            editable     : false,
            splitCls     : 'b-sch-recurrencepositions-split',
            displayField : 'text',
            valueField   : 'value',
            defaultValue : 1,
            maxPosition  : 5
        };
    }

    buildLocalizedItems() {
        const me = this;

        return me.buildDayNumbers().concat([
            // the following lines are added to satisfy the 904_unused localization test
            // to let it know that these locales are used:
            // this.L('position-1')
            { value : '-1', text : me.L('position-1'), cls : me.splitCls }
        ]);
    }

    buildDayNumbers() {
        const me = this;

        // the following lines are added to satisfy the 904_unused localization test
        // to let it know that these locales are used:
        // this.L('position1')
        // this.L('position2')
        // this.L('position3')
        // this.L('position4')
        // this.L('position5')

        return ArrayHelper.populate(me.maxPosition, i => (
            { value : i, text : me.L(`position${i}`) }
        ));
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
        const value = super.value;

        return value ? `${value}`.split(',').map(item => parseInt(item, 10)) : [];
    }

};

BryntumWidgetAdapterRegister.register('recurrencepositionscombo', RecurrencePositionsCombo);
