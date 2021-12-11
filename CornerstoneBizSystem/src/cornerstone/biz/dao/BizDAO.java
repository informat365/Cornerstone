package cornerstone.biz.dao;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cornerstone.biz.domain.*;
import cornerstone.biz.domain.Account.AccountQuery;
import cornerstone.biz.domain.AccountOverDueTask.AccountProjectOverDueTask;
import cornerstone.biz.domain.CmdbApplication.CmdbApplicationInfo;
import cornerstone.biz.domain.CmdbInstance.CmdbInstanceInfo;
import cornerstone.biz.domain.CmdbMachine.CmdbMachineInfo;
import cornerstone.biz.domain.Company.CompanyInfo;
import cornerstone.biz.domain.CompanyMember.CompanyMemberInfo;
import cornerstone.biz.domain.CompanyMember.CompanyMemberQuery;
import cornerstone.biz.domain.Department.DepartmentInfo;
import cornerstone.biz.domain.Department.DepartmentQuery;
import cornerstone.biz.domain.File.FileInfo;
import cornerstone.biz.domain.Machine.MachineInfo;
import cornerstone.biz.domain.Project.ProjectInfo;
import cornerstone.biz.domain.ProjectArtifact.ProjectArtifactInfo;
import cornerstone.biz.domain.ProjectDataPermission.ProjectDataPermissionInfo;
import cornerstone.biz.domain.ProjectFieldDefine.ProjectFieldDefineInfo;
import cornerstone.biz.domain.ProjectMember.ProjectMemberInfo;
import cornerstone.biz.domain.ProjectModule.ProjectModuleInfo;
import cornerstone.biz.domain.ProjectObjectTypeTemplate.ProjectObjectTypeTemplateInfo;
import cornerstone.biz.domain.ProjectPriorityDefine.ProjectPriorityDefineInfo;
import cornerstone.biz.domain.ProjectStatusDefine.ProjectStatusDefineInfo;
import cornerstone.biz.domain.Role.RoleInfo;
import cornerstone.biz.domain.Task.TaskDetailInfo;
import cornerstone.biz.domain.Task.TaskInfo;
import cornerstone.biz.domain.Task.TaskQuery;
import cornerstone.biz.domain.TaskRemind.TaskRemindInfo;
import cornerstone.biz.domain.TaskWorkTimeLog.TaskWorkTimeLogInfo;
import cornerstone.biz.domain.TaskWorkTimeLog.TaskWorkTimeLogQuery;
import cornerstone.biz.domain.WikiPage.WikiPageDetailInfo;
import cornerstone.biz.domain.WikiPageChangeLog.WikiPageChangeLogDetailInfo;
import cornerstone.biz.domain.WorkflowDefinePermission.WorkflowDefinePermissionInfo;
import cornerstone.biz.domain.WorkflowFormDefine.WorkflowFormDefineInfo;
import cornerstone.biz.taskjob.BizTaskJobs;
import cornerstone.biz.util.*;
import jazmin.core.app.AppException;
import jazmin.driver.jdbc.ConnectionException;
import jazmin.driver.jdbc.smartjdbc.Query;
import jazmin.driver.jdbc.smartjdbc.QueryWhere;
import jazmin.driver.jdbc.smartjdbc.provider.SelectProvider;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

/**
 * @author cs
 */
public class BizDAO extends ITFDAO {

    private static final Logger logger = LoggerFactory.get(BizDAO.class);

    /**
     * 手机号查询用户信息
     */
    public Account getAccountByMobileNo(String mobileNo) {
        return getDomain(Account.class, QueryWhere.create().where("mobile_no", mobileNo));
    }

    /**
     * 微信union id查询用户信息
     */
    public Account getAccountByWxUnionIdForUpdate(String wxUnionId) {
        return getDomain(Account.class, QueryWhere.create().where("wx_union_id", wxUnionId).forUpdate());
    }

    /**
     * 微信open id查询用户信息
     */
    public Account getAccountByWxOpenIdForUpdate(String wxOpenId) {
        return getDomain(Account.class, QueryWhere.create().where("wx_open_id", wxOpenId).forUpdate());
    }


    /**
     * 飞书open id查询用户信息
     */
    public Account getAccountByLarkOpenId(String openId) {
        return getDomain(Account.class, QueryWhere.create().where("lark_open_id", openId));
    }

    /**
     * 手机号查询用户信息（互斥锁）
     */
    public Account getAccountByMobileNoForUpdate(String mobileNo) {
        return getDomain(Account.class, QueryWhere.create().where("mobile_no", mobileNo).forUpdate());
    }

    /**
     * 邮箱查询用户信息
     */
    public Account getAccountByEmailForUpdate(String email) {
        return getDomain(Account.class, QueryWhere.create().where("email", email).forUpdate());
    }

    /**
     * 用户名查询用户信息
     */
    public Account getAccountByUserName(String userName) {
        return getDomain(Account.class, QueryWhere.create().where("user_name", userName));
    }

    /**
     * 查询企业下某个用户名对应的成员ID
     */
    public Integer getCompanyAccountIdByUserName(int companyId, String userName) {
        String sql = "select a.id from t_company_member t left join t_account a on t.account_id = a.id " +
                "where t.company_id =? and a.user_name=? ";
        return queryForInteger(sql, companyId, userName);
    }

    /**
     * 用户名和手机号查询用户信息
     */
    public Account getAccountByUserNameOrEmail(String userNameOrEmail) {
        String sql = "select * from t_account where user_name=? or email=?";
        return queryObject(Account.class, sql, userNameOrEmail, userNameOrEmail);
    }

    /**
     * 用户名和手机号查询企业下的用户信息
     */
    public Account getCompanyAccountByUserNameOrEmail(int companyId, String userNameOrEmail) {
        String sql = "select * from t_account where company_id =? and ( user_name=? or email=?)";
        return queryObject(Account.class, sql, companyId, userNameOrEmail, userNameOrEmail);
    }

    /**
     * 邮箱查询用户信息
     */
    public Account getAccountByEmail(String email) {
        return getDomain(Account.class, QueryWhere.create().where("email", email));
    }

    /**
     * 手机号查询用户信息（互斥锁）
     */
    public Account getExistedAccountByMobileNoForUpdate(String mobileNo) {
        Account account = getDomain(Account.class, QueryWhere.create().where("mobile_no", mobileNo).forUpdate());
        if (account == null) {
            throw new AppException("账号不存在");
        }
        return account;
    }

    public Account getAccountByUserNameForUpdate(String userName) {
        return getDomain(Account.class,
                QueryWhere.create().where("user_name", userName).
                        forUpdate());
    }

    /**
     * @param accountUuid
     * @return
     */
    public Account getAccountByUuid(String accountUuid) {
        return getDomain(Account.class, QueryWhere.create().where("uuid", accountUuid));
    }

    /**
     * @param accountUuid
     * @return
     */
    public Account getAccountByUuidForUpdate(String accountUuid) {
        return getDomain(Account.class, QueryWhere.create().where("uuid", accountUuid).forUpdate());
    }

    //

    /**
     * @return
     */
    public List<Integer> getAllPrivateDeployCompanyIdList() {
        String sql = "select id from t_company where version=? and status=?";
        return queryForIntegers(sql, Company.VERSION_私有部署版, Company.STATUS_许可中);
    }

    /**
     * @return
     */
    public List<Account> getAllPrivateDeployCompanyAccountList() {
        List<Integer> companyIds = getAllPrivateDeployCompanyIdList();
        if (companyIds.isEmpty()) {
            return new ArrayList<>();
        }
        AccountQuery query = new AccountQuery();
        query.companyIdInList = BizUtil.convertList(companyIds);
        query.status = Account.STATUS_有效;
        query.isActivated = true;
        return getAll(query);
    }
    //
    //

    /**
     * @param accountId
     * @return
     */
    public AccountToken getAccountTokenByAccountId(int accountId) {
        return getDomain(AccountToken.class, QueryWhere.create().where("account_id", accountId));
    }

    /**
     * @param accountId
     * @return
     */
    public AccountToken getAccountTokenByAccountIdForUpdate(int accountId) {
        return getDomain(AccountToken.class, QueryWhere.create().where("account_id", accountId).forUpdate());
    }

    /**
     * @param accountId
     * @return
     */
    public List<Integer> getCompanyIdList(int accountId) {
        String sql = "select a.id from t_company a inner join t_company_member i1 on i1.company_id=a.id where i1.account_id=?";
        return queryForIntegers(sql, accountId);
    }

    /**
     * @param uuid
     * @return
     */
    public CompanyInfo getCompanyInfoByUuid(String uuid) {
        return getDomain(CompanyInfo.class, QueryWhere.create().where("uuid", uuid));
    }

    /**
     * @param uuid
     * @return
     */
    public ProjectInfo getProjectInfoByUuid(String uuid) {
        return getDomain(ProjectInfo.class, QueryWhere.create().where("uuid", uuid));
    }

    /**
     * @param projectId
     * @param objectType
     * @param field
     * @return
     */
    public ProjectFieldDefine getProjectFieldDefineByProjectIdObjectTypeField(int projectId, int objectType, String field) {
        return getDomain(ProjectFieldDefine.class, QueryWhere.create().
                where("project_id", projectId).
                where("object_type", objectType).
                where("field", field)
        );
    }

    /**
     * @param projectId
     * @param objectType
     * @return
     */
    public List<ProjectFieldDefineInfo> getProjectFieldDefineInfoList(int projectId, int objectType) {
        return getList(ProjectFieldDefineInfo.class, QueryWhere.create().
                where("project_id", projectId).
                where("object_type", objectType).
                orderBy("sort_weight asc"));
    }

    /**
     * @param id
     * @param sortWeight
     */
    public void updateProjectFieldDefineSortWeightIsShow(int accountId, int id, int sortWeight, boolean isShow) {
        executeUpdate("update t_project_field_define set update_time=now(),update_account_id=?,sort_weight=?,is_show=? where id=?",
                accountId, sortWeight, isShow, id);
    }

    /**
     * @param projectId
     * @param objectType
     * @return
     */
    public List<ProjectStatusDefineInfo> getProjectStatusDefineInfoList(int projectId, int objectType) {
        return getList(ProjectStatusDefineInfo.class, QueryWhere.create().
                where("project_id", projectId).
                where("object_type", objectType).
                orderBy("type asc"));
    }

    /**
     * @param projectId
     * @param objectType
     * @param type
     * @return
     */
    public int getProjectStatusDefineListCount(int projectId, int objectType, int type) {
        return getListCount(ProjectStatusDefine.class, QueryWhere.create().
                where("project_id", projectId).
                where("object_type", objectType).
                where("type", type));
    }

    /**
     * @param projectId
     * @param objectType
     * @return
     */
    public int getProjectStatusDefineListCount(int projectId, int objectType) {
        return getListCount(ProjectStatusDefine.class, QueryWhere.create().
                where("project_id", projectId).
                where("object_type", objectType));
    }

    /**
     * @param projectId
     * @param objectType
     * @return
     */
    public List<ProjectStatusDefine> getProjectStatusDefineList(int projectId, int objectType) {
        return getList(ProjectStatusDefine.class, QueryWhere.create().
                where("project_id", projectId).
                where("object_type", objectType));
    }

    /**
     * 删除项目任务数据
     * @param companyId
     * @param projectId
     * @param isDelete
     * @param associateProjectIdList
     * @return
     */
    public int updateProjectTaskDeleteStatus(int companyId,
                                          int projectId,
                                          boolean isDelete,
                                          List<Integer> associateProjectIdList) {

        StringBuilder sbu = new StringBuilder();
        sbu.append("update t_task set is_delete = ? where company_id = ? and project_id = ? and is_delete <> ? ");
        sbu.append(" and associate_project_id IN(")
                .append(JdbcUtil.getCollPlaceholder(associateProjectIdList)).append(")");
        List<Object> params = Lists.newArrayList(isDelete, companyId, projectId,isDelete);
        associateProjectIdList.forEach(params::add);

        return executeUpdate(sbu.toString(), params.toArray());
    }

    /**
     * @param projectId
     * @param objectType
     * @return
     */
    public List<ProjectPriorityDefineInfo> getProjectPriorityDefineInfoList(int projectId, int objectType) {
        return getList(ProjectPriorityDefineInfo.class, QueryWhere.create().
                where("project_id", projectId).
                where("object_type", objectType).
                orderBy("sort_weight asc")
        );
    }

    public List<ProjectModuleInfo> getEnabledProjectModuleInfoList(int projectId) {
        return getList(ProjectModuleInfo.class, QueryWhere.create().
                where("project_id", projectId).
                where("is_enable", true).
                orderBy("sort_weight asc"));
    }

    /**
     * @param projectId
     * @return
     */
    public List<ProjectModuleInfo> getAllProjectModuleInfoList(int projectId) {
        return getList(ProjectModuleInfo.class, QueryWhere.create().
                where("project_id", projectId).
                orderBy("sort_weight asc"));
    }

    /**
     * @param projectId
     * @param objectType
     */
    public void setProjectPriorityDefineIsDefaultFalse(int projectId, int objectType) {
        executeUpdate("update t_project_priority_define set is_default=false where project_id=? and object_type=?",
                projectId, objectType);
    }

    /**
     * @param projectId
     * @param objectType
     * @return
     */
    public int getProjectPriorityDefineListCount(int projectId, int objectType) {
        return getListCount(ProjectPriorityDefine.class, QueryWhere.create().
                where("project_id", projectId).
                where("object_type", objectType)
        );
    }

    /**
     * @param companyId
     * @return
     */
    public List<DepartmentInfo> getAllDepartmentInfo(int companyId) {
        List<DepartmentInfo> departmentlist = getAllDepartmentInfoWithoutAccount(companyId);
        DepartmentQuery query = new DepartmentQuery();
        query.companyId = companyId;
        query.type = DepartmentInfo.TYPE_人员;
        query.accountStatus = Account.STATUS_有效;
        query.levelSort = Query.SORT_TYPE_ASC;
        query.pageSize = 10000;
        List<DepartmentInfo> staffList = getList(query);
        if (!staffList.isEmpty()) {
            departmentlist.addAll(staffList);
        }
        return departmentlist;
    }

    public List<DepartmentInfo> getAllDepartmentInfoWithoutAccount(int companyId) {
        return getList(DepartmentInfo.class, QueryWhere.create().
                where("company_id", companyId).
                where("type", DepartmentInfo.TYPE_组织架构).
                orderBy("level asc"));
    }

    /**
     * @param companyId
     * @return
     */
    public List<Department> getDepartmentByCompanyIdType(int companyId, int type) {
        return getList(Department.class, QueryWhere.create().
                where("company_id", companyId).
                where("type", type)
        );
    }

    /**
     * @param companyId
     * @param level
     * @return
     */
    public Department getDepartmentByCompanyIdLevel(int companyId, int level) {
        return getDomain(Department.class, QueryWhere.create().
                where("company_id", companyId).
                where("level", level));
    }

