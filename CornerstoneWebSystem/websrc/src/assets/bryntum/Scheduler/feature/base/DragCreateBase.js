import InstancePlugin from '../../../Core/mixin/InstancePlugin.js';
import ResizeHelper from '../../../Core/helper/ResizeHelper.js';
import DateHelper from '../../../Core/helper/DateHelper.js';
import DomHelper from '../../../Core/helper/DomHelper.js';
import Rectangle from '../../../Core/helper/util/Rectangle.js';
import Tooltip from '../../../Core/widget/Tooltip.js';
import ClockTemplate from '../../tooltip/ClockTemplate.js';
import EventHelper from '../../../Core/helper/EventHelper.js';

/**
 * @module Scheduler/feature/base/DragCreateBase
 */

// TODO: add hovertip if EvenDrag feature not used
// TODO: some of the code here could be shared with EventResize. make common base class?
// TODO: touch support
// TODO: mouse up outside not working as it should

/**
 * Base class for EventDragCreate (Scheduler) and TaskDragCreate (Gantt) features. Contains shared code. Not to be used directly.
 *
 * @extends Core/mixin/InstancePlugin
 */
export default class DragCreateBase extends InstancePlugin {
    //region Config

    static get defaultConfig() {
        return {
            /**
             * true to show a time tooltip when dragging to create a new event
             * @config {Boolean}
             * @default
             */
            showTooltip : true,

            /**
             * Number of pixels the drag target must be moved before dragging is considered to have started. Defaults to 2.
             * @config {Number}
             * @default
             */
            dragTolerance : 2,

            // used by gantt to only allow one task per row
            preventMultiple : false,

            validatorFn : () => {},

            /**
             * `this` reference for the validatorFn
             * @config {Object}
             */
            validatorFnThisObj : null,

            /**
             * CSS class to add to proxy used when creating a new event
             * @config {String}
             * @default
             * @private
             */
            proxyCls : 'b-sch-dragcreator-proxy',

            tipTemplate : data => `
                <div class="b-sch-tip-${data.valid ? 'valid' : 'invalid'}">
                    ${data.startClockHtml}
                    ${data.endClockHtml}
                    <div class="b-sch-tip-message">${data.message}</div>
                </div>
            `
        };
    }

    // Plugin configuration. This plugin chains some of the functions in Grid.
    static get pluginConfig() {
        return {
            chain  : ['onElementMouseDown', 'onElementMouseMove'],
            before : ['onElementContextMenu']
        };
    }

    doDestroy() {
        this.tip && this.tip.destroy();
        this.resize && this.resize.destroy();
        super.doDestroy();
    }

    //endregion

    //region Proxy element & resizing

    /**
     * Adds a proxy element to illustrate the timespan that might be created
     * @param config
     * @param {Core.data.Model} config.rowRecord  The row for which an event proxy element is being created.
     * @param {Number} config.startX the position along the time axis at which to display the proxy element.
     * @param {Number} [config.width] the width of the proxy element.
     */
    addProxy(config) {
        const
            me            = this,
            { client }    = me,
            { barMargin } = client,
            region        = client.currentOrientation.getRowRegion(config.rowRecord);

        let width, height, proxyX, proxyY;

        if (client.isHorizontal) {
            proxyX = ('currentX' in config) ? Math.min(config.startX, config.currentX + 1) : config.startX;
            proxyY = region.y + barMargin;
            width  = config.width || me.dragTolerance;
            height = region.height - barMargin * 2;
        }
        else {
            proxyX = region.x + barMargin;
            proxyY = ('currentY' in config) ? Math.min(config.startY, config.currentY + 1) : config.startY;
            width  = region.width - barMargin * 2;
            height = config.height || me.dragTolerance;
        }

        // This CSS class is to block further drag creates when one is in progress (like awaiting async finalization)
        client.element.classList.add('b-dragcreating');
        // This CSS class is to block hover for other events during actually dragging the proxy
        client.element.classList.add('b-dragcreating-proxy-sizing');

        // We are dragging to size element from nothing. Its size on drag is calculated by its *initial size*, plus the
        // mouse movement delta, so it MUST start at zero for that to yield the correct result.
        return me.proxy = DomHelper.createElement({
            parent        : client.foregroundCanvas,
            className     : me.proxyCls,
            style         : `transform:translate(${proxyX}px, ${proxyY}px);width:${width}px;height:${height}px`,
            // Prevent element from being recycled by DomHelper.sync()
            retainElement : true
        });
    }

