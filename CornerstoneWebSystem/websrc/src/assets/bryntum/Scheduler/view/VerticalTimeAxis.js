import TimeAxisBase from './TimeAxisBase.js';

/**
 * @module Scheduler/view/VerticalTimeAxis
 */

/**
 * Widget that renders a vertical time axis. Only renders ticks in view. Used in vertical mode.
 * @extends Core/widget/Widget
 * @private
 */
export default class VerticalTimeAxis extends TimeAxisBase {

    static get $name() {
        return 'VerticalTimeAxis';
    }

    static get defaultConfig() {
        return {
            cls : 'b-verticaltimeaxis',

            sizeProperty : 'height',

            positionProperty : 'top',

            wrapText : true
        };
    }

    get height() {
        return this.size;
    }
}
