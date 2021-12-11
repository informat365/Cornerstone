package cornerstone.biz.action;

import cornerstone.biz.CornerstoneBizSystem;
import cornerstone.biz.action.CommAction.CommActionImpl;
import cornerstone.biz.annotations.ApiDefine;
import cornerstone.biz.dao.BizDAO;
import cornerstone.biz.dao.ProjectAlterationDAO;
import cornerstone.biz.domain.*;
import cornerstone.biz.domain.TaskAlteration.TaskAlterationInfo;
import cornerstone.biz.domain.TaskAlterationDefine.TaskAlterationDefineInfo;
import cornerstone.biz.srv.BizService;
import cornerstone.biz.util.BeanUtil;
import cornerstone.biz.util.BizUtil;
import cornerstone.biz.util.DateUtil;
import cornerstone.biz.websocket.WebEvent;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;
import jazmin.driver.jdbc.ConnectionDriver;
import jazmin.driver.jdbc.Transaction;
import jazmin.driver.jdbc.TransactionSynchronizationAdapter;
import jazmin.driver.jdbc.smartjdbc.Query;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.rpc.RpcService;
import jazmin.util.DumpUtil;
import jazmin.util.JSONUtil;

import java.util.*;
import java.util.stream.Collectors;

@ApiDefine("任务对象变更接口")
public interface TaskAlterationAction {

    /**
     * 创建变更
     */
    int addTaskAlteration(String token, TaskAlterationInfo bean);

    /**
     * 修改变更
     */
    void updateTaskAlteration(String token, TaskAlterationInfo bean);

    /**
     * 删除变更申请
     */
    void deleteTaskAlteration(String token, int id);

    /**
     * 审批变更
     */
    void auditTaskAlteration(String token, TaskAlterationLog bean);

    /**
     * 取消变更
     */
    void cancelTaskAlteration(String token, TaskAlterationInfo bean);

    /**
     * 查询变更详情
     */
    TaskAlterationInfo getTaskAlterationById(String token, int id);

    /**
     * 查询变更列表
     */
    List<TaskAlterationInfo> getTaskAlterationList(String token, TaskAlteration.TaskAlterationQuery query);

    /**
     * 创建变更流程
     */
    int addTaskAlterationDefine(String token, TaskAlterationDefineInfo bean);

    /**
     * 删除变更流程
     */
    void deleteTaskAlterationDefine(String token, int id);

    /**
     * 编辑变更流程
     */
    void updateTaskAlterationDefine(String token, TaskAlterationDefineInfo bean);

    /**
     * 查询变更流程详情
     */
    TaskAlterationDefineInfo getTaskAlterationDefineById(String token, int id);

    /**
     * 查询变更流程列表
     */
    List<TaskAlterationDefineInfo> getTaskAlterationDefineList(String token, TaskAlterationDefine.TaskAlterationDefineQuery query);

    /**
     * 查询变更日志详情
     */
    TaskAlterationLog.TaskAlterationLogInfo getTaskAlterationLogById(String token, int id);

    /**
     * 查询变更日志列表
     */
    List<TaskAlterationLog.TaskAlterationLogInfo> getTaskAlterationLogList(String token, TaskAlterationLog.TaskAlterationLogQuery query);


    /**
     * 保存变更流程
     */
    void saveTaskAlterationDefineList(String token, List<TaskAlterationDefine> defines);

    @RpcService
    class TaskAlterationActionImpl extends CommActionImpl implements TaskAlterationAction {

        private Logger logger = LoggerFactory.getLogger(TaskAlterationActionImpl.class);

        @AutoWired
        private ProjectAlterationDAO projectAlterationDAO;

        @AutoWired
        private BizDAO bizDAO;

        @AutoWired
        private BizService bizService;

