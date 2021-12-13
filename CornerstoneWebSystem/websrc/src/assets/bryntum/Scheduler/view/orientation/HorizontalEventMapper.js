import BaseHorizontalMapper from './BaseHorizontalMapper.js';
import HorizontalEventMapperCache from './HorizontalEventMapperCache.js';
import Rectangle from '../../../Core/helper/util/Rectangle.js';
import DateHelper from '../../../Core/helper/DateHelper.js';
import DomHelper from '../../../Core/helper/DomHelper.js';
import IdHelper from '../../../Core/helper/IdHelper.js';

/**
 * @module Scheduler/view/orientation/HorizontalEventMapper
 */

const getStartEnd = (view, timeAxis, date, dateMS, useEnd) => {
    if (view.fillTicks) {
        const tickIndex = Math.floor(timeAxis.getTickFromDate(date)),
            tick      = timeAxis.getAt(tickIndex);

        if (tick) {
            return tick[useEnd ? 'endDate' : 'startDate'].getTime();
        }
    }

    return dateMS;
};

/**
 * Handles rendering of events when scheduler uses horizontal mode. The need to interact with this class should be
 * minimal, most functions are called from Scheduler or its mixins.
 * @private
 */
export default class HorizontalEventMapper extends BaseHorizontalMapper {
    //region Init

    construct(scheduler) {
        this.scheduler = scheduler;

        super.construct(scheduler);

        this.cache = new HorizontalEventMapperCache(this);
    }

    init() {
        const me = this;

        super.init();

        this.scheduler.on({
            togglegroup : me.onToggleGroup,
            rowremove   : me.onRowRemove,
            prio        : 2,
            thisObj     : me
        });
    }

    //endregion

    //region Div reusage

    // called from cache when removing events
    clearDiv(div, remove, remainVisible) {
        const me = this,
            divStyle = div.style;

        if (!remove || remainVisible) {
            me.releaseTimeSpanDiv(div, remainVisible);
        }
        // For example when adding events we remove all existing immediately to not have the new event transition into
        // place by from reusing an existing element. Also want to remove right away when not using event animations.
        else if (remove === 'immediate' || !me.scheduler.enableEventAnimations) {
            div.remove();
        }
        else {
            // The div doesn't get removed until the opacity transition has done.
            // Avoid id collisions if the event is rerendered immediately.
            // Can't set it to "" because of test requirements.
            div.id = IdHelper.generateId('obsolete-event');
            divStyle.opacity = 0;
            divStyle.pointerEvents = 'none';
            me.setTimeout(() => {
                div.remove();
                divStyle.opacity = 1;
                divStyle.pointerEvents = '';
            }, 200);
        }
    }

    // called from cache when removing events
    clearAllDivs() {
        const me = this;
        me.availableDivs.forEach(div => div.remove());
        me.availableDivs.clear();
    }

    clearEvents() {
        this.cache.clear(true);
    }

    /**
     * Releases elements for events no longer in view.
     */
    releaseEvent(rowId, timeSpanElementId, remainVisible = false) {
        const me         = this,
            timeSpanData = me.cache.getRenderedTimeSpan(rowId, timeSpanElementId),
            cache        = timeSpanData && me.cache.getTimeSpan(timeSpanData.eventId, rowId);

        if (cache && cache.div) {
            // release div. remember for reuse, if such mode is used
            me.releaseTimeSpanDiv(cache.div, remainVisible);
            cache.div = cache.eventEl = null;
        }
        return me.cache.clearRenderedTimeSpan(rowId, timeSpanElementId);
    }
    //endregion

    //region Elements

    getElementFromEventRecord(eventRecord, resourceRecord = this.scheduler.eventStore.getResourcesForEvent(eventRecord)[0]) {
        if (resourceRecord) {
            const renderedRowEvents = this.cache.getRenderedEvents(resourceRecord.id);

            if (renderedRowEvents) {
                const layoutId = this.scheduler.getEventRenderId(eventRecord, resourceRecord),
                    layout = renderedRowEvents[layoutId],
                    layoutCache = layout && layout.layoutCache;

                return layoutCache && layoutCache.eventEl;
            }
        }
    }

