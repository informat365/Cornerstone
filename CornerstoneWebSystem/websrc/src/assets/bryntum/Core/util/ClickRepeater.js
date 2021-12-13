import Base from '../Base.js';
import Delayable from '../mixin/Delayable.js';
import FunctionHelper from '../helper/FunctionHelper.js';
import EventHelper from '../helper/EventHelper.js';

/**
 * @module Core/util/ClickRepeater
 */

/**
 * A helper class, which, when applied to an {@link #config-element} means that a mousedown
 * and hold on that element will, after a configured {@link #config-delay}, begin
 * autorepeating `click` events on that element, starting at a rate of
 * {@link #config-startRate} clicks per second, and over {@link #config-accelerateDuration}
 * milliseconds, accelerate to firing clicks at {@link #config-endRate} times per second.
 *
 * An example of this is used by the {@link Core.widget.NumberField NumberField}'s
 * spinner triggers.
 */
export default class ClickRepeater extends Delayable(Base) {
    static get defaultConfig() {
        return {
            /**
             * The element on which to fire autorepeating `click` events when the mouse is held down.
             * @config {HTMLElement}
             */
            element : null,

            /**
             * A query selector which specifies subtargets of this ClickRepeater's {@link #config-element}
             * which act as the click auto repeat event targets.
             * @config {String}
             * @default
             */
            delegate : null,

            /**
             * How long in milliSeconds to pause before starting the click repeats.
             * @config {Number}
             * @default
             */
            delay : 500,

            /**
             * Clicks per second to start firing after the initial {@link #config-delay}
             * @config {Number}
             * @default
             */
            startRate : 2,

            /**
             * Clicks per second to fire at top speed, after accelerating over the {@link #config-accelerateDuration}
             * @config {Number}
             * @default
             */
            endRate : 20,

            /**
             * How long in millieconds to accelerate from the
             * {@link #config-startRate} to the {@link #config-startRate}.
             * @config {Number}
             * @default
             */
            accelerateDuration : 4000
        };
    }

    doDestroy() {
        this.mousedownRemover && this.mousedownRemover();
        super.doDestroy();
    }

    set element(element) {
        this._element = element;

        this.mousedownRemover = EventHelper.on({
            element,
            mousedown : 'onMouseDown',
            thisObj   : this
        });
    }

    get element() {
        return this._element;
    }

    onMouseDown(e) {
        const me = this,
            target = me.delegate ? e.target.closest(me.delegate) : me.element;

        if (target) {
            me.triggerEvent = e;
            EventHelper.lockComposedPath(me.triggerEvent);

            me.activeListenerRemover = EventHelper.on({
                mouseup : {
                    element : document,
                    handler : 'onMouseUp'
                },
                mousemove : {
                    element : target,
                    handler : 'onTargetMouseMove'
                },
                mouseleave : {
                    element : target,
                    handler : 'onTargetMouseLeave'
                },
                mouseenter : {
                    element : target,
                    handler : 'onTargetMouseEnter'
                },
                thisObj : me
            });

            me.setTimeout('startAutoRepeat', me.delay);
        }
    }

    onMouseUp(e) {
        const me = this;

        me.acceleration && me.acceleration.cancel();
        me.activeListenerRemover && me.activeListenerRemover();
        me.clearTimeout('startAutoRepeat');
        me.clearTimeout(me.repeatTimer);
    }

    onTargetMouseLeave() {
        this.clearInterval(this.repeatTimer);
    }

    onTargetMouseEnter(e) {
        this.triggerEvent = e;
        this.fireClick();
    }

    onTargetMouseMove(e) {
        this.triggerEvent = e;
    }

    startAutoRepeat() {
        const me = this;

        me.interval = me.startInterval = 1000 / me.startRate;
        me.accelerationDelta = me.startInterval - 1000 / me.endRate;

        // Begin animating the interval from 1000/me.startRate to 1000/me.endRate
        me.fireClick();
        me.acceleration = FunctionHelper.animate(me.accelerateDuration, me.nextTick, me, 'easeOutQuad');
    }

    nextTick(progress) {
        this.interval = this.startInterval - (this.accelerationDelta * progress);
    }

    fireClick() {
        const me = this,
            clickEvent = new MouseEvent('click', me.triggerEvent);

        me.triggerEvent.target.dispatchEvent(clickEvent);
        me.repeatTimer = me.setTimeout(me.fireClick, me.interval);
    }
}
