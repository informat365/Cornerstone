import Widget from './Widget.js';
import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';
import EventHelper from '../helper/EventHelper.js';

/**
 * @module Core/widget/Tool
 */

/**
 * Base class for tools.
 *
 * May be configured with a `cls` and a `handler` which is a function (or name of a function)
 * in the owning Panel.
 * @extends Core/widget/Widget
 *
 * @classType tool
 */
export default class Tool extends Widget {

    static get $name() {
        return 'Tool';
    }

    template() {
        return `<div class="b-icon"></div>`;
    }

    construct(config) {
        super.construct(config);

        EventHelper.on({
            element   : this.element,
            click     : 'onClick',
            mousedown : 'onMousedown',
            thisObj   : this
        });
    }

    onClick(e) {
        const me = this,
            panel = me.panel,
            handler = (typeof me.handler === 'function') ? me.handler : panel[me.handler];

        if (panel.trigger('toolclick', {
            tool : me
        }) !== false) {
            handler && me.callback(handler, panel, [e]);
        }
    }

    onMousedown(e) {
        const panel = this.panel,
            focusEl = panel.focusElement;

        e.preventDefault();
        if (focusEl && document.activeElement !== focusEl) {
            panel.focus();
        }
    }

    get panel() {
        return this.parent;
    }
}

BryntumWidgetAdapterRegister.register('tool', Tool);
