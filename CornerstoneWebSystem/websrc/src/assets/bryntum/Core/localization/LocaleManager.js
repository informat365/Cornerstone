import Base from '../Base.js';
import AjaxHelper from '../helper/AjaxHelper.js';
import Events from '../mixin/Events.js';

/**
 * Singleton that handles switching locale. Locales can be included on page with `<script type="module">` tags or
 * loaded using ajax. When using script tags the first locale loaded is used per default, if another should be used as
 * default specify it on the script tag for the grid (see example below).
 *
 * ```
 * // Using Ecma 6 modules
 * &lt;script type="module" src="./Core/localization/SvSE.js"&gt;
 *
 * // Specify default when using scripts
 * &lt;script src="build/locales/grid-locale-SvSE.js"&gt;
 * &lt;script data-default-locale="En" src="build/grid-all.js"&gt;
 *
 * import LocaleManager from 'Core/localization/LocaleManager.js';
 * LocaleManager.applyLocale('SvSE');
 * ```
 *
 * @demo Grid/localization
 */
class LocaleManager extends Events(Base) {
    static get defaultConfig() {
        return {
            locales : {}
        };
    }

    construct(...args) {
        const me = this;

        super.construct(...args);

        // currentScript not working in IE11
        // if (document.currentScript && document.currentScript.dataset.defaultLocale) {
        //     me.defaultLocaleName = document.currentScript.dataset.defaultLocale;
        // }

        let scriptTag = document.querySelector('script[data-default-locale]');
        if (scriptTag) {
            me.defaultLocaleName = scriptTag.dataset.defaultLocale;
        }

        if (window.bryntum && window.bryntum.locales) {
            Object.keys(window.bryntum.locales).forEach(localeName => {
                // keeping this check in case some client tries to use an old locale
                if (!localeName.startsWith('moment')) {
                    let locale = window.bryntum.locales[localeName];
                    if (locale.extends) {
                        me.extendLocale(locale.extends, locale);
                    }
                    else {
                        me.registerLocale(locale.localeName, { desc : locale.localeDesc, locale : locale });
                    }
                }
            });

            if (!me.locale) {
                // English locale is built in, no need to apply it here since it will be applied anyway
                if (me.defaultLocaleName !== 'En') {
                    // No locale applied, use default or first found
                    me.applyLocale(me.defaultLocaleName || Object.keys(me.locales)[0]);
                }
            }
        }
    }

    set locales(localeConfigs) {
        this._locales = localeConfigs;
    }

    get locales() {
        return this._locales;
    }

    /**
     * Get/set currently used locale. Set a name of a locale to have it applied, or give a locale configuration to
     * have it registered and then applied
     * @property {String|Object}
     */
    set locale(locale) {
        if (typeof locale === 'string') {
            this.applyLocale(locale);
        }
        else {
            if (!locale.locale) {
                locale = {
                    locale,
                    localeName : locale.localeName || 'custom'
                };
            }

            this.registerLocale(locale.localeName, locale);
            this.applyLocale(locale.localeName);
        }
    }

    get locale() {
        return this._locale;
    }

    /**
     * Register a locale to make it available for applying
     * @param name
     * @param config
     */
    registerLocale(name, config) {
        const me        = this,
            isDefault = me.defaultLocaleName === name,
            isCurrent = me.locale && me.locale.localeName === name,
            isFirst   = Object.keys(me.locales).length === 0;

        me.locales[name] = config;

        // if no default locale specified, use the first one. otherwise apply the default when it is registered
        // also reapply if current locale is registered again (first grid, then scheduler etc).
        if (isDefault || (!me.defaultLocaleName && (isFirst || isCurrent))) {
            me.internalApplyLocale(config);
        }
    }

    /**
     * Extend an already loaded locale to add additional translations
     * @param name
     * @param config
     */
    extendLocale(name, config) {
        const locale = this.locales[name];
        if (!locale) return false;

        Object.keys(config).forEach(cls => {
            if (!locale.locale[cls]) {
                locale.locale[cls] = config[cls];
            }
            else {
                Object.assign(locale.locale[cls], config[cls]);
            }
        });

        delete locale.locale.extends;

        return true;
    }

    internalApplyLocale(localeConfig) {
        const me = this;
        me._locale = localeConfig.locale;
        me.trigger('locale', localeConfig);
    }

    /**
     * Apply a locale. Locale must be defined in LocaleManager#locales. If it is not loaded it will be loaded using ajax
     * and then applied.
     * @param {String} name Name of locale to apply (for example En or SvsE)
     * @returns {boolean|Promise}
     */
    applyLocale(name, forceApply = false, ignoreError = false) {
        const me           = this,
            localeConfig = me.locales[name];

        if (localeConfig && localeConfig.locale && me._locale === localeConfig.locale && !forceApply) {
            // no need to apply same locale again
            return true;
        }

        // ignoreError is used in examples where one example might have defined a locale not available in another

        if (!localeConfig) {
            if (ignoreError) return true;

            throw new Error(`Locale ${name} not registered`);
        }

        function internalApply() {
            me.internalApplyLocale(localeConfig);
        }

        if (!localeConfig.locale) {
            return new Promise((resolve, reject) => {
                me.loadLocale(localeConfig.path).then(response => {
                    //const text      = response.substring(response.indexOf('{'), response.lastIndexOf('};') + 1),
                    //      getLocale = new Function('return ' + text);
                    response.text().then(text => {
                        // eslint-disable-next-line no-new-func
                        const parseLocale = new Function(text);
                        parseLocale();

                        localeConfig.locale = window.bryntum.locales[name];
                        internalApply();
                        resolve(localeConfig);
                    });
                }).catch(response => reject(response));
            });
        }

        internalApply();
        return true;
    }

    /**
     * Loads a locale using ajax
     * @private
     * @param {String} path Path to locale file
     * @returns {Promise}
     */
    loadLocale(path) {
        return AjaxHelper.get(path);
    }
}

let LocaleManagerSingelton = new LocaleManager();

export default LocaleManagerSingelton;