    /**
     * Remove proxy element and clean up related stuff
     * @private
     */
    removeProxy() {
        const me = this;
        if (me.proxy) {
            me.proxy.remove();
            me.resize.destroy();
            me.proxy = me.resize = null;

            // This CSS class is to block further drag creates when one is in progress (like awaiting async finalization)
            me.client.element.classList.remove('b-dragcreating');
            // This CSS class is to block hover for other events during actually dragging the proxy
            me.client.element.classList.remove('b-dragcreating-proxy-sizing');
            me.tip && me.tip.hide();
        }
    }

    /**
     * Creates an instance of ResizeHelper used to resize the proxy element
     * @param event
     * @param data
     */
    initResizer(event, data) {
        const me = this;

        let edge;

        if (me.client.isHorizontal) {
            edge = data.currentX > data.startX ? 'right' : 'left';
        }
        else {
            edge = data.currentY > data.startY ? 'bottom' : 'top';
        }

        me.resize && me.resize.destroy();

        me.resize = new ResizeHelper({
            name      : me.constructor.name, // For debugging
            direction : me.client.isVertical ? 'vertical' : 'horizontal',
            isTouch   : event.type.startsWith('touch'),
            grab      : {
                element : me.proxy,
                edge,
                event   : data.startEvent
            },
            allowEdgeSwitch : true,   // Means that they can switch edges; the mouse can cross the zero point and drag the other way
            outerElement    : me.client.timeAxisSubGridElement, // Constrain resize to view
            scrollManager   : me.client.scrollManager,
            scroller        : me.client.timeAxisSubGrid.scrollable, // Scroll in both directions
            listeners       : {
                resizing : me.onResizing,
                resize   : me.onResize,
                cancel   : me.onCancel,
                thisObj  : me
            }
        });

        me.resize.context.resource = data.resource;
    }

    //endregion

    //region Tooltip

    /**
     * Creates a tooltip that displays start & end dates. Anchored to the proxy element
     */
    initTooltip() {
        const
            me     = this,
            client = me.client;

        if (me.showTooltip) {
            if (me.tip) {
                me.tip.showBy(me.getTooltipTarget());
            }
            else {
                me.clockTemplate = new ClockTemplate({
                    timeAxisViewModel : client.timeAxisViewModel
                });

                me.tip = new Tooltip({
                    id         : `${client.id}-drag-create-tip`,
                    autoShow   : true,
                    trackMouse : false,
                    getHtml    : me.getTipHtml.bind(me),
                    align      : client.isVertical ? 't-b' : 'b100-t100',
                    hideDelay  : 0,
                    axisLock   : true // Don't want it flipping to the side where we are dragging
                });

                me.tip.on('innerhtmlupdate', me.updateDateIndicator, me);
            }
        }
    }

    updateDateIndicator() {
        const
            me                     = this,
            { startDate, endDate } = me.createContext,
            tip                    = me.tip,
            endDateElement         = tip.element.querySelector('.b-sch-tooltip-enddate');

        if (startDate && endDate) {
            me.clockTemplate.updateDateIndicator(tip.element, startDate);

            endDateElement && me.clockTemplate.updateDateIndicator(endDateElement, endDate);
        }
    }

    /**
     * Updates tooltips contents
     * @returns {*}
     */
    getTipHtml() {
        if (!this.resize.context) return;

        // keeping this on separate row to not mess up look of other declarations
        const
            me        = this,
            client    = me.client,
            {
                edge,
                newX,
                newY,
                elementStartX,
                elementStartY,
                elementWidth,
                elementHeight,
                newWidth,
                newHeight,
                valid,
                message
            } = me.resize.context;

        let start, end;

        if (client.isHorizontal) {
            const
                x    = edge === 'left' ? newX : elementStartX,
                // Coordinates are 0 based, widths are not so subtracting 1 from the widths
                endX = Math.min(Math.max(x + (newWidth || elementWidth) - 1, 0), client.timeAxisViewModel.totalSize - 1);
            start = client.getDateFromCoordinate(x, 'round', true);
            end   = client.getDateFromCoordinate(endX, 'round', true);
        }
        else {
            const
                y    = edge === 'top' ? newY : elementStartY,
                // Coordinates are 0 based, widths are not so subtracting 1 from the widths
                endY = Math.min(Math.max(y + (newHeight || elementHeight) - 1, 0), client.timeAxisViewModel.totalSize - 1);
            start = client.getDateFromCoordinate(y, 'round', true);
            end   = client.getDateFromCoordinate(endY, 'round', true);
        }

        const
            first     = DateHelper.min(start, end),
            last      = DateHelper.max(start, end),
            startText = first && client.getFormattedDate(first),
            endText   = last && end && client.getFormattedEndDate(last, first);

        return me.tipTemplate({
            valid          : valid,
            startDate      : first,
            endDate        : last,
            startText      : startText,
            endText        : endText,
            message        : message,
            startClockHtml : me.clockTemplate.template({
                date : first,
                text : startText,
                cls  : 'b-sch-tooltip-startdate'
            }),
            endClockHtml : me.clockTemplate.template({
                // actual end date and display date may differ
                date : client.getDisplayEndDate(last, first),
                text : endText,
                cls  : 'b-sch-tooltip-enddate'
            })
        });
    }

