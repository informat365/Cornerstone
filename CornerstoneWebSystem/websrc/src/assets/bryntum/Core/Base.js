/**
 * @module Core/Base
 */

import StringHelper from './helper/StringHelper.js';
import VersionHelper from './helper/VersionHelper.js';

const defaultConfigSymbol = Symbol('defaultConfig'),
    propertyInitializersSymbol = Symbol('propertyInitializers'),
    classHierarchySymbol = Symbol('classHierarchy'),
    configurationSymbol = Symbol('configuration'),
    originalConfigSymbol = Symbol('originalConfig'),
    instancePropertiesSymbol = Symbol('instanceProperties');

/**
 * Base class for all configurable classes.
 *
 * Subclasses do not have to implement a constructor with its restriction of having to call super()
 * before there is a `this` reference. Subclasses instead implement a `construct` method which is
 * called by the `Base` constructor. This may call its `super` implementation at any time.
 *
 * The `Base` constructor applies all configs to properties of the new instance. The instance
 * will have been configured after the `super.construct(config)` is called.
 *
 * See the Class System documentation in the guides for more information.
 *
 * @abstract
 */
export default class Base {
    // defaultConfig & properties made private to not spam all other classes

    /**
     * A getter for the default configuration of this class, which can be overridden by configurations passed at construction time.
     * @member {Object} defaultConfig
     * @static
     * @category Configuration
     * @private
     */

    /**
     * A getter for the default values of internal properties for this class
     * @member {Object} properties
     * @static
     * @category Configuration
     * @private
     */

    /**
     * Base constructor, passes arguments to {@link #function-construct}.
     * @param args
     * @function constructor
     * @category Lifecycle
     */
    constructor(...args) {
        // Allow subclasses to have a pseudo constructor with "this" already set;
        this.construct(...args);

        this.afterConstruct();
    }

    /**
     * Base implementation applies configuration.
     *
     * Subclasses need only implement this if they have to initialize instance specific
     * properties required by the class. Often a `construct` method is
     * unnecessary. All initialization of incoming configuration properties can be
     * done in a `set propName` implementation.
     * @param {Object} [config] Usually called with a config object, but accepts any params
     * @category Lifecycle
     */
    construct(...args) {
        // Passing null to base construct means bypass the config system and stack creation (to gain performance)
        if (args[0] !== null) {
            //<debug>
            this.$createdAt = new Error().stack;
            //</debug>
            this.configure(...args);
        }
        this.afterConfigure();
    }

    /**
     * Destroys the provided objects by calling their {@link #function-destroy} method.
     * Skips empty values or objects that are already destroyed.
     *
     * ```javascript
     * Base.destroy(myButton, toolbar1, helloWorldMessageBox);
     * ```
     *
     * @category Lifecycle
     */
    static destroy(...args) {
        args.forEach(object => {
            if (object && object.destroy && !object.isDestroyed) {
                object.destroy();
            }
        });
    }

    /**
     * Destroys the object.
     * Do not override this in subclasses. To provide class-specific destruction, implement a
     * doDestroy method. It is a lifecycle hook that will be called during destruction.
     * @category Lifecycle
     */
    destroy() {
        const me = this;

        //<debug>
        if (me.isDestroyed) {
            throw new Error('Trying to destroy an already destroyed object');
        }
        //</debug>

        me.isDestroying = true;
        me.doDestroy();

        Object.setPrototypeOf(this, null);

        // Clear all remaining instance properties.
        for (let key in me) {
            delete me[key];
        }
        delete me[originalConfigSymbol];

        // Only one property remains to signal why the object is inert.
        me.isDestroyed = true;
    }

    /**
     * This method is required to help `unused` getters to survive production build process. Some tools, like angular,
     * will remove `unused` code in production build, making our side-effected getters behind, breaking code heavily.
     * @internal
     * @param getter Getter to evaluate
     */
    _thisIsAUsedExpression(getter) {}

    static get $name() {
        return this.hasOwnProperty('_$name') && this._$name || this.name;
    }

    get $name() {
        return this.constructor.$name;
    }

