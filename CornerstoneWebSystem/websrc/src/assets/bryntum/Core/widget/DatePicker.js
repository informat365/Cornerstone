import CalendarPanel from './CalendarPanel.js';
import DateHelper from '../helper/DateHelper.js';
import EventHelper from '../helper/EventHelper.js';
import DomHelper from '../helper/DomHelper.js';
import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';
import Editor from './Editor.js';
import Combo from './Combo.js';
import LocaleManager from '../localization/LocaleManager.js';

/**
 * @module Core/widget/DatePicker
 */

/**
 * A Panel which can display a month of date cells, which navigates between the cells,
 * fires events upon user selection actions, optionally navigates to other months
 * in response to UI gestures, and optionally displays information about each date cell.
 *
 * This class is not intended for use in applications. It is used internally by the
 * {@link Core.widget.DateField} class.
 *
 * @classtype datepicker
 */
export default class DatePicker extends CalendarPanel {
    static get $name() {
        return 'DatePicker';
    }

    static get defaultConfig() {
        return {
            focusable : true,

            tools : {
                prevMonth : {
                    align   : 'start',
                    cls     : 'b-icon b-icon-angle-left',
                    handler : 'gotoPrevMonth'
                },
                nextMonth : {
                    align   : 'end',
                    cls     : 'b-icon b-icon-angle-right',
                    handler : 'gotoNextMonth'
                }
            },

            header : {
                title      : '<div class="b-editable b-datepicker-month" reference="monthElement"></div> <div class="b-editable b-datepicker-year" reference="yearElement"></div>',
                titleAlign : 'center'
            },

            /**
             * The minimum selectable date. Selection of and navigtion to dates prior
             * to this date will not be possible.
             * @config {Date}
             */
            minDate : null,

            /**
             * The maximum selectable date. Selection of and navigtion to dates after
             * this date will not be possible.
             * @config {Date}
             */
            maxDate : null,

            /**
             * The class name to add to the calendar cell whose date which is outside of the
             * {@link #config-minDate}/{@link #config-maxDate} range.
             * @config {String}
             * @private
             */
            outOfRangeCls : 'b-out-of-range',

            /**
             * The class name to add to the currently focused calendar cell.
             * @config {String}
             * @private
             */
            activeCls : 'b-active-date',

            /**
             * The class name to add to selected calendar cells.
             * @config {String}
             * @private
             */
            selectedCls : 'b-selected-date',

            /**
             * By default, disabled dates cannot be navigated to, and they are skipped over
             * during keyboard navigation. Configure this as `true` to enable navigation to
             * disabled dates.
             * @config {Boolean}
             * @default
             */
            focusDisabledDates : null,

            /**
             * Configure as `true` to enable selecting a single date range by selecting a
             * start and end date.
             * @config {Boolean}
             * @default
             */
            multiSelect : false,

            /**
             * By default, the month and year show their editor on hover instead of click.
             * On a touch platform, they both show the editor on tap.
             * @config
             * @default
             */
            editOnHover : true
        };
    }

    /**
     * Fires when a date is selected. If {@link #config-multiSelect} is specified, this
     * will fire upon deselection and selection of dates.
     * @event selectionChange
     * @param {Date[]} selection The selected date. If {@link #config-multiSelect} is specified
     * this may be a two element array specifying start and end dates.
     */

    construct(config) {
        const me = this;

        LocaleManager.on({
            locale  : 'onLocaleChange',
            thisObj : me
        });
        me.selection = [];
        me.refresh = me.createOnFrame(me.refresh);
        super.construct(config);
        me.element.setAttribute('aria-activedescendant', `${me.id}-active-day`);

        EventHelper.on({
            element   : me.element,
            mouseover : 'onPickerMouseover',
            mousedown : 'onPickerMousedown',
            click     : 'onPickerClick',
            keydown   : 'onPickerKeyDown',
            thisObj   : me
        });
    }

    doDestroy() {
        if (this._yearEditor) {
            this._yearEditor.destroy();
        }
        if (this._monthEditor) {
            this._monthEditor.destroy();
        }
        super.doDestroy();
    }

    get focusElement() {
        return this.element;
    }

    eachWidget(fn, deep) {
        const widgets = this.items || [];

        if (this._yearEditor) {
            widgets.unshift(this._yearEditor);
        }
        if (this._monthEditor) {
            widgets.unshift(this._monthEditor);
        }

        for (let i = 0; i < widgets.length; i++) {
            const widget = widgets[i];

            if (fn(widget) === false) {
                return;
            }

            if (deep && widget.eachWidget) {
                widget.eachWidget(fn, deep);
            }
        }
    }

    refresh() {
        const
            me  = this,
            sbw = DomHelper.scrollBarWidth;

        super.refresh();
        me.monthElement.style.minWidth = `calc(${me.maxMonthLength + 1}ch + ${sbw}px)`;
        me.monthElement.innerHTML = DateHelper.format(me.month.date, 'MMMM');
        me.yearElement.style.minWidth = sbw ? `calc(3ch + ${sbw}px` : '7ch';
        me.yearElement.innerHTML = DateHelper.format(me.month.date, 'YYYY');
    }

