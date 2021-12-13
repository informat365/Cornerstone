import DomHelper from '../../Core/helper/DomHelper.js';
import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import Footer from '../view/Footer.js';
import GridFeatureManager from '../feature/GridFeatureManager.js';
import SummaryFormatter from './mixin/SummaryFormatter.js';

/**
 * @module Grid/feature/Summary
 */

/**
 * Displays a summary row in the grid footer. Specify type of summary on columns, available types are:
 * <dl class="wide">
 * <dt>sum <dd>Sum of all values in the column
 * <dt>add <dd>Alias for sum
 * <dt>count <dd>Number of rows
 * <dt>countNotEmpty <dd>Number of rows containing a value
 * <dt>average <dd>Average of all values in the column
 * <dt>function <dd>A custom function, used with store.reduce. Should take arguments (sum, record)
 * </dl>
 * Columns can also specify a summaryRenderer to format the calculated sum.
 *
 * This feature is <strong>disabled</strong> by default.
 *
 * ```
 * { text : 'Score', data : 'score', width : 80, sum : true }
 * { text : 'Rank', data : 'rank', width : 80, sum : 'average', summaryRenderer: sum => return 'Average rank ' + sum }
 * ```
 *
 * Also it is possible to set up multiple summaries as array of summary configs:
 * ```
 * { text : 'Rank', data : 'rank', summaries : [{ sum : 'average', label : 'Average' }, { sum : 'count', label : 'Count' }] }
 * ```
 *
 * @extends Core/mixin/InstancePlugin
 *
 * @demo Grid/features
 * @classtype summary
 * @externalexample feature/Summary.js
 */
export default class Summary extends SummaryFormatter(InstancePlugin) {
    //region Init

    static get $name() {
        return 'Summary';
    }

    construct(grid, config) {
        const me = this;

        Object.assign(me, {
            grid  : grid,
            store : grid.store
        });

        super.construct(grid, config);

        me.store.on({
            change  : me.onStoreChanged,
            thisObj : me
        });

        // footers aren't added by default, add
        me.grid.eachSubGrid(subGrid => {
            if (!subGrid.footer) {
                subGrid.footer = new Footer({
                    subGrid,
                    parent : subGrid  // Contained widgets need to know their parents
                });
            }
        });
    }

    doDestroy() {
        super.doDestroy();
    }

    doDisable(disable) {
        super.doDisable(disable);

        if (disable) {
            this.grid.element.classList.add('b-summary-disabled');
        }
        else {
            this.updateSummaries();
            this.grid.element.classList.remove('b-summary-disabled');
        }
    }

    //endregion

    //region Plugin config

    // Plugin configuration. This plugin chains some of the functions in Grid.
    static get pluginConfig() {
        return {
            chain : ['renderRows']
        };
    }

    //endregion

    //region Render

    renderRows() {
        this.updateSummaries();
    }

    /**
     * Updates summaries. Summaries are displayed as tables in footer (styling left out to keep brief):
     * ```
     * <table>
     *     <tr><td colspan="2">0</td></tr> // { sum : 'min' } Only a calculation, span entire table
     *     <tr><td>Max</td><td>10</td></tr> // { sum : 'max', label: 'Max' } Label + calculation
     *     <tr><td>Max</td><td>10</td></tr> // { sum : 'sum', label: 'Max' } Label + calculation
     * </table>
     * ```
     * @private
     */
    updateSummaries() {
        const
            me              = this,
            { grid, store } = me,
            cells           = DomHelper.children(grid.element, '.b-grid-footer'),
            records         = store.storage.values.filter(r => !r.meta.specialRow);

        // reset seeds, to not have ever increasing sums :)
        grid.columns.forEach(column => {
            column.summaries && column.summaries.forEach(config => {
                if ('seed' in config) {
                    if (!('initialSeed' in config)) {
                        config.initialSeed = config.seed;
                    }

                    if (['number', 'string', 'date'].includes(typeof config.initialSeed)) {
                        config.seed = config.initialSeed;
                    }
                    else {
                        // create shallow copy
                        config.seed = Object.assign({}, config.initialSeed);
                    }
                }
            });
        });

        cells.forEach(cellElement => {
            const
                column = grid.columns.get(cellElement.dataset.column),
                html   = me.generateHtml(column, records, 'b-grid-footer-summary');

            if (column.summaries ? column.summaries.length : column.sum ? 1 : 0) {
                // First time, set table
                if (!cellElement.children.length) {
                    cellElement.innerHTML = html;
                }
                // Following times, sync changes
                else {
                    DomHelper.sync(html, cellElement.firstElementChild);
                }
            }
        });
    }

    //endregion

    //region Events

    /**
     * Updates summaries on store changes (except record update, handled below)
     * @private
     */
    onStoreChanged({ action, changes }) {
        let shouldUpdate = true;

        if (this.disabled) {
            return;
        }

        if (action === 'update') {
            // only update summary when a field that affects summary is changed
            // TODO: this should maybe be removed, another column might depend on the value for its summary?
            shouldUpdate = Object.keys(changes).some(field => {
                const colField = this.grid.columns.get(field);
                // check existence, since a field not used in a column might have changed
                return Boolean(colField) && (Boolean(colField.sum) || Boolean(colField.summaries));
            });
        }

        if (shouldUpdate) {
            this.updateSummaries();
        }
    }

    //endregion
}

Summary.featureClass = 'b-summary';

GridFeatureManager.registerFeature(Summary);
