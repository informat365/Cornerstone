import Base from '../../../Core/Base.js';
import Delayable from '../../../Core/mixin/Delayable.js';
import DomHelper from '../../../Core/helper/DomHelper.js';
import DomSync from '../../../Core/helper/DomSync.js';
import ObjectHelper from '../../../Core/helper/ObjectHelper.js';
import VerticalLayout from '../../eventlayout/VerticalLayout.js';
import Rectangle from '../../../Core/helper/util/Rectangle.js';
import DateHelper from '../../../Core/helper/DateHelper.js';

const
    releaseEventActions = {
        'releaseElement' : 1, // Not used at all at the moment
        'reuseElement'   : 1  // Used by some other element
    },
    renderEventActions = {
        'newElement'      : 1,
        'reuseOwnElement' : 1,
        'reuseElement'    : 1
    },
    emptyArray = Object.freeze([]);

export default class VerticalEventMapper extends Delayable(Base) {

    //region Config & Init

    static get properties() {
        return {
            eventMap         : {},
            resourceMap      : {},
            releasedElements : {}
        };
    }

    construct(scheduler) {
        this.scheduler = scheduler;
        this.timeAxisViewModel = scheduler.timeAxisViewModel;
        this.verticalLayout = new VerticalLayout({ scheduler });

        super.construct({});
    }

    init() {
        const
            me = this,
            { scheduler } = me;

        // Resource header/columns
        me.resourceColumns = scheduler.timeAxisColumn.resourceColumns;

        scheduler.element.classList.add('b-sch-vertical');

        scheduler.on({
            assignmentstorechange : me.onChangeAssignmentStore,
            resourcestorechange   : me.onChangeResourceStore,
            thisObj               : me
        });

        me.resourceColumns.on({
            columnWidthChange : me.onResourceColumnWidthChange,
            thisObj           : me
        });

        me.resourceStore = scheduler.resourceStore;
        me.assignmentStore = scheduler.assignmentStore;

        me.initialized = true;

        if (scheduler.isPainted) {
            me.renderer();
        }

        me.resourceColumns.availableWidth = scheduler.timeAxisSubGrid.width;
    }

    //endregion

    //region Elements <-> Records

    resolveRowRecord(elementOrEvent, xy) {
        const
            me            = this,
            { scheduler } = me,
            event         = elementOrEvent instanceof Event ? elementOrEvent : null,
            element       = event ? event.target : elementOrEvent,
            coords        = event ? [event.offsetX, event.offsetY] : xy,
            // Fix for FF on Linux having text nodes as event.target
            el            = element.nodeType === 3 ? element.parentElement : element,
            eventElement  = DomHelper.up(el, scheduler.eventSelector);

        if (eventElement) {
            return scheduler.resourceStore.getById(eventElement.dataset.resourceId);
        }

        // Need to be inside schedule at least
        if (!DomHelper.up(element, '.b-sch-timeaxis-cell')) {
            return null;
        }

        if (!coords) {
            throw new Error(`Vertical mode needs coordinates to resolve this element. Can also be called with a browser 
                event instead of element to extract element and coordinates from`);
        }

        const index = Math.floor(coords[0] / me.resourceColumns.columnWidth);

        return scheduler.resourceStore.getAt(index);
    }

    getElementFromEventRecord(eventRecord, resourceRecord) {
        // All elements for event + optionally assigned resource
        const elements = this.getElementsFromEventRecord(eventRecord, resourceRecord);
        // Return first one
        return elements.length ? elements[0] : null;
    }

    getElementsFromEventRecord(eventRecord) {
        // Holds resources for which the event is drawn
        const map = this.eventMap[eventRecord.id];
        // Return the elements for those
        return map ? Object.values(map).map(data => data.element) : emptyArray;
    }

    toggleCls(eventRecord, resourceRecord, cls, add = true) {
        const eventData = ObjectHelper.getPath(this.eventMap, `${eventRecord.id}.${resourceRecord.id}`);

        if (eventData) {
            eventData.tplData.cls[cls] = add ? 1 : 0;

            if (eventData.element) {
                eventData.element.querySelector(this.scheduler.eventInnerSelector).classList[add ? 'add' : 'remove'](cls);
            }
        }
    }

