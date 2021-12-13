// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import VueRouter from 'vue-router'
import App from './App'
//
import {
	AlertPlugin,
	ToastPlugin,
	LoadingPlugin,
	ConfirmPlugin
} from 'vux'
Vue.use(AlertPlugin)
Vue.use(ToastPlugin)
Vue.use(LoadingPlugin)
Vue.use(ConfirmPlugin)
//
import './assets/js/component-mixin.js';
import './assets/js/frame-mixin.js';
import './assets/css/style.css'
import './assets/css/simditor.css'
import './assets/css/simditor-small.css'
//
Vue.use(VueRouter)
//
var loadComponent = function () {
	const requireComponent = require.context(
		'./components',
		true
	)
	requireComponent.keys().forEach(fileName => {
		const componentConfig = requireComponent(fileName)
		var componentName = fileName.replace(/^\.\/(.*)\.\w+$/, '$1')
		var idx = componentName.lastIndexOf('/');
		if (idx != -1) {
			componentName = componentName.substring(idx + 1);
		}
		console.log("register component:", componentName);
		window[componentName] = Vue.component(
			componentName,
			componentConfig.default || componentConfig
		)
	})
};
loadComponent();
//
const routes = [{
		path: '/login',
		component: LoginPage
	},
	{
		path: '/wechat_login',
		component: WechatLoginPage
	},
	{
		path: '/m',
		component: MainFrame,
		children: [{
			path: ':page',
			component: NavigationPageContainer
		}]
	},
	{
		path: '/t',
		component: TabMainFrame,
		children: [{
			path: ':page',
			component: NavigationPageContainer
		}]
	}
]
//
const router = new VueRouter({
	routes
})
//
Vue.config.productionTip = false
//
new Vue({
	router,
	render: h => h(App),
	mixins: [frameMixin],
	created: function () {
		window.app = this;
		this.version = "0.0.1"
		this.setup();
		app.invokeErrorHandler = (code, message) => {
			if (code == 100004) {
				window.location.href = "/mobile/#/login"
			}
		}
	},
	methods: {
		setup() {
			var host = window.location.host;
			if (host.indexOf('localhost') != -1 || host.indexOf('10.0.0') != -1) {
				this.serverAddr = "https://pm.itit.io";
				// this.serverAddr="http://10.0.0.123:8888";
			}
			//
			var DEBUG = false;
			var host = window.location.host;
			if (host.indexOf("localhost") != -1) {
				DEBUG = true;
			}
			if (!DEBUG) {
				if (!window.console) window.console = {};
				var methods = ["log", "debug", "warn", "info"];
				for (var i = 0; i < methods.length; i++) {
					console[methods[i]] = function () {};
				}
			}
		},
		getLoginInfo(callback) {
			var token = app.getCookie('token');
			if (token == null) {
				window.location.href = "/mobile/#/login"
				return;
			}
			app.token = token;
			app.invoke('BizAction.getLoginInfo', [token], (info) => {
				app.account = info.account;
				app.companyList = info.companyList;
				app.projectList = info.projectList;
				app.dataDicts = info.dataDicts;
				app.mentionList = info.mentionList;
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
			})
		}
	}
}).$mount('#app-box')