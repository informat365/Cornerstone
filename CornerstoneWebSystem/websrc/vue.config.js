'use strict';
const glob = require('glob');
const pages = {};
(() => {
    const now = new Date();
    const year = now.getFullYear();
    const month = now.getMonth() + 1;
    const day = now.getDate();
    const hour = now.getHours();
    const minute = now.getMinutes();
    const dateTime = [year, month, day].map(item => item > 9 ? item : `0${ item }`).join('') + '' + [hour, minute].map(item => item > 9 ? item : `0${ item }`).join('');
    process.env.VUE_APP_RELEASE_VERSION = process.env.VUE_APP_RELEASE_VERSION + '_'+dateTime.substring(2)+'_' + Number.parseInt(dateTime, 10).toString(36);
    console.log('Build Version##', process.env.VUE_APP_RELEASE_VERSION);
})();
var pageFilter = './src/pages/**/app.js';
// 开发环境下 新增了页面 必须要在这里配置 否则都会跳转至login页面
if (process.env.NODE_ENV === 'development') {
    pageFilter = './src/pages/@(login|index)/app.js';
    pageFilter = './src/pages/@(login|index|register|resetpwd|surveysform)/app.js';
}
glob.sync(pageFilter).forEach(path => {
    const chunk = path.split('./src/pages/')[1].split('/app.js')[0];
    pages[chunk] = {
        entry: path,
        template: './src/pages/' + chunk + '/' + chunk + '.html',
    };
});
console.log('============================Project setting============================');
console.log('VUE_APP_PROJECT_SET_TEMPLATE_UUID:\t\t\t', process.env.VUE_APP_PROJECT_SET_TEMPLATE_UUID);
console.log('VUE_APP_DASHBOARD_PAGE_PROJECT_SET_MENU:\t\t', process.env.VUE_APP_DASHBOARD_PAGE_PROJECT_SET_MENU);
console.log('VUE_APP_PROJECT_SET_OBJECT_TYPE_SYSTEM_NAME_PRO:\t', process.env.VUE_APP_PROJECT_SET_OBJECT_TYPE_SYSTEM_NAME_PRO);
console.log('VUE_APP_PROJECT_SET_OBJECT_TYPE_SYSTEM_NAME_POS:\t', process.env.VUE_APP_PROJECT_SET_OBJECT_TYPE_SYSTEM_NAME_POS);
console.log('=======================================================================');
console.table(pages);
module.exports = {
    publicPath: '/',
    outputDir: '../webroot',
    lintOnSave: false,
    productionSourceMap: process.env.NODE_ENV === 'development',
    transpileDependencies: [
        'vue-echarts',
        'resize-detector',
    ],
    css: {
        extract: true,
        sourceMap: false,
        loaderOptions: {},
        modules: false,
    },
    chainWebpack: (config) => {
        // 因为是多页面，所以取消 chunks，每个页面只对应一个单独的 JS / CSS
        config.optimization.splitChunks({
            cacheGroups: {},
        });
        config.plugins.delete('named-chunks');
        config.module
        .rule('i18n')
        .resourceQuery(/blockType=i18n/)
        .type('javascript/auto')
        .use('i18n')
        .loader('@kazupon/vue-i18n-loader')
        .end();
    },
    pages,
    devServer: {
        open: process.platform === 'darwin',
        host: '0.0.0.0',
        port: 8080,
        https: false,
        hotOnly: true,
        overlay: {
            warnings: true,
            errors: true,
        },
        proxy: {
            '/p': {
                target: 'http://127.0.0.1:8888',
            },
        },
    },
};
