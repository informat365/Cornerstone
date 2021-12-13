/* eslint-disable no-prototype-builtins */
import DateHelper from '../helper/DateHelper.js';
import ObjectHelper from '../helper/ObjectHelper.js';
import ModelStm from './stm/mixin/ModelStm.js';
import TreeNode from './mixin/TreeNode.js';

const
    internalProps = {
        children : 1,
        data     : 1,
        meta     : 1
    },
    _undefined    = undefined,
    emptyObject   = {},
    convertDate   = function(date) {
        if (!(date instanceof Date)) {
            // Use configured format, if null/undefined use DateHelpers default format
            date = DateHelper.parse(date, this.format || this.dateFormat || DateHelper.defaultFormat);
        }
        // if parsing has failed, we would like to return `undefined` to indicate the "absence" of data
        // instead of `null` (presence of "empty" data)
        return date || _undefined;
    },
    isEqual       = (field, oldValue, value) => {
        // using fn to keep field scope
        return field && field.isEqual ? field.isEqual(oldValue, value) : ObjectHelper.isEqual(oldValue, value);
    },
    nullFn        = () => {};

/**
 * @module Core/data/Model
 */

/**
 * A Model is a definition for a record in a store. It defines which fields the data contains and exposes an interface
 * to access and manipulate that data.
 *
 * Models are created from json objects, the input json is stored in `Model#data`. By default it stores a shallow copy of
 * the raw json, but for records in stores configured with `useRawData: true` it stores the supplied json object as is.
 *
 * ## Defining fields
 * A Model can either define its fields explicitly or have them created from its data. This snippet shows a model with
 * two fields:
 *
 * ```
 * class Person extends Model {
 *     static get fields() {
 *         return [
 *             'name',
 *             { name : 'birthday', type : 'date', dateFormat : 'YYYY-MM-DD' },
 *             { name : 'shoeSize', type : 'number', defaultValue : 11 },
 *             { name : 'age', readOnly : true }
 *         ];
 *     }
 * }
 * ```
 *
 * The first field (name) has an unspecified type, which is fine in most cases since this is JavaScript. The second
 * field (birthday) is defined to be a date, which will make the model parse any supplied value into an actual date.
 * The parsing is handled by {@link Core/helper/DateHelper#function-parse-static DateHelper.parse()} using the specified
 * `dateFormat`, or if no format is specified using
 * {@link Core/helper/DateHelper#property-defaultFormat-static DateHelper.defaultFormat}. Currently date is the only
 * specified type available.
 *
 * You can also set a `defaultValue that will be used if the data doesn't contain a value for the field:
 *
 * ```
 *       { name : 'shoeSize', type : 'number', defaultValue : 11 }
 * ```
 *
 * To create a record from a Model, supply data to its constructor:
 *
 * ```
 * let guy = new Person({
 *   id       : 1,
 *   name     : 'Dude',
 *   birthday : '2014-09-01'
 * });
 * ```
 *
 * If no id is specified, a temporary id will be generated.
 *
 * ## Persisting fields
 * By default all fields are persisted. If you don't want particular field to get saved to the server, configure it with
 * `persist: false`. In this case field will not be among changes which are sent by
 * {@link Core/data/AjaxStore#function-commit store.commit()}, otherwise its behavior doesn't change.
 *
 * ```
 * class Person extends Model {
 *     static get fields() {
 *         return [
 *             'name',
 *             { name : 'age', persist : false }
 *         ];
 *     }
 * }
 * ```
 *
 * ## Id field
 * By default Model expects its id to be stored in a field named "id". The name of the field can be customized by
 * setting {@link #property-idField-static}:
 *
 * ```
 * class Person extends Model {
 *     static get fields() {
 *         return {
 *             'name',
 *             { name : 'age', persist : false },
 *             { name : 'personId' },
 *             { name : 'birthday', type : 'date' }
 *         }
 *     }
 * }
 * // Id drawn from 'id' property by default; use custom field here
 * Person.idField = 'personId';
 *
 * let girl = new Person({
 *    personId : 2,
 *    name     : 'Lady',
 *    birthday : '2011-11-05'
 * });
 * ```
 *
 * ## Getting and setting values
 * Fields are used to generate getters and setters on the records. Use them to access or modify values (they are
 * reactive):
 *
 * ```
 * console.log(guy.name);
 * girl.birthday = new Date(2011,10,6);
 * ```
 *
 * NOTE: In an application with multiple different models you should subclass Model, since the prototype is decorated
 * with getters and setters. Otherwise you might got unforeseen collisions.
 *
 * ## Field data mapping
 * By default fields are mapped to data using their name. If you for example have a "name" field it expects data to be
 * `{ name: 'Some name' }`. If you need to map it to some other property, specify `dataSource` in your field definition:
 *
 * ```
 * class Person extends Model {
 *   static get fields {
 *     return [
 *       { name : 'name', dataSource : 'TheName' }
 *     ];
 * }
 *
 * // This is now OK:
 * let dude = new Person({ TheName : 'Manfred' });
 * console.log(dude.name); // --> Manfred
 * ```
 *
 * ## Tree API
 * This class mixes in the {@link Core/data/mixin/TreeNode TreeNode} mixin which provides an API for tree related functionality (only relevant if your
 * store is configured to be a {@link Core/data/Store#config-tree tree}).
 *
 * @mixes Core/data/mixin/TreeNode
 * @mixes Core/data/stm/mixin/ModelStm
 */
