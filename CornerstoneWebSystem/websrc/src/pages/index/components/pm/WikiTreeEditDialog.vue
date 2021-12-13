<style scoped>
.wiki-name{
    font-size:13px;
    padding-left:5px;
    color:#333;
    display: block;
}
.wiki-name:hover{
   cursor:move;
}
</style>

<i18n>
    {
    "en": {
         "知识库": "Wiki",
    "拖动调整页面的结构和顺序": "Drag to reorder",
    "所有页面": "All Pages",
    "保存成功": "Success",
    "保存": "Save"
    },
    "zh_CN": {
    "知识库": "知识库",
    "拖动调整页面的结构和顺序": "拖动调整页面的结构和顺序",
    "所有页面": "所有页面",
    "保存成功": "保存成功",
    "保存": "保存"
    }
    }
</i18n>

 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('知识库')" width="700"  @on-ok="confirm">
        <div style="padding:10px;color:#999;font-size:13px">{{$t('拖动调整页面的结构和顺序')}}</div>
        <div style="min-height:500px;padding:10px">

            <SortableTree :data="treeData">
                <template slot-scope="{item}">
                    <span class="wiki-name text-no-wrap">{{item.name}}</span>
                </template>
            </SortableTree>

        </div>
    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{$t('保存')}}</Button></Col>
        </Row>
    </div>

    </Modal>
</template>


<script>
    export default {
        mixins: [componentMixin],
        data () {
            return {
                treeData:{},
            }
        },
        methods: {
            pageLoad(){
                app.invoke('BizAction.getWikiPageList',[app.token,this.args.id],(info)=>{
                    this.treeData={
                        name:this.$t('所有页面'),
                        children: info.wikiPageTree
                    }
                })
            },
            confirm(){
                app.invoke('BizAction.updateWikiPageSortWeightAndRelation',[app.token,this.treeData.children],(info)=>{
                    app.toast(this.$t('保存成功'))
                    app.postMessage('wikipage.edit');
                    this.showDialog=false;
                })
            },
        }
    }
</script>
