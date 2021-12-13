<style scoped lang="less">
    .link {
        color: #0094FB;
        text-decoration: underline;
        cursor: pointer;
    }

    code {
        margin-right: 5px;
        margin-left: 5px;
    }
    .content-container{
        width: 800px;
    }
    .t-input /deep/{
        input, textarea, div, span{
            outline: none;
            background: transparent;
            border: none;
            box-shadow: none;

            &:focus {
                outline: none;
                background: transparent;
                border: none;
                box-shadow: none;
            }

            &::selection {
                outline: none;
                background: transparent;
                border: none;
                box-shadow: none;
            }

            &::-moz-selection {
                outline: none;
                background: transparent;
                border: none;
                box-shadow: none;
            }
        }

    }
</style>
<i18n>
    {
    "en": {
    "分支配置提示":"You should config code associate page before you use this function,by configurating code branch you can   choose whether associate code with task or statistic code number for branch",
    "添加项目名":"Add new project name",
    "分支设置":"Config brance",
    "是否关联":"Whether associate to task",
    "是否统计":"Whether statistic code numbers",
    "保存成功":"Save success",
    "其他说明":"This function can only works for platforms like git 、gitlab, svn、github is not support yet",
    "默认说明":"If you are not config branch ,all branch will be associate to task and statistic"
    },
    "zh_CN": {
    "分支配置提示":"代码分支需要先配置[代码关联]，配置分支可选择是否对分支提交关联到任务及代码量的统计，未关联的分支将不会进行代码统计",
    "添加项目名":"添加项目名",
    "分支设置":"分支设置",
    "是否关联":"是否关联",
    "是否统计":"是否统计",
    "保存成功":"保存成功",
    "其他说明":"代码分支配置功能仅适用于git、gitlab版本控制平台，svn、github暂不支持",
    "默认说明":"如果未进行本页面的分支配置，则默认所有的分支将会被关联且会进行代码量统计"
    }
    }
</i18n>
<template>
    <div class="admin-page">
        <Alert>
            {{$t('其他说明')}}
        </Alert>
        <Alert>
            {{$t('默认说明')}}
        </Alert>
        <Alert>
            {{$t('分支配置提示')}}
        </Alert>

        <div class="content-container">
        <Form label-position="top">
            <FormItem :label="$t('添加项目名')">
                <Input style="width: 300px;" v-model="project" search enter-button="添加" placeholder="请输入项目名称" @on-enter="addProjectName"
                       @on-click="addProjectName"  @on-search="addProjectName"/>
            </FormItem>
        </Form>
        <table v-if="!Array.isEmpty(scmBranchList)" class="table-content table-grid table-content-small"
               style="border-top:1px solid #eee">
            <thead>
            <tr>
                <th style="width:180px">{{$t('项目名称')}}</th>
                <th style="width:180px">{{$t('分支名称')}}</th>
                <th style="width:120px">
                    {{$t('是否关联')}}
                </th>
<!--                <th style="width:120px" >-->
<!--                    {{$t('是否统计')}}-->
<!--                </th>-->
                <th style="width: 100px;"></th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="(item,idx) in scmBranchList" :key="item.id">
                <td>{{item.project}}</td>
                <td>
                    <span v-if="!edit">{{item.branch}}</span>
                    <Input v-else class="t-input" v-model="item.branch" placeholder="请输入分支名称"/>
                </td>
                <td>
                    <i-Switch @on-change="changeAssociate($event,idx)" v-model="item.isAssociate"></i-Switch>
                </td>
<!--                <td >-->
<!--                    <i-Switch  :disable="!item.isAssociate"  v-model="item.isStat"></i-Switch>-->
<!--                </td>-->
                <td style="text-align: center;">
                    <Icon size="22" type="ios-add" @click="copyScmBranch(item,idx)"/>
                    <Icon size="22" type="ios-trash-outline" @click="deleteScmBranch(item,idx)"/>
                </td>
            </tr>
            </tbody>
        </table>
        </div>
        <Button  style="margin-top: 20px;" v-if="!Array.isEmpty(scmBranchList)" type="default" @click="saveScmBranchList">{{$t('保存')}}</Button>
    </div>
</template>

<script>

    import IconButton from "../../../../components/ui/IconButton";
    export default {
        components: {IconButton},
        mixins: [componentMixin],
        data() {
            return {
                project: "",
                scmBranchQuery: {
                    companyId: app.company.id
                },
                scmBranchList: [],
                edit:false
            }
        },
        methods: {
            pageLoad() {
                app.invoke('BizAction.getScmBranchList', [app.token, this.scmBranchQuery], (info) => {
                    this.scmBranchList = info;
                })
            },
            saveScmBranchList() {
                var _this = this;
                app.invoke('BizAction.saveScmBranchList', [app.token, this.scmBranchList], (info) => {
                   app.toast(this.$t('保存成功'));
                })
            },
            addProjectName() {
                if(!this.project){
                    return;
                }
                this.edit = true;
                var contains = false;
                if(!Array.isEmpty(this.scmBranchList)){
                  contains= this.scmBranchList.filter(item=>item.project==this.project).length>0;
                }
                if(!contains){
                    this.scmBranchList.unshift({
                        project:this.project,
                        branch:null,
                        isAssociate:true,
                        isStat:true
                    })
                }
                this.branch=null;
            },
            changeAssociate(val,idx){
                if(!val){
                    this.$set(this.scmBranchList[idx],"isStat",false)
                }
            },
            copyScmBranch(item,idx){
                this.edit=true;
                var back = {
                    project:item.project,
                    branch:null,
                    isAssociate: true,
                    isStat:true
                }
                this.scmBranchList.splice(idx+1,0,back);
            },
            deleteScmBranch(item,idx){
                this.scmBranchList.splice(idx,1);
            }
        }
    }
</script>