    //endregion

    //region Finalize (create EventModel)

    finalize(doCreate) {
        const me = this;

        const resetFinalization = () => {
            me.proxy.retainElement = false;
            me.reset();
        };

        const completeFinalization = () => {
            me.client.trigger('afterDragCreate', {
                proxyElement : me.proxy
            });

            resetFinalization();
        };

        if (doCreate) {
            // Call product specific implementation
            const result = me.finalizeDragCreate(me.createContext);

            if (result instanceof Promise) {
                result.then(completeFinalization, resetFinalization);
            }
            else {
                completeFinalization();
            }
        }
        else {
            completeFinalization();
        }
    }

    //endregion

    //region Events

    onElementMouseDown(event) {
        const
            me = this,
            { client } = me;

        // detect mouse down directly in scheduler cell
        if (event.button === 0 && event.target.matches('.b-sch-timeaxis-cell') && !me.disabled && !client.readOnly) {
            const rowRecord = client.isVertical
                ? client.resolveResourceRecord(event)
                : client.getRecordFromElement(event.target);

            // no drag creation in group headers etc.
            if (rowRecord.meta.specialRow) {
                return;
            }

            if (me.preventMultiple && !me.isRowEmpty(rowRecord)) {
                return;
            }

            me.monitorMove = true;
            me.createContext = {
                startEvent   : event,
                startScreenX : event.screenX,
                startScreenY : event.screenY,
                startX       : event.offsetX,
                startY       : event.offsetY,
                cellElement  : event.target,
                rowRecord
            };

            //<debug>
            if (!rowRecord) throw new Error('Row record cannot be resolved');
            //</debug>

            me.mouseUpDetacher = EventHelper.on({
                element : document.body,
                mouseup : 'onMouseUp',
                once    : true,
                thisObj : this
            });
        }
    }

    onElementMouseMove(event) {
        const me = this;

        if (me.monitorMove) {
            const
                { createContext, client } = me,
                delta                     = me.client.isVertical
                    ? createContext.startScreenY - event.screenY
                    : createContext.startScreenX - event.screenX;

            if (Math.abs(delta) > me.dragTolerance) {
                const
                    dateTime = client.getDateFromDomEvent(event),
                    region   = Rectangle.from(client.timeAxisSubGridElement, null, true);

                if (me.handleBeforeDragCreate(dateTime, event) === false) {
                    me.monitorMove = false;
                    return;
                }

                // Math.max with 0 because if the mousedown is within dragTolerance of
                // the left edge and then drag goes left, dragging can begin with
                // the X in negative territory.
                createContext.currentX = Math.max(event.clientX - region.x + client.timeAxisSubGrid.scrollable.x, 0);
                createContext.currentY = Math.max(event.clientY - region.y + client.timeAxisSubGrid.scrollable.y, 0);
                me.addProxy(me.createContext);

                me.initResizer(event, me.createContext);
                me.initTooltip();

                me.monitorMove = false;

                client.trigger('dragCreateStart', {
                    proxyElement : me.proxy
                });
            }
        }

        // If dragcreate is async, resizer was already reset and we should not
        // align tooltip anymore
        if (me.proxy && me.showTooltip && me.resize.context) {
            me.tip.alignTo(me.getTooltipTarget());
        }
    }

