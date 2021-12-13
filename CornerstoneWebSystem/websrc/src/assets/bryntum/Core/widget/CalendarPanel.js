import Panel from './Panel.js';
import DateHelper from '../helper/DateHelper.js';
import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';
import Month from '../util/Month.js';
import Tooltip from './Tooltip.js';
import LocaleManager from '../localization/LocaleManager.js';

/**
 * @module Core/widget/CalendarPanel
 */

/**
 * A Panel which can display a month of date cells.
 *
 * This is a base class for UI widgets which need to make use of a calendar layout
 * and should not be used directly.
 */
export default class CalendarPanel extends Panel {
    static get $name() {
        return 'CalendarPanel';
    }

    static get defaultConfig() {
        return {
            /**
             * The week start day, 0 meaning Sunday, 6 meaning Saturday.
             * Defaults to the locale's week start day.
             * @config {Number}
             */
            weekStartDay : null,

            /**
             * Configure as `true` to always show a six week calendar.
             * @config {Boolean}
             * @default
             */
            sixWeeks : true,

            /**
             * Configure as `true` to show a week number column at the start of the calendar block.
             * @config {Boolean}
             */
            showWeekNumber : false,

            /**
             * Either an array of `Date` objects which are to be disabled, or
             * a function, which, when passed a `Date` returns `true` if the
             * date is disabled.
             * @config {Function|Date[]}
             */
            disabledDates : null,

            /**
             * A function which creates content in, and may mutate a day header element.
             * The following parameters are passed:
             *  - cell [HTMLElement](https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement) The header element.
             *  - day [Number](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number) The day number conforming to the specified {@link #config-weekStartDay}. Will be in the range 0 to 6.
             * @config {Function}
             */
            headerRenderer : null,

            /**
             * A function which creates content in, and may mutate the week cell element at the start of a week row.
             * The following parameters are passed:
             *  - cell [HTMLElement](https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement) The header element.
             *  - week [Number[]](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number) An array containing `[year, weekNumber]`.
             * @config {Function}
             */
            weekRenderer : null,

            /**
             * A function which creates content in, and may mutate a day cell element.
             * The following parameters are passed:
             *  - cell [HTMLElement](https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement) The header element.
             *  - date [Date](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date) The date for the cell.
             * @config {Function}
             */
            cellRenderer : null,

            /**
             * Configure as `true` to render weekends as {@link #config-disabledDates}.
             * @config {Boolean}
             */
            disableWeekends : null,

            /**
             * A config object to create a tooltip which will show on hover of a date cell
             * including disabled, weekend, and "other month" cells.
             *
             * It is the developer's responsibility to hook the `beforeshow` event
             * to either veto the show by returning `false` or provide contextual
             * content for the date.
             *
             * The tip instance will be primed with a `currentDate` property.
             * @config {Object}
             */
            tip : null,

            /**
             * The class name to add to calendar cells.
             * @config {String}
             * @private
             */
            cellCls : 'b-calendar-cell',

            /**
             * The class name to add to disabled calendar cells.
             * @config {String}
             * @private
             */
            disabledCls : 'b-disabled-date',

            /**
             * The class name to add to calendar cells which are in the previous or next month.
             * @config {String}
             * @private
             */
            otherMonthCls : 'b-other-month',

            /**
             * The class name to add to calendar cells which are weekend dates.
             * @config {String}
             * @private
             */
            weekendCls : 'b-weekend',

            /**
             * The class name to add to the calendar cell which contains today's date.
             * @config {String}
             * @private
             */
            todayCls : 'b-today'
        };
    }

    construct(config) {
        LocaleManager.on({
            locale : calculateDayNames
        });
        calculateDayNames();
        super.construct(config);
        this.refresh();
    }

    doDestroy() {
        this.tip && this.tip.destroy();

        super.doDestroy();
    }