    getElementsFromEventRecord(eventRecord, resourceRecord) {
        const me = this;

        // Single event instance, as array
        if (resourceRecord) {
            return [me.getElementFromEventRecord(eventRecord, resourceRecord)];
        }
        // All instances
        else {
            return me.scheduler.eventStore.getResourcesForEvent(eventRecord).reduce((result, resourceRecord) => {
                const el = me.getElementFromEventRecord(eventRecord, resourceRecord);
                if (el) {
                    result.push(el);
                }
                return result;
            }, []);
        }
    }

    resolveRowRecord(elementOrEvent) {
        const
            me        = this,
            { view }  = me,
            element   = elementOrEvent instanceof Event ? elementOrEvent.target : elementOrEvent,
            // Fix for FF on Linux having text nodes as event.target
            el        = element.nodeType === 3 ? element.parentElement : element,
            eventNode = el.matches(view.eventSelector) && el || DomHelper.up(el, view.eventSelector);

        if (eventNode) {
            return view.getResourceRecordFromDomId(eventNode.id);
        }

        return view.getRecordFromElement(el);
    }

    toggleCls(eventRecord, resourceRecord, cls, add = true) {
        if (!resourceRecord) {
            return;
        }

        const
            eventLayout = this.cache.getRenderedTimeSpan(
                resourceRecord.id,
                this.scheduler.getEventRenderId(eventRecord, resourceRecord)
            ),
            element = this.getElementFromEventRecord(eventRecord, resourceRecord);

        if (eventLayout) {
            eventLayout.cls[cls] = add ? 1 : 0;
        }

        if (element) {
            element.classList[add ? 'add' : 'remove'](cls);
        }
    }

    //endregion

    //region Region

    getResourceEventBox(eventId, resourceId) {
        const cached = this.cache.getTimeSpan(eventId, resourceId);
        if (cached) {
            return cached;
        }
    }

    /**
     * Gets the region, relative to the page, represented by the schedule and optionally only for a single resource. This method will call getDateConstraints to
     * allow for additional resource/event based constraints. By overriding that method you can constrain events differently for
     * different resources.
     * @param {Core.data.Model} rowRecord (optional) The row record
     * @param {Scheduler.model.EventModel} eventRecord (optional) The event record
     * @return {Object} The region of the schedule
     */
    getScheduleRegion(rowRecord, eventRecord, local = true) {
        let me     = this,
            view   = me.view,
            column = me.column,
            region;

        if (rowRecord) {
            const eventElement = eventRecord && me.getElementsFromEventRecord(eventRecord, rowRecord)[0];

            region = Rectangle.from(view.getRowById(rowRecord.id).getElement('locked'));

            if (eventElement) {
                const eventRegion = Rectangle.from(eventElement, column.subGridElement);

                region.y = eventRegion.y;
                region.bottom = eventRegion.bottom;
            }
            else {
                region.y = region.y + view.resourceMargin;
                region.bottom = region.bottom - view.resourceMargin;
            }
        }
        else {
            // TODO: This is what the bizarre function that was removed here did.
            // The coordinate space needs to be sorted out here!
            region = Rectangle.from(column.subGridElement).moveTo(null, 0);
            region.width = column.subGridElement.scrollWidth;

            region.y = region.y + view.resourceMargin;
            region.bottom = region.bottom - view.resourceMargin;
        }

        let taStart         = me.timeAxis.startDate,
            taEnd           = me.timeAxis.endDate,
            dateConstraints = view.getDateConstraints(rowRecord, eventRecord) || {
                start : taStart,
                end   : taEnd
            },
            startX          = view.getCoordinateFromDate(DateHelper.max(taStart, dateConstraints.start)),
            //startX          = this.translateToPageCoordinate(scheduler.getCoordinateFromDate(DateHelper.max(taStart, dateConstraints.start))),
            endX            = view.getCoordinateFromDate(DateHelper.min(taEnd, dateConstraints.end)),
            //endX            = this.translateToPageCoordinate(scheduler.getCoordinateFromDate(DateHelper.min(taEnd, dateConstraints.end))),
            top             = region.y,
            bottom          = region.bottom;

        if (!local) {
            startX = me.translateToPageCoordinate(startX);
            endX = me.translateToPageCoordinate(endX);
        }

        return { top, right : Math.max(startX, endX), bottom, left : Math.min(startX, endX) };
    }

    getResoureEventBox(eventId, resourceId) {
        return this.cache.getTimeSpan(eventId, resourceId);
    }

    //endregion

    //region Layout & render

    //region Stack & pack

