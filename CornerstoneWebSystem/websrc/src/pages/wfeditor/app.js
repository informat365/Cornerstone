import Vue from 'vue'
import App from './wfeditor.vue'
import iView from 'iview';
import VueRouter from 'vue-router';
import VueClipboard from 'vue-clipboard2'
Vue.use(VueClipboard)

import VueHotkey from 'v-hotkey'
Vue.use(VueHotkey)
import i18n from '../../assets/i18n2';

import 'iview/dist/styles/iview.css';
import '../../assets/component-mixin.js';
import '../../assets/frame-mixin.js';
import '../../assets/style.css'
//
Vue.config.productionTip = false
Vue.use(iView);
Vue.use(VueRouter);
//
var loadComponent=function(){
	const requireComponent = require.context(
		'./components',
		true
	)
	requireComponent.keys().forEach(fileName => {
		const componentConfig = requireComponent(fileName)
		var componentName = fileName.replace(/^\.\/(.*)\.\w+$/, '$1')
		var idx=componentName.lastIndexOf('/');
		if(idx!=-1){
		  componentName=componentName.substring(idx+1);
		}
		console.log("register component:",componentName);
		window[componentName]=Vue.component(
		  componentName,
		  componentConfig.default || componentConfig
		)
	})
};
loadComponent();
//
import '../../components/ui/install'
//
import NavigationPageContainer from '../../components/ui/NavigationPageContainer.vue'
import WfEditorMainFrame from './components/WfEditorMainFrame.vue'
//
const routes = [
    {
        path: '/:uuid/', component: WfEditorMainFrame,
        children:[
            {path:':page',component:NavigationPageContainer}
        ]
    },
]

const router = new VueRouter({
    routes
  })
//
new Vue({
    render: h => h(App),
    router,
    i18n,
	mixins: [frameMixin],
	created:function(){
		window.app=this;
        app.name="CORNERSTONE";
        app.i18n=i18n;
        app.serverAddr="";
		this.setup();
		app.invokeErrorHandler=(code,message)=>{
			if(code==100004){
				window.location.href="/login.html"
			}
        }
        //
        app.token=app.getCookie('token');
	},
	methods:{
		setup(){
            app.serverAddr = [window.location.protocol, '//', window.location.host].join('');
            this.serverAddr = [window.location.protocol, '//', window.location.host].join('');
            const DEBUG = process.env.NODE_ENV === 'development';
			if(!DEBUG){
				if(!window.console) window.console = {};
				var methods = ["log", "debug", "warn", "info"];
				for(var i=0;i<methods.length;i++){
					console[methods[i]] = function(){};
				}
			}
		}
	}
}).$mount('#app')
