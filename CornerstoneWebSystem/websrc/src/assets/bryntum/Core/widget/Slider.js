import Widget from './Widget.js';
import TemplateHelper from '../helper/TemplateHelper.js';
import Tooltip from './Tooltip.js';
import Rectangle from '../helper/util/Rectangle.js';
import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';

/**
 * @module Core/widget/Slider
 */

/**
 * Wraps native &lt;input type="range"&gt;
 *
 * @extends Core/widget/Widget
 *
 * @example
 * let slider = new Slider({
 *   text: 'Choose value'
 * });
 *
 * @classType slider
 * @externalexample widget/Slider.js
 */
export default class Slider extends Widget {
    //region Config
    static get $name() {
        return 'Slider';
    }

    static get defaultConfig() {
        return {
            /**
             * Slider label text
             * @config {String}
             */
            text : '',

            /**
             * Show value in label (appends in () if text is set)
             * @config {Boolean}
             * @default
             */
            showValue : true,

            /**
             * Show value in tooltip
             * @config {Boolean}
             * @default
             */
            showTooltip : false,

            /**
             * Minimum value
             * @config {Number}
             * @default
             */
            min : 0,

            /**
             * Maximum value
             * @config {Number}
             * @default
             */
            max : 100,

            /**
             * Step size
             * @config {Number}
             * @default
             */
            step : 1,

            /**
             * Initial value
             * @config {Number}
             */
            value : null,

            // The value is set in the Light theme. The Material theme will have different value.
            thumbSize : 20,

            localizableProperties : ['text']
        };
    }

    //endregion

    //region Init

    construct(config) {
        const me = this;

        super.construct(config);

        me.updateLabel();

        if (me.showTooltip) {
            me.tip = new Tooltip({
                forElement : me.input,
                anchor     : false, // No anchor displayed since thumbSize is different for different themes
                align      : 'b-t',
                axisLock   : true
            });
        }

        me.input.addEventListener('input', me.onInternalInput.bind(me));
        me.input.addEventListener('change', me.onInternalChange.bind(me));
        me.input.addEventListener('mouseover', me.onInternalMouseOver.bind(me));
        me.input.addEventListener('mouseout', me.onInternalMouseOut.bind(me));
    }

    template(widget) {
        return TemplateHelper.tpl`
            <div class="${widget.text ? 'b-text b-has-label' : ''}">
                <input type="range"
                   reference="input"
                   id="${widget.id}_input"
                   min="${widget.min}"
                   max="${widget.max}"
                   step="${widget.step}"
                   value="${widget.value}">
                <label reference="label" for="${widget.id}_input">${widget.text}</label>
            </div>
        `;
    }

    get focusElement() {
        return this.input;
    }

    //endregion

    //region Events

    onInternalInput(event) {
        const me = this;

        me._value = parseInt(me.input.value);

        me.updateUI();
        me.trigger('input', { value : me.value });
    }

    onInternalChange(event) {
        const me = this;
        me.updateUI();
        me.triggerChange(true);
        me.trigger('action', { value : me.value });
    }

    onInternalMouseOver(event) {
        const me = this;

        me.updateLabel();

        if (me.tip) {
            me.tip.showBy({
                target : me.calcThumbPosition(),
                offset : 5
            });
        }
    }

    onInternalMouseOut(event) {
        if (this.tip) {
            this.tip.hide();
        }
    }

    triggerChange(userAction) {
        this.trigger('change', {
            value : this.value,
            userAction
        });
    }

    //endregion

    //region Getters/setters

    /**
     * Get/Set text. Appends value if Slider.showValue is true
     * @property {String}
     */
    get text() {
        return this._text;
    }

    set text(text) {
        const me = this;

        me._text = text;

        if (me.label) {
            if (me.showValue) {
                text = text ? `${text} (${me.value})` : me.value;
            }

            me.label.innerHTML = text;
        }

        if (me.tip) {
            me.tip.html = me.value;
        }
    }

    /**
     * Get/set value
     * @property {Number}
     */
    get value() {
        return this.input ? parseInt(this.input.value) : this._value;
    }

    set value(value) {
        const me = this;

        if (me._value !== value) {
            if (me.input) {
                me.input.value = value;
                me.updateUI();
                me.triggerChange(false);
            }
            me._value = value;
        }
    }

    /**
     * Get/set min value
     * @property {Number}
     */
    get min() {
        return this.input ? this.input.min : this._min;
    }

    set min(min) {
        const me = this;

        if (me.input) {
            me.input.min = min;
            if (me._value < min) {
                me.value = min;
                me.trigger('input', { value : me.value });
            }
        }

        me._min = min;
    }

    /**
     * Get/set max value
     * @property {Number}
     */
    get max() {
        return this.input ? this.input.max : this._max;
    }

    set max(max) {
        const me = this;

        if (me.input) {
            me.input.max = max;
            if (me._value > max) {
                me.value = max;
                me.trigger('input', { value : me.value });
            }
        }

        me._max = max;
    }

    /**
     * Get/set step size
     * @property {Number}
     */
    get step() {
        return this.input ? this.input.step : this._step;
    }

    set step(step) {
        if (this.input) this.input.step = step;
        this._step = step;
    }

    //endregion

    //region Util

    /**
     * Refresh label text
     * @private
     */
    updateLabel() {
        this.text = this._text;
    }

    /**
     * Refresh tooltip position
     * @private
     */
    updateTooltipPosition() {
        if (this.tip) {
            this.tip.alignTo({
                target : this.calcThumbPosition(),
                offset : 5
            });
        }
    }

    /**
     * Refresh slider UI
     * @private
     */
    updateUI() {
        this.updateLabel();
        this.updateTooltipPosition();
    }

    calcThumbPosition() {
        const me = this,
            inputRect = Rectangle.from(me.input),
            offset = (inputRect.width - me.thumbSize) * me.calcPercentProgress() / 100;

        return new Rectangle(
            inputRect.x + offset,
            inputRect.y + inputRect.height / 2 - me.thumbSize / 2,
            me.thumbSize,
            me.thumbSize
        );
    }

    calcPercentProgress() {
        return (this.value - this.min) / (this.max - this.min) * 100;
    }

    //endregion
}

BryntumWidgetAdapterRegister.register('slider', Slider);
