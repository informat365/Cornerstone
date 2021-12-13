import Base from '../../Base.js';
import DomHelper from '../DomHelper.js';
import EventHelper from '../EventHelper.js';
import Events from '../../mixin/Events.js';

/**
 * @module Core/helper/util/Navigator
 */

/**
 * A helper class which allows keyboard navigation within the {@link #config-target} element.
 * @private
 */
export default class Navigator extends Events(Base) {
    static get defaultConfig() {
        return {
            /**
             * The owning Widget which is using this Navigator.
             * @config {Core.widget.Widget}
             */
            ownerCmp : null,

            /**
             * The encapsulating element in which navigation takes place.
             * @config {HTMLElement}
             */
            target : null,

            /**
             * The element which provides key events for navigation. Optional. Defaults to the {@link #config-target} element.
             * @config {HTMLElement}
             */
            keyEventTarget : null,

            /**
             * An optional key event processor which may preprocess the key event. Returning `null` prevents processing of the event.
             * @config {Function}
             */
            processEvent : null,

            /**
             * A query selector which identifies descendant elements within the {@link #config-target} which are navigable.
             * @config {String}
             */
            itemSelector : null,

            /**
             * A CSS class name to add to focused elements.
             * @config {String}
             * @default
             */
            focusCls : 'b-active',

            /**
             * An object containing key definitions keys by the key name eg:
             *
             * ```javascript
             *  keys : {
             *      "CTRL+SPACE" : 'onCtrlSpace',
             *      ENTER        : 'onEnterKey'
             *  }```
             *
             * The {@link #config-ownerCmp} is used as the `this` reference and to resolve string method names.
             *
             * Modified key names must be created prepending one or more `'CTRL+'`, `'SHIFT+'`, `'ALT+'`
             * in that order, for example `"CTRL+SHIFT+ENTER" : 'showMenu'`
             * @config {Object}
             * @default
             */
            keys : null,

            /**
             * Configure as `true` to also navigate when the `CTRL` modifier key is used along with
             * navigation keys.
             * @config {Boolean}
             * @default false
             */
            allowCtrlKey : null,

            /**
             * Configure as `true` to also navigate when the `SHIFT` modifier key is used along with
             * navigation keys.
             * @config {Boolean}
             * @default false
             */
            allowShiftKey : null
        };
    }

    set itemSelector(itemSelector) {
        this._itemSelector = itemSelector;
    }

    get itemSelector() {
        // Defaults to the itemCls of the owning Component (Most likely a List which must have itemCls)
        return this._itemSelector || `.${this.ownerCmp.itemCls}`;
    }

    onKeyDown(keyEvent) {
        const me = this,
            ownerCmp = me.ownerCmp,
            activeItem = me.activeItem,
            firstItem = me.target.querySelector(me.itemSelector),
            keyName = keyEvent.key.trim() || keyEvent.code,
            composedKeyName = `${keyEvent.ctrlKey ? 'CTRL+' : ''}${keyEvent.shiftKey ? 'SHIFT+' : ''}${keyEvent.altKey ? 'ALT+' : ''}${keyName}`;

        if (firstItem && me.target.offsetParent) {
            // Detect whether the navigable items flow inline or downwards.
            if (me.inlineFlow == null) {
                let itemContainer = firstItem.parentNode,
                    itemPositionStyle = DomHelper.getStyleValue(firstItem, 'position'),
                    itemDisplayStyle = DomHelper.getStyleValue(firstItem, 'display'),
                    itemFloatStyle = DomHelper.getStyleValue(firstItem, 'float');

                // This is how we know that RIGHT and LEFT arrow should be used for next and previous.
                // If inlineFlow is false, we use UP and DOWN. Consider tabs in a tab bar.
                me.inlineFlow = (
                    itemPositionStyle === 'absolute' ||
                    itemDisplayStyle === 'inline' || itemDisplayStyle === 'inline-block' ||
                    itemFloatStyle === 'left' || itemFloatStyle === 'right' ||
                    (DomHelper.getStyleValue(itemContainer, 'display') === 'flex' && DomHelper.getStyleValue(itemContainer, 'flex-direction') === 'row')
                );

                if (me.inlineFlow) {
                    me.prevKey = 'ArrowLeft';
                    me.nextKey = 'ArrowRight';
                }
                else {
                    me.prevKey = 'ArrowUp';
                    me.nextKey = 'ArrowDown';
                }
            }

            // Feed the key event through our configured processor, process the event that returns if any.
            if (me.processEvent) {
                keyEvent = me.processEvent.call(ownerCmp, keyEvent);
                if (!keyEvent) {
                    return;
                }
            }

            if (activeItem) {
                switch (keyName) {
                    case me.prevKey:
                        if (me.disabled || keyEvent.ctrlKey && !me.allowCtrlKey) {
                            return;
                        }
                        if (keyEvent.shiftKey && !me.allowShiftKey) {
                            return;
                        }
                        keyEvent.preventDefault();
                        me.navigatePrevious(keyEvent);
                        break;
                    case me.nextKey:
                        if (me.disabled || keyEvent.ctrlKey && !me.allowCtrlKey) {
                            return;
                        }
                        if (keyEvent.shiftKey && !me.allowShiftKey) {
                            return;
                        }
                        keyEvent.preventDefault();
                        me.navigateNext(keyEvent);
                        break;
                    default:
                        let keyHandler = me.keys && me.keys[composedKeyName];

                        if (keyHandler && !me.disabled) {
                            me.callback(keyHandler, me.thisObj || ownerCmp, [keyEvent]);
                        }
                        // Note that even if this.disabled, the ownerCmp will expect
                        // to have its key down hander invoked.
                        else if (ownerCmp.onInternalKeyDown) {
                            ownerCmp.onInternalKeyDown(keyEvent);
                        }
                }
            }
            else {
                me.activeItem = me.previousActiveItem || me.getDefaultNavigationItem(keyEvent);
            }

            if (me.activeItem !== activeItem) {
                /**
                 * Fired when a user gesture causes the active item to change _or become `null`_.
                 * @event navigate
                 * @param {Event} event The browser event which instigated navigation. May be a click or key or focus move event.
                 * @param {HTMLElement} item The newly active item, or `null` if focus moved out.
                 */
                me.trigger('navigate', {
                    event : keyEvent,
                    item  : me.activeItem
                });
            }
        }
    }

