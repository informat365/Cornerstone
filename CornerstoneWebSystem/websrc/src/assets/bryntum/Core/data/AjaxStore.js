import Store from './Store.js';
import AjaxHelper from '../helper/AjaxHelper.js';

/**
 * @module Core/data/AjaxStore
 */

const processParamEntry = (paramArray, entry) => {
        if (Array.isArray(entry[1])) {
            entry[1].forEach(value => paramArray.push(entry[0] + '=' + value));
        }
        else {
            paramArray.push(entry[0] + '=' + entry[1]);
        }
        return paramArray;
    },
    immediatePromise  = new Promise((resolve) => resolve());

/**
 * Store that uses the Fetch API to read data from a remote server, and optionally sends synchronization
 * requests to the server containing information about locally created, modified and deleted records.
 *
 * ### Create
 * Posts array of JSON data for newly added records to {@link #config-createUrl}, expects response containing an array of JSON objects
 * in same order with id set (uses Model#idField as id).
 *
 * ### Read
 * Reads array of JSON data from the data packet returned from the {@link #config-readUrl}. Unique id for each row is required.
 * By default looks in field 'id' but can be configured by setting {@link Core.data.Model#property-idField-static}.
 *
 * ### Update
 * Posts array of JSON data for newly modified records to {@link #config-updateUrl}.
 *
 * ### Destroy
 * Posts to {@link #config-deleteUrl} with removed records ids (for example id=1,4,7).
 *
 * ```javascript
 * new AjaxStore({
 *   createUrl  : 'php/create',
 *   readUrl    : 'php/read',
 *   updateUrl  : 'php/update',
 *   deleteUrl  : 'php/delete',
 *   modelClass : Customer
 * });
 * ```
 *
 * ### Pagination
 * Configuring an `AjaxStore` with {@link #config-pageParamName} or {@link #config-pageStartParamName} means that the store requests **pages**
 * of data from the remote source, sending the configured {@link #config-pageParamName} or {@link #config-pageStartParamName} to request the page
 * along with the {@link #config-pageSizeParamName}.
 *
 * If `pageParamName` is set, that is passed with the requested page number **(one based)**, along with the {@link #config-pageSizeParamName}.
 *
 * If `pageStartParamName` is set, that is passed with the requested page starting record index **(zero based)**, along with the {@link #config-pageSizeParamName}.
 *
 * ### Remote filtering
 * To specify that filtering is the responsibility of the server, configure the store with
 * `{@link #config-filterParamName}: 'nameOfFilterParameter'`
 *
 * When this is set, any {@link Core.data.mixin.StoreFilter#function-filter} operation causes the store to
 * reload itself, encoding the filters as JSON representations in the {@link #config-filterParamName} HTTP
 * parameter.
 *
 * The filters will look like this:
 * ```javascript
 * {
 *     "field": "country",
 *     "operator": "=",
 *     "value": "sweden",
 *     "caseSensitive": false
 * }
 * ```
 *
 * The encoding may be overridden by configuring an implementation of {@link #function-encodeFilterParams}
 * into the store which returns the value for the {@link #config-filterParamName} when passed an _Iterable_ of filters.
 *
 * ### Remote sorting
 * To specify that sorting is the responsibility of the server, configure the store with
 * `{@link #config-sortParamName}: 'nameOfSortParameter'`
 *
 * When this is set, any {@link Core.data.mixin.StoreSort#function-sort} operation causes the store to
 * reload itself, encoding the sorters as JSON representations in the {@link #config-sortParamName} HTTP
 * parameter.
 *
 * The sorters will look like this:
 * ```javascript
 * {
 *     "field": "name",
 *     "ascending": true
 * }
 * ```
 *
 * The encoding may be overridden by configuring an implementation of {@link #function-encodeSorterParams}
 * into the store which returns the value for the {@link #config-sortParamName} when passed an _Iterable_ of sorters.
 *
 * @extends Core/data/Store
 */
export default class AjaxStore extends Store {
    // region Events

    /**
     * Fired when a remote request fails, either at the network level, or the server returns a failure, or an invalid response.
     *
     * Note that when a {@link #function-commit} fails, more than one exception event will be triggered. The individual operation,
     * `create`, `update` or `delete` will trigger their own `exception` event, but the encapsulating commit operation will also
     * trigger an `exception` event when all the operations have finished, so if exceptions are going to be handled gracefully,
     * the event's `action` property must be examined, and the constituent operations of the event must be examined.
     * @event exception
     * @param {Core.data.Store} source This Store
     * @param {Boolean} exception `true`
     * @param {String} action Action that failed, `'create'`, `'read'`, `'update'` or `'delete'`. May also be fired
     * with '`commit'` to indicate the failure of an aggregated `create`, `update` and `delete` operation. In this case,
     * the event will contain a property for each operation of the commit named `'create'`, `'update'` and `'delete'`,
     * each containing the individual `exception` events.
     * @param {String} exceptionType The type of failure, `'network'` or `'server'`
     * @param {Response} response the `Response` object
     * @param {Object} json The decoded response object *if the exceptionType is `'server'`*
     */

    /**
     * Fired after committing added records
     * @event commitAdded
     * @param {Core.data.Store} source This Store
     */

    /**
     * Fired after committing modified records
     * @event commitModified
     * @param {Core.data.Store} source This Store
     */

