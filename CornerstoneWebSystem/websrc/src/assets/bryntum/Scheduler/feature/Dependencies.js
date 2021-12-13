import { base } from '../../Core/helper/MixinHelper.js';
import BrowserHelper from '../../Core/helper/BrowserHelper.js';
import DateHelper from '../../Core/helper/DateHelper.js';
import DomHelper from '../../Core/helper/DomHelper.js';
import Rectangle from '../../Core/helper/util/Rectangle.js';
import TemplateHelper from '../../Core/helper/TemplateHelper.js';
import InstancePlugin from '../../Core/mixin/InstancePlugin.js';
import DomDataStore from '../../Core/data/DomDataStore.js';
import Tooltip from '../../Core/widget/Tooltip.js';
import GridFeatureManager from '../../Grid/feature/GridFeatureManager.js';

import DependencyCreation from './mixin/DependencyCreation.js';
import DependencyModel from '../model/DependencyModel.js';
import DependencyStore from '../data/DependencyStore.js';
import RectangularPathFinder from '../util/RectangularPathFinder.js';
import ResourceModel from '../model/ResourceModel.js';
import Model from '../../Core/data/Model.js';
import Delayable from '../../Core/mixin/Delayable.js';
import SchedulerFeatureDataLayer from './mixin/SchedulerFeatureDataLayer.js';
import DataAPI from '../data/api/DataAPI.js';

/**
 * @module Scheduler/feature/Dependencies
 */

const
    fromBoxSide = [
        'left',
        'left',
        'right',
        'right'
    ],
    toBoxSide  = [
        'left',
        'right',
        'left',
        'right'
    ];

/**
 * Feature that draws dependencies between events Uses a {@link Scheduler.data.DependencyStore DependencyStore} to determine which dependencies to draw, if
 * none is defined one will be created automatically. Dependencies can also be specified as Scheduler#dependencies, see
 * example below.
 *
 * This feature is **disabled** by default. It is **not** supported in vertical mode.
 *
 * @mixes Core/mixin/Delayable
 * @mixes Scheduler/feature/mixin/DependencyCreation
 *
 * @extends Core/mixin/InstancePlugin
 * @demo Scheduler/dependencies
 * @externalexample scheduler/Dependencies.js
 */
