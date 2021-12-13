import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import GridFeatureManager from '../feature/GridFeatureManager.js';
import StringHelper from '../../Core/helper/StringHelper.js';

/**
 * @module Grid/feature/ColumnPicker
 */

/**
 * Displays a column picker (to show/hide columns) in the header context menu. Columns can be displayed in sub menus
 * by region or tag. Grouped headers are displayed as menu hierarchies.
 *
 * This feature is <strong>enabled</strong> by default.
 *
 * @extends Core/mixin/InstancePlugin
 *
 * @demo Grid/columns
 * @classtype columnPicker
 * @externalexample feature/ColumnPicker.js
 */
export default class ColumnPicker extends InstancePlugin {
    //region Config

    static get $name() {
        return 'ColumnPicker';
    }

    static get defaultConfig() {
        return {
            /**
             * Groups columns in the picker by region (each region gets its own sub menu)
             * @config {Boolean}
             * @default
             */
            groupByRegion : false,

            /**
             * Groups columns in the picker by tag, each column may be shown under multiple tags. See
             * {@link Grid.column.Column#config-tags}
             * @config {Boolean}
             * @default
             */
            groupByTag : false
        };
    }

    // Plugin configuration. This plugin chains some of the functions in Grid.
    static get pluginConfig() {
        return {
            chain : ['getHeaderMenuItems', 'getColumnDragToolbarItems']
        };
    }

    //endregion

    //region Init

    construct(grid, config) {
        this.grid = grid;
        super.construct(grid, config);
    }

    //endregion

    //region Context menu

    /**
     * Get menu items, either a straight list of columns or sub menus per subgrid
     * @private
     * @param columnStore Column store to traverse
     * @returns {Object[]} Menu item configs
     */
    getColumnPickerItems(columnStore) {
        const me = this;

        if (me.groupByRegion) {
            // submenus for grids regions
            return me.grid.regions.map(region => {
                const columns = me.grid.getSubGrid(region).columns.topColumns;

                return {
                    text     : StringHelper.capitalizeFirstLetter(region),
                    menu     : me.buildColumnMenu(columns),
                    disabled : columns.length === 0,
                    region   : region
                };
            });
        }
        else if (me.groupByTag) {
            // submenus for column tags
            const tags = {};
            columnStore.topColumns.forEach(column => {
                column.tags && column.hideable && column.tags.forEach(tag => {
                    if (!tags[tag]) {
                        tags[tag] = 1;
                    }
                });
            });

            // TODO: as checkitems, but how to handle toggling? hide a column only when all tags for it are unchecked?
            return Object.keys(tags).sort().map(tag => ({
                text            : StringHelper.capitalizeFirstLetter(tag),
                menu            : me.buildColumnMenu(me.getColumnsForTag(tag)),
                tag             : tag,
                onBeforeSubMenu : ({ item, itemEl }) => {
                    me.refreshTagMenu(item, itemEl);
                }
            }));
        }
        else {
            // all columns in same menu
            return me.buildColumnMenu(columnStore.topColumns);
        }
    }

    /**
     * Get all columns that has the specified tag
     * TODO: if tags are useful from somewhere else, move to ColumnStore
     * @private
     * @param tag
     * @returns {Grid.column.Column[]}
     */
    getColumnsForTag(tag) {
        // TODO: if tags are usefull from somewhere else, move to ColumnStore
        return this.grid.columns.records.filter(column =>
            column.tags && column.tags.includes(tag) && column.hideable !== false
        );
    }

    /**
     * Refreshes checked status for a tag menu. Needed since columns can appear under multiple tags.
     * @private
     */
    refreshTagMenu(item, itemEl) {
        const columns = this.getColumnsForTag(item.tag);
        columns.forEach(column => {
            const subItem = item.items.find(subItem => subItem.column === column);
            if (subItem) subItem.checked = column.hidden !== true;
        });
    }

    /**
     * Traverses columns to build menu items for the column picker.
     * @private
     */
    buildColumnMenu(columns) {
        let currentRegion = columns.length > 0 && columns[0].region,
            { grid }      = this;

        return columns.reduce((items, column) => {
            const visibleInRegion = this.grid.columns.visibleColumns.filter(col => col.region === column.region);

            if (column.hideable !== false) {
                const itemConfig = {
                    grid,
                    text     : column.text,
                    column   : column,
                    name     : column.id,
                    checked  : column.hidden !== true,
                    disabled : column.hidden !== true && visibleInRegion.length === 1,
                    cls      : column.region !== currentRegion ? 'b-separator' : ''
                };

                currentRegion = column.region;

                if (column.children) {
                    itemConfig.menu = this.buildColumnMenu(column.children);
                }

                items.push(itemConfig);
            }
            return items;
        }, []);
    }

