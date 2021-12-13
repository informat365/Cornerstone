/**
 * @module Core/helper/util/Rectangle
 */

import DomHelper from '../DomHelper.js';
import BrowserHelper from '../BrowserHelper.js';

const allBorders        = ['border-top-width', 'border-right-width', 'border-bottom-width', 'border-left-width'],
    allPaddings       = ['padding-top', 'padding-right', 'padding-bottom', 'padding-left'],
    borderNames       = {
        t : 'border-top-width',
        r : 'border-right-width',
        b : 'border-bottom-width',
        l : 'border-left-width'
    },
    paddingNames      = {
        t : 'padding-top',
        r : 'padding-right',
        b : 'padding-bottom',
        l : 'padding-left'
    },
    zeroBased         = Object.freeze({
        x : 0,
        y : 0
    }),
    alignSpecRe       = /^([trblc])(\d*)-([trblc])(\d*)$/i,
    alignPointRe      = /^([trblc])(\d*)$/i,
    edgeNames         = [
        'top',
        'right',
        'bottom',
        'left'
    ],
    edgeIndices       = {
        t : 0,
        r : 1,
        b : 2,
        l : 3
    },
    defaultAlignments = [
        'b-t',
        'l-r',
        't-b',
        'r-l'
    ],
    zeroOffsets       = Object.freeze([0, 0]),
    matchDimensions   = ['width', 'height'];

// Parse a l0-r0 (That's how Menus align to their owning MenuItem) slign spec.
function parseAlign(alignSpec) {
    const parts        = alignSpecRe.exec(alignSpec),
        myOffset     = parseInt(parts[2] || 50),
        targetOffset = parseInt(parts[4] || 50);

    //<debug>
    if (!parts) {
        throw new Error('Invalid Rectangle alignment specification "' + alignSpec + '"');
    }
    //</debug>

    // Comments assume the Menu's alignSpec of l0-r0 is used.
    return {
        myAlignmentPoint     : parts[1] + myOffset,     // l0
        myEdge               : parts[1],                // l
        myOffset,                                       // 0
        targetAlignmentPoint : parts[3] + targetOffset, // r0
        targetEdge           : parts[3],                // r
        targetOffset,                                   // 0
        startZone            : edgeIndices[parts[3]]    // 1 - start trying zone 1 in TRBL order
    };
}

// Takes a result from the above function and flips edges for the axisLock config
function flipAlign(align) {
    return `${edgeNames[(edgeIndices[align.myEdge] + 2) % 4][0]}${align.myOffset}-${edgeNames[(edgeIndices[align.targetEdge] + 2) % 4][0]}${align.targetOffset}`;
}

function createOffsets(offset) {
    if (offset == null) {
        return zeroOffsets;
    }
    else if (typeof offset === 'number') {
        return [offset, offset];
    }
    //<debug>
    else if (!Array.isArray(offset)) {
        throw new Error('Rectangle offset must be a single number, or an [x, y] array');
    }
    //</debug>
    return offset;
}

/**
 * Encapsulates rectangular areas for comparison, intersection etc.
 *
 * Note that the `right` and `bottom` properties are *exclusive*.
 *
 */
export default class Rectangle {
    /**
     * Returns the Rectangle in document based coordinates of the passed element.
     *
     * *Note:* If the element passed is the `document` or `window` the `window`'s
     * rectangle is returned which is always at `[0, 0]` and encompasses the
     * browser's entire document viewport.
     * @param {HTMLElement} element The element to calculate the Rectangle for.
     * @param {HTMLElement} [relativeTo] Optionally, a parent element in whose space to calculate the Rectangle.
     * @param {Boolean} [ignorePageScroll] Use browser viewport based coordinates.
     * @return {Core.helper.util.Rectangle} The Rectangle in document based (or, optionally viewport based) coordinates. Relative to the _relativeTo_ parameter if passed.
     */
    static from(element, relativeTo, ignorePageScroll = false) {
        if (element instanceof Rectangle) {
            return element;
        }

        if (relativeTo) {
            // TODO: nige should figure out if there is a better solution
            let { scrollLeft, scrollTop } = relativeTo;
            if ((BrowserHelper.isEdge || BrowserHelper.isSafari) && relativeTo === document.body) {
                scrollLeft = scrollTop = 0;
            }
            relativeTo = Rectangle.from(relativeTo).translate(-scrollLeft, -scrollTop);
        }
        else {
            relativeTo = zeroBased;
        }

        // Viewport is denoted by requesting window or document.
        // document.body may overflow the viewport, so this must not be evaluated as the viewport.
        const
            isViewport   = element === document || element === window,
            viewRect     = isViewport ? new Rectangle(0, 0, window.innerWidth, window.innerHeight) : element.getBoundingClientRect(),
            scrollOffset = (ignorePageScroll || isViewport) ? [0, 0] : [window.pageXOffset, window.pageYOffset];

        return new Rectangle(viewRect.left + scrollOffset[0] - relativeTo.x, viewRect.top + scrollOffset[1] - relativeTo.y, viewRect.width, viewRect.height);
    }