export default class Dependencies extends base(InstancePlugin).mixes(
    DependencyCreation,
    SchedulerFeatureDataLayer,
    Delayable,
    DataAPI
) {

    /**
     * Fired when dependencies are rendered
     * @event dependenciesDrawn
     * @param {Boolean} [partial] Optional event parameter. `true` when subset of dependencies is repainted, omitted
     * when all lines were repainted.
     */

    //region Config

    static get $name() {
        return 'Dependencies';
    }

    static get defaultConfig() {
        return {
            /**
             * Path finder instance configuration
             * @config {Object}
             */
            pathFinderConfig : null,

            /**
             * The CSS class to add to a dependency line when hovering over it
             * @config {String}
             * @default
             * @private
             */
            overCls : 'b-sch-dependency-over',

            /**
             * The CSS class applied to dependency lines
             * @config {String}
             * @default
             * @private
             */
            baseCls : 'b-sch-dependency',

            /**
             * Store that holds dependencies (using DependencyModel or subclass thereof). A store will be automatically
             * created if none is specified
             * @config {Scheduler.data.DependencyStore}
             */
            store : null,

            /**
             * Dependency definitions (data to DependencyModels). Will be added to store. Can also be specified on
             * Scheduler for convenience
             * @config {DependencyModel[]|Object[]}
             */
            dependencies : null,

            highlightDependenciesOnEventHover : false,

            /**
             * Set to true to show a tooltip when hovering a dependency line
             * @config {Boolean}
             */
            showTooltip : true,

            /**
             * A tooltip config object that will be applied to the dependency hover tooltip. Can be used to for example
             * customize delay
             * @config {Object}
             */
            tooltip : null,

            bufferSize : 50,

            cacheGridSize : {
                x     : 500,
                index : 25
            },

            drawnDependencies     : [],
            drawnLines            : [],
            dependenciesToRefresh : new Map(),

            storeClass : DependencyStore
        };
    }

    // Plugin configuration. This plugin chains some of the functions in Grid.
    static get pluginConfig() {
        return {
            chain  : ['render', 'renderContents', 'onElementClick', 'onElementDblClick', 'onElementMouseOver', 'onElementMouseOut'],
            assign : ['getElementForDependency', 'getDependencyForElement']
        };
    }
    //endregion

    //region Init & destroy

    construct(scheduler, config = {}) {
        const me = this;

        if (scheduler.isVertical) {
            throw new Error('Dependencies feature is not supported in vertical mode');
        }

        // Many things may schedule a draw. Ensure it only happens once, on the next frame.
        // And Ensure it really is on the *next* frame after invocation by passing
        // the cancelOutstanding flag.
        me.doScheduleDraw = me.createOnFrame('draw', [], me, true);

        super.construct(scheduler, config);

        me.pathFinder = new RectangularPathFinder(me.pathFinderConfig);
        delete me.pathFinderConfig;

        me.lineDefAdjusters = me.createLineDefAdjusters();
    }

    doDestroy() {
        this.tooltip && this.tooltip.destroy();
        super.doDestroy();
    }

    doDisable(disable) {
        this.updateCreateListeners();

        if (this.client.isPainted) {
            this.draw();
        }

        super.doDisable(disable);
    }

    //endregion

    //region Regions need cleanup

    exposeSchedulerDependenciesProperty() {
        const me = this;

        if (me.dependenciesPropertyExposed) {
            delete me.client.dependencies;
        }

        Object.defineProperty(me.client, 'dependencies', {
            configurable : true,
            get          : () => me.dependencyStore.records,
            set          : dependencies => me.dependencyStore.data = dependencies
        });

        me.dependenciesPropertyExposed = true;
    }

    get rowStore() {
        return this.resourceStore;
    }

    // referenced in feature a lot
    get scheduler() {
        return this.client;
    }

    getResourceStoreListenersConfig() {
        return {
            refresh : this.onRowStoreRefresh
        };
    }

    obtainDependencyStore(scheduler, config) {
        let store = (config && config.store) || super.obtainDependencyStore(scheduler, config);

        // Backward compatibility
        if (store === true || !store) {
            store = new this.storeClass();
        }

        return store;
    }

    getDependencyStoreListenersConfig() {
        return {
            change : this.onDependencyChange
        };
    }

    onDependencyStoreChange(newStore, oldStore) {
        const
            me = this,
            scheduler = me.client;

        // When assigning a new store there is no point in keeping anything cached
        me.resetGridCache();
        me.resetBoundsCache();

        scheduler.dependencyStore = newStore;

        if (newStore) {
            if (!me.dependenciesPropertyExposed && scheduler.dependencies) {
                newStore.data = scheduler.dependencies;
                delete scheduler.dependencies;
            }

            // TODO: deprecate and get rid of this
            if (me.dependencies) {
                newStore.data = me.dependencies;
                delete me.dependencies;
            }

            // TODO: This is rather doubtfull code, investigate it
            // used to store meta per scheduler on models, in case they are used in multiple schedulers
            newStore.metaMapId = scheduler.id;

            // TODO: This is very unclear code, investigate it, and better get rid of it
            // Ask the client for its eventStore after we set our store.
            // Its eventStore getter attempts to read our store.
            newStore.eventStore = scheduler.eventStore;

            // expose getter/setter for dependencies on scheduler
            me.exposeSchedulerDependenciesProperty();
        }
    }

    getAssignmentStoreListenersConfig() {
        return {
            change  : this.onAssignmentChange,
            refresh : this.onAssignmentRefresh
        };
    }

    getEventStoreListenersConfig() {
        return {
            change : this.onEventChange
        };
    }

    createMarkers() {
        const
            me  = this,
            svg = me.client.svgCanvas,
            endMarker = me.endMarker = me.initMarkerElement('arrowEnd', '8', 'M0,0 L0,6 L9,3 z');

        // Edge and IE11 do not support required svg 2.0 orient value
        if (BrowserHelper.isEdge || BrowserHelper.isIE11) {
            const startMarker = me.startMarker = me.initMarkerElement('arrowStart', '1', 'M0,3 L9,6 L9,0 z');

            svg.appendChild(startMarker);
        }
        else {
            endMarker.setAttribute('orient', 'auto-start-reverse');
        }

        svg.appendChild(endMarker);
    }

    /**
     * Creates SVG marker element (arrow) which is used for all dependency lines
     * @private
     * @param {String} id Id of the marker element
     * @param {String} refX
     * @param {String} arrowPath Path defining arrow
     */
    initMarkerElement(id, refX, arrowPath) {
        return DomHelper.createElement({
            id,
            tag          : 'marker',
            ns           : 'http://www.w3.org/2000/svg',
            markerHeight : 9,
            markerWidth  : 9,
            refX,
            refY         : 3,
            viewBox      : '0 0 9 6',
            orient       : 'auto',
            markerUnits  : 'userSpaceOnUse',
            children     : [{
                tag : 'path',
                ns  : 'http://www.w3.org/2000/svg',
                d   : arrowPath
            }]
        });
    }

    /**
     * Returns an array of functions used to alter path config when no path found.
     * It first tries to shrink arrow margins and secondly hides arrows entirely
     * @private
     * @returns {Function[]}
     */
    createLineDefAdjusters() {
        const scheduler = this.client;

        function shrinkArrowMargins(lineDef) {
            let adjusted = false;

            if (lineDef.startArrowMargin > scheduler.barMargin || lineDef.endArrowMargin > scheduler.barMargin) {
                lineDef.startArrowMargin = lineDef.endArrowMargin = scheduler.barMargin;
                adjusted = true;
            }

            return adjusted ? lineDef : adjusted;
        }

        function resetArrowMargins(lineDef) {
            let adjusted = false;

            if (lineDef.startArrowMargin > 0 || lineDef.endArrowMargin > 0) {
                lineDef.startArrowMargin = lineDef.endArrowMargin = 0;
                adjusted = true;
            }

            return adjusted ? lineDef : adjusted;
        }

        // function shrinkStartEndMarginsBy2(lineDef) {
        //     let adjusted = false;
        //
        //     if (lineDef.hasOwnProperty('startHorizontalMargin') && lineDef.startHorizontalMargin > 2) {
        //         lineDef.startHorizontalMargin = Math.round(lineDef.startHorizontalMargin / 2);
        //         adjusted = true;
        //     }
        //     if (lineDef.hasOwnProperty('startVerticalMargin') && lineDef.startVerticalMargin > 2) {
        //         lineDef.startVerticalMargin = Math.round(lineDef.startVerticalMargin / 2);
        //         adjusted = true;
        //     }
        //     if (lineDef.hasOwnProperty('endHorizontalMargin') && lineDef.endHorizontalMargin > 2) {
        //         lineDef.endHorizontalMargin = Math.round(lineDef.endHorizontalMargin / 2);
        //         adjusted = true;
        //     }
        //     if (lineDef.hasOwnProperty('endVerticalMargin') && lineDef.endVerticalMargin > 2) {
        //         lineDef.endVerticalMargin = Math.round(lineDef.endVerticalMargin / 2);
        //         adjusted = true;
        //     }
        //
        //     return adjusted ? lineDef : adjusted;
        // }
        //
        // function resetArrowSizes(lineDef) {
        //     let adjusted = false;
        //
        //     if (lineDef.startArrowSize > 0 || lineDef.endArrowSize > 0) {
        //         lineDef.startArrowSize = lineDef.endArrowSize = 0;
        //         adjusted = true;
        //     }
        //
        //     return adjusted ? lineDef : adjusted;
        // }

        return [
            shrinkArrowMargins,
            resetArrowMargins//,
            // shrinkStartEndMarginsBy2,
            // shrinkStartEndMarginsBy2,
            // shrinkStartEndMarginsBy2,
            // resetArrowSizes
        ];
    }

    //endregion

    //region Elements

    getElementForDependency(dependency, assignmentData = null) {
        let selector = `[depId="${dependency.id}"]`;

        if (assignmentData) {
            selector += `[fromId="${assignmentData.from.id}"][toId="${assignmentData.to.id}"]`;
        }

        return this.client.svgCanvas.querySelector(selector);
    }

    getDependencyForElement(element) {
        const id = typeof element === 'string' ? element : element.getAttribute('depId');

        // TODO: DataAPI?
        return this.dependencyStore.getById(id);
    }

    //endregion

    //region Events

    //region Events that triggers redraw

    onToggleNode() {
        // Need to repopulate grid cache
        this.dependencyGridCache = null;
        // node toggled in tree, can affect resources both above and below, need to redraw all.
        this.scheduleDraw(true);
    }

    // onEventLayout() {
    //     this.scheduleDraw(true);
    // }

    onViewportResize() {
        this.scheduleDraw(true);
    }

    /**
     * Flags for redrawing if a rows height has changed
     * @private
     */
    onTranslateRow({ row }) {
        // a changetotalheight event is fired after translations, if a rowHeight change is detected here it will redraw
        // all dependencies
        if (row.lastTop >= 0 && row.top !== row.lastTop) {
            this.scheduleDraw(true);
        }
    }

    /**
     * Redraws all dependencies if a rows height changed, as detected in onTranslateRow
     * @private
     */
    onChangeTotalHeight() {
        // redraw all dependencies if the height changes. Could be caused by resource add/remove.
        // in reality not all deps needs to be redrawn, those fully above the row which changed height could be left
        // as is, but determining that would likely require more processing than redrawing
        this.scheduleDraw(true);
    }

    /**
     * Draws dependencies on horizontal scroll
     * @private
     */
    onHorizontalScroll({ subGrid }) {
        // ResizeMonitor triggers scroll during render, make sure we have been drawn some other way before redrawing
        if (this.isDrawn && subGrid === this.client.timeAxisSubGrid) {
            this.scheduleDraw(false);
        }
    }

    /**
     * Draws dependencies on vertical scroll
     * @private
     */
    onVerticalScroll() {
        // ResizeMonitor triggers scroll during render, make sure render is done
        if (this.isDrawn) {
            // Do not invalidate on scroll, if height changes it will be invalidated anyway
            this.scheduleDraw(false);
        }
    }

    onRowsRefresh() {
        this.scheduleDraw(true);
    }

    /**
     * When rows are rerendered, better redraw dependencies (might have been collapsed etc).
     * @private
     */
    onRowsRerender() {
        this.scheduleDraw(true);
    }

    /**
     * Redraws dependencies when a row has changed
     * @private
     */
    onRowStoreRefresh({ action }) {
        switch (action) {
            case 'sort':
            case 'filter':
            case 'batch':
                // Will need to recreate grid cache after sort, filter, and any unspecified
                // set of operations encapsulated by a batch, and redraw everything
                this.dependencyGridCache = null;
                return this.scheduleDraw(true);
        }
    }

    // TODO: refactor this! maybe add dataLayerBindOnRender : Boolean configuration option to get rid of if scheduler is rendered check
    /**
     * Redraws dependencies when a dependency has changed
     * @private
     */
    onDependencyChange({ action, record, records }) {
        const me = this;

        if (!me.client.isPainted || me.disabled) {
            return;
        }

        switch (action) {
            case 'dataset':
                me.dependencyGridCache = {};
                // dataset should fall through to add after clearing the cache
                // eslint disable no-fallthrough
            case 'add':
                // dependency added, draw it

                // Check if there is a cache exists. If it does - use it,
                // if not - create one after all records are drawn, this will cache all existing records
                const cache = me._dependencyGridCache;

                records.forEach(dependency => {
                    // Previously this code would draw added dependencies here, no matter if in view or not. To not have
                    // to have the logic for determining whats in view or not here also, simply do a full draw below.
                    // Old approach was especially costly when using CrudManager, which adds on load

                    cache && me.addToGridCache(dependency);
                });

                if (!cache) {
                    me._thisIsAUsedExpression(me.dependencyGridCache);
                }

                me.scheduleDraw();

                return;
            case 'update':
                // Dependency updated. Might have changed source or target, redraw it completely
                return me.scheduleRefreshDependency(record);
            case 'remove':
                // dependencies removed, release elements and remove from cache
                records.forEach(dependency => {
                    me.releaseDependency(dependency, true);
                    me.removeFromCache(dependency);
                });
                me.client.trigger('dependenciesDrawn');
                return;
            // Removing all or filtering -> full redraw
            case 'removeall':
            case 'filter':
                me.dependencyGridCache = null;
                // continue to schedule draw
                break;
        }

        // other changes (removeall, dataset, filter) trigger full redraw
        me.scheduleDraw(true);
    }

    /**
     * Redraws dependencies when an event has changed
     * @private
     */
    onEventChange({ action, record }) {
        switch (action) {
            case 'filter':
                // filtering events, need to redraw all dependencies
                return this.scheduleDraw(true);
            case 'update':
                if (!record.isEvent) {
                    this.drawForTimeSpan(record, true);
                }
                else {
                    // event updated, need to redraw dependencies for all events in its row since it might have changed
                    // vertical position (from changing startDate, duration, name or anything if using custom sorter)
                    const eventRecords = [record];
                    // Collect all events for all resources which have this event assigned... they might all be affected
                    record.resources.forEach(resourceRecord => eventRecords.push(...resourceRecord.events));
                    new Set(eventRecords).forEach(eventRecord => this.drawForTimeSpan(eventRecord, true));
                }
                break;
            case 'removeall':
                this.resetGridCache();
                return this.scheduleDraw(true);
        }

        // adding event has no effect on dependencies, unless it changes row height. in which case it will be handled
        // by onTranslateRow().
        // updating an event might also change row height, handled the same way.
        // removing events will also remove dependencies, thus handled in onDependencyChange
    }

    onAssignmentRefresh({ action }) {
        if (action === 'dataset') {
            // Assigning using EventEdit replaces all assignments. Taking the easy way out, throwing cache away
            this.resetGridCache();
            this.scheduleDraw(true);
        }
    }

    onAssignmentChange({ action, record, records }) {
        const me = this;

        if (record) {
            records = [record];
        }

        if (action === 'add' || action === 'remove' || action === 'update') {
            records.forEach(assignment => {
                const dependencies = this.dataApi.getAssignmentDependencies({
                    assignment,
                    dependencyStore : this.dependencyStore
                });

                dependencies.forEach(dependency => {
                    // New assignment added by other means than EventEdit
                    if (action === 'add') {
                        me.scheduleRefreshDependency(dependency);
                    }
                    // Event unassigned, remove dep line
                    else if (action === 'remove') {
                        let assignments;

                        // Removed source?
                        if (this.dataApi.isDependencySourceAssignment({ dependency, assignment })) {
                            // Might point to a multi assigned event, need to remove all lines
                            assignments = this.dataApi.getDependencyTargetAssignments({ dependency, assignmentStore : this.assignmentStore }).map(
                                to => ({ from : assignment, to })
                            );
                        }
                        // Nope, target
                        else {
                            // Might point to a multi assigned event, need to remove all lines
                            assignments = this.dataApi.getDependencySourceAssignments({ dependency, assignmentStore : this.assignmentStore }).map(
                                from => ({ from, to : assignment })
                            );
                        }

                        assignments.forEach(assignmentData => {
                            me.releaseDependency(dependency, assignmentData);
                            me.removeFromCache(dependency, assignmentData);
                        });
                    }
                    // Single assignment updated, redraw lines for it
                    else if (action === 'update') {
                        this.dataApi.getDependencyAssignmentsAsFromToArray({ dependency, assignmentStore : this.assignmentStore }).filter(
                            a => (a.from === record || a.to === record)
                        ).forEach(assignmentData => {
                            this.scheduleRefreshDependency(dependency, assignmentData);
                        });
                    }
                });
            });
        }
    }

    //endregion

    onElementClick(event) {
        const me = this;

        if (event.target.matches('.' + me.baseCls)) {
            const
                dependency = DomDataStore.get(event.target).dependency,
                eventName = event.type === 'click' ? 'Click' : 'DblClick';

            /**
             * Fires on the owning Scheduler/Gantt when a click is registered on a dependency line.
             * @event dependencyClick
             * @param {Scheduler.view.Scheduler} source The scheduler
             * @param {Scheduler.model.DependencyModel} dependency
             * @param {MouseEvent} event
             */
            /**
             * Fires on the owning Scheduler/Gantt when a click is registered on a dependency line.
             * @event dependencyDblClick
             * @param {Scheduler.view.Scheduler} source The scheduler
             * @param {Scheduler.model.DependencyModel} dependency
             * @param {MouseEvent} event
             */
            me.client.trigger(`dependency${eventName}`, {
                dependency,
                event
            });
        }
    }

    onElementDblClick(event) {
        return this.onElementClick(event);
    }

    onElementMouseOver(event) {
        const me = this;

        if (event.target.matches('.' + me.baseCls)) {
            const dependency = DomDataStore.get(event.target).dependency;

            /**
             * Fires on the owning Scheduler/Gantt when the mouse moves over a dependency line.
             * @event dependencyMouseOver
             * @param {Scheduler.view.Scheduler} source The scheduler
             * @param {Scheduler.model.DependencyModel} dependency
             * @param {MouseEvent} event
             */
            me.client.trigger('dependencyMouseOver', {
                dependency,
                event
            });

            if (me.overCls) me.highlight(dependency, me.overCls); // TODO: dataApi?
        }
    }

    onElementMouseOut(event) {
        const me = this;

        if (event.target.matches('.' + me.baseCls)) {
            const dependency = DomDataStore.get(event.target).dependency;

            /**
             * Fires on the owning Scheduler/Gantt when the mouse moves out of a dependency line.
             * @event dependencyMouseOut
             * @param {Scheduler.view.Scheduler} source The scheduler
             * @param {Scheduler.model.DependencyModel} dependency
             * @param {MouseEvent} event
             */
            me.client.trigger('dependencyMouseOut', {
                dependency,
                event
            });

            if (me.overCls && !dependency.meta.removed) me.unhighlight(dependency);
        }
    }

    //endregion

    //region Highlight

    highlight(dependency, cls = this.overCls) {
        const element = this.getElementForDependency(dependency);

        element && element.classList.add(cls);
        dependency.highlight(cls); // TODO: dataApi?
    }

    unhighlight(dependency, cls = this.overCls) {
        const element = this.getElementForDependency(dependency);

        element && element.classList.remove(cls);
        dependency.unhighlight(cls); // TODO: dataApi?
    }

    highlightEventDependencies(timespan) {
        timespan.allDependencies.forEach(dep => this.highlight(dep));
    }

    unhighlightEventDependencies(timespan) {
        timespan.allDependencies.forEach(dep => this.unhighlight(dep));
    }

    //endregion

    //region Determining dependencies to draw

    // Neither resource can be hidden for a dependency to be considered visible
    isDependencyVisible(dependency, assignmentData = null) {
        const
            me   = this,
            { dataApi, resourceStore, eventStore } = me,
            from = dataApi.getDependencySourceEvent({ dependency }),
            to   = dataApi.getDependencyTargetEvent({ dependency });

        // Bail out early in case source or target doesn't exist
        if (!(from && to)) {
            return false;
        }

        let fromResource, toResource;

        // Using multi-assignment, resource obtained from assignment
        if (assignmentData) {
            fromResource = dataApi.getAssignmentResource({ assignment : assignmentData.from, resourceStore });
            toResource = dataApi.getAssignmentResource({ assignment : assignmentData.to, resourceStore });

            // Filtering EventStore does not filter AssignmentStore, determine if Event is available in this case
            if (eventStore.isFiltered && (eventStore.indexOf(from) === -1 || eventStore.indexOf(to) === -1)) {
                return false;
            }
        }
        // Not using assignments, resource obtained from event
        else {
            fromResource = dataApi.getEventResource({ event : from, resourceStore });
            toResource = dataApi.getEventResource({ event : to, resourceStore });
        }

        return from instanceof Model &&
            // Verify these are real existing Resources and not placeholders (resource not existing in resource store)
            fromResource instanceof ResourceModel &&
            toResource instanceof ResourceModel &&
            !fromResource.instanceMeta(resourceStore).hidden &&
            !toResource.instanceMeta(resourceStore).hidden;
    }

    // Get the bounding box for either the source or the target event
    getBox(dependency, source, assignmentData = null) {
        const eventRecord = this.getTimeSpanRecordFromDependency(dependency, source);

        let resource;

        // Multi-assignment, get resource from assignment
        if (assignmentData) {
            resource = this.dataApi.getAssignmentResource({ assignment : assignmentData[source ? 'from' : 'to'], resourceStore : this.resourceStore });
        }
        // Single, get resource from event
        else {
            resource = this.dataApi.getEventResource({ event : eventRecord, resourceStore : this.resoureStore });
        }

        // TODO: change getResourceEventBox to use Rectangle
        return this.client.getResourceEventBox(eventRecord, resource, true);
    }

    // Get source or target events resource
    getRowRecordFromDependency(dependency, source, assignmentData) {
        let rowRecord;

        // Multi-assigned, use assignments resource
        if (assignmentData) {
            rowRecord = this.dataApi.getAssignmentResource({ assignment : assignmentData[source ? 'from' : 'to'], resourceStore : this.resourceStore });
        }
        // Not multi-assigned, get events resource
        else {
            rowRecord = this.dataApi.getEventResource({ event : this.getTimeSpanRecordFromDependency(dependency, source), resourceStore : this.resourceStore });
        }

        return rowRecord;
    }

    // Get source or target event
    getTimeSpanRecordFromDependency(dependency, source = true) {
        return dependency[`${source ? 'source' : 'target'}Event`];
    }

    getMetaId(assignmentData = null) {
        return assignmentData ? `${this.client.id}-ass${assignmentData.from.id}-ass${assignmentData.to.id}` : this.client.id;
    }

    // Gets the source and target events bounds and unions them to determine the dependency bounds
    getDependencyBounds(dependency, assignmentData = null) {
        const
            me        = this,
            scheduler = me.client,
            ddr       = dependency.getDateRange();

        // quick bailout for hidden rows
        if (!scheduler.rowManager.rowCount || !me.isDependencyVisible(dependency, assignmentData)) {
            return null;
        }
        // quick bailout in case dependency dates and view dates do not intersect
        if (!(ddr && DateHelper.intersectSpans(ddr.start, ddr.end, scheduler.startDate, scheduler.endDate))) {
            return null;
        }

        const
            metaId = me.getMetaId(assignmentData),
            instanceMeta = dependency.instanceMeta(metaId);

        // If we are forcing recalculation of dep bounds, or there are no calculated bounds for this dependency
        // or the calculated bounds were based on a "best guess", then recalculate the bounds.
        if (me._resetBoundsCache || !instanceMeta.bounds || !instanceMeta.bounds.layout) {
            const
                startBox = me.getBox(dependency, true, assignmentData),
                endBox   = me.getBox(dependency, false, assignmentData);

            // cant draw dependency if either start or end is in collapsed row
            if (!startBox || !endBox) {
                return null;
            }

            const
                from           = me.getTimeSpanRecordFromDependency(dependency, true),
                to             = me.getTimeSpanRecordFromDependency(dependency, false),
                startRectangle = startBox instanceof Rectangle ? startBox : new Rectangle(
                    startBox.start,
                    startBox.top,
                    startBox.end - startBox.start,
                    startBox.bottom - startBox.top
                ),
                endRectangle   = endBox instanceof Rectangle ? endBox : new Rectangle(
                    endBox.start,
                    endBox.top,
                    endBox.end - endBox.start,
                    endBox.bottom - endBox.top
                ),
                bounds         = Rectangle.union(startRectangle, endRectangle);

            [[from, startRectangle, startBox], [to, endRectangle, endBox]].map(([record, rectangle, eventBox]) => {
                // When using other milestoneLayoutMode than default milestones should be treated as normal events.
                // Milestones are zero width by default, so we must measure the milestone el's height
                // (or icon el width) and use that as the width. We cannot use the event's calculated height because
                // if there are labels, the milestone diamond will be smaller.
                // If the event doesn't have an element, then it's outside of the rendered block and the exact
                // width doesn't matter.
                if (scheduler.milestoneLayoutMode === 'default' && record.isMilestone) {
                    if (!scheduler.milestoneWidth && eventBox.eventEl) {
                        scheduler.milestoneWidth = record.iconCls ? eventBox.eventEl.firstElementChild.offsetWidth : parseInt(window.getComputedStyle(eventBox.eventEl, ':before').fontSize);
                    }

                    // If it could not be measured due to the event being outside of the rendered block
                    // we have to use the calculated height.
                    const milestoneWidth = scheduler.milestoneWidth || rectangle.height;
                    rectangle.left -= milestoneWidth / 2;
                    rectangle.right += milestoneWidth / 2;
                }
            });

            instanceMeta.bounds = {
                bounds,
                startRectangle,
                endRectangle,

                // Cache whether both rectangles are based on the true layout
                // or a best guess approximation to be recalculated
                // next time through.
                layout : startBox.layout && endBox.layout
            };
        }

        return dependency.instanceMeta(metaId).bounds;
    }

    // Grid cache is a virtual grid holding info on which dependencies intersects its virtual cells.
    // Used to determine which dependencies should be considered for drawing, iterating over all dependencies each update
    // gets too costly when count increases (>10000).
    //
    // Illustration shows entire schedule area, dddd is a dependency line, vvv is viewport, xxx virtual cell border:
    //
    // ----------------------------------
    // |     vvvvvxvvvvv                |
    // |     v    x    v                |
    // |     v d  x    v                |
    // |     v d  x    v                |
    // |xxxxxvxdxxx    v                |
    // |     vvdvvxvvvvv                |
    // |       d  x                     |
    // |       d  x                     |
    // |       d  x                     |
    // |xxxxxxxdxxxxxxx                 |
    // |       d  x                     |
    // |       d  x                     |
    // |          x                     |
    // |          x                     |
    // |xxxxxxxxxxxxxxx                 |
    // ----------------------------------
    //
    // The dependency crosses three virtual grid cells [0,0], [0,1] and [0,2]. Stored in a map in cache:
    // {
    //    0 : {
    //      0 : [ d, ... ],
    //      1 : [ d, ... ],
    //      2 : [ d, ... ]
    //    }
    // }
    //
    // Viewport crosses four virtual grid cells [0,0], [1,0], [0,1], [1,1]. Those cells are checked in the cached map to
    // find out which rows should be considered for drawing.
    //
    // This approach minimizes the amount of iteration needed
    get dependencyGridCache() {
        const me = this;

        if (!me._dependencyGridCache) {
            me._dependencyGridCache = {};
            me.dependencyStore.forEach(dependency => me.addToGridCache(dependency));
        }

        return me._dependencyGridCache;
    }

    // With multi-assign each dependency might be drawn several times
    getIteratableDependencyAssignments(dependency) {
        return this.assignmentStore ? this.dataApi.getDependencyAssignmentsAsFromToArray({
            dependency,
            assignmentStore : this.assignmentStore
        }) : [null]; // On purpose, to be iterable
    }

    addToGridCache(dependency) {
        const
            me                  = this,
            { dependencyGridCache, cacheGridSize, rowStore } = me,
            assignmentDataArray = me.getIteratableDependencyAssignments(dependency);

        assignmentDataArray.forEach(assignmentData => {
            const dependencyBounds = me.getDependencyBounds(dependency, assignmentData);

            if (dependencyBounds) {
                const
                    metaId = me.getMetaId(assignmentData),
                    meta = dependency.instanceMeta(metaId),
                    metaGridCache = meta.gridCache = [],

                    // Using index vertically rather than y for reliability with variable row height
                    fromIndex   = rowStore.indexOf(me.getRowRecordFromDependency(dependency, true, assignmentData)),
                    toIndex     = rowStore.indexOf(me.getRowRecordFromDependency(dependency, false, assignmentData)),
                    topIndex    = Math.min(fromIndex, toIndex),
                    bottomIndex = Math.max(fromIndex, toIndex),

                    // Convert dependency bounds/index into virtual grid cells
                    box         = dependencyBounds.bounds,
                    boxLeft     = Math.floor(Math.max(box.x, 0) / cacheGridSize.x),
                    boxRight    = Math.floor(box.right / cacheGridSize.x),
                    boxTop      = Math.floor(topIndex / cacheGridSize.index),
                    boxBottom   = Math.floor(bottomIndex / cacheGridSize.index);

                let cacheX, cacheY, x, y;

                // Store the dependency in the virtual cells which it intersects
                for (x = boxLeft; x <= boxRight; x++) {
                    cacheX = dependencyGridCache[x] || (dependencyGridCache[x] = {});

                    for (y = boxTop; y <= boxBottom; y++) {
                        cacheY = cacheX[y] || (cacheX[y] = []);

                        cacheY.push({ dependency, assignmentData, metaId });
                        metaGridCache.push([x, y]);
                    }
                }
            }
        });
    }

    removeFromCache(dependency, assignmentData = null) {
        const me   = this;

        let assignments;

        // Some short-cut case
        if (assignmentData) {
            assignments = [assignmentData];
        }
        else {
            assignments = this.getIteratableDependencyAssignments(dependency);
        }

        assignments.forEach(assignmentData => {
            const
                metaId = me.getMetaId(assignmentData),
                meta   = dependency.instanceMeta(metaId);

            meta.gridCache && me._dependencyGridCache && meta.gridCache.forEach(([x, y]) => {
                if (me._dependencyGridCache.hasOwnProperty(x) && me._dependencyGridCache[x].hasOwnProperty(y)) {
                    const
                        entries = me._dependencyGridCache[x][y],
                        index   = entries.findIndex(d => d.dependency === dependency && (d.assignmentData === assignmentData || (d.assignmentData.from === assignmentData.from && d.assignmentData.to === assignmentData.to)));

                    // Cannot use ArrayHelper#remove since it cannot compare deeply
                    if (index > -1) {
                        entries.splice(index, 1);
                    }
                }
            });

            meta.bounds = null;
            meta.gridCache = null;
        });
    }

    set dependencyGridCache(cache) {
        this._dependencyGridCache = cache;
    }

    // Reset cached bounds, not grid cache since it is expensive to create. It is so coarse anyway so should be fine
    // with most changes, except for sorting and similar. Reset on demand instead
    resetBoundsCache() {
        // Not actually resetting here, would just be costly to iterate and reset per dependency, instead flagging to
        // force cached value to be updated
        this._resetBoundsCache = true;
    }

    // In some cases we do need to reset cache, like when time axis is reconfigured
    resetGridCache() {
        this.dependencyGridCache = null;
    }

    //endregion

    //region Draw & render

    //region Lines

    prepareLineDef(dependency, dependencyDrawData, assignmentData = null) {
        const
            me     = this,
            source = me.getTimeSpanRecordFromDependency(dependency, true),
            target = me.getTimeSpanRecordFromDependency(dependency, false),
            type   = dependency.type;

        let startSide = dependency.fromSide,
            endSide   = dependency.toSide;

        // Fallback to view trait if dependency start side is not given
        if (!startSide) {
            switch (true) {
                case type === DependencyModel.Type.StartToEnd:
                    startSide = me.getConnectorStartSide(source);
                    break;

                case type === DependencyModel.Type.StartToStart:
                    startSide = me.getConnectorStartSide(source);
                    break;

                case type === DependencyModel.Type.EndToStart:
                    startSide = me.getConnectorEndSide(source);
                    break;

                case type === DependencyModel.Type.EndToEnd:
                    startSide = me.getConnectorEndSide(source);
                    break;

                default:
                    throw new Error('Invalid dependency type: ' + type);
            }
        }

        // Fallback to view trait if dependency end side is not given /*or can be obtained from type*/
        if (!endSide) {
            switch (true) {
                case type === DependencyModel.Type.StartToEnd:
                    endSide = me.getConnectorEndSide(target);
                    break;

                case type === DependencyModel.Type.StartToStart:
                    endSide = me.getConnectorStartSide(target);
                    break;

                case type === DependencyModel.Type.EndToStart:
                    endSide = me.getConnectorStartSide(target);
                    break;

                case type === DependencyModel.Type.EndToEnd:
                    endSide = me.getConnectorEndSide(target);
                    break;

                default:
                    throw new Error('Invalid dependency type: ' + type);
            }
        }

        const { startRectangle, endRectangle } = dependencyDrawData;

        return {
            startBox : {
                start  : startRectangle.x,
                end    : startRectangle.right,
                top    : startRectangle.y,
                bottom : startRectangle.bottom
            },

            endBox : {
                start  : endRectangle.x,
                end    : endRectangle.right,
                top    : endRectangle.y,
                bottom : endRectangle.bottom
            },
            startSide : startSide,
            endSide   : endSide
        };
    }

    // Draws a single SVG line that represents the dependency
    drawLine(canvas, dependency, points, assignmentData = null, cache = true) {
        const
            scheduler = this.client,
            metaId = this.getMetaId(assignmentData);

        // Reuse existing element if possible
        let line = dependency.instanceMeta(metaId).lineElement;

        if (!line || !cache) {
            line = document.createElementNS('http://www.w3.org/2000/svg', 'polyline');
    
            if (cache) {
                dependency.instanceMeta(metaId).lineElement = line;
            }
            
            line.setAttribute('depId', dependency.id);
            if (assignmentData) {
                line.setAttribute('fromId', assignmentData.from.id);
                line.setAttribute('toId', assignmentData.to.id);
            }
            canvas.appendChild(line);
        }

        // TODO: Use DomHelper.syncClassList

        // className is SVGAnimatedString for svg elements, reading attribute instead
        line.classList.length && line.classList.remove.apply(line.classList, line.getAttribute('class').split(' '));

        line.classList.add(this.baseCls);

        if (dependency.cls) {
            line.classList.add(dependency.cls);
        }
        if (dependency.bidirectional) {
            line.classList.add('b-sch-bidirectional-line');
        }
        if (dependency.highlighted) {
            line.classList.add(...dependency.highlighted.split(' '));
        }
        if (BrowserHelper.isIE11) {
            const
                ddr       = dependency.getDateRange(true),
                viewStart = scheduler.startDate;

            if (ddr.start < viewStart) {
                line.classList.add('b-no-start-marker');
            }
            if (ddr.end < viewStart) {
                line.classList.add('b-no-end-marker');
            }
        }

        line.setAttribute('points', !points ? '' : points.map((p, i) => i !== points.length - 1 ? `${p.x1},${p.y1}` : `${p.x1},${p.y1} ${p.x2},${p.y2}`).join(' '));

        DomDataStore.set(line, {
            dependency
        });
        
        return line;
    }

    //endregion

    /**
     * Re-caches and redraws a dependency, for all assignments.
     * @param {Scheduler.model.DependencyModel} dependency Dependency to refresh
     */
    refreshDependency(dependency) {
        const
            me          = this,
            assignments = me.getIteratableDependencyAssignments(dependency);

        // Release dependency element, for all assignments if using AssignmentStore
        me.releaseDependency(dependency, assignments[0] !== null);
        // Remove it from grid & bounds cache
        me.removeFromCache(dependency);
        // Re-add it to grid cache
        me.addToGridCache(dependency);
        // Draw all assignments
        assignments.forEach(assignmentData =>
            me.drawDependency(dependency, null, assignmentData)
        );
    }

    /**
     * Re-caches and redraws a dependency for given assignment.
     * @param {Scheduler.model.DependencyModel} dependency Dependency to refresh
     * @param {Object} assignmentData
     * @param {Scheduler.model.AssignmentModel} assignmentData.from Source assignment
     * @param {Scheduler.model.AssignmentModel} assignmentData.to Target assignment
     * @private
     */
    refreshDependencyAssignment(dependency, assignmentData) {
        const me = this;

        // In case it was assigned to something not in view/timeline, release the line
        me.releaseDependency(dependency, assignmentData);
        // Update cache to only contain whats left of it
        me.removeFromCache(dependency, assignmentData);
        me.addToGridCache(dependency);
        // Draw lines
        me.drawDependency(dependency, null, assignmentData);
    }

    /**
     * Stores all dependencies/assignments that were requested to refresh and schedules repaint on next animation frame
     * @param {Scheduler.model.DependencyModel} dependency Dependency model to refresh
     * @param {Object} [assignmentData] Assignment data
     * @param {Scheduler.model.AssignmentModel} [assignmentData.from] Source assignment
     * @param {Scheduler.model.AssignmentModel} [assignmentData.to] Target assignment
     * @private
     */
    scheduleRefreshDependency(dependency, assignmentData = null) {
        const map = this.dependenciesToRefresh;

        // If this method was called once without assignment data - all lines releated should be repainted
        if (!assignmentData) {
            map.set(dependency, true);
        }
        else if (map.has(dependency)) {
            if (map.get(dependency) !== true) {
                map.get(dependency).add(assignmentData);
            }
        }
        else {
            map.set(dependency, new Set([assignmentData]));
        }

        if (map.size === 1) {
            this.requestAnimationFrame(() => {
                this.refreshDependencyOnFrame();
            });
        }
    }

    /**
     * Repaints scheduled dependencies/assignments
     * @private
     */
    refreshDependencyOnFrame() {
        const
            me  = this,
            map = me.dependenciesToRefresh;
        if (me.client.isPainted) {
        // First clear cache and release dependencies. This will modify DOM
            map.forEach((assignments, dependency) => {
                if (assignments === true) {
                    const assignments = me.getIteratableDependencyAssignments(dependency);
                    // Release dependency element, for all assignments if using AssignmentStore
                    me.releaseDependency(dependency, assignments[0] !== null);
                    // Remove it from grid & bounds cache
                    me.removeFromCache(dependency);
                }
                else {
                    assignments.forEach(assignment => {
                    // In case it was assigned to something not in view/timeline, release the line
                        me.releaseDependency(dependency, assignment);
                        // Update cache to only contain whats left of it
                        me.removeFromCache(dependency, assignment);
                    });
                }
            });

            // Then fill cache before drawing dependencies. This will read the DOM forcing reflow
            map.forEach((assignments, dependency) => {
            // Re-add it to grid cache
                me.addToGridCache(dependency);
            });

            // Finally append elements to the DOM
            map.forEach((assignments, dependency) => {
                if (assignments === true) {
                    assignments = me.getIteratableDependencyAssignments(dependency);
                }

                assignments.forEach(assignmentData => {
                    me.drawDependency(dependency, null, assignmentData);
                });
            });

            map.clear();

            me.client.trigger('dependenciesDrawn', { partial : true });
        }
    }

    /**
     * Draws a single dependency (for a single assignment if using multiple), if in view.
     * @param {Scheduler.model.DependencyModel} dependency Dependency to draw
     */
    drawDependency(dependency, drawData = null, assignmentData = null, canvas = this.client.svgCanvas, cache = true) {
        const
            me                                          = this,
            { drawnDependencies, oldDrawnDependencies } = me,
            // Determines if a dependency should be draw, and if so returns the coordinates of its events
            dependencyDrawData = drawData || me.getDependencyBounds(dependency, assignmentData),
            lookup = d => d.dependency === dependency && (d.assignmentData === assignmentData || (d.assignmentData && d.assignmentData.from === assignmentData.from && d.assignmentData.to === assignmentData.to));

        if (!me.disabled && dependencyDrawData) {
            // Build line defs
            const
                lineDef = me.prepareLineDef(dependency, dependencyDrawData),
                lines   = me.pathFinder.findPath(lineDef, me.lineDefAdjusters);

            me.drawLine(canvas, dependency, lines, assignmentData, cache);

            // Do not push dependency to drawn deps if this is a temporary render
            // Cannot use ArrayHelper#include since object wont be the same, only its contents
            if (cache && !drawnDependencies.some(lookup)) {
                drawnDependencies.push({ dependency, assignmentData });
            }
        }

        // Remove from oldDrawnDeps, to not have element removed. Cannot use ArrayHelper#remove as stated above
        const oldIndex = oldDrawnDependencies && oldDrawnDependencies.findIndex(lookup);
        if (oldIndex >= 0) {
            oldDrawnDependencies.splice(oldIndex, 1);
        }
    }

    /**
     * Draws multiple dependencies, called from drawForEvent() or drawFromTask().
     * @private
     */
    drawForTimeSpan(timeSpanRecord, async = false) {
        const me = this;

        // If the client is doing an animated update, we must update at end.
        // That will be asynchronous relative to now, so do not pass on the async flag.
        if (me.client.isAnimating) {
            me.client.on({
                transitionend() {
                    me.drawForTimeSpan(timeSpanRecord, true);
                },
                once : true
            });
        }
        // Otherwise, schedule the draw for the next frame.
        else {
            me.dependencyStore.getTimeSpanDependencies(timeSpanRecord).forEach(dependency => {
                if (async) {
                    me.scheduleRefreshDependency(dependency);
                }
                else {
                    me.refreshDependency(dependency);
                }
            });
        }
    }

    /**
     * Draws all dependencies for the specified event.
     */
    drawForEvent(eventRecord) {
        this.drawForTimeSpan(eventRecord);
    }

    // Redraw all dependencies for a particular eventRecord, using its current element instead of calculating a box
    // Used to do live redraw while resizing or dragging events
    updateDependenciesForTimeSpan(timeSpanRecord, element) {
        const
            me               = this,
            eventRecord      = timeSpanRecord.isAssignment ? timeSpanRecord.event : timeSpanRecord,
            deps             = me.dependencyStore.getTimeSpanDependencies(eventRecord),
            metaId           = me.getMetaId(),
            scheduler        = me.client,
            originalTaskRect = Rectangle.from(element, scheduler.timeAxisSubGridElement);

        let bounds;

        deps.forEach(dep => {
            const assignments = this.getIteratableDependencyAssignments(dep);

            assignments.forEach(assignmentData => {
                const taskRect = originalTaskRect.clone();
                let startRectangle, endRectangle, box;

                // If dragging one multi assigned event the others wont move until it is dropped. Prevent their dep
                // lines from updating by bailing out
                if (assignmentData && assignmentData.from !== timeSpanRecord && assignmentData.to !== timeSpanRecord) {
                    return;
                }

                // Bail out if dependency is not visible (other end might be collapsed)
                if (!me.isDependencyVisible(dep, assignmentData)) {
                    return;
                }

                if (me.getTimeSpanRecordFromDependency(dep, true) === eventRecord) {
                    startRectangle = taskRect;

                    // try to look into dependency cache first
                    if ((bounds = dep.instanceMeta(metaId).bounds)) {
                        endRectangle = bounds.endRectangle;
                    }
                    else {
                        box = me.getBox(dep, false, assignmentData);

                        // Row might be in collapsed node, in which case we get no box
                        if (box) {
                            endRectangle = new Rectangle(
                                box.start,
                                box.top,
                                box.end - box.start,
                                box.bottom - box.top
                            );
                        }
                    }
                }
                else {
                    // try to look into dependency cache first
                    if ((bounds = dep.instanceMeta(metaId).bounds)) {
                        startRectangle = bounds.startRectangle;
                    }
                    else {
                        box = me.getBox(dep, true, assignmentData);

                        // Row might be in collapsed node, in which case we get no box
                        if (box) {
                            startRectangle = new Rectangle(
                                box.start,
                                box.top,
                                box.end - box.start,
                                box.bottom - box.top
                            );
                        }
                    }

                    endRectangle = taskRect;
                }

                if (startRectangle && endRectangle)  {
                    me.drawDependency(dep, { startRectangle, endRectangle }, assignmentData);
                }
            });
        });
    }

    scheduleDraw(relayout = false) {
        const me = this;

        // There way be number of concurrent calls to this method, we need to reset cache if at least
        // once it was called with relayout = true
        if (relayout) {
            me.resetBoundsCache();
        }

        // If the scheduler/gantt is doing an animated update, schedule the draw
        // for when that's done so that we get correct element boxes.
        if (me.client.isAnimating) {
            if (!me.clientTransitionRemover) {
                me.clientTransitionRemover = me.client.on({
                    transitionend() {
                        me.clientTransitionRemover();
                        me.clientTransitionRemover = null;
                        me.draw();
                    },
                    once : true
                });
            }
        }
        // Otherwise, schedule the draw for the next frame.
        else {
            me.doScheduleDraw();
        }
    }

    /**
     * Draws all dependencies that overlap the current viewport
     */
    draw(reLayout = false) {
        const
            me        = this,
            scheduler = me.client;

        // Early bailout if we get here before we have any deps or rows rendered
        if (!me.oldDrawnDependencies && !me.dependencyStore.count || !scheduler.isPainted) {
            return;
        }
        // if animation is in progress, schedule drawing and skip current one
        if (scheduler.isAnimating) {
            scheduler.on({
                transitionend() {
                    me.scheduleDraw(true);
                },
                once : true
            });

            return;
        }

        // viewBox is the bounds of the current viewport, used to determine which dependencies to draw
        const viewBox = scheduler.timeAxisSubGrid.viewRectangle;

        if (reLayout) {
            me.resetBoundsCache();
        }

        me.oldDrawnDependencies = me.drawnDependencies;
        me.drawnDependencies = [];

        // too early
        if (!viewBox.width || !viewBox.height) return [];

        // expand viewBox with buffer size
        viewBox.inflate(me.bufferSize);

        // Do not draw if no rows
        if (!me.disabled && me.rowStore.count && scheduler.rowManager.rowCount) {
            const
                consideredDependencies = {},
                { dependencyGridCache, cacheGridSize } = me,
                viewLeft               = Math.floor(Math.max(viewBox.left, 0) / cacheGridSize.x),
                viewRight              = Math.floor(viewBox.right / cacheGridSize.x),
                topIndex               = Math.floor(scheduler.rowManager.topRow.dataIndex / cacheGridSize.index),
                bottomIndex            = Math.floor(scheduler.rowManager.bottomRow.dataIndex / cacheGridSize.index),
                dependenciesToDraw     = [];

            let x, rowIndex, i;

            // Iterate over virtual dependency grid cells, pushing each dependency that intersects that cell
            for (x = viewLeft; x <= viewRight; x++) {
                for (rowIndex = topIndex; rowIndex <= bottomIndex; rowIndex++) {
                    const
                        cacheX = dependencyGridCache[x],
                        deps   = cacheX && cacheX[rowIndex];

                    for (i = 0; deps && i < deps.length; i++) {
                        const
                            { dependency, assignmentData, metaId } = deps[i],
                            // Unique id for dependency combined with assignment
                            flagId = dependency.id + '-' + metaId;

                        if (!consideredDependencies[flagId]) {
                            // Only draw those actually in view
                            const bounds = me.getDependencyBounds(dependency, assignmentData);
                            // TODO: get rid of this export-specific flag
                            if (bounds && bounds.bounds.intersect(viewBox, true) || scheduler.ignoreViewBox) {
                                dependenciesToDraw.push([dependency, bounds, assignmentData]);
                            }

                            consideredDependencies[flagId] = true;
                        }
                    }
                }
            }

            // Append dependencies to the DOM only after all have been calculated
            dependenciesToDraw.forEach(([dependency, bounds, assignmentData]) => {
                me.drawDependency(dependency, bounds, assignmentData);
            });
        }

        // Stop forcing recalculation of bounds
        me._resetBoundsCache = false;

        // Release elements for any dependencies that wasn't drawn
        me.oldDrawnDependencies.forEach(data => me.releaseDependency(data.dependency, data.assignmentData));

        scheduler.trigger('dependenciesDrawn');

        this.isDrawn = true;
    }

    /**
     * Release a dependency that is determined to be no longer visible
     * @param {Scheduler.model.DependencyModel} dependency
     */
    releaseDependency(dependency, assignmentData = null) {
        // Remove for all assignments (related to this client, store might be shared)
        if (assignmentData === true) {
            Object.keys(dependency.meta.map || {}).filter(key => key.startsWith(this.client.id)).forEach(key => {
                const data = dependency.meta.map[key];
                if (data.lineElement) {
                    data.lineElement.remove();
                    data.lineElement = null;
                }
            });
        }
        // Remove specific
        else {
            const
                metaId = this.getMetaId(assignmentData),
                lineElement = dependency.instanceMeta(metaId).lineElement;

            if (lineElement) {
                dependency.instanceMeta(metaId).lineElement = null;
                // Not reusing elements for other lines currently
                lineElement.remove();
            }
        }
    }

    render() {
        const
            me = this,
            scheduler = me.client;

        if (me.showTooltip) {
            me.tooltip = me.createTooltip();
        }

        scheduler.timeAxis.on({
            endreconfigure : me.resetGridCache,
            thisObj        : me
        });

        scheduler.rowManager.on({
            refresh           : me.onRowsRefresh, // redraws dependencies after zoom
            changetotalheight : me.onChangeTotalHeight, // redrawn dependencies after group collapse
            thisObj           : me
        });

        // dependencies are drawn on scroll, both horizontal and vertical
        scheduler.on({
            horizontalscroll       : me.onHorizontalScroll,
            svgcanvascreated       : me.createMarkers,
            togglenode             : me.onToggleNode,
            scroll                 : me.onVerticalScroll,
            //            eventlayout      : me.onEventLayout,
            timelineviewportresize : me.onViewportResize,
            thisObj                : me
        });

        if (me.highlightDependenciesOnEventHover) {
            scheduler.on(scheduler.scheduledEventName + 'MouseEnter', (params) => me.highlightEventDependencies(params.eventRecord || params.taskRecord));
            scheduler.on(scheduler.scheduledEventName + 'MouseLeave', (params) => me.unhighlightEventDependencies(params.eventRecord || params.taskRecord));
        }

        me.draw();
    }

    renderContents() {
        this.draw();
    }

    //endregion

    //region Connector sides

    /**
     * Gets displaying item start side
     *
     * @param {Scheduler.model.TimeSpan} timeSpanRecord
     * @return {String} 'left' / 'right' / 'top' / 'bottom'
     */
    getConnectorStartSide(timeSpanRecord) {
        return this.client.currentOrientation.getConnectorStartSide(timeSpanRecord);
    }

    /**
     * Gets displaying item end side
     *
     * @param {Scheduler.model.TimeSpan} timeSpanRecord
     * @return {String} 'left' / 'right' / 'top' / 'bottom'
     */
    getConnectorEndSide(timeSpanRecord) {
        return this.client.currentOrientation.getConnectorEndSide(timeSpanRecord);
    }

    //endregion

    //region Tooltip

    createTooltip() {
        const
            me = this,
            scheduler = me.client;

        return new Tooltip(Object.assign({
            align          : 'b-t',
            id             : `${scheduler.id}-dependency-tip`,
            //TODO: need some way better to specify this. maybe each feature should be queried?
            forSelector    : `.b-timelinebase:not(.b-eventeditor-editing):not(.b-resizing-event):not(.b-dragcreating):not(.b-dragging-event):not(.b-creating-dependency) .${me.baseCls}`,
            clippedBy      : [scheduler.timeAxisSubGridElement, scheduler.bodyContainer],
            forElement     : scheduler.timeAxisSubGridElement,
            showOnHover    : true,
            hoverDelay     : 0,
            hideDelay      : 0,
            anchorToTarget : false,
            trackMouse     : false,
            getHtml        : me.getHoverTipHtml.bind(me)
        }, me.tooltip || {}));
    }

    /**
     * Generates html for the tooltip shown when hovering a dependency
     * @param {Object} tooltipConfig
     * @returns {string} Html to display in the tooltip
     * @private
     */
    getHoverTipHtml({ forElement }) {
        const
            me         = this,
            dependency = me.getDependencyForElement(forElement),
            fromEvent  = me.dataApi.getDependencySourceEvent({ dependency, eventStore : this.eventStore }),
            toEvent    = me.dataApi.getDependencyTargetEvent({ dependency, eventStore : this.eventStore });

        return TemplateHelper.tpl`
             <table class="b-sch-dependency-tooltip">
                <tr>
                    <td>${me.L('from')}: </td>
                    <td>${fromEvent.name}</td>
                    <td>
                        <div class="b-sch-box b-${fromBoxSide[dependency.type]}"></div>
                    </td>
                </tr>
                <tr>
                    <td>${me.L('to')}: </td>
                    <td>${toEvent.name}</td>
                    <td><div class="b-sch-box b-${toBoxSide[dependency.type]}"></div></td>
                </tr>
            </table>
        `;
    }

    //endregion
}

