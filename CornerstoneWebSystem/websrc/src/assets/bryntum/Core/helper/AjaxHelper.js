/**
 * @module Core/helper/AjaxHelper
 */

/**
 * Simplifies Ajax requests. Uses fetch & promises.
 *
 * ```javascript
 * AjaxHelper.get('some-url').then(response => {
 *     // process request response here
 * });
 * ```
 *
 * Uploading file to server via FormData interface.
 * Please visit <https://developer.mozilla.org/en-US/docs/Web/API/FormData> for details.
 *
 * ```javascript
 * const formData = new FormData();
 * formData.append('file', 'fileNameToUpload');
 * AjaxHelper.post('file-upload-url', formData).then(response => {
 *     // process request response here
 * });
 * ```
 *
 */
export default class AjaxHelper {
    /**
     * Make a request (using GET) to the specified url.
     * @param {String} url Url
     * @param {Object} [options] The options for the `fetch` API. Please see <https://developer.mozilla.org/en-US/docs/Web/API/WindowOrWorkerGlobalScope/fetch> for details
     * @param {Object} [options.queryParams] A key-value pair Object containing the params to add to the query string
     * @param {Object} [options.headers] Any headers you want to add to your request, contained within a `Headers object or an object literal with ByteString values
     * @param {Object} [options.body] Any body that you want to add to your request: this can be a Blob, BufferSource, FormData, URLSearchParams, or USVString object. Note that a request using the GET or HEAD method cannot have a body.
     * @param {Object} [options.mode] The mode you want to use for the request, e.g., cors, no-cors, or same-origin.
     * @param {Object} [options.credentials] The request credentials you want to use for the request: omit, same-origin, or include. To automatically send cookies for the current domain, this option must be provided
     * @param {Object} [options.parseJson] Specify `true` to parses the response and attach the resulting object to the `Response` object as `parsedJson`
     * @returns {Promise} The fetch Promise, which can be aborted by calling a special `abort` method
     */
    static get(url, options) {
        return this.fetch(url, options);
    }

    /**
     * POST data to the specified URL.
     * @param {String} url The URL
     * @param {String|Object|FormData} payload The data to post. If an object is supplied, it will be stringified
     * @param {Object} options The options for the `fetch` API. Please see <https://developer.mozilla.org/en-US/docs/Web/API/WindowOrWorkerGlobalScope/fetch> for details
     * @param {Object} [options.queryParams] A key-value pair Object containing the params to add to the query string
     * @param {Object} [options.headers] Any headers you want to add to your request, contained within a `Headers object or an object literal with ByteString values
     * @param {Object} [options.body] Any body that you want to add to your request: this can be a Blob, BufferSource, FormData, URLSearchParams, or USVString object. Note that a request using the GET or HEAD method cannot have a body.
     * @param {Object} [options.mode] The mode you want to use for the request, e.g., cors, no-cors, or same-origin.
     * @param {Object} [options.credentials] The request credentials you want to use for the request: omit, same-origin, or include. To automatically send cookies for the current domain, this option must be provided
     * @param {Object} [options.parseJson] Specify `true` to parses the response and attach the resulting object to the `Response` object as `parsedJson`
     * @returns {Promise} The fetch Promise, which can be aborted by calling a special `abort` method
     */
    static post(url, payload, options = {}) {
        if (!(payload instanceof FormData) && !(typeof payload === 'string')) {
            payload = JSON.stringify(payload);
        }

        return this.fetch(url, Object.assign({
            method : 'POST',
            body   : payload
        }, options));
    }

    /**
     * Fetch the specified resource using the `fetch` API.
     * @param {String} url object to fetch
     * @param {Object} options The options for the `fetch` API. Please see <https://developer.mozilla.org/en-US/docs/Web/API/WindowOrWorkerGlobalScope/fetch> for details
     * @param {Object} [options.method] The request method, e.g., GET, POST
     * @param {Object} [options.queryParams] A key-value pair Object containing the params to add to the query string
     * @param {Object} [options.headers] Any headers you want to add to your request, contained within a `Headers object or an object literal with ByteString values
     * @param {Object} [options.body] Any body that you want to add to your request: this can be a Blob, BufferSource, FormData, URLSearchParams, or USVString object. Note that a request using the GET or HEAD method cannot have a body.
     * @param {Object} [options.mode] The mode you want to use for the request, e.g., cors, no-cors, or same-origin.
     * @param {Object} [options.credentials] The request credentials you want to use for the request: omit, same-origin, or include. To automatically send cookies for the current domain, this option must be provided
     * @param {Object} [options.parseJson] Specify `true` to parses the response and attach the resulting object to the `Response` object as `parsedJson`
     * @returns {Promise} The fetch Promise, which can be aborted by calling a special `abort` method
     */
    static fetch(url, options = {}) {
        const
            controller = new AbortController();
        options.signal = controller.signal;

        if (!('credentials' in options)) {
            options.credentials = 'include';
        }

        if (options.queryParams) {
            const params = Object.entries(options.queryParams);
            if (params.length) {
                url += '?' + params.map(([param, value]) =>
                    `${param}=${encodeURIComponent(value)}`
                ).join('&');
            }
        }

        // Promise that will be resolved either when network request is finished or when json is parsed
        const promise = new Promise((resolve, reject) => {
            fetch(url, options).then(
                response => {
                    if (options.parseJson) {
                        response.json().then(json => {
                            response.parsedJson = json;
                            resolve(response);
                        }).catch(error => {
                            response.parsedJson = null;
                            response.error = error;
                            reject(response);
                        });
                    }
                    else {
                        resolve(response);
                    }
                }
            ).catch(error => {
                reject(error);
            });
        });

        promise.abort = function() {
            controller.abort();
        };

        return promise;
    }
}
