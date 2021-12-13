import BryntumWidgetAdapterRegister from '../../../../Core/adapter/widget/util/BryntumWidgetAdapterRegister.js';
import DateHelper from '../../../../Core/helper/DateHelper.js';
import ButtonGroup from '../../../../Core/widget/ButtonGroup.js';

/**
 * A segmented button field allowing to pick months for the `Yearly` mode in the {@link Scheduler.view.recurrence.RecurrenceEditor recurrence dialog}.
 *
 * @extends Core/widget/ButtonGroup
 */
export default class RecurrenceMonthsButtonGroup extends ButtonGroup {

    static get $name() {
        return 'RecurrenceMonthsButtonGroup';
    }

    static get defaultConfig() {
        return {
            defaults : {
                toggleable : true,
                cls        : 'b-raised'
            }
        };
    }

    construct(config = {}) {
        const me = this;

        config.columns = 4;
        config.items   = me.buildItems();

        super.construct(config);
    }

    buildItems() {
        return DateHelper.getMonthNames().map((item, index) => ({
            text  : item.substring(0, 3),
            value : index + 1 // 1-based
        }));
    }

    updateItemText(item) {
        item.text = DateHelper.getMonthName(item.value - 1).substring(0, 3);
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

BryntumWidgetAdapterRegister.register('recurrencemonthsbuttongroup', RecurrenceMonthsButtonGroup);
