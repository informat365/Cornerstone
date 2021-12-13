import ActionBase from './ActionBase.js';
import Model from '../../Model.js';

const PARENT_MODEL_PROP    = Symbol('PARENT_MODEL_PROP');
const CHILD_MODELS_PROP    = Symbol('CHILD_MODELS_PROP');
const CONTEXT_PROP         = Symbol('CONTEXT_PROP');

export default class RemoveChildAction extends ActionBase {

    static get defaultConfig() {
        return {
            /**
             * Reference to a parent model a child model has been removed to.
             *
             * @config {Core.data.Model}
             * @default
             */
            parentModel : undefined,

            /**
             * Children models removed.
             *
             * @config {Core.data.Model[]}
             * @default
             */
            childModels : undefined,

            /**
             * Map having children models as keys and values containing previous parent
             * index at the parent.
             *
             * @config {Object}
             * @default
             */
            context : undefined
        };
    }

    get type() {
        return 'RemoveChildAction';
    }

    //<debug>
    afterConfig() {
        super.afterConfig();

        console.assert(
            this.parentModel instanceof Model &&
            Array.isArray(this.childModels) &&
            this.childModels.every(m => m instanceof Model) &&
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

    get context() {
        return this[CONTEXT_PROP];
    }

    set context(ctx) {
        //<debug>
        console.assert(
            !this[CONTEXT_PROP] &&
            ctx instanceof Map &&
            Array.from(ctx.entries()).every(([k, v]) => {
                return k instanceof Model && typeof v == 'number';
            }),
            "Can't set context, the value is required it should be Map keyed by inserted models with `index` values, and it can be set only once!"
        );
        //</debug>

        this[CONTEXT_PROP] = ctx;
    }

    undo() {
        const { parentModel, context, childModels } = this;

        // Let's sort models by parent index such that models with lesser index
        // were inserted back first, thus making valid parent index of models following.

        childModels.sort((lhs, rhs) => {
            const lhsIndex = context.get(lhs),
                rhsIndex = context.get(rhs);

            return (lhsIndex - rhsIndex);
        });

        // Now let's re-insert records back to where they were
        childModels.forEach(m => {
            parentModel.insertChild(context.get(m), m);
        });
    }

    redo() {
        this.parentModel.removeChild(this.childModels);
    }
}
