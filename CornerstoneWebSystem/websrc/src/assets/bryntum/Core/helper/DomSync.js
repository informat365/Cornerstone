import ArrayHelper from './ArrayHelper.js';
import DomHelper from './DomHelper.js';
import ObjectHelper from './ObjectHelper.js';
import StringHelper from './StringHelper.js';

/**
 * @module Core/helper/DomSync
 */

const
    arraySlice            = Array.prototype.slice,
    // Used in sync to give ObjectHelper.isDeeplyEqual() some domain knowledge
    syncEqualityEvaluator = {
        // Attributes used during creation that should not be compared
        ignore : {
            '_element'    : 1,
            'parent'      : 1,
            'elementData' : 1,
            'ns'          : 1
        },
        // Function to evaluate 'compareHtml' property instead of 'html' for DocumentFragments
        evaluate(property, a, b) {
            if (property === 'html' && typeof a.value !== 'string' && `compareHtml` in a.object) {
                // DocumentFragment, compare separately supplied html
                return (a.object.compareHtml === b.object.compareHtml);
            }
        }
    },
    // Attributes to ignore on sync
    syncIgnoreAttributes  = {
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
        syncOptions   : 1,
        isReleased    : 1
    },
    isClass               = {
        'class'   : 1,
        className : 1,
        classname : 1
    },
    emptyArray            = Object.freeze([]);

/**
 * A utility class for syncing DOM config objects to DOM elements. Syncing compares the new config with the previously
 * used for that element, only applying the difference. Very much like a virtual DOM approach on a per element basis
 * (element + its children).
 *
 * Usage example:
 *
 * ```javascript
 * DomSync.sync({
 *     domConfig: {
 *         className : 'b-outer',
 *         children : [
 *             {
 *                 className : 'b-child',
 *                 html      : 'Child 1',
 *                 dataset   : {
 *                     custom : true
 *                 }
 *             },
 *             {
 *                 className : 'b-child',
 *                 html      : 'Child 2',
 *                 style     : {
 *                     fontWeight : 'bold',
 *                     color      : 'blue'
 *                 }
 *             }
 *         ]
 *     },
 *     targetElement : target
 * });
 * ```
 */
export default class DomSync {
    /**
     * Sync a DOM config to a target element
     * @param {Object} options Options object
     * @param {Object} options.domConfig A DOM config object
     * @param {HTMLElement} options.targetElement Target element to apply to
     * @param {String} [options.syncIdField] Field in dataset to use to match elements for re-usage
     * @param {Function} [options.callback] A function that will be called on element re-usage, creation and similar
     * @param {Boolean} [options.configEquality] A function that will be called to compare an incoming config to
     * the last config applied to the `targetElement`. Defaults to {@link Core.helper.ObjectHelper#function-isDeeplyEqual-static isDeeplyEqual}.
     * @returns {HTMLElement} Returns the updated target element (which is also updated in place)
     */
    static sync(options) {
        this.performSync(options, options.targetElement);

        return options.targetElement;
    }

    static performSync(options, targetElement) {
        const
            { domConfig, callback } = options,
            configIsEqual           = options.configEquality ?
                options.configEquality(domConfig, targetElement.lastDomConfig, syncEqualityEvaluator) :
                ObjectHelper.isDeeplyEqual(domConfig, targetElement.lastDomConfig, syncEqualityEvaluator);

        if (!configIsEqual) {
            if (domConfig) {
                // Sync without affecting the containing element?
                if (!domConfig.onlyChildren) {
                    this.syncAttributes(domConfig, targetElement);
                    this.syncContent(domConfig, targetElement);
                }

                this.syncChildren(options, targetElement);

                // Link the element for easy retrieval later
                domConfig._element = targetElement;
            }
            // Allow null to clear html
            else {
                targetElement.innerHTML = null;
                targetElement.syncIdMap = null;
            }

            // Cache the config on the target for future comparison
            targetElement.lastDomConfig = domConfig;

            return true;
        }
        else {
            // Maintain link to element (deep)
            this.relinkElements(domConfig, targetElement);
            // Sync took no action, notify the world
            callback && callback({
                action : 'none',
                domConfig,
                targetElement
            });
        }

        return false;
    }

