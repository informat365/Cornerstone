<style scoped>
.page{
    background-color: #F1F4F5;
}
.page-toolbar{
    height:40px;
    border-bottom:1px solid  #ccc;
    display: flex;
    align-items: center;
    padding:0 10px;
}

.page-toolbar-gap{
    flex:1;
}
.mr{
    margin-right: 5px;
}
.graph-box{
    width:100%;
    height: calc(100vh - 90px);
    background-color: #fff;
    overflow: auto;
    position: relative;
}
.zoom-label{
    color:#666;
    font-weight: bold;
    width:40px;
    text-align: center;
    cursor: pointer;
}
.side-box{
    position: fixed;
    top:88px;
    right:0;
    width:350px;
    height:calc(100vh - 88px);
    background-color: #fff;
    border-left: 1px solid #ccc;
    z-index:100;
    overflow: auto;
    box-shadow: -4px 6px 14px 6px rgba(225,225,225,0.5);
}
.side-box-inner{
    padding:10px;
}
.node-desc{
    font-size:12px;
    color:#666;
    margin:5px 0;
}
</style>
<i18n>
{
	"en": {
        "新建流程": "新建流程",
		"启用流程": "启用流程",
		"启用中": "启用中",
		"删除流程": "删除流程",
		"流程开始节点": "流程开始节点",
		"流程结束节点": "流程结束节点,流转到此节点后流程结束",
		"人工参与节点": "人工参与节点",
		"流程跳转条件": "流程跳转条件",
		"名称": "Name",
		"删除": "Delete",
		"未命名": "Untitled",
		"开始": "Start",
		"结束": "End",
		"未命名自动节点": "未命名自动节点",
		"工作流中有节点的名称为空": "工作流中有节点的名称为空，请修改",
		"工作流中节点名称重复": "工作流中节点【{0}】名称重复，请修改",
		"工作流中应该至少有一个结束节点": "工作流中应该至少有一个结束节点",
		"保存成功": "Saved Success",
		"确定要启用此工作流吗": "确定要启用此工作流吗？启用后新申请的流程将使用此工作流",
		"操作成功": "操作成功",
		"确定要删除此工作流吗": "确定要删除此工作流吗？",
		"默认流程图": "默认流程图",
		"节点和连接线的默认值": "节点和连接线的默认值",
		"执行动作后的流转规则": "执行动作后的流转规则",
		"执行下一步": "执行下一步",
		"等待外部接口触发": "等待外部接口触发",
		"新建人工节点": "新建人工节点",
		"新建结束节点": "新建结束节点",
		"新建自动节点": "新建自动节点"
    },
	"zh_CN": {
		"新建流程": "新建流程",
		"启用流程": "启用流程",
		"启用中": "启用中",
		"删除流程": "删除流程",
		"流程开始节点": "流程开始节点",
		"流程结束节点": "流程结束节点,流转到此节点后流程结束",
		"人工参与节点": "人工参与节点",
		"流程跳转条件": "流程跳转条件",
		"名称": "名称",
		"删除": "删除",
		"未命名": "未命名",
		"开始": "开始",
		"结束": "结束",
		"未命名自动节点": "未命名自动节点",
		"工作流中有节点的名称为空": "工作流中有节点的名称为空，请修改",
		"工作流中节点名称重复": "工作流中节点【{0}】名称重复，请修改",
		"工作流中应该至少有一个结束节点": "工作流中应该至少有一个结束节点",
		"保存成功": "保存成功",
		"确定要启用此工作流吗": "确定要启用此工作流吗？启用后新申请的流程将使用此工作流",
		"操作成功": "操作成功",
		"确定要删除此工作流吗": "确定要删除此工作流吗？",
		"默认流程图": "默认流程图",
		"节点和连接线的默认值": "节点和连接线的默认值",
		"执行动作后的流转规则": "执行动作后的流转规则",
		"执行下一步": "执行下一步",
		"等待外部接口触发": "等待外部接口触发",
		"新建人工节点": "新建人工节点",
		"新建结束节点": "新建结束节点",
		"新建自动节点": "新建自动节点"
	}
}
</i18n>
<template>
    <div  class="page">
       <div class="page-toolbar">
            <IconButton @click="zoomIn()" icon="md-remove"></IconButton>
            <div class="zoom-label" @click="zoomFit()">{{getZoomDisplay()}}</div>
            <IconButton @click="zoomOut()" icon="md-add"></IconButton>
            <div class="page-toolbar-gap"></div>   
            
            <IconButton @click="addWorkflowChart"  :title="$t('新建流程')"></IconButton> 
            <IconButton v-if="workflowDefine.chartDefineId!=selectedWorkflowChartId" @click="enableWorkflowChart()" :title="$t('启用流程')"></IconButton>
            
            <Select @on-change="loadData" v-model="selectedWorkflowChartId" class="mr" size="small" style="width:100px">
                <Option v-for="item in workflowChartList"  :key="'wc'+item.id" :value="item.id">
                    {{item.name}} 
                    <template v-if="workflowDefine.chartDefineId==item.id">{{$t('启用中')}}</template>
                </Option>
            </Select>
            <IconButton @click="deleteWorkflowChart()" :title="$t('删除流程')"></IconButton>
       </div>
       <div class="graph-box">
           <div  onselectstart="javascript:/*IE8 hack*/return false" id="gfxholder" style="width:2500px; height:2500px; "></div>
       </div>
       <div v-if="graphItem.type!=null" class="side-box scrollbox">
           <div class="side-box-inner">
                <div v-if="graphItem.type=='nodeStart'" class="node-desc">{{$t('流程开始节点')}}</div>
                <div v-if="graphItem.type=='nodeEnd'" class="node-desc">{{$t('流程结束节点')}}</div>
                <div v-if="graphItem.type=='nodeEvent'" class="node-desc">{{$t('人工参与节点')}}</div>
                <div v-if="graphItem.type=='link'" class="node-desc">{{$t('流程跳转条件')}}</div>
                <Form :label-width="60" label-position="left" :model="graphItem" :rules="formRule">
                     <FormItem :label="$t('名称')" prop="name"> 
                          <Input  v-model.trim="graphItem.name" type="text" :maxlength="50"/>
                     </FormItem>
                </Form>
               
                <div style="margin-top:5px">
                    <Button v-if="graphItem.type!='nodeStart'"
                        type="error" @click="deleteFingure">{{$t('删除')}}</Button>
                </div>
           </div>
           <div v-if="graphItem.type=='link'">
                <WorkflowLinkEdit v-if="graphItemProp" 
                :value="graphItemProp" 
                :graph="graph"
                :workflowFormFieldList="workflowFormFieldList"
                ></WorkflowLinkEdit>
           </div>
           <div v-if="graphItem.type=='nodeEvent'">
                <WorkflowEventNodeEdit v-if="graphItemProp" 
                :value="graphItemProp" 
                :graph="graph"
                :workflowFormFieldList="workflowFormFieldList"
                ></WorkflowEventNodeEdit>
           </div>
            <div v-if="graphItem.type=='nodeAuto'">
                <WorkflowAutoNodeEdit v-if="graphItemProp" 
                v-model="graphItemProp" 
                :workflowFormFieldList="workflowFormFieldList"
                :graph="graph"
                ></WorkflowAutoNodeEdit>
           </div>
       </div>
    </div>
