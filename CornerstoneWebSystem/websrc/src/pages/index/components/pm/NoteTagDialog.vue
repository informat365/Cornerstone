<style scoped>
</style>
<i18n>
{
    "en": {
        "设置标签": "Tag Setting",
        "选择标签":"Choose tag",
        "新建标签":"Add tag",
        "添加":"Add",
        "确定":"OK"
    },
    "zh_CN": {
        "设置标签": "设置标签",
        "选择标签":"选择标签",
        "新建标签":"新建标签",
        "添加":"添加",
        "确定":"确定"
    }
}
</i18n>
 <template>
    <Modal
        ref="dialog" v-model="showDialog" :closable="true" :mask-closable="false"
        :loading="false" :title="$t('设置标签')" width="700">
   
    <Form   label-position="top" style="height:300px;padding:15px">
        <FormItem v-if="tagList.length>0" :label="$t('选择标签')">
            <NoteTag @click.native="selectTag(item)" 
            v-for="item in tagList" 
            :key="item.id" 
            :enableEdit="true"
            @edit="editTag"
            @deleteTag="deleteTag"
            :value="item" :selected="item.isSelected"></NoteTag>
        </FormItem>
        <FormItem :label="$t('新建标签')">
            <Input type="text" :maxlength="30" v-model.trim="newTagContent" style="width:150px;margin-right:10px"></Input>
            <Button icon="md-add" @click="confirmAdd" type="default">{{$t('添加')}}</Button>
        </FormItem>
    </Form> 

    <div slot="footer">
        <Row>
            <Col span="24" style="text-align:right"> <Button @click="confirm" type="default" size="large" >{{$t('确定')}}</Button></Col>
        </Row>
    </div>

    </Modal>
</template>


<script>
    export default {
        name:"NoteTagDialog",
        mixins: [componentMixin],
        data () {
            return {
                tagList:[],
                newTagContent:null
            }
        },
        methods: {
            pageLoad(){
                this.loadData();
            },
            loadData(){
                var selectedMap={};
                if(this.args.selectedList){
                    this.args.selectedList.forEach(item=>{
                        selectedMap[item.id]=true;
                    })
                }
                app.invoke('NoteAction.getMyNoteTagList',[app.token],(list)=>{
                    list.forEach(item=>{
                        var t=selectedMap[item.id];
                        item.isSelected=t==null?false:t;
                    })
                    this.tagList=list;
                });
            },
            selectTag(item){
                item.isSelected=!item.isSelected;
            },
            confirm(){
                var list=[];
                this.tagList.forEach(item=>{
                    if(item.isSelected){
                        list.push(item.id);
                    }
                })
                this.showDialog=false;
                if(this.args.callback){
                    this.args.callback(list)
                }
            },
            editTag(e){
                app.invoke('NoteAction.updateNoteTag',[app.token,e],(id)=>{
                   
                });
            },
            deleteTag(e){
                app.invoke('NoteAction.deleteNoteTag',[app.token,e.id],(id)=>{
                    for(var i=0;i<this.tagList.length;i++){
                        var t=this.tagList[i];
                        if(t.id==e.id){
                            this.tagList.splice(i,1);
                        }
                    }
                });
            },
            confirmAdd(){
                if(this.newTagContent==null||this.newTagContent==''){
                   return;
                }
                var tagBean={
                    name:this.newTagContent
                }
                app.invoke('NoteAction.addNoteTag',[app.token,tagBean],(id)=>{
                    this.tagList.push({
                        id:id,
                        name:this.newTagContent,
                        isSelected:true
                    })
                    this.newTagContent="";
                });
            }
        }
    }
</script>