import DateHelper from '../../Core/helper/DateHelper.js';
import Model from '../../Core/data/Model.js';
import TimeSpan from './TimeSpan.js';
import LocaleManager from '../../Core/localization/LocaleManager.js';

/**
 * @module Scheduler/model/DependencyBaseModel
 */

const canonicalDependencyTypes = [
    'SS',
    'SF',
    'FS',
    'FF'
];

/**
 * Base class used for both Ext Scheduler and Ext Gantt. Not intended to be used directly
 *
 * @extends Core/data/Model
 */
export default class DependencyBaseModel extends Model {
    //region Fields

    /**
     * An enumerable object, containing names for the dependency types integer constants.
     * - 0 StartToStart
     * - 1 StartToEnd
     * - 2 EndToStart
     * - 3 EndToEnd
     * @property {Object}
     * @readonly
     */
    static get Type() {
        return {
            StartToStart : 0,
            StartToEnd   : 1,
            EndToStart   : 2,
            EndToEnd     : 3
        };
    }

    static get fields() {
        return [
            // 3 mandatory fields

            /**
             * From event, id of source event
             * @field {String|number} from
             */
            { name : 'from' },

            /**
             * To event, id of target event
             * @field {String|number} to
             */
            { name : 'to' },

            /**
             * Dependency type, see static property Type
             * @field {Number} type
             * @default 2
             */
            { name : 'type', type : 'int', defaultValue : 2 },

            /**
             * CSS class to apply to lines drawn for the dependency
             * @field {String} cls
             */
            { name : 'cls', defaultValue : '' },

            /**
             * Bidirectional, drawn with arrows in both directions
             * @field {Boolean} bidirectional
             */
            { name : 'bidirectional', type : 'boolean' },

            /**
             * Start side on source (top, left, bottom, right)
             * @field {String} fromSide
             */
            { name : 'fromSide', type : 'string' },

            /**
             * End side on target (top, left, bottom, right)
             * @field {String} toSide
             */
            { name : 'toSide', type : 'string' },

            /**
             * The magnitude of this dependency's lag (the number of units).
             * @field {Number} lag
             */
            { name : 'lag', type : 'number', allowNull : true, defaultValue : 0 },

            /**
             * The units of this dependency's lag, defaults to "d" (days). Valid values are:
             *
             * - "ms" (milliseconds)
             * - "s" (seconds)
             * - "m" (minutes)
             * - "h" (hours)
             * - "d" (days)
             * - "w" (weeks)
             * - "M" (months)
             * - "y" (years)
             *
             * This field is readonly after creation, to change lagUnit use #setlag().
             * @field {String} lagUnit
             */
            {
                name         : 'lagUnit',
                type         : 'string',
                defaultValue : 'd'
            }

            //{ name : 'highlighted', type : 'string', persist : false }
        ];
    }

    static get relationConfig() {
        return [
            { relationName : 'sourceEvent', fieldName : 'from', store : 'eventStore', collectionName : 'successors' },
            { relationName : 'targetEvent', fieldName : 'to', store : 'eventStore', collectionName : 'predecessors' }
        ];
    }

    //endregion

    //region Init

    construct(data) {
        super.construct(...arguments);

        if (data) {
            // Allow passing in event instances too
            if (data.from && data.from instanceof TimeSpan) {
                this.setSourceEvent(data.from);
                delete data.from;
            }

            if (data.to && data.to instanceof TimeSpan) {
                this.setTargetEvent(data.to);
                delete data.to;
            }
        }
    }

    //endregion

    get eventStore() {
        const { stores, unjoinedStores } = this;
        return stores[0] && stores[0].eventStore || unjoinedStores[0] && unjoinedStores[0].eventStore;
    }

    /**
     * Alias to dependency type, but when set resets {@link #field-fromSide} {@link #field-toSide} to null as well.
     *
     * @property {Number}
     */
    get hardType() {
        return this.getHardType();
    }