    /**
     * Base implementation so that all subclasses and mixins may safely call super.startConfigure.
     *
     * This is called by the Base class before setting configuration properties, but after
     * the active initial getters have been set, so all configurations are available.
     *
     * This method allows all classes in the hierarchy to force some configs to be evaluated before others.
     * @internal
     * @category Lifecycle
     * @params {Object} config The configuration object use to set the initial state.
     */
    startConfigure(config) {

    }

    /**
     * Base implementation so that all subclasses and mixins may safely call super.finishConfigure.
     *
     * This is called by the Base class before exiting the {@link #function-configure} method.
     *
     * At this point, all configs have been applied, but the `isConfiguring` property is still set.
     *
     * This method allows all classes in the hierarchy to inject functionality
     * into the config phase.
     * @internal
     * @category Lifecycle
     * @params {Object} config The configuration object use to set the initial state.
     */
    finishConfigure(config) {

    }

    /**
     * Base implementation so that all subclasses and mixins may safely call `super.afterConfigure`. This is called by the Base class after the {@link #function-configure} method has been
     * called. At this point, all configs have been applied.
     *
     * This method allows all classes in the hierarchy to inject functionality
     * either before or after the super.afterConstruct();
     * @internal
     * @category Lifecycle
     */
    afterConfigure() {

    }

    /**
     * Base implementation so that all subclasses and mixins may safely call super.afterConstruct.
     *
     * This is called by the Base class after the {@link #function-construct} method has been
     * called.
     *
     * At this point, all configs have been applied.
     *
     * This method allows all classes in the hierarchy to inject functionality
     * either before or after the super.afterConstruct();
     * @internal
     * @function afterConstructor
     * @category Lifecycle
     */
    afterConstruct() {

    }

    /**
     * Provides a way of calling callbacks which may have been specified as the _name_ of a function
     * and optionally adds scope resolution.
     *
     * For example, if the callback is specified as a string, then if it is prefixed with `'this.'`
     * then the function is resolved in this object. This is useful when configuring listeners
     * at the class level.
     *
     * If the callback name is prefixed with `'up.'` then the ownership hierarchy is queried
     * using the `owner` property until an object with the named function is present, then the
     * named function is called upon that object.
     * @param {String/Function} handler The function to call, or the name of the function to call.
     * @param {Object} thisObj The `this` object of the function.
     * @param {Object[]} args The argument list to pass.
     */
    callback(fn, thisObject, args) { // Maintainer: do not make args ...args. This method may acquire more arguments
        if (thisObject === 'this') {
            thisObject = this;
        }

        const { handler, thisObj } = this.resolveCallback(fn, thisObject);

        return args ? handler.apply(thisObj, args) : handler.call(thisObj);
    }

    resolveCallback(handler, thisObj = this) {

        // It's a string, we find it in its own thisObj
        if (handler.substr) {
            if (handler.startsWith('up.')) {
                handler = handler.substr(3);

                // Empty loop until we find the function owner
                for (thisObj = this.owner; thisObj && !thisObj[handler]; thisObj = thisObj.owner);

                //<debug>
                if (!thisObj) {
                    throw new Error(`Function ${handler} not found in ownership hierarchy`);
                }
                //</debug>
            }
            else if (handler.startsWith('this.')) {
                thisObj = this;
            }
            //<debug>
            if (!thisObj || !(thisObj instanceof Object)) {
                throw new Error(`Named method ${handler} requires a thisObj object`);
            }
            if (typeof thisObj[handler] !== 'function') {
                throw new Error(`No method named ${handler} on ${thisObj.$name || 'thisObj object'}`);
            }
            //</debug>

            handler = thisObj[handler];
        }

        return { handler, thisObj };
    }

    bindCallback(inHandler, inThisObj = this) {
        if (inHandler) {
            const { handler, thisObj } = this.resolveCallback(inHandler, inThisObj);
            if (handler) {
                return handler.bind(thisObj);
            };
        }
    }

