/**
 * @module Core/data/stm/action/ActionBase
 */
import Base from '../../../Base.js';

const throwAbstractMethodCall = () => {
    throw new Error('Abstract method call!');
};

/**
 * Base class for STM actions.
 *
 * @abstract
 */
export default class ActionBase extends Base {

    /**
     * Gets the type of the action (stringified class name).
     *
     * @return {String}
     */
    get type() {
        return this.constructor.name;
    }

    /**
     * Undoes an action
     */
    undo() {
        throwAbstractMethodCall();
    }

    /**
     * Redoes an action
     */
    redo() {
        throwAbstractMethodCall();
    }
}
