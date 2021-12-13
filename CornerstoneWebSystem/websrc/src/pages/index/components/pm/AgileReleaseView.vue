<style scoped>
    .table-box {
        background-color: #fff;
        padding: 30px;
        padding-top: 10px;
        box-shadow: 0px 2px 10px 0px rgba(225, 225, 225, 0.5);
        border: 1px solid rgba(216, 216, 216, 1);
    }

    .table-info {
        color: #999;
        text-align: center;
    }

    .table-count {
        background-color: #e8e8e8;
        color: #666;
        padding: 3px 5px;
        border-radius: 3px;
    }

    .table-col-name {
        display: inline-block;
        vertical-align: middle;
    }

    .table-remark {
        color: #999;
        margin-top: 5px;
    }

    .nodata {
        padding: 60px;
        font-size: 20px;
        color: #999;
        text-align: center;
    }

    .table-info-row {
        display: flex;
        align-items: center;
    }

    .card-box {
        display: block;
    }

    .right-opt {
        display: flex;
        align-items: center;
        flex-direction: row-reverse;
    }


    .release-item {
        display: flex;
        align-items: center;
    }

    .release-name {
        font-size: 14px;
        color: #333;
        padding-right: 10px;
    }

    .release-time {
        font-size: 12px;
        color: #666;
    }

    .release-note {
        margin-top: 10px;
        font-size: 13px;
    }

    .input-search /deep/ input{
        border-radius: 16px !important;

    }
</style>
<i18n>
    {
    "en": {
    "个Release": "{0} Release",
    "创建Release":"Create Release",
    "发布":"released",
    "编辑":"Edit",
    "暂无数据":"No Data"
    },
    "zh_CN": {
    "个Release": "{0}个Release",
    "创建Release":"创建Release",
    "发布":"发布",
    "编辑":"编辑",
    "暂无数据":"暂无数据"
    }
    }
</i18n>
<template>
    <div v-if="list" style="padding:20px">
        <Row>
            <Col span="6">&nbsp;</Col>
            <Col span="12">
                <div class="table-info">
                    <span class="table-count">{{$t('个Release',[list.length])}}</span>
                </div>
            </Col>
            <Col span="6" class="right-opt">
                <IconButton
                    v-if="prjPerm('project_release_config')&&!projectDisabled"
                    icon="md-add"
                    @click="showEditReleaseDialog()"
                    :title="$t('创建Release')"
                ></IconButton>
            </Col>
        </Row>

        <div class="card-box">
            <Timeline>
                <TimelineItem
                    color="green"
                    v-for="(item,idx) in filterList"
                    :key="item.id"
                >
                    <Icon
                        v-if="idx==filterList.length-1&&idx!=0"
                        type="md-plane"
                        slot="dot"
                    ></Icon>
                    <Icon v-if="idx==0" type="md-paper-plane" slot="dot"></Icon>

                    <div>
                        <div class="release-item">
                            <span class="release-name">{{item.name}}</span>
                            <span class="release-time">{{item.releaseDate|fmtDate}} {{$t('发布')}}</span>
                            <Tag v-if="item.category" style="margin-left: 10px;">{{item.category}}</Tag>
                            <IconButton
                                v-if="prjPerm('project_release_config')"
                                @click="showEditReleaseDialog(item)"
                                :tips="$t('编辑')"
                                icon="ios-settings-outline"
                            ></IconButton>
                        </div>
                        <div class="release-note">
                            <RichtextLabel v-model="item.description"></RichtextLabel>
                        </div>
                    </div>
                </TimelineItem>
            </Timeline>
            <div class="nodata" v-if="list.length==0">{{$t('暂无数据')}}</div>
        </div>
    </div>
</template>

<script>
    export default {
        mixins: [componentMixin],
        props:['filter'],
        data() {
            return {
                list: [],
                filterList:[],
                categories:[]
            };
        },
        mounted() {
            this.loadData();
        },
        watch:{
            filter(val){
                let list =[];
                for (let i = 0; i < this.list.length; i++) {
                    let item = this.list[i];
                    if(!!val.name){
                        if(item.name.indexOf(val.name)===-1){
                            continue;
                        }
                    }
                    if(!!val.category&&val.category.length>0){
                        if(!val.category.contains(item.category)){
                            continue;
                        }
                    }

                    list.push(this.list[i]);
                }
                this.filterList = list;
                console.log(val)
          }
        },
        methods: {
            loadData() {
                var query = {
                    pageIndex: 1,
                    pageSize: 1000,
                    projectId: app.projectId
                };
                app.invoke(
                    "BizAction.getProjectReleaseList",
                    [app.token, query],
                    info => {
                        this.list = info.list;
                        this.filterList = this.list;


                        if(!Array.isEmpty(this.list)){

                            let categories = this.list.filter(item=>!!item.category).map(item=>item.category);
                            if(!Array.isEmpty(categories)){
                                this.categories= Array.from(new Set(categories))
                            }

                            let categoryList=[];
                            this.list.forEach(item=>{
                                let contains = false;
                                for (let i = 0; i < categoryList.length; i++) {
                                    if(categoryList[i].name===item.category){
                                        contains = true;
                                        categoryList[i].count++;
                                    }
                                }
                                if(!contains&&!!item.category){
                                    categoryList.push({name:item.category,count:1})
                                }
                            });
                            this.$emit("on-filter",categoryList);
                        }
                    }
                );
            },
            showEditReleaseDialog(item) {
                app.showDialog(ReleaseEditDialog, {
                    projectId: app.projectId,
                    categories:this.categories,
                    item: item
                });
            }
        }
    };
</script>
