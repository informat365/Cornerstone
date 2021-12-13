import Layout from './Layout.js';
import Widget from '../Widget.js';
import EventHelper from '../../helper/EventHelper.js';
import BrowserHelper from '../../helper/BrowserHelper.js';

/**
 * @module Core/widget/layout/Card
 */

const animationClasses = [
    'b-slide-out-left',
    'b-slide-out-right',
    'b-slide-in-left',
    'b-slide-in-right'
];

/**
 * A helper class for containers which must manage multiple child widgets, of which only one may be visible at once such
 * as a {@link Core.widget.TabPanel}. This class offers an active widget switching API, and optional slide-in,
 * slide-out animations from child to child.
 */
export default class Card extends Layout {
    static get $name() {
        return 'Card';
    }

    static get defaultConfig() {
        return {
            containerCls : 'b-card-container',

            itemCls : 'b-card-item',

            /**
             * Specifies whether to slide tabs in and out of visibility.
             * @config {Boolean}
             * @default
             */
            animateCardChange : true
        };
    }

    /**
     * Get/set active item, using index or the Widget to activate
     * @param {Core.widget.Widget|Number} activeItem
     * @returns {Object} An object describing the card change containing the following properties:
     *
     *  - `prevActiveIndex` The previously active index.
     *  - `prevActiveItem ` The previously active child item.
     *  - `activeIndex    ` The newly active index.
     *  - `activeItem     ` The newly active child item.
     *  - `promise        ` A promise which completes when the slide-in animation finishes and the child item contains
     * focus if it is focusable.
     */
    setActiveItem(activeIndex) {
        const me                    = this,
            { owner }                 = me,
            { contentElement, items } = owner,
            widgetPassed              = activeIndex instanceof Widget,
            prevActiveIndex           = parseInt(contentElement.dataset.activeIndex),
            prevActiveItem            = items[prevActiveIndex],
            newActiveItem             = owner.items[activeIndex = widgetPassed ? (activeIndex = items.indexOf(activeIndex)) : parseInt(activeIndex)],
            event = {
                prevActiveIndex,
                prevActiveItem
            };

        // There's a child widget at that index to activate
        if (newActiveItem && newActiveItem !== prevActiveItem) {
            const prevItemElement = prevActiveItem.element,
                newActiveElement = newActiveItem && newActiveItem.element;

            event.activeIndex = activeIndex;
            event.activeItem = newActiveItem;

            // A previous card change is in progress, abort it and clean the items it was operating upon
            if (me.animateDetacher) {
                const abortedEvent = me.animateDetacher.event;
                me.animateDetacher();
                abortedEvent.prevActiveItem.element.classList.remove(animationClasses);
                abortedEvent.activeItem.element.classList.remove('b-active', ...animationClasses);
                me.animateDetacher = null;
            }

            event.promise = new Promise((resolve, reject) => {
                // If there's something to slide out, slide it out, and slide the new item in
                if (prevItemElement && me.animateCardChange) {
                    prevItemElement.classList.add(activeIndex > prevActiveIndex ? 'b-slide-out-left' : 'b-slide-out-right');
                    newActiveElement.classList.add('b-active', activeIndex < prevActiveIndex ? 'b-slide-in-left' : 'b-slide-in-right');

                    // Paint early, to have contents in place when sliding in
                    newActiveItem.triggerPaint();

                    // When the new widget is in place, clean up
                    me.animateDetacher = EventHelper.on({
                        element      : newActiveElement,
                        animationend : () => {
                            me.animateDetacher = null;

                            // Clean incoming widget's animation classes
                            newActiveElement.classList.remove(...animationClasses);

                            // If there's an outgoing item, clean its animation classes and hide it
                            if (prevItemElement) {
                                prevItemElement.classList.remove('b-active', ...animationClasses);
                            }
                            contentElement.dataset.activeIndex = activeIndex;

                            // Note that we have to call focus *after* the element is in its new position
                            // because focus({preventScroll:true}) is not supported everywhere
                            // and crazy browser scrolling behaviour on focus breaks the animation.
                            newActiveItem.focus();
                            resolve(event);
                        },
                        once : true
                    });
                    me.animateDetacher.reject = reject;
                    me.animateDetacher.event = event;
                }
                // Nothing to slide out or we are not animating.
                else {
                    if (prevItemElement) {
                        prevItemElement.classList.remove('b-active');
                    }
                    newActiveElement.classList.add('b-active');
                    contentElement.dataset.activeIndex = activeIndex;
                    newActiveItem.focus();
                    newActiveItem.triggerPaint();
                    resolve(event);
                }
            });
        }

        return event;
    }

    renderChildren() {
        const { owner } = this,
            activeIndex = owner.activeIndex;

        // The usual; not working on IE11
        if (BrowserHelper.isIE11) {
            this.animateCardChange = false;
        }

        // Ensure activeIndex of the owning Container at render time is honoured.
        if (owner.items && activeIndex != null && owner.items[activeIndex]) {
            owner.contentElement.dataset.activeIndex = activeIndex;
            owner.items[activeIndex].element.classList.add('b-active');
        }
        super.renderChildren();
    }

    /**
     * The active child index. Setting must be done through {@link #function-setActiveItem}
     * @property {Number}
     * @readonly
     */
    get activeIndex() {
        return parseInt(this.owner.contentElement.dataset.activeIndex);
    }

    /**
     * The active child item. Setting must be done through {@link #function-setActiveItem}
     * @property {Core.widget.Widget}
     * @readonly
     */
    get activeItem() {
        return this.owner.items[parseInt(this.owner.contentElement.dataset.activeIndex)];
    }
}

// Layouts must register themselves so that the static layout instantiation
// in Layout knows what to do with layout type names
Layout.registerLayout(Card);
