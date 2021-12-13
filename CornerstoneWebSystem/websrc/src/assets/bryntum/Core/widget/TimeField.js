//TODO: picker icon (clock) should be clock that shows actual time
import PickerField from './PickerField.js';
import TimePicker from './TimePicker.js';
import DateHelper from '../helper/DateHelper.js';
import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';

/**
 * @module Core/widget/TimeField
 */

/**
 * Time field widget (text field + time picker).
 *
 * @extends Core/widget/PickerField
 *
 * @example
 * let field = new TimeField({
 *   format: 'HH'
 * });
 *
 * @classType timefield
 * @externalexample widget/TimeField.js
 */
export default class TimeField extends PickerField {
    //region Config
    static get $name() {
        return 'TimeField';
    }

    static get defaultConfig() {
        return {

            /**
             * Format for date displayed in field (see Core.helper.DateHelper#function-format-static for formatting options)
             * @config {String}
             * @default
             */
            format : 'LT',

            triggers : {
                back : {
                    cls     : 'b-icon b-icon-angle-left',
                    handler : 'onBackClick',
                    align   : 'start'
                },
                expand : {
                    template : () => `<div class="b-align-${this.align || 'end'}"><div class="b-icon-clock-live"></div></div>`,
                    handler  : 'onTriggerClick',
                    align    : 'end'
                },
                forward : {
                    cls     : 'b-icon b-icon-angle-right',
                    handler : 'onForwardClick',
                    align   : 'end'
                }
            },

            /**
             * Min time value
             * @config {String|Date}
             */
            min : null,

            /**
             * Max time value
             * @config {String|Date}
             */
            max : null,

            /**
             * Time increment duration value. Defaults to 5 minutes.
             * The value is taken to be a string consisting of the numeric magnitude and the units.
             * The units may be a recognised unit abbreviation of this locale or the full local unit name.
             * For example `"10m"` or `"5min"` or `"2 hours"`

             * @config {String}
             */
            step : '5m'
        };
    }

    //endregion

    //region Init & destroy

    createPicker(picker) {
        const me = this;

        return new TimePicker(Object.assign({
            owner      : me,
            floating   : true,
            forElement : me[me.pickerAlignElement],
            align      : {
                align    : 't0-b0',
                axisLock : true,
                anchor   : me.overlayAnchor,
                target   : me[me.pickerAlignElement]
            },
            value  : me.value,
            format : me.format,
            onTimeChange({ time }) {
                me._isUserAction = true;
                me.value = time;
                me._isUserAction = false;
            }
        }, picker));
    }

    //endregion

    //region Click listeners

    onBackClick() {
        const
            me      = this,
            { min } = me;

        if (!me.readOnly && me.value) {
            const newValue = DateHelper.add(me.value, -1 * me._step.magnitude, me._step.unit);
            if (!min || min.getTime() <= newValue) {
                me._isUserAction = true;
                me.value = newValue;
                me._isUserAction = false;
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
                me._isUserAction = true;
                me.value = newValue;
                me._isUserAction = false;
            }
        }
    }

    //endregion

    // region Validation

    get isValid() {
        const me  = this;

        me.clearError('minimumValueViolation', true);
        me.clearError('maximumValueViolation', true);

        let value = me.value;
        if (value) {
            value = value.getTime();
            if (me._min && me._min.getTime() > value) {
                me.setError('minimumValueViolation', true);
                return false;
            }

            if (me._max && me._max.getTime() < value) {
                me.setError('maximumValueViolation', true);
                return false;
            }
        }

        return super.isValid;
    }

    hasChanged(oldValue, newValue) {
        if (oldValue && oldValue.getTime && newValue && newValue.getTime) {
            return oldValue.getTime() !== newValue.getTime();
        }

        return super.hasChanged(oldValue, newValue);
    }

    //endregion

    //region Toggle picker

    /**
     * Show picker
     */
    showPicker(focusPicker) {
        const me = this,
            picker = me.picker;

        picker.initialValue = me.value;
        picker.format = me.format;
        picker.maxTime = me.max;
        picker.minTime = me.min;

        // Show valid time from picker while editor has undefined value
        me.value = picker.value;

        super.showPicker(focusPicker);
    }

    onPickerShow() {
        const me = this;
        super.onPickerShow();
        // Remove PickerField key listener
        me.pickerKeyDownRemover && me.pickerKeyDownRemover();
    }

    /**
     * Focus time picker
     */
    focusPicker() {
        this.picker.focus();
    }

    //endregion

    //region Getters/setters

    transformTimeValue(value) {
        if (value != null) {
            if (!(value.constructor.name === 'Date')) {
                if (typeof value === 'string') {
                    value = DateHelper.parse(value, this.format);
                }
                else {
                    value = new Date(value);
                }
            }

            // We insist on a *valid* Time as the value.
            // An invalid Date object returns NaN as its valueof().
            if (value && value.constructor.name === 'Date' && !isNaN(value.valueOf())) {
                // Clear date part back to zero so that all we have is the time part of the epoch.
                value = DateHelper.clone(value);
                value.setFullYear(1970, 0, 1);
                return value;
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
        me._min = me.transformTimeValue(value);
        me.input && (me.input.min = me._min);
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
        me._max = me.transformTimeValue(value);
        me.input && (me.input.max = me._max);
        me.updateInvalid();
    }

    get max() {
        return this._max;
    }

    /**
     * Get/set value, which can be a Date or a string. If a string is specified, it will be converted using the
     * specified {@link #config-format}
     * @property {Date|String}
     */
    set value(value) {
        const me = this,
            oldValue = me.value,
            newValue = me.transformTimeValue(value);

        // A value we could not parse
        if (value && !newValue) {
            // setError uses localization
            me.setError('invalidTime');
            return;
        }
        me.clearError('invalidTime');

        // Reject non-change
        if (!me.hasChanged(oldValue, newValue)) {
            // But we must fix up the display in case it was an unparseable string
            // and the value therefore did not change.
            if (!me.inputting) {
                me.syncInputFieldValue();
            }
            return;
        }

        // This makes to clock icon show correct time
        if (me.triggers.expand && newValue) {
            me.triggers.expand.element.firstElementChild.style.animationDelay = -((newValue.getHours() * 60 + newValue.getMinutes()) / 10) + 's';
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
     * If a Number is passed, the steps's current unit is used and just the magnitude is changed.
     *
     * If a String is passed, it is parsed in accordance with (see {@link Core.helper.DateHelper#function-format-static}.
     * The string is taken to be the numeric magnitude then an abbreviation, or name of the unit.
     *
     * Upon read, the value is always returned in object form containing `magnitude` and `unit`.
     * @property {String|Number|Object}
     * */
    set step(value) {
        const me = this;

        if (typeof value === 'number') {
            value = {
                magnitude : value,
                unit      : me._step.unit
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

    /**
     * Get/Set format for time displayed in field (see {@link Core.helper.DateHelper#function-format-static} for formatting options)
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

    get inputValue() {
        return DateHelper.format(this.value, this.format);
    }

    //endregion

    //region Localization

    updateLocalization() {
        super.updateLocalization();
        this.syncInputFieldValue(true);
    }

    //endregion
}

BryntumWidgetAdapterRegister.register('timefield', TimeField);
BryntumWidgetAdapterRegister.register('time', TimeField);