    layoutEventVerticallyStack(bandIndex) {
        const { resourceMargin, rowHeight, barMargin } = this.scheduler;
        return bandIndex === 0
            ? resourceMargin
            : resourceMargin + bandIndex * (rowHeight - resourceMargin * 2) + bandIndex * barMargin;
    }

    layoutEventVerticallyPack(topFraction, heightFraction) {
        const
            { resourceMargin, rowHeight, barMargin } = this.scheduler,
            // TODO reduce grid row borders when available, https://app.assembla.com/spaces/bryntum/tickets/5840-measure-grid-row-border-at-first-render/details#
            availableHeight = rowHeight - (2 * resourceMargin),
            count           = 1 / heightFraction,
            bandIndex       = topFraction * count, // "y" within row
            height          = (availableHeight - ((count - 1) * barMargin)) * heightFraction,
            top             = resourceMargin + bandIndex * height + bandIndex * barMargin;

        return {
            top, height
        };
    }

    //endregion

    /**
     * Converts a start/endDate into a MS value used when rendering the event. If scheduler is configured with
     * `fillTicks: true` the value returned will be snapped to tick start/end.
     * @private
     * @param {Scheduler.model.EventModel} eventRecord
     * @returns {Object} Object of format { startMS, endMS, durationMS }
     */
    calculateMS(eventRecord) {
        const me   = this,
            view = me.view;

        let startMS    = getStartEnd(view, me.timeAxis, eventRecord.startDate, eventRecord.startDateMS, false),
            endMS      = getStartEnd(view, me.timeAxis, eventRecord.endDate, eventRecord.endDateMS, true),
            durationMS = endMS - startMS;

        if (view.milestoneLayoutMode !== 'default' && durationMS === 0) {
            const pxPerMinute = me.timeAxisViewModel.getSingleUnitInPixels('minute'),
                lengthInPx  = view.getMilestoneLabelWidth(eventRecord),
                duration    = lengthInPx * (1 / pxPerMinute);

            durationMS = duration * 60 * 1000;

            switch (view.milestoneAlign) {
                case 'start':
                case 'left':
                    endMS = startMS + durationMS;
                    break;
                case 'end':
                case 'right':
                    endMS = startMS;
                    startMS = endMS - durationMS;
                    break;
                default: // using center as default
                    endMS = startMS + durationMS / 2;
                    startMS = endMS - durationMS;
                    break;
            }
        }

        return {
            startMS,
            endMS,
            durationMS
        };
    }