    /**
     * Returns the inner Rectangle (within border) in document based coordinates
     * of the passed element.
     * @param element The element to calculate the Rectangle for.
     * @param {HTMLElement} [relativeTo] Optionally, a parent element in whose space to calculate the Rectangle.
     * @param {Boolean} [ignorePageScroll] Use browser viewport based coordinates.
     * @return {Core.helper.util.Rectangle} The Rectangle in document based (or, optionally viewport based) coordinates. Relative to the _relativeTo_ parameter if passed.
     */
    static inner(element, relativeTo, ignorePageScroll = false) {
        const result = this.from(element, relativeTo, ignorePageScroll);

        // Can only ask for the following styles if element is in the document.
        if (document.body.contains(element)) {
            const borders = DomHelper.getStyleValue(element, allBorders);

            result.x += parseInt(borders[borderNames.l]);
            result.y += parseInt(borders[borderNames.t]);
            result.right -= parseInt(borders[borderNames.r]);
            result.bottom -= parseInt(borders[borderNames.b]);
        }

        return result;
    }

    /**
     * Returns the content Rectangle (within border and padding) in document based coordinates
     * of the passed element.
     * @param element The element to calculate the Rectangle for.
     * @param {HTMLElement} [relativeTo] Optionally, a parent element in whose space to calculate the Rectangle.
     * @param {Boolean} [ignorePageScroll] Use browser viewport based coordinates.
     * @return {Core.helper.util.Rectangle} The Rectangle in document based (or, optionally viewport based) coordinates. Relative to the _relativeTo_ parameter if passed.
     */
    static content(element, relativeTo, ignorePageScroll = false) {
        const result = this.from(element, relativeTo, ignorePageScroll);

        // Can only ask for the following styles if element is in the document.
        if (document.body.contains(element)) {
            const borders = DomHelper.getStyleValue(element, allBorders),
                padding = DomHelper.getStyleValue(element, allPaddings);

            result.x += parseInt(borders[borderNames.l]) + parseInt(padding[paddingNames.l]);
            result.y += parseInt(borders[borderNames.t]) + parseInt(padding[paddingNames.t]);
            result.right -= parseInt(borders[borderNames.r]) + parseInt(padding[paddingNames.r]);
            result.bottom -= parseInt(borders[borderNames.b]) + parseInt(padding[paddingNames.b]);
        }

        return result;
    }

    /**
     * Returns the client Rectangle (within border and padding and scrollbars) in document based coordinates
     * of the passed element.
     * @param element The element to calculate the Rectangle for.
     * @param {HTMLElement} [relativeTo] Optionally, a parent element in whose space to calculate the Rectangle.
     * @param {Boolean} [ignorePageScroll] Use browser viewport based coordinates.
     * @return {Core.helper.util.Rectangle} The Rectangle in document based (or, optionally viewport based) coordinates. Relative to the _relativeTo_ parameter if passed.
     */
    static client(element, relativeTo, ignorePageScroll = false) {
        let result = this.content(element, relativeTo, ignorePageScroll),
            scrollbarWidth = DomHelper.scrollBarWidth,
            padding;

        if (scrollbarWidth) {
            // Capture width taken by any vertical scrollbar.
            // If there is a vertical scrollbar, shrink the box.
            // TODO: We may have to shrink from the left in RTL mode.
            if (element.scrollHeight > element.clientHeight && DomHelper.getStyleValue(element, 'overflow-y') !== 'hidden') {
                padding = parseInt(DomHelper.getStyleValue(element, 'padding-right'));
                result.right += padding - Math.max(padding, scrollbarWidth);
            }

            // Capture height taken by any horizontal scrollbar.
            // If there is a horizontal scrollbar, shrink the box.
            if (element.scrollWidth > element.clientWidth && DomHelper.getStyleValue(element, 'overflow-x') !== 'hidden') {
                padding = parseInt(DomHelper.getStyleValue(element, 'padding-bottom'));
                result.bottom += padding - Math.max(padding, scrollbarWidth);
            }
        }

        // The client region excluding any scrollbars.
        return result;
    }

    /**
     * Returns a new rectangle created as the union of all supplied rectangles.
     * @param {Core.helper.util.Rectangle[]} rectangles
     * @return {Core.helper.util.Rectangle}
     */
    static union(...rectangles) {
        let { x, y, right, bottom } = rectangles[0],
            current;

        if (rectangles.length > 1) {
            for (let i = 1; i < rectangles.length; i++) {
                current = rectangles[i];

                if (current.x < x) {
                    x = current.x;
                }

                if (current.y < y) {
                    y = current.y;
                }

                if (current.right > right) {
                    right = current.right;
                }

                if (current.bottom > bottom) {
                    bottom = current.bottom;
                }
            }
        }

