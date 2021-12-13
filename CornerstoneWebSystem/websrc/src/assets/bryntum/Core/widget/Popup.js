import Panel from './Panel.js';
import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';
import BrowserHelper from '../helper/BrowserHelper.js';
import EventHelper from '../helper/EventHelper.js';
import GlobalEvents from '../GlobalEvents.js';
import DomHelper from '../helper/DomHelper.js';

/**
 * @module Core/widget/Popup
 */

/**
 * Popup widget, used as base class for Menu but can also be used as is to contain widgets or html
 *
 * @extends Core/widget/Panel
 *
 * @example
 * let popup = new Popup({
 *   forElement : document.querySelector('button'),
 *   widgets    : [
 *     { type : 'text', placeholder: 'Text' },
 *     { type: 'button', text: 'Okey', style: 'width: 100%', color: 'orange'}
 *   ]
 * });
 *
 * @classType popup
 * @externalexample widget/Popup.js
 */
export default class Popup extends Panel {
    //region Config
    static get $name() {
        return 'Popup';
    }

    static get defaultConfig() {
        return {
            autoShow : true,

            /**
             * By default a Popup is transient, and will {@link #function-close} when the user clicks or
             * taps outside its owned widgets and when focus moves outside its owned widgets.
             *
             * **Note**: {@link #config-modal Modal} popups won't {@link #function-close} when focus moves outside even if autoClose is `true`.
             *
             * Configure as `false` to make a Popup non-transient.
             * @config {Boolean}
             * @default
             */
            autoClose : true,

            /**
             * Show popup when user clicks the element that it is anchored to. Cannot be combined with showOnHover
             * @config {Boolean}
             * @default
             */
            showOnClick : false,

            forElement : null,

            monitorResize : true,

            floating : true,
            hidden   : true,

            axisLock : true, // Flip edges if align violates constrainTo

            hideAnimation : BrowserHelper.isIE11 ? null : {
                opacity : {
                    from     : 1,
                    to       : 0,
                    duration : '.3s',
                    delay    : '0s'
                }
            },

            showAnimation : BrowserHelper.isIE11 ? null : {
                opacity : {
                    from     : 0,
                    to       : 1,
                    duration : '.4s',
                    delay    : '0s'
                }
            },

            /**
             * The action to take when calling the {@link #function-close} method.
             * By default, the popup is hidden.
             *
             * This may be set to `'destroy'` to destroy the popup upon close.
             * @config {String}
             * @default
             */
            closeAction : 'hide',

            /**
             * By default, tabbing within a Popup is circular - that is it does not exit.
             * Configure this as `false` to allow tabbing out of the Popup.
             * @config {Boolean}
             * @default
             */
            trapFocus : true,

            /**
             * By default a Popup is focused when it is shown.
             * Configure this as `false` to prevent automatic focus on show.
             * @config {Boolean}
             * @default
             */
            focusOnToFront : true,

            /**
             * Show a tool in the header to close this Popup, and allow `ESC` close it.
             * The tool is available in the {@link #property-tools} object
             * under the name `close`. It uses the CSS class `b-popup-close` to apply a
             * default close icon. This may be customized with your own CSS rules.
             * @config {Boolean}
             * @default
             */
            closable : null,

            /**
             * Optionally show an opaque mask below this Popup when shown.
             * Configure this as `true` to show the mask.
             *
             * When a Popup is modal, it defaults to being {@link Core.widget.Widget#config-centered centered}.
             * Also it won't {@link #function-close} when focus moves outside even if {@link #config-autoClose} is `true`.
             *
             * May also be an object containing the following properties:
             * * `closeOnMaskTap` Specify as `true` to {@link #function-close} when mask is tapped.
             * The default action is to focus the popup.
             *
             * Usage:
             * ```javascript
             * new Popup({
             *     title  : 'I am modal',
             *     modal  : {
             *         closeOnMaskTap : true
             *     },
             *     height : 100,
             *     width  : 200
             * });
             * ```
             *
             * @config {Boolean}
             * @default
             */
            modal : null
        };
    }

    //endregion

    startConfigure(config) {
        // Read the closable config which will force evaluation of tools
        // to include close tool.
        this._thisIsAUsedExpression(this.closable);

        super.startConfigure(config);
    }

    //region Init & destroy

    finalizeInit() {
        const me = this;

        me.anchoredTo        = me.forElement;
        me.initialAnchor     = me.anchor;

        if (me.forElement && me.showOnClick) {
            // disable autoShow if not enabled by config
            if (!me.initialConfig.autoShow) {
                me.autoShow = false;
            }
            EventHelper.on({
                element : me.forElement,
                click   : 'onElementUserAction',
                thisObj : me
            });
        }

        super.finalizeInit();

        if (me.autoShow) {
            if (me.autoShow === true) {
                me.show();
            }
            else {
                me.setTimeout(() => me.show(), me.autoShow);
            }
        }
    }

    doDestroy() {
        this.syncModalMask();
        super.doDestroy();
    }

    //endregion

    //region Show/hide

    /**
     * Performs the configured {@link #config-closeAction} upon this popup.
     * By default, the popup hides. The {@link #config-closeAction} may be
     * configured as `'destroy'`.
     * @fires beforeclose If popup is not hidden
     */
    close() {
        /**
         * Fired when the {@link #function-close} method is called and the popup is not hidden.
         * May be vetoed by returning `false` from a handler.
         * @event beforeClose
         * @param {Core.widget.Popup} source - This Popup
         */
        if ((!this._hidden && this.trigger('beforeClose') !== false) ||
            // we should destroy it even if it's hidden just omit beforeclose event
            (this._hidden && this.closeAction == 'destroy')) {
            return this[this.closeAction]();
        }
    }

