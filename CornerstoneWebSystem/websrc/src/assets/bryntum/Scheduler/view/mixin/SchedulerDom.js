import Base from '../../../Core/Base.js';
import DomHelper from '../../../Core/helper/DomHelper.js';

/**
 * @module Scheduler/view/mixin/SchedulerDom
 */

/**
 * Mixin with EventModel and ResourceModel <-> HTMLElement mapping functions
 *
 * @mixin
 */
export default Target => class SchedulerDom extends (Target || Base) {
    //region Get

    /**
     * Returns a single HTMLElement representing an event record assigned to a specific resource.
     * @param {Scheduler.model.AssignmentModel} assignmentRecord An assignment record
     * @return {HTMLElement} The element representing the event record
     */
    getElementFromAssignmentRecord(assignmentRecord) {
        return this.currentOrientation.getElementFromEventRecord(assignmentRecord.event, assignmentRecord.resource);
    }

    /**
     * Returns a single HTMLElement representing an event record assigned to a specific resource.
     * @param {Scheduler.model.EventModel} eventRecord An event record
     * @param {Scheduler.model.ResourceModel} resourceRecord A resource record
     * @return {HTMLElement} The element representing the event record
     */
    getElementFromEventRecord(eventRecord, resourceRecord) {
        return this.currentOrientation.getElementFromEventRecord(eventRecord, resourceRecord);
    }

    /**
     * Returns all the HTMLElements representing an event record.
     *
     * @param {Scheduler.model.EventModel} eventRecord An event record
     * @param {Scheduler.model.ResourceModel} [resourceRecord] A resource record
     *
     * @return {HTMLElement[]} The element(s) representing the event record
     */
    getElementsFromEventRecord(eventRecord, resourceRecord) {
        return this.currentOrientation.getElementsFromEventRecord(eventRecord, resourceRecord);
    }

    /**
     * Returns the event id for a DOM id
     * @private
     * @param {String} id The id of the DOM node
     * @return {String} An event record (internal) id
     */
    getEventIdFromDomNodeId(id) {
        const eventId = id.substring(this.eventPrefix.length).split('-')[0];

        // id format is "PREFIX"-eventid-resourceid[-part]
        // do not want - from other sources in it, replace with ._.
        // TODO: come up with something risk free
        return eventId && eventId.replace(/\._\./g, '-');
    }

    /**
     * Returns the event record for a DOM id
     * @param {String} id The id of the DOM node
     * @return {Scheduler.model.EventModel} The event record
     */
    getEventRecordFromDomId(id) {
        id = this.getEventIdFromDomNodeId(id);
        return this.eventStore.getById(id);
    }

    /**
     * Returns a resource id for a DOM id
     * @private
     * @param {String} id An id of an event DOM node
     * @return {String} A resource record (internal) id
     */
    getResourceIdFromDomNodeId(id) {
        const resourceId = id.substring(this.eventPrefix.length).split('-')[1];

        // id format is "PREFIX"-eventid-resourceid[-part]
        // do not want - from other sources in it, replace with ._.
        // TODO: come up with something risk free
        return resourceId && resourceId.replace(/\._\./g, '-');
    }

    /**
     * Returns a resource record for a DOM id
     * @param {String} id An id of an event DOM node
     * @return {Scheduler.model.ResourceModel} A resource record
     */
    getResourceRecordFromDomId(id) {
        id = this.getResourceIdFromDomNodeId(id);
        return this.resourceStore.getById(id);
    }

    //endregion

    //region Resolve

    /**
     * Resolves the resource based on a dom element or event. In vertical mode, if resolving from an element higher up in
     * the hierarchy than event elements, then it is required to supply an coordinates since resources are virtual
     * columns.
     * @param {HTMLElement|Event} elementOrEvent The HTML element or DOM event to resolve a resource from
     * @param {Number[]} [xy] X and Y coordinates, required in some cases in vertical mode, disregarded in horizontal
     * @return {Scheduler.model.ResourceModel} The resource corresponding to the element, or null if not found.
     */
    resolveResourceRecord(elementOrEvent, xy) {
        return this.currentOrientation.resolveRowRecord(elementOrEvent, xy);
    }

    /**
     * Returns the event record for a DOM element
     * @param {HTMLElement} element The DOM node to lookup
     * @return {Scheduler.model.EventModel} The event record
     */
    resolveEventRecord(element) {
        element = DomHelper.up(element, this.eventSelector);

        if (element) {
            if (element.dataset.eventId) {
                return this.eventStore.getById(element.dataset.eventId);
            }

            if (element.dataset.assignmentId) {
                return this.assignmentStore.getById(element.dataset.assignmentId).event;
            }
        }

        return null;
    }

    // Used by shared features to resolve an event or task
    resolveTimeSpanRecord(element) {
        return this.resolveEventRecord(element);
    }

    /**
     * Returns an assignment record for a DOM element
     * @param {HTMLElement} element The DOM node to lookup
     * @return {Scheduler.model.AssignmentModel} The assignment record
     */
    resolveAssignmentRecord(element) {
        const { assignmentStore } = this.eventStore;

        let assignment = null;

        if (assignmentStore) {
            const
                eventElement = DomHelper.up(element, this.eventSelector),
                event        = this.resolveEventRecord(eventElement),
                resource     = this.resolveResourceRecord(eventElement);

            if (event && resource) {
                assignment = assignmentStore.getAssignmentForEventAndResource(event, resource);
            }
        }

        return assignment;
    }

    //endregion

    // Decide if a record is inside a collapsed tree node, or inside a collapsed group (using grouping feature)
    isRowVisible(resourceRecord) {
        // records in collapsed groups/brances etc are removed from processedRecords
        return this.store.indexOf(resourceRecord) >= 0;
    }

    /**
     * Determines width of a milestones label. How width is determined is decided by configuring Scheduler#milestoneLayoutMode.
     * Please note that currently text width is always determined using EventModel#name.
     * @param {Scheduler.model.EventModel} eventRecord
     * @returns {Number}
     */
    getMilestoneLabelWidth(eventRecord) {
        const me = this,
            mode = me.milestoneLayoutMode;

        if (mode === 'measure') {
            const element = me.milestoneMeasureElement || (me.milestoneMeasureElement = DomHelper.createElement({
                className : 'b-sch-event-wrap b-milestone-wrap b-measure',
                children  : [{
                    className : 'b-sch-event b-milestone',
                    html      : `<label></label>`
                }],
                parent : me.foregroundCanvas
            }));

            element.firstElementChild.firstElementChild.innerHTML = eventRecord.name;

            return element.offsetWidth;
        }

        if (mode === 'estimate') {
            return Math.max(eventRecord.name.length * me.milestoneCharWidth, me.milestoneMinWidth);
        }

        if (mode === 'data') {
            return Math.max(eventRecord.milestoneWidth, me.milestoneMinWidth);
        }

        return 0;
    }

    set milestoneLayoutMode(mode) {
        const me = this;

        me._milestoneLayoutMode = mode;

        me.element.classList[mode !== 'default' ? 'add' : 'remove']('b-sch-layout-milestones');
        me.refresh();
    }

    get milestoneLayoutMode() {
        return this._milestoneLayoutMode;
    }

    set milestoneAlign(align) {
        this._milestoneAlign = align;

        this.refresh();
    }

    get milestoneAlign() {
        return this._milestoneAlign;
    }

    set milestoneCharWidth(width) {
        this._milestoneCharWidth = width;

        this.refresh();
    }

    get milestoneCharWidth() {
        return this._milestoneCharWidth;
    }

    // This does not need a className on Widgets.
    // Each *Class* which doesn't need 'b-' + constructor.name.toLowerCase() automatically adding
    // to the Widget it's mixed in to should implement thus.
    get widgetClass() {}
};
