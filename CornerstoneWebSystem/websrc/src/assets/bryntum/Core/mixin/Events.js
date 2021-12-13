import Base from '../Base.js';
import ArrayHelper from '../helper/ArrayHelper.js';
import StringHelper from '../helper/StringHelper.js';

//TODO: Implement mon

/**
 * @module Core/mixin/Events
 */

// Used to distinguish event names from listener options in addListener object config.
const specialProperties = {
        thisObj        : 1,
        detachable     : 1,
        once           : 1,
        detacher       : 1,
        prio           : 1,
        args           : 1,
        expires        : 1,
        name           : 1,
        _oldDestructor : 1
    },
    priorityComparator = (a, b) => b.prio - a.prio;

/**
 * Mix this into another class to enable event handling.
 *
 * ## Basic usage
 * Listeners can be added either through config:
 *
 * ```javascript
 * let button = new Button({
 *   listeners: {
 *     click: () => {},
 *     press: () => {},
 *     ...
 *   }
 * });
 * ```
 *
 * *NOTE*: Do not reuse listeners config object, use new every time:
 * ```javascript
 * // wrong
 * let config = { click : () => {} }
 * new Button({
 *     listeners : config
 * })
 * new Button({
 *     listeners : config
 * })
 * // right
 * new Button({
 *     listeners : { click : () => {} }
 * })
 * new Button({
 *     listeners : { click : () => {} }
 * })
 * ```
 *
 * Or by calling on()/addListener():
 *
 * ```javascript
 * let button = new Button();
 *
 * button.addListener('press', () => {});
 * // on is an alias for addListener
 * button.on('click', () => {});
 * ```
 *
 * This style also accepts multiple listeners in same way as when using config:
 *
 * ```javascript
 * button.on({
 *   click: () => {},
 *   press: () => {},
 *   ...
 * });
 * ```
 *
 * ## Options
 * ### Once
 * Listeners can be configured to automatically deregister after first trigger by specifing config option `once`:
 *
 * ```javascript
 * button.on({
 *   click: () => {},
 *   once: true
 * });
 * ```
 *
 * ### Priority
 * Specifying priority affects the order in which listeners are called when triggering an event. Higher prios will be
 * called before lower. Default value is 0.
 *
 * ```javascript
 * button.on({
 *   click: this.onClick,
 *   prio: 1
 * });
 * ```
 *
 * ### This reference
 * If desired, you can specify thisObj when configuring listeners. There is no need if you are using arrow functions as
 * listeners, but might be handy in other cases. Of course, you can also use bind to set `this` reference.
 *
 * ```javascript
 * button.on({
 *   click: this.onClick,
 *   thisObj: this
 * });
 *
 * // or
 *
 * button.on({
 *   click: this.onClick.bind(this)
 * });
 * ```
 *
 * ### Detacher
 * A convenient way of unregistering events is to use a detacher, a function returned when adding listeners that you
 * call later to deregister them. As of version 1.0, detachable defaults to true.
 *
 * ```javascript
 * let detacher = button.on({
 *   click: () => {},
 *   press: () => {},
 *   detachable: true
 * });
 *
 * // when you want to detach, for example in destroy()
 * detacher();
 * ```
 *
 * ### Auto detaching
 * When listeners are bound to a class instance using `thisObj`, the `thisObj`'s `doDestroy` method
 * is overridden to remove the listeners before calling the overridden doDestroy.
 *
 * ```javascript
 * class MyClass extends Base {
 *   construct() {
 *     let button = new Button({
 *       listeners: {
 *         click: () => {},
 *         thisObj: this
 *       }
 *     });
 *   }
 *
 *   doDestroy() {
 *     // clean up stuff
 *   }
 * }
 *
 * let myObj = new MyClass();
 * // clean up, also removes listeners
 * myObj.destroy();
 * ```
 *
 * ### On-functions
 * When mixing Events into another class it can be configured to call on-functions when events are triggered.
 * On-functions are functions named 'onEventname', for example 'onClick', 'onPress' declared on the class triggering
 * the event.
 *
 * ```javascript
 * // mix Events in with on-functions activated
 * let button = new Button({
 *   callOnFunctions: true,
 *
 *   onClick: () => {}
 * });
 *
 * // or add a getter in class declaration
 * ```
 *
 * ### Catching all events
 * By specifying a listener for `catchAll` a function can be notified when any event is triggered:
 *
 * ```javascript
 * const button = new Button({
 *    listeners : {
 *        catchAll(event) {
 *            // All events on the button will pass through here
 *        }
 *    }
 * });
 * ```
 *
 * @mixin
 */
