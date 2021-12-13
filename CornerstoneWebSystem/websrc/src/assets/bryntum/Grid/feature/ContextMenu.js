//TODO: Context menu should hide when clicking elsewhere

import DomHelper from '../../Core/helper/DomHelper.js';
import WidgetHelper from '../../Core/helper/WidgetHelper.js';
import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import GridFeatureManager from '../feature/GridFeatureManager.js';

/**
 * @module Grid/feature/ContextMenu
 */

/**
 * Right click to display context menu for headers and cells. Items for the menu are requested by calling
 * {@link Grid.view.Grid#function-getHeaderMenuItems Grid.getHeaderMenuItems()} or
 * {@link Grid.view.Grid#function-getCellMenuItems Grid.getCellMenuItems()} (see {@link Grid.feature.Sort Sort} feature).
 * It is also possible to add items via the features config and via column configs. See examples below.
 *
 * Add extra items to both header and cell for all columns:
 *
 * ```javascript
 * // Extra items for all columns
 * const grid = new Grid({
 *   features: {
 *     contextMenu: {
 *       headerItems: [
 *         { text: 'My header item', icon: 'fa fa-car', weight: 200, onItem : () => ... }
 *       ],
 *
 *       cellItems: [
 *         { text: 'My cell item', icon: 'fa fa-bus', weight: 200, onItem : () => ... }
 *       ]
 *     }
 *   }
 * });
 * ```
 *
 * Add extra items to both header and cell for a single column:
 *
 * ```javascript
 * // Extra items for single column
 * const grid = new Grid({
 *   columns: [
 *     { field: 'name', text: 'Name', headerMenuItems: [
 *       { text: 'My unique header item', icon: 'fa fa-flask', onItem : () => ... }
 *     ]},
 *     { field: 'city', text: 'City', cellMenuItems: [
 *       { text: 'My unique cell item', icon: 'fa fa-beer', onItem : () => ... }
 *     ]},
 *   ]
 * });
 * ```
 *
 * It is also possible to manipulate the default items and add new ones in a processing function (same pattern applies
 * for header menu):
 *
 * ```javascript
 * const grid = new Grid({
 *   features: {
 *     contextMenu: {
 *       processCellItems({items, record}) {
 *           if (record.cost > 5000) {
 *              items.push({ text : 'Split cost' });
 *           }
 *       }
 *     }
 *   }
 * });
 * ```
 *
 * This feature is <strong>enabled</strong> by default.
 *
 * @extends Core/mixin/InstancePlugin
 * @demo Grid/filtering
 * @classtype contextMenu
 * @externalexample feature/ContextMenu.js
 */
export default class ContextMenu extends InstancePlugin {
    //region Config

    static get $name() {
        return 'ContextMenu';
    }

    static get defaultConfig() {
        return {
            /**
             * Extra items to add to the header context menu. See {@link Core.widget.MenuItem} for more info.
             *
             * ```javascript
             * features : {
             *     contextMenu : {
             *         headerItems : [
             *             { text : 'Header item', onItem : () => ... }
             *         ]
             *     }
             * }
             * ```
             *
             * @config {Object[]}
             */
            headerItems : [],

            /**
             * A function called before displaying the header menu that allows manipulations of its items. Called with a
             * single parameter with format { column, items }. Returning `false` from this function prevents
             * the menu from being shown.
             *
             * ```javascript
             * features : {
             *     contextMenu : {
             *         processHeaderItems({record, items}) {
             *             // Add or remove items here as needed
             *             if (column.field === 'age') {
             *                 items.push({ text: 'Hide youngsters', icon : 'b-fa b-fa-fw b-fa-baby' })
             *             }
             *         }
             *     }
             * }
             * ```
             *
             * @config {Function}
             */
            processHeaderItems : null,

            /**
             * Extra items to add to the cell context menu. See {@link Core.widget.MenuItem} for more info.
             *
             * ```javascript
             * features : {
             *     contextMenu : {
             *         cellItems : [
             *             { text : 'Cell item', onItem : () => ... }
             *         ]
             *     }
             * }
             * ```
             *
             * @config {Object[]}
             */
            cellItems : [],

            /**
             * A function called before displaying the cell menu that allows manipulations of its items. Called with a
             * single parameter with format { record, items }. Returning `false` from this function prevents
             * the menu from being shown.
             *
             * ```javascript
             * features : {
             *     contextMenu : {
             *         processCellItems({record, items}) {
             *             // Add or remove items here as needed
             *             if (record.age > 50) {
             *                 items.push({ text: 'Add extra vacation', icon : 'b-fa b-fa-fw b-fa-umbrella-beach' })
             *             }
             *         }
             *     }
             * }
             * ```
             *
             * @config {Function}
             */
            processCellItems : null,

            /**
             * Event which is used to show context menu.
             * Available options are: 'contextmenu', 'click', 'dblclick'.
             * Default value is used from {@link Grid/view/Grid#config-contextMenuTriggerEvent}
             * @config {String}
             */
            triggerEvent : null

        };
    }

