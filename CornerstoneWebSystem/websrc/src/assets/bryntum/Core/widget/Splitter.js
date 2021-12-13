import Widget from './Widget.js';
import DomHelper from '../helper/DomHelper.js';
import TemplateHelper from '../helper/TemplateHelper.js';
import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';
import EventHelper from '../helper/EventHelper.js';

/**
 * @module Core/widget/Splitter
 */

const hasFlex = el => parseInt(DomHelper.getStyleValue(el, 'flex-basis'), 10) || parseInt(DomHelper.getStyleValue(el, 'flex-grow'), 10);

/**
 * A simple splitter widget that resizes the elements next to it or above/below it depending on orientation.
 *
 * @extends Core/widget/Widget
 * @classType splitter
 * @externalexample widget/Splitter.js
 */
export default class Splitter extends Widget {
    //region Config

    static get defaultConfig() {
        return {
            /**
             * The splitters orientation, configurable with 'auto', 'horizontal' or 'vertical'.
             *
             * 'auto' tries to determine the orientation by either checking the `flex-direction` of the parent element
             * or by comparing the positions of the closest sibling elements to the splitter. If they are above and
             * below 'horizontal' is used, if not it uses 'vertical'.
             *
             * ```
             * new Splitter({
             *    orientation : 'horizontal'
             * });
             * ```
             *
             * To receive the actully used orienatation when configured with 'auto', see
             * {@link #property-currentOrientation}.
             *
             * @config {String}
             * @default
             */
            orientation : 'auto'
        };
    }

    //endregion

    //region Init & destroy

    doDestroy() {
        this.mouseDetacher && this.mouseDetacher();
    }

    //endregion

    //region Template & element

    template() {
        return TemplateHelper.tpl`
            <div class="b-splitter"></div>
        `;
    }

    get element() {
        return super.element;
    }

    set element(element) {
        super.element = element;

        EventHelper.on({
            element   : this.element,
            mousedown : 'onMouseDown',
            thisObj   : this
        });
    }

    //endregion

    //region Orientation

    /**
     * Get actually used orientation, which is either the configured value for `orientation` or if configured with
     * 'auto' the currently used orientation.
     * @property {String}
     * @readonly
     */
    get currentOrientation() {
        return this._currentOrientation;
    }

    /**
     * Splitter orientation, see {@link #config-orientation}. When set to 'auto' then actually used orientation can be
     * retrieved using {@link #property-currentOrientation}.
     * @property {String}
     * @readonly
     */
    get orientation() {
        return this._orientation;
    }

    set orientation(orientation) {
        this._orientation = orientation;

        if (orientation === 'auto') {
            this._currentOrientation = null;
        }
        else {
            this._currentOrientation = orientation;
        }

        this.updateOrientation();
    }

    // Determines current orientation if configured with 'auto'. Adds orientation cls
    updateOrientation() {
        const
            me          = this,
            { element } = this;

        if (me._prevOrientation) {
            element.classList.remove(me._prevOrientation);
        }

        // Orientation auto and already rendered, determine orientation to use
        if (!me._currentOrientation && me.rendered && element.offsetParent) {
            const flexDirection = DomHelper.getStyleValue(element.parentElement, 'flex-direction');

            // If used in a flex layout, determine orientation from flex-direction
            if (flexDirection) {
                me._currentOrientation = flexDirection.startsWith('column') ? 'horizontal' : 'vertical';
            }
            // If used in some other layout, try to determine form sibling elements position
            else {
                const
                    previous = element.previousElementSibling,
                    next     = element.nextElementSibling;

                if (!previous || !next) {
                    // To early in rendering, next sibling not rendered yet
                    return;
                }

                const
                    prevRect   = previous.getBoundingClientRect(),
                    nextRect   = next.getBoundingClientRect(),
                    topMost    = prevRect.top < nextRect.top ? prevRect : nextRect,
                    bottomMost = topMost === nextRect ? prevRect : nextRect;

                me._currentOrientation = topMost.top !== bottomMost.top ? 'horizontal' : 'vertical';
            }
        }

        if (me._currentOrientation) {
            element.classList.add(`b-${me._currentOrientation}`);
        }

        me._prevOrientation = me._currentOrientation;
    }

