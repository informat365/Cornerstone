import Base from '../../Base.js';

/**
 * @module Core/widget/mixin/Badge
 */

/**
 * Mixin that allows a widget to display a badge (mostly done as css)
 *
 * @example
 * // show badge
 * button.badge = 5;
 *
 * // hide badge
 * button.badge = null;
 *
 * @externalexample widget/Badge.js
 *
 * @mixin
 */
export default Target => class Badge extends (Target || Base) {
    construct(config) {
        super.construct(config);

        // set after we have element to display badge from start
        if (this._badge) this.badge = this._badge;
    }

    /**
     * Initial text to show in badge
     * @config {String} badge
     */

    /**
     * Get/sets and display badge, set to null or empty string to hide
     * @property {String}
     */
    set badge(badge) {
        const { element } = this;

        this._badge = badge;

        if (element) {
            if (badge != null && badge !== '') {
                element.dataset.badge = badge;
                element.classList.add('b-badge');
            }
            else {
                if (element.dataset.badge) delete element.dataset.badge;
                element.classList.remove('b-badge');
            }
        }
    }

    get badge() {
        return this._badge;
    }

    // Each *Class* which doesn't need 'b-' + constructor.name.toLowerCase() automatically adding
    // to the Widget it's mixed in to should implement thus.
    get widgetClass() {
        // If we don't have a badge, our classList doesn't include "b-badge"
        if (this.badge) {
            return 'b-badge';
        }
    }
};