    // Called from sync when there is no change to elements, to set up link between new config and existing element.
    // Plucks the element from the last applied config, no need to hit DOM so is cheap
    static relinkElements(domConfig, targetElement) {
        domConfig._element = targetElement;

        // Since there was no change detected, there is a 1 to 1 ratio between new config and last config, should be
        // safe to do a straight mapping
        domConfig.children && domConfig.children.forEach((childDomConfig, i) => {
            // Skip null entries, allowed for convenience, neat with map.
            // Also skip text nodes
            if (childDomConfig && typeof childDomConfig !== 'string') {
                this.relinkElements(childDomConfig, targetElement.lastDomConfig.children[i]._element);
            }
        });
    }

    //region Attributes

    static syncDataset(domConfig, targetElement) {
        const
            { lastDomConfig } = targetElement,
            sameConfig        = domConfig === lastDomConfig,
            source            = Object.keys(domConfig.dataset),
            target            = lastDomConfig && lastDomConfig.dataset && Object.keys(lastDomConfig.dataset),
            delta             = ArrayHelper.delta(source, target);

        // New attributes in dataset
        for (let i = 0; i < delta.onlyInA.length; i++) {
            const attr = delta.onlyInA[i];
            targetElement.setAttribute(`data-${StringHelper.hyphenate(attr)}`, domConfig.dataset[attr]);
        }

        // Might have changed
        for (let i = 0; i < delta.inBoth.length; i++) {
            const attr = delta.inBoth[i];
            // Intentional != since dataset is always string but want numbers to match
            // noinspection EqualityComparisonWithCoercionJS
            if (sameConfig || domConfig.dataset[attr] != lastDomConfig.dataset[attr]) {
                targetElement.setAttribute(`data-${StringHelper.hyphenate(attr)}`, domConfig.dataset[attr]);
            }
        }

        // Removed
        for (let i = 0; i < delta.onlyInB.length; i++) {
            targetElement.removeAttribute(`data-${StringHelper.hyphenate(delta.onlyInB[i])}`)
        }
    }

    static syncClassList(domConfig, targetElement) {
        const
            attr            = domConfig.className || domConfig.class,
            classNameString = typeof attr === 'object' ?
                ObjectHelper.getTruthyKeys(attr).join(' ') :
                attr;

        targetElement.setAttribute('class', classNameString);
    }

    // Attributes as map { attr : value, ... }
    static getSyncAttributes(domConfig) {
        const
            attributes = {},
            // Attribute names, simplifies comparisons and calls to set/removeAttribute
            names      = [];

        // On a first sync, there are no domConfig on the target element yet
        if (domConfig) {
            Object.keys(domConfig).forEach(attr => {
                if (!syncIgnoreAttributes[attr]) {
                    const name = attr.toLowerCase();
                    attributes[name] = domConfig[attr];
                    names.push(name);
                }
            });
        }

        return { attributes, names };
    }

