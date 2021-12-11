/**
 *
 */
package cornerstone.web.controller;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.alibaba.fastjson.JSONPath;

import cornerstone.biz.BizData;
import cornerstone.biz.BizExceptionCode;
import cornerstone.biz.domain.CompanyMember.CompanyMemberQuery;
import cornerstone.biz.domain.ObjectType.ObjectTypeQuery;
import cornerstone.biz.domain.Project;
import cornerstone.biz.domain.Project.ProjectQuery;
import cornerstone.biz.domain.Role.RoleInfo;
import cornerstone.biz.domain.Task.TaskDetailInfo;
import cornerstone.biz.domain.Task.TaskQuery;
import cornerstone.biz.util.StringUtil;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.server.web.mvc.Context;
import jazmin.server.web.mvc.Controller;
import jazmin.server.web.mvc.HttpMethod;
import jazmin.server.web.mvc.PlainTextView;
import jazmin.server.web.mvc.ProxyController;
import jazmin.server.web.mvc.Service;
import jazmin.util.Base64Util;
import jazmin.util.JSONUtil;

/**
 * 
 * @author cs
 *
 */
@Controller(id = "openapi")
public class OpenAPIController extends ProxyController {
	//
	private static Logger logger=jazmin.log.LoggerFactory.get(OpenAPIController.class);
	//
	public static class OpenAPIResp{
		public int code;
		public String msg;
		public Object data;
		//
		public OpenAPIResp() {
			this.code=0;
			this.msg="OK";
		}
	}
	//
	private int getInt(String json,String path) {
		String strTemplateProjectId=JSONPath.read(json,path).toString();
		int templateProjectId=0;
		if(!StringUtil.isEmpty(strTemplateProjectId)) {
			templateProjectId=Integer.valueOf(strTemplateProjectId);
		}
		return templateProjectId;
	}
	//
	private String auth(Context ctx) {
		String auth=ctx.request().headers("Authorization");
		if(logger.isInfoEnabled()) {
			logger.info("auth:{}",auth);
		}
		if(auth==null) {
			throw new AppException(BizExceptionCode.CODE_OPENAPI认证失败,"认证失败");
		}
		if(auth.length()<6) {
			throw new AppException(BizExceptionCode.CODE_OPENAPI认证失败,"认证失败");
		}
		auth=auth.substring(6);
		String userPassword=null;
		try {
			userPassword=new String(Base64Util.decode(auth.getBytes()));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new AppException(BizExceptionCode.CODE_OPENAPI认证失败,"认证失败");
		}
		String tmp[]=userPassword.split(":");
		if(tmp.length<2) {
			throw new AppException(BizExceptionCode.CODE_OPENAPI认证失败,"认证失败");
		}
		String userName=tmp[0];
		String accessToken=tmp[1];
		try {
			String token=BizData.bizAction.openapiLogin(userName,accessToken);
			return token;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new AppException(BizExceptionCode.CODE_OPENAPI认证失败,"认证失败");
		}
	}
	//
	private void resp(Context ctx,Supplier<?> sipplier) {
		OpenAPIResp resp=new OpenAPIResp();
		Object data=null;
		try {
			if(sipplier!=null) {
				data=sipplier.get();
			}
			resp.data=data;
		}catch (AppException e) {
			resp.code=e.getCode();
			resp.msg=e.getMessage();
			logger.error(e.getMessage(),e);
			if(resp.code==0) {
				resp.code=BizExceptionCode.CODE_OPENAPI业务错误;
			}
		} catch (Exception e) {
			resp.msg=e.getMessage();
			logger.error(e.getMessage(),e);
			resp.code=BizExceptionCode.CODE_OPENAPI调用失败;
		}
		ctx.view(new PlainTextView(JSONUtil.toJson(resp)));
	}
	//
	private void resp(Context ctx,Consumer<Void> consumer) {
		OpenAPIResp resp=new OpenAPIResp();
		Object data=null;
		try {
			if(consumer!=null) {
				consumer.accept(null);
			}
			resp.data=data;
		}catch (AppException e) {
			resp.code=e.getCode();
			resp.msg=e.getMessage();
			if(resp.code==0) {
				resp.code=BizExceptionCode.CODE_OPENAPI业务错误;
			}
		} catch (Exception e) {
			resp.msg=e.getMessage();
			resp.code=BizExceptionCode.CODE_OPENAPI调用失败;
		}
		ctx.view(new PlainTextView(JSONUtil.toJson(resp)));
	}
	//
	/**
	 * 查询对象类型列表
	 * @param ctx
	 */
	@Service(id="get_object_type_list",method=HttpMethod.POST)
	public void getObjectTypeList(Context ctx){
		String json=ctx.request().body();
		resp(ctx, (t)->{
			ObjectTypeQuery query=JSONUtil.fromJson(JSONPath.read(json,"$.query").toString(),ObjectTypeQuery.class);
			BizData.bizAction.getObjectTypeList(auth(ctx), query);
		});
	}
	
	/**
	 * 查询企业成员列表
	 * @param ctx
	 */
	@Service(id="get_company_member_list",method=HttpMethod.POST)
	public void getCompanyMemberList(Context ctx){
		String json=ctx.request().body();
		resp(ctx,()->{
			CompanyMemberQuery query=JSONUtil.fromJson(JSONPath.read(json,"$.query").toString(),CompanyMemberQuery.class);
			return BizData.bizAction.getCompanyMemberList(auth(ctx), query);
		});	
	}
	
