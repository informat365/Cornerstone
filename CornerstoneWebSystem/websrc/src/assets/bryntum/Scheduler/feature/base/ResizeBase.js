//TODO: Prevent right click while resizing (do it in ResizeHelper?)
//TODO: Scroll

import InstancePlugin from '../../../Core/mixin/InstancePlugin.js';
import DateHelper from '../../../Core/helper/DateHelper.js';
import DomHelper from '../../../Core/helper/DomHelper.js';
import Rectangle from '../../../Core/helper/util/Rectangle.js';
import Tooltip from '../../../Core/widget/Tooltip.js';
import ClockTemplate from '../../tooltip/ClockTemplate.js';

/**
 * @module Scheduler/feature/base/ResizeBase
 */

const tipAlign = {
    'top'    : 'b-t',
    'right'  : 'b100-t100',
    'bottom' : 't-b',
    'left'   : 'b0-t0'
};

/**
 * Base class for EventResize (Scheduler) and TaskResize (Gantt) features. Contains shared code. Not to be used directly.
 *
 * @extends Core/mixin/InstancePlugin
 * @abstract
 */
export default class ResizeBase extends InstancePlugin {
    //region Config

    static get defaultConfig() {
        return {
            /**
             * `false` to not show a tooltip while resizing
             * @config {Boolean}
             * @default
             */
            showTooltip : true,

            /**
             * true to see exact event length during resizing
             * @config {Boolean}
             * @default
             */
            showExactResizePosition : false,

            /**
             * An empty function by default, but provided so that you can perform custom validation on
             * the item being resized. Return true if the new duration is valid, false to signal that it is not.
             * @param {Object} context The resize context, contains the record & dates.
             * @param {Event} e The browser Event object
             * @return {Boolean}
             * @config {Function}
             */
            validatorFn : () => {},

            /**
             * `this` reference for the validatorFn
             * @config {Object}
             */
            validatorFnThisObj : null,

            /**
             * The tooltip instance to show while resizing an event or a configuration object for the {@link Core.widget.Tooltip}.
             * @config {Core.widget.Tooltip}
             */
            tip : null,

            tipTemplate : data => `
                <div class="b-sch-tip-${data.valid ? 'valid' : 'invalid'}">
                    ${data.startClockHtml}
                    ${data.endClockHtml}
                    <div class="b-sch-tip-message">${data.message}</div>
                </div>
            `
        };
    }

    static get pluginConfig() {
        return {
            chain : ['render']
        };
    }

    //endregion

    //region Init & destroy

    render() {
        const me     = this,
            client = me.client;

        me.resize && me.resize.destroy();

        me.resize = me.createResizeHelper();

        if (me.showTooltip) {
            me.clockTemplate = new ClockTemplate({
                timeAxisViewModel : client.timeAxisViewModel
            });
        }
    }

    doDestroy() {
        const me = this;

        me.tip && me.tip.destroy();
        me.clockTemplate && me.clockTemplate.destroy();
        me.resize && me.resize.destroy();

        super.doDestroy();
    }

    //endregion

    //region Events

    isElementResizable(element, event) {
        const
            { client, resize } = this,
            timespanRecord     = client.resolveTimeSpanRecord(element);

        if (client.readOnly) {
            return false;
        }

        let resizable = timespanRecord && timespanRecord.isResizable;

        // go up from "handle" to resizable element
        element = DomHelper.up(event.target, client.eventSelector);

        // Not resizable if the mousedown is on a resizing handle of
        // a percent bar.
        const
            handleHoldingElement = element ? element.firstElementChild : element,
            handleEl             = event.target.closest('[class$="-handle"]');

        if (!resizable || (handleEl && handleEl !== handleHoldingElement)) {
            return false;
        }

        let startsOutside = element.classList.contains('b-sch-event-startsoutside'),
            endsOutside   = element.classList.contains('b-sch-event-endsoutside');

        if (resizable === true) {
            if (startsOutside && endsOutside) {
                return false;
            }
            else if (startsOutside) {
                resizable = 'end';
            }
            else if (endsOutside) {
                resizable = 'start';
            }
            else {
                return resize.overStartHandle(event, element) || resize.overEndHandle(event, element);
            }
        }

        if (
            (startsOutside && resizable === 'start') ||
            (endsOutside && resizable === 'end')
        ) {
            return false;
        }

        if (
            (resize.overStartHandle(event, element) && resizable === 'start') ||
            (resize.overEndHandle(event, element) && resizable === 'end')
        ) {
            return true;
        }

        return false;
    }

    onBeforeResizeStart({ element, event }) {
        const
            { client }     = this,
            name           = client.scheduledEventName,
            timespanRecord = client.resolveTimeSpanRecord(element);

        if (this.disabled) {
            return false;
        }

        // trigger beforeEventResize or beforeTaskResize depending on product
        return client.trigger(
            `before${client.capitalizedEventName}Resize`,
            { [name + 'Record'] : timespanRecord, event }
        ) !== false;
    }