    set hardType(type) {
        this.setHardType(type);
    }

    /**
     * Returns dependency hard type, see {@link #property-hardType}.
     *
     * @return {Number}
     */
    getHardType() {
        return this.get('type');
    }

    /**
     * Sets dependency {@link #field-type} and resets {@link #field-fromSide} and {@link #field-toSide} to null.
     *
     * @param {Number} type
     */
    setHardType(type) {
        let result;

        if (type !== this.getHardType()) {
            result = this.set({
                type,
                fromSide : null,
                toSide   : null
            });
        }

        return result;
    }

    get lag() {
        return this.get('lag');
    }

    set lag(lag) {
        if (typeof lag === 'number') {
            this.set({
                lag
            });
        }
        else {
            this.setLag(lag);
        }
    }

    /**
     * Sets lag and lagUnit in one go. Only allowed way to change lagUnit, the lagUnit field is
     * readonly after creation
     * @param {Number|String|Object} lag The lag value. May be just a numeric magnitude, or a full string descriptor eg '1d'
     * @param {String} [lagUnit] Unit for numeric lag value, see {@link #field-lagUnit} for valid values
     */
    setLag(lag, lagUnit) {
        // Either they're only setting the magnitude
        // or, if it's a string, parse the full duration.
        if (arguments.length === 1) {
            if (typeof lag === 'number') {
                this.lag = lag;
            }
            else {
                //<debug>
                if (typeof lag !== 'string') {
                    throw new Error('Depenedency#setLag accepts either numeric magnitude, or a duration string');
                }
                //</debug>
                lag = DateHelper.parseDuration(lag);
                this.set({
                    lag     : lag.magnitude,
                    lagUnit : lag.unit
                });
            }
            return;
        }

        // Must be a number
        lag = parseFloat(lag);

        this.set({
            lag,
            lagUnit
        });
    }

    getLag() {
        if (this.lag) {
            return `${this.lag < 0 ? '-' : '+'}${Math.abs(this.lag)}${DateHelper.getShortNameOfUnit(this.lagUnit)}`;
        }
        return '';
    }

    /**
     * Property which encapsulates the lag's magnitude and units.
     * An object which contains two properties:
     * - magnitude : [Number](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number) The magnitude of the duration.
     * - unit : [String](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String) The unit in which the duration is measured, eg `'d'` for days.
     * @property {Object}
     */
    get fullLag() {
        return {
            unit      : this.lagUnit,
            magnitude : this.lag
        };
    }

    set fullLag(lag) {
        if (typeof lag === 'string') {
            this.setLag(lag);
        }
        else {
            this.setLag(lag.magnitude, lag.unit);
        }
    }

    /**
     * Gets/sets the source event of the dependency
     *
     * @property {Scheduler.model.EventModel}
     */
    set sourceEvent(event) {
        this.from = event.id;
    }

    /**
     * Gets/sets the target event of the dependency
     *
     * @property {Scheduler.model.EventModel}
     */
    set targetEvent(event) {
        this.to = event.id;
    }

    /**
     * Returns true if the linked events have been persisted (e.g. neither of them are 'phantoms')
     *
     * @property {Boolean}
     * @readonly
     */
    get isPersistable() {
        const
            me = this,
            { stores, unjoinedStores } = me;

        let store = stores[0],
            result;

        if (store) {
            const source    = me.getSourceEvent(),
                target      = me.getTargetEvent(),
                crudManager = store.crudManager;

            // if crud manager is used it can deal with phantom source/target since it persists all records in one batch
            // if no crud manager used we have to wait till source/target are persisted
            result = source && (crudManager || !source.hasGeneratedId) && target && (crudManager || !target.hasGeneratedId);
        }
        else {
            result = Boolean(unjoinedStores[0]);
        }

        return result;
    }

