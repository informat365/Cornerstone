import GridFeatureManager from '../../Grid/feature/GridFeatureManager.js';
import TimeSpanRecordContextMenuBase from './base/TimeSpanRecordContextMenuBase.js';

/**
 * @module Scheduler/feature/EventContextMenu
 */

/**
 * Displays a context menu for events. Items are populated by other features and/or application code.
 *
 * To add extra items for all events (Array notation):
 *
 * ```javascript
 * const scheduler = new Scheduler({
 *     features : {
 *         eventContextMenu : {
 *             // Extra items for all events
 *             items : [
 *                 {
 *                     text : 'Extra',
 *                     icon : 'b-fa b-fa-fw b-fa-flag',
 *                     onItem({eventRecord}) {
 *                         eventRecord.flagged = true;
 *                     }
 *                 }
 *             ]
 *         }
 *     }
 * });
 * ```
 *
 * To add extra items for all events (Object notation):
 *
 * ```javascript
 * const scheduler = new Scheduler({
 *     features : {
 *         eventContextMenu : {
 *             // Extra items for all events
 *             items : {
 *                 extraItems : {
 *                     text : 'Extra',
 *                     icon : 'b-fa b-fa-fw b-fa-flag',
 *                     onItem({eventRecord}) {
 *                         eventRecord.flagged = true;
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
 *         eventContextMenu : {
 *             items : {
 *                 deleteEvent   : false,
 *                 unassignEvent : false
 *             }
 *         }
 *     }
 * });
 * ```
 *
 * Manipulate existing items for all events or specific events:
 *
 * ```javascript
 * const scheduler = new Scheduler({
 *     features : {
 *         eventContextMenu : {
 *             // Process items before menu is shown
 *             processItems({eventRecord, items}) {
 *                  // Push an extra item for conferences
 *                  if (eventRecord.type === 'conference') {
 *                      items.showSessionItem = {
 *                          text : 'Show sessions',
 *                          onItem({eventRecord}) {
 *                              // ...
 *                          }
 *                      };
 *                  }
 *
 *                  // Do not show menu for secret events
 *                  if (eventRecord.type === 'secret') {
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
 * @extends Scheduler/feature/base/TimeSpanRecordContextMenuBase
 * @demo Scheduler/eventcontextmenu
 */
export default class EventContextMenu extends TimeSpanRecordContextMenuBase {
    //region Config

    static get $name() {
        return 'EventContextMenu';
    }

    static get defaultConfig() {
        return {
            /**
             * An array of extra menu items to add to the context menu
             *
             * ```javascript
             * const scheduler = new Scheduler({
             *     features : {
             *         eventContextMenu : {
             *             // Extra items for all events
             *             items : [
             *                 {
             *                     text : 'Extra',
             *                     icon : 'b-fa b-fa-fw b-fa-flag',
             *                     onItem({eventRecord}) {
             *                         eventRecord.flagged = true;
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
             * const scheduler = new Scheduler({
             *     features : {
             *         eventContextMenu : {
             *             items : {
             *                 // Add custom 'Extra' item
             *                 extra : {
             *                     text : 'Extra',
             *                     icon : 'b-fa b-fa-fw b-fa-flag',
             *                     onItem({eventRecord}) {
             *                         eventRecord.flagged = true;
             *                     }
             *                 },
             *                 // Hide default 'Delete event' and 'Unassign event' items
             *                 deleteEvent   : false,
             *                 unassignEvent : false
             *             }
             *         }
             *     }
             * });
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
             * features : {
             *     eventContextMenu : {
             *         // Process items before menu is shown
             *         processItems({eventRecord, items}) {
             *              // Push an extra item for conferences
             *              if (eventRecord.type === 'conference') {
             *                  items.showSessionItem = {
             *                      text : 'Show sessions',
             *                      onItem({eventRecord}) {
             *                          // ...
             *                      }
             *                  };
             *              }
             *
             *              // Do not show menu for secret events
             *              if (eventRecord.type === 'secret') {
             *                  return false;
             *              }
             *         }
             *     }
             * }
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
             *        deleteEvent   : true,
             *        unassignEvent : true
             *    }
             *```
             *
             * The `namedItems` provided by this feature are listed below. These are the property
             * names which you may configure in the feature's {@link #config-items} config:
             *
             * - `deleteEvent` Deletes the context event.
             * - `unassignEvent` Unassigns the context event from the context resource.
             *
             * To remove existing items, set corresponding keys to `false`
             *
             * ```javascript
             * const scheduler = new Scheduler({
             *     features : {
             *         eventContextMenu : {
             *             items : {
             *                 deleteEvent   : false,
             *                 unassignEvent : false
             *             }
             *         }
             *     }
             * });
             * ```
             *
             * See the feature config in the above example for details.
             * @config {Object}
             */
            defaultItems : {
                deleteEvent   : true,
                unassignEvent : true
            }
        };
    }
    //endregion

