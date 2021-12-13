import Widget from '../../Core/widget/Widget.js';
import DomSync from '../../Core/helper/DomSync.js';

/**
 * @module Scheduler/view/TimeAxisBase
 */

function isLastLevel(level, levels) {
    return level === levels.length - 1;
}

function isLastCell(level, cell) {
    return cell === level.cells[level.cells.length - 1];
}

/**
 * Base class for HorizontalTimeAxis and VerticalTimeAxis. Contains shared functionality to only render ticks in view,
 * should not be used directly.
 *
 * @extends Core/widget/Widget
 * @private
 * @abstract
 */
export default class TimeAxisBase extends Widget {

    //region Config

    static get defaultConfig() {
        return {
            /**
             * The minimum width for a bottom row header cell to be considered 'compact', which adds a special CSS class
             * to the row (for special styling). Copied from Scheduler/Gantt.
             * @config {Number}
             * @default
             */
            compactCellWidthThreshold : 15,

            // TimeAxisViewModel
            model : null,

            cls : null,

            /**
             * Style property to use as cell size. Either width or height depending on orientation
             * @config {String}
             * @private
             */
            sizeProperty : null,

            /**
             * Style property to use as cells position. Either left or top depending on orientation
             * @config {String}
             * @private
             */
            positionProperty : null,

            /**
             * Specify `true` to wrap header text in `.b-sch-header-text`
             * @config {String}
             * @private
             */
            wrapText : null
        };
    }

    static get properties() {
        return {
            startDate : null,
            endDate   : null,
            levels    : [],
            size      : null
        };
    }

    // Set visible date range
    set range(range) {
        this.startDate = range.startDate;
        this.endDate = range.endDate;

        this.refresh();

        this.trigger('rangeChange', { startDate : range.startDate, endDate : range.endDate });
    }

    //endregion

    //region Html & rendering

    // Generates element configs for all levels defined by the current ViewPreset
    buildCells() {
        const
            me                   = this,
            { sizeProperty }     = me,
            featureHeaderConfigs = [],
            cellConfigs          = me.levels.map((level, i) => (
                {
                    className : {
                        'b-sch-header-row'                     : 1,
                        [`b-sch-header-row-${level.position}`] : 1,
                        'b-sch-header-row-main'                : i === me.model.viewPreset.mainHeaderLevel,
                        'b-lowest'                             : isLastLevel(i, me.levels)
                    },
                    syncOptions : {
                        // Keep a maximum of 5 released cells. Might be fine with fewer since ticks are fixed width.
                        // Prevents an unnecessary amount of cells from sticking around when switching from narrow to
                        // wide tickSizes
                        releaseThreshold : 5,
                        syncIdField      : 'tickIndex'
                    },
                    dataset : {
                        headerFeature  : `headerRow${i}`,
                        headerPosition : level.position
                    },
                    // Only include cells in view
                    children : level.cells.filter(cell => cell.start < me.endDate && cell.end > me.startDate).map(cell => ({
                        className : {
                            'b-sch-header-timeaxis-cell' : 1,
                            [cell.headerCellCls]         : cell.headerCellCls,
                            [`b-align-${cell.align}`]    : cell.align,
                            'b-last'                     : isLastCell(level, cell)
                        },
                        dataset : {
                            tickIndex : cell.index,
                            // Used in export tests to resolve dates from tick elements
                            ...window.DEBUG && { date : cell.start.getTime() }
                        },
                        style : {
                            // DomHelper appends px to numeric dimensions
                            [me.positionProperty]   : cell.coord,
                            [sizeProperty]          : cell.width,
                            [`min-${sizeProperty}`] : cell.width
                        },
                        children : me.wrapText ? [
                            {
                                className : 'b-sch-header-text',
                                html      : cell.value
                            }
                        ] : null,
                        html : me.wrapText ? null : cell.value
                    }))
                }
            ));

        // When tested in isolation there is no client
        me.client && me.client.getHeaderDomConfigs(featureHeaderConfigs);

        cellConfigs.push(...featureHeaderConfigs);

        // noinspection JSSuspiciousNameCombination
        return {
            className   : `b-widget ${me.cls}`,
            syncOptions : {
                // Do not keep entire levels no longer used, for example after switching view preset
                releaseThreshold : 0
            },
            style : {
                [sizeProperty] : me.size
            },
            children : cellConfigs
        };
    }

    render(targetElement) {
        super.render(targetElement);

        this.refresh(true);
    }

    /**
     * Refresh the UI
     * @param {Boolean} [rebuild] Specify `true` to force a rebuild of the underlying header level definitions
     */
    refresh(rebuild = !this.levels.length) {
        const
            me               = this,
            { columnConfig } = me.model,
            { levels }       = me,
            oldLevelsCount   = levels.length;

        if (rebuild) {
            levels.length = 0;

            columnConfig.forEach((cells, position) => levels[position] = {
                position,
                cells
            });

            me.size = levels[0].cells.reduce((sum, cell) => sum += cell.width, 0);

            // TODO clean up when this is fixed: https://app.assembla.com/spaces/bryntum/tickets/8413-horizontaltimeaxis-should-not-completely-overwrite-contents-of-column-el/details#
            const parentEl = me.element.parentElement;

            if (parentEl) {
                parentEl.classList.remove(`b-sch-timeaxiscolumn-levels-${oldLevelsCount}`);
                parentEl.classList.add(`b-sch-timeaxiscolumn-levels-${levels.length}`);
            }
        }

        if (!me.startDate || !me.endDate) {
            return;
        }

        // Boil down levels to only show what is in view
        DomSync.sync({
            domConfig     : me.buildCells(),
            targetElement : me.element,
            syncIdField   : 'headerFeature'
        });
        
        me.trigger('refresh');
    }

    //endregion
}
