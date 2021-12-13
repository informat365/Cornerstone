import Base from '../../../Core/Base.js';
import DateHelper from '../../../Core/helper/DateHelper.js';
import Rectangle from '../../../Core/helper/util/Rectangle.js';
import DomHelper from '../../../Core/helper/DomHelper.js';
import DomDataStore from '../../../Core/data/DomDataStore.js';
import Delayable from '../../../Core/mixin/Delayable.js';
import Bag from '../../../Core/util/Bag.js';

/**
 * @module Scheduler/view/orientation/BaseHorizontalMapper
 */

const
    emptyObject = {},
    elRange     = document.createRange(),
    MAX_WIDTH   = 9999999;

function isEventElAvailable(element) {
    return !(element.classList.contains('b-dragging') || element.classList.contains('b-resizing'));
}

/**
 * Base class for task & event rendering in horizontal mode, used by Scheduler and Gantt
 * @private
 * @abstract
 */
export default class BaseHorizontalMapper extends Delayable(Base) {
    //region Init

    construct(view) {
        const me = this;

        me.view = view;
        me.innerElCls = `${view.eventCls}`;

        me.availableDivs = new Bag();
        me.reservedIds = {};
        me.divCount = 0;
    }

    init() {
        const me = this;

        me.view.rowManager.on({
            beforetranslaterow : me.onBeforeTranslateRow,
            translaterow       : me.onTranslateRow,
            beforerowheight    : me.onBeforeRowHeightChange,
            thisObj            : me
        });

        me.view.on({
            beforetogglenode     : me.onBeforeToggleNode,
            togglenode           : me.onToggleNode,
            beforetoggleallnodes : me.onBeforeToggleAllNodes,
            toggleallnodes       : me.onToggleAllNodes,
            thisObj              : me
        });
    }

    //endregion

    //region Helpers

    get column() {
        return this.view.timeAxisColumn;
    }

    get timeAxis() {
        return this.view.timeAxis;
    }

    get timeAxisViewModel() {
        return this.view.timeAxisViewModel;
    }

    translateToPageCoordinate(x) {
        const element = this.column.subGridElement;
        return x + element.getBoundingClientRect().left - element.scrollLeft;
    }

    //endregion

    //Region View hooks

    refreshRows(reLayoutEvents) {
        if (reLayoutEvents) {
            this.cache.clear(this.refreshFromRerender);
            this.view.refreshFromRerender = false;
        }
    }

    // If we dragged an event outside of its rendered block, it will have been released
    // but releaseTimeSpanDiv won't hide it if it has b-dragging, so we have to
    // hide it now.
    onDragAbort(context) {
        if (this.view.currentOrientation.availableDivs.includes(context.element)) {
            context.element.classList.add('b-sch-released');
        }
    }

    onLocaleChange() {
        // Clear events in case they use date as part of displayed info
        this.cache.clear();
    }

    //endregion

    //region Layout & render

    /**
     * Converts a start/endDate into a MS value used when rendering the timeSpan
     * @private
     * @abstract
     */
    calculateMS(timespanRecord) {
        throw new Error('Implement in subclass');
    }

