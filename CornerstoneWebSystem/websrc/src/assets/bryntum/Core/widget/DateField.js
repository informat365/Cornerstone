//TODO: picker icon (calendar) should show day number
import PickerField from './PickerField.js';
import DatePicker from './DatePicker.js';
import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';
import DateHelper from '../helper/DateHelper.js';
import TemplateHelper from '../helper/TemplateHelper.js';
import ObjectHelper from '../helper/ObjectHelper.js';

/**
 * @module Core/widget/DateField
 */

/**
 * Date field widget (text field + date picker).
 *
 * @extends Core/widget/PickerField
 *
 * @example
 * // minimal DateField config with date format specified
 * let dateField = new DateField({
 *   format: 'YYMMDD'
 * });
 *
 * @classType datefield
 * @externalexample widget/DateField.js
 */
export default class DateField extends PickerField {
    //region Config
    static get $name() {
        return 'DateField';
    }

    static get defaultConfig() {
        return {
            /**
             * Format for date displayed in field. Defaults to using long date format, as defined by current locale (`L`)
             * @config {String}
             * @default
             */
            format : 'L',

            fallbackFormat : 'YYYY-MM-DD', // same for all languages

            /**
             * Format for date in picker. Uses localized format per default
             * @config {String}
             */
            pickerFormat : null,

            triggers : {
                expand : {
                    cls     : 'b-icon-calendar',
                    handler : 'onTriggerClick',
                    weight  : 200
                }
            },

            stepTriggers : {
                back : {
                    cls     : 'b-icon-angle-left b-step-trigger',
                    handler : 'onBackClick',
                    align   : 'start',
                    weight  : 100
                },

                forward : {
                    cls     : 'b-icon-angle-right b-step-trigger',
                    handler : 'onForwardClick',
                    align   : 'end',
                    weight  : 100
                }
            },

            // An optional extra CSS class to add to the picker container element
            calendarContainerCls : '',

            /**
             * Min value
             * @config {String|Date}
             */
            min : null,

            /**
             * Max value
             * @config {String|Date}
             */
            max : null,

            /**
             * Time increment duration value. If specified, `forward` and `back` triggers are displayed.
             * The value is taken to be a string consisting of the numeric magnitude and the units.
             * The units may be a recognised unit abbreviation of this locale or the full local unit name.
             * For example `'1d'` or `'1w'` or `'1 week'`. This may be specified as an object containing
             * two properties: `magnitude`, a Number, and `unit`, a String
             * @config {String|Number|Object}
             */
            step : null,

            /**
             * A config object used to configure the {@link Core.widget.DatePicker datePicker}.
             * ```javascript
             * dateField = new DateField({
             *      picker    : {
             *          multiSelect : true
             *      }
             *  });
             * ```
             * @config {Object}
             */
            picker : null
        };
    }

    //endregion

    //region Init & destroy

    inputTemplate() {
        const me = this;
        return TemplateHelper.tpl`<input type="${me.inputType || 'text'}"
            reference="input"
            class="${me.inputCls || ''}"
            min="${me.min}"
            max="${me.max}"
            placeholder="${me.placeholder}"
            name="${me.name || me.id}"
            id="${me.id + '_input'}"/>`;
    }

    /**
     * Creates default picker widget
     *
     * @internal
     */
    createPicker(picker) {
        const me = this;

        picker = new DatePicker(Object.assign({
            owner        : me,
            forElement   : me[me.pickerAlignElement],
            floating     : true,
            scrollAction : 'realign',
            align        : {
                align    : 't0-b0',
                axisLock : true,
                anchor   : me.overlayAnchor,
                target   : me[me.pickerAlignElement]
            },
            value   : me.value,
            minDate : me.min,
            maxDate : me.max,

            onSelectionChange : ({ selection, source : picker }) => {
                // We only care about what DatePicker does if it has been opened
                if (picker.isVisible) {
                    me._isUserAction = true;
                    me.value = selection[0];
                    me._isUserAction = false;
                    picker.hide();
                }
            }
        }, picker));

        if (me.calendarContainerCls) {
            picker.element.classList.add(me.calendarContainerCls);
        }

        return picker;
    }

    //endregion

    set triggers(triggers) {
        super.triggers = ObjectHelper.assign(triggers, this.stepTriggers);
    }

    get triggers() {
        return super.triggers;
    }

    //region Click listeners

    onBackClick() {
        const
            me      = this,
            { min } = me;

        if (!me.readOnly && me.value) {
            const newValue = DateHelper.add(me.value, -1 * me._step.magnitude, me._step.unit);
            if (!min || min.getTime() <= newValue) {
                me.value = newValue;
            }
        }
    }

    onForwardClick() {
        const
            me      = this,
            { max } = me;

        if (!me.readOnly && me.value) {
            const newValue = DateHelper.add(me.value, me._step.magnitude, me._step.unit);
            if (!max || max.getTime() >= newValue) {
                me.value = newValue;
            }
        }
    }

    //endregion

    //region Toggle picker

    showPicker(focusPicker) {
        this.picker.value = this.picker.activeDate = this.value;
        super.showPicker(focusPicker);
    }

    focusPicker() {
        this.picker.focus();
    }

