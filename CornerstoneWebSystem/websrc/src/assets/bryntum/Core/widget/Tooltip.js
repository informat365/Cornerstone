import Widget from './Widget.js';
import Popup from './Popup.js';
import Rectangle from '../helper/util/Rectangle.js';
import Point from '../helper/util/Point.js';
import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';
import EventHelper from '../helper/EventHelper.js';
import IdHelper from '../helper/IdHelper.js';
import StringHelper from '../helper/StringHelper.js';
import DomHelper from '../helper/DomHelper.js';

const realignTransitions = {
    left      : true,
    right     : true,
    top       : true,
    bottom    : true,
    transform : true
};

/**
 * @module Core/widget/Tooltip
 */

/**
 * Tooltip. Easiest way of assigning a tooltip to a widget is by setting {@link Core.widget.Widget#config-tooltip}, see example below.
 *
 * ```javascript
 * new Button {
 *     text    : 'Hover me',
 *     tooltip : 'Click me and you wont believe what happens next'
 * });
 * ```
 *
 * By default, tooltips of widgets use a singleton Tooltip instance which may be accessed from the
 * `{@link Core.widget.Widget Widget}` class under the name `Widget.tooltip`.
 * This is configured according to the config object on pointer over.
 *
 * To request a separate instance be created just for this widget, add `newInstance : true`
 * to the configuration:
 *
 * ```javascript
 * new Button {
 *     text    : 'Hover me',
 *     tooltip : {
 *         html        : 'Click me and you wont believe what happens next',
 *         newInstance : true
 *     }
 * });
 * ```
 *
 * You can ask for the singleton instance to display configured tips for your own DOM structure using
 * `data-btip` element attributes:
 *
 * ```html
 * <button class="my-button" data-btip="Contextual help for my button" data-btip-scroll-action="realign">Hover me</button>
 * ```
 *
 * ## Showing async content
 * To create async tooltip and show {@link #config-loadingMsg}, set {@link #property-html} to `false`
 *
 * Ensure that the {@link #property-activeTarget} is the same when the data arrives.
 *
 * ```javascript
 * new Tooltip({
 *     listeners : {
 *         beforeShow : ({ source : tip }) => {
 *             const showingFor = tip.activeTarget;
 * 
 *             tip.html = false;
 *             AjaxHelper.get('someurl').then(response => {
 *                 // Still visible for the same triggering element by the time the data arrives...
 *                 if (tip.isVisible && tip.activeTarget === showingFor) {
 *                     tip.html = 'Done!';
 *                 }
 *             });
 *         }
 *     }
 * });
 * ```
 *
 * @extends Core/widget/Widget
 * @classType tooltip
 * @externalexample widget/Tooltip.js
 */
export default class Tooltip extends Popup {
    //region Default config
    static get $name() {
        return 'Tooltip';
    }

