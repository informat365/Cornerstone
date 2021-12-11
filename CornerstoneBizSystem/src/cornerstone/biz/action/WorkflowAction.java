package cornerstone.biz.action;

import cornerstone.biz.action.CommAction.CommActionImpl;
import cornerstone.biz.annotations.ApiDefine;
import cornerstone.biz.dao.BizDAO;
import cornerstone.biz.domain.*;
import cornerstone.biz.domain.WorkflowChartDefine.*;
import cornerstone.biz.domain.WorkflowDefine.WorkflowDefineInfo;
import cornerstone.biz.domain.WorkflowDefine.WorkflowDefineQuery;
import cornerstone.biz.domain.WorkflowDefinePermission.WorkflowDefinePermissionInfo;
import cornerstone.biz.domain.WorkflowFormDefine.FormField;
import cornerstone.biz.domain.WorkflowFormDefine.FormFieldObjectValue;
import cornerstone.biz.domain.WorkflowFormDefine.WorkflowFormDefineInfo;
import cornerstone.biz.domain.WorkflowFormDefine.WorkflowFormDefineQuery;
import cornerstone.biz.domain.WorkflowInstance.WorkflowInstanceInfo;
import cornerstone.biz.domain.WorkflowInstance.WorkflowInstanceQuery;
import cornerstone.biz.domain.WorkflowInstanceChangeLog.WorkflowInstanceChangeLogQuery;
import cornerstone.biz.domain.WorkflowInstanceNodeChange.WorkflowInstanceNodeChangeInfo;
import cornerstone.biz.domain.WorkflowInstanceNodeChange.WorkflowInstanceNodeChangeQuery;
import cornerstone.biz.domain.WorkflowInstanceOwner.WorkflowInstanceOwnerQuery;
import cornerstone.biz.domain.WorkflowInstanceOwner.WorkflowInstanceOwnerSimpleInfo;
import cornerstone.biz.poi.TableData;
import cornerstone.biz.srv.EmailService;
import cornerstone.biz.srv.WorkflowService;
import cornerstone.biz.util.*;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;
import jazmin.driver.jdbc.Transaction;
import jazmin.driver.jdbc.smartjdbc.Query;
import jazmin.driver.jdbc.smartjdbc.QueryWhere;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.rpc.RpcService;
import jazmin.util.JSONUtil;

import java.util.*;

/**
 * 流程Action
 */
@ApiDefine("流程接口")
public interface WorkflowAction {

    @ApiDefine(value = "通过ID查询流程模板", resp = "流程模板信息", params = {"登录TOKEN", "流程模板ID"})
    WorkflowDefineInfo getWorkflowDefineById(String token, int id);

    @ApiDefine(value = "通过ID查询流程模板权限", resp = "流程模板权限", params = {"登录TOKEN", "流程模板ID"})
    WorkflowDefinePermissionInfo getWorkflowDefinePermissionById(String token, int workflowDefineId);

    @ApiDefine(value = "查询流程模板列表和总数", resp = "流程模板信息", params = {"登录TOKEN", "流程模板查询类"})
    Map<String, Object> getWorkflowDefineList(String token, WorkflowDefineQuery query);

    @ApiDefine(value = "新增流程模板", resp = "流程模板ID", params = {"登录TOKEN", "流程模板"})
    int addWorkflowDefine(String token, WorkflowDefineInfo bean);

    @ApiDefine(value = "编辑流程模板", params = {"登录TOKEN", "流程模板ID"})
    void updateWorkflowDefine(String token, WorkflowDefineInfo bean);

    @ApiDefine(value = "启用流程模板", params = {"登录TOKEN", "流程模板ID"})
    void enableWorkflowDefine(String token, int workflowDefineId);

    @ApiDefine(value = "禁用流程模板", params = {"登录TOKEN", "流程模板ID"})
    void disableWorkflowDefine(String token, int workflowDefineId);

    @ApiDefine(value = "编辑流程模板权限", params = {"登录TOKEN", "流程模板权限信息"})
    void updateWorkflowDefinePermission(String token, WorkflowDefinePermissionInfo bean);

    @ApiDefine(value = "删除流程模板", params = {"登录TOKEN", "流程模板ID"})
    void deleteWorkflowDefine(String token, int id);

    @ApiDefine(value = "通过ID查询表单定义", resp = "流程表单定义信息", params = {"登录TOKEN", "流程模板ID"})
    WorkflowFormDefineInfo getWorkflowFormDefineById(String token, int workflowDefineId);

    @ApiDefine(value = "查询表单定义列表和总数", resp = "流程表单定义信息", params = {"登录TOKEN", "流程表单查询类"})
    Map<String, Object> getWorkflowFormDefineList(String token, WorkflowFormDefineQuery query);

    @ApiDefine(value = "编辑表单定义", params = {"登录TOKEN", "流程表单信息"})
    void updateWorkflowFormDefine(String token, WorkflowFormDefineInfo bean);

    @ApiDefine(value = "通过ID查询流程流转", resp = "流程流转信息", params = {"登录TOKEN", "流程模板ID"})
    WorkflowChartDefineInfo getWorkflowChartDefineById(String token, int id);

    @ApiDefine(value = "查询流程流转列表和总数", resp = "流程流转信息", params = {"登录TOKEN", "流程流转查询类"})
    Map<String, Object> getWorkflowChartDefineList(String token, WorkflowChartDefineQuery query);

    @ApiDefine(value = "新增流程流转", resp = "流程流转ID", params = {"登录TOKEN", "流程模板ID"})
    int addWorkflowChartDefine(String token, int workflowDefineId);

    @ApiDefine(value = "编辑流程流转", params = {"登录TOKEN", "流程流转信息"})
    void updateWorkflowChartDefine(String token, WorkflowChartDefineInfo bean);

    @ApiDefine(value = "删除流程流转", params = {"登录TOKEN", "流程流转ID"})
    void deleteWorkflowChartDefine(String token, int id);

    @ApiDefine(value = "设置流程流转定义", params = {"登录TOKEN", "流程模板ID", "流程流转ID"})
    void setWorkflowChartDefine(String token, int workflowDefineId, int workflowChartDefineId);

    @ApiDefine(value = "查询我作为责任人的流程实例列表和总数", resp = "流程实例信息", params = {"登录TOKEN", "流程实例查询类"})
    Map<String, Object> getOwnerWorkflowInstanceList(String token, WorkflowInstanceQuery query);

    @ApiDefine(value = "查询我创建的实例列表和总数", resp = "流程实例信息", params = {"登录TOKEN", "流程实例查询类"})
    Map<String, Object> getMyCreateWorkflowInstanceList(String token, WorkflowInstanceQuery query);

    @ApiDefine(value = "查询我参与的实例列表和总数(当前阶段抄送给我的或责任人是我的)", resp = "流程实例信息", params = {"登录TOKEN", "流程实例查询类"})
    Map<String, Object> getMyJoinWorkflowInstanceList(String token, WorkflowInstanceQuery query);

    @ApiDefine(value = "查询我能看到的流程模板列表", resp = "流程模板列表", params = {"登录TOKEN"})
    List<WorkflowDefine> getMyWorkflowDefineList(String token);

    @ApiDefine(value = "查询有数据权限的流程模板列表", resp = "流程模板列表", params = {"登录TOKEN"})
    List<WorkflowDefine> getMyDataWorkflowDefineList(String token);

    @ApiDefine(value = "查询数据的实例列表和总数", resp = "流程实例信息", params = {"登录TOKEN", "流程实例查询类"})
    Map<String, Object> getDataWorkflowInstanceList(String token, WorkflowInstanceQuery query);

    @ApiDefine(value = "管理员查询流程实例列表和总数", resp = "流程实例信息", params = {"登录TOKEN", "流程实例查询类"})
    Map<String, Object> getWorkflowInstanceListByAdmin(String token, WorkflowInstanceQuery query);

    @ApiDefine(value = "发起流程", resp = "流程实例UUID", params = {"登录TOKEN", "流程模板ID"})
    String applyWorkflow(String token, int workflowDefineId);

    @ApiDefine(value = "删除流程", params = {"登录TOKEN", "流程实例UUID"})
    void deleteWorkflowInstance(String token, String uuid);

    @ApiDefine(value = "提交表单", params = {"登录TOKEN", "流程表单信息"})
    void saveWorkflow(String token, WorkflowSubmitInfo submitInfo);

    @ApiDefine(value = "查看流程数据", resp = "流程数据信息", params = {"登录TOKEN", "流程实例UUID"})
    Map<String, Object> getWorkflowInstanceData(String token, String uuid);

    @ApiDefine(value = "查询流程实例", resp = "流程数据信息", params = {"登录TOKEN", "流程实例UUID", "是否为管理员"})
    Map<String, Object> getWorkflowInstanceInfo(String token, String uuid, boolean isAdmin);

