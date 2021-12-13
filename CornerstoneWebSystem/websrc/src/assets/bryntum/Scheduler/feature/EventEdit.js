import EditBase from './base/EditBase.js';
import GridFeatureManager from '../../Grid/feature/GridFeatureManager.js';
import DomHelper from '../../Core/helper/DomHelper.js';
import ObjectHelper from '../../Core/helper/ObjectHelper.js';
import EventEditor from '../view/EventEditor.js';
import RecurringEventEdit from './mixin/RecurringEventEdit.js';

/**
 * @module Scheduler/feature/EventEdit
 */

/**
 * Feature that displays a popup containing fields for editing event data.
 *
 * To customize its contents you can:
 *
 * * Toggle visibility of some built in widgets: `showResourceField`, `showNameField` and `showDeleteButton`
 * * Change the date format of the date & time fields: `dateFormat` and `timeFormat`
 * * Configure date & time fields: `startDateConfig`, `startTimeConfig`, `endDateConfig` and `endTimeConfig`
 * * Configure the resource field: `resourceFieldConfig`
 * * Append additional fields: `extraItems`
 * * Advanced: Replace entire contents using `editorConfig`
 *
 * This feature is **enabled** by default
 *
 * @mixes Scheduler/feature/mixin/RecurringEventEdit
 * @extends Scheduler/feature/base/EditBase
 * @demo Scheduler/eventeditor
 * @externalexample scheduler/EventEdit.js
 */
export default class EventEdit extends RecurringEventEdit(EditBase) {
    //region Config

    static get $name() {
        return 'EventEdit';
    }

    // TODO: check which configs are actually used

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
             * True to show a combo for picking resource
             * @config {Boolean}
             * @default
             * @category Editor widgets
             */
            showResourceField : true,

            // TODO remove this config?
            /**
             * Config for the resourceField constructor.
             * @config {Object}
             * @category Editor widgets
             */
            resourceFieldConfig : null,

            /**
             * The data field in the model that defines the eventType.
             * Applied as class (b-eventtype-xx) to the editors element, to allow showing/hiding fields depending on
             * eventType. Dynamic toggling of fields in the editor is activated by using `ref: 'eventTypeField'` on
             * your widget:
             *
             * ```javascript
             * const scheduler = new Scheduler({
             *    features : {
             *       eventEdit  : {
             *           extraItems : [
             *               {
             *                   type  : 'combo',
             *                   name  : 'eventType',
             *                   ref   : 'eventTypeField',
             *                   label : 'Type',
             *                   items : ['Appointment', 'Internal', 'Meeting']
             *               }
             *           ]
             *        }
             *     }
             * });
             * ```
             *
             * @config {String}
             * @default
             * @category Editor
             */
            typeField : 'eventType',

            /**
             * The current {@link Scheduler.model.EventModel} record, which is being edited by the event editor.
             * @property {Scheduler.model.EventModel}
             * @readonly
             */
            eventRecord : null,

            /**
             * Specify `true` to put the editor in read only mode.
             * @config {Boolean}
             * @default false
             */
            readOnly : null,