export default class Model extends TreeNode(ModelStm()) {
    /**
     * The name of the data field which provides the ID of instances of this Model.
     * @property {String}
     * @category Fields
     */
    static set idField(idField) {
        this._assignedIdField = true;
        this._idField = idField;
    }

    static get idField() {
        return this._idField;
    }

    /**
     * The name of the data field which holds children of this Model when used in a tree structure
     * ```javascript
     * MyModel.childrenField = 'kids';
     * const parent = new MyModel({
     *   name : 'Dad',
     *   kids : [
     *     { name : 'Daughter' },
     *     { name : 'Son' }
     *   ]
     * });
     * ```
     * @property {String}
     * @category Fields
     */
    static set childrenField(childrenField) {
        this._childrenField = childrenField;
    }

    static get childrenField() {
        return this._childrenField || 'children';
    }

    /**
     * Returns index path to this node. This is the index of each node in the node path
     * starting from the topmost parent. (only relevant when its part of a tree store).
     * @returns {Number[]} The index of each node in the path from the topmost parent to this node.
     * @category Parent & children
     * @private
     */
    get indexPath() {
        const indices = [];

        for (let task = this; task && !task.isRoot; task = task.parent) {
            indices.unshift(task.parentIndex + 1);
        }

        return indices;
    }

    /**
     * Unique identifier for the record. Might be mapped to another dataSource using idField, but always exposed as
     * record.id. Will get a generated value if none is specified in records data.
     * @member {String|Number} id
     * @category Identification
     */

    //region Init

    /**
     * Constructs a new record from the supplied data.
     * @param {Object} [data] Raw data
     * @param {Core.data.Store} [store] Data store
     * @param {Object} [meta] Meta data
     * @function constructor
     * @category Misc
     */
    construct(data = {}, store = null, meta = null, skipExpose = false) {
        const
            me              = this,
            { constructor } = me,
            { fieldMap }    = constructor,
            stores          = store ? Array.isArray(store) ? store : [store] : [];

        store = stores[0];

        me.meta = Object.assign({
            modified : {}
        }, constructor.metaConfig, meta);

        // null passed to Base construct inhibits config processing.
        super.construct(null);

        // make getters/setters for fields, needs to be done before processing data to make sure defaults are available
        if (skipExpose) {
            if (!constructor.hasOwnProperty('fieldMap')) {
                constructor.exposeProperties();
            }
        }
        else {
            constructor.exposeProperties(data);
        }

        // It's only valid to do this once, on construction of the first instance
        if (!constructor.hasOwnProperty('idFieldProcessed')) {
            // idField can be overridden from meta, or from the store if we have not had an idField set programmatically
            // and if we have not had an id field defined above the base Model class level.

            let overriddenIdField = me.meta.idField;

            if (!overriddenIdField) {
                // Might have been set to Model after construction but before load
                if (constructor._assignedIdField) {
                    overriddenIdField = constructor.idField;
                }
                // idField on store was deprecated, but should still work to not break code
                // TODO: Remove in 3.0? Or reintroduce it...
                else if (store) {
                    overriddenIdField = store.idField;
                }
            }

            // If it's overridden to something different than we already have, replace the 'id' field in the fieldMap
            if (overriddenIdField && overriddenIdField !== fieldMap.id.dataSource) {
                constructor.addField({
                    name       : 'id',
                    dataSource : overriddenIdField
                });
            }
            constructor.idFieldProcessed = true;
        }

        // assign internalId, unique among all records
        me._internalId = Model._internalIdCounter++;

        // relation code expects store to be available for relation lookup, but actual join done below
        me.stores = [];
        me.unjoinedStores = [];

        // Superclass constructors may set this in their own way before this is called.
        if (!me.originalData) {
            me.originalData = data;
        }

        me.data = constructor.processData(data, false, store);

        // Consider undefined and null as missing id and generate one
        if (me.id == null) {
            // Assign a generated id silently, record should not be considered modified
            me.setData('id', me.generateId(store));
        }
        if (me.data[constructor.childrenField]) {
            me.processChildren(stores);
        }
        me.generation = 0;
    }

