//TODO: Should it fire more own events instead and rely less on function chaining?

import Base from '../../../Core/Base.js';
import DomDataStore from '../../../Core/data/DomDataStore.js';
import DomHelper from '../../../Core/helper/DomHelper.js';
import Rectangle from '../../../Core/helper/util/Rectangle.js';
import EventHelper from '../../../Core/helper/EventHelper.js';
import StringHelper from '../../../Core/helper/StringHelper.js';
import ObjectHelper from '../../../Core/helper/ObjectHelper.js';
import BrowserHelper from '../../../Core/helper/BrowserHelper.js';

const domEventHandlers = {
    touchstart  : 'onElementTouchStart',
    touchmove   : 'onElementTouchMove',
    touchend    : 'onElementTouchEnd',
    mouseover   : 'onElementMouseOver',
    mouseout    : 'onElementMouseOut',
    mousedown   : 'onElementMouseDown',
    mousemove   : 'onElementMouseMove',
    mouseup     : 'onElementMouseUp',
    click       : 'onHandleElementClick',
    dblclick    : 'onElementDblClick',
    keydown     : 'onElementKeyDown',
    keyup       : 'onElementKeyUp',
    keypress    : 'onElementKeyPress',
    contextmenu : 'onElementContextMenu',
    focus       : 'onGridElementFocus'
};

/**
 * @module Grid/view/mixin/GridElementEvents
 */

/**
 * Mixin for Grid that handles dom events. Some listeners fire own events but all can be chained by features. None of
 * the functions in this class are indented to be called directly.
 *
 * @mixin
 */
