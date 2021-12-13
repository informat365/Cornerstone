import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import GridFeatureManager from '../feature/GridFeatureManager.js';
import SummaryFormatter from './mixin/SummaryFormatter.js';
import DomHelper from '../../Core/helper/DomHelper.js';

/**
 * @module Grid/feature/GroupSummary
 */

/**
 * Displays a summary row as a group footer in a grouped grid. Uses same configuration options on columns as
 * {@link Grid.feature.Summary}.
 *
 * This feature is <strong>disabled</strong> by default.
 *
 * ```
 * features : {
 *     group        : 'city',
 *     groupSummary : true
 * }
 * ```
 *
 * @extends Core/mixin/InstancePlugin
 *
 * @demo Grid/groupsummary
 * @classtype groupsummary
 * @externalexample feature/GroupSummary.js
 */
export default class GroupSummary extends SummaryFormatter(InstancePlugin) {
    //region Init

    static get $name() {
        return 'GroupSummary';
    }

    construct(grid, config) {
        const me = this;

        Object.assign(me, {
            grid  : grid,
            store : grid.store
        });

        super.construct(grid, config);

        if (!grid.features.group) {
            throw new Error('Requires Group feature to work, please enable');
        }

        me.store.on({
            update  : me.onStoreUpdate,
            // need to run before grids listener, to flag for full refresh
            prio    : 1,
            thisObj : me
        });

        me.grid.rowManager.on({
            beforerenderrow : me.onBeforeRenderRow,
            rendercell      : me.renderCell,
            thisObj         : me
        });
    }

    doDisable(disable) {
        // Flag that will make the Store insert rows for group footers
        this.store.useGroupFooters = !disable;

        // Refresh groups to show/hide footers
        if (!this.isConfiguring) {
            this.store.group();
        }

        super.doDisable(disable);
    }

    //endregion

    //region Render

    /**
     * Called before rendering row contents, used to reset rows no longer used as group summary rows
     * @private
     */
    onBeforeRenderRow({ row, record }) {
        if (row.isGroupFooter && !record.meta.hasOwnProperty('groupFooterFor')) {
            // not a group row, remove css
            row.isGroupFooter = false;
            row.removeCls('b-group-footer');
            // force full "redraw" when rendering cells
            row.forceInnerHTML = true;
        }
    }

    /**
     * Called when a cell is rendered, styles the group rows first cell.
     * @private
     */
    renderCell({ column, cellElement, rowElement, row, record, size }) {
        const me = this;

        // no need to do the code below if not grouping
        if (!me.store.isGrouped) return;

        if (record.meta.hasOwnProperty('groupFooterFor')) {
            // this is a group row, add css
            rowElement.classList.add('b-group-footer');
            row.isGroupFooter = true;

            // returns height config or count. config format is { height, count }. where `height is in px and should be
            // added to value calculated from `count
            const heightSetting = me.updateSummaryHtml(cellElement, column, record.meta.groupRecord.groupChildren);

            const count = typeof heightSetting === 'number' ? heightSetting : heightSetting.count;

            // number of summaries returned, use to calculate cell height
            if (count > 1) {
                size.height = me.grid.rowHeight + count * me.grid.rowHeight * 0.1;
            }

            // height config with height specified, added to cell height
            if (heightSetting.height) {
                size.height += heightSetting.height;
            }
        }
    }

    updateSummaryHtml(cellElement, column, records) {
        records = records.slice();
        records.pop(); // last record is group footer, remove

        const html = this.generateHtml(column, records, 'b-grid-group-summary');

        // First time, set table
        if (!cellElement.children.length) {
            cellElement.innerHTML = html;
        }
        // Following times, sync changes
        else {
            DomHelper.sync(html, cellElement.firstElementChild);
        }

        // return summary "count", used to set row height
        return column.summaries ? column.summaries.length : column.sum ? 1 : 0;
    }

    //endregion

    //region Events

    /**
     * Updates summaries on store changes (except record update, handled below)
     * @private
     */
    onStoreUpdate({ source : store, changes }) {
        if (!this.disabled) {
            // If a grouping field is among the changes, StoreGroup#onDataChanged will
            // take care of the update by re-sorting.
            if (changes && store.groupers.find(grouper => grouper.field in changes)) {
                return;
            }
            // only update summary when a field that affects summary is changed
            // TODO: this should maybe be removed, another column might depend on the value for its summary?
            let shouldUpdate = Object.keys(changes).some(field => {
                const colField = this.grid.columns.get(field);
                // check existence, since a field not used in a column might have changed
                return Boolean(colField) && (Boolean(colField.sum) || Boolean(colField.summaries));
            });

            if (shouldUpdate) {
                this.grid.forceFullRefresh = true;
            }
        }
    }

    //endregion
}

GroupSummary.featureClass = 'b-group-summary';

GridFeatureManager.registerFeature(GroupSummary);
