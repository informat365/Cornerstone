import Base from '../../../Core/Base.js';
import StringHelper from '../../../Core/helper/StringHelper.js';

/**
 * @module Grid/feature/mixin/GridFeatureDataLayer
 */

/**
 * Mixin handling common feature data layer facility.
 *
 * The mixin is designed for grid feature to consume. Since it's impossible to predict what store a feature might need the mixin provides a way
 * to obtain a custom store via minimized yet useful set of template methods with default implementation provided.
 *
 * There are several phases during a feature lifetime the mixin methods should/might be invoked:
 *
 * 1. Construction/configuration
 * 2. Component destruction
 * 3. Component lifetime
 *
 * 1. Construction/configuration time
 * ----------------------------------
 * At construction time the mixin relies on {@link #function-configDataLayer} to inject configuration properties into the mixin config object.
 * The method iterates over {@link #config-dataLayerStores} array and checks if corresponding config option is already set
 * in the provided feature configuration object. If there's no such option present then mixin calls obtain*() method where * represents
 * a store name from {@link #config-dataLayerStores} array with the first letter capitalized.
 *
 * The mixin will also create store accessors if ones do not exist at this time.
 *
 * 2. Grid destruction time
 * ------------------------
 * At this time the mixin will do the usual cleanup. Detach stores event listeners if those were provided via calls to get*ListenersConfig()
 * methods, where * represents a store name from {@link #config-dataLayerStores} array with the first letter capitalized. Set previously obtained
 * store references to null.
 *
 * 3. Component lifetime
 * ---------------------
 * It might be that the stores the feature is needed are available only after the client component has been already rendered. To obtain such stores
 * one should call mixin's {@link #function-attachToDataLayer} method. The method will iterate over {@link #config-dataLayerStores} configuration
 * option and try to obtain any store, which hasn't been yet obtained, via call to obtain*() method, where * represents a store name
 * from {@link #config-dataLayerStores} array with the first letter capitalized. The difference between calls to obtain*() method at construction/configuration
 * time and component lifetime is that for the second case the obtain*() methods will be called without the second argument.
 *
 * During a component lifetime store accessors might be called as well as {@link #function-attachToDataLayer} method. If store reference set
 * via an accessors is different from the one the mixing already has then the previous store event listeners will be detached
 * and new listeners configuration will be requested via call to get*ListenersConfig(), where * represents a store name
 * from the {@link #config-dataLayerStores} array with the first letter capitalized.
 *
 * @mixin
 * @private
 */
export default Target => class GridFeatureDataLayer extends (Target || Base) {

    static get defaultConfig() {
        return {
            /**
             * List of data layer store names the mixin will take to obtain store instances, create corresponding store accessors and
             * attach listeners to.
             *
             * @config {String[]}
             */
            dataLayerStores : ['store']
        };
    }

    startConfigure(config) {
        this.configDataLayer(this.client, config);
        super.startConfigure(config);
    }

    doDestroy() {
        (this.dataLayerStores || []).forEach(s => {
            if (s === 'columns') {
                s = 'columnStore';
            }
            this[s] = null;
        });

        return super.doDestroy();
    }

    /**
     *
     * @param {Grid.view.Grid} client
     * @param {Object} config
     * @returns {Object}
     * @internal
     */
    configDataLayer(client, config) {
        return (config.dataLayerStores || []).reduce((config, s) => {
            if (s === 'columnStore' || s === 'columns') {
                s = 'columnStore';
                config.columnStore = config.columnStore || config.columns || this.obtainColumnStore(client, config);
            }
            else {
                const
                    obtainFnName = `obtain${StringHelper.capitalizeFirstLetter(s)}`,
                    obtainFn = this[obtainFnName];

                config[s] = config[s] || (obtainFn && obtainFn.call(this, client, config));
            }

            if (!Reflect.has(this, s) && Object.isExtensible(this) && !Object.isSealed(this) && !Object.isFrozen(this)) {
                Object.defineProperty(this, s, {
                    get : function() {
                        return this[`_${s}`];
                    },
                    set : function(store) {
                        this.setDataLayerStore(s, store);
                    }
                });
            }

            return config;
        }, config);
    }

    /**
     * Call this method to obtain stores not yet obtained.
     */
    attachToDataLayer(force = false) {
        (this.dataLayerStores || []).forEach(s => {
            if (s === 'columns') {
                s = 'columnStore';
            }

            const
                obtainFnName = `obtain${StringHelper.capitalizeFirstLetter(s)}`,
                obtainFn = this[obtainFnName];

            if ((!this[s] || force) && obtainFn) {
                this[s] = obtainFn.call(this, this.client);
            }
        });
    }

    setDataLayerStore(propName, store) {
        const
            privPropName = `_${propName}`,
            detacherName = `_${propName}Detacher`,
            onChangeTplMethodName = `on${StringHelper.capitalizeFirstLetter(propName)}Change`,
            listenersGetterName = `get${StringHelper.capitalizeFirstLetter(propName)}ListenersConfig`;

        if (this[privPropName] !== store) {

            if (this[detacherName]) {
                this[detacherName]();
                this[detacherName] = null;
            }

            if (this[onChangeTplMethodName]) {
                this[onChangeTplMethodName](store, this[privPropName]);
            }

            this[privPropName] = store;

            if (store && this[listenersGetterName]) {
                const listeners = this[listenersGetterName]();

                if (listeners) {
                    this[detacherName] = store.on(Object.assign({ thisObj : this, detachable : true }, listeners));
                }
            }
        }
    }

    obtainStore(client, config) {
        return client.store;
    }

    obtainColumnStore(client, config) {
        return client.columns;
    }
};
