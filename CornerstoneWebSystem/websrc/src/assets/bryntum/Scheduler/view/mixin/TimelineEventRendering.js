import Base from '../../../Core/Base.js';
import ObjectHelper from '../../../Core/helper/ObjectHelper.js';

/**
 * @module Scheduler/view/mixin/TimelineEventRendering
 */

/**
 * Functions to handle event rendering (EventModel -> dom elements).
 *
 * @mixin
 */
export default Target => class TimelineEventRendering extends (Target || Base) {
    //region Default config

    static get defaultConfig() {
        return {
            /**
             * Controls how much space to leave between stacked event bars in px
             * @config {Number}
             * @default
             * @category Scheduled events
             */
            barMargin : 10,

            resourceMargin : null,

            /**
             * True to size events based on the rowHeight and barMargin settings. Set this to false if you want to
             * control height and top properties via CSS instead.
             * @config {Boolean}
             * @default
             * @category Scheduled events
             */
            managedEventSizing : true,

            /**
             * The CSS class added to an event/assignment when it is newly created
             * in the UI and unsynced with the server.
             * @config {String}
             * @default
             * @private
             * @category CSS
             */
            generatedIdCls : 'b-sch-dirty-new',

            /**
             * The CSS class added to an event when it has unsaved modifications
             * @config {String}
             * @default
             * @private
             * @category CSS
             */
            dirtyCls : 'b-sch-dirty',

            /**
             * The CSS class added to an event when it is currently committing changes
             * @config {String}
             * @default
             * @private
             * @category CSS
             */
            committingCls : 'b-sch-committing',

            /**
             * The CSS class added to an event/assignment when it ends outside of the visible time range.
             * @config {String}
             * @default
             * @private
             * @category CSS
             */
            endsOutsideViewCls : 'b-sch-event-endsoutside',

            /**
             * The CSS class added to an event/assignment when it starts outside of the visible time range.
             * @config {String}
             * @default
             * @private
             * @category CSS
             */
            startsOutsideViewCls : 'b-sch-event-startsoutside',

            /**
             * The CSS class added to an event/assignment when it is not draggable.
             * @config {String}
             * @default
             * @private
             * @category CSS
             */
            fixedEventCls : 'b-sch-event-fixed',

            /**
             * Event style used by default. Events and resources can specify their own style, with priority order being:
             * Event -> Resource -> Scheduler default. Determines the appearance of the event by assigning a CSS class
             * to it. Available styles are:
             * * plain (default), flat look
             * * border, has border in darker shade of events color
             * * colored, has colored text and wide left border in same color
             * * hollow, only border + text until hovered
             * * line, as a line with the text below it
             * * dashed, as a dashed line with the text below it
             * * minimal, as a thin line with small text above it
             *
             * Specify `null` to not apply a default style and take control using custom CSS (easily overridden basic
             * styling will be used).
             *
             * @config {String}
             * @default
             * @category Scheduled events
             */
            eventStyle : 'plain',

            /**
             * Event color used by default. Events and resources can specify their own color, with priority order being:
             * Event -> Resource -> Scheduler default. Available colors are:
             * * red
             * * pink
             * * purple
             * * violet
             * * indigo
             * * blue
             * * cyan
             * * teal
             * * green
             * * lime
             * * yellow
             * * orange
             * * gray
             *
             * Specify `null` to not apply a default color and take control using custom CSS (an easily overridden color
             * will be used to make sure events are still visible).
             *
             * @config {String}
             * @default
             * @category Scheduled events
             */
            eventColor : 'green'
        };
    }

    //endregion

    //region Settings

    /**
     * Control how much space to leave between stacked event bars in px. Value will be constrained by half the row height.
     * @property {Number}
     * @category Scheduled events
     */
    get barMargin() {
        return this._barMargin;
    }

    set barMargin(margin) {
        const me = this;

        ObjectHelper.assertNumber(margin, 'barMargin');

        // bar margin should not exceed half of the row height
        if (me.isHorizontal && me.rowHeight) {
            margin = Math.min(Math.ceil(me.rowHeight / 2), margin);
        }

        if (me._barMargin !== margin) {
            me._barMargin = margin;
            if (me.rendered) {
                me.currentOrientation.onBeforeRowHeightChange();
                me.refreshWithTransition();
            }
        }
    }

    // Documented in SchedulerEventRendering to not show up in Gantt docs
    get resourceMargin() {
        return this._resourceMargin == null ? this.barMargin : this._resourceMargin;
    }

    set resourceMargin(margin) {
        const me = this;

        ObjectHelper.assertNumber(margin, 'resourceMargin');

        // bar margin should not exceed half of the row height
        if (me.isHorizontal && me.rowHeight) {
            margin = Math.min(Math.ceil(me.rowHeight / 2), margin);
        }

        if (me._resourceMargin !== margin) {
            me._resourceMargin = margin;
            if (me.rendered) {
                me.currentOrientation.onBeforeRowHeightChange();
                me.refreshWithTransition();
            }
        }
    }

    // /**
    //  * Distance between bars when using eventLayout pack
    //  * @property {Number}
    //  */
    // get barPackMargin() {
    //     return this._barPackMargin;
    // }
    //
    // set barPackMargin(margin) {
    //     if (this._barPackMargin !== margin) {
    //         this._barPackMargin = margin;
    //         this.refresh();
    //     }
    // }

    /**
     * Get/set the widths of all the time columns to the supplied value. Only applicable when {@link Scheduler.view.TimelineBase#config-forceFit} is set to false.
     * Deprecated in favor of {@link #property-tickSize}
     * @property {Number}
     * @deprecated 2.2
     * @category Scheduled events
     */
    set tickWidth(width) {
        this.tickSize = width;
    }

    get tickWidth() {
        return this.tickSize;
    }

    /**
     * Get/set the width/height (depending on mode) of all the time columns to the supplied value. Only applicable when
     * {@link Scheduler.view.TimelineBase#config-forceFit} is set to false.
     * @property {Number}
     * @category Scheduled events
     */
    set tickSize(width) {
        ObjectHelper.assertNumber(width, 'tickSize');

        this.timeAxisViewModel.tickSize = width;
    }

    get tickSize() {
        return this.timeAxisViewModel.tickSize;
    }

    /**
     * Predefined event colors, useful in combos etc.
     * @type {String}
     * @category Scheduled events
     */
    static get eventColors() {
        return ['red', 'pink', 'purple', 'violet', 'indigo', 'blue', 'cyan', 'teal', 'green', 'lime', 'yellow', 'orange', 'gray'];
    }

    /**
     * Predefined event styles , useful in combos etc.
     * @type {String}
     * @category Scheduled events
     */
    static get eventStyles() {
        return ['plain', 'border', 'hollow', 'colored', 'line', 'dashed', 'minimal'];
    }

    //endregion

    // This does not need a className on Widgets.
    // Each *Class* which doesn't need 'b-' + constructor.name.toLowerCase() automatically adding
    // to the Widget it's mixed in to should implement thus.
    get widgetClass() {}
};