    cellRenderer(cell, cellDate) {
        const me = this,
            { activeCls, selectedCls } = me,
            cellClassList = cell.classList;

        cell.innerHTML = cellDate.getDate();
        cell.setAttribute('aria-label', DateHelper.format(cellDate, 'MMMM D, YYYY'));

        if (me.isActiveDate(cellDate)) {
            cellClassList.add(activeCls);
            cell.id = `${me.id}-active-day`;
        }
        if (me.isSelectedDate(cellDate)) {
            cellClassList.add(selectedCls);
        }
        if (me.minDate && cellDate < me.minDate) {
            cellClassList.add(me.outOfRangeCls);
        }
        else if (me.maxDate && cellDate > me.maxDate) {
            cellClassList.add(me.outOfRangeCls);
        }
    }

    onPickerMousedown(event) {
        event.preventDefault();
    }

    onPickerMouseover(event) {
        if (this.editOnHover) {
            const editable   = DomHelper.up(event.target, '.b-editable');

            if (editable) {
                return this.onEditGesture(event);
            }
        }
    }

    onPickerClick(event) {
        const
            me         = this,
            { target } = event,
            cell       = DomHelper.up(target, `.${me.cellCls}:not(.${me.disabledCls}):not(.${me.outOfRangeCls})`),
            editable   = DomHelper.up(target, '.b-editable');

        if (cell) {
            return me.onCellClick(event);
        }
        if ((!me.editOnHover || DomHelper.isTouchEvent) && editable) {
            return me.onEditGesture(event);
        }
        if (me._monthEditor && !me._monthEditor.owns(event)) {
            me._monthEditor.cancelEdit();
        }
        if (me._yearEditor && !me._yearEditor.owns(event)) {
            me._yearEditor.cancelEdit();
        }
    }

    onCellClick(event) {
        this.onDateActivate(DateHelper.parse(event.target.dataset.date, 'YYYY-MM-DD'), event);
    }

    onEditGesture(event) {
        const
            me         = this,
            { month }  = me,
            { target } = event;

        if (target === me.monthElement) {
            me.monthEditor.startEdit({
                target,
                value            : month.month,
                fitTargetContent : false,
                hideTarget       : true
            });
        }
        else if (target === me.yearElement) {
            me.yearEditor.minWidth = `calc(50px + ${DomHelper.scrollBarWidth}px)`;
            me.yearEditor.startEdit({
                target,
                value            : month.year,
                fitTargetContent : true,
                hideTarget       : true
            });
        }
    }

    onDateActivate(date, event) {
        const me = this,
            { lastClickedDate, selection } = me;

        me.activeDate = date;
        me.lastClickedDate = date;

        // Handle multi selecting.
        // * single contiguous date range, eg: an event start and end
        // * multiple discontiguous ranges
        if (me.multiSelect) {
            if (me.multiRange) {
                // TODO: multiple date ranges
            }
            else if (!lastClickedDate || date.getTime() !== lastClickedDate.getTime()) {
                if (lastClickedDate && event.shiftKey) {
                    selection[1] = date;
                    selection.sort();
                }
                else {
                    selection.length = 0;
                    selection[0] = date;
                }

                me.trigger('selectionChange', {
                    selection
                });
            }
        }
        else {
            if (!me.value || me.value.getTime() !== date.getTime()) {
                me.value = date;
            }
            else {
                me.hide();
            }
        }
    }

    onPickerKeyDown(keyEvent) {
        const
            me = this,
            keyName = keyEvent.key.trim() || keyEvent.code,
            activeDate = me.activeDate,
            newDate = new Date(activeDate);

        if (activeDate) {
            do {
                switch (keyName) {
                    case 'Escape':
                        me.hide();
                        break;
                    case 'ArrowLeft':
                        // Disable browser use of this key.
                        // Ctrl+ArrowLeft navigates back.
                        // ArrowLeft scrolls if there is horizontal scroll.
                        keyEvent.preventDefault();

                        if (keyEvent.ctrlKey) {
                            newDate.setMonth(newDate.getMonth() - 1);
                        }
                        else {
                            newDate.setDate(newDate.getDate() - 1);
                        }
                        break;
                    case 'ArrowUp':
                        // Disable browser use of this key.
                        // ArrowUp scrolls if there is vertical scroll.
                        keyEvent.preventDefault();

                        newDate.setDate(newDate.getDate() - 7);
                        break;
                    case 'ArrowRight':
                        // Disable browser use of this key.
                        // Ctrl+ArrowRight navigates forwards.
                        // ArrowRight scrolls if there is horizontal scroll.
                        keyEvent.preventDefault();

                        if (keyEvent.ctrlKey) {
                            newDate.setMonth(newDate.getMonth() + 1);
                        }
                        else {
                            newDate.setDate(newDate.getDate() + 1);
                        }
                        break;
                    case 'ArrowDown':
                        // Disable browser use of this key.
                        // ArrowDown scrolls if there is vertical scroll.
                        keyEvent.preventDefault();

                        newDate.setDate(newDate.getDate() + 7);
                        break;
                    case 'Enter':
                        me.onDateActivate(activeDate, keyEvent);
                        break;
                }
            } while (me.isDisabledDate(newDate) && !me.focusDisabledDates);

            // Don't allow navigation to outside of date bounds.
            if (me.minDate && newDate < me.minDate) {
                return;
            }
            if (me.maxDate && newDate > me.maxDate) {
                return;
            }
            me.activeDate = newDate;
        }
    }