    getTooltipTarget() {
        const
            me     = this,
            target = Rectangle.from(me.proxy, null, true);

        if (me.client.isVertical) {
            // Align to the dragged edge of the proxy, and then bump bottom so that the anchor aligns perfectly.
            if (me.resize.context.edge === 'bottom') {
                target.y = target.bottom - 1;
            }
            target.height = me.tip.anchorSize[1] / 2;
        }
        else {
            // Align to the dragged edge of the proxy, and then bump right so that the anchor aligns perfectly.
            if (me.resize.context.edge === 'right') {
                target.x = target.right - 1;
            }
            target.width = me.tip.anchorSize[0] / 2;
        }

        return { target };
    }

    onMouseUp() {
        const me = this;

        me.client.element.classList.remove('b-dragcreating-proxy-sizing');

        me.monitorMove = false;
    }

    /**
     * Prevent right click when drag creating
     * @returns {Boolean}
     * @internal
     */
    onElementContextMenu() {
        if (this.proxy) return false;
    }

    onResizing({ context, event }) {
        const
            me         = this,
            client     = me.client,
            x          = context.edge === 'left' ? context.newX : context.elementStartX,
            y          = context.edge === 'top' ? context.newY : context.elementStartY,
            // Coordinates are 0 based, widths are not so subtracting 1 from the widths
            endX       = Math.min(x + Math.max((context.newWidth || context.elementWidth) - 1, 0), client.timeAxisViewModel.totalSize - 1),
            endY       = Math.min(y + Math.max((context.newHeight || context.elementHeight) - 1, 0), client.timeAxisViewModel.totalSize - 1),
            startCoord = client.isVertical ? y : x,
            endCoord   = client.isVertical ? endY : endX,
            first      = client.getDateFromCoordinate(startCoord, 'round', true),
            last       = client.getDateFromCoordinate(endCoord, 'round', true),
            dc         = me.dateConstraints;

        let start = DateHelper.min(first, last),
            end   = DateHelper.max(first, last);

        if (dc) {
            end = DateHelper.constrain(end, dc.start, dc.end);
            start = DateHelper.constrain(start, dc.start, dc.end);
        }

        Object.assign(me.createContext, {
            startDate : start,
            endDate   : end
        });

        context.valid = me.checkValidity(me.createContext, event);
        context.message = '';

        if (context.valid && typeof context.valid !== 'boolean') {
            context.message = context.valid.message;
            context.valid = context.valid.valid;
        }

        // If users returns nothing, that's interpreted as valid
        context.valid = (context.valid !== false);
    }

    onResize({ context : { valid }, event }) {
        const
            me                     = this,
            { startDate, endDate } = me.createContext;

        if (!startDate || !endDate || (endDate - startDate <= 0)) valid = false;

        me.createContext = me.prepareCreateContextForFinalization(me.createContext, event, me.finalize.bind(me));

        if (valid) {
            me.client.trigger('beforeDragCreateFinalize', {
                context      : me.createContext,
                event,
                proxyElement : me.proxy
            });
        }

        // Drag create could be finalized immediately
        if (me.createContext) {
            // OMG, how not to confuse those contexts?
            me.resize.context.async = me.createContext.async;

            if (!me.createContext.async) {
                me.finalize(valid);
            }
            else {
                // We do not want to remove resizer yet, because it will also remove context and proxy element (or resize to 0 width)
                // Instead we blindfold resize helper to prevent event resize from starting parallel drag create
                me.resize.removeListeners();
            }
        }
    }

    prepareCreateContextForFinalization(createContext, event, finalize, async = false) {
        return Object.assign({}, createContext, {
            async,
            event,
            finalize
        });
    }

    get dragging() {
        return this.resize;
    }

    onCancel() {
        this.cancel();
    }

    cancel() {
        this.reset();
        this.mouseUpDetacher && this.mouseUpDetacher();
    }

    reset() {
        const me = this;

        me.removeProxy();
        me.createContext = null;
        me.monitorMove = false;
    }

    //endregion

    //region Product specific, implemented in subclasses

    checkValidity(context, event) {
        throw new Error('Implement in subclass');
    }

    triggerDragCreateEnd(newRecord, context) {
        throw new Error('Implement in subclass');
    }

    handleBeforeDragCreate(dateTime, event) {
        throw new Error('Implement in subclass');
    }

    isRowEmpty(rowRecord) {
        throw new Error('Implement in subclass');
    }

    //endregion
}
