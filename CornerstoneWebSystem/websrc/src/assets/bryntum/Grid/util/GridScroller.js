import Scroller from '../../Core/helper/util/Scroller.js';

/**
 * @module Grid/util/GridScroller
 */

const xAxis = {
    x : 1
};

/**
 * A Scroller subclass which handles scrolling in a grid.
 *
 * If the grid has no parallel scrolling grids (No locked columns), then this functions
 * transparently as a Scroller.
 *
 * If there are locked columns, then scrolling to an _element_ will invoke the scroller
 * of the subgrid which contains that element.
 * @internal
 */
export default class GridScroller extends Scroller {
    addScroller(scroller) {
        (this.xScrollers || (this.xScrollers = [])).push(scroller);
    }

    addPartner(otherScroller, axes = xAxis) {
        if (typeof axes === 'string') {
            axes = {
                [axes] : 1
            };
        }

        // Link up all our X scrollers
        if (axes.x) {
            //<debug>
            if (otherScroller.xScrollers.length !== this.xScrollers.length) {
                throw new Error('Grid scrollers can only be synced in the X axis between grids with the same number of SubGrids');
            }
            //</debug>
            for (let i = 0; i < this.xScrollers.length; i++) {
                this.xScrollers[i].addPartner(otherScroller.xScrollers[i], 'x');
            }
        }
        // We are the only Y scroller
        if (axes.y) {
            super.addPartner(otherScroller, 'y');
        }
    }

    updateOverflowX(overflowX) {
        this.xScrollers && this.xScrollers.forEach(s => s.overflowX = overflowX);
        this.widget.virtualScrollers.classList[overflowX === false ? 'add' : 'remove']('b-hide-display');
    }

    scrollIntoView(element, options) {
        // If we are after an element, we have to ask the scroller of the SubGrid
        // that the element is in. It will do the X scrolling and delegate the Y
        // scrolling up to this GridScroller.
        if (element.nodeType === 1) {
            for (const subGridScroller of this.xScrollers) {
                if (subGridScroller.element.contains(element)) {
                    return subGridScroller.scrollIntoView(element, options);
                }
            }
        }
        else {
            return super.scrollIntoView(element, options);
        }
    }

    set x(x) {
        if (this.xScrollers) {
            this.xScrollers[0].x = x;
        }
    }

    get x() {
        // when trying to scroll grid with no columns xScrollers do not exist
        return this.xScrollers ? this.xScrollers[0].x : 0;
    }
}
