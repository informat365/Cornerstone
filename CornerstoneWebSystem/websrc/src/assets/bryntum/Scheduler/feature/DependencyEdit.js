import GridFeatureManager from '../../Grid/feature/GridFeatureManager.js';
import ObjectHelper from '../../Core/helper/ObjectHelper.js';
import Rectangle from '../../Core/helper/util/Rectangle.js';
import '../../Core/widget/DisplayField.js';
import '../../Core/widget/DurationField.js';
import DependencyEditor from '../view/DependencyEditor.js';
import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import DependencyModel from '../model/DependencyModel.js';

/**
 * @module Scheduler/feature/DependencyEdit
 */

/**
 * Feature that displays a popup containing fields for editing a dependency. Requires the {@link Scheduler.feature.Dependencies Dependencies} feature to be enabled.
 *
 * This feature is **disabled** by default. It does **not** support vertical mode.
 *
 * @extends Core/mixin/InstancePlugin
 * @externalexample scheduler/Dependencies.js
 * @demo Scheduler/dependencies
 */
export default class DependencyEdit extends InstancePlugin {
    //region Config

    static get $name() {
        return 'DependencyEdit';
    }

    static get defaultConfig() {
        return {
            /**
             * True to hide this editor if a click is detected outside it (defaults to true)
             * @config {Boolean}
             * @default
             * @category Editor
             */
            autoClose : true,

            /**
             * True to save and close this panel if ENTER is pressed in one of the input fields inside the panel.
             * @config {Boolean}
             * @default
             * @category Editor
             */
            saveAndCloseOnEnter : true,

            /**
             * True to show a delete button in the form.
             * @config {Boolean}
             * @default
             * @category Editor widgets
             */
            showDeleteButton : true,

            /**
             * The event that shall trigger showing the editor. Defaults to `dependencydblclick`, set to empty string or `null` to disable editing of dependencies.
             * @config {String}
             * @default
             * @category Editor
             */
            triggerEvent : 'dependencydblclick',

            /**
             * True to show the lag field for the dependency
             * @config {Boolean}
             * @default
             * @category Editor widgets
             */
            showLagField : false,

            dependencyRecord : null,

            /**
             * Default editor configuration, which widgets it shows etc.
             * @config {Object}
             * @category Editor
             */
            editorConfig : {
                title       : 'L{Edit dependency}',
                localeClass : this,
                closable    : true,

                items : [
                    /**
                     * Reference to the from name
                     * @member {Core.widget.DisplayField} fromNameField
                     * @readonly
                     */
                    {
                        type        : 'display',
                        localeClass : this,
                        label       : 'L{From}',
                        editable    : false,
                        ref         : 'fromNameField'
                    },
                    /**
                     * Reference to the to name field
                     * @member {Core.widget.DisplayField} toNameField
                     * @readonly
                     */
                    {
                        type        : 'display',
                        localeClass : this,
                        label       : 'L{To}',
                        ref         : 'toNameField'
                    },
                    /**
                     * Reference to the type field
                     * @member {Core.widget.Combo} typeField
                     * @readonly
                     */
                    {
                        type         : 'combo',
                        localeClass  : this,
                        label        : 'L{Type}',
                        name         : 'type',
                        ref          : 'typeField',
                        editable     : false,
                        valueField   : 'id',
                        displayField : 'name',
                        items        : Object.keys(DependencyModel.Type).map(type => {
                            return  {
                                id   : DependencyModel.Type[type],
                                name : this.L(type)
                            };
                        })
                    },

                    /**
                     * Reference to the lag field
                     * @member {Core.widget.DurationField} lagField
                     * @readonly
                     */
                    {
                        type        : 'duration',
                        localeClass : this,
                        label       : 'L{Lag}',
                        name        : 'lag',
                        ref         : 'lagField'
                    }
                ],

                bbar : [
                    {
                        type : 'widget',
                        cls  : 'b-label-filler'
                    },
                    /**
                     * Reference to the save button, if used
                     * @member {Core.widget.Button} saveButton
                     * @readonly
                     */
                    {
                        color       : 'b-green',
                        localeClass : this,
                        text        : 'L{Save}',
                        ref         : 'saveButton'
                    },
                    /**
                     * Reference to the delete button, if used
                     * @member {Core.widget.Button} deleteButton
                     * @readonly
                     */
                    {
                        color       : 'b-gray',
                        localeClass : this,
                        text        : 'L{Delete}',
                        ref         : 'deleteButton'
                    },
                    /**
                     * Reference to the cancel button, if used
                     * @member {Core.widget.Button} cancelButton
                     * @readonly
                     */
                    {
                        color       : 'b-gray',
                        localeClass : this,
                        text        : 'L{Cancel}',
                        ref         : 'cancelButton'
                    }
                ]
            }
        };
    }

    //endregion

    //region Init & destroy

