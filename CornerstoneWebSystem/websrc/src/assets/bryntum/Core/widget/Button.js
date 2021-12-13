import Widget from './Widget.js';
import TemplateHelper from '../helper/TemplateHelper.js';
import Badge from './mixin/Badge.js';
import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';
import WidgetHelper from '../helper/WidgetHelper.js';
import DomHelper from '../helper/DomHelper.js';
import EventHelper from '../helper/EventHelper.js';

//TODO: should togglebutton be own class implemented as input type=checkbox?
//TODO: A toggling widget's focusElement should be an opacity:0 input type=checkbox which covers the clickable area.

/**
 * @module Core/widget/Button
 */

const
    bIcon = /^b-icon-/,
    bFa = /^b-fa-/;

/**
 * Button widget, wraps and styles a regular <code>&lt;button&gt;</code> element. Can display text and icon and allows specifying button color.
 *
 * @extends Core/widget/Widget
 * @mixes Core/widget/mixin/Badge
 *
 * @example
 * // button with text and icon
 * let button = new Button({
 *   icon: 'b-fa-plus-circle',
 *   text: 'Add',
 *   color: 'green',
 *   onClick: () => {}
 * });
 *
 * @classType button
 * @externalexample widget/Button.js
 */
export default class Button extends Badge(Widget) {
    //region Config
    static get $name() {
        return 'Button';
    }

    static get defaultConfig() {
        return {
            /**
             * Button icon class.
             *
             * All [Font Awesome](https://fontawesome.com/cheatsheet) icons may also be specified as `'b-fa-' + iconName`.
             *
             * Otherwise this is a developer-defined CSS class string which results in the desired icon.
             * @config {String}
             */
            icon : null,

            /**
             * Icon class for the buttons pressed state. Only applies to toggle buttons
             *
             * All [Font Awesome](https://fontawesome.com/cheatsheet) icons may also be specified as `'b-fa-' + iconName`.
             *
             * Otherwise this is a developer-defined CSS class string which results in the desired icon.
             *
             * ```
             * new Button({
             *    // Icon for unpressed button
             *    icon        : 'b-fa-wine-glass',
             *
             *    // Icon for pressed button
             *    pressedIcon : 'b-fa-wine-glass-alt',
             *
             *    // Only applies to toggle buttons
             *    toggleable  : true
             * });
             * ```
             *
             * @config {String}
             */
            pressedIcon : null,

            /**
             * Button icon alignment. May be `'start'` or `'end'`. Defaults to `'start'`
             * @config {String}
             */
            iconAlign : 'start',

            /**
             * Button text
             * @config {String}
             */
            text : '',

            /**
             * Button color (should have match in button.scss or your custom styling). Valid values in Bryntum themes
             * are:
             * * b-amber
             * * b-blue
             * * b-dark-gray
             * * b-deep-orange
             * * b-gray
             * * b-green
             * * b-indigo
             * * b-lime
             * * b-light-gray
             * * b-light-green
             * * b-orange
             * * b-purple
             * * b-red
             * * b-teal
             * * b-white
             * * b-yellow
             * Combine with specifying `b-raised` for raised/filled style (theme dependent).
             *
             * ```
             * new Button({
             *    color : 'b-teal b-raised'
             * });
             * ```
             *
             * @config {String}
             */
            color : null,

            /**
             * Enabled toggling of the button (stays pressed when pressed).
             * @config {Boolean}
             * @default
             */
            toggleable : false,

            /**
             * Initially pressed or not. Only applies with `toggleable = true`.
             * ```
             * const toggleButton = new Button({
             *    toggleable : true,
             *    text : 'Enable cool action'
             * });
             * ```
             * @config {Boolean}
             * @default
             */
            pressed : false,

            /**
             * Indicate that this button is part of a group where only one button can be pressed. Assigning a value
             * also sets `toggleable` to `true`.
             * ```
             * const yesButton = new Button({
             *    toggleGroup : 'yesno',
             *    text        : 'Yes'
             * });
             *
             * const noButton = new Button({
             *    toggleGroup : 'yesno',
             *    text        : 'No'
             * });
             * ```
             * @config {String}
             */
            toggleGroup : null,

            ripple : {
                radius : 75
            },

            defaultBindProperty : 'text',

            localizableProperties : ['text'],

            /**
             * A submenu configuration object, or an array of MenuItem configuration
             * objects from which to create a submenu which is shown when this button is pressed.
             *
             * Note that this does not have to be a Menu. The `type` config can be used
             * to specify any widget as the submenu.
             * 
             * May also be specified as a fully instantiated {@link Core.widget.Widget#config-floating floating Widget}
             * such as a {@link Core.widget.Popup Popup}.
             * @config {Object|Object[]|Core.widget.Widget}
             */
            menu : null
        };
    }

    template() {
        const me = this;

        return TemplateHelper.tpl`
            <button class="${me.color || ''} ${me.toggleable && me.pressed ? 'b-pressed' : ''}" ${me.toggleGroup ? `data-group="${me.toggleGroup}"` : ''}>
                \xa0
            </button>
        `;
    }

    //endregion

    //region Construct/Destroy

    construct(config = {}, ...args) {
        if (config.toggleGroup) {
            config.toggleable = true;
        }

        super.construct(config, ...args);

        EventHelper.on({
            element : this.element,
            click   : 'onInternalClick',
            thisObj : this
        });
    }

    doDestroy() {
        this.destroyProperties('menu');
        super.doDestroy();
    }

    //endregion

    eachWidget(fn, deep = true) {
        const { menu } = this;

        if (menu) {
            if (fn(menu) === false) {
                return;
            }
            if (deep && menu.eachWidget) {
                menu.eachWidget(fn, deep);
            }
        }
    }

