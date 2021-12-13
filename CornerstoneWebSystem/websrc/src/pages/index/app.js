import * as VueWindow from '@hscmap/vue-window';
import 'echarts/lib/chart/bar';
import 'echarts/lib/chart/funnel';
import 'echarts/lib/chart/line';
import 'echarts/lib/chart/pie';
import 'echarts/lib/chart/sunburst';
import 'echarts/lib/component/legend';
import 'echarts/lib/component/title';
import 'echarts/lib/component/toolbox';
import 'echarts/lib/component/tooltip';
import 'echarts/lib/component/visualMap';
import 'echarts/lib/component/dataZoom';
import iView from 'iview';

import 'iview/dist/styles/iview.css';
import VueHotkey from 'v-hotkey';
import Vue from 'vue';
import VueClipboard from 'vue-clipboard2';
import ECharts from 'vue-echarts/components/ECharts';
import 'vue-image-lightbox/dist/vue-image-lightbox.min.css';
import VueLazyLoad from 'vue-lazyload';

import VueRouter from 'vue-router';
import '../../assets/component-mixin.js';
import '../../assets/frame-mixin.js';
//
import i18n from '../../assets/i18n2';
import '../../assets/style.css';
import '../../assets/v-directive-tooltip.less';
import '../../components/ui/install';
//
import NavigationPageContainer from '../../components/ui/NavigationPageContainer.vue';
import uploader from 'vue-simple-uploader'

import App from './index.vue';

Vue.use(uploader);

Vue.use(VueClipboard);

Vue.use(VueHotkey);

Vue.component('v-chart', ECharts);
//
Vue.config.productionTip = false;
Vue.use(VueWindow);
Vue.use(VueRouter);
Vue.use(iView);
Vue.use(VueLazyLoad);

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
        path: '/main/', component: CreateCompanyMainFrame,
    },
    {
        path: '/pm/version/', component: ProjectMainFrame,
        children: [
            { path: 'repository', component: VersionRepositoryMainFrame },
        ],
    },
    {
        path: '/pm/project/:project/', component: ProjectMainFrame,
        children: [
            { path: ':page', component: NavigationPageContainer },
        ],
    },
    {
        path: '/pm/index/', component: ProjectMainFrame,
        children: [
            { path: ':page', component: NavigationPageContainer },
        ],
    },
    {
        path: '/pm/terminal/:token/', component: ProjectMainFrame,
        children: [
            { path: ':page', component: NavigationPageContainer },
        ],
    },
    {
        path: '/company/:company/', component: CompanyMainFrame,
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
    created() {
        window.app = this;
        app.i18n = i18n;
        app.name = 'CORNERSTONE';
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
            app.beta = DEBUG;
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
                app.departmentList = info.departmentList;
                app.dataDicts = info.dataDicts;
                app.owaUrl = info.owaUrl;
                app.permissionMap = {};
                app.roles = info.roles;
                app.reportConfig = info.reportConfig;
                app.webEventPort = info.webEventPort;
                app.uploadFileSize = info.uploadFileSize;
                app.showMultiUpload = info.showMultiUpload;
                app.showNewHome = info.showNewHome;
                app.globalKanbanSort = info.globalKanbanSort;
                app.isAdSet = info.isAdSet;
                app.isSupplierEnable = info.isSupplierEnable;
                app.isAttendanceEnable = info.isAttendanceEnable;
                app.officeType = info.officeType;
                app.isSpiEnable = info.isSpiEnable;
                app.isAddMemberLimit = info.isAddMemberLimit;
                app.isRepositoryVersionExtension = info.isRepositoryVersionExtension;
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