    /**
     * Fired on successful load
     * @event load
     * @param {Core.data.Store} source This Store
     * @param {Object[]} data Data loaded
     * @param {Response} response the `Response` object
     * @param {Object} json The decoded response object.
     */

    /**
     * Fired on successful load of remote child nodes for a tree node.
     * @event loadChildren
     * @param {Core.data.Store} source This Store
     * @param {Object[]} data Data loaded
     * @param {Object} json The decoded response object.
     */

    /**
     * Fired after committing removed records
     * @event commitRemoved
     * @param {Core.data.Store} source This Store
     */

    /**
     * Fired before loading starts. Allows altering parameters and is cancelable
     * @event beforeLoad
     * @preventable
     * @param {Core.data.Store} source This Store
     * @param {Object} params An object containing property/name pairs which are the parameters.
     * This may be mutated to affect the parameters used in the Ajax request.
     */

    /**
     * Fired before loading of remote child nodes of a tree node starts. Allows altering parameters and is cancelable
     * @event beforeLoadChildren
     * @preventable
     * @param {Core.data.Store} source This Store
     * @param {Object} params An object containing property/name pairs which are the parameters.
     * This may be mutated to affect the parameters used in the Ajax request.
     */

    /**
     * When the store {@link #property-isPaged is paged}, this is fired before loading a page and is cancelable
     * @event beforeLoadPage
     * @preventable
     * @param {Core.data.Store} source This Store
     * @param {Object} params An object containing property/name pairs which are the parameters.
     * This may be mutated to affect the parameters used in the Ajax request.
     */

    /**
     * Fired when loading is beginning. This is not cancelable. Parameters in the event may still be
     * mutated at this stage.
     * @event loadStart
     * @param {Core.data.Store} source This Store
     * @param {Object} params An object containing property/name pairs which are the parameters.
     * This may be mutated to affect the parameters used in the Ajax request.
     */

    /**
     * Fired when loading of remote child nodes into a tree node is beginning. This is not cancelable. Parameters in the event may still be
     * mutated at this stage.
     * @event loadChildrenStart
     * @param {Core.data.Store} source This Store
     * @param {Object} params An object containing property/name pairs which are the parameters.
     * This may be mutated to affect the parameters used in the Ajax request.
     */

    /**
     * Fired before any remote request is initiated.
     * @event beforeRequest
     * @param {Core.data.Store} source This Store
     * @param {Object} params An object containing property/name pairs which are the parameters.
     * @param {Object} body The body of the request to be posted to the server.
     * @param {String} action Action that is making the request, `'create'`, `'read'`, `'update'` or `'delete'`
     */

    /**
     * Fired after any remote request has finished whether successfully or unsuccessfully.
     * @event afterRequest
     * @param {Boolean} exception `true`. *Only present if the request triggered an exception.*
     * @param {String} action Action that has finished, `'create'`, `'read'`, `'update'` or `'delete'`
     * @param {String} exceptionType The type of failure, `'network'` or `'server'`. *Only present if the request triggered an exception.*
     * @param {Response} response The `Response` object
     * @param {Object} json The decoded response object if there was no `'network'` exception.
     */

    // endregion

    //region Config

    static get defaultConfig() {
        return {
            /**
             * An object containing the HTTP headers to add to each server request issued by this Store.
             * @config {Object}
             * @default
             */
            headers : null,

            /**
             * An object containing the Fetch options to pass to each server request issued by this Store. Use this to control if credentials are sent
             * and other options, read more at [MDN](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch).
             * @config {Object}
             * @default
             */
            fetchOptions : null,

            /**
             * Specify `true` to send payloads as form data, `false` to send as regular JSON.
             * @config {Boolean}
             * @default
             */
            sendAsFormData : false,

            /**
             * Specify `true` to send all model fields when committing modified records (as opposed to just the modified fields)
             * @config {Boolean}
             * @default
             */
            writeAllFields : false,

            /**
             * The name of the HTTP parameter passed to this Store's {@link #config-readUrl} to indicate the node `id` to
             * load when loading child nodes on demand if the node being expanded was created with data containing `children: true`.
             * @config {String}
             * @default
             */
            parentIdParamName : 'id',

            /**
             * The property name in JSON responses from the server that contains the data for the records
             * ```
             * {
             *   "success" : true,
             *   // The property name used here should match that of 'reponseDataProperty'
             *   "data" : [
             *     ...
             *   ]
             * }
             * ```
             * @config {String}
             * @default
             */
            responseDataProperty : 'data',

            /**
             * The property name in JSON responses from the server that contains the dataset total size
             * **when this store {@link #property-isPaged is paged}**
             * ```
             * {
             *   "success" : true,
             *   // The property name used here should match that of 'reponseDataProperty'
             *   "data" : [
             *     ...
             *   ],
             *   // The property name used here should match that of 'responseTotalProperty'
             *   "total" : 65535
             * }
             * ```
             * @config {String}
             * @default
             */
            responseTotalProperty : 'total',

            /**
             * The name of the HTTP parameter to use to pass any encoded filters when loading data from the server and a filtered response is required.
             *
             * **Note:** When this is set, filters must be defined using a field name, an operator and a value
             * to compare, **not** a comparison function.
             * @config {String}
             */
            filterParamName : null,

            /**
             * The name of the HTTP parameter to use to pass any encoded sorters when loading data from the server and a sorted response is required.
             *
             * **Note:** When this is set, sorters must be defined using a field name and an ascending flag,
             * **not** a sort function.
             * @config {String}
             */
            sortParamName : null,

            /**
             * The name of the HTTP parameter to use when requesting pages of data using the **one based** page number required.
             * @config {String}
             */
            pageParamName : null,

            /**
             * The name of the HTTP parameter to use when requesting pages of data using the **zero based** index of the required page's starting record.
             * @config {String}
             */
            pageStartParamName : null,

            /**
             * The name of the HTTP parameter to use when requesting pages of data using the **zero based** index of the required page's starting record.
             * @config {String}
             * @default
             */
            pageSizeParamName : 'pageSize',

            /**
             * When paging of data is requested by setting _either_ the {@link #config-pageParamName} _or_ the {@link #config-pageStartParamName},
             * this is the value to send in the {@link #config-pageSizeParamName}
             * @config {String}
             * @default
             */
            pageSize : 50
        };
    }