    getDefaultNavigationItem(keyEvent) {
        const { target, itemSelector, prevKey,  nextKey } = this;

        // Navigating backwards from after the component, we default to last item
        if (target.compareDocumentPosition(keyEvent.target) & 4 && keyEvent.key === prevKey) {
            return target.querySelector(`${itemSelector}:last-of-type`);
        }
        // Navigating forwards from before the component we default to the first item
        if (target.compareDocumentPosition(keyEvent.target) & 2 && keyEvent.key === nextKey) {
            return target.querySelector(`${itemSelector}`);
        }
    }

    navigatePrevious(keyEvent) {
        const me = this,
            previous = me.previous;

        keyEvent.preventDefault();
        if (previous) {
            me.ownerCmp.scrollable.scrollIntoView(previous, { animate : 50 }).then(() => {
                me.activeItem = previous;
                me.trigger('navigate', {
                    event : keyEvent,
                    item  : me.activeItem
                });
            });
        }
    }

    navigateNext(keyEvent) {
        const me = this,
            next = me.next;

        keyEvent.preventDefault();
        if (next) {
            me.ownerCmp.scrollable.scrollIntoView(next, { animate : 50 }).then(() => {
                me.activeItem = next;
                me.trigger('navigate', {
                    event : keyEvent,
                    item  : me.activeItem
                });
            });
        }
    }

    get owner() {
        return this.ownerCmp;
    }

    get previous() {
        const treeWalker = this.treeWalker;

        treeWalker.currentNode = this.activeItem;
        treeWalker.previousNode();
        if (treeWalker.currentNode !== this.activeItem) {
            return treeWalker.currentNode;
        }
    }

    get next() {
        const treeWalker = this.treeWalker;

        treeWalker.currentNode = this.activeItem;
        treeWalker.nextNode();
        if (treeWalker.currentNode !== this.activeItem) {
            return treeWalker.currentNode;
        }
    }

    onTargetFocusIn(focusInEvent) {
        if (focusInEvent.target.matches(this.itemSelector)) {
            // We may need to know this in downstream code.
            // for example set activeItem must not scrollintoView on click
            this.skipScrollIntoView = true;

            this.activeItem = focusInEvent.target;

            this.skipScrollIntoView = false;
        }
    }

    onTargetFocusOut(focusOutEvent) {
        if (!focusOutEvent.relatedTarget || !this.target.contains(focusOutEvent.relatedTarget)) {
            this.activeItem = null;
        }
    }

    onTargetMouseDown(mousedownEvent) {
        // Mousedown is the focus gesture.
        // This holds true even on touch platforms
        // where the mousedown event is synthesized -
        // preventing default prevents focus on the upcoming touchend.
        if (this.ownerCmp.itemsFocusable === false) {
            this.onFocusGesture(mousedownEvent);
        }
        else {
            const target = mousedownEvent.target.closest(this.itemSelector);

            if (target) {
                event.preventDefault();
                DomHelper.focusWithoutScrolling(target);
            }
        }
    }

    onTargetClick(clickEvent) {
        const me = this;

        if (me.disabled) {
            return;
        }

        // We may need to know this in downstream code.
        // for example set activeItem must not scrollintoView on click
        me.skipScrollIntoView = true;

        // ownerCmp's preprocessing of any navigate event.
        if (me.processEvent) {
            clickEvent = me.processEvent.call(me.ownerCmp, clickEvent);
        }

        if (clickEvent) {
            me.activeItem = clickEvent.target.closest(me.itemSelector);
            me.trigger('navigate', {
                event : clickEvent,
                item  : me.activeItem
            });
        }
        me.skipScrollIntoView = false;
    }

