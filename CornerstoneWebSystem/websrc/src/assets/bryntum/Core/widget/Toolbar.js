import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';
import Container from './Container.js';

/**
 * @module Core/widget/Toolbar
 */

/**
 * Widget that is themed to contain Buttons which is docked to the bottom or top of
 * a {@link Core.widget.Panel Panel}.
 *
 * ```javascript
 * // create a toolbar with two buttons
 * let container = new Toolbar({
 *   items : [
 *     { text : 'Add' },
 *     { text : 'Delete' }
 *   ]
 * });
 * ```
 *
 * @extends Core/widget/Container
 * @classType toolbar
 */
export default class Toolbar extends Container {
    static get $name() {
        return 'Toolbar';
    }

    static get defaultConfig() {
        return {
            defaultType : 'button',

            /**
             * Custom CSS class to add to toolbar widgets
             * @config {String}
             * @category CSS
             */
            widgetCls : null,

            layout : 'default'
        };
    }

    createWidget(widget) {
        if (widget === '->') {
            widget = {
                type : 'widget',
                cls  : 'b-toolbar-fill'
            };
        }
        else if (widget === '|') {
            widget = {
                type : 'widget',
                cls  : 'b-toolbar-separator'
            };
        }
        else if (typeof widget === 'string') {
            widget = {
                type : 'widget',
                cls  : 'b-toolbar-text',
                html : widget
            };
        }

        const result = super.createWidget(widget);

        if (this.widgetCls) {
            result.element.classList.add(this.widgetCls);
        }

        return result;
    }
}

BryntumWidgetAdapterRegister.register('toolbar', Toolbar);
