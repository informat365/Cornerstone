import Base from '../../../Core/Base.js';

/**
 * @module Grid/feature/mixin/SummaryFormatter
 */

/**
 * Mixin for Summary and GroupSummary that handles formatting sums.
 * @mixin
 * @private
 */
export default Target => class SummaryFormatter extends (Target || Base) {
    /**
     * Calculates sums and returns as a html table
     * @param {Grid.column.Column} column Column to calculate sum for
     * @param {Core.data.Model[]} records Records to include in calculation
     * @param {String} cls CSS class to apply to summary table
     * @returns {string}
     */
    generateHtml(column, records, cls) {
        const store = this.store,
            summaries = column.summaries ||
                  (column.sum ? [{ sum : column.sum, renderer : column.summaryRenderer }] : []);

        let html = `<table class="${cls}">`;

        summaries.forEach(config => {
            let type = config.sum,
                sum  = null;

            if (type === true) type = 'sum';

            switch (type) {
                case 'sum':
                case 'add':
                    sum = store.sum(column.field, records);
                    break;
                case 'max':
                    sum = store.max(column.field, records);
                    break;
                case 'min':
                    sum = store.min(column.field, records);
                    break;
                case 'average':
                case 'avg':
                    sum = store.average(column.field, records);
                    break;
                case 'count':
                    sum = records.length;
                    break;
                case 'countNotEmpty':
                    sum = records.reduce((sum, record) => {
                        const value = record[column.field];
                        return sum + (value !== null && value !== undefined ? 1 : 0);
                    }, 0);
                    break;
            }

            if (typeof type === 'function') {
                sum = records.reduce(type, 'seed' in config ? config.seed : 0);
            }

            if (sum !== null) {
                const valueCls = 'b-grid-summary-value',
                    // value to display, either using renderer or as is
                    valueHtml = config.renderer
                        ? config.renderer({ config, sum })
                        : sum,
                    // optional label
                    labelHtml = config.label
                        ? `<td class="b-grid-summary-label">${config.label}</td>`
                        : '';

                let summaryHtml;

                // no <td>s in html, wrap it (always the case when not using renderer)
                if (!String(valueHtml).includes('<td>')) {
                    summaryHtml = labelHtml
                        // has label, use returned html as value cell
                        ? `${labelHtml}<td class="${valueCls}">${valueHtml}</td>`
                        // no label, span entire table
                        : `<td colspan="2" class="${valueCls}">${valueHtml}</td>`;
                }
                // user is in charge of giving correct formatting
                else {
                    summaryHtml = valueHtml;
                }

                html += `<tr>${summaryHtml}</tr>`;
            }
        });

        return html + '</table>';
    }
};