    /**
     * @param query
     * @return
     */
    public int getDepartmentListCount(DepartmentQuery query) {
        return getListCount(query);
    }

    /**
     * @param type
     * @return
     */
    public List<Permission> getPermissionList(int type, int companyVersion) {
        String sql = "select * from t_permission where type=? and (company_version=0 or company_version=?) "
                + "order by sort_weight asc";
        return queryList(Permission.class, sql, type, companyVersion);
    }

    /**
     * @param companyId
     * @param type
     * @return
     */
    public List<RoleInfo> getRoleInfoList(int companyId, int type) {
        return getList(RoleInfo.class, QueryWhere.create().
                where("company_id", companyId).
                where("type", type).
                orderBy("id asc")
        );
    }

    /**
     * @param roleDefineId
     */
    public void deleteAccountRoles(int roleDefineId) {
        delete(CompanyMemberRole.class, QueryWhere.create().where("role_id", roleDefineId));
    }

    /**
     * @param projectId
     * @return
     */
    public List<ProjectDataPermissionInfo> getProjectDataPermissionInfoList(int projectId, int objectType) {
        return getList(ProjectDataPermissionInfo.class,
                QueryWhere.create().
                        where("project_id", projectId).
                        where("object_type", objectType));
    }

    /**
     * @param projectMemberId
     */
    public void deleteProjectMemberRolesByProjectMemberId(int projectMemberId) {
        delete(ProjectMemberRole.class, QueryWhere.create().where("project_member_id", projectMemberId));
    }

    /**
     * @param companyId
     * @param accountId
     * @return
     */
    public CompanyMemberInfo getCompanyMemberInfoByCompanyIdAccountId(int companyId, int accountId) {
        return getDomain(CompanyMemberInfo.class,
                QueryWhere.create().
                        where("company_id", companyId).
                        where("account_id", accountId));
    }

    /**
     * @param projectId
     * @param accountId
     * @return
     */
    public ProjectMemberInfo getProjectMemberInfoByProjectIdAccountId(int projectId, int accountId) {
        return getDomain(ProjectMemberInfo.class,
                QueryWhere.create().
                        where("project_id", projectId).
                        where("account_id", accountId));
    }

    public ProjectMemberInfo getProjectMemberInfoByProjectIdAccountIdForUpdate(int projectId, int accountId) {
        return getDomain(ProjectMemberInfo.class,
                QueryWhere.create().
                        where("project_id", projectId).
                        where("account_id", accountId).forUpdate());
    }

    /**
     * @param projectIterationId
     */
    public void deleteProjectIterationSteps(int projectIterationId) {
        delete(ProjectIterationStep.class, QueryWhere.create().
                where("iteration_id", projectIterationId));
    }

    /**
     * @param companyId
     * @return
     */
    public TaskSerialNo getTaskSerialNoForUpdate(int companyId) {
        return getDomain(TaskSerialNo.class,
                QueryWhere.create().
                        where("company_id", companyId).
                        forUpdate());
    }

    public TaskSerialNo getTaskSerialNoForUpdate(int companyId, String objectTypeSystemName) {
        TaskSerialNo bean = getDomain(TaskSerialNo.class,
                QueryWhere.create().
                        where("company_id", companyId).
                        where("object_type_system_name", objectTypeSystemName).
                        forUpdate());
        if (bean == null) {
            bean = new TaskSerialNo();
            bean.companyId = companyId;
            bean.objectTypeSystemName = objectTypeSystemName;
            bean.serialNo = 10000;
            add(bean);
            //
            bean = getDomain(TaskSerialNo.class,
                    QueryWhere.create().
                            where("company_id", companyId).
                            where("object_type_system_name", objectTypeSystemName).
                            forUpdate());
        }
        return bean;
    }

    /**
     * @param uuid
     * @return
     */
    public TaskInfo getTaskInfoByUuid(String uuid) {
        return getDomain(TaskInfo.class, QueryWhere.create().
                where("uuid", uuid).
                where("is_delete", false));
    }

    /**
     * @param serialNo
     * @return
     */
    public TaskInfo getExistedTaskBySerialNo(int companyId, String serialNo) {
        TaskInfo bean = getDomain(TaskInfo.class, QueryWhere.create().
                where("company_id", companyId).
                where("serial_no", serialNo).
                where("is_delete", false));
        if (bean == null) {
            throw new AppException("任务不存在" + serialNo);
        }
        return bean;
    }

    public Task getTaskByCompanyIdSerialNo(int companyId, String serialNo) {
        return getDomain(Task.class, QueryWhere.create().
                where("company_id", companyId).
                where("serial_no", serialNo).
                where("is_delete", false));
    }

    /**
     * @param uuid
     * @return
     */
    public TaskDetailInfo getTaskDetailInfoByUuid(String uuid) {
        return getDomain(TaskDetailInfo.class, QueryWhere.create().
                where("uuid", uuid));
    }

    /**
     * @param uuid
     * @return
     */
    public Attachment getAttachmentByUuid(String uuid) {
        return getDomain(Attachment.class, QueryWhere.create().
                where("uuid", uuid));
    }

    /**
     * @param companyId
     * @return
     */
    public List<AccountMention> getAccountMentionList(int companyId) {
        String sql = "select a.id,concat(a.name,'(',a.user_name,')') as name,a.pinyin_name as pinyin from t_account a "
                + "inner join t_company_member i1 on i1.account_id=a.id "
                + "where i1.company_id=?";
        return queryList(AccountMention.class, sql, companyId);
    }

    /**
     * @param taskId
     * @param accountId
     * @return
     */
    public ChangeLog getLastChangeLogByTaskIdCreateAccountId(int taskId, int accountId) {
        return getDomain(ChangeLog.class, QueryWhere.create().
                where("task_id", taskId).
                where("create_account_id", accountId).
                orderBy("id desc").
                limit(1));
    }

    /**
     * @param token
     * @return
     */
    public MachineLoginToken getMachineLoginTokenByToken(String token) {
        return getDomain(MachineLoginToken.class, QueryWhere.create().
                where("token", token));
    }

    /**
     * @param token
     * @return
     */
    public MachineLoginToken getMachineLoginTokenByTokenForUpdate(String token) {
        return getDomain(MachineLoginToken.class, QueryWhere.create().
                where("token", token).forUpdate());
    }


    /**
     * @param sessionId
     * @return
     */
    public List<AccountSimpleInfo> getMachineLoginAccountList(int sessionId) {
        String sql = "select a.id,a.name,a.image_id,a.create_time,a.update_time from t_account a "
                + "inner join t_machine_login_token i1 on i1.account_id=a.id "
                + "where i1.session_id=" + sessionId;
        return queryList(AccountSimpleInfo.class, sql);
    }

    /**
     * 查询没删除的机器
     *
     * @param projectId
     * @param name
     * @return
     */
    public Machine getMachineByProjectIdName(int projectId, String name) {
        return getDomain(Machine.class, QueryWhere.create().
                where("project_id", projectId).
                where("name", name));
    }

    /**
     * 不包含删除的
     *
     * @param id
     * @param objectType
     * @return
     */
    public TaskInfo getExistedTaskByIdObjectType(int id, int objectType) {
        TaskInfo task = getDomain(TaskInfo.class, QueryWhere.create().
                where("id", id).
                where("object_type", objectType).
                where("is_delete", false));
        if (task == null) {
            String objectTypeName = BizTaskJobs.getDataDictName("Task.objectType", objectType);
            throw new AppException(objectTypeName + "不存在");
        }
        return task;
    }

    /**
     * 不包含删除的
     *
     * @param id
     * @param objectType
     * @return
     */
    public Task getExistedTaskByIdObjectTypeForUpdate(int id, int objectType) {
        Task task = getDomain(Task.class, QueryWhere.create().
                where("id", id).
                where("object_type", objectType).
                where("is_delete", false).
                forUpdate());
        if (task == null) {
            String objectTypeName = BizTaskJobs.getDataDictName("Task.objectType", objectType);
            throw new AppException(objectTypeName + "不存在");
        }
        return task;
    }

    /**
     * @param projectId
     * @param objectType
     * @param name
     * @return
     */
    public ProjectStatusDefine getProjectStatusDefine(int projectId, int objectType,
                                                      String name, Boolean ignoreError) {
        ProjectStatusDefine bean = getDomain(ProjectStatusDefine.class, QueryWhere.create().
                where("project_id", projectId).
                where("object_type", objectType).
                where("name", name));
        if (bean == null) {
            if (ignoreError != null && ignoreError) {
                return null;
            }
            throw new AppException("状态字不在范围内【" + name + "】,请先在【项目设置->工作流】里添加状态");
        }
        return bean;
    }

    /**
     * @param projectId
     * @param objectType
     * @param name
     * @return
     */
    public ProjectPriorityDefine getProjectPriorityDefine(int projectId, int objectType,
                                                          String name, Boolean ignoreError) {
        ProjectPriorityDefine bean = getDomain(ProjectPriorityDefine.class, QueryWhere.create().
                where("project_id", projectId).
                where("object_type", objectType).
                where("name", name));
        if (bean == null) {
            if (ignoreError != null && ignoreError) {
                return null;
            }
            throw new AppException("优先级错误【" + name + "】");
        }
        return bean;
    }

    /**
     * @param companyId
     * @param name
     * @return
     */
    public Account getExistedAccountByCompanyIdName(int companyId, String name) {
        Account bean = getDomain(Account.class, QueryWhere.create().
                where("company_id", companyId).
                where("name", name));
        if (bean == null) {
            throw new AppException("找不到名为【" + name + "】的用户");
        }
        return bean;
    }

    public Account getAccountByCompanyIdName(int companyId, String name) {
        return getDomain(Account.class, QueryWhere.create().
                where("company_id", companyId).
                where("name", name));
    }

    public Account getAccountByCompanyIdLarkOpenId(int companyId, String larkOpenId) {
        return getDomain(Account.class, QueryWhere.create().
                where("company_id", companyId).
                where("lark_open_id", larkOpenId));
    }

    /**
     * @param projectId
     * @param name
     * @return
     */
    public ProjectIteration getExistedProjectIterationByProjectIdName(int projectId, String name) {
        ProjectIteration bean = getDomain(ProjectIteration.class, QueryWhere.create().
                where("project_id", projectId).
                where("name", name));
        if (bean == null) {
            throw new AppException("找不到名为【" + name + "】的迭代");
        }
        return bean;
    }

    /**
     * @param projectId
     * @param name
     * @return
     */
    public ProjectRelease getExistedProjectReleaseByProjectIdName(int projectId, String name) {
        ProjectRelease bean = getDomain(ProjectRelease.class, QueryWhere.create().
                where("project_id", projectId).
                where("name", name));
        if (bean == null) {
            throw new AppException("找不到名为【" + name + "】的Release");
        }
        return bean;
    }

    /**
     * @param projectId
     * @param name
     * @return
     */
    public ProjectSubSystem getExistedProjectSubSystemByProjectIdName(int projectId, String name) {
        ProjectSubSystem bean = getDomain(ProjectSubSystem.class, QueryWhere.create().
                where("project_id", projectId).
                where("name", name));
        if (bean == null) {
            throw new AppException("找不到名为【" + name + "】的子系统");
        }
        return bean;
    }

    /**
     * @param projectId
     * @param objectType
     * @return
     */
    public ProjectStatusDefine getProjectStatusDefineByProjectIdObjectTypeType(int projectId, int objectType, int type) {
        return getDomain(ProjectStatusDefine.class, QueryWhere.create().
                where("project_id", projectId).
                where("object_type", objectType).
                where("type", type)
        );
    }

    /**
     * @param projectId
     * @param objectType
     * @return
     */
    public ProjectPriorityDefine getDefaultProjectPriorityDefineByProjectIdObjectType(int projectId, int objectType) {
        return getDomain(ProjectPriorityDefine.class, QueryWhere.create().
                where("project_id", projectId).
                where("object_type", objectType).
                where("is_default", true)
        );
    }

    /**
     * @param projectId
     * @param objectType
     * @return
     */
    public ProjectModule getProjectModuleByProjectObjectTypeForUpdate(int projectId, int objectType) {
        return getDomain(ProjectModule.class, QueryWhere.create().
                where("project_id", projectId).
                where("object_type", objectType).
                forUpdate()
        );
    }

    /**
     * @param projectId
     * @param objectType
     * @return
     */
    public ProjectModule getProjectModuleByProjectObjectType(int projectId, int objectType) {
        return getDomain(ProjectModule.class, QueryWhere.create().
                where("project_id", projectId).
                where("object_type", objectType)
        );
    }

    /**
     * @param accountId
     * @return
     */
    public AccountSimpleInfo getExistedAccountSimpleInfoById(int accountId) {
        String sql = "select id,company_id,name,user_name,image_id from t_account where id=" + accountId;
        AccountSimpleInfo bean = queryObject(AccountSimpleInfo.class, sql);
        if (bean == null) {
            throw new AppException("账号不存在");
        }
        return bean;
    }

    public DepartmentSimpleInfo getExistedDepartmentSimpleInfoById(int departmentId) {
        String sql = "select id,company_id,name from t_department where id=" + departmentId+" and type ="+Department.TYPE_组织架构;
        DepartmentSimpleInfo bean = queryObject(DepartmentSimpleInfo.class, sql);
        if (bean == null) {
            throw new AppException("部门不存在");
        }
        return bean;
    }

    /**
     * @param type
     * @param associatedId
     * @return
     */
    public AccountNotification getAccountNotification(int type, int associatedId) {
        return getDomain(AccountNotification.class,
                QueryWhere.create().
                        where("type", type).
                        where("associated_id", associatedId).
                        whereSql("day(now())=day(create_time)")//不是今天创建
        );
    }

    /**
     * @return
     */
    public List<Integer> getNeedPushRemindInfo() {
        String sql = "select *,(hour(remind_time)*60+minute(remind_time))-(hour(now())*60+minute(now())) from t_remind a\n" +
                "where \n" +
                "(UNIX_TIMESTAMP(remind_time) - UNIX_TIMESTAMP(now()))<300 and\n" + //5分钟
                "(hour(remind_time)*60+minute(remind_time))-(hour(now())*60+minute(now())) <=5 and \n" +
                "(hour(remind_time)*60+minute(remind_time))-(hour(now())*60+minute(now()))>0 and \n" +
                "if(`repeat`=1,DAYOFYEAR(remind_time)=DAYOFYEAR(now()),true) and \n" +
                "if(`repeat`=3,WEEKDAY(remind_time)=WEEKDAY(now()),true) and\n" +
                "if(`repeat`=4,WEEKDAY(remind_time)=WEEKDAY(now()) and (DAYOFYEAR(now())-DAYOFYEAR(remind_time))%14=0,true) and\n" +
                "if(`repeat`=5,day(remind_time)=day(now()),true);";
        return queryForIntegers(sql);

    }

    /**
     * @param taskId
     * @param isCreateIndex
     */
    public void updateTaskIsCreateIndex(int taskId, boolean isCreateIndex) {
        executeUpdate("update t_task set is_create_index=? where id=?", isCreateIndex, taskId);
    }

