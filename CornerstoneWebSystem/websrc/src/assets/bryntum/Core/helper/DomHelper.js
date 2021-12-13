/* eslint-disable standard/no-callback-literal */
import WidgetHelper from './WidgetHelper.js';
import BrowserHelper from './BrowserHelper.js';
import StringHelper from './StringHelper.js';
import Rectangle from './util/Rectangle.js';
import ObjectHelper from './ObjectHelper.js';
import ArrayHelper from './ArrayHelper.js';
import IdHelper from './IdHelper.js';
import DomClassList from './util/DomClassList.js';
// Gives circular dependencies which I could not solve, called from global scope instead
//import GlobalEvents from '../GlobalEvents.js';

// https://app.assembla.com/spaces/bryntum/tickets/7903-rendering-fails
// HACK: this value is required to calculate width if it was configured relative to font size (em) but no element is set
const
    DEFAULT_FONT_SIZE = 14,
    t0t0              = { align : 't0-t0' };

// We only do the measurement once, if the value is null
let scrollBarWidth = null,
    idCounter      = 0,
    themeInfo      = null,
    host;

const
    // Transform matrix parse Regex. CSS transform computed style looks like this:
    // matrix(scaleX(), skewY(), skewX(), scaleY(), translateX(), translateY())
    // or
    // matrix3d(scaleX(), skewY(), 0, 0, skewX(), scaleY(), 0, 0, 0, 0, 1, 0, translateX(), translateY())
    // This is more reliable than using the style literal which may include
    // relative styles such as "translateX(-20em)", or not include the translation at all if it's from a CSS rule.
    // Use a const so as to only compile RexExp once
    translateMatrixRe        = /(?:matrix\((?:-?\d*\.?[0-9]*),\s?(?:-?\d*\.?[0-9]*),\s?(?:-?\d*\.?[0-9]*),\s?(?:-?\d*\.?[0-9]*),\s?(-?\d*\.?[0-9]*),\s?(-?\d*\.?[0-9]*))|(?:matrix3d\((?:-?\d*),\s?(?:-?\d*),\s?(?:-?\d*),\s?(?:-?\d*),\s?(?:-?\d*),\s?(?:-?\d*),\s?(?:-?\d*),\s?(?:-?\d*),\s?(?:-?\d*),\s?(?:-?\d*),\s?(?:-?\d*),\s?(?:-?\d*),\s?(-?\d*),\s?(-?\d*))/,
    pxTtranslateXRe          = /translate(3d|X)?\((-?\d*\.?[0-9]*)px(?:,\s?(-?\d*\.?[0-9]*)px)?/,
    pxTtranslateYRe          = /translate(3d|Y)?\((-?\d*\.?[0-9]*)px(?:,\s?(-?\d*\.?[0-9]*)px)?/,
    domIdRe                  = /^[^a-z]+|[^\w:.-]+/gi,
    whiteSpaceRe             = /\s+/,

    // DomHelper#createElement properties which require special processing.
    // All other configs such as id and type are applied directly to the element.
    elementCreateProperties  = {
        tag          : 1,
        html         : 1,
        children     : 1,
        tooltip      : 1,
        style        : 1,
        dataset      : 1,
        parent       : 1,
        nextSibling  : 1,
        ns           : 1,
        reference    : 1,
        className    : 1,
        unmatched    : 1, // Used by syncId approach
        _element     : 1, // Used by sync to assign used element back to the config, for usage by the caller
        onlyChildren : 1, // Used by sync to not touch the target element itself,
        elementData  : 1,
        compareHtml  : 1, // Sync,
        syncOptions  : 1  // Sync
    },

    // Attributes to ignore on sync
    syncIgnoreAttributes     = {
        tag           : 1,
        html          : 1,
        children      : 1,
        tooltip       : 1,
        parent        : 1,
        nextSibling   : 1,
        ns            : 1,
        reference     : 1,
        _element      : 1,
        elementData   : 1,
        retainElement : 1,
        compareHtml   : 1,
        syncOptions   : 1
    },

    styleIgnoreProperties    = {
        length     : 1,
        parentRule : 1,
        style      : 1
    },

    styleDimensionProperties = {
        width     : 1,
        height    : 1,
        top       : 1,
        left      : 1,
        minWidth  : 1,
        minHeight : 1,
        maxWidth  : 1,
        maxHeight : 1,
        fontSize  : 1
    },

    nativeFocusableTags      = {
        BUTTON   : 1,
        IFRAME   : 1,
        EMBED    : 1,
        INPUT    : 1,
        OBJECT   : 1,
        SELECT   : 1,
        TEXTAREA : 1,
        HTML     : BrowserHelper.isIE11 ? 1 : 0,
        BODY     : BrowserHelper.isIE11 ? 0 : 1
    },
    win                      = window,
    doc                      = document,
    emptyObject              = {},
    emptyArray               = [],
    arraySlice               = Array.prototype.slice,
    immediatePromise         = new Promise((resolve) => resolve()),
    devicePixelRatio         = window.devicePixelRatio || 1,
    roundPx                  = devicePixelRatio === 1 ? Math.round : px => Math.round(px * devicePixelRatio) / devicePixelRatio,
    fontProps                = [
        'font-size',
        'font-size-adjust',
        'font-style',
        'font-weight',
        'font-family',
        'font-kerning',
        'font-stretch',
        'line-height',
        'text-transform',
        'text-decoration',
        'letter-spacing',
        'word-break'
    ],
    // Used in sync to give ObjectHelper.isDeeplyEqual() some domain knowledge
    syncEqualityEvaluator    = {
        // Attributes used during creation that should not be compared
        ignore : {
            '_element'    : 1,
            'parent'      : 1,
            'elementData' : 1,
            'ns'          : 1
        },
        // Function to evaluate 'compareHtml' property instead of 'html' for DocumentFragments
        evaluate(property, a, b) {
            if (property === 'html' && typeof a.value !== 'string') {
                // DocumentFragment, compare separately supplied html
                return (a.object.compareHtml === b.object.compareHtml);
            }
        }
    },
    isHiddenWidget           = e => e._hidden,
    parentNode               = el => el.parentNode || el.host,
    isVisible                = e => {
        const style = e.ownerDocument.defaultView.getComputedStyle(e);

        return style.getPropertyValue('display') !== 'none' && style.getPropertyValue('visibility') !== 'hidden';
    },
    getRootNode              = document.documentElement.getRootNode ? el => el.getRootNode() : el => {
        while (el.parentNode) el = el.parentNode;
        return el;
    },
    // Check whether the element has an offsetParent.
    // Nodes such as SVG which do not expose such a property must have an ancestor which has an offsetParent.
    hasLayout                = el => el && (el === document.body || ('offsetParent' in el ? el.offsetParent : hasLayout(el.parentNode))),
    // Check for node being in document.
    // If part of shadow DOM, see if the root's host is in the DOM
    isInDocument             = el => el && (document.body.contains(el) || ((host = getRootNode(el).host) && isInDocument(host)));

export { roundPx, hasLayout, isInDocument };

let templateElement, htmlParser;

/**
 * @module Core/helper/DomHelper
 */

/**
 * Helps with dom querying and manipulation.
 * ```
 * DomHelper.createElement({
 *   tag: 'div',
 *   className: 'parent',
 *   style: 'background: red',
 *   children: [
 *      { tag: 'div', className: 'child' },
 *      { tag: 'div', className: 'child' }
 *   ]
 * });
 * ```
 */
export default class DomHelper {
    /**
     * Returns `true` if the passed element is focusable either programatically or through pointer gestures.
     * @param {HTMLElement} element The element to test.
     */
    static isFocusable(element, skipAccessibilityCheck = false) {
        if (!skipAccessibilityCheck) {
            // If element is hidden or in a hidden Widget, it's not focusable.
            if (!this.isVisible(element) || IdHelper.fromElement(element, isHiddenWidget)) {
                return false;
            }
        }

        const nodeName = element.nodeName;

        /*
         * An element is focusable if:
         *   - It is natively focusable, or
         *   - It is an anchor or link with href attribute, or
         *   - It has a tabIndex, or
         *   - It is an editing host (contenteditable="true")
         */
        return nativeFocusableTags[nodeName] ||
            ((nodeName === 'A' || nodeName === 'LINK') && !!element.href) ||
            element.getAttribute('tabIndex') != null ||
            element.contentEditable === 'true';
    }

    /**
     * Returns `true` if the passed element is currently visible in the browser viewport, i.e. user can find it on screen
     * @param {HTMLElement} element The element to test.
     * @param {Boolean} whole Whether to check that whole element is visible, not just part of it.
     */
    static isInView(el, whole = true) {
        let elRect = Rectangle.from(el),
            inView = true;

        const fullHeight = elRect.height,
              fullWidth  = elRect.width;

        while (inView && el.parentElement) {
            el = el.parentElement;
            elRect = elRect.intersect(Rectangle.from(el));
            inView = elRect && (!whole || (elRect.height >= fullHeight && elRect.width >= fullWidth));
        }

        return inView;
    }

    /**
     * Returns `true` if the passed element is deeply visible. Meaning it is not hidden using `display`
     * or `visibility` and no ancestor node is hidden.
     * @param {HTMLElement} element The element to test.
     * @returns {Boolean} `true` if deeply visible.
     */
    static isVisible(element) {
        const document = element.ownerDocument;

        // Use the parentNode function so that we can traverse upwards through shadow DOM
        // to correctly ascertain visibility of nodes in web components.
        for (; element; element = parentNode(element)) {
            // Visible if we've reached top of the owning document without finding a hidden Element.
            if (element === document) {
                return true;
            }
            // Must not evaluate a shadow DOM's root fragment.
            if (element.nodeType === 1 && !isVisible(element)) {
                return false;
            }
        }

        // We get here if the node is detached.
        return false;
    }

    static roundPx(v) {
        return roundPx(v);
    }

    /**
     * Returns true if element has opened shadow root
     * @param {HTMLElement} element Element to check
     * @returns {Boolean}
     */
    static isCustomElement(element) {
        return element && element.shadowRoot;
    }

    /**
     * Resolves element from point, checking shadow DOM if requried
     * @param {Number} x
     * @param {Number} y
     * @returns {HTMLElement}
     */
    static elementFromPoint(x, y) {
        let el = document.elementFromPoint(x, y);

        // Try to check shadow dom if it exists
        if (DomHelper.isCustomElement(el)) {
            el = el.shadowRoot.elementFromPoint(x, y) || el;
        }

        return el;
    }

    /**
     * Returns active element checking shadow dom too
     * @returns {HTMLElement}
     */
    static get activeElement() {
        let el = document.activeElement;

        while (el.shadowRoot) {
            el = el.shadowRoot.activeElement;
        }

        return el;
    }

    /**
     * Returns the `id` of the passed element. Generates a unique `id` if the element does not have one.
     * @param {HTMLElement} element The element to return the `id` of.
     */
    static getId(element) {
        return element.id || (element.id = 'b-element-' + (++idCounter));
    }

    //region Internal

    /**
     * Internal convenience fn to allow specifying either an element or a CSS selector to retrieve one
     * @private
     * @param {String|HTMLElement} elementOrSelector element or selector to lookup in DOM
     * @returns {HTMLElement}
     */
    static getElement(elementOrSelector) {
        // also used for SVG elements, so need to use more basic class, that is also returned by querySelector
        if (elementOrSelector instanceof Element) {
            return elementOrSelector;
        }

        return doc.querySelector(elementOrSelector);
    }

    /**
     * Sets attributes passed as object to given element
     * @private
     * @param {String|Element} elementOrSelector
     * @param {Object} attributes
     */
    static setAttributes(elementOrSelector, attributes) {
        const element = this.getElement(elementOrSelector);

        Object.entries(attributes).forEach(([key, value]) => element.setAttribute(key, value));
    }

    /**
     * Sets a CSS [length](https://developer.mozilla.org/en-US/docs/Web/CSS/length) style value.
     * @param {String|HTMLElement} element The element to set the style in, or, if just the result is required,
     * the style magnitude to return with units added.
     * @param {String} [style] The name of a style property which specifies a [length](https://developer.mozilla.org/en-US/docs/Web/CSS/length)
     * @param {Number|String} [value] The magnitude. If a number is used, the value will be set in `px` units.
     * @returns {String} The style value string.
     */
    static setLength(element, style, value) {
        if (arguments.length === 1) {
            return (typeof element === 'number') ? `${element}px` : element;
        }
        else {
            element = this.getElement(element);
            return element.style[style] = (typeof value === 'number') ? `${value}px` : value;
        }
    }

    //endregion

    //region Children, going down...

    /**
     * Gets the first direct child of `element` that matches `selector.
     * @param {HTMLElement} element Parent element
     * @param {String} selector CSS selector
     * @returns {HTMLElement}
     * @category Query children
     */
    static getChild(element, selector) {
        // TODO: Only IE11 doesn't support :scope
        if (BrowserHelper.supportsQueryScope) {
            selector = ':scope>' + selector;
        }
        else {
            const elId = element.id || (element.id = 'b-element-' + (++idCounter));
            selector = `#${elId} > ${selector}`;
        }
        return element.querySelector(selector);
    }

    /**
     * Checks if `element` has any child that matches `selector`.
     * @param {HTMLElement} element Parent element
     * @param {String} selector CSS selector
     * @returns {Boolean} true if any child matches selector
     * @category Query children
     */
    static hasChild(element, selector) {
        return DomHelper.getChild(element, selector) != null;
    }

    /**
     * Returns all child elements (not necessarily direct children) that matches `selector.
     * @param {HTMLElement} element Parent element
     * @param {String} selector CSS selector
     * @returns {HTMLElement[]} Matched elements, somewhere below `element
     * @category Query children
     */
    static children(element, selector) {
        return Array.from(element.querySelectorAll(selector));
    }

    /**
     * Looks at the specified `element` and all of its children for the one that first matches `selector.
     * @param {HTMLElement} element Parent element
     * @param {String} selector CSS selector
     * @returns {HTMLElement} Matched element, either element or an element below it
     * @category Query children
     */
    static down(element, selector) {
        if (!element) {
            return null;
        }

        if (element.matches && element.matches(selector)) {
            return element;
        }
        if (BrowserHelper.supportsQueryScope) {
            selector = ':scope ' + selector;
        }
        else {
            const elId = element.id || (element.id = 'b-element-' + (++idCounter));
            selector = `#${elId} ${selector}`;
        }
        return element.querySelector(selector);
    }

    /**
     * Checks if childElement is a descendant of parentElement (contained in it or a sub element)
     * @param {HTMLElement} parentElement Parent element
     * @param {HTMLElement} childElement Child element, at any level below parent
     * @returns {Boolean}
     * @category Query children
     */
    static isDescendant(parentElement, childElement) {
        // In case of IE11 and parentElement is <html>, HTMLDocument#contains is not supported - fallback to body
        if (!parentElement.contains) {
            parentElement = parentElement.body;
        }

        return parentElement.contains(childElement);
    }

    /**
     * Iterates over each result returned from `element.querySelectorAll(selector)`. First turns it into an array to
     * work in IE. Can also be called with only two arguments, in which case the first argument is used as selector and
     * document is used as the element.
     * @param {HTMLElement} element Parent element
     * @param {String} selector CSS selector
     * @param {Function} fn Function called for each found element
     * @category Query children
     */
    static forEachSelector(element, selector, fn) {
        if (typeof element === 'string') {
            fn = selector;
            selector = element;
            element = doc;
        }
        this.children(element, selector).forEach(fn);
    }

    /**
     * Iterates over the direct child elements of the specified element. First turns it into an array to
     * work in IE.
     * @param {HTMLElement} element Parent element
     * @param {Function} fn Function called for each child element
     * @category Query children
     */
    static forEachChild(element, fn) {
        Array.from(element.children).forEach(fn);
    }

    /**
     * Removes each element returned from `element.querySelectorAll(selector)`.
     * @param {HTMLElement} element
     * @param {String} selector
     * @category Query children
     */
    static removeEachSelector(element, selector) {
        this.forEachSelector(element, selector, child => child.remove());
    }

    static removeClsGlobally(element, ...classes) {
        classes.forEach(cls => this.forEachSelector(element, '.' + cls, child => child.classList.remove(cls)));
    }

    //endregion

    //region Parents, going up...

    /**
     * Looks at the specified element and all of its parents for the one that first matches selector.
     * @param {HTMLElement} element Element
     * @param {String} selector CSS selector
     * @returns {HTMLElement} Matched element, either the passed in element or an element above it
     * @category Query parents
     */
    static up(element, selector) {
        /*let parent = element;
        while (parent && !parent.matches(selector)) parent = parent.parentElement;
        return parent;*/
        return element && element.closest(selector);
    }

    static getAncestor(element, possibleAncestorParents, outerElement = null) {
        let found  = false,
            ancestor,
            parent = element;

        if (!Array.isArray(possibleAncestorParents)) possibleAncestorParents = [possibleAncestorParents];

        while ((parent = parent.parentElement)) {
            if (possibleAncestorParents.includes(parent)) {
                found = true;
                break;
            }
            if (outerElement && parent === outerElement) break;
            ancestor = parent;
        }

        if (!found) return null;
        return ancestor || element;
    }

    /**
     * Retrieves all parents to the specified element.
     * @param {HTMLElement} element Element
     * @returns {HTMLElement[]} All parent elements, bottom up
     * @category Query parents
     */
    static getParents(element) {
        const parents = [];

        while (element.parentElement) {
            parents.push(element.parentElement);
            element = element.parentElement;
        }

        return parents;
    }

    //endregion

    //region Creation

    /**
     * Converts the passed id to an id valid for usage as id on a DOM element.
     * @param {String} id
     * @returns {String}
     */
    static makeValidId(id) {
        if (id == null) return null;

        return String(id).replace(domIdRe, '');
    }

    /**
     * Creates an Element. Example usage:
     * @example
     * DomHelper.createElement({
     *   tag         : 'table', // defaults to 'div'
     *   cellSpacing : 0,
     *   className   : 'nacho',
     *   html        : 'I am a nacho',
     *   children    : [ { tag: 'tr', ... }, myDomElement ],
     *   parent      : myExistingElement // Or its id
     *   style       : 'font-weight: bold;color: red',
     *   dataset     : { index: 0, size: 10 },
     *   tooltip     : 'Yay!',
     *   ns          : 'http://www.w3.org/1999/xhtml'
     * });
     * @param {Object} config Element config, as in example
     * @param {Boolean} returnAll Specify true to return all elements & child elements created as an array
     * @returns {HTMLElement|HTMLElement[]|Object} Single element or array of elements `returnAll` was set to true.
     * If any elements had a `reference` property, this will be an object containing a reference to
     * all those elements, keyed by the reference name.
     * @category Creation
     */
    static createElement(config = {}, returnAll = false, refs = null, syncIdField = null) {
        if (typeof config.parent === 'string') {
            config.parent = document.getElementById(config.parent);
        }

        if (config.syncOptions && config.syncOptions.syncIdField) {
            syncIdField = config.syncOptions.syncIdField;
        }

        // nextSibling implies a parent
        const parent = config.parent || (config.nextSibling && config.nextSibling.parentNode);

        let element;

        if (config.ns) {
            element = doc.createElementNS(config.ns, config.tag || 'svg');
        }
        else {
            element = doc.createElement(config.tag || 'div');
        }

        if (config.html != null) {
            if (config.html instanceof DocumentFragment) {
                element.appendChild(config.html);
            }
            else {
                element.innerHTML = config.html;
            }
        }

        if (config.tooltip) {
            WidgetHelper.attachTooltip(element, config.tooltip);
        }

        if (config.style) {
            this.applyStyle(element, config.style);
        }

        if (config.dataset) {
            Object.assign(element.dataset, config.dataset);
        }

        if (parent) {
            parent.insertBefore(element, config.nextSibling);
        }

        if (config.reference) {
            (refs || (refs = {}))[config.reference] = element;
            element.setAttribute('reference', config.reference);
        }

        // Attach custom data to the element, not visible
        if (config.elementData) {
            element.elementData = config.elementData;
        }

        const { className } = config;

        if (className) {
            if (className.isDomClassList) {
                element.className = className.toString();
            }
            else if (typeof className === 'object') {
                element.className = new DomClassList(className);
            }
            else {
                element.className = className;
            }
        }

        // Handle things like id, className, type, rel, cellSpacing, href etc which just get assigned.
        for (const prop of Object.keys(config)) {
            if (!elementCreateProperties[prop]) {
                if (config.ns) {
                    element.setAttribute(prop, config[prop]);
                    //element.setAttributeNS(config.ns, prop, config[prop]);
                }
                else {
                    element[prop] = config[prop];
                }
            }
        }

        // if returnAll is true, use array
        if (returnAll === true) {
            returnAll = [element];
        }
        // if it already is an array, add to it (we are probably a child)
        else if (Array.isArray(returnAll)) {
            returnAll.push(element);
        }

        if (config.children) {

            if (syncIdField) {
                // Map syncId -> child element to avoid querying dom later on
                element.syncIdMap = {};
            }

            config.children.forEach(child => {
                // Skip null children, convenient to allow those for usage with Array.map()
                if (child) {
                    // Append string children as text nodes
                    if (typeof child === 'string') {
                        element.appendChild(document.createTextNode(child));
                    }
                    // Just append Elements directly.
                    else if (isNaN(child.nodeType)) {
                        child.parent = element;
                        if (!child.ns && config.ns) {
                            child.ns = config.ns;
                        }

                        const
                            childElement = DomHelper.createElement(child, returnAll, refs, syncIdField),
                            syncId       = child.dataset && child.dataset[syncIdField];

                        // syncId is used with DomHelper.sync to match elements. Populate a map here to make finding them faster
                        if (syncId != null) {
                            element.syncIdMap[syncId] = childElement;
                        }

                        // Do not want to alter the initial config
                        delete child.parent;
                    }
                    else {
                        element.appendChild(child);
                    }
                }
            });
        }

        // Store used config, to be able to compare on sync to determine if changed without hitting dom
        element.lastConfig = config;

        // Link element to config for easy retrieval
        config._element = element;

        // If references were used, return them in an object
        // If returnAll was specified, return the array
        // By default, return the root element
        return refs || returnAll || element;
    }

    /**
     * Create element(s) from a template (html string). Note that
     * `textNode`s are discarded unless the `raw` option is passed
     * as `true`.
     *
     * If the template has a single root element, then the single element will be returned
     * unless the `array` option is passed as `true`.
     *
     * If there are multiple elements, then an Array will be returned.
     *
     * @param {String} template The HTML string from which to create DOM content
     * @param {Object} [options] An object containing properties to modify how the DOM is created and returned.
     * @param {Boolean} [options.array] `true` to return an array even if there's only one resulting element.
     * @param {Boolean} [options.raw] Return all child nodes, including text nodes.
     * @param {Boolean} [options.fragment] Return a DocumentFragment.
     * @private
     */
    static createElementFromTemplate(template, options = emptyObject) {
        const { array, raw, fragment } = options;
        let result;

        // Use template by preference if it exists. It's faster on most supported platforms
        // https://jsperf.com/domparser-vs-template/
        if (DomHelper.supportsTemplate) {
            (templateElement || (templateElement = doc.createElement('template'))).innerHTML = template;

            result = templateElement.content;
            if (fragment) {
                // The template is reused, so therefore is its fragment.
                // If we release the fragment to a caller, it must be a clone.
                return result.cloneNode(true);
            }
        }
        else {
            (htmlParser || (htmlParser = new DOMParser())).parseFromString(template, 'text/html');

            result = htmlParser.parseFromString(template, 'text/html').body;

            // We must return a DocumentFragment.
            // myElement.append(fragment) inserts the contents of the fragment, not the fragment itself.
            if (fragment) {
                // Empty string results in *no document.body* on IE!
                const nodes = result ? result.childNodes : emptyArray;
                result = document.createDocumentFragment();
                while (nodes.length) {
                    result.appendChild(nodes[0]);
                }
                return result;
            }
            // Happens with empty template in IE11
            else if (!result) {
                result = { children : [], childNodes : [] };
            }
        }

        // Raw means all child nodes are returned
        if (raw) {
            result = result.childNodes;
        }
        // Otherwise, only element nodes
        else {
            result = result.children;
        }

        return result.length === 1 && !array ? result[0] : arraySlice.call(result);
    }

    /**
     * Inserts an `element` at first position in `into`.
     * @param {HTMLElement} into Parent element
     * @param {HTMLElement} element Element to insert, or an element config passed on to createElement()
     * @returns {HTMLElement}
     * @category Creation
     */
    static insertFirst(into, element) {
        if (element && element.nodeType !== Node.ELEMENT_NODE && element.tag) {
            element = DomHelper.createElement(element);
        }
        return into.insertBefore(element, into.firstElementChild);
    }

    /**
     * Inserts a `element` before `beforeElement` in `into`.
     * @param {HTMLElement} into Parent element
     * @param {HTMLElement} element Element to insert, or an element config passed on to createElement()
     * @param {HTMLElement} beforeElement Element before which passed element should be inserted
     * @returns {HTMLElement}
     * @category Creation
     */
    static insertBefore(into, element, beforeElement) {
        if (element && element.nodeType !== Node.ELEMENT_NODE && element.tag) {
            element = DomHelper.createElement(element);
        }
        return beforeElement ? into.insertBefore(element, beforeElement) : DomHelper.insertFirst(into, element);
    }

    /**
     * Appends element to parentElement.
     * @param {HTMLElement} parentElement Parent element
     * @param {HTMLElement|Object|String} elementOrConfig Element to insert, or an element config passed on to createElement(), or an html string passed to createElementFromTemplate
     * @returns {HTMLElement}
     * @category Creation
     */
    static append(parentElement, elementOrConfig) {
        if (elementOrConfig) {
            if (typeof elementOrConfig === 'string') {
                elementOrConfig = DomHelper.createElementFromTemplate(elementOrConfig);
            }
            else if (elementOrConfig.nodeType !== Node.ELEMENT_NODE && elementOrConfig.tag) {
                elementOrConfig = DomHelper.createElement(elementOrConfig);
            }
        }
        if (Array.isArray(elementOrConfig)) {
            return elementOrConfig.map(element => parentElement.appendChild(element));
        }
        else {
            return parentElement.appendChild(elementOrConfig);
        }
    }

    //endregion

    //region Get position

    /**
     * Returns the element's `transform translateX` value in pixels.
     * @param {HTMLElement} element
     * @returns {Number} X transform
     * @category Position, get
     */
    static getTranslateX(element) {
        let transformStyle = element.style.transform,
            matches        = pxTtranslateXRe.exec(transformStyle);

        // Use inline transform style if it contains "translate(npx, npx" or "translate3d(npx, npx" or "translateX(npx"
        if (matches) {
            return parseInt(matches[2]);
        }
        else {
            // If the inline style is the matrix() form, then use that, otherwise, use computedStyle
            matches =
                translateMatrixRe.exec(transformStyle) ||
                translateMatrixRe.exec(this.getStyleValue(this.getElement(element), 'transform'));
            return matches ? parseInt(matches[1] || matches[3]) : 0;
        }
    }

    /**
     * Returns the element's `transform translateY` value in pixels.
     * @param {HTMLElement} element
     * @returns {Number} Y coordinate
     * @category Position, get
     */
    static getTranslateY(element) {
        let transformStyle = element.style.transform,
            matches        = pxTtranslateYRe.exec(transformStyle);
        // Use inline transform style if it contains "translate(npx, npx" or "translate3d(npx, npx" or "translateY(npx"
        if (matches) {
            // If it was translateY(npx), use first item in the parens.
            const y = parseInt(matches[matches[1] === 'Y' ? 2 : 3]);
            // FF will strip `translate(x, 0)` -> `translate(x)`, so need to check for isNaN also
            return isNaN(y) ? 0 : y;
        }
        else {
            // If the inline style is the matrix() form, then use that, otherwise, use computedStyle
            matches =
                translateMatrixRe.exec(transformStyle) ||
                translateMatrixRe.exec(this.getStyleValue(this.getElement(element), 'transform'));
            return matches ? parseInt(matches[2] || matches[4]) : 0;
        }
    }

    /**
     * Gets both X and Y coordinates as an array [x, y]
     * @param {HTMLElement} element
     * @returns {Number[]} [x, y]
     * @category Position, get
     */
    static getTranslateXY(element) {
        return [this.getTranslateX(element), this.getTranslateY(element)];
    }

    /**
     * Get elements X offset within a containing element
     * @param {HTMLElement} element
     * @param {HTMLElement} container
     * @returns {Number} X offset
     * @category Position, get
     */
    static getOffsetX(element, container = null) {
        return container ? element.getBoundingClientRect().left - container.getBoundingClientRect().left : element.offsetLeft;
    }

    /**
     * Get elements Y offset within a containing element
     * @param {HTMLElement} element
     * @param {HTMLElement} container
     * @returns {Number} Y offset
     * @category Position, get
     */
    static getOffsetY(element, container = null) {
        return container ? element.getBoundingClientRect().top - container.getBoundingClientRect().top : element.offsetTop;
    }

    /**
     * Gets elemnts X and Y offset within containing element as an array [x, y]
     * @param {HTMLElement} element
     * @param {HTMLElement} container
     * @returns {Number[]} [x, y]
     * @category Position, get
     */
    static getOffsetXY(element, container = null) {
        return [this.getOffsetX(element, container), this.getOffsetY(element, container)];
    }

    /**
     * Focus element without scrolling the element into view.
     * @param {HTMLElement} element
     */
    static focusWithoutScrolling(element) {

        function resetScroll(scrollHierarchy) {
            scrollHierarchy.forEach(({ element, scrollLeft, scrollTop }) => {
                // Check first to avoid triggering unnecessary `scroll` events
                if (element.scrollLeft !== scrollLeft) {
                    element.scrollLeft = scrollLeft;
                }
                if (element.scrollTop !== scrollTop) {
                    element.scrollTop = scrollTop;
                }
            });
        }

        // Check browsers which do support focusOptions
        // https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/focus
        const preventScrollSupported = BrowserHelper.chromeVersion >= 64 || BrowserHelper.fireFoxVersion >= 68;

        if (preventScrollSupported) {
            element.focus({ preventScroll : true });
        }
        else {
            // Examine every parentNode of the target and cache the scrollLeft and scrollTop,
            // and restore all values after the focus has taken place
            const
                parents         = DomHelper.getParents(element),
                scrollHierarchy = parents.map(parent => ({
                    element    : parent,
                    scrollLeft : parent.scrollLeft,
                    scrollTop  : parent.scrollTop
                }));

            element.focus();

            // Reset in sync on IE, async in others
            if (BrowserHelper.isIE11) {
                resetScroll(scrollHierarchy);
            }
            else {
                setTimeout(() => resetScroll(scrollHierarchy), 0);
            }
        }
    }

    /**
     * Get elements X position on page
     * @param {HTMLElement} element
     * @returns {Number}
     * @category Position, get
     */
    static getPageX(element) {
        return element.getBoundingClientRect().left + win.pageXOffset; // no window.scrollX in IE11
    }

    /**
     * Get elements Y position on page
     * @param {HTMLElement} element
     * @returns {Number}
     * @category Position, get
     */
    static getPageY(element) {
        return element.getBoundingClientRect().top + win.pageYOffset; // no window.scrollY in IE11
    }

    /**
     * Returns extremal (min/max) size (height/width) of the element in pixels
     * @param {HTMLElement} element
     * @param {String} style minWidth/minHeight/maxWidth/maxHeight
     * @returns {number}
     * @internal
     */
    static getExtremalSizePX(element, style) {
        let prop    = StringHelper.hyphenate(style),
            measure = prop.split('-')[1],
            value   = this.getStyleValue(element, prop);

        if (/%/.test(value)) {
            // Element might be detached from DOM
            if (element.parentElement) {
                value = parseInt(this.getStyleValue(element.parentElement, measure), 10);
            }
            else {
                value = NaN;
            }
        }
        else {
            value = parseInt(value, 10);
        }

        return value;
    }

    //endregion

    //region Set position

    /**
     * Set element's `X` translation in pixels.
     * @param {HTMLElement} element
     * @param {Number} x The value by which the element should be translated from its default position.
     * @category Position, set
     */
    static setTranslateX(element, x) {
        const t = DomHelper.getStyleValue(element, 'transform').split(/,\s*/);

        // Avoid blurry text on non-retina displays
        x = roundPx(x);

        if (t.length > 1) {
            t[t[0].startsWith('matrix3d') ? 12 : 4] = x;
            element.style.transform = t.join(',');
        }
        else {
            element.style.transform = `translateX(${x}px)`;
        }
    }

    /**
     * Set element's `Y` translation in pixels.
     * @param {HTMLElement} element
     * @param {Number} y  The value by which the element should be translated from its default position.
     * @category Position, set
     */
    static setTranslateY(element, y) {
        const t = DomHelper.getStyleValue(element, 'transform').split(/,\s*/);

        // Avoid blurry text on non-retina displays
        y = roundPx(y);

        if (t.length > 1) {
            t[t[0].startsWith('matrix3d') ? 13 : 5] = y;
            element.style.transform = t.join(',') + ')';
        }
        else {
            element.style.transform = `translateY(${y}px)`;
        }
    }

    /**
     * Set element's style `top`.
     * @param {HTMLElement} element
     * @param {Number/String} y The top position. If numeric, `'px'` is used as the unit.
     * @category Position, set
     */
    static setTop(element, y) {
        this.setLength(element, 'top', y);
    }

    /**
     * Set element's style `left`.
     * @param {HTMLElement} element
     * @param {Number/String} x The top position. If numeric, `'px'` is used as the unit.
     * @category Position, set
     */
    static setLeft(element, x) {
        this.setLength(element, 'left', x);
    }

    /**
     * Set elements `X` and `Y` translation in pixels.
     * @param {HTMLElement} element
     * @param {Number} [x] The `X translation.
     * @param {Number} [y] The `Y translation.
     * @category Position, set
     */
    static setTranslateXY(element, x, y) {
        if (x == null) {
            return this.setTranslateY(element, y);
        }
        if (y == null) {
            return this.setTranslateX(element, x);
        }

        // Avoid blurry text on non-retina displays
        x = roundPx(x);
        y = roundPx(y);

        const
            t    = DomHelper.getStyleValue(element, 'transform').split(/,\s*/),
            is3d = t[0].startsWith('matrix3d');

        if (t.length > 1) {
            t[is3d ? 12 : 4] = x;
            t[is3d ? 13 : 5] = y;
            element.style.transform = t.join(',') + ')';
        }
        else {
            element.style.transform = `translateX(${x}px) translateY(${y}px)`;
        }
    }

    /**
     * Increase `X` translation
     * @param {HTMLElement} element
     * @param {Number} x The number of pixels by which to increase the element's `X` translation.
     * @category Position, set
     */
    static addTranslateX(element, x) {
        DomHelper.setTranslateX(element, DomHelper.getTranslateX(element) + x);
    }

    /**
     * Increase `Y` position
     * @param {HTMLElement} element
     * @param {Number} y The number of pixels by which to increase the element's `Y` translation.
     * @category Position, set
     */
    static addTranslateY(element, y) {
        DomHelper.setTranslateY(element, DomHelper.getTranslateY(element) + y);
    }

    /**
     * Increase X position
     * @param {HTMLElement} element
     * @param x
     * @category Position, set
     */
    static addLeft(element, x) {
        DomHelper.setLeft(element, DomHelper.getOffsetX(element) + x);
    }

    /**
     * Increase Y position
     * @param {HTMLElement} element
     * @param y
     * @category Position, set
     */
    static addTop(element, y) {
        DomHelper.setTop(element, DomHelper.getOffsetY(element) + y);
    }

    /**
     * Align the passed element with the passed target according to the align spec.
     * @param {HTMLElement} element The element to align.
     * @param {HTMLElement|Core.helper.util.Rectangle} target The target element or rectangle to align to
     * @param {Object} alignSpec See {@link Core.helper.util.Rectangle#function-alignTo} Defaults to `{ align : 't0-t0' }`
     */
    static alignTo(element, target, alignSpec = t0t0) {
        target = (target instanceof Rectangle) ? target : Rectangle.from(target, null, true);

        const
            elXY       = this.getTranslateXY(element),
            elRect     = Rectangle.from(element, null, true),
            targetRect = elRect.alignTo(Object.assign(alignSpec, {
                target
            }));

        this.setTranslateXY(element, elXY[0] + targetRect.x - elRect.x, elXY[1] + targetRect.y - elRect.y);
    }

    //endregion

    //region Styles & CSS

    /**
     * Returns a style value or values for the passed element.
     * @param {HTMLElement} element The element to read styles from
     * @param {String|String[]} propName The property or properties to read
     * @param {Boolean} [inline=false] Pass as `true` to read the element's inline style.
     * Note that this could return inaccurate results if CSS rules apply to this element.
     * @return {String|Object} The value or an object containing the values keyed by the requested property name.
     * @category CSS
     */
    static getStyleValue(element, propName, inline, pseudo) {
        const styles = inline ? element.style : element.ownerDocument.defaultView.getComputedStyle(element, pseudo);

        if (Array.isArray(propName)) {
            let result = {};
            for (let prop of propName) {
                result[prop] = styles.getPropertyValue(StringHelper.hyphenate(prop));
            }
            return result;
        }

        // Use the elements owning view to get the computed style.
        // Ensure the property name asked for is hyphenated.
        // getPropertyValue doesn't work with camelCase
        return styles.getPropertyValue(StringHelper.hyphenate(propName));
    }

    /**
     * Applies specified style to the passed element. Style can be an object or a string.
     * @param {HTMLElement} element Target element
     * @param {String|Object} style Style to apply, 'border: 1px solid black' or { border: '1px solid black' }
     * @param {Boolean} [overwrite] Specify `true` to replace style instead of applying changes
     * @category CSS
     */
    static applyStyle(element, style, overwrite = false) {

        if (typeof style === 'string') {
            if (overwrite) {
                // Only assign if either end has any styles, do not want to add empty `style` tag on element
                if (style.length || element.style.cssText.length) {
                    element.style.cssText = style;
                }
            }
            else {
                // Add style so as not to delete configs in style such as width, height, flex etc.
                // If a style is already there, the newest, appended one will take precedence.
                element.style.cssText += style;
            }
        }
        else {
            if (overwrite) {
                element.style.cssText = '';
                //element.removeAttribute('style');
            }

            // Has a sub-style block?
            if (style.style) {
                if (typeof style.style === 'string') {
                    element.style.cssText = style.style;
                }
                else {
                    style = Object.assign({}, style.style, style);
                }
            }

            // Prototype chained objects may be passed, so use direct loop.
            for (let key in style) {
                // Ignore readonly properties of the CSSStyleDeclaration object:
                // https://developer.mozilla.org/en-US/docs/Web/API/CSSStyleDeclaration
                // Also ignores sub-style blocks, which are applied above
                if (!styleIgnoreProperties[key]) {
                    // Append 'px' for numeric dimensions
                    if (styleDimensionProperties[key] && typeof style[key] == 'number') {
                        element.style[StringHelper.hyphenate(key)] = style[key] + 'px';
                    }
                    else {
                        element.style[StringHelper.hyphenate(key)] = style[key];
                    }
                }
            }
        }
    }

    static getCSSText(style) {
        if (typeof style === 'string') {
            return style;
        }

        let cssText = '';

        for (let key in style) {
            // Ignore readonly properties of the CSSStyleDeclaration object:
            // https://developer.mozilla.org/en-US/docs/Web/API/CSSStyleDeclaration
            if (!styleIgnoreProperties[key]) {
                cssText += `${StringHelper.hyphenate(key)}:${style[key]};`;
            }
        }

        return cssText;
    }

    // For IE11, it doesn't support adding/removing multiple classes at once

    /**
     * Add multiple classes to elements classList. Helper for IE11 which does not support it directly
     * @param {HTMLElement} element
     * @param {String[]} classes
     * @category CSS
     */
    static addClasses(element, classes) {
        classes.forEach(cls => element.classList.add(cls));
    }

    /**
     * Remove multiple classes to elements classList. Helper for IE11 which does not support it directly
     * @param {HTMLElement} element
     * @param {String[]} classes
     * @category CSS
     */
    static removeClasses(element, classes) {
        classes.forEach(cls => element.classList.remove(cls));
    }

    /**
     * Toggle multiple classes in elements classList. Helper for IE11 which does not support toggling with force or for
     * multiple classes at once.
     * @param {HTMLElement} element
     * @param {String[]} classes
     * @param {Boolean} [force] Specify true to add classes, false to remove. Leave blank to toggle
     * @category CSS
     */
    static toggleClasses(element, classes, force = null) {
        if (!Array.isArray(classes)) {
            classes = [classes];
        }

        if (force === true) {
            this.addClasses(element, classes);
        }
        else if (force === false) {
            this.removeClasses(element, classes);
        }
        else {
            classes.forEach(cls => element.classList.toggle(cls));
        }
    }

    /**
     * Adds a CSS class to an element during the specified duration
     * @param {HTMLElement} element Target element
     * @param {String} cls CSS class to add temporarily
     * @param {Number} duration Duration in ms, 0 means cls will not be applied
     * @category CSS
     */
    static addTemporaryClass(element, cls, duration) {
        if (duration > 0) {
            element.classList.add(cls);
            setTimeout(() => {
                element && element.classList.remove(cls);
            }, duration);
        }
    }

    //endregion

    //region Effects

    /**
     * Highlights the passed element or Rectangle according to the theme's highlihghting rules.
     * Usually an animated framing effect.
     * @param {HTMLElement/Core.helper.util.Rectangle} element The element or Rectangle to highlight.
     */
    static highlight(element) {
        if (element instanceof Rectangle) {
            return element.highlight();
        }
        return new Promise(resolve => {
            setTimeout(() => {
                element.classList.add('b-fx-highlight');
                setTimeout(() => {
                    element.classList.remove('b-fx-highlight');
                    resolve();
                }, 1000);
            }, 0);
        });
    }

    //endregion

    //region Measuring / Scrollbar

    /**
     * Measures the scrollbar width using a hidden div. Caches result
     * @returns {Number}
     * @readonly
     */
    static get scrollBarWidth() {
        // Ensure the measurement is only done once, when the value is null and body is available
        if (scrollBarWidth === null && doc.body) {
            const element = DomHelper.createElement({
                parent : doc.body,
                style  : 'position:absolute;top:-999px;width:100px;height:100px;overflow:scroll'
            });
            scrollBarWidth = element.offsetWidth - element.clientWidth;
            element.remove();
        }

        return scrollBarWidth;
    }

    /**
     * Resets DomHelper.scrollBarWidth cache, triggering a new measurement next time it is read
     */
    static resetScrollBarWidth() {
        scrollBarWidth = null;
    }

    /**
     * Measures the text width using a hidden div
     * @param {String} text
     * @param {HTMLElement} sourceElement
     * @returns {Number} width
     * @category Measure
     */
    static measureText(text, sourceElement, useHTML, parentElement) {
        const offScreenDiv = this.getMeasureElement(sourceElement, parentElement);

        offScreenDiv[useHTML ? 'innerHTML' : 'innerText'] = text;

        const result = offScreenDiv.clientWidth;
        offScreenDiv.className = '';

        return result;
    }

    /**
     * Measures a relative size, such as a size specified in `em` units for the passed element.
     * @param {String} size The CSS size value to measure.
     * @param {HTMLElement} sourceElement
     * @returns {Number} size The size in pixels of the passed relative measurement.
     * @category Measure
     */
    static measureSize(size, sourceElement) {
        if (!size) {
            return 0;
        }

        if (typeof size === 'number') {
            return size;
        }

        if (!size.length) {
            return 0;
        }

        if (/^\d+(px)?$/.test(size)) {
            return parseInt(size);
        }

        if (sourceElement) {
            const offScreenDiv = this.getMeasureElement(sourceElement);
            offScreenDiv.innerHTML = '';
            offScreenDiv.style.width = DomHelper.setLength(size);
            //const result = BrowserHelper.isIE11 ?  offScreenDiv.offsetWidth : offScreenDiv.clientWidth;
            const result = offScreenDiv.offsetWidth;
            offScreenDiv.style.width = offScreenDiv.className = '';
            return result;
        }

        if (/^\d+em$/.test(size)) {
            return parseInt(size) * DEFAULT_FONT_SIZE;
        }

        return isNaN(size) ? 0 : parseInt(size);
    }

    // parentElement allows measurement to happen inside a specific element, allowing scoped css rules to match
    static getMeasureElement(sourceElement, parentElement = doc.body) {
        const
            sourceElementStyle = win.getComputedStyle(sourceElement),
            offScreenDiv       = parentElement.offScreenDiv = parentElement.offScreenDiv || DomHelper.createElement({
                parent    : parentElement,
                style     : 'position:fixed;top:-10000px;left:-10000px;visibility:hidden;contain:strict',
                className : 'b-measure-element',
                children  : [{
                    style : 'white-space:nowrap;display:inline-block;will-change:contents;width:auto;contain:none'
                }]
            }, true)[1];

        fontProps.forEach(prop => {
            if (offScreenDiv.style[prop] !== sourceElementStyle[prop]) {
                offScreenDiv.style[prop] = sourceElementStyle[prop];
            }
        });
        offScreenDiv.className = sourceElement.className;

        // In case the measure element was moved/removed, re-add it
        if (offScreenDiv.parentElement.parentElement !== parentElement) {
            parentElement.appendChild(offScreenDiv.parentElement);
        }

        return offScreenDiv;
    }

    //endregion

    //region Sync

    /**
     * Sync one source element attributes, children etc. to a target element. Source element can be specified as a html
     * string or an actual HTMLElement.
     *
     * NOTE: This function is superseded by {@link Core/helper/DomSync#function-sync-static DomSync.sync()}, which works
     * with DOM configs. For most usecases, use it instead.
     *
     * @param {String|HTMLElement} sourceElement Source "element" to copy from
     * @param {HTMLElement} targetElement Target element to apply to, can also be specified as part of the config object
     * @returns {HTMLElement} Returns the updated targetElement (which is also updated in place)
     */
    static sync(sourceElement, targetElement) {
        if (typeof sourceElement === 'string') {
            if (sourceElement === '') {
                targetElement.innerHTML = '';
                return;
            }
            else {
                sourceElement = this.createElementFromTemplate(sourceElement);
            }
        }

        this.performSync(sourceElement, targetElement);

        return targetElement;
    }

    // Internal helper used for recursive syncing
    static performSync(sourceElement, targetElement) {
        // Syncing identical elements is a no-op
        if (sourceElement.outerHTML !== targetElement.outerHTML) {
            this.syncAttributes(sourceElement, targetElement);
            this.syncContent(sourceElement, targetElement);
            this.syncChildren(sourceElement, targetElement);

            return true;
        }
        return false;
    }

    // Attributes as map { attr : value, ... }, either from an html element or from a config
    static getSyncAttributes(element) {
        const
            attributes = {},
            // Attribute names, simplifies comparisons and calls to set/removeAttribute
            names      = [];

        // Extract from element
        for (let i = 0; i < element.attributes.length; i++) {
            const attr = element.attributes[i];
            if (attr.specified) {
                const name = attr.name.toLowerCase();
                attributes[name] = attr.value;
                names.push(name);
            }
        }

        return { attributes, names };
    }

    /**
     * Syncs attributes from sourceElement to targetElement.
     * @private
     * @param {HTMLElement} sourceElement
     * @param {HTMLElement} targetElement
     */
    static syncAttributes(sourceElement, targetElement) {
        const
            // Extract attributes from elements (sourceElement might be a config)
            {
                attributes : sourceAttributes,
                names      : sourceNames
            }          = this.getSyncAttributes(sourceElement),
            {
                attributes : targetAttributes,
                names      : targetNames
            }          = this.getSyncAttributes(targetElement),
            // Used to ignore data-xx attributes when we will be setting entire dataset
            hasDataset = sourceNames.includes('dataset'),
            // Intersect arrays to determine what needs adding, removing and syncing
            toAdd      = sourceNames.filter(attr => !targetNames.includes(attr)),
            toRemove   = targetNames.filter(attr => !sourceNames.includes(attr) && (!hasDataset || !attr.startsWith('data-'))),
            toSync     = sourceNames.filter(attr => targetNames.includes(attr));

        if (toAdd.length > 0) {
            for (let i = 0; i < toAdd.length; i++) {
                const attr = toAdd[i];

                // Style requires special handling
                if (attr === 'style') {
                    this.applyStyle(targetElement, sourceAttributes.style, true);
                }
                // So does dataset
                else if (attr === 'dataset') {
                    Object.assign(targetElement.dataset, sourceAttributes.dataset);
                }
                // Other attributes are set using setAttribute (since it calls toString() DomClassList works fine)
                else {
                    targetElement.setAttribute(attr, sourceAttributes[attr]);
                }
            }
        }

        if (toRemove.length > 0) {
            for (let i = 0; i < toRemove.length; i++) {
                targetElement.removeAttribute(toRemove[i]);
            }
        }

        if (toSync.length > 0) {
            for (let i = 0; i < toSync.length; i++) {
                const attr = toSync[i];
                // Set all attributes that has changed, with special handling for style
                if (attr === 'style') {
                    // TODO: Check for changes?
                    this.applyStyle(targetElement, sourceAttributes.style, true);
                }
                // And dataset
                else if (attr === 'dataset') {
                    // TODO: Any cost to assigning same values?
                    Object.assign(targetElement.dataset, sourceAttributes.dataset);
                }
                // And class, which might be a DomClassList or an config for a DomClassList
                else if (attr === 'class' && (sourceAttributes.class.isDomClassList || typeof sourceAttributes.class === 'object')) {
                    let classList;

                    if (sourceAttributes.class.isDomClassList) {
                        classList = sourceAttributes.class;
                    }
                    else {
                        // TODO : Reuse a single DomClassList?
                        classList = new DomClassList(sourceAttributes.class);
                    }

                    if (!classList.isEqual(targetAttributes.class)) {
                        targetElement.setAttribute('class', classList);
                    }
                }
                else if (targetAttributes[attr] !== sourceAttributes[attr]) {
                    targetElement.setAttribute(attr, sourceAttributes[attr]);
                }
            }
        }
    }

    /**
     * Sync content (innerText) from sourceElement to targetElement
     * @private
     * @param {HTMLElement} sourceElement
     * @param {HTMLElement} targetElement
     */
    static syncContent(sourceElement, targetElement) {
        if (sourceElement.childElementCount === 0) {
            targetElement.innerText = sourceElement.innerText;
        }
    }

    static setInnerText(targetElement, text) {
        // setting firstChild.data is faster than innerText (and innerHTML),
        // but in some cases the inner node is lost and needs to be recreated
        const firstChild = targetElement.firstChild;
        if (firstChild) {
            firstChild.data = text;
        }
        else {
            // textContent is supposed to be faster than innerText, since it does not trigger layout
            targetElement.textContent = text;
        }
    }

    /**
     * Sync traversing children
     * @private
     * @param {HTMLElement} sourceElement Source element
     * @param {HTMLElement} targetElement Target element
     * @param {String} syncIdField Field in dataset to use for element re-usage
     */
    static syncChildren(sourceElement, targetElement) {
        const
            me          = this,
            sourceNodes = arraySlice.call(sourceElement.childNodes),
            targetNodes = arraySlice.call(targetElement.childNodes);

        while (sourceNodes.length) {
            const
                sourceNode = sourceNodes.shift(),
                targetNode = targetNodes.shift();

            // only textNodes and elements allowed (no comments)
            if (sourceNode && sourceNode.nodeType !== Node.TEXT_NODE && sourceNode.nodeType !== Node.ELEMENT_NODE) {
                throw new Error(`Source node type ${sourceNode.nodeType} not supported by DomHelper.sync()`);
            }
            if (targetNode && targetNode.nodeType !== Node.TEXT_NODE && targetNode.nodeType !== Node.ELEMENT_NODE) {
                throw new Error(`Target node type ${targetNode.nodeType} not supported by DomHelper.sync()`);
            }

            if (!targetNode) {
                // out of target nodes, add to target
                targetElement.appendChild(sourceNode);
            }
            else {
                // match node

                if (sourceNode.nodeType === targetNode.nodeType) {
                    // same type of node, take action depending on which type
                    if (sourceNode.nodeType === Node.TEXT_NODE) {
                        // text
                        targetNode.data = sourceNode.data;
                    }
                    else {
                        if (sourceNode.tagName === targetNode.tagName) {
                            me.performSync(sourceNode, targetNode);
                        }
                        else {
                            // new tag, remove targetNode and insert new element
                            targetElement.insertBefore(sourceNode, targetNode);
                            targetNode.remove();
                        }
                    }
                }
                // Trying to set text node as element, use it as innerText
                // (we get this in FF with store mutations and List)
                else if (sourceNode.nodeType === Node.TEXT_NODE && targetNode.nodeType === Node.ELEMENT_NODE) {
                    targetElement.innerText = sourceNode.data.trim();
                }
                else {
                    throw new Error('Currently no support for transforming nodeType');
                }
            }
        }

        // Out of source nodes, remove remaining target nodes
        targetNodes.forEach(targetNode => {
            targetNode.remove();
        });
    }

    /**
     * Synchronizes the passed element's `classList` with the class names
     * passed in either Array or String format or Object. Avoiding mutating an element's
     * `classList` or `className` can avoid browser style recalculations.
     * @param {HTMLElement} element The element whose class list to synchronize.
     * @param {String[]|String|Object} newClasses The incoming class names to set on the element.
     * @category CSS
     */
    static syncClassList(element, newClasses) {
        const
            classList   = element.classList,
            isString    = typeof newClasses === 'string',
            newClsArray = isString
                ? newClasses.trim().split(whiteSpaceRe)
                : (Array.isArray(newClasses)
                    ? newClasses
                    : ObjectHelper.getTruthyKeys(newClasses)),
            classCount  = newClsArray.length;

        let changed = classList.length !== classCount,
            i;

        // If the incoming and existing class lists are the same length
        // then check that each contains the same names. As soon as
        // we find a non-matching name, we know we have to update the
        // className.
        for (i = 0; !changed && i < classCount; i++) {
            //<debug>
            // Protect against IE throwing difficult to debug illegalCharacter errors
            // by validating that no className contains spaces.
            if (newClsArray[i].match(/\s/)) {
                throw new Error(`Illegal space character detected in CSS className ${newClsArray[i]}`);
            }
            //</debug>
            changed = !classList.contains(newClsArray[i]);
        }

        if (changed) {
            element.className = isString ? newClasses : newClsArray.join(' ');
        }
    }

    /**
     * Changes the theme to the passed theme name if possible.
     *
     * Theme names are case insensitive. The `href` used is all lower case.
     *
     * To use this method, the `<link rel="stylesheet">` _must_ use the default,
     * Bryntum-supplied CSS files where the `href` end with `<themeName>.css`, so that
     * it can be found in the document, and switched out for a new link with
     * the a modified `href`. The new `href` will use the same path, just
     * with the `themeName` portion subsituted for the new name.
     *
     * If no `<link>` with that name pattern can be found, an error will be thrown.
     *
     * If you use this method, you  must ensure that the theme files are
     * all accessible on your server.
     *
     * Because this is an asynchronous operation, a `Promise` is returned.
     * The theme change event is passed to the success function. If the
     * theme was not changed, because the theme name passed is the current theme,
     * nothing is passed to the success function.
     *
     * The theme change event contains two properties:
     *
     *  - `prev` The previous Theme name.
     *  - `theme` The new Theme name.
     *
     * @param {String} newThemeName
     * @returns {Promise} A promise who's success callback receives the theme change
     * event if the theme in fact changed. If the theme `href` could not be loaded,
     * the failure callback is called, passing the error event caught.
     */
    static setTheme(newThemeName) {
        newThemeName = newThemeName.toLowerCase();

        const
            oldThemeName = this.themeInfo.name.toLowerCase(),
            oldThemeLink =
                document.head.querySelector('#bryntum-theme') ||
                document.head.querySelector(`[href$="${oldThemeName}.css"]`);

        // Theme link href ends with <themeName>.css also there could be a query - css?11111...
        if (!oldThemeLink || !oldThemeLink.href.includes(`${oldThemeName}.css`)) {
            throw new Error(`Theme link for ${oldThemeName} not found`);
        }

        // Do not reapply same theme
        if (oldThemeLink.href.includes(newThemeName)) {
            return immediatePromise;
        }

        return new Promise((resolve, reject) => {
            const
                newThemeLink     = document.createElement('link'),
                nextSibling      = oldThemeLink.nextSibling,
                oldThemeName     = DomHelper.themeInfo.name.toLowerCase(),
                themeEvent       = {
                    theme : newThemeName,
                    prev  : oldThemeName
                },
                onThemeLoad      = () => {
                    themeInfo = null;
                    oldThemeLink.remove();
                    window.bryntum.GlobalEvents.trigger('theme', themeEvent);
                    resolve(themeEvent);
                },
                onThemeLoadError = e => {
                    reject(e);
                };

            newThemeLink.rel = 'stylesheet';
            newThemeLink.id = 'bryntum-theme';
            newThemeLink.addEventListener('load', onThemeLoad);
            newThemeLink.addEventListener('error', onThemeLoadError);
            newThemeLink.href = oldThemeLink.href.replace(oldThemeName, newThemeName);
            nextSibling.parentNode.insertBefore(newThemeLink, nextSibling);
        });
    }

    /**
     * A theme information object about the current theme.
     *
     * Currently this has only one property:
     *
     *   - `name` The current theme name.
     * @property {Object}
     * @readonly
     */
    static get themeInfo() {
        if (!themeInfo) {
            const
                testDiv   = this.createElement({
                    parent    : document.body,
                    className : 'b-theme-info'
                }),
                // Need to be a pseudo element for Edge to report content correctly
                themeData = this.getStyleValue(testDiv, 'content', false, ':before');

            if (themeData) {
                // themeData could be invalid JSON string in case there is no content rule
                try {
                    themeInfo = JSON.parse(themeData.replace(/^["']|["']$|\\/g, ''));
                } catch (e) {
                    themeInfo = null;
                }
            }

            testDiv.remove();
        }
        return themeInfo;
    }

    //endregion
}

let clearTouchTimer;
const
    clearTouchEvent = () => DomHelper.isTouchEvent = false,
    setTouchEvent   = () => {
        DomHelper.isTouchEvent = true;

        // Jump round the click delay
        clearTimeout(clearTouchTimer);
        clearTouchTimer = setTimeout(clearTouchEvent, 400);
    };

// Set event type flags so that mousedown and click handlers can know whether a touch gesture was used.
// This is used. This must stay until we have a unified DOM event system which handles both touch and mouse events.
doc.addEventListener('touchstart', setTouchEvent, true);
doc.addEventListener('touchend', setTouchEvent, true);

DomHelper.supportsTemplate = 'content' in doc.createElement('template');

//region Polyfills

// TODO: include babels polyfills instead of keeping own?

if (!('children' in Node.prototype)) {
    const elementFilter = node => node.nodeType === 1;
    Object.defineProperty(Node.prototype, 'children', {
        get : function() {
            return Array.prototype.filter.call(this.childNodes, elementFilter);
        }
    });
}

if (!Element.prototype.matches) {
    Element.prototype.matches =
        Element.prototype.matchesSelector ||
        Element.prototype.mozMatchesSelector ||
        Element.prototype.msMatchesSelector ||
        Element.prototype.oMatchesSelector ||
        Element.prototype.webkitMatchesSelector ||
        function(s) {
            let matches = (this.document || this.ownerDocument).querySelectorAll(s),
                i       = matches.length;
            while (--i >= 0 && matches.item(i) !== this) {}
            return i > -1;
        };
}

if (win.Element && !Element.prototype.closest) {
    Node.prototype.closest = Element.prototype.closest = function(s) {
        var el = this;
        if (!doc.documentElement.contains(el)) return null;

        do {
            if (el.matches(s)) return el;
            el = el.parentElement || el.parentNode;
        } while (el !== null && el.nodeType === 1);
        return null;
    };
}
else {
    // It's crazy that closest is not already on the Node interface!
    Node.prototype.closest = function(selector) {
        return this.parentNode.closest(selector);
    };
}

// from:https://github.com/jserz/js_piece/blob/master/DOM/ChildNode/remove()/remove().md
(function(arr) {
    arr.forEach(function(item) {
        if (item.hasOwnProperty('remove')) {
            return;
        }
        Object.defineProperty(item, 'remove', {
            configurable : true,
            enumerable   : true,
            writable     : true,
            value        : function remove() {
                this.parentNode && this.parentNode.removeChild(this);
            }
        });
    });
})([Element.prototype, CharacterData.prototype, DocumentType.prototype]);

// IE11 polyfill
if (!SVGElement.prototype.contains) {
    SVGElement.prototype.contains = function(node) {
        do {
            if (this === node) {
                return true;
            }
            node = node.parentNode;
        } while (node);

        return false;
    };
}

// IE11 polyfill for Event constructors
if (typeof win.CustomEvent !== 'function') {
    let evt, constructor;

    win.CustomEvent = constructor = function(event, params = {
        bubbles    : false,
        cancelable : false,
        detail     : undefined
    }) {
        evt = doc.createEvent('CustomEvent');
        evt.initCustomEvent(event, params.bubbles, params.cancelable, params.detail);
        return evt;
    };
    constructor.prototype = win.Event.prototype;

    win.MouseEvent = constructor = function(event, params = {
        bubbles    : false,
        cancelable : false,
        detail     : undefined
    }) {
        evt = doc.createEvent('MouseEvents');
        evt.initMouseEvent(event, params.bubbles, params.cancelable, doc.defaultView || win, params.detail, params.screenX, params.screenY, params.clientX, params.clientY, false, false, false, false, 0, document);
        return evt;
    };
    constructor.prototype = win.Event.prototype;

    win.KeyboardEvent = constructor = function(event, params = {
        bubbles    : false,
        cancelable : false,
        detail     : undefined
    }) {
        const modifiers = `${params.shiftKey ? 'Shift ' : ''}${params.ctrlKey ? 'Control' : ''}`;

        evt = doc.createEvent('KeyboardEvent');
        evt.initKeyboardEvent(event, params.bubbles, params.cancelable, doc.defaultView || win, params.key, params.location, modifiers, false, '');
        return evt;
    };
    constructor.prototype = win.Event.prototype;
}

//endregion

// https://gist.github.com/brettz9/4093766
if (!Object.getOwnPropertyDescriptor(SVGElement.prototype, 'dataset') ||
    !Object.getOwnPropertyDescriptor(SVGElement.prototype, 'dataset').get) {
    var propDescriptor = {
        enumerable : true,
        get        : function() {
            'use strict';
            var i,
                that        = this,
                map         = {},
                attrVal, attrName, propName,
                attribute,
                attributes  = this.attributes,
                attsLength  = attributes.length,
                toUpperCase = function(n0) {
                    return n0.charAt(1).toUpperCase();
                },
                getter      = function() {
                    return this;
                },
                setter      = function(attrName, value) {
                    return (typeof value !== 'undefined')
                        ? this.setAttribute(attrName, value)
                        : this.removeAttribute(attrName);
                };

            for (i = 0; i < attsLength; i++) {
                attribute = attributes[i];
                // Fix: This test really should allow any XML Name without
                //         colons (and non-uppercase for XHTML)
                if (attribute && attribute.name &&
                    (/^data-\w[\w-]*$/).test(attribute.name)) {
                    attrVal = attribute.value;
                    attrName = attribute.name;
                    // Change to CamelCase
                    propName = attrName.substr(5).replace(/-./g, toUpperCase);
                    Object.defineProperty(map, propName, {
                        enumerable : this.enumerable,
                        get        : getter.bind(attrVal || ''),
                        set        : setter.bind(that, attrName)
                    });
                }
            }
            return map;
        }
    };
    // FF enumerates over element's dataset, but not
    // SVGElement.prototype.dataset; IE9 iterates over both
    Object.defineProperty(SVGElement.prototype, 'dataset', propDescriptor);
}

// Polyfill to allow an array to be passed to classList.add/remove
const
    nativeAdd    = DOMTokenList.prototype.add,
    nativeRemove = DOMTokenList.prototype.remove;

DOMTokenList.prototype.add = function(cls) {
    if (Array.isArray(cls)) {
        nativeAdd.call(this, ...cls);
    }
    else {
        nativeAdd.call(this, ...arguments);
    }
};
DOMTokenList.prototype.remove = function(cls) {
    if (Array.isArray(cls)) {
        nativeRemove.call(this, ...cls);
    }
    else {
        nativeRemove.call(this, ...arguments);
    }
};
