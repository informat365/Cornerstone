import Field from './Field.js';
import TemplateHelper from '../helper/TemplateHelper.js';
import DomHelper from '../helper/DomHelper.js';
import WidgetHelper from '../helper/WidgetHelper.js';
import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';

/**
 * @module Core/widget/Checkbox
 */

/**
 * Checkbox field, wraps <code>&lt;input type="checkbox"&gt;</code>.
 * Color can be specified and you can optionally configure {@link #config-text}
 * to display in a label to the right of the checkbox in addition to a standard
 * field {@link #config-label}.
 *
 * @extends Core/widget/Field
 *
 * @example
 * // checkbox with a label and a handler
 * let checkbox = new Checkbox({
 *   text: 'Check me, please',
 *   onAction: () => {}
 * });
 *
 * @classType checkbox
 * @externalexample widget/Checkbox.js
 */
export default class Checkbox extends Field {
    //region Config
    static get $name() {
        return 'Checkbox';
    }

    static get defaultConfig() {
        return {
            /**
             * Text to display on checkbox label
             * @config {String}
             */
            text : '',

            /**
             * Checkbox color, must have match in css
             * @config {String}
             */
            color : null,

            /**
             * Sets input fields value attribute
             * @config {String}
             */
            value : '',

            toggleGroup : null,

            defaultBindProperty : 'value',

            localizableProperties : ['label', 'text']
        };
    }

    //endregion

    //region Init

    construct(config) {
        super.construct(config);

        const me = this;

        if (me.initialConfig.readOnly) me.readOnly = true;
    }

    inputTemplate() {
        const me = this;

        return TemplateHelper.tpl`
            <input type="checkbox" id="${me.id}_input" reference="input" ${me.toggleGroup ? `data-group="${me.toggleGroup}"` : ``}/>
            <label class="b-checkbox-label" for="${me.id}_input" reference="textLabel">${me.text || ''}</label>
        `;
    }

    set element(element) {
        const me = this;

        super.element = element;

        if (me.color) {
            me.element.classList.add(me.color);
        }
        if (me.text) {
            me.element.classList.add('b-text');
        }
    }

    get element() {
        return super.element;
    }
    //endregion

    //region Toggle

    /**
     * Get/set label
     * @property {String}
     */
    get text() {
        return this._text;
    }

    set text(value) {
        this._text = value;
        if (this.textLabel) {
            this.textLabel.innerHTML = value;
        }
    }

    /**
     * Get/set value
     * @property {String}
     */
    get value() {
        return this.input.value;
    }

    set value(value) {
        this.input.value = value;
    }

    /**
     * Get/set checked state
     * @property {Boolean}
     */
    get checked() {
        return this.input.checked;
    }

    set checked(checked) {
        const me = this;

        checked = Boolean(checked);

        // Only do action if change needed.
        if (me.input.checked !== checked) {
            me.input.checked = checked;

            me.uncheckToggleGroupMembers();

            // The change event does not fire on programmatic change of input.
            if (!me.isConfiguring) {
                me.triggerChange(false);
            }
        }
    }

    getToggleGroupMembers() {
        const
            me = this,
            { checked, toggleGroup, input : checkedElement } = me,
            result = [];

        if (checked && toggleGroup) {
            DomHelper.forEachSelector(`input[type=checkbox][data-group=${toggleGroup}]`, inputEl => {
                if (inputEl !== checkedElement) {
                    const partnerCheckbox = WidgetHelper.fromElement(inputEl);
                    partnerCheckbox && result.push(partnerCheckbox);
                }
            });
        }

        return result;
    }

    uncheckToggleGroupMembers() {
        if (this.checked && this.toggleGroup) {
            this.getToggleGroupMembers().forEach(widget => widget.checked = false);
        }
    }

    /**
     * Get/set readonly state (disabled underlying input)
     * @property {Boolean}
     */
    get readOnly() {
        return this._readOnly;
    }

    set readOnly(readOnly) {
        this._readOnly = readOnly;
        this.element.classList[readOnly ? 'add' : 'remove']('b-readonly');
        this.input.disabled = readOnly;
    }

    /**
     * Check the box
     */
    check() {
        this.checked = true;
    }

    /**
     * Uncheck the box
     */
    uncheck() {
        this.checked = false;
    }

    /**
     * Toggle checked state. If you want to force a certain state, assign to {@link #property-checked} instead.
     */
    toggle() {
        this.checked = !this.checked;
    }

    //endregion

    //region Events

    /**
     * Triggers events when user toggles the checkbox
     * @fires beforeChange
     * @fires change
     * @fires action
     * @private
     */
    internalOnChange(event) {
        /**
         * Fired before checkbox is toggled. Returning false from a listener prevents the checkbox from being toggled.
         * @event beforeChange
         * @preventable
         * @param {Core.widget.Checkbox} source Checkbox
         * @param {Boolean} checked Checked or not
         */

        /**
         * Fired when checkbox is toggled
         * @event change
         * @param {Core.widget.Checkbox} source Checkbox
         * @param {Boolean} checked Checked or not
         */

        this.triggerChange(true);
    }

    /**
     * Triggers events when checked state is changed
     * @fires beforeChange
     * @fires change
     * @fires action
     * @private
     */
    triggerChange(userAction) {
        const me = this,
            { checked } = me.input;

        // Prevent uncheck if this checkbox is part of a toggleGroup (radio-button mode) ..also ensure the group has visible active members
        const prevented = (!checked && userAction && me.toggleGroup && me.getToggleGroupMembers().filter(widget => widget.isVisible && !widget.disabled).length) ||
            // Since Widget has Events mixed in configured with 'callOnFunctions' this will also call onBeforeChange, onChange and onAction
            !me.callPreventable(
                'change',

                { checked, userAction }, eventObject => {

                    if (userAction) {
                        me.uncheckToggleGroupMembers();
                    }

                    /**
                    * User performed the default action (toggled the checkbox)
                    * @event action
                    * @param {Core.widget.Checkbox} source Checkbox
                    * @param {Boolean} checked Checked or not
                    */
                    me.trigger('action', eventObject);

                    return true;
                }
            );

        // If prevented need to rollback the checkbox input
        if (prevented) {
            // Input change is not preventable, so need to revert the changes
            // The change event does not fire on programmatic change of input, so no need to suspend
            me.input.checked = !me.input.checked;
        }
    }

    //endregion
}

BryntumWidgetAdapterRegister.register('checkbox', Checkbox);
BryntumWidgetAdapterRegister.register('check', Checkbox);
