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
     * 发送短信验证码
     */
    public void sendVerifyCode(String mobileNo) {
        if (StringUtil.isEmpty(mobileNo)) {
            throw new AppException("手机号不能为空");
        }
        if (!PatternUtil.isMobile(mobileNo)) {
            throw new AppException("手机号格式错误");
        }
        StringBuilder code = new StringBuilder();
        int[] codes = new int[]{0, 1, 2, 3, 5, 6, 7, 8, 9};
        for (int i = 0; i < 4; i++) {
            code.append(codes[RandomUtil.randomInt(codes.length)]);
        }
        //
        try {
            // 5分钟有效
            int validTime = 5 * 60;
            Date validDateTime = new Date(System.currentTimeMillis() + validTime * 1000);
            Kaptcha verfiedCode = dao.getByField(Kaptcha.class, "sign", mobileNo);
            if (verfiedCode == null) {
                verfiedCode = new Kaptcha();
                verfiedCode.type = Kaptcha.TYPE_手机验证码;
                verfiedCode.sign = mobileNo;
                verfiedCode.code = code.toString();
                verfiedCode.validTime = validDateTime;
                dao.add(verfiedCode);
            } else {
                //20200214新增
                KaptchaLog log = KaptchaLog.create(verfiedCode);
                dao.add(log);
                //
                verfiedCode.code = code.toString();
                verfiedCode.validTime = validDateTime;
                dao.update(verfiedCode);
            }
            //注意：这里一定要这样写 上面可能会报错
            String content = code + "为您的操作验证码，请于5分钟内填写。如非本人操作，请忽略本短信";
            qCloundSMSService.sendSms(mobileNo, content);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (e instanceof SQLIntegrityConstraintViolationException) {//重复添加
                throw new AppException("不能重复发送验证码");
            }
            throw new AppException("短信发送失败");
        }

    }

    /**
     * 检查非手机验证码
     */
    public void checkVerifyCode(String sign, String code) {
        if (sign == null || code == null) {
            logger.warn("sign == null || code == null", sign, code);
            throw new AppException("验证码已过期");
        }
        Kaptcha verifyCode = dao.getByFieldForUpdate(Kaptcha.class, "sign", sign);
        if (verifyCode == null) {
            logger.warn("verifyCode == null");
            throw new AppException("验证码已过期");
        }
        if ((!verifyCode.code.equalsIgnoreCase(code))) {
            logger.warn("verifyCode:{} not equal code:{}", cornerstone.biz.util.DumpUtil.dump(verifyCode), code);
            throw new AppException("验证码不正确");
        }
        Date now = new Date();
        if (verifyCode.validTime.before(now)) {
            logger.warn("verifyCode.validTime:{} now:{}", verifyCode.validTime, now);
            throw new AppException("验证码已过期");
        }
        verifyCode.validTime = new Date();//到期了
        dao.update(verifyCode);
    }

    public void checkMobileVerifyCode(Account account, String sign, String code) {
        if (sign == null || code == null) {
            updateKaptchaErrorCount(account);
            throw new AppException("验证码已过期");
        }
        Kaptcha verifyCode = dao.getByFieldForUpdate(Kaptcha.class, "sign", sign);
        if (verifyCode == null) {
            updateKaptchaErrorCount(account);
            throw new AppException("验证码已过期");
        }
        if ((!verifyCode.code.equalsIgnoreCase(code))) {
            updateKaptchaErrorCount(account);
            logger.error("verifyCode:{} not equal code:{}", DumpUtil.dump(verifyCode), code);
            throw new AppException("验证码不正确");
        }
        Date now = new Date();
        if (verifyCode.validTime.before(now)) {
            updateKaptchaErrorCount(account);
            throw new AppException("验证码已过期");
        }
        verifyCode.validTime = new Date();//到期了
        dao.update(verifyCode);
    }

    /**
     * 目前只检查手机验证码
     * 错误5次 禁用账号10分钟
     * 错误10次 禁用账号1个小时
     * 错误20次或以上 禁用账号24个小时
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
     * 检查密码是否正确
     */
    public void checkPasswordValid(Account account, String password) {
        if (StringUtil.isEmpty(password)) {
            throw new AppException("用户名或密码错误");
        }
        if (!BizUtil.encryptPassword(password).equals(account.password)) {
            throw new AppException("用户名或密码错误");
        }
    }


    /**
     * 查询项目对象字段列表
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
     * 查询项目迭代列表
     */
    public List<ProjectIterationInfo> getProjectIterationListByProjectId(int projectId) {
        ProjectIterationQuery query = new ProjectIterationQuery();
        query.projectId = projectId;
        query.isDelete = false;
        query.pageSize = Integer.MAX_VALUE;
        return dao.getList(query);
    }

    /**
     * 查询项目成员列表
     */
    public List<ProjectMemberInfo> getProjectMemberInfoListByProjectId(int projectId) {
        ProjectMemberQuery memberQuery = new ProjectMemberQuery();
        memberQuery.projectId = projectId;
        memberQuery.pageSize = Integer.MAX_VALUE;
        return dao.getList(memberQuery);
    }

    /**
     * 查询项目指定对象的工作流定义
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
            if (e.type == ProjectStatusDefineInfo.TYPE_开始状态) {
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
                if (to.type != ProjectStatusDefineInfo.TYPE_结束状态) {
                    queue.offer(to);
                    list.remove(to);
                }
            }
        }
        //
        for (ProjectStatusDefineInfo e : list) {
            if (e.type == ProjectStatusDefineInfo.TYPE_结束状态) {
                result.add(e);
            }
        }
        if (result.size() != all.size()) {
            return all;
        }
        return result;
    }

    /**
     * 查询项目对象工作流的初始状态
     */
    public int getStartProjectStatusDefineIdByProjectIdObjectType(int projectId, int objectType) {
        ProjectStatusDefineQuery query = new ProjectStatusDefineQuery();
        query.projectId = projectId;
        query.objectType = objectType;
        query.pageSize = Integer.MAX_VALUE;
        query.typeSort = Query.SORT_TYPE_ASC;
        query.type = ProjectStatusDefine.TYPE_开始状态;
        List<ProjectStatusDefineInfo> list = dao.getList(query);
        if (list.isEmpty()) {
            ObjectType ot = dao.getExistedById(ObjectType.class, objectType);
            if (ot == null) {
                throw new AppException("对象类型错误" + objectType);
            }
            throw new AppException(ot.name + "没有开始状态");
        }
        return list.get(0).id;
    }

    /**
     * 查询项目优先级定义
     */
    public List<ProjectPriorityDefineInfo> getProjectPriorityDefineInfoListByProjectIdObjectType(int projectId,
                                                                                                 int objectType) {
        return dao.getProjectPriorityDefineInfoList(projectId, objectType);
    }

    /**
     * 找出默认优先级
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
                throw new AppException("对象类型错误" + objectType);
            }
            throw new AppException(ot.name + "没有默认优先级");
        }
        return list.get(0).id;
    }

    /**
     * 查询项目release列表
     */
    public List<ProjectReleaseInfo> getProjectReleaseInfoListByProjectId(int projectId) {
        ProjectReleaseQuery query = new ProjectReleaseQuery();
        query.isDelete = false;
        query.projectId = projectId;
        query.pageSize = Integer.MAX_VALUE;
        return dao.getList(query);
    }

    /**
     * 查询项目子系统列表
     */
    public List<ProjectSubSystemInfo> getProjectSubSystemInfoListByProjectId(int projectId) {
        ProjectSubSystemQuery query = new ProjectSubSystemQuery();
        query.isDelete = false;
        query.projectId = projectId;
        query.pageSize = Integer.MAX_VALUE;
        return dao.getList(query);
    }

    /**
     * 查询项目对象分类列表
     */
    public List<CategoryInfo> getCategoryInfoList(int projectId, int objectType) {
        CategoryQuery query = new CategoryQuery();
        query.projectId = projectId;
        query.objectType = objectType;
        query.pageSize = Integer.MAX_VALUE;
        return dao.getList(query);
    }


    /**
     * 迭代下的状态关联的
     */
    public int getTotalStatusBaseTaskNumByIteration(ProjectIteration iteration, int[] objectTypeList) {
        if (BizUtil.isNullOrEmpty(objectTypeList)) {
            return 0;
        }
        TaskQuery taskQuery = new TaskQuery();
        taskQuery.iterationId = iteration.id;
        taskQuery.objectTypeInList = objectTypeList;
        // 不能加时间 否则会有产品bug
        // taskQuery.createTimeStart=iteration.startDate;
        // taskQuery.createTimeEnd=iteration.endDate;
        taskQuery.isDelete = false;
        return dao.getListCount(taskQuery);
    }

    /**
     * 查询一个迭代一个ObjetctType下的任务总数
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
     * 查询一个迭代status base的已完成任务数量
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
     * 查询一个迭代任务延迟的任务数量
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
     * 查询一个企业下开启的objectType列表(objectType>0)
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
     * 查询一个项目下启用模块的objectType列表
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
     * 查询项目成员列表
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
     * 所有我参与的运行中的项目(非模板项目)
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
        query.status = Project.STATUS_运行中;
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
        query.status = Project.STATUS_运行中;
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
     * 查询我所在的运行中的项目id列表
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
     * 所有我参与的所有项目（一个公司下）删除的不算，结束的也算
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
     * 所有我参与的进行中的项目（一个公司下）删除的不算，结束的不算
     */
    public List<ProjectInfo> getMyProjectInfoListExcludeFinished(int accountId, int companyId) {
        ProjectQuery query = new ProjectQuery();
        query.isDelete = false;
        query.status = ProjectInfo.STATUS_运行中;
        query.memberAccountId = accountId;
        query.companyId = companyId;
        query.pageSize = 10000;
        return dao.getList(query);
    }

    /**
     * 所有我的角色
     */
    public List<RoleInfo> getMyRoleInfoList(int accountId, int companyId) {
        RoleQuery query = new RoleQuery();
        query.globalAccountId = accountId;
        query.companyId = companyId;
        query.pageSize = 10000;
        return dao.getList(query);
    }

    /**
     * 我的全局角色列表
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
     * 我的企业角色列表
     */
    public List<RoleInfo> getMyCompanyRoleList(Account account) {
        RoleQuery roleQuery = new RoleQuery();
        roleQuery.globalAccountId = account.id;
        roleQuery.companyId = account.companyId;
        roleQuery.type = Role.TYPE_全局;
        roleQuery.pageSize = Integer.MAX_VALUE;
        List<RoleInfo> roleList = dao.getList(roleQuery);
        return roleList;
    }

    /**
     * 我的企业角色列表
     */
    public Set<Integer> getMyCompanyRoleIdList(int accountId, int companyId) {
        CompanyMember member = dao.getCompanyMemberInfoByCompanyIdAccountId(companyId, accountId);
        if (member == null) {
            throw new AppException("权限不足");
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
     * 通知创建人和负责人
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
     * 任务通知入库
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
        // 找到需要通知的人
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
     * 变更通知消息
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
     * 判断是否是创建人或责任人
     */
    public boolean isCreaterOrOwner(Account account, Task task) {
        if (account.id == task.createAccountId) {
            return true;
        }
        return BizUtil.contains(task.ownerAccountIdList, account.id);
    }


    /***
     * 数据权限检查
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
            throw new AppException("权限不足" + permissionId);
        }
    }

    /**
     * 检查是否有权限查看任务
     */
    public void checkTaskViewPermission(Account account, TaskInfo info) {
        if (isCompanySuperBoss(account)) {
            return;
        }
        //查看权限校验
        if (!checkTaskViewPermission(account.id, info.projectId, info.objectType)) {
            if (info.createAccountId != account.id && !info.ownerAccountIdList.contains(account.id)) {
                logger.error("account.id {} info.projectId:{}", account.id, info.projectId);
                throw new AppException("权限不足,请查看项目可见性设置");
            }
        }
        if (info.isDelete) {
            throw new AppException(info.objectTypeName + "已经被删除");
        }
        checkPermission(account, info.companyId);
    }

    /**
     * 检查是否是所在企业的成员
     */
    public void checkIsCompanyMember(int accountId, int companyId) {
        CompanyMember member = dao.getCompanyMemberByAccountIdCompanyId(accountId, companyId);
        if (member == null) {
            throw new AppException("非本企业成员");
        }
    }

    /**
     * 检查是否是企业成员
     */
    public boolean isCompanyMember(int accountId, int companyId) {
        CompanyMember member = dao.getCompanyMemberByAccountIdCompanyId(accountId, companyId);
        return member != null;
    }


    /**
     * 查询对象类型的扩展信息
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
     * 查询项目中 角色成员清单
     */
    public Set<Integer> getAccountIdListByProjectIdRoleId(int projectId, int roleId) {
        List<Integer> idList = dao.getAccountIdListByProjectIdRoleId(projectId, roleId);
        return BizUtil.convert(idList);
    }

    /**
     * 清空token
     */
    public void cleanToken(int accountId) {
        AccountToken token = dao.getAccountTokenByAccountIdForUpdate(accountId);
        if (token != null) {
            token.token = BizUtil.randomUUID();
            dao.update(token);
        }
    }

    /**
     * 添加变更日志
     */
    public ChangeLog addChangeLog(Account account, int associatedId, int type, String items) {
        return addChangeLog(account, 0, 0, associatedId, type, "", items, "");
    }

    /**
     * 注意：任务动态时projectId为0  项目动态是projectId不为0
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
        if (account != null) {// 有些场景是没有用户的，比如scm提交
            changeLog.companyId = account.companyId;
            changeLog.createAccountId = account.id;
        }
        dao.add(changeLog);
        return changeLog;
    }

    public void addChangeLog(Account account, int projectId, String itemId, List<ChangeLogItem> itemList,
                             String remark) {
        if (itemList.size() > 0) {
            addChangeLog(account, projectId, 0, 0, ChangeLog.TYPE_编辑属性, itemId, JSONUtil.toJson(itemList), remark);
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
                if (lastRunLog.status != ProjectPipelineRunLog.STATUS_执行成功
                        && lastRunLog.status != ProjectPipelineRunLog.STATUS_执行失败) {
                    logger.error("old.runId:{} lastRunLog:{}", old.runId, cornerstone.biz.util.DumpUtil.dump(lastRunLog));
                    throw new AppException("上一个任务正在执行中，不能开始新的任务");
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
        runLog.status = ProjectPipelineRunLog.STATUS_正在执行;
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
        addChangeLog(account, old.projectId, 0, ChangeLog.TYPE_执行PIPELINE, JSONUtil.toJson(simpleInfo));
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
     * 企业新增成员或更成员角色和所在组织架构
     */
    public void addOrUpdateCompanyAccount(int companyId, Account optAccount, Account account, List<Integer> roleIds,
                                          List<Integer> departmentIds) {
        int accountId = account.id;
        // 1.先删除角色
        dao.deleteAccountRolesByAccountIdCompanyId(accountId, companyId);
        Set<Integer> roleSet = BizUtil.convert(roleIds);
        for (Integer roleId : roleSet) {
            Role role = dao.getExistedById(Role.class, roleId);
            if (companyId != role.companyId) {
                throw new AppException("角色【" + role.name + "】不存在");
            }
            CompanyMemberRole bean = new CompanyMemberRole();
            bean.accountId = accountId;
            bean.companyId = companyId;
            bean.roleId = roleId;
            dao.add(bean);
        }
        // 2.再删除原来所在部门
        dao.deleteDepartmentByAccountIdCompanyId(accountId, companyId);
        int[] departmentIdList = BizUtil.convertList(departmentIds);
        List<DepartmentSimpleInfo> departmentList = new ArrayList<>();
        if (!BizUtil.isNullOrEmpty(departmentIdList)) {
            if (departmentIdList.length > 50) {//新增20190719
                throw new AppException("成员所属部门列表不能超过50个");
            }
            DepartmentQuery query = new DepartmentQuery();
            query.idInList = departmentIdList;
            query.type = Department.TYPE_组织架构;
            query.pageSize = Integer.MAX_VALUE;
            List<Department> departments = dao.getAll(query);
            for (Department department : departments) {
                if (department.companyId != companyId) {
                    throw new AppException("组织架构不存在." + department.name);
                }
                departmentList.add(DepartmentSimpleInfo.create(department));
                Department memberDepartment = new Department();
                memberDepartment.companyId = companyId;
                memberDepartment.accountId = accountId;
                memberDepartment.level = department.level + 1;
                memberDepartment.name = account.name;
                memberDepartment.parentId = department.id;
                memberDepartment.type = Department.TYPE_人员;
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
            // 公司人数+1
            Company company = dao.getExistedByIdForUpdate(Company.class, companyMember.companyId);
            company.memberNum = dao.getCurrMemberNum(company.id);
            if (company.memberNum > company.maxMemberNum) {
                throw new AppException("成员数量达到上限" + company.maxMemberNum + "，不能新增成员");
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
            query.type = Department.TYPE_组织架构;
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
        query.accountStatus = Account.STATUS_有效;
        List<CompanyMemberInfo> list = dao.getList(query);
        for (CompanyMemberInfo e : list) {
            RoleQuery roleQuery = new RoleQuery();
            roleQuery.companyId = e.companyId;
            roleQuery.type = RoleInfo.TYPE_全局;
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
            throw new AppException("文件不存在" + name);
        }
        return bean.uuid;
    }

    public void checkWeekValid(List<Long> weeks) {
        if (weeks == null) {
            return;
        }
        for (Long week : weeks) {
            if (week < 1 || week > 7) {
                throw new AppException("数据错误" + week);
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
            if (bean.period == ReportTemplate.PERIOD_日) {
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
            if (bean.period == ReportTemplate.PERIOD_周) {
                Date monday = DateUtil.getMondayOfThisWeek();
                Date friday = DateUtil.getNextDay(monday, 4);// 本周五
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
            if (bean.period == ReportTemplate.PERIOD_月) {
                Date lastDayOfMonth = DateUtil.getNextDay(DateUtil.getNextMonth(DateUtil.getBeginOfMonth(), 1), -1);// 这个月最后一天
                c.setTime(lastDayOfMonth);
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                if (c.getTime().before(new Date())) {/// 下个月最后一天
                    c.add(Calendar.MONTH, 1);
                }
                bean.reportTime = DateUtil.formatDate(c.getTime(), "yy-MM");
            }
            bean.nextRemindTime = c.getTime();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException("提醒时间格式错误");
        }
    }

    public Date getNextNotifyTime(SystemNotification bean) {
        if (bean.notifyTime == null) {
            throw new AppException("通知时间格式错误");
        }
        String[] minHour = bean.notifyTime.split(":");
        if (minHour.length != 2) {
            throw new AppException("通知时间格式错误");
        }
        Date now = new Date();
        int hour = Integer.parseInt(minHour[0]);
        int minute = Integer.parseInt(minHour[1]);
        if (hour > 24 || hour < 0) {
            throw new AppException("通知时间格式错误");
        }
        if (minute > 60 || minute < 0) {
            throw new AppException("通知时间格式错误");
        }
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        if (bean.period == SystemNotification.PERIOD_日) {
            if (bean.periodSetting != null && bean.periodSetting.size() > 0) {//指定某一天
                Date pushDate = new Date(bean.periodSetting.get(0));
                c.setTime(pushDate);
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                if (c.getTime().before(now)) {
                    throw new AppException("通知日期不能早于当前时间");
                }

            } else {
                if (c.getTime().before(new Date())) {
                    c.add(Calendar.DAY_OF_YEAR, 1);//下一天
                }
            }
            return c.getTime();
        }
        if (bean.period == SystemNotification.PERIOD_周) {
            for (int i = 0; i <= 14; i++) {//一定是14
                int week = DateUtil.getWeek(c.getTime());
                if (isSatisfy(c, bean.periodSetting, week)) {
                    return c.getTime();
                }
                c.add(Calendar.DAY_OF_YEAR, 1);
            }
        }
        //
        if (bean.period == SystemNotification.PERIOD_月) {
            for (int i = 0; i <= 33; i++) {//一定是33
                int dayOfMonth = DateUtil.getDayOfMonth(c.getTime());
                if (isSatisfy(c, bean.periodSetting, dayOfMonth)) {
                    return c.getTime();
                }
                c.add(Calendar.DAY_OF_YEAR, 1);
            }
        }
        //
        throw new AppException("通知时间格式错误");
    }

    private boolean isSatisfy(Calendar c, List<Long> periodSetting, int day) {
        Date now = new Date();
        if (!BizUtil.contains(periodSetting, day)) {
            return false;
        }
        if (DateUtil.isSameDay(now, c.getTime())) {// 如果是今天
            //满足
            //换一天
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
                if (oldObj != null) {// 编辑(判断是否一样)
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
                if (optLogField != null && (!optLogField.showValue())) {// 不显示值
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
     * 添加操作日志
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
        if (remark != null && remark.length() > 1024 * 10) {// 最大10k
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
            throw new AppException("权限不足,对象所在企业与账号登录企业不一致,请切换企业");
        }
    }

    /**
     * 判断是否是企业的创建人
     */
    public void checkIsCompanyCreater(int companyId, int accountId) {
        Company company = dao.getExistedById(Company.class, companyId);
        if (company.createAccountId != accountId) {
            logger.error("companyId:{} company.createAccountId{}!=accountId{}",
                    companyId, company.createAccountId, accountId);
            throw new AppException("权限不足");
        }
    }

    /**
     * 校验成员的企业权限
     */
    public boolean checkAccountCompanyPermission(int accountId, boolean enableRole, List<Integer> roles,
                                                 String members) {
        return checkAccountCompanyPermission(accountId, enableRole, BizUtil.convert(roles), members);
    }

    /**
     * 校验成员的企业权限
     */
    public boolean checkAccountCompanyPermission(int accountId, boolean enableRole, Set<Integer> roles,
                                                 String members) {
        if (!enableRole) {// 如果没有启用就是全部都可以
            return true;
        }
        if ((roles == null || roles.isEmpty()) && // 没有设置角色和成员，则默认都允许
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
     * 校验成员的项目权限
     */
    public boolean checkAccountProjectPermission(int accountId, int projectId, boolean enableRole, Integer createAccountId, List<Integer> roles,
                                                 String members) {
        return checkAccountProjectPermission(accountId, projectId, enableRole, createAccountId,
                BizUtil.convert(roles), members);
    }

    /**
     * 检查项目权限
     *
     * @param createAccountId 创建人
     */
    public boolean checkAccountProjectPermission(int accountId, int projectId, boolean enableRole, Integer createAccountId, Set<Integer> roles,
                                                 String members) {
        if (!enableRole) {// 如果没有启用就是全部都可以
            return true;
        }
        if (createAccountId != null && createAccountId == accountId) {//如果等于创建人则可以
            return true;
        }
        if ((roles == null || roles.isEmpty()) && // 没有设置角色和成员，则都不允许
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
     * 查询文件夹下的子文件（不递归）
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
            throw new IllegalArgumentException("文件名不能重复");
        }
    }

    //
    public void checkParentFile(File parent, int projectId) {
        if (parent.isDelete || (!parent.isDirectory) || parent.projectId != projectId) {
            throw new AppException("父目录不存在");
        }
    }

    // 检查文件名
    public String getFileName(String name) {
        if (StringUtil.isEmptyWithTrim(name)) {
            throw new AppException("文件名不能为空");
        }
        name = name.trim();
        if (name.contains("/")) {
            throw new AppException("文件名不能包含/");
        }
        return name;
    }

    //
    public void updateCompanyLicenseInfo(Company old, String license) {
        try {
            if (StringUtil.isEmpty(license)) {
                old.status = Company.STATUS_未授权;
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
                    old.status = Company.STATUS_已到期;
                } else {
                    old.status = Company.STATUS_许可中;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            old.status = Company.STATUS_未授权;
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
        // runLog.status=ProjectPipelineRunLog.STATUS_正在执行;
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
        // addChangeLog(account, old.projectId, 0, ChangeLog.TYPE_执行PIPELINE,
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

    // 检查是否是同一个项目
    public void checkProjectId(int projectId, int projectId2, String errorMsg) {
        if (projectId != projectId2) {
            throw new AppException(errorMsg);
        }
    }

    /**
     * 逻辑未进行时间校验
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
        //废弃
//		String period = " [" + DateUtil.formatDate(iteration.startDate, "yyyy-MM-dd") + "-"
//				+ DateUtil.formatDate(iteration.endDate, "yyyy-MM-dd") + "]";
//		if (iteration.startDate.after(date) || iteration.endDate.before(date)) {
//			throw new AppException(dateStr + "不在迭代周期" + period + "内");
//		}
    }

    public void checkTaskStartEndDate(Date startDate, Date endDate) {
        if (endDate != null && startDate != null) {
            startDate = DateUtil.getBeginOfDay(startDate);// 没有时分秒
            endDate = DateUtil.getBeginOfDay(endDate);// 没有时分秒
            if (endDate.before(startDate)) {
                throw new AppException("截止时间不能早于开始时间");
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
            //项目集任务不做校验
            if (bean.objectType != Task.OBJECTTYPE_项目清单) {
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
            if (project.workflowStatusType == ProjectStatusDefine.TYPE_结束状态) {
                throw new AppException("项目【" + project.name + "】已完成，无法创建新任务");
            }
            checkPermission(account, project.companyId);
            checkProjectPermission(account, project.id, Permission.ID_创建TASK + bean.objectType);
            checkDataDictValueValid("Task.objectType", bean.objectType, "对象类型错误");
            bean.isFinish = false;
        }
        //
        if (!isImportCreate) {
            if (bean.status > 0) {
                ProjectStatusDefine statusDefine = dao.getById(ProjectStatusDefine.class, bean.status);
                if (statusDefine == null) {
                    throw new AppException("状态不能为空");
                }
                bean.statusName = statusDefine.name;
                if (statusDefine.type == ProjectStatusDefine.TYPE_结束状态) {
                    bean.isFinish = true;
                    bean.finishTime = new Date();
                } else {
                    bean.isFinish = false;
                }
                //
            } else {
                ProjectStatusDefine statusDefine = dao.getProjectStatusDefineByProjectIdObjectTypeType(bean.projectId,
                        bean.objectType, ProjectStatusDefine.TYPE_开始状态);
                if (statusDefine != null) {
                    bean.status = statusDefine.id;
                    bean.statusName = statusDefine.name;
                }
            }

            if (bean.priority > 0) {
                ProjectPriorityDefine priorityDefine = dao.getById(ProjectPriorityDefine.class, bean.priority);
                if (priorityDefine == null) {
                    throw new AppException("优先级不能为空");
                }
            } else {
                ProjectPriorityDefine priorityDefine = dao
                        .getDefaultProjectPriorityDefineByProjectIdObjectType(bean.projectId, bean.objectType);
                if (priorityDefine != null) {
                    bean.priority = priorityDefine.id;
                }
            }

            if (bean.iterationId != 0) {
                if (bean.objectType == Task.OBJECTTYPE_测试用例) {//测试用例没有迭代 20200212添加
                    bean.iterationId = 0;
                } else {
                    ProjectIteration iteration = dao.getExistedById(ProjectIteration.class, bean.iterationId);
                    if (iteration.isDelete) {
                        throw new AppException("迭代已被删除，不能关联");
                    }
                    checkProjectId(project.id, iteration.projectId, "迭代不存在");
                }
            }

            if (bean.releaseId > 0) {
                ProjectRelease release = dao.getExistedById(ProjectRelease.class, bean.releaseId);
                if (release.isDelete) {
                    throw new AppException("Release已被删除，不能关联");
                }
                checkProjectId(project.id, release.projectId, "Release不存在");
            }
            if (bean.subSystemId > 0) {
                ProjectSubSystem subSystem = dao.getExistedById(ProjectSubSystem.class, bean.subSystemId);
                if (subSystem.isDelete) {
                    throw new AppException("子系统已被删除，不能关联");
                }
                checkProjectId(project.id, subSystem.projectId, "子系统不存在");
            }

            if (bean.stageId > 0) {
                Stage stage = dao.getExistedById(Stage.class, bean.stageId);
                if (stage.isDelete) {
                    throw new AppException("阶段已被删除，不能关联");
                }
                checkProjectId(project.id, stage.projectId, "阶段不存在");
            }

            if (bean.repositoryId > 0) {
                CompanyVersionRepository repository = dao.getExistedById(CompanyVersionRepository.class, bean.repositoryId);
                if (repository.isDelete) {
                    throw new AppException("系统已被删除，不能关联");
                }
            }

        }
        //
        if (bean.expectWorkTime > 10000000) {
            throw new AppException("预计工时设置不合法");
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
//            checkDateInIterationRange(bean.iterationId, bean.startDate, "开始时间");
        }
        if (bean.endDate != null) {
            bean.endDate = DateUtil.getBeginOfDay(bean.endDate);
//            checkDateInIterationRange(bean.iterationId, bean.endDate, "截止时间");
        }
        checkTaskStartEndDate(bean.startDate, bean.endDate);
        bean.reopenCount = 0;
        // Category检查
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

        //TODO 非导入唯一字段校验
//        if (!isImportCreate) {
        AtomicInteger at = null;
        if (isImportCreate) {
            at = new AtomicInteger(0);
        }

        checkUniqueField(bean, at);
//        }
        if (isImportCreate) {
            //导入时有唯一字段需进行覆盖
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
        // 添加状态变更记录
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

        // 添加附件列表
        if (!BizUtil.isNullOrEmpty(bean.attachmentUuidList)) {
            for (String uuid : bean.attachmentUuidList) {
                addTaskAttachment0(account, uuid, bean.id, false);
            }
        }
        // 添加关联对象列表
        if (!BizUtil.isNullOrEmpty(bean.associatedIdList)) {
            addTaskAssociatedList0(account, bean.id, 0, bean.associatedIdList);
        }
        // 测试用例特殊处理
        if (bean.objectType == Task.OBJECTTYPE_测试用例) {
            if (bean.parentId > 0) {
                Task testPlan = dao.getExistedTaskByIdObjectType(bean.parentId, Task.OBJECTTYPE_测试计划);
                addTestPlanTestCase(account, testPlan, bean);
            }
        }
        // 任务详情
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
        if (bean.parentId > 0) {// 更新父任务
            ProjectStatusDefine statusDefine = dao.getById(ProjectStatusDefine.class, bean.status);
            TaskInfo parentTask = dao.getExistedByIdForUpdate(TaskInfo.class, bean.parentId);
            //校验开始截止时间
            if (null != bean.startDate) {
                if (null != parentTask.startDate && bean.startDate.before(parentTask.startDate)) {
                    throw new AppException("对象的开始时间不能早于父对象【" + parentTask.name + "】的开始时间");
                }
            }
            if (null != bean.endDate) {
                if (null != parentTask.endDate && bean.endDate.after(parentTask.endDate)) {
                    throw new AppException("对象的截止时间不能晚于父对象【" + parentTask.name + "】的截止时间");
                }
            }
            parentTask.subTaskCount++;
            if (statusDefine != null && statusDefine.type == ProjectStatusDefine.TYPE_结束状态) {
                parentTask.finishSubTaskCount++;
            }
//            dao.update(parentTask);
            dao.updateParentTaskCount(parentTask);
            //
            TaskSimpleInfo subTask = TaskSimpleInfo.createTaskSimpleinfo(bean);
            addChangeLog(account, 0, bean.parentId, ChangeLog.TYPE_新增子任务, JSONUtil.toJson(subTask));
            addChangeLog(account, bean.projectId, 0, ChangeLog.TYPE_新增子任务,
                    JSONUtil.toJson(TaskSimpleInfo.createTaskSimpleinfo(parentTask)));
            Map<String, Object> map = new HashMap<>();
            map.put("subTask", subTask);
            sendNotificationForTask(account, parentTask, AccountNotificationSetting.TYPE_对象新增子对象, "对象新增子对象", map);
        }
        // 通知责任人
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
            item.name = "责任人";
            item.afterContent = accountInfoList;
            map.put("changeLogItemList", Collections.singletonList(item));
            map.put("ownerList", item.afterContent);
            for (Integer accountId : bean.ownerAccountIdList) {
                if (accountId == account.id) {
                    continue;
                }
                addAccountNotification(accountId, AccountNotificationSetting.TYPE_对象指派, bean.companyId, bean.projectId,
                        bean.id, "对象指派提醒", JSONUtil.toJson(map), new Date(), account);
            }
        }
        //
        addChangeLog(account, 0, bean.id, ChangeLog.TYPE_创建, "");
        ProjectModule module = dao.getProjectModuleByProjectObjectType(bean.projectId, bean.objectType);
        if (module.isPublic) {
            addChangeLog(account, bean.projectId, 0, ChangeLog.TYPE_新增任务,
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

    //编辑权限
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

            throw new AppException(BizUtil.isNullOrEmpty(errMsg) ? ("权限不足" + permissionId) : errMsg);
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
        if (pro.workflowStatusType == ProjectStatusDefine.TYPE_结束状态) {
            throw new AppException("项目【" + pro.name + "】已完成，无法编辑任务");
        }
        ProjectModule module = dao.getProjectModuleByProjectObjectType(old.projectId, old.objectType);
        String permissionId = Permission.ID_编辑TASK + old.objectType;
        boolean changeOwnerAccount = false;// 修改责任人
        Set<String> havePermissions = new HashSet<>();
        if (!isCompanySuperBoss(account)) {
            havePermissions = getMyTaskPermission(account, old);//这个权限已经包含数据权限过滤后得到的
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
                //todo校验唯一字段
                if (fieldDefine.isUnique) {
                    checkUniqueField(fieldDefine, bean, null);
                }
            }

            if ("name".equals(field)) {
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                item.name = "名称";
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
                item.name = "迭代";
                item.beforeContent = old.iterationName;
                old.iterationId = bean.iterationId;
                if (bean.iterationId > 0) {
                    ProjectIteration iteration = dao.getExistedById(ProjectIteration.class, bean.iterationId);
                    if (iteration.isDelete) {
                        throw new AppException("迭代已被删除不能关联");
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
                item.name = "系统";
                item.beforeContent = old.repositoryName;
                old.repositoryId = bean.repositoryId;
                if (bean.repositoryId > 0) {
                    CompanyVersionRepository repository = dao.getExistedById(CompanyVersionRepository.class, bean.repositoryId);
                    if (repository.isDelete) {
                        throw new AppException("系统已被删除，无法进行关联");
                    }
                    if (repository.status == CompanyVersionRepository.STATUS_已关闭) {
                        throw new AppException("系统已关闭，无法进行关联");
                    }
                    item.afterContent = repository.name;
                }
                addChangeLogItem(changeLogItems, item);
                old.isCreateIndex = false;
            } else if ("startDate".equals(field)) {
                updateStartEndDate = true;
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                    checkEditPermission0(account, havePermissions, Permission.ID_修改开始截止时间 + old.objectType, projectId, "没有权限修改开始/截止时间");
                }
                if (bean.startDate != null) {
                    bean.startDate = DateUtil.getBeginOfDay(bean.startDate);
                }
                Date oldStartDate = old.startDate;
                item.name = "开始时间";
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
                    checkEditPermission0(account, havePermissions, Permission.ID_修改开始截止时间 + old.objectType, projectId, "没有权限修改开始/截止时间");
                }
                if (bean.endDate != null) {
                    bean.endDate = DateUtil.getBeginOfDay(bean.endDate);
                }
                Date oldEndDate = old.endDate;
                item.name = "截止时间";
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
                    checkEditPermission0(account, havePermissions, Permission.ID_修改完成时间 + old.objectType, projectId, "没有权限修改完成时间");
                }
                if (bean.finishTime != null) {
                    if (null != old.startDate && DateUtil.getBeginOfDay(bean.finishTime).before(DateUtil.getBeginOfDay(old.startDate))) {
                        throw new AppException("完成时间不能早于开始时间");
                    }
//                    if (null != old.endDate && bean.finishTime.after(DateUtil.getBeginOfDay(DateUtil.getNextDay(old.endDate, 1)))) {
//                        throw new AppException("完成时间不能晚于截止时间");
//                    }
                }
                item.name = "完成时间";
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
                item.name = "计划截止时间";
                item.beforeContent = DateUtil.formatDate(old.expectEndDate, "yyyy-MM-dd");
                old.expectEndDate = bean.expectEndDate;
                item.afterContent = DateUtil.formatDate(old.expectEndDate, "yyyy-MM-dd");
                addChangeLogItem(changeLogItems, item);
            } else if ("expectWorkTime".equals(field)) {
                if (bean.expectWorkTime < 0) {
                    throw new AppException("预计工时不能小于0");
                }
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                item.name = "预计工时";
                item.beforeContent = old.expectWorkTime + "";
                old.expectWorkTime = bean.expectWorkTime;
                item.afterContent = old.expectWorkTime + "";
                addChangeLogItem(changeLogItems, item);
            } else if ("workLoad".equals(field)) {
                if (bean.workLoad < 0) {
                    throw new AppException("工作量不能小于0");
                }
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                item.name = "工作量";
                item.beforeContent = old.workLoad + "";
                old.workLoad = bean.workLoad;
                item.afterContent = old.workLoad + "";
                addChangeLogItem(changeLogItems, item);
            } else if ("workTime".equals(field)) {
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                item.name = "工时";
                item.beforeContent = old.workTime + "";
                old.workTime = bean.workTime;
                item.afterContent = old.workTime + "";
                addChangeLogItem(changeLogItems, item);
            } else if ("status".equals(field)) {
                if (old.status == bean.status) {
                    continue;
                }
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, Permission.ID_更改状态TASK + old.objectType, projectId);
                }
                item.name = "状态";
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
                    throw new AppException("状态不能为空");
                }
                if (oldStatusDefine != null) {//20200216新增
                    if (oldStatusDefine.transferTo == null ||
                            !oldStatusDefine.transferTo.contains(bean.status)) {
                        throw new AppException("状态流转限制，不能从【" + oldStatusDefine.name + "】流转到【" + statusDefine.name + "】");
                    }
                }
                newStatus = statusDefine.name;
                boolean oldIsFinish = old.isFinish;
                if (statusDefine.type == ProjectStatusDefine.TYPE_结束状态) {
                    old.isFinish = true;
                    old.finishTime = new Date();
                    if (null != old.startDate) {
                        old.startDays = DateUtil.differentDays(old.startDate, old.finishTime) + 1;
                    }
                } else {
                    old.isFinish = false;
//                    old.finishTime = null;
                }
                //如果新状态不是开始状态,判断前置任务是否完成
                if (statusDefine.type != ProjectStatusDefine.TYPE_开始状态) {
                    TaskAssociatedQuery associatedQuery = new TaskAssociatedQuery();
                    associatedQuery.taskId = old.id;
                    associatedQuery.associatedType = TaskAssociated.ASSOCIATED_TYPE_前置任务;
                    associatedQuery.pageSize = Integer.MAX_VALUE;
                    List<TaskAssociatedInfo> forwardTasks = dao.getList(associatedQuery);
                    for (TaskAssociatedInfo e : forwardTasks) {
                        if (!e.isFinish && e.statusType != 0) {
                            throw new AppException("前置" + e.objectTypeName + "【" + e.name + "】没有完成");
                        }
                    }
                }
                changeFinish = (oldIsFinish != old.isFinish);
                if (oldStatusDefine != null && oldStatusDefine.type == ProjectStatusDefine.TYPE_结束状态
                        && statusDefine.type != ProjectStatusDefine.TYPE_结束状态) {
                    old.reopenCount++;//重新打开
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
                item.name = "优先级";
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
                    throw new AppException("优先级不能为空");
                }
                old.priority = bean.priority;
                newPriority = priorityDefine.name;
                item.beforeContent = oldPriority;
                item.afterContent = newPriority;
                addChangeLogItem(changeLogItems, item);
                old.isCreateIndex = false;
            } else if ("ownerAccountIdList".equals(field)) {
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, Permission.ID_更改责任人TASK + old.objectType, projectId);
                }
                if (BizUtil.isNullOrEmpty(old.firstOwner)) {
                    old.firstOwner = bean.ownerAccountIdList;
                }
                item.name = "责任人";
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
                item.name = "分类";
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
                    throw new AppException("分类数量不能超过20个");
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
                    addChangeLog(account, 0, bean.id, ChangeLog.TYPE_编辑详细描述, JSONUtil.toJson(diff.id));
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
                        throw new AppException("Release已被删除不能关联");
                    }
                    checkProjectId(old.projectId, release.projectId, "release不存在");
                    afterReleaseName = release.name;
                }
                old.releaseId = bean.releaseId;
                item.beforeContent = beforeReleaseName;
                item.afterContent = afterReleaseName;
                addChangeLogItem(changeLogItems, item);
            } else if ("subSystemId".equals(field)) {// 子系统
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                item.name = "子系统";
                String beforeSubSystemName = "";
                String afterSubSystemName = "";
                if (old.subSystemId > 0) {
                    ProjectSubSystem oldProjectSubSystem = dao.getExistedById(ProjectSubSystem.class, old.subSystemId);
                    beforeSubSystemName = oldProjectSubSystem.name;
                }
                if (bean.subSystemId > 0) {
                    ProjectSubSystem subSystem = dao.getExistedById(ProjectSubSystem.class, bean.subSystemId);
                    if (subSystem.isDelete) {
                        throw new AppException("子系统已被删除不能关联");
                    }
                    checkProjectId(old.projectId, subSystem.projectId, "子系统不存在");
                    afterSubSystemName = subSystem.name;
                }
                old.subSystemId = bean.subSystemId;
                item.beforeContent = beforeSubSystemName;
                item.afterContent = afterSubSystemName;
                addChangeLogItem(changeLogItems, item);
            } else if ("stageId".equals(field)) {// 子系统
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                item.name = "阶段";
                String beforeStageName = old.stageName;
                String afterStageName = "";
                if (old.stageId > 0) {
                    Stage oldStage = dao.getExistedById(Stage.class, old.stageId);
                    beforeStageName = oldStage.name;
                }
                if (bean.stageId > 0) {
                    Stage stage = dao.getExistedById(Stage.class, bean.stageId);
                    if (stage.isDelete) {
                        throw new AppException("该阶段已被删除不能关联");
                    }
                    checkProjectId(old.projectId, stage.projectId, "阶段不存在");
                    afterStageName = stage.name;
                }
                if (old.stageId > 0) {
                    dao.deleteStageTaskAssociate(old.stageId, old.id);
                }
                if (bean.stageId > 0) {
                    StageAssociate associate = new StageAssociate();
                    associate.stageId = bean.stageId;
                    associate.associateId = old.id;
                    associate.type = StageAssociate.TYPE_任务;
                    associate.projectId = old.projectId;
                    associate.companyId = old.companyId;
                    associate.createAccountId = account.id;
                    dao.add(associate);
                }

                old.stageId = bean.stageId;
                item.beforeContent = beforeStageName;
                item.afterContent = afterStageName;
                addChangeLogItem(changeLogItems, item);
            } else if ("progress".equals(field)) {// 进度
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                if (bean.progress < 0 || bean.progress > 100) {
                    throw new AppException("进度取值不在0-100范围内");
                }
                item.name = "进度";
                String beforeProgress = old.progress + "";
                String afterProgress = bean.progress + "";
                old.progress = bean.progress;
                item.beforeContent = beforeProgress;
                item.afterContent = afterProgress;
                addChangeLogItem(changeLogItems, item);
            } else if ("leaderAccountIdList".equals(field)) {// 分管领导
                if (!ignorePermission) {
                    checkEditPermission(account, havePermissions, permissionId, projectId);
                }
                String beforeLeaderName = "";
                String afterLeaderName = "";
                item.name = "分管领导";
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
            } else if (field.startsWith("field_")) {// 自定义字段
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
                if (fieldDefine.type == ProjectFieldDefine.TYPE_团队成员选择) {
                    item.beforeContent = BizUtil.getCustomeAccountNames(old, fieldDefine.id);
                } else if (fieldDefine.type == ProjectFieldDefine.TYPE_日期) {
                    if (fieldDefine.showTimeField) {
                        item.beforeContent = BizUtil.getCustomeDateTimeString(oldValue);
                        item.afterContent = BizUtil.getCustomeDateTimeString(newValue);
                    } else {
                        item.beforeContent = BizUtil.getCustomeDateString(oldValue);
                        item.afterContent = BizUtil.getCustomeDateString(newValue);
                    }
                } else if (fieldDefine.type == ProjectFieldDefine.TYPE_复选框) {
                    item.beforeContent = BizUtil.getCheckBoxValues(oldValue);
                    item.afterContent = BizUtil.getCheckBoxValues(newValue);
                } else {
                    item.beforeContent = BizUtil.toString(oldValue);
                    item.afterContent = BizUtil.toString(newValue);
                }
                checkFieldValid(old, false);// 设置
                if (fieldDefine.type == ProjectFieldDefine.TYPE_团队成员选择) {
                    item.afterContent = BizUtil.getCustomeAccountNames(old, fieldDefine.id);
                }
                addChangeLogItem(changeLogItems, item);
            } else {
                throw new AppException("不支持编辑" + field);
            }
        }
        if (!updateStartEndDate) {
//            checkTaskStartEndDate(old.startDate, old.endDate);
            old.updateAccountId = account.id;
            BizUtil.checkValid(old);
            dao.update(old);
        }
        //
        if (old.parentId > 0 && changeFinish) {// 更新父任务
            Task parentTask = dao.getExistedByIdForUpdate(Task.class, old.parentId);
            parentTask.finishSubTaskCount = dao.calcFinishSubTaskCount(old.parentId);
            dao.update(parentTask);
        }
        //
        if (changeLogItems.size() > 0 && !updateStartEndDate) {
            ChangeLog changeLog = dao.getLastChangeLogByTaskIdCreateAccountId(old.id, account.id);
            long now = System.currentTimeMillis();
            if (changeLog == null || changeLog.type != ChangeLog.TYPE_编辑属性
                    || (now - changeLog.createTime.getTime() > 5 * 60 * 1000)) {
                addChangeLog(account, 0, old.id, ChangeLog.TYPE_编辑属性,
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

    //2020.7.28 前调整截止时间的处理方式， 前置任务延期 后置任务自动延期
    @Deprecated
    private void updatePostTask(Account account, TaskInfo task, Date oldEndDate, Date newEndDate) {
        if (oldEndDate == null || newEndDate == null) {
            return;
        }
        Date beforeEndDate = DateUtil.getBeginOfDay(oldEndDate);
        Date afterEndDate = DateUtil.getBeginOfDay(newEndDate);
        int diff = DateUtil.differentDays(beforeEndDate, afterEndDate);
        if (diff <= 0) {//提前不管
            return;
        }
        TaskAssociatedQuery query = new TaskAssociatedQuery();
        query.pageSize = 10000;
        query.taskId = task.id;
        query.associatedType = TaskAssociated.ASSOCIATED_TYPE_后置任务;
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
                throw new AppException("对象【" + task.name + "】的开始时间不能晚于其截止时间");
            }
        }
    }

    /**
     * 校验父子任务的开始截止时间是否有效
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
                throw new AppException("对象【" + subTask.name + "】的开始时间不能早于其父对象【" + pubTask.name + "】的开始时间");
            }
        }
        if (null != subTask.endDate && null != pubTask.endDate) {
            if (subTask.endDate.after(pubTask.endDate)) {
                throw new AppException("对象【" + subTask.name + "】的截止时间不能晚于其父对象【" + pubTask.name + "】的截止时间");
            }
        }

    }


    /**
     * 2020.7.28后的开始截止时间动态调整处理方式，前置任务延期 后置任务自动延期
     *
     * @param account         操作人
     * @param task            任务
     * @param oldDate         原来的时间
     * @param newDate         新的时间
     * @param dateField       时间字段
     * @param finalUpdate     是否最后修改，开始时间导致的截止时间变动不会再次修改
     * @param havePermissions 操作权限
     * @param isManualUpdate  是否手动修改，手动修改模式下不会影响其前后置任务
     */
    private void updateTaskStartEndDate(Account account, TaskInfo task, Date oldDate, Date newDate,
                                        String dateField, boolean finalUpdate, Set<String> havePermissions, boolean isManualUpdate) {
        if (oldDate == null && newDate == null) {
            return;
        }
        //判断是否有前后置关联关系
        Date beforeDate = DateUtil.getBeginOfDay(oldDate);
        Date afterDate = DateUtil.getBeginOfDay(newDate);
        int diff = 0;
        if (null != oldDate && null != newDate) {
            diff = DateUtil.differentDays(beforeDate, afterDate);
            if (diff == 0) {
                return;
            }
        }
        String permissionId = Permission.ID_编辑TASK + task.objectType;
        if (finalUpdate) {
            havePermissions.addAll(getMyTaskPermission(account, task));
        }
        checkEditPermission(account, havePermissions, permissionId, task.projectId);
        checkEditPermission0(account, havePermissions, Permission.ID_修改开始截止时间 + task.objectType, task.projectId, "没有权限修改对象【" + task.name + "】的开始/截止时间");
        List<TaskAssociated> taskAssociatedList = dao.getTaskAssociatedTaskList(task.id);
        boolean hasPreTask = !BizUtil.isNullOrEmpty(taskAssociatedList) && taskAssociatedList.stream().anyMatch(k -> k.associatedType == TaskAssociated.ASSOCIATED_TYPE_前置任务);
        boolean hasNextTask = !BizUtil.isNullOrEmpty(taskAssociatedList) && taskAssociatedList.stream().anyMatch(k -> k.associatedType == TaskAssociated.ASSOCIATED_TYPE_后置任务);
        if (!hasPreTask && !hasNextTask && task.subTaskCount == task.finishSubTaskCount) {
            isManualUpdate = true;
        }
        //1.同步调整截止时间 2.调整子任务的开始、截止时间 3、调整后置任务的开始截止时间
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
                //前置对象校验
                if (diff < 0) {
                    TaskAssociatedQuery query = new TaskAssociatedQuery();
                    query.pageSize = 10000;
                    query.taskId = task.id;
                    query.associatedType = TaskAssociated.ASSOCIATED_TYPE_前置任务;
                    List<TaskAssociated> taskAssociateds = dao.getList(query);
                    if (!BizUtil.isNullOrEmpty(taskAssociateds)) {
                        for (TaskAssociated taskAssociated : taskAssociateds) {
                            TaskInfo associateTask = dao.getById(TaskInfo.class, taskAssociated.associatedTaskId);
                            if (associateTask == null || associateTask.isDelete || associateTask.isFinish || associateTask.endDate == null) {
                                continue;
                            }
                            if (!task.startDate.after(associateTask.endDate)) {
                                throw new AppException("对象【" + task.name + "】的开始时间不能早于前置对象【" + associateTask.name + "】的截止时间");
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
            query.associatedType = TaskAssociated.ASSOCIATED_TYPE_后置任务;
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
                        permissionId = Permission.ID_编辑TASK + associateTask.objectType;
                        checkEditPermission(account, havePermissions, permissionId, task.projectId);
                        checkEditPermission0(account, havePermissions, Permission.ID_修改开始截止时间 + associateTask.objectType, task.projectId, "没有权限修改对象【" + associateTask.name + "】的开始/截止时间");
                        updateTaskStartEndDate(account, associateTask, oldStartDate, associateTask.startDate, "startDate", true, havePermissions, isManualUpdate);
                    }
                }
            }
        }
        if (task.subTaskCount > 0) {
            List<TaskInfo> subTaskList = dao.getSubTaskListById(task.id);
            if (!BizUtil.isNullOrEmpty(subTaskList)) {
                if (!isManualUpdate) {
                    //子任务有前后关联关系，前后顺序排列
                    Object[] taskIds = subTaskList.stream().map(k -> k.id).toArray();
                    List<TaskAssociated> tas = dao.getTaskAssociatedByTaskIds(taskIds);
                    if (!BizUtil.isNullOrEmpty(tas)) {
                        Map<Integer, List<Integer>> beforemap =
                                tas.stream().filter(k -> k.associatedType == TaskAssociated.ASSOCIATED_TYPE_前置任务)
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
                        //获取前置任务
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
                        //前置任务校验
                        TaskAssociatedQuery query = new TaskAssociatedQuery();
                        query.pageSize = 10000;
                        query.taskId = subTask.id;
                        query.associatedType = TaskAssociated.ASSOCIATED_TYPE_前置任务;
                        List<TaskAssociated> taskAssociateds = dao.getList(query);
                        if (!BizUtil.isNullOrEmpty(taskAssociateds)) {
                            for (TaskAssociated associated : taskAssociateds) {
                                TaskInfo associateTask = dao.getById(TaskInfo.class, associated.associatedTaskId);
                                if (associateTask == null || associateTask.isDelete || associateTask.isFinish || associateTask.endDate == null) {
                                    continue;
                                }
                                if (!subTask.startDate.after(associateTask.endDate)) {
                                    throw new AppException("后置对象【" + subTask.name + "】的开始时间不能早于或等于前置对象【" + associateTask.name + "】的截止时间");
                                }
                            }
                        }

                        if (!DateUtil.isSameDay(subTask.startDate, oldStartDate)) {
                            updateTaskDate(account, subTask, "startDate", subTask.startDate, oldStartDate);
                        }
                        if (null != subTask.endDate) {
                            updateTaskDate(account, subTask, "endDate", subTask.endDate, oldEndDate);
                            havePermissions.addAll(getMyTaskPermission(account, subTask));
                            permissionId = Permission.ID_编辑TASK + subTask.objectType;
                            checkEditPermission(account, havePermissions, permissionId, task.projectId);
                            checkEditPermission0(account, havePermissions, Permission.ID_修改开始截止时间 + subTask.objectType, task.projectId, "没有权限修改对象【" + subTask.name + "】的开始/截止时间");
                            updateTaskStartEndDate(account, subTask, oldStartDate, subTask.startDate, "startDate", true, havePermissions, isManualUpdate);
                        }
                    }
                }
            }
        }
    }

    /**
     * 更新对象的开始截止时间
     */
    private void updateTaskDate(Account account, TaskInfo task, String dateField, Date dateValue, Date oldDateValue) {
        if (DateUtil.isSameDay(dateValue, oldDateValue)) {
            return;
        }
        logger.info("------>update task :{},#{} date field:{},old:{},new:{}", task.name, task.id, dateField, oldDateValue, dateValue);
        dao.updateTaskDateLonely(task, dateField, dateValue);
        ChangeLogItem item = new ChangeLogItem();
        item.name = "startDate".equalsIgnoreCase(dateField) ? "开始时间" : "截止时间";
        item.beforeContent = DateUtil.formatDate(oldDateValue, "yyyy-MM-dd");
        item.afterContent = DateUtil.formatDate(dateValue, "yyyy-MM-dd");
        item.createTime = new Date();
        ChangeLog changeLog = dao.getLastChangeLogByTaskIdCreateAccountId(task.id, account.id);
        long now = System.currentTimeMillis();
        if (changeLog == null || changeLog.type != ChangeLog.TYPE_编辑属性
                || (now - changeLog.createTime.getTime() > 5 * 60 * 1000)) {
            addChangeLog(account, 0, task.id, ChangeLog.TYPE_编辑属性,
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
                if (account.id == task.createAccountId) {//是否是创建人
                    return;
                }
            } else if ("owner".equals(owner)) {
                if (BizUtil.contains(task.ownerAccountIdList, account.id)) {//是否是责任人
                    return;
                }
            } else if (owner.startsWith("role_")) {// t_role
                int roleId = Integer.parseInt(owner.substring(owner.lastIndexOf("_") + 1));
                Set<Integer> accountIds = getAccountIdListByProjectIdRoleId(task.projectId, roleId);
                if (BizUtil.contains(accountIds, account.id)) {//是否是角色
                    return;
                }
            } else if (owner.startsWith("member_")) {// t_account
                int accountId = Integer.parseInt(owner.substring(owner.lastIndexOf("_") + 1));
                if (account.id == accountId) {//是否是成员
                    return;
                }
            }
        }
        throw new AppException("您没有权限修改" + task.objectTypeName + "状态【" + task.statusName + "】");
    }

    // 任务变更后给用户发送通知
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
                    if ("责任人".equals(item.name)) {
                        map.put("ownerList", item.afterContent);
                        break;
                    }
                }
                addAccountNotification(accountId, AccountNotificationSetting.TYPE_对象指派, task.companyId, task.projectId,
                        task.id, "对象指派提醒", JSONUtil.toJson(map), new Date(), optAccount);
            } else {
                if (isDelete) {
                    addAccountNotification(accountId, AccountNotificationSetting.TYPE_对象删除, task.companyId,
                            task.projectId, task.id, "对象删除提醒", JSONUtil.toJson(map), new Date(), optAccount);
                } else {
                    addAccountNotification(accountId, AccountNotificationSetting.TYPE_对象属性变更, task.companyId,
                            task.projectId, task.id, "对象变更提醒", JSONUtil.toJson(map), new Date(), optAccount);
                }
            }
        }

        //2021年5月1日 任务状态变更后需要同步通知到关联对象
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
                            addAccountNotification(ownerId, AccountNotificationSetting.TYPE_对象属性变更, task.companyId,
                                    task.projectId, task.id, "你负责的任务所关联的任务状态变更为" + task.statusName, JSONUtil.toJson(map), new Date(), optAccount);
                        }
                    }
                });

            }
        }
    }

    //检查字段是否设置
    private void checkFieldListValid(Task task, List<Integer> checkFieldList) {
        if (checkFieldList == null || checkFieldList.size() == 0) {
            return;
        }
        for (Integer fieldDefineId : checkFieldList) {
            ProjectFieldDefine e = dao.getCheckProjectFieldDefine(task.projectId, task.objectType, fieldDefineId);
            //bug fix 复制项目时会复制checkFieldList,然而字段实际ID不匹配
//            dao.getById(ProjectFieldDefine.class, fieldDefineId);
            if (e == null) {
                logger.warn("ProjectFieldDefine not found fieldDefineId:{}", fieldDefineId);
                continue;
            }
            TaskFieldValue fieldValue = getTaskFieldValue(task, e);
            if (BizUtil.isNullOrEmpty(fieldValue.value)) {
                logger.error("checkFieldListValid field:{} value:{}", e.field, DumpUtil.dump(fieldValue));
                throw new AppException("字段【" + e.name + "】没有设置");
            }
            if (fieldValue.field != null) {//只针对整数判断是否等于0
                Class<?> fieldType = fieldValue.field.getType();
                if (fieldType.equals(int.class) ||
                        fieldType.equals(short.class) ||
                        fieldType.equals(long.class)) {
                    long lvalue = Long.parseLong(fieldValue.value.toString());
                    if (lvalue <= 0) {
                        logger.error("checkFieldListValid field:{} value:{}<=0 lvalue:{}",
                                e.field, DumpUtil.dump(fieldValue), lvalue);
                        throw new AppException("字段【" + e.name + "】没有设置");
                    }
                }
            }
        }
    }

    // 任务状态变更后的配置
    private void setTaskOwnerWhenUpdateStatus(TaskInfo old, ProjectStatusDefine statusDefine,
                                              List<ChangeLogItem> changeLogItems) {
        if (statusDefine == null) {
            return;
        }
        //检查字段是否设置
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
                    //最初的责任人
                } else if ("firstOwner".equals(newOwner)) {
                    if (!BizUtil.isNullOrEmpty(old.firstOwner)) {
                        newOwnerIdList.addAll(old.firstOwner);
                    }
                    //上上一个状态责任人
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
            item.name = "责任人";
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
        if (e.isSystemField) {//系统字段
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
        } else {//自定义字段
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
        throw new AppException("【" + e.name + "】是必选字段，不能为空");
    }

    private TaskFieldValue getTaskFieldValue(Task bean, ProjectFieldDefine e) {
        TaskFieldValue tfv = new TaskFieldValue();
        if (e.field.equals("ownerAccountName")) {
            e.field = "ownerAccountIdList";
        }
        if (!e.field.startsWith("field_")) {//非自定义字段
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
     * 检查字段合法性
     */
    @SuppressWarnings("unchecked")
    private void checkFieldValid(TaskInfo bean, boolean isAdd) {
        List<ProjectFieldDefineInfo> fieldDefines = dao.getProjectFieldDefineInfoList(bean.projectId, bean.objectType);
        Map<String, Object> customFields = new LinkedHashMap<>();
        //团队成员选择特殊处理
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
                //项目集同步过去的专属字段不做校验
                if (bean.objectType == Task.OBJECTTYPE_项目清单 && e.isPsField) {
                    customFields.put(key, value);
                    continue;
                }
                if (value != null) {
                    if (e.type == ProjectFieldDefineInfo.TYPE_日期) {
                        try {
                            if (NumberUtils.isNumber(value.toString())) {
                                new Date((long) value);
                            } else {
                                Date v = DateUtil.parseDateTimeFromExcel(value.toString());
                                if (null == v) {
                                    throw new AppException("字段【" + e.name + "】格式错误");
                                }
                            }
                        } catch (Exception ex) {
                            logger.error("date field error value:" + value);
                            throw new AppException("字段【" + e.name + "】格式错误");
                        }
                    }
                    if (e.type == ProjectFieldDefineInfo.TYPE_数值) {
                        try {
                            Double.valueOf(value.toString());
                        } catch (Exception ex) {
                            logger.error("number field error value:" + value);
                            throw new AppException("字段【" + e.name + "】格式错误");
                        }
                    }
                    if (e.type == ProjectFieldDefineInfo.TYPE_单选框) {
                        if (e.valueRange != null) {
                            if (!BizUtil.contains(e.valueRange, value.toString().trim())) {
                                logger.error("单选框 field error value:{},range:{}", value, DumpUtil.dump(e.valueRange));
                                throw new AppException("字段【" + e.name + "】取值错误，不在正确的范围内");
                            }
                        }
                    }
                    if (e.type == ProjectFieldDefineInfo.TYPE_复选框) {
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
                                    logger.error("复选框 field error v:{} value:{},range:{}", v, value, DumpUtil.dump(e.valueRange));
                                    throw new AppException("字段【" + e.name + "】取值错误，不在正确的范围内");
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
        if (fieldDefine.type == ProjectFieldDefine.TYPE_团队成员选择) {
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
            throw new AppException("找不到名为【" + testCase.name + "】的测试用例");
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
        bean.status = TestPlanTestCase.STATUS_未执行;
        bean.createAccountId = account.id;
        dao.add(bean);
    }

    public void addTaskAttachment0(Account account, String uuid, int taskId, boolean isWiki) {
        TaskInfo task = dao.getExistedById(TaskInfo.class, taskId);
        checkPermission(account, task.companyId);

        checkProjectPermission(account, task.projectId, "task_add_attachment_" + task.objectType);
        //文件附件
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
            addChangeLog(account, 0, //任务动态
                    taskId, ChangeLog.TYPE_上传附件, JSONUtil.toJson(attachment));
            addChangeLog(account, task.projectId, //项目动态
                    0, ChangeLog.TYPE_上传附件,
                    toSimpleJson(task));
            Map<String, Object> map = new HashMap<>();
            map.put("attachment", attachment);
            sendNotificationForTask(account, task, AccountNotificationSetting.TYPE_对象上传附件, "对象上传附件", map);
            addOptLog(account, bean.id, attachment.name,
                    OptLog.EVENT_ID_对象上传附件, "名称:" + attachment.name);
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
            addChangeLog(account, 0, //任务动态
                    taskId, ChangeLog.TYPE_关联wiki附件, JSONUtil.toJson(wikiPage));
            addChangeLog(account, task.projectId, //项目动态
                    0, ChangeLog.TYPE_关联wiki附件,
                    toSimpleJson(task));
            Map<String, Object> map = new HashMap<>();
            map.put("attachment", wikiPage);
            sendNotificationForTask(account, task, AccountNotificationSetting.TYPE_对象关联WIKI, "对象关联WIKI", map);
            addOptLog(account, bean.id, wikiPage.name,
                    OptLog.EVENT_ID_对象关联WIKI, "名称:" + wikiPage.name);
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
        //checkProjectId(task.projectId, associatedTask.projectId, "数据不存在");
        if (associatedTask.id == task.id) {
            return;
        }
        if (task.companyId != associatedTask.companyId) {//不能跨企业
            throw new AppException("权限不足");
        }
        if (task.isDelete || associatedTask.isDelete) {
            throw new AppException("对象已经删除");
        }
        int oppType = 0;
        if (associatedType == TaskAssociated.ASSOCIATED_TYPE_前置任务) {
            oppType = TaskAssociated.ASSOCIATED_TYPE_后置任务;
            if (null == task.startDate && null != associatedTask.endDate) {
                task.startDate = DateUtil.getNextDay(associatedTask.endDate, 1);
                dao.updateSpecialFields(task, "startDate");
            }
        }
        if (associatedType == TaskAssociated.ASSOCIATED_TYPE_后置任务) {
            oppType = TaskAssociated.ASSOCIATED_TYPE_前置任务;
            if (null == associatedTask.startDate && null != task.endDate) {
                associatedTask.startDate = DateUtil.getNextDay(task.endDate, 1);
                dao.updateSpecialFields(associatedTask, "startDate");
            }
        }
        if (task.objectType == Task.OBJECTTYPE_测试计划 && associatedTask.objectType == Task.OBJECTTYPE_测试用例) {
            addTestPlanTestCase(account, task, associatedTask);
        } else {
            TaskAssociated bean = dao.getTaskAssociated(task.id, associatedTask.id);
            if (bean != null) {
                throw new AppException("关联对象已经存在.【" + associatedTask.name + "】");
            }
            bean = new TaskAssociated();
            bean.companyId = task.companyId;
            bean.taskId = task.id;
            bean.associatedTaskId = associatedTask.id;
            bean.associatedType = associatedType;
            dao.add(bean);
            //
            try {//兼容之前的问题
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

        //关联数量计数
        task.associateCount += 1;
        associatedTask.associateCount += 1;
        dao.updateSpecialFields(task, "associateCount");
        dao.updateSpecialFields(associatedTask, "associateCount");


        //前后置关联时，开始截止时间校验
        if (associatedType == TaskAssociated.ASSOCIATED_TYPE_前置任务) {
            if (null != associatedTask.endDate) {
                if (null != task.startDate) {
                    if (task.startDate.before(associatedTask.endDate)) {
                        throw new AppException("前置关联对象【" + associatedTask.name + "】的截止时间不能晚于对象【" + task.name + "】的开始时间");
                    }
                }
            }
        }

        if (associatedType == TaskAssociated.ASSOCIATED_TYPE_后置任务) {
            if (null != associatedTask.startDate) {
                if (null != task.endDate && task.endDate.after(associatedTask.startDate)) {
                    throw new AppException("后置关联对象【" + associatedTask.name + "】的开始时间不能早于对象【" + task.name + "】的截止时间");
                }
            }
        }

        //关联任务发送通知
        int changeLogType = ChangeLog.TYPE_新增关联对象;
        String notificationName = "对象关联其它对象";

        int associtedChangeLogType = ChangeLog.TYPE_被对象关联;
        String associtedNotificationName = "对象被其它对象关联";

        if (associatedType == TaskAssociated.ASSOCIATED_TYPE_前置任务) {
            changeLogType = ChangeLog.TYPE_关联前置对象;
            notificationName = "对象关联前置对象";
            associtedChangeLogType = ChangeLog.TYPE_被关联为前置对象;
            associtedNotificationName = "对象被关联为前置对象";
        }


        if (associatedType == TaskAssociated.ASSOCIATED_TYPE_后置任务) {
            changeLogType = ChangeLog.TYPE_关联后置对象;
            notificationName = "对象关联后置对象";
            associtedChangeLogType = ChangeLog.TYPE_被关联为后置对象;
            associtedNotificationName = "对象被关联为后置对象";
        }

        TaskSimpleInfo associatedSimpleTask = TaskSimpleInfo.createTaskSimpleinfo(associatedTask);
        addChangeLog(account, 0, task.id, //任务动态
                changeLogType,
                JSONUtil.toJson(associatedSimpleTask));
        addChangeLog(account, task.projectId, 0,//项目动态
                changeLogType,
                JSONUtil.toJson(TaskSimpleInfo.createTaskSimpleinfo(task)));
        Map<String, Object> map = new HashMap<>();
        map.put("associatedTask", associatedSimpleTask);
        sendNotificationForTask(account,
                task,
                AccountNotificationSetting.TYPE_对象关联其它对象,
                notificationName,
                map);

        //被关联任务也需要发送通知
        addChangeLog(account, 0, associatedTask.id, //任务动态
                associtedChangeLogType,
                JSONUtil.toJson(TaskSimpleInfo.createTaskSimpleinfo(task)));
        Map<String, Object> map0 = new HashMap<>();
        map0.put("associatedTask", TaskSimpleInfo.createTaskSimpleinfo(task));
        sendNotificationForTask(account,
                associatedTask,
                AccountNotificationSetting.TYPE_对象被其它对象关联,
                associtedNotificationName,
                map0);
    }


    /**
     * 解除父子任务关系
     */
    public void dismisPubSubTask(Account account, int subTaskId) {
        TaskInfo subTask = dao.getExistedById(TaskInfo.class, subTaskId);
        if (subTask.parentId == 0) {
            return;
        }
        checkProjectPermission(account, subTask.projectId, Permission.ID_编辑TASK + subTask.objectType);
        TaskInfo parentTask = dao.getExistedById(TaskInfo.class, subTask.parentId);
        checkPermissionForTask(account, parentTask);
        checkProjectPermission(account, parentTask.projectId, Permission.ID_编辑TASK + parentTask.objectType);

        ProjectStatusDefine statusDefine = dao.getById(ProjectStatusDefine.class, subTask.status);
        parentTask.subTaskCount--;
        if (statusDefine != null && statusDefine.type == ProjectStatusDefine.TYPE_结束状态) {
            parentTask.finishSubTaskCount--;
        }
        dao.update(parentTask);
        //
        TaskSimpleInfo subTaskSimple = TaskSimpleInfo.createTaskSimpleinfo(subTask);
        addChangeLog(account, 0, subTask.parentId, ChangeLog.TYPE_删除子任务, JSONUtil.toJson(subTaskSimple));
        addChangeLog(account, subTask.projectId, 0, ChangeLog.TYPE_删除子任务,
                JSONUtil.toJson(TaskSimpleInfo.createTaskSimpleinfo(parentTask)));
        Map<String, Object> map = new HashMap<>();
        map.put("subTask", subTaskSimple);
        sendNotificationForTask(account, parentTask, AccountNotificationSetting.TYPE_对象删除子对象, "对象解除子对象", map);
        subTask.parentId = 0;
        dao.update(subTask);
    }


    public void addTaskParentRelation(Account account, TaskInfo parentTask, TaskInfo subTask) {
        //checkProjectId(task.projectId, associatedTask.projectId, "数据不存在");
        if (subTask.id == parentTask.id) {
            return;
        }
        //已添加
        if (subTask.parentId == parentTask.id) {
            return;
        }
        if (parentTask.parentId == subTask.id) {
            throw new AppException("不可互相添加父子对象");
        }
        if (parentTask.companyId != subTask.companyId) {//不能跨企业
            throw new AppException("权限不足");
        }
        if (parentTask.isDelete || subTask.isDelete) {
            throw new AppException("对象已经删除");
        }
        //先解除原来的关系
        if (subTask.parentId > 0) {
            dismisPubSubTask(account, subTask.id);
        }

        if (null != subTask.startDate) {
            if (null != parentTask.startDate && subTask.startDate.before(parentTask.startDate)) {
                throw new AppException("子对象【" + subTask.name + "】的开始时间不能早于父对象【" + parentTask.name + "】的开始时间");
            }
        }

        if (null != subTask.endDate) {
            if (null != parentTask.endDate && subTask.endDate.after(parentTask.endDate)) {
                throw new AppException("子对象【" + subTask.name + "】的截止时间不能晚于父对象【" + parentTask.name + "】的截止时间");
            }
        }

        subTask.parentId = parentTask.id;
//        dao.update(subTask);
        dao.updateTaskParentId(subTask);

        if (parentTask.objectType == Task.OBJECTTYPE_测试计划 && subTask.objectType == Task.OBJECTTYPE_测试用例) {
            addTestPlanTestCase(account, parentTask, subTask);
        } else {
            ProjectStatusDefine statusDefine = dao.getById(ProjectStatusDefine.class, subTask.status);
            parentTask.subTaskCount++;
            if (statusDefine != null && statusDefine.type == ProjectStatusDefine.TYPE_结束状态) {
                parentTask.finishSubTaskCount++;
            }
//            dao.update(parentTask);
            dao.updateParentTaskCount(parentTask);
            //
            TaskSimpleInfo subTaskSimple = TaskSimpleInfo.createTaskSimpleinfo(subTask);
            addChangeLog(account, 0, subTask.parentId, ChangeLog.TYPE_新增子任务, JSONUtil.toJson(subTaskSimple));
            addChangeLog(account, subTask.projectId, 0, ChangeLog.TYPE_新增子任务,
                    JSONUtil.toJson(TaskSimpleInfo.createTaskSimpleinfo(parentTask)));
            Map<String, Object> map = new HashMap<>();
            map.put("subTask", subTaskSimple);
            sendNotificationForTask(account, parentTask, AccountNotificationSetting.TYPE_对象新增子对象, "对象新增子对象", map);
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
        //先删除之前的时间
        dao.delete(TaskRemindTime.class, QueryWhere.create().where("task_remind_id", bean.id));
        if (bean.remindRules != null && bean.remindRules.size() > 0) {
            for (TaskRemindRule e : bean.remindRules) {
                Date remindTime = null;
                if (e.type <= 0 || e.unit <= 0) {
                    continue;
                }
                if (e.type == TaskRemindRule.TYPE_开始时间前) {
                    remindTime = calcRemindTime(task.startDate, -e.value, e.unit);
                }
                if (e.type == TaskRemindRule.TYPE_开始时间后) {
                    remindTime = calcRemindTime(task.startDate, e.value, e.unit);
                }
                if (e.type == TaskRemindRule.TYPE_截止时间前) {
                    remindTime = calcRemindTime(task.endDate, -e.value, e.unit);
                }
                if (e.type == TaskRemindRule.TYPE_截止时间后) {
                    remindTime = calcRemindTime(task.endDate, e.value, e.unit);
                }
                if (e.type == TaskRemindRule.TYPE_指定时间) {
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
        if (unit == TaskRemindRule.UNIT_分钟) {
            return DateUtil.addMinute(date, (int) value);
        }
        if (unit == TaskRemindRule.UNIT_小时) {
            return DateUtil.addHour(date, (int) value);
        }
        if (unit == TaskRemindRule.UNIT_天) {
            return DateUtil.addDay(date, (int) value);
        }
        return null;
    }

    public Account getExistedAccountByUserName(String userName) {
        Account account = dao.getAccountByUserName(userName);
        if (account == null) {
            throw new AppException("用户不存在" + userName);
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
     * 返回组织架构
     */
    public List<DepartmentSimpleInfo> getMyDepartmentList(AccountInfo account) {
        DepartmentQuery query = new DepartmentQuery();
        query.accountId = account.id;
        query.companyId = account.companyId;
        query.type = Department.TYPE_人员;
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
            throw new AppException("TOKEN不存在");
        }
        ConnectionDetailInfo detailInfo = new ConnectionDetailInfo();
        ConnectionInfo info = null;
        MachineLoginSession session = dao.getExistedById(MachineLoginSession.class, loginToken.sessionId);
        if (session.machineId > 0) {
            Machine machine = dao.getExistedById(Machine.class, session.machineId);
            if (machine.isDelete) {
                throw new AppException("主机不存在或已删除");
            }
            info = BizUtil.createConnectionInfo(machine);
            info.enableInput = loginToken.type == MachineLoginToken.TYPE_交互;
        } else if (session.cmdbMachineId > 0) {
            CmdbMachine machine = dao.getExistedById(CmdbMachine.class, session.cmdbMachineId);
            if (machine.isDelete) {
                throw new AppException("主机不存在或已删除");
            }
            info = BizUtil.createConnectionInfo(machine);
            info.enableInput = loginToken.type == MachineLoginToken.TYPE_交互;
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
     * 是否是正常用户
     */
    public boolean isNormalAccount(Account account) {
        return account.status == Account.STATUS_有效 && account.isActivated;
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
        //includeFields(includeField)  这个不能用
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
     * 同步项目集
     */
    public void syncTasksFromProject0(Account account, int projectId) {
        //从现有项目全部导进去
        ProjectQuery projectQuery = new ProjectQuery();
        projectQuery.companyId = account.companyId;
        projectQuery.status = Project.STATUS_运行中;
        projectQuery.pageSize = 10000;
        projectQuery.isTemplate = false;
        projectQuery.excludeId = projectId;
        projectQuery.idSort = Query.SORT_TYPE_ASC;
        List<ProjectInfo> projectList = dao.getList(projectQuery);
        // 查询已经删除的数据
        List<Integer> deleteIdList =
                projectList.stream().filter(project -> project.isDelete).map(project -> project.id)
                .collect(Collectors.toList());
        // 如果存在已删除的数据 那么直接更新旧的数据为delete
        if (CollUtil.isNotEmpty(deleteIdList)){
            dao.updateProjectTaskDeleteStatus(account.companyId, projectId, true, deleteIdList);
        }
        // 上面已经处理了 删除的任务了  下面就只用处理未删除的 就好了
        projectList.removeIf(project -> project.isDelete);
        // 查询所有的任务
        List<Integer> projectIdList =
                projectList.stream().map(project -> project.id).collect(Collectors.toList());
        List<Task> oldList
                = dao.getTaskByCompanyIdProjectIdAssociateProjectIdList(account.companyId, projectId, projectIdList);
        Map<Integer, List<Task>> oldTaskMap
                = oldList.stream().collect(Collectors.groupingBy(task -> task.associateProjectId));

        // 获取项目字段的定义
        List<ProjectFieldDefine> projectFieldDefineList = dao.getProjectFieldDefineByPsField(account.companyId, Task.OBJECTTYPE_项目清单);
        for (ProjectInfo e : projectList) {
            // 获取项目中有关联项目的任务
            List<Task> oldTaskList = oldTaskMap.get(e.id);
            Task old = CollUtil.isEmpty(oldTaskList) ? null : oldTaskList.get(0);
            // 如果项目未删除
            //若任务存在则更新任务的名字、开始于截止时间
            if (old != null) {
                old.name = e.name;
                old.startDate = e.startDate;
                //项目集甘特图不显示的问题
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
            // 如果项目未删除
            // 若任务不存在则创建任务
            TaskDetailInfo task = new TaskDetailInfo();
            task.name = e.name;
            task.companyId = account.companyId;
            task.projectId = projectId;
            task.objectType = Task.OBJECTTYPE_项目清单;
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
            //项目集跳转项目保留字段
            task.customFields.put("associateProjectUuid", e.uuid);
            dao.update(task);
        }
    }

    /**
     * 项目集自定义字段赋值
     */
    private void setupProjectSetCustomerFields(Task task, ProjectInfo project) {
        setupProjectSetCustomerFields(task, project, null);
    }

    /**
     * 项目集自定义字段赋值
     */
    private void setupProjectSetCustomerFields(Task task, ProjectInfo project, List<ProjectFieldDefine> defines) {
        Map<String, Object> cs = task.customFields;
        if (null == cs) {
            cs = new HashMap<>(16);
        }
        //甘特图颜色
        cs.put("statusColor", project.color);
        if (null == defines) {
            //获取项目集同步自定义字段名
            defines = dao.getProjectFieldDefineByPsField(project.companyId, Task.OBJECTTYPE_项目清单);
        }
        if (!BizUtil.isNullOrEmpty(defines)) {
            defines.sort(Comparator.comparing(k -> k.field));
            for (ProjectFieldDefine fd : defines) {
                if (fd.name.contains("运行状态")) {
                    cs.put(fd.field, project.workflowStatusName);
                } else if (fd.name.contains("运行备注")) {
                    cs.put(fd.field, project.runLogRemark);
                } else if (fd.name.contains("运行天数")) {
                    if (null != project.startDate) {
                        cs.put(fd.field, DateUtil.getDayDiff(project.startDate, new Date()));
                    }
                } else if (fd.name.contains("项目分组")) {
                    cs.put(fd.field, project.group);
                } else if (fd.name.contains("项目状态")) {
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
     * 钉钉考勤数据同步(未与钉钉版CS打通)
     */
    public void syncDingtalkAttendance(Date date) {
        Date dayMonth = DateUtil.getBeginOfMonth(date);
        Date theMonth = DateUtil.getBeginOfMonth();
        if (dayMonth.equals(theMonth) || dayMonth.after(theMonth)) {
            throw new AppException("仅能同步过去的月份考勤数据");
        }
        //clear
        dao.deleteAttendaceByMonth(new Date(dayMonth.getTime() - 1));
        List<DingtalkMember> members = dao.getAll(DingtalkMember.class);
        if (!BizUtil.isNullOrEmpty(members)) {
            //钉钉每次最多查询50人
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
     * 同步钉钉成员
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
     * 校验字段取值唯一性
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
        if (bean.type == ProjectFieldDefine.TYPE_日期 && bean.isSystemField) {
            if (bean.showTimeField) {
                columnName = "DATE_FORMAT(" + columnName + ", '%Y-%m-%d %H:%i:%s')";
            } else {
                columnName = "DATE_FORMAT(" + columnName + ", '%Y-%m-%d')";
            }
        }
        String sql = "select t.fname from (select " + columnName + " as fname,count(*) as count0 from `t_task` where company_id = ? and project_id=? and object_type=? and is_delete =false  group by fname having  count0>1";
        if (bean.type == ProjectFieldDefine.TYPE_数值) {
            sql += " and fname >0) t";
        } else {
            sql += " and fname is not null) t";
        }
        List<String> ret = dao.queryForStrings(sql, companyId, projectId, objectType);
        if (!BizUtil.isNullOrEmpty(ret)) {
            logger.warn(DumpUtil.dump(ret));
            for (String s : ret) {
                if (!BizUtil.isNullOrEmpty(s)) {
                    throw new AppException("字段【" + bean.name + "】存在重复值:" + s);
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
        //自定义字段
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
        if (bean.type == ProjectFieldDefine.TYPE_日期 && bean.isSystemField) {
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
        if (bean.type == ProjectFieldDefine.TYPE_日期 && bean.isSystemField) {
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
                throw new AppException("字段【" + bean.name + "】已存在重复值");
            } else {
                at.addAndGet(id);
//                = new AtomicInteger(id);
            }
        }
    }

    private String getTaskFieldValue(ProjectFieldDefine e, Object value) {
        if (e.type == ProjectFieldDefineInfo.TYPE_日期) {
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
                    repositoryInfo.name = repositoryInfo.name + (" (" + repositoryInfo.ownerDepartmentList.stream().map(k -> k.name).collect(Collectors.joining("、")) + ")");
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
        //追加查询条件
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
