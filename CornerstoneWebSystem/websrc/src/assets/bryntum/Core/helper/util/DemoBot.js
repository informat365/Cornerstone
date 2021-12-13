import DomHelper from '../DomHelper.js';
import Rectangle from './Rectangle.js';
import IdHelper from '../IdHelper.js';
import Events from '../../mixin/Events.js';
import EventHelper from '../../helper/EventHelper.js';
import Delayable from '../../mixin/Delayable.js';

const knownProps = [
    'action',
    'target',
    'to',
    'deltaX',
    'deltaY',
    'x',
    'y',
    'text'
];

export default class DemoBot extends Events(Delayable()) {
    static get defaultConfig() {
        return {
            repeat          : true,
            outerElement    : document.body,
            callOnFunctions : true
        };
    }

    // expects an outer element (grid.element/scheduler.element) and an array of steps similar to chain steps in siesta
    construct(config) {
        super.construct(config);

        const me = this;

        if (me.widget) {
            me.outerElement = me.widget.element;
            me.widget.playingDemo = true;
        }

        EventHelper.playingDemo = true;

        Object.assign(me, {
            prevTarget  : null,
            currentStep : 0,
            mouse       : DomHelper.createElement({
                parent    : me.outerElement,
                tag       : 'div',
                className : 'simulated-mouse'
            }),
            timeoutId        : null,
            innerIntervalId  : null,
            mouseOutElements : []
        });

        me.intervalId = me.setInterval(me.nextStep.bind(me), 1000);

        me.outerElement.classList.add('b-playing-demo');

        me.outerElement.addEventListener('click', event => {
            if (event.isTrusted) {
                me.abort();
            }
        });
    }

    doDestroy() {
        this.abort();
    }

    // stops the bot
    abort(atEnd = false) {
        const me = this;

        me.mouse.style.top = '-100px';
        me.clearInterval(me.intervalId);
        me.timeoutId && me.clearTimeout(me.timeoutId);
        me.innerIntervalId && me.clearInterval(me.innerIntervalId);

        me.outerElement.classList.remove('b-playing-demo');
        if (me.widget) {
            me.widget.playingDemo = false;
        }

        EventHelper.playingDemo = false;

        me.trigger(atEnd ? 'done' : 'abort');
    }

    // triggers a synthetic event
    triggerEvent(element, type, data) {
        if (!element) return null;

        let event;

        if (type.startsWith('mouse')) {
            const box = this.mouse.getBoundingClientRect();

            event = new MouseEvent(type, Object.assign({
                view       : window,
                bubbles    : true,
                cancelable : true,
                clientX    : box.left,
                clientY    : box.top
            }, data || {}));
        }
        else {
            event = document.createEvent('Event');
            event.initEvent(type, true, false);
        }

        element.dispatchEvent(event);

        return event;
    }

    // moves mouse to target in 10 steps, with animated transition between steps
    handleMouseMove(step, target) {
        const me    = this,
            mouse = me.mouse;

        mouse.classList.add('quick');
        if (me.mouseDown) mouse.classList.add('drag');

        let mouseBox    = Rectangle.from(mouse, me.outerElement),
            x           = mouseBox.x,
            y           = mouseBox.y,
            deltaX      = 0,
            deltaY      = 0;

        if (step.to) {
            if (typeof step.to === 'string') {
                const toElement = me.outerElement.querySelector(step.to);
                if (toElement) {
                    const rect = Rectangle.from(toElement, me.outerElement),
                        toX  = (rect.x + rect.width / 2),
                        toY  = (rect.y + rect.height / 2);
                    deltaX = (toX - x) / 10;
                    deltaY = (toY - y) / 10;
                }
            }
            else if (step.to.x) {
                deltaX = (step.to.x - x) / 10;
            }
            else {
                deltaX = step.to[0] / 10;
                deltaY = step.to[1] / 10;
            }
        }
        else if (step.deltaX) {
            deltaX = step.deltaX / 10;
        }
        else if (step.x) {
            deltaX = (step.x - x) / 10;
        }

        if (step.deltaY) {
            deltaY = step.deltaY / 10;
        }

        let i = 0;

        me.innerIntervalId = me.setInterval(() => {
            // Only move mouse if in view and not scrolling
            if (me.shouldPause) {
                return;
            }

            if (i++ === 9) {
                clearInterval(me.innerIntervalId);
                if (step.then) {
                    step.then();
                }
            }

            const mouseX = x + deltaX * i,
                mouseY = y + deltaY * i;

            // Move mouse there also
            mouse.style.left = mouseX + 'px';
            mouse.style.top = mouseY + 'px';

            const
                mouseBounds = mouse.getBoundingClientRect(),
                clientX     = mouseBounds.left,
                clientY     = mouseBounds.top,
                eventTarget = document.elementFromPoint(clientX, clientY);

            if (eventTarget !== me.prevTarget) {
                if (me.prevTarget) {
                    me.mouseOutElements.push(me.prevTarget);
                    if (!DomHelper.isDescendant(me.mouseOutElements[0], eventTarget)) {
                        me.mouseOutElements.forEach(element => me.triggerEvent(element, 'mouseout'));
                        me.mouseOutElements.length = 0;
                    }
                }
                me.prevTarget = eventTarget;
                me.triggerEvent(eventTarget, 'mouseover');
            }

            me.triggerEvent(eventTarget, step.action, {
                clientX,
                clientY
            });
        }, 50);
    }

