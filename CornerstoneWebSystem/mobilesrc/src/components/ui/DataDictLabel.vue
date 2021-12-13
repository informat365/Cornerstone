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

</style>
<template>
   <span class="status-label" :style="{backgroundColor:color}">{{itemText}}</span>
</template>
<script>
export default {
    name:"DataDictLabel",
    props: ['value','type'],
    data (){
        return{
            dictColors:{
                'ProjectIteration.status.1':'#dddfe6',
                'ProjectIteration.status.2':'#6abe83',
                'ProjectIteration.status.3':'#1794A5',

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
                
            }
        }
    },
    computed: {
        itemText: function () {
            return app.dataDictValue(this.type, this.value)
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