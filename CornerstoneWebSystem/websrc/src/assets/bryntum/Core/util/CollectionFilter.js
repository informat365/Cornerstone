import Base from '../Base.js';
import ObjectHelper from '../helper/ObjectHelper.js';
import FunctionHelper from '../helper/FunctionHelper.js';
import IdHelper from '../helper/IdHelper.js';

/**
 * @module Core/util/CollectionFilter
 */

/**
 * A class which encapsulates a single filter operation which may be applied to any object to decide whether to
 * include or exclude it from a set.
 *
 * A CollectionFilter generally has at least three main properties:
 *
 * * `property` - The name of a property in candidate objects from which to extract the value to test
 * * `value` - The value which  this filter uses to test against.
 * * `operator` - The comparison operator, eg: `'='` or `'>'` etc.
 *
 * Given these three essential values, further configurations may affect how the filter is applied:
 *
 * * `caseSensitive` - If configured as `false`, string comparisons are case insensitive.
 * * `convert` - A function which, when passed the extracted value from the candidate object, returns the value to test.
 *
 * A filter may also be configured with a single `filterBy` property. This function is just passed the raw
 * candidate object and must return `true` or `false`.
 *
 * A CollectionFilter may be configured to encapsulate a single filtering function by passing that function as the sole
 * parameter to the constructor:
 *
 *     new CollectionFilter(candidate => candidate.title.contains('search string'));
 *
 */
export default class CollectionFilter extends Base {
    static get defaultConfig() {
        return {
            /**
             * The name of a property of candidate objects which yields the value to compare against this CollectionFilter's {@link #config-value}.
             * @config {String}
             */
            property : null,

            /**
             * The value against which to compare the {@link #config-property} of candidate objects.
             * @config {*}
             */
            value : null,

            /**
             * The operator to use when comparing a candidate object's {@link #config-property} with this CollectionFilter's {@link #config-value}.
             * May be: `'='`, `'!='`, `'>'`, `'>='`, `'<'`, `'<='`, `'*'`, `'startsWith'`, `'endsWith'`
             * @config {String}
             */
            operator : null,

            /**
             * May be used in place of the {@link #config-property}, {@link #config-value} and {@link #config-property} configs. A function which
             * accepts a candidate object and returns `true` or `false`
             * @config {Function}
             */
            filterBy : null,

            /**
             * A function which accepts a value extracted from a candidate object using the {@link #config-property} name, and
             * returns the value which the filter should use to compare against its {@link #config-value}.
             * @config {Function}
             */
            convert : null,

            /**
             * Configure as `false` to have string comparisons case insensitive.
             * @config {Boolean}
             */
            caseSensitive : true,

            /**
             * The `id` of this Filter for when used by a {@link Core.util.Collection} Collection.
             * By default the `id` is the {@link #config-property} value.
             * @config {String}
             */
            id : null
        };
    }

    construct(config) {
        if (typeof config === 'function') {
            config = {
                filterBy : config
            };
        }

        super.construct(config);

        //<debug>
        if (!this._filterBy) {
            if (!this.property) {
                throw new Error('CollectionFilter must be configured with the name of a property to test in candidate items');
            }
        }
        //</debug>
    }

    /**
     * When in a Collection (A Collection holds its Filters in a Collection), we need an id.
     * @property {String}
     * @private
     */
    get id() {
        return this._id || (this._id = this.property || IdHelper.generateId('b-filter'));
    }

    set id(id) {
        this._id = id;
    }

    onChange(propertyChanged) {
        const me = this;

        // Inform any owner (eg a Store), that it has to reassess its CollectionFilters
        if (!me.isConfiguring && me.owner && !me.owner.isConfiguring && me.owner.onFilterChanged) {
            me.owner.onFilterChanged(me, propertyChanged);
        }
    }

    get filterBy() {
        return this._filterBy || this.defaultFilterBy;
    }

    /**
     * May be used in place of the {@link #config-property}, {@link #config-value} and {@link #config-property} configs. A function which
     * accepts a candidate object and returns `true` or `false`
     * @type {Function}
     */
    set filterBy(filterBy) {
        this._filterBy = filterBy;
    }

    defaultFilterBy(candidate) {
        return this[this.operator](this.convert(candidate[this.property]));
    }

    /**
     * The name of a property of candidate objects which yields the value to compare against this CollectionFilter's {@link #config-value}.
     * @type {String}
     */
    set property(property) {
        this._property = property;

        // Signal to owner about filter change
        this.onChange('property');
    }

    get property() {
        return this._property;
    }

    /**
     * The value against which to compare the {@link #config-property} of candidate objects.
     * @type {*}
     */
    set value(value) {
        this._value = !this.caseSensitive && (typeof value === 'string') ? value.toLowerCase() : value;

        // Signal to owner about filter change
        this.onChange('value');
    }

    get value() {
        return this._value;
    }

    /**
     * The operator to use when comparing a candidate object's {@link #config-property} with this CollectionFilter's {@link #config-value}.
     * May be: `'='`, `'!='`, `'>'`, `'>='`, `'<'`, `'<='`, `'*'`, `'startsWith'`, `'endsWith'`
     * @type {String}
     */
    set operator(operator) {
        this._operator = operator;

        // Signal to owner about filter change
        this.onChange('operator');
    }

    get operator() {
        return this._operator || ((typeof this.value === 'string') ? '*' : '=');
    }

    convert(value) {
        return !this.caseSensitive && (typeof value === 'string') ? value.toLowerCase() : value;
    }

    filter(candidate) {
        return this.filterBy(candidate);
    }

    startsWith(v) {
        return String(v).startsWith(this.value);
    }

    endsWith(v) {
        return String(v).endsWith(this.value);
    }

    '='(v) {
        return ObjectHelper.isEqual(v, this.value);
    }

    '!='(v) {
        return !ObjectHelper.isEqual(v, this.value);
    }

    '>'(v) {
        return ObjectHelper.isMoreThan(v, this.value);
    }

    '>='(v) {
        return ObjectHelper.isMoreThan(v, this.value) || ObjectHelper.isEqual(v, this.value);
    }

    '<'(v) {
        return ObjectHelper.isLessThan(v, this.value);
    }

    '<='(v) {
        return ObjectHelper.isLessThan(v, this.value) || ObjectHelper.isEqual(v, this.value);
    }

    '*'(v) {
        return ObjectHelper.isPartial(v, this.value);
    }

    // Accepts an array or a Collection
    static generateFiltersFunction(filters) {
        if (!filters || (!filters.length && !filters.count)) {
            return FunctionHelper.returnTrue;
        }

        return function(candidate) {
            let match = true;

            for (const filter of filters) {
                // Skip disabled filters
                if (!filter.disabled) {
                    match = filter.filter(candidate);
                }
                if (!match) {
                    break;
                }
            }

            return match;
        };
    }
}
