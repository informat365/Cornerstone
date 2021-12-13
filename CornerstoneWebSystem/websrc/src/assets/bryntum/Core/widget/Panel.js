import './Tool.js';
import Container from './Container.js';
import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';
import ObjectHelper from '../helper/ObjectHelper.js';
import EventHelper from '../helper/EventHelper.js';
import WidgetHelper from '../helper/WidgetHelper.js';
import DomHelper from '../helper/DomHelper.js';
import Toolbar from './Toolbar.js';

/**
 * @module Core/widget/Panel
 */

const
    acceptNode = e => !e.classList.contains('b-focus-trap') && DomHelper.isFocusable(e) ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_SKIP,
    byWeight = (l, r) => (l.weight || 0) - (r.weight || 0),
    byWeightReverse = (l, r) => (r.weight || 0) - (l.weight || 0);

// https://github.com/webcomponents/webcomponentsjs/issues/556
// Work around Internet Explorer wanting a function instead of an object.
// IE also *requires* this argument where other browsers don't.
acceptNode.acceptNode = acceptNode;

/**
 * Panel widget. A general pupose container which may be used to contain child {@link Core.widget.Container#config-items}
 * or {@link Core.widget.Widget#config-html}.
 *
 * Also may dock a {@link #config-header} and {@link #config-footer} either at top/bottom or left/right
 *
 * @extends Core/widget/Container
 *
 * @example
 * let panel = new Panel({
 *   title   : 'A Test Panel',
 *   widgets : [
 *     { type : 'text', placeholder: 'Text' },
 *   ],
 *   bbar : [{
 *     text : 'Proceed',
 *     onClick : () => {
 *       alert('Proceeding!');
 *     }
 *   }]
 * });
 *
 * @classType panel
 */
export default class Panel extends Container {
    //region Config
    static get $name() {
        return 'Panel';
    }

    static get defaultConfig() {
        return {
            /**
             * By default, tabbing within a Panel is not contained, ie you can TAB out of the Panel
             * forwards or backwards.
             * Configure this as `true` to disallow tabbing out of the Panel, and make tabbing circular within this Panel.
             * @config {Boolean}
             * @default false
             */
            trapFocus : null,

            /**
             * A title to display in the header. Causes creation and docking of a header
             * to the top if no header is configured.
             *
             * If specified, overrides any title configured within the {@link #config-header} configuration.
             * @default
             * @config {String}
             */
            title : null,

            /**
             * Config object of a header. May contain a `title`, `titleAlign`, `dock`
             * and a `cls` property. If passed as a string, the value is used
             * as the title, dock is defaulted to `'top', and titleAlign` is defaulted to `start`.
             *
             * The `dock` property may be `top`, `right`, `bottom` or `left`.
             * The `titleAlign` property may be `start`, `center` or `end`.
             * @default
             * @config {Object|String}
             */
            header : null,

            /**
             * Config object of a footer. May contain a `dock`
             * and a `cls` property.
             *
             * The `dock` property may be `top`, `right`, `bottom` or `left`.
             * @default
             * @config {Object|String}
             */
            footer : null,

            /**
             * The tools to add either before or after the title in the Panel header.
             * Each property name is the reference by which an instantiated Tool Widget
             * may be retrieved from the live `{@link #property-tools}` property.
             * Each tool may have the following properties:
             * * `cls` The CSS class to apply.
             * * `handler` A method in the field to call upon click
             * * `align` `'start'` or `'end'` which end of the header the tool should go.
             * * `weight` (Optional) Heigher weighted tools gravitate towards the centre.
             * @config {Object}
             */
            tools : null,

            /**
             * A Config object representing the configuration of a {@link Core.widget.Toolbar},
             * or array of config objects representing the child items of a Toolbar.
             *
             * This creates a toolbar docked to the top of the panel immediately below the header.
             * @config {Object[]|Object}
             */
            tbar : null,

            /**
             * A Config object representing the configuration of a {@link Core.widget.Toolbar},
             * or array of config objects representing the child items of a Toolbar.
             *
             * This creates a toolbar docked to the bottom of the panel immediately above the footer.
             * @config {Object[]|Object}
             */
            bbar : null
        };
    }