    /**
     * Gets timespan coordinates etc. Relative to containing row. If the timespan is outside of the zone in
     * which timespans are rendered, that is outside of the TimeAxis, or outside of the vertical zone in which timespans
     * are rendered, then `undefined` is returned.
     * @private
     * @param {Scheduler.model.TimeSpan} timeSpan TimeSpan record
     * @param {Core.data.Model} rowRecord Row record
     * @param {Boolean|Object} includeOutside Specify true to get boxes for timespans outside of the rendered zone in both
     * dimensions. This option is used when calculating dependency lines, and we need to include routes from timespans
     * which may be outside the rendered zone.
     * @param {Boolean} includeOutside.timeAxis Pass as `true` to include timespans outside of the TimeAxis's bounds
     * @param {Boolean} includeOutside.viewport Pass as `true` to include timespans outside of the vertical timespan viewport's bounds.
     * @returns {{event/task: *, left: number, width: number, start: (Date), end: (Date), startMs: number, endMs: number, startsOutsideView: boolean, endsOutsideView: boolean}}
     */
    getTimeSpanRenderData(timeSpan, rowRecord, includeOutside = false) {
        const
            me                     = this,
            { timeAxis }           = me,
            includeOutsideTimeAxis = includeOutside === true || includeOutside.timeAxis,
            includeOutsideViewport = includeOutside === true || includeOutside.viewport;

        // If timespan is outside the TimeAxis, give up trying to calculate a layout (Unless we're including timespans
        // outside our zone)
        if (includeOutsideTimeAxis || timeAxis.isTimeSpanInAxis(timeSpan)) {
            const view = me.view,
                row  = view.getRowById(rowRecord);

            // If row is outside the rendered block, we cannot compute a timespan layout.
            if (row || includeOutsideViewport) {
                // TODO: buffer size as config
                const
                    scrollTop      = view.scrollable.y,
                    viewportTop    = Math.max(scrollTop - 50, 0),
                    viewportBottom = Math.min(scrollTop + view._bodyRectangle.height + 50, view.virtualScrollHeight);

                // If the row is outside the zone in which we render timespans, give up trying to calculate a
                // layout (Unless we're including timespans outside our zone)
                if (includeOutsideViewport || (row.top < viewportBottom && (row.top + (row.height || row.lastHeight)) >= viewportTop)) {
                    const
                        pxPerMinute                    = me.timeAxisViewModel.getSingleUnitInPixels('minute'),
                        timespanStart                  = timeSpan.startDate,
                        timespanEnd                    = timeSpan.endDate || timespanStart, // Allow timespans to be rendered even when they are missing an end date
                        viewStartMS                    = timeAxis.startMS,
                        viewEndMS                      = timeAxis.endMS,
                        { startMS, endMS, durationMS } = me.calculateMS(timeSpan),
                        // These flags have two components because includeOutsideViewport
                        // means that we can be calculating data for events either side of
                        // the TimeAxis.
                        // The start is outside of the view if it's before *or after* the TimeAxis range.
                        // 1 set means the start is before the TimeAxis
                        // 2 set means the start is after the TimeAxis
                        // Either way, a truthy value means that the start is outside of the TimeAxis.
                        startsOutsideView              = startMS < viewStartMS  | ((startMS >  viewEndMS)   << 1),
                        // The end is outside of the view if it's before *or after* the TimeAxis range.
                        // 1 set means the end is after the TimeAxis
                        // 2 set means the end is before the TimeAxis
                        // Either way, a truthy value means that the end is outside of the TimeAxis.
                        endsOutsideView                = endMS   > viewEndMS    | ((endMS   <= viewStartMS) << 1),
                        durationMinutes                = durationMS / (1000 * 60),
                        width                          = endsOutsideView ? pxPerMinute * durationMinutes : null; //view.getCoordinateFromDate(viewStartMS + durationMS),

                    let endX = view.getCoordinateFromDate(endMS, {
                            local            : true,
                            respectExclusion : true,
                            isEnd            : true
                        }), startX, clippedStart = false, clippedEnd = false;

                    // If event starts outside of view, estimate where.
                    if (startsOutsideView) {
                        startX = (startMS - viewStartMS) / (1000 * 60) * pxPerMinute;
                    }
                    // Starts in view, calculate exactly
                    else {
                        // If end date is included in time axis but start date is not (when using time axis exclusions), snap start date to next included data
                        startX = view.getCoordinateFromDate(startMS, {
                            local              : true,
                            respectExclusion   : true,
                            isEnd              : false,
                            snapToNextIncluded : endX !== -1
                        });

                        clippedStart = startX === -1;
                    }

                    if (endsOutsideView) {
                        endX = startX + width;
                    }
                    else {
                        clippedEnd = endX === -1;
                    }

                    if (clippedEnd && !clippedStart) {
                        // We know where to start but not where to end, snap it (the opposite is already handled by the
                        // snapToNextIncluded flag when calculating startX above)
                        endX = view.getCoordinateFromDate(endMS, {
                            local              : true,
                            respectExclusion   : true,
                            isEnd              : true,
                            snapToNextIncluded : true
                        });
                    }

                    // If the element is very wide there's no point in displaying it all.
                    // Indeed the element may not be displayable at extremely large widths.
                    if (width > MAX_WIDTH) {
                        // The start is before the TimeAxis start
                        if (startsOutsideView === 1) {
                            // Both ends outside - spans TimeAxis
                            if (endsOutsideView === 1) {
                                startX = -100;
                                endX = view.timeAxisColumn.width + 100;
                            }
                            // End is in view
                            else {
                                startX = endX - MAX_WIDTH;
                            }
                        }
                        // The end is after, but the start is in view
                        else if (endsOutsideView === 1) {
                            endX = startX + MAX_WIDTH;
                        }
                    }

                    if (clippedStart && clippedEnd) {
                        // Both ends excluded, but there might be some part in between that should be displayed...
                        startX = view.getCoordinateFromDate(startMS, {
                            local              : true,
                            respectExclusion   : true,
                            isEnd              : false,
                            snapToNextIncluded : true,
                            max                : endMS
                        });

                        endX = view.getCoordinateFromDate(endMS, {
                            local              : true,
                            respectExclusion   : true,
                            isEnd              : true,
                            snapToNextIncluded : true,
                            min                : startMS
                        });

                        if (startX === endX) {
                            // Raise flag on instance meta to avoid duplicating this logic
                            timeSpan.instanceMeta(view).excluded = true;
                            // Excluded by time axis exclusion rules, render nothing
                            return null;
                        }
                    }

                    const data                           = {
                        [me.view.scheduledEventName] : timeSpan,
                        left                         : Math.min(startX, endX),
                        width                        : Math.abs(endX - startX),
                        start                        : timespanStart,
                        end                          : timespanEnd,
                        startMs                      : startMS,
                        endMs                        : endMS,
                        rowId                        : rowRecord.id,
                        startsOutsideView,
                        endsOutsideView,
                        clippedStart,
                        clippedEnd,
                        row
                    };

                    // If filling ticks we need to also keep datas MS values, since they are used for sorting timespans
                    if (me.view.fillTicks) {
                        data.dataStartMs = data.start.getTime();
                        data.dataEndMs = data.end.getTime();
                    }

                    // in ExtScheduler this is only checked when managedEventSizing is true, but we need top because of positioning
                    data.top = Math.max(0, view.resourceMargin);

                    if (view.managedEventSizing) {
                        // Timespan height should be at least 1px
                        data.height = Math.max(view.rowManager.rowHeight - (2 * view.resourceMargin), 1);
                    }

                    return data;
                }
            }
        }
    }

