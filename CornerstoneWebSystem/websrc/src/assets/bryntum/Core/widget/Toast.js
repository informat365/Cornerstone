import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';

/**
 * @module Core/widget/Toast
 */

import Widget from './Widget.js';
import DomHelper from '../helper/DomHelper.js';
import EventHelper from '../helper/EventHelper.js';

/**
 * Basic toast. Toasts are stacked on top of each other
 * @example
 * // simplest possible
 * Toast.show('Just toasting');
 *
 * // with config
 * Toast.show({
 *   html: 'Well toasted',
 *   showProgress: false
 * });
 *
 * // as instance (instance is also returned from Toast.show()
 * let toast = new Toast({
 *   html: 'Not going away',
 *   timeout: 0
 * });
 *
 * toast.show();
 *
 * @classType toast
 * @externalexample widget/Toast.js
 */
export default class Toast extends Widget {

    static get $name() {
        return 'Toast';
    }

    static get defaultConfig() {
        return {
            floating : true,

            /**
             * Timeout (in ms) until the toast is automatically dismissed. Set to 0 to never hide.
             * @config {Number}
             * @default
             */
            timeout : 2500,

            /**
             * Show a progress bar indicating the time remaining until the toast is dismissed.
             * @config {Boolean}
             * @default
             */
            showProgress : true,

            /**
             * Toast color (should have match in toast.scss or your custom styling).
             * Valid values in Bryntum themes are:
             * * b-amber
             * * b-blue
             * * b-dark-gray
             * * b-deep-orange
             * * b-gray
             * * b-green
             * * b-indigo
             * * b-lime
             * * b-light-gray
             * * b-light-green
             * * b-orange
             * * b-purple
             * * b-red
             * * b-teal
             * * b-white
             * * b-yellow
             *
             * ```
             * new Toast({
             *    color : 'b-blue'
             * });
             * ```
             *
             * @config {String}
             */
            color : null,

            bottomMargin : 20
        };
    }

    doDestroy() {
        const index = Toast.toasts.indexOf(this);

        if (index > -1) {
            Toast.toasts.splice(index, 1);
        }

        super.doDestroy();
    }

    // Toasts must not create their element until show.
    set element(element) {}

    get element() {
        // Toasts must not create their element until show.
        if (!this._element && !this.isConfiguring) {
            const
                me       = this,
                topToast = Toast.toasts.length && Toast.toasts[0].element,
                bottom   = topToast ? topToast._nextBottom : me.bottomMargin,
                element  = super.element = DomHelper.createElement({
                    parent   : me.appendTo || Widget.floatRoot,
                    children : me.showProgress ? [{
                        className : 'b-toast-progress',
                        style     : `animation-duration:${me.timeout / 1000}s;`
                    }] : null,
                    html      : me._html,
                    className : me.color || '',
                    style     : me._style || ''
                });

            EventHelper.on({
                element,
                click   : 'hide',
                thisObj : me
            });

            Toast.toasts.unshift(me);

            element._nextBottom = bottom + element.offsetHeight + me.bottomMargin;

            // Transitioned style props must be set dynamically
            element.style.cssText += `;bottom:${bottom}px;transform:translateY(0)`;
        }
        return super.element;
    }

    /**
     * Show the toast
     */
    show() {
        super.show();

        if (this.timeout > 0) {
            this.hideTimeout = this.setTimeout('hide', this.timeout);
        }
    }

    /**
     * Hide the toast
     */
    hide() {
        const me = this;
        if (!me.destroyTimeout) {
            me.element.classList.add('b-toast-hide');
            me.element.style.bottom = '';
            me.animationTimeout = me.setTimeout('destroy', 200);
        }
    }

    /**
     * Hide all visible toasts
     */
    static hideAll() {
        Toast.toasts.reverse().forEach(toast => toast.hide());
    }

    /**
     * Easiest way to show a toast
     * @param {String|Object} msgOrConfig Message or toast config object
     * @returns {Core.widget.Toast}
     * @example
     * Toast.show('Hi');
     * @example
     * Toast.show({
     *   html   : 'Read quickly, please',
     *   timeout: 1000
     * });
     */
    static show(msgOrConfig) {
        const toast = new Toast((typeof msgOrConfig === 'string') ? { html : msgOrConfig } : msgOrConfig);
        toast.show();
        return toast;
    }
}

Toast.toasts = [];

BryntumWidgetAdapterRegister.register('toast', Toast);
