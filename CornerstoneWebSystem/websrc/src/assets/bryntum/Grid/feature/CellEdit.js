//TODO: Maybe some more way to stop editing in touch mode (in case grid fills entire page...)

import DomHelper from '../../Core/helper/DomHelper.js';
import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import GridFeatureManager from '../feature/GridFeatureManager.js';
import Delayable from '../../Core/mixin/Delayable.js';
import ObjectHelper from '../../Core/helper/ObjectHelper.js';
import Editor from '../../Core/widget/Editor.js';
import GlobalEvents from '../../Core/GlobalEvents.js';
import MessageDialog from '../../Core/widget/MessageDialog.js';

const
    validNonEditingKeys = {
        Enter : 1,
        F2    : 1
    },
    validEditingKeys = {
        ArrowUp    : 1,
        ArrowDown  : 1,
        ArrowLeft  : 1,
        ArrowRight : 1
    };

/**
 * @module Grid/feature/CellEdit
 */

/**
 * Adding this feature to the grid enables cell editing, usage instructions:
 * ### Start editing
 * * Double click on a cell
 * * Press [ENTER] or [F2] with a cell selected
 *
 * ### While editing
 * * [ENTER] Finish editing and start editing the same cell in next row
 * * [SHIFT] + [ENTER] Same as above put with previous row
 * * [F2] Finish editing
 * * [CMD/CTRL] + [ENTER] Finish editing
 * * [ESC] By default, first reverts the value back to its original value, next press cancels editing
 * * [TAB] Finish editing and start editing the next cell
 * * [SHIFT] + [TAB] Finish editing and start editing the previous cell
 *
 * Columns specify editor in their configuration. Editor can also by set by using a column type. Columns
 * may also contain these three configurations which affect how their cells are edited:
 * * {@link Grid.column.Column#config-invalidAction}
 * * {@link Grid.column.Column#config-revertOnEscape}
 * * {@link Grid.column.Column#config-finalizeCellEdit}
 *
 * ### Preventing editing of certain cells
 * You can prevent editing on a column by setting `editor` to false:
 *
 * ```javascript
 * new Grid({
 *    columns : [
 *       {
 *          type   : 'number',
 *          text   : 'Age',
 *          field  : 'age',
 *          editor : false
 *       }
 *    ]
 * ```
 * To prevent editing in a specific cell, listen to the {@link #event-beforeCellEditStart} and return false:
 *
 * ```javascript
 * grid.on('beforeCellEditStart', ({ editorContext }) => {
 *     return editorContext.column.field !== 'id';
 * });
 * ```
 *
 * To use an alternative input field to edit a cell, listen to the {@link #event-beforeCellEditStart} and
 * set the `editor` property of the context to the input field you want to use:
 *
 * ```javascript
 * grid.on('beforeCellEditStart', ({ editorContext }) => {
 *     return editorContext.editor = myDateField;
 * });
 * ```
 *
 * This feature is *enabled* by default.
 *
 * @example
 * { data: 'name', text: 'Name', editor: 'text' }
 *
 * @extends Core/mixin/InstancePlugin
 *
 * @demo Grid/celledit
 * @classtype cellEdit
 * @externalexample feature/CellEdit.js
 */
export default class CellEdit extends Delayable(InstancePlugin) {
    //region Config

    static get $name() {
        return 'CellEdit';
    }

    // Default configuration
    static get defaultConfig() {
        return {
            /**
             * Set to true to select the field text when editing starts
             * @config {Boolean}
             * @default
             */
            autoSelect : true,

            /**
             * What action should be taken when focus moves leaves the cell editor, for example when clicking outside.
             * May be `'complete'` or `'cancel`'.
             * @config {String}
             * @default
             */
            blurAction : 'complete',

            /**
             * Set to true to have TAB on the last cell in the data set create a new record
             * and begin editing it at its first editable cell.
             *
             * If this is configured as an object, it is used as the default data value set for each new record.
             * @config {Boolean/Object}
             * @default
             */
            addNewAtEnd : null,

            /**
             * Set to true to start editing when user starts typing text on a focused cell (as in Excel)
             * @config {Boolean}
             * @default
             */
            autoEdit : false,

            /**
             * Class to use as an editor. Default value: {@link Core.widget.Editor}
             * @config {Core.widget.Widget}
             * @internal
             */
            editorClass : Editor
        };
    }

