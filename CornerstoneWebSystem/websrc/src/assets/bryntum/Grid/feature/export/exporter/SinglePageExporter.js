import Exporter from './Exporter.js';
import { Orientation, PaperFormat, RowsRange } from '../../../feature/export/Utils.js';

export default class SinglePageExporter extends Exporter {
    static get type() {
        return 'singlepage';
    }
    
    static get title() {
        // In case locale is missing exporter is still distinguishable
        return this.L('singlepage');
    }
    
    static get defaultConfig() {
        return {
            /**
             * Set to true to center content horizontally on the page
             * @config {Boolean}
             */
            centerContentHorizontally : false
        };
    }
    
    async prepareComponent(config) {
        await super.prepareComponent(config);
        
        Object.assign(this.exportMeta, {
            verticalPages      : 1,
            horizontalPages    : 1,
            totalPages         : 1,
            currentPage        : 0,
            verticalPosition   : 0,
            horizontalPosition : 0
        });
    }
    
    async onRowsCollected() {}
    
    positionRows(rows) {
        let currentTop = 0;
    
        // In case of variable row height row vertical position is not guaranteed to increase
        // monotonously. Position row manually instead
        return rows.map(([html, height]) => {
            const result = html.replace(/translate\(\d+px, \d+px\)/, `translate(0px, ${currentTop}px)`);
            
            currentTop += height;
            
            return result;
        });
    }
    
    collectRow(row) {
        const subGrids = this.exportMeta.subGrids;
        
        Object.entries(row.elements).forEach(([key, value]) => {
            subGrids[key].rows.push([value.outerHTML, row.offsetHeight]);
        });
    }
    
    buildPageHtml() {
        const
            me           = this,
            { subGrids } = me.exportMeta;
        
        // Now when rows are collected, we need to add them to exported grid
        let html = me.prepareExportElement();
    
        Object.values(subGrids).forEach(({ placeHolder, rows }) => {
            const placeHolderText = placeHolder.outerHTML;
            html = html.replace(placeHolderText, me.positionRows(rows).join(''));
        });
        
        return html;
    }
}

// HACK: terser/obfuscator doesn't yet support async generators, when processing code it converts async generator to regular async
// function.
SinglePageExporter.prototype.pagesExtractor = async function * pagesExtractor(config) {
    // When we prepared grid we stretched it horizontally, now we need to gather all rows
    // There are two ways:
    // 1. set component height to scrollable.scrollHeight value to render all rows at once (maybe a bit more complex
    // if rows have variable height)
    // 2. iterate over rows, scrolling new portion into view once in a while
    // #1 sounds simpler, but that might require too much rendering, let's scroll rows instead
    
    const
        me          = this,
        { client }  = config,
        {
            rowManager,
            store
        }           = client,
        styles      = me.stylesheets,
        portrait    = config.orientation === Orientation.portrait,
        paperFormat = PaperFormat[config.paperFormat],
        paperWidth  = portrait ? paperFormat.width : paperFormat.height,
        paperHeight = portrait ? paperFormat.height : paperFormat.width,
        totalRows   = config.rowsRange === RowsRange.visible && store.count
            ? rowManager.visibleRowCount
            : store.count;
    
    let {
            totalHeight,
            totalWidth
        }                 = me.exportMeta,
        processedRows     = 0,
        lastDataIndex     = -1,
        header, footer;
    
    if (rowManager.rows.length > 0) {
        if (config.rowsRange === RowsRange.visible) {
            lastDataIndex = rowManager.rows.find(row => row.bottom > client.scrollable.y).dataIndex - 1;
        }
    
        // Collecting rows
        while (processedRows < totalRows) {
            const
                rows    = rowManager.rows,
                lastRow = rows[rows.length - 1],
                lastProcessedRowIndex = processedRows;
            
            rows.forEach(row => {
                // When we are scrolling rows will be duplicated even with disabled buffers (e.g. when we are trying to
                // scroll last record into view). So we store last processed row dataIndex (which is always growing
                // sequence) and filter all rows with lower/same dataIndex
                if (row.dataIndex > lastDataIndex && processedRows < totalRows) {
                    ++processedRows;
                    totalHeight += row.offsetHeight;
                    me.collectRow(row);
                }
            });
            
            // Calculate new rows processed in this iteration e.g. to collect events
            const
                firstNewRowIndex = rows.findIndex(r => r.dataIndex === lastDataIndex + 1),
                lastNewRowIndex  = firstNewRowIndex + (processedRows - lastProcessedRowIndex);
            
            await me.onRowsCollected(rows.slice(firstNewRowIndex, lastNewRowIndex), config);
            
            if (processedRows < totalRows) {
                lastDataIndex = lastRow.dataIndex;
                await me.scrollRowIntoView(client, lastDataIndex + 1);
            }
        }
    }
    
    const html = me.buildPageHtml();
    
    // Calculate header height
    totalHeight += client.height - client.bodyHeight;
    
    let totalClientHeight = totalHeight;
    
    // Measure header and footer height
    if (config.headerTpl) {
        header = me.prepareHTML(config.headerTpl({ totalWidth }));
        const height = me.measureElement(header);
        totalHeight += height;
    }
    
    if (config.footerTpl) {
        footer = me.prepareHTML(config.footerTpl({ totalWidth }));
        const height = me.measureElement(footer);
        totalHeight += height;
    }
    
    const
        widthScale  = Math.min(1, me.getScaleValue(me.inchToPx(paperWidth), totalWidth)),
        heightScale = Math.min(1, me.getScaleValue(me.inchToPx(paperHeight), totalHeight));
    
    // Now add style to stretch grid vertically
    styles.push(
        `<style>
                #${client.id} {
                    height: ${totalClientHeight}px !important;
                    width: ${totalWidth}px !important;
                }
                
                .b-export-content {
                    ${me.centerContentHorizontally ? 'left: 50%;' : ''}
                    transform: scale(${Math.min(widthScale, heightScale)}) ${me.centerContentHorizontally ? 'translateX(-50%)' : ''};
                    transform-origin: top left;
                }
            </style>`
    );
    
    // This is a single page exporter so we only yield one page
    yield {
        html : me.pageTpl({
            html,
            header,
            footer,
            styles,
            paperWidth,
            paperHeight
        })
    };
};
