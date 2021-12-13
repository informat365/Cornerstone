import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';
import Popup from './Popup.js';
import DateHelper from '../helper/DateHelper.js';
import EventHelper from '../helper/EventHelper.js';

/**
 * @module Core/widget/TimePicker
 */

/**
 * A Popup which displays hour and minute number fields and AM/PM switcher buttons for 12 hour time format.
 * Fires timeChange event upon user changes time.
 *
 * This class is not intended for use in applications. It is used internally by the
 * {@link Core.widget.TimeField} class.
 *
 * @classType timepicker
 * @private
 */
export default class TimePicker extends Popup {

    //region Config
    static get $name() {
        return 'TimePicker';
    }

    static get defaultConfig() {
        return {
            items : [
                {
                    type                    : 'number',
                    ref                     : 'fieldHours',
                    min                     : 0,
                    max                     : 23,
                    highlightExternalChange : false
                },
                { html : '<label>:</label>' },
                {
                    type                    : 'number',
                    ref                     : 'fieldMinutes',
                    min                     : 0,
                    max                     : 59,
                    highlightExternalChange : false
                },
                {
                    type        : 'button',
                    text        : 'AM',
                    toggleGroup : 'am-pm',
                    ref         : 'buttonAM',
                    cls         : 'b-blue'
                },
                {
                    type        : 'button',
                    text        : 'PM',
                    toggleGroup : 'am-pm',
                    ref         : 'buttonPM',
                    cls         : 'b-blue'
                }
            ],

            float : '1',

            autoShow : false,

            trapFocus : true,

            /**
             * Default time value
             * @config {Date}
             */
            value : DateHelper.getTime(0),

            /**
             * Time format. Used to set appropriate 12/24 hour format to display.
             * See Core.helper.DateHelper#format for formatting options.
             * @config {String}
             */
            format : null
        };
    }

    //endregion

    //region Init

    /**
     * Fires when a time is changed.
     * @event timeChange
     * @param {Date} time The selected time.
     */
    construct(config) {
        const me = this;
        super.construct(config);

        me._pm = false;
        me.fieldHours.on('change', me.onFieldChange, me);
        me.fieldMinutes.on('change', me.onFieldChange, me);
        me.buttonAM.on('click', me.onButtonAMClick, me);
        me.buttonPM.on('click', me.onButtonPMClick, me);

        EventHelper.on({
            element : me.element,
            keydown : 'onPickerKeyDown',
            thisObj : me
        });

        EventHelper.on({
            element : me.fieldHours.element,
            keydown : 'onPickerKeyDown',
            thisObj : me
        });

        EventHelper.on({
            element : me.fieldMinutes.element,
            keydown : 'onPickerKeyDown',
            thisObj : me
        });

        me.refresh();
    }

    //endregion

    //region Event listeners

    onFieldChange() {
        const me = this;
        if (me._time) {
            me.value = me.pickerToTime();
        }
    }

    onButtonAMClick() {
        const me = this;
        me._pm = false;
        if (me._time) {
            me.value = me.pickerToTime();
        }
    }

    onButtonPMClick() {
        const me = this;
        me._pm = true;
        if (me._time) {
            me.value = me.pickerToTime();
        }
    }

    onPickerKeyDown(keyEvent) {
        const me = this,
            keyName = (keyEvent.key && keyEvent.key.trim()) || keyEvent.code;

        switch (keyName) {
            case 'Escape':
                // Support for undefined initial time
                me.triggerTimeChange(me._initialValue);
                me.hide();
                keyEvent.preventDefault();
                break;
            case 'Enter':
                me.value = me.pickerToTime();
                me.hide();
                keyEvent.preventDefault();
                break;
        }
    }

    //endregion

    //region Internal functions

    pickerToTime() {
        const me = this,
            pm = me._pm;
        let hours = me.fieldHours.value,
            newValue = new Date(me._time);

        if (!me._is24Hour) {
            if (pm && hours < 12) hours = hours + 12;
            if (!pm && hours === 12) hours = 0;
        }

        newValue.setHours(hours);
        newValue.setMinutes(me.fieldMinutes.value);

        if (me._min) {
            newValue = DateHelper.max(me._min, newValue);
        }
        if (me._max) {
            newValue = DateHelper.min(me._max, newValue);
        }

        return newValue;
    }

    triggerTimeChange(time) {
        this.trigger('timeChange', { time });
    }

    //endregion

    //region Getters / Setters

    /**
     * Get/set value, which can be a Date or a string. If a string is specified, it will be converted using the
     * specified {@link #config-format}
     * @property {Date|String}
     */
    set value(newTime) {
        const me = this;
        let changed = false;

        if (!newTime || !me._time) {
            me._time = TimePicker.defaultConfig.value;
            changed  = true;
        }
        else if (newTime.getTime() !== me._time.getTime()) {
            me._time = newTime;
            changed  = true;
        }

        if (changed) {
            if (me.isVisible) {
                me.triggerTimeChange(me.value);
            }
            me.refresh();
        }
    }

    get value() {
        return this._time;
    }

    /**
     * Get/Set format for time displayed in field (see Core.helper.DateHelper#format for formatting options)
     * @property {String}
     */
    set format(value) {
        const me = this;
        me._format = value;
        me._is24Hour = DateHelper.is24HourFormat(me._format);
        me.refresh();
    }

    get format() {
        return this._format;
    }

    /**
     * Get/set max value, which can be a Date or a string. If a string is specified, it will be converted using the
     * specified {@link #config-format}
     * @property {Date|String}
     */
    set min(value) {
        this._min = value;
    }

    get min() {
        return this._min;
    }

    /**
     * Get/set min value, which can be a Date or a string. If a string is specified, it will be converted using the
     * specified {@link #config-format}
     * @property {Date|String}
     */
    set max(value) {
        this._max = value;
    }

    get max() {
        return this._max;
    }

    /**
     * Get/set initial value and value, which can be a Date or a string. If a string is specified,
     * it will be converted using the specified {@link #config-format}. Initial value is restored on Escape click
     * @property {Date|String}
     */
    set initialValue(value) {
        this.value = value;
        this._initialValue = value;
    }

    get initialValue() {
        return this._initialValue;
    }

    //endregion

    //region Internal widgets getters

    get buttonAM() {
        return this.widgetMap.buttonAM;
    }

    get buttonPM() {
        return this.widgetMap.buttonPM;
    }

    get fieldHours() {
        return this.widgetMap.fieldHours;
    }

    get fieldMinutes() {
        return this.widgetMap.fieldMinutes;
    }

    //endregion

    //region Display

    refresh() {
        const me = this;
        if (!me.isConfiguring) {
            const time       = me._time,
                is24       = me._is24Hour,
                hours      = time.getHours(),
                pm         = me._pm = hours >= 12,
                fieldHours = me.fieldHours;

            me.element.classList[is24 ? 'add' : 'remove']('b-24h');

            fieldHours.min = is24 ? 0 : 1;
            fieldHours.max = is24 ? 23 : 12;
            fieldHours.value = is24 ? hours : (hours % 12) || 12;
            me.fieldMinutes.value = time.getMinutes();
            me.buttonAM.pressed = !pm;
            me.buttonPM.pressed = pm;
            me.buttonAM.hidden = me.buttonPM.hidden = is24;
        }
    }

    //endregion

}

BryntumWidgetAdapterRegister.register('timepicker', TimePicker);
