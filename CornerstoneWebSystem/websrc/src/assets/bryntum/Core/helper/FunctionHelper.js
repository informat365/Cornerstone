/**
 * @module Core/helper/FunctionHelper
 */

/**
 * Provides functionality for working with functions
 * @internal
 */
export default class FunctionHelper {
    static curry(func) {
        return function curried(...args) {
            if (args.length >= func.length) {
                return func.apply(this, args);
            }
            else {
                return function(...args2) {
                    return curried.apply(this, args.concat(args2));
                };
            }
        };
    }

    static bindAll(obj) {
        for (let key in obj) {
            if (typeof obj[key] === 'function') {
                obj[key] = obj[key].bind(obj);
            }
        }
    }

    /**
     * Returns a function which calls the passed `interceptor` function first, and the passed `original` after
     * as long as the `interceptor` does not return `false`.
     * @param {Function} original The function to call second.
     * @param {Function} interceptor The function to call first.
     * @param {Object} [thisObj] The `this` reference when the functions are called.
     * @returns The return value from the `original` function **if it was called**, else `false`.
     */
    static createInterceptor(original, interceptor, thisObj) {
        return (...args) => {
            if (interceptor.call(thisObj, ...args) !== false) {
                return original.call(thisObj, ...args);
            }
            return false;
        };
    }

    /**
     * Returns a function which calls the passed `sequence` function after calling
     * the passed `original`.
     * @param {Function} original The function to call first.
     * @param {Function} sequence The function to call second.
     * @param {Object} [thisObj] The `this` reference when the functions are called.
     * @returns The value returned from the sequence if it returned a value, else the return
     * value from the original function.
     */
    static createSequence(original, sequence, thisObj) {
        return (...args) => {
            const origResult = original.call(thisObj, ...args),
                sequenceResult = sequence.call(thisObj, ...args);

            return (sequenceResult === void 0) ? origResult : sequenceResult;
        };
    }

    /**
     * Create a "debounced" function which will call on the "leading edge" of a timer period.
     * When first invoked will call immediately, but invocations after that inside its buffer
     * period will be rejected, and *one* invocation will be made after the buffer period has expired.
     *
     * This is useful for responding immediately to a first mousemove, but from then on, only
     * calling the action function on a regular timer while the mouse continues to move.
     *
     * @param {Function} fn The function to call.
     * @param {Number} buffer The milliseconds to wait after each execution before another execution takes place.
     * @param {Object} [thisObj] `this` reference for the function.
     * @param {Array} [extraArgs] The argument list to append to those passed to the function.
     * @param {Function} [alt] A function to call when the invocation is rejected due to buffer time not having expired.
     */
    static createThrottled(fn, buffer, thisObj, extraArgs, alt) {
        let lastCallTime = 0,
            callArgs,
            timerId,
            result;

        const invoke = () => {
            timerId = 0;
            lastCallTime = performance.now();
            callArgs.push.apply(callArgs, extraArgs);
            fn.apply(thisObj, callArgs);
        };

        result = function(...args) {
            let elapsed = performance.now() - lastCallTime;

            callArgs = args;

            // If it's been more then the buffer period since we invoked, we can call it now
            if (elapsed >= buffer) {
                clearTimeout(timerId);
                invoke();
            }
            // Otherwise, kick off a timer for the requested period.
            else {
                if (!timerId) {
                    timerId = setTimeout(invoke, buffer - elapsed);
                }
                if (alt) {
                    callArgs.push.apply(callArgs, extraArgs);
                    alt.apply(thisObj, callArgs);
                }
            }
        };

        result.cancel = () => clearTimeout(timerId);

        return result;
    }

    /**
     * Create a "debounced" function which will call on the "trailing edge" of a timer period.
     * When first invoked will wait until the buffer period has expired to call the function, and
     * more calls within that time will restart the timer.
     *
     * This is useful for responding to keystrokes, but deferring action until the user pauses typing.
     *
     * @param {Function} fn The function to call.
     * @param {Number} buffer The milliseconds to wait after each execution before another execution takes place.
     * @param {Object} [thisObj] `this` reference for the function.
     * @param {Array} [args] The argument list to append to those passed to the function.
     */
    static createBuffered(fn, buffer, thisObj, args) {
        let callArgs,
            timerId,
            result;

        const invoke = () => {
            timerId = 0;
            callArgs.push.apply(callArgs, args);
            fn.apply(thisObj, callArgs);
        };

        result = function(...args) {
            callArgs = args;

            // Cancel any impending invocation. It's pushed out for <buffer> ms from each call
            if (timerId) {
                clearTimeout(timerId);
            }

            timerId = setTimeout(invoke, buffer);
        };

        result.cancel = () => clearTimeout(timerId);

        return result;
    }

    static returnTrue() {
        return true;
    }