    /**
     * @param id
     * @param isCreateIndex
     */
    public void updateWikiPageIsCreateIndex(int id, boolean isCreateIndex) {
        executeUpdate("update t_wiki_page set is_create_index=? where id=?", isCreateIndex, id);
    }

    /**
     * @param noteId
     * @param isCreateIndex
     */
    public void updateNoteIsCreateIndex(int noteId, boolean isCreateIndex) {
        executeUpdate("update t_note set is_create_index=? where id=?", isCreateIndex, noteId);
    }

    /**
     * @param id
     * @param isCreateIndex
     */
    public void updateFileIsCreateIndex(int id, boolean isCreateIndex) {
        executeUpdate("update t_file set is_create_index=? where id=?", isCreateIndex, id);
    }

    /**
     * @param token
     * @return
     */
    public ScmToken getExistedScmTokenByToken(String token) {
        ScmToken bean = getDomain(ScmToken.class, QueryWhere.create().where("token", token));
        if (bean == null) {
            throw new AppException("TOKEN不存在或已过期");
        }
        return bean;
    }

    public ScmToken getScmTokenByCompanyIdType(int companyId, int type) {
        return getDomain(ScmToken.class, QueryWhere.create().
                where("company_id", companyId).where("type", type));
    }

    /**
     * @param uuid
     * @return
     */
    public ScmCommitLog getScmCommitLogByUuid(String uuid) {
        return getDomain(ScmCommitLog.class, QueryWhere.create().where("uuid", uuid));
    }

    /**
     * @param projectId
     * @return
     */
    public Integer getLastProjectIterationId(int projectId) {
        String sql = "select id from t_project_iteration where project_id=? and is_delete=false order by id desc limit 1";
        return queryForInteger(sql, projectId);
    }

    /**
     * 更新task开始天数
     */
    public void updateTaskStartDays() {
        executeUpdate("update t_task a inner join(\r\n" +
                "	select id,DATEDIFF(now(),start_date) as use_days from t_task "
                + "where is_finish=false and is_delete=false and start_date is not null and start_date<=now()\r\n" +
                ")b on a.id=b.id set a.start_days=b.use_days;");
    }

    /**
     * 更新task剩余天数
     */
    public void updateTaskEndDays() {
        executeUpdate("update t_task a inner join(\r\n" +
                "	select id,DATEDIFF(end_date,now()) as left_days from t_task "
                + "where is_finish=false and is_delete=false and end_date is not null and end_days>0\r\n" +
                ")b on a.id=b.id set a.end_days=b.left_days;");
    }

    /**
     * @param testPlanId
     * @param testCaseId
     * @return
     */
    public TestPlanTestCase getTestPlanTestCase(int testPlanId, int testCaseId) {
        return getDomain(TestPlanTestCase.class, QueryWhere.create().
                where("test_plan_id", testPlanId).
                where("test_case_id", testCaseId));
    }

    /**
     * @param attachmentId
     * @param taskId
     * @return
     */
    public AttachmentAssociated getAttachmentAssociatedByTaskIdAttachmentId(int taskId, int attachmentId) {
        return getDomain(AttachmentAssociated.class, QueryWhere.create().
                where("attachment_id", attachmentId).
                where("task_id", taskId));
    }

    /**
     * @param taskId
     * @param attachmentId
     * @return
     */
    public AttachmentAssociated getExistedByTaskIdAttachmentIdForUpdate(int taskId, int attachmentId) {
        return getDomain(AttachmentAssociated.class, QueryWhere.create().
                where("attachment_id", attachmentId).
                where("task_id", taskId).
                forUpdate());
    }


    /**
     * @param accountId
     * @param companyId
     * @return
     */
    public CompanyMember getCompanyMemberByAccountIdCompanyId(int accountId, int companyId) {
        return getDomain(CompanyMember.class, QueryWhere.create().
                where("account_id", accountId).
                where("company_id", companyId));
    }

    /**
     * @param accountId
     * @param companyId
     * @return
     */
    public CompanyMemberInfo getExistedCompanyMemberInfo(int accountId, int companyId) {
        CompanyMemberInfo bean = getDomain(CompanyMemberInfo.class, QueryWhere.create().
                where("account_id", accountId).
                where("company_id", companyId));
        if (bean == null) {
            throw new AppException("成员不存在");
        }
        return bean;
    }

    /**
     * @param accountId
     * @param companyId
     * @return
     */
    public CompanyMember getExistedCompanyMemberForUpdate(int accountId, int companyId) {
        CompanyMember bean = getDomain(CompanyMember.class, QueryWhere.create().
                where("account_id", accountId).
                where("company_id", companyId).
                forUpdate());
        if (bean == null) {
            throw new AppException("成员不存在");
        }
        return bean;
    }

    /**
     * @param accountId
     * @param companyId
     */
    public void deleteCompanyMemberByAccountIdCompanyId(int accountId, int companyId) {
        delete(CompanyMember.class, QueryWhere.create().
                where("account_id", accountId).
                where("company_id", companyId));
    }

    /**
     * @param accountId
     * @return
     */
    public int getCompanyIdByAccountId(int accountId) {
        Integer companyId = queryForInteger("select company_id from t_company_member a "
                + "inner join t_company i1 on a.company_id=i1.id "
                + "where a.account_id=? and i1.is_delete=false "
                + "order by a.id desc limit 1 ", accountId);
        if (companyId == null) {
            companyId = 0;
        }
        return companyId;
    }

    /**
     * @param accountId
     * @param companyId
     */
    public void deleteProjectMembersByAccountIdCompanyId(int accountId, int companyId) {
        delete(ProjectMember.class, QueryWhere.create().
                where("account_id", accountId).
                where("company_id", companyId));
    }

    /**
     * @param accountId
     * @param companyId
     */
    public void deleteProjectMemberRolesByAccountIdCompanyId(int accountId, int companyId) {
        delete(ProjectMemberRole.class, QueryWhere.create().
                where("account_id", accountId).
                where("company_id", companyId));
    }

    /**
     * @param companyId
     * @param projectId
     * @param accountId
     * @param email
     * @return
     */
    public CompanyMemberInvite getCompanyMemberInvite(int companyId, int projectId, int accountId, String email) {
        return getDomain(CompanyMemberInvite.class, QueryWhere.create().
                where("company_id", companyId).
                where("project_id", projectId).
                where("account_id", accountId).
                where("invited_email", email).
                where("is_agree", false)
        );
    }

    /**
     * 查询我的进行中项目ID列表
     *
     * @param accountId
     * @return
     */
    public List<Integer> getRunningProjectIdList(int accountId) {
        String sql = "select a.id from t_project a \n" +
                "inner join t_project_member i1 on i1.`project_id`=a.id \n" +
                "where a.`status`=" + Project.STATUS_运行中 + " and i1.`account_id`=? and a.is_delete=false;";
        return queryForIntegers(sql, accountId);
    }

    /**
     * 查询全部进行中项目ID列表
     */
    public List<Integer> getAllRunningProjectIdList(int companyId) {
        String sql = "select a.id from t_project a \n" +
                "where a.`status`=" + Project.STATUS_运行中 + " and a.is_delete=false and a.company_id=?;";
        return queryForIntegers(sql, companyId);
    }


    /**
     * @param projectId
     * @param objectType
     * @param permissionId
     * @return
     */
    public ProjectDataPermission getProjectDataPermission(int projectId, int objectType, String permissionId) {
        return getDomain(ProjectDataPermission.class, QueryWhere.create().
                where("project_id", projectId).
                where("object_type", objectType).
                where("permission_id", permissionId)
        );
    }

    /**
     * 查询一个账号在一个企业里的所有角色id列表
     *
     * @param accountId
     * @param companyId
     * @return
     */
    public List<Integer> getCompanyMemberRoleIdList(int accountId, int companyId) {
        String sql = "select a.role_id from t_company_member_role a \r\n" +
                "inner join t_company_member i0 on i0.`account_id`=a.`account_id`\r\n" +
                "where i0.company_id=? and a.account_id=?;";
        return queryForIntegers(sql, companyId, accountId);
    }

    /**
     * @param categoryCode
     * @return
     */
    public List<DataDict> getDataDictListByCategoryCode(String categoryCode) {
        return getList(DataDict.class, QueryWhere.create().where("category_code", categoryCode));
    }

    /**
     * @param accountId
     * @param roleId
     * @return
     */
    public CompanyMemberRole getAccountRoleByAccountIdRoleId(int accountId, Integer roleId) {
        return getDomain(CompanyMemberRole.class, QueryWhere.create().
                where("account_id", accountId).
                where("role_id", roleId)
        );
    }

    /**
     * @param accountId
     * @param companyId
     */
    public void deleteAccountRolesByAccountIdCompanyId(int accountId, int companyId) {
        delete(CompanyMemberRole.class, QueryWhere.create().
                where("account_id", accountId).
                where("company_id", companyId));
    }

    /**
     * @param accountId
     * @param companyId
     */
    public void deleteDepartmentByAccountIdCompanyId(int accountId, int companyId) {
        delete(Department.class, QueryWhere.create().
                where("account_id", accountId).
                where("company_id", companyId).
                where("type", Department.TYPE_人员)
        );
    }

    /**
     * @param id
     * @param companyId
     * @return
     */
    public CmdbMachineInfo getExistedCmdbMachineInfo(int id, int companyId) {
        CmdbMachineInfo bean = getDomain(CmdbMachineInfo.class, QueryWhere.create().
                where("id", id).
                where("company_id", companyId)
        );
        if (bean == null) {
            throw new AppException("主机不存在");
        }
        return bean;
    }

    /**
     * @param companyId
     * @param name
     * @return
     */
    public CmdbMachineInfo getExistedCmdbMachineInfo(int companyId, String name) {
        CmdbMachineInfo bean = getDomain(CmdbMachineInfo.class, QueryWhere.create().
                where("name", name).
                where("company_id", companyId)
        );
        if (bean == null) {
            throw new AppException("主机不存在");
        }
        return bean;
    }

    /**
     * @param id
     * @param companyId
     * @return
     */
    public CmdbApplicationInfo getExistedCmdbApplicationInfo(int id, int companyId) {
        CmdbApplicationInfo bean = getDomain(CmdbApplicationInfo.class, QueryWhere.create().
                where("id", id).
                where("company_id", companyId)
        );
        if (bean == null) {
            throw new AppException("Application不存在");
        }
        return bean;
    }

    /**
     * @param companyId
     * @param name
     * @return
     */
    public CmdbApplicationInfo getExistedCmdbApplicationInfo(int companyId, String name) {
        CmdbApplicationInfo bean = getDomain(CmdbApplicationInfo.class, QueryWhere.create().
                where("name", name).
                where("company_id", companyId)
        );
        if (bean == null) {
            throw new AppException("Application不存在");
        }
        return bean;
    }

    //
    public CmdbInstanceInfo getExistedCmdbInstanceInfo(int id, int companyId) {
        CmdbInstanceInfo bean = getDomain(CmdbInstanceInfo.class, QueryWhere.create().
                where("id", id).
                where("company_id", companyId)
        );
        if (bean == null) {
            throw new AppException("Instance不存在");
        }
        return bean;
    }

    /**
     * @param projectId
     * @return
     */
    public List<ProjectDataPermission> getAllProjectDataPermission(int projectId) {
        return queryList(ProjectDataPermission.class,
                "select * from t_project_data_permission where project_id=?", projectId);
    }

    /**
     * @param projectId
     * @return
     */
    public List<ProjectFieldDefine> getAllProjectFieldDefine(int projectId) {
        return queryList(ProjectFieldDefine.class,
                "select * from t_project_field_define where project_id=?", projectId);
    }

    /**
     * @param projectId
     * @return
     */
    public List<ProjectFieldDefine> getProjectFieldDefineList(int projectId, int objectType) {
        return queryList(ProjectFieldDefine.class,
                "select * from t_project_field_define where project_id=? and object_type=?",
                projectId, objectType);
    }

    /**
     * @param projectId
     * @return
     */
    public List<ProjectIteration> getAllProjectIteration(int projectId) {
        return queryList(ProjectIteration.class,
                "select * from t_project_iteration where project_id=? and is_delete=false", projectId);
    }

    /**
     * @param projectId
     * @return
     */
    public List<ProjectIterationStep> getAllProjectIterationStep(int projectId) {
        return queryList(ProjectIterationStep.class,
                "select * from t_project_iteration_step where project_id=?", projectId);
    }

    /**
     * @param projectId
     * @return
     */
    public List<ProjectModule> getAllProjectModule(int projectId) {
        return queryList(ProjectModule.class,
                "select * from t_project_module where project_id=?", projectId);
    }

    /**
     * @param projectId
     * @return
     */
    public List<ProjectStatusDefine> getAllProjectStatusDefine(int projectId) {
        return queryList(ProjectStatusDefine.class,
                "select * from t_project_status_define where project_id=?", projectId);
    }

    /**
     * @param projectId
     * @return
     */
    public List<ProjectPriorityDefine> getAllProjectPriorityDefine(int projectId) {
        return queryList(ProjectPriorityDefine.class,
                "select * from t_project_priority_define where project_id=?", projectId);
    }

    /**
     * @param companyId
     * @param type
     * @param name
     * @return
     */
    public Role getRoleByCompanyIdTypeName(int companyId, int type, String name) {
        return getDomain(Role.class, QueryWhere.create().
                where("company_id", companyId).
                where("type", type).
                where("name", name)
        );
    }

    public Role getRoleByCompanyIdTypeNameForUpdate(int companyId, int type, String name) {
        return getDomain(Role.class, QueryWhere.create().
                where("company_id", companyId).
                where("type", type).
                where("name", name).forUpdate()
        );
    }

    /**
     * @return
     */
    public Set<String> getAllGlobalPermisions(int version) {
        return new LinkedHashSet<>(queryForStrings("select id from t_permission where type=? "
                        + "and (company_version=0 or company_version=?)",
                Permission.TYPE_全局, version));
    }

    public Set<String> getAllGlobalMemberPermisions(int version) {
        return new LinkedHashSet<>(queryForStrings("select id from t_permission where type=? "
                        + "and (company_version=0 or company_version=?) and is_member_permission=true ",
                Permission.TYPE_全局, version));
    }

    public Set<String> getAllProjectPermisions(int version) {
        return new LinkedHashSet<>(queryForStrings("select id from t_permission where type=? "
                        + "and (company_version=0 or company_version=?)",
                Permission.TYPE_项目, version));
    }

    public Set<String> getAllProjectMemberPermisions(int version) {
        return new LinkedHashSet<>(queryForStrings("select id from t_permission where type=? "
                + "and (company_version=0 or company_version=?) "
                + "and is_member_permission=true", Permission.TYPE_项目, version));
    }

    /**
     * @param objectType
     */
    public void deleteObjectTypeFieldDefinesByObjectType(int objectType) {
        delete(ObjectTypeFieldDefine.class, QueryWhere.create().where("object_type", objectType));
    }

