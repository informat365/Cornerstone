package cornerstone.biz.action;

import com.alibaba.fastjson.JSONObject;
import cornerstone.biz.ConstDefine;
import cornerstone.biz.annotations.ApiDefine;
import cornerstone.biz.dao.BizDAO;
import cornerstone.biz.dao.StatDAO;
import cornerstone.biz.domain.*;
import cornerstone.biz.domain.AccountNotification.AccountNotificationInfo;
import cornerstone.biz.domain.AccountNotification.AccountNotificationQuery;
import cornerstone.biz.domain.AccountOverDueTask.AccountProjectOverDueTask;
import cornerstone.biz.domain.Company.CompanyInfo;
import cornerstone.biz.domain.Company.CompanyQuery;
import cornerstone.biz.domain.File.FileInfo;
import cornerstone.biz.domain.File.FileQuery;
import cornerstone.biz.domain.Note.NoteInfo;
import cornerstone.biz.domain.Note.NoteQuery;
import cornerstone.biz.domain.ProjectPipeline.ProjectPipelineInfo;
import cornerstone.biz.domain.Remind.RemindInfo;
import cornerstone.biz.domain.Remind.RemindQuery;
import cornerstone.biz.domain.SystemNotification.SystemNotificationInfo;
import cornerstone.biz.domain.Task.TaskInfo;
import cornerstone.biz.domain.Task.TaskQuery;
import cornerstone.biz.domain.WikiPage.WikiPageDetailInfo;
import cornerstone.biz.domain.WikiPage.WikiPageDetailInfoQuery;
import cornerstone.biz.domain.WorkflowInstance.WorkflowInstanceInfo;
import cornerstone.biz.domain.WorkflowInstance.WorkflowInstanceQuery;
import cornerstone.biz.domain.query.DesignerDatabaseInfoQuery;
import cornerstone.biz.lucene.DocumentData;
import cornerstone.biz.lucene.LuceneService;
import cornerstone.biz.srv.*;
import cornerstone.biz.util.*;
import cornerstone.biz.util.MySQLDatabaseSchema.DatabaseInfo;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;
import jazmin.driver.jdbc.Transaction;
import jazmin.driver.jdbc.smartjdbc.Query;
import jazmin.driver.jdbc.smartjdbc.QueryWhere;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.rpc.RpcService;
import jazmin.util.DumpUtil;
import jazmin.util.JSONUtil;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.wltea.analyzer.dic.Dictionary;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 定时任务Action
 */

@ApiDefine(value = "定时任务接口")
public interface TaskJobAction {

    @ApiDefine(value = "初始化ldap")
    void initLdapService();

    @ApiDefine(value = "初始化MySQL")
    void initMysqlProxy();

    @ApiDefine(value = "获取配置集合", resp = "配置集合")
    List<Config> getConfigs();

    @ApiDefine(value = "发送过期提醒")
    void sendExpiredRemindList();

    @ApiDefine(value = "跑除笔记外的全文检索")
    void initDocumentIndex();

    @ApiDefine(value = "跑笔记全文检索")
    void initNoteIndex();

    @ApiDefine(value = "计算Task的已用天数和剩余天数")
    void updateTaskStartEndDays();

    @ApiDefine(value = "生成每日统计")
    void doCreateTaskDayData();

    @ApiDefine(value = "计算所有objectType=0的统计")
    void calcNoObjectTypeTaskDayData();

    @ApiDefine(value = "异步发送提醒", params = {"用户通知信息"})
    void asyncSendNotification(AccountNotificationInfo e);

    @ApiDefine(value = "查询需要跑的pipeline", resp = "Pipeline集合")
    List<ProjectPipeline> getNeedRunProjectPipelineInfos();

    @ApiDefine(value = "执行Pipeline", params = {"Pipeline"})
    void runProjectPipeline(ProjectPipeline bean);

    @ApiDefine(value = "查询需要跑的CmdbRobot集合")
    List<CmdbRobot> getNeedRunCmdbRobots();

    @ApiDefine(value = "执行CmdbRobot")
    void runCmdbRobot(CmdbRobot bean);

    @ApiDefine(value = "获取用户通知信息集合", resp = "用户通知集合信息", params = "用户通知查询类")
    List<AccountNotificationInfo> getAccountNotificationInfoList(AccountNotificationQuery query);

    @ApiDefine(value = "通过用户名称获取用户信息", resp = "用户信息", params = "用户名称")
    Account getAccountByUserName(String userName);

    @ApiDefine(value = "判断用户权限", params = {"用户ID", "MYSQL代理"})
    void checkAccountPermission(int accountId, DesignerMysqlProxyInstance instance);

    @ApiDefine(value = "查询所有数据库", resp = "数据库集合")
    List<DesignerDatabase> getAllDesignerDatabases();

    @ApiDefine(value = "判断数据库是否有变更", params = "数据库")
    void doJudgeDatabaseChangeLog(DesignerDatabase e);

    @ApiDefine(value = "获取任务提醒时间集合", resp = "任务提醒时间集合")
    List<TaskRemindTime> getNeedRunTaskRemindTimes();

    @ApiDefine(value = "发送任务提醒", params = "任务提醒时间")
    void doSendTaskRemind(TaskRemindTime bean);

    @ApiDefine(value = "获取需要执行的报表模板", resp = "报表模板集合")
    List<ReportTemplate> getNeedRunReportTemplates();

    @ApiDefine(value = "--", params = "报表模板")
    void doGenerateReport(ReportTemplate bean);

    @ApiDefine(value = "初始化分词器信息")
    void initIkAnalyzerDicts();

    @ApiDefine(value = "计算文件path")
    void doCalcFilePath();

    @ApiDefine(value = "计算项目Activity")
    void doCalcProjectActivity();

    @ApiDefine(value = "重置每日登陆次数,手机验证码错误次数")
    void doResetDailyLoginFailCountKaptchaErrorCount();

    @ApiDefine(value = "更新所有项目任务每日数据(不分迭代)")
    void updateProjectTaskDayData();

    @ApiDefine(value = "更新所有迭代任务每日数据(按迭代)")
    void updateIterationTaskDayData();

    @ApiDefine(value = "发送个人超期任务")
    void doSendTaskOverDueNotification();

    @ApiDefine(value = "修复数据")
    void doFixTaskOwnerDatas();


    @ApiDefine(value = "修复数据V60")
    void doFixDataForV60();


    @ApiDefine(value = "修复数据V60")
    void doFixDataForV65();

    @ApiDefine(value = "查询需要跑的SystemHook", resp = "SystemHook集合")
    List<SystemHook> getNeedRunSystemHooks();

    @ApiDefine(value = "跑SystemHook", params = "SystemHook")
    void doRunSystemHook(SystemHook bean);

    @ApiDefine(value = "查询需要跑的公司", resp = "公司集合")
    List<CompanyInfo> getNeedRunCompanies();

    @ApiDefine(value = "更新公司许可证信息", params = "公司信息")
    void doUpdateCompanyLiceneInfo(CompanyInfo bean);

    @ApiDefine("检查数据字典数据对不对")
    void doCheckDataDictData();

    @ApiDefine(value = "查询需要删除的草稿流程实例", resp = "流程实例集合")
    List<WorkflowInstanceInfo> getNeedDeleteDraftWorkflowInstances();

    @ApiDefine(value = "删除草稿流程实例", params = "流程ID")
    void deleteWorkflowInstance(int id);

    @ApiDefine("调用存储过程检查数据正确性")
    void doCallProcCheckData();

    @ApiDefine(value = "查询需要跑的TaskActionJob", resp = "TaskActionJob集合")
    List<TaskActionJob> getNeedRunTaskActionJobs();

    @ApiDefine(value = "更新TaskActionJob", params = "TaskActionJob")
    void updateTaskActionJob(TaskActionJob bean);

    @ApiDefine(value = "更新项目拼音名称")
    void updateProjectPinyinShortName();

    void doResetAccountInviteMemberDailySendMailNum();

    /**
     * 删除过期的验证码
     */
    void doDeleteInvalidKaptchas();

    /**
     * 发送系统通知
     */
    void doSendSystemNotifaction(int id);

    /**
     * 获取需要发送的系统通知列表
     */
    List<Integer> getNeedSendSystemNotifactionList();

    @ApiDefine(value = "代码数量统计重试")
    void retryScmCommit();

    @ApiDefine(value = "异步发送邮件")
    void asyncSendEmail();

    /**
     * 同步项目集
     */
    void syncProjectSet();

    /**
     * 同步钉钉通讯录用户
     */
    void syncDingtalkAccount();

    /**
     * 每月8号2点同步上个月的钉钉用户考勤信息
     */
    void syncDingtalkAttendance();

    /**
     * 同步AD域账号状态
     */
    void syncAdAccount();

    /**
     * 同步项目基于spi的进度
     */
    void syncProjectSpiProcess();

    //
    @RpcService
    class TaskJobActionImpl implements TaskJobAction {
        //
        private static Logger logger = LoggerFactory.get(TaskJobActionImpl.class);
        //
        @AutoWired
        BizDAO dao;
        @AutoWired
        StatDAO statDao;
        @AutoWired
        BizService bizService;
        @AutoWired
        LuceneService luceneService;
        @AutoWired
        LdapService ldapService;
        @AutoWired
        WeixinService weixinService;
        @AutoWired
        MysqlProxyService mysqlProxyService;
        @AutoWired
        WorkflowService workflowService;
        @AutoWired
        LarkService larkService;
        @AutoWired
        WebApiService webApiService;
        @AutoWired
        DingtalkService dingtalkService;

        @Override
        public List<Config> getConfigs() {
            return dao.getAll(Config.class);
        }

        @Transaction
        @Override
        public void sendExpiredRemindList() {
            List<Integer> ids = dao.getNeedPushRemindInfo();
            if (ids.isEmpty()) {
                return;
            }
            RemindQuery remindQuery = new RemindQuery();
            remindQuery.pageSize = Integer.MAX_VALUE;
            remindQuery.idInList = BizUtil.convertList(ids);
            List<RemindInfo> remindInfos = dao.getList(remindQuery);
            for (RemindInfo e : remindInfos) {
                AccountNotification bean = dao.getAccountNotification(AccountNotificationSetting.TYPE_日历提醒, e.id);
                if (bean != null) {
                    continue;
                }
                bizService.addAccountNotification(e.createAccountId,
                        AccountNotificationSetting.TYPE_日历提醒,
                        e.companyId, 0,
                        e.id, e.name, JSONUtil.toJson(e),
                        new Date(), 0, "", "");
            }
        }

