import PackMixin from './PackMixin.js';

/**
 * @module Scheduler/eventlayout/VerticalLayout
 */

/**
 * Assists with event layout in vertical mode, handles `eventLayout: none|pack|mixed`
 * @private
 * @mixes Scheduler/eventlayout/PackMixin
 */
export default class VerticalLayout extends PackMixin() {

    static get defaultConfig() {
        return {
            coordProp : 'leftFactor',
            sizeProp  : 'widthFactor'
        };
    }

    // Try to pack the events to consume as little space as possible
    applyLayout(events, columnWidth, resourceMargin, barMargin, columnIndex) {
        super.applyLayout(events, (tplData, clusterIndex, slot, slotSize) => {
            // Stretch events to fill available width
            if (this.scheduler.eventLayout === 'none') {
                tplData.width = columnWidth - resourceMargin * 2;
                tplData.left += resourceMargin;
            }
            else {
                // Fractions of resource column
                tplData.widthFactor = slotSize;

                const
                    leftFactor      = tplData.leftFactor = slot.start + (clusterIndex * slotSize),
                    // Number of "columns" in the current slot
                    packColumnCount = Math.round(1 / slotSize),
                    // Index among those columns for current event
                    packColumnIndex = leftFactor / slotSize,
                    // Width with all bar margins subtracted
                    availableWidth  = columnWidth - resourceMargin * 2 - barMargin * (packColumnCount - 1);

                // Allowing two events to overlap? Slightly offset the second
                if (this.scheduler.eventLayout === 'mixed' && packColumnCount === 2) {
                    tplData.left += leftFactor * columnWidth / 5 + barMargin;
                    tplData.width = columnWidth - leftFactor * columnWidth / 5 - barMargin * 2;
                    tplData.zIndex = 5 + packColumnIndex;
                }
                // Pack by default
                else {
                    // Fractional width
                    tplData.width = slotSize * availableWidth;
                    // Translate to absolute position
                    tplData.left += leftFactor * availableWidth + resourceMargin + barMargin * packColumnIndex;
                }
            }
        });
    }
}