    static get defaultConfig() {
        return {
            /**
             * Horizontal offset from mouse when {@link #config-anchorToTarget} is `false`
             * @config {Number}
             * @default
             */
            mouseOffsetX : 15,

            /**
             * Vertical offset from mouse when {@link #config-anchorToTarget} is `false`
             * @config {Number}
             * @default
             */
            mouseOffsetY : 15,

            floating : true,

            /**
             * Set to a function that returns a string to update tooltips contents. Called on each mouse move event.
             * @config {Function}
             */
            getHtml : null,

            /**
             * DOM element to attach tooltip to. By default, the mouse entering this element will kick off a timer
             * (see {@link #config-hoverDelay}) to show itself.
             *
             * If the {@link #config-forSelector} is specified, then mouse entering matching elements within the `forElement`
             * will trigger the show timer to start.
             *
             * Note that when moving from matching element to matching element within the `forElement`, the tooltip
             * will remain visible for {@link #config-hideDelay} milliseconds after exiting one element, so that rapidly
             * entering another matching element will not cause hide+show flicker. To prevent this behaviour configure
             * with `hideDelay: 0`.
             * @config {HTMLElement}
             */
            forElement : null,

            trackMouse : null,

            /**
             * Shows tooltip for all elements that match this selector within the {@link #config-forElement}.
             * @config {String}
             */
            forSelector : null,

            /**
             * By default, when moving rapidly from target to target, if, when mouseing over
             * a new target, the tip is still visible, the tooltip does not hide, it remains
             * visible, but updates its content however it is configured to do so.
             *
             * Configure `hideOnDelegateChange : true` to have the tip hide, and then trigger
             * a new show delay upon entry of a new target while still visible.
             * @config {Boolean}
             * @default false
             */
            hideOnDelegateChange : null,

            /**
             * Set to true to anchor tooltip to the triggering target. If set to `false`, the tooltip
             * will align to the mouse position. When set to `false`, it will also set `anchor: false`
             * to hide anchor arrow.
             * @config {Boolean}
             * @default true
             */
            anchorToTarget : true,

            /**
             * Show on hover
             * @config {Boolean}
             * @default
             */
            showOnHover : false,

            /**
             * The amount of time to hover before showing
             * @config {Number}
             * @default
             */
            hoverDelay : 500,

            /**
             * Show immediately when created
             * @config {Boolean}
             * @default
             */
            autoShow : false,

            /**
             * The time (in milliseconds) that the Tooltip should stay visible for when it shows over its
             * target. If the tooltip is anchored to its target, then moving the mouse during this time
             * resets the timer so that the tooltip will remain visible.
             *
             * Defaults to `0` which means the Tooltip will persist until the mouse leaves the target.
             * @config {Number}
             * @default
             */
            dismissDelay : 0,

            /**
             * The time (in milliseconds) for which the Tooltip remains visible when the mouse leaves the target.
             *
             * May be configured as `false` to persist visible after the mouse exits the target element. Configure it
             * as 0 to always retrigger `hoverDelay` even when moving mouse inside `fromElement`
             * @config {Number}
             * @default
             */
            hideDelay : 500,

            /**
             * Loading message for async tooltips.
             * @config {String}
             * @default
             */
            loadingMsg : 'Loading...',

            /**
             * Keep the tooltip open if user hovers it
             * @config {Boolean}
             * @default
             */
            allowOver : false,

            /**
             * Specify `true` for a tooltip used to show unformatted text. It will apply the `b-tooltip-text-content`
             * class which specifies a default max width that makes long text more readable.
             */
            textContent : false,

            anchor   : true,
            align    : 'b-t',
            axisLock : true,

            showAnimation : null,
            hideAnimation : null
        };
    }

    //endregion

    //region Events

    /**
     * Triggered when a mouseover event is detected on a potential target element.
     * Return false to prevent the action
     * @event pointerOver
     * @param {Core.widget.Tooltip} sourceThe tooltip instance.
     * @param {Event} event The mouseover event.
     */

    //endregion

    //region Properties

    /**
     * The HTML element that triggered this Tooltip to show
     * @member {HTMLElement} activeTarget
     */

    //endregion

    //region Init & destroy

    afterConstruct() {
        const me = this,
            { forSelector } = me;

        if (forSelector) {
            me.showOnHover = true;
            if (!me.forElement) {
                me.trackMouse = true;
                me.forElement = document.body;
            }
        }

        super.afterConstruct();

        // There's a triggering element, and we're showing on hover, add the mouse listeners
        if (me.forElement && me.showOnHover) {
            EventHelper.on({
                element       : me.forElement,
                mouseover     : 'internalOnPointerOver',
                mouseout      : 'onPointerOut',
                transitionend : 'onTransitionEnd',
                thisObj       : me
            });
        }

        if (me.autoShow) {
            me.show();
        }

        if (me.allowOver) {
            EventHelper.on({
                element    : me.element,
                mouseenter : 'onOwnElementMouseEnter',
                mouseleave : 'onPointerOut',
                thisObj    : me
            });
        }
    }

    get focusElement() {
        const result = super.focusElement;
        if (result !== this.element) {
            return result;
        }
    }

    get anchorToTarget() {
        return this._anchorToTarget;
    }

    set anchorToTarget(anchorToTarget) {
        this._anchorToTarget = anchorToTarget;
        if (!anchorToTarget) this.anchor = false;
    }

    set trackMouse(trackMouse) {
        this._trackMouse = trackMouse;
    }

    get trackMouse() {
        // We do not track the mouse if we are anchored to (aligned to) the target
        return this._trackMouse && !this.anchorToTarget;
    }

    //endregion

    //region Hovering, show and hide

    onDocumentMouseDown({ event }) {
        const
            me = this,
            { pointerEvent } = me;

        // If it's a tap that is caused by the touch that was converted into a mousover we should not hide.
        // That is if it's a touch and at the same place and within 500ms
        if (pointerEvent && DomHelper.isTouchEvent) {
            if (event.pageX === pointerEvent.pageX && event.pageY === pointerEvent.pageY && me.activeTarget.contains(event.target) && (performance.now() - pointerEvent.timeStamp < 500)) {
                return;
            }
        }
        super.onDocumentMouseDown({ event });
    }

