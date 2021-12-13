import DateHelper from '../../Core/helper/DateHelper.js';
import DomHelper from '../../Core/helper/DomHelper.js';
import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import GridFeatureManager from '../../Grid/feature/GridFeatureManager.js';

/**
 * @module Scheduler/feature/ColumnLines
 */

/**
 * Displays column lines for ticks, with a different styling for major ticks (by default they are darker). If this
 * feature is disabled, no lines are shown. If it's enabled, line are shown for the tick level which is set in current
 * ViewPreset. Please see {@link Scheduler.preset.ViewPreset#field-columnLinesFor} config for details.
 *
 * The lines are drawn to a canvas, converted to an image and used as background in the schedulers background canvas.
 * In certain scenarios, major column lines are drawn as divs.
 *
 * The color and style of the lines are determined by extracting the values for `border-left-color` and
 * `border-left-style` (solid, dashed or dotted are supported) from the css rules for `.b-column-line` and
 * `.b-column-line-major`.
 *
 * This feature is **enabled** by default
 *
 * @extends Core/mixin/InstancePlugin
 * @demo Scheduler/basic
 * @externalexample scheduler/ColumnLines.js
 */
export default class ColumnLines extends InstancePlugin {
    //region Config

    static get $name() {
        return 'ColumnLines';
    }

    static get properties() {
        return {
            tickColor      : '#e6e6e6',
            majorTickColor : '#999',
            tickStyle      : 'solid',
            majorTickStyle : 'solid'
        };
    }

    // Plugin configuration. This plugin chains some of the functions in Grid.
    static get pluginConfig() {
        return {
            after : ['render', 'updateCanvasSize']
        };
    }

    //endregion

    //region Init & destroy

    construct(client, config) {
        const me = this;

        client.useBackgroundCanvas = true;

        super.construct(client, config);

        me.timeAxisViewModel = client.timeAxisViewModel;

        client.on({
            theme   : 'onThemeChange',
            thisObj : me
        });
    }

    doDestroy() {
        this.timeAxisViewModelDetatcher && this.timeAxisViewModelDetatcher();
        super.doDestroy();
    }

    doDisable(disable) {
        if (!this.isConfiguring) {
            if (disable) {
                DomHelper.removeEachSelector(this.client.backgroundCanvas, '.b-column-line-major');
                this.client.backgroundCanvas.style.backgroundImage = 'none';
            }
            else {
                this.drawLines();
            }
        }

        super.doDisable(disable);
    }

    //endregion

    //region Draw

    /**
     * Reads lines colors from temporary element
     * @private
     */
    getColorsFromCSS() {
        const me = this;

        // Create two fake column lines. But only do it once per app.
        if (!me.GotColors) {
            const element      = DomHelper.createElement({
                    style  : 'position: absolute; visibility: hidden',
                    html   : '<div class="b-column-line"></div><div class="b-column-line-major"></div>',
                    parent : document.body
                }),
                tickColor      = DomHelper.getStyleValue(element.firstElementChild, 'border-left-color'),
                majorColor     = DomHelper.getStyleValue(element.lastElementChild, 'border-left-color'),
                tickStyle      = DomHelper.getStyleValue(element.firstElementChild, 'border-left-style'),
                majorTickStyle = DomHelper.getStyleValue(element.lastElementChild, 'border-left-style');

            if (tickColor) {
                me.tickColor = tickColor;
            }

            if (majorColor) {
                me.majorTickColor = majorColor;
            }

            if (tickStyle) {
                me.tickStyle = tickStyle;
            }

            if (majorTickStyle) {
                me.majorTickStyle = majorTickStyle;
            }

            element.remove();
            me.GotColors = true;
        }
    }

    onThemeChange() {
        this.GotColors = false;
        this.render();
    }

    /**
     * Draw lines when scheduler/gantt is rendered.
     * @private
     */
    render() {
        this.getColorsFromCSS();
        this.drawLines();
    }