        @Transaction
        @Override
        public int addTaskAlteration(String token, TaskAlterationInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            if (bean.taskId == 0) {
                throw new AppException("变更对象信息缺失");
            }
            if (BizUtil.isNullOrEmpty(bean.reason)) {
                throw new AppException("请提交变更原因");
            }
            List<TaskAlterationField> fields = bean.fields;
            if (BizUtil.isNullOrEmpty(fields)) {
                throw new AppException("请提交需要变更的字段");
            }
            Task.TaskInfo task = dao.getExistedById(Task.TaskInfo.class, bean.taskId);

            bizService.checkPermissionForProjectAccess(account, task.projectId);
            if (account.companyId != task.companyId) {
                throw new AppException("无操作权限");
            }
            bean.companyId = task.companyId;
            bean.projectId = task.projectId;
            bean.objectType = task.objectType;
            if (null == task.ownerAccountIdList || !task.ownerAccountIdList.contains(account.id)) {
                throw new AppException("您不是该任务的负责人，无法发起变更申请");
            }
            if (task.isFreeze) {
                throw new AppException("该任务已被提交变更锁定，本次无法提交变更申请");
            }
            if (task.isDelete) {
                throw new AppException("任务不存在");
            }
            if (task.isFinish) {
                throw new AppException("任务已完成，无法提交变更申请");
            }
            List<Map<String, Object>> editableFieldList = bizService.getTaskFieldPermissionList(account, task);
            Map<String, Boolean> em = new HashMap<>();
            if (!BizUtil.isNullOrEmpty(editableFieldList)) {
                editableFieldList.forEach(item -> em.put((String) item.get("field"), (boolean) item.get("editable")));
            }
            for (TaskAlterationField f : fields) {
                String field = f.field;
                field = convertAlterationField(field);
                if (null == em.get(field) || em.get(field)) {
                    String msg = "您拥有字段%s的修改权限，无需变更";
                    String fieldName = "";
                    if ("startDate".equals(field) || "endDate".equals(field)) {
                        fieldName = "开始截止时间";
                    } else if ("finishTime".equals(field)) {
                        fieldName = "完成时间";
                    } else if ("categoryIdList".equals(field)) {
                        fieldName = "分类";
                    } else if ("statusName".equals(field)) {
                        fieldName = "状态";
                    } else if ("ownerAccountName".equals(field)) {
                        fieldName = "责任人";
                    }
                    throw new AppException(String.format(msg, fieldName));
                }
            }

            TaskAlterationDefine.TaskAlterationDefineQuery query = new TaskAlterationDefine.TaskAlterationDefineQuery();
            setupQuery(account, query);
            query.projectId = bean.projectId;
            query.objectType = task.objectType;
            List<TaskAlterationDefineInfo> defines = dao.getList(query);
            if (BizUtil.isNullOrEmpty(defines)) {
                throw new AppException("变更流程未定义，无法提交变更申请");
            }
            Optional<TaskAlterationDefineInfo> optional = defines.stream().filter(k -> k.type == ProjectStatusDefine.TYPE_开始状态).findFirst();
            if (optional.isPresent()) {
                TaskAlterationDefineInfo define = optional.get();
                bean.flowStatus = define.id;
                //设置负责人信息
                setAlterationOwnerList(define, bean);
            } else {
                throw new AppException("变更流程开始状态未定义，无法发起变更申请");
            }

            //入库
            bean.status = TaskAlteration.STATUS_待定;
            bean.isFinish = false;
            bean.createAccountId = account.id;
            bean.updateAccountId = account.id;
            bean.task = task;
            dao.add(bean);
            //冻结任务
            task.isFreeze = true;
            task.alterationId = bean.id;
            dao.updateSpecialFields(task, "isFreeze", "alterationId");

            //日志
            TaskAlterationLog log = new TaskAlterationLog();
            log.companyId = bean.companyId;
            log.taskId = bean.taskId;
            log.projectId = bean.projectId;
            log.alterationId = bean.id;
            log.status = bean.status;
            log.isFinish = false;
            log.remark = "提交变更申请";
            log.flowStatus = bean.flowStatus;
            log.createAccountId = account.id;
            log.updateAccountId = account.id;
            dao.add(log);

            Map<String, Object> content = new HashMap<>();
            content.put("content", "您有新的变更申请待审批，请及时处理");
            content.put("name", "您有新的变更申请待审批，请及时处理");
            if(!BizUtil.isNullOrEmpty(bean.ownerAccountList)){
                    bizService.sendNotificationForAlteration(account,bean.ownerAccountList,
                            task,
                            AccountNotificationSetting.TYPE_对象变更审批,
                            "变更申请待审批",
                            content);

            }

            return bean.id;
        }

