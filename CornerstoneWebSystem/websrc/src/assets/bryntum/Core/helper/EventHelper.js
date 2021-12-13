import Point from './util/Point.js';
import DomHelper from './DomHelper.js';
import ObjectHelper from './ObjectHelper.js';
import FunctionHelper from './FunctionHelper.js';
import BrowserHelper from './BrowserHelper.js';
import Rectangle from './util/Rectangle.js';

/**
 * @module Core/helper/EventHelper
 */

const touchProperties = [
        'clientX',
        'clientY',
        'pageX',
        'pageY',
        'screenX',
        'screenY'
    ],
    isOption = {
        element    : 1,
        thisObj    : 1,
        once       : 1,
        delegate   : 1,
        delay      : 1,
        capture    : 1,
        passive    : 1,
        throttled  : 1,
        autoDetach : 1,
        expires    : 1
    },
    ctrlKeyProp = {
        get : () => true
    },
    normalizedKeyNames = {
        Spacebar : 'Space',
        Del      : 'Delete',
        Esc      : 'Escape',
        Left     : 'ArrowLeft',
        Up       : 'ArrowUp',
        Right    : 'ArrowRight',
        Down     : 'ArrowDown'
    },
    fixEvent = (event) => {
        const { type } = event;

        // Normalize key names
        if (type.startsWith('key')) {
            const normalizedKeyName = normalizedKeyNames[event.key];
            if (normalizedKeyName) {
                Object.defineProperty(event, 'key', {
                    get : () => normalizedKeyName
                });
            }

            // Polyfill the code property for SPACE because it is not set for synthetic events.
            if (event.key === ' ' && !event.code) {
                Object.defineProperty(event, 'code', {
                    get : () => 'Space'
                });
            }
        }

        // Sync OSX's meta key with the ctrl key. This will only happen on Mac platform.
        // It's read-only, so define a local property to return true for ctrlKey.
        if (event.metaKey && !event.ctrlKey) {
            Object.defineProperty(event, 'ctrlKey', ctrlKeyProp);
        }

        // When we listen to event on document and get event which bubbled from shadow dom, reading its target would
        // return shadow root element. We need actual element which started the event
        if (event.target && event.target.shadowRoot && event.composedPath && !BrowserHelper.isIE11 && !BrowserHelper.isEdge) {
            const
                targetElement  = event.composedPath()[0],
                originalTarget = event.target;

            // Can there be an event which actually originated from custom element, not its shadow dom?
            if (event.target !== targetElement) {
                Object.defineProperty(event, 'target', {
                    get : () => targetElement
                });

                // Save original target just in case
                Object.defineProperty(event, 'originalTarget', {
                    get : () => originalTarget
                });
            }
        }

        // Chrome 78 has a bug where moving out of the left edge of an element can report a mousemove
        // with that element as the target, but offsetX as -1 and moving out the right edge can report
        // that the element is the target but an offset of the offsetWidth. Patch the event until they fix it.
        // https://bugs.chromium.org/p/chromium/issues/detail?id=1010528
        if (BrowserHelper.isChrome && event.target) {
            const
                { target } = event,
                { offsetX, offsetY } = event,
                { offsetWidth, offsetHeight } = target,
                x = Math.min(Math.max(offsetX, 0), offsetWidth - 1),
                y = Math.min(Math.max(offsetY, 0), offsetHeight - 1);

            if (!Object.getOwnPropertyDescriptor(event, 'offsetX') && (offsetX < 0 || offsetX >= offsetWidth)) {
                Object.defineProperty(event, 'offsetX', {
                    get : () => x
                });
            }
            if (!Object.getOwnPropertyDescriptor(event, 'offsetY') && (offsetY < 0 || offsetY >= offsetHeight)) {
                Object.defineProperty(event, 'offsetY', {
                    get : () => y
                });
            }
        }

        // Firefox has a bug where it can report that the target is the #document when mouse is over a pseudo element
        if (event.target && event.target.nodeType === 9 && 'clientX' in event) {
            const targetElement = DomHelper.elementFromPoint(event.clientX, event.clientY);
            Object.defineProperty(event, 'target', {
                get : () => targetElement
            });
        }

        // Firefox has a bug where it can report a textNode as an event target/relatedTarget.
        // We standardize this to report the parentElement.
        if (event.target && event.target.nodeType === 3) {
            const targetElement = event.target.parentElement;
            Object.defineProperty(event, 'target', {
                get : () => targetElement
            });
        }
        if (event.relatedTarget && event.relatedTarget.nodeType === 3) {
            const relatedTargetElement = event.target.parentElement;
            Object.defineProperty(event, 'relatedTarget', {
                get : () => relatedTargetElement
            });
        }

        // If it's a touch event, move the positional details
        // of touches[0] up to the event.
        if (type.startsWith('touch') && event.touches.length) {
            EventHelper.normalizeEvent(event);
        }

        return event;
    };

