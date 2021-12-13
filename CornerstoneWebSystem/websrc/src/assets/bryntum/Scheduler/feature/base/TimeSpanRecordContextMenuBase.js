import InstancePlugin from '../../../Core/mixin/InstancePlugin.js';
import WidgetHelper from '../../../Core/helper/WidgetHelper.js';
import DomHelper from '../../../Core/helper/DomHelper.js';
import ObjectHelper from '../../../Core/helper/ObjectHelper.js';
import StringHelper from '../../../Core/helper/StringHelper.js';
import Rectangle from '../../../Core/helper/util/Rectangle.js';

/**
 * @module Scheduler/feature/base/TimeSpanRecordContextMenuBase
 */

// This is a version of what Containers do, except that we have to apply our namedItems
// all the way down any configured menu hierarchy, and the resulting structure must
// be available *before* menu instantiation for the processItems method to interrogate.
const applyNamedItems = function(items, namedItems) {
    for (const ref in items) {
        let item = items[ref];
        if (item) {
            if (ref in namedItems) {
                item = items[ref] = typeof item === 'object' ? ObjectHelper.merge(ObjectHelper.clone(namedItems[ref]), item) : namedItems[ref];
            }

            // Our namedItems must apply all the way down any descendant menus.
            // Extract menu here because it may have been applied by a namedItem.
            const menu = item.menu;
            if (menu) {
                applyNamedItems(('items' in menu) ? menu.items : menu, namedItems);
            }
        }
    }
};

/**
 * Abstract base class used by other context menu features such as {@link Scheduler/feature/EventContextMenu EventContextMenu} and
 * {@link Scheduler/feature/ScheduleContextMenu ScheduleContextMenu}.
 * @extends Core/mixin/InstancePlugin
 * @abstract
 */
export default class TimeSpanRecordContextMenuBase extends InstancePlugin {
    //region Config
    static get defaultConfig() {
        return {
            /**
             * This is a preconfigured set of {@link Core.widget.Container#config-namedItems} used to create the default
             * context menu.
             * @config {Object}
             */
            defaultItems : null,

            /**
             * An {@link Core.widget.Menu Menu} items object containing named child menu items
             * to apply to the feature's provided context menu, see {@link #config-defaultItems}.
             *
             * This may add extra items as below, but may also remove any of the {@link #config-defaultItems}
             * by configuring the name of the item as `false`
             *
             * ```javascript
             * features : {
             *     taskContextMenu : { // use eventContextMenu in the Scheduler product
             *         // This object is applied to the Feature's predefined defaultItems object
             *         items : {
             *             switchToDog : {
             *                 text : 'Dog',
             *                 icon : 'b-fa b-fa-fw b-fa-dog',
             *                 onItem({contextRecord}) {
             *                     contextRecord.dog = true;
             *                     contextRecord.cat = false;
             *                 },
             *                 weight : 500     // Make this second from end
             *             },
             *             switchToCat : {
             *                 text : 'Cat',
             *                 icon : 'b-fa b-fa-fw b-fa-cat',
             *                 onItem({contextRecord}) {
             *                     contextRecord.dog = false;
             *                     contextRecord.cat = true;
             *                 },
             *                 weight : 510     // Make this sink to end
             *             },
             *             add : false // We do not want the "Add" submenu to be available
             *         }
             *     }
             * }
             * ```
             *
             * @config {Object|Object[]}
             */
            items : null,

            /**
             * A function called before displaying the menu that allows manipulations of its items. Called with a
             * single parameter with format { contextRecord, eventElement, items }. Returning `false`
             * from this function prevents the menu from being shown.
             *
             * ```javascript
             * features : {
             *     taskContextMenu : {
             *         processItems({contextRecord, items}) {
             *             // Add or remove items here as needed
             *             if (contextRecord.type === 'Meeting') {
             *                 items.cancel = {
             *                     text   : 'Cancel',
             *                     icon   : 'b-fa b-fa-fw b-fa-ban',
             *                     weight : 200 // Move to end
             *                 };
             *             }
             *
             *             // Hide delete for parents
             *             items.deleteTask.hidden = contextRecord.isParent;
             *         }
             *     }
             * }
             * ```
             *
             * @config {Function}
             */
            processItems : null,

            /**
             * Event which is used to show context menu.
             * Available options are: 'contextmenu', 'click', 'dblclick'.
             * Default value is used from {@link Grid/view/GridBase#config-contextMenuTriggerEvent}
             * @config {String}
             */
            triggerEvent : null

        };
    }

    // Plugin configuration. This plugin chains some of the functions in Grid.
    static get pluginConfig() {
        return {
            assign : ['showEventContextMenu'],
            chain  : [
                'onElementContextMenu',
                'onElementClick',
                'onElementDblClick',
                'onEventSpaceKey'
            ]
        };
    }

    //endregion

    //region Init

    doDestroy() {
        if (this.menu) {
            this.menu.destroy();
        }
    }

    //endregion

    //region Events

    onElementContextMenu(event) {
        this.triggerEvent === 'contextmenu' && this.showEventContextMenu(event);
    }

    onElementClick(event) {
        this.triggerEvent === 'click' && this.showEventContextMenu(event);
    }