    /**
     * Returns the source event of the dependency
     *
     * @return {Scheduler.model.EventModel} The source event of this dependency
     */
    getSourceEvent(eventStore = this.eventStore) {
        if (!eventStore && this.unjoinedStores.length) {
            eventStore = this.leftProjectEventStore;
        }

        return eventStore && eventStore.getById(this.from);
    }

    /**
     * Gets/sets the dependency type
     *
     * @property {Number}
     */

    /**
     * Gets/sets the name of field holding the CSS class for each rendered dependency element
     *
     * @property {String} cls
     */

    /**
     * Returns the target event of the dependency
     *
     * @return {Scheduler.model.EventModel} The target event of this dependency
     */
    getTargetEvent(eventStore = this.eventStore) {
        if (!eventStore && this.unjoinedStores.length) {
            eventStore = this.leftProjectEventStore;
        }

        return eventStore && eventStore.getById(this.to);
    }

    getDateRange(doNotNormalize = false) {
        const sourceTask = this.sourceEvent,
            targetTask = this.targetEvent;

        if (sourceTask && targetTask && sourceTask.isScheduled && targetTask.isScheduled) {
            let Type = DependencyBaseModel.Type,
                sourceDate,
                targetDate;

            switch (this.type) {
                case Type.StartToStart:
                    sourceDate = sourceTask.startDate;
                    targetDate = targetTask.startDate;
                    break;

                case Type.StartToEnd:
                    sourceDate = sourceTask.startDate;
                    targetDate = targetTask.endDate;
                    break;

                case Type.EndToEnd:
                    sourceDate = sourceTask.endDate;
                    targetDate = targetTask.endDate;
                    break;

                case Type.EndToStart:
                    sourceDate = sourceTask.endDate;
                    targetDate = targetTask.startDate;
                    break;
            }

            return {
                start : doNotNormalize ? sourceDate : DateHelper.min(sourceDate, targetDate),
                end   : doNotNormalize ? targetDate : DateHelper.max(sourceDate, targetDate)
            };
        }

        return null;
    }

    /**
     * Applies given CSS class to dependency, the value doesn't persist
     *
     * @param {String} cls
     */
    highlight(cls) {
        const me = this,
            h  = me.highlighted ? me.highlighted.split(' ') : [];

        if (!h.includes(cls)) me.highlighted = h.concat(cls).join(' ');
    }

    /**
     * Removes given CSS class from dependency if applied, the value doesn't persist
     *
     * @param {String} cls
     */
    unhighlight(cls) {
        const me        = this,
            highlighted = me.highlighted;

        if (highlighted) {
            const h = highlighted.split(' '),
                idx = h.findIndex(i => i === cls);

            if (idx >= 0) {
                h.splice(idx, 1);
                me.highlighted = h.join(' ');
            }
        }
    }

    /**
     * Checks if the given CSS class is applied to dependency.
     *
     * @param {String} cls
     * @return {Boolean}
     */
    isHighlightedWith(cls) {
        const me        = this,
            highlighted = me.highlighted;

        return highlighted && highlighted.split(' ').includes(cls);
    }

    getConnectorString(raw) {
        const rawValue = canonicalDependencyTypes[this.type];

        if (raw) {
            return rawValue;
        }

        // FS => empty string; it's the default
        if (this.type === DependencyBaseModel.Type.EndToStart) {
            return '';
        }

        const locale = LocaleManager.locale;

        // See if there is a local version of SS, SF or FF
        if (locale) {
            const localized = locale.Scheduler && locale.Scheduler[rawValue];
            if (localized) {
                return localized;
            }
        }

        return rawValue;
    }

    toString() {
        return `${this.from}${this.getConnectorString()}${this.getLag()}`;
    }

    /**
     * Returns `true` if the dependency is valid. Has valid type and both source and target ids set and not links to itself.
     *
     * @return {Boolean}
     * @typings ignore
     */
    isValid(taskStore) {
        const { from, to, type } = this;

        return typeof type === 'number' && from && from !== '' && to != null && to !== '' && from !== to;
    }
}

DependencyBaseModel.exposeProperties();
