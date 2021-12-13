/**
 * @module Grid/util/SubGridScroller
 */

import Scroller from '../../Core/helper/util/Scroller.js';
import Rectangle from '../../Core/helper/util/Rectangle.js';
import DomHelper from '../../Core/helper/DomHelper.js';

const immediatePromise = new Promise((resolve) => resolve()),
    defaultScrollOptions = {
        block : 'nearest'
    };

/**
 * A Scroller subclass which handles scrolling in a SubGrid. Needs special treatment since the SubGrid itself only
 * allows horizontal scrolling, while the vertical scrolling is done by an outer element containing all subgrids.
 *
 * @internal
 */
export default class SubGridScroller extends Scroller {
    scrollIntoView(element, options = defaultScrollOptions) {
        const me = this,
            { xDelta, yDelta } = me.getDeltaTo(element, options),
            result = (xDelta || yDelta) ? me.scrollBy(xDelta, yDelta, options) : immediatePromise;

        if (options.highlight || options.focus) {
            result.then(() => {
                if (options.highlight) {
                    if (element instanceof Rectangle) {
                        element.translate(-xDelta, -yDelta).highlight();
                    }
                    else {
                        DomHelper.highlight(element);
                    }
                }
                options.focus && element.focus && element.focus();
            });
        }
        return result;
    }

    scrollBy(xDelta, yDelta, options) {
        const yPromise = yDelta && this.yScroller.scrollBy(0, yDelta, options),
            xPromise = xDelta && super.scrollBy(xDelta, 0, options);

        if (xPromise && xPromise.cancel && yPromise && yPromise.cancel) {
            const cancelX = xPromise.cancel,
                cancelY = yPromise.cancel;

            // Set up cross canceling
            xPromise.cancel = yPromise.cancel = () => {
                cancelX();
                cancelY();
            };
            return Promise.all([xPromise, yPromise]);
        }

        return xPromise || yPromise;
    }

    scrollTo(toX, toY, options) {
        const
            yPromise = (toY != null) && this.yScroller.scrollTo(null, toY, options),
            xPromise = (toX != null) && super.scrollTo(toX, null, options);

        // Keep parters in sync immediately unless we are going to animate our position.
        // There are potentially three: The header, the footer and the docked fake horizontal scroller.
        // It will be more efficient and maintain correct state doing it now.
        if (!(options && options.animate)) {
            this.syncPartners();
        }
        
        if (xPromise && xPromise.cancel && yPromise && yPromise.cancel) {
            const cancelX = xPromise.cancel,
                cancelY = yPromise.cancel;

            // Set up cross canceling
            xPromise.cancel = yPromise.cancel = () => {
                cancelX();
                cancelY();
            };
            return Promise.all([xPromise, yPromise]);
        }

        return xPromise || yPromise;
    }

    get viewport() {
        return Rectangle.from(this.element).intersect(Rectangle.from(this.yScroller.element));
    }

    set y(y) {
        if (this.yScroller) {
            this.yScroller.y = y;
        }
    }

    get y() {
        return this.yScroller ? this.yScroller.y : 0;
    }

    get maxY() {
        return this.yScroller ? this.yScroller.maxY : 0;
    }

    get scrollHeight() {
        return this.yScroller ? this.yScroller.scrollHeight : 0;
    }

    get clientHeight() {
        return this.yScroller ? this.yScroller.clientHeight : 0;
    }
}