/**
 * Utility methods for dealing with Events, normalizing Touch/Pointer/Mouse events.
 */
export default class EventHelper {
    static normalizeEvent(event) {
        return ObjectHelper.copyPropertiesIf(event, event.touches[0] || event.changedTouches[0], touchProperties);
    }

    /**
     * Returns the `[x, y]` coordinates of the event in the viewport coordinate system.
     * @param {Event} event The event
     * @return {Number[]} The coordinate.
     */
    static getXY(event) {
        if (event.touches) {
            event = event.touches[0];
        }
        return [event.clientX, event.clientY];
    }

    /**
     * Returns the pixel distance between two mouse/touch/pointer events.
     * @param {Event} event1 The first event.
     * @param {Event} event2 The second event.
     * @return {Number} The distance in pixels between the two events.
     */
    static getDistanceBetween(event1, event2) {
        const
            xy1 = this.getXY(event1),
            xy2 = this.getXY(event2);

        // No point in moving this to Point. We are dealing only with number values here.
        return Math.sqrt(Math.pow(xy1[0] - xy2[0], 2) + Math.pow(xy1[1] - xy2[1], 2));
    }

    /**
     * Returns a {@link Core.helper.util.Point} which encapsulates the `pageX/Y` position of the event.
     * May be used in {@link Core.helper.util.Rectangle} events.
     * @param {Event} event A browser mouse/touch/pointer event.
     * @return {Core.helper.util.Point} The page point.
     */
    static getPagePoint(event) {
        return new Point(event.pageX, event.pageY);
    }

    /**
     * Returns a {@link Core.helper.util.Point} which encapsulates the `clientX/Y` position of the event.
     * May be used in {@link Core.helper.util.Rectangle} events.
     * @param {Event} event A browser mouse/touch/pointer event.
     * @return {Core.helper.util.Point} The page point.
     */
    static getClientPoint(event) {
        return new Point(event.clientX, event.clientY);
    }

    /**
     * Add a listener or listeners to an element
     * @param {HTMLElement} element The element to add a listener/listeners to.
     * @param {String|Object} eventName Either a string, being the name of the event to listen for,
     * or an options object containing event names and options as keys. See the options parameter
     * for details, or the {@link #function-on-static} method for details.
     * @param {Function} [handler] If the second parameter is a string event name, this is the handler function.
     * @param {Object} [options] If the second parameter is a string event name, this is the options.
     * @param {HTMLElement} options.element The element to add the listener to.
     * @param {Object} options.thisObj The default `this` reference for all handlers added in this call.
     * @param {Boolean} [options.autoDetach=true] The listeners are automatically removed when the `thisObj` is destroyed.
     * @param {String} [options.delegate] A CSS selector string which only fires the handler when the event takes place in a matching element.
     * @param {Boolean} [options.once] Specify as `true` to have the listener(s) removed upon first invocation.
     * @param {Number} [options.delay] The number of millieconds to delay the handler call after the event fires:
     *
     *     {
     *         once       : true,           // Removed upon first firing
     *         delegate   : this.iconCls,   // Only when clicking the icon
     *         thisObj    : this            // The this reference when the handler is called
     *                                      // Listener is removed when thisObj is destroyed
     *     }
     * @returns {Function} A detacher function which removes all the listeners when called.
     */
    static addListener(element, eventName, handler, options) {
        if (element.nodeType) {
            // All separate params, element, eventname and handler
            if (typeof eventName === 'string') {
                options = Object.assign({
                    element,
                    [eventName] : handler
                }, options);
            }
            // element, options
            else {
                options = Object.assign({
                    element
                }, eventName);
            }
        }
        // Just an options object passed
        else {
            options = element;
        }
        return this.on(options);
    }