    /**
     * Url to post newly created records to.
     *
     * The response must be in the form:
     *
     *     {
     *         "success": true,
     *         "data": [{
     *             "id": 0, "name": "General Motors"
     *         }, {
     *             "id": 1, "name": "Apple"
     *         }]
     *     }
     *
     * Just the array of data may be returned, however that precludes the
     * orderly handling of errors encountered at the server.
     *
     * If the server encountered an error, the packet would look like this:
     *
     *     {
     *         "success": false,
     *         "message": "Some kind of database error"
     *     }
     *
     * And that packet would be available in the {@link #event-exception} handler
     * in the `response` property of the event.
     *
     * The `success` property may be ommitted, it defaults to `true`.
     *
     * @config {String} createUrl
     * @category CRUD
     */

    /**
     * Url to read data from.
     *
     * The response must be in the form:
     *
     *     {
     *         "success": true,
     *         "data": [{
     *             "id": 0, "name": "General Motors"
     *         }, {
     *             "id": 1, "name": "Apple"
     *         }]
     *     }
     *
     * If the store {@link #property-isPaged is paged}, the total dataset size must be returned
     * in the {@link #config-responseTotalProperty} property:
     *
     *     {
     *         "success": true,
     *         "data": [{
     *             "id": 0, "name": "General Motors"
     *         }, {
     *             "id": 1, "name": "Apple"
     *         }],
     *         "total": 65535
     *     }
     *
     * Just the array of data may be returned, however that precludes the
     * orderly handling of errors encountered at the server.
     *
     * If the server encountered an error, the packet would look like this:
     *
     *     {
     *         "success": false,
     *         "message": "Some kind of database error"
     *     }
     *
     * And that packet would be available in the {@link #event-exception} handler
     * in the `response` property of the event.
     *
     * The `success` property may be omitted, it defaults to `true`.
     *
     * @config {String} readUrl
     * @category CRUD
     */

    /**
     * Url to post record modifications to.
     *
     * The response must be in the form:
     *
     *     {
     *         "success": true,
     *         "data": [{
     *             "id": 0, "name": "General Motors"
     *         }, {
     *             "id": 1, "name": "Apple"
     *         }]
     *     }
     *
     * Just the array of data may be returned, however that precludes the
     * orderly handling of errors encountered at the server.
     *
     * If the server encountered an error, the packet would look like this:
     *
     *     {
     *         "success": false,
     *         "message": "Some kind of database error"
     *     }
     *
     * And that packet would be available in the {@link #event-exception} handler
     * in the `response` property of the event.
     *
     * The `success` property may be ommitted, it defaults to `true`.
     *
     * @config {String} updateUrl
     * @category CRUD
     */

    /**
     * Url for destroying records.
     *
     * The response must be in the form:
     *
     *     {
     *         "success": true
     *     }
     *
     * If the server encountered an error, the packet would look like this:
     *
     *     {
     *         "success": false,
     *         "message": "Some kind of database error"
     *     }
     *
     * And that packet would be available in the {@link #event-exception} handler
     * in the `response` property of the event.
     *
     * The `success` property may be ommitted, it defaults to `true`.
     *
     * @config {String} deleteUrl
     * @category CRUD
     */

    /**
     * True to initiate a load when the store is instantiated
     * @config {Boolean} autoLoad
     * @category Common
     */

    //endregion

    afterConstruct(config) {
        super.afterConstruct(config);

        if (this.autoLoad) {
            this.load().catch(() => {});
        }
    }

    /**
     * Returns true if the Store is currently loading
     * @property {Boolean}
     * @readonly
     * @category CRUD
     */
    get isLoading() {
        return this._isLoading;
    }

    /**
     * Returns true if the Store is currently committing
     * @property {Boolean}
     * @readonly
     * @category CRUD
     */
    get isCommitting() {
        return Boolean(this.commitPromise);
    }

    set pageParamName(pageParamName) {
        if (this.tree) {
            throw new Error('Paging cannot be supported for tree stores');
        }
        if (this.pageStartParamName) {
            throw new Error('Configs pageStartParamName and pageParamName are mutually exclusive');
        }
        this._pageParamName = pageParamName;
    }

    get pageParamName() {
        return this._pageParamName;
    }

    set pageStartParamName(pageStartParamName) {
        if (this.tree) {
            throw new Error('Paging cannot be supported for tree stores');
        }
        if (this.pageParamName) {
            throw new Error('Configs pageParamName and pageStartParamName are mutually exclusive');
        }
        this._pageStartParamName = pageStartParamName;
    }