    /**
     * Compares this Model instance to the passed instance. If they are of the same type, and all fields
     * (except, obviously, `id`) are equal, this returns `true`.
     * @param {Core.data.Model} other The record to compare this record with.
     * @returns {Boolean} `true` if the other is of the same class and has all fields equal.
     */
    equals(other) {
        if (other instanceof this.constructor) {
            for (let { fields } = this, i = 0, { length } = fields; i < length; i++) {
                const
                    field    = fields[i],
                    { name } = field;

                if (name !== 'id' && !isEqual(field, this[name], other[name])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    get subclass() {
        return new this.constructor(Object.setPrototypeOf({
            id : _undefined
        }, this.data), this.stores[0], null, true);
    }

    /**
     * Processes raw data, converting values and setting defaults.
     * @private
     * @param {Object} data Raw data
     * @param {Boolean} [ignoreDefaults] Ignore setting default values, used when updating
     * @returns {Object} Processed data
     * @category Fields
     */
    static processData(data, ignoreDefaults = false, store) {
        const
            { fieldMap, defaultValues } = this,
            { useRawData = { enabled : false } } = store || { },
            // Store configured with useRawData uses the supplied data object, polluting it. When not configured with
            // useRawData it instead makes a shallow copy.
            processed = useRawData.enabled ? data : Object.assign({}, data);

        let fieldName;

        ignoreDefaults = ignoreDefaults || useRawData.disableDefaultValue;

        if (!ignoreDefaults) {
            for (fieldName in defaultValues) {
                if (processed[fieldName] === _undefined) processed[fieldName] = defaultValues[fieldName];
            }
        }

        if (!useRawData.disableTypeConversion) {
            // Convert field types which need converting
            for (fieldName in fieldMap) {
                const
                    fieldDefinition      = fieldMap[fieldName],
                    { name, dataSource } = fieldDefinition,
                    // Value might have been supplied either using mapped dataSource (when loading JSON etc. for example
                    // event.myStartDate) or as field name (from internal code, for example event.startDate). If [name]
                    // exists but not [dataSource], use it.
                    useNameForValue      = dataSource !== name && !ObjectHelper.pathExists(data, dataSource) && name in data,
                    convert              = !useRawData.disableTypeConversion && fieldDefinition.convert;

                // Only action field definitions which have a convert function or remap data
                if (useNameForValue || convert) {
                    const value = useNameForValue ? data[name] : data[dataSource];

                    // When ignoringDefaults, do not convert unspecified values
                    if (!ignoreDefaults || ((useNameForValue && name in data) || (!useNameForValue && dataSource in data))) {
                        ObjectHelper.setPath(
                            processed,
                            dataSource,
                            convert
                                ? fieldDefinition.convert(value)
                                : value
                        );
                        // Remove [startDate] from internal data holder, only keeping [myStartDate]
                        if (useNameForValue) {
                            delete processed[name];
                        }
                    }
                }
            }
        }

        return processed;
    }

    /**
     * Makes getters and setters for fields (from definitions and data). Called once when class is defined and once when
     * data is loaded first time.
     * @internal
     * @param {Object} [data] Raw data
     * @category Fields
     */
    static exposeProperties(data) {
        const me         = this,
            superclass = me.superclass,
            rawFields  = me.hasOwnProperty('fields') && me.fields;

        // Ensure that the class hierarchy processes its fields on first construction.
        if (superclass.exposeProperties && !superclass.hasOwnProperty('fieldMap')) {
            superclass.exposeProperties();
        }

        // Don't expose field properties if already done
        if (!me.hasOwnProperty('propertiesExposed')) {
            // Clone the superclass's defaults, and override that with our own defaults.
            // As we find fields with a defaultValue, more defaults may be added
            me.defaultValues = Object.assign({}, superclass.defaultValues, me.hasOwnProperty('defaults') ? me.defaults : null);

            // Hook up our field maps with the class hierarchy's fieldMaps.
            // We need to be able to look up field definitions by the name, or by the dataSource property name

            /**
             * An object containing all the _defined_ fields for this Model class. This will include all superclass's
             * defined fields through its prototype chain. So be aware that `Object.keys` and `Object.entries` will only
             * access this class's defined fields.
             * @member {Object} fieldMap
             * @static
             * @readonly
             * @category Fields
             */
            me.fieldMap = Object.setPrototypeOf({}, superclass.fieldMap || emptyObject);
            me.fieldDataSourceMap = Object.setPrototypeOf({}, superclass.fieldDataSourceMap || emptyObject);

            // Hook up our propertiesExposed with the superclass's version
            me.propertiesExposed = Object.setPrototypeOf({}, superclass.propertiesExposed || emptyObject);

            // If the idField is overridden at this level, create a new field
            if (me.hasOwnProperty('idField')) {
                me.addField({
                    name       : 'id',
                    dataSource : me.idField
                });
                me.propertiesExposed[me.idField] = true;
            }

            // Process fields defined in the class definition
            if (rawFields && rawFields.length) {
                rawFields.map(me.addField, me);
            }
        }

        // Process the raw data properties and expose them as fields unless the property name
        // has already been used by the "dataSource" of a defined field.
        if (me.autoExposeFields && data && !me.hasOwnProperty('propertiesExposedForData')) {
            for (const dataProperty in data) {
                if (!me.propertiesExposed[dataProperty]) {
                    // Create a field definition in our fieldMap with the flag that it's from data
                    me.addField({
                        name       : dataProperty,
                        dataSource : dataProperty,
                        fromData   : true
                    });
                }
            }
            me.propertiesExposedForData = true;
        }

        me.exposeRelations();
    }

    /**
     * Add a field definition in addition to those predefined in `fields`.
     * @param {String|Object} field A field name or definition
     */
    static addField(fieldDef) {
        if (fieldDef == null) {
            return;
        }

        if (typeof fieldDef === 'string') {
            fieldDef = {
                name : fieldDef
            };
        }

        const
            me = this,
            { propertiesExposed, defaultValues } = me,
            { name } = fieldDef,
            dataSource = fieldDef.dataSource || (fieldDef.dataSource = name);

        if (!fieldDef.hasOwnProperty('persist')) {
            fieldDef.persist = true;
        }

        if (fieldDef.type === 'date') {
            fieldDef.convert = convertDate;
        }
        me.fieldMap[name] = fieldDef;

        if (!me.fieldDataSourceMap[dataSource]) {
            me.fieldDataSourceMap[dataSource] = fieldDef;
        }

        // When iterating through the raw data, if autoExposeFields is set
        // We do not need to create properties for raw property names we've processed here
        if (dataSource.indexOf('.') === -1) {
            propertiesExposed[dataSource] = true;
        }
        // With complex mapping avoid exposing object as model field
        else {
            fieldDef.complexMapping = true;
            propertiesExposed[dataSource.split('.')[0]] = true;
        }

        // Maintain an object of defaultValues for fields.
        if ('defaultValue' in fieldDef) {
            defaultValues[dataSource] = fieldDef.defaultValue;
        }

        // Create a property on this Model's prototype, named for the defined field name
        // which reads the correct property out of the raw data object.
        me.createFieldProperty(name, dataSource, fieldDef);

        return fieldDef;
    }

    /**
     * Remove a field definition by name.
     * @param {String} fieldName Field name
     */
    static removeField(fieldName) {
        const definition = this.fieldMap[fieldName];
        if (definition) {
            const { dataSource } = definition;
            delete this.fieldMap[fieldName];
            delete this.fieldDataSourceMap[dataSource];
            delete this.prototype[fieldName];
            this._internalFields = null; // Regenerated on next usage
        }
    }

    /**
     * Create getter and setter functions for the specified field name under the specified key.
     *
     * @param {String} fieldName The defined field name (or property name if this is created by exposing a raw property)
     * @param {String} propertyName The property name to read off the data property
     * @param {Object} fieldDef The full field definition.
     * @private
     * @category Fields
     */
    static createFieldProperty(fieldName, propertyName, fieldDef) {
        const me = this;

        // checking (fieldName in me.prototype) instead of hasOwnProperty to catch cases where getters/setters are
        // manually created on some parent
        if (!internalProps[propertyName]) {

            if (!(fieldName in me.prototype)) {
                Object.defineProperty(me.prototype, fieldName, {
                    enumerable   : true,
                    configurable : true, // To allow removing it later
                    get() {
                        // no arrow functions here, need `this` to change to instance
                        // noinspection JSPotentiallyInvalidUsageOfClassThis
                        return this.get(fieldName);
                    },
                    // Only create a real setter if the field is read/write.
                    // privately, we will use setData to set a field's value
                    set : (fieldDef && fieldDef.readOnly) ? nullFn : function(value) {
                        // no arrow functions here, need `this` to change to instance
                        // noinspection JSPotentiallyInvalidUsageOfClassThis
                        this.set(fieldName, value);
                    }
                });
            }
        }
    }

    /**
     * Makes getters and setters for related records. Populates a Model#relation array with the relations, to allow it
     * to be modified later when assigning stores.
     * @internal
     * @category Relations
     */
    static exposeRelations() {
        const me = this;

        if (me.hasOwnProperty('relationsExposed')) return;

        if (me.relationConfig) {
            me.relationsExposed = true;
            me.relations = [];

            me.relationConfig.forEach(relation => {
                me.relations.push(relation);

                const name = relation.relationName;

                // getter and setter for related object
                if (!Reflect.ownKeys(me.prototype).includes(name)) {
                    Object.defineProperty(me.prototype, name, {
                        enumerable : true,
                        get        : function() {
                            // noinspection JSPotentiallyInvalidUsageOfClassThis
                            return this.getForeign(name);
                        },
                        set : function(value) {
                            // noinspection JSPotentiallyInvalidUsageOfClassThis
                            this.setForeign(name, value, relation);
                        }
                    });
                }
            });
        }
    }

    //endregion

    //region Fields

    /**
     * Flag checked from Store when loading data that determines if fields found in first records should be exposed in
     * same way as predefined fields.
     * @returns {Boolean}
     * @category Fields
     */
    static get autoExposeFields() {
        return true;
    }

    /**
     * Predefined fields, none per default, override in subclasses to add fields.
     * @returns {Object[]}
     * @readonly
     * @category Fields
     */
    static get fields() {
        return [];
    }

    static get internalFields() {
        const { fieldMap } = this;

        let result = this._internalFields;

        // Produce the array lazily - it probably will never be used.
        // Only available when we have exposed our properties.
        if (this.hasOwnProperty('fieldMap') && !result) {
            result = this._internalFields = [];
            for (const fieldName in fieldMap) {
                result.push(fieldMap[fieldName]);
            }
        }
        return result;
    }

    /**
     * Convenience getter to get field definitions from class.
     * @returns {Array}
     * @category Fields
     */
    get fields() {
        return this.constructor.internalFields || this.constructor.fields;
    }

    /**
     * Convenience function to get the definition for a field from class.
     * @param {String} fieldName Field name
     * @returns {Object}
     * @category Fields
     */
    getFieldDefinition(fieldName) {
        return this.constructor.getFieldDefinition(fieldName);
    }

    /**
     * Get the names of all fields in data.
     * @returns {String[]} Field names
     * @readonly
     * @category Fields
     */
    get fieldNames() {
        return Object.keys(this.data);
    }

    /**
     * Get the definition for a field by name. Caches results.
     * @param {String} fieldName Field name
     * @returns {Object} Field definition or null if none found
     * @category Fields
     */
    static getFieldDefinition(fieldName) {
        return this.fieldMap[fieldName];
    }

    /**
     * Get the data source used by specified field. Returns the fieldName if no data source specified.
     * @param {String} fieldName Field name
     * @returns {String}
     * @category Fields
     */
    getDataSource(fieldName) {
        const def = this.constructor.getFieldDefinition(fieldName);
        if (def) return def.dataSource || def.name;
    }

    /**
     * Processes input to a field, converting to expected type.
     * @param {String} fieldName Field dataSource
     * @param {*} value Value to process
     * @returns {*} Converted value
     * @category Fields
     */
    static processField(fieldName, value) {
        const field = this.fieldMap[fieldName];

        if (field && field.convert) {
            return field.convert(value);
        }
        return value;
    }

    //endregion

    //region Relations

    /**
     * Initializes model relations. Called from store when adding a record.
     * @private
     * @category Relations
     */
    initRelations() {
        const me        = this,
            relations = me.constructor.relations;

        if (!relations) return;

        // TODO: feels strange to have to look at the store for relation config but didn't figure out anything better.
        // TODO: because other option would be to store it on each model instance, not better...

        me.stores.forEach(store => {
            if (!store.modelRelations) store.initRelations();

            // TODO: not at all tested for multiple stores, can't imagine it works as is
            const relatedRecords = [];

            store.modelRelations && store.modelRelations.forEach(config => {
                relatedRecords.push({ related : me.initRelation(config), config });
            });
            store.updateRecordRelationCache(me, relatedRecords);
        });
    }

    /**
     * Initializes/updates a single relation.
     * @param config Relation config
     * @returns {Core.data.Model} Related record
     * @private
     * @category Relations
     */
    initRelation(config) {
        const
            me          = this,
            keyValue    = me.get(config.fieldName),
            foreign     = keyValue !== _undefined && typeof config.store !== 'string' && config.store.getById(keyValue),
            placeHolder = { id : keyValue, placeHolder : true };

        if (!me.meta.relationCache) me.meta.relationCache = {};
        // apparently scheduler tests expect cache to work without matched related record, thus the placeholder
        me.meta.relationCache[config.relationName] = foreign || (keyValue != null ? placeHolder : null);

        return foreign;
    }

    removeRelation(config) {
        // (have to check for existence before deleting to work in Safari)
        if (this.meta.relationCache[config.relationName]) {
            delete this.meta.relationCache[config.relationName];
            if (config.nullFieldOnRemove) {
                // Setting to null silently, to not trigger additional relation behaviour
                this.setData(config.fieldName, null);
            }
        }
    }

    getForeign(name) {
        return this.meta.relationCache && this.meta.relationCache[name];
    }

    setForeign(name, value, config) {
        const id = Model.asId(value);
        return this.set(config.fieldName, id);
    }

    //endregion

    //region Get/set values, data handling

    /**
     * Get value for specified field name. You can also use the generated getters if loading through a Store.
     * If model is currently in batch operation this will return updated batch values which are not applied to Model
     * until endBatch() is called.
     * @param {String} fieldName Field name to get value from
     * @returns {*} Fields value
     * @category Fields
     */
    get(fieldName) {
        const
            me         = this,
            recData    = me.meta.batchChanges ?  Object.assign({}, me.data, me.meta.batchChanges) : me.data,
            field      = me.constructor.fieldMap[fieldName],
            dataSource = field ? field.dataSource : fieldName;

        if (dataSource) {
            if (field && field.complexMapping) {
                return ObjectHelper.getPath(recData, dataSource);
            }

            return (dataSource in recData) ? recData[dataSource] : recData[fieldName];
        }
    }

    /**
     * Internal function used to update a records underlying data block (record.data) while still respecting field
     * mappings. Needed in cases where a field needs setting without triggering any associated behaviour and it has a
     * dataSource with a different name.
     *
     * For example:
     * ```javascript
     * // startDate mapped to data.beginDate
     * { name : 'startDate', dataSource : 'beginDate' }
     *
     * // Some parts of our code needs to update the data block without triggering any of the behaviour associated with
     * // calling set. This would then not update "beginDate":
     * record.data.startDate = xx;
     *
     * // But this would
     * record.setData('startDate', xx);
     * ```
     * @internal
     * @category Editing
     */
    setData(fieldName, value) {
        const field      = this.constructor.fieldMap[fieldName],
            dataSource = field ? field.dataSource : fieldName;

        if (dataSource) {
            ObjectHelper.setPath(this.data, dataSource, value);
        }
    }

    /**
     * Silently updates record's id with no flagging the property as modified.
     * Triggers onModelChange event for changed id.
     * @param {String|Number} value id value
     * @private
     */
    syncId(value) {
        const oldValue = this.id;
        if (oldValue !== value) {
            this.setData('id', value);
            const data = { id : { value, oldValue } };
            this.afterChange(data, data);
        }
    }

    /**
     * Set value for the specified field. You can also use the generated setters if loading through a Store.
     * @param {String|Object} field The field to set value for, or an object with multiple values to set in one call
     * @param {*} value Value to set
     * @param {Boolean} [silent] Set to true to not trigger events
     * @fires Store#idChange
     * @fires Store#update
     * @fires Store#change
     * @example
     * person.set('name', 'Donald');
     * @category Editing
     */
    set(field, value, silent = false, fromRelationUpdate = false) {
        const me = this;

        // We use beforeSet/inSet/afterSet approach here because mixin interested in overriding set() method
        // like STM, for example, might be mixed before Model class or after. In general I have no control over this.
        // STM mixed before, so the only option to wrap set() method body is actually to call
        // beforeSet()/afterSet().

        if (me.isBatchUpdating) {
            me.inBatchSet(field, value);
            return null;
        }
        else {
            const
                preResult = me.beforeSet ? me.beforeSet(field, value, silent, fromRelationUpdate) : _undefined,
                wasSet    = me.inSet(field, value, silent, fromRelationUpdate);
            me.afterSet && me.afterSet(field, value, silent, fromRelationUpdate, preResult, wasSet);
            return wasSet;
        }
    }

    fieldToKeys(field, value) {
        let result;
        if (typeof field !== 'string') {
            result = {};
            // will get in trouble when setting same field on multiple models without this
            Reflect.ownKeys(field).forEach(key => result[key] = field[key]);
        }
        else {
            result = {
                [field] : value
            };
        }
        return result;
    }

    inBatchSet(field, value) {
        if (typeof field !== 'string') {
            const toSet = this.fieldToKeys(field, value);
            Object.keys(toSet).forEach(key => {
                // Store batch changes
                this.meta.batchChanges[key] = this.constructor.processField(key, toSet[key]);
            });
        }
        else {
            // Minor optimization for engine writing back a lot of changes
            this.meta.batchChanges[field] = value;
        }
    }

    inSet(field, value, silent, fromRelationUpdate) {
        const
            me       = this,
            fieldMap = me.constructor.fieldMap,
            myProto  = me.constructor.prototype,
            data     = me.data,
            wasSet   = {},
            toSet    = me.fieldToKeys(field, value);
        let
            changed  = false;

        // Give a chance to cancel action before records updated.
        if (!silent && !me.triggerBeforeUpdate(toSet)) {
            return null;
        }

        Object.keys(toSet).forEach(key => {
            // Currently not allowed to set children in a TreeNode this way, will be ignored
            if (key === me.constructor.childrenField) {
                return;
            }

            const
                field    = fieldMap[key],
                readOnly = field && field.readOnly,
                mapping  = field ? field.dataSource : key,
                useProp  = !field && (key in myProto),
                oldValue = useProp ? me[mapping] : ObjectHelper.getPath(data, mapping),
                value    = me.constructor.processField(key, toSet[key]),
                val      = toSet[key] = { value },
                relation = me.getRelationConfig(key);

            if (!readOnly && !isEqual(field, oldValue, value))  {
                // Indicate to observers that data has changed.
                me.generation++;
                val.oldValue = oldValue;

                changed = true;

                // changing back to old value? remove from modified
                // `modified` should contain mapped field name, it is used in sync
                if (isEqual(field, me.meta.modified[key], value)) {
                    delete me.meta.modified[key];
                }
                else {
                    // store info on modification
                    me.meta.modified[key] = oldValue;
                    if (val.oldValue === _undefined && 'oldValue' in val) {
                        delete val.oldValue;
                    }
                }

                // The wasSet object keys must be the field *name*, not its dataSource.
                wasSet[key] = val;

                // If we don't have a field, but we have a property defined
                // eg, the fullDuration property defined in TaskModel, then
                // use the property
                if (useProp) {
                    me[key] = value;
                }
                // Otherwise, push the value through into the data.
                else {
                    ObjectHelper.setPath(data, mapping, value);
                }

                // changing foreign key
                if (relation && !fromRelationUpdate) {
                    me.initRelation(relation);
                    me.stores.forEach(store => store.cacheRelatedRecord(me, value, relation.relationName, val.oldValue));
                }
            }
            else {
                delete toSet[key];
            }
        });

        if (changed) {
            me.afterChange(toSet, wasSet, silent, fromRelationUpdate);
        }

        return changed ? wasSet : null;
    }

    afterChange(toSet, wasSet, silent, fromRelationUpdate) {
        this.stores.forEach(store => {
            store.onModelChange(this, toSet, wasSet, silent, fromRelationUpdate);
        });
    }

    get isPersistable() {
        return true;
    }

    /**
     * True if this model has any uncommitted changes.
     * @property {Boolean}
     * @readonly
     * @category Editing
     */
    get isModified() {
        return Boolean(this.meta.modified && Object.keys(this.meta.modified).length > 0);
    }

    /**
     * Returns true if this model has uncommitted changes for the provided field.
     * @param {String} fieldName Field name
     * @returns {Boolean} True if the field is changed
     */
    isFieldModified(fieldName) {
        return this.isModified && this.meta.modified[fieldName];
    }

    /**
     * Returns field value that should be persisted, or `undefined` if field is configured with `persist: false`.
     * @param {String} name Name of the field to get value
     * @private
     * @category Fields
     */
    getFieldPersistentValue(name) {
        const field = this.getFieldDefinition(name);
        let result;

        if (!field || field.persist) {
            result = this[name];
            // if serialize function is provided we use it to prepare the persistent value
            if (field && field.serialize) {
                result = field.serialize.call(this, result, this);
            }
        }

        return result;
    }

    /**
     * Get a map of the modified fields in form of an object. The field *names* are used as the property names
     * in the returned object.
     * @property {Object}
     * @readonly
     * @category Editing
     */
    get modifications() {
        const me = this;

        if (!me.isModified) {
            return null;
        }

        const data = {};
        Object.keys(me.meta.modified).forEach(key => {
            // TODO: isModified will report record as modified even if a modification wont be persisted here. Should it?
            const value = me.getFieldPersistentValue(key);
            if (value !== _undefined) {
                data[key] = value;
            }
        });
        data[me.constructor.idField] = me.id;

        return data;
    }

    /**
     * Get a map of the modified fields in form of an object. The field *dataSources* are used as the property names
     * in the returned object.
     * @property {Object}
     * @readonly
     * @category Editing
     */
    get modificationData() {
        const
            me = this,
            { fieldMap } = me.constructor;

        if (!me.isModified) {
            return null;
        }

        const data = {};
        Object.keys(me.meta.modified).forEach(fieldName => {
            // TODO: isModified will report record as modified even if a modification wont be persisted here. Should it?
            const field = fieldMap[fieldName];

            // No field definition means there's no original dataSource to update
            if (field) {
                const value = me.getFieldPersistentValue(fieldName);

                if (value !== _undefined) {
                    data[field.dataSource] = value;
                }
            }
        });
        data[me.constructor.idField] = me.id;

        return data;
    }

    /**
     * Get persistable data in form of an object.
     * @property {Object}
     * @internal
     * @readonly
     * @category Editing
     */
    get persistableData() {
        const
            me = this,
            data = {};

        Object.keys(me.data).forEach(key => {
            const value = me.getFieldPersistentValue(key);
            if (value !== _undefined) {
                data[key] = value;
            }
        });

        return data;
    }

    /**
     * True if this models changes are currently being committed.
     * @property {boolean}
     * @category Editing
     */
    get isCommitting() {
        return Boolean(this.meta.committing);
    }

    /**
     * Clear stored changes, used on commit. Does not revert changes.
     * @param {Boolean} [removeFromStoreChanges] Update related stores modified collection or not
     * @param {Boolean} [includeDescendants] Set true to clear store descendants
     * @category Editing
     * @private
     */
    clearChanges(removeFromStoreChanges = true, includeDescendants = true) {
        const me = this,
            { meta } = me;

        meta.modified = {};
        meta.committing = false;

        if (removeFromStoreChanges) {
            me.stores.forEach(store => {
                store.modified.remove(me);
                store.added.remove(me);
                if (includeDescendants) {
                    const descendants = store.collectDescendants(me).all;
                    store.added.remove(descendants);
                    store.modified.remove(descendants);
                }
            });
        }
    }

    //endregion

    //region Id

    /**
     * Gets the records internalId. It is assigned during creation, guaranteed to be globally unique among models.
     * @property {Number}
     * @category Identification
     */
    get internalId() {
        return this._internalId;
    }

    /**
     * Returns true if the record is new and has not been persisted (and received a proper id).
     * @property {Boolean}
     * @readonly
     * @category Identification
     */
    get isPhantom() {
        return this.id === '' || this.id == null || this.hasGeneratedId;
    }

    get isModel() {
        return true;
    }

    /**
     * Checks if record has a generated id. New records are assigned a generated id (starting with _generated), which should be
     * replaced on commit.
     * @property {Boolean}
     * @category Identification
     */
    get hasGeneratedId() {
        return this.id && typeof this.id === 'string' && this.id.startsWith('_generated');
    }

    /**
     * Generates id for new record which starts with _generated.
     * @category Identification
     */
    generateId() {
        if (!this.constructor.generatedIdIndex) this.constructor.generatedIdIndex = 0;
        return '_generated' + this.$name + (++this.constructor.generatedIdIndex);
    }

    /**
     * Gets the id of specified model or the value if passed string/Number.
     * @param {Core.data.Model|String|Number} model
     * @returns {String|Number} id
     * @category Identification
     */
    static asId(model) {
        return model && model.isModel ? model.id : model;
    }

    //endregion

    //region JSON

    /**
     * Get the records data as a json string.
     * @member {String}
     * @category Misc
     */
    get json() {
        return JSON.stringify(this.data);
    }

    /**
     * Used by JSON.stringify to correctly convert this record to json. No point in calling it directly.
     * @private
     * @category Misc
     */
    toJSON() {
        return this.data;
    }

    //endregion

    //region Batch

    /**
     * True if this Model is currently batching its changes.
     * @property {Boolean}
     * @readonly
     * @category Editing
     */
    get isBatchUpdating() {
        return Boolean(this.batching);
    }

    /**
     * Begin a batch, which stores changes and commits them when the batch ends.
     * Prevents events from being fired during batch.
     * ```
     * record.beginBatch();
     * record.name = 'Mr Smith';
     * record.team = 'Golden Knights';
     * record.endBatch();
     * ```
     * Please note that you can also set multiple fields in a single call using {@link #function-set}, which in many
     * cases can replace using a batch:
     * ```
     * record.set({
     *   name : 'Mr Smith',
     *   team : 'Golden Knights'
     * });
     * ```
     * @category Editing
     */
    beginBatch() {
        const me = this;
        if (!me.batching) {
            me.batching = 0;
            me.meta.batchChanges = {};
        }
        me.batching++;
    }

    /**
     * End a batch, triggering events if data has changed.
     * @param {Boolean} [silent] Specify `true` to not trigger events
     * @category Editing
    */
    endBatch(silent = false) {
        const
            me = this,
            { parentIdField } = me.constructor;

        if (!me.batching) {
            return;
        }

        me.batching--;

        if (me.batching > 0) {
            return;
        }

        // Set pending batch changes
        if (!ObjectHelper.isEmpty(me.meta.batchChanges)) {
            const batchChanges = Object.assign({}, me.meta.batchChanges);
            me.meta.batchChanges = null;

            // Move to its new parent before applying the other changes.
            if (batchChanges[parentIdField]) {
                me.parentId = batchChanges[parentIdField];
                delete batchChanges[parentIdField];
            }

            me.set(batchChanges, _undefined, silent);
            me.cancelBatch();
        }
    }

    /**
     * Cancels current batch operation. Any changes during the batch are discarded.
     * @category Editing
     */
    cancelBatch() {
        this.batching = null;
        this.meta.batchChanges = null;
    }

    //endregion

    //region Events

    /**
     * Triggers beforeUpdate event for each store and checks if changes can be made from event return value.
     * @param {Object} changes Data changes
     * @returns {Boolean} returns true if data changes are accepted
     * @private
     */
    triggerBeforeUpdate(changes) {
        return !this.stores.some(s => {
            if (s.trigger('beforeUpdate', { record : this, changes }) === false) {
                return true;
            }
        });
    }

    //endregion

    //region Additional functionality

    /**
     * Makes a copy of this model, assigning the specified id or a generated id.
     * @param {Number|String|Object} [newId] Id to set (or config object), leave out to use generated id or specify false to also copy id
     * @param {Number|String} [newId.id] Id to set, leave out to use generated id or specify false to also copy id
     * @param {Boolean} [newId.deep] True to also clone children
     * @returns {Core.data.Model} Copy of this model
     * @category Editing
     */
    copy(newId = null) {
        const
            me      = this,
            data    = Object.assign({}, me.data),
            idField = me.constructor.idField;

        let returnInstance = true,
            deep, id, copy;

        if (newId && typeof newId === 'object') {
            deep = newId.deep;
            id   = newId.id;

            // Only use id once to avoid collisions
            delete newId.id;
        }
        else {
            id = newId;
        }

        // Iterate over instance children, because data may not reflect actual children state
        if (deep && me.children) {
            returnInstance = false;
            data.children  = me.children.map(child => child.copy(newId));
        }
        else {
            delete data.children;
            delete data.expanded;
        }

        if (id) {
            data[idField] = id;
        }
        else if (id == null) {
            data[idField] = me.generateId(me.firstStore);
        }

        if (returnInstance) {
            copy = new me.constructor(data);
        }
        else {
            copy = data;
        }

        // Store original record internal id to lookup from copy later
        copy.originalInternalId = me.internalId;

        return copy;
    }

    /**
     * Removes this record from all stores (and in a tree structure, also from its parent if it has one).
     * @param {Boolean} [silent] Specify `true` to not trigger events
     * @category Editing
     */
    remove(silent = false) {
        const me = this,
            { parent } = this;

        // Remove from parent if we're in a tree structure.
        // This informs the owning store(s)
        if (parent) {
            parent.removeChild(me);
        }
        // Store handles remove
        else if (me.stores.length) {
            // Not sure what should happen if you try to remove a special row (group row for example), bailing out
            if (!me.meta.specialRow) {
                me.stores.forEach(s => s.remove(me, silent, false, true));
            }
        }
    }

    /**
     * Get the first store that this model is assigned to.
     * @returns {Core.data.Store}
     * @category Misc
     */
    get firstStore() {
        return this.stores.length > 0 && this.stores[0];
    }

    /**
     * Get a relation config by name, from the first store.
     * @param {String} name
     * @returns {Object}
     * @private
     * @category Relations
     */
    getRelationConfig(name) {
        // using first store for relations, might have to revise later..
        return this.firstStore && this.firstStore.modelRelations && this.firstStore.modelRelations.find(r => r.fieldName === name);
    }

    //endregion

    //region Validation

    /**
     * Check if record has valid data. Default implementation returns true, override in your model to do actual validation.
     * @returns {Boolean}
     * @category Editing
     */
    get isValid() {
        return true;
    }

    //endregion

    //region Store

    /**
     * Joins this record and any children to specified store, if not already joined.
     * @internal
     * @param {Core.data.Store} store Store to join
     * @category Misc
     */
    joinStore(store) {
        const me = this,
            { stores, unjoinedStores } = me;

        if (!stores.includes(store)) {
            super.joinStore && super.joinStore(store);
            store.register(me);
            stores.push(store);
            if (unjoinedStores.includes(store)) {
                unjoinedStores.splice(unjoinedStores.indexOf(store), 1);
            }
            me.isLoaded && me.children.forEach(child => child.joinStore(store));
            me.initRelations();
        }
    }

    /**
     * Unjoins this record and any children from specified store, if already joined.
     * @internal
     * @param {Core.data.Store} store Store to join
     * @category Misc
     */
    unJoinStore(store) {
        const me = this,
            { stores, unjoinedStores } = me;

        if (stores.includes(store)) {
            store.unregister(me);
            me.children && me.children.forEach(child => child.unJoinStore(store));
            stores.splice(stores.indexOf(store), 1);
            // keep the cord to allow removed records to reach the store when needed
            unjoinedStores.push(store);
            super.unJoinStore && super.unJoinStore(store);

            // remove from relation cache
            store.uncacheRelatedRecord(me);
        }
        if (!stores.length) {
            me.meta.removed = true;
        }
    }

    /**
     * Returns true if this record is contained in the specified store, or in any store if store param is omitted.
     * @internal
     * @param {Core.data.Store} store Store to join
     * @returns {Boolean}
     * @category Misc
     */
    isPartOfStore(store) {
        if (store) {
            return store.indexOf(this) >= 0;
        }

        return this.stores.length > 0;
    }
    //endregion

    //region Per instance meta

    /**
     * Used to set per external instance meta data. For example useful when using a record in multiple grids to store some state
     * per grid.
     * @param {String|Object} instanceOrId External instance id or the instance itself, if it has id property
     * @private
     * @category Misc
     */
    instanceMeta(instanceOrId) {
        const { meta } = this,
            id       = instanceOrId.id || instanceOrId;
        if (!meta.map) meta.map = {};
        return meta.map[id] || (meta.map[id] = {});
    }

    //endregion
}

Model._idField = 'id';
Model._internalIdCounter = 1;
Model._assignedIdField = false;

Model.exposeProperties();
