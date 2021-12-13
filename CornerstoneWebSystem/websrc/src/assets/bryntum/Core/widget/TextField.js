import Field from './Field.js';
import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';
import TemplateHelper from '../helper/TemplateHelper.js';
import DomHelper from '../helper/DomHelper.js';

//TODO: label should be own element

/**
 * @module Core/widget/TextField
 */

/**
 * Textfield widget. Wraps native &lt;input type="text"&gt;
 *
 * @extends Core/widget/Field
 *
 * @example
 * let textField = new TextField({
 *   placeholder: 'Enter some text'
 * });
 *
 * @classType textfield
 * @externalexample widget/TextField.js
 */
export default class TextField extends Field {

    static get defaultConfig() {
        return {
            /**
             * The tab index of the input field
             * @config {Number} tabIndex
             */

            /**
             * The min number of characters for the input field
             * @config {Number} minLength
             */

            /**
             * The max number of characters for the input field
             * @config {Number} maxLength
             */

            attributes : [
                'placeholder',
                'autoComplete',
                'minLength',
                'maxLength',
                'tabIndex'
            ],

            localizableProperties : ['label', 'title', 'placeholder']
        };
    }

    static get $name() {
        return 'TextField';
    }

    inputTemplate() {
        const
            me = this,
            style = 'inputWidth' in me ? `style="width:${DomHelper.setLength(me.inputWidth)}"` : '';

        return TemplateHelper.tpl`<input type="${me.inputType || 'text'}"
            reference="input"
            class="${me.inputCls || ''}"
            name="${me.name || me.id}"
            id="${me.id + '_input'}"
            ${style}
            ${me.attributeString}/>`;
    }

    set value(value) {
        super.value = (this.$name === 'TextField' && value == null) ? '' : value;
    }

    get value() {
        return super.value;
    }

    set placeholder(value) {
        this._placeholder = value;
        if (this.input) {
            this.input.placeholder = value;
        }
    }

    get placeholder() {
        return this._placeholder;
    }
}

BryntumWidgetAdapterRegister.register('textfield', TextField);
BryntumWidgetAdapterRegister.register('text', TextField);