    static syncAttributes(domConfig, targetElement) {
        const
            { lastDomConfig } = targetElement,
            // If the same config has come through, due to configEquality, we must update all attrs.
            sameConfig        = domConfig === lastDomConfig,
            sourceSyncAttrs   = this.getSyncAttributes(domConfig),
            // Extract attributes from elements (sourceElement might be a config)
            {
                attributes : sourceAttributes,
                names      : sourceNames
            }                 = sourceSyncAttrs,
            {
                attributes : targetAttributes,
                names      : targetNames
            }                 = sameConfig ? sourceSyncAttrs : this.getSyncAttributes(lastDomConfig),
            // Intersect arrays to determine what needs adding, removing and syncing
            {
                onlyInA : toAdd,
                onlyInB : toRemove,
                inBoth  : toSync
            }                 = sameConfig ? {
                onlyInA : emptyArray,
                onlyInB : emptyArray,
                inBoth  : sourceNames
            } : ArrayHelper.delta(sourceNames, targetNames);

        // Add new attributes
        for (let i = 0; i < toAdd.length; i++) {
            const
                attr       = toAdd[i],
                sourceAttr = sourceAttributes[attr];

            // Style requires special handling
            if (attr === 'style') {
                // TODO: Do diff style apply also instead of this replace
                DomHelper.applyStyle(targetElement, sourceAttr, true);
            }
            // So does dataset
            else if (attr === 'dataset') {
                this.syncDataset(domConfig, targetElement);
            }
            // And class, which might be an object
            else if (isClass[attr]) {
                this.syncClassList(domConfig, targetElement);
            }
            // Other attributes are set using setAttribute (since it calls toString() DomClassList works fine)
            else {
                targetElement.setAttribute(attr, sourceAttr);
            }
        }

        // Removed no longer used attributes
        for (let i = 0; i < toRemove.length; i++) {
            targetElement.removeAttribute(toRemove[i]);
        }

        // TODO: toAdd and toSync are growing very alike, consider merging
        // Sync values for all other attributes
        for (let i = 0; i < toSync.length; i++) {
            const
                attr       = toSync[i],
                sourceAttr = sourceAttributes[attr],
                targetAttr = targetAttributes[attr];

            // Attribute value null means remove attribute
            if (sourceAttr == null) {
                targetElement.removeAttribute(attr);
            }
            // Set all attributes that has changed, with special handling for style.
            else if (attr === 'style') {
                if (sameConfig || !ObjectHelper.isEqual(sourceAttr, targetAttr, true)) {
                    // TODO: Do diff style apply also instead of this replace
                    DomHelper.applyStyle(targetElement, sourceAttr, true);
                }
            }
            // And dataset
            else if (attr === 'dataset') {
                this.syncDataset(domConfig, targetElement);
            }
            // And class, which might be an object
            else if (isClass[attr]) {
                this.syncClassList(domConfig, targetElement);
            }
            else if (sameConfig || sourceAttr !== targetAttr) {
                targetElement.setAttribute(attr, sourceAttr);
            }
        }
    }

    //endregion

    //region Content

    static syncContent(domConfig, targetElement) {

        const { html } = domConfig;

        // elementData holds custom data that we want to attach to the element (not visible in dom)
        if (domConfig.elementData) {
            targetElement.elementData = domConfig.elementData;
        }

        // Apply html from config
        if (html != null) {
            // If given a DocumentFragment, replace content with it
            if (html instanceof DocumentFragment) {
                // Syncing a textNode to a textNode? Use shortcut
                if (
                    targetElement.childNodes.length === 1 &&
                    targetElement.childElementCount === 0 &&
                    html.childNodes.length === 1 &&
                    html.childElementCount === 0
                ) {
                    DomHelper.setInnerText(targetElement, html.firstChild.data);
                }
                else {
                    targetElement.innerHTML = '';
                    targetElement.appendChild(html);
                }
            }
            // Something that might be html, set innerHTML
            else if (String(html).includes('<')) {
                targetElement.innerHTML = html;
            }
            // Plain text, prefer setting data on first text node
            else {
                DomHelper.setInnerText(targetElement, html);
            }
        }
    }

    static appendTextNode(text, targetElement, callback) {
        const newNode = document.createTextNode(text);
        targetElement.appendChild(newNode);

        callback && callback({
            action        : 'newNode',
            domConfig     : text,
            targetElement : newNode
        });

    }

    static insertElement(domConfig, targetElement, targetNode, syncId, options) {
        // Create a new element
        const newElement = options.ns ?
            document.createElementNS(options.ns, domConfig.tag || 'svg') :
            document.createElement(domConfig.tag || 'div');

        // Insert (or append if no targetNode)
        targetElement.insertBefore(newElement, targetNode);

        // Sync to it
        this.performSync(options, newElement);

        if (syncId != null) {
            targetElement.syncIdMap[syncId] = newElement;
        }

        options.callback && options.callback({
            action        : 'newElement',
            domConfig,
            targetElement : newElement,
            syncId
        });
    }

