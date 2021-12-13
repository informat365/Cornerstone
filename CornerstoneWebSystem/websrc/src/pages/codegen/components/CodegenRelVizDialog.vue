<style scoped>
   .table-row{
       padding:8px;
       font-size:13px;
       font-weight: bold;
   }
</style>
<i18n>
{
	"en": {
        "关系图": "关系图"
    },
	"zh_CN": {
		"关系图": "关系图"
	}
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('关系图')" class="fullscreen-title-modal"  fullscreen :footer-hide="true">
   <div style="flex:1;margin-top:0">
        <div style="position: relative;height:100%;width:100%">
             <div class="editor"></div>
         </div>
    </div> 
    </Modal>
</template>


<script>

export default {
    mixins: [componentMixin],
    data () {
        return {
        }
    },
    methods: {
        pageLoad(){
            app.invoke('DesignerAction.getAllDesignerColumns',[app.token,this.args.id],(obj)=>{
                this.genGraph(obj);
            });
        },
        genGraph:function(allColumns){
            var allTables={};
            for(var i=0;i<allColumns.length;i++){
                var t=allColumns[i];
                if(t.comment!=null&&t.comment.indexOf('[')!=-1){
                    t.comment="";
                }
                allTables[t.tableName]=true;
            }
            //
            var s="digraph models_diagram{\n"+
                "graph[rankdir=LR, overlap=true, splines=true];\n"+
                "node [shape=record, fontsize=9, fontname=\"Verdana\"];\n"+
                "edge [style=dashed];\n";

            for(var table in allTables){
                //
                var rows="";
                for(var i=0;i<allColumns.length;i++){
                    var t=allColumns[i]
                    var color="white";
                    if(t.fieldType==2){
                        color="#84D457";
                    }
                    if(t.fieldType==3){
                        color="#D65A6E";
                    }
                    if(t.tableName==table){
                        rows+='<tr><td  port="'+t.columnName+'" align="left" bgcolor="'+color+'">'+t.columnName+" "+t.comment+'</td></tr>\n'
                    }
                }
                //
                s+=''+table+' [shape=none, margin=0, label=<\n'+
                '<table border="0" cellborder="1" cellspacing="0" cellpadding="4">\n'+
                '    <tr><td FIXEDSIZE="true" width="150" fixedsize="true"  bgcolor="lightblue">'+table+'</td></tr>\n'+
                rows+
                '</table>>];\n';
            }
            //
            for(var i=0;i<allColumns.length;i++){
                var t=allColumns[i]
                if(t.foreignTableName!=null&&t.fieldType==1){
                    s+=  t.tableName+":"+t.columnName+" -> "+t.foreignTableName+":"+t.foreignColumnName+";\n";
                }
                if(t.relationTableName!=null&&t.fieldType==3){
                    s+=  t.tableName+":"+t.columnName+" -> "+t.relationTableName+":"+t.relationColumnName+"[style=solid;color=\"#D65A6E\"];\n";
                }
                if(t.relationTableName!=null&&t.fieldType==4){
                    s+=  t.tableName+":"+t.columnName+" -> "+t.relationTableName+":"+t.relationColumnName+"[style=solid;color=\"#2AB7AC\"];\n";
                    
                    s+=  t.relationTableName+":"+t.relationRightColumnName+" -> "+t.targetTableName+":"+t.targetColumnName+"[style=solid;color=\"#2AB7AC\"];\n";
                    
                }
            }
            //
            s+="}";
            //console.log(s)
            //
            
            var svg = Viz(s);
            this.$el.getElementsByClassName('editor')[0].innerHTML=svg;
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
            setTimeout(()=>{
                 svgElement.setAttribute("width",  windowWidth+"pt");
            },200)
        }
    }
}
</script>