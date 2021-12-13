import Vue from 'vue'

//
window.frameMixin = {
	data: {
		debug: true,
		version: "1.0",
		name:"CORNERSTONE",
		serverAddr: "",
		loadingDialog:null,
        dataDicts:[],
        permissionMap:{},
        projectPermissionMap:{},
	},
	//
	methods: {
		dataDictValue(type,value){
			var s=this.dataDicts[type];
            if(s){
                for(var i=0;i<s.length;i++){
                    if(s[i].value==value){
                        return s[i].name;
                    }
                }
			}
			console.warn('dataDict not found',type,value);
            return "";
		},
		getHost(){
			var s=window.location.protocol+"//"+window.location.host;
			return s;
		},
		toast (obj) {
            this.$vux.toast.text(obj)
		},
		//
		hideLoading () {
			this.$vux.loading.hide()
		},
		//
		showLoading (message) {
            if(message==null){
                message="加载中";
            }
            this.$vux.loading.show({
                text: message
            })
        },
    	//----------------------------------------------------------------------
        perm(list){
            if(typeof list === 'object' && !isNaN(list.length)){
                for(var i=0;i<list.length;i++){
                    var t=list[i];
                    if(this.permissionMap[t]!=null){
                        return true;
                    }
                }
                return false;
            }else{
                
                return this.permissionMap[list]!=null
            }
        },
        prjPerm(list){
            if(typeof list === 'object' && !isNaN(list.length)){
                for(var i=0;i<list.length;i++){
                    var t=list[i];
                    if(this.projectPermissionMap[t]!=null){
                        return true;
                    }
                }
                return false;
            }else{
                return this.projectPermissionMap[list]!=null
            }
        },
		//----------------------------------------------------------------------
		confirm (message,callback) {
            this.$vux.confirm.show({
                content:message,
                onCancel () {},
                onConfirm () {
                    if(callback){
                        callback();
                    }
                }
            })
		},
		//
		loadPage (url, args) {
			this.$router.push({ path: url, query: args })
        },
        popPage () {
			this.$router.go(-1);
        },
		showDialog (objName,args) {
			let dialogClass = objName;
			var dialogComp = new dialogClass().$mount()
			document.body.appendChild(dialogComp.$el)
			dialogComp.showDialog = true
			if(args){
				dialogComp.args=args;
			}
			if(dialogComp.pageLoad){
				dialogComp.pageLoad();
			}
			return dialogComp;
		},
		previewImages(item,list) {
			this.showDialog(PreviewImageDialog,{
				item:item,
				list:list
			})
		},
		
		loadComponent(name,args,mountPoint){
			var compDefine=window[name];
			if(compDefine==null){
				console.log("can not find "+name); 
				return;
			}
			let compClass = compDefine
			var comp = new compClass().$mount(mountPoint)
			if(args){
				comp.args=args;
			}else{
				comp.args={};
			}
			console.log("load component",name,args)
			if(comp.pageLoad){
                comp.pageLoad();
			}
			return comp;
		},
		///
		saveObject(key,obj){
			if(obj!=null){
				window.localStorage[key]=JSON.stringify(obj);
			}else{
				window.localStorage.removeItem(key);
			}
		},
		loadObject(key){
			var obj=window.localStorage[key];
            if(obj){
                return JSON.parse(obj);
			}
			return null;
        },
        deleteObject(key){
            window.localStorage.removeItem(key);
        },
        debounce:function(func, wait, immediate) {
            var timeout, args, context, timestamp, result;
            var later = function() {
              var last = new Date().getTime() - timestamp;
              if (last < wait && last > 0) {
                timeout = setTimeout(later, wait - last);
              } else {
                timeout = null;
                if (!immediate) {
                  result = func.apply(context, args);
                  if (!timeout) context = args = null;
                }
              }
            };
          
            return function() {
              context = this;
              args = arguments;
              timestamp = new Date().getTime();
              var callNow = immediate && !timeout;
              if (!timeout) timeout = setTimeout(later, wait);
              if (callNow) {
                result = func.apply(context, args);
                context = args = null;
              }
          
              return result;
            };
        },
		//----------------------------------------------------------------------
		postMessage (type, content) {
			this.$emit("AppEvent",{
				type:type,
				content:content
			});
		},
		onMessage(type,func){
			this.$on("AppEvent",func)
		},
		offMessage(type,func){
			this.$off("AppEvent",func)
		},
		//----------------------------------------------------------------------
		setCookie (name, value) {
			var expireTime=3600*24*1000*30;
			var exdate = new Date(new Date().getTime() + expireTime)
			document.cookie = name + "=" + escape(value) + ";expires=" + exdate.toGMTString();
		},
		//
		getCookie (name) {
			var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
			if (arr = document.cookie.match(reg)) {
				return unescape(arr[2]);
			} else {
				return null;
			}
		},
		//
		deleteCookie (name) {
			var exp = new Date();
			exp.setTime(exp.getTime() - 1);
			var cval = app.getCookie(name);
			if (cval != null) {
				document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
			}
		},
		//----------------------------------------------------------------------
		invoke (func, args, success, error, hideLoading) {
			var data = {};
			for (var i = 0; i < args.length; i++) {
				data["arg" + i] = JSON.stringify(args[i]);
			}
			console.log(">>>invoke:", func, args);
			if (hideLoading == null) {
				var timeoutId = setTimeout(function () {
					app.showLoading('正在加载');
				}, 1000);
			}//
			ajax().post(app.serverAddr + '/p/api/invoke/' + func, data).always(function (response, xhr) {
				clearTimeout(timeoutId);
				app.hideLoading();
				//
				if (xhr.status != 200) {
					if(hideLoading==null){
                        app.toast("网络连接失败，请稍后重试");
                    }
					if (error) {
						error(0, "网络连接失败，请稍后重试");
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
						if (app.invokeErrorHandler) {
							app.invokeErrorHandler(qq[1], errorString)
						}
						if (error) {
							error(qq[1], errorString);
						}
						app.toast("" + errorString);
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
		//
	}
}

