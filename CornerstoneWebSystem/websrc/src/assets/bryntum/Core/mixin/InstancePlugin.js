import Base from '../Base.js';
import Events from '../mixin/Events.js';
import Localizable from '../localization/Localizable.js';

/**
 * @module Core/mixin/InstancePlugin
 */

/**
 * Base class for plugins. Published functions will be available from the other class. `this` in published functions is
 * referenced to the plugin, access the other class using `this.pluggedInto` (deprecated in 2.3) or `this.client`.
 *
 * Observe that plugin doesn't apply itself on class level but instead on instance level. Plugin is its own instance
 * that can have own functions and data that is not exposed to target class.
 *
 * Functions can be published in four ways:
 *
 * * `assign` (when function is not already available on target)
 * * `before` (when function is already available on target, will be called before original function)
 * * `after` (when function is already available on target, will be called after original function)
 * * `override` (replaces function on target, but old function can be reached)
 *
 * To configure which functions get published and in what way, specify `pluginConfig` getter on plugin:
 *
 * ```
 * class Sort extends InstancePlugin {
 *   static get pluginConfig {
 *      return {
 *          before   : ['init'],
 *          after    : ['destroy', 'onElementClick'],
 *          override : ['render']
 *      };
 *   }
 * }
 * ```
 *
 * @mixes Core/localization/Localizable
 * @mixes Core/mixin/Events
 */
export default class InstancePlugin extends Localizable(Events(Base)) {

    //region Config

    static get defaultConfig() {
        return {
            /**
             * The plugin disabled state
             * @config {Boolean}
             * @default
             */
            disabled : false
        };
    }

    //endregion

    //region Init

    /**
     * Call from another instance to add plugins to it.
     * @example
     * InstancePlugin.initPlugins(this, Search, Stripe);
     * @param plugInto Instance to mix into (usually this)
     * @param plugins Classes to plug in
     * @internal
     */
    static initPlugins(plugInto, ...plugins) {
        const property = plugInto.plugins || (plugInto.plugins = {});

        for (const PluginClass of plugins) {
            property[PluginClass.$name] = new PluginClass(plugInto);
        }
    }

    /**
     * The Widget which was passed into the constructor,
     * which is the Widget we are providing extra services for.
     * @property {Core.widget.Widget}
     * @readonly
     */
    get client() {
        return this._client;
    }

    set client(client) {
        this._client = client;
    }

    /**
     * Initializes the plugin.
     * @internal
     * @param plugInto Target instance to plug into
     * @function constructor
     */
    construct(plugInto, config) {
        this.pluggedInto = this.client = plugInto;

        super.construct(config);

        this.applyPluginConfig(plugInto);
    }

    /**
     * Applies config as found in plugInto.pluginConfig, or published all if no config found.
     * @private
     * @param plugInto Target instance to plug into
     */
    applyPluginConfig(plugInto) {
        const
            me          = this,
            config      = me.pluginConfig || me.constructor.pluginConfig;

        if (config) {
            const { assign, chain, after, before, override } = config;

            assign && me.applyAssign(plugInto, assign);
            (chain || after) && me.applyChain(plugInto, chain || after);
            before && me.applyChain(plugInto, before, false);
            override && me.applyOverride(plugInto, override);
        }
    }

    /**
     * Applies assigning for specified functions.
     * @private
     * @param plugInto
     * @param fnNames
     */
    applyAssign(plugInto, fnNames) {
        fnNames.forEach(fnName => this.assign(plugInto, fnName));
    }

    /**
     * Applies chaining for specified functions.
     * @private
     * @param plugInto
     * @param fnNames
     * @param after
     */
    applyChain(plugInto, fnNames, after = true) {
        fnNames.forEach(fnName => {
            if (plugInto[fnName]) {
                this.chain(plugInto, fnName, after);
            }
            else {
                this.assign(plugInto, fnName);
            }
        });
    }

