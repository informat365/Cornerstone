//TODO: Handle date columns
//TODO: Icons to navigate between hits in the header? or in each cell?
//TODO: Icon to clear quickfind in header
//TODO: regex for valid key pressed?

import DomDataStore from '../../Core/data/DomDataStore.js';
import DomHelper from '../../Core/helper/DomHelper.js';
import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import GridFeatureManager from '../feature/GridFeatureManager.js';
import BrowserHelper from '../../Core/helper/BrowserHelper.js';

/**
 * @module Grid/feature/QuickFind
 */

/**
 * Feature that allows the user to search in a column by focusing a cell and typing. Navigate between hits using the
 * keyboard, [f3] or [ctrl]/[cmd] + [g] moves to next, also pressing [shift] moves to previous.
 *
 * This feature is <strong>disabled</strong> by default.
 *
 * @extends Core/mixin/InstancePlugin
 *
 * @example
 * // enable QuickFind
 * let grid = new Grid({
 *   features: {
 *     quickFind: true
 *   }
 * });
 *
 * // navigate to next hit programmatically
 * grid.features.quickFind.gotoNextHit();
 *
 * @demo Grid/quickfind
 * @classtype quickFind
 * @externalexample feature/QuickFind.js
 */
export default class QuickFind extends InstancePlugin {
    //region Config

    static get $name() {
        return 'QuickFind';
    }

    static get defaultConfig() {
        return {
            mode : 'header',
            find : ''
        };
    }

    // Plugin configuration. This plugin chains some of the functions in Grid.
    static get pluginConfig() {
        return {
            chain : ['onElementKeyDown', 'onElementKeyPress', 'onCellNavigate']
        };
    }

    //endregion

    //region Init

    construct(grid, config) {
        const me = this;

        me.grid = grid;
        me.store = grid.store;

        super.construct(grid, config);
    }

    doDisable(disable) {
        if (disable) {
            this.clear();
        }

        super.doDisable(disable);
    }

    //endregion

    //region Show/hide QuickFind

    /**
     * Shows a "searchfield" in the header. Triggered automatically when you have a cell focused and start typing.
     * @private
     */
    showQuickFind() {
        const me     = this,
            header = me.grid.getHeaderElement(me.columnId);

        if (header) {
            if (!me.headerField) {
                const [element, field, badge] = DomHelper.createElement({
                    tag       : 'div',
                    className : 'b-quick-hit-header',
                    children  : [
                        { tag : 'div', className : 'b-quick-hit-field' },
                        { tag : 'div', className : 'b-quick-hit-badge' }
                    ]
                }, true);

                if (me.mode === 'header') {
                    header.appendChild(element);
                }
                else {
                    element.className += ' b-quick-hit-mode-grid';
                    me.grid.element.appendChild(element);
                }

                me.headerField = {
                    header    : element,
                    field     : field,
                    badge     : badge,
                    colHeader : header
                };
            }

            me.headerField.field.innerHTML = me.find;
            me.headerField.badge.innerHTML = me.found.length;

            header.classList.add('b-quick-find-header');

            if (!me.renderListenerInitialized) {
                me.grid.rowManager.on({
                    rendercell : me.renderCell,
                    thisObj    : me
                });
                me.renderListenerInitialized = true;
            }
        }
    }

    /**
     * Hide the "searchfield" and remove highlighted hits. Called automatically when pressing [esc] or backspacing away
     * the keywords.
     * @private
     */
    hideQuickFind() {
        const me = this;

        // rerender cells to remove quick-find markup
        for (let hit of (me.prevFound || me.found)) {
            let row = me.grid.getRowById(hit.id);
            if (row) row.renderCell(row.getCell(me.columnId), hit.data);
        }

        if (me.headerField) {
            me.headerField.header.parentNode.removeChild(me.headerField.header);
            me.headerField.colHeader.classList.remove('b-quick-find-header');
            me.headerField = null;
        }

        if (me.renderListenerInitialized) {
            me.grid.rowManager.un({ rendercell : me.renderCell }, me);
            me.renderListenerInitialized = false;
        }

        me.grid.trigger('hideQuickFind');
    }

    //endregion

    //region Search

