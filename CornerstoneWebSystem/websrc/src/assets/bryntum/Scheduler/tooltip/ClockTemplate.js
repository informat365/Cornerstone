import Base from '../../Core/Base.js';
import DateHelper from '../../Core/helper/DateHelper.js';

//import styles from '../../../resources/sass/tooltip/clocktemplate.scss';

/**
 * @module Scheduler/tooltip/ClockTemplate
 */

/**
 * A template showing a clock, it consumes an object containing a date and a text
 * @private
 */
export default class ClockTemplate extends Base {
    static get defaultConfig() {
        return {
            minuteHeight      : 8,
            minuteTop         : 2,
            hourHeight        : 8,
            hourTop           : 2,
            handLeft          : 10,
            timeAxisViewModel : null,
            template          : function(data) {
                return `<div class="b-sch-clockwrap b-sch-clock-${this.mode} ${data.cls || ''}">
                    <div class="b-sch-clock">
                        <div class="b-sch-hour-indicator">${DateHelper.format(data.date, 'MMM')}</div>
                        <div class="b-sch-minute-indicator">${DateHelper.format(data.date, 'D')}</div>
                        <div class="b-sch-clock-dot"></div>
                    </div>
                    <span class="b-sch-clock-text">${data.text}</span>
                </div>`;
            }
        };
    }

    construct(config) {
        super.construct(config);

        this.timeAxisViewModelDetacher = this.timeAxisViewModel.on({
            update  : this.onTimeAxisViewModelUpdate,
            thisObj : this
        });
    }

    doDestroy() {
        this.timeAxisViewModelDetacher && this.timeAxisViewModelDetacher();
    }

    onTimeAxisViewModelUpdate() {
        delete this._mode;
    }

    updateDateIndicator(tipEl, date) {
        const me = this,
            hourIndicatorEl = tipEl.querySelector('.b-sch-hour-indicator'),
            minuteIndicatorEl = tipEl.querySelector('.b-sch-minute-indicator');

        if (date && hourIndicatorEl && minuteIndicatorEl) {
            if (me.mode === 'hour') {
                hourIndicatorEl.style.transform = `rotate(${(date.getHours() % 12) * 30}deg)`;
                minuteIndicatorEl.style.transform = `rotate(${date.getMinutes() * 6}deg)`;
            }
            else {
                hourIndicatorEl.style.transform = 'none';
                minuteIndicatorEl.style.transform = 'none';
            }
        }
    }

    get mode() {
        // 'hour' for a clock view or 'day' for a calendar view
        return this._mode || (this._mode = DateHelper.compareUnits(this.timeAxisViewModel.timeResolution.unit, 'day') >= 0 ? 'day' : 'hour');
    }

    set template(template) {
        this._template = template;
    }

    /**
     * Get the clock template, which accepts an object of format { date, text }
     * @returns {function(*): string}
     */
    get template() {
        return this._template;
    }
}