    get pageStartParamName() {
        return this._pageStartParamName;
    }

    /**
     * Yields true if this Store is loaded page by page. This yields `true` if either of the {@link #config-pageParamName}
     * of {@link #config-pageStartParamName} configs are set.
     * @property {Boolean}
     * @readonly
     */
    get isPaged() {
        return this.pageParamName || this.pageStartParamName;
    }

    /**
     * Yields the complete dataset size. If the store is {@link #property-isPaged is paged} this is the value
     * returned in the last loaded data block in the {@link #config-responseTotalProperty} property. Otherwise it is
     * the number of records in the store's underlying storage collection.
     * @property {Number}
     * @readonly
     */
    get allCount() {
        return ('remoteTotal' in this) ? this.remoteTotal : super.allCount;
    }

    /**
     * **If the store {@link #property-isPaged is paged}**, yields the highest page number in the dataset as calculated from the {@link #config-responseTotalProperty}
     * returned in the last page data block loaded.
     * @property {Number}
     * @readonly
     */
    get lastPage() {
        if (this.isPaged) {
            return Math.floor((this.allCount + this.pageSize - 1) / this.pageSize);
        }
    }

    buildQueryString(...paramObjects) {
        const queryString = Object.entries(Object.assign({}, ...paramObjects)).reduce(processParamEntry, []).join('&');

        return queryString ? '?' + queryString : '';
    }

    performSort(silent) {
        const me = this;

        if (me.sortParamName) {
            const
                { groupRecords } = me,
                // Current collapsed state for groups
                collapsed = {};

            // Temporarily remove group headers, will be re-added after sort
            if (me.isGrouped && groupRecords && groupRecords.length) {
                groupRecords.forEach(r => {
                    if (r.meta.collapsed) {
                        me.includeGroupRecords(r);
                        collapsed[r.id] = true;
                    }
                });
            }
            // TODO: Should we cache lastParams from the load call and use them here?
            return me.internalLoad({}, '', event => {
                me.data = event.data;
                me.afterPerformSort(silent, collapsed);
            });
        }
        else {
            super.performSort(silent);
        }
    }

    performFilter(silent) {
        const me = this;

        // For remote filtering, the dataset cannot be preserved. The size may be completely different.
        // This is a reload operation.
        if (me.filterParamName) {
            const
                oldCount = me.count,
                { filters } = me;

            // load should default to page 1
            me.currentPage = 1;

            // TODO: Should we cache lastParams from the load call and use them here?
            return me.internalLoad({}, '', event => {
                me.data = event.data;
                me.afterPerformFilter(silent ? null : {
                    action  : 'filter',
                    filters,
                    oldCount,
                    records : me.storage.values
                });
            });
        }
        else {
            super.performFilter();
        }
    }

    /**
     * A provided function which creates an array of values for the {#config-filterParamName} to pass
     * any filters to the server upon load.
     *
     * By default, this creates a JSON string containing the following properties:
     *
     * ```javascript
     *    [{
     *        field         : <theFieldName>
     *        operator      : May be: `'='`, `'!='`, `'>'`, `'>='`, `'<'`, `'<='`, `'*'`, `'startsWith'`, `'endsWith'`
     *        value         : The value to compare
     *        caseSensitive : true for case sensitive comparisons
     *    }]
     * ```
     * @param {Core.util.CollectionFilter[]} filters The filters to encode.
     */
    encodeFilterParams(filters) {
        const result = [];

        for (const { property, operator, value, caseSensitive } of filters) {
            result.push({
                field : property,
                operator,
                value,
                caseSensitive
            });
        }
        return  JSON.stringify(result);
    }

    /**
     * A provided function which creates an array of values for the {#config-sortParamName} to pass
     * any sorters to the server upon load.
     *
     * By default, this creates a JSON string containing the following properties:
     *
     * ```javascript
     *    [{
     *        field     : <theFieldName>
     *        ascending : true/false
     *    }]
     * ```
     *
     * @param {Object[]} sorters The sorters to encode.
     */
    encodeSorterParams(sorters) {
        return JSON.stringify(sorters.map(sorter => sorter));
    }

