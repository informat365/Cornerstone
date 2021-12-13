import VersionHelper from '../helper/VersionHelper.js';

/**
 * @module Core/mixin/Override
 */

const excludedPropNames = {
    constructor : 1,
    prototype   : 1,
    name        : 1,
    length      : 1,
    arguments   : 1,
    caller      : 1,
    callee      : 1,
    __proto__   : 1
};

/**
 * Simplifies overriding class methods by allowing methods from another class to be used as overrides.
 * Overrides are defined as own classes. They must at a minimum contain a static getter named targetClass, which
 * should return the class to override. Apply the override by calling {@link #function-apply-static apply()}.
 *
 * @example
 * class TemplateColumnOverride {
 *   static get target() {
 *     return {
 *             class: TemplateColumn,
 *             product: 'grid',
 *             minVersion: '1.0',
 *             maxVersion: '1.5'
 *     }
 *   }
 *
 *   renderer(renderData) {
 *       // call overridden function (optional)
 *       const value = this._overridden.renderer.call(this, renderData);
 *
 *       return 'HELLO' + value;
 *   }
 * }
 * Override.apply(TemplateColumnOverride);
 */
export default class Override {
    /**
     * Apply override. We strongly suggest that you at least specify a maxVersion for your overrides.
     * ```
     * class OriginalOverride {
     *     static get target() {
     *         return {
     *             class: Original,
     *             product: 'grid',
     *             minVersion: '1.0',
     *             maxVersion: '1.5'
     *         }
     *     }
     * }
     * ```
     * @param override An override class definition
     */
    static apply(override) {
        if (!override.target) throw new Error('Override must specify what it overrides, using static getter target');
        if (!override.target.class) throw new Error('Override must specify which class it overrides, using target.class');

        if (!this.shouldApplyOverride(override)) return false;

        const staticKeys   = Object.getOwnPropertyNames(override),
            instanceKeys = Object.getOwnPropertyNames(override.prototype);

        staticKeys.splice(staticKeys.indexOf('target'), 1);

        this.internalOverrideAll(override.target.class, staticKeys, override);
        this.internalOverrideAll(override.target.class.prototype, instanceKeys, override.prototype);

        return true;
    }

    static internalOverrideAll(targetClass, properties, overrideDefinition) {
        Reflect.ownKeys(overrideDefinition).forEach(key => {
            if (properties.includes(key) && !excludedPropNames[key]) {
                const desc = Object.getOwnPropertyDescriptor(overrideDefinition, key);
                let currentTargetClass = targetClass;

                let targetProperty = null;

                // Walk up the prototype chain to find fn, needed for mixin overrides applied to class that has them
                // mixed in
                while (!targetProperty && currentTargetClass) {
                    targetProperty = Object.getOwnPropertyDescriptor(currentTargetClass, key);
                    if (!targetProperty) {
                        currentTargetClass = Object.getPrototypeOf(currentTargetClass);
                    }
                }

                if (targetProperty) {
                    this.internalOverride(currentTargetClass, key, desc, targetProperty);
                }
            }
        });
    }

    static internalOverride(target, key, desc, targetDesc) {
        const overrides = target._overridden = target._overridden || {};

        overrides[key] = target[key];

        if (targetDesc.get) {
            Object.defineProperty(target, key, {
                enumerable   : false,
                configurable : true,
                get          : desc.get
            });
        }
        else {
            target[key] = desc.value;
        }
    }

    /**
     * Checks versions if an override should be applied. Specify version in your overrides target config
     * @param override
     * @returns {Boolean}
     * @example
     * class OriginalOverride {
     *     static get target() {
     *         return {
     *             class: Original,
     *             product: 'grid',
     *             minVersion: '1.0',
     *             maxVersion: '1.5'
     *         }
     *     }
     * }
     * @private
     */
    static shouldApplyOverride(override) {
        const config = override.target;
        // not using versioning, allow override
        if (!config.maxVersion && !config.minVersion) return true;

        // must specify product to be able to lookup versions
        if (!config.product) throw new Error('Override must specify product when using versioning');

        // override is for older version, disallow
        if (config.maxVersion && VersionHelper[config.product].isNewerThan(config.maxVersion)) {
            //<debug>
            console.log(`Override ${override.name} for class ${config.class.name} is for an older version of ${config.product} and will not be applied`);
            //</debug>
            return false;
        }

        // override is for newer version, disallow
        if (config.minVersion && VersionHelper[config.product].isOlderThan(config.minVersion)) {
            //<debug>
            console.log(`Override ${override.name} for class ${config.class.name} is for a newer version of ${config.product} and will not be applied`);
            //</debug>
            return false;
        }

        // override is for current version, allow
        return true;
    }
}