    onFocusOut(e) {
        super.onFocusOut(e);
        if (this.menu) {
            this.menu.hide();
        }
    }

    //region Getters/Setters

    get focusElement() {
        return this.element;
    }

    /**
     * Get/set text displayed on the button.
     * @property {String}
     */
    get text() {
        return this._text;
    }

    set text(text) {
        const { element } = this;

        if (text === null || text === undefined) text = '';

        element.lastChild.data = this._text = text;
        element.classList[text ? 'add' : 'remove']('b-text');
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
                scrollAction : 'realign',
                constrainTo  : document.body,
                forElement   : me.element,
                align        : 't0-b0',
                owner        : me,
                onHide() {
                    // In case the reason for the hide is a mousedown
                    // on this button, wait until after any impending
                    // click handler to sync our state with the visibility.
                    me.setTimeout(() => me.toggle(false), 300);
                }
            }, result));
        }

        return result;
    }

    set menu(menu) {
        this._menu = menu;

        // We are toggleable if there's a menu.
        // Pressed means menu visible, not pressed means menu hidden.
        this.toggleable = Boolean(menu);
    }

    /**
     * Get/set button pressed state
     * @property {Boolean}
     */
    set pressed(pressed) {
        const me = this;

        if (pressed && me.toggleGroup) {
            DomHelper.forEachSelector(`button[data-group=${me.toggleGroup}]`, btnEl => {
                if (btnEl !== me.element) {
                    WidgetHelper.getById(btnEl.id).toggle(false);
                }
            });
        }

        me._pressed = pressed;

        if (me.element) me.element.classList[pressed ? 'add' : 'remove']('b-pressed');
    }

    get pressed() {
        return this._pressed;
    }

    /**
     * Get/set the Button icon
     * @property {String}
     */
    set icon(icon) {
        this._icon = icon;
        this.syncIconCls();
    }

    get icon() {
        return this._icon;
    }

    /**
     * Get/set the Button pressed icon
     * @property {String}
     */
    set pressedIcon(icon) {
        this._pressedIcon = icon;
        this.syncIconCls();
    }

    get pressedIcon() {
        return this._pressedIcon;
    }

    /**
     * Get/Set the Button icon alignment.
     * May be `'start'` or `'end'`. Defaults to `'start'`
     * @property {String}
     */

    set iconAlign(iconAlign) {
        const
            me       = this,
            oldAlign = me._iconAlign;

        if (iconAlign !== oldAlign) {
            if (oldAlign) {
                me.element.classList.remove(`b-icon-align-${oldAlign}`);
            }
            me.element.classList.add(`b-icon-align-${iconAlign}`);
            me._iconAlign = iconAlign;
        }
    }

    get iconAlign() {
        return this._iconAlign;
    }

    //endregion

    //region Events

    /**
     * Triggers events when user clicks button
     * @fires click
     * @fires action
     * @private
     */
    onInternalClick(event) {
        const me = this,
            bEvent = { event };

        if (me.toggleable) {
            // Clicking the pressed button in a toggle group should do nothing
            if (me.toggleGroup && me.pressed) {
                return;
            }

            me.toggle(!me.pressed);
        }

        /**
         * User clicked button
         * @event click
         * @property {Core.widget.Button} button - Clicked button
         * @property {Event} event - DOM event
         */
        me.trigger('click', bEvent);

        /**
         * User performed the default action (clicked the button)
         * @event action
         * @property {Core.widget.Button} button - Clicked button
         * @property {Event} event - DOM event
         */
        // A handler may have resulted in destruction.
        if (!me.isDestroyed) {
            me.trigger('action', bEvent);
        }

        // since Widget has Events mixed in configured with 'callOnFunctions' this will also call onClick and onAction

        // stop the event since it has been handled
        event.preventDefault();
        event.stopPropagation();
    }

    //endregion

    //region Toggle

    /**
     * Toggle button state (only use with toggleable = true)
     * @param {Boolean} pressed Specify to force a certain toggle state
     * @fires toggle
     */
    toggle(pressed = null) {
        const
            me       = this,
            { menu } = me;

        if (!me.toggleable) {
            return;
        }

        if (pressed === null) {
            pressed = !me.pressed;
        }

        me.pressed = pressed;

        // For handlers from the code below to detect and avoid recursion
        me.toggling = true;

        if (menu) {
            menu.minWidth = me.width;
            menu[pressed ? 'show' : 'hide']();
        }

        me.syncIconCls();

        /**
         * Button state was toggled
         * @event toggle
         * @property {Core.widget.Button} button - Button
         * @property {Boolean} pressed - New pressed state
         */
        me.trigger('toggle', { pressed });

        me.toggling = false;
    }

    //endregion

    //region Other

    syncIconCls() {
        const me = this;
        let iconEl = me._iconEl;

        if (me.icon) {
            if (!iconEl) {
                iconEl = me._iconEl = DomHelper.createElement({
                    tag         : 'i',
                    nextSibling : me.element.firstChild
                });
            }

            const iconCls = (me.pressed && me.pressedIcon) ? me.pressedIcon : me.icon;
            me._iconEl.className = iconCls;

            // if it's a supplied icon class b-fa-xxx or b-icon-xxx, add extra class
            // so user doesn't have to; they can just specify the icon
            if (bIcon.test(iconCls)) {
                iconEl.classList.add('b-icon');
            }
            else if (bFa.test(iconCls)) {
                iconEl.classList.add('b-fa');
            }
        }
        else {
            iconEl && iconEl.remove();
        }
    }

    //endregion
}

BryntumWidgetAdapterRegister.register('button', Button);
