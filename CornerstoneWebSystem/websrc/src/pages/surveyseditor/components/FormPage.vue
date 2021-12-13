<style scoped>
.page{
    background-color: #F1F4F5;
    height:calc(100vh - 48px);
    display: flex;
}
.toolbox{
    width:290px;
    border-right: 1px solid #ccc;
    height:calc(100vh - 48px);
    overflow: auto;
    background-color: #fff;
    padding:15px;
    padding-top:30px;
}
.form-box{
    flex:1;
    height:calc(100vh - 48px);
    overflow: auto;
    display: flex;
    align-items: center;
    flex-direction: column;
    padding:30px 0;
}
.form{
    width:700px;
    background-color: #fff;
    border:1px solid #eee;
    border-radius: 3px;
    padding:40px;
}
.tool-form-item{
    display: inline-block;
    width:120px;
    text-align: center;
    padding:3px 10px;
    border:1px solid #eee;
    margin-right:5px;
    background-color: #F4F6FB;
}
.tool-form-item:hover{
    cursor:move;
}
.side-box{
    position: fixed;
    top:48px;
    right:0;
    width:300px;
    height:calc(100vh - 48px);
    background-color: #fff;
    border-left: 1px solid #ccc;
    z-index:100;
    overflow: auto;
    box-shadow: -4px 6px 14px 6px rgba(225,225,225,0.5);
}
.side-box-inner{
    padding:10px;
}
.side-box-note{
    margin: 5px 0;
    color:#666;
}
.form-desc{
    font-size:12px;
    color:#666;
    margin-bottom:5px;
}
.node-desc{
    font-size:12px;
    color:#4494F1;
    margin:5px 0;
}
.form-title-row{
    padding:10px 0;
}
.form-title-row input{
    font-size:18px;
    font-weight: bold;
    color: #333;
    outline: none;
    border: none;
    width:100%;
    text-align: center;
}
.form-subtitle-row{
    padding:5px 0;
    padding-top:0;
}
.form-subtitle-row input{
    font-size:14px;
    font-weight: bold;
    color: #666;
    outline: none;
    border: none;
    text-align: center;
    width:100%;
}
.form-line{
    padding:5px 0;
    border-bottom: 1px solid #eee;
}
.form-item-box {
    min-height: 300px;
    margin: 10px 0;
    padding-bottom:70px;
    border:2px solid #fff;
}
.form-item-box-inner{
    display: flex;
    flex-wrap: wrap;
    width:100%;
}
.dragover{
    border:2px dashed #eee;
}

