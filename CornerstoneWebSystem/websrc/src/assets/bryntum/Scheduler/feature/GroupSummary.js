import GridGroupSummary from '../../Grid/feature/GroupSummary.js';
import DomHelper from '../../Core/helper/DomHelper.js';
import GridFeatureManager from '../../Grid/feature/GridFeatureManager.js';
import Tooltip from '../../Core/widget/Tooltip.js';
import Scheduler from '../../Scheduler/view/Scheduler.js';

// Actions that trigger rerendering of group summary rows
const refreshActions = {
    add       : 1,
    remove    : 1,
    update    : 1,
    removeAll : 1,
    filter    : 1
};

/**
 * @module Scheduler/feature/GroupSummary
 */

// noinspection JSClosureCompilerSyntax
/**
 * A special version of the Grid GroupSummary feature that enables summaries within scheduler. To use a single summary
 * it is easiest to configure {@link #config-renderer}, for multiple summaries see {@link #config-summaries}.
 *
 * This feature is <strong>disabled</strong> by default. It is **not** supported in vertical mode.
 *
 * @extends Grid/feature/GroupSummary test
 *
 * @classtype groupsummary
 * @externalexample scheduler/GroupSummary.js
 * @demo Scheduler/groupsummary
 * @typings Grid/feature/GroupSummary -> Grid/feature/GridGroupSummary
 */
export default class GroupSummary extends GridGroupSummary {
    //region Config

    static get $name() {
        return 'GroupSummary';
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
             * Easier way to configure when using a single summary. Accepts a renderer function with the format specified
             * in {@link #config-summaries}
             * @config {Function}
             */
            renderer : null
        };
    }

    static get pluginConfig() {
        return {
            chain : ['render']
        };
    }

    //endregion

    //region Init

    construct(scheduler, config) {
        const me = this;

        if (scheduler.isVertical) {
            throw new Error('GroupSummary feature is not supported in vertical mode');
        }

        me.scheduler = scheduler;

        super.construct(scheduler, config);

        if (!me.summaries && me.renderer) {
            me.summaries = [{ renderer : me.renderer }];
        }

        me.isScheduler = scheduler instanceof Scheduler;

        if (me.isScheduler) {
            scheduler.eventStore.on({
                change  : me.onEventStoreChange,
                thisObj : me
            });
            scheduler.timeAxis.on({
                reconfigure : me.onTimeAxisChange,
                thisObj     : me
            });
            scheduler.timeAxisViewModel.on({
                update  : me.onTimeAxisChange,
                thisObj : me
            });
        }

        //<debug>
        if (!me.summaries) {
            throw new Error('Summaries required');
        }
        //</debug>
    }

    doDestroy() {
        if (this._tip) {
            this._tip.destroy();
        }

        super.doDestroy();
    }

    //endregion

    //region Events

    onTimeAxisChange() {
        this.rerenderGroupSummaries();
    }

    onEventStoreChange({ action }) {
        // Scheduler does minimal update on event changes, it will not rerender the summary rows.
        // Need to handle that here
        if (refreshActions[action]) {
            this.rerenderGroupSummaries();
        }
    }

    rerenderGroupSummaries() {
        // TODO: Sort out the affected rows by checking events resources
        this.scheduler.rowManager.rows.forEach(row => {
            if (row.isGroupFooter) {
                row.render();
            }
        });
    }

    //endregion

    //region Render

    /**
     * Called before rendering row contents, used to reset rows no longer used as group summary rows
     * @private
     */
    onBeforeRenderRow({ row, record }) {
        if (row.isGroupFooter && !record.meta.hasOwnProperty('groupFooterFor')) {
            const timeaxisCell = row.elements.normal.querySelector('.b-sch-timeaxis-cell');

            // remove summary cells if exist
            if (timeaxisCell) {
                timeaxisCell.innerHTML = '';
            }
        }

        super.onBeforeRenderRow(...arguments);
    }