GridFeatureManager.registerFeature(Dependencies, false, 'Scheduler');

// region polyfills
// from https://github.com/eligrey/classList.js
if (document.createElementNS && !('classList' in document.createElementNS('http://www.w3.org/2000/svg', 'g'))) {
    (function(view) {
        if (!('Element' in view)) return;

        var classListProp         = 'classList',
            protoProp             = 'prototype',
            elemCtrProto          = view.Element[protoProp],
            objCtr                = Object,
            strTrim               = String[protoProp].trim || function() {
                return this.replace(/^\s+|\s+$/g, '');
            },
            arrIndexOf            = Array[protoProp].indexOf || function(item) {
                for (var i = 0, len = this.length; i < len; i++) {
                    if (i in this && this[i] === item) {
                        return i;
                    }
                }
                return -1;
            },
            // Vendors: please allow content code to instantiate DOMExceptions
            DOMEx                 = function(type, message) {
                this.name = type;
                this.code = DOMException[type]; // eslint-disable-line no-undef
                this.message = message;
            },
            checkTokenAndGetIndex = function(classList, token) {
                if (token === '') {
                    throw new DOMEx('SYNTAX_ERR', 'The token must not be empty.');
                }
                if (/\s/.test(token)) {
                    throw new DOMEx('INVALID_CHARACTER_ERR', 'The token must not contain space characters.');
                }
                return arrIndexOf.call(classList, token);
            },
            ClassList             = function(elem) {
                var trimmedClasses = strTrim.call(elem.getAttribute('class') || ''),
                    classes        = trimmedClasses ? trimmedClasses.split(/\s+/) : [];

                for (var i = 0, len = classes.length; i < len; i++) {
                    this.push(classes[i]);
                }
                this._updateClassName = function() {
                    elem.setAttribute('class', this.toString());
                };
            },
            classListProto        = ClassList[protoProp] = [],
            classListGetter = function() {
                return new ClassList(this);
            };

        // Most DOMException implementations don't allow calling DOMException's toString()
        // on non-DOMExceptions. Error's toString() is sufficient here.
        DOMEx[protoProp] = Error[protoProp];
        classListProto.item = function(i) {
            return this[i] || null;
        };
        classListProto.contains = function(token) {
            return ~checkTokenAndGetIndex(this, token + '');
        };
        classListProto.add = function() {
            var tokens  = arguments,
                i       = 0,
                l       = tokens.length,
                token,
                updated = false;

            do {
                token = tokens[i] + '';
                if (!~checkTokenAndGetIndex(this, token)) {
                    this.push(token);
                    updated = true;
                }
            }
            while (++i < l);

            if (updated) {
                this._updateClassName();
            }
        };
        classListProto.remove = function() {
            var tokens  = arguments,
                i       = 0,
                l       = tokens.length,
                token,
                updated = false,
                index;

            do {
                token = tokens[i] + '';
                index = checkTokenAndGetIndex(this, token);
                while (~index) {
                    this.splice(index, 1);
                    updated = true;
                    index = checkTokenAndGetIndex(this, token);
                }
            }
            while (++i < l);

            if (updated) {
                this._updateClassName();
            }
        };
        classListProto.toggle = function(token, force) {
            var result = this.contains(token),
                method = result ? force !== true && 'remove' : force !== false && 'add';

            if (method) {
                this[method](token);
            }

            if (force === true || force === false) {
                return force;
            }
            else {
                return !result;
            }
        };
        classListProto.replace = function(token, replacementToken) {
            var index = checkTokenAndGetIndex(token + '');
            if (~index) {
                this.splice(index, 1, replacementToken);
                this._updateClassName();
            }
        };
        classListProto.toString = function() {
            return this.join(' ');
        };

        if (objCtr.defineProperty) {
            var classListPropDesc = {
                get          : classListGetter,
                enumerable   : true,
                configurable : true
            };
            try {
                objCtr.defineProperty(elemCtrProto, classListProp, classListPropDesc);
            }
            catch (ex) { // IE 8 doesn't support enumerable:true
                // adding undefined to fight this issue https://github.com/eligrey/classList.js/issues/36
                // modernie IE8-MSW7 machine has IE8 8.0.6001.18702 and is affected
                if (ex.number === undefined || ex.number === -0x7FF5EC54) {
                    classListPropDesc.enumerable = false;
                    objCtr.defineProperty(elemCtrProto, classListProp, classListPropDesc);
                }
            }
        }
        else if (objCtr[protoProp].__defineGetter__) {
            elemCtrProto.__defineGetter__(classListProp, classListGetter);
        }
    }(window));
}

