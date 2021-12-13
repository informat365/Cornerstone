<style scoped>
.page{
  min-height: 400px;
}
.opt-bar{
  background-color: #F1F4F5;
  margin-top: 0;
}
.opt-right{
  text-align: right;
}
.table-info-bar{
  font-size:12px;
  font-weight:bold;
  color:#999999;
  text-align:right;
}
.workflow-graph{
    width:100%;
    height:calc(100vh - 180px)
}

</style>
<i18n>
{
	"en": {
        "分组": "分组",
		"查询": "查询"
    },
	"zh_CN": {
		"分组": "分组",
		"查询": "查询"
	}
}
</i18n>
<template>
    <div class="page">
         <Row class="opt-bar">
          <Col span="18" class="opt-left">
               <Form inline @submit.native.prevent>
                    <FormItem>
                      <Input  v-model="formItem.group"  type="text" :placeholder="$t('分组')"></Input>
                    </FormItem>
                    <FormItem>
                        <Button :disabled="formItem.group==null" @click="loadData()" type="default">{{$t('查询')}}</Button>
                    </FormItem>
             </Form>

          </Col>
          <Col span="6" class="opt-right">
            <Form inline>
             </Form>
            </Col>
       </Row>

        <div style="padding:20px">
            <div class="workflow-graph"></div>
        </div>

    </div>
</template>

<script>

export default {
    mixins: [componentMixin],
    data(){
        return {
            pageQuery:{
                pageIndex:1,
                pageSize:9999,
            },
            formItem:{
                group:null
            },
            tableData:[],
        }
    },
    methods:{
        pageLoad(){
             this.loadPageStatus("CmdbApplicationGraphPage")
        },
        loadData(){
            var query=copyObject(this.pageQuery,this.formItem)
            this.savePageStatus("CmdbApplicationGraphPage")
            app.invoke('BizAction.getCmdbApplicationList',[app.token,query],(info)=>{
                this.tableData=info.list;
                this.pageQuery.total=info.count;
                this.loadGraph();
            })
        },
        loadGraph(){
            var layers={
                'mysql':'db',
                'mongodb':'db',
                'mssql':'db',
                'oracle':'db',
                'nosql':'db',
                'redis':'cache',
                'memcached':'cache',
                'apache':'web',
                'jazmin-web':'web',
                'iis':'web',
                'kangle':'web',
                'jboss':'web',
                'web':'web',
                'tomcat':'web',
                'jetty':'web',
                'weblogic':'web',
                'jazmin-rpc':'app',
                'rpc':'app',
                'netty':'app',
                'mq':'app',
                'haproxy':'proxy',
                'nginx':'proxy',
                'lvs':'proxy',
                'proxy':'proxy',
                'app':'user'
            };
            //
            var nodeColors={
                'user':'aliceblue',
                'proxy':'pink1',
                'web':'pink1',
                'app':'lightblue2',
                'cache':'olivedrab1',
                'db':'olivedrab1',
                'other':'yellow3',
            }
            var nodeShape={
                'user':'component',
                'proxy':'box',
                'web':'box',
                'app':'box',
                'cache':'tab',
                'db':'tab',
                'other':'component',
            }
            var s=`
            digraph appgraph{
                    fontsize = 9;
                    rankdir=LR;
                    node [shape="record" fontsize="9" fontname="Arial",style="filled,rounded,solid"];
                    edge [style="dashed",fontname="Arial",fillcolor="#999999"];
            `
            for(var k in nodeColors){
                var color=nodeColors[k];
                var shape=nodeShape[k];
                s+=(k+'[shape="'+shape+'" color="'+color+'"]\n')
            }
            for(var i=0;i<this.tableData.length;i++){
                var t=this.tableData[i];
                var layer=layers[t.type];
                if(layer==null){
                    layer="other";
                }
                layer = layer.toLowerCase();
                var color=nodeColors[layer];
                var shape=nodeShape[layer];
                 s+='"'+(t.name+'" [shape="'+shape+'" color="'+color+'"]\n')
            }

            for(var i=0;i<this.tableData.length;i++){
                var t=this.tableData[i];
                if(t.depends){
                     for(var j=0;j<t.depends.length;j++){
                        var dep=t.depends[j];
                        s+=("\""+t.name)+"\" -> \""+dep+"\"\n";
                    }
                }
            }
            //
            for(var k in nodeColors){
                s+="{rank=same;";
                s+=k+";"
                for(var i=0;i<this.tableData.length;i++){
                    var t=this.tableData[i]
                    var layer=layers[t.type];
                    if(layer==null){
                        layer="other";
                    }
                    if(layer==k){
                        s+="\""+t.name+"\";"
                    }
                }
                s+="}\n;"
            }
            s+='user->proxy->web->app->cache->db->other;\n'
            s+="\n}"
            var svg = Viz(s);
            this.$el.getElementsByClassName('workflow-graph')[0].innerHTML=svg;
            //
            var svgElement = this.$el.getElementsByTagName("svg")[0];
            var panZoom = svgPanZoom(svgElement, {
                zoomEnabled: true,
                controlIconsEnabled: true,
                fit: true,
                center: true,
                minZoom: 0.1
            });
            svgElement.addEventListener('paneresize', function(e) {
                panZoom.resize();
            }, false);
            var windowWidth=this.$el.clientWidth;
            var windowHeight=this.$el.clientHeight;
            //
            var svgPan=document.getElementById('svg-pan-zoom-controls')
            svgPan.setAttribute('transform','translate(1 1) scale(0.75)')
            //
            setTimeout(()=>{
                svgElement.setAttribute("width",   windowWidth+"pt");
                svgElement.setAttribute("height",  windowHeight+"pt");
            },200)
        },
    }
}
</script>