    /**
     * Delays the execution of the passed function by the passed time quantum, or if the time is omitted
     * or not a number, delays until the next animation frame. Note that this will use
     * {@link Core.mixin.Delayable#function-setTimeout} || {@link Core.mixin.Delayable#function-requestAnimationFrame}
     * if this class mixes in `Delayable`, otherwise it uses the global methods. The function will
     * be called using `this` object as its execution scope.
     * @param {Function} fn The function to call on a delay.
     * @param {Number} [by] The number of milliseconds to delay.
     * @private
     */
    delay(fn, by) {
        // Force scope on the fn if we are not a Delayable
        fn = this.setTimeout ? fn : fn.bind(this);

        (typeof by === 'number' ? (this.setTimeout || setTimeout) : (this.requestAnimationFrame || requestAnimationFrame))(fn, by);
    }

    /**
     * Base implementation so that all subclasses and mixins may safely call super.
     * doDestroy is how all subclasses must clean themselves up. It is called by the
     * Base class *before* final destruction of the object.
     * @internal
     * @category Lifecycle
     */
    doDestroy() {

    }

    /**
     * Destroys the named properties if they have been initialized, and if they have a `destroy` method.
     * Deletes the property from this object. For example:
     *
     *      this.destroyProperties('store', 'resourceStore', 'eventStore', 'dependencyStore', 'assignmentStore');
     *
     * @param {String} properties The names of the properties to destroy.
     * @internal
     * @category Lifecycle
     */
    destroyProperties(...properties) {
        const me = this;

        let key, propertyValue;

        for (key of properties) {
            // If the value has *not* been pulled in from the configuration object yet
            // we must not try to access it, as that will cause the property to be initialized.
            if (key in me && (!me[configurationSymbol] || !me[configurationSymbol][key])) {
                propertyValue = me[key];
                if (propertyValue && propertyValue.destroy) {
                    propertyValue.destroy();
                }
                delete me[key];
            }
        }
    }

    /**
     * Called by the Base constructor to apply configs to this instance. The must not be called.
     * @param {Object} config The configuration object from which instance properties are initialized.
     * @private
     * @category Lifecycle
     */
    configure(config = {}) {
        //<debug>
        // Guard against instances being passed in an attempt to clone.
        if (config.constructor.name !== 'Object') {
            throw new Error('Raw object must be passed to configure');
        }
        //</debug>
        const me = this;

        me.initialConfig = config;

        // Important flag for setters to know whether they are being called during
        // configuration when this object is not fully alive, or whether it's being reconfgured.
        me.isConfiguring = true;

        // Assign any instance properties declared by the class.
        Object.assign(me, me.getProperties());

        // Apply configuration to default (Which is safe, because it's a chained object) from class definition.
        // Cache me.config for use by get config.
        me.setConfig(me[originalConfigSymbol] = Base.assign(me.getDefaultConfiguration(), config), true);

        me.isConfiguring = false;
    }

    /**
     * Sets configuration options this object with all the properties passed in the parameter object.
     * Timing is taken care of. If the setter of one config is called first, and references
     * the value of another config which has not yet been set, that config will be set just
     * in time, and the *new* value will be used.
     * @param {Object} config An object containing configurations to change.
     * @category Configuration
     */
    setConfig(config, isConstructing) {
        const me = this,
            wasConfiguring = me[configurationSymbol],
            configDone = wasConfiguring ? me.configDone : (me.configDone = {}),
            instanceProperties = me[instancePropertiesSymbol] = {};

        let key,
            instanceProperty;

        // Cache me.configuration for use by injected property initializers.
        me[configurationSymbol] = wasConfiguring ? Object.setPrototypeOf(Object.assign({}, config), wasConfiguring) : config;

        // For each incoming non-null configuration, create a temporary getter which will
        // pull the value in from the initialConfig so that it doesn't matter in
        // which order properties are set. You can access any property at any time.
        for (key in config) {
            // Don't default null configs inunless it's a direct property of the
            // the passed configuration. When used at construct time, defaultConfigs
            // will be prototype-chained onto the config.
            if (config[key] != null || config.hasOwnProperty(key)) {
                // If there is an existing property with a getter/setter, *not* a value
                // defined on the object for this config we must call it in our injected getter/setter.
                // Maintainer, this is testing the truthiness of the assignment, *not* a botched equality test.
                if (!instanceProperties[key] && (instanceProperty = Reflect.getOwnPropertyDescriptor(me, key)) && !('value' in instanceProperty)) {
                    instanceProperties[key] = instanceProperty;
                }
                // Set up a temporary instance property which will
                // Pull in the value from the initialConfig if the getter
                // is called first.
                Reflect.defineProperty(me, key, Base.createPropInitializer(key));

                if (!isConstructing) {
                    configDone[key] = false;
                }
            }
            else {
                configDone[key] = true;
            }
        }

        if (isConstructing) {
            me.startConfigure(config);
        }

        // Set all our properties from the config object.
        // If one of the properties needs to access a property that has not
        // yet been set, the above temporary property will pull it through.
        // Can't use Object.assign because that only uses own properties.
        // config value blocks are prototype chained subclass->superclass
        for (key in config) {
            // Only push the value through if the property initializer is still present.
            // If it gets triggered to pull the configuration value in, it deleted itself.
            if (!configDone[key]) {
                me[key] = config[key];
            }
        }

        if (wasConfiguring) {
            me[configurationSymbol] = wasConfiguring;
        }
        else {
            delete me[configurationSymbol];
        }

        if (isConstructing) {
            me.finishConfigure(config);
        }
    }

