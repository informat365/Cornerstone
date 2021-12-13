import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import GridFeatureManager from '../feature/GridFeatureManager.js';
import DomHelper from '../../Core/helper/DomHelper.js';
import TemplateHelper from '../../Core/helper/TemplateHelper.js';
import Delayable from '../../Core/mixin/Delayable.js';
import BrowserHelper from '../../Core/helper/BrowserHelper.js';

/**
 * @module Grid/feature/ColumnDragToolbar
 */

/**
 * Displays a toolbar while dragging column headers. Drop on a button in the toolbar to activate a certain function,
 * for example to group by that column. This feature simplifies certain operations on touch devices.
 *
 * This feature is <strong>disabled</strong> by default, but turned on automatically on touch devices.
 *
 * @extends Core/mixin/InstancePlugin
 *
 * @classtype columnDragToolbar
 * @externalexample feature/ColumnDragToolbar.js
 * @demo Grid/columndragtoolbar
 */
export default class ColumnDragToolbar extends Delayable(InstancePlugin) {
    //region Config

    static get $name() {
        return 'ColumnDragToolbar';
    }

    // Plugin configuration. This plugin chains some of the functions in Grid
    static get pluginConfig() {
        return {
            after : ['render']
        };
    }

    //endregion

    //region Init

    construct(grid, config) {
        if (grid.features.columnReorder) {
            grid.features.columnReorder.on('beforedestroy', this.onColumnReorderBeforeDestroy, this);
        }

        this.grid = grid;

        super.construct(grid, config);
    }

    doDestroy() {
        const me = this;

        if (me.grid.features.columnReorder && !me.grid.features.columnReorder.isDestroyed) {
            me.detachFromColumnReorder();
        }

        me.element && me.element.remove();
        me.element = null;
        super.doDestroy();
    }

    doDisable(disable) {
        if (this.initialized) {
            if (disable) {
                this.detachFromColumnReorder();
            }
            else {
                this.init();
            }
        }
        super.doDisable(disable);
    }

    init() {
        const
            me   = this,
            grid = me.grid;

        if (!grid.features.columnReorder) {
            return;
        }

        me.reorderDetacher = grid.features.columnReorder.on({
            gridheaderdragstart({ context }) {
                const column = grid.columns.getById(context.element.dataset.columnId);
                me.showToolbar(column);
            },

            gridheaderdrag : ({ context }) => me.onDrag(context),

            gridheaderabort : () => {
                me.hideToolbar();
            },

            gridheaderdrop : ({ context }) => {
                if (context.valid) {
                    me.hideToolbar();
                }
                else {
                    me.onDrop(context);
                }
            },

            thisObj : me
        });

        me.initialized = true;
    }

    onColumnReorderBeforeDestroy() {
        this.detachFromColumnReorder();
    }

    detachFromColumnReorder() {
        const me = this;

        me.grid.features.columnReorder.un('beforedestroy', me.onColumnReorderBeforeDestroy, me);

        me.reorderDetacher && me.reorderDetacher();
        me.reorderDetacher = null;
    }

    /**
     * Initializes this feature on grid render.
     * @private
     */
    render() {
        if (!this.initialized) {
            this.init();
        }
    }

    //endregion

    //region Toolbar

    showToolbar(column) {
        const me      = this,
            buttons = me.grid.getColumnDragToolbarItems(column, []),
            groups  = [];

        me.clearTimeout(me.buttonHideTimer);
        me.clearTimeout(me.toolbarHideTimer);

        buttons.forEach(button => {
            let group = groups.find(group => group.text === button.group);
            if (!group) {
                group = { text : button.group, buttons : [] };
                groups.push(group);
            }

            group.buttons.push(button);
        });

        me.element = DomHelper.append(me.grid.element, me.template(groups));

        me.groups  = groups;
        me.buttons = buttons;
        me.column  = column;
    }

    hideToolbar() {
        const me = this;

        return new Promise(resolve => {
            if (me.element && !me.toolbarHideTimer) {
                me.element.classList.add('b-remove');

                // TODO: use AnimationHelper when available
                me.toolbarHideTimer = me.setTimeout(() => {
                    me.toolbarHideTimer = null;
                    me.element && me.element.remove();
                    me.element = null;
                    resolve();
                }, 200);
            }
        });
    }

    //endregion

    //region Events

    onDrag(info) {
        const me = this;

        if (info.dragProxy.getBoundingClientRect().top - me.grid.element.getBoundingClientRect().top > 100) {
            me.element.classList.add('b-closer');
        }
        else {
            me.element.classList.remove('b-closer');
        }

        if (me.hoveringButton) {
            me.hoveringButton.classList.remove('b-hover');
            me.hoveringButton = null;
        }

        if (info.targetElement && info.targetElement.closest('.b-columndragtoolbar')) {
            me.element.classList.add('b-hover');

            let button = info.targetElement.closest('.b-columndragtoolbar  .b-target-button:not([data-disabled=true])');
            if (button) {
                button.classList.add('b-hover');
                me.hoveringButton = button;
            }
        }
        else {
            me.element.classList.remove('b-hover');
        }
    }

    onDrop(info) {
        const me = this;

        if (info.targetElement && info.targetElement.matches('.b-columndragtoolbar .b-target-button:not([data-disabled=true])')) {
            const buttonEl = info.targetElement,
                button   = me.buttons.find(button => button.name === buttonEl.dataset.name);

            if (button) {
                buttonEl.classList.add('b-activate');

                me.buttonHideTimer = me.setTimeout(() => {
                    me.hideToolbar();
                    button.onDrop({ column : me.column });
                }, 100);
            }
        }
        else {
            me.hideToolbar();
        }
    }

    //endregion

    template(groups) {
        return TemplateHelper.tpl`
            <div class="b-columndragtoolbar">     
            <div class="b-title"></div>          
            ${groups.map(group => TemplateHelper.tpl`
                <div class="b-group">
                    <div class="b-buttons">
                    ${group.buttons.map(btn => TemplateHelper.tpl`
                        <div class="b-target-button" data-name="${btn.name}" data-disabled="${btn.disabled}">
                            <i class="${btn.icon}"></i>
                            ${btn.text}
                        </div>
                    `)}
                    </div>
                    <div class="b-title">${group.text}</div>
                </div>
            `)}
            </div>`;
    }
}

ColumnDragToolbar.featureClass = 'b-hascolumndragtoolbar';

// used by default on touch devices, can be enabled otherwise
GridFeatureManager.registerFeature(ColumnDragToolbar, BrowserHelper.isTouchDevice);
