/* eslint-disable */
import Popper from 'popper.js/dist/popper';
import ResizeObserver from 'resize-observer-polyfill';
import { off, on } from './vue.assist';

const resizeHandler = function (entries) {
    for (let entry of entries) {
        const listeners = entry.target.__resizeListeners__ || [];
        if (listeners.length) {
            listeners.forEach(fn => {
                if (fn) {
                    fn();
                }
            });
        }
    }
};

export const addResizeListener = function (element, fn) {
    if (!element.__resizeListeners__) {
        element.__resizeListeners__ = [];
        element.__ro__ = new ResizeObserver(resizeHandler);
        element.__ro__.observe(element);
    }
    element.__resizeListeners__.push(fn);
};

export const removeResizeListener = function (element, fn) {
    if (!element || !element.__resizeListeners__) return;
    element.__resizeListeners__.splice(element.__resizeListeners__.indexOf(fn), 1);
    if (!element.__resizeListeners__.length) {
        element.__ro__.disconnect();
    }
};


const TooltipPopper = document.createElement('div');
const TooltipPopperInner = document.createElement('div');
(() => {
    TooltipPopper.className = 'v-directive-tooltip';
    const TooltipPopperContent = document.createElement('div');
    TooltipPopperContent.className = 'v-directive-tooltip-content';
    const TooltipPopperArrow = document.createElement('div');
    TooltipPopperArrow.className = 'v-directive-tooltip-arrow';
    TooltipPopperInner.className = 'v-directive-tooltip-inner';
    document.body.appendChild(TooltipPopper);
    TooltipPopper.appendChild(TooltipPopperContent);
    TooltipPopperContent.appendChild(TooltipPopperArrow);
    TooltipPopperContent.appendChild(TooltipPopperInner);
    TooltipPopperInner.innerText = '';
})();

const createPopperJS = (el, popper) => {
    return new Popper(el, popper, {
        placement: 'top',
        positionFixed: true,
        eventsEnabled: true,
        modifiers: {
            preventOverflow: {
                boundariesElement: 'window',
            },
            computeStyle: {
                gpuAcceleration: false,
            },
            offset: {
                offset: '0,8,0,0',
            },
        },
    });
};

export const Tooltip = {
    inserted(el, binding) {
        if (binding.arg === 'disable') {
            return;
        }
        el.$popperJS = createPopperJS(el, TooltipPopper);
        on(el, 'mouseover', () => {
            TooltipPopperInner.innerText = binding.value || '';
            TooltipPopper.style.display = 'block';
            if (el.$popperJS) {
                el.$popperJS.update();
            }
        });
        on(el, 'mouseout', () => {
            TooltipPopper.style.display = 'none';
            if (el.$popperJS) {
                el.$popperJS.update();
            }
        });
        on(el, 'click', () => {
            TooltipPopper.style.display = 'none';
            if (el.$popperJS) {
                el.$popperJS.update();
            }
        });
    },
    componentUpdated(el, binding) {
        if (binding.arg === 'disable') {
            if (binding.oldArg !== 'enable') {
                off(el, 'mouseover');
                off(el, 'mouseout');
                off(el, 'click');
                if (el.$popperJS) {
                    el.$popperJS.destroy();
                    el.$popperJS = null;
                }
            }
            return;
        }
        if (!el.$popperJS) {
            el.$popperJS = createPopperJS(el, TooltipPopper);
        }
        // TooltipPopperInner.innerText = binding.value || '';
        off(el, 'mouseover');
        off(el, 'mouseout');
        off(el, 'click');
        on(el, 'mouseover', () => {
            TooltipPopperInner.innerText = binding.value || '';
            TooltipPopper.style.display = 'block';
            if (el.$popperJS) {
                el.$popperJS.update();
            }
        });
        on(el, 'mouseout', () => {
            TooltipPopper.style.display = 'none';
            if (el.$popperJS) {
                el.$popperJS.update();
            }
        });
        on(el, 'click', () => {
            TooltipPopper.style.display = 'none';
            if (el.$popperJS) {
                el.$popperJS.update();
            }
        });
    },
    unbind(el) {
        TooltipPopper.style.display = 'none';
        TooltipPopperInner.innerText = '';
        if (el.$popperJS) {
            el.$popperJS.destroy();
            el.$popperJS = null;
        }
        off(el, 'mouseover');
        off(el, 'mouseout');
        off(el, 'click');
    },
};

const TextOverflowCrop = (elm) => {
    const __data__ = elm.__textOverflowData__;
    if (!__data__) {
        return;
    }
    let text = __data__.text;
    if (!text) {
        return;
    }
    elm.innerHTML = `<span style="white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">${ text }</span>`;
    //计算原始宽度,隐藏时可能宽度无法获取
    let width = elm.getBoundingClientRect().width;
    if (width === 0) {
        return;
    }
    elm.innerHTML = `<span style="white-space: nowrap;overflow: hidden;">${ text }</span>`;
    const child = elm.firstElementChild;
    //计算插入元素的宽度,如果未超出出则不处理省略号
    if (child.getBoundingClientRect().width <= width) {
        return;
    }
    width = width - 6;
    //左侧省略号 v-text-overflow.right="value"
    if (__data__.modifiers.left) {
        elm.innerHTML = elm.innerHTML = `<div style="white-space: nowrap;overflow: hidden;text-overflow: ellipsis;direction:rtl;text-align:right;">${ text }</div>`;
        return;
    }
    //右侧省略号 v-text-overflow.right="value"
    if (__data__.modifiers.right) {
        elm.innerHTML = elm.innerHTML = `<div style="white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">${ text }</div>`;
        return;
    }
    //中间省略号 v-text-overflow="value"
    elm.innerHTML = `
        <span style="display:inline-block;white-space: nowrap;overflow: hidden; text-overflow: ellipsis;float:left;width:${ width / 2 }px">${ text }</span>
        <span style="display:inline-block;white-space: nowrap;overflow: hidden;float:left;direction:rtl;text-align:right;width:${ width / 2 }px;">${ text }</span>
        <span style="clear:both !important;"></span>
    `;
};

export const TextOverflow = {
    inserted(el, binding) {
        el.__textOverflowData__ = {
            text: binding.value,
            modifiers: {
                left: binding.modifiers.left === true,
                right: binding.modifiers.right === true,
            },
            resizeHandler: () => {
                TextOverflowCrop(el, binding);
            },
        };
        removeResizeListener(el);
        addResizeListener(el, el.resizeHandler);
        TextOverflowCrop(el);
    },
    componentUpdated(el, binding) {
        el.__textOverflowData__ = {
            text: binding.value,
            modifiers: {
                left: binding.modifiers.left === true,
                right: binding.modifiers.right === true,
            },
            resizeHandler: el.__textOverflowData__.resizeHandler,
        };
        TextOverflowCrop(el);
    },
    unbind(el) {
        const __data__ = el.__textOverflowData__;
        if (__data__) {
            removeResizeListener(el, __data__.resizeHandler);
        }
        el.__textOverflowData__ = null;
        el.innerHTML = '';
    },
};

export default {};
