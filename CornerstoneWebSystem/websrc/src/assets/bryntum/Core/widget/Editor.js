import Widget from './Widget.js';
import Container from './Container.js';
import IdHelper from '../helper/IdHelper.js';
import WidgetHelper from '../helper/WidgetHelper.js';
import ObjectHelper from '../helper/ObjectHelper.js';
import Rectangle from '../helper/util/Rectangle.js';
import EventHelper from '../helper/EventHelper.js';
import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';
import LocaleManager from '../../Core/localization/LocaleManager.js';
import './layout/Fit.js';
import StringHelper from '../helper/StringHelper.js';
import DomHelper from '../helper/DomHelper.js';

/**
 * @module Core/widget/Editor
 */

/**
 * Displays an input field, optionally editing a field of a record at a particular position.
 *
 * Offers events to signal edit completion upon `ENTER` or focus loss (if configured to do so),
 * or edit cancellation on `ESC`, or focus loss if configured that way.
 * @extends Core/widget/Container
 *
 * @classType Editor
 */
export default class Editor extends Container {
    //region Config
    static get $name() {
        return 'Editor';
    }

    static get defaultConfig() {
        return {
            positioned : true,

            hidden : true,

            layout : 'fit',

            /**
             * A config object, or the `type` string of the input field which this editor will encapsulate.
             * @config {Object|String}
             * @default
             */
            inputField : 'textfield',

            /**
             * What action should be taken when focus moves out of the editor, either by `TAB` or clicking outside.
             * May be `'complete'` or `'cancel`'. Any other value results in no action being taken upon focus leaving the editor
             * leaving the application to listen for the {@link Core.widget.Widget#event-focusout focusout} event.
             * @config {String}
             * @default
             */
            blurAction : 'complete',

            /**
             * The name of the `key` which completes the edit.
             *
             * See https://developer.mozilla.org/en-US/docs/Web/API/KeyboardEvent/key/Key_Values for key names.
             * @config {String}
             * @default
             */
            completeKey : 'Enter',

            /**
             * The name of the `key` which cancels the edit.
             *
             * See https://developer.mozilla.org/en-US/docs/Web/API/KeyboardEvent/key/Key_Values for key names.
             * @config {String}
             * @default
             */
            cancelKey : 'Escape',

            /**
             * Configure as `true` to allow editing to complete when the field is invalid. Editing may always be _canceled_.
             * This is deprecated and has been replaced by the more flexible {@link #config-invalidAction} option.
             * @config {Boolean}
             * @deprecated 2.4
             * @default false
             */
            allowInvalid : null,

            /**
             * How to handle a request to complete the edit if the field is invalid. There are three choices:
             *  - `block` The default. The edit is not exited, the field remains focused.
             *  - `allow` Allow the edit to be completed.
             *  - `revert` The field value is reverted and the edit is completed.
             * @config {String}
             * @default
             */
            invalidAction : 'block',

            /**
             * Configure as `true` to have editing complete as soon as the field fires its `change` event.
             * @config {Boolean}
             * @default false
             */
            completeOnChange : null
        };
    }

    //endregion

    //region Events