    @ApiDefine(value = "撤回流程实例", params = {"登录TOKEN", "流程实例UUID", "备注信息"})
    void cancelWorkflowInstance(String token, String uuid, String remark);

    @ApiDefine(value = "回退流程实例", params = {"登录TOKEN", "流程实例UUID", "节点ID"})
    void backToWorkflowInstance(String token, String uuid, String nodeId);

    @ApiDefine(value = "终止流程实例", params = {"登录TOKEN", "流程实例UUID", "备注信息"})
    void terminalWorkflowInstance(String token, String uuid, String remark);

    @ApiDefine(value = "转交流程实例", params = {"登录TOKEN", "流程实例UUID", "人员ID集合"})
    void transferWorkflowInstance(String token, String uuid, List<Integer> accountIdList);

    @ApiDefine(value = "查询流程实例变更记录列表和总数", resp = "流程实例变更记录信息", params = {"登录TOKEN", "流程变更查询类"})
    Map<String, Object> getWorkflowInstanceChangeLogList(String token, WorkflowInstanceChangeLogQuery query);

    @ApiDefine(value = "评论", params = {"登录TOKEN", "流程实例UUID", "评论内容"})
    void commentWorkflowInstance(String token, int workflowInstanceId, String message);

    @ApiDefine(value = "删除评论", params = {"登录TOKEN", "评论Id"})
    void deleteWorkflowInstanceComment(String token, int id);

    @ApiDefine(value = "导出到excel", resp = "导出数据", params = {"登录TOKEN", "流程实例查询类"})
    ExportData exportWorkflowInstancesToExcel(String token, WorkflowInstanceQuery query);

    @ApiDefine(value = "唤醒进入下一个节点", params = {"流程实例UUID", "内容【未启用】"})
    void notifyWorkflowInstance(String workflowInstanceUuid, String body);

    //
    @RpcService
    class WorkflowActionImpl extends CommActionImpl implements WorkflowAction {
        //
        @AutoWired
        BizDAO dao;
        @AutoWired
        WorkflowService workflowService;
        @AutoWired
        EmailService emailService;
        //
        private static Logger logger = LoggerFactory.get(WorkflowActionImpl.class);
        //
        //

        /**
         * 通过ID查询流程模板
         */
        @Override
        public WorkflowDefineInfo getWorkflowDefineById(String token, int id) {
            Account account = workflowService.getExistedAccountByToken(token);
            return dao.getById(WorkflowDefineInfo.class, id);
        }

        /**
         * 通过ID查询流程模板权限
         */
        @Override
        public WorkflowDefinePermissionInfo getWorkflowDefinePermissionById(String token, int workflowDefineId) {
            Account account = workflowService.getExistedAccountByToken(token);
            return dao.getWorkflowDefinePermissionByWorkflowDefineId(workflowDefineId);
        }