    // Plugin configuration. This plugin chains some of the functions in Grid.
    static get pluginConfig() {
        return {
            assign : ['startEditing'],
            before : ['onElementKeyDown', 'onElementMouseDown'],
            chain  : ['onElementClick']
        };
    }

    //endregion

    //region Init

    construct(grid, config) {
        const me = this;

        me.grid = grid;

        super.construct(grid, config);

        me.storeListenerDetacher = grid.store.on({
            update  : 'onStoreUpdate',
            thisObj : me
        });
        me.listenerDetacher = grid.on({
            cellclick        : 'onCellClick',
            celldblclick     : 'onCellDblClick',
            beforerenderrows : 'onGridBeforeRenderRows',
            thisObj          : me
        });
    }

    /**
     * Displays a YES/NO confirmation dialog box owned by the current Editor. This is intended to be
     * used by {@link Grid.column.Column#config-finalizeCellEdit} implementations. The returned promise resolves passing `true`
     * if the "yes" button is pressed, and `false` if the "No" button is pressed. Typing `ESC` rejects.
     * @param {Object} options An options object for what to show.
     * @param {String} [options.title] The title to show in the dialog header.
     * @param {String} [options.message] The message to show in the dialog body.
     * @async
     */
    async confirm(options) {
        let result = true;

        if (this.editorContext) {
            // The input field must not lose containment of focus during this confirmation
            // so temporarily make the MessageDialog a descendant widget.
            MessageDialog.owner = this.editorContext.editor.inputField;
            result = await MessageDialog.confirm(options);
            MessageDialog.owner = null;
        }

        return result === MessageDialog.yesButton;
    }

    doDestroy() {
        const me = this;

        me.cancelEditing(true);
        me.listenerDetacher();
        me.storeListenerDetacher();

        // To kill timeouts
        me.grid.columns.allRecords.forEach(column => {
            if (column._cellEditor) {
                column._cellEditor.destroy();
            }
        });

        super.doDestroy();
    }

    doDisable(disable) {
        if (disable) {
            this.cancelEditing(true);
        }

        super.doDisable(disable);
    }

    set disabled(disabled) {
        super.disabled = disabled;
    }

    get disabled() {
        const { grid } = this;

        return Boolean(super.disabled || grid.disabled || grid.readOnly);
    }

    //endregion

    //region Editing

    /**
     * Is any cell currently being edited?
     * @returns {boolean}
     */
    get isEditing() {
        return Boolean(this.editorContext);
    }

    /**
     * Internal function to create or get existing editor for specified cell.
     * @private
     * @param cellContext Cell to get or create editor for
     * @returns {Core.widget.Editor} An Editor container which displays the input field.
     * @category Internal
     */
    getEditorForCell({ column, selector, editor }) {
        const
            me = this,
            grid = me.grid;

        // Reuse the Editor by caching it on the column.
        let cellEditor = column._cellEditor;

        editor.autoSelect = me.autoSelect;
        if (cellEditor) {
            // Already got the positioned Editor container which carries the input field.
            // just check if the actual field has been changed in a beforeCellEditStart handler.
            // If so, switch it out.
            if (cellEditor.inputField !== editor) {
                cellEditor.remove(cellEditor.items[0]);
                cellEditor.add(editor);
            }
        }
        else {
            cellEditor = column._cellEditor = new me.editorClass({
                cls           : 'b-cell-editor',
                inputField    : editor,
                blurAction    : 'none',
                invalidAction : column.invalidAction,
                completeKey   : false,
                cancelKey     : false,
                owner         : grid,
                listeners     : me.getEditorListeners()
            });
        }

        // Keep the record synced with the value
        if (column.instantUpdate && !editor.cellEditValueSetter) {
            ObjectHelper.wrapProperty(editor, 'value', null, v => {
                const { editorContext } = me;
                // Only tickle the record if the value has changed.
                if (editorContext && !ObjectHelper.isEqual(editorContext.record[editorContext.column.field], v)) {
                    editorContext.record[editorContext.column.field] = v;
                }
            });
            editor.cellEditValueSetter = true;
        }

        Object.assign(cellEditor.element.dataset, {
            rowId    : selector.id,
            columnId : selector.columnId,
            field    : column.field
        });

        // First ESC press reverts
        cellEditor.inputField.revertOnEscape = column.revertOnEscape;

        return me.editor = cellEditor;
    }