    // target can be a string selector, a function or blank to use last target or outerElement if first time
    getTarget(step) {
        const me     = this,
            target = step.target;

        if (!target) {
            return me.prevTarget || me.outerElement;
        }

        if (typeof target === 'function') {
            return target(step);
        }

        return document.querySelector(target);
    }

    // action can be a function, a string or extracted from a property by scanning for unknown names
    normalizeStep(step) {
        if (step.action) {
            if (typeof step.action === 'function') {
                return step.action(step);
            }
            return step;
        }

        if (typeof step === 'function') {
            step();
            return step;
        }

        // try to find action among properties
        for (let prop in step) {
            if (step.hasOwnProperty(prop) && !knownProps.includes(prop)) {
                step.action = prop.toLowerCase();
                step.to = step[prop];
            }
        }

        if (!step.target && (typeof step.to === 'string' || typeof step.to === 'function')) step.target = step.to;

        return step;
    }

    get isScrolling() {
        const me       = this,
            box      = me.outerElement.getBoundingClientRect(),
            scrolled = me.lastTop && box.top !== me.lastTop;

        me.lastTop = box.top;

        return scrolled;
    }

    get isInView() {
        const box = this.outerElement.getBoundingClientRect();
        return (box.top < window.innerHeight && box.bottom > 0);
    }

    get shouldPause() {
        return !this.isInView || this.isScrolling || document.hidden || !document.hasFocus();
    }

    // process the next step
    nextStep() {
        const me = this;

        // Only perform step if in view and not scrolling
        if (me.shouldPause) {
            return;
        }

        if (me.currentStep === me.steps.length) {
            if (me.repeat) {
                me.currentStep = 0;
            }
            else {
                return me.abort(true);
            }
        }

        // First step, signal to let demo initialize stuff
        if (me.currentStep === 0) {
            me.trigger('initialize');
        }

        const mouse  = me.mouse,
            step   = me.normalizeStep(me.steps[me.currentStep++]),
            target = me.getTarget(step),
            action = step.action;

        if (target && action) {
            mouse.className = 'simulated-mouse';

            if (action === 'mousemove') {
                me.handleMouseMove(step, target);
            }
            else {
                // First move mouse into position
                if (target !== me.prevTarget) {
                    const rect = Rectangle.from(target, me.outerElement);
                    mouse.style.left = (rect.x + rect.width / 2) + 'px';
                    mouse.style.top = (rect.y + rect.height / 2) + 'px';
                }

                if (action === 'mousedown') {
                    me.mouseDown = true;
                }

                if (action === 'mouseup') {
                    me.mouseDown = false;
                }

                // Then trigger action
                me.timeoutId = me.setTimeout(() => {
                    me.prevTarget = target;

                    // Animate click etc.
                    mouse.classList.add(action);

                    if (action === 'type') {
                        const field = IdHelper.fromElement(target),
                            parts = step.text.split('|');

                        field.value = parts[parts.length === 1 || field.value != parts[0] ? 0 : 1];
                    }
                    else {
                        me.triggerEvent(target, action);
                    }

                }, action === 'type' ? 100 : 550);
            }
        }
    }
}
