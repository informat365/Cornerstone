package cornerstone.biz.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 权限
 *
 * @author 杜展扬 2018-07-30
 *
 */
@DomainDefine(domainClass = Permission.class)
@DomainDefineValid(comment ="权限" ,uniqueKeys={@UniqueKey(fields={"name"})})
public class Permission{
	//权限类型
	public static final int TYPE_项目=1;
	public static final int TYPE_全局=2;
	//
	////////////////////////////////////////全局权限///////////////////////////////////

	//报表
	public static final String ID_报表管理="chart";
	public static final String ID_开启报表模块="chart_view";
	public static final String ID_查看企业报表="chart_company_view";
	public static final String ID_查看项目报表="chart_project_view";
	public static final String ID_查看数据表格="chart_data_table_view";
	public static final String ID_创建数据表格="chart_data_table_create";

	//CMDB
	public static final String ID_CMDB="cmdb";
	public static final String ID_查看CMDB="cmdb_view";

	//代码助手(codegen)
	public static final String ID_代码助手="codegen";
	public static final String ID_代码设计器="codegen_code_desginer";
	public static final String ID_数据库配置="codegen_database_config";
	public static final String ID_编辑MYSQL代理="codegen_edit_mysql_proxy";
	public static final String ID_模板配置="codegen_template_config";
	public static final String ID_查看代码助手="codegen_view";

	//企业管理(company_admin)
	public static final String ID_修改企业信息="company_admin_edit_company_info";
	public static final String ID_设置企业信息="company_admin_manage";
	public static final String ID_管理企业角色="company_admin_manage_company_role";
	public static final String ID_管理组织架构="company_admin_manage_department";
	public static final String ID_管理成员="company_admin_manage_member";
	public static final String ID_管理项目角色="company_admin_manage_project_role";
	public static final String ID_管理回收站="company_admin_manage_recycle";
	public static final String ID_系统设置="company_admin_system_settings";
	public static final String ID_查看操作日志="company_admin_view_opt_log";
	public static final String ID_查看项目列表="company_admin_view_project";
	public static final String ID_管理系统通知="company_admin_system_notifaction";

	//项目管理(company)
	public static final String ID_创建项目="company_create_project";
//	public static final String ID_仪表盘="company_dashboard";
	public static final String ID_删除项目="company_delete_project";
	public static final String ID_项目动态="company_project_change_log";
//	public static final String ID_讨论="company_discuss";
//	public static final String ID_查询全局报表="company_gloal_report";
//	public static final String ID_查询项目报表="company_project_report";
//	public static final String ID_汇报="company_report";
//	public static final String ID_管理汇报模板="company_report_template";

	//仪表盘
	public static final String ID_仪表盘管理="dashboard";
	public static final String ID_开启仪表盘模块="dashboard_view";
	public static final String ID_创建仪表盘="dashboard_create";

	//讨论
	public static final String ID_讨论管理="discuss";
	public static final String ID_开启讨论模块="discuss_view";
	public static final String ID_创建讨论="discuss_create";

	//汇报
	public static final String ID_汇报管理="report";
	public static final String ID_开启汇报模块="report_view";
	public static final String ID_创建汇报="report_create";
	public static final String ID_删除汇报="report_delete";
	public static final String ID_汇报模板管理="report_template_admin";

	//问卷调查
	public static final String ID_问卷调查管理="surveys";
	public static final String ID_开启问卷调查模块="surveys_view";
	public static final String ID_创建问卷调查="surveys_create";

	//流程管理workflow
	public static final String ID_流程管理="workflow";
	public static final String ID_开启流程模块="workflow_view";
	public static final String ID_发起流程="workflow_create";
	public static final String ID_管理流程模板="workflow_template_admin";
	public static final String ID_管理流程数据="workflow_data";//判断是否是admin is admin

	//项目集
	public static final String ID_项目集管理="project_set";
	public static final String ID_开启项目集管理模块="project_set_view";


	//系统版本
	public static final String ID_版本库管理="version_repository";
	public static final String ID_创建版本库="version_repository_add";
	public static final String ID_删除版本库="version_repository_delete";
	public static final String ID_开启版本库模块="version_repository_view";
	public static final String ID_创建版本="version_add";
	public static final String ID_删除版本="version_delete";

	////////////////////////////////////////项目权限///////////////////////////////////

	//敏捷
	public static final String ID_敏捷="agility";
	public static final String ID_敏捷_迭代="agility_iteration";
	public static final String ID_敏捷_Release="agility_release";
	public static final String ID_敏捷_子系统="agility_subsystem";

	//devops(devops)
	public static final String ID_DEVOPS="devops";
	public static final String ID_创建和编辑主机="devops_edit_machine";
	public static final String ID_创建和编辑pipeline="devops_edit_pipeline";
//	public static final String ID_执行pipeline="devops_excute_pipeline";
	public static final String ID_查看devops列表="devops_list";
//	public static final String ID_登录主机="devops_login_machine";

