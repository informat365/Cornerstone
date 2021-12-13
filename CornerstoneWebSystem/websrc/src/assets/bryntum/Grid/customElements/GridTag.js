import Grid from '../view/Grid.js';
import { setupFocusListeners } from '../../Core/GlobalEvents.js';

//TODO: If we want to improve on this, add settings as attributes, replace data-field with field etc.

/**
 * @module Grid/customElements/GridTag
 */

/**
 * Import this file to be able to use the tag &lt;bryntum-grid&gt; to create a grid. This is more of a proof of concept than
 * a ready to use class. Example:
 * ```javascript
 * &lt;bryntum-grid&gt;
 *   &lt;column data-field="name"&gt;Name&lt;/column&gt;
 *   &lt;column data-field="city"&gt;City&lt;/column&gt;
 *   &lt;column data-field="food"&gt;Food&lt;/column&gt;
 *   &lt;data data-id="1" data-name="Daniel" data-city="Stockholm" data-food="Hamburgers"&gt;&lt;/data&gt;
 *   &lt;data data-id="2" data-name="Steve" data-city="Lund" data-food="Pasta"&gt;&lt;/data&gt;
 *   &lt;data data-id="3" data-name="Sergei" data-city="St Petersburg" data-food="Pizza"&gt;&lt;/data&gt;
 * &lt;/bryntum-grid&gt;
 * ```
 * @demo Grid/webcomponents
 */
export default class GridTag extends (window.customElements ? HTMLElement : Object) {
    constructor() {
        super();

        const
            me      = this,
            columns = [],
            data    = [];

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
                const row = {};
                Object.assign(row, tag.dataset);
                data.push(row);
            }
        }

        const
            // go over to the dark side
            shadowRoot = this.attachShadow({ mode : 'open' }),
            // include css and target div in shadow dom
            link       = document.createElement('link');

        link.rel = 'stylesheet';
        link.href = '../../build/grid.light.css';

        link.onload = () => {
            const div = document.createElement('div');

            div.id = 'container';
            div.style.width = '100%';
            div.style.height = '100%';

            shadowRoot.appendChild(div);

            // Listen to focus events on shadow root to handle focus inside the shadow dom
            setupFocusListeners(shadowRoot);

            // render as usual
            const grid = new Grid({
                appendTo : div,
                columns  : columns,
                data     : data
            });

            // for testing, set first grid as global variable
            if (!window.grid) window.grid = grid;
        };

        shadowRoot.appendChild(link);
    }
}

// Try-catch to make trial work
try {
    window.customElements && window.customElements.define('bryntum-grid', GridTag);
}
catch (error) {

}
