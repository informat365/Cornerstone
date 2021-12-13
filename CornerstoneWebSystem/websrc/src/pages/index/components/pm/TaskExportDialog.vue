<style scoped>
   
</style>
<i18n>
{
    "en": {
        "设置需要导出的字段": "Set the fields that need to be exported",
        "导出":"Export",
        "请选择要导出的字段":"Select the fields to export",
        "详细描述":"Detail"
    },
    "zh_CN": {
        "设置需要导出的字段": "设置需要导出的字段",
        "导出":"导出",
        "请选择要导出的字段":"请选择要导出的字段",
        "详细描述":"详细描述"
    }
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('设置需要导出的字段')" width="400">

    <div>
        <Row @click.native="selectField(item)" class="popup-item-name" v-for="item in fieldList" :key="item.id">
            <Col span="20">{{item.name}}</Col>
            <Col span="4" style="text-align:right">
            <Icon v-if="item.selected" type="md-checkmark" />&nbsp;
            </Col>
        </Row>
    </div>

    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{$t('导出')}}</Button></Col>
        </Row>
    </div>

    </Modal>
</template>


<script>
import { Base64 } from 'js-base64';
    export default {
        mixins: [componentMixin],
        data () {
            return {
               
                fieldList:[],
            }
        },
        methods: {
            pageLoad(){
                this.fieldList=[];
                this.args.fieldList.map(item=>{
                    if(item.isShow){
                        this.fieldList.push({
                            id:item.id,
                            name:item.name,
                            selected:true
                        })
                    }
                })
                this.fieldList.push({
                    id:-1,
                    name:this.$t("详细描述"),
                    selected:true
                })
            },
            selectField(item){
                item.selected=!item.selected;
            },
            confirm(){
                var ids=[];
                for(var i=0;i<this.fieldList.length;i++){
                    var t=this.fieldList[i];
                    if(t.selected){
                        ids.push(t.id)
                    }
                }
                //
                if(ids.length==0){
                    app.toast(this.$t('请选择要导出的字段'));
                    return;
                }
                //
                var query={
                    token:app.token,
                    query:this.args.queryItem,
                    fields:ids,
                }
                //
                var queryString=JSON.stringify(query);
                var encoded=Base64.encode(queryString);
                window.open('/p/main/export_task_list?arg='+encodeURIComponent(encoded))
            },
            
        }
    }
</script>