	//文件系统(file)
	public static final String ID_文件="file";
	public static final String ID_创建文件夹="file_create_dir";
	public static final String ID_删除文件="file_delete";
	public static final String ID_下载文件="file_download";
	public static final String ID_查看文件列表="file_list";
	public static final String ID_移动="file_move";
	public static final String ID_预览文件="file_preview";
	public static final String ID_上传文件="file_upload";

	//项目(project)
	public static final String ID_项目复制="project_copy";
	public static final String ID_修改项目设置="project_edit_config";
//	public static final String ID_创建和编辑过滤器="project_edit_filter";
	public static final String ID_编辑项目进度="project_edit_progress";
	public static final String ID_创建和编辑迭代="project_iteration_config";
	public static final String ID_管理项目成员="project_member_config";
	public static final String ID_创建和编辑Release="project_release_config";
	public static final String ID_创建和编辑子系统="project_sub_system_config";
	public static final String ID_查看进度报告="project_view_iteration_progress";
	public static final String ID_编辑项目工作流状态="project_edit_workflow_status";

	//对象(task)
	public static final String ID_TASK="task_";
	public static final String ID_上传附件="task_add_attachment_";
	public static final String ID_批量复制="task_batch_copy_";
	public static final String ID_批量删除="task_batch_delete_";
	public static final String ID_批量编辑="task_batch_edit_";
	public static final String ID_创建TASK="task_create_";
	public static final String ID_编辑TASK="task_edit_";
	public static final String ID_删除TASK="task_delete_";
	public static final String ID_更改状态TASK="task_change_status_";
	public static final String ID_更改责任人TASK="task_change_owner_";
	public static final String ID_编辑分组TASK="task_edit_category_";
	public static final String ID_导出TASK="task_export_";
	public static final String ID_导入TASK="task_import_";
	public static final String ID_查看列表TASK="task_list_";
	public static final String ID_修改开始截止时间="task_edit_startend_date_";
	public static final String ID_修改完成时间="task_edit_finish_date_";

	//wiki(wiki)
	public static final String ID_WIKI="wiki";
	public static final String ID_创建和编辑知识库="wiki_edit";
	public static final String ID_创建和编辑页面="wiki_edit_page";
	public static final String ID_查看知识库列表="wiki_list";

	//企业百科（companywiki）
	public static final String ID_COMPANY_WIKI="company_wiki";
	public static final String ID_COMPANY_WIKI_LIST = "company_wiki_list";
	public static final String ID_创建企业wiki="company_wiki_create";
	public static final String ID_编辑企业wiki="company_wiki_edit";
	public static final String ID_删除企业wiki="company_wiki_delete";

	//交付版本(delivery)
	public static final String ID_DELIVERY="delivery";
	public static final String ID_创建和编辑交付版本="delivery_edit";
	public static final String ID_查看交付版本="delivery_list";
	public static final String ID_删除交付版本="delivery_delete";

	//阶段(stage)
	public static final String ID_STAGE="stage";
	public static final String ID_创建和编辑阶段="stage_edit";
	public static final String ID_查看阶段="stage_list";
	public static final String ID_删除阶段="stage_delete";

	//里程碑(landmark)
	public static final String ID_LANDMARK="landmark";
	public static final String ID_创建和编辑里程碑="landmark_edit";
	public static final String ID_查看里程碑="landmark_list";
	public static final String ID_删除里程碑="landmark_delete";


	//供应商(supplier)
	public static final String ID_SUPPLIER="supplier";
	public static final String ID_创建和编辑供应商="supplier_edit";
	public static final String ID_查看供应商="supplier_list";
	public static final String ID_删除供应商="supplier_delete";

