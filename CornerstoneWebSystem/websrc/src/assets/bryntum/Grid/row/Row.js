import Base from '../../Core/Base.js';
import DomDataStore from '../../Core/data/DomDataStore.js';
import BrowserHelper from '../../Core/helper/BrowserHelper.js';
import DomHelper from '../../Core/helper/DomHelper.js';
import DomSync from '../../Core/helper/DomSync.js';
import Delayable from '../../Core/mixin/Delayable.js';

/**
 * @module Grid/row/Row
 */

/**
 * Represents a single rendered row in the grid. Consists of one row element for each SubGrid in use. The grid only
 * creates as many rows as needed to fill the current viewport (and a buffer). As the grid scrolls
 * the rows are repositioned and reused, there is not a one to one relation between rows and records.
 *
 * For normal use cases you should not have to use this class directly. Rely on using renderers instead.
 * @extends Core/Base
 */
export default class Row extends Delayable(Base) {
    static get defaultConfig() {
        return {
            cls : 'b-grid-row'
        };
    }

    //region Init

    /**
     * Constructs a Row setting its index.
     * @param {Object} config A configuration object which must contain the following two properties:
     * @param {Grid.view.Grid} config.grid The owning Grid.
     * @param {Grid.row.RowManager} config.rowManager The owning RowManager.
     * @param {Number} index The index of the row within the RowManager's cache.
     * @function constructor
     */
    construct(config) {
        // Set up defaults and properties
        Object.assign(this, {
            _elements      : {},
            _elementsArray : [],
            _cells         : {},
            _allCells      : [],
            _regions       : [],
            lastHeight     : 0,
            lastTop        : -1,
            _dataIndex     : 0,
            _top           : 0,
            _height        : 0,
            _id            : null,
            forceInnerHTML : false,
            isGroupFooter  : false
        }, config);

        super.construct();
    }

    doDestroy() {
        const me = this;

        // No need to clean elements up if the entire thing is being destroyed
        if (!me.rowManager.isDestroying) {

            me.removeElements();

            if (me.rowManager.idMap[me.id] === me) {
                delete me.rowManager.idMap[me.id];
            }

        }

        super.doDestroy();
    }

    //endregion

    //region Data getters/setters

    /**
     * Get index in RowManagers rows array
     * @property {Number}
     * @readonly
     */
    get index() {
        return this._index;
    }

    set index(index) {
        this._index = index;
    }

    /**
     * Get/set this rows current index in grids store
     * @property {Number}
     */
    get dataIndex() {
        return this._dataIndex;
    }

    set dataIndex(dataIndex) {
        if (this._dataIndex !== dataIndex) {
            const elements = this._elementsArray;

            this._dataIndex = dataIndex;
            for (let i = 0; i < elements.length; i++) {
                elements[i].dataset.index = dataIndex;
            }
        }
    }

    /**
     * Get/set id for currently rendered record
     * @property {String|Number}
     */
    get id() {
        return this._id;
    }

    set id(id) {
        const me    = this,
            idObj = { id },
            idMap = me.rowManager.idMap,
            elements = me._elementsArray,
            cells = me._allCells;

        if (me._id != id || idMap[id] !== me) {
            if (idMap[me._id] === me) delete idMap[me._id];
            idMap[id] = me;

            me._id = id;
            for (let i = 0; i < elements.length; i++) {
                DomDataStore.assign(elements[i], idObj);
            }
            for (let i = 0; i < cells.length; i++) {
                DomDataStore.assign(cells[i], idObj);
            }

            for (let i = 0; i < elements.length; i++) {
                elements[i].dataset.id = id;
            }
        }
    }

    //endregion

    //region Row elements

    /**
     * Add a row element for specified region.
     * @param {String} region Region to add element for
     * @param {HTMLElement} element Element
     * @private
     */
    addElement(region, element) {
        const me = this;

        let cellElement = element.firstElementChild;

        me._elements[region] = element;
        me._elementsArray.push(element);
        me._regions.push(region);
        DomDataStore.assign(element, { index : me.index });

        me._cells[region] = [];

        while (cellElement) {
            me._cells[region].push(cellElement);
            me._allCells.push(cellElement);

            DomDataStore.set(cellElement, {
                column     : cellElement.dataset.column, // TODO: dataset is slow, read from columnstore using index instead
                columnId   : cellElement.dataset.columnId,
                rowElement : cellElement.parentNode,
                row        : me
            });

            cellElement = cellElement.nextElementSibling;
        }

        // making css selectors simpler, dataset has bad performance but it is only set once and never read
        element.dataset.index = me.index;
    }

    /**
     * Get the element for the specified region.
     * @param {String} region
     * @returns {HTMLElement}
     */
    getElement(region) {
        return this._elements[region];
    }