    //endregion

    //region Coordinate <-> Date

    getDateFromXY(xy, roundingMethod, local, allowOutOfRange = false) {
        let coord = xy[1];

        if (!local) {
            coord = this.translateToScheduleCoordinate(coord);
        }

        return this.timeAxisViewModel.getDateFromPosition(coord, roundingMethod, allowOutOfRange);
    }

    translateToScheduleCoordinate(y) {
        return y - this.scheduler._bodyRectangle.y + this.scheduler.scrollTop;
    }

    translateToPageCoordinate(y) {
        return y + this.scheduler._bodyRectangle.y - this.scheduler.scrollTop;
    }

    //endregion

    //region Regions

    getResourceEventBox(eventId, resourceId) {
        let { tplData } = ObjectHelper.getPath(this.eventMap, `${eventId}.${resourceId}`) || {};

        if (!tplData) {
            // Never been in view, lay it out
            this.layoutResource(this.scheduler.resourceStore.getById(resourceId));

            // Have another go at getting the layout data
            tplData = ObjectHelper.getPath(this.eventMap, `${eventId}.${resourceId}`).tplData;
        }

        return tplData ? {
            start  : tplData.left,
            end    : tplData.left + tplData.width,
            top    : tplData.top,
            bottom : tplData.bottom
        } : null;
    }

    getScheduleRegion(resourceRecord, eventRecord, local) {
        const
            me            = this,
            { scheduler } = me;

        let region = Rectangle.from(scheduler.timeAxisSubGridElement);

        if (resourceRecord) {
            // TODO: How to account for eventRecord here?
            region.left  = me.resourceStore.indexOf(resourceRecord) * scheduler.resourceColumnWidth;
            region.right = region.left + scheduler.resourceColumnWidth;
        }

        const
            start           = scheduler.timeAxis.startDate,
            end             = scheduler.timeAxis.endDate,
            dateConstraints = scheduler.getDateConstraints(resourceRecord, eventRecord) || {
                start,
                end
            },
            startY          = scheduler.getCoordinateFromDate(DateHelper.max(start, dateConstraints.start)),
            endY            = scheduler.getCoordinateFromDate(DateHelper.min(end, dateConstraints.end));

        if (!local) {
            region.top = me.translateToPageCoordinate(startY);
            region.bottom = me.translateToPageCoordinate(endY);
        }
        else {
            region.top = startY;
            region.bottom = endY;
        }

        return region;
    }

    getRowRegion(resourceRecord, startDate, endDate) {
        const
            me            = this,
            { scheduler } = me,
            x             = me.resourceStore.indexOf(resourceRecord) * scheduler.resourceColumnWidth,
            taStart       = scheduler.timeAxis.startDate,
            taEnd         = scheduler.timeAxis.endDate,
            start         = startDate ? DateHelper.max(taStart, startDate) : taStart,
            end           = endDate ? DateHelper.min(taEnd, endDate) : taEnd,
            startY        = scheduler.getCoordinateFromDate(start),
            endY          = scheduler.getCoordinateFromDate(end, true, true),
            y             = Math.min(startY, endY),
            height        = Math.abs(startY - endY);

        return new Rectangle(x, y, scheduler.resourceColumnWidth, height);
    }

    getVisibleDateRange() {
        const
            scheduler = this.scheduler,
            scrollPos = scheduler.scrollable.y,
            height    = scheduler.scrollable.clientHeight;

        return {
            startDate : scheduler.getDateFromCoordinate(scrollPos),
            endDate   : scheduler.getDateFromCoordinate(scrollPos + height) || scheduler.timeAxis.endDate
        };
    }
    //endregion

    //region Events

    // Column width changed, rerender fully
    onResourceColumnWidthChange({ width, oldWidth }) {
        const
            me = this,
            { scheduler } = me;

        // Fix width of column & header
        me.resourceColumns.width = scheduler.timeAxisColumn.width = me.resourceStore.count * width;
        me.clearAll();

        // Only transition large changes, otherwise it is janky when dragging slider in demo
        scheduler.runWithTransition(() => me.renderer(), Math.abs(width - oldWidth) > 30);

        // Not detected by resizeobserver? Need to call this for virtual scrolling to react to update
        //        scheduler.callEachSubGrid('refreshFakeScroll');
        //        scheduler.refreshVirtualScrollbars();
    }

