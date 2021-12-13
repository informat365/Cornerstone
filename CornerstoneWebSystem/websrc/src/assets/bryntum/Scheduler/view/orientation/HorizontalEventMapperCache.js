export default class HorizontalEventMapperCache {
    constructor(mapper) {
        const me = this;

        me.mapper = mapper;

        // caching layout calculations
        me.renderedEventsMap = {};
        me.rowLayoutCache = {};
    }

    /**
     * Clear event & row layout cache (removes cached layout for all resources and events)
     * @internal
     */
    clear(removeDivs = false) {
        const me = this;

        me.renderedEventsMap = {};
        Object.keys(me.rowLayoutCache).forEach(resourceId => me.clearRow(resourceId, removeDivs ? 'immediate' : false));
    }

    //region Render data

    addRenderedEvents(resourceId, data) {
        this.renderedEventsMap[resourceId] = data;
    }

    addRenderedEvent(eventData) {
        // If the resource is not layed out (outside of rendered block)
        // then this is a no-op.
        if (eventData.resourceId in this.renderedEventsMap) {
            const resourceRenderedEvents = this.renderedEventsMap[eventData.resourceId] || (this.renderedEventsMap[eventData.resourceId] = {});

            resourceRenderedEvents[eventData.id] = eventData;
        }
    }

    getRenderedTimeSpan(resourceId, eventElementId) {
        const resourceRenderedEvents = this.renderedEventsMap[resourceId];

        return resourceRenderedEvents && resourceRenderedEvents[eventElementId];
    }

    clearRenderedTimeSpan(resourceId, eventElementId) {
        const resourceRenderedEvents = this.renderedEventsMap[resourceId];

        if (resourceRenderedEvents) {
            const result = this.renderedEventsMap[resourceId][eventElementId];

            delete this.renderedEventsMap[resourceId][eventElementId];
            return result;
        }
    }

    getRenderedEvents(resourceId) {
        return this.renderedEventsMap[resourceId];
    }

    clearRenderedEvents(resourceId) {
        this.renderedEventsMap[resourceId] = null;
    }

    //endregion

    //region TimeSpans

    getTimeSpan(timeSpanId, rowId) {
        const resourceCache = this.rowLayoutCache[rowId],
            timeSpanCache    = resourceCache && resourceCache[timeSpanId];

        if (!timeSpanCache) return null;

        return timeSpanCache.layoutCache;
    }

    /**
     * Clears the event layout for the passed event and resource. Will usually preserve
     * the event DIVs for recycling unless `removeDiv` is passed. If preserving them,
     * it will hide the div unless `remainVisible` is passed.
     * @param {*} eventId ID of event
     * @param {*} resourceId ID of resource
     * @param {*} removeDiv Defaults to false
     * @param {*} remainVisible Defaults to false
     * @private
     */
    clearEvent(eventId, resourceId, removeDiv = false, remainVisible = false) {
        const me = this,
            eventCache = me.getTimeSpan(eventId, resourceId);

        if (!eventCache) return null;

        if (eventCache.div) {
            me.mapper.clearDiv(eventCache.div, removeDiv, remainVisible);
        }

        me.rowLayoutCache[resourceId][eventId].layoutCache = null;
    }

    //endregion

    //region Row

    getRow(resourceId) {
        return this.rowLayoutCache[resourceId];
    }

    addRow(resourceId, data) {
        this.rowLayoutCache[resourceId] = data;
    }

    clearRow(resourceId, removeDivs = false, remainVisible = false) {
        const me            = this,
            resourceCache = me.rowLayoutCache[resourceId];

        if (!resourceCache) return;

        if (me.renderedEventsMap[resourceId]) delete me.renderedEventsMap[resourceId];

        Object.keys(resourceCache).forEach(eventId => {
            if (eventId !== '_allEvents' && eventId !== '_rowHeight') {
                me.clearEvent(eventId, resourceId, removeDivs, remainVisible);
            }
        });
        delete me.rowLayoutCache[resourceId];
    }

    //endregion
}