</template>

<script>
var CustomNode= draw2d.shape.basic.Rectangle.extend({
    init:function(attr){
        this._super(attr);
        var text=app.currentPage.$t('未命名');
        if(attr&&attr.labelText){
            text=attr.labelText
        }
        this.label = new draw2d.shape.basic.Label({text:text,stroke:0, color:"#333", fontColor:"#333"});
        this.add(this.label, new draw2d.layout.locator.CenterLocator(this));
        this.installEditPolicy(new draw2d.policy.figure.AntSelectionFeedbackPolicy());
        this.width=100;
        this.height=40;
    }
})
var StartNode =CustomNode.extend({
    init:function(attr){
        this._super(extend({
            bgColor:'#59CEF7',
            color: 'white',
        }, attr))
        this.label.text=app.currentPage.$t('开始');
        this.outport=this.createPort("output",new draw2d.layout.locator.BottomLocator())
    }
})
//
var EndNode =CustomNode.extend({
    init:function(attr){
        this._super(extend({
            bgColor:'#DCDEE2',
            color: '#fff',
        }, attr))
        this.label.text=app.currentPage.$t('结束');
        this.inport=this.createPort("input",new draw2d.layout.locator.TopLocator())
    }
})
//
var EventNode =CustomNode.extend({
    init:function(attr){
        this._super(extend({
            bgColor:'#baddd6',
            color: 'white',
        }, attr))
        this.label.text=app.currentPage.$t('未命名');
        this.inport = this.createPort("input",
            new draw2d.layout.locator.TopLocator()
        );
        this.outport = this.createPort("output",
            new draw2d.layout.locator.BottomLocator()
        );
    }
})
//
var AutoNode =CustomNode.extend({
    init:function(attr){
         this._super(extend({
            bgColor:'#f0f2cc',
            color: 'white',
        }, attr))
        this.label.text=app.currentPage.$t('未命名自动节点');
        this.inport = this.createPort("input",
            new draw2d.layout.locator.TopLocator()
        );
        this.outport = this.createPort("output",
            new draw2d.layout.locator.BottomLocator()
        );
    }
})
//
var createConnection=function(sourcePort, targetPort){
    var c = new draw2d.Connection({
        targetDecorator: new draw2d.decoration.connection.ArrowDecorator(),
        outlineColor:"#ffffff",
        outlineStroke:1,
        color:"#000000",
        stroke:1,
        radius:2
        
    });
    var locator=new draw2d.layout.locator.ParallelMidpointLocator()
    var label = new draw2d.shape.basic.Label({
        text:"", 
        stroke:0, 
        color:"#FF0000", 
        fontColor:"#0d0d0d"
    });
    c.label=label;
    c.add(label, locator);
    c.setRouter(null);
    //
    if(sourcePort){
        c.setSource(sourcePort);
    }
    if(targetPort){
        c.setTarget(targetPort);
    }
    return c;
};
//
import WorkflowPageMixin from './WorkflowPageMixin'
export default {
    mixins: [componentMixin,WorkflowPageMixin],
    components:{
    },
    data(){
        return {
            formRule:{
                "name":[vd.name2_15],
            },
            formItem:{
                id:0
            },
            workflowChartList:[],
            selectedWorkflowChartId:null,
            workflowDefine:{},
            graph:null,
            graphItem:{
                id:null,
                type:null,
                name:null
            },
            graphItemProp:null,
            graphItemProps:{

            },
            zoom:1,
            selectedFigure:null,
            workflowFormFieldList:[],
        }
    },
    mounted(){
        this.$nextTick(()=>{
            this.setupDraw2d();
        })
    },
    watch:{
        "graphItem.name"(val){
            if(this.selectedFigure){
                this.selectedFigure.label.setText(val);
                this.getGraph();
                this.isSaved=false
            }
        }
    }, 
    methods:{
        pageLoad(){
            app.currentPage=this;
            this.workflowDefine=app.workflowDefine;
            this.selectedWorkflowChartId=this.workflowDefine.chartDefineId;
            this.loadData();
            this.loadWorkflowForm();
            this.loadWorkflowChartList();
            //
            this.watchSaveProp('graphItemProp');
        },
        getZoomDisplay(){
            var t=(1-(this.zoom-1))*100
            return t.toFixed(0)+"%"
        },
        confirm(callback){
            this.getGraph();
            for(var i=0;i<this.graph.nodes.length;i++){
                var node=this.graph.nodes[i];
                if(node.name==null||node.name==''){
                    app.toast(this.$t('工作流中有节点的名称为空'));
                    return;
                }
            }
            //
            var nodeNameCount={};
            //
            for(var i=0;i<this.graph.nodes.length;i++){
                var node=this.graph.nodes[i]; 
                if(nodeNameCount[node.name]){
                    app.toast(this.$t('工作流中节点名称重复',[node.name]));
                    return;
                }else{
                    nodeNameCount[node.name]=true;
                }
            }
            var endNodeCount=0;
            this.graph.nodes.forEach(n=>{
                if(n.type=='nodeEnd'){
                    endNodeCount++;
                }
            })
            if(endNodeCount==0){
                app.toast(this.$t('工作流中应该至少有一个结束节点'))
                return;
            }
            //
            this.formItem.graph=JSON.stringify(this.graph);
            this.formItem.props=JSON.stringify(this.graphItemProps)
            app.invoke('WorkflowAction.updateWorkflowChartDefine',[app.token,this.formItem],(info)=>{
                app.toast(this.$t('保存成功'));
                this.isSaved=true;
                if(callback){
                    callback();
                }
            })
        },
        loadData(chartId){
            var id=this.selectedWorkflowChartId;
            if(chartId){
                id=chartId;
            }
            if(id==null){
                return;
            }
            app.invoke('WorkflowAction.getWorkflowChartDefineById',[app.token,id],(info)=>{
                this.formItem=info;
                this.graph=null;
                this.graphItemProps={};
                try{
                    if(info.graph){
                        this.graph=JSON.parse(info.graph);
                        // this.graph.nodes.forEach((item)=>{
                        //     item.name = app.currentPage.$t(item.name);
                        // });
                    }
                    if(info.props){
                        var prop=JSON.parse(info.props)
                        for(var k in prop){
                            if(prop[k].owner==null){
                                prop[k].owner={}
                            }
                            if(prop[k].cc==null){
                                prop[k].cc={}
                            }
                        }
                        this.graphItemProps=prop;
                    }

                }catch(e){
                    console.log(e);
                }
                //
                this.setupGraphCanvas();
                this.$nextTick(()=>{
                    this.isSaved=true;
                })
            });
        },
        loadWorkflowForm(){
            app.invoke('WorkflowAction.getWorkflowFormDefineById',[app.token,this.workflowDefine.id],(info)=>{
                try{
                    if(info.fieldList){
                        this.workflowFormFieldList=JSON.parse(info.fieldList)
                    }
                }catch(e){
                    console.log(e);
                }
            });
        },
        loadWorkflowChartList(selectId){
            var query={
                pageIndex:1,
                pageSize:999,
                workflowDefineId:this.workflowDefine.id
            }
            app.invoke('WorkflowAction.getWorkflowChartDefineList',[app.token,query],(info)=>{
                this.workflowChartList=info.list;
                if(selectId){
                    this.selectedWorkflowChartId=selectId;
                }
            });
        },
        enableWorkflowChart(){
            app.confirm(this.$t('确定要启用此工作流吗'),()=>{
                app.invoke('WorkflowAction.setWorkflowChartDefine',[app.token,
                this.workflowDefine.id,
                this.selectedWorkflowChartId
                ],(info)=>{
                    app.toast(this.$t('操作成功'));
                    this.reloadWorkflowDefine();
                })
            })
        },
        deleteWorkflowChart(){
            app.confirm(this.$t('确定要删除此工作流吗'),()=>{
                app.invoke('WorkflowAction.deleteWorkflowChartDefine',[app.token,this.formItem.id],(info)=>{
                    app.toast(this.$t('操作成功'));
                    this.reloadWorkflowDefine();
                })
            })
        },
        addWorkflowChart(){
            app.invoke('WorkflowAction.addWorkflowChartDefine',[app.token,this.workflowDefine.id],(id)=>{
                app.toast(this.$t('操作成功'));
                this.loadWorkflowChartList(id);
                this.loadData(id);
            })
        },
        reloadWorkflowDefine(){
            app.invoke('WorkflowAction.getWorkflowDefineById',[app.token,this.workflowDefine.id],(info)=>{
                this.workflowDefine=info;
                app.workflowDefine=info;
                this.selectedWorkflowChartId=this.workflowDefine.chartDefineId;
                this.loadWorkflowChartList();
                this.loadData();
            });
        },
        
        getFigureType(f){
            var t=null;
            if(f instanceof draw2d.Connection) {
                t="link"
            }
            if(f instanceof StartNode) {
                t="nodeStart"
            }
            if(f instanceof EndNode) {
                t="nodeEnd"
            }
            if(f instanceof EventNode) {
                t="nodeEvent"
            }
             if(f instanceof AutoNode) {
                t="nodeAuto"
            }
            return t;
        },
        setupGraphCanvas(){
            this.canvas.clear();
            if(this.graph==null||this.graph.nodes==null){
                //默认流程图
                var node1 =new StartNode();
                var node2 =new EndNode();
                var nodeEvent=new EventNode();
                this.canvas.add(node1,400,50);
                this.canvas.add(nodeEvent,400,170);
                this.canvas.add(node2,400,300);
                //
                this.canvas.add(createConnection(node1.outport,nodeEvent.inport));
                this.canvas.add(createConnection(nodeEvent.outport,node2.inport));
                this.getGraph();
            }else{
                var allGraphNodes={};
                for(var i=0;i<this.graph.nodes.length;i++){
                    var node=this.graph.nodes[i];
                    var canvasNode;
                    if(node.type=='nodeStart'){
                        canvasNode=new StartNode();
                    }
                    if(node.type=='nodeEnd'){
                        canvasNode=new EndNode();
                    }
                    if(node.type=='nodeEvent'){
                        canvasNode=new EventNode();
                    }
                     if(node.type=='nodeAuto'){
                        canvasNode=new AutoNode();
                    }
                    canvasNode.id=node.id;
                    canvasNode.label.setText(node.name);
                    this.canvas.add(canvasNode,node.x,node.y);
                    
                    //
                    allGraphNodes[node.id]=canvasNode;
                }
                // 
                for(var i=0;i<this.graph.links.length;i++){
                    var link=this.graph.links[i];
                    var source=allGraphNodes[link.source];
                    var target=allGraphNodes[link.target];
                    var conn=createConnection(source.outport,target.inport)
                    conn.id=link.id;
                    conn.label.setText(link.name);
                    this.canvas.add(conn);
                }
            }
        },
        getGraph(){
            var graph={
                nodes:[],
                links:[]
            }
            var figures = this.canvas.getFigures().data;
            for(var i=0;i<figures.length;i++){
                var f=figures[i];
                graph.nodes.push({
                    id:f.id,
                    name:f.label.text,
                    x:f.x,
                    y:f.y,
                    type:this.getFigureType(f)
                })
            }
            //
            var lines = this.canvas.getLines().data;
            for(var i=0;i<lines.length;i++){
                var f=lines[i];
                graph.links.push({
                    id:f.id,
                    name:f.label.text,
                    source:f.sourcePort.parent.id,
                    target:f.targetPort.parent.id,
                })
            }
            //
            this.graph=graph;
        },
        getGraphItemProp(graphItem){
            if(this.graphItemProps[graphItem.id]==null){
                this.graphItemProps[graphItem.id]=this.createGraphItemProp(graphItem);
            }
            var prop= this.graphItemProps[graphItem.id];
            return prop;
        },
        createGraphItemProp(graphItem){
            //节点和连接线的默认值
            if(graphItem.type=='link'){
                return {
                    id:graphItem.id,
                    express:'',
                    ruleType:'and',
                    ruleList:[]
                }
            }
            if(graphItem.type=='nodeEvent'){
                return {
                    id:graphItem.id,
                    fieldList:[],
                    enableBackword:false,
                    backwordNodeList:[],
                    enableSave:false,
                    enableTerminal:false,
                    enableTransferTo:false,
                    forwardRule:'any',
                    owner:{
                        userList:[],
                        departmentList:[],
                        departmentOwnerList:[],
                        companyRoleList:[],
                        projectRoleList:[],
                        formItemList:[],
                        submitter:true,
                    },
                    cc:{
                        userList:[],
                        departmentList:[],
                        departmentOwnerList:[],
                        companyRoleList:[],
                        projectRoleList:[],
                        formItemList:[],
                        submitter:true,
                    }
                }
            }
            if(graphItem.type=='nodeAuto'){
                return{
                    id:graphItem.id,
                    action:null,
                    emailSetting:{},
                    webApiSetting:{},
                    forwardRule:"next",//执行动作后的流转规则 next,执行下一步 wait,等待外部接口触发
                }
            }
            return {id:graphItem.id}
        },
        
        setupDraw2d(){
            var canvas = new draw2d.Canvas('gfxholder');
            this.canvas=canvas;
            canvas.installEditPolicy(new draw2d.policy.canvas.SnapToGridEditPolicy());
            canvas.installEditPolicy(new draw2d.policy.canvas.ShowGridEditPolicy());
            canvas.installEditPolicy( new draw2d.policy.connection.ComposedConnectionCreatePolicy([
                new draw2d.policy.connection.DragConnectionCreatePolicy({
                    createConnection:createConnection
                })
            ]));
            //
            canvas.on("contextmenu", (emitter, event)=>{
                $.contextMenu({ 
                    selector: '#gfxholder', 
                    autoHide: true, 
                    events: { 
                        hide: function() { 
                            $.contextMenu('destroy'); 
                        } 
                    }, 
                    callback: $.proxy(function (key, options) { 
                       this.addCanvasNode(key,event.x,event.y);
                    }, this), 
                    x: event.x, 
                    y: event.y, 
                    items: { 
                        "nodeEvent": { name: this.$t("新建人工节点") }, 
                        "nodeEnd": { name: this.$t("新建结束节点") }, 
                        "nodeAuto": { name: this.$t("新建自动节点") }, 
                    } 
                }); 
            })
            //
            canvas.on("added", (emitter, event)=>{
                this.isSaved=false;
            })
            //
            canvas.on("select", (emitter, event)=>{
                this.selectedFigure=event.figure;
                if(event.figure==null){
                    return;
                }
                this.graphItem.id=event.figure.id;
                this.graphItem.name=event.figure.label.text;
			    if(event.figure instanceof draw2d.Connection) {
                    event.figure.addCssClass("connection_highlight");
                    this.graphItem.type="link"
                }
                this.graphItem.type=this.getFigureType(event.figure);
                var lastSaved=this.isSaved;
                this.graphItemProp=this.getGraphItemProp(this.graphItem)
                setTimeout(()=>{
                    this.isSaved=lastSaved;
                },500)
            });
            canvas.on("unselect", (emitter, event)=>{
                this.selectedFigure=null;
                this.graphItem.type=null;
                if(event.figure instanceof draw2d.Connection) {
                    event.figure.removeCssClass("connection_highlight");
                }
            });
        },

        zoomOut(){
            this.zoom-=0.1
            if(this.zoom<=0.3){
                this.zoom=0.3;
            }
            this.canvas.setZoom(this.zoom);
        },
        zoomFit(){
            this.zoom=1;
            this.canvas.setZoom(this.zoom);
        },
        zoomIn(){
            this.zoom+=0.1;
             if(this.zoom>=1.9){
                this.zoom=1.9;
            }
            this.canvas.setZoom(this.zoom);
        },
        addCanvasNode(type,x,y){
            var node=null;
            if(type=='nodeEnd'){
                node=new EndNode();
            }
            if(type=='nodeAuto'){
                node=new AutoNode();
            }
            if(type=='nodeEvent'){
                node=new EventNode();
            }
            if(node){
                this.canvas.add(node,x,y);
                this.getGraph();
                this.isSaved=false;
            }
        },
        deleteFingure(){
            this.canvas.remove(this.selectedFigure)
            this.selectedFigure=null;
            this.graphItem.type=null;
            this.getGraph();
            this.isSaved=false;
        }
    }
}
</script>