import HorizontalLayout from './HorizontalLayout.js';
import PackMixin from './PackMixin.js';

/**
 * @module Scheduler/eventlayout/HorizontalLayoutPack
 */

/**
 * Handles layout of events within a row (resource) in horizontal mode. Packs events (adjusts their height) to fit
 * available row height
 *
 * @extends Scheduler/eventlayout/HorizontalLayout
 * @mixes Scheduler/eventlayout/PackMixin
 * @private
 */
export default class HorizontalLayoutPack extends PackMixin(HorizontalLayout) {
    // Packs the events to consume as little space as possible
    applyLayout(events) {
        super.applyLayout(events, (event, j, slot, slotSize) => {
            event.height = slotSize;
            event.top    = slot.start + (j * slotSize);
        });

        events.forEach(event => {
            Object.assign(
                event,
                this.bandIndexToPxConvertFn.call(
                    this.bandIndexToPxConvertThisObj || this,
                    event.top,
                    event.height,
                    null,
                    event.event
                )
            );
        });
    }
}