    //endregion

    //region Events

    /**
     * Fired from grid before the context menu is shown for a header. Allows manipulation of the items
     * to show in the same way as in `processHeaderItems`. Returning false from a listener prevents the
     * menu from being shown.
     * @event headerContextMenuBeforeShow
     * @preventable
     * @param {Grid.view.Grid} source
     * @param {Object} items Menu item configs
     * @param {Grid.column.Column} column Column
     */

    /**
     * Fired from grid after showing the context menu for a header
     * @event headerContextMenuShow
     * @preventable
     * @param {Grid.view.Grid} source
     * @param {Core.widget.Menu} menu The menu
     * @param {Grid.column.Column} column Column
     */

    /**
     * Fired from grid before the context menu is shown for a cell. Allows manipulation of the items
     * to show in the same way as in `processCellItems`. Returning false from a listener prevents the
     * menu from being shown.
     * @event cellContextMenuBeforeShow
     * @preventable
     * @param {Grid.view.Grid} source
     * @param {Object} items Menu item configs
     * @param {Grid.column.Column} column Column
     * @param {Core.data.Model} record Record
     */

    /**
     * Fired from grid after showing the context menu for a cell
     * @event cellContextMenuShow
     * @preventable
     * @param {Grid.view.Grid} source
     * @param {Core.widget.Menu} menu The menu
     * @param {Grid.column.Column} column Column
     * @param {Core.data.Model} record Record
     */

    /**
     * Fired when an item is selected in the context menu.
     * @event contextMenuItem
     * @param {Grid.view.Grid} grid The grid
     * @param {Object} item Selected menu item
     * @param {Grid.column.Column} column Column
     * @param {HTMLElement} itemEl Menu item element
     */

    /**
     * Fired when an check item is toggled in the context menu.
     * @event contextMenuToggleItem
     * @param {Grid.view.Grid} grid The grid
     * @param {Object} item Selected menu item
     * @param {Grid.column.Column} column Column
     * @param {Boolean} checked Checked or not
     * @param {HTMLElement} itemEl Menu item element
     */

    //endregion

    //region Init

    construct(grid, config) {
        this.grid = grid;

        super.construct(grid, config);
    }

    doDestroy() {
        if (this.currentMenu) {
            this.currentMenu.destroy();
        }

        super.doDestroy();
    }

    //endregion

    //region Plugin config

    // Plugin configuration. This plugin chains some of the functions in Grid.
    // The contextmenu event is emulated from a taphold gesture on touch platforms.
    static get pluginConfig() {
        return {
            assign : ['showContextMenu'],
            chain  : [
                'onElementContextMenu',
                'onElementClick',
                'onElementDblClick',
                'onElementKeyDown'
            ]
        };
    }

    //endregion

    //region Events

    onElementContextMenu(event) {
        this.triggerEvent === 'contextmenu' && this.showContextMenu(event);
    }

    onElementClick(event) {
        this.triggerEvent === 'click' && this.showContextMenu(event);
    }

    onElementDblClick(event) {
        this.triggerEvent === 'dblclick' && this.showContextMenu(event);
    }

    onElementKeyDown(event) {
        if (!event.handled && event.target.matches('.b-grid-header.b-depth-0')) {
            switch (event.key) {
                case ' ':
                case 'ArrowDown':
                    this.showContextMenu(event);
                    break;
            }
        }
    }

    /**
     * Show context menu.
     * @param event
     * @fires contextmenuitem
     * @fires contextmenutoggleitem
     * @private
     */
    showContextMenu(event) {
        if (!this.disabled) {
            const
                header   = DomHelper.up(event.target, '.b-grid-header'),
                cellData = this.grid.getEventData(event);

            if (header) {
                this.handleHeaderContextMenu(header, event);
            }
            else if (cellData) {
                this.handleCellContextMenu(cellData, event);
            }
        }
    }