    /**
     * Adds a listener or listeners to an element.
     * all property names other than the options listed below are taken to be event names,
     * and the values as handler specs.
     *
     * A handler spec is usually a function reference or the name of a function in the `thisObj`
     * option.
     *
     * But a handler spec may also be an options object containing a `handler` property which is
     * the function or function name, and local options, including `element` and `thisObj`
     * which override the top level options.
     *
     *  Usage example
     *
     * ```javascript
     * construct(config) {
     *     super.construct(config);
     *
     *     // Add auto detaching event handlers to this Widget's reference elements
     *     EventHelper.on({
     *         element : this.iconElement,
     *         click   : '_handleIconClick',
     *         thisObj : this,
     *         contextmenu : {
     *             element : document,
     *             handler : '_handleDocumentContextMenu'
     *         }
     *     });
     * }
     *```
     *
     * The `click` handler on the `iconElement` calls `this._handleIconClick`.
     *
     * The `contextmenu` handler is added to the `document` element, but the `thisObj`
     * is defaulted in from the top `options` and calls `this._handleDocumentContextMenu`.
     *
     * Note that on touch devices, `dblclick` and `contextmenu` events are synthesized.
     * Synthesized events contain a `browserEvent` property containing the final triggering
     * event of the gesture. For example a synthesized `dblclick` event would contain a
     * `browserEvent` property which is the last `touchend` event. A synthetic `contextmenu`
     * event will contain a `browserEvent` property which the longstanding `touchstart` event.
     *
     * @param {Object} options The full listener specification.
     * @param {HTMLElement} options.element The element to add the listener to.
     * @param {Object} options.thisObj The default `this` reference for all handlers added in this call.
     * @param {Boolean} [options.autoDetach=true] The listeners are automatically removed when the `thisObj` is destroyed.
     * @param {String} [options.delegate] A CSS selector string which only fires the handler when the event takes place in a matching element.
     * @param {Boolean} [options.once] Specify as `true` to have the listener(s) removed upon first invocation.
     * @param {Number} [options.delay] The number of millieconds to delay the handler call after the event fires.
     * @param {Number} [options.throttled] For rapidly repeating events (Such as `wheel` or `scroll` or `mousemove`)
     * this is the number of millieconds to delay subsequent handler calls after first invocation which happens immediately.
     * @returns {Function} A detacher function which removes all the listeners when called.
     */
    static on(options) {
        const EventHelper    = this,
            element        = options.element,
            thisObj        = options.thisObj,
            handlerDetails = [],
            keys           = ObjectHelper.allKeys(options);

        let len = keys.length,
            i, eventName;

        for (i = 0; i < len; i++) {
            eventName = keys[i];

            // Only treat it as an event name if it's not a supported option
            if (!isOption[eventName]) {
                let handlerSpec = options[eventName];
                if (typeof handlerSpec !== 'object') {
                    handlerSpec = {
                        handler : handlerSpec
                    };
                }
                const targetElement = handlerSpec.element || element;

                // If we need to convert taphold to an emulated contextmenu, add a wrapping function
                // in addition to the contextmenu listener. Platforms may support mouse *and* touch.
                if (BrowserHelper.isTouchDevice && !BrowserHelper.isAndroid) {
                    if (eventName === 'contextmenu') {
                        handlerDetails.push(EventHelper.addElementListener(targetElement, 'touchstart', {
                            handler : EventHelper.createContextMenuWrapper(handlerSpec.handler, handlerSpec.thisObj || thisObj)
                        }, options));
                    }
                }

                // Keep track of the real handlers added.
                // addElementLister returns [ element, eventName, addedfunction, capture ]
                handlerDetails.push(EventHelper.addElementListener(targetElement, eventName, handlerSpec, options));
            }
        }

        const detacher = () => {
            len = handlerDetails.length;
            for (i = 0; i < len; i++) {
                const handlerSpec = handlerDetails[i];
                handlerSpec[0].removeEventListener(handlerSpec[1], handlerSpec[2], handlerSpec[3]);
            }
            handlerDetails.length = 0;
        };

        // { autoDetach : true, thisObj : scheduler } means remove all listeners when the scheduler dies.
        if (thisObj && options.autoDetach !== false) {
            thisObj.doDestroy = FunctionHelper.createInterceptor(thisObj.doDestroy, detacher, thisObj);
        }

        return detacher;
    }

