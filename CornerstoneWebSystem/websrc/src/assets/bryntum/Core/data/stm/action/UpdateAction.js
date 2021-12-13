/**
 * @module Core/data/stm/action/UpdateAction
 */
import ActionBase from './ActionBase.js';
import Model from '../../Model.js';

const MODEL_PROP    = Symbol('MODEL_PROP');
const NEW_DATA_PROP = Symbol('NEW_DATA_PROP');
const OLD_DATA_PROP = Symbol('OLD_DATA_PROP');

/**
 * Action to record the fact that a model has been updated.
 */
export default class UpdateAction extends ActionBase {

    static get defaultConfig() {
        return {
            /**
             * Reference to a model which has been updated.
             *
             * @config {Core.data.Model}
             * @default
             */
            model : undefined,

            /**
             * Map of updated properties with new values.
             *
             * @config {Object}
             * @default
             */
            newData : undefined,

            /**
             * Map of updated properties with old values.
             *
             * @config {Object}
             * @default
             */
            oldData : undefined
        };
    }

    get type() {
        return 'UpdateAction';
    }

    //<debug>
    afterConfig() {
        super.afterConfig();

        console.assert(
            this.model instanceof Model && typeof this.newData == 'object' && typeof this.oldData == 'object',
            "Can't create action, bad configuration!"
        );
    }
    //</debug>

    get model() {
        return this[MODEL_PROP];
    }

    set model(value) {
        //<debug>
        console.assert(
            !this[MODEL_PROP] && value instanceof Model,
            "Can't set model, model should be instanceof `Model` class and can be set only once!"
        );
        //</debug>
        this[MODEL_PROP] = value;
    }

    get newData() {
        return this[NEW_DATA_PROP];
    }

    set newData(value) {
        //<debug>
        console.assert(
            !this[NEW_DATA_PROP] && value && typeof value == 'object',
            "Can't set new data, new data should be an object and can be set only once!"
        );
        //</debug>
        this[NEW_DATA_PROP] = Object.assign({}, value);
    }

    get oldData() {
        return this[OLD_DATA_PROP];
    }

    set oldData(value) {
        //<debug>
        console.assert(
            !this[OLD_DATA_PROP] && value && typeof value == 'object',
            "Can't set old data, old data should be an object and can be set only once!"
        );
        //</debug>
        this[OLD_DATA_PROP] = Object.assign({}, value);
    }

    undo() {
        this.model.set(this.oldData);
    }

    redo() {
        this.model.set(this.newData);
    }
}
