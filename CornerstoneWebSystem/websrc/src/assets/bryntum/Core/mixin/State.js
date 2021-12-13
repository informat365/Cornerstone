import Base from '../Base.js';
/**
 * @module Core/mixin/State
 */

/**
 * Mixin that simplifies handling state for an ui component. Classes that uses this mixin must implement `getState()` and
 * `applyState(state)`.
 *
 * ```
 * class MyUIComponent extends State() {
 *   getState() {
 *     return {
 *       this.text,
 *       this.size
 *     }
 *   }
 *
 *   applyState(state) {
 *      this.text = state.text;
 *      this.size = state.size;
 *   }
 * }
 * ```
 *
 * @demo Grid/state
 *
 * @mixin
 */
export default Target => class State extends (Target || Base) {
    //getState() {}

    //applyState(State) {}

    /**
     * Gets or sets a component's state
     * @property {Object}
     */
    get state() {
        this._state = this.getState();
        return this._state;
    }

    set state(state) {
        this._state = state;
        this.applyState(state);
    }

    // This does not need a className on Widgets.
    // Each *Class* which doesn't need 'b-' + constructor.name.toLowerCase() automatically adding
    // to the Widget it's mixed in to should implement thus.
    get widgetClass() {}
};