    /**
     * Returns a *copy* of the full configuration which was used to configure this object.
     * @returns {Object} All configuration properties which were used to configure this object.
     * @category Misc
     */
    get config() {
        let result = {},
            myConfig = this[originalConfigSymbol],
            key;

        // The configuration was created as a prototype chain of the class hierarchy's
        // defaultConfig values hanging off a copy of the initialConfig object, so
        // we must loop and copy since Object.assign only copies own properties.
        for (key in myConfig) {
            result[key] = myConfig[key];
        }
        return result;
    }

    /**
     * Gets the full {@link #property-defaultConfig-static} block for this object's entire inheritance chain
     * all the way up to but not including {@link Core.Base}
     * @return {Object} All default config values for this class.
     * @private
     * @category Configuration
     */
    getDefaultConfiguration() {
        return this.constructor.getDefaultConfiguration();
    }

    /**
     * Gets the full {@link #property-defaultConfig-static} block for the entire inheritance chain for this class
     * all the way up to but not including {@link Core.Base}
     * @return {Object} All default config values for this class.
     * @private
     * @category Configuration
     */
    static getDefaultConfiguration() {
        const configDescriptor = this.getConfigDescriptor();

        return configDescriptor.needsFork ? this.fork(configDescriptor.defaultConfig) : Object.setPrototypeOf({}, configDescriptor.defaultConfig);
    }

    /**
     * Gets the full {@link #property-defaultConfig-static} block for this class's entire inheritance chain
     * all the way up to but not including {@link Core.Base}
     * @return {Object} All default config values for this class.
     * @private
     * @category Configuration
     */
    static getConfigDescriptor(shallow) {
        const me = this;

        let configDescriptor = !shallow && me.hasOwnProperty(defaultConfigSymbol) ? me[defaultConfigSymbol] : null,
            defaults, key, cls, value, superConfigDescriptor;

        if (!configDescriptor) {
            //<debug>
            // Export all classes in the bryntum namespace
            // Only in debug mode, bundles expose their classes in a similar way anyway
            window.bryntum[me.$name] = me;
            //</debug>

            configDescriptor = {
                defaultConfig : defaults = me.hasOwnProperty('defaultConfig') ? me.defaultConfig : {},
                // Use 1 instead of true because below we're using |= to or up all the flags from the class hierarchy
                needsFork     : 0
            };

            // Only cache top level descriptors.
            if (!shallow) {
                me[defaultConfigSymbol] = configDescriptor;
            }
            for (key in defaults) {
                value = defaults[key];

                // If any default properties are *mutable* Objects or Array we need to clone them.
                // so that instances do not share configured values.
                if (value && (value.constructor === Object || Array.isArray(value)) && !Object.isFrozen(value)) {
                    // Use 1 instead of true because below we're using |= to or up all the flags from the class hierarchy
                    configDescriptor.needsFork = 1;
                    break;
                }
            }

            // Because of mixins being mixed into different parts of the class hiererchy,
            // we can't chain to an existing deep descriptor. We have to climb from
            // here and assemble the deep descriptor from shallow ones.
            // If any along the way need forking (have an Object or Array), so do we.
            // We chain them together because that is hundreds of times faster
            // than Object.assign: https://jsperf.com/setprototypeof-vs-object-assign/
            if (!shallow) {
                for (cls = me.superclass; cls && cls !== Base; cls = cls.superclass) {
                    superConfigDescriptor = cls.getConfigDescriptor(true);
                    if (Object.keys(superConfigDescriptor.defaultConfig).length) {
                        configDescriptor.needsFork |= superConfigDescriptor.needsFork;
                        Object.setPrototypeOf(defaults, defaults = superConfigDescriptor.defaultConfig);
                    }
                }
            }
        }

        return configDescriptor;
    }

