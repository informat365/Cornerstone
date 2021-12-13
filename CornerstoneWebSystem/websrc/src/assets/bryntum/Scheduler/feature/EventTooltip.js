import TooltipBase from './base/TooltipBase.js';
import GridFeatureManager from '../../Grid/feature/GridFeatureManager.js';

/**
 * @module Scheduler/feature/EventTooltip
 */

/**
 * Displays a tooltip when hovering events. The template used to render the tooltip can be customized, see {@link #config-template}.
 * Config options are also applied to the tooltip shown, see {@link Core.widget.Tooltip} for available options.
 *
 * ```javascript
 * new Scheduler({
 *   features : {
 *     eventTooltip : {
 *         // Tooltip configs can be used here
 *         align : 'l-r' // Align left to right
 *     }
 *   }
 * });
 * ```
 *
 * This feature is **enabled** by default
 *
 * By default, the tooltip {@link Core.widget.Widget#config-scrollAction realigns on scroll}
 * meaning that it will stay aligned with its target should a scroll interaction make the target move.
 *
 * If this is causing performance issues in a Scheduler, such as if there are many dozens of events
 * visible, you can configure this feature with `scrollAction: 'hide'`. This feature's configuration is
 * applied to the tooltip, so that will mean that the tooltip will hide if its target is moved by a
 * scroll interaction.
 *
 * @extends Scheduler/feature/base/TooltipBase
 * @demo Scheduler/basic
 * @externalexample scheduler/EventTooltip.js
 */
export default class EventTooltip extends TooltipBase {
    //region Config

    static get $name() {
        return 'EventTooltip';
    }

    static get defaultConfig() {
        return {
            /**
             * Template (a function accepting event data and returning a string) used to display info in the tooltip.
             * The template will be called with an object containing the fields below
             * @param {Object} data
             * @param {Scheduler.model.EventModel} data.eventRecord
             * @param {Date} data.startDate
             * @param {Date} data.endDate
             * @param {String} data.startText
             * @param {String} data.endText
             * @config {Function} template
             */
            template : data => `
                ${data.eventRecord.name ? `<div class="b-sch-event-title">${data.eventRecord.name}</div>` : ''}
                ${data.startClockHtml}
                ${data.endClockHtml}`,

            cls : 'b-sch-event-tooltip'
        };
    }

    //endregion
}

GridFeatureManager.registerFeature(EventTooltip, true, 'Scheduler');