    /**
     * Used internally to add a single event handler to an element.
     * @param {HTMLElement} element The element to add the handler to.
     * @param {String} eventName The name of the event to add a handler for.
     * @param {Function|String|Object} handlerSpec Either a function to call, or
     * the name of a function to call in the `thisObj`, or an object containing
     * the handler local options.
     * @param {Function|String} [handlerSpec.handler] Either a function to call, or
     * the name of a function to call in the `thisObj`.
     * @param {HTMLElement} [handlerSpec.element] Optionally a local element for the listener.
     * @param {Object} [handlerSpec.thisObj] A local `this` specification for the handler.
     * @param {Object} defaults The `options` parameter from the {@link #function-addListener-static} call.
     * @private
     */
    static addElementListener(element, eventName, handlerSpec, defaults) {
        const handler           = this.createHandler(element, eventName, handlerSpec, defaults),
            handlerHasPassive = ('passive' in handlerSpec),
            expires           = handlerSpec.expires || defaults.expires;

        let options = handlerSpec.capture || defaults.capture;

        // If we are passed the passive option and the browser supports it, then convert
        // The capture option into the object options form.
        if ((handlerHasPassive || ('passive' in defaults)) && BrowserHelper.supportsPassive) {
            options = {
                capture : !!options,
                passive : handlerHasPassive ? handlerSpec.passive : defaults.passive
            };
        }
        element.addEventListener(eventName, handler, options);
        if (expires) {
            (typeof expires === 'number' ? setTimeout : requestAnimationFrame)(() => element.removeEventListener(eventName, handler, options), expires);
        }

        return [element, eventName, handler, options];
    }