	/**
	 * 查询企业角色列表
	 * @param ctx
	 *
	 */
	@Service(id="get_company_role_list",method=HttpMethod.POST)
	public void getCompanyRoleList(Context ctx){
		resp(ctx,()->{
			return BizData.bizAction.getRoleInfoList(auth(ctx), RoleInfo.TYPE_全局);
		});	
	}
	
	/**
	 * 查询项目所有成员列表
	 * @param ctx
	 *
	 */
	@Service(id="get_project_all_member_list",method=HttpMethod.POST)
	public void getProjectAllMemberList(Context ctx){
		String json=ctx.request().body();
		resp(ctx,()->{
			int projectId=getInt(json, "$.projectId");
			return BizData.bizAction.getProjectMemberInfoList(auth(ctx), projectId);
		});	
	}
	
	/**
	 * 查询项目角色列表
	 * @param ctx
	 *
	 */
	@Service(id="get_project_role_list",method=HttpMethod.POST)
	public void getProjectRoleList(Context ctx){
		resp(ctx,()->{
			return BizData.bizAction.getRoleInfoList(auth(ctx), RoleInfo.TYPE_项目);
		});	
	}
	
	/**
	 * 查询项目列表
	 * @param ctx
	 *
	 */
	@Service(id="get_project_list",method=HttpMethod.POST)
	public void getProjectList(Context ctx){
		String json=ctx.request().body();
		resp(ctx,()->{
			ProjectQuery query=JSONUtil.fromJson(JSONPath.read(json,"$.query").toString(),ProjectQuery.class);
			return BizData.bizAction.getProjectList(auth(ctx), query);
		});	
	}
	
	/**
	 * 查询单个项目详情
	 * @param ctx
	 *
	 */
	@Service(id="get_project",method=HttpMethod.POST)
	public void getProject(Context ctx){
		String json=ctx.request().body();
		resp(ctx,()->{
			String uuid=JSONPath.read(json,"$.uuid").toString();
			return BizData.bizAction.getProjectInfoByUuid(auth(ctx), uuid);
		});	
	}

	/**
	 * 创建项目
	 * @param ctx
	 *
	 */
	@Service(id="create_project",method=HttpMethod.POST)
	public void createProject(Context ctx){
		String json=ctx.request().body();
		resp(ctx,()->{
			Project project=JSONUtil.fromJson(
					JSONPath.read(json,"$.project").toString(), Project.class);
			int templateProjectId=getInt(json, "$.templateProjectId");
			return BizData.bizAction.createProject(auth(ctx),project,templateProjectId);
		});	
	} 
	
	/**
	 * 更新项目
	 * @param ctx
	 *
	 */
	@Service(id="update_project",method=HttpMethod.POST)
	public void updateProject(Context ctx){
		String json=ctx.request().body();
		resp(ctx, (t)->{
			Project bean=JSONUtil.fromJson(
					JSONPath.read(json,"$.project").toString(),
					TaskDetailInfo.class);
			BizData.bizAction.updateProject(auth(ctx), bean);
		});
	}
	
	/**
	 * 查询任务列表(按objectType分类)
	 * @param ctx
	 *
	 */
	@Service(id="get_task_list",method=HttpMethod.POST)
	public void getTaskList(Context ctx){
		String json=ctx.request().body();
		resp(ctx,()->{
			TaskQuery query=JSONUtil.fromJson(JSONPath.read(json,"$.query").toString(), TaskQuery.class);
			return BizData.bizAction.getTaskInfoList(auth(ctx), query);
		});	
	}
	
	/**
	 * 查询单个任务详情
	 * @param ctx
	 *
	 */
	@Service(id="get_task",method=HttpMethod.POST)
	public void getTask(Context ctx){
		String json=ctx.request().body();
		resp(ctx,()->{
			String uuid=JSONPath.read(json,"$.uuid").toString();
			return BizData.bizAction.getTaskInfoByUuidForOpenApi(auth(ctx), uuid);
		});	
	}
	
	/**
	 * 创建任务
	 * @param ctx
	 *
	 */
	@Service(id="create_task",method=HttpMethod.POST)
	public void createTask(Context ctx){
		String json=ctx.request().body();
		resp(ctx,()->{
			TaskDetailInfo bean=JSONUtil.fromJson(
					JSONPath.read(json,"$.task").toString(), TaskDetailInfo.class);
			return BizData.bizAction.createTask(auth(ctx), bean);
		});	
	} 
	
	/**
	 * 更新任务
	 * @param ctx
	 *
	 */
	@Service(id="update_task",method=HttpMethod.POST)
	public void updateTask(Context ctx){
		String json=ctx.request().body();
		resp(ctx, (t)->{
			TaskDetailInfo bean=JSONUtil.fromJson(
					JSONPath.read(json,"$.task").toString(),
					TaskDetailInfo.class);
			List<String> updateFields=JSONUtil.fromJsonList(
					JSONPath.read(json,"$.updateFields").toString(),
					String.class);
			BizData.bizAction.updateTask(auth(ctx), bean,updateFields,true);
		});
	} 
	
	
	/**
	 * 查询对象类型字段列表
	 * @param ctx
	 *
	 */
	@Service(id="get_project_field_list",method=HttpMethod.POST)
	public void getProjectFieldDefineInfoList(Context ctx){
		String json=ctx.request().body();
		resp(ctx, (t)->{
			int project=getInt(json, "$.project");
			int objectType=getInt(json, "$.objectType");
			BizData.bizAction.getProjectFieldDefineInfoList(auth(ctx),project,objectType);
		});
	} 
	//
	
	
}