import Base from '../Base.js';
import Events from '../mixin/Events.js';
import DragHelperContainer from './mixin/DragHelperContainer.js';
import DragHelperTranslate from './mixin/DragHelperTranslate.js';
import BrowserHelper from './BrowserHelper.js';
import EventHelper from './EventHelper.js';
import { base } from './MixinHelper.js';
import DomHelper from './DomHelper.js';

//TODO: add touch support
//TODO: add pointer events support

/**
 * @module Core/helper/DragHelper
 */

const documentListeners = {
    down       : 'onMouseDown',
    move       : 'onMouseMove',
    up         : 'onMouseUp',
    docclick   : 'onDocumentClick',
    touchstart : 'onTouchStart',
    touchmove  : 'onTouchMove',
    touchend   : 'onTouchEnd',
    keydown    : 'onKeyDown'
};

/**
 * Helps with drag and drop. Supports two modes of dragging:
 * * `container` - moving/rearranging elements within and between specified containers
 * * `translateXY` - repositioning an element withing its container
 *
 * Usage examples:
 * ```
 * // dragging element between containers
 * let dragHelper = new DragHelper({
 *   mode       : 'container',
 *   containers : [ container1, container2 ]
 * });
 *
 * // dragging element within container
 * let dragHelper = new DragHelper({
 *   mode    : 'translateXY',
 *   targetSelector: 'div.moveable'
 * });
 * ```
 * In the various Drag event handlers, you will have access to the raw DOM event and some useful `context` of the drag operation:
 *
 * ```
 *  myDrag.on({
 *      drag : ({event , context}) {
 *            // The element which we're moving, could be a cloned version of grabbed, or the grabbed element itself
 *           const element = context.element;
 *
 *           // The original mousedown element upon which triggered the drag operation
 *           const grabbed = context.grabbed;
 *
 *           // The target under the current mouse / pointer / touch position
 *           const target = context.target;
 *       }
 *  });
 * ```
 *
 * Simple drag drop with a drop target specified:
 * ```
 * export default class MyDrag extends DragHelper {
        static get defaultConfig() {
            return {
                // Don't drag the actual cell element, clone it
                cloneTarget        : true,
                mode               : 'translateXY',
                // Only allow drops on DOM elements with 'yourDropTarget' CSS class specified
                dropTargetSelector : '.yourDropTarget',

                // Only allow dragging elements with the 'draggable' CSS class
                targetSelector : '.draggable'
            };
        }

        construct(config) {
            const me = this;

            super.construct(config);

            me.on({
                dragstart : me.onDragStart
            });
        }

        onDragStart({ event, context }) {
            const target = context.target;

            // Here you identify what you are dragging (an image of a user, grid row in an order table etc) and map it to something in your
            // data model. You can store your data on the context object which is available to you in all drag-related events
            context.userId = target.dataset.userId;
        }

        onEquipmentDrop({ context, event }) {
            const me = this;

            if (context.valid) {
                const userId   = context.userId,
                      droppedOnTarget = context.target;

                console.log(`You dropped user ${userStore.getById(userId).name} on ${droppedOnTarget}`, droppedOnTarget);

                // Dropped on a scheduled event, display toast
                WidgetHelper.toast(`You dropped user ${userStore.getById(userId).name} on ${droppedOnTarget}`);

                // tell the drag helper the operation is finished
                me.context.finalize();
            }
        }
    };
 * ```
 * @mixes Core/helper/mixin/DragHelperContainer
 * @mixes Core/helper/mixin/DragHelperTranslate
 * @mixes Core/mixin/Events
 * @extends Core/Base
 */
export default class DragHelper extends base(Base).mixes(Events, DragHelperContainer, DragHelperTranslate) {
    //region Config

