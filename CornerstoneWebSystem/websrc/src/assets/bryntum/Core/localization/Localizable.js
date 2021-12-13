import Base from '../Base.js';
import LocaleManager from './LocaleManager.js';

/**
 * @module Core/localization/Localizable
 */

/**
 * Mixin that simplifies localization of strings in a class.
 *
 * ```
 * // Get localized string
 * grid.L('sort')
 * ```
 *
 * @mixin
 */
export default Target => class Localizable extends (Target || Base) {
    static get defaultConfig() {
        return {
            localeClass           : null,
            localizableProperties : []
        };
    }

    static get inTextLocaleRegExp() {
        return /L{([^}]+)}/g;
    }

    // In case it's wrapped in 'L{text}'
    static parseText(text) {
        const match = this.inTextLocaleRegExp.exec(text);
        return match ? match[1] : text;
    }

    construct(config = {}, ...args) {
        const me = this;

        // Base class applies configs.
        super.construct(config, ...args);

        LocaleManager.on('locale', me.updateLocalization, me);

        me.updateLocalization();
    }

    get localeClass() {
        // Trying to extract localeClass from a parent widget. null by default
        return this._localeClass || (this.parent && this.parent.localeClass) || null;
    }

    set localeClass(key) {
        this._localeClass = key;
    }

    localizeProperty(name) {
        const me = this;

        // need to translate string properties only
        if (typeof me[name] === 'string') {

            me.originalLocales = me.originalLocales || {};

            // Need to save original values since they will be overridden by localizable equivalents
            me.originalLocales[name] = me.originalLocales[name] || me[name];

            const text = me.originalLocales[name];

            // Doing localization from the original values
            if (text) {
                me[name] = me.L(text);
            }
        }
    }

    updateLocalization() {
        const me = this;

        me.localizableProperties && me.localizableProperties.forEach(me.localizeProperty, me);
    }

    static getTranslation(text, templateData, cls) {
        const { locale } = LocaleManager;

        let result, name;

        if (locale) {
            text = this.parseText(text);

            // traverse prototypes to find localization
            while (cls && (name = typeof cls === 'string' ? cls : cls.$name || cls.name)) {
                const classTranslation = locale[name];

                if (classTranslation && text in classTranslation) {
                    const translation = classTranslation[text];

                    result = typeof translation === 'function' && templateData !== undefined ? translation(templateData) : translation;

                    // break the loop
                    break;
                }
                else if (typeof cls === 'string') {
                    break;
                }
                else {
                    cls = Object.getPrototypeOf(cls);
                }
            }
        }

        return result;
    }

    /**
     * Builds `localizableProperties` config value for the class instance by iterating
     * though the class ancestor classes and concatenating their `localizableProperties` values.
     * @param {Object} config The clas instance configuration object.
     * @private
     */
    buildLocalizableProperties(config) {
        const localizableProperties = [];

        if (config.localizableProperties) {
            localizableProperties.push(...config.localizableProperties);
        }

        // collect localizable properties defined on subclasses
        for (let cls = this.constructor.superclass; cls && cls !== Base; cls = cls.superclass) {
            const superConfigDefaultConfig = cls.getConfigDescriptor(true).defaultConfig;

            if (superConfigDefaultConfig && superConfigDefaultConfig.localizableProperties) {
                localizableProperties.push(
                    ...(superConfigDefaultConfig.localizableProperties.filter(i => localizableProperties.indexOf(i) < 0))
                );
            }
        }

        return localizableProperties;
    }

    startConfigure(config) {
        config.localizableProperties = this.buildLocalizableProperties(config);

        super.startConfigure(config);
    }

    /**
     * Get localized string, returns value of `text` if no localized string found
     * @param {String} text String key
     * @param {Object} [templateData] Data to supply to template if localized string is one
     * @returns {String}
     */
    static L(text, templateData = undefined, ...translationProviders) {

        // In case this static method is called directly third argument is not provided
        // just fallback to searching locales for the class itself
        if (!translationProviders.length) {
            translationProviders = [this];
        }

        let translation;

        translationProviders.some(cls => {
            translation = this.getTranslation(text, templateData, cls);
            return translation !== undefined;
        });

        return translation === undefined ? text : translation;
    }

    /**
     * Convenience function that can be called directly on the class that mixes Localizable in
     * @param {String} text String key
     * @param {Object} [templateData] Data to supply to template if localized string is one
     * @returns {String}
     * @category Misc
     * @example
     * button.text = grid.L('group');
     */
    L(text, templateData = undefined) {
        // If we have a different class set as translations provider
        // pass it first and use the class being translated as a fallback provider
        if (this.localeClass && this.localeClass !== this.constructor) {
            return Localizable.L(text, templateData, this.localeClass, this.constructor);
        }
        else {
            return Localizable.L(text, templateData, this.constructor);
        }
    }

    /**
     * Get the global LocaleManager
     * @returns {Core.localization.LocaleManager}
     * @category Misc
     */
    get localeManager() {
        return LocaleManager;
    }
};