    // Turned into function to allow overriding in Gantt, and make more configurable in general
    getEditorListeners() {
        return {
            focusout       : 'onEditorFocusOut',
            focusin        : 'onEditorFocusIn',
            start          : 'onEditorStart',
            beforecomplete : 'onEditorBeforeComplete',
            complete       : 'onEditorComplete',
            cancel         : 'onEditorCancel',
            thisObj        : this
        };
    }

    onEditorStart({ source : editor }) {
        const me = this,
            editorContext = me.editorContext = editor.cellEditorContext;

        if (editorContext) {
            const { grid } = me,
                { cell, editor, column } = editorContext;

            // Match editorTarget size and position
            if (column.editTargetSelector) {
                const editorTarget = cell.querySelector(column.editTargetSelector);

                if (editorTarget) {
                    editor.width -= editorTarget.offsetLeft;
                    DomHelper.addTranslateX(editor.element, editorTarget.offsetLeft);
                }
            }

            cell.classList.add('b-editing');

            me.grid.on({
                cellclick      : 'onCellClickWhileEditing',
                viewportResize : 'onViewportResizeWhileEditing'
            }, me);

            // Handle tapping outside of the grid element. Use GlobalEvents
            // because it uses a capture:true listener before any other handlers
            // might stop propagation.
            // Cannot use delegate here. A tapped cell will match :not(#body-container)
            me.removeEditingListeners = GlobalEvents.addListener({
                globaltap : 'onTapOut',
                thisObj   : me
            });

            /**
             * Fires on the owning Grid when editing starts
             * @event startCellEdit
             * @param {Grid.view.Grid} grid **Deprecated** Use `source` instead
             * @param {Grid.view.Grid} source Owner grid
             * @param {Object} editorContext Editing context
             * @param {Core.widget.Editor} editorContext.editor The Editor being used.
             * Will contain an `inputField` property which is the field being used to perform the editing.
             * @param {Grid.column.Column} editorContext.column Target column
             * @param {Core.data.Model} editorContext.record Target record
             * @param {HTMLElement} editorContext.cell Target cell
             * @param {*} editorContext.value Cell value
             */
            grid.trigger('startCellEdit', { grid, editorContext });
        }
    }

    onEditorBeforeComplete(context) {
        const { grid } = this,
            editor = context.source,
            editorContext = editor.cellEditorContext;

        context.grid = grid;
        context.editorContext = editorContext;

        /**
         * Fires on the owning Grid before the cell editing is finished, return false to signal that the value is invalid and editing should not be finalized.
         * @event beforeFinishCellEdit
         * @param {Grid.view.Grid} grid Target grid
         * @param {Object} editorContext Editing context
         * @param {Core.widget.Editor} editorContext.editor The Editor being used.
         * Will contain an `inputField` property which is the field being used to perform the editing.
         * @param {Grid.column.Column} editorContext.column Target column
         * @param {Core.data.Model} editorContext.record Target record
         * @param {HTMLElement} editorContext.cell Target cell
         * @param {*} editorContext.value Cell value
         */
        return grid.trigger('beforeFinishCellEdit', context);
    }

    onEditorComplete({ source : editor }) {
        const { grid } = this,
            editorContext = editor.cellEditorContext;

        // Ensure the docs below are accurate!
        editorContext.value = editor.inputField.value;

        /**
         * Fires on the owning Grid when cell editing is finished
         * @event finishCellEdit
         * @param {Grid.view.Grid} grid Target grid
         * @param {Object} editorContext Editing context
         * @param {Core.widget.Editor} editorContext.editor The Editor being used.
         * Will contain an `inputField` property which is the field being used to perform the editing.
         * @param {Grid.column.Column} editorContext.column Target column
         * @param {Core.data.Model} editorContext.record Target record
         * @param {HTMLElement} editorContext.cell Target cell
         * @param {*} editorContext.value Cell value
         */
        grid.trigger('finishCellEdit', { grid, editorContext });
        this.cleanupAfterEdit(editorContext);
    }

