import Base from '../../../Core/Base.js';
import DomHelper from '../../../Core/helper/DomHelper.js';
import StringHelper from '../../../Core/helper/StringHelper.js';
import EventHelper from '../../../Core/helper/EventHelper.js';
import DomDataStore from '../../../Core/data/DomDataStore.js';

/**
 * @module Scheduler/view/mixin/TimelineDomEvents
 */

const eventNameMap = {
    mousedown   : 'MouseDown',
    mouseup     : 'MouseUp',
    click       : 'Click',
    dblclick    : 'DblClick',
    contextmenu : 'ContextMenu',
    mouseover   : 'MouseOver',
    mouseout    : 'MouseOut'
};

/**
 * Mixin that handles dom events (click etc) for scheduler and rendered events.
 *
 * @mixin
 */
export default Target => class TimelineDomEvents extends (Target || Base) {
    //region Default config

    static get defaultConfig() {
        return {
            // TODO: PORT longpress missing
            scheduledBarEvents : {
                mousedown   : 'handleScheduledBarEvent',
                mouseup     : 'handleScheduledBarEvent',
                click       : 'handleScheduledBarEvent',
                dblclick    : 'handleScheduledBarEvent',
                contextmenu : 'handleScheduledBarEvent',
                mouseover   : 'handleScheduledBarEvent',
                mouseout    : 'handleScheduledBarEvent'
            },

            // TODO: PORT pinch, pinchstart, pinchend missing
            schedulerEvents : {
                click       : 'handleScheduleEvent',
                dblclick    : 'handleScheduleEvent',
                contextmenu : 'handleScheduleEvent',
                mousemove   : 'handleScheduleEvent'
            }
        };
    }

    //endregion

    //region Init

    /**
     * Adds listeners for DOM events for the scheduler and its events.
     * Which events is specified in Scheduler#scheduledBarEvents and Scheduler#schedulerEvents.
     * @private
     */
    initDomEvents() {
        const me = this;

        // Set thisObj and element of the configured listener specs.
        me.scheduledBarEvents.element = me.schedulerEvents.element = me.timeAxisSubGridElement;
        me.scheduledBarEvents.thisObj = me.schedulerEvents.thisObj = me;

        // same listener used for different events
        EventHelper.on(me.scheduledBarEvents);
        EventHelper.on(me.schedulerEvents);
    }

    //endregion

    //region Event handling

    getTimeSpanMouseEventParams(eventElement, event) {
        throw new Error('Implement in subclass');
    }

    getScheduleMouseEventParams(cellData, event) {
        throw new Error('Implement in subclass');
    }

    /**
     * Wraps dom Events for rendered scheduler EventModels and fires prefixed as our events.
     * For example click -> eventclick
     * @private
     * @param event
     */
    handleScheduledBarEvent(event) {
        const me           = this,
            eventElement = DomHelper.up(event.target, me.eventSelector),
            eventName    = eventNameMap[event.type] || StringHelper.capitalizeFirstLetter(event.type);

        if (eventElement) {
            me.trigger(me.scheduledEventName + eventName, me.getTimeSpanMouseEventParams(eventElement, event));
        }
    }

    /**
     * Wraps dom Events for the scheduler and fires as our events.
     * For example click -> scheduleClick
     * @private
     * @param event
     */
    handleScheduleEvent(event) {
        const
            me           = this,
            eventElement = DomHelper.up(event.target, me.eventSelector),
            cellElement  = !eventElement && DomHelper.up(event.target, '.' + me.timeCellCls),
            eventName    = eventNameMap[event.type] || StringHelper.capitalizeFirstLetter(event.type);

        if (cellElement) {
            const
                clickedDate = me.getDateFromDomEvent(event, 'floor'),
                cellData    = DomDataStore.get(cellElement),
                index       = cellData.row.dataIndex,
                tickIndex   = me.timeAxis.getTickFromDate(clickedDate),
                tick        = me.timeAxis.getAt(Math.floor(tickIndex));

            if (tick) {
                me.trigger('schedule' + eventName, Object.assign({
                    date          : clickedDate,
                    tickStartDate : tick.startDate,
                    tickEndDate   : tick.endDate,
                    row           : cellData.row,
                    index,
                    event
                }, me.getScheduleMouseEventParams(cellData, event)));
            }
        }
    }

    /**
     * Relays mouseover events as eventmouseenter if over rendered event.
     * Also adds Scheduler#overScheduledEventClass to the hovered element.
     * @private
     */
    onElementMouseOver(event) {
        super.onElementMouseOver(event);

        const me           = this,
            target       = event.target;

        // We must be over the event bar
        if (target.closest(me.eventInnerSelector)) {
            const eventElement = target.closest(me.eventSelector);

            eventElement.classList.add(me.overScheduledEventClass);

            if (eventElement !== me.hoveredEventNode && !me.preventOverCls) {
                me.hoveredEventNode = eventElement;

                const params = me.getTimeSpanMouseEventParams(eventElement, event);
                if (params) {
                    // do not fire this event if model cannot be found
                    // this can be the case for "b-sch-dragcreator-proxy" elements for example
                    me.trigger(me.scheduledEventName + 'MouseEnter', params);
                }
            }
        }
        else {
            me.hoveredEventNode = null;
        }
    }

    /**
     * Relays mouseout events as eventmouseleave if out from rendered event.
     * Also removes Scheduler#overScheduledEventClass from the hovered element.
     * @private
     */
    onElementMouseOut(event) {
        super.onElementMouseOut(event);

        const me = this;

        // We must be over the event bar
        if (event.target.closest(me.eventInnerSelector) && me.resolveTimeSpanRecord(event.target) && me.hoveredEventNode) {
            // out to child shouldn't count...
            if (event.relatedTarget && DomHelper.isDescendant(event.target.closest(me.eventInnerSelector), event.relatedTarget)) return;

            me.unhover(event);
        }
    }

    unhover(event) {
        const me           = this,
            eventElement = me.hoveredEventNode;
        if (eventElement) {
            eventElement.classList.remove(me.overScheduledEventClass);
            me.trigger(me.scheduledEventName + 'MouseLeave', me.getTimeSpanMouseEventParams(eventElement, event));
            me.hoveredEventNode = null;
        }
    }

    //endregion

    // This does not need a className on Widgets.
    // Each *Class* which doesn't need 'b-' + constructor.name.toLowerCase() automatically adding
    // to the Widget it's mixed in to should implement thus.
    get widgetClass() {}
};
