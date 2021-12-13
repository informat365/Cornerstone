import Scheduler from '../view/Scheduler.js';
import { setupFocusListeners } from '../../Core/GlobalEvents.js';

//TODO: If we want to improve on this, add settings as attributes, replace data-field with field etc.

/**
 * @module Scheduler/customElements/SchedulerTag
 */

/**
 * Import this file to be able to use the tag **&lt;bryntum-scheduler&gt;** to create a scheduler. This is more of a
 * proof of concept than a ready to use class. Dataset from **&lt;data&gt;** and **&lt;bryntum-scheduler&gt;** tags is
 * applied to record and scheduler config, which means, that you can pass any documented config there, not only
 * demonstrated here. Dataset attributes are translated as follows:
 *
 *  * data-view-preset -> viewPreset
 *  * data-start-date -> startDate
 *
 *  etc.
 * ## Example
 * ```
 * &lt;bryntum-scheduler data-view-preset="weekAndDay" data-start-date="2018-04-02" data-end-date="2018-04-09"&gt;
 *  &lt;column data-field="name"&gt;Name&lt;/column&gt;
 *      &lt;data&gt;
 *          &lt;events&gt;
 *              &lt;data data-id="1" data-resource-id="1" data-start-date="2018-04-03" data-end-date="2018-04-05"&gt;&lt;/data&gt;
 *              &lt;data data-id="2" data-resource-id="2" data-start-date="2018-04-04" data-end-date="2018-04-06"&gt;&lt;/data&gt;
 *              &lt;data data-id="3" data-resource-id="3" data-start-date="2018-04-05" data-end-date="2018-04-07"&gt;&lt;/data&gt;
 *          &lt;/events&gt;
 *          &lt;resources&gt;
 *              &lt;data data-id="1" data-name="Daniel"&gt;&lt;/data&gt;
 *              &lt;data data-id="2" data-name="Steven"&gt;&lt;/data&gt;
 *              &lt;data data-id="3" data-name="Sergei"&gt;&lt;/data&gt;
 *          &lt;/resources&gt;
 *      &lt;/data&gt;
 * &lt;/bryntum-scheduler&gt;
 * ```
 */
// This is required to make trial build function normally
const TagBase = window.customElements ? HTMLElement : Object;

export default class SchedulerTag extends TagBase {
    constructor() {
        super();

        const
            me        = this,
            columns   = [],
            resources = [],
            events    = [];

        // create columns and data
        for (let tag of me.children) {
            if (tag.tagName === 'COLUMN') {
                const
                    width  = parseInt(tag.dataset.width),
                    flex   = parseInt(tag.dataset.flex),
                    column = {
                        field : tag.dataset.field,
                        text  : tag.innerHTML
                    };

                if (width) column.width = width;
                else if (flex) column.flex = flex;
                else column.flex = 1;

                columns.push(column);
            }
            else if (tag.tagName === 'DATA') {
                for (let storeType of tag.children) {
                    for (let record of storeType.children) {
                        const row = {};

                        Object.assign(row, record.dataset);

                        if (storeType.tagName === 'EVENTS') {
                            events.push(row);
                        }
                        else if (storeType.tagName === 'RESOURCES') {
                            resources.push(row);
                        }
                    }
                }
            }
        }

        const
            // go over to the dark side
            shadowRoot = this.attachShadow({ mode : 'open' }),
            // include css and target div in shadow dom
            link       = document.createElement('link');

        link.rel = 'stylesheet';
        link.href = '../../build/scheduler.default.css';

        link.onload = () => {
            const div = document.createElement('div');

            div.id = 'container';
            div.style.width = '100%';
            div.style.height = '100%';

            shadowRoot.appendChild(div);

            // Listen to focus events on shadow root to handle focus inside the shadow dom
            setupFocusListeners(shadowRoot);

            const config = {
                appendTo : div,
                columns  : columns,
                resources,
                events
            };
            Object.assign(config, me.dataset);

            // render as usual
            const scheduler = new Scheduler(config);

            // for testing, set first scheduler as global variable
            if (!window.scheduler) window.scheduler = scheduler;
        };

        shadowRoot.appendChild(link);
    }
}

// Try-catch to make trial work
try {
    window.customElements && window.customElements.define('bryntum-scheduler', SchedulerTag);
}
catch (error) {

}
