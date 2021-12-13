  
;(function (root, factory) {
  'use strict'
  /* istanbul ignore next */
  if (typeof define === 'function' && define.amd) {
    define('ajax', factory)
  } else if (typeof exports === 'object') {
    exports = module.exports = factory()
  } else {
    root.ajax = factory()
  }
})(this, function () {
  'use strict'

  function ajax (options) {
    var methods = ['get', 'post', 'put', 'delete']
    options = options || {}
    options.baseUrl = options.baseUrl || ''
    if (options.method && options.url) {
      return xhrConnection(
        options.method,
        options.baseUrl + options.url,
        maybeData(options.data),
        options
      )
    }
    return methods.reduce(function (acc, method) {
      acc[method] = function (url, data) {
        return xhrConnection(
          method,
          options.baseUrl + url,
          maybeData(data),
          options
        )
      }
      return acc
    }, {})
  }

  function maybeData (data) {
    return data || null
  }

  function xhrConnection (type, url, data, options) {
    var returnMethods = ['then', 'catch', 'always']
    var promiseMethods = returnMethods.reduce(function (promise, method) {
      promise[method] = function (callback) {
        promise[method] = callback
        return promise
      }
      return promise
    }, {})
    var xhr = new XMLHttpRequest()
    var featuredUrl = getUrlWithData(url, data, type)
    xhr.open(type, featuredUrl, true)
    xhr.withCredentials = options.hasOwnProperty('withCredentials')
    setHeaders(xhr, options.headers)
    xhr.addEventListener('readystatechange', ready(promiseMethods, xhr), false)
    xhr.send(objectToQueryString(data))
    promiseMethods.abort = function () {
      return xhr.abort()
    }
    return promiseMethods
  }

  function getUrlWithData (url, data, type) {
    if (type.toLowerCase() !== 'get' || !data) {
      return url
    }
    var dataAsQueryString = objectToQueryString(data)
    var queryStringSeparator = url.indexOf('?') > -1 ? '&' : '?'
    return url + queryStringSeparator + dataAsQueryString
  }

  function setHeaders (xhr, headers) {
    headers = headers || {}
    if (!hasContentType(headers)) {
      headers['Content-Type'] = 'application/x-www-form-urlencoded'
    }
    Object.keys(headers).forEach(function (name) {
      (headers[name] && xhr.setRequestHeader(name, headers[name]))
    })
  }

  function hasContentType (headers) {
    return Object.keys(headers).some(function (name) {
      return name.toLowerCase() === 'content-type'
    })
  }

  function ready (promiseMethods, xhr) {
    return function handleReady () {
      if (xhr.readyState === xhr.DONE) {
        xhr.removeEventListener('readystatechange', handleReady, false)
        promiseMethods.always.apply(promiseMethods, parseResponse(xhr))

        if (xhr.status >= 200 && xhr.status < 300) {
          promiseMethods.then.apply(promiseMethods, parseResponse(xhr))
        } else {
          promiseMethods.catch.apply(promiseMethods, parseResponse(xhr))
        }
      }
    }
  }

  function parseResponse (xhr) {
    return [ xhr.responseText, xhr ]
  }

  function objectToQueryString (data) {
    return isObject(data) ? getQueryString(data) : data
  }

  function isObject (data) {
    return Object.prototype.toString.call(data) === '[object Object]'
  }

  function getQueryString (object) {
    return Object.keys(object).reduce(function (acc, item) {
      var prefix = !acc ? '' : acc + '&'
      return prefix + encode(item) + '=' + encode(object[item])
    }, '')
  }

  function encode (value) {
    return encodeURIComponent(value)
  }

  return ajax
})
//
function ajaxInvoke(addr,func, args, success, error){
    var data = {};
    for (var i = 0; i < args.length; i++) {
      data["arg" + i] = JSON.stringify(args[i]);
    }
    console.log(">>>invoke:", func, args);
    ajax().post(addr + func, data).always(function (response, xhr) {
      if (xhr.status != 200) {
         if(error){
             error(0,"网络连接失败，请稍后重试");
         }
      } else {
        var ss = response.split("\n");
        var ret = ss[0];
        var exception = "";
        for (var i = 1; i < ss.length; i++) {
          exception += ss[i];
        }
        if (exception != null && exception != '') {
          var qq = exception.split(",");
          var errorMessage = [];
          for (var i = 2; i < qq.length; i++) {
            errorMessage.push(qq[i]);
          }
          var errorString = errorMessage.join(",");
          console.log("<<<invoke:", func, errorString);
          if (error) {
              error(qq[1], errorString);
          }
        } else {
          var t = null;
          if (ret != '') {
            t = JSON.parse(ret);
          }
          console.log("<<<invoke:", func, t);
          if (success) {
            success(t)
          }
        }
      }
    })
}