    /**
     * Layouts events on a row, caching on each event and the entire result on the resource
     * @private
     * @param {Scheduler.view.Scheduler} scheduler
     * @param {Scheduler.model.ResourceModel} resource
     * @param {Grid.row.Row} row
     * @returns {Boolean} Returns false if no events on row, otherwise true
     */
    layoutEvents(scheduler, resource, row) {
        const
            me             = this,
            { timeAxis }   = me,
            { eventStore } = scheduler,
            resourceId     = resource.id,
            resourceEvents = eventStore.getEventsForResource(resourceId),
            // When using an AssignmentStore we will get all events for the resource even if the EventStore is filtered,
            // handle this be excluding "invisible" events here
            availableEvents = eventStore.isFiltered ? resourceEvents.filter(eventRecord =>
                eventStore.indexOf(eventRecord) > -1
            ) : resourceEvents,
            // Call a chainable template function on scheduler to allow features to add additional "events" to render
            // Currently used by ResourceZones
            allEvents     = scheduler.getEventsToRender(resource, availableEvents) || [],
            events        = allEvents.filter(e => timeAxis.isTimeSpanInAxis(e)),
            // Generate layout data for events belonging to current row which are within the TimeAxis
            eventsTplData = events.reduce((result, event) => {
                const eventBox = scheduler.generateTplData(event, resource);

                // Collect layouts of visible events
                if (eventBox) {
                    result.push(eventBox);
                }
                // Release events that are no longer visible
                else if (me.cache.getRenderedEvents(resourceId)) {
                    me.releaseEvent(resourceId, scheduler.getEventRenderId(event, resource));
                }

                return result;
            }, []);

        // If there are no events within the rendering zone, clear the resource layout cache.
        // Return false to indicate that there's nothing to render.
        if (!eventsTplData.length) {
            me.cache.clearRenderedEvents(resourceId);
            return false;
        }

        // Ensure the events are rendered in natural order so that navigation works.
        eventsTplData.sort(scheduler.horizontalEventSorterFn || me.eventSorter);

        let rowHeight = scheduler.rowHeight;

        const
            layout = scheduler.currentEventLayout,
            // Only events and tasks should be considered during layout (not resource time ranges if any)
            layoutEventData = eventsTplData.filter(d => d.event.isEvent || d.event.isTask);

        // Event data is now gathered, calculate layout properties for each event
        if (scheduler.eventLayout === 'stack') {
            const
                nbrOfBandsRequired = layout.applyLayout(layoutEventData, resource),
                heightPerEvent     = scheduler.rowHeight - scheduler.resourceMargin * 2;

            rowHeight = (nbrOfBandsRequired * heightPerEvent) + ((nbrOfBandsRequired - 1) * scheduler.barMargin) + scheduler.resourceMargin * 2;
        }
        else if (scheduler.eventLayout === 'pack') {
            layout.applyLayout(layoutEventData, resource);
        }

        let absoluteTop = row.top;

        // adjust row top, when it is rendered on top (since in that case top is not known until height is set)
        if (rowHeight !== row.height && row.estimatedTop) {
            absoluteTop = row.top + row.height - rowHeight;
        }

        const cache = {
            _allEvents : eventsTplData,
            _rowHeight : rowHeight
        };

        // cache boxes
        eventsTplData.forEach(data => {
            const layout      = me.cache.getTimeSpan(data.eventId, resourceId),
                relativeTop = data.top;

            data.top += absoluteTop;
            data.rowTop = absoluteTop;

            // ResourceTimeRanges sets this flag to fill the entire row (can't use 100% since it is not actually in the row).
            // Also cant set it in ResourceTimeRanges#onEventDataGenerated since it is called prior to laying out
            if (data.fillSize) {
                data.height = rowHeight;
            }

            // TODO: Include directly in data instead, to simplify cache a wee bit
            // cache layout to not have to recalculate every time
            data.layoutCache = {
                layout         : true,
                // reuse div if already assigned (for example when resizing an event)
                div            : layout && layout.div,
                eventEl        : layout && layout.eventEl,
                width          : data.width,
                height         : data.height,
                start          : data.left,
                end            : data.left + data.width,
                relativeTop    : relativeTop,
                top            : data.top,
                relativeBottom : relativeTop + data.height,
                bottom         : data.top + data.height
            };

            cache[data.eventId] = data;
        });

        me.cache.addRow(resourceId, cache);
        //me.resourceLayoutCache[resourceId] = eventsTplData;

        return true;
    }

    // Earlier start dates are above later tasks
    // If same start date, longer tasks float to top
    // If same start + duration, sort by name
    eventSorter(a, b) {
        const
            startA    = a.dataStartMs || a.startMs, // dataXX are used if configured with fillTicks
            endA      = a.dataEndMs || a.endMs,
            startB    = b.dataStartMs || b.startMs,
            endB      = b.dataEndMs || b.endMs,
            sameStart = (startA === startB);

        if (sameStart) {
            if (endA - endB === 0) {
                return a.event.name < b.event.name ? -1 : 1;
            }
            return endA > endB ? -1 : 1;
        }

        return (startA < startB) ? -1 : 1;
    }

    /**
     * Called when rows are translated. Since events "float" on top of everything they must be adjusted if translation
     * changes.
     * @private
     */
    onTranslateRow({ row }) {
        this.matchRowTranslation(row);
    }

    /**
     * Renders a single event, creating a div for it if needed or updates an existing div.
     * @private
     * @param data
     */
    renderEvent(data) {
        const me            = this,
            eventIdProperty = me.view.scheduledEventName + 'Id',
            eventId         = data[eventIdProperty],
            layoutCache     = me.cache.getTimeSpan(eventId, data.resourceId),
            renderedEvents  = me.cache.getRenderedEvents(data.resourceId),
            meta            = data.event.instanceMeta(me.scheduler),
            // Event might be flagged to require a new element in onEventAdd, but if it is a drag proxy it should still
            // reuse an existing element (it will be correctly linked to the drag proxy element)
            wrapperElement  = me.renderTimeSpan(data, layoutCache, renderedEvents[data.id], meta.requireElement && !meta.fromDragProxy);

        if (data.assignment) {
            wrapperElement.dataset.assignmentId = data.assignment.id;
        }

        // Add event/task id to wrappers dataset
        // Otherwise event element won't have event id property in it's dataset and scheduler
        // won't be able to resolve event by element reference (#8943)
        if (eventId) {
            wrapperElement.dataset[eventIdProperty] = eventId;
        }

        renderedEvents[data.id] = data;

        if (meta.requireElement) {
            delete meta.requireElement;
            delete meta.fromDragProxy;
        }

        // This event is documented on Scheduler
        me.scheduler.trigger('renderEvent', {
            eventRecord      : data.event,
            resourceRecord   : data.resource,
            assignmentRecord : data.assignment,
            element          : wrapperElement,
            tplData          : data
        });
    }