.op-note{
    color:#999;
    font-size:18px;
    padding:15px 0;
    text-align: center;
    width:100%;
}
.form-item-type-name{
    font-size:16px;
    color:#333;
    font-weight: bold;
    padding:15px 0;
}
</style>
<i18n>
{
	"en": {
        "基础组件": "基础组件",
		"静态字段": "静态字段",
		"成员部门": "成员部门",
		"高级组件": "高级组件",
		"请输入表单标题": "请输入表单标题",
		"请输入副标题": "请输入副标题",
		"将组件拖动到此区域可新增组件，拖动组件可调整顺序": "将组件拖动到此区域可新增组件，拖动组件可调整顺序",
		"名称": "名称",
		"删除": "删除",
		"描述": "描述",
		"内容": "内容",
		"保存": "保存",
		"是否必填": "是否必填",
		"显示宽度": "显示宽度",
		"最小长度": "最小长度",
		"最大长度": "最大长度",
		"最小值": "最小值",
		"最大值": "最大值",
		"选项": "选项",
		"表格列": "表格列",
		"文本格式": "文本格式",
		"邮箱": "邮箱",
		"手机": "手机",
		"身份证号码": "身份证号码",
		"日期格式": "日期格式",
		"年月日": "年月日",
		"年月日小时分钟": "年月日小时分钟",
		"年份": "年份",
		"月份": "月份",
		"类型": "类型",
		"单一值": "单一值",
		"多个值": "多个值",
		"默认": "默认",
		"一般": "一般",
		"警告": "警告",
		"严重": "严重",
		"水平": "水平",
		"垂直": "垂直",
		"提交人": "提交人",
		"当前日期": "当前日期",
		"流水号": "流水号",
		"单行文本": "单行文本",
		"多行文本": "多行文本",
		"富文本": "富文本",
		"数字": "数字",
		"附件": "附件",
		"日期选择": "日期选择",
		"时间选择": "时间选择",
		"下拉框": "下拉框",
		"单选框": "单选框",
		"复选框": "复选框",
		"表格": "表格",
		"分组标题": "分组标题",
		"静态文本": "静态文本",
		"文字提醒": "文字提醒",
		"成员选择": "成员选择",
		"部门选择": "部门选择",
		"企业角色": "企业角色",
		"项目角色": "项目角色",
		"系统字段": "系统字段",
		"请输入表单名称": "请输入表单名称",
		"表单中有组件的名称为空，请修改": "表单中有组件的名称为空，请修改",
		"表单组件名称重复": "表单组件【{0}】名称重复，请修改",
		"保存成功": "保存成功",
		"列": "列"
    },
	"zh_CN": {
		"基础组件": "基础组件",
		"静态字段": "静态字段",
		"成员部门": "成员部门",
		"高级组件": "高级组件",
		"请输入表单标题": "请输入表单标题",
		"请输入副标题": "请输入副标题",
		"将组件拖动到此区域可新增组件，拖动组件可调整顺序": "将组件拖动到此区域可新增组件，拖动组件可调整顺序",
		"名称": "名称",
		"删除": "删除",
		"描述": "描述",
		"内容": "内容",
		"保存": "保存",
		"是否必填": "是否必填",
		"显示宽度": "显示宽度",
		"最小长度": "最小长度",
		"最大长度": "最大长度",
		"最小值": "最小值",
		"最大值": "最大值",
		"选项": "选项",
		"表格列": "表格列",
		"文本格式": "文本格式",
		"邮箱": "邮箱",
		"手机": "手机",
		"身份证号码": "身份证号码",
		"日期格式": "日期格式",
		"年月日": "年月日",
		"年月日小时分钟": "年月日小时分钟",
		"年份": "年份",
		"月份": "月份",
		"类型": "类型",
		"单一值": "单一值",
		"多个值": "多个值",
		"默认": "默认",
		"一般": "一般",
		"警告": "警告",
		"严重": "严重",
		"水平": "水平",
		"垂直": "垂直",
		"提交人": "提交人",
		"当前日期": "当前日期",
		"流水号": "流水号",
		"单行文本": "单行文本",
		"多行文本": "多行文本",
		"富文本": "富文本",
		"数字": "数字",
		"附件": "附件",
		"日期选择": "日期选择",
		"时间选择": "时间选择",
		"下拉框": "下拉框",
		"单选框": "单选框",
		"复选框": "复选框",
		"表格": "表格",
		"分组标题": "分组标题",
		"静态文本": "静态文本",
		"文字提醒": "文字提醒",
		"成员选择": "成员选择",
		"部门选择": "部门选择",
		"企业角色": "企业角色",
		"项目角色": "项目角色",
		"系统字段": "系统字段",
		"请输入表单名称": "请输入表单名称",
		"表单中有组件的名称为空，请修改": "表单中有组件的名称为空，请修改",
		"表单组件名称重复": "表单组件【{0}】名称重复，请修改",
		"保存成功": "保存成功",
		"列": "列"
	}
}
</i18n>
<template>
    <div class="page">
        <div class="toolbox scrollbox">
            <Form label-position="top">
                <FormItem v-for="group in ['基础组件','静态字段']" :label="$t(group)" :key="'g'+group">
                    <drag class="tool-form-item text-no-wrap" :key="'fi'+item.id"  v-for="item in getFormItemTypeByGroup(group)" :transfer-data="item">
                        <div>{{$t(item.name)}}</div>
                    </drag>
                </FormItem>
            </Form>
        </div>
        <div class="form-box scrollbox" @click="selectFormItem(null)">
            <div class="form">
                <div class="form-title-row"><input  v-model.trim="formItem.title" type="text" :placeholder="$t('请输入表单标题')"/></div>
                <div class="form-line"></div>
                <drop class="form-item-box" :class="{'dragover':dragover}" 
                    @drop="handleDrop" 
                    @dragleave="dragover = false" @dragover="onDragOver">
                    <div v-if="formItemList.length==0" class="op-note">{{$t('将组件拖动到此区域可新增组件，拖动组件可调整顺序')}}</div>
                    
                    <draggable class="form-item-box-inner" v-model="formItemList" 
                        :options="{draggable:'.form-item-holder'}">
                         <FormItemHolder :select="editFormItem==item||hoverFormItemId==item.id"
                            @copy="copyFormItem(item,itemIdx)"
                            @delete="deleteFormItem0(item,itemIdx)"
                            @click.native.stop="selectFormItem(item)" 
                            v-for="(item,itemIdx) in formItemList" :key="item.id" :item="item"/>
                    </draggable>
                </drop>
            </div>
        </div>

        <div v-if="editFormItem" class="side-box scrollbox">
            <div class="side-box-inner">
                <Form label-position="top" :model="editFormItem" :rules="formRule">
                    <div class="form-item-type-name">{{getTypeName(editFormItem.type)}}</div>
                    <FormItem :label="$t('名称')"  v-if="showEditProp(editFormItem.type,'name')" prop="name"> 
                        <Input :maxlength="25" v-model="editFormItem.name"/>
                    </FormItem>
                    <FormItem label="">
                        <Button @click="deleteFormItem" type="error">{{$t('删除')}}</Button>
                     </FormItem>
                     
                     <FormItem :label="$t('描述')" v-if="showEditProp(editFormItem.type,'remark')" prop="remark">
                        <Input :maxlength="100" v-model="editFormItem.remark"/>
                     </FormItem>

                    <FormItem :label="$t('内容')" v-if="showEditProp(editFormItem.type,'content')" prop="content">
                        <RichtextEditor ref="contentEditor" v-model="editFormItem.content"/>
                        <div style="margin-top:5px"><Button type="default" @click="saveRichText">{{$t('保存')}}</Button></div>
                     </FormItem>

                     <FormItem :label="$t('是否必填')" v-if="showEditProp(editFormItem.type,'required')">
                       <i-Switch v-model="editFormItem.required"></i-Switch>
                     </FormItem>
                     <FormItem :label="$t('显示宽度')" v-if="showEditProp(editFormItem.type,'width')">
                        <RadioGroup v-model="editFormItem.width">
                            <Radio :label="25" >25%</Radio>
                            <Radio :label="50" >50%</Radio>
                            <Radio :label="75" >75%</Radio>
                            <Radio :label="100">100%</Radio>
                        </RadioGroup>
                     </FormItem>

                    <FormItem :label="$t('最小长度')" v-if="showEditProp(editFormItem.type,'minLength')">
                        <InputNumber :max="500" :min="1" v-model="editFormItem.minLength"></InputNumber>
                    </FormItem>

                    <FormItem :label="$t('最大长度')" v-if="showEditProp(editFormItem.type,'maxLength')">
                        <InputNumber :max="500" :min="1" v-model="editFormItem.maxLength"></InputNumber>
                    </FormItem>

                    <FormItem :label="$t('最小值')" v-if="showEditProp(editFormItem.type,'minValue')">
                        <InputNumber  v-model="editFormItem.minValue"></InputNumber>
                    </FormItem>

                    <FormItem :label="$t('最大值')" v-if="showEditProp(editFormItem.type,'maxValue')">
                        <InputNumber  v-model="editFormItem.maxValue"></InputNumber>
                    </FormItem>

                    <FormItem :label="$t('选项')" v-if="showEditProp(editFormItem.type,'optionList')">
                        <OptionList :single="editFormItem.type=='select'||editFormItem.type=='radio'" v-model="editFormItem.optionList"/>
                    </FormItem>

                    <FormItem :label="$t('表格列')" v-if="showEditProp(editFormItem.type,'columnList')">
                        <ColumnList v-model="editFormItem.columnList"/>
                    </FormItem>

                    <FormItem :label="$t('文本格式')" v-if="showEditProp(editFormItem.type,'textFormat')">
                       <Select clearable v-model="editFormItem.textFormat">
                            <Option value="url">URL</Option>
                            <Option value="email">{{$t('邮箱')}}</Option>
                            <Option value="mobile">{{$t('手机')}}</Option>
                            <Option value="idcard">{{$t('身份证号码')}}</Option>
                        </Select>
                    </FormItem>

                    <FormItem :label="$t('日期格式')" v-if="showEditProp(editFormItem.type,'dateFormat')">
                       <Select v-model="editFormItem.dateFormat">
                            <Option value="date">{{$t('年月日')}}</Option>
                            <Option value="datetime">{{$t('年月日小时分钟')}}</Option>
                            <Option value="year">{{$t('年份')}}</Option>
                            <Option value="month">{{$t('月份')}}</Option>
                        </Select>
                    </FormItem>

                    <FormItem :label="$t('类型')" v-if="showEditProp(editFormItem.type,'countType')">
                       <Select  v-model="editFormItem.countType">
                            <Option value="single">{{$t('单一值')}}</Option>
                            <Option value="multiple">{{$t('多个值')}}</Option>
                        </Select>
                    </FormItem>

                    <FormItem :label="$t('类型')" v-if="showEditProp(editFormItem.type,'alertType')">
                       <Select  v-model="editFormItem.alertType">
                            <Option value="info">{{$t('默认')}}</Option>
                            <Option value="success">{{$t('一般')}}</Option>
                            <Option value="warning">{{$t('警告')}}</Option>
                            <Option value="error">{{$t('严重')}}</Option>
                        </Select>
                    </FormItem>

                     <FormItem :label="$t('类型')" v-if="showEditProp(editFormItem.type,'optionLayout')">
                       <Select v-model="editFormItem.optionLayout">
                            <Option value="horizontal">{{$t('水平')}}</Option>
                            <Option value="vertical">{{$t('垂直')}}</Option>
                        </Select>
                    </FormItem>

                    <FormItem :label="$t('类型')" v-if="showEditProp(editFormItem.type,'systemValueType')">
                       <Select v-model="editFormItem.systemValueType">
                            <Option value="submitAccount">{{$t('提交人')}}</Option>
                            <Option value="systemTime">{{$t('当前日期')}}</Option>
                            <Option value="serialNO">{{$t('流水号')}}</Option>
                        </Select>
                    </FormItem>

                 </Form>
            </div>
        </div>

    </div>