        private String convertAlterationField(String field) {
            if ("iterationId".equals(field)) {
                field = "iterationName";
            }
            if ("releaseId".equals(field)) {
                field = "releaseName";
            }
            if ("subSystemId".equals(field)) {
                field = "subSystemName";
            }
            if ("ownerAccountIdList".equals(field)) {
                field = "ownerAccountName";
            }
            if ("stageId".equals(field)) {
                field = "stageName";
            }
            if ("priority".equals(field)) {
                field = "priorityName";
            }
            if ("status".equals(field)) {
                field = "statusName";
            }

            return field;
        }

        @Transaction
        @Override
        public void updateTaskAlteration(String token, TaskAlterationInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            TaskAlteration old = dao.getExistedById(TaskAlteration.class, bean.id);
            if (account.id != old.createAccountId) {
                throw new AppException("您不是变更申请人，无法进行修改");
            }
            if (BizUtil.isNullOrEmpty(bean.reason)) {
                throw new AppException("变更原因不能为空");
            }
            old.reason = bean.reason;
            dao.updateSpecialFields(old, "reason");

            //日志
            TaskAlterationLog log = new TaskAlterationLog();
            log.companyId = bean.companyId;
            log.taskId = bean.taskId;
            log.projectId = bean.projectId;
            log.alterationId = bean.id;
            log.status = old.status;
            log.isFinish = false;
            log.remark = "修改变更原因";
            log.flowStatus = old.flowStatus;
            log.createAccountId = account.id;
            log.updateAccountId = account.id;
            dao.add(log);
        }


        @Override
        public void deleteTaskAlteration(String token, int id) {
            //无删除操作
        }


        /**
         * 设置当前变更负责人
         */
        private void setAlterationOwnerList(TaskAlterationDefineInfo define, TaskAlteration bean) {
            bean.lastOwner = bean.ownerIdList;
            //检查字段是否设置
            logger.info("setAlterationOwnerList:{},statusDefine:{}", bean.id, DumpUtil.dump(define));
            // owner creater lastOwner firstOwner emptyOwner role_ member_
            if (!BizUtil.isNullOrEmpty(define.setOwnerList)) {
                Set<Integer> newOwnerIdList = new LinkedHashSet<>();
                for (String newOwner : define.setOwnerList) {
                    if ("creater".equals(newOwner)) {
                        newOwnerIdList.add(bean.createAccountId);
                    } else if ("owner".equals(newOwner)) {
                        if (bean.ownerIdList != null) {
                            newOwnerIdList.addAll(bean.ownerIdList);
                        }
                        //最初的责任人
                    } else if ("firstOwner".equals(newOwner)) {
                        if (!BizUtil.isNullOrEmpty(bean.firstOwner)) {
                            newOwnerIdList.addAll(bean.firstOwner);
                        }
                        //上一个状态责任人
                    } else if ("lastOwner".equals(newOwner)) {
                        if (!BizUtil.isNullOrEmpty(bean.lastOwner)) {
                            newOwnerIdList.addAll(bean.lastOwner);
                        }
                    } else if (newOwner.startsWith("role_")) {// t_role
                        int roleId = Integer.parseInt(newOwner.substring(newOwner.lastIndexOf("_") + 1));
                        List<Integer> idList = bizDAO.getAccountIdListByProjectIdRoleId(bean.projectId, roleId);
                        Set<Integer> accountIds = BizUtil.convert(idList);
                        newOwnerIdList.addAll(accountIds);
                    } else if (newOwner.startsWith("member_")) {// t_account
                        int accountId = Integer.parseInt(newOwner.substring(newOwner.lastIndexOf("_") + 1));
                        newOwnerIdList.add(accountId);
                    }
                }
                logger.info("setAlterationOwnerList:newOwnerIdList:{}", DumpUtil.dump(newOwnerIdList));

                if (!BizUtil.isNullOrEmpty(newOwnerIdList)) {
                    bean.ownerIdList = BizUtil.convert(newOwnerIdList);
                    bean.ownerAccountList = bizService.getAccountSimpleInfoList(BizUtil.convertList(bean.ownerIdList));
                    if (define.type == TaskAlterationDefine.TYPE_开始状态) {
                        bean.firstOwner = BizUtil.convert(newOwnerIdList);
                    }
                } else {
                    throw new AppException("无法找到变更负责人");
                }
            }
        }

