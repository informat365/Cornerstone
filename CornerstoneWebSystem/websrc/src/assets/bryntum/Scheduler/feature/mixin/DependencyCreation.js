import Base from '../../../Core/Base.js';
import DomHelper from '../../../Core/helper/DomHelper.js';
import Rectangle from '../../../Core/helper/util/Rectangle.js';
import Tooltip from '../../../Core/widget/Tooltip.js';
import EventHelper from '../../../Core/helper/EventHelper.js';
import DependencyBaseModel from '../../model/DependencyBaseModel.js';

/**
 * @module Scheduler/feature/mixin/DependencyCreation
 */

// TODO: refactor this class using StateChart utility to be implemented in Core/util/StateChart.js or XState library if allowed to be used
/**
 * Mixin for Dependencies feature that handles dependency creation (drag & drop from terminals which are shown on hover).
 * Requires {@link Core.mixin.Delayable}, {@link Scheduler.data.api.DataAPI} to be mixed in alongside.
 *
 * @mixin
 */
export default Target => class DependencyCreation extends (Target || Base) {
    //region Config

    static get defaultConfig() {
        return {
            /**
             * `false` to not show a tooltip while creating a dependency
             * @config {Boolean}
             * @default
             */
            showCreationTooltip : true,

            /**
             * A tooltip config object that will be applied to the dependency creation tooltip
             * @config {Object}
             */
            creationTooltip : null,

            /**
             * CSS class used for terminals
             * @config {String}
             * @default
             */
            terminalCls : 'b-sch-terminal',

            /**
             * Where (at events borders) to display terminals
             * @config {String[]}
             * @default
             */
            terminalSides : ['left', 'top', 'right', 'bottom'],

            /**
             * Set to `false` to not allow creating dependencies
             * @config {Boolean}
             * @default
             */
            allowCreate : true
        };
    }

    //endregion

    //region Init & destroy

    construct(view, config) {
        super.construct(view, config);

        const me = this;

        me.view = view;
        me.eventName = view.scheduledEventName;

        me.view.on('readOnly', () => me.updateCreateListeners());

        me.updateCreateListeners();
    }

    doDestroy() {
        const me = this;

        me.schedulerListenersDetacher && me.schedulerListenersDetacher();
        me.abort();
        me.creationTooltip && me.creationTooltip.destroy();

        super.doDestroy();
    }

    updateCreateListeners() {
        const me = this;

        if (!me.view) {
            return;
        }

        me.listenersDetacher && me.listenersDetacher();
        me.listenersDetacher = null;

        if (me.isCreateAllowed) {
            me.listenersDetacher = me.view.on({
                [`${me.eventName}mouseenter`] : me.onTimeSpanMouseEnter,
                [`${me.eventName}mouseleave`] : me.onTimeSpanMouseLeave,
                thisObj                       : me
            });
        }
    }

    set allowCreate(value) {
        this._allowCreate = value;

        this.updateCreateListeners();
    }

    get allowCreate() {
        return this._allowCreate;
    }

    get isCreateAllowed() {
        return this.allowCreate && !this.view.readOnly && !this.disabled;
    }
    //endregion

    //region Events

    /**
     * Show terminals when mouse enters event/task element
     * @private
     */
    onTimeSpanMouseEnter(event) {
        const record = event[`${this.eventName}Record`],
            element = event[`${this.eventName}Element`];
        this.showTerminals(record, DomHelper.down(element, event.source.eventInnerSelector));
    }

    /**
     * Hide terminals when mouse leaves event/task element
     * @private
     */
    onTimeSpanMouseLeave(event) {
        const me = this,
            element = event[`${me.eventName}Element`];

        if (!me.creationData || !DomHelper.isDescendant(element, me.creationData.sourceTerminal.parentElement)) {
            me.hideTerminals(element);
        }
    }

    /**
     * Remove hover styling when mouse leaves terminal. Also hides terminals when mouse leaves one it and not creating a dependency.
     * @private
     */
    onTerminalMouseOut(event) {
        const me = this,
            el = DomHelper.up(event.target, me.view.eventSelector);

        if (!DomHelper.isDescendant(el, me.showingTerminalsFor) && (!me.creationData || el !== me.creationData.sourceTerminal.parentElement)) {
            me.hideTerminals(el);
            me.view.unhover(event);
        }
    }

    /**
     * Start creating a dependency when mouse is pressed over terminal
     * @private
     */
    onTerminalMouseDown(event) {
        const me = this;

        // ignore non-left button clicks
        if (event.button === 0 && !me.creationData) {
            const
                view                   = me.view,
                timeAxisSubGridElement = view.timeAxisSubGridElement,
                terminal               = event.target,
                element                = terminal.parentElement.closest(view.eventSelector),
                viewBounds             = Rectangle.from(view.element, document.body);

            event.preventDefault();
            event.stopPropagation();

            me.creationData = {
                source         : view.resolveTimeSpanRecord(element),
                sourceTerminal : terminal,
                startPoint     : Rectangle.from(terminal, timeAxisSubGridElement).center,
                startX         : event.pageX - viewBounds.x + view.scrollLeft,
                startY         : event.pageY - viewBounds.y + view.scrollTop,
                valid          : false
            };
            if (view.resolveResourceRecord) {
                me.creationData.sourceResource = view.resolveResourceRecord(element);
            }

            me.mouseDetacher = EventHelper.on({
                mouseup : {
                    element : document.body,
                    handler : 'onMouseUp'
                },
                mousemove : {
                    element : timeAxisSubGridElement,
                    handler : 'onMouseMove'
                },
                thisObj : me
            });
        }
    }

    /**
     * Update connector line showing dependency between source and target when mouse moves. Also check if mouse is over a valid
     * target terminal
     * @private
     */
    onMouseMove(event) {
        const
            me     = this,
            { target } = event,
            { view, creationData : data } = me,
            viewBounds = Rectangle.from(view.element, document.body),
            deltaX = (event.pageX - viewBounds.x + view.scrollLeft) - data.startX,
            deltaY = (event.pageY - viewBounds.y + view.scrollTop) - data.startY,
            length = Math.round(Math.sqrt(deltaX * deltaX + deltaY * deltaY)) - 3,
            angle  = Math.atan2(deltaY, deltaX);

        let { connector } = me;

        if (!(data.valid instanceof Promise)) {
            if (!connector) {
                /**
                * Fired on the owning Scheduler/Gantt before a dependency creation drag operation starts. Return false to prevent it
                * @event beforeDependencyCreateDrag
                * @param {object} data
                */
                if (view.trigger('beforeDependencyCreateDrag', { data }) === false) {
                    me.abort();
                    return;
                }
                connector = me.createConnector(data.startPoint.x, data.startPoint.y);
                /**
                * Fired on the owning Scheduler/Gantt when a dependency creation drag operation starts
                * @event dependencyCreateDragStart
                * @param {object} data
                */
                view.trigger('dependencyCreateDragStart', { data });
            }

            connector.style.width = `${length}px`;
            connector.style.transform = `rotate(${angle}rad)`;

            if (target.matches(`.${me.terminalCls}`) && target !== data.sourceTerminal) {
                if (target !== data.targetTerminal) {
                    data.targetTerminal = target;
                    data.target = view.resolveTimeSpanRecord(target);

                    if (view.resolveResourceRecord) {
                        data.targetResource = view.resolveResourceRecord(target);
                    }

                    const
                        fromSide = data.sourceTerminal.dataset.side,
                        toSide = data.targetTerminal.dataset.side,
                        updateValidity = valid => {
                            if (!me.isDestroyed) {
                                data.valid = valid;
                                target.classList.add(valid ? 'b-valid' : 'b-invalid');
                                connector.classList.add(valid ? 'b-valid' : 'b-invalid');

                                /**
                                 * Fired on the owning Scheduler/Gantt when asynchronous dependency validation completes
                                 * @event dependencyValidationComplete
                                 * @param {Object} data
                                 */
                                view.trigger('dependencyValidationComplete', { data });
                            }
                        };

                    let type;

                    // NOTE: Top/Bottom sides are not taken into account due to
                    //       scheduler doesn't check for type value anyway, whereas
                    //       gantt will reject any other dependency types undefined in
                    //       DependencyBaseModel.Type enumeration.
                    switch (true) {
                        case fromSide === 'left' && toSide === 'left':
                            type = DependencyBaseModel.Type.StartToStart;
                            break;
                        case fromSide === 'left' && toSide === 'right':
                            type = DependencyBaseModel.Type.StartToEnd;
                            break;
                        case fromSide === 'right' && toSide === 'left':
                            type = DependencyBaseModel.Type.EndToStart;
                            break;
                        case fromSide === 'right' && toSide === 'right':
                            type = DependencyBaseModel.Type.EndToEnd;
                            break;
                    }

                    /**
                     * Fired on the owning Scheduler/Gantt when asynchronous dependency validation starts
                     * @event dependencyValidationStart
                     * @param {Object} data
                     */
                    view.trigger('dependencyValidationStart', { data });

                    data.valid = this.dataApi.isValidDependency({
                        type,
                        sourceEvent     : data.source,
                        targetEvent     : data.target,
                        dependencyStore : this.dependencyStore
                    });

                    // Promise is returned when using the engine
                    if (data.valid instanceof Promise) {
                        data.valid.then(updateValidity);
                    }
                    else {
                        updateValidity(data.valid);
                    }
                }
            }
            else {
                data.target = data.targetTerminal = null;
                data.valid = false;
                connector.classList.remove('b-valid');
                connector.classList.remove('b-invalid');
            }

            me.lastMouseMoveEvent = event;
        }
    }

    /**
     * Create a new dependency if mouse release over valid terminal. Hides connector
     * @private
     */
    onMouseUp(event) {
        const
            me     = this,
            data   = me.creationData,
            target = event.target;

        const doDependencyDrop = async() => {
            data.targetTerminal = event.target;

            const result = me.createDependency();

            if (result instanceof Promise) {
                await result;
            }

            //data.valid = true; // TODO: remove this line if tests are fine

            /**
             * Fired on the owning Scheduler/Gantt when a dependency drag creation operation succeeds
             * @event dependencyCreateDrop
             * @param {object} data
             */
            me.view.trigger('dependencyCreateDrop', { data });
        };

        const doAfterDependencyDrop = () => {
            /**
             * Fired on the owning Scheduler/Gantt after a dependency drag creation operation finished, no matter to outcome
             * @event afterDependencyCreateDrop
             * @param {object} data
             */
            me.view.trigger('afterDependencyCreateDrop', { data });

            me.abort();
        };

        // TODO: should call finalize and allow user to hook it (as in EventDrag, EventResize)
        if (data.valid && target.matches(`.${me.terminalCls}`)) {

            if (data.valid instanceof Promise) {

                data.valid.then((valid) => {

                    data.valid = valid;

                    if (valid) {
                        doDependencyDrop().then(doAfterDependencyDrop);
                    }
                    else {
                        doAfterDependencyDrop();
                    }

                });
            }
            else {
                doDependencyDrop().then(doAfterDependencyDrop);
            }
        }
        else {
            data.valid = false;
            doAfterDependencyDrop();
        }
    }

    /**
     * Aborts dependency creation, removes proxy and cleans up listeners
     */
    abort() {
        const me     = this,
            { view, creationData } = me;

        // Remove terminals from source and target events.
        if (creationData) {
            const { source, sourceResource, target, targetResource } = creationData;

            if (source) {
                const el = view.getElementFromEventRecord(source, sourceResource);
                if (el) {
                    me.hideTerminals(el);
                }
            }
            if (target) {
                const el = view.getElementFromEventRecord(target, targetResource);
                if (el) {
                    me.hideTerminals(el);
                }
            }
        }

        me.creationData = null;

        me.mouseDetacher && me.mouseDetacher();

        me.removeConnector();
    }

    //endregion

    //region Connector

    /**
     * Creates a connector line that visualizes dependency source & target
     * @private
     */
    createConnector(x, y) {
        const me = this,
            view = me.view;

        me.connector = DomHelper.createElement({
            parent    : view.timeAxisSubGridElement,
            className : `${me.baseCls}-connector`,
            style     : `left:${x}px;top:${y}px`
        });

        view.element.classList.add('b-creating-dependency');

        if (me.showCreationTooltip) {
            if (me.creationTooltip) {
                me.creationTooltip.show();
            }
            else {
                me.creationTooltip = new Tooltip(Object.assign({
                    id             : `${view.id}-dependency-drag-tip`,
                    cls            : 'b-sch-dependency-creation-tooltip',
                    autoShow       : true,
                    anchorToTarget : false,
                    trackMouse     : true,
                    // Do not constrain at all, want it to be able to go outside of the viewport to not get in the way
                    constrainTo    : null,
                    onBeforeAlign  : (e) => me.updateTipContent(e.source, me.creationData),
                    header         : {
                        dock : 'right'
                    }
                }, me.creationTooltip || {}));
            }
        }
        view.scrollManager.startMonitoring({
            element  : view.timeAxisSubGridElement,
            callback : () => me.creationData && me.lastMouseMoveEvent && me.onMouseMove(me.lastMouseMoveEvent),
            thisObj  : me
        });

        return me.connector;
    }

    /**
     * Remove connector
     * @private
     */
    removeConnector(callback) {
        const me = this,
            { connector, view } = me;

        if (connector) {
            connector.classList.add('b-removing');
            connector.style.width = '0';
            me.setTimeout(() => {
                connector.remove();
                me.connector = null;
                if (callback) {
                    callback.call(me);
                }
            }, 200);
        }

        view.element.classList.remove('b-creating-dependency');
        me.creationTooltip && me.creationTooltip.hide();

        view.scrollManager.stopMonitoring(view.timeAxisSubGridElement);
    }

    //endregion

    //region Terminals

    /**
     * Show terminals for specified event at sides defined in #terminalSides.
     * @param {Scheduler.model.TimeSpan} timeSpanRecord Event/task to show terminals for
     * @param {HTMLElement} element Event/task element
     */
    showTerminals(timeSpanRecord, element) {
        const me  = this;

        if (!me.isCreateAllowed) {
            return;
        }

        const
            cls = me.terminalCls,
            terminalsVisibleCls = `${cls}s-visible`;

        // We operate on the event bar, not the wrap
        element = DomHelper.down(element, me.view.eventInnerSelector);

        // bail out if terminals already shown or if view is readonly
        // do not draw new terminals if we are resizing event
        if (!element.classList.contains(terminalsVisibleCls) && !this.view.element.classList.contains('b-resizing-event') && !me.view.readOnly) {

            // create terminals for desired sides
            me.terminalSides.forEach(side => {
                const terminal = DomHelper.createElement({
                    parent    : element,
                    className : `${cls} ${cls}-${side}`,
                    dataset   : {
                        side,
                        feature : true
                    }
                });

                terminal.detacher = EventHelper.on({
                    element   : terminal,
                    mouseout  : 'onTerminalMouseOut',
                    mousedown : {
                        handler : 'onTerminalMouseDown',
                        capture : true
                    },
                    thisObj : me
                });
            });

            element.classList.add(terminalsVisibleCls);
            timeSpanRecord.cls.add(terminalsVisibleCls);

            me.showingTerminalsFor = element;
        }
    }

    /**
     * Hide terminals for specified event
     * @param {HTMLElement} eventElement Event element
     */
    hideTerminals(eventElement) {
        // remove all terminals
        const me = this,
            eventParams = me.client.getTimeSpanMouseEventParams(eventElement),
            timeSpanRecord = eventParams[`${this.eventName}Record`],
            terminalsVisibleCls = `${me.terminalCls}s-visible`;

        DomHelper.forEachSelector(eventElement, `.${me.terminalCls}`, terminal => {
            terminal.detacher && terminal.detacher();
            terminal.remove();
        });

        DomHelper.down(eventElement, me.view.eventInnerSelector).classList.remove(terminalsVisibleCls);
        timeSpanRecord.cls.remove(terminalsVisibleCls);

        me.showingTerminalsFor = null;
    }

    //endregion

    //region Dependency creation

    /**
     * Create a new dependency from source terminal to target terminal
     * @internal
     */
    createDependency() {
        const me     = this,
            data     = me.creationData,
            source   = data.source,
            target   = data.target,
            fromSide = data.sourceTerminal.dataset.side,
            toSide   = data.targetTerminal.dataset.side,
            type     = (fromSide === 'left' ? 0 : 2) + (toSide === 'right' ? 1 : 0);

        return this.dataApi.createDependency({
            sourceEvent     : source,
            targetEvent     : target,
            dependencyStore : this.dependencyStore,
            type,
            fromSide,
            toSide
        });
    }

    //endregion

    //region Tooltip

    /**
     * Update dependency creation tooltip
     * @private
     */
    updateTipContent(tip, data) {
        const me   = this;
        let { valid } = data;

        Object.assign(data, {
            fromText : data.source.name,
            toText   : data.target ? data.target.name : '',
            fromSide : data.sourceTerminal.dataset.side,
            toSide   : data.targetTerminal ? data.targetTerminal.dataset.side : ''
        });

        let tipTitleIconClsSuffix,
            tipTitleText;

        // Promise, when using engine
        if (valid instanceof Promise) {
            tip.element.classList.remove('b-invalid');
            tip.element.classList.add('b-checking');
            tipTitleIconClsSuffix = 'checking';
            tipTitleText = me.L('Checking');

            valid.then(valid => {
                data.valid = valid;
                if (!tip.isDestroyed) {
                    me.updateTipContent(tip, data);
                }
            });
        }
        // Valid
        else if (valid === true) {
            tip.element.classList.remove('b-invalid');
            tip.element.classList.remove('b-checking');
            tipTitleIconClsSuffix = 'valid';
            tipTitleText = me.L('valid');
        }
        // Invalid
        else {
            tip.element.classList.remove('b-checking');
            tip.element.classList.add('b-invalid');
            tipTitleIconClsSuffix = 'invalid';
            tipTitleText = me.L('invalid');
        }

        tip.titleElement.innerHTML = `<i class="b-icon b-icon-${tipTitleIconClsSuffix}"></i>${tipTitleText}`;
        tip.html = `<table class="b-sch-dependency-creation-tooltip">
                        <tr><td>${me.L('from')}: </td><td>${data.fromText}</td><td><div class="b-sch-box b-${data.fromSide}"></div></td></tr>
                        <tr><td>${me.L('to')}: </td><td>${data.toText}</td><td><div class="b-sch-box b-${data.toSide}"></div></td></tr>
                    </table>`;
    }

    //endregion
};
