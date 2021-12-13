<style scoped>
.step-item-row{
    border-left:4px solid #aaa;
    list-style: none;
    padding-left:10px;
}
.step-box ul{
    padding-left:10px;
    list-style: none;
}
.step-item{
    padding-top:5px;
}

</style>
<i18n>
{
    "en": {
        "满足所有条件": "All",
        "满足任一条件": "Any",
        "增加查询条件":"Add query",
        "增加子查询":"Add subquery",
        "删除查询条件":"Delete",
        "删除":"Delete",
        "字段":"Field",
        "条件":"Condition",
        "值":"Value",
        "等于":"equal",
        "不等于":"not equal",
        "大于":"greater",
        "小于":"less",
        "等于空":"is null",
        "不等于空":"is not null"
    },
    "zh_CN": {
        "满足所有条件": "满足所有条件",
        "满足任一条件": "满足任一条件",
        "增加查询条件":"增加查询条件",
        "增加子查询":"增加子查询",
        "删除查询条件":"删除查询条件",
        "删除":"删除",
        "字段":"字段",
        "条件":"条件",
        "值":"值",
        "等于":"等于",
        "不等于":"不等于",
        "大于":"大于",
        "小于":"小于",
        "等于空":"等于空",
        "不等于空":"不等于空"
    }
}
</i18n>
<template>
      <div class="step-item">
            <div class="step-item-name">
                 <div  v-if="data.type!=0">
                    <Select v-model="data.type" style="width:150px">
                            <Option :value="1" >{{$t('满足所有条件')}}</Option>
                            <Option :value="2" >{{$t('满足任一条件')}}</Option>
                    </Select>
                    <IconButton @click="addQuery(0)" icon="ios-add" :title="$t('增加查询条件')"></IconButton>
                    <IconButton @click="addQuery(1)" icon="ios-add" :title="$t('增加子查询')"></IconButton>
                    <IconButton @click="deleteQuery" icon="ios-trash" :tips="$t('删除')"></IconButton>
                </div>

                <span v-if="data.type==0">
                    <Row>
                        <Col span="6">
                            <Select transfer :placeholder="$t('字段')" v-model="data.key">
                                <Option v-for="field in  queryInfo.fieldList" :key="field.id" :value="field.id">{{field.name}}</Option>
                            </Select>
                        </Col>
                        <Col span="4" style="padding-left:5px">
                             <Select transfer :placeholder="$t('条件')" v-model="data.operator">
                                <Option v-for="(op,opIndex) in operaterList" :value="op.value" :key="'op'+opIndex">{{op.name}}</Option>
                            </Select>
                        </Col>
                        <Col span="11" style="padding-left:5px">
                            <template v-if="data.operator<6&&keyField.isSystemField&&keyField.isShow">
                                <Input v-model.trim="data.stringValue" v-if="keyField.field=='name'" :placeholder="$t('值')"/>
                                <ExDatePicker v-model="data.dateValue" v-if="keyField.field=='startDate'||keyField.field=='finishTime'||keyField.field=='endDate'" :placeholder="$t('值')"/>
                                <ExDatePicker v-model="data.dateValue" v-if="keyField.field=='createTime'||keyField.field=='updateTime'" :placeholder="$t('值')"/>
                                <ExDatePicker v-model="data.dateValue" v-if="keyField.field=='expectEndDate'" :placeholder="$t('值')"/>

                                <InputNumber v-model="data.intValue" v-if="keyField.field=='workTime'||keyField.field=='workLoad'
                                ||keyField.field=='expectWorkTime'||keyField.field=='startDays'||keyField.field=='endDays'" :placeholder="$t('值')"/>

                                <Select transfer v-model="data.intValueList" filterable clearable multiple v-if="keyField.field=='statusName'">
                                    <Option v-for="status in queryInfo.statusList" :value="status.id" :key="'status'+status.id" >{{status.name}}</Option>
                                </Select>

                                <Select transfer v-model="data.intValueList" filterable clearable multiple v-if="keyField.field=='priorityName'">
                                    <Option v-for="priority in queryInfo.priorityList" :value="priority.id" :key="'status'+priority.id" >{{priority.name}}</Option>
                                </Select>

                                <Select transfer v-model="data.intValueList" filterable  clearable multiple v-if="keyField.field=='releaseName'">
                                    <Option v-for="release in queryInfo.releaseList" :value="release.id" :key="'release'+release.id" >{{release.name}}</Option>
                                </Select>

                                 <Select transfer v-model="data.intValueList" filterable  clearable multiple v-if="keyField.field=='subSystemName'">
                                    <Option v-for="subSystem in queryInfo.subSystemList" :value="subSystem.id" :key="'subSystem'+subSystem.id" >{{subSystem.name}}</Option>
                                </Select>

                                <Select transfer v-model="data.intValueList" filterable  clearable multiple v-if="keyField.field=='iterationName'">
                                    <Option v-for="iteration in queryInfo.iterationList" :value="iteration.id" :key="'iteration'+iteration.id" >{{iteration.name}}</Option>
                                </Select>

                                <Select transfer v-model="data.intValueList" filterable  clearable multiple v-if="keyField.field=='stageName'">
                                    <Option v-for="stage in queryInfo.stageList" :value="stage.id" :key="'stage'+stage.id" >{{stage.name}}</Option>
                                </Select>


                                <Select transfer v-model="data.intValueList" filterable  clearable multiple v-if="keyField.field=='repositoryName'">
                                    <Option v-for="repository in queryInfo.repositoryList" :value="repository.id" :key="'repository'+repository.id" >{{repository.name}}</Option>
                                </Select>

                                 <Select transfer v-model="data.intValueList" filterable clearable multiple v-if="keyField.field=='createAccountName'||keyField.field=='ownerAccountName'">
                                    <Option v-for="member in queryInfo.memberList" :value="member.accountId" :key="'member'+member.accountId" >{{member.accountName}}</Option>
                                </Select>

                                <Select transfer v-model="data.intValueList"  filterable clearable multiple v-if="keyField.field=='categoryIdList'">
                                    <Option v-for="cate in queryInfo.categoryList" :value="cate.id" :key="'cate'+cate.id" >{{cate.name}}</Option>
                                </Select>

                            </template>

                             <template v-if="data.operator<6&&keyField.isSystemField==false&&keyField.isShow">
                                <Input v-model.trim="data.stringValue" v-if="keyField.type==1" :placeholder="$t('值')"/>
                                <ExDatePicker v-model="data.dateValue" v-if="keyField.type==7" :placeholder="$t('值')"/>
                                <InputNumber v-model="data.intValue" v-if="keyField.type==8" :placeholder="$t('值')"/>

                                <Select transfer v-model="data.stringValueList" filterable  clearable multiple v-if="keyField.type==3||keyField.type==4">
                                    <Option v-for="member in keyField.valueRange" :value="member" :key="'cus'+member" >{{member}}</Option>
                                </Select>

                                <Select transfer v-model="data.intValueList" filterable  clearable multiple v-if="keyField.type==6">
                                    <Option v-for="member in queryInfo.memberList" :value="member.accountId" :key="'member'+member.accountId" >{{member.accountName}}</Option>
                                </Select>
                             </template>
                        </Col>
                        <Col span="3"> <IconButton @click="deleteQuery" icon="ios-trash" :tips="$t('删除查询条件')"></IconButton></Col>
                    </Row>
                </span>

            </div>
                <ul >
                <li class="step-item-row" v-for="(item,itemIdx) in data.children" :key="'item'+itemIdx">
                    <FilterStep :query-info="queryInfo" :data="item"></FilterStep>
                </li>
            </ul>
        </div>

