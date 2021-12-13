import GridSummary from '../../Grid/feature/Summary.js';
import DomHelper from '../../Core/helper/DomHelper.js';
import GridFeatureManager from '../../Grid/feature/GridFeatureManager.js';
import Tooltip from '../../Core/widget/Tooltip.js';
import Scheduler from '../view/Scheduler.js';

/**
 * @module Scheduler/feature/Summary
 */

// noinspection JSClosureCompilerSyntax
/**
 * A special version of the Grid Summary feature. This feature displays a summary row in the grid footer.
 * For regular columns in the locked section - specify type of summary on columns, available types are:
 * <dl class="wide">
 * <dt>sum <dd>Sum of all values in the column
 * <dt>add <dd>Alias for sum
 * <dt>count <dd>Number of rows
 * <dt>countNotEmpty <dd>Number of rows containing a value
 * <dt>average <dd>Average of all values in the column
 * <dt>function <dd>A custom function, used with store.reduce. Should take arguments (sum, record)
 * </dl>
 * Columns can also specify a summaryRender to format the calculated sum.
 *
 * To summarize events, either provide a {@link #config-renderer} or use {@link #config-summaries}
 *
 * This feature is <strong>disabled</strong> by default. It is **not** supported in vertical mode.
 *
 * @extends Grid/feature/Summary
 *
 * @classtype summary
 * @externalexample scheduler/Summary.js
 * @demo Scheduler/summary
 * @typings Grid/feature/Summary -> Grid/feature/GridSummary
 */
export default class Summary extends GridSummary {
    //region Config

    static get $name() {
        return 'Summary';
    }

    static get defaultConfig() {
        return {
            /**
             * Show tooltip containing summary values and labels
             * @config {Boolean}
             * @default
             */
            showTooltip : true,

            /**
             * Array of summary configs, with format
             * `[{ label: 'Label', renderer : ({startDate, endDate, eventStore, resourceStore, events, element}) }]`.
             * @config {Object[]}
             */
            summaries : null,

            /**
             * Easier way to configure When using single summary, accepts a renderer function with the format specified
             * in {@link #config-summaries}
             * @config {Function}
             */
            renderer : null
        };
    }

    //endregion

    //region Init

    construct(scheduler, config) {
        const me = this;

        if (scheduler.isVertical) {
            throw new Error('Summary feature is not supported in vertical mode');
        }

        me.scheduler = scheduler;

        super.construct(scheduler, config);

        if (!me.summaries) me.summaries = [{ renderer : me.renderer }];

        // Feature might be run from Grid (in docs), should not crash
        // https://app.assembla.com/spaces/bryntum/tickets/6801/details
        if (scheduler instanceof Scheduler) {
            scheduler.eventStore.on({
                change  : me.updateScheduleSummaries,
                thisObj : me
            });

            scheduler.resourceStore.on({
                change  : me.updateScheduleSummaries,
                thisObj : me
            });

            scheduler.timeAxisViewModel.on({
                update  : me.renderRows,
                thisObj : me
            });
        }
    }

    //endregion

    //region Render

    renderRows() {
        super.renderRows();
        this.render();
    }

    /**
     * Updates summaries.
     * @private
     */
    updateScheduleSummaries() {
        const me               = this,
            scheduler        = me.scheduler,
            summaryContainer = scheduler.element.querySelector('.b-grid-footer.b-sch-timeaxiscolumn');

        if (summaryContainer) {
            DomHelper.forEachSelector(summaryContainer, '.b-timeaxis-tick', (element, i) => {
                const tick      = scheduler.timeAxis.getAt(i),
                    events    = scheduler.eventStore.getEventsInTimeSpan(tick.startDate, tick.endDate, true, true);

                let html = '',
                    tipHtml = `<header>${me.L('Summary for')} ${scheduler.getFormattedDate(tick.startDate)}</header>`;

                me.summaries.forEach(config => {
                    const value = config.renderer({
                            startDate     : tick.startDate,
                            endDate       : tick.endDate,
                            eventStore    : scheduler.eventStore,
                            resourceStore : scheduler.resourceStore,
                            events,
                            element
                        }),
                        valueHtml = `<div class="b-timeaxis-summary-value">${value}</div>`;

                    if (me.summaries.length > 1 || value !== '') html += valueHtml;
                    tipHtml += `<label>${config.label || ''}</label>` + valueHtml;
                });

                element.innerHTML = html;
                element._tipHtml = tipHtml;
            });
        }
    }

    render() {
        const me        = this,
            scheduler = me.scheduler,
            summaryEl = scheduler.element.querySelector('.b-grid-footer.b-sch-timeaxiscolumn');

        if (summaryEl) {
            // if any sum config has a label, init tooltip
            if (me.summaries.some(config => config.label) && me.showTooltip && !me._tip) {
                me._tip = new Tooltip({
                    id             : `${scheduler.id}-summary-tip`,
                    cls            : 'b-timeaxis-summary-tip',
                    hoverDelay     : 0,
                    hideDelay      : 100,
                    forElement     : summaryEl,
                    anchorToTarget : true,
                    trackMouse     : false,
                    forSelector    : '.b-timeaxis-tick',
                    getHtml        : ({ forElement }) => forElement._tipHtml
                });
            }

            summaryEl.innerHTML =
                `<div class="b-timeaxis-tick" style="width: ${scheduler.tickSize}px"></div>`
                    .repeat(scheduler.timeAxis.count);

            me.updateScheduleSummaries();
        }
    }

    //endregion
}

// Needed for module bundle, which turns class name into Summary$1 and registers it wrong
Summary._$name = 'Summary';

// Override Grids Summary with this improved version
GridFeatureManager.registerFeature(Summary, false, 'Scheduler');