    onTranslateRow({ source : row }) {

    }

    onBeforeRowHeightChange(event) {
        const view = this.view,
            newHeight = event ? event.height : this.rowHeight;

        this.cache.clear();

        // TODO: should move out of here to scheduler
        if (view.foregroundCanvas) {
            view.element.classList.add('b-notransition');
            view.foregroundCanvas.style.fontSize = `${newHeight - view.resourceMargin * 2}px`;

            // We must force a style recalculation so that the next measurement of milestoneWidth
            // gets the new value.
            this._thisIsAUsedExpression(window.getComputedStyle(view.foregroundCanvas).fontSize);
            view.element.classList.remove('b-notransition');
        }

        // The Dependencies feature will have to recalculate its milestoneWidth when it is refreshed next.
        view.milestoneWidth = null;
    }

    onBeforeTranslateRow({ row }) {
        // Triggered before row is repurposed with new record on scroll.
        // Clear layout for the outgoing record.
        this.cache.clearRow(row.id);
    }

    onViewportResize() {
        // Force layout even if scroll positions have not changed.
        this.lastUpdateScrollPos = null;

        // Update the layout.
        this.update();
    }

    // /**
    //  * Called when all rows are rerendered, following a sort operation etc.
    //  * @private
    //  */
    // onBeforeFullRender() {
    //     this.cache.clear();
    // }