    /**
     * Execute supplied function for each regions element.
     * @param {Function} fn
     */
    eachElement(fn) {
        for (let i = 0; i < this._elementsArray.length; i++) {
            fn(this._elementsArray[i], i);
        }
        //this._elementsArray.forEach(fn);
    }

    /**
     * Execute supplied function for each cell.
     * @param {Function} fn
     */
    eachCell(fn) {
        this._allCells.forEach(fn);
    }

    /**
     * Row elements (one for each region)
     * @type {HTMLElement[]}
     * @readonly
     */
    get elements() {
        return this._elements;
    }

    //endregion

    //region Cell elements

    /**
     * Row cell elements
     * @returns {HTMLElement[]}
     * @readonly
     */
    get cells() {
        return this._allCells;
    }

    /**
     * Get cell elements for specified region.
     * @param {String} region Region to get elements for
     * @returns {HTMLElement[]} Array of cell elements
     */
    getCells(region) {
        return this._cells[region];
    }

    /**
     * Get the cell element for the specified column.
     * @param {String|Number} columnId Column id
     * @returns {HTMLElement} Cell element
     */
    getCell(columnId) {
        return this._allCells.find(cell => {
            const cellData = DomDataStore.get(cell);
            return cellData.columnId === columnId || cellData.column === columnId;
        });
    }

    removeElements(onlyRelease = false) {
        const me = this;

        // Triggered before the actual remove to allow cleaning up elements etc.
        me.rowManager.trigger('removeRow', { row : me });

        if (!onlyRelease) {
            me._elementsArray.forEach(element => element.remove());
        }
        me._elements = {};
        me._cells = {};
        me._elementsArray.length = me._regions.length = me._allCells.length = me.lastHeight = me.height = 0;
        me.lastTop = -1;
    }

    //endregion

    //region Height

    /**
     * Get/set row height
     * @property {Number}
     */
    get height() {
        return this._height;
    }

    set height(height) {
        this._height = height;
    }

    /**
     * Get row height including border
     * @property {Number}
     */
    get offsetHeight() {
        // me.height is specified height, add border height to it to get cells height to match specified rowHeight
        // border height is measured in Grid#get rowManager
        return this.height + this.grid._rowBorderHeight;
    }

    /**
     * Sets row height, but only if the height is bigger than currently set height (used when rendering rows to
     * match height for highest region element)
     * @param height Height to set
     * @private
     */
    setHeightIfBigger(height) {
        if (height > this.height) this.height = height;
    }

    /**
     * Sync elements height to rows height
     * @private
     */
    updateElementsHeight() {
        const me = this;
        // prevent unnecessary style updates
        if (me.lastHeight !== me.height) {
            const elements = me._elementsArray;

            for (let i = 0; i < elements.length; i++) {
                elements[i].style.height = `${me.offsetHeight}px`;
            }
            me.lastHeight = me.height;
        }
    }

    //endregion

    //region CSS

    /**
     * Add css classes to each element.
     * @param {...String} classes
     */
    addCls(...classes) {
        this.eachElement(element => DomHelper.addClasses(element, classes));
    }

    /**
     * Remove css classes from each element.
     * @param {...String} classes
     */
    removeCls(...classes) {
        this.eachElement(element => DomHelper.removeClasses(element, classes));
    }

    //endregion

    //region Position

    /**
     * Is this the very first row?
     * @property {Boolean}
     * @readonly
     */
    get isFirst() {
        return this.dataIndex === 0;
    }

    /**
     * Row top coordinate
     * @property {Number}
     * @readonly
     */
    get top() {
        return this._top;
    }

    /**
     * Row bottom coordinate
     * @property {Number}
     * @readonly
     */
    get bottom() {
        return this._top + this._height + this.grid._rowBorderHeight;
    }

    /**
     * Sets top coordinate, translating elements position.
     * @param top Top coordinate
     * @internal
     */
    setTop(top) {
        if (this._top !== top) {
            this._top = top;
            this.translateElements();
        }
    }

    /**
     * Sets bottom coordinate, translating elements position.
     * @param bottom Bottom coordinate
     * @private
     */
    setBottom(bottom) {
        this.setTop(bottom - this.offsetHeight);
    }

    /**
     * Sets css transform to position elements at correct top position (translateY)
     * @private
     */
    translateElements() {
        const me           = this,
            positionMode = me.grid.positionMode;

        if (me.lastTop !== me.top) {
            const elements = me._elementsArray;

            for (let i = 0; i < elements.length; i++) {
                const style = elements[i].style;

                if (positionMode === 'translate') {
                    style.transform = `translate(0,${me.top}px)`;
                }
                else if (positionMode === 'translate3d') {
                    style.transform = `translate3d(0,${me.top}px,0)`;
                }
                else if (positionMode === 'position') {
                    style.top = `${me.top}px`;
                }
            }
            me.rowManager.trigger('translateRow', { row : me });
            me.lastTop = me.top;
        }
    }