    construct(client, config) {
        const me = this;

        client.dependencyEdit = me;

        me.dependencyStore = client.dependencyStore;

        super.construct(client, config);

        if (!client.features.dependencies) {
            throw new Error('Dependencies feature required when using DependencyEdit');
        }

        me.clientListenersDetacher = client.on({
            [me.triggerEvent] : me.onActivateEditor,
            thisObj           : me
        });
    }

    doDestroy() {
        this.clientListenersDetacher();

        this.editor && this.editor.destroy();

        super.doDestroy();
    }

    //endregion

    //region Editing

    get editorConfig() {
        return this._editorConfig;
    }

    set editorConfig(editorConfig) {
        const me = this,
            defaultEditorConfig = me.getDefaultConfiguration().editorConfig;

        // Apply editorConfig to the default editorConfig, allowing users to manipulate for example only bbar
        editorConfig = ObjectHelper.assign({}, defaultEditorConfig, editorConfig);

        editorConfig.items = editorConfig.items || [];

        me._editorConfig = editorConfig;
    }

    //endregion

    //region Save

    get isValid() {
        return Object.values(this.editor.widgetMap).every(field => {
            if (!field.name || field.hidden) {
                return true;
            }

            return field.isValid !== false;
        });
    }

    get values() {
        const values = {};

        this.editor.eachWidget(widget => {
            if (!widget.name || widget.hidden) return;

            values[widget.name] = widget.value;
        }, true);

        return values;
    }

    /**
     * Template method, intended to be overridden. Called before the dependency record has been updated.
     * @param {Scheduler.model.DependencyModel} dependencyRecord The dependency record
     *
     **/
    onBeforeSave(dependencyRecord) {}

    /**
     * Template method, intended to be overridden. Called after the dependency record has been updated.
     * @param {Scheduler.model.DependencyModel} dependencyRecord The dependency record
     *
     **/
    onAfterSave(dependencyRecord) {}

    /**
     * Updates record being edited with values from the editor
     * @private
     */
    updateRecord(dependency) {
        const values = this.values;

        dependency.beginBatch();

        dependency.set(values);

        if (this.lagField) {
            const lag = this.lagField.value;

            dependency.setLag(lag.magnitude, lag.unit);
        }

        if (this.typeField && 'type' in values) {
            dependency.set({ fromSide : null, toSide : null });
        }

        dependency.endBatch();
    }
    //endregion

    //region Events

    onPopupKeyDown({ event }) {
        if (event.key === 'Enter' && this.saveAndCloseOnEnter && event.target.tagName.toLowerCase() === 'input') {
            // Need to prevent this key events from being fired on whatever receives focus after the editor is hidden
            event.preventDefault();

            this.onSaveClick();
        }
    }

    onSaveClick() {
        if (this.save()) {
            this.editor.hide();
        }
    }

    onDeleteClick() {
        this.deleteDependency();
        this.editor.hide();
    }

    onCancelClick() {
        this.editor.hide();
    }

    //region Editing

    // Called from editDependency() to actually show the editor
    internalShowEditor(dependencyRecord) {
        const me               = this,
            scheduler        = me.client;

        let showPoint = me.lastPointerDownCoordinate;

        /**
         * Fires on the owning Scheduler before an dependency is displayed in the editor.
         * This may be listened for to allow an application to take over dependency editing duties. Returning `false`
         * stops the default editing UI from being shown.
         * @event beforeDependencyEdit
         * @param {Scheduler.view.Scheduler} source The scheduler
         * @param {Scheduler.feature.DependencyEdit} dependencyEdit The dependencyEdit feature
         * @param {Scheduler.model.DependencyModel} dependencyRecord The record about to be shown in the editor.
         * @preventable
         */
        if (scheduler.trigger('beforeDependencyEdit', {
            dependencyEdit : me,
            dependencyRecord
        }) === false) {
            return;
        }

        const editor = me.getEditor(dependencyRecord);

        me.loadRecord(dependencyRecord);

        /**
         * Fires on the owning Scheduler when the editor for a dependency is available but before it is shown. Allows
         * manipulating fields etc.
         * @event beforeDependencyEditShow
         * @param {Scheduler.view.Scheduler} source The scheduler
         * @param {Scheduler.feature.DependencyEdit} dependencyEdit The dependencyEdit feature
         * @param {Scheduler.model.DependencyModel} dependencyRecord The record about to be shown in the editor.
         * @param {Scheduler.view.DependencyEditor} editor The editor
         */
        scheduler.trigger('beforeDependencyEditShow', {
            dependencyEdit : me,
            dependencyRecord,
            editor
        });

        if (!showPoint) {
            const center = Rectangle.from(me.client.element).center;

            showPoint = [center.x - editor.width / 2, center.y - editor.height / 2];
        }

        editor.showByPoint(showPoint);
    }

    /**
     * Opens an {@link Scheduler.view.DependencyEditor DependencyEditor} to edit the passed dependency.
     * @param {Scheduler.model.DependencyModel} dependencyRecord The dependency to edit
     */
    editDependency(dependencyRecord) {
        if (this.client.readOnly) return;

        this.internalShowEditor(dependencyRecord);
    }

    //endregion

    //region Save

