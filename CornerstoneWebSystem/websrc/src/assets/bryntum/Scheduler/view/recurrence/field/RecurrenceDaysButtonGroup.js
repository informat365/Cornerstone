import BryntumWidgetAdapterRegister from '../../../../Core/adapter/widget/util/BryntumWidgetAdapterRegister.js';
import DateHelper from '../../../../Core/helper/DateHelper.js';
import ButtonGroup from '../../../../Core/widget/ButtonGroup.js';
import RecurrenceDayRuleEncoder from '../../../data/util/recurrence/RecurrenceDayRuleEncoder.js';

/**
 * @module Scheduler/view/recurrence/field/RecurrenceDaysButtonGroup
 */

/**
 * A segmented button field allowing to pick days for the "Weekly" mode in the {@link Scheduler.view.recurrence.RecurrenceEditor recurrence dialog}.
 *
 * @extends Core/widget/ButtonGroup
 */
export default class RecurrenceDaysButtonGroup extends ButtonGroup {

    static get $name() {
        return 'RecurrenceDaysButtonGroup';
    }

    static get defaultConfig() {
        return {
            defaults : {
                cls        : 'b-raised',
                toggleable : true
            }
        };
    }

    construct(config = {}) {
        const me = this;

        config.columns = 7;
        config.items   = me.buildItems();

        super.construct(config);
    }

    updateItemText(item) {
        const day = RecurrenceDayRuleEncoder.decodeDay(item.value)[0];

        item.text = DateHelper.getDayName(day).substring(0, 3);
    }

    buildItems() {
        const me = this;

        if (!me.__items) {
            const weekStartDay = DateHelper.weekStartDay;

            const dayNames = DateHelper.getDayNames().map((text, index) => ({
                text  : text.substring(0, 3),
                value : RecurrenceDayRuleEncoder.encodeDay(index)
            }));

            // we should start week w/ weekStartDay
            me.__items = dayNames.slice(weekStartDay).concat(dayNames.slice(0, weekStartDay));
        }

        return me.__items;
    }

    set value(value) {
        if (value && Array.isArray(value)) {
            value = value.join(',');
        }

        super.value = value;
    }

    get value() {
        let value = super.value;

        if (value && Array.isArray(value)) {
            value = value.join(',');
        }

        return value;
        // return value ? value.split(',') : [];
    }

    onLocaleChange() {
        // update button texts on locale switch
        this.items.forEach(this.updateItemText, this);
    }

    updateLocalization() {
        this.onLocaleChange();
        super.updateLocalization();
    }

    get widgetClassList() {
        const classList = super.widgetClassList;
        // to look more like a real field
        classList.push('b-field');
        return classList;
    }
};

BryntumWidgetAdapterRegister.register('recurrencedaysbuttongroup', RecurrenceDaysButtonGroup);
