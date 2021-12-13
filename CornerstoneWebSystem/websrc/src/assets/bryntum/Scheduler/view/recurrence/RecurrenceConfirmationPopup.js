import BryntumWidgetAdapterRegister from '../../../Core/adapter/widget/util/BryntumWidgetAdapterRegister.js';
import Popup from '../../../Core/widget/Popup.js';

/**
 * @module Scheduler/view/recurrence/RecurrenceConfirmationPopup
 */

/**
 * Confirmation dialog showing up before modifying a recurring event or some of its occurrences.
 * For recurring events the dialog notifies user that the event change/removal will cause all its occurrences
 * change/removal and asks to confirm the action.
 *
 * And for occurrences the dialog allows to choose if user wants to affect all further occurrences, this occurrence only or cancel the change.
 *
 * Usage example:
 *
 * ```javascript
 * const confirmation = new RecurrenceConfirmationPopup();
 *
 * confirmation.confirm({
 *     eventRecord : recurringEvent,
 *     actionType  : "delete",
 *     changerFn   : () => recurringEvent.remove(event)
 * });
 * ```
 *
 * @classType recurrenceconfirmation
 * @extends Core/widget/Popup
 */
export default class RecurrenceConfirmationPopup extends Popup {

    static get $name() {
        return 'RecurrenceConfirmationPopup';
    }

    static get defaultConfig() {
        return {
            localizableProperties : [],
            align                 : 'b-t',
            autoShow              : false,
            autoClose             : false,
            centered              : true,
            scrollAction          : 'realign',
            constrainTo           : window,
            draggable             : true,
            closable              : true,
            floating              : true,
            eventRecord           : null,
            cls                   : 'b-sch-recurrenceconfirmation',
            bbar                  : [
                /**
                 * Reference to the "apply changes to multiple occurrences" button, if used
                 * @member {Core.widget.Button} changeMultipleButton
                 * @readonly
                 */
                {
                    color       : 'b-green',
                    localeClass : this,
                    text        : 'L{Yes}',
                    ref         : 'changeMultipleButton'
                },
                /**
                 * Reference to the button that causes changing of the event itself only, if used
                 * @member {Core.widget.Button} changeSingleButton
                 * @readonly
                 */
                {
                    color       : 'b-gray',
                    localeClass : this,
                    text        : 'L{update-only-this-btn-text}',
                    ref         : 'changeSingleButton'
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
        };
    }

    construct(...args) {
        const me = this;

        super.construct(...args);

        const { changeMultipleButton, changeSingleButton, cancelButton } = me.widgetMap;

        changeMultipleButton && changeMultipleButton.on('click', me.onChangeMultipleButtonClick, me);
        changeSingleButton && changeSingleButton.on('click', me.onChangeSingleButtonClick, me);
        cancelButton && cancelButton.on('click', me.onCancelButtonClick, me);

        me.on('toolclick', me.onToolClick, me);
    }

    onChangeMultipleButtonClick() {
        this.processMultipleRecords();
        this.close();
    }

    onChangeSingleButtonClick() {
        this.processSingleRecord();
        this.close();
    }

    onCancelButtonClick() {
        this.cancelFn && this.cancelFn.call(this.thisObj);
        this.close();
    }

    onToolClick({ tool }) {
        if (tool.handler === 'close' && this.cancelFn) {
            this.cancelFn.call(this.thisObj);
        }
    }

    /**
     * Displays the confirmation.
     * Example usage:
     *
     * ```javascript
     * const popup = new RecurrenceConfirmationPopup();
     *
     * popup.confirm({
     *     eventRecord,
     *     actionType : "delete",
     *     changerFn  : () => eventStore.remove(record)
     * });
     * ```
     *
     * @param {Object}                     config               The following config options are supported:
     * @param {Scheduler.model.EventModel} config.eventRecord   Event being modified.
     * @param {String}                     config.actionType    Type of modification to be applied to the event. Can be either "update" or "delete".
     * @param {Function}                   config.changerFn     A function that should be called to apply the change to the event upon user choice.
     * @param {Function}                   [config.thisObj]     `changerFn` and `cancelFn` functions scope.
     * @param {Function}                   [config.cancelFn]    Function called on `Cancel` button click.
     */
    confirm(config = {}) {
        const me = this;

        //<debug>
        if (!config || !config.actionType || !config.eventRecord) {
            throw new Error('actionType and eventRecord must be specified for Scheduler.view.recurrence.RecurrenceConfirmationPopup');
        }
        //</debug>

        [
            'actionType',
            'eventRecord',
            'title',
            'html',
            'changerFn',
            'cancelFn',
            'thisObj'
        ].forEach(prop => {
            if (prop in config) me[prop] = config[prop];
        });

        me.updatePopupContent();

        return super.show(config);
    }

    updatePopupContent() {
        const
            me = this,
            { changeMultipleButton, changeSingleButton, cancelButton } = me.widgetMap;

        let { eventRecord, actionType = 'update' } = me,
            isMaster = eventRecord && eventRecord.isRecurring;

        // the following lines are added to satisfy the 904_unused localization test
        // to let it know that these locales are used:
        // me.L('delete-further-message')
        // me.L('update-further-message')
        // me.L('delete-all-message')
        // me.L('update-all-message')
        // me.L('delete-further-btn-text')
        // me.L('update-further-btn-text')
        // me.L('delete-only-this-btn-text')
        // me.L('update-only-this-btn-text')

        if (isMaster) {
            changeMultipleButton.text = me.L('Yes');
            me.html = me.L(`${actionType}-all-message`);
        }
        else {
            changeMultipleButton.text = me.L(`${actionType}-further-btn-text`);
            me.html = me.L(`${actionType}-further-message`);
        }

        changeSingleButton.text = me.L(`${actionType}-only-this-btn-text`);
        cancelButton.text       = me.L('Cancel');

        // TODO: so far we hide 'Only this event' option for a recurring event itself until this case is supported
        if (isMaster) {
            changeSingleButton.hide();
        }
        else {
            changeSingleButton.show();
        }

        me.width = me.L('width');

        // the following lines are added to satisfy the 904_unused localization test
        // to let it know that these locales are used:
        // this.L('delete-title') not found
        // this.L('update-title') not found
        me.title = me.L(`${actionType}-title`);
    }

    processMultipleRecords() {
        const
            { eventRecord, changerFn, thisObj } = this,
            { recurringEvent }                  = eventRecord,
            stopDate                            = new Date(eventRecord.startDate - 1);

        eventRecord.beginBatch();

        // apply changes to the occurrence
        changerFn.call(thisObj, eventRecord);

        // reset occurrence linkage to the "master" event
        eventRecord.recurringTimeSpanId = null;

        eventRecord.endBatch();

        // stop the previous recurrence
        if (recurringEvent) {
            recurringEvent.recurrence.endDate = stopDate;
        }
    }

    processSingleRecord() {
        const
            { eventRecord, changerFn, thisObj } = this,
            { recurringEvent, startDate }  = eventRecord;

        eventRecord.beginBatch();

        changerFn.call(thisObj);

        eventRecord.recurrence = null;

        eventRecord.endBatch();

        recurringEvent.addExceptionDate(startDate);
    }

    updateLocalization() {
        this.updatePopupContent();
        super.updateLocalization();
    }

};

BryntumWidgetAdapterRegister.register('recurrenceconfirmation', RecurrenceConfirmationPopup);
