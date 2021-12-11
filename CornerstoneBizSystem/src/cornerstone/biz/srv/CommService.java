package cornerstone.biz.srv;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import cornerstone.biz.BizExceptionCode;
import cornerstone.biz.dao.BizDAO;
import cornerstone.biz.domain.*;
import cornerstone.biz.domain.Account.AccountInfo;
import cornerstone.biz.domain.AccountSimpleInfo.AccountSimpleInfoQuery;
import cornerstone.biz.domain.ProjectMember.ProjectMemberInfo;
import cornerstone.biz.domain.ProjectModule.ProjectModuleInfo;
import cornerstone.biz.domain.ProjectModule.ProjectModuleQuery;
import cornerstone.biz.domain.Role.RoleInfo;
import cornerstone.biz.domain.Role.RoleQuery;
import cornerstone.biz.domain.SurveysDefine.PermissionRole;
import cornerstone.biz.domain.SurveysDefine.ProjectPermissionRole;
import cornerstone.biz.domain.Task.TaskQuery;
import cornerstone.biz.domain.TaskAssociated.TaskAssociatedInfo;
import cornerstone.biz.domain.TaskAssociated.TaskAssociatedQuery;
import cornerstone.biz.util.BizUtil;
import cornerstone.biz.util.DateUtil;
import cornerstone.biz.util.LicenseUtil;
import cornerstone.biz.util.LicenseUtil.License;
import cornerstone.biz.util.SqlUtils;
import cornerstone.biz.util.StringUtil;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;
import jazmin.driver.jdbc.smartjdbc.Query;
import jazmin.driver.jdbc.smartjdbc.SmartJdbcException;
import jazmin.driver.jdbc.smartjdbc.SqlBean;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.provider.SelectProvider;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

/**
 * @author cs
 */
public class CommService {

    //
    private static Logger logger = LoggerFactory.get(CommService.class);

    @AutoWired
    public BizDAO dao;

    //公共
    public void setupQuery(Account account, Query query) {
        BizUtil.setFieldValue(query, "isDelete", false);
        BizUtil.setFieldValue(query, "companyId", account.companyId);
    }

    public Map<String, Object> createResult(List<?> list, int count) {
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("count", count);
        return result;
    }

    public void checkNullOrDelete(Object info, String msg) {
        if (info == null) {
            throw new AppException(msg);
        }
        Boolean isDelete = (Boolean) BizUtil.getFieldValue(info, "isDelete");
        if (isDelete == null) {
            return;
        }
        if (isDelete) {
            throw new AppException(msg);
        }
    }

    public Class<?> getDomainClass(Query query) {
        QueryDefine queryDefine = query.getClass().getAnnotation(QueryDefine.class);
        if (queryDefine == null) {
            throw new SmartJdbcException("no domainClass found in QueryClass[" + query.getClass().getName() + "]");
        }
        return queryDefine.domainClass();
    }

    //
    public void addAccountNotification(int accountId, int type, int companyId, int projectId, int associatedId,
                                       String name, String content, Date expectPushTime, Account optAccount) {
        if (optAccount != null && optAccount.id > 0) {
            addAccountNotification(accountId, type, companyId, projectId, associatedId, name, content, expectPushTime,
                    optAccount.id, optAccount.name, optAccount.imageId);
        } else {
            addAccountNotification(accountId, type, companyId, projectId, associatedId, name, content, expectPushTime,
                    0, "", "");
        }
    }

    //通知相关

    /**
     * 发送通知
     */
    public void addAccountNotification(int accountId, int type, int companyId, int projectId, int associatedId,
                                       String name, String content, Date expectPushTime, int optAccountId, String optAccountName,
                                       String optAccountImageId) {
        AccountNotification bean = new AccountNotification();
        bean.accountId = accountId;
        bean.type = type;
        bean.companyId = companyId;
        bean.projectId = projectId;
        bean.associatedId = associatedId;
        bean.name = name;
        bean.content = content;
        bean.expectPushTime = expectPushTime;
        bean.optAccountId = optAccountId;
        bean.optAccountName = optAccountName;
        bean.optAccountImageId = optAccountImageId;
        dao.add(bean);
    }

    //
    //生成序列号
    public int generateSerialNo(int companyId, int type) {
        SerialNoGenerator serialNo = dao.getSerialNoGeneratorForUpdate(companyId, type);
        if (serialNo == null) {
            serialNo = new SerialNoGenerator();
            serialNo.companyId = companyId;
            serialNo.type = type;
            serialNo.serialNo = 10000;
            dao.add(serialNo);
        } else {
            serialNo.serialNo++;
            dao.update(serialNo);
        }
        return serialNo.serialNo;
    }

    //
    //
    //
    //权限管理
    //
    //
    //
    public boolean isCompanyAdmin(Account account) {
        Role role = dao.getRoleByCompanyIdTypeName(account.companyId, Role.TYPE_全局, Role.NAME_管理员);
        CompanyMemberRole memberRole = dao.getAccountRoleByAccountIdRoleId(account.id, role.id);
        return memberRole != null;
    }

    /**
     *
     */
    public void checkIsCompanyAdmin(Account account) {
        if (!isCompanyAdmin(account)) {
            throw new AppException("权限不足");
        }
    }

    public void checkSameCompany(int companyId1, int companyId2) {
        if (companyId1 != companyId2) {
            throw new AppException("权限不足");
        }
    }