    positionTimeSpan(el, x, y) {
        const mode = this.view.eventPositionMode || 'translate';

        switch (mode) {
            case 'position':
                el.style.left = `${x}px`;
                el.style.top = `${y}px`;
                return;
            case 'translate':
                el.style.transform = `translate(${x}px,${y}px)`;
                return;
            case 'translate3d':
                el.style.transform = `translate3d(${x}px,${y}px,0)`;
        }
    }

    getTimeSpanDiv(timeSpanData) {
        const
            me                = this,
            { availableDivs } = me,
            { id, oldId }     = timeSpanData;

        if (me.view.eventScrollMode === 'move') {
            let wrapperElement;

            // Allow a TimeSpan to reuse the last div that was assigned to it so that transitions may be used when
            // editing an event. Will fallback to old id, to cover cases where id has changed since last update
            if (id) {
                wrapperElement = availableDivs.get(id) || availableDivs.get(oldId);
            }

            // Do not return it if it's in use by drag/drop or resize
            if (wrapperElement && !isEventElAvailable(wrapperElement)) {
                wrapperElement = null;
            }

            // No element which matches the requested element id available.
            // Just allocate one from the cache which is not being dragged or resized.
            if (!wrapperElement) {
                wrapperElement = availableDivs.find(isEventElAvailable);
            }

            if (wrapperElement) {
                availableDivs.remove(wrapperElement);
                wrapperElement.classList.remove('b-sch-released');
            }
            return wrapperElement;
        }
        return null;
    }

    releaseTimeSpanDiv(div, remainVisible = false) {
        const me = this,
            divStyle = div.style;

        // Some browsers do not blur on set to display:none, so releasing the active element
        // must *explicitly* move focus outwards to the view.
        if (!remainVisible && div === document.activeElement) {
            me.view.focusElement.focus();
        }

        if (me.view.eventScrollMode === 'move') {
            me.availableDivs.add(div);
            if (!remainVisible && isEventElAvailable(div)) {
                div.classList.add('b-sch-released');
            }
        }
        else {
            if (!remainVisible) {
                divStyle.opacity = 0;
                divStyle.pointerEvents = 'none';
                me.setTimeout(() => {
                    div.remove();
                    divStyle.opacity = 1;
                    divStyle.pointerEvents = '';
                }, 200);
            }
        }
    }

    updateElementId(element, newId) {
        const { availableDivs } = this;

        // Change the id, and move it to the end so that it won't be immediately
        // be consumed by the next request for an element, but is more likely to
        // remain to be found by its original owner TimeSpan
        if (availableDivs.includes(element)) {
            availableDivs.changeId(element, newId);
            // Used to be Collection.move(). Deleting and adding to end of Bags Set to mimic
            availableDivs.items.delete(element);
            availableDivs.items.add(element);
        }
    }

    triggerPaint(data, element) {
        throw new Error('Implement in subclass');
    }

