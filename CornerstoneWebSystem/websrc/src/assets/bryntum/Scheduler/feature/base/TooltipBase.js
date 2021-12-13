import InstancePlugin from '../../../Core/mixin/InstancePlugin.js';
import Tooltip from '../../../Core/widget/Tooltip.js';
import ClockTemplate from '../../tooltip/ClockTemplate.js';

/**
 * @module Scheduler/feature/base/TooltipBase
 */

/**
 * Base class for EventTooltip (Scheduler) and TaskTooltip (Gantt) features. Contains shared code. Not to be used directly.
 *
 * @extends Core/mixin/InstancePlugin
 */
export default class TooltipBase extends InstancePlugin {
    //region Config

    static get defaultConfig() {
        return {

            /**
             * Specify true to have tooltip updated when mouse moves, if you for example want to display date at mouse
             * position.
             * @config {Boolean}
             * @default
             */
            autoUpdate : false,

            template : null,

            cls : null,

            align : 'b-t',

            clockTemplate : null
        };
    }

    // Plugin configuration. This plugin chains some of the functions in Grid.
    static get pluginConfig() {
        return {
            chain : ['render']
        };
    }

    //endregion

    //region Init

    construct(client, config) {
        const me = this;

        // process initial config into an actual config object
        config = me.processConfig(config);

        super.construct(client, config);

        // Default triggering selector is the client's inner element selector
        if (!me.forSelector) {
            me.forSelector = `${client.eventInnerSelector}:not(.b-dragproxy)`;
        }

        me.clockTemplate = new ClockTemplate({
            timeAxisViewModel : client.timeAxisViewModel
        });

        client.on({
            [`before${client.scheduledEventName}drag`] : () => {
                me.tooltip && me.tooltip.hide();
            }
        });
    }

    // TooltipBase feature handles special config cases, where user can supply a function to use as template
    // instead of a normal config object
    processConfig(config) {
        if (typeof config === 'function') {
            return {
                template : config
            };
        }

        return config;
    }

    // override setConfig to process config before applying it (used mainly from ReactScheduler)
    setConfig(config) {
        super.setConfig(this.processConfig(config));
    }

    doDestroy() {
        this.clockTemplate && this.clockTemplate.destroy();
        this.tooltip && this.tooltip.destroy();

        super.doDestroy();
    }

    doDisable(disable) {
        if (this.tooltip) {
            this.tooltip.disabled = disable;
        }

        super.doDisable(disable);
    }

    //endregion

    render() {
        const
            me             = this,
            client         = me.client,
            ignoreSelector = [
                '.b-dragselecting',
                '.b-eventeditor-editing',
                '.b-resizing-event',
                '.b-dragcreating',
                `.b-dragging-${client.scheduledEventName}`,
                '.b-creating-dependency',
                '.b-dragproxy'
            ].map(cls => `:not(${cls})`).join('');

        if (me.tooltip) {
            me.tooltip.destroy();
        }

        /**
         * A reference to the tooltip instance, which will have a special `eventRecord` property that
         * you can use to get data from the contextual event record to which this tooltip is related.
         * @member {Core.widget.Tooltip} tooltip
         * @readonly
         */
        me.tooltip = new Tooltip(
            Object.assign({
                axisLock       : 'flexible',
                id             : me.tipId || `${me.client.id}-event-tip`,
                cls            : me.tipCls,
                forSelector    : `.b-timelinebase${ignoreSelector} ${me.forSelector}`,
                scrollAction   : 'realign',
                clippedBy      : [client.timeAxisSubGridElement, client.bodyContainer],
                forElement     : client.timeAxisSubGridElement,
                showOnHover    : true,
                hoverDelay     : 0,
                hideDelay      : 100,
                anchorToTarget : true,
                allowOver      : true,
                getHtml        : me.getTipHtml.bind(me),
                disabled       : me.disabled
            },
            me.config)
        );

        me.tooltip.on('innerhtmlupdate', me.updateDateIndicator, me);
    }

    updateDateIndicator() {
        const me           = this,
            client         = me.client,
            tip            = me.tooltip,
            timeSpanRecord = tip.activeTarget && client.resolveTimeSpanRecord(tip.activeTarget),
            endDateElement = tip.element.querySelector('.b-sch-tooltip-enddate');

        if (!timeSpanRecord) return;

        me.clockTemplate.updateDateIndicator(tip.element, timeSpanRecord.startDate);

        endDateElement && me.clockTemplate.updateDateIndicator(endDateElement, timeSpanRecord.endDate);
    }

    resolveTimeSpanRecord(forElement) {
        return this.client.resolveTimeSpanRecord(forElement);
    }

    getTipHtml({ tip, forElement }) {
        const
            me             = this,
            client         = me.client,
            recordProp     = me.recordType || `${client.scheduledEventName}Record`,
            timeSpanRecord = me.resolveTimeSpanRecord(forElement);

        // If user has mouseovered a fading away element of a deleted event,
        // an event record will not be found. In this case the tip must hide.
        if (timeSpanRecord) {
            const
                { startDate, endDate } = timeSpanRecord,
                startText              = client.getFormattedDate(startDate),
                endDateValue           = client.getDisplayEndDate(endDate, startDate),
                endText                = client.getFormattedDate(endDateValue);

            tip.eventRecord = timeSpanRecord;
            tip.title = timeSpanRecord.name;

            return me.template({
                tip,
                // eventRecord for Scheduler, taskRecord for Gantt
                [`${recordProp}`] : timeSpanRecord,
                startDate,
                endDate,
                startText,
                endText,
                startClockHtml    : me.clockTemplate.template({
                    date : startDate,
                    text : startText,
                    cls  : 'b-sch-tooltip-startdate'
                }),
                endClockHtml : timeSpanRecord.isMilestone ? '' : me.clockTemplate.template({
                    date : endDateValue,
                    text : endText,
                    cls  : 'b-sch-tooltip-enddate'
                })
            });
        }
        else {
            tip.hide();
            return '';
        }
    }
}