    //endregion

    //region Events

    onMouseDown(event) {
        const
            me          = this,
            { element } = me,
            prev        = element.previousElementSibling,
            next        = element.nextElementSibling,
            prevHasFlex = hasFlex(prev),
            nextHasFlex = hasFlex(next),
            flexed      = [];

        // Remember flexed children, to enable maintaining their proportions on resize
        for (const child of element.parentElement.children) {
            if (hasFlex(child) && child !== element) {
                flexed.push({
                    element : child,
                    width   : child.offsetWidth,
                    height  : child.offsetHeight
                });
            }
        }

        me.context = {
            startX     : event.pageX,
            startY     : event.pageY,
            prevWidth  : prev.offsetWidth,
            prevHeight : prev.offsetHeight,
            nextWidth  : next.offsetWidth,
            nextHeight : next.offsetHeight,
            prevHasFlex,
            nextHasFlex,
            flexed,
            prev,
            next
        };

        me.mouseDetacher = EventHelper.on({
            element   : document,
            mousemove : 'onMouseMove',
            mouseup   : 'onMouseUp',
            thisObj   : me
        });
    }

    onMouseMove(event) {
        const
            me = this,
            { context } = me,
            deltaX = event.pageX - context.startX,
            deltaY = event.pageY - context.startY;

        event.preventDefault();

        Object.assign(context, {
            deltaX,
            deltaY
        });

        if (!context.started) {
            context.started = true;

            me.trigger('start', { context, event });

            // Convert heights/widths to flex for flexed elements to maintain proportions
            // 100px high -> flex-grow 100
            context.flexed.forEach(flexed => {
                if (me.currentOrientation === 'vertical') {
                    flexed.element.style.flexGrow = flexed.width;
                }
                else {
                    flexed.element.style.flexGrow = flexed.height;
                }
                //Remove flex-basis, since it interferes with resizing
                flexed.element.style.flexBasis = '0';
            });
        }

        // Adjust flex-grow or width/height for splitters closest siblings
        if (me.currentOrientation === 'vertical') {
            const
                newPrevWidth = context.prevWidth + deltaX,
                newNextWidth = context.nextWidth - deltaX;

            if (context.prevHasFlex) {
                context.prev.style.flexGrow = newPrevWidth;
            }
            else {
                context.prev.style.width = `${newPrevWidth}px`;
            }

            if (context.nextHasFlex) {
                context.next.style.flexGrow = newNextWidth;
            }
            else {
                context.next.style.width = `${newNextWidth}px`;
            }
        }
        else {
            const
                newPrevHeight = context.prevHeight + deltaY,
                newNextHeight = context.nextHeight - deltaY;

            if (context.prevHasFlex) {
                context.prev.style.flexGrow = newPrevHeight;
            }
            else {
                context.prev.style.height = `${newPrevHeight}px`;
            }

            if (context.nextHasFlex) {
                context.next.style.flexGrow = newNextHeight;
            }
            else {
                context.next.style.height = `${newNextHeight}px`;
            }
        }

        me.trigger('move', { context, event });
    }

    onMouseUp(event) {
        const me = this;

        me.mouseDetacher && me.mouseDetacher();
        me.mouseDetacher = null;

        if (me.context.started) {
            me.trigger('end', { context : me.context, event });
        }

        me.context = null;
    }

    //endregion

    render() {
        super.render(...arguments);

        const me = this;

        if (!me._currentOrientation) {
            // Determine current orientation
            me.updateOrientation();
            if (!me._currentOrientation) {
                // Determine current orientation from closest siblings, needs to happen on new frame since the splitter
                // is rendered before its next sibling
                me.requestAnimationFrame(() => me.updateOrientation());
            }
        }
    }
}

BryntumWidgetAdapterRegister.register('splitter', Splitter);
