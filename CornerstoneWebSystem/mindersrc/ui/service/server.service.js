/**
 * @fileOverview
 *
 *  与后端交互的服务
 *
 * @author: zhangbobell
 * @email : zhangbobell@163.com
 *
 * @copyright: Baidu FEX, 2015
 */
angular.module('kityminderEditor')
    .service('server', ['config', '$http', function (config, $http) {

        return {
            uploadImage: function (file) {
                var url = window.ThirdSettings.get('imageUpload') || config.get('imageUpload');
                var fileKey = window.ThirdSettings.get('fileKey') || config.get('fileKey');
                var fd = new FormData();
                fd.append(fileKey || 'upload_file', file);

                return $http.post(url, fd, {
                    transformRequest: angular.identity,
                    headers: {
                        'Content-Type': undefined
                    }
                });
            }
        }
    }]);