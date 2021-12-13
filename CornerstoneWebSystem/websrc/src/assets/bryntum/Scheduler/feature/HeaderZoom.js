import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import GridFeatureManager from '../../Grid/feature/GridFeatureManager.js';
import BrowserHelper from '../../Core/helper/BrowserHelper.js';
import DomHelper from '../../Core/helper/DomHelper.js';
import Rectangle from '../../Core/helper/util/Rectangle.js';

/**
 * @module Scheduler/feature/HeaderZoom
 */

/**
 * Enables users to click and drag to zoom to a date range in Scheduler's header time axis.
 *
 * This feature is **disabled** by default.
 *
 * @extends Core/mixin/InstancePlugin
 *
 * @example
 * let scheduler = new Scheduler({
 *   features : {
 *     headerZoom      : true
 *   }
 * });
 *
 * @classtype HeaderZoom
 * @externalexample scheduler/HeaderZoom.js
 */
export default class HeaderZoom extends InstancePlugin {

    static get $name() {
        return 'HeaderZoom';
    }

    // Plugin configuration. This plugin chains some of the functions in Scheduler.
    static get pluginConfig() {
        return {
            chain : ['onElementMouseDown', 'onElementMouseMove', 'onElementMouseUp']
        };
    }

    onElementMouseDown(event) {
        const
            me        = this,
            scheduler = me.client;

        // only react to mouse input, and left button
        if (event.touches || event.button !== 0 || me.disabled) {
            return;
        }

        // only react to mousedown directly on timeaxis cell
        if (event.target.matches('.b-sch-header-timeaxis-cell')) {
            const headerEl = scheduler.subGrids.normal.header.headersElement;

            me.startX = event.clientX;

            me.element = DomHelper.createElement({
                parent    : headerEl,
                tag       : 'div',
                className : 'b-headerzoom-rect'
            });

            me.headerElementRect = Rectangle.from(headerEl);
        }
    }

    onElementMouseMove(event) {
        const me = this;

        // Synthetic mousemove event has no button for IE11
        // Detect if a mouseup happened outside our element (or browser window for that matter). Note 'buttons' is not supported by Safari
        if (event.buttons === 0 && (!BrowserHelper.isSafari && (!BrowserHelper.isIE11 || event.isTrusted))) {
            me.onElementMouseUp(event);
            return;
        }

        if (typeof me.startX === 'number') {
            const
                x     = Math.max(event.clientX, me.headerElementRect.left),
                left  = Math.min(me.startX, x),
                width = Math.abs(me.startX - x),
                rect  = new Rectangle(left - me.headerElementRect.x + me.client.scrollLeft, 0, width, me.headerElementRect.height);

            DomHelper.setTranslateX(me.element, rect.left);
            me.element.style.width = rect.width + 'px';
        }
    }

    onElementMouseUp(event) {
        const me = this;

        if (typeof me.startX === 'number') {
            const
                timeline  = me.client,
                rect      = Rectangle.from(me.element),
                startDate = timeline.getDateFromCoordinate(rect.left, 'round', false),
                endDate   = timeline.getDateFromCoordinate(rect.right, 'round', false);

            me.element && me.element.remove();
            me.startX = null;

            me.client.zoomToSpan({
                startDate,
                endDate
            });
        }
    }
}

GridFeatureManager.registerFeature(HeaderZoom, false, 'Scheduler');