    /**
     * Performs a search and highlights hits. If find is empty, QuickFind is closed.
     * @param find Text to search for
     * @param columnFieldOrId Column to search
     */
    search(find, columnFieldOrId = this.columnId) {
        let me     = this,
            column = me.grid.columns.getById(columnFieldOrId) || me.grid.columns.get(columnFieldOrId),
            found  = me.store.findByField(column.field, find),
            i      = 1,
            grid   = me.grid;

        Object.assign(me, {
            foundMap  : {},
            prevFound : me.found,
            found     : found,
            find      : find,
            columnId  : column.id
        });

        if (find) {
            me.showQuickFind();
        }
        else {
            me.hideQuickFind();
        }

        // reset column to use its normal settings for htmlEncoding
        if (me.currentColumn && me.currentColumn !== column) me.currentColumn.disableHtmlEncode = false;

        // clear old hits
        for (let cell of DomHelper.children(grid.element, '.b-quick-hit')) {
            //IE11 doesnt support this
            //cell.classList.remove('b-quick-hit', 'b-quick-hit-cell');
            cell.classList.remove('b-quick-hit');
            cell.classList.remove('b-quick-hit-cell');

            // rerender cell to remove quick-hit-text
            let row = DomDataStore.get(cell).row;
            row.renderCell(cell);
        }

        // want to set innerHTML each time for cell decoration to work
        column.disableHtmlEncode = true;
        me.currentColumn = column;

        if (!found) return;

        if (found.length > 0) {
            me.gotoClosestHit(grid.focusedCell, found);
        }

        // highlight hits for visible cells
        for (let hit of found) {
            me.foundMap[hit.id] = i++;

            let row = grid.getRowById(hit.data.id);
            if (row) {
                row.renderCell(row.getCell(column.id));
            }

            // limit highlighted hits
            if (i > 1000) break;
        }

        me.grid.trigger('quickFind', { find, found });
    }

    /**
     * Clears and closes QuickFind.
     */
    clear() {
        if (this.found && this.found.length) {
            this.search('');
        }
    }

    /**
     * Number of results found
     * @type {Number}
     * @readonly
     */
    get foundCount() {
        return this.found ? this.found.length : 0;
    }

    /**
     * Found results (as returned by Store#findByField), an array in format { index: x, data: record }
     * @member {Object[]} found
     * @readonly
     */

    //endregion