    /**
     * Draw column lines to a offscreen canvas, convert to base64 and use as background image.
     * @private
     */
    drawLines() {
        // Early bailout for timeaxis without start date
        if (!this.client.timeAxis.startDate) {
            return;
        }

        // We cannot rely on timeAxisViewModel because rendered header may not include full top header.
        // This means we should generate whole top level tick and then iterate over ticks, calculating lines position
        // depending on header config
        const
            me            = this,
            { client }    = me,
            { timeAxis }  = client,
            axisStart     = timeAxis.startDate,
            viewModel     = client.timeAxisViewModel,
            tickSize      = viewModel.tickSize,
            element       = client.backgroundCanvas,
            canvas         = document.createElement('canvas'),
            ctx            = canvas.getContext('2d'),
            linesForLevel = viewModel.columnLinesFor,
            // header to draw lines for
            targetHeader  = viewModel.headers[linesForLevel],
            headers       = viewModel.headers,
            // header which is used to draw major lines
            upperHeader   = headers[headers.indexOf(targetHeader) - 1] || headers[0],
            // header defining ticks
            lowerHeader   = headers[headers.length - 1],
            // when unit is year we should use 1 as increment
            startDate     = timeAxis.floorDate(axisStart, false, upperHeader.unit, upperHeader.unit === 'year' ? 1 : upperHeader.increment || 1),
            endDate       = DateHelper.getNext(startDate, upperHeader.unit, upperHeader.increment || 1, timeAxis.weekStartDay),
            // we rendered one upper header and need to calculate how many ticks fit in it
            ticksInHeader  = Math.round(DateHelper.getDurationInUnit(startDate, endDate, lowerHeader.unit)) / (lowerHeader.increment || 1),
            nbrLinesToDraw = Math.round(DateHelper.getDurationInUnit(startDate, endDate, targetHeader.unit)) / (targetHeader.increment || 1),
            // shows how many ticks should we skip before drawing next line
            ratio          = ticksInHeader / nbrLinesToDraw;

        if (client.isHorizontal) {
            if (axisStart) {
                const
                    doUnitsAlign   = headers.length > 1 && DateHelper.doesUnitsAlign(upperHeader.unit, targetHeader.unit),
                    offsetDate     = doUnitsAlign ? startDate : timeAxis.floorDate(axisStart, false, targetHeader.unit, targetHeader.increment),
                    // TODO: isContinuous check solved the issue I was seeing but not very generic
                    offset         = !timeAxis.isContinuous ? 0 : DateHelper.getDurationInUnit(offsetDate, axisStart, lowerHeader.unit, true) / timeAxis.increment * tickSize,
                    // this is position from left side of the canvas to draw first line, otherwi
                    startPos       = 10,
                    height         = 20;

                DomHelper.removeEachSelector(element, '.b-column-line-major');

                let isMajor = false,
                    majorHeaderIsRegular = true;

                if (targetHeader !== upperHeader && doUnitsAlign && lowerHeader.unit === 'day' &&
                    DateHelper.compareUnits(upperHeader.unit, 'month') !== -1) {
                    // This condition means, that major lines are irregular, e.g. when lower level is days and upper is
                    // months. Since months have different duration, we cannot safely repeat images
                    majorHeaderIsRegular = false;
                    timeAxis.forEachAuxInterval(upperHeader.unit, upperHeader.increment, (start, end) => {
                        DomHelper.append(element, {
                            tag       : 'div',
                            className : 'b-column-line-major',
                            style     : `left:${viewModel.getPositionFromDate(end) - 1}px;`
                        });
                    });
                }

                // hack for FF to not crash when trying to create too wide canvas.
                canvas.width = Math.min(ticksInHeader * 2 * tickSize, 32767);
                canvas.height = height;
                ctx.translate(-0.5, -0.5);
                ctx.lineWidth = 2;

                for (let i = 0; i < nbrLinesToDraw; i++) {
                    // Only first interval may be major
                    if (i === 0) {
                        // Filtered time axis should not have any major lines
                        isMajor = upperHeader !== targetHeader && doUnitsAlign && majorHeaderIsRegular && timeAxis.isContinuous;
                    }
                    else {
                        isMajor = false;
                    }

                    const tickStyle = (isMajor && me.majorTickStyle) || (!isMajor && me.tickStyle);
                    if (tickStyle !== 'solid') {
                        switch (tickStyle) {
                            case 'dashed':
                                ctx.setLineDash([6, 4]);
                                break;
                            case 'dotted':
                                ctx.setLineDash([2, 3]);
                                break;
                        }
                    }

                    ctx.beginPath();
                    ctx.strokeStyle = isMajor ? me.majorTickColor : me.tickColor;

                    // draw ticks
                    ctx.moveTo(i * ratio * tickSize * 2 + startPos - 1, 0);
                    ctx.lineTo(i * ratio * tickSize * 2 + startPos - 1, height + 2);
                    ctx.stroke();
                }

                // use as background image
                element.style.backgroundImage = `url(${canvas.toDataURL()})`;
                element.style.backgroundSize = `${canvas.width / 2}px`;
                element.style.backgroundPositionX = `${-(startPos / 2 + offset)}px`;
            }
        }
        else {
            // hack for FF to not crash when trying to create too wide canvas.
            canvas.width = client.timeAxisColumn.resourceColumns.columnWidth * 2;
            canvas.height = 2;
            ctx.translate(-0.5, -0.5);
            ctx.lineWidth = 2;

            if (axisStart) {

                DomHelper.removeEachSelector(element, '.b-column-line-major');

                // Major lines always as divs to not get so large image
                if (targetHeader !== upperHeader) {
                    timeAxis.forEachAuxInterval(upperHeader.unit, upperHeader.increment, (start, end) => {
                        DomHelper.append(element, {
                            tag       : 'div',
                            className : 'b-column-line-major',
                            style     : `top:${viewModel.getPositionFromDate(end) - 1}px;`
                        });
                    });
                }

                if (me.tickStyle !== 'solid') {
                    switch (me.tickStyle) {
                        case 'dashed':
                            ctx.setLineDash([6, 4]);
                            break;
                        case 'dotted':
                            ctx.setLineDash([2, 3]);
                            break;
                    }
                }

                const height = ratio * tickSize * 2;

                canvas.height = height;

                ctx.beginPath();
                ctx.strokeStyle = me.tickColor;
                ctx.lineWidth = 2;

                // draw ticks
                ctx.moveTo(0,                height - 1);
                ctx.lineTo(canvas.width + 2, height - 1);
                ctx.stroke();
            }

            ctx.beginPath();
            ctx.strokeStyle = me.tickColor;

            // draw ticks
            ctx.moveTo(canvas.width - 1, 0);
            ctx.lineTo(canvas.width - 1, canvas.height + 2);
            ctx.stroke();

            // use as background image
            element.style.backgroundImage = `url(${canvas.toDataURL()})`;
            element.style.backgroundSize = `${canvas.width / 2}px`;
            element.style.backgroundPositionX = '0';
        }
    }

    //endregion

    //region Events

    /**
     * Redraw lines when time axis changes.
     * @private
     */
    updateCanvasSize() {
        if (!this.disabled) {
            this.drawLines();
        }
    }

    //endregion
}

GridFeatureManager.registerFeature(ColumnLines, true, ['Scheduler', 'Gantt']);