</template>

<script>
import { Drag, Drop } from 'vue-drag-drop';
import draggable from 'vuedraggable'
import SurveysPageMixin from './SurveysPageMixin'
export default {
    mixins: [componentMixin,SurveysPageMixin],
    components:{
        Drag,Drop,draggable
    },
    data(){
        return {
            dragover:false,
            surveysDefine:null,
            hoverFormItemId:null,
            formItem:{
                title:null,
                subTitle:null,
                fieldList:null
            },
            formRule:{
                "name":[vd.req,vd.name2_20],
                "remark":[vd.desc]
            },
            formItemTypeProps:{
                "text-single":{
                    name:true,
                    remark:true,
                    required:true,
                    width:true,
                    minLength:true,
                    maxLength:true,
                    textFormat:true
                },
                "text-area":{
                    name:true,
                    remark:true,
                    required:true,
                    width:true,
                    minLength:true,
                    maxLength:true
                },
               
                "text-number":{
                    name:true,
                    remark:true,
                    required:true,
                    width:true,
                    minValue:true,
                    maxValue:true
                },
                "date":{
                    name:true,
                    remark:true,
                    required:true,
                    width:true,
                    dateFormat:true
                },
                "time":{
                    name:true,
                    remark:true,
                    required:true,
                    width:true,
                },
                "attachment":{
                    name:true,
                    remark:true,
                    required:true,
                    width:true,
                    countType:true
                },
                "static-group":{
                    name:true
                },
                "static-label":{
                    name:true,
                    content:true,
                    width:true,
                },
                "static-alert":{
                    name:true,
                    remark:true,
                    alertType:true
                },
                'system-value':{
                    name:true,
                    remark:true,
                    width:true,
                    systemValueType:true
                },
                'select':{
                    name:true,
                    remark:true,
                    required:true,
                    width:true,
                    optionList:true
                },
                'radio':{
                    name:true,
                    remark:true,
                    required:true,
                    width:true,
                    optionList:true,
                    optionLayout:true
                },
                'table':{
                    name:true,
                    remark:true,
                    width:true,
                    required:true,
                    columnList:true
                },
                'checkbox':{
                    name:true,
                    remark:true,
                    required:true,
                    width:true,
                    optionList:true,
                    optionLayout:true
                },
              
            },
            formItemTypeList:[
                {group:'基础组件',name:'单行文本',id:'text-single',minLength:1,maxLength:100},
                {group:'基础组件',name:'多行文本',id:'text-area',minLength:1,maxLength:200},
                {group:'基础组件',name:'数字',id:'text-number',minValue:1,maxValue:10000000},
                {group:'基础组件',name:'附件',id:'attachment',countType:'single'},
                {group:'基础组件',name:'日期选择',id:'date',dateFormat:'date'},
                {group:'基础组件',name:'时间选择',id:'time'},
                {group:'基础组件',name:'下拉框',id:'select',optionList:[]},
                {group:'基础组件',name:'单选框',id:'radio',optionList:[],optionLayout:'horizontal'},
                {group:'基础组件',name:'复选框',id:'checkbox',optionList:[],optionLayout:'horizontal'},
                //
                {group:'静态字段',name:'分组标题',id:'static-group',width:100},
                {group:'静态字段',name:'静态文本',id:'static-label'},
                {group:'静态字段',name:'文字提醒',id:'static-alert',alertType:"info",width:100},
                //
            ],
            formItemList:[],
            editFormItem:null,
        }
    },
    mounted(){
        
    },
    watch:{
        formItemList(val){
            this.isSaved=false;
        }
    },
    methods:{
        pageLoad(){
            app.currentPage=this;
            this.surveysDefine=app.surveysDefine;
            this.loadData();
            //
            this.watchSaveProp('editFormItem');
        },
        getTypeName(type){
            for(var i=0;i<this.formItemTypeList.length;i++){
                var t=this.formItemTypeList[i];
                if(t.id==type){
                    return t.name;
                }
            }
            return "";
        },
        onDragOver(transferData, nativeEvent){
            var to = nativeEvent.toElement;
            var id=to.getAttribute('data-id');
            if(id==null){
                id=to.parentElement.getAttribute('data-id');
            }
            this.hoverFormItemId=id;
            this.dragover = true
        },
        loadData(){
            app.invoke('SurveysAction.getSurveysFormDefineById',[app.token,
                this.surveysDefine.id],(info)=>{
                this.formItem=info;
                try{
                    if(info.fieldList){
                        this.formItemList=JSON.parse(info.fieldList)
                    }
                }catch(e){
                    console.log(e);
                }
                this.$nextTick(()=>{
                    this.isSaved=true;
                })
            });
        },
        confirm(callback){
            if(this.formItem.title==null||this.formItem.title==''){
                app.toast(this.$t('请输入表单名称'));
                return;
            }
            for(var i=0;i<this.formItemList.length;i++){
                var field=this.formItemList[i];
                if(field.name==null||field.name==''){
                    app.toast(this.$t("表单中有组件的名称为空，请修改"));
                    return;
                }
            }
            //
            var nodeNameCount={};
            for(var i=0;i<this.formItemList.length;i++){
                var field=this.formItemList[i]; 
                if(nodeNameCount[field.name]){
                    app.toast(this.$t('表单组件名称重复',[field.name]));
                    return;
                }else{
                    nodeNameCount[field.name]=true;
                }
            }
            //
            this.formItem.fieldList=JSON.stringify(this.formItemList);
            app.invoke('SurveysAction.updateSurveysFormDefine',[app.token,this.formItem],(info)=>{
                app.toast(this.$t('保存成功'));
                this.isSaved=true;
                if(callback){
                    callback();
                }
            })
        },
        showEditProp(itemType,fieldType){
            var t=this.formItemTypeProps[itemType];
            if(t!=null){
                return t[fieldType]!=null;
            }
            return false;
        },
        selectFormItem(item){
            var lastSaved=this.isSaved;
            this.editFormItem=item;
            this.$nextTick(()=>{
                this.isSaved=lastSaved;
            }) 
        },
        saveRichText(){
            var t=this.$refs.contentEditor.getValue();
            this.editFormItem.content=t;
        },
        deleteFormItem(){
            for(var i=0;i<this.formItemList.length;i++){
                if(this.formItemList[i]==this.editFormItem){
                    this.formItemList.splice(i,1);
                    this.editFormItem=null;
                    return;
                }
            }
        },
        deleteFormItem0(item,idx){
            this.formItemList.splice(idx,1);
        },
        copyFormItem(item,idx){
            var newObj = JSON.parse(JSON.stringify(item))
            newObj.id=this.guid();
            this.formItemList.splice(idx+1,0,newObj);
        },
        handleDrop(data) {
            if(data==null){
                return;
            }
            var item={
                name:this.$t(data.name),
                type:data.id,
                width:data.width==null?50:data.width,
                required:true,
                id:this.guid()
            }
            if(item.type=='static-alert'){
                item.remark=item.name+this.$t("内容");
            }
            if(item.type=='static-label'){
                item.content=item.name+this.$t("内容");
            }
            if(data.dateFormat){
                item.dateFormat=data.dateFormat;
            }
            if(data.minLength){
                item.minLength=data.minLength;
            }
            if(data.maxLength){
                item.maxLength=data.maxLength;
            }
            if(data.optionList){
                item.optionList=[
                    {checked:false,value:this.$t("选项")+"1"},
                    {checked:false,value:this.$t("选项")+"2"},
                    {checked:false,value:this.$t("选项")+"3"},
                ];
            }
            if(data.columnList){
                item.columnList=[
                    {width:100,name:this.$t("列")+"1"},
                    {width:100,name:this.$t("列")+"2"},
                    {width:100,name:this.$t("列")+"3"},
                ];
            }
            if(data.countType){
                item.countType=data.countType;
            }
            if(data.alertType){
                item.alertType=data.alertType;
            }
            if(data.minValue){
                item.minValue=data.minValue;
            }
            if(data.maxValue){
                item.maxValue=data.maxValue;
            }
            if(data.optionLayout){
                item.optionLayout=data.optionLayout;
            }
            if(data.systemValueType){
                item.systemValueType=data.systemValueType;
            }
            //
            if(this.hoverFormItemId){
                for(var i=0;i<this.formItemList.length;i++){
                    var t=this.formItemList[i];
                    if(t.id==this.hoverFormItemId){
                        this.formItemList.splice(i,0,item)
                        break;
                    }
                }
            }else{
                this.formItemList.push(item)
            }
            this.hoverFormItemId=null;
            this.dragover=false;
		},
        getFormItemTypeByGroup(group){
            var list=[];
            this.formItemTypeList.forEach(item=>{
                if(item.group==group){
                    list.push(item);
                }
            })
            return list;
        },
        guid() {
            return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                var r = Math.random() * 16 | 0,
                    v = c == 'x' ? r : (r & 0x3 | 0x8);
                return v.toString(16);
            });
        }
    }
}
</script>