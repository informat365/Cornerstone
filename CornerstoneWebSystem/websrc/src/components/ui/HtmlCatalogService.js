/**
 * debouncing, executes the function if there was no new event in $wait milliseconds
 * @param func
 * @param wait
 * @param scope
 * @returns {Function}
 */
export const debounce = function (func, wait = 250, scope) {
  let timeout;
  return function () {
    const context = scope || this;
    const args = arguments;
    const later = function () {
      timeout = null;
      func.apply(context, args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
};
/**
 * in case of a "storm of events", this executes once every $threshold
 * @param fn
 * @param threshhold
 * @param scope
 * @returns {Function}
 */
export const throttle = function (fn, threshhold = 250, scope) {
  let last;
  let deferTimer;
  return function () {
    const context = scope || this;
    const now = +new Date;
    const args = arguments;
    if (last && now < last + threshhold) {
      clearTimeout(deferTimer);
      deferTimer = setTimeout(function () {
        last = now;
        fn.apply(context, args);
      }, threshhold);
    } else {
      last = now;
      fn.apply(context, args);
    }
  };
};

export const on = (element, event, handler) => {
  if (document.addEventListener) {
    if (element && event && handler) {
      element.addEventListener(event, handler, false);
    }
    return;
  }
  if (element && event && handler) {
    element.attachEvent('on' + event, handler);
  }
};

export const off = (element, event, handler) => {
  if (document.removeEventListener) {
    if (element && event) {
      element.removeEventListener(event, handler, false);
    }
    return;
  }
  if (element && event) {
    element.detachEvent('on' + event, handler);
  }
};


export const scrollTop = (el, from = 0, to, duration = 500, endCallback) => {
  if (!window.requestAnimationFrame) {
    window.requestAnimationFrame = (
        window.webkitRequestAnimationFrame ||
        window.mozRequestAnimationFrame ||
        window.msRequestAnimationFrame ||
        function (callback) {
          return window.setTimeout(callback, 1000 / 60);
        }
    );
  }
  const difference = Math.abs(from - to);
  const step = Math.ceil(difference / duration * 50);
  const scroll = (start, end, step) => {
    if (start === end) {
      endCallback && endCallback();
      return;
    }

    let d = (start + step > end) ? end : start + step;
    if (start > end) {
      d = (start - step < end) ? end : start - step;
    }

    if (el === window) {
      window.scrollTo(d, d);
    } else {
      el.scrollTop = d;
    }
    window.requestAnimationFrame(() => scroll(d, end, step));
  };
  scroll(from, to, step);
};
