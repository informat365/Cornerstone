import Base from '../../Core/Base.js';
import DateHelper from '../../Core/helper/DateHelper.js';

/**
 * @module Scheduler/eventlayout/PackMixin
 */

/**
 * Mixin holding functionality shared between HorizontalLayoutPack and VerticalLayout.
 *
 * @mixin
 * @private
 */
export default Target => class PackMixin extends (Target || Base) {

    static get defaultConfig() {
        return {
            coordProp : 'top',
            sizeProp  : 'height'
        };
    }

    // Packs the events to consume as little space as possible
    applyLayout(events, applyClusterFn) {
        const
            me = this,
            { coordProp, sizeProp } = me;

        let slot,
            firstInCluster,
            cluster,
            j;

        for (let i = 0, l = events.length; i < l; i++) {
            firstInCluster = events[i];

            slot = me.findStartSlot(events, firstInCluster);

            cluster = me.getCluster(events, i);

            if (cluster.length > 1) {
                firstInCluster[coordProp] = slot.start;
                firstInCluster[sizeProp]  = slot.end - slot.start;

                // If there are multiple slots and events in the cluster have multiple start dates, group all same-start events into first slot
                j = 1;

                while (j < (cluster.length - 1) && cluster[j + 1].start - firstInCluster.start === 0) {
                    j++;
                }

                // See if there's more than 1 slot available for this cluster, if so - first group in cluster consumes the entire first slot
                const nextSlot = me.findStartSlot(events, cluster[j]);

                if (nextSlot && nextSlot.start < 0.8) {
                    cluster.length = j;
                }
            }

            const
                clusterSize = cluster.length,
                slotSize    = (slot.end - slot.start) / clusterSize;

            // Apply fraction values
            for (j = 0; j < clusterSize; j++) {
                applyClusterFn(cluster[j], j, slot, slotSize);
            }

            i += clusterSize - 1;
        }
    }

    findStartSlot(events, event) {
        const
            { sizeProp, coordProp } = this,
            priorOverlappers = this.getPriorOverlappingEvents(events, event);

        let i;

        if (priorOverlappers.length === 0) {
            return {
                start : 0,
                end   : 1
            };
        }

        for (i = 0; i < priorOverlappers.length; i++) {
            if (i === 0 && priorOverlappers[0][coordProp] > 0) {
                return {
                    start : 0,
                    end   : priorOverlappers[0][coordProp]
                };
            }
            else if (priorOverlappers[i][coordProp] + priorOverlappers[i][sizeProp] < (i < priorOverlappers.length - 1 ? priorOverlappers[i + 1][coordProp] : 1)) {
                return {
                    start : priorOverlappers[i][coordProp] + priorOverlappers[i][sizeProp],
                    end   : i < priorOverlappers.length - 1 ? priorOverlappers[i + 1][coordProp] : 1
                };
            }
        }

        return false;
    }

    getPriorOverlappingEvents(events, event) {
        const
            start       = event.start,
            end         = event.end,
            overlappers = [];

        for (let i = 0, l = events.indexOf(event); i < l; i++) {
            if (DateHelper.intersectSpans(start, end, events[i].start, events[i].end)) {
                overlappers.push(events[i]);
            }
        }

        overlappers.sort(this.sortOverlappers.bind(this));

        return overlappers;
    }

    sortOverlappers(e1, e2) {
        return e1[this.coordProp] < e2[this.coordProp] ? -1 : 1;
    }

    getCluster(events, startIndex) {
        const startEvent = events[startIndex];

        if (startIndex >= events.length - 1) {
            return [startEvent];
        }

        const
            evts = [startEvent],
            l    = events.length;

        let { start, end } = startEvent,
            i              = startIndex + 1;

        while (i < l && DateHelper.intersectSpans(start, end, events[i].start, events[i].end)) {
            evts.push(events[i]);
            start = DateHelper.max(start, events[i].start);
            end   = DateHelper.min(events[i].end, end);
            i++;
        }

        return evts;
    }
};
