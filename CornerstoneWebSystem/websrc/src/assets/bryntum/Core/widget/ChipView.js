import List from './List.js';
import TemplateHelper from '../helper/TemplateHelper.js';
import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';

/**
 * @module Core/widget/ChipView
 */

/**
 * Displays an inline series of Chips which may be navigated to, selected and deleted.
 * @extends Core/widget/List
 *
 * @classType chipview
 */
export default class ChipView extends List {
    //region Config
    static get $name() {
        return 'ChipView';
    }

    static get defaultConfig() {
        return {
            itemCls : 'b-chip',

            /**
             * Configure as `true` to display a clickable close icon after the {@link Core.widget.List#config-itemTpl}.
             * When tapped, the configured {@link #config-closeHandler} is called passing the
             * associated record.
             *
             * Chips may also be selected using the `LEFT` and `RIGHT` arrows (And the `Shift` key to
             * do multiple, contiguous election). Pressing the `DELETE` or `BACKSPACE` key passes the
             * selected records to the {@link #config-closeHandler}
             * @config {Boolean}
             * @default
             */
            closable : true,

            /**
             * A template function, which, when passed a record, returns the markup which
             * encapsulates a chip's icon to be placed before the {@link Core.widget.List#config-itemTpl}.
             * @config {Function}
             */
            iconTpl : null,

            /**
             * If {@link #config-closable} is `true`, this is the name of a callback function
             * to handle what the "close" action means.
             * @config {String|Function}
             */
            closeHandler : null
        };
    }

    itemContentTpl(record, i) {
        const me = this;

        return TemplateHelper.tpl`${me.iconTpl ? this.iconTpl(record) : ''}
            ${me.itemTpl(record, i)}
            ${me.closable ? '<div class="b-icon b-close-icon b-icon-clear" data-noselect></div>' : ''}`;
    }

    onInternalKeyDown(event) {
        if (event.key === 'Delete' || event.key === 'Backspace' && this.selected.count) {
            this.callback(this.closeHandler, this.owner, [this.selected.values, { isKeyEvent : true }]);
        }
        else {
            super.onInternalKeyDown(event);
        }
    }

    onClick(event) {
        const me = this,
            item = event.target.closest(`.${me.itemCls}`);

        if (me.closable && event.target.classList.contains('b-close-icon')) {
            const record = me.store.getAt(parseInt(item.dataset.index));

            me.callback(me.closeHandler, me.owner, [[record]]);
        }
        else {
            super.onClick(event);
        }
    }
}

BryntumWidgetAdapterRegister.register('chipview', ChipView);
