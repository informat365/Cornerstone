import Base from '../../Base.js';
import GlobalEvents from '../../GlobalEvents.js';
import DomHelper from '../../helper/DomHelper.js';

// TODO: Use mousedown pos and not center too reposition

/**
 * @module Core/helper/mixin/DragHelperContainer
 */

/**
 * Mixin for DragHelper that handles dragging elements between containers (or rearranging within)
 *
 * @private
 * @mixin
 */
export default Target => class DragHelperContainer extends (Target || Base) {
    //region Init

    /**
     * Initialize container drag mode.
     * @private
     */
    initContainerDrag() {
        const me = this;
        //use container drag as default mode
        if (!me.mode) me.mode = 'container';
        if (me.mode === 'container' && !me.containers) throw new Error('Container drag mode must specify containers');
    }

    //endregion

    //region Grab, update, finish

    /**
     * Grab an element which can be dragged between containers.
     * @private
     * @param event
     * @returns {Boolean}
     */
    grabContainerDrag(event) {
        const me = this;

        // allow specified selectors to prevent drag
        if (!me.ignoreSelector || !DomHelper.up(event.target, me.ignoreSelector)) {
            // go up from "handle" to draggable element
            const element = DomHelper.getAncestor(event.target, me.containers, me.outerElement);

            if (element) {
                const box = element.getBoundingClientRect();

                me.context = {
                    element,
                    valid            : true,
                    action           : 'container',
                    offsetX          : event.pageX - box.left,
                    offsetY          : event.pageY - box.top,
                    originalPosition : {
                        parent : element.parentElement,
                        prev   : element.previousElementSibling,
                        next   : element.nextElementSibling
                    }
                };
            }

            return true;
        }

        return false;
    }

    /**
     * Starts dragging, called when mouse moves first time after grabbing
     * @private
     * @param event
     */
    startContainerDrag(event) {
        const
            { context } = this,
            { element : dragElement } = context,
            div      = dragElement.cloneNode(true),
            box      = dragElement.getBoundingClientRect();

        // init drag proxy
        div.classList.add(this.dragProxyCls);
        div.classList.add(this.draggingCls);
        document.body.appendChild(div);
        context.dragProxy = div;

        // Always set the proxy element width manually, drag target could be sized with flex or % width
        div.style.width  = box.width + 'px';
        div.style.height = box.height + 'px';
        DomHelper.setTranslateXY(context.dragProxy, box.left, box.top);

        // style dragged element
        context.dragging = dragElement;
        dragElement.classList.add(this.dropPlaceholderCls);
    }

    /**
     * Move the placeholder element into its new position on valid drag.
     * @private
     * @param event
     */
    updateContainerDrag(event) {
        const me       = this,
            context = me.context;

        if (!context.started || !context.targetElement) return;

        const containerElement = DomHelper.getAncestor(context.targetElement, me.containers, 'b-grid'),
            willLoseFocus = context.dragging && context.dragging.contains(document.activeElement);

        if (containerElement && DomHelper.isDescendant(context.element, containerElement)) {
            // dragging over part of self, do nothing
            return;
        }

        // The dragging element contains focus, and moving it within the DOM
        // will cause focus loss which might affect an encapsulating autoClose Popup.
        // Prevent focus loss handling during the DOM move.
        if (willLoseFocus) {
            GlobalEvents.suspendFocusEvents();
        }
        if (containerElement && context.valid) {
            me.moveNextTo(containerElement, event);
        }
        else {
            // dragged outside of containers, revert position
            me.revertPosition();
        }
        if (willLoseFocus) {
            GlobalEvents.resumeFocusEvents();
        }

        event.preventDefault();
    }

    /**
     * Finalize drag, fire drop.
     * @private
     * @param event
     * @fires drop
     */
    finishContainerDrag(event) {
        const me       = this,
            context = me.context,
            // extracting variables to make code more readable
            { dragging, dragProxy, valid, draggedTo, insertBefore, originalPosition } = context;

        if (dragging) {
            // needs to have a valid target
            context.valid = valid && draggedTo &&
                    // no drop on self or parent
                    (dragging !== insertBefore || originalPosition.parent !== draggedTo);

            context.finalize = (valid = context.valid) => {
                // revert if invalid (and context still exists, might have been aborted from outside)
                if (!valid && this.context) {
                    me.revertPosition();
                }

                dragging.classList.remove(me.dropPlaceholderCls);
                dragProxy.remove();

                me.reset();
            };

            // allow async finalization by setting async to true on context in drop handler,
            // requires implementer to call context.finalize later to finish the drop
            context.async = false;

            me.trigger('drop', { context, event });

            if (!context.async) {
                // finalize immediately
                context.finalize();
            }
            else {
                context.awaitingFinalization = true;
            }
        }
    }

    /**
     * Aborts a drag operation.
     * @private
     * @param {Boolean} [invalid]
     * @param {Object} [event]
     * @param {Boolean} [silent]
     */
    abortContainerDrag(invalid = false, event = null, silent = false) {
        const me = this,
            context = me.context;

        if (context.dragging) {
            context.dragging.classList.remove(me.dropPlaceholderCls);
            context.dragProxy.remove();

            me.context = {};
        }

        if (!silent) {
            me.trigger(invalid ? 'drop' : 'abort', { context, event });
        }
    }

    //endregion

    //region Helpers

    /**
     * Starts a drag operation by creating a proxy and storing which element is being dragged.
     * @private
     */
    // startDrag() {
    //     const context = this.context,
    //           div      = context.element.cloneNode(true);
    //
    //     // init drag proxy
    //     div.classList.add('b-drag-proxy');
    //     document.body.appendChild(div);
    //     context.dragProxy = div;
    //
    //     // style dragged element
    //     context.dragging = context.element;
    //     context.dragging.classList.add('myClass');
    // }

    /**
     * Updates the drag proxy position.
     * @private
     * @param event
     */
    updateContainerProxy(event) {
        const me = this,
            context = me.context,
            proxy = context.dragProxy;

        let newX = event.pageX - context.offsetX,
            newY = event.pageY - context.offsetY;

        if (typeof me.minX === 'number') {
            newX = Math.max(me.minX, newX);
        }

        if (typeof me.maxX === 'number') {
            newX = Math.min(me.maxX - proxy.offsetWidth, newX);
        }

        if (typeof me.minY === 'number') {
            newY = Math.max(me.minY, newY);
        }

        if (typeof me.maxY === 'number') {
            newY = Math.min(me.maxY  - proxy.offsetHeight, newY);
        }

        if (me.lockX) {
            DomHelper.setTranslateY(proxy, newY);
        }
        else if (me.lockY) {
            DomHelper.setTranslateX(proxy, newX);
        }
        else {
            DomHelper.setTranslateXY(proxy, newX, newY);
        }

        let targetElement;

        if (event.type === 'touchmove') {
            let touch     = event.changedTouches[0];
            targetElement = document.elementFromPoint(touch.clientX, touch.clientY);
        }
        else {
            targetElement = event.target;
        }

        context.targetElement = targetElement;
    }

    /**
     * Positions element being dragged in relation to targetElement.
     * @private
     * @param targetElement
     * @param event
     */
    moveNextTo(targetElement, event) {
        const context = this.context,
            dragElement = context.dragging,
            parent   = targetElement.parentElement;

        if (targetElement !== dragElement) {
            // dragged over a container and not over self, calculate where to insert

            const centerX = targetElement.getBoundingClientRect().left + targetElement.offsetWidth / 2;

            if (event.pageX < centerX) {
                // dragged left of target center, insert before
                parent.insertBefore(dragElement, targetElement);
                context.insertBefore = targetElement;
            }
            else {
                // dragged right of target center, insert after
                if (targetElement.nextElementSibling) {
                    // check that not dragged to the immediate left of self. in such case, position should not change
                    if (targetElement.nextElementSibling !== dragElement) {
                        context.insertBefore = targetElement.nextElementSibling;
                        parent.insertBefore(dragElement, targetElement.nextElementSibling);
                    }
                    else if (!context.insertBefore && dragElement.parentElement.lastElementChild !== dragElement) {
                        // dragged left initially, should stay in place (checked in finishContainerDrag)
                        // TODO: or flag as invalid drag? since no change...
                        context.insertBefore = targetElement.nextElementSibling;
                    }
                }
                else {
                    parent.appendChild(dragElement);
                    context.insertBefore = null;
                }
            }

            context.draggedTo = parent;
        }
    }

    /**
     * Moves element being dragged back to its original position.
     * @private
     */
    revertPosition() {
        const context = this.context,
            original = context.originalPosition;

        // revert to correct location
        if (original.next) {
            const isNoop = original.next && original.next.previousSibling === context.dragging || (!original.next && context.dragging === original.parent.lastChild);

            if (!isNoop) {
                original.parent.insertBefore(context.dragging, original.next);
            }
        }
        else {
            original.parent.appendChild(context.dragging);
        }

        // no target container
        context.draggedTo = null;
    }

    //endregion
};