        /**
         * 查询流程模板列表和总数
         */
        @Override
        public Map<String, Object> getWorkflowDefineList(String token, WorkflowDefineQuery query) {
            Account account = workflowService.getExistedAccountByToken(token);
            setupQuery(account, query);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        /**
         * 新增流程模板
         */
        @Transaction
        @Override
        public int addWorkflowDefine(String token, WorkflowDefineInfo bean) {
            Account account = workflowService.getExistedAccountByToken(token);
            bean.createAccountId = account.id;
            bean.status = WorkflowDefineInfo.STATUS_无效;
            bean.companyId = account.companyId;
            WorkflowDefine copy = null;
            workflowService.checkCompanyPermission(account, Permission.ID_管理流程模板);
            if (bean.copyId > 0) {
                copy = dao.getExistedById(WorkflowDefine.class, bean.copyId);
                if (copy.status != WorkflowDefine.STATUS_有效) {
                    throw new AppException("复制流程模板不存在");
                }
                if (copy.companyId != account.companyId) {
                    throw new AppException("复制流程模板不存在");
                }
                bean.color = copy.color;
                bean.enableCancel = copy.enableCancel;
                bean.enablePrint = copy.enablePrint;
                bean.enableNotifyWechat = copy.enableNotifyWechat;
                bean.enableNotifyEmail = copy.enableNotifyEmail;
                bean.titleFormFieldList = copy.titleFormFieldList;
            }
            if (bean.titleFormFieldList == null) {
                bean.titleFormFieldList = new ArrayList<>();
            }
            if (StringUtil.isEmpty(bean.color)) {
                bean.color = "#333333";//默认颜色
            }
            checkValid(bean);
            dao.add(bean);
            //
            WorkflowChartDefine chartDefine = null;
            WorkflowFormDefine formDefine = null;
            WorkflowDefinePermission permission = null;
            if (copy != null) {
                chartDefine = dao.getExistedById(WorkflowChartDefine.class, copy.chartDefineId);
                formDefine = dao.getWorkflowFormDefineInfoByWorkflowDefineId(copy.id);
                permission = dao.getWorkflowDefinePermissionByWorkflowDefineId(copy.id);
            } else {
                chartDefine = new WorkflowChartDefine();
                formDefine = new WorkflowFormDefine();
                permission = new WorkflowDefinePermission();
            }
            chartDefine.companyId = account.companyId;
            chartDefine.workflowDefineId = bean.id;
            chartDefine.name = "V1";
            chartDefine.version = 1;
            chartDefine.createAccountId = account.id;
            dao.add(chartDefine);
            //
            formDefine.title = bean.name;
            formDefine.workflowDefineId = bean.id;
            formDefine.companyId = account.companyId;
            dao.add(formDefine);
            //
            permission.workflowDefineId = bean.id;
            permission.companyId = bean.companyId;
            dao.add(permission);
            //
            bean.formDefineId = formDefine.id;
            bean.chartDefineId = chartDefine.id;
            dao.update(bean);
            //
            bizService.addOptLog(account, bean.id, bean.name, OptLog.EVENT_ID_新增流程模板, "name:" + bean.name);
            //
            return bean.id;
        }

        /**
         * 编辑流程模板
         */
        @Transaction
        @Override
        public void updateWorkflowDefine(String token, WorkflowDefineInfo bean) {
            Account account = workflowService.getExistedAccountByToken(token);
            WorkflowDefine old = dao.getExistedByIdForUpdate(WorkflowDefine.class, bean.id);
            workflowService.checkCompanyPermission(account, Permission.ID_管理流程模板, old.companyId);
            bizService.addOptLog(account, bean.id, bean.name, OptLog.EVENT_ID_编辑流程模板,
                    old, bean, "name", "remark", "group", "color", "enablePrint", "enableCancel", "enableNotifyWechat",
                    "enableNotifyEmail");
            old.name = bean.name;
            old.remark = bean.remark;
            old.group = bean.group;
            old.color = bean.color;
            old.enablePrint = bean.enablePrint;
            old.enableCancel = bean.enableCancel;
            old.enableNotifyWechat = bean.enableNotifyWechat;
            old.enableNotifyEmail = bean.enableNotifyEmail;
            old.titleFormFieldList = bean.titleFormFieldList;
            old.updateAccountId = account.id;
            checkValid(old);
            dao.update(old);
        }

        @Transaction
        @Override
        public void enableWorkflowDefine(String token, int workflowDefineId) {
            Account account = workflowService.getExistedAccountByToken(token);
            WorkflowDefine old = dao.getExistedByIdForUpdate(WorkflowDefine.class, workflowDefineId);
            workflowService.checkCompanyPermission(account, Permission.ID_管理流程模板, old.companyId);
            if (old.status == WorkflowDefine.STATUS_有效) {
                return;
            }
            old.status = WorkflowDefine.STATUS_有效;
            old.updateAccountId = account.id;
            dao.update(old);
            bizService.addOptLog(account, old.id, old.name, OptLog.EVENT_ID_启用流程模板, "name:" + old.name);
        }

        @Transaction
        @Override
        public void disableWorkflowDefine(String token, int workflowDefineId) {
            Account account = workflowService.getExistedAccountByToken(token);
            WorkflowDefine old = dao.getExistedByIdForUpdate(WorkflowDefine.class, workflowDefineId);
            if (old.status == WorkflowDefine.STATUS_无效) {
                return;
            }
            workflowService.checkCompanyPermission(account, Permission.ID_管理流程模板, old.companyId);
            old.status = WorkflowDefine.STATUS_无效;
            old.updateAccountId = account.id;
            dao.update(old);
            bizService.addOptLog(account, old.id, old.name, OptLog.EVENT_ID_禁用流程模板, "name:" + old.name);
        }

        @Transaction
        @Override
        public void updateWorkflowDefinePermission(String token, WorkflowDefinePermissionInfo bean) {
            Account account = workflowService.getExistedAccountByToken(token);
            WorkflowDefinePermission old = dao.getExistedByIdForUpdate(WorkflowDefinePermission.class, bean.id);
            workflowService.checkCompanyPermission(account, Permission.ID_管理流程模板, old.companyId);
            old.accountList = bean.accountList;
            old.companyRoleList = bean.companyRoleList;
            old.projectRoleList = bean.projectRoleList;
            old.departmentList = bean.departmentList;
            old.dataAccountList = bean.dataAccountList;
            old.dataCompanyRoleList = bean.dataCompanyRoleList;
            old.dataProjectRoleList = bean.dataProjectRoleList;
            old.dataDepartmentList = bean.dataDepartmentList;
            dao.update(old);
        }

        /**
         * 删除流程模板
         */
        @Transaction
        @Override
        public void deleteWorkflowDefine(String token, int id) {
            Account account = workflowService.getExistedAccountByToken(token);
            WorkflowDefine old = dao.getExistedByIdForUpdate(WorkflowDefine.class, id);
            workflowService.checkCompanyPermission(account, Permission.ID_管理流程模板, old.companyId);
            //
            WorkflowInstanceQuery query = new WorkflowInstanceQuery();
            query.workflowDefineId = old.id;
            int instanceCount = dao.getListCount(query);
            if (instanceCount > 0) {
                throw new AppException("已有" + instanceCount + "个流程实例关联此模板，不能删除");
            }
            dao.deleteById(WorkflowDefine.class, id);
            dao.delete(WorkflowDefinePermission.class, QueryWhere.create().where("workflow_define_id", id));
            dao.delete(WorkflowFormDefine.class, QueryWhere.create().where("workflow_define_id", id));
            dao.delete(WorkflowChartDefine.class, QueryWhere.create().where("workflow_define_id", id));
            //
            bizService.addOptLog(account, old.id, old.name, OptLog.EVENT_ID_删除流程模板, "name:" + old.name);
        }

        //

        /**
         * 通过ID查询表单定义
         */
        @Override
        public WorkflowFormDefineInfo getWorkflowFormDefineById(String token, int workflowDefineId) {
            Account account = workflowService.getExistedAccountByToken(token);
            return dao.getWorkflowFormDefineInfoByWorkflowDefineId(workflowDefineId);
        }

        /**
         * 查询表单定义列表和总数
         */
        @Override
        public Map<String, Object> getWorkflowFormDefineList(String token, WorkflowFormDefineQuery query) {
            Account account = workflowService.getExistedAccountByToken(token);
            setupQuery(account, query);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        /**
         * 编辑表单定义
         */
        @Transaction
        @Override
        public void updateWorkflowFormDefine(String token, WorkflowFormDefineInfo bean) {
            Account account = workflowService.getExistedAccountByToken(token);
            WorkflowFormDefine old = dao.getExistedByIdForUpdate(WorkflowFormDefine.class, bean.id);
            workflowService.checkCompanyPermission(account, Permission.ID_管理流程模板, old.companyId);
            old.title = bean.title;
            old.subTitle = bean.subTitle;
            old.fieldList = bean.fieldList;
            old.updateAccountId = account.id;
            checkValid(old);
            dao.update(old);
            //
            List<FormField> formFields = JSONUtil.fromJsonList(old.fieldList, FormField.class);
            WorkflowDefine define = dao.getExistedById(WorkflowDefine.class, old.workflowDefineId);
            List<WorkflowChartDefine> charts = workflowService.getAllWorkflowChartDefines(old.workflowDefineId);
            checkWorkflowDefine(define, formFields);
            for (WorkflowChartDefine e : charts) {
                checkWorkflowChartDefines(e, formFields);
            }
            //
            bizService.addOptLog(account, old.id, old.title, OptLog.EVENT_ID_编辑流程表单设计, "name:" + old.title);
        }

        private void checkWorkflowDefine(WorkflowDefine define, List<FormField> formFields) {
            if (define.titleFormFieldList != null) {
                for (String titleFormFieldId : define.titleFormFieldList) {
                    if (!BizUtil.contains(formFields, "id", titleFormFieldId)) {
                        throw new AppException("数据标题设置的表单值不能删除");
                    }
                }
            }
        }

        private void checkWorkflowChartDefines(WorkflowChartDefine chart, List<FormField> formFields) {
            logger.info("checkWorkflowChartDefines chart:{}", chart.id);
            Graph graph = JSONUtil.fromJson(chart.graph, Graph.class);
            Map<String, GraphProperty> graphProperty = workflowService.parseProperty(chart.props);
            if (graphProperty == null) {
                graphProperty = new HashMap<>();
            }
            if (graph == null || graph.nodes == null) {
                return;
            }
            for (GraphNode node : graph.nodes) {
                if (!node.type.equals(GraphNode.TYPE_NODE_EVENT)) {
                    continue;
                }
                GraphProperty property = graphProperty.get(node.id);
                if (property == null) {
                    continue;
                }
                List<NodeFormField> nodeFields = property.fieldList;
                //判断是否有新增
                for (FormField e : formFields) {
                    if (!BizUtil.contains(nodeFields, "id", e.id)) {
                        NodeFormField nodeFormField = new NodeFormField();
                        nodeFormField.id = e.id;
                        nodeFormField.name = e.name;
                        nodeFormField.editable = false;
                        nodeFormField.visible = false;
                        nodeFields.add(nodeFormField);
                    }
                }
                //删除
                Iterator<NodeFormField> iter = nodeFields.iterator();
                while (iter.hasNext()) {
                    NodeFormField e = iter.next();
                    if (!BizUtil.contains(formFields, "id", e.id)) {
                        iter.remove();
                    }
                }
            }
            //
            chart.props = JSONUtil.toJson(graphProperty);
            dao.update(chart);
            //检查被删除字段是否被引用
            for (GraphNode node : graph.nodes) {
                if (!node.type.equals(GraphNode.TYPE_NODE_EVENT)) {
                    continue;
                }
                GraphProperty property = graphProperty.get(node.id);
                if (property == null) {
                    continue;
                }
                if (property.owner != null && property.owner.formItemList != null) {
                    for (String ownerFormId : property.owner.formItemList) {
                        if (!BizUtil.contains(formFields, "id", ownerFormId)) {
                            throw new AppException("【" + node.name + "】负责人设置的表单值不能删除");
                        }
                    }
                }
                if (property.cc != null && property.cc.formItemList != null) {
                    for (String ownerFormId : property.cc.formItemList) {
                        if (!BizUtil.contains(formFields, "id", ownerFormId)) {
                            throw new AppException("【" + node.name + "】抄送人设置的表单值不能删除");
                        }
                    }
                }
            }
            //
            for (GraphLink link : graph.links) {
                GraphProperty property = graphProperty.get(link.id);
                if (property != null && property.ruleList != null) {
                    for (GraphRule rule : property.ruleList) {
                        if (!BizUtil.contains(formFields, "id", rule.field)) {
                            throw new AppException("【" + link.name + "】规则设置的表单值不能删除");
                        }
                    }
                }
            }
        }

        //

        /**
         * 通过ID查询流程流转
         */
        @Override
        public WorkflowChartDefineInfo getWorkflowChartDefineById(String token, int id) {
            Account account = workflowService.getExistedAccountByToken(token);
            return dao.getById(WorkflowChartDefineInfo.class, id);
        }

        /**
         * 查询流程流转列表和总数
         */
        @Override
        public Map<String, Object> getWorkflowChartDefineList(String token, WorkflowChartDefineQuery query) {
            Account account = workflowService.getExistedAccountByToken(token);
            setupQuery(account, query);
            return createResult(dao.getList(query, "graph", "props"), dao.getListCount(query));
        }

        /**
         * 新增流程流转
         */
        @Transaction
        @Override
        public int addWorkflowChartDefine(String token, int workflowDefineId) {
            Account account = workflowService.getExistedAccountByToken(token);
            workflowService.checkCompanyPermission(account, Permission.ID_管理流程模板);
            int maxVersion = dao.getMaxVersionWorkflowChartDefineId(workflowDefineId);
            WorkflowDefine define = dao.getExistedById(WorkflowDefine.class, workflowDefineId);
            WorkflowChartDefine using = dao.getById(WorkflowChartDefine.class, define.chartDefineId);
            //复制使用中的流程
            WorkflowChartDefine bean = new WorkflowChartDefine();
            bean.companyId = define.companyId;
            bean.workflowDefineId = workflowDefineId;
            bean.version = maxVersion + 1;
            bean.name = "V" + bean.version;
            bean.createAccountId = account.id;
            if (using != null) {
                bean.graph = using.graph;
                bean.props = using.props;
            }
            dao.add(bean);
            checkValid(bean);
            //
            bizService.addOptLog(account, bean.id, bean.name, OptLog.EVENT_ID_新增流程, "name:" + bean.name);
            return bean.id;
        }

        /**
         * 编辑流程流转
         */
        @Transaction
        @Override
        public void updateWorkflowChartDefine(String token, WorkflowChartDefineInfo bean) {
            Account account = workflowService.getExistedAccountByToken(token);
            WorkflowChartDefine old = dao.getExistedByIdForUpdate(WorkflowChartDefine.class, bean.id);
            workflowService.checkCompanyPermission(account, Permission.ID_管理流程模板, old.companyId);
            old.graph = bean.graph;
            old.props = bean.props;
            old.updateAccountId = account.id;
            checkValid(old);
            workflowService.checkWorkflowChartDefineValid(old);
            dao.update(old);
            //
            bizService.addOptLog(account, old.id, old.name, OptLog.EVENT_ID_编辑流程, "name:" + old.name);
        }

        /**
         * 删除流程
         */
        @Transaction
        @Override
        public void deleteWorkflowChartDefine(String token, int id) {
            Account account = workflowService.getExistedAccountByToken(token);
            WorkflowChartDefine old = dao.getExistedByIdForUpdate(WorkflowChartDefine.class, id);
            workflowService.checkCompanyPermission(account, Permission.ID_管理流程模板, old.companyId);
            WorkflowDefine define = dao.getWorkflowDefineByWorkflowChartDefineId(old.id);
            if (define != null) {
                throw new AppException("已有流程模板在使用，不能删除");
            }
            if (old.createAccountId != account.id) {
                throw new AppException("删除失败，只有自己的流程可以删除");
            }
            dao.deleteById(WorkflowChartDefine.class, id);
            //
            bizService.addOptLog(account, old.id, old.name, OptLog.EVENT_ID_删除流程, "name:" + old.name);
        }

        @Transaction
        @Override
        public void setWorkflowChartDefine(String token, int workflowDefineId, int workflowChartDefineId) {
            Account account = workflowService.getExistedAccountByToken(token);
            WorkflowDefine old = dao.getExistedByIdForUpdate(WorkflowDefine.class, workflowDefineId);
            workflowService.checkCompanyPermission(account, Permission.ID_管理流程模板, old.companyId);
            WorkflowChartDefine workflowChartDefine = dao.getExistedByIdForUpdate(WorkflowChartDefine.class, workflowChartDefineId);
            old.chartDefineId = workflowChartDefine.id;
            old.updateAccountId = account.id;
            dao.update(old);

        }

        /**
         * 查询我申请的流程实例列表和总数
         */
        @Override
        public Map<String, Object> getMyCreateWorkflowInstanceList(String token, WorkflowInstanceQuery query) {
            Account account = workflowService.getExistedAccountByToken(token);
            setupQuery(account, query);
            query.createAccountId = account.id;
            query.status = WorkflowInstance.STATUS_正式;
            return createResult(dao.getList(query, "formData", "joinAccountIdList"), dao.getListCount(query));
        }

        /**
         * 查询责任人是我的流程实例列表和总数
         */
        @Override
        public Map<String, Object> getOwnerWorkflowInstanceList(String token, WorkflowInstanceQuery query) {
            Account account = workflowService.getExistedAccountByToken(token);
            setupQuery(account, query);
            query.status = WorkflowInstance.STATUS_正式;
            query.ownerAccountId = account.id;
            query.ownerStatus = WorkflowInstanceOwner.STATUS_待处理;
            query.ownerType = WorkflowInstanceOwner.TYPE_责任人;
            return createResult(dao.getList(query, "formData", "joinAccountIdList"), dao.getListCount(query));
        }

        /**
         * 查询我参与的流程实例列表和总数
         */
        @Override
        public Map<String, Object> getMyJoinWorkflowInstanceList(String token, WorkflowInstanceQuery query) {
            Account account = workflowService.getExistedAccountByToken(token);
            setupQuery(account, query);
            query.status = WorkflowInstance.STATUS_正式;
            query.joinAccountId = account.id;
            return createResult(dao.getList(query, "formData", "joinAccountIdList"), dao.getListCount(query));
        }

        @Transaction
        @Override
        public List<WorkflowDefine> getMyWorkflowDefineList(String token) {
            WorkflowDefineQuery query = new WorkflowDefineQuery();
            query.pageSize = Integer.MAX_VALUE;
            query.status = WorkflowDefine.STATUS_有效;
            Account account = workflowService.getExistedAccountByToken(token);
            setupQuery(account, query);
            List<WorkflowDefine> list = dao.getList(query);
            List<WorkflowDefine> ret = new ArrayList<>();
            for (WorkflowDefine e : list) {
                if (havePermission(account.id, account.companyId, e.id)) {
                    ret.add(e);
                }
            }
            return ret;
        }

        @Transaction
        @Override
        public List<WorkflowDefine> getMyDataWorkflowDefineList(String token) {
            WorkflowDefineQuery query = new WorkflowDefineQuery();
            query.pageSize = Integer.MAX_VALUE;
            query.status = WorkflowDefine.STATUS_有效;
            Account account = workflowService.getExistedAccountByToken(token);
            setupQuery(account, query);
            List<WorkflowDefine> list = dao.getList(query);
            List<WorkflowDefine> ret = new ArrayList<>();
            for (WorkflowDefine e : list) {
                if (haveDataPermission(account.id, account.companyId, e.id)) {
                    ret.add(e);
                }
            }
            return ret;
        }

        /**
         * 是否拥有发起权限
         *
         * @param accountId
         * @param companyId
         * @param workflowDefineid
         * @return
         */
        private boolean havePermission(int accountId, int companyId, int workflowDefineid) {
            WorkflowDefinePermission permission = dao.getWorkflowDefinePermissionByWorkflowDefineId(workflowDefineid);
            return havePermission(accountId, companyId, permission.accountList, permission.companyRoleList,
                    permission.projectRoleList, permission.departmentList);
        }

        /**
         * 是否拥有数据权限
         *
         * @param accountId
         * @param companyId
         * @param workflowDefineId
         * @return
         */
        private boolean haveDataPermission(int accountId, int companyId, int workflowDefineId) {
            WorkflowDefinePermission permission = dao.getWorkflowDefinePermissionByWorkflowDefineId(workflowDefineId);
            return havePermission(accountId, companyId, permission.dataAccountList, permission.dataCompanyRoleList,
                    permission.dataProjectRoleList, permission.dataDepartmentList);
        }

        private boolean havePermission(int accountId, int companyId, List<GraphUser> userList,
                                       List<GraphCompanyRole> companyRoleList, List<GraphProjectRole> projectRoleList,
                                       List<GraphDepartment> departmentList) {
            Set<Integer> ownerId = workflowService.getAccountIds(companyId, null,
                    userList, companyRoleList, projectRoleList, departmentList, null, null);
            if (ownerId.contains(accountId)) {
                return true;
            }
            return false;
        }

        /**
         * 流程数据
         */
        @Transaction
        @Override
        public Map<String, Object> getDataWorkflowInstanceList(String token, WorkflowInstanceQuery query) {
            Account account = workflowService.getExistedAccountByToken(token);
            if (query.workflowDefineId == null) {
                throw new AppException("请选择流程模板");
            }
            if (!haveDataPermission(account.id, account.companyId, query.workflowDefineId)) {
                throw new AppException("权限不足");
            }
            query.status = WorkflowInstance.STATUS_正式;
            setupQuery(account, query);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        @Transaction
        @Override
        public Map<String, Object> getWorkflowInstanceListByAdmin(String token, WorkflowInstanceQuery query) {
            Account account = workflowService.getExistedAccountByToken(token);
            setupQuery(account, query);
            query.status = WorkflowInstance.STATUS_正式;
            workflowService.checkCompanyPermission(account, Permission.ID_管理流程数据);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        /**
         * 新增流程实例
         */
        @Transaction
        @Override
        public String applyWorkflow(String token, int workflowDefineId) {
            Account account = workflowService.getExistedAccountByToken(token);
            workflowService.checkCompanyPermission(account, Permission.ID_开启流程模块);
            WorkflowDefine define = workflowService.getValidWorkflowDefine(workflowDefineId);
            WorkflowInstance instance = new WorkflowInstance();
            instance.uuid = BizUtil.randomUUID();
            instance.workflowDefineId = define.id;
            instance.serialNo = workflowService.generateSerialNo(account.companyId, SerialNoGenerator.TYPE_流程实例序列号) + "";
            instance.workflowChartDefineId = define.chartDefineId;
            instance.workflowFormDefineId = define.formDefineId;
            instance.createAccountId = account.id;
            instance.companyId = account.companyId;
            instance.title = define.name + "-" + account.name;
            instance.status = WorkflowInstance.STATUS_草稿;
            checkValid(instance);
            dao.add(instance);
            //
            WorkflowChartDefine chart = dao.getExistedById(WorkflowChartDefine.class, instance.workflowChartDefineId);
            GraphTree tree = workflowService.parse(chart);
            instance.currNode = tree.startNode.id;
            instance.currNodeName = tree.startNode.name;
            //
            workflowService.addWorkflowInstanceChangeLog(instance,
                    WorkflowInstanceChangeLog.TYPE_发起, account.id, null, null, null, null);
            //
            doNextNode(account, define, instance, tree, null);
            //
            return instance.uuid;
        }

        @Transaction
        @Override
        public void deleteWorkflowInstance(String token, String uuid) {
            Account account = workflowService.getExistedAccountByToken(token);
            WorkflowInstance old = dao.getExistedByUuid(WorkflowInstance.class, uuid);
            workflowService.checkCompanyPermission(account, Permission.ID_管理流程数据, old.companyId);
            workflowService.deleteWorkflowInstance(old.id);
            bizService.addOptLog(account, old.id, old.title, OptLog.EVENT_ID_删除流程实例, "name:" + old.title);
        }

        private void nextNode(Account account, WorkflowDefine define, WorkflowInstance instance, String remark) {
            WorkflowChartDefine chart = dao.getExistedById(WorkflowChartDefine.class, instance.workflowChartDefineId);
            GraphTree tree = workflowService.parse(chart);
            doNextNode(account, define, instance, tree, remark);
        }

        private void beforeNextNode(int accountId, WorkflowInstance instance) {
            //离开当前节点
            leaveCurrNode(accountId, instance);
            //
            instance.currAccountList = new ArrayList<>();
            workflowService.setWorkflowInstanceOwnerStatusOtherDone(instance);
        }

        //获取下一个节点
        private void doNextNode(Account account, WorkflowDefine define, WorkflowInstance instance,
                                GraphTree tree, String remark) {
            //进入下一个节点
            GraphNodeInfo currNode = workflowService.getNextNode(instance, tree);
            if (currNode == null) {
                throw new AppException("没有可用节点");
            }
            enterNode(account, define, instance, tree, currNode, instance.formData);
        }

        /**
         * 进入节点
         *
         * @param account
         * @param instance
         * @param tree
         * @param formData
         */
        private void enterNode(Account account, WorkflowDefine define, WorkflowInstance instance,
                               GraphTree tree, GraphNodeInfo newNode, String formData) {
            //
            List<WorkflowInstanceOwnerSimpleInfo> beforeOwnerList = instance.currAccountList;
            //进入节点前
            beforeNextNode(account.id, instance);
            //删除以前的责任人记录
            dao.deleteWorkflowInstanceOwner(instance.id, newNode.id);
            dao.deleteWorkflowInstanceCc(instance.id, newNode.id);
            //
            instance.beforeNodeId = instance.currNode;
            instance.beforeNodeName = instance.currNodeName;
            instance.currNode = newNode.id;
            instance.currNodeName = newNode.name;
            //
            List<WorkflowInstanceOwnerSimpleInfo> afterCcList = null;
            //
            if (newNode.type.equals(GraphNode.TYPE_NODE_END)) {//结束节点
                onFinishWorkflowInstance(instance, WorkflowInstance.FINISH_TYPE_正常结束, null);
            } else if (newNode.type.equals(GraphNode.TYPE_NODE_AUTO)) {//自动节点
                enterAutoNode(instance, newNode);
            } else if (newNode.type.equals(GraphNode.TYPE_NODE_EVENT)) {//
                Set<Integer> ownerList = workflowService.getOwner(instance, newNode);
                if (ownerList.isEmpty()) {
                    throw new AppException("【" + newNode.name + "】没有设置责任人");
                }
                for (Integer ownerAccountId : ownerList) {
                    workflowService.addWorkflowInstanceOwner(account.id, instance,
                            ownerAccountId, WorkflowInstanceOwner.TYPE_责任人);
                }
                instance.currAccountList = workflowService.getOwnerAccounts(instance, WorkflowInstanceOwner.TYPE_责任人);
                //抄送
                Set<Integer> ccAccountIdList = workflowService.getCc(instance, newNode);
                for (Integer ccAccountId : ccAccountIdList) {
                    workflowService.addWorkflowInstanceOwner(account.id, instance,
                            ccAccountId, WorkflowInstanceOwner.TYPE_抄送人);
                }
                afterCcList = workflowService.getOwnerAccounts(instance, WorkflowInstanceOwner.TYPE_抄送人);
                //新增参加过的人
                if (instance.joinAccountIdList == null) {
                    instance.joinAccountIdList = new HashSet<>();
                }
                for (WorkflowInstanceOwnerSimpleInfo e : instance.currAccountList) {
                    instance.joinAccountIdList.add(e.ownerAccountId);
                }
                instance.joinAccountIdList.addAll(ccAccountIdList);
            } else {
                throw new AppException("错误的节点【" + newNode + "】类型");
            }
            //新增流转事件
            Map<String, Object> remark = new HashMap<>();
            remark.put("beforeOwnerList", beforeOwnerList);
            remark.put("afterOwnerList", instance.currAccountList);
            workflowService.addWorkflowInstanceChangeLog(instance, WorkflowInstanceChangeLog.TYPE_流转,
                    account.id, JSONUtil.toJson(remark), null, null, null);
            //
            //新增节点变化记录
            WorkflowInstanceNodeChange nodeChange = new WorkflowInstanceNodeChange();
            nodeChange.companyId = instance.companyId;
            nodeChange.workflowInstanceId = instance.id;
            nodeChange.nodeId = instance.currNode;
            nodeChange.nodeName = instance.currNodeName;
            nodeChange.enterTime = new Date();
            if (newNode.type.equals(GraphNode.TYPE_NODE_END)) {//结束节点
                nodeChange.leaveTime = nodeChange.enterTime;
            }
            nodeChange.ownerAccountList = instance.currAccountList;
            nodeChange.ccAccountList = afterCcList;
            nodeChange.formData = instance.formData;
            nodeChange.createAccountId = account.id;
            dao.add(nodeChange);
            //发送通知责任人和抄送人
            if (instance.status == WorkflowInstance.STATUS_正式) {
                Set<Integer> notifyAccountIds = workflowService.getWorkflowInstanceJoinMembers(instance);
                for (Integer notifyAccountId : notifyAccountIds) {
                    WorkflowInstanceNotify notify = new WorkflowInstanceNotify();
                    notify.workflowDefineName = define.name;
                    notify.beforeNodeName = instance.beforeNodeName;
                    notify.currNodeName = instance.currNodeName;
                    notify.title = instance.title;
                    notify.uuid = instance.uuid;
                    notify.serialNo = instance.serialNo;
                    notify.enableNotifyWechat = define.enableNotifyWechat;
                    notify.enableNotifyEmail = define.enableNotifyEmail;
                    workflowService.addAccountNotification(notifyAccountId, AccountNotificationSetting.TYPE_流程提醒,
                            instance.companyId, 0, instance.id, "流程变更提醒", JSONUtil.toJson(notify),
                            new Date(), account);
                }
            }
            //
            dao.update(instance);
            //执行完后判断是否在自动节点
            if (newNode.type.equals(GraphNode.TYPE_NODE_AUTO)) {//自动节点
                if (newNode.nodeProperty.forwardRule.equals(GraphProperty.FORWARD_RULE_NEXT)) {//进入下一个节点
                    doNextNode(account, define, instance, tree, null);
                }
            }
        }

        private void enterAutoNode(WorkflowInstance instance, GraphNodeInfo node) {
            GraphProperty property = node.nodeProperty;
            if (property.action.equals(GraphProperty.ACTION_EMAIL)) {//发邮件
                try {
                    Map<String, Object> formFieldValue = workflowService.getFormFieldValues(instance);
                    EmailSetting setting = property.emailSetting;
                    String subject = getFieldValue(formFieldValue, setting.title, null);
                    String to = getFieldValue(formFieldValue, setting.to, ";");
                    String cc = getFieldValue(formFieldValue, setting.cc, ";");
                    String content = getFieldValue(formFieldValue, setting.content, null);
                    emailService.sendMail(to, cc, subject, content);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
            //
            if (property.action.equals(GraphProperty.ACTION_WEBAPI)) {//call webapi
                WebApiSetting setting = property.webApiSetting;
                String url = setting.url;
                if (!StringUtil.isEmpty(url)) {
                    try {
                        URLUtil.httpGet(url);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }

        private String getFieldValue(Map<String, Object> formFieldValue, List<String> fields, String split) {
            if (fields == null) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            for (String fieldId : fields) {
                Object v = formFieldValue.get(fieldId);
                if (v == null) {
                    continue;
                }
                sb.append(v.toString());
                if (!StringUtil.isEmpty(split)) {
                    sb.append(split);
                }
            }
            if (!StringUtil.isEmpty(split) && sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            return sb.toString();
        }

        /**
         * 流程结束
         */
        private void onFinishWorkflowInstance(WorkflowInstance instance, int finishType, String finishText) {
            instance.isFinished = true;
            instance.finishTime = new Date();
            instance.finishType = finishType;
            instance.finishText = finishText;
            //清空责任人
            instance.currAccountList = new ArrayList<>();
            workflowService.setWorkflowInstanceOwnerStatusOtherDone(instance);
        }

        /**
         * 离开节点
         */
        private void leaveCurrNode(int accountId, WorkflowInstance instance) {
            if (StringUtil.isEmpty(instance.currNode)) {
                return;
            }
            WorkflowInstanceNodeChange nodeChange = dao.getWorkflowInstanceNodeChangeForUpdate(instance.id, instance.currNode);
            if (nodeChange == null) {
                return;
            }
            nodeChange.leaveTime = new Date();
            nodeChange.updateAccountId = accountId;
            dao.update(nodeChange);
        }

        /**
         * 提交表单
         */
        @Transaction
        @Override
        public void saveWorkflow(String token, WorkflowSubmitInfo submitInfo) {
            Account account = workflowService.getExistedAccountByToken(token);
            WorkflowInstance instance = dao.getExistedByIdForUpdate(WorkflowInstance.class, submitInfo.workflowInstanceId);
            workflowService.checkCompanyPermission(account, Permission.ID_开启流程模块, instance.companyId);
            if (instance.isFinished) {
                throw new AppException("流程已经结束");
            }
            if (instance.status != WorkflowInstance.STATUS_正式) {
                instance.status = WorkflowInstance.STATUS_正式;
            }
            WorkflowDefine define = dao.getExistedById(WorkflowDefine.class, instance.workflowDefineId);
            instance.formData = workflowService.mergeFormData(instance.formData, submitInfo.formData);
            instance.updateAccountId = account.id;
            dao.update(instance);
            //检查是否是owner
            WorkflowInstanceOwner owner = dao.getWorkflowInstanceOwnerForUpdate(account.id, instance.id, instance.currNode);
            if (owner == null) {
                throw new AppException("权限不足，您不是当前责任人");
            }
            //
            if (submitInfo.submit) {//提交表单
                //
                WorkflowInstancePermission permission = workflowService.getPermissionForOperation(account, instance);
                if (!permission.enableSubmit) {
                    throw new AppException("提交失败,权限不足");
                }
                //
                checkFormData(define, submitInfo.formData);
                //
                owner.leaveTime = new Date();
                owner.status = WorkflowInstanceOwner.STATUS_已处理;
                owner.formData = submitInfo.formData;
                owner.remark = submitInfo.remark;
                owner.submitButtonText = permission.submitButtonText;
                BizUtil.checkValid(owner);
                dao.update(owner);
                //刷新status状态
                instance.currAccountList = workflowService.getOwnerAccounts(instance, WorkflowInstanceOwner.TYPE_责任人);
                dao.update(instance);
                //
                workflowService.addWorkflowInstanceChangeLog(instance, WorkflowInstanceChangeLog.TYPE_提交表单,
                        account.id, submitInfo.formData, submitInfo.remark,
                        permission.submitButtonText, permission.terminalButtonText);
                //
                GraphNodeInfo currNode = workflowService.getCurrNode(instance);
                boolean next = false;
                if (currNode.nodeProperty.forwardRule.equals(GraphProperty.FORWARD_RULE_ANY)) {
                    next = true;
                }
                if (currNode.nodeProperty.forwardRule.equals(GraphProperty.FORWARD_RULE_ALL)) {//判断都同意了
                    next = workflowService.isAllSubmit(instance);
                }
                if (next) {
                    //下个节点
                    nextNode(account, define, instance, submitInfo.remark);
                }
            }
            //数据标题
            if (define.titleFormFieldList != null && define.titleFormFieldList.size() > 0) {
                StringBuilder title = new StringBuilder();
                Map<String, FormField> formFieldDefineMap = workflowService.getFormFieldDefines(instance);
                Map<String, Object> data = workflowService.getFormFieldValues(instance);
                for (String fieldId : define.titleFormFieldList) {
                    Object value = data.get(fieldId);
                    if (value == null) {
                        continue;
                    }
                    FormField formField = formFieldDefineMap.get(fieldId);
                    if (formField == null) {
                        continue;
                    }
                    String t = formField.type;
                    if (t.equals(FormField.TYPE_TEXT_SINGLE)
                            || t.equals(FormField.TYPE_SYSTEM_VALUE)
                            || t.equals(FormField.TYPE_RADIO)
                            || t.equals(FormField.TYPE_SELECT)) {//直接取值
                        title.append(value.toString()).append("-");
                    } else if (t.equals(FormField.TYPE_USER_SELECT)) {
                        List<GraphUser> users = JSONUtil.fromJsonList(value.toString(), GraphUser.class);
                        if (users != null && users.size() > 0) {
                            for (GraphUser user : users) {
                                title.append(user.name).append("-");
                            }
                        }
                    } else {
                        continue;
                    }
                }
                if (title.length() > 0) {
                    title.deleteCharAt(title.length() - 1);
                    instance.title = title.toString();
                    dao.update(instance);
                }
            }
        }

        private void checkFormData(WorkflowDefine define, String formData) {
            if (StringUtil.isEmpty(formData)) {
                return;
            }
            WorkflowFormDefine form = dao.getExistedById(WorkflowFormDefine.class, define.formDefineId);
            List<FormField> formFields = JSONUtil.fromJsonList(form.fieldList, FormField.class);
            if (formFields == null || formFields.isEmpty()) {
                return;
            }
            Map<String, Object> formDataMap = JSONUtil.fromJson(formData, Map.class);
            for (FormField formField : formFields) {
                Object value = formDataMap.get(formField.id);
//				if(formField.required&&(!formField.type.equals(FormField.TYPE_STATIC_LABEL))) {
//					if(value==null) {
//						throw new AppException(formField.name+"是必选项不能为空");
//					}
//				}
                if (value == null) {
                    continue;
                }
                Integer minLength = formField.minLength;
                Integer maxLength = formField.maxLength;
                if (formField.type.equals(FormField.TYPE_TEXT_SINGLE) ||
                        formField.type.equals(FormField.TYPE_TEXT_AREA)) {
                    if (!(value instanceof String)) {
                        logger.error("value:{}", value);
                        throw new AppException(formField.name + "格式错误");
                    }
                    if (minLength != null && minLength > ((String) value).length()) {
                        throw new AppException(formField.name + "长度太短,至少" + minLength);
                    }
                    if (maxLength != null && maxLength < ((String) value).length()) {
                        throw new AppException(formField.name + "长度太长,至多" + minLength);
                    }
                }
                if (formField.type.equals(FormField.TYPE_TEXT_NUMBER)) {
                    try {
                        Double dValue = Double.valueOf(value.toString());
                        if (minLength != null && minLength > dValue) {
                            throw new AppException(formField.name + "数值太小,最小" + minLength);
                        }
                        if (maxLength != null && maxLength < ((String) value).length()) {
                            throw new AppException(formField.name + "数值太大,最大" + minLength);
                        }
                    } catch (Exception e) {
                        logger.error("value is not number {}", value);
                        logger.error(e.getMessage(), e);
                        throw new AppException(formField.name + "格式错误");
                    }
                }
            }

        }

        /**
         * 查询实例
         *
         * @param token
         * @param uuid
         * @return
         */
        @Override
        public Map<String, Object> getWorkflowInstanceData(String token, String uuid) {
            Account account = workflowService.getExistedAccountByToken(token);
            Map<String, Object> result = new HashMap<>();
            WorkflowInstanceInfo instance = workflowService.getWorkflowInstanceByUuid(uuid);
            if (!haveDataPermission(account.id, account.companyId, instance.workflowDefineId)) {
                throw new AppException("权限不足");
            }
            result.put("instance", instance);
            //
            WorkflowDefineInfo define = dao.getExistedById(WorkflowDefineInfo.class, instance.workflowDefineId);
            result.put("define", define);
            //
            WorkflowFormDefineInfo form = dao.getExistedById(WorkflowFormDefineInfo.class, instance.workflowFormDefineId);
            result.put("form", form);
            //
            List<NodeFormField> formFieldList = workflowService.getVisiableFieldIds(account.id, true, instance);
            result.put("formFieldList", formFieldList);
            //
            return result;
        }

        /**
         * 查看实例
         * isAdmin 如果为true 则放开所有权限
         */
        @Override
        public Map<String, Object> getWorkflowInstanceInfo(String token, String uuid, boolean isAdmin) {
            Account account = workflowService.getExistedAccountByToken(token);
            Map<String, Object> result = new HashMap<>();
            WorkflowInstanceInfo instance = workflowService.getWorkflowInstanceByUuid(uuid);
            result.put("instance", instance);
            Set<Integer> memberIds = instance.joinAccountIdList;
            if (!BizUtil.isNullOrEmpty(memberIds)) {
                List<Account> mentionList = dao.getAccountMentionListByIds(memberIds);
                result.put("mentionList", mentionList);
            }
            //
            WorkflowDefineInfo define = dao.getExistedById(WorkflowDefineInfo.class, instance.workflowDefineId);
            result.put("define", define);
            //
            WorkflowFormDefineInfo form = dao.getExistedById(WorkflowFormDefineInfo.class, instance.workflowFormDefineId);
            result.put("form", form);
            //当前责任人
            WorkflowInstanceOwnerQuery ownerQuery = new WorkflowInstanceOwnerQuery();
            ownerQuery.workflowInstanceId = instance.id;
            ownerQuery.eqNodeId = instance.currNode;
            ownerQuery.pageSize = Integer.MAX_VALUE;
            ownerQuery.type = WorkflowInstanceOwner.TYPE_责任人;
            ownerQuery.sortFields = new String[]{"nodeId", "id"};
            ownerQuery.nodeIdSort = Query.SORT_TYPE_ASC;
            ownerQuery.idSort = Query.SORT_TYPE_ASC;
            List<WorkflowInstanceOwner> ownerList = dao.getList(ownerQuery);
            result.put("ownerList", ownerList);
            //
            //当前抄送人列表
            ownerQuery.type = WorkflowInstanceOwner.TYPE_抄送人;
            List<WorkflowInstanceOwner> ccList = dao.getList(ownerQuery);
            result.put("ccList", ccList);
            //
            WorkflowInstanceNodeChangeQuery nodeQuery = new WorkflowInstanceNodeChangeQuery();
            nodeQuery.workflowInstanceId = instance.id;
            nodeQuery.pageSize = Integer.MAX_VALUE;
            nodeQuery.sortFields = new String[]{"nodeId", "id"};
            nodeQuery.nodeIdSort = Query.SORT_TYPE_ASC;
            nodeQuery.idSort = Query.SORT_TYPE_ASC;
            List<WorkflowInstanceNodeChangeInfo> nodeChangeList = dao.getList(nodeQuery);
            result.put("nodeChangeList", nodeChangeList);
            //权限
            WorkflowInstancePermission permission = workflowService.getPermissionForView(account, instance, isAdmin);
            result.put("permission", permission);
            //
            WorkflowInstanceOwner owner = dao.getWorkflowInstanceOwner(account.id, instance.id, instance.currNode);
            if (owner != null) {
                instance.formData = workflowService.filterFormData(account.id, instance, isAdmin);
            }
            //
            return result;
        }

        /**
         * 撤回流程实例
         */
        @Transaction
        @Override
        public void cancelWorkflowInstance(String token, String uuid, String remark) {
            Account account = workflowService.getExistedAccountByToken(token);
            WorkflowInstance old = workflowService.getWorkflowInstanceByUuidForUpdate(uuid);
            workflowService.checkCompanyPermission(account, Permission.ID_开启流程模块, old.companyId);
            if (old.isFinished) {
                throw new AppException("撤回失败,只有进行中的流程才能撤回");
            }
            if (old.createAccountId != account.id) {
                throw new AppException("撤回失败,只有提交人才能撤回");
            }
            WorkflowInstancePermission permission = workflowService.getPermissionForOperation(account, old);
            if (!permission.enableCancel) {
                throw new AppException("撤回失败,权限不足");
            }
            //
            onFinishWorkflowInstance(old, WorkflowInstance.FINISH_TYPE_撤回, null);
            dao.update(old);
            //
            workflowService.addWorkflowInstanceChangeLog(old,
                    WorkflowInstanceChangeLog.TYPE_撤回,
                    account.id, null, remark,
                    permission.submitButtonText,
                    permission.terminalButtonText);
        }

        /**
         * 回退流程实例
         */
        @Transaction
        @Override
        public void backToWorkflowInstance(String token, String uuid, String nodeId) {
            Account account = workflowService.getExistedAccountByToken(token);
            WorkflowInstance old = workflowService.getWorkflowInstanceByUuidForUpdate(uuid);
            workflowService.checkCompanyPermission(account, Permission.ID_开启流程模块, old.companyId);
            if (old.isFinished) {
                throw new AppException("回退失败,只有进行中的流程才能撤回");
            }
            if (!workflowService.isOwner(old, account.id)) {
                throw new AppException("回退失败,只有当前责任人可以回退流程");
            }
            WorkflowInstancePermission permission = workflowService.getPermissionForOperation(account, old);
            if (!permission.enableBackword) {
                throw new AppException("回退失败,权限不足");
            }
            List<WorkflowInstanceNodeChange> nodes = workflowService.getNodeChangeList(old.id);//id从小到大排序
            WorkflowInstanceNodeChange nodeChange = BizUtil.getByTarget(nodes, "nodeId", nodeId);
            if (nodeChange == null) {
                throw new AppException("回退节点不存在");
            }
            //回退到节点
            GraphNodeInfo node = workflowService.getNodeById(old, nodeId);
            WorkflowDefine define = dao.getExistedById(WorkflowDefine.class, old.workflowDefineId);
            WorkflowChartDefine chart = dao.getExistedById(WorkflowChartDefine.class, old.workflowChartDefineId);
            GraphTree tree = workflowService.parse(chart);
            enterNode(account, define, old, tree, node, null);
        }

        /**
         * 终止流程实例
         */
        @Transaction
        @Override
        public void terminalWorkflowInstance(String token, String uuid, String remark) {
            Account account = workflowService.getExistedAccountByToken(token);
            WorkflowInstance old = workflowService.getWorkflowInstanceByUuidForUpdate(uuid);
            workflowService.checkCompanyPermission(account, Permission.ID_开启流程模块, old.companyId);
            if (old.isFinished) {
                throw new AppException("终止失败,流程已经结束");
            }
            if (!workflowService.isOwner(old, account.id)) {
                throw new AppException("终止失败,只有责任人可以终止流程");
            }
            WorkflowInstancePermission permission = workflowService.getPermissionForOperation(account, old);
            if (!permission.enableTerminal) {
                throw new AppException("终止失败,权限不足");
            }
            onFinishWorkflowInstance(old, WorkflowInstance.FINISH_TYPE_终止, permission.terminalButtonText);
            dao.update(old);
            workflowService.addWorkflowInstanceChangeLog(old, WorkflowInstanceChangeLog.TYPE_终止,
                    account.id, null, remark, permission.submitButtonText, permission.terminalButtonText);
        }

        /**
         * 转交
         */
        @Transaction
        @Override
        public void transferWorkflowInstance(String token, String uuid, List<Integer> accountIdList) {
            Account account = workflowService.getExistedAccountByToken(token);
            WorkflowInstance old = workflowService.getWorkflowInstanceByUuidForUpdate(uuid);
            workflowService.checkCompanyPermission(account, Permission.ID_开启流程模块, old.companyId);
            if (old.isFinished) {
                throw new AppException("转交失败,流程已经结束");
            }
            WorkflowInstancePermission permission = workflowService.getPermissionForOperation(account, old);
            if (!permission.enableTransferTo) {
                throw new AppException("转交失败,权限不足");
            }
            List<WorkflowInstanceOwnerSimpleInfo> beforeOwnerList = old.currAccountList;
            //判断转交人有没有自己,如果没有则删除自己
            if (!BizUtil.contains(accountIdList, account.id)) {
                WorkflowInstanceOwner owner = dao.getWorkflowInstanceOwner(account.id, old.id, old.currNode);
                dao.deleteById(WorkflowInstanceOwner.class, owner.id);
            }
            Set<Integer> ownerAccountIds = BizUtil.convert(accountIdList);
            if (old.joinAccountIdList == null) {
                old.joinAccountIdList = new HashSet<>();
            }
            old.joinAccountIdList.remove((Integer) account.id);
            for (Integer accountId : ownerAccountIds) {
                Account owner = dao.getById(Account.class, accountId);
                if (owner == null || owner.companyId != account.companyId) {
                    throw new AppException("用户不存在");
                }
                old.joinAccountIdList.add(accountId);
                if (BizUtil.contains(old.currAccountList, "ownerAccountId", accountId)) {
                    continue;
                }
                workflowService.addWorkflowInstanceOwner(account.id, old, accountId, WorkflowInstanceOwner.TYPE_责任人);
            }
            //
            old.currAccountList = workflowService.getOwnerAccounts(old, WorkflowInstanceOwner.TYPE_责任人);
            old.updateAccountId = account.id;
            dao.update(old);
            //
            WorkflowInstanceNodeChange nodeChange = dao.getWorkflowInstanceNodeChangeForUpdate(old.id, old.currNode);
            nodeChange.ownerAccountList = old.currAccountList;
            dao.update(nodeChange);
            //
            Map<String, Object> remark = new HashMap<>();
            remark.put("beforeOwnerList", beforeOwnerList);
            remark.put("afterOwnerList", old.currAccountList);
            workflowService.addWorkflowInstanceChangeLog(old, WorkflowInstanceChangeLog.TYPE_转交,
                    account.id, JSONUtil.toJson(remark), null,
                    permission.submitButtonText, permission.terminalButtonText);
        }

        @Override
        public Map<String, Object> getWorkflowInstanceChangeLogList(String token,
                                                                    WorkflowInstanceChangeLogQuery query) {
            query.orderType = WorkflowInstanceChangeLogQuery.ORDER_TYPE_ID_DESC;//强制按照ID降序
            Account account = workflowService.getExistedAccountByToken(token);
            setupQuery(account, query);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        @Transaction
        @Override
        public void commentWorkflowInstance(String token, int workflowInstanceId, String message) {
            Account account = workflowService.getExistedAccountByToken(token);
            WorkflowInstance instance = dao.getExistedById(WorkflowInstance.class, workflowInstanceId);
            workflowService.checkCompanyPermission(account, Permission.ID_开启流程模块, instance.companyId);
            workflowService.addWorkflowInstanceChangeLog(instance, WorkflowInstanceChangeLog.TYPE_评论,
                    account.id, message, null, null, null);

            addAccountNotificationWhenComment(account, instance, message);
        }

        private void addAccountNotificationWhenComment(Account optAccount, WorkflowInstance instance, String message) {
            WorkflowDefine define = dao.getExistedById(WorkflowDefine.class, instance.workflowDefineId);
            List<String> userNames = PatternUtil.matchs(message, "@([^@ ]{0,20})\\)");
            Set<Integer> accountSet = new HashSet<>();
            if (!userNames.isEmpty()) {
                for (String userName : userNames) {
                    userName = userName.substring(userName.indexOf("(") + 1).trim();
                    Account account = dao.getAccountByUserName(userName);
                    if (account.id == optAccount.id) {
                        continue;
                    }
                    accountSet.add(account.id);
                }
            }
            //
            for (Integer accountId : accountSet) {
                WorkflowInstanceNotify notify = new WorkflowInstanceNotify();
                notify.beforeNodeName = instance.beforeNodeName;
                notify.currNodeName = instance.currNodeName;
                notify.title = instance.title;
                notify.uuid = instance.uuid;
                notify.content = message;
                notify.serialNo = instance.serialNo;
                notify.enableNotifyWechat = define.enableNotifyWechat;
                notify.enableNotifyEmail = define.enableNotifyEmail;
                workflowService.addAccountNotification(accountId, AccountNotificationSetting.TYPE_流程沟通,
                        instance.companyId, 0, instance.id, "在流程@了你", JSONUtil.toJson(notify),
                        new Date(), optAccount);
            }
        }

        @Transaction
        @Override
        public void deleteWorkflowInstanceComment(String token, int id) {
            Account account = workflowService.getExistedAccountByToken(token);
            WorkflowInstanceChangeLog changeLog = dao.getExistedById(WorkflowInstanceChangeLog.class, id);
            workflowService.checkCompanyPermission(account, Permission.ID_开启流程模块, changeLog.companyId);
            if (changeLog.type != WorkflowInstanceChangeLog.TYPE_评论) {
                throw new AppException("评论不存在");
            }
            if (changeLog.ownerAccountId != account.id) {
                throw new AppException("权限不足");
            }
            dao.deleteById(WorkflowInstanceChangeLog.class, id);
        }

        @Override
        public ExportData exportWorkflowInstancesToExcel(String token, WorkflowInstanceQuery query) {
            ExportData ret = new ExportData();
            Account account = bizService.getExistedAccountByToken(token);
            setupQuery(account, query);
            if (query.workflowDefineId == 0) {
                throw new AppException("流程模板不能为空");
            }
            if (!haveDataPermission(account.id, account.companyId, query.workflowDefineId)) {
                throw new AppException("权限不足");
            }
            query.status = WorkflowInstance.STATUS_正式;
            List<String> headers = new ArrayList<>(Arrays.asList("编号", "提交人", "当前步骤", "当前负责人", "结束状态", "提交时间"));
            //
            WorkflowDefine define = dao.getExistedById(WorkflowDefine.class, query.workflowDefineId);
            WorkflowFormDefine form = dao.getExistedById(WorkflowFormDefine.class, define.formDefineId);
            List<FormField> fieldDefines = JSONUtil.fromJsonList(form.fieldList, FormField.class);
            List<FormField> importField = new ArrayList<>();
            if (fieldDefines != null) {
                for (FormField e : fieldDefines) {
                    if (e.type.equals(FormField.TYPE_TABLE) || e.type.startsWith("static-")) {
                        continue;//不导出表格和静态文本
                    }
                    headers.add(e.name);
                    importField.add(e);
                }
            }
            //
            List<WorkflowInstanceInfo> list = dao.getList(query);
            TableData data = new TableData();
            data.headers = headers;
            for (WorkflowInstanceInfo e : list) {
                List<String> content = new ArrayList<>();
                content.add(e.serialNo);
                content.add(e.createAccountName);
                content.add(e.currNodeName);
                content.add(getCurrOwners(e));
                content.add(e.isFinished ? "已结束" : "未结束");
                content.add(DateUtil.formatDate(e.createTime));
                //
                Map<String, Object> formFieldValue = workflowService.getFormFieldValues(e);
                for (FormField fieldDefine : importField) {
                    String fieldId = fieldDefine.id;
                    String value = getFormFieldValueForExport(fieldDefine, formFieldValue.get(fieldId));
                    content.add(value);
                }
                //
                data.contents.add(content);
            }
            ret.fileName = "流程数据.xlsx";
            ret.tableData = data;
            return ret;
        }
        //

        /**
         * 富文本会htmlclean掉 staticFiled不会导出
         *
         * @param formField
         * @param value
         * @return
         */
        private String getFormFieldValueForExport(FormField formField, Object value) {
            if (formField == null || value == null) {
                return "";
            }
            String t = formField.type;
            if (t.equals(FormField.TYPE_TEXT_SINGLE) ||
                    t.equals(FormField.TYPE_SYSTEM_VALUE) ||
                    t.equals(FormField.TYPE_RADIO) ||
                    t.equals(FormField.TYPE_SELECT)) {// 直接取值
                return value.toString();
            } else if (t.equals(FormField.TYPE_TEXT_AREA) ||
                    t.equals(FormField.TYPE_TEXT_RICH)) {// 直接取值且htmlclean
                return BizUtil.cleanHtml(value.toString());
            } else if (t.equals(FormField.TYPE_USER_SELECT) ||
                    t.equals(FormField.TYPE_ATTACHMENT) ||
                    t.equals(FormField.TYPE_DEPARTMENT_SELECT) ||
                    t.equals(FormField.TYPE_ROLE_COMPANY_SELECT) ||
                    t.equals(FormField.TYPE_ROLE_PROJECT_SELECT)) {
                List<FormFieldObjectValue> values = JSONUtil.fromJsonList(value.toString(),
                        FormFieldObjectValue.class);
                return BizUtil.getListSingleField(values, "name");
            } else if (t.equals(FormField.TYPE_DATE)) {
                return DateUtil.formatDate(DateUtil.parseDateTimeFromExcel(value.toString()));
            } else {
                return value.toString();
            }
        }

        //
        private String getCurrOwners(WorkflowInstanceInfo instance) {
            List<WorkflowInstanceOwnerSimpleInfo> owners = instance.currAccountList;
            if (owners == null || owners.isEmpty()) {
                return "";
            }
            StringBuilder ownerNames = new StringBuilder();
            for (WorkflowInstanceOwnerSimpleInfo e : owners) {
                ownerNames.append(e.ownerAccountName).append(",");
            }
            ownerNames.deleteCharAt(ownerNames.length() - 1);
            return ownerNames.toString();
        }

        //
        @Transaction
        @Override
        public void notifyWorkflowInstance(String workflowInstanceUuid, String body) {
            WorkflowInstance instance = dao.getExistedByUuidForUpdate(WorkflowInstance.class, workflowInstanceUuid);
            if (instance.isFinished) {
                throw new AppException("流程已经结束");
            }
            GraphNodeInfo currNode = workflowService.getCurrNode(instance);
            if (!currNode.type.equals(GraphNodeInfo.TYPE_NODE_AUTO)) {
                logger.warn("currNode.type.equals(GraphNodeInfo.TYPE_NODE_AUTO {} {}",
                        workflowInstanceUuid, currNode.id);
                throw new AppException("权限不足");
            }
            if (!currNode.nodeProperty.forwardRule.equals(GraphProperty.FORWARD_RULE_WAIT)) {
                logger.warn("currNode.nodeProperty.forwardRule.equals(GraphProperty.FORWARD_RULE_WAIT {} {}",
                        workflowInstanceUuid, currNode.id);
                throw new AppException("权限不足");
            }
            Account account = new Account();
            WorkflowDefine define = dao.getExistedById(WorkflowDefine.class, instance.workflowDefineId);
            nextNode(account, define, instance, "");
        }
    }
}