    //endregion

    /**
     * A header {@link #config-tools tool} has been clicked.
     * @event toolclick
     * @param {Core.widget.Tool} source - This Panel.
     * @param {Core.widget.Tool} tool - The tool which is being clicked.
     */

    //region Init & destroy

    set element(element) {
        const me = this,
            headerFromTitle = me.title && (!me.parent || !me.parent.suppressChildHeaders),
            // If tools exist, we need to create the header infrastructure
            header = me.header || (me.tools || headerFromTitle ? {} : null),
            footer = me.footer,
            title = header && (typeof header === 'string' ? header : me.title || header.title),
            titleAlign = header && (header.titleAlign || 'start'),
            headerClass = me.classHierarchy(Panel).map(cls => `b-${cls.$name.toLowerCase()}-header`).join(' '),
            tbar = me.tbar,
            bbar = me.bbar || me.buttons,
            children = [{
                reference : 'topFocusTrap',
                className : 'b-focus-trap',
                tabIndex  : 0
            }],
            startTools = [],
            endTools = [],
            result = {
                children
            };

        if (me.hasItems || me.focusable) {
            result.tabIndex = 0;
        }

        if (header) {
            const headerChildren = [{
                reference : 'titleElement',
                className : `b-header-title b-align-${titleAlign}`,
                html      : title
            }];

            for (const toolRef in me.tools) {
                const tool = me.tools[toolRef];
                if (tool.align === 'start') {
                    startTools.unshift(tool);
                }
                else {
                    endTools.push(tool);
                }
            }

            // The tools at each end are sorted "gravitationally".
            // Higher weight sorts towards the center which is the title element.
            startTools.sort(byWeight);
            endTools.sort(byWeightReverse);
            headerChildren.unshift(...startTools.map(t => t.element));
            headerChildren.push(...endTools.map(t => t.element));

            children.push({
                tag       : 'header',
                reference : 'headerElement',
                className : `${headerClass} b-dock-${header.dock || 'top'} ${header.cls || ''}`,
                children  : headerChildren
            });
        }

        if (tbar) {
            tbar.layout.renderChildren();
            children.push(tbar.element);
        }

        children.push(me.bodyConfig);

        if (bbar) {
            bbar.layout.renderChildren();
            children.push(bbar.element);
        }

        if (footer) {
            children.push({
                tag       : 'footer',
                reference : 'footerElement',
                className : `${headerClass} b-dock-${footer.dock || 'bottom'} ${footer.cls || ''}`
            });
        }

        children.push({
            reference : 'bottomFocusTrap',
            className : 'b-focus-trap',
            tabIndex  : 0
        });

        super.element = result;
    }

    set bodyConfig(bodyConfig) {
        this._bodyConfig = bodyConfig;
    }

    get bodyConfig() {
        return this._bodyConfig || {
            reference : 'bodyElement',
            className : this.classHierarchy(Panel).map(cls => `b-${cls.$name.toLowerCase()}-content`).join(' '),
            html      : this.html
        };
    }

    get element() {
        return super.element;
    }

    set tbar(tbar) {
        if (tbar instanceof Toolbar) {
            tbar.parent = this;
        }
        else {
            if (Array.isArray(tbar)) {
                tbar = {
                    items : tbar
                };
            }
            tbar = new Toolbar(Object.assign({
                parent : this
            }, tbar));
        }
        this.hasItems = this.hasItems || tbar.hasItems;
        tbar.element.classList.add('b-top-toolbar', `b-dock-${tbar.dock || 'top'}`);
        this._tbar = tbar;
    }

    get tbar() {
        return this._tbar;
    }

    set bbar(bbar) {
        if (bbar instanceof Toolbar) {
            bbar.parent = this;
        }
        else {
            if (Array.isArray(bbar)) {
                bbar = {
                    items : bbar
                };
            }
            bbar = WidgetHelper.createWidget(Object.assign({
                type   : 'toolbar',
                parent : this
            }, bbar));
        }
        this.hasItems = this.hasItems || bbar.hasItems;
        bbar.element.classList.add('b-bottom-toolbar', `b-dock-${bbar.dock || 'bottom'}`);
        this._bbar = bbar;
    }