    static get defaultConfig() {
        return {
            /**
             * Drag proxy CSS class
             * @config {String}
             * @default
             * @private
             */
            dragProxyCls : 'b-drag-proxy',

            /**
             * CSS class added when drag is invalid
             * @config {String}
             * @default
             */
            invalidCls : 'b-drag-invalid',

            /**
             * CSS class added to the source element in Container drag
             * @config {String}
             * @default
             * @private
             */
            draggingCls : 'b-dragging',

            /**
             * CSS class added to the source element in Container drag
             * @config {String}
             * @default
             * @private
             */
            dropPlaceholderCls : 'b-drop-placeholder',

            /**
             * The amount of pixels to move mouse before it counts as a drag operation
             * @config {Number}
             * @default
             */
            dragThreshold : 5,

            /**
             * The outer element where the drag helper will operate (attach events to it and use as outer limit when looking for ancestors)
             * @config {HTMLElement}
             * @default
             */
            outerElement : document.body,

            /**
             * Outer element that limits where element can be dragged
             * @config {HTMLElement}
             * @default
             */
            dragWithin : null,

            /**
             * Constrain translate drag to dragWithin elements bounds (set to false to allow it to "overlap" edges)
             * @config {Boolean}
             * @default
             */
            constrain : true,

            /**
             * Smallest allowed x when dragging horizontally.
             * @config {Number}
             * @default
             */
            minX : null,

            /**
             * Largest allowed x when dragging horizontally.
             * @config {Number}
             * @default
             */
            maxX : null,

            /**
             * Smallest allowed y when dragging horizontally.
             * @config {Number}
             * @default
             */
            minY : null,

            /**
             * Largest allowed y when dragging horizontally.
             * @config {Number}
             * @default
             */
            maxY : null,

            /**
             * Enabled dragging, specify mode:
             * <table>
             * <tr><td>container<td>Allows reordering elements within one and/or between multiple containers
             * <tr><td>translateXY<td>Allows dragging within a parent container
             * </table>
             * @config {String}
             * @default
             * @private
             */
            mode : null,

            /**
             * A function that determines if dragging an element is allowed. Gets called with the element as argument,
             * return true to allow dragging or false to prevent.
             * @config {Function}
             * @default
             */
            isElementDraggable : null,

            /**
             * A CSS selector used to determine if dragging an element is allowed.
             * @config {String}
             * @default
             */
            targetSelector : null,

            /**
             * A CSS selector used to determine if a drop is allowed at the current position.
             * @config {String}
             * @default
             */
            dropTargetSelector : null,

            /**
             * Set to true to clone the dragged target, and not move the actual target DOM node.
             * @config {Boolean}
             * @default
             */
            cloneTarget : false,

            /**
             * Set to true to hide the original element while dragging (applicable when `cloneTarget` is true).
             * @config {Boolean}
             * @default
             */
            hideOriginalElement : false,

            /**
             * Containers whose elements can be rearranged (and moved between the containers). Used when
             * mode is set to "container".
             * @config {HTMLElement[]}
             * @default
             */
            containers : null,

            /**
             * A CSS selector used to exclude elements when using container mode
             * @config {String}
             * @default
             */
            ignoreSelector : null,

            startEvent : null,

            /**
             * Configure as `true` to disallow dragging in the `X` axis. The dragged element will only move vertically.
             * @config {Boolean}
             * @default
             */
            lockX : false,

            /**
             * Configure as `true` to disallow dragging in the `Y` axis. The dragged element will only move horizontally.
             * @config {Boolean}
             * @default
             */
            lockY : false,

            touchStartDelay    : 300,
            // For the abort animation
            transitionDuration : 300,

            ignoreSamePositionDrop : true
        };
    }

    //endregion

    //region Events

    /**
     * Fired before dragging starts, return false to prevent the drag operation.
     * @preventable
     * @event beforeDragStart
     * @param {DragHelper} source
     * @param {Object} context
     * @param {MouseEvent|TouchEvent} event
     */

    /**
     * Fired when dragging starts.
     * @event dragStart
     * @param {DragHelper} source
     * @param {Object} context
     * @param {MouseEvent|TouchEvent} event
     */

    /**
     * Fired while dragging, you can signal that the drop is valid or invalid by setting `context.valid = false;`
     * @event drag
     * @param {DragHelper} source
     * @param {Object} context
     * @param {Boolean} context.valid Set this to true or false to indicate whether the drop position is valid.
     * @param {MouseEvent} event
     */

    //endregion

    //region Init

    /**
     * Initializes a new DragHelper.
     * @param {Object} config Configuration object, accepts options specified under Configs above
     * @example
     * new DragHelper({
     *   containers: [div1, div2],
     *   isElementDraggable: element => element.className.contains('handle'),
     *   outerElement: topParent,
     *   listeners: {
     *     drop: onDrop,
     *     thisObj: this
     *   }
     * });
     * @function constructor
     */
    construct(config) {
        const me = this;

        super.construct(config);

        me.initListeners();

        if (me.mode === 'container') {
            me.initContainerDrag();
        }
        else if (me.mode.startsWith('translate')) {
            if (me.lockY) {
                me.mode = 'translateX';
            }
            else if (me.lockX) {
                me.mode = 'translateY';
            }
            me.initTranslateDrag();
        }
    }

    doDestroy() {
        // Abort dragging
        this.abort(true);
        super.doDestroy();
    }

    /**
     * Initialize listener
     * @private
     */
    initListeners() {
        const dragStartListeners = {
            element   : this.outerElement,
            mousedown : documentListeners.down,
            thisObj   : this
        };

        if (BrowserHelper.isTouchDevice) {
            dragStartListeners.touchstart = documentListeners.touchstart;
        }

        // These will be autoDetached upon destroy
        EventHelper.on(dragStartListeners);
    }

