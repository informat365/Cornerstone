import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import GridFeatureManager from '../../Grid/feature/GridFeatureManager.js';
import BrowserHelper from '../../Core/helper/BrowserHelper.js';
import DragBase from './base/DragBase.js';

/**
 * @module Scheduler/feature/Pan
 */

/**
 * Makes the scheduler's timeline pannable by dragging with the mouses.
 *
 * This feature is **disabled** by default.
 *
 * **NOTE:** Incompatible with {@link Scheduler.feature.EventDragCreate EventDragCreate} feature.
 *
 * @extends Core/mixin/InstancePlugin
 *
 * @example
 * // enable Pan
 * let scheduler = new Scheduler({
 *   features : {
 *     pan : true,
 *     eventDragCreate : false
 *   }
 * });
 *
 * @classtype pan
 */
export default class Pan extends InstancePlugin {
    // region Init

    static get $name() {
        return 'Pan';
    }

    static get defaultConfig() {
        return {
            /**
             * Set to false to only pan horizontally
             * @config {Boolean}
             * @default
             */
            vertical : true
        };
    }

    construct(timeline, config) {
        this.timeline = timeline;

        if (timeline.features.eventDragCreate && !timeline.features.eventDragCreate.disabled) {
            throw new Error('Cannot combine Pan and eventDragCreate features');
        }

        const targetSelectors = [
            '.b-grid-cell',
            '.b-timeline-subgrid'
        ];

        this.targetSelector = targetSelectors.join(',');

        super.construct(timeline, config);
    }

    //endregion

    //region Plugin config

    // Plugin configuration. This plugin chains some of the functions in Scheduler.
    static get pluginConfig() {
        return {
            chain : ['onElementMouseDown', 'onElementMouseMove', 'onElementMouseUp']
        };
    }

    //endregion

    onElementMouseDown(event) {
        const
            me                = this,
            timeline          = me.timeline,
            dragFeature       = Object.values(timeline.features).find(feature => feature instanceof DragBase),
            enablePanOnEvents = timeline.readOnly || !dragFeature || dragFeature.disabled;

        // only react to mouse input, and left button
        if (event.touches || event.button !== 0 || me.disabled) {
            return;
        }

        // only react to mousedown directly on grid cell, subgrid element or if drag is disabled - the events too
        if (event.target.matches(me.targetSelector) || (enablePanOnEvents && event.target.closest(timeline.eventSelector))) {
            me.mouseX = event.clientX;
            me.mouseY = event.clientY;
        }
    }

    onElementMouseMove(event) {
        const me = this;

        // Synthetic mousemove event has no button for IE11
        // Detect if a mouseup happened outside our element (or browser window for that matter). Note 'buttons' is not supported by Safari
        if (event.buttons === 0 && (!BrowserHelper.isSafari && (!BrowserHelper.isIE11 || event.isTrusted))) {
            me.onElementMouseUp();
            return;
        }

        if (typeof me.mouseX === 'number') {
            const xScroller = me.timeline.subGrids.normal.scrollable,
                yScroller = me.timeline.scrollable,
                x         = event.clientX,
                y         = event.clientY;

            event.preventDefault();

            if (me.vertical) {
                yScroller.scrollBy(0, me.mouseY - y);
            }

            xScroller.scrollBy(me.mouseX - x);

            me.mouseX = x;
            me.mouseY = y;
        }
    }

    onElementMouseUp(event) {
        this.mouseX = this.mouseY = null;
    }

    //endregion
}

GridFeatureManager.registerFeature(Pan, false, ['Scheduler', 'Gantt']);
