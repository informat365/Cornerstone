import Popup from '../../Core/widget/Popup.js';

/**
 * @module Scheduler/view/DependencyEditor
 */

/**
 * A dependency editor popup.
 *
 * @extends Core/widget/Popup
 * @private
 */
export default class DependencyEditor extends Popup {

    static get $name() {
        return 'DependencyEditor';
    }

    static get defaultConfig() {
        return {
            items     : [],
            draggable : {
                handleSelector : ':not(button,.b-field-inner)' // blacklist buttons and field inners
            },
            axisLock : 'flexible'
        };
    }

    processWidgetConfig(widget) {
        const dependencyEditFeature = this.dependencyEditFeature;

        let fieldConfig = {};

        if (widget.ref === 'lagField' && !dependencyEditFeature.showLagField) {
            return false;
        }
        if (widget.ref === 'deleteButton' && !dependencyEditFeature.showDeleteButton) {
            return false;
        }

        Object.assign(widget, fieldConfig);

        return super.processWidgetConfig(widget);
    }

    afterShow(...args) {
        const deleteButton = this.widgetMap.deleteButton;

        // Only show delete button if the dependency record belongs to a store
        if (deleteButton) {
            deleteButton.hidden = !this.record.isPartOfStore();
        }
        super.afterShow(...args);
    }

    onInternalKeyDown(event) {
        this.trigger('keyDown', { event });
        super.onInternalKeyDown(event);
    }
}