    // forceNewElement is true for newly added events, to not "steal" an element that might be needed later on in the
    // same row (which triggers a transition from that location to the new)
    renderTimeSpan(data, cache, renderedTimeSpan, forceNewElement = false) {
        const
            me   = this,
            view = me.view;

        let {
                cls,
                id
            }              = data,
            wrapperElement = cache && cache.div,
            changedDiv     = false,
            isDragCreateProxy,
            trigger        = false,
            isRepaint      = false,
            innerElement   = cache && cache.eventEl;

        // TODO: this fn should not need to be called for events already in view, unless there is some update. might give a little better performance

        // No div assigned, reuse one if possible and such mode is used
        if (!forceNewElement && !wrapperElement && (wrapperElement = me.getTimeSpanDiv(data))) {
            // changedDiv means that we're using somebody else's element.
            changedDiv = wrapperElement.id !== id;
        }

        isDragCreateProxy = wrapperElement && wrapperElement.classList.contains('b-sch-dragcreator-proxy');

        // No div assigned or reusing some other TimeSpan's released div
        if (!wrapperElement || changedDiv) {
            // Reusing another TimeSpan's released element.
            // It needs to be emptied.
            if (changedDiv) {
                // If there's no .b-sch-event, then it could be a resourceTimeRange element
                // so just use firstElementChild.
                innerElement =  wrapperElement.innerElement || wrapperElement.firstElementChild;
                innerElement.innerHTML = '';
                innerElement.style.cssText = '';
                if (innerElement.nextSibling) {
                    elRange.setStartAfter(innerElement);
                    elRange.setEndAfter(wrapperElement.lastChild);
                    elRange.deleteContents();
                }
            }
            // New element needed
            else {
                [wrapperElement, innerElement] = DomHelper.createElement({
                    tabIndex : 0,

                    // adding inner div which will be rendered as the event
                    children : [{
                        className : `${me.innerElCls}`
                    }]
                }, true);
                // To not have to retrieve it from DOM later
                wrapperElement.innerElement = innerElement;
            }

            wrapperElement.id = id;
            // Add event/task id to wrappers dataset
            if (data[view.scheduledEventName + 'Id']) {
                wrapperElement.dataset[view.scheduledEventName + 'Id'] = data[view.scheduledEventName + 'Id'];
            }

            DomHelper.syncClassList(wrapperElement, data.wrapperCls);

            if (wrapperElement.parentNode !== view.foregroundCanvas) {
                view.foregroundCanvas.appendChild(wrapperElement);
            }

            // set all attributes
            DomHelper.syncClassList(innerElement, cls);
            if (data.style || data.internalStyle) {
                innerElement.style.cssText = (data.internalStyle || '') + (data.style || '');
            }

            // Only update content if the data block has new content in it
            if (data.body) {
                // Clone because this operation has to be repeatable
                innerElement.appendChild(data.body.cloneNode(true));
            }

            // positioning and sizing wrapper
            wrapperElement.style.cssText = `width:${data.width}px;height:${data.height}px;${data.wrapperStyle || ''}`;
            me.positionTimeSpan(wrapperElement, data.left, data.top);

            trigger = true;
        }
        // Reusing TimeSpan's own div - it's a repaint
        else {
            // has div, update attributes if needed.
            const old = (renderedTimeSpan && cache.div) ? renderedTimeSpan : emptyObject;

            innerElement = wrapperElement.querySelector(`.${me.innerElCls}`) || wrapperElement.firstElementChild;

            // We are updating an existing event here, it may have lifecycle classes
            // such as resizing, or terminals visible etc.
            // Surgically update the class list of the elements.
            DomHelper.syncClassList(wrapperElement, data.wrapperCls);
            DomHelper.syncClassList(innerElement, data.cls);

            // TODO: have to apply style on each update when specified, or store it and check if changed
            if (data.style) {
                innerElement.style.cssText = data.style;
            }
            // Clear style, might have changed from having a style to not having
            else {
                innerElement.style.cssText = '';
            }

            // Only update content if the data block has new content in it
            if (data.body) {
                const
                    oldInnerHTML = innerElement.innerHTML,
                    // TODO: Not in use? Should be used by labels? Percent bar in gantt? Store them in an array to avoid QSA
                    featureEls = innerElement.featureEls; //innerElement.querySelectorAll('[data-feature]');

                if (featureEls) {
                    featureEls.forEach(featureEl => data.body.appendChild(featureEl));
                }
                innerElement.innerHTML = '';

                // Clone because this operation has to be repeatable
                innerElement.appendChild(data.body.cloneNode(true));

                // trigger only if content changed
                trigger = innerElement.innerHTML !== oldInnerHTML;
            }

            // Wrapper element gets the focus class.
            if (document.activeElement && document.activeElement.id === data.id) {
                wrapperElement.classList.add(view.focusCls);
            }

            // updates to wrapper
            isRepaint = true;

            if (data.wrapperStyle) {
                wrapperElement.style.cssText += data.wrapperStyle;
            }

            if (old.left !== data.left || old.top !== data.top || data.style) {
                me.positionTimeSpan(wrapperElement, data.left, data.top);
                trigger = true;
            }
            if (old.width !== data.width || data.style) {
                wrapperElement.style.width = `${data.width}px`;
                trigger = true;
            }
            if (old.height !== data.height || data.style) {
                wrapperElement.style.height = `${data.height}px`;
                trigger = true;
            }
        }

        cache.div = wrapperElement;
        cache.eventEl = innerElement;

        if (trigger) {
            // If we are rendering to a drag-create proxy, it's not a repaint
            if (isRepaint && isDragCreateProxy) {
                isRepaint = false;
                wrapperElement.classList.remove('b-sch-dragcreator-proxy');
            }
            // trigger paint or repaint, for features to hook into
            me.triggerPaint(data, wrapperElement, isRepaint);
        }

        return wrapperElement;
    }