    /**
     * @param projectId
     */
    public void deleteProjectModuleByProjectId(int projectId) {
        delete(ProjectModule.class, QueryWhere.create().where("project_id", projectId));
    }

    /**
     * 删除系统字段
     *
     * @param projectId
     */
    public void deleteProjectSystemFieldDefines(int projectId, int objectType) {
        delete(ProjectFieldDefine.class,
                QueryWhere.create().
                        where("project_id", projectId).
                        where("object_type", objectType).
                        where("is_system_field", true));
    }

    /**
     * @param projectMemberId
     * @param roleId
     * @return
     */
    public ProjectMemberRole getProjectMemberRole(int projectMemberId, int roleId) {
        return getDomain(ProjectMemberRole.class, QueryWhere.create().
                where("project_member_id", projectMemberId).
                where("role_id", roleId)
        );
    }

    /**
     * @param accountId
     * @param projectId
     * @param roleId
     * @return
     */
    public ProjectMemberRole getProjectMemberRole(int accountId, int projectId, int roleId) {
        return getDomain(ProjectMemberRole.class, QueryWhere.create().
                where("account_id", accountId).
                where("project_id", projectId).
                where("role_id", roleId)
        );
    }

    /**
     * @param projectId
     * @param roleId
     */
    public List<Integer> getAccountIdListByProjectIdRoleId(int projectId, int roleId) {
        String sql = "select account_id from t_project_member_role where project_id=? and role_id=?";
        return queryForIntegers(sql, projectId, roleId);
    }

    /**
     * @param state
     * @return
     */
    public WxOauth getWxOauthByStateForUpdate(String state) {
        return getDomain(WxOauth.class, QueryWhere.create().where("state", state).forUpdate());
    }

    /**
     * 删除关联关系
     *
     * @param taskId
     */
    public void deleteTaskAssociatedList(int taskId) {
        delete(TaskAssociated.class, QueryWhere.create().where("task_id", taskId));
        delete(TaskAssociated.class, QueryWhere.create().where("associated_task_id", taskId));
    }

    /**
     * @param taskId
     * @param associatedTaskId
     * @return
     */
    public TaskAssociated getTaskAssociated(int taskId, int associatedTaskId) {
        return getDomain(TaskAssociated.class,
                QueryWhere.create().where("task_id", taskId).
                        where("associated_task_id", associatedTaskId));
    }

    /**
     * @param accountId
     */
    public void updateAccountNotificationWebSend(int accountId) {
        executeUpdate("update t_account_notification set is_web_send=true where account_id=? and type!=?",
                accountId, AccountNotificationSetting.TYPE_日历提醒);
    }

    /**
     * @param projectId
     * @param name
     * @param version
     * @return
     */
    public List<ProjectArtifact> getProjectArtifactList(int projectId, String name, String version) {
        return getList(ProjectArtifact.class,
                QueryWhere.create().
                        where("is_delete", false).
                        where("project_id", projectId).
                        where("name", name).
                        where("version", version)
        );
    }

    public List<ProjectArtifact> getNeedDeleteFileProjectArtifactList(int projectId, String name, String version) {
        return getList(ProjectArtifact.class,
                QueryWhere.create().
                        where("is_file_delete", false).
                        where("project_id", projectId).
                        where("name", name).
                        where("version", version).
                        orderBy("id desc").
                        limit(10, 100)    //只保留10个
        );
    }

    /**
     * @param projectId
     * @param uuid
     * @return
     */
    public ProjectArtifact getProjectArtifact(int projectId, String uuid) {
        return getDomain(ProjectArtifact.class, QueryWhere.create().
                where("project_id", projectId).
                where("uuid", uuid).
                where("is_delete", false));
    }

    /**
     * @param apiKey
     * @return
     */
    public CmdbApi getExistedCmdbApi(String apiKey) {
        CmdbApi bean = getDomain(CmdbApi.class, QueryWhere.create().
                where("api_key", apiKey));
        if (bean == null) {
            throw new AppException("错误的apiKey");
        }
        return bean;
    }

    /**
     * @param name
     * @return
     */
    public CmdbInstanceInfo getCmdbInstanceByName(String name) {
        return getDomain(CmdbInstanceInfo.class, QueryWhere.create().
                where("name", name));
    }

    /**
     * @param name
     * @return
     */
    public ProjectArtifact getProjectArtifactByName(String name) {
        return getDomain(ProjectArtifact.class, QueryWhere.create().
                where("name", name).
                where("is_delete", false).
                orderBy("id desc"));
    }

    /**
     * @param name
     * @param version
     * @return
     */
    public ProjectArtifact getProjectArtifactByNameVersion(String name, String version) {
        return getDomain(ProjectArtifact.class, QueryWhere.create().
                where("name", name).
                where("version", version).
                where("is_delete", false).
                orderBy("id desc"));
    }

    /**
     * @param name
     * @return
     */
    public CmdbRobot getCmdbRobot(String name) {
        return getDomain(CmdbRobot.class, QueryWhere.create().
                where("name", name));
    }

    /**
     * @param categoryCode
     * @return
     */
    public List<DataDict> getDataDictList(String categoryCode) {
        return getList(DataDict.class, QueryWhere.create().where("category_code", categoryCode));
    }

    /**
     * @param calUuid
     * @return
     */
    public Account getExistedAccountByCalUuid(String calUuid) {
        Account account = getDomain(Account.class, QueryWhere.create().where("cal_uuid", calUuid));
        if (account == null) {
            throw new AppException("账号不存在");
        }
        return account;
    }

    /**
     * @param projectId
     * @param name
     * @return
     */
    public ProjectPipeline getProjectPipeline(int projectId, String name) {
        return getDomain(ProjectPipeline.class,
                QueryWhere.create().
                        where("project_id", projectId).
                        where("name", name));
    }

    /**
     * @param uuid
     * @return
     */
    public WikiPageDetailInfo getWikiPageDetailInfoByUuid(String uuid) {
        return getDomain(WikiPageDetailInfo.class,
                QueryWhere.create().
                        where("uuid", uuid));
    }

    /**
     * @param uuid
     * @return
     */
    public ProjectArtifactInfo getExistedProjectArtifactInfoByUuid(String uuid) {
        ProjectArtifactInfo bean = getDomain(ProjectArtifactInfo.class,
                QueryWhere.create().
                        where("uuid", uuid));
        if (bean == null) {
            throw new AppException("交付物不存在");
        }
        return bean;
    }

    /**
     * @param cmdbMachineId
     * @return
     */
    public List<MachineInfo> getMachineListByCmdbMachineId(int cmdbMachineId) {
        return getList(MachineInfo.class, QueryWhere.create().where("cmdb_machine_id", cmdbMachineId));
    }

    /**
     * @param uuid
     * @return
     */
    public File getFileByUuid(String uuid) {
        return getDomain(File.class,
                QueryWhere.create().
                        where("uuid", uuid));
    }

    public FileInfo getFileInfoByUuid(String uuid) {
        return getDomain(FileInfo.class,
                QueryWhere.create().
                        where("uuid", uuid));
    }

