<style scoped>
   .menu-item-group{
        font-size:12px;
        color:#999;
         padding:8px 0px;
        margin-top:20px;
    }
    .filter-tag{
        font-size:13px;
        padding:3px 10px;
        background-color: #EEEEEE;
        color:#777;
        display: inline-block;
        margin-right: 10px;
        margin-top:10px;
        border-radius: 13px;
        cursor: pointer;
        user-select: none;
         max-width: 200px;
        text-overflow:ellipsis;
        overflow: hidden;
        white-space: nowrap;
    }
    .filter-count{
        font-size:12px;
        margin-left: 5px;
        font-weight: bold;
    }
    .filter-tag-select{
        background-color: #009CF1;
        color:#fff;
    }
</style>
<i18n>
{
    "en": {
        "日期筛选": "Date filter",
        "星标筛选": "Star filter",
        "分组筛选": "Group filter",
        "已超期":"Overdue",
        "本周到期":"This Week",
        "今日到期":"Today",
        "未规划迭代":"Unplanned",
        "星标项目":"Star"
    },
    "zh_CN": {
        "日期筛选": "日期筛选",
        "星标筛选": "星标筛选",
        "分组筛选": "分组筛选",
        "已超期":"已超期",
        "本周到期":"本周到期",
        "今日到期":"今日到期",
        "未规划迭代":"未规划迭代",
        "星标项目":"星标项目"
    }
}
</i18n>
<template>
<div>
      <div class="menu-item-group">{{$t('日期筛选')}}</div>
                <div :class="{'filter-tag-select':projectFilters.dateType==1}"  @click="setProjectDateFilter(1)" class="filter-tag">{{$t('已超期')}}</div>
                <div :class="{'filter-tag-select':projectFilters.dateType==2}"  @click="setProjectDateFilter(2)" class="filter-tag">{{$t('本周到期')}}</div>
                <div :class="{'filter-tag-select':projectFilters.dateType==3}"  @click="setProjectDateFilter(3)" class="filter-tag">{{$t('今日到期')}}</div>
                <div :class="{'filter-tag-select':projectFilters.dateType==4}"  @click="setProjectDateFilter(4)" class="filter-tag">{{$t('未规划迭代')}}</div>

                <div class="menu-item-group">{{$t('星标筛选')}}</div>
                <div :class="{'filter-tag-select':projectFilters.starProject}"  @click="toggleStar" class="filter-tag">{{$t('星标项目')}}</div>

                <div class="menu-item-group">{{$t('分组筛选')}}</div>
                <div v-for="group in groupList" :key="'g'+group" :class="{'filter-tag-select':projectFilters.group==group}"
                    @click="toggleGroup(group)" class="filter-tag">{{group}}</div>
</div>
</template>

<script>
export default {
    mixins: [componentMixin],
    props:["datas"],
    data(){
        return {
            projectFilters:{
                dateType:null,
                starProject:false,
                group:null,
            },
            projectList:[],
            groupList:[],
        }
    },
    mounted(){
        this.loadProjectData()
    },
    methods:{
        filteredProjectList(){
            var list=[];
            for(var i=0;i<this.projectList.length;i++){
                var t=this.projectList[i];
                if(this.projectFilters.group!=null){
                    if(t.group!=this.projectFilters.group){
                        continue;
                    }
                }
                if(this.projectFilters.dateType==1){
                    if(t.iteration.status==3){
                        continue;
                    }
                    if(t.iteration.endDate==null||getLeftDays(t.iteration.endDate)>=0){
                        continue;
                    }
                }
                if(this.projectFilters.dateType==2){
                    if(t.iteration.endDate==null||!dateSameWeek(new Date(t.iteration.endDate),new Date())){
                        continue;
                    }
                }
                if(this.projectFilters.dateType==3){
                    if(t.iteration.endDate==null||!dateSameDay(new Date(t.iteration.endDate),new Date())){
                        continue;
                    }
                }
                if(this.projectFilters.dateType==4){
                    if(t.iteration.endDate!=null){
                        continue;
                    }
                }
                if(this.projectFilters.starProject){
                    if(t.star==false){
                        continue;
                    }
                }
                list.push(t);
            }
            return list;
        },
        setProjectDateFilter(value){
            if(this.projectFilters.dateType==value){
                this.projectFilters.dateType=null;
            }else{
                this.projectFilters.dateType=value;
            }
            this.$emit('list-changed',this.filteredProjectList())
        },
        toggleStar(){
            this.projectFilters.starProject=!this.projectFilters.starProject;
            this.$emit('list-changed',this.filteredProjectList())
        },
        toggleGroup(group){
            if(this.projectFilters.group==group){
                this.projectFilters.group=null;
            }else{
                this.projectFilters.group=group;
            }
            this.$emit('list-changed',this.filteredProjectList())
        },
        reloadData(){
            this.loadProjectData();
        },
        loadProjectData(){
            var allGroupList=[];
            for(var i=0;i<this.datas.length;i++){
                var t=this.datas[i];
                if(t.activityCount){
                    t.activityCount=JSON.parse(t.activityCount);
                }
                if(t.group){
                    if(allGroupList[t.group]==null){
                        allGroupList[t.group]=true;
                        this.groupList.push(t.group);
                    }
                }
            }
            this.projectList=this.datas;
            //
            this.$emit('show-create',list.length==0)
            this.$emit('list-changed',this.filteredProjectList())
        }
    }
}
</script>
