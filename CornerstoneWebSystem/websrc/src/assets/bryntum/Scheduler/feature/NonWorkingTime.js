import AbstractTimeRanges from './AbstractTimeRanges.js';
import Calendar from '../data/Calendar.js';
import GridFeatureManager from '../../Grid/feature/GridFeatureManager.js';
import DateHelper from '../../Core/helper/DateHelper.js';

/**
 * @module Scheduler/feature/NonWorkingTime
 */

/**
 * Feature that allows styling of weekends (and other non working time) by adding timeRanges for those days.
 *
 * This feature is **disabled** by default
 *
 * @extends Scheduler/feature/AbstractTimeRanges
 * @demo Scheduler/configuration
 * @externalexample scheduler/NonWorkingTime.js
 */
export default class NonWorkingTime extends AbstractTimeRanges {
    //region Default config

    static get $name() {
        return 'NonWorkingTime';
    }

    static get defaultConfig() {
        return {
            /**
             * Highlight weekends
             * @config {Boolean}
             * @default
             */
            highlightWeekends : true,

            showHeaderElements : true,

            cls : 'b-sch-nonworkingtime'
        };
    }

    //endregion

    //region Init & destroy

    construct(client, config) {
        const me = this;

        super.construct(client, config);

        if (!me.calendar || !client.calendar) {
            me.calendar = new Calendar();
        }

        me.bindCalendar(me.calendar);
    }

    doDestroy() {
        this.bindCalendar(null);
        super.doDestroy();
    }

    //endregion

    //region Calendar

    bindCalendar(calendar) {
        const me = this;

        if (me.calendarDetacher) me.calendarDetacher();

        if (calendar) {
            me.calendarDetacher = calendar.on({
                change  : me.renderRanges,
                thisObj : me,
                delay   : 1
            });
        }

        me.calendar = calendar;

        me.renderRanges();
    }

    //endregion

    //region Draw

    renderRanges() {
        const me = this;

        if (me.store && !me.store.isDestroyed) {
            const shouldPaint = DateHelper.as(me.client.timeAxis.unit, 1, 'week') >= 1;

            me.store.removeAll(true);

            if (me.calendar && me.highlightWeekends && shouldPaint) {
                me.store.add(me.calendar.getHolidaysRanges(me.client.timeAxis.startDate, me.client.timeAxis.endDate, true), true);
            }
        }

        super.renderRanges();
    }

    //endregion
}

GridFeatureManager.registerFeature(NonWorkingTime, false, 'Scheduler');
