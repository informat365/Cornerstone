import InstancePlugin from '../../../Core/mixin/InstancePlugin.js';
import DateField from '../../../Core/widget/DateField.js';
import DateHelper from '../../../Core/helper/DateHelper.js';
import ObjectHelper from '../../../Core/helper/ObjectHelper.js';

/**
 * @module Scheduler/feature/base/EditBase
 */

const DH = DateHelper,
    makeDate = (fields) => {
        // single field, update record directly
        if (fields.length === 1) return fields[0].value;
        // two fields, date + time
        else if (fields.length === 2) {
            const [date, time] = fields[0] instanceof DateField ? fields : fields.reverse(),
                dateValue = DH.parse(date.value);

            dateValue.setHours(
                time.value.getHours(),
                time.value.getMinutes(),
                time.value.getSeconds(),
                time.value.getMilliseconds()
            );

            return dateValue;
        }
        // shouldn't happen...
        return null;
    },
    copyTime = (dateTo, dateFrom) => {
        let d = new Date(dateTo.getTime());
        d.setHours(dateFrom.getHours(), dateFrom.getMinutes());
        return d;
    },
    adjustEndDate = (startDate, startTime, me) => {
        // The end datetime just moves in response to the changed start datetime, keeping the same duration.
        if (startDate && startTime) {
            const newEndDate = DH.add(copyTime(me.startDateField.value, me.startTimeField.value), me.eventRecord.durationMS, 'milliseconds');
            me.endDateField.value = newEndDate;
            me.endTimeField.value = DH.clone(newEndDate);
        }
    };

/**
 * Base class for EventEdit (Scheduler) and TaskEdit (Gantt) features. Contains shared code. Not to be used directly.
 *
 * @extends Core/mixin/InstancePlugin
 */
export default class EditBase extends InstancePlugin {
    //region Config

    // TODO: check which configs are actually used

    static get defaultConfig() {
        return {
            /**
             * True to hide this editor if a click is detected outside it (defaults to true)
             * @config {Boolean}
             * @default
             * @category Editor
             */
            autoClose : true,

            /**
             * True to save and close this panel if ENTER is pressed in one of the input fields inside the panel.
             * @config {Boolean}
             * @default
             * @category Editor
             */
            saveAndCloseOnEnter : true,

            triggerEvent : null,

            /**
             * True to show a delete button in the form.
             * @config {Boolean}
             * @default
             * @category Editor widgets
             */
            showDeleteButton : true,

            /**
             * True to show a text field for entering event name
             * @config {Boolean}
             * @default
             * @category Editor widgets
             */
            showNameField : true,

            /**
             * Config for the `startTimeField` constructor.
             * @config {Object}
             * @category Editor widgets
             */
            startTimeConfig : null,

            /**
             * Config for the `startDateField` constructor.
             * @config {Object}
             * @category Editor widgets
             */
            startDateConfig : null,

            /**
             * Config for the `endTimeField` constructor.
             * @config {Object}
             * @category Editor widgets
             */
            endTimeConfig : null,

            /**
             * Config for the `endDateField` constructor.
             * @config {Object}
             * @category Editor widgets
             */
            endDateConfig : null,

            /**
             * This config parameter is passed to the `startDateField` and `endDateField` constructor.
             * @config {String}
             * @default
             * @category Editor widgets
             */
            dateFormat : 'L', // date format that uses browser locale

            /**
             * This config parameter is passed to the `startTimeField` and `endTimeField` constructor.
             * @config {String}
             * @default
             * @category Editor widgets
             */
            timeFormat : 'LT', // date format that uses browser locale

            /**
             * Default editor configuration, which widgets it shows etc.
             * @config {Object}
             * @category Editor
             */
            editorConfig : null,

            /**
             * Array of widgets. Will be inserted above buttons unless an index is
             * specified for the widget:
             * ```
             * new Grid({
             *   features : {
             *     eventEdit : {
             *       extraItems : [
             *         { type : 'text', index : 1 },
             *         ...
             *       ]
             *     }
             *   }
             * });
             * ```
             * @config {String|Object[]}
             * @category Editor widgets
             */
            extraItems : null,

            /**
             * This config has been deprecated in favour of {@link #config-extraItems}.
             * @deprecated 2.1
             * @config {String|Object[]}
             * @category Editor widgets
             */
            extraWidgets : null
        };
    }

