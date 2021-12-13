import StringHelper from '../../Core/helper/StringHelper.js';

/**
 * @module Grid/feature/GridFeatureManager
 */

const
    consumerToFeatureMap        = new Map(),
    consumerToDefaultFeatureMap = new Map(),
    DEFAULT_FOR_TYPE            = 'Grid',
    remapToBase                 = {
        'Grid'      : 'GridBase',
        'Scheduler' : 'SchedulerBase',
        'Gantt'     : 'GanttBase'
    },
    classNameFix = /\$\d+$/;

/**
 * Static class intended to register and query grid features
 *
 * @class
 */
export default class GridFeatureManager {
    /**
     * Register a feature class with the Grid. Enables it to be created and configured using config Grid#features.
     * @param {Core.mixin.InstancePlugin} featureClass Feature to register
     * @param {Boolean} [onByDefault] Specify true to have the feature enabled per default
     * @param {String|String[]} [forType] Specify a type to let the class applying the feature to determine if it should use it
     */
    static registerFeature(featureClass, onByDefault = false, forType = null, as = null) {
        // Our built in features should all define $name to survive minification/obfuscation, but user defined features might not
        as = StringHelper.lowercaseFirstLetter(as || (featureClass.hasOwnProperty('$name') && featureClass.$name) || featureClass.name);

        // Remove webpack's disambiguation suffix.
        // For example ExcelExporter in Scheduler will be called ExcelExporter$1
        // It must be found as ExcelExporter in the Scheduler's feature Map, so correct the name.
        as = as.replace(classNameFix, '');

        if (!Array.isArray(forType)) {
            forType = [forType || DEFAULT_FOR_TYPE];
        }

        forType.forEach(forType => {
            const
                type                       = remapToBase[forType] || forType,
                consumerFeaturesMap        = consumerToFeatureMap.get(type) || new Map(),
                consumerDefaultFeaturesMap = consumerToDefaultFeatureMap.get(type) || new Map();

            consumerFeaturesMap.set(as, featureClass);
            consumerDefaultFeaturesMap.set(featureClass, onByDefault);

            consumerToFeatureMap.set(type, consumerFeaturesMap);
            consumerToDefaultFeatureMap.set(type, consumerDefaultFeaturesMap);
        });
    }

    /**
     * Get all the features registered for the given type name in an object where keys are feature names and values are feature constructors.
     *
     * @param {String} [forType]
     * @return {Object}
     */
    static getTypeNameFeatures(forType = DEFAULT_FOR_TYPE) {
        const
            type                = remapToBase[forType] || forType,
            consumerFeaturesMap = consumerToFeatureMap.get(type),
            features            = {};

        if (consumerFeaturesMap) {
            consumerFeaturesMap.forEach((featureClass, as) => features[as] = featureClass);
        }

        return features;
    }

    /**
     * Get all the default features registered for the given type name in an object where keys are feature names and values are feature constructors.
     *
     * @param {String} [forType]
     * @return {Object}
     */
    static getTypeNameDefaultFeatures(forType = DEFAULT_FOR_TYPE) {
        const
            type                       = remapToBase[forType] || forType,
            consumerFeaturesMap        = consumerToFeatureMap.get(type),
            consumerDefaultFeaturesMap = consumerToDefaultFeatureMap.get(type);

        let features = {};

        if (consumerFeaturesMap && consumerDefaultFeaturesMap) {
            consumerFeaturesMap.forEach((featureClass, as) => {
                if (consumerDefaultFeaturesMap.get(featureClass)) {
                    features[as] = featureClass;
                }
            });
        }

        return features;
    }

    /**
     * Gets all the features registered for the given instance type name chain. First builds the type name chain then quaries for features
     * for each type name and combines them into one object, see {@link #function-getTypeNameFeatures-static}() for returned object description.
     * If feature is registered for both parent and child type name then feature for child overrides feature for parent.
     *
     * @param {Object} instance
     * @return {Object}
     */
    static getInstanceFeatures(instance) {
        return instance.classNameHierarchy().reduce(
            (features, typeName) => Object.assign(features, this.getTypeNameFeatures(typeName)),
            {}
        );
    }

    /**
     * Gets all the *defualt* features registered for the given instance type name chain. First builds the type name chain then quaries for features
     * for each type name and combines them into one object, see {@link #function-getTypeNameFeatures-static}() for returned object description.
     * If feature is registered for both parent and child type name then feature for child overrides feature for parent.
     *
     * @param {Object} instance
     * @return {Object}
     */
    static getInstanceDefaultFeatures(instance) {
        return instance.classNameHierarchy().reduce(
            (features, typeName) => Object.entries(
                this.getTypeNameFeatures(typeName)
            ).reduce(
                (features, [as, featureClass]) => {
                    if (this.isDefaultFeatureForTypeName(featureClass, typeName)) {
                        features[as] = featureClass;
                    }
                    else {
                        delete features[as];
                    }
                    return features;
                },
                features
            ),
            {}
        );
    }

    /**
     * Checks if the given feature class is default for the type name
     *
     * @param {Core.mixin.InstancePlugin} featureClass Feature to check
     * @param {String} [forType]
     * @return {Boolean}
     */
    static isDefaultFeatureForTypeName(featureClass, forType = DEFAULT_FOR_TYPE) {
        const
            type                       = remapToBase[forType] || forType,
            consumerDefaultFeaturesMap = consumerToDefaultFeatureMap.get(type);
        return consumerDefaultFeaturesMap && consumerDefaultFeaturesMap.get(featureClass) || false;
    }

    /**
     * Checks if the given feature class is default for the given instance type name chain. If the feature is not default for the
     * parent type name but it is for the child type name, then the child setting overrides the parent one.
     *
     * @param {Core.mixin.InstancePlugin} featureClass Feature to check
     * @param {String} [forType]
     * @return {Boolean}
     */
    static isDefaultFeatureForInstance(featureClass, instance) {
        //const typeChain = ObjectHelper.getTypeNameChain(instance);
        const typeChain = instance.classNameHierarchy().reverse();

        let result = null;

        for (let i = 0, len = typeChain.length; i < len && result === null; ++i) {

            const consumerDefaultFeaturesMap = consumerToDefaultFeatureMap.get(typeChain[i]);

            if (consumerDefaultFeaturesMap && consumerDefaultFeaturesMap.has(featureClass)) {
                result = consumerDefaultFeaturesMap.get(featureClass);
            }
        }

        return result || false;
    }

    /**
     * Resets feature registration date, used in tests to reset state after test
     *
     * @internal
     */
    static reset() {
        consumerToFeatureMap.clear();
        consumerToDefaultFeatureMap.clear();
    }
}