export default Target => class Events extends (Target || Base) {
    //region Events
    /**
     * Fires before an object is destroyed.
     * @event beforeDestroy
     * @param {Object} source The Object that is being destroyed.
     */

    /**
     * Fires when an object is destroyed.
     * @event destroy
     * @param {Object} source The Object that is being destroyed.
     */

    //endregion

    //region Config

    /**
     * Listeners can be specified in target class config.
     * ```
     * let store = new Store({
     *   listeners: {
     *     load: () => {},
     *     ...
     *   }
     * });
     * ```
     * @config {Object} listeners
     */

    destroy() {
        this.trigger('beforeDestroy');
        super.destroy();
    }

    //endregion

    //region Init

    // TODO: Check, because not sure about using @function constructor here
    /**
     * Adds configured listeners
     * @private
     * @function constructor
     */
    construct(...args) {
        super.construct(...args);

        this.processConfiguredListeners();
    }

    processConfiguredListeners(listeners = this.listeners && this.initialEventListeners) {
        const me = this;

        // TODO: listeners should be added first, so that any setters which fire or rely on listeners work.
        // This may also expose bugs where listeners are fired too early.
        if (listeners && !me.defaultListenersInitialized) {
            const ret = me.addListener(listeners, me);

            if (me.listeners.detachable !== false) {
                me.listenerDetacher = ret;
            }

            me.defaultListenersInitialized = true;
        }
    }

    /**
     * Auto detaches listeners registered from start, if set as detachable
     * @internal
     */
    doDestroy() {
        const me = this;

        me.trigger('destroy');
        if (me.listenerDetacher) {
            me.listenerDetacher();
        }
        me.removeAllListeners();

        super.doDestroy();
    }

    //endregion

    //region Listeners

    /**
     * Adds an event listener. This method accepts parameters in the following format:
     *
     * ```javascript
     *  {
     *      thisObj    : this,          // The this reference for the handlers
     *      eventname2 : 'functionName' // Resolved at invocation time using the thisObj,
     *      otherevent : {
     *          fn      : 'handlerFnName',
     *          once    : true          // Just this handler is auto-removed on fire
     *      },
     *      yetanother  : {
     *          fn      : 'yetAnotherHandler',
     *          args    : [ currentState1, currentState2 ] // Capture info to be passed to handler
     *      }
     *      prio        : 100           // Higher prio listeners are called before lower
     *  }
     * ```
     *
     * When listeners have a `thisObj` option, they are linked to the lifecycle of that object.
     * When it is destroyed, those listeners are removed.
     *
     * @param {Object} config An object containing listener definitions.
     * @param {Object} [config.thisObj] The `this` reference for all listeners.
     * (May be overridden if a handler is specified in object form)
     * @param {Boolean} [config.once] Specify as `true` to remove the listener as soon as it is invoked.
     * @param {Object[]} [config.args] An array of arguments to be passed to the handler before the event object.
     * @param {Object} [thisObj] `this` reference for all listeners.
     * @param {Number} [prio] The priority for all listeners; higher priority listeners are caled before lower.
     * @returns {Function} Returns a detacher function unless configured with `detachable: false`. Call detacher to remove listeners
     */
    addListener(config, thisObj, arg2) {
        const
            me = this,
            events = me.eventListeners || (me.eventListeners = {});

        if (typeof config === 'string') {
            // arguments[2] is thisObj if (eventname, handler, thisObj) form called.
            // Note that the other side of the if compares to undefined, so this will work.
            return me.addListener({
                [config]   : thisObj,
                detachable : thisObj.detachable !== false,
                thisObj    : arg2
            });
        }
        else {
            // If the thisObj came from the parameter, ensure the config object has it
            // because this is how autoDetaching works.
            thisObj = config.thisObj !== undefined ? config.thisObj : (config.thisObj = thisObj);

            for (const key in config) {
                if (!specialProperties[key]) {
                    const
                        listenerSpec = config[key],
                        listener  = {
                            fn       : typeof listenerSpec === 'object' ? listenerSpec.fn : listenerSpec,
                            thisObj  : listenerSpec.thisObj !== undefined ? listenerSpec.thisObj : thisObj,
                            args     : listenerSpec.args || config.args,
                            prio     : listenerSpec.prio !== undefined ? listenerSpec.prio : config.prio !== undefined ? config.prio : 0,
                            once     : listenerSpec.once !== undefined ? listenerSpec.once : config.once !== undefined ? config.once : false,
                            catchAll : key === 'catchAll'
                        },
                        eventName = key.toLowerCase(),
                        listeners = events[eventName] || (events[eventName] = []);

                    //<debug>
                    if (!listener.fn) {
                        throw new Error('Listener added with no handler');
                    }
                    //</debug>

                    if (listenerSpec.expires) {
                        me.delay(() => me.un(eventName, listener), config.expires);
                    }

                    // Insert listener directly in prio order
                    listeners.splice(ArrayHelper.findInsertionIndex(listener, listeners, priorityComparator, listeners.length), 0, listener);
                }
            }

            if (config.relayAll) {
                me.relayAll(config.relayAll);
            }

            // Hook into the thisObj's destruction sequence to remove these listeners.
            if (thisObj) {
                me.attachAutoDetacher(config);
            }

            const
                detachable = config.detachable !== false,
                name = config.name,
                destroy = (config.expires || detachable || name) ? () => {
                    // drop listeners if not destroyed yet
                    if (!me.isDestroyed) {
                        me.removeListener(config);
                    }
                } : null;

            if (destroy) {
                destroy.eventer = me;
                destroy.listenerName = name;

                if (name && thisObj && thisObj.trackDetacher) {
                    thisObj.trackDetacher(name, destroy);
                }

                if (config.expires) {
                    me.delay(destroy, config.expires);
                }

                if (detachable) {
                    return destroy;
                }
            }
        }
    }

    /**
     * Shorthand for addListener
     * @param config
     * @param [thisObj]
     */
    on() {
        return this.addListener(...arguments);
    }

    /**
     * Shorthand for removeListener
     * @param config
     * @param thisObj
     */
    un() {
        this.removeListener(...arguments);
    }

    /**
     * Sets the listener set for this object. Removes any existing listeners and adds the
     * specified set of listeners.
     * @property {Object} When *setting*, this is the listeners to add in object form, see {@link #function addListener}.
     * When *getting*, this is an object map keys by event name, but each value is an *array* of listener definitions.
     */
    set listeners(listeners) {
        // Listeners are not applied during initial configuration.
        // Configured listeners are added by the constructor above.
        if (this.isConfiguring) {
            this.initialEventListeners = listeners;
        }
        else {
            this.removeAllListeners();
            if (listeners) {
                this.on(listeners);
            }
        }
    }

    get listeners() {
        return this.eventListeners || this.initialEventListeners;
    }

    /**
     * Removes an event listener. Same API signature as {@link #function-addListener}
     * @param {Object} config Listeners
     * @param {Object} thisObj `this` reference for all listeners
     */
    removeListener(config, thisObj = config.thisObj, ...params) {
        const me = this;

        if (typeof config === 'string') {
            me.removeListener({ [config] : thisObj }, params.length > 0 ? params[0] : true);
            return;
        }

        Object.entries(config).forEach(([key, configListener]) => {
            if (specialProperties[key]) {
                return;
            }

            const
                eventName = key.toLowerCase(),
                listeners = me.eventListeners && me.eventListeners[eventName];

            if (listeners) {
                const index = listeners.findIndex(listener =>
                    (listener.fn === configListener || listener.fn === configListener.fn) &&
                    (listener.thisObj === thisObj || listener.thisObj === configListener.thisObj || !thisObj)
                );
                if (index >= 0) {
                    // NOTE: we cannot untrack any detachers here because we may only be
                    // removing some of its listeners
                    listeners.splice(index, 1);
                    if (!listeners.length) {
                        delete me.eventListeners[eventName];
                    }
                }
            }
        });

        if (config.thisObj && !config.thisObj.isDestroyed) {
            me.detachAutoDetacher(config);
        }
    }

    /**
     * Check if any listener is registered for the specified eventName
     * @param {String} eventName
     * @returns {Boolean} `true` if listener is registered, otherwise `false`
     */
    hasListener(eventName) {
        return Boolean(this.eventListeners && this.eventListeners[eventName]);
    }

    /**
     * Relays all events through another object that also implements Events mixin. Adds a prefix to the event name
     * before relaying, for example add -> storeAdd
     * ```
     * // Relay all events from store through grid, will make it possible to listen for store events prefixed on grid:
     * 'storeLoad', 'storeChange', 'storeRemoveAll' etc.
     * store.relayAll(grid, 'store');
     *
     * grid.on('storeLoad', () => console.log('Store loaded');
     * ```
     * @param {Core.mixin.Events} through Object to relay the events through, needs to mix Events mixin in
     * @param {String} prefix Prefix to add to event name
     * @param {Boolean} [transformCase] Specify false to prevent making first letter of event name uppercase
     */
    relayAll(through, prefix, transformCase = true) {
        (this.relayAllTargets || (this.relayAllTargets = [])).push({ through, prefix, transformCase });
    }

    /**
     * Removes all listeners registered to this object
     */
    removeAllListeners() {
        const listeners = this.eventListeners;
        let i, thisObj;

        for (const event in listeners) {
            const bucket = listeners[event];

            // We iterate backwards since we call removeListener which will splice out of
            // this array as we go...
            for (i = bucket.length; i-- > 0; /* empty */) {
                const cfg = bucket[i];

                this.removeListener(event, cfg);

                thisObj = cfg.thisObj;

                if (thisObj && thisObj.untrackDetachers) {
                    thisObj.untrackDetachers(this);
                }
            }
        }
    }

    relayEvents(source, eventNames, prefix) {
        const
            listenerConfig = { detachable : true },
            retrigger = (eventName, ...params) => {
                return this.trigger((prefix || '') + eventName, ...params);
            };

        eventNames.forEach(eventName => {
            listenerConfig[eventName] = (...params) => {
                return retrigger(eventName, ...params);
            };
        });

        return source.on(listenerConfig);
    }

    /**
     * Internal function used to hook destroy() calls when using thisObj
     * @private
     */
    attachAutoDetacher(config) {
        const
            target = config.thisObj,
            // If it's a Bryntyum Base subclass, hook doDestroy, otherwise, destroy
            destructorName = ('doDestroy' in target) ? 'doDestroy' : 'destroy';

        if (destructorName in target) {
            config._oldDestructor = target[destructorName];
            target[destructorName]     = () => {
                // Remove listeners first, so that they do not fire during destruction.
                // The observable being listened to by the thisObj may already have
                // been destroyed in a clean up sequence
                if (!this.isDestroyed) {
                    this.removeListener(config);
                }
                config._oldDestructor.call(target);
            };
        }
        else {
            target[destructorName] = () => {
                this.removeListener(config);
            };
        }
    }

    /**
     * Internal function used restore hooked destroy() calls when using thisObj
     * @private
     */
    detachAutoDetacher(config) {
        const
            target = config.thisObj,
            destructorName = ('doDestroy' in target) ? 'doDestroy' : 'destroy';

        if (config._oldDestructor) {
            target[destructorName] = config._oldDestructor;
        }
    }

    //endregion

    //region Promise based workflow

    // experimental, used in tests to support async/await workflow
    await(eventName, options = { checkLog : true, resetLog : true }) {
        const me = this;

        if (options === false) {
            options = { checkLog : false };
        }

        return new Promise(resolve => {
            // check if previously triggered?
            if (options.checkLog && me._triggered && me._triggered[eventName]) {
                // resolve immediately, no params though...
                resolve();
                // reset log to be able to await again
                if (options.resetLog) {
                    me.clearLog(eventName);
                }
            }

            me.on({
                [eventName] : (...params) => {
                    // resolve when event is caught
                    resolve(...params);
                    // reset log to be able to await again
                    if (options.resetLog) {
                        me.clearLog(eventName);
                    }
                },
                prio : -10000, // Let others do their stuff first
                once : true // promises can only be resolved once anyway
            });
        });
    }

    clearLog(eventName) {
        if (this._triggered) {
            if (eventName) {
                delete this._triggered[eventName];
            }
            else {
                this._triggered = {};

            }
        }
    }

    //endregion

    //region Trigger

    /**
     * Wraps a function with event triggering. First triggers before[eventName] with the supplied eventObject. If it is
     * not prevented (by returning false) the supplied fn is called with the eventObject as first argument and any
     * optional arguments after that. It then triggers [eventName] with the eventObject (that might have been altered in
     * fn). Finally it returns the return value from the fn.
     * @param {String} eventName Event name, used to trigger 'beforeEventName' and 'eventName'
     * @param {Object} eventObject Passed when triggering events
     * @param {Function} fn Function to call if before is not prevented
     * @param {Array} args Arguments to pass to fn
     * @param {Boolean} quiet Specify true to not trigger events
     * @returns {*} Result returned from calling fn, or false if prevented
     * @internal
     */
    callPreventable(eventName, eventObject, fn, args = [], quiet = false) {
        const me = this;

        if (!quiet && me.trigger('before' + StringHelper.capitalizeFirstLetter(eventName), eventObject) === false) {
            return false;
        }

        const result = fn(eventObject, ...args);

        if (!quiet) {
            me.trigger(eventName, eventObject);
        }

        return result;
    }

    /**
     * Triggers an event, calling all registered listeners with the supplied arguments. Returning false from any listener
     * makes function return false.
     * @param {String} eventName Event name for which to trigger listeners
     * @param {Object} param Single parameter passed on to listeners, source property will be added to it (this)
     * @param {Boolean} [param.bubbles] Pass as `true` to indicate that the event will bubble up the widget
     * ownership hierarchy. For example up a Menu->parent Menu tree, or a Field->Container tree.
     * @returns {Boolean} Returns false if any listener returned false, otherwise true
     */
    // TODO: should returning false from a listener really prevent other listeners from executing?
    trigger(eventName, param) {
        //<debug>
        if (window.bryntum.LOGEVENTS) console.log(eventName, arguments);
        //</debug>
        const
            me   = this,
            name = eventName.toLowerCase(),
            {
                eventsSuspended,
                relayAllTargets,
                callOnFunctions,
                owner
            }   = me;

        let listeners = me.eventListeners && me.eventListeners[name],
            doSlice   = true;

        // log trigger, used by experimental promise support to resolve immediately when needed
        if (!me._triggered) {
            me._triggered = {};
        }
        me._triggered[eventName] = true;

        if (eventsSuspended) {
            if (eventsSuspended.shouldQueue) eventsSuspended.queue.push(arguments);
            return true;
        }

        // Include catchall listener for all events.
        // Do not push the catchAll listeners onto the events own listener array.
        if (me.eventListeners && me.eventListeners.catchall) {
            (listeners = (listeners ? listeners.slice() : [])).push(...me.eventListeners.catchall);

            // The catchAll listeners must honour their prio settings.
            listeners.sort(priorityComparator);
            doSlice = false;
        }

        if (!listeners && !relayAllTargets && !callOnFunctions) return true;

        // default to include source : this in param
        if (param) {
            if (!('source' in param)) {
                if (Object.isExtensible(param)) {
                    param.source = me;
                }
                else {
                    param = Object.setPrototypeOf({
                        source : me
                    }, param);
                }
            }
        }
        else {
            param = {
                source : me
            };
        }

        // Lowercased event name should be the "type" property in keeping with DOM events.
        if (param.type !== name) {
            // Create instance property because "type" is read only
            if (param.constructor !== Object) {
                Reflect.defineProperty(param, 'type', { get : () => name });
            }
            else {
                param.type = name;
            }
        }

        if (callOnFunctions) {
            const fnName = 'on' + StringHelper.capitalizeFirstLetter(eventName);
            if (fnName in me) {
                me.callback(me[fnName], me, [param]);

                // See if the called function was injected into the instance
                // masking an implementation in the prototype.
                // we must call the class's implementation after the injected one.
                // unless it's an injected chained function, in which case it will have been called above.
                // Note: The handler may have resulted in destruction.
                if (!me.isDestroyed && me.hasOwnProperty(fnName) && (!me.pluginFunctionChain || !me.pluginFunctionChain[fnName])) {
                    const myProto = Object.getPrototypeOf(me);
                    if (fnName in myProto) {
                        myProto[fnName].call(me, param);

                        // A handler may have resulted in destruction.
                        if (me.isDestroyed) {
                            return;
                        }
                    }
                }
            }
        }

        if (listeners) {
            let i = 0,
                listener, handler;

            // Make a flat copy of the listeners to protect against a situation where an event listener
            // adds another listener while triggering the event, which we do not want.
            // The array may already be a copy if there are catchAll listeners. See above.
            if (doSlice) {
                listeners = listeners.slice();
            }

            // If any listener resulted in our destruction, abort.
            for (i; i < listeners.length && !me.isDestroyed; i++) {
                listener = listeners[i];

                let thisObj = listener.thisObj;

                // Listeners that have thisObj are auto removed when thisObj is destroyed. If thisObj is destroyed from
                // a listener we might still end up here, since listeners are sliced and not affected by the removal
                if (!thisObj || !thisObj.isDestroyed) {
                    if (listener.once) {
                        me.removeListener(name, listener);
                    }

                    if (typeof listener.fn === 'string') {
                        if (thisObj) {
                            handler = thisObj[listener.fn];
                        }

                        // keep looking for the callback in the hierarchy
                        if (!handler) {
                            const result = me.resolveCallback(listener.fn);

                            handler = result.handler;
                            thisObj = result.thisObj;
                        }
                    }
                    else {
                        handler = listener.fn;
                    }

                    // If listener was declared with args, send our param after them
                    if (listener.args) {
                        if (handler.call(thisObj || me, ...listener.args, param) === false) {
                            return false;
                        }
                    }
                    else {
                        if (handler.call(thisObj || me, param) === false) {
                            return false;
                        }
                    }
                }
            }
        }

        // relay all?
        if (relayAllTargets) {
            relayAllTargets.forEach(config => {
                let name = eventName;
                if (config.transformCase) name = StringHelper.capitalizeFirstLetter(name);
                if (config.prefix) name = config.prefix + name;
                if (config.through.trigger(name, param) === false) return false;
            });
        }

        // Use DOM standard event property name to indicate that the event
        // bubbles up the owner axis.
        // False from any handler cancels the bubble.
        // The owner prop was cached above in case any handlers destroyed this object.
        // Must also avoid owner if any handlers destroyed the owner.
        if (param.bubbles && owner && !owner.isDestroyed) {
            return owner.trigger(eventName, param);
        }

        return true;
    }

    /**
     * Prevents events from being triggered until {@link #function-resumeEvents()} is called. Optionally queues events that are triggered while
     * suspended. Multiple calls stack to require matching calls to `resumeEvents()` before actually resuming.
     * @param {Boolean} queue Specify true to queue events triggered while suspended
     */
    suspendEvents(queue = false) {
        const eventsSuspended = this.eventsSuspended || (this.eventsSuspended = { shouldQueue : queue, queue : [], count : 0 });
        eventsSuspended.count++;
    }

    /**
     * Resume event triggering after a call to {@link #function-suspendEvents()}. If any triggered events were queued they will be triggered.
     * @returns `true` if events have been resumed (multiple calls to suspend require an equal number of resume calls to resume).
     */
    resumeEvents() {
        const suspended = this.eventsSuspended;
        if (suspended) {
            if (--suspended.count === 0) {
                this.eventsSuspended = null;
                if (suspended.shouldQueue) {
                    for (let queued of suspended.queue) {
                        this.trigger(...queued);
                    }
                }
            }
        }

        return !Boolean(this.eventsSuspended);
    }

    //endregion
};