        return new Rectangle(x, y, right - x, bottom - y);
    }

    /**
     * Rounds this Rectangle to the pixel resolution of the current display.
     */
    round() {
        const me = this;

        me._x = DomHelper.roundPx(me._x);
        me._y = DomHelper.roundPx(me._y);
        me._width = DomHelper.roundPx(me._width);
        me._height = DomHelper.roundPx(me._height);

        return me;
    }

    // This class doesn't extend Base and extending doesn't seem to be
    // the way to go. Instead we duplicate smallest piece of logic here
    static get $name() {
        return this.hasOwnProperty('_$name') && this._$name || this.name;
    }

    get $name() {
        return this.constructor.$name;
    }

    /**
     * Constructs a Rectangle
     * @param x The X coordinate
     * @param y The Y coordinate
     * @param width The width
     * @param height The height
     */
    constructor(x, y, width, height) {
        const me = this;

        me._x = x;
        me._y = y;
        me._width = width;
        me._height = height;
    }

    /**
     * Creates a copy of this Rectangle.
     */
    clone() {
        const me     = this,
            result = new Rectangle(me.x, me.y, me.width, me.height);

        result.minHeight = me.minHeight;
        result.minWidth = me.minWidth;

        return result;
    }

    /**
     * Returns `true` if this Rectangle wholly contains the passed rectangle.
     *
     * Note that a {@link Core.helper.util.Point} may be passed.
     * @param other The Rectangle to test for containment within this Rectangle
     * @return {Boolean} `true` if the other Rectangle is wholly contained within this Rectangle
     */
    contains(other) {
        const me = this;

        if (other instanceof Rectangle) {
            return other._x >= me._x &&
                other._y >= me._y &&
                other.right <= me.right &&
                other.bottom <= me.bottom;
        }
        else {
            return false;
        }
    }

    /**
     * Checks if this Rectangle intersects the passed Rectangle
     * @param {Core.helper.util.Rectangle} other The Rectangle to intersect with this.
     * @param {Boolean} useBoolean Specify `true` to return a boolean value instead of constructing a new Rectangle
     * @return {Core.helper.util.Rectangle|Boolean} Returns the intersection Rectangle or `false` if there is no intersection.
     */
    intersect(other, useBoolean = false) {
        var me = this,
            y  = Math.max(me.y, other.y),
            r  = Math.min(me.right, other.right),
            b  = Math.min(me.bottom, other.bottom),
            x  = Math.max(me.x, other.x);

        if (b > y && r > x) {
            return useBoolean ? true : new Rectangle(x, y, r - x, b - y);
        }
        else {
            return false;
        }
    }

    equals(other, round = false) {
        const processor = round ? x => Math.round(x) : x => x;
        
        return other instanceof Rectangle &&
            processor(other.x) === processor(this.x) &&
            processor(other.y) === processor(this.y) &&
            processor(other.width) === processor(this.width) &&
            processor(other.height) === processor(this.height);
    }

    /**
     * Translates this Rectangle by the passed vector. Size is maintained.
     * @param {Number} x The X translation vector.
     * @param {Number} y The Y translation vector.
     * @returns This Rectangle;
     */
    translate(x, y) {
        this._x += x || 0;
        this._y += y || 0;
        return this;
    }

    /**
     * Moves this Rectangle to the passed `x`, `y` position. Size is maintained.
     * @param {Number} x The new X position.
     * @param {Number} y The new Y position.
     * @returns This Rectangle;
     */
    moveTo(x, y) {
        if (x != null) {
            this._x = x;
        }
        if (y != null) {
            this._y = y;
        }
        return this;
    }

    /**
     * Returns the vector which would translate this Rectangle (or Point) to the same position as the other Rectangle (or point)
     * @param {Core.helper.util.Rectangle/Core.helper.util.Point} other The Rectangle or Point to calculate the delta to.
     */
    getDelta(other) {
        return [other.x - this.x, other.y - this.y];
    }

    /**
     * The center point of this rectangle.
     * @property {Core.helper.util.Point}
     */
    get center() {
        return new Rectangle.Point(this.x + this.width / 2, this.y + this.height / 2);
    }

    /**
     * Get/sets the X coordinate of the Rectangle. Note that this does *not* translate the
     * Rectangle. The requested {@link #property-width} will change.
     * @property {Number}
     */
    set x(x) {
        let xDelta = x - this._x;

        this._x = x;
        this._width -= xDelta;
    }

    get x() {
        return this._x;
    }

    /**
     * Alias for x. To match DOMRect.
     * @property {Number}
     */
    set left(x) {
        this.x = x;
    }

    get left() {
        return this.x;
    }