export default Target => class GridElementEvents extends (Target || Base) {
    //region Config

    static get defaultConfig() {
        return {
            /**
             * Time in ms until a longpress is triggered
             * @config {Number}
             * @default
             * @category Events
             */
            longPressTime : 400
        };
    }

    //endregion

    //region Events

    /**
     * User clicked in a grid cell
     * @event cellClick
     * @param {Grid.view.Grid} grid
     * @param {Core.data.Model} record
     * @param {Grid.column.Column} column
     * @param {Object} cellSelector
     * @param {HTMLElement} cellElement
     * @param {HTMLElement} target
     * @param {Event} event
     */

    /**
     * User double clicked in a grid cell
     * @event cellDblClick
     * @param {Grid.view.Grid} grid
     * @param {Core.data.Model} record
     * @param {Grid.column.Column} column
     * @param {Object} cellSelector
     * @param {HTMLElement} cellElement
     * @param {HTMLElement} target
     * @param {Event} event
     */

    /**
     * User activated contextmenu in a grid cell
     * @event cellContextMenu
     * @param {Grid.view.Grid} grid
     * @param {Core.data.Model} record
     * @param {Grid.column.Column} column
     * @param {Object} cellSelector
     * @param {HTMLElement} cellElement
     * @param {HTMLElement} target
     * @param {Event} event
     */

    /**
     * User moved moused to a grid cell
     * @event cellMouseOver
     * @param {Grid.view.Grid} grid
     * @param {Core.data.Model} record
     * @param {Grid.column.Column} column
     * @param {Object} cellSelector
     * @param {HTMLElement} cellElement
     * @param {HTMLElement} target
     * @param {Event} event
     */

    /**
     * User moved moused out of a grid cell
     * @event cellMouseOut
     * @param {Grid.view.Grid} grid
     * @param {Core.data.Model} record
     * @param {Grid.column.Column} column
     * @param {Object} cellSelector
     * @param {HTMLElement} cellElement
     * @param {HTMLElement} target
     * @param {Event} event
     */

    //endregion

    //region Event handling

    /**
     * Init listeners for a bunch of dom events. All events are handled by handleEvent().
     * @private
     * @category Events
     */
    initInternalEvents() {
        const handledEvents = Object.keys(domEventHandlers),
            len           = handledEvents.length,
            listeners     = {
                element : this.element,
                thisObj : this
            };

        // Route all events through handleEvent, so that we can capture this.event
        // before we route to the handlers
        for (let i = 0; i < len; i++) {
            listeners[handledEvents[i]] = 'handleEvent';
        }

        EventHelper.on(listeners);
    }

    /**
     * This method find the cell location of the passed event. It returns an object describing the cell.
     * @param {Event} event A Mouse, Pointer or Touch event targeted at part of the grid.
     * @returns {Object} An object containing the following properties:
     * - `cellElement` - The cell element clicked on.
     * - `columnId` - The `id` of the column clicked under.
     * - `record` - The {@link Core.data.Model record} clicked on.
     * - `id` - The `id` of the {@link Core.data.Model record} clicked on.
     * @private
     * @category Events
     */
    getEventData(event) {
        const
            me          = this,
            cellElement = DomHelper.up(event.target, '.b-grid-cell');

        // There is a cell
        if (cellElement) {
            const
                cellData         = DomDataStore.get(cellElement),
                { id, columnId } = cellData,
                record           = me.store.getById(id);

            // Row might not have a record, since we transition record removal
            // https://app.assembla.com/spaces/bryntum/tickets/6805
            return record ? {
                cellElement,
                cellData,
                columnId,
                id,
                record,
                cellSelector : { id, columnId }
            } : null;
        }
    }

    /**
     * Handles all dom events, routing them to correct functions (touchstart -> onElementTouchStart)
     * @param event
     * @private
     * @category Events
     */
    handleEvent(event) {
        if (!this.disabled) {

            this.event = event;

            if (domEventHandlers[event.type]) {
                this[domEventHandlers[event.type]](event);
            }
        }
    }

    //endregion

    //region Touch events

    /**
     * Touch start, chain this function in features to handle the event.
     * @param event
     * @category Touch events
     * @internal
     */
    onElementTouchStart(event) {
        const me = this;

        DomHelper.isTouchEvent = true;

        if (event.touches.length === 1) {
            me.longPressTimeout = setTimeout(() => {
                me.onElementLongPress(event);
                event.preventDefault();
                me.longPressPerformed = true;
            }, me.longPressTime);
        }
    }

    /**
     * Touch move, chain this function in features to handle the event.
     * @param event
     * @category Touch events
     * @internal
     */
    onElementTouchMove(event) {
        if (this.longPressTimeout) {
            clearTimeout(this.longPressTimeout);
            this.longPressTimeout = null;
        }
    }

    /**
     * Touch end, chain this function in features to handle the event.
     * @param event
     * @category Touch events
     * @internal
     */
    onElementTouchEnd(event) {
        const me = this;

        if (me.longPressPerformed) {
            if (event.cancelable) {
                event.preventDefault();
            }
            me.longPressPerformed = false;
        }

        if (me.longPressTimeout) {
            clearTimeout(me.longPressTimeout);
            me.longPressTimeout = null;
        }
    }

    onElementLongPress(event) {}

    //endregion

    //region Mouse events

    // Trigger events in same style when clicking, dblclicking and for contextmenu
    triggerCellMouseEvent(name, event) {
        const me       = this,
            cellData = me.getEventData(event);

        // There is a cell
        if (cellData) {
            const column = me.columns.getById(cellData.columnId);

            me.trigger('cell' + StringHelper.capitalizeFirstLetter(name), {
                grid         : this,
                record       : cellData.record,
                column,
                cellSelector : cellData.cellSelector,
                cellElement  : cellData.cellElement,
                target       : event.target,
                event
            });
        }
    }

    /**
     * Mouse down, chain this function in features to handle the event.
     * @param event
     * @category Mouse events
     * @internal
     */
    onElementMouseDown(event) {
        const me       = this,
            cellData = me.getEventData(event);

        me.skipFocusSelection = true;

        me.triggerCellMouseEvent('mousedown', event);

        // Browser event unification fires a mousedown on touch tap prior to focus.
        if (!event.defaultPrevented) {
            me.onFocusGesture(cellData, event);
        }
    }

    /**
     * Move move, chain this function in features to handle the event.
     * @param event
     * @category Mouse events
     * @internal
     */
    onElementMouseMove(event) {}

    /**
     * Mouse up, chain this function in features to handle the event.
     * @param event
     * @category Mouse events
     * @internal
     */
    onElementMouseUp(event) {}

    /**
     * Called before {@link #function-onElementClick}.
     * Fires 'beforeElementClick' event which can return false to cancel further onElementClick actions.
     * @param event
     * @fires beforeElementClick
     * @category Mouse events
     * @internal
     */

    onHandleElementClick(event) {
        if (this.trigger('beforeElementClick', { event }) !== false) {
            this.onElementClick(event);
        }
    }

    /**
     * Click, select cell on click and also fire 'cellClick' event.
     * Chain this function in features to handle the dom event.
     * @param event
     * @fires cellClick
     * @category Mouse events
     * @internal
     */
    onElementClick(event) {
        const me       = this,
            cellData = me.getEventData(event);

        // There is a cell
        if (cellData) {
            me.triggerCellMouseEvent('click', event);

            // Clear hover styling when clicking in a row to avoid having it stick around if you keyboard navigate
            // away from it
            // https://app.assembla.com/spaces/bryntum/tickets/5848
            DomDataStore.get(cellData.cellElement).row.removeCls('b-hover');
        }
    }

    onFocusGesture(cellData, event) {
        //TODO: should be able to cancel focusCell from listeners
        if (cellData) {
            this.focusCell(cellData.cellSelector, {
                doSelect : true,
                event
            });
        }
    }

    /**
     * Double click, fires 'cellDblClick' event.
     * Chain this function in features to handle the dom event.
     * @param {Event} event
     * @fires cellDblClick
     * @category Mouse events
     * @internal
     */
    onElementDblClick(event) {
        const me          = this,
            target      = event.target;

        me.triggerCellMouseEvent('dblClick', event);

        if (target.classList.contains('b-grid-header-resize-handle')) {
            const header = DomHelper.up(target, '.b-grid-header'),
                column = me.columns.getById(header.dataset.columnId);

            column.resizeToFitContent();
        }
    }

    /**
     * Mouse over, adds 'hover' class to elements.
     * @param event
     * @fires mouseOver
     * @category Mouse events
     * @internal
     */
    onElementMouseOver(event) {
        // bail out early if scrolling
        if (!this.scrolling) {
            const cellElement = DomHelper.up(event.target, '.b-grid-cell');

            if (cellElement) {
                const row = DomDataStore.get(cellElement).row;

                // No hover effect needed if a mouse button is pressed (like when resizing window, region, or resizing something etc).
                // NOTE: 'buttons' not supported in Safari
                if (row && (typeof event.buttons !== 'number' || event.buttons === 0)) {
                    this.hoveredRow = row;
                }

                this.triggerCellMouseEvent('mouseOver', event);
            }

            /**
             * Mouse moved in over element in grid
             * @event mouseOver
             * @param {Event} event
             */
            this.trigger('mouseOver', { event });
        }
    }

    /**
     * Mouse out, removes 'hover' class from elements.
     * @param event
     * @fires mouseOut
     * @category Mouse events
     * @internal
     */
    onElementMouseOut(event) {
        this.hoveredRow = null;

        // bail out early if scrolling
        if (!this.scrolling) {
            const cellElement = DomHelper.up(event.target, '.b-grid-cell');

            if (cellElement) {
                this.triggerCellMouseEvent('mouseOut', event);
            }

            /**
             * Mouse moved out from element in grid
             * @event mouseOut
             * @param {Event} event
             */
            this.trigger('mouseOut', { event });
        }
    }

    set hoveredRow(row) {
        const me = this;

        // Unhover
        if (me._hoveredRow && !me._hoveredRow.isDestroyed) {
            me._hoveredRow.removeCls('b-hover');
            me._hoveredRow = null;
        }

        // Hover
        if (row && !me.scrolling) {
            me._hoveredRow = row;
            row.addCls('b-hover');
        }
    }

    //endregion

    //region Keyboard events

    /**
     * Key down, handles arrow keys for selection.
     * Chain this function in features to handle the dom event.
     * @param event
     * @category Keyboard events
     * @internal
     */
    onElementKeyDown(event) {
        const me          = this;

        // flagging event with handled = true used to signal that other features should probably not care about it.
        // for this to work you should specify overrides for onElementKeyDown to be run before this function
        // (see for example CellEdit feature)
        if (event.handled) return;

        if (event.target.matches('.b-grid-header.b-depth-0')) {
            me.handleHeaderKeyDown(event);
        }
        else if (event.target === this.element || (BrowserHelper.isIE11 && event.currentTarget === this.element)) {
            // IE11 Browser check is not placed in EventHelper to maintain built-in delegated functionality
            me.handleViewKeyDown(event);
        }
        // If focus is *within* a cell (eg WidgetColumn or CheckColumn), jump up to focus the cell.
        else if (event.key === 'Escape' && me.isActionableLocation) {
            const focusedCell = ObjectHelper.clone(me.focusedCell);
            focusedCell.element = null;
            me.focusCell(focusedCell);
            DomHelper.focusWithoutScrolling(me.element);
        }
    }

    handleViewKeyDown(event) {
        const me = this;

        switch (event.key) {
            case 'ArrowLeft':
                event.preventDefault();
                return me.navigateLeft(event);
            case 'ArrowRight':
                event.preventDefault();
                return me.navigateRight(event);
            case 'ArrowUp':
                event.preventDefault();
                return me.navigateUp(event);
            case 'ArrowDown':
                event.preventDefault();
                return me.navigateDown(event);
        }
    }

    handleHeaderKeyDown(event) {
        const me = this,
            column = me.columns.getById(event.target.dataset.columnId);

        column.onKeyDown && column.onKeyDown(event);
        switch (event.key) {
            case 'ArrowLeft':
                const prev = me.columns.getAdjacentVisibleLeafColumn(column, false);

                if (prev) {
                    let element = me.getHeaderElement(prev.id);
                    element.focus();
                }

                break;

            case 'ArrowRight':
                const next = me.columns.getAdjacentVisibleLeafColumn(column, true);

                if (next) {
                    let element = me.getHeaderElement(next.id);
                    element.focus();
                }

                break;

            case 'Enter':
                const element = me.getHeaderElement(column.id);

                element.click();
                break;
        }
    }

    /**
     * Key press, chain this function in features to handle the dom event.
     * @param event
     * @category Keyboard events
     * @internal
     */
    onElementKeyPress(event) {}

    /**
     * Key up, chain this function in features to handle the dom event.
     * @param event
     * @category Keyboard events
     * @internal
     */
    onElementKeyUp(event) {}

    //endregion

    //region Other events

    /**
     * Context menu, chain this function in features to handle the dom event.
     * In most cases, include ContextMenu feature instead.
     * @param event
     * @category Other events
     * @internal
     */
    onElementContextMenu(event) {
        const me       = this,
            cellData = me.getEventData(event);

        // There is a cell
        if (cellData) {
            me.triggerCellMouseEvent('contextMenu', event);

            // Focus on tap for touch events.
            // Selection follows from focus.
            if (DomHelper.isTouchEvent) {
                me.onFocusGesture(cellData, event);
            }
        }
    }

    /**
     * Overrides empty base function in View, called when view is resized.
     * @fires resize
     * @param element
     * @param width
     * @param height
     * @param oldWidth
     * @param oldHeight
     * @category Other events
     * @internal
     */
    onInternalResize(element, width, height, oldWidth, oldHeight) {
        const me = this;

        if (me._devicePixelRatio && me._devicePixelRatio !== window.devicePixelRatio) {
            // Pixel ratio changed, likely because of browser zoom. This affects the relative scrollbar width also
            DomHelper.resetScrollBarWidth();
        }

        me._devicePixelRatio = window.devicePixelRatio;
        // cache to avoid recalculations in the middle of rendering code (RowManger#getRecordCoords())
        me._bodyRectangle = Rectangle.client(me.bodyContainer);

        super.onInternalResize(...arguments);

        if (height !== oldHeight) {
            me._bodyHeight = me.bodyContainer.offsetHeight;
            if (me.isPainted) {
                // initial height will be set from render(),
                // it reaches onInternalResize too early when rendering, headers/footers are not sized yet
                me.rowManager.initWithHeight(me._bodyHeight);
            }
        }
        me.refreshVirtualScrollbars();

        if (width !== oldWidth) {
            // Slightly delay to avoid resize loops.
            me.setTimeout(() => {
                if (!me.isDestroyed) {
                    me.updateResponsive(width, oldWidth);
                }
            }, 0);
        }
    }

    //endregion

    // This does not need a className on Widgets.
    // Each *Class* which doesn't need 'b-' + constructor.name.toLowerCase() automatically adding
    // to the Widget it's mixed in to should implement thus.
    get widgetClass() {}
};