// There is full or partial native classList support, so just check if we need
// to normalize the add/remove and toggle APIs.

(function() {
    var testElement = document.createElement('_');

    testElement.classList.add('c1', 'c2');

    // Polyfill for IE 10/11 and Firefox <26, where classList.add and
    // classList.remove exist but support only one argument at a time.
    if (!testElement.classList.contains('c2')) {
        var createMethod = function(method) {
            var original = DOMTokenList.prototype[method]; // eslint-disable-line no-undef

            DOMTokenList.prototype[method] = function(token) { // eslint-disable-line no-undef
                for (var i = 0, len = arguments.length; i < len; i++) {
                    token = arguments[i];
                    original.call(this, token);
                }
            };
        };
        createMethod('add');
        createMethod('remove');
    }

    testElement.classList.toggle('c3', false);

    // Polyfill for IE 10 and Firefox <24, where classList.toggle does not
    // support the second argument.
    if (testElement.classList.contains('c3')) {
        var _toggle = DOMTokenList.prototype.toggle; // eslint-disable-line no-undef

        DOMTokenList.prototype.toggle = function(token, force) { // eslint-disable-line no-undef
            if (1 in arguments && !this.contains(token) === !force) {
                return force;
            }
            else {
                return _toggle.call(this, token);
            }
        };
    }

    // replace() polyfill
    if (!('replace' in document.createElement('_').classList)) {
        DOMTokenList.prototype.replace = function(token, replacementToken) { // eslint-disable-line no-undef
            var tokens = this.toString().split(' '),
                index  = tokens.indexOf(token + '');

            if (~index) {
                tokens = tokens.slice(index);
                this.remove.apply(this, tokens);
                this.add(replacementToken);
                this.add.apply(this, tokens.slice(1));
            }
        };
    }

    testElement = null;
}());
// endregion