    onResizeStart({ context, event }) {
        const
            me             = this,
            client         = me.client,
            timespanRecord = client.resolveTimeSpanRecord(context.element),
            name           = client.scheduledEventName;

        client.element.classList.add('b-resizing-event');

        // Let products to their specific stuff
        me.setupProductResizeContext(context, event);

        if (me.showTooltip) {
            if (me.tip) {
                me.tip.align = tipAlign[context.edge];
                me.tip.showBy(me.getTooltipTarget());
            }
            else {
                me.tip = new Tooltip({
                    id         : `${client.id}-event-resize-tip`,
                    autoShow   : true,
                    axisLock   : true,
                    trackMouse : false,
                    getHtml    : me.getTipHtml.bind(me),
                    align      : tipAlign[context.edge],
                    hideDelay  : 0
                });

                me.tip.on('innerhtmlupdate', me.updateDateIndicator, me);
            }
        }

        // flag to not allow release of element when scrolling
        timespanRecord.instanceMeta(client).retainElement = true;

        // Trigger eventResizeStart or taskResizeStart depending on product
        client.trigger(`${name}ResizeStart`, { [`${name}Record`] : timespanRecord, event });
    }

    updateDateIndicator() {
        const
            { edge, startDate, endDate } = this.resize.context,
            { element }                  = this.tip;

        if (startDate || endDate) {
            if (edge === 'right' || edge === 'bottom') {
                this.clockTemplate.updateDateIndicator(element.querySelector('.b-sch-tooltip-enddate'), endDate);
            }
            else {
                this.clockTemplate.updateDateIndicator(element, startDate);
            }
        }
    }

    getTooltipTarget() {
        const
            me     = this,
            target = Rectangle.from(me.resize.context.element, null, true);

        if (me.resize.direction === 'horizontal') {
            // Align to the dragged edge of the proxy, and then bump right so that the anchor aligns perfectly.
            if (me.resize.context.edge === 'right') {
                target.x = target.right - 1;
            }
            target.width = me.tip.anchorSize[0] / 2;
        }
        else {
            // Align to the dragged edge of the proxy, and then bump bottom so that the anchor aligns perfectly.
            if (me.resize.context.edge === 'bottom') {
                target.y = target.bottom - 1;
            }
            target.height = me.tip.anchorSize[1] / 2;
        }

        return { target };
    }

    onResizing({ context, event }) {
        const
            me                = this,
            { client }        = me,
            depFeature        = client.features.dependencies,
            timespanRecord    = me.getTimespanRecord(context),
            name              = client.scheduledEventName,
            { element, edge } = context,
            xy                = DomHelper.getTranslateXY(element);

        let start, end;

        if (edge === 'top' || edge === 'left') {
            end = timespanRecord.endDate;

            if (client.snapRelativeToEventStartDate) {
                start = client.getDateFromXY(xy, null, true);
                start = client.timeAxis.roundDate(start, timespanRecord.startDate);
            }
            else {
                start = client.getDateFromXY(xy, 'round', true);
            }
        }
        // bottom || right
        else {
            xy[0] += element.offsetWidth;
            xy[1] += element.offsetHeight;

            start = timespanRecord && timespanRecord.startDate;

            if (client.snapRelativeToEventStartDate) {
                end = client.getDateFromXY(xy, null, true);
                end = client.timeAxis.roundDate(end, timespanRecord.endDate);
            }
            else {
                end = client.getDateFromXY(xy, 'round', true);
            }
        }

        start = start || context.startDate;
        end = end || context.endDate;

        if (context.dateConstraints) {
            start = DateHelper.constrain(start, context.dateConstraints.start, context.dateConstraints.end);
            end = DateHelper.constrain(end, context.dateConstraints.start, context.dateConstraints.end);
        }

        if (me.showExactResizePosition || client.timeAxisViewModel.snap) {
            const exactSize = edge === 'top' || edge === 'left'
                ? client.timeAxisViewModel.getDistanceBetweenDates(start, timespanRecord.endDate)
                : client.timeAxisViewModel.getDistanceBetweenDates(timespanRecord.startDate, end);

            switch (edge) {
                case 'top':
                    DomHelper.setTranslateY(element, context.elementStartY + context.elementWidth - exactSize);
                    element.style.height = exactSize + 'px';
                    break;
                case 'right':
                    element.style.width = exactSize + 'px';
                    break;
                case 'bottom':
                    element.style.height = exactSize + 'px';
                    break;
                case 'left':
                    DomHelper.setTranslateX(element, context.elementStartX + context.elementWidth - exactSize);
                    element.style.width = exactSize + 'px';
                    break;
            }
        }

        const dateChanged = context.endDate - end !== 0 || context.startDate - start !== 0;

        context.endDate = end;
        context.startDate = start;

        // No need to query on every pixel of mouse move
        if (dateChanged) {
            context.valid = me.checkValidity(context, event);
        }

        // Trigger eventPartialResize or taskPartialResize depending on product
        client.trigger(`${name}PartialResize`, {
            [`${name}Record`] : timespanRecord,
            startDate         : start,
            endDate           : end,

            element,
            context
        });

        if (depFeature) {
            depFeature.updateDependenciesForTimeSpan(timespanRecord, element);
        }

        if (me.showTooltip) {
            me.tip.alignTo(me.getTooltipTarget());
        }
    }