    onEditorCancel() {
        const { editorContext, muteEvents, grid } = this;

        if (editorContext) {
            this.cleanupAfterEdit(editorContext);
        }
        if (!muteEvents) {
            /**
             * Fires on the owning Grid when editing is cancelled
             * @event cancelCellEdit
             * @param {Grid.view.Grid} grid **Deprecated** Use `source` instead
             * @param {Grid.view.Grid} source Owner grid
             */
            grid.trigger('cancelCellEdit', { grid });
        }
    }

    cleanupAfterEdit(editorContext) {
        const me = this,
            { editor } = editorContext;

        editorContext.cell.classList.remove('b-editing');
        editor.cellEditorContext = me.editorContext = null;
        me.grid.un({
            cellclick      : 'onCellClickWhileEditing',
            viewportResize : 'onViewportResizeWhileEditing'
        }, me);
        me.removeEditingListeners();
        // MS Edge workaround.
        // At this moment active element is grid.element, but removing editor element still triggers focusout event
        // which is processed by the GlobalEvents, which decides that focus goes to body element. That, in turn, triggers
        // clearFocus on grid navigation, removing focused cell from cache etc, eventually focus actually goes to body.
        // Suspending listener to seamlessly remove element keeping focus where it belongs.
        // NOTE: not reproducible in IFrame, so our tests cannot catch this
        GlobalEvents.suspendFocusEvents();
        editor.element.remove();
        GlobalEvents.resumeFocusEvents();
    }

    /**
     * Find the next succeeding or preceding cell which is editable (column.editor != false)
     * @param {Object} cellInfo
     * @param {Boolean} isForward
     * @returns {Object}
     * @private
     * @category Internal
     */
    getAdjacentEditableCell(cellInfo, isForward) {
        let addNewAtEnd = this.addNewAtEnd,
            grid = this.grid,
            store = grid.store,
            rowManager = grid.rowManager,
            rowId    = cellInfo.id,
            columnId = cellInfo.columnId,
            columns  = grid.columns,
            column   = columns.getAdjacentLeaf(columnId, isForward);

        while (rowId) {
            const record = store.getById(rowId);

            if (column) {
                columnId = column.id;

                if (!column.hidden && column.editor && column.canEdit(record)) {
                    return { id : rowId, columnId : column.id };
                }

                column = columns.getAdjacentLeaf(columnId, isForward);
            }
            else {
                let editRec = store.getAdjacent(cellInfo.id, isForward, false, true);

                if (!editRec && isForward && addNewAtEnd) {
                    editRec = store.add(typeof addNewAtEnd === 'object' ? ObjectHelper.clone(addNewAtEnd) : {})[0];

                    // If the new record was not added due to it being off the end of the rendered block
                    // ensure we force it to be there before we attempt to edit it.
                    if (!rowManager.getRowFor(editRec)) {
                        rowManager.displayRecordAtBottom();
                    }
                }

                rowId = editRec && editRec.id;

                if (editRec) {
                    column = isForward ? columns.first : columns.leaves[columns.leaves.length - 1];
                }
            }
        }

        return null;
    }

    /**
     * Creates an editing context object for the passed cell context (target cell must be in the DOM).
     *
     * If the referenced cell is editable, an object returned will
     * be returned containing the following properties:
     *
     *     - column
     *     - record
     *     - cell
     *     - value
     *     - selector
     *
     * If the references cell is _not_ editable, `false` will be returned.
     * @param {Object} cellContext an object which encapsulates a cell.
     * @param {String} cellContext.id The record id of the row to edit
     * @param {String} cellContext.columnId The column id of the column to edit
     * @private
     */
    getEditingContext(cellContext) {
        cellContext = this.grid.normalizeCellContext(cellContext);

        const me = this,
            { grid } = me,
            column = grid.columns.getById(cellContext.columnId),
            record = grid.store.getById(cellContext.id),
            cell = grid.getCell(cellContext);

        // Cell must be in the DOM to edit.
        // Cannot edit hidden columns and columns without an editor.
        // Cannot edit special rows (groups etc).
        if (cell && column && !column.hidden && column.editor && record && !record.meta.specialRow && column.canEdit(record)) {
            const value = record && record[column.field];

            return {
                column,
                record,
                cell,
                value    : value === undefined ? null : value,
                selector : cellContext,
                editor   : column.editor
            };
        }
        else {
            return false;
        }
    }

