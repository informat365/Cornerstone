package cornerstone.biz.srv;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import cornerstone.biz.ConstDefine;
import cornerstone.biz.CornerstoneThreadFactory;
import cornerstone.biz.annotations.ChangeLogTypes;
import cornerstone.biz.annotations.OptLogField;
import cornerstone.biz.dao.BizDAO;
import cornerstone.biz.domain.*;
import cornerstone.biz.domain.Account.AccountInfo;
import cornerstone.biz.domain.Account.AccountQuery;
import cornerstone.biz.domain.Category.CategoryInfo;
import cornerstone.biz.domain.Category.CategoryQuery;
import cornerstone.biz.domain.ChangeLog.ChangeLogItemDetailInfo;
import cornerstone.biz.domain.CmdbApplication.CmdbApplicationInfo;
import cornerstone.biz.domain.CmdbApplication.CmdbApplicationQuery;
import cornerstone.biz.domain.CmdbInstance.CmdbInstanceInfo;
import cornerstone.biz.domain.CmdbInstance.CmdbInstanceQuery;
import cornerstone.biz.domain.CmdbMachine.CmdbMachineInfo;
import cornerstone.biz.domain.CmdbMachine.CmdbMachineQuery;
import cornerstone.biz.domain.CompanyMember.CompanyMemberInfo;
import cornerstone.biz.domain.CompanyMember.CompanyMemberQuery;
import cornerstone.biz.domain.Department.DepartmentInfo;
import cornerstone.biz.domain.Department.DepartmentQuery;
import cornerstone.biz.domain.File.FileQuery;
import cornerstone.biz.domain.Filter.FilterInfo;
import cornerstone.biz.domain.Filter.FilterQuery;
import cornerstone.biz.domain.Project.ProjectInfo;
import cornerstone.biz.domain.Project.ProjectQuery;
import cornerstone.biz.domain.ProjectFieldDefine.ProjectFieldDefineInfo;
import cornerstone.biz.domain.ProjectFieldDefine.ProjectFieldDefineQuery;
import cornerstone.biz.domain.ProjectIteration.ProjectIterationInfo;
import cornerstone.biz.domain.ProjectIteration.ProjectIterationQuery;
import cornerstone.biz.domain.ProjectMember.ProjectMemberInfo;
import cornerstone.biz.domain.ProjectMember.ProjectMemberQuery;
import cornerstone.biz.domain.ProjectModule.ProjectModuleInfo;
import cornerstone.biz.domain.ProjectModule.ProjectModuleQuery;
import cornerstone.biz.domain.ProjectPipeline.ProjectPipelineInfo;
import cornerstone.biz.domain.ProjectPriorityDefine.ProjectPriorityDefineInfo;
import cornerstone.biz.domain.ProjectPriorityDefine.ProjectPriorityDefineQuery;
import cornerstone.biz.domain.ProjectRelease.ProjectReleaseInfo;
import cornerstone.biz.domain.ProjectRelease.ProjectReleaseQuery;
import cornerstone.biz.domain.ProjectStatusDefine.ProjectStatusDefineInfo;
import cornerstone.biz.domain.ProjectStatusDefine.ProjectStatusDefineQuery;
import cornerstone.biz.domain.ProjectSubSystem.ProjectSubSystemInfo;
import cornerstone.biz.domain.ProjectSubSystem.ProjectSubSystemQuery;
import cornerstone.biz.domain.Role.RoleInfo;
import cornerstone.biz.domain.Role.RoleQuery;
import cornerstone.biz.domain.Task.TaskDetailInfo;
import cornerstone.biz.domain.Task.TaskInfo;
import cornerstone.biz.domain.Task.TaskQuery;
import cornerstone.biz.domain.TaskAssociated.TaskAssociatedInfo;
import cornerstone.biz.domain.TaskAssociated.TaskAssociatedQuery;
import cornerstone.biz.domain.TaskRemind.TaskRemindInfo;
import cornerstone.biz.pipeline.JavaScriptPipeline;
import cornerstone.biz.ssh.ConnectionInfo;
import cornerstone.biz.ssh.JavaScriptChannelRobot;
import cornerstone.biz.ssh.SshChannel;
import cornerstone.biz.systemhook.JavaScriptSystemHook;
import cornerstone.biz.systemhook.SystemHookContext;
import cornerstone.biz.taskjob.BizTaskJobs;
import cornerstone.biz.util.*;
import cornerstone.biz.util.LicenseUtil.License;
import jazmin.core.Jazmin;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;
import jazmin.driver.jdbc.smartjdbc.Query;
import jazmin.driver.jdbc.smartjdbc.QueryWhere;
import jazmin.driver.jdbc.smartjdbc.SqlBean;
import jazmin.driver.jdbc.smartjdbc.provider.SelectProvider;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;
import jazmin.util.JSONUtil;
import jazmin.util.RandomUtil;
import org.apache.commons.lang.math.NumberUtils;

import javax.script.ScriptException;
import java.lang.reflect.Field;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Calendar;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

/**
 * @author cs
 */
public class BizService extends CommService {
    //
    private static Logger logger = LoggerFactory.get(BizService.class);
    //
    @AutoWired
    BizDAO dao;
    @AutoWired
    QCloundSMSService qCloundSMSService;

    /**
     * ?????????????????????
     */
    public void sendVerifyCode(String mobileNo) {
        if (StringUtil.isEmpty(mobileNo)) {
            throw new AppException("?????????????????????");
        }
        if (!PatternUtil.isMobile(mobileNo)) {
            throw new AppException("?????????????????????");
        }
        StringBuilder code = new StringBuilder();
        int[] codes = new int[]{0, 1, 2, 3, 5, 6, 7, 8, 9};
        for (int i = 0; i < 4; i++) {
            code.append(codes[RandomUtil.randomInt(codes.length)]);
        }
        //
        try {
            // 5????????????
            int validTime = 5 * 60;
            Date validDateTime = new Date(System.currentTimeMillis() + validTime * 1000);
            Kaptcha verfiedCode = dao.getByField(Kaptcha.class, "sign", mobileNo);
            if (verfiedCode == null) {
                verfiedCode = new Kaptcha();
                verfiedCode.type = Kaptcha.TYPE_???????????????;
                verfiedCode.sign = mobileNo;
                verfiedCode.code = code.toString();
                verfiedCode.validTime = validDateTime;
                dao.add(verfiedCode);
            } else {
                //20200214??????
                KaptchaLog log = KaptchaLog.create(verfiedCode);
                dao.add(log);
                //
                verfiedCode.code = code.toString();
                verfiedCode.validTime = validDateTime;
                dao.update(verfiedCode);
            }
            //????????????????????????????????? ?????????????????????
            String content = code + "?????????????????????????????????5?????????????????????????????????????????????????????????";
            qCloundSMSService.sendSms(mobileNo, content);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (e instanceof SQLIntegrityConstraintViolationException) {//????????????
                throw new AppException("???????????????????????????");
            }
            throw new AppException("??????????????????");
        }

    }

    /**
     * ????????????????????????
     */
    public void checkVerifyCode(String sign, String code) {
        if (sign == null || code == null) {
            logger.warn("sign == null || code == null", sign, code);
            throw new AppException("??????????????????");
        }
        Kaptcha verifyCode = dao.getByFieldForUpdate(Kaptcha.class, "sign", sign);
        if (verifyCode == null) {
            logger.warn("verifyCode == null");
            throw new AppException("??????????????????");
        }
        if ((!verifyCode.code.equalsIgnoreCase(code))) {
            logger.warn("verifyCode:{} not equal code:{}", cornerstone.biz.util.DumpUtil.dump(verifyCode), code);
            throw new AppException("??????????????????");
        }
        Date now = new Date();
        if (verifyCode.validTime.before(now)) {
            logger.warn("verifyCode.validTime:{} now:{}", verifyCode.validTime, now);
            throw new AppException("??????????????????");
        }
        verifyCode.validTime = new Date();//?????????
        dao.update(verifyCode);
    }

    public void checkMobileVerifyCode(Account account, String sign, String code) {
        if (sign == null || code == null) {
            updateKaptchaErrorCount(account);
            throw new AppException("??????????????????");
        }
        Kaptcha verifyCode = dao.getByFieldForUpdate(Kaptcha.class, "sign", sign);
        if (verifyCode == null) {
            updateKaptchaErrorCount(account);
            throw new AppException("??????????????????");
        }
        if ((!verifyCode.code.equalsIgnoreCase(code))) {
            updateKaptchaErrorCount(account);
            logger.error("verifyCode:{} not equal code:{}", DumpUtil.dump(verifyCode), code);
            throw new AppException("??????????????????");
        }
        Date now = new Date();
        if (verifyCode.validTime.before(now)) {
            updateKaptchaErrorCount(account);
            throw new AppException("??????????????????");
        }
        verifyCode.validTime = new Date();//?????????
        dao.update(verifyCode);
    }

    /**
     * ??????????????????????????????
     * ??????5??? ????????????10??????
     * ??????10??? ????????????1?????????
     * ??????20???????????? ????????????24?????????
     */
    private void updateKaptchaErrorCount(Account account) {
        if (account == null) {
            return;
        }
        account.kaptchaErrorCount++;
        if (account.kaptchaErrorCount >= 20) {
            account.disableEndTime = DateUtil.getNextHour(24);
        } else if (account.kaptchaErrorCount >= 10) {
            account.disableEndTime = DateUtil.getNextHour(1);
        } else if (account.kaptchaErrorCount >= 5) {
            account.disableEndTime = DateUtil.getNextMinute(10);
        }
        dao.updateAccountKaptchaErrorCount(account);
    }

    /**
     * ????????????????????????
     */
    public void checkPasswordValid(Account account, String password) {
        if (StringUtil.isEmpty(password)) {
            throw new AppException("????????????????????????");
        }
        if (!BizUtil.encryptPassword(password).equals(account.password)) {
            throw new AppException("????????????????????????");
        }
    }


    /**
     * ??????????????????????????????
     */
    public List<ProjectFieldDefineInfo> getProjectFieldDefineList(int projectId, int objectType) {
        ProjectFieldDefineQuery fieldDefineQuery = new ProjectFieldDefineQuery();
        fieldDefineQuery.projectId = projectId;
        fieldDefineQuery.isShow = true;
        fieldDefineQuery.objectType = objectType;
        fieldDefineQuery.sortWeightSort = ProjectFieldDefineQuery.SORT_TYPE_ASC;
        fieldDefineQuery.pageSize = Integer.MAX_VALUE;
        return dao.getList(fieldDefineQuery);
    }

    /**
     * ????????????????????????
     */
    public List<ProjectIterationInfo> getProjectIterationListByProjectId(int projectId) {
        ProjectIterationQuery query = new ProjectIterationQuery();
        query.projectId = projectId;
        query.isDelete = false;
        query.pageSize = Integer.MAX_VALUE;
        return dao.getList(query);
    }

    /**
     * ????????????????????????
     */
    public List<ProjectMemberInfo> getProjectMemberInfoListByProjectId(int projectId) {
        ProjectMemberQuery memberQuery = new ProjectMemberQuery();
        memberQuery.projectId = projectId;
        memberQuery.pageSize = Integer.MAX_VALUE;
        return dao.getList(memberQuery);
    }

    /**
     * ??????????????????????????????????????????
     */
    public List<ProjectStatusDefineInfo> getProjectStatusDefineInfoListByProjectIdObjectType(int projectId,
                                                                                             int objectType) {
        List<ProjectStatusDefineInfo> list = dao.getProjectStatusDefineInfoList(projectId, objectType);
        List<ProjectStatusDefineInfo> all = new ArrayList<>();
        all.addAll(list);
        //
        Queue<ProjectStatusDefineInfo> queue = new LinkedList<>();
        List<ProjectStatusDefineInfo> result = new ArrayList<>();
        for (ProjectStatusDefineInfo e : list) {
            if (e.type == ProjectStatusDefineInfo.TYPE_????????????) {
                queue.offer(e);
                list.remove(e);
                break;
            }
        }
        while (!queue.isEmpty()) {
            ProjectStatusDefineInfo info = queue.poll();
            result.add(info);
            List<Integer> transferTo = info.transferTo;
            if (transferTo == null || transferTo.isEmpty()) {
                break;
            }
            for (Integer toId : transferTo) {
                ProjectStatusDefineInfo to = BizUtil.getByTarget(list, "id", toId);
                if (to == null) {
                    continue;
                }
                if (to.type != ProjectStatusDefineInfo.TYPE_????????????) {
                    queue.offer(to);
                    list.remove(to);
                }
            }
        }
        //
        for (ProjectStatusDefineInfo e : list) {
            if (e.type == ProjectStatusDefineInfo.TYPE_????????????) {
                result.add(e);
            }
        }
        if (result.size() != all.size()) {
            return all;
        }
        return result;
    }

    /**
     * ??????????????????????????????????????????
     */
    public int getStartProjectStatusDefineIdByProjectIdObjectType(int projectId, int objectType) {
        ProjectStatusDefineQuery query = new ProjectStatusDefineQuery();
        query.projectId = projectId;
        query.objectType = objectType;
        query.pageSize = Integer.MAX_VALUE;
        query.typeSort = Query.SORT_TYPE_ASC;
        query.type = ProjectStatusDefine.TYPE_????????????;
        List<ProjectStatusDefineInfo> list = dao.getList(query);
        if (list.isEmpty()) {
            ObjectType ot = dao.getExistedById(ObjectType.class, objectType);
            if (ot == null) {
                throw new AppException("??????????????????" + objectType);
            }
            throw new AppException(ot.name + "??????????????????");
        }
        return list.get(0).id;
    }

    /**
     * ???????????????????????????
     */
    public List<ProjectPriorityDefineInfo> getProjectPriorityDefineInfoListByProjectIdObjectType(int projectId,
                                                                                                 int objectType) {
        return dao.getProjectPriorityDefineInfoList(projectId, objectType);
    }

    /**
     * ?????????????????????
     */
    public int getDefaultProjectPriorityDefineIdByProjectIdObjectType(int projectId, int objectType) {
        ProjectPriorityDefineQuery query = new ProjectPriorityDefineQuery();
        query.projectId = projectId;
        query.objectType = objectType;
        query.pageSize = Integer.MAX_VALUE;
        List<ProjectPriorityDefineInfo> list = dao.getList(query);
        if (list.isEmpty()) {
            ObjectType ot = dao.getExistedById(ObjectType.class, objectType);
            if (ot == null) {
                throw new AppException("??????????????????" + objectType);
            }
            throw new AppException(ot.name + "?????????????????????");
        }
        return list.get(0).id;
    }

    /**
     * ????????????release??????
     */
    public List<ProjectReleaseInfo> getProjectReleaseInfoListByProjectId(int projectId) {
        ProjectReleaseQuery query = new ProjectReleaseQuery();
        query.isDelete = false;
        query.projectId = projectId;
        query.pageSize = Integer.MAX_VALUE;
        return dao.getList(query);
    }

    /**
     * ???????????????????????????
     */
    public List<ProjectSubSystemInfo> getProjectSubSystemInfoListByProjectId(int projectId) {
        ProjectSubSystemQuery query = new ProjectSubSystemQuery();
        query.isDelete = false;
        query.projectId = projectId;
        query.pageSize = Integer.MAX_VALUE;
        return dao.getList(query);
    }

    /**
     * ??????????????????????????????
     */
    public List<CategoryInfo> getCategoryInfoList(int projectId, int objectType) {
        CategoryQuery query = new CategoryQuery();
        query.projectId = projectId;
        query.objectType = objectType;
        query.pageSize = Integer.MAX_VALUE;
        return dao.getList(query);
    }


    /**
     * ???????????????????????????
     */
    public int getTotalStatusBaseTaskNumByIteration(ProjectIteration iteration, int[] objectTypeList) {
        if (BizUtil.isNullOrEmpty(objectTypeList)) {
            return 0;
        }
        TaskQuery taskQuery = new TaskQuery();
        taskQuery.iterationId = iteration.id;
        taskQuery.objectTypeInList = objectTypeList;
        // ??????????????? ??????????????????bug
        // taskQuery.createTimeStart=iteration.startDate;
        // taskQuery.createTimeEnd=iteration.endDate;
        taskQuery.isDelete = false;
        return dao.getListCount(taskQuery);
    }

    /**
     * ????????????????????????ObjetctType??????????????????
     */
    public int getTotalTaskNumByIterationObjectType(int iterationId, Integer objectType) {
        TaskQuery taskQuery = new TaskQuery();
        taskQuery.iterationId = iterationId;
        taskQuery.objectType = objectType;
        taskQuery.isDelete = false;
        return dao.getListCount(taskQuery);
    }

    public int getTotalTaskNumByProjectObjectType(int projectId, Integer objectType) {
        TaskQuery taskQuery = new TaskQuery();
        taskQuery.projectId = projectId;
        taskQuery.objectType = objectType;
        taskQuery.isDelete = false;
        return dao.getListCount(taskQuery);
    }

    /**
     * ??????????????????status base????????????????????????
     */
    public int getTotalStatusBaseFinishTaskNumByIterationObjectType(int iterationId, Integer objectType) {
        TaskQuery taskQuery = new TaskQuery();
        taskQuery.isDelete = false;
        taskQuery.objectType = objectType;
        taskQuery.iterationId = iterationId;
        taskQuery.isFinish = true;
        return dao.getListCount(taskQuery);
    }

    public int getTotalStatusBaseFinishTaskNumByProjectObjectType(int projectId, Integer objectType) {
        TaskQuery taskQuery = new TaskQuery();
        taskQuery.isDelete = false;
        taskQuery.objectType = objectType;
        taskQuery.projectId = projectId;
        taskQuery.isFinish = true;
        return dao.getListCount(taskQuery);
    }

    /**
     * ?????????????????????????????????????????????
     */
    public int getTotalTimeBaseDelayTaskNumByIterationObjectType(int iterationId, Integer objectType) {
        TaskQuery taskQuery = new TaskQuery();
        taskQuery.isDelete = false;
        taskQuery.iterationId = iterationId;
        taskQuery.objectType = objectType;
        taskQuery.isFinish = false;
        taskQuery.isDelay = true;
        return dao.getListCount(taskQuery);
    }

    public int getTotalTimeBaseDelayTaskNumByProjectObjectType(int projectId, Integer objectType) {
        TaskQuery taskQuery = new TaskQuery();
        taskQuery.isDelete = false;
        taskQuery.projectId = projectId;
        taskQuery.objectType = objectType;
//        taskQuery.isFinish = false;
        taskQuery.isDelay = true;
        return dao.getListCount(taskQuery);
    }

    /**
     * ??????????????????????????????objectType??????(objectType>0)
     */
    public int[] getCompanyEnableObjectTypelist(int companyId) {
        ProjectModuleQuery query = new ProjectModuleQuery();
        query.companyId = companyId;
        query.isEnable = true;
        query.pageSize = Integer.MAX_VALUE;
        List<ProjectModuleInfo> modules = dao.getList(query);
        Set<Integer> statusBaseObjectType = new LinkedHashSet<>();
        for (ProjectModuleInfo e : modules) {
            if (e.objectType > 0) {
                statusBaseObjectType.add(e.objectType);
            }
        }
        return BizUtil.convertList(statusBaseObjectType);
    }

    /**
     * ????????????????????????????????????objectType??????
     */
    public int[] getEnableObjectTypelist(int projectId) {
        List<ProjectModuleInfo> modules = getEnableProjectModuleInfoList(projectId);
        List<Integer> statusBaseObjectType = new ArrayList<>();
        for (ProjectModuleInfo e : modules) {
            if (e.objectType > 0) {
                statusBaseObjectType.add(e.objectType);
            }
        }
        return BizUtil.convertList(statusBaseObjectType);
    }

    public List<ProjectModuleInfo> getEnableObjectTypeList(int projectId) {
        List<ProjectModuleInfo> modules = getEnableProjectModuleInfoList(projectId);
        List<ProjectModuleInfo> list = new ArrayList<>();
        for (ProjectModuleInfo e : modules) {
            if (e.objectType > 0) {
                list.add(e);
            }
        }
        return list;
    }


    /**
     * ????????????????????????
     */
    public List<ProjectMemberInfo> getProjectMemberInfoList(int projectId) {
        ProjectMemberQuery query = new ProjectMemberQuery();
        query.projectId = projectId;
        query.pageSize = Integer.MAX_VALUE;
        query.idSort = Query.SORT_TYPE_ASC;
        List<ProjectMemberInfo> memberList = dao.getList(query);
        //
        for (ProjectMemberInfo member : memberList) {
            RoleQuery roleQuery = new RoleQuery();
            roleQuery.projectMemberId = member.id;
            roleQuery.pageSize = Integer.MAX_VALUE;
            member.roleList = dao.getList(roleQuery);
        }
        return memberList;
    }

    public List<ProjectMemberInfo> getProjectMemberInfoListByCompanyId(int companyId) {
        ProjectMemberQuery query = new ProjectMemberQuery();
        query.companyId = companyId;
        query.pageSize = Integer.MAX_VALUE;
        query.idSort = Query.SORT_TYPE_ASC;
        List<ProjectMemberInfo> memberList = dao.getList(query);
        //
        for (ProjectMemberInfo member : memberList) {
            RoleQuery roleQuery = new RoleQuery();
            roleQuery.projectMemberId = member.id;
            roleQuery.pageSize = Integer.MAX_VALUE;
            member.roleList = dao.getList(roleQuery);
        }
        return memberList;
    }

    /**
     * ????????????????????????????????????(???????????????)
     */
    public List<ProjectInfo> getMyRunnigProjectInfoList(int accountId, int companyId) {
        return getMyRunnigProjectInfoList(accountId, companyId, null);
    }

    public List<ProjectInfo> getMyRunnigProjectInfoList(int accountId, int companyId, ProjectQuery query) {
        if (query == null) {
            query = new ProjectQuery();
        }
        if (!isCompanySuperBoss(accountId)) {
            query.memberAccountId = accountId;
        }
        query.companyId = companyId;
        query.status = Project.STATUS_?????????;
        query.isDelete = false;
        query.pageSize = 10000;
        query.isTemplate = false;
        return dao.getList(query);
    }

    public List<String> getMyRunningProjectGroupList(int accountId, int companyId) {
        ProjectQuery query = new ProjectQuery();
        if (!isCompanySuperBoss(accountId)) {
            query.memberAccountId = accountId;
        }
        query.companyId = companyId;
        query.status = Project.STATUS_?????????;
        query.isDelete = false;
        query.pageSize = 10000;
        query.isTemplate = false;
        query.groupSet = true;
        SqlBean sqlBean = new SelectProvider(Project.class).query(query).needOrderBy(false).build();
        sqlBean.selectSql = "select distinct(`group`)  ";
        sqlBean.orderBySql = "order by `group` asc ";
        return dao.queryForStrings(sqlBean.toSql(), sqlBean.parameters);
    }