    // Overrides fn from baseclass to trigger a paint event with correct params
    triggerPaint(data, element, isRepaint = false) {
        const { scheduler } = this;

        scheduler.trigger(isRepaint ? 'eventRepaint' : 'eventPaint', {
            scheduler,
            eventRecord    : data.event,
            resourceRecord : data.resource,
            element
        });
    }

    // We only have to ask this question in the horizontal axis.
    // Vertical rendering is driven fully by the Grid's RowManager
    // rendering and derendering rows.
    isEventInView(eventLayout) {
        //<debug>
        // This was causing bugs when EventModel instance was passed in error.
        if (!('startMs' in eventLayout) && !('endMs' in eventLayout)) {
            throw new Error('Event render data block must be passed to HorizontalEventMapper#isEventInView');
        }
        //</debug>

        // Milestones need to be visible at start & end
        if (eventLayout.startMs === eventLayout.endMs) {
            return eventLayout.startMs <= this.viewportEnd && eventLayout.endMs > this.viewportStart;
        }

        // But normal events do not
        return eventLayout.startMs < this.viewportEnd && eventLayout.endMs > this.viewportStart;
    }

    // Displays events that are in view, hides/recycles those that are no longer
    updateRowTimeSpans(row, resource, forceLayout = false, fromHorizontalScroll = false) {
        const
            me         = this,
            scheduler  = me.scheduler,
            resourceId = resource.id;

        let renderedEvents      = me.cache.getRenderedEvents(resourceId),
            resourceLayoutCache = me.cache.getRow(resourceId);

        // no need to relayout events if only scrolling horizontally
        if ((scheduler.forceLayout || forceLayout || !resourceLayoutCache) && !me.layoutEvents(scheduler, resource, row)) {
            return (resourceLayoutCache && resourceLayoutCache._rowHeight) || 0;
        }

        // might have been updated above
        resourceLayoutCache = me.cache.getRow(resourceId);

        if (!resourceLayoutCache) return 0;

        const eventsInView = resourceLayoutCache._allEvents.filter(evt =>
            // Keep events flagged by for example EventDrag
            evt.event.instanceMeta(scheduler).retainElement ||
            // Assignment might also be flagged if used
            (evt.assignment &&  evt.assignment.instanceMeta(scheduler).retainElement) ||
            // And keep events actually in view :)
            me.isEventInView(evt)
        );

        if (!renderedEvents) {
            renderedEvents = {};
            me.cache.addRenderedEvents(resourceId, renderedEvents);
        }

        // Add events not already in dom or refresh those that are
        for (let i = 0; i < eventsInView.length; i++) {
            const data = eventsInView[i];

            // There are multiple pathways that might lead to the first render of events. This is the first reliable
            // place were we can determine that something will be rendered
            scheduler._firstRenderDone && scheduler._firstRenderDone();

            // Do not render events whose element is retained, or if scrolling horizontally already has an element
            if ((!fromHorizontalScroll && !data.event.instanceMeta(scheduler).retainElement) ||
                (fromHorizontalScroll && !data.layoutCache.div)) {

                const animation = scheduler.isFirstRender && data.top < scheduler.bodyHeight && scheduler.useInitialAnimation;

                if (animation) {
                    data.wrapperCls.add('b-first-render');
                    data.internalStyle = `animation-delay: ${data.row.index / 20}s;`;
                }

                me.renderEvent(data);

                data.wrapperCls['b-first-render'] = null;
            }
        }

        // Remove already rendered events that are now outside of view
        const renderedEventIds = Object.keys(renderedEvents);
        for (let i = 0; i < renderedEventIds.length; i++) {
            const eventId = renderedEventIds[i];
            if (!eventsInView.some(e => e.id === eventId)) {
                me.releaseEvent(resourceId, eventId);
            }
        }

        return resourceLayoutCache._rowHeight;
    }

