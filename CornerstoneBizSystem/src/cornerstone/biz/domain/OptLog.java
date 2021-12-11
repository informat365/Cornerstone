package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 操作日志
 * 
 * @author 杜展扬 2019-05-23 10:26
 *
 */
@DomainDefine(domainClass = OptLog.class)
@DomainDefineValid(comment ="操作日志" )
public class OptLog extends BaseDomain{
    //
	public static final String EVENT_ID_账号密码登录="账号密码登录";
	public static final String EVENT_ID_微信扫码登录="微信扫码登录";
	public static final String EVENT_ID_手机验证码登录="手机验证码登录";
	public static final String EVENT_ID_AD域账号登录="AD域账号登录";
	public static final String EVENT_ID_登出="登出";
	public static final String EVENT_ID_编辑配置="编辑配置";
	public static final String EVENT_ID_对象上传附件="对象上传附件";
	public static final String EVENT_ID_对象关联WIKI="对象关联WIKI";
	public static final String EVENT_ID_对象删除附件="对象删除附件";
	public static final String EVENT_ID_注册="注册";
	public static final String EVENT_ID_加入项目="加入项目";
	public static final String EVENT_ID_激活账号="激活账号";
	public static final String EVENT_ID_创建成员="创建成员";
	public static final String EVENT_ID_编辑成员="编辑成员";
	public static final String EVENT_ID_创建企业="创建企业";
	public static final String EVENT_ID_更新License="更新License";
	public static final String EVENT_ID_从企业中移除="从企业中移除";
	public static final String EVENT_ID_从所有项目中移除="从所有项目中移除";
	public static final String EVENT_ID_禁用账户="禁用账户";
	public static final String EVENT_ID_启用账户="启用账户";
	public static final String EVENT_ID_重置密码="重置密码";
	public static final String EVENT_ID_强制修改密码="强制修改密码";
	public static final String EVENT_ID_创建密码规则="创建密码规则";
	public static final String EVENT_ID_编辑密码规则="编辑密码规则";
	public static final String EVENT_ID_编辑企业信息="编辑企业信息";
	public static final String EVENT_ID_删除企业="删除企业";
	public static final String EVENT_ID_创建部门="创建部门";
	public static final String EVENT_ID_编辑部门="编辑部门";
	public static final String EVENT_ID_删除部门="删除部门";
	public static final String EVENT_ID_创建项目="创建项目";
	public static final String EVENT_ID_归档项目="归档项目";
	public static final String EVENT_ID_删除项目="删除项目";
	public static final String EVENT_ID_删除回收站数据="删除回收站数据";
	public static final String EVENT_ID_恢复回收站数据="恢复回收站数据";
	public static final String EVENT_ID_创建项目角色="创建项目角色";
	public static final String EVENT_ID_编辑项目角色="编辑项目角色";
	public static final String EVENT_ID_删除项目角色="删除项目角色";
	public static final String EVENT_ID_创建企业角色="创建企业角色";
	public static final String EVENT_ID_编辑企业角色="编辑企业角色";
	public static final String EVENT_ID_删除企业角色="删除企业角色";
	public static final String EVENT_ID_创建对象类型="创建对象类型";
	public static final String EVENT_ID_编辑对象类型="编辑对象类型";
	public static final String EVENT_ID_删除对象类型="删除对象类型";
	public static final String EVENT_ID_新增数据表格="新增数据表格";
	public static final String EVENT_ID_编辑数据表格="编辑数据表格";
	public static final String EVENT_ID_删除数据表格="删除数据表格";
	public static final String EVENT_ID_新增系统钩子="新增系统钩子";
	public static final String EVENT_ID_编辑系统钩子="编辑系统钩子";
	public static final String EVENT_ID_删除系统钩子="删除系统钩子";
	public static final String EVENT_ID_新增WEBAPI="新增WEB API";
	public static final String EVENT_ID_编辑WEBAPI="编辑WEB API";
	public static final String EVENT_ID_删除WEBAPI="删除WEB API";
	public static final String EVENT_ID_删除WEBAPIKEY="刷新WEB API KEY";