        /**
         * 审批变更
         */
        @Transaction
        @Override
        public void auditTaskAlteration(String token, TaskAlterationLog bean) {
            Account account = bizService.getExistedAccountByToken(token);

            TaskAlteration old = dao.getExistedById(TaskAlteration.class, bean.alterationId);
            if (null == old.ownerIdList || !old.ownerIdList.contains(account.id)) {
                throw new AppException("您无权限审批此变更");
            }
            if (bean.flowStatus <= 0 || bean.flowStatus == old.flowStatus) {
                return;
            }
            Task task = dao.getExistedById(Task.class, old.taskId);
            if (!task.isFreeze) {
                throw new AppException("任务状态异常，无法审批变更申请");
            }
            List<TaskAlterationDefineInfo> defineInfos = projectAlterationDAO.getProjectAlterationDefineList(old.projectId, task.objectType);
            if (BizUtil.isNullOrEmpty(defineInfos)) {
                throw new AppException("变更流程未定义");
            }
            Map<Integer, TaskAlterationDefineInfo> defineMap = defineInfos.stream().collect(Collectors.toMap(k -> k.id, v -> v));

            TaskAlterationDefineInfo oldDefine = defineMap.get(old.flowStatus);
            if (null == oldDefine) {
                throw new AppException("变更流程定义缺失，审批失败");
            }
            TaskAlterationDefineInfo newDefine = defineMap.get(bean.flowStatus);
            if (!oldDefine.transferTo.contains(bean.flowStatus)) {
                throw new AppException("状态异常，不能从【" + oldDefine.name + "】流转到【" + newDefine.name + "】");
            }
            if (newDefine.type == TaskAlterationDefine.TYPE_失败结束) {
                old.isFinish = true;
                old.status = TaskAlteration.STATUS_驳回;
            }
            if (newDefine.type == TaskAlterationDefine.TYPE_成功结束) {
                old.isFinish = true;
                old.status = TaskAlteration.STATUS_通过;
            }

            old.flowStatus = bean.flowStatus;
            //设置当前负责人
            setAlterationOwnerList(newDefine, old);

            //
            dao.updateSpecialFields(old, "flowStatus", "isFinish", "status", "lastOwner", "ownerIdList", "ownerAccountList", "firstOwner");

            if (old.isFinish) {
                task.isFreeze = false;
                dao.updateSpecialFields(task, "isFreeze");

                if (old.status == TaskAlteration.STATUS_通过) {
                    Task.TaskDetailInfo taskDetail = BeanUtil.copyTo(Task.TaskDetailInfo.class, task);
                    //变更字段
                    List<TaskAlterationField> fields = old.fields;
                    List<String> updateFields = new ArrayList<>();
                    for (TaskAlterationField f : fields) {
                        String field = f.field;
                        int type = f.type;
                        updateFields.add(field);
                        if (null == taskDetail.customFields) {
                            taskDetail.customFields = new HashMap<>();
                        }
                        boolean isCustomField = field.startsWith("field_");
                        Object value = f.newValue;
                        if (isCustomField) {
                            taskDetail.customFields.put(field, value);
                        } else {
                            if (type == ProjectFieldDefine.TYPE_单行文本框 || type == ProjectFieldDefine.TYPE_单选框) {
                                BizUtil.setFieldValue(taskDetail, field, f.newValue);
                            }
                            if (type == ProjectFieldDefine.TYPE_日期) {
                                if (!BizUtil.isNullOrEmpty(value)) {
                                    value = new Date(Long.parseLong(String.valueOf(f.newValue)));
                                }
                                BizUtil.setFieldValue(taskDetail, field, value);
                            }
                            if (type == ProjectFieldDefine.TYPE_数值) {
                                BizUtil.setFieldValue(taskDetail, field, value);
                            }
                            if (type == ProjectFieldDefine.TYPE_复选框) {
                                if (!BizUtil.isNullOrEmpty(value)) {
                                    value = JSONUtil.fromJsonList(JSONUtil.toJson(f.newValue), String.class);
                                }
                                BizUtil.setFieldValue(taskDetail, field, value);
                            }
                            if (type == ProjectFieldDefine.TYPE_团队成员选择) {
                                if (!BizUtil.isNullOrEmpty(value)) {
                                    value = JSONUtil.fromJsonList(f.newValue.toString(), int.class);
                                }
                                BizUtil.setFieldValue(taskDetail, field, value);
                            }
                        }
                    }

//                    throw new AppException("xxxx");
                    bizService.doUpdateTask(account, taskDetail, updateFields, false, true, true);
                }
                Map<String, Object> content = new HashMap<>();
                content.put("content", "您的变更申请已完成，请查看");
                content.put("name", "您的变更申请已完成，请查看");
                if(old.createAccountId>0){
                  List<AccountSimpleInfo> accountSimpleInfos=  bizService.getAccountSimpleInfoList(new int[]{old.createAccountId});
                    bizService.sendNotificationForAlteration(account,accountSimpleInfos,
                            BeanUtil.copyTo(Task.TaskInfo.class,task),
                            AccountNotificationSetting.TYPE_对象变更申请结果,
                            "变更申请结果通知",
                            content);
                }

            }else{
                Map<String, Object> content = new HashMap<>();
                content.put("content", "您有新的变更申请待审批，请及时处理");
                content.put("name", "您有新的变更申请待审批，请及时处理");
                if(!BizUtil.isNullOrEmpty(old.ownerAccountList)){
                    bizService.sendNotificationForAlteration(account,old.ownerAccountList,
                            BeanUtil.copyTo(Task.TaskInfo.class,task),
                            AccountNotificationSetting.TYPE_对象变更审批,
                            "变更申请待审批",
                            content);

                }
            }

            TaskAlterationLog log = new TaskAlterationLog();
            log.companyId = old.companyId;
            log.projectId = old.projectId;
            log.taskId = old.taskId;
            log.alterationId = old.id;
            log.status = bean.status;
            log.flowStatus = old.flowStatus;
            log.isFinish = old.isFinish;
            log.remark = BizUtil.isNullOrEmpty(bean.remark) ? "审批" : bean.remark;
            log.createAccountId = account.id;
            log.updateAccountId = account.id;
            dao.add(log);
        }

