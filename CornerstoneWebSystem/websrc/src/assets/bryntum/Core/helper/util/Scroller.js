import Base from '../../Base.js';
import Rectangle from './Rectangle.js';
import Point from './Point.js'; // eslint-disable-line
import Delayable from '../../mixin/Delayable.js';
import Events from '../../mixin/Events.js';
import DomHelper from '../DomHelper.js';
import FunctionHelper from '../FunctionHelper.js';
import Collection from '../../util/Collection.js';
import IdHelper from '../IdHelper.js';
import BrowserHelper from '../BrowserHelper.js';
import EventHelper from '../EventHelper.js';

/**
 * @module Core/helper/util/Scroller
 */

const scrollLiterals = {
        true            : 'auto',
        false           : 'hidden',
        'hidden-scroll' : 'auto'
    },
    scrollerCls = 'b-widget-scroller',
    defaultScrollOptions = {
        block : 'nearest'
    },
    immediatePromise = new Promise((resolve) => resolve()),
    scrollPromise = (element) => new Promise(resolve => EventHelper.on({
        element,
        scroll : resolve,
        once   : true
    })),
    xAxis = {
        x : 1
    };

/**
 * Encapsulates scroll functionality for a Widget. All requests for scrolling and scrolling information
 * must go through a Widget's {@link Core.widget.Widget#config-scrollable} property.
 * @mixes Core/mixin/Events
 * @mixes Core/mixin/Delayable
 * @extends Core/Base
 */
export default class Scroller extends Delayable(Events(Base)) {
    static get defaultConfig() {
        return {
            /**
             * The element which is to scroll.
             * @config {HTMLElement}
             */
            element : null,

            /**
             * How to handle overflowing in the `X` axis.
             * May be:
             * * `'auto'`
             * * `'visible'`
             * * `'hidden'`
             * * `'scroll'`
             * * `'hidden-scroll'` Meaning scrollable from the UI but with no scrollbar,
             * for example a grid header. Only on platforms which support this feature.
             * * `true` - meaning `'auto'`
             * * `false` - meaning `'hidden'`
             * @config {String/Boolean}
             */
            overflowX : null,

            /**
             * How to handle overflowing in the `Y` axis.
             * May be:
             * * `'auto'`
             * * `'visible'`
             * * `'hidden'`
             * * `'scroll'`
             * * `'hidden-scroll'` Meaning scrollable from the UI but with no scrollbar.
             * Only on platforms which support this feature.
             * * `true` - meaning `'auto'`
             * * `false` - meaning `'hidden'`
             * @config {String/Boolean}
             */
            overflowY : null,

            /**
             * If configured as `true`, the {@link #config-element} is not scrolled
             * but is translated using CSS transform when controlled by this class's API.
             * Scroll events are fired when the element is translated.
             * @default
             * @config {Boolean}
             */
            translate : null,

            _x : 0,
            _y : 0
        };
    }

    /**
     * Fired when scrolling happens on this Scroller's element. The event object is a native `scroll` event
     * with the described extra properties injected.
     * @event scroll
     * @param {Core.widget.Widget} widget The owning Widget which has been scrolled.
     * @param {Core.helper.util.Scroller} source This Scroller
     */

    /**
     * Fired when scrolling finished on this Scroller's element. The event object is the last native `scroll` event
     * fires by the element with the described extra properties injected.
     * @event scrollend
     * @param {Core.widget.Widget} widget The owning Widget which has been scrolled.
     * @param {Core.helper.util.Scroller} source This Scroller
     */

    /**
     * Partners this Scroller with the passed scroller in order to sync the scrolling position in the passed axes
     * @param {Core.helper.util.Scroller} otherScroller
     * @param {String|Object} [axes='x'] `'x'` or `'y'` or `{x: true/false, y: true/false}` axes to sync
     */
    addPartner(otherScroller, axes = xAxis) {
        //<debug>
        if (!(otherScroller instanceof Scroller)) {
            throw new Error('Scroller partner must be another Scroller');
        }
        //</debug>
        if (typeof axes === 'string') {
            axes = {
                [axes] : 1
            };
        }

        (this.partners || (this.partners = new Collection())).add({
            id       : otherScroller.id,
            scroller : otherScroller,
            axes
        });

        // It's a mutual relationship - the other scroller partners with us.
        if (!otherScroller.partners || !otherScroller.partners.includes(this.id)) {
            otherScroller.addPartner(this, axes);
        }
    }