    /**
     * 可见性
     */
    public boolean checkTaskViewPermission(int accountId, int projectId, int objectType) {
        ProjectModule module = dao.getProjectModuleByProjectObjectTypeForUpdate(projectId, objectType);
        if (module == null) {
            logger.error("checkTaskViewPermission false accountId:{} projectId:{} objectType:{}",
                    accountId, projectId, objectType);
            return false;
        }
        if (module.isPublic) {
            return true;
        }
        if (module.publicRoles != null && module.publicRoles.size() > 0) {
            if (checkProjectRoles(accountId, projectId, module.publicRoles)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 校验项目角色
     */
    public boolean checkProjectRoles(int accountId, int projectId, List<Integer> publicRoles) {
        for (Integer roleId : publicRoles) {
            ProjectMemberRole role = dao.getProjectMemberRole(accountId, projectId, roleId);
            if (role != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否是项目成员
     */
    public void checkIsProjectMember(int accountId, int projectId) {
        ProjectMemberInfo member = dao.getProjectMemberInfoByProjectIdAccountId(projectId, accountId);
        if (member == null) {
            Account account = dao.getById(Account.class,accountId);
            throw new AppException(account.name+"非此项目成员");
        }
    }

    public boolean isPrivateDeploy(Account account) {
        Company company = dao.getExistedById(Company.class, account.companyId);
        return company.version == Company.VERSION_私有部署版;
    }

    public boolean isPrivateDeploy(Company company) {
        return company.version == Company.VERSION_私有部署版;
    }

    /**
     * 检查企业是否是私有部署
     */
    public void checkIsPrivateDeploy(Account account) {
        Company company = dao.getExistedById(Company.class, account.companyId);
        if (company.version != Company.VERSION_私有部署版) {
            throw new AppException("权限不足");
        }
    }

    public boolean isSuperBoss(Account account) {
        if (null == account) {
            return false;
        }
        return Lists.newArrayList(Account.BOSS_企业_读写, Account.BOSS_企业_只读, Account.BOSS_部门_读写, Account.BOSS_部门_只读).contains(account.superBoss);
    }

    /**
     * 判断是否是企业级超级boss(全部CS项目的boss权限)
     */
    public boolean isCompanySuperBoss(Account account) {
        return Account.BOSS_企业_读写 == account.superBoss || Account.BOSS_企业_只读 == account.superBoss;
    }

    /**
     * 判断是否是企业级超级boss
     */
    public boolean isCompanySuperBoss(int accountId) {
        Integer superBoss = dao.queryForAccountSuperBoss(accountId);
        return null != superBoss && (Account.BOSS_企业_读写 == superBoss || Account.BOSS_企业_只读 == superBoss);
    }

    /**
     * 判断是否是部门级超级boss(所管辖部门及子部门的项目boss权限)
     */
    public boolean isDepartmentSuperBoss(Account account) {
        return Account.BOSS_部门_读写 == account.superBoss || Account.BOSS_部门_只读 == account.superBoss;
    }

    /**
     * 判断是否是部门级超级boss
     */
    public boolean isDepartmentSuperBoss(int accountId) {
        Integer superBoss = dao.queryForAccountSuperBoss(accountId);
        return null != superBoss && (Account.BOSS_部门_读写 == superBoss || Account.BOSS_部门_只读 == superBoss);
    }

    /**
     * 判断超级boss的读写权限
     */
    public boolean checkAccountSuperBossPermission(Account account, String permissionId, int projectId) {
        if (account.superBoss > 0) {
            if (Account.BOSS_企业_读写 == account.superBoss) {
                return true;
            } else if (Account.BOSS_企业_只读 == account.superBoss) {
                return Permission.isReadPermission(permissionId);
            } else if (Account.BOSS_部门_读写 == account.superBoss) {
                if (0 >= projectId) {
                    return false;
                } else {
                    Set<Integer> projectIds = getAccountAccessProjectList(account, false, false);
                    return !BizUtil.isNullOrEmpty(projectIds) && projectIds.contains(projectId);
                }
            } else if (Account.BOSS_部门_只读 == account.superBoss) {
                if (0 >= projectId) {
                    return false;
                } else {
                    Set<Integer> projectIds = getAccountAccessProjectList(account, false, false);
                    return !BizUtil.isNullOrEmpty(projectIds) && projectIds.contains(projectId) && Permission.isReadPermission(permissionId);
                }
            }
        }
        return false;
    }


    public void checkAccountPermission(Account account) {
        if (account.companyId == 0) {
            throw new AppException("权限不足");
        }
    }

    /**
     * 判断是否可访问项目
     */
    public void checkPermissionForProjectAccess(Account account, Integer projectId) {
        checkAccountPermission(account);
        if (projectId == null) {
            throw new AppException("权限不足");
        }
        Project project = dao.getExistedById(Project.class, projectId);
        checkPermissionForProjectAccess(account, project);
    }

    /**
     * 判断是否可访问项目
     */
    public void checkPermissionForProjectAccess(Account account, Project project) {
        if (project.isTemplate) {
            return;
        }
        if (isCompanySuperBoss(account)) {
            return;
        }
        if (isDepartmentSuperBoss(account)) {
            Set<Integer> projectIds = getAccountAccessProjectList(account, false, false);
            if (!BizUtil.isNullOrEmpty(projectIds) && projectIds.contains(project.id)) {
                return;
            }
        }
//		if(account.companyId!=project.companyId) {//去掉这个限制 saas版存成这种可能
//			throw new AppException("权限不足");
//		}
        Set<String> allPermissions = getAllGlobalPermission(account);
        if (allPermissions.contains(Permission.ID_设置企业信息)) {//产品确认 认为是超级管理员 可以有无限权限
            return;
        }
        //判断是否是项目成员
        checkIsProjectMember(account.id, project.id);
    }

    public void checkPermissionForTask(Account account, int taskId) {
        checkAccountPermission(account);
        Task task = dao.getExistedById(Task.class, taskId);
        if (account.companyId != task.companyId) {
            throw new AppException("权限不足");
        }
    }

    public void checkPermissionForTask(Account account, Task task) {
        checkAccountPermission(account);
        if (account.companyId != task.companyId) {
            throw new AppException("权限不足");
        }
    }

    public Set<String> getAllGlobalPermission(Account account, int companyId) {
        Set<String> permissions = new HashSet<>();
        RoleQuery query = new RoleQuery();
        query.globalAccountId = account.id;
        query.companyId = companyId;
        query.pageSize = Integer.MAX_VALUE;
        List<RoleInfo> roles = dao.getAll(query);
        for (RoleInfo e : roles) {
            if (e.permissionIds != null) {
                permissions.addAll(e.permissionIds);
            }
        }
        return permissions;
    }

    /**
     * 查询用户所有全局权限
     */
    public Set<String> getAllGlobalPermission(Account account) {
        Set<String> permissions = new HashSet<>();
        RoleQuery query = new RoleQuery();
        query.globalAccountId = account.id;
        query.companyId = account.companyId;
        query.pageSize = Integer.MAX_VALUE;
        List<RoleInfo> roles = dao.getAll(query);
        for (RoleInfo e : roles) {
            if (e.permissionIds != null) {
                permissions.addAll(e.permissionIds);
            }
        }
        //license限制
        Set<String> needRemovePermissions = getNeedRemovePermissions(account.companyId);
        for (String needRemovePermission : needRemovePermissions) {
            permissions.remove(needRemovePermission);
        }
        //
        return permissions;
    }

    /**
     * license是否过期
     */
    public boolean isLicenseExpired(String license) {
        if (StringUtil.isEmpty(license)) {
            return true;
        }
        try {
            License liceneBean = LicenseUtil.decryptByPublicKey(license);
            if (liceneBean.expireDate.before(new Date())) {//到期了
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return true;
        }
        return false;
    }

    /**
     * 是否是试用版license
     */
    public boolean isTrailLicense(String license) {
        if (StringUtil.isEmpty(license)) {
            return false;
        }
        try {
            License liceneBean = LicenseUtil.decryptByPublicKey(license);
            if (!StringUtil.isEmpty(liceneBean.id) && liceneBean.id.startsWith("Trail")) {
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return false;
    }

    //
    public Set<String> getNeedRemovePermissions(int companyId) {
        Set<String> permissions = new HashSet<>();
        if (companyId == 0) {
            return permissions;
        }
        Company company = dao.getExistedById(Company.class, companyId);
        if (!StringUtil.isEmpty(company.license)) {
            License liceneBean = LicenseUtil.decryptByPublicKey(company.license);
            if (liceneBean.moduleList != null && liceneBean.moduleList.size() > 0) {//说明设置了
                if (!liceneBean.moduleList.contains(License.MODULE_CMDB)) {//没有CMDB
                    permissions.add(Permission.ID_CMDB);
                    permissions.add(Permission.ID_查看CMDB);
                }
                if (!liceneBean.moduleList.contains(License.MODULE_DEVOPS)) {//没有DEVOPS
                    permissions.add(Permission.ID_DEVOPS);
                    permissions.add(Permission.ID_查看devops列表);
                    permissions.add(Permission.ID_创建和编辑主机);
                    permissions.add(Permission.ID_创建和编辑pipeline);
                }
                if (!liceneBean.moduleList.contains(License.MODULE_WORKFLOW)) {//没有workflow
                    permissions.add(Permission.ID_流程管理);
                    permissions.add(Permission.ID_管理流程模板);
                    permissions.add(Permission.ID_管理流程数据);
                    permissions.add(Permission.ID_发起流程);
                    permissions.add(Permission.ID_开启流程模块);
                }
                if (!liceneBean.moduleList.contains(License.MODULE_WIKI)) {//没有wiki
                    permissions.add(Permission.ID_WIKI);
                    permissions.add(Permission.ID_查看知识库列表);
                    permissions.add(Permission.ID_创建和编辑页面);
                    permissions.add(Permission.ID_创建和编辑知识库);
                }
                if (!liceneBean.moduleList.contains(License.MODULE_CODEGEN)) {//没有codegen
                    permissions.add(Permission.ID_代码助手);
                    permissions.add(Permission.ID_代码设计器);
                    permissions.add(Permission.ID_数据库配置);
                    permissions.add(Permission.ID_编辑MYSQL代理);
                    permissions.add(Permission.ID_模板配置);
                    permissions.add(Permission.ID_查看代码助手);
                }
                if (!liceneBean.moduleList.contains(License.MODULE_FILE)) {//没有file
                    permissions.add(Permission.ID_文件);
                    permissions.add(Permission.ID_创建文件夹);
                    permissions.add(Permission.ID_删除文件);
                    permissions.add(Permission.ID_下载文件);
                    permissions.add(Permission.ID_查看文件列表);
                    permissions.add(Permission.ID_移动);
                    permissions.add(Permission.ID_预览文件);
                    permissions.add(Permission.ID_上传文件);
                }
                if (!liceneBean.moduleList.contains(License.MODULE_REPORT)) {//没有汇报
                    permissions.add(Permission.ID_汇报管理);
                    permissions.add(Permission.ID_开启汇报模块);
                    permissions.add(Permission.ID_创建汇报);
                    permissions.add(Permission.ID_汇报模板管理);
                }
                if (!liceneBean.moduleList.contains(License.MODULE_DISCUSS)) {//没有讨论
                    permissions.add(Permission.ID_讨论管理);
                    permissions.add(Permission.ID_开启讨论模块);
                    permissions.add(Permission.ID_创建讨论);
                }
                if (!liceneBean.moduleList.contains(License.MODULE_PROJECTSET)) {//没有项目集
                    permissions.add(Permission.ID_项目集管理);
                    permissions.add(Permission.ID_开启项目集管理模块);
                }
            }
        }

        //功能开关
        boolean isSupplierEnable=Boolean.parseBoolean(GlobalConfig.getValue("supplier.func.open", "false"));
        if(!isSupplierEnable){
            permissions.add(Permission.ID_SUPPLIER);
            permissions.add(Permission.ID_创建和编辑供应商);
            permissions.add(Permission.ID_查看供应商);
            permissions.add(Permission.ID_删除供应商);
        }
        boolean isAttendanceEnable= Boolean.parseBoolean(GlobalConfig.getValue("attendance.func.open", "false"));
        if(!isAttendanceEnable){
            permissions.add(Permission.ID_ATTENDANCE);
            permissions.add(Permission.ID_查看钉钉考勤);
            permissions.add(Permission.ID_创建和编辑钉钉考勤);
            permissions.add(Permission.ID_删除钉钉考勤);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("needRemovePermissions:{}", permissions);
        }
        return permissions;
    }
    //

    /**
     * 是否拥有公司权限permissionId
     */
    public boolean haveCompanyPermission(Account account, String permissionId) {
        Set<String> permissions = getAllGlobalPermission(account);
        if (permissions.contains(permissionId)) {
            return true;
        }
        return false;
    }

    /**
     * 项目权限
     */
    public Set<String> getAllProjectPermission(Account account, int companyId, int projectId) {
        Set<String> permissions = new HashSet<>();
        RoleQuery query = new RoleQuery();
        if (null != account) {
            query.projectAccountId = account.id;
        }
        query.companyId = companyId;
        query.projectId = projectId;
        List<RoleInfo> roles = dao.getAll(query);
        for (RoleInfo e : roles) {
            if (e.permissionIds != null) {
                permissions.addAll(e.permissionIds);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("getAllProjectPermission {}", DumpUtil.dump(permissions));
        }
        return permissions;
    }

    /**
     * 查询我没有的数据权限
     */
    public Set<String> getTaskNoDataPermissionList(Task task, int accountId) {
        Set<Integer> myRoleIds = getMyProjectRoleIdList(accountId, task.projectId);
        if (task.createAccountId == accountId) {
            myRoleIds.add(ProjectDataPermission.OWNER_ID_创建人);
        }
        if (BizUtil.contains(task.ownerAccountIdList, accountId)) {
            myRoleIds.add(ProjectDataPermission.OWNER_ID_责任人);
        }
        Set<String> noDataPermissionIds = new HashSet<>();
        List<ProjectDataPermission> dataPermissions = dao.getAllProjectDataPermission(task.projectId);
        for (ProjectDataPermission e : dataPermissions) {
            if (!BizUtil.contains(myRoleIds, BizUtil.convert(e.ownerList))) {
                noDataPermissionIds.add(e.permissionId);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("noDataPermissionIds {}", DumpUtil.dump(myRoleIds), DumpUtil.dump(noDataPermissionIds));
        }
        return noDataPermissionIds;
    }

    /**
     * 查询我的任务权限
     */
    public Set<String> getMyTaskPermission(Account account, Task task) {
        Set<String> permissionList = new HashSet<>();
        if (!isSuperBoss(account)) {
            permissionList = getAllProjectPermission(account, account.companyId, task.projectId);
            Set<String> noDataPermissionIds = getTaskNoDataPermissionList(task, account.id);
            for (String noDataPermission : noDataPermissionIds) {
                permissionList.remove(noDataPermission);
            }
        } else {
            permissionList = getAllProjectPermission(null, account.companyId, task.projectId);
            if (account.superBoss == Account.BOSS_企业_只读 || account.superBoss == Account.BOSS_部门_只读) {
                permissionList = permissionList.stream().filter(Permission::isReadPermission).collect(Collectors.toSet());
            }
        }

        return permissionList;
    }

    /**
     * 校验企业权限
     */
    public void checkCompanyPermission(Account account, Integer companyId) {
        checkCompanyPermission(account, null, companyId);
    }

    /**
     * 校验企业权限
     *
     */
    public void checkCompanyPermission(Account account, String permissionId, Integer companyId) {
        if (account.companyId == 0) {
            throw new AppException("权限不足");
        }
        if (account.status == Account.STATUS_无效) {
            throw new AppException(BizExceptionCode.CODE_TOKEN过期, "TOKEN已过期");
        }
        if (companyId != null) {
            if (account.companyId != companyId) {
                throw new AppException("权限不足");
            }
        }
        if (!BizUtil.isNullOrEmpty(permissionId)) {
            if (isCompanySuperBoss(account)) {
                if (checkAccountSuperBossPermission(account, permissionId, 0)) {
                    return;
                }
            }
            Set<String> allPermissions = getAllGlobalPermission(account);
            if (!allPermissions.contains(permissionId)) {
                logger.error("checkCompanyPermission failed.account:{} allPermissions not contains permissionId:{}",
                        account.id, permissionId);
                throw new AppException("权限不足");
            }
        }
    }

    /**
     * 校验企业权限
     *
     */
    public void checkCompanyPermission(Account account, String permissionId) {
        checkCompanyPermission(account, permissionId, null);
    }

    /**
     * 校验项目权限
     */
    public void checkProjectPermission(Account account, int projectId, String permissionId) {
        checkProjectPermission(account, account.companyId, projectId, permissionId);
    }

    /**
     * 检查项目权限
     *
     */
    public void checkProjectPermission(Account account, int companyId, int projectId, String permissionId) {
        if (companyId > 0 && account.companyId != companyId) {
            throw new AppException("所属企业权限不足");
        }
        //如果是超级boss角色先走超级boss的权限验证再走普通权限验证
        if (isSuperBoss(account)) {
            if (checkAccountSuperBossPermission(account, permissionId, projectId)) {
                return;
            }
        }
        Set<String> allPermissions = getAllProjectPermission(account, companyId, projectId);
        if (!allPermissions.contains(permissionId)) {
            logger.error("checkProjectPermission failed.account:{} allPermissions not contains permissionId:{}",
                    account.id,
                    permissionId);
            throw new AppException("权限不足【" + permissionId + "】");
        }
    }


    /**
     * 我的项目角色列表
     *
     */
    public Set<Integer> getMyProjectRoleIdList(int accountId, int projectId) {
        ProjectMember member = dao.getProjectMemberInfoByProjectIdAccountId(projectId, accountId);
        if (member == null) {
            throw new AppException("权限不足");
        }
        RoleQuery roleQuery = new RoleQuery();
        roleQuery.projectMemberId = member.id;
        roleQuery.pageSize = Integer.MAX_VALUE;
        List<Role> roleList = dao.getList(roleQuery);
        Set<Integer> idList = new HashSet<>();
        for (Role role : roleList) {
            idList.add(role.id);
        }
        return idList;
    }

    /////账号相关//
    //
    //
    //
    ///

    /**
     * token查询账户信息
     */
    public Account getExistedAccountByToken(String token) {
        AccountToken at = dao.getAccountTokenByToken(token);
        if (at == null) {
            logger.warn("token is expired {}", token);
            throw new AppException(BizExceptionCode.CODE_TOKEN过期, "TOKEN已过期");
        }
        Account account = dao.getExistedById(Account.class, at.accountId);
        checkAccount(account);
        return account;
    }

    public Account getAccountByToken(String token) {
        if (StringUtil.isEmptyWithTrim(token)) {
            return null;
        }
        AccountToken at = dao.getAccountTokenByToken(token);
        if (at == null) {
            return null;
        }
        return dao.getById(Account.class, at.accountId);
    }

    public void checkAccount(Account account) {
        if (account.status == Account.STATUS_无效) {
            throw new AppException("账号已被禁用");
        }
        if (account.disableEndTime != null && account.disableEndTime.after(new Date())) {
            throw new AppException("账号已被锁定,解锁时间" + DateUtil.formatDate(account.disableEndTime, "MM-dd HH:mm"));
        }
    }

    public Account getExistedAccountByTokenForUpdate(String token) {
        AccountToken at = dao.getAccountTokenByToken(token);
        if (at == null) {
            throw new AppException(BizExceptionCode.CODE_TOKEN过期, "TOKEN已过期");
        }
        Account account = dao.getExistedByIdForUpdate(Account.class, at.accountId);
        checkAccount(account);
        return account;
    }

    public AccountInfo getExistedAccountInfoByToken(String token) {
        AccountToken at = dao.getAccountTokenByToken(token);
        if (at == null) {
            throw new AppException(BizExceptionCode.CODE_TOKEN过期, "TOKEN已过期");
        }
        AccountInfo account = dao.getExistedById(AccountInfo.class, at.accountId);
        checkAccount(account);
        return account;
    }

    public List<AccountSimpleInfo> getAccountSimpleInfoList(int[] accountIds) {
        if (accountIds == null || accountIds.length == 0) {
            return new ArrayList<>();
        }
        AccountSimpleInfoQuery query = new AccountSimpleInfoQuery();
        query.idInList = accountIds;
        return dao.getAll(query);
    }
    //
    //
    //
    //组织架构相关
    //
    //
    //

    /**
     * 获取departmentId下所有组织架构列表(包含departmentId)
     *
     */
    public int[] getChildDepartmentIds(int departmentId) {
        Department department = dao.getExistedById(Department.class, departmentId);
        List<Department> all = dao.getDepartmentByCompanyIdType(department.companyId, Department.TYPE_组织架构);
        Queue<Department> queue = new LinkedList<>();
        queue.offer(department);
        List<Integer> result = new ArrayList<>();
        result.add(department.id);//包含departmentId
        while (!queue.isEmpty()) {
            Department dep = queue.poll();
            List<Department> removes = new ArrayList<>();
            for (Department e : all) {
                if (e.parentId == dep.id) {
                    queue.offer(e);
                    result.add(e.id);
                    removes.add(e);
                }
            }
            for (Department e : removes) {
                all.remove(e);
            }
        }
        return BizUtil.convertList(result);
    }


    ///
    //
    /////模块 对象类型相关
    //
    //
    //

    /**
     * 查询一个项目下有状态关联的模块
     */
    public List<Integer>  getStatusBasedObjectTypelist(int projectId) {
        List<ProjectModuleInfo> modules = getEnableProjectModuleInfoList(projectId);
        List<Integer> statusBaseObjectType = new ArrayList<>();
        for (ProjectModuleInfo e : modules) {
            if (e.isStatusBased && e.objectType > 0) {
                statusBaseObjectType.add(e.objectType);
            }
        }
        return statusBaseObjectType;
    }

    //

    /**
     * 查询一个企业下有状态关联的模块
     */
    public int[] getStatusBasedObjectTypeListByCompanyId(int companyId) {
        List<ProjectModuleInfo> modules = getEnableProjectModuleInfoListByCompanyId(companyId);
        Set<Integer> statusBaseObjectType = new LinkedHashSet<>();
        for (ProjectModuleInfo e : modules) {
            if (e.isStatusBased && e.objectType > 0) {
                statusBaseObjectType.add(e.objectType);
            }
        }
        return BizUtil.convertList(statusBaseObjectType);
    }

    //

    /**
     * 查询一个项目下开启的timeBased模块
     */
    public int[] getTimeBasedObjectTypelist(int projectId) {
        List<ProjectModuleInfo> modules = getEnableProjectModuleInfoList(projectId);
        List<Integer> statusBaseObjectType = new ArrayList<>();
        for (ProjectModuleInfo e : modules) {
            if (e.isTimeBased && e.objectType > 0) {
                statusBaseObjectType.add(e.objectType);
            }
        }
        return BizUtil.convertList(statusBaseObjectType);
    }

    /**
     * 查询一个公司下开启的timeBased模块
     */
    public int[] getTimeBasedObjectTypelistByCompanyId(int companyId) {
        List<ProjectModuleInfo> modules = getEnableProjectModuleInfoListByCompanyId(companyId);
        Set<Integer> statusBaseObjectType = new LinkedHashSet<>();
        for (ProjectModuleInfo e : modules) {
            if (e.isTimeBased && e.objectType > 0) {
                statusBaseObjectType.add(e.objectType);
            }
        }
        return BizUtil.convertList(statusBaseObjectType);
    }

    public List<ProjectModuleInfo> getEnableProjectModuleInfoList(int projectId) {
        ProjectModuleQuery query = new ProjectModuleQuery();
        query.projectId = projectId;
        query.isEnable = true;
        query.pageSize = Integer.MAX_VALUE;
        query.sortWeightSort = Query.SORT_TYPE_ASC;
        return dao.getList(query);
    }

    /**
     * 一个项目下开启的timebase的模块列表
     *
     */
    public List<ProjectModuleInfo> getEnableTimeBaseProjectModuleInfoList(int projectId) {
        ProjectModuleQuery query = new ProjectModuleQuery();
        query.projectId = projectId;
        query.isEnable = true;
        query.isTimeBased = true;
        query.pageSize = Integer.MAX_VALUE;
        query.sortWeightSort = Query.SORT_TYPE_ASC;
        return dao.getList(query);
    }

    /**
     * 一个项目下开启的statusbase的模块列表
     */
    public List<ProjectModuleInfo> getEnableStatusBaseProjectModuleInfoList(int projectId) {
        ProjectModuleQuery query = new ProjectModuleQuery();
        query.projectId = projectId;
        query.isEnable = true;
        query.isStatusBased = true;
        query.pageSize = Integer.MAX_VALUE;
        query.sortWeightSort = Query.SORT_TYPE_ASC;
        return dao.getList(query);
    }

    public List<ProjectModuleInfo> getEnableProjectModuleInfoListByCompanyId(int companyId) {
        ProjectModuleQuery query = new ProjectModuleQuery();
        query.companyId = companyId;
        query.isEnable = true;
        query.pageSize = Integer.MAX_VALUE;
        query.sortWeightSort = Query.SORT_TYPE_ASC;
        return dao.getList(query);
    }

    //
    //
    //任务相关
    //
    //
    public Map<String, Object> getTaskInfoList0(Account account, TaskQuery query, Set<String> includeField, boolean needAssociateTasks) {
        return getTaskInfoList0(account, query, includeField, true, true, needAssociateTasks);
    }

    //
    public Map<String, Object> getTaskInfoList0(Account account, TaskQuery query, Set<String> includeField,
                                                boolean needList, boolean needCount, boolean needAssociateTasks) {
        setupQuery(account, query);
        if (query.customFieldsSort != null && query.customFieldsSort.size() > 0) {//如果有自定义字段排序，则删除默认时间排序
            query.orderType = null;
        }
        if (query.priorityNameSort > 0) {//不能按照拼音排序 按照排序顺序排
            query.prioritySortWeightSort = query.priorityNameSort;
            query.priorityNameSort = 0;
        }
        if (query.ownerAccountNameSort > 0) {
            query.ownerAccountIdListSort = query.ownerAccountNameSort;
            query.ownerAccountNameSort = 0;
        }
        if (query.projectId != null && query.objectType != null) {//如果不可见则只能查看你自己创建的或责任人
            if (!checkTaskViewPermission(account.id, query.projectId, query.objectType)) {
                query.ownerAccountOrCreateAccountIdId = account.id;
            }
        }
        query.companyId = account.companyId;
        if (query.projectId != null) {
            Project project = dao.getExistedById(Project.class, query.projectId);
            checkNullOrDelete(project, "项目不存在");
        }
        if (query.ownerAccountId != null) {
            if (query.ownerAccountId == 0) {
                query.ownerAccountIdIsNull = true;
                query.ownerAccountId = null;
            }
        }
        StringBuffer addWhereSql = new StringBuffer();
        List<Object> values = new ArrayList<>();
        if (query.isSetCategory != null) {
            if (query.isSetCategory) {
                addWhereSql.append(" and JSON_LENGTH(a.category_id_list)>0 \n");
            } else {
                addWhereSql.append(" and (JSON_LENGTH(a.category_id_list) is null or JSON_LENGTH(a.category_id_list)=0) \n");
            }
        }
        if (!BizUtil.isNullOrEmpty(query.categoryIdList)) {
            setupCategoryIdList(query);//查出子分类
            int index = 1;
            addWhereSql.append(" and ( ");
            for (Integer id : query.categoryIdList) {
                addWhereSql.append(" json_contains(a.category_id_list,'" + id + "') ");
                if (index != query.categoryIdList.length) {
                    addWhereSql.append(" or ");
                }
                index++;
            }
            addWhereSql.append(" ) \n");
        }
        if (!BizUtil.isNullOrEmpty(query.ownerAccountIdList)) {
            addWhereSql.append(" and ( ");
            addWhereSql.append(SqlUtils.makeJsonContainsSql("a.owner_account_id_list",
                    query.ownerAccountIdList, FilterCondition.OPERATOR_等于));
            addWhereSql.append(" ) \n");
        }
        if (query.filterId != null) {//过滤器
            addWhereSql.append(SqlUtils.addFilterSql(dao, account, query, values));
        }
        //自定义字段过滤
        WhereSql ws = addTaskQueryCustomFieldsWhereSql(query);
        if (ws != null) {
            addWhereSql.append(ws.sql);
            if (ws.values != null) {
                values.addAll(ws.values);
            }
        }
        //
        return queryTaskInfoList(query, addWhereSql.toString(), values.toArray(),
                includeField, needList, needCount, needAssociateTasks);
    }

    private void setupCategoryIdList(TaskQuery query) {
        if (query.categoryIdList == null || query.categoryIdList.length == 0) {
            return;
        }
        List<Integer> all = new ArrayList<>();
        for (Integer categoryId : query.categoryIdList) {
            all.add(categoryId);
            List<Integer> children = dao.getChildrenByParentId(categoryId);
            if (children != null && children.size() > 0) {
                all.addAll(children);
            }
        }
        query.categoryIdList = Ints.toArray(all);
    }


    private WhereSql addTaskQueryCustomFieldsWhereSql(TaskQuery query) {
        if (query.customFields == null || query.customFields.isEmpty()) {
            return null;
        }
        StringBuilder sql = new StringBuilder();
        List<Object> values = new ArrayList<>();
        for (Map.Entry<String, Object> entry : query.customFields.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key == null || value == null) {
                continue;
            }
            int fieldDefineId = BizUtil.getFieldDefineId(key);
            ProjectFieldDefine define = dao.getById(ProjectFieldDefine.class, fieldDefineId);
            if (define == null) {
                continue;
            }
            int operator = QueryCondition.OPERATOR_等于;
            if (define.type == ProjectFieldDefine.TYPE_单行文本框) {
                operator = QueryCondition.OPERATOR_LIKE;
            }
            String sqlKey = BizUtil.getCustomerSqlKey("a.custom_fields", key);//custom_fields->'$."field_11"'
            WhereSql whereSql = SqlUtils.createWhereSql(define, sqlKey, value, operator);
            if (whereSql != null) {
                sql.append("and(");
                sql.append(whereSql.sql);
                sql.append(")\n");
                if (whereSql.values != null && whereSql.values.size() > 0) {
                    values.addAll(whereSql.values);
                }
            }
        }
        WhereSql ws = new WhereSql();
        ws.sql = sql.toString();
        ws.values = values;
        return ws;
    }


    private Map<String, Object> queryTaskInfoList(TaskQuery query, String addWhereSql, Object[] addValues, Set<String> includeField,
                                                  boolean needList, boolean needCount, boolean needAssociateTasks) {
        Class<?> domainClass = getDomainClass(query);
        //includeFields(includeField)  这个不能用
        SqlBean sqlBean = new SelectProvider(domainClass).query(query).needPaging(true).build();
        sqlBean.whereSql += addWhereSql;
        if (!BizUtil.isNullOrEmpty(addValues)) {
            List<Object> newValues = new ArrayList<>();
            if (!BizUtil.isNullOrEmpty(sqlBean.parameters)) {
                newValues.addAll(Arrays.asList(sqlBean.parameters));
            }
            newValues.addAll(Arrays.asList(addValues));
            sqlBean.parameters = newValues.toArray();
        }
        sqlBean.orderBySql = getOrderBySql(query, sqlBean.orderBySql);
        String sql = sqlBean.toSql();
        List<?> taskInfos = new ArrayList<>();
        if (needList) {
            taskInfos = dao.queryList(domainClass, sql, sqlBean.parameters);
        }
        //
        SqlBean countSqlBean = new SelectProvider(domainClass).query(query).
                includeFields(includeField).needOrderBy(false).selectCount().build();
        countSqlBean.whereSql += addWhereSql;
        sql = countSqlBean.toSql();
        int count = -1;
        if (needCount) {
            count = dao.queryCount(sql, sqlBean.parameters);
        }
        try {
            if (needAssociateTasks && taskInfos.size() > 0) {
                Set<Integer> ids = new LinkedHashSet<>();
                for (Object task : taskInfos) {
                    ids.add((Integer) BizUtil.getFieldValue(task, "id"));
                }
                TaskAssociatedQuery associatedQuery = new TaskAssociatedQuery();
                associatedQuery.pageSize = Integer.MAX_VALUE;
                associatedQuery.taskIdInList = BizUtil.convertList(ids);
                //20200203修改 修复了jazmin的bug
//				SelectProvider selectProvider=new SelectProvider(TaskAssociatedInfo.class);
//				selectProvider.query(associatedQuery);
//				SqlBean assSqlBean=selectProvider.build();
//				assSqlBean.whereSql+=BizUtil.addInCondition("a.task_id", ids);
                List<TaskAssociatedInfo> associatedTasks = dao.getList(associatedQuery);
                Map<Integer, List<TaskAssociatedInfo>> associatedMap = new HashMap<>();
                for (TaskAssociatedInfo e : associatedTasks) {
                    List<TaskAssociatedInfo> list = associatedMap.computeIfAbsent(e.taskId, k -> new ArrayList<>());
                    list.add(e);
                }
                for (Object task : taskInfos) {
                    int id = ((int) BizUtil.getFieldValue(task, "id"));
                    BizUtil.setFieldValue(task, "associatedList",
                            associatedMap.get(id));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return createResult(taskInfos, count);
    }
    //

    //自定义字段排序
    private String getOrderBySql(TaskQuery query, String orderBySql) {
        StringBuilder sql = new StringBuilder();
        if (query.customFieldsSort != null) {
            int index = 0;
            for (Map.Entry<String, Integer> entry : query.customFieldsSort.entrySet()) {
                String key = entry.getKey();
                BizUtil.checkValidCustomFieldId(key);
                if (!StringUtil.isEmpty(orderBySql) || index > 0) {
                    sql.append(",");
                }
                if (entry.getValue() == Query.SORT_TYPE_ASC) {
                    sql.append("custom_fields->'$.").append(key).append("' asc");
                } else {
                    sql.append("custom_fields->'$.").append(key).append("' desc");
                }
                index++;
            }
        }
        sql.append(" ");
        if (StringUtil.isEmpty(orderBySql)) {
            orderBySql = "order by ";
        }
        orderBySql = orderBySql + sql.toString();
        return orderBySql;
    }

    //
    public Set<Integer> getAccountIds(int companyId,
                                      List<PermissionRole> userList, List<PermissionRole> companyRoleList,
                                      List<ProjectPermissionRole> projectRoleList, List<PermissionRole> departmentList,
                                      List<PermissionRole> departmentOwnerList) {
        Set<Integer> accountSet = new HashSet<>();
        addMembers(accountSet, userList);
        addDepartmentMembers(companyId, accountSet, departmentList);
        addDepartmentOwners(companyId, accountSet, departmentOwnerList);
        addCompanyRoleMembers(companyId, accountSet, companyRoleList);
        addProjectRoleMembers(companyId, accountSet, projectRoleList);
        return accountSet;
    }
    //

    // 成员选择
    private void addMembers(Set<Integer> accountSet, List<PermissionRole> userList) {
        if (userList != null) {
            for (PermissionRole user : userList) {
                accountSet.add(user.id);
            }
        }
    }

    // 部门成员列表
    private void addDepartmentMembers(int companyId, Set<Integer> accountSet, List<PermissionRole> departmentList) {
        if (departmentList != null) {// 部门成员列表
            Set<Integer> departmentIdSet = new LinkedHashSet<>();
            for (PermissionRole department : departmentList) {
                int[] departmentIds = getChildDepartmentIds(department.id);
                for (int departmentId : departmentIds) {
                    departmentIdSet.add(departmentId);
                }
            }
            if (!departmentIdSet.isEmpty()) {
                List<Integer> accountIdList = dao.getAccountIdsByDepartmentIds(companyId, departmentIdSet);
                accountSet.addAll(accountIdList);
            }
        }
    }

    // 部门负责人列表
    private void addDepartmentOwners(int companyId, Set<Integer> accountSet,
                                     List<PermissionRole> departmentOwnerList) {
        if (departmentOwnerList != null) {// 部门负责人列表
            Set<Integer> departmentIdSet = new LinkedHashSet<>();
            for (PermissionRole department : departmentOwnerList) {
                departmentIdSet.add(department.id);
            }
            List<Integer> accountIdList = dao.getOwnerAccountIdsByDepartmentIds(companyId, departmentIdSet);
            accountSet.addAll(accountIdList);
        }
    }

    // 公司角色列表
    private void addCompanyRoleMembers(int companyId, Set<Integer> accountSet, List<PermissionRole> companyRoleList) {
        if (companyRoleList != null) {
            Set<Integer> roleSet = new LinkedHashSet<>();
            for (PermissionRole role : companyRoleList) {
                roleSet.add(role.id);
            }
            List<Integer> accountIdList = dao.getAccountIdsByCompanyRoles(companyId, roleSet);
            accountSet.addAll(accountIdList);
        }
    }

    // 项目角色列表
    private void addProjectRoleMembers(int companyId, Set<Integer> accountSet, List<ProjectPermissionRole> projectRoleList) {
        if (projectRoleList != null) {
            Set<String> roleSet = new LinkedHashSet<>();
            for (ProjectPermissionRole role : projectRoleList) {
                roleSet.add(role.id);
            }
            List<Integer> accountIdList = dao.getAccountIdsByProjectRoles(companyId, roleSet);
            accountSet.addAll(accountIdList);
        }
    }


    /**
     * 查询当前用户可访问的项目ID列表
     *
     * @param account        当前用户
     * @param running        项目是否运行中
     * @param responsibility 部门成员是否是项目负责人
     */
    public Set<Integer> getAccountAccessProjectList(Account account, boolean running, boolean responsibility) {

        if (BizUtil.isNullOrEmpty(account)) {
            return null;
        }
        //普通成员
        if (account.superBoss == 0) {
            Set<Integer> pids = new HashSet<>();
            ProjectMember.ProjectMemberQuery projectMemberQuery = new ProjectMember.ProjectMemberQuery();
            projectMemberQuery.accountId = account.id;
            setupQuery(account, projectMemberQuery);
            projectMemberQuery.pageSize = Integer.MAX_VALUE;
            List<ProjectMember> members = dao.getList(projectMemberQuery);
            if (!BizUtil.isNullOrEmpty(members)) {
                pids = members.stream().map(k -> k.projectId).collect(Collectors.toSet());
                if (running) {
                    Project.ProjectQuery pq = new Project.ProjectQuery();
                    setupQuery(account, pq);
                    pq.idInList = BizUtil.convertList(pids);
                    pq.status = Project.STATUS_运行中;
                    pq.pageSize = Integer.MAX_VALUE;
                    List<Project> projects = dao.getList(pq);
                    if (!BizUtil.isNullOrEmpty(projects)) {
                        pids = projects.stream().map(k -> k.id).collect(Collectors.toSet());
                    } else {
                        pids = new HashSet<>();
                    }
                }
            }
            return pids;

            //企业boss
        } else if (isCompanySuperBoss(account)) {
            Project.ProjectQuery pq = new Project.ProjectQuery();
            setupQuery(account, pq);
            pq.status = Project.STATUS_运行中;
            pq.pageSize = Integer.MAX_VALUE;
            List<Project> projects = dao.getList(pq);
            if (!BizUtil.isNullOrEmpty(projects)) {
                return projects.stream().map(k -> k.id).collect(Collectors.toSet());
            }
            return null;
            //部门boss
        } else if (isDepartmentSuperBoss(account)) {
            Set<Integer> projectIds = new HashSet<>();

            //查询项目集项目
//            Project projectSet = dao.getProjectSetProject(account.companyId);
//            projectIds.add(projectSet.id);
            Map<Integer, List<Department>> accountDepartmentMap = new HashMap<>();
            List<Department> departments = new ArrayList<>();
            List<Integer> departmentIds = new ArrayList<>();
            DepartmentOwner.DepartmentOwnerQuery departmentOwnerQuery = new DepartmentOwner.DepartmentOwnerQuery();
            departmentOwnerQuery.ownerAccountId = account.id;
            departmentOwnerQuery.pageSize = Integer.MAX_VALUE;
            setupQuery(account, departmentOwnerQuery);
            List<DepartmentOwner> owners = dao.getList(departmentOwnerQuery);
            if (!BizUtil.isNullOrEmpty(owners)) {
                for (DepartmentOwner owner : owners) {
                    departmentIds.add(owner.departmentId);
                }
                Department.DepartmentQuery departmentQuery = new Department.DepartmentQuery();
//                departmentQuery.companyId = account.companyId;
                setupQuery(account, departmentQuery);
                departmentQuery.pageSize = Integer.MAX_VALUE;
                departments = dao.getList(departmentQuery);
                accountDepartmentMap = departments.stream().filter(k -> Department.TYPE_人员 == k.type)
                        .collect(Collectors.groupingBy(l -> l.accountId));

                Map<Integer, List<Integer>> depLevelMap = departments.stream().filter(k -> Department.TYPE_组织架构 == k.type)
                        .collect(Collectors.groupingBy(l -> l.parentId, mapping(v -> v.id, toList())));

                List<Integer> parents = new ArrayList<>(departmentIds);
                recursiveDepartmentIdList(departmentIds, parents, depLevelMap);
                if (BizUtil.isNullOrEmpty(departmentIds)) {
                    return projectIds;
                }
            } else {
                return projectIds;
            }
            Set<Integer> depIdList = new HashSet<>(departmentIds);
            //查询下级负责的项目
            if (responsibility) {
                try {
                    //查找项目ID
                    List<Project> projectList = dao.getProjectList(account.companyId);
                    if (running && !BizUtil.isNullOrEmpty(projectList)) {
                        projectList = projectList.stream().filter(k -> k.status == Project.STATUS_运行中).collect(toList());
                    }
                    if (!BizUtil.isNullOrEmpty(projectList)) {
                        Map<Integer, List<Project>> accountProjectMap = new HashMap<>();
                        Set<Integer> ownerAccountIdList = new HashSet<>();
                        ownerAccountIdList.add(account.id);
                        for (Project project : projectList) {
                            if (!BizUtil.isNullOrEmpty(project.ownerAccountIdList)) {
                                project.ownerAccountIdList.forEach(accountId -> accountProjectMap.computeIfAbsent(accountId, v -> new ArrayList<>()).add(project));
                                ownerAccountIdList.addAll(project.ownerAccountIdList);
                            }
                        }
                        ownerAccountIdList.add(account.id);
                        if (!BizUtil.isNullOrEmpty(ownerAccountIdList)) {
                            //获取部门下的成员负责的项目
                            for (Integer ownerId : ownerAccountIdList) {
                                List<Department> deps = accountDepartmentMap.get(ownerId);
                                if (!BizUtil.isNullOrEmpty(deps)) {
                                    List<Integer> depIds = deps.stream().map(k -> k.parentId).collect(toList());
                                    depIds.retainAll(depIdList);
                                    if (!BizUtil.isNullOrEmpty(depIds)) {
                                        List<Project> accountProjects = accountProjectMap.get(ownerId);
                                        if (!BizUtil.isNullOrEmpty(accountProjects)) {
                                            accountProjects.forEach(k -> projectIds.add(k.id));
                                        }
                                    }
                                }
                            }
                        }
                    }
//                return projectIds;
                } catch (Exception e) {
                    logger.error(e.getMessage(),e);;
                    logger.warn("query for project set err ");
                }
            } else {
                ProjectMember.ProjectMemberQuery memberQuery = new ProjectMember.ProjectMemberQuery();
                setupQuery(account, memberQuery);
                memberQuery.pageSize = Integer.MAX_VALUE;
                if (running) {
                    memberQuery.projectStatus = Project.STATUS_运行中;
                }
                //本部门下级部门成员
                Set<Integer> subAccountIds = new HashSet<>();
                subAccountIds.add(account.id);
                for (Department department : departments) {
                    if (department.type == Department.TYPE_人员 && departmentIds.contains(department.parentId)) {
                        subAccountIds.add(department.accountId);
                    }
                }
                if (!BizUtil.isNullOrEmpty(subAccountIds)) {
                    memberQuery.accountIdInList = BizUtil.convertList(subAccountIds);
                }
                List<ProjectMember> pms = dao.getList(memberQuery);
                if (!BizUtil.isNullOrEmpty(pms)) {
                    for (ProjectMember pm : pms) {
                        projectIds.add(pm.projectId);
                    }
                }
            }
            return projectIds;
        }
        return null;
    }


    private void recursiveDepartmentIdList(List<Integer> departmentIds, List<Integer> parentIds, Map<Integer, List<Integer>> depLevelMap) {
        if (BizUtil.isNullOrEmpty(parentIds)) {
            return;
        }
        parentIds.forEach(depId -> {
            List<Integer> subDepIds = depLevelMap.get(depId);
            if (!BizUtil.isNullOrEmpty(subDepIds)) {
                departmentIds.addAll(subDepIds);
                recursiveDepartmentIdList(departmentIds, subDepIds, depLevelMap);
            }
        });

    }


}