    //endregion

    //region Events

    /**
     * Fires after drop. For valid drops, it exposes `context.async` which you can set to true to signal that additional
     * processing is needed before finalizing the drop (such as showing some dialog). When that operation is done, call
     * `context.finalize(true/false)` with a boolean that determines the outcome of the drop.
     * @event drop
     * @param {DragHelper} dragHelper
     * @param {Object} context
     */

    onPointerDown(event) {
        let me      = this,
            handled = false,
            isTouch = 'touches' in event;

        // If a drag is ongoing already, finalize it and don't proceed with new drag (happens if user does mouseup
        // outside browser window). Also handles the edge case of trying to start a new drag while previous is awaiting
        // finalization, in which case it just bails out.
        if (me.context) {
            if (!me.context.awaitingFinalization) {
                me.onMouseUp(event);
                me.reset();
            }
            return;
        }

        me.startEvent = event;

        if (me.isElementDraggable && !me.isElementDraggable(event.target, event)) return;

        if (me.mode) {
            if (!handled && me.mode === 'container') handled = me.grabContainerDrag(event);
            if (!handled && me.mode.startsWith('translate')) handled = me.grabTranslateDrag(event);
        }

        if (handled) {
            const dragListeners = {
                element : document,
                thisObj : me,
                keydown : documentListeners.keydown
            };

            if (isTouch) {
                dragListeners.touchmove = {
                    handler : documentListeners.touchmove,
                    passive : false // We need to be able to preventDefault on the touchmove
                };
                // Touch desktops don't fire touchend event when touch has ended, instead pointerup is fired
                // iOS do fire touchend
                dragListeners.touchend = dragListeners.pointerup = documentListeners.touchend;
            }
            else {
                dragListeners.mousemove = documentListeners.move;
                dragListeners.mouseup = documentListeners.up;
            }

            // A listener detacher is returned;
            me.removeListeners = EventHelper.on(dragListeners);

            if (me.dragWithin && me.dragWithin !== me.outerElement) {
                const box = me.dragWithin.getBoundingClientRect();

                me.minY = box.top;
                me.maxY = box.bottom;

                me.minX = box.left;
                me.maxX = box.right;
            }
        }
    }

    /**
     * @param event
     * @private
     */
    onTouchStart(event) {
        const me = this;
        // only allowing one finger for now...
        if (event.touches.length === 1) {
            me.touchStartTimer = me.setTimeout(() => {
                me.touchStartTimer = null;
            }, me.touchStartDelay);

            me.onPointerDown(event);
        }
    }

    /**
     * Grab draggable element on mouse down.
     * @private
     * @param event
     */
    onMouseDown(event) {
        // only dragging with left mouse button
        if (event.button === 0) {
            this.onPointerDown(event);
        }
    }

    internalMove(event) {
        const
            me      = this,
            context = me.context,
            distance = EventHelper.getDistanceBetween(me.startEvent, event),
            abortTouchDrag = me.touchStartTimer && distance > me.dragThreshold;

        if (abortTouchDrag) {
            me.abort(true);
            return;
        }

        if (!me.touchStartTimer && context && context.element &&
            // Only target Elements, not text nodes
            event.target.nodeType === Node.ELEMENT_NODE &&
            (context.started || distance >= me.dragThreshold)) {
            if (!context.started) {
                // triggers beforeDragStart, dragStart. returning false from beforeDragStart aborts drag
                if (me.callPreventable('dragStart', { context, event }, () => {
                    const direction = me.lockX ? 'vertical' : (me.lockY ? 'horizontal' : 'both');

                    if (context.action.startsWith('translate')) {
                        me.startTranslateDrag(event);
                    }
                    else if (context.action === 'container') {
                        me.startContainerDrag(event);
                    }

                    context.started = true;

                    // Now that the drag drop is confirmed to be starting, activate the configured scrollManager if present
                    if (me.scrollManager) {
                        me.scrollManager.startMonitoring({
                            direction : direction,
                            element   : me.dragWithin || me.outerElement,
                            callback  : config => {
                                if (me.context.element && me.lastMouseMoveEvent) {
                                    // Indicate that this is a 'fake' mousemove event as a result of the scrolling
                                    me.lastMouseMoveEvent.isScroll = true;

                                    me.update(me.lastMouseMoveEvent, config);
                                }
                            },
                            thisObj : me
                        });
                    }

                    // Global informatoinal class for when DragHelper is dragging
                    document.body.classList.add('b-draghelper-active');
                }) === false) {
                    return me.abort();
                }
            }

            // to prevent view drag (scroll) on ipad
            if (event.type === 'touchmove') {
                event.preventDefault();
                event.stopImmediatePropagation();
            }

            me.update(event);
        }
    }

    onTouchMove(event) {
        this.internalMove(event);
    }