    internalOnPointerOver(event) {
        const me = this,
            forElement = me.forElement,
            forSelector = me.forSelector,
            activeTarget = me.activeTarget;
        let newTarget;

        if (me.disabled) {
            return;
        }

        // If the mouse moves over this tooltip, it is theoretically a mouseout of its
        // forElement, but allowOver lets us tolerate this ane remain visible.
        if (me.allowOver && me.element.contains(event.target)) {
            return;
        }

        // There's been a mouseover. If we have a forSelector, we have to check
        // if it's an enter of a matching child
        if (forSelector) {
            // Moving inside a forSelector matching element
            if (activeTarget && activeTarget.contains(event.target) && activeTarget.contains(event.relatedTarget)) {
                return;
            }
            newTarget = event.target.closest(forSelector);

            // Mouseovers while within a target do nothing
            if (newTarget && event.relatedTarget && event.relatedTarget.closest(forSelector) === newTarget) {
                return;
            }
        }
        // There's no forSelector, so check if we moved from outside the target
        else if (!forElement.contains(event.relatedTarget)) {
            newTarget = forElement;
        }
        // Mouseover caused by moving from child to child inside the target
        else {
            return;
        }

        // If pointer entered the target or a forSelector child, then show.
        if (newTarget) {
            me.handleForElementOver(event, newTarget);
        }
        // If over a non-forSelector child, behave as in forElement out
        else if (activeTarget) {
            me.handleForElementOut();
        }
    }

    // Handle a transitioned reposition when the activeTarget moved beneath the pointer.
    // When it comes to an end, if the mouseout has not hidden, then realign at the new position
    // if the activeTarget is still beneath the pointer.
    onTransitionEnd(event) {
        const me = this,
            currOver = Tooltip.currentOverElement;

        if (realignTransitions[event.propertyName]) {
            // Don't realign if the mouse is over this, and is allowed to be over this
            // If user is interacting with this Toolltip, they won't expect it to move.
            if (me.allowOver && me.element.contains(currOver)) {
                return;
            }

            // If we are still visible, and mouse is still over the activeTarget, realign
            if (me.isVisible && me.activeTarget.contains(currOver) && !me.trackMouse) {
                me.realign();
            }
        }
    }

    handleForElementOver(event, newTarget) {
        const me = this;

        // Vetoed, then behave as is a targetout
        if (me.trigger('pointerOver', { event, target : newTarget }) === false) {
            me.onPointerOut(event);
        }
        else {
            me.pointerEvent = event;
            me.abortDelayedHide();

            // We are over a new target. If we are still visible, we
            // do not want to hide to avoid flickering. But if there is a
            // beforeshow listener which may mutate us, we still have to
            // consult it. If it returns a veto, then we do in fact hide.
            // Under normal circumstances we just alignTo the new target.
            // We must handle the post show tasks like starting the dismiss timer etc.
            if (me.isVisible && !me.hideOnDelegateChange) {
                me.updateActiveTarget(newTarget);
                if (me.trigger('beforeShow') === false) {
                    return me.hide();
                }
                me.alignTo({
                    target  : me.anchorToTarget ? newTarget : new Point(me.pointerEvent.pageX - window.pageXOffset + me.mouseOffsetX, me.pointerEvent.pageY - window.pageYOffset + me.mouseOffsetY),
                    overlap : !(me.anchorToTarget && me.anchor)
                });
                me.trigger('show');
                me.afterShowByTarget();
            }
            else {
                if (me.hideOnDelegateChange) {
                    me.hide();
                }
                me.updateActiveTarget(newTarget);
                me.delayShow(newTarget);
            }
        }
    }