        @Transaction
        @Override
        public void cancelTaskAlteration(String token, TaskAlterationInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            TaskAlterationInfo old = dao.getExistedById(TaskAlterationInfo.class, bean.id);
            if (account.id != old.createAccountId) {
                throw new AppException("您不是该变更的发起人，无法撤销");
            }
            if (old.status == TaskAlteration.STATUS_取消) {
                throw new AppException("该变更已撤销");
            }
            if (old.status != TaskAlteration.STATUS_待定) {
                throw new AppException("该变更已经进入审批流程，无法撤销");
            }
            old.isFinish = true;
            old.status = TaskAlteration.STATUS_取消;
            old.flowStatus = 0;
            dao.updateSpecialFields(old, "status", "flowStatus", "isFinish");

            Task task = dao.getExistedById(Task.class, old.taskId);
            task.isFreeze = false;
            dao.updateSpecialFields(task, "isFreeze");

            TaskAlterationLog log = new TaskAlterationLog();
            log.companyId = old.companyId;
            log.projectId = old.projectId;
            log.taskId = old.taskId;
            log.alterationId = old.id;
            log.status = old.status;
            log.flowStatus = old.flowStatus;
            log.isFinish = old.isFinish;
            log.remark = "发起人撤销变更";
            log.createAccountId = account.id;
            log.updateAccountId = account.id;
            dao.add(log);

        }