</template>
<script>
export default {
    name:"FilterStep",
    props: ['data','queryInfo'],
    data (){
        return{
            keyField:{type:1,isSystemField:true}
        }
    },
    watch:{
        'data.key':function(val){
            this.setupItem();
            this.resetItem();
        }
    },
    mounted(){
        this.setupItem();
    },
    computed:{

        operaterList(){
            var list=[];
            list.push({value:1,name:this.$t("等于")})
            list.push({value:2,name:this.$t("不等于")})
            list.push({value:3,name:this.$t("大于")})
            list.push({value:4,name:this.$t("小于")})
            list.push({value:10,name:this.$t("等于空")})
            list.push({value:11,name:this.$t("不等于空")})

            return list;
        }
    },

    methods:{
        setupItem(){
            this.keyField=this.getKeyField(this.data.key);
        },
        resetItem(){
            this.data.operator=1;
            this.data.stringValueList=[];
            this.data.intValueList=[];
            this.data.stringValue=null;
            this.data.dateValue=new Date();
            this.data.intValue=null;
        },
        getKeyField(key){
            if(this.queryInfo.fieldList==null){
                return {type:1}
            }
            for(var i=0;i<this.queryInfo.fieldList.length;i++){
                var t=this.queryInfo.fieldList[i];
                if(t.id==key){
                    return t;
                }
            }
            return {type:1};
        },
        addQuery(type){
            if(this.data.children==null){
                this.data.children=[];
            }
            this.data.children.push(
                {type:type,children:[]}
            );
        },
        deleteQuery(){
                var list=this.$parent.data.children;
                for(var i=0;i<list.length;i++){
                    var t=list[i];
                    if(t==this.data){
                        list.splice(i,1);
                        break;
                    }
                }
        },

    }
}
</script>