    set minDate(minDate) {
        this._minDate = minDate ? this.ingestDate(minDate) : null;
        this.refresh();
    }

    get minDate() {
        return this._minDate;
    }

    set maxDate(maxDate) {
        this._maxDate = maxDate ? this.ingestDate(maxDate) : null;
        this.refresh();
    }

    get maxDate() {
        return this._maxDate;
    }

    set activeDate(activeDate) {
        const me = this;

        if (activeDate) {
            me._activeDate = me.ingestDate(activeDate);
        }
        else {
            me._activeDate = DateHelper.clearTime(new Date());
        }

        // New active date is in another month
        if (me.month.month !== me._activeDate.getMonth()) {
            me.month.date = me._activeDate;
        }
        me.refresh();
    }

    get activeDate() {
        return this._activeDate;
    }

    set value(date) {
        const me = this,
            { selection } = me;

        let changed;

        if (date) {
            date = me.ingestDate(date);
            if (!me.value || date.getTime() !== me.value.getTime()) {
                selection.length = 0;
                selection[0] = date;
                changed = true;
            }
            me.date = date;
        }
        else {
            changed = selection.length;
            selection.length = 0;

            // Clearing the value - go to today's calendar
            me.date = new Date();
        }

        if (changed) {
            me.trigger('selectionChange', {
                selection
            });
        }
    }

    get value() {
        return this.selection[this.selection.length - 1];
    }

    gotoPrevMonth() {
        const date = this.date;

        date.setMonth(date.getMonth() - 1);
        this.date = date;
    }

    gotoNextMonth() {
        const date = this.date;

        date.setMonth(date.getMonth() + 1);
        this.date = date;
    }

    isActiveDate(date) {
        return this.activeDate && this.ingestDate(date).getTime() === this.activeDate.getTime();
    }

    isSelectedDate(date) {
        return this.selection.some(d => d.getTime() === date.getTime());
    }

    get monthEditor() {
        const me = this;

        if (!me._monthEditor) {
            me._monthEditor = new Editor({
                owner      : me,
                appendTo   : me.element,
                inputField : me.monthInput = new Combo({
                    editable                : false,
                    autoExpand              : !me.editOnHover,
                    items                   : me.monthItems,
                    highlightExternalChange : false,
                    picker                  : {
                        align : {
                            align : 't0-b0'
                        },
                        cls        : 'b-month-picker-list',
                        scrollable : {
                            overflowX : false
                        }
                    }
                }),
                completeOnChange : true,
                listeners        : {
                    complete : 'onMonthPicked',
                    thisObj  : me
                }
            });
        }

        return me._monthEditor;
    }

    onMonthPicked({ value }) {
        this.month = value;
    }

    get yearEditor() {
        const me = this;

        if (!me._yearEditor) {
            me._yearEditor = new Editor({
                owner      : me,
                appendTo   : me.element,
                inputField : me.yearInput = new Combo({
                    editable                : false,
                    autoExpand              : !me.editOnHover,
                    items                   : me.yearItems,
                    highlightExternalChange : false,
                    picker                  : {
                        cls        : 'b-year-picker-list',
                        scrollable : {
                            overflowX : false
                        }
                    }
                }),
                completeOnChange : true,
                listeners        : {
                    complete : 'onYearPicked',
                    thisObj  : me
                }
            });
        }

        return me._yearEditor;
    }

    onYearPicked({ value }) {
        this.year = value;
    }

    get monthItems() {
        return DateHelper.getMonthNames().map((m, i) => [i, m]);
    }

    get yearItems() {
        const
            result = [],
            middle = new Date().getFullYear();

        for (let y = middle - 20; y < middle + 21; y++) {
            result.push(y);
        }

        return result;
    }

    get maxMonthLength() {
        if (!this._maxMonthLength) {
            this._maxMonthLength = 0;

            for (let i = 0, months = this.monthItems; i < 12; i++) {
                this._maxMonthLength = Math.max(this._maxMonthLength, months[i][1].length);
            }
        }

        return this._maxMonthLength;
    }

    onLocaleChange() {
        if (this._monthEditor) {
            this._monthEditor.doDestroy();
            this._monthEditor = null;
        }
        if (this._yearEditor) {
            this._yearEditor.doDestroy();
            this._yearEditor = null;
        }
        this._maxMonthLength = 0;
    }
}

BryntumWidgetAdapterRegister.register('datepicker', DatePicker);
