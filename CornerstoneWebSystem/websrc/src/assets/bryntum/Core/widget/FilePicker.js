import BryntumWidgetAdapterRegister from '../../../lib/Core/adapter/widget/util/BryntumWidgetAdapterRegister.js';
import Container from '../../../lib/Core/widget/Container.js';
import Tooltip from '../../../lib/Core/widget/Tooltip.js';
import '../../../lib/Core/widget/Button.js';
import './FileField.js';

/**
 * @module Core/widget/FilePicker
 */

/**
 * File input field wrapped into {@link Core/widget/Button button}. Clicking button opens browser file picker window.
 * When files are chosen, badge appears showing amount of files. Hovering the button shows tip with file names.
 *
 * By default only single file allowed.
 *
 * @extends Core/widget/Container
 * @example
 *
 * let fileField = new FilePicker({
 *   fileFieldConfig : {
 *      multiple : true,
 *      accept   : "image/*"
 *   },
 *   buttonConfig : {
 *       text : 'Pick file...'
 *   }
 * });
 *
 * @classType filepicker
 * @externalexample widget/FilePicker.js
 */
export default class FilePicker extends Container {
    static get $name() {
        return 'FilePicker';
    }

    static get defaultConfig() {
        return {

            /**
             * The name of the property to set when a single value is to be applied to this FilePicker. Such as when used
             * in a grid WidgetColumn, this is the property to which the column's `field` is applied.
             * @config {String}
             * @default
             * @category Misc
             */
            defaultBindProperty : 'value',

            /**
             * Fires after user closes file picker dialog.
             * @event change
             * @param {FileList} files List of picked files
             */

            /**
             * Fires when field is cleared with {@link #function-clear} method
             * @event clear
             */

            /**
             * Wrapper button config object. See {@link Core/widget/Button} for list of available configs.
             * @config {Object}
             */
            buttonConfig : null,

            /**
             * Underlying field config object. See {@link Core/widget/FileField} for list of available configs.
             * @config {Object}
             */
            fileFieldConfig : null
        };
    }

    construct(config = {}) {
        const me = this;

        config.items = [
            Object.assign({
                type : 'button',
                ref  : 'fileButton',
                text : 'File'
            }, config.buttonConfig),
            Object.assign({
                type  : 'filefield',
                ref   : 'fileField',
                style : 'display: none'
            }, config.fileFieldConfig)
        ].concat(config.items || []);

        super.construct(config);

        me.button.on({
            click   : me.onButtonClick,
            thisObj : me
        });

        me.fileField.on({
            change  : me.onFileFieldChange,
            thisObj : me
        });

        me._thisIsAUsedExpression(me.fileTip);
    }

    get button() {
        return this.widgetMap.fileButton;
    }

    get fileField() {
        return this.widgetMap.fileField;
    }

    /**
     * List of selected files
     * @returns {FileList}
     * @readonly
     */
    get files() {
        return this.fileField.files;
    }

    get fileTip() {
        const me = this;

        return me._fileTip || (me._fileTip = new Tooltip({
            cls          : 'b-file-tip',
            forElement   : me.button.element,
            showOnHover  : true,
            align        : 'b-t',
            scrollAction : 'realign',
            listeners    : {
                beforeshow() {
                    const
                        tip   = this,
                        files = me.files;

                    if (files && files.length) {
                        tip.html = `${Array.from(files).map(file => file.name).join('<br>')}`;
                        return true;
                    }

                    // Veto show
                    return false;
                }
            }
        }));
    }

    /**
     * Clears field
     */
    clear() {
        const me = this;

        me.fileField.clear();
        me.button.badge = '';

        me.trigger('clear');
    }

    onButtonClick({ event }) {
        const me = this;

        // forward click to the file input to open browser file picker
        // me.fileField.input.click();
        me.fileField.pickFile();

        event.preventDefault();
    }

    onFileFieldChange() {
        const me = this;

        me.button.badge = me.files.length || '';

        me.trigger('change', { files : me.files });
    }
}

BryntumWidgetAdapterRegister.register('filepicker', FilePicker);
