import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';
import ObjectHelper from '../helper/ObjectHelper.js';
import TemplateHelper from '../helper/TemplateHelper.js';
import Field from './Field.js';

/**
 * @module Core/widget/NumberField
 */

const decimalSeparators  = /[.,]/;

/**
 * Number field widget. Wraps native `<input type="number">`
 *
 * @extends Core/widget/Field
 *
 * @example
 * let number = new NumberField({
 *   min: 1,
 *   max: 5,
 *   value: 3
 * });
 *
 * @classType numberfield
 * @externalexample widget/NumberField.js
 */
export default class NumberField extends Field {

    //region Config

    static get $name() {
        return 'NumberField';
    }

    static get defaultConfig() {
        return {

            /**
             * Min value
             * @config {Number}
             */
            min : null,

            /**
             * Max value
             * @config {Number}
             */
            max : null,

            /**
             * Step size for spin button clicks.
             * @config {Number}
             * @default
             */
            step : 1,

            /**
             * Initial value
             * @config {Number}
             */
            value : 0,

            /**
             * The number of decimal places to allow. Defaults to no constraint.
             * @config {Number}
             * @default
             */
            decimalPrecision : null,

            /**
             * The maximum number of leading zeroes to show. Defaults to no constraint.
             * @config {Number}
             * @default
             */
            leadingZeroes : null,

            triggers : {
                spin : {
                    type : 'spintrigger'
                }
            },

            attributes : [
                'placeholder',
                'autoComplete',
                'min',
                'max',
                'tabIndex'
            ],

            /**
             * Controls how change events are triggered when stepping the value up or down using either spinners or
             * arrow keys.
             *
             * Configure with:
             * * `true` to trigger a change event per step
             * * `false` to not trigger change while stepping. Will trigger on blur/Enter
             * * A number of milliseconds to buffer the change event, triggering when no steps are performed during that
             *   period of time.
             *
             * @config {Boolean|Number}
             * @default
             */
            changeOnSpin : true
        };
    }

    //endregion

    //region Init

    construct(config) {
        const me = this;

        super.construct(config);

        // Support for selecting all by double click in empty input area
        // Browsers work differently at this case
        me.input.addEventListener('dblclick', () => {
            me.select();
        });

        if (typeof me.changeOnSpin === 'number') {
            me.bufferedSpinChange = me.buffer(me.triggerChange, me.changeOnSpin);
        }
    }

    //endregion

    //region Template
    
    inputTemplate() {
        const
            me    = this,
            style = 'inputWidth' in me
                ? `style="width:${me.inputWidth}${typeof me.inputWidth === 'number' ? 'px' : ''}"`
                : '';

        return TemplateHelper.tpl`
            <input type="number"
                reference="input"
                value="${me._value}"
                name="${me.name || me.id}"
                id="${me.id}_input"
                step="any"
                ${style}
                ${me.attributeString}/>
            `;
    }

    //endregion

    //region Internal functions
    
    internalOnKeyPress(e) {
        // Overriding native arrow key spin behaviour, which differs between browsers
        if (e.type === 'keydown') {
            if (e.key === 'ArrowUp') {
                this.doSpinUp();
                e.preventDefault();
            }
            else if (e.key === 'ArrowDown') {
                this.doSpinDown();
                e.preventDefault();
            }
        }
        
        super.internalOnKeyPress(e);
    }

    doSpinUp() {
        const
            me           = this,
            { min, max } = me;

        let newValue = (me.value || 0) + me.step;

        if (!isNaN(min) && newValue < min) {
            newValue = min;
        }

        if (isNaN(max) || newValue <= max) {
            me.applySpinChange(newValue);
        }
    }

    doSpinDown() {
        const
            me           = this,
            { min, max } = me;
        
        let newValue = (me.value || 0) - me.step;

        if (!isNaN(max) && newValue > max) {
            newValue = max;
        }

        if (isNaN(min) || newValue >= min) {
            me.applySpinChange(newValue);
        }
    }

    applySpinChange(newValue) {
        const me = this;

        me._isUserAction = true;

        // Should not trigger change immediately?
        if (me.changeOnSpin !== true) {
            // Silence the change
            me.silenceChange = true;
            // Optionally buffer the change
            me.bufferedSpinChange && me.bufferedSpinChange(null, true);
        }
        
        me.value = newValue;

        me._isUserAction = false;
        me.silenceChange = false;
    }

    triggerChange() {
        if (!this.silenceChange) {
            super.triggerChange(...arguments);
        }
    }

    internalOnInput(event) {
        const
            me = this,
            { input } = me,
            value = me.formatValue(input.value);

        if (input.value !== value) {
            input.value = value;
        }

        super.internalOnInput(event);
    }

    static formatValue(value, leadingZeroes, decimalPrecision) {
        if (typeof value !== 'string') {
            return value;
        }
        let [integer, decimal] = value.split(decimalSeparators);

        if (decimalPrecision && decimal && decimal.length > decimalPrecision) {
            // Trim back to the configured decimals
            decimal = decimal.substr(0, decimalPrecision);
        }

        if (leadingZeroes && integer && integer.length < leadingZeroes) {
            // Put zeros to the beginning
            integer = integer.padStart(leadingZeroes, '0');
        }
        return [integer, decimal].filter(v => v).join('.');
    }

    formatValue(value) {
        return NumberField.formatValue(value, this.leadingZeroes, this.decimalPrecision);
    }

    //endregion

    //region Getters/Setters

    /**
     * Step size for spin button clicks.
     * @property {Number}
     */

    set step(step) {
        this.element.classList[step ? 'remove' : 'add']('b-hide-spinner');
        this._step = step;
    }

    get step() {
        return this._step;
    }

    /**
     * Min value
     * @property {Number}
     */
    set min(min) {
        this._min = min;

        if (this.input) {
            this.input.min = min;
        }
    }

    get min() {
        return this._min;
    }

    /**
     * Max value
     * @property {Number}
     */
    set max(max) {
        this._max = max;

        if (this.input) {
            this.input.max = max;
        }
    }

    get max() {
        return this._max;
    }

    /**
     * Get/set the NumberField's value
     * @property {Number}
     */
    set value(value) {
        const me = this;

        if (value || value === 0) {
            let valueIsNaN;

            // We insist on a number as the value
            if (typeof value !== 'number') {
                value = (typeof value === 'string') ? parseFloat(value) : Number(value);

                valueIsNaN = isNaN(value);
                if (valueIsNaN) {
                    value = '';
                }
            }
            if (!valueIsNaN && me.decimalPrecision != null) {
                value = ObjectHelper.round(value, me.decimalPrecision);
            }
        }
        else {
            value = me.clearable ? undefined : 0;
        }

        // Reject non-changes & not interested in non-number values
        if (me.value !== value) {
            super.value = value;
            if (me.leadingZeroes) {
                me.input.value = me.formatValue(value.toString());
            }
        }
    }

    get value() {
        return super.value;
    }

    //endregion
}

BryntumWidgetAdapterRegister.register('numberfield', NumberField);
BryntumWidgetAdapterRegister.register('number', NumberField);