    /**
     * Alias for y. To match DOMRect.
     * @property {Number}
     */
    set top(y) {
        this.y = y;
    }

    get top() {
        return this.y;
    }

    /**
     * Get/sets the Y coordinate of the Rectangle. Note that this does *not* translate the
     * Rectangle. The requested {@link #property-height} will change.
     * @property {Number}
     */
    set y(y) {
        let yDelta = y - this._y;

        this._y = y;
        this._height -= yDelta;
    }

    get y() {
        return this._y;
    }

    /**
     * Get/sets the width of the Rectangle. Note that the requested {@link #property-right} will change.
     * @property {Number}
     */
    set width(width) {
        this._width = width;
    }

    get width() {
        return this._width;
    }

    /**
     * Get/sets the height of the Rectangle. Note that the requested {@link #property-bottom} will change.
     * @property {Number}
     */
    set height(height) {
        this._height = height;
    }

    get height() {
        return this._height;
    }

    /**
     * Get/sets the right edge of the Rectangle. Note that the requested {@link #property-width} will change.
     *
     * The right edge value is exclusive of the calculated rectangle width. So x=0 and right=10
     * means a width of 10.
     * @property {Number}
     */
    set right(right) {
        this._width = right - this._x;
    }

    get right() {
        return this._x + this._width;
    }

    /**
     * Get/sets the bottom edge of the Rectangle. Note that the requested {@link #property-height} will change.
     *
     * The bottom edge value is exclusive of the calculated rectangle height. So y=0 and bottom=10
     * means a height of 10.
     * @property {Number}
     */
    set bottom(bottom) {
        this._height = bottom - this._y;
    }

    get bottom() {
        return this._y + this._height;
    }

    get area() {
        return this.width * this.height;
    }

    set minWidth(minWidth) {
        const me = this;

        if (isNaN(minWidth)) {
            me._minWidth = null;
        }
        else {
            me._minWidth = Number(minWidth);
            me.width = Math.max(me.width, me._minWidth);
        }
    }

    get minWidth() {
        return this._minWidth;
    }

    set minHeight(minHeight) {
        const me = this;

        if (isNaN(minHeight)) {
            me._minHeight = null;
        }
        else {
            me._minHeight = Number(minHeight);
            me.height = Math.max(me.height, me._minHeight);
        }
    }

    get minHeight() {
        return this._minHeight;
    }

    /**
     * Modifies the bounds of this Rectangle by the specified deltas.
     * @param {Number} x How much to *add* to the x position.
     * @param {Number} y  How much to *add* to the y position.
     * @param {Number} width  How much to add to the width.
     * @param {Number} height  How much to add to the height.
     * @returns This Rectangle
     */
    adjust(x, y, width, height) {
        const me = this;
        me.x += x;
        me.y += y;
        me.width += width;
        me.height += height;
        return me;
    }

    /**
     * Modifies the bounds of this rectangle by moving them by the specified amount in all directions.
     * @param {Number} amount How much to inflate
     * @returns {Core.helper.util.Rectangle} This Rectangle
     */
    inflate(amount) {
        return this.adjust(-amount, -amount, amount, amount);
    }

    /**
     * Attempts constrain this Rectangle into the passed Rectangle. If the `strict` parameter is `true`
     * then this method will return `false` if constraint could not be acheived.
     *
     * If this Rectangle has a `minHeight` or `minWidth` property, size will be adjusted while attempting to constrain.
     *
     * Right and bottom are adjusted first leaving the top and bottom sides to "win" in the case that this Rectangle overflows
     * the constrainTo Rectangle.
     * @param {Core.helper.util.Rectangle} constrainTo The Rectangle to constrain this Rectangle into if possible.
     * @param {Boolean} strict Pass `true` to return false, and leave this Rectangle unchanged if constraint
     * could not be achieved.
     * @returns This Rectangle. If `strict` is true, and constraining was not successful, `false`.
     */
    constrainTo(constrainTo, strict) {
        const me        = this,
            originalHeight = me.height,
            originalY = me.y,
            minWidth  = me.minWidth || me.width,
            minHeight = me.minHeight || me.height;

        if (me.height >= constrainTo.height) {
            // If we're strict, fail if we could *never* fit into available height.
            if (strict && minHeight > constrainTo.height) {
                return false;
            }
            // If we are >= constrain height, we will have to be at top edge of constrainTo
            me._y = constrainTo.y;
            me.height = constrainTo.height;
        }

        if (me.width >= constrainTo.width) {
            // If we're strict, fail if we could *never* fit into available width.
            if (strict && minWidth > constrainTo.width) {
                // Could not be constrained; undo any previous attempt with height
                me.y = originalY;
                me.height = originalHeight;
                return false;
            }
            // If we are >= constrain width, we will have to be at left edge of constrainTo
            me._x = constrainTo.x;
            me.width = constrainTo.width;
        }

        let overflow = me.bottom - constrainTo.bottom;

        // Overflowing the bottom side, translate upwards.
        if (overflow > 0) {
            me.translate(0, -overflow);
        }

        overflow = me.right - constrainTo.right;

        // Overflowing the right side, translate leftwards.
        if (overflow > 0) {
            me.translate(-overflow);
        }

        overflow = constrainTo.y - me.y;

        // Now, after possible translation upwards, we overflow the top, translate downwards.
        if (overflow > 0) {
            me.translate(0, overflow);
        }

        overflow = constrainTo.x - me.x;

        // Now, after possible translation leftwards, we overflow the left, translate rightwards.
        if (overflow > 0) {
            me.translate(overflow);
        }

        return me;
    }

