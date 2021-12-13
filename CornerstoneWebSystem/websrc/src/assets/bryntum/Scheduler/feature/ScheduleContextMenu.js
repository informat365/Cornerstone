import TimeSpanRecordContextMenuBase from '../../Scheduler/feature/base/TimeSpanRecordContextMenuBase.js';
import GridFeatureManager from '../../Grid/feature/GridFeatureManager.js';

/**
 * @module Scheduler/feature/ScheduleContextMenu
 */

/**
 * Displays a context menu for empty parts of the schedule. Items are populated in the first place
 * by configurations of this Feature, then by other features and/or application code.
 *
 * To add extra items (Array notation):
 *
 * ```javascript
 * const scheduler = new Scheduler({
 *     features : {
 *         scheduleContextMenu : {
 *             // Extra items for all events
 *             items : [
 *                 {
 *                     text : 'Extra',
 *                     icon : 'b-fa b-fa-fw b-fa-flag',
 *                     onItem({date, resourceRecord, items}) {
 *                         // Custom date based action
 *                     }
 *                 }
 *             ]
 *         }
 *     }
 * });
 * ```
 *
 * To add extra items (Object notation):
 *
 * ```javascript
 * const scheduler = new Scheduler({
 *     features : {
 *         scheduleContextMenu : {
 *             // Extra items for all events
 *             items : {
 *                 extraItem : {
 *                     text : 'Extra',
 *                     icon : 'b-fa b-fa-fw b-fa-flag',
 *                     onItem({date, resourceRecord, items}) {
 *                         // Custom date based action
 *                     }
 *                 }
 *             }
 *         }
 *     }
 * });
 * ```
 *
 * To remove existing items:
 *
 * ```javascript
 * const scheduler = new Scheduler({
 *     features : {
 *         scheduleContextMenu : {
 *             items : {
 *                 addEvent : false
 *             }
 *         }
 *     }
 * });
 * ```
 *
 * Manipulate existing menu items:
 *
 * ```javascript
 * const scheduler = new Scheduler({
 *     features : {
 *         scheduleContextMenu : {
 *             // Process items before menu is shown
 *             processItems({date, resourceRecord, items}) {
 *                  // Push an extra item for ancient times
 *                  if (date < new Date(2018, 11, 17)) {
 *                      items.modernize = {
 *                          text : 'Modernize',
 *                          ontItem({date}) {
 *                              // Custom date based action
 *                          }
 *                      };
 *                  }
 *
 *                  // Do not show menu for Sundays
 *                  if (date.getDay() === 0) {
 *                      return false;
 *                  }
 *             }
 *         }
 *     }
 * });
 * ```
 *
 * This feature is **enabled** by default
 *
 * @demo Scheduler/basic
 * @extends Scheduler/feature/base/TimeSpanRecordContextMenuBase
 */
export default class ScheduleContextMenu extends TimeSpanRecordContextMenuBase {
    //region Config

    static get $name() {
        return 'ScheduleContextMenu';
    }

    static get defaultConfig() {
        return {
            /**
             * An array of extra menu items to add to the context menu
             *
             * ```javascript
             * const scheduler = new Scheduler({
             *     features : {
             *         scheduleContextMenu : {
             *             // Extra items for all events
             *             items : [
             *                 {
             *                     text : 'Add meeting',
             *                     icon : 'b-fa b-fa-fw b-fa-calendar-plus',
             *                     onItem({ date, resourceRecord }) {
             *                         scheduler.eventStore.add({
             *                             name : 'Meeting',
             *                             startDate : date,
             *                             duration: 2,
             *                             resourceId : resourceRecord.id
             *                         });
             *                     }
             *                 }
             *             ]
             *         }
             *     }
             * });
             * ```
             *
             * or items config to add extra items to the context menu or hide default items
             *
             * ```javascript
             * features : {
             *     scheduleContextMenu : {
             *         items : {
             *             // Add custom 'Add meeting' item
             *             addMeeting : {
             *                 text : 'Add meeting',
             *                 icon : 'b-fa b-fa-fw b-fa-calendar-plus',
             *                 onItem({ date, resourceRecord }) {
             *                     scheduler.eventStore.add({
             *                         name : 'Meeting',
             *                         startDate : date,
             *                         duration: 2,
             *                         resourceId : resourceRecord.id
             *                     });
             *                 }
             *             },
             *             // Hide default 'Add event' item
             *             addEvent : false
             *         }
             *     }
             * }
             * ```
             *
             * @config {Object|Object[]}
             */
            items : null,

            /**
             * A function called before displaying the menu that allows manipulations of its items.
             * Called with a single parameter with format { date, resourceRecord, items }.
             * Returning `false` from this function prevents the menu from being shown.
             *
             * ```javascript
             * const scheduler = new Scheduler({
             *     features : {
             *         scheduleContextMenu : {
             *             // Process items before menu is shown
             *             processItems({date, resourceRecord, items}) {
             *                  // Push an extra item for ancient times
             *                  if (date < new Date(2018, 11, 17)) {
             *                      items.modernize = {
             *                          text : 'Modernize',
             *                          ontItem({date}) {
             *                              // Custom date based action
             *                          }
             *                      };
             *                  }
             *
             *                  // Do not show menu for Sundays
             *                  if (date.getDay() === 0) {
             *                      return false;
             *                  }
             *             }
             *         }
             *     }
             * });
             * ```
             *
             * @config {Function}
             */
            processItems : null,

            /**
             * This is a preconfigured set of {@link Core.widget.Container#config-namedItems} used to create the default context menu.
             *
             * The provided defaultItems setting is
             *
             *```javascript
             *    {
             *        addEvent : true
             *    }
             *```
             *
             * The `namedItems` provided by this feature are listed below. These are the property
             * names which you may configure in the feature's {@link #config-items} config:
             *
             * - `addEvent` Add an event for at the resource and time indicated by the `contextmenu` event.
             *
             * To remove existing items, set corresponding keys to `false`
             *
             * ```javascript
             * const scheduler = new Scheduler({
             *     features : {
             *         scheduleContextMenu : {
             *             items : {
             *                 addEvent : false
             *             }
             *         }
             *     }
             * });
             * ```
             *
             * @config {Object}
             */
            defaultItems : {
                addEvent : true
            }
        };
    }

