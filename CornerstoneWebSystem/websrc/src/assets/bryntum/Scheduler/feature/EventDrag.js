import { base } from '../../Core/helper/MixinHelper.js';
import DragBase from './base/DragBase.js';
import DateHelper from '../../Core/helper/DateHelper.js';
import DomHelper from '../../Core/helper/DomHelper.js';
import Rectangle from '../../Core/helper/util/Rectangle.js';
import GridFeatureManager from '../../Grid/feature/GridFeatureManager.js';
import AssignmentModel from '../model/AssignmentModel.js';
import SchedulerFeatureDataLayer from './mixin/SchedulerFeatureDataLayer.js';
import DataAPI from '../data/api/DataAPI.js';

/**
 * @module Scheduler/feature/EventDrag
 */

const nullResourceArray = [null];

//TODO: relay events as in Dependencies. (drag -> eventdrag etc)
//TODO: shift to copy
//TODO: dragging of event that starts & ends outside of view

/**
 * Allows user to drag and drop events within the scheduler, to change startDate or resource assignment.
 *
 * This feature is **enabled** by default
 *
 * @example
 * // constrain drag to current resource
 * let scheduler = new Scheduler({
 *   features: {
 *     eventDrag: {
 *       constrainDragToResource: true
 *     }
 *   }
 * });
 *
 * @extends Scheduler/feature/base/DragBase
 * @demo Scheduler/basic
 * @externalexample scheduler/EventDrag.js
 */