    onElementDblClick(event) {
        this.triggerEvent === 'dblclick' && this.showEventContextMenu(event);
    }

    // chained from EventNavigation
    onEventSpaceKey(keyEvent) {
        const targetPoint = Rectangle.from(keyEvent.target).center,
            contextmenuEvent = new MouseEvent('contextmenu', Object.assign({
                clientX : targetPoint.x,
                clientY : targetPoint.y
            }, keyEvent));

        Object.defineProperty(contextmenuEvent, 'target', {
            get() {
                return keyEvent.target;
            }
        });

        this.showEventContextMenu(contextmenuEvent);
    }

    //endregion

    //region Menu handlers

    /**
     * Show event context menu.
     * @param event
     * @fires eventContextMenuItem
     * @internal
     */
    showEventContextMenu(event) {
        const
            me           = this,
            client       = me.client,
            target       = event.target,
            eventElement = DomHelper.up(target, client.eventSelector) || target;

        if (eventElement) {
            event.preventDefault();

            const record = me.resolveRecord(eventElement);

            if (record) {
                me.showContextMenuFor(record, { targetElement : eventElement, event });
            }
        }
    }

    /**
     * Shows context menu for the provided record. If record is not rendered (outside of time span, or collapsed)
     * menu won't appear.
     * @param {Scheduler.model.TimeSpan} record
     * @param {Object} [options]
     * @param {HTMLElement} options.targetElement Element to align context menu to
     * @param {Event} options.event Browser event. If provided menu will be aligned according to clientX/clientY coordinates.
     * If omitted, context menu will be centered to targetElement
     */
    showContextMenuFor(record, options) {}

    // Implement in subclasses to massage options or veto show.
    beforeContextMenuShow() {}

    //endregion

    //region Show/Hide

    /**
     * @param {Object} eventParams
     * @param {Object[]} items
     * @protected
     * @internal
     */
    showContextMenu(eventParams) {
        if (this.disabled) {
            return;
        }

        const
            me                       = this,
            event                    = eventParams.event,
            menuType                 = eventParams.menuType.toLowerCase(),
            eventType                = StringHelper.lowercaseFirstLetter(menuType),
            clientGetItemsMethod     = `get${StringHelper.capitalizeFirstLetter(menuType)}MenuItems`,
            { client, processItems, defaultItems, namedItems } = me,
            point = event ? [event.clientX + 1, event.clientY + 1] : Rectangle.from(eventParams.targetElement).center,
            items = eventParams.items = ObjectHelper.isEmpty(me.items) ? ObjectHelper.clone(defaultItems) : ObjectHelper.merge(ObjectHelper.clone(defaultItems), me.items);

        eventParams.namedItems = namedItems;
        eventParams.selection = client.selectedRecords;

        // Apply the named items prior to Container's item processing.
        // Our namedItems must cascade to all descendant Menu levels.
        // And they MUST have all been converted prior to the processItems call.
        applyNamedItems(items, namedItems);

        // Call the chainable method which other features use to add their own menu items.
        // For example getEventMenuItems
        if (client[clientGetItemsMethod]) {
            client[clientGetItemsMethod](eventParams, items);
        }

        // Allow user a chance at processing the items and preventing the menu from showing
        if ((!processItems || processItems(eventParams) !== false) && !ObjectHelper.isEmpty(eventParams.items)) {

            // beforeContextMenuShow is a lifecycle method which may be implemented in subclasses to
            // preprocess the event.
            if (me.beforeContextMenuShow(eventParams) !== false) {
                // Trigger event that allows preventing menu or manipulating its items
                if (client.trigger(`${eventType}ContextMenuBeforeShow`, eventParams) !== false) {
                    me.menu = WidgetHelper.showContextMenu(point, {
                        owner        : client,
                        scrollAction : 'hide',
                        clippedBy    : [client.timeAxisSubGridElement, client.bodyContainer],
                        constrainTo  : window,
                        items,
                        onDestroy() {
                            me.menu = null;
                        },
                        // Load up the item event with the contextual info
                        onBeforeItem : itemEvent => {
                            Object.assign(itemEvent, eventParams);
                        },

                        onItem : itemEvent => client.trigger(`${eventType}ContextMenuItem`, itemEvent),

                        listeners : {
                            show({ source : menu }) {
                                eventParams.menu = menu;
                                client.trigger(`${eventType}ContextMenuShow`, eventParams);
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * Hides the context menu
     * @protected
     * @internal
     */
    hideContextMenu(animate) {
        this.menu && this.menu.hide(animate);
    }

    //endregion

    //region Getters/Setters

    /**
     * Provides the default configuration of the context menu.
     *
     * Concrete classes must all provide their own defaultItems value in their defaultConfig blocks
     * @private
     */
    set defaultItems(defaultItems) {
        this._defaultItems = defaultItems;
    }

    get defaultItems() {
        const result = ObjectHelper.clone(this._defaultItems);

        // Read-only client should have no default items enabled
        if (this.client.readOnly) {
            for (let item in result) {
                result[item] = false;
            }
        }

        return result;
    }

    get triggerEvent() {
        return this._triggerEvent || this.client.contextMenuTriggerEvent;
    }

    set triggerEvent(value) {
        this._triggerEvent = value;
    }

    //endregion

}