    delayShow(target) {
        const me = this;

        // Caught in an animation - cancel it
        if (me.currentAnimation) {
            me.cancelHideShowAnimation();
            me._hidden = true;
        }

        if (!me.isVisible && !me.hasTimeout('show')) {
            // Allow hoverDelay:0 or rapid movement from delegate to delegate to show immediately
            if (!me.hoverDelay || (me.forSelector && Date.now() - me.lastHidden < me.quickShowInterval)) {
                me.showByTarget(target);
            }
            else {
                // If we're not going to anchor to the hovered element, then we need to keep track
                // of mousemoves until the show happens so we can show where the mouse currently is.
                if (!me.listeningForMouseMove && !me.anchorToTarget) {
                    me.mouseMoveRemover = EventHelper.on({
                        element   : document,
                        mousemove : 'onMouseMove',
                        thisObj   : me
                    });
                }
                // If a tap event triggered, do not wait. Show immediately.
                me.setTimeout(() => me.showByTarget(target), (!me.pointerEvent || me.pointerEvent.type === 'mouseover') ? me.hoverDelay : 0, 'show');
            }
        }
        else if (me.isVisible) {
            me.showByTarget(target);
        }
    }

    showByTarget(target) {
        const me = this;

        if (me.mouseMoveRemover) {
            me.mouseMoveRemover();
            me.mouseMoveRemover = null;
        }

        // Show by the correct thing.
        // If we are not anchored to the target, then it's the current pointer event.
        // Otherwise it's the activeTarget.
        me.showBy({
            target  : me.anchorToTarget ? target : new Point(me.pointerEvent.pageX - window.pageXOffset + me.mouseOffsetX, me.pointerEvent.pageY - window.pageYOffset + me.mouseOffsetY),
            overlap : !(me.anchorToTarget && me.anchor)
        });
    }

    afterShowByTarget() {
        const me = this,
            dismissDelay = me.dismissDelay;

        me.abortDelayedShow();
        if (dismissDelay) {
            me.setTimeout('hide', dismissDelay);
        }
        me.toFront();
    }

    updateActiveTarget(newTarget) {
        const me = this,
            lastTarget = me.activeTarget;

        me.activeTarget = newTarget;

        if (!me.isConfiguring) {
            me.trigger('overtarget', { newTarget, lastTarget });
        }
    }

    onPointerOut(event) {
        const me = this,
            toElement = event.relatedTarget;

        // Edge case: If there is no space to fit the tooltip, and as a result of showing the tooltip - the mouse is over the tooltip
        // Make sure we don't end up in an infinite hide/show loop
        if (me.allowOver && me.element.contains(toElement)) {
            return;
        }

        // If we were in an allowOver situation and exited
        // into the activeTarget, do nothing; in this situation
        // the tip is treated as if it were part of the target.
        if (me.element.contains(event.target) && me.activeTarget && me.activeTarget.contains(toElement)) {
            return;
        }

        // We have exited the active target
        if (me.activeTarget && !me.activeTarget.contains(event.relatedTarget)) {
            me.handleForElementOut();
        }
    }

    handleForElementOut() {
        // Separated from onTargetOut so that subclasses can handle target out in any way.
        const me = this,
            { hideDelay } = me;

        me.abortDelayedShow();

        // Even if there is a hide timer, it's a *dismiss* timer which hides the tip
        // after a hover time. We begin a new delay on target out.
        if (me.isVisible && hideDelay !== false) {
            me.abortDelayedHide();
            if (hideDelay > 0) {
                me.setTimeout('hide', hideDelay);
            }
            else {
                // Hide immediately when configured with `hideDelay: 0`. Used by async cell tooltips that always should
                // retrigger `hoverDelay`, to not spam the backend
                me.hide();
            }
        }
    }

    show() {
        const me = this;

        // If we know what element to show it by, and we are anchoring to it
        // and there's no ambiguity with a selector for sub elements,
        // then show it by our forElement (Unless we're being called from showBy)
        if (me.forElement && me.anchorToTarget && !me.forSelector && !me.inShowBy) {
            me.showByTarget(me.forElement);
        }
        // All we can do is the basic Widget show.
        else {
            if (me.inShowBy) {
                me.y = -10000;
            }
            super.show();
        }

        me.afterShowByTarget();
        // If we've shown, and are tracking the mouse and not anchored to (aligned to) the target, track the mouse
        if (!me.mouseMoveRemover && !me._hidden && me.trackMouse) {
            me.mouseMoveRemover = EventHelper.on({
                element   : document,
                mousemove : 'onMouseMove',
                thisObj   : me
            });
        }
    }

    hide(...args) {
        const me = this;

        if (me.isVisible) {
            me.abortDelayedShow();
            me.abortDelayedHide();

            super.hide(...args);

            me.lastHidden = Date.now();
            me.activeTarget = null;
            if (me.mouseMoveRemover) {
                me.mouseMoveRemover();
                me.mouseMoveRemover = null;
            }

            me.listeningForMouseMove = false;
        }
    }