    //region Events

    /**
     * Fired from scheduler before the context menu is shown for an event. Allows manipulation of the items
     * to show in the same way as in `processItems`. Returning false from a listener prevents the menu from
     * being shown.
     * @event eventContextMenuBeforeShow
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
     * @event eventContextMenuItem
     * @param {Scheduler.view.Scheduler} source
     * @param {Core.widget.MenuItem} item
     * @param {Scheduler.model.EventModel} eventRecord
     * @param {Scheduler.model.ResourceModel} resourceRecord
     * @param {Scheduler.model.AssignmentModel} assignmentRecord Assignment record, if assignments are used
     * @param {HTMLElement} eventElement
     */

    /**
     * Fired from scheduler after showing the context menu for an event
     * @event eventContextMenuShow
     * @preventable
     * @param {Scheduler.view.Scheduler} source
     * @param {Core.widget.Menu} menu The menu
     * @param {Scheduler.model.EventModel} eventRecord Event record for which the menu was triggered
     * @param {Scheduler.model.ResourceModel} resourceRecord Resource record
     * @param {Scheduler.model.AssignmentModel} assignmentRecord Assignment record, if assignments are used
     * @param {HTMLElement} eventElement
     */

    //endregion

    //region Events

    resolveRecord(element) {
        return this.client.resolveEventRecord(element);
    }

    //endregion

    /**
     * Shows context menu for the provided event. If record is not rendered (outside of time span/filtered)
     * menu won't appear.
     * @param {Scheduler.model.EventModel} eventRecord
     * @param {Object} [options]
     * @param {HTMLElement} options.targetElement Element to align context menu to
     * @param {Event} options.event Browser event. If provided menu will be aligned according to clientX/clientY coordinates.
     * If omitted, context menu will be centered to taskElement
     */
    showContextMenuFor(eventRecord, { targetElement, event } = {}) {
        const
            me         = this,
            { client } = me;

        if (!targetElement) {
            targetElement = client.getElementsFromEventRecord(eventRecord)[0];

            // If record is not rendered, do nothing
            if (!targetElement) {
                return;
            }
        }

        me.showContextMenu({
            menuType         : 'event',
            eventElement     : targetElement,
            targetElement,
            eventRecord,
            resourceRecord   : client.resolveResourceRecord(targetElement),
            assignmentRecord : client.resolveAssignmentRecord(targetElement),
            event
        });
    }

    set defaultItems(defaultItems) {
        this._defaultItems = defaultItems;
    }

    get defaultItems() {
        const result = super.defaultItems;

        if (!this.client.assignmentStore) {
            result.unassignEvent = false;
        }

        return result;
    }

    get namedItems() {
        const
            me         = this,
            { client } = me;

        if (!me._namedItems) {
            me._namedItems = {
                deleteEvent : {
                    text   : client.L('Delete event'),
                    icon   : 'b-icon b-icon-trash',
                    weight : -160,
                    onItem : ({ menu, eventRecord }) => {
                        // We must synchronously push focus back into the menu's triggering
                        // event so that the our beforeRemove handlers can move focus onwards
                        // to the closest remaining event.
                        // Otherwise, the menu's default hide processing on hide will attempt
                        // to move focus back to the menu's triggering event which will
                        // by then have been deleted.
                        const revertTarget = menu.focusInEvent && menu.focusInEvent.relatedTarget;
                        if (revertTarget) {
                            revertTarget.focus();
                            client.navigator.activeItem = revertTarget;
                        }
                        client.removeRecords([eventRecord]);
                    }
                },
                unassignEvent : {
                    text   : client.L('Unassign event'),
                    icon   : 'b-icon b-icon-unassign',
                    weight : -150,
                    name   : 'unassignEvent',
                    onItem : ({ menu, eventRecord, resourceRecord }) => {
                        // We must synchronously push focus back into the menu's triggering
                        // event so that the our beforeRemove handlers can move focus onwards
                        // to the closest remaining event.
                        // Otherwise, the menu's default hide processing on hide will attempt
                        // to move focus back to the menu's triggering event which will
                        // by then have been deleted.
                        const revertTarget = menu.focusInEvent && menu.focusInEvent.relatedTarget;
                        if (revertTarget) {
                            revertTarget.focus();
                            client.navigator.activeItem = revertTarget;
                        }
                        eventRecord.unassign(resourceRecord);
                    }
                }
            };
        }

        return me._namedItems;
    }
}

EventContextMenu.featureClass = '';

GridFeatureManager.registerFeature(EventContextMenu, true, 'Scheduler');