    /**
     * Moves all row elements up or down and updates model.
     * @param {Number} offsetTop Pixels to offset the elements
     * @private
     */
    offset(offsetTop) {
        let newTop = this._top + offsetTop;

        // Not allowed to go below zero (won't be reachable on scroll in that case)
        if (newTop < 0) {
            offsetTop -= newTop;
            newTop = 0;
        }
        this.setTop(newTop);
        return offsetTop;
    }

    //endregion

    //region Render

    /**
     * Renders a record into this rows elements (trigger event that subgrids catch to do the actual rendering).
     * @param {Core.data.Model} record
     * @private
     */
    render(recordIndex, record, updatingSingleRow = true, batch = false) {
        const
            me              = this,
            { cells, grid } = me,
            oldId           = me._id,
            elements        = me._elementsArray,
            rowElData       = DomDataStore.get(elements[0]),
            rowManager      = me.rowManager,
            rowHeight       = rowManager._rowHeight,
            oldHeight       = me.height, // not using getter since we don't want to use average row height here
            defaultRowCls   = updatingSingleRow && grid.transitionDuration ? `${me.cls} b-grid-row-updating` : me.cls;

        let i = 0,
            rowElement,
            cell,
            size;

        // no record specified, try looking up in store (false indicates empty row, don't do lookup
        if (!record && record !== false) {
            record = grid.store.getById(rowElData.id);
            recordIndex = grid.store.indexOf(record);
        }

        const selected = record && grid.isSelected(record.id);

        // used by GroupSummary feature to clear row before
        rowManager.trigger('beforeRenderRow', { row : me, record, oldId });

        for (i; i < elements.length; i++) {
            rowElement = elements[i];

            // Clean classList
            rowElement.className = defaultRowCls;

            // Apply cls from data directly to row
            // TODO: should be configurable on grid
            if (selected) {
                rowElement.classList.add('b-selected');
            }

            // no record & data says it is displayed, "undisplay" it
            if (!record) {
                // TODO: Can this scenario still happen?
                rowElData.displayed = false;
                rowElement.classList.add('b-grid-row-not-displayed');
            }

            if (record) {
                if (record.cls) {
                    record.cls.split(' ').forEach(cls => rowElement.classList.add(cls));
                }
                // has record and data says it is not displayed, flag as displayed
                rowElData.displayed = true;
            }
        }

        if (updatingSingleRow && grid.transitionDuration) {
            me.setTimeout(() => {
                // We should iterate all elements, in case we have regions in the grid
                elements.forEach(element => {
                    element.classList.remove('b-grid-row-updating');
                });
            }, grid.transitionDuration);
        }

        // Will be set from renderers
        me.height = 0;

        if (record) {
            me.id = record.id;
            me.dataIndex = recordIndex;
            //<debug>
            if (me.dataIndex === -1) {
                throw new Error(`Row's record, id: ${record.id} not found in store`);
            }
            //</debug>
        }
        else {
            // More rows then records, render it empty for now...
            me.id = null;
            me.dataIndex = null;
        }

        for (i = 0; i < cells.length; i++) {
            cell = cells[i];

            size = me.renderCell(cell, record, i, updatingSingleRow);
            // we want to make row in all parts as high as the highest cell
            me.setHeightIfBigger((size && size.height) || rowHeight);
        }

        // Height gets set during render, reflect on elements
        me.updateElementsHeight();

        // Rerendering a row might change its height, which forces translation of all following rows
        if (updatingSingleRow) {
            if (oldHeight !== me.height) {
                rowManager.translateFromRow(me, batch);
            }
            rowManager.trigger('updateRow', { row : me, record, oldId });
            rowManager.trigger('renderDone');
        }

        // TODO: Make direct function call if no more features than Stripe needs this
        rowManager.trigger('renderRow', { row : me, record, oldId });

        me.forceInnerHTML = false;
    }

