import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import GridFeatureManager from '../../Grid/feature/GridFeatureManager.js';
import DateHelper from '../../Core/helper/DateHelper.js';
import DateField from '../../Core/widget/DateField.js';

/**
 * @module Scheduler/feature/HeaderContextMenu
 */

/**
 * Adds scheduler specific menu items to the timeline header context menu.
 * By default the menu has the following items:
 * * Filter tasks (if {@link Scheduler.feature.EventFilter EventFilter} is enabled)
 * * Zoom
 * * Date range
 * Can be populated by other features.
 *
 *  * To add extra items to the menu:
 *
 * ```javascript
 * const scheduler = new Scheduler({
 *     features : {
 *         headerContextMenu : {
 *             // Extra items
 *             extraItems : [
 *                 {
 *                     text : 'Extra',
 *                     icon : 'b-fa b-fa-fw b-fa-flag',
 *                     onItem() {
 *                         ...
 *                     }
 *                 }
 *             ]
 *         }
 *     }
 * });
 * ```
 *
 * Manipulate existing menu items in the timeaxis header menu:
 *
 * ```javascript
 * const scheduler = new Scheduler({
 *     features : {
 *         headerContextMenu : {
 *             // Process items before menu is shown
 *             processItems({ items }) {
 *                  // Push an extra item
 *                 items.push({
 *                     text : 'Cool action',
 *                     onItem() {
 *                           // ...
 *                     }
 *                  }
 *             }
 *         }
 *     }
 * });
 * ```
 *
 * To disable header context menu for locked grid, but leave it for normal grid:
 *
 * ```javascript
 * const scheduler = new Scheduler({
 *     features : {
 *         contextMenu : {
 *             // to disable menu for headers except timeline header
 *             processHeaderItems : ({ column }) => column instanceof TimeAxisColumn,
 *             // to disable menu for cells
 *             processCellItems   : () => false
 *         }
 *     }
 * });
 * ```
 *
 * This feature is **enabled** by default
 *
 * @extends Core/mixin/InstancePlugin
 * @externalexample scheduler/HeaderContextMenu.js
 * @demo Scheduler/basic
 */
export default class HeaderContextMenu extends InstancePlugin {

    static get $name() {
        return 'HeaderContextMenu';
    }

    static get defaultConfig() {
        return {
            /**
             * An array of additional items to add to the menu
             * @config {Object[]}
             * @default
             */
            extraItems : null,

            /**
             * A function called before displaying the menu that allows manipulations of its items. Called with a
             * single parameter with format { eventRecord, resourceRecord, eventElement, items }.
             *
             * ```javascript
             * features : {
             *     headerContextMenu : {
             *         processItems({ items }) {
             *             // Add or remove items here as needed
             *             items.push({ text: 'Some action', icon : 'b-fa b-fa-fw b-fa-ban' })
             *         }
             *     }
             * }
             * ```
             *
             * @config {Function}
             */
            processItems : null
        };
    }

    // Plugin configuration. This plugin chains some of the functions in Grid.
    static get pluginConfig() {
        return {
            chain : ['getHeaderMenuItems']
        };
    }

    construct(scheduler, config) {
        super.construct(scheduler, config);

        this.scheduler = scheduler;
    }

    /**
     * Populates the header context menu items.
     * @param column Column for which the menu will be shown
     * @param items Array of menu items
     * @internal
     */
    getHeaderMenuItems(column, items) {
        const
            me            = this,
            { scheduler, processItems } = me,
            { timeAxis }  = scheduler,
            dateStep      = {
                magnitude : timeAxis.shiftIncrement,
                unit      : timeAxis.shiftUnit
            };

        if (column.type !== 'timeAxis')  {
            return;
        }

        items.push({
            text     : me.L('pickZoomLevel'),
            icon     : 'b-fw-icon b-icon-search-plus',
            disabled : !scheduler.presets.count || me.disabled,
            menu     : {
                type  : 'popup',
                items : [{
                    type      : 'slider',
                    showValue : false,
                    listeners : {
                        input   : me.onZoomSliderChange,
                        thisObj : me
                    },
                    // set width for IE11
                    minWidth : 130
                }],
                onBeforeShow({ source : menu }) {
                    const [zoom] = menu.items;

                    zoom.min   = scheduler.minZoomLevel;
                    zoom.max   = scheduler.maxZoomLevel;
                    zoom.value = scheduler.zoomLevel;
                }
            }
        });

        me.startDateField = new DateField({
            label      : me.L('startText'),
            labelWidth : '6em',
            required   : true,
            step       : dateStep,
            listeners  : {
                change  : me.onRangeDateFieldChange,
                thisObj : me
            }
        });

        me.endDateField = new DateField({
            label      : me.L('endText'),
            labelWidth : '6em',
            required   : true,
            step       : dateStep,
            listeners  : {
                change  : me.onRangeDateFieldChange,
                thisObj : me
            }
        });

        items.push({
            text     : me.L('activeDateRange'),
            icon     : 'b-fw-icon b-icon-calendar',
            disabled : me.disabled,
            menu     : {
                type  : 'popup',
                width : '20em',
                items : [
                    me.startDateField,
                    me.endDateField,
                    {
                        type      : 'button',
                        cls       : 'b-left-nav-btn',
                        icon      : 'b-icon b-icon-prev',
                        color     : 'b-blue b-raised',
                        flex      : 1,
                        margin    : 0,
                        listeners : {
                            click   : me.onLeftShiftBtnClick,
                            thisObj : me
                        }
                    },
                    {
                        type      : 'button',
                        cls       : 'b-today-nav-btn',
                        color     : 'b-blue b-raised',
                        text      : me.L('todayText'),
                        flex      : 4,
                        margin    : '0 8',
                        listeners : {
                            click   : me.onTodayBtnClick,
                            thisObj : me
                        }
                    },
                    {
                        type      : 'button',
                        cls       : 'b-right-nav-btn',
                        icon      : 'b-icon b-icon-next',
                        color     : 'b-blue b-raised',
                        flex      : 1,
                        listeners : {
                            click   : me.onRightShiftBtnClick,
                            thisObj : me
                        }
                    }
                ],
                onBeforeShow : () => me.initDates()
            }
        });

        if (me.extraItems) {
            items.push.apply(items, me.extraItems);
        }

        // Allow user to process the items
        if (processItems) {
            processItems({ items });
        }
    }

