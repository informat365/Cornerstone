import Bar from './Bar.js';
import TemplateHelper from '../../Core/helper/TemplateHelper.js';
import DomClassList from '../../Core/helper/util/DomClassList.js';

/**
 * @module Grid/view/Header
 */

/**
 * The Grid header, which contains simple columns but also allows grouped columns. One instance is created and used per SubGrid
 * automatically, you should not need to instantiate this class manually. See {@link Grid.column.Column} for information about
 * column configuration.
 *
 * @extends Grid/view/Bar
 * @internal
 *
 * @externalexample grid/Header.js
 */
export default class Header extends Bar {

    static get $name() {
        return 'Header';
    }

    static get defaultConfig() {
        return {
            isHeader : true
        };
    }

    startConfigure(config) {
        config.scrollable.overflowX = 'hidden-scroll';
        super.startConfigure(config);
    }

    get subGrid() {
        return this._subGrid;
    }

    set subGrid(subGrid) {
        this._subGrid = subGrid;
        this.id = subGrid.id + '-header';
    }

    get region() {
        return this.subGrid && this.subGrid.region;
    }

    template() {
        const me     = this,
            region = me.region;

        return TemplateHelper.tpl`
            <div class="b-grid-header-scroller b-grid-header-scroller-${region}">
                <div reference="headersElement" class="b-grid-headers b-grid-headers-${region}" data-region="${region}" data-max-depth="${me.maxDepth}" reference="headersElement"></div>
            </div>
        `;
    }

    get overflowElement() {
        return this.headersElement;
    }

    /**
     * Header template, recursive template for column headers.
     * Style not included because of CSP. Widths are fixed up in
     * {@link #function-fixHeaderWidths}
     * @private
     */
    contentTemplate(column) {
        const me = this;

        if (column.hidden) {
            return '';
        }
        else {
            /* eslint-disable */
            return TemplateHelper.tpl`
                <div class="b-grid-header" data-column="${column.field || ''}" data-column-id="${column.id}" ${column.isLeaf ? 'tabindex="0"' : ''}>
                    <div class="b-grid-header-text">
                        <div class="b-grid-header-text-content"></div>
                    </div>
                    ${column.children ? `
                    <div class="b-grid-header-children">
                            ${column.children.map(child =>
                            me.contentTemplate(child)
                        ).join('')}
                    </div>
                    ` : ''}
                    <div class="b-grid-header-resize-handle"></div>
                </div>
            `;
            /* eslint-enable */
        }
    }

    // used by safari to fix flex when rows width shrink below this value
    calculateMinWidthForSafari() {
        let minWidth = 0,
            columns  = this.columns.visibleColumns;

        columns.forEach(column => {
            minWidth += column.calculateMinWidth();
        });

        return minWidth;
    }

    /**
     * Fix header widths (flex or fixed width) after rendering. Not a part of template any longer because of CSP
     * @private
     */
    fixHeaderWidths() {
        this.fixCellWidths();
    }

    refreshHeaders() {
        const me = this;

        // run renderers, not done from template to work more like cell rendering
        me.columns.traverse(column => {
            const headerElement = me.getBarCellElement(column.id);

            if (headerElement) {
                const classList = new DomClassList({
                    'b-grid-header'                         : 1,
                    'b-grid-header-parent'                  : column.isParent,
                    [`b-level-${column.childLevel}`]        : 1,
                    [`b-depth-${column.meta.depth}`]        : 1,
                    [`b-grid-header-align-${column.align}`] : column.align,
                    'b-grid-header-resizable'               : column.resizable && column.isLeaf,
                    [column.cls]                            : column.cls,
                    'b-last-parent'                         : column.isParent && column.isLastInSubGrid,
                    'b-last-leaf'                           : column.isLeaf && column.isLastInSubGrid
                });

                let html = column.text;

                headerElement.className = classList;

                if (column.headerRenderer) {
                    html = column.headerRenderer.call(column.thisObj || me, { column, headerElement });
                }

                if (column.icon) {
                    html = `<i class="${column.icon}"></i>` + (html || '');
                }

                const innerEl = headerElement.querySelector('.b-grid-header-text-content');
                if (innerEl) {
                    innerEl.innerHTML = html || '';
                }
            }
        });

        me.fixHeaderWidths();
    }

    get columns() {
        const
            me     = this,
            result = super.columns;

        if (!me.columnsDetacher) {
            // columns is a chained store, it will be repopulated from master when columns change.
            // That action always triggers change with action dataset.
            me.columnsDetacher = result.on({
                change() {
                    me.initDepths();
                },
                thisObj : me
            });

            me.initDepths();
        }

        return result;
    }

    set columns(columns) {
        super.columns = columns;
    }

    /**
     * Depths are used for styling of grouped headers. Sets them on meta.
     * @private
     */
    initDepths(columns = this.columns.topColumns, parent = null) {
        let me       = this,
            maxDepth = 0;

        if (parent && parent.meta) parent.meta.depth++;

        for (let column of columns) {
            // TODO: this should maybe move
            column.meta.depth = 0;

            if (column.children) {
                me.initDepths(column.children.filter(me.columns.chainedFilterFn), column);
                if (column.meta.depth && parent) parent.meta.depth += column.meta.depth;
            }

            if (column.meta.depth > maxDepth) maxDepth = column.meta.depth;
        }

        if (!parent) {
            me.maxDepth = maxDepth;
        }

        return maxDepth;
    }

    //endregion

    //region Getters

    /**
     * Get the header cell element for the specified column.
     * @param {String} columnId Column id
     * @returns {HTMLElement} Header cell element
     */
    getHeader(columnId) {
        return this.getBarCellElement(columnId);
    }

    //endregion

    get contentElement() {
        return this.element.firstElementChild;
    }

    refreshContent() {
        const me = this;

        me.content = me.columns.topColumns.map(col => me.contentTemplate(col)).join('');

        me.refreshHeaders();
    }

    onPaint({ firstPaint }) {
        if (firstPaint) {
            this.refreshContent();
        }
    }
}