	//钉钉考勤(attendance)
	public static final String ID_ATTENDANCE="attendance";
	public static final String ID_创建和编辑钉钉考勤="attendance_edit";
	public static final String ID_查看钉钉考勤="attendance_list";
	public static final String ID_删除钉钉考勤="attendance_delete";
	//
	public Permission() {

	}
	//
	public Permission(String id,int type,String name,String parentId,
			boolean isDataPermission,boolean isMemberPermission,int sortWeight) {
		this.id=id;
		this.type=type;
		this.name=name;
		this.parentId=parentId;
		this.isDataPermission=isDataPermission;
		this.isMemberPermission=isMemberPermission;
		this.sortWeight=sortWeight;
	}
	//
	//
	public static final Map<String,Permission> TASK_PERMISSION_MAP=new HashMap<>();
	static {
		TASK_PERMISSION_MAP.put(ID_TASK, new Permission(ID_TASK, TYPE_项目, "任务",null, false,true,1));
		TASK_PERMISSION_MAP.put(ID_上传附件, new Permission(ID_上传附件, TYPE_项目, "上传附件",ID_TASK, true ,true,2));
		TASK_PERMISSION_MAP.put(ID_批量复制, new Permission(ID_批量复制, TYPE_项目, "批量复制",ID_TASK, false,true,3));
		TASK_PERMISSION_MAP.put(ID_批量删除, new Permission(ID_批量删除, TYPE_项目, "批量删除",ID_TASK, false,true,4));
		TASK_PERMISSION_MAP.put(ID_批量编辑, new Permission(ID_批量编辑, TYPE_项目, "批量编辑",ID_TASK, false,true,5));
		TASK_PERMISSION_MAP.put(ID_编辑TASK, new Permission(ID_编辑TASK, TYPE_项目, "编辑",ID_TASK, true,true,6));
		TASK_PERMISSION_MAP.put(ID_创建TASK, new Permission(ID_创建TASK, TYPE_项目, "创建",ID_TASK, true,true,6));
		TASK_PERMISSION_MAP.put(ID_修改完成时间, new Permission(ID_修改完成时间, TYPE_项目, "修改完成时间",ID_TASK, true,true,6));
		TASK_PERMISSION_MAP.put(ID_删除TASK, new Permission(ID_删除TASK, TYPE_项目, "删除",ID_TASK, true,true,7));
		TASK_PERMISSION_MAP.put(ID_更改状态TASK, new Permission(ID_更改状态TASK, TYPE_项目, "更改状态",ID_TASK, true,true,8));
		TASK_PERMISSION_MAP.put(ID_更改责任人TASK, new Permission(ID_更改责任人TASK, TYPE_项目, "更改责任人",ID_TASK, true,true,9));
		TASK_PERMISSION_MAP.put(ID_编辑分组TASK, new Permission(ID_编辑分组TASK, TYPE_项目, "编辑分类",ID_TASK, false,true,10));
		TASK_PERMISSION_MAP.put(ID_导出TASK, new Permission(ID_导出TASK, TYPE_项目, "导出",ID_TASK, false,true,11));
		TASK_PERMISSION_MAP.put(ID_导入TASK, new Permission(ID_导入TASK, TYPE_项目, "导入",ID_TASK, false,true,12));
		TASK_PERMISSION_MAP.put(ID_查看列表TASK, new Permission(ID_查看列表TASK, TYPE_项目, "查看列表",ID_TASK, false,true,13));
		TASK_PERMISSION_MAP.put(ID_修改开始截止时间, new Permission(ID_修改开始截止时间, TYPE_项目, "修改开始/截止时间",ID_TASK, false,true,14));
	}

	public static boolean isReadPermission(String permission){
		if(null==permission){
			return true;
		}
		return permission.contains("_list")||permission.contains("_view")||permission.contains("_log");
	}
	//

	@DomainFieldValid(comment="ID")
	public String id;

    @DomainFieldValid(comment="type",canUpdate=true)
    public int type;

    @DomainFieldValid(comment="名称",canUpdate=true,maxValue=32)
    public String name;

    @ForeignKey(domainClass=Permission.class)
    @DomainFieldValid(comment="parentId",canUpdate=true)
    public String parentId;

    @DomainFieldValid(comment="是否是数据权限",required=true,canUpdate=true)
    public boolean isDataPermission;

    @DomainFieldValid(comment="是否是成员权限",required=true,canUpdate=true)
    public boolean isMemberPermission;

    @DomainFieldValid(comment="企业版本",required=true,canUpdate=true)
    public int companyVersion;

    public int objectType;

    public int sortWeight;

	@DomainFieldValid(comment="创建时间")
	public Date createTime;

	@DomainFieldValid(comment="更新时间")
	public Date updateTime;
    //
    //
    public static class PermissionInfo extends Permission{
    //

    }
    //
    //
    @QueryDefine(domainClass=PermissionInfo.class)
    public static class PermissionQuery extends BizQuery{
        //
        public String id;

        public Integer type;

        public String name;

        public String parentId;

        public Boolean isDataPermission;

        public Boolean isMemberPermission;

        public Integer companyVersion;

        public Integer objectType;

        public Integer sortWeight;

        @QueryField(operator=">=",field="createTime")
        public Date createTimeStart;

        @QueryField(operator="<=",field="createTime")
        public Date createTimeEnd;

        @QueryField(operator=">=",field="updateTime")
        public Date updateTimeStart;

        @QueryField(operator="<=",field="updateTime")
        public Date updateTimeEnd;

        //in or not in fields

        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int typeSort;
        public int nameSort;
        public int parentIdSort;
        public int isDataPermissionSort;
        public int sortWeightSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}
