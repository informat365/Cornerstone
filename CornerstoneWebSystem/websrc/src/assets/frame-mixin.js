//
window.frameMixin = {
    data: {
        debug: true,
        version: "11-b200120",
        name: "CORNERSTONE",
        serverAddr: "",
        loadingDialog: null,
        dataDicts: [],
        permissionMap: {},
        projectPermissionMap: {},
    },
    //
    methods: {
        dataDictValue(type, value) {
            var s = this.dataDicts[type];
            if (s) {
                for (var i = 0; i < s.length; i++) {
                    if (s[i].value == value) {
                        return s[i].name;
                    }
                }
            }
            console.warn('dataDict not found', type, value);
            return "";
        },
        getHost() {
            var s = window.location.protocol + "//" + window.location.host;
            return s;
        },
        toast(obj) {
            this.$Message.info({
                content: obj,
                duration: 3
            });
        },
        info(message) {
            this.$Modal.info({
                title: "提示",
                content: message
            });
        },
        error(message) {
            this.$Modal.error({
                title: "错误",
                content: message
            });
        },
        //
        hideLoading() {
            if (this.loadingDialog) {
                this.loadingDialog.showDialog = false;
                this.loadingDialog = null;
            }
        },
        //
        showLoading(message) {
            if (this.loadingDialog) {
                this.loadingDialog.showDialog = false;
            }
            this.loadingDialog = this.showDialog(LoadingDialog, {
                message: message
            })
        },
        isReadPermission(permission) {
            return permission.indexOf("_list") > 0 || permission.indexOf("_view") > 0 || permission.indexOf("_log") > 0;
        },
        isProjectAccessiable() {
            // console.log(app.isProjectSet,app.projectId,app.projectList.map(k=>k.id).join("-"))
            // if(app.isProjectSet){
            // 	return true;
            // }
            if (!!app.projectId && !!app.projectList) {
                return Array.contains(app.projectList, app.projectId, k => k.id)
            }
            return false;
        },
        //----------------------------------------------------------------------
        /**
         * 企业权限
         * @param list
         * @returns {boolean}
         */
        perm(list) {
            if (app.account.superBoss === 1 || (app.account.superBoss === 3 && this.isProjectAccessiable())) {
                return true;
            }
            if (typeof list === 'object' && !isNaN(list.length)) {
                //超级boss 1-读写 2-只读 3-部门读写 4-部门读
                if (app.account.superBoss === 2 || (app.account.superBoss === 4 && this.isProjectAccessiable())) {
                    var readPermissionCount = list.filter(item => this.isReadPermission(item)).length;
                    if (readPermissionCount > 0) {
                        return true;
                    }
                }
                for (var i = 0; i < list.length; i++) {
                    var t = list[i];
                    if (this.permissionMap[t] != null) {
                        return true;
                    }
                }
                return false;
            } else {
                if (app.account.superBoss === 2 || (app.account.superBoss === 4 && this.isProjectAccessiable())) {
                    if (this.isReadPermission(list)) {
                        return true;
                    }
                }
                return this.permissionMap[list] != null
            }
        },
        /**
         * 项目权限
         * @param list
         * @returns {boolean}
         */
        prjPerm(list) {
            if (app.account.superBoss === 1 || (app.account.superBoss === 3 && this.isProjectAccessiable())) {
                return true;
            }
            if (typeof list === 'object' && !isNaN(list.length)) {
                //超级boss 1-读写 2-只读
                if (app.account.superBoss === 2 || (app.account.superBoss === 4 && this.isProjectAccessiable())) {
                    var readPermissionCount = list.filter(item => this.isReadPermission(item)).length;
                    if (readPermissionCount > 0) {
                        return true;
                    }
                }
                for (var i = 0; i < list.length; i++) {
                    var t = list[i];
                    if (this.projectPermissionMap[t] != null) {
                        return true;
                    }
                }
                return false;
            } else {
                if (app.account.superBoss === 2 || (app.account.superBoss === 4 && this.isProjectAccessiable())) {
                    if (this.isReadPermission(list)) {
                        return true;
                    }
                }
                return this.projectPermissionMap[list] != null
            }
        },
        //----------------------------------------------------------------------
        confirm(message, callback, cancel, confirmText, cancelText) {
            this.showDialog(window["ConfirmDialog"], {
                message: message,
                callback: callback,
                cancel: cancel,
                confirmText: confirmText,
                cancelText: cancelText
            })
        },
        //
        loadPage(url, args) {
            this.$router.push({path: url, query: args})
        },
        showDialog(objName, args, props) {
            try {
                let dialogClass = objName;
                var i18n = app.i18n;
                var dialogComp = new dialogClass({i18n});
                dialogComp.$mount();
                //挂载完成后设置属性值
                if (typeof props === "object") {
                    Object.keys(props).forEach(key => {
                        dialogComp[key] = props[key];
                    });
                }
                document.body.appendChild(dialogComp.$el)
                dialogComp.showDialog = true
                if (args) {
                    dialogComp.args = args;
                }
                if (dialogComp.pageLoad) {
                    dialogComp.pageLoad();
                }
                return dialogComp;
            }catch (e) {
                console.error(e)
            }
        },
        showDialogByName(objName, args, props) {
            let dialogClass = window[objName];
            var i18n = app.i18n;
            var dialogComp = new dialogClass({i18n});
            dialogComp.$mount();
            //挂载完成后设置属性值
            if (typeof props === "object") {
                Object.keys(props).forEach(key => {
                    dialogComp[key] = props[key];
                });
            }
            document.body.appendChild(dialogComp.$el)
            dialogComp.showDialog = true
            if (args) {
                dialogComp.args = args;
            }
            if (dialogComp.pageLoad) {
                dialogComp.pageLoad();
            }
            return dialogComp;
        },
        previewImages(item, list) {
            this.showDialog(PreviewImage2Dialog, {
                item: item,
                list: list
            })
        },
        async previewFile(uuid, slient) {
            try {
                // await  this.invoke('BizAction.getFileByUuid', [app.token, uuid], info => {
                //      if(info&&info.isDelete){
                //          this.toast("文件已删除，无法查看");
                //          return false;
                //      }else{
                var name = uuid.toLowerCase();
                if (name.indexOf('.png') != -1 ||
                    name.indexOf('.jpg') != -1 ||
                    name.indexOf('.jpeg') != -1 ||
                    name.indexOf('.svg') != -1 ||
                    name.indexOf('.gif') != -1) {
                    app.previewImages(app.serverAddr + '/p/file/get_file/' + uuid + "?token=" + app.token);
                    return true;
                }
                if (name.indexOf('.mp3') != -1 ||
                    name.indexOf('.wav') != -1 ||
                    name.indexOf('.acc') != -1) {
                    app.showDialog(PreviewAudioDialog, {
                        uuid: uuid
                    });
                    return true;
                }
                if (name.indexOf('.mp4') != -1 ||
                    name.indexOf('.ogg') != -1) {
                    app.showDialog(PreviewVideoDialog, {
                        uuid: uuid
                    });
                    return true;
                }
                if (name.indexOf('.pdf') != -1) {
                    app.showDialog(PreviewPdfDialog, {
                        uuid: uuid
                    });
                    return true;
                }
                if (name.indexOf('.sketch') != -1) {
                    window.open('/sketchviewer.html?id=' + uuid);
                    return true;
                }
                if (
                    name.indexOf('.ppt') != -1 ||
                    name.indexOf('.pptx') != -1 ||
                    name.indexOf('.pps') != -1 ||
                    name.indexOf('.ppsx') != -1 ||
                    name.indexOf('.pptm') != -1 ||
                    name.indexOf('.ppam') != -1 ||
                    name.indexOf('.potx') != -1 ||
                    name.indexOf('.ppsm') != -1 ||

                    name.indexOf('.doc') != -1 ||
                    name.indexOf('.docx') != -1 ||
                    name.indexOf('.dotm') != -1 ||
                    name.indexOf('.dotx') != -1 ||

                    name.indexOf('.xls') != -1 ||
                    name.indexOf('.xlsx') != -1 ||
                    name.indexOf('.xlsb') != -1 ||
                    name.indexOf('.xlsm') != -1) {
                    if (app.owaUrl == null || app.owaUrl == '') {
                        if (slient != true) {
                            app.toast("不支持预览此文件格式");
                        }
                        return false;
                    }


                    app.showDialog(PreviewOfficeDialog, {
                        uuid: uuid
                    });
                    return true;
                    // return false;
                }
                if (name.indexOf('.txt') != -1 ||
                    name.indexOf('.json') != -1 ||
                    name.indexOf('.js') != -1 ||
                    name.indexOf('.java') != -1 ||
                    name.indexOf('.js') != -1 ||
                    name.indexOf('.xml') != -1 ||
                    name.indexOf('.cs') != -1 ||
                    name.indexOf('.css') != -1 ||
                    name.indexOf('.swift') != -1 ||
                    name.indexOf('.cpp') != -1 ||
                    name.indexOf('.h') != -1) {
                    app.showDialog(PreviewTextDialog, {
                        uuid: uuid
                    });
                    return true;
                }
                if (slient != true) {
                    app.toast("不支持预览此文件格式");
                }
                return false;
                // });
            } catch (e) {

            }
        },
        downloadFile(uuid) {
            window.open(app.serverAddr + '/p/file/download_attachment/' + uuid + "?token=" + app.token)
        },
        loadComponent(name, args, mountPoint) {
            var compDefine = window[name];
            if (compDefine == null) {
                console.log("can not find " + name);
                return;
            }
            let compClass = compDefine
            var comp = new compClass({i18n: app.i18n}).$mount(mountPoint)
            if (args) {
                comp.args = args;
            } else {
                comp.args = {};
            }
            console.log("load component", name, args)
            if (comp.pageLoad) {
                comp.pageLoad();
            }
            return comp;
        },
        ///
        saveObject(key, obj) {
            if (app.account) {
                key = app.account.id + "_" + app.account.companyId + "_" + key;
            }
            if (obj != null) {
                window.localStorage[key] = JSON.stringify(obj);
            } else {
                window.localStorage.removeItem(key);
            }
        },
        loadObject(key) {
            if (app.account) {
                key = app.account.id + "_" + app.account.companyId + "_" + key;
            }
            var obj = window.localStorage[key];
            if (obj) {
                return JSON.parse(obj);
            }
            return null;
        },
        removeObject(key){
            if (app.account) {
                key = app.account.id + "_" + app.account.companyId + "_" + key;
            }
            window.localStorage.removeItem(key);
        },
        deleteObject(key) {
            if (app.account) {
                key = app.account.id + "_" + key;
            }
            window.localStorage.removeItem(key);
        },
        debounce(fn, delay) {
            var timer
            return function () {
                var context = this
                var args = arguments
                clearTimeout(timer)
                timer = setTimeout(function () {
                    fn.apply(context, args)
                }, delay)
            }
        },
        //----------------------------------------------------------------------
        postMessage(type, content) {
            this.$emit("AppEvent", {
                type: type,
                content: content
            });
        },
        onMessage(type, func) {
            this.$on("AppEvent", func)
        },
        offMessage(type, func) {
            this.$off("AppEvent", func)
        },
        //----------------------------------------------------------------------
        setCookie(name, value) {
            var expireTime = 3600 * 24 * 1000 * 30;
            var exdate = new Date(new Date().getTime() + expireTime)
            document.cookie = name + "=" + escape(value) + ";expires=" + exdate.toGMTString() + ";path=/";
        },
        //
        getCookie(name) {
            var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
            if (arr = document.cookie.match(reg)) {
                return unescape(arr[2]);
            } else {
                return null;
            }
        },
        //
        deleteCookie(name) {
            var exp = new Date();
            exp.setTime(exp.getTime() - 1);
            var cval = app.getCookie(name);
            if (cval != null) {
                document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
            }
        },
        //----------------------------------------------------------------------
        invoke(func, args, success, error, hideLoading) {
            var data = {};
            for (var i = 0; i < args.length; i++) {
                data["arg" + i] = JSON.stringify(args[i]);
            }
            // console.log(">>>invoke:", func, args);
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
                    app.toast("网络连接失败，请稍后重试");
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
                        if (qq[0] == 'RpcException') {
                            qq[2] = "服务器错误，请稍后重试";
                        }
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
                        // console.log("<<<invoke:", func, t);
                        if (success) {
                            success(t)
                        }
                    }
                }
            })
        },
        //判断对象是否为空
        isEmptyOrNull(val) {
            if (!val) {
                return true;
            }
            var str = JSON.stringify(val);
            if(str.length===0||str==="{}"||str==="[]"){
                return true;
            }
            if(Object.isEmpty(val)){
                return true;
            }

            return !!(Array.isArray(val) && Array.isEmpty(val));

        }
        //
    }
}