    /**
     * Start editing specified cell. If no cellContext is given it starts with the first cell in the first row.
     * This function is exposed on Grid and can thus be called as `grid.startEditing(...)`
     * @param {Object} cellContext Cell specified in format { id: 'x', columnId/column/field: 'xxx' }. See {@link Grid.view.Grid#function-getCell} for details.
     * @fires startCellEdit
     * @returns {Boolean} editingStarted
     * @category Editing
     */
    startEditing(cellContext = {}) {
        const me = this;

        // If already editing, or grid is readonly, no can do.
        if (!(me.editorContext || me.disabled)) {
            const
                { grid }          = me,
                normalizedContext = grid.normalizeCellContext(cellContext);

            // First scroll record into view and register it as last focusedCell
            grid.focusCell(cellContext);

            const editorContext = me.getEditingContext(normalizedContext);

            /**
             * Fires on the owning Grid before editing starts, return `false` to prevent editing
             * @event beforeCellEditStart
             * @preventable
             * @param {Grid.view.Grid} source Owner grid
             * @param {Object} editorContext Editing context
             * @param {Grid.column.Column} editorContext.column Target column
             * @param {Core.data.Model} editorContext.record Target record
             * @param {HTMLElement} editorContext.cell Target cell
             * @param {Core.widget.Field} editorContext.editor The input field that the column is configured
             * with (see {@link Grid.column.Column#config-field}). This property mey be replaced
             * to be a different {@link Core.widget.Field field} in the handler, to take effect
             * just for the impending edit.
             * @param {Function} [editorContext.finalize] An async function may be injected into this property
             * which performs asynchronous finalization tasks such as complex validation of confirmation. The
             * value `true` or `false` must be returned.
             * @param {Object} [editorContext.finalize.context] An object describing the editing context upon requested completion of the edit.
             * @param {*} editorContext.value Cell value
             */
            if (!editorContext || grid.trigger('beforeCellEditStart', { grid, editorContext }) === false) {
                return false;
            }

            // Cannot edit hidden columns and columns without an editor
            // Cannot edit special rows (groups etc).
            if (editorContext) {
                // Focus grid element to preserve focus inside once editing is started
                // https://app.assembla.com/spaces/bryntum/tickets/8155-grid-cell-not-properly-focused-in-advanced-demo
                DomHelper.focusWithoutScrolling(grid.element);

                const
                    editor                   = editorContext.editor = me.getEditorForCell(editorContext),
                    { cell, record, column } = editorContext;

                // Prevent highlight when setting the value in the editor
                editor.inputField.highlightExternalChange = false;

                editor.cellEditorContext = editorContext;
                editor.render(me.grid.getSubGridFromColumn(column).element);

                // Attempt to start edit.
                // We will set up our context in onEditorStart *if* the start was successful.
                editor.startEdit({
                    target : cell,
                    record,
                    field  : editor.inputField.name || editorContext.column.field
                });

                return true;
            }
        }

        return false;
    }

    /**
     * Cancel editing, destroys the editor
     * @param {Boolean} silent Pass true to prevent method from firing event
     * @fires cancelCellEdit
     * @category Editing
     */
    cancelEditing(silent = false) {
        const
            me                              = this,
            { editorContext, editor, grid } = me;

        if (editorContext) {
            // If cancel was not called from onEditorFocusOut, then refocus the grid.
            if (editor.containsFocus) {
                // Kill editorContext before we destroy the editor so that we know we are not editing
                // in ensuing focusout event handling
                me.editorContext = null;

                // Control focus reversion if we own focus
                if (editor.inputField.owns(DomHelper.activeElement)) {
                    DomHelper.focusWithoutScrolling(grid.element);
                }
                me.editorContext = editorContext;
            }

            me.muteEvents = silent;
            editor.cancelEdit();
            me.muteEvents = false;
        }
    }

