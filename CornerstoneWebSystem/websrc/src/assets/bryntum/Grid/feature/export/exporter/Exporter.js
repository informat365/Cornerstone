import TemplateHelper from '../../../../Core/helper/TemplateHelper.js';
import BrowserHelper from '../../../../Core/helper/BrowserHelper.js';
import Base from '../../../../Core/Base.js';
import Localizable from '../../../../Core/localization/Localizable.js';
import IdHelper from '../../../../Core/helper/IdHelper.js';
import DomHelper from '../../../../Core/helper/DomHelper.js';
import { RowsRange } from '../Utils.js';
import Events from '../../../../Core/mixin/Events.js';
import Delayable from '../../../../Core/mixin/Delayable.js';

/**
 * @module Grid/feature/export/exporter/Exporter
 */

/**
 * Base class for all exporters
 * @mixes Core/localization/Localizable
 * @mixes Core/mixin/Events
 * @private
 */
export default class Exporter extends Delayable(Events(Localizable(Base))) {
    static get defaultConfig() {
        return {
            /**
             * `True` to replace all linked CSS files URLs to absolute before passing HTML to the server.
             * When passing a string the current origin of the CSS files URLS will be replaced by the passed origin.
             *
             * For example: css files pointing to /app.css will be translated from current origin to {translateURLsToAbsolute}/app.css
             * @config {Boolean|String}
             * @default
             */
            translateURLsToAbsolute : true,
    
            /**
             * When true links are converted to absolute by combining current window location (with replaced origin) with
             * resource link.
             * When false links are converted by combining new origin with resource link (for angular)
             * @config {Boolean}
             * @default
             */
            keepPathName : true
        };
    }
    
    /**
     * Template of an extracted page.
     * @param {Object} data Data for the page template
     * @returns {String}
     */
    pageTpl(data) {
        const
            {
                title,
                header,
                footer,
                styles,
                htmlClasses,
                bodyStyle,
                bodyClasses = [],
                paperHeight,
                paperWidth,
                html
            } = data;
        
        bodyClasses.push(`b-${this.constructor.type}`);
        
        return TemplateHelper.tpl`
            <!DOCTYPE html>
            <html class="${htmlClasses}" style="width: ${paperWidth}in; height: ${paperHeight}in;">
                <head>
                    <meta content="text/html; charset=UTF-8" http-equiv="Content-Type" />
                    <title>${title}</title>
                    ${styles.join('')}
                </head>
                <body class="b-export ${bodyClasses.join(' ')}" style="width: ${paperWidth}in; height: ${paperHeight}in; ${bodyStyle}">
                    <div class="b-export-content">
                        ${header && `<div class="b-export-header" style="width: 100%">${header}</div>`}
                        <div class="b-export-body"><div class="b-export-viewport">${html}</div></div>
                        ${footer && `<div class="b-export-footer" style="width: 100%">${footer}</div>`}
                    </div>
                </body>
            </html>`;
    }
    