    //endregion

    //region EventStore

    onEventStoreChange({ action, records, record, replaced, changes, fromRelationUpdate }) {
        const
            me = this,
            eventRecords = records || (record ? [record] : []),
            resourceIds = new Set();

        let transition = true;

        eventRecords.forEach(eventRecord => {
            eventRecord.resources.forEach(resourceRecord => resourceIds.add(resourceRecord.id));
        });

        switch (action) {
            // No-ops
            case 'sort':
            case 'group':
            case 'move':
                // Order in EventStore does not matter, so these actions are no-ops
                return;

            case 'dataset':
                me.clearAll();
                transition = false;
                break;

            case 'add':
            case 'remove':
            case 'updateMultiple':
                // Clear all affected resources
                me.clearResources(resourceIds);
                break;

            case 'replace':
                // Gather resources from both the old record and the new one
                replaced.forEach(([oldEvent, newEvent]) => {
                    oldEvent.resources.map(resourceRecord => resourceIds.add(resourceRecord.id));
                    newEvent.resources.map(resourceRecord => resourceIds.add(resourceRecord.id));
                });
                // And clear them
                me.clearResources(resourceIds);
                break;

            case 'removeall':
            case 'filter':
                // Clear all when filtering for simplicity. If that turns out to give bad performance, one would need to
                // figure out which events was filtered out and only clear their resources.
                me.clearAll();
                break;

            case 'update':
                // Removing a resource will unassign events, triggering an update. Ignore this case, since it will be
                // handled by the resource store listener
                if (record.meta.removingResource) {
                    return;
                }

                let processed = false;

                // Potentially affecting more elements in the same resource
                if ('startDate' in changes || 'endDate' in changes || 'duration' in changes || 'id' in changes) {
                    me.clearResources(resourceIds);
                    processed = true;
                }

                // Moved between resources, redraw both ends (new value already extracted above)
                if ('resourceId' in changes) {
                    // unless if caused by a relation update (changed resources id), in which case it will be handled
                    // by onResourceStoreChange()
                    if (fromRelationUpdate) {
                        return;
                    }

                    resourceIds.add(changes.resourceId.oldValue);
                    me.clearResources(resourceIds);
                    processed = true;
                }

                // "Internal" change, not affecting layout
                if (!processed) {
                    const eventDatas = Object.values(me.eventMap[record.id]);
                    eventDatas.forEach(eventData => {
                        // Update layout/contents
                        const { tplData } = eventData;
                        eventData.tplData = me.scheduler.generateTplData(tplData.eventRecord, tplData.resourceRecord);
                        eventData.tplData.left = tplData.left;
                        eventData.tplData.width = tplData.width;
                    });
                }
                break;
        }

        me.scheduler.runWithTransition(() => me.renderer(), transition);
    }

    //endregion

    //region ResourceStore

    // Hook up resource store for the mapper and the resource header
    set resourceStore(store) {
        const me = this;

        me.resourceStoreDetacher && me.resourceStoreDetacher();

        me.resourceColumns.resourceStore = me._resourceStore = store;

        me.resourceStoreDetacher = store.on({
            change  : me.onResourceStoreChange,
            refresh : me.onResourceStoreRefresh,
            thisObj : me,
            prio    : 1 // Call before others to clear cache before redraw
        });
    }

    get timeView() {
        return this.scheduler.timeView;
    }

    get resourceStore() {
        return this._resourceStore;
    }

    onChangeResourceStore({ newResourceStore }) {
        const me = this;

        // Invalidate resource range and events
        me.firstResource = me.lastResource = null;
        me.clearAll();

        me.resourceStore = newResourceStore;

        me.renderer();
    }

