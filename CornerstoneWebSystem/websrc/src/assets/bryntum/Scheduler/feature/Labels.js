import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import GridFeatureManager from '../../Grid/feature/GridFeatureManager.js';
import DomHelper from '../../Core/helper/DomHelper.js';
import DateHelper from '../../Core/helper/DateHelper.js';
import Editor from '../../Core/widget/Editor.js';

// TODO: Gantt uses its own rendering, split this into LabelsBase + Labels.
//  LabelsBase = everthing except onEventPaint, appendLabel & updateLabel

/**
 * @module Scheduler/feature/Labels
 */

const sides = [
        'top',
        'left',
        'right',
        'bottom'
    ],
    editorAlign = {
        top    : 'b-b',
        right  : 'l-l',
        bottom : 't-t',
        left   : 'r-r'
    },
    topBottom = {
        top    : 1,
        bottom : 1
    };

/**
 * Displays labels at positions {@link #config-top}, {@link #config-right}, {@link #config-bottom} and {@link #config-left}.
 *
 * Text in labels can be set from a field on the {@link Scheduler.model.EventModel EventModel}
 * or the {@link Scheduler.model.ResourceModel ResourceModel} or using a custom renderer.
 *
 * Since `top` and `bottom` labels occupy space that would otherwise be used by the event we recommend using bigger rowHeights
 * (>55px for both labels with default styling) and zero barMargins because `top`/`bottom` labels give space around events anyway.
 *
 * This feature is **disabled** by default. It is **not** supported in vertical mode.
 *
 * @extends Core/mixin/InstancePlugin
 * @demo Scheduler/labels
 * @externalexample scheduler/Labels.js
 */
export default class Labels extends InstancePlugin {
    //region Config

    static get $name() {
        return 'Labels';
    }

    static get defaultConfig() {
        return {
            /**
             * CSS class to apply to label elements
             * @config {String}
             * @default
             */
            labelCls : 'b-sch-label',

            /**
             * Top label configuration object. May contain the following properties:
             * - field : [String](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String) The name of a field in one of the associated records,  {@link Scheduler.model.EventModel EventModel} or  {@link Scheduler.model.ResourceModel ResourceModel}. The record from which the field value is drawn will be ascertained by checking for field definitions by the specified name.<br>
             * - renderer : [Function](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Function) A function, which when passed an object containing `eventRecord`, `resourceRecord` and `labelElement` properties, returns the HTML to display as the label.<br>
             * - thisObj : [Object](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object) The `this` reference to use in the `renderer`.
             * - editor : [Object](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object) / {@link Core.widget.Field Field} If the label is to be editable, a field configuration object with a `type` property, or an instantiated Field. **The `field` property is mandatory for editing to work**.
             * @config {Object}
             * @default
             */
            top : null,

            /**
             * Right label configuration object. May contain the following properties:
             * - field : [String](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String) The name of a field in one of the associated records,  {@link Scheduler.model.EventModel EventModel} or  {@link Scheduler.model.ResourceModel ResourceModel}. The record from which the field value is drawn will be ascertained by checking for field definitions by the specified name.<br>
             * - renderer : [Function](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Function) A function, which when passed an object containing `eventRecord`, `resourceRecord` and `labelElement` properties, returns the HTML to display as the label.<br>
             * - thisObj : [Object](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object) The `this` reference to use in the `renderer`.
             * - editor : [Object](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object) / {@link Core.widget.Field Field} If the label is to be editable, a field configuration object with a `type` property, or an instantiated Field. **The `field` property is mandatory for editing to work**.
             * @config {Object}
             * @default
             */
            right : null,

            /**
             * Bottom label configuration object. May contain the following properties:
             * - field : [String](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String) The name of a field in one of the associated records,  {@link Scheduler.model.EventModel EventModel} or  {@link Scheduler.model.ResourceModel ResourceModel}. The record from which the field value is drawn will be ascertained by checking for field definitions by the specified name.<br>
             * - renderer : [Function](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Function) A function, which when passed an object containing `eventRecord`, `resourceRecord` and `labelElement` properties, returns the HTML to display as the label.<br>
             * - thisObj : [Object](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object) The `this` reference to use in the `renderer`.
             * - editor : [Object](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object) / {@link Core.widget.Field Field} If the label is to be editable, a field configuration object with a `type` property, or an instantiated Field. **The `field` property is mandatory for editing to work**.
             * @config {Object}
             * @default
             */
            bottom : null,

            /**
             * Left label configuration object. May contain the following properties:
             * - field : [String](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String) The name of a field in one of the associated records,  {@link Scheduler.model.EventModel EventModel} or  {@link Scheduler.model.ResourceModel ResourceModel}. The record from which the field value is drawn will be ascertained by checking for field definitions by the specified name.<br>
             * - renderer : [Function](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Function) A function, which when passed an object containing `eventRecord`, `resourceRecord` and `labelElement` properties, returns the HTML to display as the label.<br>
             * - thisObj : [Object](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object) The `this` reference to use in the `renderer`.
             * - editor : [Object](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object) / {@link Core.widget.Field Field} If the label is to be editable, a field configuration object with a `type` property, or an instantiated Field. **The `field` property is mandatory for editing to work**.
             * @config {Object}
             * @default
             */
            left : null,

            thisObj : null,

            /**
             * What action should be taken when focus moves leaves the cell editor, for example when clicking outside.
             * May be `'complete'` or `'cancel`'.
             * @config {String}
             * @default
             */
            blurAction : 'cancel'
        };
    }

