import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import GridFeatureManager from '../../Grid/feature/GridFeatureManager.js';
import Tooltip from '../../Core/widget/Tooltip.js';
import ClockTemplate from '../tooltip/ClockTemplate.js';
import EventHelper from '../../Core/helper/EventHelper.js';

/**
 * @module Scheduler/feature/ScheduleTooltip
 */

/**
 * Feature that displays a tooltip containing the time at the mouse position when hovering empty parts of the schedule. To not show the tooltip, just disable this feature:
 *
 * ```javascript
 * const scheduler = new Scheduler({
 *     features : {
 *         scheduleTooltip : false
 *     }
 * });
 * ```
 *
 * @extends Core/mixin/InstancePlugin
 * @demo Scheduler/basic
 * @externalexample scheduler/ScheduleTooltip.js
 */
export default class ScheduleTooltip extends InstancePlugin {
    //region Config

    static get $name() {
        return 'ScheduleTooltip';
    }

    static get defaultConfig() {
        return {
            messageTemplate : data => `<div class="b-sch-hovertip-msg">${data.message}</div>`
        };
    }

    // Plugin configuration. This plugin chains some of the functions in Grid.
    static get pluginConfig() {
        return {
            chain : ['render']
        };
    }

    //endregion

    //region Init

    /**
     * Called when scheduler is rendered. Sets up drag and drop and hover tooltip.
     * @private
     */
    render() {
        const me           = this,
            scheduler    = me.client;

        // TODO: render should not ever be called twice.
        if (me.hoverTip) {
            me.hoverTip.destroy();
        }

        let reshowListener;

        const tip = me.hoverTip = new Tooltip({
            id             : `${scheduler.id}-schedule-tip`,
            cls            : 'b-sch-scheduletip',
            allowOver      : true,
            hoverDelay     : 0,
            hideDelay      : 100,
            showOnHover    : true,
            forElement     : scheduler.timeAxisSubGridElement,
            anchorToTarget : false,
            trackMouse     : true,
            forSelector    : '.b-schedulerbase:not(.b-animating):not(.b-dragging-event):not(.b-dragcreating) .b-timeline-subgrid > :not(.b-sch-foreground-canvas):not(.b-group-footer):not(.b-group-row) *',
            // Do not constrain at all, want it to be able to go outside of the viewport to not get in the way
            constrainTo    : null,
            getHtml        : me.getHoverTipHtml.bind(me),
            onDocumentMouseDown(event) {
                // Click on the scheduler hides until the very next
                // non-button-pressed mouse move!
                if (tip.forElement.contains(event.event.target)) {
                    reshowListener = EventHelper.on({
                        element   : scheduler.timeAxisSubGridElement,
                        mousemove : e => tip.internalOnPointerOver(e),
                        capture   : true
                    });
                }

                const hideAnimation = tip.hideAnimation;
                tip.hideAnimation = false;
                tip.constructor.prototype.onDocumentMouseDown.call(tip, event);
                tip.hideAnimation = hideAnimation;
            },
            listeners : {
                pointerover : ({ event }) => {
                    const buttonsPressed = 'buttons' in event ? event.buttons > 0
                        : event.which > 0; // fallback for Safari which doesn't support 'buttons'

                    // This is the non-button-pressed mousemove
                    // after the document mousedown
                    if (!buttonsPressed && reshowListener) {
                        reshowListener();
                    }

                    // Never any tooltip while interaction is ongoing and a mouse button is pressed
                    return !me.disabled && !scheduler.readOnly && !buttonsPressed;
                }
            }
        });

        // Do this to make tip CSP compliant. Not possible to have inline styles
        const tipMouseMove = tip.onMouseMove;
        tip.onMouseMove = event => {
            tipMouseMove.call(tip, event);
            me.onTipMove(tip, event);
        };

        me.clockTemplate = new ClockTemplate({
            timeAxisViewModel : scheduler.timeAxisViewModel
        });
    }

    doDestroy() {
        const me = this;
        me.clockTemplate && me.clockTemplate.destroy();
        me.hoverTip && me.hoverTip.destroy();
        super.doDestroy();
    }

    //endregion

    //region Contents

    onTipMove(tip, event) {
        this.clockTemplate.updateDateIndicator(tip.element, this.lastTime);
    }

    /**
     * Gets html to display in hover tooltip (tooltip displayed on empty parts of scheduler)
     */
    getHoverTipHtml({ tip, event }) {
        const
            me        = this,
            scheduler = me.client,
            time      = event && scheduler.getDateFromDomEvent(event, 'floor', true);

        let html      = me.lastHtml;

        if (time) {
            const resourceRecord = scheduler.resolveResourceRecord(event);

            if (time - me.lastTime !== 0 || resourceRecord !== me.lastResource) {
                me.lastResource = resourceRecord;
                html = me.lastHtml = me.updateHoverTip(time, event);
            }
        }
        else {
            tip.hide();
            me.lastTime = null;
            me.lastResource = null;
        }

        return html;
    }

    /**
     * Called from getHoverTipHtml(), fills templates used.
     * @private
     */
    updateHoverTip(date, event) {
        if (date) {
            const me          = this,
                clockHtml   = me.clockTemplate.template({
                    date : date,
                    text : me.client.getFormattedDate(date)
                }),
                messageHtml = me.messageTemplate({
                    message : me.getText(date, event) || ''
                });

            me.lastTime = date;

            return clockHtml + messageHtml;
        }
    }

    /**
     * Override this to render custom text to default hover tip
     * @param {Date} date
     * @param {Event} event Browser event
     * @return {String}
     */
    getText(date, event) {

    }

    //endregion
}

// TODO: Refactor SASS so thet auto-generated class name of 'b-' + cls.name.toLowerCase() can be used.
ScheduleTooltip.featureClass = 'b-scheduletip';

GridFeatureManager.registerFeature(ScheduleTooltip, true, 'Scheduler');