    /**
     * Finish editing, update the underlying record and destroy the editor
     * @fires finishCellEdit
     * @category Editing
     * @returns `false` if the edit could not be finished due to the value being invalid or the
     * Editor's `complete` event was vetoed.
     * @async
     */
    async finishEditing() {
        const
            { editorContext } = this,
            { column }        = editorContext;

        let result = false;

        if (editorContext) {
            // If completeEdit finds that the editor context has a finalize method in it,
            // it will *await* the completion of that method before completing the edit
            // so we must await completeEdit.
            // We can override that finalize method by passing the column's own finalizeCellEdit.
            result = await editorContext.editor.completeEdit(column.bindCallback(column.finalizeCellEdit));
        }
        return result;
    }

    //endregion

    //region Events

    /**
     * Event handler added when editing is active called when user clicks a cell in the grid during editing.
     * It finishes editing and moves editor to the selected cell instead.
     * @private
     * @category Internal event handling
     */
    onCellClickWhileEditing({ event, cellSelector }) {
        const me = this;

        // Ignore clicks in the editor.
        if (!me.editorContext.editor.owns(event.target)) {
            if (me.getEditingContext(cellSelector)) {
                // Attempt to finish the current edit.
                // Will return false if the field is invalid.
                if (me.finishEditing()) {
                    me.startEditing(cellSelector);
                }
                // Previous edit was invalid, return to it.
                else {
                    me.grid.focusCell(me.editorContext.selector);
                    me.editor.inputField.focus();
                }
            }
            else {
                me.finishEditing();
            }
        }
    }

    onViewportResizeWhileEditing() {
        const {
            editor,
            column
        } = this.editorContext;

        editor.width = column.element.offsetWidth;
    }

    /**
     * Starts editing if user taps selected cell again on touch device. Chained function called when user clicks a cell.
     * @private
     * @category Internal event handling
     */
    onCellClick({ source : grid, record, cellSelector, cellElement, target, event }) {
        const
            selected = grid.focusedCell || {},
            column   = grid.columns.getById(cellSelector.columnId);

        // Columns may provide their own handling of cell editing
        if (column.onCellClick) {
            column.onCellClick({ grid, column, record, cellSelector, cellElement, target, event });
        }
        else if (target.matches('.b-tree-expander')) {
            this.cancelEditing();
            return false;
        }
        else if (DomHelper.isTouchEvent &&
            cellSelector.id == selected.id &&
            cellSelector.columnId == selected.columnId) {
            this.startEditing(cellSelector);
        }
    }

    /**
     * Chained function called when user dbl clicks a cell. Starts editing.
     * @private
     * @category Internal event handling
     */
    onCellDblClick({ cellSelector }) {
        const me = this;

        if (me.editorContext && !me.finishEditing()) {
            return;
        }
        me.startEditing(cellSelector);
    }

    /**
     * Update the input field if underlying data changes during edit.
     * @private
     * @category Internal event handling
     */
    onStoreUpdate({ changes, record }) {
        const { editorContext } = this;

        if (editorContext && editorContext.editor.isVisible) {
            if (record === editorContext.record && editorContext.editor.dataField in changes) {
                editorContext.editor.refreshEdit();
            }
        }
    }

    /**
     * Invalidate editor when grid renders rows.
     * @private
     * @category Internal event handling
     */
    onGridBeforeRenderRows() {
        // grid rows are being rerendered, meaning the underlying data might be changed.
        // the editor probably won't be over the same record, so cancel
        if (this.editorContext && this.editorContext.editor.isVisible) {
            this.cancelEditing();
        }
    }