    internalLoad(params, eventName, successFn) {
        const
            me        = this,
            allParams = Object.assign({}, me.params, params),
            event     = { action : 'read' + eventName, params : allParams },
            result    = me.readUrl ? new Promise((resolve, reject) => {
                if (me.trigger('beforeLoad' + eventName, event) === false) {
                      return reject(false); // eslint-disable-line
                }

                me._isLoading = true;

                // This may look redundant, but it allows for two levels of event listening.
                // Granular, where the observer observes only the events of interest, and
                // catch-all, where the observer is interested in all requests.
                me.trigger(`load${eventName}Start`, event);
                me.trigger('beforeRequest', event);

                // Add filter information to the request parameters
                if (me.filterParamName && me.isFiltered) {
                    allParams[me.filterParamName] = me.encodeFilterParams(me.filters.values);
                }

                // Add sorter information to the request parameters.
                // isSorted includes grouping in its evaluation.
                if (me.sortParamName && me.isSorted) {
                    allParams[me.sortParamName] = me.encodeSorterParams(me.groupers ? me.groupers.concat(me.sorters) : me.sorters);
                }

                // Ensure our next page is passed to the server in the params if not already set.
                // Ensure our page size is always passed.
                if (me.isPaged) {
                    if (!(allParams[me.pageParamName] || allParams[me.pageStartParamName])) {
                        const page = Math.min(me.currentPage || 1, me.allCount ? me.lastPage : Infinity);

                        if (me.pageParamName) {
                            allParams[me.pageParamName] = page;
                        }
                        else {
                            allParams[me.pageStartParamName] = (page - 1) * me.pageSize;
                        }
                    }
                    allParams[me.pageSizeParamName] = me.pageSize;
                }

                AjaxHelper.get(me.readUrl + me.buildQueryString(allParams), Object.assign({ headers : me.headers, parseJson : true }, me.fetchOptions))
                    .then((response) => {
                        const
                            data = response.parsedJson,
                            isArray = Array.isArray(data),
                            success = isArray || (data && (data.success !== false));

                        me._isLoading = false;
                        event.response = response;
                        event.json    = data;

                        if (success) {
                            if (me.responseTotalProperty in data) {
                                me.remoteTotal = parseInt(data[me.responseTotalProperty], 10);
                            }

                            // If we are issuing paged requests, work out what page we are on based
                            // on the requested page and the size of the dataset declared.
                            if (me.isPaged) {
                                if (me.remoteTotal >= 0) {
                                    const requestedPage = me.pageParamName ? allParams[me.pageParamName] : allParams[me.pageStartParamName] / me.pageSize + 1;

                                    me.currentPage = Math.min(requestedPage, me.lastPage);
                                }
                                else {
                                    throw new Error('A paged store must receive its responseTotalProperty in each data packet');
                                }
                            }
                            event.data = isArray ? data : data[me.responseDataProperty];
                            successFn(event);
                            me.trigger('load' + eventName, event);
                            resolve(event);
                        }
                        else {
                            event.exception     = true;

                            event.exceptionType = 'server';
                            me.trigger('exception', event);
                            reject(event);
                        }

                        // finally
                        me.trigger('afterRequest', event);
                    }).catch(responseOrError => {
                        me._isLoading = false;

                        event.exception = true;

                        if (responseOrError instanceof Response) {
                            event.exceptionType = responseOrError.ok ? 'server' : 'network';
                            event.response = responseOrError;
                            event.error = responseOrError.error;
                        }
                        else {
                            event.exceptionType = 'server';
                            event.error = responseOrError;
                        }

                        me.trigger('exception', event);
                        reject(event);

                        // finally
                        me.trigger('afterRequest', event);
                    });
            }) : null;

        return result;
    }

    /**
     * Load data from the {@link #config-readUrl}.
     * @param {Object} params A hash of parameters to append to querystring (will also append Store#params)
     * @returns {Promise} A Promise which will be resolved if the load succeeds, and rejected if the load is
     * vetoed by a {@link #event-beforeLoad} handler, or if an {@link #event-exception} is detected.
     * The resolved function is passed the event object passed to any event handlers.
     * The rejected function is passed the {@link #event-exception} event if an exception occurred,
     * or `false` if the load was vetoed by a {@link #event-beforeLoad} handler.
     * @fires beforeLoad
     * @fires loadStart
     * @fires beforeRequest
     * @fires load
     * @fires exception
     * @fires afterRequest
     * @category CRUD
     * @async
     * @returns {Promise} A promise which is resolved when the Ajax request completes and has been processed.
     */
    load(params) {
        const me = this;

        if (me.isPaged) {
            return me.loadPage((me.currentPage || 0) + 1, params);
        }
        else {
            return me.internalLoad(params, '', (event) => {
                // The set Data setter will trigger the refresh event with { action: 'dataset' }
                me.data = event.data;
            });
        }
    }

    /**
     * Loads children into specified parent record. Parent records id is sent as a param (param name configured with
     * {@link #config-parentIdParamName}.
     * @param {Core.data.Model} parentRecord Parent record
     * @returns {Promise} A Promise which will be resolved if the load succeeds, and rejected if the load is
     * vetoed by a {@link #event-beforeLoadChildren} handler, or if an {@link #event-exception} is detected.
     * The resolved function is passed the event object passed to any event handlers.
     * The rejected function is passed the {@link #event-exception} event if an exception occurred,
     * or `false` if the load was vetoed by a {@link #event-beforeLoadChildren} handler.
     * @fires beforeLoadChildren
     * @fires loadChildrenStart
     * @fires beforeRequest
     * @fires loadChildren
     * @fires exception
     * @fires afterRequest
     * @category CRUD
     * @async
     * @returns {Promise} A promise which is resolved when the Ajax request completes and has been processed.
     */
    async loadChildren(parentRecord) {
        const me = this;

        return me.internalLoad({ [me.parentIdParamName] : parentRecord.id }, 'Children', (event) => {
            event.parentRecord = parentRecord;
            // Append received children
            parentRecord.data[parentRecord.constructor.childrenField] = event.data;
            parentRecord.processChildren(parentRecord.stores);
        });
    }