    /**
     * Adds an element for recycling to the event element recycling cache for the passed Event/Task if
     * there is no cache entry. Use this to prevent cache misses and the re-use by
     * event rendering of elements for other events which means that the element would have to be heavily
     * modified.
     * @param {Scheduler.model.TimeSpan} timeSpanRecord The event/task to create an element recycling cache entry for.
     * @param {Scheduler.model.ResourceModel} [resourceRecord] The associated resource record.
     * @param {HTMLElement} [element] The element to add to the element recycling cache under that id.
     */
    cacheTimeSpanElement(timeSpanRecord, resourceRecord, element) {
        const
            me                      = this,
            { availableDivs, view } = me,
            timeSpanRecordId        = view.getEventRenderId(timeSpanRecord, resourceRecord);

        if (!availableDivs.includes(timeSpanRecordId)) {
            if (element) {
                element.id = timeSpanRecordId;
                view.foregroundCanvas.appendChild(element);
            }
            else {
                element = DomHelper.createElement({
                    id       : timeSpanRecordId,
                    tabIndex : 0,

                    // adding inner div which will be rendered as the event
                    children : [{
                        className : `${me.innerElCls}`
                    }]
                });
            }
            availableDivs.add(element);
        }
    }

    updateRowTimeSpans(row, rowRecord, forceLayout = false, fromHorizontalScroll = false) {}

    renderer(renderData) {}

    updateFromHorizontalScroll(scrollLeft) {
        const
            me                  = this,
            view                = me.view,
            width               = view.timeAxisSubGrid.width,
            startDate           = view.getDateFromX(Math.max(0, scrollLeft - 100)),
            endDate             = view.getDateFromX(scrollLeft + width + 100) || view.timeAxis.endDate,
            x                   = me.view.timeAxisSubGrid.scrollable.x,
            lastUpdateScrollPos = me.lastUpdateScrollPos;

        // TODO: make buffer width a config, add in correct unit
        me.viewportStart = (startDate && startDate.getTime());
        me.viewportEnd = (endDate && endDate.getTime());

        // Only rerender all events if we have scrolled horizontally by at least half
        // of the buffer zone above which is 100 pixels.
        if (!lastUpdateScrollPos || Math.abs(x - lastUpdateScrollPos.x) > 50) {
            me.update();
        }

        // Update timeaxis header making it display the new dates
        view.timeView.range = { startDate, endDate };
    }

    update() {
        const
            me   = this,
            view = me.view;

        me.lastUpdateScrollPos = {
            x : view.timeAxisSubGrid.scrollable.x,
            y : view.scrollable.y
        };

        for (const row of view.rowManager) {
            const cell   = row.getCell(view.timeAxisColumn.id),
                cellData = DomDataStore.get(cell),
                record   = view.store.getById(cellData.id);

            if (record) {
                me.updateRowTimeSpans(cellData.row, record, false, true);
            }
        }
    }

    //endregion

    //region Dates

    getDateFromXY(xy, roundingMethod, local, allowOutOfRange = false) {
        let coord = xy[0];

        if (!local) {
            coord = this.translateToScheduleCoordinate(coord);
        }

        return this.timeAxisViewModel.getDateFromPosition(coord, roundingMethod, allowOutOfRange);
    }

    translateToScheduleCoordinate(x) {
        const pos = x - this.column.subGridElement.getBoundingClientRect().left;

        return pos + this.view.scrollLeft;
    }

