<style scoped>
    .chart-container{
        padding:15px;
    }
    .chart-box{
        background-color: #fff;
        border-radius: 5px;
        border:1px solid #eee;
        height:340px;
        overflow: hidden;
    }
    .chart-title{
        font-size:13px;
        padding:15px;
    }
    .count-label{
        color:#999;
        font-size:13px;
    }
    .count-value{
        font-size:20px;
        font-weight: bold;
    }
    .count-value-big{
        font-size:50px;
        margin-top:20px;
    }
    .count-bar{
        background-color: #FBFBFB;
        padding-top:10px;
        padding-bottom: 10px;
    }
    .count-box{
        padding:10px;
        text-align: center;
    }
</style>
<i18n>
{
    "en": {
        "状态统计":"Status statistics",
        "完成度":"Completion Rate",
        "延期率":"Delayed Rate",
        "总数":"Total",
        "已完成":"Finished",
        "未完成":"Unfinished",
        "延期":"Delayed",
        "状态分布":"Status",
        "成员分布":"Member",
        "部门分布": "Department",
        "分类统计":"Category",
        "数量":"Count",
        "总数":"Total",
        "完成":"Finished",
        "任务数（个）": "Works Num"
    },
    "zh_CN": {
        "状态统计":"状态统计",
        "完成度":"完成度",
        "延期率":"延期率",
        "总数":"总数",
        "已完成":"已完成",
        "未完成":"未完成",
        "延期":"延期",
        "状态分布":"状态分布",
        "成员分布":"成员分布",
        "部门分布": "部门分布",
        "分类统计":"分类统计",
        "数量":"数量",
        "总数":"总数",
        "完成":"完成",
        "延期":"延期",
        "任务数（个）": "任务数（个）"
    }   
}
</i18n>
<template>
<div v-if="stat" class="scrollbox">
    <Row>
        <Col span="12" class="chart-container">
            <div class="chart-box">
                <div class="chart-title">{{$t('状态统计')}}</div>
                <Row style="margin-top:20px;">
                    <Col span="12" style="text-align:center">
                        <div style="color:#57D1C9" class="count-value count-value-big numberfont" >
                            {{percent(stat.finishNum,stat.totalNum)}}
                        </div>
                        <div class="count-label">{{$t('完成度')}}</div>
                    </Col>
                    <Col span="12" style="text-align:center">
                        <div  style="color:#ED5485" class="count-value count-value-big numberfont">
                            {{percent(stat.delayNum,stat.totalNum)}}
                        </div>
                        <div  class="count-label">{{$t('延期率')}}</div>
                    </Col>
                </Row>
                <Row style="margin-top:66px;" class="count-bar">
                    <Col span="6" class="count-box">
                        <div class="count-value numberfont">{{stat.totalNum}}</div>
                        <div class="count-label">{{$t('总数')}}</div>
                    </Col>
                    <Col span="6" class="count-box">
                        <div  style="color:#57D1C9" class="count-value numberfont">{{stat.finishNum}}</div>
                        <div  class="count-label">{{$t('已完成')}}</div>
                    </Col>
                    <Col span="6" class="count-box">
                        <div  style="color:#F0B775" class="count-value numberfont">{{stat.totalNum-stat.finishNum}}</div>
                        <div  class="count-label">{{$t('未完成')}}</div>
                    </Col>
                    <Col span="6" class="count-box">
                        <div  style="color:#ED5485"  class="count-value numberfont">
                            <template v-if="stat.delayNum>=0">  {{stat.delayNum}}</template>
                            <template v-if="stat.delayNum<0"> --</template>
                        </div>
                        <div  class="count-label">{{$t('延期')}}</div>
                    </Col>
                </Row>
            </div>
        </Col>
        <Col span="12" class="chart-container">
              <div class="chart-box">
                <div class="chart-title">
                    {{$t('状态分布')}}
                    <span style="margin-left:30px;color:#888;margin-right:5px">显示为漏斗图</span> <i-Switch size="small" v-model="showStatusFunnel" @on-change="changeStatusChart" />
                </div>

                <div style="padding:10px">
                       <v-chart :autoresize="true" :options="statusChartOptions" style="width:100%;height:300px"/>
                 </div>     
              </div>
        </Col>
    </Row>

    <div class="chart-container">
        <div class="chart-box" style="height:380px;">
                <div class="chart-title">{{$t('成员分布')}}</div>
                <div style="padding:10px">
                   <v-chart :autoresize="true" :options="authorChartOptions" style="width:100%;height:300px"/>
                </div>
        </div>
    </div>

    <div class="chart-container">
        <div class="chart-box" style="height:380px;">
            <div class="chart-title">
                {{$t('部门分布')}}
                <Select v-model="departmentLevel" style="display: inline-block; width: 120px; margin-left: 10px" transfer>
                    <Option :key="'dv_'+item.value" v-for="item in departmentLevelList" :value="item.value">{{item.name}}</Option>
                </Select>
            </div>
            <div style="padding:10px">
                <v-chart :autoresize="true" :options="departmentChartOptions" style="width:100%;height:300px"/>
            </div>
        </div>
    </div>
    
    <div v-if="categoryChartOptions.series[0].data.length>0" class="chart-container">
        <div class="chart-box" style="height:380px;">
                <div class="chart-title">
                    {{$t('分类统计')}}
                    <span style="margin-left:30px;color:#888;margin-right:5px">显示为旭日图</span> <i-Switch size="small" v-model="showCatogarySunburst" />
                </div>
                <div style="padding:10px">
                    <v-chart :autoresize="true" :options="categoryChartOptions" style="width:100%;height:300px"/>
                </div>
        </div>
    </div>
 </div>  