    checkValidity(context, event) {
        let valid = context.startDate && context.endDate > context.startDate && this.validatorFn.call(this.validatorFnThisObj || this, context, event);

        if (valid && typeof valid !== 'boolean') {
            context.message = valid.message || '';

            valid = valid.valid;
        }

        return valid !== false;
    }

    onFinishResize({ source : drag, context, event }) {
        const
            me             = this,
            timespanRecord = me.getTimespanRecord(context),
            oldStart       = timespanRecord.startDate,
            oldEnd         = timespanRecord.endDate,
            start          = context.startDate || oldStart,
            end            = context.endDate || oldEnd,
            client         = me.client;

        let modified = false;

        // allow release of element again
        timespanRecord.instanceMeta(client).retainElement = false;

        const old = context.finalize;
        context.finalize = (...params) => {
            // We are overriding context of the resize helper. It is finalized automatically on sync resize. Which means,
            // we should only call finalize if context is async.
            context.async && me.finalize(...params);
            old.call(context, params);
        };

        context.valid = start && end && (end - start > 0) && // Input sanity check
            ((start - oldStart !== 0) || (end - oldEnd !== 0)) && // Make sure start OR end changed
            context.valid !== false;

        if (context.valid) {
            // Seems to be a valid resize operation, ask outside world if anyone wants to take control over the finalizing,
            // to show a confirm dialog prior to applying the new values. Triggers beforeEventResizeFinalize or
            // beforeTaskResizeFinalize depending on product
            client.trigger(`before${client.scheduledEventName}ResizeFinalize`, { context, event });
            modified = true;
        }

        if (!context.async) {
            me.finalize(modified);
        }
    }

    onCancelResize({ context }) {
        const timespanRecord = this.getTimespanRecord(context);

        // resizing may not have started at all (just clicking a resize handle)
        if (timespanRecord) {
            timespanRecord.instanceMeta(this.client).retainElement = false;
        }

        this.finalize(false);
    }

    finalize(updateRecord) {
        const
            me             = this,
            { client }     = me,
            context        = me.resize.context,
            timespanRecord = me.getTimespanRecord(context),
            name           = client.scheduledEventName;

        let wasChanged = false;

        if (me.tip) {
            me.tip.hide();
        }

        if (context.started) {
            if (updateRecord) {
                // Scheduler and gantt updates the record differently
                wasChanged = me.internalUpdateRecord(context, timespanRecord);
            }

            if (!updateRecord || !wasChanged) {
                const dependencies = client.features.dependencies;

                // Dependencies are updated dynamically during resize, so ensure they are redrawn
                // if the event snaps back with no change.
                if (dependencies) {
                    dependencies.scheduleDraw(true);
                }

                client.repaintEventsForResource(me.getRowRecord(context));
            }
        }

        client.element.classList.remove('b-resizing-event');

        // Triggers eventResizeEnd or taskResizeEnd depending on product
        client.trigger(`${name}ResizeEnd`, {
            changed           : wasChanged,
            [`${name}Record`] : timespanRecord || client.resolveEventRecord(context.element)
        });
    }

    //endregion

    //region Tooltip

    getTipHtml({ tip }) {
        const me = this;
        let { startDate, endDate, valid, message } = me.resize.context;

        // Empty string hides the tip - we get called before the Resizer, so first call will be empty
        if (!startDate || !endDate) {
            return tip.html;
        }

        if (message === undefined) message = '';

        endDate = me.client.getDisplayEndDate(endDate, startDate);

        let startText = me.client.getFormattedDate(startDate),
            endText   = me.client.getFormattedDate(endDate);

        return me.tipTemplate({
            valid,
            startDate,
            endDate,
            startText,
            endText,
            message,
            startClockHtml : me.clockTemplate.template({
                date : startDate,
                text : startText,
                cls  : 'b-sch-tooltip-startdate'
            }),
            endClockHtml : me.clockTemplate.template({
                date : endDate,
                text : endText,
                cls  : 'b-sch-tooltip-enddate'
            })
        });
    }

    //endregion

    //region Product specific, implemented in subclasses

    getRowRecord(context) {
        throw new Error('Implement in subclass');
    }

    getTimespanRecord(context) {
        throw new Error('Implement in subclass');
    }

    setupProductResizeContext(context, event) {
        throw new Error('Implement in subclass');
    }

    // Store containing the timespan record being resized
    get store() {
        throw new Error('Implement in subclass');
    }

    //endregion
}