    onResourceStoreChange({ source : resourceStore, action, records, record }) {
        const
            me = this,
            resourceRecords = records || (record ? [record] : []);

        let transition = true;

        // Invalidate resource range
        me.firstResource = me.lastResource = null;

        switch (action) {
            case 'add':
                // Make sure all existing events following added resources are offset correctly
                const firstIndex = resourceRecords.reduce(
                    (index, record) => Math.min(index, resourceStore.indexOf(record)),
                    resourceStore.count
                );

                for (let i = firstIndex; i < resourceStore.count; i++) {
                    me.clearResources([resourceStore.getAt(i).id]);
                }

                return; // Will be redrawn from column width change

            case 'remove':
            case 'removeall':
                // Cannot tell from which index a record was removed, update all.
                me.clearAll();
                return; // Will be redrawn from column width change

            case 'replace':
            case 'update':
                // Only the invalidation above needed
                break;

            case 'filter':
                // All filtered out resources needs clearing and so does those not filtered out since they might have
                // moved horizontally when others hide
                me.clearAll();
                break;
        }

        me.scheduler.runWithTransition(() => me.renderer(), transition);
    }

    onResourceStoreRefresh({ action }) {
        const me = this;

        if (action === 'group') {
            throw new Error('Grouping of resources not supported in vertical mode');
        }

        if (action === 'sort') {
            // Invalidate resource range
            me.firstResource = me.lastResource = null;
            me.clearAll();
            me.scheduler.runWithTransition(() => me.renderer());
        }
    }

    //endregion

    //region AssignmentStore

    // Hook up assignment store for the mapper
    set assignmentStore(store) {
        const me = this;

        me.assignmentStoreDetacher && me.assignmentStoreDetacher();

        me._assignmentStore = store;

        if (store) {
            me.assignmentStoreDetacher = store.on({
                change  : me.onAssignmentStoreChange,
                thisObj : me
            });
        }
    }

    get assignmentStore() {
        return this._assignmentStore;
    }

    onChangeAssignmentStore({ newAssignmentStore }) {
        this.assignmentStore = newAssignmentStore;
    }

    onAssignmentStoreChange({ action, records, replaced, record, changes }) {
        const
            me                = this,
            assignmentRecords = records || (record ? [record] : []),
            resourceIds       = new Set(assignmentRecords.map(assignmentRecord => assignmentRecord.resourceId));

        let transition = true;

        switch (action) {
            case 'add':
            case 'remove':
            case 'updateMultiple':
                me.clearResources(resourceIds);
                break;

            case 'filter':
            case 'removeall':
                me.clearAll();
                break;

            case 'replace':
                // Gather resources from both the old record and the new one
                replaced.forEach(([oldAssignment, newAssignment]) => {
                    resourceIds.add(oldAssignment.resourceId);
                    resourceIds.add(newAssignment.resourceId);
                });
                // And clear them
                me.clearResources(resourceIds);
                break;

            case 'update':
                // When reassigning, clear old resource also
                if ('resourceId' in changes) {
                    resourceIds.add(changes.resourceId.oldValue);
                }
                me.clearResources(resourceIds);
                break;
        }

        me.scheduler.runWithTransition(() => me.renderer(), transition);
    }

    //endregion

    //region View hooks

    refreshRows(reLayoutEvents) {
        if (reLayoutEvents) {
            this.clearAll();
            this.scheduler.refreshFromRerender = false;
        }
    }
    updateFromHorizontalScroll(scrollLeft) {
        if (scrollLeft !== this.prevScrollLeft) {
            this.renderer();
            this.prevScrollLeft = scrollLeft;
        }
    }

    updateFromVerticalScroll() {
        this.renderer();
    }

    scrollResourceIntoView(resourceRecord, options) {
        const
            { scheduler } = this,
            x = scheduler.resourceStore.indexOf(resourceRecord) * scheduler.resourceColumnWidth;

        return scheduler.scrollHorizontallyTo(x, options);
    }

    // Called when viewport size changes
    onViewportResize(width) {
        this.resourceColumns.availableWidth = width;
        this.renderer();
    }

    // Clear events in case they use date as part of displayed info
    onLocaleChange() {
        this.clearAll();
    }

    // No need to do anything special
    onDragAbort() {}

    onBeforeRowHeightChange() {}

    onTimeAxisViewModelUpdate() {}

    clearEvents() {}

    updateElementId() {}

    releaseTimeSpanDiv() {}

    //endregion

    //region Rendering