    /**
     * Returns a cloned version of this Rectangle aligned to a target Rectangle, or element or {@link Core.widget.Widget}.
     * @param {Object} spec Alignment specification.
     * @param {HTMLElement|Core.widget.Widget|Core.helper.util.Rectangle} spec.target The Widget or element or Rectangle to align to.
     * @param {Number[]} [spec.anchorSize] The `[width, height]` of the anchor pointer when in `top` position. The
     * width is the baseline length, and the height is the height of the arrow. If passed, the anchor position
     * will be calculated to be at the centre of the overlap of the two aligned edges and returned in the `anchor`
     * property of the resulting Rectangle:
     *
     *     {
     *         edge: 'top',         // or 'right' or 'bottom' or 'left'
     *         x/y: offset          // dimension to translate and value to translate by.
     *     }
     *
     * @param {Object} [spec.anchorPosition] an `{ x: n, y: n }` anchor translation to be used *if the requested alignment
     * succeeds without violating constraints*. If a fallback alignment is used, the anchor will be centered in the
     * overlap of the aligned edges as usual.
     * @param {Boolean} [spec.overlap] True to allow this to overlap the target.
     * @param {String} spec.align The edge alignment specification string, specifying two points to bring together.
     * The form is `[trblc][n]-[trblc][n]. The `n` is a percentage offset
     * along that edge which defines the alignment point. This is not valid for alignment point `c`.
     * For example `t0-b0' would align this Rectangle's top left corner with the bottom left corner of the `target`.
     * @param {HTMLElement|Core.widget.Widget|Core.helper.util.Rectangle} [spec.constrainTo] The Widget or Element or Rectangle to constrain to.
     * If the requested alignment cannot be constrained (it will first shrink the resulting Rectangle according
     * to the `minWidth` and `minHeight` properties of this rectangle), then it will try aligning at other edges
     * (honouring the `axisLock` option), and pick the fallback alignment which results in the shortest translation.
     * @param {Boolean} [spec.axisLock] Specify as a truthy value to fall back to aligning against the opposite
     * edge first if the requested alignment cannot be constrained into the `constrainTo` option. If specified
     * as `'flexible'`, then fallback will continue searching for solutions on the remaining two sides.
     * @param {Number|Number[]} [spec.offset] The 'x' and 'y' offset values to create an extra margin round the target
     * to offset the aligned widget further from the target. May be configured as -ve to move the aligned widget
     * towards the target - for example producing the effect of the anchor pointer piercing the target.
     * @returns {Core.helper.util.Rectangle} A new Rectangle aligned as requested if possible, but if the requested position violates
     * the `constrainTo` Rectangle, the shortest translation from the requested position which obeys constraints will be used.
     */
    alignTo(spec) {
        //<debug>
        if (!spec.target && !spec.position) {
            throw new Error('alignTo must be either passed a target to position by, or a position Point to position at');
        }
        //</debug>

        // The target and constrainTo may be passed as HtmlElements or Widgets.
        // If so, extract the Rectangles without mutating the incoming spec.
        let result = this.clone(),
            target = spec.target,
            constrainTo = spec.constrainTo,
            calculatedAnchorPosition, zone, resultZone, constrainingToViewport;

        if (target && !(target instanceof Rectangle)) {
            target = Rectangle.from(target.element ? target.element : target);
        }
        if (constrainTo && !(constrainTo instanceof Rectangle)) {
            // Viewport is denoted by requesting window or document.
            // document.body may overflow the viewport, so this must not be evaluated as the viewport.
            constrainingToViewport = constrainTo === window || constrainTo === document;
            constrainTo = Rectangle.from(constrainTo.element ? constrainTo.element : constrainTo, null, spec.ignorePageScroll);
        }
        const me                  = this,
            targetOffsets       = createOffsets(spec.offset),
            {
                align,
                axisLock,
                anchorSize,
                anchorPosition
            }                   = spec,
            alignSpec           = parseAlign(align),
            position            = spec.position || ((target && target.$name === 'Point') ? target : null),
            targetConstrainRect = constrainTo && constrainTo.clone(),
            constraintZones = [],
            zoneOrder = [{
                zone : zone = alignSpec.startZone,
                align
            }],
            matchDimension = spec.matchSize && matchDimensions[alignSpec.startZone & 1],
            originalSize   = me[matchDimension];

        // Match the size of the edge we are aligning against
        if (matchDimension && axisLock) {
            result[matchDimension] = target[matchDimension];
        }

        // Ensure we will fit before trying
        if (constrainTo) {
            result.constrainTo(constrainTo);
        }

        // If we are aligning edge-to-edge, then plan our fallback strategy when we are constrained.
        if (constrainTo && alignSpec.startZone != null) {
            // Create the list of zone numbers and alignments to try in the preferred order.
            //
            // In the case of axisLock, go through the zones by each axis.
            // So if they asked for t-b, which is zone 2,
            // the array will be [2, 0, 3, 1] (t-b, b-t, r-l, l-r)
            if (axisLock) {
                // First axis flip has to maintain the offset along that axis.
                // so align: l0-r0 has to flip to align: r0-l0. See submenu flipping when
                // constrained to the edge. It flips sides but maintains vertical position.
                zoneOrder.push({
                    zone  : zone = (zone + 2) % 4,
                    align : flipAlign(alignSpec)
                });

                // Only try the other axis is axisLock is 'flexible'
                if (axisLock === 'flexible') {
                    zoneOrder.push({
                        zone  : zone = (alignSpec.startZone + 1) % 4,
                        align : defaultAlignments[zone]
                    });
                    zoneOrder.push({
                        zone  : zone = (zone + 2) % 4,
                        align : defaultAlignments[zone]
                    });
                }
            }
            // Go through the zones in order from the requested start.
            // So if they asked for t-b, which is zone 2,
            // the array will be [2, 3, 0, 1] (t-b, r-l, b-t, l-r)
            else {
                for (let i = 1; i < 4; i++) {
                    zoneOrder.push({
                        zone  : zone = (zone + 1) % 4,
                        align : defaultAlignments[zone]
                    });
                }
            }
        }

        // Allow them to pass anchorPosition: {x: 10} to indicate that after a fully successful,
        // unconstrained align, the anchor should be 10px from the start.
        if (anchorPosition) {
            let pos = (alignSpec.startZone & 1) ? 'y' : 'x';
            calculatedAnchorPosition = {
                [pos] : anchorPosition[pos],
                edge  : edgeNames[(alignSpec.startZone + 2) % 4]
            };
        }

        // Keep the target within reach. If it's way outside, pull it back so that it's only just outside);
        if (targetConstrainRect && target) {
            targetConstrainRect.adjust(-target.width, -target.height, target.width, target.height);
            target.constrainTo(targetConstrainRect);
        }

        // As part of fallback process when fitting within constraints, result may shrink to our minima
        result.minWidth = me.minWidth;
        result.minHeight = me.minHeight;

        // We're being commanded to try to align at a position
        if (position) {
            result.moveTo(position.x, position.y);
            if (constrainTo) {
                result.constrainTo(constrainTo);
            }
        }

        // We're aligning to a Target Rectangle within a ConstrainTo Rectangle, taking into account
        // a possible anchor pointer, or x/y offsets. Here's the situation:
        //
        //                             <-- ConstrainTo Rectangle -->
        //  +-----------------------------------+--------------------+-------------------------+
        //  |                                   |                    |                         |
        //  |                                   |                    |                         |
        //  |                                   |                    |                         |
        //  |-----------------------------------+--------------------+-------------------------+
        //  |                                   |          ▼         |                         |
        //  |                                   | +----------------+ |                         |
        //  |                                   | |                | |                         |
        //  |                                   | |                | |                         |
        //  |                                   |▶|     Target     |◀|                         |
        //  |                                   | |                | |                         |
        //  |                                   | |                | |                         |
        //  |                                   | +----------------+ |                         |
        //  |                                   |          ▲         |                         |
        //  +-----------------------------------+--------------------+-------------------------|
        //  |                                   |                    |                         |
        //  |                                   |                    |                         |
        //  |                                   |                    |                         |
        //  +-----------------------------------+--------------------+-------------------------+
        //
        // Which results in the four possible constraint zones above, which we index in standard CSS order.
        //
        // Top    = 0
        // Right  = 1
        // Bottom = 2
        // Left   = 3
        //
        // If the initially requested alignment is not within the constrainTo rectangle
        // then, calculate these four, and then loop through them, beginning at the requested one,
        // quitting when we find a position which does not violate constraints. This includes
        // shrinking the aligning Rectangle towards its minima to attempt a fit.
        //
        // The final fallback, if there is no position which does not violate constraints
        // is to position in whichever of the four rectangles has the largest area shrinking overflowing
        // dimensions down to minima if specified.
        //
        else {
            // Offsets: If we are using an anchor to move away from the target, use anchor height in both dimensions.
            // It's rotated so that "height" always has the same meaning. It's the height of the arrow.
            const offsets     = anchorSize ? [anchorSize[1] + targetOffsets[0], anchorSize[1] + targetOffsets[1]] : targetOffsets,
                targetPoint = target.getAlignmentPoint(alignSpec.targetAlignmentPoint, offsets),
                myPoint     = result.getAlignmentPoint(alignSpec.myAlignmentPoint);

            result.translate(targetPoint[0] - myPoint[0], targetPoint[1] - myPoint[1]);

            // If an overlapping position was requested, then we are *not* trying out those four zones.
            // We just respect constraint, and that's it.
            const overlap = result.intersect(target, true);

            // If we are aligned over our target, we just obey that within any constraint.
            // No complex edge alignment attempts to fall back to.
            if (overlap) {
                if (constrainTo) {
                    result.constrainTo(constrainTo);
                }
                resultZone = alignSpec.startZone;
            }
            // Aligned to outside of our target, and we need to be constrained
            else if (constrainTo && !constrainTo.contains(result)) {
                let requestedResult = result.clone(),
                    solutions       = [],
                    zone, largestZone;

                // Any configured anchorPosition becomes invalid now that we're having to move the resulting zone
                // to some unpredictable new place where it fits. It will have to be calculated based upon where
                // we end up aligning.
                calculatedAnchorPosition = null;

                // Calculate the four constraint zones illustrated above.
                // Top
                constraintZones[0] = zone = constrainTo.clone();
                zone.bottom = target.y - offsets[1];

                // Right
                constraintZones[1] = zone = constrainTo.clone();
                zone.x = target.right + offsets[0];

                // Bottom
                constraintZones[2] = zone = constrainTo.clone();
                zone.y = target.bottom + offsets[1];

                // Left
                constraintZones[3] = zone = constrainTo.clone();
                zone.right = target.x - offsets[0];

                // Start from the preferred edge and see if we are able to constrain to within each rectangle
                for (let i = 0; i < zoneOrder.length; i++) {
                    // Revert to incoming dimension for fallback out of axisLock
                    if (matchDimension && i == 2) {
                        result[matchDimension] = originalSize;
                    }

                    zone = constraintZones[resultZone = zoneOrder[i].zone];

                    // Perform unconstrained alignment at the calculated alignment for the zone
                    result = result.alignTo({
                        target  : target,
                        offsets : offsets,
                        align   : zoneOrder[i].align
                    });

                    // If we are able to strictly constrain into this area, then it's one of the possible solutions.
                    // We choose the solution which result in the shortest translation from the initial position.
                    if (result.constrainTo(zone, true)) {
                        solutions.push({
                            result : result,
                            zone   : resultZone
                        });

                        // If this successful constraint is at the requested alignment, or at a fallback
                        // alignment which has used min size constraints, then that's the correct solution.
                        // If there's no size compromising, we have to pick the shortest translation.
                        if (!largestZone || result.width < me.width || result.height < me.height) {
                            result.align = zoneOrder[i].align;
                            break;
                        }
                    }

                    // Cache the largest zone we find in case we need the final fallback.
                    if (!largestZone || zone.area > largestZone.area) {
                        const r = result.clone();

                        // And just move the result clone into the edge zone
                        switch (resultZone) {
                            // Top
                            case 0:
                                r.moveTo(null, zone.bottom - r.height);
                                break;
                            // Right
                            case 1:
                                r.moveTo(zone.left);
                                break;
                            // Bottom
                            case 2:
                                r.moveTo(null, zone.top);
                                break;
                            // Left
                            case 3:
                                r.moveTo(zone.right - r.width);
                                break;
                        }

                        largestZone = {
                            area   : zone.area,
                            result : r,
                            zone   : resultZone
                        };
                    }
                }

                // The loop found at least one solution
                if (solutions.length) {
                    // Multiple fallbacks with no axisLock.
                    // Use the solution which resulted in the shortest translation distance from the requested alignment.
                    if (solutions.length > 1 && !axisLock) {
                        solutions.sort((s1, s2) => {
                            let s1TranslationDistance = Math.sqrt((requestedResult.x - s1.result.x) ** 2 + (requestedResult.y - s1.result.y) ** 2),
                                s2TranslationDistance = Math.sqrt((requestedResult.x - s2.result.x) ** 2 + (requestedResult.y - s2.result.y) ** 2);

                            return s1TranslationDistance - s2TranslationDistance;
                        });
                    }
                    // Initial success, or axisLock. Use first successful solution.
                    result = solutions[0].result;
                    resultZone = solutions[0].zone;
                }
                // No solutions found - use the largest rectangle.
                else {
                    result = largestZone.result;
                    resultZone = largestZone.zone;

                    // When we are constraining to the viewport, we must still must be constrained,
                    // even after we've given up making it align *and* constrain.
                    if (constrainingToViewport) {
                        result.constrainTo(constrainTo);
                    }
                }
            }
            else {
                resultZone = alignSpec.startZone;
            }

            result.zone = resultZone;

            // If they included an anchor, calculate its position along its edge.
            // TODO: Handle the edge overlap being less than anchor width.
            if (anchorSize) {
                // If we were passed an anchorPosition, and it has remnained valid (meaning the requested
                // alignment succeeded with no constraint), then anchorPosition will be set. If not,
                // we have to calculate it based upon the aligned edge.
                if (!calculatedAnchorPosition) {
                    let isLeftOrRight = resultZone & 1,
                        start         = isLeftOrRight ? 'y' : 'x',
                        end           = isLeftOrRight ? 'bottom' : 'right',
                        startValue    = Math.max(target[start], result[start]),
                        endValue      = Math.min(target[end], result[end]),
                        anchorStart   = (startValue + (endValue - startValue) / 2 - anchorSize[0] / 2),
                        anchorEnd     = anchorStart + anchorSize[0];

                    if (anchorEnd > result[end]) {
                        anchorStart -= (anchorEnd - result[end]);
                    }
                    if (anchorStart < result[start]) {
                        anchorStart += (result[start] - anchorStart);
                    }

                    // Return an anchor property which will have an x or y property and an edge name onto which the
                    // arrow should be aligned.
                    calculatedAnchorPosition = {
                        [start] : anchorStart - result[start],
                        edge    : edgeNames[(resultZone + 2) % 4]
                    };
                }

                result.anchor = calculatedAnchorPosition;
            }
        }

        return result;
    }

