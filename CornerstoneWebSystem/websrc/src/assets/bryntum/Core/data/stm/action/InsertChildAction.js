/**
 * @module Core/data/stm/action/InsertChildAction
 */
import ActionBase from './ActionBase.js';
import Model from '../../Model.js';

const PARENT_MODEL_PROP    = Symbol('PARENT_MODEL_PROP');
const CHILD_MODELS_PROP    = Symbol('CHILD_MODELS_PROP');
const INSERT_INDEX_PROP    = Symbol('INSERT_INDEX_PROP');
const CONTEXT_PROP         = Symbol('CONTEXT_PROP');

/**
 * Action to record the fact of adding a children models into a parent model.
 */
export default class InsertChildAction extends ActionBase {

    static get defaultConfig() {
        return {
            /**
             * Reference to a parent model a child model has been added to.
             *
             * @config {Core.data.Model}
             * @default
             */
            parentModel : undefined,

            /**
             * Children models inserted.
             *
             * @config {Core.data.Model[]}
             * @default
             */
            childModels : undefined,

            /**
             * Index a children models are inserted at
             *
             * @config {Number}
             * @default
             */
            insertIndex : undefined,

            /**
             * Map having children models as keys and values containing previous parent
             * of each model and index at the previous parent.
             *
             * @config {Object}
             * @default
             */
            context : undefined
        };
    }

    get type() {
        return 'InsertChildAction';
    }

    //<debug>
    afterConfig() {
        super.afterConfig();

        console.assert(
            this.parentModel instanceof Model &&
            Array.isArray(this.childModels) &&
            this.childModels.every(m => m instanceof Model) &&
            this.insertIndex !== undefined &&
            this.context instanceof Map,
            "Can't create action, bad configuration!"
        );
    }
    //</debug>

    get parentModel() {
        return this[PARENT_MODEL_PROP];
    }

    set parentModel(model) {
        //<debug>
        console.assert(
            !this[PARENT_MODEL_PROP] && model,
            "Can't set parent model, model is required and can be set only once!"
        );
        //</debug>

        this[PARENT_MODEL_PROP] = model;
    }

    get childModels() {
        return this[CHILD_MODELS_PROP];
    }

    set childModels(models) {
        //<debug>
        console.assert(
            !this[CHILD_MODELS_PROP] &&
            Array.isArray(models) &&
            models.every(m => m instanceof Model),
            "Can't set child models, models are required, it should be array of Model class and can be set only once!"
        );
        //</debug>

        this[CHILD_MODELS_PROP] = models.slice(0);
    }

    get insertIndex() {
        return this[INSERT_INDEX_PROP];
    }

    set insertIndex(index) {
        //<debug>
        console.assert(
            this[INSERT_INDEX_PROP] === undefined && index !== undefined,
            "Can't set insert index, the value is required and can be set only once!"
        );
        //</debug>

        this[INSERT_INDEX_PROP] = index;
    }

    get context() {
        return this[CONTEXT_PROP];
    }

    set context(ctx) {
        //<debug>
        console.assert(
            !this[CONTEXT_PROP] &&
            ctx instanceof Map &&
            Array.from(ctx.entries()).every(([k, v]) => {
                return k instanceof Model && typeof v == 'object' && v && v.hasOwnProperty('parent') && v.hasOwnProperty('index');
            }),
            "Can't set context, the value is required it should be Map keyed by inserted models with object values containing `paret` and `index` properties, and it can be set only once!"
        );
        //</debug>

        this[CONTEXT_PROP] = ctx;
    }

    undo() {
        const { parentModel, context, childModels } = this;

        // Let's sort models by parent index such that models with lesser index
        // were inserted back first, thus making valid parent index of models following.
        childModels.sort((lhs, rhs) => {
            const { lhsParent, lhsIndex } = context.get(lhs) || {},
                { rhsParent, rhsIndex } = context.get(rhs) || {};

            return lhsParent && lhsParent === rhsParent ? (lhsIndex - rhsIndex) : 0;
        });

        // Now let's re-insert records back to where they were or remove them
        // if they weren't anywhere
        childModels.forEach(m => {
            const { parent, index } = context.get(m) || {};

            if (parent) {
                // If we move within same parent then index must be adjusted
                if (parent === parentModel) {
                    parent.insertChild(m, parent.children[index + 1]);
                }
                else {
                    // Insert at previous index
                    parent.insertChild(m, parent.children[index]);
                }
            }
            else {
                // Just remove
                parentModel.removeChild(m);
            }
        });
    }

    redo() {
        const me = this;
        me.parentModel.insertChild(me.insertIndex, me.childModels);
    }
}