</template>

<script>
export default {
  mixins: [componentMixin],
  props:['queryInfo','queryForm'],
  data(){
    return {
        showStatusFunnel:false,
        showCatogarySunburst: false,
        firstLoad: true,
        departmentLevel: 2,
        departmentLevelList: [],
        statusChartOptions:{
                tooltip: {
                    trigger: 'axis'
                }, 
                legend: {
                    data:[]
                },
                series: [{
                    name: this.$t('数量'),
                    data: [],
                    type: 'pie',
                    radius : '55%',
                    itemStyle: {
                        emphasis: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    },
                    label : {
                        normal : {
                            formatter: '{b}:{c}: ({d}%)',
                            textStyle : {
                            fontWeight : 'normal',
                            fontSize : 12
                            }
                        }
                    }
                }]
        },
        authorChartOptions:{
            color: ['#009AF4', '#35E491', '#FF355A'],
            tooltip: {
                trigger: 'axis'
            },
            
            xAxis: {
                data:[],
                type: 'category',
                axisLabel:{
                    'interval':0,
                    rotate:20
                },
            },
            yAxis: {
                type: 'value'
            },
            legend: {
                data:[this.$t('总数'),this.$t('完成'),this.$t('延期')]
            },
            series: [{
                name: this.$t('总数'),
                data: [],
                type: 'bar',
                barWidth : 6,
            },{
                name: this.$t('完成'),
                data: [],
                type: 'bar',
                barWidth : 6,
            },{
                name: this.$t('延期'),
                data: [],
                type: 'bar',
                barWidth : 6,
            }]
        },
        departmentChartOptions:{
            color: ['#009AF4', '#35E491', '#FF355A'],
            tooltip: {
                trigger: 'axis'
            },
            xAxis: {
                data:[],
                type: 'category',
                axisLabel:{
                    'interval':0,
                    rotate:20
                },
            },
            yAxis: {
                type: 'value'
            },
            legend: {
                data:[this.$t('总数'),this.$t('完成'),this.$t('延期')]
            },
            series: [{
                name: this.$t('总数'),
                data: [],
                type: 'bar',
                barWidth : 6,
            },{
                name: this.$t('完成'),
                data: [],
                type: 'bar',
                barWidth : 6,
            },{
                name: this.$t('延期'),
                data: [],
                type: 'bar',
                barWidth : 6,
            }]
        },
        categoryChartPieOptions:{
                tooltip: {
                    trigger: 'axis'
                }, 
                
                legend: {
                    data:[]
                },
                series: [{
                    name: this.$t('数量'),
                    data: [],
                    type: 'pie',
                    radius : '55%',
                    itemStyle: {
                        emphasis: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    },
                    label : {
                        normal : {
                            formatter: '{b}:{c}: ({d}%)',
                            textStyle : {
                            fontWeight : 'normal',
                            fontSize : 12
                            }
                        }
                    }
                }]
        },
        categoryChartSunOptions: {
            silent: false,
            visualMap: {
                type: 'continuous',
                text:[this.$t('任务数（个）')],
                min: 1,
                inRange: {
                    color: ['#2D5F73', '#538EA6', '#F2D1B3', '#F2B8A2', '#F28C8C']
                },
                formatter: (value) => {
                    return `${value}(${this.percent(value, this.stat.totalNum)})`;
                }
            },
            series: [{
                type: 'sunburst',
                highlightPolicy: 'ancestor',
                data: [],
                radius: ['15%', '80%'],
                label: {
                    rotate: 'radial',
                },
                levels: [],
                itemStyle: {
                    borderWidth: 2
                },
                emphasis: {
                    label: {
                        show: true,
                        formatter: (params) => {
                            return `${params.name}(${this.percent(params.value, this.stat.totalNum)})`;
                        }
                    }
                }
            }]
        },
        title:"TaskStatView",
        colorsStatus:['#D0DFE6','#F84F84'],
        colorsAccount:['#2CD3C9','#D0DFE6','#F84F84'],
        stat:null,
    }
  },
  computed: {
      categoryChartOptions() {
          return this.showCatogarySunburst ? this.categoryChartSunOptions : this.categoryChartPieOptions;
      }
  },
  watch: {
      departmentLevel() {
          this.loadData();
      }
  },
  methods:{
      pageMessage(type){
        if(type=='task.edit'){
          this.loadData();
        }
      },
      changeStatusChart(){
        if(this.showStatusFunnel){
            this.statusChartOptions.series[0].type="funnel";
        }else{
            this.statusChartOptions.series[0].type="pie";
        }  
      },
      percent(v1,v2){
          if(v1<0){
              return '--';
          }
          if(v2==0){
              return "--";
          }
          return (v1*100/v2).toFixed(1)+"%";
      },
      loadData(){
        app.invoke("BizAction.getTaskStat",[app.token,{...this.queryForm, departmentLevel: this.departmentLevel}],info => {
            this.stat=info;
            this.statusChartOptions.series[0].data=[];
            this.statusChartOptions.legend.data=[]
            for(var i=0;i<info.statusStatList.length;i++){
                var t=info.statusStatList[i];
                this.statusChartOptions.legend.data.push(t.name)
                this.statusChartOptions.series[0].data.push({
                    name:t.name,
                    value:t.totalNum
                })
            }

            const departmentLevelList = [];
            for(let i = 1; i <= info.maxDepartmentLevel;i++) {
                departmentLevelList.push({
                    name: `第${i}层级`,
                    value: i
                });
            }
            this.departmentLevelList = departmentLevelList;
            if(this.firstLoad) {
                if(this.departmentLevelList.length >= 2) {
                    this.departmentLevel = 2;
                } else {
                    this.departmentLevel = 1;
                }
            }
            
            
            this.authorChartOptions.series[0].data=[];
            this.authorChartOptions.series[1].data=[];
            this.authorChartOptions.series[2].data=[];
            this.authorChartOptions.xAxis.data=[]
            for(var i=0;i<info.accountStatList.length;i++){
                var t=info.accountStatList[i];
                this.authorChartOptions.xAxis.data.push(t.name)
                this.authorChartOptions.series[0].data.push(t.totalNum)
                this.authorChartOptions.series[1].data.push(t.finishNum)
                this.authorChartOptions.series[2].data.push(t.delayNum)
            }
            if(info.accountStatList.length >= 15) {
                this.authorChartOptions.dataZoom = [
                    {
                        type: 'slider',
                        show: true,
                        height: 20,
                        bottom: 10,
                        xAxisIndex: 0,
                        handleIcon: 'M10.7,11.9H9.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4h1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9z M13.3,24.4H6.7V23h6.6V24.4z M13.3,19.6H6.7v-1.4h6.6V19.6z',
                        handleSize: '80%',
                    },
                    {
                        type: 'inside',
                        xAxisIndex: 0
                    }
                ];
                this.authorChartOptions.grid = {
                    bottom: 80
                }
            }

            this.departmentChartOptions.series[0].data=[];
            this.departmentChartOptions.series[1].data=[];
            this.departmentChartOptions.series[2].data=[];
            this.departmentChartOptions.xAxis.data=[]
            for(var i=0;i<info.departmentStatList.length;i++){
                var t=info.departmentStatList[i];
                if(t.totalNum > 0) {
                    this.departmentChartOptions.xAxis.data.push(t.name)
                    this.departmentChartOptions.series[0].data.push(t.totalNum)
                    this.departmentChartOptions.series[1].data.push(t.finishNum)
                    this.departmentChartOptions.series[2].data.push(t.delayNum)
                }
            }
            if(this.departmentChartOptions.xAxis.data >= 15) {
                this.departmentChartOptions.dataZoom = [
                    {
                        type: 'slider',
                        show: true,
                        height: 20,
                        bottom: 10,
                        xAxisIndex: 0,
                        handleIcon: 'M10.7,11.9H9.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4h1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9z M13.3,24.4H6.7V23h6.6V24.4z M13.3,19.6H6.7v-1.4h6.6V19.6z',
                        handleSize: '80%',
                    },
                    {
                        type: 'inside',
                        xAxisIndex: 0
                    }
                ];
                this.departmentChartOptions.grid = {
                    bottom: 80
                }
            }
            //
            this.categoryChartSunOptions.series[0].data= info.categoryStatSunList || [];
            this.categoryChartPieOptions.series[0].data=[];
            this.categoryChartPieOptions.legend.data=[]
            for(var i=0;i<info.categoryStatList.length;i++){
                var t=info.categoryStatList[i];
                this.categoryChartPieOptions.legend.data.push(t.name)
                this.categoryChartPieOptions.series[0].data.push({
                    name:t.name,
                    value:t.totalNum
                })
            }

            this.categoryChartSunOptions.visualMap.max = info.totalNum || 1;
            this.firstLoad = false;
        });
    }
}
}
</script>