import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import GridFeatureManager from '../../Grid/feature/GridFeatureManager.js';
import BrowserHelper from '../../Core/helper/BrowserHelper.js';
import DomHelper from '../../Core/helper/DomHelper.js';
import Rectangle from '../../Core/helper/util/Rectangle.js';
import Delayable from '../../Core/mixin/Delayable.js';
import Widget from '../../Core/widget/Widget.js';

/**
 * @module Scheduler/feature/EventDragSelect
 */

/**
 * Enables users to click and drag to select events inside the Scheduler's timeline.
 *
 * This feature is **disabled** by default.
 *
 * **NOTE:** Incompatible with {@link Scheduler.feature.EventDragCreate EventDragCreate} and the {@link Scheduler.feature.Pan Pan} features.
 *
 * @extends Core/mixin/InstancePlugin
 * @mixes Core/mixin/Delayable
 *
 * @example
 * let scheduler = new Scheduler({
 *   features : {
 *     eventDragSelect      : true,
 *     eventDragCreate      : false
 *   }
 * });
 *
 * @demo Scheduler/dragselection
 * @classtype eventDragSelect
 */
export default class EventDragSelect extends Delayable(InstancePlugin) {
    // region Init

    static get $name() {
        return 'EventDragSelect';
    }

    construct(timeline, config) {
        this.timeline = timeline;

        timeline.multiEventSelect = true;

        if (timeline.features.eventDragCreate || timeline.features.pan) {
            throw new Error('Cannot combine this feature with Pan or EventDragCreate features');
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
            me = this,
            scheduler = me.client;

        // only react to mouse input, and left button
        if (event.touches || event.button !== 0 || me.disabled) {
            return;
        }

        // only react to mousedown directly on grid cell or subgrid element
        if (event.target.matches(me.targetSelector)) {
            me.startX = event.clientX;
            me.startY = event.clientY;

            me.element = DomHelper.createElement({
                tag       : 'div',
                className : 'b-dragselect-rect'
            }, true)[0];

            Widget.floatRoot.appendChild(me.element);
            scheduler.element.classList.add('b-dragselecting');

            me.eventRectangles = Array.from(scheduler.element.querySelectorAll(scheduler.eventSelector)).map(el => ({
                rectangle : Rectangle.from(el),
                record    : scheduler.resolveEventRecord(el)
            }));

            scheduler.clearEventSelection();
            me.subGridElementRect = Rectangle.from(scheduler.timeAxisSubGrid.element);

            // No key processing during drag selection
            scheduler.navigator.disabled = true;
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
                x          = Math.max(event.clientX, me.subGridElementRect.left),
                y          = Math.max(event.clientY, me.subGridElementRect.top),
                left       = Math.min(me.startX, x),
                top        = Math.min(me.startY, y),
                width      = Math.abs(me.startX - x),
                height     = Math.abs(me.startY - y),
                rect       = new Rectangle(left, top, width, height).constrainTo(me.subGridElementRect);

            DomHelper.setTranslateXY(me.element, rect.left, rect.top);
            me.element.style.width  = rect.width + 'px';
            me.element.style.height = rect.height + 'px';

            me.rectangle  = rect;

            me.updateSelection();
        }
    }

    onElementMouseUp(event) {
        const me = this;

        if (typeof me.startX === 'number') {
            me.element && me.element.remove();
            me.client.element.classList.remove('b-dragselecting');
            me.startX = me.startY = null;

            // Navigator will react to the 'click' event which clears selection, bypass this with a short timeout
            me.setTimeout(() => me.client.navigator.disabled = false, 100);
        }
    }

    updateSelection() {
        const
            me = this,
            renderedEventRects = me.eventRectangles,
            rectangle = me.rectangle;

        for (let i = 0, len = renderedEventRects.length; i < len; i++) {
            const
                eventData = renderedEventRects[i],
                shouldSelect = rectangle.intersect(eventData.rectangle, true);

            if (shouldSelect && !eventData.selected) {
                eventData.selected = true;

                me.client.selectEvent(eventData.record, true);
            }
            else if (!shouldSelect && eventData.selected) {
                eventData.selected = false;

                me.client.deselectEvent(eventData.record);
            }
        }
    }
}

GridFeatureManager.registerFeature(EventDragSelect, false, 'Scheduler');
