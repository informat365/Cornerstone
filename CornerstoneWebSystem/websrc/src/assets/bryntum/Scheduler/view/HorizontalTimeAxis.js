import TimeAxisBase from './TimeAxisBase.js';

/**
 * @module Scheduler/view/HorizontalTimeAxis
 */

/**
 * A visual horizontal representation of the time axis described in the
 * {@link Scheduler.preset.ViewPreset#field-headers headers}.
 * Normally you should not interact with this class directly.
 *
 * @extends Scheduler/view/TimeAxisBase
 * @private
 */
export default class HorizontalTimeAxis extends TimeAxisBase {

    static get $name() {
        return 'HorizontalTimeAxis';
    }

    static get defaultConfig() {
        return {
            cls : 'b-horizontaltimeaxis',

            sizeProperty : 'width',

            positionProperty : 'left',

            wrapText : false
        };
    }

    construct() {
        super.construct(...arguments);

        this.model.on({
            update  : this.onModelUpdate,
            thisObj : this
        });
    }

    get width() {
        return this.size;
    }

    onModelUpdate() {
        // Force rebuild when availableSpace has changed, to recalculate width and maybe apply compact styling
        if (this.model.availableSpace !== this.width) {
            this.refresh(true);
        }
    }
}