    onZoomSliderChange({ value }) {
        const menu = this.client.features.contextMenu.currentMenu;

        // Zooming maintains timeline center point by scrolling the newly rerendered timeline to the
        // correct point to maintain the visual center. Temporarily inhibit context menu hide on scroll
        // of its context element.
        menu.scrollAction = 'realign';
        this.scheduler.zoomLevel = value;
        menu.setTimeout({
            fn                : () => menu.scrollAction = 'hide',
            delay             : 100,
            cancelOutstanding : true
        });
    }

    initDates() {
        const me = this;

        me.startDateField.suspendEvents();
        me.endDateField.suspendEvents();

        // The actual scheduler start dates may include time, but our Date field cannot currently handle
        // a time portion and throws it away, so when we need the value from an unchanged field, we need
        // to use the initialValue set from the timeAxis values.
        // Until our DateField can optionally include a time value, this is the solution.
        me.startDateField.value = me.startDateFieldInitialValue = me.scheduler.startDate;
        me.endDateField.value   = me.endDateFieldInitialValue = me.scheduler.endDate;

        me.startDateField.resumeEvents();
        me.endDateField.resumeEvents();
    }

    onRangeDateFieldChange({ source }) {
        const
            me                 = this,
            startDateChanged   = (source === me.startDateField),
            { client }         = me,
            { timeResolution } = client,
            { scrollable }     = client.timeAxisSubGrid,
            startDate          = me.startDateFieldInitialValue && !startDateChanged ? me.startDateFieldInitialValue : me.startDateField.value,
            viewportStartDate  = client.getDateFromX(scrollable.x);

        let endDate = me.endDateFieldInitialValue && startDateChanged ? me.endDateFieldInitialValue : me.endDateField.value;

        // When either of the fields is changed, we no longer use its initialValue from the timeAxis start or end
        // so that gets nulled to indicate that it's unavailable and the real field value is to be used.
        if (startDateChanged) {
            me.startDateFieldInitialValue = null;
        }
        else {
            me.endDateFieldInitialValue = null;
        }

        // Because the start and end dates are exclusive, avoid a zero
        // length time axis by incrementing the end by one tick unit
        // if they are the same.
        if (!(endDate - startDate)) {
            endDate = DateHelper.add(endDate, timeResolution.increment, timeResolution.unit);
        }
        // if start date got bigger than end date set end date to start date plus one tick
        else if (endDate < startDate) {
            endDate = DateHelper.add(startDate, timeResolution.increment, timeResolution.unit);
        }

        me.scheduler.timeAxis.setTimeSpan(startDate, endDate);

        // Keep the visual start time the same
        scrollable.x = client.getCoordinateFromDate(viewportStartDate, true);

        me.initDates();
    }

    onLeftShiftBtnClick() {
        const me = this;

        me.scheduler.timeAxis.shiftPrevious();
        me.initDates();
    }

    onTodayBtnClick() {
        const me = this,
            today = DateHelper.clearTime(new Date());

        me.scheduler.timeAxis.setTimeSpan(today, DateHelper.add(today, 1, 'day'));
        me.initDates();
    }

    onRightShiftBtnClick() {
        const me = this;

        me.scheduler.timeAxis.shiftNext();
        me.initDates();
    }
}

HeaderContextMenu.featureClass = '';

GridFeatureManager.registerFeature(HeaderContextMenu, true, ['Scheduler', 'Gantt']);