    static fork(obj) {
        var ret, key, value;

        if (obj && obj.constructor === Object && !Object.isFrozen(obj)) {
            ret = Object.setPrototypeOf({}, obj);

            for (key in obj) {
                value = obj[key];

                if (value) {
                    if (value.constructor === Object) {
                        ret[key] = this.fork(value);
                    }
                    else if (value instanceof Array) {
                        ret[key] = value.slice();
                    }
                }
            }
        }
        else {
            ret = obj;
        }

        return ret;
    }

    static assign(dest, ...sources) {
        var i = 0,
            ln = sources.length,
            source, key;

        for (; i < ln; i++) {
            source = sources[i];

            for (key in source) {
                dest[key] = source[key];
            }
        }
        return dest;
    }

    /**
     * Gets the full {@link #property-properties-static} block for this class's entire inheritance chain
     * all the way up to but not including {@link Core.Base}
     * @return {Object} All default config values for this class.
     * @private
     * @category Configuration
     */
    getProperties() {
        const hierarchy = this.classHierarchy(),
            len = hierarchy.length;

        let result = {},
            i,
            cls;

        // TODO: if properties block does not change this could be cached? would speed up loading of big data sets into grid

        // Gather the class result in *top-down* order so that a subclass's properties
        // overrides properties from superclasses.
        //for (cls of this.classHierarchy()) { // replaced for of since it transpiles badly and this is called a lot when creating many instances
        for (i = 0; i < len; i++) {
            cls = hierarchy[i];
            // Skip classes which don't have it. We're going to ask its superclass anyway.
            if (cls.hasOwnProperty('properties')) {
                Object.assign(result, cls.properties);
            }
        }

        return result;
    }

    static get superclass() {
        return Object.getPrototypeOf(this);
    }

    static createPropInitializer(key) {
        // Because initializers for a property name are the same, cache them on the Base
        // class so that only one copy is created for each property name globally.
        var result = (this[propertyInitializersSymbol] || (this[propertyInitializersSymbol] = {}))[key];

        if (!result) {
            let initializingFlagName = 'initializing' + StringHelper.capitalizeFirstLetter(key);

            result = this[propertyInitializersSymbol][key] = {
                configurable : true,
                get() {
                    const me = this,
                        instanceProperty = me[instancePropertiesSymbol][key];

                    // If we took over from an instance property, replace it
                    if (instanceProperty) {
                        Reflect.defineProperty(me, key, instanceProperty);
                    }
                    // Otherwise just delete the instance property who's getter we are in.
                    else {
                        delete me[key];
                    }

                    // Set the value from the configuration.
                    me[initializingFlagName] = true;
                    me[key] = me[configurationSymbol][key];
                    me[initializingFlagName] = false;

                    // The property has been *pulled* from the configuration.
                    // Prevent the setting loop in configure from setting it again.
                    me.configDone[key] = true;

                    // Finally, allow the prototype getter to return the value.
                    return me[key];
                },
                set(value) {
                    const me = this,
                        instanceProperty = me[instancePropertiesSymbol][key];

                    // If we took over from an instance property, replace it
                    if (instanceProperty) {
                        Reflect.defineProperty(me, key, instanceProperty);
                    }
                    // Otherwise just delete the instance property who's getter we are in.
                    else {
                        delete me[key];
                    }

                    // The config has been set (some internal code may have called the setter)
                    // so prevent it from being called again and overwritten with data from initialConfig.
                    me.configDone[key] = true;

                    // Set the property normally (Any prototype setter will be invoked)
                    me[key] = value;
                }
            };
        }

        return result;
    }

