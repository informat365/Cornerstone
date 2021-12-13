import Rectangle from './Rectangle.js';

/**
 * @module Core/helper/util/Point
 */

/**
 * Encapsulates an X,Y coordinate point.
 * @extends Core/helper/util/Rectangle
 */
export default class Point extends Rectangle {
    /**
     * Creates a new Point encapsulting the event's page position.
     * @param {Event} event
     * @typings ignore
     */
    static from(event) {
        const touchPoints = event.changedTouches;

        return new Point(touchPoints ? touchPoints[0].screenX : event.screenX, touchPoints ? touchPoints[0].screenY : event.pageY);
    }

    /**
     * Constructs a Point
     * @param x The X coordinate
     * @param y The Y coordinate
     */
    constructor(x, y) {
        super(x, y, 0, 0);
    }

    /**
     * Coerces this Point to be within the passed Rectangle. Translates it into the bounds.
     * @param {Core.helper.util.Rectangle} into The Rectangle into which to coerce this Point.
     */
    constrain(into) {
        this.x = Math.min(Math.max(this.x, into.x), into.right - 1);
        this.y = Math.min(Math.max(this.y, into.y), into.bottom - 1);
        return  this;
    }

    toArray() {
        return [this.x, this.y];
    }
}

// The Rectangle class uses the Point class, but cannot import it.
// TODO: find a better way of getting a reference to the Point class in Rectangle.

// #8224 - Gantt angular demo doesn't work in production
// eslint-disable-next-line no-proto
Point.__proto__.Point = Point;
