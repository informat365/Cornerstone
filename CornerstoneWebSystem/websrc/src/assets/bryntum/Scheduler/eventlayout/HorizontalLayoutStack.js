import HorizontalLayout from './HorizontalLayout.js';

/**
 * @module Scheduler/eventlayout/HorizontalLayoutStack
 */

/**
 * Handles layout of events within a row (resource) in horizontal mode. Stacks events, increasing row height when to fit
 * all overlapping events.
 *
 * This layout is used by default in horizontal mode.
 *
 * @extends Scheduler/eventlayout/HorizontalLayout
 * @private
 */
export default class HorizontalLayoutStack extends HorizontalLayout {
    // Input: Array of event layout data
    layoutEventsInBands(events) {
        let verticalPosition = 0;

        do {
            let event = events[0];

            while (event) {
                // Apply band height to the event cfg
                event.top = this.bandIndexToPxConvertFn.call(
                    this.bandIndexToPxConvertThisObj || this,
                    verticalPosition,
                    event.event
                );

                // Remove it from the array and continue searching
                events.splice(events.indexOf(event), 1);

                event = this.findClosestSuccessor(event, events);
            }

            verticalPosition++;
        } while (events.length > 0);

        // Done!
        return verticalPosition;
    }

    // TODO: optimize this for better performance with many events per resource
    findClosestSuccessor(eventRenderData, events) {
        let minGap      = Infinity,
            closest,
            eventEnd    = eventRenderData.endMs,
            gap,
            isMilestone = eventRenderData.event.duration === 0,
            evt;

        for (let i = 0, l = events.length; i < l; i++) {
            evt = events[i];
            gap = evt.startMs - eventEnd;

            if (
                gap >= 0 && gap < minGap &&
                // Two milestones should not overlap
                (gap > 0 || evt.endMs - evt.startMs > 0 || !isMilestone)
            ) {
                closest = evt;
                minGap  = gap;
            }
        }

        return closest;
    }
}
