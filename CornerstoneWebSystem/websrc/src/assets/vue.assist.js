// For Modal scrollBar hidden
let cached
export function getScrollBarSize(fresh) {    //获取滚动条宽度
    if(fresh || cached === undefined) {
        const inner = document.createElement('div')
        inner.style.width = '100%'
        inner.style.height = '200px'
        const outer = document.createElement('div')
        const outerStyle = outer.style
        outerStyle.position = 'absolute'
        outerStyle.top = 0
        outerStyle.left = 0
        outerStyle.pointerEvents = 'none'
        outerStyle.visibility = 'hidden'
        outerStyle.width = '200px'
        outerStyle.height = '150px'
        outerStyle.overflow = 'hidden'
        outer.appendChild(inner)
        document.body.appendChild(outer)
        const widthContained = inner.offsetWidth
        outer.style.overflow = 'scroll'
        let widthScroll = inner.offsetWidth
        if(widthContained === widthScroll) {
            widthScroll = outer.clientWidth
        }
        document.body.removeChild(outer)
        cached = widthContained - widthScroll
    }
    return cached || 15     //MAC可以设置滚动条显示形式
}

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