    /**
     * Renders a single cell, calling features to allow them to hook.
     * @param cellElement Cell element to render
     * @param record Record, fetched from store if undefined
     * @param index
     * @param updatingSingleRow
     * @private
     */
    renderCell(cellElement, record, index, updatingSingleRow = true) {
        const me              = this,
            cellElementData = DomDataStore.get(cellElement),
            col             = me.grid.columns.getById(cellElementData.columnId),
            rowElement      = cellElementData.rowElement,
            rowElementData  = DomDataStore.get(rowElement),
            { // Avoid two calls to col's getters by gathering these fields.
                internalCellCls,
                cellCls,
                align,
                renderer,
                defaultRenderer,
                id : columnId
            }               = col,
            cellContext     = { columnId, id : rowElementData.id },
            useRenderer     = renderer || defaultRenderer;

        if (!record) {
            // Clear out row without record
            // Edge case, only happens if groups/tree is collapsed leading to fewer records than row elements
            if (rowElementData.id === null) {
                // setting to ' ' (space) makes it not remove firstChild, thus not breaking
                // when doing real render the next time
                // NOTE: have opted to not clear cell. might be confusing in DOM but simplifies for cells reusing
                // elements internally. Another option would be to have a derenderer per column which is called
                // cell.innerHTML = ' ';
                cellElement.className = 'b-grid-cell';
                return;
            }

            record = me.grid.store.getById(rowElementData.id);

            if (!record) return;
        }

        const newCellClass = {
            'b-grid-cell'                  : 1,
            [internalCellCls]              : internalCellCls,
            [cellCls]                      : cellCls,
            'b-cell-dirty'                 : record.isFieldModified(col.field),
            [`b-grid-cell-align-${align}`] : align,
            'b-selected'                   : me.grid.isSelected(cellContext),
            'b-focused'                    : me.grid.isFocused(cellContext)
        };
        DomHelper.syncClassList(cellElement, newCellClass);

        let // TODO: record.rowHeight should be configurable (which field)
            size             = { height : record.get('rowHeight') || 0 },
            cellContent      = col.getRawValue(record),
            shouldSetContent = true,
            rendererData     = {
                cellElement,
                rowElement,
                value  : cellContent,
                record : record,
                column : col,
                size   : size,
                grid   : me.grid,
                row    : cellElementData.row,
                updatingSingleRow
            };

        // Call renderer or set innerHTML directly if none
        // if renderer returns a value, set it in innerHTML below. render is also free to do it itself
        if (useRenderer) {
            cellContent = useRenderer.call(col, rendererData);
            if (cellContent === undefined) {
                shouldSetContent = false;
            }
        }

        // Allow wrappers to do their own processing of cell contents, for example react wrapper allows JSX
        if (me.grid.processCellContent) {
            shouldSetContent = me.grid.processCellContent({
                cellContent,
                cellElement,
                record,
                cellElementData,
                rendererData
            });
        }

        // Listeners might need to know what string went into the DOM
        // Maintainer: the == null test below ensures that values of both null and undefined
        // result in an empty cell.
        const text = rendererData.cellContent = cellContent == null ? '' : String(cellContent);

        if (shouldSetContent) {
            // row might be flagged by GroupSummary to require full "redraw"
            if (me.forceInnerHTML) {
                cellElement.innerHTML = text;
            }
            // display cell contents as text or use actual html?
            // (disableHtmlEncode set by features that decorate cell contents)
            else if (col.htmlEncode && !col.disableHtmlEncode) {
                // Set innerText if cell currently has html content (from for example group renderer),
                // always do it on Linux, it does not have firstChild.data
                if (BrowserHelper.isLinux || cellElement._hasHtml) {
                    cellElement.innerText = text;
                    cellElement._hasHtml = false;
                }
                else {
                    // setting firstChild.data is faster than innerText (and innerHTML),
                    // but in some cases the inner node is lost and needs to be recreated
                    const firstChild = cellElement.firstChild;
                    if (!firstChild) {
                        cellElement.innerText = text;
                    }
                    else {
                        firstChild.data = text;
                    }
                }
            }
            else {
                const
                    hasStringContent = typeof cellContent === 'string',
                    hasObjectContent = typeof cellContent === 'object';
                if (col.autoSyncHtml && (!hasStringContent || cellElement.childElementCount)) {
                    // String content in html column is handled as a html template string
                    if (hasStringContent) {
                        // update cell with only changed attributes etc.
                        DomHelper.sync(text, cellElement.firstElementChild);
                    }
                    // Other content is considered to be a DomHelper config object
                    else {
                        DomSync.sync({
                            domConfig     : cellContent,
                            targetElement : cellElement
                        });
                    }
                }
                // Consider all returned plain objects to be DomHelper configs for cell content
                else if (hasObjectContent) {
                    DomSync.sync({
                        targetElement : cellElement,
                        domConfig     : {
                            onlyChildren : true,
                            children     : [
                                cellContent
                            ]
                        }
                    });
                }
                else {
                    cellElement.innerHTML = text;
                }
            }
        }

        // Allow others to affect rendering
        me.rowManager.trigger('renderCell', rendererData);

        return size;
    }

//endregion
}