    /**
     * Returns the `[x, y]` position of the specified anchor point of this Rectangle in <edge><offset> format.
     * for example passing "t50" will return the centre point of the top edge, passing "r0" will return the start
     * position of the right edge (the top right corner).
     *
     * Note that the offset defaults to 50, so "t" means the centre of the top edge.
     * @param {String} alignmentPoint The alignment point to calculate. Must match the RegExp `[trbl]\d*`
     * @param {Number[]} margins The `[x, y]` margins to add from the left/right, top/bottom edge.
     */
    getAlignmentPoint(alignmentPoint, margins = zeroOffsets) {
        //<debug>
        if (typeof alignmentPoint !== 'string' || !alignmentPoint.match(alignPointRe)) {
            throw new Error('Alignment point must be of the form /[trblc]\\d*/');
        }
        //</debug>

        const
            me         = this,
            parts      = alignPointRe.exec(alignmentPoint),
            edge       = parts[1].toLowerCase(),
            edgeOffset = Math.min(Math.max(parseInt(parts[2] || 50), 0), 100) / 100;

        switch (edge) {
            case 't':
                return [me.x + me.width * edgeOffset, me.y - margins[1]];
            case 'r':
                return [me.right + margins[0], me.y + me.height * edgeOffset];
            case 'b':
                return [me.x + me.width * edgeOffset, me.bottom + margins[1]];
            case 'l':
                return [me.x - margins[0], me.y + me.height * edgeOffset];
            case 'c':
                const center = me.center;
                return [center.x + margins[0], center.y + margins[1]];
        }
    }