    ingestDate(date) {
        if (!(date instanceof Date)) {
            date = new Date(date);
            if (isNaN(date)) {
                throw new Error('CalendarPanel date ingestion must be passed a Date, or a valid argument to the Date constructor');
            }
            // Edge parses YYYY-MM-DD as UTC, not local, so in other locales, the value
            // may end up not being 00:00:00 in the date, so remove any time zone offset.
            const tzo = date.getTimezoneOffset();
            if (tzo) {
                date = DateHelper.add(date, tzo, 'minute');
            }
        }
        return DateHelper.clearTime(date);
    }

    set tip(tip) {
        this._tip = new Tooltip(Object.assign({
            forElement  : this.element,
            forSelector : '.b-calendar-cell'
        }, tip));
        this._tip.on({
            pointerOver : 'onTipOverCell',
            thisObj     : this
        });
    }

    get tip() {
        return this._tip;
    }

    get element() {
        return super.element;
    }

    set element(element) {
        const me = this;

        super.element = element;
        me.weekElements = Array.from(me.element.querySelectorAll('.b-calendar-week'));
        me.cellElements = Array.from(me.element.querySelectorAll('.b-calendar-week > div'));
    }

    /**
     * The date which this CalendarPanel encapsulates. Setting this causes the
     * content to be refreshed.
     * @property {Date}
     */
    set date(date) {
        const me = this;

        date = me._date = me.ingestDate(date);
        if (!me.month || me.month.month !== date.getMonth()) {
            me.month = new Month({
                date,
                weekStartDay : me.weekStartDay,
                sixWeeks     : me.sixWeeks
            });
        }
        me.refresh();
    }

    get date() {
        return this._date;
    }

    set month(month) {
        if (month instanceof Month) {
            this._month = month;
        }
        else {
            this.month.month = month;
            this.refresh();
        }
    }

    get month() {
        return this._month;
    }

    set year(year) {
        this.month.year = year;
        this.refresh();
    }

    get year() {
        return this.month.year;
    }

    set showWeekNumber(showWeekNumber) {
        const me = this;

        me.element.classList[showWeekNumber ? 'add' : 'remove']('b-show-week-number');
        if (me.floating) {
            // Must realign because content change might change dimensions
            if (!me.isAligning) {
                me.realign();
            }
        }
    }

    refresh() {
        const me = this,
            today = DateHelper.clearTime(new Date()),
            { cellElements, weekElements, date, month, cellCls, disabledCls, otherMonthCls, weekendCls, todayCls } = me;

        // If we have not been initialized with a current date, use today
        if (!date) {
            return me.date = today;
        }

        // Clear all content and CSS
        for (let i = 0, len = cellElements.length; i < len; i++) {
            cellElements[i].className = cellElements[i].innerHTML = '';
        }

        for (let i = 0; i < 7; i++) {
            const cell = me.weekdayCells[i];

            cell.className = cell.innerHTML = '';
            if (me.headerRenderer) {
                me.headerRenderer(cell, i);
            }
            else {
                cell.innerHTML = shortDayNames[me.canonicalDayNumbers[i]];
            }
            cell.classList.add('b-calendar-day-header');
        }

        // Create cell content
        let weekIndex = 0,
            cellIndex = 0;

        month.eachWeek((week, dates) => {
            const weekElement = weekElements[weekIndex],
                weekCells = weekElement.children;

            weekCells[0].className = 'b-week-number-cell';
            if (me.weekRenderer) {
                me.weekRenderer(weekCells[0], week);
            }
            else {
                weekCells[0].innerHTML = week[1];
            }

            for (let i = 0; i < 7; i++) {
                const cellDate = dates[i],
                    cellDay = cellDate.getDay(),
                    cell = weekCells[i + 1],
                    cellClassList = cell.classList;

                cellClassList.add(cellCls);
                if (me.isDisabledDate(cellDate)) {
                    cellClassList.add(disabledCls);
                }
                if (cellDate.getMonth() !== month.month) {
                    cellClassList.add(otherMonthCls);
                }
                if (cellDay === 0 || cellDay === 6) {
                    cellClassList.add(weekendCls);
                }
                if (cellDate.getTime() === today.getTime()) {
                    cellClassList.add(todayCls);
                }
                cell.dataset.date = DateHelper.format(cellDate, 'YYYY-MM-DD');
                cell.dataset.cellIndex = cellIndex;

                if (me.cellRenderer) {
                    me.cellRenderer(cell, cellDate);
                }
                else {
                    cell.innerHTML = cellDate.getDate();
                }
                cellIndex++;
            }

            weekIndex++;
        });
        if (me.floating) {
            // Must realign because content change might change dimensions
            if (!me.isAligning) {
                me.realign();
            }
        }
    }

