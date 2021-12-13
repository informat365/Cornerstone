//TODO: Should listen for store search also, to work the other way around
//TODO: Buggy sometimes, try searching for Barcelona tigers, navigate using buttons
//TODO: Allow regex
//TODO: Optional case sensitive
//TODO: build in UI, popup with keyboard shortcut?

import DomHelper from '../../Core/helper/DomHelper.js';
import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import GridFeatureManager from '../feature/GridFeatureManager.js';
import DomDataStore from '../../Core/data/DomDataStore.js';

/**
 * @module Grid/feature/Search
 */

/**
 * Feature that allows the user to search the entire grid. Navigate between hits using the
 * keyboard, [f3] or [ctrl]/[cmd] + [g] moves to next, also pressing [shift] moves to previous.
 *
 * Note that this feature does not include a UI, please build your own and call appropriate methods in the feature. For
 * a demo implementation, see
 * <a href="../examples/search" target="_blank">Search example</a>.
 *
 * This feature is <strong>disabled</strong> by default.
 *
 * @extends Core/mixin/InstancePlugin
 *
 * @example
 * // enable Search
 * let grid = new Grid({
 *   features: {
 *     search: true
 *   }
 * });
 *
 * // perform search
 * grid.features.search.search('steve');
 *
 * @demo Grid/search
 * @classtype search
 * @externalexample feature/Search.js
 */
export default class Search extends InstancePlugin {
    //region Init

    static get $name() {
        return 'Search';
    }

    construct(grid, config) {
        const me = this;

        super.construct(grid, config);

        Object.assign(me, {
            store  : grid.store,
            grid   : grid,
            find   : '',
            hitEls : []
        });
    }

    doDestroy() {
        this.clear(true);
        super.doDestroy();
    }

    doDisable(disable) {
        if (disable) {
            this.clear();
        }

        super.doDisable(disable);
    }

    //endregion

    //region Plugin config

    // Plugin configuration. This plugin chains some of the functions in Grid.
    static get pluginConfig() {
        return {
            chain : ['getCellMenuItems', 'onElementKeyDown']
        };
    }

    //endregion

    //region Search

    /**
     * Performs a search and highlights hits.
     * @param {String} find Text to search for
     * @param {Boolean} gotoHit Go to first hit after search
     * @param {Boolean} reapply Pass true to force search
     */
    search(find, gotoHit = true, reapply = false) {
        const me = this;

        // empty search considered a clear
        if (!find) {
            return me.clear();
        }

        // searching for same thing again, do nothing
        if (!reapply && find === me.find || me.disabled) {
            return;
        }

        const
            grid    = me.grid,
            // Only search columns in use
            columns = grid.columns.visibleColumns.filter(col => col.searchable !== false),
            fields  = columns.map(col => col.field),
            found   = me.store.search(find, fields);

        let i = 1;

        Object.assign(me, {
            foundMap  : {},
            prevFound : me.found,
            found,
            find
        });

        // clear old hits
        for (let cell of DomHelper.children(grid.element, '.b-search-hit')) {
            // IE11 doesnt support this
            //cell.classList.remove('b-search-hit', 'b-search-hit-cell');
            cell.classList.remove('b-search-hit');
            cell.classList.remove('b-search-hit-cell');

            // rerender cell to remove search-hit-text
            let row = DomDataStore.get(cell).row;
            row.renderCell(cell);
        }

        if (!found) return;

        // columns from previous search, reset htmlEncode
        if (me.hitColumns) {
            me.hitColumns.forEach(col => col.disableHtmlEncode = false);
        }

        me.hitColumns = [];

        // highlight hits for visible cells
        for (let hit of found) {
            me.foundMap[hit.field + '-' + hit.id] = i++;

            // disable htmlEncode for columns with hits
            const column = me.grid.columns.get(hit.field);
            if (column) {
                column.disableHtmlEncode = true;
                me.hitColumns.push(column);
            }
            // limit hits
            if (i > 1000) break;
        }

        if (!me.listenersInitialized) {
            me.grid.rowManager.on({ rendercell : me.renderCell }, me);
            me.store.on({ refresh : me.onStoreRefresh }, me);
            me.listenersInitialized = true;
        }

        grid.refreshRows();

        me.grid.trigger('search', { grid, find, found });

        if (gotoHit && !me.isHitFocused) {
            me.gotoNextHit(true);
        }

        return found;
    }

    /**
     * Clears search results.
     */
    clear(silent = false) {
        const
            me   = this,
            grid = me.grid;

        if (me.foundMap) {
            delete me.foundMap;
        }

        delete me.find;

        DomHelper.forEachSelector(grid.element, '.b-search-hit', cell =>
            DomHelper.removeClasses(cell, ['b-search-hit', 'b-search-hit-cell'])
        );

        DomHelper.removeEachSelector(grid.element, '.b-search-hit-cell-badge,.b-search-hit-text');

        if (me.listenersInitialized) {
            grid.rowManager.un({ rendercell : me.renderCell }, me);
            me.store.un({ refresh : me.onStoreRefresh }, me);
            me.listenersInitialized = false;
        }

        if (!silent) {
            grid.refreshRows();

            me.grid.trigger('clearSearch', { grid });
        }
    }

    /**
     * Number of results found
     * @readonly
     * @returns {Number}
     */
    get foundCount() {
        return (this.found && this.found.length) || 0;
    }

    //endregion

    //region Navigation