    //endregion

    //region Children

    static syncChildren(options, targetElement) {
        let { domConfig, syncIdField, callback, releaseThreshold, configEquality, ns } = options;

        // Having specified html replaces all inner content, no point in syncing
        if (domConfig.html) {
            return;
        }

        const
            me            = this,
            sourceConfigs = arraySlice.call(domConfig.children || []),
            targetNodes   = arraySlice.call(targetElement.childNodes),
            syncIdMap     = targetElement.syncIdMap || {},
            syncOptions   = domConfig.syncOptions || {};

        // Each level can optionally specify its own syncIdField and callback, if left out parent levels will be used
        syncIdField = syncOptions.syncIdField || syncIdField;
        callback = syncOptions.callback || callback;
        configEquality = syncOptions.configEquality || configEquality;
        // Make sure releaseThreshold 0 is respected...
        releaseThreshold = 'releaseThreshold' in syncOptions ? syncOptions.releaseThreshold : releaseThreshold;

        let syncId;

        // Always repopulate the map, since elements might get used by other syncId below
        if (syncIdField) {
            targetElement.syncIdMap = {};
        }

        // Settings to use in all syncs below
        const syncChildOptions = {
            syncIdField,
            releaseThreshold,
            callback,
            configEquality
        };

        while (sourceConfigs.length) {
            const sourceConfig = sourceConfigs.shift();

            syncId = null;

            // Allowing null, convenient when using Array.map() to generate children
            if (!sourceConfig) {
                continue;
            }

            const isTextNode = typeof sourceConfig === 'string';

            // Used in all syncs
            syncChildOptions.domConfig = sourceConfig;
            syncChildOptions.ns = sourceConfig.ns || ns

            if (!isTextNode) {
                // If syncIdField was supplied, we should first try to reuse element with matching "id"
                if (syncIdField && sourceConfig.dataset) {
                    syncId = sourceConfig.dataset[syncIdField];
                    // We have an id to look for
                    if (syncId != null && !sourceConfig.unmatched) {
                        // Find any matching element
                        const syncTargetElement = syncIdMap[syncId];
                        if (syncTargetElement) {
                            if (
                                // Ignore if flagged with `retainElement` (for example during dragging)
                                !sourceConfig.retainElement &&
                                // Otherwise sync with the matched element
                                me.performSync(syncChildOptions, syncTargetElement)
                            ) {
                                // Sync took some action, notify the world
                                callback && callback({
                                    action        : 'reuseOwnElement',
                                    domConfig     : sourceConfig,
                                    targetElement : syncTargetElement,
                                    syncId
                                });
                            }

                            // Since it wont sync above when flagged to be retained, we need to apply the flag here
                            if (sourceConfig.retainElement) {
                                syncTargetElement.retainElement = true;
                                // Normally linked in performSync(), but for retained elements that fn is not called
                                sourceConfig._element = syncTargetElement;
                            }
                            // And remove it when no longer needed
                            else if (syncTargetElement.retainElement) {
                                syncTargetElement.retainElement = false;
                            }

                            // Cache the element on the syncId for faster retrieval later
                            targetElement.syncIdMap[syncId] = syncTargetElement;

                            // Remove our target from targetElements, no-one else is allowed to sync with it
                            ArrayHelper.remove(targetNodes, syncTargetElement);

                            syncTargetElement.isReleased = false;
                        }
                        else {
                            // No match, move to end of queue to not steal some one else's element
                            sourceConfigs.push(sourceConfig);
                            // Also flag as unmatched to know that when we reach this element again
                            sourceConfig.unmatched = true;
                        }
                        // Node handled, carry on with next one
                        continue;
                    }
                }

                // Avoid polluting the config object when done
                if (sourceConfig.unmatched) {
                    delete sourceConfig.unmatched;
                }
            }

            // Skip over any retained elements
            let targetNode;

            for (let i = 0; i < targetNodes.length && !targetNode; i++) {
                if (!targetNodes[i].retainElement) {
                    targetNode = targetNodes[i];
                    // shift is much faster than splice...
                    if (i === 0) {
                        targetNodes.shift();
                    }
                    else {
                        targetNodes.splice(i, 1);
                    }
                }
            }

            // Out of target nodes, add to target
            if (!targetNode) {
                if (isTextNode) {
                    this.appendTextNode(sourceConfig, targetElement, callback);
                }
                else {
                    // Will append
                    this.insertElement(sourceConfig, targetElement, null, syncId, syncChildOptions);
                }
            }
            // We have targets left
            else {
                // Matching element tag, sync it
                if (
                    targetNode.nodeType === Node.ELEMENT_NODE &&
                    (sourceConfig.tag || 'div').toLowerCase() === targetNode.tagName.toLowerCase()
                ) {
                    const
                        { lastDomConfig } = targetNode,
                        result            = me.performSync(syncChildOptions, targetNode);

                    if (syncId != null) {
                        targetElement.syncIdMap[syncId] = targetNode;
                    }

                    targetNode.isReleased = false;

                    // Only use callback if sync succeeded (anything changed)
                    result && callback && callback({
                        action        : 'reuseElement',
                        domConfig     : sourceConfig,
                        lastDomConfig,
                        targetElement : targetNode,
                        syncId
                    });
                }
                // Text node to text node, change text :)
                else if (isTextNode && targetNode.nodeType === Node.TEXT_NODE) {
                    targetNode.data = sourceConfig;

                    // Not using callback for updating text of node, have no usecase for it currently
                }
                // Not matching, replace it
                else {
                    if (isTextNode) {
                        this.appendTextNode(sourceConfig, targetElement, callback);
                    }
                    else {
                        // Will insert
                        this.insertElement(sourceConfig, targetElement, targetNode, syncId, syncChildOptions);
                    }

                    targetNode.remove();
                }
            }
        }

        let releaseCount = 0;

        // Out of source nodes, remove remaining target nodes
        targetNodes.forEach(targetNode => {
            const { lastDomConfig } = targetNode;

            // Element might be retained, hands off (for example while dragging)
            if (!targetNode.retainElement) {
                // When using syncId to reuse elements, "release" left over elements instead of removing them, up to a
                // limit specified as releaseThreshold, above which elements are removed instead
                if (syncIdField && (releaseThreshold == null || releaseCount < releaseThreshold)) {
                    // Prevent releasing already released element
                    if (!targetNode.isReleased) {
                        targetNode.className = 'b-released';
                        targetNode.isReleased = true;

                        callback && callback({
                            action        : 'releaseElement',
                            domConfig     : lastDomConfig,
                            lastDomConfig,
                            targetElement : targetNode
                        });

                        // Done after callback on purpose, to allow checking old className
                        if (lastDomConfig) {
                            // Make sure lastDomConfig differs even from the same domConfig applied again
                            // Do not want to discard it completely since it is needed for diff when reused later
                            lastDomConfig.isReleased = true;

                            // To force reapply of classes on reuse
                            if (lastDomConfig.className) {
                                lastDomConfig.className = 'b-released';
                            }
                        }

                        targetNode.elementData = targetNode.lastConfig = null;
                    }

                    releaseCount++;
                }
                // In normal sync mode, remove left overs
                else {
                    targetNode.remove();

                    callback && callback({
                        action        : 'removeElement',
                        domConfig     : targetNode.lastDomConfig,
                        lastDomConfig : targetNode.lastDomConfig,
                        targetElement : targetNode
                    });
                }
            }
            else if (syncIdField) {
                // Keep retained element in map
                if (targetNode.lastDomConfig) {
                    targetElement.syncIdMap[targetNode.dataset[syncIdField]] = targetNode;
                }
            }
        });
    }

    //endregion
}