    /**
     * Loads a page of data from the {@link #config-readUrl}.
     * @param {Number} page The *one based* page number to load.
     * @param {Object} params A hash of parameters to append to querystring (will also append Store#params)
     * @returns {Promise} A Promise which will be resolved if the load succeeds, and rejected if the load is
     * vetoed by a {@link #event-beforeLoadPage} handler, or if an {@link #event-exception} is detected.
     * The resolved function is passed the event object passed to any event handlers.
     * The rejected function is passed the {@link #event-exception} event if an exception occurred,
     * or `false` if the load was vetoed by a {@link #event-beforeLoadPage} handler.
     * @fires beforeLoadPage
     * @fires loadPageStart
     * @fires beforeRequest
     * @fires loadPage
     * @fires exception
     * @fires afterRequest
     * @category CRUD
     * @async
     * @returns {Promise} A promise which is resolved when the Ajax request completes and has been processed.
     */
    async loadPage(page, params) {
        if (this.allCount) {
            page = Math.min(page, this.lastPage);
        }
        const
            me        = this,
            pageParam = me.pageParamName ? {
                [me.pageParamName] : page
            } : {
                [me.pageStartParamName] : (page - 1) * me.pageSize
            };

        pageParam[me.pageSizeParamName] = me.pageSize;
        return me.internalLoad(Object.assign(pageParam, params), 'Page', (event) => {
            // We go directly to loadPage because paging a tree store is unsupportable.
            // loadPage will trigger the refresh event with { action: 'pageLoad' }
            me.loadData(event.data, 'pageLoad');
        });
    }

    /**
     * If this store {@link #property-isPaged is paged}, and is not already at the {@link #property-lastPage}
     * then this will load the next page of data.
     * @fires beforeLoadPage
     * @fires loadPageStart
     * @fires beforeRequest
     * @fires loadPage
     * @fires exception
     * @fires afterRequest
     * @category CRUD
     * @async
     * @returns {Promise} A promise which is resolved when the Ajax request completes and has been processed.
     */
    async nextPage(params) {
        return this.isPaged && this.currentPage !== this.lastPage ? this.loadPage(this.currentPage + 1, params) : immediatePromise;
    }

    /**
     * If this store {@link #property-isPaged is paged}, and is not already at the first page
     * then this will load the previous page of data.
     * @fires beforeLoadPage
     * @fires loadPageStart
     * @fires beforeRequest
     * @fires loadPage
     * @fires exception
     * @fires afterRequest
     * @category CRUD
     * @async
     * @returns {Promise} A promise which is resolved when the Ajax request completes and has been processed.
     */
    async previousPage(params) {
        return this.isPaged && this.currentPage !== 1 ? this.loadPage(this.currentPage - 1, params) : immediatePromise;
    }

    /**
     * Commits all changes (added, modified and removed) using corresponding urls ({@link #config-createUrl},
     * {@link #config-updateUrl} and {@link #config-deleteUrl})
     * @fires beforeCommit
     * @returns {Promise} A Promise which is resolved only if all pending changes (Create, Update and Delete) successfully resolve.
     * Both the resolve and reject functions are passed a `commitState` object which is stored the {@link #event-afterRequest}
     * event for each request. Each event contains the `exception`, `request` and `response` properties eg:
     *
     *     {
     *          success: true,                  // If *all* commits succeeded
     *          changes: {
     *              added: [records...],
     *              modified: [records...],
     *              removed: [records...],
     *          },
     *          added: {
     *              source: theStore,
     *              exception: true,            // Only if the add request triggered an exception
     *              exceptionType: 'server'/'network', // Only if the add request triggered an exception
     *              response: Response,
     *              json: parsedResponseObject
     *          },
     *          modified: {},                   // Same format as added
     *          removed: {}                     // Same format as added
     *     }
     *
     * If there were no pending changes, the resolve and reject functions are passed no parameters.
     *
     * Returns `false` if a commit operation is already in progress.
     * The resolved function is passed the event object passed to any event handlers.
     * The rejected function is passed the {@link #event-exception} event if an exception occurred,
     * @category CRUD
     * @async
     * @returns {Promise} A promise which is resolved when all the Ajax requests complete and have been processed.
     */
    commit() {
        const me          = this,
            changes     = me.changes,
            allPromises = [];

        // not allowing additional commits while in progress
        // TODO: should queue
        if (me.commitPromise) return false;

        // No outstanding changes, return a Promise that resolves immediately.
        if (!changes) {
            return immediatePromise;
        }

        // Flag all affected records as being committed
        [...changes.added, ...changes.modified, ...changes.removed].forEach(record => record.meta.committing = true);

        // TODO: do we need a general way of disabling plugins?
        if (!me.disabled && me.trigger('beforeCommit', { changes }) !== false) {
            let commitState = {
                    action    : 'commit',
                    exception : false,
                    changes
                },
                p           = me.commitRemoved(commitState);

            if (p) {
                allPromises.push(p);
            }
            p = me.commitAdded(commitState);
            if (p) {
                allPromises.push(p);
            }
            p = me.commitModified(commitState);
            if (p) {
                allPromises.push(p);
            }

            // If there were no urls configured, behave as a local store
            if (!allPromises.length) {
                me.modified.forEach(r => r.clearChanges(false));
                me.modified.clear();

                me.added.forEach(r => r.clearChanges(false));
                me.added.clear();

                me.removed.clear();
                me.trigger('commit', { changes });
                return immediatePromise;
            }

            // The Promises from the commit methods all resolve whether the request
            // succeeded or not. They each contribute their afterrequest event to the
            // commitState which can be used to detect overall success or failure
            // and granular inspection of which operations succeeded or failed.
            // If there's only one operation, wait for it.
            // If there's more than one operation, we have to wait for allPromises to resolve.
            p = allPromises.length === 1 ? allPromises[0] : Promise.all(allPromises);

            return me.commitPromise = new Promise((resolve, reject) => {
                p.then(() => {
                    me.commitPromise = null;
                    if (commitState.exception) {
                        me.trigger('exception', commitState);
                        reject(commitState);
                    }
                    else {
                        me.trigger('commit', { changes });
                        resolve(commitState);
                    }
                }).catch(() => {
                    me.commitPromise = null;
                    reject(commitState);
                });
            });
        }
    }

