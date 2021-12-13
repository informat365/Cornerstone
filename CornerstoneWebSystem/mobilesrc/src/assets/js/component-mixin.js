import Vue from 'vue'

window.formatByteSize=function(value){
    if(value==null){
        return "";
    }
    if(value<1024){
        return value+"B";
    }
    if(value<1024*1024){
        return (parseFloat(value/1024)).toFixed(2)+"KB";
    }
    if(value<1024*1024*1024){
        return (parseFloat(value/1024/1024)).toFixed(2)+"MB";
    }
    var q=parseFloat(value/(1024*1024*1024));
    return q.toFixed(2)+"GB";
};
//公共函数
window.formatNumber=function(n){
    n = n.toString();
    return n[1] ? n : '0' + n
};
window.fmtDeltaTime=function(t){
    if(t==null){
        return "-";
    }
    var date=new Date(t);
    var now=new Date();
    var diff=now.getTime()-date.getTime();
    if(diff<=24*3600*60*1000&&now.getDay()==date.getDay()){
        //same day
        return formatTime(t);
    }
    if(now.getFullYear()==date.getFullYear()){
        return formatMonthDatetime(t);
    }
    return formatDatetime(t); 
},
window.formatTime=function(t){
    if(t==null){
        return "-";
    }
    var date=new Date(t);
    var hour = date.getHours();
    var minute = date.getMinutes();
    return [hour, minute].map(formatNumber).join(':')
};
window.formatMonthDatetime=function(t){
    if(t==null){
        return "-";
    }
    var date=new Date(t);
    var month = date.getMonth() + 1;
    var day = date.getDate();
    var hour = date.getHours();
    var minute = date.getMinutes();
    return [month, day].map(formatNumber).join('-') + ' ' + [hour, minute].map(formatNumber).join(':')
};
window.formatDatetime=function(t){
    if(t==null){
        return "-";
    }
    var date=new Date(t);
    var year = date.getFullYear()>2000?date.getFullYear()-2000:date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();
    var hour = date.getHours();
    var minute = date.getMinutes();
    return [year, month, day].map(formatNumber).join('-') + ' ' + [hour, minute].map(formatNumber).join(':')
};
window.formatUTCDate=function(t){
    if(t==null||t==''){
        return "-"
    }
    var date=Date.parse(t);
    return window.formatDate(date);
}
window.formatUTCDatetime=function(t){
    if(t==null||t==''){
        return "-"
    }
    var date=Date.parse(t);
    return window.formatDatetime(date);
}
window.formatDate=function(t){
    if(t==null){
        return "-";
    }
    var date=new Date(t);
    var year = date.getFullYear()>2000?date.getFullYear()-2000:date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();
    var now=new Date();
    if(now.getFullYear()==date.getFullYear()){
        return [month, day].map(formatNumber).join('-');
    }
    return [year, month, day].map(formatNumber).join('-');
};
window.dateSameDay=function(date1,date2){
    return formatDate(date1)==formatDate(date2);
}
window.dateSameWeek=function(date1,date2){
    var oneDayTime = 1000*60*60*24;
	var old_count =parseInt(date1.getTime()/oneDayTime);
	var now_other =parseInt(date2.getTime()/oneDayTime);
    return parseInt((old_count+4)/7) == parseInt((now_other+4)/7);
}
window.getWeekNum=function(date) {
    var target = new Date(date.valueOf()),
    dayNumber = (date.getUTCDay() + 6) % 7,
    firstThursday;
    target.setUTCDate(target.getUTCDate() - dayNumber + 3);
    firstThursday = target.valueOf();
    target.setUTCMonth(0, 1);
    if (target.getUTCDay() !== 4) {
        target.setUTCMonth(0, 1 + ((4 - target.getUTCDay()) + 7) % 7);
    }
    return Math.ceil((firstThursday - target) /  (7 * 24 * 3600 * 1000)) + 1;
}
window.copyObject=function(a,b){
    var options = Object.assign({}, a, b);
    return options;
}
//
//表单验证
window.vd={
    req:{required:true,message:"必选项"},
    rangeDate:{type:'array',length:2,required:true,message:"必选项"},
    name:{type: 'string', min: 2,max:50,message:"长度为2-50个字"},
    name2_100:{type: 'string', min: 2,max:100,message:"长度为2-100个字"},
    name2_10:{type: 'string', min: 2,max:10,message:"长度为2-10个字"},
    name1_10:{type: 'string', min: 1,max:10,message:"长度为1-10个字"},
    desc:{type: 'string', min: 4,max:200,message:"长度为4-200个字"},
    intValue:{type: 'integer',min:1,message:"必须为正整数"}   ,
    port:{type: 'integer',min:1,max:65535,message:"端口范围1~65535"}   ,
    mobile:{type: 'string',length:11,message:"手机号格式错误"},
    email:{type: 'email',message:"邮箱格式错误"},
    password:{type: 'string',min: 8,max:12,message:"密码为8-12个数字和字母的组合"},
};
//
//
window.travalTree=function(node,callback){
    callback(node);
    if(node.children!=null){
        for(var i=0;i<node.children.length;i++){
            travalTree(node.children[i],callback)
        }
      }
},
window.dateDiff=function(dt1,dt2){
    return Math.floor((Date.UTC(dt2.getFullYear(), dt2.getMonth(), dt2.getDate())
         - Date.UTC(dt1.getFullYear(), dt1.getMonth(), dt1.getDate()) ) /(1000 * 60 * 60 * 24));            
},
window.getLeftDays=function(value) {
    if(value==null){
        return 0;
    }
    return dateDiff(new Date(),new Date(value))
},
Vue.filter('leftDays', function (v) {
    return getLeftDays(v);
})
//
Vue.filter('fmtPercent', function (v) {
    return parseFloat(v.toFixed(2));
})
Vue.filter('fmtPercentStr', function (v1,v2) {
    if(v1<0){
        return '--';
    }
    if(v2==0){
        return "--";
    }
    return (v1*100/v2).toFixed(1)+"%";
})
Vue.filter('fmtMoney', function (v) {
    var v=Number(v)/100;
    return parseFloat(v.toFixed(2));
})
Vue.filter('fmtDateTime', function (v) {
    return formatDatetime(v);
})
Vue.filter('fmtDate', function (v) {
    return formatDate(v);
})

