import Popup from '../../Core/widget/Popup.js';

/**
 * @module Scheduler/view/EventEditor
 */

/**
 * Provided event editor dialog.
 *
 * @extends Core/widget/Popup
 * @private
 */
export default class EventEditor extends Popup {

    static get $name() {
        return 'EventEditor';
    }

    static get defaultConfig() {
        return {
            items     : [],
            draggable : {
                handleSelector : ':not(button,.b-field-inner)' // blacklist buttons and field inners
            },
            axisLock : 'flexible'
        };
    }

    processWidgetConfig(widget) {
        const me               = this,
            eventEditFeature = me.eventEditFeature;

        if (widget.type === 'extraItems') {
            return false;
        }

        let fieldConfig = {};

        if (widget.ref === 'resourceField') {
            if (!eventEditFeature.showResourceField) return false;

            // Can't use store directly since it may be grouped and then contains irrelevant group records
            me.resourceStore = widget.store = new eventEditFeature.resourceStore.constructor({
                chained         : true,
                chainAllRecords : true, // By default it chains processedRecords, leading to only "expanded" resources showing up in editor
                masterStore     : eventEditFeature.resourceStore,
                chainedFilterFn : record => !record.meta.specialRow
            });

            widget.multiSelect = Boolean(eventEditFeature.scheduler.assignmentStore);

            if (eventEditFeature.resourceFieldConfig) fieldConfig = eventEditFeature.resourceFieldConfig;
        }

        if (widget.ref === 'nameField' && !eventEditFeature.showNameField) return false;

        if (widget.ref === 'deleteButton' && !eventEditFeature.showDeleteButton) return false;

        if ((widget.name === 'startDate' || widget.name === 'endDate') && widget.type === 'date') {
            fieldConfig.format = eventEditFeature.dateFormat;
        }

        if ((widget.name === 'startDate' || widget.name === 'endDate') && widget.type === 'time') {
            fieldConfig.format = eventEditFeature.timeFormat;
        }

        if (eventEditFeature.startDateConfig && widget.name === 'startDate' && widget.type === 'date') {
            fieldConfig = eventEditFeature.startDateConfig;
        }

        if (eventEditFeature.startTimeConfig && widget.name === 'startDate' && widget.type === 'time') {
            fieldConfig = eventEditFeature.startTimeConfig;
        }

        if (eventEditFeature.endDateConfig && widget.name === 'endDate' && widget.type === 'date') {
            fieldConfig = eventEditFeature.endDateConfig;
        }

        if (eventEditFeature.endTimeConfig && widget.name === 'endDate' && widget.type === 'time') {
            fieldConfig = eventEditFeature.endTimeConfig;
        }

        Object.assign(widget, fieldConfig);

        return super.processWidgetConfig(widget);
    }

    show(...args) {
        // Updated chained store. It is not done automatically for grouping/trees.
        if (this.resourceStore) {
            this.resourceStore.fillFromMaster();
        }

        super.show(...args);
    }

    afterShow(...args) {
        const deleteButton = this.widgetMap.deleteButton;

        // Only show delete button if the event belongs to a store
        if (deleteButton) {
            deleteButton.hidden = this.readOnly || !this.record.stores.length;
        }
        super.afterShow(...args);
    }

    onInternalKeyDown(event) {
        this.trigger('keyDown', { event });
        super.onInternalKeyDown(event);
    }

    set readOnly(readOnly) {
        const { deleteButton, saveButton, cancelButton } = this.widgetMap;

        this._readOnly = readOnly;

        this.element.classList[readOnly ? 'add' : 'remove']('b-readonly');

        this.eachWidget(widget => {
            widget.readOnly = readOnly;
        });

        if (deleteButton) {
            deleteButton.hidden = readOnly;
        }

        if (saveButton) {
            saveButton.hidden = readOnly;
        }

        if (cancelButton) {
            cancelButton.hidden = readOnly;
        }
    }

    get readOnly() {
        return this._readOnly;
    }
}
