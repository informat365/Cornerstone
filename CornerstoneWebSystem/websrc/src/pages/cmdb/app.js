import * as VueWindow from '@hscmap/vue-window';
import iView from 'iview';

import 'iview/dist/styles/iview.css';
import VueHotkey from 'v-hotkey';
import Vue from 'vue';
import VueClipboard from 'vue-clipboard2';
import VueLazyLoad from 'vue-lazyload';


import VueRouter from 'vue-router';
import '../../assets/component-mixin.js';
import '../../assets/frame-mixin.js';
import i18n from '../../assets/i18n2';
import '../../assets/style.css';
import '../../components/ui/install';
//
import NavigationPageContainer from '../../components/ui/NavigationPageContainer.vue';
import App from './cmdb.vue';

Vue.use(VueClipboard);

Vue.use(VueHotkey);

//
Vue.config.productionTip = false;
Vue.use(VueWindow);
Vue.use(VueRouter);
Vue.use(iView);
Vue.use(VueLazyLoad);
//


var loadComponent = function () {
    const requireComponent = require.context(
        './components',
        true,
    );
    requireComponent.keys().forEach(fileName => {
        const componentConfig = requireComponent(fileName);
        var componentName = fileName.replace(/^\.\/(.*)\.\w+$/, '$1');
        var idx = componentName.lastIndexOf('/');
        if (idx != -1) {
            componentName = componentName.substring(idx + 1);
        }
        console.log('register component:', componentName);
        window[componentName] = Vue.component(
            componentName,
            componentConfig.default || componentConfig,
        );
    });
};
loadComponent();
//
const routes = [
    {
        path: '/:company/', component: CmdbMainFrame,
        children: [
            { path: ':page', component: NavigationPageContainer },
        ],
    },
];
//

const router = new VueRouter({
    routes,
});
//
new Vue({
    render: h => h(App),
    router,
    i18n,
    mixins: [frameMixin],
    created: function () {
        window.app = this;
        app.name = 'CORNERSTONE';
        app.i18n = i18n;
        app.serverAddr = '';
        this.setup();
        app.invokeErrorHandler = (code, message) => {
            if (code == 100004) {
                window.location.href = '/login.html';
            }
        };
        app.getLoginInfo = this.getLoginInfo;
    },
    methods: {
        setup() {
            app.serverAddr = [window.location.protocol, '//', window.location.host].join('');
            this.serverAddr = [window.location.protocol, '//', window.location.host].join('');
            const DEBUG = process.env.NODE_ENV === 'development';
            if (!DEBUG) {
                if (!window.console) window.console = {};
                var methods = ['log', 'debug', 'warn', 'info'];
                for (var i = 0; i < methods.length; i++) {
                    console[methods[i]] = function () {
                    };
                }
            }
        },
        getLoginInfo(callback) {
            var token = app.getCookie('token');
            if (token == null) {
                window.location.href = '/login.html';
                return;
            }
            app.token = token;
            app.invoke('BizAction.getLoginInfo', [token], (info) => {
                app.account = info.account;
                app.companyList = info.companyList;
                app.projectList = info.projectList;
                app.dataDicts = info.dataDicts;
                app.owaUrl = info.owaUrl;
                app.permissionMap = {};
                for (var i = 0; i < info.permissionList.length; i++) {
                    var t = info.permissionList[i];
                    app.permissionMap[t] = true;
                }
                //
                if (info.companyList) {
                    for (var i = 0; i < info.companyList.length; i++) {
                        if (info.companyList[i].id == app.account.companyId) {
                            app.company = info.companyList[i];
                        }
                    }
                }
                if (callback) {
                    callback();
                }
            });
        },
    },
}).$mount('#app');