    static createHandler(element, eventName, handlerSpec, defaults) {
        const
            delay            = handlerSpec.delay || defaults.delay,
            throttled        = handlerSpec.throttled || defaults.throttled,
            once             = handlerSpec.once || defaults.once,
            thisObj          = handlerSpec.thisObj || defaults.thisObj,
            capture          = handlerSpec.capture || defaults.capture,
            delegate         = handlerSpec.delegate || defaults.delegate;

        //Capture initial conditions in case of destruction of thisObj.
        // Destruction completely wipes the object.
        //<debug>
        const thisObjClassName = (thisObj && thisObj.constructor.name) || 'Object',
            thisObjId        = (thisObj && thisObj.id) || 'unknown';
            //</debug>

        let wrappedFn = handlerSpec.handler,
            // Innermost level of wrapping which calls the user's handler.
            // Normalize the event cross-browser, and attempt to normalize touch events.
            // Resolve named functions in the thisObj.
            handler = (event, ...args) => {
                // When playing a demo using DemoBot, only handle synthetic events
                if (this.playingDemo && event.isTrusted) {
                    return;
                }

                // If the thisObj is already destroyed, we cannot call the function.
                // If in dev mode, warn the developer with a JS error.
                if (thisObj && thisObj.isDestroyed) {
                    //<debug>
                    throw new Error(`Attempting to fire ${eventName} event on destroyed ${thisObjClassName} instance with id: ${thisObjId}`);
                    //</debug>
                    // eslint-disable-next-line
                    return;
                }

                // Fix up events to handle various browser inconsistencies
                fixEvent(event);

                // delegate: '.b-field-trigger' only fires when click is in a matching el.
                // currentTarget becomes the delegate.
                if (delegate) {
                    // Maintainer: In Edge event.target can be an empty object for transitionend events
                    const delegatedTarget = event.target instanceof HTMLElement && event.target.closest(delegate);
                    if (!delegatedTarget) {
                        return;
                    }
                    // Allow this to be redefined as it bubbles through listeners up the parentNode axis
                    // which might have their own delegate settings.
                    Object.defineProperty(event, 'currentTarget', {
                        get          : () => delegatedTarget,
                        configurable : true
                    });
                }

                if (typeof wrappedFn === 'string') {
                    wrappedFn = thisObj[wrappedFn];
                }
                wrappedFn.call(thisObj, event, ...args);
            };

        // Go through options, each creates a new handler by wrapping the previous handler to implement the options.
        // Right now, we have delay. Note that it may be zero, so test != null
        if (delay != null) {
            const wrappedFn = handler;
            handler = (...args) => {
                setTimeout(() => {
                    wrappedFn(...args);
                }, delay);
            };
        }

        // If they specified the throttled option, wrap the hander in a createdThrottled
        // version. Allow the called to specify an alt function to call when the event
        // fires before the buffer time has expired.
        if (throttled != null) {
            let alt, buffer = throttled;

            if (throttled.buffer) {
                alt = e => throttled.alt.call(this, fixEvent(e));
                buffer = throttled.buffer;
            }
            handler = FunctionHelper.createThrottled(handler, buffer, thisObj, null, alt);
        }

        // This must always be the last option processed so that it is the outermost handler
        // which is the one added to the element and is called immediately so that the
        // handler is removed immediately.
        // TODO: Use the native once option when all browsers support it. Only IE11 doesn't.
        if (once) {
            const wrappedFn = handler;
            handler = (...args) => {
                element.removeEventListener(eventName, handler, capture);
                wrappedFn(...args);
            };
        }

        // Only autoDetach here if there's a local thisObj is in the handlerSpec for this one listener.
        // If it's in the defaults, then the "on" method will handle it.
        if (handlerSpec.thisObj && handlerSpec.autoDetach !== false) {
            thisObj.doDestroy = FunctionHelper.createInterceptor(thisObj.doDestroy, () => element.removeEventListener(eventName, handler), thisObj);
        }

        return handler;
    }

    /**
     * Private function to wrap the passed function. The returned wrapper function to be used as
     * a `touchstart` handler which will call the passed function passing a fabricated `contextmenu`
     * event if there's no `touchend` or `touchmove` after a default of 400ms.
     * @param {String|Function} handler The handler to call.
     * @param {Object} thisObj The owner of the function.
     * @private
     */
    static createContextMenuWrapper(handler, me) {
        const EventHelper = this;

        return event => {
            // Only attempt conversion to contextmenu if it's a single touch start.
            if (event.touches.length === 1) {
                const tapholdStartTouch = event.touches[0],
                    // Dispatch a synthetic "contextmenu" event from the touchpoint in <longPressTime> milliseconds.
                    tapholdTimer = setTimeout(() => {
                        // Remove the gesture cancelling listeners
                        touchMoveRemover();

                        const contextmenuEvent = new MouseEvent('contextmenu', tapholdStartTouch);
                        Object.defineProperty(contextmenuEvent, 'target', {
                            get() {
                                return tapholdStartTouch.target;
                            }
                        });
                        if (typeof handler === 'string') {
                            handler = me[handler];
                        }

                        contextmenuEvent.browserEvent = event;

                        // Call the wrapped handler passing the fabricated contextmenu event
                        handler.call(me, contextmenuEvent);
                        EventHelper.contextMenuTouchId = tapholdStartTouch.identifier;
                    }, EventHelper.longPressTime),
                    // This is what gets called if the user moves their touchpoint,
                    // or releases the touch before <longPressTime>ms is up
                    cancelTapholdTimer = () => {
                        EventHelper.contextMenuTouchId = null;
                        touchMoveRemover();
                        clearTimeout(tapholdTimer);
                    },
                    // Touchmove or touchend before that timer fires cancels the timer and removes these listeners.
                    touchMoveRemover = EventHelper.on({
                        element     : document,
                        touchmove   : cancelTapholdTimer,
                        touchend    : cancelTapholdTimer,
                        pointermove : cancelTapholdTimer,
                        pointerup   : cancelTapholdTimer,
                        capture     : true
                    });
            }
        };
    }

