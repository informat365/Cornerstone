<style scoped>
    .card-main-content{
        min-width:500px;
        width:100%;
        flex:1;
    }
    .left-day{
       font-weight: bold;
       color:#17A7ED;
       margin-left:5px;
   }
   .left-overdue{
       color:#F84F84;
   }
</style>
<template>
    <CardBaseView :card="card" desc="">
        <div slot="content" class="card-main-content">
        <table class="table-content table-color" style="table-layout:fixed">
                <thead>
                    <tr>
                        <th style="width:220px;">名称</th>
                        <th style="width:220px;">进度</th>
                        <th style="width:250px">迭代</th>
                    </tr>     
                  </thead>

                    <tbody>
                        <tr v-for="item in list" :key="'prj_'+item.id" class="table-row">
                            <td class="text-no-wrap" @click="showProject(item)">
                                <span class="table-col-name">{{item.name}}</span>
                            </td>
                            <td >
                                <div v-if="item.runStatus>0"> <Progress :percent="item.progress" /></div>
                                <div v-if="item.runStatus>0" class="table-remark">  <ProjectStatusLabel v-model="item.runStatus"></ProjectStatusLabel></div>
                            </td>
                            <td>
                              <div v-if="item.iteration.id>0">
                                    <div class="table-info-row"><DataDictLabel  type="ProjectIteration.status" :value="item.iteration.status"></DataDictLabel>
                                    <span style="margin-left:5px">{{item.iteration.name}}</span>
                                    </div>
                                    <div class="table-remark">
                                            {{item.iteration.startDate|fmtDate}}
                                            ~
                                            {{item.iteration.endDate|fmtDate}}
                                            <template v-if="item.iteration.status!=3">
                                                <span class="left-day" v-if="getLeftDays(item.iteration.endDate)>0">
                                                    剩余{{getLeftDays(item.iteration.endDate)}}天
                                                </span>
                                                <span class="left-day" v-if="getLeftDays(item.iteration.endDate)==0">
                                                    今天到期
                                                </span>
                                                <span  class="left-day left-overdue" v-if="getLeftDays(item.iteration.endDate)<0">
                                                    超期{{getLeftDays(item.iteration.endDate)*-1}}天 
                                                </span>
                                            </template>
                                        </div>
                                   


                                </div>
                            </td>
                           
                        </tr>
                      
                    </tbody>
        </table>
        


        </div>
    </CardBaseView>
</template>

<script>
export default {
    mixins: [componentMixin],
    props:['card'],
    data(){
        return {
            list:[]
        }
    },  
    mounted(){
        this.setupData();
    },
    watch:{
        card(val){
            this.setupData();
        }
    },
    methods:{
        setupData(){
            var data=JSON.parse(this.card.cardData);
            this.list=data.dataList;
        },
        getLeftDays(date){
            return getLeftDays(date)
        },
        showProject(item){
            app.loadPage('/pm/project/'+item.uuid+'/project')
        },
    }
}
</script>