    /**
     * Checks if focused row is a search hit.
     * @returns {Boolean} Returns true if focused row is a hit
     * @readonly
     */
    get isHitFocused() {
        const
            me            = this,
            grid          = me.grid,
            currentIndex  = grid.focusedCell ? grid.store.indexOf(grid.focusedCell.id) : -1,
            currentColumn = grid.focusedCell ? grid.columns.getById(grid.focusedCell.columnId) : null;

        return currentIndex !== -1 && me.found.some(hit =>
            hit.index === currentIndex && currentColumn && hit.field === currentColumn.field
        );
    }

    /**
     * Select the next hit, scrolling it into view. Triggered with [f3] or [ctrl]/[cmd] + [g].
     */
    gotoNextHit(fromStart = false) {
        const me = this;

        if (!me.found || !me.found.length) return;

        const grid         = me.grid,
            fromCell     = grid.focusedCell || grid.lastFocusedCell,
            currentIndex = fromCell && !fromStart ? grid.store.indexOf(fromCell.id) : -1,
            nextHit      = me.found.findIndex(hit => hit.index > currentIndex);

        if (nextHit !== -1) {
            me.gotoHit(nextHit);
        }
    }

    /**
     * Select the previous hit, scrolling it into view. Triggered with [shift] + [f3] or [shift] + [ctrl]/[cmd] + [g].
     */
    gotoPrevHit() {
        const me = this;

        if (!me.found || !me.found.length) return;

        const grid         = me.grid,
            fromCell     = grid.focusedCell || grid.lastFocusedCell,
            currentIndex = fromCell ? grid.store.indexOf(fromCell.id) : 0,
            found        = me.found;

        for (let i = found.length - 1; i--; i >= 0) {
            let hit = found[i];
            if (hit.index < currentIndex) {
                me.gotoHit(i);
                break;
            }
        }
    }

    /**
     * Go to specified hit.
     * @param index
     */
    gotoHit(index) {
        let me      = this,
            grid    = me.grid,
            nextHit = me.found[index];

        if (nextHit) {
            grid.focusCell({
                field : nextHit.field,
                id    : nextHit.id
            });
        }

        return !!nextHit;
    }

    /**
     * Go to the first hit.
     */
    gotoFirstHit() {
        this.gotoHit(0);
    }

    /**
     * Go to the last hit.
     */
    gotoLastHit() {
        this.gotoHit(this.found.length - 1);
    }

    //endregion

    //region Render

    /**
     * Called from SubGrid when a cell is rendered. Highlights search hits.
     * @private
     */
    renderCell({ cellElement, column, record, value, cellContent }) {
        const me          = this,
            hitIndex    = me.foundMap && me.foundMap[column.field + '-' + record.id];

        // clear search stuff from cell, might not be done by rendering since it does not always set innerHTML any longer
        //if (me.hitElements.includes(cellElement)) {
        //    const textElement  = cellElement.querySelector('.b-search-hit-text'),
        //        badgeElement = cellElement.querySelector('.b-search-hit-cell-badge');
        //
        //    textElement && textElement.remove();
        //    badgeElement && badgeElement.remove();
        //
        //    cellElement.classList.remove('b-search-hit');
        //    cellElement.classList.remove('b-search-hit-cell');
        //
        //    me.hitElements.splice(me.hitElements.indexOf(cellElement), 1);
        //}

        if (hitIndex) {
            // highlight cell
            cellElement.classList.add('b-search-hit');

            // highlight in cell if found in innerHTML
            const inner = DomHelper.down(cellElement, '.b-grid-cell-value') || cellElement,
                find  = String(me.find).toLowerCase();

            if (String(value).toLowerCase() === find) {
                inner.innerHTML = `<span class="b-search-hit-text">${cellContent}</span><div class="b-search-hit-cell-badge">${hitIndex}</div>`;
            }
            else {
                const find = String(me.find).toLowerCase(),
                    where = cellContent && cellContent.toLowerCase().indexOf(find);

                if (where > -1) {
                    let end       = where + find.length,
                        casedFind = cellContent.slice(where, end);

                    inner.innerHTML = `${cellContent.slice(0, where)}<span class="b-search-hit-text">${casedFind}</span>${cellContent.slice(end)}<div class="b-search-hit-cell-badge">${hitIndex}</div>`;
                }
                else {
                    cellElement.classList.add('b-search-hit-cell');
                }
            }

            me.hitEls.push(cellElement);
        }
    }

    //endregion

    //region Context menu

    /**
     * Add search menu item to cell context menu.
     * @param column
     * @param record
     * @param items
     * @returns {*}
     * @private
     */
    getCellMenuItems(column, record, items) {
        const me = this;

        if (column.searchable) {
            items.push({
                text     : me.L('searchForValue'),
                icon     : 'b-fw-icon b-icon-search',
                cls      : 'b-separator',
                name     : 'search',
                disabled : me.disabled,
                onItem   : ({ item }) => {
                    const { column, record } = item;
                    // TODO: Only extract selection from current cell instead? Lazy way for now
                    let sel = window.getSelection().toString();
                    if (!sel) sel = record[column.field];
                    me.search(sel);
                }
            });
        }

        return items;
    }

    //endregion

    //region Events

    /**
     * Chained function called on grids keydown event. Handles backspace, escape, f3 and ctrl/cmd + g keys.
     * @private
     * @param event KeyboardEvent
     */
    onElementKeyDown(event) {
        const me = this;

        if (me.find && me.find !== '') {
            if (event.key === 'F3' || (event.key.toLowerCase() === 'g' && (event.ctrlKey || event.metaKey))) {
                event.preventDefault();

                if (event.shiftKey) {
                    me.gotoPrevHit();
                }
                else {
                    me.gotoNextHit();
                }
            }
        }
    }

    onStoreRefresh() {
        this.search(this.find, false, true);
    }

    //endregion
}

Search.featureClass = 'b-search';

GridFeatureManager.registerFeature(Search);