    //endregion

    //region Init & destroy

    construct(client, config) {
        const me = this;

        client.eventEdit = me;

        super.construct(client, config);

        me.clientListenersDetacher = client.on({
            [me.triggerEvent] : me.onActivateEditor,
            dragcreateend     : me.onDragCreateEnd,
            thisObj           : me
        });
    }

    doDestroy() {
        this.clientListenersDetacher();

        this.editor && this.editor.destroy();

        super.doDestroy();
    }

    //endregion

    //region Editing

    get editorConfig() {
        return this._editorConfig;
    }

    set editorConfig(editorConfig) {
        const me = this,
            defaultEditorConfig = me.getDefaultConfiguration().editorConfig;

        // Apply editorConfig to the default editorConfig, allowing users to manipulate for example only bbar
        editorConfig = ObjectHelper.assign({}, defaultEditorConfig, editorConfig);

        editorConfig.items = editorConfig.items || [];

        // Massage the incoming widgets according to our needs before caching the configuration for use in getEditor.
        me.insertExtraWidgetsIntoDefaultWidgets(editorConfig);

        me._editorConfig = editorConfig;
    }

    get extraWidgets() {
        if (!this._skipWarn) {
            console.warn('`extraWidgets` was deprecated in 2.1, please change your code to use `extraItems`');
        }
        return this._extraItems;
    }

    set extraWidgets(widgets) {
        console.warn('`extraWidgets` was deprecated in 2.1, please change your code to use `extraItems`');
        this._extraItems = widgets;
    }

    // TODO: When removing extraWidgets above, these can also be removed
    get extraItems() {
        const me = this;

        // Pull in extraWidgets, for compatibility
        me._skipWarn = true;
        me._thisIsAUsedExpression(me.extraWidgets);
        me._skipWarn = false;

        return me._extraItems;
    }

    set extraItems(items) {
        this._extraItems = items;
    }

    /**
     * Insert extra fields into default Editor fields according to specific rules:
     * - If no index provided insert them into `extraItems` placeholder (or at the end if not);
     * - If index provided sort ASC and insert one by one, but only after no-index fields are inserted;
     * - If `extraItems` placeholder exists, don't take widgets that go after it into account;
     * @private
     */
    insertExtraWidgetsIntoDefaultWidgets(editorConfig) {
        const me = this;

        if (!me.extraItems || !me.extraItems.length) {
            return;
        }

        // Find default extra widgets position
        let index = editorConfig.items.findIndex(widget => widget.type === 'extraItems'),
            tail;

        // If extra widgets placeholder exists
        if (index > -1) {
            // Remove extra widgets placeholder from its position
            editorConfig.items.splice(index, 1);

            // Backup everything that goes after extra widgets placeholder, like Save/Delete/Cancel buttons
            tail = editorConfig.items.splice(index);
        }

        // Split extra widgets on 2 parts: those which have index and those which haven't
        let withIndex    = me.extraItems.filter(widget => widget.index >= 0),
            withoutIndex = me.extraItems.filter(widget => !(widget.index >= 0));

        // Add those without index to the end of the default widgets
        editorConfig.items = editorConfig.items.concat(withoutIndex);

        // Sort those which have index in ASC order, so we insert fields in series
        withIndex.sort((widgetA, widgetB) => widgetA.index - widgetB.index);

        // And now insert extra widgets at their individually specified index
        withIndex.forEach(widget => editorConfig.items.splice(widget.index, 0, widget));

        if (tail && tail.length) {
            // Return backuped fields to the end of the widgets
            editorConfig.items = editorConfig.items.concat(tail);
        }
    }

