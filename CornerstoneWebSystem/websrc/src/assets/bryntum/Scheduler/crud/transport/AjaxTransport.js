import Base from '../../../Core/Base.js';
import AjaxHelper from '../../../Core/helper/AjaxHelper.js';

/**
 * @module Scheduler/crud/transport/AjaxTransport
 */

/**
 * Implements data transferring functional that can be used for {@link Scheduler.crud.AbstractCrudManager} super classing.
 * Uses the fetch API for transport, https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API
 *
 * @example
 * // create a new CrudManager using AJAX as a transport system and JSON for encoding
 * class MyCrudManager extends AjaxTransport(JsonEncode(AbstractCrudManager)) {}
 *
 * @abstract
 * @mixin
 */
export default Target => class AjaxTransport extends (Target || Base) {
    /**
     * Configuration of the AJAX requests used to communicate with a server-side.
     * An object where you can set the following possible properties:
     * @config {Object} transport
     * @property {Object} transport.load Load requests configuration:
     * @property {String} transport.load.url URL to request for data loading.
     * @property {String} [transport.load.method='POST'] HTTP method to be used for load requests.
     * @property {String} [transport.load.paramName] Name of parameter in which a packet will be transfered. If not specified then a packet will be transfered in a request body (default).
     * @property {Object} [transport.load.params] Extra load request params if needed.
     * @property {Object} [transport.load.requestConfig] Ajax request config. Can be used instead of above `url`, `method`, `params` and some more:
     * @property {Object} [transport.load.requestConfig.headers] An object containing headers to pass to each server request.
     * @property {Object} [transport.load.requestConfig.fetchOptions] An object containing the Fetch options to pass to each server request.
     * Use this to control if credentials are sent and other options, read more at [MDN](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch).
     *
     * ```javascript
     * transport   : {
     *     load    : {
     *         requestConfig : {
     *             url             : 'http://some-url',
     *             method          : 'GET',
     *             // get rid of cache-buster parameter
     *             disableCaching  : false,
     *             // extra request parameters
     *             params          : {
     *                 foo         : 'bar'
     *             },
     *             // custom request headers
     *             headers         : {
     *                 ...
     *             },
     *             fetchOptions    : {
     *                 credentials : 'include'
     *             }
     *         }
     *     }
     * }
     * ```
     *
     * @property {Object} transport.sync Sync requests configuration:
     * @property {String} transport.sync.url URL to request for data persisting.
     * @property {String} [transport.sync.method='POST'] HTTP method to be used for sync requests.
     * @property {String} [transport.sync.paramName] Name of parameter in which a packet will be transfered. If not specified then a packet will be transfered in a request body (default).
     * @property {Object} [transport.sync.params] Extra sync request params if needed.
     * @property {Object} [transport.sync.requestConfig] Ajax request config. Can be used instead of above `url`, `method`, `params` and some more:
     * @property {Object} [transport.sync.requestConfig.headers] An object containing headers to pass to each server request.
     * @property {Object} [transport.sync.requestConfig.fetchOptions] An object containing the Fetch options to pass to each server request.
     * Use this to control if credentials are sent and other options, read more at [MDN](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch).
     *
     * ```javascript
     * transport   : {
     *     sync    : {
     *         requestConfig : {
     *             url             : 'http://some-url',
     *             method          : 'GET',
     *             // get rid of cache-buster parameter
     *             disableCaching  : false,
     *             // extra request parameters
     *             params          : {
     *                 foo         : 'bar'
     *             },
     *             // custom request headers
     *             headers         : {
     *                 ...
     *             },
     *             fetchOptions    : {
     *                 credentials : 'include'
     *             }
     *         }
     *     }
     * }
     * ```
     */

    static get defaultMethod() {
        return {
            load : 'GET',
            sync : 'POST'
        };
    }

    /**
     * Cancels a sent request.
     * @param {Promise} requestPromise The Promise object wrapping the Request to be cancelled.
     * The _requestPromise_ is the return value returned from the corresponding {@link #function-sendRequest} call.
     */
    cancelRequest(requestPromise) {
        requestPromise.abort();
    }

    /**
     * Sends request to the server.
     * @param {Object} request The request configuration object having following properties:
     * @param {String} request.data The encoded request.
     * @param {String} request.type The request type. Either `load` or `sync`.
     * @param {Function} request.success A function to be started on successful request transferring.
     * @param {String} request.success.rawResponse `Response` object returned by the [fetch api](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API).
     * @param {Function} request.failure A function to be started on request transfer failure.
     * @param {String} request.failure.rawResponse `Response` object returned by the [fetch api](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API).
     * @param {Object} request.thisObj `this` reference for the above `success` and `failure` functions.
     * @return {Promise} The fetch Promise object.
     * @fires beforesend
     */
    sendRequest(config) {
        let me        = this,
            pack      = config.data,
            packCfg   = me.transport[config.type] || {},
            paramName = packCfg.paramName,
            params    = Object.assign({}, packCfg.params),
            method    = packCfg.method || AjaxTransport.defaultMethod[config.type];

        let requestConfig = Object.assign({
            url    : packCfg.url,
            method : method,
            params : params
        }, packCfg.requestConfig);

        // if no param name specified then we'll transfer package in the request body
        if (!paramName) {
            // TODO: get rid of this legacy code in the next major release
            // ..here we should simply make: requestConfig.body = pack
            requestConfig.jsonData = pack;
        // ..otherwise we use parameter
        }
        else {
            requestConfig.params = requestConfig.params || {};
            requestConfig.params[paramName] = pack;
        }

        /**
         * Fires before a request is sent to the server.
         *
         * ```javascript
         * crudManager.on('beforesend', function (crud, params, requestType) {
         *     // let's set "sync" request parameters
         *     if (requestType == 'sync') {
         *         // dynamically depending on "flag" value
         *         if (flag) {
         *             params.foo = 'bar';
         *         } else {
         *             params.foo = 'smth';
         *         }
         *     }
         * });
         * ```
         * @event beforeSend
         * @param {Scheduler.crud.AbstractCrudManager} crudManager The CRUD manager.
         * @param {Object} params Request params
         * @param {String} requestType Request type (`load`/`sync`)
         * @param {Object} requestConfig Configuration object for Ajax request call
         */
        me.trigger('beforeSend', { params, type : config.type, requestConfig, config });

        const
            responseOptions = Object.assign({
                method,
                headers     : requestConfig.headers,
                queryParams : params,
                body        : method === 'HEAD' || method === 'GET' ? undefined : requestConfig.jsonData
            }, requestConfig.fetchOptions),
            ajaxPromise = AjaxHelper.fetch(requestConfig.url, responseOptions);

        ajaxPromise.then(response => {
            if (response.ok) {
                config.success && config.success.call(config.thisObj || me, response, responseOptions);
            }
            else {
                config.failure && config.failure.call(config.thisObj || me, response, responseOptions);
            }
        });

        return ajaxPromise;
    }
};