    /**
     * Supply items for headers context menu.
     * @private
     * @param column Header for this column
     * @param items Array of items to add to
     * @returns {Object[]} Modified items
     */
    getHeaderMenuItems(column, items) {
        const me = this,
            { grid, disabled } = me,
            { columns } = grid;

        if (column.showColumnPicker !== false && columns.some(col => col.hideable)) {
            // column picker
            items.push({
                text     : me.L('columnsMenu'),
                name     : 'columnPicker',
                icon     : 'b-fw-icon b-icon-columns',
                cls      : 'b-separator',
                weight   : 100,
                menu     : me.getColumnPickerItems(columns),
                onToggle : me.onColumnToggle,
                disabled
            });
        }

        // menu item for hiding this column
        if (column.hideable !== false) {
            const visibleInRegion = columns.visibleColumns.filter(col => col.region === column.region);

            items.push({
                text     : me.L('hideColumn'),
                icon     : 'b-fw-icon b-icon-hide-column',
                weight   : 101,
                name     : 'hideColumn',
                disabled : visibleInRegion.length === 1 || disabled,
                onItem   : () => column.hide()
            });
        }
    }

    /**
     * Handler for column hide/show menu checkitems.
     * @private
     * @param {Object} The {@link Core.widget.MenuItem#event-toggle} event.
     */
    onColumnToggle({ menu, item, checked }) {
        if (!!item.column.hidden !== !checked) {
            item.column[checked ? 'show' : 'hide']();

            const
                { grid, column } = item,
                { columns } = grid,
                // Sibling items, needed to disable other item if it is the last one in region
                siblingItems = menu.items,
                // Columns left visible in same region as this items column
                visibleInRegion = columns.visibleColumns.filter(col => col.region === item.column.region),
                // Needed to access "hide-column" item outside of column picker
                { currentMenu } = grid.features.contextMenu,
                // TODO: When we have actual MenuItems there should be a way to store the item and not have to look it up
                hideItem = currentMenu.items.find(item => item.name === 'hideColumn');

            // Do not allow user to hide the last column in any region
            if (visibleInRegion.length === 1) {
                const lastVisibleItem = siblingItems.find(i => i.name === visibleInRegion[0].id);
                if (lastVisibleItem) {
                    lastVisibleItem.disabled = true;
                }

                // Also disable "Hide column" item if only one column left in this region
                if (hideItem && column.region === item.column.region) {
                    hideItem.disabled = true;
                }
            }
            // Multiple columns visible, enable "hide-column" and all items for that region
            else {
                visibleInRegion.forEach(col => {
                    const siblingItem = siblingItems.find(sibling => sibling.column === col);
                    if (siblingItem) {
                        siblingItem.disabled = false;
                    }
                });

                if (hideItem && column.region === item.column.region) {
                    hideItem.disabled = false;
                }
            }

            if (item.menu) {
                // Reflect status in submenu.
                // Cannot use short form () => foo because eachWidget aborts on return of false
                item.menu.eachWidget(subItem => {
                    subItem.checked = checked;
                });
            }

            const parentItem = menu.owner;
            if (parentItem && parentItem.column === column.parent) {
                const anyChecked = siblingItems.some(subItem => subItem.checked === true);
                parentItem.checked = anyChecked;
            }
        }
    }

    /**
     * Supply items to ColumnDragToolbar
     * @private
     */
    getColumnDragToolbarItems(column, items) {
        const visibleInRegion = this.grid.columns.visibleColumns.filter(col => col.region === column.region);

        if (column.hideable !== false && visibleInRegion.length > 1) {
            items.push({
                text   : this.L('hideColumnShort'),
                group  : this.L('Column'),
                icon   : 'b-fw-icon b-icon-hide-column',
                weight : 101,
                name   : 'hideColumn',
                onDrop : ({ column }) => column.hide()
            });
        }
        return items;
    }

    //endregion
}

GridFeatureManager.registerFeature(ColumnPicker, true);
