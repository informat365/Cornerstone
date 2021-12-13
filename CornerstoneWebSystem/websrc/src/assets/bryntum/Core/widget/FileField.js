import Field from '../../../lib/Core/widget/Field.js';
import TemplateHelper from '../../../lib/Core/helper/TemplateHelper.js';
import BryntumWidgetAdapterRegister from '../../../lib/Core/adapter/widget/util/BryntumWidgetAdapterRegister.js';
import EventHelper from '../helper/EventHelper.js';

/**
 * @module Core/widget/FileField
 */

/**
 * Filefield widget. Wraps native &lt;input type="file"&gt;.
 *
 * There is a nicer styled wrapper for this field, see {@link Core/widget/FilePicker}
 *
 * @extends Core/widget/Field
 * @example
 *
 * let fileField = new FileField({
 *   multiple : true,
 *   accept   : "image/*"
 * });
 *
 * @classType filefield
 * @externalexample widget/FileField.js
 */
export default class FileField extends Field {
    static get $name() {
        return 'FileField';
    }

    static get defaultConfig() {
        return {
            /**
             * Set to true to allow picking multiple files
             * @config {Boolean}
             * @default
             */
            multiple : false,

            /**
             * Comma-separated list of file extensions or MIME type to to accept. E.g.
             * ".jpg,.png,.doc" or "image/*". Null by default, allowing all files.
             * @config {String}
             */
            accept : null
        };
    }

    inputTemplate() {
        const me = this;

        // Not using reference="input" here intentionally.
        // In IE11/Edge when you pick file first time, field.value reports empty string while field.files.length is
        // non zero. Trying to fix this and embed file field to common field behavior is very tricky because cannot
        // be covered with siesta tests (it looks like).
        // Also we don't need much from this field - only `change` event.
        return TemplateHelper.tpl`
            <input
             type="file"
             reference="input"
             id="${me.id}_input"
             class="${me.inputCls || ''}"
             ${me.multiple ? 'multiple' : ''}
             accept="${me.accept || ''}"
            />
        `;
    }

    construct(config) {
        const me = this;

        super.construct(config);

        me.input = me.element.querySelector('input');

        EventHelper.on({
            element : me.input,
            change  : me.onFileInputChange,
            thisObj : me
        });
    }

    get oldValue() {
        return this._oldValue;
    }

    set oldValue(value) {
        this._oldValue = value;
    }

    /**
     * Returns list of selected files
     * @returns {FileList}
     * @readonly
     */
    get files() {
        return this.input.files;
    }

    /**
     * Opens browser file picker
     * @internal
     */
    pickFile() {
        this.input.click();
    }

    /**
     * Clears field value
     */
    clear() {
        this.input.value = null;
    }

    onFileInputChange() {
        const
            me       = this,
            value    = me.input.value,
            oldValue = me.oldValue;

        this.trigger('change', { value, oldValue, userAction : true, valid : true });

        me.oldValue = value;
    }
}

BryntumWidgetAdapterRegister.register('filefield', FileField);
