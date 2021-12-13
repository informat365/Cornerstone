<style scoped>
.node-row-status{
    width:80px;
    border-radius: 3px;
    padding:3px 8px;
    font-weight: normal;
    font-size:12px;
    margin-right:10px;
}
.node-row-status-1{
    color:#bbbbbb;
    border:2px solid #bbbbbb;
}
.node-row-status-2{
    color:#f29c2b;
    border:2px solid #f29c2b;
}
.node-row-status-3{
    color:#8bc24c;
    border:2px solid #8bc24c;
}
</style>

<i18n>
    {
    "en": {
        "未开始": "Init",
        "未处理": "Working",
        "已处理": "Processed"
    },
    "zh_CN": {
        "未开始": "未开始",
        "未处理": "未处理",
        "已处理": "已处理"
    }
    }
</i18n>

<template>
<div>
    <div style="text-align:center;margin-bottom:20px">
        <span class="node-row-status node-row-status-1">{{$t('未开始')}}</span>
        <span class="node-row-status node-row-status-2">{{$t('未处理')}}</span>
        <span class="node-row-status node-row-status-3">{{$t('已处理')}}</span>
    </div>
    <div ref="workflow-graph"></div>
</div>
</template>
<script>
export default {
    props:['graph','allNodeLogList'],
    data() {
        return{

        }
    },
    mounted(){
        this.setupValue();
    },
    methods: {
        getColor(item){
            for(var i=0;i<this.allNodeLogList.length;i++){
                var t=this.allNodeLogList[i];
                if(t.nodeId==item.id){
                    if(t.finish){
                        return "#8bc24c"
                    }else{
                        return "#f29c2b"
                    }
                }
            }
            return "#bbbbbb"
        },
        setupValue(){
            var s=`
            digraph workflow{
                    fontsize = 9;
                    node [shape=box ,style="rounded" ,fontsize="9"];
                    edge [style="dashed" ,fontsize="9",color="#333333"];
            `
            this.graph.graphNodeList.forEach(item=>{
                var color=this.getColor(item)
                var shape="";
                if(item.type=='nodeStart'){
                    shape="shape=circle,"
                    color="#8bc24c"
                }
                if(item.type=='nodeEnd'){
                    shape="shape=doublecircle,";
                }
                s+= '"'+item.id+'"'+'[label="'+item.name+'",'+shape+' color="'+color+'", fontcolor="'+color+'",fillcolor="#ffffff"]\n';
            })
            //
            this.graph.graphLinkList.forEach(item=>{
                s+="\n"+'"'+item.source+'"'+" -> "+'"'+item.target+'"'+'[label="'+item.name+'" ]' + ";\n";
            })
            s+="\n}"
            //
            var svg = Viz(s);
            this.$refs['workflow-graph'].innerHTML=svg;
        }
    }
};
</script>