    //endregion

    //region Events

    /**
     * Fired from scheduler before the context menu is shown for an event. Allows manipulation of the items
     * to show in the same way as in `processItems`. Returning false from a listener prevents the menu from
     * being shown.
     * @event scheduleContextMenuBeforeShow
     * @preventable
     * @param {Scheduler.view.Scheduler} source
     * @param {Object} items Menu item configs
     * @param {Scheduler.model.EventModel} eventRecord Event record for which the menu was triggered
     * @param {Scheduler.model.ResourceModel} resourceRecord Resource record
     * @param {Scheduler.model.AssignmentModel} assignmentRecord Assignment record, if assignments are used
     * @param {HTMLElement} eventElement
     */

    /**
     * Fired from scheduler when an item is selected in the context menu.
     * @event scheduleContextMenuItem
     * @param {Scheduler.view.Scheduler} source
     * @param {Core.widget.MenuItem} item
     * @param {Scheduler.model.EventModel} eventRecord
     * @param {Scheduler.model.ResourceModel} resourceRecord
     * @param {Scheduler.model.AssignmentModel} assignmentRecord Assignment record, if assignments are used
     * @param {HTMLElement} eventElement
     */

    /**
     * Fired from scheduler after showing the context menu for an event
     * @event scheduleContextMenuShow
     * @preventable
     * @param {Scheduler.view.Scheduler} source
     * @param {Core.widget.Menu} menu The menu
     * @param {Scheduler.model.EventModel} eventRecord Event record for which the menu was triggered
     * @param {Scheduler.model.ResourceModel} resourceRecord Resource record
     * @param {Scheduler.model.AssignmentModel} assignmentRecord Assignment record, if assignments are used
     * @param {HTMLElement} eventElement
     */

    //endregion

    //region Init

    construct(scheduler, config) {
        super.construct(scheduler, config);

        this.scheduler = scheduler;
    }

    //endregion

    //region Events

    showEventContextMenu(event) {
        const
            scheduler        = this.scheduler,
            cellData         = scheduler.getEventData(event),
            isTimeAxisColumn = cellData
                ? scheduler.columns.getById(cellData.columnId) === scheduler.timeAxisColumn
                : scheduler.timeAxisSubGrid.element === event.target,
            // For vertical mode the resource must be resolved from the event
            resourceRecord   = scheduler.resolveResourceRecord(event) || scheduler.resourceStore.last;

        if (isTimeAxisColumn) {
            this.showContextMenu({
                menuType : 'schedule',
                date     : scheduler.getDateFromDomEvent(event, 'floor'),
                resourceRecord,
                event
            });

            event.preventDefault();
        }
    }

    //endregion

    get namedItems() {
        const client = this.client;

        if (!this._namedItems) {
            this._namedItems = {
                addEvent : {
                    text     : client.L('Add event', null),
                    icon     : 'b-icon b-icon-add',
                    disabled : client.resourceStore.count === 0,
                    weight   : 100,
                    onItem({ date, resourceRecord }) {
                        client.internalAddEvent(date, resourceRecord, client.getRowFor(resourceRecord));
                    }
                }
            };
        }

        return this._namedItems;
    }
}

ScheduleContextMenu.featureClass = '';

GridFeatureManager.registerFeature(ScheduleContextMenu, true, 'Scheduler');