    // Resources in view + buffer
    get resourceRange() {
        const { scheduler, resourceStore } = this;

        if (!resourceStore || !resourceStore.count) {
            return { firstResource : -1, lastResource : -1 };
        }

        return {
            firstResource : Math.max(Math.floor(scheduler.scrollLeft / scheduler.resourceColumnWidth) - 1, 0),
            lastResource  : Math.min(
                Math.floor((scheduler.scrollLeft + scheduler.timeAxisSubGrid.width) /  scheduler.resourceColumnWidth) + 1,
                resourceStore.count - 1
            )
        };
    }

    // Dates in view + buffer
    get dateRange() {
        const
            { scheduler } = this;

        let bottomDate = scheduler.getDateFromCoordinate(Math.min(
            scheduler.scrollTop + scheduler.bodyHeight + scheduler.tickSize - 1,
            (scheduler.virtualScrollHeight || scheduler.scrollable.scrollHeight) - 1)
        );

        // Might end up below time axis (out of ticks)
        // TODO: Change call order on refresh to make sure this is not needed?
        if (!bottomDate) {
            bottomDate = scheduler.timeAxis.last.endDate;
        }

        return {
            topDate : scheduler.getDateFromCoordinate(Math.max(scheduler.scrollTop - scheduler.tickSize, 0)),
            bottomDate
        };
    }

    getTimeSpanRenderData(eventRecord, resourceRecord, includeOutside = false) {
        const
            me            = this,
            { scheduler } = me,
            { startDate, endDate } = eventRecord,
            top           = scheduler.getCoordinateFromDate(startDate),
            // Preliminary values for left & width, used for proxy. Will be changed on layout
            left          = me.resourceStore.indexOf(resourceRecord) * scheduler.resourceColumnWidth,
            width         = scheduler.resourceColumnWidth - scheduler.resourceMargin * 2,
            startDateMS   = startDate.getTime(),
            endDateMS     = endDate.getTime();

        let bottom = scheduler.getCoordinateFromDate(endDate),
            height = bottom - top;

        // Below, estimate height
        if (bottom === -1) {
            height = Math.round((endDateMS - startDateMS) * scheduler.timeAxisViewModel.getSingleUnitInPixels('ms'));
            bottom = top + height;
        }

        return {
            eventRecord,
            resourceRecord,
            left,
            top,
            bottom,
            width,
            height,
            startDate,
            endDate,
            startDateMS,
            endDateMS,

            // to match horizontal, TODO: should change there
            start   : startDate,
            end     : endDate,
            startMs : startDateMS,
            endMs   : endDateMS
        };
    }

    // Earlier start dates are above later tasks
    // If same start date, longer tasks float to top
    // If same start + duration, sort by name
    eventSorter(a, b) {
        const
            startA    = a.dataStartMs || a.startDateMS, // dataXX are used if configured with fillTicks
            endA      = a.dataEndMs || a.endDateMS,
            startB    = b.dataStartMs || b.startDateMS,
            endB      = b.dataEndMs || b.endDateMS,
            sameStart = (startA === startB);

        if (sameStart) {
            if (endA - endB === 0) {
                return a.eventRecord.name < b.eventRecord.name ? -1 : 1;
            }
            return endA > endB ? -1 : 1;
        }

        return (startA < startB) ? -1 : 1;
    }