    //endregion

    // region Validation

    get isValid() {
        const me  = this,
            min = me.min,
            max = me.max;

        me.clearError('minimumValueViolation', true);
        me.clearError('maximumValueViolation', true);

        let value = me.value;

        if (value) {
            value = value.getTime();
            if (min && min.getTime() > value) {
                me.setError('minimumValueViolation', true);
                return false;
            }

            if (max && max.getTime() < value) {
                me.setError('maximumValueViolation', true);
                return false;
            }
        }

        return super.isValid;
    }

    //endregion

    //region Getters/setters
    transformDateValue(value) {
        if (value != null) {
            if (!(value.constructor.name === 'Date')) {
                if (typeof value === 'string') {
                    // If date cannot be parsed with set format, try fallback - the more general one
                    value = DateHelper.parse(value, this.format) || DateHelper.parse(value, this.fallbackFormat);
                }
                else {
                    value = new Date(value);
                }
            }

            // We insist on a *valid* Date as the value.
            // An invalid Date object returns NaN as its valueof().
            if (value && value.constructor.name === 'Date' && !isNaN(value.valueOf())) {
                return DateHelper.clearTime(value);
            }
        }
        return null;
    }

    /**
     * Get/set min value, which can be a Date or a string. If a string is specified, it will be converted using the
     * specified {@link #config-format}
     * @property {Date|String}
     */
    set min(value) {
        const me = this;
        me._min = me.transformDateValue(value);
        me.input && (me.input.min = me._min);

        if (me._picker) {
            me._picker.minDate = me._min;
        }
        me.updateInvalid();
    }

    get min() {
        return this._min;
    }

    /**
     * Get/set max value, which can be a Date or a string. If a string is specified, it will be converted using the
     * specified {@link #config-format}
     * @property {Date|String}
     */
    set max(value) {
        const me = this;
        me._max = me.transformDateValue(value);
        me.input && (me.input.max = me._max);

        if (me._picker) {
            me._picker.maxDate = me._max;
        }
        me.updateInvalid();
    }
    get max() {
        return this._max;
    }

    set value(value) {
        const me = this,
            oldValue = me.value,
            picker   = me._picker,
            newValue = me.transformDateValue(value);

        // A value we could not parse
        if (value && !newValue) {
            // setError uses localization
            me.setError('invalidDate');
            return;
        }
        me.clearError('invalidDate');

        // Reject non-change
        if (!me.hasChanged(oldValue, newValue)) {
            // But we must fix up the display in case it was an unparseable string
            // and the value therefore did not change.
            if (!me.inputting) {
                me.syncInputFieldValue();
            }
            return;
        }

        if (picker && !me.inputting) {
            picker.value = newValue;
        }
        super.value = newValue;
    }

    get value() {
        return super.value;
    }

    /**
     *  The `step` property may be set in Object form specifying two properties,
     * `magnitude`, a Number, and `unit`, a String.
     *
     * If a Number is passed, the steps's current unit is used (or `day` if no current step set)
     * and just the magnitude is changed.
     *
     * If a String is passed, it is parsed in accordance with (see {@link Core.helper.DateHelper#function-format-static}.
     * The string is taken to be the numeric magnitude then an abbreviation, or name of the unit.
     *
     * Upon read, the value is always returned in object form containing `magnitude` and `unit`.
     * @property {String|Number|Object}
     * */
    set step(value) {
        const me = this;

        // If a step is configured, show the steppers
        me.element.classList[value ? 'add' : 'remove']('b-show-steppers');

        if (typeof value === 'number') {
            value = {
                magnitude : value,
                unit      : me._step ? me._step.unit : 'day'
            };
        }
        else if (typeof value !== 'object') {
            value = DateHelper.parseDuration(value);
        }

        if (value && value.magnitude && value.unit) {
            value.magnitude = Math.abs(value.magnitude);
            me._step = value;
            me.updateInvalid();
        }
    }

    get step() {
        return this._step;
    }

    hasChanged(oldValue, newValue) {
        if (oldValue && oldValue.getTime && newValue && newValue.getTime) {
            return oldValue.getTime() !== newValue.getTime();
        }

        return super.hasChanged(oldValue, newValue);
    }

    get inputValue() {
        // Do not use the _value property. If called during configuration, this
        // will import the configured value from the config object.
        let date = this.value;

        return date ? DateHelper.format(date, this.format) : '';
    }

    /**
     * Get/Set format for date displayed in field (see {@link Core.helper.DateHelper#function-format-static} for formatting options)
     * @property {String}
     */
    set format(value) {
        const me = this;
        me._format = value;
        me.syncInputFieldValue(true);
    }

    get format() {
        return this._format;
    }

    //endregion

    //region Localization

    updateLocalization() {
        super.updateLocalization();
        this.syncInputFieldValue(true);
    }

    //endregion

    //region Other

    internalOnKeyPress(event) {
        super.internalOnKeyPress(event);

        if (event.key === 'Enter' && this.isValid) {
            this.picker.hide();
        }
    }

    //endregion
}

BryntumWidgetAdapterRegister.register('datefield', DateField);
BryntumWidgetAdapterRegister.register('date', DateField);
