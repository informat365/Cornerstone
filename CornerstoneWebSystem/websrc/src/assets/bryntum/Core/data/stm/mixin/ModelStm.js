/**
 * @module Core/data/stm/mixin/ModelStm
 */
import Base from '../../../Base.js';

const STM_PROP = Symbol('STM_PROP');

/**
 * Mixin making a model compatible with {@link Core/data/stm/StateTrackingManager}
 *
 * @mixin
 */
export default Target => class ModelStm extends (Target || Base) {

    static get defaultConfig() {
        return {
            stm : null
        };
    }

    //<debug>
    afterConstruct() {
        super.afterConstruct();

        console.assert(
            typeof this.set == 'function',
            "Can't mix into this model class, no method `set` found!"
        );
    }
    //</debug>

    joinStore(store) {
        super.joinStore && super.joinStore(store);

        if (!this.stm) {
            this.stm = store.stm;
        }
    }

    unJoinStore(store) {
        if (this.stm === store.stm) {
            this.stm = null;
        }

        super.unJoinStore && super.unJoinStore(store);
    }

    /**
     * Reference to STM manager, if used
     * @member {Core.data.stm.StateTrackingManager}
     * @category Misc
     */
    get stm() {
        return this[STM_PROP];
    }

    set stm(stm) {
        this[STM_PROP] = stm;
    }

    /**
     * Overridden to collect initial data for the fields about to be changed.
     * The method is called from within {@link Core/data/Model#function-set} method.
     *
     * @private
     */
    beforeSet(field, value, silent, fromRelationUpdate) {
        const preResult = super.beforeSet ? super.beforeSet(field, value, silent, fromRelationUpdate) : [];

        const stm = this.stm;

        if (stm && !stm.disabled) {

            let newDataCandidate, oldDataCandidate;

            if (typeof field == 'object') {
                [newDataCandidate, oldDataCandidate] = Object.keys(field).reduce(
                    (data, fName) => {
                        data[0][fName] = field[fName];
                        data[1][fName] = this.get(fName);
                        return data;
                    },
                    [{}, {}]
                );
            }
            else {
                newDataCandidate = { [field] : value };
                oldDataCandidate = { [field] : this.get(field) };
            }

            preResult.push([newDataCandidate, oldDataCandidate]);
        }

        return preResult;
    }

    /**
     * Overridden to store initial data of the changed fields and to notify STM
     * manager about the change action if anything has been changed in result.
     *
     * The method is called from within {@link Core/data/Model#function-set} method.
     *
     * @private
     */
    afterSet(field, value, silent, fromRelationUpdate, beforeResult, wasSet) {

        const stm = this.stm;

        if (stm && !stm.disabled) {

            const preResult = beforeResult.pop();

            if (wasSet) {
                let [newDataCandidate, oldDataCandidate] = preResult;

                let [newData, oldData] = Object.keys(wasSet).reduce(
                    (data, fName) => {
                        data[0][fName] = newDataCandidate[fName];
                        data[1][fName] = oldDataCandidate[fName];
                        return data;
                    },
                    [{}, {}]
                );

                stm.onModelUpdate(this, newData, oldData);
            }
        }

        super.afterSet && super.afterSet(field, value, silent, fromRelationUpdate, beforeResult, wasSet);
    }

    /**
     * Called from {@link Core/data/mixin/TreeNode#function-insertChild} to obtain inserted
     * records initial parents and parent index, to be able to restore the state back upon undo.
     *
     * @param {Core.data.Model[]} childRecords
     * @return {Array} Array of results from this call and any of super calls if any.
     *               This result is consumed by {@link #function-afterInsertChild} which pops
     *               from the result array to take only results of this method call and leave
     *               results from super calls untouched.
     *
     * @private
     */
    beforeInsertChild(childRecords) {
        const preResult = super.beforeInsertChild ? super.beforeInsertChild(childRecords) : [];

        const stm = this.stm;

        if (stm && !stm.disabled) {
            preResult.push(
                childRecords.reduce((result, childRecord) => {
                    // We are interested only in records from the same root node.
                    // Removing (which is done before insertion) of the records
                    // from another root (and store) should
                    // be handled by that store STM instance.
                    if (childRecord.root === this.root) {
                        result.set(childRecord, {
                            parent : childRecord.parent,
                            index  : childRecord.parent ? childRecord.parentIndex : undefined
                        });
                    }

                    return result;
                }, new Map())
            );
        }

        return preResult;
    }

    /**
     * Called from {@link Core/data/mixin/TreeNode#function-insertChild} to notify {@link Core/data/stm/StateTrackingManager}
     * about children insertion. Provides it with all necessary context information collected
     * in {@link #function-beforeInsertChild} required to undo/redo the action.
     *
     * @private
     */
    afterInsertChild(index, childRecords, beforeResult, inserted) {
        const stm = this.stm;

        if (stm && !stm.disabled) {
            const context = beforeResult.pop();

            if (inserted) {
                stm.onModelInsertChild(this, index, inserted, context);
            }
        }

        super.afterInsertChild && super.afterInsertChild(index, childRecords, beforeResult, inserted);
    }

    /**
     * Called from {@link Core/data/mixin/TreeNode#function-removeChild} to obtain removed
     * records initial parent index, to be able to restore the state back upon undo.
     *
     * @param {Core.data.Model[]} childRecords
     * @param {Boolean} isMove
     * @return {Array} Array of results from this call and any of super calls if any.
     *               This result is consumed by {@link #function-afterRemoveChild} which pops
     *               from the result array to take only results of this method call and leave
     *               results from super calls untouched.
     *
     * @private
     */
    beforeRemoveChild(childRecords, isMove) {
        const preResult = super.beforeRemoveChild ? super.beforeRemoveChild(childRecords, isMove) : [];

        const stm = this.stm;

        // If it's move then InsertChildAction will handle this case
        if (stm && !stm.disabled && !isMove) {
            // Child records we receive are guaranteed to be direct children
            // of this node, see Core/data/mixin/TreeNode#removeChild method

            // Here we interested in the original index for each child removed,
            // we collect it and store for future use in RemoveChildAction
            preResult.push(
                childRecords.reduce((result, childRecord) => {
                    result.set(childRecord, childRecord.parentIndex);
                    return result;
                }, new Map())
            );
        }

        return preResult;
    }

    /**
     * Called from {@link Core/data/mixin/TreeNode#function-removeChild} to notify {@link Core/data/stm/StateTrackingManager}
     * about children removing. Provides it with all necessary context information collected
     * in {@link #function-beforeRemoveChild} required to undo/redo the action.
     *
     * @private
     */
    afterRemoveChild(childRecords, beforeResult, isMove) {
        const stm = this.stm;

        // If it's move then InsertChildAction will handle this case
        if (stm && !stm.disabled && !isMove) {
            const context = beforeResult.pop();

            if (childRecords && childRecords.length) {
                stm.onModelRemoveChild(this, childRecords, context);
            }
        }

        super.afterRemoveChild && super.afterRemoveChild(childRecords, beforeResult, isMove);
    }
};
