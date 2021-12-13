import BryntumWidgetAdapterRegister from '../../../../Core/adapter/widget/util/BryntumWidgetAdapterRegister.js';
import ButtonGroup from '../../../../Core/widget/ButtonGroup.js';

/**
 * A segmented button field allowing to pick month days for the `Monthly` mode in the {@link Scheduler.view.recurrence.RecurrenceEditor recurrence dialog}.
 *
 * @extends Core/widget/ButtonGroup
 */
export default class RecurrenceMonthDaysButtonGroup extends ButtonGroup {

    static get $name() {
        return 'RecurrenceMonthDaysButtonGroup';
    }

    static get defaultConfig() {
        return {
            defaults : {
                toggleable : true,
                cls        : 'b-raised'
            }
        };
    }

    get minValue() {
        return 1;
    }

    get maxValue() {
        return 31;
    }

    construct(config = {}) {
        const me = this;

        config.columns = 7;
        config.items   = me.buildItems();

        super.construct(config);
    }

    buildItems() {
        const
            me    = this,
            items = [];

        for (let value = me.minValue; value <= me.maxValue; value++) {
            // button config
            items.push({
                text : value + '',
                value
            });
        }

        return items;
    }

    get widgetClassList() {
        const classList = super.widgetClassList;
        // to look more like a real field
        classList.push('b-field');
        return classList;
    }

};

BryntumWidgetAdapterRegister.register('recurrencemonthdaysbuttongroup', RecurrenceMonthDaysButtonGroup);