    onDatesChange(params) {
        const me = this,
            field = params.source,
            value = params.value;

        switch (field.ref) {
            case 'startDateField':
                me.startTimeField && adjustEndDate(value, me.startTimeField.value, me);
                break;

            case 'startTimeField':
                me.startDateField && adjustEndDate(me.startDateField.value, value, me);
                break;
        }

        if (me.endTimeField) {
            // If the event starts and ends on the same day, the time fields need
            // to have their min and max set against each other.
            if (DH.isEqual(DH.clearTime(me.startDateField.value), DH.clearTime(me.endDateField.value))) {
                me.endTimeField.min = me.startTimeField.value;
            }
            else {
                me.endTimeField.min = null;
            }
        }
    }

    //endregion

    //region Save

    async save() {
        throw new Error('Implement in subclass');
    }

    get isValid() {
        const me = this;
        return Object.values(me.editor.widgetMap).every(field => {
            if (!field.name || field.hidden) {
                return true;
            }

            return field.isValid !== false;
        });
    }

    get values() {
        const
            me          = this,
            startFields = [],
            endFields   = [],
            values      = {};

        me.editor.eachWidget(widget => {
            const name = widget.name;

            if (!name || widget.hidden) {
                return;
            }

            switch (name) {
                case 'startDate':
                    startFields.push(widget);
                    break;
                case 'endDate':
                    endFields.push(widget);
                    break;
                case 'resource':
                    values[name] = widget.record;
                    break;
                default:
                    values[name] = widget.value;
            }
        }, true);

        values.startDate = makeDate(startFields);
        values.endDate = makeDate(endFields);

        // Since there is no duration field in the editor,
        // we don't need to recalc duration value on each date change.
        // It's enough to return correct duration value in `values`,
        // so the record will get updated with the correct data.
        values.duration = DH.diff(values.startDate, values.endDate, me.editor.record.durationUnit, true);

        return values;
    }

    /**
     * Template method, intended to be overridden. Called before the event record has been updated.
     * @param {Scheduler.model.EventModel} eventRecord The event record
     *
     **/
    onBeforeSave(eventRecord) {}

    /**
     * Template method, intended to be overridden. Called after the event record has been updated.
     * @param {Scheduler.model.EventModel} eventRecord The event record
     *
     **/
    onAfterSave(eventRecord) {}

    /**
     * Updates record being edited with values from the editor
     * @private
     */
    updateRecord(record) {
        const { values } = this;

        // Clean resourceId / resources out of values when using assignment store, it will handle the assignment
        if (this.scheduler.assignmentStore) {
            delete values.resource;
        }

        record.set(values);
    }

    //endregion

    //region Events

    onPopupBeforeHide() {
        const me = this;
        // reset flag indicating that we are editing
        me.isEditing = false;
        me.client.element.classList.remove('b-eventeditor-editing');
        me.dragProxyElement && me.dragProxyElement.remove();
        me.dragProxyElement = null;
    }

    onPopupKeyDown({ event }) {
        if (event.key === 'Enter' && this.saveAndCloseOnEnter && event.target.tagName.toLowerCase() === 'input') {
            // Need to prevent this key events from being fired on whatever receives focus after the editor is hidden
            event.preventDefault();

            // If enter key was hit in an input element of a start field, need to adjust end date fields (the same way as if #onDatesChange handler was called)
            if (event.target.name === 'startDate') {
                adjustEndDate(this.startDateField.value, this.startTimeField.value, this);
            }

            this.onSaveClick();
        }
    }

    async onSaveClick() {
        const saved = await this.save();

        if (saved) {
            this.editor.close();
        }
    }

    async onDeleteClick() {
        const removed = await this.deleteEvent();

        if (removed) {
            // We expect deleteEvent will trigger close if autoClose is true and focus has moved out,
            // otherwise need to call it manually
            if (!this.editor.autoClose || this.editor.containsFocus) {
                this.editor.close();
            }
        }
    }

    onCancelClick() {
        this.editor.close();
    }

    //endregion
}