    isDisabledDate(date) {
        const day = date.getDay(),
            disabledDates = this.disabledDates;

        if (this.disableWeekends && (day === 0 || day === 6)) {
            return true;
        }

        if (disabledDates) {
            if (typeof disabledDates === 'function') {
                return disabledDates(date);
            }
            if (Array.isArray(disabledDates)) {
                return disabledDates.some(d => DateHelper.clearDate(d, true).getTime() === DateHelper.clearDate(date, true).getTime());
            }
        }
    }

    get bodyConfig() {
        const result = super.bodyConfig,
            weeksContainerChildren = [];

        result.children = [{
            tag       : 'div',
            className : 'b-calendar-row b-calendar-weekdays',
            children  : this.dayNameCells
        }, {
            className : 'b-weeks-container',
            reference : 'weeksElement',
            children  : weeksContainerChildren
        }];
        for (let i = 0; i < 6; i++) {
            let weekRow = {
                className : 'b-calendar-row b-calendar-week',
                children  : []
            };
            // Generate cells for week number plus seven day cells
            for (let j = 0; j < 8; j++) {
                weekRow.children.push({});
            }
            weeksContainerChildren.push(weekRow);
        }

        return result;
    }

    /**
     * Set to 0 for Sunday (the default), 1 for Monday etc.
     */
    set weekStartDay(weekStartDay) {
        const me = this;
        me.dayNames = [];
        me.dayNumbers = [];
        me.canonicalDayNumbers = [];
        me._weekStartDay = weekStartDay != null ? weekStartDay : DateHelper.weekStartDay;

        // So, if they set weekStartDay to 1 meaning Monday which is ISO standard, we will
        // have mapping of internal day number to canonical day number (as used by Date class)
        // and to abbreviated day name like this:
        // canonicalDayNumbers = [1, 2, 3, 4, 5, 6, 0] // Use for translation from our day number to Date class's day number
        // dayNumbers          = [6, 0, 1, 2, 3, 4, 5] // Use for translation from Date object's day number to ours
        // dayNames            = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        for (let i = 0; i < 7; i++) {
            const canonicalDay = (me._weekStartDay + i) % 7;
            me.canonicalDayNumbers[i] = canonicalDay;
            me.dayNumbers[canonicalDay] = i;
            me.dayNames[i] = shortDayNames[canonicalDay];
        }
    }

    get weekStartDay() {
        if (!this.hasOwnProperty('_weekStartDay')) {
            this.weekStartDay = undefined;
        }
        return this._weekStartDay;
    }

    get dayNameCells() {
        const me = this,
            weekNumberHeader = document.createElement('div'),
            result = [weekNumberHeader],
            weekdayCells = me.weekdayCells = [];

        weekNumberHeader.className = 'b-week-number-cell';

        // Ensure our week is initialized by set weekStartDay
        me._thisIsAUsedExpression(me.weekStartDay);

        for (let i = 0; i < 7; i++) {
            const cell = document.createElement('div');
            result.push(cell);
            weekdayCells.push(cell);
        }
        return result;
    }

    onTipOverCell({ source : tip, target }) {
        tip.date = DateHelper.parse(target.dataset.date, 'YYYY-MM-DD');
    }
}

function calculateDayNames() {
    shortDayNames.length = 0;
    for (let date = 2; date < 9; date++) {
        d.setDate(date);
        shortDayNames.push(DateHelper.format(d, 'ddd'));
    }
}

// In the Date class, 0=Sunday, 6=Saturday. 2nd Jan 2000 is Sunday.
// Collect local shortDayNames in default order.
const d = new Date('2000-01-01T00:00:00'),
    shortDayNames = [];

BryntumWidgetAdapterRegister.register('calendarpanel', CalendarPanel);
