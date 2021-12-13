<style scoped>

.status-label{
    display: inline-block;
    padding: .25em .6em;
    font-size: 12px;
    font-weight: 600;
    line-height: 1;
    text-align: center;
    white-space: nowrap;
    vertical-align: middle;
    border-radius: 1rem;
    color: #fff;
}
.status-label-category{
    border-radius: 2px;
}

</style>
<template>
   <span class="status-label"
    :class="{'status-label-category':category}"
    :style="{backgroundColor:color}">

    <Icon v-if="icon" :type="icon" />
    {{itemText}}

    </span>
</template>
<script>
export default {
    name:"DataDictLabel",
    props: ['value','type','category'],
    data (){
        return{
            dictIcons:{
                'ProjectPipelineRunLog.status.3':'md-checkmark-circle',
                'ProjectPipelineRunLog.status.4':'md-close-circle',

                'Report.status.3':'md-checkmark-circle',

                'ProjectIteration.status.1':'md-time',
                'ProjectIteration.status.2':'ios-paper-plane',
                'ProjectIteration.status.3':'md-checkmark-circle',

                'Project.status.1':"ios-paper-plane",
                'Project.status.2':"md-checkmark-circle",
            },
            dictColors:{
                'ProjectIteration.status.1':'#1794A5',
                'ProjectIteration.status.2':'#6abe83',
                'ProjectIteration.status.3':'#dddfe6',

                'ProjectSubSystem.status.1':'#6abe83',
                'ProjectSubSystem.status.2':'#dddfe6',

                'TestPlanTestCase.status.1':'#gray',
                'TestPlanTestCase.status.2':'#2CD3C9',
                'TestPlanTestCase.status.3':'#F84F84',
                'TestPlanTestCase.status.4':'#333',

                'ProjectPipelineRunLog.status.1':'#gray',
                'ProjectPipelineRunLog.status.2':'#75BFD2',
                'ProjectPipelineRunLog.status.3':'#2BDE73',
                'ProjectPipelineRunLog.status.4':'red',

                'Account.status.1':"#0097F7",
                'Account.status.2':"#ccc",

                'Project.status.1':"#0097F7",
                'Project.status.2':"#ccc",

                'Common.status.1':"#0097F7",
                'Common.status.2':"#ccc",

                'ReportTemplate.period.1':"#a39391",
                'ReportTemplate.period.2':"#716e77",
                'ReportTemplate.period.3':"#ecd6c7",

                'Report.status.1':'#EB6E44',
                'Report.status.2':'#0063DC',
                'Report.status.3':'#9dd3a8',

                'Stage.status.1':"#1794A5",
                'Stage.status.2':"#F84F84",
                'Stage.status.3':"#dddfe6",


                'WikiPage.type.1':"#2E94B9",
                'WikiPage.type.2':"#F0B775",
                'WikiPage.type.3':"#D25565",
                'WikiPage.type.4':"#a39391",
                'WikiPage.type.5':"#5BE7C4",

            }
        }
    },
    computed: {
        itemText: function () {
            return app.dataDictValue(this.type, this.value)
        },
        icon(){
            var icon=this.dictIcons[this.type+"."+this.value];
            if(icon==null){
                return null;
            }
            return icon;
        },
        color(){
            var color=this.dictColors[this.type+"."+this.value];
            if(color==null){
                return "#79BEDB";
            }
            return color;
        }
    }
}
</script>
