import Widget from './Widget.js';
import PickerField from './PickerField.js';
import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';
import TemplateHelper from '../helper/TemplateHelper.js';
import ObjectHelper from '../helper/ObjectHelper.js';
import EventHelper from '../helper/EventHelper.js';

/**
 * @module Core/widget/TextAreaField
 */

/**
 * TextAreaField widget. Wraps native &lt;input type="text"&gt;
 *
 * @extends Core/widget/Field
 *
 * @example
 * let textAreaField = new TextAreaField({
 *   placeholder: 'Enter some text'
 * });
 *
 * @classType textareafield
 */
export default class TextAreaField extends PickerField {
    static get $name() {
        return 'TextAreaField';
    }

    static get defaultConfig() {
        return {
            triggers : null, // Override PickerField. We don't have a trigger by default

            /**
             * Configure as `false` to have the field render as a non-editable picker field which
             * shows a `<textarea>` input when expanded.
             * @config {Boolean}
             * @default
             */
            inline : true,

            /**
             * The resize style to apply to the `<textarea>` element.
             * @config {String}
             * @default
             */
            resize : 'none'
        };
    }

    startConfigure(config) {
        // Read the inline config which will force evaluation of triggers.
        this._thisIsAUsedExpression(this.inline);
        super.startConfigure(config);
    }

    inputTemplate() {
        const me = this;

        if (me.inline) {
            return TemplateHelper.tpl`<textarea
                reference="input"
                class="${me.inputCls || ''}"
                placeholder="${me.placeholder}"
                name="${me.name || me.id}"
                style="resize:${me.resize}"
                id="${me.id + '_input'}"></textarea>`;
        }
        else {
            return TemplateHelper.tpl`<input type="text"
                readOnly="readonly"
                reference="displayElement"
                placeholder="${me.placeholder}"/>`;
        }
    }

    get focusElement() {
        return this.inline || this._picker && this._picker.isVisible ? this.input : this.displayElement;
    }

    showPicker() {
        const { picker } = this;

        picker.width = this.pickerWidth || this[this.pickerAlignElement].offsetWidth;

        // Always focus the picker.
        super.showPicker(true);
    }

    focusPicker() {
        this.input.focus();
    }

    onPickerKeyDown(keyEvent) {
        const
            me = this,
            realInput = me.input;

        switch (keyEvent.key.trim() || keyEvent.code) {
            case 'Escape':
                // TODO: revert value?
                me.picker.hide();
                return;
            case 'Enter':
                if (keyEvent.ctrlKey) {
                    me.syncInputFieldValue();
                    me.picker.hide();
                }
                break;
        }

        // Super's onPickerKeyDown fires through this.input, so avoid infinite recursion
        // by redirecting it through the displayElement.
        me.input = me.displayElement;
        const result = super.onPickerKeyDown(keyEvent);
        me.input = realInput;

        return result;
    }

    syncInputFieldValue(skipHighlight) {
        if (this.displayElement) {
            this.displayElement.value = this.inputValue;
        }
        super.syncInputFieldValue(skipHighlight);
    }

    set value(value) {
        super.value = value == null ? '' : value;
    }

    get value() {
        return super.value;
    }

    set inline(inline) {
        this._inline = inline;
        if (!inline && !this.triggers) {
            this.triggers = {};
        }
    }

    set triggers(triggers) {
        if (!this.inline) {
            (triggers || (triggers = {})).expand = {
                cls     : 'b-icon-picker',
                handler : 'onTriggerClick'
            };
        }
        super.triggers = triggers;
    }

    get triggers() {
        return super.triggers;
    }

    get inline() {
        return this._inline;
    }

    createPicker(picker) {
        const me = this;

        // Allow configuring pickerWidth in one go. Setting `picker = { width : 300 }` will otherwise be overridden by
        // pickerWidth or fields width.
        if (picker.width) {
            me.pickerWidth = picker.width;
        }

        picker = new Widget(ObjectHelper.merge({
            cls          : 'b-textareafield-picker',
            owner        : me,
            floating     : true,
            scrollAction : 'realign',
            forElement   : me[me.pickerAlignElement],
            align        : {
                align    : 't-b',
                axisLock : true,
                anchor   : me.overlayAnchor,
                target   : me[me.pickerAlignElement]
            },
            html     : `<textarea id="${me.id + '_input'}" style="resize:${me.resize}">${me.value}</textarea>`,
            autoShow : false
        }, picker));

        const input = me.input = picker.element.querySelector(`#${me.id}_input`);

        me.inputListenerRemover = EventHelper.on({
            element  : input,
            thisObj  : me,
            focus    : 'internalOnInputFocus',
            change   : 'internalOnChange',
            input    : 'internalOnInput',
            keydown  : 'internalOnKeyPress',
            keypress : 'internalOnKeyPress',
            keyup    : 'internalOnKeyPress'
        });

        return picker;
    }
}

BryntumWidgetAdapterRegister.register('textareafield', TextAreaField);
BryntumWidgetAdapterRegister.register('textarea', TextAreaField);