    /**
     * Used by the Widget and GridFeatureManager class internally. Returns the class hierarchy of this object
     * starting from the `topClass` class (which defaults to `Base`).
     *
     * For example `classHierarchy(Widget)` on a Combo would yield `[Widget, Field, TextField, PickerField, Combo]`
     * @param {Function} [topClass] The topmost class constructor to start from.
     * @returns {Function[]} The class hierarchy of this instance.
     * @private
     * @category Configuration
     */
    classHierarchy(topClass) {
        let cls = this.constructor,
            fullClassHierarchy = cls.hasOwnProperty(classHierarchySymbol) ? cls[classHierarchySymbol] : null,
            result;

        // Collect the full class hierarchy only once.
        if (!fullClassHierarchy) {
            // Using Object.getPrototypeOf instead of Refect.getPrototypeOf because:
            // 1. The are almost the same, according to the MDN difference is handling getPrototypeOf('string')
            // 2. It allows to pass security check is salesforce environment
            for (fullClassHierarchy = cls[classHierarchySymbol] = []; cls !== Base; cls = Object.getPrototypeOf(cls)) {
                fullClassHierarchy.unshift(cls);
            }
            // Don't let anybody mutate this. It's cached.
            Object.freeze(fullClassHierarchy);
        }

        // Cut down to the requested topClass
        if (topClass) {
            for (let i = 0; !result && i < fullClassHierarchy.length; i++) {
                if (fullClassHierarchy[i] === topClass) {
                    result = fullClassHierarchy.slice(i);
                }
            }
        }
        else {
            result = fullClassHierarchy;
        }
        return result;
    }

    /**
     * Same as {@link #function-classHierarchy} but returns array of class names obtained via $name property
     *
     * @param {Function} [topClass] The topmost class constructor to start from.
     * @returns {String[]} The class name hierarchy of this instance.
     * @private
     * @category Configuration
     */
    classNameHierarchy(topClass) {
        return this.classHierarchy(topClass).map(f => f.$name);
    }

    /**
     * Checks if an obj is of type using object's $name property and doing string comparision of the property with the type parameter.
     *
     * @param {String} type
     * @return {Boolean}
     */
    static isOfTypeName(type) {
        return this.classNameHierarchy().includes(type);
    }

    /**
     * Removes all event listeners that were registered with the given `name`.
     * @param {String} name The name of the event listeners to be removed.
     */
    detachListeners(name) {
        let detachers = this.$detachers;

        detachers = detachers && detachers[name];

        if (detachers) {
            while (detachers.length) {
                detachers.pop()();
            }
        }
    }

    /**
     * Tracks a detacher function for the specified listener name.
     * @param {String} name The name assigned to the associated listeners.
     * @param {Function} detacher The detacher function.
     * @private
     */
    trackDetacher(name, detacher) {
        const
            detachers = this.$detachers || (this.$detachers = {}),
            bucket = detachers[name] || (detachers[name] = []);

        bucket.push(detacher);
    }

    /**
     * Removes all detacher functions for the specified `Events` object. This is called
     * by the `removeAllListeners` method on that object which is typically called by its
     * `destroy` invocation.
     * @param {Core.mixin.Events} eventer The `Events` instance to untrack.
     * @private
     */
    untrackDetachers(eventer) {
        const detachers = this.$detachers;

        if (detachers) {
            for (const name in detachers) {
                const bucket = detachers[name];

                for (let i = bucket.length; i-- > 0; /* empty */) {
                    if (bucket[i].eventer === eventer) {
                        bucket.splice(i, 1);
                    }
                }
            }
        }
    }
}

// Avoid some object shape changes:
Object.assign(Base.prototype, {
    $detachers : null
});

//<debug>
// Export this class in the bryntum namespace.
// All subclasses are exported on first instantiation;
(window.bryntum || (window.bryntum = {})).Base = Base;
//</debug>

VersionHelper.setVersion('core', '0.0.0');