    // TODO: need a way to abort commits

    /**
     * Commits added records by posting to {@link #config-createUrl}.
     * Server should return a JSON object with a 'success' property indicating whether the operation was succesful.
     * @param {Object} commitState An object into which is added a `delete` property being the {@link #event-afterRequest} event.
     * @returns {Promise} If there are added records, a Promise which will be resolved whether the commit
     * succeeds or fails. The resulting event is placed into the `add` property of the passed `commitState`
     * parameter. If there are no added records, `null` is returned.
     * The resolved function is passed the event object passed to any event handlers.
     * @private
     * @fires beforeRequest
     * @fires commitAdded
     * @fires refresh
     * @fires exception
     * @fires afterRequest
     * @returns {Promise} A promise which is resolved when the Ajax request completes and has been processed.
     * @async
     */
    commitAdded(commitState) {
        const me     = this,
            added  = me.added,
            event  = { action : 'create', params : me.params },
            result = added.count && me.createUrl ? new Promise((resolve) => {
                const toAdd = added.values.map(r => r.persistableData);
                commitState.create = event;

                let dataToSend = event.body = { data : toAdd };

                me.trigger('beforeRequest', event);

                if (me.sendAsFormData) {
                    const formData = new FormData();

                    formData.append('data', JSON.stringify(toAdd));
                    dataToSend = formData;
                }

                AjaxHelper.post(me.createUrl + me.buildQueryString(me.params), dataToSend, Object.assign({ headers : me.headers, parseJson : true }, me.fetchOptions)).then(response => {
                    const
                        data = response.parsedJson,
                        isArray = Array.isArray(data),
                        success = isArray || (data && (data.success !== false));

                    event.json = data;
                    event.response = response;

                    if (success) {
                        // Copy updated fields and updated ID back into records.
                        // This also calls clearChanges on each record.
                        me.processReturnedData(added.values, isArray ? data : data[me.responseDataProperty]);

                        // Clear down added records cache
                        added.clear();

                        me.trigger('commitAdded');

                        // We must signal a full refresh because any number of records could have recieved any number of field updates
                        // back from the server, so a refresh is more efficient than picking through the received updates.
                        me.trigger('refresh', event);

                        resolve(commitState);
                    }
                    else {
                        // Clear committing flag
                        added.forEach(r => r.meta.committing = false);

                        commitState.exception = event.exception = true;

                        commitState.exceptionType = event.exceptionType = 'server';

                        me.trigger('exception', event);
                        resolve(commitState);
                    }

                    // finally
                    me.trigger('afterRequest', event);
                }).catch(responseOrError => {
                    // Clear committing flag
                    added.forEach(r => r.meta.committing = false);

                    commitState.exception = event.exception = true;

                    if (responseOrError instanceof Response) {
                        event.exceptionType = responseOrError.ok ? 'server' : 'network';
                        event.response = responseOrError;
                        event.error = responseOrError.error;
                    }
                    else {
                        event.exceptionType = 'server';
                        event.error = responseOrError;
                    }

                    me.trigger('exception', event);
                    resolve(commitState);

                    // finally
                    me.trigger('afterRequest', event);
                });
            }) : null;

        return result;
    }

