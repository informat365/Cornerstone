import Container from './Container.js';
import TemplateHelper from '../helper/TemplateHelper.js';
import EventHelper from '../helper/EventHelper.js';
import DomHelper from '../helper/DomHelper.js';
import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';
import './layout/Card.js';

/**
 * @module Core/widget/TabPanel
 */

/**
 * Tab panel widget, displays a collection of tabs which each can contain other widgets. Layout is handled using css
 *
 * @extends Core/widget/Widget
 * @example
 * let tabPanel = new TabPanel({
 *  items: [
 *      {
 *          title: 'First',
 *          items: [
 *              { type: 'textfield', label: 'Name' },
 *              ...
 *          ]
 *      }, {
 *          title: 'Last',
 *          items: [
 *              ...
 *          ]
 *      }
 *  ]
 * });
 *
 * @classType tabpanel
 * @externalexample widget/TabPanel.js
 */
export default class TabPanel extends Container {
    //region Config
    static get $name() {
        return 'TabPanel';
    }

    static get defaultConfig() {
        return {
            template : me => TemplateHelper.tpl`
                <div>
                    <div class="b-tabpanel-tabs" reference="tabStrip">
                        ${me.items.map((tab, i) => `
                        <div tabindex="-1" data-index="${i}" class="b-tabpanel-tab ${(i === me.activeIndex) ? 'b-active' : ''} ${tab.cls || ''}" style="${me.tabMinWidth ? 'min-width:' + DomHelper.setLength(me.tabMinWidth) + ';' : ''} ${me.tabMaxWidth ? 'max-width:' + DomHelper.setLength(me.tabMaxWidth) + ';' : ''}" >
                            <span class="b-tabpanel-tab-title">${tab.title}</span>
                        </div>
                        `)}
                    </div>
                    <div class="b-tabpanel-body" reference="tabPanelBody" data-activeIndex="${me.activeIndex}">
                    </div>
                </div>
            `,

            itemCls : 'b-tabpanel-item',

            defaultType : 'container',

            /**
             * The index of the initially active tab.
             * @config {Number}
             * @default
             */
            activeTab : 0,

            /**
             * Min width of a tab title. 0 means no minimum width. This is default.
             * @config {Number}
             * @default
             */
            tabMinWidth : null,

            /**
             * Max width of a tab title. 0 means no maximum width. This is default.
             * @config {Number}
             * @default
             */
            tabMaxWidth : null,

            /**
             * Specifies whether to slide tabs in and out of visibility.
             * @config {Boolean}
             * @default
             */
            animateTabChange : true,

            layout : 'card',

            // Prevent child panels from displaying a header unless explicitly configured with one
            suppressChildHeaders : true
        };
    }

    //endregion

    //region Init

    construct(config) {
        const me = this;

        super.construct(config);

        // assign elements to titleElement, so they can get updated automatically when title is changed
        me.items.forEach((tab, i) => {
            tab.titleElement = me.element.querySelector(`div.b-tabpanel-tab[data-index="${i}"] span`);
        });

        EventHelper.on({
            element  : me.tabStrip,
            delegate : '.b-tabpanel-tab',
            click    : 'onTabElementClick',
            thisObj  : me
        });
    }

    //endregion

    set tabMinWidth(width) {
        this._tabMinWidth = width;

        this.element && DomHelper.forEachSelector(this.element, '.b-tabpanel-tab', tab => {
            DomHelper.setLength(tab, 'minWidth', width || null);
        });
    }

    get tabMinWidth() {
        return this._tabMinWidth;
    }

    set tabMaxWidth(width) {
        this._tabMaxWidth = width;

        this.element && DomHelper.forEachSelector(this.element, '.b-tabpanel-tab', tab => {
            DomHelper.setLength(tab, 'maxWidth', width || null);
        });
    }

    get tabMaxWidth() {
        return this._tabMaxWidth;
    }

    set layout(layout) {
        super.layout = layout;
        this.layout.animateCardChange = this.animateTabChange;
    }

    get layout() {
        return super.layout;
    }

    //region Tabs

    get contentElement() {
        return this.tabPanelBody;
    }

    get focusElement() {
        const activeTab = this.items[this.activeTab || 0],
            tabFocusElement = activeTab && activeTab.focusElement;

        return tabFocusElement || this.tabStrip.children[this.activeTab];
    }

    /**
     * Get/set active tab, using index or the Widget to activate.
     * @property {Core.widget.Widget|Number}
     */
    set activeTab(index) {
        const me = this;

        if (me.isConfiguring) {
            me._activeTab = index;
        }
        else {
            const tabchangeEvent = me.layout.setActiveItem(index),
                { promise, activeIndex, activeItem } = tabchangeEvent;

            // If the layout successfully activated a new item...
            if (activeItem) {
                const { tabStrip } = me,
                    prevTabElement = tabStrip.children[me._activeTab];

                // Our UI changes immediately, our state must be accurate
                me._activeTab = activeIndex;

                // Deactivate previous active tab
                if (prevTabElement) {
                    prevTabElement.classList.remove('b-active');
                }

                // Activate the new tab
                tabStrip.children[activeIndex].classList.add('b-active');

                promise.then(() => {
                    /**
                     * The active tab has changed.
                     * @event tabchange
                     * @param {Core.widget.Widget} prevActiveItem - The previous active child widget.
                     * @param {Number} prevActiveIndex - The previous active index.
                     * @param {Core.widget.Widget} activeItem - The new active child widget.
                     * @param {Number} activeIndex - The new active index.
                     */
                    me.trigger('tabchange', tabchangeEvent);
                });
            }
        }
    }

    /**
     * The active tab index. Setting must be done through {@link #property-activeTab}
     * @property {Number}
     * @readonly
     */
    get activeIndex() {
        return this.activeTab;
    }

    get activeTab() {
        return this._activeTab;
    }

    /**
     * The active child widget. Setting must be done through {@link #property-activeTab}
     * @property {Core.widget.Widget}
     * @readonly
     */
    get activeItem() {
        return this.layout.activeItem;
    }

    //endregion

    //region Events

    onTabElementClick(event) {
        this.activeTab = event.currentTarget.dataset.index;
    }

    //endregion
}

BryntumWidgetAdapterRegister.register('tabpanel', TabPanel);
BryntumWidgetAdapterRegister.register('tabs', TabPanel);