    /**
     * @param companyId
     * @param accountName
     * @return
     */
    public CompanyMemberInfo getCompanyMemberInfoByCompanyIdAccountName(int companyId, String accountName) {
        CompanyMemberQuery query = new CompanyMemberQuery();
        query.companyId = companyId;
        query.accountName = accountName;
        query.pageSize = 1;
        List<CompanyMemberInfo> list = getList(query);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public Integer getProjectMemberAccountIdByProjectIdAccountName(int projectId, String accountName) {
        return queryForInteger("select t.account_id from t_project_member t inner join t_account t1 on t.account_id=t1.id where t.project_id=? and t1.name=?", projectId, accountName);
    }

    public ProjectObjectTypeTemplateInfo getProjectObjectTypeTemplateByProjectIdObjectType(int projectId,
                                                                                           int objectType) {
        return getDomain(ProjectObjectTypeTemplateInfo.class,
                QueryWhere.create().
                        where("project_id", projectId).
                        where("object_type", objectType)
        );
    }

    public Kaptcha getExistedKaptchaBySign(String sign) {
        Kaptcha bean = getDomain(Kaptcha.class,
                QueryWhere.create().
                        where("sign", sign)
        );
        if (bean == null) {
            throw new AppException("验证码已过期");
        }
        return bean;
    }

    public List<ProjectPipeline> getNeedRunProjectPipelineInfos() {
        return queryList(ProjectPipeline.class, "select * from t_project_pipeline where "
                + "next_run_time is not null and next_run_time<? and is_delete=false", new Date());
    }

    public List<CmdbRobot> getNeedRunCmdbRobots() {
        return queryList(CmdbRobot.class, "select * from t_cmdb_robot where "
                + "next_run_time is not null and next_run_time<?", new Date());
    }

    public List<SystemHook> getNeedRunSystemHooks() {
        return queryList(SystemHook.class, "select * from t_system_hook where "
                + "next_run_time is not null and next_run_time<? and is_delete=false", new Date());
    }

    public CompanyMemberInvite getCompanyMemberInviteByUuidForUpdate(String uuid) {
        return getDomain(CompanyMemberInvite.class,
                QueryWhere.create().
                        where("uuid", uuid).
                        forUpdate()
        );
    }

    public Department getDepartmentByCompanyIdParentIdTypeAccountId(int companyId, int parentId, int type, int accountId) {
        return getDomain(Department.class,
                QueryWhere.create().
                        where("company_id", companyId).
                        where("parent_id", parentId).
                        where("type", type).
                        where("account_id", accountId)
        );
    }

    public void deleteTasksByProjectId(int projectId) {
        executeUpdate("update t_task set is_delete=true,is_create_index=false where project_id=? and is_delete=false", projectId);
    }

    public void deleteWikiPagesByProjectId(int projectId) {
        executeUpdate("update t_wiki_page set is_delete=true,is_create_index=false where project_id=? and is_delete=false", projectId);
    }

    public void deleteFilesByProjectId(int projectId) {
        executeUpdate("update t_file set is_delete=true,is_create_index=false where project_id=? and is_delete=false", projectId);
    }

    public void deleteWikisByProjectId(int projectId) {
        executeUpdate("update t_wiki set is_delete=true where project_id=? and is_delete=false", projectId);
    }

    public AccountNotificationSetting getAccountNotificationSetting(int accountId, int type) {
        return getDomain(AccountNotificationSetting.class,
                QueryWhere.create().
                        where("type", type).
                        where("account_id", accountId)
        );
    }

    /**
     * @param account
     * @return
     */
    public Set<Integer> getAllCompanyRoles(Account account) {
        return new LinkedHashSet<>(queryForIntegers("select role_id "
                        + "from t_company_member_role where company_id=? and account_id=?",
                account.companyId, account.id));
    }

    /**
     * @param account
     * @return
     */
    public Set<Integer> getAllProjectRoles(Account account, int projectId) {
        return new LinkedHashSet<>(queryForIntegers("select role_id "
                        + "from t_project_member_role where project_id=? and account_id=?",
                projectId, account.id));
    }

    /**
     * 查询最新的记录
     *
     * @param id
     * @return
     */
    public DesignerDatabaseChangeLog getLastDesignerDatabaseChangeLog(int id) {
        return queryObject(DesignerDatabaseChangeLog.class,
                "select * from t_designer_database_change_log "
                        + "where designer_database_id=? "
                        + "order by create_time desc limit 1", id);
    }

    /**
     * @param taskId
     * @return
     */
    public TaskRemindInfo getTaskRemindByTaskId(int taskId) {
        return getDomain(TaskRemindInfo.class, QueryWhere.create().where("task_id", taskId));
    }

    public TaskRemindInfo getTaskRemindByTaskIdForUpdate(int taskId) {
        return getDomain(TaskRemindInfo.class, QueryWhere.create().where("task_id", taskId).forUpdate());
    }

    /**
     * @return
     */
    public List<TaskRemindTime> getNeedRunTaskRemindTimes() {
        return queryList(TaskRemindTime.class, "select * from t_task_remind_time where "
                + "remind_time <? ", new Date());
    }

    /**
     * @return
     */
    public List<ReportTemplate> getNeedRunReportTemplates() {
        return queryList(ReportTemplate.class, "select * from t_report_template where status=? and "
                + "next_remind_time <? ", ReportTemplate.STATUS_有效, new Date());
    }

    public int getMyReportSubmitNum(int accountId) {
        String sql = "select count(1) from t_report where submitter_id=? and status=?";
        return queryCount(sql, accountId, Report.STATUS_待汇报);
    }

    public int getMyReportAuditNum(int accountId) {
        String sql = "select count(1) from t_report where json_contains(auditor_ids,'" + accountId + "') and status=?";
        return queryCount(sql, Report.STATUS_待审核);
    }

    public Integer getMaxYDashboardCard(int accountId, int dashboardId) {
        String sql = "select max(y) from t_dashboard_card where create_account_id=? and dashboard_id=?";
        return queryForInteger(sql, accountId, dashboardId);
    }

    public ProjectIteration getLastProjectIteration(int projectId) {
        return getDomain(ProjectIteration.class, QueryWhere.create().
                where("project_id", projectId).
                where("is_delete", false).
                where("status", "!=", ProjectIteration.STATUS_已完成).
                orderBy("id desc").limit(1));
    }

    /**
     * 我所在的项目
     *
     * @param accountId
     * @return
     */
    public List<Integer> getProjectIdList(int accountId) {
        return queryForIntegers("select a.id from t_project a "
                + "inner join t_project_member b on a.id=b.project_id "
                + "where a.is_delete=false and b.account_id=?;", accountId);
    }

    /**
     * 找出除模板外的项目列表
     *
     * @return
     */
    public List<Integer> getCompanyProjectIdList() {
        return queryForIntegers("select id from t_project "
                + "where is_delete=false and company_id>0;");
    }

    public List<Integer> getProjectMemberIdList(int companyId, int accountId) {
        return queryForIntegers("select a.id from t_project_member a "
                + "where a.company_id=? and a.account_id=?;", companyId, accountId);
    }

    public List<Integer> getProjectMemberIdList(int companyId, int accountId, List<Integer> projectIds) {
        return queryForIntegers("select a.id from t_project_member a "
                + "where a.company_id=? and a.account_id=? and find_in_set(a.project_id ,?);", companyId, accountId, Joiner.on(",").join(projectIds));
    }

    /**
     * 获取消息数量
     *
     * @param discussId
     * @return
     */
    public int getReplyCount(int discussId) {
        return queryForInteger("select count(1) from t_discuss_message where discuss_id=?", discussId);
    }

    /**
     * @return
     */
    public List<String> getAccountNames() {
        return queryForStrings("select name from t_account");
    }

    /**
     * @param parentId
     * @return
     */
    public List<Integer> getChildrenByParentId(int parentId) {
        String sql = "select id\n" +
                "from    (select * from t_category\r\n" +
                "         order by parent_id, id) products_sorted,\r\n" +
                "        (select @pv := ?) initialisation\r\n" +
                "where   find_in_set(parent_id, @pv)\r\n" +
                "and     length(@pv := concat(@pv, ',', id))";
        return queryForIntegers(sql, parentId);
    }

    /**
     * @param projectId
     * @param name
     * @return
     */
    public ProjectIteration getProjectIterationByProjectIdName(int projectId, String name) {
        return getDomain(ProjectIteration.class, QueryWhere.create().
                where("project_id", projectId).
                where("name", name));
    }

    /**
     * @param projectId
     * @param name
     * @return
     */
    public ProjectRelease getProjectReleaseByProjectIdName(int projectId, String name) {
        return getDomain(ProjectRelease.class, QueryWhere.create().
                where("project_id", projectId).
                where("name", name));
    }

    /**
     * @param projectId
     * @param name
     * @return
     */
    public ProjectSubSystem getProjectSubSystemByProjectIdName(int projectId, String name) {
        return getDomain(ProjectSubSystem.class, QueryWhere.create().
                where("project_id", projectId).
                where("name", name));
    }

    public int calcSubTaskCount(int parentId) {
        String sql = "select count(1) from t_task where parent_id=? and is_delete=false";
        return queryForInteger(sql, parentId);
    }

    /**
     * @param parentId
     * @return
     */
    public int calcFinishSubTaskCount(int parentId) {
        String sql = "select count(1) from t_task where parent_id=? and is_finish=true and is_delete=false";
        return queryForInteger(sql, parentId);
    }


    /**
     * 计算总工时
     *
     * @param taskId
     * @return
     */
    public double calcWorkTime(int taskId) {
        String sql = "select sum(hour) from t_task_work_time_log where task_id=?";
        Double value = queryForDouble(sql, taskId);
        if (value == null) {
            value = 0d;
        }
        return value;
    }

    /**
     * @param taskId
     * @param associatedTaskId
     */
    public void deleteTaskAssociated(int taskId, int associatedTaskId) {
        delete(TaskAssociated.class, QueryWhere.create().
                where("task_id", taskId).
                where("associated_task_id", associatedTaskId));
    }

    /**
     * @param taskId
     * @param status
     * @return
     */
    public TaskStatusChangeLog getTaskStatusChangeLog(int taskId, int status) {
        return getDomain(TaskStatusChangeLog.class,
                QueryWhere.create().
                        where("task_id", taskId).
                        where("status", status).
                        whereSql("leave_time is null")
        );
    }

    /**
     * @param companyId
     * @param accountId
     * @param startTime
     * @return
     */
    public int getTaskWorkHours(int companyId, int accountId, Date startTime, Date endTime) {
        String sql = "select sum(a.hour) from t_task_work_time_log a\n" +
                "inner join t_task i1 on a.task_id=i1.id\n" +
                "where a.company_id=? and a.create_account_id=? and a.start_time>=? and a.start_time<? and i1.is_delete=false";
        Integer hours = queryForInteger(sql, companyId, accountId, startTime, endTime);
        if (hours == null) {
            hours = 0;
        }
        return hours;
    }

    /**
     * @param sql
     * @param parameters
     * @return
     */
    public List<Map<String, Object>> querListMap(
            String sql,
            Object... parameters) {
        Connection conn = connectionDriver.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            if (limitMaxRows > 0) {
                ps.setMaxRows(limitMaxRows);
            }
            try {
                ConnectionUtil.set(ps, parameters);
            } catch (SQLException e) {
                logger.error("ConnectionUtil.set failed.\nsql:{} \nparameters:{}", sql, DumpUtil.dump(parameters));
                throw new AppException();
            }
            rs = ps.executeQuery();
            List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                int columnCount = rsmd.getColumnCount();
                for (int i = 0; i < columnCount; i++) {
                    String columnLabel = rsmd.getColumnLabel(i + 1);
                    map.put(columnLabel, rs.getObject(i + 1));
                }
                result.add(map);
            }
            return result;
        } catch (Exception e) {
            throw new ConnectionException(e);
        } finally {
            ConnectionUtil.closeResultSet(rs);
            ConnectionUtil.closeStatement(ps);
            ConnectionUtil.closeConnection(conn);
        }
    }

    public Map<String, Object> queryMap(
            String sql,
            Object... parameters) {
        Connection conn = connectionDriver.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            if (limitMaxRows > 0) {
                ps.setMaxRows(limitMaxRows);
            }
            try {
                ConnectionUtil.set(ps, parameters);
            } catch (SQLException e) {
                logger.error("ConnectionUtil.set failed.\nsql:{} \nparameters:{}", sql, DumpUtil.dump(parameters));
                throw new AppException();
            }
            rs = ps.executeQuery();
            Map<String, Object> result = null;
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                int columnCount = rsmd.getColumnCount();
                for (int i = 0; i < columnCount; i++) {
                    String columnLabel = rsmd.getColumnLabel(i + 1);
                    map.put(columnLabel, rs.getObject(i + 1));
                }
                result = map;
                break;
            }
            return result;
        } catch (Exception e) {
            throw new ConnectionException(e);
        } finally {
            ConnectionUtil.closeResultSet(rs);
            ConnectionUtil.closeStatement(ps);
            ConnectionUtil.closeConnection(conn);
        }
    }

    public WikiPageChangeLogDetailInfo getLastWikiPageChangeLog(int wikiPageId) {
        return getDomain(WikiPageChangeLogDetailInfo.class,
                QueryWhere.create().where("wiki_page_id", wikiPageId).orderBy("id desc").limit(1));
    }

    public List<ProjectModule> getProjectModuleListByProjectId(int projectId) {
        return getAll(ProjectModule.class, QueryWhere.create().where("project_id", projectId));
    }

    public boolean existedAccessToken(String accessToken) {
        return queryCount("select count(1) from t_account where access_token=?", accessToken) >= 1;
    }

    public Account getAccountByAccessToken(String accessToken) {
        return getDomain(Account.class, QueryWhere.create().where("access_token", accessToken));
    }

    /**
     * 判断路径是否重复
     *
     * @param file
     * @return
     */
    public boolean existedFilePath(File file) {
        return queryCount("select count(1) from t_file where "
                        + "id!=? and project_id=? and is_delete=? and path=?",
                file.id, file.projectId, false, file.path) >= 1;
    }

    /**
     * @return
     */
    public List<ProjectActivity> getProjectActivityList() {
        Date date = DateUtil.getBeginOfDay(DateUtil.getNextDay(-15));
        String sql = "select project_id,DATE_FORMAT(create_time, '%Y-%m-%d') as riqi,"
                + "count(1) as num "
                + "from t_change_log "
                + "where project_id>0 and create_time>=? "
                + "group by project_id,riqi order by project_id asc,riqi asc;";
        return queryList(ProjectActivity.class, sql, date);

    }

    /**
     * @param projectId
     * @param count
     */
    public void updateProjectActivityNums(int projectId, String count) {
        executeUpdate("update t_project set activity_count=? where id=?", count, projectId);
    }

    /**
     * 重置每日登陆失败次数 验证码错误次数
     */
    public void doResetDailyLoginFailCountKaptchaErrorCount() {
        executeUpdate("update t_account set daily_login_fail_count=0,kaptcha_error_count=0 "
                + "where daily_login_fail_count>0 or kaptcha_error_count>0");
    }

    /**
     * @param companyId
     * @return
     */
    public Task getCompanyTaskMinCreateTime(int companyId) {
        String sql = "select * from t_task where company_id=? and is_delete=false order by id asc limit 1";
        return queryObject(Task.class, sql, companyId);
    }

    /**
     * @param taskId
     */
    public void deleteTaskOwnersByTaskId(int taskId) {
        delete(TaskOwner.class, QueryWhere.create().where("task_id", taskId));
    }

    /**
     * @param taskId
     * @param accountId
     * @return
     */
    public TaskOwner getTaskOwner(int taskId, Integer accountId) {
        return getDomain(TaskOwner.class, QueryWhere.create().
                where("task_id", taskId).
                where("account_id", accountId));
    }

    /**
     * 查询超期任务数量(已过滤归档项目)
     *
     * @return
     */
    public List<AccountOverDueTask> getAccountOverDueTask() {
        Date dueDate = DateUtil.getBeginOfDay(new Date());
        Date lastLoginTime = DateUtil.getNextDay(-7);//7天前登陆过的才通知
        String sql = "select i1.id as account_id,i1.company_id,i1.name as account_name,count(1) as num from t_task a \r\n" +
                "inner join t_task_owner i0 on a.id=i0.task_id\r\n" +
                "inner join t_account i1 on i0.account_id=i1.id\r\n" +
                "inner join t_project_module i2 on i2.object_type=a.object_type and i2.project_id=a.project_id\r\n" +
                "inner join t_project i3 on i3.id=a.project_id\r\n" +
                "inner join t_company i4 on i4.id=a.company_id\r\n" +
                "where a.is_delete=false and a.is_finish=false "
                + "and i2.is_status_based=true and a.end_date is not null and a.object_type !=2001 "
                + "and a.end_date<? and i3.status=1 and i3.is_delete=false and i4.is_delete=false and i1.last_login_time>=?\r\n" + //20200205添加
                "group by i1.id,i1.company_id\r\n" +
                "order by num desc;";
        return queryList(AccountOverDueTask.class, sql, dueDate, lastLoginTime);
    }

    /**
     * 查询即将到期任务
     *
     * @return
     */
    public List<AccountOverDueTask> getAccountAboutToExpireTask(int delta) {
        Date endDate = DateUtil.getBeginOfDay(new Date());
        Date startDate = DateUtil.getNextDay(endDate, -delta);
        Date lastLoginTime = DateUtil.getNextDay(-7);//7天前登陆过的才通知
        String sql = "select i1.id as account_id,i1.company_id,i1.name as account_name,count(1) as num from t_task a \r\n" +
                "inner join t_task_owner i0 on a.id=i0.task_id\r\n" +
                "inner join t_account i1 on i0.account_id=i1.id\r\n" +
                "inner join t_project_module i2 on i2.object_type=a.object_type and i2.project_id=a.project_id\r\n" +
                "inner join t_project i3 on i3.id=a.project_id\r\n" +
                "inner join t_company i4 on i4.id=a.company_id\r\n" +
                "where a.is_delete=false and a.is_finish=false "
                + "and i2.is_status_based=true and a.end_date is not null "
                + "and a.end_date between ? and ? and i3.status=1 and i3.is_delete=false and i4.is_delete=false and i1.last_login_time>=?\r\n" + //20200205添加
                "group by i1.id,i1.company_id\r\n" +
                "order by num desc;";
        return queryList(AccountOverDueTask.class, sql, startDate, endDate, lastLoginTime);
    }

    public List<AccountProjectOverDueTask> getAccountProjectOverDueTask(int accountId) {
        Date dueDate = DateUtil.getBeginOfDay(new Date());
        String sql = "select i1.id as account_id,i1.company_id,i3.id as project_id,i3.name as project_name,i1.name as account_name,count(1) as num from t_task a \r\n" +
                "inner join t_task_owner i0 on a.id=i0.task_id\r\n" +
                "inner join t_account i1 on i0.account_id=i1.id\r\n" +
                "inner join t_project_module i2 on i2.object_type=a.object_type and i2.project_id=a.project_id\r\n" +
                "inner join t_project i3 on i3.id=a.project_id\r\n" +
                "inner join t_company i4 on i4.id=a.company_id\r\n" +
                "where i1.id=? and a.is_delete=false and a.is_finish=false and i2.is_status_based=true "
                + "and a.end_date is not null and a.end_date<? and i3.status=1 "
                + "and i3.is_delete=false and i4.is_delete=false\r\n" +
                "group by i1.id,i1.company_id,a.project_id\r\n" +
                "order by num desc;";
        return queryList(AccountProjectOverDueTask.class, sql, accountId, dueDate);
    }

    /**
     * 责任人分布图
     *
     * @return
     */
    public List<AccountTaskNum> getAccountTaskNum(Account account, TaskQuery query, Boolean isStatusBased) {
        StringBuilder sql = new StringBuilder();
        List<Object> values = new ArrayList<>();
        sql.append("select i1.id as account_id,i1.name as account_name,count(1) as num,i1.company_id from t_task a \r\n" +
                "inner join t_task_owner i0 on a.id=i0.task_id\r\n" +
                "inner join t_account i1 on i0.account_id=i1.id\r\n" +
                "inner join t_project_module i2 on i2.object_type=a.object_type and i2.project_id=a.project_id\r\n" +
                "inner join t_project i3 on i3.id=a.project_id\r\n" +
                "where 1=1 \r\n");
        if (query.isDelete != null) {
            sql.append(" and a.is_delete=" + query.isDelete + "\r\n");
        }
        if (isStatusBased != null) {
            sql.append(" and i2.is_status_based=true\r\n");
        }
        if (query.isFinish != null) {
            sql.append(" and a.is_finish=" + query.isFinish + "\r\n");
        }
        if (query.projectStatus != null) {
            sql.append(" and i3.status=" + query.projectStatus + "\r\n");
        }
        if (query.projectId != null) {
            sql.append(" and i3.id=" + query.projectId + "\r\n");
        }
        if (query.objectType != null) {
            sql.append(" and a.object_type=" + query.objectType + "\r\n");
        }
        if (query.iterationId != null) {
            sql.append(" and a.iteration_id=" + query.iterationId + "\r\n");
        }
        if (query.filterId != null) {
            sql.append(SqlUtils.addFilterSql(this, account, query, values));
        }
        sql.append("group by i1.id\r\n");
        sql.append("order by num desc\r\n");
        return queryList(AccountTaskNum.class, sql.toString(), values.toArray());
    }

    /**
     * 公司成员数量(排除root)
     *
     * @param companyId
     * @return
     */
    public int getCurrMemberNum(int companyId) {
        String sql = "select count(1) from t_company_member where company_id=? and id!=1";
        return queryCount(sql, companyId);
    }

    /**
     * @param apiKey
     * @return
     */
    public WebApi getWebApiByApiKey(String apiKey) {
        return getDomain(WebApi.class, QueryWhere.create().where("api_key", apiKey));
    }

    /**
     * @param functionName
     * @return
     */
    public List<SystemHook> getSystemHooksByFunctionName(int companyId, String functionName) {
        String sql = "select * from t_system_hook a where a.company_id=? and a.is_delete=false and a.status=? and a.function_names like '%\"" + functionName + "\"%';";
        return queryList(SystemHook.class, sql, companyId, SystemHook.STATUS_有效);
    }

    /**
     * @param workflowDefineId
     * @return
     */
    public int getMaxVersionWorkflowChartDefineId(int workflowDefineId) {
        String sql = "select max(version) from t_workflow_chart_define where workflow_define_id=?;";
        return queryForInteger(sql, workflowDefineId);
    }

    /**
     * @param chartDefineId
     * @return
     */
    public WorkflowDefine getWorkflowDefineByWorkflowChartDefineId(int chartDefineId) {
        return getDomain(WorkflowDefine.class,
                QueryWhere.create().where("chart_define_id", chartDefineId));
    }

    /**
     * @param accountId
     * @param workflowInstanceId
     * @param nodeId
     * @return
     */
    public WorkflowInstanceOwner getWorkflowInstanceOwner(int accountId, int workflowInstanceId, String nodeId) {
        return getDomain(WorkflowInstanceOwner.class,
                QueryWhere.create().
                        where("owner_account_id", accountId).
                        where("workflow_instance_id", workflowInstanceId).
                        where("node_id", nodeId).
                        whereSql("leave_time is null"));
    }

    /**
     * @param accountId
     * @param workflowInstanceId
     * @param nodeId
     * @return
     */
    public WorkflowInstanceOwner getWorkflowInstanceOwnerForUpdate(int accountId, int workflowInstanceId, String nodeId) {
        return getDomain(WorkflowInstanceOwner.class,
                QueryWhere.create().
                        where("owner_account_id", accountId).
                        where("workflow_instance_id", workflowInstanceId).
                        where("node_id", nodeId).
                        whereSql("leave_time is null").
                        forUpdate());
    }

    /**
     * @param workflowInstanceId
     * @param nodeId
     * @return
     */
    public WorkflowInstanceNodeChange getWorkflowInstanceNodeChangeForUpdate(int workflowInstanceId, String nodeId) {
        return getDomain(WorkflowInstanceNodeChange.class,
                QueryWhere.create().where("workflow_instance_id", workflowInstanceId).
                        where("node_id", nodeId).
                        whereSql("leave_time is null").forUpdate());
    }

    /**
     * @param workflowInstanceId
     */
    public void setWorkflowInstanceOwnerStatusOtherDone(int workflowInstanceId) {
        executeUpdate("update t_workflow_instance_owner set status=?,leave_time=now() where workflow_instance_id=? and status=?",
                WorkflowInstanceOwner.STATUS_他人已处理, workflowInstanceId, WorkflowInstanceOwner.STATUS_待处理);
    }

    /**
     * @param departmentId
     * @return
     */
    public List<Integer> getDepartmentOwnerAccountIdList(int departmentId) {
        return queryForIntegers("select owner_account_id from t_department_owner where department_id=?", departmentId);
    }

    /**
     * @param workflowDefineId
     * @return
     */
    public WorkflowFormDefineInfo getWorkflowFormDefineInfoByWorkflowDefineId(int workflowDefineId) {
        return getDomain(WorkflowFormDefineInfo.class, QueryWhere.create().where("workflow_define_id", workflowDefineId));
    }

    /**
     * @param workflowDefineId
     * @return
     */
    public WorkflowDefinePermissionInfo getWorkflowDefinePermissionByWorkflowDefineId(int workflowDefineId) {
        return getDomain(WorkflowDefinePermissionInfo.class, QueryWhere.create().where("workflow_define_id", workflowDefineId));
    }

    /**
     * 查询部门下的成员列表
     *
     * @param departmentIds
     * @return
     */
    public List<Integer> getAccountIdsByDepartmentIds(int companyId, Set<Integer> departmentIds) {
        if (departmentIds.isEmpty()) {
            return new ArrayList<>();
        }
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct(account_id) from t_department \n");
        sql.append("where company_id=? and type=" + Department.TYPE_人员 + "\n");
        sql.append("and parent_id in(\n");
        for (Integer departmentId : departmentIds) {
            sql.append(departmentId).append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")\n");
        return queryForIntegers(sql.toString(), companyId);
    }

    /**
     * 找出部门负责人列表
     *
     * @param departmentIds
     * @return
     */
    public List<Integer> getOwnerAccountIdsByDepartmentIds(int companyId, Set<Integer> departmentIds) {
        if (departmentIds.isEmpty()) {
            return new ArrayList<>();
        }
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct(owner_account_id) from t_department_owner \n");
        sql.append("where company_id=? and department_id in(\n");
        for (Integer departmentId : departmentIds) {
            sql.append(departmentId).append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")\n");
        return queryForIntegers(sql.toString(), companyId);
    }

    /**
     * 根据企业角色查询用户列表
     *
     * @param companyRoleSet
     * @return
     */
    public List<Integer> getAccountIdsByCompanyRoles(int companyId, Set<Integer> companyRoleSet) {
        if (companyRoleSet.isEmpty()) {
            return new ArrayList<>();
        }
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct(account_id) from t_company_member_role \n");
        sql.append("where company_id=? and role_id in(\n");
        for (Integer roleId : companyRoleSet) {
            sql.append(roleId).append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")\n");
        return queryForIntegers(sql.toString(), companyId);
    }

    /**
     * 根据项目角色查询用户列表
     *
     * @param companyId
     * @param projectRoleSet
     * @return
     */
    public List<Integer> getAccountIdsByProjectRoles(int companyId, Set<String> projectRoleSet) {
        if (projectRoleSet.isEmpty()) {
            return new ArrayList<>();
        }
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct(account_id) from t_project_member_role \n");
        sql.append("where company_id=?\n");
        sql.append("and (\n");
        int index = 0;
        for (String projectRoleId : projectRoleSet) {
            List<Integer> idList = BizUtil.splitInteger(projectRoleId, "-");
            int projectId = idList.get(0);
            int roleId = idList.get(1);
            sql.append("(project_id=" + projectId + " and role_id=" + roleId + ")");
            if (index != projectRoleSet.size() - 1) {
                sql.append(" or ");
            }
            index++;
        }
        sql.append(")\n");
        return queryForIntegers(sql.toString(), companyId);
    }

    /**
     * @param workflowInstanceId
     * @param nodeId
     */
    public void deleteWorkflowInstanceOwner(int workflowInstanceId, String nodeId) {
        delete(WorkflowInstanceOwner.class, QueryWhere.create().
                where("workflow_instance_id", workflowInstanceId).
                where("type", WorkflowInstanceOwner.TYPE_责任人).
                where("node_id", nodeId));
    }

    /**
     * @param workflowInstanceId
     * @param nodeId
     */
    public void deleteWorkflowInstanceCc(int workflowInstanceId, String nodeId) {
        delete(WorkflowInstanceOwner.class, QueryWhere.create().
                where("workflow_instance_id", workflowInstanceId).
                where("type", WorkflowInstanceOwner.TYPE_抄送人).
                where("node_id", nodeId));
    }

    /**
     * @param noteId
     * @return
     */
    public String getNoteContentByNoteId(int noteId) {
        return queryForString("select content from t_note_content where note_id=?", noteId);
    }

    /**
     * @param nodeId
     * @return
     */
    public void updateNoteContentByNoteId(int nodeId, String content) {
        executeUpdate("update t_note_content set content=? where note_id=?", content, nodeId);
    }

    /**
     * 日历任务列表
     *
     * @param account
     * @param month
     * @param objectTypeInList
     * @return
     */
    public List<TaskInfo> getMyCalendarTasks(Account account, Date month, int[] objectTypeInList) {
        StringBuilder sql = new StringBuilder();
        Date beginMonth = DateUtil.getBeginOfMonth(month);
        Date beginNextMonth = DateUtil.getNextMonth(beginMonth, 1);
        sql.append("select a.id,a.name,a.uuid,a.create_time,a.object_type,a.serial_no,a.status,"
                + "i2.name as status_name,a.start_date,"
                + "a.end_date,i1.name as object_type_name,i3.id as project_id,i3.name as project_name from t_task a\n");
        sql.append("inner join t_object_type i1 on i1.id=a.object_type\n");
        sql.append("inner join t_project_status_define i2 on i2.id=a.status\n");
        sql.append("inner join t_project i3 on i3.id=a.project_id\n");
        sql.append("where a.company_id=").append(account.companyId).append("\n");
        sql.append("and json_contains(a.owner_account_id_list,'" + account.id + "')\n");
        sql.append("and ( (a.start_date < ? and a.end_date is null) or (a.start_date < ? and a.end_date>=?))\n");
        sql.append("and a.is_finish=false\n");
        sql.append("and a.is_delete=false\n");
        sql.append("and a.object_type in(");
        for (int objectType : objectTypeInList) {
            sql.append(objectType).append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")\n");
        sql.append("order by a.create_time desc\n");
        sql.append("limit 10000");
        return queryList(TaskInfo.class, sql.toString(), beginNextMonth, beginNextMonth, beginMonth);
    }

    /**
     *
     */
    public List<CallProcCheckDataResult> callProcCheckData() {
        return call(CallProcCheckDataResult.class, "call proc_check_data();");
    }

    /**
     * @return
     */
    public List<TaskActionJob> getNeedRunTaskActionJobs() {
        return queryList(TaskActionJob.class, "select * from t_task_action_job where "
                + "run_time is not null and run_time<? and is_run=false", new Date());
    }

    /**
     * @param companyId
     * @return
     */
    public int getCompanyAdminNum(int companyId) {
        String sql = "select count(1) from t_company_member_role a "
                + "inner join t_role b on a.role_id=b.id "
                + "inner join t_account c on a.account_id=c.id "
                + "where b.name='管理员' and a.company_id=? and c.status=?;";
        return queryCount(sql, companyId, Account.STATUS_有效);
    }

    public Category getCategory(int projectId, int objectType, String name) {
        return getDomain(Category.class, QueryWhere.create().
                where("project_id", projectId).
                where("object_type", objectType).
                where("name", name));
    }

    public List<Integer> getObjectTypeList(int companyId) {
        return queryForIntegers("select id from t_object_type");
    }

    public Department getDepartmentByCompanyIdName(int companyId, String name) {
        return getDomain(Department.class, QueryWhere.create().
                where("company_id", companyId).
                where("name", name));
    }

    public Department getDepartmentByCompanyIdId(int companyId, int id) {
        return getDomain(Department.class, QueryWhere.create().
                where("company_id", companyId).
                where("id", id));
    }

    public Project getProjectByCompanyIdName(int companyId, String projectName) {
        return getDomain(Project.class, QueryWhere.create().
                where("company_id", companyId).
                where("name", projectName).
                where("is_delete", false));
    }

    public Project getProjectByCompanyIdNameTemplateId(int companyId, String projectName, int templateId) {
        return getDomain(Project.class, QueryWhere.create().
                where("company_id", companyId).
                where("name", projectName).
                where("template_id", templateId).
                where("is_delete", false));
    }

    public ObjectType getObjectTypeByName(String objectTypeName) {
        return getDomain(ObjectType.class, QueryWhere.create().
                where("name", objectTypeName));
    }

    public void deleteCalendarSchedules(int calendarId) {
        delete(CalendarSchedule.class, QueryWhere.create().where("calendar_id", calendarId));
    }

    public AccountInviteMember getAccountInviteMember(int accountId) {
        return getDomain(AccountInviteMember.class, QueryWhere.create().
                where("account_id", accountId));
    }

    public void doResetAccountInviteMemberDailySendMailNum() {
        executeUpdate("update t_account_invite_member set daily_send_mail_num=0");
    }

    public SerialNoGenerator getSerialNoGeneratorForUpdate(int companyId, int type) {
        return getDomain(SerialNoGenerator.class, QueryWhere.create().
                where("company_id", companyId).
                where("type", type).
                forUpdate()
        );
    }

    public List<TaskWorkTimeLogInfo> getProjectWorkTimeLogList(TaskWorkTimeLogQuery query) {
        StringBuilder sql = new StringBuilder();
        sql.append("select a.project_id,a.create_account_id,i1.name as project_name,i3.name as create_account_name,sum(hour) as hour from t_task_work_time_log a \r\n" +
                "inner join t_project i1 on a.project_id=i1.id\r\n" +
                "inner join t_task i2 on a.task_id=i2.id\r\n" +
                "inner join t_account i3 on a.create_account_id=i3.id\r\n");
        sql.append("where 1=1\r\n");
        List<Object> values = new ArrayList<>();
        if (query.projectId != null) {
            sql.append("and a.project_id=" + query.projectId + "\r\n");
        }
        if (query.objectType != null) {
            sql.append("and i2.object_type=" + query.objectType + "\r\n");
        }
        if (query.startTimeStart != null) {
            sql.append("and a.start_time>=?\r\n");
            values.add(query.startTimeStart);
        }
        if (query.startTimeEnd != null) {
            sql.append("and a.start_time<?\r\n");
            values.add(query.startTimeEnd);
        }
        sql.append("group by a.project_id,a.create_account_id,project_name,create_account_name;");

        return queryList(TaskWorkTimeLogInfo.class, sql.toString(), values.toArray());
    }

    /**
     * 复制附件
     *
     * @param originalId
     * @param newTaskId
     */
    public void copyTaskAttachments(int originalId, int newTaskId, int newProjectId) {
        executeUpdate("insert into t_attachment_associated(company_id,attachment_id,project_id,task_id,create_time,update_time) "
                + "select company_id,attachment_id," + newProjectId + "," + newTaskId + ",create_time,update_time from t_attachment_associated "
                + "where task_id=?", originalId);
    }

    /**
     * 复制评论
     *
     * @param originalId
     */
    public void copyTaskComments(int originalId, int newTaskId) {
        executeUpdate("insert into t_task_comment(company_id,task_id,`comment`,create_account_id,is_delete,create_time,update_time) "
                + "select company_id," + newTaskId + ",`comment`,create_account_id,is_delete,create_time,update_time from t_task_comment "
                + "where task_id=? and is_delete=false", originalId);
    }

    /**
     * 复制变更记录
     *
     * @param originalId
     * @param newTaskId
     */
    public void copyTaskChangeLogs(int originalId, int newTaskId) {
        executeUpdate("insert into t_change_log(company_id,project_id,task_id,type,item_id,items,create_account_id,remark,create_time,update_time) "
                + "select company_id,0," + newTaskId + ",type,item_id,items,create_account_id,remark,create_time,update_time from t_change_log "
                + "where task_id=? and type!=?", originalId, ChangeLog.TYPE_创建);

        executeUpdate("insert into t_change_log_diff(task_id,before_content,after_content,create_time,update_time) "
                + "select " + newTaskId + ",before_content,after_content,create_time,update_time from t_change_log_diff "
                + "where task_id=?", originalId);
    }

    /**
     * 复制关联对象
     *
     * @param originalId
     * @param newTaskId
     */
    public void copyTaskAssociateds(int originalId, int newTaskId) {
        executeUpdate("insert into t_task_associated(company_id,task_id,associated_task_id,associated_type,create_time,update_time) "
                + "select company_id," + newTaskId + ",associated_task_id,associated_type,create_time,update_time from t_task_associated "
                + "where task_id=?", originalId);
        executeUpdate("insert into t_task_associated(company_id,task_id,associated_task_id,associated_type,create_time,update_time) "
                + "select company_id,task_id," + newTaskId + ",associated_type,create_time,update_time from t_task_associated "
                + "where associated_task_id=?", originalId);
    }

    /**
     * 复制测试用例
     *
     * @param originalTestPlanId
     * @param newTestPlanId
     * @param accountId
     */
    public void copyTestPlanTestCases(int originalTestPlanId, int newTestPlanId, int accountId) {
        executeUpdate("insert into t_test_plan_test_case(company_id,project_id,test_plan_id,test_case_id,status,create_account_id,update_account_id) "
                + "select company_id,project_id," + newTestPlanId + ",test_case_id,1," + accountId + "," + accountId + " from t_test_plan_test_case "
                + "where test_plan_id=?", originalTestPlanId);
    }

    /**
     * @param codes
     * @return
     */
    public int getCompanyMemberInviteCodeCount(List<String> codes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select count(1) from t_company_member_invite_code where code in(");
        for (String code : codes) {
            sql.append("'").append(code).append("',");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");
        return queryCount(sql.toString());
    }

    public CompanyMemberInviteCode getCompanyMemberInviteCodeForUpdate(String code) {
        return getDomain(CompanyMemberInviteCode.class,
                QueryWhere.create().
                        where("code", code).
                        forUpdate()
        );
    }

    public void updateAccountKaptchaErrorCount(Account account) {
        executeUpdate("update t_account set disable_end_time=?,kaptcha_error_count=? where id=?",
                account.disableEndTime, account.kaptchaErrorCount, account.id);
    }

    public void doResetKaptchaErrorCount() {
        executeUpdate("update t_account set kaptcha_error_count=0 where kaptcha_error_count>0");
    }

    public int getAppVersion() {
        Integer version = queryForInteger("select version from t_app_version");
        if (version == null) {
            version = 0;
        }
        return version;
    }

    /**
     * 复制数据权限
     *
     * @param optAccountId
     * @param newProjectId
     * @param srcProjectId
     */
    public void batchCopyProjectDataPermissions(int optAccountId, int newProjectId, int srcProjectId) {
        String sql = "insert into t_project_data_permission(company_id,project_id,object_type,permission_id,owner_list,create_account_id,update_account_id) "
                + "select company_id," + newProjectId + ",object_type,permission_id,owner_list," + optAccountId + ",0 from t_project_data_permission where project_id=" + srcProjectId;
        executeUpdate(sql);
    }

    public void batchCopyProjectObjectTypeTemplatesCategories(Account optAccount, int newProjectId, int srcProjectId) {
        String sql = "insert into t_project_object_type_template(company_id,project_id,object_type,name,content,"
                + "create_account_id,update_account_id) "
                + "select " + optAccount.companyId + "," + newProjectId
                + ",object_type,name,content," + optAccount.id + ",0 from t_project_object_type_template where project_id=" + srcProjectId;
        executeUpdate(sql);
    }

    public List<Integer> getValidAccountIds(int companyId) {
        String sql = "select id from t_account where company_id=? and status=?";
        return queryForIntegers(sql, companyId, Account.STATUS_有效);
    }

    public CompanyReportConfig getCompanyReportConfig(int companyId) {
        return getDomain(CompanyReportConfig.class,
                QueryWhere.create().
                        where("company_id", companyId));
    }

    public List<Integer> getNeedSendSystemNotifactionList() {
        String sql = "select id from t_system_notification "
                + "where is_delete=false and status=? and next_notify_time<? "
                + "limit 1000";
        return queryForIntegers(sql, SystemNotification.STATUS_有效, new Date());
    }

    public ObjectType getObjectTypeBySystemName(String systemName) {
        return getDomain(ObjectType.class,
                QueryWhere.create().
                        where("system_name", systemName));
    }

    public Task getTaskByCompanyIdProjectIdAssociateProjectId(int companyId, int projectId,
                                                              int associateProjectId) {
        return getDomain(Task.class,
                QueryWhere.create().
                        where("company_id", companyId).
                        where("project_id", projectId).
                        where("associate_project_id", associateProjectId)
        );
    }

    /**
     * 获取任务 通过公司id、项目id、关联项目id集
     * @param companyId
     * @param projectId
     * @param associateProjectIdList
     * @return
     */
    public List<Task> getTaskByCompanyIdProjectIdAssociateProjectIdList(int companyId, int projectId,
                                                              List<Integer> associateProjectIdList) {
        return getList(Task.class,
                QueryWhere.create().
                        where("company_id", companyId).
                        where("project_id", projectId).
                        where("associate_project_id", "in", associateProjectIdList.toArray())
        );
    }

    public List<String> getProjectMemberTags(int projectId) {
        String sql = "select tag from t_project_member where project_id=?";
        return queryForStrings(sql, projectId);
    }

    public void deleteTestPlanTestCaseByTestCaseId(int testCaseId) {
        executeUpdate("delete from t_test_plan_test_case where test_case_id=?", testCaseId);
    }

    public void deleteTestPlanTestCaseByTestPlanId(int testPlanId) {
        executeUpdate("delete from t_test_plan_test_case where test_plan_id=?", testPlanId);
    }

    public CompanyVersionTask getCompanyVersionTask(int versionId, int taskId) {
        return queryObject(CompanyVersionTask.class, "select * from t_company_version_task where version_id=? and task_id=?",
                versionId, taskId);
    }

    public String getCompanyUuidByLarkEnantKey(String tenantKey) {
        return queryForString("select company_uuid from t_company_lark where lark_tenant_key=?", tenantKey);
    }

    public CompanyLark getCompanyLarkByLarkTenantKey(String larkTenantKey) {
        return queryObject(CompanyLark.class, "select * from t_company_lark where lark_tenant_key=?",
                larkTenantKey);
    }

    public Account getAccountByQywxUserId(String qywxUserId) {
        return queryObject(Account.class, "select * from t_account where qywx_user_id=?",
                qywxUserId);
    }

    public List<Integer> getCompanyVersionTaskIdList(int versionId) {
        return queryForIntegers("select task_id from t_company_version_task where version_id=?", versionId);
    }

    public List<CompanyVersion> getCompanyVersionTotalTaskNums(List<Integer> idList) {
        StringBuilder sql = new StringBuilder();
        sql.append("select version_id as id,count(1) as total_task_num from t_company_version_task "
                + "where version_id in(");
        for (Integer id : idList) {
            sql.append(id).append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")  group by version_id");
        return queryList(CompanyVersion.class, sql.toString());
    }

    public List<CompanyVersion> getCompanyVersionFinishTaskNums(List<Integer> idList) {
        StringBuilder sql = new StringBuilder();
        sql.append("select version_id as id,count(1) as finish_task_num from t_company_version_task a "
                + "inner join t_task i1 on a.task_id=i1.id "
                + "where version_id in(");
        for (Integer id : idList) {
            sql.append(id).append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(") and i1.is_finish=true group by version_id");
        return queryList(CompanyVersion.class, sql.toString());
    }

    public List<ProjectFieldDefine> getProjectFieldDefineByPsField(int companyId, int objectType) {
        return getList(ProjectFieldDefine.class, QueryWhere.create()
                .where("company_id", companyId)
                .where("object_type", objectType)
                .where("is_ps_field", true));
    }

    public void batchUpdateDepartmentLevel(List<Department> updateLevelDepartments) {
        String sql = "update t_department set level=? where id =?";
        int batchSize = 256, size = updateLevelDepartments.size();
        int batch = size % batchSize == 0 ? size / batchSize : size / batchSize + 1;
        for (int i = 0; i < batch; i++) {
            List<Department> subList = updateLevelDepartments.subList(i * batchSize, Math.min((i + 1) * batchSize, size));
            List<Object[]> params = new ArrayList<>();
            subList.forEach(k -> params.add(new Object[]{k.level, k.id}));
            executeBatch(sql, params);
        }
    }

    public List<Account> getAccountIdsByIds(List<Integer> ids) {
        return getList(Account.class, QueryWhere.create().in(SelectProvider.MAIN_TABLE_ALIAS, "id", ids.toArray()));
    }

    public List<String> getProjectGroupNames(int companyId) {
        return queryForStrings("select distinct(`group`) from t_project where company_id=? AND `group` IS NOT NULL", companyId);
    }

    public List<ObjectType> getObjectTypeListByIds(Set<Integer> objectTypes) {
        return getList(ObjectType.class, QueryWhere.create().in(SelectProvider.MAIN_TABLE_ALIAS, "id", objectTypes.toArray()));
    }

    public List<ObjectType> getObjectTypeListByIds(Object[] objectTypes) {
        return getList(ObjectType.class, QueryWhere.create().in(SelectProvider.MAIN_TABLE_ALIAS, "id", objectTypes));
    }

    public void updateTaskDayData(TaskDayData data) {
        executeUpdate("update t_task_day_data set total_num=?,total_finish_num=?,today_finish_num=?,total_commit_num=?,today_commit_num=?,total_load=?,total_finish_load=?,today_finish_load=? where company_id=? and  project_id=?  and iteration_id=?  and object_type=?  and stat_date=?",
                data.totalNum, data.totalFinishNum, data.todayFinishNum, data.totalCommitNum, data.todayCommitNum,data.totalLoad,data.totalFinishLoad,data.todayFinishLoad, data.companyId, data.projectId, data.iterationId, data.objectType, data.statDate);
    }

    public void updateAccountNotificationSendState(AccountNotification.AccountNotificationInfo notification) {
        executeUpdate("update t_account_notification set is_web_send=?,is_weixin_send=?,is_lark_send=?,is_dingtalk_send=? where id=?",
                notification.isWebSend, notification.isWeixinSend, notification.isLarkSend, notification.isDingtalkSend, notification.id);
    }

    public Integer queryForAccountSuperBoss(int accountId) {
        return queryForInteger("select super_boss from t_account where id =?", accountId);
    }

    public PasswordRule getPasswordRuleByCompanyId(int companyId) {
        return getDomain(PasswordRule.class, QueryWhere.create().where("company_id", companyId));
    }

    public void deleteDeliveryItem(int deliveryId) {
        delete(DeliveryItem.class, QueryWhere.create().where("delivery_id", deliveryId));
    }

    public DeliveryItem getDeliveryItemByDeliveryIdAndVersionId(int deliveryId, int versionId) {
        return getDomain(DeliveryItem.class, QueryWhere.create().where("delivery_id", deliveryId).where("version_id", versionId));
    }

    public void updateDeliveryVersionStatus(Delivery bean) {
        executeUpdate("update t_delivery set status =? where id=?", bean.status, bean.id);
    }

    public List<TaskInfo> getSubTaskListById(int parentId) {
        return getList(TaskInfo.class, QueryWhere.create().where("parent_id", parentId));
    }

    public ScmCommitLog getScmCommitLogByVersionAndCompanyId(int companyId, String version) {
        return getDomain(ScmCommitLog.class, QueryWhere.create().where("company_id", companyId).where("version", version));
    }

    public Integer getScmCommitLogIdByVersionAndCompanyId(int companyId, String version) {
        return queryForInteger("select id from t_scm_commit_log where company_id=? and version=?", companyId, version.trim());
    }

    public void updateGitlabScmCommitLogNum(ScmCommitLog commitLog) {
        executeUpdate("update t_scm_commit_log set add_line_num =add_line_num+? ,decrease_line_num =decrease_line_num+? where id =?", commitLog.addLineNum, commitLog.decreaseLineNum, commitLog.id);
    }

    public void updateGitlabScmCommitLogNum(Integer commitLogId, int addLineNum, int decreaseLineNum) {
        executeUpdate("update t_scm_commit_log set add_line_num =add_line_num+? ,decrease_line_num =decrease_line_num+? where id =?", addLineNum, decreaseLineNum, commitLogId);
    }

    public void updateTaskDateLonely(Task task, String dateField, Date dateValue) {
        executeUpdate("update t_task set " + BizUtil.getColumnFieldName(dateField) + "=? where id=?", dateValue, task.id);
    }

    public List<ProjectMemberInfo> getProjectMemberInfoList(int projectId) {
        return getList(ProjectMemberInfo.class, QueryWhere.create().where("project_id", projectId));
    }

    public List<ProjectIteration> getProjectIterationList(int projectId) {
        return getList(ProjectIteration.class, QueryWhere.create().where("project_id", projectId));
    }

    public List<ProjectRelease> getProjectReleaseList(int projectId) {
        return getList(ProjectRelease.class, QueryWhere.create().where("project_id", projectId));
    }

    public List<ProjectSubSystem> getProjectSubSystemList(int projectId) {
        return getList(ProjectSubSystem.class, QueryWhere.create().where("project_id", projectId));
    }

    public List<Account> getCompanyAccountList(int companyId) {
        return getList(Account.class, QueryWhere.create().where("company_id", companyId));
    }

    public List<Category> getCompanyCategoryList(int companyId, int projectId, int objectType) {
        Category.CategoryQuery query = new Category.CategoryQuery();
        query.companyId = companyId;
        query.projectId = projectId;
        query.objectType = objectType;
        query.pageSize = -1;
        return getList(query);
//        return getList(Category.class, QueryWhere.create().where("company_id", companyId).where("project_id", projectId));
    }

    public void updateTaskDescription(TaskDetailInfo bean) {
        executeUpdate("update t_task set task_description_id=? where id=?", bean.taskDescriptionId, bean.id);
    }

    public List<TaskAssociated> getTaskAssociatedTaskList(int taskId) {
        return getList(TaskAssociated.class, QueryWhere.create().where("task_id", taskId)
                .in(SelectProvider.MAIN_TABLE_ALIAS, "associated_type", new Object[]{TaskAssociated.ASSOCIATED_TYPE_前置任务, TaskAssociated.ASSOCIATED_TYPE_后置任务}));
    }

    public void updateTaskParentId(TaskInfo subTask) {
        executeUpdate("update t_task set parent_id=? where id=?", subTask.parentId, subTask.id);
    }

    public void updateParentTaskCount(TaskInfo parentTask) {
        executeUpdate("update t_task set sub_task_count=?,finish_sub_task_count=? where id =?", parentTask.subTaskCount, parentTask.finishSubTaskCount, parentTask.id);
    }

    public List<TaskAssociated> getTaskAssociatedByTaskIds(Object[] taskIds) {
        return getList(TaskAssociated.class, QueryWhere.create().in(SelectProvider.MAIN_TABLE_ALIAS, "task_id", taskIds));
    }

    public ProjectFieldDefine getCheckProjectFieldDefine(int projectId, int objectType, Integer fieldDefineId) {
        return getDomain(ProjectFieldDefine.class, QueryWhere.create()
                .where("id", fieldDefineId).where("project_id", projectId).where("object_type", objectType));
    }

    public void updateCustomeProductField(ProjectFieldDefine e) {
        executeUpdate("update t_project_field_define set field=? where id =?", e.field, e.id);
    }

    public List<Project> getProjectList(int companyId) {
        return getList(Project.class, QueryWhere.create().where("company_id", companyId).where("status", Project.STATUS_运行中));
    }

    public void updateStageWorkday(Stage.StageInfo bean) {
        executeUpdate("update t_stage set work_day=?,start_date=?,end_date=?,change_remark=? where id=?",
                bean.workDay, bean.startDate, bean.endDate, bean.changeRemark, bean.id);
    }

    public List<Task> getStageTaskListByProjectId(int projectId) {
        String sql = "SELECT t1.`create_time`,t1.`end_date`,t1.`finish_time`,t1.`object_type`,t1.`is_finish` FROM t_stage_associate t LEFT JOIN t_task t1 " +
                "ON t.`associate_id` = t1.id  WHERE t.`project_id` = ? ";
        return queryForList(sql, row -> {
            Task task = new Task();
            task.startDate = row.getDate(1);
            task.endDate = row.getDate(2);
            task.finishTime = row.getDate(3);
            return task;
        }, projectId);
    }

    public List<Task> getStageTaskListByProjectIdObjectType(int projectId) {
        String sql = "SELECT t1.`create_time`,t1.`end_date`,t1.`finish_time`,t1.`object_type`,t1.`is_finish` FROM t_stage_associate t LEFT JOIN t_task t1 " +
                "ON t.`associate_id` = t1.id  WHERE t.`project_id` = ? ";
        return queryForList(sql, row -> {
            Task task = new Task();
            task.startDate = row.getDate(1);
            task.endDate = row.getDate(2);
            task.finishTime = row.getDate(3);
            task.objectType = row.getInt(4);
            task.isFinish = row.getBoolean(5);
            return task;
        }, projectId);
    }

    public List<ProjectStatusDefine> getProjectStatusListByStatus(Object[] status) {
        return getList(ProjectStatusDefine.class, QueryWhere.create().in(SelectProvider.MAIN_TABLE_ALIAS, "id", status));
    }

    public void updateTaskWorkTime(Task task) {
        executeUpdate("update t_task set work_time=? where id=?", task.workTime, task.id);
    }

    public void deleteAllCompanyScmBranch(int companyId) {
        delete(ScmBranch.class, QueryWhere.create().where("company_id", companyId));
    }

    public List<ScmBranch> getScmBranchListByCompanyId(int companyId) {
        return getList(ScmBranch.class, QueryWhere.create().where("company_id", companyId));
    }

    public Date getPreTaskLastEndData(int id) {
        String sql = "SELECT MAX(t1.end_date) FROM t_task_associated t LEFT JOIN t_task t1 " +
                "ON t.`associated_task_id` = t1.`id` WHERE t.`task_id` = ? AND t.`associated_type` = 1;";
        return queryForDate(sql, id);
    }

    public List<File> getFileListByProjectId(int projectId) {
        return getList(File.class, QueryWhere.create().where("project_id", projectId));
    }

    public List<Wiki> getWikiListByProjectId(int projectId) {
        return getList(Wiki.class, QueryWhere.create().where("project_id", projectId));
    }

    public List<WikiPage> getWikiPageListByProjectId(int projectId) {
        return getList(WikiPage.class, QueryWhere.create().where("project_id", projectId));
    }

    public WikiAssociated getWikiAssociatedByTaskIdAttachmentId(int wikiPageId, int taskId) {
        return getDomain(WikiAssociated.class, QueryWhere.create().where("task_id", taskId).where("wiki_page_id", wikiPageId));
    }

    public List<WikiPage.WikiPageInfo> getTaskAssociateWikiPageList(Object[] wikiIds) {
        return getList(WikiPage.WikiPageInfo.class, QueryWhere.create().in(SelectProvider.MAIN_TABLE_ALIAS, "id", wikiIds));
    }

    public List<ProjectModuleInfo> getCompanyProjectModuleList(int companyId) {
        return getList(ProjectModuleInfo.class, QueryWhere.create().where("company_id", companyId).where("is_enable", true));
    }

    public void deleteTemplateProjectFile(int projectId) {
        delete(File.class, QueryWhere.create().where("project_id", projectId));
    }

    public void updateFileParentId(Integer readId, Integer readParentId) {
        execute("update t_file set parent_id=? where id=?", readParentId, readId);
    }

    public List<AttachmentAssociated> getAttachmentAssociatedListByAttachmentIdAndCompanyId(int id, int companyId) {
        return getList(AttachmentAssociated.class, QueryWhere.create().where("company_id", companyId).where("attachment_id", id));
    }

    public List<WikiAssociated> getWikiAssociatedListByWikiPageIdAndCompanyId(int id, int companyId) {
        return getList(WikiAssociated.class, QueryWhere.create().where("company_id", companyId).where("wiki_page_id", id));
    }


    public List<Integer> getParentTaskIdList(Set<Integer> taskIds) {
        String sql = "select parent_id from t_task where id in (?)";
        return queryForIntegers(sql, Joiner.on(",").join(taskIds));
    }

    public List<Integer> getSubTaskIdList(Set<Integer> taskIds) {
        String sql = "select id from t_task where parent_id in (?)";
        return queryForIntegers(sql, Joiner.on(",").join(taskIds));
    }

    public void updateTaskParentId(Integer taskId, Integer parentId) {
        executeUpdate("update t_task set parent_id=? where id =?", parentId, taskId);
    }

    public void increamentTaskSubTaskCount(Integer taskId) {
        executeUpdate("update t_task set sub_task_count=sub_task_count+1 where id=?", taskId);
    }

    public int getAccountOwnerDepartmentCount(int accountId) {
        return getListCount(DepartmentOwner.class, QueryWhere.create().where("owner_account_id", accountId));
    }

    public Project getProjectSetProject(int companyId) {
        return getDomain(Project.class, QueryWhere.create().where("company_id", companyId
        ).where("is_template", false).where("template_id", Project.ID_项目集模板ID));
    }

    public Account getAccountByDingtalkUserId(String userid) {
        return getDomain(Account.class, QueryWhere.create().where("dingtalk_user_id", userid));
    }

    public Department getDepartmentByAccountIdAndDepartmentId(Integer ownerAccountId, int departmentId) {
        return getDomain(Department.class, QueryWhere.create().where("parent_id", departmentId).where("type", Department.TYPE_人员).where("account_id", ownerAccountId));
    }

    public List<Stage.StageInfo> getProjectStageList(int companyId, int projectId) {
        Stage.StageQuery sq = new Stage.StageQuery();
        sq.companyId = companyId;
        sq.projectId = projectId;
        sq.isDelete = false;
        sq.pageSize = Integer.MAX_VALUE;
        return getList(sq);
    }

    public Landmark getProjectLandmarkByName(int projectId, String name) {
        return getDomain(Landmark.class, QueryWhere.create().where("project_id", projectId).where("name", name).where("is_delete", false));
    }

    public void deleteStageLandmarkAssociate(int stageId, int landmarkId) {
        delete(StageAssociate.class, QueryWhere.create().where("stage_id", stageId).where("landmark_id", landmarkId));
    }

    public void deleteStageTaskAssociate(int stageId, int taskId) {
        delete(StageAssociate.class, QueryWhere.create().where("stage_id", stageId).where("associate_id", taskId));
    }

    public List<ProjectModuleInfo> getProjectModuleListByProjectIds(Set<Integer> projectIdList) {
        return getList(ProjectModuleInfo.class, QueryWhere.create().in(SelectProvider.MAIN_TABLE_ALIAS, "project_id", projectIdList.toArray()));
    }


    public void resetPreLandmark(int preLandmarkId) {
        executeUpdate("update t_landmark set pre_id =0  where pre_id =?", preLandmarkId);
    }

    public void resetLandmarkStage(int stageId) {
        executeUpdate("update t_landmark set stage_id =0  where stage_id =?", stageId);
    }

    public void clearStageAssociateLandmark(int stageId) {
        delete(StageAssociate.class, QueryWhere.create().where("stage_id", stageId).where("type", StageAssociate.TYPE_里程碑));
    }

    public String getProjectSerialNo(int projectId) {
        return queryForString("SELECT serial_no FROM t_task WHERE associate_project_id = ? AND object_type=?", projectId, Task.OBJECTTYPE_项目清单);
    }

    public void updateProjectModuleName(int id, String name) {
        executeUpdate("update t_project_module set name =? where object_type=?", name, id);
    }

    public void updateDataFixFlag(String name) {
        executeUpdate("update t_config set `value`='1' where name=?", name);
    }

    public Task getProjectSetTaskByProjectId(int id) {
        return getDomain(Task.class, QueryWhere.create().where("associate_project_id", id), Sets.newHashSet("id", "uuid", "serialNo"));
    }

    public AccountHomeSetting getAccountHomeSetting(int accountId, int companyId) {
        return getDomain(AccountHomeSetting.class, QueryWhere.create()
                .where("account_id", accountId).where("company_id", companyId));
    }

    public void deleteAccountStar(AccountStar bean) {
        delete(AccountStar.class, QueryWhere.create()
                .where("account_id", bean.accountId).where("company_id", bean.companyId)
                .where("type", bean.type).where("associate_id", bean.associateId));
    }

    public AccountStar getAccountStarTask(Account account, int id) {
        return getDomain(AccountStar.class, QueryWhere.create()
                .where("account_id", account.id).where("type", AccountStar.TYPE_任务).where("associate_id", id));
    }

    public TaskSortSetting getTaskSortSetting(TaskSortSetting bean) {
        return getDomain(TaskSortSetting.class, QueryWhere.create()
                .where("company_id", bean.companyId).where("project_id", bean.projectId)
                .where("object_type", bean.objectType).where("type", bean.type));
    }

    /**
     * 批量删除部门下的成员信息
     */
    public void deleteAccountDepartment(int departmentId, List<Integer> oldOwnerAccountIdList) {
        delete(Department.class, QueryWhere.create()
                .where("parent_id", departmentId)
                .whereSql(" find_in_set(account_id ,?)", Joiner.on(",").join(oldOwnerAccountIdList)));
//               .in(SelectProvider.MAIN_TABLE_ALIAS,"account_id",oldOwnerAccountIdList.toArray()));
    }

    public void batchInsertDingtalkMember(List<DingtalkMember> members) {
        String sql = "insert ignore into t_dingtalk_member (dingtalk_id,name,mobile_no,job_number,title) value (?,?,?,?,?);";
        int batchSize = 256;
        int total = members.size();
        int batch = (int) Math.ceil(total*1.0f / batchSize);
        for (int i = 0; i < batch; i++) {
            List<DingtalkMember> subList = members.subList(i * batchSize, Math.min(total, (i + 1) * batchSize));
            List<Object[]> params = new ArrayList<>();
            subList.forEach(member -> {
                Object[] os = new Object[5];
                os[0] = member.dingtalkId;
                os[1] = member.name;
                os[2] = member.mobileNo;
                os[3] = member.jobNumber;
                os[4] = member.title;
                params.add(os);
            });
            executeBatch(sql, params);
        }
    }

    public void batchInsertDingtalkAttendance(List<DingtalkAttendance> attendanceList) {
        String sql = "insert into t_dingtalk_attendance (account_id,dingtalk_member_id,dingtalk_id,source_type,am_base_time,am_user_time,am_time_result,am_location," +
                "pm_base_time,pm_user_time,pm_time_result,pm_location,work_date) value (?,?,?,?,?,?,?,?,?,?,?,?,?);";
        int batchSize = 256;
        int total = attendanceList.size();
        int batch = (int) Math.ceil(total*1.0f / batchSize);
        for (int i = 0; i < batch; i++) {
            List<DingtalkAttendance> subList = attendanceList.subList(i * batchSize, Math.min(total, (i + 1) * batchSize));
            List<Object[]> params = new ArrayList<>();
            subList.forEach(member -> {
                Object[] os = new Object[13];
                os[0] = member.accountId;
                os[1] = member.dingtalkMemberId;
                os[2] = member.dingtalkId;
                os[3] = member.sourceType;
                os[4] = member.amBaseTime;
                os[5] = member.amUserTime;
                os[6] = member.amTimeResult;
                os[7] = member.amLocation;
                os[8] = member.pmBaseTime;
                os[9] = member.pmUserTime;
                os[10] = member.pmTimeResult;
                os[11] = member.pmLocation;
                os[12] = member.workDate;
                params.add(os);
            });
            executeBatch(sql, params);
        }
    }

    public void batchUpdateAdAccount(List<Account> accounts) {
        String sql = "update t_account set status=? where ad_name =?;";
        int batchSize = 256, size = accounts.size();
        int batch = size % batchSize == 0 ? size / batchSize : size / batchSize + 1;
        for (int i = 0; i < batch; i++) {
            List<Account> subList = accounts.subList(i * batchSize, Math.min((i + 1) * batchSize, size));
            List<Object[]> params = new ArrayList<>();
            subList.forEach(k -> params.add(new Object[]{k.status, k.adName}));
            executeBatch(sql, params);
        }
    }

    /**
     * 根据AD域账号查询用户
     * @param adName
     * @return
     */
    public Account getAccountByAdNameForUpdate(String adName) {
        return getDomain(Account.class,QueryWhere.create()
                .where("ad_name",adName)
                .where("status",Account.STATUS_有效).forUpdate());
    }

    public void updateDingtalkAttendanceAccount(DingtalkMember member) {
        executeUpdate("update t_dingtalk_attendance set account_id=? where dingtalk_member_id=?",member.accountId,member.id);
    }

    public void resetDingtalkAttendanceAccount(int accountId) {
        executeUpdate("update t_dingtalk_attendance set account_id=0 where account_id=?",accountId);
    }

    public void updateSupplierMemberStatus(Account account) {
        executeUpdate("update t_supplier_member set status=? where account_id=?",account.status,account.id);
    }

    public void deleteAttendaceByMonth(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        executeUpdate("delete from t_dingtalk_attendance where date_format(work_date,'%Y-%m')= DATE_FORMAT(?,'%Y-%m');",format.format(date));
    }

    public List<Integer> getManagedProjectIdList(Account account) {
        return queryForIntegers("SELECT id FROM t_project WHERE company_id =?  and json_contains(leader_account_id_list,'"+account.id+"')"
                ,account.companyId);
    }

    /**
     * 分管的督办任务
     */
    public List<Integer> getManagedTaskIdList(Account account) {
        return queryForIntegers("SELECT id FROM t_task WHERE object_type=? and  company_id =?  and json_contains(leader_account_id_list,'"+account.id+"')"
                ,Task.OBJECTTYPE_项目督办,account.companyId);
    }

    /**
     * 查询可供@成员信息
     * @param memberIds
     * @return
     */
    public List<Account> getAccountMentionListByIds(Set<Integer> memberIds) {
        String sql ="select id,name,user_name,email,pinyin_name from t_account where find_in_set(id,?);";
        return queryList(Account.class,sql,Joiner.on(",").join(memberIds));
    }

    /**
     * 查询项目已用工时
     * @param projectId
     * @return
     */
    public double getProjectCostWorkTime(int projectId) {
        String sql ="SELECT SUM(HOUR) FROM t_task_work_time_log t LEFT JOIN " +
                "t_task t1 ON t.`task_id` = t1.`id` WHERE t1.`is_delete` =FALSE AND t.project_id = ?;";
        return  queryForDouble(sql,projectId);
    }

    public void deleteTaskDescription(int taskId) {
        delete(TaskDescription.class,QueryWhere.create().where("task_id",taskId));
    }

    public void updateDataDictName(DataDict dataDict) {
        executeUpdate("update `t_data_dict` set `name` =? where `category_code`=? and `value`=?",dataDict.name,dataDict.categoryCode,dataDict.value);
    }
}