    /**
     * Highlights this Rectangle using the highlighting effect of {@link Core.helper.DomHelper}
     * on a transient element which encapsulates the region's area.
     */
    highlight() {
        const
            me               = this,
            highlightElement = DomHelper.createElement({
                parent : document.body,
                style  : `position:absolute;z-index:9999999;pointer-events:none;
                            left:${me.x}px;top:${me.y}px;width:${me.width}px;height:${me.height}px`
            });

        return DomHelper.highlight(highlightElement).then(() => highlightElement.remove());
    }

    //<debug>
    show(duration = 5000) {
        let me = this;

        // Passing an element or Rectangle means it's relative to that.
        // For example an event in a scheduler.
        const relativeTo = duration instanceof Rectangle ? duration : (duration.nodeType === 1 ? Rectangle.from(duration) : null);
        if (relativeTo) {
            duration = arguments[1];
            me = me.clone();
            me.translate(relativeTo.x, relativeTo.y);
        }
        const color            = typeof duration === 'string' ? duration : '#52a0db',
            highlightElement = DomHelper.createElement({
                parent : document.body,
                style  : `background-color:${color};opacity:0.4;position:absolute;z-index:9999999;pointer-events:none;
                            left:${me.x}px;top:${me.y}px;width:${me.width}px;height:${me.height}px`
            }),
            unhighlight      = () => document.body.removeChild(highlightElement);

        setTimeout(unhighlight, typeof duration === 'number' ? duration : 5000);

        return highlightElement;
    }

    //</debug>
}
