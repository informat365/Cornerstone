<style scoped>
  .page {
    min-height: calc(100vh - 51px);
    background-color: #f1f4f5;
    width: 100%;
    padding: 20px;
    text-align: center;
  }

  .chart-content {
    width: 1200px;
    display: inline-block;
    background-color: #fff;
    border: 1px solid #eee;
    border-radius: 5px;
    text-align: left;
  }

  .chart-opt-bar {
    padding: 20px;
    border-bottom: 1px solid #eee;
  }

  .chart-chart {
    padding: 20px 0;
  }

  .content-box {
    display: flex;
    align-items: center;
    padding: 10px;
    flex-direction: column;
  }

  .table-box {
    background-color: #fff;
    padding: 30px;
    padding-top: 10px;
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

  .nodata {
    padding: 60px;
    font-size: 20px;
    color: #999;
    text-align: center;
  }

  .create-account-select {
    position: relative;
    display: inline-block;
    color: #515a6e;
    width: 220px;
    font-size: 14px;
    cursor: pointer;
    background-color: #fff;
    border: 1px solid #dcdee2;
    outline: 0;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    box-sizing: border-box;
    vertical-align: middle;
  }

  .create-account-select:hover {
    border-color: #57a3f3
  }


  .create-account-select .placeholder {
    display: block;
    height: 32px;
    line-height: 32px;
    font-size: 12px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    padding-left: 8px;
    padding-right: 24px;
    color: #c5c8ce;
  }

  .create-account-select .selection {
    position: relative;
    display: block;
    outline: 0;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    cursor: pointer;
    background-color: #fff;
    min-height: 32px;
    max-height: 120px;
    overflow: auto;
    padding-left: 4px;
    padding-right: 24px;
  }

  .create-account-select .icon {
    position: absolute;
    top: 50%;
    right: 8px;
    line-height: 1;
    transform: translateY(-50%);
    font-size: 14px;
    color: #808695;
  }
</style>
<i18n>
  {
  "en": {
  "工时统计": "Worktime stat",
  "项目":"Project",
  "名称":"Name",
  "类型":"Type",
  "开始时间":"Start",
  "结束时间":"End",
  "查询":"Query",
  "暂无数据":"No Data",
  "时间":"Time",
  "工时":"Worktime",
  "内容":"Content",
  "创建人":"Created by",
  "小时":"H",
  "条数据 ， 总工时":"{0} items ， total {1} hours",
  "导出":"Export",
  "对象":"Object"
  },
  "zh_CN": {
  "工时统计": "工时统计",
  "名称": "名称",
  "项目":"项目",
  "类型":"类型",
  "开始时间":"开始时间",
  "结束时间":"结束时间",
  "查询":"查询",
  "暂无数据":"暂无数据",
  "项目":"项目",
  "时间":"时间",
  "工时":"工时",
  "内容":"内容",
  "创建人":"创建人",
  "小时":"小时",
  "条数据 ， 总工时":"{0} 条数据 ， 总工时 {1} 小时",
  "导出":"导出",
  "对象":"对象"
  }
  }
</i18n>
<template>
  <Modal
    ref="dialog"
    v-model="showDialog"
    :closable="true"
    :mask-closable="false"
    :loading="false"
    :title="$t('工时统计')"
    class="fullscreen-modal"
    width="100%"
    :footer-hide="true">
    <div class="page">
      <div class="chart-content">
        <div class="chart-opt-bar">
          <Form inline>
            <FormItem>
              <Select v-model="formItem.projectId" :placeholder="$t('项目')" style="width:200px">
                <Option v-for="item in projectList" :value="item.id" :key="'prj'+item.id">{{ item.name }}</Option>
              </Select>
            </FormItem>
            <FormItem>
              <Select clearable v-model="formItem.objectType" :placeholder="$t('类型')" style="width:150px">
                <template v-for="item in moduleList">
                  <Option v-if="item.objectType>0" :value="item.objectType" :key="'md'+item.id">{{ item.name }}</Option>
                </template>
              </Select>
            </FormItem>
            <FormItem>
              <CompanyUserSelect
                v-model="formItem.createAccountIdInList" clearable multiple style="width: 180px" />
            </FormItem>

            <FormItem>
              <ExDatePicker
                type="date"
                style="width:130px"
                v-model="formItem.startTimeStart"
                :placeholder="$t('开始时间')"></ExDatePicker>
            </FormItem>
            <FormItem>
              <ExDatePicker
                type="date"
                style="width:130px"
                :day-end="true"
                v-model="formItem.startTimeEnd"
                :placeholder="$t('结束时间')"></ExDatePicker>
            </FormItem>

            <FormItem>
              <Button type="default" @click="loadChartData()">{{$t('查询')}}</Button>
            </FormItem>

            <FormItem>
              <Button type="default" @click="exportData()">{{$t('导出')}}</Button>
            </FormItem>

          </Form>
        </div>
        <div class="chart-chart">
          <div class="table-info">
            <IconButton
              :size="15"
              :disabled="pageQuery.pageIndex==1"
              @click="changePage(-1)"
              icon="md-arrow-round-back"></IconButton>
            <span class="table-count">{{pageQuery.pageIndex}}/{{pageQuery.totalPage}} ， {{$t('条数据 ， 总工时',[pageQuery.total,pageQuery.totalHours])}}</span>
            <IconButton
              :size="15"
              :disabled="pageQuery.pageIndex==pageQuery.totalPage"
              @click="changePage(1)"
              icon="md-arrow-round-forward"></IconButton>


          </div>
          <div v-if="list" class="table-box">
            <table class="table-content " style="table-layout:fixed">
              <thead>
              <tr>
                <th style="width:250px;">{{$t('名称')}}</th>
                <th style="width:250px;">{{$t('项目')}}</th>
                <th style="width:110px;">{{$t('时间')}}</th>
                <th style="width:80px;">{{$t('工时')}}</th>
                <th style="width:100px;">{{$t('预计工时')}}</th>
                <th>{{$t('内容')}}</th>
                <th style="width:150px">{{$t('创建人')}}</th>
              </tr>
              </thead>

              <tbody>
              <tr v-for="item in list" :key="'todo'+item.id" class="table-row ">
                <td style="width:250px;" class="text-no-wrap">
                  #{{item.taskSerialNo}} {{item.taskName}}
                </td>
                <td style="width:250px;" class="text-no-wrap">
                  {{ item.projectName }}
                </td>

                <td style="width:110px;">
                  {{item.startTime|fmtDate}}
                </td>

                <td style="width:80px;">
                  {{item.hour}}{{$t('小时')}}
                </td>
                <td style="width:80px;">
                  {{item.expectWorkTime}}{{$t('小时')}}
                </td>
                <td class="text-no-wrap">
                  {{item.content}}
                </td>

                <td style="width:150px">
                  {{item.createAccountName}}
                </td>
              </tr>
              </tbody>
            </table>
            <div class="nodata" v-if="list.length==0">
              {{$t('暂无数据')}}
            </div>
          </div>

        </div>
      </div>
    </div>
  </Modal>
</template>

<script>

  export default {
    mixins: [componentMixin],
    data() {
      return {
        projectList: [],
        iterationList: [],
        moduleList: [],
        formItem: {
          projectId: null,
          startTimeStart: null,
          startTimeEnd: null,
          createAccountName: null,
          createAccountIdInList: null,
        },
        pageQuery: {
          totalHours: 0,
          total: 0,
          pageIndex: 1,
          pageSize: 50,
          totalPage: 1,
        },
        list: [],
      };
    },
    watch: {
      'formItem.projectId': function (val) {
        this.loadProjectModule();
      },
    },
    methods: {
      pageLoad() {
        this.loadProjectList();
        this.loadData(true);
      },
      loadProjectList() {
        app.invoke('BizAction.getMyProjectList', [app.token], list => {
          this.projectList = list;
        });
      },
      loadProjectModule() {
        this.formItem.objectType = null;
        app.invoke('BizAction.getProjectModuleInfoList', [app.token, this.formItem.projectId], (list) => {
          this.moduleList = list;
        });
      },

      loadChartData() {
        this.loadData(true);
      },
      loadData(resetPage) {
        if (resetPage) {
          this.pageQuery.pageIndex = 1;
        }
        var query = copyObject(this.pageQuery, this.formItem);
       /* if (query.startTimeEnd) {
          const startTimeEnd = new Date(query.startTimeEnd);
          if (!Number.isNaN(startTimeEnd.getTime())) {
            startTimeEnd.setDate(startTimeEnd.getDate() + 1);
            query.startTimeEnd = startTimeEnd.getTime();
          }
        }*/
        app.invoke('BizAction.getAllTaskWorkTimeLogList', [app.token, query], (info) => {
          this.list = info.list;
          this.pageQuery.total = info.count;
          this.pageQuery.totalHours = info.totalHours;
          if (info.count === 0) {
            this.pageQuery.totalPage = 1;
          } else {
            this.pageQuery.totalPage = Math.ceil(info.count / this.pageQuery.pageSize);
          }
        });
      },
      changePage(delta) {
        var t = this.pageQuery.pageIndex + delta;
        if (t <= 0 || t > this.pageQuery.totalPage) {
          return;
        }
        this.pageQuery.pageIndex = t;
        this.loadData();
      },
      exportData() {
        var query = {
          token: app.token,
          query: this.formItem,
        };
       /* if (query.query.startTimeEnd) {
          const startTimeEnd = new Date(query.query.startTimeEnd);
          if (!Number.isNaN(startTimeEnd.getTime())) {
            startTimeEnd.setDate(startTimeEnd.getDate() + 1);
            query.query.startTimeEnd = startTimeEnd.getTime();
          }
        }*/
        var queryString = JSON.stringify(query);
        console.log(queryString);
        var encoded = Base64.encode(queryString);
        window.open('/p/main/export_account_worktime_list?arg=' + encodeURIComponent(encoded));
      },
    },
  };
</script>