    // Plugin configuration. This plugin chains some of the functions in Grid.
    static get pluginConfig() {
        return {};
    }

    //endregion

    //region Init & destroy

    construct(scheduler, config) {
        const me = this;

        if (scheduler.isVertical) {
            throw new Error('Labels feature is not supported in vertical mode');
        }

        me.scheduler = scheduler;
        me.labelElements = {};

        super.construct(scheduler, config);

        const { top, bottom, left, right } = me;

        if (top || bottom || left || right) {
            me.schedulerDetatcher = scheduler.on({
                eventpaint   : 'onEventPaint',
                eventrepaint : 'onEventPaint',
                thisObj      : me
            });

            me.updateHostClasslist();

            // rowHeight warning, not in use
            //const labelCount = !!me.topLabel + !!me.bottomLabel;
            //if (scheduler.rowHeight < 60 - labelCount * 12) console.log('')
        }
    }

    updateHostClasslist() {
        const { top, bottom } = this,
            { classList } = this.scheduler.element;

        classList.remove('b-labels-topbottom');
        classList.remove('b-labels-top');
        classList.remove('b-labels-bottom');

        // OR is correct. This means that there are labels above OR below.
        if (top || bottom) {
            classList.add('b-labels-topbottom');
            if (top) {
                classList.add('b-labels-top');
            }
            if (bottom) {
                classList.add('b-labels-bottom');
            }
        }
    }

    onEventDblClick(eventDblclick) {
        const me = this,
            { event } = eventDblclick,
            target = event.target.closest('.b-sch-label');

        if (target && !me.scheduler.readOnly) {
            const { side } = target.dataset,
                labelConfig = me[side],
                { editor, field, recordType } = labelConfig;

            if (editor) {
                if (!(editor instanceof Editor)) {
                    labelConfig.editor = new Editor({
                        appendTo     : me.scheduler.element,
                        blurAction   : me.blurAction,
                        inputField   : editor,
                        scrollAction : 'realign'
                    });
                }

                labelConfig.editor.startEdit({
                    target,
                    align     : editorAlign[side],
                    matchSize : false,
                    record    : eventDblclick[`${recordType}Record`],
                    field
                });

                event.stopImmediatePropagation();
                return false;
            }
        }
    }

    set top(top) {
        this._top = this.processLabelSpec(top, 'top');
        this.updateHostClasslist();
    }
    get top() {
        return this._top;
    }

    set right(right) {
        this._right = this.processLabelSpec(right, 'right');
        this.updateHostClasslist();
    }
    get right() {
        return this._right;
    }

    set bottom(bottom) {
        this._bottom = this.processLabelSpec(bottom, 'bottom');
        this.updateHostClasslist();
    }
    get bottom() {
        return this._bottom;
    }

    set left(left) {
        this._left = this.processLabelSpec(left, 'left');
        this.updateHostClasslist();
    }
    get left() {
        return this._left;
    }