    /**
     * Chained function called on key down. [enter] or [f2] starts editing. [enter] also finishes editing and starts
     * editing next row, [f2] also finishes editing without moving to the next row. [esc] cancels editing. [tab]
     * edits next column, [shift] + [tab] edits previous.
     * @param event
     * @private
     * @category Internal event handling
     */
    async onElementKeyDown(event) {
        const me = this;

        // flagging event with handled = true used to signal that other features should probably not care about it
        if (event.handled) return;

        if (!me.editorContext) {
            const key = event.key,
                editingStartedWithCharacterKey = me.autoEdit && (key.length === 1 || key === 'Backspace');

            // enter or f2 to edit, or any character key if autoEdit is enabled
            if ((editingStartedWithCharacterKey || validNonEditingKeys[key]) && me.grid.focusedCell) {
                event.preventDefault();

                if (!me.startEditing(me.grid.focusedCell)) {
                    return;
                }

                const inputField = me.editor.inputField,
                    input = inputField.input;

                // if editing started with a keypress and the editor has an input field, set its value
                if (editingStartedWithCharacterKey && input) {
                    // Simulate a keydown in an input field by setting input value
                    // plus running our internal processing of that event
                    input.value = key === 'Backspace' ? '' : key;
                    inputField.internalOnInput(event);

                    // IE11 + Edge put caret at 0 when focusing
                    inputField.moveCaretToEnd();
                }
            }
        }
        else {
            // enter
            if (event.key === 'Enter') {
                event.preventDefault();
                event.stopPropagation();
                if (await me.finishEditing()) {
                    // Finalizing might have been blocked by an invalid value
                    if (!me.isEditing) {
                        // Enter in combination with special keys finishes editing
                        // On touch Enter always finishes editing. Feels more natural since no tab-key etc.
                        if (event.ctrlKey || event.metaKey || event.altKey || me.grid.touch) {
                            return;
                        }
                        // Edit previous
                        else if (event.shiftKey) {
                            if (me.grid.navigateUp()) {
                                me.startEditing(me.grid.focusedCell);
                            }
                        }
                        // Edit next
                        else if (me.grid.navigateDown()) {
                            me.startEditing(me.grid.focusedCell);
                        }
                    }
                }
            }

            // f2
            if (event.key === 'F2') {
                event.preventDefault();
                me.finishEditing();
            }

            // esc
            if (event.key === 'Escape') {
                event.stopPropagation();
                event.preventDefault();
                me.cancelEditing();
            }

            // tab
            if (event.key === 'Tab') {
                event.preventDefault();

                let focusedCell = me.grid.focusedCell;

                if (focusedCell) {
                    let cellInfo = me.getAdjacentEditableCell(focusedCell, !event.shiftKey);

                    if (cellInfo) {
                        if (await me.finishEditing()) {
                            me.grid.focusCell(cellInfo, {
                                animate : 100
                            });

                            me.startEditing(cellInfo);
                        }
                    }
                }
            }

            // prevent arrow keys from moving editor
            if (validEditingKeys[event.key]) {
                event.handled = true;
            }
        }
    }

    onElementMouseDown(event) {
        // If it's a contextmenu mousedown during cell edit, prevent default
        // because the contextmenu handler will move focus directly to the context menu.
        // If we allow it to go through the grid, the edit will not terminate because
        // that usually means begin editing somewhere else in the grid.
        // TODO: This won't be necessary when cells are the focusable DOM unit.
        if (event.button === 2 && this.editorContext) {
            event.preventDefault();
        }
    }

    /**
     * Cancel editing on widget focusout
     * @private
     */
    onEditorFocusOut(event) {
        const me = this,
            { grid } = me;

        // If the editor is not losing focus as a result of its tidying up process
        // And focus is moving to outside of the grid, or back to the initiating cell
        // (which indicates a click on empty space below rows), then explicitly terminate.
        if (me.editorContext && !me.editor.isFinishing && me.editor.inputField.owns(event.target) && (event.toWidget !== grid || grid.isLocationEqual(me.grid.focusedCell, me.editorContext.selector))) {
            if (me.blurAction === 'cancel') {
                me.cancelEditing();
            }
            else {
                me.finishEditing();
            }
        }
    }

    onEditorFocusIn(event) {
        const widget = event.toWidget;

        if (widget === this.editor.inputField) {
            if (this.autoSelect && widget.selectAll && !widget.readOnly && !widget.disabled) {
                widget.selectAll();
            }
        }
    }

    /**
     * Cancel edit on touch outside of grid for mobile Safari (focusout not triggering unless you touch something focusable)
     * @private
     */
    onTapOut({ event }) {
        const me = this;

        if (!me.grid.bodyContainer.contains(event.target)) {
            if (!me.editor.owns(event.target)) {
                if (me.blurAction === 'cancel') {
                    me.cancelEditing();
                }
                else {
                    me.finishEditing();
                }
            }
        }
    }

    /**
     * Finish editing if clicking below rows (only applies when grid is higher than rows).
     * @private
     * @category Internal event handling
     */
    onElementClick(event) {
        const me = this;
        if (event.target.classList.contains('b-grid-body-container') && me.editorContext) {
            me.finishEditing();
        }
    }

    //endregion
}

GridFeatureManager.registerFeature(CellEdit, true);
