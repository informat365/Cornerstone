import Base from '../../../Core/Base.js';
/**
 * @module Grid/view/mixin/GridResponsive
 */

/**
 * Simplifies making grid responsive. Supply levels as {@link #config-responsiveLevels} config, default levels are:
 * <dl>
 * <dt>small <dd>< 400px,
 * <dt>medium <dd>< 600px
 * <dt>large <dd>> 600px
 * </dl>
 *
 * Columns can define configs per level to be resized etc:
 *
 * ```
 * let grid = new Grid({
 *   responsiveLevels: {
 *     small: 300,
 *     medium: 400,
 *     large: '*' // everything above 400
 *   },
 *
 *   columns: [
 *     {
 *       field: 'name',
 *       text: 'Name',
 *       responsiveLevels: {
 *         small: { hidden: true },
 *         '*': { hidden: false } // all other levels
 *       }
 *     },
 *     { field: 'xx', ... }
 *   ]
 * });
 * ```
 *
 * It is also possible to give a [Grid state](#Grid/view/mixin/GridState) object instead of a level width, but in that
 * case the object must contain a `levelWidth` property:
 *
 * ```
 * let grid = new Grid({
 *   responsiveLevels: {
 *     small: {
 *       // Width is required
 *       levelWidth : 400,
 *       // Other configs are optional, see GridState for available options
 *       rowHeight  : 30
 *     },
 *     medium : {
 *       levelWidth : 600,
 *       rowHeight  : 40
 *     },
 *     large: {
 *       levelWidth : '*', // everything above 300
 *       rowHeight  : 45
 *     }
 *   }
 * });
 * ```
 *
 * @demo Grid/responsive
 * @externalexample grid/Responsive.js
 * @mixin
 */
export default Target => class GridResponsive extends (Target || Base) {
    static get defaultConfig() {
        return {
            /**
             * "Break points" for which responsive config to use for columns and css.
             * @config {Object}
             * @category Misc
             * @default <code>{ small : 400, medium : 600, large : '*' }</code>
             */
            responsiveLevels : Object.freeze({
                small  : 400,
                medium : 600,
                large  : '*'
            })
        };
    }

    /**
     * Find closes bigger level, aka level we want to use.
     * @private
     * @category Misc
     */
    getClosestBiggerLevel(width) {
        let me           = this,
            levels       = Object.keys(me.responsiveLevels),
            useLevel     = null,
            minDelta     = 99995,
            biggestLevel = null;

        levels.forEach(level => {
            let levelSize = me.responsiveLevels[level];

            // responsiveLevels can contains config objects, in which case we should use width from it
            if (!['number', 'string'].includes(typeof levelSize)) {
                //<debug>
                if (!('levelWidth' in levelSize)) {
                    throw new Error('levelWidth required when using state config as responsive level');
                }
                //</debug>
                levelSize = levelSize.levelWidth;
            }

            if (levelSize === '*') {
                biggestLevel = level;
            }
            else if (width < levelSize) {
                const delta = levelSize - width;
                if (delta < minDelta) {
                    minDelta = delta;
                    useLevel = level;
                }
            }
        });

        return useLevel || biggestLevel;
    }

    /**
     * Get currently used responsive level (as string)
     * @returns {String}
     * @readonly
     * @category Misc
     */
    get responsiveLevel() {
        return this.getClosestBiggerLevel(this.width);
    }

    /**
     * Check if resize lead to a new responsive level and take appropriate actions
     * @private
     * @fires responsive
     * @param width
     * @param oldWidth
     * @category Misc
     */
    updateResponsive(width, oldWidth) {
        const me       = this,
            oldLevel = me.getClosestBiggerLevel(oldWidth),
            level    = me.getClosestBiggerLevel(width);

        if (oldLevel !== level) {
            // Level might be a state object
            const levelConfig = me.responsiveLevels[level];
            if (!['number', 'string'].includes(typeof levelConfig)) {
                me.applyState(levelConfig);
            }

            // check columns for responsive config
            me.columns.forEach(column => {
                const levels = column.responsiveLevels;
                if (levels) {
                    if (levels[level]) {
                        // using state to apply responsive config, since it already does what we want...
                        column.applyState(levels[level]);
                    }
                    else if (levels['*']) {
                        column.applyState(levels['*']);
                    }
                }
            });

            me.element.classList.remove('b-responsive-' + oldLevel);
            me.element.classList.add('b-responsive-' + level);

            /**
             * Grid resize lead to a new responsive level being applied
             * @event responsive
             * @param {Grid.view.Grid} grid Grid that was resized
             * @param {String} level New responsive level (small, large, etc)
             * @param {Number} width New width in px
             * @param {String} oldLevel Old responsive level
             * @param {Number} oldWidth Old width in px
             */
            me.trigger('responsive', { level, width, oldLevel, oldWidth });
        }
    }

    // This does not need a className on Widgets.
    // Each *Class* which doesn't need 'b-' + constructor.name.toLowerCase() automatically adding
    // to the Widget it's mixed in to should implement thus.
    get widgetClass() {}
};