    /**
     * ????????????????????????????????????id??????
     */
    public int[] getMyRunnigProjectIdList(int accountId, int companyId) {
        List<ProjectInfo> list = getMyRunnigProjectInfoList(accountId, companyId);
        List<Integer> projectIdList = new ArrayList<>();
        for (ProjectInfo e : list) {
            projectIdList.add(e.id);
        }
        return BizUtil.convertList(projectIdList);
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????
     */
    public List<ProjectInfo> getMyProjectInfoList(int accountId, int companyId) {
        ProjectQuery query = new ProjectQuery();
        query.isDelete = false;
        query.memberAccountId = accountId;
        query.companyId = companyId;
        query.pageSize = 10000;
        return dao.getList(query);
    }

    /**
     * ??????????????????????????????????????????????????????????????????????????????????????????
     */
    public List<ProjectInfo> getMyProjectInfoListExcludeFinished(int accountId, int companyId) {
        ProjectQuery query = new ProjectQuery();
        query.isDelete = false;
        query.status = ProjectInfo.STATUS_?????????;
        query.memberAccountId = accountId;
        query.companyId = companyId;
        query.pageSize = 10000;
        return dao.getList(query);
    }

    /**
     * ??????????????????
     */
    public List<RoleInfo> getMyRoleInfoList(int accountId, int companyId) {
        RoleQuery query = new RoleQuery();
        query.globalAccountId = accountId;
        query.companyId = companyId;
        query.pageSize = 10000;
        return dao.getList(query);
    }

    /**
     * ????????????????????????
     */
    public List<Integer> getMyRoleIdList(int accountId, int companyId) {
        List<RoleInfo> list = getMyRoleInfoList(accountId, companyId);
        List<Integer> result = new ArrayList<>();
        for (RoleInfo e : list) {
            result.add(e.id);
        }
        return result;
    }

    /**
     * ????????????????????????
     */
    public List<RoleInfo> getMyCompanyRoleList(Account account) {
        RoleQuery roleQuery = new RoleQuery();
        roleQuery.globalAccountId = account.id;
        roleQuery.companyId = account.companyId;
        roleQuery.type = Role.TYPE_??????;
        roleQuery.pageSize = Integer.MAX_VALUE;
        List<RoleInfo> roleList = dao.getList(roleQuery);
        return roleList;
    }

    /**
     * ????????????????????????
     */
    public Set<Integer> getMyCompanyRoleIdList(int accountId, int companyId) {
        CompanyMember member = dao.getCompanyMemberInfoByCompanyIdAccountId(companyId, accountId);
        if (member == null) {
            throw new AppException("????????????");
        }
        RoleQuery roleQuery = new RoleQuery();
        roleQuery.globalAccountId = accountId;
        roleQuery.pageSize = Integer.MAX_VALUE;
        List<Role> roleList = dao.getList(roleQuery);
        Set<Integer> idList = new HashSet<>();
        for (Role role : roleList) {
            idList.add(role.id);
        }
        return idList;
    }

    /**
     * ???????????????????????????
     */
    public Set<Integer> getTaskNotificationAccountIds(Task task, int excludeAccountId) {
        Set<Integer> accountIds = new LinkedHashSet<>();
        if (excludeAccountId != task.createAccountId) {
            accountIds.add(task.createAccountId);
        }
        if (task.ownerAccountIdList != null) {
            for (Integer id : task.ownerAccountIdList) {
                if (id != excludeAccountId) {
                    accountIds.add(id);
                }
            }
        }
        return accountIds;
    }

    /**
     * ??????????????????
     */
    public void sendNotificationForTask(Account optAccount, TaskInfo task, int type, String name,
                                        Map<String, Object> extraInfo) {
        int optAccountId = 0;
        String optAccountName = "";
        String optAccountImageId = "";
        if (optAccount != null) {
            optAccountId = optAccount.id;
            optAccountName = optAccount.name;
            optAccountImageId = optAccount.imageId;
        }
        // ????????????????????????
        Set<Integer> accountIds = getTaskNotificationAccountIds(task, optAccountId);
        if (accountIds.isEmpty()) {
            return;
        }
        TaskSimpleInfo bean = TaskSimpleInfo.createTaskSimpleinfo(task);
        Map<String, Object> map = new HashMap<>();
        map.put("task", bean);
        if (extraInfo != null) {
            extraInfo.forEach(map::put);
        }
        for (Integer accountId : accountIds) {
            addAccountNotification(accountId, type, task.companyId, task.projectId, task.id, name, JSONUtil.toJson(map),
                    new Date(), optAccountId, optAccountName, optAccountImageId);

        }
    }

    /**
     * ??????????????????
     */
    public void sendNotificationForAlteration(Account optAccount, List<AccountSimpleInfo> receiverAccountList, TaskInfo task, int type, String name,
                                              Map<String, Object> extraInfo) {
        TaskSimpleInfo bean = TaskSimpleInfo.createTaskSimpleinfo(task);
        Map<String, Object> map = new HashMap<>();
        map.put("task", bean);
        if (extraInfo != null) {
            extraInfo.forEach(map::put);
        }
        for (AccountSimpleInfo account : receiverAccountList) {
            addAccountNotification(account.id, type, task.companyId, task.projectId, task.id, name, JSONUtil.toJson(map),
                    new Date(), optAccount.id, optAccount.name, optAccount.imageId);

        }
    }


    /**
     * ????????????????????????????????????
     */
    public boolean isCreaterOrOwner(Account account, Task task) {
        if (account.id == task.createAccountId) {
            return true;
        }
        return BizUtil.contains(task.ownerAccountIdList, account.id);
    }


    /***
     * ??????????????????
     *
     */
    public void checkProjectDataPermission(Account account, Task task, String permissionId) {
        if (checkAccountSuperBossPermission(account, permissionId, task.projectId)) {
            return;
        }
        Set<String> havePermissions = getMyTaskPermission(account, task);
        if (!havePermissions.contains(permissionId)) {
            logger.warn("permissionId:{} accountId:{} havePermissions:{}", permissionId, account.id,
                    DumpUtil.dump(havePermissions));
            throw new AppException("????????????" + permissionId);
        }
    }

    /**
     * ?????????????????????????????????
     */
    public void checkTaskViewPermission(Account account, TaskInfo info) {
        if (isCompanySuperBoss(account)) {
            return;
        }
        //??????????????????
        if (!checkTaskViewPermission(account.id, info.projectId, info.objectType)) {
            if (info.createAccountId != account.id && !info.ownerAccountIdList.contains(account.id)) {
                logger.error("account.id {} info.projectId:{}", account.id, info.projectId);
                throw new AppException("????????????,??????????????????????????????");
            }
        }
        if (info.isDelete) {
            throw new AppException(info.objectTypeName + "???????????????");
        }
        checkPermission(account, info.companyId);
    }

    /**
     * ????????????????????????????????????
     */
    public void checkIsCompanyMember(int accountId, int companyId) {
        CompanyMember member = dao.getCompanyMemberByAccountIdCompanyId(accountId, companyId);
        if (member == null) {
            throw new AppException("??????????????????");
        }
    }

    /**
     * ???????????????????????????
     */
    public boolean isCompanyMember(int accountId, int companyId) {
        CompanyMember member = dao.getCompanyMemberByAccountIdCompanyId(accountId, companyId);
        return member != null;
    }


    /**
     * ?????????????????????????????????
     */
    public ObjectTypeExtInfo getObjectTypeExtInfo(List<ProjectFieldDefine> list) {
        ObjectTypeExtInfo info = new ObjectTypeExtInfo();
        for (ProjectFieldDefine e : list) {
            if (e.isSystemField) {
                if (e.isShow && ("startDate".equals(e.field) || "endDate".equals(e.field))) {
                    info.isTimeBased = true;
                }
                if (e.isShow && "statusName".equals(e.field)) {
                    info.isStatusBased = true;
                }
            }
        }
        return info;
    }

    /**
     * ??????????????? ??????????????????
     */
    public Set<Integer> getAccountIdListByProjectIdRoleId(int projectId, int roleId) {
        List<Integer> idList = dao.getAccountIdListByProjectIdRoleId(projectId, roleId);
        return BizUtil.convert(idList);
    }

    /**
     * ??????token
     */
    public void cleanToken(int accountId) {
        AccountToken token = dao.getAccountTokenByAccountIdForUpdate(accountId);
        if (token != null) {
            token.token = BizUtil.randomUUID();
            dao.update(token);
        }
    }

    /**
     * ??????????????????
     */
    public ChangeLog addChangeLog(Account account, int associatedId, int type, String items) {
        return addChangeLog(account, 0, 0, associatedId, type, "", items, "");
    }

    /**
     * ????????????????????????projectId???0  ???????????????projectId??????0
     */
    public ChangeLog addChangeLog(Account account, int projectId, int taskId, int type, String items) {
        return addChangeLog(account, projectId, taskId, 0, type, "", items, "");
    }

    //
    public String toSimpleJson(TaskInfo bean) {
        return JSONUtil.toJson(TaskSimpleInfo.createTaskSimpleinfo(bean));
    }

    //
    public ChangeLog addChangeLog(Account account, int projectId, int taskId, int associatedId, int type, String itemId, String items,
                                  String remark) {
        ChangeLog changeLog = new ChangeLog();
        changeLog.projectId = projectId;
        changeLog.taskId = taskId;
        changeLog.associatedId = associatedId;
        changeLog.type = type;
        changeLog.itemId = itemId;
        changeLog.items = items;
        changeLog.remark = remark;
        if (account != null) {// ???????????????????????????????????????scm??????
            changeLog.companyId = account.companyId;
            changeLog.createAccountId = account.id;
        }
        dao.add(changeLog);
        return changeLog;
    }

    public void addChangeLog(Account account, int projectId, String itemId, List<ChangeLogItem> itemList,
                             String remark) {
        if (itemList.size() > 0) {
            addChangeLog(account, projectId, 0, 0, ChangeLog.TYPE_????????????, itemId, JSONUtil.toJson(itemList), remark);
        }
    }

    public List<FilterInfo> getFilterList(Account account, int projectId, int objectType) {
        FilterQuery filterQuery = new FilterQuery();
        filterQuery.projectId = projectId;
        filterQuery.objectType = objectType;
        filterQuery.orCreateAccountId = account.id;
        filterQuery.orOwnerAccountId = 0;
        filterQuery.pageSize = Integer.MAX_VALUE;
        List<FilterInfo> filterList = dao.getList(filterQuery);
        for (Filter e : filterList) {
            e.condition = null;
        }
        return filterList;
    }

    public AccountSimpleInfo createAccountSimpleInfo(CompanyMemberInfo info) {
        AccountSimpleInfo bean = new AccountSimpleInfo();
        bean.id = info.accountId;
        bean.imageId = info.accountImageId;
        bean.name = info.accountName;
        bean.userName = info.accountUserName;
        return bean;
    }

    public AccountSimpleInfo createAccountSimpleInfo(ProjectMemberInfo info) {
        AccountSimpleInfo bean = new AccountSimpleInfo();
        bean.id = info.accountId;
        bean.imageId = info.accountImageId;
        bean.name = info.accountName;
        bean.userName = info.accountUserName;
        return bean;
    }


    public List<AccountSimpleInfo> createAccountSimpleInfo(List<Integer> ownerAccountIdList) {
        List<Account> accounts = dao.getAccountIdsByIds(ownerAccountIdList);
        List<AccountSimpleInfo> infos = new ArrayList<>();
        accounts.forEach(acc -> {
            AccountSimpleInfo info = new AccountSimpleInfo();
            info.id = acc.id;
            info.name = acc.name;
            info.userName = acc.userName;
            info.companyId = acc.companyId;
            info.imageId = acc.imageId;
            infos.add(info);
        });
        return infos;
    }

    //
    public int runProjectPipeline(Account account, ProjectPipelineInfo old) {
        if (old.isDelete) {
            return 0;
        }
        if (account != null) {
            checkPermission(account, old.companyId);
        }
        if (old.runId > 0) {
            ProjectPipelineRunLog lastRunLog = dao.getById(ProjectPipelineRunLog.class, old.runId);
            if (lastRunLog != null) {
                if (lastRunLog.status != ProjectPipelineRunLog.STATUS_????????????
                        && lastRunLog.status != ProjectPipelineRunLog.STATUS_????????????) {
                    logger.error("old.runId:{} lastRunLog:{}", old.runId, cornerstone.biz.util.DumpUtil.dump(lastRunLog));
                    throw new AppException("?????????????????????????????????????????????????????????");
                }
            }
        }
        ProjectPipelineRunLog runLog = new ProjectPipelineRunLog();
        runLog.companyId = old.companyId;
        runLog.projectId = old.projectId;
        runLog.pipelineId = old.id;
        runLog.script = old.script;
        runLog.stepInfo = new ArrayList<>();
        if (account != null) {
            runLog.createAccountId = account.id;
        }
        runLog.status = ProjectPipelineRunLog.STATUS_????????????;
        runLog.startTime = new Date();
        dao.add(runLog);
        //
        old.runId = runLog.id;
        old.lastRunTime = new Date();
        if (account != null) {
            old.lastRunAccountId = account.id;
        }
        old.nextRunTime = BizUtil.nextRunTime(old.lastRunTime, old.cron);
        old.runCount++;
        dao.update(old);
        //
        try {
            CornerstoneThreadFactory.execute(() -> {
                JavaScriptPipeline javaScriptPipeline = new JavaScriptPipeline(runLog, runLog.script);
                javaScriptPipeline.run(ConstDefine.GLOBAL_KEY);
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        //
        ProjectPipelineSimpleInfo simpleInfo = ProjectPipelineSimpleInfo.create(old);
        addChangeLog(account, old.projectId, 0, ChangeLog.TYPE_??????PIPELINE, JSONUtil.toJson(simpleInfo));
        //
        return runLog.id;
    }

    public void runCmdbRobot(CmdbRobot old) {
        if (old.machineList == null || old.machineList.size() == 0) {
            logger.warn("runCmdbRobot no machine to run");
            return;
        }
        try {
            for (Integer machineId : old.machineList) {
                CmdbMachine machine = dao.getExistedById(CmdbMachine.class, machineId);
                runCmdbRobot(machine, old);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        old.lastRunTime = new Date();
        old.lastRunMachine = JSONUtil.toJson(old.machineList);
        old.nextRunTime = BizUtil.nextRunTime(old.lastRunTime, old.cron);
        dao.update(old);
    }

    private void runCmdbRobot(CmdbMachine machine, CmdbRobot robot) {
        if (logger.isDebugEnabled()) {
            logger.debug("runCmdbRobot machine:{} robot:{}", machine.name, robot.name);
        }
        ConnectionInfo connectionInfo = BizUtil.createConnectionInfo(machine);
        SshChannel sshChannel = new SshChannel(null);
        sshChannel.connectionInfo = connectionInfo;
        try {
            connectionInfo.channelListener = new JavaScriptChannelRobot(robot.code);
        } catch (ScriptException e) {
            logger.error(e.getMessage(), e);
            throw new AppException(e);
        }
        Jazmin.execute(() -> {
            try {
                sshChannel.startShell();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new AppException(e);
            }
        });
    }

    public List<Integer> getAccountIdByAccoountNames(int companyId, String accountNames) {
        List<Integer> list = new ArrayList<>();
        if (StringUtil.isEmpty(accountNames)) {
            return list;
        }
        String[] accountNameList = accountNames.split(",");
        for (String name : accountNameList) {
            if (name == null) {
                continue;
            }
            AccountQuery query = new AccountQuery();
            query.companyId = companyId;
            query.eqName = name;
            query.pageSize = 1;
            List<Account> accounts = dao.getList(query);
            if (accounts.isEmpty()) {
                continue;
            }
            list.add(accounts.get(0).id);
        }
        return list;
    }

    public List<Integer> getCategoryIdList(int companyId, String names) {
        List<Integer> list = new ArrayList<>();
        if (StringUtil.isEmpty(names)) {
            return list;
        }
        String[] nameList = names.split(",");
        for (String name : nameList) {
            if (name == null) {
                continue;
            }
            CategoryQuery query = new CategoryQuery();
            query.companyId = companyId;
            query.eqName = name;
            query.pageSize = 1;
            List<Category> categories = dao.getList(query);
            if (categories.isEmpty()) {
                continue;
            }
            list.add(categories.get(0).id);
        }
        return list;
    }

    /**
     * ?????????????????????????????????????????????????????????
     */
    public void addOrUpdateCompanyAccount(int companyId, Account optAccount, Account account, List<Integer> roleIds,
                                          List<Integer> departmentIds) {
        int accountId = account.id;
        // 1.???????????????
        dao.deleteAccountRolesByAccountIdCompanyId(accountId, companyId);
        Set<Integer> roleSet = BizUtil.convert(roleIds);
        for (Integer roleId : roleSet) {
            Role role = dao.getExistedById(Role.class, roleId);
            if (companyId != role.companyId) {
                throw new AppException("?????????" + role.name + "????????????");
            }
            CompanyMemberRole bean = new CompanyMemberRole();
            bean.accountId = accountId;
            bean.companyId = companyId;
            bean.roleId = roleId;
            dao.add(bean);
        }
        // 2.???????????????????????????
        dao.deleteDepartmentByAccountIdCompanyId(accountId, companyId);
        int[] departmentIdList = BizUtil.convertList(departmentIds);
        List<DepartmentSimpleInfo> departmentList = new ArrayList<>();
        if (!BizUtil.isNullOrEmpty(departmentIdList)) {
            if (departmentIdList.length > 50) {//??????20190719
                throw new AppException("????????????????????????????????????50???");
            }
            DepartmentQuery query = new DepartmentQuery();
            query.idInList = departmentIdList;
            query.type = Department.TYPE_????????????;
            query.pageSize = Integer.MAX_VALUE;
            List<Department> departments = dao.getAll(query);
            for (Department department : departments) {
                if (department.companyId != companyId) {
                    throw new AppException("?????????????????????." + department.name);
                }
                departmentList.add(DepartmentSimpleInfo.create(department));
                Department memberDepartment = new Department();
                memberDepartment.companyId = companyId;
                memberDepartment.accountId = accountId;
                memberDepartment.level = department.level + 1;
                memberDepartment.name = account.name;
                memberDepartment.parentId = department.id;
                memberDepartment.type = Department.TYPE_??????;
                if (optAccount != null) {
                    memberDepartment.createAccountId = optAccount.id;
                }
                dao.add(memberDepartment);
            }
        }
        //
        updateAccountDepartments(accountId, companyId, departmentList);

    }

    public void updateAccountDepartments(int accountId, int companyId, List<DepartmentSimpleInfo> departmentList) {
        CompanyMember companyMember = dao.getCompanyMemberByAccountIdCompanyId(accountId, companyId);
        if (companyMember == null) {
            companyMember = new CompanyMember();
            companyMember.accountId = accountId;
            companyMember.companyId = companyId;
            companyMember.departmentList = departmentList;
            dao.add(companyMember);
            // ????????????+1
            Company company = dao.getExistedByIdForUpdate(Company.class, companyMember.companyId);
            company.memberNum = dao.getCurrMemberNum(company.id);
            if (company.memberNum > company.maxMemberNum) {
                throw new AppException("????????????????????????" + company.maxMemberNum + "?????????????????????");
            }
            dao.updateSpecialFields(company, "memberNum");
        } else {
            companyMember.departmentList = departmentList;
            dao.updateSpecialFields(companyMember, "departmentList");
        }
    }

    //
    public List<DepartmentSimpleInfo> createDepartmentSimpleInfo(List<Integer> departmentIds) {
        int[] departmentIdList = BizUtil.convertList(departmentIds);
        List<DepartmentSimpleInfo> departmentList = new ArrayList<>();
        if (!BizUtil.isNullOrEmpty(departmentIdList)) {
            DepartmentQuery query = new DepartmentQuery();
            query.idInList = departmentIdList;
            query.type = Department.TYPE_????????????;
            query.pageSize = Integer.MAX_VALUE;
            List<Department> departments = dao.getAll(query);
            for (Department department : departments) {
                departmentList.add(DepartmentSimpleInfo.create(department));
            }
        }
        return departmentList;
    }

    //
    @Override
    public void setupQuery(Account account, Query query) {
        BizUtil.setFieldValue(query, "isDelete", false);
        BizUtil.setFieldValue(query, "companyId", account.companyId);
    }

    //
    public List<CmdbInstanceInfo> getCmdbInstanceList(String apiKey) {
        CmdbApi old = dao.getExistedCmdbApi(apiKey);
        CmdbInstanceQuery query = new CmdbInstanceQuery();
        query.companyId = old.companyId;
        query.pageSize = Integer.MAX_VALUE;
        return dao.getList(query);
    }

    public List<CmdbApplicationInfo> getCmdbApplicationList(String apiKey) {
        CmdbApi old = dao.getExistedCmdbApi(apiKey);
        CmdbApplicationQuery query = new CmdbApplicationQuery();
        query.companyId = old.companyId;
        query.pageSize = Integer.MAX_VALUE;
        return dao.getList(query);

    }

    public List<CmdbMachineInfo> getCmdbMachineList(String apiKey) {
        CmdbApi old = dao.getExistedCmdbApi(apiKey);
        CmdbMachineQuery query = new CmdbMachineQuery();
        query.companyId = old.companyId;
        query.pageSize = Integer.MAX_VALUE;
        return dao.getList(query);
    }

    public List<CompanyMemberInfo> getCompanyMemberList(String apiKey) {
        CmdbApi old = dao.getExistedCmdbApi(apiKey);
        CompanyMemberQuery query = new CompanyMemberQuery();
        query.companyId = old.companyId;
        query.pageSize = Integer.MAX_VALUE;
        query.accountStatus = Account.STATUS_??????;
        List<CompanyMemberInfo> list = dao.getList(query);
        for (CompanyMemberInfo e : list) {
            RoleQuery roleQuery = new RoleQuery();
            roleQuery.companyId = e.companyId;
            roleQuery.type = RoleInfo.TYPE_??????;
            roleQuery.globalAccountId = e.accountId;
            roleQuery.pageSize = Integer.MAX_VALUE;
            e.roleList = dao.getList(roleQuery);
        }
        return list;
    }

    public List<ProjectMemberInfo> getProjectMemberList(String apiKey) {
        CmdbApi old = dao.getExistedCmdbApi(apiKey);
        return getProjectMemberInfoListByCompanyId(old.companyId);
    }

    public List<ProjectSubSystemInfo> getProjectSubSystemList(String apiKey) {
        CmdbApi old = dao.getExistedCmdbApi(apiKey);
        ProjectSubSystemQuery query = new ProjectSubSystemQuery();
        query.isDelete = false;
        query.companyId = old.companyId;
        query.pageSize = Integer.MAX_VALUE;
        return dao.getList(query);
    }

    public String getArtifactUuid(String name, String version) {
        ProjectArtifact bean;
        if (StringUtil.isEmpty(version)) {
            bean = dao.getProjectArtifactByName(name);
        } else {
            bean = dao.getProjectArtifactByNameVersion(name, version);
        }
        if (bean == null || bean.isDelete) {
            throw new AppException("???????????????" + name);
        }
        return bean.uuid;
    }

    public void checkWeekValid(List<Long> weeks) {
        if (weeks == null) {
            return;
        }
        for (Long week : weeks) {
            if (week < 1 || week > 7) {
                throw new AppException("????????????" + week);
            }
        }
    }

    public void setNextRemindTimeNew(ReportTemplate bean) {
        if (bean.remindTime == null) {
            return;
        }
        String[] minHour = bean.remindTime.split(":");
        if (minHour.length != 2) {
            return;
        }
        try {
            int hour = Integer.parseInt(minHour[0]);
            int minute = Integer.parseInt(minHour[1]);
            Calendar c = Calendar.getInstance();
            if (bean.period == ReportTemplate.PERIOD_???) {
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                if (bean.periodSetting == null || bean.periodSetting.size() == 0) {//
                    if (c.getTime().before(new Date())) {
                        c.add(Calendar.DAY_OF_YEAR, 1);
                    }
                } else {
                    for (int i = 0; i < 14; i++) {
                        int week = DateUtil.getWeek(c.getTime());
                        if (isSatisfy(c, bean.periodSetting, week)) {
                            bean.reportTime = DateUtil.formatDate(c.getTime(), "yy-MM-dd");
                            bean.nextRemindTime = c.getTime();
                            return;
                        }
                        c.add(Calendar.DAY_OF_YEAR, 1);
                    }
                }
            }
            if (bean.period == ReportTemplate.PERIOD_???) {
                Date monday = DateUtil.getMondayOfThisWeek();
                Date friday = DateUtil.getNextDay(monday, 4);// ?????????
                c.setTime(friday);
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                if (c.getTime().before(new Date())) {
                    c.add(Calendar.DAY_OF_YEAR, 7);
                    bean.reportTime = DateUtil.formatDate(DateUtil.getNextDay(monday, 7), "yy-MM-dd") + "~"
                            + DateUtil.formatDate(DateUtil.getNextDay(friday, 7), "yy-MM-dd");
                } else {
                    bean.reportTime = DateUtil.formatDate(monday, "yy-MM-dd") + "~"
                            + DateUtil.formatDate(friday, "yy-MM-dd");
                }
            }
            if (bean.period == ReportTemplate.PERIOD_???) {
                Date lastDayOfMonth = DateUtil.getNextDay(DateUtil.getNextMonth(DateUtil.getBeginOfMonth(), 1), -1);// ?????????????????????
                c.setTime(lastDayOfMonth);
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                if (c.getTime().before(new Date())) {/// ?????????????????????
                    c.add(Calendar.MONTH, 1);
                }
                bean.reportTime = DateUtil.formatDate(c.getTime(), "yy-MM");
            }
            bean.nextRemindTime = c.getTime();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException("????????????????????????");
        }
    }

    public Date getNextNotifyTime(SystemNotification bean) {
        if (bean.notifyTime == null) {
            throw new AppException("????????????????????????");
        }
        String[] minHour = bean.notifyTime.split(":");
        if (minHour.length != 2) {
            throw new AppException("????????????????????????");
        }
        Date now = new Date();
        int hour = Integer.parseInt(minHour[0]);
        int minute = Integer.parseInt(minHour[1]);
        if (hour > 24 || hour < 0) {
            throw new AppException("????????????????????????");
        }
        if (minute > 60 || minute < 0) {
            throw new AppException("????????????????????????");
        }
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        if (bean.period == SystemNotification.PERIOD_???) {
            if (bean.periodSetting != null && bean.periodSetting.size() > 0) {//???????????????
                Date pushDate = new Date(bean.periodSetting.get(0));
                c.setTime(pushDate);
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                if (c.getTime().before(now)) {
                    throw new AppException("????????????????????????????????????");
                }

            } else {
                if (c.getTime().before(new Date())) {
                    c.add(Calendar.DAY_OF_YEAR, 1);//?????????
                }
            }
            return c.getTime();
        }
        if (bean.period == SystemNotification.PERIOD_???) {
            for (int i = 0; i <= 14; i++) {//?????????14
                int week = DateUtil.getWeek(c.getTime());
                if (isSatisfy(c, bean.periodSetting, week)) {
                    return c.getTime();
                }
                c.add(Calendar.DAY_OF_YEAR, 1);
            }
        }
        //
        if (bean.period == SystemNotification.PERIOD_???) {
            for (int i = 0; i <= 33; i++) {//?????????33
                int dayOfMonth = DateUtil.getDayOfMonth(c.getTime());
                if (isSatisfy(c, bean.periodSetting, dayOfMonth)) {
                    return c.getTime();
                }
                c.add(Calendar.DAY_OF_YEAR, 1);
            }
        }
        //
        throw new AppException("????????????????????????");
    }

    private boolean isSatisfy(Calendar c, List<Long> periodSetting, int day) {
        Date now = new Date();
        if (!BizUtil.contains(periodSetting, day)) {
            return false;
        }
        if (DateUtil.isSameDay(now, c.getTime())) {// ???????????????
            //??????
            //?????????
            return c.getTime().after(now);
        } else {
            return true;
        }
    }

    public String getOptLogRemark(Object oldObj, Object newObj, String... javaFieldNames) {
        try {
            if (javaFieldNames == null || javaFieldNames.length == 0) {
                return "";
            }
            if (newObj == null) {
                return "";
            }
            StringBuilder changeLog = new StringBuilder();
            for (String fieldName : javaFieldNames) {
                Field field = newObj.getClass().getField(fieldName);
                Object newValue = BizUtil.getFieldValue(newObj, field);
                if (oldObj != null) {// ??????(??????????????????)
                    Object oldValue = BizUtil.getFieldValue(oldObj, field);
                    if (!BizUtil.isChange(oldValue, newValue)) {
                        continue;
                    }
                }
                String comment = BizUtil.getFieldComment(field);
                if (StringUtil.isEmpty(comment)) {
                    continue;
                }
                OptLogField optLogField = field.getAnnotation(OptLogField.class);
                if (optLogField != null && (!optLogField.showValue())) {// ????????????
                    changeLog.append(comment).append(";");
                } else {
                    if (newValue == null) {
                        changeLog.append(comment).append(":").append(";");
                    } else {
                        changeLog.append(comment).append(":").append(newValue.toString()).append(";");
                    }

                }

            }
            if (changeLog.length() > 0) {
                changeLog.deleteCharAt(changeLog.length() - 1);
            }
            return changeLog.toString();
        } catch (Exception e) {
            logger.error("getOptLogRemark failed.newObj:{}", DumpUtil.dump(newObj));
            logger.error(e.getMessage(), e);
            return "";
        }

    }

    //
    public int addOptLog(Account optAccount, int associatedId, String associatedName, String event, Object old,
                         Object bean, String... javaFieldNames) {
        String changeLog = getOptLogRemark(old, bean, javaFieldNames);
        if (StringUtil.isEmpty(changeLog)) {
            return 0;
        }
        return addOptLog(optAccount, associatedId, associatedName, event, changeLog);
    }

    /**
     * ??????????????????
     */
    public int addOptLog(Account optAccount, int associatedId, String associatedName, String event, String remark) {
        return addOptLog(optAccount, 0, 0, associatedId, associatedName, event, remark);
    }

    //
    public int addOptLog(Account optAccount, int projectId, int taskId, int associatedId, String associatedName,
                         String event, String remark) {
        OptLog bean = new OptLog();
        bean.accountId = optAccount.id;
        bean.accountName = optAccount.name;
        bean.companyId = optAccount.companyId;
        bean.projectId = projectId;
        bean.taskId = taskId;
        bean.associatedId = associatedId;
        if (associatedName != null && associatedName.length() > 64) {
            associatedName = associatedName.substring(0, 64);
        }
        bean.associatedName = associatedName;
        bean.event = event;
        if (remark != null && remark.length() > 1024 * 10) {// ??????10k
            remark = remark.substring(0, 1024 * 10);
        }
        bean.remark = remark;
        dao.add(bean);
        return bean.id;
    }

    //
    public void checkPermission(Account account, int companyId) {
        if (account.companyId != companyId) {
            logger.error("id:{} account.companyId{} != companyId{}",
                    account.id, account.companyId, companyId);
            throw new AppException("????????????,????????????????????????????????????????????????,???????????????");
        }
    }

    /**
     * ?????????????????????????????????
     */
    public void checkIsCompanyCreater(int companyId, int accountId) {
        Company company = dao.getExistedById(Company.class, companyId);
        if (company.createAccountId != accountId) {
            logger.error("companyId:{} company.createAccountId{}!=accountId{}",
                    companyId, company.createAccountId, accountId);
            throw new AppException("????????????");
        }
    }

    /**
     * ???????????????????????????
     */
    public boolean checkAccountCompanyPermission(int accountId, boolean enableRole, List<Integer> roles,
                                                 String members) {
        return checkAccountCompanyPermission(accountId, enableRole, BizUtil.convert(roles), members);
    }

    /**
     * ???????????????????????????
     */
    public boolean checkAccountCompanyPermission(int accountId, boolean enableRole, Set<Integer> roles,
                                                 String members) {
        if (!enableRole) {// ???????????????????????????????????????
            return true;
        }
        if ((roles == null || roles.isEmpty()) && // ????????????????????????????????????????????????
                StringUtil.isEmptyWithTrim(members)) {
            return true;
        }
        Account account = dao.getExistedById(Account.class, accountId);
        if (roles != null && (!roles.isEmpty())) {
            Set<Integer> myRoles = dao.getAllCompanyRoles(account);
            for (Integer role : roles) {
                if (myRoles.contains(role)) {
                    return true;
                }
            }
        }
        if (!StringUtil.isEmptyWithTrim(members)) {
            String[] userNames = members.split(",");
            for (String userName : userNames) {
                if (userName == null) {
                    continue;
                }
                if (userName.equals(account.userName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * ???????????????????????????
     */
    public boolean checkAccountProjectPermission(int accountId, int projectId, boolean enableRole, Integer createAccountId, List<Integer> roles,
                                                 String members) {
        return checkAccountProjectPermission(accountId, projectId, enableRole, createAccountId,
                BizUtil.convert(roles), members);
    }

    /**
     * ??????????????????
     *
     * @param createAccountId ?????????
     */
    public boolean checkAccountProjectPermission(int accountId, int projectId, boolean enableRole, Integer createAccountId, Set<Integer> roles,
                                                 String members) {
        if (!enableRole) {// ???????????????????????????????????????
            return true;
        }
        if (createAccountId != null && createAccountId == accountId) {//??????????????????????????????
            return true;
        }
        if ((roles == null || roles.isEmpty()) && // ?????????????????????????????????????????????
                StringUtil.isEmptyWithTrim(members)) {
            return false;
        }
        Account account = dao.getExistedById(Account.class, accountId);
        if (roles != null && (!roles.isEmpty())) {
            Set<Integer> myRoles = dao.getAllProjectRoles(account, projectId);
            for (Integer role : roles) {
                if (myRoles.contains(role)) {
                    return true;
                }
            }
        }
        if (!StringUtil.isEmptyWithTrim(members)) {
            String[] userNames = members.split(",");
            for (String userName : userNames) {
                if (userName == null) {
                    continue;
                }
                if (userName.equals(account.userName)) {
                    return true;
                }
            }
        }
        return isCompanySuperBoss(account);
    }

    /**
     * ?????????????????????????????????????????????
     */
    public List<File> getFileChildren(int parentId) {
        FileQuery query = new FileQuery();
        query.parentId = parentId;
        query.isDelete = false;
        return dao.getAll(query);
    }

    public String getFilePath(File file, int parentId) {
        if (parentId == 0) {
            return getFilePath(file, null);
        }
        File parent = dao.getExistedById(File.class, parentId);
        return getFilePath(file, parent);
    }

    public String getFilePath(File file, File parent) {
        String parentPath = "";
        if (parent != null && parent.path != null) {
            parentPath = parent.path;
        }
        return parentPath + "/" + file.name;
    }

    public int getFileLevel(int parentId) {
        File parent = null;
        if (parentId > 0) {
            parent = dao.getExistedById(File.class, parentId);
        }
        return getFileLevel(parent);
    }

    public int getFileLevel(File parent) {
        if (parent == null) {
            return 1;
        }
        return parent.level + 1;
    }

    //
    public void checkFilePath(File bean) {
        if (dao.existedFilePath(bean)) {
            throw new IllegalArgumentException("?????????????????????");
        }
    }

    //
    public void checkParentFile(File parent, int projectId) {
        if (parent.isDelete || (!parent.isDirectory) || parent.projectId != projectId) {
            throw new AppException("??????????????????");
        }
    }

    // ???????????????
    public String getFileName(String name) {
        if (StringUtil.isEmptyWithTrim(name)) {
            throw new AppException("?????????????????????");
        }
        name = name.trim();
        if (name.contains("/")) {
            throw new AppException("?????????????????????/");
        }
        return name;
    }

    //
    public void updateCompanyLicenseInfo(Company old, String license) {
        try {
            if (StringUtil.isEmpty(license)) {
                old.status = Company.STATUS_?????????;
                old.maxMemberNum = 20;
            } else {
                License liceneBean = LicenseUtil.decryptByPublicKey(license);
                old.license = license;
                old.dueDate = liceneBean.expireDate;
                old.maxMemberNum = liceneBean.maxUserNum;
//                old.name = liceneBean.companyName;
                old.licenseId = liceneBean.id;
                old.moduleList = BizUtil.convert(liceneBean.moduleList);
                if (old.dueDate.before(new Date())) {
                    old.status = Company.STATUS_?????????;
                } else {
                    old.status = Company.STATUS_?????????;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            old.status = Company.STATUS_?????????;
            old.maxMemberNum = 20;
        }
        dao.update(old);
    }

    //
    public int doRunSystemHook(Account account, SystemHook old) {
        if (old.isDelete) {
            return 0;
        }
        if (account != null) {
            checkPermission(account, old.companyId);
        }
        // ProjectPipelineRunLog runLog=new ProjectPipelineRunLog();
        // runLog.projectId=old.projectId;
        // runLog.pipelineId=old.id;
        // runLog.script=old.script;
        // runLog.stepInfo=new ArrayList<>();
        // if(account!=null) {
        // runLog.createAccountId=account.id;
        // }
        // runLog.status=ProjectPipelineRunLog.STATUS_????????????;
        // runLog.startTime=new Date();
        // dao.add(runLog);
        //
        // old.runId=runLog.id;
        old.lastRunTime = new Date();
        if (account != null) {
            old.lastRunAccountId = account.id;
        }
        old.nextRunTime = BizUtil.nextRunTime(old.lastRunTime, old.cron);
        old.runCount++;
        dao.update(old);
        //
        // try {
        // CornerstoneThreadFactory.execute(()->{
        // JavaScriptPipeline javaScriptPipeline=new
        // JavaScriptPipeline(runLog,runLog.script);
        // javaScriptPipeline.run(ConstDefine.GLOBAL_KEY);
        // });
        // } catch (Exception e) {
        // logger.error(e.getMessage(),e);
        // }
        //
        // ProjectPipelineSimpleInfo simpleInfo=ProjectPipelineSimpleInfo.create(old);
        // addChangeLog(account, old.projectId, 0, ChangeLog.TYPE_??????PIPELINE,
        // JSONUtil.toJson(simpleInfo));
        //
        // return runLog.id;
        return 0;
    }

    //
    public TaskDetailInfo createTask(Account account, TaskDetailInfo bean) {
        return createTask(account, bean, true, false);
    }

    //
    public TaskDetailInfo createTask(Account account, TaskDetailInfo bean, boolean needCheckCustomerFiled, boolean isImportCreate) {
        if (!isImportCreate) {
            preCreateTask(account, bean);
        }
        TaskDetailInfo task = doCreateTask(account, bean, needCheckCustomerFiled, isImportCreate);
        if (!isImportCreate) {
            postCreateTask(account, task);
        }
        return task;
    }

    private void preCreateTask(Account account, TaskDetailInfo task) {
        String functionName = SystemHook.FUNCTION_PRE_CREATE_TASK;
        SystemHookContext ctx = new SystemHookContext();
        ctx.account = account;
        ctx.task = task;
        runSystemHook(account.companyId, functionName, ctx);
    }

    private void postCreateTask(Account account, TaskDetailInfo task) {
        String functionName = SystemHook.FUNCTION_POST_CREATE_TASK;
        SystemHookContext ctx = new SystemHookContext();
        ctx.account = account;
        ctx.task = task;
        runSystemHook(account.companyId, functionName, ctx);
    }

    private void runSystemHook(int companyId, String functionName, SystemHookContext ctx) {
        List<SystemHook> systemHooks = dao.getSystemHooksByFunctionName(companyId, functionName);
        for (SystemHook e : systemHooks) {
            if (logger.isInfoEnabled()) {
                logger.info("runSystemHook systemHookId:{}", e.id);
            }
            JavaScriptSystemHook jssk = new JavaScriptSystemHook(e.script);
            jssk.run(ConstDefine.GLOBAL_KEY, functionName, ctx);
        }
    }

    public void checkDataDictValueValid(String categoryCode, int value, String errorMsg) {
        BizTaskJobs.checkDataDictValueValid(categoryCode, value, errorMsg);
    }

    // ??????????????????????????????
    public void checkProjectId(int projectId, int projectId2, String errorMsg) {
        if (projectId != projectId2) {
            throw new AppException(errorMsg);
        }
    }

    /**
     * ???????????????????????????
     */
    @Deprecated
    public void checkDateInIterationRange(int iterationId, Date date, String dateStr) {
        if (iterationId == 0) {
            return;
        }
        if (date == null) {
            return;
        }
        ProjectIteration iteration = dao.getById(ProjectIteration.class, iterationId);
        if (iteration == null) {
            return;
        }
        if (iteration.startDate == null || iteration.endDate == null) {
            return;
        }
        //??????
//		String period = " [" + DateUtil.formatDate(iteration.startDate, "yyyy-MM-dd") + "-"
//				+ DateUtil.formatDate(iteration.endDate, "yyyy-MM-dd") + "]";
//		if (iteration.startDate.after(date) || iteration.endDate.before(date)) {
//			throw new AppException(dateStr + "??????????????????" + period + "???");
//		}
    }

    public void checkTaskStartEndDate(Date startDate, Date endDate) {
        if (endDate != null && startDate != null) {
            startDate = DateUtil.getBeginOfDay(startDate);// ???????????????
            endDate = DateUtil.getBeginOfDay(endDate);// ???????????????
            if (endDate.before(startDate)) {
                throw new AppException("????????????????????????????????????");
            }
        }
    }

    public String addOwnerAccountList(TaskInfo bean) {

        if (bean.ownerAccountIdList == null || bean.ownerAccountIdList.isEmpty()) {
            bean.ownerAccountList = new ArrayList<>();
            return "";
        }
        StringBuilder accountNames = new StringBuilder();
        List<AccountSimpleInfo> ownerAccountList = new ArrayList<>();
        for (Integer accountId : bean.ownerAccountIdList) {
            //???????????????????????????
            if (bean.objectType != Task.OBJECTTYPE_????????????) {
                checkIsProjectMember(accountId, bean.projectId);
            }
            AccountSimpleInfo ownerAccount = dao.getExistedAccountSimpleInfoById(accountId);
            accountNames.append(ownerAccount.name).append(",");
            ownerAccountList.add(ownerAccount);
        }
        bean.ownerAccountList = ownerAccountList;
        if (accountNames.length() > 0) {
            accountNames.deleteCharAt(accountNames.length() - 1);
        }
        //
        if (bean.ownerAccountIdList.size() > 0) {
            if (bean.firstOwner == null || bean.firstOwner.isEmpty()) {
                bean.firstOwner = bean.ownerAccountIdList;
            }

        }

        //
        return accountNames.toString();
    }

    public void updateTaskOwners(int taskId, int companyId, List<Integer> ownerAccountIdList, boolean isCreated) {
        dao.deleteTaskOwnersByTaskId(taskId);
        if (BizUtil.isNullOrEmpty(ownerAccountIdList)) {
            return;
        }
        for (Integer accountId : ownerAccountIdList) {
            TaskOwner owner = new TaskOwner();
            owner.companyId = companyId;
            owner.taskId = taskId;
            owner.accountId = accountId;
            dao.add(owner);
        }
    }

    private TaskDetailInfo doCreateTask(Account account, TaskDetailInfo bean, boolean needCheckCustomerFiled, boolean isImportCreate) {
        bean.companyId = account.companyId;
        ProjectInfo project = null;
        if (!isImportCreate) {
            project = dao.getExistedById(ProjectInfo.class, bean.projectId);
            if (project.workflowStatusType == ProjectStatusDefine.TYPE_????????????) {
                throw new AppException("?????????" + project.name + "????????????????????????????????????");
            }
            checkPermission(account, project.companyId);
            checkProjectPermission(account, project.id, Permission.ID_??????TASK + bean.objectType);
            checkDataDictValueValid("Task.objectType", bean.objectType, "??????????????????");
            bean.isFinish = false;
        }
        //
        if (!isImportCreate) {
            if (bean.status > 0) {
                ProjectStatusDefine statusDefine = dao.getById(ProjectStatusDefine.class, bean.status);
                if (statusDefine == null) {
                    throw new AppException("??????????????????");
                }
                bean.statusName = statusDefine.name;
                if (statusDefine.type == ProjectStatusDefine.TYPE_????????????) {
                    bean.isFinish = true;
                    bean.finishTime = new Date();
                } else {
                    bean.isFinish = false;
                }
                //
            } else {
                ProjectStatusDefine statusDefine = dao.getProjectStatusDefineByProjectIdObjectTypeType(bean.projectId,
                        bean.objectType, ProjectStatusDefine.TYPE_????????????);
                if (statusDefine != null) {
                    bean.status = statusDefine.id;
                    bean.statusName = statusDefine.name;
                }
            }

            if (bean.priority > 0) {
                ProjectPriorityDefine priorityDefine = dao.getById(ProjectPriorityDefine.class, bean.priority);
                if (priorityDefine == null) {
                    throw new AppException("?????????????????????");
                }
            } else {
                ProjectPriorityDefine priorityDefine = dao
                        .getDefaultProjectPriorityDefineByProjectIdObjectType(bean.projectId, bean.objectType);
                if (priorityDefine != null) {
                    bean.priority = priorityDefine.id;
                }
            }

            if (bean.iterationId != 0) {
                if (bean.objectType == Task.OBJECTTYPE_????????????) {//???????????????????????? 20200212??????
                    bean.iterationId = 0;
                } else {
                    ProjectIteration iteration = dao.getExistedById(ProjectIteration.class, bean.iterationId);
                    if (iteration.isDelete) {
                        throw new AppException("?????????????????????????????????");
                    }
                    checkProjectId(project.id, iteration.projectId, "???????????????");
                }
            }

            if (bean.releaseId > 0) {
                ProjectRelease release = dao.getExistedById(ProjectRelease.class, bean.releaseId);
                if (release.isDelete) {
                    throw new AppException("Release???????????????????????????");
                }
                checkProjectId(project.id, release.projectId, "Release?????????");
            }
            if (bean.subSystemId > 0) {
                ProjectSubSystem subSystem = dao.getExistedById(ProjectSubSystem.class, bean.subSystemId);
                if (subSystem.isDelete) {
                    throw new AppException("????????????????????????????????????");
                }
                checkProjectId(project.id, subSystem.projectId, "??????????????????");
            }

            if (bean.stageId > 0) {
                Stage stage = dao.getExistedById(Stage.class, bean.stageId);
                if (stage.isDelete) {
                    throw new AppException("?????????????????????????????????");
                }
                checkProjectId(project.id, stage.projectId, "???????????????");
            }

            if (bean.repositoryId > 0) {
                CompanyVersionRepository repository = dao.getExistedById(CompanyVersionRepository.class, bean.repositoryId);
                if (repository.isDelete) {
                    throw new AppException("?????????????????????????????????");
                }
            }

        }
        //
        if (bean.expectWorkTime > 10000000) {
            throw new AppException("???????????????????????????");
        }


        //
        ObjectType objectType = dao.getExistedById(ObjectType.class, bean.objectType);
        TaskSerialNo taskSerialNo;
        if (objectType.useDefaultSerialNo) {
            taskSerialNo = dao.getTaskSerialNoForUpdate(bean.companyId);
            taskSerialNo.serialNo++;
            dao.update(taskSerialNo);
            bean.serialNo = taskSerialNo.serialNo + "";
        } else {
            taskSerialNo = dao.getTaskSerialNoForUpdate(bean.companyId, objectType.systemName);
            taskSerialNo.serialNo++;
            dao.update(taskSerialNo);
            bean.serialNo = objectType.systemName + taskSerialNo.serialNo;
        }
        //
        bean.uuid = BizUtil.randomUUID();
        if (bean.ownerAccountIdList == null) {
            bean.ownerAccountIdList = new ArrayList<>();
            bean.ownerAccountList = new ArrayList<>();
            bean.firstOwner = new ArrayList<>();
            bean.lastOwner = new ArrayList<>();
        }
        if (bean.customFields == null) {
            bean.customFields = new HashMap<>();
        }
        if (bean.createAccountId > 0 && !isImportCreate) {
            Account createAccount = dao.getExistedById(Account.class, bean.createAccountId);
            checkIsProjectMember(createAccount.id, project.id);
        } else {
            bean.createAccountId = account.id;
        }
        bean.startDays = 0;
        bean.endDays = 0;
        bean.isCreateIndex = false;
        if (bean.endDate != null) {
            bean.endDays = DateUtil.leftDays(new Date(), bean.endDate);
        }
        if (bean.startDate != null) {
            bean.startDate = DateUtil.getBeginOfDay(bean.startDate);
//            checkDateInIterationRange(bean.iterationId, bean.startDate, "????????????");
        }
        if (bean.endDate != null) {
            bean.endDate = DateUtil.getBeginOfDay(bean.endDate);
//            checkDateInIterationRange(bean.iterationId, bean.endDate, "????????????");
        }
        checkTaskStartEndDate(bean.startDate, bean.endDate);
        bean.reopenCount = 0;
        // Category??????
        if (bean.categoryIdList == null) {
            bean.categoryIdList = new ArrayList<>();
        }
        String accountInfoList = "";
        if (!isImportCreate) {
            accountInfoList = addOwnerAccountList(bean);
        } else {
            if (!BizUtil.isNullOrEmpty(bean.ownerAccountList)) {
                StringBuilder sbr = new StringBuilder();
                for (AccountSimpleInfo accountSimpleInfo : bean.ownerAccountList) {
                    sbr.append(accountSimpleInfo.name).append(",");
                }
                accountInfoList = sbr.deleteCharAt(sbr.length() - 1).toString();
            }
        }
        if (!BizUtil.isNullOrEmpty(bean.leaderAccountIdList)) {
            bean.leaderAccountList = createAccountSimpleInfo(bean.leaderAccountIdList);
        }
        BizUtil.checkUniqueKeysOnAdd(dao, bean);
        BizUtil.checkValid(bean);
        if (needCheckCustomerFiled) {
            checkFieldValid(bean, true);
        }
        bean.subTaskCount = 0;
        bean.finishSubTaskCount = 0;

        //TODO ???????????????????????????
//        if (!isImportCreate) {
        AtomicInteger at = null;
        if (isImportCreate) {
            at = new AtomicInteger(0);
        }

        checkUniqueField(bean, at);
//        }
        if (isImportCreate) {
            //???????????????????????????????????????
            if (at.get() > 0) {
                bean.id = at.get();
                dao.update(bean);
            } else {
                dao.add(bean);
            }
        } else {
            dao.add(bean);
        }

        //
        updateTaskOwners(bean.id, bean.companyId, bean.ownerAccountIdList, true);
        // ????????????????????????
        if (bean.status > 0) {
            TaskStatusChangeLog statusChangeLog = new TaskStatusChangeLog();
            statusChangeLog.companyId = bean.companyId;
            statusChangeLog.projectId = bean.projectId;
            statusChangeLog.taskId = bean.id;
            statusChangeLog.status = bean.status;
            statusChangeLog.createAccountId = account.id;
            statusChangeLog.enterTime = new Date();
            statusChangeLog.beforeOwnerList = new ArrayList<>();
            statusChangeLog.beforeOwnerIdList = new ArrayList<>();
            statusChangeLog.afterOwnerList = bean.ownerAccountList;
            statusChangeLog.afterOwnerIdList = bean.ownerAccountIdList;
            dao.add(statusChangeLog);
        }

        // ??????????????????
        if (!BizUtil.isNullOrEmpty(bean.attachmentUuidList)) {
            for (String uuid : bean.attachmentUuidList) {
                addTaskAttachment0(account, uuid, bean.id, false);
            }
        }
        // ????????????????????????
        if (!BizUtil.isNullOrEmpty(bean.associatedIdList)) {
            addTaskAssociatedList0(account, bean.id, 0, bean.associatedIdList);
        }
        // ????????????????????????
        if (bean.objectType == Task.OBJECTTYPE_????????????) {
            if (bean.parentId > 0) {
                Task testPlan = dao.getExistedTaskByIdObjectType(bean.parentId, Task.OBJECTTYPE_????????????);
                addTestPlanTestCase(account, testPlan, bean);
            }
        }
        // ????????????
        TaskDescription description = new TaskDescription();
        description.companyId = bean.companyId;
        description.content = getContent(bean.content);
        description.taskId = bean.id;
        dao.deleteTaskDescription(bean.id);
        dao.add(description);
        //
        bean.taskDescriptionId = description.id;
//        dao.update(bean);
        dao.updateTaskDescription(bean);
        //
        if (bean.parentId > 0) {// ???????????????
            ProjectStatusDefine statusDefine = dao.getById(ProjectStatusDefine.class, bean.status);
            TaskInfo parentTask = dao.getExistedByIdForUpdate(TaskInfo.class, bean.parentId);
            //????????????????????????
            if (null != bean.startDate) {
                if (null != parentTask.startDate && bean.startDate.before(parentTask.startDate)) {
                    throw new AppException("?????????????????????????????????????????????" + parentTask.name + "??????????????????");
                }
            }
            if (null != bean.endDate) {
                if (null != parentTask.endDate && bean.endDate.after(parentTask.endDate)) {
                    throw new AppException("?????????????????????????????????????????????" + parentTask.name + "??????????????????");
                }
            }
            parentTask.subTaskCount++;
            if (statusDefine != null && statusDefine.type == ProjectStatusDefine.TYPE_????????????) {
                parentTask.finishSubTaskCount++;
            }
//            dao.update(parentTask);
            dao.updateParentTaskCount(parentTask);
            //
            TaskSimpleInfo subTask = TaskSimpleInfo.createTaskSimpleinfo(bean);
            addChangeLog(account, 0, bean.parentId, ChangeLog.TYPE_???????????????, JSONUtil.toJson(subTask));
            addChangeLog(account, bean.projectId, 0, ChangeLog.TYPE_???????????????,
                    JSONUtil.toJson(TaskSimpleInfo.createTaskSimpleinfo(parentTask)));
            Map<String, Object> map = new HashMap<>();
            map.put("subTask", subTask);
            sendNotificationForTask(account, parentTask, AccountNotificationSetting.TYPE_?????????????????????, "?????????????????????", map);
        }
        // ???????????????
        if (!BizUtil.isNullOrEmpty(bean.ownerAccountIdList)) {
            Map<String, Object> map = new HashMap<>();
            TaskSimpleInfo taskSimpleInfo;
            if (isImportCreate) {
                taskSimpleInfo = TaskSimpleInfo.createTaskSimpleinfo(bean);
            } else {
                TaskInfo old = dao.getExistedById(TaskInfo.class, bean.id);
                taskSimpleInfo = TaskSimpleInfo.createTaskSimpleinfo(old);
            }
            map.put("task", taskSimpleInfo);
            ChangeLogItem item = new ChangeLogItem();
            item.name = "?????????";
            item.afterContent = accountInfoList;
            map.put("changeLogItemList", Collections.singletonList(item));
            map.put("ownerList", item.afterContent);
            for (Integer accountId : bean.ownerAccountIdList) {
                if (accountId == account.id) {
                    continue;
                }
                addAccountNotification(accountId, AccountNotificationSetting.TYPE_????????????, bean.companyId, bean.projectId,
                        bean.id, "??????????????????", JSONUtil.toJson(map), new Date(), account);
            }
        }
        //
        addChangeLog(account, 0, bean.id, ChangeLog.TYPE_??????, "");
        ProjectModule module = dao.getProjectModuleByProjectObjectType(bean.projectId, bean.objectType);
        if (module.isPublic) {
            addChangeLog(account, bean.projectId, 0, ChangeLog.TYPE_????????????,
                    JSONUtil.toJson(TaskSimpleInfo.createTaskSimpleinfo(bean)));
        }
        //
        return bean;
    }

    //
    //
    public String getContent(String content) {
        try {
            content = HtmlUtil.cleanHtml(content);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return content;
    }

    //????????????
    private void checkEditPermission(Account account, Set<String> havePermissions, String permissionId, int projectId) {
        checkEditPermission0(account, havePermissions, permissionId, projectId, null);
    }

    //
    private void checkEditPermission0(Account account, Set<String> havePermissions, String permissionId, int projectId, String errMsg) {
        if (checkAccountSuperBossPermission(account, permissionId, projectId)) {
            return;
        }
        if (!havePermissions.contains(permissionId)) {
            logger.warn("permissionId:{} accountId:{} havePermissions:{}", permissionId, account.id,
                    DumpUtil.dump(havePermissions));

            throw new AppException(BizUtil.isNullOrEmpty(errMsg) ? ("????????????" + permissionId) : errMsg);
        }
    }

    //
    private void preUpdateTask(Account account, Task task) {
        String functionName = SystemHook.FUNCTION_PRE_UPDATE_TASK;
        SystemHookContext ctx = new SystemHookContext();
        ctx.account = account;
        ctx.task = task;
        runSystemHook(account.companyId, functionName, ctx);
    }

    private void postUpdateTask(Account account, Task task) {
        String functionName = SystemHook.FUNCTION_POST_UPDATE_TASK;
        SystemHookContext ctx = new SystemHookContext();
        ctx.account = account;
        ctx.task = task;
        runSystemHook(account.companyId, functionName, ctx);
    }

    //
    public TaskInfo updateTask0(Account account, TaskDetailInfo bean, List<String> updateFields, boolean isBatchUpdate, boolean isManualUpdate) {
        preUpdateTask(account, bean);
        TaskInfo task = doUpdateTask(account, bean, updateFields, isBatchUpdate, isManualUpdate, false);
        postUpdateTask(account, task);
        return task;
    }

    //
    private String getProjectFieldDefineField(String updateField) {
        if (updateField == null) {
            return null;
        }
        if ("iterationId".equals(updateField)) {
            return "iterationName";
        }
        if ("status".equals(updateField)) {
            return "statusName";
        }
        if ("priority".equals(updateField)) {
            return "priorityName";
        }
        if ("ownerAccountIdList".equals(updateField)) {
            return "ownerAccountName";
        }
        if ("categoryIdList".equals(updateField)) {
            return "categoryIdList";
        }
        if ("releaseId".equals(updateField)) {
            return "releaseName";
        }
        if ("subSystemId".equals(updateField)) {
            return "subSystemName";
        }
        if ("stageId".equals(updateField)) {
            return "stageName";
        }
        if ("repositoryId".equals(updateField)) {
            return "repositoryName";
        }
        return updateField;
    }

    //
    public TaskInfo doUpdateTask(Account account, TaskDetailInfo bean, List<String> updateFields,
                                 boolean isBatchUpdate, boolean isManualUpdate, boolean ignorePermission) {
        if (updateFields == null || updateFields.isEmpty()) {
            return null;
        }
        boolean updateStartEndDate = false;
        TaskInfo old = dao.getExistedByIdForUpdate(TaskInfo.class, bean.id);
        int projectId = old.projectId;
        ProjectInfo pro = dao.getExistedById(ProjectInfo.class, projectId);
        if (pro.workflowStatusType == ProjectStatusDefine.TYPE_????????????) {
            throw new AppException("?????????" + pro.name + "?????????????????????????????????");
        }
        ProjectModule module = dao.getProjectModuleByProjectObjectType(old.projectId, old.objectType);
        String permissionId = Permission.ID_??????TASK + old.objectType;
        boolean changeOwnerAccount = false;// ???????????????
        Set<String> havePermissions = new HashSet<>();
        if (!isCompanySuperBoss(account)) {
            havePermissions = getMyTaskPermission(account, old);//??????????????????????????????????????????????????????
        }
        //
        boolean changeFinish = false;
        boolean changeStatus = updateFields.contains("status");
        List<ChangeLogItem> changeLogItems = new ArrayList<>();
        for (String field : updateFields) {

            ChangeLogItem item = new ChangeLogItem();
            //
            String fieldDefineField = getProjectFieldDefineField(field);
            ProjectFieldDefine fieldDefine = dao.getProjectFieldDefineByProjectIdObjectTypeField(
                    old.projectId, old.objectType, fieldDefineField);
            if (fieldDefine != null) {
                checkSingleField(bean, fieldDefine);
                //todo??????????????????
                if (fieldDefine.isUnique) {
                    checkUniqueField(fieldDefine, bean, null);
                }
            }

            if ("name".equals(field)) {
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                item.name = "??????";
                item.beforeContent = old.name;
                old.name = bean.name;
                item.afterContent = old.name;
                addChangeLogItem(changeLogItems, item);
                old.isCreateIndex = false;
            } else if ("iterationId".equals(field)) {
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                if (old.iterationId == bean.iterationId) {
                    continue;
                }
                if (bean.iterationId < 0) {
                    bean.iterationId = 0;
                }
                item.name = "??????";
                item.beforeContent = old.iterationName;
                old.iterationId = bean.iterationId;
                if (bean.iterationId > 0) {
                    ProjectIteration iteration = dao.getExistedById(ProjectIteration.class, bean.iterationId);
                    if (iteration.isDelete) {
                        throw new AppException("??????????????????????????????");
                    }
                    item.afterContent = iteration.name;
                }
                addChangeLogItem(changeLogItems, item);
                old.isCreateIndex = false;
            } else if ("repositoryId".equals(field)) {
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                if (old.repositoryId == bean.repositoryId) {
                    continue;
                }
                if (bean.repositoryId < 0) {
                    bean.repositoryId = 0;
                }
                item.name = "??????";
                item.beforeContent = old.repositoryName;
                old.repositoryId = bean.repositoryId;
                if (bean.repositoryId > 0) {
                    CompanyVersionRepository repository = dao.getExistedById(CompanyVersionRepository.class, bean.repositoryId);
                    if (repository.isDelete) {
                        throw new AppException("???????????????????????????????????????");
                    }
                    if (repository.status == CompanyVersionRepository.STATUS_?????????) {
                        throw new AppException("????????????????????????????????????");
                    }
                    item.afterContent = repository.name;
                }
                addChangeLogItem(changeLogItems, item);
                old.isCreateIndex = false;
            } else if ("startDate".equals(field)) {
                updateStartEndDate = true;
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                    checkEditPermission0(account, havePermissions, Permission.ID_???????????????????????? + old.objectType, projectId, "????????????????????????/????????????");
                }
                if (bean.startDate != null) {
                    bean.startDate = DateUtil.getBeginOfDay(bean.startDate);
                }
                Date oldStartDate = old.startDate;
                item.name = "????????????";
                item.beforeContent = DateUtil.formatDate(old.startDate, "yyyy-MM-dd");
                old.startDate = bean.startDate;
                item.afterContent = DateUtil.formatDate(old.startDate, "yyyy-MM-dd");
                addChangeLogItem(changeLogItems, item);
                //
                calcRemindTimeWhenUpdateTask(old);
                updateTaskStartEndDate(account, old, oldStartDate, bean.startDate, "startDate", false, havePermissions, isManualUpdate);
            } else if ("endDate".equals(field)) {
                updateStartEndDate = true;
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                    checkEditPermission0(account, havePermissions, Permission.ID_???????????????????????? + old.objectType, projectId, "????????????????????????/????????????");
                }
                if (bean.endDate != null) {
                    bean.endDate = DateUtil.getBeginOfDay(bean.endDate);
                }
                Date oldEndDate = old.endDate;
                item.name = "????????????";
                item.beforeContent = DateUtil.formatDate(old.endDate, "yyyy-MM-dd");
                old.endDate = bean.endDate;
                if (old.endDate != null) {
                    old.endDays = DateUtil.leftDays(new Date(), old.endDate);
                }
                item.afterContent = DateUtil.formatDate(old.endDate, "yyyy-MM-dd");
                addChangeLogItem(changeLogItems, item);
                //
                calcRemindTimeWhenUpdateTask(old);
                updateTaskStartEndDate(account, old, oldEndDate, bean.endDate, "endDate", false, havePermissions, isManualUpdate);
            } else if ("finishTime".equals(field)) {
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                    checkEditPermission0(account, havePermissions, Permission.ID_?????????????????? + old.objectType, projectId, "??????????????????????????????");
                }
                if (bean.finishTime != null) {
                    if (null != old.startDate && DateUtil.getBeginOfDay(bean.finishTime).before(DateUtil.getBeginOfDay(old.startDate))) {
                        throw new AppException("????????????????????????????????????");
                    }
//                    if (null != old.endDate && bean.finishTime.after(DateUtil.getBeginOfDay(DateUtil.getNextDay(old.endDate, 1)))) {
//                        throw new AppException("????????????????????????????????????");
//                    }
                }
                item.name = "????????????";
                item.beforeContent = DateUtil.formatDate(old.finishTime, "yyyy-MM-dd");
                old.finishTime = bean.finishTime;
                item.afterContent = DateUtil.formatDate(old.finishTime, "yyyy-MM-dd");
                addChangeLogItem(changeLogItems, item);
            } else if ("expectEndDate".equals(field)) {
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                if (bean.expectEndDate != null) {
                    bean.expectEndDate = DateUtil.getBeginOfDay(bean.expectEndDate);
                }
                item.name = "??????????????????";
                item.beforeContent = DateUtil.formatDate(old.expectEndDate, "yyyy-MM-dd");
                old.expectEndDate = bean.expectEndDate;
                item.afterContent = DateUtil.formatDate(old.expectEndDate, "yyyy-MM-dd");
                addChangeLogItem(changeLogItems, item);
            } else if ("expectWorkTime".equals(field)) {
                if (bean.expectWorkTime < 0) {
                    throw new AppException("????????????????????????0");
                }
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                item.name = "????????????";
                item.beforeContent = old.expectWorkTime + "";
                old.expectWorkTime = bean.expectWorkTime;
                item.afterContent = old.expectWorkTime + "";
                addChangeLogItem(changeLogItems, item);
            } else if ("workLoad".equals(field)) {
                if (bean.workLoad < 0) {
                    throw new AppException("?????????????????????0");
                }
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                item.name = "?????????";
                item.beforeContent = old.workLoad + "";
                old.workLoad = bean.workLoad;
                item.afterContent = old.workLoad + "";
                addChangeLogItem(changeLogItems, item);
            } else if ("workTime".equals(field)) {
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                item.name = "??????";
                item.beforeContent = old.workTime + "";
                old.workTime = bean.workTime;
                item.afterContent = old.workTime + "";
                addChangeLogItem(changeLogItems, item);
            } else if ("status".equals(field)) {
                if (old.status == bean.status) {
                    continue;
                }
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, Permission.ID_????????????TASK + old.objectType, projectId);
                }
                item.name = "??????";
                String oldStatus = "";
                String newStatus = "";
                int oldStatusInt = 0;
                ProjectStatusDefine oldStatusDefine = null;
                List<AccountSimpleInfo> beforeOwnerAccountList = old.ownerAccountList;
                List<Integer> beforeOwnerIdList = old.ownerAccountIdList;
                if (old.status > 0) {
                    oldStatusDefine = dao.getById(ProjectStatusDefine.class, old.status);
                    if (oldStatusDefine != null) {
                        checkStatusChangePermission(account, old, oldStatusDefine);//20190923
                        oldStatusInt = oldStatusDefine.id;
                        oldStatus = oldStatusDefine.name;
                    }
                }
                ProjectStatusDefine statusDefine = dao.getById(ProjectStatusDefine.class, bean.status);
                if (statusDefine == null) {
                    throw new AppException("??????????????????");
                }
                if (oldStatusDefine != null) {//20200216??????
                    if (oldStatusDefine.transferTo == null ||
                            !oldStatusDefine.transferTo.contains(bean.status)) {
                        throw new AppException("?????????????????????????????????" + oldStatusDefine.name + "???????????????" + statusDefine.name + "???");
                    }
                }
                newStatus = statusDefine.name;
                boolean oldIsFinish = old.isFinish;
                if (statusDefine.type == ProjectStatusDefine.TYPE_????????????) {
                    old.isFinish = true;
                    old.finishTime = new Date();
                    if (null != old.startDate) {
                        old.startDays = DateUtil.differentDays(old.startDate, old.finishTime) + 1;
                    }
                } else {
                    old.isFinish = false;
//                    old.finishTime = null;
                }
                //?????????????????????????????????,??????????????????????????????
                if (statusDefine.type != ProjectStatusDefine.TYPE_????????????) {
                    TaskAssociatedQuery associatedQuery = new TaskAssociatedQuery();
                    associatedQuery.taskId = old.id;
                    associatedQuery.associatedType = TaskAssociated.ASSOCIATED_TYPE_????????????;
                    associatedQuery.pageSize = Integer.MAX_VALUE;
                    List<TaskAssociatedInfo> forwardTasks = dao.getList(associatedQuery);
                    for (TaskAssociatedInfo e : forwardTasks) {
                        if (!e.isFinish && e.statusType != 0) {
                            throw new AppException("??????" + e.objectTypeName + "???" + e.name + "???????????????");
                        }
                    }
                }
                changeFinish = (oldIsFinish != old.isFinish);
                if (oldStatusDefine != null && oldStatusDefine.type == ProjectStatusDefine.TYPE_????????????
                        && statusDefine.type != ProjectStatusDefine.TYPE_????????????) {
                    old.reopenCount++;//????????????
                }
                item.beforeContent = oldStatus;
                old.status = bean.status;
                old.statusName = newStatus;
                item.afterContent = newStatus;
                addChangeLogItem(changeLogItems, item);
                if (module.isPublic) {
                    addChangeLog(account, old.projectId, old.uuid, changeLogItems, old.name);
                }
                old.isCreateIndex = false;
                //
                setTaskOwnerWhenUpdateStatus(old, statusDefine, changeLogItems);
                //
                Date now = new Date();
                if (oldStatusDefine != null) {
                    TaskStatusChangeLog oldStatusChangeLog = dao.getTaskStatusChangeLog(bean.id, oldStatusDefine.id);
                    if (oldStatusChangeLog != null) {
                        oldStatusChangeLog.leaveTime = now;
                        oldStatusChangeLog.updateAccountId = account.id;
                        dao.update(oldStatusChangeLog);
                    }
                }
                TaskStatusChangeLog statusChangeLog = new TaskStatusChangeLog();
                statusChangeLog.projectId = old.projectId;
                statusChangeLog.taskId = bean.id;
                statusChangeLog.status = bean.status;
                statusChangeLog.oldStatus = oldStatusInt;
                statusChangeLog.createAccountId = account.id;
                statusChangeLog.enterTime = new Date();
                statusChangeLog.beforeOwnerList = beforeOwnerAccountList;
                statusChangeLog.beforeOwnerIdList = beforeOwnerIdList;
                statusChangeLog.afterOwnerList = old.ownerAccountList;
                statusChangeLog.afterOwnerIdList = old.ownerAccountIdList;
                dao.add(statusChangeLog);
            } else if ("priority".equals(field)) {
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                item.name = "?????????";
                String oldPriority = "";
                String newPriority = "";
                if (old.priority > 0) {
                    ProjectPriorityDefine priorityDefine = dao.getById(ProjectPriorityDefine.class, old.priority);
                    if (priorityDefine != null) {
                        oldPriority = priorityDefine.name;
                    }
                }
                ProjectPriorityDefine priorityDefine = dao.getById(ProjectPriorityDefine.class, bean.priority);
                if (priorityDefine == null) {
                    throw new AppException("?????????????????????");
                }
                old.priority = bean.priority;
                newPriority = priorityDefine.name;
                item.beforeContent = oldPriority;
                item.afterContent = newPriority;
                addChangeLogItem(changeLogItems, item);
                old.isCreateIndex = false;
            } else if ("ownerAccountIdList".equals(field)) {
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, Permission.ID_???????????????TASK + old.objectType, projectId);
                }
                if (BizUtil.isNullOrEmpty(old.firstOwner)) {
                    old.firstOwner = bean.ownerAccountIdList;
                }
                item.name = "?????????";
                if (old.ownerAccountList != null && old.ownerAccountList.size() > 0) {
                    item.beforeContent = BizUtil.appendFields(old.ownerAccountList, "name");
                }
                old.ownerAccountIdList = bean.ownerAccountIdList;
                updateTaskOwners(old.id, old.companyId, old.ownerAccountIdList, false);
                item.afterContent = addOwnerAccountList(old);
                changeOwnerAccount = addChangeLogItem(changeLogItems, item);
                if (changeOwnerAccount) {
                    old.isCreateIndex = false;
                }
            } else if ("categoryIdList".equals(field)) {
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                item.name = "??????";
                CategoryQuery categoryQuery = new CategoryQuery();
                if (old.categoryIdList != null && old.categoryIdList.size() > 0) {
                    categoryQuery.idInList = BizUtil.convertList(old.categoryIdList);
                    List<Category> categories = dao.getList(categoryQuery);
                    item.beforeContent = BizUtil.appendFields(categories, "name");
                }
                if (bean.categoryIdList != null && bean.categoryIdList.size() > 0) {
                    categoryQuery.idInList = BizUtil.convertList(bean.categoryIdList);
                    List<Category> categories = dao.getList(categoryQuery);
                    item.afterContent = BizUtil.appendFields(categories, "name");
                }
                old.categoryIdList = bean.categoryIdList;
                if (old.categoryIdList != null && old.categoryIdList.size() > 20) {
                    throw new AppException("????????????????????????20???");
                }
                addChangeLogItem(changeLogItems, item);
            } else if ("content".equals(field)) {//
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                ChangeLogDiff diff = new ChangeLogDiff();
                diff.companyId = old.companyId;
                diff.taskId = old.id;
                TaskDescription description = dao.getExistedByIdForUpdate(TaskDescription.class, old.taskDescriptionId);
                diff.beforeContent = BizUtil.cleanHtml(description.content);
                description.content = getContent(bean.content);
                diff.afterContent = BizUtil.cleanHtml(description.content);
                dao.update(description);
                //
                if (!BizUtil.equalString(diff.beforeContent, diff.afterContent)) {
                    dao.add(diff);
                    addChangeLog(account, 0, bean.id, ChangeLog.TYPE_??????????????????, JSONUtil.toJson(diff.id));
                }
                old.isCreateIndex = false;
            } else if ("releaseId".equals(field)) {// release
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                item.name = "Release";
                String beforeReleaseName = "";
                String afterReleaseName = "";
                if (old.releaseId > 0) {
                    ProjectRelease oldProjectRelease = dao.getExistedById(ProjectRelease.class, old.releaseId);
                    beforeReleaseName = oldProjectRelease.name;
                }
                if (bean.releaseId > 0) {
                    ProjectRelease release = dao.getExistedById(ProjectRelease.class, bean.releaseId);
                    if (release.isDelete) {
                        throw new AppException("Release????????????????????????");
                    }
                    checkProjectId(old.projectId, release.projectId, "release?????????");
                    afterReleaseName = release.name;
                }
                old.releaseId = bean.releaseId;
                item.beforeContent = beforeReleaseName;
                item.afterContent = afterReleaseName;
                addChangeLogItem(changeLogItems, item);
            } else if ("subSystemId".equals(field)) {// ?????????
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                item.name = "?????????";
                String beforeSubSystemName = "";
                String afterSubSystemName = "";
                if (old.subSystemId > 0) {
                    ProjectSubSystem oldProjectSubSystem = dao.getExistedById(ProjectSubSystem.class, old.subSystemId);
                    beforeSubSystemName = oldProjectSubSystem.name;
                }
                if (bean.subSystemId > 0) {
                    ProjectSubSystem subSystem = dao.getExistedById(ProjectSubSystem.class, bean.subSystemId);
                    if (subSystem.isDelete) {
                        throw new AppException("?????????????????????????????????");
                    }
                    checkProjectId(old.projectId, subSystem.projectId, "??????????????????");
                    afterSubSystemName = subSystem.name;
                }
                old.subSystemId = bean.subSystemId;
                item.beforeContent = beforeSubSystemName;
                item.afterContent = afterSubSystemName;
                addChangeLogItem(changeLogItems, item);
            } else if ("stageId".equals(field)) {// ?????????
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                item.name = "??????";
                String beforeStageName = old.stageName;
                String afterStageName = "";
                if (old.stageId > 0) {
                    Stage oldStage = dao.getExistedById(Stage.class, old.stageId);
                    beforeStageName = oldStage.name;
                }
                if (bean.stageId > 0) {
                    Stage stage = dao.getExistedById(Stage.class, bean.stageId);
                    if (stage.isDelete) {
                        throw new AppException("?????????????????????????????????");
                    }
                    checkProjectId(old.projectId, stage.projectId, "???????????????");
                    afterStageName = stage.name;
                }
                if (old.stageId > 0) {
                    dao.deleteStageTaskAssociate(old.stageId, old.id);
                }
                if (bean.stageId > 0) {
                    StageAssociate associate = new StageAssociate();
                    associate.stageId = bean.stageId;
                    associate.associateId = old.id;
                    associate.type = StageAssociate.TYPE_??????;
                    associate.projectId = old.projectId;
                    associate.companyId = old.companyId;
                    associate.createAccountId = account.id;
                    dao.add(associate);
                }

                old.stageId = bean.stageId;
                item.beforeContent = beforeStageName;
                item.afterContent = afterStageName;
                addChangeLogItem(changeLogItems, item);
            } else if ("progress".equals(field)) {// ??????
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                if (bean.progress < 0 || bean.progress > 100) {
                    throw new AppException("??????????????????0-100?????????");
                }
                item.name = "??????";
                String beforeProgress = old.progress + "";
                String afterProgress = bean.progress + "";
                old.progress = bean.progress;
                item.beforeContent = beforeProgress;
                item.afterContent = afterProgress;
                addChangeLogItem(changeLogItems, item);
            } else if ("leaderAccountIdList".equals(field)) {// ????????????
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                String beforeLeaderName = "";
                String afterLeaderName = "";
                item.name = "????????????";
                if (!BizUtil.isNullOrEmpty(old.leaderAccountList)) {
                    beforeLeaderName = Joiner.on(" ").join(old.leaderAccountList.stream().map(k -> k.name).collect(toList()));
                }
                if (!BizUtil.isNullOrEmpty(bean.leaderAccountIdList)) {
                    old.leaderAccountList = createAccountSimpleInfo(bean.leaderAccountIdList);
                    afterLeaderName = Joiner.on(" ").join(old.leaderAccountList.stream().map(k -> k.name).collect(toList()));
                } else {
                    old.ownerAccountList = new ArrayList<>();
                }

                old.leaderAccountIdList = bean.leaderAccountIdList;
                item.beforeContent = beforeLeaderName;
                item.afterContent = afterLeaderName;
                addChangeLogItem(changeLogItems, item);
            } else if (field.startsWith("field_")) {// ???????????????
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                item.name = fieldDefine.name;
                if (bean.customFields == null) {
                    bean.customFields = new LinkedHashMap<>();
                }
                Object newValue = bean.customFields.get(field);
                if (old.customFields == null) {
                    old.customFields = new LinkedHashMap<>();
                }
                Object oldValue = old.customFields.get(field);
                old.customFields.put(field, newValue);
                if (fieldDefine.type == ProjectFieldDefine.TYPE_??????????????????) {
                    item.beforeContent = BizUtil.getCustomeAccountNames(old, fieldDefine.id);
                } else if (fieldDefine.type == ProjectFieldDefine.TYPE_??????) {
                    if (fieldDefine.showTimeField) {
                        item.beforeContent = BizUtil.getCustomeDateTimeString(oldValue);
                        item.afterContent = BizUtil.getCustomeDateTimeString(newValue);
                    } else {
                        item.beforeContent = BizUtil.getCustomeDateString(oldValue);
                        item.afterContent = BizUtil.getCustomeDateString(newValue);
                    }
                } else if (fieldDefine.type == ProjectFieldDefine.TYPE_?????????) {
                    item.beforeContent = BizUtil.getCheckBoxValues(oldValue);
                    item.afterContent = BizUtil.getCheckBoxValues(newValue);
                } else {
                    item.beforeContent = BizUtil.toString(oldValue);
                    item.afterContent = BizUtil.toString(newValue);
                }
                checkFieldValid(old, false);// ??????
                if (fieldDefine.type == ProjectFieldDefine.TYPE_??????????????????) {
                    item.afterContent = BizUtil.getCustomeAccountNames(old, fieldDefine.id);
                }
                addChangeLogItem(changeLogItems, item);
            } else {
                throw new AppException("???????????????" + field);
            }
        }
        if (!updateStartEndDate) {
//            checkTaskStartEndDate(old.startDate, old.endDate);
            old.updateAccountId = account.id;
            BizUtil.checkValid(old);
            dao.update(old);
        }
        //
        if (old.parentId > 0 && changeFinish) {// ???????????????
            Task parentTask = dao.getExistedByIdForUpdate(Task.class, old.parentId);
            parentTask.finishSubTaskCount = dao.calcFinishSubTaskCount(old.parentId);
            dao.update(parentTask);
        }
        //
        if (changeLogItems.size() > 0 && !updateStartEndDate) {
            ChangeLog changeLog = dao.getLastChangeLogByTaskIdCreateAccountId(old.id, account.id);
            long now = System.currentTimeMillis();
            if (changeLog == null || changeLog.type != ChangeLog.TYPE_????????????
                    || (now - changeLog.createTime.getTime() > 5 * 60 * 1000)) {
                addChangeLog(account, 0, old.id, ChangeLog.TYPE_????????????,
                        JSONUtil.toJson(changeLogItems));
            } else {
                List<ChangeLogItem> oldItems = JSONUtil.fromJsonList(changeLog.items, ChangeLogItem.class);
                oldItems.addAll(changeLogItems);
                changeLog.items = JSONUtil.toJson(oldItems);
                dao.update(changeLog);
            }
            //
            sendNotificationAfterUpdateTask(account, old, changeLogItems, changeOwnerAccount, false, changeStatus);
        }
        //
        return old;
    }

    //2020.7.28 ??????????????????????????????????????? ?????????????????? ????????????????????????
    @Deprecated
    private void updatePostTask(Account account, TaskInfo task, Date oldEndDate, Date newEndDate) {
        if (oldEndDate == null || newEndDate == null) {
            return;
        }
        Date beforeEndDate = DateUtil.getBeginOfDay(oldEndDate);
        Date afterEndDate = DateUtil.getBeginOfDay(newEndDate);
        int diff = DateUtil.differentDays(beforeEndDate, afterEndDate);
        if (diff <= 0) {//????????????
            return;
        }
        TaskAssociatedQuery query = new TaskAssociatedQuery();
        query.pageSize = 10000;
        query.taskId = task.id;
        query.associatedType = TaskAssociated.ASSOCIATED_TYPE_????????????;
        List<TaskAssociated> list = dao.getList(query);
        for (TaskAssociated e : list) {
            TaskDetailInfo after = dao.getById(TaskDetailInfo.class, e.associatedTaskId);
            if (after == null || after.isDelete || after.isFinish || after.endDate == null) {
                continue;
            }
            after.endDate = DateUtil.getNextDay(after.endDate, diff);
            logger.info("beforeTask:{} oldEndDate:{} newEndDate:{} "
                            + "afterTask:{} endDate:{}",
                    task.id, oldEndDate, newEndDate, after.id, after.endDate);
            doUpdateTask(account, after, Arrays.asList("endDate"), false, true, false);
        }
    }


    private void checkTaskStartEndDate(Task task) {
        if (null != task.startDate && null != task.endDate) {
            if (task.startDate.after(task.endDate)) {
                throw new AppException("?????????" + task.name + "?????????????????????????????????????????????");
            }
        }
    }

    /**
     * ???????????????????????????????????????????????????
     */
    private void checkSubPubTaskDate(Task subTask, Task pubTask) {
        if (null == subTask || null == pubTask) {
            return;
        }
        if (subTask.isDelete || subTask.isFinish || pubTask.isFinish || pubTask.isDelete) {
            logger.info("sub name:{},{},{},pub name:{},{},{}", subTask.name, subTask.isDelete, subTask.isFinish, pubTask.name, pubTask.isDelete, pubTask.isFinish);
        }
        if (null != subTask.startDate && null != pubTask.startDate) {
            if (subTask.startDate.before(pubTask.startDate)) {
                throw new AppException("?????????" + subTask.name + "?????????????????????????????????????????????" + pubTask.name + "??????????????????");
            }
        }
        if (null != subTask.endDate && null != pubTask.endDate) {
            if (subTask.endDate.after(pubTask.endDate)) {
                throw new AppException("?????????" + subTask.name + "?????????????????????????????????????????????" + pubTask.name + "??????????????????");
            }
        }

    }


    /**
     * 2020.7.28????????????????????????????????????????????????????????????????????? ????????????????????????
     *
     * @param account         ?????????
     * @param task            ??????
     * @param oldDate         ???????????????
     * @param newDate         ????????????
     * @param dateField       ????????????
     * @param finalUpdate     ??????????????????????????????????????????????????????????????????????????????
     * @param havePermissions ????????????
     * @param isManualUpdate  ????????????????????????????????????????????????????????????????????????
     */
    private void updateTaskStartEndDate(Account account, TaskInfo task, Date oldDate, Date newDate,
                                        String dateField, boolean finalUpdate, Set<String> havePermissions, boolean isManualUpdate) {
        if (oldDate == null && newDate == null) {
            return;
        }
        //????????????????????????????????????
        Date beforeDate = DateUtil.getBeginOfDay(oldDate);
        Date afterDate = DateUtil.getBeginOfDay(newDate);
        int diff = 0;
        if (null != oldDate && null != newDate) {
            diff = DateUtil.differentDays(beforeDate, afterDate);
            if (diff == 0) {
                return;
            }
        }
        String permissionId = Permission.ID_??????TASK + task.objectType;
        if (finalUpdate) {
            havePermissions.addAll(getMyTaskPermission(account, task));
        }
        checkEditPermission(account, havePermissions, permissionId, task.projectId);
        checkEditPermission0(account, havePermissions, Permission.ID_???????????????????????? + task.objectType, task.projectId, "???????????????????????????" + task.name + "????????????/????????????");
        List<TaskAssociated> taskAssociatedList = dao.getTaskAssociatedTaskList(task.id);
        boolean hasPreTask = !BizUtil.isNullOrEmpty(taskAssociatedList) && taskAssociatedList.stream().anyMatch(k -> k.associatedType == TaskAssociated.ASSOCIATED_TYPE_????????????);
        boolean hasNextTask = !BizUtil.isNullOrEmpty(taskAssociatedList) && taskAssociatedList.stream().anyMatch(k -> k.associatedType == TaskAssociated.ASSOCIATED_TYPE_????????????);
        if (!hasPreTask && !hasNextTask && task.subTaskCount == task.finishSubTaskCount) {
            isManualUpdate = true;
        }
        //1.???????????????????????? 2.??????????????????????????????????????? 3??????????????????????????????????????????
        if ("startDate".equalsIgnoreCase(dateField) && !finalUpdate) {
            task.startDate = newDate;
            if (!isManualUpdate) {
                Date oldTaskEndDate = task.endDate;
                if (null != task.endDate) {
                    task.endDate = DateUtil.getNextDay(task.endDate, diff);
//                    if (task.startDate.after(task.endDate)) {
//                        task.endDate = task.startDate;
//
//                    }
                } else {
                    task.endDate = task.startDate;
                }
                checkTaskStartEndDate(task);
                //??????????????????
                if (diff < 0) {
                    TaskAssociatedQuery query = new TaskAssociatedQuery();
                    query.pageSize = 10000;
                    query.taskId = task.id;
                    query.associatedType = TaskAssociated.ASSOCIATED_TYPE_????????????;
                    List<TaskAssociated> taskAssociateds = dao.getList(query);
                    if (!BizUtil.isNullOrEmpty(taskAssociateds)) {
                        for (TaskAssociated taskAssociated : taskAssociateds) {
                            TaskInfo associateTask = dao.getById(TaskInfo.class, taskAssociated.associatedTaskId);
                            if (associateTask == null || associateTask.isDelete || associateTask.isFinish || associateTask.endDate == null) {
                                continue;
                            }
                            if (!task.startDate.after(associateTask.endDate)) {
                                throw new AppException("?????????" + task.name + "?????????????????????????????????????????????" + associateTask.name + "??????????????????");
                            }
                        }
                    }
                }
                if (null != task.endDate && oldTaskEndDate != task.endDate) {
                    updateTaskDate(account, task, "endDate", task.endDate, oldTaskEndDate);
                }
            }
            if (task.parentId > 0) {
                Task parentTask = dao.getExistedById(Task.class, task.parentId);
                checkSubPubTaskDate(task, parentTask);
            }
            updateTaskDate(account, task, "startDate", newDate, oldDate);
//            updateTaskStartEndDate(account, task, oldTaskEndDate, task.endDate, "endDate", true);

        } else if ("endDate".equalsIgnoreCase(dateField) && !finalUpdate) {
            task.endDate = newDate;
            checkTaskStartEndDate(task);
            if (task.parentId > 0) {
                Task parentTask = dao.getExistedById(Task.class, task.parentId);
                checkSubPubTaskDate(task, parentTask);
            }
            updateTaskDate(account, task, "endDate", newDate, oldDate);

        }

        if (!isManualUpdate) {
            TaskAssociatedQuery query = new TaskAssociatedQuery();
            query.pageSize = 10000;
            query.taskId = task.id;
            query.associatedType = TaskAssociated.ASSOCIATED_TYPE_????????????;
            List<TaskAssociated> taskAssociateds = dao.getList(query);
            if (!BizUtil.isNullOrEmpty(taskAssociateds)) {
                for (TaskAssociated associated : taskAssociateds) {
                    TaskInfo associateTask = dao.getById(TaskInfo.class, associated.associatedTaskId);
                    if (associateTask == null || associateTask.isDelete || associateTask.isFinish || associateTask.endDate == null) {
                        continue;
                    }
                    Date oldStartDate = associateTask.startDate;
                    if (null == task.endDate) {
                        if (null != oldStartDate) {
//                            if(oldStartDate.after(task.endDate)){
//                                associateTask.startDate = DateUtil.getNextDay(task.endDate, 1);
//                            }
                            Date d0 = DateUtil.getNextDay(oldStartDate, diff);
                            Date d1 = DateUtil.getNextDay(task.startDate, 1);
                            associateTask.startDate = DateUtil.max(d0, d1);
                        } else {
                            associateTask.startDate = DateUtil.getNextDay(task.startDate, 1);
                        }
                    } else {
                        if (null != oldStartDate) {
                            if (!oldStartDate.after(task.endDate)) {
                                associateTask.startDate = DateUtil.getNextDay(oldStartDate, diff);
                            }
                        } else {
                            associateTask.startDate = DateUtil.getNextDay(task.endDate, 1);
                        }
                    }

                    Date oldEndDate = associateTask.endDate;
                    if (null != oldEndDate && null != oldStartDate) {
//                        if (associateTask.startDate.after(associateTask.endDate)) {
//                            associateTask.endDate = associateTask.startDate;
//                        }
                        associateTask.endDate = DateUtil.getNextDay(oldEndDate, DateUtil.differentDays(oldStartDate, associateTask.startDate));
                    }

                    checkTaskStartEndDate(associateTask);
                    if (associateTask.parentId > 0) {
                        Task associateTaskParnet = dao.getExistedById(Task.class, associateTask.parentId);
                        checkSubPubTaskDate(associateTask, associateTaskParnet);
                    }
                    if (!DateUtil.isSameDay(associateTask.startDate, oldStartDate)) {
                        updateTaskDate(account, associateTask, "startDate", associateTask.startDate, oldStartDate);
                    }
                    if (null != oldEndDate && oldEndDate != associateTask.endDate) {
                        updateTaskDate(account, associateTask, "endDate", associateTask.endDate, oldEndDate);
                        havePermissions.addAll(getMyTaskPermission(account, associateTask));
                        permissionId = Permission.ID_??????TASK + associateTask.objectType;
                        checkEditPermission(account, havePermissions, permissionId, task.projectId);
                        checkEditPermission0(account, havePermissions, Permission.ID_???????????????????????? + associateTask.objectType, task.projectId, "???????????????????????????" + associateTask.name + "????????????/????????????");
                        updateTaskStartEndDate(account, associateTask, oldStartDate, associateTask.startDate, "startDate", true, havePermissions, isManualUpdate);
                    }
                }
            }
        }
        if (task.subTaskCount > 0) {
            List<TaskInfo> subTaskList = dao.getSubTaskListById(task.id);
            if (!BizUtil.isNullOrEmpty(subTaskList)) {
                if (!isManualUpdate) {
                    //???????????????????????????????????????????????????
                    Object[] taskIds = subTaskList.stream().map(k -> k.id).toArray();
                    List<TaskAssociated> tas = dao.getTaskAssociatedByTaskIds(taskIds);
                    if (!BizUtil.isNullOrEmpty(tas)) {
                        Map<Integer, List<Integer>> beforemap =
                                tas.stream().filter(k -> k.associatedType == TaskAssociated.ASSOCIATED_TYPE_????????????)
                                        .collect(Collectors.groupingBy(k -> k.taskId, mapping(v -> v.associatedTaskId, toList())));
                        subTaskList.sort((a, b) -> {
                            List<Integer> befores = beforemap.get(a.id);
                            if (!BizUtil.isNullOrEmpty(befores) && befores.contains(b.id)) {
                                return 1;
                            }
                            return 0;
                        });
                    }
                }
                for (TaskInfo subTask : subTaskList) {
                    if (subTask.isDelete || subTask.isFinish) {
                        continue;
                    }
                    Date oldStartDate = subTask.startDate;
                    if (null == subTask.startDate) {
                        //??????????????????
                        Date last = dao.getPreTaskLastEndData(subTask.id);
                        if (null != last) {
                            subTask.startDate = DateUtil.getNextDay(last, 1);
                        } else {
                            subTask.startDate = task.startDate;
                        }
                    } else {
                        if (null != task.startDate && subTask.startDate.before(task.startDate)) {
                            subTask.startDate = task.startDate;
                        }
                    }
                    Date oldEndDate = subTask.endDate;
                    if (null == oldEndDate) {
                        subTask.endDate = subTask.startDate;
                    } else {
                        if (null != subTask.startDate && oldEndDate.before(subTask.startDate)) {
                            subTask.endDate = subTask.startDate;
                        }
                    }

                    if (null != subTask.endDate && null != task.endDate) {
                        if (subTask.endDate.after(task.endDate)) {
                            subTask.endDate = task.endDate;
                        }
                    }
                    if (null != task.startDate) {
                        if (subTask.startDate.before(task.startDate)) {
                            subTask.startDate = task.startDate;
                        }
                    }

                    if (null != subTask.startDate && null != subTask.endDate) {
                        if (subTask.startDate.after(subTask.endDate)) {
                            subTask.startDate = subTask.endDate;
                        }
                    }

                    checkTaskStartEndDate(subTask);
                    checkSubPubTaskDate(subTask, task);

                    if (!isManualUpdate) {
                        //??????????????????
                        TaskAssociatedQuery query = new TaskAssociatedQuery();
                        query.pageSize = 10000;
                        query.taskId = subTask.id;
                        query.associatedType = TaskAssociated.ASSOCIATED_TYPE_????????????;
                        List<TaskAssociated> taskAssociateds = dao.getList(query);
                        if (!BizUtil.isNullOrEmpty(taskAssociateds)) {
                            for (TaskAssociated associated : taskAssociateds) {
                                TaskInfo associateTask = dao.getById(TaskInfo.class, associated.associatedTaskId);
                                if (associateTask == null || associateTask.isDelete || associateTask.isFinish || associateTask.endDate == null) {
                                    continue;
                                }
                                if (!subTask.startDate.after(associateTask.endDate)) {
                                    throw new AppException("???????????????" + subTask.name + "??????????????????????????????????????????????????????" + associateTask.name + "??????????????????");
                                }
                            }
                        }

                        if (!DateUtil.isSameDay(subTask.startDate, oldStartDate)) {
                            updateTaskDate(account, subTask, "startDate", subTask.startDate, oldStartDate);
                        }
                        if (null != subTask.endDate) {
                            updateTaskDate(account, subTask, "endDate", subTask.endDate, oldEndDate);
                            havePermissions.addAll(getMyTaskPermission(account, subTask));
                            permissionId = Permission.ID_??????TASK + subTask.objectType;
                            checkEditPermission(account, havePermissions, permissionId, task.projectId);
                            checkEditPermission0(account, havePermissions, Permission.ID_???????????????????????? + subTask.objectType, task.projectId, "???????????????????????????" + subTask.name + "????????????/????????????");
                            updateTaskStartEndDate(account, subTask, oldStartDate, subTask.startDate, "startDate", true, havePermissions, isManualUpdate);
                        }
                    }
                }
            }
        }
    }

    /**
     * ?????????????????????????????????
     */
    private void updateTaskDate(Account account, TaskInfo task, String dateField, Date dateValue, Date oldDateValue) {
        if (DateUtil.isSameDay(dateValue, oldDateValue)) {
            return;
        }
        logger.info("------>update task :{},#{} date field:{},old:{},new:{}", task.name, task.id, dateField, oldDateValue, dateValue);
        dao.updateTaskDateLonely(task, dateField, dateValue);
        ChangeLogItem item = new ChangeLogItem();
        item.name = "startDate".equalsIgnoreCase(dateField) ? "????????????" : "????????????";
        item.beforeContent = DateUtil.formatDate(oldDateValue, "yyyy-MM-dd");
        item.afterContent = DateUtil.formatDate(dateValue, "yyyy-MM-dd");
        item.createTime = new Date();
        ChangeLog changeLog = dao.getLastChangeLogByTaskIdCreateAccountId(task.id, account.id);
        long now = System.currentTimeMillis();
        if (changeLog == null || changeLog.type != ChangeLog.TYPE_????????????
                || (now - changeLog.createTime.getTime() > 5 * 60 * 1000)) {
            addChangeLog(account, 0, task.id, ChangeLog.TYPE_????????????,
                    JSONUtil.toJson(Collections.singletonList(item)));
        } else {
            List<ChangeLogItem> oldItems = JSONUtil.fromJsonList(changeLog.items, ChangeLogItem.class);
            oldItems.add(item);
            changeLog.items = JSONUtil.toJson(oldItems);
            dao.update(changeLog);
        }
        sendNotificationAfterUpdateTask(account, task, Collections.singletonList(item), false, false, false);
    }

    private void checkStatusChangePermission(Account account, TaskInfo task, ProjectStatusDefine statusDefine) {
        if (account == null || statusDefine == null || task == null) {
            return;
        }
        if (statusDefine.permissionOwnerList == null || statusDefine.permissionOwnerList.size() == 0) {
            return;
        }
        for (String owner : statusDefine.permissionOwnerList) {
            if ("creater".equals(owner)) {
                if (account.id == task.createAccountId) {//??????????????????
                    return;
                }
            } else if ("owner".equals(owner)) {
                if (BizUtil.contains(task.ownerAccountIdList, account.id)) {//??????????????????
                    return;
                }
            } else if (owner.startsWith("role_")) {// t_role
                int roleId = Integer.parseInt(owner.substring(owner.lastIndexOf("_") + 1));
                Set<Integer> accountIds = getAccountIdListByProjectIdRoleId(task.projectId, roleId);
                if (BizUtil.contains(accountIds, account.id)) {//???????????????
                    return;
                }
            } else if (owner.startsWith("member_")) {// t_account
                int accountId = Integer.parseInt(owner.substring(owner.lastIndexOf("_") + 1));
                if (account.id == accountId) {//???????????????
                    return;
                }
            }
        }
        throw new AppException("?????????????????????" + task.objectTypeName + "?????????" + task.statusName + "???");
    }

    // ????????????????????????????????????
    public void sendNotificationAfterUpdateTask(Account optAccount, TaskInfo task,
                                                List<ChangeLogItem> changeLogItemList, boolean changeOwnerAccount, boolean isDelete, boolean changeStatus) {
        Set<Integer> accountIds = getTaskNotificationAccountIds(task, optAccount.id);
        if (accountIds.isEmpty()) {
            return;
        }
        TaskSimpleInfo bean = TaskSimpleInfo.createTaskSimpleinfo(task);
        Map<String, Object> map = new HashMap<>();
        map.put("task", bean);
        map.put("changeLogItemList", changeLogItemList);

        for (Integer accountId : accountIds) {
            if (changeOwnerAccount) {
                for (ChangeLogItem item : changeLogItemList) {
                    if ("?????????".equals(item.name)) {
                        map.put("ownerList", item.afterContent);
                        break;
                    }
                }
                addAccountNotification(accountId, AccountNotificationSetting.TYPE_????????????, task.companyId, task.projectId,
                        task.id, "??????????????????", JSONUtil.toJson(map), new Date(), optAccount);
            } else {
                if (isDelete) {
                    addAccountNotification(accountId, AccountNotificationSetting.TYPE_????????????, task.companyId,
                            task.projectId, task.id, "??????????????????", JSONUtil.toJson(map), new Date(), optAccount);
                } else {
                    addAccountNotification(accountId, AccountNotificationSetting.TYPE_??????????????????, task.companyId,
                            task.projectId, task.id, "??????????????????", JSONUtil.toJson(map), new Date(), optAccount);
                }
            }
        }

        //2021???5???1??? ??????????????????????????????????????????????????????
        if (changeStatus) {
            TaskAssociatedQuery tq = new TaskAssociatedQuery();
            tq.taskId = task.id;
            setupQuery(optAccount, tq);
            List<TaskAssociatedInfo> associatedInfos = dao.getList(tq);
            List<Integer> sendAccount = new ArrayList<>();
            if (!BizUtil.isNullOrEmpty(associatedInfos)) {
                associatedInfos.forEach(associateTask -> {
                    List<Integer> owners = associateTask.ownerAccountIdList;
                    if (!BizUtil.isNullOrEmpty(owners)) {
                        for (Integer ownerId : owners) {
                            if (sendAccount.contains(ownerId)) {
                                continue;
                            }
                            sendAccount.add(ownerId);
                            addAccountNotification(ownerId, AccountNotificationSetting.TYPE_??????????????????, task.companyId,
                                    task.projectId, task.id, "???????????????????????????????????????????????????" + task.statusName, JSONUtil.toJson(map), new Date(), optAccount);
                        }
                    }
                });

            }
        }
    }

    //????????????????????????
    private void checkFieldListValid(Task task, List<Integer> checkFieldList) {
        if (checkFieldList == null || checkFieldList.size() == 0) {
            return;
        }
        for (Integer fieldDefineId : checkFieldList) {
            ProjectFieldDefine e = dao.getCheckProjectFieldDefine(task.projectId, task.objectType, fieldDefineId);
            //bug fix ????????????????????????checkFieldList,??????????????????ID?????????
//            dao.getById(ProjectFieldDefine.class, fieldDefineId);
            if (e == null) {
                logger.warn("ProjectFieldDefine not found fieldDefineId:{}", fieldDefineId);
                continue;
            }
            TaskFieldValue fieldValue = getTaskFieldValue(task, e);
            if (BizUtil.isNullOrEmpty(fieldValue.value)) {
                logger.error("checkFieldListValid field:{} value:{}", e.field, DumpUtil.dump(fieldValue));
                throw new AppException("?????????" + e.name + "???????????????");
            }
            if (fieldValue.field != null) {//?????????????????????????????????0
                Class<?> fieldType = fieldValue.field.getType();
                if (fieldType.equals(int.class) ||
                        fieldType.equals(short.class) ||
                        fieldType.equals(long.class)) {
                    long lvalue = Long.parseLong(fieldValue.value.toString());
                    if (lvalue <= 0) {
                        logger.error("checkFieldListValid field:{} value:{}<=0 lvalue:{}",
                                e.field, DumpUtil.dump(fieldValue), lvalue);
                        throw new AppException("?????????" + e.name + "???????????????");
                    }
                }
            }
        }
    }

    // ??????????????????????????????
    private void setTaskOwnerWhenUpdateStatus(TaskInfo old, ProjectStatusDefine statusDefine,
                                              List<ChangeLogItem> changeLogItems) {
        if (statusDefine == null) {
            return;
        }
        //????????????????????????
        checkFieldListValid(old, statusDefine.checkFieldList);
        logger.info("setTaskOwnerWhenUpdateStatus:{},statusDefine:{}", old.id, DumpUtil.dump(statusDefine));
        // owner creater lastOwner firstOwner emptyOwner role_ member_
        List<Integer> newLastOwner = old.ownerAccountIdList;
        if (statusDefine.setOwnerList != null && statusDefine.setOwnerList.size() > 0) {
            Set<Integer> newOwnerIdList = new LinkedHashSet<>();
            for (String newOwner : statusDefine.setOwnerList) {
                if ("creater".equals(newOwner)) {
                    newOwnerIdList.add(old.createAccountId);
                } else if ("owner".equals(newOwner)) {
                    if (old.ownerAccountIdList != null) {
                        newOwnerIdList.addAll(old.ownerAccountIdList);
                    }
                    //??????????????????
                } else if ("firstOwner".equals(newOwner)) {
                    if (!BizUtil.isNullOrEmpty(old.firstOwner)) {
                        newOwnerIdList.addAll(old.firstOwner);
                    }
                    //???????????????????????????
                } else if ("lastOwner".equals(newOwner)) {
                    if (!BizUtil.isNullOrEmpty(old.lastOwner)) {
                        newOwnerIdList.addAll(old.lastOwner);
                    }
                } else if (newOwner.startsWith("role_")) {// t_role
                    int roleId = Integer.parseInt(newOwner.substring(newOwner.lastIndexOf("_") + 1));
                    Set<Integer> accountIds = getAccountIdListByProjectIdRoleId(old.projectId, roleId);
                    newOwnerIdList.addAll(accountIds);
                } else if (newOwner.startsWith("member_")) {// t_account
                    int accountId = Integer.parseInt(newOwner.substring(newOwner.lastIndexOf("_") + 1));
                    newOwnerIdList.add(accountId);
                }
            }
            if (statusDefine.setOwnerList.contains("emptyOwner")) {
                newOwnerIdList = new HashSet<>();
            }
            //
            logger.info("setTaskOwnerWhenUpdateStatus:newOwnerIdList:{}", DumpUtil.dump(newOwnerIdList));


            ChangeLogItem item = new ChangeLogItem();
            item.name = "?????????";
            if (old.ownerAccountList != null && old.ownerAccountList.size() > 0) {
                item.beforeContent = BizUtil.appendFields(old.ownerAccountList, "name");
            }
            old.ownerAccountIdList = BizUtil.convert(newOwnerIdList);

            item.afterContent = addOwnerAccountList(old);
            updateTaskOwners(old.id, old.companyId, old.ownerAccountIdList, false);

            addChangeLogItem(changeLogItems, item);
            old.isCreateIndex = false;

        }

        if (!BizUtil.isNullOrEmpty(newLastOwner)) {
            old.lastOwner = new ArrayList<>(newLastOwner);
        } else {
            old.lastOwner = null;
        }
    }

    private void checkSingleField(Task task, ProjectFieldDefine e) {
        if ((!e.isShow) || (!e.isRequired)) {
            return;
        }
        if (e.isSystemField) {//????????????
            if ("createAccountName".equals(e.field)) {
                return;
            } else if ("name".equals(e.field)) {
                if (StringUtil.isEmptyWithTrim(task.name)) {
                    throwRequiredException(e);
                }
            } else if ("statusName".equals(e.field)) {
                if (task.status == 0) {
                    throwRequiredException(e);
                }
            } else if ("priorityName".equals(e.field)) {
                if (task.priority == 0) {
                    throwRequiredException(e);
                }
            } else if ("stageName".equals(e.field)) {
                if (task.stageId == 0) {
                    throwRequiredException(e);
                }
            } else if ("iterationName".equals(e.field)) {
                if (task.iterationId == 0) {
                    throwRequiredException(e);
                }
            } else if ("categoryIdList".equals(e.field)) {
                if (task.categoryIdList == null || task.categoryIdList.isEmpty()) {
                    throwRequiredException(e);
                }
            } else if ("ownerAccountName".equals(e.field)) {
                if (task.ownerAccountIdList == null || task.ownerAccountIdList.isEmpty()) {
                    throwRequiredException(e);
                }
            } else if ("startDate".equals(e.field)) {
                if (task.startDate == null) {
                    throwRequiredException(e);
                }
            } else if ("endDate".equals(e.field)) {
                if (task.endDate == null) {
                    throwRequiredException(e);
                }
            } else if ("expectWorkTime".equals(e.field)) {
                if (task.expectWorkTime <= 0) {
                    throwRequiredException(e);
                }
            } else if ("releaseName".equals(e.field)) {
                if (task.releaseId <= 0) {
                    throwRequiredException(e);
                }
            } else if ("subSystemName".equals(e.field)) {
                if (task.subSystemId <= 0) {
                    throwRequiredException(e);
                }
            }
        } else {//???????????????
            String key = BizUtil.getCustomerFieldKey(e.id);
            Object value = null;
            if (task.customFields != null) {
                value = task.customFields.get(key);
            }
            if (BizUtil.isNullOrEmpty(value)) {
                throwRequiredException(e);
            }
        }
    }

    private void throwRequiredException(ProjectFieldDefine e) {
        logger.error("checkSingleFileld ProjectFieldDefineInfo:{}", DumpUtil.dump(e));
        throw new AppException("???" + e.name + "?????????????????????????????????");
    }

    private TaskFieldValue getTaskFieldValue(Task bean, ProjectFieldDefine e) {
        TaskFieldValue tfv = new TaskFieldValue();
        if (e.field.equals("ownerAccountName")) {
            e.field = "ownerAccountIdList";
        }
        if (!e.field.startsWith("field_")) {//??????????????????
            Field field = BizUtil.getField(bean, e.field);
            if (field != null) {
                tfv.value = BizUtil.getFieldValue(bean, field);
            }
            tfv.field = field;
        } else {
            String key = BizUtil.getCustomerFieldKey(e.id);
            tfv.value = bean.customFields.get(key);
        }
        return tfv;
    }

    /**
     * ?????????????????????
     */
    @SuppressWarnings("unchecked")
    private void checkFieldValid(TaskInfo bean, boolean isAdd) {
        List<ProjectFieldDefineInfo> fieldDefines = dao.getProjectFieldDefineInfoList(bean.projectId, bean.objectType);
        Map<String, Object> customFields = new LinkedHashMap<>();
        //??????????????????????????????
        for (ProjectFieldDefineInfo e : fieldDefines) {
            addOrUpdateProjectFieldDefine(customFields, bean, e);
        }
        for (ProjectFieldDefineInfo e : fieldDefines) {
            TaskFieldValue tfv = getTaskFieldValue(bean, e);
            Object value = tfv.value;
            if (isAdd) {
                checkSingleField(bean, e);
            }


            String key = BizUtil.getCustomerFieldKey(e.id);
            if (!e.isSystemField) {
                //????????????????????????????????????????????????
                if (bean.objectType == Task.OBJECTTYPE_???????????? && e.isPsField) {
                    customFields.put(key, value);
                    continue;
                }
                if (value != null) {
                    if (e.type == ProjectFieldDefineInfo.TYPE_??????) {
                        try {
                            if (NumberUtils.isNumber(value.toString())) {
                                new Date((long) value);
                            } else {
                                Date v = DateUtil.parseDateTimeFromExcel(value.toString());
                                if (null == v) {
                                    throw new AppException("?????????" + e.name + "???????????????");
                                }
                            }
                        } catch (Exception ex) {
                            logger.error("date field error value:" + value);
                            throw new AppException("?????????" + e.name + "???????????????");
                        }
                    }
                    if (e.type == ProjectFieldDefineInfo.TYPE_??????) {
                        try {
                            Double.valueOf(value.toString());
                        } catch (Exception ex) {
                            logger.error("number field error value:" + value);
                            throw new AppException("?????????" + e.name + "???????????????");
                        }
                    }
                    if (e.type == ProjectFieldDefineInfo.TYPE_?????????) {
                        if (e.valueRange != null) {
                            if (!BizUtil.contains(e.valueRange, value.toString().trim())) {
                                logger.error("????????? field error value:{},range:{}", value, DumpUtil.dump(e.valueRange));
                                throw new AppException("?????????" + e.name + "??????????????????????????????????????????");
                            }
                        }
                    }
                    if (e.type == ProjectFieldDefineInfo.TYPE_?????????) {
                        List<String> vl;
                        if (value instanceof List) {
                            vl = (List<String>) value;
                        } else if (value instanceof Set) {
                            vl = BizUtil.convert((Set<String>) value);
                        } else {
                            vl = JSONUtil.fromJsonList(value.toString(), String.class);
                        }
                        if (CollUtil.isNotEmpty(e.valueRange)) {
                            for (String v : vl) {
                                if (StrUtil.isBlank(v)) {
                                    continue;
                                }
                                if (!BizUtil.contains(e.valueRange, v.trim())) {
                                    logger.error("????????? field error v:{} value:{},range:{}", v, value, DumpUtil.dump(e.valueRange));
                                    throw new AppException("?????????" + e.name + "??????????????????????????????????????????");
                                }
                            }
                        }
                    }
                }
                customFields.put(key, value);
            }
        }
        bean.customFields = customFields;
    }

    private void addOrUpdateProjectFieldDefine(Map<String, Object> customFields, TaskInfo bean, ProjectFieldDefine fieldDefine) {
        if (bean.customFields == null) {
            bean.customFields = new LinkedHashMap<>();
        }
        String key = BizUtil.getCustomerFieldKey(fieldDefine.id);
        Object value = bean.customFields.get(key);
        if (fieldDefine.type == ProjectFieldDefine.TYPE_??????????????????) {
            List<AccountSimpleInfo> accountSimpleInfos = new ArrayList<>();
            String objectKey = BizUtil.getCustomerFieldKeyForObject(fieldDefine.id);
            List<Integer> ids = BizUtil.getIntegerList(value);
            for (Integer accountId : ids) {
                AccountSimpleInfo accountSimpleInfo = dao.getExistedAccountSimpleInfoById(accountId);
                accountSimpleInfos.add(accountSimpleInfo);
            }
            customFields.put(objectKey, accountSimpleInfos);
        }
    }

    public void addTestPlanTestCase(Account account, Task testPlan, Task testCase) {
        if (testCase.projectId != testPlan.projectId) {
            throw new AppException("??????????????????" + testCase.name + "??????????????????");
        }
        TestPlanTestCase bean = dao.getTestPlanTestCase(testPlan.id, testCase.id);
        if (bean != null) {
            return;
        }
        bean = new TestPlanTestCase();
        bean.companyId = testPlan.companyId;
        bean.projectId = testPlan.projectId;
        bean.testPlanId = testPlan.id;
        bean.testCaseId = testCase.id;
        bean.status = TestPlanTestCase.STATUS_?????????;
        bean.createAccountId = account.id;
        dao.add(bean);
    }

    public void addTaskAttachment0(Account account, String uuid, int taskId, boolean isWiki) {
        TaskInfo task = dao.getExistedById(TaskInfo.class, taskId);
        checkPermission(account, task.companyId);

        checkProjectPermission(account, task.projectId, "task_add_attachment_" + task.objectType);
        //????????????
        if (!isWiki) {
            Attachment attachment = dao.getAttachmentByUuid(uuid);
            checkPermission(account, attachment.companyId);
            AttachmentAssociated bean = dao.getAttachmentAssociatedByTaskIdAttachmentId(taskId, attachment.id);
            if (bean != null) {
                return;
            }
            bean = new AttachmentAssociated();
            bean.companyId = account.companyId;
            bean.taskId = task.id;
            bean.projectId = task.projectId;
            bean.attachmentId = attachment.id;
            dao.add(bean);
            //
            addChangeLog(account, 0, //????????????
                    taskId, ChangeLog.TYPE_????????????, JSONUtil.toJson(attachment));
            addChangeLog(account, task.projectId, //????????????
                    0, ChangeLog.TYPE_????????????,
                    toSimpleJson(task));
            Map<String, Object> map = new HashMap<>();
            map.put("attachment", attachment);
            sendNotificationForTask(account, task, AccountNotificationSetting.TYPE_??????????????????, "??????????????????", map);
            addOptLog(account, bean.id, attachment.name,
                    OptLog.EVENT_ID_??????????????????, "??????:" + attachment.name);
        } else {
            WikiPage wikiPage = dao.getWikiPageDetailInfoByUuid(uuid);
            checkPermission(account, wikiPage.companyId);
            WikiAssociated bean = dao.getWikiAssociatedByTaskIdAttachmentId(wikiPage.id, taskId);
            if (bean != null) {
                return;
            }
            bean = new WikiAssociated();
            bean.companyId = account.companyId;
            bean.taskId = task.id;
            bean.projectId = task.projectId;
            bean.wikiPageId = wikiPage.id;
            dao.add(bean);
            //
            addChangeLog(account, 0, //????????????
                    taskId, ChangeLog.TYPE_??????wiki??????, JSONUtil.toJson(wikiPage));
            addChangeLog(account, task.projectId, //????????????
                    0, ChangeLog.TYPE_??????wiki??????,
                    toSimpleJson(task));
            Map<String, Object> map = new HashMap<>();
            map.put("attachment", wikiPage);
            sendNotificationForTask(account, task, AccountNotificationSetting.TYPE_????????????WIKI, "????????????WIKI", map);
            addOptLog(account, bean.id, wikiPage.name,
                    OptLog.EVENT_ID_????????????WIKI, "??????:" + wikiPage.name);
        }


    }

    public boolean addChangeLogItem(List<ChangeLogItem> list, ChangeLogItem item) {
        if (!BizUtil.equalString(item.beforeContent, item.afterContent)) {
            list.add(item);
            return true;
        }
        return false;
    }

    public void addTaskAssociatedList0(Account account, int taskId, int type, List<Integer> associatedTasks) {
        TaskInfo task = dao.getExistedById(TaskInfo.class, taskId);
        LinkedHashSet<Integer> associatedTaskIds = BizUtil.convertLinkedHashSet(associatedTasks);
        if (associatedTaskIds == null) {
            return;
        }
        for (Integer associatedTaskId : associatedTaskIds) {
            TaskInfo associatedTask = dao.getExistedById(TaskInfo.class, associatedTaskId);
            addTaskAssociated(account, task, type, associatedTask);
        }
    }

    public void addTaskAssociated(Account account, TaskInfo task, int associatedType, TaskInfo associatedTask) {
        //checkProjectId(task.projectId, associatedTask.projectId, "???????????????");
        if (associatedTask.id == task.id) {
            return;
        }
        if (task.companyId != associatedTask.companyId) {//???????????????
            throw new AppException("????????????");
        }
        if (task.isDelete || associatedTask.isDelete) {
            throw new AppException("??????????????????");
        }
        int oppType = 0;
        if (associatedType == TaskAssociated.ASSOCIATED_TYPE_????????????) {
            oppType = TaskAssociated.ASSOCIATED_TYPE_????????????;
            if (null == task.startDate && null != associatedTask.endDate) {
                task.startDate = DateUtil.getNextDay(associatedTask.endDate, 1);
                dao.updateSpecialFields(task, "startDate");
            }
        }
        if (associatedType == TaskAssociated.ASSOCIATED_TYPE_????????????) {
            oppType = TaskAssociated.ASSOCIATED_TYPE_????????????;
            if (null == associatedTask.startDate && null != task.endDate) {
                associatedTask.startDate = DateUtil.getNextDay(task.endDate, 1);
                dao.updateSpecialFields(associatedTask, "startDate");
            }
        }
        if (task.objectType == Task.OBJECTTYPE_???????????? && associatedTask.objectType == Task.OBJECTTYPE_????????????) {
            addTestPlanTestCase(account, task, associatedTask);
        } else {
            TaskAssociated bean = dao.getTaskAssociated(task.id, associatedTask.id);
            if (bean != null) {
                throw new AppException("????????????????????????.???" + associatedTask.name + "???");
            }
            bean = new TaskAssociated();
            bean.companyId = task.companyId;
            bean.taskId = task.id;
            bean.associatedTaskId = associatedTask.id;
            bean.associatedType = associatedType;
            dao.add(bean);
            //
            try {//?????????????????????
                bean = new TaskAssociated();
                bean.companyId = task.companyId;
                bean.associatedTaskId = task.id;
                bean.taskId = associatedTask.id;
                bean.associatedType = oppType;
                dao.add(bean);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        //??????????????????
        task.associateCount += 1;
        associatedTask.associateCount += 1;
        dao.updateSpecialFields(task, "associateCount");
        dao.updateSpecialFields(associatedTask, "associateCount");


        //?????????????????????????????????????????????
        if (associatedType == TaskAssociated.ASSOCIATED_TYPE_????????????) {
            if (null != associatedTask.endDate) {
                if (null != task.startDate) {
                    if (task.startDate.before(associatedTask.endDate)) {
                        throw new AppException("?????????????????????" + associatedTask.name + "???????????????????????????????????????" + task.name + "??????????????????");
                    }
                }
            }
        }

        if (associatedType == TaskAssociated.ASSOCIATED_TYPE_????????????) {
            if (null != associatedTask.startDate) {
                if (null != task.endDate && task.endDate.after(associatedTask.startDate)) {
                    throw new AppException("?????????????????????" + associatedTask.name + "???????????????????????????????????????" + task.name + "??????????????????");
                }
            }
        }

        //????????????????????????
        int changeLogType = ChangeLog.TYPE_??????????????????;
        String notificationName = "????????????????????????";

        int associtedChangeLogType = ChangeLog.TYPE_???????????????;
        String associtedNotificationName = "???????????????????????????";

        if (associatedType == TaskAssociated.ASSOCIATED_TYPE_????????????) {
            changeLogType = ChangeLog.TYPE_??????????????????;
            notificationName = "????????????????????????";
            associtedChangeLogType = ChangeLog.TYPE_????????????????????????;
            associtedNotificationName = "??????????????????????????????";
        }


        if (associatedType == TaskAssociated.ASSOCIATED_TYPE_????????????) {
            changeLogType = ChangeLog.TYPE_??????????????????;
            notificationName = "????????????????????????";
            associtedChangeLogType = ChangeLog.TYPE_????????????????????????;
            associtedNotificationName = "??????????????????????????????";
        }

        TaskSimpleInfo associatedSimpleTask = TaskSimpleInfo.createTaskSimpleinfo(associatedTask);
        addChangeLog(account, 0, task.id, //????????????
                changeLogType,
                JSONUtil.toJson(associatedSimpleTask));
        addChangeLog(account, task.projectId, 0,//????????????
                changeLogType,
                JSONUtil.toJson(TaskSimpleInfo.createTaskSimpleinfo(task)));
        Map<String, Object> map = new HashMap<>();
        map.put("associatedTask", associatedSimpleTask);
        sendNotificationForTask(account,
                task,
                AccountNotificationSetting.TYPE_????????????????????????,
                notificationName,
                map);

        //????????????????????????????????????
        addChangeLog(account, 0, associatedTask.id, //????????????
                associtedChangeLogType,
                JSONUtil.toJson(TaskSimpleInfo.createTaskSimpleinfo(task)));
        Map<String, Object> map0 = new HashMap<>();
        map0.put("associatedTask", TaskSimpleInfo.createTaskSimpleinfo(task));
        sendNotificationForTask(account,
                associatedTask,
                AccountNotificationSetting.TYPE_???????????????????????????,
                associtedNotificationName,
                map0);
    }


    /**
     * ????????????????????????
     */
    public void dismisPubSubTask(Account account, int subTaskId) {
        TaskInfo subTask = dao.getExistedById(TaskInfo.class, subTaskId);
        if (subTask.parentId == 0) {
            return;
        }
        checkProjectPermission(account, subTask.projectId, Permission.ID_??????TASK + subTask.objectType);
        TaskInfo parentTask = dao.getExistedById(TaskInfo.class, subTask.parentId);
        checkPermissionForTask(account, parentTask);
        checkProjectPermission(account, parentTask.projectId, Permission.ID_??????TASK + parentTask.objectType);

        ProjectStatusDefine statusDefine = dao.getById(ProjectStatusDefine.class, subTask.status);
        parentTask.subTaskCount--;
        if (statusDefine != null && statusDefine.type == ProjectStatusDefine.TYPE_????????????) {
            parentTask.finishSubTaskCount--;
        }
        dao.update(parentTask);
        //
        TaskSimpleInfo subTaskSimple = TaskSimpleInfo.createTaskSimpleinfo(subTask);
        addChangeLog(account, 0, subTask.parentId, ChangeLog.TYPE_???????????????, JSONUtil.toJson(subTaskSimple));
        addChangeLog(account, subTask.projectId, 0, ChangeLog.TYPE_???????????????,
                JSONUtil.toJson(TaskSimpleInfo.createTaskSimpleinfo(parentTask)));
        Map<String, Object> map = new HashMap<>();
        map.put("subTask", subTaskSimple);
        sendNotificationForTask(account, parentTask, AccountNotificationSetting.TYPE_?????????????????????, "?????????????????????", map);
        subTask.parentId = 0;
        dao.update(subTask);
    }


    public void addTaskParentRelation(Account account, TaskInfo parentTask, TaskInfo subTask) {
        //checkProjectId(task.projectId, associatedTask.projectId, "???????????????");
        if (subTask.id == parentTask.id) {
            return;
        }
        //?????????
        if (subTask.parentId == parentTask.id) {
            return;
        }
        if (parentTask.parentId == subTask.id) {
            throw new AppException("??????????????????????????????");
        }
        if (parentTask.companyId != subTask.companyId) {//???????????????
            throw new AppException("????????????");
        }
        if (parentTask.isDelete || subTask.isDelete) {
            throw new AppException("??????????????????");
        }
        //????????????????????????
        if (subTask.parentId > 0) {
            dismisPubSubTask(account, subTask.id);
        }

        if (null != subTask.startDate) {
            if (null != parentTask.startDate && subTask.startDate.before(parentTask.startDate)) {
                throw new AppException("????????????" + subTask.name + "??????????????????????????????????????????" + parentTask.name + "??????????????????");
            }
        }

        if (null != subTask.endDate) {
            if (null != parentTask.endDate && subTask.endDate.after(parentTask.endDate)) {
                throw new AppException("????????????" + subTask.name + "??????????????????????????????????????????" + parentTask.name + "??????????????????");
            }
        }

        subTask.parentId = parentTask.id;
//        dao.update(subTask);
        dao.updateTaskParentId(subTask);

        if (parentTask.objectType == Task.OBJECTTYPE_???????????? && subTask.objectType == Task.OBJECTTYPE_????????????) {
            addTestPlanTestCase(account, parentTask, subTask);
        } else {
            ProjectStatusDefine statusDefine = dao.getById(ProjectStatusDefine.class, subTask.status);
            parentTask.subTaskCount++;
            if (statusDefine != null && statusDefine.type == ProjectStatusDefine.TYPE_????????????) {
                parentTask.finishSubTaskCount++;
            }
//            dao.update(parentTask);
            dao.updateParentTaskCount(parentTask);
            //
            TaskSimpleInfo subTaskSimple = TaskSimpleInfo.createTaskSimpleinfo(subTask);
            addChangeLog(account, 0, subTask.parentId, ChangeLog.TYPE_???????????????, JSONUtil.toJson(subTaskSimple));
            addChangeLog(account, subTask.projectId, 0, ChangeLog.TYPE_???????????????,
                    JSONUtil.toJson(TaskSimpleInfo.createTaskSimpleinfo(parentTask)));
            Map<String, Object> map = new HashMap<>();
            map.put("subTask", subTaskSimple);
            sendNotificationForTask(account, parentTask, AccountNotificationSetting.TYPE_?????????????????????, "?????????????????????", map);
        }
    }

    //
    public void calcRemindTimeWhenUpdateTask(TaskInfo task) {
        TaskRemindInfo bean = dao.getTaskRemindByTaskId(task.id);
        calcRemindTime(bean, task);
    }

    //
    public void calcRemindTime(TaskRemind bean, Task task) {
        if (bean == null) {
            return;
        }
        //????????????????????????
        dao.delete(TaskRemindTime.class, QueryWhere.create().where("task_remind_id", bean.id));
        if (bean.remindRules != null && bean.remindRules.size() > 0) {
            for (TaskRemindRule e : bean.remindRules) {
                Date remindTime = null;
                if (e.type <= 0 || e.unit <= 0) {
                    continue;
                }
                if (e.type == TaskRemindRule.TYPE_???????????????) {
                    remindTime = calcRemindTime(task.startDate, -e.value, e.unit);
                }
                if (e.type == TaskRemindRule.TYPE_???????????????) {
                    remindTime = calcRemindTime(task.startDate, e.value, e.unit);
                }
                if (e.type == TaskRemindRule.TYPE_???????????????) {
                    remindTime = calcRemindTime(task.endDate, -e.value, e.unit);
                }
                if (e.type == TaskRemindRule.TYPE_???????????????) {
                    remindTime = calcRemindTime(task.endDate, e.value, e.unit);
                }
                if (e.type == TaskRemindRule.TYPE_????????????) {
                    remindTime = new Date(e.value);
                }
                if (remindTime == null) {
                    continue;
                }
                TaskRemindTime taskRemindTime = new TaskRemindTime();
                taskRemindTime.companyId = task.companyId;
                taskRemindTime.taskRemindId = bean.id;
                taskRemindTime.remindTime = remindTime;
                dao.add(taskRemindTime);
            }
        }
    }

    //
    public Date calcRemindTime(Date date, long value, int unit) {
        if (date == null) {
            return null;
        }
        if (unit == TaskRemindRule.UNIT_??????) {
            return DateUtil.addMinute(date, (int) value);
        }
        if (unit == TaskRemindRule.UNIT_??????) {
            return DateUtil.addHour(date, (int) value);
        }
        if (unit == TaskRemindRule.UNIT_???) {
            return DateUtil.addDay(date, (int) value);
        }
        return null;
    }

    public Account getExistedAccountByUserName(String userName) {
        Account account = dao.getAccountByUserName(userName);
        if (account == null) {
            throw new AppException("???????????????" + userName);
        }
        return account;
    }

    public String getProjectPinyinShortName(String name) {
        String pinyinShortName = PinYinUtil.getHanziPinYin(name);
        if (pinyinShortName != null && pinyinShortName.length() > 128) {
            pinyinShortName = pinyinShortName.substring(0, 128);
        }
        return pinyinShortName;
    }

    /**
     * ??????????????????
     */
    public List<DepartmentSimpleInfo> getMyDepartmentList(AccountInfo account) {
        DepartmentQuery query = new DepartmentQuery();
        query.accountId = account.id;
        query.companyId = account.companyId;
        query.type = Department.TYPE_??????;
        query.idSort = Query.SORT_TYPE_ASC;
        query.pageSize = 100;
        List<DepartmentInfo> list = dao.getList(query);
        List<DepartmentSimpleInfo> result = new ArrayList<>();
        for (DepartmentInfo e : list) {
            result.add(DepartmentSimpleInfo.create(e));
        }
        return result;
    }

    public ConnectionDetailInfo getConnectionDetailInfo0(String token) {
        MachineLoginToken loginToken = dao.getMachineLoginTokenByToken(token);
        if (loginToken == null) {
            throw new AppException("TOKEN?????????");
        }
        ConnectionDetailInfo detailInfo = new ConnectionDetailInfo();
        ConnectionInfo info = null;
        MachineLoginSession session = dao.getExistedById(MachineLoginSession.class, loginToken.sessionId);
        if (session.machineId > 0) {
            Machine machine = dao.getExistedById(Machine.class, session.machineId);
            if (machine.isDelete) {
                throw new AppException("???????????????????????????");
            }
            info = BizUtil.createConnectionInfo(machine);
            info.enableInput = loginToken.type == MachineLoginToken.TYPE_??????;
        } else if (session.cmdbMachineId > 0) {
            CmdbMachine machine = dao.getExistedById(CmdbMachine.class, session.cmdbMachineId);
            if (machine.isDelete) {
                throw new AppException("???????????????????????????");
            }
            info = BizUtil.createConnectionInfo(machine);
            info.enableInput = loginToken.type == MachineLoginToken.TYPE_??????;
        }
        if (session.cmdbRobotId > 0) {
            detailInfo.robot = dao.getById(CmdbRobot.class, session.cmdbRobotId);
        }
        detailInfo.connectionInfo = info;
        return detailInfo;
    }

    //
    public static ChangeLogItemDetailInfo create(ChangeLog bean) {
        if (StringUtil.isEmpty(bean.items)) {
            return null;
        }
        ChangeLogItemDetailInfo info = new ChangeLogItemDetailInfo();
        Field[] field = ChangeLogItemDetailInfo.class.getDeclaredFields();
        for (Field f : field) {
            ChangeLogTypes types = f.getAnnotation(ChangeLogTypes.class);
            if (types == null) {
                continue;
            }
            int[] value = types.value();
            if (value.length == 0) {
                continue;
            }
            if (BizUtil.contains(value, bean.type)) {
                try {
                    f.set(info, BeanUtil.fromJson(f, bean.items));
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                break;
            }
        }
        return info;
    }

    /**
     * ?????????????????????
     */
    public boolean isNormalAccount(Account account) {
        return account.status == Account.STATUS_?????? && account.isActivated;
    }


    public Map<String, Object> getReportList(Report.ReportQuery query) {
        StringBuilder addWhereSql = new StringBuilder();
        List<Object> values = new ArrayList<>();
        if (!BizUtil.isNullOrEmpty(query.auditorIdInList)) {
            addWhereSql = new StringBuilder(" and (");
            int[] auditorIdInList = query.auditorIdInList;
            for (int i1 = 0; i1 < auditorIdInList.length; i1++) {
                int i = auditorIdInList[i1];
                addWhereSql.append("json_contains(a.auditor_ids,'").append(i).append("') ");
                if (i1 < auditorIdInList.length - 1) {
                    addWhereSql.append(" or ");
                }
//                values.add(i);
            }
            addWhereSql.append(") ");
            query.auditorIdInList = null;
        }
        return getReportList(query, addWhereSql.toString(), values.toArray(new Object[]{}), null, true, true);
    }


    public Map<String, Object> getReportList(Report.ReportQuery query, String addWhereSql, Object[] addValues, Set<String> includeField,
                                             boolean needList, boolean needCount) {
        Map<String, Object> ret = new HashMap<>();
        Class<?> domainClass = getDomainClass(query);
        //includeFields(includeField)  ???????????????
        SqlBean sqlBean = new SelectProvider(domainClass).query(query).needPaging(true).build();
        if (!BizUtil.isNullOrEmpty(addWhereSql)) {
            sqlBean.whereSql += addWhereSql;
        }
        if (!BizUtil.isNullOrEmpty(addValues)) {
            List<Object> newValues = new ArrayList<>();
            if (!BizUtil.isNullOrEmpty(sqlBean.parameters)) {
                newValues.addAll(Arrays.asList(sqlBean.parameters));
            }
            newValues.addAll(Arrays.asList(addValues));
            sqlBean.parameters = newValues.toArray();
        }
        String sql = sqlBean.toSql();
        List<?> reportList;
        if (needList) {
            reportList = dao.queryList(domainClass, sql, sqlBean.parameters);
            ret.put("list", reportList);
        }
        SqlBean countSqlBean = new SelectProvider(domainClass).query(query).
                includeFields(includeField).needOrderBy(false).selectCount().build();
        if (!BizUtil.isNullOrEmpty(addWhereSql)) {
            countSqlBean.whereSql += addWhereSql;
        }
        sql = countSqlBean.toSql();
        int count;
        if (needCount) {
            count = dao.queryCount(sql, sqlBean.parameters);
            ret.put("count", count);
        }
        return ret;
    }

    /**
     * ???????????????
     */
    public void syncTasksFromProject0(Account account, int projectId) {
        //??????????????????????????????
        ProjectQuery projectQuery = new ProjectQuery();
        projectQuery.companyId = account.companyId;
        projectQuery.status = Project.STATUS_?????????;
        projectQuery.pageSize = 10000;
        projectQuery.isTemplate = false;
        projectQuery.excludeId = projectId;
        projectQuery.idSort = Query.SORT_TYPE_ASC;
        List<ProjectInfo> projectList = dao.getList(projectQuery);
        // ???????????????????????????
        List<Integer> deleteIdList =
                projectList.stream().filter(project -> project.isDelete).map(project -> project.id)
                .collect(Collectors.toList());
        // ?????????????????????????????? ?????????????????????????????????delete
        if (CollUtil.isNotEmpty(deleteIdList)){
            dao.updateProjectTaskDeleteStatus(account.companyId, projectId, true, deleteIdList);
        }
        // ????????????????????? ??????????????????  ????????????????????????????????? ?????????
        projectList.removeIf(project -> project.isDelete);
        // ?????????????????????
        List<Integer> projectIdList =
                projectList.stream().map(project -> project.id).collect(Collectors.toList());
        List<Task> oldList
                = dao.getTaskByCompanyIdProjectIdAssociateProjectIdList(account.companyId, projectId, projectIdList);
        Map<Integer, List<Task>> oldTaskMap
                = oldList.stream().collect(Collectors.groupingBy(task -> task.associateProjectId));

        // ???????????????????????????
        List<ProjectFieldDefine> projectFieldDefineList = dao.getProjectFieldDefineByPsField(account.companyId, Task.OBJECTTYPE_????????????);
        for (ProjectInfo e : projectList) {
            // ???????????????????????????????????????
            List<Task> oldTaskList = oldTaskMap.get(e.id);
            Task old = CollUtil.isEmpty(oldTaskList) ? null : oldTaskList.get(0);
            // ?????????????????????
            //???????????????????????????????????????????????????????????????
            if (old != null) {
                old.name = e.name;
                old.startDate = e.startDate;
                //????????????????????????????????????
                if (null == old.startDate) {
                    old.startDate = e.createTime;
                }
                old.isFinish = e.isFinish;
                old.finishTime = e.finishDate;
                old.endDate = e.endDate;
                old.progress = e.progress;
                old.updateAccountId = e.updateAccountId;
                old.ownerAccountIdList = e.ownerAccountIdList;
                old.ownerAccountList = e.ownerAccountList;
                setupProjectSetCustomerFields(old, e, projectFieldDefineList);
                old.customFields.put("associateProjectUuid", e.uuid);
                dao.update(old);
                continue;
            }
            // ?????????????????????
            // ?????????????????????????????????
            TaskDetailInfo task = new TaskDetailInfo();
            task.name = e.name;
            task.companyId = account.companyId;
            task.projectId = projectId;
            task.objectType = Task.OBJECTTYPE_????????????;
            task.startDate = e.startDate;
            if (null == task.startDate) {
                task.startDate = e.createTime;
            }
            task.isFinish = e.isFinish;
            task.finishTime = e.finishDate;
            task.endDate = e.endDate;
            task.associateProjectId = e.id;
            task.progress = e.progress;
            task.ownerAccountIdList = e.ownerAccountIdList;
            task.ownerAccountList = e.ownerAccountList;
            setupProjectSetCustomerFields(task, e, projectFieldDefineList);
            createTask(account, task, false, true);
            task.createAccountId = e.createAccountId;
            //?????????????????????????????????
            task.customFields.put("associateProjectUuid", e.uuid);
            dao.update(task);
        }
    }

    /**
     * ??????????????????????????????
     */
    private void setupProjectSetCustomerFields(Task task, ProjectInfo project) {
        setupProjectSetCustomerFields(task, project, null);
    }

    /**
     * ??????????????????????????????
     */
    private void setupProjectSetCustomerFields(Task task, ProjectInfo project, List<ProjectFieldDefine> defines) {
        Map<String, Object> cs = task.customFields;
        if (null == cs) {
            cs = new HashMap<>(16);
        }
        //???????????????
        cs.put("statusColor", project.color);
        if (null == defines) {
            //???????????????????????????????????????
            defines = dao.getProjectFieldDefineByPsField(project.companyId, Task.OBJECTTYPE_????????????);
        }
        if (!BizUtil.isNullOrEmpty(defines)) {
            defines.sort(Comparator.comparing(k -> k.field));
            for (ProjectFieldDefine fd : defines) {
                if (fd.name.contains("????????????")) {
                    cs.put(fd.field, project.workflowStatusName);
                } else if (fd.name.contains("????????????")) {
                    cs.put(fd.field, project.runLogRemark);
                } else if (fd.name.contains("????????????")) {
                    if (null != project.startDate) {
                        cs.put(fd.field, DateUtil.getDayDiff(project.startDate, new Date()));
                    }
                } else if (fd.name.contains("????????????")) {
                    cs.put(fd.field, project.group);
                } else if (fd.name.contains("????????????")) {
                    cs.put(fd.field, BizTaskJobs.getDataDictName("Project.runStatus", project.runStatus));
                }
            }
        }
        task.customFields = cs;
    }

    public List<Map<String, Object>> getTaskFieldPermissionList(Account account, Task task) {
        int objectType = task.objectType;
        List<Map<String, Object>> result = new ArrayList<>();
        List<ProjectFieldDefine> fieldDefines = dao.getProjectFieldDefineList(task.projectId, task.objectType);
        Set<String> taskPermissions = getMyTaskPermission(account, task);
        if (!BizUtil.isNullOrEmpty(fieldDefines)) {
            fieldDefines.forEach(field -> {
                Map<String, Object> map = JSONUtil.fromJson(JSONUtil.toJson(field), Map.class);
                String permission;
                boolean editable = true;
                if ("startDate".equals(field.field)) {
                    permission = "task_edit_startend_date_";
                    editable = taskPermissions.contains("task_edit_" + objectType);
                } else if ("endDate".equals(field.field)) {
                    permission = "task_edit_startend_date_";
                    editable = taskPermissions.contains("task_edit_" + objectType);
                } else if ("finishTime".equals(field.field)) {
                    permission = "task_edit_finish_date_";
                } else if ("categoryIdList".equals(field.field)) {
                    permission = "task_edit_category_";
                } else if ("statusName".equals(field.field)) {
                    permission = "task_change_status_";
                } else if ("ownerAccountName".equals(field.field)) {
                    permission = "task_change_owner_";
                } else {
                    permission = "task_edit_";
                }
                map.put("editable", editable && taskPermissions.contains(permission + objectType));
                if (task.isFinish || task.isFreeze || task.isDelete) {
                    map.put("editable", false);
                }
                result.add(map);
            });
        }
        return result;
    }


    public List<Task> getSimpleTaskWorkTimeList(int companyId, List<Integer> projectIdList, int objectType, List<Integer> accountIds) {
        QueryWhere qw = QueryWhere.create().where("company_id", companyId);
        if (!BizUtil.isNullOrEmpty(projectIdList)) {
            qw.in(SelectProvider.MAIN_TABLE_ALIAS, "project_id", projectIdList.toArray());
        }
        if (objectType > 0) {
            qw.where("object_type", objectType);
        }
        if (!BizUtil.isNullOrEmpty(accountIds)) {
            StringBuilder sbr = new StringBuilder(" (");
            for (Integer accountId : accountIds) {
                sbr.append(" json_contains(owner_account_id_list,'").append(accountId).append("')").append(" or");
            }
            sbr.deleteCharAt(sbr.length() - 3);
            sbr.append(" ) ");
            qw.whereSql(sbr.toString());
        }
        return dao.getList(Task.class, qw, Sets.newHashSet("project_id", "object_type", "expect_work_time", "owner_account_id_list"));
    }


    /**
     * ????????????????????????(???????????????CS??????)
     */
    public void syncDingtalkAttendance(Date date) {
        Date dayMonth = DateUtil.getBeginOfMonth(date);
        Date theMonth = DateUtil.getBeginOfMonth();
        if (dayMonth.equals(theMonth) || dayMonth.after(theMonth)) {
            throw new AppException("???????????????????????????????????????");
        }
        //clear
        dao.deleteAttendaceByMonth(new Date(dayMonth.getTime() - 1));
        List<DingtalkMember> members = dao.getAll(DingtalkMember.class);
        if (!BizUtil.isNullOrEmpty(members)) {
            //????????????????????????50???
            int batchSize = 50;
            int total = members.size();
            int batch = (int) Math.ceil(total * 1.0f / batchSize);
            for (int i = 0; i < batch; i++) {
                List<DingtalkMember> userIdList = members.subList(i * batchSize, Math.min(total, (i + 1) * batchSize));
                List<DingtalkAttendance> attendanceList = DingtalkUtil.getAttendanceList(userIdList, date);
                logger.info("syncDingtalkAttendance---> size:{}", attendanceList.size());
                if (!BizUtil.isNullOrEmpty(attendanceList)) {
                    dao.batchInsertDingtalkAttendance(attendanceList);
                }

            }
        }
    }

    /**
     * ??????????????????
     */
    public void syncDingtalkMember() {
        List<DingtalkMember> members = DingtalkUtil.getAllDepartmentUserList();
        dao.batchInsertDingtalkMember(members);
    }

    public void syncAdAccount() {
        List<Account> accounts = LdapUtil.getAdAccounts();
        logger.info("syncAdAccount---> size:{}", accounts.size());
        if (!BizUtil.isNullOrEmpty(accounts)) {
            dao.batchUpdateAdAccount(accounts);
        }
    }

    /**
     * ???????????????????????????
     */
    public void checkUniqueField(ProjectFieldDefine bean) {
        if (!bean.isUnique) {
            return;
        }
        int companyId = bean.companyId;
        int projectId = bean.projectId;
        int objectType = bean.objectType;
        String columnName = null;
        if (bean.isSystemField) {
            columnName = "`" + TaskUtil.getColumnName(bean.field) + "`";
        } else {
            columnName = "json_extract(custom_fields,'$." + bean.field + "')";
        }
        if (bean.type == ProjectFieldDefine.TYPE_?????? && bean.isSystemField) {
            if (bean.showTimeField) {
                columnName = "DATE_FORMAT(" + columnName + ", '%Y-%m-%d %H:%i:%s')";
            } else {
                columnName = "DATE_FORMAT(" + columnName + ", '%Y-%m-%d')";
            }
        }
        String sql = "select t.fname from (select " + columnName + " as fname,count(*) as count0 from `t_task` where company_id = ? and project_id=? and object_type=? and is_delete =false  group by fname having  count0>1";
        if (bean.type == ProjectFieldDefine.TYPE_??????) {
            sql += " and fname >0) t";
        } else {
            sql += " and fname is not null) t";
        }
        List<String> ret = dao.queryForStrings(sql, companyId, projectId, objectType);
        if (!BizUtil.isNullOrEmpty(ret)) {
            logger.warn(DumpUtil.dump(ret));
            for (String s : ret) {
                if (!BizUtil.isNullOrEmpty(s)) {
                    throw new AppException("?????????" + bean.name + "??????????????????:" + s);
                }
            }
            /*ret.forEach(k -> {
                k.forEach((key, value) -> {
                    String showValue = getTaskFieldValue(bean, key);

                });
            });*/
        }
    }


    public void checkUniqueField(TaskDetailInfo taskDetailInfo, AtomicInteger at) {
        Field[] fields = taskDetailInfo.getClass().getFields();
        for (Field field : fields) {
            String defineFieldName = TaskUtil.getDefineFieldName(field.getName());
            if (BizUtil.isNullOrEmpty(defineFieldName)) {
                continue;
            }
            Object val = BizUtil.getFieldValue(taskDetailInfo, field);
            if (val instanceof Integer || val instanceof Long) {
                int v = BizUtil.null2Int(val);
                if (0 == v) {
                    continue;
                }
            }
            String fieldDefineField = getProjectFieldDefineField(field.getName());
            ProjectFieldDefine fieldDefine = dao.getProjectFieldDefineByProjectIdObjectTypeField(
                    taskDetailInfo.projectId, taskDetailInfo.objectType, fieldDefineField);
            try {
                checkUniqueField(fieldDefine, taskDetailInfo, at);
                if (null != at && at.get() > 0) {
                    return;
                }
            } catch (AppException e) {
                throw e;
            } catch (Exception e1) {
                logger.error(e1.getMessage());
            }
        }
        //???????????????
        if (!BizUtil.isNullOrEmpty(taskDetailInfo.customFields)) {
            for (Map.Entry<String, Object> entry : taskDetailInfo.customFields.entrySet()) {
                String field = entry.getKey();
                Object val = entry.getValue();
                if (val instanceof Integer) {
                    int v = BizUtil.null2Int(val);
                    if (0 == v) {
                        continue;
                    }
                }
                if (val instanceof Long) {
                    long v = BizUtil.null2Long(val);
                    if (0 == v) {
                        continue;
                    }
                }
                if (null == val) {
                    continue;
                }
                ProjectFieldDefine fieldDefine = dao.getProjectFieldDefineByProjectIdObjectTypeField(
                        taskDetailInfo.projectId, taskDetailInfo.objectType, field);
                try {
                    checkUniqueField(fieldDefine, taskDetailInfo, at);
                    if (null != at && at.get() > 0) {
                        return;
                    }
                } catch (AppException e) {
                    throw e;
                } catch (Exception e1) {
                    logger.error(e1.getMessage());
                }
            }
        }
    }


    public void checkUniqueField(ProjectFieldDefine bean, TaskDetailInfo detailInfo, AtomicInteger at) {
        if (!bean.isUnique) {
            return;
        }
        int companyId = bean.companyId;
        int projectId = bean.projectId;
        int objectType = bean.objectType;

        Object val = null;
        if (!bean.isSystemField && !BizUtil.isNullOrEmpty(detailInfo.customFields)) {
            val = detailInfo.customFields.get(bean.field);
        } else {
            val = BizUtil.getFieldValue(detailInfo, TaskUtil.getDomainFieldName(bean.field));
        }
        if (null == val) {
            return;
        }
        boolean isList = false;
        if (val instanceof List) {
            isList = true;
            val = JSONUtil.toJson(val);
        }
        if (bean.type == ProjectFieldDefine.TYPE_?????? && bean.isSystemField) {
            Date d;
            if (val instanceof Long) {
                d = new Date();
                d.setTime((long) val);
            } else if (val instanceof Date) {
                d = (Date) val;
            } else {
                throw new IllegalArgumentException("custome field:" + bean.field + "with date value:" + val + " is illegal!!!");
            }
            if (bean.showTimeField) {
                val = DateUtil.formatDate(d, "yyyy-MM-dd HH:mm");
            } else {
                val = DateUtil.formatDate(d, "yyyy-MM-dd");
            }
        }
        String columnName = null;
        if (bean.isSystemField) {
            columnName = "`" + TaskUtil.getColumnName(bean.field) + "`";
        } else {
            columnName = "json_extract(custom_fields,'$." + bean.field + "')";
        }
        if (bean.type == ProjectFieldDefine.TYPE_?????? && bean.isSystemField) {
            if (bean.showTimeField) {
                columnName = "DATE_FORMAT(" + columnName + ", '%Y-%m-%d %H:%i:%s')";
            } else {
                columnName = "DATE_FORMAT(" + columnName + ", '%Y-%m-%d')";
            }
        }

        String sql = "select id from (select id ," + columnName + " as fname from `t_task` where company_id = ? and project_id=? and object_type=? and is_delete =false";
        if (isList) {
            sql += " having concat(fname,'')=? limit 1) t";
        } else {
            sql += "  having fname =? limit 1)t";
        }
        Integer id = dao.queryForInteger(sql, companyId, projectId, objectType, val);
        if (null != id && id > 0) {
            if (null == at) {
                throw new AppException("?????????" + bean.name + "?????????????????????");
            } else {
                at.addAndGet(id);
//                = new AtomicInteger(id);
            }
        }
    }

    private String getTaskFieldValue(ProjectFieldDefine e, Object value) {
        if (e.type == ProjectFieldDefineInfo.TYPE_??????) {
            try {
                if (NumberUtils.isNumber(value.toString())) {
                    if (e.showTimeField) {
                        return DateUtil.formatDate(new Date((long) value), "yyyy-MM-dd HH:mm:ss");
                    } else {
                        return DateUtil.formatDate(new Date((long) value), "yyyy-MM-dd");
                    }
                } else {
                    Date v = DateUtil.parseDateTimeFromExcel(value.toString());
                    if (null != v) {
                        if (e.showTimeField) {
                            return DateUtil.formatDate(v, "yyyy-MM-dd HH:mm:ss");
                        } else {
                            return DateUtil.formatDate(v, "yyyy-MM-dd");
                        }
                    }
                }
            } catch (Exception ex) {
                logger.error("date field error value:" + value);
            }
        }
        return value.toString();
    }

    public List<CompanyVersionRepository.CompanyVersionRepositoryInfo> getCompanyVersionRepositoryList(int companyId) {
        CompanyVersionRepository.CompanyVersionRepositoryQuery query = new CompanyVersionRepository.CompanyVersionRepositoryQuery();
        query.isDelete = false;
        query.companyId = companyId;
        List<CompanyVersionRepository.CompanyVersionRepositoryInfo> list = dao.getAll(query);
        if (!BizUtil.isNullOrEmpty(list)) {
            for (CompanyVersionRepository.CompanyVersionRepositoryInfo repositoryInfo : list) {
                if (!BizUtil.isNullOrEmpty(repositoryInfo.ownerDepartmentList)) {
                    repositoryInfo.name = repositoryInfo.name + (" (" + repositoryInfo.ownerDepartmentList.stream().map(k -> k.name).collect(Collectors.joining("???")) + ")");
                }
            }
        }
        return list;
    }

    public Map<String, Object> getCompanyVersionRepositoryInfoList(CompanyVersionRepository.CompanyVersionRepositoryQuery query) {
        StringBuilder countSql = new StringBuilder("select count(1) ");
        StringBuilder listSql = new StringBuilder("select l1.`image_id` as `create_account_image_id`,l1.`name` as `create_account_name`," +
                "a.`company_id`,a.`name`,a.`description`,a.`is_delete`,a.`create_account_id`,a.`update_account_id`," +
                "a.`latest`,a.`owner_account_id_list`,a.`owner_account_list`,a.`business_leader`,a.`owner_department_id_list`," +
                "a.`owner_department_list`,a.`department`,a.`release_date`,a.`status`,a.`is_arch332n`,a.`arch`,a.`id`,a.`create_time`,a.`update_time`\n");

        StringBuilder sql = new StringBuilder("from t_company_version_repository a \n" +
                "left join  t_account l1 on a.`create_account_id`=l1.id\n" +
                "where 1=1  ");
        Map<String, Object> map = new HashMap<>();
        //??????????????????
        if (null != query.companyId) {
            sql.append(" and a.company_id = ").append(query.companyId);
        }
        if (null != query.status) {
            sql.append(" and a.status = ").append(query.status);
        }
        if (null != query.isDelete) {
            sql.append(" and a.is_delete = ").append(query.isDelete);
        }
        if (null != query.isArch332n) {
            sql.append(" and a.is_arch332n = ").append(query.isArch332n);
        }
        if (null != query.name) {
            sql.append(" and a.name like '%").append(query.name.trim()).append("%'");
        }
        if (null != query.businessLeader) {
            sql.append(" and a.business_leader like '%").append(query.businessLeader.trim()).append("%'");
        }
        if (!BizUtil.isNullOrEmpty(query.ownerAccountIds)) {
            sql.append(" and (");
            int[] ownerAccountIds = query.ownerAccountIds;
            for (int i = 0; i < ownerAccountIds.length; i++) {
                int id = ownerAccountIds[i];
                if (i != 0) {
                    sql.append(" or ");
                }
                sql.append("  json_contains(a.owner_account_id_list,'").append(id).append("') ");
            }
            sql.append(" ) ");
        }

        if (!BizUtil.isNullOrEmpty(query.ownerDepartmentIds)) {
            sql.append(" and (");
            int[] departmentIds = query.ownerDepartmentIds;
            for (int i = 0; i < departmentIds.length; i++) {
                int id = departmentIds[i];
                if (i != 0) {
                    sql.append(" or ");
                }
                sql.append("  json_contains(a.owner_department_id_list,'").append(id).append("') ");
            }
            sql.append(" ) ");
        }

        if (null != query.releaseDateStart) {
            sql.append(" and a.release_date >= '").append(DateUtil.formatDate(query.releaseDateStart, "yyyy-MM-dd HH:mm:ss")).append("'");
        }
        if (null != query.releaseDateEnd) {
            sql.append(" and a.release_date <='").append(DateUtil.formatDate(query.releaseDateEnd, "yyyy-MM-dd HH:mm:ss")).append("'");
        }

        sql.append(" order by  a.create_time desc");

        countSql.append(sql);
        listSql.append(sql).append(" limit ")
                .append((query.pageIndex - 1) * query.pageSize).append(",").append(query.pageSize);
        int count = dao.queryCount(countSql.toString());
        map.put("count", count);
        if (count <= 0) {
            map.put("list", Collections.emptyList());
            return map;
        }
        List<?> caseInfos = dao.queryList(CompanyVersionRepository.CompanyVersionRepositoryInfo.class, listSql.toString());
        map.put("list", caseInfos);

        return map;
    }
}