    handleHeaderContextMenu(header, event) {
        if (header.dataset.column) {
            const
                me        = this,
                grid      = me.grid,
                column    = grid.columns.getById(header.dataset.columnId),
                setColumn = item => {
                    if (!item.column) {
                        item.column = column;
                    }

                    let menu = item.menu;
                    if (menu) {
                        if (!Array.isArray(menu)) {
                            menu = menu.items || menu.widgets;
                        }
                        menu.forEach(setColumn);
                    }
                };

            if (column.enableHeaderContextMenu !== false) {
                // User's items for all headers and for specific column correspondingly. Check Context Menu demo for details.
                const
                    items                  = [...me.headerItems, ...(column.headerMenuItems || [])],
                    { processHeaderItems } = me,
                    eventParams            = { items, column, event, element : header };

                // getHeaderMenuItems() is chained by mixins, thus each feature can supply items
                grid.getHeaderMenuItems(column, items);

                if ((!processHeaderItems || processHeaderItems(eventParams) !== false) && items.length > 0) {
                    event.preventDefault();

                    items.sort((a, b) => ((a.weight || 150) - (b.weight || 150)));

                    // Propagate the operating column down to all MenuItem levels
                    // Will not override if they are preconfigured with a column.
                    items.forEach(setColumn);

                    // Trigger event that allows preventing menu or manipulating its items
                    if (grid.trigger('headerContextMenuBeforeShow', eventParams) !== false) {
                        // Align to header element when using arrow down key
                        me.currentMenu = WidgetHelper.showContextMenu(event.type === 'keydown' ? header : [event.clientX + 1, event.clientY + 1], {
                            owner       : me.client,
                            constrainTo : document,
                            cls         : 'b-context-menu',
                            items       : items,

                            // We can only realign if we are aligning to an element.
                            scrollAction : 'hide',
                            onItem({ source, item, element }) {
                                grid.trigger('contextMenuItem', { source : grid, item, column, element });
                            },

                            onToggle({ source, item, checked, element }) {
                                grid.trigger('contextMenuToggleItem', {
                                    source : grid,
                                    item,
                                    column,
                                    checked,
                                    element
                                });
                            },

                            onDestroy() {
                                // If menu is destroyed by WidgetHelper, make sure we don't keep a reference to it anymore
                                me.currentMenu = null;
                            },

                            listeners : {
                                show({ source : menu }) {
                                    eventParams.menu = menu;
                                    grid.trigger('headerContextMenuShow', eventParams);
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    handleCellContextMenu(cellData, event) {
        const
            me = this,
            grid = me.grid,
            cell = cellData.cellElement,
            column   = grid.columns.getById(cellData.columnId);

        if (column.enableCellContextMenu !== false) {

            // Process the gesture as navigation so that the use may select/multiselect
            // the items to include in their context menu operation.
            // Also select if not already selected.
            grid.focusCell(cellData.cellSelector, {
                doSelect : !grid.isSelected(cellData.id),
                event
            });

            const
                record = cellData.record,
                items  = [...(me.cellItems || []), ...(column.cellMenuItems || [])],
                { processCellItems } = me,
                eventParams            = { items, column, event, record, element : cell };

            // getCellMenuItems() is chained by mixins, thus each feature can supply items
            grid.getCellMenuItems(column, record, items);

            if ((!processCellItems || processCellItems(eventParams) !== false) && items.length > 0) {

                items.forEach(item => {
                    item.column = column;
                    item.record = record;
                });

                event.preventDefault();

                items.sort((a, b) => ((a.weight || 150) - (b.weight || 150)));

                // Trigger event that allows preventing menu or manipulating its items
                if (grid.trigger('cellContextMenuBeforeShow', eventParams) !== false) {
                    me.currentMenu = WidgetHelper.showContextMenu([event.clientX + 1, event.clientY + 1], {
                        owner : me.client,
                        items : items,

                        // Load up the item event with the contextual info
                        onBeforeItem : itemEvent => {
                            Object.assign(itemEvent, eventParams);
                        },

                        onItem({ item }) {
                            grid.trigger('contextMenuItem', { source : grid, item, column, record, cell });
                        },

                        onClose({ reason }) {
                            // return focus to grid when context menu is closed, if not cause by clicking outside of grid
                            if (reason !== 'outside') {
                                grid.element.focus();
                            }
                        },

                        onDestroy() {
                            // If menu is destroyed by WidgetHelper, make sure we don't keep a reference to it anymore
                            me.currentMenu = null;
                        },

                        listeners : {
                            show({ source : menu }) {
                                eventParams.menu = menu;
                                grid.trigger('cellContextMenuShow', eventParams);
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
        this.currentMenu && this.currentMenu.hide(animate);
    }

    //endregion

    //region Getters/Setters

    get triggerEvent() {
        return this._triggerEvent || this.client.contextMenuTriggerEvent;
    }

    set triggerEvent(value) {
        this._triggerEvent = value;
    }

    //endregion

}

ContextMenu.featureClass = '';

GridFeatureManager.registerFeature(ContextMenu, true);