    renderer(renderData) {
        // Render the resource's events.
        renderData.size.height = this.updateRowTimeSpans(renderData.row, renderData.record);
    }

    //endregion

    //region Store events

    matchRowTranslation(row) {
        const me            = this,
            // Cannot check row.id because translation happens before render, id might not be updated.
            recordId      = me.scheduler.store.getAt(row.dataIndex).id,
            resourceCache = me.cache.getRow(recordId),
            first         = resourceCache && resourceCache._allEvents.length > 0 && resourceCache._allEvents[0];

        // only update events whose resource has changed top
        if (first && first.rowTop !== row.top) {
            const deltaY = row.top - first.rowTop;
            resourceCache._allEvents.forEach(data => {
                const eventLayout = data.layoutCache;
                eventLayout.top += deltaY;
                eventLayout.bottom += deltaY;
                if (eventLayout.div) {
                    me.positionTimeSpan(eventLayout.div, eventLayout.start, eventLayout.top);
                }

                data.top += deltaY;
                data.rowTop = row.top;
            });
        }
    }

    onTimeAxisViewModelUpdate() {
        // if we do not clear cache, refresh will happen and will use older cache
        // caught by test 012_dragdrop
        this.cache.clear();

        // TODO: this always happen, unnecessary to perform layout prior to this?

        // always update view bounds and redraw events
        this.updateFromHorizontalScroll(this.scheduler.timeAxisSubGrid.scrollable.x);
    }

    // resource removed, move all affected events up
    onRowRemove({ isCollapse }) {
        const { scheduler } = this;

        if (!isCollapse) {
            scheduler.runWithTransition(() => {
                scheduler.rowManager.forEach(row => this.matchRowTranslation(row));
            });
        }
    }

    /**
     * User toggled a group in a grouped grid, events needs to be redrawn since they might appear/disappear
     * @private
     */
    onToggleGroup({ groupRecord, collapse }) {
        let store       = this.scheduler.store,
            // First record in next group
            recordIndex = store.indexOf(groupRecord) + (collapse ? 1 : groupRecord.groupChildren.length);

        // Handle this group
        if (collapse) {
            // Collapsing -> events in the group will be hidden, remove them from cache
            groupRecord.groupChildren.forEach(child => {
                this.cache.clearRow(child.id);
            });
        }

        // TODO: this should not need to loop til the end, since only events in view are drawn. will be costly with large amount of resources
        // Loop starting at the next group
        for (; recordIndex < store.count; recordIndex++) {
            this.cache.clearRow(store.getAt(recordIndex).id);
        }
    }

    onEventDataset() {
        // Previously dataset would use onEventAdd, but if we get fewer events than we had some will stick around
        // just for the fun of it. Better clear all
        this.cache.clear(true);
    }

    onEventAdd({ records, resources }) {
        let me = this,
            startDate, endDate;

        //        me.scheduler.rowManager.averageRowHeight = null; // force recalculation, since moving events might have affected row height

        records.forEach(event => {
            startDate = event.startDate;
            endDate = event.endDate;

            if (startDate && endDate && me.timeAxis.timeSpanInAxis(startDate, endDate)) {
                const eventResources = event.resources;
                // Flag that this event needs a new element, it should not steal some other released events element because
                // that might lead to a transition. Only set if event will be in view
                if (eventResources && eventResources.some(resource => me.scheduler.getRowFor(resource))) {
                    event.instanceMeta(me.scheduler).requireElement = true;
                }

                // TODO: this does not make sense when adding multiple events, resources are then not tied to the event
                // repaint row only if event is in time axis
                resources.forEach(resource => {
                    this.cache.clearRow(resource.id, false, true);
                });
            }
        });
    }

    onEventRemove({ records, resources }) {
        const me = this;

        // Remove the divs fully since creation now adopts the proxy to use as the event div
        resources.forEach(resource =>
            me.cache.clearRow(resource.id, true)
            //scheduler.store.getIndex(resource) >= 0 && scheduler.repaintEventsForResource(resource)
        );
    }