    /**
     * Commits modified records by posting to {@link #config-updateUrl}.
     * Server should return a JSON object with a 'success' property indicating whether the operation was succesful.
     * @param {Object} commitState An object into which is added a `delete` property being the {@link #event-afterRequest} event.
     * @returns {Promise} If there are added records, a Promise which will be resolved whether the commit
     * succeeds or fails. The resulting event is placed into the `update` property of the passed `commitState`
     * parameter. If there are no added records, `null` is returned.
     * The resolved function is passed the event object passed to any event handlers.
     * @private
     * @fires beforeRequest
     * @fires commitModified
     * @fires refresh
     * @fires exception
     * @fires afterRequest
     * @returns {Promise} A promise which is resolved when the Ajax request completes and has been processed.
     * @async
     */
    commitModified(commitState) {
        let me       = this,
            modified = me.modified,
            event    = { action : 'update', params : me.params },
            result   = modified.count && me.updateUrl ? new Promise((resolve) => {

                // Use the record's modificationData, not modifications.
                // modifications returns a map using *field names*
                // The server will expect a map using the original dataSource properties.
                const modifications = modified.map(r => me.writeAllFields ? r.persistableData : r.modificationData);
                commitState.update = event;

                let dataToSend = event.body = { data : modifications };

                me.trigger('beforeRequest', event);

                if (me.sendAsFormData) {
                    const formData = new FormData();

                    formData.append('data', JSON.stringify(modifications));
                    dataToSend = formData;
                }

                AjaxHelper.post(
                    me.updateUrl + me.buildQueryString(me.params),
                    dataToSend,
                    Object.assign({ headers : me.headers, parseJson : true }, me.fetchOptions)
                ).then(response => {
                    const
                        data = response.parsedJson,
                        isArray = Array.isArray(data),
                        success = isArray || (data && (data.success !== false));

                    event.json = data;
                    event.response = response;

                    if (success) {
                        // Copy updated fields and updated ID back into records.
                        // This also calls clearChanges on each record.
                        me.processReturnedData(me.modified.values, isArray ? data : data[me.responseDataProperty], true);

                        // Clear down modified records cache
                        modified.clear();

                        me.trigger('commitModified');

                        // We must signal a full refresh because any number of records could have recieved any number of field updates
                        // back from the server, so a refresh is more efficient than picking through the received updates.
                        me.trigger('refresh', event);

                        resolve(commitState);
                    }
                    else {
                        // Clear committing flag
                        modified.forEach(r => r.meta.committing = false);

                        commitState.exception = event.exception = true;
                        event.exceptionType = 'server';
                        me.trigger('exception', event);
                        resolve(commitState);
                    }

                    // finally
                    me.trigger('afterRequest', event);
                }).catch(responseOrError => {
                    // Clear committing flag
                    modified.forEach(r => r.meta.committing = false);

                    commitState.exception = event.exception = true;

                    if (responseOrError instanceof Response) {
                        event.exceptionType = responseOrError.ok ? 'server' : 'network';
                        event.response = responseOrError;
                        event.error = responseOrError.error;
                    }
                    else {
                        event.exceptionType = 'server';
                        event.error = responseOrError;
                    }

                    me.trigger('exception', event);
                    resolve(commitState);

                    // finally
                    me.trigger('afterRequest', event);
                });
            }) : null;

        return result;
    }

    processReturnedData(localRecords, returnedData, isUpdating = false) {
        const me = this,
            Model = me.modelClass,
            idDataSource = Model.fieldMap.id.dataSource;

        returnedData.forEach((recData, i) => {
            const record = localRecords[i];

            // Must clear changed state before syncId goes through store.onModelChange
            record.clearChanges(false);

            // Using syncId to update record's id with no flagging the property as modified.
            record.syncId(recData[idDataSource]);

            // When updating, only want to apply the actual changes and not reapply defaults. When adding, also
            // apply the defaults
            Object.assign(localRecords[i].data, Model.processData(recData, isUpdating));
        });
    }

    /**
     * Commits removed records by posting to {@link #config-deleteUrl}.
     * Server should return a JSON object with a 'success' property indicating whether the operation was succesful.
     * @param {Object} commitState An object into which is added a `delete` property being the {@link #event-afterRequest} event.
     * @returns {Promise} If there are added records, a Promise which will be resolved whether the commit
     * succeeds or fails. The resulting event is placed into the `delete` property of the passed `commitState`
     * parameter. If there are no added records, `null` is returned.
     * The resolved function is passed the event object passed to any event handlers.
     * @private
     * @fires beforerequest
     * @fires commitremoved
     * @fires refresh
     * @fires exception
     * @fires afterrequest
     * @returns {Promise} A promise which is resolved when the Ajax request completes and has been processed.
     * @async
     */
    commitRemoved(commitState) {
        const me      = this,
            removed = me.removed,
            event   = { action : 'delete', params : me.params },
            result  = removed.count && me.deleteUrl ? new Promise((resolve) => {
                commitState.delete = event;

                let dataToSend = event.body = { ids : removed.map(r => r.id) };

                me.trigger('beforeRequest', event);

                if (me.sendAsFormData) {
                    const formData = new FormData();

                    formData.append('id', JSON.stringify(dataToSend.ids));
                    dataToSend = formData;
                }

                AjaxHelper.post(
                    me.deleteUrl + me.buildQueryString(me.params),
                    dataToSend,
                    Object.assign({ headers : me.headers, parseJson : true }, me.fetchOptions)
                ).then(response => {
                    const
                        data = response.parsedJson,
                        isArray = Array.isArray(data),
                        success = isArray || (data && (data.success !== false));

                    event.json = data;
                    event.response = response;

                    if (success) {
                        removed.forEach(record => record.meta.committing = false); // In case used by other store etc.
                        removed.clear();

                        me.trigger('commitRemoved');
                        me.trigger('refresh', event);

                        resolve(commitState);
                    }
                    else {
                        // Clear committing flag
                        removed.forEach(r => r.meta.committing = false);

                        commitState.exception = event.exception = true;

                        event.exceptionType = 'server';
                        me.trigger('exception', event);
                        resolve(commitState);
                    }

                    // finally
                    me.trigger('afterRequest', event);
                }).catch(responseOrError => {
                    // Clear committing flag
                    removed.forEach(r => r.meta.committing = false);

                    commitState.exception = event.exception = true;

                    if (responseOrError instanceof Response) {
                        event.exceptionType = responseOrError.ok ? 'server' : 'network';
                        event.response = responseOrError;
                        event.error = responseOrError.error;
                    }
                    else {
                        event.exceptionType = 'server';
                        event.error = responseOrError;
                    }

                    me.trigger('exception', event);
                    resolve(commitState);

                    // finally
                    me.trigger('afterRequest', event);
                });
            }) : null;

        return result;
    }
}
