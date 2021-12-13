import Delayable from '../mixin/Delayable.js';
import DomHelper from '../helper/DomHelper.js';

//TODO: add loader
/**
 * @module Core/widget/Mask
 */

/**
 * Masks an element (document.body if no element specified). Call static methods for ease of use or make instance for
 * reusability.
 *
 * @example
 * Mask.mask('hello');
 * Mask.unmask();
 *
 * @externalexample widget/Mask.js
 */
export default class Mask extends Delayable() {
    //region Config
    static get $name() {
        return 'Mask';
    }

    static get defaultConfig() {
        return {
            /**
             * Mode: bright, bright-blur, dark or dark-blur
             * @config {String}
             * @default
             */
            mode : 'dark',

            /**
             * The icon to show next to the text. Defaults to showing a spinner
             * @config {String}
             * @default
             */
            icon : 'b-icon b-icon-spinner',

            /**
             * The text (or HTML) to show in mask
             * @config {String}
             */
            text : null,

            type : null,

            progress : null,

            maxProgress : null,

            useTransition : false
        };
    }

    //endregion

    //region Init

    construct(config) {
        super.construct(config);

        if (!this.element) {
            this.element = document.body;
        }

        this.show();
    }

    doDestroy() {
        const me = this;

        if (me.maskElement) {
            me.maskElement.remove();
            me.maskElement = null;
            if (me.mode.endsWith('blur')) {
                DomHelper.forEachChild(me.element, child => {
                    child.classList.remove(`b-masked-${me.mode}`);
                });
            }
            me.elementMask = null;
        }
        super.doDestroy();
    }

    get typeName() {
        return typeof this.type === 'string' ? this.type.trim() : '';
    }

    get maskName(){
        return `mask${this.typeName}`;
    }

    get elementMask() {
        return this.element && this.element[this.maskName];
    }

    set elementMask(value) {
        if (this.element) {
            this.element[this.maskName] = value;
        }
    }

    /**
     * Creates mask element
     * @private
     */
    create() {
        const me = this;
        me.elementMask = me;

        Object.assign(me, DomHelper.createElement({
            parent    : me.element,
            className : {
                'b-mask'                : 1,
                'b-mask-type'                : 1,
                'b-widget'              : 1,
                [`b-mask-${me.mode}`]   : 1,
                'b-progress'            : me.maxProgress,
                'b-prevent-transitions' : !me.useTransition
            },
            reference : 'maskElement',
            children  : [
                {
                    className : 'b-mask-content',
                    reference : 'maskContent',
                    children  : [
                        me.maxProgress ? {
                            className : 'b-mask-progress-bar',
                            reference : 'progressElement',
                            style     : {
                                width : (Math.round(me.progress / me.maxProgress * 100)) + '%'
                            }
                        } : null,
                        {
                            className : 'b-mask-text',
                            reference : 'maskText'
                        }
                    ]
                }
            ]
        }));

        me.text = me._text;
    }

    //endregion

    //region Static

    /**
     * Shows a mask with the specified message
     * @param {String|Object} text Message
     * @param {HTMLElement} element Element to mask
     * @returns {Core.widget.Mask}
     */
    static mask(text, element = document.body) {
        return new Mask(typeof text !== 'string' ? Object.assign({ element }, text) : {
            element,
            text
        });
    }

    /**
     * Unmask
     * @param {HTMLElement} element Element to unmask
     * @returns {Promise} A promise which is resolved when the mask is gone
     */
    static unmask(element = document.body) {
        return element.mask && element.mask.close();
    }

    //endregion

    //region Mask content

    /**
     * Gets or sets the text displayed in the mask
     * @property {String}
     */
    set text(txt) {
        let me = this;

        me._text = txt || '';

        if (me.maskText) {
            me.maskText.innerHTML = `<i class="b-mask-icon ${me.icon}"></i>${me._text}`;
        }
    }

    get text() {
        return this._text;
    }

    set progress(progress) {
        this._progress = progress;

        if (this.progressElement && progress != null) {
            this.progressElement.style.width = (Math.round(progress / this._maxProgress * 100)) + '%';
        }
    }

    get progress() {
        return this._progress;
    }

    set maxProgress(max) {
        this._maxProgress = max;
        this.progress = this.progress;
    }

    get maxProgress() {
        return this._maxProgress;
    }

    //endregion

    //region Show & hide

    /**
     * Show mask
     */
    show() {
        const me = this;

        if (me.hasTimeout('hide')) {
            // TODO: Consider this behaviour, should showing a mask which is not fully hidden resolve?
            me.resolve();
            me.clearTimeout('hide');
        }

        if (!me.elementMask) {
            me.create();
        }
        const { element, maskElement } = me;

        if (!maskElement) return; // already masked by other instance, only allowing one

        element.classList.add('b-masked');
        maskElement.classList.add('b-visible');
        maskElement.classList.remove('b-hidden');
        me.shown = true;

        // blur has to blur child elements
        if (me.mode.endsWith('blur')) {
            DomHelper.forEachChild(element, child => {
                if (child !== maskElement) {
                    child.classList.add(`b-masked-${me.mode}`);
                }
            });
        }
    }

    /**
     * Hide mask
     * @returns {Promise} A promise which is resolved when the mask is hidden, or immediately if already hidden
     */
    hide() {
        const me = this,
            { element, maskElement } = me;

        return new Promise(resolve => {
            if (me.shown) {
                me.shown = false;
                maskElement.classList.remove('b-visible');
                maskElement.classList.add('b-hidden');
                element.classList.remove('b-masked');

                if (me.mode.endsWith('blur')) {
                    DomHelper.forEachChild(element, child => {
                        if (child !== maskElement) {
                            child.classList.remove(`b-masked-${me.mode}`);
                        }
                    });
                }

                // TODO: use AnimationHelper when available
                me.resolve = resolve;
                me.setTimeout(() => resolve(), 500, 'hide');
            }
            else {
                resolve();
            }
        });
    }

    /**
     * Close mask (removes it)
     * @returns {Promise} A promise which is resolved when the mask is closed
     */
    close() {
        return new Promise(resolve => {
            this.hide().then(() => {
                this.destroy();
                resolve();
            });
        });
    }

    //endregion
}
