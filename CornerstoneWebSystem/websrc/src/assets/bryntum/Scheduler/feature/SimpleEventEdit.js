import GridFeatureManager from '../../Grid/feature/GridFeatureManager.js';
import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import AssignmentModel from '../model/AssignmentModel.js';
import Editor from '../../Core/widget/Editor.js';
import DomHelper from '../../Core/helper/DomHelper.js';

/**
 * @module Scheduler/feature/SimpleEventEdit
 */

/**
 * Feature that displays a text field to edit the event name. You can control the flow of this by listening to the events relayed by this class from the underlying {@link Core.widget.Editor}.
 * To use this feature, you also need to disable the built-in default editing feature:
 *
 * ```javascript
 * const scheduler = new Scheduler({
 *     features : {
 *         eventEdit       : false,
 *         simpleEventEdit : true
 *     }
 * });
 * ```
 *
 * This feature is **disabled** by default
 *
 * @extends Core/mixin/InstancePlugin
 * @demo Scheduler/simpleeditor
 * @externalexample scheduler/SimpleEventEdit.js
 */
export default class SimpleEventEdit extends InstancePlugin {

    // region Events
    /**
     * Fired before the editor is shown to start an edit operation. Returning `false` from a handler vetoes the edit operation.
     * @event beforeStart
     * @preventable
     * @property {Object} value - The value to be edited.
     * @property {Core.widget.Editor} source - The Editor that triggered the event.
     */
    /**
     * Fired when an edit operation has begun.
     * @event start
     * @property {Object} value - The starting value of the field.
     * @property {Core.widget.Editor} source - The Editor that triggered the event.
     */
    /**
     * Fired when an edit completion has been requested, either by `ENTER`, or focus loss (if configured to complete on blur).
     * The completion may be vetoed, in which case, focus is moved back into the editor.
     * @event beforeComplete
     * @property {Object} oldValue - The original value.
     * @property {Object} value - The new value.
     * @property {Core.widget.Editor} source - The Editor that triggered the event.
     * @preventable
     */
    /**
     * Edit has been completed, and any associated record or element has been updated.
     * @event complete
     * @property {Object} oldValue - The original value.
     * @property {Object} value - The new value.
     * @property {Core.widget.Editor} source - The Editor that triggered the event.
     */
    /**
     * Fired when cancellation has been requested, either by `ESC`, or focus loss (if configured to cancel on blur).
     * The cancellation may be vetoed, in which case, focus is moved back into the editor.
     * @event beforeCancel
     * @property {Object} oldValue - The original value.
     * @property {Object} value - The new value.
     * @property {Core.widget.Editor} source - The Editor that triggered the event.
     * @preventable
     */
    /**
     * Edit has been canceled without updating the associated record or element.
     * @event cancel
     * @property {Object} oldValue - The original value.
     * @property {Object} value - The value of the field.
     * @property {Core.widget.Editor} source - The Editor that triggered the event.
     */
    // endregion

    //region Config

    static get $name() {
        return 'SimpleEventEdit';
    }

    static get defaultConfig() {
        return {
            /**
             * The event that shall trigger showing the editor. Defaults to `eventdblclick`, set to `` or null to disable editing of existing events.
             * @config {String}
             * @default
             * @category Editor
             */
            triggerEvent : 'eventdblclick',

            /**
             * The current {@link Scheduler.model.EventModel} record, which is being edited by the event editor.
             * @property {Scheduler.model.EventModel}
             * @readonly
             */
            eventRecord : null,

            /**
             * The {@link Scheduler.model.EventModel} field to edit
             * @config {String}
             * @category Editor
             */
            field : 'name',

            /**
             * The editor configuration, where you can control which widget to show
             * @config {Object}
             * @category Editor
             */
            editorConfig : null
        };
    }

    static get pluginConfig() {
        return {
            chain  : ['onEventEnterKey'],
            assign : ['editEvent']
        };
    }

    //endregion

    //region Editing

