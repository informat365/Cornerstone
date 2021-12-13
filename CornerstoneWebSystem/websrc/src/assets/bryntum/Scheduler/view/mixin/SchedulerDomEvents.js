import Base from '../../../Core/Base.js';

/**
 * @module Scheduler/view/mixin/SchedulerDomEvents
 */

/**
 * Mixin that handles dom events (click etc) for scheduler and rendered events.
 *
 * @mixin
 */
export default Target => class SchedulerDomEvents extends (Target || Base) {
    //region Events

    /**
     * Triggered when user moves mouse over an empty area in the schedule.
     * @event scheduleMouseMove
     * @param {Scheduler.view.TimelineBase} source This TimelineBase
     * @param {Date} date Date at mouse position
     * @param {Date} tickStartDate The start date of the current time axis tick
     * @param {Date} tickEndDate The end date of the current time axis tick
     * @param {Grid.row.Row} row Clicked row (in horizontal mode only)
     * @param {Number} index Index of clicked resource
     * @param {Scheduler.model.ResourceModel} resourceRecord Resource record
     * @param {MouseEvent} event Browser event
     */

    /**
     * Triggered when user clicks an empty area in the schedule.
     * @event scheduleClick
     * @param {Scheduler.view.TimelineBase} source This TimelineBase
     * @param {Date} date Date at mouse position
     * @param {Date} tickStartDate The start date of the current time axis tick
     * @param {Date} tickEndDate The end date of the current time axis tick
     * @param {Grid.row.Row} row Clicked row (in horizontal mode only)
     * @param {Number} index Index of clicked resource
     * @param {Scheduler.model.ResourceModel} resourceRecord Resource record
     * @param {MouseEvent} event Browser event
     */

    /**
     * Triggered when user clicks an empty area in the schedule.
     * @event scheduleDblClick
     * @param {Scheduler.view.TimelineBase} source This TimelineBase
     * @param {Date} date Date at mouse position
     * @param {Date} tickStartDate The start date of the current time axis tick
     * @param {Date} tickEndDate The end date of the current time axis tick
     * @param {Grid.row.Row} row Double clicked row (in horizontal mode only)
     * @param {Number} index Index of double clicked resource
     * @param {Scheduler.model.ResourceModel} resourceRecord Resource record
     * @param {MouseEvent} event Browser event
     */

    /**
     * Triggered when user right clicks an empty area in the schedule.
     * @event scheduleContextMenu
     * @param {Scheduler.view.TimelineBase} source This TimelineBase
     * @param {Date} date Date at mouse position
     * @param {Date} tickStartDate The start date of the current time axis tick
     * @param {Date} tickEndDate The end date of the current time axis tick
     * @param {Grid.row.Row} row Clicked row (in horizontal mode only)
     * @param {Number} index Index of clicked resource
     * @param {Scheduler.model.ResourceModel} resourceRecord Resource record
     * @param {MouseEvent} event Browser event
     */

    /**
     * Triggered for mouse down on an event.
     * @event eventMouseDown
     * @param {Scheduler.view.Scheduler} source This Scheduler
     * @param {Scheduler.model.EventModel} eventRecord Event record
     * @param {MouseEvent} event Browser event
     */

    /**
     * Triggered for mouse up on an event.
     * @event eventMouseUp
     * @param {Scheduler.view.Scheduler} source This Scheduler
     * @param {Scheduler.model.EventModel} eventRecord Event record
     * @param {MouseEvent} event Browser event
     */

    /**
     * Triggered for click on an event.
     * @event eventClick
     * @param {Scheduler.view.Scheduler} source This Scheduler
     * @param {Scheduler.model.EventModel} eventRecord Event record
     * @param {MouseEvent} event Browser event
     */

    /**
     * Triggered for double click on an event.
     * @event eventDblClick
     * @param {Scheduler.view.Scheduler} source This Scheduler
     * @param {Scheduler.model.EventModel} eventRecord Event record
     * @param {MouseEvent} event Browser event
     */

    /**
     * Triggered for right click on an event.
     * @event eventContextMenu
     * @param {Scheduler.view.Scheduler} source This Scheduler
     * @param {Scheduler.model.EventModel} eventRecord Event record
     * @param {MouseEvent} event Browser event
     */

    /**
     * Triggered for mouse over on an event.
     * @event eventMouseOver
     * @param {Scheduler.view.Scheduler} source This Scheduler
     * @param {Scheduler.model.EventModel} eventRecord Event record
     * @param {MouseEvent} event Browser event
     */

    /**
     * Triggered for mouse out from an event.
     * @event eventMouseOut
     * @param {Scheduler.view.Scheduler} source This Scheduler
     * @param {Scheduler.model.EventModel} eventRecord Event record
     * @param {MouseEvent} event Browser event
     */

    //endregion

    //region Event handling

    getTimeSpanMouseEventParams(eventElement, event) {
        return {
            eventRecord      : this.resolveEventRecord(eventElement),
            resourceRecord   : this.resolveResourceRecord(eventElement),
            assignmentRecord : this.resolveAssignmentRecord(eventElement),
            eventElement,
            event
        };
    }

    getScheduleMouseEventParams(cellData, event) {
        const resourceRecord = this.isVertical ? this.resolveResourceRecord(event) : this.store.getById(cellData.id);

        return {
            resourceRecord
        };
    }

    /**
     * Relays keydown events as eventkeydown if we have a selected task.
     * @private
     */
    onElementKeyDown(event) {
        super.onElementKeyDown(event);

        const me = this;

        if (me.selectedEvents.length) {
            me.trigger(me.scheduledEventName + 'KeyDown', { eventRecord : me.selectedEvents });
        }
    }

    /**
     * Relays keyup events as eventkeyup if we have a selected task.
     * @private
     */
    onElementKeyUp(event) {
        super.onElementKeyUp(event);

        const me = this;

        if (me.selectedEvents.length) {
            me.trigger(me.scheduledEventName + 'KeyUp', { eventRecord : me.selectedEvents });
        }
    }

    //endregion

    // This does not need a className on Widgets.
    // Each *Class* which doesn't need 'b-' + constructor.name.toLowerCase() automatically adding
    // to the Widget it's mixed in to should implement thus.
    get widgetClass() {}
};