        @Override
        public TaskAlterationInfo getTaskAlterationById(String token, int id) {
            TaskAlterationInfo info = dao.getExistedById(TaskAlterationInfo.class, id);
            TaskAlterationLog.TaskAlterationLogQuery query = new TaskAlterationLog.TaskAlterationLogQuery();
            query.alterationId = id;
            query.pageSize = Integer.MAX_VALUE;
            info.logList = dao.getList(query);
            return info;
        }

        @Override
        public List<TaskAlterationInfo> getTaskAlterationList(String
                                                                      token, TaskAlteration.TaskAlterationQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            setupQuery(account, query);

            return dao.getList(query);
        }

        @Transaction
        @Override
        public int addTaskAlterationDefine(String token, TaskAlterationDefineInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            if (bean.type == TaskAlterationDefine.TYPE_开始状态) {
                int count = projectAlterationDAO.getProjectAlterationDefineCount(bean.projectId, bean.objectType, bean.type);
                if (count > 0) {
                    throw new AppException("开始状态只能有一个");
                }
            }
            if (bean.type == TaskAlterationDefine.TYPE_失败结束) {
                int count = projectAlterationDAO.getProjectAlterationDefineCount(bean.projectId, bean.objectType, bean.type);
                if (count > 0) {
                    throw new AppException("变更失败结束状态只能有一个");
                }
            }
            if (bean.type == TaskAlterationDefine.TYPE_成功结束) {
                int count = projectAlterationDAO.getProjectAlterationDefineCount(bean.projectId, bean.objectType, bean.type);
                if (count > 0) {
                    throw new AppException("变更成功结束状态只能有一个");
                }
            }
            if (bean.transferTo == null) {
                bean.transferTo = new ArrayList<>();
            }
            if (!bean.transferTo.isEmpty()) {
                for (int to : bean.transferTo) {
                    TaskAlterationDefine toBean = projectAlterationDAO.getExistedById(TaskAlterationDefine.class, to);
                    if (toBean.projectId != bean.projectId || toBean.objectType != bean.objectType) {
                        throw new AppException("转移状态不存在");
                    }
                }
            }
            checkNull(bean, "对象");
            BizUtil.checkUniqueKeysOnAdd(projectAlterationDAO, bean, "名称为【" + bean.name + "】的变更状态已经存在");
            BizUtil.checkValid(bean);
            bean.createAccountId = account.id;
            bean.companyId = account.companyId;
            projectAlterationDAO.add(bean);
            return bean.id;
        }

        @Transaction
        @Override
        public void deleteTaskAlterationDefine(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            TaskAlterationDefine define = dao.getExistedById(TaskAlterationDefine.class, id);
            TaskAlteration.TaskAlterationQuery query = new TaskAlteration.TaskAlterationQuery();
            setupQuery(account, query);
            query.flowStatus = id;
            int count = dao.getListCount(query);
            if (count > 0) {
                throw new AppException("有" + count + "个变更处于该状态,无法删除");
            }
            dao.deleteById(TaskAlterationDefine.class, id);

        }

