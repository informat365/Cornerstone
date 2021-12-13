import Base from '../../Base.js';
import Model from '../Model.js';

/**
 * @module Core/data/mixin/TreeNode
 */

/**
 * Mixin for Model with tree node related functionality. This class is mixed into the {@link Core/data/Model} class.
 *
 * ## Adding and removing child nodes
 * ```
 * const parent = store.getById(1),
 *
 * firstBorn = parent.insertChild({
 *      name : 'Child node'
 *  }, parent.children[0]); // Insert a child at a specific place in the children array
 *
 * parent.removeChild(parent.children[0]); // Removes a child node
 * parent.appendChild({ name : 'New child node' }); // Appends a child node
 * ```
 *
 * @mixin
 */
export default Target => class TreeNode extends (Target || Base) {
    /**
     * This static configuration option allows you to control whether an empty parent task should be converted into a
     * leaf. Enable/disable it for a whole class:
     *
     * ```javascript
     * Model.convertEmptyParentToLeaf = false;
     * ```
     *
     * By specifying `true`, all empty parents will be considered leafs. Can also be assigned a configuration object
     * with with the following Boolean properties to customize the behaviour:
     *
     * * `onLoad` - Apply the transformation on load to any parents without children (`children : []`)
     * * `onRemove` - Apply the transformation when all children have been removed from a parent
     *
     * ```javascript
     * Model.convertEmptyParentToLeaf = {
     *     onLoad   : false,
     *     onRemove : true
     * }
     * ```
     *
     * @property {Boolean|Object}
     * @default false
     * @category Parent & children
     */
    static set convertEmptyParentToLeaf(value) {
        if (value === true) {
            value = {
                onLoad   : true,
                onRemove : true
            };
        }
        else if (value === false) {
            value = {
                onLoad   : false,
                onRemove : false
            };
        }
        this._convertEmptyParentToLeaf = value;
    }

    static get convertEmptyParentToLeaf() {
        return this._convertEmptyParentToLeaf || { onLoad : false, onRemove : false };
    }

    /**
     * This is a read-only property providing access to the parent node.
     * @member {Core.data.Model} parent
     * @readonly
     * @category Parent & children
     */

    /**
     * This is a read-only field provided in server synchronization packets to specify
     * which record id is the parent of the record.
     * @readonly
     * @field {String|Number} parentId
     */

    ingestChildren(childRecord, stores = this.stores) {
        const
            { inProcessChildren, constructor : MyClass } = this,
            store = stores[0];

        if (childRecord === true) {
            if (inProcessChildren) {
                return true;
            }
            return [];
        }
        if (childRecord) {
            if (!Array.isArray(childRecord)) {
                childRecord = [childRecord];
            }
            let i = 0,
                len = childRecord.length,
                result = [],
                child;

            for (; i < len; i++) {
                child = childRecord[i];
                child = child instanceof Model ? child : (store ? store.createRecord(child) : new MyClass(child, null, null, true));
                result.push(child);
            }
            return result;
        }
    }

    /**
     * Child nodes. To allow loading children on demand, specify `children : true` in your data.
     * @member {Core.data.Model[]} children
     * @category Parent & children
     */

    /**
     * Called during creation to also turn any children into Models joined to the same stores as this model
     * @internal
     * @category Parent & children
     */
    processChildren(stores = this.stores) {
        const
            me = this,
            { meta } = me;

        me.inProcessChildren = true;

        const children = me.ingestChildren(me.data[me.constructor.childrenField], stores);

        if (children) {
            if (children.length) {
                meta.isLeaf = false;
                // We are processing a remote load
                if (me.children === true) {
                    me.children = [];
                }
                me.appendChild(children);
            }
            // Flagged for load on demand
            else if (children === true) {
                meta.isLeaf = false;
                me.children = true;
            }
            // Empty child array, flag is leaf if configured to do so
            else if (!me.isRoot) {
                meta.isLeaf = me.constructor.convertEmptyParentToLeaf.onLoad;
            }
        }

        me.inProcessChildren = false;
    }

    /**
     * This property is `true` if this record has all expanded ancestors and is therefore
     * eligible for inclusion in a UI.
     * @property {Boolean}
     * @readonly
     * @category Parent & children
     */
    ancestorsExpanded(store) {
        const { parent } = this;

        return !parent || (parent.isExpanded(store) && parent.ancestorsExpanded(store));
    }

    /**
     * Used by stores to assess the record's collapsed/expanded state in that store.
     * @param {Core.data.Store} store
     * @category Parent & children
     */
    isExpanded(store) {
        const mapMeta = this.instanceMeta(store.id);

        // Default initial expanded/collapsed state when in the store
        // to the record's original expanded property.
        if (!mapMeta.hasOwnProperty('collapsed')) {
            mapMeta.collapsed = !this.expanded;
        }

        return !mapMeta.collapsed;
    }

    // A read-only property. It provides the initial state upon load
    // The UI's expanded/collapsed state is in the store's meta map.
    get expanded() {
        return this.data.expanded;
    }

    /**
     * Depth in the tree at which this node exists. First visual level of nodes are at level 0, their direct children at
     * level 1 and so on.
     * @property {Number}
     * @readonly
     * @category Parent & children
     */
    get childLevel() {
        return this.parent ? this.parent.childLevel + 1 : (this.isRoot ? -1 : 0);
    }

    /**
     * Is a leaf node in a tree structure?
     * @property {Boolean}
     * @readonly
     * @category Parent & children
     */
    get isLeaf() {
        return this.meta.isLeaf !== false && !this.isRoot;
    }

    /**
     * Is a parent node in a tree structure?
     * @property {Boolean}
     * @readonly
     * @category Parent & children
     */
    get isParent() {
        return !this.isLeaf;
    }

    /**
     * Returns true for parent nodes with children loaded (there might still be no children)
     * @property {Boolean}
     * @readonly
     * @category Parent & children
     */
    get isLoaded() {
        return this.isParent && Array.isArray(this.children);
    }

    /**
     * Count all children (including sub-children) for a node (in its `firstStore´)
     * @member {Number}
     * @category Parent & children
     */
    get descendantCount() {
        return this.getDescendantCount();
    }

    /**
     * Count visible (expanded) children (including sub-children) for a node (in its `firstStore`)
     * @member {Number}
     * @category Parent & children
     */
    get visibleDescendantCount() {
        return this.getDescendantCount(true);
    }

    /**
     * Count visible (expanded)/all children for this node, optionally specifying for which store.
     * @param {Boolean} [onlyVisible] Specify `true` to only count visible (expanded) children.
     * @param {Core.data.Store} [store] A Store to which this node belongs
     * @returns {Number}
     * @category Parent & children
     */
    getDescendantCount(onlyVisible = false, store = this.firstStore) {
        const children = this.children;

        if (!children || !Array.isArray(children) || (onlyVisible && !this.isExpanded(store))) {
            return 0;
        }

        return children.reduce((count, child) => count + child.getDescendantCount(onlyVisible), children.length);
    }

    /**
     * Retrieve all children (by traversing sub nodes)
     * @returns {Core.data.Model[]}
     * @category Parent & children
     */
    get allChildren() {
        const children = this.children;
        if (!children) return [];

        return children.reduce((all, child) => {
            all.push(child);

            // push.apply is faster than push with array spread:
            // https://jsperf.com/push-apply-vs-push-with-array-spread/1
            all.push.apply(all, child.allChildren);
            return all;
        }, []);
    }

    /**
     * Get the first child of this node
     * @returns {Core.data.Model}
     * @category Parent & children
     */
    get firstChild() {
        const children = this.children;

        return (children && children.length && children[0]) || null;
    }

    /**
     * Get the last child of this node
     * @returns {Core.data.Model}
     * @category Parent & children
     */
    get lastChild() {
        const children = this.children;

        return (children && children.length && children[children.length - 1]) || null;
    }

    /**
     * Returns count of all preceding sibling nodes (including their children).
     * @property {Number}
     * @category Parent & children
     */
    get previousSiblingsTotalCount() {
        let task  = this.previousSibling,
            count = this.parentIndex;

        while (task) {
            count += task.descendantCount;
            task = task.previousSibling;
        }

        return count;
    }

    get root() {
        return this.parent && this.parent.root || this;
    }

    /**
     * Reading this property returns the id of the parent node, if this record is a child of a node.
     *
     * Setting this property appends this record to the record with the passed
     * id **in the same store that this record is already in**.
     *
     * Note that setting this property is **only valid if this record is already
     * part of a tree store**.
     *
     * This is not intended for general use. This is for when a server responds to a record
     * mutation and the server decides to move a record to a new parent. If a `parentId` property
     * is passed in the response data for a record, that record will be moved.
     * @property {Number|String}
     * @category Parent & children
     */
    get parentId() {
        return this.parent && !this.parent.isRoot ? this.parent.id : null;
    }

    set parentId(parentId) {
        const
            me = this,
            { parent } = me,
            newParent = parentId && me.firstStore.getById(parentId);

        // Handle exact equality of parent.
        // Also handle one being null and the other being undefined meaning no change.
        if (!(newParent === parent || (!parent && !newParent))) {
            // If we are batching, we do not trigger a change immediately.
            // endBatch will set the field which will set the property again.
            if (me.isBatchUpdating) {
                me.meta.batchChanges.parentId = parentId;
            }
            else {
                if (newParent) {
                    newParent.appendChild(me);
                }
                else {
                    me.parent.removeChild(me);
                }
            }
        }
    }

    static set parentIdField(parentIdField) {
        // Maintainer: the "this" references in here reference two different contexts.
        // Outside of the property definition, it's the Model Class.
        // In the getter and setter, it's the record instance.
        this._parentIdField = parentIdField;

        Object.defineProperty(this.prototype, parentIdField, {
            set : function(parentId) {
                // no arrow functions here, need `this` to change to instance
                // noinspection JSPotentiallyInvalidUsageOfClassThis
                this.parentId = parentId;
            },
            get : function() {
                // no arrow functions here, need `this` to change to instance
                // noinspection JSPotentiallyInvalidUsageOfClassThis
                return this.parentId;
            }
        });
    }

    static get parentIdField() {
        return this._parentIdField || 'parentId';
    }

    /**
     * Traverses all child nodes recursively calling the passed function
     * on a target node **before** iterating the child nodes.
     * @param fn
     * @category Parent & children
     */
    traverse(fn, skipSelf = false) {
        const { children } = this;

        if (!skipSelf) {
            fn.call(this, this);
        }

        // Simply testing whether there is non-zero children length
        // is 10x faster than using this.isLoaded
        for (let i = 0, l = children && children.length; i < l; i++) {
            children[i].traverse(fn);
        }
    }

    /**
     * Traverses all child nodes recursively calling the passed function
     * on child nodes of a target **before** calling it it on the node.
     * @param fn
     * @category Parent & children
     */
    traverseBefore(fn, skipSelf = false) {
        const { children } = this;

        // Simply testing whether there is non-zero children length
        // is 10x faster than using this.isLoaded
        for (let i = 0, l = children && children.length; i < l; i++) {
            children[i].traverse(fn);
        }
        if (!skipSelf) {
            fn.call(this, this);
        }
    }

    /**
     * Traverses child nodes recursively while fn returns true
     * @param {Function} fn
     * @category Parent & children
     * @returns {Boolean}
     */
    traverseWhile(fn, skipSelf = false) {
        const me = this;

        let goOn = true;

        if (!skipSelf) {
            goOn = fn.call(me, me) !== false;
        }

        if (goOn && me.isLoaded) {
            goOn = me.children.every(child => child.traverseWhile(fn));
        }

        return goOn;
    }

    /**
     * Bubbles up from this node, calling the specified function with each node.
     *
     * @param {Function} fn
     * @category Parent & children
     */
    bubble(fn, skipSelf = false) {
        let me = this;

        if (!skipSelf) {
            fn.call(me, me);
        }

        while (me.parent) {
            me = me.parent;
            fn.call(me, me);
        }
    }

    /**
     * Bubbles up from this node, calling the specified function with each node,
     * while the function returns true.
     *
     * @param {Function} fn
     * @category Parent & children
     * @return {Boolean}
     */
    bubbleWhile(fn, skipSelf = false) {
        let me = this,
            goOn = true;

        if (!skipSelf) {
            goOn = fn.call(me, me);
        }

        while (goOn && me.parent) {
            me = me.parent;
            goOn = fn.call(me, me);
        }

        return goOn;
    }

    /**
     * Checks if this model contain another model as one of it's descendants
     *
     * @param {Core.data.Model|String|Number} childOrId child node or id
     * @category Parent & children
     * @returns {Boolean}
     */
    contains(childOrId) {
        if (childOrId && typeof childOrId === 'object') {
            childOrId = childOrId.id;
        }
        return !this.traverseWhile(node => node.id != childOrId);
    }

    getTopParent(all) {
        let result;

        if (all) {
            result = [];
            this.bubbleWhile((t) => {
                result.push(t);
                return t.parent && !t.parent.isRoot;
            });
        }
        else {
            result = null;
            this.bubbleWhile((t) => {
                if (!t.parent) {
                    result = t;
                }
                return t.parent && !t.parent.isRoot;
            });
        }

        return result;
    }

    /**
     * Append a child record(s) to any current children.
     * @param {Core.data.Model|Core.data.Model[]} childRecord Record or array of records to append
     * @param {Boolean} [silent] Pass `true` to not trigger events during append
     * @returns {Core.data.Model|Core.data.Model[]}
     * @category Parent & children
     */
    appendChild(childRecord, silent = false) {
        return this.insertChild(childRecord, null, silent);
    }

    /**
     * Insert a child record(s) before an existing child record.
     * @param {Core.data.Model|Core.data.Model[]} childRecord Record or array of records to insert
     * @param {Core.data.Model} [before] Optional record to insert before, leave out to append to the end
     * @param {Boolean} [silent] Pass `true` to not trigger events during append
     * @returns {Core.data.Model|Core.data.Model[]}
     * @category Parent & children
     */
    insertChild(childRecord, beforeRecord = null, silent = false) {
        // Handle deprecated signature
        if (typeof childRecord === 'number') {
            const index = childRecord;
            childRecord = beforeRecord;
            beforeRecord = this.children[index];
        }

        const
            me          = this,
            wasLeaf     = me.isLeaf,
            returnArray = Array.isArray(childRecord);

        if (!Array.isArray(childRecord)) childRecord = [childRecord];

        if (!silent) {
            if (!me.stores.every(s => s.trigger('beforeAdd', { records : childRecord, parent : me }) !== false)) {
                return null;
            }
        }

        // This call makes child record an array containing Models
        childRecord = me.ingestChildren(childRecord);

        // NOTE: see comment in Model::set() about before/in/after calls approach.
        const
            index     = beforeRecord ? beforeRecord.parentIndex : me.children ? me.children.length : 0,
            preResult = me.beforeInsertChild ? me.beforeInsertChild(childRecord) : undefined,
            inserted  = me.internalAppendInsert(childRecord, beforeRecord, silent);

        // Turn into a parent if not already one
        if (wasLeaf && inserted.length) {
            me.meta.isLeaf = false;
        }

        // If we've transitioned to being a branch node, signal a change event
        // so that the UI updates.
        // Not if it's due to root node loading. StoreTree#onNodeAddChild
        // for the rootNode will fire a store refresh.
        if (me.isLeaf !== wasLeaf && !me.root.isLoading && !silent) {
            me.stores.forEach(s => {
                const changes = {
                    isLeaf : {
                        value : false,
                        oldValue : true
                    }
                };
                s.trigger('update', { record : me, changes });
                s.trigger('change', { action : 'update', record : me, changes });
            });
        }

        me.afterInsertChild && me.afterInsertChild(index, childRecord, preResult, inserted);

        return (returnArray || !inserted) ? inserted : inserted[0];
    }

    internalAppendInsert(newRecords, beforeRecord, silent) {
        const
            me = this,
            { stores, root } = me,
            { firstStore : rootStore } = root,
            isMove = {};

        let isNoop, start, i, newRecordsCloned;

        // The reference node must be one of our children. If not, fall back to an append.
        if (beforeRecord && beforeRecord.parent !== me) {
            beforeRecord = null;
        }

        // If the records starting at insertAt or (insertAt - 1), are the same sequence
        // that we are being asked to add, this is a no-op.
        if (me.children) {
            const
                children = me.children,
                insertAt = beforeRecord ? beforeRecord.parentIndex : children.length;

            if (children[start = insertAt] === newRecords[0] || children[start = insertAt - 1] === newRecords[0]) {
                for (isNoop = true, i = 0; isNoop && i < newRecords.length; i++) {
                    if (newRecords[i] !== children[start + i]) {
                        isNoop = false;
                    }
                }
            }
        }

        // Fulfill the contract of appendChild/insertChild even if we did not have to do anything.
        // Callers must be able to correctly postprocess the returned value as an array.
        if (isNoop) {
            return newRecords;
        }

        // Remove incoming child nodes from any current parent.
        for (i = 0; i < newRecords.length; i++) {
            const newRecord = newRecords[i],
                oldParent = newRecord.parent;

            // Store added should not be modified for adds
            // caused by moving.
            isMove[newRecord.id] = newRecord.root === root;

            // Check if any descendants of the added node are moves.
            rootStore && newRecord.traverse(r => {
                if (r.root === root) {
                    isMove[r.id] = true;
                }
            });

            // If the new record has a parent, remove from that parent.
            // This operation may be vetoed by listeners.
            // If it is vetoed, then remove from the newRecords and do not
            // set the parent property
            if (oldParent && oldParent.removeChild(newRecord, isMove[newRecord.id]) === false) {
                if (!newRecordsCloned) {
                    newRecords = newRecords.slice();
                    newRecordsCloned = true;
                }
                newRecords.splice(i--, 1);
            }
            else {
                const
                    { parentIdField } = newRecord.constructor,
                    parentId = me.isAutoRoot ? null : me.id;

                newRecord.parent = me;
                newRecord.data[parentIdField] = parentId;

                // If we are in the recursive inclusion of children at construction
                // time, or in a store load, that must not be a data modification.
                // Otherwise, we have to signal a change
                if (!(me.inProcessChildren || me.isLoading)) {
                    const
                        toSet = {
                            parentId
                        },
                        { modified } = newRecord.meta,
                        oldParentId = oldParent ? oldParent.id : null,
                        wasSet = {
                            [parentIdField] : {
                                value    : me.id,
                                oldValue : oldParentId
                            }
                        };

                    // Changing back to its original value
                    if (modified[parentIdField] === me.id) {
                        delete modified[parentIdField];
                    }
                    // Cache its original value
                    else if (!('parentId' in modified)) {
                        modified[parentIdField] = oldParentId;
                    }

                    newRecord.afterChange(toSet, wasSet);
                }
            }
        }

        // Still records to insert after beforeRemove listeners may have vetoed some
        if (newRecords.length) {

            const insertAt = me.addToChildren(me.children || (me.children = []), beforeRecord, newRecords);
            me.addToChildren(me.unfilteredChildren, beforeRecord, newRecords, true);

            stores.forEach(store => {
                if (!store.isChained) {

                    newRecords.forEach(newRecord => {
                        newRecord.instanceMeta(store.id).collapsed = !newRecord.expanded;
                        newRecord.joinStore(store);
                    });

                    // Add to store (will also add any child records and trigger events)
                    store.onNodeAddChild(me, newRecords, insertAt, isMove, silent);
                }
            });
        }

        return newRecords;
    }

    /**
     * Remove a child record. Only direct children of this node can be removed, others are ignored.
     * @param {Core.data.Model|Core.data.Model[]} childRecords The record(s) to remove.
     * @param {Boolean} [isMove] Pass `true` if the record is being moved within the same store.
     * @param {Boolean} [silent] Pass `true` to not trigger events during remove.
     * @category Parent & children
     */
    removeChild(childRecords, isMove = false, silent = false) {
        const
            me = this,
            wasLeaf = me.isLeaf,
            { children, stores } = me;

        if (!Array.isArray(childRecords)) {
            childRecords = [childRecords];
        }

        childRecords = childRecords.filter(r => r.parent === me);

        if (!silent) {
            // Allow store listeners to veto the beforeRemove event
            for (const store of stores) {
                if (!store.isChained) {
                    if (store.trigger('beforeRemove', { parent : me, records : childRecords, isMove }) === false) {
                        return false;
                    }
                }
            }
        }

        const preResult = me.beforeRemoveChild ? me.beforeRemoveChild(childRecords, isMove) : undefined;

        for (const childRecord of childRecords) {

            const index = me.removeFromChildren(children, childRecord);
            me.removeFromChildren(me.unfilteredChildren, childRecord, true);

            stores.forEach(store => {
                if (!store.isChained) {
                    store.onNodeRemoveChild(me, [childRecord], index, isMove, silent);
                }
            });

            childRecord.parent = childRecord.parentIndex = childRecord.unfilteredIndex = childRecord.nextSibling = childRecord.previousSibling = null;
        }

        // Convert emptied parent into leaf if configured to do so
        if (!children.length && me.constructor.convertEmptyParentToLeaf.onRemove && !me.isRoot) {
            me.meta.isLeaf = true;
        }

        // If we've transitioned to being a leaf node, signal a change event
        // so that the UI updates
        if (me.isLeaf !== wasLeaf && !silent) {
            me.stores.forEach(s => {
                const changes = {
                    isLeaf : {
                        value : true,
                        oldValue : false
                    }
                };
                s.trigger('update', { record : me, changes });
                s.trigger('change', { action : 'update', record : me, changes });
            });
        }

        me.afterRemoveChild && me.afterRemoveChild(childRecords, preResult, isMove);
    }

    clearChildren() {
        const me = this,
            { children, stores } = me;

        if (children) {
            me.children = [];
            if (me.unfilteredChildren) {
                me.unfilteredChildren = [];
            }

            stores.forEach(store => {
                if (!store.isChained) {
                    store.onNodeRemoveChild(me, children, 0);
                }
            });
        }
    }

    /**
     * Removes all records from the rootNode
     * @private
     */
    clear() {
        const
            me         = this,
            { stores } = me,
            children   = me.children && me.children.slice();

        // Only allow for root node and if data is present
        if (!me.isRoot || !children) {
            return;
        }

        for (const store of stores) {
            if (!store.isChained) {
                if (store.trigger('beforeRemove', { parent : me, records : children, isMove : false, removingAll : true }) === false) {
                    return false;
                }
            }
        }

        me.children.length = 0;

        stores.forEach(store => {
            children.forEach(child => {
                if (child.stores.includes(store)) {
                    // this will drill down the child, unregistering whole branch
                    child.unJoinStore(store);
                }

                child.parent = child.parentIndex = child.nextSibling = child.previousSibling = null;
            });

            store.storage.suspendEvents();
            store.storage.clear();
            store.storage.resumeEvents();

            store.added.clear();
            store.modified.clear();

            store.trigger('removeAll');
            store.trigger('change', { action : 'removeall' });
        });
    }

    updateChildrenIndex(children, unfiltered = false) {
        const indexName = unfiltered ? 'unfilteredIndex' : 'parentIndex';
        let previousSibling = null;
        for (let i = 0; i < children.length; i++) {
            const child = children[i];

            child[indexName] = i;
            if (!unfiltered) {
                child.previousSibling = previousSibling;
                if (previousSibling) {
                    previousSibling.nextSibling = child;
                }
                // Last child never has a nextSibling
                if (i === children.length - 1) {
                    child.nextSibling = null;
                }
                previousSibling = child;
            }
        }
    }

    addToChildren(children, beforeRecord, newRecords, unfiltered = false) {
        if (children) {
            const
                indexName = unfiltered ? 'unfilteredIndex' : 'parentIndex',
                index     = beforeRecord ? beforeRecord[indexName] : children.length;
            children.splice(index, 0, ...newRecords);
            this.updateChildrenIndex(children, unfiltered);
            return index;
        }
    }

    removeFromChildren(children, childRecord, unfiltered = false) {
        if (children) {
            const
                indexName = unfiltered ? 'unfilteredIndex' : 'parentIndex',
                index     = childRecord[indexName];
            if (index > -1) {
                children.splice(index, 1);
                this.updateChildrenIndex(children, unfiltered);
            }
            return index;
        }
    }

};
