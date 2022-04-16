package cornerstone.biz.action;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.property.CalendarScale;
import biweekly.property.Classification;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cornerstone.biz.*;
import cornerstone.biz.action.CommAction.CommActionImpl;
import cornerstone.biz.annotations.ApiDefine;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.dao.BizDAO;
import cornerstone.biz.dao.StatDAO;
import cornerstone.biz.domain.*;
import cornerstone.biz.domain.Account.AccountInfo;
import cornerstone.biz.domain.Account.AccountQuery;
import cornerstone.biz.domain.AccountNotification.AccountNotificationQuery;
import cornerstone.biz.domain.AccountNotificationSetting.AccountNotificationSettingInfo;
import cornerstone.biz.domain.AccountNotificationSetting.AccountNotificationSettingQuery;
import cornerstone.biz.domain.AccoutBugStat.AccoutBugStatQuery;
import cornerstone.biz.domain.Attachment.AttachmentInfo;
import cornerstone.biz.domain.Calendar;
import cornerstone.biz.domain.Attachment.AttachmentQuery;
import cornerstone.biz.domain.Calendar.CalendarInfo;
import cornerstone.biz.domain.Calendar.CalendarQuery;
import cornerstone.biz.domain.CalendarSchedule.CalendarScheduleInfo;
import cornerstone.biz.domain.CalendarSchedule.CalendarScheduleQuery;
import cornerstone.biz.domain.Category.CategoryQuery;
import cornerstone.biz.domain.ChangeLog.ChangeLogInfo;
import cornerstone.biz.domain.ChangeLog.ChangeLogQuery;
import cornerstone.biz.domain.CmdbApi.CmdbApiInfo;
import cornerstone.biz.domain.CmdbApi.CmdbApiQuery;
import cornerstone.biz.domain.CmdbApplication.CmdbApplicationInfo;
import cornerstone.biz.domain.CmdbApplication.CmdbApplicationQuery;
import cornerstone.biz.domain.CmdbInstance.CmdbInstanceInfo;
import cornerstone.biz.domain.CmdbInstance.CmdbInstanceQuery;
import cornerstone.biz.domain.CmdbMachine.CmdbMachineInfo;
import cornerstone.biz.domain.CmdbMachine.CmdbMachineQuery;
import cornerstone.biz.domain.CmdbRobot.CmdbRobotInfo;
import cornerstone.biz.domain.CmdbRobot.CmdbRobotQuery;
import cornerstone.biz.domain.Company.CompanyInfo;
import cornerstone.biz.domain.Company.CompanyQuery;
import cornerstone.biz.domain.CompanyMember.CompanyMemberInfo;
import cornerstone.biz.domain.CompanyMember.CompanyMemberQuery;
import cornerstone.biz.domain.CompanyMemberInvite.CompanyMemberInviteInfo;
import cornerstone.biz.domain.CompanyMemberInvite.CompanyMemberInviteQuery;
import cornerstone.biz.domain.CompanyMemberInviteCode.CompanyMemberInviteCodeInfo;
import cornerstone.biz.domain.CompanyMemberInviteCode.CompanyMemberInviteCodeQuery;
import cornerstone.biz.domain.CompanyMemberTodoTask.AccountTodoTask;
import cornerstone.biz.domain.CompanyRecycle.CompanyRecycleInfo;
import cornerstone.biz.domain.CompanyRecycle.CompanyRecycleQuery;
import cornerstone.biz.domain.CompanyTaskReport.CompanyTaskReportQuery;
import cornerstone.biz.domain.CompanyTaskReport.ReportTaskInfo;
import cornerstone.biz.domain.CompanyTaskReport.TaskAccount;
import cornerstone.biz.domain.CompanyVersion.CompanyVersionInfo;
import cornerstone.biz.domain.CompanyVersion.CompanyVersionQuery;
import cornerstone.biz.domain.CompanyVersionRepository.CompanyVersionRepositoryInfo;
import cornerstone.biz.domain.CompanyVersionRepository.CompanyVersionRepositoryQuery;
import cornerstone.biz.domain.CompanyVersionTask.CompanyVersionTaskInfo;
import cornerstone.biz.domain.CompanyVersionTask.CompanyVersionTaskQuery;
import cornerstone.biz.domain.Config.ConfigQuery;
import cornerstone.biz.domain.Dashboard.DashboardInfo;
import cornerstone.biz.domain.Dashboard.DashboardQuery;
import cornerstone.biz.domain.DashboardCard.DashboardCardInfo;
import cornerstone.biz.domain.DashboardCard.DashboardCardQuery;
import cornerstone.biz.domain.Department.DepartmentInfo;
import cornerstone.biz.domain.Department.DepartmentQuery;
import cornerstone.biz.domain.Discuss.DiscussInfo;
import cornerstone.biz.domain.Discuss.DiscussQuery;
import cornerstone.biz.domain.DiscussMessage.DiscussMessageInfo;
import cornerstone.biz.domain.File;
import cornerstone.biz.domain.DiscussMessage.DiscussMessageQuery;
import cornerstone.biz.domain.File.FileInfo;
import cornerstone.biz.domain.File.FileQuery;
import cornerstone.biz.domain.Filter.FilterInfo;
import cornerstone.biz.domain.Filter.FilterQuery;
import cornerstone.biz.domain.Machine.MachineInfo;
import cornerstone.biz.domain.Machine.MachineQuery;
import cornerstone.biz.domain.MachineLoginSession.MachineLoginSessionInfo;
import cornerstone.biz.domain.MindMapTree.Children;
import cornerstone.biz.domain.ObjectType.ObjectTypeInfo;
import cornerstone.biz.domain.ObjectType.ObjectTypeQuery;
import cornerstone.biz.domain.ObjectTypeFieldDefine.ObjectTypeFieldDefineQuery;
import cornerstone.biz.domain.OptLog.OptLogInfo;
import cornerstone.biz.domain.OptLog.OptLogQuery;
import cornerstone.biz.domain.Permission.PermissionQuery;
import cornerstone.biz.domain.Project.ProjectInfo;
import cornerstone.biz.domain.Project.ProjectQuery;
import cornerstone.biz.domain.ProjectArtifact.ProjectArtifactInfo;
import cornerstone.biz.domain.ProjectArtifact.ProjectArtifactQuery;
import cornerstone.biz.domain.ProjectDataPermission.ProjectDataPermissionInfo;
import cornerstone.biz.domain.ProjectFieldDefine.ProjectFieldDefineInfo;
import cornerstone.biz.domain.ProjectFieldDefine.ProjectFieldDefineQuery;
import cornerstone.biz.domain.ProjectIteration.ProjectIterationInfo;
import cornerstone.biz.domain.ProjectIteration.ProjectIterationQuery;
import cornerstone.biz.domain.ProjectIterationStep.ProjectIterationStepInfo;
import cornerstone.biz.domain.ProjectIterationStep.ProjectIterationStepQuery;
import cornerstone.biz.domain.ProjectMember.ProjectMemberInfo;
import cornerstone.biz.domain.ProjectMember.ProjectMemberQuery;
import cornerstone.biz.domain.ProjectMemberRole.ProjectMemberRoleInfo;
import cornerstone.biz.domain.ProjectMemberRole.ProjectMemberRoleQuery;
import cornerstone.biz.domain.ProjectModule.ProjectModuleInfo;
import cornerstone.biz.domain.ProjectModule.ProjectModuleQuery;
import cornerstone.biz.domain.ProjectObjectTypeTemplate.ProjectObjectTypeTemplateInfo;
import cornerstone.biz.domain.ProjectObjectTypeTemplate.ProjectObjectTypeTemplateQuery;
import cornerstone.biz.domain.ProjectPipeline.ProjectPipelineInfo;
import cornerstone.biz.domain.ProjectPipeline.ProjectPipelineQuery;
import cornerstone.biz.domain.ProjectPipelineRunDetailLog.ProjectPipelineRunDetailLogQuery;
import cornerstone.biz.domain.ProjectPipelineRunLog.ProjectPipelineRunLogInfo;
import cornerstone.biz.domain.ProjectPipelineRunLog.ProjectPipelineRunLogQuery;
import cornerstone.biz.domain.ProjectPriorityDefine.ProjectPriorityDefineInfo;
import cornerstone.biz.domain.ProjectRelease.ProjectReleaseInfo;
import cornerstone.biz.domain.ProjectRelease.ProjectReleaseQuery;
import cornerstone.biz.domain.ProjectRunLog.ProjectRunLogInfo;
import cornerstone.biz.domain.ProjectRunLog.ProjectRunLogQuery;
import cornerstone.biz.domain.ProjectStatusDefine.ProjectStatusDefineInfo;
import cornerstone.biz.domain.ProjectSubSystem.ProjectSubSystemInfo;
import cornerstone.biz.domain.ProjectSubSystem.ProjectSubSystemQuery;
import cornerstone.biz.domain.Remind.RemindInfo;
import cornerstone.biz.domain.Remind.RemindQuery;
import cornerstone.biz.domain.Report.ReportInfo;
import cornerstone.biz.domain.Report.ReportQuery;
import cornerstone.biz.domain.ReportContent.ReportContentQuery;
import cornerstone.biz.domain.ReportTemplate.ReportTemplateInfo;
import cornerstone.biz.domain.ReportTemplate.ReportTemplateQuery;
import cornerstone.biz.domain.Role.RoleInfo;
import cornerstone.biz.domain.Role.RoleQuery;
import cornerstone.biz.domain.SeniorMindMapTree.SeniorChildren;
import cornerstone.biz.domain.SystemNotification.SystemNotificationInfo;
import cornerstone.biz.domain.SystemNotification.SystemNotificationQuery;
import cornerstone.biz.domain.Task.TaskDetailInfo;
import cornerstone.biz.domain.Task.TaskDetailInfoQuery;
import cornerstone.biz.domain.Task.TaskInfo;
import cornerstone.biz.domain.Task.TaskQuery;
import cornerstone.biz.domain.TaskAssociated.TaskAssociatedQuery;
import cornerstone.biz.domain.TaskCalendarInfo.CalendarInfoQuery;
import cornerstone.biz.domain.TaskComment.TaskCommentInfo;
import cornerstone.biz.domain.TaskComment.TaskCommentQuery;
import cornerstone.biz.domain.TaskImportTemplate.TaskImportTemplateInfo;
import cornerstone.biz.domain.TaskImportTemplate.TaskImportTemplateQuery;
import cornerstone.biz.domain.TaskRemind.TaskRemindInfo;
import cornerstone.biz.domain.TaskStatDayData.TaskStatDayDataQuery;
import cornerstone.biz.domain.TaskStatusChangeLog.TaskStatusChangeLogQuery;
import cornerstone.biz.domain.TaskWorkTimeLog.TaskWorkTimeLogInfo;
import cornerstone.biz.domain.TaskWorkTimeLog.TaskWorkTimeLogQuery;
import cornerstone.biz.domain.TestPlanTestCase.TestPlanTestCaseQuery;
import cornerstone.biz.domain.TestPlanTestCaseRunLog.TestPlanTestCaseRunLogInfo;
import cornerstone.biz.domain.TestPlanTestCaseRunLog.TestPlanTestCaseRunLogQuery;
import cornerstone.biz.domain.Wiki.WikiInfo;
import cornerstone.biz.domain.Wiki.WikiQuery;
import cornerstone.biz.domain.WikiPage.WikiPageDetailInfo;
import cornerstone.biz.domain.WikiPage.WikiPageInfo;
import cornerstone.biz.domain.WikiPage.WikiPageQuery;
import cornerstone.biz.domain.WikiPageChangeLog.WikiPageChangeLogDetailInfo;
import cornerstone.biz.domain.WikiPageChangeLog.WikiPageChangeLogInfo;
import cornerstone.biz.domain.WikiPageChangeLog.WikiPageChangeLogQuery;
import cornerstone.biz.domain.file.WpsFileInfo;
import cornerstone.biz.domain.query.BizQuery;
import cornerstone.biz.util.HttpRollback;
import cornerstone.biz.domain.wx.WeixinAccount;
import cornerstone.biz.domain.wx.WeixinOAuthToken;
import cornerstone.biz.lucene.LuceneService;
import cornerstone.biz.lucene.SearchDocument;
import cornerstone.biz.lucene.SearchException;
import cornerstone.biz.pipeline.JavaScriptPipeline;
import cornerstone.biz.pipeline.PipelineDefine;
import cornerstone.biz.poi.ExcelCell;
import cornerstone.biz.poi.ExcelUtils;
import cornerstone.biz.poi.PoiUtils;
import cornerstone.biz.poi.TableData;
import cornerstone.biz.sftp.SftpChannel;
import cornerstone.biz.srv.*;
import cornerstone.biz.srv.LarkService.LarkUserAccessToken2;
import cornerstone.biz.srv.LarkService.LarkUserInfo2;
import cornerstone.biz.ssh.ConnectionInfo;
import cornerstone.biz.ssh.SshChannel;
import cornerstone.biz.taskjob.BizTaskJobs;
import cornerstone.biz.util.*;
import cornerstone.biz.util.example.ExampleTaskUtil;
import cornerstone.biz.websocket.WebEvent;
import jazmin.core.Jazmin;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;
import jazmin.driver.jdbc.ConnectionDriver.AutoTranscationCallback;
import jazmin.driver.jdbc.Transaction;
import jazmin.driver.jdbc.TransactionSynchronizationAdapter;
import jazmin.driver.jdbc.smartjdbc.Query;
import jazmin.driver.jdbc.smartjdbc.QueryWhere;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.rpc.RpcService;
import jazmin.util.IOUtil;
import jazmin.util.JSONUtil;
import jazmin.util.MD5Util;
import jazmin.util.RandomUtil;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ApiDefine("业务通用接口")
public interface BizAction {

    @ApiDefine(value = "查询配置列表", resp = "配置列表集合", params = {"登录TOKEN", "配置查询类"})
    List<Config> getConfigList(String token, ConfigQuery query);

    @ApiDefine(value = "修改配置", params = {"登录TOKEN", "配置类"})
    void updateConfig(String token, Config bean);

    @ApiDefine(value = "心跳", params = {"登录TOKEN"}, resp = "心跳信息")
    HeartbeatInfo heartbeat(String token);

    @ApiDefine(value = "等会通知", params = {"登录TOKEN", "通知ID集合"})
    void sendNotificationsLater(String token, List<Integer> ids);

    @ApiDefine(value = "我知道了", params = {"登录TOKEN", "通知ID集合"})
    void readNotificationList(String token, List<Integer> ids);

    @ApiDefine(value = "添加附件", params = {"登录TOKEN", "附件信息"}, resp = "附件信息")
    Attachment addAttachment(String token, Attachment bean);

    @ApiDefine(value = "查询附件 不校验token", params = {"登录TOKEN", "附件UUID"}, resp = "附件信息")
    Attachment queryAttachmentByUuid(String token, String uuid);

    @ApiDefine(value = "查询附件", params = {"登录TOKEN", "附件UUID"}, resp = "附件信息")
    Attachment getAttachmentByUuid(String token, String uuid);

    @ApiDefine(value = "添加任务附件", params = {"登录TOKEN", "附件UUID", "任务ID"})
    void addTaskAttachment(String token, String uuid, int taskId);

    @ApiDefine(value = "添加任务wiki", params = {"登录TOKEN", "附件UUID", "任务ID"})
    void addTaskWiki(String token, String uuid, int taskId);

    @ApiDefine(value = "删除任务附件", params = {"登录TOKEN", "任务ID", "附件ID", "wiki或文件"})
    void deleteTaskAttachment(String token, int taskId, int attachmentId, int type);

    @ApiDefine(value = "图形验证码", params = {"SIGN", "CODE"})
    void kaptcha(String sign, String code);

    @ApiDefine(value = "注册", params = {"注册信息"}, resp = "注册用户UUID")
    String register(RegisterInfo info);

    /**
     * 注册
     */
    String registerNew(RegisterInfo info);

    @ApiDefine(value = "激活账号", params = {"类型", "用户UUID"})
    void activateAccount(int type, String accountUuid);

    @ApiDefine(value = "确认激活账号", params = {"类型", "用户UUID", "激活码"})
    void confirmActivateAccount(int type, String accountUuid, String code);

    @ApiDefine(value = "登录", params = {"用户名", "密码"}, resp = "用户登录结果")
    LoginResult login(String userName, String password);

    @ApiDefine(value = "sso登录", resp = "sso登录结果")
    LoginResult ssoLogin(String rollbackInfo);

    /**
     * 手机验证码登录
     */
    LoginResult loginByMobileNo(String mobileNo, String code);


    @ApiDefine(value = "AD域账号登录", params = {"AD域用户名", "AD域密码"}, resp = "用户登录结果")
    LoginResult loginbyAdAccount(String userName, String password);

    @ApiDefine(value = "绑定AD域账号", params = {"AD域用户名", "AD域密码"})
    void bindAdAccount(String token, String userName, String password);

    @ApiDefine(value = "解除绑定AD域账号")
    void unbindAdAccount(String token);

    /***/
    String openapiLogin(String userName, String accessToken);

    @ApiDefine(value = "登出", params = {"用户登录TOKEN"})
    void logout(String token);

    @ApiDefine(value = "重置账号每日登陆失败次数", params = {"用户登录TOKEN", "用户ID"})
    void resetAccountDailyLoginFailCount(String token, int accountId);

    @ApiDefine(value = "切换公司", params = {"用户登录TOKEN", "公司UUID"})
    void switchCompany(String token, String companyUuid);

    @ApiDefine(value = "查询登录信息", params = {"用户登录TOKEN"}, resp = "登录用户信息")
    LoginInfo getLoginInfo(String token);

    @ApiDefine(value = "创建企业", params = {"用户登录TOKEN", "公司信息"})
    void createCompany(String token, Company bean);

    @ApiDefine(value = "编辑企业", params = {"用户登录TOKEN", "公司信息"})
    void updateCompany(String token, Company bean);

    @ApiDefine(value = "删除企业", params = {"用户登录TOKEN", "公司ID"})
    void deleteCompany(String token, int companyId);

    @ApiDefine(value = "更新企业许可证", params = {"用户登录TOKEN", "公司ID", "许可证信息"})
    void updateCompanyLicense(String token, int companyId, String license);

    @ApiDefine(value = "创建企业成员", params = {"用户登录TOKEN", "创建公司账号请求"})
    void createCompanyAccount(String token, CreateCompanyAccountReq req);

    /**
     * 从excel导入成员列表
     */
    void importCompanyAccountFromExcel(String token, String fileId);

    @ApiDefine(value = "通过uuid查询企业", params = {"用户登录TOKEN", "公司UUID"}, resp = "公司信息")
    CompanyInfo getCompanyInfoByUuid(String token, String uuid);

    @ApiDefine(value = "创建项目", params = {"用户登录TOKEN", "项目信息", "项目ID"}, resp = "项目UUID")
    String createProject(String token, Project bean, int templateProjectId);

    @ApiDefine(value = "创建项目", params = {"用户登录TOKEN", "项目信息", "勾选模块"}, resp = "项目UUID")
    String createTemplateProject(String token, Project bean, List<Integer> objectTypeIdList);

    @ApiDefine(value = "打开项目", params = {"用户登录TOKEN", "项目ID"})
    void openProject(String token, int id);

    @ApiDefine(value = "归档项目", params = {"用户登录TOKEN", "项目ID"})
    void archiveProject(String token, int id);

    @ApiDefine(value = "删除项目", params = {"用户登录TOKEN", "项目ID"})
    void deleteProject(String token, int id);

    @ApiDefine(value = "更新项目", params = {"用户登录TOKEN", "项目信息"})
    void updateProject(String token, Project bean);

    /***/
    void updateProjectWorkflowStatus(String token, int projectId, int workflowStatus);

    @ApiDefine(value = "通过uuid查询项目", params = {"用户登录TOKEN", "项目UUID"}, resp = "项目信息")
    ProjectInfo getProjectInfoByUuid(String token, String uuid);

    @ApiDefine(value = "项目展现内容", params = {"用户登录TOKEN", "项目ID"}, resp = "项目集合列表")
    Map<String, Object> getProjectShowInfo(String token, int projectId);

    @ApiDefine(value = "查询项目列表和总数", params = {"用户登录TOKEN", "项目查询类"}, resp = "项目集合列表")
    Map<String, Object> getProjectList(String token, ProjectQuery query);

    @ApiDefine(value = "查询模板项目列表和总数", params = {"用户登录TOKEN", "项目查询类"}, resp = "项目集合")
    Map<String, Object> getTemplateProjectList(String token, ProjectQuery query);

    @ApiDefine(value = "查询模板项目列表", params = {"用户登录TOKEN"}, resp = "项目列表")
    List<ProjectInfo> getTemplateProjectInfoList(String token);

    @ApiDefine(value = "查询所有项目列表和总数", params = {"用户登录TOKEN", "项目查询类"}, resp = "项目列表")
    Map<String, Object> getAllProjectList(String token, ProjectQuery query);

    @ApiDefine(value = "查询所有运行中的项目列表和总数", params = {"用户登录TOKEN", "项目查询类"}, resp = "项目列表")
    Map<String, Object> getAllRunningProjectList(String token, ProjectQuery query);

    @ApiDefine(value = "查询我能看到的项目列表", params = {"用户登录TOKEN"}, resp = "项目列表")
    List<ProjectInfo> getMyProjectList(String token);

    @ApiDefine(value = "设置星标项目", params = {"用户登录TOKEN", "项目ID"})
    void setStarProject(String token, int projectId);

    @ApiDefine(value = "取消星标项目", params = {"用户登录TOKEN", "项目ID"})
    void cancelStarProject(String token, int projectId);

    @ApiDefine(value = "查询项目自定义字段", params = {"用户登录TOKEN", "项目ID", "类型"}, resp = "自定义字段列表")
    List<ProjectFieldDefineInfo> getProjectFieldDefineInfoList(String token, int project, int objectType);

    @ApiDefine(value = "新增项目自定义字段", params = {"用户登录TOKEN", "自定义字段"})
    void addProjectFieldDefine(String token, ProjectFieldDefine bean);

    @ApiDefine(value = "编辑项目自定义字段", params = {"用户登录TOKEN", "自定义字段"})
    void updateProjectFieldDefine(String token, ProjectFieldDefine bean);

    @ApiDefine(value = "删除项目自定义字段", params = {"用户登录TOKEN", "自定义字段ID"})
    void deleteProjectFieldDefine(String token, int id);

    @ApiDefine(value = "保存项目显示字段列表", params = {"用户登录TOKEN", "自定义字段集合"})
    void saveProjectFieldDefineList(String token, List<ProjectFieldDefine> list);

    /**
     * 设置是否必选
     */
    void setProjectFieldDefineIsRequired(String token, int id, boolean isRequired);

    @ApiDefine(value = "查询状态工作流", params = {"用户登录TOKEN", "项目ID", "类型"}, resp = "工作流集合")
    List<ProjectStatusDefineInfo> getProjectStatusDefineInfoList(String token, int projectId, int objectType);

    @ApiDefine(value = "增加状态工作流", params = {"用户登录TOKEN", "项目工作流状态"}, resp = "状态工作流ID")
    int addProjectStatusDefine(String token, ProjectStatusDefine bean);

    @ApiDefine(value = "编辑状态工作流", params = {"用户登录TOKEN", "项目工作流状态"})
    void updateProjectStatusDefine(String token, ProjectStatusDefine bean);

    @ApiDefine(value = "删除状态工作流", params = {"用户登录TOKEN", "项目工作流状态ID"})
    void deleteProjectStatusDefine(String token, int id);

    @ApiDefine(value = "更新状态工作流", params = {"用户登录TOKEN", "项目工作流状态集合"})
    void saveProjectStatusDefineList(String token, List<ProjectStatusDefine> list);

    @ApiDefine(value = "查询优先级定义列表", params = {"用户登录TOKEN", "项目ID", "类型"}, resp = "优先级定义列表")
    List<ProjectPriorityDefineInfo> getProjectPriorityDefineInfoList(String token, int projectId, int objectType);

    @ApiDefine(value = "增加优先级", params = {"用户登录TOKEN", "优先级"}, resp = "优先级ID")
    int addProjectPriorityDefine(String token, ProjectPriorityDefine bean);

    @ApiDefine(value = "编辑优先级", params = {"用户登录TOKEN", "优先级"})
    void updateProjectPriorityDefine(String token, ProjectPriorityDefine bean);

    @ApiDefine(value = "删除优先级", params = {"用户登录TOKEN", "优先级ID"})
    void deleteProjectPriorityDefine(String token, int id);

    @ApiDefine(value = "更新优先级顺序", params = {"用户登录TOKEN", "项目ID", "类型", "优先级ID集合"})
    void updateProjectPrioritySortWeight(String token, int projectId, int objectType, List<Integer> idList);

    @ApiDefine(value = "查询启用的项目功能列表", params = {"用户登录TOKEN", "项目ID"}, resp = "项目功能列表")
    List<ProjectModuleInfo> getProjectModuleInfoList(String token, int projectId);

    @ApiDefine(value = "查询所有项目功能列表", params = {"用户登录TOKEN", "项目ID"}, resp = "项目功能列表")
    List<ProjectModuleInfo> getAllProjectModuleInfoList(String token, int projectId);

    @ApiDefine(value = "查询所有项目功能列表", params = {"用户登录TOKEN"}, resp = "项目功能列表")
    List<ProjectModuleInfo> getCompanyProjectModuleInfoList(String token);

    @ApiDefine(value = "更新项目对象类型可见性信息", params = {"用户登录TOKEN", "项目功能"})
    void updateProjectModulePublicInfo(String token, ProjectModule bean);

    @ApiDefine(value = "保存项目功能列表", params = {"用户登录TOKEN", "项目功能列表"})
    void saveProjectModuleList(String token, List<ProjectModule> list);

    @ApiDefine(value = "更新项目模块列表", params = {"用户登录TOKEN", "项目ID", "项目功能列表"})
    void updateProjectModuleList(String token, int projectId, List<ProjectModule> list);

    @ApiDefine(value = "新增组织架构", params = {"用户登录TOKEN", "组织架构"}, resp = "组织架构ID")
    int createDepartment(String token, DepartmentInfo bean);

    /**
     * 导入组织架构
     */
    void importDepartmentFromExcel(String token, String fileId);

    @ApiDefine(value = "编辑组织架构", params = {"用户登录TOKEN", "组织架构"})
    void updateDepartment(String token, DepartmentInfo bean);

    @ApiDefine(value = "删除组织架构", params = {"用户登录TOKEN", "组织架构ID"})
    void deleteDepartment(String token, int id);

    @ApiDefine(value = "查询组织架构树", params = {"用户登录TOKEN", "是否需要用户"}, resp = "组织架构树集合")
    List<DepartmentTree> getDepartmentTree(String token, Boolean needAccount);

    @ApiDefine(value = "查询部门列表", params = {"用户登录TOKEN"}, resp = "查询部门")
    List<DepartmentInfo> getDepartmentInfoList(String token);

    @ApiDefine(value = "查询部门详情", params = {"用户登录TOKEN", "部门ID"}, resp = "查询部门详情")
    DepartmentInfo getDepartmentInfo(String token, int id);

    @ApiDefine(value = "查询用户列表", params = {"用户登录TOKEN", "用户查询类"}, resp = "用户列表")
    List<AccountInfo> getAccountInfoList(String token, AccountQuery query);

    @ApiDefine(value = "查询用户列表", params = {"用户登录TOKEN", "公司人员查询类"}, resp = "用户列表")
    Map<String, Object> getCompanyMemberList(String token, CompanyMemberQuery query);

    @ApiDefine(value = "查询权限列表", params = {"用户登录TOKEN", "类型"}, resp = "权限树集合")
    List<PermissionTree> getPermissionList(String token, int type);

    @ApiDefine(value = "增加角色", params = {"用户登录TOKEN", "角色类"}, resp = "角色ID")
    int addRole(String token, Role bean);

    @ApiDefine(value = "编辑角色", params = {"用户登录TOKEN", "角色类"})
    void updateRole(String token, Role bean);

    @ApiDefine(value = "删除角色", params = {"用户登录TOKEN", "角色类ID"})
    void deleteRole(String token, int id);

    @ApiDefine(value = "查询角色列表", params = {"用户登录TOKEN", "类型"}, resp = "角色列表")
    List<RoleInfo> getRoleInfoList(String token, int type);

    @ApiDefine(value = "查询单个角色信息", params = {"用户登录TOKEN", "角色ID"}, resp = "角色信息")
    RoleInfo getRoleInfoById(String token, int id);

    @ApiDefine(value = "保存角色列表", params = {"用户登录TOKEN", "角色列表"})
    void saveRoleInfoList(String token, List<Role> list);

    @ApiDefine(value = "查询数据权限列表", params = {"用户登录TOKEN", "项目ID", "类型"}, resp = "查询数据权限列表")
    List<ProjectDataPermissionInfo> getProjectDataPermissionInfoList(String token, int projectId, int objectType);

    @ApiDefine(value = "保存数据权限列表", params = {"用户登录TOKEN", "查询数据权限列表"})
    void saveProjectDataPermissionInfoList(String token, List<ProjectDataPermission> list);

    @ApiDefine(value = "添加项目成员列表", params = {"用户登录TOKEN", "项目ID", "用户ID集合", "角色ID集合"})
    void addProjectMembers(String token, int projectId, Set<Integer> accountIdList, Set<Integer> roleIds, List<String> tags);

    /**
     * 成员批量加入项目
     */
    void memberJoinProjects(String token, int accountId, Set<Integer> projectIdList, Set<Integer> roleIds);

    @ApiDefine(value = "加入项目成员", params = {"用户登录TOKEN", "邀请码"})
    void joinProjectMember(String token, String inviteCode);

    @ApiDefine(value = "编辑项目成员信息", params = {"用户登录TOKEN", "项目ID", "角色ID集合"})
    void updateProjectMember(String token, int id, Set<Integer> roleIds, List<String> tag, boolean updateTags);

    @ApiDefine(value = "删除项目成员", params = {"用户登录TOKEN", "项目ID"})
    void deleteProjectMember(String token, int id);

    /**
     * 查询项目成员tag
     */
    List<String> getProjectMemberTags(String token, int projectId);

    @ApiDefine(value = "查询项目成员列表", params = {"用户登录TOKEN", "项目ID"}, resp = "项目成员信息列表")
    List<ProjectMemberInfo> getProjectMemberInfoList(String token, int projectId);

    @ApiDefine(value = "创建迭代", params = {"用户登录TOKEN", "项目迭代信息"}, resp = "迭代ID")
    int createProjectIteration(String token, ProjectIterationInfo bean);

    @ApiDefine(value = "编辑迭代", params = {"用户登录TOKEN", "项目迭代信息"})
    void updateProjectIteration(String token, ProjectIterationInfo bean);

    @ApiDefine(value = "删除迭代", params = {"用户登录TOKEN", "项目迭代信息ID"})
    void deleteProjectIteration(String token, int id);

    @ApiDefine(value = "查询迭代", params = {"用户登录TOKEN", "项目迭代信息ID"}, resp = "迭代信息")
    ProjectIterationInfo getProjectIterationInfoById(String token, int id);

    @ApiDefine(value = "查询项目下所有迭代", params = {"用户登录TOKEN", "项目ID"}, resp = "迭代信息集合")
    List<ProjectIterationInfo> getProjectIterationInfoList(String token, int projectId);

    @ApiDefine(value = "查询项目迭代列表", params = {"用户登录TOKEN", "迭代信息查询类"}, resp = "迭代信息列表")
    Map<String, Object> getProjectIterationList(String token, ProjectIterationQuery query);

    @ApiDefine(value = "查询Release列表", params = {"用户登录TOKEN", "迭代信息查询类"}, resp = "Release列表")
    Map<String, Object> getProjectReleaseList(String token, ProjectReleaseQuery query);

    @ApiDefine(value = "创建Release", params = {"用户登录TOKEN", "Release类"}, resp = "Release ID")
    int createProjectRelease(String token, ProjectRelease bean);

    @ApiDefine(value = "更新Release", params = {"用户登录TOKEN", "Release类"})
    void updateProjectRelease(String token, ProjectRelease bean);

    @ApiDefine(value = "删除Release", params = {"用户登录TOKEN", "Release类ID"})
    void deleteProjectRelease(String token, int id);

    @ApiDefine(value = "查询Release", params = {"用户登录TOKEN", "Release类ID"}, resp = "项目Release信息")
    ProjectReleaseInfo getProjectReleaseInfo(String token, int id);

    @ApiDefine(value = "获取项目子系统列表", params = {"用户登录TOKEN", "项目子系统查询类"}, resp = "项目子系统列表")
    Map<String, Object> getProjectSubSystemList(String token, ProjectSubSystemQuery query);

    @ApiDefine(value = "创建子系统", params = {"用户登录TOKEN", "项目子系统"}, resp = "项目子系统ID")
    int createProjectSubSystem(String token, ProjectSubSystem bean);

    @ApiDefine(value = "更新子系统", params = {"用户登录TOKEN", "项目子系统"})
    void updateProjectSubSystem(String token, ProjectSubSystem bean);

    @ApiDefine(value = "删除子系统", params = {"用户登录TOKEN", "项目子系统"})
    void deleteProjectSubSystem(String token, int id);

    @ApiDefine(value = "查询子系统", params = {"用户登录TOKEN", "项目子系统"})
    ProjectSubSystemInfo getProjectSubSystemInfo(String token, int id);

    @ApiDefine(value = "任务查询信息", params = {"用户登录TOKEN", "项目UUID", "类型"}, resp = "任务查询信息")
    TaskQueryInfo getTaskQueryInfo(String token, String projectUuid, int objectType);

    @ApiDefine(value = "创建或编辑任务信息", params = {"用户登录TOKEN", "项目UUID", "类型"}, resp = "创建或编辑任务信息")
    TaskEditInfo getEditTaskInfo(String token, int projectId, int objectType);


    @ApiDefine(value = "任务过滤器信息", params = {"用户登录TOKEN", "项目ID", "类型"}, resp = "任务过滤器信息")
    FilterEditTaskInfo getFilterEditTaskInfo(String token, int projectId, int objectType);

    /**
     * 创建任务
     */
    int createTask(String token, TaskDetailInfo bean);

    /**
     * 创建wiki任务(根据脑图创建任务)
     */
    void createWikiTask(String token, TaskDetailInfo bean, int wikiPageId, String nodeId, boolean onlyLeafNode);

    /**
     * 创建wiki任务(根据高级脑图创建任务)
     */
    void createSeniorWikiTask(String token, TaskDetailInfo bean, int wikiPageId, String nodeId, boolean onlyLeafNode);

    /**
     * 更新脑图
     */
    WikiPageInfo updateWikiPageByTasks(String token, int wikiPageId);

    /**
     * 更新高级脑图
     */
    WikiPageInfo updateSeniorWikiPageByTasks(String token, int wikiPageId);

    /**
     * 新增bug
     */
    void addBugFromTestPlanTestCase(String token, int testPlanTestCaseId, int bugId);

    /**
     * 从Excel导入(批量新增任务)
     */
    int batchAddTaskList(String token, List<TaskDetailInfo> list);

    /**
     * 编辑任务
     */
    void updateTask(String token, TaskDetailInfo bean, List<String> updateFields, boolean isManualUpdate);

    /**
     * 批量编辑任务
     */
    void batchUpdateTask(String token, BatchUpdateTaskInfo bean, Set<Integer> ids, List<String> updateFields);

    /**
     * 批量复制任务
     */
    List<TaskBriefInfo> batchCopyTask(String token, Set<Integer> ids, int project, Integer iterationId,
                                      Integer objectType, boolean deleteOriginal,
                                      Boolean copyComments, Boolean copyChangeLogs, Boolean copyAssociatedTasks,
                                      Boolean copyPubSubTasks);

    /**
     * 删除任务
     */
    void deleteTask(String token, int id);

    /**
     * 批量删除任务
     */
    void batchDeleteTaskList(String token, Set<Integer> ids);

    /**
     * 查询任务列表
     */
    List<Document> searchDocumentList(String token, String key);

    /**
     * 查询任务
     */
    Map<String, Object> getTaskInfoByUuid(String token, String uuid);

    /***/
    TaskSimpleInfo getTaskSimpleInfoByUuid(String token, String uuid);

    /**
     * 查询任务
     */
    Map<String, Object> getTaskInfoByUuidForOpenApi(String token, String uuid);

    /**
     * 生成pdf
     */
    TaskPdfInfo getTaskPdfInfoByUuid(String token, String uuid);

    /**
     * 查询任务列表
     */
    Map<String, Object> getTaskInfoList(String token, TaskQuery query);

    /**
     * 查询子任务列表
     */
    List<TaskInfo> getSubTaskInfoList(String token, String taskUuid, TaskQuery query);

    /**
     * 查询任务列表 并且把关联任务也返回
     */
    Map<String, Object> getTaskInfoListAndAssociateTasks(String token, TaskQuery query);

    /**
     * 我的待办任务列表
     */
    List<TaskInfo> getTodoTaskInfoList(String token);

    /**
     * 报表-查询成员任务列表
     */
    List<TaskInfo> getAccountsTaskList(String token, int type, List<Integer> accountIdList);

    /**
     * 我创建的未完成的任务列表
     */
    List<TaskInfo> getMyCreateTaskInfoList(String token);

    /**
     * 查询任务统计页信息
     */
    TaskStat getTaskStat(String token, TaskQuery query);

    /**
     * 查询任务周统计页信息 type 0为周 1为月
     */
    TaskWeekInfo getTaskWeekInfo(String token, TaskQuery query, int next, Integer type);
    //

    /**
     * 创建过滤器
     */
    int createFilter(String token, Filter bean);

    /**
     * 更新过滤器
     */
    void updateFilter(String token, Filter bean);

    /**
     * 删除过滤器
     */
    void deleteFilter(String token, int id);

    /**
     * 查询过滤器
     */
    FilterInfo getFilterInfoById(String token, int id);

    /**
     * 查询过滤器列表
     */
    List<FilterInfo> getFilterInfoList(String token, FilterQuery query);
    //

    /**
     * 添加关联对象
     */
    void addTaskAssociatedList(String token, int taskId, int type, List<Integer> associatedTaskIds);

    /**
     * 批量添加关联对象
     */
    void batchAddTaskAssociatedList(String token, Set<Integer> fromIds, Set<Integer> toIds);

    /**
     * 批量添加到父对象
     */
    void batchAddToParentTask(String token, Set<Integer> fromIds, Set<Integer> toIds);

    /**
     * 解除父子关系
     */
    void dismissPubSubTask(String token, int subTaskId);

    /**
     * 解除关联对象
     */
    void deleteTaskAssociated(String token, int id);

    /**
     * 新增任务评论
     */
    int addTaskComment(String token, TaskComment bean);

    /**
     * 删除任务评论
     */
    void deleteTaskComment(String token, int id);

    /**
     * 查询任务评论列表
     */
    List<TaskCommentInfo> getTaskCommentInfoList(String token, TaskCommentQuery query);

    /**
     * 查询变更记录列表 项目动态
     */
    List<ChangeLogInfo> getChangeLogList(String token, ChangeLogQuery query);

    /**
     * 查询变更记录列表
     */
    Map<String, Object> getAllChangeLogList(String token, ChangeLogQuery query);

    /**
     * 查询项目变更记录列表
     */
    Map<String, Object> getAllProjectChangeLogList(String token, ChangeLogQuery query);

    /**
     * 查询富文本变更内容
     */
    ChangeLogDiff getChangeLogDiffById(String token, int id);

    //

    /**
     * 通过ID查询机器
     */
    MachineInfo getMachineById(String token, int id);

    /**
     * 查询机器列表
     */
    List<MachineInfo> getMachineInfoList(String token, MachineQuery query);

    /**
     * 新增机器
     */
    int createMachine(String token, MachineInfo bean);

    /**
     * 通过cmdb machine新增主机
     */
    void createMachineListFromCmdbMachine(String token, int cmdbMachineId, List<Integer> projectIdList);

    /**
     * 编辑机器
     */
    void updateMachine(String token, MachineInfo bean);

    /**
     * 删除机器
     */
    void deleteMachine(String token, int id);

    /**
     * 上传文件到机器
     */
    void uploadFileToMachine(String token, String machineToken, String uuid, String remoteFilePath);

    /**
     * 登录主机
     */
    LoginMachineInfo loginMachine(String token, int machineId);

    /**
     * 登录CMDB主机
     */
    LoginMachineInfo loginCmdbMachine(String token, int cmdbMachineId, int cmdbRobotId);

    /**
     * 分享登录session
     */
    LoginMachineInfo shareLoginMachine(String token, String machineLoginToken, int type);

    /**
     * 通过分享的token获取信息
     */
    LoginMachineInfo getLoginMachineInfoByToken(String token, String machineLoginToken);

    /**
     * 查询登录机器的用户列表
     */
    List<AccountSimpleInfo> getLoginMachineAccountListByToken(String token, String machineLoginToken);

    //账号相关

    /**
     * 查询登录账号
     */
    AccountInfo getAccountInfo(String token);

    /**
     * 查询账号在某企业下的信息
     */
    AccountCompanyInfo getAccountCompanyInfo(String token, int accountId);

    /**
     * 从企业中移除
     */
    void deleteCompanyMember(String token, int accountId);

    /**
     * 从所有项目中移除
     */
    void deleteCompanyMemberProject(String token, int accountId);

    /**
     * 更新企业账号角色部门
     */
    void updateCompanyAccount(String token, int accountId, AccountCompanyInfo account);

    /**
     * 修改账号信息
     */
    void updateAccountInfo(String token, Account bean);

    /**
     * 禁用账号(仅限私有部署公司)
     */
    void disableAccount(String token, int accountId);

    /**
     * 启用账号(仅限私有部署公司)
     */
    void enableAccount(String token, int accountId, int type);

    /**
     * 管理员重置账号密码(仅限私有部署公司)
     */
    String resetAccountPassword(String token, int accountId);

    /**
     * 修改密码
     */
    void updatePassword(String token, String oldPassword, String newPassword);

    /**
     * 重置密码
     */
    void resetPassword(String mobileNo, String newPassword, String verifyCode);

    /**
     * 绑定新手机号
     */
    void bindNewMobile(String token, String newMobileNo, String newVerifyCode);

    /**
     * 取消绑定微信
     */
    void unbindWeixin(String token);

    /**
     * 取消绑定邮箱
     */
    void unbindEmail(String token);

    /**
     * 发送邮件验证码
     */
    void sendEmailVeryCode(String token, String email);

    /**
     * 发送手机验证码
     */
    void sendMobileVerifyCode(String token, String mobileNo);

    /**
     * 发送手机验证码
     */
    void sendMobileCode(String mobileNo, String ip, String sign, String kaptchaCode);

    /**
     * 发送邀请手机验证码
     */
    void sendInviteMobileCode(String token, String mobileNo);

    /**
     * 发送登录手机验证码
     */
    void sendLoginMobileCode(String mobileNo,  String sign, String kaptchaCode);

    /**
     * 重置密码 发送手机验证码
     */
    void sendMobileVerifyCodeForResetPassword(String mobileNo, String sign, String kaptchaCode);

    /**
     * 绑定邮箱
     */
    void bindEmail(String token, String email);

    /**
     * 确认绑定邮箱
     */
    void confirmBindEmail(String sign);
    //

    /**
     * 添加目录
     */
    void addDirectory(String token, File bean);

    /**
     * 编辑目录
     */
    void updateDirectory(String token, File bean);

    /**
     * 查询文件
     */
    FileInfo getFileInfoById(String token, int id);

    /**
     * 添加文件
     */
    int addFile(String token, int projectId, String attachmentUuid, Integer parentId);

    /**
     * 更新文件
     */
    void updateFile(String token, int id, String newAttachmentUuid);

    /**
     * 查询文件
     */
    File getFileByUuid(String token, String uuid);

    /**
     * 查询文件列表
     */
    Map<String, Object> getFiles(String token, FileQuery query);

    /**
     * 删除文件
     */
    void deleteFileById(String token, int id);

    /**
     * 删除文件
     */
    void deleteFiles(String token, List<Integer> ids);

    /**
     * 查询目录树
     */
    List<DirectoryNode> getDirectoryNode(String token, int projectId);

    /**
     * 移动文件或目录
     */
    void moveFiles(String token, int projectId, Set<Integer> files, Integer parentId);

    /**
     * 创建分类
     */
    int createCategory(String token, Category bean);

    /**
     * 编辑分类
     */
    void updateCategory(String token, Category bean);

    /**
     * 批量更新分类权重
     */
    void updateCategorySortWeightAndRelation(String token, List<CategoryNode> nodes);

    /**
     * 删除分类
     */
    void deleteCategory(String token, int id);

    /**
     * 分类从一个对象类型复制到另外一个对象类型
     */
    void batchCopyCategories(String token, int projectId, int srcObjectType, int dstProjectId, int dstObjectType);

    /**
     * 查询分类列表
     */
    List<CategoryNode> getCategoryNodeList(String token, int projectId, int objectType);

    /**
     * 新增工时记录
     */
    int addTaskWorkTimeLog(String token, TaskWorkTimeLog bean);

    /**
     * 编辑工时记录
     */
    void updateTaskWorkTimeLog(String token, TaskWorkTimeLog bean);

    /**
     * 删除工时记录
     */
    void deleteTaskWorkTimeLog(String token, int id);

    /**
     * 获取指定日期的工时
     */
    int getTaskWorkHours(String token, Long date);

    /**
     * 查询工时列表
     */
    Map<String, Object> getTaskWorkTimeLogList(String token, TaskWorkTimeLogQuery query);

    /**
     * 报表-工时统计-查询工时列表
     */
    Map<String, Object> getAllTaskWorkTimeLogList(String token, TaskWorkTimeLogQuery query);

    /**
     * 报表-项目工时统计
     */
    Map<String, Object> getProjectWorkTimeLogList(String token, TaskWorkTimeLogQuery query);

    /**
     * 导出项目工时统计到excel里
     */
    List<TaskWorkTimeLogInfo4Excel> exportProjectWorkTimeLogList(String token, TaskWorkTimeLogQuery query);

    /**
     * 创建pipeline
     */
    int createProjectPipeline(String token, ProjectPipeline bean);

    /**
     * 编辑pipeline
     */
    void updateProjectPipeline(String token, ProjectPipeline bean);

    /**
     * 删除pipeline
     */
    void deleteProjectPipeline(String token, int id);

    /**
     * 查询pipeline列表
     */
    List<ProjectPipelineInfo> getProjectPipelineInfoList(String token, ProjectPipelineQuery query);

    /**
     * 查询pipeline
     */
    ProjectPipelineInfo getProjectPipelineInfoById(String token, int id);

    /**
     * 运行pipeline
     */
    int runProjectPipeline(String token, int id);

    /**
     * 停止运行pipeline
     */
    void stopProjectPipeline(String token, int id);

    /***/
    ProjectPipelineRunInfo getProjectPipelineRunInfoById(String token, int id);

    /**
     * 查询pipeline运行详情,包含startDetailId
     */
    List<ProjectPipelineRunDetailLog> getProjectPipelineRunDetailLogList(String token, int runLogId, int startDetailId);

    /**
     * 查询pipeline列表
     */
    Map<String, Object> getProjectPipelineRunLogInfoList(String token, ProjectPipelineRunLogQuery query);

    /**
     * 查询pipeline runlog
     */
    ProjectPipelineRunLogInfo getProjectPipelineRunLogInfoById(String token, int id);

    /**
     * 设置参数
     */
    void setProjectPipelineRunLogParameter(String token, int id, ParameterValue value);

    /**
     * 测试计划新增测试用例
     */
    void addTestPlanTestCaseList(String token, int testPlanId, Set<Integer> testCaseIds);

    /**
     * 删除测试计划测试用例
     */
    void deleteTestPlanTestCase(String token, int id);

    /**
     * 读取Excel文件
     */
    TableData getTableDataFromExcel(String token, String fileUuid);

    /**
     * 从excel导入task
     */
    void importTaskFromExcel(String token, int projectId, int objectType,
                             List<Integer> idList, String fileUuid, Boolean ignoreError);

    /**
     * 导出task到excel里
     */
    ExportData exportTaskListToExcel(String token, TaskQuery query, List<Integer> fields);

    /**
     * 下载任务导入模板
     */
    TableData downloadImportTaskExcelTemplate(String token, int projectId, int objectType);

    /**
     * 执行测试用例
     */
    void runTestPlanTestCase(String token, int id, int status, String remark);

    /**
     * 批量执行测试用例
     */
    void batchRunTestPlanTestCase(String token, List<Integer> idList, int status);

    /**
     * 查询我的日历
     */
    TaskCalendarInfo getMyCalendarInfo(String token, CalendarInfoQuery query);

    /***/
    String getICalendar(String calUuid);

    /**
     * 查询提醒
     */
    RemindInfo getRemindInfoById(String token, int id);

    /**
     * 新增提醒
     */
    int addRemind(String token, Remind bean);

    /**
     * 编辑提醒
     */
    void updateRemind(String token, Remind bean);

    /**
     * 编辑提醒时间
     */
    void updateRemindTime(String token, int id, Date remindTime);

    /**
     * 删除提醒
     */
    void deleteRemind(String token, int id);

    //

    /**
     * 查询测试用例关联的bug列表
     */
    List<TaskInfo> getBugListFromTestCaseId(String token, int testCaseId);

    /**
     * 查询用例执行记录
     */
    List<TestPlanTestCaseRunLogInfo> getTestPlanTestCaseRunLogInfoList(String token, int testPlanTestCaseId);

    /***/
    Map<String, Object> getTestPlanTestCaseRunLogList(String token, TestPlanTestCaseRunLogQuery query);

    /**
     * 创建任务导入方案
     */
    int createTaskImportTemplate(String token, TaskImportTemplate bean);

    /**
     * 编辑任务导入方案
     */
    void updateTaskImportTemplate(String token, TaskImportTemplate bean);

    /**
     * 删除任务导入方案
     */
    void deleteTaskImportTemplate(String token, int id);

    /**
     * 通过ID查询任务导入模板
     */
    TaskImportTemplateInfo getTaskImportTemplateById(String token, int id);

    /**
     * 查询任务导入模板列表和总数
     */
    Map<String, Object> getTaskImportTemplateList(String token, TaskImportTemplateQuery query);

    //

    /**
     * 通过ID查询Wiki
     */
    WikiInfo getWikiInfoById(String token, int id);

    /**
     * 通过uuid查询wikiPage
     */
    WikiPageDetailInfo getWikiPageDetailInfoByUuid(String token, String uuid);

    /**
     * 查询Wiki列表和总数
     */
    List<WikiInfo> getWikiInfoList(String token, WikiQuery query);

    /**
     * 新增Wiki
     */
    int addWiki(String token, WikiInfo bean);

    /**
     * 编辑Wiki
     */
    void updateWiki(String token, WikiInfo bean);

    /**
     * 删除Wiki
     */
    void deleteWiki(String token, int id);
    //

    /**
     * 通过ID查询wiki页面
     */
    WikiPageDetailInfo getWikiPageById(String token, int id);

    /**
     * 全局wiki详情
     */
    WikiPageDetailInfo getCompanyWikiPageById(String token, int id);

    int addCompanyWikiPage(String token, WikiPageDetailInfo bean);

    void updateCompanyWikiPage(String token, WikiPageDetailInfo bean);

    void deleteCompanyWikiPage(String token, int id);

    /**
     * 全局wiki查询
     */
    Map<String, Object> getCompanyWikiPageList(String token, WikiPageQuery query);


    /**
     * 通过ID查询wiki页面 分享用到
     */
    WikiPageDetailInfo getWikiPageByUuid(String uuid);

    /**
     * 查询最后一个草稿wiki页面
     */
    int getLatestDraftWikiPage(String token, int originalId);

    /**
     * 查询wiki页面列表和总数
     */
    Map<String, Object> getWikiPageList(String token, int wikiId);


    List<WikiPageChangeLogInfo> getCompanyWikiPageChangeLogInfoList(String token, int id);

    WikiPageChangeLogDetailInfo getCompanyWikiPageChangeLogInfoById(String token, int id);

    /**
     * 新增wiki页面
     */
    int addWikiPage(String token, WikiPageDetailInfo bean);

    /**
     * 编辑wiki页面
     */
    void updateWikiPage(String token, WikiPageDetailInfo bean);

    /**
     * 删除wiki页面
     */
    void deleteWikiPage(String token, int id, String reason);

    /**
     * 发布wikiPage
     */
    void releaseWikiPage(String token, int id);

    /**
     * 回滚wikiPage
     */
    int revertWikiPage(String token, int logId);

    @Transaction
    void releaseCompanyWikiPage(String token, int id);

    @Transaction
    int revertCompanyWikiPage(String token, int logId);

    /**
     * 查询wiki变更记录列表
     */
    List<WikiPageChangeLogInfo> getWikiPageChangeLogInfoList(String token, int id);

    /**
     * 查询wiki变更记录
     */
    WikiPageChangeLogDetailInfo getWikiPageChangeLogInfoById(String token, int id);

    /**
     * 更新wiki权重和父子关系
     */
    void updateWikiPageSortWeightAndRelation(String token, List<WikiPageTree> tree);

    //统计相关

    /**
     * 查询迭代的燃尽图
     *
     * @param type       统计类型 任务数量、任务工作量
     * @param objectType 对象类型
     */
    List<IterationBurnDownData> getIterationBurnDownChart(String token, int iterationId, int type, int objectType);

    /**
     * 查询迭代完成率和延迟率
     */
    List<IterationFinishDelayRate> getIterationFinishDelayRate(String token, int iterationId);

    /**
     * 查询项目完成率和延迟率
     */
    List<ProjectFinishDelayRate> getProjectFinishDelayRate(String token, int projectId);

    /**
     * 查询每日新增task数量
     */
    List<TaskStatDayData> getTaskCreateDayDataList(String token, TaskStatDayDataQuery query);

    /**
     * 查询任务当前状态分布
     */
    List<TaskStatDayData> getTaskCurrStatusDistributeList(String token, TaskStatDayDataQuery query);

    /**
     * 查询任务每日完成情况
     */
    List<TaskStatDayData> getTaskFinishList(String token, TaskStatDayDataQuery query);

    /**
     * 查询任务累积数量
     */
    List<TaskStatDayData> getTaskTotalNumList(String token, TaskStatDayDataQuery query);

    /**
     * 根据迭代查询版本库提交统计
     */
    ScmCommitStatInfo getScmCommitStatInfo(String token, int iterationId);

    /**
     * 根据项目查询版本库提交统计
     */
    ScmCommitStatInfo getScmCommitStatInfoByProjectId(String token, int projectId);

    /**
     * 报表-查询迭代进度报告
     */
    List<ProgressReport> getIterationProgressReportList(String token);

    /**
     * 报表-查询项目进度报告
     */
    List<ProgressReport> getProjectProgressReportList(String token, ProjectQuery query);

    /***/
    List<String> getMyRunningProjectGroupList(String token);
    //

    /**
     * 通过手机号邀请加入项目
     */
    void inviteMemberFromMobileNo(String token, String mobileNo, String code, Integer projectId);

    /**
     * 邀请朋友加入项目
     */
    void inviteProjectMember(String token, String emails, Integer projectId);

    /**
     * 删除邀请
     */
    void deleteCompanyMemberInvite(String token, int id);

    /**
     * 查询企业邀请码列表
     */
    List<CompanyMemberInviteCodeInfo> getCompanyMemberInviteCodeList(String token);

    /**
     * 重置企业邀请码列表
     */
    List<CompanyMemberInviteCodeInfo> resetCompanyMemberInviteCodeList(String token);

    /**
     * 查询邀请好友列表
     */
    List<CompanyMemberInviteInfo> getCompanyMemberInviteInfoList(String token, Integer projectId);

    /**
     * 获取邀请信息
     */
    CompanyMemberInviteInfo getCompanyMemberInviteInfoByUuID(String uuid);

    /**
     * 查询通知列表
     */
    List<AccountNotification> getAccountNotificationList(String token, AccountNotificationQuery query);
    //

    /**
     * 查询回收站列表
     */
    Map<String, Object> getCompanyRecycleInfoList(String token, CompanyRecycleQuery query);

    /**
     * 删除回收站数据
     */
    void deleteCompanyRecycle(String token, int id);

    /**
     * 清空回收站
     */
    void emptyCompanyRecycle(String token);

    /**
     * 从回收站恢复
     */
    void restoreCompanyRecycle(String token, int id);

    //CMDB

    /**
     * 通过ID查询cmdb主机
     */
    CmdbMachineInfo getCmdbMachineById(String token, int id);

    /**
     * 查询cmdb主机列表和总数
     */
    Map<String, Object> getCmdbMachineList(String token, CmdbMachineQuery query);

    /**
     * 新增cmdb主机
     */
    int addCmdbMachine(String token, CmdbMachineInfo bean);

    /**
     * 编辑cmdb主机
     */
    void updateCmdbMachine(String token, CmdbMachineInfo bean);

    /**
     * 删除cmdb主机
     */
    void deleteCmdbMachine(String token, int id, String reason);
    //

    /**
     * 通过ID查询Application
     */
    CmdbApplicationInfo getCmdbApplicationById(String token, int id);

    /**
     * 查询Applicatio列表和总数
     */
    Map<String, Object> getCmdbApplicationList(String token, CmdbApplicationQuery query);

    /***/
    List<CmdbApplicationInfo> getCmdbApplicationInfoList(String token);

    /**
     * 新增Applicatio
     */
    int addCmdbApplication(String token, CmdbApplicationInfo bean);

    /**
     * 编辑Applicatio
     */
    void updateCmdbApplication(String token, CmdbApplicationInfo bean);

    /**
     * 删除Applicatio
     */
    void deleteCmdbApplication(String token, int id, String reason);

    //

    /**
     * 通过ID查询实例
     */
    CmdbInstanceInfo getCmdbInstanceById(String token, int id);

    /**
     * 查询实例列表和总数
     */
    Map<String, Object> getCmdbInstanceList(String token, CmdbInstanceQuery query);

    /**
     * 新增实例
     */
    int addCmdbInstance(String token, CmdbInstanceInfo bean);

    /**
     * 编辑实例
     */
    void updateCmdbInstance(String token, CmdbInstanceInfo bean);

    /**
     * 批量编辑实例
     */
    void batchUpdateCmdbInstance(String token, Set<Integer> idList, CmdbInstanceInfo bean);

    /**
     * 删除实例
     */
    void deleteCmdbInstance(String token, int id, String reason);
    //

    /**
     * 通过ID查询cmdb api
     */
    CmdbApiInfo getCmdbApiById(String token, int id);

    /**
     * 查询cmdb api列表和总数
     */
    Map<String, Object> getCmdbApiList(String token, CmdbApiQuery query);

    /**
     * 新增cmdb api
     */
    int addCmdbApi(String token, CmdbApiInfo bean);

    /**
     * 编辑cmdb api
     */
    void updateCmdbApi(String token, CmdbApiInfo bean);

    /**
     * 重新生成api key
     */
    void reloadCmdbApiKey(String token, int id);

    /**
     * 删除cmdb api
     */
    void deleteCmdbApi(String token, int id, String reason);

    //

    /**
     * 通过ID查询cmdb robot
     */
    CmdbRobotInfo getCmdbRobotById(String token, int id);

    /**
     * 查询cmdb robot列表和总数
     */
    Map<String, Object> getCmdbRobotList(String token, CmdbRobotQuery query);

    /**
     * 新增cmdb robot
     */
    int addCmdbRobot(String token, CmdbRobotInfo bean);

    /**
     * 编辑cmdb robot
     */
    void updateCmdbRobot(String token, CmdbRobotInfo bean);

    /**
     * 删除cmdb robot
     */
    void deleteCmdbRobot(String token, int id, String reason);

    //

    /**
     * 通过ID查询对象类型
     */
    ObjectTypeInfo getObjectTypeById(String token, int id);

    /***/
    List<ObjectTypeFieldDefine> getObjectTypeSystemFieldDefineList(String token);

    /**
     * 保存对象类型字段
     */
    void saveObjectType(String token, ObjectType bean, List<ObjectTypeFieldDefine> fieldList);

    /**
     * 删除对象类型
     */
    void deleteObjectType(String token, int id);

    /**
     * 查询对象类型列表和总数
     */
    Map<String, Object> getObjectTypeList(String token, ObjectTypeQuery query);

    //微信
    String generateQRCode(String token);

    /***/
    WxOauth getWxOauth(String state);

    /**
     * 微信登录
     */
    String wxLogin(String code, String state);

    /**
     * 微信网页登录
     */
    String wxPageLogin(String code);

    /**
     * 确认微信登录
     */
    void wxConfirmLogin(String state);

    /**
     * 是否微信绑定
     */
    boolean isWeixinBind(String token);

    /***/
    String createWxOauthState();

    //

    /**
     * 查询用户通知配置列表和总数
     */
    List<AccountNotificationSetting> getAccountNotificationSettingList(String token);

    /**
     * 新增用户通知配置
     */
    void saveAccountNotificationSetting(String token, List<AccountNotificationSettingInfo> bean);

    //

    /**
     * 查询ProjectArtifact列表和总数
     */
    Map<String, Object> getProjectArtifactList(String token, ProjectArtifactQuery query);

    /**
     * 查询ProjectArtifact
     */
    ProjectArtifactInfo getProjectArtifactInfoById(String token, int id);

    /***/
    ProjectArtifactInfo getProjectArtifactInfoByUuid(String token, String uuid);
    //

    /**
     * 查询日历token
     */
    String getAccountCalUuid(String token);

    /**
     * 刷新日历token
     */
    String refreshAccountCalUuid(String token);

    /**
     * 刷新accessToken
     */
    String refreshAccessToken(String token);

    /**
     * 获取文件列表
     */
    List<File> getFileList(String token, Set<String> fileUuidList);

    /**
     * 获取文件树
     */
    List<FileTree> getFileTree(String token, Set<String> fileUuidList);

    /**
     * 检查下载权限
     */
    void checkFileDownloadPermission(String token, String fileId);

    /**
     * 生成RSA公私钥队
     */
    RsaKeyPair genKeyPair(String token);

    ///

    /**
     * 通过ID查询项目对象模板
     */
    ProjectObjectTypeTemplateInfo getProjectObjectTypeTemplateById(String token, int id);

    /***/
    ProjectObjectTypeTemplateInfo getProjectObjectTypeTemplateByProjectIdObjectType(String token, int projectId, int objectType);

    /**
     * 查询项目对象模板列表和总数
     */
    List<ProjectObjectTypeTemplateInfo> getProjectObjectTypeTemplateList(String token, int projectId);

    /**
     * 新增项目对象模板
     */
    int saveProjectObjectTypeTemplate(String token, ProjectObjectTypeTemplateInfo bean);

    /**
     * 删除项目对象模板
     */
    void deleteProjectObjectTypeTemplate(String token, int id, String reason);


    /***/
    String gitlabWebhookToken(String token);

    /***/
    String githubWebhookToken(String token);
    ////////////
    //

    /**
     * 设置强制更新密码
     */
    void forceResetPassword(String token, int accountId);
    /////

    /**
     * 查询任务提醒
     */
    TaskRemindInfo getTaskRemindInfo(String token, int taskId);

    /**
     * 新增或编辑任务提醒
     */
    void updateTaskRemind(String token, TaskRemind bean);

    ///

    /**
     * 通过ID查询汇报模板
     */
    ReportTemplateInfo getReportTemplateById(String token, int id);

    /**
     * 查询汇报模板列表和总数
     */
    Map<String, Object> getReportTemplateList(String token, ReportTemplateQuery query);

    /**
     * 管理员查看汇报模板列表和总数
     */
    Map<String, Object> getReportTemplateListByAdmin(String token, ReportTemplateQuery query);

    /**
     * 新增汇报模板
     */
    int addReportTemplate(String token, ReportTemplateInfo bean);

    /**
     * 编辑汇报模板
     */
    void updateReportTemplate(String token, ReportTemplateInfo bean);

    /**
     * 设置汇报模板状态
     */
    void updateReportTemplateStatus(String token, int id, int status);

    /**
     * 删除汇报模板
     */
    void deleteReportTemplate(String token, int id, String reason);

    //

    /**
     * 通过ID查询汇报
     */
    ReportInfo getReportById(String token, int id);

    /**
     * 查询汇报列表和总数
     */
    Map<String, Object> getReportList(String token, ReportQuery query);

    /**
     * 保存汇报信息
     */
    void saveReport(String token, ReportInfo bean, boolean isSubmit);

    /**
     * 回复
     */
    void replyReport(String token, ReportContent bean);

    /**
     * 审核不通过
     */
    void auditRejectReport(String token, int reportId);

    /**
     * 审核通过
     */
    void auditPassReport(String token, int reportId);
    //

    /**
     * 通过ID查询仪表盘
     */
    DashboardInfo getDashboardById(String token, int id);

    /**
     * 查询仪表盘列表和总数
     */
    List<DashboardInfo> getDashboardList(String token);

    /**
     * 新增仪表盘
     */
    int addDashboard(String token, DashboardInfo bean);

    /**
     * 编辑仪表盘
     */
    void updateDashboard(String token, DashboardInfo bean);

    /**
     * 删除仪表盘
     */
    void deleteDashboard(String token, int id, String reason);

    //

    /**
     * 通过ID查询仪表盘卡牌
     */
    DashboardCardInfo getDashboardCardById(String token, int id);

    /**
     * 查询仪表盘卡牌列表和总数
     */
    List<DashboardCardInfo> getDashboardCardList(String token, DashboardCardQuery query);

    /**
     * 新增仪表盘卡牌
     */
    DashboardCardInfo addDashboardCard(String token, DashboardCardInfo bean);

    /**
     * 编辑仪表盘卡牌
     */
    void updateDashboardCard(String token, DashboardCardInfo bean);

    /**
     * 批量刷新仪表盘卡牌
     */
    void batchRefreshDashboardCard(String token, Set<Integer> idList);

    /**
     * 批量刷新仪表盘所有卡片
     */
    void refreshDashboardAllCard(String token, int dashboardId);

    /**
     * 刷新仪表盘卡牌
     */
    void refreshDashboardCard(String token, int id);

    /**
     * 删除仪表盘卡牌
     */
    void deleteDashboardCard(String token, int id, String reason);

    /**
     * 排序
     */
    void saveDashboardCardList(String token, List<DashboardCardInfo> cardList);

    /**
     * 新增项目运行日志
     */
    void addProjectRunLog(String token, ProjectRunLog bean);

    /**
     * 通过ID查询项目运行日志
     */
    ProjectRunLogInfo getProjectRunLogById(String token, int id);

    /**
     * 查询项目运行日志列表和总数
     */
    Map<String, Object> getProjectRunLogList(String token, ProjectRunLogQuery query);

    //讨论

    /**
     * 通过ID查询讨论
     */
    DiscussInfo getDiscussById(String token, int id);

    /**
     * 查询讨论最后更新时间 新增或删除讨论消息都会修改updateTime
     */
    Date getDiscussUpdateTimeById(String token, int id);

    /**
     * 查询讨论列表和总数
     */
    Map<String, Object> getDiscussList(String token, DiscussQuery query);

    /**
     * 新增讨论
     */
    int addDiscuss(String token, DiscussInfo bean);

    /**
     * 编辑讨论
     */
    void updateDiscuss(String token, DiscussInfo bean);

    /**
     * 删除讨论
     */
    void deleteDiscuss(String token, int id, String reason);

    //

    /**
     * 通过ID查询讨论消息
     */
    DiscussMessageInfo getDiscussMessageById(String token, int id);

    /**
     * 查询讨论消息列表和总数
     */
    Map<String, Object> getDiscussMessageList(String token, DiscussMessageQuery query);

    /**
     * 新增讨论消息
     */
    int addDiscussMessage(String token, DiscussMessageInfo bean);

    /**
     * 删除讨论消息
     */
    void deleteDiscussMessage(String token, int id, String reason);

    //

    /**
     * 通过ID查询操作日志
     */
    OptLogInfo getOptLogById(String token, int id);

    /**
     * 查询操作日志列表和总数
     */
    Map<String, Object> getOptLogList(String token, OptLogQuery query);

    /**
     * 新建示例工程
     */
    void addExampleProjects(String token, boolean isPrivateDeploy);

    /**
     * 示例工程增加数据
     */
    void doAddExampleProjects(String token, int projectId, boolean isPrivateDeploy);

    /***/
    Map<String, Object> getSystemConfig();

    /***/
    AccountProfileInfo getAccountProfileInfo(String token);
    //

    /**
     * 通过ID查询日历
     */
    CalendarInfo getCalendarById(String token, int id);

    /**
     * 查询日历列表和总数
     */
    List<CalendarInfo> getCalendarList(String token);

    /**
     * 新增日历
     */
    int addCalendar(String token, CalendarInfo bean);

    /**
     * 编辑日历
     */
    void updateCalendar(String token, CalendarInfo bean);

    /**
     * 删除日历
     */
    void deleteCalendar(String token, int id);
    //

    /**
     * 通过ID查询日程
     */
    CalendarScheduleInfo getCalendarScheduleById(String token, int id);

    /**
     * 查询日程列表和总数
     */
    Map<String, Object> getCalendarScheduleList(String token, CalendarScheduleQuery query);

    /**
     * 新增日程
     */
    int addCalendarSchedule(String token, CalendarScheduleInfo bean);

    /**
     * 编辑日程
     */
    void updateCalendarSchedule(String token, CalendarScheduleInfo bean);

    /**
     * 拖动日程
     */
    void dragCalendarSchedule(String token, CalendarScheduleDragInfo bean);

    /**
     * 删除日程
     */
    void deleteCalendarSchedule(String token, int id);

    //sftp
    //
    String startSftp(String token, String machineLoginToken);

    /**
     * 获取FTP信息
     */
    SftpInfo getSftpInfo(String token, String machineLoginToken);

    /***/
    void sftpCd(String token, String machineLoginToken, String dir);

    void sftpRm(String token, String machineLoginToken, String path);

    void sftpRmdir(String token, String machineLoginToken, String path);

    void sftpMkdir(String token, String machineLoginToken, String path);

    ConnectionDetailInfo getConnectionDetailInfo(String token, String machineLoginToken);

    //
    String pingBiz();

    /**
     * 飞书一键登录(授权登录)
     */
    LarkLoginInfo larkAuthorize(String token, String data);

    /**
     * 飞书注册
     */
    String larkRegister(String code, String state);

    /**
     * 飞书登录
     */
    String loginWithLarkOpenIdEncode(String openIdEncode);

    /**
     * 企业任务报告（周报）
     */
    CompanyTaskReport getCompanyTaskReport(String token, CompanyTaskReportQuery query);

    /**
     * 缺陷统计
     */
    List<AccoutBugStat> getAccountBugStatList(String token, AccoutBugStatQuery query);


    /**
     * 待办事项统计
     *
     * @param token
     * @param query
     * @return
     */
    List<CompanyMemberTodoTask> getCompanyMemberTodoTask(String token, CompanyMemberTodoTask.CompanyMemberTodoTaskQuery query);

    /**
     * 返回预览地址
     *
     * @param token
     * @param fileId
     * @return
     */
    String getFilePreviewLocation(String token, String fileId);

    /**
     * WPS文件元数据
     *
     * @param fName
     * @param reqSignature
     * @return
     */
    WpsFileInfo getWpsFileInfo(String fName, String reqSignature);
    //

    /**
     * 通过ID查询系统通知
     */
    SystemNotificationInfo getSystemNotificationById(String token, int id);

    /**
     * 查询系统通知列表和总数
     */
    Map<String, Object> getSystemNotificationList(String token, SystemNotificationQuery query);

    /**
     * 新增系统通知
     */
    int addSystemNotification(String token, SystemNotificationInfo bean);

    /**
     * 编辑系统通知
     */
    void updateSystemNotification(String token, SystemNotificationInfo bean);

    /**
     * 设置有效无效
     */
    void setSystemNotificationStatus(String token, int id, int status);

    /**
     * 删除系统通知
     */
    void deleteSystemNotification(String token, int id);

    /**
     * 项目集
     */
    Map<String, Object> getProjectSetInfo(String token);

    /**
     * 同步项目集
     */
    void syncTasksFromProject(String token);

    //

    /**
     * 通过ID查询版本库
     */
    CompanyVersionRepositoryInfo getCompanyVersionRepositoryById(String token, int id);

    /**
     * 查询版本库列表和总数
     */
    Map<String, Object> getCompanyVersionRepositoryList(String token, CompanyVersionRepositoryQuery query);

    /**
     * 新增版本库
     */
    int addCompanyVersionRepository(String token, CompanyVersionRepositoryInfo bean);

    /**
     * 编辑版本库
     */
    void updateCompanyVersionRepository(String token, CompanyVersionRepositoryInfo bean);

    /**
     * 删除版本库
     */
    void deleteCompanyVersionRepository(String token, int id);

    //

    /**
     * 通过ID查询版本
     */
    CompanyVersionInfo getCompanyVersionById(String token, int id);

    /**
     * 查询版本列表和总数
     */
    Map<String, Object> getCompanyVersionList(String token, CompanyVersionQuery query);

    /**
     * 新增版本
     */
    int addCompanyVersion(String token, CompanyVersionInfo bean);

    /**
     * 编辑版本
     */
    void updateCompanyVersion(String token, CompanyVersionInfo bean);

    /**
     * 删除版本
     */
    void deleteCompanyVersion(String token, int id);

    //

    /**
     * 通过ID查询版本任务
     */
    CompanyVersionTaskInfo getCompanyVersionTaskById(String token, int id);

    /**
     * 查询版本任务列表和总数
     */
    Map<String, Object> getCompanyVersionTaskList(String token, CompanyVersionTaskQuery query);

    /**
     * 批量新增版本任务
     */
    void addCompanyVersionTaskList(String token, int versionId, List<Integer> taskIdList);

    /**
     * 批量删除版本任务
     */
    void batchDeleteCompanyVersionTaskList(String token, int versionId, List<Integer> idList);

    /**
     * 查询变更记录列表 项目动态
     */
    Map<String, Object> getCompanyVersionTaskChangeLogList(String token, int versionId, ChangeLogQuery query);

    /**
     * 获取历史项目分组候选
     */
    List<String> getProjectGroups(String token);

    /**
     * 查询汇报模板项目列表
     */
    Map<String, Object> getReportAllProjectList(String token, ProjectQuery query);

    /**
     * 批量冻结账号
     */
    void batchFreezeAccount(String token, List<Integer> accountIds);

    /**
     * 批量删除账号
     */
    void batchDeleteAccount(String token, List<Integer> accountIds);

    /**
     * 批量强制账号改密
     */
    void batchForceUpdatePasswd(String token, List<Integer> accountIds);

    /**
     * 批量更新企业角色
     */
    void batchUpdateRole(String token, List<Integer> accountIds, List<Integer> roleIds);

    /**
     * 批量调整部门
     */
    void batchAdjustDepartment(String token, List<Integer> accountIds, List<Integer> departmentIds);

    /**
     * 批量加入项目
     */
    void batchJoinProject(String token, List<Integer> accountIds, List<Integer> projectIds, List<Integer> projectRoleIds);

    /**
     * 批量移出项目
     */
    void batchRemoveProject(String token, List<Integer> accountIds, List<Integer> projectIds);

    /**
     * 查询企业密码规则
     */
    PasswordRule getPasswordRule(String token);

    /**
     * 创建企业密码规则
     */
    void addPasswordRule(String token, PasswordRule bean);

    /**
     * 修改企业密码规则
     */
    void updatePasswordRule(String token, PasswordRule bean);

    /**
     * 查询交付版本列表
     */
    List<Delivery.DeliveryInfo> getDeliveryList(String token, Delivery.DeliveryQuery query);

    /**
     * 查询交付版本详情
     */
    Delivery.DeliveryInfo getDeliveryById(String token, int id);

    /**
     * 创建交付版本
     */
    void addDelivery(String token, Delivery.DeliveryInfo bean);

    /**
     * 交付状态变更
     */
    void updateDeliveryStatus(String token, Delivery bean);

    /**
     * 编辑交付版本
     */
    void updateDelivery(String token, Delivery.DeliveryInfo bean);

    /**
     * 删除交付版本
     */
    void deleteDelivery(String token, int id);

    /**
     * 删除交付版本版本明细
     */
    void addDeliveryItem(String token, DeliveryItem bean);

    /**
     * 删除交付版本下的版本明细
     */
    void deleteDeliveryItem(String token, int id);

    /**
     * 获取任务关联信息
     */
    Map<String, Object> getTaskAssociateStat(String token, int id);

    /**
     * 批量删除汇报
     */
    void batchDeleteReport(String token, List<Integer> reportIds);

    /**
     * 查询scm分支列表
     */
    List<ScmBranch.ScmBranchInfo> getScmBranchList(String token, ScmBranch.ScmBranchQuery query);

    /**
     * 查询scm分支详情
     */
    ScmBranch.ScmBranchInfo getScmBranchById(String token, int id);

    /**
     * 创建scm分支
     */
    void addScmBranch(String token, ScmBranch.ScmBranchInfo bean);

    /**
     * 编辑scm分支
     */
    void updateScmBranch(String token, ScmBranch.ScmBranchInfo bean);

    void saveScmBranchList(String token, List<ScmBranch.ScmBranchInfo> branchList);

    /**
     * 删除scm分支
     */
    void deleteScmBranch(String token, int id);

    /**
     * 查询阶段列表
     */
    List<Stage.StageInfo> getStageList(String token, Stage.StageQuery query);

    /**
     * 查询阶段详情
     */
    Stage.StageInfo getStageById(String token, int id);

    /**
     * 创建阶段
     */
    void addStage(String token, Stage.StageInfo bean);

    /**
     * 编辑阶段
     */
    void updateStage(String token, Stage.StageInfo bean);

    /**
     * 编辑阶段工时
     */
    void updateStageWorkday(String token, Stage.StageInfo bean, boolean danymicUpdate);

    /**
     * 编辑阶段
     */
    void deleteStage(String token, int id);

    /**
     * 查询阶段关联列表
     */
    List<StageAssociate.StageAssociateInfo> getStageAssociateList(String token, StageAssociate.StageAssociateQuery query);

    /**
     * 查询阶段关联详情
     */
    StageAssociate.StageAssociateInfo getStageAssociateById(String token, int id);

    /**
     * 创建阶段关联
     */
    void addStageAssociate(String token, StageAssociate.StageAssociateInfo bean);

    /**
     * 批量添加关联
     */
    void batchAddStageAssociate(String token, List<StageAssociate> associateList);

    void deleteLandmarkAssociate(String token, int id);

    void batchAddLandmarkAssociate(String token, List<LandmarkAssociate> associateList);

    /**
     * 编辑阶段关联
     */
    void updateStageAssociate(String token, StageAssociate.StageAssociateInfo bean);

    /**
     * 删除阶段关联
     */
    void deleteStageAssociate(String token, int id);

    List<StageBurnDownData> getProjectStageBurnDownChart(String token, int projectId);

    List<StageFinishDelayRate> getProjectStageFinishDelayRate(String token, int projectId);

    /**
     * 获取项目文件节点树
     */
    List<DirectoryNode> getProjectFullFileTree(String token, int projectId);

    /**
     * 获取项目wiki节点树
     */
    List<DirectoryNode> getProjectFullWikiTree(String token, int projectId);

    /**
     * 模板项目文件树
     */
    void saveTemplateProjectFileList(String token, int projectId, List<DirectoryNode> files);

    /**
     * 设置复制对象的父子关联关系
     *
     * @param token
     * @param relations
     */
    void setCopyTaskPubsubRelation(String token, List<Map<String, Integer>> relations);

    /**
     * 创建里程碑
     */
    void addLandmark(String token, Landmark.LandmarkInfo bean);

    /**
     * 删除里程碑
     */
    void deleteLandmark(String token, int id);

    /**
     * 编辑里程碑
     */
    void updateLandmark(String token, Landmark.LandmarkInfo bean);

    /**
     * 编辑里程碑周期
     */
    void updateLandmarkWorkday(String token, Landmark.LandmarkInfo bean, boolean danymicUpdate);

    /**
     * 查询里程碑详情
     */
    Landmark.LandmarkInfo getLandmarkById(String token, int id);

    /**
     * 查询里程碑列表
     */
    List<Landmark.LandmarkInfo> getLandmarkList(String token, Landmark.LandmarkQuery query);

    /**
     * 查询里程碑日志列表
     */
    List<LandmarkLog.LandmarkLogInfo> getLandmarkLogList(String token, LandmarkLog.LandmarkLogQuery query);

    /**
     * 查询任务的可编辑字段列表
     */
    List<Map<String, Object>> getTaskEditableFieldList(String token, int taskId);

    /**
     * 添加成员关注
     */
    void addAccountStar(String token, AccountStar bean);

    /**
     * 删除成员关注
     */
    void deleteAccountStar(String token, AccountStar bean);

    /**
     * 查询成员关注列表
     */
    Map<String, Object> getAccountStars(String token);

    /**
     * 保存主页设置
     */
    void saveAccountHomeSetting(String token, AccountHomeSetting bean);

    /**
     * 查询用户的主页的显示隐藏
     */
    void updateAccountHomeVisible(String token, boolean visible);

    /**
     * 查询用户的主页设置
     */
    AccountHomeSetting getAccountHomeSetting(String token);

    /**
     * 保存任务排序设置
     */
    void saveTaskSortSetting(String token, TaskSortSetting bean);

    /**
     * 保存任务排序设置
     */
    TaskSortSetting getTaskSortSetting(String token, int projectId, int objectType);

    /**
     * 查询供应商列表
     *
     * @return
     */
    Map<String, Object> getSupplierList(String token, Supplier.SupplierQuery query);

    /**
     * 查询供应商详情
     */
    Supplier.SupplierInfo getSupplierById(String token, int id);

    /**
     * 创建供应商
     *
     * @return
     */
    int addSupplier(String token, Supplier.SupplierInfo bean);

    /**
     * 编辑供应商
     */
    void updateSupplier(String token, Supplier.SupplierInfo bean);

    /**
     * 删除供应商
     */
    void deleteSupplier(String token, int id);

    /**
     * 查询供应商成员列表
     *
     * @return
     */
    Map<String, Object> getSupplierMemberList(String token, SupplierMember.SupplierMemberQuery query);

    /**
     * 查询供应商成员详情
     */
    SupplierMember.SupplierMemberInfo getSupplierMemberById(String token, int id);

    /**
     * 创建供应商成员
     *
     * @return
     */
    int addSupplierMember(String token, SupplierMember.SupplierMemberInfo bean);

    /**
     * 编辑供应商成员
     */
    void updateSupplierMember(String token, SupplierMember.SupplierMemberInfo bean);

    /**
     * 删除供应商成员
     */
    void deleteSupplierMember(String token, int id);


    /**
     * 查询钉钉考勤列表
     *
     * @return
     */
    Map<String, Object> getDingtalkAttendanceList(String token, DingtalkAttendance.DingtalkAttendanceQuery query);

    /**
     * 查询钉钉考勤详情
     */
    DingtalkAttendance.DingtalkAttendanceInfo getDingtalkAttendanceById(String token, int id);


    /**
     * 删除钉钉考勤
     */
    void deleteDingtalkAttendance(String token, int id);

    /**
     * 查询钉钉成员列表
     *
     * @return
     */
    Map<String, Object> getDingtalkMemberList(String token, DingtalkMember.DingtalkMemberQuery query);

    /**
     * 查询钉钉成员详情
     */
    DingtalkMember.DingtalkMemberInfo getDingtalkMemberById(String token, int id);


    /**
     * 删除钉钉成员
     */
    void deleteDingtalkMember(String token, int id);

    /**
     * 成员账号绑定钉钉账号
     */
    void bindAccountDingtalkAttendance(String token, int accountId, int dingtalkMemberId);

    void syncDingtalkAttendance(String token, Date date);

    void syncDingtalkMember(String token);

    void syncAdAccount(String token);

    ExportData exportSupplierMemberListToExcel(String token, SupplierMember.SupplierMemberQuery query);

    ExportData exportCompanyVersionRepositoryListToExcel(String token, CompanyVersionRepositoryQuery query);

    /**
     * 导入系统
     *
     * @param token
     * @param fileId
     */
    void importCompanyVersionRepositoryFromExcel(String token, String fileId);

    //END
    @RpcService
    class BizActionImpl extends CommActionImpl implements BizAction {
        //
        private static final Logger logger = LoggerFactory.get(BizActionImpl.class);
        //
        @AutoWired
        public BizDAO dao;
        @AutoWired
        StatDAO statDao;
        @AutoWired
        BizService bizService;
        @AutoWired
        StatService statService;
        @AutoWired
        EmailService emailService;
        @AutoWired
        QCloundSMSService qCloundSMSService;
        @AutoWired
        LuceneService luceneService;
        @AutoWired
        LdapService ldapService;
        @AutoWired
        LdapAuthService ldapAuthService;
        @AutoWired
        WeixinService weixinService;
        @AutoWired
        LarkService larkService;

        @Override
        public List<Map<String, Object>> getTaskEditableFieldList(String token, int taskId) {
            Account account = bizService.getExistedAccountByToken(token);
            Task task = dao.getExistedById(Task.class, taskId);
            return bizService.getTaskFieldPermissionList(account, task);
        }

        @Override
        public void addAccountStar(String token, AccountStar bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bean.companyId = account.companyId;
            bean.accountId = account.id;
            dao.add(bean);
        }

        @Override
        public void deleteAccountStar(String token, AccountStar bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bean.companyId = account.companyId;
            bean.accountId = account.id;
            dao.deleteAccountStar(bean);
        }

        @Override
        public Map<String, Object> getAccountStars(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            Map<String, Object> result = new HashMap<>();
            AccountStar.AccountStarQuery query = new AccountStar.AccountStarQuery();
            query.companyId = account.companyId;
            query.accountId = account.id;
            query.pageSize = Integer.MAX_VALUE;
            List<AccountStar> accountStarList = dao.getList(query);
            //分管项目
            List<Integer> managedProjectIds = dao.getManagedProjectIdList(account);
            List<Integer> managedTaskIds = dao.getManagedTaskIdList(account);
            Set<Integer> taskIds = new HashSet<>();
            Set<Integer> projectIds = new HashSet<>();
            if (!BizUtil.isNullOrEmpty(accountStarList)) {
                taskIds = accountStarList.stream().filter(k -> k.type == AccountStar.TYPE_任务)
                        .map(k -> k.associateId).collect(Collectors.toSet());
                projectIds = accountStarList.stream().filter(k -> k.type == AccountStar.TYPE_项目)
                        .map(k -> k.associateId).collect(Collectors.toSet());
            }
            taskIds.addAll(managedTaskIds);
            if (BizUtil.isNullOrEmpty(taskIds)) {
                result.put("tasks", Collections.emptyList());
            } else {
                TaskQuery tq = new TaskQuery();
                tq.idInList = BizUtil.convertList(taskIds);
                tq.pageSize = Integer.MAX_VALUE;
                result.put("tasks", dao.getList(tq));
            }


            projectIds.addAll(managedProjectIds);
            if (BizUtil.isNullOrEmpty(projectIds)) {
                result.put("projects", Collections.emptyList());
            } else {
                ProjectQuery pq = new ProjectQuery();
                pq.idInList = BizUtil.convertList(projectIds);
                pq.pageSize = Integer.MAX_VALUE;
                result.put("projects", dao.getList(pq));
            }


            return result;
        }

        @Override
        public void saveAccountHomeSetting(String token, AccountHomeSetting bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bean.accountId = account.id;
            bean.companyId = account.companyId;
            AccountHomeSetting old = dao.getAccountHomeSetting(account.id, account.companyId);
            if (null == old) {
                dao.add(bean);
            } else {
                bean.id = old.id;
                dao.update(bean);
            }
        }

        @Override
        public void updateAccountHomeVisible(String token, boolean visible) {
            Account account = bizService.getExistedAccountByToken(token);
            AccountHomeSetting old = dao.getAccountHomeSetting(account.id, account.companyId);
            if (null == old) {
                old = new AccountHomeSetting();
                old.companyId = account.companyId;
                old.accountId = account.id;
                old.visible = visible;
                dao.add(old);
            } else {
                old.visible = visible;
                dao.updateSpecialFields(old, "visible");
            }
        }

        @Override
        public AccountHomeSetting getAccountHomeSetting(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            return dao.getAccountHomeSetting(account.id, account.companyId);
        }

        @Override
        public void saveTaskSortSetting(String token, TaskSortSetting bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bean.companyId = account.companyId;
            bean.updateAccountId = account.id;
            TaskSortSetting old = dao.getTaskSortSetting(bean);
            if (null == old) {
                dao.add(bean);
            } else {
                old.sortData = bean.sortData;
                dao.updateSpecialFields(old, "sortData");
            }
        }

        @Override
        public TaskSortSetting getTaskSortSetting(String token, int projectId, int objectType) {
            Account account = bizService.getExistedAccountByToken(token);
            TaskSortSetting setting = new TaskSortSetting();
            setting.companyId = account.companyId;
            setting.projectId = projectId;
            setting.objectType = objectType;
            setting.type = TaskSortSetting.TYPE_看板;
            return dao.getTaskSortSetting(setting);
        }

        @Override
        public Map<String, Object> getSupplierList(String token, Supplier.SupplierQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看供应商, account.companyId);
            setupQuery(account, query);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        @Override
        public Supplier.SupplierInfo getSupplierById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看供应商, account.companyId);
            return dao.getExistedById(Supplier.SupplierInfo.class, id);
        }

        @Transaction
        @Override
        public int addSupplier(String token, Supplier.SupplierInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_创建和编辑供应商, account.companyId);
            BizUtil.checkValid(bean);
            bean.companyId = account.companyId;
            bean.createUserId = account.id;
            int id = dao.add(bean);
            bizService.addOptLog(account, bean.id, bean.name, OptLog.EVENT_ID_新增供应商, JSONUtil.toJson(bean));
            return id;
        }

        @Transaction
        @Override
        public void updateSupplier(String token, Supplier.SupplierInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_创建和编辑供应商, account.companyId);
            BizUtil.checkValid(bean);
            bean.updateUserId = account.id;
            dao.update(bean);
            bizService.addOptLog(account, bean.id, bean.name, OptLog.EVENT_ID_编辑供应商, JSONUtil.toJson(bean));
        }

        @Transaction
        @Override
        public void deleteSupplier(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_删除供应商, account.companyId);
            Supplier old = dao.getExistedById(Supplier.class, id);
            SupplierMember.SupplierMemberQuery query = new SupplierMember.SupplierMemberQuery();
            setupQuery(account, query);
            query.supplierId = id;
            int memberCount = dao.getListCount(query);
            if (memberCount > 0) {
                throw new AppException("该供应商有人员存在,无法删除");
            }
            dao.deleteById(Supplier.class, id);
            bizService.addOptLog(account, id, old.name, OptLog.EVENT_ID_删除供应商, "name:" + old.name);
        }

        @Override
        public Map<String, Object> getSupplierMemberList(String token, SupplierMember.SupplierMemberQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看供应商, account.companyId);
            setupQuery(account, query);
            int count = dao.getListCount(query);
            if (count == 0) {
                return createResult(Collections.emptyList(), 0);
            }
            List<SupplierMember.SupplierMemberInfo> list = dao.getList(query);
           /* for (SupplierMember.SupplierMemberInfo member : list) {
                member.projectList = bizService.getMyProjectInfoList(member.accountId, account.companyId);
                CompanyMemberInfo companyMember = dao.getExistedCompanyMemberInfo(member.accountId, account.companyId);
                if(null!=companyMember){
                    member.departmentList = companyMember.departmentList;
                }
                if(!BizUtil.isNullOrEmpty(member.productIds)){
                    member.repositoryList = dao.getList(CompanyVersionRepository.class,QueryWhere.create()
                            .in(SelectProvider.MAIN_TABLE_ALIAS,"id",member.productIds.toArray()));
                }
            }*/
            loadSupplierMemberInfos(account, list);
            return createResult(list, count);
        }


        private void loadSupplierMemberInfos(Account account, List<SupplierMember.SupplierMemberInfo> list) {
            if (!BizUtil.isNullOrEmpty(list)) {
                List<ProjectInfo> projects = dao.getAll(ProjectInfo.class);
                List<ProjectMember> projectMembers = dao.getAll(ProjectMember.class);
                List<CompanyMember> companyMembers = dao.getAll(CompanyMember.class);
                List<CompanyVersionRepository> repositories = dao.getAll(CompanyVersionRepository.class);
                Map<Integer, List<Integer>> accountProjectMap = new HashMap<>();
                Map<Integer, ProjectInfo> projectMap = new HashMap<>();
                Map<Integer, CompanyMember> companyMemberMap = new HashMap<>();
                Map<Integer, CompanyVersionRepository> repositoryMap = new HashMap<>();
                if (!BizUtil.isNullOrEmpty(projects)) {
                    projectMap = projects.stream().filter(k -> k.companyId == account.companyId).collect(Collectors.toMap(k -> k.id, v -> v));
                }
                if (!BizUtil.isNullOrEmpty(repositories)) {
                    repositoryMap = repositories.stream().filter(k -> k.companyId == account.companyId).collect(Collectors.toMap(k -> k.id, v -> v));
                }
                if (!BizUtil.isNullOrEmpty(companyMembers)) {
                    companyMemberMap = companyMembers.stream().filter(k -> k.companyId == account.companyId).collect(Collectors.toMap(k -> k.accountId, v -> v));
                }
                if (!BizUtil.isNullOrEmpty(projectMembers)) {
                    accountProjectMap = projectMembers.stream().filter(k -> k.companyId == account.companyId).collect(Collectors.groupingBy(k -> k.accountId, Collectors.mapping(k -> k.projectId, Collectors.toList())));
                }
                for (SupplierMember.SupplierMemberInfo e : list) {
                    int accountId = e.accountId;
                    List<Integer> projectIds = accountProjectMap.get(accountId);
                    if (!BizUtil.isNullOrEmpty(projectIds)) {
                        e.projectList = new ArrayList<>();
                        for (Integer projectId : projectIds) {
                            ProjectInfo pro = projectMap.get(projectId);
                            if (null != pro && !pro.isDelete) {
                                e.projectList.add(pro);
                            }
                        }
                    }
                    CompanyMember cm = companyMemberMap.get(accountId);
                    if (null != cm) {
                        e.departmentList = cm.departmentList;
                    }
                    if (!BizUtil.isNullOrEmpty(e.productIds)) {
                        e.repositoryList = new ArrayList<>();
                        for (Integer productId : e.productIds) {
                            CompanyVersionRepository cvr = repositoryMap.get(productId);
                            if (null != cvr && !cvr.isDelete) {
                                e.repositoryList.add(cvr);
                            }
                        }
                    }
                }

            }
        }

        @Override
        public SupplierMember.SupplierMemberInfo getSupplierMemberById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看供应商, account.companyId);
            SupplierMember.SupplierMemberInfo info = dao.getExistedById(SupplierMember.SupplierMemberInfo.class, id);
            info.projectList = bizService.getMyProjectInfoList(info.accountId, account.companyId);
            CompanyMemberInfo member = dao.getExistedCompanyMemberInfo(info.accountId, account.companyId);
            if (null != member) {
                info.departmentList = member.departmentList;
            }
            return info;
        }

        @Transaction
        @Override
        public int addSupplierMember(String token, SupplierMember.SupplierMemberInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            if (bean.accountId <= 0) {
                throw new AppException("请先从成员库中选择成员");
            }
            Account acc = dao.getExistedById(Account.class, bean.accountId);
            bean.name = acc.name;
            if (BizUtil.isNullOrEmpty(bean.mobile)) {
                bean.mobile = acc.mobileNo;
            }
            if (BizUtil.isNullOrEmpty(bean.email)) {
                bean.email = acc.email;
            }
            if (bean.status == 0) {
                bean.status = ConstDefine.Common_status_有效;
            }
            if (bean.supplierId > 0) {
                acc.supplierId = bean.supplierId;
                dao.updateSpecialFields(acc, "supplierId");
            }
            bizService.checkCompanyPermission(account, Permission.ID_创建和编辑供应商, account.companyId);
            BizUtil.checkValid(bean);
            bean.companyId = account.companyId;
            bean.createUserId = account.id;
            if (bean.dingtalkMemberId > 0) {
                DingtalkMember old = dao.getDomain(DingtalkMember.class, QueryWhere.create().where("account_id", acc.id));
                if (null != old) {
                    throw new AppException("该钉钉号已绑定");
                }
                DingtalkMember dingtalkMember = dao.getExistedById(DingtalkMember.class, bean.dingtalkMemberId);
                acc.dingtalkMemberId = bean.dingtalkMemberId;
                dao.updateSpecialFields(acc, "dingtalkMemberId");

                dingtalkMember.accountId = acc.id;
                dingtalkMember.companyId = acc.companyId;
                dao.updateSpecialFields(dingtalkMember, "accountId", "companyId");

                //
                dao.updateDingtalkAttendanceAccount(dingtalkMember);
            }
            int id = dao.add(bean);
            bizService.addOptLog(account, bean.id, bean.name, OptLog.EVENT_ID_新增供应商成员, JSONUtil.toJson(bean));
            return id;
        }

        @Transaction
        @Override
        public void updateSupplierMember(String token, SupplierMember.SupplierMemberInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            if (bean.accountId <= 0) {
                throw new AppException("请先从成员库中选择成员");
            }
            Account acc = dao.getExistedById(Account.class, bean.accountId);
            bean.name = acc.name;
            if (BizUtil.isNullOrEmpty(bean.mobile)) {
                bean.mobile = acc.mobileNo;
            }
            if (BizUtil.isNullOrEmpty(bean.email)) {
                bean.email = acc.email;
            }
            bizService.checkCompanyPermission(account, Permission.ID_创建和编辑供应商, account.companyId);
            BizUtil.checkValid(bean);
            bean.companyId = account.companyId;
            bean.createUserId = account.id;
            if (bean.dingtalkMemberId > 0) {
                DingtalkMember old = dao.getDomain(DingtalkMember.class, QueryWhere.create().where("account_id", acc.id));
                if (null != old && old.id != bean.dingtalkMemberId) {
                    throw new AppException("该钉钉号已绑定");
                }
                DingtalkMember dingtalkMember = dao.getExistedById(DingtalkMember.class, bean.dingtalkMemberId);
                acc.dingtalkMemberId = bean.dingtalkMemberId;
                dao.updateSpecialFields(acc, "dingtalkMemberId");

                dingtalkMember.accountId = acc.id;
                dingtalkMember.companyId = acc.companyId;
                dao.updateSpecialFields(dingtalkMember, "accountId", "companyId");

                //
                dao.updateDingtalkAttendanceAccount(dingtalkMember);
            } else {
                if (acc.dingtalkMemberId > 0) {
                    acc.dingtalkMemberId = 0;
                    dao.updateSpecialFields(acc, "dingtalkMemberId");
                }
                DingtalkMember dingtalkMember = dao.getDomain(DingtalkMember.class, QueryWhere.create().where("account_id", acc.id));
                if (null != dingtalkMember) {
                    dingtalkMember.accountId = 0;
                    dao.updateSpecialFields(dingtalkMember, "accountId");
                }
                //清除考勤数据关联
                dao.resetDingtalkAttendanceAccount(acc.id);
            }
            bean.updateUserId = account.id;
            dao.update(bean);
            bizService.addOptLog(account, bean.id, bean.name, OptLog.EVENT_ID_编辑供应商成员, JSONUtil.toJson(bean));
        }

        @Transaction
        @Override
        public void deleteSupplierMember(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_删除供应商, account.companyId);
            SupplierMember old = dao.getExistedById(SupplierMember.class, id);
            dao.deleteById(SupplierMember.class, id);
            bizService.addOptLog(account, id, old.name, OptLog.EVENT_ID_删除供应商成员, "name:" + old.name);
        }

        @Override
        public Map<String, Object> getDingtalkAttendanceList(String token, DingtalkAttendance.DingtalkAttendanceQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看钉钉考勤, account.companyId);
            setupQuery(account, query);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        @Override
        public DingtalkAttendance.DingtalkAttendanceInfo getDingtalkAttendanceById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看钉钉考勤, account.companyId);
            return dao.getExistedById(DingtalkAttendance.DingtalkAttendanceInfo.class, id);
        }

        @Override
        public void deleteDingtalkAttendance(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_删除钉钉考勤, account.companyId);
            DingtalkAttendance old = dao.getExistedById(DingtalkAttendance.class, id);
            dao.deleteById(DingtalkAttendance.class, id);
//            bizService.addOptLog(account, id, old.name, OptLog.EVENT_ID_删除供应商成员, "name:" + old.name);
        }

        @Override
        public Map<String, Object> getDingtalkMemberList(String token, DingtalkMember.DingtalkMemberQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看钉钉考勤, account.companyId);
            setupQuery(account, query);
            query.pageSize = Integer.MAX_VALUE;
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        @Override
        public DingtalkMember.DingtalkMemberInfo getDingtalkMemberById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看钉钉考勤, account.companyId);
            return dao.getExistedById(DingtalkMember.DingtalkMemberInfo.class, id);
        }

        @Override
        public void deleteDingtalkMember(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_删除钉钉考勤, account.companyId);
            DingtalkMember old = dao.getExistedById(DingtalkMember.class, id);
            dao.deleteById(DingtalkAttendance.class, id);
        }

        @Transaction
        @Override
        public void bindAccountDingtalkAttendance(String token, int accountId, int dingtalkMemberId) {
            Account optAccount = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(optAccount, Permission.ID_管理成员);
            Account account = dao.getExistedById(Account.class, accountId);
            DingtalkMember member = dao.getExistedById(DingtalkMember.class, dingtalkMemberId);
            //account dingtalkmember dingtalkattendance
            account.dingtalkMemberId = member.id;
            dao.updateSpecialFields(account, "dingtalkId");

            member.accountId = accountId;
            dao.updateSpecialFields(member, "accountId");

            dao.updateDingtalkAttendanceAccount(member);
        }

        @Transaction
        @Override
        public void syncDingtalkAttendance(String token, Date date) {
            Account account = bizService.getExistedAccountByToken(token);
            if (null == date) {
                throw new AppException("请指定同步月份");
            }
            bizService.syncDingtalkAttendance(date);
        }

        @Override
        public void syncDingtalkMember(String token) {
            Account account = bizService.getExistedAccountByToken(token);

            bizService.syncDingtalkMember();
        }

        @Override
        public void syncAdAccount(String token) {
            Account account = bizService.getExistedAccountByToken(token);

            bizService.syncAdAccount();
        }

        @Override
        public ExportData exportSupplierMemberListToExcel(String token, SupplierMember.SupplierMemberQuery query) {
            ExportData ret = new ExportData();
            Account account = bizService.getExistedAccountByToken(token);
            setupQuery(account, query);
            List<String> headers = new ArrayList<>(Arrays.asList("姓名", "关联钉钉成员", "手机号", "所属供应商", "所属科室", "参与项目", "系统", "工作状态", "岗位"
                    , "工号", "工位号", "工作地点", "邮箱", "身份证", "门禁是否已发放", "承诺书是否已签"
                    , "级别", "入场时间", "离场时间", "籍贯", "学历", "毕业院校"));
            //
            List<SupplierMember.SupplierMemberInfo> list = dao.getList(query);

            TableData data = new TableData();
            data.headers = headers;
            //export optimize
            loadSupplierMemberInfos(account, list);

            for (SupplierMember.SupplierMemberInfo e : list) {
                List<String> content = new ArrayList<>();
                content.add(e.name);
                content.add(e.dingtalkMemberName);
                content.add(e.mobile);
                content.add(e.supplierName);
                if (!BizUtil.isNullOrEmpty(e.departmentList)) {
                    content.add(e.departmentList.stream().map(k -> k.name).collect(Collectors.joining("、")));
                } else {
                    content.add("");
                }
                if (!BizUtil.isNullOrEmpty(e.projectList)) {
                    content.add(e.projectList.stream().map(k -> k.name).collect(Collectors.joining("、")));
                } else {
                    content.add("");
                }
                if (!BizUtil.isNullOrEmpty(e.repositoryList)) {
                    content.add(e.repositoryList.stream().map(k -> k.name).collect(Collectors.joining("、")));
                } else {
                    content.add("");
                }
                content.add(BizUtil.getDictNameByValue("SupplierMember.workStatus", e.workStatus));
                content.add(BizUtil.getDictNameByValue("SupplierMember.position", e.position));
                content.add(e.code);
                content.add(e.baseStationCode);
                content.add(e.baseStation);
                content.add(e.email);
                content.add(e.idCard);
                content.add(e.entryCard ? "已发放" : "未发放");
                content.add(e.promiseDesc ? "已签" : "未签");
                content.add(BizUtil.getDictNameByValue("SupplierMember.level", e.level));
                content.add(DateUtil.formatDate(e.entryTime, "yyyy-MM-dd"));
                content.add(DateUtil.formatDate(e.leaveTime, "yyyy-MM-dd"));
                content.add(e.nativePlace);
                content.add(e.education);
                content.add(e.graduation);
                //
                data.contents.add(content);
            }
            ret.fileName = "外包人员.xlsx";
            ret.tableData = data;
            return ret;
        }

        @Override
        public ExportData exportCompanyVersionRepositoryListToExcel(String token, CompanyVersionRepositoryQuery query) {
            ExportData ret = new ExportData();
            Account account = bizService.getExistedAccountByToken(token);
            setupQuery(account, query);
            List<String> headers = new ArrayList<>(Arrays.asList("系统名称", "系统经理", "开发科室", "业务经理", "系统主管部门", "系统最新版本号", "系统发布时间", "系统状态", "是否纳入322N架构体系"
                    , "332N板块分类", "详细描述"));
            //
            query.pageSize = Integer.MAX_VALUE;
            List<CompanyVersionRepositoryInfo> list = dao.getList(query);

            TableData data = new TableData();
            data.headers = headers;
            data.rangeAddresses = new ArrayList<>();
            //export optimize
            if (!BizUtil.isNullOrEmpty(list)) {
                CompanyVersionQuery versionQuery = new CompanyVersionQuery();
                setupQuery(account, versionQuery);
               /* List<CompanyVersionInfo> versionInfos = dao.getAll(versionQuery);
                if (!BizUtil.isNullOrEmpty(versionInfos)) {
                    Map<Integer, List<CompanyVersionInfo>> versionMap = versionInfos.stream().collect(Collectors.groupingBy(k -> k.repositoryId));
                    for (CompanyVersionRepositoryInfo repository : list) {
                        repository.versionList = versionMap.get(repository.id);
                    }
                }*/
            }

            int lastRowSpanIndex = 1;
            for (CompanyVersionRepositoryInfo repositoryInfo : list) {
               /* List<CompanyVersionInfo> versionList = repositoryInfo.versionList;
                if (!BizUtil.isNullOrEmpty(versionList)) {

                    if (versionList.size() > 1) {
                        for (int i = 0; i < 11; i++) {
                            CellRangeAddress cellRangeAddress = new CellRangeAddress(lastRowSpanIndex, (lastRowSpanIndex + versionList.size() - 1), i, i);
                            data.rangeAddresses.add(cellRangeAddress);
                        }
                        lastRowSpanIndex += (versionList.size() - 1);
                    } else {
                        lastRowSpanIndex++;
                    }
                    versionList.forEach(version -> {
                        List<String> content = new ArrayList<>();
                        content.add(repositoryInfo.name);
                        if (!BizUtil.isNullOrEmpty(repositoryInfo.ownerAccountList)) {
                            content.add(repositoryInfo.ownerAccountList.stream().map(k -> k.name).collect(Collectors.joining("、")));
                        } else {
                            content.add("");
                        }
                        content.add(repositoryInfo.ownerDepartmentName);
                        content.add(repositoryInfo.businessLeader);
                        content.add(repositoryInfo.department);
                        content.add(repositoryInfo.latest);
                        content.add(DateUtil.formatDate(repositoryInfo.releaseDate, "yyyy-MM-dd"));
                        content.add(BizUtil.getDictNameByValue("CompanyVersionRepository.status", repositoryInfo.status));
                        content.add(BizUtil.cleanHtml(repositoryInfo.description));
                        content.add(BizUtil.getBooleanValue(repositoryInfo.isArch332n));
                        content.add(BizUtil.getDictNameByValue("CompanyVersionRepository.arch", repositoryInfo.arch));

                        content.add(version.versionNo);
                        content.add(BizUtil.getDictNameByValue("CompanyVersion.status", version.status));
                        if (!BizUtil.isNullOrEmpty(version.ownerAccountList)) {
                            content.add(version.ownerAccountList.stream().map(k -> k.name).collect(Collectors.joining("、")));
                        } else {
                            content.add("");
                        }
                        content.add(DateUtil.formatDate(version.startTime, "yyyy-MM-dd"));
                        content.add(DateUtil.formatDate(version.endTime, "yyyy-MM-dd"));
                        content.add(BizUtil.cleanHtml(version.remark));

                        data.contents.add(content);
                    });
                } else {*/
                List<String> content = new ArrayList<>();
                lastRowSpanIndex++;
                content.add(repositoryInfo.name);
                if (!BizUtil.isNullOrEmpty(repositoryInfo.ownerAccountList)) {
                    content.add(repositoryInfo.ownerAccountList.stream().map(k -> k.name).collect(Collectors.joining("、")));
                } else {
                    content.add("");
                }
                if (!BizUtil.isNullOrEmpty(repositoryInfo.ownerDepartmentList)) {
                    content.add(repositoryInfo.ownerDepartmentList.stream().map(k -> k.name).collect(Collectors.joining("、")));
                } else {
                    content.add("");
                }
//                content.add(repositoryInfo.ownerDepartmentName);
                content.add(repositoryInfo.businessLeader);
                content.add(repositoryInfo.department);
                content.add(repositoryInfo.latest);
                content.add(DateUtil.formatDate(repositoryInfo.releaseDate, "yyyy-MM-dd"));
                content.add(BizUtil.getDictNameByValue("CompanyVersionRepository.status", repositoryInfo.status));
                content.add(BizUtil.getBooleanValue(repositoryInfo.isArch332n));
                content.add(BizUtil.getDictNameByValue("CompanyVersionRepository.arch", repositoryInfo.arch));
                content.add(repositoryInfo.description);
//                    content.add("");
//                    content.add("");
//                    content.add("");
//                    content.add("");
//                    content.add("");
//                    content.add("");

                data.contents.add(content);
//                }
            }
            ret.fileName = "系统清单.xlsx";
            ret.tableData = data;
            return ret;
        }

        @Override
        public void importCompanyVersionRepositoryFromExcel(String token, String fileId) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_创建版本库, account.companyId);
            Attachment attachment = dao.getAttachmentByUuid(fileId);
            BizUtil.asserts(null != attachment, "文件不存在");
            java.io.File file = FileServiceManager.get().download(fileId);
            List<CreateCompanyVersionRepositoryReq> dataList = new ArrayList<>();
            try {
                dataList = ExcelUtils.readExcel(CreateCompanyVersionRepositoryReq.class, new FileInputStream(file));
            } catch (Exception e) {
                logger.error(e);
                throw new AppException("文件解析失败");
            }
            BizUtil.asserts(!BizUtil.isNullOrEmpty(dataList), "请勿导入空文件");
            AccountQuery aq = new AccountQuery();
            setupQuery(account, aq);
            aq.pageSize = Integer.MAX_VALUE;
            List<Account> accountList = dao.getList(aq, Sets.newHashSet("id", "name"));
            Map<String, Integer> accountMap = new HashMap<>();
            if (!BizUtil.isNullOrEmpty(accountList)) {
                accountMap = accountList.stream().collect(Collectors.toMap(k -> k.name, v -> v.id, (key1, key2) -> key2));
            }
            DepartmentQuery dq = new DepartmentQuery();
            setupQuery(account, dq);
            dq.pageSize = Integer.MAX_VALUE;
            dq.type = Department.TYPE_组织架构;
            List<Department> departmentList = dao.getList(dq, Sets.newHashSet("id", "name"));
            Map<String, Integer> departmentMap = new HashMap<>();
            if (!BizUtil.isNullOrEmpty(departmentList)) {
                departmentMap = departmentList.stream().collect(Collectors.toMap(k -> k.name, v -> v.id, (key1, key2) -> key2));
            }
            for (CreateCompanyVersionRepositoryReq repositoryReq : dataList) {
                CompanyVersionRepositoryInfo repository = BeanUtil.copyTo(CompanyVersionRepositoryInfo.class, repositoryReq);
                BizUtil.asserts(!BizUtil.isNullOrEmpty(repositoryReq.name), "系统名称不可为空");
                if (!BizUtil.isNullOrEmpty(repositoryReq.ownerAccountName)) {
                    List<String> accounts = BizUtil.splitList(repositoryReq.ownerAccountName);
                    repository.ownerAccountIdList = new ArrayList<>();
                    if (!BizUtil.isNullOrEmpty(accounts)) {
                        for (String an : accounts) {
                            int ownerAccountId = accountMap.getOrDefault(an, 0);
                            if (ownerAccountId > 0) {
                                repository.ownerAccountIdList.add(ownerAccountId);
                            }
                        }
                    }
                }

                if (!BizUtil.isNullOrEmpty(repositoryReq.ownerDepartmentName)) {
                    List<String> depts = BizUtil.splitList(repositoryReq.ownerDepartmentName);
                    repository.ownerDepartmentIdList = new ArrayList<>();
                    if (!BizUtil.isNullOrEmpty(depts)) {
                        for (String an : depts) {
                            int ownerDepartmentId = departmentMap.getOrDefault(an, 0);
                            if (ownerDepartmentId > 0) {
                                repository.ownerDepartmentIdList.add(ownerDepartmentId);
                            }
                        }
                    }
//                    repository.ownerDepartmentId = departmentMap.getOrDefault(repositoryReq.ownerDepartmentName, 0);
                }

                repository.releaseDate = DateUtil.parseDateTimeFromExcel(repositoryReq.releaseDateStr);
                repository.arch = BizUtil.getDictValueByName("CompanyVersionRepository.arch", repositoryReq.archName);
                repository.status = BizUtil.getDictValueByName("CompanyVersionRepository.status", repositoryReq.statusName);
                repository.isArch332n = !BizUtil.isNullOrEmpty(repositoryReq.isArch332nName) && !repositoryReq.isArch332nName.contains("否");
                addCompanyVersionRepository0(account, repository);
            }

        }

        //
        //
        @Transaction
        @Override
        public void kaptcha(String sign, String code) {
            Kaptcha verifyCode = dao.getByField(Kaptcha.class, "sign", sign);
            Date now = new Date();
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(java.util.Calendar.MINUTE, 5);//5分钟有效
            if (verifyCode == null) {
                verifyCode = new Kaptcha();
                verifyCode.type = Kaptcha.TYPE_图形验证码;
                verifyCode.sign = sign;
                verifyCode.code = code;
                verifyCode.createTime = new Date();
                verifyCode.validTime = calendar.getTime();
                dao.add(verifyCode);
            } else {
                //20200214新增
                KaptchaLog log = KaptchaLog.create(verifyCode);
                dao.add(log);
                //
                verifyCode.code = code;
                verifyCode.createTime = new Date();
                verifyCode.validTime = calendar.getTime();
                dao.update(verifyCode);
            }
        }

        @Override
        public List<Config> getConfigList(String token, ConfigQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_系统设置);
            bizService.checkIsPrivateDeploy(account);
            query.nameSort = Query.SORT_TYPE_ASC;
            return dao.getAll(query);
        }

        @Transaction
        @Override
        public void updateConfig(String token, Config bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkIsPrivateDeploy(account);
            bizService.checkCompanyPermission(account, Permission.ID_系统设置);
            Config old = dao.getExistedByIdForUpdate(Config.class, bean.id);
            //
            bizService.addOptLog(account, old.id, old.name,
                    OptLog.EVENT_ID_编辑配置, "名称:" + old.name + ",旧值:" + old.value + ",新值:" + bean.value);
            //
            old.value = bean.value;
            old.description = bean.description;
            dao.updateSpecialFields(old, "value", "description");
            //
            List<Config> configs = dao.getAll(Config.class);
            GlobalConfig.setupConfig(configs);
        }

        @Override
        public HeartbeatInfo heartbeat(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            HeartbeatInfo info = new HeartbeatInfo();
            AccountNotificationQuery query = new AccountNotificationQuery();
            query.accountId = account.id;
            query.isWebSend = false;
            query.pageSize = 20;
            query.expectPushTimeEnd = new Date();
            query.orderType = BizQuery.ORDER_TYPE_ID_ASC;
            info.notificationList = dao.getList(query);
            info.unReadNotificationNum = getUnReadAccountNotificationCount(account);
            info.myReportSubmitNum = dao.getMyReportSubmitNum(account.id);
            info.myReportAuditNum = dao.getMyReportAuditNum(account.id);
            //
            try {//更新最后登录时间
                if (!DateUtil.isSameDay(account.lastLoginTime, new Date())) {
                    account.lastLoginTime = new Date();
                    dao.updateSpecialFields(account, "lastLoginTime");
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            //
            return info;
        }

        @Override
        @Transaction
        public void sendNotificationsLater(String token, List<Integer> ids) {
            for (Integer id : ids) {
                AccountNotification bean = dao.getExistedByIdForUpdate(AccountNotification.class, id);
                if (bean.type != AccountNotificationSetting.TYPE_日历提醒) {
                    throw new AppException("参数错误");
                }
                bean.expectPushTime = DateUtil.addMinute(8);
                bean.isWebSend = false;
                dao.update(bean);
            }
        }

        @Override
        @Transaction
        public void readNotificationList(String token, List<Integer> ids) {
            for (Integer id : ids) {
                AccountNotification bean = dao.getExistedByIdForUpdate(AccountNotification.class, id);
                if (bean.isWebSend) {
                    return;
                }
                bean.isWebSend = true;
                dao.updateSpecialFields(bean, "isWebSend");
            }
        }

        //
        @Transaction
        @Override
        public Attachment addAttachment(String token, Attachment bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bean.companyId = account.companyId;
            bean.createAccountId = account.id;
            BizUtil.checkValid(bean);
            dao.add(bean);
            return bean;
        }

        @Override
        public Attachment getAttachmentByUuid(String token, String uuid) {
            Account account = bizService.getExistedAccountByToken(token);
            Attachment bean = dao.getAttachmentByUuid(uuid);
            if (bean == null) {
                return null;
            }
            bizService.checkPermission(account, bean.companyId);
            return bean;
        }

        @Override
        public Attachment queryAttachmentByUuid(String token, String uuid) {
            return dao.getAttachmentByUuid(uuid);
        }

        @Transaction
        @Override
        public void addTaskAttachment(String token, String uuid, int taskId) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.addTaskAttachment0(account, uuid, taskId, false);
        }

        @Transaction
        @Override
        public void addTaskWiki(String token, String uuid, int taskId) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.addTaskAttachment0(account, uuid, taskId, true);
        }

        @Transaction
        @Override
        public void deleteTaskAttachment(String token, int taskId, int assocaiteAttachmentId, int type) {
            deleteTaskAttachment0(token, taskId, assocaiteAttachmentId, false, type);
        }

        public void deleteTaskAttachment0(String token, int taskId, int assocaiteAttachmentId, boolean force, int type) {
            Account account = bizService.getExistedAccountByToken(token);

            TaskInfo task = dao.getExistedById(TaskInfo.class, taskId);
            //强制删除
            if (!force) {
                bizService.checkProjectPermission(account, task.projectId, "task_edit_" + task.objectType);
            }

            //type=1 关联文件  type=2关联wiki
            if (type == 1) {
                AttachmentAssociated old = dao.getAttachmentAssociatedByTaskIdAttachmentId(taskId, assocaiteAttachmentId);
                if (!force) {
                    bizService.checkPermission(account, old.companyId);
                }
                dao.deleteById(AttachmentAssociated.class, old.id);
                //
                AttachmentInfo attachment = dao.getExistedByIdForUpdate(AttachmentInfo.class, old.attachmentId);
                attachment.isDelete = true;
                dao.update(attachment);
                //记录日志
                bizService.addChangeLog(account, 0, taskId, ChangeLog.TYPE_删除附件, JSONUtil.toJson(attachment));
                bizService.addChangeLog(account, task.projectId, 0, ChangeLog.TYPE_删除附件, bizService.toSimpleJson(task));
                Map<String, Object> map = new HashMap<>();
                map.put("attachment", attachment);
                bizService.sendNotificationForTask(account,
                        task,
                        AccountNotificationSetting.TYPE_对象删除附件,
                        "对象删除附件",
                        map);
                //
                bizService.addOptLog(account, assocaiteAttachmentId, attachment.name,
                        OptLog.EVENT_ID_对象删除附件, "");
            } else {
                WikiAssociated wikiAsso = dao.getWikiAssociatedByTaskIdAttachmentId(assocaiteAttachmentId, taskId);
                if (null != wikiAsso) {
                    if (!force) {
                        bizService.checkPermission(account, wikiAsso.companyId);
                    }
                    dao.deleteById(WikiAssociated.class, wikiAsso.id);
                    WikiPage wikiPage = dao.getExistedByIdForUpdate(WikiPage.class, wikiAsso.wikiPageId);
//                    wikiPage.isDelete = true;
//                    dao.update(wikiPage);
                    //记录日志
                    bizService.addChangeLog(account, 0, taskId, ChangeLog.TYPE_删除附件, JSONUtil.toJson(wikiPage));
                    bizService.addChangeLog(account, task.projectId, 0, ChangeLog.TYPE_删除附件, bizService.toSimpleJson(task));
                    Map<String, Object> map = new HashMap<>();
                    map.put("attachment", wikiPage);
                    bizService.sendNotificationForTask(account,
                            task,
                            AccountNotificationSetting.TYPE_对象删除附件,
                            "对象删除关联WIKI",
                            map);
                    //
                    bizService.addOptLog(account, assocaiteAttachmentId, wikiPage.name,
                            OptLog.EVENT_ID_对象删除附件, "");
                }

            }
        }

        //
        @Transaction
        @Override
        public String register(RegisterInfo info) {
            if (GlobalConfig.isPrivateDeploy) {
                throw new AppException("私有化版本不支持注册功能,请联系管理员");
            }
            if (StringUtil.isEmpty(info.mobileNo)) {
                throw new AppException("手机号不能为空");
            }
            if (StringUtil.isEmpty(info.userName) || StringUtil.isEmpty(info.userName.trim())) {
                throw new AppException("用户名不能为空");
            }
            if (StringUtil.isEmpty(info.name)) {
                throw new AppException("名称不能为空");
            }
            if (StringUtil.isEmpty(info.password)) {
                throw new AppException("密码不能为空");
            }
            if (StringUtil.isEmpty(info.code)) {
                throw new AppException("验证码不能为空");
            }
            info.userName = info.userName.trim();
            PatternUtil.checkUserName(info.userName);
            //检查图形验证码是否正确
            bizService.checkVerifyCode(info.sign, info.code);
            //
            Account account = dao.getAccountByUserName(info.userName);
            if (account != null && account.isActivated) {
                throw new AppException("用户名已经被注册");
            }
            if (account == null) {
                account = dao.getAccountByMobileNoForUpdate(info.mobileNo);
                if (account != null && account.isActivated) {
                    throw new AppException("手机号已经被注册");
                }
            }
            if (account == null) {
                account = new Account();
                account.isActivated = false;
                account.status = Account.STATUS_有效;
                account.uuid = BizUtil.randomUUID();
                account.name = info.name.trim();
                account.userName = info.userName;
                account.pinyinName = PinYinUtil.getHanziPinYin(account.name);
                account.mobileNo = info.mobileNo.trim();
                account.password = BizUtil.encryptPassword(info.password.trim());
                account.encryptPassword = TripleDESUtil.encrypt(info.password.trim(), ConstDefine.GLOBAL_KEY);
                BizUtil.checkValid(account);
                BizUtil.checkUniqueKeysOnAdd(dao, account);
                dao.add(account);
            }
            //
            if (!StringUtil.isEmpty(info.inviteCode)) {
                CompanyMemberInvite invite = dao.getCompanyMemberInviteByUuidForUpdate(info.inviteCode);
                if (invite != null && (!invite.isAgree) && invite.invitedAccountId == 0) {
                    joinProjectMember0(account, invite);
                }
            }
            //
            if (!StringUtil.isEmpty(info.inviteNumberCode)) {
                CompanyMemberInviteCode inviteCode = dao.getCompanyMemberInviteCodeForUpdate(info.inviteNumberCode);
                if (inviteCode != null && inviteCode.status == CompanyMemberInviteCode.STATUS_未使用) {
                    Account inviteAccount = dao.getExistedById(Account.class, inviteCode.createAccountId);
                    joinCompanyMember0(inviteAccount, account, inviteCode.companyId);
                    //
                    inviteCode.accountId = account.id;
                    inviteCode.status = CompanyMemberInviteCode.STATUS_已使用;
                    inviteCode.useTime = new Date();
                    dao.update(inviteCode);
                }
            }
            //
            if (ldapService != null) {
                ldapService.addAccount(account.userName, account.password);
            }
            //
            bizService.addOptLog(account, 0, "", OptLog.EVENT_ID_注册, "");
            //
            return account.uuid;
        }

        //
        @Transaction
        @Override
        public String registerNew(RegisterInfo info) {
            if (GlobalConfig.isPrivateDeploy) {
                throw new AppException("私有化版本不支持注册功能,请联系管理员");
            }
            if (StringUtil.isEmpty(info.mobileNo)) {
                throw new AppException("手机号不能为空");
            }
            if (StringUtil.isEmpty(info.userName) || StringUtil.isEmpty(info.userName.trim())) {
                throw new AppException("用户名不能为空");
            }
            if (StringUtil.isEmpty(info.name)) {
                throw new AppException("名称不能为空");
            }
            if (StringUtil.isEmpty(info.password)) {
                throw new AppException("密码不能为空");
            }
            if (StringUtil.isEmpty(info.code)) {
                throw new AppException("验证码不能为空");
            }
            info.userName = info.userName.trim();
            info.mobileNo = info.mobileNo.trim();
            PatternUtil.checkUserName(info.userName);
            //检查图形验证码是否正确
            bizService.checkVerifyCode(info.mobileNo, info.code);
            //
            Account account = dao.getAccountByUserName(info.userName);
            if (account != null) {
                throw new AppException("用户名已经被注册");
            }
            account = dao.getAccountByMobileNoForUpdate(info.mobileNo);
            if (account != null) {
                throw new AppException("手机号已经被注册");
            }
            String larkOpenId = null;
            String larkTenantKey = null;
            Company larkCompany = null;//larkCompany
            try {
                if (!StringUtil.isEmptyWithTrim(info.larkAuthorizeData)) {
                    String decode = new String(Base64.getDecoder().decode(info.larkAuthorizeData));
                    LarkAuthorize larkAuthorize = JSONUtil.fromJson(decode, LarkAuthorize.class);
                    //larkAuthorize.openId=new String(Base64.getUrlDecoder().decode(larkAuthorize.openId));
                    larkOpenId = TripleDESUtil.decrypt(larkAuthorize.openId, ConstDefine.GLOBAL_KEY);
                    larkTenantKey = larkAuthorize.tenantKey;
                    String companyUuid = larkAuthorize.cUuid;
                    if (!StringUtil.isEmpty(companyUuid)) {
                        larkCompany = dao.getByUuid(Company.class, companyUuid);
                    }
                    Account old = dao.getAccountByLarkOpenId(larkOpenId);
                    if (old != null) {
                        logger.warn("larkOpenId existed {}", larkOpenId);
                        larkOpenId = null;
                        larkTenantKey = null;
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            account = new Account();
            account.isActivated = true;
            account.status = Account.STATUS_有效;
            account.uuid = BizUtil.randomUUID();
            account.name = info.name.trim();
            account.userName = info.userName;
            account.pinyinName = PinYinUtil.getHanziPinYin(account.name);
            account.mobileNo = info.mobileNo.trim();
            account.password = BizUtil.encryptPassword(info.password.trim());
            account.encryptPassword = TripleDESUtil.encrypt(info.password.trim(), ConstDefine.GLOBAL_KEY);
            account.larkOpenId = larkOpenId;
            account.larkTenantKey = larkTenantKey;
            account.registerIp = info.ip;
            BizUtil.checkValid(account);
            BizUtil.checkUniqueKeysOnAdd(dao, account);
            dao.add(account);
            //
            if (!StringUtil.isEmpty(info.inviteCode)) {
                CompanyMemberInvite invite = dao.getCompanyMemberInviteByUuidForUpdate(info.inviteCode);
                if (invite != null && (!invite.isAgree) && invite.invitedAccountId == 0) {
                    joinProjectMember0(account, invite);
                }
            }
            //
            if (!StringUtil.isEmpty(info.inviteNumberCode)) {
                CompanyMemberInviteCode inviteCode = dao.getCompanyMemberInviteCodeForUpdate(info.inviteNumberCode);
                if (inviteCode != null && inviteCode.status == CompanyMemberInviteCode.STATUS_未使用) {
                    Account inviteAccount = dao.getExistedById(Account.class, inviteCode.createAccountId);
                    joinCompanyMember0(inviteAccount, account, inviteCode.companyId);
                    //
                    inviteCode.accountId = account.id;
                    inviteCode.status = CompanyMemberInviteCode.STATUS_已使用;
                    inviteCode.useTime = new Date();
                    dao.update(inviteCode);
                }
            }
            //
            if (larkCompany != null) {
                joinCompanyMember0(null, account, larkCompany.id);
                Project testProject = dao.getProjectByCompanyIdName(larkCompany.id, "示例项目");
                Account inviteAccount = dao.getById(Account.class, testProject.createAccountId);
                if (inviteAccount != null) {
                    joinProject(inviteAccount, account, testProject.id, "larkCompany");
                }
            }
            //
            if (ldapService != null) {
                ldapService.addAccount(account.userName, account.password);
            }
            //
            bizService.addOptLog(account, 0, "", OptLog.EVENT_ID_注册, "");
            //
            AccountToken at = loginSuccess(account);
            //绑定飞书成功
            if (!StringUtil.isEmpty(account.larkOpenId)) {
                String finalLarkOpenId = account.larkOpenId;
                String finalLarkTenantKey = account.larkTenantKey;
                AutoTranscationCallback.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        larkService.pushBindSuccessMessage(finalLarkOpenId, null,
                                finalLarkTenantKey, null);
                    }
                });
            }
            //
            return at.token;
        }

        //
        private void joinCompanyMember0(Account inviteAccount, Account invitedAccount, int newCompanyId) {
            //先判断是否已经是企业成员
            CompanyMember companyMember = dao.getCompanyMemberByAccountIdCompanyId(invitedAccount.id, newCompanyId);
            if (companyMember == null) {
                Role role = dao.getRoleByCompanyIdTypeName(newCompanyId, Role.TYPE_全局, Role.NAME_成员);
                Department root = dao.getDepartmentByCompanyIdLevel(newCompanyId, 1);
                bizService.addOrUpdateCompanyAccount(newCompanyId, inviteAccount, invitedAccount,
                        Collections.singletonList(role.id), Collections.singletonList(root.id));
                //
                invitedAccount.companyId = newCompanyId;
                dao.update(invitedAccount);
            }
        }

        //account是被邀请的人
        private void joinProjectMember0(Account account, CompanyMemberInvite invite) {
            Account inviteAccount = dao.getExistedById(Account.class, invite.accountId);
            int companyId = invite.companyId;
            //先判断是否已经是企业成员
            CompanyMember companyMember = dao.getCompanyMemberByAccountIdCompanyId(account.id, invite.companyId);
            if (companyMember == null) {
                Role role = dao.getRoleByCompanyIdTypeName(companyId, Role.TYPE_全局, Role.NAME_成员);
                Department root = dao.getDepartmentByCompanyIdLevel(companyId, 1);
                bizService.addOrUpdateCompanyAccount(invite.companyId, inviteAccount, account,
                        Collections.singletonList(role.id), Collections.singletonList(root.id));
                //
                account.companyId = invite.companyId;
                if (StringUtil.isEmpty(account.email) && invite.invitedEmail != null) {
                    Account old = dao.getAccountByEmail(invite.invitedEmail);
                    if (old == null) {
                        account.email = invite.invitedEmail;
                    }
                }
                dao.update(account);
            }
            //
            invite.isAgree = true;
            invite.invitedAccountId = account.id;
            dao.update(invite);
            //
            if (invite.projectId > 0) {//加入项目
                joinProject(inviteAccount, account, invite.projectId, "joinProjectMember0");
            }
        }

        private void joinProject(Account inviteAccount, Account account, int projectId, String source) {
            try {
                logger.info("joinProject inviteAccount:{} account:{} projectId:{} source:{}",
                        DumpUtil.dump(inviteAccount), DumpUtil.dump(account), projectId, source);
                Project project = dao.getExistedById(Project.class, projectId);
                if (!project.isDelete) {
                    Role role = dao.getRoleByCompanyIdTypeName(project.companyId, Role.TYPE_项目, Role.NAME_项目成员);
                    Set<Integer> accountIdSet = new HashSet<>();
                    accountIdSet.add(account.id);
                    Set<Integer> roleSet = new HashSet<>();
                    roleSet.add(role.id);
                    addProjectMembers0(inviteAccount, projectId, accountIdSet, roleSet, true, null);
                    bizService.addOptLog(account, project.id, 0, project.id, project.name, OptLog.EVENT_ID_加入项目, "");
                }
            } catch (Exception e) {
                logger.error("joinProject failed." + e.getMessage());
                logger.error(e.getMessage(), e);
            }
        }

        @Override
        @Transaction
        public void joinProjectMember(String token, String inviteCode) {
            Account account = bizService.getExistedAccountByToken(token);
            if (StringUtil.isEmpty(inviteCode)) {
                throw new AppException("邀请码不能为空");
            }
            CompanyMemberInvite invite = dao.getCompanyMemberInviteByUuidForUpdate(inviteCode);
            if (invite == null) {
                logger.error("joinProjectMember failed.account:{} inviteCode:{}",
                        account.id, inviteCode);
                throw new AppException("邀请码不存在");
            }
            if (invite.isAgree) {//已经邀请了
                return;
            }
            if (invite.invitedAccountId > 0 && account.id != invite.invitedAccountId) {
                throw new AppException("您不是被邀请人");
            }
            if (account.id == invite.invitedAccountId) {
                throw new AppException("您已注册成功，无需再次接受邀请");
            }
            joinProjectMember0(account, invite);
        }

        @Transaction
        @Override
        public void activateAccount(int type, String accountUuid) {
            Account account = dao.getAccountByUuid(accountUuid);
            if (account == null) {
                throw new AppException("账号不存在");
            }
            if (account.isActivated) {
                throw new AppException("账号已经激活");
            }
            if (type == 1) {//发送手机验证码
                bizService.sendVerifyCode(account.mobileNo);
            } else {
                throw new AppException("参数错误");
            }
        }

        @Transaction
        @Override
        public void confirmActivateAccount(int type, String accountUuid, String code) {
            Account account = dao.getAccountByUuidForUpdate(accountUuid);
            if (account == null) {
                throw new AppException("账号不存在");
            }
            if (account.isActivated) {
                return;
            }
            if (type == 1) {
                bizService.checkMobileVerifyCode(account, account.mobileNo, code);
            } else {
                throw new AppException("参数错误");
            }
            account.isActivated = true;
            dao.update(account);
            //
            bizService.addOptLog(account, 0, "", OptLog.EVENT_ID_激活账号, "");
        }

        @Transaction
        @Override
        public LoginResult login(String userName, String password) {
            LoginResult result = new LoginResult();
            Account account = dao.getAccountByUserNameForUpdate(userName);
            if (account == null) {
                account = dao.getAccountByMobileNoForUpdate(userName);
            }
            if (account == null) {
                account = dao.getAccountByEmailForUpdate(userName);
            }
            if (account == null) {
                result.errCode = BizExceptionCode.CODE_用户名或密码错误;
                result.errMsg = "用户名或密码错误";
                return result;
            }
            try {
                bizService.checkPasswordValid(account, password);
            } catch (AppException e) {
                if (!GlobalConfig.authLdapEnable) {
                    loginPasswordError(account, result);
                    return result;
                }
                try {
                    ldapAuthService.auth(userName, password);
                    if (account.needUpdatePassword) {
                        account.needUpdatePassword = false;
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                    loginPasswordError(account, result);
                    return result;
                }
            }
            boolean isOk = checkLogin(result, account);
            if (!isOk) {
                return result;
            }
            try {
                AccountToken userToken = loginSuccess(account);
                bizService.addOptLog(account, 0, "", OptLog.EVENT_ID_账号密码登录, "");
                result.token = userToken.token;
                result.errCode = 0;
                result.errMsg = "SUCCESS";
                return result;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                result.errCode = BizExceptionCode.CODE_系统繁忙;
                result.errMsg = "系统繁忙，请稍后再试";
                return result;
            }
        }

        @Transaction
        @Override
        public LoginResult ssoLogin(String rollbackInfo) {
            LoginResult result = new LoginResult();
            String ssoLoginField = StrUtil.blankToDefault(GlobalConfig.ssoLoginField, "MobileNo");
            Object callbackResult ;
            try {
                callbackResult = HttpRollback.callback(GlobalConfig.ssoConfig, rollbackInfo);
            }catch (Exception e) {
                logger.error("BizAction.ssoLogin.callback error", e);
                result.errCode = BizExceptionCode.CODE_SSO回掉异常;
                result.errMsg = "SSO回掉异常" + e.getMessage();
                return result;
            }
            logger.info("BizAction.ssoLogin rollbackInfo:{}, callbackResult:{}", rollbackInfo, callbackResult);
            if (null == callbackResult) {
                result.errCode = BizExceptionCode.CODE_SSO回掉结果为空;
                result.errMsg = "SSO回掉结果为空";
                return result;
            }
            Account account ;
            if ("UserName".equalsIgnoreCase(ssoLoginField)) {
                account = dao.getAccountByUserName(String.valueOf(callbackResult));
            } else if ("MobileNo".equalsIgnoreCase(ssoLoginField)) {
                account = dao.getAccountByMobileNo(String.valueOf(callbackResult));
            } else {
                result.errCode = BizExceptionCode.CODE_未知SSO回掉结果类型;
                result.errMsg = "未知SSO回掉结果类型";
                return result;
            }
            if (null == account) {
                logger.error("ssoLogin error, ssoCallBackResultType:{}, callbackResult:{}", ssoLoginField, callbackResult);
                result.errCode = BizExceptionCode.CODE_SSO账号获取失败;
                result.errMsg = "账号获取失败，请联系管理员确认";
                return result;
            }
            
            boolean isOk = checkLogin(result, account);
            if (!isOk) {
                return result;
            }
            try {
                AccountToken userToken = loginSuccess(account);
                bizService.addOptLog(account, 0, "", OptLog.EVENT_ID_SSO登录, "");
                result.token = userToken.token;
                result.errCode = 0;
                result.errMsg = "SUCCESS";
                return result;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                result.errCode = BizExceptionCode.CODE_系统繁忙;
                result.errMsg = "系统繁忙，请稍后再试";
                return result;
            }
        }

        private boolean checkLogin(LoginResult result, Account account) {
            if (account.dailyLoginFailCount >= 10) {
                result.errCode = BizExceptionCode.CODE_登录失败次数超过今日上限;
                result.errMsg = "登录失败次数超过今日上限";
                return false;
            }
            if (!account.isActivated) {
                result.errCode = BizExceptionCode.CODE_账号尚未激活;
                result.errMsg = "账号尚未激活";
                return false;
            }
            if (account.status == Account.STATUS_无效) {
                result.errCode = BizExceptionCode.CODE_账号已被禁用;
                result.errMsg = "账号已被禁用";
                return false;
            }
            if (account.disableEndTime != null && account.disableEndTime.after(new Date())) {
                result.errCode = BizExceptionCode.CODE_账号已被锁定;
                result.errMsg = "账号已被锁定,解锁时间" + DateUtil.formatDate(account.disableEndTime, "MM-dd HH:mm");
                return false;
            }
            //
            if (account.companyId > 0) {
                Company company = dao.getExistedById(Company.class, account.companyId);
                if (bizService.isPrivateDeploy(company)) {//只针对私有化部署
                    if (bizService.isLicenseExpired(company.license) &&
                            bizService.isTrailLicense(company.license)) {
                        //对于试用license，到期之后，只有root用户可以登陆，其它用户将不能登陆。除非更新license。
                        if (account.userName != null && (!"root".equals(account.userName))) {
                            result.errCode = BizExceptionCode.CODE_授权已到期;
                            result.errMsg = "授权已到期";
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        @Transaction
        @Override
        public LoginResult loginByMobileNo(String mobileNo, String code) {
            LoginResult result = new LoginResult();
            if (StringUtil.isEmptyWithTrim(mobileNo)) {
                throw new AppException("手机号不能为空");
            }
            if (StringUtil.isEmptyWithTrim(code)) {
                throw new AppException("验证码不能为空");
            }
            Account account = dao.getAccountByMobileNoForUpdate(mobileNo);
            if (account == null) {
                result.errCode = BizExceptionCode.CODE_用户名或密码错误;
                result.errMsg = "手机号未注册";
                return result;
            }
            boolean isOk = checkLogin(result, account);
            if (!isOk) {
                return result;
            }
            bizService.checkMobileVerifyCode(account, mobileNo, code);
            try {
                AccountToken userToken = loginSuccess(account);
                bizService.addOptLog(account, 0, "", OptLog.EVENT_ID_手机验证码登录, "");
                result.token = userToken.token;
                result.errCode = 0;
                result.errMsg = "SUCCESS";
                return result;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                result.errCode = BizExceptionCode.CODE_系统繁忙;
                result.errMsg = "系统繁忙，请稍后再试";
                return result;
            }
        }

        @Transaction
        @Override
        public LoginResult loginbyAdAccount(String userName, String password) {
            LoginResult result = new LoginResult();
            if (StringUtil.isEmptyWithTrim(userName)) {
                throw new AppException("AD域账号不能为空");
            }
            if (StringUtil.isEmptyWithTrim(password)) {
                throw new AppException("AD域密码不能为空");
            }
            //AD域验证
            Map<String, Object> adAccount = LdapUtil.auth(userName, password);
            String adName = adAccount.get("adName").toString();
            if (BizUtil.isNullOrEmpty(adName)) {
                throw new AppException("请确认AD域账号是否存在");
            }
            Account account = dao.getAccountByAdNameForUpdate(adName);
            if (null == account) {
//                throw new AppException("您还未绑定AD域账号");
                //创建账号，状态无效(缺少组织架构和角色)
                account = new Account();
                account.adName = adName;
                account.name = adName.split("@")[0];
                account.userName = account.name;
                Account old = dao.getAccountByUserName(account.name);
                if (null != old) {
                    throw new AppException("用户名【" + account.name + "】已存在,若为同一人请在账号设置中绑定AD域账号");
                }
                if (null != adAccount.get("email")) {
                    account.email = adAccount.get("email").toString();
                }

                if (null != adAccount.get("telephoneNumber")) {
                    account.mobileNo = adAccount.get("telephoneNumber").toString();
                }

                account.isActivated = false;
                account.status = Account.STATUS_有效;
                account.uuid = BizUtil.randomUUID();
                account.pinyinName = PinYinUtil.getHanziPinYin(account.name);
                account.password = BizUtil.encryptPassword(password.trim());
                account.encryptPassword = TripleDESUtil.encrypt(password.trim(), ConstDefine.GLOBAL_KEY);
                //私有部署初始账号
                Account optAccount = dao.getById(Account.class, 1);
                if (!bizService.isPrivateDeploy(optAccount)) {
                    throw new AppException("仅私有部署版本支持此功能");
                }
                account.createAccountId = optAccount.id;
                account.companyId = optAccount.companyId;
                account.needUpdatePassword = false;
                dao.add(account);

                CompanyMember companyMember = new CompanyMember();
                companyMember.accountId = account.id;
                companyMember.companyId = account.companyId;
                dao.add(companyMember);
                // 公司人数+1
                Company company = dao.getExistedByIdForUpdate(Company.class, companyMember.companyId);
                company.memberNum = dao.getCurrMemberNum(company.id);
                if (company.memberNum > company.maxMemberNum) {
                    throw new AppException("成员数量达到上限" + company.maxMemberNum + "，不能新增成员");
                }
                dao.updateSpecialFields(company, "memberNum");
            }
            try {
                AccountToken userToken = loginSuccess(account);
                bizService.addOptLog(account, 0, "", OptLog.EVENT_ID_AD域账号登录, "AD域账号登录");
                result.token = userToken.token;
                result.errCode = 0;
                result.errMsg = "SUCCESS";
                return result;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                result.errCode = BizExceptionCode.CODE_系统繁忙;
                result.errMsg = "系统繁忙，请稍后再试";
                return result;
            }
        }

        @Transaction
        @Override
        public void bindAdAccount(String token, String userName, String password) {
            Account account = bizService.getAccountByToken(token);
            if (BizUtil.isNullOrEmpty(userName)) {
                throw new AppException("请输入AD域账号");
            }
            if (BizUtil.isNullOrEmpty(password)) {
                throw new AppException("请输入AD域账号密码");
            }

            Map<String, Object> adAccount = LdapUtil.auth(userName, password);
            account.adName = adAccount.get("adName").toString();

            Account acc = dao.getAccountByAdNameForUpdate(account.adName);
            if (null != acc) {
                acc.isActivated = false;
                String[] splits = acc.userName.split("_");
                acc.userName = (splits.length > 0 ? splits[0] : acc.userName) + "_bind";
                dao.updateSpecialFields(acc, "isActivated", "userName");
                logger.info("bind AD account :{}", DumpUtil.dump(acc));
            }

            dao.updateSpecialFields(account, "adName");

        }


        @Override
        public void unbindAdAccount(String token) {
            Account optAccount = bizService.getAccountByToken(token);
            optAccount.adName = null;
            dao.updateSpecialFields(optAccount, "adName");
        }

        @Transaction
        @Override
        public String openapiLogin(String userName, String accessToken) {
            if (StringUtil.isEmpty(userName) || StringUtil.isEmpty(accessToken)) {
                throw new AppException("认证失败");
            }
            Account account = dao.getAccountByUserNameForUpdate(userName);
            if (account == null) {
                account = dao.getAccountByMobileNoForUpdate(userName);
            }
            if (account == null) {
                throw new AppException("账号不存在");
            }
            if (!account.isActivated) {
                throw new AppException("账号尚未激活");
            }
            if (account.status == Account.STATUS_无效) {
                throw new AppException("账号已被禁用");
            }
            if (!account.accessToken.equals(accessToken)) {
                logger.error("account.accessToken:{} accessToken:{}", account.accessToken, accessToken);
                throw new AppException("认证失败");
            }
            AccountToken userToken = dao.getAccountTokenByAccountId(account.id);
            if (userToken == null) {
                userToken = new AccountToken();
                userToken.accountId = account.id;
                userToken.token = BizUtil.randomUUID();
                userToken.createTime = new Date();
                userToken.updateTime = new Date();
                dao.add(userToken);
            }
            return userToken.token;
        }

        private void loginPasswordError(Account account, LoginResult result) {
            account.dailyLoginFailCount++;
            dao.updateSpecialFields(account, "dailyLoginFailCount");
            result.errCode = BizExceptionCode.CODE_用户名或密码错误;
            result.errMsg = "用户名或密码错误";
        }

        private AccountToken loginSuccess(Account account) {
            AccountToken userToken = dao.getAccountTokenByAccountId(account.id);
            if (userToken == null) {
                userToken = new AccountToken();
                userToken.accountId = account.id;
                userToken.token = BizUtil.randomUUID();
                userToken.createTime = new Date();
                userToken.updateTime = new Date();
                dao.add(userToken);
                //
                if (account.id == 1 && GlobalConfig.isPrivateDeploy) {//root且私有部署
                    addExampleProjects(userToken.token, true);
                }
            } else {
//				userToken.token = BizUtil.randomUUID();
//				dao.update(userToken);
            }
            account.lastLoginTime = new Date();
            account.dailyLoginFailCount = 0;
            dao.update(account);
            return userToken;
        }

        @Override
        @Transaction
        public void logout(String token) {
            Account account = bizService.getAccountByToken(token);
            if (account == null) {
                return;
            }
            AccountToken userToken = dao.getAccountTokenByAccountIdForUpdate(account.id);
            userToken.token = BizUtil.randomUUID();
            dao.update(userToken);
            //
            bizService.addOptLog(account, 0, "", OptLog.EVENT_ID_登出, "");
        }

        @Override
        public void resetAccountDailyLoginFailCount(String token, int accountId) {
            Account optAccount = bizService.getExistedAccountByToken(token);
            if (!bizService.isCompanyAdmin(optAccount)) {
                throw new AppException("权限不足");
            }
            Account account = dao.getExistedByIdForUpdate(Account.class, accountId);
            if (account.companyId != optAccount.companyId) {
                throw new AppException("权限不足");
            }
            if (account.dailyLoginFailCount == 0) {
                return;
            }
            account.dailyLoginFailCount = 0;
            dao.updateSpecialFields(account, "dailyLoginFailCount");
        }

        @Transaction
        @Override
        public void switchCompany(String token, String companyUuid) {
            Account account = bizService.getExistedAccountByTokenForUpdate(token);
            CompanyInfo company = dao.getCompanyInfoByUuid(companyUuid);
            CompanyMember member = dao.getCompanyMemberByAccountIdCompanyId(account.id, company.id);
            if (member == null) {
                throw new AppException("权限不足");
            }
            if (company.status != Company.STATUS_许可中) {
                throw new AppException("权限不足");
            }
            account.companyId = company.id;
            dao.update(account);
        }

        @Override
        public LoginInfo getLoginInfo(String token) {
            AccountInfo account = bizService.getExistedAccountInfoByToken(token);
            LoginInfo info = new LoginInfo();
            info.account = account;
            info.account.password = null;
            info.account.encryptPassword = null;
            //
            CompanyQuery query = new CompanyQuery();
            query.accountId = account.id;
            query.isDelete = false;
            query.pageSize = 10000;

            boolean isPrivateDeploy = true;
            info.companyList = dao.getList(query, "license", "licenseId");
            if (account.companyId > 0 && !BizUtil.isNullOrEmpty(info.companyList)) {
                Optional<CompanyInfo> opt = info.companyList.stream().filter(k -> k.id == account.companyId).findFirst();
                if (opt.isPresent()) {
                    Company company = opt.get();
                    isPrivateDeploy = company.version == Company.VERSION_私有部署版;
                    if (!DateUtil.isSameDay(company.lastLoginTime, new Date())) {
                        company.lastLoginTime = new Date();
                        dao.updateSpecialFields(company, "lastLoginTime");
                    }
                }
            }
            //
            ProjectMemberQuery memberQuery = new ProjectMemberQuery();
            info.projectList = getMyProjectList0(account, memberQuery);
            //
            info.dataDicts = BizTaskJobs.dictMap;
            info.owaUrl = GlobalConfig.owaUrl;
            info.officeType = GlobalConfig.officeType;
            //
//			info.mentionList=dao.getAccountMentionList(account.companyId);
            info.permissionList = bizService.getAllGlobalPermission(account);
            info.roles = bizService.getMyCompanyRoleList(account);
            String webEventPort = GlobalConfig.getValue("webevent.port");
            if (!StringUtil.isEmpty(webEventPort)) {
                info.webEventPort = Integer.parseInt(webEventPort);
            }
            CompanyMember companyMember = dao.getCompanyMemberByAccountIdCompanyId(account.id, account.companyId);
            if (companyMember != null) {
                info.departmentList = companyMember.departmentList;
            } else {
                info.departmentList = new ArrayList<>();
            }
            //
            if (account.accessToken == null) {
                refreshAccessToken(token);
            }
            //
            try {
                if (!DateUtil.isSameDay(account.lastLoginTime, new Date())) {
                    account.lastLoginTime = new Date();
                    dao.updateSpecialFields(account, "lastLoginTime");
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            info.reportConfig = dao.getCompanyReportConfig(account.companyId);
            info.uploadFileSize = Integer.parseInt(GlobalConfig.getValue("system.maxUploadFileSize", "100"));
            //是否开启分片上传功能
            info.showMultiUpload = Boolean.parseBoolean(GlobalConfig.getValue("file.multi.upload", "true"));
            //

            //部门boss用户获取他可访问的项目列表
            if (account.superBoss == Account.BOSS_部门_读写 || account.superBoss == Account.BOSS_部门_只读) {
                info.accessProjectList = bizService.getAccountAccessProjectList(account, true, false);
            }

            AccountHomeSetting homeSetting = dao.getAccountHomeSetting(account.id, account.companyId);
            if (null != homeSetting && homeSetting.visible) {
                info.showNewHome = true;
            }
            info.globalKanbanSort = Boolean.parseBoolean(GlobalConfig.getValue("global.kanbanSort", "false"));
            info.isAdSet = Boolean.parseBoolean(GlobalConfig.getValue("auth.ldap.enable", "false"));
            info.isSupplierEnable = Boolean.parseBoolean(GlobalConfig.getValue("supplier.func.open", "false"));
            info.isAttendanceEnable = Boolean.parseBoolean(GlobalConfig.getValue("attendance.func.open", "false"));
            info.isSpiEnable = Double.parseDouble(GlobalConfig.getValue("project.spi.value", "0")) > 0 && isPrivateDeploy;
            info.isAddMemberLimit = Boolean.parseBoolean(GlobalConfig.getValue("project.manager.add.manager", "false"));
            info.isRepositoryVersionExtension = Boolean.parseBoolean(GlobalConfig.getValue("company.version.extension", "false"));
            return info;
        }

        @Transaction
        @Override
        public void createCompany(String token, Company bean) {
            Account account = bizService.getExistedAccountByTokenForUpdate(token);
            bean.uuid = BizUtil.randomUUID();
            BizUtil.checkUniqueKeysOnAdd(dao, bean);
            BizUtil.checkValid(bean);
            bean.createAccountId = account.id;
            bean.status = Company.STATUS_许可中;
            if (GlobalConfig.isPrivateDeploy) {
                bean.dueDate = DateUtil.getBeginOfDay(DateUtil.getNextDay(365));
            } else {
                bean.dueDate = DateUtil.parse("2030-1-1 0:0:0");
            }
            bean.isDelete = false;
            bean.version = GlobalConfig.isPrivateDeploy ? Company.VERSION_私有部署版 : Company.VERSION_云平台版;
            if (GlobalConfig.isPrivateDeploy && (!"root".equals(account.userName))) {
                throw new AppException("私有化版本只能由root账号来创建企业");
            }
            //20200206添加
            CompanyQuery query = new CompanyQuery();
            query.createAccountId = account.id;
            query.eqName = bean.name;
            query.isDelete = false;
            int count = dao.getListCount(query);
            if (count > 0) {
                throw new AppException("企业名称已经存在");
            }
            //
            if (bean.version == Company.VERSION_云平台版) {
                bean.maxMemberNum = 20;
            } else {
                bean.maxMemberNum = 1000;
            }
            bean.memberNum = 0;
            dao.add(bean);
            int companyId = bean.id;
            //
            account.companyId = bean.id;
            dao.update(account);
            //
            TaskSerialNo serialNo = new TaskSerialNo();
            serialNo.companyId = bean.id;
            serialNo.serialNo = 10000;
            dao.add(serialNo);
            //
            Department rootDept = new Department();
            rootDept.level = 1;
            rootDept.name = bean.name;
            rootDept.createAccountId = account.id;
            rootDept.type = Department.TYPE_组织架构;
            rootDept.companyId = companyId;
            dao.add(rootDept);
            //
            //添加四个系统角色
            addSystemRole(account, bean.id, Role.TYPE_全局, Role.NAME_管理员, dao.getAllGlobalPermisions(bean.version));
            addSystemRole(account, bean.id, Role.TYPE_全局, Role.NAME_成员, dao.getAllGlobalMemberPermisions(bean.version));
            addSystemRole(account, bean.id, Role.TYPE_项目, Role.NAME_项目管理员, dao.getAllProjectPermisions(bean.version));
            addSystemRole(account, bean.id, Role.TYPE_项目, Role.NAME_项目成员, dao.getAllProjectMemberPermisions(bean.version));
            //
            Role role = dao.getRoleByCompanyIdTypeName(bean.id, Role.TYPE_全局, Role.NAME_管理员);
            bizService.addOrUpdateCompanyAccount(account.companyId, account, account, Collections.singletonList(role.id), Collections.singletonList(rootDept.id));
            //
            List<DataDict> dataDicts = BizTaskJobs.dictMap.get("ScmToken.type");
            for (DataDict e : dataDicts) {
                ScmToken scmToken = new ScmToken();
                scmToken.companyId = companyId;
                scmToken.token = BizUtil.randomUUID();
                scmToken.type = e.value;
                scmToken.createAccountId = account.id;
                dao.add(scmToken);
            }
            //
            try {
                if (!StringUtil.isEmpty(account.larkTenantKey)) {
                    CompanyLark companyLark = dao.getCompanyLarkByLarkTenantKey(account.larkTenantKey);
                    if (companyLark == null) {
                        companyLark = new CompanyLark();
                        companyLark.companyUuid = bean.uuid;
                        companyLark.larkTenantKey = account.larkTenantKey;
                        companyLark.createAccountId = account.id;
                        dao.add(companyLark);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            //
            bizService.addOptLog(account, bean.id, bean.name,
                    OptLog.EVENT_ID_创建企业, "名称:" + bean.name);
            try {
                logger.info("before addExampleProjects accountId:{} companyId:{}", account.id, companyId);
                //添加示例工程
                addExampleProjects(token, false);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        //
        private void addSystemRole(Account account, int companyId, int type, String name,
                                   Set<String> allPermissionSet) {
            Role role = new Role();
            role.createAccountId = account.id;
            role.isSystemRole = true;
            role.companyId = companyId;
            role.type = type;
            role.name = name;
            role.permissionIds = BizUtil.convertTreeSet(allPermissionSet);
            dao.add(role);
        }
        //


        @Transaction
        @Override
        public void updateCompany(String token, Company bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_修改企业信息);
            Company old = dao.getExistedByIdForUpdate(Company.class, bean.id);
            BizUtil.checkUniqueKeysOnUpdate(dao, bean, old);
            //
            bizService.addOptLog(account, old.id, old.name, OptLog.EVENT_ID_编辑企业信息,
                    old, bean, "name", "imageId", "remark");
            //
            old.name = bean.name;
            old.imageId = bean.imageId;
            old.remark = bean.remark;
            old.announcement = bean.announcement;
            old.updateAccountId = account.id;
            BizUtil.checkValid(old);
            dao.update(old);
        }

        /**
         * 只能创建者操作 而且要保证企业只能有一个人 就是他自己
         */
        @Transaction
        @Override
        public void deleteCompany(String token, int companyId) {
            Account account = bizService.getExistedAccountByTokenForUpdate(token);
            Company old = dao.getExistedByIdForUpdate(Company.class, companyId);
            bizService.checkCompanyPermission(account, Permission.ID_设置企业信息);
            bizService.checkPermission(account, companyId);
            if (GlobalConfig.isPrivateDeploy) {
                throw new AppException("私有化版本不支持此功能");
            }
            if (old.isDelete) {
                return;
            }
            //只能创建者操作 而且要保证企业只能有一个人 就是他自己
            if (old.createAccountId != account.id) {
                throw new AppException("权限不足，只有创建者可以删除企业");
            }
            CompanyMemberQuery query = new CompanyMemberQuery();
            query.companyId = companyId;
            int count = dao.getListCount(query);
            if (count > 1) {
                throw new AppException("企业里还有" + count + "个成员不能删除");
            }
            List<CompanyMember> companyMembers = dao.getList(query);
            if (companyMembers.size() != 1) {
                throw new AppException("企业里还有" + companyMembers.size() + "个成员不能删除");
            }
            CompanyMember member = companyMembers.get(0);
            if (member.accountId != account.id) {
                throw new AppException("您不是企业成员，删除失败");
            }
            old.isDelete = true;
            old.updateAccountId = account.id;
            dao.update(old);
            //
            account.companyId = dao.getCompanyIdByAccountId(account.id);//重新选择一个企业
            dao.update(account);
            //
            bizService.addOptLog(account, old.id, old.name,
                    OptLog.EVENT_ID_删除企业, "名称:" + old.name);
        }

        @Transaction
        @Override
        public void updateCompanyLicense(String token, int companyId, String license) {
            Account account = bizService.getExistedAccountByToken(token);
            Company old = dao.getExistedByIdForUpdate(Company.class, companyId);
            bizService.checkPermission(account, companyId);
            try {
                LicenseUtil.decryptByPublicKey(license);
            } catch (Exception e) {
                logger.error("license:{}", license);
                logger.error(e.getMessage(), e);
                throw new AppException("许可证格式错误");
            }
            bizService.updateCompanyLicenseInfo(old, license);
            //
            bizService.addOptLog(account, old.id, old.name,
                    OptLog.EVENT_ID_更新License, "license:" + license);
        }

        @Transaction
        @Override
        public void createCompanyAccount(String token, CreateCompanyAccountReq req) {
            Account optAccount = bizService.getExistedAccountByToken(token);
            createCompanyAccount0(optAccount, req);
        }

        /**
         * 私有化版本才有创建账号的权限
         * @param optAccount
         * @param req
         */
        private void createCompanyAccount0(Account optAccount, CreateCompanyAccountReq req) {
            bizService.checkIsPrivateDeploy(optAccount);//私有化部署版
            bizService.checkCompanyPermission(optAccount, Permission.ID_管理成员);
            //
            Company company = dao.getById(Company.class, optAccount.companyId);
            if (bizService.isLicenseExpired(company.license)) {
                throw new AppException("授权已到期");
            }
            //
            req.userName = req.userName.trim();
            if(!BizUtil.isNullOrEmpty(req.mobileNo)){
                req.mobileNo = req.mobileNo.trim();
            }

            if (req.email != null) {
                req.email = req.email.trim();
            }
            req.name = req.name.trim();
            if (StringUtil.isEmpty(req.name)) {
                throw new AppException("名称不能为空");
            }
            if (req.roleList == null || req.roleList.isEmpty()) {
                throw new AppException("请选择全局角色");
            }
            if (req.departmentList == null || req.departmentList.isEmpty()) {
                throw new AppException("请选择部门");
            }
            if (BizUtil.isNullOrEmpty(req.userName)) {
                throw new AppException("用户名不能为空");
            }
            PatternUtil.checkUserName(req.userName);
            if (!BizUtil.isNullOrEmpty(req.mobileNo)) {
                if (!PatternUtil.isMobile(req.mobileNo)) {
                    throw new AppException("手机号码格式错误");
                }
            }
            else {
                if(!GlobalConfig.isPrivateDeploy){
                     throw new AppException("手机号不能为空");
                }
            }
            Account account = null;
            //多企业切换companyId会变化
            Integer acountId = dao.getCompanyAccountIdByUserName(optAccount.companyId, req.userName);
            if (null != acountId) {
                account = dao.getExistedById(Account.class, acountId);
            }
            if (account == null) {
                if (req.mobileNo != null) {
                    Account old = dao.getAccountByMobileNoForUpdate(req.mobileNo);
                    if (old != null) {
                        throw new AppException("手机号已经被注册");
                    }
                }
                if (req.email != null) {
                    Account old = dao.getAccountByEmailForUpdate(req.email);
                    if (old != null) {
                        throw new AppException("邮箱已经被注册");
                    }
                }
                if (BizUtil.isNullOrEmpty(req.password)) {
                    throw new AppException("新注册用户【" + req.name + "】需要提供密码");
                }
                account = new Account();
                account.userName = req.userName;
                account.name = req.name;
                account.mobileNo = req.mobileNo;
                account.email = req.email;
                account.isActivated = true;
                account.status = Account.STATUS_有效;
                account.uuid = BizUtil.randomUUID();
                account.pinyinName = PinYinUtil.getHanziPinYin(account.name);
                account.password = BizUtil.encryptPassword(req.password.trim());
                account.encryptPassword = TripleDESUtil.encrypt(req.password.trim(), ConstDefine.GLOBAL_KEY);
                account.createAccountId = optAccount.id;
                account.companyId = optAccount.companyId;
                account.needUpdatePassword = true;
                dao.add(account);
                //初始化通知配置
                initAccountNotificationSettingList(account);
                //
                bizService.addOptLog(optAccount, account.id, account.name,
                        OptLog.EVENT_ID_创建成员, "名称:" + account.name);
            } else {//如果存在(新导入的是新增，结果修改了旧有的账号)
                throw new AppException("企业中已存在该用户名【" + req.userName + "】成员");
               /* if (req.mobileNo != null && (!account.mobileNo.equals(req.mobileNo))) {//修改了手机号
                    Account old = dao.getAccountByMobileNoForUpdate(req.mobileNo);
                    if (old != null) {
                        throw new AppException("手机号已经被注册");
                    }
                }
                if (req.email != null && (account.email == null || (!account.email.equals(req.email)))) {//修改了邮箱
                    Account old = dao.getAccountByEmailForUpdate(req.email);
                    if (old != null) {
                        throw new AppException("邮箱已经被注册");
                    }
                }
                account.userName = req.userName;
                account.name = req.name;
                account.mobileNo = req.mobileNo;
                account.email = req.email;
                account.pinyinName = PinYinUtil.getHanziPinYin(account.name);
                if (!BizUtil.isNullOrEmpty(req.password)) {
                    account.password = BizUtil.encryptPassword(req.password.trim());
                    account.encryptPassword = TripleDESUtil.encrypt(req.password.trim(), ConstDefine.GLOBAL_KEY);
                }
                account.isActivated = true;
                account.status = Account.STATUS_有效;
                account.companyId = optAccount.companyId;
                account.needUpdatePassword = true;
                dao.update(account);*/
            }
            //
            bizService.addOrUpdateCompanyAccount(optAccount.companyId, optAccount, account, req.roleList, req.departmentList);
        }

        @Transaction
        @Override
        public void importCompanyAccountFromExcel(String token, String fileId) {
            Account account = bizService.getExistedAccountByToken(token);
            TableData data = getTableDataFromExcel0(token, account, fileId);
            logger.info("data:{}", DumpUtil.dump(data));
            Map<Integer, Field> headerPos = new HashMap<>();
            Field[] fields = BeanUtil.getFields(CreateCompanyAccountReq.class);
            for (int i = 0; i < data.headers.size(); i++) {
                String header = data.headers.get(i);
                if (BizUtil.isNullOrEmpty(header)) {
                    continue;
                }
                for (Field field : fields) {
                    ExcelCell cell = field.getAnnotation(ExcelCell.class);
                    if (cell == null) {
                        continue;
                    }
                    if (cell.name().equals(header.trim())) {
                        headerPos.put(i, field);
                        break;
                    }
                }
            }
            logger.info("headerPos:{}", DumpUtil.dump(headerPos));
            int line = 2;
            for (List<String> contents : data.contents) {
                if (contents == null || contents.size() == 0) {
                    continue;
                }
                try {
                    CreateCompanyAccountReq bean = new CreateCompanyAccountReq();
                    for (int i = 0; i < contents.size(); i++) {
                        String content = contents.get(i);
                        if (StringUtil.isEmptyWithTrim(content)) {
                            continue;
                        }
                        Field field = headerPos.get(i);
                        if (field == null) {
                            continue;
                        }
                        DomainFieldValid domainField = field.getAnnotation(DomainFieldValid.class);
                        Class<?> fieldType = field.getType();
                        Object value;
                        if (domainField != null && (!StringUtil.isEmpty(domainField.dataDict()))) {
                            value = BizTaskJobs.getDataDictValue(domainField.dataDict(), content);
                        } else if (fieldType.equals(Integer.class)
                                || fieldType.equals(int.class)) {
                            value = Integer.valueOf(content.trim());
                        } else if (fieldType.equals(Long.class)
                                || fieldType.equals(long.class)) {
                            value = Long.valueOf(content.trim());
                        } else {
                            value = content;
                        }
                        field.set(bean, value);
                    }
                    //
                    if (StringUtil.isEmpty(bean.name)) {
                        throw new AppException("姓名不能为空");
                    }
                    if (StringUtil.isEmpty(bean.userName)) {
                        throw new AppException("用户名不能为空");
                    }
                    if (StringUtil.isEmpty(bean.mobileNo)) {
                        throw new AppException("手机号不能为空");
                    }
                  /*  if (StringUtil.isEmpty(bean.password)) {
                        throw new AppException("密码不能为空");
                    }*/
                    if (StringUtil.isEmpty(bean.departmentNames)) {
                        throw new AppException("部门不能为空");
                    }
                    if (StringUtil.isEmpty(bean.roleNames)) {
                        throw new AppException("全局角色不能为空");
                    }
                    bean.departmentNames = bean.departmentNames.replaceAll("，", ",").trim();
                    String[] departmentNames = bean.departmentNames.split(",");
                    for (String departmentName : departmentNames) {
                        DepartmentQuery query = new DepartmentQuery();
                        query.eqName = departmentName;
                        query.type = Department.TYPE_组织架构;
                        query.companyId = account.companyId;
                        query.pageSize = Integer.MAX_VALUE;
                        List<DepartmentInfo> departmentList = dao.getList(query);
                        for (DepartmentInfo e : departmentList) {
                            bean.departmentList.add(e.id);
                        }
                    }
                    //
                    bean.roleNames = bean.roleNames.replaceAll("，", ",").trim();
                    String[] roleNames = bean.roleNames.split(",");
                    for (String roleName : roleNames) {
                        RoleQuery query = new RoleQuery();
                        query.eqName = roleName;
                        query.type = Role.TYPE_全局;
                        query.companyId = account.companyId;
                        query.pageSize = Integer.MAX_VALUE;
                        List<RoleInfo> roleInfoList = dao.getList(query);
                        for (RoleInfo e : roleInfoList) {
                            bean.roleList.add(e.id);
                        }
                    }
                    //
                    createCompanyAccount0(account, bean);
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                    logger.error("content:{}", DumpUtil.dump(contents));
                    throw new AppException("第" + line + "行数据错误," + ex.getMessage());
                }
                line++;
            }//for
        }

        @Transaction
        @Override
        public CompanyInfo getCompanyInfoByUuid(String token, String uuid) {
            return dao.getCompanyInfoByUuid(uuid);
        }

        @Transaction
        @Override
        public String createProject(String token, Project bean, int templateProjectId) {
            Account account = bizService.getExistedAccountByToken(token);
            createProject0(account, bean, templateProjectId);
            bizService.addOptLog(account, bean.id, bean.name, OptLog.EVENT_ID_创建项目, "名称:" + bean.name);
            return bean.uuid;
        }

        @Transaction
        @Override
        public String createTemplateProject(String token, Project bean, List<Integer> objectTypeIdList) {
            Account account = bizService.getExistedAccountByToken(token);
            createTemplateProject0(account, bean, objectTypeIdList);
            bizService.addOptLog(account, bean.id, bean.name, OptLog.EVENT_ID_创建项目, "名称:" + bean.name);
            return bean.uuid;
        }

        private void createTemplateProject0(Account account, Project bean, List<Integer> objectTypeIdList) {
            if (account.companyId == 0) {
                throw new AppException("请选择公司");
            }
            if (BizUtil.isNullOrEmpty(objectTypeIdList)) {
                throw new AppException("请选择模板项目的模块");
            }
            Project old = dao.getProjectByCompanyIdName(account.companyId, bean.name);
            if (old != null) {
                throw new AppException("项目名称已存在");
            }
            bean.companyId = account.companyId;
            bean.isTemplate = true;
            bizService.checkIsPrivateDeploy(account);
            bizService.checkCompanyPermission(account, Permission.ID_系统设置);
            bean.companyId = 0;
            bizService.checkCompanyPermission(account, Permission.ID_创建项目);
            bean.uuid = BizUtil.randomUUID();
            BizUtil.checkUniqueKeysOnAdd(dao, bean);
            BizUtil.checkValid(bean);
            bean.createAccountId = account.id;
            bean.status = Project.STATUS_运行中;
            bean.iterationId = 0;
            bean.templateId = 0;
            bean.isPriavteDeploy = true;
            bean.pinyinShortName = bizService.getProjectPinyinShortName(bean.name);
            dao.add(bean);
            int projectId = bean.id;
            //项目数据权限
            addProjectDataPermission(account, bean.companyId, projectId, 0);
            List<ObjectType> objectTypes = dao.getObjectTypeListByIds(BizUtil.convert(objectTypeIdList));
            //项目模块
            addTemplateProjectModule(account, projectId, objectTypeIdList, objectTypes);
            //项目字段
            addTemplateProjectFieldDefine(account, bean, objectTypes);
            //文件树
            bizService.addChangeLog(account, bean.id, 0, ChangeLog.TYPE_创建, "");
        }

        private void addTemplateProjectFieldDefine(Account account, Project bean, List<ObjectType> objectTypes) {
            if (BizUtil.isNullOrEmpty(objectTypes)) {
                return;
            }
            objectTypes.forEach(objectType -> {
                ObjectTypeFieldDefineQuery query = new ObjectTypeFieldDefineQuery();
                query.objectType = objectType.id;
                List<ObjectTypeFieldDefine> defines = dao.getAll(query);
                for (ObjectTypeFieldDefine define : defines) {
                    ProjectFieldDefine pfd = new ProjectFieldDefine();
                    pfd.companyId = account.companyId;
                    pfd.projectId = bean.id;
                    pfd.createAccountId = account.id;
                    pfd.objectType = define.objectType;
                    pfd.name = define.name;
                    pfd.field = define.field;
                    pfd.type = define.type;
                    pfd.isRequired = false;
                    pfd.isShow = true;
                    pfd.isRequiredShow = define.isRequiredShow;
                    pfd.isSystemField = true;
                    pfd.isPsField = false;
                    pfd.sortWeight = define.sortWeight;
                    if ("createTime".equals(define.field) || "updateTime".equals(define.field)) {
                        pfd.showTimeField = true;
                    }
                    dao.add(pfd);
                }
            });
        }

        private void addTemplateProjectModule(Account account, int projectId, List<Integer> objectTypeIdList, List<ObjectType> objectTypes) {
            int sortWeight = 1;
            if (!BizUtil.isNullOrEmpty(objectTypes)) {
                for (ObjectType objectType : objectTypes) {
                    ProjectModule module = new ProjectModule();
                    module.name = objectType.name;
                    module.url = "task" + objectType.id;
                    module.projectId = projectId;
                    module.companyId = account.companyId;
                    module.createAccountId = account.id;
                    module.isPublic = true;
                    module.isEnable = true;
                    module.sortWeight = sortWeight++;
                    module.isStatusBased = objectType.id != Task.OBJECTTYPE_项目清单;
                    module.isTimeBased = true;
                    module.objectType = objectType.id;
                    dao.add(module);
                }
            }
            //1.固定的模块
            //    //  landmark   agile file wiki Devops stage delivery
            for (Integer objectTypeId : objectTypeIdList) {
                boolean sys = false;
                ProjectModule module = new ProjectModule();
                if (objectTypeId == -1) {
                    sys = true;
                    module.url = ProjectModule.SYS_MODULE_URL_交付版本;
                    module.name = "交付版本";
                } else if (objectTypeId == -2) {
                    sys = true;
                    module.url = ProjectModule.SYS_MODULE_URL_阶段;
                    module.name = "阶段";
                } else if (objectTypeId == -3) {
                    sys = true;
                    module.url = ProjectModule.SYS_MODULE_URL_DEVOPS;
                    module.name = "DevOps";
                } else if (objectTypeId == -4) {
                    sys = true;
                    module.url = ProjectModule.SYS_MODULE_URL_WIKI;
                    module.name = "WIKI";
                } else if (objectTypeId == -5) {
                    sys = true;
                    module.url = ProjectModule.SYS_MODULE_URL_文件;
                    module.name = "文件";
                } else if (objectTypeId == -6) {
                    sys = true;
                    module.url = ProjectModule.SYS_MODULE_URL_敏捷;
                    module.name = "敏捷";
                } else if (objectTypeId == -7) {
                    sys = true;
                    module.url = ProjectModule.SYS_MODULE_URL_里程碑;
                    module.name = "里程碑";
                }
                if (sys) {
                    module.projectId = projectId;
                    module.companyId = account.companyId;
                    module.createAccountId = account.id;
                    module.isPublic = true;
                    module.isEnable = true;
                    module.isStatusBased = false;
                    module.isTimeBased = false;
                    module.objectType = 0;
                    module.sortWeight = sortWeight++;
                    dao.add(module);
                }
            }

        }

        private void createProject0(Account account, Project bean, int templateProjectId) {
            if (account.companyId == 0) {
                throw new AppException("请选择公司");
            }
            //20200310新增
            Project old = dao.getProjectByCompanyIdName(account.companyId, bean.name);
            if (old != null) {
                throw new AppException("项目名称已存在");
            }
            bean.companyId = account.companyId;
            if (bean.isTemplate) {//如果是创建模板，则判断只能是私有部署才可以
                bizService.checkIsPrivateDeploy(account);
                bizService.checkCompanyPermission(account, Permission.ID_系统设置);
                bean.companyId = 0;
            }
            Project templateProject = dao.getExistedById(Project.class, templateProjectId);
            bizService.checkCompanyPermission(account, Permission.ID_创建项目);
            //
            bean.uuid = BizUtil.randomUUID();
            BizUtil.checkUniqueKeysOnAdd(dao, bean);
            BizUtil.checkValid(bean);
            bean.createAccountId = account.id;
            bean.status = Project.STATUS_运行中;
            bean.iterationId = 0;
            //兼容项目集
            if (bean.templateId != Project.ID_项目集模板ID) {
                if (templateProject.isTemplate) {
                    bean.templateId = templateProject.id;
                } else {
                    bean.templateId = templateProject.templateId;
                }
            }
            bean.isPriavteDeploy = templateProject.isPriavteDeploy;
            bean.isIterationBased = templateProject.isIterationBased;
            bean.pinyinShortName = bizService.getProjectPinyinShortName(bean.name);
            //项目负责人
            if (!BizUtil.isNullOrEmpty(bean.ownerAccountIdList)) {
                bean.ownerAccountList = bizService.createAccountSimpleInfo(bean.ownerAccountIdList);
            } else {
                //默认创建人为项目负责人
                bean.ownerAccountIdList = Lists.newArrayList(account.id);
                AccountSimpleInfo asi = BeanUtil.copyTo(AccountSimpleInfo.class, account);
                bean.ownerAccountList = Lists.newArrayList(asi);
            }

            //项目分管领导
            if (!BizUtil.isNullOrEmpty(bean.leaderAccountIdList)) {
                bean.leaderAccountList = bizService.createAccountSimpleInfo(bean.leaderAccountIdList);
            } else {
                bean.leaderAccountList = new ArrayList<>();
            }

            if (null == bean.startDate) {
                bean.startDate = new Date();
            }
            dao.add(bean);
            int projectId = bean.id;
            //
            ProjectMember member = new ProjectMember();
            member.companyId = bean.companyId;
            member.accountId = account.id;
            member.projectId = bean.id;
            dao.add(member);

            //项目负责人是否为项目管理员角色
//            boolean defManagerRole = GlobalConfig.getBooleanValue("project.owner.manager");
            Role role = dao.getRoleByCompanyIdTypeName(account.companyId, Role.TYPE_项目, Role.NAME_项目管理员);
            addProjectMemberRole(member, role.id);
            //
            addProjectDataPermission(account, bean.companyId, projectId, templateProjectId);
            Map<Integer, Integer> fieldMap = new HashMap<>(16);
            addProjectModule(account, projectId, templateProjectId);
            addProjectFieldDefine(account, bean, templateProjectId, fieldMap);//可优化速度
            addProjectStatusDefine(account, projectId, templateProjectId, fieldMap);
            addProjectPriorityDefine(account, projectId, templateProjectId);
            batchCopyProjectCategories(account, projectId, templateProjectId);
            dao.batchCopyProjectObjectTypeTemplatesCategories(account, projectId, templateProjectId);
            //复制模板项目的文件树
            addProjectFileTree(account, projectId, templateProjectId);
            //
            if (!bean.isTemplate) {
                WikiInfo wikiInfo = new WikiInfo();
                wikiInfo.name = "项目WIKI";
                wikiInfo.projectId = projectId;
                addWiki0(account, wikiInfo, true);
            }
            //
            bizService.addChangeLog(account, bean.id, 0, ChangeLog.TYPE_创建, "");
        }

        private void addProjectFileTree(Account account, int projectId, int templateProjectId) {
            List<File> fileList = dao.getFileListByProjectId(templateProjectId);
            if (!BizUtil.isNullOrEmpty(fileList)) {
                Map<Integer, Integer> parentmap = new HashMap<>();
                Map<Integer, Integer> realIdMap = new HashMap<>();
                for (File file : fileList) {
                    if (!file.isDirectory) {
                        continue;
                    }
                    int srcId = file.id;
                    parentmap.put(srcId, file.parentId);
                    file.id = 0;
                    file.createAccountId = account.id;
                    file.projectId = projectId;
                    file.uuid = BizUtil.randomUUID();
                    dao.add(file);
                    realIdMap.put(srcId, file.id);
                }
                parentmap.forEach((srcId, parentId) -> {
                    Integer readId = realIdMap.get(srcId);
                    Integer readParentId = realIdMap.get(parentId);
                    if (null != readId && null != readParentId) {
                        dao.updateFileParentId(readId, readParentId);
                    }
                });

            }

        }

        private void batchCopyProjectCategories(Account account, int newProjectId, int templateProjectId) {
            CategoryQuery query = new CategoryQuery();
            query.projectId = templateProjectId;
            query.pageSize = Integer.MAX_VALUE;
            query.objectTypeSort = Query.SORT_TYPE_ASC;
            query.sortWeightSort = Query.SORT_TYPE_ASC;
            List<Category> list = dao.getAll(query);
            Map<Integer, Integer> newIdMap = new HashMap<>();
            Queue<Category> queue = new LinkedList<>();
            for (Category category : list) {
                if (category.parentId == 0) {
                    queue.offer(category);
                }
            }
            while (!queue.isEmpty()) {
                Category c = queue.poll();
                int newParentId = 0;
                if (c.parentId > 0) {
                    newParentId = newIdMap.get(c.parentId);
                }
                int newId = addCategory(account, c, newProjectId, newParentId);
                newIdMap.put(c.id, newId);
                for (Category e : list) {
                    if (e.parentId == c.id) {
                        queue.offer(e);
                    }
                }
            }
        }

        //
        private int addCategory(Account account, Category category, int newProjectId, int newParentId) {
            Category clone = BeanUtil.copyTo(Category.class, category);
            clone.id = 0;
            clone.projectId = newProjectId;
            clone.parentId = newParentId;
            clone.createAccountId = account.id;
            clone.updateAccountId = account.id;
            return dao.add(clone);
        }

        //复制数据权限
        private void addProjectDataPermission(Account account, int companyId, int projectId, int templateProjectId) {
            if (templateProjectId > 0) {//20200219 复制模板的数据权限
                dao.batchCopyProjectDataPermissions(account.id, projectId, templateProjectId);
            } else {
                PermissionQuery query = new PermissionQuery();
                query.isDataPermission = true;
                List<Permission> list = dao.getAll(query);
                for (Permission e : list) {
                    addProjectDataPermission(account, companyId, projectId, e);
                }
            }
        }

        private void addProjectDataPermission(Account account, int companyId, int projectId, Permission e) {
            ProjectDataPermission bean = new ProjectDataPermission();
            bean.permissionId = e.id;
            bean.objectType = Integer.parseInt(e.id.substring(e.id.lastIndexOf("_") + 1));
            bean.companyId = companyId;
            bean.projectId = projectId;
            bean.ownerList = new ArrayList<>();
            bean.ownerList.add(ProjectDataPermission.OWNER_ID_创建人);
            bean.ownerList.add(ProjectDataPermission.OWNER_ID_责任人);
            bean.createAccountId = account.id;
            dao.add(bean);
        }

        //复制自定义字段
        private void addProjectFieldDefine(Account account, Project project, int templateProjectId, Map<Integer, Integer> fieldMap) {
            List<ProjectFieldDefine> list = dao.getAllProjectFieldDefine(templateProjectId);
            for (ProjectFieldDefine e : list) {
                int oldId = e.id;
                e.companyId = project.companyId;
                e.projectId = project.id;
                e.createAccountId = account.id;
                dao.add(e);
                fieldMap.put(oldId, e.id);
                //如果是自定义字段
                if (!e.isSystemField) {
                    e.field = "field_" + e.id;
                    dao.updateCustomeProductField(e);
                }
            }
        }


        //复制模块
        private void addProjectModule(Account account, int projectId, int templateProjectId) {
            List<ProjectModule> list = dao.getAllProjectModule(templateProjectId);
            for (ProjectModule e : list) {
                e.companyId = account.companyId;
                e.projectId = projectId;
                e.createAccountId = account.id;
//				e.isPublic=true;			//20200219添加
//				e.publicRoles=new ArrayList<>();
                dao.add(e);
            }
        }

        //复制状态
        private void addProjectStatusDefine(Account account, int projectId, int templateProjectId, Map<Integer, Integer> fieldMap) {
            List<ProjectStatusDefine> list = dao.getAllProjectStatusDefine(templateProjectId);
            Map<Integer, Integer> oldNewIdMap = new HashMap<>();
            List<ProjectStatusDefine> newList = new ArrayList<>();
            Project project = dao.getExistedById(Project.class, projectId);
            for (ProjectStatusDefine e : list) {
                int oldId = e.id;
                e.companyId = project.companyId;
                e.projectId = projectId;
                e.createAccountId = account.id;
                if (!BizUtil.isNullOrEmpty(e.checkFieldList)) {
                    List<Integer> newCheckFieldList = new ArrayList<>();
                    for (Integer oldFieldId : e.checkFieldList) {
                        newCheckFieldList.add(fieldMap.get(oldFieldId));
                    }
                    e.checkFieldList = newCheckFieldList;
                }
                dao.add(e);
                oldNewIdMap.put(oldId, e.id);
                newList.add(e);
            }
            //把老的transferTo 修改为新的id
            for (ProjectStatusDefine e : newList) {
                if (e.transferTo != null && e.transferTo.size() > 0) {
                    List<Integer> newTransferToList = new ArrayList<>();
                    for (Integer transferId : e.transferTo) {
                        newTransferToList.add(oldNewIdMap.get(transferId));
                    }
                    e.transferTo = newTransferToList;
                    dao.update(e);
                }
            }
        }

        //复制优先级
        private void addProjectPriorityDefine(Account account, int projectId, int templateProjectId) {
            List<ProjectPriorityDefine> list = dao.getAllProjectPriorityDefine(templateProjectId);
            for (ProjectPriorityDefine e : list) {
                e.companyId = account.companyId;
                e.projectId = projectId;
                e.createAccountId = account.id;
                dao.add(e);
            }
        }

        @Transaction
        @Override
        public void updateProject(String token, Project bean) {
            Account account = bizService.getExistedAccountByToken(token);
            Project old = dao.getExistedByIdForUpdate(Project.class, bean.id);
            String oldName = old.name;
            String oldDescription = old.description;
            old.name = bean.name;
            old.description = bean.description;
            old.imageId = bean.imageId;
            if (old.isTemplate) {//如果是模板项目
                old.isIterationBased = bean.isIterationBased;
            }
            old.expectWorkTime = bean.expectWorkTime;
            old.color = bean.color;
            old.group = bean.group;
            old.remark = bean.remark;
            old.announcement = bean.announcement;
            old.startDate = bean.startDate;
            old.endDate = bean.endDate;
            old.workflowStatus = bean.workflowStatus;
            old.pinyinShortName = bizService.getProjectPinyinShortName(bean.name);
            //项目负责人
            old.ownerAccountIdList = bean.ownerAccountIdList;
            if (!BizUtil.isNullOrEmpty(bean.ownerAccountIdList)) {
                old.ownerAccountList = bizService.createAccountSimpleInfo(bean.ownerAccountIdList);
                updateProjectOwnerAccountList(bean, account);
            } else {
                old.ownerAccountList = new ArrayList<>();
            }
            //项目分管领导
            old.leaderAccountIdList = bean.leaderAccountIdList;
            if (!BizUtil.isNullOrEmpty(bean.leaderAccountIdList)) {
                old.leaderAccountList = bizService.createAccountSimpleInfo(bean.leaderAccountIdList);
            } else {
                old.leaderAccountList = new ArrayList<>();
            }
            BizUtil.checkValid(old);
            dao.update(old);
            //
            List<ChangeLogItem> itemList = new ArrayList<>();
            if (!BizUtil.equalString(oldName, bean.name)) {
                ChangeLogItem item = new ChangeLogItem();
                item.name = "名称";
                item.beforeContent = oldName;
                item.afterContent = bean.name;
                itemList.add(item);
            }
            if (!BizUtil.equalString(oldDescription, bean.description)) {
                ChangeLogItem item = new ChangeLogItem();
                item.name = "备注";
                item.beforeContent = oldDescription;
                item.afterContent = bean.description;
                itemList.add(item);
            }
            bizService.addChangeLog(account, bean.id, bean.id + "", itemList, bean.name);
        }

        private void updateProjectOwnerAccountList(Project bean, Account createAccount) {
            //添加到项目管理员角色
            ProjectMemberQuery memberQuery = new ProjectMemberQuery();
            memberQuery.projectId = bean.id;
            memberQuery.pageSize = Integer.MAX_VALUE;
            List<ProjectMember> members = dao.getAll(memberQuery);
            if (!BizUtil.isNullOrEmpty(members)) {
                boolean defManagerRole = GlobalConfig.getBooleanValue("project.owner.manager");
                Role role = dao.getRoleByCompanyIdTypeName(bean.companyId, Role.TYPE_项目, Role.NAME_项目成员);
                Map<Integer, ProjectMember> memberMap = members.stream().collect(Collectors.toMap(k -> k.accountId, v -> v));
                bean.ownerAccountIdList.forEach(accountId -> {
                    ProjectMember member = memberMap.get(accountId);
                    if (null == member) {
                        member = new ProjectMember();
                        member.projectId = bean.id;
                        member.accountId = accountId;
                        member.companyId = bean.companyId;
                        member.createAccountId = createAccount.id;
                        dao.add(member);
                    }
                    int memberId = member.id;
                    ProjectMemberRole projectMemberRole = dao.getProjectMemberRole(accountId, bean.id, role.id);
                    if (null == projectMemberRole) {
                        projectMemberRole = new ProjectMemberRole();
                        projectMemberRole.accountId = accountId;
                        projectMemberRole.companyId = bean.companyId;
                        projectMemberRole.projectId = bean.id;
                        projectMemberRole.projectMemberId = memberId;
                        projectMemberRole.roleId = role.id;
                        dao.add(projectMemberRole);
                    }
                });
            }
        }

        @Transaction
        @Override
        public void updateProjectWorkflowStatus(String token, int projectId, int workflowStatus) {
            Account account = bizService.getExistedAccountByToken(token);
            Project old = dao.getExistedByIdForUpdate(Project.class, projectId);
            if (old.companyId > 0) {
                bizService.checkProjectPermission(account, old.id, Permission.ID_编辑项目工作流状态);
            } else {
                bizService.checkCompanyPermission(account, Permission.ID_创建项目);
            }
            List<ProjectStatusDefine> projectStatusDefineList = dao.getProjectStatusDefineList(projectId, 0);
            if (!BizUtil.isNullOrEmpty(projectStatusDefineList)) {
                Map<Integer, String> statusmap = projectStatusDefineList.stream().collect(Collectors.toMap(k -> k.id, v -> v.name));
                Map<String, Object> log = ImmutableMap.of(
                        "id", projectId,
                        "name", old.name,
                        "beforeContent", statusmap.getOrDefault(old.workflowStatus, "未设置"),
                        "afterContent", statusmap.getOrDefault(workflowStatus, "未设置"));
                bizService.addChangeLog(account, projectId, 0, ChangeLog.TYPE_变更项目工作流状态, JSONUtil.toJson(log));
            } else {
                bizService.addChangeLog(account, projectId, 0, ChangeLog.TYPE_变更项目工作流状态, JSONUtil.toJson(ProjectSimpleInfo.create(old)));
            }

            old.workflowStatus = workflowStatus;
            for (ProjectStatusDefine projectStatusDefine : projectStatusDefineList) {
                if (projectStatusDefine.type == ProjectStatusDefine.TYPE_结束状态 && workflowStatus == projectStatusDefine.id) {
                    old.finishDate = new Date();
                    old.isFinish = true;
                    break;
                } else {
                    old.isFinish = false;
                    old.finishDate = null;
                }
            }

            dao.update(old);

        }

        /**
         * 打开项目
         */
        @Transaction
        @Override
        public void openProject(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            Project old = dao.getExistedByIdForUpdate(Project.class, id);
            if (!bizService.isPrivateDeploy(account)) {
                bizService.checkPermission(account, old.companyId);
            }
            if (old.status == Project.STATUS_运行中) {
                return;
            }
            old.status = Project.STATUS_运行中;
            dao.update(old);
            //
            bizService.addChangeLog(account, id, 0, ChangeLog.TYPE_重新打开项目, JSONUtil.toJson(ProjectSimpleInfo.create(old)));
        }

        @Transaction
        @Override
        public void archiveProject(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            Project old = dao.getExistedByIdForUpdate(Project.class, id);
            if (!bizService.isPrivateDeploy(account)) {
                bizService.checkPermission(account, old.companyId);
            }
            if (old.status == Project.STATUS_已归档) {
                return;
            }
            old.status = Project.STATUS_已归档;
            dao.update(old);
            //
            bizService.addChangeLog(account, id, 0, ChangeLog.TYPE_归档项目, JSONUtil.toJson(ProjectSimpleInfo.create(old)));
            bizService.addOptLog(account, id, old.name, OptLog.EVENT_ID_归档项目, "名称:" + old.name);
        }

        @Transaction
        @Override
        public void deleteProject(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            Project old = dao.getExistedByIdForUpdate(Project.class, id);
            boolean isPrivetaDeploy = bizService.isPrivateDeploy(account);
            if (!isPrivetaDeploy) {
                bizService.checkPermission(account, old.companyId);
            }
            //saas不可删除项目模板
            if (old.isTemplate && !isPrivetaDeploy) {
                throw new AppException("模板项目不能删除");
            }
            if (old.isDelete) {
                return;
            }
            old.isDelete = true;
            dao.update(old);
            if (!isPrivetaDeploy) {
                //
                bizService.addChangeLog(account, id, 0, ChangeLog.TYPE_删除项目, JSONUtil.toJson(ProjectSimpleInfo.create(old)));
                //删除task
                dao.deleteTasksByProjectId(old.id);
                dao.deleteWikiPagesByProjectId(old.id);
                dao.deleteFilesByProjectId(old.id);
                dao.deleteWikisByProjectId(old.id);
                //
                bizService.addOptLog(account, old.id, old.name,
                        OptLog.EVENT_ID_删除项目, "名称:" + old.name);
            }
        }

        /**
         * 通过ID查询项目
         */
        @Override
        public ProjectInfo getProjectInfoByUuid(String token, String uuid) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectInfo info = dao.getProjectInfoByUuid(uuid);
            bizService.checkNullOrDelete(info, "项目不存在");
            if (!info.isTemplate) {
                bizService.checkPermission(account, info.companyId);
                ProjectMember projectMember = dao.getProjectMemberInfoByProjectIdAccountIdForUpdate(info.id, account.id);
                if (projectMember != null) {
                    projectMember.lastAccessTime = new Date();
                    dao.updateSpecialFields(projectMember, "lastAccessTime");
                    info.star = projectMember.star;
                    info.lastAccessTime = projectMember.lastAccessTime;
                }
            }
            //
            info.memberList = bizService.getProjectMemberInfoList(info.id);
            //项目集对应的任务
            Task projectSetTask = dao.getProjectSetTaskByProjectId(info.id);
            if (null != projectSetTask) {
                info.serialNo = projectSetTask.serialNo;
                info.taskId = projectSetTask.id;
                info.taskUuid = projectSetTask.uuid;
            }
            //
            if (!info.isFinish) {
                info.isFinish = info.workflowStatusType == ProjectStatusDefine.TYPE_结束状态;
            }
            return info;
        }

        /**
         * 通过ID查询项目
         */
        @Override
        public Map<String, Object> getProjectShowInfo(String token, int projectId) {
            Account account = bizService.getExistedAccountByToken(token);
            Map<String, Object> result = new HashMap<>();
            result.put("iterationList", getProjectIterationInfoList(token, projectId));
            result.put("moduleList", getProjectModuleInfoList(token, projectId));
            result.put("permissionList", bizService.getAllProjectPermission(account, account.companyId, projectId));
            return result;
        }

        /**
         * 查询我参与的项目列表和总数
         */
        @Override
        public Map<String, Object> getProjectList(String token, ProjectQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            setupQuery(account, query);
            query.memberAccountId = account.id;
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        /**
         * 查询模板项目列表和总数
         */
        @Override
        public Map<String, Object> getTemplateProjectList(String token, ProjectQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkIsPrivateDeploy(account);
            query.companyId = 0;
            query.isTemplate = true;
            query.isDelete = false;
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        @Override
        public List<ProjectInfo> getTemplateProjectInfoList(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_创建项目);
            ProjectQuery query = new ProjectQuery();
            query.companyId = 0;
            query.status = Project.STATUS_运行中;
            query.isPriavteDeploy = GlobalConfig.isPrivateDeploy;
            query.isTemplate = true;
            return dao.getAll(query);
        }

        /**
         * 查询所有项目列表和总数(仅汇报模板使用,兼容权限的问题)
         */
        @Override
        public Map<String, Object> getReportAllProjectList(String token, ProjectQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_汇报模板管理);
            setupQuery(account, query);
            return createResult(dao.getList(query, Sets.newHashSet("id", "name")), dao.getListCount(query));
        }

        @Transaction
        @Override
        public void batchFreezeAccount(String token, List<Integer> accountIds) {
            Account optAccount = bizService.getExistedAccountByToken(token);
            if (!bizService.isPrivateDeploy(optAccount)) {
                throw new AppException("权限不足");
            }
            bizService.checkCompanyPermission(optAccount, Permission.ID_管理成员);
            for (Integer accountId : accountIds) {
                Account account = dao.getExistedByIdForUpdate(Account.class, accountId);
                account.status = Account.STATUS_无效;
                dao.update(account);
                //重置TOKEN
                bizService.cleanToken(accountId);
                //
                bizService.addOptLog(optAccount, account.id, account.name, OptLog.EVENT_ID_禁用账户, "name:" + account.name);
            }

        }

        @Transaction
        @Override
        public void batchDeleteAccount(String token, List<Integer> accountIds) {
            Account optAccount = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(optAccount, Permission.ID_管理成员);
            for (Integer accountId : accountIds) {
                Account a = dao.getExistedByIdForUpdate(Account.class, accountId);
                if (optAccount.id == a.id) {
                    throw new AppException("不能将自己移出企业");
                }
                bizService.checkIsCompanyMember(a.id, optAccount.companyId);
                //1.先删除公司的成员
                dao.deleteCompanyMemberByAccountIdCompanyId(accountId, optAccount.companyId);
                //2.更新Account里的companyId
                if (a.companyId == optAccount.companyId) {
                    a.companyId = dao.getCompanyIdByAccountId(accountId);//找出最近一家企业
                    dao.update(a);
                }
                //3.删除对应公司组织架构里数据
                dao.deleteDepartmentByAccountIdCompanyId(accountId, optAccount.companyId);
                //4.再删除项目里的成员
                dao.deleteProjectMembersByAccountIdCompanyId(accountId, optAccount.companyId);
                dao.deleteProjectMemberRolesByAccountIdCompanyId(accountId, optAccount.companyId);
                //5.公司人数-1
                Company company = dao.getExistedByIdForUpdate(Company.class, optAccount.companyId);
                company.memberNum = dao.getCurrMemberNum(company.id);
                dao.update(company);
                //如果是私有化部署版本 则把账号用户名手机号邮箱清空20200223
                if (bizService.isPrivateDeploy(company)) {
                    String uuid = BizUtil.randomUUID();
                    a.userName = uuid;
                    a.mobileNo = uuid;
                    a.email = uuid;
                    a.wxOpenId = uuid;
                    a.wxUnionId = uuid;
                    a.larkOpenId = uuid;
                    dao.update(a);
                }
                //
                bizService.addOptLog(optAccount, a.id, a.name, OptLog.EVENT_ID_从企业中移除, "name:" + a.name);
            }
        }

        @Transaction
        @Override
        public void batchForceUpdatePasswd(String token, List<Integer> accountIds) {
            Account optAccount = bizService.getExistedAccountByToken(token);
            bizService.checkIsCompanyAdmin(optAccount);
            for (Integer accountId : accountIds) {
                Account account = dao.getExistedByIdForUpdate(Account.class, accountId);
                if (optAccount.companyId != account.companyId) {
                    throw new AppException("权限不足");
                }
                if (account.needUpdatePassword) {
                    return;
                }
                account.needUpdatePassword = true;
                dao.update(account);
            }
        }

        @Transaction
        @Override
        public void batchUpdateRole(String token, List<Integer> accountIds, List<Integer> roleIds) {
            Account optAccount = bizService.getExistedAccountByToken(token);
            bizService.checkIsCompanyAdmin(optAccount);
            int companyId = optAccount.companyId;
            for (Integer accountId : accountIds) {
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
            }
        }

        @Transaction
        @Override
        public void batchAdjustDepartment(String token, List<Integer> accountIds, List<Integer> departmentIds) {
            Account optAccount = bizService.getExistedAccountByToken(token);
            bizService.checkIsCompanyAdmin(optAccount);
            int companyId = optAccount.companyId;
            for (Integer accountId : accountIds) {
                Account account = dao.getExistedById(Account.class, accountId);
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
                        memberDepartment.createAccountId = optAccount.id;
                        dao.add(memberDepartment);
                    }
                }

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
        }

        @Transaction
        @Override
        public void batchJoinProject(String token, List<Integer> accountIds, List<Integer> projectIds, List<Integer> roleIds) {
            Account optAccount = bizService.getExistedAccountByToken(token);
            if (!bizService.isPrivateDeploy(optAccount)) {
                throw new AppException("只支持私有部署环境");
            }
            for (Integer accountId : accountIds) {
                Account account = dao.getExistedById(Account.class, accountId);
                if (account.companyId != optAccount.companyId) {
                    throw new AppException("权限不足");
                }
                Set<Integer> accountIdList = new HashSet<>();
                accountIdList.add(account.id);
                //
                for (Integer roleId : roleIds) {
                    Role role = dao.getById(Role.class, roleId);
                    if (role == null) {
                        logger.error("roleId:{}", roleId);
                        throw new AppException("角色不存在");
                    }
                    if (role.companyId != account.companyId) {
                        throw new AppException("角色【" + role.name + "】不存在");
                    }
                }
                for (Integer projectId : projectIds) {
                    Set<Integer> roleIdSet = new HashSet<>(roleIds);
                    addProjectMembers0(optAccount, projectId, accountIdList, roleIdSet, false, null);
                }
            }
        }

        @Transaction
        @Override
        public void batchRemoveProject(String token, List<Integer> accountIds, List<Integer> projectIds) {
            if (BizUtil.isNullOrEmpty(projectIds)) {
                throw new AppException("请选择需要移除的项目");
            }
            Account optAccount = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(optAccount, Permission.ID_管理成员);
            for (Integer accountId : accountIds) {
                Account account = dao.getExistedById(Account.class, accountId);
                if (account.id == optAccount.id) {
                    throw new AppException("不能删除自己");
                }
                //
                List<Integer> idList = dao.getProjectMemberIdList(optAccount.companyId, accountId, projectIds);
                for (Integer id : idList) {
                    deleteProjectMember0(optAccount, id);
                }
                bizService.addOptLog(optAccount, account.id, account.name, OptLog.EVENT_ID_从所有项目中移除, "name:" + account.name);
            }
        }

        @Override
        public PasswordRule getPasswordRule(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            return dao.getPasswordRuleByCompanyId(account.companyId);
        }

        @Transaction
        @Override
        public void addPasswordRule(String token, PasswordRule bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.isCompanyAdmin(account);
            bean.companyId = account.companyId;
            dao.add(bean);
            bizService.addOptLog(account, account.id, account.name, OptLog.EVENT_ID_创建密码规则, "companyId:" + account.companyId);
        }

        @Transaction
        @Override
        public void updatePasswordRule(String token, PasswordRule bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.isCompanyAdmin(account);
            PasswordRule old = dao.getExistedById(PasswordRule.class, bean.id);
            old.rules = bean.rules;
            dao.update(old);
            bizService.addOptLog(account, account.id, account.name, OptLog.EVENT_ID_编辑密码规则, "companyId:" + account.companyId);
        }

        @Override
        public List<Delivery.DeliveryInfo> getDeliveryList(String token, Delivery.DeliveryQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkProjectPermission(account, query.projectId, Permission.ID_查看交付版本);
            setupQuery(account, query);
            query.pageSize = 10000;
            query.isDelete = false;
            return dao.getList(query);
        }

        @Override
        public Delivery.DeliveryInfo getDeliveryById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            Delivery.DeliveryInfo info = dao.getExistedById(Delivery.DeliveryInfo.class, id);
            bizService.checkProjectPermission(account, info.projectId, Permission.ID_查看交付版本);
            DeliveryItem.DeliveryItemQuery iq = new DeliveryItem.DeliveryItemQuery();
            iq.deliveryId = id;
            iq.pageSize = 10000;
            info.deliveryItems = dao.getList(iq);
            return info;
        }

        @Transaction
        @Override
        public void addDelivery(String token, Delivery.DeliveryInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bean.status = Delivery.STATUS_未交付;
            if (0 == bean.projectId) {
                throw new AppException("参数异常");
            }
            bizService.checkProjectPermission(account, bean.projectId, Permission.ID_创建和编辑交付版本);
            bean.companyId = account.companyId;
            if (BizUtil.isNullOrEmpty(bean.deliveryItems)) {
                throw new AppException("交付版本信息未添加");
            }
            BizUtil.checkUniqueKeysOnAdd(dao, bean);
            int deliveryId = dao.add(bean);
            bean.deliveryItems.forEach(item -> {
                item.deliveryId = deliveryId;
                item.versionId = item.id;
                item.id = 0;
                dao.add(item);
            });
            bizService.addChangeLog(account, deliveryId, ChangeLog.TYPE_新增交付版本, JSONUtil.toJson(bean));
        }

        @Transaction
        @Override
        public void updateDelivery(String token, Delivery.DeliveryInfo bean) {
            if (0 == bean.projectId) {
                throw new AppException("参数异常");
            }
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkProjectPermission(account, bean.projectId, Permission.ID_创建和编辑交付版本);
            bean.companyId = account.companyId;
            if (BizUtil.isNullOrEmpty(bean.deliveryItems)) {
                throw new AppException("交付版本信息未添加");
            }
            Delivery old = dao.getExistedById(Delivery.class, bean.id);
            BizUtil.checkUniqueKeysOnUpdate(dao, bean, old);
            if (old.status == Delivery.STATUS_已交付 && bean.status == Delivery.STATUS_未交付) {
                throw new AppException("交付状态不可从已交付变更为未交付");
            }
            dao.update(bean);
            dao.deleteDeliveryItem(bean.id);
            bean.deliveryItems.forEach(item -> dao.add(item));
            bizService.addChangeLog(account, bean.id, ChangeLog.TYPE_编辑交付版本, JSONUtil.toJson(bean));
        }

        @Transaction
        @Override
        public void updateDeliveryStatus(String token, Delivery bean) {
            if (0 == bean.projectId) {
                throw new AppException("参数异常");
            }
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkProjectPermission(account, bean.projectId, Permission.ID_创建和编辑交付版本);
            bean.companyId = account.companyId;
            dao.updateDeliveryVersionStatus(bean);
            bizService.addChangeLog(account, bean.id, ChangeLog.TYPE_编辑交付版本, JSONUtil.toJson(bean));
        }

        @Transaction
        @Override
        public void deleteDelivery(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            Delivery delivery = dao.getExistedById(Delivery.class, id);
            bizService.checkProjectPermission(account, delivery.projectId, Permission.ID_删除交付版本);
            delivery.isDelete = true;
            dao.updateSpecialFields(delivery, "isDelete");
            bizService.addChangeLog(account, id, ChangeLog.TYPE_删除交付版本, JSONUtil.toJson(delivery));
        }

        @Transaction
        @Override
        public void addDeliveryItem(String token, DeliveryItem bean) {
            Account account = bizService.getExistedAccountByToken(token);
            Delivery delivery = dao.getExistedById(Delivery.class, bean.deliveryId);
            bizService.checkProjectPermission(account, delivery.projectId, Permission.ID_创建和编辑交付版本);
            DeliveryItem item = dao.getDeliveryItemByDeliveryIdAndVersionId(bean.deliveryId, bean.versionId);
            if (null != item) {
                throw new AppException("已关联此版本");
            }
            dao.add(bean);
            bizService.addChangeLog(account, delivery.id, ChangeLog.TYPE_编辑交付版本, JSONUtil.toJson(bean));
        }

        @Transaction
        @Override
        public void deleteDeliveryItem(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            DeliveryItem item = dao.getExistedById(DeliveryItem.class, id);
            Delivery delivery = dao.getExistedById(Delivery.class, item.deliveryId);
            bizService.checkProjectPermission(account, delivery.projectId, Permission.ID_创建和编辑交付版本);
            dao.deleteById(DeliveryItem.class, id);
            bizService.addChangeLog(account, delivery.id, ChangeLog.TYPE_删除交付版本, JSONUtil.toJson(item));
        }

        @Override
        public Map<String, Object> getTaskAssociateStat(String token, int taskId) {
            Map<String, Object> ret = new HashMap<>();
            bizService.getExistedAccountByToken(token);
            List<TaskAssociated> associatedList = dao.getTaskAssociatedTaskList(taskId);
            ret.put("taskId", taskId);
            long beforeCount = 0, afterCount = 0, subCount = 0;
            if (!BizUtil.isNullOrEmpty(associatedList)) {
                beforeCount = associatedList.stream().filter(k -> k.associatedType == TaskAssociated.ASSOCIATED_TYPE_前置任务).count();
                afterCount = associatedList.stream().filter(k -> k.associatedType == TaskAssociated.ASSOCIATED_TYPE_后置任务).count();
            }
            if (beforeCount == 0 && afterCount == 0) {
                subCount = dao.getById(Task.class, taskId).subTaskCount;
            }
            ret.put("beforeCount", beforeCount);
            ret.put("afterCount", afterCount);
            ret.put("subCount", subCount);
            return ret;
        }

        @Transaction
        @Override
        public void batchDeleteReport(String token, List<Integer> reportIds) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_删除汇报, account.companyId);
            if (!BizUtil.isNullOrEmpty(reportIds)) {
                for (Integer reportId : reportIds) {
                    Report report = dao.getExistedById(Report.class, reportId);
                    report.isDelete = true;
                    dao.update(report);
                    addCompanyRecycle(account, report.id, CompanyRecycle.TYPE_汇报, 0, report.name);
                }
            }
        }

        @Override
        public List<ScmBranch.ScmBranchInfo> getScmBranchList(String token, ScmBranch.ScmBranchQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            if (!bizService.isCompanySuperBoss(account)) {
                bizService.checkCompanyPermission(account, Permission.ID_设置企业信息);
            }
            query.companyId = account.companyId;
            query.pageSize = Integer.MAX_VALUE;
            return dao.getList(query);
        }

        @Override
        public ScmBranch.ScmBranchInfo getScmBranchById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            if (!bizService.isCompanySuperBoss(account)) {
                bizService.checkCompanyPermission(account, Permission.ID_设置企业信息);
            }
            return dao.getExistedById(ScmBranch.ScmBranchInfo.class, id);
        }

        @Transaction
        @Override
        public void addScmBranch(String token, ScmBranch.ScmBranchInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            if (!bizService.isCompanySuperBoss(account)) {
                bizService.checkCompanyPermission(account, Permission.ID_设置企业信息);
            }
            if (BizUtil.isNullOrEmpty(bean.branch)) {
                throw new AppException("分支名称不能为空");
            }
            bean.companyId = account.companyId;
            bean.createAccountId = account.id;
            dao.add(bean);
        }

        @Transaction
        @Override
        public void updateScmBranch(String token, ScmBranch.ScmBranchInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            if (!bizService.isCompanySuperBoss(account)) {
                bizService.checkCompanyPermission(account, Permission.ID_设置企业信息);
            }
            ScmBranch old = dao.getExistedById(ScmBranch.class, bean.id);
            if (old.companyId != account.companyId || old.companyId != bean.companyId) {
                throw new AppException("您的操作企业与所属企业不符");
            }
            bean.updateAccountId = account.id;
            dao.update(bean);
        }

        @Transaction
        @Override
        public void saveScmBranchList(String token, List<ScmBranch.ScmBranchInfo> branchList) {
            Account account = bizService.getExistedAccountByToken(token);
            if (!bizService.isCompanySuperBoss(account)) {
                bizService.checkCompanyPermission(account, Permission.ID_设置企业信息);
            }
            if (BizUtil.isNullOrEmpty(branchList)) {
                return;
            }
            dao.deleteAllCompanyScmBranch(account.companyId);
            for (ScmBranch.ScmBranchInfo branch : branchList) {
                if (BizUtil.isNullOrEmpty(branch.project) || BizUtil.isNullOrEmpty(branch.branch)) {
                    continue;
                }
                branch.companyId = account.companyId;
                branch.createAccountId = account.id;
                dao.add(branch);
            }
        }

        @Transaction
        @Override
        public void deleteScmBranch(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            if (!bizService.isCompanySuperBoss(account)) {
                bizService.checkCompanyPermission(account, Permission.ID_设置企业信息);
            }
            ScmBranch old = dao.getExistedById(ScmBranch.class, id);
            if (old.companyId != account.companyId) {
                throw new AppException("您的操作企业与所属企业不符");
            }
            dao.deleteById(ScmBranch.class, id);
        }

        @Override
        public List<Stage.StageInfo> getStageList(String token, Stage.StageQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkProjectPermission(account, query.projectId, Permission.ID_查看阶段);
            setupQuery(account, query);
            query.isDelete = false;
            query.pageSize = Integer.MAX_VALUE;
            return dao.getList(query);
        }

        @Override
        public Stage.StageInfo getStageById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);

            Stage.StageInfo stageInfo = dao.getExistedById(Stage.StageInfo.class, id);
            bizService.checkProjectPermission(account, stageInfo.projectId, Permission.ID_查看阶段);
            StageLog.StageLogQuery query = new StageLog.StageLogQuery();
            query.stageId = id;
            query.pageSize = Integer.MAX_VALUE;
            stageInfo.logList = dao.getList(query);

            StageAssociate.StageAssociateQuery aq = new StageAssociate.StageAssociateQuery();
            aq.stageId = id;
            aq.pageSize = Integer.MAX_VALUE;

            List<StageAssociate.StageAssociateInfo> list = dao.getList(aq);
            if (!BizUtil.isNullOrEmpty(list)) {
                Object[] status = list.stream().map(k -> k.taskStatus).distinct().toArray();
                List<ProjectStatusDefine> statusDefines = dao.getProjectStatusListByStatus(status);
                Map<Integer, ProjectStatusDefine> statusmap = statusDefines.stream().collect(Collectors.toMap(k -> k.id, v -> v));
                list.forEach(item -> {
                    ProjectStatusDefine define = statusmap.get(item.taskStatus);
                    if (null != define) {
                        item.taskStatusName = define.name;
                    }
                });
            }
            stageInfo.associateList = list;
            return stageInfo;
        }

        @Transaction
        @Override
        public void addStage(String token, Stage.StageInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            if (0 == bean.projectId) {
                throw new AppException("阶段归属项目不能为空");
            }

            bizService.checkProjectPermission(account, bean.projectId, Permission.ID_创建和编辑阶段);
            Project project = dao.getExistedById(Project.class, bean.projectId);
            bean.companyId = account.companyId;
            if (BizUtil.isNullOrEmpty(bean.name)) {
                throw new AppException("阶段名称不能为空");
            }

            if (null == bean.startDate) {
                throw new AppException("阶段开始时间不能为空");
            }
            if (null != project.startDate && bean.startDate.before(DateUtil.getBeginOfDay(project.startDate))) {
                throw new AppException("阶段开始时间不能早于项目开始时间【"+DateUtil.formatDate(project.startDate,"yyyy-MM-dd")+"】");
            }
            if (null == bean.endDate) {
                throw new AppException("阶段结束时间不能为空");
            }
            if (null != project.endDate && project.endDate.before(bean.endDate)) {
                throw new AppException("阶段结束时间不能晚于项目结束时间【"+DateUtil.formatDate(project.endDate,"yyyy-MM-dd")+"】");
            }
            if (bean.startDate.after(bean.endDate)) {
                throw new AppException("阶段开始时间不能晚于结束时间");
            }
            Stage.StageQuery query = new Stage.StageQuery();
//            setupQuery(account, query);
            query.companyId = account.companyId;
            query.projectId = project.id;
            List<Stage.StageInfo> stageList = dao.getAll(query);
            if (!BizUtil.isNullOrEmpty(stageList)) {
                Optional<Stage.StageInfo> optional = stageList.stream().filter(k -> k.name.equals(bean.name)).findFirst();
                if (optional.isPresent()) {
                    throw new AppException("该阶段已存在");
                }
            }
            bean.workDay = Math.abs(DateUtil.differentDays(bean.startDate, bean.endDate)) + 1;
            bean.createAccountId = account.id;

            if (bean.preId > 0) {
                Stage pre = dao.getExistedById(Stage.class, bean.preId);
                if (null!=pre.endDate&&bean.startDate.before(pre.endDate)) {
                    throw new AppException("本阶段的开始时间不能早于前置阶段【"+pre.name+"】的结束时间【"+DateUtil.formatDate(pre.endDate,"yyyy-MM-dd")+"】");
                }
            }

            int stageId = dao.add(bean);

            StageLog log = new StageLog();
            log.stageId = stageId;
            log.createAccountId = account.id;
            log.content = "创建阶段  " + bean.name;
            dao.add(log);

            bizService.addChangeLog(account, bean.projectId, 0, ChangeLog.TYPE_新增阶段, JSONUtil.toJson(bean));
        }

        @Transaction
        @Override
        public void updateStage(String token, Stage.StageInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            Stage old = dao.getExistedById(Stage.class, bean.id);
            if (old.status == Stage.STATUS_已完成) {
                throw new AppException("该阶段已完成不可编辑");
            }
            bizService.checkProjectPermission(account, old.projectId, Permission.ID_创建和编辑阶段);
            Project project = dao.getExistedById(Project.class, old.projectId);
            if (old.companyId != account.companyId) {
                throw new AppException("阶段归属企业信息与当前登录企业不符");
            }
            if (BizUtil.isNullOrEmpty(bean.name)) {
                throw new AppException("阶段名称不能为空");
            }
            if (0 == bean.projectId) {
                throw new AppException("阶段归属项目不能为空");
            }
            if (null == bean.startDate) {
                throw new AppException("阶段开始时间不能为空");
            }
            if (null != project.startDate && bean.startDate.before(project.startDate)) {
                throw new AppException("阶段开始时间不能早于项目开始时间【"+DateUtil.formatDate(project.startDate,"yyyy-MM-dd")+"】");
            }
            if (null == bean.endDate) {
                throw new AppException("阶段结束时间不能为空");
            }
            if (null != project.endDate && project.endDate.before(bean.endDate)) {
                throw new AppException("阶段结束时间不能晚于项目结束时间【"+DateUtil.formatDate(project.endDate,"yyyy-MM-dd")+"】");
            }
            if (bean.startDate.after(bean.endDate)) {
                throw new AppException("阶段开始时间不能晚于结束时间");
            }
            Stage.StageQuery query = new Stage.StageQuery();
//            setupQuery(account, query);
            query.companyId = account.companyId;
            query.projectId = project.id;
            List<Stage.StageInfo> stageList = dao.getAll(query);
            if (!BizUtil.isNullOrEmpty(stageList)) {
                Optional<Stage.StageInfo> optional = stageList.stream().filter(k -> k.name.equals(bean.name) && k.id != bean.id).findFirst();
                if (optional.isPresent()) {
                    throw new AppException("该阶段名已存在");
                }
            }

            bean.workDay = Math.abs(DateUtil.differentDays(bean.startDate, bean.endDate)) + 1;
            bean.updateAccountId = account.id;
            if (bean.preId > 0) {
                Stage pre = dao.getExistedById(Stage.class, bean.preId);
                if (null!=pre.endDate&&bean.startDate.before(pre.endDate)) {
                    throw new AppException("本阶段的开始时间不能早于前置阶段【"+pre.name+"】的结束时间【"+DateUtil.formatDate(pre.endDate,"yyyy-MM-dd")+"】");
                }
                if (bean.status == Stage.STATUS_已完成 && pre.status != Stage.STATUS_已完成) {
                    throw new AppException("不能提前完成阶段，前置阶段【"+pre.name+"】未完成");
                }
            }
            if (bean.preId == bean.id) {
                throw new AppException("前置阶段不能为自己");
            }
            dao.updateSpecialFields(bean, "workDay", "startDate", "endDate", "status","name");
            StageLog log = new StageLog();
            log.stageId = bean.id;
            log.createAccountId = account.id;
            log.content = "修改阶段  " + bean.name;
            dao.add(log);

            bizService.addChangeLog(account, bean.projectId, 0, ChangeLog.TYPE_编辑阶段, JSONUtil.toJson(bean));
        }

        @Transaction
        @Override
        public void updateStageWorkday(String token, Stage.StageInfo bean, boolean danymicUpdate) {
            Account account = bizService.getExistedAccountByToken(token);
            Stage old = dao.getExistedById(Stage.class, bean.id);
            if (old.status == Stage.STATUS_已完成) {
                throw new AppException("该阶段已完成不可编辑");
            }
            bizService.checkProjectPermission(account, old.projectId, Permission.ID_创建和编辑阶段);

            int diff = DateUtil.differentDaysByDay(old.startDate, bean.startDate);
            if (diff != 0) {
                if (danymicUpdate) {
                    bean.endDate = DateUtil.getNextDay(old.endDate, diff);
                }
            }
            int diff1 = DateUtil.differentDaysByDay(old.endDate, bean.endDate);
            if (diff1 != 0) {
                if (danymicUpdate) {
                    bean.startDate = DateUtil.getNextDay(old.startDate, diff);
                }
            }
            if (diff == 0 && diff1 == 0) {
                return;
            }
            bean.workDay = Math.abs(DateUtil.differentDays(bean.startDate, bean.endDate)) + 1;
            if (BizUtil.isNullOrEmpty(bean.changeRemark)) {
                throw new AppException("请填写修改调整原因信息");
            }
            if (old.preId > 0) {
                Stage pre = dao.getExistedById(Stage.class, old.preId);
                if (bean.startDate.before(pre.endDate)) {
                    throw new AppException("本阶段的开始时间不能早于前置阶段的结束时间");
                }
            }
            if (danymicUpdate) {
                if (!(diff < 0)) {
                    //同步调整后置阶段时间
                    Stage.StageQuery query = new Stage.StageQuery();
                    query.projectId = old.projectId;
                    query.pageSize = Integer.MAX_VALUE;
                    query.idNotInList = new int[]{bean.id};
                    query.statusInList = new int[]{Stage.STATUS_未开始, Stage.STATUS_进行中};
                    List<Stage.StageInfo> stages = dao.getList(query);
                    if (!BizUtil.isNullOrEmpty(stages)) {
                        Map<Integer, List<Stage.StageInfo>> stagemap = stages.stream().collect(Collectors.groupingBy(k -> k.preId));
                        recursiveUpdateStageWorkday(stagemap, bean, diff, account, bean.changeRemark);
                    }
                }
            }

            dao.updateStageWorkday(bean);
            StageLog log = new StageLog();
            log.type = StageLog.TYPE_时间调整;
            log.stageId = bean.id;
            log.createAccountId = account.id;
            log.content = bean.changeRemark;
            dao.add(log);

            bizService.addChangeLog(account, bean.projectId, 0, ChangeLog.TYPE_编辑阶段, JSONUtil.toJson(bean));
        }

        private void recursiveUpdateStageWorkday(Map<Integer, List<Stage.StageInfo>> stageMap, Stage parent, int diff, Account account, String remark) {
            List<Stage.StageInfo> subs = stageMap.get(parent.id);
            if (!BizUtil.isNullOrEmpty(subs)) {
                for (Stage.StageInfo sub : subs) {
                    sub.startDate = DateUtil.getNextDay(sub.startDate, diff);
                    sub.endDate = DateUtil.getNextDay(sub.endDate, diff);
                    sub.workDay = Math.abs(DateUtil.differentDays(sub.startDate, sub.endDate)) + 1;
                    dao.updateStageWorkday(sub);
                    StageLog log = new StageLog();
                    log.type = StageLog.TYPE_时间调整;
                    log.stageId = sub.id;
                    log.createAccountId = account.id;
                    log.content = remark;
                    dao.add(log);
                    recursiveUpdateStageWorkday(stageMap, sub, diff, account, remark);
                }
            }
        }

        @Transaction
        @Override
        public void deleteStage(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            Stage old = dao.getExistedById(Stage.class, id);
            bizService.checkProjectPermission(account, old.projectId, Permission.ID_删除阶段);
            if (old.companyId != account.companyId) {
                throw new AppException("阶段归属企业信息与当前登录企业不符");
            }
            old.isDelete = true;
//            dao.update(old);
            dao.deleteById(Stage.class, id);

            StageLog log = new StageLog();
            log.stageId = old.id;
            log.createAccountId = account.id;
            log.content = "删除阶段  " + old.name;
            dao.add(log);

            bizService.addChangeLog(account, old.projectId, 0, ChangeLog.TYPE_删除阶段, JSONUtil.toJson(old));
        }

        @Override
        public List<StageAssociate.StageAssociateInfo> getStageAssociateList(String token, StageAssociate.StageAssociateQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            if (null != query.stageId) {
                Stage stage = dao.getExistedById(Stage.class, query.stageId);
                bizService.checkProjectPermission(account, stage.projectId, Permission.ID_查看阶段);
//                com.sun.jndi.ldap.LdapCtxFactory
            }

            setupQuery(account, query);
            query.pageSize = Integer.MAX_VALUE;
            List<StageAssociate.StageAssociateInfo> list = dao.getList(query);
            if (!BizUtil.isNullOrEmpty(list)) {
                Object[] status = list.stream().map(k -> k.taskStatus).distinct().toArray();
                Object[] objecttypes = list.stream().map(k -> k.objectType).distinct().toArray();
                List<ProjectStatusDefine> statusDefines = dao.getProjectStatusListByStatus(status);
                List<ObjectType> objectTypeList = dao.getObjectTypeListByIds(objecttypes);
                Map<Integer, ProjectStatusDefine> statusmap = statusDefines.stream().collect(Collectors.toMap(k -> k.id, v -> v));
                Map<Integer, ObjectType> objectTypeMap = objectTypeList.stream().collect(Collectors.toMap(k -> k.id, v -> v));
                list.forEach(item -> {
                    ProjectStatusDefine define = statusmap.get(item.taskStatus);
                    if (null != define) {
                        item.taskStatusName = define.name;
                        item.taskStatusColor = define.color;
                    }
                    ObjectType ot = objectTypeMap.get(item.objectType);
                    if (null != ot) {
                        item.objectTypeName = ot.name;
                    }
                });
            }
            return list;
        }

        @Override
        public StageAssociate.StageAssociateInfo getStageAssociateById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            StageAssociate.StageAssociateInfo info = dao.getExistedById(StageAssociate.StageAssociateInfo.class, id);
            bizService.checkProjectPermission(account, info.projectId, Permission.ID_查看阶段);
            return info;
        }

        @Deprecated
        @Transaction
        @Override
        public void addStageAssociate(String token, StageAssociate.StageAssociateInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);

            bean.companyId = account.companyId;
            if (0 == bean.associateId) {
                throw new AppException("阶段关联对象不能为空");
            }
            Task task = dao.getById(Task.class, bean.associateId);
            if (null == task) {
                throw new AppException("阶段关联对象不存在");
            }
            if (!Lists.newArrayList(StageAssociate.TYPE_任务, StageAssociate.TYPE_里程碑).contains(bean.type)) {
                throw new AppException("阶段关联对象类型为空");
            }
            if (0 == bean.stageId) {
                throw new AppException("阶段关联对象不能为空");
            }

            //阶段关联一个里程碑
            if (bean.type == StageAssociate.TYPE_里程碑) {
                StageAssociate.StageAssociateQuery q = new StageAssociate.StageAssociateQuery();
                setupQuery(account, q);
                q.stageId = bean.stageId;
                q.type = StageAssociate.TYPE_里程碑;
                int count = dao.getListCount(q);
                if (count > 0) {
                    throw new AppException("该阶段已关联里程碑");
                }
            }

            Stage stage = dao.getExistedById(Stage.class, bean.stageId);
            bizService.checkProjectPermission(account, stage.projectId, Permission.ID_创建和编辑阶段);
            bean.projectId = stage.projectId;
            dao.add(bean);

            StageLog log = new StageLog();
            log.stageId = bean.stageId;
            log.createAccountId = account.id;
            log.content = "新增关联阶段对象  " + task.name;
            dao.add(log);
        }

        @Transaction
        @Override
        public void batchAddStageAssociate(String token, List<StageAssociate> associateList) {
            Account account = bizService.getExistedAccountByToken(token);
            if (BizUtil.isNullOrEmpty(associateList)) {
                throw new AppException("关联事件不能为空");
            }
            int stageId = associateList.get(0).stageId;
            Stage old = dao.getExistedById(Stage.class, stageId);
            bizService.checkProjectPermission(account, old.projectId, Permission.ID_创建和编辑阶段);
//            if (old.status == Stage.STATUS_已完成) {
//                throw new AppException("该阶段已完成,不可继续关联事件");
//            }


            for (StageAssociate bean : associateList) {
                StageLog log = new StageLog();
                log.stageId = associateList.get(0).stageId;
                log.createAccountId = account.id;

                bean.companyId = account.companyId;
                bean.projectId = old.projectId;
                if (bean.type == StageAssociate.TYPE_任务) {
                    if (0 == bean.associateId) {
                        throw new AppException("阶段关联对象不能为空");
                    }
                    Task task = dao.getById(Task.class, bean.associateId);
                    if (null == task) {
                        throw new AppException("关联对象不存在");
                    }
                    if (task.stageId > 0 && task.stageId != bean.stageId) {
                        throw new AppException("关联对象已关联有阶段");
                    }
                    if (task.projectId != old.projectId) {
                        throw new AppException("关联对象【" + task.name + "】不属于本项目");
                    }
                    if (null != old.endDate && null != task.endDate && old.endDate.before(task.endDate)) {
                        throw new AppException("关联对象的截止时间超出当前阶段周期");
                    }
                    StageAssociate.StageAssociateQuery sq = new StageAssociate.StageAssociateQuery();
                    sq.stageId = bean.stageId;
                    sq.associateId = bean.associateId;
                    int count = dao.getListCount(sq);
                    if (count > 0) {
                        continue;
                    }
                    if (!Lists.newArrayList(StageAssociate.TYPE_任务, StageAssociate.TYPE_里程碑).contains(bean.type)) {
                        throw new AppException("阶段关联对象类型为空");
                    }
                    if (0 == bean.stageId) {
                        throw new AppException("阶段关联对象不能为空");
                    }
                    bean.createAccountId = account.id;
                    dao.add(bean);

                    task.stageId = bean.stageId;
                    dao.updateSpecialFields(task, "stageId");

                    log.content = "新增关联阶段对象  " + task.name;
                } else {
                    Landmark landmark = dao.getById(Landmark.class, bean.landmarkId);
                    if (null == landmark) {
                        throw new AppException("关联里程碑不存在");
                    }

                    if (landmark.stageId > 0) {
                        throw new AppException("里程碑已关联有阶段");
                    }

                    if (landmark.projectId != old.projectId) {
                        throw new AppException("关联里程碑【" + landmark.name + "】不属于本项目");
                    }
                    if (landmark.startDate.before(old.startDate)) {
                        throw new AppException("关联里程碑的开始时间不能早于阶段的开始时间");
                    }
                    if (landmark.endDate.after(old.endDate)) {
                        throw new AppException("关联里程碑的结束时间不能晚于阶段的结束时间");
                    }

                    StageAssociate associate = new StageAssociate();
                    associate.companyId = old.companyId;
                    associate.projectId = old.projectId;
                    associate.landmarkId = bean.landmarkId;
                    associate.stageId = bean.stageId;
                    associate.type = StageAssociate.TYPE_里程碑;
                    dao.add(associate);

                    landmark.stageId = bean.stageId;
                    dao.updateSpecialFields(landmark, "stageId");

                    log.content = "新增关联阶段里程碑对象  " + landmark.name;
                }

                dao.add(log);
            }

        }

        @Transaction
        @Override
        public void deleteLandmarkAssociate(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            LandmarkAssociate old = dao.getExistedById(LandmarkAssociate.class, id);
            bizService.checkProjectPermission(account, old.projectId, Permission.ID_删除里程碑);
            if (old.companyId != account.companyId) {
                throw new AppException("阶段归属企业信息与当前登录企业不符");
            }
            String content = "";
            Task task = dao.getById(Task.class, old.taskId);
            content = "解除关联阶段对象  " + (null != task ? task.name : "");
            dao.deleteById(LandmarkAssociate.class, id);

            LandmarkLog log = new LandmarkLog();
            log.landmarkId = old.landmarkId;
            log.createAccountId = account.id;
            log.content = BizUtil.isNullOrEmpty(content) ? "解除关联" : content;
            dao.add(log);
        }

        @Transaction
        @Override
        public void batchAddLandmarkAssociate(String token, List<LandmarkAssociate> associateList) {
            Account account = bizService.getExistedAccountByToken(token);
            if (BizUtil.isNullOrEmpty(associateList)) {
                throw new AppException("关联事件不能为空");
            }
            int landmarkId = associateList.get(0).landmarkId;
            Landmark old = dao.getExistedById(Landmark.class, landmarkId);
            bizService.checkProjectPermission(account, old.projectId, Permission.ID_创建和编辑里程碑);

            for (LandmarkAssociate bean : associateList) {
                LandmarkLog log = new LandmarkLog();
                log.landmarkId = landmarkId;
                log.createAccountId = account.id;

                bean.companyId = account.companyId;
                bean.projectId = old.projectId;
                if (0 == bean.taskId) {
                    throw new AppException("阶段关联对象不能为空");
                }
                Task task = dao.getById(Task.class, bean.taskId);
                if (null == task) {
                    throw new AppException("关联对象不存在");
                }
                if (task.projectId != old.projectId) {
                    throw new AppException("关联对象【" + task.name + "】不属于本项目");
                }
                /*if (null != old.endDate && null != task.endDate && old.endDate.before(task.endDate)) {
                    throw new AppException("关联对象的截止时间超出当前阶段周期");
                }*/
                LandmarkAssociate.LandmarkAssociateQuery lq = new LandmarkAssociate.LandmarkAssociateQuery();
                lq.landmarkId = bean.landmarkId;
                lq.taskId = bean.taskId;
                int count = dao.getListCount(lq);
                if (count > 0) {
                    continue;
                }
                bean.createAccountId = account.id;
                dao.add(bean);

                log.content = "新增关联阶段对象  " + task.name;
                dao.add(log);
            }

        }

        @Transaction
        @Override
        public void updateStageAssociate(String token, StageAssociate.StageAssociateInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);

            if (0 == bean.associateId) {
                throw new AppException("阶段关联对象不能为空");
            }
            Task task = dao.getById(Task.class, bean.associateId);
            if (null == task) {
                throw new AppException("阶段关联对象不存在");
            }
            if (!Lists.newArrayList(StageAssociate.TYPE_任务, StageAssociate.TYPE_里程碑).contains(bean.type)) {
                throw new AppException("阶段关联对象类型为空");
            }
            if (0 == bean.stageId) {
                throw new AppException("阶段关联对象不能为空");
            }
            Stage stage = dao.getExistedById(Stage.class, bean.stageId);
            bizService.checkProjectPermission(account, stage.projectId, Permission.ID_创建和编辑阶段);
            bean.projectId = stage.projectId;
            dao.update(bean);

            StageLog log = new StageLog();
            log.stageId = bean.stageId;
            log.createAccountId = account.id;
            log.content = "编辑关联阶段对象  " + task.name;
            dao.add(log);
        }

        @Transaction
        @Override
        public void deleteStageAssociate(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            StageAssociate old = dao.getExistedById(StageAssociate.class, id);
            bizService.checkProjectPermission(account, old.projectId, Permission.ID_删除阶段);
            if (old.companyId != account.companyId) {
                throw new AppException("阶段归属企业信息与当前登录企业不符");
            }
            String content = "";
            if (old.type == StageAssociate.TYPE_任务) {
                Task task = dao.getById(Task.class, old.associateId);
                content = "解除关联阶段对象  " + (null != task ? task.name : "");
                dao.deleteById(StageAssociate.class, id);
            } else {
                if (old.landmarkId > 0) {
                    Landmark landmark = dao.getById(Landmark.class, old.landmarkId);
                    content = "解除关联里程碑  " + landmark.name;
                    dao.deleteById(StageAssociate.class, id);
                    //
                    landmark.stageId = 0;
                    dao.updateSpecialFields(landmark, "stageId");
                }
            }

            StageLog log = new StageLog();
            log.stageId = old.stageId;
            log.createAccountId = account.id;
            log.content = BizUtil.isNullOrEmpty(content) ? "解除关联" : content;
            dao.add(log);
        }

        @Override
        public List<StageBurnDownData> getProjectStageBurnDownChart(String token, int projectId) {
            Account account = bizService.getExistedAccountByToken(token);
            return statService.getProjectStageBurnDownChart(account, projectId);
        }

        @Override
        public List<StageFinishDelayRate> getProjectStageFinishDelayRate(String token, int projectId) {
            Account account = bizService.getExistedAccountByToken(token);
            return statService.getProjectStageFinishDelayRate0(account, projectId);
        }

        @Override
        public List<DirectoryNode> getProjectFullFileTree(String token, int projectId) {
            bizService.getExistedAccountByToken(token);
            List<File> fileList = dao.getFileListByProjectId(projectId);
            List<DirectoryNode> nodes = new ArrayList<>();
            if (!BizUtil.isNullOrEmpty(fileList)) {
                List<DirectoryNode> nodeList = new ArrayList<>();
                fileList.forEach(item -> {
                    DirectoryNode node = new DirectoryNode();
                    node.id = item.id;
                    node.isDelete = item.isDelete;
                    node.title = item.name;
                    node.isDirectory = item.isDirectory;
                    node.level = item.level;
                    node.path = item.uuid;
                    node.parentId = item.parentId;
                    node.children = new ArrayList<>();
                    nodeList.add(node);
                });
                Map<Integer, List<DirectoryNode>> nodemap = nodeList.stream().collect(Collectors.groupingBy(k -> k.parentId));
                List<DirectoryNode> topLevel = nodemap.get(0);
                for (DirectoryNode directoryNode : topLevel) {
                    if (!directoryNode.isDelete) {
                        recursiveFileTree(directoryNode, nodemap);
                        nodes.add(directoryNode);
                    }
                }
            }
            return nodes;
        }

        private void recursiveFileTree(DirectoryNode directoryNode, Map<Integer, List<DirectoryNode>> nodemap) {
            int nodeId = directoryNode.id;
            List<DirectoryNode> subs = nodemap.get(nodeId);
            if (!BizUtil.isNullOrEmpty(subs)) {
                subs.forEach(subItem -> {
                    if (!subItem.isDelete) {
                        recursiveFileTree(subItem, nodemap);
                        directoryNode.children.add(subItem);
                    }
                });
            }

        }

        @Override
        public List<DirectoryNode> getProjectFullWikiTree(String token, int projectId) {
            bizService.getExistedAccountByToken(token);
            List<Wiki> wikiList = dao.getWikiListByProjectId(projectId);
            List<WikiPage> wikiPageList = dao.getWikiPageListByProjectId(projectId);
            List<DirectoryNode> nodes = new ArrayList<>();
            if (!BizUtil.isNullOrEmpty(wikiList)) {
                wikiList.forEach(item -> {
                    DirectoryNode node = new DirectoryNode();
                    node.id = item.id;
                    node.title = item.name;
                    node.isDelete = item.isDelete;
                    node.isDirectory = true;
                    node.level = 0;
                    node.path = item.uuid;
                    node.parentId = 0;
                    node.children = new ArrayList<>();
                    nodes.add(node);
                });
                Map<Integer, List<WikiPage>> wikimap = wikiPageList.stream().collect(Collectors.groupingBy(k -> k.wikiId));
                for (DirectoryNode directoryNode : nodes) {
                    List<WikiPage> wikiPages = wikimap.get(directoryNode.id);
                    List<DirectoryNode> pageNodes = new ArrayList<>();
                    if (!BizUtil.isNullOrEmpty(wikiPages)) {
                        wikiPages.forEach(item -> {
                            DirectoryNode node = new DirectoryNode();
                            node.id = item.id;
                            node.title = item.name;
                            node.isDirectory = false;
                            node.level = item.level;
                            node.path = item.uuid;
                            node.parentId = item.parentId;
                            node.children = new ArrayList<>();
                            pageNodes.add(node);
                        });
                    }
                    Map<Integer, List<DirectoryNode>> nodemap = pageNodes.stream().collect(Collectors.groupingBy(k -> k.parentId));
                    List<DirectoryNode> topLevel = nodemap.get(0);
                    if (!BizUtil.isNullOrEmpty(topLevel)) {
                        for (DirectoryNode topNode : topLevel) {
                            recursiveFileTree(topNode, nodemap);
                            directoryNode.children.add(topNode);
                        }
                    }
                }

            }
            return nodes;
        }

        @Transaction
        @Override
        public void saveTemplateProjectFileList(String token, int projectId, List<DirectoryNode> files) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_创建项目);
            Project project = dao.getExistedById(Project.class, projectId);
            if (!project.isTemplate) {
                return;
            }
            dao.deleteTemplateProjectFile(projectId);
            //nodekey->parent_nodekey
            Map<Integer, Integer> nodeParentMap = new HashMap<>();
            //nodekey->file
            Map<Integer, File> nodeIdMap = new HashMap<>();
            for (DirectoryNode node : files) {
                recursiveCreateTemaplateProjectFile(account, projectId, nodeParentMap, nodeIdMap, node);
            }


        }

        @Transaction
        @Override
        public void setCopyTaskPubsubRelation(String token, List<Map<String, Integer>> relations) {
            bizService.getExistedAccountByToken(token);
            relations.forEach(rel -> {
                dao.updateTaskParentId(rel.get("id"), rel.get("parentId"));
                dao.increamentTaskSubTaskCount(rel.get("parentId"));
            });
        }

        @Transaction
        @Override
        public void addLandmark(String token, Landmark.LandmarkInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            if (bean.projectId == 0) {
                throw new AppException("该里程碑缺少项目信息");
            }
            bean.createAccountId = account.id;
            bean.updateAccountId = account.id;
            Project project = dao.getExistedById(Project.class, bean.projectId);
            if (BizUtil.isNullOrEmpty(bean.name)) {
                throw new AppException("里程碑名称不能为空");
            }
            if (null == bean.startDate) {
                throw new AppException("里程碑开始时间不能为空");
            }
            if (null != project.startDate && bean.startDate.before(project.startDate)) {
                throw new AppException("里程碑开始时间不能早于项目开始时间");
            }
            if (null == bean.endDate) {
                throw new AppException("里程碑结束时间不能为空");
            }
            if (null != project.endDate && project.endDate.before(bean.endDate)) {
                throw new AppException("里程碑结束时间不能晚于项目结束时间");
            }
            if (bean.startDate.after(bean.endDate)) {
                throw new AppException("里程碑开始时间不能晚于结束时间");
            }
            bizService.checkProjectPermission(account, bean.projectId, Permission.ID_创建和编辑里程碑);
            Landmark lm = dao.getProjectLandmarkByName(bean.projectId, bean.name);
            if (null != lm) {
                throw new AppException("项目中已存在该名称里程碑");
            }

            bean.companyId = account.companyId;
            //阶段的前后置需要同步到里程碑上
            if (bean.stageId > 0) {

                 /*
                 2021年4月23日14点59分  阶段关联到里程碑支持一对多
                 StageAssociate.StageAssociateQuery q0 = new StageAssociate.StageAssociateQuery();
                setupQuery(account, q0);
                q0.stageId = bean.stageId;
                q0.type = StageAssociate.TYPE_里程碑;
                List<StageAssociate> associates = dao.getList(q0);

              if (!BizUtil.isNullOrEmpty(associates)) {
                    long count = associates.stream().filter(k -> k.landmarkId > 0).count();
                    if (count > 0) {
                        throw new AppException("关联阶段已有里程碑事件存在");
                    }
                }
                */

                //获取关联阶段的前置阶段
                Stage stage = dao.getExistedById(Stage.class, bean.stageId);
                //里程碑的时间窗不能超出阶段的时间窗
                if (null != stage.startDate && bean.startDate.before(stage.startDate)) {
                    throw new AppException("里程碑的开始时间不能早于阶段的开始时间");
                }
                if (null != stage.endDate && bean.endDate.after(stage.endDate)) {
                    throw new AppException("里程碑的结束时间不能晚于阶段的结束时间");
                }


                if (bean.preId > 0) {
                    Landmark preLandmark = dao.getExistedById(Landmark.class, bean.preId);
                    if (null != preLandmark.endDate && bean.startDate.before(preLandmark.endDate)) {
                        throw new AppException("里程碑的开始时间不能早于前置里程碑的结束时间");
                    }
                    if (bean.status == Landmark.STATUS_已完成) {
                        if (preLandmark.status != Landmark.STATUS_已完成) {
                            throw new AppException("前置里程碑未完成");
                        }
                    }
                }
                dao.add(bean);

                //清空阶段已关联的里程碑
//                dao.clearStageAssociateLandmark(bean.stageId);

                StageAssociate associate = new StageAssociate();
                associate.type = StageAssociate.TYPE_里程碑;
                associate.stageId = bean.stageId;
                associate.companyId = bean.companyId;
                associate.projectId = bean.projectId;
                associate.landmarkId = bean.id;
                associate.createAccountId = account.id;
                associate.updateAccountId = account.id;
                dao.add(associate);
            } else {
                dao.add(bean);
            }

            bizService.addChangeLog(account, bean.projectId, 0, ChangeLog.TYPE_新增里程碑, JSONUtil.toJson(bean));
        }

        @Transaction
        @Override
        public void deleteLandmark(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            Landmark old = dao.getExistedById(Landmark.class, id);
            bizService.checkProjectPermission(account, old.projectId, Permission.ID_删除里程碑);
            old.isDelete = true;
//            dao.updateSpecialFields(old, "isDelete");
            dao.deleteById(Landmark.class, id);
            //删除关联
            dao.deleteStageLandmarkAssociate(old.stageId, old.id);
            //删除前置里程碑
            dao.resetPreLandmark(old.id);

            bizService.addChangeLog(account, old.projectId, 0, ChangeLog.TYPE_删除里程碑, JSONUtil.toJson(old));
        }

        @Transaction
        @Override
        public void updateLandmark(String token, Landmark.LandmarkInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            if (bean.projectId == 0) {
                throw new AppException("该里程碑缺少项目信息");
            }
            Landmark old = dao.getExistedById(Landmark.class, bean.id);
            if (old.status == Landmark.STATUS_已完成) {
                throw new AppException("该里程碑已完成,无法编辑");
            }
            Project project = dao.getExistedById(Project.class, bean.projectId);
            if (BizUtil.isNullOrEmpty(bean.name)) {
                throw new AppException("里程碑名称不能为空");
            }
            if (null == bean.startDate) {
                throw new AppException("里程碑开始时间不能为空");
            }
            if (null != project.startDate && bean.startDate.before(project.startDate)) {
                throw new AppException("里程碑开始时间不能早于项目开始时间");
            }
            if (null == bean.endDate) {
                throw new AppException("里程碑结束时间不能为空");
            }
            if (null != project.endDate && project.endDate.before(bean.endDate)) {
                throw new AppException("里程碑结束时间不能晚于项目结束时间");
            }
            if (bean.startDate.after(bean.endDate)) {
                throw new AppException("里程碑开始时间不能晚于结束时间");
            }
            bizService.checkProjectPermission(account, bean.projectId, Permission.ID_创建和编辑里程碑);
            Landmark lm = dao.getProjectLandmarkByName(bean.projectId, bean.name);
            if (null != lm && lm.id != bean.id) {
                throw new AppException("项目中已存在该名称里程碑");
            }

            bean.companyId = account.companyId;
            //阶段的前后置需要同步到里程碑上
            if (bean.stageId > 0) {
              /*  StageAssociate.StageAssociateQuery q0 = new StageAssociate.StageAssociateQuery();
                setupQuery(account, q0);
                q0.stageId = bean.stageId;
                q0.type = StageAssociate.TYPE_里程碑;
                List<StageAssociate> associates = dao.getList(q0);
                if (!BizUtil.isNullOrEmpty(associates)) {
                    long count = associates.stream().filter(k -> k.landmarkId > 0 && k.landmarkId != bean.id).count();
                    if (count > 0) {
                        throw new AppException("关联阶段已有里程碑事件存在");
                    }
                }*/
                //获取关联阶段的前置阶段
                Stage stage = dao.getExistedById(Stage.class, bean.stageId);
                //里程碑的时间窗不能超出阶段的时间窗
                if (null != stage.startDate && bean.startDate.before(stage.startDate)) {
                    throw new AppException("里程碑的开始时间不能早于阶段的开始时间");
                }
                if (null != stage.endDate && bean.endDate.after(stage.endDate)) {
                    throw new AppException("里程碑的结束时间不能晚于阶段的结束时间");
                }

                //前置里程碑
                if (bean.preId > 0) {
                    Landmark preLandmark = dao.getExistedById(Landmark.class, bean.preId);
                    if (null != preLandmark.endDate && bean.startDate.before(preLandmark.endDate)) {
                        throw new AppException("里程碑的开始时间不能早于前置里程碑的结束时间");
                    }
                    if (bean.status == Landmark.STATUS_已完成) {
                        if (preLandmark.status != Landmark.STATUS_已完成) {
                            throw new AppException("前置里程碑未完成");
                        }
                    }
                } else {
//                    if (stage.preId > 0) {
//                        StageAssociate.StageAssociateQuery q = new StageAssociate.StageAssociateQuery();
//                        setupQuery(account, q);
//                        q.stageId = stage.preId;
//                        q.type = StageAssociate.TYPE_里程碑;
//                        List<StageAssociate> list = dao.getList(q);
//                        if (!BizUtil.isNullOrEmpty(list)) {
//                            Optional<StageAssociate> optinal = list.stream().filter(k -> k.landmarkId > 0).findFirst();
//                            optinal.ifPresent(k -> bean.preId = k.landmarkId);
//                        }
//                    }
                }
                dao.update(bean);

                //清空阶段已关联的里程碑
//                dao.clearStageAssociateLandmark(bean.stageId);

                if (old.stageId != bean.stageId) {
                    dao.deleteStageLandmarkAssociate(old.stageId, old.id);

                    StageAssociate associate = new StageAssociate();
                    associate.type = StageAssociate.TYPE_里程碑;
                    associate.stageId = bean.stageId;
                    associate.companyId = bean.companyId;
                    associate.projectId = bean.projectId;
                    associate.landmarkId = bean.id;
                    associate.createAccountId = account.id;
                    associate.updateAccountId = account.id;
                    dao.add(associate);
                }


            } else {
                dao.update(bean);
            }
            bizService.addChangeLog(account, bean.projectId, 0, ChangeLog.TYPE_编辑里程碑, JSONUtil.toJson(bean));

        }


        /**
         * 调整里程碑的时间
         */
        @Transaction
        @Override
        public void updateLandmarkWorkday(String token, Landmark.LandmarkInfo bean, boolean danymicUpdate) {
            Account account = bizService.getExistedAccountByToken(token);
            Landmark old = dao.getExistedById(Landmark.class, bean.id);
            if (old.status == Stage.STATUS_已完成) {
                throw new AppException("该里程碑已完成，不可编辑");
            }
            bizService.checkProjectPermission(account, old.projectId, Permission.ID_创建和编辑里程碑);

            Project project = dao.getExistedById(Project.class, old.projectId);
            if (BizUtil.isNullOrEmpty(bean.name)) {
                throw new AppException("里程碑名称不能为空");
            }
            if (null == bean.startDate) {
                throw new AppException("里程碑开始时间不能为空");
            }
            if (null != project.startDate && bean.startDate.before(project.startDate)) {
                throw new AppException("里程碑开始时间不能早于项目开始时间");
            }
            if (null == bean.endDate) {
                throw new AppException("里程碑结束时间不能为空");
            }
            if (null != project.endDate && project.endDate.before(bean.endDate)) {
                throw new AppException("里程碑结束时间不能晚于项目结束时间");
            }
            if (bean.startDate.after(bean.endDate)) {
                throw new AppException("里程碑开始时间不能晚于结束时间");
            }

            Stage stage = dao.getById(Stage.class, old.stageId);
            //动态修改需要修改阶段的时间
            if (null != stage) {
                bean.stageId = stage.id;
                if (danymicUpdate) {
                    int diff = DateUtil.differentDaysByDay(old.endDate, bean.endDate);
                    if (diff != 0) {
                        stage.endDate = DateUtil.getNextDay(stage.endDate, diff);
                        dao.updateSpecialFields(stage, "endDate");
                    }
                } else {
                    //里程碑的时间窗不能超出阶段的时间窗
                    if (null != stage.startDate && bean.startDate.before(stage.startDate)) {
                        throw new AppException("里程碑的开始时间不能早于阶段的开始时间");
                    }
                    if (null != stage.endDate && bean.endDate.after(stage.endDate)) {
                        throw new AppException("里程碑的结束时间不能晚于阶段的结束时间");
                    }
                }
            } else {
                bean.stageId = 0;
            }
            dao.updateSpecialFields(bean, "startDate", "endDate", "stageId");

            if (BizUtil.isNullOrEmpty(bean.changeRemark)) {
                throw new AppException("请填写调整原因");
            }
            if (old.preId > 0) {
                Landmark pre = dao.getExistedById(Landmark.class, old.preId);
                if (bean.startDate.before(pre.endDate)) {
                    throw new AppException("当前里程碑的开始时间不能早于前置里程碑【" + pre.name + "】的结束时间");
                }
            }
            //校验后置里程碑
            Landmark.LandmarkQuery query = new Landmark.LandmarkQuery();
            query.preId = bean.id;
            query.pageSize = Integer.MAX_VALUE;
            List<Landmark.LandmarkInfo> landmarkInfos = dao.getList(query);
            if (!BizUtil.isNullOrEmpty(landmarkInfos)) {
                for (Landmark.LandmarkInfo landmarkInfo : landmarkInfos) {
                    if (bean.endDate.after(landmarkInfo.startDate)) {
                        throw new AppException("当前里程碑的结束时间不能晚于后置里程【" + landmarkInfo.name + "】的开始时间");
                    }
                }
            }

            LandmarkLog log = new LandmarkLog();
            log.type = StageLog.TYPE_时间调整;
            log.landmarkId = bean.id;
            log.createAccountId = account.id;
            log.updateAccountId = account.id;
            log.content = bean.changeRemark;
            dao.add(log);

            bizService.addChangeLog(account, bean.projectId, 0, ChangeLog.TYPE_编辑里程碑, JSONUtil.toJson(bean));
        }


        @Override
        public Landmark.LandmarkInfo getLandmarkById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            Landmark.LandmarkInfo info = dao.getExistedById(Landmark.LandmarkInfo.class, id);
            bizService.checkProjectPermission(account, info.projectId, Permission.ID_查看里程碑);

            LandmarkAssociate.LandmarkAssociateQuery query = new LandmarkAssociate.LandmarkAssociateQuery();
            query.landmarkId = id;
            List<LandmarkAssociate.LandmarkAssociateInfo> list = dao.getList(query);
            if (!BizUtil.isNullOrEmpty(list)) {
                Object[] status = list.stream().map(k -> k.taskStatus).distinct().toArray();
                List<ProjectStatusDefine> statusDefines = dao.getProjectStatusListByStatus(status);
                Map<Integer, ProjectStatusDefine> statusmap = statusDefines.stream().collect(Collectors.toMap(k -> k.id, v -> v));
                list.forEach(item -> {
                    ProjectStatusDefine define = statusmap.get(item.taskStatus);
                    if (null != define) {
                        item.taskStatusName = define.name;
                    }
                });
            }
            info.associateList = list;
            return info;
        }

        @Override
        public List<Landmark.LandmarkInfo> getLandmarkList(String token, Landmark.LandmarkQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkProjectPermission(account, query.projectId, Permission.ID_查看里程碑);
            query.pageSize = Integer.MAX_VALUE;
            setupQuery(account, query);
            return dao.getList(query);
        }

        @Override
        public List<LandmarkLog.LandmarkLogInfo> getLandmarkLogList(String token, LandmarkLog.LandmarkLogQuery query) {
            if (null == query.landmarkId) {
                return Collections.emptyList();
            }
            query.pageSize = Integer.MAX_VALUE;
            return dao.getList(query);
        }


        private void recursiveCreateTemaplateProjectFile(Account account, int projectId, Map<Integer, Integer> nodeParentMap, Map<Integer, File> nodeIdMap, DirectoryNode node) {
//            if(node.nodeKey!=0){
//                File file = new File();
//                file.uuid = BizUtil.randomUUID();
//                File parent= nodeIdMap.get(node.nodeKey);
//                if(null!=parent){
//                    file.level = parent.level+1;
//                    file.parentId = parent.id;
//                }
//                file.companyId = account.companyId;
//                file.projectId = projectId;
//                file.isDirectory=true;
//                file.createAccountId=account.id;
//                file.color = node.color;
//                file.name = node.title;
//                dao.add(file);
//                nodeIdMap.put(node.nodeKey,file);
//            }
            List<DirectoryNode> children = node.children;
            if (!BizUtil.isNullOrEmpty(children)) {
                for (DirectoryNode child : children) {
                    nodeParentMap.put(child.nodeKey, node.nodeKey);
                    File f = new File();
                    File parent = nodeIdMap.get(node.nodeKey);
                    if (null != parent) {
                        f.level = parent.level + 1;
                        f.parentId = parent.id;
                    }
                    if (node.nodeKey == 0) {
                        f.level = 1;
                    }
                    f.uuid = BizUtil.randomUUID();
                    f.companyId = account.companyId;
                    f.projectId = projectId;
                    f.isDirectory = true;
                    f.createAccountId = account.id;
                    f.color = child.color;
                    f.name = child.title;
                    dao.add(f);
                    nodeIdMap.put(child.nodeKey, f);
                    recursiveCreateTemaplateProjectFile(account, projectId, nodeParentMap, nodeIdMap, child);
                }
            }
        }

        @Override
        public Map<String, Object> getAllProjectList(String token, ProjectQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看项目列表);
            setupQuery(account, query);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        @Override
        public Map<String, Object> getAllRunningProjectList(String token, ProjectQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看项目列表);
            setupQuery(account, query);
            query.status = Project.STATUS_运行中;
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        @Override
        public List<ProjectInfo> getMyProjectList(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectMemberQuery memberQuery = new ProjectMemberQuery();
            memberQuery.excludeTemplateId = Project.ID_项目集模板ID;
            return getMyProjectList0(account, memberQuery);
        }

        public List<ProjectInfo> getMyProjectList0(Account account, ProjectMemberQuery memberQuery) {
            List<ProjectInfo> projectList = new ArrayList<>();
            memberQuery.projectStatus = Project.STATUS_运行中;
            memberQuery.accountId = account.id;
            setupQuery(account, memberQuery);
            memberQuery.pageSize = Integer.MAX_VALUE;
            List<ProjectMemberInfo> members = dao.getList(memberQuery);

            ProjectQuery pq = new ProjectQuery();
            pq.status = Project.STATUS_运行中;
            pq.isTemplate = false;
            setupQuery(account, pq);
            pq.pageSize = Integer.MAX_VALUE;
            if (bizService.isDepartmentSuperBoss(account)) {
                Set<Integer> projectIds = bizService.getAccountAccessProjectList(account, true, false);
                if (!BizUtil.isNullOrEmpty(projectIds)) {
                    pq.idInList = BizUtil.convertList(projectIds);
                    projectList = dao.getList(pq);
                }
            } else if (bizService.isCompanySuperBoss(account)) {
                projectList = dao.getList(pq);
            } else {
                if (!BizUtil.isNullOrEmpty(members)) {
                    pq.idInList = members.stream().mapToInt(k -> k.projectId).toArray();
                    projectList = dao.getList(pq);
                }
            }
            if (BizUtil.isNullOrEmpty(projectList)) {
                return Collections.emptyList();
            }


            if (!BizUtil.isNullOrEmpty(members)) {
                Map<Integer, ProjectMember> memberMap = members.stream().collect(Collectors.toMap(k -> k.projectId, v -> v));
                for (ProjectInfo info : projectList) {
                    //兼容以前的数据，不能精准统计到是否已经完成
                    if (!info.isFinish) {
                        info.isFinish = info.workflowStatusType == ProjectStatusDefine.TYPE_结束状态;
                    }
                    ProjectMember pm = memberMap.get(info.id);
                    if (null != pm) {
                        info.star = pm.star;
                        info.lastAccessTime = pm.lastAccessTime;
                    }
                }
            }
            //star createtime 排序
            projectList.sort((o1, o2) -> {
                if (o1.star) {
                    return -1;
                }
                if (o2.star) {
                    return 1;
                }
                return 0;
            });

            projectList.sort((o1, o2) -> {
                if (null != o1.lastAccessTime && null != o2.lastAccessTime) {
                    if (o1.lastAccessTime.before(o2.lastAccessTime)) {
                        return -1;
                    }
                    if (o1.lastAccessTime.after(o2.lastAccessTime)) {
                        return 1;
                    }
                }
                if (null != o1.lastAccessTime && null == o2.lastAccessTime) {
                    return -1;
                }
                if (null == o1.lastAccessTime && null != o2.lastAccessTime) {
                    return 1;
                }
                return 0;
            });
            //
         /*   Set<Integer> projectIds = new HashSet<>();
            for (ProjectMemberInfo member : members) {
                projectIds.add(member.projectId);
            }
            ProjectQuery query = new ProjectQuery();
            setupQuery(account, query);
            query.idInList = BizUtil.convertList(projectIds);
            query.pageSize = Integer.MAX_VALUE;
            List<ProjectInfo> list = dao.getList(query);
            List<ProjectInfo> result = new ArrayList<>();
            for (ProjectMemberInfo m : members) {
                for (ProjectInfo e : list) {
                    if (m.projectId == e.id) {
                        e.star = m.star;
                        e.lastAccessTime = m.lastAccessTime;
                        result.add(e);
                        break;
                    }
                }
            }
            //*/
            if (!BizUtil.isNullOrEmpty(projectList)) {
                projectList = projectList.stream().filter(BizUtil.distinctByKey(k -> k.id)).collect(Collectors.toList());
            }
            return projectList;
        }

        /**
         * 设置星标项目
         */
        @Override
        @Transaction
        public void setStarProject(String token, int projectId) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectMember old = dao.getProjectMemberInfoByProjectIdAccountIdForUpdate(projectId, account.id);
            old.star = true;
            dao.update(old);

            AccountStar accountStar = new AccountStar();
            accountStar.companyId = account.companyId;
            accountStar.accountId = account.id;
            accountStar.associateId = projectId;
            accountStar.type = AccountStar.TYPE_项目;
            dao.add(accountStar);
        }

        /**
         * 取消星标项目
         */
        @Override
        @Transaction
        public void cancelStarProject(String token, int projectId) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectMember old = dao.getProjectMemberInfoByProjectIdAccountIdForUpdate(projectId, account.id);
            old.star = false;
            dao.update(old);

            AccountStar accountStar = new AccountStar();
            accountStar.companyId = account.companyId;
            accountStar.accountId = account.id;
            accountStar.associateId = projectId;
            accountStar.type = AccountStar.TYPE_项目;
            dao.deleteAccountStar(accountStar);
        }

        @Override
        public List<ProjectFieldDefineInfo> getProjectFieldDefineInfoList(String token, int projectId, int objectType) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkPermissionForProjectAccess(account, projectId);
            List<ProjectFieldDefineInfo> fields = dao.getProjectFieldDefineInfoList(projectId, objectType);
            //私有部署字段过滤
            if (bizService.isPrivateDeploy(account)) {
                ObjectTypeFieldDefineQuery query = new ObjectTypeFieldDefineQuery();
                query.objectType = objectType;
                query.sortWeightSort = Query.SORT_TYPE_ASC;
                List<ObjectTypeFieldDefine.ObjectTypeFieldDefineInfo> fieldDefineList = dao.getAll(query);
                if (!BizUtil.isNullOrEmpty(fieldDefineList)) {
                    Map<String, String> hideFieldMap = fieldDefineList.stream().filter(k -> !k.isRequiredShow).collect(Collectors.toMap(k -> k.field, v -> v.field));
                    if (!BizUtil.isNullOrEmpty(hideFieldMap)) {
                        for (ProjectFieldDefineInfo info : fields) {
                            if (null != hideFieldMap.get(info.field)) {
                                info.isRequiredShow = false;
                            }
                        }
                    }
                }
            }
            return fields;
        }

        @Transaction
        @Override
        public void addProjectFieldDefine(String token, ProjectFieldDefine bean) {
            Account account = bizService.getExistedAccountByToken(token);
            checkNull(bean, "对象");
            bean.companyId = account.companyId;
            BizUtil.checkUniqueKeysOnAdd(dao, bean, "名称为【" + bean.name + "】的自定义字段已经存在");
            BizUtil.checkValid(bean);
            bizService.checkPermissionForProjectAccess(account, bean.projectId);
            bean.createAccountId = account.id;
            bean.isShow = true;
            bean.sortWeight = Integer.MAX_VALUE;//放在最后面
            checkProjectFieldDefineValid(bean);
            dao.add(bean);
            //
            if (!bean.isSystemField) {
                bean.field = BizUtil.getCustomerFieldKey(bean.id);
                dao.update(bean);
            }
        }

        @Transaction
        @Override
        public void updateProjectFieldDefine(String token, ProjectFieldDefine bean) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectFieldDefine old = dao.getExistedByIdForUpdate(ProjectFieldDefine.class, bean.id);
            BizUtil.checkUniqueKeysOnUpdate(dao, bean, old, "名称为【" + bean.name + "】的自定义字段已经存在");
            old.name = bean.name;
            old.isRequired = bean.isRequired;
            if (!old.isUnique && bean.isUnique) {
                bizService.checkUniqueField(bean);
            }
            old.isUnique = bean.isUnique;
            old.type = bean.type;
            old.valueRange = bean.valueRange;
            old.showTimeField = bean.showTimeField;
            old.remark = bean.remark;
            old.updateAccountId = account.id;
            checkProjectFieldDefineValid(old);
            BizUtil.checkValid(old);
            dao.update(old);
        }

        //检查自定义字段合法性
        private void checkProjectFieldDefineValid(ProjectFieldDefine bean) {
            if (!bean.isSystemField) {//自定义字段检查
                checkDataDictValueValid("ProjectFieldDefine.type", bean.type, "类型错误");
                if (bean.type != ProjectFieldDefine.TYPE_复选框 &&
                        bean.type != ProjectFieldDefine.TYPE_单选框) {
                    bean.valueRange = null;
                } else {
                    if (bean.valueRange == null || bean.valueRange.size() == 0) {
                        throw new AppException("单选框或复选框取值范围不能为空");
                    }
                    Set<String> values = new HashSet<>();
                    for (String value : bean.valueRange) {
                        if (StringUtil.isEmptyWithTrim(value)) {
                            throw new AppException("取值范围值不能为空");
                        }
                        if (value.length() > 200) {
                            throw new AppException("取值范围值长度不能大于200的字符");
                        }
                        value = value.trim();
                        if (values.contains(value)) {
                            throw new AppException("取值范围值不能重复");
                        }
                        values.add(value);
                    }
                }
            }
        }

        @Transaction
        @Override
        public void deleteProjectFieldDefine(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectFieldDefine old = dao.getExistedByIdForUpdate(ProjectFieldDefine.class, id);
            bizService.checkPermissionForProjectAccess(account, old.projectId);
            if (old.isSystemField) {
                throw new AppException("系统字段不能删除");
            }
            //项目集专属自定义字段不可删除
            if (old.isPsField) {
                Project project = dao.getExistedById(Project.class, old.projectId);
                if (Project.ID_项目集模板ID == project.templateId) {
                    throw new AppException("项目集专属自定义字段不能删除");
                }
            }
            dao.deleteById(ProjectFieldDefine.class, old.id);
        }

        @Transaction
        @Override
        public void saveProjectFieldDefineList(String token, List<ProjectFieldDefine> list) {
            Account account = bizService.getExistedAccountByToken(token);
            if (list == null || list.isEmpty()) {
                return;
            }
            ProjectFieldDefine first = list.get(0);
            int projectId = first.projectId;
            //
            int objectType = first.objectType;
            bizService.checkPermissionForProjectAccess(account, projectId);
            ProjectModule module = dao.getProjectModuleByProjectObjectTypeForUpdate(projectId, objectType);
            if (module == null) {
                throw new AppException("模块不存在");
            }
            int sortWeight = 1;
            boolean isTimeBased = false;
            boolean isStatusBased = false;
            for (ProjectFieldDefine e : list) {
                if (e.projectId != projectId) {
                    throw new AppException("参数错误");
                }
                if (e.isSystemField) {
                    if (e.isShow && ("startDate".equals(e.field) || "endDate".equals(e.field))) {
                        isTimeBased = true;
                    }
                    if (e.isShow && "statusName".equals(e.field)) {
                        isStatusBased = true;
                    }
                }
                dao.updateProjectFieldDefineSortWeightIsShow(account.id, e.id, sortWeight++, e.isShow);
            }
            if (module.isTimeBased != isTimeBased) {
                module.isTimeBased = isTimeBased;
                dao.update(module);
            }
            if (module.isStatusBased != isStatusBased) {
                module.isStatusBased = isStatusBased;
                dao.update(module);
            }
        }

        @Transaction
        @Override
        public void setProjectFieldDefineIsRequired(String token, int id, boolean isRequired) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectFieldDefine old = dao.getExistedById(ProjectFieldDefine.class, id);
            bizService.checkPermissionForProjectAccess(account, old.projectId);
            old.isRequired = isRequired;
            dao.updateSpecialFields(old, "isRequired");
        }

        @Override
        public List<ProjectModuleInfo> getProjectModuleInfoList(String token, int projectId) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkPermissionForProjectAccess(account, projectId);
            return dao.getEnabledProjectModuleInfoList(projectId);
        }

        @Override
        public List<ProjectModuleInfo> getAllProjectModuleInfoList(String token, int projectId) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkPermissionForProjectAccess(account, projectId);
            return dao.getAllProjectModuleInfoList(projectId);
        }

        @Override
        public List<ProjectModuleInfo> getCompanyProjectModuleInfoList(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, account.companyId);
            return dao.getCompanyProjectModuleList(account.companyId);
        }

        @Transaction
        @Override
        public void updateProjectModulePublicInfo(String token, ProjectModule bean) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectModule old = dao.getExistedByIdForUpdate(ProjectModule.class, bean.id);
            bizService.checkPermissionForProjectAccess(account, bean.projectId);
            if (bean.isPublic) {
                bean.publicRoles = new ArrayList<>();
            }
            if (bean.publicRoles == null) {
                bean.publicRoles = new ArrayList<>();
            }
            old.isPublic = bean.isPublic;
            old.publicRoles = bean.publicRoles;
            dao.update(old);
        }

        /**
         * 更新模板项目模块列表(只更新是否开启) 传过来的list都是选中的
         */
        @Transaction
        @Override
        public void updateProjectModuleList(String token, int projectId, List<ProjectModule> list) {
            Account account = bizService.getExistedAccountByToken(token);
            Project project = dao.getExistedById(Project.class, projectId);
            bizService.checkPermissionForProjectAccess(account, projectId);
            if (project.isTemplate) {
                bizService.checkCompanyPermission(account, Permission.ID_系统设置, null);
            } else {
                bizService.checkProjectPermission(account, projectId, Permission.ID_修改项目设置);
            }
            List<ProjectModule> oldList = dao.getProjectModuleListByProjectId(projectId);
            for (ProjectModule e : oldList) {
                ProjectModule newM = getProjectModule(list, e.url);
                if (newM == null) {
                    e.isEnable = false;
                } else {
                    e.name = newM.name;
                    e.isEnable = true;
                }
                dao.update(e);
            }
            int sortWeight = oldList.size() + 1;
            for (ProjectModule e : list) {
                ProjectModule old = getProjectModule(oldList, e.url);
                if (old == null) {//新增模块
                    ObjectTypeExtInfo info = addProjectFieldDefineForProject(account, project, e.objectType);
                    e.projectId = projectId;
                    e.companyId = project.companyId;
                    e.isEnable = true;
                    e.isPublic = true;
                    e.publicRoles = new ArrayList<>();
                    e.sortWeight = sortWeight++;
                    e.isTimeBased = info.isTimeBased;
                    e.isStatusBased = info.isStatusBased;
                    BizUtil.checkValid(e);
                    BizUtil.checkUniqueKeysOnAdd(dao, e);
                    dao.add(e);
                }
            }
        }

        private ProjectModule getProjectModule(List<ProjectModule> list, String url) {
            if (list == null) {
                return null;
            }
            for (ProjectModule e : list) {
                if (e.url.equals(url)) {
                    return e;
                }
            }
            return null;
        }

        private ObjectTypeExtInfo addProjectFieldDefineForProject(Account account, Project project, int objectType) {
            ObjectTypeExtInfo info = new ObjectTypeExtInfo();
            if (objectType == 0) {
                return info;
            }
            //数据权限
            PermissionQuery permissionQuery = new PermissionQuery();
            permissionQuery.isDataPermission = true;
            List<Permission> pList = dao.getAll(permissionQuery);
            for (Permission e : pList) {
                int eObjectType = Integer.parseInt(e.id.substring(e.id.lastIndexOf("_") + 1));
                if (eObjectType != objectType) {
                    continue;
                }
                ProjectDataPermission permission = dao.getProjectDataPermission(project.id, objectType, e.id);
                if (permission == null) {
                    addProjectDataPermission(account, project.companyId, project.id, e);
                }
            }
            //字段
            ObjectTypeFieldDefineQuery query = new ObjectTypeFieldDefineQuery();
            query.objectType = objectType;
            query.sortWeightSort = Query.SORT_TYPE_ASC;
            List<ObjectTypeFieldDefine> fieldDefines = dao.getAll(query);
            dao.deleteProjectSystemFieldDefines(project.id, objectType);
            List<ProjectFieldDefine> list = new ArrayList<>();
            int sortWeight = 1;
            for (ObjectTypeFieldDefine e : fieldDefines) {
                ProjectFieldDefine bean = new ProjectFieldDefine();
                bean.sortWeight = sortWeight++;
                bean.projectId = project.id;
                bean.companyId = project.companyId;
                bean.objectType = objectType;
                bean.name = e.name;
                bean.field = e.field;
                bean.type = e.type;
                bean.isSystemField = true;
                bean.isShow = true;
                bean.isRequiredShow = e.isRequiredShow;
                dao.add(bean);
                list.add(bean);
            }
            info = bizService.getObjectTypeExtInfo(list);
            return info;
        }

        @Transaction
        @Override
        public void saveProjectModuleList(String token, List<ProjectModule> list) {
            Account account = bizService.getExistedAccountByToken(token);
            if (list == null || list.isEmpty()) {
                return;
            }
            ProjectModule module = dao.getExistedById(ProjectModule.class, list.get(0).id);
            int projectId = module.projectId;
            bizService.checkPermissionForProjectAccess(account, module.projectId);
            int sortWeight = 1;
            for (ProjectModule e : list) {
                ProjectModule old = dao.getExistedByIdForUpdate(ProjectModule.class, e.id);
                if (old.projectId != projectId) {
                    throw new AppException("权限不足");
                }
                old.isEnable = e.isEnable;
                old.sortWeight = sortWeight++;
                dao.update(old);
            }
        }

        //
        @Override
        public List<ProjectStatusDefineInfo> getProjectStatusDefineInfoList(String token, int projectId,
                                                                            int objectType) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkPermissionForProjectAccess(account, projectId);
            //
            return bizService.getProjectStatusDefineInfoListByProjectIdObjectType(projectId, objectType);
        }

        @Transaction
        @Override
        public int addProjectStatusDefine(String token, ProjectStatusDefine bean) {
            Account account = bizService.getExistedAccountByToken(token);
            if (bean.type == ProjectStatusDefine.TYPE_开始状态) {
                int count = dao.getProjectStatusDefineListCount(bean.projectId, bean.objectType, bean.type);
                if (count > 0) {
                    throw new AppException("开始状态只能有一个");
                }
            }
            if (bean.transferTo == null) {
                bean.transferTo = new ArrayList<>();
            }
            if (!bean.transferTo.isEmpty()) {
                for (int to : bean.transferTo) {
                    ProjectStatusDefine toBean = dao.getExistedById(ProjectStatusDefine.class, to);
                    if (toBean.projectId != bean.projectId || toBean.objectType != bean.objectType) {
                        throw new AppException("转移状态不存在");
                    }
                }
            }
            checkNull(bean, "对象");
            BizUtil.checkUniqueKeysOnAdd(dao, bean, "名称为【" + bean.name + "】的状态已经存在");
            BizUtil.checkValid(bean);
            bean.createAccountId = account.id;
            bean.companyId = account.companyId;
            dao.add(bean);
            return bean.id;
        }

        @Transaction
        @Override
        public void updateProjectStatusDefine(String token, ProjectStatusDefine bean) {
            Account account = bizService.getExistedAccountByToken(token);
            if (bean.transferTo == null) {
                bean.transferTo = new ArrayList<>();
            }
            if (!bean.transferTo.isEmpty()) {
                for (int to : bean.transferTo) {
                    if (to == bean.id) {
                        throw new AppException("转移状态不能是自己");
                    }
                    ProjectStatusDefine toBean = dao.getExistedById(ProjectStatusDefine.class, to);
                    if (toBean.projectId != bean.projectId || toBean.objectType != bean.objectType) {
                        throw new AppException("转移状态不存在");
                    }
                }
            }
            checkNull(bean, "对象");
            ProjectStatusDefine old = dao.getExistedByIdForUpdate(ProjectStatusDefine.class, bean.id);
            BizUtil.checkUniqueKeysOnUpdate(dao, bean, old, "名称为【" + bean.name + "】的状态已经存在");
            if (old.type != bean.type && bean.type == ProjectStatusDefine.TYPE_开始状态) {
                int count = dao.getProjectStatusDefineListCount(bean.projectId, bean.objectType, bean.type);
                if (count > 0) {
                    throw new AppException("开始状态只能有一个");
                }
            }
            old.name = bean.name;
            old.type = bean.type;
            old.transferTo = bean.transferTo;
            old.color = bean.color;
            old.remark = bean.remark;
            old.setOwnerList = bean.setOwnerList;
            old.permissionOwnerList = bean.permissionOwnerList;
            old.checkFieldList = bean.checkFieldList;
            old.updateAccountId = account.id;
            BizUtil.checkValid(old);
            dao.update(old);
        }

        @Transaction
        @Override
        public void deleteProjectStatusDefine(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectStatusDefine old = dao.getExistedByIdForUpdate(ProjectStatusDefine.class, id);
            Project pro = dao.getById(Project.class, old.projectId);
            if (null != pro && pro.isTemplate) {
//                 bizService.checkPermissionForProjectAccess(account, old.projectId);
            } else {
                bizService.checkProjectPermission(account, old.companyId, old.projectId, Permission.ID_修改项目设置);
            }
            //
            /*
            int count = dao.getProjectStatusDefineListCount(old.projectId, old.objectType);
            if (count <= 1) {
                throw new AppException("至少要有一个状态");
            }*/
            TaskQuery query = new TaskQuery();
            query.status = id;
            query.projectId = old.projectId;
            query.objectType = old.objectType;
            query.isDelete = false;
            int taskUserCount = dao.getListCount(query);
            if (taskUserCount > 0) {
                throw new AppException("已经有" + taskUserCount + "条数据使用此状态,不能删除");
            }
            dao.deleteById(ProjectStatusDefine.class, old.id);
            //
            List<ProjectStatusDefine> list = dao.getProjectStatusDefineList(old.projectId, old.objectType);
            for (ProjectStatusDefine e : list) {
                if (e.transferTo != null) {
                    if (BizUtil.contains(e.transferTo, id)) {
                        e.transferTo.remove((Integer) id);
                        dao.update(e);
                    }
                }
            }
        }

        @Transaction
        @Override
        public void saveProjectStatusDefineList(String token, List<ProjectStatusDefine> list) {
            for (ProjectStatusDefine e : list) {
                ProjectStatusDefine old = dao.getExistedByIdForUpdate(ProjectStatusDefine.class, e.id);
                old.transferTo = e.transferTo;
                dao.update(old);
            }
        }

        //
        @Override
        public List<ProjectPriorityDefineInfo> getProjectPriorityDefineInfoList(String token, int projectId, int objectType) {
            return dao.getProjectPriorityDefineInfoList(projectId, objectType);
        }

        @Transaction
        @Override
        public int addProjectPriorityDefine(String token, ProjectPriorityDefine bean) {
            Account account = bizService.getExistedAccountByToken(token);
            checkNull(bean, "对象");
            BizUtil.checkUniqueKeysOnAdd(dao, bean, "名称为【" + bean.name + "】的优先级已经存在");
            BizUtil.checkValid(bean);
            if (bean.isDefault) {
                dao.setProjectPriorityDefineIsDefaultFalse(bean.projectId, bean.objectType);
            }
            Project project = dao.getExistedById(Project.class, bean.projectId);
            //
            bean.createAccountId = account.id;
            bean.sortWeight = Integer.MAX_VALUE;
            bean.companyId = project.companyId;
            dao.add(bean);
            //
            resetProjectPriorityDefineInfoSortWeight(bean.projectId, bean.objectType);
            //
            return bean.id;
        }

        @Transaction
        @Override
        public void updateProjectPriorityDefine(String token, ProjectPriorityDefine bean) {
            Account account = bizService.getExistedAccountByToken(token);
            checkNull(bean, "对象");
            ProjectPriorityDefine old = dao.getExistedByIdForUpdate(ProjectPriorityDefine.class, bean.id);
            BizUtil.checkUniqueKeysOnUpdate(dao, bean, old, "名称为【" + bean.name + "】的优先级已经存在");
            if (old.isDefault && (!bean.isDefault)) {
                throw new AppException("不能没有默认优先级");
            }
            if (bean.isDefault && (!old.isDefault)) {
                dao.setProjectPriorityDefineIsDefaultFalse(bean.projectId, bean.objectType);
            }
            old.name = bean.name;
            old.color = bean.color;
            old.isDefault = bean.isDefault;
            old.remark = bean.remark;
            old.updateAccountId = account.id;
            BizUtil.checkValid(old);
            dao.update(old);
        }


        @Transaction
        @Override
        public void deleteProjectPriorityDefine(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectPriorityDefine old = dao.getExistedByIdForUpdate(ProjectPriorityDefine.class, id);
            bizService.checkPermissionForProjectAccess(account, old.projectId);
           /* int count = dao.getProjectPriorityDefineListCount(old.projectId, old.objectType);
            if (count <= 1) {
                throw new AppException("至少要有一个优先级");
            }*/
            if (old.isDefault) {
                throw new AppException(" 不能删除默认优先级");
            }
            TaskQuery query = new TaskQuery();
            query.priority = id;
            query.projectId = old.projectId;
            query.objectType = old.objectType;
            query.isDelete = false;
            int taskUserCount = dao.getListCount(query);
            if (taskUserCount > 0) {
                throw new AppException("已经有" + taskUserCount + "条数据使用此优先级,不能删除");
            }
            dao.deleteById(ProjectPriorityDefine.class, old.id);
            //
            resetProjectPriorityDefineInfoSortWeight(old.projectId, old.objectType);
        }

        private void resetProjectPriorityDefineInfoSortWeight(int projectId, int objectType) {
            List<ProjectPriorityDefineInfo> defines = dao.getProjectPriorityDefineInfoList(projectId, objectType);
            int index = 1;
            for (ProjectPriorityDefineInfo e : defines) {
                e.sortWeight = index++;
                dao.update(e);
            }
        }


        @Transaction
        @Override
        public void updateProjectPrioritySortWeight(String token, int projectId, int objectType, List<Integer> idList) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkPermissionForProjectAccess(account, projectId);
            List<ProjectPriorityDefineInfo> defines = dao.getProjectPriorityDefineInfoList(projectId, objectType);
            if (idList == null || idList.isEmpty()) {
                return;
            }
            if (idList.size() != defines.size()) {
                throw new AppException("优先级列表数量错误");
            }
            for (ProjectPriorityDefineInfo e : defines) {
                e.sortWeight = getSortWeight(e.id, idList);
                dao.update(e);
            }
        }

        private int getSortWeight(int id, List<Integer> idList) {
            int index = 1;
            for (Integer lId : idList) {
                if (id == lId) {
                    return index;
                }
                index++;
            }
            return index;
        }

        @Transaction
        @Override
        public int createDepartment(String token, DepartmentInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            return createDepartment0(account, bean);
        }

        private int createDepartment0(Account account, DepartmentInfo bean) {
            Department parent;
            if (bean.parentId == 0) {
                parent = dao.getDepartmentByCompanyIdLevel(account.companyId, 1);
            } else {
                parent = dao.getExistedById(Department.class, bean.parentId);
            }
            if (parent == null || parent.companyId != account.companyId || parent.type != Department.TYPE_组织架构) {
                throw new AppException("父组织不存在");
            }
            bean.parentId = parent.id;
            bean.level = parent.level + 1;
            bean.companyId = account.companyId;
            bean.type = Department.TYPE_组织架构;
            BizUtil.checkUniqueKeysOnAdd(dao, bean, "组织架构名称已存在，不能重复");
            BizUtil.checkValid(bean);
            bean.createAccountId = account.id;
            dao.add(bean);
            //
            updateDepartmentOwnerList(account, bean, bean.ownerAccountIdList);
            //
            bizService.addOptLog(account, bean.id, bean.name,
                    OptLog.EVENT_ID_创建部门, null, bean, "name", "remark");
            //
            return bean.id;
        }

        @Transaction
        @Override
        public void importDepartmentFromExcel(String token, String fileId) {
            Account account = bizService.getExistedAccountByToken(token);
            TableData data = getTableDataFromExcel0(token, account, fileId);
            Map<Integer, Field> headerPos = new HashMap<>();
            Field[] fields = BeanUtil.getFields(DepartmentCreateReq.class);
            for (int i = 0; i < data.headers.size(); i++) {
                String header = data.headers.get(i);
                if (header == null) {
                    continue;
                }
                for (Field field : fields) {
                    ExcelCell cell = field.getAnnotation(ExcelCell.class);
                    if (cell == null) {
                        continue;
                    }
                    if (cell.name().equals(header)) {
                        headerPos.put(i, field);
                        break;
                    }
                }
            }
            logger.info("headerPos:{}", DumpUtil.dump(headerPos));
            int line = 2;
            for (List<String> contents : data.contents) {
                try {
                    DepartmentCreateReq bean = new DepartmentCreateReq();
                    for (int i = 0; i < contents.size(); i++) {
                        String content = contents.get(i);
                        if (StringUtil.isEmptyWithTrim(content)) {
                            continue;
                        }
                        Field field = headerPos.get(i);
                        if (field == null) {
                            continue;
                        }
                        DomainFieldValid domainField = field.getAnnotation(DomainFieldValid.class);
                        Class<?> fieldType = field.getType();
                        Object value;
                        if (domainField != null && (!StringUtil.isEmpty(domainField.dataDict()))) {
                            value = BizTaskJobs.getDataDictValue(domainField.dataDict(), content);
                        } else if (fieldType.equals(Integer.class)
                                || fieldType.equals(int.class)) {
                            value = Integer.valueOf(content.trim());
                        } else if (fieldType.equals(Long.class)
                                || fieldType.equals(long.class)) {
                            value = Long.valueOf(content.trim());
                        } else {
                            value = content;
                        }
                        field.set(bean, value);
                    }
                    //
                    if (StringUtil.isEmpty(bean.name)) {
                        throw new AppException("部门名称不能为空");
                    }
                    DepartmentInfo departmentInfo = new DepartmentInfo();
                    departmentInfo.name = bean.name;
                    departmentInfo.remark = bean.remark;
                    if (!StringUtil.isEmptyWithTrim(bean.belongDepartmentName)) {
                        Department parent = dao.getDepartmentByCompanyIdName(account.companyId, bean.belongDepartmentName.trim());
                        if (parent == null) {
                            throw new AppException("所属部门不存在");
                        }
                        departmentInfo.parentId = parent.id;
                    }
                    if (!StringUtil.isEmptyWithTrim(bean.owner)) {
                        bean.owner = bean.owner.replaceAll("，", ",").trim();
                        String[] owners = bean.owner.split(",");
                        departmentInfo.ownerAccountIdList = new ArrayList<>();
                        for (String ownerName : owners) {
                            AccountQuery query = new AccountQuery();
                            query.eqName = ownerName;
                            query.companyId = account.companyId;
                            List<AccountInfo> ownerList = dao.getList(query);
                            for (AccountInfo e : ownerList) {
                                departmentInfo.ownerAccountIdList.add(e.id);
                            }
                        }
                    }
                    createDepartment0(account, departmentInfo);
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                    logger.error("content:{}", DumpUtil.dump(contents));
                    throw new AppException("第" + line + "行数据错误," + ex.getMessage());
                }
                line++;
            }//for
        }

        @Transaction
        @Override
        public void updateDepartment(String token, DepartmentInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            Department old = dao.getExistedByIdForUpdate(Department.class, bean.id);
            bizService.checkCompanyPermission(account, Permission.ID_管理组织架构);
            bizService.addOptLog(account, bean.id, bean.name,
                    OptLog.EVENT_ID_编辑部门, old, bean, "name", "remark");
            bizService.checkPermission(account, old.companyId);
            boolean changeName = (!bean.name.equals(old.name));
            bean.companyId = old.companyId;
            if (bean.parentId == 0) {
                bean.parentId = old.parentId;
            }
            bean.type = old.type;
            bean.accountId = old.accountId;
            BizUtil.checkUniqueKeysOnUpdate(dao, bean, old, "组织架构名称已存在，不能重复");
            old.name = bean.name;
            old.remark = bean.remark;
            if (bean.parentId > 0) {
                old.parentId = bean.parentId;
            }
            Department parent;
            if (bean.parentId == 0) {
                parent = dao.getDepartmentByCompanyIdLevel(account.companyId, 1);
                if (null != parent) {
                    old.level = 1;
                }
            } else {
                parent = dao.getExistedById(Department.class, bean.parentId);
                old.level = parent.level + 1;
            }
            if (parent == null || parent.companyId != account.companyId || parent.type != Department.TYPE_组织架构) {
                throw new AppException("父组织不存在");
            }

            BizUtil.checkValid(bean);
            dao.update(old);
            //子部门、部门成员level同步+1
            DepartmentQuery departmentQuery = new DepartmentQuery();
            departmentQuery.pageSize = Integer.MAX_VALUE;
            departmentQuery.companyId = account.companyId;
            List<Department> departments = dao.getAll(departmentQuery);
            if (!BizUtil.isNullOrEmpty(departments)) {
                List<Department> updateLevelDepartments = new ArrayList<>();
                //+1 递归同步level
                recursiveDepartment(updateLevelDepartments, old.level, Sets.newHashSet(old.id), departments);
                if (!BizUtil.isNullOrEmpty(updateLevelDepartments)) {
                    dao.batchUpdateDepartmentLevel(updateLevelDepartments);
                }
            }
            //department
            //
            updateDepartmentOwnerList(account, old, bean.ownerAccountIdList);
            //
            if (changeName) {//改变部门名称
                CompanyMemberQuery query = new CompanyMemberQuery();
                query.pageSize = Integer.MAX_VALUE;
                query.departmentId = bean.id;
                List<CompanyMember> members = dao.getList(query);
                for (CompanyMember e : members) {
                    if (e.departmentList == null || e.departmentList.isEmpty()) {
                        continue;
                    }
                    List<Integer> idList = new ArrayList<>();
                    for (DepartmentSimpleInfo department : e.departmentList) {
                        idList.add(Integer.valueOf(department.id));
                    }
                    e.departmentList = bizService.createDepartmentSimpleInfo(idList);
                    dao.updateSpecialFields(e, "departmentList");
                }
            }
        }

        private void updateDepartmentOwnerList(Account account, Department department, List<Integer> ownerAccountIds) {
            DepartmentOwner.DepartmentOwnerQuery doQuery = new DepartmentOwner.DepartmentOwnerQuery();
            doQuery.companyId = account.companyId;
            doQuery.departmentId = department.id;
            List<DepartmentOwner.DepartmentOwnerInfo> oldOwnerList = dao.getList(doQuery);
            if (!BizUtil.isNullOrEmpty(oldOwnerList)) {
                //被移除的部门负责人需要将其department companyMember信息删除
                List<Integer> oldOwnerAccountIdList = oldOwnerList.stream().map(k -> k.ownerAccountId).collect(Collectors.toList());
                if (!BizUtil.isNullOrEmpty(ownerAccountIds)) {
                    oldOwnerAccountIdList.removeAll(ownerAccountIds);
                }
                if (!BizUtil.isNullOrEmpty(oldOwnerAccountIdList)) {
                    dao.deleteAccountDepartment(department.id, oldOwnerAccountIdList);
                    logger.info("remove department owner dp info--->", DumpUtil.dump(oldOwnerAccountIdList));
                }
                for (Integer accountId : oldOwnerAccountIdList) {
                    CompanyMember companyMember = dao.getCompanyMemberByAccountIdCompanyId(accountId, department.companyId);
                    DepartmentQuery dq = new DepartmentQuery();
                    dq.companyId = department.companyId;
                    dq.accountId = accountId;
                    dq.type = Department.TYPE_人员;
                    List<Department> deps = dao.getAll(dq);
                    if (!BizUtil.isNullOrEmpty(deps)) {
                        List<Integer> parentIds = deps.stream().map(k -> k.parentId).collect(Collectors.toList());
                        if (!BizUtil.isNullOrEmpty(parentIds)) {
                            DepartmentQuery dq1 = new DepartmentQuery();
                            dq1.companyId = dq.companyId;
                            dq1.idInList = BizUtil.convertList(parentIds);
                            dq1.type = Department.TYPE_组织架构;
                            List<Department> depList = dao.getAll(dq1);
                            List<DepartmentSimpleInfo> departmentList = new ArrayList<>();
                            if (!BizUtil.isNullOrEmpty(depList)) {
                                for (Department d : depList) {
                                    departmentList.add(DepartmentSimpleInfo.create(d));
                                }
                            }
                            companyMember.departmentList = departmentList;
                            dao.updateSpecialFields(companyMember, "departmentList");
                        }
                    }
                }

            }
            dao.delete(DepartmentOwner.class, QueryWhere.create().where("department_id", department.id));
            if (!BizUtil.isNullOrEmpty(ownerAccountIds)) {
                for (Integer ownerAccountId : ownerAccountIds) {
                    DepartmentOwner bean = new DepartmentOwner();
                    bean.companyId = account.companyId;
                    bean.departmentId = department.id;
                    bean.ownerAccountId = ownerAccountId;
                    bean.createAccountId = account.id;
                    dao.add(bean);

                    //department添加
                    Department dp = dao.getDepartmentByAccountIdAndDepartmentId(ownerAccountId, department.id);
                    if (null == dp) {
                        dp = new Department();
                        dp.type = Department.TYPE_人员;
                        dp.companyId = department.companyId;
                        dp.name = account.name;
                        dp.parentId = department.id;
                        dp.accountId = ownerAccountId;
                        dp.level = department.level + 1;
                        dp.remark = "组织负责人加入组织架构";
                        dp.createAccountId = account.id;
                        dp.updateAccountId = account.id;
                        dao.add(dp);

                        //companyMember更新部门
                        CompanyMember companyMember = dao.getCompanyMemberByAccountIdCompanyId(dp.accountId, dp.companyId);
                        if (companyMember == null) {
                            companyMember = new CompanyMember();
                            companyMember.accountId = dp.accountId;
                            companyMember.companyId = dp.companyId;
                            companyMember.departmentList = Lists.newArrayList(DepartmentSimpleInfo.create(dp));
                            dao.add(companyMember);
                            // 公司人数+1
                            Company company = dao.getExistedByIdForUpdate(Company.class, companyMember.companyId);
                            company.memberNum = dao.getCurrMemberNum(company.id);
                            if (company.memberNum > company.maxMemberNum) {
                                throw new AppException("成员数量达到上限" + company.maxMemberNum + "，不能新增成员");
                            }
                            dao.updateSpecialFields(company, "memberNum");
                        } else {
                            DepartmentQuery dq = new DepartmentQuery();
                            dq.companyId = dp.companyId;
                            dq.accountId = dp.accountId;
                            dq.type = Department.TYPE_人员;
                            List<Department> deps = dao.getAll(dq);
                            if (!BizUtil.isNullOrEmpty(deps)) {
                                List<Integer> parentIds = deps.stream().map(k -> k.parentId).collect(Collectors.toList());
                                if (!BizUtil.isNullOrEmpty(parentIds)) {
                                    DepartmentQuery dq1 = new DepartmentQuery();
                                    dq1.companyId = dq.companyId;
                                    dq1.idInList = BizUtil.convertList(parentIds);
                                    dq1.type = Department.TYPE_组织架构;
                                    List<Department> depList = dao.getAll(dq1);
                                    List<DepartmentSimpleInfo> departmentList = new ArrayList<>();
                                    if (!BizUtil.isNullOrEmpty(depList)) {
                                        for (Department d : depList) {
                                            departmentList.add(DepartmentSimpleInfo.create(d));
                                        }
                                    }
                                    companyMember.departmentList = departmentList;
                                    dao.updateSpecialFields(companyMember, "departmentList");
                                }
                            }

                        }
                    }
                }
            }
        }


        private void recursiveDepartment(List<Department> departments, int parentLevel, Set<Integer> parentIds, List<Department> departmentList) {
            if (!BizUtil.isNullOrEmpty(parentIds)) {
                List<Department> subDepts = departmentList.stream().filter(k -> parentIds.contains(k.parentId)).collect(Collectors.toList());
                Set<Integer> pids = new HashSet<>();
                subDepts.forEach(dep -> {
                    if (dep.level != parentLevel + 1) {
                        dep.level = parentLevel + 1;
                        departments.add(dep);
                    }
                    pids.add(dep.id);
                });
                if (!BizUtil.isNullOrEmpty(pids)) {
                    recursiveDepartment(departments, parentLevel + 1, pids, departmentList);
                }
            }
        }

        @Transaction
        @Override
        public void deleteDepartment(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            Department old = dao.getExistedByIdForUpdate(Department.class, id);
            bizService.checkPermission(account, old.companyId);
            //
            if (old.level == 1) {
                throw new AppException("根组织不能删除");
            }
            //
            DepartmentQuery query = new DepartmentQuery();
            query.parentId = old.id;
            query.type = Department.TYPE_组织架构;
            int childCount = dao.getDepartmentListCount(query);
            if (childCount > 0) {
                throw new AppException("有" + childCount + "个子部门，不能删除");
            }
            //
            query = new DepartmentQuery();
            query.parentId = old.id;
            query.type = Department.TYPE_人员;
            childCount = dao.getDepartmentListCount(query);
            if (childCount > 0) {
                throw new AppException("有" + childCount + "个成员所属此部门，不能删除");
            }
            //
            CompanyMemberQuery memberQuery = new CompanyMemberQuery();
            memberQuery.departmentId = id;
            int count = dao.getListCount(memberQuery);
            if (count > 0) {
                throw new AppException("有" + childCount + "个成员所属此部门，不能删除");
            }
            dao.deleteById(Department.class, id);
            //
            bizService.addOptLog(account, old.id, old.name, OptLog.EVENT_ID_删除部门, "名称:" + old.name);
        }

        @Override
        public List<DepartmentTree> getDepartmentTree(String token, Boolean needAccount) {
            Account account = bizService.getExistedAccountByToken(token);
            List<DepartmentInfo> list;
            if (needAccount != null && needAccount) {
                list = dao.getAllDepartmentInfo(account.companyId);
            } else {
                list = dao.getAllDepartmentInfoWithoutAccount(account.companyId);
            }
            return getDepartmentTree(list);
        }

        /**
         * 查询部门列表
         */
        @Override
        public List<DepartmentInfo> getDepartmentInfoList(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            DepartmentQuery query = new DepartmentQuery();
            query.companyId = account.companyId;
            return dao.getAll(query);
        }

        //
        private List<DepartmentTree> getDepartmentTree(List<DepartmentInfo> list) {
            boolean expand = true;
            List<DepartmentTree> result = new ArrayList<>();
            Queue<DepartmentTree> queue = new LinkedList<>();
            for (DepartmentInfo e : list) {
                if (e.level == 1) {
                    DepartmentTree tree = createDepartmentTree(e, expand);
                    queue.offer(tree);
                    result.add(tree);
                }
            }
            while (!queue.isEmpty()) {
                DepartmentTree dir = queue.poll();
                for (DepartmentInfo e : list) {
                    if (e.parentId == dir.id) {
                        DepartmentTree child = createDepartmentTree(e, expand);
                        dir.children.add(child);
                        queue.offer(child);
                    }
                }
            }
            //
            return result;
        }

        private DepartmentTree createDepartmentTree(DepartmentInfo cd, boolean expand) {
            DepartmentTree dir = new DepartmentTree();
            dir.id = cd.id;
            if (cd.type == Department.TYPE_组织架构) {
                dir.title = cd.name;
            } else {
                dir.title = cd.accountName;
                dir.pinyinName = cd.accountPinyinName;
                dir.userName = cd.accountUserName;
            }
            dir.level = cd.level;
            dir.parentId = cd.parentId;
            dir.type = cd.type;
            dir.accountId = cd.accountId;
            dir.expand = expand;
            return dir;
        }

        @Override
        public List<PermissionTree> getPermissionList(String token, int type) {
            Account account = bizService.getExistedAccountByToken(token);
            Company company = dao.getExistedById(Company.class, account.companyId);
            List<Permission> permissions = dao.getPermissionList(type, company.version);
            Set<String> needRemovePermissions = bizService.getNeedRemovePermissions(account.companyId);
            if (!needRemovePermissions.isEmpty()) {
                permissions.removeIf(p -> needRemovePermissions.contains(p.id));
            }
            if (type == Permission.TYPE_项目) {
                permissions = new ArrayList<>(permissions);
            }
            List<PermissionTree> result = new ArrayList<>();
            for (Permission permission : permissions) {
                if (StringUtil.isEmpty(permission.parentId)) {
                    PermissionTree tree = new PermissionTree();
                    tree.id = permission.id;
                    tree.name = permission.name;
                    for (Permission p : permissions) {
                        if (p.parentId != null && p.parentId.equals(permission.id)) {
                            tree.children.add(p);
                        }
                    }
                    result.add(tree);
                }
            }
            return result;
        }

        @Override
        public DepartmentInfo getDepartmentInfo(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            DepartmentInfo info = dao.getExistedById(DepartmentInfo.class, id);
            bizService.checkPermission(account, info.companyId);
            List<Integer> accountIds = dao.getDepartmentOwnerAccountIdList(id);
            info.ownerAccountList = bizService.getAccountSimpleInfoList(BizUtil.convertList(accountIds));
            return info;
        }

        @Override
        public List<AccountInfo> getAccountInfoList(String token, AccountQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            query.companyId = account.companyId;
            if (query.departmentId != null) {
                query.departmentIds = bizService.getChildDepartmentIds(query.departmentId);
            }
            return dao.getList(query);
        }

        @Override
        public Map<String, Object> getCompanyMemberList(String token, CompanyMemberQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            query.companyId = account.companyId;
            List<CompanyMemberInfo> list = dao.getList(query);
            for (CompanyMemberInfo e : list) {
                RoleQuery roleQuery = new RoleQuery();
                roleQuery.companyId = e.companyId;
                roleQuery.type = RoleInfo.TYPE_全局;
                roleQuery.globalAccountId = e.accountId;
                roleQuery.pageSize = Integer.MAX_VALUE;
                e.roleList = dao.getList(roleQuery, "permissionIds");
            }
            return createResult(list, dao.getListCount(query));
        }

        @Transaction
        @Override
        public int addRole(String token, Role bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bean.companyId = account.companyId;
            if (bean.type == Role.TYPE_全局) {
                bizService.checkCompanyPermission(account, Permission.ID_管理企业角色);
            }
            if (bean.type == Role.TYPE_项目) {
                bizService.checkCompanyPermission(account, Permission.ID_管理项目角色);
            }
            if (bean.type != Role.TYPE_全局 && bean.type != Role.TYPE_项目) {
                throw new AppException("角色类型错误");
            }
            if (bean.permissionIds == null) {
                bean.permissionIds = new TreeSet<>();
            } else {
                bean.permissionIds = BizUtil.convertTreeSet(bean.permissionIds);
            }
            bean.isSystemRole = false;
            BizUtil.checkValid(bean);
            BizUtil.checkUniqueKeysOnAdd(dao, bean, "角色名称不能重复");
            dao.add(bean);
            //
            if (bean.type == Role.TYPE_全局) {
                bizService.addOptLog(account, bean.id, bean.name, OptLog.EVENT_ID_创建企业角色, "name:" + bean.name);
            } else {
                bizService.addOptLog(account, bean.id, bean.name, OptLog.EVENT_ID_创建项目角色, "name:" + bean.name);
            }

            //
            return bean.id;
        }

        @Transaction
        @Override
        public void updateRole(String token, Role bean) {
            Account account = bizService.getExistedAccountByToken(token);
            updateRole0(account, bean);
        }

        public void updateRole0(Account account, Role bean) {
            if (bean.type != Role.TYPE_全局 && bean.type != Role.TYPE_项目) {
                throw new AppException("角色类型错误");
            }
            if (bean.type == Role.TYPE_全局) {
                bizService.checkCompanyPermission(account, Permission.ID_管理企业角色);
            }
            if (bean.type == Role.TYPE_项目) {
                bizService.checkCompanyPermission(account, Permission.ID_管理项目角色);
            }
            Role old = dao.getExistedByIdForUpdate(Role.class, bean.id);
            if (bean.permissionIds == null) {
                bean.permissionIds = new TreeSet<>();
            }
            BizUtil.checkUniqueKeysOnUpdate(dao, bean, old, "角色名称不能重复");
            old.permissionIds = BizUtil.convertTreeSet(bean.permissionIds);
            old.name = bean.name;
            old.remark = bean.remark;
            BizUtil.checkValid(old);
            dao.update(old);
            //
            if (bean.type == Role.TYPE_全局) {
                bizService.addOptLog(account, bean.id, bean.name, OptLog.EVENT_ID_编辑企业角色, "name:" + bean.name);
            } else {
                bizService.addOptLog(account, bean.id, bean.name, OptLog.EVENT_ID_编辑项目角色, "name:" + bean.name);
            }
        }

        @Override
        @Transaction
        public void deleteRole(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            Role old = dao.getExistedByIdForUpdate(Role.class, id);
            if (old.type == Role.TYPE_全局) {
                bizService.checkCompanyPermission(account, Permission.ID_管理企业角色);
            }
            if (old.type == Role.TYPE_项目) {
                bizService.checkCompanyPermission(account, Permission.ID_管理项目角色);
            }
            if (old.isSystemRole) {
                throw new AppException("系统角色不能删除");
            }
            //
            dao.deleteById(Role.class, id);
            dao.deleteAccountRoles(old.id);
            //
            if (old.type == Role.TYPE_全局) {
                bizService.addOptLog(account, old.id, old.name, OptLog.EVENT_ID_删除企业角色, "name:" + old.name);
            } else {
                bizService.addOptLog(account, old.id, old.name, OptLog.EVENT_ID_删除项目角色, "name:" + old.name);
            }
        }

        //
        @Override
        public List<RoleInfo> getRoleInfoList(String token, int type) {
            Account account = bizService.getExistedAccountByToken(token);
            return dao.getRoleInfoList(account.companyId, type);
        }

        @Override
        public RoleInfo getRoleInfoById(String token, int id) {
            return dao.getExistedById(RoleInfo.class, id);
        }

        @Transaction
        @Override
        public void saveRoleInfoList(String token, List<Role> list) {
            Account account = bizService.getExistedAccountByToken(token);
            for (Role bean : list) {
                updateRole0(account, bean);
            }
        }

        @Override
        public List<ProjectDataPermissionInfo> getProjectDataPermissionInfoList(String token, int projectId, int objectType) {
            return dao.getProjectDataPermissionInfoList(projectId, objectType);
        }

        @Transaction
        @Override
        public void saveProjectDataPermissionInfoList(String token, List<ProjectDataPermission> list) {
            Account account = bizService.getExistedAccountByToken(token);
            for (ProjectDataPermission e : list) {
                ProjectDataPermission old = dao.getExistedByIdForUpdate(ProjectDataPermission.class, e.id);
                old.ownerList = e.ownerList;
                old.updateAccountId = account.id;
                dao.update(old);
            }
        }

        @Transaction
        @Override
        public void addProjectMembers(String token, int projectId, Set<Integer> accountIdList, Set<Integer> roleIds,
                                      List<String> tags) {
            Account account = bizService.getExistedAccountByToken(token);
            addProjectMembers0(account, projectId, accountIdList, roleIds, false, tags);
        }

        @Transaction
        @Override
        public void memberJoinProjects(String token, int accountId, Set<Integer> projectIdList, Set<Integer> roleIds) {
            Account optAccount = bizService.getExistedAccountByToken(token);
            if (!bizService.isPrivateDeploy(optAccount)) {
                throw new AppException("只支持私有部署环境");
            }
            Account account = dao.getExistedById(Account.class, accountId);
            if (account.companyId != optAccount.companyId) {
                throw new AppException("权限不足");
            }
            Set<Integer> accountIdList = new HashSet<>();
            accountIdList.add(account.id);
            //
            for (Integer roleId : roleIds) {
                Role role = dao.getById(Role.class, roleId);
                if (role == null) {
                    logger.error("roleId:{}", roleId);
                    throw new AppException("角色不存在");
                }
                if (role.companyId != account.companyId) {
                    throw new AppException("角色【" + role.name + "】不存在");
                }
            }
            //
            for (Integer projectId : projectIdList) {
                addProjectMembers0(optAccount, projectId, accountIdList, roleIds, false, null);
            }
        }

        /**
         * @param isJoin 是否是邀请加入saas
         */
        private void addProjectMembers0(Account optAccount, int projectId,
                                        Set<Integer> accountIdList, Set<Integer> roleIds, boolean isJoin,
                                        List<String> tags) {
            if (accountIdList == null || roleIds == null) {
                return;
            }
            Project project = dao.getExistedById(Project.class, projectId);
            if (!isJoin) {
                bizService.checkPermission(optAccount, project.companyId);
                Set<String> allPermissions = bizService.getAllGlobalPermission(optAccount, project.companyId);
                if (!allPermissions.contains(Permission.ID_管理成员)) {
                    bizService.checkProjectPermission(optAccount, project.companyId, projectId, Permission.ID_管理项目成员);
                }
            }
            List<ProjectMemberInfo> memberInfos = new ArrayList<>();
            for (Integer accountId : accountIdList) {
                if (accountId == null) {
                    throw new AppException("参数错误");
                }
                Account targetAccount = dao.getExistedById(Account.class, accountId);
                if (targetAccount.status == Account.STATUS_无效) {
                    throw new AppException("账号【" + targetAccount.name + "】无效,无法添加到项目中");
                }
                if (!targetAccount.isActivated) {
                    throw new AppException("账号【" + targetAccount.name + "】未激活,无法添加到项目中");
                }
                dao.getExistedCompanyMemberInfo(accountId, project.companyId);
                ProjectMemberInfo projectMember = dao.getProjectMemberInfoByProjectIdAccountId(projectId, accountId);
                if (projectMember == null) {
                    projectMember = new ProjectMemberInfo();
                    projectMember.companyId = project.companyId;
                    projectMember.accountId = accountId;
                    projectMember.projectId = projectId;
                    projectMember.tag = tags;
                    dao.add(projectMember);
                    //
                    ProjectMemberInfo addProjectMember = dao.getExistedById(ProjectMemberInfo.class, projectMember.id);
                    memberInfos.add(addProjectMember);
                }
                for (Integer roleId : roleIds) {
                    Role role = dao.getExistedById(Role.class, roleId);
                    if (role.companyId != project.companyId) {
                        throw new AppException("角色不存在");
                    }
                    ProjectMemberRole pmr = dao.getProjectMemberRole(projectMember.id, roleId);
                    if (pmr == null) {
                        addProjectMemberRole(projectMember, roleId);
                    }
                }
            }
            //
            if (memberInfos.size() > 0) {
                bizService.addChangeLog(optAccount, projectId, 0, ChangeLog.TYPE_新增项目成员,
                        JSONUtil.toJson(memberInfos));
            }
        }

        @Transaction
        @Override
        public void updateProjectMember(String token, int id, Set<Integer> roleIds, List<String> tag, boolean updateTags) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectMemberInfo info = dao.getExistedById(ProjectMemberInfo.class, id);
            bizService.checkPermissionForProjectAccess(account, info.projectId);
            Set<String> allPermissions = bizService.getAllGlobalPermission(account);
            if (!allPermissions.contains(Permission.ID_管理成员)) {
                bizService.checkProjectPermission(account, info.projectId, Permission.ID_管理项目成员);
            }
            //
            StringBuilder beforeRoleNames = new StringBuilder();
            ProjectMemberRoleQuery query = new ProjectMemberRoleQuery();
            query.projectMemberId = id;
            query.pageSize = Integer.MAX_VALUE;
            List<ProjectMemberRoleInfo> old = dao.getList(query);
            for (ProjectMemberRoleInfo e : old) {
                beforeRoleNames.append(e.roleName).append(",");
            }
            if (beforeRoleNames.length() > 0) {
                beforeRoleNames.deleteCharAt(beforeRoleNames.length() - 1);
            }
            //
            dao.deleteProjectMemberRolesByProjectMemberId(id);
            //
            StringBuilder afterRoleNames = new StringBuilder();
            for (Integer roleId : roleIds) {
                Role role = dao.getExistedById(Role.class, roleId);
                if (role.companyId != account.companyId) {
                    throw new AppException("角色不存在");
                }
                addProjectMemberRole(info, roleId);
                afterRoleNames.append(role.name).append(",");
            }
            if (afterRoleNames.length() > 0) {
                afterRoleNames.deleteCharAt(afterRoleNames.length() - 1);
            }
            //
            List<ChangeLogItem> itemList = new ArrayList<>();
            ChangeLogItem item = new ChangeLogItem();
            item.name = "角色";
            item.beforeContent = beforeRoleNames.toString();
            item.afterContent = afterRoleNames.toString();
            if (item.beforeContent.equals(item.afterContent)) {
                itemList.add(item);
            }
            //
            if (updateTags) {
                info.tag = tag;
            }
            dao.update(info);
            //
            bizService.addChangeLog(account, info.projectId, id + "", itemList, info.accountName);
        }

        //
        private void addProjectMemberRole(ProjectMember pm, int roleId) {
            ProjectMemberRole bean = new ProjectMemberRole();
            bean.projectMemberId = pm.id;
            bean.companyId = pm.companyId;
            bean.accountId = pm.accountId;
            bean.projectId = pm.projectId;
            bean.roleId = roleId;
            dao.add(bean);
        }

        //
        @Override
        public List<ProjectMemberInfo> getProjectMemberInfoList(String token, int projectId) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkPermissionForProjectAccess(account, projectId);
            Set<String> allPermissions = bizService.getAllGlobalPermission(account);
            if (!allPermissions.contains(Permission.ID_管理成员)) {
                bizService.checkProjectPermission(account, projectId, Permission.ID_管理项目成员);
            }
            return bizService.getProjectMemberInfoList(projectId);
        }
        //

        @Transaction
        @Override
        public void deleteProjectMember(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            deleteProjectMember0(account, id);
        }

        @Override
        public List<String> getProjectMemberTags(String token, int projectId) {
            bizService.getExistedAccountByToken(token);
            List<String> tags = dao.getProjectMemberTags(projectId);
            Set<String> set = new LinkedHashSet<>();
            for (String e : tags) {
                List<String> list = JSONUtil.fromJsonList(e, String.class);
                if (list != null) {
                    for (String t : list) {
                        if (t == null) {
                            continue;
                        }
                        set.add(t.trim());
                    }
                }
            }
            return BizUtil.convert(set);
        }

        public void deleteProjectMember0(Account account, int projectMemberId) {
            ProjectMemberInfo oldInfo = dao.getExistedById(ProjectMemberInfo.class, projectMemberId);
            bizService.checkCompanyPermission(account, Permission.ID_管理成员);
            bizService.checkPermissionForProjectAccess(account, oldInfo.projectId);
            if (account.id == oldInfo.accountId) {
                throw new AppException("不能将自己移出项目");
            }
            Set<String> allPermissions = bizService.getAllGlobalPermission(account);
            if (!allPermissions.contains(Permission.ID_管理成员)) {
                bizService.checkProjectPermission(account, oldInfo.projectId, Permission.ID_管理项目成员);
            }
            //
            ProjectMember old = dao.getExistedByIdForUpdate(ProjectMember.class, projectMemberId);
            dao.deleteById(ProjectMember.class, old.id);
            dao.deleteProjectMemberRolesByProjectMemberId(projectMemberId);
            //
            bizService.addChangeLog(account, old.projectId, 0, 0, ChangeLog.TYPE_删除项目成员,
                    projectMemberId + "", JSONUtil.toJson(oldInfo), "");
        }

        @Transaction
        @Override
        public int createProjectIteration(String token, ProjectIterationInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            Project project = dao.getExistedByIdForUpdate(Project.class, bean.projectId);
            bizService.checkProjectPermission(account, project.id, Permission.ID_创建和编辑迭代);
            //
            checkDataDictValueValid("ProjectIteration.status", bean.status, "状态错误");
            bean.createAccountId = account.id;
            calcStartDateEndDate(bean);
            bean.companyId = project.companyId;
            BizUtil.checkUniqueKeysOnAdd(dao, bean, "迭代名称不能重复");
            BizUtil.checkValid(bean);
            dao.add(bean);
            addProjectIterationStepList(account.id, project.companyId, bean);
            bizService.addChangeLog(account, bean.projectId, 0,
                    ChangeLog.TYPE_新增迭代, JSONUtil.toJson(bean));
            //
            project.iterationId = bean.id;
            dao.update(project);
            //
            return bean.id;
        }

        @Transaction
        @Override
        public void updateProjectIteration(String token, ProjectIterationInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectIterationInfo old = dao.getExistedByIdForUpdate(ProjectIterationInfo.class, bean.id);
            bizService.checkPermission(account, old.companyId);
            bizService.checkProjectPermission(account, old.projectId, Permission.ID_创建和编辑迭代);
            //
            checkDataDictValueValid("ProjectIteration.status", bean.status, "状态错误");
            BizUtil.checkUniqueKeysOnUpdate(dao, bean, old, "迭代名称不能重复");
            calcStartDateEndDate(bean);
            //修复迭代未同步企业ID的问题
            if (old.companyId == 0) {
                Project pro = dao.getById(Project.class, old.projectId);
                old.companyId = pro.companyId;
            }
            old.name = bean.name;
            old.description = bean.description;
            old.status = bean.status;
            old.startDate = bean.startDate;
            old.endDate = bean.endDate;
            old.updateAccountId = account.id;
            BizUtil.checkValid(old);
            dao.update(old);
            //1.删除所有阶段
            dao.deleteProjectIterationSteps(bean.id);
            addProjectIterationStepList(account.id, old.companyId, bean);
            //
            bizService.addChangeLog(account, old.projectId, 0,
                    ChangeLog.TYPE_编辑迭代, JSONUtil.toJson(old));

        }

        private void addProjectIterationStepList(int accountId, int companyId, ProjectIterationInfo bean) {
            for (ProjectIterationStep step : bean.stepList) {
                if (StringUtil.isEmpty(step.name)) {
                    continue;
                }
                step.companyId = companyId;
                step.projectId = bean.projectId;
                step.iterationId = bean.id;
                step.createAccountId = accountId;
                BizUtil.checkUniqueKeysOnAdd(dao, step, "阶段名称不能重复");
                BizUtil.checkValid(step);
                dao.add(step);
            }
        }

        //计算startDate endDate
        private void calcStartDateEndDate(ProjectIterationInfo bean) {
            if (bean.stepList == null || bean.stepList.isEmpty()) {
                throw new AppException("阶段不能为空");
            }
            Date startDate = null;
            Date endDate = null;
            for (ProjectIterationStep step : bean.stepList) {
                if (StringUtil.isEmpty(step.name)) {
                    continue;
                }
                if (step.startDate == null) {
                    throw new AppException("阶段开始时间不能为空");
                }
                if (step.endDate == null) {
                    throw new AppException("阶段结束时间不能为空");
                }
                if (step.startDate.after(step.endDate)) {
                    throw new AppException("阶段开始时间不能晚于结束时间");
                }
                if (startDate == null || startDate.after(step.startDate)) {
                    startDate = step.startDate;
                }
                if (endDate == null || endDate.before(step.endDate)) {
                    endDate = step.endDate;
                }
            }
            if (startDate == null) {
                throw new AppException("阶段不能为空");
            }
            bean.startDate = startDate;
            bean.endDate = endDate;
            //
//			if(DateUtil.differentDays(bean.startDate,bean.endDate)>365) {
//				throw new AppException("时间间隔不能大于1年");
//			}
//			if(DateUtil.differentDays(bean.startDate,bean.endDate)<2) {
//				throw new AppException("时间间隔不能小于2天");
//			}
        }

        @Transaction
        @Override
        public void deleteProjectIteration(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectIterationInfo old = dao.getExistedByIdForUpdate(ProjectIterationInfo.class, id);
            Project project = dao.getExistedByIdForUpdate(Project.class, old.projectId);
            bizService.checkPermission(account, old.companyId);
            bizService.checkProjectPermission(account, old.projectId, Permission.ID_创建和编辑迭代);
            //
            TaskQuery taskQuery = new TaskQuery();
            taskQuery.isDelete = false;
            taskQuery.iterationId = id;
            int taskCount = dao.getListCount(taskQuery);
            if (taskCount > 0) {
                throw new AppException("有" + taskCount + "个对象关联了此迭代，删除失败");
            }
            //
            if (old.isDelete) {
                return;
            }
            old.isDelete = true;
            old.updateAccountId = account.id;
            dao.deleteProjectIterationSteps(id);
            bizService.addChangeLog(account, old.projectId, 0,
                    ChangeLog.TYPE_删除迭代, JSONUtil.toJson(old));
            //
            old.originalName = old.name;
            old.name = BizUtil.randomUUID();
            dao.update(old);
            //修改project关联的迭代是上一个迭代
            Integer iterationId = dao.getLastProjectIterationId(old.projectId);
            if (iterationId == null) {
                iterationId = 0;
            }
            project.iterationId = iterationId;
            dao.update(project);
            //
            addCompanyRecycle(account, old.id, CompanyRecycle.TYPE_迭代, 0, old.originalName);
        }

        @Override
        public ProjectIterationInfo getProjectIterationInfoById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectIterationInfo bean = dao.getExistedById(ProjectIterationInfo.class, id);
            bizService.checkPermissionForProjectAccess(account, bean.projectId);
            ProjectIterationStepQuery query = new ProjectIterationStepQuery();
            query.iterationId = id;
            query.pageSize = Integer.MAX_VALUE;
            bean.stepList = dao.getList(query);
            return bean;
        }

        @Override
        public List<ProjectIterationInfo> getProjectIterationInfoList(String token, int projectId) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkPermissionForProjectAccess(account, projectId);
            //
            ProjectIterationQuery query = new ProjectIterationQuery();
            query.projectId = projectId;
            query.pageSize = Integer.MAX_VALUE;
            return dao.getList(query);
        }

        @Override
        public Map<String, Object> getProjectIterationList(String token, ProjectIterationQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkPermissionForProjectAccess(account, query.projectId);
            setupQuery(account, query);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        @Override
        public Map<String, Object> getProjectReleaseList(String token, ProjectReleaseQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkPermissionForProjectAccess(account, query.projectId);
            setupQuery(account, query);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        @Override
        public Map<String, Object> getProjectSubSystemList(String token, ProjectSubSystemQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkPermissionForProjectAccess(account, query.projectId);
            setupQuery(account, query);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        @Transaction
        @Override
        public int createProjectRelease(String token, ProjectRelease bean) {
            Account account = bizService.getExistedAccountByToken(token);
            Project project = dao.getExistedById(Project.class, bean.projectId);
            bizService.checkPermission(account, project.companyId);
            bizService.checkProjectPermission(account, project.id, Permission.ID_创建和编辑Release);
            //
            bean.createAccountId = account.id;
            bean.description = bizService.getContent(bean.description);
            bean.companyId = project.companyId;
            BizUtil.checkUniqueKeysOnAdd(dao, bean, "名称为【" + bean.name + "】的Release已经存在");
            BizUtil.checkValid(bean);
            dao.add(bean);
            bizService.addChangeLog(account, bean.projectId, 0, ChangeLog.TYPE_新增Release, JSONUtil.toJson(bean));
            return bean.id;
        }

        @Transaction
        @Override
        public void updateProjectRelease(String token, ProjectRelease bean) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectReleaseInfo old = dao.getExistedByIdForUpdate(ProjectReleaseInfo.class, bean.id);
            bizService.checkPermission(account, old.companyId);
            bizService.checkProjectPermission(account, old.projectId, Permission.ID_创建和编辑Release);
            //
            BizUtil.checkUniqueKeysOnUpdate(dao, bean, old, "名称为【" + bean.name + "】的Release已经存在");
            bean.description = bizService.getContent(bean.description);
            old.name = bean.name;
            old.releaseDate = bean.releaseDate;
            old.category = bean.category;
            old.description = bean.description;
            old.updateAccountId = account.id;
            BizUtil.checkValid(old);
            dao.update(old);
        }

        @Transaction
        @Override
        public void deleteProjectRelease(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectReleaseInfo old = dao.getExistedByIdForUpdate(ProjectReleaseInfo.class, id);
            bizService.checkPermission(account, old.companyId);
            bizService.checkProjectPermission(account, old.projectId, Permission.ID_创建和编辑Release);
            //
            if (old.isDelete) {
                return;
            }
            old.isDelete = true;
            old.updateAccountId = account.id;
            //
            TaskQuery taskQuery = new TaskQuery();
            taskQuery.isDelete = false;
            taskQuery.releaseId = id;
            int taskCount = dao.getListCount(taskQuery);
            if (taskCount > 0) {
                throw new AppException("有" + taskCount + "个对象关联了此Release，删除失败");
            }
            //
            bizService.addChangeLog(account, old.projectId, 0, ChangeLog.TYPE_删除Release, JSONUtil.toJson(old));
            //
            old.originalName = old.name;
            old.name = BizUtil.randomUUID();
            dao.update(old);
        }

        @Override
        public ProjectReleaseInfo getProjectReleaseInfo(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectReleaseInfo bean = dao.getExistedById(ProjectReleaseInfo.class, id);
            bizService.checkPermission(account, bean.companyId);
            bizService.checkProjectPermission(account, bean.projectId, Permission.ID_创建和编辑Release);
            return bean;
        }

        @Transaction
        @Override
        public int createProjectSubSystem(String token, ProjectSubSystem bean) {
            Account account = bizService.getExistedAccountByToken(token);
            Project project = dao.getExistedById(Project.class, bean.projectId);
            bizService.checkPermission(account, project.companyId);
            bizService.checkProjectPermission(account, bean.projectId, Permission.ID_创建和编辑子系统);
            //
            checkDataDictValueValid("ProjectSubSystem.status", bean.status, "状态错误");
            bean.createAccountId = account.id;
            bean.companyId = project.companyId;
            BizUtil.checkUniqueKeysOnAdd(dao, bean, "名称为【" + bean.name + "】的子系统已经存在");
            BizUtil.checkValid(bean);
            dao.add(bean);
            bizService.addChangeLog(account, bean.projectId, 0, ChangeLog.TYPE_新增子系统, JSONUtil.toJson(bean));
            return bean.id;
        }

        @Transaction
        @Override
        public void updateProjectSubSystem(String token, ProjectSubSystem bean) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectSubSystemInfo old = dao.getExistedByIdForUpdate(ProjectSubSystemInfo.class, bean.id);
            bizService.checkPermission(account, old.companyId);
            bizService.checkProjectPermission(account, old.projectId, Permission.ID_创建和编辑子系统);
            //
            checkDataDictValueValid("ProjectSubSystem.status", bean.status, "状态错误");
            BizUtil.checkUniqueKeysOnUpdate(dao, bean, old, "名称为【" + bean.name + "】的子系统已经存在");
            old.name = bean.name;
            old.status = bean.status;
            old.description = bean.description;
            old.updateAccountId = account.id;
            BizUtil.checkValid(old);
            dao.update(old);
        }

        @Transaction
        @Override
        public void deleteProjectSubSystem(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectSubSystemInfo old = dao.getExistedByIdForUpdate(ProjectSubSystemInfo.class, id);
            bizService.checkPermission(account, old.companyId);
            bizService.checkProjectPermission(account, old.projectId, Permission.ID_创建和编辑子系统);
            if (old.isDelete) {
                return;
            }
            //
            TaskQuery taskQuery = new TaskQuery();
            taskQuery.isDelete = false;
            taskQuery.subSystemId = id;
            int taskCount = dao.getListCount(taskQuery);
            if (taskCount > 0) {
                throw new AppException("有" + taskCount + "个对象关联了此子系统，删除失败");
            }
            //
            old.isDelete = true;
            old.updateAccountId = account.id;

            //
            bizService.addChangeLog(account, old.projectId, 0, ChangeLog.TYPE_删除子系统, JSONUtil.toJson(old));
            //
            old.originalName = old.name;
            old.name = BizUtil.randomUUID();
            dao.update(old);
        }

        @Override
        public ProjectSubSystemInfo getProjectSubSystemInfo(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectSubSystemInfo bean = dao.getExistedById(ProjectSubSystemInfo.class, id);
            bizService.checkPermissionForProjectAccess(account, bean.projectId);
            bizService.checkProjectPermission(account, bean.projectId, Permission.ID_创建和编辑子系统);
            return bean;
        }


        @Override
        public TaskQueryInfo getTaskQueryInfo(String token, String projectUuid, int objectType) {
            Account account = bizService.getExistedAccountByToken(token);
            Project project = dao.getProjectInfoByUuid(projectUuid);
            checkNull(project, "项目");
            int projectId = project.id;
            //
            TaskQueryInfo info = new TaskQueryInfo();
            info.project = project;
            info.iterationList = bizService.getProjectIterationListByProjectId(projectId);
            info.memberList = bizService.getProjectMemberInfoListByProjectId(projectId);
            info.statusList = bizService.getProjectStatusDefineInfoListByProjectIdObjectType(projectId, objectType);
            info.priorityList = bizService.getProjectPriorityDefineInfoListByProjectIdObjectType(projectId, objectType);
            info.fieldList = bizService.getProjectFieldDefineList(projectId, objectType);
            //项目清单屏蔽状态字段statusName
            if (objectType == Task.OBJECTTYPE_项目清单) {
                if (!BizUtil.isNullOrEmpty(info.fieldList)) {
                    Iterator<ProjectFieldDefineInfo> iterator = info.fieldList.iterator();
                    while (iterator.hasNext()) {
                        if ("statusName".equals(iterator.next().field)) {
                            iterator.remove();
                            break;
                        }
                    }
                }
            }
            info.releaseList = bizService.getProjectReleaseInfoListByProjectId(projectId);
            info.subSystemList = bizService.getProjectSubSystemInfoListByProjectId(projectId);
            info.categoryList = bizService.getCategoryInfoList(projectId, objectType);
            info.filterList = bizService.getFilterList(account, projectId, objectType);
            //
            ObjectType ot = dao.getExistedById(ObjectType.class, objectType);
            //
            info.filterList.add(0, new FilterInfo(-8, "提前完成的" + ot.name));
            info.filterList.add(0, new FilterInfo(-7, "今天到期的" + ot.name));
            info.filterList.add(0, new FilterInfo(-6, "逾期的" + ot.name));
            info.filterList.add(0, new FilterInfo(-5, "我参与的" + ot.name));
            info.filterList.add(0, new FilterInfo(-4, "未完成的" + ot.name));
            info.filterList.add(0, new FilterInfo(-3, "已完成的" + ot.name));
            info.filterList.add(0, new FilterInfo(-2, "待认领的" + ot.name));
            info.filterList.add(0, new FilterInfo(-1, "我的" + ot.name));
            //可行性
            info.isPublic = bizService.checkTaskViewPermission(account.id, projectId, objectType);
            //
            return info;
        }

        @Override
        public TaskEditInfo getEditTaskInfo(String token, int projectId, int objectType) {
            Account account = bizService.getExistedAccountByToken(token);
            return getEditTaskInfo0(account, projectId, objectType);
        }

        @Override
        public FilterEditTaskInfo getFilterEditTaskInfo(String token, int projectId, int objectType) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkPermissionForProjectAccess(account, projectId);
            FilterEditTaskInfo info = new FilterEditTaskInfo();
            info.iterationList = bizService.getProjectIterationListByProjectId(projectId);
            info.memberList = bizService.getProjectMemberInfoListByProjectId(projectId);
            info.statusList = bizService.getProjectStatusDefineInfoListByProjectIdObjectType(projectId, objectType);
            info.priorityList = bizService.getProjectPriorityDefineInfoListByProjectIdObjectType(projectId, objectType);
            info.releaseList = bizService.getProjectReleaseInfoListByProjectId(projectId);
            info.subSystemList = bizService.getProjectSubSystemInfoListByProjectId(projectId);
            info.fieldList = bizService.getProjectFieldDefineList(projectId, objectType);
            info.stageList = dao.getProjectStageList(account.companyId, projectId);
            //
            CategoryQuery categoryQuery = new CategoryQuery();
            categoryQuery.projectId = projectId;
            categoryQuery.objectType = objectType;
            categoryQuery.objectTypeSort = Query.SORT_TYPE_ASC;
            categoryQuery.sortWeightSort = Query.SORT_TYPE_ASC;
            categoryQuery.pageSize = Integer.MAX_VALUE;
            info.categoryList = dao.getList(categoryQuery);
            //
            return info;
        }


        private TaskEditInfo getEditTaskInfo0(Account account, int projectId, int objectType) {
            TaskEditInfo info = new TaskEditInfo();
            info.project = dao.getExistedById(ProjectInfo.class, projectId);
            info.iterationList = bizService.getProjectIterationListByProjectId(projectId);
            info.memberList = bizService.getProjectMemberInfoListByProjectId(projectId);
            info.statusList = bizService.getProjectStatusDefineInfoListByProjectIdObjectType(projectId, objectType);
            info.priorityList = bizService.getProjectPriorityDefineInfoListByProjectIdObjectType(projectId, objectType);
            info.releaseList = bizService.getProjectReleaseInfoListByProjectId(projectId);
            info.subSystemList = bizService.getProjectSubSystemInfoListByProjectId(projectId);
            info.fieldList = bizService.getProjectFieldDefineList(projectId, objectType);
            info.categoryNodeList = getCategoryNodeList0(account, projectId, objectType);
            info.objectTypeTemplate = dao.getProjectObjectTypeTemplateByProjectIdObjectType(projectId, objectType);
            info.stageList = dao.getProjectStageList(account.companyId, projectId);
            info.repositoryList = bizService.getCompanyVersionRepositoryList(account.companyId);
            //
            return info;
        }

        @Transaction
        @Override
        public int createTask(String token, TaskDetailInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            TaskDetailInfo task = createTask0(account, bean, false);
            //
            if (CornerstoneBizSystem.webEventServer != null) {
                AutoTranscationCallback.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        logger.info("afterCommit");
                        WebEvent event = WebEvent.createWebEvent(account.id, account.companyId, WebEvent.TYPE_创建任务,
                                task.objectType, task.projectId, task.id);
                        CornerstoneBizSystem.webEventServer.boardcastAsync(event);
                    }
                });
            }

            return task.id;
        }

        private TaskDetailInfo createTask0(Account account, TaskDetailInfo bean, boolean isImportCreate) {
            return bizService.createTask(account, bean, true, isImportCreate);
        }

        @Transaction
        @Override
        public void createWikiTask(String token, TaskDetailInfo bean, int wikiPageId, String nodeId, boolean onlyLeafNode) {
            Account account = bizService.getExistedAccountByToken(token);
            WikiPage wikiPage = dao.getExistedByIdForUpdate(WikiPage.class, wikiPageId);
            if (wikiPage.isDelete) {
                throw new AppException("页面不存在");
            }
            if (wikiPage.status != WikiPage.STATUS_已发布) {
                throw new AppException("只有已发布的页面才能创建");
            }
            bizService.checkPermission(account, wikiPage.companyId);
            //
            WikiContent content = dao.getExistedByIdForUpdate(WikiContent.class, wikiPage.contentId);
            MindMapTree tree = JSONUtil.fromJson(content.content, MindMapTree.class);
            //
            if (tree == null || tree.data == null || tree.data.children == null) {
                return;
            }
            if (tree.data.children.size() == 0) {
                return;
            }
            Set<Integer> taskIdSet = new LinkedHashSet<>();
            if ("root".equals(nodeId)) {
                nodeId = null;
            }
            if (BizUtil.isNullOrEmpty(nodeId)) {
                for (Children child : tree.data.children) {
                    createWikiTask0(taskIdSet, account, child, bean, onlyLeafNode);
                }
            } else {
                //指定节点生成任务
                AtomicReference<Children> child = new AtomicReference<>();
                recursiveChild(child, tree.data.children, nodeId);
                createWikiTask0(taskIdSet, account, child.get(), bean, onlyLeafNode);
            }

            if (taskIdSet.size() > 0) {
                content.content = JSONUtil.toJson(tree);
                dao.update(content);
                //
                wikiPage.isAssociatedTask = true;
                dao.update(wikiPage);
            }
        }

        private void recursiveChild(AtomicReference<Children> childReference, List<Children> children, String nodeId) {
            for (Children child : children) {
                if (null != childReference.get() && !BizUtil.isNullOrEmpty(childReference.get().id)) {
                    return;
                }
                if (nodeId.equals(child.id)) {
                    childReference.set(child);
                    return;
                } else {
                    List<Children> childrenList = child.children;
                    if (!BizUtil.isNullOrEmpty(childrenList)) {
                        recursiveChild(childReference, childrenList, nodeId);
                    }
                }
            }
        }

        //
        //
        private void createWikiTask0(Set<Integer> taskIdSet, Account account, Children child, TaskDetailInfo bean, boolean onlyLeafNode) {
            if (child == null) {
                return;
            }
            if (BizUtil.isNullOrEmpty(child.id) || child.id.startsWith("task_")) {//已经关联了
                return;
            }

            if (onlyLeafNode) {
                if (BizUtil.isNullOrEmpty(child.children)) {
                    bean.name = child.topic;
                    if (StringUtil.isEmpty(bean.name)) {
                        return;
                    }
                    TaskDetailInfo task = createTask0(account, bean, false);
                    setNewTopic(task, child);
                    taskIdSet.add(task.id);
                } else {
                    for (Children c : child.children) {
                        createWikiTask0(taskIdSet, account, c, bean, onlyLeafNode);
                    }
                }
            } else {
                bean.name = child.topic;
                if (StringUtil.isEmpty(bean.name)) {
                    return;
                }
                TaskDetailInfo task = createTask0(account, bean, false);
                setNewTopic(task, child);
                taskIdSet.add(task.id);
                if (!BizUtil.isNullOrEmpty(child.children)) {
                    for (Children c : child.children) {
                        createWikiTask0(taskIdSet, account, c, bean, onlyLeafNode);
                    }
                }
            }
        }

        //
        @Transaction
        @Override
        public void createSeniorWikiTask(String token, TaskDetailInfo bean, int wikiPageId, String nodeId, boolean onlyLeafNode) {
            Account account = bizService.getExistedAccountByToken(token);
            WikiPage wikiPage = dao.getExistedByIdForUpdate(WikiPage.class, wikiPageId);
            if (wikiPage.isDelete) {
                throw new AppException("页面不存在");
            }
            if (wikiPage.status != WikiPage.STATUS_已发布) {
                throw new AppException("只有已发布的页面才能创建");
            }
            bizService.checkPermission(account, wikiPage.companyId);
            //
            WikiContent content = dao.getExistedByIdForUpdate(WikiContent.class, wikiPage.contentId);
            SeniorMindMapTree tree = JSONUtil.fromJson(content.content, SeniorMindMapTree.class);
            //
            if (tree == null || tree.root == null || tree.root.children == null) {
                return;
            }
            if (tree.root.children.size() == 0) {
                return;
            }
            Set<Integer> taskIdSet = new LinkedHashSet<>();
            if ("root".equals(nodeId)) {
                nodeId = null;
            }
            if (BizUtil.isNullOrEmpty(nodeId)) {
                for (SeniorChildren child : tree.root.children) {
                    createSeniorWikiTask0(taskIdSet, account, child, bean, onlyLeafNode);
                }
            } else {
                //指定节点生成任务
                AtomicReference<SeniorChildren> child = new AtomicReference<>(new SeniorChildren());
                recursiveSeniorChild(child, tree.root.children, nodeId);
                createSeniorWikiTask0(taskIdSet, account, child.get(), bean, onlyLeafNode);
            }

            if (taskIdSet.size() > 0) {
                content.content = JSONUtil.toJson(tree);
                dao.update(content);
                //
                wikiPage.isAssociatedTask = true;
                dao.update(wikiPage);
            }
        }

        private void recursiveSeniorChild(AtomicReference<SeniorChildren> sc, List<SeniorChildren> children, String nodeId) {
            for (SeniorChildren child : children) {
                if (null != sc && null != sc.get() && null != sc.get().data && null != sc.get().data.id) {
                    return;
                }
                if (nodeId.equals(child.data.id)) {
                    sc.set(child);
                    return;
                } else {
                    List<SeniorChildren> childrenList = child.children;
                    if (!BizUtil.isNullOrEmpty(childrenList)) {
                        recursiveSeniorChild(sc, childrenList, nodeId);
                    }
                }
            }
        }

        //
        //
        private void createSeniorWikiTask0(Set<Integer> taskIdSet, Account account, SeniorChildren child, TaskDetailInfo bean, boolean onlyLeafNode) {
            if (child == null || child.data == null || StringUtil.isEmpty(child.data.text)) {
                return;
            }
            if (BizUtil.isNullOrEmpty(child.data) || BizUtil.isNullOrEmpty(child.data.id) || child.data.id.startsWith("task_")) {//已经关联了
                return;
            }
            if (onlyLeafNode) {
                if (BizUtil.isNullOrEmpty(child.children)) {
                    bean.name = child.data.text;
                    if (StringUtil.isEmpty(bean.name)) {
                        return;
                    }
                    TaskDetailInfo task = createTask0(account, bean, false);
                    setSeniorNewTopic(task, child);
                    taskIdSet.add(task.id);
                } else {
                    for (SeniorChildren c : child.children) {
                        createSeniorWikiTask0(taskIdSet, account, c, bean, onlyLeafNode);
                    }
                }
            } else {
                bean.name = child.data.text;
                if (StringUtil.isEmpty(bean.name)) {
                    return;
                }
                TaskDetailInfo task = createTask0(account, bean, false);
                setSeniorNewTopic(task, child);
                taskIdSet.add(task.id);
                if (!BizUtil.isNullOrEmpty(child.children)) {
                    for (SeniorChildren c : child.children) {
                        createSeniorWikiTask0(taskIdSet, account, c, bean, onlyLeafNode);
                    }
                }
            }
        }

        //
        private void setSeniorNewTopic(TaskInfo task, SeniorChildren child) {
            child.data.id = "task_" + task.uuid;
            if (StringUtil.isEmpty(task.statusName)) {
                child.data.text = "#" + task.serialNo + " " + child.data.text;
            } else {
                child.data.text = "#" + task.serialNo + "[" + task.statusName + "]" + child.data.text;
            }
            child.data.background = task.statusColor;
        }

        //
        private void setNewTopic(TaskInfo task, Children child) {
            child.id = "task_" + task.uuid;
            if (StringUtil.isEmpty(task.statusName)) {
                child.topic = "#" + task.serialNo + " " + child.topic;
            } else {
                child.topic = "#" + task.serialNo + "[" + task.statusName + "]" + child.topic;
            }
            child.backgroundColor = task.statusColor;
        }

        //
        private void updateNewTopic(TaskInfo task, Children child) {
            child.topic = "#" + task.serialNo + "[" + task.statusName + "]" + task.name;
            child.backgroundColor = task.statusColor;
        }

        //
        private void updateSeniorNewTopic(TaskInfo task, SeniorChildren child) {
            child.data.text = "#" + task.serialNo + "[" + task.statusName + "]" + task.name;
            child.data.background = task.statusColor;
        }
        //

        /**
         * 更新脑图数据
         */
        @Transaction
        @Override
        public WikiPageDetailInfo updateWikiPageByTasks(String token, int wikiPageId) {
            Account account = bizService.getExistedAccountByToken(token);
            WikiPage wikiPage = dao.getExistedById(WikiPage.class, wikiPageId);
            bizService.checkPermission(account, wikiPage.companyId);
            if (wikiPage.isDelete) {
                throw new AppException("页面不存在");
            }
            if (!wikiPage.isAssociatedTask) {
                throw new AppException("没有关联任务");
            }
            WikiContent content = dao.getExistedByIdForUpdate(WikiContent.class, wikiPage.contentId);
            if (content == null) {
                throw new AppException("数据错误");
            }
            MindMapTree tree = JSONUtil.fromJson(content.content, MindMapTree.class);
            //
            if (tree == null || tree.data == null || tree.data.children == null) {
                throw new AppException("没有关联任务");
            }
            if (tree.data.children.size() == 0) {
                throw new AppException("没有关联任务");
            }
            //
            for (Children child : tree.data.children) {
                updateMindMapTree(child);
            }
            //
            content.content = JSONUtil.toJson(tree);
            dao.update(content);
            //
            return dao.getExistedById(WikiPageDetailInfo.class, wikiPageId);
        }

        //
        @Transaction
        @Override
        public WikiPageInfo updateSeniorWikiPageByTasks(String token, int wikiPageId) {
            Account account = bizService.getExistedAccountByToken(token);
            WikiPage wikiPage = dao.getExistedById(WikiPage.class, wikiPageId);
            bizService.checkPermission(account, wikiPage.companyId);
            if (wikiPage.isDelete) {
                throw new AppException("页面不存在");
            }
            if (!wikiPage.isAssociatedTask) {
                throw new AppException("没有关联任务");
            }
            WikiContent content = dao.getExistedByIdForUpdate(WikiContent.class, wikiPage.contentId);
            if (content == null) {
                throw new AppException("数据错误");
            }
            SeniorMindMapTree tree = JSONUtil.fromJson(content.content, SeniorMindMapTree.class);
            //
            if (tree == null || tree.root == null || tree.root.children == null) {
                throw new AppException("没有关联任务");
            }
            if (tree.root.children.size() == 0) {
                throw new AppException("没有关联任务");
            }
            //
            for (SeniorChildren child : tree.root.children) {
                updateSeniorMindMapTree(child);
            }
            //
            content.content = JSONUtil.toJson(tree);
            dao.update(content);
            //
            return dao.getExistedById(WikiPageDetailInfo.class, wikiPageId);
        }

        //
        private void updateMindMapTree(Children child) {
            if (child == null) {
                return;
            }
            if (child.children == null || child.children.size() == 0) {
                if (child.id.startsWith("task_")) {
                    String uuid = child.id.substring(5);
                    TaskInfo task = dao.getTaskInfoByUuid(uuid);
                    if (task == null) {
                        return;
                    }
                    if (task.isDelete) {
                        return;
                    }
                    updateNewTopic(task, child);
                }
            } else {
                for (Children c : child.children) {
                    updateMindMapTree(c);
                }
            }
        }

        //
        private void updateSeniorMindMapTree(SeniorChildren child) {
            if (child == null) {
                return;
            }
            if (child.children == null || child.children.size() == 0) {//说明是任务
                String id = child.data.id;
                if (id.startsWith("task_")) {
                    String uuid = id.substring(5);
                    TaskInfo task = dao.getTaskInfoByUuid(uuid);
                    if (task == null) {
                        return;
                    }
                    if (task.isDelete) {
                        return;
                    }
                    updateSeniorNewTopic(task, child);
                }
            } else {
                for (SeniorChildren c : child.children) {
                    updateSeniorMindMapTree(c);
                }
            }
        }

        //
        @Transaction
        @Override
        public int batchAddTaskList(String token, List<TaskDetailInfo> list) {
            Account account = bizService.getExistedAccountByToken(token);
            for (TaskDetailInfo bean : list) {
                createTask0(account, bean, false);
            }
            return list.size();
        }


        @Transaction
        @Override
        public void addBugFromTestPlanTestCase(String token, int testPlanTestCaseId, int bugId) {
            Account account = bizService.getExistedAccountByToken(token);
            TestPlanTestCase bean = dao.getExistedById(TestPlanTestCase.class, testPlanTestCaseId);
            bizService.checkPermissionForProjectAccess(account, bean.projectId);
            TaskInfo bug = dao.getExistedTaskByIdObjectType(bugId, Task.OBJECTTYPE_缺陷);
            TaskInfo testCase = dao.getExistedTaskByIdObjectType(bean.testCaseId, Task.OBJECTTYPE_测试用例);
            bizService.checkProjectId(bean.projectId, bug.projectId, "缺陷不存在");
            bizService.addTaskAssociated(account, bug, 0, testCase);
        }

        @Transaction
        @Override
        public void updateTask(String token, TaskDetailInfo bean, List<String> updateFields, boolean isManualUpdate) {
            Account account = bizService.getExistedAccountByToken(token);
            TaskInfo task = bizService.updateTask0(account, bean, updateFields, false, isManualUpdate);
            //
            if (task != null) {
                if (CornerstoneBizSystem.webEventServer != null) {
                    AutoTranscationCallback.registerSynchronization(new TransactionSynchronizationAdapter() {
                        @Override
                        public void afterCommit() {
                            WebEvent event = WebEvent.createWebEvent(account.id, account.companyId,
                                    WebEvent.TYPE_编辑任务, task.objectType, task.projectId, task.id);
                            CornerstoneBizSystem.webEventServer.boardcastAsync(event);
                        }
                    });
                }
            }
        }
        //

        @Transaction
        @Override
        public void batchUpdateTask(String token, BatchUpdateTaskInfo bean, Set<Integer> ids, List<String> updateFields) {
            Account account = bizService.getExistedAccountByToken(token);
            if (updateFields == null || updateFields.isEmpty()) {
                throw new AppException("请选择更新项");
            }
            TaskDetailInfo task = new TaskDetailInfo();
            if (bean.name != null) {
                task.name = bean.name;
            }
            if (bean.iterationId != null) {
                task.iterationId = bean.iterationId;
            }
            if (bean.repositoryVersionId != null) {
                task.repositoryVersionId = bean.repositoryVersionId;
            }
            if (bean.repositoryId != null) {
                task.repositoryId = bean.repositoryId;
            }
            if (bean.startDate != null) {
                task.startDate = bean.startDate;
            }
            if (bean.endDate != null) {
                task.endDate = bean.endDate;
            }
            if (bean.expectWorkTime != null) {
                task.expectWorkTime = bean.expectWorkTime;
            }
            if (bean.workTime != null) {
                task.workTime = bean.workTime;
            }
            if (bean.status != null) {
                task.status = bean.status;
            }
            if (bean.priority != null) {
                task.priority = bean.priority;
            }
            if (bean.ownerAccountIdList != null) {
                task.ownerAccountIdList = bean.ownerAccountIdList;
            }
            if (bean.categoryIdList != null) {
                task.categoryIdList = bean.categoryIdList;
            }
            if (bean.content != null) {
                task.content = bean.content;
            }
            if (bean.releaseId != null) {
                task.releaseId = bean.releaseId;
            }
            if (bean.subSystemId != null) {
                task.subSystemId = bean.subSystemId;
            }
            if (bean.stageId != null) {
                task.stageId = bean.stageId;
            }
            if (bean.finishTime != null) {
                task.finishTime = bean.finishTime;
            }
            if (bean.progress != null) {
                task.progress = bean.progress;
            }
            if (bean.customFields != null) {
                task.customFields = bean.customFields;
            }
            if (bean.workLoad != null) {
                task.workLoad = bean.workLoad;
            }
            Task old = null;
            for (Integer id : ids) {
                if (id == null) {
                    continue;
                }
                task.id = id;
                TaskInfo editTask = bizService.updateTask0(account, task, updateFields, true, true);
                if (editTask != null) {
                    old = editTask;
                }
            }
            //
            if (old != null) {
                int objectType = old.objectType;
                int projectId = old.projectId;
                if (CornerstoneBizSystem.webEventServer != null) {
                    AutoTranscationCallback.registerSynchronization(new TransactionSynchronizationAdapter() {
                        @Override
                        public void afterCommit() {
                            WebEvent event = WebEvent.createWebEvent(account.id, account.companyId,
                                    WebEvent.TYPE_编辑任务, objectType, projectId, 0);
                            CornerstoneBizSystem.webEventServer.boardcastAsync(event);
                        }
                    });
                }
            }
        }


        @Transaction
        @Override
        public List<TaskBriefInfo> batchCopyTask(String token, Set<Integer> ids, int projectId, Integer iterationId,
                                                 Integer objectType, boolean deleteOriginal, Boolean copyComments,
                                                 Boolean copyChangeLogs, Boolean copyAssociatedTasks, Boolean copyParentSubTasks) {
            Account account = bizService.getExistedAccountByToken(token);
            if (iterationId == null) {
                iterationId = 0;
            }
            List<TaskBriefInfo> infos = new ArrayList<>();
            for (Integer id : ids) {
                Task task = copyTask0(account, id, projectId, iterationId, objectType, deleteOriginal,
                        copyComments == null ? false : copyComments,
                        copyChangeLogs == null ? false : copyChangeLogs,
                        copyAssociatedTasks == null ? false : copyAssociatedTasks,
                        copyParentSubTasks == null);
                TaskBriefInfo info = new TaskBriefInfo();
                info.id = task.id;
                info.projectId = task.projectId;
                info.iterationId = task.iterationId;
                info.objectType = task.objectType;
                info.name = task.name;
                info.uuid = task.uuid;
                info.serialNo = task.serialNo;
                infos.add(info);
            }
            return infos;
        }

        /**
         * 递归获取父任务ID
         */
        private void recusiveParentTaskIds(Set<Integer> ids) {
            List<Integer> parentIds = dao.getParentTaskIdList(ids);
            if (!BizUtil.isNullOrEmpty(parentIds)) {
                ids.addAll(parentIds);
                recusiveParentTaskIds(new HashSet<>(parentIds));
            }
        }

        /**
         * 递归获取子任务ID
         */
        private void recusiveSubTaskIds(Set<Integer> ids) {
            List<Integer> parentIds = dao.getSubTaskIdList(ids);
            if (!BizUtil.isNullOrEmpty(parentIds)) {
                ids.addAll(parentIds);
                recusiveSubTaskIds(new HashSet<>(parentIds));
            }
        }

        /**
         * 复制任务并返回任务详情
         *
         * @param account         操作用户
         * @param originalId      被复制的对象ID
         * @param projectId       项目
         * @param iterationId     迭代
         * @param objectType      对象类型
         * @param deleteOriginal  是否删除原对象
         * @param copyComments    是否复制评论
         * @param copyChangeLogs  是否复制变更日志
         * @param copyPubSubTasks 是否复制父子关系
         */
        private Task copyTask0(Account account, int originalId, int projectId, int iterationId,
                               Integer objectType, boolean deleteOriginal,
                               boolean copyComments, boolean copyChangeLogs,
                               boolean copyAssociatedTasks, boolean copyPubSubTasks) {
            TaskDetailInfo bean = dao.getExistedById(TaskDetailInfo.class, originalId);
            if (objectType == null || objectType == 0) {
                objectType = bean.objectType;
            }

            bizService.checkPermissionForProjectAccess(account, bean.projectId);
            bizService.checkProjectPermission(account, projectId, "task_batch_copy_" + bean.objectType);
            //
            TaskDetailInfo copyTask = new TaskDetailInfo();
            if (!copyPubSubTasks) {
                copyTask.parentId = 0;
            }
            copyTask.name = bean.name;
            copyTask.content = bean.content;
            copyTask.projectId = projectId;
            copyTask.objectType = objectType;
            copyTask.iterationId = iterationId;
            boolean needCheckCustomerFiled = false;
            if (bean.projectId != projectId) {//跨项目复制
                copyTask.status = bizService.getStartProjectStatusDefineIdByProjectIdObjectType(projectId, objectType);
                copyTask.priority = bizService.getDefaultProjectPriorityDefineIdByProjectIdObjectType(projectId, objectType);
                if (copyPubSubTasks) {
                    copyTask.parentId = 0;
                }
            } else {//同一项目
                if (copyTask.iterationId == 0) {
                    copyTask.iterationId = bean.iterationId;
                }
                copyTask.releaseId = bean.releaseId;
                copyTask.subSystemId = bean.subSystemId;
                if (bean.objectType != objectType) {//跨对象类型
                    copyTask.status = bizService.getStartProjectStatusDefineIdByProjectIdObjectType(projectId, objectType);
                    copyTask.priority = bizService.getDefaultProjectPriorityDefineIdByProjectIdObjectType(projectId, objectType);
                } else {//同一对象
                    needCheckCustomerFiled = true;
                    copyTask.startDate = bean.startDate;
                    copyTask.endDate = bean.endDate;
                    copyTask.customFields = bean.customFields;
                    copyTask.categoryIdList = bean.categoryIdList;
                    copyTask.parentId = bean.parentId;
                    copyTask.ownerAccountIdList = bean.ownerAccountIdList;
                }
            }
            Task task = bizService.createTask(account, copyTask, needCheckCustomerFiled, false);
            //复制附件
            dao.copyTaskAttachments(originalId, task.id, task.projectId);
            //复制评论
            if (copyComments) {
                dao.copyTaskComments(originalId, task.id);
            }
            //复制变更记录
            if (copyChangeLogs) {
                dao.copyTaskChangeLogs(originalId, task.id);
            }
            //复制关联对象
            if (copyAssociatedTasks) {
                dao.copyTaskAssociateds(originalId, task.id);
                if (bean.objectType == Task.OBJECTTYPE_测试计划) {
                    dao.copyTestPlanTestCases(originalId, task.id, account.id);
                }
            }
            //删除原始对象
            if (deleteOriginal) {
                deleteTask0(account, originalId);
            }
            //
            return task;
        }

        @Transaction
        @Override
        public void deleteTask(String token, int id) {
            final Account account = bizService.getExistedAccountByToken(token);
            final TaskInfo task = deleteTask0(account, id);
            //
            if (CornerstoneBizSystem.webEventServer != null) {
                AutoTranscationCallback.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        WebEvent event = WebEvent.createWebEvent(account.id, account.companyId, WebEvent.TYPE_删除任务,
                                task.objectType, task.projectId, task.id);
                        CornerstoneBizSystem.webEventServer.boardcastAsync(event);
                    }
                });
            }
        }

        private TaskInfo deleteTask0(Account account, int id) {
            TaskInfo old = dao.getExistedByIdForUpdate(TaskInfo.class, id);
            bizService.checkPermission(account, old.companyId);
            bizService.checkProjectPermission(account, old.projectId, Permission.ID_删除TASK + old.objectType);
//			bizService.checkProjectDataPermission(account, old, permissionId);
            //项目清单不能删除
            if (old.associateProjectId > 0) {
                throw new AppException("已关联项目的项目清单不能删除");
            }
            old.isDelete = true;
            old.isCreateIndex = false;
            dao.update(old);
            //删除关联关系
            dao.deleteTaskAssociatedList(old.id);
            //
            if (old.objectType == Task.OBJECTTYPE_测试用例) {
//                TestPlanTestCaseQuery query=new TestPlanTestCaseQuery();
//                query.testCaseId=old.id;
//                int count=dao.getListCount(query);
//                if(count>0) {
//                    throw new AppException("有"+count+"个测试计划关联了此用例，不能删除");
//                }
                dao.deleteTestPlanTestCaseByTestCaseId(old.id);
            }
            if (old.objectType == Task.OBJECTTYPE_测试计划) {
//                TestPlanTestCaseQuery query=new TestPlanTestCaseQuery();
//                query.testPlanId=old.id;
//                int count=dao.getListCount(query);
//                if(count>0) {
//                    throw new AppException("有"+count+"个测试用例关联了此测试计划，不能删除");
//                }
                dao.deleteTestPlanTestCaseByTestPlanId(old.id);
            }
            //
            if (old.parentId > 0) {//更新父任务
                TaskInfo parentTask = dao.getExistedByIdForUpdate(TaskInfo.class, old.parentId);
                parentTask.subTaskCount = dao.calcSubTaskCount(parentTask.id);
                if (old.isFinish) {
                    parentTask.finishSubTaskCount = dao.calcFinishSubTaskCount(parentTask.id);
                }
                dao.update(parentTask);
                //
                TaskSimpleInfo subTask = TaskSimpleInfo.createTaskSimpleinfo(old);
                bizService.addChangeLog(account, 0, parentTask.id,
                        ChangeLog.TYPE_删除子任务, JSONUtil.toJson(subTask));
                bizService.addChangeLog(account, parentTask.projectId, 0,
                        ChangeLog.TYPE_删除子任务, JSONUtil.toJson(TaskSimpleInfo.createTaskSimpleinfo(parentTask)));
                Map<String, Object> map = new HashMap<>();
                map.put("subTask", subTask);
                bizService.sendNotificationForTask(account,
                        parentTask,
                        AccountNotificationSetting.TYPE_对象删除子对象,
                        "对象删除子对象",
                        map);
            }
            bizService.addChangeLog(account, old.projectId, 0, ChangeLog.TYPE_删除任务,
                    JSONUtil.toJson(TaskSimpleInfo.createTaskSimpleinfo(old)));
            //放入回收站
            addCompanyRecycle(account, old.id, CompanyRecycle.TYPE_TASK,
                    old.objectType, "#" + old.serialNo + " " + old.name);
            //
            bizService.sendNotificationAfterUpdateTask(account, old, null, false, true, false);
            //
            return old;
        }

        @Transaction
        @Override
        public void batchDeleteTaskList(String token, Set<Integer> ids) {
            Account account = bizService.getExistedAccountByToken(token);
            TaskInfo task = null;
            for (Integer id : ids) {
                task = deleteTask0(account, id);
            }
            //
            if (task != null) {
                int objectType = task.objectType;
                int projectId = task.projectId;
                if (CornerstoneBizSystem.webEventServer != null) {
                    AutoTranscationCallback.registerSynchronization(new TransactionSynchronizationAdapter() {
                        @Override
                        public void afterCommit() {
                            logger.info("afterCommit");
                            WebEvent event = WebEvent.createWebEvent(account.id, account.companyId, WebEvent.TYPE_删除任务,
                                    objectType, projectId, 0);
                            CornerstoneBizSystem.webEventServer.boardcastAsync(event);
                        }
                    });
                }
            }
        }

        @Override
        public List<Document> searchDocumentList(String token, String key) {
            Account account = bizService.getExistedAccountByToken(token);
            if (account.status == Account.STATUS_无效 || account.companyId <= 0) {//还没有加入企业
                return new ArrayList<>();
            }
            List<Document> list = new ArrayList<>();
            if (key == null) {
                return list;
            }
            if (StringUtil.isEmpty(key.trim())) {
                return list;
            }
            List<Integer> projectIds = dao.getProjectIdList(account.id);
            if (projectIds.isEmpty()) {
                return list;
            }
            try {
                Map<String, Float> boosts = new HashMap<>();//权重
                boosts.put("serialNo", 100f);
                boosts.put("name", 8f);
                boosts.put("createAccountName", 5f);
                boosts.put("content", 2f);
                String indexPath = BizUtil.getIndexPath(account.companyId + "");
                BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
                String[] fieldList = new String[]{
                        "serialNo", "name", "content", "createAccountName",
                        "statusName", "priorityName", "projectName"
                };
                org.apache.lucene.search.Query keywordQuery = luceneService.createQuery(fieldList, key, boosts);
                booleanQueryBuilder.add(keywordQuery, Occur.MUST);
                //
                org.apache.lucene.search.Query projectIdQuery = IntPoint.newSetQuery("projectId", projectIds);
                booleanQueryBuilder.add(projectIdQuery, Occur.MUST);
                //
                org.apache.lucene.search.Query typeQuery = IntPoint.newExactQuery("type", Document.TYPE_TASK);
                booleanQueryBuilder.add(typeQuery, Occur.MUST);
                //
                BooleanQuery finalQuery = booleanQueryBuilder.build();
                List<SearchDocument> result = luceneService.searchDocuments(indexPath, finalQuery, 10);
                if (logger.isDebugEnabled()) {
                    logger.debug("accountId:{} projectIds:{} \nresult:{}", account.id,
                            DumpUtil.dump(projectIds), DumpUtil.dump(result));
                }
                if (result == null || result.isEmpty()) {
                    return new ArrayList<>();
                }
                for (SearchDocument e : result) {
                    list.add(createDocument(e));
                }
            } catch (SearchException e1) {
                logger.error(e1.getMessage(), e1);
            }
            return list;
        }

        private Document createDocument(SearchDocument e) {
            Document d = new Document();
            d.id = Integer.parseInt(e.get("id"));
            d.uuid = e.get("uuid");
            d.serialNo = e.get("serialNo");
            d.projectId = Integer.parseInt(e.get("projectId"));
            d.projectName = e.get("projectName");
            d.projectUuid = e.get("projectUuid");
            d.type = Integer.parseInt(e.get("type"));
            String objectType = e.get("objectType");
            if (objectType != null) {
                d.objectType = Integer.parseInt(objectType);
            }
            d.name = e.get("name");
            d.createAccountName = e.get("createAccountName");
            d.updateTime = e.get("updateTime");
            return d;
        }

        @Override
        public TaskSimpleInfo getTaskSimpleInfoByUuid(String token, String uuid) {
            Account account = bizService.getExistedAccountByToken(token);
            TaskInfo info = dao.getTaskInfoByUuid(uuid);
            if (info == null) {
                throw new AppException("对象不存在");
            }
            bizService.checkTaskViewPermission(account, info);
            return TaskSimpleInfo.createTaskSimpleinfo(info);
        }

        @Override
        public Map<String, Object> getTaskInfoByUuid(String token, String uuid) {
            Account account = bizService.getExistedAccountByToken(token);
            Map<String, Object> result = new HashMap<>();
            TaskDetailInfo info = dao.getTaskDetailInfoByUuid(uuid);
            if (info == null) {
                throw new AppException("对象不存在");
            }
            //项目集类型的任务在被项目负责人查看时不进行查看权限校验
            boolean isProjectSetTaskOwner = info.objectType == Task.OBJECTTYPE_项目清单 &&
                    !BizUtil.isNullOrEmpty(info.ownerAccountIdList) && info.ownerAccountIdList.contains(account.id);
            if (!isProjectSetTaskOwner) {
                bizService.checkTaskViewPermission(account, info);
            }

            //
            AttachmentQuery attachmentQuery = new AttachmentQuery();
            attachmentQuery.taskId = info.id;
            attachmentQuery.pageSize = Integer.MAX_VALUE;
            attachmentQuery.isDelete = false;
            info.attachmentList = dao.getList(attachmentQuery);
            //
            WikiAssociated.WikiAssociatedQuery wikiAssociatedQuery = new WikiAssociated.WikiAssociatedQuery();
            wikiAssociatedQuery.taskId = info.id;
            wikiAssociatedQuery.pageSize = Integer.MAX_VALUE;
            List<WikiAssociated> wikiAssociateds = dao.getList(wikiAssociatedQuery);
            if (!BizUtil.isNullOrEmpty(wikiAssociateds)) {
                Object[] wikiIds = wikiAssociateds.stream().map(k -> k.wikiPageId).toArray();
                info.wikiPageList = dao.getTaskAssociateWikiPageList(wikiIds);
            }
            //
            TaskAssociatedQuery associatedQuery = new TaskAssociatedQuery();
            associatedQuery.taskId = info.id;
            associatedQuery.pageSize = Integer.MAX_VALUE;
            info.associatedList = dao.getList(associatedQuery);
            //
            TaskQuery taskQuery = new TaskQuery();
            taskQuery.parentId = info.id;
            taskQuery.pageSize = Integer.MAX_VALUE;
            taskQuery.isDelete = false;
            info.subTaskList = dao.getList(taskQuery);
            //
            if (info.parentId > 0) {
                taskQuery = new TaskQuery();
                taskQuery.id = info.parentId;
                taskQuery.isDelete = false;
                info.parentTaskList = dao.getList(taskQuery);
            } else {
                info.parentTaskList = new ArrayList<>();
            }
            //
            TaskWorkTimeLogQuery workTimeLogQuery = new TaskWorkTimeLogQuery();
            workTimeLogQuery.taskId = info.id;
            workTimeLogQuery.pageSize = Integer.MAX_VALUE;
            info.workTimeLogList = dao.getList(workTimeLogQuery);
            //
            if (info.objectType == Task.OBJECTTYPE_测试计划) {
                TestPlanTestCaseQuery testCaseQuery = new TestPlanTestCaseQuery();
                testCaseQuery.testPlanId = info.id;
                testCaseQuery.pageSize = Integer.MAX_VALUE;
                info.testCaseList = dao.getList(testCaseQuery);
            }
            //
            TaskStatusChangeLogQuery changeLogQuery = new TaskStatusChangeLogQuery();
            changeLogQuery.taskId = info.id;
            changeLogQuery.pageSize = 1000;
            info.statusChangeLogList = dao.getList(changeLogQuery);
            //
            TaskEditInfo editTaskInfo = getEditTaskInfo0(account, info.projectId, info.objectType);
            //
            List<ProjectStatusDefineInfo> statusList = new ArrayList<>();
            //先创建任务后创建工作流的情况下会没有状态，导致无法编辑赋值
            int statusValue = info.status;
            if (statusValue == 0) {
                if (!BizUtil.isNullOrEmpty(editTaskInfo.statusList)) {
                    Optional<ProjectStatusDefineInfo> optional = editTaskInfo.statusList.stream().filter(k -> k.type == ProjectStatusDefine.TYPE_开始状态).findFirst();
                    if (optional.isPresent()) {
                        statusValue = optional.get().id;
                    }
                }
            }
            for (ProjectStatusDefineInfo statusDefine : editTaskInfo.statusList) {
                if (statusDefine.id == statusValue) {
                    statusList.add(statusDefine);
                    if (statusDefine.transferTo != null) {
                        for (Integer e : statusDefine.transferTo) {
                            for (ProjectStatusDefineInfo e1 : editTaskInfo.statusList) {
                                if (e1.id == e) {
                                    statusList.add(e1);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            editTaskInfo.statusList = statusList;//只保留当前状态和能跳转的状态
            //
            Set<String> permissionList = new HashSet<>();
            if (!bizService.isSuperBoss(account)) {
                if (!isProjectSetTaskOwner) {
                    permissionList = bizService.getMyTaskPermission(account, info);
                }
            }
            result.put("task", info);
            result.put("editTaskInfo", editTaskInfo);
            if (isProjectSetTaskOwner) {
                permissionList.add(Permission.ID_编辑TASK + info.objectType);
            }
            result.put("permissionList", permissionList);
            TaskRemindInfo remindInfo = dao.getTaskRemindByTaskId(info.id);
            boolean haveRemind = false;
            if (remindInfo != null && remindInfo.remindRules != null && remindInfo.remindRules.size() > 0 &&
                    remindInfo.remindAccountIdList != null && remindInfo.remindAccountIdList.size() > 0) {
                haveRemind = true;
            }
            result.put("haveRemind", haveRemind);
            //星标任务
            AccountStar starTask = dao.getAccountStarTask(account, info.id);
            result.put("haveStar", null != starTask);
            //
            return result;
        }

        //
        @Override
        public Map<String, Object> getTaskInfoByUuidForOpenApi(String token, String uuid) {
            Map<String, Object> resp = getTaskInfoByUuid(token, uuid);
            resp.remove("editTaskInfo");
            resp.remove("permissionList");
            resp.remove("haveRemind");
            return resp;
        }

        @Override
        public TaskPdfInfo getTaskPdfInfoByUuid(String token, String uuid) {
            Account account = bizService.getExistedAccountByToken(token);
            TaskPdfInfo info = new TaskPdfInfo();
            Map<String, Object> resp = getTaskInfoByUuid(token, uuid);
            info.task = (TaskDetailInfo) resp.get("task");
            //
            TaskCommentQuery commentQuery = new TaskCommentQuery();
            commentQuery.idSort = Query.SORT_TYPE_DESC;
            commentQuery.taskId = info.task.id;
            setupQuery(account, commentQuery);
            commentQuery.pageSize = 20;
            info.commentList = dao.getList(commentQuery);
            //
            CategoryQuery categoryQuery = new CategoryQuery();
            categoryQuery.projectId = info.task.projectId;
            categoryQuery.objectType = info.task.objectType;
            categoryQuery.pageIndex = 1;
            categoryQuery.pageSize = 1000;
            info.categories = dao.getList(categoryQuery);
            //
            ChangeLogQuery changeLogQuery = new ChangeLogQuery();
            changeLogQuery.idSort = Query.SORT_TYPE_DESC;
            changeLogQuery.taskId = info.task.id;
            changeLogQuery.projectId = 0;
            changeLogQuery.pageSize = 20;
            setupQuery(account, changeLogQuery);
            info.changeLogList = dao.getList(changeLogQuery);
            //
            ProjectFieldDefineQuery fieldDefineQuery = new ProjectFieldDefineQuery();
            fieldDefineQuery.projectId = info.task.projectId;
            fieldDefineQuery.objectType = info.task.objectType;
            fieldDefineQuery.idSort = Query.SORT_TYPE_ASC;
            fieldDefineQuery.pageSize = 10000;
            info.projectFieldDefineList = dao.getList(fieldDefineQuery);
            //
            info.memberList = bizService.getProjectMemberInfoList(info.task.projectId);


            //
            return info;
        }

        //
        @Override
        public Map<String, Object> getTaskInfoList(String token, TaskQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            return bizService.getTaskInfoList0(account, query, null, false);
        }

        //
        @Override
        public Map<String, Object> getTaskInfoListAndAssociateTasks(String token, TaskQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            if (query.orderType != null && query.orderType == TaskQuery.ORDER_TYPE_CREATE_TIME_DESC) {//按时间不准确
                query.orderType = TaskQuery.ORDER_TYPE_ID_DESC;//按照id降序
            }
            //项目集特殊处理：部门管理员可以看到下面部门的项目集清单
            if (query.objectType == Task.OBJECTTYPE_项目清单) {
                try {
                    bizService.checkCompanyPermission(account, Permission.ID_开启项目集管理模块);
                    Set<Integer> projectIdSet = bizService.getAccountAccessProjectList(account, true, false);
                    if (!BizUtil.isNullOrEmpty(projectIdSet)) {
                        query.associateProjectIdInList = BizUtil.convertList(projectIdSet);
                    }
                } catch (Exception e) {
                    logger.info("cant access sub department project" + e.getMessage());
                }
            }
            return bizService.getTaskInfoList0(account, query, null, true);
        }


        @Override
        public List<TaskInfo> getSubTaskInfoList(String token, String taskUuid, TaskQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            TaskInfo task = dao.getExistedByUuid(TaskInfo.class, taskUuid);
            //查看权限校验
            if (!bizService.checkTaskViewPermission(account.id, task.projectId, task.objectType)) {
                if (task.createAccountId != account.id && !task.ownerAccountIdList.contains(account.id)) {
                    logger.error("account.id {} info.projectId:{}", account.id, task.projectId);
                    throw new AppException("权限不足,请查看项目可见性设置");
                }
            }
            if (task.isDelete) {
                throw new AppException(task.objectTypeName + "已经被删除");
            }
            bizService.checkPermission(account, task.companyId);
            List<TaskInfo> list = new ArrayList<>();
            getSubTaskList(list, account, query, task.id);
            return list;
        }

        //
        @SuppressWarnings("unchecked")
        private void getSubTaskList(List<TaskInfo> list, Account account, TaskQuery query, int parentTaskId) {
            if (query == null) {
                query = new TaskQuery();
            }
            query.parentId = parentTaskId;
            query.pageSize = 1000;
            query.serialNoSort = Query.SORT_TYPE_ASC;
//            query.idSort = Query.SORT_TYPE_ASC;
            Map<String, Object> map = bizService.getTaskInfoList0(account, query, null, false);
            List<TaskInfo> subList = (List<TaskInfo>) map.get("list");
            list.addAll(subList);
//            for (TaskInfo e : subList) {
//                getSubTaskList(list, account, query, e.id);
//            }
        }

        //
        @SuppressWarnings("unchecked")
        @Override
        public List<TaskInfo> getTodoTaskInfoList(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            int[] objectTypes = bizService.getStatusBasedObjectTypeListByCompanyId(account.companyId);
            if (BizUtil.isNullOrEmpty(objectTypes)) {
                return new ArrayList<>();
            }
            int[] projectIdInList = bizService.getMyRunnigProjectIdList(account.id, account.companyId);
            if (BizUtil.isNullOrEmpty(projectIdInList)) {
                return new ArrayList<>();
            }
            TaskQuery query = new TaskQuery();
            query.companyId = account.companyId;
            query.projectStatus = Project.STATUS_运行中;
            query.ownerAccountId = account.id;
            query.objectTypeInList = BizUtil.deleteElement(objectTypes, Task.OBJECTTYPE_项目清单);
            query.isFinish = false;
            query.pageSize = 10000;
            query.projectIdInList = projectIdInList;
            setupQuery(account, query);
            Map<String, Object> map = bizService.getTaskInfoList0(account, query, null, false);
            return (List<TaskInfo>) map.get("list");
        }

        /**
         * 报表-查询成员任务列表
         */
        @SuppressWarnings("unchecked")
        @Override
        public List<TaskInfo> getAccountsTaskList(String token, int type, List<Integer> accountIdList) {
            Account optAccount = bizService.getExistedAccountByToken(token);
            int[] objectTypes = bizService.getStatusBasedObjectTypeListByCompanyId(optAccount.companyId);
            if (objectTypes == null || objectTypes.length == 0) {
                return new ArrayList<>();
            }
            if (accountIdList == null || accountIdList.size() == 0) {
                return new ArrayList<>();
            }
            CompanyMemberQuery cmQuery = new CompanyMemberQuery();
            setupQuery(optAccount, cmQuery);
            cmQuery.accountIdInList = BizUtil.convertList(accountIdList);
            cmQuery.pageSize = 10000;
            List<CompanyMember> companyMemberList = dao.getList(cmQuery);
            if (companyMemberList.size() == 0) {
                return new ArrayList<>();
            }
            int[] accountIdInList = new int[companyMemberList.size()];
            for (int i = 0; i < companyMemberList.size(); i++) {
                accountIdInList[i] = companyMemberList.get(i).accountId;
            }
            Set<Integer> idSet = bizService.getAccountAccessProjectList(optAccount, true, false);

//            int[] projectIdInList = bizService.getMyRunnigProjectIdList(optAccount.id, optAccount.companyId);
            if (BizUtil.isNullOrEmpty(idSet)) {
                return new ArrayList<>();
            }
            TaskQuery query = new TaskQuery();
            query.companyId = optAccount.companyId;
            query.projectStatus = Project.STATUS_运行中;
            query.objectTypeInList = BizUtil.deleteElement(objectTypes, Task.OBJECTTYPE_项目清单);

            query.projectIdInList = BizUtil.convertList(idSet);
            query.pageSize = 10000;
            setupQuery(optAccount, query);
            if (type == 1) {//待办
                query.isFinish = false;
                query.ownerAccountIdList = accountIdInList;
            } else if (type == 2) {//我创建的
                query.createAccountIdInList = accountIdInList;
            } else if (type == 3) {
                query.isFinish = null;
                query.ownerAccountIdList = accountIdInList;
                List<TaskInfo> todoList = (List<TaskInfo>) bizService.getTaskInfoList0(optAccount, query, null, false).get("list");
                query.isFinish = null;
                query.ownerAccountIdList = null;
                query.createAccountIdInList = accountIdInList;
                List<TaskInfo> createList = (List<TaskInfo>) bizService.getTaskInfoList0(optAccount, query, null, false).get("list");
                if (BizUtil.isNullOrEmpty(todoList) && BizUtil.isNullOrEmpty(createList)) {
                    return Collections.emptyList();
                }
                if (!BizUtil.isNullOrEmpty(todoList)) {
                    if (!BizUtil.isNullOrEmpty(createList)) {
                        todoList.addAll(createList);
                        todoList = todoList.stream().filter(BizUtil.distinctByKey(k -> k.id)).collect(Collectors.toList());
                    }
                    return todoList;
                } else {
                    return createList;
                }

            }
            Map<String, Object> map = bizService.getTaskInfoList0(optAccount, query, null, false);
            return (List<TaskInfo>) map.get("list");
        }

        /**
         * 我的日历
         */
        @Override
        public TaskCalendarInfo getMyCalendarInfo(String token, CalendarInfoQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            return getMyCalendarInfo0(account, query);
        }

        private TaskCalendarInfo getMyCalendarInfo0(Account account, CalendarInfoQuery query) {
            if (query.monthDate == null) {
                query.monthDate = new Date();
            }
            TaskCalendarInfo info = new TaskCalendarInfo();
            TaskQuery taskQuery = new TaskQuery();
            setupQuery(account, taskQuery);
            taskQuery.pageSize = 10000;
            taskQuery.companyId = account.companyId;
            taskQuery.ownerAccountId = account.id;
            taskQuery.isFinish = false;
            int[] objectTypeInList = bizService.getCompanyEnableObjectTypelist(account.companyId);
            if (!BizUtil.isNullOrEmpty(objectTypeInList)) {
                info.taskList = dao.getMyCalendarTasks(account, query.monthDate, objectTypeInList);
            }
            //
            Date startDate = DateUtil.getBeginOfMonth(query.monthDate);
            Date endDate = DateUtil.getNextMonth(startDate, 2);
            RemindQuery remindQuery = new RemindQuery();
            remindQuery.createAccountId = account.id;
            remindQuery.remindTimeStart = startDate;
            remindQuery.remindTimeEnd = endDate;
            remindQuery.repeat = Remind.REPEAT_不提醒;
            List<RemindInfo> remindList = dao.getList(remindQuery);
            remindQuery = new RemindQuery();
            remindQuery.createAccountId = account.id;
            remindQuery.excludeRepeat = Remind.REPEAT_不提醒;
            List<RemindInfo> otherRemindList = dao.getList(remindQuery);
            if (!otherRemindList.isEmpty()) {
                remindList.addAll(otherRemindList);
            }
            List<RemindInfo> finalRemindList = new ArrayList<>();
            for (RemindInfo remind : remindList) {
                finalRemindList.add(remind);
                if (remind.repeat == RemindInfo.REPEAT_每天) {
                    int days = DateUtil.differentDays(remind.remindTime, endDate);
                    for (int i = 1; i <= days; i++) {
                        RemindInfo newRemind = BeanUtil.copyTo(RemindInfo.class, remind);
                        newRemind.remindTime = DateUtil.getNextDay(remind.remindTime, i);
                        finalRemindList.add(newRemind);
                    }
                }
                if (remind.repeat == RemindInfo.REPEAT_每周) {
                    for (int i = 1; i <= 4; i++) {
                        RemindInfo newRemind = BeanUtil.copyTo(RemindInfo.class, remind);
                        newRemind.remindTime = DateUtil.getNextDay(remind.remindTime, 7 * i);
                        finalRemindList.add(newRemind);
                    }
                }
                if (remind.repeat == RemindInfo.REPEAT_每双周) {
                    for (int i = 1; i <= 4; i++) {
                        RemindInfo newRemind = BeanUtil.copyTo(RemindInfo.class, remind);
                        newRemind.remindTime = DateUtil.getNextDay(remind.remindTime, 14 * i);
                        finalRemindList.add(newRemind);
                    }
                }
                if (remind.repeat == RemindInfo.REPEAT_每月) {
                    RemindInfo newRemind = BeanUtil.copyTo(RemindInfo.class, remind);
                    newRemind.remindTime = DateUtil.getNextMonth(remind.remindTime, 1);
                    finalRemindList.add(newRemind);
                }
            }
            info.remindList = finalRemindList;
            //
            if (logger.isDebugEnabled()) {
                logger.debug("CalendarInfo:{}", DumpUtil.dump(info));
            }
            //
            return info;

        }

        /**
         * 日历订阅
         */
        @Override
        public String getICalendar(String calUuid) {
            Account account = dao.getExistedAccountByCalUuid(calUuid);
            ICalendar ical = new ICalendar();
            ical.setCalendarScale(CalendarScale.gregorian());
            ical.setName("CORNERSTONE");
            CalendarInfoQuery query = new CalendarInfoQuery();
            query.monthDate = new Date();
            TaskCalendarInfo calendarInfo = getMyCalendarInfo0(account, query);
            for (TaskInfo e : calendarInfo.taskList) {
                VEvent event = new VEvent();
                event.setSummary(e.name);
                event.setDateStart(e.startDate);
                event.setDateEnd(e.endDate);
                event.setColor(e.statusColor);
                event.setCreated(e.createTime);
                event.setUid(e.uuid);
                event.setClassification(Classification.PUBLIC);
//				event.setUrl(GlobalConfig.webUrl+"p/");
                ical.addEvent(event);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                Biweekly.write(ical).go(out);
            } catch (IOException e) {
                logger.error("getICalendar ERR", e.getMessage());
            }
            return new String(out.toByteArray());
        }

        @Override
        public TaskStat getTaskStat(String token, TaskQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            return statService.getTaskStat0(account, query);
        }

        //
        @Override
        public TaskWeekInfo getTaskWeekInfo(String token, TaskQuery query, int next, Integer type) {
            Account account = bizService.getExistedAccountByToken(token);
            TaskWeekInfo info = new TaskWeekInfo();
            setupQuery(account, query);
            if (query.projectId == null) {
                throw new AppException("请选择一个项目");
            }
            //先翻页后切换统计视图影响到分页参数
            query.pageIndex = 1;
            query.pageSize = Integer.MAX_VALUE;
            //
            Date startDate = null;
            Date endDate = null;
            if (type == null || type == 0) {//按周
                startDate = DateUtil.getNextDay(DateUtil.getBeginOfWeek(), 7 * next);
                endDate = DateUtil.getNextDay(startDate, 6);
                query.expectEndDateStart = startDate;
                query.expectEndDateEnd = endDate;
                info.thisWeekTasks = getTaskInfos(account, query);
                //
                query.expectEndDateStart = DateUtil.getNextDay(startDate, 7);
                query.expectEndDateEnd = DateUtil.getNextDay(endDate, 7);
                info.nextWeekTasks = getTaskInfos(account, query);
            } else if (type == 1) {//按月
                startDate = DateUtil.getBeginOfMonth(next);
                endDate = DateUtil.getNextDay(DateUtil.getBeginOfMonth(next + 1), -1);
                query.expectEndDateStart = startDate;
                query.expectEndDateEnd = endDate;
                info.thisWeekTasks = getTaskInfos(account, query);
                //
                query.expectEndDateStart = DateUtil.getBeginOfMonth(next + 1);
                query.expectEndDateEnd = DateUtil.getNextDay(DateUtil.getBeginOfMonth(next + 2), -1);
                info.nextWeekTasks = getTaskInfos(account, query);
                //
            }
            //
            query.expectEndDateStart = null;
            query.expectEndDateEnd = DateUtil.getBeginOfDay(DateUtil.getNextDay(-1));
            query.isFinish = false;
            info.dueTasks = getTaskInfos(account, query);
            //
            info.weekStart = startDate;
            info.weekEnd = endDate;
            //
            return info;
        }

        //
        private List<TaskInfo> getTaskInfos(Account account, TaskQuery query) {
            Set<String> includeField = new HashSet<>();
            includeField.add("id");
            includeField.add("name");
            includeField.add("serialNo");
            includeField.add("projectName");
            includeField.add("status");
            includeField.add("statusName");
            includeField.add("isFinish");
            includeField.add("startDate");
            includeField.add("endDate");
            includeField.add("expectEndDate");
            includeField.add("ownerAccountList");
            includeField.add("createTime");
            Map<String, Object> map = bizService.getTaskInfoList0(account, query, includeField, false);
            @SuppressWarnings("unchecked")
            List<TaskInfo> list = (List<TaskInfo>) map.get("list");
            return list;
        }

        //
        @Override
        public List<TaskInfo> getMyCreateTaskInfoList(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            int[] objectTypeList = bizService.getStatusBasedObjectTypeListByCompanyId(account.companyId);
            if (BizUtil.isNullOrEmpty(objectTypeList)) {
                return new ArrayList<>();
            }
            TaskQuery query = new TaskQuery();
            query.companyId = account.companyId;
            query.projectStatus = Project.STATUS_运行中;
            query.projectIsDelete = false;
            query.objectTypeInList = BizUtil.deleteElement(objectTypeList, Task.OBJECTTYPE_项目清单);
            query.createAccountId = account.id;
            query.isFinish = false;
            query.pageSize = 10000;
            setupQuery(account, query);
            return dao.getList(query);
        }

        //
        @Transaction
        @Override
        public int createFilter(String token, Filter bean) {
            Account account = bizService.getExistedAccountByToken(token);
            Project project = dao.getExistedById(Project.class, bean.projectId);
            bizService.checkPermission(account, project.companyId);
            bean.createAccountId = account.id;
            bean.ownerAccountId = account.id;
            bean.companyId = project.companyId;
            BizUtil.checkUniqueKeysOnAdd(dao, bean);
            BizUtil.checkValid(bean);
            checkFilterConditionValid(account, bean.condition);
            dao.add(bean);
            return bean.id;
        }

        private void checkFilterConditionValid(Account account, FilterCondition condition) {
            if (condition.type == FilterCondition.TYPE_查询条件) {
                ProjectFieldDefine define = dao.getById(ProjectFieldDefine.class, condition.key);
                if (define == null) {
                    throw new AppException("过滤条件不能为空");
                }
                if (condition.operator == 0) {
                    throw new AppException("过滤条件不能为空");
                }
                if (condition.operator != FilterCondition.OPERATOR_IS_NULL &&
                        condition.operator != FilterCondition.OPERATOR_IS_NOT_NULL) {
                    bizService.checkPermissionForProjectAccess(account, define.projectId);
                    if (define.isSystemField) {
                        String field = define.field;
                        switch (field) {
                            case "name":
                                if (StringUtil.isEmpty(condition.stringValue)) {
                                    throw new AppException("过滤条件不能为空");
                                }
                                break;
                            case "statusName":
                            case "priorityName":
                            case "categoryIdList":
                            case "iterationName":
                            case "releaseName":
                            case "subSystemName":
                            case "createAccountName":
                            case "ownerAccountName":
                                if (condition.intValueList == null || condition.intValueList.isEmpty()) {
                                    throw new AppException("过滤条件不能为空");
                                }
                                break;
                            case "startDate":
                            case "endDate":
                                if (condition.dateValue == null) {
                                    throw new AppException("过滤条件不能为空");
                                }
                                break;
                            case "expectWorkTime":
                            case "workTime":
                            case "startDays":
                            case "endDays":
                                if (condition.intValue == null) {
                                    throw new AppException("过滤条件不能为空");
                                }
                                break;
                            default:
                                break;
                        }
                    } else {
                        if (define.type == ProjectFieldDefine.TYPE_单行文本框) {
                            if (StringUtil.isEmpty(condition.stringValue)) {
                                throw new AppException("过滤条件不能为空");
                            }
                        } else if (define.type == ProjectFieldDefine.TYPE_日期) {
                            if (condition.dateValue == null) {
                                throw new AppException("过滤条件不能为空");
                            }
                            if (!define.showTimeField) {
                                condition.dateValue = DateUtil.getBeginOfDay(condition.dateValue);
                            }
                        } else if (define.type == ProjectFieldDefine.TYPE_复选框) {
                            if (condition.stringValueList == null || condition.stringValueList.isEmpty()) {
                                throw new AppException("过滤条件不能为空");
                            }
                        } else if (define.type == ProjectFieldDefine.TYPE_单选框) {
                            if (condition.stringValueList == null || condition.stringValueList.isEmpty()) {
                                throw new AppException("过滤条件不能为空");
                            }
                        } else if (define.type == ProjectFieldDefine.TYPE_团队成员选择) {
                            if (condition.intValueList == null || condition.intValueList.isEmpty()) {
                                throw new AppException("过滤条件不能为空");
                            }
                        }
                    }
                }
            }
            if (condition.children != null) {
                for (FilterCondition child : condition.children) {
                    checkFilterConditionValid(account, child);
                }
            }
        }

        @Transaction
        @Override
        public void updateFilter(String token, Filter bean) {
            Account account = bizService.getExistedAccountByToken(token);
            FilterInfo old = dao.getExistedByIdForUpdate(FilterInfo.class, bean.id);
            bizService.checkPermission(account, old.companyId);
            BizUtil.checkUniqueKeysOnUpdate(dao, bean, old);
            old.name = bean.name;
            old.condition = bean.condition;
            old.updateAccountId = account.id;
            checkFilterConditionValid(account, old.condition);
            BizUtil.checkValid(old);
            dao.update(old);
        }

        @Transaction
        @Override
        public void deleteFilter(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            FilterInfo old = dao.getExistedByIdForUpdate(FilterInfo.class, id);
            bizService.checkPermission(account, old.companyId);
            dao.deleteById(FilterInfo.class, old.id);
        }

        @Override
        public FilterInfo getFilterInfoById(String token, int id) {
            return dao.getExistedById(FilterInfo.class, id);
        }

        @Override
        public List<FilterInfo> getFilterInfoList(String token, FilterQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            setupQuery(account, query);
            query.orCreateAccountId = account.id;
            query.orOwnerAccountId = 0;
            return dao.getList(query);
        }

        @Transaction
        @Override
        public void addTaskAssociatedList(String token, int taskId, int type, List<Integer> associatedTaskIds) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.addTaskAssociatedList0(account, taskId, type, associatedTaskIds);
        }

        @Transaction
        @Override
        public void batchAddTaskAssociatedList(String token, Set<Integer> fromIds, Set<Integer> toIds) {
            Account account = bizService.getExistedAccountByToken(token);
            for (Integer toId : toIds) {
                TaskInfo task = dao.getExistedById(TaskInfo.class, toId);
                for (Integer fromId : fromIds) {
                    TaskInfo associatedTask = dao.getExistedById(TaskInfo.class, fromId);
                    bizService.addTaskAssociated(account, task, 0, associatedTask);
                }
            }
        }

        @Transaction
        @Override
        public void batchAddToParentTask(String token, Set<Integer> fromIds, Set<Integer> toIds) {
            Account account = bizService.getExistedAccountByToken(token);
            for (Integer toId : toIds) {
                TaskInfo parentTask = dao.getExistedById(TaskInfo.class, toId);
                for (Integer fromId : fromIds) {
                    TaskInfo subTask = dao.getExistedById(TaskInfo.class, fromId);
                    bizService.addTaskParentRelation(account, parentTask, subTask);
                }
            }
        }

        @Transaction
        @Override
        public void dismissPubSubTask(String token, int subTaskId) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkPermissionForTask(account, subTaskId);
            bizService.dismisPubSubTask(account, subTaskId);
        }


        @Transaction
        @Override
        public void deleteTaskAssociated(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            TaskAssociated old = dao.getExistedById(TaskAssociated.class, id);
            bizService.checkPermissionForTask(account, old.taskId);
            dao.deleteTaskAssociated(old.taskId, old.associatedTaskId);
            dao.deleteTaskAssociated(old.associatedTaskId, old.taskId);
            //
            TaskInfo task = dao.getExistedById(TaskInfo.class, old.taskId);
            TaskInfo associatedTask = dao.getExistedById(TaskInfo.class, old.associatedTaskId);

            //associateCount计数
            task.associateCount = Math.max(task.associateCount - 1, 0);
            associatedTask.associateCount = Math.max(associatedTask.associateCount - 1, 0);
            dao.updateSpecialFields(task, "associateCount");
            dao.updateSpecialFields(associatedTask, "associateCount");

            TaskSimpleInfo associatedSimpleTask = TaskSimpleInfo.createTaskSimpleinfo(associatedTask);
            bizService.addChangeLog(account, 0, task.id,
                    ChangeLog.TYPE_取消关联对象,
                    JSONUtil.toJson(associatedSimpleTask));
            bizService.addChangeLog(account, associatedTask.projectId, 0,
                    ChangeLog.TYPE_取消关联对象,
                    JSONUtil.toJson(TaskSimpleInfo.createTaskSimpleinfo(task)));
            Map<String, Object> map = new HashMap<>();
            map.put("associatedTask", associatedSimpleTask);
            bizService.sendNotificationForTask(account,
                    task,
                    AccountNotificationSetting.TYPE_对象取消关联其它对象,
                    "对象取消关联其它对象",
                    map);

            Map<String, Object> map0 = new HashMap<>();
            map0.put("associatedTask", task);
            bizService.addChangeLog(account, 0, associatedTask.id,
                    ChangeLog.TYPE_取消关联对象,
                    JSONUtil.toJson(TaskSimpleInfo.createTaskSimpleinfo(associatedTask)));

            bizService.sendNotificationForTask(account,
                    associatedTask,
                    AccountNotificationSetting.TYPE_其它对象取消对象关联,
                    "其它对象已取消对象关联",
                    map0);
        }

        @Transaction
        @Override
        public int addTaskComment(String token, TaskComment bean) {
            Account account = bizService.getExistedAccountByToken(token);
            TaskInfo task = dao.getExistedById(TaskInfo.class, bean.taskId);
            bizService.checkPermission(account, task.companyId);
            bean.createAccountId = account.id;
            bean.companyId = task.companyId;
            BizUtil.checkValid(bean);
            bean.comment = bizService.getContent(bean.comment);
            dao.add(bean);
            //
            TaskCommentInfo commentInfo = dao.getExistedById(TaskCommentInfo.class, bean.id);
            addAccountNotificationWhenComment(account, task, commentInfo);
            return bean.id;
        }

        /**
         * 评论后通知
         */
        private void addAccountNotificationWhenComment(Account optAccount, TaskInfo task, TaskCommentInfo bean) {
            Set<Integer> accountIds = bizService.getTaskNotificationAccountIds(task, optAccount.id);
            if (accountIds.isEmpty()) {
                return;
            }
            TaskSimpleInfo taskBean = TaskSimpleInfo.createTaskSimpleinfo(task);
            Map<String, Object> map = new HashMap<>();
            map.put("task", taskBean);
            map.put("commentInfo", bean);
            //
            List<String> userNames = PatternUtil.matchs(bean.comment, "@([^@ ]{0,20})\\)");
            Set<Integer> accountSet = new HashSet<>();
            if (!userNames.isEmpty()) {
                for (String userName : userNames) {
                    userName = userName.substring(userName.indexOf("(") + 1).trim();
                    Account account = dao.getAccountByUserName(userName);
                    if (account.id == optAccount.id) {
                        continue;
                    }
                    accountSet.add(account.id);
                    bizService.addAccountNotification(account.id,
                            AccountNotificationSetting.TYPE_在评论中被提醒,
                            task.companyId, task.projectId, bean.id, "新提醒",
                            JSONUtil.toJson(map), new Date(), optAccount);

                }
            }
            //
            for (Integer accountId : accountIds) {
                if (accountSet.contains(accountId)) {
                    continue;
                }
                bizService.addAccountNotification(accountId,
                        AccountNotificationSetting.TYPE_对象评论,
                        task.companyId, task.projectId,
                        bean.id, "新评论",
                        JSONUtil.toJson(map), new Date(), optAccount);
            }
            //

        }

        @Transaction
        @Override
        public void deleteTaskComment(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            TaskComment old = dao.getExistedByIdForUpdate(TaskComment.class, id);
            bizService.checkPermissionForTask(account, old.taskId);
            old.isDelete = true;
            dao.update(old);
        }

        @Transaction
        @Override
        public List<TaskCommentInfo> getTaskCommentInfoList(String token, TaskCommentQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            setupQuery(account, query);
            if (query.taskId == null) {
                throw new AppException("参数错误");
            }
            bizService.checkPermissionForTask(account, query.taskId);
            query.isDelete = false;
            return dao.getList(query);
        }

        @Override
        public Map<String, Object> getAllChangeLogList(String token, ChangeLogQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            if (query.createTimeEnd != null) {
                query.createTimeEnd = DateUtil.getNextDay(query.createTimeEnd, 1);
            }
            int[] projectIdInList = bizService.getMyRunnigProjectIdList(account.id, account.companyId);
            if (BizUtil.isNullOrEmpty(projectIdInList)) {
                return createResult(new ArrayList<>(), 0);
            }
            query.projectIdInList = projectIdInList;
            setupQuery(account, query);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        @Override
        public Map<String, Object> getAllProjectChangeLogList(String token, ChangeLogQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            setupQuery(account, query);
            if (query.projectId != null) {
                bizService.checkPermissionForProjectAccess(account, query.projectId);
            }
            List<ChangeLogInfo> list = dao.getList(query);
            int count = dao.getListCount(query);
            return createResult(list, count);
        }

        @Override
        public List<ChangeLogInfo> getChangeLogList(String token, ChangeLogQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            return statService.getChangeLogList(account, query);
        }

        @Override
        public ChangeLogDiff getChangeLogDiffById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            ChangeLogDiff diff = dao.getExistedById(ChangeLogDiff.class, id);
            bizService.checkPermissionForTask(account, diff.taskId);
            return diff;
        }

        /**
         * 通过ID查询机器
         */
        @Override
        public MachineInfo getMachineById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            MachineInfo bean = dao.getExistedById(MachineInfo.class, id);
            bizService.checkPermission(account, bean.companyId);
            return bean;
        }

        /**
         * 查询机器列表和总数
         */
        @Override
        public List<MachineInfo> getMachineInfoList(String token, MachineQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            setupQuery(account, query);
            //		    for (MachineInfo e : list) {
//			    	if(e.createAccountId==account.id||//自己可见
//			    			bizService.checkAccountProjectPermission(account.id, query.projectId, e.enableRole, e.roles, null)) {
//			    		ret.add(e);
//			    	}
//			}
            return dao.getAll(query, "password");
        }

        private String getMachineCloneName(int projectId, String name) {
            for (int i = 1; i <= 1000; i++) {
                String newName = "(复制" + i + ")" + name;
                Machine old = dao.getMachineByProjectIdName(projectId, newName);
                if (old == null) {
                    return newName;
                }
            }
            throw new AppException("复制失败,名称重复");
        }

        private String getMachineRestoreName(int projectId, String name) {
            Machine old = dao.getMachineByProjectIdName(projectId, name);
            if (old == null) {
                return name;
            }
            for (int i = 1; i <= 1000; i++) {
                String newName = "(恢复" + i + ")" + name;
                old = dao.getMachineByProjectIdName(projectId, newName);
                if (old == null) {
                    return newName;
                }
            }
            throw new AppException("复制失败,名称重复");
        }

        private void checkMachineValid(MachineInfo bean) {
            if (bean.loginType != MachineInfo.LOGINTYPE_证书登录) {
                if (StringUtil.isEmpty(bean.password)) {
                    throw new AppException("密码不能为空");
                }
            } else {
                if (StringUtil.isEmpty(bean.privateKey)) {
                    throw new AppException("私钥不能为空");
                }
                if (StringUtil.isEmpty(bean.publicKey)) {
                    throw new AppException("公钥不能为空");
                }
            }
        }

        /**
         * 新增机器
         */
        @Transaction
        @Override
        public int createMachine(String token, MachineInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            return createMachine0(account, bean);
        }

        public int createMachine0(Account account, MachineInfo bean) {
            bizService.checkPermissionForProjectAccess(account, bean.projectId);
            if (bean.id == -1) {//复制出来的
                bean.name = getMachineCloneName(bean.projectId, bean.name);
            }
            bean.roles = BizUtil.distinct(bean.roles);
            checkMachineValid(bean);
            bizService.checkProjectPermission(account, bean.projectId, Permission.ID_创建和编辑主机);
            bean.createAccountId = account.id;
            bean.uuid = BizUtil.randomUUID();
            bean.updateAccountId = 0;
            Machine old = dao.getMachineByProjectIdName(bean.projectId, bean.name);
            if (old != null) {
                throw new AppException("主机名称已存在");
            }
            BizUtil.checkValid(bean);
            if (bean.id == 0) {
                if (bean.password != null) {
                    bean.password = TripleDESUtil.encrypt(bean.password, ConstDefine.GLOBAL_KEY);
                }
            }
            dao.add(bean);
            //
            bizService.addChangeLog(account, bean.projectId, 0, ChangeLog.TYPE_新增主机,
                    JSONUtil.toJson(bean));
            //
            return bean.id;
        }

        /**
         * 通过cmdb machine新增主机
         */
        @Transaction
        @Override
        public void createMachineListFromCmdbMachine(String token, int cmdbMachineId, List<Integer> projectIdList) {
            Account account = bizService.getExistedAccountByToken(token);
            CmdbMachineInfo old = dao.getExistedByIdForUpdate(CmdbMachineInfo.class, cmdbMachineId);
            bizService.checkPermission(account, old.companyId);
            MachineInfo machineInfo = new MachineInfo();
            machineInfo.companyId = old.companyId;
            machineInfo.createAccountId = account.id;
            machineInfo.host = old.outerHost;
            machineInfo.loginType = old.loginType;
            machineInfo.name = old.name;
            if (old.password != null) {
                machineInfo.password = TripleDESUtil.decrypt(old.password, ConstDefine.GLOBAL_KEY);
            }
            machineInfo.port = old.port;
            machineInfo.privateKey = old.privateKey;
            machineInfo.publicKey = old.publicKey;
            machineInfo.readonlyMode = false;
            machineInfo.userName = old.userName;
            machineInfo.cmdbMachineId = old.id;
            for (Integer projectId : projectIdList) {
                machineInfo.projectId = projectId;
                createMachine0(account, machineInfo);
            }
        }

        @Transaction
        @Override
        public void updateMachine(String token, MachineInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            updateMachine0(account, bean);
        }

        private void updateMachine0(Account account, MachineInfo bean) {
            MachineInfo old = dao.getExistedByIdForUpdate(MachineInfo.class, bean.id);
            bizService.checkPermission(account, old.companyId);
            if (!bean.name.equals(old.name)) {//修改了名称
                Machine m = dao.getMachineByProjectIdName(old.projectId, bean.name);
                if (m != null) {
                    throw new AppException("主机名称已存在");
                }
            }
            checkMachineValid(bean);
            bizService.checkProjectPermission(account, old.projectId, Permission.ID_创建和编辑主机);
            //
            old.host = bean.host;
            old.port = bean.port;
            old.name = bean.name;
            old.userName = bean.userName;
            if (bean.loginType != Machine.LOGINTYPE_证书登录) {
                if (old.password == null || (!bean.password.equals(old.password))) {//如果修改了密码
                    old.password = TripleDESUtil.encrypt(bean.password, ConstDefine.GLOBAL_KEY);
                }
            } else {
                old.privateKey = bean.privateKey;
                old.publicKey = bean.publicKey;
            }
            old.loginType = bean.loginType;
            old.roles = BizUtil.distinct(bean.roles);
            old.enableRole = bean.enableRole;
            old.remark = bean.remark;
            old.readonlyMode = bean.readonlyMode;
            old.cmd = bean.cmd;
            old.group = bean.group;
            old.updateAccountId = account.id;
            BizUtil.checkValid(old);
            dao.update(old);
        }

        @Transaction
        @Override
        public void deleteMachine(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            MachineInfo old = dao.getExistedByIdForUpdate(MachineInfo.class, id);
            if (old.isDelete) {
                return;
            }
            bizService.checkPermission(account, old.companyId);
            bizService.checkProjectPermission(account, old.projectId, Permission.ID_创建和编辑主机);
            old.isDelete = true;
            old.updateAccountId = account.id;
            bizService.addChangeLog(account, old.projectId, 0, ChangeLog.TYPE_删除主机, JSONUtil.toJson(old));
            //
            old.originalName = old.name;
            old.name = BizUtil.randomUUID();
            dao.update(old);
        }

        /**
         * 上传文件到机器
         */
        @Transaction
        @Override
        public void uploadFileToMachine(String token, String machineToken, String uuid, String remoteFilePath) {
            Account account = bizService.getExistedAccountByToken(token);
            MachineLoginToken loginToken = dao.getMachineLoginTokenByToken(machineToken);
            if (loginToken == null) {
                throw new AppException("TOKEN已过期");
            }
            Attachment attachment = dao.getAttachmentByUuid(uuid);
            if (attachment == null) {
                throw new AppException("文件不存在" + uuid);
            }
            bizService.checkPermission(account, attachment.companyId);
            //
            MachineLoginSession session = dao.getById(MachineLoginSession.class, loginToken.sessionId);
            if (session == null) {
                throw new AppException("TOKEN已过期");
            }
            java.io.File file = BizUtil.getFile(uuid);
            ConnectionInfo info = null;
            if (session.machineId > 0) {
                Machine machine = dao.getExistedById(Machine.class, session.machineId);
                bizService.checkPermissionForProjectAccess(account, machine.projectId);
                info = BizUtil.createConnectionInfo(machine);
            }
            if (session.cmdbMachineId > 0) {
                CmdbMachine machine = dao.getExistedById(CmdbMachine.class, session.cmdbMachineId);
                bizService.checkPermission(account, machine.companyId);
                info = BizUtil.createConnectionInfo(machine);
            }
            if (info == null) {
                throw new AppException("主机不能为空");
            }
            if (StringUtil.isEmpty(remoteFilePath)) {
                remoteFilePath = "~";
            }
            java.io.File remoteFile = new java.io.File(remoteFilePath.trim(), attachment.name);
            SshUtil.scpTo(info.host, info.port, info.user, info.password,
                    info.privateKey, file.getAbsolutePath(),
                    remoteFile.getAbsolutePath(), null);
            file.delete();
        }

        @Transaction
        @Override
        public LoginMachineInfo loginMachine(String token, int machineId) {
            Account account = bizService.getExistedAccountByToken(token);
            Machine machine = dao.getExistedById(Machine.class, machineId);
            bizService.checkPermissionForProjectAccess(account, machine.projectId);
            if (!bizService.checkAccountProjectPermission(account.id, machine.projectId,
                    machine.enableRole, machine.createAccountId, machine.roles, null)) {
                throw new AppException("权限不足");
            }
            MachineSimpleInfo simpleInfo = MachineSimpleInfo.create(machine);
            bizService.addChangeLog(account, machine.projectId, 0, ChangeLog.TYPE_登录主机,
                    JSONUtil.toJson(simpleInfo));
            return loginMachine(account, machine, null, 0);
        }

        @Transaction
        @Override
        public LoginMachineInfo loginCmdbMachine(String token, int cmdbMachineId, int cmdbRobotId) {
            Account account = bizService.getExistedAccountByToken(token);
            CmdbMachine machine = dao.getExistedById(CmdbMachine.class, cmdbMachineId);
            bizService.checkPermission(account, machine.companyId);
            //
            if (cmdbRobotId > 0) {
                CmdbRobot robot = dao.getExistedById(CmdbRobot.class, cmdbRobotId);
                bizService.checkPermission(account, robot.companyId);
            }
            return loginMachine(account, null, machine, cmdbRobotId);
        }

        private LoginMachineInfo loginMachine(Account account, Machine machine, CmdbMachine cmdbMachine, int cmdbRobotId) {
            int machineId = 0;
            int cmdbMachineId = 0;
            int loginType;
            String machineName;
            boolean readonlyMode = false;
            if (machine != null) {
                machineId = machine.id;
                loginType = machine.loginType;
                machineName = machine.name;
                readonlyMode = machine.readonlyMode;
            } else if (cmdbMachine != null) {
                cmdbMachineId = cmdbMachine.id;
                loginType = cmdbMachine.loginType;
                machineName = cmdbMachine.name;
            } else {
                throw new AppException("请选择主机");
            }
            LoginMachineInfo info = new LoginMachineInfo();
            //
            MachineLoginSession session = new MachineLoginSession();
            session.machineId = machineId;
            session.cmdbRobotId = cmdbRobotId;
            session.cmdbMachineId = cmdbMachineId;
            session.loginType = loginType;
            session.createAccountId = account.id;
            dao.add(session);
            //我自己的token
            MachineLoginToken loginToken = new MachineLoginToken();
            loginToken.accountId = account.id;
            loginToken.token = BizUtil.randomUUID();
            loginToken.type = readonlyMode ? MachineLoginToken.TYPE_只读 : MachineLoginToken.TYPE_交互;
            loginToken.sessionId = session.id;
            loginToken.enableShare = true;
            dao.add(loginToken);
            //
            info.machineName = machineName;
            if (loginType == Machine.LOGINTYPE_普通登录 || loginType == Machine.LOGINTYPE_证书登录) {
                info.websocketHost = GlobalConfig.getValue("websocket.host");
                info.websocketPort = Integer.parseInt(GlobalConfig.getValue("websocket.port", "0"));
            }
            if (loginType == Machine.LOGINTYPE_VNC登录) {
                info.websocketHost = GlobalConfig.getValue("websockify.host");
                info.websocketPort = Integer.parseInt(GlobalConfig.getValue("websockify.port", "0"));
            }
            info.token = loginToken.token;
            info.accountList = dao.getMachineLoginAccountList(session.id);
            return info;
        }

        @Transaction
        @Override
        public LoginMachineInfo shareLoginMachine(String token, String machineLoginToken, int type) {
            Account account = bizService.getExistedAccountByToken(token);
            MachineLoginToken mlt = dao.getMachineLoginTokenByToken(machineLoginToken);
            if (mlt == null) {
                throw new AppException("TOKEN不存在");
            }
            MachineLoginSessionInfo session = dao.getExistedById(MachineLoginSessionInfo.class, mlt.sessionId);
            if (session.createAccountId != account.id) {
                throw new AppException("权限不足");
            }
            MachineLoginToken loginToken = new MachineLoginToken();
            loginToken.token = BizUtil.randomUUID();
            loginToken.type = MachineLoginToken.TYPE_只读;
            loginToken.sessionId = session.id;
            loginToken.enableShare = false;
            dao.add(loginToken);
            //
            LoginMachineInfo info = new LoginMachineInfo();
            info.machineName = session.machineName;
            info.token = loginToken.token;
            info.createAccountName = account.name;
            info.createTime = new Date();
            //
            return info;
        }

        /**
         * 收到分享的用户，查询token对应的登录信息，用于登录
         */
        @Override
        public LoginMachineInfo getLoginMachineInfoByToken(String token, String machineLoginToken) {
            Account account = bizService.getExistedAccountByToken(token);
            MachineLoginToken mlt = dao.getMachineLoginTokenByTokenForUpdate(machineLoginToken);
            if (mlt.accountId > 0) {
                if (mlt.accountId != account.id) {
                    throw new AppException("链接已失效");
                }
            } else {
                mlt.accountId = account.id;
                dao.update(mlt);
            }
            MachineLoginSessionInfo session = dao.getExistedById(MachineLoginSessionInfo.class, mlt.sessionId);
            //
            LoginMachineInfo info = new LoginMachineInfo();
            info.machineName = session.machineName;
            info.websocketHost = GlobalConfig.getValue("websocket.host");
            info.websocketPort = Integer.parseInt(GlobalConfig.getValue("websocket.port", "0"));
            info.token = machineLoginToken;
            info.createAccountName = session.createAccountName;
            info.createTime = mlt.createTime;
            info.accountList = dao.getMachineLoginAccountList(mlt.sessionId);
            return info;
        }

        @Override
        public List<AccountSimpleInfo> getLoginMachineAccountListByToken(String token, String machineLoginToken) {
            Account account = bizService.getExistedAccountByToken(token);
            MachineLoginToken mlt = dao.getMachineLoginTokenByToken(machineLoginToken);
            if (mlt == null) {
                throw new AppException("权限不足");
            }
            if (mlt.accountId != account.id) {
                throw new AppException("权限不足");
            }
            return dao.getMachineLoginAccountList(mlt.sessionId);
        }

        @Override
        public AccountInfo getAccountInfo(String token) {
            AccountInfo info = bizService.getExistedAccountInfoByToken(token);
            info.roles = bizService.getMyCompanyRoleList(info);
            return info;
        }

        /**
         * 查询账号在某企业下的信息
         */
        @Override
        public AccountCompanyInfo getAccountCompanyInfo(String token, int accountId) {
            Account account = bizService.getExistedAccountByToken(token);
            CompanyMemberInfo member = dao.getExistedCompanyMemberInfo(accountId, account.companyId);
            AccountCompanyInfo bean = new AccountCompanyInfo();
            bean.id = member.accountId;

            bean.status = member.accountStatus;
            bean.needUpdatePassword = member.needUpdatePassword;
            bean.name = member.accountName;
            bean.userName = member.accountUserName;
            bean.mobileNo = member.accountMobileNo;
            bean.email = member.accountEmail;
            bean.imageId = member.accountImageId;
            bean.dailyLoginFailCount = member.dailyLoginFailCount;
            bean.projectList = bizService.getMyProjectInfoList(accountId, account.companyId);
            bean.roleList = bizService.getMyRoleIdList(accountId, account.companyId);
            //
            bean.departmentList = member.departmentList;
            Account acc = dao.getExistedById(Account.class, accountId);
            bean.superBoss = acc.superBoss;
            bean.isActivated = acc.isActivated;
            bean.adName = acc.adName;
            return bean;
        }

        @Transaction
        @Override
        public void deleteCompanyMemberProject(String token, int accountId) {
            Account optAccount = bizService.getExistedAccountByToken(token);
            Account account = dao.getExistedById(Account.class, accountId);
            if (account.id == optAccount.id) {
                throw new AppException("不能删除自己");
            }
            bizService.checkCompanyPermission(optAccount, Permission.ID_管理成员);
            //
            List<Integer> idList = dao.getProjectMemberIdList(optAccount.companyId, accountId);
            for (Integer id : idList) {
                deleteProjectMember0(optAccount, id);
            }
            bizService.addOptLog(optAccount, account.id, account.name, OptLog.EVENT_ID_从所有项目中移除, "name:" + account.name);
        }

        @Transaction
        @Override
        public void deleteCompanyMember(String token, int accountId) {
            Account optAccount = bizService.getExistedAccountByToken(token);
            Account a = dao.getExistedByIdForUpdate(Account.class, accountId);
            if (optAccount.id == a.id) {
                throw new AppException("不能将自己移出企业");
            }
            bizService.checkIsCompanyMember(a.id, optAccount.companyId);
            //1.先删除公司的成员
            dao.deleteCompanyMemberByAccountIdCompanyId(accountId, optAccount.companyId);
            //2.更新Account里的companyId
            if (a.companyId == optAccount.companyId) {
                a.companyId = dao.getCompanyIdByAccountId(accountId);//找出最近一家企业
                dao.update(a);
            }
            //3.删除对应公司组织架构里数据
            dao.deleteDepartmentByAccountIdCompanyId(accountId, optAccount.companyId);
            //4.再删除项目里的成员
            dao.deleteProjectMembersByAccountIdCompanyId(accountId, optAccount.companyId);
            dao.deleteProjectMemberRolesByAccountIdCompanyId(accountId, optAccount.companyId);
            //5.公司人数-1
            Company company = dao.getExistedByIdForUpdate(Company.class, optAccount.companyId);
            company.memberNum = dao.getCurrMemberNum(company.id);
            dao.update(company);
            //如果是私有化部署版本 则把账号用户名手机号邮箱清空20200223
            if (bizService.isPrivateDeploy(company)) {
                String uuid = BizUtil.randomUUID();
                a.userName = uuid;
                a.mobileNo = uuid;
                a.email = uuid;
                a.wxOpenId = uuid;
                a.wxUnionId = uuid;
                a.larkOpenId = uuid;
                dao.update(a);
            }
            //
            bizService.addOptLog(optAccount, a.id, a.name, OptLog.EVENT_ID_从企业中移除, "name:" + a.name);
        }

        @Transaction
        @Override
        public void updateCompanyAccount(String token, int accountId, AccountCompanyInfo info) {
            Account optAccount = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(optAccount, Permission.ID_管理成员);
            info.userName = info.userName.trim();
            info.mobileNo = info.mobileNo.trim();
            if (info.email != null) {
                info.email = info.email.trim();
            }
            info.name = info.name.trim();
            if (StringUtil.isEmpty(info.name)) {
                throw new AppException("名称不能为空");
            }
            Account old = dao.getExistedByIdForUpdate(Account.class, accountId);
            if (optAccount.companyId != old.companyId) {//20200210新增
                throw new AppException("不在同一个企业");
            }
            if (!old.userName.equals(info.userName)) {
                Account dupAccount = dao.getAccountByUserName(info.userName);
                if (dupAccount != null) {
                    throw new AppException("用户名已经被使用");
                }
            }
            if (!BizUtil.isNullOrEmpty(info.mobileNo)) {
                Account dupAccount = dao.getAccountByMobileNoForUpdate(info.mobileNo);
                if (dupAccount != null && dupAccount.id != accountId) {
                    throw new AppException("手机号已经被注册");
                }
            }else{
                if(!bizService.isPrivateDeploy(optAccount)){
                    throw new AppException("手机号不能为空");
                }
            }
            if (!StringUtil.isEmptyWithTrim(info.email)) {
                if (StringUtil.isEmpty(old.email) || !old.email.equals(info.email)) {
                    Account dupAccount = dao.getAccountByEmailForUpdate(info.email);
                    if (dupAccount != null) {
                        throw new AppException("邮箱已经被注册");
                    }
                }
            } else {
                info.email = null;//强制设置为null
            }

            Account newAccount = new Account();
            newAccount.userName = info.userName;
            newAccount.name = info.name;
            newAccount.mobileNo = info.mobileNo;
            newAccount.email = info.email;


            if (info.superBoss > 0) {
                newAccount.superBoss = info.superBoss;
                old.superBoss = info.superBoss;
                int row = dao.getAccountOwnerDepartmentCount(info.id);
                if (row <= 0) {
                    throw new AppException("非组织架构部门负责人，无法赋予boss权限");
                }
            } else {
                newAccount.superBoss = 0;
                old.superBoss = 0;
            }
            //
            bizService.addOptLog(optAccount, old.id, old.name,
                    OptLog.EVENT_ID_编辑成员, old, newAccount,
                    "userName", "name", "mobileNo", "email");
            PatternUtil.checkUserName(info.userName);
            old.userName = info.userName;
            old.name = info.name;
            old.pinyinName = PinYinUtil.getHanziPinYin(old.name);
            old.mobileNo = info.mobileNo;
            old.email = info.email;
            dao.update(old);
            bizService.addOrUpdateCompanyAccount(optAccount.companyId,
                    optAccount, old, info.roleList, info.departmentIdList);
            //确保企业必须有一个有效的管理员
            int adminNum = dao.getCompanyAdminNum(optAccount.companyId);
            if (adminNum == 0) {
                throw new AppException("企业至少需要一个管理员");
            }

        }

        @Transaction
        @Override
        public void updateAccountInfo(String token, Account bean) {
            Account old = bizService.getExistedAccountByTokenForUpdate(token);
            old.imageId = bean.imageId;
            if (StringUtil.isEmptyWithTrim(bean.name)) {
                throw new AppException("名称不能为空");
            }
            old.name = bean.name;
            old.pinyinName = PinYinUtil.getHanziPinYin(old.name);
            dao.update(old);
        }

        @Override
        @Transaction
        public void updatePassword(String token, String oldPassword, String newPassword) {
            Account account = bizService.getExistedAccountByTokenForUpdate(token);
            oldPassword = BizUtil.encryptPassword(oldPassword);
            if (!account.password.equals(oldPassword)) {
                throw new IllegalArgumentException("旧密码错误");
            }
            if (StringUtil.isEmpty(newPassword)) {
                throw new IllegalArgumentException("新密码格式错误");
            }
            if (oldPassword.equals(newPassword)) {
                throw new AppException("新旧密码不可相同");
            }
            //校验密码规则
            PasswordRule rule = dao.getPasswordRuleByCompanyId(account.companyId);
            checkCompanyPasswordRule(account, newPassword, rule);
            account.beforeLastPassword = account.lastPassword;
            account.lastPassword = account.password;

            account.password = BizUtil.encryptPassword(newPassword.trim());
            account.encryptPassword = TripleDESUtil.encrypt(newPassword.trim(), ConstDefine.GLOBAL_KEY);
            account.needUpdatePassword = false;
            dao.update(account);
            //
            if (ldapService != null) {
                ldapService.addAccount(account.userName, account.password);
            }
        }

        private void checkCompanyPasswordRule(Account account, String password, PasswordRule rule) {
            if (null == rule || BizUtil.isNullOrEmpty(rule.rules)) {
                return;
            }
            List<Integer> rules = rule.rules;
            for (Integer ruleType : rules) {
                if (ruleType == PasswordRule.RULE_八位长度以上) {
                    if (password.length() < 8) {
                        throw new AppException("密码需要8位以上长度");
                    }
                } else if (ruleType == PasswordRule.RULE_包含大小写字母) {
                    Pattern pattern = Pattern.compile("[a-zA-Z]+");
                    if (!pattern.matcher(password).find()) {
                        throw new AppException("密码需要包含大小写字母");
                    }
                } else if (ruleType == PasswordRule.RULE_包含数字) {
                    Pattern pattern = Pattern.compile("\\d");
                    if (!pattern.matcher(password).find()) {
                        throw new AppException("密码需要包含有数字");
                    }
                } else if (ruleType == PasswordRule.RULE_包含特殊字符) {
                    Pattern pattern = Pattern.compile("[!@#$%^&*?_+)(]+");
                    if (!pattern.matcher(password).find()) {
                        throw new AppException("密码需要包含特殊字符");
                    }
                } else if (ruleType == PasswordRule.RULE_不能包含用户名) {
                    if (password.contains(account.userName)) {
                        throw new AppException("密码不能包含用户名");
                    }
                } else if (ruleType == PasswordRule.RULE_不能与前两次密码重复) {
                    String newPwd = BizUtil.encryptPassword(password);
                    if (newPwd.equals(account.lastPassword) || newPwd.equals(account.beforeLastPassword)) {
                        throw new AppException("密码不能与前两次的密码重复");
                    }
                }
            }

        }

        @Transaction
        @Override
        public void resetPassword(String mobileNo, String password, String verifyCode) {
            bizService.checkVerifyCode(mobileNo, verifyCode);
            Account account = dao.getAccountByMobileNoForUpdate(mobileNo);
            if (account == null) {
                throw new AppException("该手机号未注册");
            }
            account.password = BizUtil.encryptPassword(password.trim());
            account.encryptPassword = TripleDESUtil.encrypt(password.trim(), ConstDefine.GLOBAL_KEY);
            dao.update(account);
            //
            if (ldapService != null) {
                ldapService.addAccount(account.userName, account.password);
            }
        }

        @Transaction
        @Override
        public void bindNewMobile(String token, String newMobileNo,
                                  String newVerifyCode) {
            Account account = bizService.getExistedAccountByTokenForUpdate(token);
            if (account.mobileNo.equals(newMobileNo)) {
                return;
            }
            Account old = dao.getAccountByMobileNo(newMobileNo);
            if (old != null) {
                throw new IllegalArgumentException("新手机号已被人使用");
            }
            bizService.checkMobileVerifyCode(account, newMobileNo, newVerifyCode);
            account.mobileNo = newMobileNo;
            dao.update(account);
        }

        /**
         * 取消绑定微信
         */
        @Transaction
        @Override
        public void unbindWeixin(String token) {
            Account account = bizService.getExistedAccountByTokenForUpdate(token);
            account.wxOpenId = null;
            account.wxUnionId = null;
            dao.update(account);
        }

        /**
         * 取消绑定邮箱
         */
        @Transaction
        @Override
        public void unbindEmail(String token) {
            Account account = bizService.getExistedAccountByTokenForUpdate(token);
            account.email = null;
            dao.update(account);
        }

        @Transaction
        @Override
        public void sendEmailVeryCode(String token, String email) {
            checkNull(email, "邮箱不能为空");
            Account account = bizService.getExistedAccountByToken(token);
            String name = GlobalConfig.getValue("name");
            StringBuilder code = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                code.append(RandomUtil.randomInt(10));
            }
            String content = code + " (请勿泄露,30分钟内有效)。";
            emailService.sendMail(email, "【" + name + "】安全验证", content);
            //
            // 30分钟有效
            int validTime = 30 * 60;
            Date validDateTime = new Date(System.currentTimeMillis() + validTime * 1000);
            Kaptcha kaptcha = dao.getByFieldForUpdate(Kaptcha.class, "sign", email);
            if (kaptcha == null) {
                kaptcha = new Kaptcha();
                kaptcha.type = Kaptcha.TYPE_邮箱验证码;
                kaptcha.sign = email;
                kaptcha.validTime = validDateTime;
                kaptcha.code = code.toString();
                kaptcha.createAccountId = account.id;
                dao.add(kaptcha);
            } else {
                //20200214新增
                KaptchaLog log = KaptchaLog.create(kaptcha);
                dao.add(log);
                //
                kaptcha.validTime = validDateTime;
                kaptcha.code = code.toString();
                dao.update(kaptcha);
            }
        }

        /**
         * 发送手机验证码
         */
        @Override
        @Transaction
        public void sendMobileVerifyCode(String token, String mobileNo) {
            Account account = bizService.getExistedAccountByToken(token);
            logger.info("BizAction.sendMobileVerifyCode,accountId: {}, mobileNo:{}", account.id, mobileNo);
            bizService.sendVerifyCode(mobileNo);
        }

        @Override
        @Transaction
        public void sendMobileCode(String mobileNo, String ip, String sign, String kaptchaCode) {
            // 校验验证码
            bizService.checkVerifyCode(sign, kaptchaCode);
            logger.info("BizAction.sendMobileCode: mobileNo:{}, ip:{}", mobileNo, ip);
            if (StringUtil.isEmptyWithTrim(mobileNo)) {
                throw new AppException("手机号不能为空");
            }
            //检查是否注册了
            Account account = dao.getAccountByMobileNo(mobileNo);
            if (account != null) {
                throw new AppException("手机号已注册");
            }
            bizService.sendVerifyCode(mobileNo);
        }

        @Override
        @Transaction
        public void sendInviteMobileCode(String token, String mobileNo) {
            Account account = bizService.getExistedAccountByTokenForUpdate(token);
            Account invitedAccount = dao.getAccountByMobileNo(mobileNo);
            if (invitedAccount == null) {
                throw new AppException("被邀请人没有注册，请在该手机号注册后再邀请该用户");
            }
            CompanyMember companyMember = dao.getCompanyMemberByAccountIdCompanyId(invitedAccount.id, account.companyId);
            if (companyMember != null) {
                throw new AppException("被邀请人已经是企业成员");
            }
            bizService.sendVerifyCode(mobileNo);
        }

        /**
         * 注意；这里前端一定要控制不能同时触发两次
         */
        @Override
        @Transaction
        public void sendLoginMobileCode(String mobileNo, String sign, String kaptchaCode) {
            // 校验验证码
            bizService.checkVerifyCode(sign, kaptchaCode);
            Account account = dao.getAccountByMobileNo(mobileNo);
            if (account == null) {
                throw new AppException("手机号未注册,请先注册");
            }
            bizService.checkAccount(account);
            bizService.sendVerifyCode(mobileNo);
        }

        @Override
        @Transaction
        public void sendMobileVerifyCodeForResetPassword(String mobileNo, String sign, String kaptchaCode) {
            bizService.checkVerifyCode(sign, kaptchaCode);
            Account bean = dao.getAccountByMobileNo(mobileNo);
            if (bean == null) {
                throw new AppException("手机号码不存在");
            }
            logger.info("BizAction.sendMobileVerifyCodeForResetPassword, mobileNo: {}, accountId: {}", mobileNo, bean.id);
            bizService.sendVerifyCode(mobileNo);
        }

        @Transaction
        @Override
        public void bindEmail(String token, String email) {
            Account account = bizService.getExistedAccountByTokenForUpdate(token);
            if (!StringUtil.isEmpty(account.email)) {
                throw new AppException("已经绑定邮箱，不能重复绑定");
            }
            if (!PatternUtil.isEmail(email)) {
                throw new AppException("邮箱格式错误");
            }
            Account old = dao.getAccountByEmail(email);
            if (old != null) {
                throw new AppException("邮箱已经被绑定");
            }
            Kaptcha kaptcha = new Kaptcha();
            kaptcha.type = Kaptcha.TYPE_绑定邮箱;
            kaptcha.sign = BizUtil.randomUUID();
            kaptcha.code = email;
            kaptcha.createAccountId = account.id;
            dao.add(kaptcha);
            //
            String url = GlobalConfig.webUrl + "p/main/confirm_bind_email/" + kaptcha.sign;
            String title = "确认绑定邮箱";
            String content = "点击按钮即可绑定邮箱。";
            String btnName = "确认绑定邮箱";
            sendEmail(email, title, content, btnName, url);
        }

        @Transaction
        @Override
        public void confirmBindEmail(String sign) {
            Kaptcha kaptcha = dao.getExistedKaptchaBySign(sign);
            if (kaptcha.type != Kaptcha.TYPE_绑定邮箱) {
                throw new AppException("验证码已过期");
            }
            Account account = dao.getExistedByIdForUpdate(Account.class, kaptcha.createAccountId);
            if (!StringUtil.isEmpty(account.email)) {
                throw new AppException("已经绑定邮箱，不能重复绑定");
            }
            Account old = dao.getAccountByEmail(kaptcha.code);
            if (old != null) {
                throw new AppException("此邮箱已经被使用");
            }
            account.email = kaptcha.code;
            dao.update(account);
            dao.deleteById(Kaptcha.class, kaptcha.id);
        }

        //创建目录
        @Transaction
        @Override
        public void addDirectory(String token, File bean) {
            Account account = bizService.getExistedAccountByToken(token);
            if (StringUtil.isEmpty(bean.name)) {
                throw new IllegalArgumentException("名称不能为空");
            }
            bizService.checkPermissionForProjectAccess(account, bean.projectId);
            bizService.checkProjectPermission(account, bean.projectId, Permission.ID_创建文件夹);
            File parent = null;
            if (bean.parentId > 0) {
                parent = dao.getExistedById(File.class, bean.parentId);
                if (parent.projectId != bean.projectId) {
                    throw new IllegalArgumentException("父级目录不存在");
                }
                bean.level = parent.level + 1;
            } else {
                bean.level = 1;
            }
            bean.name = bizService.getFileName(bean.name);
            bean.companyId = account.companyId;
            bean.createAccountId = account.id;
            bean.createAccountName = account.name;
            bean.createAccountImageId = account.imageId;
            bean.uuid = BizUtil.randomUUID();
            bean.isDirectory = true;
            bean.path = bizService.getFilePath(bean, parent);
            bizService.checkFilePath(bean);
            dao.add(bean);
        }

        @Transaction
        @Override
        public void updateDirectory(String token, File bean) {
            Account account = bizService.getExistedAccountByToken(token);
            File old = dao.getExistedByIdForUpdate(File.class, bean.id);
            if (!old.isDirectory) {
                throw new IllegalArgumentException("只有目录可以编辑");
            }
            if (!bizService.checkAccountProjectPermission(account.id, old.projectId, old.enableRole,
                    old.createAccountId, old.roles, null)) {
                throw new IllegalArgumentException("权限不足");
            }
            old.roles = BizUtil.distinct(bean.roles);
            old.enableRole = bean.enableRole;
            old.color = bean.color;
            old.name = bizService.getFileName(bean.name);
            old.path = bizService.getFilePath(old, old.parentId);
            bizService.checkFilePath(old);
            old.updateAccountId = account.id;
            old.updateAccountName = account.name;
            old.updateAccountImageId = account.imageId;
            dao.update(old);
        }

        /***/
        @Override
        public FileInfo getFileInfoById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            FileInfo fileInfo = dao.getExistedById(FileInfo.class, id);
            bizService.checkPermissionForProjectAccess(account, fileInfo.projectId);
            return fileInfo;
        }

        /**
         * 更新文件
         */
        @Override
        public void updateFile(String token, int id, String newAttachmentUuid) {
            Account account = bizService.getExistedAccountByToken(token);
            File old = dao.getExistedById(File.class, id);
            if (!bizService.isCompanyAdmin(account)) {
                if (account.id != old.createAccountId) {
                    throw new AppException("权限不足");
                }
            }
            Attachment newAtt = dao.getAttachmentByUuid(newAttachmentUuid);
            if (newAtt == null) {
                throw new AppException("参数错误,文件不存在");
            }
            old.uuid = newAttachmentUuid;
            old.updateAccountId = account.id;
            dao.update(old);
        }

        @Override
        public File getFileByUuid(String token, String uuid) {
            Account account = bizService.getExistedAccountByToken(token);
            File file = dao.getFileByUuid(uuid);
            if (null != file && file.isDelete) {
                throw new AppException("文件已经被删除");
            }
            if (null == file) {
                throw new AppException("文件不存在");
            }
            bizService.checkPermissionForProjectAccess(account, file.projectId);
            return file;
        }

        @Transaction
        @Override
        public int addFile(String token, int projectId, String attachmentUuid, Integer parentId) {
            Account account = bizService.getExistedAccountByToken(token);
            Project project = dao.getExistedById(Project.class, projectId);
            bizService.checkPermission(account, project.companyId);
            bizService.checkProjectPermission(account, project.id, Permission.ID_上传文件);
            Attachment attachment = dao.getAttachmentByUuid(attachmentUuid);
            if (attachment == null) {
                throw new AppException("文件不存在" + attachmentUuid);
            }
            File file = addFile0(account, projectId, attachment, parentId);
            //
            return file.id;
        }

        private File addFile0(Account account, int projectId, Attachment attachment, Integer parentId) {
            Project project = dao.getExistedById(Project.class, projectId);
            if (parentId == null) {
                parentId = 0;
            }
            File parent = null;
            if (parentId > 0) {
                parent = dao.getExistedById(File.class, parentId);
                bizService.checkParentFile(parent, projectId);
            }
            File file = new File();
            file.companyId = account.companyId;
            file.projectId = project.id;
            file.createAccountId = account.id;
            file.createAccountName = account.name;
            file.createAccountImageId = account.imageId;
            file.parentId = parentId;
            file.level = bizService.getFileLevel(parent);
            file.name = bizService.getFileName(attachment.name);
            file.size = attachment.size;
            file.uuid = attachment.uuid;
            file.path = bizService.getFilePath(file, parent);
            bizService.checkFilePath(file);
            dao.add(file);
            //
            bizService.addChangeLog(account, file.projectId, 0,
                    ChangeLog.TYPE_新增文件, JSONUtil.toJson(file));
            //
            return file;
        }

        @Transaction
        @Override
        public void deleteFileById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            deleteFileById(token, account, id);
        }

        private void deleteFileById(String token, Account account, int id) {
            List<File> deletes = new ArrayList<>();
            File old = dao.getExistedByIdForUpdate(File.class, id);
            if (old.isDelete) {
                return;
            }
            deletes.add(old);
            bizService.checkPermissionForProjectAccess(account, old.projectId);
            bizService.checkProjectPermission(account, old.projectId, Permission.ID_删除文件);
            if (old.isDirectory) {
                if (!bizService.checkAccountProjectPermission(account.id, old.projectId, old.enableRole,
                        old.createAccountId, old.roles, null)) {
                    throw new IllegalArgumentException("权限不足");
                }
                Set<Integer> childrenFiles = getChildrenFiles(old);
                for (Integer fileId : childrenFiles) {
                    File child = dao.getExistedByIdForUpdate(File.class, fileId);
                    deletes.add(child);
                    child.isDelete = true;
                    dao.update(child);
                }
            }
            old.isDelete = true;
            old.isCreateIndex = false;
            dao.update(old);
            //
            bizService.addChangeLog(account, old.projectId, 0,
                    ChangeLog.TYPE_删除文件, JSONUtil.toJson(old));

            //如果是关联文件也要解除关联关系
            if (!BizUtil.isNullOrEmpty(deletes)) {
                deletes.forEach(f -> {
                    Attachment attachment = dao.getAttachmentByUuid(f.uuid);
                    if (null != attachment) {
                        List<AttachmentAssociated> associated = dao.getAttachmentAssociatedListByAttachmentIdAndCompanyId(attachment.id, account.companyId);
                        if (!BizUtil.isNullOrEmpty(associated)) {
                            try {
                                associated.forEach(item -> deleteTaskAttachment0(token, item.taskId, item.attachmentId, true, AttachmentAssociated.TYPE_文件));
                            } catch (Exception e) {
                                logger.warn("can't delete task associate file", e);
                            }
                        }
                    }
                });
            }
        }

        @Transaction
        @Override
        public void deleteFiles(String token, List<Integer> ids) {
            Account account = bizService.getExistedAccountByToken(token);
            for (Integer id : ids) {
                deleteFileById(token, account, id);
            }
        }

        /**
         * 找出父目录下的所有子目录和文件
         */
        private Set<Integer> getChildrenFiles(File parentFile) {
            if (parentFile.isDelete || (!parentFile.isDirectory)) {
                throw new AppException("文件夹不存在");
            }
            Set<Integer> childrenFiles = new LinkedHashSet<>();
            FileQuery query = new FileQuery();
            query.projectId = parentFile.projectId;
            query.pageSize = Integer.MAX_VALUE;
            query.isDelete = false;
            List<File> list = dao.getList(query);
            Queue<File> queue = new LinkedList<>();
            queue.offer(parentFile);
            while (!queue.isEmpty()) {
                File dir = queue.poll();
                for (File e : list) {
                    if (e.parentId == dir.id) {
                        queue.offer(e);
                        childrenFiles.add(e.id);
                    }
                }
            }
            return childrenFiles;
        }

        private Set<Integer> getParentFiles(File file) {
            Set<Integer> list = new LinkedHashSet<>();
            if (file.parentId == 0) {
                return list;
            }
            File parentFile = file;
            while (parentFile.parentId > 0) {
                list.add(parentFile.parentId);
                parentFile = dao.getExistedById(File.class, parentFile.parentId);
            }
            return list;
        }

        @Override
        public Map<String, Object> getFiles(String token, FileQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            if (query.projectId == null) {
                throw new AppException("项目不能为空");
            }
            bizService.checkPermissionForProjectAccess(account, query.projectId);
            setupQuery(account, query);
            //文件夹权限控制
            File parent = null;
            if (query.parentId != null && query.parentId > 0) {
                parent = dao.getExistedById(File.class, query.parentId);
                if (!bizService.checkAccountProjectPermission(account.id, query.projectId,
                        parent.enableRole, parent.createAccountId, parent.roles, null)) {//如果没有权限
                    logger.warn("getFiles faild.权限不足 accountId:{} parentId:{}", account.id, parent.id);
                    throw new AppException("权限不足");
                }
            }
            Map<String, Object> result = new HashMap<>();
            query.sortFields = new String[]{"isDirectorySort", "createTimeSort"};
            query.isDirectorySort = Query.SORT_TYPE_DESC;
            query.createTimeSort = Query.SORT_TYPE_DESC;
            result.put("list", dao.getList(query));
            result.put("count", dao.getListCount(query));
            result.put("parent", parent);
            return result;
        }

        //只能查看目录
        @Override
        public List<DirectoryNode> getDirectoryNode(String token, int projectId) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkPermissionForProjectAccess(account, projectId);
            List<DirectoryNode> directorys = new ArrayList<>();
            FileQuery query = new FileQuery();
            query.projectId = projectId;
            query.pageSize = Integer.MAX_VALUE;
            query.isDelete = false;
            query.isDirectory = true;
            List<File> list = dao.getList(query);
            Queue<DirectoryNode> queue = new LinkedList<>();
            for (File e : list) {
                if (!bizService.checkAccountProjectPermission(account.id, projectId,
                        e.enableRole, e.createAccountId, e.roles, null)) {//如果没有权限
                    continue;
                }
                if (e.level == 1) {
                    DirectoryNode directory = createDirectoryNode(e);
                    directory.expand = true;
                    queue.offer(directory);
                    directorys.add(directory);
                }
            }
            while (!queue.isEmpty()) {
                DirectoryNode dir = queue.poll();
                for (File e : list) {
                    if (e.parentId == dir.id) {
                        DirectoryNode child = createDirectoryNode(e);
                        dir.children.add(child);
                        queue.offer(child);
                    }
                }
            }
            return directorys;
        }

        //
        private DirectoryNode createDirectoryNode(File cd) {
            DirectoryNode dir = new DirectoryNode();
            dir.id = cd.id;
            dir.title = cd.name;
            dir.isDirectory = cd.isDirectory;
            dir.level = cd.level;
            dir.parentId = cd.parentId;
            dir.path = cd.path;
            return dir;
        }
        //

        /**
         * 移动文件或目录
         */
        @Transaction
        @Override
        public void moveFiles(String token, int projectId, Set<Integer> fileIds, Integer parentId) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkPermissionForProjectAccess(account, projectId);
            bizService.checkProjectPermission(account, projectId, Permission.ID_移动);
            Set<Integer> parentIdList = new HashSet<>();
            File parent = null;
            if (parentId == null || parentId == 0) {
                parentId = 0;
            } else {
                if (fileIds.contains(parentId)) {
                    throw new AppException("不能移动自己");
                }
                parent = dao.getExistedByIdForUpdate(File.class, parentId);
                bizService.checkParentFile(parent, projectId);
                parentIdList = getParentFiles(parent);
                logger.info("parentIdList:" + DumpUtil.dump(parentIdList));
            }
            for (Integer fileId : fileIds) {
                if (fileId.equals(parentId)) {
                    throw new AppException("目录不存在");
                }
                if (parentIdList.contains(fileId)) {
                    throw new AppException("不能移动父目录");
                }
                File child = dao.getExistedByIdForUpdate(File.class, fileId);
                if (child.projectId != projectId) {
                    throw new AppException("文件不属于当前项目[" + child.name + "]");
                }
                moveDir(account, parent, child, true);
            }
        }

        private void moveDir(Account account, File parent, File file, boolean firstLevel) {
            if (parent == null) {
                parent = new File();
            }
            if (parent.id == file.id) {
                logger.error("parent.id==file.id", parent.id);
                throw new AppException("不能移动自己");
            }
            file.parentId = parent.id;
            file.level = bizService.getFileLevel(parent);
            file.path = bizService.getFilePath(file, parent);
            bizService.checkFilePath(file);
            dao.updateSpecialFields(file, "parentId", "level", "path");
            if (file.isDirectory) {
                if (firstLevel) {
                    if (!bizService.checkAccountProjectPermission(account.id, file.projectId, file.enableRole,
                            file.createAccountId, file.roles, null)) {
                        throw new IllegalArgumentException("权限不足");
                    }
                }
                List<File> children = bizService.getFileChildren(file.id);
                for (File child : children) {
                    child = dao.getExistedByIdForUpdate(File.class, child.id);
                    moveDir(account, file, child, false);
                }
            }
        }

        @Transaction
        @Override
        public int createCategory(String token, Category bean) {
            Account account = bizService.getExistedAccountByToken(token);
            Project project = dao.getExistedById(Project.class, bean.projectId);
            bizService.checkPermission(account, project.companyId);
            int level = 1;
            if (bean.parentId > 0) {
                Category parent = dao.getExistedById(Category.class, bean.parentId);
                level = parent.level + 1;
            }
            bean.companyId = project.companyId;
            bean.createAccountId = account.id;
            bean.level = level;
            bean.sortWeight = Integer.MAX_VALUE;
            BizUtil.checkUniqueKeysOnAdd(dao, bean, "名称已存在，不能重复");
            BizUtil.checkValid(bean);
            dao.add(bean);
            //
            if (CornerstoneBizSystem.webEventServer != null) {
                AutoTranscationCallback.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        WebEvent event = WebEvent.createWebEvent(account.id, account.companyId,
                                WebEvent.TYPE_任务分类创建, bean.objectType, bean.projectId,
                                null, null, null, bean.id, null, null);
                        CornerstoneBizSystem.webEventServer.boardcastAsync(event);
                    }
                });
            }
            //
            return bean.id;
        }

        @Transaction
        @Override
        public void updateCategory(String token, Category bean) {
            Account account = bizService.getExistedAccountByToken(token);
            Category old = dao.getExistedByIdForUpdate(Category.class, bean.id);
            bizService.checkPermissionForProjectAccess(account, old.projectId);
            BizUtil.checkUniqueKeysOnUpdate(dao, bean, old, "名称已存在，不能重复");
            old.name = bean.name;
            old.color = bean.color;
            old.remark = bean.remark;
            old.updateAccountId = account.id;
            BizUtil.checkValid(old);
            dao.update(old);
            //
            if (CornerstoneBizSystem.webEventServer != null) {
                AutoTranscationCallback.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        WebEvent event = WebEvent.createWebEvent(account.id, account.companyId,
                                WebEvent.TYPE_任务分类编辑, old.objectType, old.projectId,
                                null, null, null, old.id, null, null);
                        CornerstoneBizSystem.webEventServer.boardcastAsync(event);
                    }
                });
            }
        }

        @Transaction
        @Override
        public void updateCategorySortWeightAndRelation(String token, List<CategoryNode> nodes) {
            Account account = bizService.getExistedAccountByToken(token);
            if (nodes == null || nodes.isEmpty()) {
                return;
            }
            CategoryNode first = nodes.get(0);
            Category bean = dao.getExistedByIdForUpdate(Category.class, first.id);
            bizService.checkPermissionForProjectAccess(account, bean.projectId);
            int sortWeight = 1;
            for (CategoryNode tree : nodes) {
                recurveCategoryTree(0, 1, sortWeight++, tree);
            }
        }

        //递归Category
        private void recurveCategoryTree(int parentId, int level, int sortWeight, CategoryNode tree) {
            Category bean = dao.getExistedByIdForUpdate(Category.class, tree.id);
            bean.level = level;
            bean.parentId = parentId;
            bean.sortWeight = sortWeight;
            dao.update(bean);
            if (tree.children != null) {
                for (CategoryNode child : tree.children) {
                    sortWeight++;
                    recurveCategoryTree(bean.id, bean.level + 1, sortWeight, child);
                }
            }
        }

        @Transaction
        @Override
        public void deleteCategory(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            Category old = dao.getExistedByIdForUpdate(Category.class, id);
            bizService.checkPermissionForProjectAccess(account, old.projectId);
            CategoryQuery categoryQuery = new CategoryQuery();
            categoryQuery.parentId = id;
            int childCategoryCount = dao.getListCount(categoryQuery);
            if (childCategoryCount > 0) {
                throw new AppException("含有子分类不能删除");
            }
            dao.deleteById(Category.class, old.id);
            TaskQuery query = new TaskQuery();
            query.categoryId = id;
            query.pageSize = Integer.MAX_VALUE;
            List<Task> tasks = dao.getList(query);
            for (Task task : tasks) {
                task = dao.getExistedByIdForUpdate(Task.class, task.id);
                task.categoryIdList.remove((Integer) id);
                dao.update(task);
            }
            //
            if (CornerstoneBizSystem.webEventServer != null) {
                AutoTranscationCallback.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        WebEvent event = WebEvent.createWebEvent(account.id, account.companyId,
                                WebEvent.TYPE_任务分类删除, old.objectType, old.projectId,
                                null, null, null, old.id, null, null);
                        CornerstoneBizSystem.webEventServer.boardcastAsync(event);
                    }
                });
            }
        }


        private int copyCategory(Category bean) {
            Category old = dao.getCategory(bean.projectId, bean.objectType, bean.name);
            if (old != null) {
                return old.id;
            }
            int oldId = bean.id;
            int newId = dao.add(bean);
            bean.id = oldId;
            return newId;
        }

        @Transaction
        @Override
        public void batchCopyCategories(String token, int projectId, int srcObjectType, int dstProjectId, int dstObjectType) {
            Account account = bizService.getExistedAccountByToken(token);
            if (projectId == dstProjectId && srcObjectType == dstObjectType) {
                throw new AppException("同项目同模块内无需复制");
            }
            //跨项目
            if (dstProjectId > 0) {
                Project dstProject = dao.getExistedById(Project.class, dstProjectId);
                bizService.checkPermissionForProjectAccess(account, dstProject);
            }
            //同项目复制
            Project srcProject = dao.getExistedById(Project.class, projectId);
            bizService.checkPermissionForProjectAccess(account, srcProject);

            CategoryQuery query = new CategoryQuery();
            query.projectId = projectId;
            query.objectType = srcObjectType;
            query.pageSize = Integer.MAX_VALUE;
            query.idSort = Query.SORT_TYPE_ASC;
            List<Category> list = dao.getList(query);
            for (Category category : list) {
                if (dstProjectId > 0) {
                    category.projectId = dstProjectId;
                }
                category.objectType = dstObjectType;
                category.createAccountId = account.id;
                category.updateAccountId = account.id;
            }
            Map<Integer, Integer> ids = new HashMap<>();
            Queue<Category> queue = new LinkedList<>();
            for (Category e : list) {
                if (e.level == 1) {
                    int newId = copyCategory(e);
                    ids.put(e.id, newId);
                    queue.offer(e);
                }
            }
            while (!queue.isEmpty()) {
                Category parent = queue.poll();
                for (Category e : list) {
                    if (e.parentId == parent.id) {
                        e.parentId = ids.get(parent.id);
                        int newId = copyCategory(e);
                        ids.put(e.id, newId);
                        queue.offer(e);
                    }
                }
            }
        }


        @Override
        public List<CategoryNode> getCategoryNodeList(String token, int projectId, int objectType) {
            Account account = bizService.getExistedAccountByToken(token);
            return getCategoryNodeList0(account, projectId, objectType);
        }

        private List<CategoryNode> getCategoryNodeList0(Account account, int projectId, int objectType) {
            bizService.checkPermissionForProjectAccess(account, projectId);
            List<CategoryNode> directorys = new ArrayList<>();
            CategoryQuery query = new CategoryQuery();
            query.projectId = projectId;
            query.objectType = objectType;
            query.pageSize = Integer.MAX_VALUE;
            query.objectTypeSort = Query.SORT_TYPE_ASC;
            query.sortWeightSort = Query.SORT_TYPE_ASC;
            List<Category> list = dao.getList(query);
            Queue<CategoryNode> queue = new LinkedList<>();
            for (Category e : list) {
                if (e.level == 1) {
                    CategoryNode directory = createCategoryNode(e);
                    directory.expand = true;
                    queue.offer(directory);
                    directorys.add(directory);
                }
            }
            while (!queue.isEmpty()) {
                CategoryNode dir = queue.poll();
                for (Category e : list) {
                    if (e.parentId == dir.id) {
                        CategoryNode child = createCategoryNode(e);
                        dir.children.add(child);
                        queue.offer(child);
                    }
                }
            }
            return directorys;
        }

        //
        private CategoryNode createCategoryNode(Category category) {
            CategoryNode dir = new CategoryNode();
            dir.id = category.id;
            dir.name = category.name;
            dir.level = category.level;
            dir.objectType = category.objectType;
            dir.parentId = category.parentId;
            dir.projectId = category.projectId;
            dir.remark = category.remark;
            dir.color = category.color;
            return dir;
        }

        //
        @Override
        @Transaction
        public int addTaskWorkTimeLog(String token, TaskWorkTimeLog bean) {
            Account account = bizService.getExistedAccountByToken(token);
            Task task = dao.getExistedByIdForUpdate(Task.class, bean.taskId);
            if (task.isDelete) {
                throw new AppException("任务不存在");
            }
            if (BizUtil.isNullOrEmpty(task.ownerAccountIdList) || !task.ownerAccountIdList.contains(account.id)) {
                throw new AppException("您不是当前任务责任人，无法添加工时信息");
            }
            bizService.checkPermissionForProjectAccess(account, task.projectId);
            bean.createAccountId = account.id;
            bean.projectId = task.projectId;
            bean.companyId = task.companyId;
            BizUtil.checkValid(bean);
            dao.add(bean);
            //
            task.workTime = dao.calcWorkTime(task.id);
            dao.updateSpecialFields(task, "workTime");
            //
            return bean.id;
        }

        //
        private String getProjectPipelineNewName(int projectId, String name) {
            for (int i = 1; i <= 1000; i++) {
                String newName = "(复制" + i + ")" + name;
                ProjectPipeline old = dao.getProjectPipeline(projectId, newName);
                if (old == null) {
                    return newName;
                }
            }
            throw new AppException("复制失败名称重复");
        }

        //
        @Transaction
        @Override
        public void updateTaskWorkTimeLog(String token, TaskWorkTimeLog bean) {
            Account account = bizService.getExistedAccountByToken(token);
            TaskWorkTimeLog old = dao.getExistedByIdForUpdate(TaskWorkTimeLog.class, bean.id);
            if (old == null) {
                return;
            }
            if (old.createAccountId != account.id && (!bizService.isCompanyAdmin(account))) {
                throw new AppException("权限不足");
            }
            List<ChangeLogItem> changeLogItems = new ArrayList<>();
            if (old.hour != bean.hour) {
                ChangeLogItem item = new ChangeLogItem();
                item.name = "工时耗费时长";
                item.beforeContent = old.hour + "";
                old.hour = bean.hour;
                item.afterContent = bean.hour + "";
                bizService.addChangeLogItem(changeLogItems, item);
            }

            if (!old.content.equals(bean.content)) {
                ChangeLogItem item = new ChangeLogItem();
                item.name = "工时工作事项";
                item.beforeContent = old.content + "";
                old.content = bean.content;
                item.afterContent = bean.content + "";
                bizService.addChangeLogItem(changeLogItems, item);
            }

            if (!old.startTime.equals(bean.startTime)) {
                ChangeLogItem item = new ChangeLogItem();
                item.name = "工时开始时间";
                item.beforeContent = DateUtil.formatDate(old.startTime, "yyyy-MM-dd");
                old.startTime = bean.startTime;
                item.afterContent = DateUtil.formatDate(bean.startTime, "yyyy-MM-dd");
                bizService.addChangeLogItem(changeLogItems, item);
            }

            dao.update(old);
            //
            Task task = dao.getExistedByIdForUpdate(Task.class, old.taskId);
            bizService.checkPermissionForProjectAccess(account, task.projectId);
            task.workTime = dao.calcWorkTime(task.id);
            dao.updateTaskWorkTime(task);

            //添加工时修改日志到任务变更记录中
            boolean record = GlobalConfig.getBooleanValue("task.worktimelog.record");
            if (record) {
                ChangeLog changeLog = dao.getLastChangeLogByTaskIdCreateAccountId(old.taskId, account.id);
                long now = System.currentTimeMillis();
                if (changeLog == null || changeLog.type != ChangeLog.TYPE_编辑工时记录
                        || (now - changeLog.createTime.getTime() > 5 * 60 * 1000)) {
                    bizService.addChangeLog(account, 0, old.taskId, ChangeLog.TYPE_编辑工时记录,
                            JSONUtil.toJson(changeLogItems));
                } else {
                    List<ChangeLogItem> oldItems = JSONUtil.fromJsonList(changeLog.items, ChangeLogItem.class);
                    oldItems.addAll(changeLogItems);
                    changeLog.items = JSONUtil.toJson(oldItems);
                    dao.update(changeLog);
                }
            }

        }

        @Transaction
        @Override
        public void deleteTaskWorkTimeLog(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            TaskWorkTimeLog old = dao.getExistedByIdForUpdate(TaskWorkTimeLog.class, id);
            if (old == null) {
                return;
            }
            if (old.createAccountId != account.id && (!bizService.isCompanyAdmin(account))) {
                throw new AppException("权限不足");
            }
            dao.deleteById(TaskWorkTimeLog.class, id);
            //
            Task task = dao.getExistedByIdForUpdate(Task.class, old.taskId);
            bizService.checkPermissionForProjectAccess(account, task.projectId);
            task.workTime = dao.calcWorkTime(task.id);
            dao.update(task);
        }

        @Override
        public int getTaskWorkHours(String token, Long date) {
            if (date == null) {
                return 0;
            }
            Account account = bizService.getExistedAccountByToken(token);
            Date day = new Date(date);
            Date startTime = DateUtil.getBeginOfDay(day);
            Date endTime = DateUtil.getNextDay(startTime, 1);
            return dao.getTaskWorkHours(account.companyId, account.id, startTime, endTime);
        }

        @Override
        public Map<String, Object> getTaskWorkTimeLogList(String token, TaskWorkTimeLogQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            query.createAccountId = account.id;
            return getTaskWorkTimeLogList0(account, query);
        }

        private Map<String, Object> getTaskWorkTimeLogList0(Account account, TaskWorkTimeLogQuery query) {
            int totalHours = 0;
            query.isDelete = false;
            List<TaskWorkTimeLogInfo> list = dao.getList(query);
            for (TaskWorkTimeLogInfo e : list) {
                totalHours += e.hour;
            }
            Map<String, Object> result = createResult(list, dao.getListCount(query));
            result.put("totalHours", totalHours);
            return result;
        }

        @Override
        public Map<String, Object> getAllTaskWorkTimeLogList(String token, TaskWorkTimeLogQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            query.taskIdSort = Query.SORT_TYPE_ASC;
            return getTaskWorkTimeLogList0(account, query);
        }

        @Override
        public Map<String, Object> getProjectWorkTimeLogList(String token, TaskWorkTimeLogQuery query) {
            Map<String, Object> result = new HashMap<>();
            Account account = bizService.getExistedAccountByToken(token);
            int totalHours = 0;
            query.isDelete = false;
            query.companyId = account.companyId;
            List<TaskWorkTimeLogInfo> list = dao.getList(query);
            if (BizUtil.isNullOrEmpty(list)) {
                result.put("totalHours", 0);
            } else {
                List<Map<String, Object>> datas = new ArrayList<>();
                Map<Integer, List<TaskWorkTimeLogInfo>> workTimeProjectGroupMap = new HashMap<>();
                //objectType名称
//                Set<Integer> projectIdList = new HashSet<>();
                Set<Integer> objectTypes = new HashSet<>();
                for (TaskWorkTimeLogInfo log : list) {
                    objectTypes.add(log.objectType);
//                    projectIdList.add(log.projectId);
                    workTimeProjectGroupMap.computeIfAbsent(log.projectId, k -> new ArrayList<>()).add(log);
                }
//                if(!query.projectIdInList){
//
//                }
//                List<ProjectModuleInfo> moduleList = dao.getProjectModuleListByProjectIds(projectIdList);
//                Map<Integer,ProjectModuleInfo>  moduleMap= moduleList.stream().filter(k->k.objectType>0).collect(Collectors.toMap(k->k.objectType,v->v));

                List<ObjectType> objectTypeList = dao.getObjectTypeListByIds(objectTypes);
                Map<Integer, ObjectType> objectTypeMap = objectTypeList.stream().collect(Collectors.toMap(k -> k.id, v -> v));
                //模块分组
                workTimeProjectGroupMap.forEach((projectId, logList) -> {
                    Map<String, Object> vm = new HashMap<>();
                    vm.put("projectId", projectId);
                    if (!BizUtil.isNullOrEmpty(logList)) {
                        vm.put("projectName", logList.get(0).projectName);
                        long totalExpectTime = logList.stream().filter(BizUtil.distinctByKey(k -> k.taskId)).mapToInt(k -> k.expectWorkTime).sum();
                        double totalCostTime = logList.stream().mapToDouble(k -> k.hour).sum();
                        Map<Integer, List<TaskWorkTimeLogInfo>> objectTypeGroup = logList.stream().collect(Collectors.groupingBy(k -> k.objectType));
                        List<Map<String, Object>> os = new ArrayList<>();
                        if (!BizUtil.isNullOrEmpty(objectTypeGroup)) {
                            objectTypeGroup.forEach((objectType, infos) -> {
                                long expectTime = infos.stream().filter(BizUtil.distinctByKey(k -> k.taskId)).mapToInt(k -> k.expectWorkTime).sum();
                                double costTime = infos.stream().mapToDouble(k -> k.hour).sum();
                                Map<String, Object> objectMap = new HashMap<>();
                                objectMap.put("expectTime", expectTime);
                                objectMap.put("costTime", costTime);
                                objectMap.put("logs", infos);
                                objectMap.put("objectType", objectType);
                                objectMap.put("objectTypeName", objectTypeMap.get(objectType).name);
                                os.add(objectMap);
                            });
                        }
                        vm.put("totalExpectTime", totalExpectTime);
                        vm.put("totalCostTime", totalCostTime);
                        vm.put("objects", os);
                    } else {
                        vm.put("totalExpectTime", 0);
                    }
                    datas.add(vm);
                });
//            Map<String, Object> result = createResult(list, list.size());
                result.put("totalHours", totalHours);
                result.put("projects", datas);
            }
            return result;
        }

        @Override
        public List<TaskWorkTimeLogInfo4Excel> exportProjectWorkTimeLogList(String token, TaskWorkTimeLogQuery query) {
            query.isDelete = false;
            List<TaskWorkTimeLogInfo> list = dao.getList(query);
            List<TaskWorkTimeLogInfo4Excel> excelDatas = new ArrayList<>();
            for (TaskWorkTimeLogInfo e : list) {
                excelDatas.add(TaskWorkTimeLogInfo4Excel.create(e));
            }
            return excelDatas;
        }

        @Transaction
        @Override
        public int createProjectPipeline(String token, ProjectPipeline bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkPermissionForProjectAccess(account, bean.projectId);
            if (bean.id == -1) {//说明是拷贝被的pipeline
                bean.name = getProjectPipelineNewName(bean.projectId, bean.name);
            }
            Project project = dao.getExistedById(Project.class, bean.projectId);
            bizService.checkProjectPermission(account, bean.projectId, Permission.ID_创建和编辑pipeline);
            bean.companyId = project.companyId;
            bean.lastRunTime = null;
            bean.runCount = 0;
            bean.nextRunTime = BizUtil.nextRunTime(null, bean.cron);
            bean.createAccountId = account.id;
            bean.runId = 0;
            bean.pipelineDefine = getPipelineDefine(bean.script);
            bean.roles = BizUtil.distinct(bean.roles);
            BizUtil.checkValid(bean);
            dao.add(bean);
            return bean.id;
        }

        @Transaction
        @Override
        public void updateProjectPipeline(String token, ProjectPipeline bean) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectPipeline old = dao.getExistedByIdForUpdate(ProjectPipeline.class, bean.id);
            bizService.checkPermissionForProjectAccess(account, old.projectId);
            bizService.checkProjectPermission(account, old.projectId, Permission.ID_创建和编辑pipeline);
            //
            addBackup(old.id, Backup.TYPE_pipeline, account.id, old.name, JSONUtil.toJson(old));
            //
            old.name = bean.name;
            old.script = bean.script;
            old.cron = bean.cron;
            old.nextRunTime = BizUtil.nextRunTime(null, old.cron);
            old.remark = bean.remark;
            old.roles = BizUtil.distinct(bean.roles);
            old.enableRole = bean.enableRole;
            old.group = bean.group;
            old.pipelineDefine = getPipelineDefine(bean.script);
            old.updateAccountId = account.id;
            BizUtil.checkValid(old);
            dao.update(old);
        }

        private PipelineDefine getPipelineDefine(String script) {
            JavaScriptPipeline javaScriptPipeline = new JavaScriptPipeline(script);
            return javaScriptPipeline.getPipelineDefine();
        }

        @Transaction
        @Override
        public void deleteProjectPipeline(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectPipeline old = dao.getExistedByIdForUpdate(ProjectPipeline.class, id);
            if (old.isDelete) {
                return;
            }
            bizService.checkProjectPermission(account, old.projectId, Permission.ID_创建和编辑pipeline);
            bizService.checkPermissionForProjectAccess(account, old.projectId);
            old.isDelete = true;
            old.updateAccountId = account.id;
            dao.update(old);

        }

        @Override
        public List<ProjectPipelineInfo> getProjectPipelineInfoList(String token, ProjectPipelineQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            setupQuery(account, query);
            List<ProjectPipelineInfo> list = dao.getAll(query, "script");
            for (ProjectPipelineInfo e : list) {
                if (e.runLog != null) {
                    e.runLog.script = null;
                }
//				if(e.createAccountId==account.id||
//						bizService.checkAccountProjectPermission(account.id, query.projectId, e.enableRole, e.roles, null)) {
//					ret.add(e);
//				}
            }
            return list;
        }

        @Override
        public ProjectPipelineInfo getProjectPipelineInfoById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectPipelineInfo old = dao.getExistedById(ProjectPipelineInfo.class, id);
            checkDelete(old);
            bizService.checkPermission(account, old.companyId);
            return old;
        }

        private void checkDelete(Object bean) {
            Boolean isDelete = (Boolean) BizUtil.getFieldValue(bean, "isDelete");
            if (isDelete != null && isDelete) {
                throw new AppException("数据不存在");
            }
        }


        @Transaction
        @Override
        public int runProjectPipeline(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectPipelineInfo old = dao.getExistedByIdForUpdate(ProjectPipelineInfo.class, id);
            if (!bizService.checkAccountProjectPermission(account.id, old.projectId, old.enableRole,
                    old.createAccountId, old.roles, null)) {
                throw new AppException("权限不足");
            }
//			bizService.checkProjectPermission(account, old.projectId, Permission.ID_执行pipeline);
            return bizService.runProjectPipeline(account, old);
        }

        /**
         * 停止运行pipeline
         */
        @Override
        @Transaction
        public void stopProjectPipeline(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectPipelineInfo old = dao.getExistedByIdForUpdate(ProjectPipelineInfo.class, id);
            checkDelete(old);
            bizService.checkPermission(account, old.companyId);
            if (!bizService.checkAccountProjectPermission(account.id, old.projectId, old.enableRole,
                    old.createAccountId, old.roles, null)) {
                throw new AppException("权限不足");
            }
            //
            ProjectPipelineRunLog runLog = dao.getExistedByIdForUpdate(ProjectPipelineRunLog.class, old.runId);
            if (runLog.status != ProjectPipelineRunLog.STATUS_Pending && runLog.status != ProjectPipelineRunLog.STATUS_正在执行) {
                throw new AppException("该任务不在运行中");
            }
            runLog.status = ProjectPipelineRunLog.STATUS_执行失败;
            runLog.errorMessage = "手工停止";
            runLog.endTime = new Date();
            runLog.useTime = BizUtil.getUseTime(runLog.startTime.getTime());
            dao.update(runLog);
            //
            ProjectPipelineRunDetailLog detailLog = new ProjectPipelineRunDetailLog();
            detailLog.runLogId = runLog.id;
            detailLog.type = ProjectPipelineRunDetailLog.TYPE_错误;
            detailLog.message = "手工停止";
            dao.add(detailLog);
        }

        @Override
        public ProjectPipelineRunInfo getProjectPipelineRunInfoById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectPipelineInfo old = dao.getExistedByIdForUpdate(ProjectPipelineInfo.class, id);
            checkDelete(old);
            bizService.checkPermission(account, old.companyId);
            ProjectPipelineRunInfo info = new ProjectPipelineRunInfo();
            info.pipelineInfo = old;
            if (old.runId > 0) {
                old.runLog = dao.getExistedById(ProjectPipelineRunLogInfo.class, old.runId);
            }
            return info;
        }


        @Override
        public List<ProjectPipelineRunDetailLog> getProjectPipelineRunDetailLogList(String token, int runLogId,
                                                                                    int startDetailId) {
            ProjectPipelineRunDetailLogQuery query = new ProjectPipelineRunDetailLogQuery();
            query.runLogId = runLogId;
            query.startId = startDetailId;
            query.orderType = ProjectPipelineRunDetailLogQuery.ORDER_TYPE_ID_ASC;
            query.pageSize = 10000;
            List<ProjectPipelineRunDetailLog> list = dao.getList(query);
            return list;
        }


        @Override
        public Map<String, Object> getProjectPipelineRunLogInfoList(String token, ProjectPipelineRunLogQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            if (query.pipelineId == null) {
                throw new AppException("参数错误");
            }
            ProjectPipeline pipeline = dao.getExistedById(ProjectPipeline.class, query.pipelineId);
            bizService.checkPermissionForProjectAccess(account, pipeline.projectId);
            return createResult(dao.getList(query, "script"), dao.getListCount(query));
        }

        @Override
        public ProjectPipelineRunLogInfo getProjectPipelineRunLogInfoById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectPipelineRunLogInfo bean = dao.getExistedById(ProjectPipelineRunLogInfo.class, id);
            bizService.checkPermissionForProjectAccess(account, bean.projectId);
            return bean;
        }

        @Transaction
        @Override
        public void setProjectPipelineRunLogParameter(String token, int id, ParameterValue value) {
            Account account = bizService.getExistedAccountByToken(token);
            checkNull(value, "参数值不能为空");
            ProjectPipelineRunLogInfo old = dao.getExistedById(ProjectPipelineRunLogInfo.class, id);
            bizService.checkPermissionForProjectAccess(account, old.projectId);
            if (old.status != ProjectPipelineRunLogInfo.STATUS_Pending) {
                throw new AppException("状态错误");
            }
            if (old.parameterMap == null) {
                old.parameterMap = new HashMap<>();
            }
            String key = old.stage + "-" + old.parameter;
            old.parameterMap.put(key, value);
            old.status = ProjectPipelineRunLogInfo.STATUS_正在执行;
            dao.update(old);
        }

        @Transaction
        @Override
        public void addTestPlanTestCaseList(String token, int testPlanId, Set<Integer> testCaseIds) {
            Account account = bizService.getExistedAccountByToken(token);
            Task testPlan = dao.getExistedTaskByIdObjectType(testPlanId, Task.OBJECTTYPE_测试计划);
            bizService.checkPermissionForProjectAccess(account, testPlan.projectId);
            for (Integer testCaseId : testCaseIds) {
                Task testCase = dao.getExistedTaskByIdObjectType(testCaseId, Task.OBJECTTYPE_测试用例);
                bizService.addTestPlanTestCase(account, testPlan, testCase);
            }
        }

        @Override
        public TableData getTableDataFromExcel(String token, String fileUuid) {
            Account account = bizService.getExistedAccountByToken(token);
            return getTableDataFromExcel0(token, account, fileUuid);
        }

        private TableData getTableDataFromExcel0(String token, Account account, String fileUuid) {
            Attachment attachment = dao.getAttachmentByUuid(fileUuid);
            if (attachment == null) {
                throw new AppException("文件不存在");
            }
            bizService.checkPermission(account, attachment.companyId);
            java.io.File f = FileServiceManager.get().download(fileUuid);
            TableData data = null;
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
                data = PoiUtils.readExcel(fis);
                if (data.headers == null || data.headers.size() == 0) {
                    throw new AppException("excel数据不能为空");
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new AppException("EXCEL文件解析失败");
            } finally {
                IOUtil.closeQuietly(fis);
            }
            return data;
        }

        @Transaction
        @Override
        public void deleteTestPlanTestCase(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            TestPlanTestCase old = dao.getExistedById(TestPlanTestCase.class, id);
            bizService.checkPermissionForProjectAccess(account, old.projectId);
            dao.deleteById(TestPlanTestCase.class, id);
        }

        @Transaction
        @Override
        public void importTaskFromExcel(String token, int projectId, int objectType,
                                        List<Integer> ids, String fileUuid, Boolean ignoreError) {
            long startTime = System.currentTimeMillis();
            Account account = bizService.getExistedAccountByToken(token);
            Project project = dao.getExistedById(Project.class, projectId);
            ObjectType oType = dao.getExistedById(ObjectType.class, objectType);
            bizService.checkProjectPermission(account, project.id, Permission.ID_编辑TASK + objectType);
            int importContentPos = -1;//详情位置  -1表示不导入
            for (int i = 0; i < ids.size(); i++) {
                if (ids.get(i) == null) {
                    continue;
                }
                if (ids.get(i) == -1) {//id=-1表示是详情
                    importContentPos = i;
                    break;
                }
            }
            //
            TableData data = getTableDataFromExcel0(token, account, fileUuid);
            int[] idList = BizUtil.convertList(ids);
            ProjectFieldDefineQuery query = new ProjectFieldDefineQuery();
            query.idInList = idList;
            query.sortWeightSort = ProjectFieldDefineQuery.SORT_TYPE_ASC;
            List<ProjectFieldDefine> fieldDefines = dao.getList(query);
            List<ProjectFieldDefine> list = new ArrayList<>();
            int index = 1;
            int columnCount = 0;
            for (Integer id : ids) {
                if (id == null || id <= 0) {
                    list.add(null);
                } else {
                    boolean found = false;
                    for (ProjectFieldDefine e : fieldDefines) {
                        if (e.id == id) {
                            list.add(e);
                            found = true;
                            columnCount++;
                            break;
                        }
                    }
                    if (!found) {
                        throw new AppException("第" + index + "列字段不存在");
                    }
                }
                index++;
            }
            if (columnCount == 0) {
                throw new AppException("请选择导入列");
            }
            int line = 1;
            //预加载状态流、优先级、项目成员等信息
            List<ProjectStatusDefine> statusDefines = dao.getProjectStatusDefineList(projectId, objectType);
            List<ProjectPriorityDefineInfo> priorityDefines = dao.getProjectPriorityDefineInfoList(projectId, objectType);
            List<ProjectMemberInfo> memberInfos = dao.getProjectMemberInfoList(projectId);
            List<ProjectIteration> iterations = dao.getProjectIterationList(projectId);
            List<ProjectRelease> releases = dao.getProjectReleaseList(projectId);
            List<ProjectSubSystem> subSystems = dao.getProjectSubSystemList(projectId);
            List<Account> companyAccounts = dao.getCompanyAccountList(account.companyId);
            List<Category> companyCategories = dao.getCompanyCategoryList(account.companyId, projectId, objectType);
            logger.info("import init data --------> cost:{}ms", (System.currentTimeMillis() - startTime));
            long middleTime = System.currentTimeMillis();
            for (List<String> content : data.contents) {
                long singleStartTime = System.currentTimeMillis();
                TaskDetailInfo bean = makeTaskByImport(account.companyId, projectId,
                        objectType, content, list, importContentPos, line, ignoreError,
                        statusDefines, priorityDefines, memberInfos, iterations, releases,
                        subSystems, companyAccounts, companyCategories);
                logger.info("make task cost----> cost:{}", (System.currentTimeMillis() - singleStartTime));
                if (bean == null) {
                    continue;
                }
                try {
                    bean.companyId = account.companyId;
                    bean.projectId = projectId;
                    bean.objectType = objectType;
                    bean.projectName = project.name;
                    bean.objectTypeName = oType.name;
                    createTask0(account, bean, true);
                    logger.info("create task cost----> cost:{}", (System.currentTimeMillis() - singleStartTime));
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    logger.error("content:{}", DumpUtil.dump(content));
                    throw new AppException("第" + line + "行数据错误," + e.getMessage());
                }
                line++;
            }
            logger.info("import create end --------> cost:{}ms", (System.currentTimeMillis() - middleTime));
            //
            if (CornerstoneBizSystem.webEventServer != null) {
                AutoTranscationCallback.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        logger.info("afterCommit");
                        WebEvent event = WebEvent.createWebEvent(account.id, account.companyId, WebEvent.TYPE_导入任务,
                                objectType, projectId, 0);
                        CornerstoneBizSystem.webEventServer.boardcastAsync(event);
                    }
                });
            }
        }

        private boolean isEmpty(List<String> content) {
            if (content == null || content.size() == 0) {
                return true;
            }
            for (String e : content) {
                if (!StringUtil.isEmptyWithTrim(e)) {
                    return false;
                }
            }
            return true;
        }


        /**
         * 导入时用到
         *
         * @param importContentPos 从0开始算
         */
        private TaskDetailInfo makeTask(String from, int companyId, int projectId, int objectType,
                                        List<String> content, List<ProjectFieldDefine> fieldDefines,
                                        int importContentPos, int line, Boolean ignoreError) {
            if (logger.isDebugEnabled()) {
                logger.debug("makeTask start from:{} line:{} companyId:{} projectId:{} "
                                + "objectType:{} content:{} fieldDefines:{} importContentPos;{} ignoreError:{}",
                        from, line, companyId, projectId, objectType,
                        DumpUtil.dump(content),
                        DumpUtil.dump(fieldDefines),
                        importContentPos, ignoreError);
            }
            if (content == null || fieldDefines == null) {
                throw new AppException("数据错误");
            }
            if (isEmpty(content)) {
                return null;
            }
            TaskDetailInfo bean = new TaskDetailInfo();
            bean.projectId = projectId;
            bean.objectType = objectType;
            bean.customFields = new LinkedHashMap<>();
            int i = 0;
            for (ProjectFieldDefine e : fieldDefines) {
                if (content.size() < i + 1) {
                    continue;
                }
                if (i == importContentPos) {//详情
                    //把换行变为<br/>缺陷 #18922
                    bean.content = content.get(i++);
                    if (bean.content != null) {
                        bean.content = bean.content.replaceAll("\n", "<br/>");
                    }
                    continue;
                }
                if (e == null) {
                    i++;
                    continue;
                }
                String value = content.get(i++);
                if (StringUtil.isEmpty(value)) {
                    continue;
                }
                try {
                    if (e.isSystemField) {
                        if ("name".equals(e.field)) {
                            bean.name = value;
                        } else if ("statusName".equals(e.field)) {
                            ProjectStatusDefine statusDefine = dao.getProjectStatusDefine(projectId, objectType, value, ignoreError);
                            if (statusDefine != null) {
                                bean.status = statusDefine.id;
                            }
                        } else if ("priorityName".equals(e.field)) {
                            ProjectPriorityDefine priorityDefine = dao.getProjectPriorityDefine(projectId, objectType, value, ignoreError);
                            if (priorityDefine != null) {
                                bean.priority = priorityDefine.id;
                            }
                        } else if ("ownerAccountName".equals(e.field)) {//责任人可以多个,逗号分隔
                            List<Integer> ownerIdList = new ArrayList<>();
                            value = value.replaceAll("，", ",");
                            value = value.replaceAll(";", ",");
                            value = value.replaceAll("；", ",");
                            String[] accountNames = value.split(",");
                            for (String accountName : accountNames) {
//                                CompanyMemberInfo member = dao.getCompanyMemberInfoByCompanyIdAccountName(companyId, accountName);
                                Integer memberAccountId = dao.getProjectMemberAccountIdByProjectIdAccountName(projectId, accountName);
                                if (memberAccountId == null) {
                                    continue;
                                }
                                ownerIdList.add(memberAccountId);
                            }
                            bean.ownerAccountIdList = ownerIdList;
                        } else if ("createAccountName".equals(e.field)) {//创建人
                            Integer createAccountId = dao.getProjectMemberAccountIdByProjectIdAccountName(projectId, value);
                            if (createAccountId != null) {
                                bean.createAccountId = createAccountId;
                            }
                        } else if ("categoryIdList".equals(e.field)) {
                            List<Integer> categoryIdList = bizService.getCategoryIdList(companyId, value);
                            bean.categoryIdList = new ArrayList<>();
                            bean.categoryIdList.addAll(categoryIdList);
                        } else if ("startDate".equals(e.field)) {
                            bean.startDate = DateUtil.parseFromExcel(value);
                        } else if ("endDate".equals(e.field)) {
                            bean.endDate = DateUtil.parseFromExcel(value);
                        } else if ("expectWorkTime".equals(e.field)) {
                            bean.expectWorkTime = Integer.parseInt(value);
                        } else if ("iterationName".equals(e.field)) {//迭代
                            ProjectIteration iteration = dao.getExistedProjectIterationByProjectIdName(projectId, value);
                            bean.iterationId = iteration.id;
                        } else if ("releaseName".equals(e.field)) {//Release
                            ProjectRelease release = dao.getExistedProjectReleaseByProjectIdName(projectId, value);
                            bean.releaseId = release.id;
                        } else if ("subSystemName".equals(e.field)) {//子系统
                            ProjectSubSystem subSystem = dao.getExistedProjectSubSystemByProjectIdName(projectId, value);
                            bean.subSystemId = subSystem.id;
                        } else if ("progress".equals(e.field)) {//子系统
                            bean.progress = Integer.parseInt(value);
                            if (bean.progress < 0) {
                                throw new AppException("进度不能为负数");
                            }
                        }
                    } else {//自定义字段
                        String key = BizUtil.getCustomerFieldKey(e.id);
                        ProjectFieldDefine fieldDefine = dao.getExistedById(ProjectFieldDefine.class, e.id);
                        if (fieldDefine.type == ProjectFieldDefine.TYPE_复选框) {
                            bean.customFields.put(key, (BizUtil.splitStringReturnSet(value)));
                        } else if (fieldDefine.type == ProjectFieldDefine.TYPE_团队成员选择) {
                            List<Integer> accountIds = bizService.getAccountIdByAccoountNames(companyId, value);
                            bean.customFields.put(key, accountIds);
                        } else if (fieldDefine.type == ProjectFieldDefine.TYPE_日期) {
                            try {
                                if (fieldDefine.showTimeField) {
                                    Date date = DateUtil.parseDateTimeFromExcel(value);
                                    if (null != date) {
                                        bean.customFields.put(key, date.getTime());
                                    }
                                } else {
                                    Date date = DateUtil.parseFromExcel(value);
                                    if (null != date) {
                                        bean.customFields.put(key, date.getTime());
                                    }
                                }
                            } catch (Exception ex) {
                                logger.error(ex.getMessage(), ex);
                                throw new AppException("日期格式错误");
                            }
                        } else {
                            bean.customFields.put(key, value);
                        }
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                    if (ignoreError == null || (!ignoreError)) {
                        throw new AppException(ex.getMessage());
                    }
                }
            }
            //
            if (logger.isDebugEnabled()) {
                logger.debug("makeTask end bean:{}", DumpUtil.dump(bean));
            }
            //
            return bean;
        }


        /**
         * 导入优化任务创建
         */
        private TaskDetailInfo makeTaskByImport(int companyId, int projectId, int objectType,
                                                List<String> content, List<ProjectFieldDefine> fieldDefines,
                                                int importContentPos, int line, Boolean ignoreError,
                                                List<ProjectStatusDefine> statusDefines, List<ProjectPriorityDefineInfo> priorityDefines,
                                                List<ProjectMemberInfo> memberInfos, List<ProjectIteration> iterations,
                                                List<ProjectRelease> releases, List<ProjectSubSystem> subSystems,
                                                List<Account> companyAccounts, List<Category> companyCategories) {
            if (logger.isDebugEnabled()) {
                logger.debug("makeTask start line:{} companyId:{} projectId:{} "
                                + "objectType:{} content:{} fieldDefines:{} importContentPos;{} ignoreError:{}",
                        line, companyId, projectId, objectType,
                        DumpUtil.dump(content),
                        DumpUtil.dump(fieldDefines),
                        importContentPos, ignoreError);
            }
            if (content == null || fieldDefines == null) {
                throw new AppException("数据错误");
            }
            if (isEmpty(content)) {
                return null;
            }
            TaskDetailInfo bean = new TaskDetailInfo();
            bean.projectId = projectId;
            bean.objectType = objectType;
            bean.customFields = new LinkedHashMap<>();
            int i = 0;
            for (ProjectFieldDefine fieldDefine : fieldDefines) {
                if (content.size() < i + 1) {
                    continue;
                }
                if (i == importContentPos) {//详情
                    //把换行变为<br/>缺陷 #18922
                    bean.content = content.get(i++);
                    if (bean.content != null) {
                        bean.content = bean.content.replaceAll("\n", "<br/>");
                    }
                    continue;
                }
                if (fieldDefine == null) {
                    i++;
                    continue;
                }
                String value = content.get(i++);
                if (StringUtil.isEmpty(value)) {
                    continue;
                }
                try {
                    if (fieldDefine.isSystemField) {
                        switch (fieldDefine.field) {
                            case "name":
                                bean.name = value;
                                break;
                            case "statusName":
                                if (!BizUtil.isNullOrEmpty(statusDefines)) {
                                    ProjectStatusDefine statusDefine = BizUtil.filter(statusDefines, "name", value);
                                    if (statusDefine != null) {
                                        bean.status = statusDefine.id;
                                        bean.statusName = value;
                                        if (statusDefine.type == ProjectStatusDefine.TYPE_结束状态) {
                                            bean.isFinish = true;
                                            bean.finishTime = new Date();
                                        }
                                    } else {
                                        throw new AppException("状态字不在范围内【" + value + "】,请先在【项目设置->工作流】里添加状态");
                                    }
                                }
                                break;
                            case "priorityName":
                                if (!BizUtil.isNullOrEmpty(priorityDefines)) {
                                    ProjectPriorityDefine priorityDefine = BizUtil.filter(priorityDefines, "name", value);
                                    if (priorityDefine != null) {
                                        bean.priority = priorityDefine.id;
                                    } else {
                                        throw new AppException("优先级错误【" + value + "】");
                                    }
                                }
                                break;
                            case "ownerAccountName": //责任人可以多个,逗号分隔
                                List<Integer> ownerIdList = new ArrayList<>();
                                List<AccountSimpleInfo> accountSimpleInfos = new ArrayList<>();
                                value = value.replaceAll("，", ",");
                                value = value.replaceAll(";", ",");
                                value = value.replaceAll("；", ",");
                                String[] accountNames = value.split(",");
                                for (String accountName : accountNames) {
                                    if (!BizUtil.isNullOrEmpty(memberInfos)) {
                                        ProjectMemberInfo memberInfo = BizUtil.filter(memberInfos, "accountName", accountName);
                                        if (null != memberInfo) {
                                            ownerIdList.add(memberInfo.accountId);
                                            accountSimpleInfos.add(bizService.createAccountSimpleInfo(memberInfo));
                                        }
                                    }
                                }
                                bean.ownerAccountIdList = ownerIdList;
                                bean.ownerAccountList = accountSimpleInfos;
                                bean.firstOwner = ownerIdList;
                                bean.lastOwner = ownerIdList;
                                break;
                            case "createAccountName": //创建人
                                if (!BizUtil.isNullOrEmpty(memberInfos)) {
                                    ProjectMemberInfo memberInfo = BizUtil.filter(memberInfos, "accountName", value);
                                    if (null != memberInfo) {
                                        bean.createAccountId = memberInfo.accountId;
                                    }
                                }
                                break;
                            case "categoryIdList":
                                bean.categoryIdList = new ArrayList<>();
                                if (!StringUtil.isEmpty(value)) {
                                    value = value.replaceAll("，", ",");
                                    value = value.replaceAll(";", ",");
                                    value = value.replaceAll("；", ",");
                                    String[] nameList = value.split(",");
                                    for (String name : nameList) {
                                        if (name == null) {
                                            continue;
                                        }
                                        Category category = BizUtil.filter(companyCategories, "name", name);
                                        if (null != category) {
                                            bean.categoryIdList.add(category.id);
                                        }
                                    }
                                }
//                            List<Integer> categoryIdList = bizService.getCategoryIdList(companyId, value);
                                break;
                            case "startDate":
                                bean.startDate = DateUtil.parseFromExcel(value);
                                break;
                            case "endDate":
                                bean.endDate = DateUtil.parseFromExcel(value);
                                break;
                            case "expectWorkTime":
                                bean.expectWorkTime = Integer.parseInt(value);
                                break;
                            case "iterationName": //迭代
                                if (!BizUtil.isNullOrEmpty(iterations)) {
                                    ProjectIteration iteration = BizUtil.filter(iterations, "name", value);
                                    if (null != iteration) {
                                        bean.iterationId = iteration.id;
                                    } else {
                                        throw new AppException("找不到名为【" + value + "】的迭代");
                                    }
                                }
                                break;
                            case "releaseName": //Release
                                if (!BizUtil.isNullOrEmpty(releases)) {
                                    ProjectRelease release = BizUtil.filter(releases, "name", value);
                                    if (null != release) {
                                        bean.releaseId = release.id;
                                    } else {
                                        throw new AppException("找不到名为【" + value + "】的Release");
                                    }
                                }
                                break;
                            case "subSystemName": //子系统
                                if (!BizUtil.isNullOrEmpty(subSystems)) {
                                    ProjectSubSystem subSystem = BizUtil.filter(subSystems, "name", value);
                                    if (null != subSystem) {
                                        bean.subSystemId = subSystem.id;
                                    } else {
                                        throw new AppException("找不到名为【" + value + "】的子系统");
                                    }
                                }
                                break;
                            case "progress": //进度
                                bean.progress = Integer.parseInt(value);
                                if (bean.progress < 0) {
                                    throw new AppException("进度不能为负数");
                                }
                                break;
                            default:
                                break;
                        }
                    } else {//自定义字段
                        String key = BizUtil.getCustomerFieldKey(fieldDefine.id);
                        if (fieldDefine.type == ProjectFieldDefine.TYPE_复选框) {
                            bean.customFields.put(key, (BizUtil.splitStringReturnSet(value)));
                        } else if (fieldDefine.type == ProjectFieldDefine.TYPE_团队成员选择) {
                            List<Integer> list = new ArrayList<>();
                            if (!StringUtil.isEmpty(value)) {
                                value = value.replaceAll("，", ",");
                                value = value.replaceAll(";", ",");
                                value = value.replaceAll("；", ",");
                                String[] accountNameList = value.split(",");
                                for (String name : accountNameList) {
                                    if (!StringUtil.isEmptyWithTrim(name)) {
                                        Account account = BizUtil.filter(companyAccounts, "name", name);
                                        if (null != account) {
                                            list.add(account.id);
                                        }
                                    }

                                }
                            }
//                            List<Integer> accountIds = bizService.getAccountIdByAccoountNames(companyId, value);
                            bean.customFields.put(key, list);
                        } else if (fieldDefine.type == ProjectFieldDefine.TYPE_日期) {
                            try {
                                if (fieldDefine.showTimeField) {
                                    Date date = DateUtil.parseDateTimeFromExcel(value);
                                    if (null != date) {
                                        bean.customFields.put(key, date.getTime());
                                    }
                                } else {
                                    Date date = DateUtil.parseFromExcel(value);
                                    if (null != date) {
                                        bean.customFields.put(key, date.getTime());
                                    }
                                }
                            } catch (Exception ex) {
                                logger.error(ex.getMessage(), ex);
                                throw new AppException("日期格式错误");
                            }
                        } else {
                            bean.customFields.put(key, value);
                        }
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                    if (ignoreError == null || (!ignoreError)) {
                        throw new AppException(ex.getMessage());
                    }
                }
            }
            //
            if (logger.isDebugEnabled()) {
                logger.debug("makeTask end bean:{}", DumpUtil.dump(bean));
            }
            //
            return bean;
        }

        @SuppressWarnings("unchecked")
        @Override
        public ExportData exportTaskListToExcel(String token, TaskQuery query, List<Integer> fieldDefineIds) {
            Account account = bizService.getExistedAccountByToken(token);
            boolean exportContent = false;//导出详情
            for (Integer id : fieldDefineIds) {
                if (id == -1) {
                    exportContent = true;
                    query = BeanUtil.copyTo(TaskDetailInfoQuery.class, query);//查详情query
                    break;
                }
            }
            //boss项目集可以穿透
            if (query.objectType == Task.OBJECTTYPE_项目清单) {
                try {
                    bizService.checkCompanyPermission(account, Permission.ID_开启项目集管理模块);
                    Set<Integer> projectIdSet = bizService.getAccountAccessProjectList(account, true, false);
                    if (!BizUtil.isNullOrEmpty(projectIdSet)) {
                        query.associateProjectIdInList = BizUtil.convertList(projectIdSet);
                    }
                } catch (Exception e) {
                    logger.info("cant access sub department project" + e.getMessage());
                }
            }
            query.companyId = account.companyId;
            query.pageIndex = 1;
            query.pageSize = Integer.MAX_VALUE;
            String fileName = "";
            if (query.projectId != null) {
                Project project = dao.getExistedById(Project.class, query.projectId);
                bizService.checkPermission(account, project.companyId);
                fileName = project.name;
            }
            if (query.objectType != null) {
                ObjectType objectType = dao.getExistedById(ObjectType.class, query.objectType);
                if (fileName.isEmpty()) {
                    fileName = objectType.name;
                } else {
                    fileName = fileName + "-" + objectType.name;
                }
            }
            int[] ids = BizUtil.convertList(fieldDefineIds);
            if (BizUtil.isNullOrEmpty(ids)) {
                throw new AppException("请选择导出列");
            }
            ProjectFieldDefineQuery fieldDefineQuery = new ProjectFieldDefineQuery();
            fieldDefineQuery.idInList = ids;
            fieldDefineQuery.pageSize = Integer.MAX_VALUE;
            fieldDefineQuery.orderType = null;
            List<ProjectFieldDefineInfo> fieldDefines = dao.getList(fieldDefineQuery);
            List<ProjectFieldDefineInfo> sortedFieldDefines = new ArrayList<>();
            CategoryQuery categoryQuery = new CategoryQuery();
            setupQuery(account, categoryQuery);
            categoryQuery.pageSize = Integer.MAX_VALUE;
            categoryQuery.projectId = query.projectId;
            List<Category> categories = dao.getList(categoryQuery);
            Map<Integer, String> categoriesMap = new HashMap<>();
            for (Category e : categories) {
                categoriesMap.put(e.id, e.name);
            }
            Map<String, ProjectFieldDefineInfo> fieldDefineMap = new HashMap<>();
            for (Integer id : fieldDefineIds) {
                ProjectFieldDefineInfo e = BizUtil.getByTarget(fieldDefines, "id", id);
                if (e != null) {
                    fieldDefineMap.put(BizUtil.getCustomerFieldKey(e.id), e);
                    sortedFieldDefines.add(e);
                }
            }
            Map<ProjectFieldDefineInfo, TaskField> map = getTaskFieldsMap(sortedFieldDefines);
            List<String> headers = new ArrayList<>();
            List<Integer> columnWidth = new ArrayList<>();
            headers.add("ID");
            columnWidth.add(getColumnWidth("ID"));
            for (ProjectFieldDefineInfo fieldDefine : map.keySet()) {
                headers.add(fieldDefine.name);
                columnWidth.add(getColumnWidth(fieldDefine.field));
            }
            if (exportContent) {
                headers.add("详细描述");
                columnWidth.add(getColumnWidth("content"));
            }
            Map<String, Object> result = bizService.getTaskInfoList0(account, query, null, false);
            List<TaskInfo> taskInfos = (List<TaskInfo>) result.get("list");
            TableData data = new TableData();
            List<List<String>> contentList = new ArrayList<>();
            for (TaskInfo e : taskInfos) {
                List<String> content = new ArrayList<>();
                content.add(e.serialNo + "");
                for (TaskField tf : map.values()) {
                    Object value = null;
                    if (tf.field != null) {
                        try {
                            value = tf.field.get(e);
                            if (tf.field.getType().equals(Date.class)) {//日期导出不需要时分秒
                                if (value != null) {
                                    value = DateUtil.formatDate((Date) value, "yyyy-MM-dd");
                                }
                            }
                            if ("ownerAccountList".equals(tf.field.getName())) {
                                List<AccountSimpleInfo> accountSimpleInfos = (List<AccountSimpleInfo>) value;
                                value = BizUtil.appendFields(accountSimpleInfos, "name");
                            }
                            if ("categoryIdList".equals(tf.field.getName())) {
                                List<Integer> categoryIdList = (List<Integer>) value;
                                if (categoryIdList != null) {
                                    StringBuilder category = new StringBuilder();
                                    for (Integer id : categoryIdList) {
                                        String name = categoriesMap.get(id);
                                        if (name != null) {
                                            category.append(name).append(",");
                                        }
                                    }
                                    if (category.length() > 0) {
                                        category.deleteCharAt(category.length() - 1);
                                        value = category.toString();
                                    } else {
                                        value = "";
                                    }
                                } else {
                                    value = "";
                                }
                            }
                        } catch (Exception e1) {
                            logger.error(e1.getMessage(), e1);
                        }
                    } else if (!StringUtil.isEmpty(tf.customerFieldKey)) {//自定义字段
                        if (e.customFields != null) {
                            ProjectFieldDefineInfo define = fieldDefineMap.get(tf.customerFieldKey);
                            if (define.type == ProjectFieldDefineInfo.TYPE_团队成员选择) {
                                JSONArray newObjectValue = (JSONArray) e.customFields.get(BizUtil.getCustomerFieldKeyForObject(define.id));
                                value = BizUtil.getAccountNamesFromJsonArray(newObjectValue);
                            } else if (define.type == ProjectFieldDefineInfo.TYPE_复选框) {
                                JSONArray newObjectValue = (JSONArray) e.customFields.get(BizUtil.getCustomerFieldKey(define.id));
                                value = BizUtil.getCheckBoxValues(newObjectValue);
                            } else if (define.type == ProjectFieldDefineInfo.TYPE_日期) {
                                try {
                                    Long lValue = (Long) e.customFields.get(BizUtil.getCustomerFieldKey(define.id));
                                    if (define.showTimeField) {
                                        value = BizUtil.getCustomeDateTimeString(lValue);
                                    } else {
                                        value = BizUtil.getCustomeDateString(lValue);
                                    }

                                } catch (Exception ex) {
                                    value = "";
                                }
                            } else {
                                value = e.customFields.get(tf.customerFieldKey);
                            }
                        }
                    }
                    if (value == null) {
                        content.add("");
                    } else {
                        content.add(value.toString());
                    }
                }
                if (exportContent) {//导出详情
                    String detailContent = ((TaskDetailInfo) e).content;
                    if (detailContent != null) {
                        detailContent = BizUtil.cleanHtml(detailContent);
                    } else {
                        detailContent = "";
                    }
                    if (detailContent.length() >= 32767) {//he maximum length of cell contents (text) is 32,767 characters
                        detailContent = detailContent.substring(0, 32767);
                    }
                    content.add(detailContent);
                }
                contentList.add(content);
            }
            data.headers = new ArrayList<>(headers);
            data.columnWidth = columnWidth;
            data.contents = contentList;
            //
            if (fileName.isEmpty()) {
                fileName = "列表";
            }
            //
            ExportData exportData = new ExportData();
            exportData.tableData = data;
            exportData.fileName = fileName + ".xlsx";
            //
            return exportData;
        }

        private int getColumnWidth(String name) {
            if (name == null) {
                return 0;
            }
            if ("ID".equals(name)) {
                return 60;
            }
            if ("name".equals(name)) {
                return 200;
            }
            if ("statusName".equals(name)) {
                return 80;
            }
            if ("priorityName".equals(name)) {
                return 40;
            }
            if ("ownerAccountName".equals(name)) {
                return 80;
            }
            if ("createAccountName".equals(name)) {
                return 60;
            }
            if ("startDate".equals(name)) {
                return 50;
            }
            if ("endDate".equals(name)) {
                return 50;
            }
            if ("startDays".equals(name) || "endDays".equals(name)) {
                return 50;
            }
            if ("workTime".equals(name) || "expectWorkTime".equals(name)) {
                return 50;
            }
            if ("categoryIdList".equals(name)) {
                return 100;
            }
            return 80;
        }


        private Map<ProjectFieldDefineInfo, TaskField> getTaskFieldsMap(List<ProjectFieldDefineInfo> fieldDefines) {
            Map<ProjectFieldDefineInfo, TaskField> map = new LinkedHashMap<>();
            for (ProjectFieldDefineInfo e : fieldDefines) {
                TaskField tf = new TaskField();
                Field f = null;
                if (e.isSystemField) {
                    if ("ownerAccountName".equals(e.field)) {
                        try {
                            f = TaskInfo.class.getField("ownerAccountList");
                        } catch (Exception e2) {
                            logger.error(e2.getMessage(), e2);
                            throw new AppException("字段不存在" + e.field);
                        }
                    } else {
                        try {
                            f = TaskInfo.class.getField(e.field);
                        } catch (Exception e2) {
                            logger.error(e2.getMessage(), e2);
                            throw new AppException("字段不存在" + e.field);
                        }
                        if (f == null) {
                            throw new AppException("字段不存在" + e.field);
                        }
                    }
                    tf.field = f;
                } else {//自定义字段
                    tf.customerFieldKey = BizUtil.getCustomerFieldKey(e.id);
                }
                map.put(e, tf);
            }
            return map;
        }

        @Override
        public TableData downloadImportTaskExcelTemplate(String token, int projectId, int objectType) {
            ProjectFieldDefineQuery query = new ProjectFieldDefineQuery();
            query.projectId = projectId;
            query.objectType = objectType;
            query.sortWeightSort = Query.SORT_TYPE_ASC;
            List<ProjectFieldDefine> list = dao.getList(query);
            TableData data = new TableData();
            List<String> headers = new ArrayList<>();
            for (ProjectFieldDefine e : list) {
                headers.add(e.name);
            }
            data.headers = headers;
            logger.info("downloadImportTaskExcelTemplate:{}", DumpUtil.dump(data));
            return data;
        }

        @Transaction
        @Override
        public void batchRunTestPlanTestCase(String token, List<Integer> idList, int status) {
            Account account = bizService.getExistedAccountByToken(token);
            for (Integer id : idList) {
                runTestPlanTestCase0(account, id, status, "");
            }
        }

        @Transaction
        @Override
        public void runTestPlanTestCase(String token, int id, int status, String remark) {
            Account account = bizService.getExistedAccountByToken(token);
            runTestPlanTestCase0(account, id, status, remark);
        }

        private void runTestPlanTestCase0(Account account, int id, int status, String remark) {
            TestPlanTestCase old = dao.getExistedByIdForUpdate(TestPlanTestCase.class, id);
            bizService.checkPermissionForProjectAccess(account, old.projectId);
            checkDataDictValueValid("TestPlanTestCase.status", status, "状态错误");
            if (status == TestPlanTestCase.STATUS_通过) {
                passTestPlanTestCase(account, old);
            }
            if (status == TestPlanTestCase.STATUS_不通过 || status == TestPlanTestCase.STATUS_阻塞) {
                failTestPlanTestCase(account, old, status);
            }
            //
//			Task testPlan=dao.getExistedTaskByIdObjectTypeForUpdate(old.testPlanId,Task.OBJECTTYPE_测试计划);
//			if(testPlan.status==TestPlan.STATUS_未开始) {//TODO
//				testPlan.status=TestPlan.STATUS_正在测试;
//			}
//			updateTestPlanCount(testPlan);

            //
            TestPlanTestCaseRunLog log = new TestPlanTestCaseRunLog();
            log.companyId = old.companyId;
            log.testPlanTestCaseId = id;
            log.testPlanId = old.testPlanId;
            log.testCaseId = old.testCaseId;
            log.status = status;
            log.remark = remark;
            log.createAccountId = account.id;
            dao.add(log);
        }

        //
        private void passTestPlanTestCase(Account account, TestPlanTestCase old) {
            old.status = TestPlanTestCase.STATUS_通过;
            old.passTime = new Date();
            old.excuteTime = new Date();
            old.updateAccountId = account.id;
            old.runCount++;
            old.lastRunTime = new Date();
            old.lastRunAccountId = account.id;
            dao.update(old);
        }

        //
        private void failTestPlanTestCase(Account account, TestPlanTestCase old, int status) {
            old.status = status;
            old.failTime = new Date();
            old.excuteTime = new Date();
            old.updateAccountId = account.id;
            old.runCount++;
            old.lastRunTime = new Date();
            old.lastRunAccountId = account.id;
            dao.update(old);
        }

        @Override
        public RemindInfo getRemindInfoById(String token, int id) {
            return dao.getExistedById(RemindInfo.class, id);
        }

        @Transaction
        @Override
        public int addRemind(String token, Remind bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bean.companyId = account.companyId;
            bean.createAccountId = account.id;
            BizUtil.checkValid(bean);
            Date today = DateUtil.getToday();
            if (bean.remindTime.before(today)) {
                throw new AppException("提醒时间不能早于今天");
            }
            dao.add(bean);
            return bean.id;
        }

        @Transaction
        @Override
        public void updateRemind(String token, Remind bean) {
            Account account = bizService.getExistedAccountByToken(token);
            updateRemind0(account, bean);
        }

        private void updateRemind0(Account account, Remind bean) {
            Remind old = dao.getExistedByIdForUpdate(Remind.class, bean.id);
            if (account.id != old.createAccountId) {
                throw new AppException("权限不足");
            }
            old.name = bean.name;
            old.remindTime = bean.remindTime;
            old.repeat = bean.repeat;
            old.remark = bean.remark;
            dao.update(old);
        }

        @Transaction
        @Override
        public void updateRemindTime(String token, int id, Date remindTime) {
            Account account = bizService.getExistedAccountByToken(token);
            Remind old = dao.getExistedByIdForUpdate(Remind.class, id);
            old.remindTime = remindTime;
            updateRemind0(account, old);
        }

        @Transaction
        @Override
        public void deleteRemind(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            Remind old = dao.getExistedByIdForUpdate(Remind.class, id);
            if (account.id != old.createAccountId) {
                throw new AppException("权限不足");
            }
            dao.deleteById(Remind.class, id);
        }

        /**
         * 注意：bug跟用例的关联是  bug关联用例 在t_task_associated表 taskId是bug的id,associated_task_id是测试用例id
         */
        @Override
        public List<TaskInfo> getBugListFromTestCaseId(String token, int testCaseId) {
            Account account = bizService.getExistedAccountByToken(token);
            Task testCase = dao.getExistedTaskByIdObjectType(testCaseId, Task.OBJECTTYPE_测试用例);
            bizService.checkPermissionForProjectAccess(account, testCase.projectId);
            TaskQuery query = new TaskQuery();
            query.associatedTaskId = testCaseId;
            query.pageSize = Integer.MAX_VALUE;
            return dao.getList(query);
        }

        @Override
        public List<TestPlanTestCaseRunLogInfo> getTestPlanTestCaseRunLogInfoList(String token, int testPlanTestCaseId) {
            Account account = bizService.getExistedAccountByToken(token);
            TestPlanTestCase bean = dao.getExistedById(TestPlanTestCase.class, testPlanTestCaseId);
            bizService.checkPermissionForProjectAccess(account, bean.projectId);
            //
            TestPlanTestCaseRunLogQuery query = new TestPlanTestCaseRunLogQuery();
            query.testPlanTestCaseId = testPlanTestCaseId;
            query.pageSize = 5000;
            return dao.getList(query);
        }

        @Override
        public Map<String, Object> getTestPlanTestCaseRunLogList(String token, TestPlanTestCaseRunLogQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            setupQuery(account, query);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        @Transaction
        @Override
        public int createTaskImportTemplate(String token, TaskImportTemplate bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkPermissionForProjectAccess(account, bean.projectId);
            if (bean.fields == null || bean.fields.isEmpty()) {
                throw new AppException("字段不能为空");
            }
            bean.companyId = account.companyId;
            bean.createAccountId = account.id;
            BizUtil.checkValid(bean);
            dao.add(bean);
            return bean.id;
        }

        @Transaction
        @Override
        public void updateTaskImportTemplate(String token, TaskImportTemplate bean) {
            Account account = bizService.getExistedAccountByToken(token);
            TaskImportTemplate old = dao.getExistedByIdForUpdate(TaskImportTemplate.class, bean.id);
            bizService.checkPermissionForProjectAccess(account, old.projectId);
            if (bean.fields == null || bean.fields.isEmpty()) {
                throw new AppException("字段不能为空");
            }
            old.name = bean.name;
            old.fields = bean.fields;
            old.remark = bean.remark;
            BizUtil.checkValid(bean);
            dao.update(old);
        }

        @Transaction
        @Override
        public void deleteTaskImportTemplate(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            TaskImportTemplate old = dao.getExistedByIdForUpdate(TaskImportTemplate.class, id);
            bizService.checkPermissionForProjectAccess(account, old.projectId);
            dao.deleteById(TaskImportTemplate.class, id);
        }

        @Override
        public TaskImportTemplateInfo getTaskImportTemplateById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            TaskImportTemplateInfo old = dao.getExistedById(TaskImportTemplateInfo.class, id);
            bizService.checkPermissionForProjectAccess(account, old.projectId);
            return old;
        }

        @Override
        public Map<String, Object> getTaskImportTemplateList(String token, TaskImportTemplateQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkPermissionForProjectAccess(account, query.projectId);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        @Override
        public WikiInfo getWikiInfoById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            WikiInfo info = dao.getExistedById(WikiInfo.class, id);
            bizService.checkPermissionForProjectAccess(account, info.projectId);
            return info;
        }

        @Override
        public WikiPageDetailInfo getWikiPageDetailInfoByUuid(String token, String uuid) {
            Account account = bizService.getExistedAccountByToken(token);
            WikiPageDetailInfo info = dao.getWikiPageDetailInfoByUuid(uuid);
            Set<String> permissions = bizService.getAllGlobalPermission(account);
            // 判断用户是否有企业wiki列表权限
            if (!permissions.contains(Permission.ID_COMPANY_WIKI_LIST)) {
                bizService.checkPermissionForProjectAccess(account, info.projectId);
            }
            return info;
        }

        @Override
        public List<WikiInfo> getWikiInfoList(String token, WikiQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            setupQuery(account, query);
            query.pageSize = Integer.MAX_VALUE;
            //
            List<WikiInfo> list = dao.getList(query);
            List<WikiInfo> ret = new ArrayList<>();
            for (WikiInfo wiki : list) {
                if (bizService.checkAccountProjectPermission(account.id, wiki.projectId, wiki.enableRole,
                        wiki.createAccountId, wiki.roles, null)) {
                    ret.add(wiki);
                }
            }
            return ret;
        }

        //
        @Override
        @Transaction
        public int addWiki(String token, WikiInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            return addWiki0(account, bean, false);
        }

        private int addWiki0(Account account, WikiInfo bean, boolean ignorePermission) {
//            bizService.checkPermissionForProjectAccess(account, bean.projectId);
            if(!ignorePermission){
                bizService.checkProjectPermission(account, bean.projectId, Permission.ID_创建和编辑知识库);
            }
            bean.companyId = account.companyId;
            bean.createAccountId = account.id;
            bean.uuid = BizUtil.randomUUID();
            BizUtil.checkValid(bean);
            BizUtil.checkUniqueKeysOnAdd(dao, bean, "Wiki名称已经存在");
            dao.add(bean);
            //
            bizService.addChangeLog(account, bean.projectId, 0, ChangeLog.TYPE_新增知识库,
                    JSONUtil.toJson(bean));
            //`
            return bean.id;
        }

        @Transaction
        @Override
        public void updateWiki(String token, WikiInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            Wiki old = dao.getExistedByIdForUpdate(Wiki.class, bean.id);
            checkWikiPermission(account, old.id);
            bizService.checkPermission(account, old.companyId);
            BizUtil.checkUniqueKeysOnUpdate(dao, bean, old);
            old.roles = bean.roles;
            old.enableRole = bean.enableRole;
            old.name = bean.name;
            old.updateAccountId = account.id;
            BizUtil.checkValid(old);
            dao.update(old);

        }

        @Transaction
        @Override
        public void deleteWiki(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            WikiInfo old = dao.getExistedByIdForUpdate(WikiInfo.class, id);
            if (old.isDelete) {
                return;
            }
            bizService.checkPermissionForProjectAccess(account, old.projectId);
            old.isDelete = true;
            old.updateAccountId = account.id;
            BizUtil.checkValid(old);
            dao.update(old);
            //
            bizService.addChangeLog(account, old.projectId, 0, ChangeLog.TYPE_删除知识库,
                    JSONUtil.toJson(old));
            //
            //放入回收站
            addCompanyRecycle(account, old.id, CompanyRecycle.TYPE_WIKI, 0, old.name);
        }
        ///

        /**
         * 通过ID查询wiki页面
         */
        @Override
        public WikiPageDetailInfo getWikiPageById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            WikiPageDetailInfo info = dao.getExistedById(WikiPageDetailInfo.class, id);
            checkWikiPermission(account, info.wikiId);
            return info;
        }


        @Override
        public WikiPageDetailInfo getWikiPageByUuid(String uuid) {
            WikiPageDetailInfo info = dao.getExistedByUuid(WikiPageDetailInfo.class, uuid);
            if (info.isDelete) {
                return null;
            }
            return info;
        }

        @Override
        public int getLatestDraftWikiPage(String token, int originalId) {
            bizService.getExistedAccountByToken(token);
            return originalId;
        }

        private void checkWikiPermission(Account account, int wikiId) {
            Wiki wiki = dao.getExistedById(Wiki.class, wikiId);
            bizService.checkPermissionForProjectAccess(account, wiki.projectId);
            if (!bizService.checkAccountProjectPermission(account.id, wiki.projectId,
                    wiki.enableRole, wiki.createAccountId, wiki.roles, null)) {
                throw new AppException("权限不足");
            }
        }

        /**
         * 查询wiki页面列表和总数
         */
        @Override
        public Map<String, Object> getWikiPageList(String token, int wikiId) {
            Account account = bizService.getExistedAccountByToken(token);
            checkWikiPermission(account, wikiId);
            Map<String, Object> result = new HashMap<>();
            //
            WikiPageQuery query = new WikiPageQuery();
            query.wikiId = wikiId;
            query.sortWeightSort = WikiPageQuery.SORT_TYPE_ASC;
            setupQuery(account, query);
            query.pageSize = Integer.MAX_VALUE;
            List<WikiPageInfo> list = dao.getList(query);
            List<WikiPageInfo> draftList = new ArrayList<>();
            List<WikiPageInfo> releaseList = new ArrayList<>(list);
            List<WikiPageTree> trees = getWikiPageTree(releaseList);
            //
            result.put("draftList", draftList);
            result.put("wikiPageTree", trees);
            return result;
        }

        @Override
        public Map<String, Object> getCompanyWikiPageList(String token, WikiPageQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            // 判断是否存在权限控制 -> 查询用户关联所有项目【项目未删除、公司为登陆用户公司、账号编号为登陆用户】
            List<Wiki> wikiList = getCompanyWikiList(account);
            // 核实具备角色控制的wiki，则移除【编码逻辑控制】
            List<Integer> wikiIds = new ArrayList<>();
            // 默认首页wiki
            wikiIds.add(0);
            if (wikiList != null && wikiList.size() > 0) {
                wikiList.forEach(wiki -> {
                    boolean hasPermission = bizService.checkAccountProjectPermission(account.id, wiki.projectId,
                            wiki.enableRole, wiki.createAccountId, wiki.roles, null);
                    if (hasPermission) {
                        wikiIds.add(wiki.id);
                    }
                });
            }
            // 查询关联数据信息
            query.wikiIds = wikiIds.stream().mapToInt(Integer::intValue).toArray();
            setupQuery(account, query);
            return createResult(dao.getList(query), dao.getListCount(query));
        }


        /**
         * 获取用户关联的wiki集合
         *
         * @param account
         * @return
         */
        private List<Wiki> getCompanyWikiList(Account account) {
            if(bizService.isCompanySuperBoss(account)){
                WikiQuery wikiQuery = new WikiQuery();
                setupQuery(account,wikiQuery);
                return dao.getAll(wikiQuery);
            }
            if(bizService.isDepartmentSuperBoss(account)){
              Set<Integer> projectId  = bizService.getAccountAccessProjectList(account,false,false);
                WikiQuery wikiQuery = new WikiQuery();
                setupQuery(account,wikiQuery);
                wikiQuery.projectIdInList = projectId.stream().mapToInt(k->k).toArray();
                return dao.getAll(wikiQuery);
            }
            String sql = "SELECT * FROM t_wiki WHERE is_delete = false AND \n" +
                    "project_id IN (SELECT DISTINCT(project_id) FROM t_project_member " +
                    "WHERE account_id = ? AND company_id = ?  AND project_id IN (SELECT id FROM t_project WHERE is_delete = false)) ;\n";
            return dao.queryList(Wiki.class, sql, account.id, account.companyId);
        }

        @Override
        public WikiPageDetailInfo getCompanyWikiPageById(String token, int id) {
            return dao.getExistedById(WikiPageDetailInfo.class, id);
        }

        @Transaction
        @Override
        public int addCompanyWikiPage(String token, WikiPageDetailInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            if (StringUtil.isEmpty(bean.content)) {
                throw new AppException("内容不能为空");
            }
            bizService.checkCompanyPermission(account,Permission.ID_创建企业wiki);
            setWikiPageLevel(bean);
            bean.companyId = account.companyId;
            bean.projectId = 0;
            bean.createAccountId = account.id;
            bean.status = WikiPageInfo.STATUS_已发布;
            bean.isCreateIndex = true;
            bean.uuid = BizUtil.randomUUID();
            checkDataDictValueValid("WikiPage.type", bean.type, "类型错误");
            bean.sortWeight = Integer.MAX_VALUE;
            BizUtil.checkValid(bean);
            BizUtil.checkUniqueKeysOnAdd(dao, bean);
            int id = dao.add(bean);
            //
            WikiContent content = new WikiContent();
            if (bean.type == WikiPage.TYPE_富文本) {
                content.content = bizService.getContent(bean.content);
            } else {
                content.content = bean.content;
            }
            content.companyId = bean.companyId;
            content.wikiPageId = bean.id;
            dao.add(content);
            //
            bean.contentId = content.id;
            dao.updateSpecialFields(bean, "contentId");
            //
//            bizService.addChangeLog(account, bean.projectId, 0, ChangeLog.TYPE_新增WIKIPAGE,
//                    JSONUtil.toJson(WikiPageSimpleInfo.create(bean)));
//            //
            return id;
        }

        @Transaction
        @Override
        public void updateCompanyWikiPage(String token, WikiPageDetailInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            WikiPage old = dao.getExistedByIdForUpdate(WikiPage.class, bean.id);
            if (StringUtil.isEmpty(bean.content)) {
                throw new AppException("内容不能为空");
            }
            if(old.createAccountId!=account.id){
                bizService.checkCompanyPermission(account,Permission.ID_编辑企业wiki);
            }
//            BizUtil.asserts(old.createAccountId == account.id, "您不是创建人，无法进行编辑");
            old.parentId = bean.parentId;
            setWikiPageLevel(old);
            old.name = bean.name;
            old.updateAccountId = account.id;
            old.isCreateIndex = false;
            BizUtil.checkValid(old);
            dao.update(old);
            //
            WikiContent content = dao.getExistedByIdForUpdate(WikiContent.class, old.contentId);
            if (old.type == WikiPage.TYPE_富文本) {
                content.content = bizService.getContent(bean.content);
            } else {
                content.content = bean.content;
            }
            dao.update(content);
        }

        @Transaction
        @Override
        public void deleteCompanyWikiPage(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            WikiPage old = dao.getExistedByIdForUpdate(WikiPage.class, id);
            if(old.createAccountId!=account.id){
                bizService.checkCompanyPermission(account,Permission.ID_删除企业wiki);
            }
            old.isDelete = true;
            old.isCreateIndex = false;
            old.updateAccountId = account.id;
            dao.update(old);
        }

        /**
         * 只记录变更记录
         */
        @Transaction
        @Override
        public void releaseCompanyWikiPage(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
//            WikiPageDetailInfo old = dao.getExistedById(WikiPageDetailInfo.class, id);
      /*      Wiki wiki = dao.getExistedByIdForUpdate(Wiki.class, old.wikiId);
            checkWikiPermission(account, wiki.id);*/
            WikiPageDetailInfo    old = dao.getExistedById(WikiPageDetailInfo.class, id);
            if (old.isDelete) {
                return;
            }
            WikiPageChangeLogDetailInfo changeLog = dao.getLastWikiPageChangeLog(id);
            if (changeLog == null ||
                    !changeLog.content.equals(old.content)) {
                //
                WikiPageChangeLog log = new WikiPageChangeLog();
//                log.wikiId = wiki.id;
                log.wikiPageId = id;
                log.name = old.name;
                log.createAccountId = account.id;
                dao.add(log);
                //
                WikiContent content = new WikiContent();
                content.companyId = old.companyId;
                content.content = old.content;
                content.wikiPageId = id;
                content.wikiPageChangeLogId = log.id;
                dao.add(content);
                //
                log.contentId = content.id;
                dao.update(log);
            }
        }

        @Override
        @Transaction
        public int revertCompanyWikiPage(String token, int logId) {
            Account account = bizService.getExistedAccountByToken(token);
            WikiPageChangeLogDetailInfo log = dao.getExistedById(WikiPageChangeLogDetailInfo.class, logId);
           /* Wiki wiki = dao.getExistedByIdForUpdate(Wiki.class, log.wikiId);
            checkWikiPermission(account, wiki.id);*/
            //
            WikiPageDetailInfo oldPage = dao.getExistedByIdForUpdate(WikiPageDetailInfo.class, log.wikiPageId);
            //
            oldPage.name = log.name;
            dao.update(oldPage);
            //
            WikiContent content = dao.getExistedByIdForUpdate(WikiContent.class, oldPage.contentId);
            content.content = log.content;
            dao.update(content);

            return oldPage.id;
        }

        @Override
        public List<WikiPageChangeLogInfo> getCompanyWikiPageChangeLogInfoList(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            WikiPage wikiPage = dao.getExistedById(WikiPage.class, id);
            /* checkWikiPermission(account, wikiPage.wikiId);*/
            WikiPageChangeLogQuery query = new WikiPageChangeLogQuery();
            query.wikiPageId = id;
            query.pageSize = 10;
            return dao.getList(query);
        }

        @Override
        public WikiPageChangeLogDetailInfo getCompanyWikiPageChangeLogInfoById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            WikiPageChangeLogDetailInfo bean = dao.getExistedById(WikiPageChangeLogDetailInfo.class, id);
            /*checkWikiPermission(account, bean.wikiId);*/
            return bean;
        }


        private List<WikiPageTree> getWikiPageTree(List<WikiPageInfo> list) {
            boolean expand = true;
            List<WikiPageTree> result = new ArrayList<>();
            Queue<WikiPageTree> queue = new LinkedList<>();
            for (WikiPageInfo e : list) {
                if (e.level == 1) {
                    WikiPageTree tree = createWikiPageTree(e, expand);
                    queue.offer(tree);
                    result.add(tree);
                }
            }
            while (!queue.isEmpty()) {
                WikiPageTree dir = queue.poll();
                for (WikiPageInfo e : list) {
                    if (e.parentId == dir.id) {
                        WikiPageTree child = createWikiPageTree(e, expand);
                        dir.children.add(child);
                        queue.offer(child);
                    }
                }
            }
            //
            return result;
        }

        private WikiPageTree createWikiPageTree(WikiPageInfo bean, boolean expand) {
            WikiPageTree dir = new WikiPageTree();
            dir.id = bean.id;
            dir.name = bean.name;
            dir.level = bean.level;
            dir.parentId = bean.parentId;
            dir.draftId = bean.draftId;
            dir.type = bean.type;
            dir.expand = expand;
            return dir;
        }

        /**
         * 新增wiki页面
         */
        @Transaction
        @Override
        public int addWikiPage(String token, WikiPageDetailInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            return addWikiPage0(account, bean);
        }

        private int addWikiPage0(Account account, WikiPageDetailInfo bean) {
            Wiki wiki = dao.getExistedByIdForUpdate(Wiki.class, bean.wikiId);//一定要锁定
            checkWikiPermission(account, wiki.id);
            bizService.checkProjectPermission(account, wiki.projectId, Permission.ID_创建和编辑页面);
            if (StringUtil.isEmpty(bean.content)) {
                throw new AppException("内容不能为空");
            }
            setWikiPageLevel(bean);
            bean.companyId = account.companyId;
            bean.projectId = wiki.projectId;
            bean.createAccountId = account.id;
            bean.status = WikiPageInfo.STATUS_已发布;
            bean.isCreateIndex = true;
            bean.uuid = BizUtil.randomUUID();
            checkDataDictValueValid("WikiPage.type", bean.type, "类型错误");
            bean.sortWeight = Integer.MAX_VALUE;
            BizUtil.checkValid(bean);
            BizUtil.checkUniqueKeysOnAdd(dao, bean);
            int id = dao.add(bean);
            //
            WikiContent content = new WikiContent();
            if (bean.type == WikiPage.TYPE_富文本) {
                content.content = bizService.getContent(bean.content);
            } else {
                content.content = bean.content;
            }
            content.companyId = bean.companyId;
            content.wikiPageId = bean.id;
            dao.add(content);
            //
            bean.contentId = content.id;
            dao.update(bean);
            //
            bizService.addChangeLog(account, bean.projectId, 0, ChangeLog.TYPE_新增WIKIPAGE,
                    JSONUtil.toJson(WikiPageSimpleInfo.create(bean)));
            //
            return id;
        }

        //
        private void setWikiPageLevel(WikiPage bean) {
            if (bean.parentId == 0) {
                bean.level = 1;
            } else {
                WikiPage parent = dao.getExistedById(WikiPage.class, bean.parentId);
                if (parent.wikiId != bean.wikiId || parent.isDelete) {
                    throw new AppException("父页面不存在");
                }
                bean.level = parent.level + 1;
            }
        }

        /**
         * 编辑wiki页面
         */
        @Transaction
        @Override
        public void updateWikiPage(String token, WikiPageDetailInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            WikiPage old = dao.getExistedByIdForUpdate(WikiPage.class, bean.id);
            if (StringUtil.isEmpty(bean.content)) {
                throw new AppException("内容不能为空");
            }
            checkWikiPermission(account, old.wikiId);
            old.parentId = bean.parentId;
            setWikiPageLevel(old);
            old.name = bean.name;
            old.updateAccountId = account.id;
            old.isCreateIndex = false;
            BizUtil.checkValid(old);
            dao.update(old);
            //
            WikiContent content = dao.getExistedByIdForUpdate(WikiContent.class, old.contentId);
            if (old.type == WikiPage.TYPE_富文本) {
                content.content = bizService.getContent(bean.content);
            } else {
                content.content = bean.content;
            }
            dao.update(content);
        }

        /**
         * 删除wiki页面
         */
        @Transaction
        @Override
        public void deleteWikiPage(String token, int id, String reason) {
            Account account = bizService.getExistedAccountByToken(token);
            WikiPage old = dao.getExistedById(WikiPage.class, id);
            Wiki wiki = dao.getExistedByIdForUpdate(Wiki.class, old.wikiId);//一定要锁定
            checkWikiPermission(account, wiki.id);
            old = dao.getExistedByIdForUpdate(WikiPage.class, id);//这一步不是多余的
            if (old.isDelete) {
                return;
            }
            WikiPageQuery query = new WikiPageQuery();
            query.parentId = old.id;
            query.isDelete = false;
            int childCount = dao.getListCount(query);
            if (childCount > 0) {
                throw new AppException("包含子页面的父页面不能删除");
            }
            //
            old.isDelete = true;
            old.isCreateIndex = false;
            old.updateAccountId = account.id;
            dao.update(old);
            //
            if (old.status == WikiPage.STATUS_已发布) {
                bizService.addChangeLog(account, old.projectId, 0, ChangeLog.TYPE_删除WIKIPAGE,
                        JSONUtil.toJson(WikiPageSimpleInfo.create(old)));
            }

            //如果是关联文件也要解除关联关系
            List<WikiAssociated> wikiAssociated = dao.getWikiAssociatedListByWikiPageIdAndCompanyId(id, account.companyId);
            if (!BizUtil.isNullOrEmpty(wikiAssociated)) {
                wikiAssociated.forEach(w -> {
                    try {
                        deleteTaskAttachment0(token, w.taskId, w.wikiPageId, true, AttachmentAssociated.TYPE_WIKI);
                    } catch (Exception e) {
                        logger.warn("can't delete task associate wiki", e);
                    }
                });
            }
        }

        /**
         * 只记录变更记录
         */
        @Transaction
        @Override
        public void releaseWikiPage(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            WikiPageDetailInfo old = dao.getExistedById(WikiPageDetailInfo.class, id);
            Wiki wiki = dao.getExistedByIdForUpdate(Wiki.class, old.wikiId);
            checkWikiPermission(account, wiki.id);
            old = dao.getExistedById(WikiPageDetailInfo.class, id);
            if (old.isDelete) {
                return;
            }
            WikiPageChangeLogDetailInfo changeLog = dao.getLastWikiPageChangeLog(id);
            if (changeLog == null ||
                    !changeLog.content.equals(old.content)) {
                //
                WikiPageChangeLog log = new WikiPageChangeLog();
                log.wikiId = wiki.id;
                log.wikiPageId = id;
                log.name = old.name;
                log.createAccountId = account.id;
                dao.add(log);
                //
                WikiContent content = new WikiContent();
                content.companyId = old.companyId;
                content.content = old.content;
                content.wikiPageId = id;
                content.wikiPageChangeLogId = log.id;
                dao.add(content);
                //
                log.contentId = content.id;
                dao.update(log);
            }
        }

        @Override
        @Transaction
        public int revertWikiPage(String token, int logId) {
            Account account = bizService.getExistedAccountByToken(token);
            WikiPageChangeLogDetailInfo log = dao.getExistedById(WikiPageChangeLogDetailInfo.class, logId);
            Wiki wiki = dao.getExistedByIdForUpdate(Wiki.class, log.wikiId);
            checkWikiPermission(account, wiki.id);
            //
            WikiPageDetailInfo oldPage = dao.getExistedByIdForUpdate(WikiPageDetailInfo.class, log.wikiPageId);
            //
            oldPage.name = log.name;
            dao.update(oldPage);
            //
            WikiContent content = dao.getExistedByIdForUpdate(WikiContent.class, oldPage.contentId);
            content.content = log.content;
            dao.update(content);

            return oldPage.id;
        }

        @Override
        public List<WikiPageChangeLogInfo> getWikiPageChangeLogInfoList(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            WikiPage wikiPage = dao.getExistedById(WikiPage.class, id);
            checkWikiPermission(account, wikiPage.wikiId);
            WikiPageChangeLogQuery query = new WikiPageChangeLogQuery();
            query.wikiPageId = id;
            query.pageSize = 10;
            return dao.getList(query);
        }

        @Override
        public WikiPageChangeLogDetailInfo getWikiPageChangeLogInfoById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            WikiPageChangeLogDetailInfo bean = dao.getExistedById(WikiPageChangeLogDetailInfo.class, id);
            checkWikiPermission(account, bean.wikiId);
            return bean;
        }

        @Transaction
        @Override
        public void updateWikiPageSortWeightAndRelation(String token, List<WikiPageTree> trees) {
            Account account = bizService.getExistedAccountByToken(token);
            if (trees == null || trees.isEmpty()) {
                return;
            }
            WikiPageTree first = trees.get(0);
            WikiPage bean = dao.getExistedByIdForUpdate(WikiPage.class, first.id);
            Wiki wiki = dao.getExistedByIdForUpdate(Wiki.class, bean.wikiId);//一定要锁住，否则这个时候新增一个wiki就会有问题),新增,删除,发布都需要锁住
            checkWikiPermission(account, wiki.id);
            int sortWeight = 1;
            for (WikiPageTree tree : trees) {
                recurveWikiPageTree(bean.wikiId, 0, 1, sortWeight++, tree);
            }
        }

        //递归wikiePagessss
        private void recurveWikiPageTree(int wikiId, int parentId, int level, int sortWeight, WikiPageTree tree) {
            WikiPage bean = dao.getExistedByIdForUpdate(WikiPage.class, tree.id);
            if (wikiId != bean.wikiId) {
                throw new AppException("参数错误");
            }
            if (bean.status != WikiPage.STATUS_已发布) {
                throw new AppException("参数错误");
            }
            if (bean.isDelete) {
                throw new AppException("页面【" + bean.name + "】已经删除");
            }
            bean.level = level;
            bean.parentId = parentId;
            bean.sortWeight = sortWeight;
            dao.update(bean);
            if (tree.children != null) {
                for (WikiPageTree child : tree.children) {
                    sortWeight++;
                    recurveWikiPageTree(wikiId, bean.id, bean.level + 1, sortWeight, child);
                }
            }
        }

        @Override
        public List<IterationBurnDownData> getIterationBurnDownChart(String token, int iterationId, int type, int objectType) {
            Account account = bizService.getExistedAccountByToken(token);
            return statService.getIterationBurnDownChart0(account, iterationId, type, objectType);
        }

        //
        @Override
        public List<ProjectFinishDelayRate> getProjectFinishDelayRate(String token, int projectId) {
            Account account = bizService.getExistedAccountByToken(token);
            return getProjectFinishDelayRate0(account, projectId);
        }

        private List<ProjectFinishDelayRate> getProjectFinishDelayRate0(Account account, int projectId) {
            Project project = dao.getExistedById(Project.class, projectId);
            bizService.checkPermissionForProjectAccess(account, project.id);
            List<ProjectFinishDelayRate> list = new ArrayList<>();
            //1.先找到项目下所有开启statusBase(会finish的)的模块
            List<ProjectModuleInfo> modules = bizService.getEnableStatusBaseProjectModuleInfoList(projectId);
            for (ProjectModuleInfo module : modules) {
                if (module.objectType == 0) {
                    continue;
                }
                ProjectFinishDelayRate bean = new ProjectFinishDelayRate();
                bean.objectType = module.objectType;
                bean.totalNum = bizService.getTotalTaskNumByProjectObjectType(projectId, module.objectType);
                if (bean.totalNum > 0) {
                    bean.finishNum = bizService.getTotalStatusBaseFinishTaskNumByProjectObjectType(projectId, module.objectType);
                    bean.delayNum = bizService.getTotalTimeBaseDelayTaskNumByProjectObjectType(projectId, module.objectType);
                }
                list.add(bean);
            }
            return list;
        }

        //
        @Override
        public List<IterationFinishDelayRate> getIterationFinishDelayRate(String token, int iterationId) {
            Account account = bizService.getExistedAccountByToken(token);
            return getIterationFinishDelayRate0(account, iterationId);
        }

        private List<IterationFinishDelayRate> getIterationFinishDelayRate0(Account account, int iterationId) {
            ProjectIteration iteration = dao.getExistedById(ProjectIteration.class, iterationId);
            bizService.checkPermissionForProjectAccess(account, iteration.projectId);
            int projectId = iteration.projectId;
            List<IterationFinishDelayRate> list = new ArrayList<>();
            //1.先找到项目下所有开启的模块
            List<ProjectModuleInfo> modules = bizService.getEnableProjectModuleInfoList(projectId);
            for (ProjectModuleInfo module : modules) {
                if (module.objectType == 0) {
                    continue;
                }
                IterationFinishDelayRate bean = new IterationFinishDelayRate();
                bean.objectType = module.objectType;
                bean.totalNum = bizService.getTotalTaskNumByIterationObjectType(iterationId, module.objectType);
                if (bean.totalNum > 0) {
                    bean.finishNum = bizService.getTotalStatusBaseFinishTaskNumByIterationObjectType(iteration.id, module.objectType);
                    bean.delayNum = bizService.getTotalTimeBaseDelayTaskNumByIterationObjectType(iteration.id, module.objectType);
                }
                list.add(bean);
            }
            return list;
        }
        //

        /**
         * 查询迭代进度报告
         */
        @Override
        public List<ProgressReport> getIterationProgressReportList(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            Set<Integer> projectIds = bizService.getAccountAccessProjectList(account, true, false);
//            if (!bizService.isCompanySuperBoss(account)) {
//                projectIds = dao.getRunningProjectIdList(account.id);
//            } else {
//                projectIds = dao.getAllRunningProjectIdList(account.companyId);
//            }
//            if (projectIds.size() == 0) {
//                return new ArrayList<>();
//            }
            ProjectIterationQuery query = new ProjectIterationQuery();
            query.statusInList = new int[]{ProjectIteration.STATUS_开发中, ProjectIteration.STATUS_计划中};
            query.isDelete = false;
            query.projectIdInList = BizUtil.convertList(projectIds);
            query.pageSize = 10000;
            List<ProjectIterationInfo> iterations = dao.getList(query);
            List<ProgressReport> list = new ArrayList<>();
            for (ProjectIterationInfo iteration : iterations) {
                list.add(createIterationProgressReport(account, iteration));
            }
            return list;
        }

        private ProgressReport createIterationProgressReport(Account account, ProjectIterationInfo iteration) {
            List<IterationFinishDelayRate> list = getIterationFinishDelayRate0(account, iteration.id);
            ProgressReport report = new ProgressReport();
            report.projectName = iteration.projectName;
            report.iterationName = iteration.name;
            report.iterationStatus = iteration.status;
            report.strIterationStatus = BizTaskJobs.getDataDictName("ProjectIteration.status", iteration.status);
            report.startDate = iteration.startDate;
            report.endDate = iteration.endDate;
            report.projectUuid = iteration.projectUuid;
            for (IterationFinishDelayRate e : list) {
                report.totalNum += e.totalNum;
                report.finishNum += e.finishNum;
                report.delayNum += e.delayNum;
            }
            if (report.totalNum == 0) {
                report.finishRate = "--";
                report.delayRate = "--";
            } else {
                report.finishRate = StringUtil.numberFormat(report.finishNum / (report.totalNum * 1.0), 3);
                report.delayRate = StringUtil.numberFormat(report.delayNum / (report.totalNum * 1.0), 3);
            }
            return report;
        }

        /**
         * 查询项目进度报告
         */
        @Override
        public List<ProgressReport> getProjectProgressReportList(String token, ProjectQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            List<ProgressReport> list = new ArrayList<>();
            List<ProjectInfo> projectList = bizService.getMyRunnigProjectInfoList(account.id, account.companyId, query);
            for (ProjectInfo e : projectList) {
                list.add(createProjectProgressReport(e));
            }
            return list;
        }

        //
        @Override
        public List<String> getMyRunningProjectGroupList(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            return bizService.getMyRunningProjectGroupList(account.id, account.companyId);
        }

        //
        private ProgressReport createProjectProgressReport(ProjectInfo bean) {
            ProgressReport report = new ProgressReport();
            report.projectName = bean.name;
            int[] objectTypes = bizService.getEnableObjectTypelist(bean.id);
            if (BizUtil.isNullOrEmpty(objectTypes)) {
                return report;
            }
            TaskQuery query = new TaskQuery();
            //测试用例不统计
            query.objectTypeNotInList = new int[]{Task.OBJECTTYPE_测试用例};
            query.projectId = bean.id;
            query.isDelete = false;
            report.totalNum = dao.getListCount(query);
            query.isFinish = true;
            report.finishNum = dao.getListCount(query);
            query.isFinish = null;
            query.isDelay = true;
            report.delayNum = dao.getListCount(query);
            //
            if (report.totalNum == 0) {
                report.finishRate = "--";
                report.delayRate = "--";
            } else {
                report.finishRate = StringUtil.numberFormat(report.finishNum / (report.totalNum * 1.0), 3);
                report.delayRate = StringUtil.numberFormat(report.delayNum / (report.totalNum * 1.0), 3);
            }
            //
            report.projectUuid = bean.uuid;
            return report;
        }

        //
        @Override
        @Transaction
        public void inviteMemberFromMobileNo(String token, String mobileNo, String code, Integer projectId) {
            Account account = bizService.getExistedAccountByToken(token);
            int companyId = account.companyId;
            if (projectId == null) {
                projectId = 0;
            }
            if (StringUtil.isEmpty(mobileNo)) {
                throw new AppException("手机号不能为空");
            }
            bizService.checkAccountPermission(account);
            bizService.checkVerifyCode(mobileNo, code);
            //
            Account invitedAccount = dao.getAccountByMobileNo(mobileNo);
            if (invitedAccount == null) {
                throw new AppException("被邀请人没有注册，请在该手机号注册后再邀请该用户");
            }
            //
            //邀请到公司成员
            CompanyMember companyMember = dao.getCompanyMemberByAccountIdCompanyId(invitedAccount.id, companyId);
            if (companyMember != null) {
                throw new AppException("被邀请人已经是企业成员");
            }
            Role role = dao.getRoleByCompanyIdTypeName(companyId, Role.TYPE_全局, Role.NAME_成员);
            Department root = dao.getDepartmentByCompanyIdLevel(companyId, 1);
            bizService.addOrUpdateCompanyAccount(account.companyId, account, invitedAccount,
                    Collections.singletonList(role.id), Collections.singletonList(root.id));
            //
            invitedAccount.companyId = companyId;
            dao.update(invitedAccount);
            //
            if (projectId > 0) {//邀请成为项目成员
                Project project = dao.getExistedById(Project.class, projectId);
                bizService.checkPermission(account, project.companyId);
                ProjectMember pm = dao.getProjectMemberInfoByProjectIdAccountId(projectId, invitedAccount.id);
                if (pm != null) {
                    throw new AppException("被邀请人已经是项目成员");
                }
                if (!project.isDelete) {
                    role = dao.getRoleByCompanyIdTypeName(project.companyId, Role.TYPE_项目, Role.NAME_项目成员);
                    Set<Integer> accountIdSet = new HashSet<>();
                    accountIdSet.add(invitedAccount.id);
                    Set<Integer> roleSet = new HashSet<>();
                    roleSet.add(role.id);
                    addProjectMembers0(account, projectId, accountIdSet, roleSet, true, null);
                    bizService.addOptLog(invitedAccount, project.id, 0, project.id, project.name, OptLog.EVENT_ID_加入项目, "");
                }
            }
        }

        @Transaction
        @Override
        public void inviteProjectMember(String token, String emails, Integer projectId) {
            Account account = bizService.getExistedAccountByToken(token);
            if (projectId == null) {
                projectId = 0;
            }
            bizService.checkAccountPermission(account);
            if (StringUtil.isEmpty(emails)) {
                throw new AppException("邮箱不能为空");
            }
            emails = emails.replaceAll("；", ";");
            String[] emailList = emails.split(";");
            //
            AccountInviteMember sendMail = dao.getAccountInviteMember(account.id);
            if (sendMail == null) {
                sendMail = new AccountInviteMember();
                sendMail.accountId = account.id;
                dao.add(sendMail);
            }
            //
            for (String email : emailList) {
                if (email == null || email.trim().isEmpty()) {
                    continue;
                }
                if (sendMail.dailySendMailNum >= 10) {
                    throw new AppException("每日发送邮件数量超过上限");
                }
                if (sendMail.totalSendMailNum >= 500) {
                    throw new AppException("发送邮件数量超过上限");
                }
                email = email.trim();
                if (!PatternUtil.isEmail(email)) {
                    logger.error("inviteProjectMember failed.isNotEmail email:{}", emails);
                    throw new AppException("邮箱格式错误");
                }
                Account invitedAccount = dao.getAccountByEmail(email);
                int invitedAccountId;
                if (invitedAccount != null) {
                    invitedAccountId = invitedAccount.id;
                    if (invitedAccountId == account.id) {
                        throw new AppException("不能邀请自己");
                    }
                }
                if (projectId > 0) {//邀请成为项目成员
                    Project project = dao.getExistedById(Project.class, projectId);
                    bizService.checkPermission(account, project.companyId);
                    if (invitedAccount != null) {
                        ProjectMember pm = dao.getProjectMemberInfoByProjectIdAccountId(projectId, invitedAccount.id);
                        if (pm != null) {
                            throw new AppException("被邀请人已经是项目成员");
                        }
                    }
                } else {//邀请到公司成员
                    if (invitedAccount != null) {
                        CompanyMember companyMember = dao.getCompanyMemberByAccountIdCompanyId(invitedAccount.id, account.companyId);
                        if (companyMember != null) {
                            throw new AppException("被邀请人已经是企业成员");
                        }
                    }
                }
                //发送邮件
                CompanyMemberInvite bean = dao.getCompanyMemberInvite(account.companyId, projectId, account.id, email);
                if (bean == null) {
                    bean = new CompanyMemberInvite();
                    bean.uuid = BizUtil.randomUUID();
                    bean.companyId = account.companyId;
                    bean.projectId = projectId;
                    bean.accountId = account.id;
                    bean.invitedEmail = email;
                    dao.add(bean);
                }
                //
                String code = bean.uuid;
                String url = GlobalConfig.webUrl + "invite.html?code=" + code;
                //
                String title = account.name + "邀请您使用CORNERSTONE";
                String content = account.name + "邀请您使用CORNERSTONE，点击按钮进入CORNERSTONE。";
                String btnName = "立即加入";
                sendEmail(email, title, content, btnName, url);
                //
                sendMail.dailySendMailNum++;
                sendMail.totalSendMailNum++;
                dao.update(sendMail);
                //
            }//for
            //
        }

        //
        private void sendEmail(String toEmails, String title, String content, String btnName, String url) {
            try {
                InputStream is = EmailService.class.getResourceAsStream("email_template.html");
                String htmlContent = IOUtil.toString(is);
                htmlContent = htmlContent.replaceAll("#content#", content);
                htmlContent = htmlContent.replaceAll("#btnName#", btnName);
                htmlContent = htmlContent.replaceAll("#url#", url);
                emailService.sendMail(toEmails, title, htmlContent);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new AppException("邮件发送失败");
            }
        }

        @Transaction
        @Override
        public void deleteCompanyMemberInvite(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            CompanyMemberInvite old = dao.getExistedByIdForUpdate(CompanyMemberInvite.class, id);
            bizService.checkPermission(account, old.companyId);
            dao.deleteById(CompanyMemberInvite.class, id);
            logger.info("deleteCompanyMemberInvite accountId:{} old id:{} uuid:{}",
                    account.id, old.id, old.uuid);
        }

        /**
         * 查询企业邀请码列表和总数
         */
        @Override
        @Transaction
        public List<CompanyMemberInviteCodeInfo> getCompanyMemberInviteCodeList(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            CompanyMemberInviteCodeQuery query = new CompanyMemberInviteCodeQuery();
            query.pageSize = 20;
            setupQuery(account, query);
            List<CompanyMemberInviteCodeInfo> list = dao.getList(query);
            if (list.size() == 0) {
                list = generateCompanyMemberInviteCodes(account);
            }
            return list;
        }

        @Override
        @Transaction
        public List<CompanyMemberInviteCodeInfo> resetCompanyMemberInviteCodeList(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            dao.delete(CompanyMemberInviteCode.class, QueryWhere.create().where("company_id", account.companyId));
            return generateCompanyMemberInviteCodes(account);
        }

        private synchronized List<CompanyMemberInviteCodeInfo> generateCompanyMemberInviteCodes(Account account) {
            List<CompanyMemberInviteCodeInfo> list = new ArrayList<>();
            List<String> codes = generateCodeTry100Count(Integer.toHexString(account.companyId).toUpperCase());
            for (String code : codes) {
                CompanyMemberInviteCodeInfo bean = new CompanyMemberInviteCodeInfo();
                bean.companyId = account.companyId;
                bean.code = code;
                bean.createAccountId = account.id;
                bean.status = CompanyMemberInviteCode.STATUS_未使用;
                dao.add(bean);
                list.add(bean);
            }
            return list;
        }

        private List<String> generateCodeTry100Count(String pre) {
            for (int i = 0; i < 100; i++) {
                List<String> codes = generateCode(pre, 20);
                int count = dao.getCompanyMemberInviteCodeCount(codes);
                if (count == 0) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("generateCodeTry100Count success.i:{}", i);
                    }
                    return codes;
                }
            }
            throw new AppException("生成失败，请稍后再试");
        }

        private List<String> generateCode(String pre, int total) {
            List<String> codes = new ArrayList<>();
            int codeNum = 0;
            for (int i = 0; i < 10000; i++) {
                String code = pre + RC4.encry_RC4_string((i + 100) + "", UUID.randomUUID().toString()).toUpperCase();
                if (!codes.contains(code)) {
                    codes.add(code);
                    codeNum++;
                    if (codeNum == total) {
                        break;
                    }
                }

            }
            return codes;
        }

        @Override
        public List<CompanyMemberInviteInfo> getCompanyMemberInviteInfoList(String token, Integer projectId) {
            Account account = bizService.getExistedAccountByToken(token);
            CompanyMemberInviteQuery query = new CompanyMemberInviteQuery();
            query.companyId = account.companyId;
            query.accountId = account.id;
            query.projectId = projectId;
            query.pageSize = 10000;
            return dao.getList(query);
        }

        @Override
        public CompanyMemberInviteInfo getCompanyMemberInviteInfoByUuID(String uuid) {
            return dao.getExistedByUuid(CompanyMemberInviteInfo.class, uuid);
        }

        @Override
        public List<TaskStatDayData> getTaskCreateDayDataList(String token, TaskStatDayDataQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            query.companyId = account.companyId;
            return statDao.getTaskCreateDayDataList(query);
        }

        @Override
        public List<TaskStatDayData> getTaskCurrStatusDistributeList(String token, TaskStatDayDataQuery query) {
            if (query.projectId == null) {
                throw new AppException("项目是必选项");
            }
            if (query.objectType == null) {
//                throw new AppException("对象类型是必选项");
                return Collections.emptyList();
            }
            Map<Integer, ProjectStatusDefine> statusDefineMap = statService.getProjectStatusDefineMap(query.projectId, query.objectType);
            Account account = bizService.getExistedAccountByToken(token);
            query.companyId = account.companyId;
            List<TaskStatDayData> list = statDao.getTaskCurrStatusDistributeList(query);
            for (TaskStatDayData e : list) {
                ProjectStatusDefine define = statusDefineMap.get(e.status);
                if (define == null) {
                    logger.error("stats not found.");
                    throw new AppException("状态错误");
                }
                e.statusName = define.name;
                e.statusColor = define.color;
            }
            return list;
        }

        //每日完成情况
        @Override
        public List<TaskStatDayData> getTaskFinishList(String token, TaskStatDayDataQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            return statService.getTaskDayDataList(account, query, "todayFinishNum", true);
        }

        //累积数量
        @Override
        public List<TaskStatDayData> getTaskTotalNumList(String token, TaskStatDayDataQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            return statService.getTaskDayDataList(account, query, "totalNum", false);
        }

        //查询代码统计(包括每日统计以及累积提交)
        @Override
        public ScmCommitStatInfo getScmCommitStatInfo(String token, int iterationId) {
            Account account = bizService.getExistedAccountByToken(token);
            return statService.getScmCommitStatInfo(account, iterationId);
        }

        @Override
        public ScmCommitStatInfo getScmCommitStatInfoByProjectId(String token, int projectId) {
            Account account = bizService.getExistedAccountByToken(token);
            return statService.getScmCommitStatInfoByProjectId(account, projectId);
        }

        //
        private int getUnReadAccountNotificationCount(Account account) {
            AccountNotificationQuery query = new AccountNotificationQuery();
            query.accountId = account.id;
            query.isWebSend = false;
            query.excludeType = AccountNotificationSetting.TYPE_日历提醒;
            return dao.getListCount(query);
        }

        //
        @Override
        public List<AccountNotification> getAccountNotificationList(String token, AccountNotificationQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            query.accountId = account.id;
            query.excludeType = AccountNotificationSetting.TYPE_日历提醒;
            List<AccountNotification> list = dao.getList(query);
            dao.updateAccountNotificationWebSend(account.id);
            return list;
        }

        //加入到回收站
        private void addCompanyRecycle(Account account, int associatedId, int type,
                                       int objectType, String name) {
            CompanyRecycle bean = new CompanyRecycle();
            bean.companyId = account.companyId;
            bean.associatedId = associatedId;
            bean.type = type;
            bean.objectType = objectType;
            bean.name = name;
            bean.createAccountId = account.id;
            bean.createAccountName = account.name;
            BizUtil.checkValid(bean);
            dao.add(bean);
        }

        @Override
        public Map<String, Object> getCompanyRecycleInfoList(String token, CompanyRecycleQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_管理回收站);
            query.companyId = account.companyId;
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        /**
         * 从回收站恢复
         */
        @Transaction
        @Override
        public void restoreCompanyRecycle(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            CompanyRecycleInfo old = dao.getExistedById(CompanyRecycleInfo.class, id);
            bizService.checkCompanyPermission(account, Permission.ID_管理回收站, old.companyId);
            dao.deleteById(CompanyRecycle.class, id);
            if (old.type == CompanyRecycle.TYPE_TASK) {
                TaskInfo bean = dao.getExistedByIdForUpdate(TaskInfo.class, old.associatedId);
                if (!bean.isDelete) {
                    return;
                }
                bean.isDelete = false;
                bean.updateAccountId = account.id;
                bean.isCreateIndex = false;
                dao.updateSpecialFields(bean, "isDelete", "updateAccountId", "isCreateIndex");
                bizService.addChangeLog(account, 0, bean.id, ChangeLog.TYPE_恢复任务,
                        JSONUtil.toJson(TaskSimpleInfo.createTaskSimpleinfo(bean)));
            } else if (old.type == CompanyRecycle.TYPE_WIKI) {
                Wiki bean = dao.getExistedByIdForUpdate(Wiki.class, old.associatedId);
                if (!bean.isDelete) {
                    return;
                }
                bean.isDelete = false;
                bean.updateAccountId = account.id;
                dao.updateSpecialFields(bean, "isDelete", "updateAccountId");
                bizService.addChangeLog(account, bean.projectId, 0, ChangeLog.TYPE_恢复知识库, JSONUtil.toJson(bean));
            } else if (old.type == CompanyRecycle.TYPE_WIKI页面) {
                WikiPage bean = dao.getExistedByIdForUpdate(WikiPage.class, old.associatedId);
                if (!bean.isDelete) {
                    return;
                }
                bean.isDelete = false;
                bean.updateAccountId = account.id;
                bean.isCreateIndex = false;
                dao.updateSpecialFields(bean, "isDelete", "updateAccountId", "isCreateIndex");
                bizService.addChangeLog(account, bean.projectId, 0, ChangeLog.TYPE_恢复WIKIPAGE,
                        JSONUtil.toJson(WikiPageSimpleInfo.create(bean)));
            } else if (old.type == CompanyRecycle.TYPE_迭代) {
                ProjectIteration bean = dao.getExistedByIdForUpdate(ProjectIteration.class, old.associatedId);
                if (!bean.isDelete) {
                    return;
                }
                bean.isDelete = false;
                bean.name = getProjectIterationRestoreName(bean.projectId, bean.originalName);
                bean.updateAccountId = account.id;
                dao.updateSpecialFields(bean, "isDelete", "name", "updateAccountId");
                bizService.addChangeLog(account, bean.projectId, 0, ChangeLog.TYPE_恢复迭代, JSONUtil.toJson(bean));
            } else if (old.type == CompanyRecycle.TYPE_Release) {
                ProjectRelease bean = dao.getExistedByIdForUpdate(ProjectRelease.class, old.associatedId);
                if (!bean.isDelete) {
                    return;
                }
                bean.isDelete = false;
                bean.name = getProjectReleaseRestoreName(bean.projectId, bean.originalName);
                bean.updateAccountId = account.id;
                dao.updateSpecialFields(bean, "isDelete", "name", "updateAccountId");
                bizService.addChangeLog(account, bean.projectId, 0, ChangeLog.TYPE_恢复Release, JSONUtil.toJson(bean));
            } else if (old.type == CompanyRecycle.TYPE_子系统) {
                ProjectSubSystem bean = dao.getExistedByIdForUpdate(ProjectSubSystem.class, old.associatedId);
                if (!bean.isDelete) {
                    return;
                }
                bean.isDelete = false;
                bean.name = getProjectSubSystemRestoreName(bean.projectId, bean.originalName);
                bean.updateAccountId = account.id;
                dao.updateSpecialFields(bean, "isDelete", "name", "updateAccountId");
                bizService.addChangeLog(account, bean.projectId, 0, ChangeLog.TYPE_恢复子系统, JSONUtil.toJson(bean));
            } else if (old.type == CompanyRecycle.TYPE_主机) {
                Machine bean = dao.getExistedByIdForUpdate(Machine.class, old.associatedId);
                if (!bean.isDelete) {
                    return;
                }
                bean.isDelete = false;
                bean.name = getMachineRestoreName(bean.projectId, bean.name);
                bean.updateAccountId = account.id;
                dao.updateSpecialFields(bean, "isDelete", "updateAccountId");
                bizService.addChangeLog(account, bean.projectId, 0, ChangeLog.TYPE_恢复主机, JSONUtil.toJson(bean));
            } else if (old.type == CompanyRecycle.TYPE_汇报) {
                Report report = dao.getExistedByIdForUpdate(Report.class, old.associatedId);
                report.isDelete = false;
                report.updateAccountId = account.id;
                dao.updateSpecialFields(report, "isDelete", "updateAccountId");
                bizService.addChangeLog(account, report.projectId, 0, ChangeLog.TYPE_恢复汇报, JSONUtil.toJson(report));
            } else {
                throw new AppException("类型错误" + old.type);
            }
            bizService.addOptLog(account, id, old.name,
                    OptLog.EVENT_ID_恢复回收站数据, "名称:" + old.name + ",类型:" + old.objectTypeName);
        }

        private void deleteCompanyRecycle(Account account, int id) {
            CompanyRecycleInfo old = dao.getExistedByIdForUpdate(CompanyRecycleInfo.class, id);
            bizService.checkCompanyPermission(account, Permission.ID_管理回收站, old.companyId);
            dao.deleteById(CompanyRecycle.class, id);
            bizService.addOptLog(account, id, old.name,
                    OptLog.EVENT_ID_删除回收站数据, "名称:" + old.name + ",类型:" + old.objectTypeName);
        }

        @Override
        @Transaction
        public void emptyCompanyRecycle(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            CompanyRecycleQuery query = new CompanyRecycleQuery();
            query.companyId = account.companyId;
            query.pageSize = 10000;
            List<Integer> list = dao.getIdList(CompanyRecycle.class, query);
            for (Integer id : list) {
                deleteCompanyRecycle(account, id);
            }
        }

        private String getProjectIterationRestoreName(int projectId, String name) {
            ProjectIteration old = dao.getProjectIterationByProjectIdName(projectId, name);
            if (old == null) {
                return name;
            }
            for (int i = 1; i <= 1000; i++) {
                String newName = "(恢复" + i + ")" + name;
                old = dao.getProjectIterationByProjectIdName(projectId, newName);
                if (old == null) {
                    return newName;
                }
            }
            throw new AppException("复制失败,名称重复");
        }

        private String getProjectReleaseRestoreName(int projectId, String name) {
            ProjectRelease old = dao.getProjectReleaseByProjectIdName(projectId, name);
            if (old == null) {
                return name;
            }
            for (int i = 1; i <= 1000; i++) {
                String newName = "(恢复" + i + ")" + name;
                old = dao.getProjectReleaseByProjectIdName(projectId, newName);
                if (old == null) {
                    return newName;
                }
            }
            throw new AppException("复制失败,名称重复");
        }

        private String getProjectSubSystemRestoreName(int projectId, String name) {
            ProjectSubSystem old = dao.getProjectSubSystemByProjectIdName(projectId, name);
            if (old == null) {
                return name;
            }
            for (int i = 1; i <= 1000; i++) {
                String newName = "(恢复" + i + ")" + name;
                old = dao.getProjectSubSystemByProjectIdName(projectId, newName);
                if (old == null) {
                    return newName;
                }
            }
            throw new AppException("复制失败,名称重复");
        }

        //
        @Transaction
        @Override
        public void deleteCompanyRecycle(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            CompanyRecycleInfo old = dao.getExistedByIdForUpdate(CompanyRecycleInfo.class, id);
            bizService.checkCompanyPermission(account, Permission.ID_管理回收站, old.companyId);
            dao.deleteById(CompanyRecycle.class, id);
            bizService.addOptLog(account, id, old.name,
                    OptLog.EVENT_ID_删除回收站数据, "名称:" + old.name + ",类型:" + old.objectTypeName);
        }
        //

        /**
         * 通过ID查询cmdb主机
         */
        @Override
        public CmdbMachineInfo getCmdbMachineById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            return dao.getExistedCmdbMachineInfo(id, account.companyId);
        }

        /**
         * 查询cmdb主机列表和总数
         */
        @Override
        public Map<String, Object> getCmdbMachineList(String token, CmdbMachineQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            setupQuery(account, query);
            return createResult(dao.getList(query, "password"), dao.getListCount(query));
        }

        private void checkCmdbMachineValid(CmdbMachineInfo bean) {
            if (bean.loginType != MachineInfo.LOGINTYPE_证书登录) {
                if (StringUtil.isEmpty(bean.password)) {
                    throw new AppException("密码不能为空");
                }
            } else {
                if (StringUtil.isEmpty(bean.privateKey)) {
                    throw new AppException("私钥不能为空");
                }
                if (StringUtil.isEmpty(bean.publicKey)) {
                    throw new AppException("公钥不能为空");
                }
            }
        }

        /**
         * 新增cmdb主机
         */
        @Transaction
        @Override
        public int addCmdbMachine(String token, CmdbMachineInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            bean.companyId = account.companyId;
            bean.uuid = BizUtil.randomUUID();
            bean.createAccountId = account.id;
            checkCmdbMachineValid(bean);
            BizUtil.checkUniqueKeysOnAdd(dao, bean);
            BizUtil.checkValid(bean);
            if (bean.loginType != CmdbMachine.LOGINTYPE_证书SSH) {
                bean.password = TripleDESUtil.encrypt(bean.password, ConstDefine.GLOBAL_KEY);
            }
            return dao.add(bean);
        }

        /**
         * 编辑cmdb主机
         */
        @Transaction
        @Override
        public void updateCmdbMachine(String token, CmdbMachineInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            CmdbMachine old = dao.getExistedByIdForUpdate(CmdbMachine.class, bean.id);
            bizService.checkPermission(account, old.companyId);
            checkCmdbMachineValid(bean);
            BizUtil.checkUniqueKeysOnUpdate(dao, bean, old);
            old.group = bean.group;
            old.innerHost = bean.innerHost;
            old.outerHost = bean.outerHost;
            old.loginType = bean.loginType;
            old.name = bean.name;
            old.userName = bean.userName;
            if (bean.loginType != CmdbMachine.LOGINTYPE_证书SSH) {
                if (old.password != null && !bean.password.equals(old.password)) {
                    old.password = TripleDESUtil.encrypt(bean.password, ConstDefine.GLOBAL_KEY);
                }
            } else {
                old.privateKey = bean.privateKey;
                old.publicKey = bean.publicKey;
            }
            old.port = bean.port;
            old.remark = bean.remark;
            old.properties = bean.properties;
            old.runStatus = bean.runStatus;
            old.updateAccountId = account.id;
            BizUtil.checkValid(old);
            dao.update(old);
            //同步修改Machine
            List<MachineInfo> machines = dao.getMachineListByCmdbMachineId(bean.id);
            for (MachineInfo machineInfo : machines) {
                if (machineInfo.isDelete) {
                    continue;
                }
                machineInfo.host = old.outerHost;
                machineInfo.loginType = old.loginType;
                machineInfo.name = old.name;
                if (old.password != null) {
                    machineInfo.password = TripleDESUtil.decrypt(old.password, ConstDefine.GLOBAL_KEY);
                }
                machineInfo.port = old.port;
                machineInfo.privateKey = old.privateKey;
                machineInfo.publicKey = old.publicKey;
                machineInfo.userName = old.userName;
                updateMachine0(account, machineInfo);
            }
        }

        /**
         * 删除cmdb主机
         */
        @Transaction
        @Override
        public void deleteCmdbMachine(String token, int id, String reason) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            CmdbMachine old = dao.getExistedByIdForUpdate(CmdbMachine.class, id);
            bizService.checkPermission(account, old.companyId);
            dao.deleteById(CmdbMachine.class, id);
        }
        //

        /**
         * 通过ID查询Application
         */
        @Override
        public CmdbApplicationInfo getCmdbApplicationById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            return dao.getExistedCmdbApplicationInfo(id, account.companyId);
        }

        /**
         * 查询Application列表和总数
         */
        @Override
        public Map<String, Object> getCmdbApplicationList(String token, CmdbApplicationQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            setupQuery(account, query);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        /**
         * 查询Application列表
         */
        @Override
        public List<CmdbApplicationInfo> getCmdbApplicationInfoList(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            CmdbApplicationQuery query = new CmdbApplicationQuery();
            setupQuery(account, query);
            return dao.getAll(query);
        }

        /**
         * 新增Application
         */
        @Transaction
        @Override
        public int addCmdbApplication(String token, CmdbApplicationInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            bean.companyId = account.companyId;
            bean.createAccountId = account.id;
            BizUtil.checkUniqueKeysOnAdd(dao, bean);
            BizUtil.checkValid(bean);
            return dao.add(bean);
        }

        /**
         * 编辑Application
         */
        @Transaction
        @Override
        public void updateCmdbApplication(String token, CmdbApplicationInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            CmdbApplication old = dao.getExistedByIdForUpdate(CmdbApplication.class, bean.id);
            bizService.checkPermission(account, old.companyId);
            BizUtil.checkUniqueKeysOnUpdate(dao, bean, old);
            old.name = bean.name;
            old.depends = bean.depends;
            old.type = bean.type;
            old.group = bean.group;
            old.priority = bean.priority;
            old.properties = bean.properties;
            old.remark = bean.remark;
            old.updateAccountId = account.id;
            BizUtil.checkValid(old);
            dao.update(old);
        }

        /**
         * 删除Application
         */
        @Transaction
        @Override
        public void deleteCmdbApplication(String token, int id, String reason) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            CmdbApplication old = dao.getExistedByIdForUpdate(CmdbApplication.class, id);
            bizService.checkPermission(account, old.companyId);
            dao.deleteById(CmdbApplication.class, id);
        }
        //

        /**
         * 通过ID查询实例
         */
        @Override
        public CmdbInstanceInfo getCmdbInstanceById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            return dao.getExistedCmdbInstanceInfo(id, account.companyId);
        }

        /**
         * 查询实例列表和总数
         */
        @Override
        public Map<String, Object> getCmdbInstanceList(String token, CmdbInstanceQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            setupQuery(account, query);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        /**
         * 新增实例
         */
        @Transaction
        @Override
        public int addCmdbInstance(String token, CmdbInstanceInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            CmdbMachine machine = dao.getExistedById(CmdbMachine.class, bean.machineId);
            CmdbApplication application = dao.getExistedById(CmdbApplication.class, bean.applicationId);
            bizService.checkSameCompany(account.companyId, machine.companyId);
            bizService.checkSameCompany(account.companyId, application.companyId);
            bean.companyId = account.companyId;
            if (!StringUtil.isEmpty(bean.password)) {
                bean.password = TripleDESUtil.encrypt(bean.password, ConstDefine.GLOBAL_KEY);
            }
            bean.createAccountId = account.id;
            BizUtil.checkUniqueKeysOnAdd(dao, bean);
            BizUtil.checkValid(bean);
            return dao.add(bean);
        }

        /**
         * 批量编辑实例
         */
        @Transaction
        @Override
        public void batchUpdateCmdbInstance(String token, Set<Integer> idList, CmdbInstanceInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            if (idList == null || idList.size() == 0) {
                return;
            }
            for (Integer id : idList) {
                CmdbInstance old = dao.getExistedByIdForUpdate(CmdbInstance.class, id);
                bizService.checkPermission(account, old.companyId);
                //
                if (!StringUtil.isEmptyWithTrim(bean.name)) {
                    old.name = bean.name;
                }
                if (bean.port > 0) {
                    old.port = bean.port;
                }
                if (!StringUtil.isEmptyWithTrim(bean.packageVersion)) {
                    old.packageVersion = bean.packageVersion;
                }
                if (!StringUtil.isEmptyWithTrim(bean.group)) {
                    old.group = bean.group;
                }
                if (bean.priority > 0) {
                    old.priority = bean.priority;
                }
                if (bean.properties != null) {
                    old.properties = bean.properties;
                }
                if (!StringUtil.isEmptyWithTrim(bean.remark)) {
                    old.remark = bean.remark;
                }
                if (!StringUtil.isEmptyWithTrim(bean.user)) {
                    old.user = bean.user;
                }
                if (bean.machineId > 0) {
                    old.machineId = bean.machineId;
                }
                if (bean.applicationId > 0) {
                    old.applicationId = bean.applicationId;
                }
                if (!StringUtil.isEmpty(bean.password)) {
                    if (StringUtil.isEmpty(old.password)) {
                        old.password = TripleDESUtil.encrypt(bean.password, ConstDefine.GLOBAL_KEY);
                    } else {
                        if (!bean.password.equals(old.password)) {
                            old.password = TripleDESUtil.encrypt(bean.password, ConstDefine.GLOBAL_KEY);
                        }
                    }
                }
                old.updateAccountId = account.id;
                BizUtil.checkValid(old);
                dao.update(old);
            }
        }

        /**
         * 编辑实例
         */
        @Transaction
        @Override
        public void updateCmdbInstance(String token, CmdbInstanceInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            CmdbInstance old = dao.getExistedByIdForUpdate(CmdbInstance.class, bean.id);
            bizService.checkPermission(account, old.companyId);
            BizUtil.checkUniqueKeysOnUpdate(dao, bean, old);
            old.name = bean.name;
            old.port = bean.port;
            old.packageVersion = bean.packageVersion;
            old.group = bean.group;
            old.priority = bean.priority;
            old.properties = bean.properties;
            old.remark = bean.remark;
            old.user = bean.user;
            old.machineId = bean.machineId;
            old.applicationId = bean.applicationId;
            if (!StringUtil.isEmpty(bean.password)) {
                if (StringUtil.isEmpty(old.password)) {
                    old.password = TripleDESUtil.encrypt(bean.password, ConstDefine.GLOBAL_KEY);
                } else {
                    if (!bean.password.equals(old.password)) {
                        old.password = TripleDESUtil.encrypt(bean.password, ConstDefine.GLOBAL_KEY);
                    }
                }
            } else {
                old.password = bean.password;
            }
            old.updateAccountId = account.id;
            BizUtil.checkValid(old);
            dao.update(old);
        }

        /**
         * 删除实例
         */
        @Transaction
        @Override
        public void deleteCmdbInstance(String token, int id, String reason) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            CmdbInstance old = dao.getExistedByIdForUpdate(CmdbInstance.class, id);
            bizService.checkPermission(account, old.companyId);
            dao.deleteById(CmdbInstance.class, id);
        }

        //
        @Override
        public CmdbApiInfo getCmdbApiById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            CmdbApiInfo bean = dao.getExistedById(CmdbApiInfo.class, id);
            bizService.checkPermission(account, bean.companyId);
            return bean;
        }

        @Override
        public Map<String, Object> getCmdbApiList(String token, CmdbApiQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            setupQuery(account, query);
            return createResult(dao.getList(query, "code"), dao.getListCount(query));
        }

        @Transaction
        @Override
        public int addCmdbApi(String token, CmdbApiInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            bean.companyId = account.companyId;
            bean.createAccountId = account.id;
            bean.apiKey = BizUtil.randomUUID();
            BizUtil.checkUniqueKeysOnAdd(dao, bean, "API名称不能重复");
            BizUtil.checkValid(bean);
            return dao.add(bean);
        }

        @Transaction
        @Override
        public void reloadCmdbApiKey(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            CmdbApi old = dao.getExistedByIdForUpdate(CmdbApi.class, id);
            bizService.checkPermission(account, old.companyId);
            old.apiKey = BizUtil.randomUUID();
            dao.update(old);
        }

        @Transaction
        @Override
        public void updateCmdbApi(String token, CmdbApiInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            CmdbApi old = dao.getExistedByIdForUpdate(CmdbApi.class, bean.id);
            bizService.checkPermission(account, old.companyId);
            addBackup(old.id, Backup.TYPE_cmdbApi, account.id, old.name, JSONUtil.toJson(old));
            //
            BizUtil.checkUniqueKeysOnUpdate(dao, bean, old, "API名称不能重复");
            old.remark = bean.remark;
            old.name = bean.name;
            old.code = bean.code;
            BizUtil.checkValid(old);
            dao.update(old);
        }

        @Transaction
        @Override
        public void deleteCmdbApi(String token, int id, String reason) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            CmdbApi old = dao.getExistedByIdForUpdate(CmdbApi.class, id);
            bizService.checkPermission(account, old.companyId);
            dao.deleteById(CmdbApi.class, id);
        }

        //

        /**
         * 通过ID查询cmdb robot
         */
        @Override
        public CmdbRobotInfo getCmdbRobotById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            CmdbRobotInfo bean = dao.getExistedById(CmdbRobotInfo.class, id);
            bizService.checkPermission(account, bean.companyId);
            return bean;
        }

        /**
         * 查询cmdb robot列表和总数
         */
        @Override
        public Map<String, Object> getCmdbRobotList(String token, CmdbRobotQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            setupQuery(account, query);
            return createResult(dao.getList(query, "code"), dao.getListCount(query));
        }

        /**
         * 新增cmdb robot
         */
        @Transaction
        @Override
        public int addCmdbRobot(String token, CmdbRobotInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            bean.companyId = account.companyId;
            bean.createAccountId = account.id;
            bean.uuid = BizUtil.randomUUID();
            bean.lastRunTime = null;
            bean.nextRunTime = BizUtil.nextRunTime(null, bean.cron);
            BizUtil.checkUniqueKeysOnAdd(dao, bean, "Robot名称不能重复");
            BizUtil.checkValid(bean);
            return dao.add(bean);
        }

        /**
         * 编辑cmdb robot
         */
        @Transaction
        @Override
        public void updateCmdbRobot(String token, CmdbRobotInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            CmdbRobot old = dao.getExistedByIdForUpdate(CmdbRobot.class, bean.id);
            bizService.checkPermission(account, old.companyId);
            //
            addBackup(old.id, Backup.TYPE_cmdb机器人, account.id, old.name, JSONUtil.toJson(old));
            //
            BizUtil.checkUniqueKeysOnUpdate(dao, bean, old, "Robot名称不能重复");
            old.remark = bean.remark;
            old.name = bean.name;
            old.code = bean.code;
            old.nextRunTime = BizUtil.nextRunTime(old.lastRunTime, bean.cron);
            old.cron = bean.cron;
            old.machineList = bean.machineList;
            BizUtil.checkValid(old);
            dao.update(old);
        }

        private void addBackup(int associateId, int type, int createAccountId, String name, String content) {
            Backup backup = new Backup();
            backup.associateId = associateId;
            backup.type = type;
            backup.createAccountId = createAccountId;
            backup.name = name;
            backup.content = content;
            dao.add(backup);
        }

        /**
         * 删除cmdb robot
         */
        @Transaction
        @Override
        public void deleteCmdbRobot(String token, int id, String reason) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看CMDB);
            CmdbRobot old = dao.getExistedByIdForUpdate(CmdbRobot.class, id);
            bizService.checkPermission(account, old.companyId);
            dao.deleteById(CmdbRobot.class, id);
        }

        //

        /**
         * 通过ID查询对象类型
         */
        @Override
        public ObjectTypeInfo getObjectTypeById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkIsPrivateDeploy(account);
            ObjectTypeInfo info = dao.getExistedById(ObjectTypeInfo.class, id);
            ObjectTypeFieldDefineQuery query = new ObjectTypeFieldDefineQuery();
            query.objectType = id;
            query.sortWeightSort = Query.SORT_TYPE_ASC;
            info.fieldDefineList = dao.getAll(query);
            return info;
        }

        @Override
        public List<ObjectTypeFieldDefine> getObjectTypeSystemFieldDefineList(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkIsPrivateDeploy(account);
            ObjectTypeFieldDefineQuery query = new ObjectTypeFieldDefineQuery();
            query.objectType = 0;
            query.sortWeightSort = Query.SORT_TYPE_ASC;
            return dao.getAll(query);
        }

        @Transaction
        @Override
        public void saveObjectType(String token, ObjectType bean, List<ObjectTypeFieldDefine> fieldList) {
            saveObjectType0(token, bean, fieldList);
        }

        //解决同时点两次的问题
        private synchronized void saveObjectType0(String token, ObjectType bean, List<ObjectTypeFieldDefine> fieldList) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkIsPrivateDeploy(account);
            bizService.checkCompanyPermission(account, Permission.ID_系统设置);
            BizUtil.asserts(!BizUtil.isNullOrEmpty(bean.name),"对象类型名称不能为空");
            if (bean.id == 0) {//新增
                BizUtil.checkValid(bean);
                BizUtil.checkUniqueKeysOnAdd(dao, bean);
                bean.createAccountId = account.id;
                bean.isSystemDefault = false;
                bean.useDefaultSerialNo = true;
                dao.add(bean);
                //
                List<String> permissions = addObjectTypePermissions(bean);
                //项目管理员默认都有权限
                Role role = dao.getRoleByCompanyIdTypeNameForUpdate(account.companyId, Role.TYPE_项目, Role.NAME_项目管理员);
                if (role != null) {
                    role.permissionIds.addAll(permissions);
                    role.permissionIds = BizUtil.convertTreeSet(role.permissionIds);
                    dao.update(role);
                }
                addDataDict(bean);
                bizService.addOptLog(account, bean.id, bean.name,
                        OptLog.EVENT_ID_创建对象类型, "名称:" + bean.name);
            } else {
                ObjectType old = dao.getExistedByIdForUpdate(ObjectType.class, bean.id);
                boolean changeName = !old.name.equals(bean.name);
                old.name = bean.name;
                old.group = bean.group;
                old.remark = bean.remark;
                BizUtil.checkValid(bean);
                dao.update(old);
                //更新task_21的名称
                if (changeName) {
                    Permission permission = dao.getExistedByIdForUpdate(Permission.class, Permission.ID_TASK + bean.id);
                    permission.name = bean.name;
                    dao.update(permission);
                    //更新对象模块的名称
                    dao.updateProjectModuleName(bean.id, bean.name);

                    updateDataDict(bean);
                }

                //
                dao.deleteObjectTypeFieldDefinesByObjectType(bean.id);
                bizService.addOptLog(account, bean.id, bean.name,
                        OptLog.EVENT_ID_编辑对象类型, "名称:" + bean.name);
            }
            int sortWeight = 1;
            for (ObjectTypeFieldDefine e : fieldList) {
                e.sortWeight = sortWeight++;
                e.objectType = bean.id;
                BizUtil.checkValid(e);
                BizUtil.checkUniqueKeysOnAdd(dao, e);
                dao.add(e);
            }
        }

        /**
         * batchCopyTask
         * 只能删除非系统默认的
         */
        @Transaction
        @Override
        public void deleteObjectType(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkIsPrivateDeploy(account);
            bizService.checkCompanyPermission(account, Permission.ID_系统设置);
            ObjectType old = dao.getExistedById(ObjectType.class, id);
            if (old.isSystemDefault) {
                throw new AppException("系统默认的对象类型不能删除");
            }
            ProjectModuleQuery query = new ProjectModuleQuery();
            query.objectType = id;
            query.projectIsDelete = false;
            int count = dao.getListCount(query);
            if (count > 0) {
                throw new AppException("对象类型【" + old.name + "】被" + count + "个项目关联，不能删除");
            }
            dao.deleteById(ObjectType.class, id);
            //删除对应权限
            Company company = dao.getExistedById(Company.class, account.companyId);
            List<Permission> permissions = dao.getPermissionList(Permission.TYPE_项目, company.version);
            for (Permission e : permissions) {
                if (e.id.startsWith("task_")) {//
                    int eObjectType = Integer.parseInt(e.id.substring(e.id.lastIndexOf("_") + 1));
                    if (eObjectType == id) {
                        dao.delete(Permission.class, QueryWhere.create().where("id", e.id));
                    }
                }
            }
            //
            bizService.addOptLog(account, old.id, old.name,
                    OptLog.EVENT_ID_删除对象类型, "名称:" + old.name);
        }

        private void addDataDict(ObjectType bean) {
            DataDict dataDict = new DataDict();
            dataDict.categoryCode = "Task.objectType";
            dataDict.name = bean.name;
            dataDict.value = bean.id;
            dao.addNotWithGenerateKey(dataDict);
            CornerstoneBizSystem.get().bizTaskJobs.reloadGlobalData0();
        }

        private void updateDataDict(ObjectType bean) {
            DataDict dataDict = new DataDict();
            dataDict.categoryCode = "Task.objectType";
            dataDict.name = bean.name;
            dataDict.value = bean.id;
            dao.updateDataDictName(dataDict);
            CornerstoneBizSystem.get().bizTaskJobs.reloadGlobalData0();
        }

        /***/
        private List<String> addObjectTypePermissions(ObjectType bean) {
            List<String> permissionIdList = new ArrayList<>();
            for (Permission p : Permission.TASK_PERMISSION_MAP.values()) {
                Permission clone = BeanUtil.copyTo(Permission.class, p);
                if ("task_".equals(clone.id)) {
                    clone.name = bean.name;
                }
                if (clone.id.startsWith("task_")) {
                    clone.id = clone.id + bean.id;
                    if (clone.parentId != null) {
                        clone.parentId = p.parentId + bean.id;
                    }
                }
                clone.objectType = bean.id;
                dao.addNotWithGenerateKey(clone);
                permissionIdList.add(clone.id);
            }
            return permissionIdList;
        }

        /**
         * 查询对象类型列表和总数
         */
        @Override
        public Map<String, Object> getObjectTypeList(String token, ObjectTypeQuery query) {
            bizService.getExistedAccountByToken(token);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        /**
         *
         */
        @Override
        public String generateQRCode(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            try {
                weixinService.refreshAccessToken();
                return weixinService.generateQRCode(account.id, 3600, account.uuid);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new AppException("生成微信二维码失败，请检查微信配置是否正确");
            }

        }

        @Override
        public boolean isWeixinBind(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            return !StringUtil.isEmpty(account.wxOpenId);
        }

        @Override
        public String createWxOauthState() {
            String state = UUID.randomUUID().toString().replaceAll("-", "");
            WxOauth bean = new WxOauth();
            bean.state = state;
            dao.add(bean);
            return state;
        }

        @Override
        public String wxPageLogin(String code) {
            WeixinOAuthToken token = weixinService.getOAuthToken(code);
            weixinService.refreshAccessToken();
            WeixinAccount wxAccount = weixinService.getUserInfo(token.openId);
            if (wxAccount.subscribe == 0) {
                return null;
            }
            Account account = dao.getAccountByWxOpenIdForUpdate(wxAccount.openid);
            if (account == null) {
                return null;
            }
            //
            AccountToken accountToken = dao.getAccountTokenByAccountId(account.id);
            if (accountToken == null) {
                return null;
            }
            return accountToken.token;
        }

        @Transaction
        @Override
        public String wxLogin(String code, String state) {
            WxOauth oauth = dao.getWxOauthByStateForUpdate(state);
            if (oauth == null) {
                return null;
            }
            if (!StringUtil.isEmpty(oauth.token)) {
                return null;
            }
            WeixinOAuthToken token = weixinService.getOAuthToken(code);
            weixinService.refreshAccessToken();
            WeixinAccount wxAccount = weixinService.getUserInfo(token.openId);
            if (wxAccount.subscribe == 0) {
                return null;
            }
            Account account = dao.getAccountByWxOpenIdForUpdate(wxAccount.openid);
            if (account == null) {
                return null;
            }
            oauth.openId = token.openId;
            oauth.accountId = account.id;
            dao.update(oauth);
            //
            AccountToken accountToken = dao.getAccountTokenByAccountId(account.id);
            if (accountToken == null) {
                return null;
            }
            //
            return accountToken.token;
        }

        @Transaction
        @Override
        public void wxConfirmLogin(String state) {
            WxOauth oauth = dao.getWxOauthByStateForUpdate(state);
            if (oauth == null) {
                return;
            }
            if (oauth.accountId == 0) {
                return;
            }
            Account account = dao.getExistedById(Account.class, oauth.accountId);
            LoginResult result = new LoginResult();
            boolean isOk = checkLogin(result, account);
            if (!isOk) {
                logger.warn("result:{}", DumpUtil.dump(result));
                return;
            }
            AccountToken accountToken = loginSuccess(account);
            oauth.token = accountToken.token;
            dao.update(oauth);
            //
            bizService.addOptLog(account, 0, "", OptLog.EVENT_ID_微信扫码登录, "");
        }

        @Override
        public WxOauth getWxOauth(String state) {
            return dao.getWxOauthByStateForUpdate(state);
        }

        @Transaction
        @Override
        public void disableAccount(String token, int accountId) {
            Account optAccount = bizService.getExistedAccountByToken(token);
            if (!bizService.isPrivateDeploy(optAccount)) {
                throw new AppException("权限不足");
            }
            Account account = dao.getExistedByIdForUpdate(Account.class, accountId);
            account.status = Account.STATUS_无效;
            dao.update(account);
            //重置TOKEN
            bizService.cleanToken(accountId);
            //禁用供应商成员
            dao.updateSupplierMemberStatus(account);
            bizService.addOptLog(optAccount, account.id, account.name, OptLog.EVENT_ID_禁用账户, "name:" + account.name);

        }

        @Transaction
        @Override
        public void enableAccount(String token, int accountId, int type) {
            Account optAccount = bizService.getExistedAccountByToken(token);
            if (!bizService.isPrivateDeploy(optAccount)) {
                throw new AppException("权限不足");
            }
            //部门 角色信息
            CompanyMemberRole.CompanyMemberRoleQuery query = new CompanyMemberRole.CompanyMemberRoleQuery();
            setupQuery(optAccount, query);
            query.accountId = accountId;
            int role = dao.getListCount(query);
            if (role <= 0) {
                throw new AppException("请先设置该账号的企业角色类型");
            }
            DepartmentQuery departmentQuery = new DepartmentQuery();
            setupQuery(optAccount, departmentQuery);
            departmentQuery.accountId = accountId;
            int department = dao.getListCount(departmentQuery);
            if (department <= 0) {
                throw new AppException("请先设置该账号的的组织部门信息");
            }
            Account account = dao.getExistedByIdForUpdate(Account.class, accountId);
            account.status = Account.STATUS_有效;
            //不影响active的其它账号逻辑，限定为ad域账号
            if (!BizUtil.isNullOrEmpty(account.adName) && type == 1) {
                account.isActivated = true;
            }
            dao.update(account);
            //
            dao.updateSupplierMemberStatus(account);
            bizService.addOptLog(optAccount, account.id, account.name, OptLog.EVENT_ID_启用账户, "name:" + account.name);
        }

        @Override
        @Transaction
        public String resetAccountPassword(String token, int accountId) {
            Account optAccount = bizService.getExistedAccountByToken(token);
            if (!bizService.isPrivateDeploy(optAccount)) {
                throw new AppException("权限不足");
            }
            Account account = dao.getExistedByIdForUpdate(Account.class, accountId);
            String newPassword = BizUtil.randomPassword(10);
            account.password = BizUtil.encryptPassword(newPassword);
            account.encryptPassword = TripleDESUtil.encrypt(newPassword, ConstDefine.GLOBAL_KEY);
            account.dailyLoginFailCount = 0;
            dao.update(account);
            //
            bizService.cleanToken(account.id);
            //
            bizService.addOptLog(optAccount, account.id, account.name, OptLog.EVENT_ID_强制修改密码, "name:" + account.name);
            //
            if (ldapService != null) {
                ldapService.addAccount(account.userName, account.password);
            }
            //
            return newPassword;
        }

        @Override
        public Map<String, Object> getProjectArtifactList(String token, ProjectArtifactQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            setupQuery(account, query);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        @Override
        public ProjectArtifactInfo getProjectArtifactInfoById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectArtifactInfo bean = dao.getExistedById(ProjectArtifactInfo.class, id);
            bizService.checkPermissionForProjectAccess(account, bean.projectId);
            return bean;
        }

        @Override
        public ProjectArtifactInfo getProjectArtifactInfoByUuid(String token, String uuid) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectArtifactInfo bean = dao.getExistedProjectArtifactInfoByUuid(uuid);
            bizService.checkPermissionForProjectAccess(account, bean.projectId);
            return bean;
        }

        //
        public void initAccountNotificationSettingList(Account account) {
            List<DataDict> dataDicts = dao.getDataDictList("AccountNotificationSetting.type");
            for (DataDict dataDict : dataDicts) {
                AccountNotificationSetting bean = dao.getAccountNotificationSetting(account.id, dataDict.value);
                if (bean == null) {
                    bean = new AccountNotificationSetting();
                    bean.accountId = account.id;
                    bean.type = dataDict.value;
                    bean.isEmailEnable = (!GlobalConfig.isMailNotificationAutoDisabled);
                    bean.isWebEnable = true;
                    bean.isWeixinEnable = (!GlobalConfig.isWechatNotificationAutoDisabled);
                    dao.add(bean);
                }
            }
        }
        //

        /**
         * 查询用户通知配置列表和总数
         */
        @Override
        public List<AccountNotificationSetting> getAccountNotificationSettingList(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            initAccountNotificationSettingList(account);
            //
            AccountNotificationSettingQuery query = new AccountNotificationSettingQuery();
            query.accountId = account.id;
            query.pageSize = Integer.MAX_VALUE;
            query.typeSort = Query.SORT_TYPE_ASC;
            return dao.getList(query);
        }

        @Transaction
        @Override
        public void saveAccountNotificationSetting(String token, List<AccountNotificationSettingInfo> bean) {
            Account account = bizService.getExistedAccountByToken(token);
            for (AccountNotificationSettingInfo e : bean) {
                AccountNotificationSetting old = dao.getExistedByIdForUpdate(AccountNotificationSetting.class, e.id);
                if (old.accountId != account.id) {
                    throw new AppException("权限不足");
                }
                old.isEmailEnable = e.isEmailEnable;
                old.isWebEnable = e.isWebEnable;
                old.isWeixinEnable = e.isWeixinEnable;
                old.isLarkEnable = e.isLarkEnable;
                old.isDingtalkEnable = e.isDingtalkEnable;
                dao.update(old);
            }
        }

        @Transaction
        @Override
        public String getAccountCalUuid(String token) {
            Account account = bizService.getExistedAccountByTokenForUpdate(token);
            if (StringUtil.isEmpty(account.calUuid)) {
                account.calUuid = BizUtil.randomUUID();
                dao.update(account);
            }
            return account.calUuid;
        }

        @Transaction
        @Override
        public String refreshAccountCalUuid(String token) {
            Account account = bizService.getExistedAccountByTokenForUpdate(token);
            account.calUuid = BizUtil.randomUUID();
            dao.updateSpecialFields(account, "calUuid");
            return account.calUuid;
        }

        @Transaction
        @Override
        public String refreshAccessToken(String token) {
            Account account = bizService.getExistedAccountByTokenForUpdate(token);
            String accessToken = null;
            for (int i = 0; i < 100; i++) {
                accessToken = BizUtil.randomAccessToken();
                if (!dao.existedAccessToken(accessToken)) {
                    break;
                }
            }
            account.accessToken = accessToken;
            dao.updateSpecialFields(account, "accessToken");
            return account.accessToken;
        }


        @Override
        public List<File> getFileList(String token, Set<String> fileUuidList) {
            List<File> list = new ArrayList<>();
            bizService.getExistedAccountByToken(token);
            fileUuidList.forEach(uuid -> {
                File file = getFileByUuid(token, uuid);
                list.add(file);
            });
            return list;
        }

        //下载多个文件
        @Override
        public List<FileTree> getFileTree(String token, Set<String> fileUuidList) {
            Account account = bizService.getExistedAccountByToken(token);
            //权限
            List<FileTree> list = new ArrayList<>();
            int projectId = 0;
            for (String uuid : fileUuidList) {
                FileInfo old = dao.getFileInfoByUuid(uuid);
                if (old == null) {
                    throw new AppException("文件不存在");
                }
                if (projectId == 0) {
                    projectId = old.projectId;
                } else {
                    if (projectId != old.projectId) {//确保都是一个项目下的文件
                        logger.error("is qainduan bug projectId!=fileInfo.projectId {}", uuid);
                        throw new AppException("文件不存在");
                    }
                }
                if (old.isDirectory) {
                    if (!bizService.checkAccountProjectPermission(account.id, old.projectId, old.enableRole,
                            old.createAccountId, old.roles, null)) {
                        throw new IllegalArgumentException("权限不足");
                    }
                }
                FileTree tree = FileTree.createFileTree(old);
                findFiles(tree);
                list.add(tree);
            }
            //
            if (projectId > 0) {
                bizService.checkProjectPermission(account, projectId, Permission.ID_下载文件);
            }
            //
            return list;
        }

        //
        private void findFiles(FileTree tree) {
            if (tree.isDirectory) {
                FileQuery fileQuery = new FileQuery();
                fileQuery.parentId = tree.id;
                fileQuery.isDelete = false;
                fileQuery.pageSize = Integer.MAX_VALUE;
                List<FileInfo> children = dao.getList(fileQuery);
                for (FileInfo fileInfo : children) {
                    FileTree child = FileTree.createFileTree(fileInfo);
                    tree.children.add(child);
                    findFiles(child);
                }
            }
        }

        @Override
        public void checkFileDownloadPermission(String token, String fileId) {
            Account account = bizService.getExistedAccountByToken(token);
            FileInfo old = dao.getFileInfoByUuid(fileId);
            if (old == null) {
                return;
            }
            if (old.isDirectory) {
                if (!bizService.checkAccountProjectPermission(account.id, old.projectId, old.enableRole,
                        old.createAccountId, old.roles, null)) {
                    throw new AppException("权限不足");
                }
            }
            bizService.checkProjectPermission(account, old.projectId, Permission.ID_下载文件);
        }

        //
        @Override
        public RsaKeyPair genKeyPair(String token) {
            return BizUtil.genKeyPair();
        }

        //
        @Override
        public ProjectObjectTypeTemplateInfo getProjectObjectTypeTemplateById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectObjectTypeTemplateInfo info = dao.getExistedById(ProjectObjectTypeTemplateInfo.class, id);
            bizService.checkPermission(account, info.companyId);
            return info;
        }

        @Override
        public ProjectObjectTypeTemplateInfo getProjectObjectTypeTemplateByProjectIdObjectType(String token, int projectId, int objectType) {
            bizService.getExistedAccountByToken(token);
            //			if(info!=null) {//有模板项目
///				bizService.checkPermission(account, info.companyId);
//			}
            return dao.getProjectObjectTypeTemplateByProjectIdObjectType(projectId, objectType);
        }

        @Override
        public List<ProjectObjectTypeTemplateInfo> getProjectObjectTypeTemplateList(String token, int projectId) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectObjectTypeTemplateQuery query = new ProjectObjectTypeTemplateQuery();
            query.projectId = projectId;
            setupQuery(account, query);
            query.pageSize = Integer.MAX_VALUE;
            return dao.getList(query);
        }

        @Transaction
        @Override
        public int saveProjectObjectTypeTemplate(String token, ProjectObjectTypeTemplateInfo bean) {
            if (bean.id == 0) {
                addProjectObjectTypeTemplate(token, bean);
            } else {
                updateProjectObjectTypeTemplate(token, bean);
            }
            return bean.id;
        }

        private int addProjectObjectTypeTemplate(String token, ProjectObjectTypeTemplateInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            Project project = dao.getExistedById(Project.class, bean.projectId);
            if (project.isTemplate) {//模板项目
                bizService.checkCompanyPermission(account, Permission.ID_设置企业信息);
            } else {
                bizService.checkProjectPermission(account, project.id, Permission.ID_修改项目设置);
            }
            dao.getExistedById(ObjectType.class, bean.objectType);
            BizUtil.checkUniqueKeysOnAdd(dao, bean);
            BizUtil.checkValid(bean);
            bean.content = bizService.getContent(bean.content);
            bean.companyId = project.companyId;
            bean.createAccountId = account.id;
            return dao.add(bean);
        }

        private void updateProjectObjectTypeTemplate(String token, ProjectObjectTypeTemplateInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectObjectTypeTemplate old = dao.getExistedByIdForUpdate(ProjectObjectTypeTemplate.class, bean.id);
            BizUtil.checkUniqueKeysOnUpdate(dao, bean, old);
            Project project = dao.getExistedById(Project.class, old.projectId);
            if (project.isTemplate) {//模板项目
                bizService.checkCompanyPermission(account, Permission.ID_设置企业信息);
            } else {
                bizService.checkProjectPermission(account, project.id, Permission.ID_修改项目设置);
            }
            old.name = bean.name;
            old.content = bizService.getContent(bean.content);
            old.updateAccountId = account.id;
            BizUtil.checkValid(old);
            dao.update(old);
        }

        @Transaction
        @Override
        public void deleteProjectObjectTypeTemplate(String token, int id, String reason) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectObjectTypeTemplate old = dao.getExistedByIdForUpdate(ProjectObjectTypeTemplate.class, id);
            bizService.checkPermission(account, old.companyId);
            dao.deleteById(ProjectObjectTypeTemplate.class, id);
        }

        //
        @Override
        public String gitlabWebhookToken(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            Company company = dao.getExistedById(Company.class, account.companyId);
            ScmToken scmToken = dao.getScmTokenByCompanyIdType(company.id, ScmToken.TYPE_GITLAB);
            if (scmToken == null) {
                scmToken = new ScmToken();
                scmToken.companyId = company.id;
                scmToken.type = ScmToken.TYPE_GITLAB;
                scmToken.token = BizUtil.randomUUID();
                dao.add(scmToken);
            }
            return scmToken.token;
        }

        //
        @Override
        public String githubWebhookToken(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            Company company = dao.getExistedById(Company.class, account.companyId);
            ScmToken scmToken = dao.getScmTokenByCompanyIdType(company.id, ScmToken.TYPE_GITHUB);
            if (scmToken == null) {
                scmToken = new ScmToken();
                scmToken.companyId = company.id;
                scmToken.type = ScmToken.TYPE_GITHUB;
                scmToken.token = BizUtil.randomUUID();
                dao.add(scmToken);
            }
            return scmToken.token;
        }

        //
        @Transaction
        @Override
        public void forceResetPassword(String token, int accountId) {
            Account optAccount = bizService.getExistedAccountByToken(token);
            bizService.checkIsCompanyAdmin(optAccount);
            Account account = dao.getExistedByIdForUpdate(Account.class, accountId);
            if (optAccount.companyId != account.companyId) {
                throw new AppException("权限不足");
            }
            if (account.needUpdatePassword) {
                return;
            }
            account.needUpdatePassword = true;
            dao.update(account);
        }


        private List<AccountSimpleInfo> getAccountSimpleInfoList(int companyId, List<Integer> accountIdList) {
            if (accountIdList == null || accountIdList.isEmpty()) {
                return new ArrayList<>();
            }
            List<AccountSimpleInfo> list = new ArrayList<>();
            Set<Integer> ids = BizUtil.convert(accountIdList);
            for (Integer accountId : ids) {
                AccountSimpleInfo ownerAccount = dao.getExistedAccountSimpleInfoById(accountId);
                if (ownerAccount.companyId != companyId && companyId > 0 && ownerAccount.companyId > 0) {
                    throw new AppException("权限不足");
                }
                if (ownerAccount.companyId <= 0) {
                    continue;
                }
                list.add(ownerAccount);
            }
            return list;
        }


        private List<DepartmentSimpleInfo> getDepartmentSimpleInfoList(int companyId, List<Integer> departmentIds) {
            if (departmentIds == null || departmentIds.isEmpty()) {
                return new ArrayList<>();
            }
            List<DepartmentSimpleInfo> list = new ArrayList<>();
            Set<Integer> ids = BizUtil.convert(departmentIds);
            for (Integer deprtId : ids) {
                DepartmentSimpleInfo ownerDepartment = dao.getExistedDepartmentSimpleInfoById(deprtId);
                if (ownerDepartment.companyId != companyId && companyId > 0 && ownerDepartment.companyId > 0) {
                    throw new AppException("权限不足");
                }
                if (ownerDepartment.companyId <= 0) {
                    continue;
                }
                list.add(ownerDepartment);
            }
            return list;
        }

        @Override
        public TaskRemindInfo getTaskRemindInfo(String token, int taskId) {
            return dao.getTaskRemindByTaskId(taskId);
        }

        //
        @Transaction
        @Override
        public void updateTaskRemind(String token, TaskRemind bean) {
            Account account = bizService.getExistedAccountByToken(token);
            Task task;
            TaskRemindInfo old = dao.getTaskRemindByTaskIdForUpdate(bean.taskId);
            if (old != null) {
                task = dao.getExistedById(Task.class, old.taskId);
                bizService.checkPermissionForProjectAccess(account, task.projectId);
                old.remindRules = bean.remindRules;
                old.remindAccountIdList = bean.remindAccountIdList;
                old.remindAccountList = getAccountSimpleInfoList(account.companyId, old.remindAccountIdList);
                old.updateAccountId = account.id;
                dao.update(old);
                bean = old;
            } else {//新增
                task = dao.getExistedById(Task.class, bean.taskId);
                bizService.checkPermissionForProjectAccess(account, task.projectId);
                bean.companyId = task.companyId;
                bean.projectId = task.projectId;
                bean.createAccountId = account.id;
                bean.remindAccountList = getAccountSimpleInfoList(account.companyId, bean.remindAccountIdList);
                dao.add(bean);
            }
            //计算提醒时间
            bizService.calcRemindTime(bean, task);
        }

        //

        /**
         * 通过ID查询汇报模板
         */
        @Override
        public ReportTemplateInfo getReportTemplateById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_汇报模板管理);
            ReportTemplateInfo bean = dao.getById(ReportTemplateInfo.class, id);
            bizService.checkPermission(account, bean.companyId);
            return bean;
        }

        /**
         * 查询汇报模板列表和总数
         */
        @Override
        public Map<String, Object> getReportTemplateList(String token, ReportTemplateQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_开启汇报模块);
            setupQuery(account, query);
            query.createAccountId = account.id;
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        @Override
        public Map<String, Object> getReportTemplateListByAdmin(String token, ReportTemplateQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_汇报模板管理);
            //bizService.checkIsCompanyAdmin(account);
            setupQuery(account, query);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        /**
         * 新增汇报模板
         */
        @Transaction
        @Override
        public int addReportTemplate(String token, ReportTemplateInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_汇报模板管理);
            bizService.checkWeekValid(bean.periodSetting);
            bean.companyId = account.companyId;
            bean.createAccountId = account.id;
            if (bean.projectId > 0) {
                dao.getExistedById(Project.class, bean.projectId);
            }
            bizService.setNextRemindTimeNew(bean);
            bean.submitterList = getAccountSimpleInfoList(0, bean.submitterIds);
            bean.auditorList = getAccountSimpleInfoList(0, bean.auditorIds);
            bean.status = ReportTemplateInfo.STATUS_有效;
            BizUtil.checkValid(bean);
            BizUtil.checkUniqueKeysOnAdd(dao, bean);
            dao.add(bean);
            //
            bizService.addOptLog(account, bean.id, bean.name, OptLog.EVENT_ID_新增汇报模板, "name:" + bean.name);
            //
            return bean.id;
        }

        /**
         * 编辑汇报模板
         */
        @Transaction
        @Override
        public void updateReportTemplate(String token, ReportTemplateInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_创建汇报);
            bizService.checkWeekValid(bean.periodSetting);
            ReportTemplateInfo old = dao.getExistedByIdForUpdate(ReportTemplateInfo.class, bean.id);
            if (bean.projectId > 0) {
                dao.getExistedById(Project.class, bean.projectId);
            }
            old.name = bean.name;
            old.period = bean.period;
            old.periodSetting = bean.periodSetting;
            old.remindTime = bean.remindTime;
            bizService.setNextRemindTimeNew(old);
            old.projectId = bean.projectId;
            old.submitterIds = bean.submitterIds;
            old.submitterList = getAccountSimpleInfoList(0, old.submitterIds);
            old.auditorIds = bean.auditorIds;
            old.auditorList = getAccountSimpleInfoList(0, old.auditorIds);
            old.content = bean.content;
            old.updateAccountId = account.id;
            BizUtil.checkValid(old);
            BizUtil.checkUniqueKeysOnUpdate(dao, bean, old);
            dao.update(old);
            //
            bizService.addOptLog(account, bean.id, bean.name, OptLog.EVENT_ID_编辑汇报模板, "name:" + old.name);
        }

        @Transaction
        @Override
        public void updateReportTemplateStatus(String token, int id, int status) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_汇报模板管理);
            //bizService.checkIsCompanyAdmin(account);
            ReportTemplateInfo old = dao.getExistedByIdForUpdate(ReportTemplateInfo.class, id);
            if (old.status == status) {
                return;
            }
            old.updateAccountId = account.id;
            old.status = status;
            dao.update(old);
            //
            if (status == ReportTemplateInfo.STATUS_有效) {
                bizService.addOptLog(account, old.id, old.name, OptLog.EVENT_ID_启用汇报模板, "name:" + old.name);
            }
            if (status == ReportTemplateInfo.STATUS_无效) {
                bizService.addOptLog(account, old.id, old.name, OptLog.EVENT_ID_禁用汇报模板, "name:" + old.name);
            }
        }

        /**
         * 删除汇报模板
         */
        @Transaction
        @Override
        public void deleteReportTemplate(String token, int id, String reason) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_汇报模板管理);
            ReportTemplate old = dao.getExistedById(ReportTemplate.class, id);
            dao.deleteById(ReportTemplate.class, id);
            //
            bizService.addOptLog(account, old.id, old.name, OptLog.EVENT_ID_删除汇报模板, "name:" + old.name);
        }
        //

        /**
         * 通过ID查询汇报
         */
        @Override
        public ReportInfo getReportById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_开启汇报模块);
            ReportInfo bean = dao.getById(ReportInfo.class, id);
            ReportContentQuery query = new ReportContentQuery();
            query.reportId = id;
            query.pageSize = Integer.MAX_VALUE;
            query.createTimeSort = Query.SORT_TYPE_ASC;
            bean.reportContentList = dao.getList(query);
            return bean;
        }

        /**
         * 查询汇报列表和总数
         */
        @Override
        public Map<String, Object> getReportList(String token, ReportQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            if (!bizService.isCompanySuperBoss(account)) {
                bizService.checkCompanyPermission(account, Permission.ID_开启汇报模块);
                query.submitterIdAuditorId = account.id;
            }
            bizService.setupQuery(account, query);
            return bizService.getReportList(query);
//            return createResult(dao.getList(query), dao.getListCount(query));
        }

        @Transaction
        @Override
        public void saveReport(String token, ReportInfo bean, boolean isSubmit) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_创建汇报);
            ReportInfo old = dao.getByIdForUpdate(ReportInfo.class, bean.id);
            if (old.status != ReportInfo.STATUS_待汇报) {
                throw new AppException("审核状态错误");
            }
            if (old.submitterId != account.id) {
                throw new AppException("权限不足");
            }
            if (bean.reportContentList != null) {
                for (ReportContent e : bean.reportContentList) {
                    ReportContent oldC = dao.getExistedByIdForUpdate(ReportContent.class, e.id);
                    if (oldC.reportId != old.id) {
                        throw new AppException("非法请求");
                    }
                    oldC.content = e.content;
                    dao.update(oldC);
                }
            }
            if (isSubmit) {
                old.status = ReportInfo.STATUS_待审核;
                //
                for (Integer auditorId : old.auditorIds) {
                    Account auditor = dao.getById(Account.class, auditorId);
                    if (bizService.isNormalAccount(auditor)) {
                        bizService.addAccountNotification(auditor.id,
                                AccountNotificationSetting.TYPE_待审核汇报,
                                old.companyId, old.projectId, bean.id,
                                "待审核汇报", JSONUtil.toJson(old),
                                new Date(), account);
                    }

                }
            }
            old.name = bean.name;
            dao.update(old);
        }

        /**
         * 检查是否是审核人
         */
        private void checkIsAuditor(Account account, ReportInfo report) {
            if (!BizUtil.contains(report.auditorIds, account.id)) {
                throw new AppException("您不是审核人，权限不足");
            }
        }

        @Transaction
        @Override
        public void replyReport(String token, ReportContent bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_开启汇报模块);
            ReportInfo old = dao.getByIdForUpdate(ReportInfo.class, bean.reportId);
            checkIsAuditor(account, old);
            if (old.status == ReportInfo.STATUS_待汇报) {
                throw new AppException("审核状态错误");
            }
            bean.type = ReportContent.TYPE_评论;
            BizUtil.checkValid(bean);
            bean.createAccountId = account.id;
            dao.add(bean);
        }

        //
        @Transaction
        @Override
        public void auditPassReport(String token, int reportId) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_开启汇报模块);
            ReportInfo old = dao.getByIdForUpdate(ReportInfo.class, reportId);
            if (old.status != ReportInfo.STATUS_待审核) {
                throw new AppException("审核状态错误");
            }
            checkIsAuditor(account, old);
            ReportContent bean = new ReportContent();
            bean.companyId = old.companyId;
            bean.reportId = reportId;
            bean.content = "审核通过";
            bean.type = ReportContent.TYPE_评论;
            BizUtil.checkValid(bean);
            bean.createAccountId = account.id;
            dao.add(bean);
            //
            old.status = ReportInfo.STATUS_已审核;
            dao.update(old);
        }

        //
        @Transaction
        @Override
        public void auditRejectReport(String token, int reportId) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_开启汇报模块);
            ReportInfo old = dao.getByIdForUpdate(ReportInfo.class, reportId);
            if (old.status != ReportInfo.STATUS_待审核) {
                throw new AppException("审核状态错误");
            }
            checkIsAuditor(account, old);
            ReportContent bean = new ReportContent();
            bean.companyId = old.companyId;
            bean.reportId = reportId;
            bean.content = "审核未通过";
            bean.type = ReportContent.TYPE_评论;
            BizUtil.checkValid(bean);
            bean.createAccountId = account.id;
            dao.add(bean);
            //
            old.status = ReportInfo.STATUS_待汇报;
            dao.update(old);
        }
        //

        /**
         * 通过ID查询仪表盘
         */
        @Override
        public DashboardInfo getDashboardById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_开启仪表盘模块);
            return dao.getById(DashboardInfo.class, id);
        }

        /**
         * 查询仪表盘列表和总数
         */
        @Override
        public List<DashboardInfo> getDashboardList(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            DashboardQuery query = new DashboardQuery();
            setupQuery(account, query);
            if (!bizService.isCompanySuperBoss(account)) {
                bizService.checkCompanyPermission(account, Permission.ID_开启仪表盘模块);
                query.createAccountIdOrshareAccountId = account.id;
            }
            query.pageSize = 10000;
            List<DashboardInfo> list = dao.getList(query);
            for (DashboardInfo e : list) {
                if (e.createAccountId != account.id) {
                    e.name += "【" + e.createAccountName + "】";
                }
            }
            return list;
        }

        /**
         * 新增仪表盘
         */
        @Transaction
        @Override
        public int addDashboard(String token, DashboardInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_创建仪表盘);
            bean.createAccountId = account.id;
            bean.companyId = account.companyId;
            bean.shareAccountList = getAccountSimpleInfoList(account.companyId, bean.shareAccountIdList);
            BizUtil.checkValid(bean);
            dao.add(bean);
            if (bean.projectId != null) {
                Project project = dao.getExistedById(Project.class, bean.projectId);
                bizService.checkPermissionForProjectAccess(account, project);
                //1.项目活动图
                DashboardCardInfo card = new DashboardCardInfo();
                card.type = DashboardCard.TYPE_项目活动图;
                card.width = getDashboradCardWidth(card.type);
                card.height = 6;
                card.dashboardId = bean.id;
                card.projectId = bean.projectId;
                card.name = project.name + "活动图";
                addDashboardCard0(account, card);
                //2.如果有迭代 取最新的迭代 迭代概览
                ProjectIteration iteration = dao.getLastProjectIteration(bean.projectId);
                if (iteration != null) {
                    card.type = DashboardCard.TYPE_迭代概览;
                    card.width = getDashboradCardWidth(card.type);
                    card.name = project.name + "-" + iteration.name + "概览";
                    card.iterationId = iteration.id;
                    addDashboardCard0(account, card);
                }
                //3.日期统计 项目已创建多少天
                card.type = DashboardCard.TYPE_日期统计;
                card.width = getDashboradCardWidth(card.type);
                card.name = project.name + "已创建";
                card.iterationId = 0;
                card.dueDate = project.createTime;
                addDashboardCard0(account, card);
                //4.如果有迭代 距离迭代结束还要多少天
                if (iteration != null) {
                    card.type = DashboardCard.TYPE_日期统计;
                    card.width = getDashboradCardWidth(card.type);
                    card.name = "距离" + project.name + "-" + iteration.name + "结束";
                    card.iterationId = iteration.id;
                    card.dueDate = iteration.endDate;
                    addDashboardCard0(account, card);
                }
                //5.数据报表
                List<ProjectModuleInfo> list = bizService.getEnableObjectTypeList(bean.projectId);
                for (ProjectModuleInfo e : list) {
                    card.type = DashboardCard.TYPE_数据报表;
                    card.width = getDashboradCardWidth(card.type);
                    card.objectType = e.objectType;
                    if (e.isStatusBased) {//状态分布图
                        if (iteration != null) {
                            card.name = project.name + "-" + iteration.name + "-" + e.objectTypeName + "状态分布图";
                        } else {
                            card.name = project.name + "-" + e.objectTypeName + "状态分布图";
                        }
                        card.chartId = DashboardCard.CHARTID_状态分布图;
                        addDashboardCard0(account, card);
                    }
                    //测试用例没有迭代
                    if (iteration != null && card.objectType != Task.OBJECTTYPE_测试用例) {
                        card.name = project.name + "-" + iteration.name + "-" + e.objectTypeName + "责任人分布图";
                        card.iterationId = iteration.id;
                    } else {
                        card.name = project.name + "-" + e.objectTypeName + "责任人分布图";
                        card.iterationId = 0;
                    }
                    card.chartId = DashboardCard.CHARTID_责任人分布图;
                    addDashboardCard0(account, card);
                }
            }
            return bean.id;
        }

        /**
         * 编辑仪表盘
         */
        @Transaction
        @Override
        public void updateDashboard(String token, DashboardInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_创建仪表盘);
            Dashboard old = dao.getExistedByIdForUpdate(Dashboard.class, bean.id);
            if (old.createAccountId != account.id) {
                throw new AppException("权限不足");
            }
            old.name = bean.name;
            old.shareAccountIdList = bean.shareAccountIdList;
            old.shareAccountList = getAccountSimpleInfoList(account.companyId, old.shareAccountIdList);
            old.updateAccountId = account.id;
            BizUtil.checkValid(old);
            dao.update(old);
        }

        /**
         * 删除仪表盘
         */
        @Transaction
        @Override
        public void deleteDashboard(String token, int id, String reason) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_创建仪表盘);
            Dashboard old = dao.getExistedByIdForUpdate(Dashboard.class, id);
            if (old.isDelete) {
                return;
            }
            if (old.createAccountId != account.id) {
                throw new AppException("权限不足");
            }
            old.isDelete = true;
            old.updateAccountId = account.id;
            dao.update(old);
        }
        //

        /**
         * 通过ID查询仪表盘卡牌
         */
        @Override
        public DashboardCardInfo getDashboardCardById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            if (!bizService.isCompanySuperBoss(account)) {
                bizService.checkCompanyPermission(account, Permission.ID_开启仪表盘模块);
            }
            return dao.getExistedById(DashboardCardInfo.class, id);
        }

        /**
         * 查询仪表盘卡牌列表和总数
         */
        @Override
        public List<DashboardCardInfo> getDashboardCardList(String token, DashboardCardQuery query) {
            query.pageSize = Integer.MAX_VALUE;
            Account account = bizService.getExistedAccountByToken(token);
            if (!bizService.isCompanySuperBoss(account)) {
                bizService.checkCompanyPermission(account, Permission.ID_开启仪表盘模块);
            }
            query.projectIsDelete = false;
            setupQuery(account, query);
            List<DashboardCardInfo> list = dao.getList(query);
            for (DashboardCardInfo e : list) {
                e.updatePassSecond = (int) ((System.currentTimeMillis() - e.updateTime.getTime()) / 1000);
            }
            if (logger.isDebugEnabled()) {
                logger.debug(cornerstone.biz.util.DumpUtil.dump(list));
            }
            return list;
        }

        /**
         * 新增仪表盘卡牌
         */
        @Transaction
        @Override
        public DashboardCardInfo addDashboardCard(String token, DashboardCardInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            return addDashboardCard0(account, bean);
        }

        private DashboardCardInfo addDashboardCard0(Account account, DashboardCardInfo bean) {
            bizService.checkCompanyPermission(account, Permission.ID_创建仪表盘);
            bean.createAccountId = account.id;
            Dashboard dashboard = dao.getExistedByIdForUpdate(Dashboard.class, bean.dashboardId);//锁住 让x y值不重复
            if (dashboard.createAccountId != account.id) {
                throw new AppException("权限不足");
            }
            checkValid(bean);
            checkDataDictValueValid("DashboardCard.type", bean.type, "卡牌类型错误");
            int width = getDashboradCardWidth(bean.type);
            DashboardCardQuery cardQuery = new DashboardCardQuery();
            cardQuery.dashboardId = bean.dashboardId;
            cardQuery.createAccountId = account.id;
            cardQuery.pageSize = Integer.MAX_VALUE;
            cardQuery.isDelete = false;
            List<DashboardCard> cards = dao.getList(cardQuery);
            cards.sort(Comparator.comparingInt(a -> a.x));
            int maxY = 0;
            for (DashboardCard e : cards) {
                if (e.y + e.height > maxY) {
                    maxY = e.y + e.height;
                }
            }
            int myX = -1;
            int myY = 0;
            int defaultHeight = 6;
            for (int y = 0; y <= maxY; y++) {
                myY = y;
                for (int x = 0; x < 6; x++) {
                    Rectangle my = new Rectangle(x, y, width, defaultHeight);
                    int maxX = x + width - 1;
                    if (maxX > 5) {
                        continue;
                    }
                    if (!isContain(my, cards)) {
                        myX = x;
                        break;
                    }
                }
                if (myX != -1) {
                    break;
                }
            }
            //
            bean.x = myX;
            bean.y = myY;
            bean.companyId = dashboard.companyId;
            bean.createAccountId = account.id;
            statService.makeDashboardCardInfo(account, bean);
            BizUtil.checkValid(bean);
            dao.add(bean);
            return bean;
        }

        private boolean isContain(Rectangle my, List<DashboardCard> cards) {
            if (cards == null || cards.size() == 0) {
                return false;
            }
            for (DashboardCard e : cards) {
                Rectangle r = new Rectangle(e.x, e.y, e.width, e.height);
                if (r.contains(my)) {
                    return true;
                }
            }
            return false;
        }

        private void checkValid(DashboardCardInfo bean) {
            if (bean.type == DashboardCardInfo.TYPE_迭代概览) {
                if (bean.iterationId == 0) {
                    throw new AppException("迭代不能为空");
                }
            }
        }

        private int getDashboradCardWidth(int type) {
            switch (type) {
                case DashboardCard.TYPE_数据报表:
                case DashboardCard.TYPE_数字指标:
                case DashboardCard.TYPE_日期统计:
                    return 2;
                case DashboardCard.TYPE_项目列表:
                case DashboardCard.TYPE_迭代概览:
                case DashboardCard.TYPE_项目活动图:
                    return 4;
                default:
                    throw new AppException("type error" + type);
            }
        }

        @Transaction
        @Override
        public void batchRefreshDashboardCard(String token, Set<Integer> idList) {
            Account account = bizService.getExistedAccountByToken(token);
            for (Integer id : idList) {
                refreshDashboardCard0(account, id);
            }
        }

        @Transaction
        @Override
        public void refreshDashboardAllCard(String token, int dashboardId) {
            Account account = bizService.getExistedAccountByToken(token);
            DashboardCardQuery query = new DashboardCardQuery();
            query.dashboardId = dashboardId;
            query.pageSize = 1000;
            bizService.checkCompanyPermission(account, Permission.ID_开启仪表盘模块);
            setupQuery(account, query);
            List<DashboardCardInfo> list = dao.getList(query);
            for (DashboardCardInfo e : list) {
                refreshDashboardCard0(account, e.id);
            }
        }

        @Transaction
        @Override
        public void refreshDashboardCard(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            refreshDashboardCard0(account, id);
        }

        /**
         * 刷新仪表盘卡牌数据
         */

        public void refreshDashboardCard0(Account account, int id) {
            DashboardCard old = dao.getExistedByIdForUpdate(DashboardCard.class, id);
            statService.makeDashboardCardInfo(account, old);
            dao.update(old);
        }

        /**
         * 编辑仪表盘卡牌
         */
        @Transaction
        @Override
        public void updateDashboardCard(String token, DashboardCardInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_创建仪表盘);
            DashboardCard old = dao.getExistedByIdForUpdate(DashboardCard.class, bean.id);
            if (old.createAccountId != account.id) {
                throw new AppException("权限不足");
            }
            old.name = bean.name;
            old.type = bean.type;
            old.projectId = bean.projectId;
            old.projectIdList = bean.projectIdList;
            old.iterationId = bean.iterationId;
            old.objectType = bean.objectType;
            old.filterId = bean.filterId;
            old.chartId = bean.chartId;
            old.dueDate = bean.dueDate;
            old.updateAccountId = account.id;
            statService.makeDashboardCardInfo(account, old);
            dao.update(old);
        }

        @Transaction
        @Override
        public void saveDashboardCardList(String token, List<DashboardCardInfo> cardList) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_创建仪表盘);
            for (DashboardCardInfo e : cardList) {
                DashboardCard old = dao.getExistedByIdForUpdate(DashboardCard.class, e.id);
                old.x = e.x;
                old.y = e.y;
                old.width = e.width;
                old.height = e.height;
                old.index = e.index;
                old.updateAccountId = account.id;
                dao.update(old);
            }
        }

        /**
         * 删除仪表盘卡牌
         */
        @Transaction
        @Override
        public void deleteDashboardCard(String token, int id, String reason) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_创建仪表盘);
            DashboardCard old = dao.getExistedByIdForUpdate(DashboardCard.class, id);
            if (old.createAccountId != account.id) {
                throw new AppException("权限不足");
            }
            if (old.isDelete) {
                return;
            }
            old.isDelete = true;
            old.updateAccountId = account.id;
            dao.update(old);
        }

        //
        @Transaction
        @Override
        public void addProjectRunLog(String token, ProjectRunLog bean) {
            Account account = bizService.getExistedAccountByToken(token);
            Project project = dao.getExistedByIdForUpdate(Project.class, bean.projectId);
            bizService.checkPermissionForProjectAccess(account, project);
            checkDataDictValueValid("Project.runStatus", bean.runStatus, "运行状态错误");
            bizService.checkProjectPermission(account, project.id, Permission.ID_编辑项目进度);
            project.runStatus = bean.runStatus;
            project.progress = bean.progress;
            project.runLogRemark = bean.remark;
            dao.update(project);
            //
            bean.createAccountId = account.id;
            bean.companyId = account.companyId;
            BizUtil.checkValid(bean);
            dao.add(bean);

        }

        @Override
        public ProjectRunLogInfo getProjectRunLogById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            ProjectRunLogInfo bean = dao.getExistedById(ProjectRunLogInfo.class, id);
            bizService.checkPermissionForProjectAccess(account, bean.projectId);
            return bean;
        }

        @Override
        public Map<String, Object> getProjectRunLogList(String token, ProjectRunLogQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            setupQuery(account, query);
            return createResult(dao.getList(query), dao.getListCount(query));
        }
        //

        /**
         * 通过ID查询讨论
         */
        @Override
        public DiscussInfo getDiscussById(String token, int id) {
            bizService.getExistedAccountByToken(token);
            return dao.getById(DiscussInfo.class, id);
        }

        @Override
        public Date getDiscussUpdateTimeById(String token, int id) {
            bizService.getExistedAccountByToken(token);
            Discuss bean = dao.getExistedById(Discuss.class, id);
            return bean.updateTime;
        }

        /**
         * 查询讨论列表和总数
         */
        @Override
        public Map<String, Object> getDiscussList(String token, DiscussQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            setupQuery(account, query);
//            if (!bizService.isCompanySuperBoss(account)) {
            query.createAccountOrMember = account.id;//查我创建的或我在成员里的讨论
//            }
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        /**
         * 新增讨论
         */
        @Transaction
        @Override
        public int addDiscuss(String token, DiscussInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bean.createAccountId = account.id;
            bean.companyId = account.companyId;
            if (bean.members == null) {
                bean.members = new ArrayList<>();
            }
            bean.memberInfos = getAccountSimpleInfoList(account.companyId, bean.members);
            BizUtil.checkValid(bean);
            return dao.add(bean);
        }

        /**
         * 编辑讨论
         */
        @Transaction
        @Override
        public void updateDiscuss(String token, DiscussInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            Discuss old = dao.getExistedByIdForUpdate(Discuss.class, bean.id);
            if (old.createAccountId != account.id) {
                throw new AppException("权限不足");
            }
            old.name = bean.name;
            old.content = bean.content;
            old.members = bean.members;
            if (old.members == null) {
                old.members = new ArrayList<>();
            }
            old.memberInfos = getAccountSimpleInfoList(old.companyId, old.members);
            old.projectId = bean.projectId;
            old.updateAccountId = account.id;
            BizUtil.checkValid(old);
            dao.update(old);
        }

        /**
         * 删除讨论
         */
        @Transaction
        @Override
        public void deleteDiscuss(String token, int id, String reason) {
            bizService.getExistedAccountByToken(token);
            Discuss old = dao.getExistedByIdForUpdate(Discuss.class, id);
            if (old.isDelete) {
                return;
            }
            old.isDelete = true;
            dao.update(old);
        }

        //
        @Override
        public DiscussMessageInfo getDiscussMessageById(String token, int id) {
            bizService.getExistedAccountByToken(token);
            return dao.getById(DiscussMessageInfo.class, id);
        }

        @Override
        public Map<String, Object> getDiscussMessageList(String token, DiscussMessageQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            setupQuery(account, query);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        @Transaction
        @Override
        public int addDiscussMessage(String token, DiscussMessageInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bean.createAccountId = account.id;
            if (bean.attachments == null) {
                bean.attachments = new ArrayList<>();
            }
            if (bean.message == null || StringUtil.isEmpty(bean.message.trim())) {
                throw new AppException("内容不能为空");
            }
            bean.companyId = account.companyId;
            BizUtil.checkValid(bean);
            dao.add(bean);
            //
            Discuss discuss = dao.getExistedByIdForUpdate(Discuss.class, bean.discussId);
            if (discuss.companyId != account.companyId) {
                throw new AppException("权限不足");
            }
            discuss.lastMessage = bean.message;
            discuss.lastMessageAccountId = account.id;
            discuss.lastMessageTime = new Date();
            discuss.replyCount = dao.getReplyCount(discuss.id);
            dao.update(discuss);
            //
            //
            List<Integer> members = discuss.members;
            for (Integer memberAccountId : members) {
                if (memberAccountId.equals(account.id)) {
                    continue;
                }
                bizService.addAccountNotification(memberAccountId, AccountNotificationSetting.TYPE_发表讨论,
                        discuss.companyId, discuss.projectId, bean.id, "发表新的讨论", JSONUtil.toJson(bean),
                        new Date(), account);
            }
            //
            if (CornerstoneBizSystem.webEventServer != null) {
                AutoTranscationCallback.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        WebEvent event = WebEvent.createWebEvent(account.id, account.companyId,
                                WebEvent.TYPE_有新发表的讨论, null, null, null, discuss.id, bean.id, null, null, null);
                        CornerstoneBizSystem.webEventServer.boardcastAsync(event);
                    }
                });
            }
            //
            return bean.id;
        }

        @Transaction
        @Override
        public void deleteDiscussMessage(String token, int id, String reason) {
            Account account = bizService.getExistedAccountByToken(token);
            DiscussMessage old = dao.getExistedByIdForUpdate(DiscussMessage.class, id);
            if (old.createAccountId != account.id) {
                throw new AppException("权限错误");
            }
            dao.deleteById(DiscussMessage.class, old.id);
            //
            Discuss discuss = dao.getExistedByIdForUpdate(Discuss.class, old.discussId);
            discuss.replyCount = dao.getReplyCount(discuss.id);
            dao.update(discuss);
        }

        @Override
        public OptLogInfo getOptLogById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看操作日志);
            return dao.getById(OptLogInfo.class, id);
        }

        @Override
        public Map<String, Object> getOptLogList(String token, OptLogQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_查看操作日志);
            setupQuery(account, query);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        @Transaction
        @Override
        public void addExampleProjects(String token, boolean isPrivateDeploy) {
            logger.info("addExampleProjects start");
            Account account = bizService.getExistedAccountByToken(token);
            //
            int templateProjectId = 1;//专业敏捷开发
            Project project = new Project();
            project.name = "示例项目";
            project.group = "示例";
            createProject0(account, project, templateProjectId);
            int projectId = project.id;
            //20200202添加
            AutoTranscationCallback.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    logger.info("addExampleProjects afterCommit accountId:{}", account.id);
                    Jazmin.execute(() -> {
                        try {
                            BizData.bizAction.doAddExampleProjects(token, projectId, isPrivateDeploy);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    });
                }
            });
        }

        @Transaction
        @Override
        public void doAddExampleProjects(String token, int projectId, boolean isPrivateDeploy) {
            doAddExampleProjects0(token, projectId, isPrivateDeploy);
        }

        //防止死锁
        private synchronized void doAddExampleProjects0(String token, int projectId, boolean isPrivateDeploy) {
            logger.info("doAddExampleProjects start projectId:{} isPrivateDeploy:{}",
                    projectId, isPrivateDeploy);
            long startTime = System.currentTimeMillis();
            Account account = bizService.getExistedAccountByToken(token);
            //创建迭代
            ProjectIterationInfo iteration = new ProjectIterationInfo();
            iteration.projectId = projectId;
            iteration.name = "1.0";
            iteration.status = ProjectIterationInfo.STATUS_开发中;
            List<ProjectIterationStepInfo> stepList = new ArrayList<>();
            ProjectIterationStepInfo step = new ProjectIterationStepInfo();
            step.name = "需求分析";
            step.startDate = DateUtil.getNextDay(-30);
            step.endDate = DateUtil.getNextDay(-15);
            stepList.add(step);
            step = new ProjectIterationStepInfo();
            step.name = "程序开发";
            step.startDate = DateUtil.getNextDay(-15);
            step.endDate = DateUtil.getNextDay(15);
            stepList.add(step);
            step = new ProjectIterationStepInfo();
            step.name = "测试上线";
            step.startDate = DateUtil.getNextDay(15);
            step.endDate = DateUtil.getNextDay(30);
            stepList.add(step);
            iteration.stepList = stepList;
            int iterationId = createProjectIteration(token, iteration);

            //任务 缺陷 需求 测试计划 测试用例
            for (int i = 0; i < 5; i++) {
                int objectType = i + 1;
                List<ProjectFieldDefine> allFieldDefines = dao.getProjectFieldDefineList(projectId, objectType);
                TableData data = ExampleTaskUtil.readTasks(objectType);
                List<ProjectFieldDefine> fieldDefines = new ArrayList<>();
                int importContentPos = -1;
                int pos = -1;
                for (String header : data.headers) {
                    pos++;
                    if ("详细描述".equals(header)) {
                        if (!isPrivateDeploy) {//私有部署没有详细描述
                            importContentPos = pos;
                        }
                        fieldDefines.add(null);
                        continue;
                    }
                    ProjectFieldDefine define = BizUtil.getByTarget(allFieldDefines, "name", header);
                    fieldDefines.add(define);
                }
                int line = 1;//行号
                for (List<String> content : data.contents) {
                    TaskDetailInfo bean = makeTask("doAddExampleProjects", account.companyId, projectId,
                            objectType, content, fieldDefines, importContentPos, line, true);
                    if (bean == null) {
                        continue;
                    }
                    try {
                        bean.ownerAccountIdList = Collections.singletonList(account.id);//责任人设置为自己
                        //第三个任务 截止日期为5天前 已超期
                        if (objectType == Task.OBJECTTYPE_任务 && line == 3) {
                            bean.endDate = DateUtil.getNextDay(-5);
                        }
                        if (objectType != Task.OBJECTTYPE_测试用例) {
                            bean.iterationId = iterationId;
                        }
                        createTask0(account, bean, false);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        logger.error("content:{}", DumpUtil.dump(content));
                        throw new AppException("第" + line + "行数据错误," + e.getMessage());
                    }
                    line++;
                }
            }
            //
            //添加仪表盘
            DashboardInfo dashboard = new DashboardInfo();
            dashboard.name = "示例项目";
            dashboard.projectId = projectId;
            addDashboard(token, dashboard);
            //
            logger.info("addExampleProjects finish using {}ms", System.currentTimeMillis() - startTime);
        }
        //

        @Override
        public Map<String, Object> getSystemConfig() {
            Map<String, Object> result = new HashMap<>();
            result.put("isWxAppIdSet", !StringUtil.isEmptyWithTrim(GlobalConfig.appId) && GlobalConfig.appId.length() >= 6);
            result.put("isQywxAppIdSet", !StringUtil.isEmptyWithTrim(GlobalConfig.qywxAppId) && GlobalConfig.qywxAppId.length() >= 6);
            result.put("isLarkAppIdSet", !StringUtil.isEmptyWithTrim(GlobalConfig.larkAppId) && GlobalConfig.larkAppId.length() >= 6);
            result.put("isDingtalkAppIdSet", !StringUtil.isEmptyWithTrim(GlobalConfig.dingtalkCorpId) && GlobalConfig.dingtalkCorpId.length() >= 16);
            result.put("isPrivateDeploy", GlobalConfig.isPrivateDeploy);
            //
            result.put("isSmsSet", !StringUtil.isEmptyWithTrim(GlobalConfig.qcloundSmsAppId) && GlobalConfig.qcloundSmsAppId.length() >= 6);

            //
            try {
                int appVersion = dao.getAppVersion();
                result.put("appVersion", appVersion);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            result.put("logoImageId", GlobalConfig.getValue("system.logo"));
            //
            CompanyQuery query = new CompanyQuery();
            query.isDelete = false;
            query.pageSize = 1;
            query.idSort = Query.SORT_TYPE_ASC;
            List<Company> companys = dao.getList(query);
            if (companys.size() > 0) {
                result.put("companyName", companys.get(0).name);
            }
            //
            result.put("loginBackgroundUrl", GlobalConfig.getValue("system.loginBackgroundUrl"));

            //开启AD域账号登录
            result.put("isAdSet", Boolean.parseBoolean(GlobalConfig.getValue("auth.ldap.enable", "false")));
            result.put("isSupplierEnable", Boolean.parseBoolean(GlobalConfig.getValue("supplier.func.open", "false")));
            result.put("isAttendanceEnable", Boolean.parseBoolean(GlobalConfig.getValue("attendance.func.open", "false")));
            //
            return result;
        }

        @Override
        public AccountProfileInfo getAccountProfileInfo(String token) {
            AccountProfileInfo info = new AccountProfileInfo();
            AccountInfo account = bizService.getExistedAccountInfoByToken(token);
            info.account = account;
            info.company = dao.getExistedById(Company.class, account.companyId);
            info.projectList = bizService.getMyProjectInfoListExcludeFinished(account.id, account.companyId);
            info.roles = bizService.getMyCompanyRoleList(info.account);
            CompanyMember companyMember = dao.getCompanyMemberByAccountIdCompanyId(
                    info.account.id, info.account.companyId);
            if (companyMember != null) {
                info.departmentList = companyMember.departmentList;
            } else {
                info.departmentList = new ArrayList<>();
            }
            int[] projectIdInList = bizService.getMyRunnigProjectIdList(account.id, account.companyId);
            //todoTask
            int[] objectTypes = bizService.getStatusBasedObjectTypeListByCompanyId(account.companyId);
            if (!BizUtil.isNullOrEmpty(objectTypes)) {
                TaskQuery query = new TaskQuery();
                query.companyId = account.companyId;
                query.projectStatus = Project.STATUS_运行中;
                query.ownerAccountId = account.id;
                query.objectTypeInList = objectTypes;
                query.isFinish = false;
                setupQuery(account, query);
                if (!BizUtil.isNullOrEmpty(projectIdInList)) {
                    query.projectIdInList = projectIdInList;
                    info.todoTaskNum = dao.getListCount(query);
                    //
                    query.createAccountId = account.id;
                    info.createTaskNum = dao.getListCount(query);
                    //
                    query.createAccountId = null;
                    query.isDelay = true;
                    info.delayTaskNum = dao.getListCount(query);
                }
            }
            //
            if (BizUtil.isNullOrEmpty(projectIdInList)) {
                info.changeLogList = createResult(new ArrayList<>(), 0);
            } else {
                ChangeLogQuery changeLogQuery = new ChangeLogQuery();
                changeLogQuery.createAccountId = account.id;
                changeLogQuery.createTimeStart = DateUtil.getNextDay(-15);
                changeLogQuery.pageSize = 1000;
                changeLogQuery.projectIdInList = projectIdInList;
                setupQuery(account, changeLogQuery);
                info.changeLogList = createResult(dao.getList(changeLogQuery), dao.getListCount(changeLogQuery));
            }
            return info;
        }

        private void checkIsCreaterOrMember(Account account, Calendar calendar) {
            if (account.id == calendar.createAccountId) {
                return;
            }
            if (BizUtil.contains(calendar.members, account.id)) {
                return;
            }
            throw new AppException("权限不足");
        }

        @Override
        public CalendarInfo getCalendarById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            CalendarInfo info = dao.getExistedById(CalendarInfo.class, id);
            checkIsCreaterOrMember(account, info);
            if (info.createAccountId != account.id) {
                info.name = info.name + "【" + info.createAccountName + "】";
            }
            return info;
        }

        @Override
        public List<CalendarInfo> getCalendarList(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            CalendarQuery query = new CalendarQuery();
            query.idSort = Query.SORT_TYPE_ASC;
            query.pageSize = Integer.MAX_VALUE;
            query.createAccountOrMember = account.id;
            setupQuery(account, query);
            List<CalendarInfo> list = dao.getList(query);
            if (list.isEmpty()) {
                CalendarInfo bean = new CalendarInfo();
                bean.name = "个人日程";
                bean.isDefault = true;
                addCalendar0(account, bean);
                list.add(bean);
            }
            for (CalendarInfo e : list) {
                if (e.createAccountId != account.id) {
                    e.name += "【" + e.createAccountName + "】";
                }
            }
            return list;
        }

        @Transaction
        @Override
        public int addCalendar(String token, CalendarInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bean.isDefault = false;
            return addCalendar0(account, bean);
        }

        private int addCalendar0(Account account, CalendarInfo bean) {
            bean.createAccountId = account.id;
            bean.companyId = account.companyId;
            if (bean.members == null) {
                bean.members = new ArrayList<>();
            }
            bean.members.add(0, account.id);
            bean.members = BizUtil.distinct(bean.members);
            bean.memberInfos = getAccountSimpleInfoList(account.companyId, bean.members);
            BizUtil.checkUniqueKeysOnAdd(dao, bean, "名称不能重复");
            BizUtil.checkValid(bean);
            return dao.add(bean);
        }

        @Transaction
        @Override
        public void updateCalendar(String token, CalendarInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            Calendar old = dao.getExistedByIdForUpdate(Calendar.class, bean.id);
            if (old.createAccountId != account.id) {
                throw new AppException("权限不足");
            }
            BizUtil.checkUniqueKeysOnUpdate(dao, bean, old, "名称不能重复");
            old.name = bean.name;
            old.members = bean.members;
            if (old.members == null) {
                bean.members = new ArrayList<>();
            }
            bean.members.add(0, old.createAccountId);//加回创建人
            bean.members = BizUtil.distinct(bean.members);
            old.memberInfos = getAccountSimpleInfoList(account.companyId, bean.members);
            old.updateAccountId = account.id;
            BizUtil.checkValid(bean);
            dao.update(old);
        }

        @Transaction
        @Override
        public void deleteCalendar(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            Calendar old = dao.getExistedByIdForUpdate(Calendar.class, id);
            if (old.isDelete) {
                return;
            }
            if (old.createAccountId != account.id) {
                throw new AppException("权限不足");
            }
            if (old.isDefault) {
                throw new AppException("默认日历不能删除");
            }
            old.isDelete = true;
            dao.update(old);
            //
            dao.deleteCalendarSchedules(id);
        }
        //

        /**
         * 通过ID查询日程
         */
        @Override
        public CalendarScheduleInfo getCalendarScheduleById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            CalendarScheduleInfo info = dao.getExistedById(CalendarScheduleInfo.class, id);
            Calendar Calendar = dao.getExistedById(Calendar.class, info.calendarId);
            checkIsCreaterOrMember(account, Calendar);
            return info;
        }

        private boolean isOutOfRange(Date startDate, Date endDate, Date date) {
            if (date.after(endDate)) {
                return true;
            }
            return date.before(startDate);
        }

        /**
         * 查询日程列表和总数
         */
        @Override
        public Map<String, Object> getCalendarScheduleList(String token, CalendarScheduleQuery query) {
            Map<String, Object> ret = new HashMap<>();
            if (query.calendarId == null) {
                throw new AppException("请选择日历");
            }
            if (query.startTimeStart == null) {
                query.startTimeStart = DateUtil.getNextDay(-7);//一周前
            }
            if (query.startTimeEnd == null) {
                query.startTimeEnd = DateUtil.getNextDay(7);//一周后
            }
            Account account = bizService.getExistedAccountByToken(token);
            query.createAccountOrMember = account.id;
            query.pageIndex = 1;
            query.pageSize = Integer.MAX_VALUE;
            setupQuery(account, query);
            //
            query.repeat = CalendarSchedule.REPEAT_永不;
            List<CalendarScheduleInfo> csList = dao.getList(query);
            query.repeat = null;
            query.excludeRepeat = Remind.REPEAT_不提醒;
            List<CalendarScheduleInfo> otherList = dao.getList(query);
            if (!otherList.isEmpty()) {
                csList.addAll(otherList);
            }
            List<CalendarScheduleInfo> finalList = new ArrayList<>();
            for (CalendarScheduleInfo e : csList) {
                finalList.add(e);
                if (e.repeat == CalendarScheduleInfo.REPEAT_每天) {
                    int days = DateUtil.differentDays(e.startTime, query.startTimeEnd);
                    for (int i = 1; i <= days; i++) {
                        CalendarScheduleInfo newRemind = BizUtil.clone(CalendarScheduleInfo.class, e);
                        newRemind.startTime = DateUtil.getNextDay(e.startTime, i);
                        newRemind.endTime = DateUtil.getNextDay(e.endTime, i);
                        newRemind.uuid = BizUtil.randomUUID();
                        if (!isOutOfRange(query.startTimeStart, query.startTimeEnd, newRemind.startTime)) {
                            finalList.add(newRemind);
                        }
                    }
                }
                if (e.repeat == CalendarScheduleInfo.REPEAT_每周) {
                    for (int i = 1; i <= 4; i++) {
                        CalendarScheduleInfo newRemind = BizUtil.clone(CalendarScheduleInfo.class, e);
                        newRemind.startTime = DateUtil.getNextDay(e.startTime, 7 * i);
                        newRemind.endTime = DateUtil.getNextDay(e.endTime, 7 * i);
                        newRemind.uuid = BizUtil.randomUUID();
                        if (!isOutOfRange(query.startTimeStart, query.startTimeEnd, newRemind.startTime)) {
                            finalList.add(newRemind);
                        }
                    }
                }
                if (e.repeat == CalendarScheduleInfo.REPEAT_每两周) {
                    for (int i = 1; i <= 4; i++) {
                        CalendarScheduleInfo newRemind = BizUtil.clone(CalendarScheduleInfo.class, e);
                        newRemind.startTime = DateUtil.getNextDay(e.startTime, 14 * i);
                        newRemind.endTime = DateUtil.getNextDay(e.endTime, 14 * i);
                        newRemind.uuid = BizUtil.randomUUID();
                        if (!isOutOfRange(query.startTimeStart, query.startTimeEnd, newRemind.startTime)) {
                            finalList.add(newRemind);
                        }
                    }
                }
                if (e.repeat == CalendarScheduleInfo.REPEAT_每月) {
                    CalendarScheduleInfo newRemind = BizUtil.clone(CalendarScheduleInfo.class, e);
                    newRemind.startTime = DateUtil.getNextMonth(e.startTime, 1);
                    newRemind.endTime = DateUtil.getNextMonth(e.endTime, 1);
                    newRemind.uuid = BizUtil.randomUUID();
                    if (!isOutOfRange(query.startTimeStart, query.startTimeEnd, newRemind.startTime)) {
                        finalList.add(newRemind);
                    }
                }
                if (e.repeat == CalendarScheduleInfo.REPEAT_每年) {
                    CalendarScheduleInfo newRemind = BizUtil.clone(CalendarScheduleInfo.class, e);
                    newRemind.startTime = DateUtil.getNextYear(e.startTime, 1);
                    newRemind.endTime = DateUtil.getNextYear(e.endTime, 1);
                    newRemind.uuid = BizUtil.randomUUID();
                    if (!isOutOfRange(query.startTimeStart, query.startTimeEnd, newRemind.startTime)) {
                        finalList.add(newRemind);
                    }
                }
            }
            //
            if (logger.isDebugEnabled()) {
                logger.debug("finalList:{}", cornerstone.biz.util.DumpUtil.dump(finalList));
            }
            //
            ret.put("list", finalList);
            ret.put("calendar", dao.getExistedById(CalendarInfo.class, query.calendarId));
            return ret;
        }

        /**
         * 新增日程
         */
        @Transaction
        @Override
        public int addCalendarSchedule(String token, CalendarScheduleInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            Calendar Calendar = dao.getExistedById(Calendar.class, bean.calendarId);
            checkIsCreaterOrMember(account, Calendar);
            bean.createAccountId = account.id;
            bean.companyId = account.companyId;
            bean.uuid = BizUtil.randomUUID();
            BizUtil.checkValid(bean);
            if (bean.endTime.before(bean.startTime)) {
                throw new AppException("结束时间不能早于开始时间");
            }
            if (bean.ownerAccountIdList == null) {
                bean.ownerAccountIdList = new ArrayList<>();
            }
            bean.ownerAccountList = getAccountSimpleInfoList(account.companyId, bean.ownerAccountIdList);
            return dao.add(bean);
        }

        /**
         * 编辑日程
         */
        @Transaction
        @Override
        public void updateCalendarSchedule(String token, CalendarScheduleInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            CalendarSchedule old = dao.getExistedByIdForUpdate(CalendarSchedule.class, bean.id);
            Calendar calendar = dao.getExistedById(Calendar.class, old.calendarId);
            checkIsCreaterOrMember(account, calendar);
            old.name = bean.name;
            old.color = bean.color;
            old.startTime = bean.startTime;
            old.endTime = bean.endTime;
            old.repeat = bean.repeat;
            old.remark = bean.remark;
            old.updateAccountId = account.id;
            if (bean.ownerAccountIdList == null) {
                bean.ownerAccountIdList = new ArrayList<>();
            }
            old.ownerAccountIdList = bean.ownerAccountIdList;
            old.ownerAccountList = getAccountSimpleInfoList(account.companyId, bean.ownerAccountIdList);
            BizUtil.checkValid(old);
            if (bean.endTime.before(bean.startTime)) {
                throw new AppException("结束时间不能早于开始时间");
            }
            dao.update(old);
        }

        /**
         * 拖动日程
         */
        @Transaction
        @Override
        public void dragCalendarSchedule(String token, CalendarScheduleDragInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            CalendarSchedule old = dao.getExistedByIdForUpdate(CalendarSchedule.class, bean.id);
            Calendar calendar = dao.getExistedById(Calendar.class, old.calendarId);
            checkIsCreaterOrMember(account, calendar);
            if (bean.startTime != null && bean.endTime != null) {
                if (bean.endTime.before(bean.startTime)) {
                    throw new AppException("结束时间不能早于开始时间");
                }
                old.startTime = bean.startTime;
                old.endTime = bean.endTime;
            }
            if (bean.oldOwnerAccountId != null && bean.newOwnerAccountId != null) {
                if (old.ownerAccountIdList == null) {
                    old.ownerAccountIdList = new ArrayList<>();
                }
                old.ownerAccountIdList.remove(bean.oldOwnerAccountId);
                old.ownerAccountIdList.add(bean.newOwnerAccountId);
                old.ownerAccountIdList = BizUtil.distinct(old.ownerAccountIdList);
                old.ownerAccountList = getAccountSimpleInfoList(account.companyId, old.ownerAccountIdList);
            }
            old.updateAccountId = account.id;
            BizUtil.checkValid(old);
            dao.update(old);
        }

        /**
         * 删除日程
         */
        @Transaction
        @Override
        public void deleteCalendarSchedule(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            CalendarSchedule old = dao.getExistedByIdForUpdate(CalendarSchedule.class, id);
            Calendar calendar = dao.getExistedById(Calendar.class, old.calendarId);
            checkIsCreaterOrMember(account, calendar);
            if (old.isDelete) {
                return;
            }
            old.isDelete = true;
            old.updateAccountId = account.id;
            dao.update(old);
        }

        //
        @Override
        public String startSftp(String token, String machineLoginToken) {
            SshChannel sshChannel = getSshChannel(machineLoginToken);
            SftpChannel sftpChannel = sshChannel.startSftp();
            return sftpChannel.uuid;
        }

        //
        private SshChannel getSshChannel(String machineLoginToken) {
            ConnectionDetailInfo detailInfo = bizService.getConnectionDetailInfo0(machineLoginToken);
            ConnectionInfo conn = detailInfo.connectionInfo;
            if (conn == null) {
                logger.error("conn==null token not found." + machineLoginToken);
                throw new AppException("token已经过期");
            }
            SshChannel sshChannel = SshChannel.sshChannelMap.get(machineLoginToken);
            if (sshChannel == null) {
                logger.error("sshChannel==null token not found." + machineLoginToken);
                throw new AppException("token已经过期");
            }
            return sshChannel;
        }

        //
        @Override
        public SftpInfo getSftpInfo(String token, String machineLoginToken) {
            SshChannel sshChannel = getSshChannel(machineLoginToken);
            SftpChannel sftpChannel = sshChannel.startSftp();
            SftpInfo info = new SftpInfo();
            info.pwd = sftpChannel.pwd();
            List<SftpFile> fileList = sftpChannel.ls(".");
            List<SftpFile> finalFileList = new ArrayList<>();
            Iterator<SftpFile> iterator = fileList.iterator();
            while (iterator.hasNext()) {
                SftpFile file = iterator.next();
                if (".".equals(file.filename)) {//不用这个
                    iterator.remove();
                }
                if ("..".equals(file.filename)) {//永远放在第一个
                    finalFileList.add(file);
                    iterator.remove();
                }
            }
            fileList.sort((a, b) -> {
                char[] chars1 = a.filename.toCharArray();
                char[] chars2 = b.filename.toCharArray();
                int i = 0;
                while (i < chars1.length && i < chars2.length) {
                    if (chars1[i] > chars2[i]) {
                        return 1;
                    } else if (chars1[i] < chars2[i]) {
                        return -1;
                    } else {
                        i++;
                    }
                }
                if (i == chars1.length) {  //o1到头
                    return -1;
                }
                if (i == chars2.length) { //o2到头
                    return 1;
                }
                return 0;
            });
            if (!fileList.isEmpty()) {
                finalFileList.addAll(fileList);
            }
            info.fileList = finalFileList;
            return info;
        }

        //
        @Override
        public void sftpCd(String token, String machineLoginToken, String dir) {
            SshChannel sshChannel = getSshChannel(machineLoginToken);
            SftpChannel sftpChannel = sshChannel.startSftp();
            sftpChannel.cd(dir);
        }

        @Override
        public void sftpRm(String token, String machineLoginToken, String path) {
            SshChannel sshChannel = getSshChannel(machineLoginToken);
            SftpChannel sftpChannel = sshChannel.startSftp();
            sftpChannel.rm(path);
        }

        @Override
        public void sftpRmdir(String token, String machineLoginToken, String path) {
            SshChannel sshChannel = getSshChannel(machineLoginToken);
            SftpChannel sftpChannel = sshChannel.startSftp();
            sftpChannel.rmdir(path);
        }

        @Override
        public void sftpMkdir(String token, String machineLoginToken, String path) {
            SshChannel sshChannel = getSshChannel(machineLoginToken);
            SftpChannel sftpChannel = sshChannel.startSftp();
            sftpChannel.mkdir(path);
        }

        @Override
        public ConnectionDetailInfo getConnectionDetailInfo(String token, String machineLoginToken) {
            return bizService.getConnectionDetailInfo0(machineLoginToken);
        }

        @Override
        public String pingBiz() {
            return "OK";
        }

        @Transaction
        @Override
        public LarkLoginInfo larkAuthorize(String token, String data) {
            LarkLoginInfo result = new LarkLoginInfo();
            try {
                result.token = token;
                if (StringUtil.isEmpty(data)) {
                    logger.error("authorize data is empty");
                    result.code = LarkLoginInfo.CODE_授权失败;
                    result.message = "授权失败";
                    return result;
                }
                String decode = new String(Base64.getUrlDecoder().decode(data));
                LarkAuthorize larkUser = JSONUtil.fromJson(decode, LarkAuthorize.class);
                logger.debug("larkAuthorize decode:{} larkUser:{}", decode, DumpUtil.dump(larkUser));
                //
                //larkUser.openId=new String(Base64.getUrlDecoder().decode(larkUser.openId));
                String larkOpenId = TripleDESUtil.decrypt(larkUser.openId, ConstDefine.GLOBAL_KEY);
                Account bindAccount = dao.getAccountByLarkOpenId(larkOpenId);
                if (bindAccount != null) {//已经绑定账号
                    result.code = LarkLoginInfo.CODE_已有账号绑定;
                    LoginResult loginResult = new LoginResult();
                    boolean isOk = checkLogin(loginResult, bindAccount);
                    if (!isOk) {
                        logger.warn("result:{}", DumpUtil.dump(loginResult));
                        result.code = LarkLoginInfo.CODE_授权失败;
                        result.message = "授权失败";
                        return result;
                    }
                    AccountToken at = loginSuccess(bindAccount);
                    result.token = at.token;
                } else {
                    Account loginAccount = bizService.getAccountByToken(token);
                    if (loginAccount != null) {//如果已经登录了
                        loginAccount.larkOpenId = larkOpenId;
                        loginAccount.larkTenantKey = larkUser.tenantKey;
                        dao.updateSpecialFields(loginAccount, "larkOpenId", "larkTenantKey");
                        result.code = LarkLoginInfo.CODE_绑定已登录账户;
                        AutoTranscationCallback.registerSynchronization(new TransactionSynchronizationAdapter() {
                            @Override
                            public void afterCommit() {
                                larkService.pushBindSuccessMessage(larkOpenId, null, larkUser.tenantKey, null);
                            }
                        });
                    } else {//
                        result.code = LarkLoginInfo.CODE_没有账号绑定;
                    }
                }
                //
                return result;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                result.code = LarkLoginInfo.CODE_授权失败;
                result.message = "授权失败";
                return result;
            }
        }

        @Transaction
        @Override
        public String larkRegister(String code, String tenantKey) {
            if (StringUtil.isEmpty(code)) {
                return null;
            }
            LarkUserAccessToken2 token = larkService.getUserAccessToken2(code);
            logger.info("login LarkUserAccessToken:{}", DumpUtil.dump(token));
            //
            Account account = dao.getAccountByLarkOpenId(token.open_id);
            if (account != null) {
                AccountToken at = dao.getAccountTokenByAccountId(account.id);
                return at.token;
            }
            //
            LarkUserInfo2 userInfo = larkService.getUserInfo(token.access_token);
            //
            account = new Account();
            account.isActivated = true;
            account.status = Account.STATUS_有效;
            account.uuid = BizUtil.randomUUID();
            account.name = userInfo.name;
            account.userName = BizUtil.randomUUID();
            account.pinyinName = PinYinUtil.getHanziPinYin(account.name);
            account.mobileNo = userInfo.mobile;
            account.email = null;
            account.password = null;
            account.encryptPassword = null;
            account.larkOpenId = token.open_id;
            account.larkTenantKey = token.tenant_key;
            dao.add(account);
            //
            AccountToken at = loginSuccess(account);
            //
            final String larkOpenId = account.larkOpenId;
            final String larkTenantKey = account.larkTenantKey;
            AutoTranscationCallback.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    larkService.pushBindSuccessMessage(larkOpenId, null, larkTenantKey, null);
                }
            });
            //创建企业
            Company company = new Company();
            company.name = "我的企业";
            createCompany(at.token, company);
            //
            return at.token;
        }

        @Transaction
        @Override
        public String loginWithLarkOpenIdEncode(String openIdEncode) {
            //NPE
            if (BizUtil.isNullOrEmpty(openIdEncode)) {
                return null;
            }
            String larkOpenId = TripleDESUtil.decryptWithUrlEncoder(openIdEncode, ConstDefine.GLOBAL_KEY);
            Account account = dao.getAccountByLarkOpenId(larkOpenId);
            if (account == null) {
                logger.error("account==null larkOpenId:{}", larkOpenId);
                throw new AppException("权限不足");
            }
            AccountToken at = loginSuccess(account);
            return at.token;
        }

        @SuppressWarnings("unchecked")
        @Override
        public CompanyTaskReport getCompanyTaskReport(String token, CompanyTaskReportQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            Set<Integer> accountIds = null;
            if (query.departmentId != null) {
                Department department = dao.getDepartmentByCompanyIdId(account.companyId, query.departmentId);
                if (department == null) {
                    throw new AppException("部门不存在");
                }
                CompanyMemberQuery companyMemberQuery = new CompanyMemberQuery();
                companyMemberQuery.companyId = account.companyId;
                companyMemberQuery.departmentId = department.id;
                companyMemberQuery.accountStatus = Account.STATUS_有效;
                List<CompanyMemberInfo> list = dao.getList(companyMemberQuery);
                accountIds = list.stream().map(item -> item.accountId).collect(Collectors.toSet());
            }
            // 如果用户有传递accountIds参数
            if (query.accountIds != null && !query.accountIds.isEmpty()) {
                // 如果没有部门参数
                if (accountIds == null) {
                    accountIds = new HashSet<>(query.accountIds);
                } else {
                    // 从部门用户id中移除不在用户传递的用户id列表中的部门用户
                    accountIds = accountIds.stream().filter(accountId -> query.accountIds.contains(accountId)).collect(Collectors.toSet());
                }
            }
            if (accountIds == null) {
                List<Integer> accountIdList = dao.getValidAccountIds(account.companyId);
                accountIds = new HashSet<>(accountIdList);
            }
            int[] ownerAccountIdList = accountIds.stream().mapToInt(value -> value).toArray();
            //
            logger.info("ownerAccountIdList:{}", DumpUtil.dump(ownerAccountIdList));
            //
            CompanyTaskReport report = new CompanyTaskReport();
            //完成任务数量
            TaskQuery taskQuery = new TaskQuery();
            setupQuery(account, taskQuery);
            taskQuery.pageSize = 10000;
            taskQuery.ownerAccountIdList = ownerAccountIdList;
            taskQuery.projectId = query.projectId;
            taskQuery.finishTimeStart = query.startDate;
            taskQuery.finishTimeEnd = query.endDate;
            taskQuery.projectStatus = Project.STATUS_运行中;
            taskQuery.isFinish = true;
            taskQuery.projectIdInList = BizUtil.convertList(query.projectIdInList);
            Set<String> includeField = new HashSet<>();
            includeField.add("id");
            includeField.add("ownerAccountList");
            includeField.add("createTime");
            Map<String, Object> ret = bizService.getTaskInfoList0(account, taskQuery, includeField, false);
            report.finishTaskNum = (int) ret.get("count");
            report.finishTaskList = new ArrayList<>();
            List<TaskInfo> finishTaskList = (List<TaskInfo>) ret.get("list");
            Map<Integer, TaskAccount> finishTaskRank = new HashMap<>();
            for (TaskInfo e : finishTaskList) {
                for (AccountSimpleInfo ownerAccount : e.ownerAccountList) {
                    if (!accountIds.contains(ownerAccount.id)) {
                        continue;
                    }
                    TaskAccount ta = finishTaskRank.get(ownerAccount.id);
                    if (ta == null) {
                        ta = new TaskAccount();
                        ta.accountId = ownerAccount.id;
                        ta.accountName = ownerAccount.name;
                        ta.accountImageId = ownerAccount.imageId;
                        finishTaskRank.put(ownerAccount.id, ta);
                    }
                    ta.num++;

                    ReportTaskInfo finishTaskInfo = new ReportTaskInfo();
                    finishTaskInfo.accountId = ownerAccount.id;
                    finishTaskInfo.accountName = ownerAccount.name;
                    finishTaskInfo.accountImageId = ownerAccount.imageId;
                    finishTaskInfo.taskId = e.id;
                    finishTaskInfo.taskUuid = e.uuid;
                    finishTaskInfo.taskSerialNo = e.serialNo;
                    finishTaskInfo.taskName = e.name;
                    finishTaskInfo.endDate = e.endDate;
                    finishTaskInfo.finishDate = e.finishTime;
                    finishTaskInfo.objectTypeName = e.objectTypeName;
                    finishTaskInfo.projectName = e.projectName;
                    report.finishTaskList.add(finishTaskInfo);
                }
            }
            report.finishTaskAccountList = new ArrayList<>(finishTaskRank.values());
            report.finishTaskAccountList.sort((a, b) -> b.num - a.num);
            //延期任务数量
            taskQuery = new TaskQuery();
            setupQuery(account, taskQuery);
            taskQuery.projectStatus = Project.STATUS_运行中;
            taskQuery.pageSize = 10000;
            taskQuery.ownerAccountIdList = ownerAccountIdList;
            taskQuery.projectId = query.projectId;
            taskQuery.endDateStart = query.startDate;
            taskQuery.endDateEnd = query.endDate;
            taskQuery.isDelay = true;
//            taskQuery.isFinish = false;
            taskQuery.projectIdInList = BizUtil.convertList(query.projectIdInList);
            ret = bizService.getTaskInfoList0(account, taskQuery, null, false);
            report.delayTaskNum = (int) ret.get("count");
            //
            List<TaskInfo> delayTaskList = (List<TaskInfo>) ret.get("list");
            Map<Integer, TaskAccount> delayTaskRank = new HashMap<>();
            report.delayTaskList = new ArrayList<>();
            for (TaskInfo e : delayTaskList) {
                for (AccountSimpleInfo ownerAccount : e.ownerAccountList) {
                    if (!accountIds.contains(ownerAccount.id)) {
                        continue;
                    }
                    TaskAccount ta = delayTaskRank.get(ownerAccount.id);
                    if (ta == null) {
                        ta = new TaskAccount();
                        ta.accountId = ownerAccount.id;
                        ta.accountName = ownerAccount.name;
                        ta.accountImageId = ownerAccount.imageId;
                        delayTaskRank.put(ownerAccount.id, ta);
                    }
                    ta.num++;
                    //
                    ReportTaskInfo delayTaskInfo = new ReportTaskInfo();
                    delayTaskInfo.accountId = ownerAccount.id;
                    delayTaskInfo.accountName = ownerAccount.name;
                    delayTaskInfo.accountImageId = ownerAccount.imageId;
                    delayTaskInfo.taskId = e.id;
                    delayTaskInfo.taskUuid = e.uuid;
                    delayTaskInfo.taskSerialNo = e.serialNo;
                    delayTaskInfo.taskName = e.name;
                    delayTaskInfo.endDate = e.endDate;
                    delayTaskInfo.objectTypeName = e.objectTypeName;
                    delayTaskInfo.projectName = e.projectName;
                    report.delayTaskList.add(delayTaskInfo);
                }
            }
            report.delayTaskAccountList = new ArrayList<>(delayTaskRank.values());
            report.delayTaskAccountList.sort((a, b) -> b.num - a.num);

            //创建任务数量
            taskQuery = new TaskQuery();
            setupQuery(account, taskQuery);
            taskQuery.projectStatus = Project.STATUS_运行中;
            taskQuery.ownerAccountIdList = ownerAccountIdList;
            taskQuery.projectId = query.projectId;
            taskQuery.createTimeStart = query.startDate;
            taskQuery.createTimeEnd = query.endDate;
            taskQuery.projectIdInList = BizUtil.convertList(query.projectIdInList);
            includeField = new HashSet<>();
            includeField.add("id");
            includeField.add("createTime");
            ret = bizService.getTaskInfoList0(account, taskQuery, includeField, false);
            report.createTaskNum = (int) ret.get("count");
            report.createTaskList = new ArrayList<>();
            //
            List<TaskInfo> createTaskList = (List<TaskInfo>) ret.get("list");
            Map<Integer, TaskAccount> createTaskRank = new HashMap<>();
            for (TaskInfo e : createTaskList) {
                for (AccountSimpleInfo ownerAccount : e.ownerAccountList) {
                    if (!accountIds.contains(ownerAccount.id)) {
                        continue;
                    }
                    TaskAccount ta = createTaskRank.get(ownerAccount.id);
                    if (ta == null) {
                        ta = new TaskAccount();
                        ta.accountId = ownerAccount.id;
                        ta.accountName = ownerAccount.name;
                        ta.accountImageId = ownerAccount.imageId;
                        createTaskRank.put(ownerAccount.id, ta);
                    }
                    ta.num++;
                    ReportTaskInfo createTaskInfo = new ReportTaskInfo();
                    createTaskInfo.accountId = ownerAccount.id;
                    createTaskInfo.accountName = ownerAccount.name;
                    createTaskInfo.accountImageId = ownerAccount.imageId;
                    createTaskInfo.taskId = e.id;
                    createTaskInfo.taskUuid = e.uuid;
                    createTaskInfo.taskSerialNo = e.serialNo;
                    createTaskInfo.taskName = e.name;
                    createTaskInfo.createDate = e.createTime;
                    createTaskInfo.objectTypeName = e.objectTypeName;
                    createTaskInfo.projectName = e.projectName;
                    report.createTaskList.add(createTaskInfo);
                }
            }
            report.createTaskAccountList = new ArrayList<>(createTaskRank.values());
            report.createTaskAccountList.sort((a, b) -> b.num - a.num);
            return report;
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<CompanyMemberTodoTask> getCompanyMemberTodoTask(String token, CompanyMemberTodoTask.CompanyMemberTodoTaskQuery query) {
            List<CompanyMemberTodoTask> result = new ArrayList<>();
            Account account = bizService.getExistedAccountByToken(token);
            Set<Integer> accountIds = null;
            if (query.departmentId != null) {
                Department department = dao.getDepartmentByCompanyIdId(account.companyId, query.departmentId);
                if (department == null) {
                    throw new AppException("部门不存在");
                }
                CompanyMemberQuery companyMemberQuery = new CompanyMemberQuery();
                companyMemberQuery.companyId = account.companyId;
                companyMemberQuery.departmentId = department.id;
                companyMemberQuery.accountStatus = Account.STATUS_有效;
                List<CompanyMemberInfo> list = dao.getList(companyMemberQuery);
                accountIds = list.stream().map(item -> item.accountId).collect(Collectors.toSet());
            }
            // 如果用户有传递accountIds参数
            if (!BizUtil.isNullOrEmpty(query.accountIds)) {
                if (accountIds == null) {
                    accountIds = new HashSet<>(query.accountIds);
                } else {
                    // 从部门用户id中移除不在用户传递的用户id列表中的部门用户
                    accountIds = accountIds.stream().filter(accountId -> query.accountIds.contains(accountId)).collect(Collectors.toSet());
                }
            }
            if (accountIds == null) {
                List<Integer> accountIdList = dao.getValidAccountIds(account.companyId);
                accountIds = new HashSet<>(accountIdList);
            }
            int[] ownerAccountIdList = BizUtil.convertList(accountIds);
            //
            logger.info("ownerAccountIdList:{}", DumpUtil.dump(ownerAccountIdList));
            if (BizUtil.isNullOrEmpty(ownerAccountIdList)) {
                return Collections.emptyList();
            }
            //
            CompanyTaskReport report = new CompanyTaskReport();
            //待办任务数量
            TaskQuery taskQuery = new TaskQuery();
            setupQuery(account, taskQuery);
            if (!BizUtil.isNullOrEmpty(query.projectIdInList)) {
                taskQuery.projectIdInList = BizUtil.convertList(query.projectIdInList);
            }
            taskQuery.pageSize = -1;
            taskQuery.ownerAccountIdList = ownerAccountIdList;
            taskQuery.projectId = query.projectId;
//            taskQuery.startDateStart = query.startDate;
//            taskQuery.endDateEnd = query.endDate;
            taskQuery.projectStatus = Project.STATUS_运行中;
            taskQuery.isFinish = false;
            int[] objectTypes = bizService.getStatusBasedObjectTypeListByCompanyId(account.companyId);
            if (!BizUtil.isNullOrEmpty(objectTypes)) {
                objectTypes = BizUtil.deleteElement(objectTypes, Task.OBJECTTYPE_项目清单);
                taskQuery.objectTypeInList = objectTypes;
            }

            Map<String, Object> ret = bizService.getTaskInfoList0(account, taskQuery, null, false);
            report.finishTaskNum = (int) ret.get("count");
            report.finishTaskList = new ArrayList<>();
            List<TaskInfo> todoTaskList = (List<TaskInfo>) ret.get("list");
            Set<Integer> accountList = new HashSet<>();
            Map<Integer, List<TaskInfo>> accountTaskMap = new HashMap<>(16);

            if (!BizUtil.isNullOrEmpty(todoTaskList)) {
                //分类列表
                CategoryQuery cq = new CategoryQuery();
                cq.pageSize = -1;
                setupQuery(account, cq);
                List<Category> categoryList = dao.getList(cq);
                Map<Integer, Category> categoryMap = new HashMap<>();
                if (!BizUtil.isNullOrEmpty(categoryList)) {
                    categoryMap = categoryList.stream().collect(Collectors.toMap(k -> k.id, v -> v));
                }
                //存在未设置开始截止时间的任务，代码中进行过滤
                for (TaskInfo t : todoTaskList) {

                    boolean hasTimeQuery = null != query.startDate || null != query.endDate;
                    boolean timed = false;

                    //没有过滤时间或者任务时间与查询时间有交集存在
                    if (null == t.startDate) {
                        t.startDate = t.endDate;
                    }
                    if (null == t.endDate && null != t.startDate) {
                        t.endDate = t.startDate;
                    }
                    if (null != t.startDate && null != t.endDate && null != query.startDate && null != query.endDate) {
                        timed = t.startDate.before(query.endDate) && t.startDate.after(query.startDate)
                                || (t.endDate.before(query.endDate) && t.endDate.after(query.startDate))
                                || (t.startDate.before(query.startDate) && t.endDate.after(query.endDate));
                    }
                    if (!hasTimeQuery || timed) {
                        List<Integer> ids = t.ownerAccountIdList;
                        if (!BizUtil.isNullOrEmpty(ids)) {
                            for (Integer accountId : ids) {
                                if (accountIds.contains(accountId)) {
                                    accountList.add(accountId);
                                    accountTaskMap.computeIfAbsent(accountId, k -> new ArrayList<>()).add(t);
                                }
                            }
                        }
                    }
                }

                if (!BizUtil.isNullOrEmpty(accountList)) {
                    for (Integer accountId : accountList) {
                        CompanyMemberTodoTask todo = new CompanyMemberTodoTask();
                        todo.accountId = accountId;
                        todo.tasks = new ArrayList<>();
                        List<TaskInfo> tasks = accountTaskMap.get(accountId);
                        for (TaskInfo task : tasks) {
                            AccountTodoTask at = new AccountTodoTask();
                            at.accountId = accountId;
                            at.companyId = task.companyId;
                            at.projectId = task.projectId;
                            at.projectName = task.projectName;
                            at.taskId = task.id;
                            at.name = task.name;
                            at.uuid = task.uuid;
                            at.serialNo = task.serialNo;
                            at.startDate = task.startDate;
                            if (null == todo.startDate || (null != task.startDate && task.startDate.before(todo.startDate))) {
                                todo.startDate = task.startDate;
                            }
                            at.endDate = task.endDate;
                            if (null == todo.endDate || (null != task.endDate && task.endDate.after(todo.endDate))) {
                                todo.endDate = task.endDate;
                            }
                            at.isFinish = task.isFinish;
                            at.objectType = task.objectType;
                            at.objectTypeName = task.objectTypeName;
                            at.status = task.status;
                            at.statusName = task.statusName;
                            at.statusColor = task.statusColor;
                            at.priority = task.priority;
                            at.priorityName = task.priorityName;
                            at.priorityColor = task.priorityColor;

                            List<Integer> categoryIds = task.categoryIdList;
                            if (!BizUtil.isNullOrEmpty(categoryIds)) {
                                at.categoryList = new ArrayList<>();
                                for (Integer categoryId : categoryIds) {
                                    if (!BizUtil.isNullOrEmpty(categoryMap.get(categoryId))) {
                                        at.categoryList.add(CategorySimpleInfo.createCategorySimpleinfo(categoryMap.get(categoryId)));
                                    }
                                }
                            }

                            List<AccountSimpleInfo> as = task.ownerAccountList;
                            for (AccountSimpleInfo a : as) {
                                if (a.id == accountId) {
                                    todo.accountImageId = a.imageId;
                                    todo.accountName = a.name;
                                    break;
                                }
                            }
                            todo.num++;
                            todo.tasks.add(at);
                        }
                        result.add(todo);
                    }
                }

            }
            return result;
        }

        @Override
        public String getFilePreviewLocation(String token, String fileId) {
            if (BizUtil.isNullOrEmpty(fileId)) {
                return null;
            }
            //默认使用live通道
            String wpsAppId = GlobalConfig.getValue("wps.appId");
            String wpsAppKey = GlobalConfig.getValue("wps.appKey");
            String owaUrl = GlobalConfig.getValue("owa.url");
            String previewLocation = null;
            if (StringUtil.isEmpty(wpsAppId) || StringUtil.isEmpty(wpsAppKey)) {
                String liveUrl = MessageFormat.format("{0}p/file/get_file_ex/{1}?token={2}", GlobalConfig.webUrl, fileId, token);
                //微软需要encode处理一下url地址，可以使用encodeURIComponent()方法，且文件的服务器地址必须是域名，不可以使用ip地址，且端口需要是80
                try {
                    previewLocation = MessageFormat.format("{0}/op/embed.aspx?src={1}", owaUrl, URLEncoder.encode(liveUrl, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    logger.error("getFilePreviewLocation ERR", e);
                }
            } else {
                String endType = BizUtil.getWpsRouteEndType(fileId);
                String fName = token.concat(",").concat(fileId);
                String base64fName = Base64.getUrlEncoder().encodeToString(fName.getBytes(StandardCharsets.UTF_8));
                String paramStr = MessageFormat.format("_w_appid={0}&_w_fname={1}", wpsAppId, base64fName);
                Map<String, String> params = new HashMap<>();
                params.put("_w_appid", wpsAppId);
                params.put("_w_fname", base64fName);
                String signature = BizUtil.getWpsSignature(params, wpsAppKey);
                previewLocation = MessageFormat.format("https://wwo.wps.cn/office{0}{1}&_w_signature={2}",
                        endType, paramStr, signature);
            }
            return previewLocation;
        }

        @Override
        public WpsFileInfo getWpsFileInfo(String base64fName, String reqSignature) {
            if (StringUtil.isEmpty(base64fName) || StringUtil.isEmpty(reqSignature)) {
                logger.warn("getWpsFileInfo error for base64fName#[{}],reqSignature#[{}}]", base64fName, reqSignature);
                return null;
            }
            String wpsAppId = GlobalConfig.getValue("wps.appId");
            String wpsAppKey = GlobalConfig.getValue("wps.appKey");
            Map<String, String> params = new HashMap<>();
            params.put("_w_appid", wpsAppId);
            params.put("_w_fname", base64fName);
            if (!BizUtil.checkWpsSignature(reqSignature, params, wpsAppKey)) {
                logger.warn("getWpsFileInfo error for base64fName#[{}],reqSignature#[{}}]", base64fName, reqSignature);
                return null;
            }
            String fName = new String(Base64.getUrlDecoder().decode(base64fName), StandardCharsets.UTF_8);
            String[] data = fName.split(",");
            if (data.length < 2) {
                return null;
            }
            String token = data[0];
            String fileId = data[1];
            Attachment attachment = dao.getAttachmentByUuid(fileId);
            Account account = bizService.getExistedAccountByToken(token);
            if (attachment == null || account == null || account.companyId != attachment.companyId) {
                return null;
            }
            String downloadUrl = MessageFormat.format("{0}p/file/get_file_ex/{1}?token={2}", GlobalConfig.webUrl, fileId, token);
            String avatarUrl = MessageFormat.format("{0}p/file/get_file_ex/{1}?token={2}", GlobalConfig.webUrl, account.imageId, token);
            WpsFileInfo fileInfo = new WpsFileInfo();
            WpsFileInfo.File wpsFile = new WpsFileInfo.File();
            wpsFile.id = MD5Util.encodeMD5(attachment.uuid).toLowerCase();
            wpsFile.name = attachment.name;
            wpsFile.size = attachment.size;
            wpsFile.version = 1;
            wpsFile.createTime = attachment.createTime.getTime() / 1000;
            wpsFile.creator = String.valueOf(attachment.createAccountId);
            wpsFile.modifier = String.valueOf(attachment.createAccountId);
            wpsFile.downloadUrl = downloadUrl;
            fileInfo.file = wpsFile;

            WpsFileInfo.User wpsUser = new WpsFileInfo.User();
            wpsUser.avatarUrl = avatarUrl;
            wpsUser.id = String.valueOf(account.id);
            wpsUser.name = String.valueOf(account.name);
            wpsUser.permission = "read";
            fileInfo.user = wpsUser;
            return fileInfo;
        }
        //
        //

        /**
         * 通过ID查询系统通知
         */
        @Override
        public SystemNotificationInfo getSystemNotificationById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_管理系统通知, account.companyId);
            SystemNotificationInfo info = dao.getExistedById(SystemNotificationInfo.class, id);
            bizService.checkCompanyPermission(account, info.companyId);
            return info;
        }

        /**
         * 查询系统通知列表和总数
         */
        @Override
        public Map<String, Object> getSystemNotificationList(String token, SystemNotificationQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_管理系统通知, account.companyId);
            setupQuery(account, query);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        /**
         * 新增系统通知
         */
        @Transaction
        @Override
        public int addSystemNotification(String token, SystemNotificationInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_管理系统通知, account.companyId);
            bean.createAccountId = account.id;
            bean.companyId = account.companyId;
            bean.status = SystemNotificationInfo.STATUS_有效;
            bean.nextNotifyTime = bizService.getNextNotifyTime(bean);
            BizUtil.checkValid(bean);
            return dao.add(bean);
        }

        /**
         * 编辑系统通知
         */
        @Transaction
        @Override
        public void updateSystemNotification(String token, SystemNotificationInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            SystemNotificationInfo old = dao.getExistedByIdForUpdate(SystemNotificationInfo.class, bean.id);
            bizService.checkCompanyPermission(account, Permission.ID_管理系统通知, old.companyId);
            old.name = bean.name;
            old.updateAccountId = account.id;
            old.accountList = bean.accountList;
            old.companyRoleList = bean.companyRoleList;
            old.projectRoleList = bean.projectRoleList;
            old.departmentList = bean.departmentList;
            old.title = bean.title;
            old.content = bean.content;
            old.notifyTime = bean.notifyTime;
            old.period = bean.period;
            old.periodSetting = bean.periodSetting;
            old.nextNotifyTime = bizService.getNextNotifyTime(old);
            dao.update(old);
        }

        @Override
        @Transaction
        public void setSystemNotificationStatus(String token, int id, int status) {
            Account account = bizService.getExistedAccountByToken(token);
            SystemNotificationInfo old = dao.getExistedByIdForUpdate(SystemNotificationInfo.class, id);
            bizService.checkCompanyPermission(account, Permission.ID_管理系统通知, old.companyId);
            old.updateAccountId = account.id;
            old.status = status == SystemNotification.STATUS_有效 ? SystemNotification.STATUS_有效 : SystemNotification.STATUS_无效;
            dao.update(old);
        }

        /**
         * 删除系统通知
         */
        @Transaction
        @Override
        public void deleteSystemNotification(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            SystemNotificationInfo old = dao.getExistedByIdForUpdate(SystemNotificationInfo.class, id);
            bizService.checkCompanyPermission(account, Permission.ID_管理系统通知, old.companyId);
            if (old.isDelete) {
                return;
            }
            old.isDelete = true;
            old.updateAccountId = account.id;
            dao.update(old);
        }

        //
        @Transaction
        @Override
        public Map<String, Object> getProjectSetInfo(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            if (!bizService.isCompanySuperBoss(account)) {
                bizService.checkCompanyPermission(account, Permission.ID_开启项目集管理模块, account.companyId);
            }
            //
            Project templateProject = dao.getByIdForUpdate(Project.class, Project.ID_项目集模板ID);//一定要forUpdate
            if (templateProject == null) {
                logger.error("templateProject==null");
                return createResult(new ArrayList<>(), 0);
            }
            ObjectType ot = dao.getObjectTypeBySystemName(ObjectType.SYSTEM_NAME_项目清单);
            if (ot == null) {
                logger.error("ot==null");
                return createResult(new ArrayList<>(), 0);
            }
            //
            Project project = dao.getProjectByCompanyIdNameTemplateId(account.companyId, "项目集管理", Project.ID_项目集模板ID);
            if (project == null) {
                project = new Project();
                project.name = "项目集管理";
                project.templateId = Project.ID_项目集模板ID;
                createProject0(account, project, Project.ID_项目集模板ID);
                bizService.syncTasksFromProject0(account, project.id);
                //
            }
            Map<String, Object> map = new HashMap<>();
            map.put("projectUuid", project.uuid);
            map.put("objectType", ot.id);
            //
            ProjectMemberQuery memberQuery = new ProjectMemberQuery();
            memberQuery.projectId = project.id;
            List<ProjectInfo> projectList = getMyProjectList0(account, memberQuery);
            map.put("projectList", projectList);
            //
            return map;
        }


        //
        @Transaction
        @Override
        public void syncTasksFromProject(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_开启项目集管理模块, account.companyId);
            Project templateProject = dao.getByIdForUpdate(Project.class, Project.ID_项目集模板ID);//一定要forUpdate
            if (templateProject == null) {
                logger.error("templateProject==null");
                return;
            }
            Project project = dao.getProjectByCompanyIdNameTemplateId(account.companyId,
                    "项目集管理", Project.ID_项目集模板ID);
            bizService.syncTasksFromProject0(account, project.id);
        }

        //
        @Override
        public CompanyVersionRepositoryInfo getCompanyVersionRepositoryById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_开启版本库模块, account.companyId);
            CompanyVersionRepositoryInfo bean = dao.getExistedById(CompanyVersionRepositoryInfo.class, id);
            return bean;
        }

        @Override
        public Map<String, Object> getCompanyVersionRepositoryList(String token, CompanyVersionRepositoryQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_开启版本库模块, account.companyId);
            setupQuery(account, query);
            if(!BizUtil.isNullOrEmpty(query.ownerDepartmentIds)){
                query.ownerDepartmentIds = getChildDepartmentIds(query.ownerDepartmentIds);
                query.ownerDepartmentId = null;
            }
//            return createResult(dao.getList(query), dao.getListCount(query));
            return bizService.getCompanyVersionRepositoryInfoList(query);
        }

        private int[] getChildDepartmentIds(int[] departmentIdList){
            DepartmentQuery departmentQuery = new DepartmentQuery();
            departmentQuery.type = Department.TYPE_组织架构;
            departmentQuery.pageSize = Integer.MAX_VALUE;
            List<Department> departmentList = dao.getList(departmentQuery);
            Set<Integer> departmentIds = new HashSet<>();
            for (int deptId : departmentIdList) {
                departmentIds.add(deptId);
                doChildDepartment(deptId,departmentIds,departmentList);
            }


            return BizUtil.convertList(departmentIds);
        }

        private void doChildDepartment(int departmentId, Set<Integer> departmentIds,  List<Department> list) {
            for (Department department : list) {
                if (department.parentId == departmentId) {
                    departmentIds.add(department.id);
                    doChildDepartment(department.id,departmentIds, list);
                }
            }
        }


        @Transaction
        @Override
        public int addCompanyVersionRepository(String token, CompanyVersionRepositoryInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_创建版本库, account.companyId);
            return addCompanyVersionRepository0(account, bean);
        }

        private int addCompanyVersionRepository0(Account account, CompanyVersionRepositoryInfo bean) {
            bean.createAccountId = account.id;
            bean.companyId = account.companyId;
            if (!BizUtil.isNullOrEmpty(bean.ownerAccountIdList)) {
                bean.ownerAccountList = getAccountSimpleInfoList(account.companyId, bean.ownerAccountIdList);
            } else {
                bean.ownerAccountList = new ArrayList<>();
            }
            if (!BizUtil.isNullOrEmpty(bean.ownerDepartmentIdList)) {
                bean.ownerDepartmentList = getDepartmentSimpleInfoList(account.companyId, bean.ownerDepartmentIdList);
            } else {
                bean.ownerDepartmentList = new ArrayList<>();
            }
            BizUtil.checkValid(bean);
            dao.add(bean);
            bizService.addChangeLog(account, bean.id, ChangeLog.TYPE_新增版本库, JSONUtil.toJson(bean));
            return bean.id;
        }


        @Transaction
        @Override
        public void updateCompanyVersionRepository(String token, CompanyVersionRepositoryInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_创建版本库, account.companyId);
            CompanyVersionRepository old = dao.getExistedById(CompanyVersionRepository.class, bean.id);
            old.name = bean.name;
            old.description = bean.description;
            old.updateAccountId = account.id;
            if (!BizUtil.isNullOrEmpty(bean.ownerAccountIdList)) {
                old.ownerAccountIdList = bean.ownerAccountIdList;
                old.ownerAccountList = getAccountSimpleInfoList(account.companyId, bean.ownerAccountIdList);
            } else {
                old.ownerAccountIdList = new ArrayList<>();
                old.ownerAccountList = new ArrayList<>();
            }
            old.latest = bean.latest;
            old.businessLeader = bean.businessLeader;
            if (!BizUtil.isNullOrEmpty(bean.ownerDepartmentIdList)) {
                old.ownerDepartmentIdList = bean.ownerDepartmentIdList;
                old.ownerDepartmentList = getDepartmentSimpleInfoList(account.companyId, bean.ownerDepartmentIdList);
            } else {
                old.ownerDepartmentIdList = new ArrayList<>();
                old.ownerDepartmentList = new ArrayList<>();
            }
            old.department = bean.department;
            old.releaseDate = bean.releaseDate;
            old.status = bean.status;
            old.isArch332n = bean.isArch332n;
            old.arch = bean.arch;

            dao.update(old);
            bizService.addChangeLog(account, bean.id, ChangeLog.TYPE_编辑版本库, JSONUtil.toJson(old));
        }



        @Transaction
        @Override
        public void deleteCompanyVersionRepository(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_删除版本库, account.companyId);
            CompanyVersionRepository old = dao.getExistedById(CompanyVersionRepository.class, id);
            if (old.isDelete) {
                return;
            }
            old.isDelete = true;
            old.updateAccountId = account.id;
            dao.update(old);
            bizService.addChangeLog(account, id, ChangeLog.TYPE_删除版本库, JSONUtil.toJson(old));
        }

        @Override
        public CompanyVersionInfo getCompanyVersionById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            CompanyVersionInfo bean = dao.getExistedById(CompanyVersionInfo.class, id);
            bizService.checkCompanyPermission(account, Permission.ID_开启版本库模块, account.companyId);
            List<CompanyVersion> totalTaskNums = dao.getCompanyVersionTotalTaskNums(Collections.singletonList(bean.id));
            List<CompanyVersion> finishTaskNums = dao.getCompanyVersionFinishTaskNums(Collections.singletonList(bean.id));
            if (!totalTaskNums.isEmpty()) {
                bean.totalTaskNum = totalTaskNums.get(0).totalTaskNum;
            }
            if (!finishTaskNums.isEmpty()) {
                bean.finishTaskNum = finishTaskNums.get(0).finishTaskNum;
            }
            return bean;
        }

        @Override
        public Map<String, Object> getCompanyVersionList(String token, CompanyVersionQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_开启版本库模块, account.companyId);
            setupQuery(account, query);
            List<CompanyVersion> list = dao.getList(query);
            List<Integer> versionIdList = new ArrayList<>();
            for (CompanyVersion e : list) {
                versionIdList.add(e.id);
            }
            if (versionIdList.size() > 0) {
                List<CompanyVersion> totalTaskNums = dao.getCompanyVersionTotalTaskNums(versionIdList);
                List<CompanyVersion> finishTaskNums = dao.getCompanyVersionFinishTaskNums(versionIdList);
                for (CompanyVersion total : totalTaskNums) {
                    CompanyVersion bean = BizUtil.getByTarget(list, "id", total.id);
                    bean.totalTaskNum = total.totalTaskNum;
                }
                for (CompanyVersion finish : finishTaskNums) {
                    CompanyVersion bean = BizUtil.getByTarget(list, "id", finish.id);
                    bean.finishTaskNum = finish.finishTaskNum;
                }
            }
            return createResult(list, dao.getListCount(query));
        }

        @Transaction
        @Override
        public int addCompanyVersion(String token, CompanyVersionInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            CompanyVersionRepository repository = dao.getExistedById(CompanyVersionRepository.class, bean.repositoryId);
            BizUtil.asserts(repository.status != CompanyVersionRepository.STATUS_已关闭, "系统已关闭，无法创建新的版本");
            bizService.checkCompanyPermission(account, Permission.ID_创建版本, account.companyId);
            if (!BizUtil.isNullOrEmpty(bean.ownerAccountIdList)) {
                bean.ownerAccountList = getAccountSimpleInfoList(account.companyId, bean.ownerAccountIdList);
            } else {
                bean.ownerAccountList = new ArrayList<>();
            }
            bean.createAccountId = account.id;
            bean.companyId = repository.companyId;
            BizUtil.checkValid(bean);
            dao.add(bean);
            bizService.addChangeLog(account, bean.id, ChangeLog.TYPE_新增版本库版本, JSONUtil.toJson(bean));
            return bean.id;
        }

        @Transaction
        @Override
        public void updateCompanyVersion(String token, CompanyVersionInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            CompanyVersion old = dao.getExistedById(CompanyVersion.class, bean.id);
            CompanyVersionRepository repository = dao.getExistedById(CompanyVersionRepository.class, old.repositoryId);
            bizService.checkCompanyPermission(account, Permission.ID_创建版本, account.companyId);
            BizUtil.asserts(repository.status != CompanyVersionRepository.STATUS_已关闭, "系统已关闭，无法编辑版本");
//            if (bean.status == CompanyVersion.STATUS_已发布) {
////                repository.releaseDate = bean.endTime;
//                repository.latest = bean.versionNo;
//                dao.updateSpecialFields(repository,  "latest");
//            }
            if (!BizUtil.isNullOrEmpty(bean.ownerAccountIdList)) {
                old.ownerAccountIdList = bean.ownerAccountIdList;
                old.ownerAccountList = getAccountSimpleInfoList(account.companyId, bean.ownerAccountIdList);
            } else {
                old.ownerAccountIdList = new ArrayList<>();
                old.ownerAccountList = new ArrayList<>();
            }
            old.name = bean.name;
            old.startTime = bean.startTime;
            old.endTime = bean.endTime;
            old.status = bean.status;
            old.remark = bean.remark;
            old.updateAccountId = account.id;
//            old.ownerAccountId = bean.ownerAccountId;
            old.versionNo = bean.versionNo;
            dao.update(old);
            bizService.addChangeLog(account, bean.id, ChangeLog.TYPE_编辑版本库版本, JSONUtil.toJson(old));
        }

        @Transaction
        @Override
        public void deleteCompanyVersion(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_删除版本, account.companyId);
            CompanyVersion old = dao.getExistedById(CompanyVersion.class, id);
            if (old.isDelete) {
                return;
            }
            CompanyVersionRepository repository = dao.getExistedById(CompanyVersionRepository.class, old.repositoryId);
            BizUtil.asserts(repository.status != CompanyVersionRepository.STATUS_已关闭, "系统已关闭，无法删除版本");
            old.isDelete = true;
            old.updateAccountId = account.id;
            dao.update(old);
            bizService.addChangeLog(account, id, ChangeLog.TYPE_删除版本库版本, JSONUtil.toJson(old));
        }

        @Override
        public CompanyVersionTaskInfo getCompanyVersionTaskById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            CompanyVersionTaskInfo bean = dao.getExistedById(CompanyVersionTaskInfo.class, id);
            bizService.checkPermission(account, bean.companyId);
            return bean;
        }

        @Override
        public Map<String, Object> getCompanyVersionTaskList(String token, CompanyVersionTaskQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            setupQuery(account, query);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        @Transaction
        @Override
        public void addCompanyVersionTaskList(String token, int versionId, List<Integer> taskIdList) {
            Account account = bizService.getExistedAccountByToken(token);
            CompanyVersion version = dao.getExistedById(CompanyVersion.class, versionId);
            BizUtil.asserts(version.status != CompanyVersion.STATUS_已发布, "系统版本已发布，无法添加发布内容");
            bizService.checkCompanyPermission(account, Permission.ID_创建版本, version.companyId);
            List<TaskSimpleInfo> taskList = new ArrayList<>();
            for (Integer taskId : taskIdList) {
                TaskInfo task = dao.getExistedById(TaskInfo.class, taskId);
                task.repositoryId = version.repositoryId;
                task.repositoryVersionId = version.id;
                dao.updateSpecialFields(task, "repositoryId", "repositoryVersionId");
                bizService.checkPermission(account, task.companyId);
                CompanyVersionTask old = dao.getCompanyVersionTask(versionId, taskId);
                if (old != null) {
                    continue;
                }
                old = new CompanyVersionTask();
                old.companyId = account.companyId;
                old.versionId = version.id;
                old.repositoryId = version.repositoryId;
                old.projectId = task.projectId;
                old.taskId = task.id;
                old.objectType = task.objectType;
                old.createAccountId = account.id;
                dao.add(old);
                taskList.add(TaskSimpleInfo.createTaskSimpleinfo(task));
            }
            //
            if (taskList.size() > 0) {
                bizService.addChangeLog(account, versionId, ChangeLog.TYPE_版本库版本关联对象, JSONUtil.toJson(taskList));
            }
        }

        @Transaction
        @Override
        public void batchDeleteCompanyVersionTaskList(String token, int versionId, List<Integer> idList) {
            Account account = bizService.getExistedAccountByToken(token);
            CompanyVersion version = dao.getExistedById(CompanyVersion.class, versionId);
            bizService.checkCompanyPermission(account, Permission.ID_创建版本, version.companyId);
            BizUtil.asserts(version.status != CompanyVersion.STATUS_已发布, "系统版本已发布，无法删除发布内容");
            List<TaskSimpleInfo> taskList = new ArrayList<>();
            for (Integer id : idList) {
                CompanyVersionTask old = dao.getExistedById(CompanyVersionTask.class, id);
                if (old.versionId != versionId) {
                    throw new AppException("权限不足");
                }
                bizService.checkPermission(account, old.companyId);
                dao.deleteById(CompanyVersionTask.class, id);
                TaskInfo taskInfo = dao.getExistedById(TaskInfo.class, old.taskId);
                taskInfo.repositoryId = 0;
                taskInfo.repositoryVersionId = 0;
                dao.updateSpecialFields(taskInfo, "repositoryId", "repositoryVersionId");
                taskList.add(TaskSimpleInfo.createTaskSimpleinfo(taskInfo));
            }
            if (taskList.size() > 0) {
                bizService.addChangeLog(account, versionId, ChangeLog.TYPE_版本库版本删除对象, JSONUtil.toJson(taskList));
            }
        }

        @Override
        public Map<String, Object> getCompanyVersionTaskChangeLogList(String token, int versionId,
                                                                      ChangeLogQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            CompanyVersion version = dao.getExistedById(CompanyVersion.class, versionId);
            bizService.checkCompanyPermission(account, version.companyId);
            List<Integer> taskIdList = dao.getCompanyVersionTaskIdList(versionId);
            if (taskIdList.isEmpty()) {
                return createEmptyResult();
            }
            query.taskIdInList = BizUtil.convertList(taskIdList);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        @Override
        public List<String> getProjectGroups(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            return dao.getProjectGroupNames(account.companyId);
        }

        @Override
        public List<AccoutBugStat> getAccountBugStatList(String token, AccoutBugStatQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            query.companyId = account.companyId;
            query.objectType = Task.OBJECTTYPE_任务;
            List<AccoutBugStat> list = statDao.getAccoutTaskStatList(query, "total_task_num");
            query.objectType = Task.OBJECTTYPE_缺陷;
            List<AccoutBugStat> totalBugNumList = statDao.getAccoutBugStatList(query, "total_bug_num");
            query.isReOpen = true;
            List<AccoutBugStat> reopenBugNumList = statDao.getAccoutBugStatList(query, "reopen_bug_num");
            for (AccoutBugStat e : list) {
                AccoutBugStat bugNum = BizUtil.getByTarget(totalBugNumList, "accountId", e.accountId);
                AccoutBugStat reopenBugNum = BizUtil.getByTarget(reopenBugNumList, "accountId", e.accountId);
                if (bugNum != null) {
                    e.totalBugNum = bugNum.totalBugNum;
                    if (reopenBugNum != null) {
                        e.reopenBugNum = reopenBugNum.reopenBugNum;
                    }
                }
            }
            list.sort((a, b) -> b.totalBugNum - a.totalBugNum);
            return list;
        }
    }
}
