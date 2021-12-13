<style scoped lang="less">
.search-input {
  width: 150px;
  height: 30px !important;
  min-height: 30px !important;
  margin: 0 10px;

  &::-webkit-input-placeholder,
  &:-moz-placeholder {
    color: #eee;
  }
}

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

.left-day {
  font-weight: bold;
  color: #17a7ed;
  margin-left: 5px;
}

.left-overdue {
  color: #f84f84;
}

.table-col-name {
  display: inline-block;
  vertical-align: middle;
}

.nodata {
  padding: 60px;
  font-size: 20px;
  color: #999;
  text-align: center;
}

.group-tr {
  background-color: #f8f8f9;

  font-weight: bold;
}

.group-tr td {
  background-color: #f8f8f9;
  font-size: 16px;
  padding: 0;
}

.group-icon {
  cursor: pointer;
}

.group-icon-selected {
  color: #0094fb;
}

.report-project {
  background-color: #eeeeee;
  color: #555;
  font-size: 12px;
  padding: 2px 5px;
  text-align: center;
  display: inline-block;
  border-radius: 3px;
  margin-right: 5px;
  font-weight: bold;
  margin-left: 5px;
}

.group-name {
  padding-left: 12px;
  display: inline-block;
}

.report-name-row {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.nodata {
  padding: 60px;
  font-size: 16px;
  color: #999;
  text-align: center;
}

.chart-opt-bar {
  padding: 15px 0;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  align-content: flex-start;
  flex-wrap: wrap;

  &-date {
    margin-top: 5px;
    display: flex;
    align-items: center;
    align-content: flex-start;
    justify-content: space-between;
    min-width: 550px;

    .btn-date-group,
    .btn-prev-week,
    .btn-next-week {
      cursor: pointer;
      color: #009af4;
      user-select: none;
      display: flex;
      align-items: center;
      align-content: center;
      margin: 0 10px;
    }

    .btn-next-week.disabled {
      cursor: not-allowed;
      color: #dcdee2;
    }

    .current-week {
      font-size: 18px;
      font-weight: bold;

      .ivu-date-picker {
        width: 150px !important;
        font-weight: 500 !important;
        font-size: 14px;
        margin-right: 5px;
      }
    }
  }
}
</style>
<i18n>
    {
    "en": {
    "个汇报":"{0} items",
    "汇报模板": "Template",
    "汇报标题": "Title",
    "汇报周期": "Period",
    "汇报人":"Submitter",
    "审核人":"Reviewer",
    "状态":"Status",
    "暂无数据":"No Data",
    "上一周": "Last Week",
    "下一周": "Next Week",
    "本周": "This Week",
    "上个月": "Last Month",
    "下个月": "Next Month",
    "本月": "This Month",
    "自定义": "Custom",
    "周": "Week",
    "月": "Month",
    "项目": "Associate project",
    "状态": "Status",
    "汇报类型":"Report period",
    "批量删除":"Batch delete"
    },
    "zh_CN": {
    "个汇报":"{0}个汇报",
    "汇报模板": "汇报模板",
    "汇报标题": "标题",
    "汇报周期": "汇报周期",
    "汇报人":"汇报人",
    "审核人":"审核人",
    "状态":"状态",
    "暂无数据":"暂无数据",
    "上一周": "上一周",
    "下一周": "下一周",
    "本周": "本周",
    "上个月": "上个月",
    "下个月": "下个月",
    "本月": "本月",
    "自定义": "自定义",
    "周": "周",
    "月": "月",
    "项目": "关联项目",
    "状态": "状态",
    "汇报类型":"汇报类型",
    "批量删除":"批量删除"
    }
    }
</i18n>
<template>
  <div style="padding: 20px">
    <Row>
      <Col span="6">&nbsp;</Col>
      <Col span="12">
        <div class="table-info">
          <IconButton
            :size="15"
            :disabled="formItem.pageIndex == 1"
            @click="changePage(-1)"
            icon="md-arrow-round-back"
          ></IconButton>
          <span class="table-count"
            >{{ $t("个汇报", [totalCount]) }}，{{ formItem.pageIndex }}/{{
              totalPage
            }}</span
          >
          <IconButton
            :size="15"
            :disabled="formItem.pageIndex == totalPage"
            @click="changePage(1)"
            icon="md-arrow-round-forward"
          ></IconButton>
        </div>
      </Col>
      <Col span="6" style="text-align: right">
        <IconButton
          v-if="perm('report_template_admin')"
          @click="showTemplate"
          :title="$t('汇报模板')"
        ></IconButton>
        <IconButton
          v-if="perm('report_delete')"
          @click="batchDelete"
          :title="$t('批量删除')"
        ></IconButton>
      </Col>
    </Row>
    <div style="background-color: #fff; padding: 0 5px">
      <div class="chart-opt-bar" :class="{ shown: showTopOptBar }">
        <Select
          v-model="formItem.projectIdInList"
          :placeholder="$t('项目')"
          transfer
          filterable
          multiple
          clearable
          style="width: 150px"
          @on-change="loadData"
          @on-clear="loadData"
        >
          <Option
            v-for="item in projectList"
            :value="item.id"
            :key="'prj' + item.id"
            >{{ item.name }}
          </Option>
        </Select>
        <CompanyUserSelect
          v-model="formItem.submitterIdInList"
          clearable
          multiple
          placeholder="汇报人"
          style="width: 150px; margin-left: 10px"
          @on-change="loadData(true)"
        />
        <CompanyUserSelect
          v-model="formItem.auditorIdInList"
          clearable
          multiple
          placeholder="审核人"
          style="width: 150px; margin: 0 10px"
          @on-change="loadData(true)"
        />
        <!--                <Input class="search-input" :placeholder="$t('审核人')" type="text" @change="loadData(true)"-->
        <!--                       v-model.trim="formItem.name"/>-->
        <DataDictSelect
          style="width: 150px"
          @change="loadData(true)"
          clearable
          multiple
          :placeholder="$t('汇报类型')"
          type="ReportTemplate.period"
          v-model="formItem.periodInList"
        ></DataDictSelect>

        <DataDictSelect
          style="width: 150px; margin-left: 10px"
          @change="loadData(true)"
          clearable
          multiple
          :placeholder="$t('状态')"
          type="Report.status"
          v-model="formItem.statusInList"
        ></DataDictSelect>

        <Input
          class="search-input"
          :placeholder="$t('汇报标题')"
          type="text"
          @on-change="loadData(true)"
          v-model.trim="formItem.name"
        />

        <div class="chart-opt-bar-date">
          <div class="btn-date-group">
            <RadioGroup v-model="datemode" type="button" size="small">
              <Radio label="week">{{ $t("周") }}</Radio>
              <Radio label="month">{{ $t("月") }}</Radio>
              <Radio label="custom">{{ $t("自定义") }}</Radio>
            </RadioGroup>
          </div>
          <div
            v-if="datemode != 'custom'"
            @click="onClickPreWeek"
            class="btn-prev-week"
          >
            <Icon type="ios-arrow-dropleft-circle" size="18" />
            {{ datemode == "week" ? $t("上一周") : $t("上个月") }}
          </div>
          <div class="current-week" v-if="datemode != 'custom'">
            <span
              >{{ formItem.createTimeStart | fmtDate }} -
              {{ formItem.createTimeEnd | fmtDate }}</span
            >
            <span v-if="isCurrentWeek">
              {{ datemode == "week" ? $t("本周") : $t("本月") }}</span
            >
          </div>
          <div class="current-week" v-else>
            <ExDatePicker
              style="width: 100px"
              type="date"
              clearable
              v-model="formItem.createTimeStart"
              :placeholder="$t('自定义')"
              @on-change="loadData(true)"
            />
            <ExDatePicker
              style="width: 100px"
              clearable
              type="date"
              :day-end="true"
              v-model="formItem.createTimeEnd"
              :placeholder="$t('自定义')"
              @on-change="loadData(true)"
            />
          </div>
          <div
            v-if="datemode != 'custom'"
            @click="onClickNextWeek"
            :class="{ disabled: isCurrentWeek }"
            class="btn-next-week"
          >
            {{ datemode == "week" ? $t("下一周") : $t("下个月") }}
            <Icon type="ios-arrow-dropright-circle" size="18" />
          </div>
        </div>
      </div>
    </div>

    <div class="table-box">
      <table class="table-content">
        <thead>
          <tr>
            <th style="width: 50px">
              <Checkbox @on-change="checkAll" v-model="allCheck"></Checkbox>
            </th>
            <th>{{ $t("汇报标题") }}</th>
            <th style="width: 180px">
              {{ $t("汇报周期") }}
              <Icon
                class="group-icon"
                :class="{ 'group-icon-selected': groupKey == 'reportTime' }"
                @click.native="groupBy('reportTime')"
                type="ios-browsers"
              />
            </th>
            <th style="width: 150px">
              {{ $t("汇报人") }}
              <Icon
                class="group-icon"
                :class="{ 'group-icon-selected': groupKey == 'submitterName' }"
                @click.native="groupBy('submitterName')"
                type="ios-browsers"
              />
            </th>
            <th style="width: 150px">{{ $t("审核人") }}</th>
            <th style="width: 100px">{{ $t("状态") }}</th>
          </tr>
        </thead>

        <tbody>
          <template v-for="(group, gidx) in dataList">
            <tr class="group-tr">
              <td colspan="999">
                <span class="group-name">{{ group.name }} </span>
                <span style="color: #999; font-size: 13px; margin-left: 8px">{{
                  group.list.length
                }}</span>
              </td>
            </tr>
            <tr
              v-for="(item, idx) in group.list"
              :key="'prj_' + item.id"
              class="table-row"
            >
              <td style="width: 50px">
                <Checkbox
                  @on-change="checkOne($event, gidx, idx)"
                  v-model="item.checked"
                ></Checkbox>
              </td>
              <td @click="showReportInfoDialog(item)">
                <div class="report-name-row">
                  <DataDictLabel
                    :category="true"
                    type="ReportTemplate.period"
                    :value="item.period"
                  />
                  <div v-if="item.projectName" class="report-project">
                    {{ item.projectName }}
                  </div>
                  <span style="margin-left: 5px">{{ item.name }}</span>
                </div>
              </td>
              <td>{{ item.reportTime }}</td>
              <td>
                <AvatarImage
                  size="small"
                  :name="item.submitterName"
                  :value="item.submitterImageId"
                  type="label"
                ></AvatarImage>
              </td>
              <td>
                <template v-if="item.auditorList.length == 1">
                  <AvatarImage
                    size="small"
                    :name="item.auditorList[0].name"
                    :value="item.auditorList[0].imageId"
                    type="label"
                  ></AvatarImage>
                </template>
                <template v-if="item.auditorList.length > 1">
                  <AvatarImage
                    v-for="acc in item.auditorList"
                    :key="item.id + '_acc' + acc.id"
                    size="small"
                    :name="acc.name"
                    :value="acc.imageId"
                    type="none"
                  ></AvatarImage>
                </template>
              </td>
              <td>
                <DataDictLabel type="Report.status" :value="item.status" />
              </td>
            </tr>
          </template>
        </tbody>
      </table>

      <div class="nodata" v-if="info.length == 0">
        {{ $t("暂无数据") }}
      </div>
    </div>
  </div>
</template>

<script>
import ExDatePicker from "../../../../components/ui/ExDatePicker";

export default {
  components: { ExDatePicker },
  mixins: [componentMixin],
  data() {
    return {
      groupKey: "reportTime",
      formItem: {
        createTimeStart: null,
        createTimeEnd: null,
        pageIndex: 1,
        pageSize: 50,
        status: null,
        period: null,
        name: null,
        submitterName: null,
        projectName: null,
        projectId: null,
        submitterIdInList: [],
        auditorId: null,
      },
      datemode: "week",
      showTopOptBar: true,
      projectList: [],
      info: [],
      totalPage: 0,
      totalCount: 0,
      initAccount: [],
      allCheck: false,
    };
  },
  created() {
    const now = new Date();
    const startDate = new Date(
      now.getFullYear(),
      now.getMonth(),
      now.getDate() - now.getDay() + 1
    );
    const endDate = new Date(
      startDate.getFullYear(),
      startDate.getMonth(),
      startDate.getDate() + 6,
      23,
      59,
      59,
      999
    );
    this.formItem.createTimeStart = startDate;
    this.formItem.createTimeEnd = endDate;
    /*     this.initAccount = [{
                id: app.account.id,
                name: app.account.name,
                imageId: app.account.imageId
            }]*/
  },
  watch: {
    datemode(val, oldVal) {
      let now = new Date();
      let year = now.getFullYear();
      let month = now.getMonth();
      let day = now.getDate();
      let dayOfWeek = now.getDay();
      if (val == "week") {
        let startDate = new Date(year, month, day);
        startDate = new Date(
          startDate.getTime() - (dayOfWeek - 1) * 24 * 3600 * 1000
        );
        let endDate = new Date(startDate.getTime() + 6 * 24 * 3600 * 1000);
        this.formItem.createTimeStart = startDate;
        this.formItem.createTimeEnd = endDate;
      } else if (val == "month") {
        let startDate = new Date(year, month, 1);
        month += 1;
        if (month >= 12) {
          month = month - 12;
          year = year + 1;
        }
        let endDate = new Date(year, month, 1);
        endDate = new Date(endDate.getTime() - 1);
        this.formItem.createTimeStart = startDate;
        this.formItem.createTimeEnd = endDate;
      }
      this.loadData(true);
    },
  },
  computed: {
    dataList() {
      var list = [];
      var groupList = {};
      var allList = [];
      var groupKey = this.groupKey;
      for (var i = 0; i < this.info.length; i++) {
        var t = this.info[i];
        t["checked"] = false;
        if (groupList[t[groupKey]] == null) {
          groupList[t[groupKey]] = {
            name: t[groupKey],
            list: [],
          };
          allList.push(groupList[t[groupKey]]);
        }
        groupList[t[groupKey]].list.push(t);
      }
      return allList;
    },
    isCurrentWeek() {
      if (
        this.formItem.createTimeStart === null ||
        this.formItem.createTimeEnd === null
      ) {
        return false;
      }
      const now = new Date();
      return (
        this.formItem.createTimeStart.getTime() <= now.getTime() &&
        now.getTime() <= this.formItem.createTimeEnd.getTime()
      );
    },
  },
  mounted() {
    // this.formItem.auditorId = app.account.id;
    this.loadData();
    this.loadProjectList();
  },
  methods: {
    loadProjectList() {
      app.invoke("BizAction.getMyProjectList", [app.token], (list) => {
        this.projectList = list;
      });
    },
    perm(id) {
      return app.perm(id);
    },
    groupBy(id) {
      this.groupKey = id;
    },
    pageMessage(type, value) {
      if (type == "report.edit") {
        this.loadData(true);
      }
    },
    changePage(delta) {
      var t = this.formItem.pageIndex + delta;
      if (t <= 0 || t > this.totalPage) {
        return;
      }else{
          this.formItem.pageIndex = t;
      }
      this.loadData();
    },
    showReportInfoDialog(item) {
      app.showDialog(ReportInfoDialog, {
        id: item.id,
      });
    },
    showTemplate() {
      app.loadPage("dashboard?view=report_template");
    },
    loadData(reload) {
      if (reload) {
        this.formItem.pageIndex = 1;
      }
      app.invoke(
        "BizAction.getReportList",
        [app.token, this.formItem],
        (res) => {
          if (res.count == 0) {
            this.totalPage = 1;
          } else {
            var t = Math.ceil(res.count / this.formItem.pageSize);
            this.totalPage = t;
          }
          this.totalCount = res.count;
          this.info = res.list;
        }
      );
    },
    startDateChange(e) {
      this.formItem.startDate = e;
    },
    endDateChange(e) {
      this.formItem.endDate = e;
    },
    onClickNextWeek() {
      if (this.isCurrentWeek) {
        return;
      }
      if (this.datemode == "week") {
        this.formItem.createTimeStart = new Date(
          this.formItem.createTimeStart.getFullYear(),
          this.formItem.createTimeStart.getMonth(),
          this.formItem.createTimeStart.getDate() + 7
        );
        this.formItem.createTimeEnd = new Date(
          this.formItem.createTimeEnd.getFullYear(),
          this.formItem.createTimeEnd.getMonth(),
          this.formItem.createTimeEnd.getDate() + 7
        );
      } else if (this.datemode == "month") {
        let year = this.formItem.createTimeStart.getFullYear();
        let month = this.formItem.createTimeStart.getMonth();
        month += 1;
        if (month >= 12) {
          month = month - 12;
          year = year + 1;
        }
        let startDate = new Date(year, month, 1);
        month += 1;
        if (month >= 12) {
          month = month - 12;
          year = year + 1;
        }
        let endDate = new Date(year, month, 1);
        endDate = new Date(endDate.getTime() - 1);
        this.formItem.createTimeStart = startDate;
        this.formItem.createTimeEnd = endDate;
      }
      this.loadData(true);
    },
    onClickPreWeek() {
      if (this.datemode == "week") {
        this.formItem.createTimeStart = new Date(
          this.formItem.createTimeStart.getFullYear(),
          this.formItem.createTimeStart.getMonth(),
          this.formItem.createTimeStart.getDate() - 7
        );
        this.formItem.createTimeEnd = new Date(
          this.formItem.createTimeEnd.getFullYear(),
          this.formItem.createTimeEnd.getMonth(),
          this.formItem.createTimeEnd.getDate() - 7
        );
      } else if (this.datemode == "month") {
        let year = this.formItem.createTimeStart.getFullYear();
        let month = this.formItem.createTimeStart.getMonth();
        month -= 1;
        if (month < 0) {
          month = month + 12;
          year = year - 1;
        }
        let startDate = new Date(year, month, 1);
        month += 1;
        if (month >= 12) {
          month = month - 12;
          year = year + 1;
        }
        let endDate = new Date(year, month, 1);
        endDate = new Date(endDate.getTime() - 1);
        this.formItem.createTimeStart = startDate;
        this.formItem.createTimeEnd = endDate;
      }
      this.loadData(true);
    },
    batchDelete() {
      var reportIds = [];
      for (let i = 0; i < this.dataList.length; i++) {
        var list = this.dataList[i];
        if (!Array.isEmpty(list.list)) {
          for (let j = 0; j < list.list.length; j++) {
            if (list.list[j].checked) {
              reportIds.push(list.list[j].id);
            }
          }
        }
      }
      if (Array.isEmpty(reportIds)) {
        app.toast("请先选择要操作的汇报");
        return;
      }
      app.confirm("确定要删除这些汇报吗？删除后可在回收站恢复.", () => {
        this.loading = true;
        app.invoke(
          "BizAction.batchDeleteReport",
          [app.token, reportIds],
          (res) => {
            app.toast(this.$t("操作成功"));
            this.loadData(true);
          },
          (err) => {
            app.toast(this.$t("操作失败"));
          }
        );
      });
    },
    checkOne(val, gidx, idx) {
      this.dataList[gidx].list[idx]["checked"] = val;
      if (!val) {
        this.allCheck = false;
      }
    },
    checkAll(val) {
      for (let i = 0; i < this.dataList.length; i++) {
        var list = this.dataList[i];
        for (let j = 0; j < list.list.length; j++) {
          this.$set(list.list[j], "checked", val);
        }
      }
    },
  },
};
</script>