    // We have to prevent focus moving from eg, an input field when we mousedown
    // or touchtap a non focusable item when ownerCmp has itemsFocusable: false.
    // If the event was not on an item, we must allow it through to allow Lists
    // to contain other widgets.
    onFocusGesture(event) {
        if (event.target === this.ownerCmp.contentElement || (event.target.closest(this.itemSelector) && this.ownerCmp.itemsFocusable === false)) {
            event.preventDefault();
        }
    }

    acceptNode(node) {
        return node.offsetParent && node.matches && node.matches(this.itemSelector) ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_SKIP;
    }

    get activeItem() {
        return this._activeItem;
    }
    set activeItem(activeItem) {
        const
            me                = this,
            { ownerCmp }      = me,
            currentActiveItem = me.activeItem,
            isActive          = currentActiveItem && me.target.contains(currentActiveItem);

        if (isActive) {
            me.previousActiveItem = currentActiveItem;
        }

        // This may be set to null on focusout of the target element.
        // Cannot use truthiness test because index zero may be passed.
        if (activeItem != null) {
            // List and Menu's getItem API allows number, or node or record or record id to be passed
            if (ownerCmp.getItem) {
                activeItem = me.ownerCmp.getItem(activeItem);
            }

            // Unable to find an item to activate so do nothing.
            if (!activeItem) {
                return;
            }

            // If the user was able to click the event, they will not expect it to attempt to scroll.
            if (!me.skipScrollIntoView) {
                ownerCmp.scrollable.scrollIntoView(activeItem, {
                    block  : 'nearest',
                    // If we are being called in response to focus movement, it will already be the document.activeElement
                    // so in that case, behave is if we were in non-focusing mode, and just add the focused class.
                    focus  : DomHelper.isFocusable(activeItem) && activeItem !== document.activeElement,
                    silent : me.scrollSilently
                });
            }

            // No change in active item, do nothing after we've ensured it's fully in view.
            if (activeItem === currentActiveItem && isActive) {
                return;
            }

            if (currentActiveItem) {
                me.activeItem.classList.remove(me.focusCls);
            }

            activeItem.classList.add(me.focusCls);
            me._activeItem = activeItem;
        }
        else {
            if (currentActiveItem) {
                currentActiveItem.classList.remove(me.focusCls);

                // If we are clearing the activeItem, and it's focused, keep focus
                // close by actively reverting it.
                if (currentActiveItem === document.activeElement) {
                    me.ownerCmp.revertFocus();
                }
            }
            me._activeItem = null;
        }
    }

    get target() {
        return this._target;
    }
    set target(target) {
        const me = this,
            listeners = {
                element   : target,
                thisObj   : me,
                focusin   : 'onTargetFocusIn',
                focusout  : 'onTargetFocusOut',
                mousedown : 'onTargetMouseDown',
                click     : 'onTargetClick'
            };

        //<debug>
        if (!me.itemSelector) {
            throw new Error('Element Navigator must be configured with an itemSelector');
        }
        if (!me.ownerCmp) {
            throw new Error('Element Navigator must be configured with an ownerCmp');
        }
        //</debug>

        me._target = target;

        if (!me.hasOwnProperty('acceptNode')) {
            me.acceptNode = me.acceptNode.bind(me);
            // https://github.com/webcomponents/webcomponentsjs/issues/556
            // Work around Internet Explorer wanting a function instead of an object.
            // IE also *requires* this argument where other browsers don't.
            me.acceptNode.acceptNode = me.acceptNode;
        }

        EventHelper.on(listeners);

        // This Navigator object acts as the filter for the TreeWalker. We must implement acceptNode(node)
        me.treeWalker = document.createTreeWalker(target, NodeFilter.SHOW_ELEMENT, me.acceptNode, false);

        // If we were not configured with an outside key event provider (think the input field providing UP/DOWN keys for the dropdown)
        // then use the target element as the source.
        if (!me.keyEventTarget) {
            me.keyEventTarget = target;
        }

        // If the activeItem gets removed, we must know, and deactivate.
        (me.targetMutationMonitor = new MutationObserver(me.onTargetChildListChange.bind(me))).observe(target, {
            childList : true
        });
    }

    onTargetChildListChange() {
        const activeItem = this.activeItem;

        // On DOM mutation, if the activeItem got changed, pull the one with the same ID out again.
        if (activeItem && !this.target.contains(activeItem)) {
            this.activeItem = this.target.querySelector(`${this.itemSelector}[data-id="${activeItem.dataset.id}"]`);
        }
    }

    get keyEventTarget() {
        return this._keyEventTarget;
    }

    set keyEventTarget(keyEventTarget) {
        this._keyEventTarget = keyEventTarget;

        EventHelper.on({
            element : keyEventTarget,
            keydown : 'onKeyDown',
            thisObj : this
        });
    }
}
