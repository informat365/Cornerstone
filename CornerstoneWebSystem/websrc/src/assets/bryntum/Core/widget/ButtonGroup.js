import Container from './Container.js';
import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';
import Button from './Button.js';
import IdHelper from '../helper/IdHelper.js';

/**
 * @module Core/widget/ButtonGroup
 */

/**
 * A specialized container that holds buttons, displaying them in a horizontal group with borders adjusted to make them
 * stick together.
 *
 * Trying to add other widgets than buttons will throw an exception.
 *
 * ```javascript
 * new ButtonGroup({
 *   items : [
 *       { icon : 'b-fa b-fa-kiwi-bird' },
 *       { icon : 'b-fa b-fa-kiwi-otter' },
 *       { icon : 'b-fa b-fa-kiwi-rabbit' },
 *       ...
 *   ]
 * });
 * ```
 *
 * @externalexample widget/ButtonGroup.js
 * @classType buttonGroup
 * @extends Core/widget/Container
 */
export default class ButtonGroup extends Container {
    static get $name() {
        return 'ButtonGroup';
    }

    static get defaultConfig() {
        return {
            defaultType : 'button',

            /**
             * Custom CSS class to add to element. When using raised buttons (cls 'b-raised' on the buttons), the group
             * will look nicer if you also set that cls on the group.
             *
             * ```
             * new ButtonGroup({
             *   cls : 'b-raised,
             *   items : [
             *       { icon : 'b-fa b-fa-unicorn', cls : 'b-raised' },
             *       ...
             *   ]
             * });
             * ```
             *
             * @config {String}
             * @category CSS
             */
            cls : null,

            /**
             * An array of Buttons or typed Button config objects.
             * @config {Object[]|Core.widget.Button[]}
             */
            items : null,

            /**
             * Default color to apply to all contained buttons, see {@link Core.widget.Button#config-color Button#color}.
             * Individual buttons can override the default.
             * @config {String}
             */
            color : null,

            /**
             * Set to `true` to turn the ButtonGroup into a toggle group, assigning a generated value to each contained
             * buttons {@link Core.widget.Button#config-toggleGroup toggleGroup config}. Individual buttons can
             * override the default.
             */
            toggleGroup : null,

            valueSeparator : ',',

            columns : null
        };
    }

    construct(config = {}) {
        super.construct(config);

        // track button clicks to update value
        this.items.forEach(w => w.on('click', this.onItemClick, this));
    }

    onItemClick() {
        // reset cached value to revalidate next time it's requested
        this._value = null;
    }

    createWidget(widget) {
        const me = this;

        if (me.color && !widget.color) {
            widget.color = me.color;
        }

        if (me.toggleGroup && !widget.toggleGroup) {

            if (typeof me.toggleGroup === 'boolean') {
                me.toggleGroup = IdHelper.generateId('toggleGroup');
            }

            widget.toggleGroup = me.toggleGroup;
        }

        if (me.columns) {
            widget.width = `${100 / me.columns}%`;
        }

        const button = super.createWidget(widget);

        if (!(button instanceof Button)) {
            throw new Error('A ButtonGroup can only contain buttons');
        }

        return button;
    }

    get value() {
        // if we don't have cached value
        // let's calculate it based on item values
        if (!this._value) {
            const values = [];

            // collect pressed item values
            this.items.forEach(w => {
                if (w.pressed && w.value !== undefined) values.push(w.value);
            });

            // build a string
            this._value = values.join(this.valueSeparator);
        }

        return this._value;
    }

    set value(value) {
        if (!Array.isArray(value)) {
            if (value === undefined || value === null) {
                value = [];
            }
            else if (typeof value == 'string') {
                value = value.split(this.valueSeparator);
            }
            else {
                value = [value];
            }
        }

        this._value = value;

        // reflect value on items
        this.items.forEach(w => {
            if (w.value !== undefined) {
                w.pressed = value.indexOf(w.value) > -1;
            }
        });
    }

    get disabled() {
        return super.disabled;
    }

    set disabled(state) {
        super.disabled = state;
        this.items.forEach(w => w.disabled = state);
    }

    get widgetClassList() {
        const classList = super.widgetClassList;
        // if the buttons should be shown in rows
        if (this.columns) classList.push('b-columned');
        return classList;
    }
}

BryntumWidgetAdapterRegister.register('buttonGroup', ButtonGroup);
