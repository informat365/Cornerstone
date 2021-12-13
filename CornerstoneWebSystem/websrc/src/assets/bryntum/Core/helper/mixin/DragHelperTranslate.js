import Base from '../../Base.js';
import DomHelper from '../DomHelper.js';
import Delayable from '../../mixin/Delayable.js';
import Rectangle from '../util/Rectangle.js';

/**
 * @module Core/helper/mixin/DragHelperTranslate
 */

const noScroll = { pageXOffset : 0, pageYOffset : 0 };

/**
 * Mixin for DragHelper that handles repositioning (translating) an element within its container
 *
 * @private
 * @mixin
 */
export default Target => class DragHelperTranslate extends Delayable(Target || Base) {
    //region Init

    /**
     * Initialize translation drag mode.
     * @private
     */
    initTranslateDrag() {
        const me = this;
        if (!me.isElementDraggable && me.targetSelector) {
            me.isElementDraggable = element => DomHelper.up(element, me.targetSelector);
        }
    }

    //endregion

    //region Grab, update, finish

    /**
     * Grab an element which can be moved using translation.
     * @private
     * @param event
     * @returns {Boolean}
     */
    grabTranslateDrag(event) {
        const me      = this,
            element = me.getTarget(event);

        if (element) {
            me.context = {
                valid  : true,
                action : me.mode, // translateX or translateXY...
                element,

                startPageX   : event.pageX,
                startPageY   : event.pageY,
                startClientX : event.clientX,
                startClientY : event.clientY
            };

            return true;
        }

        return false;
    }

    getTarget(event) {
        return DomHelper.up(event.target, this.targetSelector);
    }

    /**
     * Start translating, called on first mouse move after dragging
     * @private
     * @param event
     */
    startTranslateDrag(event) {
        const me      = this,
            context = me.context,
            { relatedElements } = context;

        let element = context.dragProxy || context.element;

        if (element && !context.started) {
            const grabbed       = element,
                grabbedParent = element.parentElement,
                // When cloning an element to be dragged, we place it in BODY by default
                dragWithin    = me.dragWithin = me.dragWithin || (me.cloneTarget && document.body),
                outerElement = me.outerElement;

            if (me.cloneTarget) {
                const offsetX      = DomHelper.getOffsetX(element, dragWithin),
                    offsetY      = DomHelper.getOffsetY(element, dragWithin),
                    offsetWidth  = element.offsetWidth,
                    offsetheight = element.offsetHeight;

                element = this.createProxy(element);

                // Match the grabbed element's size and position.
                DomHelper.setTranslateXY(element, offsetX, offsetY);
                element.style.width = `${offsetWidth}px`;
                element.style.height = `${offsetheight}px`;

                element.classList.add(me.dragProxyCls);
                dragWithin.appendChild(element);

                grabbed.classList.add('b-drag-original');

                if (me.hideOriginalElement) {
                    grabbed.classList.add('b-hidden');
                }
            }

            Object.assign(me.context, {
                // The element which we're moving, could be a cloned version of grabbed, or the grabbed element itself
                element,

                // The original element upon which the mousedown event triggered a drag operation
                grabbed,

                // The parent of the original element where the pointerdown was detected - to be able to restore after an invalid drop
                grabbedParent,

                // The next sibling of the original element where the pointerdown was detected - to be able to restore after an invalid drop
                grabbedNextSibling : element.nextElementSibling,

                // elements position within parent element
                elementStartX : DomHelper.getTranslateX(element),
                elementStartY : DomHelper.getTranslateY(element),
                elementX      : DomHelper.getOffsetX(element, dragWithin || outerElement),
                elementY      : DomHelper.getOffsetY(element, dragWithin || outerElement),

                scrollX : 0,
                scrollY : 0,

                scrollManagerElementContainsDragProxy : !me.cloneTarget || dragWithin === outerElement
            });

            element.classList.add(me.draggingCls);

            if (dragWithin) {
                context.parentElement = element.parentElement;

                if (dragWithin !== element.parentElement) {
                    dragWithin.appendChild(element);
                }
                me.updateTranslateProxy(event);
            }

            if (relatedElements) {
                relatedElements.forEach(r => {
                    r.classList.add(me.draggingCls);
                });
            }
        }
    }

    /**
     * Limit translation to outer bounds and specified constraints
     * @private
     * @param element
     * @param x
     * @param y
     * @returns {{constrainedX: *, constrainedY: *}}
     */
    applyConstraints(element, x, y) {
        const me         = this,
            dragWithin = me.dragWithin,
            { pageXOffset, pageYOffset } = dragWithin === document.body ? window : noScroll;

        // limit to outer elements edges
        if (dragWithin && me.constrain) {
            if (x < 0) x = 0;
            if (x + element.offsetWidth > dragWithin.scrollWidth) x = dragWithin.scrollWidth - element.offsetWidth;

            if (y < 0) y = 0;
            if (y + element.offsetHeight > dragWithin.scrollHeight) y = dragWithin.scrollHeight - element.offsetHeight;
        }

        // limit horizontally
        if (typeof me.minX === 'number') {
            x = Math.max(me.minX + pageXOffset, x);
        }
        if (typeof me.maxX === 'number') {
            x = Math.min(me.maxX + pageXOffset, x);
        }

        // limit vertically
        if (typeof me.minY === 'number') {
            y = Math.max(me.minY + pageYOffset, y);
        }
        if (typeof me.maxY === 'number') {
            y = Math.min(me.maxY + pageYOffset, y);
        }

        return { constrainedX : x, constrainedY : y };
    }

    /**
     * Update elements translation on mouse move.
     * @private
     * @param event
     */
    updateTranslateProxy(event, scrollManagerConfig) {
        const
            me       = this,
            { mode, lockX, lockY } = me,
            context  = me.context,
            element  = context.dragProxy || context.element,
            { relatedElements, relatedElDragFromPos } = context;

        // If we are cloning the dragged element outside of the element(s) monitored by the ScrollManager, then no need to take the scrollManager scroll values into account
        // since it is only relevant when dragProxy is inside the Grid (where scroll manager operates).
        if (context.scrollManagerElementContainsDragProxy && scrollManagerConfig) {
            context.scrollX = scrollManagerConfig.scrollRelativeLeft;
            context.scrollY = scrollManagerConfig.scrollRelativeTop;
        }

        context.pageX = event.pageX;
        context.pageY = event.pageY;
        context.clientX = event.clientX;
        context.clientY = event.clientY;

        const
            newX                           = context.elementStartX + event.pageX - context.startPageX + context.scrollX,
            newY                           = context.elementStartY + event.pageY - context.startPageY + context.scrollY,
            { constrainedX, constrainedY } = me.applyConstraints(element, newX, newY);

        if (mode === 'translateXY' && !(lockX || lockY)) {
            DomHelper.setTranslateXY(element, constrainedX, constrainedY);
        }
        else if (mode === 'translateX' || lockY) {
            DomHelper.setTranslateX(element, constrainedX);
        }
        else if (mode === 'translateY' || lockX) {
            DomHelper.setTranslateY(element, constrainedY);
        }

        if (relatedElements) {
            const
                deltaX = (!lockX && mode !== 'translateY') ? constrainedX - context.elementStartX : 0,
                deltaY = (!lockY && mode !== 'translateX') ? constrainedY - context.elementStartY : 0;

            relatedElements.forEach((r, i) => {
                const [x, y] = relatedElDragFromPos[i];

                DomHelper.setTranslateXY(r, x + deltaX, y + deltaY);
            });
        }

        context.newX = constrainedX;
        context.newY = constrainedY;
    }

    /**
     * Finalize drag, fire drop.
     * @private
     * @param event
     * @fires drop
     */
    finishTranslateDrag(event) {
        const me       = this,
            context  = me.context,
            xChanged = context.newX !== context.elementStartX,
            yChanged = context.newY !== context.elementStartY,
            element  = context.dragProxy || context.element,
            { relatedElements } = context;

        function cleanUp() {
            element.classList.remove(me.invalidCls);
            element.classList.remove(me.draggingCls);

            if (relatedElements) {
                relatedElements.forEach(r => {
                    r.classList.remove(me.invalidCls);
                    r.classList.remove(me.draggingCls);
                });
            }
            // If we're currently aborting, the b-hidden CSS class will be removed after animation is complete
            if (!element.classList.contains('b-aborting')) {
                if (me.hideOriginalElement) {
                    context.grabbed.classList.remove('b-hidden');
                }
                context.grabbed.classList.remove('b-drag-original');
            }
        }

        if (!me.ignoreSamePositionDrop || (me.mode !== 'translateY' && xChanged) || (me.mode !== 'translateX' && yChanged)) {

            if (context.valid === false) {
                me.abortTranslateDrag(true, event);
            }
            else {
                const targetRect = Rectangle.from(me.dragWithin || me.outerElement);

                if ((!me.minX && (event.pageX < targetRect.left)) ||
                    (!me.maxX && (event.pageX > targetRect.right)) ||
                    (!me.minY && (event.pageY < targetRect.top)) ||
                    (!me.maxY && (event.pageY > targetRect.bottom))) {
                    // revert location when dropped outside allowed element
                    context.valid = false;
                    me.abortTranslateDrag(true, event);
                }
                else {
                    context.finalize = (valid = context.valid) => {
                        if (context.asyncCleanup) {
                            cleanUp();
                        }

                        // abort if invalid (and context still exists, might have been aborted from outside)
                        if (!valid && me.context) {
                            // abort if flagged as invalid, without triggering abort or drop again
                            me.abortTranslateDrag(true, null, true);
                        }
                        else if (me.cloneTarget || context.dragProxy) {
                            element.remove();
                        }

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

            if (!context.asyncCleanup) {
                cleanUp();
            }
        }
        else {
            // no change, abort but not as invalid
            me.abortTranslateDrag(false, event);
        }
    }

    /**
     * Abort translation
     * @private
     * @param invalid
     * @fires abort
     */
    abortTranslateDrag(invalid = false, event = null, silent = false) {
        const me      = this,
            context = me.context,
            { relatedElements, relatedElStartPos, grabbed } = context,
            element = context.dragProxy || context.element,
            resetEl = me.cloneTarget && me.hideOriginalElement ? grabbed : element;

        let { elementStartX, elementStartY } = context;

        if (element && context.started) {
            // Put the dragged element back where it was
            if (!me.cloneTarget && me.dragWithin && me.dragWithin !== context.grabbedParent) {
                context.grabbedParent.insertBefore(element, context.grabbedNextSibling);
            }

            grabbed.classList.remove('b-hidden');

            // Align the now visible grabbed element with the clone, so that it looks like it's
            // sliding back into place when the clone is removed
            if (me.cloneTarget) {
                if (me.hideOriginalElement) {
                    [elementStartX, elementStartY] = DomHelper.getTranslateXY(grabbed);
                    DomHelper.alignTo(grabbed, element);

                    // The getBoundingClientRect is important. The aligning above must be processed
                    // by a forced synchronous layout *before* the b-aborting class is added below.
                    resetEl.getBoundingClientRect();
                }

                element.remove();
            }

            // animated restore of position.
            resetEl.classList.add('b-aborting');

            // Move the elements back to their original positions.
            if (me.mode === 'translateXY' && !(me.lockX || me.lockY)) {
                DomHelper.setTranslateXY(resetEl, elementStartX, elementStartY);
                relatedElements && relatedElements.forEach((element, i) => {
                    element.classList.add('b-aborting');
                    DomHelper.setTranslateXY(element, relatedElStartPos[i][0], relatedElStartPos[i][1]);
                });
            }
            if (me.mode === 'translateX' || me.lockY) {
                DomHelper.setTranslateX(resetEl, elementStartX);
                relatedElements && relatedElements.forEach((element, i) => {
                    element.classList.add('b-aborting');
                    DomHelper.setTranslateX(element, relatedElStartPos[i][0]);
                });
            }
            if (me.mode === 'translateY' || me.lockX) {
                DomHelper.setTranslateY(resetEl, elementStartY);
                relatedElements && relatedElements.forEach((element, i) => {
                    element.classList.add('b-aborting');
                    DomHelper.setTranslateY(element, relatedElStartPos[i][1]);
                });
            }

            me.setTimeout(() => {
                resetEl.classList.remove('b-aborting');

                grabbed.classList.remove('b-dragging');
                grabbed.classList.remove('b-drag-original');

                if (!me.cloneTarget) {
                    relatedElements && relatedElements.forEach((element, i) => {
                        element.classList.remove('b-aborting');
                    });
                    element.classList.remove('b-aborting');
                    element.classList.remove(me.draggingCls);
                    element.classList.remove(me.invalidCls);
                }
            }, me.transitionDuration, undefined, true);

            if (!silent) {
                me.trigger(invalid ? 'drop' : 'abort', { context, event });
            }
        }

        me.reset();
    }

    //endregion
};