    processLabelSpec(labelSpec, side) {
        if (typeof labelSpec === 'function') {
            labelSpec = {
                renderer : labelSpec
            };
        }
        else if (typeof labelSpec === 'string') {
            labelSpec = {
                field : labelSpec
            };
        }
        // Allow us to mutate ownProperties in the labelSpec without mutating outside object
        else if (labelSpec) {
            labelSpec = Object.setPrototypeOf({}, labelSpec);
        }
        else {
            return;
        }

        const { scheduler } = this,
            { eventStore, resourceStore, taskStore, id } = scheduler,
            { field, editor } = labelSpec;

        // If there are milestones, and we are changing the available height
        // either by adding a top/bottom label, or adding a top/bottom label
        // then during the next dependency refresh, milestone width must be recalculated.
        if (topBottom[side]) {
            scheduler.milestoneWidth = null;
        }

        // Find the field definition or property from whichever store and cache the type.
        if (field) {
            let fieldDef;

            if (eventStore && !taskStore) {
                fieldDef = eventStore.modelClass.fieldMap[field];
                if (fieldDef) {
                    labelSpec.fieldDef = fieldDef;
                    labelSpec.recordType = 'event';
                }
                // Check if it references a property
                else if (Reflect.has(eventStore.modelClass.prototype, field)) {
                    labelSpec.recordType = 'event';
                }
            }

            if (!fieldDef && taskStore) {
                fieldDef = taskStore.modelClass.fieldMap[field];
                if (fieldDef) {
                    labelSpec.fieldDef = fieldDef;
                    labelSpec.recordType = 'task';
                }
                // Check if it references a property
                else if (Reflect.has(resourceStore.modelClass.prototype, field)) {
                    labelSpec.recordType = 'task';
                }
            }

            if (!fieldDef && resourceStore) {
                fieldDef = resourceStore.modelClass.fieldMap[field];
                if (fieldDef) {
                    labelSpec.fieldDef = fieldDef;
                    labelSpec.recordType = 'resource';
                }
                // Check if it references a property
                else if (Reflect.has(resourceStore.modelClass.prototype, field)) {
                    labelSpec.recordType = 'resource';
                }
            }
            //<debug>
            // We couldn't find the requested field in the modelClass
            // for either of the stores.
            if (!labelSpec.recordType) {
                throw new Error(`Scheduler ${id} labels ${side} field ${field} does not exist in either eventStore or resourceStore`);
            }
            //</debug>

            if (editor) {
                if (typeof editor === 'boolean') {
                    scheduler.editor = {
                        type : 'textfield'
                    };
                }
                else if (typeof editor === 'string') {
                    scheduler.editor = {
                        type : editor
                    };
                }
                scheduler.on({
                    eventdblclick : 'onEventDblClick',
                    taskdblclick  : 'onEventDblClick',
                    thisObj       : this
                });
            }
        }

        //<debug>
        if (!labelSpec.field && !labelSpec.renderer) {
            throw new Error(`Scheduler ${scheduler.id} labels ${side} must either have a field or a renderer`);
        }
        //</debug>

        return labelSpec;
    }

    doDestroy() {
        this.schedulerDetatcher && this.schedulerDetatcher();
        super.doDestroy();
    }

    doDisable(disable) {
        super.doDisable(disable);

        if (this.client.isPainted) {
            this.client.refresh();
        }
    }

    //endregion

    //region Events

    /**
     * Called when a new event element is created or updated. Make correct label state.
     * @private
     */
    onEventPaint(paintEvent) {
        // Tear down old label setup (we may have been reconfigured)
        DomHelper.removeEachSelector(paintEvent.element, '.b-sch-label');

        if (!this.disabled) {
            // Insert all configured labels
            for (const side of sides) {
                if (this[side]) {
                    this.appendLabel(side, paintEvent.element, paintEvent);
                }
            }
        }
    }

    //endregion

    //region Labels

    /**
     * Appends a label to a wrapping element
     * @private
     * @param side
     * @param appendTo
     * @param eventRecord
     */
    appendLabel(side, appendTo, paintEvent) {
        this.labelElements[side] = DomHelper.createElement({
            tag     : 'label',
            dataset : { side },
            parent  : appendTo
        });

        this.updateLabel(side, paintEvent);
    }

    /**
     * Updates a labels content
     * @private
     * @param side
     * @param eventRecord
     */
    updateLabel(side, paintEvent) {
        const me            = this,
            {
                field,
                fieldDef,
                recordType,
                renderer,
                thisObj
            }             = me[side],
            labelElement  = me.labelElements[side];

        let value;

        // Correct class name in case of element recycling from another render condition.
        labelElement.className = `${me.labelCls} ${me.labelCls}-${side}`;

        // If there's a renderer, use that by preference
        if (renderer) {
            value = renderer.call(thisObj || me.thisObj || me, Object.assign({ labelElement }, paintEvent));
        }
        else {
            value = paintEvent[`${recordType}Record`][field];

            // If it's a date, format it according to the Scheduler's defaults
            if (fieldDef && fieldDef.type === 'date' && !renderer) {
                value = DateHelper.format(value, me.scheduler.displayDateFormat);
            }
        }

        labelElement.innerHTML = value || '\xa0';
    }

    //endregion
}

// TODO: Refactor the SASS, so that the auto-generated class name of ''b-' + cls.name.toLowerCase() can be used.
Labels.featureClass = 'b-sch-labels';

GridFeatureManager.registerFeature(Labels, false, 'Scheduler');
