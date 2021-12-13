import Base from '../../Core/Base.js';
import Delayable from '../../Core/mixin/Delayable.js';
import EventHelper from '../../Core/helper/EventHelper.js';

/**
 * @module Grid/util/ScrollManager
 */

/**
 * Allows features to scroll grid when for example dragging close to edges. Normally you should not need to interact
 * with this class.
 * @internal
 */
export default class ScrollManager extends Delayable(Base) {
    //region Default config

    static get defaultConfig() {
        return {
            /**
             * Width in pixels of zone at element edges where scrolling can be triggered
             * @config {Number}
             * @default
             */
            zoneWidth : 50,

            /**
             * Scroll speed, higher number is slower. Calculated as "distance from zone edge / scrollSpeed"
             * @config {Number}
             * @default
             */
            scrollSpeed : 5,

            /**
             * Default element to use for vertical scrolling. Can be overridden in calls to `startMonitoring()`.
             * @config {HTMLElement}
             */
            verticalElement : null,

            /**
             * The direction(s) to scroll ('horizontal', 'vertical' or 'both')
             * @config {String}
             */
            direction : 'both',

            monitoring : []
        };
    }

    //endregion

    //region Start/stop monitoring

    /**
     * Starts monitoring an element. It will be scrolled if mouse is pressed and within zoneWidth pixels from element edge.
     * @param {HTMLElement|Object} elementOrConfig Element which might be scrolled or config { element, callback, thisObj }
     */
    startMonitoring(elementOrConfig) {
        const me              = this,
            config          = elementOrConfig instanceof HTMLElement ? { element : elementOrConfig } : elementOrConfig,
            element         = config.element,
            verticalElement = config.verticalElement || me.verticalElement || element;

        // only interested in elements that exists and are scrollable
        if (element && (element.scrollWidth > element.offsetWidth || verticalElement.scrollHeight > verticalElement.offsetHeight)) {
            // already monitoring, bail out
            if (me.monitoring.find(m => m.element === element)) return;

            config.direction = config.direction || me.direction;

            // store some stuff needed later
            Object.assign(config, {
                verticalElement,
                scrollManager      : me,
                startScrollLeft    : element.scrollLeft,
                startScrollTop     : verticalElement.scrollTop,
                scrollLeft         : element.scrollLeft,
                scrollTop          : verticalElement.scrollTop,
                scrollRelativeLeft : 0,
                scrollRelativeTop  : 0
            });

            const handler = me.onMouseMove.bind(config);

            // listen to mousemove to determine if scroll needed or not
            me.mouseMoveDetacher = EventHelper.on({
                mousemove : handler,
                touchmove : handler,
                element
            });

            // Cache scrollWidth because it may actually change in Edge and allow scrolling to empty space when creating
            // dependency
            config.scrollWidth = element.scrollWidth;

            me.monitoring.push(config);
        }
    }

    /**
     * Stops monitoring an element.
     * @param {HTMLElement} element Element for which monitoring is not desired any more and should stop as soon as possible. Preferably right away, but no later than on next frame :)
     */
    stopMonitoring(element) {
        const me     = this,
            config = me.monitoring.find(m => m.element === element);

        // cant stop nothing...
        if (!config) return;

        me.stopScroll(config);
        me.mouseMoveDetacher && me.mouseMoveDetacher();
        me.monitoring.splice(me.monitoring.indexOf(config), 1);
    }

    //endregion

    //region Internals

    /**
     * Starts scrolling (see #performScroll). Called from onMouseMove.
     * @private
     * @param {Object} config Config object
     */
    startScroll(config) {
        config.scrolling = true;
        this.performScroll(config);
    }

    /**
     * Stops scrolling. Called from onMouseMove.
     * @private
     * @param {Object} config
     */
    stopScroll(config) {
        config.scrolling = false;
        if (config.scrollRequested) {
            this.cancelAnimationFrame(config.frameId);
            config.scrollRequested = false;
        }
    }

    /**
     * Scrolls by an amount determined by config.scrollDeltaX/Y on each frame. Start/stop by calling #startScroll and
     * #stopScroll.
     * @private
     * @param {Object} config Config object
     */
    performScroll(config) {
        // this function is called repeatedly on each frame for as long as scrolling is needed

        // check that scrolling is needed
        if (config.scrolling && !config.scrollRequested) {
            let element  = config.element,
                vertical = config.verticalElement;

            // scroll the determined amount of pixels
            if (config.scrollDeltaX !== 0) {
                config.scrollLeft += config.scrollDeltaX;

                // limit to element edges
                config.scrollLeft = Math.max(Math.min(config.scrollLeft, config.scrollWidth - element.offsetWidth), 0);

                config.scrollRelativeLeft = config.scrollLeft - config.startScrollLeft;
                element.scrollLeft = config.scrollLeft;
            }

            if (config.scrollDeltaY !== 0) {
                config.scrollTop += config.scrollDeltaY;

                // limit to element edges
                config.scrollTop = Math.max(Math.min(config.scrollTop, vertical.scrollHeight - vertical.offsetHeight), 0);

                config.scrollRelativeTop = config.scrollTop - config.startScrollTop;
                vertical.scrollTop = config.scrollTop;
            }

            // call callback if scrolled in any direction
            if ((config.scrollRelativeLeft !== 0 || config.scrollRelativeTop !== 0) && config.callback) {
                config.callback.call(config.thisObj || this, config);
            }

            // scroll some more on next frame
            config.scrollRequested = true;

            config.frameId = this.requestAnimationFrame(() => {
                config.scrollRequested = false;
                this.performScroll(config);
            });
        }
    }

    /**
     * Listener for mouse move on monitored element. Determines if scrolling is needed, and if so how fast to scroll.
     * See #zoneWidth & #scrollSpeed configs.
     * @private
     * @param {MouseEvent} event
     */
    onMouseMove(event) {
        const config   = this,
            me       = config.scrollManager,
            element  = config.element,
            vertical = config.verticalElement,
            box      = element.getBoundingClientRect(),
            vbox     = vertical.getBoundingClientRect(),
            width    = me.zoneWidth,
            speed    = me.scrollSpeed;

        // scroll left or right?
        config.scrollDeltaX = 0;

        if (config.direction !== 'vertical') {
            if (event.clientX > box.right - width) {
                config.scrollDeltaX = Math.round((width - (box.right - event.clientX)) / speed) + 1;
            }
            else if (event.clientX < box.left + width) {
                config.scrollDeltaX = -Math.round((width + (box.left - event.clientX)) / speed) - 1;
            }
        }

        //console.log(config.scrollDeltaX, event.clientX, box.right, element.scrollLeft);

        // scroll up or down?
        config.scrollDeltaY = 0;
        if (config.direction !== 'horizontal') {
            if (event.clientY > vbox.bottom - width) {
                config.scrollDeltaY = Math.round((width - (vbox.bottom - event.clientY)) / speed) + 1;
            }
            else if (event.clientY < vbox.top + width) {
                config.scrollDeltaY = -Math.round((width + (vbox.top - event.clientY)) / speed) - 1;
            }
        }

        if (config.scrollDeltaX === 0 && config.scrollDeltaY === 0) {
            me.stopScroll(config);
        }
        else {
            me.startScroll(config);
        }
    }

    //endregion
}
