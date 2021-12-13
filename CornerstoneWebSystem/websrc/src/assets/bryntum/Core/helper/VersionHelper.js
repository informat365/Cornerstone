/**
 * @module Core/helper/VersionHelper
 */

/**
 * Helper for version handling
 * @private
 * @example
 *
 * VersionHelper.setVersion('grid', '1.5');
 *
 * if (VersionHelper.getVersion('grid').isNewerThan('1.0')) {
 *   ...
 * }
 */
export default class VersionHelper {
    /**
     * Set version for specified product
     * @private
     * @param {String} product
     * @param {String} version
     */
    static setVersion(product, version) {
        product = product.toLowerCase();

        this[product] = {
            version,
            isNewerThan(otherVersion) {
                return otherVersion < version;
            },
            isOlderThan(otherVersion) {
                return otherVersion > version;
            }
        };

        if (!window.bryntum) {
            window.bryntum = {};
        }

        let bundleFor = '';

        // Var productName is only defined in bundles, it is internal to bundle so not available on window. Used to
        // tell importing combinations of grid/scheduler/gantt bundles apart from loading same bundle twice
        try {
            // eslint-disable-next-line no-undef
            bundleFor = productName;
        }
        catch (e) {

        }

        // Set "global" flag to detect bundle being loaded twice
        const globalKey = `${bundleFor}.${product}${version.replace(/\./g, '-')}`;

        if (window.bryntum[globalKey] === true) {
            if (window.parent.Siesta) {
                window.BUNDLE_EXCEPTION = true;
            }
            else {
                throw new Error('Bundle included twice, check cache-busters and file types (.js)');
            }
        }
        else {
            window.bryntum[globalKey] = true;
        }
    }

    /**
     * Get (previously set) version for specified product
     * @private
     * @param {String} product
     */
    static getVersion(product) {
        product = product.toLowerCase();

        if (!this[product])  {
            throw new Error('No version specified');
        }

        return this[product].version;
    }
}
