import Widget from './Widget.js';
import Rectangle from '../helper/util/Rectangle.js';
import BrowserHelper from '../helper/BrowserHelper.js';
import EventHelper from '../helper/EventHelper.js';
import IdHelper from '../helper/IdHelper.js';
import DomHelper from '../helper/DomHelper.js';
import GlobalEvents from '../GlobalEvents.js';
import ObjectHelper from '../helper/ObjectHelper.js';

let lastTouchTime = 0;

const hasRipple = w => w.ripple,
    Ripple = window.Ripple = new (
        class Ripple extends Widget {
            static get defaultConfig() {
                return {
                    id          : 'bryntum-ripple',
                    old_element : {
                        children : [{
                            className : 'b-ripple-inner',
                            reference : 'rippleElement'
                        }]
                    },

                    element : {
                        children : [{
                            tag       : 'svg',
                            class     : 'b-ripple-inner',
                            reference : 'rippleElement',
                            ns        : 'http://www.w3.org/2000/svg',
                            version   : '1.1',
                            viewBox   : '0 0 100 100',
                            children  : [{
                                reference : 'circleElement',
                                tag       : 'circle',
                                cx        : '0',
                                cy        : '0',
                                r         : 10
                            }]
                        }]
                    },

                    floating : true,

                    hideAnimation : false,

                    showAnimation : false,

                    scrollAction : 'realign',

                    color : 'rgba(0,0,0,.3)',

                    startRadius : 10,

                    radius : 100
                };
            }

            static get $name() {
                return 'Ripple';
            }

            afterConstruct() {
                super.afterConstruct();

                EventHelper.on({
                    element          : document,
                    DOMContentLoaded : 'doEnableDisable',
                    thisObj          : this
                });
                GlobalEvents.on({
                    theme   : 'doEnableDisable',
                    thisObj : this
                });

                // When Edge gets here in tests document is already ready and `doEnableDisable()` was not called,
                // call it directly
                if (BrowserHelper.isEdge && (document.readyState === 'complete' || document.readyState === 'interactive')) {
                    this.doEnableDisable();
                }
            }

            doEnableDisable() {
                const me = this;

                me.show();

                const rippleAnimation = DomHelper.getStyleValue(me.circleElement, 'animationName');

                me.hide();

                // If our theme supports ripples, add our listeners
                if (rippleAnimation && rippleAnimation !== 'none') {
                    me.listenerDetacher = EventHelper.on({
                        // Trap all mousedowns and see if the encapsulating Component is configured to ripple
                        mousedown : {
                            element : document,
                            capture : true,
                            handler : 'onDocumentMousedown'
                        },
                        touchstart : {
                            element : document,
                            capture : true,
                            handler : 'onDocumentTouchStart'
                        },
                        // Hide at the end of the ripple
                        animationend : {
                            element : me.circleElement,
                            handler : 'onAnimationEnd'
                        },
                        thisObj : me
                    });
                }
                // If not, remove them.
                else {
                    me.listenerDetacher && me.listenerDetacher();
                }
            }

            onDocumentTouchStart(event) {
                lastTouchTime = performance.now();
                this.handleTriggerEvent(event);
            }

            onDocumentMousedown(event) {
                // We need to prevent a touchend->mousedown simulated mousedown from triggering a ripple.
                // https://developer.mozilla.org/en-US/docs/Web/API/Touch_events/Supporting_both_TouchEvent_and_MouseEvent
                if (performance.now() - lastTouchTime > 200) {
                    this.handleTriggerEvent(event);
                }
            }

            handleTriggerEvent(event) {
                const targetWidget = IdHelper.fromElement(event.target, hasRipple);

                if (targetWidget) {
                    const
                        rippleCfg = targetWidget.ripple,
                        target    = rippleCfg.delegate
                            ? event.target.closest(rippleCfg.delegate)
                            : (targetWidget.focusElement || targetWidget.element);

                    if (target) {
                        const ripple = ObjectHelper.assign({
                            event,
                            target,
                            radius : this.radius
                        }, rippleCfg);

                        // The clip option is specified as a string property name or delegate
                        if (typeof ripple.clip === 'string') {
                            ripple.clip = targetWidget[ripple.clip] || event.target.closest(ripple.clip);

                            // Not inside an instance of the clip delegate, then no ripple
                            if (!ripple.clip) {
                                return;
                            }
                        }
                        this.ripple(ripple);
                    }
                }
            }

            ripple({
                event,
                point = EventHelper.getClientPoint(event),
                target = event.target,
                clip = target,
                radius = this.radius,
                color = this.color
            }) {
                this.clip = clip;

                //<debug>
                if (clip.nodeType !== 1) {
                    throw new Error('Ripple\'s clip option must be a constraining HTMLElement');
                }
                //</debug>
                clip = Rectangle.from(clip, null, true);

                const
                    me            = this,
                    centreDelta   = clip.getDelta(point),
                    rippleStyle   = me.rippleElement.style,
                    circleElement = me.circleElement;

                me.hide();
                me.alignTo(clip);
                rippleStyle.transform = `translateX(${centreDelta[0]}px) translateY(${centreDelta[1]}px)`;
                rippleStyle.height = rippleStyle.width = `${radius}px`;
                circleElement.setAttribute('r', radius);
                circleElement.setAttribute('fill', color);
                me.show();

                // Push binding to the scroll position out until the next animation frame.
                // This is in case the calling code is going to cause a scroll.
                // Mousedown is a focusing gesture which may cause a scroll
                // to fire as the target element moves into view.
                me.requestAnimationFrame(() => {
                    document.addEventListener('scroll', me.callRealign, true);
                    me.documentScrollListener = true;
                });
            }

            alignTo(clip) {
                if (clip.nodeType === 1) {
                    clip = Rectangle.from(clip, null, true);
                }
                const
                    me                      = this,
                    { x, y, width, height } = clip;

                me.x = x;
                me.y = y;
                me.height = height;
                me.width = width;
            }

            // When fully expanded, it's all over.
            onAnimationEnd(event) {
                if (event.animationName === 'b-ripple-expand') {
                    this.hide();
                }
            }

            realign() {
                if (this.isVisible) {
                    this.alignTo(this.clip);
                }
            }
        }
    )();

export { Ripple as default };