    //endregion

    //region Events

    onElementResize(resizedElement, lastRect, myRect) {
        const me = this,
            { lastAlignSpec } = me;

        // If this Popup changes size while we are aligned and we are aligned to
        // a target (not a position), then we might need to realign.
        if (me.isVisible && lastAlignSpec && lastAlignSpec.target) {
            const heightChange = !lastRect || myRect.height !== lastRect.height,
                widthChange = !lastRect || myRect.width !== lastRect.width;

            // Only realign if:
            // the height has changed and we are not aligned below, or
            // the width has changed and we are not aligned to the right.
            if ((heightChange && lastAlignSpec.zone !== 2) || (widthChange && lastAlignSpec.zone !== 1)) {
                // Must move to next AF because in Chrome, the resize monitor might fire
                // before the element is painted and the anchor color matching
                // scheme cannot work in that case.
                me.requestAnimationFrame(() => me.realign());
            }
        }

        super.onElementResize(resizedElement);
    }

    onInternalKeyDown(event) {
        // close on escape key
        if (event.key === 'Escape') {
            event.stopImmediatePropagation();
            this.close(true);
        }
    }

    onDocumentMouseDown({ event }) {
        const me = this;

        if (me.modal && event.target === Popup.modalMask) {
            event.preventDefault();
            if (me.modal.closeOnMaskTap) {
                me.close();
            }
            else if (!me.containsFocus) {
                me.focus();
            }
        }
        // in case of outside click and if popup is focused, focusout will trigger closing
        else if (!me.owns(event.target) && me.autoClose && !me.containsFocus) {
            me.close();
        }

        // Focus moves unrelated to where the user's attention is upon this gesture.
        // Go into the keyboard mode where the focused widget gets a rendition so that
        // it is obvious where focus now is.
        // Must jump over EventHelper's global mousedown listener which will remove this class.
        me.setTimeout(() => document.body.classList.add('b-using-keyboard'), 0);
    }

    get isTopModal() {
        return DomHelper.isVisible(Popup.modalMask) && this.element.previousElementSibling === Popup.modalMask;
    }

    onFocusIn(e) {
        const activeEl = document.activeElement;

        super.onFocusIn(e);

        // No event handler has moved focus, and target is outermost el
        // then delegate to the focusElement which for a Container
        // is found by finding the first visible, focusable descendant widget.
        if (document.activeElement === activeEl && e.target === this.element) {
            this.focus();
        }
    }

    onFocusOut(e) {
        if (!this.modal && this.autoClose) {
            this.close();
        }

        super.onFocusOut(e);
    }

    onShow() {
        const me = this;

        if (me.autoClose && !me.mouseDownRemover) {
            me.mouseDownRemover = GlobalEvents.on({
                globaltap : 'onDocumentMouseDown',
                thisObj   : me
            });
        }

        // TODO: It's the floating "toFront" operation that should handle
        // focusing based on config focusOnToFront.
        if (me.focusOnToFront) {
            me.focus();
        }

        super.onShow && super.onShow();

        // Insert the modal mask below this Popup if needed
        me.syncModalMask();
    }

    syncModalMask() {
        const
            me            = this,
            { modal }     = me,
            { modalMask } = Popup;

        if (modal && me.isVisible) {
            // If we have not been explicitly positioned, a modal is centered.
            if (!me._x && !me._y) {
                me.centered = true;
            }
            modalMask.classList.remove('b-hide-display');
            modalMask.parentNode.insertBefore(modalMask, me.element);
            me.element.classList.add('b-modal');
        }
        else {
            me.element.classList.remove('b-modal');

            const remaningModals = me.constructor.floatRoot.querySelectorAll('.b-modal');

            // If there are any other visible modals, drop the mask to just below the new topmost
            if (remaningModals.length) {
                const topModal = remaningModals[remaningModals.length - 1];

                modalMask.classList.remove('b-hide-display');
                modalMask.parentNode.insertBefore(modalMask, topModal);

            }
            else {
                modalMask.classList.add('b-hide-display');
            }
        }
    }

    onHide() {
        const me = this;

        if (me.mouseDownRemover) {
            me.mouseDownRemover();
            me.mouseDownRemover = null;
        }

        super.onHide && super.onHide();

        // Insert the modal mask below the topmost Popup if needed, else hide it
        me.syncModalMask();
    }

    onElementUserAction() {
        this.show();
    }

    //endregion

    set closable(closable) {
        this._closable = closable;
        if (!this.tools) {
            this.tools = {};
        }
    }

    get closable() {
        return this._closable;
    }

    set tools(tools) {
        const me = this;

        if (me.closable) {
            (tools || (tools = {})).close = {
                cls     : 'b-popup-close',
                handler : 'close',
                weight  : -1000
            };
        }
        super.tools = tools;
    }

    get tools() {
        return super.tools;
    }

    static get modalMask() {
        const me = this;
        if (!me._modalMask) {
            me._modalMask = DomHelper.createElement({
                className : 'b-modal-mask b-hide-display',
                parent    : me.floatRoot
            });
        }
        else if (!me.floatRoot.contains(me._modalMask)) {
            // Reattach modalMask if it was detached
            me.floatRoot.appendChild(me._modalMask);
        }

        return me._modalMask;
    }
}

BryntumWidgetAdapterRegister.register('popup', Popup);
