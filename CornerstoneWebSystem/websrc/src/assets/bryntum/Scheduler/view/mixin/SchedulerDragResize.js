import Base from '../../../Core/Base.js';

/**
 * @module Scheduler/view/mixin/SchedulerDragResize
 */

/**
 * Mixin for triggering event editor on drag creation etc.
 * @private
 * @mixin
 */
export default Target => class SchedulerDragResize extends (Target || Base) {
    construct(config) {
        const me = this;

        super.construct(config);

        if (me.hasFeature('eventDragCreate')) {
            me.on({
                dragcreateend : me.internalOnDragCreateEnd,
                thisObj       : me
            });
        }
    }

    internalOnDragCreateEnd({ newEventRecord : eventRecord, resourceRecord }) {
        const me = this;

        // If an event editor is defined and enabled, it has to manage how/if/when the event is added to the event store

        if (!me.hasFeature('eventEdit') || me.features.eventEdit.disabled) {
            const resources = resourceRecord ? [resourceRecord] : [];

            // resources **Deprecated** Use `resourceRecords` instead
            if (me.trigger('beforeEventAdd', { eventRecord, resources, resourceRecords : resources }) !== false) {
                me.onEventCreated(eventRecord);
                me.eventStore.add(eventRecord);

                eventRecord.assign(resourceRecord);
            }
        }
    }

    get eventEditor() {
        return this._eventEditor;
    }

    set eventEditor(editor) {
        this._eventEditor = editor;
    }

    // This does not need a className on Widgets.
    // Each *Class* which doesn't need 'b-' + constructor.name.toLowerCase() automatically adding
    // to the Widget it's mixed in to should implement thus.
    get widgetClass() {}
};