        //
        @Override
        public void initDocumentIndex() {
            initDocumentIndex0();
        }

        private synchronized void initDocumentIndex0() {
            try {
                initTaskIndex();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
//			try {
//				initWikiIndex();
//			} catch (Exception e) {
//				logger.error(e.getMessage(),e);
//			}
//			try {
//				initFileIndex();
//			} catch (Exception e) {
//				logger.error(e.getMessage(),e);
//			}
        }

        private void initTaskIndex() {
            long startTime = System.currentTimeMillis();
            TaskQuery query = new TaskQuery();
            query.isCreateIndex = false;
            query.pageSize = 10000;
            List<TaskInfo> list = dao.getList(query);
            logger.info("initTaskIndex size:{}", list.size());
            Map<Integer, List<TaskInfo>> taskMap = new HashMap<>();
            for (TaskInfo e : list) {
                List<TaskInfo> value = taskMap.get(e.companyId);
                if (value == null) {
                    value = new ArrayList<>();
                    taskMap.put(e.companyId, value);
                }
                value.add(e);
            }
            logger.info("initTaskIndex taskMap size:{}", taskMap.size());
            int index = 0;
            for (Map.Entry<Integer, List<TaskInfo>> entry : taskMap.entrySet()) {
                int companyId = entry.getKey();
                List<TaskInfo> taskInfos = entry.getValue();
                List<DocumentData> updateDocs = new ArrayList<>();
                List<TaskInfo> updateTaskInfos = new ArrayList<>();
                List<Term> deleteTerms = new ArrayList<>();
                List<TaskInfo> deleteTaskInfos = new ArrayList<>();
                //logger.info("processing {}/{}  taskInfos:{}",index,taskMap.size(),taskInfos.size());
                StringBuilder deleteTermUuids = new StringBuilder();
                for (TaskInfo e : taskInfos) {
                    //logger.info("TaskInfo id:{}",e.id);
                    DocumentData data = new DocumentData();
                    org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
                    data.term = new Term("uuid", e.uuid);
                    doc.add(new StoredField("id", e.id));
                    doc.add(new StringField("uuid", e.uuid, Store.YES));//一定要这么写
                    addTextField(doc, "serialNo", "#" + e.serialNo);
                    doc.add(new IntPoint("type", Document.TYPE_TASK));
                    doc.add(new StoredField("type", Document.TYPE_TASK));
                    doc.add(new IntPoint("projectId", e.projectId));
                    doc.add(new StoredField("projectId", e.projectId));
                    doc.add(new StoredField("projectUuid", e.projectUuid));
                    addTextField(doc, "projectName", e.projectName);
                    doc.add(new StoredField("objectType", e.objectType));
                    addTextField(doc, "name", e.name);
                    addTextField(doc, "createAccountName", e.createAccountName);
                    addTextField(doc, "statusName", e.statusName);
                    addTextField(doc, "priorityName", e.priorityName);
                    addTextField(doc, "updateTime", DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    if (e.isDelete) {
                        deleteTerms.add(new Term("uuid", e.uuid));
                        deleteTaskInfos.add(e);
                        deleteTermUuids.append(e.uuid).append(",");
                    } else {
                        data.document = doc;
                        updateTaskInfos.add(e);
                        updateDocs.add(data);
                    }
                }
                //logger.info("after for updateDocs:{} deleteTerms:{}",updateDocs.size(),deleteTerms.size());
                if (!updateDocs.isEmpty()) {
                    try {
                        updateDocuments(companyId, updateDocs);
                        logger.info("updateIndexs companyId:{} docNum:{}", companyId, updateDocs.size());
                        for (TaskInfo e : updateTaskInfos) {
                            dao.updateTaskIsCreateIndex(e.id, true);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                if (!deleteTerms.isEmpty()) {
                    try {
                        deleteIndexs(companyId, deleteTerms);
                        logger.info("deleteDocuments companyId:{} docNum:{} deleteTermUuids:{}",
                                companyId, deleteTerms.size(), deleteTermUuids.toString());
                        for (TaskInfo e : deleteTaskInfos) {
                            dao.updateTaskIsCreateIndex(e.id, true);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                logger.info("processed {}/{}", index++, taskMap.size());
            }//for
            long useTime = System.currentTimeMillis() - startTime;
            logger.info("initTaskIndex docNum:{} using:{}ms", list.size(), useTime);
        }

        @Override
        public void initNoteIndex() {
            initNoteIndex0();
        }

        private synchronized void initNoteIndex0() {
            try {
                long startTime = System.currentTimeMillis();
                NoteQuery query = new NoteQuery();
                query.isCreateIndex = false;
                query.pageSize = 10000;
                List<NoteInfo> list = dao.getList(query);
                logger.info("initNoteIndex size:{}", list.size());
                Map<String, List<NoteInfo>> map = new HashMap<>();
                for (NoteInfo e : list) {
                    e.content = dao.getNoteContentByNoteId(e.id);
                    String key = e.companyId + "-" + e.createAccountId;
                    List<NoteInfo> value = map.get(key);
                    if (value == null) {
                        value = new ArrayList<>();
                        map.put(key, value);
                    }
                    value.add(e);
                }
                for (Map.Entry<String, List<NoteInfo>> entry : map.entrySet()) {
                    String key = entry.getKey();
                    List<NoteInfo> infos = entry.getValue();
                    List<DocumentData> updateDocs = new ArrayList<>();
                    List<NoteInfo> updateTaskInfos = new ArrayList<>();
                    List<Term> deleteTerms = new ArrayList<>();
                    List<NoteInfo> deleteTaskInfos = new ArrayList<>();
                    StringBuilder deleteTermUuids = new StringBuilder();
                    for (NoteInfo e : infos) {
                        DocumentData data = new DocumentData();
                        org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
                        data.term = new Term("uuid", e.uuid);
                        doc.add(new StoredField("id", e.id));
                        doc.add(new StringField("uuid", e.uuid, Store.YES));//一定要这么写
                        addTextField(doc, "title", e.title);
                        addTextField(doc, "updateTime", DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
                        String content = BizUtil.cleanHtml(e.content);
                        addTextField(doc, "content", content, Store.NO);
                        if (e.isDelete) {
                            deleteTerms.add(new Term("uuid", e.uuid));
                            deleteTaskInfos.add(e);
                            deleteTermUuids.append(e.uuid).append(",");
                        } else {
                            data.document = doc;
                            updateTaskInfos.add(e);
                            updateDocs.add(data);
                        }
                    }
                    if (!updateDocs.isEmpty()) {
                        try {
                            updateDocuments(key, updateDocs);
                            logger.info("updateIndexs key:{} docNum:{}", key, updateDocs.size());
                            for (NoteInfo e : updateTaskInfos) {
                                dao.updateNoteIsCreateIndex(e.id, true);
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                    if (!deleteTerms.isEmpty()) {
                        try {
                            deleteIndexs(key, deleteTerms);
                            logger.info("deleteDocuments key:{} docNum:{} deleteTermUuids:{}",
                                    key, deleteTerms.size(), deleteTermUuids.toString());
                            for (NoteInfo e : deleteTaskInfos) {
                                dao.updateNoteIsCreateIndex(e.id, true);
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }//for
                long useTime = System.currentTimeMillis() - startTime;
                logger.info("initNoteIndex docNum:{} using:{}ms", list.size(), useTime);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        /**
         * TextField会建立索引的
         *
         * @param doc
         * @param fieldName
         * @param fieldValue
         * @return
         */
        private String addTextField(org.apache.lucene.document.Document doc, String fieldName, String fieldValue) {
            return addTextField(doc, fieldName, fieldValue, Store.YES);
        }

        private String addTextField(org.apache.lucene.document.Document doc, String fieldName, String fieldValue, Store store) {
            if (fieldValue != null) {
                fieldValue = fieldValue.trim();
            }
            if (!StringUtil.isEmpty(fieldValue)) {
                doc.add(new TextField(fieldName, fieldValue, store));
            }
            return fieldValue;
        }

        private void initWikiIndex() {
            long startTime = System.currentTimeMillis();
            WikiPageDetailInfoQuery query = new WikiPageDetailInfoQuery();
            query.isCreateIndex = false;
            query.pageSize = 10000;
            List<WikiPageDetailInfo> list = dao.getList(query);
            logger.info("initWikiIndex size:{}", list.size());
            Map<Integer, List<WikiPageDetailInfo>> taskMap = new HashMap<>();
            for (WikiPageDetailInfo e : list) {
                List<WikiPageDetailInfo> value = taskMap.get(e.companyId);
                if (value == null) {
                    value = new ArrayList<>();
                    taskMap.put(e.companyId, value);
                }
                value.add(e);
            }
            for (Map.Entry<Integer, List<WikiPageDetailInfo>> entry : taskMap.entrySet()) {
                int companyId = entry.getKey();
                List<WikiPageDetailInfo> detailInfos = entry.getValue();
                List<DocumentData> updateDocs = new ArrayList<>();
                List<Term> deleteTerms = new ArrayList<>();
                List<WikiPageDetailInfo> updateInfos = new ArrayList<>();
                List<WikiPageDetailInfo> deleteInfos = new ArrayList<>();
                StringBuilder deleteTermUuids = new StringBuilder();
                for (WikiPageDetailInfo e : detailInfos) {
                    DocumentData data = new DocumentData();
                    org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
                    data.term = new Term("uuid", e.uuid);
                    doc.add(new StoredField("id", e.id));
                    doc.add(new StringField("uuid", e.uuid, Store.YES));
                    doc.add(new StoredField("type", Document.TYPE_WIKI));
                    doc.add(new IntPoint("projectId", e.projectId));
                    doc.add(new StoredField("projectId", e.projectId));
                    doc.add(new StoredField("projectUuid", e.projectUuid));
                    addTextField(doc, "projectName", e.projectName);
                    addTextField(doc, "name", e.name);
                    addTextField(doc, "createAccountName", e.createAccountName);
                    addTextField(doc, "updateTime", DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    String content = BizUtil.cleanHtml(e.content);
                    addTextField(doc, "content", content, Store.NO);
                    if (e.isDelete || e.status != WikiPageDetailInfo.STATUS_已发布) {
                        deleteTerms.add(new Term("uuid", e.uuid));
                        deleteInfos.add(e);
                        deleteTermUuids.append(e.uuid).append(",");
                    } else {
                        data.document = doc;
                        updateDocs.add(data);
                        updateInfos.add(e);
                    }
                }
                if (!updateDocs.isEmpty()) {
                    try {
                        updateDocuments(companyId, updateDocs);
                        logger.info("initWikiIndex updateIndexs companyId:{} docNum:{}", companyId, updateDocs.size());
                        for (WikiPageDetailInfo e : updateInfos) {
                            dao.updateWikiPageIsCreateIndex(e.id, true);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                if (!deleteTerms.isEmpty()) {
                    try {
                        deleteIndexs(companyId, deleteTerms);
                        logger.info("initWikiIndex deleteIndexs companyId:{} docNum:{} deleteTermUuids:{}",
                                companyId, deleteTerms.size(), deleteTermUuids.toString());
                        for (WikiPageDetailInfo e : deleteInfos) {
                            dao.updateWikiPageIsCreateIndex(e.id, true);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }//for
            long useTime = System.currentTimeMillis() - startTime;
            logger.info("initWikiIndex docNum:{} using:{}ms", list.size(), useTime);
        }

        private void initFileIndex() {
            long startTime = System.currentTimeMillis();
            FileQuery query = new FileQuery();
            query.isCreateIndex = false;
            query.pageSize = 1000;
            List<FileInfo> list = dao.getList(query);
            logger.info("initFileIndex size:{}", list.size());
            Map<Integer, List<FileInfo>> taskMap = new HashMap<>();
            for (FileInfo e : list) {
                List<FileInfo> value = taskMap.get(e.companyId);
                if (value == null) {
                    value = new ArrayList<>();
                    taskMap.put(e.companyId, value);
                }
                value.add(e);
            }
            for (Map.Entry<Integer, List<FileInfo>> entry : taskMap.entrySet()) {
                int companyId = entry.getKey();
                List<FileInfo> detailInfos = entry.getValue();
                List<DocumentData> updateDocs = new ArrayList<>();
                List<Term> deleteTerms = new ArrayList<>();
                List<FileInfo> updateInfos = new ArrayList<>();
                List<FileInfo> deleteInfos = new ArrayList<>();
                for (FileInfo e : detailInfos) {
                    DocumentData data = new DocumentData();
                    org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
                    data.term = new Term("uuid", e.uuid);
                    doc.add(new StoredField("id", e.id));
                    doc.add(new StringField("uuid", e.uuid, Store.YES));
                    doc.add(new StoredField("type", Document.TYPE_FILE));
                    doc.add(new IntPoint("projectId", e.projectId));
                    doc.add(new StoredField("projectId", e.projectId));
                    doc.add(new StoredField("projectUuid", e.projectUuid));
                    addTextField(doc, "projectName", e.projectName);
                    addTextField(doc, "name", e.name);
                    addTextField(doc, "createAccountName", e.createAccountName);
                    addTextField(doc, "updateTime", DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    if (e.isDelete) {
                        deleteTerms.add(new Term("uuid", e.uuid));
                        deleteInfos.add(e);
                    } else {
                        data.document = doc;
                        updateDocs.add(data);
                        updateInfos.add(e);
                    }
                }
                if (!updateDocs.isEmpty()) {
                    try {
                        updateDocuments(companyId, updateDocs);
                        for (FileInfo e : updateInfos) {
                            dao.updateFileIsCreateIndex(e.id, true);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                if (!deleteTerms.isEmpty()) {
                    try {
                        deleteIndexs(companyId, deleteTerms);
                        for (FileInfo e : deleteInfos) {
                            dao.updateFileIsCreateIndex(e.id, true);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }//for
            long useTime = System.currentTimeMillis() - startTime;
            logger.info("initWikiIndex docNum:{} using:{}ms", list.size(), useTime);
        }

        private void deleteIndexs(int companyId, List<Term> terms) {
            deleteIndexs(companyId + "", terms);
        }

        private void updateDocuments(int companyId, List<DocumentData> docs) {
            updateDocuments(companyId + "", docs);
        }

        private void deleteIndexs(String key, List<Term> terms) {
            luceneService.deleteDocuments(BizUtil.getIndexPath(key), terms);
        }

        private void updateDocuments(String key, List<DocumentData> docs) {
            luceneService.updateDocuments(BizUtil.getIndexPath(key), docs);
        }

        @Override
        public void updateTaskStartEndDays() {
            dao.updateTaskStartDays();
            dao.updateTaskEndDays();
        }

        private Map<String, TaskDayData> createMap(List<TaskDayData> list) {
            Map<String, TaskDayData> map = new HashMap<>();
            for (TaskDayData e : list) {
                String key = e.projectId + "_" + e.objectType + "_" + e.iterationId;
                map.put(key, e);
            }
            return map;
        }

        private void setTaskDayDataValue(TaskDayData bean, Map<String, TaskDayData> map, String fieldName) {
            String key = bean.projectId + "_" + bean.objectType + "_" + bean.iterationId;
            TaskDayData data = map.get(key);
            if (data != null) {
                BizUtil.setFieldValue(bean, fieldName, BizUtil.getFieldValue(data, fieldName));
            }
        }

        @Override
        @Transaction
        public void doCreateTaskDayData() {
            //计算昨天
            Date statDate = DateUtil.getBeginOfDay(DateUtil.getNextDay(-1));
            //计算迭代每日统计
            createTaskDayDataByIteration0(statDate);
            //计算项目每日统计
            createTaskDayDataByProject0(statDate);
            //
            statDao.calcNoObjectTypeTaskDayData(statDate);
        }

        private void createTaskDayDataByIteration0(Date statDate) {
            long startTime = System.currentTimeMillis();
            //按照objectType分组
            List<TaskDayData> totalList = statDao.getTotalTaskNumGroupByProjectObjectTypeIterationId(statDate);
            List<TaskDayData> totalLoadList = statDao.getTotalTaskLoadGroupByProjectObjectTypeIterationId(statDate);
            List<TaskDayData> totalFinishList = statDao.getTotalFinishTaskNumGroupByProjectObjectTypeIterationId(statDate);
            List<TaskDayData> totalFinishLoadList = statDao.getTotalFinishTaskLoadGroupByProjectObjectTypeIterationId(statDate);
            List<TaskDayData> todayFinishList = statDao.getTodayFinishTaskNumGroupByProjectObjectTypeIterationId(statDate);
            List<TaskDayData> todayFinishLoadList = statDao.getTodayFinishTaskLoadGroupByProjectObjectTypeIterationId(statDate);
            List<TaskDayData> totalCommitList = statDao.getTotalScmCommitByIteration(statDate);
            List<TaskDayData> todayScmCommitList = statDao.getTodayScmCommitByIteration(statDate);
            addOrUpdateTaskDayDatas(statDate, totalList, totalFinishList, todayFinishList, totalCommitList, todayScmCommitList, totalLoadList, totalFinishLoadList, todayFinishLoadList);
            logger.info("createTaskDayDataByIteration0 finish using:{}ms statDate:{}",
                    System.currentTimeMillis() - startTime,
                    cornerstone.biz.util.DumpUtil.dump(statDate));
        }

        //
        private void createTaskDayDataByProject0(Date statDate) {
            long startTime = System.currentTimeMillis();
            List<TaskDayData> totalList = statDao.getTotalTaskNumGroupByProjectObjectType(statDate);
            List<TaskDayData> totalLoadList = statDao.getTotalTaskLoadGroupByProjectObjectType(statDate);
            List<TaskDayData> totalFinishList = statDao.getTotalFinishTaskNumGroupByProjectObjectType(statDate);
            List<TaskDayData> totalFinishLoadList = statDao.getTotalFinishTaskLoadGroupByProjectObjectType(statDate);
            List<TaskDayData> todayFinishList = statDao.getTodayFinishTaskNumGroupByProjectObjectType(statDate);
            List<TaskDayData> todayFinishLoadList = statDao.getTodayFinishTaskLoadGroupByProjectObjectType(statDate);
            List<TaskDayData> totalCommitList = statDao.getTotalScmCommitByProject(statDate);
            List<TaskDayData> todayScmCommitList = statDao.getTodayScmCommitByProject(statDate);
            addOrUpdateTaskDayDatas(statDate, totalList, totalFinishList, todayFinishList, totalCommitList, todayScmCommitList, totalLoadList, totalFinishLoadList, todayFinishLoadList);
            logger.info("createTaskDayDataByProject0 finish using:{}ms statDate:{}",
                    System.currentTimeMillis() - startTime,
                    cornerstone.biz.util.DumpUtil.dump(statDate));
        }


        private void addOrUpdateTaskDayDatas(Date statDate, List<TaskDayData> totalList, List<TaskDayData> totalFinishList,
                                             List<TaskDayData> todayFinishList, List<TaskDayData> totalCommitList, List<TaskDayData> todayScmCommitList,
                                             List<TaskDayData> totalLoadList, List<TaskDayData> totalFinishLoadList, List<TaskDayData> todayFinishLoadList) {
            Map<String, TaskDayData> totalFinishMap = createMap(totalFinishList);
            Map<String, TaskDayData> totalLoadMap = createMap(totalLoadList);
            Map<String, TaskDayData> totalFinishLoadMap = createMap(totalFinishLoadList);
            Map<String, TaskDayData> todayFinishMap = createMap(todayFinishList);
            Map<String, TaskDayData> todayFinishLoadMap = createMap(todayFinishLoadList);
            Map<String, TaskDayData> totalCommitMap = createMap(totalCommitList);
            Map<String, TaskDayData> todayCommitMap = createMap(todayScmCommitList);
            //按照objectType分组
            for (TaskDayData bean : totalList) {
                bean.statDate = statDate;
                setTaskDayDataValue(bean, totalLoadMap, "totalLoad");
                setTaskDayDataValue(bean, totalFinishMap, "totalFinishNum");
                setTaskDayDataValue(bean, totalFinishLoadMap, "totalFinishLoad");
                setTaskDayDataValue(bean, todayFinishMap, "todayFinishNum");
                setTaskDayDataValue(bean, todayFinishLoadMap, "todayFinishLoad");
                setTaskDayDataValue(bean, totalCommitMap, "totalCommitNum");
                setTaskDayDataValue(bean, todayCommitMap, "todayCommitNum");
                TaskDayData old = statDao.getTaskDayData(bean.companyId, bean.projectId, bean.iterationId, bean.objectType, bean.statDate);
                if (old != null) {
                    old.totalNum = bean.totalNum;
                    old.totalLoad = bean.totalLoad;
                    old.totalFinishNum = bean.totalFinishNum;
                    old.totalFinishLoad = bean.totalFinishLoad;
                    old.todayFinishNum = bean.todayFinishNum;
                    old.todayFinishLoad = bean.todayFinishLoad;
                    old.totalCommitNum = bean.totalCommitNum;
                    old.todayCommitNum = bean.todayCommitNum;
                    dao.updateTaskDayData(old);
                } else {
                    dao.addNotWithGenerateKey(bean);
                }
            }
        }


        @Override
        @Transaction
        public void calcNoObjectTypeTaskDayData() {
//			statDao.calcAllNoObjectTypeTaskDayData();
        }

        @Override
        public void initLdapService() {
            ldapService.init();
        }


        /**
         * 异步发送提醒
         */
        @Override
        @Transaction
        public void asyncSendNotification(AccountNotificationInfo e) {
            e.isEmailSend = true;
            e.isWeixinSend = true;
            e.isLarkSend = true;
            e.isDingtalkSend = true;
            dao.updateAccountNotificationSendState(e);
//			dao.update(e);
            //
            Account account = dao.getExistedById(Account.class, e.accountId);
            if (account.status == Account.STATUS_无效) {
                return;
            }
            if (account.lastLoginTime == null || (System.currentTimeMillis() -
                    account.lastLoginTime.getTime() > 30 * 3600 * 1000 * 24L)) {//如果最后登录时间超过30天前的忽略
                logger.info("最后登录时间超过30天前的忽略 account{}.lastLoginTime ignore {}", account.id, account.lastLoginTime);
                return;
            }
            //判断是否是项目成员
            if (!bizService.isCompanyMember(account.id, e.companyId)) {
                return;
            }
            try {
                asyncSendNotification(account, e);
            }catch (Exception e1){
                logger.error("asyncSendNotification catch error,send cancel ,accountId:{},notificationId:{}",e.accountId,e.id);
            }
        }

        //
        private boolean isTaskAccountNotificationType(int type) {
            return AccountNotificationSetting.taskTypes.contains(type);
        }

        private void asyncSendNotification(Account account, AccountNotificationInfo e) {
            if (e.type == AccountNotificationSetting.TYPE_日历提醒 || e.type == AccountNotificationSetting.TYPE_发表讨论) {
                return;
            }
            AccountNotificationSetting setting = dao.getAccountNotificationSetting(account.id, e.type);
            if (isTaskAccountNotificationType(e.type)) {//任务相关通知
                TaskInfo task = new TaskInfo();
                Map<String, Object> map = JSONUtil.fromJson(e.content, Map.class);
                JSONObject jtask = (JSONObject) map.get("task");
                task.id = Integer.parseInt(String.valueOf(jtask.get("id")));
                TaskInfo reallyTaskInfo = dao.getExistedById(TaskInfo.class, task.id);
                task.name =String.valueOf( jtask.get("name"));
                task.projectName = String.valueOf(jtask.get("projectName"));
                task.serialNo = String.valueOf(jtask.get("serialNo"));
                task.uuid = String.valueOf(jtask.get("uuid"));
                task.isDelete = Boolean.parseBoolean(String.valueOf(jtask.get("isDelete")));
                task.objectType = reallyTaskInfo.objectType;
                task.objectTypeName = String.valueOf(jtask.get("objectTypeName"));
                task.projectId = Integer.parseInt(String.valueOf(jtask.get("projectId")));
                task.projectUuid = reallyTaskInfo.projectUuid;
                task.statusName = String.valueOf(jtask.get("statusName"));
                StringBuilder title = new StringBuilder();
                if (e.optAccountName != null) {
                    title.append(e.optAccountName);
                }
                if (e.type == AccountNotificationSetting.TYPE_对象属性变更) {
                    title.append("修改了").append(task.objectTypeName).append("属性");
                }
                if (e.type == AccountNotificationSetting.TYPE_日历提醒) {
                    title = new StringBuilder("日历提醒");
                }
                if (e.type == AccountNotificationSetting.TYPE_对象指派) {
                    String ownerList = (String) map.get("ownerList");
                    if (StringUtil.isEmpty(ownerList)) {
                        ownerList = "待认领";
                    }
                    title.append("将责任人设置为" + ownerList);
                }
                if (e.type == AccountNotificationSetting.TYPE_对象评论) {
                    title.append("发表了新的评论");
                }
                if (e.type == AccountNotificationSetting.TYPE_在评论中被提醒) {
                    title.append("在评论中@了你");
                }
                if (e.type == AccountNotificationSetting.TYPE_对象删除) {
                    title.append("删除" + task.objectTypeName);
                }
                if (e.type == AccountNotificationSetting.TYPE_对象提交代码) {
                    title.append("提交代码");
                }
                if (e.type == AccountNotificationSetting.TYPE_对象新增子对象) {
                    title.append("新增子" + task.objectTypeName);
                }
                if (e.type == AccountNotificationSetting.TYPE_对象关联其它对象) {
                    title.append("新增关联" + task.objectTypeName);
                }
                if (e.type == AccountNotificationSetting.TYPE_对象上传附件) {
                    title.append("上传附件");
                }
                if (e.type == AccountNotificationSetting.TYPE_对象删除子对象) {
                    title.append("删除子" + task.objectTypeName);
                }
                if (e.type == AccountNotificationSetting.TYPE_对象取消关联其它对象) {
                    title.append("取消关联" + task.objectTypeName);
                }
                if (e.type == AccountNotificationSetting.TYPE_对象删除附件) {
                    title.append("删除附件");
                }
                if (e.type == AccountNotificationSetting.TYPE_对象提醒) {
                    title = new StringBuilder("提醒");
                }
                if (setting == null || setting.isWeixinEnable) {//发送微信通知
                    WxSendMsgUtil.sendTaskInfoMessage(weixinService, task,
                            account.wxOpenId, title.toString(), "点击查看详情");
                }
                if (setting == null || setting.isEmailEnable) {//发送邮件通知
                    EmailMsgUtil.sendTaskInfoMessage(task, account, title.toString());
                }
                if (setting == null || setting.isLarkEnable) {//发送飞书通知
                    larkService.sendTaskInfoMessage(task, account, title.toString());
                }
                if (setting == null || setting.isDingtalkEnable) {//发送钉钉通知
                    dingtalkService.sendTaskInfoMessage(account.dingtalkUserId, task, title.toString());
                }
            } else if (e.type == AccountNotificationSetting.TYPE_PIPELINE提醒) {
                PipelineNotification bean = JSONUtil.fromJson(e.content, PipelineNotification.class);
                if (setting == null || setting.isWeixinEnable) {//发送微信通知
                    WxSendMsgUtil.sendMessage(weixinService, account.wxOpenId, bean.content,
                            bean.projectName, "-", "点击查看详情", GlobalConfig.webUrl);
                }
                if (setting == null || setting.isEmailEnable) {//发送邮件通知
                    EmailMsgUtil.sendMessage(account, "Pipeline提醒", bean.content, bean.projectName, "-", "",
                            GlobalConfig.webUrl);
                }
                if (setting == null || setting.isLarkEnable) {//发送飞书通知
                    larkService.sendCardMessage(null, account,
                            "Pipeline提醒",
                            Arrays.asList(bean.content,
                                    "项目：" + bean.projectName),
                            "查看", larkService.getCsUrl(), null);
                }
                if (setting == null || setting.isDingtalkEnable) {//发送钉钉通知
                    dingtalkService.sendMessage(account.dingtalkUserId, "Pipeline提醒", bean.content, Arrays.asList(
                            new DingtalkService.MsgRow("项目名称", bean.projectName)), dingtalkService.getCsUrl(),false);
                }
            } else if (e.type == AccountNotificationSetting.TYPE_个人已超期任务提醒) {
                Map<String, Object> bean = JSONUtil.fromJson(e.content, Map.class);
                String name = bean.get("name").toString();
                String projectNames = bean.get("projectNames").toString();
                if (setting == null || setting.isWeixinEnable) {//发送微信通知
                    WxSendMsgUtil.sendMessage(weixinService, account.wxOpenId, name,
                            projectNames, "-", "点击查看详情", GlobalConfig.webUrl);
                }
                if (setting == null || setting.isEmailEnable) {//发送邮件通知
                    EmailMsgUtil.sendMessage(account, "个人已超期任务提醒",
                            name + "<br>项目名称：" + projectNames,
                            GlobalConfig.webUrl);
                }
                if (setting == null || setting.isLarkEnable) {//发送飞书通知
                    larkService.sendCardMessage(null, account,
                            "个人已超期任务提醒",
                            Arrays.asList(name,
                                    "项目：" + projectNames),
                            "查看", larkService.getCsUrl(), larkService.getCsUrl());
                }
                if (setting == null || setting.isDingtalkEnable) {//发送钉钉通知
                    dingtalkService.sendMessage(account.dingtalkUserId, "个人已超期任务提醒", name, Arrays.asList(
                            new DingtalkService.MsgRow("项目名称", projectNames)), dingtalkService.getCsUrl(),true);
                }
            } else if (e.type == AccountNotificationSetting.TYPE_系统通知提醒) {
                Map<String, Object> bean = JSONUtil.fromJson(e.content, Map.class);
                if (BizUtil.isNullOrEmpty(bean.get("content"))) {
                    return;
                }
                String content = bean.get("content").toString();
                String name = e.name;
                if (setting == null || setting.isWeixinEnable) {//发送微信通知
                    WxSendMsgUtil.sendMessage(weixinService, account.wxOpenId, content,
                            name, "-", "系统通知", null);
                }
                if (setting == null || setting.isEmailEnable) {//发送邮件通知
                    EmailMsgUtil.sendMessage(account, "系统通知",
                            "标题：" + name + "<br>内容：" + content,
                            GlobalConfig.webUrl);
                }
                if (setting == null || setting.isLarkEnable) {//发送飞书通知
                    larkService.sendCardMessage(null, account,
                            "系统通知",
                            Arrays.asList("标题：" + name,
                                    "内容：" + content),
                            "查看", larkService.getCsUrl(), larkService.getCsUrl());
                }
                if (setting == null || setting.isDingtalkEnable) {//发送钉钉通知
                    dingtalkService.sendMessage(account.dingtalkUserId, "系统通知", null, Arrays.asList(
                            new DingtalkService.MsgRow("标题", name),
                            new DingtalkService.MsgRow("内容", content)), dingtalkService.getCsUrl(),false);
                }
            } else if (e.type == AccountNotificationSetting.TYPE_流程提醒) {
                WorkflowInstanceNotify bean = JSONUtil.fromJson(e.content, WorkflowInstanceNotify.class);
                String title = "您参与的流程#" + bean.serialNo + bean.title +
                        "，步骤从【" + bean.beforeNodeName + "】变更到" + "【" + bean.currNodeName + "】";
                if (bean.enableNotifyWechat) {//发送微信通知
                    String url = GlobalConfig.webUrl + "p/wx/enter_workflow_info?state=" + bean.uuid;
                    WxSendMsgUtil.sendMessage(weixinService, account.wxOpenId, title,
                            bean.workflowDefineName, "-", "点击查看详情", url);
                }
                if (bean.enableNotifyEmail) {//发送邮件通知
                    EmailMsgUtil.sendMessage(account, "流程变更提醒",
                            title,
                            GlobalConfig.webUrl);
                }
                if (setting == null || setting.isLarkEnable) {//发送飞书通知
                    larkService.sendCardMessage(null, account,
                            "流程变更提醒",
                            Arrays.asList(title),
                            "查看", larkService.getCsUrl(), larkService.getCsUrl());
                }
                if (setting == null || setting.isDingtalkEnable) {//发送钉钉通知
                    dingtalkService.sendMessage(account.dingtalkUserId, "流程变更提醒", title, null, dingtalkService.getCsUrl(),false);
                }
            } else if (e.type == AccountNotificationSetting.TYPE_流程沟通) {
                WorkflowInstanceNotify bean = JSONUtil.fromJson(e.content, WorkflowInstanceNotify.class);
                String title = "您参与的流程#" + bean.serialNo + bean.title +
                        "，收到@提醒";
                if (bean.enableNotifyWechat) {//发送微信通知
                    String url = GlobalConfig.webUrl + "p/wx/enter_workflow_info?state=" + bean.uuid;
                    WxSendMsgUtil.sendMessage(weixinService, account.wxOpenId, title,
                            bean.workflowDefineName, "-", "点击查看详情", url);
                }
                if (bean.enableNotifyEmail) {//发送邮件通知
                    EmailMsgUtil.sendMessage(account, "在流程@了你",
                            title,
                            GlobalConfig.webUrl);
                }
                if (setting == null || setting.isLarkEnable) {//发送飞书通知
                    larkService.sendCardMessage(null, account,
                            "在流程@了你",
                            Arrays.asList(title),
                            "查看", larkService.getCsUrl(), larkService.getCsUrl());
                }
                if (setting == null || setting.isDingtalkEnable) {//发送钉钉通知
                    dingtalkService.sendMessage(account.dingtalkUserId, "在流程@了你", title, null, dingtalkService.getCsUrl(),false);
                }
            } else if (e.type == AccountNotificationSetting.TYPE_待填写汇报) {
                Report bean = JSONUtil.fromJson(e.content, Report.class);
                String title = "待填写汇报通知";
                if (setting == null || setting.isWeixinEnable) {//发送微信通知
                    WxSendMsgUtil.sendMessage(weixinService, account.wxOpenId, title,
                            bean.name, "-", "", null);
                }
                if (setting == null || setting.isEmailEnable) {//发送邮件通知
                    EmailMsgUtil.sendMessage(account,
                            title,
                            bean.name,
                            GlobalConfig.webUrl);
                }
                if (setting == null || setting.isLarkEnable) {//发送飞书通知
                    larkService.sendCardMessage(null, account,
                            "待填写汇报通知",
                            Arrays.asList(title),
                            "查看", larkService.getCsUrl(), larkService.getCsUrl());
                }
                if (setting == null || setting.isDingtalkEnable) {//发送钉钉通知
                    dingtalkService.sendMessage(account.dingtalkUserId, "待填写汇报通知", null,
                            Arrays.asList(
                                    new DingtalkService.MsgRow("名称", bean.name)
                            ), dingtalkService.getCsUrl(),false);
                }
            } else if (e.type == AccountNotificationSetting.TYPE_待审核汇报) {
                Report bean = JSONUtil.fromJson(e.content, Report.class);
                String title = "待审核汇报通知";
                if (setting == null || setting.isWeixinEnable) {//发送微信通知
                    WxSendMsgUtil.sendMessage(weixinService, account.wxOpenId, title,
                            bean.name, "-", "", null);
                }
                if (setting == null || setting.isEmailEnable) {//发送邮件通知
                    EmailMsgUtil.sendMessage(account, title,
                            bean.name,
                            GlobalConfig.webUrl);
                }
                if (setting == null || setting.isLarkEnable) {//发送飞书通知
                    larkService.sendCardMessage(null, account,
                            "待填写汇报通知",
                            Arrays.asList(title),
                            "查看", larkService.getCsUrl(), larkService.getCsUrl());
                }
                if (setting == null || setting.isDingtalkEnable) {//发送钉钉通知
                    dingtalkService.sendMessage(account.dingtalkUserId, "待审核汇报通知", null, Arrays.asList(
                            new DingtalkService.MsgRow("名称", bean.name)
                    ), dingtalkService.getCsUrl(),false);
                }
            }
        }
        //

        @Override
        public List<ProjectPipeline> getNeedRunProjectPipelineInfos() {
            return dao.getNeedRunProjectPipelineInfos();
        }

        @Override
        public List<TaskRemindTime> getNeedRunTaskRemindTimes() {
            return dao.getNeedRunTaskRemindTimes();
        }

        //
        @Transaction
        @Override
        public void runProjectPipeline(ProjectPipeline bean) {
            ProjectPipelineInfo old = dao.getExistedByIdForUpdate(ProjectPipelineInfo.class, bean.id);
            if (bean.runCount != old.runCount) {
                logger.info("bean.runCount:{}!=old.runCount:{}",
                        bean.runCount, old.runCount);
                return;
            }
            if (System.currentTimeMillis() - old.nextRunTime.getTime() > 5 * 60 * 1000) {//超过5分钟就不跑了
                logger.info("bean{} nextRunTime:{}", bean.id, old.nextRunTime);
                old.nextRunTime = BizUtil.nextRunTime(null, old.cron);
                logger.info("bean{} newNextRunTime:{}", bean.id, old.nextRunTime);
                dao.update(old);
                return;
            }
            bizService.runProjectPipeline(null, old);
        }

        @Override
        public List<CmdbRobot> getNeedRunCmdbRobots() {
            return dao.getNeedRunCmdbRobots();
        }

        @Transaction
        @Override
        public void runCmdbRobot(CmdbRobot bean) {
//			CmdbRobot old=dao.getExistedByIdForUpdate(CmdbRobot.class, bean.id);
//			if(bean.runCount!=old.runCount) {
//				logger.info("bean.runCount:{}!=old.runCount:{}",
//						bean.runCount,old.runCount);
//				return;
//			}
//			bizService.runCmdbRobot(old);
        }

        @Override
        public List<AccountNotificationInfo> getAccountNotificationInfoList(AccountNotificationQuery query) {
            return dao.getList(query);
        }

        @Override
        public void initMysqlProxy() {
            mysqlProxyService.init();
        }

        @Override
        public Account getAccountByUserName(String userName) {
            return dao.getAccountByUserName(userName);
        }

        @Override
        public void checkAccountPermission(int accountId, DesignerMysqlProxyInstance instance) {
            if ((instance.roles == null || instance.roles.isEmpty()) &&//没有设置角色和成员，则默认都允许
                    StringUtil.isEmptyWithTrim(instance.members)) {
                return;
            }
            Account account = dao.getExistedById(Account.class, accountId);
            if (instance.roles != null && (!instance.roles.isEmpty())) {
                Set<Integer> myRoles = dao.getAllCompanyRoles(account);
                for (Integer role : instance.roles) {
                    if (myRoles.contains(role)) {
                        return;
                    }
                }
            }
            if (!StringUtil.isEmptyWithTrim(instance.members)) {
                String[] userNames = instance.members.split(",");
                for (String userName : userNames) {
                    if (userName == null) {
                        continue;
                    }
                    if (userName.equals(account.userName)) {
                        return;
                    }
                }
            }
            throw new AppException("权限不足");
        }

        @Override
        public List<DesignerDatabase> getAllDesignerDatabases() {
            DesignerDatabaseInfoQuery query = new DesignerDatabaseInfoQuery();
            query.updateTimeSort = Query.SORT_TYPE_DESC;
            query.pageSize = 10000;
            return dao.getList(query);
        }

        @Override
        public void doJudgeDatabaseChangeLog(DesignerDatabase e) {
            String dbUrl = "jdbc:mysql://" + e.host + ":" + e.port + "/" + e.instanceId + "?" + GlobalConfig.mysqlDBUrlParams + "&useInformationSchema=true";
            String password = TripleDESUtil.decrypt(e.dbPassword, ConstDefine.GLOBAL_KEY);
            List<String> tables = BizUtil.split(e.dmlTables, ",");
            DatabaseInfo databaseInfo = MySQLDatabaseSchema.getDatabaseInfo(dbUrl, e.instanceId, e.dbUser, password, tables);
            if (StringUtil.isEmpty(databaseInfo.ddl)) {
                return;
            }
            DesignerDatabaseChangeLog last = dao.getLastDesignerDatabaseChangeLog(e.id);
            boolean isDdlChanged = false;
            boolean isDmlChanged = false;
            if (last != null) {
                isDdlChanged = !last.ddl.equals(databaseInfo.ddl);
                isDmlChanged = !last.dml.equals(databaseInfo.dml);
            }
            if (last == null || isDdlChanged || isDmlChanged) {
                last = new DesignerDatabaseChangeLog();
                last.companyId = e.companyId;
                last.ddl = databaseInfo.ddl;
                last.dml = databaseInfo.dml;
                last.tableCount = databaseInfo.tableCount;
                last.isDdlChanged = isDdlChanged;
                last.isDmlChanged = isDmlChanged;
                last.designerDatabaseId = e.id;
                dao.add(last);
            }
        }

        @Transaction
        @Override
        public void doSendTaskRemind(TaskRemindTime bean) {
            dao.deleteById(TaskRemindTime.class, bean.id);
            TaskRemind remind = dao.getById(TaskRemind.class, bean.taskRemindId);
            if (remind == null) {
                logger.error("remind==null {}", DumpUtil.dump(bean));
                return;
            }
            if (remind.remindAccountIdList == null || remind.remindAccountIdList.isEmpty()) {
                return;
            }
            //找到需要通知的人
            Set<Integer> accountIds = BizUtil.convert(remind.remindAccountIdList);
            if (accountIds.isEmpty()) {
                return;
            }
            TaskInfo task = dao.getExistedById(TaskInfo.class, remind.taskId);
            TaskSimpleInfo taskSimpleInfo = TaskSimpleInfo.createTaskSimpleinfo(task);
            Map<String, Object> map = new HashMap<>();
            map.put("task", taskSimpleInfo);
            map.put("remind", remind);
            for (Integer accountId : accountIds) {
                bizService.addAccountNotification(accountId,
                        AccountNotificationSetting.TYPE_对象提醒,
                        task.companyId,
                        task.projectId,
                        task.id,
                        task.name,
                        JSONUtil.toJson(map),
                        new Date(),
                        0,
                        null,
                        null
                );
            }
        }

        @Override
        public List<ReportTemplate> getNeedRunReportTemplates() {
            return dao.getNeedRunReportTemplates();
        }

        //
        @Transaction
        @Override
        public void doGenerateReport(ReportTemplate bean) {
            ReportTemplate old = dao.getExistedByIdForUpdate(ReportTemplate.class, bean.id);
            bizService.setNextRemindTimeNew(old);
            dao.update(old);
            //
            if (bean.submitterIds == null || bean.submitterIds.size() == 0) {
                return;
            }
            for (Integer submitterId : bean.submitterIds) {
                Account account = dao.getExistedById(Account.class, submitterId);
                if (account.status == Account.STATUS_无效) {
                    continue;
                }
                Report report = new Report();
                report.status = Report.STATUS_待汇报;
                report.reportTemplateId = bean.id;
                report.reportTime = bean.reportTime;
                report.companyId = bean.companyId;
                report.period = bean.period;
                report.name = bean.name;
                report.projectId = bean.projectId;
                report.submitterId = submitterId;
                report.uuid = BizUtil.randomUUID();
                report.auditorIds = bean.auditorIds;
                report.auditorList = bean.auditorList;
                dao.add(report);
                //
                bizService.addAccountNotification(account.id,
                        AccountNotificationSetting.TYPE_待填写汇报,
                        report.companyId, report.projectId, bean.id,
                        "待填写汇报", JSONUtil.toJson(report),
                        new Date(), null);
                //
                for (ReportTemplateContent e : bean.content) {
                    ReportContent rc = new ReportContent();
                    rc.reportId = report.id;
                    rc.companyId = report.companyId;
                    rc.title = e.title;
                    rc.content = e.content;
                    rc.type = ReportContent.TYPE_汇报内容;
                    rc.createAccountId = report.submitterId;
                    dao.add(rc);
                }
            }
        }

        @Override
        public void initIkAnalyzerDicts() {
            logger.info("initIkAnalyzerDicts");
            List<String> accountNames = dao.getAccountNames();
            Dictionary.getSingleton().addWords(accountNames);
            logger.info("initIkAnalyzerDicts add {} accountNames", accountNames.size());
        }

        @Override
        public void doCalcFilePath() {
            List<File> files = dao.getAll(File.class);
            Queue<File> queue = new LinkedList<>();
            for (File file : files) {
                if (file.level == 1) {
                    String path = "/" + file.name;
                    if (file.path == null || !file.path.equals(path)) {
                        file.path = path;
                        dao.updateSpecialFields(file, "path");
                    }
                    if (file.isDirectory) {
                        queue.offer(file);
                    }
                }
            }
            while (!queue.isEmpty()) {
                File parent = queue.poll();
                for (File file : files) {
                    if (file.parentId == parent.id) {
                        String path = parent.path + "/" + file.name;
                        if (file.path == null || !file.path.equals(path)) {
                            file.path = path;
                            dao.updateSpecialFields(file, "path");
                        }
                        if (file.isDirectory) {
                            queue.offer(file);
                        }
                    }
                }
            }
        }

        @Transaction
        @Override
        public void doCalcProjectActivity() {
            List<ProjectActivity> list = dao.getProjectActivityList();
            Map<String, Integer> numsCount = new HashMap<>();
            List<Integer> projectIds = dao.getCompanyProjectIdList();
            Set<Integer> projects = new HashSet<>();
            for (ProjectActivity e : list) {
                numsCount.put(e.projectId + "-" + e.riqi, e.num);
                projects.add(e.projectId);
            }
            for (Integer projectId : projects) {
                Date startDate = DateUtil.getBeginOfDay(DateUtil.getNextDay(-15));
                List<Integer> nums = new ArrayList<>();
                for (int i = 0; i < 15; i++) {
                    Date date = DateUtil.getNextDay(startDate, i);
                    String key = projectId + "-" + DateUtil.formatDate(date, "yyyy-MM-dd");
                    Integer num = numsCount.get(key);
                    if (num == null) {
                        num = 0;
                    }
                    nums.add(num);
                }
                dao.updateProjectActivityNums(projectId, JSONUtil.toJson(nums));
            }
            //15天内没有任何活动的项目
            List<Integer> nums = new ArrayList<>();
            for (int i = 0; i < 15; i++) {
                nums.add(0);
            }
            for (Integer projectId : projectIds) {
                if (projects.contains(projectId)) {
                    continue;
                }
                dao.updateProjectActivityNums(projectId, JSONUtil.toJson(nums));
            }
        }

        @Transaction
        @Override
        public void doResetDailyLoginFailCountKaptchaErrorCount() {
            dao.doResetDailyLoginFailCountKaptchaErrorCount();
        }

        /**
         * 重跑所有项目每日统计数据
         */
        @Transaction
        @Override
        public void updateProjectTaskDayData() {
            CompanyQuery query = new CompanyQuery();
            List<Company> companies = dao.getAll(query);
            int total = companies.size();
            int index = 1;
            for (Company c : companies) {
                Task minTask = dao.getCompanyTaskMinCreateTime(c.id);
                if (minTask == null) {
                    continue;
                }
                Date statDate = DateUtil.getNextDay(DateUtil.getBeginOfDay(minTask.createTime), -1);
                Date today = DateUtil.getToday();
                logger.info("updateProjectTaskDayData company:{} {}/{}", c.name, index++, total);
                while (true) {
                    statDate = DateUtil.getNextDay(statDate, 1);
                    if (statDate.after(today) || statDate.equals(today)) {
                        break;
                    }
                    createTaskDayDataByProject0(statDate);
                }
            }
        }

        /**
         * 重跑所有迭代每日统计数据
         */
        @Transaction
        @Override
        public void updateIterationTaskDayData() {
            CompanyQuery query = new CompanyQuery();
            List<Company> companies = dao.getAll(query);
            int total = companies.size();
            int index = 1;
            for (Company c : companies) {
                Task minTask = dao.getCompanyTaskMinCreateTime(c.id);
                if (minTask == null) {
                    continue;
                }
                Date statDate = DateUtil.getNextDay(DateUtil.getBeginOfDay(minTask.createTime), -1);
                Date today = DateUtil.getToday();
                logger.info("updateIterationTaskDayData company:{} {}/{}", c.name, index++, total);
                while (true) {
                    statDate = DateUtil.getNextDay(statDate, 1);
                    if (statDate.after(today) || statDate.equals(today)) {
                        break;
                    }
                    createTaskDayDataByIteration0(statDate);
                }
            }
        }

        /**
         * 推送超期任务
         */
        @Override
        public void doSendTaskOverDueNotification() {
            List<AccountOverDueTask> tasks = dao.getAccountOverDueTask();
            logger.info("doSendTaskOverDueNotification tasksNum:{} ", tasks.size());
            for (AccountOverDueTask e : tasks) {
                Map<String, Object> content = new HashMap<>();
                content.put("name", "您有" + e.num + "个任务已超期，请及时处理");
                List<AccountProjectOverDueTask> list = dao.getAccountProjectOverDueTask(e.accountId);
                if (list.isEmpty()) {
                    continue;
                }
                StringBuilder projectNames = new StringBuilder();
                for (AccountProjectOverDueTask ee : list) {
                    projectNames.append(ee.projectName).append(",");
                }
                projectNames.deleteCharAt(projectNames.length() - 1);
                content.put("projectNames", projectNames.toString());
                bizService.addAccountNotification(e.accountId,
                        AccountNotificationSetting.TYPE_个人已超期任务提醒, e.companyId,
                        0, 0, "个人已超期任务提醒",
                        JSONUtil.toJson(content), new Date(), 0, "", "");
            }

            //快到期提醒天数
            int aboutToExpire = Integer.parseInt(GlobalConfig.getValue("task.aboutToExpire", "0"));
            if (aboutToExpire <= 0) {
                return;
            }
            List<AccountOverDueTask> expireTasks = dao.getAccountAboutToExpireTask(aboutToExpire);
            logger.info("doSend about to expire Notification tasksNum:{} ", expireTasks.size());
            for (AccountOverDueTask e : expireTasks) {
                Map<String, Object> content = new HashMap<>();
                content.put("name", "您有" + e.num + "个任务即将到期，请及时处理");
                List<AccountProjectOverDueTask> list = dao.getAccountProjectOverDueTask(e.accountId);
                if (list.isEmpty()) {
                    continue;
                }
                StringBuilder projectNames = new StringBuilder();
                for (AccountProjectOverDueTask ee : list) {
                    projectNames.append(ee.projectName).append(",");
                }
                projectNames.deleteCharAt(projectNames.length() - 1);
                content.put("projectNames", projectNames.toString());
                bizService.addAccountNotification(e.accountId,
                        AccountNotificationSetting.TYPE_个人即将到期任务提醒, e.companyId,
                        0, 0, "个人任务即将到期提醒",
                        JSONUtil.toJson(content), new Date(), 0, "", "");
            }

        }

        @Override
        public void doFixTaskOwnerDatas() {
            TaskQuery query = new TaskQuery();
            query.isDelete = false;
            query.ownerAccountIdIsNotNull = true;
            int totalCount = dao.getListCount(query);
            int pageSize = 10000;
            query.pageSize = pageSize;
            int totalPage = 1;
            if (totalCount > pageSize) {
                totalPage = totalCount / pageSize;
                if (totalCount % pageSize != 0) {
                    totalPage = totalPage + 1;
                }
            }
            logger.info("doFixTaskOwnerDatas count:{} totalPage:{}", totalCount, totalPage);
            Set<String> includeFields = new LinkedHashSet<>();
            includeFields.add("id");
            includeFields.add("ownerAccountIdList");
            includeFields.add("createTime");
            int index = 1;
            for (int pageIndex = 1; pageIndex <= totalPage; pageIndex++) {
                query.pageIndex = pageIndex;
                List<TaskInfo> list = dao.getList(query, includeFields);
                for (TaskInfo e : list) {
                    if (e.ownerAccountIdList == null) {
                        continue;
                    }
                    for (Integer accountId : e.ownerAccountIdList) {
                        TaskOwner bean = dao.getTaskOwner(e.id, accountId);
                        if (bean != null) {
                            continue;
                        }
                        bean = new TaskOwner();
                        bean.companyId = e.companyId;
                        bean.taskId = e.id;
                        bean.accountId = accountId;
                        dao.add(bean);
                    }
                    logger.info("doFixTaskOwnerDatas progress:{}/{}", index++, totalCount);
                }
            }
        }


        @Transaction
        @Override
        public void doFixDataForV60() {
            int fixed = Integer.parseInt(GlobalConfig.getValue(ConstDefine.V60_DATA_REPAIR_FLAG, "1"));
            if (fixed == 1) {
                return;
            }
            //里程碑数据
            StageAssociate.StageAssociateQuery query = new StageAssociate.StageAssociateQuery();
            query.type = StageAssociate.TYPE_里程碑;
            List<StageAssociate.StageAssociateInfo> assocaiteLandmarkList = dao.getAll(query);
            if (!BizUtil.isNullOrEmpty(assocaiteLandmarkList)) {
                assocaiteLandmarkList.forEach(assocaiteLandmark -> {
                    try {
                        Landmark landmark = new Landmark();
                        landmark.companyId = assocaiteLandmark.companyId;
                        landmark.projectId = assocaiteLandmark.projectId;
                        landmark.stageId = assocaiteLandmark.stageId;
                        landmark.status = 1;
                        landmark.name = assocaiteLandmark.taskName;
                        landmark.startDate = assocaiteLandmark.taskCreateTime;
                        landmark.endDate = assocaiteLandmark.taskEndTime;
                        landmark.createAccountId = assocaiteLandmark.createAccountId;
                        landmark.updateAccountId = assocaiteLandmark.updateAccountId;
                        dao.add(landmark);
                        assocaiteLandmark.landmarkId = landmark.id;
                        dao.updateSpecialFields(assocaiteLandmark, "landmarkId");
                    } catch (Exception e) {
                        logger.error("repair landmark data fail: " + e.getMessage());
                    }

                });
            }

            //创建权限
            Pattern pattern = Pattern.compile("^task_edit_\\d+");
            Role.RoleQuery query1 = new Role.RoleQuery();
            List<Role> roles = dao.getAll(query1);
            if (!BizUtil.isNullOrEmpty(roles)) {
                for (Role role : roles) {
                    try {
                        Set<String> permissionIds = role.permissionIds;
                        boolean match = false;
                        if (!BizUtil.isNullOrEmpty(permissionIds)) {
                            Set<String> adds = new HashSet<>();
                            for (String permissionId : permissionIds) {
                                boolean m = pattern.matcher(permissionId).matches();
                                if (m) {
                                    match = true;
                                    adds.add("task_create_" + permissionId.substring(10));
                                }
                            }
                            if (match) {
                                permissionIds.addAll(adds);
                                role.permissionIds = permissionIds;
                                dao.updateSpecialFields(role, "permissionIds");
                            }
                        }
                    } catch (Exception e) {
                        logger.error("repair create permission data fail: " + e.getMessage());
                    }
                }
            }


            List<Config> configs = new ArrayList<>();
            Config config = new Config();
            config.name = ConstDefine.V60_DATA_REPAIR_FLAG;
            config.value = "1";
            config.isHidden = true;
            config.valueType = "String";
            configs.add(config);
            GlobalConfig.setupConfig(configs);
            GlobalConfig.setValue(ConstDefine.V60_DATA_REPAIR_FLAG, "1");
            dao.updateDataFixFlag(ConstDefine.V60_DATA_REPAIR_FLAG);
        }

        @Override
        public void doFixDataForV65() {
            List<Permission> permissions = dao.getAll(Permission.class);
            Pattern pattern = Pattern.compile("^task_\\d+");
            Map<String, List<Permission>> pm = permissions.stream().filter(k -> null != k.parentId && k.id.startsWith("task_")).collect(Collectors.groupingBy(k -> k.parentId));
            List<Permission> dismissPermission = new ArrayList<>();
            pm.forEach((key, ps) -> {
                boolean m = pattern.matcher(key).matches();
                if (m) {
                    if (!BizUtil.isNullOrEmpty(ps)) {
                        boolean hasCreatePermission = ps.stream().anyMatch(k -> k.id.startsWith("task_create_"));
                        if (!hasCreatePermission) {
                            int objectType = Integer.parseInt(key.substring(5));
                            Permission cp = new Permission();
                            cp.id = "task_create_" + objectType;
                            cp.type = 1;
                            cp.name = "创建";
                            cp.parentId = key;
                            cp.isDataPermission = false;
                            cp.isMemberPermission = true;
                            cp.sortWeight = 10;
                            cp.objectType = objectType;
                            dismissPermission.add(cp);
                        }
                        boolean hasTimePermission = ps.stream().anyMatch(k -> k.id.startsWith("task_edit_finish_date_"));
                        if (!hasTimePermission) {
                            int objectType = Integer.parseInt(key.substring(5));
                            Permission tp = new Permission();
                            tp.id = "task_edit_finish_date_" + objectType;
                            tp.type = 1;
                            tp.name = "修改完成时间";
                            tp.parentId = key;
                            tp.isDataPermission = false;
                            tp.isMemberPermission = true;
                            tp.sortWeight = 10;
                            tp.objectType = objectType;
                            dismissPermission.add(tp);
                        }
                    }
                }
            });

            for (Permission permission : dismissPermission) {
                dao.addNotWithGenerateKey(permission);
            }
        }

        @Override
        public List<SystemHook> getNeedRunSystemHooks() {
            return dao.getNeedRunSystemHooks();
        }

        @Transaction
        @Override
        public void doRunSystemHook(SystemHook bean) {
            SystemHook old = dao.getExistedByIdForUpdate(SystemHook.class, bean.id);
            if (bean.runCount != old.runCount) {
                logger.info("bean.runCount:{}!=old.runCount:{}",
                        bean.runCount, old.runCount);
                return;
            }
            bizService.doRunSystemHook(null, old);
        }

        @Override
        public List<CompanyInfo> getNeedRunCompanies() {
            CompanyQuery query = new CompanyQuery();
            query.version = CompanyInfo.VERSION_私有部署版;
            return dao.getList(query);
        }

        @Transaction
        @Override
        public void doUpdateCompanyLiceneInfo(CompanyInfo bean) {
            Company old = dao.getByIdForUpdate(Company.class, bean.id);
            bizService.updateCompanyLicenseInfo(old, old.license);
        }

        @Override
        public void doCheckDataDictData() {
            // TODO Auto-generated method stub

        }

        @Override
        public List<WorkflowInstanceInfo> getNeedDeleteDraftWorkflowInstances() {
            WorkflowInstanceQuery query = new WorkflowInstanceQuery();
            query.status = WorkflowInstanceInfo.STATUS_草稿;
            query.createTimeEnd = DateUtil.getNextDay(-1);
            query.pageSize = 1000;
            return dao.getList(query);
        }

        @Transaction
        @Override
        public void deleteWorkflowInstance(int id) {
            workflowService.deleteWorkflowInstance(id);
        }

        @Override
        public void doCallProcCheckData() {
            List<CallProcCheckDataResult> list = dao.callProcCheckData();
            for (CallProcCheckDataResult e : list) {
                dao.add(e);
            }
        }

        @Override
        public List<TaskActionJob> getNeedRunTaskActionJobs() {
            return dao.getNeedRunTaskActionJobs();
        }

        @Transaction
        @Override
        public void updateTaskActionJob(TaskActionJob bean) {
            dao.update(bean);
        }

        @Transaction
        @Override
        public void updateProjectPinyinShortName() {
            List<Project> list = dao.getAll(Project.class);
            for (Project e : list) {
                e.pinyinShortName = bizService.getProjectPinyinShortName(e.name);
                dao.updateSpecialFields(e, "pinyinShortName");
            }
        }

        @Override
        public void doResetAccountInviteMemberDailySendMailNum() {
            dao.doResetAccountInviteMemberDailySendMailNum();
        }

        @Override
        public void doDeleteInvalidKaptchas() {
            //删除过期时间三天前的
            dao.delete(Kaptcha.class, QueryWhere.create().where("valid_time", "<", DateUtil.getNextDay(-3)));
        }

        @Transaction
        @Override
        public void doSendSystemNotifaction(int id) {
            SystemNotificationInfo old = dao.getExistedById(SystemNotificationInfo.class, id);
            Date nextNotifyTime = old.nextNotifyTime;
            if (nextNotifyTime.after(new Date())) {
                return;
            }
            try {
                old.nextNotifyTime = bizService.getNextNotifyTime(old);
            } catch (Exception e) {
                old.nextNotifyTime = null;
                logger.error("is bug doSendSystemNotifaction " + e.getMessage(), e);
            }
            dao.update(old);
            //
            Set<Integer> getAccountIds = bizService.getAccountIds(old.companyId,
                    old.accountList, old.companyRoleList, old.projectRoleList,
                    old.departmentList, null);
            for (Integer accountId : getAccountIds) {
                try {
                    Map<String, Object> content = new HashMap<>();
                    content.put("content", old.content);
                    bizService.addAccountNotification(accountId,
                            AccountNotificationSetting.TYPE_系统通知提醒, old.companyId,
                            0, id, old.title, JSONUtil.toJson(content),
                            nextNotifyTime, old.createAccountId,
                            old.createAccountName, old.createAccountImageId);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        @Override
        public List<Integer> getNeedSendSystemNotifactionList() {
            return dao.getNeedSendSystemNotifactionList();
        }


        @Transaction
        @Override
        public void asyncSendEmail() {
            int idx = 0;
            while (idx < 100) {
                EmailLimitHelper.EmailEntity email = EmailLimitHelper.getEmail();
                long start = System.currentTimeMillis();
                if (null != email) {
                    try {
                        EmailMsgUtil.sendMessage0(email.email, email.title, email.content, email.url);
                        long cost = System.currentTimeMillis() - start;
                        if (cost < 500) {
                            Thread.sleep(500 - cost);
                        }
                    } catch (Exception e) {
                        logger.error("send email fail", e);
                    }
                }
                idx++;
            }
        }

        @Transaction
        @Override
        public void syncProjectSet() {
            Project templateProject = dao.getByIdForUpdate(Project.class, Project.ID_项目集模板ID);//一定要forUpdate
            if (templateProject == null) {
                logger.warn("templateProject==null");
                return;
            }
            CompanyQuery pq = new CompanyQuery();
            pq.pageSize = Integer.MAX_VALUE;
            List<Company> companyList = dao.getList(pq);
            if (!BizUtil.isNullOrEmpty(companyList)) {
                companyList.forEach(company -> {
                    Account account = dao.getById(Account.class, company.createAccountId);
                    if (null != account) {
                        account.companyId = company.id;
                        Project project = dao.getProjectByCompanyIdNameTemplateId(company.id,
                                "项目集管理", Project.ID_项目集模板ID);
                        if (null != project) {
                            bizService.syncTasksFromProject0(account, project.id);
                        }
                    }
                });
            }

        }

        @Transaction
        @Override
        public void syncDingtalkAccount() {
            bizService.syncDingtalkMember();
        }

        @Transaction
        @Override
        public void syncDingtalkAttendance() {
            bizService.syncDingtalkAttendance(new Date());
        }

        @Transaction
        @Override
        public void syncAdAccount() {
//            List<Account> accounts = LdapUtil.getAdAccounts();
//            logger.info("syncAdAccount---> size:{}",accounts.size());
//            if(!BizUtil.isNullOrEmpty(accounts)){
//                dao.batchUpdateAdAccount(accounts);
//            }
            bizService.syncAdAccount();
        }

        @Transaction
        @Override
        public void syncProjectSpiProcess() {
            double spiMax = Double.parseDouble(GlobalConfig.getValue("project.spi.max", "0"));
            double spiMin = Double.parseDouble(GlobalConfig.getValue("project.spi.min", "0"));
            if (spiMax <= 0) {
                return;
            }
            /*
             * SPI=挣值/计划价值=实际投入工时/计划投入工时
             * 实际投入工时=该项目实际已用工时
             * 计划投入工时=项目预计工时/（项目计划结束日期-项目计划开始日期）*（当前日期-项目计划开始日期）
             * 当前日期：当前日期-1   （截止到当前日期前一天的项目进度）*/

            CompanyQuery query = new CompanyQuery();
            query.isDelete = false;
            query.pageSize = 10000;
            List<CompanyInfo> companyList = dao.getList(query, "license", "licenseId");
            if (!BizUtil.isNullOrEmpty(companyList)) {
                for (CompanyInfo company : companyList) {
                    boolean isPrivateDeploy = company.version == Company.VERSION_私有部署版;
                    if (!isPrivateDeploy) {
                        continue;
                    }

                    Project.ProjectQuery pq = new Project.ProjectQuery();
                    pq.companyId = company.id;
                    pq.isTemplate = false;
                    pq.isDelete = false;
                    pq.isFinish = false;
                    pq.pageSize = Integer.MAX_VALUE;
                    List<Project.ProjectInfo> projectList = dao.getList(pq);
                    if (!BizUtil.isNullOrEmpty(projectList)) {
                        for (Project.ProjectInfo project : projectList) {
                            if (project.templateId == Project.ID_项目集模板ID || null == project.startDate
                                    || null == project.endDate ) {
                                continue;
                            }
                            if(BizUtil.isNullOrEmpty(project.group)||!project.group.contains("进行中")){
                                continue;
                            }
                            //同步项目集中的预计工时字段
                            Task task = dao.getDomain(Task.class,QueryWhere.create().where("associate_project_id",project.id));
                            if(null==task){
                                continue;
                            }
                            //项目集自定义的预计工时字段
                            ProjectFieldDefine.ProjectFieldDefineQuery pfdq = new ProjectFieldDefine.ProjectFieldDefineQuery();
                            pfdq.companyId = company.id;
                            pfdq.projectId = task.projectId;
                            pfdq.objectType = Task.OBJECTTYPE_项目清单;
                            pfdq.isSystemField = false;
                            pfdq.pageSize  = Integer.MAX_VALUE;
                            List<ProjectFieldDefine.ProjectFieldDefineInfo> defineList = dao.getList(pfdq);
                            if(BizUtil.isNullOrEmpty(defineList)){
                                continue;
                            }
                            Optional<ProjectFieldDefine.ProjectFieldDefineInfo> optional=defineList.stream().filter(k-> "预计工时".equals(k.name)||"预估工时".equals(k.name)).findFirst();
                            if(!optional.isPresent()){
                                continue;
                            }
                            String key = optional.get().field;
                            if(BizUtil.isNullOrEmpty(task.customFields)){
                                continue;
                            }
                            Object ewt= task.customFields.get(key);
                            if(BizUtil.isNullOrEmpty(ewt)){
                                continue;
                            }
                            double expectWorkTime =0;
                            try {
                                 expectWorkTime= Double.parseDouble(ewt.toString());
                            }catch (Exception e){
                                logger.error("parse double failure");
                            }
                            if(expectWorkTime<=0){
                                continue;
                            }
                            project.expectWorkTime = (int) expectWorkTime;
                            int cdaydiff = DateUtil.getDayDiff(project.startDate, new Date());
                            if (cdaydiff <= 0) {
                                continue;
                            }
                            int projectDayDiff = DateUtil.getDayDiff(project.startDate, project.endDate);
                            if (projectDayDiff <= 0) {
                                continue;
                            }
                            double planCostWorkTime = BigDecimal.valueOf(project.expectWorkTime)
                                    .divide(BigDecimal.valueOf(projectDayDiff), 4, BigDecimal.ROUND_HALF_UP)
                                    .multiply(BigDecimal.valueOf(cdaydiff)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            double reallyCostWorkTime = dao.getProjectCostWorkTime(project.id);
                            reallyCostWorkTime = reallyCostWorkTime/8;
                            double realSpi = BigDecimal.valueOf(reallyCostWorkTime).divide(BigDecimal.valueOf(planCostWorkTime), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            logger.warn("spi real time:{},plan time:{}",reallyCostWorkTime,planCostWorkTime);
                            ProjectRunLog runLog = new ProjectRunLog();
                            runLog.createAccountId =1;
                            runLog.projectId = project.id;
                            runLog.companyId = project.companyId;
                            runLog.progress = (int) (realSpi * 100);
                            runLog.progress = Math.min(100, runLog.progress);
                            if (realSpi >= spiMax) {
                                runLog.runStatus = 1;
                                runLog.remark = "进度正常 spi="+realSpi;
                            }else if(realSpi<spiMax&&realSpi>=spiMin){
                                runLog.runStatus = 2;
                                runLog.remark = "存在风险 spi="+realSpi;
                            }else if(realSpi<spiMin){
                                runLog.runStatus = 3;
                                runLog.remark = "进度失控 spi="+realSpi;
                            }

                            project.runStatus = runLog.runStatus;
                            project.progress = runLog.progress;
                            project.runLogRemark = runLog.remark;
                            dao.updateSpecialFields(project,"expectWorkTime","runStatus","progress","runLogRemark");

                            dao.add(runLog);
                        }
                    }
                }
            }

        }

        @Override
        public void retryScmCommit() {
            boolean con = true;
            while (con) {
                ScmRetry retry = webApiService.getScmRetry();
                if (null != retry) {
                    con = retry(retry);
                } else {
                    con = false;
                }
            }

        }

        private boolean retry(ScmRetry scmRetry) {
            List<String> items = scmRetry.items;
            String version = items.get(5);
            String diff = items.get(3);
            logger.warn("scm commit retry over  times:{},version:{}", scmRetry.times, version);
            Integer commitLogId = dao.getScmCommitLogIdByVersionAndCompanyId(scmRetry.companyId, version);
            if (null != commitLogId && commitLogId > 0) {
                //更新代码统计行数
                if (!StringUtil.isEmpty(diff)) {//1 file changed, 3 insertions(+), 1 deletion(-)
                    int addLineNum = getGitAddLineNum(diff);
                    int decreaseLineNum = getGitDeleteLineNum(diff);
                    dao.updateGitlabScmCommitLogNum(commitLogId, addLineNum, decreaseLineNum);
                }
                return true;
            } else {
                scmRetry.times += 1;
                if (scmRetry.times > 5) {
                    logger.warn("scm commit retry over 5 times,version:{}", version);
                    return true;
                }
                webApiService.toQueue(scmRetry);
            }
            return false;
        }

        private int getGitAddLineNum(String diff) {
            if (StringUtil.isEmpty(diff)) {
                return 0;
            }
            String[] contents = diff.split(",");
            if (contents.length < 2) {
                return 0;
            }
            if (contents[1] == null) {
                return 0;
            }
            contents = contents[1].trim().split(" ");
            if (contents.length < 1) {
                return 0;
            }
            return Integer.parseInt(contents[0].trim());
        }

        private int getGitDeleteLineNum(String diff) {
            if (StringUtil.isEmpty(diff)) {
                return 0;
            }
            String[] contents = diff.split(",");
            if (contents.length < 3) {
                return 0;
            }
            if (contents[2] == null) {
                return 0;
            }
            contents = contents[2].trim().split(" ");
            if (contents.length < 1) {
                return 0;
            }
            return Integer.parseInt(contents[0].trim());
        }

    }
}