    /**
     * Breaks the link between this Scroller and the passed Scroller set up by the
     * {@link #function-addPartner} method.
     * @param {Core.helper.util.Scroller} otherScroller The Scroller to unlink from.
     */
    removePartner(otherScroller) {
        if (this.partners && this.partners.includes(otherScroller)) {
            this.partners.remove(otherScroller);
            otherScroller.removePartner(this);
        }
    }

    /**
     * Scrolls the passed element or {@link Core.helper.util.Rectangle} into view according to the passed options.
     * @param {HTMLElement|Core.helper.util.Rectangle} element The element or a Rectangle in document space to scroll into view.
     * @param {Object} [options] How to scroll.
     * @param {String} [options.block] How far to scroll the element: `start/end/center/nearest`.
     * @param {Number} [options.edgeOffset] edgeOffset A margin around the element or rectangle to bring into view.
     * @param {Object|Boolean|Number} [options.animate] Set to `true` to animate the scroll by 300ms,
     * or the number of milliseconds to animate over, or an animation config object.
     * @param {Number} [options.animate.duration] The number of milliseconds to animate over.
     * @param {String} [options.animate.easing] The name of an easing function.
     * @param {Boolean} [options.highlight] Set to `true` to highlight the element when it is in view.
     * @param {Boolean} [options.focus] Set to `true` to focus the element when it is in view.
     * @param {Boolean} [options.x] Pass as `false` to disable scrolling in the `X` axis.
     * @param {Boolean} [options.y] Pass as `false` to disable scrolling in the `Y` axis.
     * @returns {Promise} A promise which is resolved when the element has been scrolled into view.
     */
    scrollIntoView(element, options = defaultScrollOptions) {
        const isRectangle = element instanceof Rectangle,
            originalRect = isRectangle ? element : Rectangle.from(element),
            { xDelta, yDelta } = this.getDeltaTo(element, options),
            result = this.scrollBy(xDelta, yDelta, options);

        if (options.highlight || options.focus) {
            result.then(() => {
                if (isRectangle) {
                    element = originalRect.translate(-xDelta, -yDelta);
                }
                if (options.highlight) {
                    DomHelper.highlight(element);
                }
                if (options.focus) {
                    element.focus();
                }
            });
        }
        return result;
    }

    /**
     * Scrolls by the passed deltas according to the passed options.
     * @param {Number} [xDelta=0] How far to scroll in the X axis.
     * @param {Number} [yDelta=0] How far to scroll in the Y axis.
     * @param {Object|Boolean} [options] How to scroll. May be passed as `true` to animate.
     * @param {Boolean} [options.silent] Set to `true` to suspend `scroll` events during scrolling.
     * @param {Object|Boolean|Number} [options.animate] Set to `true` to animate the scroll by 300ms,
     * or the number of milliseconds to animate over, or an animation config object.
     * @param {Number} [options.animate.duration] The number of milliseconds to animate over.
     * @param {String} [options.animate.easing] The name of an easing function.
     * @returns {Promise} A promise which is resolved when the scrolling has fnished.
     */
    scrollBy(xDelta = 0, yDelta = 0, options = defaultScrollOptions) {
        const me      = this,
            animate = (typeof options === 'object') ? options.animate : options,
            absX    = Math.abs(xDelta),
            absY    = Math.abs(yDelta);

        if (me.scrollAnimation) {
            me.scrollAnimation.cancel();
            me.scrollAnimation = null;
        }

        // Only set the flag if there is going to be scrolling done.
        // It is cleared by the scrollEnd handler, so there must be scrolling.
        if (xDelta || yDelta) {
            me.silent = options.silent;
        }

        let duration = animate && (typeof animate === 'number' ? animate : (typeof animate.duration === 'number' ? animate.duration : 300));

        // Only go through animation if there is significant scrolling to do.
        if (duration && (absX > 10 || absY > 10)) {
            const { x, y } = me;
            let lastX = x,
                lastY = y;

            // For small distances, constrain duration
            if (Math.max(absX, absY) < 50) {
                duration = Math.min(duration, 500);
            }

            me.scrollAnimation = FunctionHelper.animate(duration, progress => {
                const isEnd = progress === 1;
                if (xDelta) {
                    // If the user, or another process has changed the position since last time, abort.
                    // Unless called with the force option to proceed regardless.
                    if (me.x !== lastX && !options.force) {
                        return me.scrollAnimation && me.scrollAnimation.cancel();
                    }
                    me.x = Math.max(x + (isEnd ? xDelta : Math.round(xDelta * progress)), 0);
                }
                if (yDelta) {
                    // If the user, or another process has changed the position since last time, abort.
                    // Unless called with the force option to proceed regardless.
                    if (me.y !== lastY && !options.force) {
                        return me.scrollAnimation && me.scrollAnimation.cancel();
                    }
                    me.y = Math.max(y + (isEnd ? yDelta : Math.round(yDelta * progress)), 0);
                }
                // Store actual position from DOM
                lastX = me.x;
                lastY = me.y;
            }, null, animate.easing);
            me.scrollAnimation.then(() => {
                me.scrollAnimation = null;
            });
            return me.scrollAnimation;
        }
        else {
            if (xDelta | yDelta) {
                me.x += xDelta;
                me.y += yDelta;
                return scrollPromise(me.element);
            }
            else {
                return immediatePromise;
            }
        }
    }