        @Transaction
        @Override
        public void updateTaskAlterationDefine(String token, TaskAlterationDefineInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            if (bean.transferTo == null) {
                bean.transferTo = new ArrayList<>();
            }
            if (!bean.transferTo.isEmpty()) {
                for (int to : bean.transferTo) {
                    if (to == bean.id) {
                        throw new AppException("转移状态不能是自己");
                    }
                    TaskAlterationDefine toBean = dao.getExistedById(TaskAlterationDefine.class, to);
                    if (toBean.projectId != bean.projectId || toBean.objectType != bean.objectType) {
                        throw new AppException("转移状态不存在");
                    }
                }
            }
            checkNull(bean, "对象");
            TaskAlterationDefine old = dao.getExistedByIdForUpdate(TaskAlterationDefine.class, bean.id);
            BizUtil.checkUniqueKeysOnUpdate(dao, bean, old, "名称为【" + bean.name + "】的状态已经存在");
            if (old.type != bean.type && bean.type == TaskAlterationDefine.TYPE_开始状态) {
                int count = projectAlterationDAO.getProjectAlterationDefineCount(bean.projectId, bean.objectType, bean.type);
                if (count > 0) {
                    throw new AppException("开始状态只能有一个");
                }
            }
            if (old.type != bean.type && bean.type == TaskAlterationDefine.TYPE_失败结束) {
                int count = projectAlterationDAO.getProjectAlterationDefineCount(bean.projectId, bean.objectType, bean.type);
                if (count > 0) {
                    throw new AppException("变更失败结束状态只能有一个");
                }
            }
            if (old.type != bean.type && bean.type == TaskAlterationDefine.TYPE_成功结束) {
                int count = projectAlterationDAO.getProjectAlterationDefineCount(bean.projectId, bean.objectType, bean.type);
                if (count > 0) {
                    throw new AppException("变更成功结束状态只能有一个");
                }
            }
            old.name = bean.name;
            old.type = bean.type;
            old.transferTo = bean.transferTo;
            old.color = bean.color;
            old.remark = bean.remark;
            old.setOwnerList = bean.setOwnerList;
            old.permissionOwnerList = bean.permissionOwnerList;
            old.updateAccountId = account.id;
            BizUtil.checkValid(old);
            dao.update(old);

        }

        @Override
        public TaskAlterationDefineInfo getTaskAlterationDefineById(String token, int id) {
            return dao.getExistedById(TaskAlterationDefineInfo.class, id);
        }

        @Override
        public List<TaskAlterationDefineInfo> getTaskAlterationDefineList(String
                                                                                  token, TaskAlterationDefine.TaskAlterationDefineQuery query) {
            if (null == query.projectId) {
                return Collections.emptyList();
            }
            Account account = bizService.getExistedAccountByToken(token);
            setupQuery(account, query);
            query.createTimeSort = Query.SORT_TYPE_ASC;
            return dao.getList(query);
        }

        @Override
        public TaskAlterationLog.TaskAlterationLogInfo getTaskAlterationLogById(String token, int id) {
            return dao.getExistedById(TaskAlterationLog.TaskAlterationLogInfo.class, id);
        }

        @Override
        public List<TaskAlterationLog.TaskAlterationLogInfo> getTaskAlterationLogList(String
                                                                                              token, TaskAlterationLog.TaskAlterationLogQuery query) {
            if (null == query.alterationId) {
                return Collections.emptyList();
            }
            Account account = bizService.getExistedAccountByToken(token);
            setupQuery(account, query);
            query.pageSize = Integer.MAX_VALUE;
            return dao.getList(query);
        }

        @Override
        public void saveTaskAlterationDefineList(String token, List<TaskAlterationDefine> defines) {
            Account account = bizService.getExistedAccountByToken(token);
            for (TaskAlterationDefine e : defines) {
                TaskAlterationDefine old = dao.getExistedByIdForUpdate(TaskAlterationDefine.class, e.id);
                old.transferTo = e.transferTo;
                dao.updateSpecialFields(old, "transferTo");
            }
        }


    }
}