    // Calculate the layout for all events assigned to a resource. Since we are never stacking, the layout of one
    // resource will never affect the others
    layoutResource(resourceRecord) {
        const
            me = this,
            { scheduler } = me,
            // Cache per resource
            cache = me.resourceMap[resourceRecord.id] = {},
            // Resource "column"
            resourceIndex = scheduler.resourceStore.indexOf(resourceRecord);

        // All events for the resource
        let events = scheduler.eventStore.getEventsForResource(resourceRecord);

        // Hook for features to inject additional timespans to render
        events = scheduler.getEventsToRender(resourceRecord, events);

        // Generate template data for all events, used for rendering and layout
        const layoutData = events.reduce((toLayout, eventRecord) => {
            const
                tplData = scheduler.generateTplData(eventRecord, resourceRecord),
                // Elements will be appended to eventData during syncing
                eventData = { tplData };

            // Cache per event, { e1 : { r1 : { xxx }, r2 : ... }, e2 : ... }
            // Uses tplData.eventId in favor of eventRecord.id to work with ResourceTimeRanges
            ObjectHelper.setPath(me.eventMap, `${tplData.eventId}.${resourceRecord.id}`, eventData);

            // Cache per resource
            cache[tplData.eventId] = eventData;

            // Position ResourceTimeRanges directly, they do not affect the layout of others
            if (tplData.fillSize) {
                tplData.left  = resourceIndex * scheduler.resourceColumnWidth;
                tplData.width = scheduler.resourceColumnWidth;
            }
            // Anything not flagged with `fillSize` should take part in layout
            else {
                toLayout.push(tplData);
            }

            return toLayout;
        }, []);

        // Ensure the events are rendered in natural order so that navigation works.
        layoutData.sort(me.eventSorter);

        // Apply per resource event layout (pack, overlap or mixed)
        me.verticalLayout.applyLayout(
            layoutData,
            scheduler.resourceColumnWidth,
            scheduler.resourceMargin,
            scheduler.barMargin,
            resourceIndex
        );

        return cache;
    }

    // Render a single event, aborting if already in DOM. To update an event, first release its element and then render
    // it again. The element will be reused and updated. Keeps code simpler
    renderEvent(eventData) {
        // No point in rendering event that already has an element
        const
            data = eventData.tplData,
            { resourceRecord, eventRecord } = data,
            // Event element config, applied to existing element or used to create a new one below
            elementConfig = {
                className : data.wrapperCls,
                tabIndex  : '0',
                children  : [
                    {
                        className   : data.cls,
                        style       : (data.internalStyle || '') + (data.style || ''),
                        // Clone to be repeatable
                        html        : data.body.cloneNode(true),
                        // Used for comparison, cheaper than comparing fragment from above
                        compareHtml : data.eventContent
                    }
                ],
                style : {
                    transform : `translate(${data.left}px, ${data.top}px)`,
                    // DomHelper appends px to dimensions when using numbers
                    height    : data.height,
                    width     : data.width,
                    zIndex    : data.zIndex
                },
                dataset : {
                    resourceId : resourceRecord.id,
                    eventId    : data.eventId, // Not using eventRecord.id to distinguish between Event and ResourceTimeRange
                    // Sync using assignment id in multi assignment mode or event id in single assignment mode
                    syncId     : data.assignment ? data.assignment.id : data.eventId

                },
                // Will not be part of DOM, but attached to the element
                elementData   : eventData,
                // Dragging etc. flags element as retained, to not reuse/release it during that operation
                retainElement : eventRecord.instanceMeta(this.scheduler).retainElement
            };

        // Do not want to spam dataset with empty prop when not using assignments
        if (data.assignment) {
            elementConfig.dataset.assignmentId = data.assignment.id;
        }

        return elementConfig;
    }

    renderResource(resourceRecord) {
        const
            me                          = this,
            // Date at top and bottom for determining which events to include
            { topDateMS, bottomDateMS } = me,
            // Will hold element configs
            syncConfigs                 = [];

        let resourceEntry = me.resourceMap[resourceRecord.id];

        // Layout all events for the resource unless already done
        if (!resourceEntry) {
            resourceEntry = me.layoutResource(resourceRecord);
        }

        // Iterate over all events for the resource
        for (let eventId in resourceEntry) {
            const eventData = resourceEntry[eventId];
            // Only collect configs for those actually in view
            if (eventData.tplData.endDateMS >= topDateMS && eventData.tplData.startDateMS <= bottomDateMS) {
                syncConfigs.push(me.renderEvent(eventData));
            }
        }

        return syncConfigs;
    }

