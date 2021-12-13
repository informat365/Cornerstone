import TemplateHelper from '../helper/TemplateHelper.js';
import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';
import WidgetHelper from '../helper/WidgetHelper.js';
import Widget from './Widget.js';

/**
 * @module Core/widget/MenuItem
 */

const
    bIcon = /^b-icon-/,
    bFa = /^b-fa-/;

/**
 * Menu item widget, encapsulates a menu item within a Menu.
 *
 * May be configured with a {@link #config-checked} state which creates
 * a checkbox which may bwe toggled.
 *
 * Fires events when activated which bubble up through the parent hierachy
 * and may be listened for on an ancestor. See {@link Core.widget.Menu Menu}
 * for more details on usage.
 *
 * @extends Core/widget/Widget
 *
 * @classType menuitem
 */
export default class MenuItem extends Widget {
    //region Config
    static get $name() {
        return 'MenuItem';
    }

    static get defaultConfig() {
        return {
            /**
             * A submenu configuration object, or an array of MenuItem configuration
             * objects from which to create a submenu.
             *
             * Note that this does not have to be a Menu. The `type` config can be used
             * to specify any widget as the submenu.
             * @config {Object|Object[]}
             */
            menu : null,

            /**
             * Item icon class.
             *
             * All [Font Awesome](https://fontawesome.com/cheatsheet) icons may also be specified as `'b-fa-' + iconName`.
             *
             * Otherwise this is a developer-defined CSS class string which results in the desired icon.
             * @config {String}
             */
            icon : null,

            /**
             * The text to be displayed in the item
             * @config {String}
             */
            text : null,

            /**
             * If configured with a `Boolean` value, a checkbox is displayed
             * as the start icon, and the {@link #event-toggle} event is fired
             * when the checked state changes.
             * @config {Boolean}
             */
            checked : null,

            /**
             * By default, upon activate, non-checkbox menu items will collapse
             * the owning menu hierarchy.
             *
             * Configure this as `false` to cause the menu to persist after
             * activating an item
             * @config {Boolean}
             */
            closeParent : null,

            localizableProperties : ['text']
        };
    }

    /**
     * Actions this item. Fires the {@link #event-item} event, and if this
     * if a {@link #config-checked} item, toggles the checked state, firing
     * the {@link #event-toggle} event.
     */
    doAction(event) {
        const item = this,
            menu = this.parent,
            itemEvent = { menu, item, element : item.element, bubbles : true };

        if (typeof item.checked === 'boolean') {
            item.checked = !item.checked;
        }

        // Give internal handlers a chance to inject extra information before
        // user-supplied "item" handlers see the event.
        // Scheduler's ContextMenu feature does this
        item.trigger('beforeItem', itemEvent);

        /**
         * This menu item has been activated.
         *
         * Note that this event bubbles up through parents and can be
         * listened for on a top level {@link Core.widget.Menu Menu} for convenience.
         * @event item
         * @param {Core.widget.MenuItem} item - The menu item which is being actioned.
         * @param {Core.widget.Menu} menu - Menu containing the menu item
         */
        item.trigger('item', itemEvent);

        // Collapse the owning menu hierarchy if configured to do so
        if (item.closeParent && menu) {
            menu.rootMenu.close();

            // Don't prevent links doing their thing
            if (event && !item.href) {
                event.preventDefault();
            }
        }
    }

    doDestroy() {
        const menu = this._menu;

        if (menu instanceof Widget) {
            menu.destroy();
        }
        super.doDestroy();
    }

    template(me) {
        const
            icon = me.icon || ((typeof me.checked === 'boolean') ? `b-fw-icon b-icon-${me.checked ? '' : 'un'}checked` : ''),
            iconExtraCls = bIcon.test(icon) ? ' b-icon' : (bFa.test(icon) ? ' b-fa' : ''),
            tag = me.href ? 'a' : 'div';

        return TemplateHelper.tpl`
            <${tag} ${tag === 'a' ? `href="${me.href}"` : ''} ${tag === 'a' && me.target ? `target="${me.target}"` : ''} class="${me.hasMenu ? 'b-has-submenu' : ''}" ${me.name ? `data-name="${me.name}"` : ''} tabIndex="-1">
                ${icon ? `<i class="b-menuitem-icon ${icon}${iconExtraCls}" reference="iconElement"></i>` : ''}
                <span class="b-menu-text" reference="textElement">${me.text}</span>
                ${me.hasMenu ? '<i class="b-fw-icon b-icon-sub-menu" reference="subMenuIcon"></i>' : ''}
            </${tag}>`;
    }