    get bbar() {
        return this._bbar;
    }

    // Override to iterate docked Toolbars in the correct order around contained widgets.
    get childItems() {
        const
            me = this,
            items = me.items ? me.items.slice() : [];

        if (me.tools) {
            items.unshift(...Object.values(me.tools));
        }
        if (me.tbar) {
            items.unshift(me.tbar);
        }
        if (me.bbar) {
            items.push(me.bbar);
        }

        return items;
    }

    /**
     * The tool Widgets as specified by the {@link #config-tools} configuration
     * (and the {@link Core.widget.Popup#config-closable} configuration in the Popup subclass).
     * Each is a {@link Core.widget.Widget Widget} instance which may be hidden, shown and observed and styled just like any other widget.
     * @property {Object}
     */
    set tools(tools) {
        const me = this,
            myTools = me._tools = {};

        for (const toolRef in tools) {
            const tool = myTools[toolRef] = WidgetHelper.createWidget(ObjectHelper.assign({
                type      : 'tool',
                ref       : toolRef,
                //@nige: Widget defines ref but not reference, typo?
                reference : toolRef,
                parent    : me
            }, tools[toolRef]), me.defaultToolType || 'tool');

            me.registerReference(tool, tool.ref);
        }
    }

    get tools() {
        return this._tools;
    }

    set trapFocus(trapFocus) {
        const me = this;

        me.element.classList[trapFocus ? 'add' : 'remove']('b-focus-trapped');
        if (trapFocus) {
            me.focusTrapListener = EventHelper.on({
                element  : me.element,
                focusin  : 'onFocusTrapped',
                delegate : '.b-focus-trap',
                thisObj  : me
            });

            // Create a TreeWalker which visits focusable elements.
            if (!me.treeWalker) {
                me.treeWalker = document.createTreeWalker(me.element, NodeFilter.SHOW_ELEMENT, acceptNode, false);
            }
        }
        else {
            if (me.focusTrapListener) {
                me.focusTrapListener();
                me.focusTrapListener = null;
            }
        }
        me._trapFocus = trapFocus;
    }

    get trapFocus() {
        return this._trapFocus;
    }

    onFocusTrapped(e) {
        const me = this,
            treeWalker = me.treeWalker;

        // The only way of focusing these invisible elements is by TABbing to them.
        // If we hit the bottom one, wrap to the top.
        if (e.target === me.bottomFocusTrap) {
            treeWalker.currentNode = me.topFocusTrap;
            treeWalker.nextNode();
        }
        // If we hit the top one, wrap to the bottom.
        else {
            treeWalker.currentNode = me.bottomFocusTrap;
            treeWalker.previousNode();
        }

        me.requestAnimationFrame(() => treeWalker.currentNode.focus());
    }

    get focusElement() {
        // Either use our Containerness to yield the focus element of
        // a descendant or fall back to the encapsulating element.
        return this.hasItems && (super.focusElement || this.element);
    }

    get contentElement() {
        return this.element && this.bodyElement;
    }

    get widgetClassList() {
        const me = this,
            result = super.widgetClassList,
            header = me.header;

        if (header || me.title || me.tools) {
            result.push(`b-panel-has-header b-header-dock-${header && header.dock || 'top'}`);
        }

        if (me.tbar) {
            result.push(`b-panel-has-top-toolbar`);
        }

        if (me.bbar) {
            result.push(`b-panel-has-bottom-toolbar`);
        }

        return result;
    }

    //endregion

    doDestroy() {
        const { tools } = this;

        if (tools) {
            for (const t of Object.values(tools)) {
                t.destroy();
            }
        }

        // Only destroy the widgets if they have been instanced.
        if (this.tbar) {
            this.tbar.destroy();
        }

        if (this.bbar) {
            this.bbar.destroy();
        }

        super.doDestroy();
    }
}

BryntumWidgetAdapterRegister.register('panel', Panel);