            /**
             * The configuration for the {@link Scheduler.view.EventEditor editor}. With this config you can control which widgets to shows, if popup should be modal etc.
             *
             * ```javascript
             * const scheduler = new Scheduler({
             *    features : {
             *       eventEdit  : {
             *           editorConfig : {
             *               modal  : true,
             *               cls    : 'my-editor' // A CSS class
             *           }
             *        }
             *     }
             * });
             * ```
             * @config {Object}
             * @category Editor
             */
            editorConfig : {
                title       : 'L{Edit Event}',
                localeClass : this,
                closable    : true,

                items : [
                    /**
                     * Reference to the name field, if used
                     * @member {Core.widget.TextField} nameField
                     * @readonly
                     */
                    {
                        type        : 'text',
                        localeClass : this,
                        label       : 'L{Name}',
                        clearable   : true,
                        name        : 'name',
                        ref         : 'nameField'
                    },
                    /**
                     * Reference to the resource field, if used
                     * @member {Core.widget.Combo} resourceField
                     * @readonly
                     */
                    {
                        type         : 'combo',
                        localeClass  : this,
                        label        : 'L{Resource}',
                        name         : 'resource',
                        ref          : 'resourceField',
                        editable     : false,
                        valueField   : 'id',
                        displayField : 'name'
                    },
                    /**
                     * Reference to the start date field, if used
                     * @member {Core.widget.DateField} startDateField
                     * @readonly
                     */
                    {
                        type        : 'date',
                        cls         : 'b-inline',
                        clearable   : false,
                        required    : true,
                        localeClass : this,
                        label       : 'L{Start}',
                        name        : 'startDate',
                        ref         : 'startDateField',
                        flex        : '1 0 60%'
                    },
                    /**
                     * Reference to the start time field, if used
                     * @member {Core.widget.TimeField} startTimeField
                     * @readonly
                     */
                    {
                        type      : 'time',
                        clearable : false,
                        required  : true,
                        name      : 'startDate',
                        ref       : 'startTimeField',
                        cls       : 'b-match-label',
                        flex      : '1 0 40%'
                    },
                    /**
                     * Reference to the end date field, if used
                     * @member {Core.widget.DateField} endDateField
                     * @readonly
                     */
                    {
                        type        : 'date',
                        cls         : 'b-inline',
                        clearable   : false,
                        required    : true,
                        localeClass : this,
                        label       : 'L{End}',
                        name        : 'endDate',
                        ref         : 'endDateField',
                        flex        : '1 0 60%'
                    },
                    /**
                     * Reference to the end time field, if used
                     * @member {Core.widget.TimeField} endTimeField
                     * @readonly
                     */
                    {
                        type      : 'time',
                        clearable : false,
                        required  : true,
                        name      : 'endDate',
                        ref       : 'endTimeField',
                        cls       : 'b-match-label',
                        flex      : '1 0 40%'
                    },
                    {
                        // widgets specified in extraItems will be inserted here
                        type : 'extraItems'
                    }
                ],

                bbar : [
                    {
                        type : 'widget',
                        cls  : 'b-label-filler'
                    },
                    /**
                     * Reference to the save button, if used
                     * @member {Core.widget.Button} saveButton
                     * @readonly
                     */
                    {
                        color       : 'b-green',
                        localeClass : this,
                        text        : 'L{Save}',
                        ref         : 'saveButton'
                    },
                    /**
                     * Reference to the delete button, if used
                     * @member {Core.widget.Button} deleteButton
                     * @readonly
                     */
                    {
                        color       : 'b-gray',
                        localeClass : this,
                        text        : 'L{Delete}',
                        ref         : 'deleteButton'
                    },
                    /**
                     * Reference to the cancel button, if used
                     * @member {Core.widget.Button} cancelButton
                     * @readonly
                     */
                    {
                        color       : 'b-gray',
                        localeClass : this,
                        text        : 'L{Cancel}',
                        ref         : 'cancelButton'
                    }
                ]
            }
        };
    }

    static get pluginConfig() {
        return {
            chain : [
                'getEventMenuItems',
                'onEventEnterKey'
            ],
            assign : ['editEvent']
        };
    }

    //endregion

    //region Init & destroy

    construct(scheduler, config) {
        const me = this;

        me.scheduler = scheduler;
        me.eventStore = scheduler.eventStore;
        me.resourceStore = scheduler.resourceStore;

        super.construct(scheduler, config);
    }

    //endregion

    //region Editing

    /**
     * Get/set readonly state
     * @property {Boolean}
     */
    get readOnly() {
        return this.editor ? this.editor.readOnly : this._readOnly;
    }

    set readOnly(readOnly) {
        this._readOnly = readOnly;

        if (this.editor) {
            this.editor.readOnly = readOnly;
        }
    }

    /**
     * Gets an editor instance. Creates on first call, reuses on consecutive
     * @internal
     * @returns {Core.widget.Popup} Editor popup
     */
    getEditor() {
        const me = this;

        let { editor } = me;

        if (editor) {
            return editor;
        }

        editor = me.editor = new EventEditor(me.getEditorConfig());

        if (editor.items.length === 0) {
            console.warn('Event Editor configured without any `items`');
        }

        // add listeners programmatically so users cannot override them accidentally
        editor.on({
            beforehide : me.onPopupBeforeHide,
            keydown    : me.onPopupKeyDown,
            thisObj    : me
        });

        // assign widget variables, using widget name: startDate -> me.startDateField
        // widgets with id set use that instead, id -> me.idField
        Object.values(editor.widgetMap).forEach(widget => {
            const ref = widget.ref || widget.id;
            // don't overwrite if already defined
            if (ref && !me[ref]) {
                me[ref] = widget;

                switch (widget.name) {
                    case 'startDate':
                    case 'endDate':
                        widget.on('change', me.onDatesChange, me);
                        break;
                }
            }
        });

        // launch onEditorConstructed hook if provided
        me.onEditorConstructed && me.onEditorConstructed(editor);

        me.eventTypeField && me.eventTypeField.on('change', me.onEventTypeChange, me);

        me.saveButton && me.saveButton.on('click', me.onSaveClick, me);
        me.deleteButton && me.deleteButton.on('click', me.onDeleteClick, me);
        me.cancelButton && me.cancelButton.on('click', me.onCancelClick, me);

        return me.editor;
    }

    getEditorConfig() {
        const
            me = this,
            { autoClose, cls, readOnly } = me;

        return ObjectHelper.assign({
            eventEditFeature : me,
            align            : 'b-t',
            id               : `${me.scheduler.id}-event-editor`,
            autoShow         : false,
            anchor           : true,
            scrollAction     : 'realign',
            clippedBy        : [me.scheduler.timeAxisSubGridElement, me.scheduler.bodyContainer],
            constrainTo      : window,
            autoClose,
            readOnly,
            cls
        }, me.editorConfig);
    }

    // Called from editEvent() to actually show the editor
    internalShowEditor(eventRecord, resourceRecord, element = null) {
        const
            me        = this,
            scheduler = me.scheduler,
            // Align to the element (b-sch-event) and not the wrapper
            eventElement = element || DomHelper.down(
                scheduler.getElementFromEventRecord(eventRecord, resourceRecord),
                scheduler.eventInnerSelector
            );

        // Event not in current TimeAxis - cannot be edited without extending the TimeAxis.
        // If there's no event element and the eventRecord is not in the store, we still
        // edit centered on the Scheduler - we're adding a new event
        if (eventElement || !eventRecord.isPartOfStore(scheduler.eventStore)) {
            /**
             * Fires on the owning Scheduler before an event is displayed in an editor.
             * This may be listened for to allow an application to take over event editing duties. Returning `false`
             * stops the default editing UI from being shown.
             * @event beforeEventEdit
             * @param {Scheduler.view.Scheduler} source The scheduler
             * @param {Scheduler.feature.EventEdit} eventEdit The eventEdit feature
             * @param {Scheduler.model.EventModel} eventRecord The record about to be shown in the event editor.
             * @param {Scheduler.model.ResourceModel} resourceRecord The Resource record for the event. If the event
             * is being created, it will not contain a resource, so this parameter specifies the resource the
             * event is being created for.
             * @param {HTMLElement} eventElement The element which represents the event in the scheduler display.
             * @preventable
             */
            if (scheduler.trigger('beforeEventEdit', {
                eventEdit : me,
                eventRecord,
                resourceRecord,
                eventElement
            }) === false) {
                scheduler.element.classList.remove('b-eventeditor-editing');
                me.dragProxyElement && me.dragProxyElement.remove();
                me.dragProxyElement = null;
                return;
            }

            me.resourceRecord = resourceRecord;

            const editor = me.getEditor(eventRecord);

            super.internalShowEditor && super.internalShowEditor(eventRecord, resourceRecord, element);

            if (me.typeField) {
                me.toggleEventType(eventRecord.get(me.typeField));
            }

            /**
             * Fires on the owning Scheduler when the editor for an event is available but before it is populated with
             * data and shown. Allows manipulating fields etc.
             * @event beforeEventEditShow
             * @param {Scheduler.view.Scheduler} source The scheduler
             * @param {Scheduler.feature.EventEdit} eventEdit The eventEdit feature
             * @param {Scheduler.model.EventModel} eventRecord The record about to be shown in the event editor.
             * @param {Scheduler.model.ResourceModel} resourceRecord The Resource record for the event. If the event
             * is being created, it will not contain a resource, so this parameter specifies the resource the
             * event is being created for.
             * @param {HTMLElement} eventElement The element which represents the event in the scheduler display.
             * @param {Core.widget.Popup} editor The editor
             */
            scheduler.trigger('beforeEventEditShow', {
                eventEdit : me,
                eventRecord,
                resourceRecord,
                eventElement,
                editor
            });

            // raise flag indicating that we are editing an event
            me.isEditing = true;

            me.loadRecord(eventRecord, resourceRecord);

            if (eventElement) {
                eventElement.classList.add('b-editing');
                editor.anchor = true;
                editor.showBy(eventElement);
            }
            // We are adding a new event. Display the editor centered in the Scheduler
            else {
                editor.anchor = false;
                editor.showBy({
                    target : scheduler.element,
                    align  : 'c-c'
                });
            }

            // Adjust time field step increment based on timeAxis resolution
            const timeResolution = scheduler.timeAxisViewModel.timeResolution;

            if (timeResolution.unit === 'hour' || timeResolution.unit === 'minute') {
                me.startTimeField.step = me.endTimeField.step = `${timeResolution.increment}${timeResolution.unit}`;
            }
        }
    }

    /**
     * Opens an editor for the passed event. This function is exposed on Scheduler and can be called as
     * `scheduler.editEvent()`.
     * @param {Scheduler.model.EventModel} eventRecord Event to edit
     * @param {Scheduler.model.ResourceModel} [resourceRecord] The Resource record for the event.
     * This parameter is needed if the event is newly created for a resource and has not been assigned, or when using
     * multi assignment.
     * @param {HTMLElement} [element] Element to anchor editor to (defaults to events element)
     */
    editEvent(eventRecord, resourceRecord, element = null) {
        const
            me        = this,
            scheduler = me.scheduler;

        if (scheduler.readOnly || me.disabled) {
            return;
        }

        // The Promise being async allows a mouseover to trigger the event tip
        // unless we add the editing class immediately.
        scheduler.element.classList.add('b-eventeditor-editing');

        if (!resourceRecord) {
            if (eventRecord.resource) {
                resourceRecord = eventRecord.resource;
            }
            // New event not yet in store has not got the relation set up, use id if available
            else if (eventRecord.resourceId) {
                resourceRecord = scheduler.resourceStore.getById(eventRecord.resourceId);
            }
        }

        // If element is specified (call triggered by EventDragCreate)
        // Then we can align to that, and no scrolling is necessary.
        // If we are simply being asked to edit a new event which is not
        // yet added, the editor is centered, and no scroll is necessary
        if (element || scheduler.eventStore.indexOf(eventRecord) < 0) {
            me.internalShowEditor(eventRecord, resourceRecord, element);
        }
        else {
            // Ensure event is in view before showing the editor.
            // Note that we first need to extend the time axis to include
            // currently out of range events.
            scheduler.scrollResourceEventIntoView(resourceRecord, eventRecord, null, {
                animate        : true,
                edgeOffset     : 0,
                extendTimeAxis : false
            }).then(() => me.internalShowEditor(eventRecord, resourceRecord), () => scheduler.element.classList.remove('b-eventeditor-editing'));
        }
    }

    /**
     * Sets fields values from record being edited
     * @private
     */
    loadRecord(eventRecord, resourceRecord) {
        this.loadingRecord = true;

        this.internalLoadRecord(eventRecord, resourceRecord);

        this.loadingRecord = false;
    }

    internalLoadRecord(eventRecord, resourceRecord) {
        const
            me             = this,
            { eventStore } = me.client;

        me.eventRecord = eventRecord;
        me.resourceRecord = resourceRecord;

        me.editor.record = eventRecord;

        if (me.resourceField) {
            const resources = eventStore.getResourcesForEvent(eventRecord);

            // If this is an unassigned event, select the resource we've been provided
            if (!eventStore.storage.includes(eventRecord, true) && me.resourceRecord) {
                me.resourceField.value = me.resourceRecord[me.resourceField.valueField];
            }
            else if (me.scheduler.assignmentStore) {
                me.resourceField.value = resources.map((resource) => resource[me.resourceField.valueField]);
            }
        }

        super.internalLoadRecord(eventRecord, resourceRecord);
    }

    toggleEventType(eventType) {
        // expose eventType in dataset, for querying and styling
        this.editor.element.dataset.eventType = eventType || '';

        // toggle visibility of widgets belonging to eventTypes
        this.editor.eachWidget(widget =>
            widget.dataset && widget.dataset.eventType && (widget.hidden = widget.dataset.eventType !== eventType)
        );
    }

    //endregion

    //region Save

    // Override of EditBase get values, to include resourceId when not using AssignmentStore
    get values() {
        const values = super.values;

        // Use resourceId with single assignment mode
        if (!this.scheduler.assignmentStore && values.resource) {
            values.resourceId = values.resource.id;
            delete values.resource;
        }

        return values;
    }

    finalizeEventSave(eventRecord, resourceRecords, resolve, reject) {
        const
            me = this,
            { scheduler, eventStore } = me;

        me.onBeforeSave(eventRecord);

        eventRecord.beginBatch();
        me.updateRecord(eventRecord);
        eventRecord.endBatch();

        // Check if this is a new record
        if (eventStore && !eventRecord.stores.length) {
            /**
             * Fires on the owning Scheduler before an event is added
             * @event beforeEventAdd
             * @param {Scheduler.view.Scheduler} source The Scheduler instance.
             * @param {Scheduler.model.EventModel} eventRecord The record about to be added
             * @param {Scheduler.model.ResourceModel[]} resources **Deprecated** Use `resourceRecords` instead
             * @param {Scheduler.model.ResourceModel[]} resourceRecords Resources that the record is assigned to
             * @preventable
             */
            if (scheduler.trigger('beforeEventAdd', { eventRecord, resourceRecords, resources : resourceRecords }) !== false) {
                // Hand over the proxy element to be used by the new event
                if (me.dragProxyElement) {
                    const eventTpl = scheduler.generateTplData(eventRecord, resourceRecords[0]);

                    // Adopt the proxy as an event element.
                    if (eventTpl) {
                        DomHelper.syncClassList(me.dragProxyElement, eventTpl.wrapperCls);
                        DomHelper.createElement({
                            tag       : 'div',
                            parent    : me.dragProxyElement,
                            className : eventTpl.cls.toString()
                        });

                        // This is a signal that it's from a drag-create, so needs to stay
                        me.dragProxyElement.classList.add('b-sch-dragcreator-proxy');
                        me.dragProxyElement.id = eventTpl.id;
                        scheduler.isHorizontal && scheduler.currentOrientation.releaseTimeSpanDiv(me.dragProxyElement, true);
                        me.dragProxyElement = null;

                        // Flag to let HorizontalEventMapper know that it should reuse the element even though the
                        // event is new
                        eventRecord.instanceMeta(scheduler).fromDragProxy = true;
                    }
                }

                // Add to eventStore first, then assign the resource. Order is necessary since assigning might
                // involve an AssignmentStore
                me.eventStore.add(eventRecord);
                me.eventStore.assignEventToResource(eventRecord, resourceRecords);

                // If a filter was reapplied and filtered out the newly added event we need to clean up the drag proxy...
                if (!me.eventStore.includes(eventRecord)) {
                    // Feels a bit strange having that responsibility here, but since it is already handled
                    const proxyElement = scheduler.element.querySelector('.b-sch-dragcreator-proxy');

                    if (proxyElement) {
                        scheduler.currentOrientation.availableDivs.remove(proxyElement);
                        proxyElement.remove();
                    }
                }
            }
            else {
                resolve(false);
                return;
            }
        }
        else if (scheduler.assignmentStore) {
            me.eventStore.assignEventToResource(eventRecord, resourceRecords, true);
        }

        /**
         * Fires on the owning Scheduler after an event is successfully saved
         * @event afterEventSave
         * @param {Scheduler.view.Scheduler} source The scheduler instance
         * @param {Scheduler.model.EventModel} eventRecord The record about to be saved
         */
        scheduler.trigger('afterEventSave', { eventRecord });
        me.onAfterSave(eventRecord);

        resolve(eventRecord);
    }

    /**
     * Saves the changes (applies them to record if valid, if invalid editor stays open)
     * @private
     * @fires beforeEventSave
     * @fires beforeEventAdd
     * @fires afterEventSave
     * @returns {Promise}
     * @async
     */
    save() {
        return new Promise((resolve, reject) => {
            const
                me = this,
                { scheduler, eventRecord } = me;

            if (!eventRecord || !me.isValid) {
                resolve(false);
                return;
            }

            const
                { eventStore, values } = me,
                resourceRecords = me.resourceField && me.resourceField.records || [me.resourceRecord],
                resourceRecord  = resourceRecords[0];

            // Check for potential overlap scenarios before saving. TODO needs to be indicated in the UI
            if (!me.scheduler.allowOverlap && eventStore) {
                const abort = resourceRecords.some((resource) => {
                    return !eventStore.isDateRangeAvailable(values.startDate, values.endDate, eventRecord, resource);
                });

                if (abort) {
                    resolve(false);
                    return;
                }
            }

            const context = {
                finalize(saveEvent) {
                    try {
                        if (saveEvent !== false) {
                            me.finalizeEventSave(eventRecord, resourceRecords, resolve, reject);
                        }
                        else {
                            resolve(false);
                        }
                    }
                    catch (e) {
                        reject(e);
                    }
                }
            };

            /**
             * Fires on the owning Scheduler before an event is saved
             * @event beforeEventSave
             * @param {Scheduler.view.Scheduler} source The scheduler instance
             * @param {Scheduler.model.EventModel} eventRecord The record about to be saved
             * @param {Scheduler.model.ResourceModel} resourceRecord [DEPRECATED IN FAVOR OF `resourceRecords`] The resource to which the event is assigned
             * @param {Scheduler.model.ResourceModel[]} resourceRecords The resources to which the event is assigned
             * @param {Object} values The new values
             * @param {Object} context Extended save context:
             * @param {Boolean} [context.async] Set this to `true` in a listener to indicate that the listener will asynchronously decide to prevent or not the event save.
             * @param {Function} context.finalize Function to call to finalize the save. Used when `async` is `true`. Provide `false` to the function to prevent the save.
             * @preventable
             */
            if (scheduler.trigger('beforeEventSave', { eventRecord, resourceRecords, resourceRecord, values, context }) !== false) {
                context.finalize();
            }
            // truthy context.async means than a listener will decide to approve saving asynchronously
            else if (!context.async) {
                resolve();
            }
        });
    }

    //endregion

    //region Delete

    /**
     * Delete event being edited
     * @returns {Promise}
     * @fires beforeEventDelete
     * @private
     * @async
     */
    deleteEvent() {
        return new Promise((resolve, reject) => {
            const
                me = this,
                { eventRecord, editor } = me;

            me.scheduler.removeRecords([eventRecord], (removeRecord) => {
                // The reason it does it here is to move focus *before* it gets deleted,
                // and then there's code in the delete to see that it's deleting the focused one,
                // and jump forwards or backwards to move to the next or previous event
                // See 'Should allow key activation' test in tests/view/mixins/EventNavigation.t.js
                if (removeRecord && editor.containsFocus) {
                    editor.revertFocus();
                }

                resolve(removeRecord);
            });
        });
    }

    //endregion

    //region Stores

    get resourceStore() {
        return this._resourceStore;
    }

    set resourceStore(store) {
        this._resourceStore = store;
        if (this.resourceField) {
            this.resourceField.store = store;
        }
    }

    //endregion

    //region Events

    onActivateEditor({ eventRecord, resourceRecord }) {
        this.editEvent(eventRecord, resourceRecord);
    }

    onDragCreateEnd({ newEventRecord, resourceRecord, proxyElement }) {
        const me = this;

        if (!me.disabled) {
            // Call scheduler template method
            me.scheduler.onEventCreated(newEventRecord);

            // Clone proxy after showing editor so it's not deleted
            const dragProxyElement = proxyElement.cloneNode(true);
            dragProxyElement.removeAttribute('id');
            proxyElement.parentElement.appendChild(dragProxyElement);

            me.dragProxyElement = dragProxyElement;

            me.editEvent(newEventRecord, resourceRecord, dragProxyElement);
        }
    }

    // chained from EventNavigation
    onEventEnterKey({ assignmentRecord, eventRecord }) {
        if (assignmentRecord) {
            this.editEvent(eventRecord, assignmentRecord.resource);
        }
        else if (eventRecord) {
            this.editEvent(eventRecord, eventRecord.resource);
        }
    }

    // Toggle fields visibility when changing eventType
    onEventTypeChange({ value }) {
        this.toggleEventType(value);
    }

    //endregion

    //region Context menu

    getEventMenuItems({ eventRecord, resourceRecord, items }) {
        if (!this.scheduler.readOnly) {
            items.editEvent = {
                text   : this.L('Edit Event'),
                icon   : 'b-icon b-icon-edit',
                weight : -200,
                onItem : () => {
                    this.editEvent(eventRecord, resourceRecord);
                }
            };
        }
    }

    //endregion
}

GridFeatureManager.registerFeature(EventEdit, true, 'Scheduler');
