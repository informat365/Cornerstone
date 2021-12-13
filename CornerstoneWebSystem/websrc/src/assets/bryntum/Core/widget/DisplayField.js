import TextField from './TextField.js';
import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';

/**
 * @module Core/widget/DisplayField
 */

/**
 * DisplayField widget used to show a read only value
 *
 * @extends Core/widget/Field
 *
 * @example
 * let displayField = new DisplayField({
 *   label: 'name',
 *   value : 'John Doe'
 * });
 *
 * @classType DisplayField
 * @externalexample widget/DisplayField.js
 */
export default class DisplayField extends TextField {
    static get $name() {
        return 'DisplayField';
    }

    static get defaultConfig() {
        return {
            readOnly : true,
            editable : false,
            cls      : 'b-display-field'
        };
    }

    get focusElement() {
        // we're not focusable.
    }

    set readOnly(value) {
        // empty, to not allow changing readOnly status
    }

    get readOnly() {
        return true;
    }
}

BryntumWidgetAdapterRegister.register('display', DisplayField);
BryntumWidgetAdapterRegister.register('displayfield', DisplayField);