Vue.filter('fmtUTCDateTime', function (v) {
    return formatUTCDatetime(v);
})
Vue.filter('fmtUTCDate', function (v) {
    return formatUTCDate(v);
})


Vue.filter('fmtDeltaTime', function (v) {
    return fmtDeltaTime(v);
})

Vue.filter('dateDiff', function (v) {
    if(v==0){
        return "-";
    }
    var t2 = new Date().getTime()
    var t1 = v;
    var r= parseInt((t2-t1)/(24*3600*1000));
    if(r==0){
        return 1;
    }
    return r;
})
Vue.filter('fmtBool', function (t) {
    return t==true?"是":"否"
})
Vue.filter('imgurl', function (t) {
    if(t==null||t==""){
        return "./image/common/placeholder.png";
    }
    return app.serverAddr+"/p/file/get_file/"+t;
})
Vue.filter('userimgurl', function (t) {
    if(t==null||t==""){
        return "./image/common/account-placeholder.png";
    }
    return app.serverAddr+"/p/file/get_file/"+t;
})
Vue.filter('dataDict', function (value,type) {
    if(value==null){
        return "";
    }
    return app.dataDictValue(type,value)
})
Vue.filter('fmtBytes', function (value) {
    return formatByteSize(value);
})
//
Vue.filter('objectTypeName', function (t) {
    return app.dataDictValue('Task.objectType',t)
})
//
Vue.directive('focus', {
  inserted: function (el) {
    el.focus()
  }
})

Vue.directive('click-outside', {
    bind (el, binding, vnode) {
        function documentHandler (e) {
            if (el.contains(e.target)) {
                return false;
            }
            if (binding.expression) {
                binding.value(e);
            }
        }
        el.__vueClickOutside__ = documentHandler;
        document.addEventListener('click', documentHandler);
    },
    update () {

    },
    unbind (el, binding) {
        document.removeEventListener('click', el.__vueClickOutside__);
        delete el.__vueClickOutside__;
    }
})
//

if (!Array.indexOf) {
    Array.prototype.indexOf = function(elem) {
      var i=0,
          length=this.length;
      for (; i < length; i++) {
          if (this[i] === elem ) {
            return i;
          }
      }
      return -1;
    };
}
  // Add remove method to Array.
Array.prototype.remove = function(elem) {
    return this.splice(this.indexOf(elem), 1);
}
Array.prototype.contains=function(value){
    var contains=false;
    this.map(item=>{
        if(item==value){
            contains=true;
        }
    });
    return contains;
}
Array.prototype.containsKey=function(key,value){
    var contains=false;
    this.map(item=>{
        if(item[key]==value){
            contains=true;
        }
    });
    return contains;
}
//
Array.prototype.containsId=function(id){
    var contains=false;
    this.map(item=>{
        if(item.id==id){
            contains=true;
        }
    });
    return contains;
}
//
window.componentMixin = {
	data(){
		return {
            title:"undefined",
            args:{},
		}
	},
	mounted(){
        this.$addEventListener()
	},
    beforeDestroy(){
        this.$removeEventListener();
    },
	methods: {
        $addEventListener(){
            app.onMessage('AppEvent',this.$receiveMessage)
        },
        $removeEventListener(){
      	    app.offMessage('AppEvent',this.$receiveMessage)
        },
		$receiveMessage(t){
			if(this.pageMessage){
                console.log("pageMessage",t.type,this.title)
				this.pageMessage(t.type,t.content);
			}
        },
        perm(list){
            return app.perm(list);
        },
        prjPerm(list){
            return app.prjPerm(list);
        }

	}
}