    /**
     * Scrolls to the passed position according to the passed options.
     * @param {Number} [toX=0] Where to scroll to in the X axis.
     * @param {Number} [toY=0] Where to scroll to in the Y axis.
     * @param {Object|Boolean} [options] How to scroll. May be passed as `true` to animate.
     * @param {Object|Boolean|Number} [options.animate] Set to `true` to animate the scroll by 300ms,
     * or the number of milliseconds to animate over, or an animation config object.
     * @param {Number} [options.animate.duration] The number of milliseconds to animate over.
     * @param {String} [options.animate.easing] The name of an easing function.
     * @returns {Promise} A promise which is resolved when the scrolling has finished.
     */
    scrollTo(toX, toY, options) {
        const { x, y } = this,
            xDelta = toX == null ? 0 : toX - x,
            yDelta = toY == null ? 0 : toY - y;

        return this.scrollBy(xDelta, yDelta, options);
    }

    doDestroy() {
        const me = this;

        if (me._element) {
            me._element.removeEventListener('scroll', me.scrollHandler);
            me.wheelListenerRemover && me.wheelListenerRemover();
        }
        if (me.scrollAnimation) {
            me.scrollAnimation.cancel();
        }

        me.partners && me.partners.forEach(partner => partner.scroller.removePartner(me));

        super.doDestroy();
    }

    /**
     * Respond to style changes to monitor scroll *when this Scroller is in `translate: true` mode.*
     * @param {Object[]} mutations The ElementMutation records.
     * @private
     */
    onElMutation(mutations) {
        const me = this,
            [x, y] = DomHelper.getTranslateXY(me.element);

        // If the mutation was due to a change in the translateX/Y styles, this is
        // a scroll event, so inform observers and partners
        if (me._x !== -x || me.y !== -y) {
            const scrollEvent = new CustomEvent('scroll', { bubbles : true });

            Object.defineProperty(scrollEvent, 'target', {
                get : () => me.element
            });

            me.onScroll(scrollEvent);
        }
    }

    onScroll(e) {
        const me = this;

        if (!me.widget || !me.widget.isDestroyed) {
            // Don't read the value until we have to. The x & y getters will check this flag
            me.positionDirty = true;
            e.widget = me.widget;

            // If we have the scroll silent flag, do not fire the event.
            if (!me.silent) {
                me.trigger('scroll', e);
            }

            // Keep partners in sync
            me.syncPartners();

            // If this scroll impulse was from a controlling partner, clear that now
            me.controllingPartner = null;

            // Will fire in 100ms, unless another scroll event comes round.
            // In which case execution will be pushed out by another 100ms.
            me.scrollEndHandler(e);
        }
    }

    syncPartners() {
        const me = this;

        // Keep partners in sync
        if (me.partners) {
            me.partners.forEach(({ axes, scroller }) => {
                // Don't feed back to the one who's just told us to scroll here.
                if (scroller !== me.controllingPartner) {
                    scroller.sync(me, axes);
                }
            });
        }
    }

    onScrollEnd(e) {
        if (this.silent) {
            this.silent = false;
        }
        this.trigger('scrollEnd', e);
    }