    /**
     * Called by parent class to fill timeaxis with summary contents. Generates tick "cells" and populates them with
     * summaries.
     * ```
     * <div class="b-timeaxis-group-summary">
     *     <div class="b-timeaxis-tick">
     *         <div class="b-timeaxix-summary-value">x</div>
     *         ...
     *     </div>
     *     ...
     * </div>
     * ```
     * @private
     */
    generateHtml(column, records, cls) {
        if (column.type === 'timeAxis') {
            const
                me        = this,
                scheduler = me.scheduler,
                tickSize  = scheduler.tickSize;

            let html = '';

            scheduler.timeAxis.forEach(tick => {
                const
                    // events for current tick
                    events      = scheduler.eventStore.getEventsInTimeSpan(tick.startDate, tick.endDate, true, true),
                    // filter those events to current groups
                    groupEvents = events.filter(event => event.resources.some(resource => records.includes(resource)));

                // TODO: could turn this into a template

                const sumHtml = me.summaries.map(config => {
                    // summary renderer used to calculate and format value
                    const value = config.renderer({
                        startDate     : tick.startDate,
                        endDate       : tick.endDate,
                        eventStore    : scheduler.eventStore,
                        resourceStore : scheduler.resourceStore,
                        events        : groupEvents
                    });

                    return `<div class="b-timeaxis-summary-value">${value}</div>`;
                }).join('');

                html += `<div class="b-timeaxis-tick" style="width: ${tickSize}px">${sumHtml}</div>`;
            });

            return `<div class="b-timeaxis-group-summary">${html}</div>`;
        }

        return super.generateHtml(column, records, cls);
    }

    /**
     * Overrides parents function to return correct summary count, used when sizing row
     * @private
     */
    updateSummaryHtml(cellElement, column, records) {
        const count = super.updateSummaryHtml(cellElement, column, records);

        if (column.type === 'timeAxis') {
            let result = {
                count  : 0,
                height : 0
            };

            this.summaries.forEach(config => {
                if (config.height) {
                    result.height += config.height;
                }
                else {
                    result.count++;
                }
            });

            return result;
        }

        return count;
    }

    /**
     * Generates tooltip contents for hovered summary tick
     * @private
     */
    getTipHtml({ forElement }) {
        const me            = this,
            index         = Array.from(forElement.parentElement.children).indexOf(forElement),
            tick          = me.scheduler.timeAxis.getAt(index);

        let tipHtml = `<header>${me.L('Summary for')} ${me.scheduler.getFormattedDate(tick.startDate)}</header>`,
            showTip = false;

        DomHelper.forEachSelector(forElement, '.b-timeaxis-summary-value', (element, i) => {
            const label = me._labels[i],
                text  = element.innerText.trim();

            tipHtml += `<label>${label || ''}</label><div class="b-timeaxis-summary-value">${text}</div>`;

            if (element.innerHTML) showTip = true;
        });

        return showTip ? tipHtml : null;
    }

    /**
     * Initialize tooltip on render
     * @private
     */
    render() {
        const
            me = this,
            { scheduler } = me;

        if (me.isScheduler) {
            // if any sum config has a label, init tooltip
            if (me.summaries && me.summaries.some(config => config.label) && me.showTooltip && !me._tip) {
                me._labels = me.summaries.map(config => config.label || '');

                me._tip = new Tooltip({
                    id             : `${scheduler.id}-groupsummary-tip`,
                    cls            : 'b-timeaxis-summary-tip',
                    hoverDelay     : 0,
                    hideDelay      : 0,
                    forElement     : scheduler.timeAxisSubGridElement,
                    anchorToTarget : true,
                    forSelector    : '.b-timeaxis-group-summary .b-timeaxis-tick',
                    clippedBy      : [scheduler.timeAxisSubGridElement, scheduler.bodyContainer],
                    getHtml        : me.getTipHtml.bind(me)
                });
            }
        }
    }

    //endregion
}

// Needed for module bundle, which turns class name into GroupSummary$1 and registers it wrong
GroupSummary._$name = 'GroupSummary';

// Override Grids GroupSummary with this improved version
GridFeatureManager.registerFeature(GroupSummary, false, 'Scheduler');