    /**
     * Applies override for specified functions.
     * @private
     * @param plugInto
     * @param fnNames
     */
    applyOverride(plugInto, fnNames) {
        const me = this;

        if (!me.overridden) {
            me.overridden = {};
        }

        fnNames.forEach(fnName => {
            if (!me[fnName]) {
                throw new Error(this.L('overrideFnMissing', {
                    plugIntoName : plugInto.$name,
                    pluginName   : me.$name,
                    fnName       : fnName
                }));
            }
            // override
            if (typeof plugInto[fnName] === 'function') {
                me.overridden[fnName] = plugInto[fnName].bind(plugInto);
            }

            plugInto[fnName] = me[fnName].bind(me);
        });
    }

    /**
     * Assigns specified functions.
     * @private
     * @param plugInto
     * @param fnName
     */
    assign(plugInto, fnName) {
        const
            me       = this,
            property = Object.getOwnPropertyDescriptor(Object.getPrototypeOf(me), fnName);

        if (property && (property.get || property.set)) {
            // getter/setter, define corresponding property on target
            Object.defineProperty(plugInto, fnName, {
                configurable : true,
                enumerable   : true,
                get          : property.get && property.get.bind(me),
                set          : property.set && property.set.bind(me)
            });
        }
        else {
            plugInto[fnName] = me[fnName].bind(me);
        }
    }

    //endregion

    //region Chaining

    /**
     * Chains functions. When the function is called on the target class all functions in the chain will be called in
     * the order they where added.
     * @private
     * @param plugInto
     * @param key
     */
    chain(plugInto, key, after = true) {
        const
            me    = this,
            chain = plugInto.pluginFunctionChain || (plugInto.pluginFunctionChain = {});

        // duplicate function, make chain and use function to run all functions in it upon call...
        if (!chain[key]) {
            chain[key] = [plugInto[key].bind(plugInto)];
        }

        if (!me[key]) {
            throw new Error(
                this.L('fnMissing', {
                    plugIntoName : plugInto.$name,
                    pluginName   : me.$name,
                    fnName       : key
                })
            );
        }

        chain[key][after ? 'push' : 'unshift'](me[key].bind(me));

        // use function to run all functions in chain on call
        plugInto[key] = (...params) => me.functionChainRunner(key, ...params);
    }

    /**
     * Used to run multiple plugged in functions with the same name, see chain above. Returning false from a
     * function will abort chain.
     * @private
     * @param fnName
     * @param params
     * @returns value returned from last function in chain (or false if any returns false)
     */
    functionChainRunner(fnName, ...params) {
        const chain = this.client.pluginFunctionChain[fnName];
        let returnValue;

        // changed from for..of to try and fix Edge problems
        for (let i = 0; i < chain.length; i++) {
            returnValue = chain[i](...params);
            if (returnValue === false) {
                return false;
            }
        }

        return returnValue;
    }

    //endregion

    /**
     * Called when disabling/enabling the plugin. By default removes the cls of the plugin from its client.
     * Override in subclasses to take any other actions necessary.
     */
    doDisable(disable) {
        const
            { constructor } = this,
            cls = 'featureClass' in constructor ? constructor.featureClass : `b-${constructor.$name.toLowerCase()}`;

        // Some features do not use a cls
        if (cls) {
            this.client && this.client.element && this.client.element.classList[disable ? 'remove' : 'add'](cls);
        }

        if (disable) {
            /**
             * Fired when the plugin/feature is disabled.
             * @event disable
             * @param {Core.mixin.InstancePlugin} source
             */
            this.trigger('disable');
        }
        else {
            /**
             * Fired when the plugin/feature is enabled.
             * @event enable
             * @param {Core.mixin.InstancePlugin} source
             */
            this.trigger('enable');
        }
    }

    /**
     * Get/set the plugin disabled state
     * @property {Boolean}
     */
    get disabled() {
        return this._disabled;
    }

    set disabled(disabled) {
        this._disabled = disabled;

        this.doDisable(disabled);
    }
}
