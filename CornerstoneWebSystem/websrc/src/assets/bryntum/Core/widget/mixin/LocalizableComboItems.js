import Combo from '../Combo.js';

/**
 * @module Core/widget/mixin/LocalizableComboItems
 */

/**
 * A mixin that regenerates a combobox items on locale change.
 * @private
 * @mixin
 */
export default Target => class LocalizableComboItems extends (Target || Combo) {

    static get $name() {
        return 'LocalizableComboItems';
    }

    static get defaultConfig() {
        return {
            items : true
        };
    }

    set items(items) {
        if (items === true) {
            items = this.buildLocalizedItems();
        }
        super.items = items;
    }

    get items() {
        return super.items;
    }

    construct(...args) {
        // set a special flag to skip unneeded store translation on construction step
        this.inConstruct = true;
        super.construct(...args);
        this.inConstruct = false;
    }

    buildLocalizedItems() {
        return [];
    }

    updateLocalizedItems() {
        const me = this;

        if (me.store && !me.inConstruct) {
            const { value } = me;

            // TODO: just updating new data is not enough ..selected value text is shown wrong :(
            // it seems caused by selected record cache
            // review this code after #9387 is fixed
            me.store.data = me.buildLocalizedItems();
            me.value = null;
            me.value = value;
            me.syncInputFieldValue(true);
        }
    }

    updateLocalization() {
        this.updateLocalizedItems();
        super.updateLocalization();
    }
};