    abortDelayedShow() {
        const me = this;
        if (me.hasTimeout('show')) {
            me.clearTimeout('show');
            if (me.mouseMoveRemover) {
                me.mouseMoveRemover();
                me.mouseMoveRemover = null;
            }
        }
    }

    /**
     * Stops both timers which may hide this tooltip, the one which counts down from mouseout
     * and the one which counts down from mouseover show for dismissDelay ms
     * @private
     */
    abortDelayedHide()    {
        this.clearTimeout('hide');
    }

    realign(el) {
        const me = this,
            spec = me.lastAlignSpec,
            clippedBy = me.clippedBy;

        // If we are hidden because our align target scrolled, or otherwise
        // moved out of its clipping boundaries, then check if it's moved back in.
        // For example EventDrag might move the element outside of the scheduler
        // SubGrid, which will cause the tip to hide, but then moving it back in
        // must reshow it.
        if (!me.isConfiguring && clippedBy && !me.isVisible && spec.targetHidden) {
            const target = Rectangle.from(spec.target, me.positioned ? me.element.offsetParent : null, !me.positioned),
                clippedTarget = target.intersect(clippedBy);

            // If there is an intersecting Rectangle with the forElement, align
            if (clippedTarget) {
                me.show();
                spec.targetHidden = false;
            }
        }

        super.realign(el);
    }

    alignTo(spec) {
        const me = this;

        if (!me.isVisible) return;

        // getHtml implies update on align.
        // Must update HTML before calculating position
        if (me.getHtml) {
            // If mouse pointer is over this, do not attempt
            // to call the getHtml method.
            if (!(me.pointerEvent && me.element.contains(me.pointerEvent.target))) {
                //<debug>
                if (!(spec.nodeType || spec.target || spec.position || (spec instanceof Point))) {
                    throw new Error('alignTo must be either passed a target to position by, or a position Point to position at');
                }
                //</debug>
                if (spec.nodeType === 1) {
                    spec = {
                        target : spec
                    };
                }
                const xy = (spec instanceof Point) ? spec : spec.position || (spec.target.nodeType === 1 ? Rectangle.from(spec.target) : spec.target);

                // setHtml attempts to realign to previous alignment unless this flag is set
                me.isAligning = true;

                me.html = me.getHtml({
                    tip        : me,
                    element    : me.element,
                    forElement : me.activeTarget,
                    x          : xy.x,
                    y          : xy.y,
                    event      : me.pointerEvent
                });
                me.isAligning = false;
            }
        }

        if (me.isVisible) {
            super.alignTo(spec);
        }
    }

    //endregion

    //region Tooltip contents

    set textContent(value) {
        this._textContent = value;

        this.element.classList[value ? 'add' : 'remove']('b-tooltip-text-content');
    }

    get textContent() {
        return this._textContent;
    }

    set html(html) {
        const me = this;

        // setting to false signals async tooltip, show loading message.
        // TODO: Load masking should be a general Widget ability.
        if (html === false) {
            html = `<div class="b-tooltip-loading"><div class="b-icon b-icon-spinner"></div>${me.L(me.loadingMsg)}</div>`;
        }
        else if (html != null) {
            html = me.L(String(html)); // in case a number was passed in
        }

        // we have something to show...
        if (html) {
            // When setting the html, we must inhibit any configured html getter
            let getHtml = me.getHtml;
            me.getHtml = null;
            super.html = html;
            // Must realign before restoring getHtml, to not get stuck in infinite html setting loop
            if (!me.isAligning) {
                me.realign();
            }
            me.getHtml = getHtml;

            me.trigger('innerHtmlUpdate');
        }
        else { // do not show empty tooltips
            me.hide();
        }
    }

    /**
     * Get/set HTML to display. When specifying HTML, this widget's element will also have `b-html` added to its
     * classList, to allow targeted styling. To create async tooltip and show {@link #config-loadingMsg}, set it to `false`.
     * For example:
     *
     * ```javascript
     * new Tooltip({
     *     listeners : {
     *         beforeShow : ({ source : tip }) => {
     *             tip.html = false;
     *             AjaxHelper.get('someurl').then(response => tip.html = 'Done!');
     *         }
     *     }
     * });
     * ```
     *
     * @property {String}
     * @category DOM
     */
    get html() {
        return super.html;
    }

    //endregion

