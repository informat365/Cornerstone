import TemplateHelper from '../../Core/helper/TemplateHelper.js';
import Bar from './Bar.js';
//import styles from '../../../resources/sass/grid/view/footer.scss';

/**
 * @module Grid/view/Footer
 */

/**
 * Grid footer, used by Summary feature. You should not need to create instances manually.
 *
 * @extends Grid/view/Bar
 * @internal
 */
export default class Footer extends Bar {

    static get $name() {
        return 'Footer';
    }

    static get defaultConfig() {
        return {
            isFooter : true
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
        this._subGrid = this.owner = subGrid;
    }

    refreshContent() {
        this.element.firstElementChild.innerHTML = this.contentTemplate();
        this.fixFooterWidths();
    }

    onPaint({ firstPaint }) {
        if (firstPaint) {
            this.refreshContent();
        }
    }

    template() {
        const region = this.subGrid.region;

        return TemplateHelper.tpl`
            <div class="b-grid-footer-scroller b-grid-footer-scroller-${region}">
                <div reference="footersElement" class="b-grid-footers b-grid-footers-${region}" data-region="${region}"></div>
            </div>
        `;
    }

    get overflowElement() {
        return this.footersElement;
    }

    //region Getters

    /**
     * Get the footer cell element for the specified column.
     * @param {String} columnId Column id
     * @returns {HTMLElement} Footer cell element
     */
    getFooter(columnId) {
        return this.getBarCellElement(columnId);
    }

    //endregion

    /**
     * Footer template. Iterates leaf columns to create content.
     * Style not included because of CSP. Widths are fixed up in
     * {@link #function-fixFooterWidths}
     * @private
     */
    contentTemplate() {
        const me = this;

        return me.columns.visibleColumns.map(column => {
            return column.hidden ? '' : TemplateHelper.tpl`
                <div
                    class="b-grid-footer ${column.align ? `b-grid-footer-align-${column.align}` : ''} ${column.cls || ''}"
                    data-column="${column.field || ''}" data-column-id="${column.id}" data-all-index="${column.allIndex}"
                    >
                    ${column.footerText || ''}
                </div>`;
        }).join('');
    }

    /**
     * Fix footer widths (flex or fixed width) after rendering. Not a part of template any longer because of CSP
     * @private
     */
    fixFooterWidths() {
        this.fixCellWidths();
    }
}
