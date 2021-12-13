<style scoped>
    .header-th{
        position: relative;
    }
    .sorter{
        position: absolute;
        right:-15px;
        top:0px;
        color:#0094FB;
    }
    .header-label{
        max-width:90px;
        display:inline-block;
        line-height:1;
        vertical-align: middle;
        cursor: pointer;
    }
    .table-header-box{
        position:relative;
        display:inline-block;
        vertical-align: middle;
    }
    .resize-box{
        display: inline-block;
        position:absolute;
        top:0;
        right:0;
        height:35px;
        width:3px;
        cursor:col-resize;
    }
    .resizeing{
        border-right:1px solid #009AF4;
    }
</style>
<template>
   <th class="header-th" :class="{'resizeing':resizeing}" :style="{width:(width==null?'auto':width+'px')}">
    <span @click="setSortType" class="table-header-box">
        <span class="text-no-wrap header-label">{{label}}</span>
        <span  v-if="sort" class="sorter">
            <i class="ivu-icon ivu-icon-md-arrow-round-up" v-if="sortType==1&&sort.name==name"></i>
            <i class="ivu-icon ivu-icon-md-arrow-round-down" v-if="sortType==2&&sort.name==name"></i>
        </span>
    </span>
    <span @mousedown.stop = drag($event) class="resize-box"></span>
   </th>
</template>
<script>
export default {
    name:"TableHeader",
    props: ['label','name','sort','widthSetting','field'],
    data (){
        return{
           sortType:this.sort.sort,
           width:this.widthSetting==null?null:this.widthSetting[this.field],
           resizeing:false
        }
    },
    watch: {
        sortType(val) {
            this.$emit('input', val);
        },
        sort (val) {
            this.sortType = val.sort;
        },
        widthSetting(val){
            this.width=this.widthSetting[this.field]
        },
        field(val){
            this.width=this.widthSetting[this.field]
        },
    },
    methods:{
        setSortType: function (val) {
            if(this.sort==null){
                return;
            }
            if(this.sort.name==null||this.sort.name!=this.name){
                this.sortType=1;
                this.sort.sort=this.sortType;
                this.sort.name=this.name;
                this.$emit('change')
                return;
            }
            if(this.sortType==0||this.sortType==1){
                this.sortType=this.sortType+1;
                this.sort.sort=this.sortType;
                this.sort.name=this.name;
                this.$emit('change')
                return;
            }
            //
            if(this.sortType==2){
                this.sortType=0;
                this.sort.sort=this.sortType;
                this.sort.name=null;
                this.$emit('change')
            }
        },
        drag(e){
            var startX=e.clientX;
            var startWidth=this.widthSetting[this.field];
            this.resizeing=true;
            var mouseMove=app.debounce((ev)=>{
                var moveX=ev.clientX;
                var offset= moveX - startX;
                var width=startWidth+offset;
                if(width<80){
                    width=80;
                }
                this.$set(this.widthSetting,this.field,width)
                this.width=width;
                //document.body.style.webkitTransform = 'scale(1)';
                
            },60)
            document.onmousemove = mouseMove
            document.onmouseup =  () =>{
                document.onmousemove = null;
                document.onmouseup = null;
                this.resizeing=false;
            };
        }
    }
}
</script>