    /**
     * Fired before the editor is shown to start an edit operation. Returning `false` from a handler vetoes the edit operation.
     * @event beforestart
     * @param {Object} value - The value to be edited.
     * @preventable
     */
    /**
     * Fired when an edit operation has begun.
     * @event start
     * @param {Object} value - The starting value of the field.
     * @param {Core.widget.Editor} source - The Editor that triggered the event.
     */
    /**
     * Fired when an edit completion has been requested, either by `ENTER`, or focus loss (if configured to complete on blur).
     * The completion may be vetoed, in which case, focus is moved back into the editor.
     * @event beforeComplete
     * @param {Object} oldValue - The original value.
     * @param {Object} value - The new value.
     * @param {Core.widget.Editor} source - The Editor that triggered the event.
     * @param {Function} [finalize] An async function may be injected into this property
     * which performs asynchronous finalization tasks such as complex validation of confirmation. The
     * value `true` or `false` must be returned.
     * @param {Object} [finalize.context] An object describing the editing context upon requested completion of the edit.
     * @preventable
     */
    /**
     * Edit has been completed, and any associated record or element has been updated.
     * @event complete
     * @param {Object} oldValue - The original value.
     * @param {Object} value - The new value.
     * @param {Core.widget.Editor} source - The Editor that triggered the event.
     */
    /**
     * Fired when cancellation has been requested, either by `ESC`, or focus loss (if configured to cancel on blur).
     * The cancellation may be vetoed, in which case, focus is moved back into the editor.
     * @event beforeCancel
     * @param {Object} oldValue - The original value.
     * @param {Object} value - The new value.
     * @param {Core.widget.Editor} source - The Editor that triggered the event.
     * @preventable
     */
    /**
     * Edit has been canceled without updating the associated record or element.
     * @event cancel
     * @param {Object} oldValue - The original value.
     * @param {Object} value - The value of the field.
     * @param {Core.widget.Editor} source - The Editor that triggered the event.
     */
    /**
     * Fire to relay a `keypress` event from the field.
     * @event keypress
     * @param {Event} event - The key event.
     */

    //endregion

    afterConfigure() {
        const me = this;
        super.afterConfigure();

        if (me.completeKey || me.cancelKey) {
            EventHelper.on({
                element : me.element,
                keydown : 'onKeyDown',
                thisObj : me
            });
        }

        LocaleManager.on({
            locale  : 'onLocaleChange',
            thisObj : me
        });
    }

    onLocaleChange() {
        const { inputField } = this;
        if (inputField && !inputField.isDestroyed) {
            inputField.syncInputFieldValue(true);
        }
    }

    /**
     * Start editing
     * @param {Object} editObject An object containing details about what to edit.
     * @param {HTMLElement/Core.helper.util.Rectangle} editObject.target the element or Rectangle to align to.
     * @param {String} [editObject.align=t0-t0] How to align to the target.
     * @param {Boolean} [editObject.matchSize=true] Match editor size to target size.
     * @param {Boolean} [editObject.matchFont=true] Match editor's font-size size to target's font-size.
     * @param {Core.data.Model} [editObject.record] The record to edit.
     * @param {String} [editObject.field] The field name in the record to edit. This defaults to the `name` of the {@link #config-inputField}.
     * Also if record has method set + capitalized field, method will be called, e.g. if record has method named
     * `setFoobar` and this config is `foobar`, then instead of `record.foobar = value`, `record.setFoobar(value)` will be called.
     * @param {Object} [editObject.value] The value to edit.
     * @param {Boolean} [editObject.focus=true] Focus the field.
     * @param {Boolean} [editObject.fitTargetContent] Pass `true` to allow the Editor to expand beyond the
     * width of its target element if its content overflows horizontally. This is useful if the editor has
     * triggers to display, such as a combo.
     */
    startEdit({
        target,
        align = 't0-t0',
        hideTarget = false,
        matchSize = true,
        matchFont = true,
        fitTargetContent = false,
        value,
        record,
        field = this.inputField.name,
        focus = true
    }) {
        const
            me               = this,
            { inputField }   = me,
            targetRect       = (target instanceof Rectangle) ? target : Rectangle.inner(target),
            targetFontSize   = DomHelper.getStyleValue(target, 'font-size'),
            targetFontFamily = DomHelper.getStyleValue(target, 'font-family');

        if (me.trigger('beforestart', { value }) !== false) {
            if (record && field && value === undefined) {
                me.record = record;
                me.dataField = field;
                value = record[field];
            }
            if (matchSize) {
                me.width = targetRect.width;
                me.height = targetRect.height;
            }
            if (inputField.input) {
                if (matchFont) {
                    inputField.input.style.fontSize = targetFontSize;
                    inputField.input.style.fontFamily = targetFontFamily;
                }
                else {
                    inputField.input.style.fontSize = inputField.input.style.fontFamily = '';
                }
            }

            // In case our finalize code set it to invalid, start it clear of errors.
            if (inputField.clearError) {
                inputField.clearError();
            }

            inputField.value = value;

            // The initialValue is what the revertOnEscape uses by preference before it uses its valueOnFocus.
            // In an Editor, it can focus in and out but still need that correct initial value.
            inputField.initialValue = inputField.value;

            me.showBy({
                target,
                align
            });

            if (fitTargetContent) {
                // Input doesn't fit, so widen it
                const overflow = inputField.input.scrollWidth - inputField.input.clientWidth;
                if (overflow > 0) {
                    me.width += overflow + DomHelper.scrollBarWidth;
                }
            }

            if (focus && me.inputField.focus) {
                me.inputField.focus();
            }
            if (target.nodeType === 1) {
                target.classList.add('b-editing');
                if (hideTarget) {
                    target.classList.add('b-hide-visibility');
                }
            }
            // Passed value may have been '10/06/2019', send the live field value to startedit
            me.trigger('start', { value : inputField.value });
            me.oldValue = inputField.value;

            // If the value from th value getter is an array, we must clone it because
            // if it's the same *instance*, the ObjectHelper.isEqual test in completeEdit
            // will find that there are no changes.
            if (Array.isArray(me.oldValue)) {
                me.oldValue = me.oldValue.slice();
            }
            return true;
        }
        return false;
    }