export default class EventDrag extends base(DragBase).mixes(
    SchedulerFeatureDataLayer,
    DataAPI
) {
    //region Config

    static get $name() {
        return 'EventDrag';
    }

    static get defaultConfig() {
        return {
            /**
             * Template used to generate drag tooltip contents.
             * ```
             * const scheduler = new Scheduler({
             *   features : {
             *     eventDrag : {
             *       dragTipTemplate({eventRecord, startText}) {
             *         return `${eventRecord.name}: ${startText}`
             *       }
             *     }
             *   }
             * });
             * ```
             * @config {Function} dragTipTemplate
             * @param {Object} data Tooltip data
             * @param {Scheduler.model.EventModel} data.eventRecord
             * @param {Boolean} data.valid Currently over a valid drop target or not
             * @param {Date} data.startDate New start date
             * @param {Date} data.endDate New end date
             * @param {String} data.startText Formatted new start date
             * @param {String} data.endText Formatted new end date
             * @param {String} data.startClockHtml Pre-generated HTML to display startDate as clock/calendar
             * @param {String} data.endClockHtml Pre-generated HTML to display endDate as clock/calendar
             * @param {Object} data.dragData Detailed drag context
             * @returns {String}
             */

            /**
             * Set to true to only allow dragging events within the same resource.
             * @config {Boolean}
             * @default
             */
            constrainDragToResource : false,

            /**
             * Set to true to only allow dragging events to different resources, and disallow rescheduling by dragging.
             * @config {Boolean}
             * @default
             */
            constrainDragToTimeSlot : false,

            /**
             * An empty function by default, but provided so that you can perform custom validation on
             * the item being dragged. This function is called during the drag and drop process and also after the drop is made.
             * Return true if the new position is valid, false to prevent the drag.
             * @param {Object} context A drag drop context object
             * @param {Date} context.startDate New start date
             * @param {Date} context.endDate New end date
             * @param {Scheduler.model.EventModel[]} context.draggedRecords Event records which were dragged
             * @param {Scheduler.model.ResourceModel} context.newResource New resource record
             * @param {Event} event The event object
             * @return {Boolean} `true` if this validation passes
             * @config {Function}
             */
            validatorFn : () => {},

            /**
             * The `this` reference for the validatorFn
             * @config {Object}
             */
            validatorFnThisObj : null,

            /**
             * When the host Scheduler is `{@link Scheduler.view.mixin.EventSelection#config-multiEventSelect multiEventSelect}: true`
             * then, there are two modes of dragging *within the same Scheduler*.
             *
             * Non unified means that all selected events are dragged by the same number of resource rows.
             *
             * Unified means that all selected events are collected together and dragged as one, and are all dropped
             * on the same targeted resource row at the same targeted time.
             * @config {Boolean}
             * @default false
             */
            unifiedDrag : null
        };
    }

    //endregion

    //region Events

    /**
     * Fired on the owning Scheduler to allow implementer to prevent immediate finalization by setting `data.context.async = true`
     * in the listener, to show a confirmation popup etc
     * ```
     *  scheduler.on('beforeeventdropfinalize', ({context}) => {
     *      context.async = true;
     *      setTimeout(() => {
     *          // async code don't forget to call finalize
     *          context.finalize();
     *      }, 1000);
     *  })
     * ```
     * @event beforeEventDropFinalize
     * @param {Scheduler.view.Scheduler} source Scheduler instance
     * @param {Object} context
     * @param {Boolean} context.async Set true to handle dragdrop asynchronously (e.g. to wait for user
     * @param {Scheduler.model.EventModel} context.targetEventRecord Event record for drop target
     * @param {Scheduler.model.ResourceModel} context.newResource Resource record for drop target
     * confirmation)
     * @param {Function} context.finalize Call this method to finalize dragdrop. This method accepts one
     * argument: pass true to update records, or false, to ignore changes
     */

    /**
     * Fired on the owning Scheduler after event drop
     * @event afterEventDrop
     * @param {Scheduler.view.Scheduler} source
     * @param {Scheduler.model.EventModel[]} eventRecords
     * @param {Boolean} valid
     * @param {Object} context
     */

    /**
     * Fired on the owning Scheduler when an event is dropped
     * @event eventDrop
     * @param {Scheduler.view.Scheduler} source
     * @param {Scheduler.model.EventModel[]} eventRecords
     * @param {Boolean} isCopy
     * @param {Object} context
     * @param {Scheduler.model.EventModel} context.targetEventRecord Event record for drop target
     * @param {Scheduler.model.ResourceModel} context.newResource Resource record for drop target
     */

    /**
     * Fired on the owning Scheduler before event dragging starts. Return false to prevent the action
     * @event beforeEventDrag
     * @param {Scheduler.view.Scheduler} source
     * @param {Scheduler.model.EventModel} eventRecord
     * @param {Object} context
     */

    /**
     * Fired on the owning Scheduler when event dragging starts
     * @event eventDragStart
     * @param {Scheduler.view.Scheduler} source
     * @param {Scheduler.model.EventModel[]} eventRecords
     * @param {Object} context
     */

    /**
     * Fired on the owning Scheduler when event is dragged
     * @event eventDrag
     * @param {Scheduler.view.Scheduler} source
     * @param {Scheduler.model.EventModel[]} eventRecords
     * @param {Date} startDate
     * @param {Date} endDate
     * @param {Scheduler.model.ResourceModel} newResource
     * @param {Object} context
     */

    /**
     * Fired on the owning Scheduler after an event drag operation has been aborted
     * @event eventDragAbort
     * @param {Scheduler.view.Scheduler} source
     * @param {Scheduler.model.EventModel[]} eventRecords
     * @param {Object} context
     */
    //endregion

    //region Data layer

    // Deprecated. Use this.client instead
    get scheduler() {
        return this.client;
    }

    // Deperecated. Use this.eventStore instead
    get store() {
        return this.eventStore;
    }

    //endregion

    //region Drag events

    isElementDraggable(el, event) {
        const
            { scheduler }   = this,
            eventElement    = DomHelper.up(el, scheduler.eventSelector),
            { eventResize } = scheduler.features;

        if (!eventElement || this.disabled) {
            return false;
        }

        // displaying something resizable within the event?
        if (el.matches('[class$="-handle"]')) {
            return false;
        }

        const eventRecord = scheduler.resolveEventRecord(eventElement);

        // using EventResize and over a virtual handle?
        // Milestones cannot be resized
        if (eventResize && !eventRecord.isMilestone && eventResize.resize.overAnyHandle(event, eventElement)) {
            return false;
        }

        return true;
    }

    triggerEventDrag(dd, start) {
        // If there has been a change...
        if (dd.startDate - start !== 0 || dd.newResource !== dd.resourceRecord) {
            this.scheduler.trigger('eventDrag', {
                eventRecords : dd.draggedRecords,
                startDate    : dd.startDate,
                endDate      : dd.endDate,
                newResource  : dd.newResource,
                context      : dd
            });
        }
    }

    onDragStart({ context, event }) {
        const eventContextMenuFeature = this.client.features.eventContextMenu;

        super.onDragStart({ context, event });

        // If this is a touch action, hide the context menu which may have shown
        if (eventContextMenuFeature) {
            eventContextMenuFeature.hideContextMenu(false);
        }
    }

    //endregion

    //region Finalization & validation

    /**
     * Checks if an event can be dropped on the specified resource.
     * @private
     * @returns {Boolean} Valid (true) or invalid (false)
     */
    isValidDrop(dragData) {
        const { newResource, resourceRecord } = dragData;
        let sourceRecord = dragData.draggedRecords[0];

        // Not allowed to drop an event to group header
        if (newResource.meta.specialRow) {
            return false;
        }

        // Not allowed to assign an event twice to the same resource -
        // which might happen when we deal with an assignment store
        if (resourceRecord !== newResource) {
            // if we operate assignments
            if (sourceRecord instanceof AssignmentModel) {
                sourceRecord = this.dataApi.getAssignmentEvent({ assignment : sourceRecord, eventStore : this.eventStore });
                return !this.dataApi.isAssignmentForResource({ assignment : sourceRecord, resource : newResource, resourceStore : this.resourceStore });
            }
            else {
                return !this.dataApi.isEventAssignedToResource({ event : sourceRecord, resource : newResource, resourceStore : this.resourceStore, assignmentStore : this.assignmentStore });
            }
        }

        return true;
    }

    checkDragValidity(dragData, event) {
        const
            me        = this,
            scheduler = this.currentOverClient;

        let result = me.dragData.context.valid;

        if (result) {
            // First make sure DragHelper thinks it's a valid drag, then scheduler domain checks
            if (!scheduler.allowOverlap && !scheduler.isDateRangeAvailable(
                dragData.startDate,
                dragData.endDate,
                dragData.draggedRecords[0],
                dragData.newResource
            )) {
                result = {
                    valid   : false,
                    message : me.L('eventOverlapsExisting')
                };
            }
            else {
                result = me.validatorFn.call(
                    me.validatorFnThisObj || me,
                    dragData,
                    event
                );
            }
        }

        return result;
    }

    //endregion

    //region Update records

    /**
     * Update events being dragged.
     * @private
     * @param context Drag data.
     */
    updateRecords(context) {
        const
            me              = this,
            fromScheduler   = me.scheduler,
            toScheduler     = me.currentOverClient,
            copyKeyPressed  = false, //me.isCopyKeyPressed(),
            {
                assignmentStore,
                eventStore
            } = fromScheduler,
            {
                draggedRecords
            } = context;

        let result;

        // Move event to the correct event Store in case of dragging across multiple schedulers
        // TOODO @mats, what if these are Assignments? See "Multiple assignment mode" below.
        // Both Schedulers would have to have assignmentStores.
        if (eventStore !== toScheduler.eventStore) {
            // Removing deassigns events from their resources
            eventStore.remove(draggedRecords);

            // This will not affect the UI because the events are not assigned to any resource
            toScheduler.eventStore.add(draggedRecords);
        }

        // Multiple assignment mode
        if (assignmentStore) {
            if (toScheduler !== fromScheduler) {
                throw new Error('Assignments cannot be dragged cross-scheduler');
            }
            result = me.updateRecordsMultipleAssignmentMode(fromScheduler, toScheduler, context, copyKeyPressed);
        }
        // Single assignment mode
        else {
            result = me.updateRecordsSingleAssignmentMode(fromScheduler, toScheduler, context, copyKeyPressed);
        }

        // Tell the world there was a successful drop
        toScheduler.trigger('eventDrop', {
            eventRecords         : draggedRecords,
            isCopy               : copyKeyPressed,
            event                : context.browserEvent,
            targetEventRecord    : context.targetEventRecord,
            targetResourceRecord : context.newResource,
            context
        });

        return result;
    }

    /**
     * Update records being dragged, scheduler mode. Sets resource and start date.
     * @private
     */
    updateRecordsSingleAssignmentMode(fromScheduler, toScheduler, context, copy) {
        // The code is written to emit as few store events as possible
        const
            me                      = this,
            isCrossScheduler        = (fromScheduler !== toScheduler),
            {
                eventStore: fromEventStore,
                resourceStore
            }                       = fromScheduler,
            {
                draggedRecords,
                timeDiff,
                resourceRecord : fromResource,
                newResource    : toResource
            }                       = context,
            unifiedDrag             = me.unifiedDrag || (isCrossScheduler && draggedRecords.length > 1),
            toAdd = [],
            // By how many resource rows has the drag moved.
            indexDiff   = me.constrainDragToResource ? 0 : resourceStore.indexOf(fromResource) - resourceStore.indexOf(toResource),
            event1Date  = me.adjustStartDate(draggedRecords[0].startDate, timeDiff);

        let updated;

        draggedRecords.forEach((draggedEvent, i) => {
            const
                eventBar = context.eventBarEls[i],
                oldGeneration = draggedEvent.generation,
                // grabbing resources early, since after ".copy()" the record won't belong to any store
                // and ".getResources()" won't work. If it's a move to another scheduler, ensure the
                // array still has a length. The process function will do an assign as opposed
                // to a reassign
                relatedResources = isCrossScheduler ? nullResourceArray : draggedEvent.resources;

            // If changing resource, the element wont be found in SchedulerStores#onEventBeforeCommit and thus the
            // committing cls wont be applied. Apply it here, in case we are using a backend. If we are not, it will be
            // replaced anyway on the immediate redraw
            eventBar.querySelector(fromScheduler.eventInnerSelector).classList.add(fromScheduler.committingCls);

            if (copy) {
                draggedEvent = draggedEvent.fullCopy(null);
                toAdd.push(draggedEvent);
            }
            else if (fromEventStore !== toScheduler.eventStore) {
                // Removing deassigns events from their resources
                fromEventStore.remove(draggedRecords);

                // This will not affect the UI because the events are not assigned to any resource
                toScheduler.eventStore.add(draggedRecords);

                // Make the event mapper reuse this element
                draggedEvent.instanceMeta(toScheduler).fromDragProxy = true;
            }

            // Process original dragged record
            draggedEvent.beginBatch();

            // calculate new startDate (and round it) based on timeDiff
            let newStartDate = unifiedDrag ? event1Date : me.adjustStartDate(draggedEvent.startDate, timeDiff);

            (indexDiff !== 0 || unifiedDrag || isCrossScheduler) && relatedResources.length && relatedResources.forEach(r => {
                let newResource = toResource;

                // If not dragging events as a unified block, distribute each to a new resource
                // using the same offset as the dragged event.
                if (!unifiedDrag && !isCrossScheduler) {
                    let newIndex = resourceStore.indexOf(r) - indexDiff;

                    if (newIndex < 0) {
                        newIndex = 0;
                    }
                    else if (newIndex >= resourceStore.getCount()) {
                        newIndex = resourceStore.getCount() - 1;
                    }

                    newResource = resourceStore.getAt(newIndex);
                }

                if (r) {
                    draggedEvent.reassign(r, newResource);
                }
                else {
                    draggedEvent.assign(newResource);
                }

                if (toScheduler.mode !== 'vertical') {
                    const
                        isRendered = toScheduler.rowManager.getRowFor(newResource) && toScheduler.isInTimeAxis(draggedEvent),
                        newId = isRendered ? toScheduler.getEventRenderId(draggedEvent, newResource) : null;

                    // Ensure the element gets preferentially reused for its own new render
                    // when batching of its changes is ended and it fires events through its eventStore
                    if (newId) {
                        // If it's cross-scheduler, the element has to be moved into the element
                        // recycling cache of the toScheduler, and positioned in its foregroundCanvas
                        if (isCrossScheduler) {
                            const elRect = Rectangle.from(context.context.element, toScheduler.foregroundCanvas, true),
                                clone = context.context.element.cloneNode(true);

                            // Ensure that after inserting the dragged element clone into the toScheduler's foregoundCanvas
                            // it's at the same visual position that it was dragged to.
                            DomHelper.setTranslateXY(clone, elRect.x, elRect.y);
                            clone.classList.remove('b-first-render');
                            clone.classList.remove('b-active');
                            clone.classList.remove('b-drag-proxy');
                            clone.classList.remove('b-dragging');

                            // This puts a clone of the dragged element into the toScheduler's recycling cache
                            // *AND* inserts it into the toScheduler's foregroundCanvas.
                            toScheduler.currentOrientation.cacheTimeSpanElement(draggedEvent, newResource, clone);
                        }
                        else {
                            toScheduler.currentOrientation.releaseTimeSpanDiv(eventBar, true);
                            toScheduler.currentOrientation.updateElementId(eventBar, newId);
                        }
                    }
                    // If the event has been moved out of rendering, just discard the element
                    else {
                        eventBar.remove();
                    }
                }
            });

            draggedEvent.setStartDate(newStartDate, true, fromEventStore.skipWeekendsDuringDragDrop);

            draggedEvent.endBatch();

            // We need to know whether we have successfully made an update in order to
            // sync the dragContext's valid flag.
            if (draggedEvent.generation !== oldGeneration) {
                updated = true;
            }
        });

        if (toAdd.length) {
            const count = fromEventStore.count;
            fromEventStore.add(toAdd);
            if (fromEventStore.count !== count) {
                updated = true;
            }
        }

        if (!updated) {
            context.valid = false;
        }
    }

    /**
     * Update records being dragged, gantt mode. Sets resource and start date.
     * @private
     */
    updateRecordsMultipleAssignmentMode(fromScheduler, toScheduler, context, copy) {
        const
            me               = this,
            isCrossScheduler = (fromScheduler !== toScheduler),
            { eventStore }   = fromScheduler,
            {
                draggedRecords,
                timeDiff,
                resourceRecord : fromResource,
                newResource    : toResource
            }                = context,
            // In case multiSelect is true, several assignments to one event may be processed here. We will store
            // ids of processed events here to avoid setting incorrect start date
            handledEventsMap = {};

        let updated;

        draggedRecords.forEach((assignment, i) => {
            const
                event         = assignment.event,
                eventBar      = context.eventBarEls[i],
                oldGeneration = event.generation,
                newId         = fromScheduler.getEventRenderId(assignment.event, toResource);

            if (handledEventsMap[event.id]) {
                return;
            }

            handledEventsMap[event.id] = true;

            // Ensure the element gets preferentially reused for its own new render
            if (newId) {
                // If it's cross-scheduler, the element has to be moved into the element
                // recycling cache of the toScheduler, and positioned in its foregroundCanvas
                if (isCrossScheduler) {
                    toScheduler.currentOrientation.cacheTimeSpanElement(event, eventBar);
                }
                else {
                    toScheduler.currentOrientation.updateElementId(eventBar, newId);
                }
            }

            event.setStartDate(me.adjustStartDate(event.startDate, timeDiff), true, eventStore.skipWeekendsDuringDragDrop);

            // if we dragged the event to a different resource
            if (fromResource !== toResource) {
                if (copy) {
                    event.assign(toResource);
                }
                else if (!event.isAssignedTo(toResource)) {
                    event.reassign(assignment.resource, toResource);
                }
                else {
                    event.unassign(assignment.resource);
                }
            }

            // We need to know whether we have successfully made an update in order to
            // sync the dragContext's valid flag.
            updated = updated || (assignment.resource !== fromResource) || (event.generation !== oldGeneration);
        });

        if (!updated) {
            context.valid = false;
        }
    }

    //endregion

    //region Drag data

    getProductDragContext(dd) {
        const targetEventRecord = this.scheduler.resolveEventRecord(dd.browserEvent.target);

        let newResource;

        if (this.constrainDragToResource) {
            newResource = dd.resourceRecord;
        }
        else if (!this.constrainDragToTimeline) {
            // If we're dragging freely on the page, require to drag onto a resource row always
            newResource = this.resolveResource();
        }
        else {
            newResource = this.resolveResource() || dd.newResource || dd.resourceRecord;
        }

        return {
            valid : Boolean(newResource),
            newResource,
            targetEventRecord
        };
    }

    setupProductDragData(info) {
        const
            me                 = this,
            scheduler          = me.scheduler,
            element            = info.grabbed,
            eventRecord        = scheduler.resolveEventRecord(element),
            resourceRecord     = scheduler.resolveResourceRecord(element),
            assignmentRecord   = scheduler.resolveAssignmentRecord(element),
            eventRegion        = Rectangle.from(element),
            draggedRecords     = [assignmentRecord || eventRecord],
            eventBarEls        = [];

        if (me.constrainDragToResource && !resourceRecord) {
            throw new Error('Resource could not be resolved for event: ' + eventRecord.id);
        }

        const dateConstraints = scheduler.getDateConstraints(me.constrainDragToResource ? resourceRecord : null, eventRecord);

        if (me.constrainDragToTimeline) {
            me.setupConstraints(
                scheduler.getScheduleRegion(me.constrainDragToResource ? resourceRecord : null, eventRecord),
                eventRegion,
                scheduler.timeAxisViewModel.snapPixelAmount,
                Boolean(dateConstraints)
            );
        }

        // We multi drag other selected events if multiEventSelect is set and
        // (the dragged event is already selected, or the ctrl key is pressed)
        if (scheduler.multiEventSelect && (scheduler.isEventSelected(draggedRecords[0]) || me.drag.startEvent.ctrlKey)) {
            draggedRecords.push.apply(draggedRecords, me.getRelatedRecords(assignmentRecord || eventRecord));
        }

        // Collecting all elements to drag
        draggedRecords.forEach(r => {
            let eventBarEl;

            if (r instanceof AssignmentModel) {
                eventBarEl = scheduler.getElementFromEventRecord(r.event, r.resource);
            }
            else {
                eventBarEl = scheduler.getElementFromEventRecord(r, r.resource);
            }

            // It's selected but unrendered. This happens when multi-selected events are dragged such
            // that on drop, they are outside of the timeline or the rendered block, and become unrendered.
            // Users will expect the event to be "there" so we have to bring it into existence
            // just for the drag.
            if (!eventBarEl) {
                const rd = scheduler.generateTplData(r, r.resource, { timeAxis : true, viewport : true });

                rd.top = rd.row ? (rd.top + rd.row.top) : scheduler.getResourceEventBox(r, r.resource, true).top;
                eventBarEl = scheduler.currentOrientation.renderTimeSpan(rd, {}, null, true);

                // Make it be reused after drag
                scheduler.currentOrientation.availableDivs.add(eventBarEl);
                eventBarEl = eventBarEl.innerElement;
            }

            eventBarEls.push(eventBarEl);
        });

        // What is dragged is the wrapper.
        eventBarEls.forEach((el, i) => eventBarEls[i] = el.parentNode);

        return { record : eventRecord, dateConstraints, eventBarEls, draggedRecords };
    }

    /**
     * Initializes drag data (dates, constraints, dragged events etc). Called when drag starts.
     * @private
     * @param info
     * @param event
     * @returns {*}
     */
    getDragData(info, event) {
        return Object.assign(super.getDragData(info, event), {
            resourceRecord : this.scheduler.resolveResourceRecord(info.grabbed)
        });
    }

    /**
     * Provide your custom implementation of this to allow additional selected records to be dragged together with the original one.
     * @param {Scheduler.model.EventModel} eventRecord The eventRecord about to be dragged
     * @return {Scheduler.model.EventModel[]} An array of event records to drag together with the original event
     */
    getRelatedRecords(eventRecord) {
        return this.scheduler.selectedEvents.filter(selectedRecord => selectedRecord !== eventRecord && selectedRecord.isDraggable);
    }

    /**
     * Get correct axis coordinate depending on schedulers mode (horizontal -> x, vertical -> y). Also takes milestone
     * layout into account.
     * @private
     * @param {Scheduler.model.EventModel} eventRecord Record being dragged
     * @param {HTMLElement} element Element being dragged
     * @param {Number[]} coord XY coordinates
     * @returns {Number|Number[]} X,Y or XY
     */
    getCoordinate(eventRecord, element, coord) {
        const scheduler = this.currentOverClient;

        if (scheduler.isHorizontal) {
            let x = coord[0];

            // Adjust coordinate for milestones if using a layout mode, since they are aligned differently than events
            if (scheduler.milestoneLayoutMode !== 'default' && eventRecord.isMilestone) {
                switch (scheduler.milestoneAlign) {
                    case 'center':
                        x += element.offsetWidth / 2;
                        break;
                    case 'end':
                        x += element.offsetWidth;
                        break;
                }
            }

            return x;
        }
        else {
            let y = coord[1];
            // Adjust coordinate for milestones if using a layout mode, since they are aligned differently than events
            if (scheduler.milestoneLayoutMode !== 'default' && eventRecord.isMilestone) {
                switch (scheduler.milestoneAlign) {
                    case 'center':
                        y += element.offsetHeight / 2;
                        break;
                    case 'end':
                        y += element.offsetHeight;
                        break;
                }
            }

            return y;
        }
    }

    /**
     * Get resource record occluded by the drag proxy.
     * @private
     * @returns {Scheduler.model.ResourceModel}
     */
    resolveResource() {
        const
            me = this,
            client = me.currentOverClient,
            { isHorizontal } = client,
            { context } = me.dragData,
            element = context.dragProxy || context.element,
            scrollerElement = client.timeAxisSubGrid.virtualScrollerElement,
            // Page coords for elementFromPoint
            pageRect    = Rectangle.from(element, null, true),
            x           = isHorizontal ? context.clientX : pageRect.center.x,
            y           = (client.isVertical || me.unifiedDrag) ? context.clientY : pageRect.center.y,
            // Local coords to resolve resource in vertical
            localRect   = Rectangle.from(element, me.currentOverClient.timeAxisSubGridElement, true),
            { x : lx, y : ly } = localRect.center;

        // This is benchmarked as the fastest way to find a Grid Row from a viewport Y coordinate
        // so use it in preference to elementFromPoint (which causes a forced synchonous layout) in horiontal mode.
        if (isHorizontal) {
            const row = client.rowManager.getRowAt(y);
            if (row) {
                return client.resourceStore.getAt(row.dataIndex);
            }
        }

        // Do not find the drag element at the x, y position.
        element.style.pointerEvents = 'none';

        // Do not find the horizontal scrollbar either
        if (scrollerElement) {
            scrollerElement.style.display = 'none';
        }
        const node = DomHelper.elementFromPoint(x, y);
        element.style.pointerEvents = '';
        if (scrollerElement) {
            scrollerElement.style.display = '';
        }

        // If we found an element there, and it can be used to resolve a resourceRecord, use that
        return node && client.resolveResourceRecord(node, [lx, ly]);
    }

    //endregion

    //region Other stuff

    adjustStartDate(startDate, timeDiff) {
        const scheduler = this.currentOverClient;

        return scheduler.timeAxis.roundDate(new Date(startDate - 0 + timeDiff), scheduler.snapRelativeToEventStartDate ? startDate : false);
    }

    resolveStartEndDates(proxyRect) {
        const
            scheduler     = this.currentOverClient,
            dd            = this.dragData;

        let { start : startDate, end : endDate } = scheduler.getStartEndDatesFromRectangle(proxyRect, 'round', dd.duration);

        if (!dd.startsOutsideView) {
            // Make sure we didn't target a start date that is filtered out, if we target last hour cell (e.g. 21:00) of
            // the time axis, and the next tick is 08:00 following day. Trying to drop at end of 21:00 cell should target start of next cell
            if (startDate && !scheduler.timeAxis.dateInAxis(startDate, false)) {
                const tick = scheduler.timeAxis.getTickFromDate(startDate);

                if (tick) {
                    startDate = scheduler.timeAxis.getDateFromTick(tick);
                }
            }

            endDate = DateHelper.add(startDate, dd.duration, 'ms');
        }
        else if (!dd.endsOutsideView) {
            startDate = DateHelper.add(endDate, -dd.duration, 'ms');
        }

        return {
            startDate,
            endDate
        };
    }

    //endregion
}

GridFeatureManager.registerFeature(EventDrag, true, 'Scheduler');