    get focusElement() {
        return this.element;
    }

    get contentElement() {
        return this.textElement;
    }

    get isFocusable() {
        const focusElement = this.focusElement;

        // We are only focusable if the focusEl is deeply visible, that means
        // it must have layout - an offsetParent. Body does not have offsetParent.
        // Disabled menu items are focusable but cannot be activated.
        // https://www.w3.org/TR/wai-aria-practices/#h-note-17
        return focusElement && this.isVisible && (focusElement === document.body || focusElement.offsetParent);
    }

    onFocusIn(e) {
        super.onFocusIn(e);
        if (!this.disabled && this.menu) {
            this.openMenu();
        }
    }

    onFocusOut(e) {
        super.onFocusOut(e);
        this.closeMenu();
    }

    openMenu(andFocus) {
        const menu = this.menu;

        if (!this.disabled && menu) {
            menu.focusOnToFront = andFocus;
            menu.show();
            this.owner.currentSubMenu = menu;
        }
    }

    closeMenu() {
        if (this._menu instanceof Widget) {
            this.menu.close();
        }
    }

    /**
     * Get/sets the checked state of thie `MenuItem` and fires the {@link #event-toggle}
     * event upon change.
     *
     * Note that this must be configured as a `Boolean` to enable the checkbox UI.
     * @property {Boolean}
     */
    set checked(checked) {
        const me = this,
            { isConfiguring } = me;

        // If we began life as a non-checkitem, then reject attempts to set checked status.
        if (!isConfiguring && !(typeof me._checked === 'boolean')) {
            return;
        }

        // Ensure we're always dealing with a Boolean
        checked = !!checked;

        // Reject non-changes.
        if (checked !== me._checked) {
            me._checked = checked;

            // During config this is initial state; do not fire change events.
            // Initial icon state is set in the template.
            if (!isConfiguring) {
                me.iconElement.classList[checked ? 'add' : 'remove']('b-icon-checked');
                me.iconElement.classList[checked ? 'remove' : 'add']('b-icon-unchecked');

                /**
                 * The checked state of this menu item has changed.
                 *
                 * Note that this event bubbles up through parents and can be
                 * listened for on a top level {@link Core.widget.Menu Menu} for convenience.
                 * @event toggle
                 * @param {Core.widget.MenuItem} item - The menu item whose checked state changed.
                 * @param {Core.widget.Menu} menu - Menu containing the menu item
                 * @param {Boolean} checked - The _new_ checked state.
                 */
                me.trigger('toggle', {
                    menu    : me.owner,
                    item    : me,
                    checked : me._checked,
                    element : me.element,
                    bubbles : true
                });
            }
        }
    }

    get checked() {
        return this._checked;
    }

    set menu(menu) {
        this._menu = menu;
    }

    get text() {
        return this.html;
    }

    set text(text) {
        this.html = text;
    }

    /**
     * Returns the instantiated menu widget as configured by {@link #config-menu}.
     * @property {Core.widget.Widget}
     * @readonly
     */
    get menu() {
        const me = this;

        let result = me._menu;

        if (result && !(result instanceof Widget)) {
            // This covers both Array and Object which are valid items config formats.
            // menu could be { itemRef : { text : 'sub item 1 } }. But if it has
            // child items or html property in it, it's the main config
            if (typeof result === 'object' && !(('items' in result) || ('widgets' in result) || ('html' in result))) {
                result = {
                    items : result
                };
            }
            result = me.menu = WidgetHelper.createWidget(Object.assign({
                type         : 'menu',
                autoShow     : false,
                autoClose    : true,
                scrollAction : me.owner.scrollAction,
                constrainTo  : me.owner.constrainTo,
                forElement   : me.element,
                align        : 'l0-r0',
                anchor       : true,
                owner        : me,
                cls          : 'b-sub-menu' // Makes the anchor hoverable to avoid mouseleave
            }, result));
        }

        return result;
    }

    set closeParent(closeParent) {
        this._closeParent = closeParent;
    }

    get closeParent() {
        const result =  (typeof this.checked === 'boolean') ? this._closeParent : (this._closeParent !== false);

        return result && !this.hasMenu;
    }

    get hasMenu() {
        const menu = this.isConfiguring ? this.initialConfig.menu : this._menu;

        return menu && ((menu instanceof Widget) || typeof menu === 'object' || menu.length > 0);
    }
}

BryntumWidgetAdapterRegister.register('menuitem', MenuItem);