    //endregion

    //region Store events

    /**
     * User toggled a node in a tree grid, events needs to be redrawn since they might appear/disappear
     * @private
     */
    onBeforeToggleNode({ record }) {
        const
            me         = this,
            childCount = record.children && record.children.length;

        // If there are no children to toggle, we must not clear below the
        // toggling line because there's going to be no upcoming data change to fix it.
        if (childCount) {
            // The node being toggled doesn't need events clearing.
            // It just has its expand/collapse icon toggled.
            let currentRowRecord = me.view.store.getNext(record);

            while (currentRowRecord && me.view.getRowFor(currentRowRecord)) {
                me.cache.clearRow(currentRowRecord.id);
                currentRowRecord = me.view.store.getNext(currentRowRecord.id);
            }
        }

        // Flag to force drawing all events within row buffer, needed since expanding rows do not give them their
        // height right away
        me.togglingNode = true;
    }

    onToggleNode() {
        // Ignore when toggling all nodes
        if (!this.togglingAllNodes) {
            this.togglingNode = false;
        }
    }

    onBeforeToggleAllNodes() {
        this.cache.clear();
        this.togglingAllNodes = this.togglingNode = true;
    }

    onToggleAllNodes() {
        this.togglingAllNodes = this.togglingNode = false;
    }

    // Row updated, clear cache keeping div to animate changes
    onRowRecordUpdate({ record }) {
        this.cache.clearRow(record.id, false, true);
    }

    // Row removed (resource/task), clear cache
    onRowRecordRemove({ records }) {
        // Records might be parents, always traversing makes sure children are also cleared. For leafs, traverse only
        // affects the node itself. If a parent and its children are removed at once, it will still work since clearing
        // cache again has no side effects
        records.forEach(record =>
            record.traverse(r => this.cache.clearRow(r.id))
        );
    }

    //endregion

    //region Region

    /**
     * Gets the Region, relative to the timeline view element, representing the passed row and optionally just for a
     * certain date interval.
     * @param {Core.data.Model} rowRecord The row record
     * @param {Date} startDate A start date constraining the region
     * @param {Date} endDate An end date constraining the region
     * @return {Core.helper.util.Rectangle} The Rectangle which encapsulates the row
     */
    getRowRegion(rowRecord, startDate, endDate) {
        const
            { view, column } = this,
            row              = view.getRowById(rowRecord.id);

        // might not be rendered
        if (!row) {
            return null;
        }

        let rowElement = row.getElement(column.region),
            taStart    = this.timeAxis.startDate,
            taEnd      = this.timeAxis.endDate,
            start      = startDate ? DateHelper.max(taStart, startDate) : taStart,
            end        = endDate ? DateHelper.min(taEnd, endDate) : taEnd,
            startX     = view.getCoordinateFromDate(start),
            endX       = view.getCoordinateFromDate(end, true, true),
            y          = row.top + view.verticalScroller.scrollTop,
            x          = Math.min(startX, endX),
            bottom     = y + rowElement.offsetHeight;

        return new Rectangle(x, y, Math.max(startX, endX) - x, bottom - y);
    }

    getVisibleDateRange() {
        const view        = this.view,
            scrollPos = view.timeAxisSubGrid.scrollable.x,
            width     = view.timeAxisSubGrid.width;

        return {
            startDate : view.getDateFromX(scrollPos),
            endDate   : view.getDateFromX(scrollPos + width) || view.timeAxis.endDate
        };
    }

    //endregion

    //region Dependency connectors

    /**
     * Gets displaying item start side
     *
     * @param {Scheduler.model.EventModel} eventRecord
     * @return {String} 'left' / 'right' / 'top' / 'bottom'
     */
    getConnectorStartSide(eventRecord) {}

    /**
     * Gets displaying item end side
     *
     * @param {Scheduler.model.EventModel} eventRecord
     * @return {String} 'left' / 'right' / 'top' / 'bottom'
     */
    getConnectorEndSide(eventRecord) {}

    //endregion
}