    /**
     * Returns all style-related tags: `<style>` and `<link rel="stylesheet">`
     * @property {String[]}
     * @readonly
     */
    get stylesheets() {
        const me = this;
        
        if (me._stylesheets) {
            return me._stylesheets;
        }
        
        const
            translate       = me.translateURLsToAbsolute,
            origin          = window.origin,
            styleSheetNodes = Array.from(document.querySelectorAll('link[rel="stylesheet"], style')),
            styles          = [];
    
        styleSheetNodes.forEach(node => {
            node = node.cloneNode(true);
        
            // put absolute URL to node `href` attribute
            if (translate && node.href) {
                let result;
                
                if (translate === true) {
                    result = node.href;
                }
                // translate is truthy if we are here, which means it is string now
                else {
                    if (this.keepPathName) {
                        result = node.href.replace(origin, translate);
                    }
                    else {
                        result = new URL(node.getAttribute('href'), translate);
                    }
                }
    
                node.setAttribute('href', result);
            }
            
            let styleText;
        
            // Empty style tag will be copied in IE, so we need to use cssText
            // http://stackoverflow.com/questions/5227088/creating-style-node-adding-innerhtml-add-to-dom-and-ie-headaches
            if (BrowserHelper.isIE11 && node.styleSheet && /style/i.test(node.tagName)) {
                styleText = `<style type="text/css">${node.styleSheet.cssText}</style>`;
            }
            else {
                styleText = node.outerHTML;
            }
            
            if (translate && /style/i.test(node.tagName)) {
                const converter = me.getStyleTagURLConverter(translate);
                styleText = styleText.replace(/url\(['"]?(.+?)['"]?\)/g, converter);
            }
            
            styles.push(styleText);
        });
    
        styles.push(
            `<style>
                body, html {
                    overflow: auto;
                }
                
                body {
                    position: relative;
                    margin: 0;
                }
            </style>`);
    
        return me._stylesheets = styles;
    }
    
    set stylesheets(value) {
        this._stylesheets = value;
    }
    
    getStyleTagURLConverter(translate) {
        return function(match, url) {
            let result;
            
            try {
                let base;
    
                // Filter out local references, e.g. to svg marker
                if (/^#/.test(url)) {
                    result = match;
                }
                else {
                    if (translate === true) {
                        base = window.location.href;
                    }
                    // translate is truthy if we are here, which means it is string now
                    else {
                        if (this.keepPathName) {
                            base = window.location.href.replace(window.location.origin, translate);
                        }
                        else {
                            base = translate;
                        }
                    }
        
                    result = `url('${new URL(url, base).href}')`;
                }
            }
            catch (e) {
                result = match;
            }
            
            return result;
        }.bind(this);
    }
    
    saveState({ client }) {
        this.state = client.state;
    }
    
    restoreState({ client }) {
        client.state = this.state;
    }
    
    beforeExport() {
        // Into this element we will put HTML for export
        this.element = document.createElement('div');
    }
    
    //region DOM helpers
    
    cloneElement(element, target = this.element, clear = true) {
        if (clear) {
            target.innerHTML = '';
        }
        
        if (BrowserHelper.isChrome) {
            target.appendChild(element.cloneNode(true));
        }
        else {
            target.innerHTML = element.outerHTML;
        }
        
        DomHelper.removeEachSelector(target, '.b-grid-row');
        
        target.querySelector('.b-gridbase > .b-mask').remove();
    }
    
    /**
     * Appends generated header/footer element to the document body to measure their height
     * @param html
     * @returns {number}
     * @private
     */
    measureElement(html = '') {
        if (html instanceof HTMLElement) {
            html = html.outerHTML;
        }
        
        const target = DomHelper.createElement({
            parent : document.body,
            style  : {
                visibility : 'hidden',
                position   : 'absolute'
            },
            // Add html to measure to a div between two other divs to take margin into account
            html : `<div style="height: 1px"></div>${html}<div style="height: 1px"></div>`
        });
        
        const result = target.offsetHeight - 2;
        
        target.remove();
        
        return result;
    }
    
    // Converts local urls to absolute
    prepareHTML(html) {
        if (html instanceof HTMLElement) {
            html = html.outerHTML;
        }
        
        const target = DomHelper.createElement({
            parent : document.body,
            style  : {
                visibility : 'hidden',
                position   : 'absolute'
            },
            html
        });
    
        const elements = target.querySelectorAll('img');
        
        for (let i = 0, l = elements.length; i < l; i++) {
            elements[i].setAttribute('src', elements[i].src);
        }
        
        const result = target.innerHTML;
    
        target.remove();
    
        return result;
    }
    
    createPlaceholder(el, clear = true) {
        if (clear) {
            el.innerHTML = '';
        }
        
        return DomHelper.createElement({
            parent : el,
            id     : IdHelper.generateId('export')
        });
    }
    
    getVirtualScrollerHeight(client) {
        let result = 0;
        
        // If overlay scroll is enabled, this will return 0
        // when disabled, it will report proper virutalScrollers element height
        client.eachSubGrid(subGrid => {
            if (subGrid.overflowingHorizontally) {
                result = DomHelper.scrollBarWidth;
            }
        });

        // If there's a visible scrollbar, need to also take border height into account
        return result === 0 ? result : (result + 1);
    }
    
    //endregion
    
    inchToPx(value) {
        // 1in = 96px for screens
        // https://developer.mozilla.org/en-US/docs/Web/CSS/length#Absolute_length_units
        return value * 96;
    }
    
    getScaleValue(base, value) {
        return Math.floor((base * 10000 / value)) / 10000;
    }
    
    async export(config) {
        const me = this;
        
        me.beforeExport();
        
        me.saveState(config);
        
        await me.prepareComponent(config);
        
        const pages = await me.getPages(config);
        
        await me.restoreComponent(config);
        
        me.stylesheets = null;
        
        // https://app.assembla.com/spaces/bryntum/tickets/9400-scrollrowintoview-promise-is-not-reliable/details
        // Restoring scroll might trigger rows repaint on next animation frame. We are
        // waiting for next animation frame in such case. Covered in SinglePage.t.js
        await new Promise(resolve => me.requestAnimationFrame(resolve));
    
        me.restoreState(config);
        
        return pages;
    }
    
    async getPages(config) {
        const
            generator = this.pagesExtractor(config),
            pages     = [];
        
        let step;
        
        while ((step = await generator.next()) && !step.done) {
            pages.push(step.value);
        }
        
        return pages;
    }
    
    async prepareComponent(config) {
        const
            me          = this,
            { client, columns, rowsRange } = config,
            exportMeta  = me.exportMeta = {
                totalWidth  : 0,
                totalHeight : 0 - me.getVirtualScrollerHeight(client),
                subGrids    : {}
            };
        
        client.columns.forEach(column => {
            if (columns.includes(column.id)) {
                column.show();
            }
            else {
                column.hide();
            }
        });
    
        if (client.rowManager.rowCount > 0) {
            if (rowsRange === RowsRange.all) {
                exportMeta.firstVisibleDataIndex = client.rowManager.rows[0].dataIndex;
            
                await client.scrollable.scrollTo(0, 0);
            }
            else {
                const firstVisibleDataIndex = exportMeta.firstVisibleDataIndex = client.rowManager.rows.find(row => row.bottom > client.scrollable.y).dataIndex;
            
                await client.scrollRowIntoView(client.store.getAt(firstVisibleDataIndex), { block : 'start' });
            
                config.alignRows = true;
            }
        }
    
        // clone whole grid element to the detached container
        this.cloneElement(client.element);
    
        const { element } = me;
    
        client.eachSubGrid(subGrid => {
            const
                placeHolder = me.createPlaceholder(element.querySelector(`[id="${subGrid.id}"]`), false),
                width       = subGrid.columns.visibleColumns.reduce((result, column) => {
                    if (typeof column.width === 'number') {
                        result += column.width;
                    }
                    else {
                        result += client.getHeaderElement(column.id).offsetWidth;
                    }
                    return result;
                }, 0);
            
            exportMeta.totalWidth += width;
            
            const splitterEl = subGrid.splitterElement;
            
            if (splitterEl) {
                exportMeta.totalWidth += splitterEl.offsetWidth;
            }
        
            exportMeta.subGrids[subGrid.region] = {
                id       : subGrid.id,
                headerId : subGrid.header && subGrid.header.id || null,
                footerId : subGrid.footer && subGrid.footer.id || null,
                rows     : [],
                placeHolder,
                width
            };
        });
    }
    
    prepareExportElement() {
        const
            me = this,
            { element, exportMeta } = me;
        
        // Exporters may change subGrid width, e.g. when specific date range is exported
        Object.values(exportMeta.subGrids).forEach(({ width, id, headerId, footerId }) => {
            [id, headerId, footerId].forEach(id => {
                if (id) {
                    element.querySelector(`[id="${id}"]`).style.width = `${width}px`;
                }
            });
        });
        
        return element.innerHTML;
    }
    
    async restoreComponent() {}
    
    async scrollRowIntoView(client, index) {
        await client.scrollRowIntoView(client.store.getAt(index), { block : 'start' });
    
        // #9400 - scrollRowIntoView promise is not reliable
        await new Promise(resolve => this.requestAnimationFrame(resolve));
    }
}

// HACK: terser/obfuscator doesn't yet support async generators, when processing code it converts async generator to regular async
// function.
/**
 * Pages generator. Value should be string with exported HTML
 * @param {Object} config
 * @returns {AsyncIterableIterator<{value: String, done: Boolean}>}
 * @private
 */
Exporter.prototype.pagesExtractor = async function * pagesExtractor() {
    yield '';
};
