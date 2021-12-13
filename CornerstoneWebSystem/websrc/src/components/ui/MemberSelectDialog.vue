<style scoped>
.search-box{
    position: absolute;
    top:32px;
    left:0;
    width:422px;
    max-height: 400px;
    overflow: auto;
    background-color: #fff;
    box-shadow: 0 1px 6px rgba(0,0,0,.2);
}
.search-item{
    padding:10px;
    font-size:13px;
    border-bottom: 1px solid #eee;
    cursor: pointer;
}
.search-item:hover{
    background-color: #ebf7ff;
}
.search-no-data{
    text-align: center;
    padding:20px;
}
</style>
<i18n>
{
    "en": {
        "选择成员":"Choose member",
        "清除所有":"Clear all",
        "清除":"Clear",
        "找不到匹配的用户":"No data",
        "搜索成员":"Search member",
        "确定":"OK",
        "请选择用户":"Select at least one member",
        "只能选择一个用户":"Can only select one member"
    },
    "zh_CN": {
        "选择成员":"选择成员",
        "清除所有":"清除所有",
        "清除":"清除",
        "找不到匹配的用户":"找不到匹配的用户",
        "搜索成员":"搜索成员",
        "确定":"确定",
        "请选择用户":"请选择用户",
        "只能选择一个用户":"只能选择一个用户"
    }
}
</i18n> 
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('选择成员')" width="700"  @on-ok="confirm">
    <div style="height:500px;padding:10px">
        <div style="margin-top:10px;">
            <treeselect v-model="selectedUsers" 
            :clear-all-text="$t('清除所有')"
            :clear-value-text="$t('清除')"
            :no-results-text="$t('找不到匹配的用户')"
            :search-prompt-text="$t('搜索成员')"
            :placeholder="$t('搜索成员')"
            no-options-text=""
            value-format="object"
            :match-keys='["pinyinName","label","userName"]'
            :show-count="true" value-consists-of="LEAF_PRIORITY" :always-open="true" :max-height="400" 
            :searchable="true" :default-expand-level="1" :multiple="true" :options="treeData">
    
                    <label slot="option-label" slot-scope="{ node, shouldShowCount, count, labelClassName, countClassName }" :class="labelClassName">
                        <Icon v-if="node.raw.type==1" type="ios-people" />
                        <Icon v-if="node.raw.type==2" type="md-person" /> 
                        {{ node.label }}
                    <span v-if="shouldShowCount" :class="countClassName">({{ count }})</span>
                </label>
            </treeselect>
        </div>
       
    </div>
    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{$t('确定')}}</Button></Col>
        </Row>
    </div>
    </Modal>
</template>


<script>
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
export default {
        mixins: [componentMixin],
        components: { Treeselect },
        data () {
            return {
                treeData:[],
                selectedUsers:[]
            }
        },
        methods: {
            pageLoad(){
                this.loadData();
            },
            loadData(){
                app.invoke('BizAction.getDepartmentTree',[app.token,true],(info)=>{
                    travalTree(info[0],(item)=>{
                         item.label=item.title;
                         if(item.type==1&&item.children.length==0){
                             item.isDisabled=true
                         }
                         if(item.children.length==0){
                             delete item.children;
                         }
                         
                    })
                    this.treeData=info;
                   
                })
            },
            confirm:function(){
                if(this.selectedUsers.length==0){
                    app.toast(this.$t('请选择用户'));
                    return;
                }
                if(this.args.countType=='single'){
                    if(this.selectedUsers.length>1){
                        app.toast(this.$t('只能选择一个用户'));
                        return;
                    }
                }
                this.showDialog=false;
                this.args.callback(this.selectedUsers)
            }
        }
    }
</script>