    onEventUpdate({ source : eventStore, record : eventRecord, changes = {}, resources }) {
        const me                 = this,
            resourceWasChanged = 'resourceId' in changes;

        // A change happened to an event missing a resource, do nothing
        if (!resourceWasChanged && !resources.length) return;

        // Moved to another resource, invalidate both old and new (only applies when not using an AssignmentStore)
        if (resourceWasChanged) {
            const oldResource              = eventStore.resourceStore.getById(changes.resourceId.oldValue),
                newResource              = eventRecord.resource,
                eventRenderedAfterUpdate = newResource && eventStore.resourceStore.isAvailable(newResource),
                isVisible = eventRecord.resource && Boolean(me.getTimeSpanRenderData(eventRecord, eventRecord.resource));

            // invalidate old and new resource event layouts
            if (oldResource) {
                const oldLayout = me.cache.getTimeSpan(eventRecord.id, oldResource.id);

                // If the event is layed out for oldResource, that resources needs to be redrawn
                if (oldLayout) {
                    // If it's to be rerendered after the change, set the div's
                    // id so that after it's returned to the cache, it will be plucked
                    // out by the renderer to be reused.
                    if (eventRenderedAfterUpdate) {
                        // If it has an element, release the div, but release it under the id for
                        // which it *will* be used when the cleared resource rows are rerendered.
                        if (oldLayout.div && newResource) {
                            oldLayout.div.id = me.scheduler.getEventRenderId(eventRecord, newResource);
                        }
                        me.cache.clearRow(oldResource.id, false, eventRenderedAfterUpdate);
                    }
                    // If it's not going to be rerendered, dump the div completely
                    else {
                        me.cache.clearEvent(eventRecord.id, changes.resourceId.oldValue, true);
                    }
                }
            }
            // Event was added first and then assigned to a resource (or similar scenario). There will be no div tied to
            // the event and it might reuse an element reserved for some other event (which will transition in to place)
            // TODO: Rework element reusage to always process events with existing elements first and then "new",
            //   when done this workaround and the one in onEventAdd() can be removed
            else if (eventRenderedAfterUpdate) {
                eventRecord.instanceMeta(me.scheduler).requireElement = true;
            }

            eventRenderedAfterUpdate && me.cache.clearRow(newResource.id, false, isVisible);

            // TODO: Not so sure we should throw the average away on account of a single row that might change height,
            //  check if it works good without this/make gradual adjustment
            me.scheduler.rowManager.averageRowHeight = null; // force recalculation, since moving events might have affected row height
        }
        // start or end date changed, need to update layout
        else if (('startDate' in changes || 'endDate' in changes)) {
            resources.forEach(resourceRecord => {
                // Moving events on a row with multiple events might affect those other events, or the height of the row
                // which affects everything below
                if (me.scheduler.eventStore.getEventsForResource(resourceRecord).length > 1) {
                    // TODO: Not so sure we should throw the average away on account of a single row that might change height,
                    //  check if it works good without this/make gradual adjustment
                    me.scheduler.rowManager.averageRowHeight = null;
                }
            });
        }

        resources.forEach(resourceRecord => {
            const eventLayoutData = me.getTimeSpanRenderData(eventRecord, resourceRecord),
                isVisible = Boolean(eventLayoutData && me.isEventInView(eventLayoutData));

            // Clear cache, avoiding to hide the element if visible
            me.cache.clearRow(resourceRecord.id, false, isVisible);
        });
    }

    // Event id changed, change elements id to have it reused correctly on redraw
    onEventStoreIdChange({ oldValue, value }) {
        const { view } = this;

        DomHelper.forEachSelector(view.element, `[data-event-id="${oldValue}"]`, el => {
            el.id = view.getEventRenderId(value, view.getResourceIdFromDomNodeId(el.id));
        });
    }

    onEventClearChanges() {
        this.cache.clear();
    }

    onEventFilter() {
        this.cache.clear();
    }

    onEventRemoveAll() {
        this.view.rowManager.averageRowHeight = null; // force recalculation, since moving events might have affected row height
        this.cache.clear(true);
    }

    //endregion

    //region Dependency connectors

    /**
     * Gets displaying item start side
     *
     * @param {Scheduler.model.EventModel} eventRecord
     * @return {String} 'left' / 'right' / 'top' / 'bottom'
     */
    getConnectorStartSide(eventRecord) {
        return 'left';
    }

    /**
     * Gets displaying item end side
     *
     * @param {Scheduler.model.EventModel} eventRecord
     * @return {String} 'left' / 'right' / 'top' / 'bottom'
     */
    getConnectorEndSide(eventRecord) {
        return 'right';
    }

    //endregion
}