    static animate(duration, fn, scope, easing = 'linear') {
        let cancel = false;

        const result = new Promise(resolve => {
            const start = performance.now(),
                iterate = () => {
                    const progress = Math.min((performance.now() - start) / duration, 1);
                    if (!cancel) {
                        if (fn.call(scope, this.easingFunctions[easing](progress)) === false) {
                            resolve();
                        }
                    }
                    if (cancel || progress === 1) {
                        // Push resolution into the next animation frame so that
                        // this frame completes before the resolution handler runs.
                        requestAnimationFrame(() => resolve());
                    }
                    else {
                        requestAnimationFrame(iterate);
                    }
                };

            iterate();
        });

        result.cancel = () => {
            cancel = true;
            return false;
        };

        return result;
    }
}

/* eslint-disable */
const half = 0.5, e1 = 1.70158, e2 = 7.5625, e3 = 1.525, e4 = 2/2.75, e5 = 2.25/2.75, e6 = 1/2.75, e7 = 1.5/2.75, e8 = 2.5/2.75, e9 = 2.625/2.75, e10 = 0.75, e11 = 0.9375, e12 = 0.984375;
FunctionHelper.easingFunctions = {
    linear         : t => t,
    easeInQuad     : t => Math.pow(t,2),
    easeOutQuad    : t => -(Math.pow((t-1),2)-1),
    easeInOutQuad  : t => (t/=half)<1 ? half*Math.pow(t,2) : -half*((t-=2)*t-2),
    easeInCubic    : t => Math.pow(t,3),
    easeOutCubic   : t => Math.pow((t-1),3)+1,
    easeInOutCubic : t => (t/=half)<1 ? half*Math.pow(t,3) : half*(Math.pow((t-2),3)+2),
    easeInQuart    : t => Math.pow(t,4),
    easeOutQuart   : t => -(Math.pow((t-1),4)-1),
    easeInOutQuart : t => (t/=half)<1 ? half*Math.pow(t,4) : -half*((t-=2)*Math.pow(t,3)-2),
    easeInQuint    : t => Math.pow(t,5),
    easeOutQuint   : t => (Math.pow((t-1),5)+1),
    easeInOutQuint : t => (t/=half)<1 ? half*Math.pow(t,5) : half*(Math.pow((t-2),5)+2),
    easeInSine     : t => -Math.cos(t*(Math.PI/2))+1,
    easeOutSine    : t => Math.sin(t*(Math.PI/2)),
    easeInOutSine  : t => -half*(Math.cos(Math.PI*t)-1),
    easeInExpo     : t => t===0 ? 0 : Math.pow(2,10*(t-1)),
    easeOutExpo    : t => t===1 ? 1 : -Math.pow(2,-10*t)+1,
    easeInOutExpo  : t => (t===0) ? 0 : (t===1) ? 1 : ((t/=half)<1) ? half*Math.pow(2,10*(t-1)) : half*(-Math.pow(2,-10*--t)+2),
    easeInCirc     : t => -(Math.sqrt(1-(t*t))-1),
    easeOutCirc    : t => Math.sqrt(1-Math.pow((t-1),2)),
    easeInOutCirc  : t => (t/=half)<1 ? -half*(Math.sqrt(1-t*t)-1) : half*(Math.sqrt(1-(t-=2)*t)+1),
    easeOutBounce  : t => ((t)<e6) ? (e2*t*t) : (t<e4) ? (e2*(t-=e7)*t+e10) : (t<e8) ? (e2*(t-=e5)*t+e11) : (e2*(t-=e9)*t+e12),
    easeInBack     : t => (t)*t*((e1+1)*t-e1),
    easeOutBack    : t => (t=t-1)*t*((e1+1)*t+e1)+1,
    easeInOutBack  : t => {
                        let s = 1.70158;
                        return ((t/=half)<1) ? half*(t*t*(((s*=(e3))+1)*t -s)) : half*((t-=2)*t*(((s*=(e3))+1)*t+s)+2);
                    },
    elastic        : t => -1*Math.pow(4,-8*t)*Math.sin((t*6-1)*(2*Math.PI)/2)+1,
    swingFromTo    : t => {
                        let s = 1.70158;
                        return ((t/=half)<1) ? half*(t*t*(((s*=(e3))+1)*t-s)) : half*((t-=2)*t*(((s*=(e3))+1)*t+s)+2);
                    },
    swingFrom      : t => t*t*((e1+1)*t-e1),
    swingTo        : t => (t-=1)*t*((e1+1)*t+e1)+1,
    bounce         : t => (t<e6) ? (e2*t*t) : (t<e4) ? (e2*(t-=e7)*t+e10) : (t<e8) ? (e2*(t-=e5)*t+e11) : (e2*(t-=e9)*t+e12),
    bouncePast     : t => (t<e6) ? (e2*t*t) : (t<e4) ? 2-(e2*(t-=e7)*t+e10) : (t<e8) ? 2-(e2*(t-=e5)*t+e11) : 2-(e2*(t-=e9)*t+e12),
    easeFromTo     : t => (t/=half)<1 ? half*Math.pow(t,4) : -half*((t-=2)*Math.pow(t,3)-2),
    easeFrom       : t => Math.pow(t,4),
    easeTo         : t => Math.pow(t,0.25)
};

//<debug>
window.FunctionHelper = FunctionHelper;
//</debug>