    /**
     * Gets an editor instance. Creates on first call, reuses on consecutive
     * @internal
     * @returns {Scheduler.view.DependencyEditor} Editor popup
     */
    getEditor() {
        const me = this;

        let { editor } = me;

        if (editor) {
            return editor;
        }

        editor = me.editor = new DependencyEditor(ObjectHelper.assign({
            dependencyEditFeature : me,
            autoShow              : false,
            anchor                : true,
            scrollAction          : 'realign',
            clippedBy             : [me.client.timeAxisSubGridElement, me.client.bodyContainer],
            constrainTo           : window,
            autoClose             : me.autoClose,
            cls                   : me.cls,
            listeners             : {
                keydown : me.onPopupKeyDown,
                thisObj : me
            }
        }, me.editorConfig));

        if (editor.items.length === 0) {
            console.warn('Editor configured without any `items`');
        }

        // assign widget refs
        editor.eachWidget(widget => {
            const ref = widget.ref || widget.id;
            // don't overwrite if already defined
            if (ref && !me[ref]) {
                me[ref] = widget;
            }
        });

        me.saveButton && me.saveButton.on('click', me.onSaveClick, me);
        me.deleteButton && me.deleteButton.on('click', me.onDeleteClick, me);
        me.cancelButton && me.cancelButton.on('click', me.onCancelClick, me);

        return me.editor;
    }

    //endregion

    //region Delete

    /**
     * Sets fields values from record being edited
     * @private
     */
    loadRecord(dependency) {
        const me = this;

        me.fromNameField.value = dependency.sourceEvent.name;
        me.toNameField.value = dependency.targetEvent.name;

        if (me.lagField) {
            me.lagField.unit = dependency.lagUnit;
        }

        me.editor.record = me.dependencyRecord = dependency;
    }

    //endregion

    //region Stores

    /**
     * Saves the changes (applies them to record if valid, if invalid editor stays open)
     * @private
     * @fires beforeDependencySave
     * @fires beforeDependencyAdd
     * @fires afterDependencySave
     * @returns {*}
     */
    save() {
        const me                                      = this,
            { client, dependencyRecord } = me;

        if (!dependencyRecord || !me.isValid) return;

        const dependencyStore = me.dependencyStore,
            values          = me.values;

        /**
         * Fires on the owning Scheduler before a dependency is saved
         * @event beforeDependencySave
         * @param {Scheduler.view.Scheduler} source The scheduler instance
         * @param {Scheduler.model.DependencyModel} dependencyRecord The dependency about to be saved
         * @param {Object} values The new values
         * @preventable
         */
        if (client.trigger('beforeDependencySave', {
            dependencyRecord,
            values
        }) !== false) {
            me.onBeforeSave(dependencyRecord);

            me.updateRecord(dependencyRecord);

            // Check if this is a new record
            if (dependencyStore && !dependencyRecord.stores.length) {
                /**
                 * Fires on the owning Scheduler before a dependency is added
                 * @event beforeDependencyAdd
                 * @param {Scheduler.view.Scheduler} source The scheduler
                 * @param {Scheduler.feature.DependencyEdit} dependencyEdit The dependency edit feature
                 * @param {Scheduler.model.DependencyModel} dependencyRecord The dependency about to be added
                 * @preventable
                 */
                if (client.trigger('beforeDependencyAdd', { dependencyRecord, dependencyEdit : me }) !== false) {
                    dependencyStore.add(dependencyRecord);
                }
                else {
                    return;
                }
            }

            client.project && client.project.propagate();

            /**
             * Fires on the owning Scheduler after a dependency is successfully saved
             * @event afterDependencySave
             * @param {Scheduler.view.Scheduler} source The scheduler instance
             * @param {Scheduler.model.DependencyModel} dependencyRecord The dependency about to be saved
             */
            client.trigger('afterDependencySave', { dependencyRecord });
            me.onAfterSave(dependencyRecord);
        }

        return dependencyRecord;
    }

    /**
     * Delete dependency being edited
     * @private
     * @fires beforeDependencyDelete
     */
    deleteDependency() {
        const me               = this;

        /**
         * Fires on the owning Scheduler before a dependency is deleted
         * @event beforeDependencyDelete
         * @param {Scheduler.view.Scheduler} source The scheduler instance
         * @param {Scheduler.model.DependencyModel} dependencyRecord The dependency record about to be deleted
         * @preventable
         */
        if (me.client.trigger('beforeDependencyDelete', { dependencyRecord : me.dependencyRecord }) !== false) {
            if (me.editor.containsFocus) {
                me.editor.revertFocus();
            }

            me.client.dependencyStore.remove(me.dependencyRecord);
            me.client.project && me.client.project.propagate();

            return true;
        }

        return false;
    }

    //endregion

    //region Events

    onActivateEditor({ dependency, event }) {
        if (!this.disabled) {
            this.lastPointerDownCoordinate = [event.clientX, event.clientY];
            this.editDependency(dependency);
        }
    }

    //endregion
}

GridFeatureManager.registerFeature(DependencyEdit, false);