    //region Navigation

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
                columnId : me.columnId,
                id       : nextHit.id
            });
        }

        return !!nextHit;
    }

    gotoClosestHit(focusedCell, found) {
        let focusedIndex = focusedCell ? this.grid.store.indexOf(focusedCell.id) : 0,
            foundSorted  = found.slice().sort(
                (a, b) => Math.abs(a.index - focusedIndex) - Math.abs(b.index - focusedIndex)
            );

        this.gotoHit(found.indexOf(foundSorted[0]));
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

    /**
     * Select the next hit, scrolling it into view. Triggered with [f3] or [ctrl]/[cmd] + [g].
     */
    gotoNextHit() {
        let me           = this,
            grid         = me.grid,
            // start from focused cell, or if focus has left grid use lastFocusedCell
            currentId    = grid._focusedCell ? grid._focusedCell.id : grid.lastFocusedCell.id,
            currentIndex = grid.store.indexOf(currentId) || 0,
            nextHit      = me.found.find(hit => hit.index > currentIndex);

        if (nextHit) {
            grid.focusCell({
                columnId : me.columnId,
                id       : nextHit.id
            });
        }
        else {
            me.gotoFirstHit();
        }
    }

    /**
     * Select the previous hit, scrolling it into view. Triggered with [shift] + [f3] or [shift] + [ctrl]/[cmd] + [g].
     */
    gotoPrevHit() {
        let me           = this,
            grid         = me.grid,
            currentId    = grid._focusedCell ? grid._focusedCell.id : grid.lastFocusedCell.id,
            currentIndex = grid.store.indexOf(currentId) || 0,
            found        = me.found,
            prevHit;

        if (!found.length) return;

        for (let i = found.length - 1; i--; i >= 0) {
            if (found[i].index < currentIndex) {
                prevHit = found[i];
                break;
            }
        }

        if (prevHit) {
            grid.focusCell({
                columnId : me.columnId,
                id       : prevHit.id
            });
        }
        else {
            me.gotoLastHit();
        }
    }

    //endregion

    //region Render

    /**
     * Called from SubGrid when a cell is rendered.
     * @private
     */
    renderCell(renderData) {
        const
            me          = this,
            cellElement = renderData.cellElement,
            foundMap    = me.foundMap && me.columnId === renderData.column.id && me.foundMap[renderData.record.id];

        if (foundMap) {
            // Check also TreeColumn's special internal `shouldHtmlEncode` flag
            const htmlEncoded = renderData.column.htmlEncode || renderData.column.shouldHtmlEncode;

            // highlight cell
            cellElement.classList.add('b-quick-hit');

            // Special treatment of columns outputting HTML, just highlight whole cell
            if (!htmlEncoded) {
                cellElement.classList.add('b-quick-hit-cell');
            }
            else {
                // if features have added other stuff to the cell, value is in div.b-grid-cell-value
                // highlight in cell if found in innerHTML
                let inner = DomHelper.down(cellElement, '.b-grid-cell-value,.b-tree-cell-value') || cellElement,
                    html  = inner.innerText,
                    where = html && html.toLowerCase().indexOf(me.find.toLowerCase());

                if (where > -1) {
                    let end       = where + me.find.length,
                        casedFind = html.slice(where, end),
                        spaceChar = '';

                    // Insert a space if matching char preceded by whitespace
                    if (html[where - 1] === ' ') {
                        spaceChar = '&nbsp;';
                    }

                    html            = html.slice(0, where) + `<span class="b-quick-hit-text">${spaceChar}${casedFind}</span>` + html.slice(end);
                    inner.innerHTML = html + `<div class="b-quick-hit-cell-badge">${foundMap}</div>`;
                }
                else {
                    cellElement.classList.add('b-quick-hit-cell');
                }
            }
        }
    }

    //endregion

    //region Events

    /**
     * Chained function called on grids keydown event. Handles backspace, escape, f3 and ctrl/cmd + g keys.
     * @private
     * @param event KeyboardEvent
     */
    onElementKeyDown(event) {
        const
            me            = this,
            filterFeature = this.grid.features.filter;

        // Only react to keystrokes on grid cell elements. IE11 gets event.target wrong compared to other browsers...
        if (me.disabled || DomHelper.up(event.target, BrowserHelper.isIE11 ? '.b-widget:not(.b-grid-subgrid):not(.b-grid)' : '.b-widget:not(.b-grid)')) {
            return;
        }

        if (me.find.length > 0) {
            // backspace
            if (event.key === 'Backspace') {
                event.preventDefault();
                me.find = me.find.substr(0, me.find.length - 1);
                //console.log(me.find);

                me.search(me.find);
            }

            // escape
            else if (event.key === 'Escape') {
                event.preventDefault();
                me.find = '';
                me.search(me.find);
            }

            // F3 or CTRL+g
            else if (event.key === 'F3' || (event.key.toLowerCase() === 'g' && (event.ctrlKey || event.metaKey))) {
                event.preventDefault();
                if (event.shiftKey) {
                    me.gotoPrevHit();
                }
                else {
                    me.gotoNextHit();
                }
            }
            // Ctrl+Shift+F
            else if (filterFeature && me.columnId && me.foundCount && event.ctrlKey && event.shiftKey && event.key === 'F') {
                filterFeature.showFilterEditor(me.grid.columns.getById(me.columnId), me.find);
            }
        }
    }

    /**
     * Chained function called on grids keypress event. Handles input for "searchfield".
     * @private
     * @param event
     */
    onElementKeyPress(event) {
        const me = this;

        // Only react to keystrokes on grid cell elements
        if (me.disabled || DomHelper.up(event.target, BrowserHelper.isIE11 ? '.b-widget:not(.b-grid-subgrid):not(.b-grid)' : '.b-widget:not(.b-grid)') || event.key === 'Enter') {
            return;
        }

        if (me.grid._focusedCell) {
            const column = me.grid.columns.getById(me.grid._focusedCell.columnId);
            // if trying to search in invalid column, it's a hard failure
            //<debug>
            console.assert(column, 'Focused cell column not found');
            //</debug>
            if (column && column.searchable !== false) {
                me.columnId = me.grid._focusedCell.columnId;

                if (event.key && event.key.length === 1) {
                    me.find += event.key;
                    me.search(me.find);
                }
            }
        }
    }

    onCellNavigate(grid, fromCellSelector, toCellSelector, event) {
        const
            me    = this,
            found = me.prevFound || me.found;

        if (found && (!toCellSelector || toCellSelector.columnId !== me.columnId)) {
            me.clear();
        }
    }

    //endregion
}

GridFeatureManager.registerFeature(QuickFind);
