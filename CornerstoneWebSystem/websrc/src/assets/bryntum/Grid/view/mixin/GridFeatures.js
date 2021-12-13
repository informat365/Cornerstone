import Base from '../../../Core/Base.js';
import StringHelper from '../../../Core/helper/StringHelper.js';
import GridFeatureManager from '../../feature/GridFeatureManager.js';
import ObjectHelper from '../../../Core/helper/ObjectHelper.js';

/**
 * @module Grid/view/mixin/GridFeatures
 */

let validConfigTypes = {
    string   : 1,
    object   : 1,
    function : 1 // used by CellTooltip
};

/**
 * Mixin for Grid that handles features. Features are plugins that add functionality to the grid. Feature classes should
 * register with Grid by calling {@link Grid.feature.GridFeatureManager#function-registerFeature-static registerFeature}. This
 * enables features to be specified and configured in grid
 * config.
 *
 * Define which features to use:
 *
 * ```javascript
 * // specify which features to use (note that some features are used by default)
 * const grid = new Grid({
 *   features: {
 *      sort: 'name',
 *      search: true
 *   }
 * });
 * ```
 *
 * Access a feature in use:
 *
 * ```javascript
 * grid.features.search.search('cat');
 * ```
 *
 * Basic example of implementing a feature:
 *
 * ```javascript
 * class MyFeature extends InstancePlugin {
 *
 * }
 *
 * GridFeatures.registerFeature(MyFeature);
 *
 * // using the feature
 * const grid = new Grid({
 *   features: {
 *     myFeature: true
 *   }
 * });
 * ```
 *
 * @mixin
 */
export default Target => class GridFeatures extends (Target || Base) {
    //region Init

    /**
     * Specify which features to use on the grid. Most features accepts a boolean, some also accepts a config object.
     * Please note that if you are not using the bundles you might need to import the features you want to use.
     *
     * ```javascript
     * const grid = new Grid({
     *     features : {
     *         stripe : true,   // Enable stripe feature
     *         sort   : 'name', // Configure sort feature
     *         group  : false   // Disable group feature
     *     }
     * }
     * ```
     *
     * @config {Object} features
     * @category Common
     */

    /**
     * Map of the features available on the grid. Use it to access them on your grid object
     *
     * ```javascript
     * grid.features.group.expandAll();
     * ```
     *
     * @readonly
     * @property {Object}
     * @category Common
     * @typings any
     */
    set features(features) {
        const me              = this,
            defaultFeatures = GridFeatureManager.getInstanceDefaultFeatures(this);

        features = me._features = ObjectHelper.assign({}, features);

        // default features, enabled unless otherwise specified
        if (defaultFeatures) {
            Object.entries(defaultFeatures).forEach(([as, featureClass]) => {
                if (!(as in features)) {
                    features[as] = true;
                }
            });
        }

        // We *prime* the features so that if any configuration code accesses a feature, it
        // will self initialize, but if not, they will remain in a primed state until afterConfigure.
        let featureName, config,
            featureClass;

        const registeredInstanceFeatures = GridFeatureManager.getInstanceFeatures(this);

        for (featureName of Object.keys(features)) {
            config = features[featureName];

            // Create feature initialization property if config is truthy.
            // Config must be a valid configuration value for the feature class.
            if (config) {
                // Feature configs name must start with lowercase letter to be valid
                if (StringHelper.lowercaseFirstLetter(featureName) !== featureName) {
                    throw new Error(me.L('invalidFeatureNameFormat', featureName));
                }

                featureClass = registeredInstanceFeatures[featureName];

                if (!featureClass) {
                    throw new Error(me.L('featureNotFound', featureName));
                }

                // Create a self initializing property on the features object named by the feature name.
                // when accessed, it will create and return the real feature.
                // Now, if some Feature initiualization code attempt to access a feature which has not yet been initialized
                // it will be initialized just in time.
                Reflect.defineProperty(features, featureName, me.createFeatureInitializer(features, featureName, featureClass, config));
            }
        }
    }

    get features() {
        return this._features;
    }

    createFeatureInitializer(features, featureName, featureClass, config) {
        const constructorArgs = [this],
            construct = featureClass.prototype.construct;

        // Only pass config if there is one.
        // The constructor(config = {}) only works for undefined config
        if (validConfigTypes[typeof config]) {
            constructorArgs[1] = config;
        }

        return {
            configurable : true,
            get() {
                // Delete this defined property and replace it with the Feature instance.
                delete features[featureName];

                // Ensure the feature is injected into the features object before initialization
                // so that it is available from call chains from its initialization.
                featureClass.prototype.construct = function(...args) {
                    features[featureName] = this;
                    construct.apply(this, args);
                    featureClass.prototype.construct = construct;
                };

                // Return the Feature instance
                return new featureClass(...constructorArgs);
            }
        };
    }

    //endregion

    //region Other stuff

    /**
     * Check if a feature is included
     * @param {String} name Feature name, as registered with `GridFeatureManager.registerFeature()`
     * @returns {Boolean}
     * @category Misc
     */
    hasFeature(name) {
        return !!(this.features && this.features[name]);
    }

    //endregion

    // This does not need a className on Widgets.
    // Each *Class* which doesn't need 'b-' + constructor.name.toLowerCase() automatically adding
    // to the Widget it's mixed in to should implement thus.
    get widgetClass() {}
};