	public static final String EVENT_ID_新增流程模板="新增流程模板";
	public static final String EVENT_ID_编辑流程模板="编辑流程模板";
	public static final String EVENT_ID_删除流程模板="删除流程模板";
	public static final String EVENT_ID_编辑流程表单设计="编辑流程表单设计";
	public static final String EVENT_ID_删除流程表单设计="删除流程表单设计";
	public static final String EVENT_ID_新增流程="新增流程";
	public static final String EVENT_ID_编辑流程="编辑流程";
	public static final String EVENT_ID_删除流程="删除流程";
	public static final String EVENT_ID_删除流程实例="删除流程实例";
	public static final String EVENT_ID_禁用流程模板="禁用流程模板";
	public static final String EVENT_ID_启用流程模板="启用流程模板";
	//
	public static final String EVENT_ID_新增汇报模板="新增汇报模板";
	public static final String EVENT_ID_编辑汇报模板="编辑汇报模板";
	public static final String EVENT_ID_删除汇报模板="删除汇报模板";
	public static final String EVENT_ID_禁用汇报模板="禁用汇报模板";
	public static final String EVENT_ID_启用汇报模板="启用汇报模板";
	
	public static final String EVENT_ID_新增问卷调查="新增问卷调查";
	public static final String EVENT_ID_编辑问卷调查="编辑问卷调查";
	public static final String EVENT_ID_删除问卷调查="删除问卷调查";
	public static final String EVENT_ID_禁用问卷调查="禁用问卷调查";
	public static final String EVENT_ID_启用问卷调查="启用问卷调查";
	public static final String EVENT_ID_编辑问卷调查表单设计="编辑问卷调查表单设计";
	public static final String EVENT_ID_删除问卷调查表单设计="删除问卷调查表单设计";
	public static final String EVENT_ID_重置问卷调查="重置问卷调查";
	public static final String EVENT_ID_删除问卷调查实例="删除问卷调查实例";


	//供应商
	public static final String EVENT_ID_新增供应商="新增供应商";
	public static final String EVENT_ID_编辑供应商="编辑供应商";
	public static final String EVENT_ID_删除供应商="删除供应商";

	public static final String EVENT_ID_新增供应商成员="新增供应商成员";
	public static final String EVENT_ID_编辑供应商成员="编辑供应商成员";
	public static final String EVENT_ID_删除供应商成员="删除供应商成员";


	//
    //
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="用户",required=true,canUpdate=true)
    public int accountId;
    
    @DomainFieldValid(comment="用户名称",required=true,canUpdate=true,maxValue=64)
    public String accountName;
    
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
	
	@ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;
	
	@ForeignKey(domainClass=Task.class)
    @DomainFieldValid(comment="对象",required=true,canUpdate=true)
    public int taskId;
    
    @DomainFieldValid(comment="关联id",canUpdate=true)
    public int associatedId;
    
    @DomainFieldValid(comment="关联名称",canUpdate=true,maxValue=64)
    public String associatedName;
    
    @DomainFieldValid(comment="事件名",canUpdate=true,maxValue=64)
    public String event;
    
    @DomainFieldValid(comment="备注",canUpdate=true,maxValue=512)
    public String remark;
    
    @DomainFieldValid(comment="保留字段",canUpdate=true,maxValue=512)
    public String reserve;
    
    //
    //   
    public static class OptLogInfo extends OptLog{
    //

    }
    //
    //   
    @QueryDefine(domainClass=OptLogInfo.class)
    public static class OptLogQuery extends BizQuery{
        //
        public Integer id;

        public Integer accountId;

        public Integer companyId;

        public String accountName;

        public Integer associatedId;

        public String associatedName;

        public String event;

        public String remark;

        public String reserve;

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
        public int accountIdSort;
        public int accountNameSort;
        public int associatedIdSort;
        public int associatedNameSort;
        public int eventSort;
        public int remarkSort;
        public int reserveSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}