    refreshEdit() {
        if (this.isVisible) {
            const { record, dataField, inputField } = this;

            if (record && dataField) {
                const value = record[dataField];

                // Only update the field if the value has changed
                if (!ObjectHelper.isEqual(inputField.value, value)) {
                    inputField.value = value;
                }
            }
        }
    }

    onKeyDown(event) {
        const me = this;

        switch (event.key) {
            case me.completeKey:
                me.completeEdit();
                event.stopImmediatePropagation();
                break;
            case me.cancelKey:
                me.cancelEdit();
                event.stopImmediatePropagation();
                break;
        }
        me.trigger('keydown', { event });
    }

    onFocusOut(event) {
        super.onFocusOut(event);

        if (!this.isFinishing) {
            const method = this[`${this.blurAction}Edit`];

            if (method) {
                method.call(this);
            }
        }
    }

    set allowInvalid(allowInvalid) {
        this._allowInvalid = allowInvalid;
        me.invalidAction = allowInvalid ? 'allow' : 'block';
    }

    get allowInvalid() {
        return this._allowInvalid;
    }

    /**
     * Complete the edit, and, if associated with a record, update the record if possible.
     * If editing is completed, the editor is hidden.
     *
     * If the field is invalid, the `{@link #config-invalidAction}` config is used to decide
     * upon the course of action.
     *
     * If a {@link #event-beforeComplete} handler returns `false` then editing is not completed.
     *
     * If the field's valus has not been changed, then editing is terminated through {@link #function-cancelEdit}.
     *
     * @returns `true` if editing ceased, `false` if the editor is still active.
     * @async
     */
    async completeEdit(finalize) {
        const me = this,
            { inputField, oldValue, record } = me,
            invalidAction = inputField.invalidAction || (inputField.allowInvalid === false ? 'block' : me.invalidAction),
            { value } = inputField;

        // If we're configured not to allow invalid values, refocus the field in case complete was triggered by focusout.
        if (!inputField.isValid) {
            if (invalidAction === 'block') {
                inputField.focus && inputField.focus();
                return false;
            }
            else if (invalidAction === 'revert') {
                me.cancelEdit();
                return true;
            }
        }
        // No change means a cancel.
        else if (ObjectHelper.isEqual(value, oldValue)) {
            me.cancelEdit();
            return true;
        }
        // Allow veto of the completion
        else {
            const context = { inputField, record, value, oldValue };

            if (me.trigger('beforeComplete', context) === false) {
                inputField.focus && inputField.focus();
            }
            else {
                // CellEdit#onEditorBeforeComplete injects editorContext into the basic context
                if (!finalize) {
                    finalize = context.finalize || (context.editorContext && context.editorContext.finalize);
                }

                // Allow async finalization of the editing, implementer may want to show a confirmation popup etc
                if (finalize) {
                    let result = await finalize(context);

                    if (result === true) {
                        me.onEditComplete();
                    }
                    else {
                        if (inputField.setError) {
                            const
                                error = result || inputField.L('invalidValue'),
                                clearError = () => {
                                    listeners();
                                    inputField.clearError(error);
                                },
                                listeners = inputField.on({
                                    change : clearError,
                                    input  : clearError
                                });

                            // Mark as invalid. Because this is decided upon without the knowledge
                            // of the field, this state will be rescinded upon the next change of
                            // input field.
                            inputField.setError(error);
                        }
                        if (invalidAction === 'block') {
                            inputField.focus && inputField.focus();
                        }
                        else if (invalidAction === 'revert') {
                            inputField.value = oldValue;
                            result = true;
                        }
                        result = false;
                    }
                    return result;
                }
                // Successful completion
                else {
                    me.onEditComplete();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Cancel the edit and hide the editor.
     */
    cancelEdit() {
        const me = this,
            { inputField, oldValue, lastAlignSpec } = me,
            { target } = lastAlignSpec,
            { value } = inputField;

        if (!me.isFinishing && me.trigger('beforeCancel', { value : value, oldValue }) !== false) {
            // Hiding must not trigger our blurAction
            me.isFinishing = true;
            me.hide();
            me.trigger('cancel', { value, oldValue });
            if (target.nodeType === 1) {
                target.classList.remove('b-editing');
                target.classList.remove('b-hide-visibility');
            }
            me.isFinishing = false;
        }
    }

    // Handle updating what needs to be updated.
    onEditComplete() {
        const me = this,
            { record, dataField, inputField, oldValue, lastAlignSpec } = me,
            { target } = lastAlignSpec,
            { value } = inputField;

        if (!me.isFinishing) {
            // Hiding must not trigger our blurAction
            me.isFinishing = true;
            me.hide();

            if (record) {
                const setterName = `set${StringHelper.capitalizeFirstLetter(dataField)}`;
                if (record[setterName]) {
                    record[setterName](value);
                }
                else {
                    record[dataField] = value;
                }
            }
            me.trigger('complete', { value, oldValue });
            if (target.nodeType === 1) {
                target.classList.remove('b-editing');
                target.classList.remove('b-hide-visibility');
            }

            me.isFinishing = false;
        }
    }

    doDestroy() {
        if (this.createdInputField) {
            this.inputField.destroy();
        }
        super.doDestroy();
    }

    set owner(owner) {
        this._owner = owner;
    }

    // This is a positioned widget appended to a Widget's contentElement. It may have no owner link.
    // Grab the owner by finding what widget it is inside.
    get owner() {
        return this._owner || IdHelper.fromElement(this.element.parentNode);
    }

    set inputField(inputField) {
        const me = this;

        if (me._inputField) {
            me._inputField.destroy();
        }
        if (typeof inputField === 'string') {
            inputField = {
                type : inputField
            };
        }
        if (inputField instanceof Widget) {
            me._inputField = inputField;
        }
        else {
            me._inputField = WidgetHelper.createWidget(inputField);
            me.createdInputField = true; // So we know we can destroy it
        }

        if (me.completeOnChange) {
            me._inputField.on({
                change  : 'onInputFieldChange',
                thisObj : me
            });
        }

        me._inputField.parent = me;

        me.removeAll();
        me.add(inputField);
    }

    get inputField() {
        return this.items[0];
    }

    onInputFieldChange() {
        if (this.containsFocus) {
            this.completeEdit();
        }
    }
}

BryntumWidgetAdapterRegister.register('editor', Editor);