    //region Events

    /**
     * Mouse move event listener which updates tooltip
     * @private
     */
    onMouseMove(event) {
        const
            me = this,
            x = event.pageX - window.pageXOffset + me.mouseOffsetX,
            y = event.pageY - window.pageYOffset + me.mouseOffsetY,
            // If we are trackMouse: true
            // we must keep out of the way of the mouse by continuing
            // to track if we are on the way out due to a hide timer.
            isHiding = me.hasTimeout('hide'),
            target   = event.target;

        // MouseMove is listened for during the hover show timer wait phase if anchorToTarget is false
        // so that when the timer fires, it can show near the most recent pointer position.
        // It's also listened for after show when we are not anchored to the target and so tracking the mouse.
        me.pointerEvent = event;

        // Check that we are still valid to be visible, and if so, track the mouse.
        if (!me._hidden) {
            // Check whether the element we are over is still a valid delegate matching the forSelector,
            // or it's the tip element, and we're allowOver. If not, we have to hide.
            // nodeType check is for FF on Linux, event.target is sometimes a text node
            if (me.forSelector && !isHiding && target.nodeType === 1 && !target.matches(me.forSelector) && !(me.allowOver && me.element.contains(target))) {
                me.handleForElementOut();
            }
            // If we are not hiding due to moving mouse outside our forElement, tooltip stays visible and optionally realigns based on trackMouse setting.
            else if (!isHiding || me.forElement.contains(target)) {
                // Mousemoves restart the dismiss timer.
                if (me.dismissDelay && !isHiding) {
                    me.setTimeout('hide', me.dismissDelay);
                }

                // If we're not anchoring to the target, track the mouse
                if (me.trackMouse) {
                    me.alignTo({
                        position         : new Point(x, y),
                        ignorePageScroll : true
                    });
                }
            }
        }
    }

    onOwnElementMouseEnter(event) {
        this.abortDelayedHide();
    }
    //endregion
}

EventHelper.on({
    element    : document,
    mouseenter : event => Tooltip.currentOverElement = event.target,
    capture    : true
});

/**
 * Updated dynamically with the current element that the mouse is over. For use when showing a Tooltip
 * from code which is not triggered by a pointer event so that a tooltip can be positioned.
 * @member {HTMLElement} currentOverElement
 * @readonly
 * @static
 */

BryntumWidgetAdapterRegister.register('tooltip', Tooltip);

Widget.tooltip = new Tooltip({
    id          : 'bryntum-tooltip',
    forElement  : window,
    forSelector : '[data-btip]',
    header      : true,  // So that we can set a title
    resetCfg    : {},
    listeners   : {
        // Reconfigure on pointerOver
        pointerOver({ source : me, target }) {
            // Revert last pointerOver config set to initial setting.
            for (const key in me.resetCfg) {
                if (key === 'listeners') {
                    me.un(me.resetCfg[key].set);
                }
                else {
                    if ('was' in me.resetCfg[key]) {
                        me[key] = me.resetCfg[key].was;
                    }
                    else {
                        delete me[key];
                    }
                }
            }
            me.resetCfg = {};

            const forComponent = IdHelper.get(target.id);

            // If it's a component's tooltip, configure from the component,
            // Otherwise gather from the dataset
            const config = forComponent ? forComponent.tipConfig : me.gatherDataConfigs(target.dataset);

            for (const key in config) {
                me.resetCfg[key] = {
                    set : config[key]
                };
                // If no property, the reset must delete it.
                if (me.hasOwnProperty(key)) {
                    me.resetCfg[key].was = me[key];
                }

                if (key === 'listeners') {
                    me.on(config[key]);
                }
                else {
                    me[key] = config[key];
                }
            }
            me.headerElement.style.display = me.title ? '' : 'none';
        }
    },
    gatherDataConfigs(dataset) {
        const
            me = this,
            config = {};

        for (const key in dataset) {
            if (key.startsWith('btip')) {
                if (key.length > 4) {
                    const configProp = StringHelper.lowercaseFirstLetter(key.substr(4)); // Snip off "btip" prefix to convert to property name

                    // If we have a config by the name, set it
                    if (configProp in me.getDefaultConfiguration()) {
                        const value = dataset[key];

                        // gather the found config value
                        config[configProp] = isNaN(value) ? value : parseInt(value, 10);
                    }
                }
                else {
                    config.html = dataset[key];
                }
            }
        }
        return config;
    }
});