    /**
     * Private function to wrap the passed function. The returned wrapper function to be used as
     * a `touchend` handler which will call the passed function passing a fabricated `dblclick`
     * event if there is a `click` within 300ms.
     * @param {String|Function} handler The handler to call.
     * @param {Object} thisObj The owner of the function.
     * @private
     */
    static createDblClickWrapper(element, handler, me) {
        const EventHelper = this;

        let startId, secondListenerDetacher, tapholdTimer;

        return () => {
            if (!secondListenerDetacher) {
                secondListenerDetacher = EventHelper.on({
                    element,

                    // We only get here if a touchstart arrives within 300ms of a click
                    touchstart : secondStart => {
                        startId = secondStart.changedTouches[0].identifier;
                        // Prevent zoom
                        secondStart.preventDefault();
                    },
                    touchend : secondClick => {
                        if (secondClick.changedTouches[0].identifier === startId) {
                            secondClick.preventDefault();

                            clearTimeout(tapholdTimer);
                            startId = secondListenerDetacher = null;

                            const targetRect          = Rectangle.from(secondClick.changedTouches[0].target, null, true),
                                offsetX             = secondClick.changedTouches[0].pageX - targetRect.x,
                                offsetY             = secondClick.changedTouches[0].pageY - targetRect.y,
                                dblclickEventConfig = Object.assign({
                                    browserEvent : secondClick
                                }, secondClick),
                                dblclickEvent       = new MouseEvent('dblclick', dblclickEventConfig);

                            Object.defineProperty(dblclickEvent, 'target', {
                                get() {
                                    return secondClick.target;
                                }
                            });

                            Object.defineProperty(dblclickEvent, 'offsetX', {
                                get() {
                                    return offsetX;
                                }
                            });

                            Object.defineProperty(dblclickEvent, 'offsetY', {
                                get() {
                                    return offsetY;
                                }
                            });

                            if (typeof handler === 'string') {
                                handler = me[handler];
                            }

                            // Call the wrapped handler passing the fabricated dblclick event
                            handler.call(me, dblclickEvent);
                        }
                    },
                    once : true
                });

                // Cancel the second listener is there's no second click within <dblClickTime> milliseconds.
                tapholdTimer = setTimeout(() => {
                    secondListenerDetacher();
                    startId = secondListenerDetacher = null;
                }, EventHelper.dblClickTime);
            }
        };
    }

    static lockComposedPath(event) {
        if (event.composedPath) {
            event.composedPath = ((path) => () => path)(event.composedPath());
        }
    }
}

/**
 * The time in milliseconds for a `taphold` gesture to trigger a `contextmenu` event.
 * @member {Number} [longPressTime=500]
 * @readonly
 * @static
 */
EventHelper.longPressTime = 500;

/**
 * The time in milliseconds within which a second touch tap event triggers a `dblclick` event.
 * @member {Number} [dblClickTime=300]
 * @readonly
 * @static
 */
EventHelper.dblClickTime = 300;

// Flag body if last user action used keyboard, used for focus styling etc.
EventHelper.on({
    element : document,
    mousedown() {
        if (!DomHelper.isTouchEvent) {
            DomHelper.usingKeyboard = false;
            document.body.classList.remove('b-using-keyboard');
        }
    },
    touchmove() {
        DomHelper.usingKeyboard = false;
        document.body.classList.remove('b-using-keyboard');
    },
    keydown() {
        DomHelper.usingKeyboard = true;
        document.body.classList.add('b-using-keyboard');
    }
});

// When dragging on a touch device, we need to prevent scrolling from happening.
// Dragging only starts on a touchmove event, by which time it's too late to preventDefault
// on the touchstart event which started it.
// To do this we need a capturing, non-passive touchmove listener at the document level so we can preventDefault.
// This is in lieu of a functioning touch-action style on iOS Safari. When that's fixed, this will not be needed.
if (BrowserHelper.isTouchDevice) {
    EventHelper.on({
        element   : document,
        touchmove : event => {
            // If we're touching a b-dragging event, then stop any panning by preventing default.
            if (event.target.closest('.b-dragging')) {
                event.preventDefault();
            }
        },
        passive : false,
        capture : true
    });
}