    /**
     * Returns the xDelta and yDelta values in an object from the current scroll position to the
     * passed element or Rectangle.
     * @param {HTMLElement|Core.helper.util.Rectangle} element The element or a Rectangle to calculate deltas for.
     * @param {Object} [options] How to scroll.
     * @param {String} [options.block] How far to scroll the element: `start/end/center/nearest`.
     * @param {Number} [options.edgeOffset] A margin around the element or rectangle to bring into view.
     * @param {Boolean} [options.x] Pass as `false` to disable scrolling in the `X` axis.
     * @param {Boolean} [options.y] Pass as `false` to disable scrolling in the `Y` axis.
     * @returns {Object} `{ xDelta, yDelta }`
     * @internal
     */
    getDeltaTo(element, options) {
        if (!(element instanceof Rectangle)) {
            element = Rectangle.from(element);
        }

        // We must round when scrolling so that we do not overscroll leading to
        // unwanted mouseovers.
        element.round();

        const me = this,
            block = options.block || 'nearest',
            scrollerRect = me.viewport.round(),
            edgeOffset = options.edgeOffset || 0,
            // Only include the offset round the target is the viewport is big enough to accommodate it.
            xOffset = scrollerRect.width >= element.width + (edgeOffset * 2) ? edgeOffset : 0,
            yOffset = scrollerRect.height >= element.height + (edgeOffset * 2) ? edgeOffset : 0,
            elRect = element.clone().adjust(-xOffset, -yOffset, xOffset, yOffset).constrainTo(new Rectangle(scrollerRect.x - me.x, scrollerRect.y - me.y, me.scrollWidth, me.scrollHeight)),
            targetRect = elRect.clone();

        let xDelta = 0,
            yDelta = 0;

        if (block === 'start') {
            targetRect.moveTo(scrollerRect.x, scrollerRect.y);
            xDelta = elRect.x - targetRect.x;
            yDelta = elRect.y - targetRect.y;
        }
        else if (block === 'end') {
            targetRect.translate(scrollerRect.right - targetRect.right, scrollerRect.bottom - targetRect.bottom);
            xDelta = elRect.x - targetRect.x;
            yDelta = elRect.y - targetRect.y;
        }
        else {
            // Calculate deltas unless the above has done that for non-fitting target
            if (block === 'center') {
                const center = scrollerRect.center;

                targetRect.moveTo(center.x - targetRect.width / 2, center.y - targetRect.height / 2);
                xDelta = xDelta || elRect.x - targetRect.x;
                yDelta = yDelta || elRect.y - targetRect.y;
            }
            // Use "nearest"
            else {
                // Can't fit width in, scroll what is possible into view so that start is visible.
                if (targetRect.width > scrollerRect.width) {
                    xDelta = targetRect.x - scrollerRect.x;
                }
                // If it's *possible* to scroll to nearest x, calculate the delta
                else {
                    if (targetRect.right > scrollerRect.right) {
                        xDelta = targetRect.right - scrollerRect.right;
                    }
                    else if (targetRect.x < scrollerRect.x) {
                        xDelta = targetRect.x - scrollerRect.x;
                    }
                }

                // Can't fit height in, scroll what is possible into view so that start is visible.
                if (targetRect.height > scrollerRect.height) {
                    yDelta = targetRect.y - scrollerRect.y;
                }
                // If it's *possible* to scroll to nearest y, calculate the delta
                else {
                    if (targetRect.bottom > scrollerRect.bottom) {
                        yDelta = targetRect.bottom - scrollerRect.bottom;
                    }
                    else if (targetRect.y < scrollerRect.y) {
                        yDelta = targetRect.y - scrollerRect.y;
                    }
                }
            }
        }

        // Do not allow deltas which would produce -ve scrolling or scrolling past the maxX/Y
        return {
            // When calculating how much delta is necessary to scroll the targetRect to the center
            // constrain that to what is *possible*. If what you are trying to scroll into the
            // center is hard against the right edge of the scroll range, then it cannot scroll
            // to the center, and the result must reflect that even though scroll is self limiting.
            // This is because highlighting the requested "element", if that element is in fact
            // a Rectangle, uses a temporary element placed at the requested region which
            // MUST match where the actual scroll has moved the requested region.
            xDelta : options.x === false ? 0 : Math.max(Math.min(xDelta, me.maxX - me.x), -me.x),
            yDelta : options.y === false ? 0 : Math.max(Math.min(yDelta, me.maxY - me.y), -me.y)
        };
    }