    construct(scheduler, config) {
        const me = this;

        me.scheduler  = scheduler;
        me.eventStore = scheduler.eventStore;

        scheduler.eventEdit = me;

        super.construct(scheduler, config);

        me.clientListenersDetacher = scheduler.on({
            [me.triggerEvent] : ({ eventRecord, eventElement }) => me.editEvent(eventRecord, eventRecord.resource, eventElement),
            dragcreateend     : me.onDragCreateEnd,
            thisObj           : me
        });
    }

    doDestroy() {
        this.clientListenersDetacher();

        this.editor && this.editor.destroy();

        super.doDestroy();
    }

    // region Editor
    /**
     * Opens an Editor for the passed event. This function is exposed on Scheduler and can be called as
     * `scheduler.editEvent()`.
     * @param {Scheduler.model.EventModel} eventRecord The Event to edit
     * @param {Scheduler.model.ResourceModel} [resourceRecord] The Resource record for the event.
     */
    editEvent(eventRecord, resourceRecord, element) {
        const
            me        = this,
            scheduler = me.scheduler;

        if (scheduler.readOnly || me.disabled) {
            return;
        }

        // Want to put editor in inner element (b-sch-event) to get correct font size, but when drag creating the proxy
        // has no inner element
        element = DomHelper.down(element, scheduler.eventInnerSelector) || element;

        eventRecord = eventRecord instanceof AssignmentModel ? eventRecord.event : eventRecord;

        me.resource = resourceRecord;
        me.event = eventRecord;
        me.element = element;

        scheduler.element.classList.add('b-eventeditor-editing');

        if (!me.editor) {
            // Editor is contained in, and owned by the TimeAxisSubGrid to avoid focus flipping out and in.
            // The editor is an owned descendant of the SubGrid.
            me.editor = new Editor(Object.assign({
                owner        : me.scheduler.timeAxisSubGrid,
                appendTo     : me.scheduler.timeAxisSubGridElement,
                scrollAction : 'realign',
                cls          : 'b-simpleeventeditor',
                listeners    : {
                    complete : me.onEditorComplete,
                    cancel   : me.onEditorCancel,
                    thisObj  : me
                }
            }, me.editorConfig));

            me.relayEvents(me.editor, ['beforestart', 'start', 'beforecomplete', 'complete', 'beforecancel', 'cancel']);
        }

        me.editor.startEdit({
            target : element,
            record : eventRecord,
            field  : me.field
        });
    }

    onEditorComplete() {
        const me = this;

        if (me.event.stores.length === 0) {
            me.eventStore.add(me.editor.record);
            me.event.assign(me.resource);
        }

        // remove element if that's a drag create proxy
        if (me.element && me.element.classList.contains('b-sch-dragcreator-proxy')) {
            me.element.remove();
        }

        me.scheduler.element.classList.remove('b-eventeditor-editing');
    }

    onEditorCancel() {
        if (this.event.stores.length === 0) {
            this.element.remove();
        }
        this.scheduler.element.classList.remove('b-eventeditor-editing');
    }

    //endregion

    // chained from EventNavigation
    onEventEnterKey({ assignmentRecord, eventRecord }) {
        const
            element = assignmentRecord ? this.scheduler.getElementFromAssignmentRecord(assignmentRecord) : this.scheduler.getElementFromEventRecord(eventRecord),
            resourceRecord = (assignmentRecord || eventRecord).resource;

        this.editEvent(eventRecord, resourceRecord, element);
    }

    //endregion

    onDragCreateEnd({ newEventRecord, resourceRecord, proxyElement }) {
        const me = this;

        // Call scheduler template method
        me.scheduler.onEventCreated(newEventRecord);

        // Clone proxy after showing editor so it's not deleted
        const dragProxyElement = proxyElement.cloneNode(true);
        dragProxyElement.removeAttribute('id');

        proxyElement.parentElement.appendChild(dragProxyElement);

        me.element = dragProxyElement;

        me.editEvent(newEventRecord, resourceRecord, dragProxyElement);
    }
}

GridFeatureManager.registerFeature(SimpleEventEdit, false, 'Scheduler');