    /**
     * Move drag element with mouse.
     * @param event
     * @fires beforeDragStart
     * @fires dragStart
     * @private
     */
    onMouseMove(event) {
        this.internalMove(event);
    }

    /**
     * Updates drag, called when an element is grabbed and mouse moves
     * @private
     * @fires drag
     */
    update(event, scrollManagerConfig) {
        const me              = this,
            context         = me.context,
            draggingElement = context.dragProxy || context.element; // two different modes used

        let target = event.target,
            scrollingPageElement = (document.scrollingElement || document.body);

        // "pointer-events:none" touchmove has no effect for the touchmove event target, meaning we cannot know
        // what's under the cursor as easily in touch devices
        if (event.type === 'touchmove') {
            const touch = event.changedTouches[0];

            target = DomHelper.elementFromPoint(touch.clientX + scrollingPageElement.scrollLeft, touch.clientY + scrollingPageElement.scrollTop);
        }

        context.target = target;

        if (me.dropTargetSelector) {
            context.valid = Boolean(target.closest(me.dropTargetSelector));
        }
        else {
            // assume valid drop location
            context.valid = true;
        }

        // Move the drag proxy or dragged element before triggering the drag event
        if (context.action) {
            if (context.action === 'container') {
                me.updateContainerProxy(event, scrollManagerConfig);
            }
            if (context.action.startsWith('translate')) {
                me.updateTranslateProxy(event, scrollManagerConfig);
            }
        }

        // Allow external code to validate the context before updating a container drag
        me.trigger('drag', { context, event });

        // Move the placeholder element into its new place.
        // This will see the new state of context if mutated by a drag listener.
        if (context.action === 'container') {
            me.updateContainerDrag(event, scrollManagerConfig);
        }

        // change to toggle with force when not supporting IE11 any longer
        draggingElement.classList[context.valid ? 'remove' : 'add'](me.invalidCls);

        if (event) me.lastMouseMoveEvent = event;
    }

    /**
     * Abort dragging
     * @fires abort
     */
    abort(silent = false) {
        const me      = this,
            context = me.context;

        me.scrollManager && me.scrollManager.stopMonitoring(me.dragWithin || me.outerElement);

        if (context) {
            // Force a synchronous layout so that transitions from this point will work.
            context.element.getBoundingClientRect();

            // Aborted drag not considered valid
            context.valid = false;

            if (context.action === 'container') {
                me.abortContainerDrag(undefined, undefined, silent);
            }
            else {
                me.abortTranslateDrag(undefined, undefined, silent);
            }
        }

        me.reset();
    }

    // Empty class implementation. If listeners *are* added, the detacher is added
    // as an instance property. So this is always callable.
    removeListeners() {
    }

    // Called when a drag operation is completed, or aborted
    // Removes DOM listeners and resets context
    reset(silent) {
        document.body.classList.remove('b-draghelper-active');
        this.removeListeners();
        /**
         * Fired when a drag operation is completed or aborted
         * @event reset
         * @private
         * @param {DragHelper} dragHelper
         */
        if (!silent) {
            this.trigger('reset');
        }
        this.context = this.lastMouseMoveEvent = null;
    }

    onTouchEnd(event) {
        this.onMouseUp(event);
    }

    /**
     * This is a capture listener, only added during drag, which prevents a click gesture
     * propagating from the terminating mouseup geature
     * @param {MouseEvent} event
     * @private
     */
    onDocumentClick(event) {
        event.stopPropagation();
    }

    /**
     * Drop on mouse up (if dropped on valid target).
     * @param event
     * @private
     */
    onMouseUp(event) {
        const me      = this,
            context = me.context;

        me.removeListeners();

        if (context) {
            me.scrollManager && me.scrollManager.stopMonitoring(me.dragWithin || me.outerElement);

            if (context.action === 'container') {
                me.finishContainerDrag(event);
            }
            else if (context.started && context.action.startsWith('translate')) {
                me.finishTranslateDrag(event);
            }

            if (context.started) {
                // Prevent the impending document click from the mouseup event from propagating
                // into a click on our element.
                EventHelper.on({
                    element : document,
                    thisObj : me,
                    click   : documentListeners.docclick,
                    capture : true,
                    expires : 50, // In case a click did not ensue, remove the listener
                    once    : true
                });
            }
            else {
                me.reset(true);
            }
        }
    }

    /**
     * Cancel on ESC key
     * @param event
     * @private
     */
    onKeyDown(event) {
        if (event.key === 'Escape') this.abort();
    }

    /**
     * Creates the proxy element to be dragged, when using {@link #config-cloneTarget}. Clones the original element by default
     */
    createProxy(element) {
        const clone = element.cloneNode(true);
        clone.removeAttribute('id');

        return clone;
    }
    //endregion
}