    /**
     * A {@link Core.helper.util.Rectangle Rectangle} describing the bounds of the scrolling viewport.
     * @property {Core.helper.util.Rectangle}
     */
    get viewport() {
        return Rectangle.client(this.element);
    }

    get element() {
        return this._element;
    }

    set element(element) {
        const
            me            = this,
            scrollHandler = me.scrollHandler || (me.scrollHandler = me.onScroll.bind(me));

        if (!me.scrollEndHandler) {
            me.scrollEndHandler = me.buffer(me.onScrollEnd, 100);
        }

        if (me._element) {
            if (me.translate) {
                me.mutationObserver && me.mutationObserver.disconnect(me._element);
            }
            else {
                me._element.removeEventListener('scroll', scrollHandler);
                me._element.classList.remove(scrollerCls);
            }
        }
        me._element = element;

        if (me.translate) {
            if (!me.mutationObserver) {
                me.mutationObserver = new MutationObserver(mutations => me.onElMutation(mutations));
            }
            me.mutationObserver.observe(element, { attributes : true });

            me._x = me._y = 0;
            if (document.contains(element)) {
                const [x, y] = DomHelper.getTranslateXY(element);
                me._x = -x;
                me._y = -y;
            }
        }
        else {
            element.addEventListener('scroll', scrollHandler);
            element.classList.add(scrollerCls);
        }

        // Ensure the overflow configs, which are unable to process themselves
        // in the absence of the element get applied to the newly arrived element.
        me.updateOverflowX(me.overflowX);
        me.updateOverflowY(me.overflowY);
    }

    /**
     * The horizontal scroll position of the widget.
     * @property {Number}
     */
    get x() {
        const me = this,
            { element } = me;

        if (element && me.positionDirty) {
            if (me.translate) {
                const [x, y] = DomHelper.getTranslateXY(element);
                me._x = -x;
                me._y = -y;
            }
            else {
                me._x = element.scrollLeft;
                me._y = element.scrollTop;
            }
            me.positionDirty = false;
        }
        return me._x;
    }

    set x(x) {
        const { element, widget } = this;

        // When element is outside of DOM, this can have no effect
        if (widget && widget.isConfiguring) {
            return;
        }

        this._x = x;

        if (element) {
            this.trigger('scrollStart', { x });

            if (this.translate) {
                DomHelper.setTranslateX(element, -x);
            }
            else {
                element.scrollLeft = x;
            }

            // The scroll position will need to be read before we can return it.
            // Do not read it back now, that would cause a forced synchronous layout.
            this.positionDirty = true;
        }
    }

    sync(controllingPartner, axes) {
        const { x, y } = axes;

        this.controllingPartner = controllingPartner;

        if (x != null) {
            this.x = controllingPartner.x;
        }
        if (y != null) {
            this.y = controllingPartner.y;
        }
    }

    /**
     * The vertical scroll position of the widget.
     * @property {Number}
     */
    get y() {
        const me = this,
            { element } = me;

        if (element && me.positionDirty) {
            if (me.translate) {
                const [x, y] = DomHelper.getTranslateXY(element);
                me._x = -x;
                me._y = -y;
            }
            else {
                me._x = element.scrollLeft;
                me._y = element.scrollTop;
            }
            me.positionDirty = false;
        }
        return me._y;
    }

    set y(y) {
        const { element, widget } = this;

        // When element is outside of DOM, this can have no effect
        if (widget && widget.isConfiguring) {
            return;
        }

        this._y = y;

        if (element) {
            this.trigger('scrollStart', { y });

            if (this.translate) {
                DomHelper.setTranslateY(element, -y);
            }
            else {
                element.scrollTop = y;
            }

            // The scroll position will need to be read before we can return it.
            // Do not read it back now, that would cause a forced synchronous layout.
            this.positionDirty = true;
        }
    }

    /**
     * The maximum `X` scrollable position of the widget.
     * @property {Number}
     * @readonly
     */
    get maxX() {
        return this.scrollWidth - this.clientWidth;
    }

    /**
     * The maximum `Y` scrollable position of the widget.
     * @property {Number}
     * @readonly
     */
    get maxY() {
        return this.scrollHeight - this.clientHeight;
    }

    /**
     * The `overflow-x` setting for the widget. `true` means `'auto'`.
     * @property {Boolean/String}
     */
    get overflowX() {
        return this._overflowX;
    }

