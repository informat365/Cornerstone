import Base from '../../../Core/Base.js';
import DomHelper from '../../../Core/helper/DomHelper.js';
import DomClassList from '../../../Core/helper/util/DomClassList.js';
import HorizontalLayoutStack from '../../eventlayout/HorizontalLayoutStack.js';
import HorizontalLayoutPack from '../../eventlayout/HorizontalLayoutPack.js';
import TimeSpan from '../../model/TimeSpan.js';
import ResourceModel from '../../model/ResourceModel.js';

/**
 * @module Scheduler/view/mixin/SchedulerEventRendering
 */

const hyphenRe = /-/g;

/**
 * Functions to handle event rendering (EventModel -> dom elements).
 *
 * @mixin
 */
export default Target => class SchedulerEventRendering extends (Target || Base) {
    //region Default config

    static get defaultConfig() {
        return {
            /**
             * An empty function by default, but provided so that you can override it. This function is called each time
             * an event is rendered into the schedule to render the contents of the event. It's called with the event,
             * its resource and a tplData object which allows you to populate data placeholders inside the event
             * template. **IMPORTANT** You should never modify any data on the EventModel inside this method.
             * By default, the DOM markup of an event bar includes placeholders for 'cls' and 'style'. The cls property is a
             * {@link Core.helper.util.DomClassList} which will be added to the event element. The style property is an inline style declaration for
             * the event element.
             *
             * ```javascript
             * eventRenderer({eventRecord, resourceRecord, tplData}) {
             *   tplData.style = 'color:white';                 // You can use inline styles too.
             *
             *   // Property names with truthy values are added to the resulting elements CSS class.
             *   tplData.cls.isImportant = this.isImportant(eventRecord);
             *   tplData.cls.isModified = eventRecord.isModified;
             *
             *   // Remove a class name by setting the property to false
             *   tplData.cls[scheduler.generatedIdCls] = false;
             *
             *   // Or, you can treat it as a string, but this is less efficient, especially
             *   // if your renderer wants to *remove* classes that may be there.
             *   tplData.cls += ' extra-class'
             *
             *   return DateHelper.format(eventRecord.startDate, 'YYYY-MM-DD') + ': ' + eventRecord.name;
             * }
             * ```
             *
             * @param {Object} detail An object containing the information needed to render an Event.
             * @param {Scheduler.model.EventModel} detail.eventRecord The event record.
             * @param {Scheduler.model.ResourceModel} detail.resourceRecord The resource record.
             * @param {Scheduler.model.AssignmentModel} detail.assignmentRecord The assignment record, if using an AssignmentStore.
             * @param {Object} detail.tplData An object containing details about the event rendering.
             * @param {Scheduler.model.EventModel} detail.tplData.event The event record.
             * @param {Core.helper.util.DomClassList|String} detail.tplData.cls An object whose property names represent the CSS class names
             * to be added to the event bar element. Set a property's value to truthy or falsy to add or remove the class
             * name based on the property name. Using this technique, you do not have to know whether the class is already
             * there, or deal with concatenation.
             * @param {Core.helper.util.DomClassList|String} detail.tplData.wrapperCls An object whose property names represent the CSS class names
             * to be added to the event wrapper element. Set a property's value to truthy or falsy to add or remove the class
             * name based on the property name. Using this technique, you do not have to know whether the class is already
             * there, or deal with concatenation.
             * @param {Core.helper.util.DomClassList|String} detail.tplData.iconCls An object whose property names represent the CSS class
             * names to be added to an event icon element.
             * @param {Number} detail.tplData.left Vertical offset position (in pixels) on the time axis.
             * @param {Number} detail.tplData.width Width in pixels of the event element.
             * @param {Number} detail.tplData.height Height in pixels of the event element.
             * @param {String} detail.tplData.eventStyle The `eventStyle` of the event. Use this to apply custom styles to the event DOM element
             * @param {String} detail.tplData.eventColor The `eventColor` of the event. Use this to set a custom color for the rendererd event
             * @returns {String|Object} A simple string, or a custom object which will be applied to the {@link #config-eventBodyTemplate}, creating the actual HTML
             * @config {function}
             * @category Scheduled events
             */
            eventRenderer : null,

            /**
             * `this` reference for the {@link #config-eventRenderer} function
             * @config {Object}
             * @category Scheduled events
             */
            eventRendererThisObj : null,

            eventPrefix : '',

            /**
             * How to handle overlapping events. Valid values are:
             * - stack, adjusts row height (only horizontal)
             * - pack, adjusts event height
             * - mixed, allows two events to overlap, more packs (only vertical)
             * - none, allows events to overlap
             * @config {String}
             * @default
             * @category Scheduled events
             */
            eventLayout : 'stack',

            /**
             * The class responsible for the packing horizontal event layout process.
             * Override this to take control over the layout process.
             * @config {Scheduler.eventlayout.HorizontalLayout}
             * @default
             * @private
             * @category Misc
             */
            horizontalLayoutPackClass : HorizontalLayoutPack,

            /**
             * The class name responsible for the stacking horizontal event layout process.
             * Override this to take control over the layout process.
             * @config {Scheduler.eventlayout.HorizontalLayout}
             * @default
             * @private
             * @category Misc
             */
            horizontalLayoutStackClass : HorizontalLayoutStack,

            /**
             * Override this method to provide a custom sort function to sort any overlapping events. By default,
             * overlapping events are laid out based on the start date. If the start date is equal, events with earlier
             * end date go first.
             *
             * Here's a sample sort function, sorting on start- and end date. If this function returns -1, then event a
             * is placed above event b.
             * ```javascript
             * horizontalEventSorterFn(a, b) {
             *
             *   const startA = a.startDate, endA = a.endDate;
             *   const startB = b.startDate, endB = b.endDate;
             *
             *   const sameStart = (startA - startB === 0);
             *
             *   if (sameStart) {
             *     return endA > endB ? -1 : 1;
             *   } else {
             *     return (startA < startB) ? -1 : 1;
             *   }
             * }
             * ```
             * @param  {Scheduler.model.EventModel} a First event
             * @param  {Scheduler.model.EventModel} b Second event
             * @return {Number} Return -1 to display a above b, 1 for b above a
             * @config {function}
             * @category Misc
             */
            horizontalEventSorterFn : null,

            /**
             * Field from EventModel displayed as text in the bar when rendering
             * @config {String}
             * @default
             * @category Scheduled events
             */
            eventBarTextField : 'name',

            /**
             * The template used to generate the markup of your events in the scheduler. To 'populate' the
             * eventBodyTemplate with data, use the {@link #config-eventRenderer} method
             * @config {Function}
             * @category Scheduled events
             */
            eventBodyTemplate : null,

            eventPositionMode : 'translate',
            eventScrollMode   : 'move',

            /**
             * Specify `true` to force rendered events to fill entire ticks. This only affects rendering, events retain
             * their set start and end dates on the data level. When enabling this config you should probably also
             * disable EventDrag and EventResize, otherwise their behaviour will not be what a user expects.
             * @config {Boolean}
             * @default
             * @category Scheduled events
             */
            fillTicks : false,

            /**
             * By default scheduler fades events in on load. Specify `false` to prevent this animation or specify one
             * of the available animation types to use it (`true` equals `'fade-in'`):
             * * fade-in (default)
             * * slide-from-left
             * * slide-from-top
             * ```
             * // Slide events in from the left on load
             * scheduler = new Scheduler({
             *     useInitialAnimation : 'slide-from-left'
             * });
             * ```
             * @config {Boolean|String}
             * @default
             * @category Misc
             */
            useInitialAnimation : true,

            /**
             * A config object used to configure the resource columns in vertical mode.
             * See {@link Scheduler.view.ResourceHeader} for more details on available properties.
             *
             * ```
             * new Scheduler({
             *   resourceColumns : {
             *     columnWidth    : 100,
             *     headerRenderer : ({ resourceRecord }) => `${resourceRecord.id} - ${resourceRecord.name}`
             *   }
             * })
             * ```
             * @config {Object}
             * @category Resources
             */
            resourceColumns : null,

            /**
             * Path to load resource images from. Used by the resource header in vertical mode and the
             * {@link Scheduler.column.ResourceInfoColumn} in horizontal mode. Set this to display miniature
             * images for each resource using their `imageUrl` field. If set and a resource has not `imageUrl` specified
             * it will try using the resources name with `.jpg` appended.
             *
             * **NOTE**: The path should end with a `/`:
             *
             * ```
             * new Scheduler({
             *   resourceImagePath : 'images/resources/'
             * });
             * ```
             * @config {String}
             * @category Resources
             */
            resourceImagePath : null,

            /**
             * Generic resource image, used when `imageUrl` or `name.jpg` for a resource is invalid
             * @config {String}
             * @category Resources
             */
            defaultResourceImageName : 'none.png',

            /**
             * Control how much space to leave between the first event/last event and the resources edge (top/bottom
             * margin within the resource row in horizontal mode, left/right margin within the resource column in
             * vertical mode), in px. Defaults to the value of {@link Scheduler.view.mixin.TimelineEventRendering#config-barMargin}.
             * @config {Number}
             * @category Scheduled events
             */
            resourceMargin : null,

            // Used to animate events on first render
            isFirstRender : true
        };
    }

    //endregion

    //region Init

    get eventPrefix() {
        return this._eventPrefix;
    }

    set eventPrefix(eventPrefix) {
        this._eventPrefix = eventPrefix || this.id + '-';
    }

    //endregion

    //region Settings

    get layouts() {
        const me = this;

        if (!me._eventLayout) {
            me._eventLayout = {};

            // pack, fit all events in available height by adjusting their height
            if (me.horizontalLayoutPackClass) {
                me._eventLayout.horizontalPack = new me.horizontalLayoutPackClass(
                    Object.assign(
                        // this is required for table layout
                        {
                            scheduler                   : me,
                            timeAxisViewModel           : me.timeAxisViewModel,
                            bandIndexToPxConvertFn      : me.horizontal.layoutEventVerticallyPack,
                            bandIndexToPxConvertThisObj : me.horizontal
                        }
                    )
                );
            }

            // stack, adjust row height to fit all events
            if (me.horizontalLayoutStackClass) {
                me._eventLayout.horizontalStack = new me.horizontalLayoutStackClass(
                    Object.assign(
                        // this is required for table layout
                        {
                            scheduler                   : me,
                            timeAxisViewModel           : me.timeAxisViewModel,
                            bandIndexToPxConvertFn      : me.horizontal.layoutEventVerticallyStack,
                            bandIndexToPxConvertThisObj : me.horizontal
                        }
                    )
                );
            }
        }

        return me._eventLayout;
    }

    /**
     * Get/set overlap mode. See config, valid values are stack (horizontal), pack, mixed (vertical) and none
     * @property {String}
     * @category Scheduled events
     */
    get eventLayout() {
        return this._overlapMode;
    }

    set eventLayout(eventLayout) {
        const me = this;

        if (eventLayout != me._overlapMode) {
            me.element.classList.remove(`b-eventlayout-${me._overlapMode}`);
            me._overlapMode = eventLayout;

            me.runWithTransition(() => {
                me.element.classList.add(`b-eventlayout-${me._overlapMode}`);
                me.refresh();
            });
        }
    }

    /**
     * Get/set fillTicks setting. If set to true it forces the rendered events to fill entire ticks.
     * @property {String}
     * @category Scheduled events
     */
    get fillTicks() {
        return this._fillTicks;
    }

    set fillTicks(fill) {
        const me = this;

        if (fill != me._fillTicks) {
            me._fillTicks = fill;
            me.refreshWithTransition();
        }
    }

    /**
     * Control how much space to leave between the first event/last event and the resources edge (top/bottom margin within
     * the resource row in horizontal mode, left/right margin within the resource column in vertical mode),
     * in px. Defaults to the value of {@link Scheduler.view.mixin.TimelineEventRendering#config-barMargin}.
     * @member {Number} resourceMargin
     * @category Scheduled events
     */

    /**
     * Gets currently used event layout class. The event layout class decides the vertical placement of the events
     * within a resource. Returns null if no eventLayout is used (if Scheduler#eventLayout is set to "none")
     * @internal
     * @returns {*}
     * @readonly
     * @category Scheduled events
     */
    get currentEventLayout() {
        const me = this;

        if (!me.isHorizontal) return null;

        switch (me.eventLayout) {
            case 'stack':
                return me.layouts.horizontalStack;
            case 'pack':
                return me.layouts.horizontalPack;
            default:
                return null;
        }
    }

    get useInitialAnimation() {
        return this._useInitialAnimation;
    }

    set useInitialAnimation(name) {
        const me = this;

        if (me._useInitialAnimation) {
            me.element.classList.remove(`b-initial-${me._useInitialAnimation}`);
        }

        me._useInitialAnimation = (name === true ? 'fade-in' : name);

        if (name) {
            me.element.classList.add(`b-initial-${me._useInitialAnimation}`);
        }
    }

    set isFirstRender(value) {
        const me = this;

        me._isFirstRender = value;

        if (!me._firstRenderDone && value) {
            me._firstRenderDone = me.createOnFrame(() => {
                me._isFirstRender = false;
                me._firstRenderDone = null;
            });
        }
    }

    get isFirstRender() {
        return this._isFirstRender;
    }

    set horizontalEventSorterFn(fn) {
        // Wrap sorter to work on events instead of template data
        this._horizontalEventSorterFn = fn ? (a, b) => fn(a.event, b.event) : fn;

        if (this.rendered) {
            this.refreshWithTransition();
        }
    }

    get horizontalEventSorterFn() {
        return this._horizontalEventSorterFn;
    }

    //endregion

    //region Resource header/columns

    // NOTE: The configs below are initially applied to the resource header in `TimeAxisColumn#set mode`

    set resourceColumns(config) {
        this._resourceColumns = config;
    }

    /**
     * Use it to manipulate resource column properties at runtime.
     * @property {Scheduler.view.ResourceHeader}
     * @readonly
     */
    get resourceColumns() {
        return this.timeAxisColumn && this.timeAxisColumn.resourceColumns || this._resourceColumns;
    }

    /**
     * Get resource column width. Only applies to vertical mode. To set it, assign to
     * `scheduler.resourceColumns.columnWidth`.
     * @property {Number}
     * @readonly
     */
    get resourceColumnWidth() {
        return this.resourceColumns ? this.resourceColumns.columnWidth : null;
    }

    //endregion

    //region Event rendering

    // Chainable function called with the events to render for a specific resource. Allows features to add/remove.
    // Chained by ResourceTimeRanges
    getEventsToRender(resource, events) {
        return events;
    }

    /**
     * Rerenders events for specified resource (by rerendering the entire row).
     * @param {Scheduler.model.ResourceModel} resourceRecord
     */
    repaintEventsForResource(resourceRecord/*, refreshSelections*/) {
        const me           = this;

        if (me.isHorizontal) {
            me.currentOrientation.cache.clearRow(resourceRecord.id);

            const row = me.getRowFor(resourceRecord);
            if (row) {
                // Update the affected row, if it changes height RowManger will take care of translating the rest of the rows
                row.render();
            }
        }

        // TODO: PORT selection model not ported yet
        // if (refreshSelections) {
        //     const sm     = me.getEventSelectionModel(),
        //           events = me.getEventStore().getEventsForResource(resourceRecord);
        //
        //     events.forEach(ev =>
        //         sm.forEachEventRelatedSelection(ev, selectedRecord =>
        //             me.onEventBarSelect(selectedRecord, true)
        //         )
        //     );
        // }
    }

    /**
     * Rerenders the events for all resources connected to the specified event
     * @param {Scheduler.model.EventModel} eventRecord
     * @private
     */
    repaintEvent(eventRecord) {
        const resources = this.eventStore.getResourcesForEvent(eventRecord);
        resources.forEach(resourceRecord => this.repaintEventsForResource(resourceRecord));
    }

    //endregion

    //region Template

    /**
     * Generates data used in the template when rendering an event. For example which css classes to use. Also applies
     * #eventBodyTemplate and calls the {@link #config-eventRenderer}.
     * @private
     * @param {Scheduler.model.EventModel} eventRecord Event to generate data for
     * @param {Scheduler.model.ResourceModel} resourceRecord Events resource
     * @param {Boolean|Object} includeOutside Specify true to get boxes for timespans outside of the rendered zone in both
     * dimensions. This option is used when calculating dependency lines, and we need to include routes from timespans
     * which may be outside the rendered zone.
     * @param {Boolean} includeOutside.timeAxis Pass as `true` to include timespans outside of the TimeAxis's bounds
     * @param {Boolean} includeOutside.viewport Pass as `true` to include timespans outside of the vertical timespan viewport's bounds.
     * @returns {Object} Data to use in event template, or `undefined` if the event is outside of the rendered zone.
     */
    generateTplData(eventRecord, resourceRecord, includeOutside = { viewport : true }) {
        const
            me         = this,
            // generateTplData calculates layout for events which are outside of the vertical viewport
            // because the RowManager needs to know a row height.
            renderData = me.currentOrientation.getTimeSpanRenderData(eventRecord, resourceRecord, includeOutside);

        let eventContent = '';

        if (renderData) {
            let resizable = eventRecord.isResizable;
            if (renderData.startsOutsideView) {
                if (resizable === true) resizable = 'end';
                else if (resizable === 'start') resizable = false;
            }
            if (renderData.endsOutsideView) {
                if (resizable === true) resizable = 'start';
                else if (resizable === 'end') resizable = false;
            }

            // Event record cls properties are now DomClassList instances, so clone them
            // so that they can be manipulated here and by renderers.
            // Truthy value means the key will be added as a class name.
            // ResourceTimeRanges applies custom cls to wrapper.
            const
                clsList = eventRecord.isResourceTimeRange ? new DomClassList() : eventRecord.cls.clone(),
                wrapperClsList = eventRecord.isResourceTimeRange ? eventRecord.cls.clone() : new DomClassList();

            Object.assign(clsList, {
                [resourceRecord.cls]      : resourceRecord.cls,
                [me.generatedIdCls]       : eventRecord.hasGeneratedId,
                [me.dirtyCls]             : eventRecord.isPersistable && eventRecord.modifications,
                [me.committingCls]        : eventRecord.isCommitting,
                [me.endsOutsideViewCls]   : renderData.endsOutsideView,
                [me.startsOutsideViewCls] : renderData.startsOutsideView,
                'b-clipped-start'         : renderData.clippedStart,
                'b-clipped-end'           : renderData.clippedEnd
            });
            Object.assign(wrapperClsList, {
                [`${me.eventCls}-parent`] : resourceRecord.isParent
            });

            // Event specifics, things that do not apply to ResourceTimeRanges
            if (eventRecord.isEvent || eventRecord.isTask) {
                Object.assign(clsList, {
                    [me.eventCls]                          : 1,
                    'b-milestone'                          : eventRecord.isMilestone,
                    'b-sch-event-narrow'                   : renderData.width < 10,
                    [me.fixedEventCls]                     : eventRecord.isDraggable === false,
                    [`b-sch-event-resizable-${resizable}`] : Boolean(me.features.eventResize),
                    [me.eventSelectedCls]                  : me.isEventSelected(eventRecord),
                    'b-recurring'                          : eventRecord.isRecurring,
                    'b-occurrence'                         : eventRecord.isOccurrence
                });

                renderData.eventId  = eventRecord.id;
                // this is important for getElement(s)FromEventRecord()
                renderData.id = me.getEventRenderId(eventRecord, resourceRecord);

                // If id has changed we want to reuse the element for the old id, to not steal some other events element
                // and to allow any other changes from the server to be animated
                if ('id' in eventRecord.meta.modified) {
                    renderData.oldId = me.getEventRenderId(eventRecord.meta.modified.id, resourceRecord);
                }

                Object.assign(wrapperClsList, {
                    [`${me.eventCls}-wrap`] : 1,
                    'b-milestone-wrap'      : eventRecord.isMilestone
                });

                const
                    eventStyle = eventRecord.eventStyle || resourceRecord.eventStyle || me.eventStyle,
                    eventColor = eventRecord.eventColor || resourceRecord.eventColor || me.eventColor;

                renderData.eventColor = eventColor;
                renderData.eventStyle = eventStyle;

                // Using multi assignment? Supply AssignmentModel to rendering process also. Might not be available
                // if adding a new event and using EventEdit, since it might be converting a dragproxy prior to adding
                // to store. Not needed in that case anyway
                if (me.assignmentStore && eventRecord.assignments) {
                    renderData.assignment = eventRecord.assignments.find(a => a.resourceId === resourceRecord.id);
                }
            }

            // If not using a wrapping div, this cls will be added to event div for correct rendering
            renderData.wrapperCls = wrapperClsList;

            renderData.cls = clsList;
            renderData.iconCls = new DomClassList(eventRecord.get(me.eventBarIconClsField) || eventRecord.iconCls);

            // ResourceTimeRanges applies custom style to the wrapper
            if (eventRecord.isResourceTimeRange) {
                renderData.style = '';
                renderData.wrapperStyle = eventRecord.style || '';
            }
            // Others to inner
            else {
                renderData.style = eventRecord.style || '';
            }

            renderData.resource = resourceRecord;
            renderData.resourceId = renderData.rowId;

            if (eventRecord.isEvent || eventRecord.isTask) {
                if (me.eventRenderer) {
                    // User has specified a renderer fn, either to return a simple string, or an object intended for the eventBodyTemplate
                    const
                        value = me.eventRenderer.call(me.eventRendererThisObj || me, {
                            eventRecord,
                            resourceRecord,
                            assignmentRecord : renderData.assignment,
                            tplData          : renderData
                        });

                    // If the user's renderer coerced it into a string, recreate a DomClassList.
                    if (typeof renderData.cls === 'string') {
                        renderData.cls = new DomClassList(renderData.cls);
                    }

                    if (typeof renderData.wrapperCls === 'string') {
                        renderData.wrapperCls = new DomClassList(renderData.wrapperCls);
                    }

                    // Same goes for iconCls
                    if (typeof renderData.iconCls === 'string') {
                        renderData.iconCls = new DomClassList(renderData.iconCls);
                    }

                    eventContent = (me.eventBodyTemplate && me.eventBodyTemplate(value)) || (value == null ? '' : String(value));
                }
                else if (me.eventBodyTemplate) {
                    // User has specified an eventBodyTemplate, but no renderer - just apply the entire event record data.
                    eventContent = me.eventBodyTemplate(eventRecord);
                }
                else if (me.eventBarTextField) {
                    // User has specified a field in the data model to read from
                    eventContent = eventRecord.data[me.eventBarTextField] || '';
                }

                if (!me.eventBodyTemplate) {
                    // Give milestone a dedicated label element so we can use padding
                    if (eventRecord.isMilestone && eventContent) {
                        eventContent = `<label>${eventContent}</label>`;
                    }

                    if (renderData.iconCls && renderData.iconCls.length) {
                        eventContent = `<i class="${renderData.iconCls}"></i>${eventContent}`;
                    }
                }

                // renderers have last say on style & color
                renderData.wrapperCls[`b-sch-style-${renderData.eventStyle}`] = renderData.eventStyle;

                if (renderData.eventColor && renderData.eventColor.startsWith('#')) {
                    renderData.style = `background-color:${renderData.eventColor};` + renderData.style;
                }
                else {
                    renderData.wrapperCls[`b-sch-color-${renderData.eventColor}`] = renderData.eventColor;
                }
            }

            // If there are any iconCls entries...
            renderData.cls['b-sch-event-withicon'] = renderData.iconCls.length;

            // html, use templates fragment
            if (eventContent.includes('<')) {
                // Create content as a DocumentFragment which may now be exposed to Features.
                renderData.body = DomHelper.createElementFromTemplate(eventContent, {
                    fragment : true
                });
            }
            // plain text, create fragment with the text in it
            else {
                renderData.body = document.createDocumentFragment();
                renderData.body.textContent = eventContent;
            }

            // For comparison in sync, cheaper than comparing DocumentFragments
            renderData.eventContent = eventContent;

            // Method which features may chain in to
            me.onEventDataGenerated(renderData);
        }

        return renderData;
    }

    /**
     * A method which may be chained by features. It is called when an event's render
     * data is calculated so that features may update the style, class list or body.
     * @param {Object} eventData
     */
    onEventDataGenerated(eventData) {

    }

    /**
     * Generates the element `id` for an event element. This is used when
     * recycling an event div which has been moved from one resource to
     * another. The event is assigned its new render id *before* being
     * returned to the free pool, so that when the render engine requests
     * a div from the free pool, the same div will be returned.
     * @param {Scheduler.model.EventModel|String|Number} eventRecord Event record or id
     * @param {Scheduler.model.ResourceModel|String|Number} resourceRecord Resource record or id
     * @private
     */
    getEventRenderId(eventRecord, resourceRecord) {
        // Replacing and - in the id with ._. to not break `getResourceIdFromDomNodeId()`
        const
            eventId               = eventRecord instanceof TimeSpan ? eventRecord.id : eventRecord,
            eventIdWithoutDash    = eventId.toString().replace(hyphenRe, '._.'),
            resourceId            = resourceRecord instanceof ResourceModel ? resourceRecord.id : resourceRecord,
            resourceIdWithoutDash = resourceId.toString().replace(hyphenRe, '._.');
        return this.eventPrefix + `${eventIdWithoutDash}-${resourceIdWithoutDash}-x`;
    }

    //endregion

    // This does not need a className on Widgets.
    // Each *Class* which doesn't need 'b-' + constructor.name.toLowerCase() automatically adding
    // to the Widget it's mixed in to should implement thus.
    get widgetClass() {}
};