    // Single cell so only one call to this renderer, determine which events are in view and draw them.
    // Drawing on scroll is triggered by `updateFromVerticalScroll()` and `updateFromHorizontalScroll()`
    renderer() {
        const
            me                              = this,
            { scheduler }                   = me,
            { resourceStore }               = scheduler,
            // Determine resource range to draw events for
            { firstResource, lastResource } = me.resourceRange,
            // Date at top and bottom for determining which events to include
            { topDate, bottomDate }         = me.dateRange,
            syncConfigs                     = [],
            featureDomConfigs               = [];

        if (!me.initialized) {
            return;
        }

        //<debug>
        if (window.DEBUG) {
            if (me.firstResource !== firstResource || me.lastResource !== lastResource) {
                console.log(`Resources in view ${resourceStore.getAt(firstResource).id} -> ${resourceStore.getAt(lastResource).id}`);
            }
        }
        //</debug>

        // Update current time range, reflecting the change on the vertical time axis header
        if (!DateHelper.isEqual(topDate, me.topDate) || !DateHelper.isEqual(bottomDate, me.bottomDate)) {
            // Calculated values used by `renderResource()`
            me.topDate = topDate;
            me.bottomDate = bottomDate;
            me.topDateMS = topDate.getTime();
            me.bottomDateMS = bottomDate.getTime();

            me.timeView.range = { startDate : topDate, endDate : bottomDate };
        }

        if (firstResource !== -1 && lastResource !== -1) {
            // Collect all events for resources in view
            for (let i = firstResource; i <= lastResource; i++) {
                syncConfigs.push.apply(syncConfigs, me.renderResource(resourceStore.getAt(i)));
            }
        }

        scheduler.getForegroundDomConfigs(featureDomConfigs);

        syncConfigs.push.apply(syncConfigs, featureDomConfigs);

        DomSync.sync({
            domConfig : {
                onlyChildren : true,
                children     : syncConfigs
            },
            targetElement : scheduler.foregroundCanvas,
            syncIdField   : 'syncId',

            // Called by DomHelper when it creates, releases or reuses elements
            callback({ action, domConfig, lastDomConfig, targetElement }) {
                // If element is an event wrap, trigger appropriate events
                if (domConfig && domConfig.className && domConfig.className[scheduler.eventCls + '-wrap']) {
                    const
                        // Some actions are considered first a release and then a render (reusing another element).
                        // This gives clients code a chance to clean up before reusing an element
                        isRelease = releaseEventActions[action],
                        isRender  = renderEventActions[action];

                    // If we are reusing an element that was previously released we should not trigger again
                    if (isRelease && lastDomConfig && !lastDomConfig.isReleased) {
                        const
                            data  = lastDomConfig.elementData.tplData,
                            event = {
                                tplData          : data,
                                assignmentRecord : data.assignment,
                                eventRecord      : data.eventRecord,
                                resourceRecord   : data.resourceRecord,
                                targetElement
                            };
                        // This event is documented on Scheduler
                        scheduler.trigger('releaseEvent', event);
                    }

                    if (isRender) {
                        const
                            data  = domConfig.elementData.tplData,
                            event = {
                                tplData          : data,
                                assignmentRecord : data.assignment,
                                eventRecord      : data.eventRecord,
                                resourceRecord   : data.resourceRecord,
                                targetElement
                            };

                        // Store element to allow easy mapping from record
                        targetElement.elementData.element = targetElement;

                        event.reusingElement = action === 'reuseElement';
                        // This event is documented on Scheduler
                        scheduler.trigger('renderEvent', event);
                    }

                    // No changes during sync, but might need to hook up element again in case cache was cleared
                    if (action === 'none') {
                        domConfig.elementData.element = targetElement;
                    }
                }
            }
        });

        // Change in displayed resources?
        if (me.firstResource !== firstResource || me.lastResource !== lastResource) {
            // Update header to match
            me.resourceColumns.range = { firstResource, lastResource };

            // Store which resources are currently in view
            me.firstResource = firstResource;
            me.lastResource = lastResource;
        }
    }

    //endregion

    //region Cache

    // Clears cached resource layout
    clearResources(resourceIds) {
        //<debug>
        if (window.DEBUG) console.log('%Clearing resources ' + Array.from(resourceIds).join(','), 'color: #770000');
        //</debug>

        resourceIds.forEach(resourceId => {
            if (this.resourceMap[resourceId]) {
                Object.keys(this.resourceMap[resourceId]).forEach(eventId => {
                    delete this.eventMap[eventId][resourceId];
                });

                delete this.resourceMap[resourceId];
            }
        });
    }

    clearAll() {
        //<debug>
        if (window.DEBUG) console.log('%Clearing all', 'color: #770000');
        //</debug>

        this.resourceMap = {};
        this.eventMap = {};
    }

    //endregion
}