    set overflowX(overflowX) {
        this._overflowX = overflowX;

        if (this.element) {
            this.updateOverflowX(overflowX);
        }
    }

    updateOverflowX(overflowX) {
        const { element, translate } = this;

        // Scroll, but without showing scrollbars.
        // For example a grid header. Only works on platforms which
        // support suppression of scrollbars through CSS.
        if (overflowX === 'hidden-scroll' && !translate) {
            element.classList.add('b-hide-scroll');

            // Adds a wheel listener if needed - there is scrollbar width
            // and we don't already have one.
            this.enableWheel();
        }
        if (!translate) {
            element.style.overflowX = scrollLiterals[overflowX] || overflowX;
        }
        this.positionDirty = !this.isConfiguring;
    }

    /**
     * The `overflow-y` setting for the widget. `true` means `'auto'`.
     * @property {Boolean/String}
     */
    get overflowY() {
        return this._overflowY;
    }

    set overflowY(overflowY) {
        this._overflowY = overflowY;

        if (this.element) {
            this.updateOverflowY(overflowY);
        }
    }

    updateOverflowY(overflowY) {
        const { element, translate } = this;

        // Scroll, but without showing scrollbars.
        // For example a grid header. Only works on platforms which
        // support suppression of scrollbars through CSS.
        if (overflowY === 'hidden-scroll' && !translate) {
            element.classList.add('b-hide-scroll');

            // Adds a wheel listener if needed - there is scrollbar width
            // and we don't already have one.
            this.enableWheel();
        }
        if (!translate) {
            element.style.overflowY = scrollLiterals[overflowY] || overflowY;
        }
        this.positionDirty = !this.isConfiguring;
    }

    enableWheel() {
        if (!BrowserHelper.isChrome && !BrowserHelper.isSafari && !this.wheelListenerRemover) {
            this.wheelListenerRemover = EventHelper.on({
                element : this.element,
                wheel   : 'onWheel',
                thisObj : this
            });
        }
    }

    onWheel(e) {
        if (e.deltaX > e.deltaY && this.overflowX !== false) {
            this.x += e.deltaX;
        }
        else if (this.overflowY !== false) {
            this.y += e.deltaY;
        }
    }

    /**
     * The horizontal scroll range of the widget.
     * @property {Number}
     * @readonly
     */
    get scrollWidth() {
        return this.element ? this.element.scrollWidth : 0;
    }

    set scrollWidth(scrollWidth) {
        const me = this;
        let stretcher = me.widthStretcher;

        // "Unsetting" scrollWidth removes the stretcher
        if (stretcher && scrollWidth == null) {
            stretcher.remove();
            me.widthStretcher = null;
        }
        else if (scrollWidth) {
            if (!stretcher) {
                stretcher = me.widthStretcher = DomHelper.createElement({
                    className : 'b-scroller-stretcher'
                });
            }

            stretcher.style.transform = `translateX(${scrollWidth - 1}px)`;

            if (me.element && me.element.lastChild !== stretcher) {
                me.element.appendChild(stretcher);
            }
        }
    }

    get scrollHeight() {
        return this.element ? this.element.scrollHeight : 0;
    }

    /**
     * The vertical scroll range of the widget. May be set to larger than the actual data
     * height to enable virtual scrolling. This is how the grid extends its scroll range
     * while only rendering a small subset of the dataset.
     * @property {Number}
     */
    set scrollHeight(scrollHeight) {
        const me = this,
            stretcher = me.stretcher || (me.stretcher = DomHelper.createElement({
                className : 'b-scroller-stretcher'
            }));

        stretcher.style.transform = `translateY(${scrollHeight - 1}px)`;
        if (me.element && me.element.lastChild !== stretcher) {
            me.element.appendChild(stretcher);
        }
    }

    /**
     * The client width of the widget.
     * @property {Number}
     * @readonly
     */
    get clientWidth() {
        return this.element ? this.element.clientWidth : 0;
    }

    /**
     * The client height of the widget.
     * @property {Number}
     * @readonly
     */
    get clientHeight() {
        return this.element ? this.element.clientHeight : 0;
    }

    /**
     * The unique ID of this Scroller
     * @property {String}
     * @readonly
     */
    get id() {
        if (!this._id) {
            if (this.widget) {
                this._id = `${this.widget.id}-scroller`;
            }
            else {
                this._id = IdHelper.generateId('scroller-');
            }
        }
        return this._id;
    }
}
