/**
 *
 */
package cornerstone.web.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cornerstone.biz.BizData;
import cornerstone.biz.domain.GithubWebhook;
import cornerstone.biz.domain.GithubWebhook.GithubWebhookBody;
import cornerstone.biz.domain.GitlabWebhook;
import cornerstone.biz.domain.GitlabWebhookBody;
import cornerstone.biz.domain.ProjectArtifact;
import cornerstone.biz.util.BizUtil;
import cornerstone.biz.webapi.WebApiReq;
import cornerstone.web.view.CalView;
import cornerstone.web.view.ErrorViewUtf8;
import cornerstone.web.view.ExFileView;
import cornerstone.web.view.ExPlainTextView;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.web.mvc.Context;
import jazmin.server.web.mvc.Controller;
import jazmin.server.web.mvc.HttpMethod;
import jazmin.server.web.mvc.JsonView;
import jazmin.server.web.mvc.PlainTextView;
import jazmin.server.web.mvc.ProxyController;
import jazmin.server.web.mvc.Service;
import jazmin.util.DumpUtil;
import jazmin.util.JSONUtil;

/**
 * 
 * @author cs
 * 
 *
 */
@Controller(id = "webapi")
public class WebAPIController extends ProxyController {
	//
	private static Logger logger=LoggerFactory.get(WebAPIController.class);
	//
	public WebAPIController() {
		//
	}

	@Service(id = "svn_pre_commit_hook", method = HttpMethod.GET, queryCount = 3)
	public void svnPreCommitHook(Context ctx) {
		String companyUuid = ctx.request().querys().get(2);
		String content=BizData.webApiAction.svnPreCommitHook(companyUuid);
		ctx.view(new PlainTextView(content));
	}
	
	@Service(id = "svn_post_commit_hook", method = HttpMethod.GET, queryCount = 3)
	public void svnPostCommitHook(Context ctx) {
		String companyUuid = ctx.request().querys().get(2);
		String content=BizData.webApiAction.svnPostCommitHook(companyUuid);
		ctx.view(new PlainTextView(content));
	}
	
	@Service(id = "svn_pre_commit_hook_win", method = HttpMethod.GET, queryCount = 3)
	public void svnPreCommitHookWin(Context ctx) {
		String companyUuid = ctx.request().querys().get(2);
		String content=BizData.webApiAction.svnPreCommitHookWin(companyUuid);
		ctx.view(new ExPlainTextView(content,"pre-commit.bat"));
	}
	
	@Service(id = "svn_post_commit_hook_win", method = HttpMethod.GET, queryCount = 3)
	public void svnPostCommitHookWin(Context ctx) {
		String companyUuid = ctx.request().querys().get(2);
		String content=BizData.webApiAction.svnPostCommitHookWin(companyUuid);
		ctx.view(new ExPlainTextView(content,"post-commit.bat"));
	}

	/**
	 * svn提交hook
	 */
	@Service(id = "svn_commit", method = HttpMethod.POST, queryCount = 3)
	public void svnCommit(Context ctx) {
		String token = ctx.request().querys().get(2);
		String content = ctx.request().body();
//		logger.info("svn_commit content:{}",content);
		String[] items=content.split("#");
		List<String> list=new ArrayList<>();
		for (int i=0;i<items.length;i++) {
			items[i]=items[i].replaceAll("\n", "");
			items[i]=new String(Base64.getDecoder().decode(items[i].getBytes()));
			list.add(items[i]);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("svn_commit token:{} list:{}",token,DumpUtil.dump(list));
		}
		BizData.webApiAction.svnCommit(token,list);
		ctx.put("ret", 0);
		ctx.view(new JsonView());
	}
	
	@Service(id = "svn_pre_commit", method = HttpMethod.POST, queryCount = 3)
	public void svnPreCommit(Context ctx) {
		String token = ctx.request().querys().get(2);
		String content = ctx.request().body();
		content = content.replaceAll("\n", "");
		content = new String(Base64.getDecoder().decode(content.getBytes()));
		String ret=BizData.webApiAction.svnPreCommit(token, content);
		ctx.view(new PlainTextView(ret));
	}
	

	
	@Service(id = "download_artifact", queryCount = 3)
	public void downloadArtifact(Context ctx) {
		String fileId = ctx.request().querys().get(2);
		ProjectArtifact projectArtifact=BizData.webApiAction.getProjectArtifactByUuid(fileId);
        File file=BizUtil.getArtifactFile(fileId);
        ctx.view(new ExFileView(file,projectArtifact.originalName));
	}
	
	/**
	 * 运行cmdbApi
	 * @param ctx
	 */
	@Service(id = "cmdb_api",queryCount=3)
	public void runCmdbApi(Context ctx) {
		String apiKey=ctx.request().querys().get(2);
		Map<String,String>queryMap=new HashMap<>();
		for(String key : ctx.request().queryParams()) {
			queryMap.put(key,ctx.request().queryParams(key));
		}
		String host=ctx.request().ip();
		try {
			String result=BizData.webApiAction.runCmdbApi(apiKey,queryMap,host);
			ctx.view(new PlainTextView(result));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			ctx.response().raw().setContentType("text/html;charset=utf-8");
			ctx.view(new ErrorViewUtf8(500, e.getMessage()));
		}
	}
	
	/**
	 * 订阅日历
	 * @param ctx
	 */
	@Service(id = "subscribe_cal",queryCount=3)
	public void subscribeCal(Context ctx) {
		String uuid=ctx.request().querys().get(2);
		String data= BizData.bizAction.getICalendar(uuid);
		try {
			ctx.view(new CalView(data)); 
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	@Service(id = "gitlab_webhook")
	public void gitlabWebhook(Context ctx) {
		String event=ctx.request().headers("X-Gitlab-Event");
		String token=ctx.request().headers("X-Gitlab-Token");
		String body=ctx.request().body();
		logger.info("gitlab_webhook event:{} token:{} body:{}",event,token,body);
		GitlabWebhook hook=new GitlabWebhook();
		hook.body=JSONUtil.fromJson(body, GitlabWebhookBody.class);
		hook.event=event;
		BizData.webApiAction.gitlabWebhook(token,hook);
		ctx.view(new JsonView());
	}

	@Service(id = "gitlab_web_post_hook_shell",queryCount=3)
	public void gitlabWebPostCommitkHookShell(Context ctx) {
		String companyUuid = ctx.request().querys().get(2);
		String ret = BizData.webApiAction.gitlabPostCommitHook(companyUuid);
		ctx.view(new ExPlainTextView(ret,"post-receive"));
	}
	/**
	 * gitlab push hook
	 */
	@Service(id = "gitlab_post_receive", method = HttpMethod.POST, queryCount = 3)
	public void gitlabPostReceive(Context ctx) {
		String token = ctx.request().querys().get(2);
		String content = ctx.request().body();

		String[] items=content.split("#");
		List<String> list=new ArrayList<>();
		for (int i=0;i<items.length;i++) {
			items[i]=items[i].replaceAll("\n", "");
			items[i]=new String(Base64.getDecoder().decode(items[i].getBytes()));
			list.add(items[i]);
		}
		logger.info("post_receive content:{}",DumpUtil.dump(list));
		BizData.webApiAction.gitlabPostReceive(token,list);
		ctx.put("ret", 0);
		ctx.view(new JsonView());
	}


	@Service(id = "github_webhook")
	public void githubWebhook(Context ctx) {
		String event=ctx.request().headers("X-GitHub-Event");
		String delivery=ctx.request().headers("X-GitHub-Delivery");
		String token=ctx.getString("token");
		String signature=ctx.request().headers("X-Hub-Signature");
		String body=ctx.request().body();
		logger.info("github_webhook event:{} token:{} body:{} delivery:{} signature:{}",
				event,token,body,delivery,signature);
		if(token==null||body==null) {
			return;
		}
		GithubWebhook hook=new GithubWebhook();
		hook.body=JSONUtil.fromJson(body, GithubWebhookBody.class);
		hook.event=event;
		hook.signature=signature;
		hook.delivery=delivery;
		hook.reqBody=body;
		BizData.webApiAction.githubWebhook(token,hook);
		ctx.view(new JsonView());
	}
	
	@Service(id = "run_web_api",queryCount=3)
	public void runWebApi(Context ctx) {
		String apiKey=ctx.request().querys().get(2);
		Map<String,String>queryMap=new HashMap<>();
		for(String key : ctx.request().queryParams()) {
			queryMap.put(key,ctx.request().queryParams(key));
		}
		WebApiReq req=new WebApiReq();
		req.method=ctx.request().requestMethod();
		req.ip=ctx.request().ip();
		req.parameterMap=queryMap;
		req.body=ctx.request().body();
		try {
			String result=BizData.webApiAction.runWebApi(apiKey,req);
			ctx.view(new PlainTextView(result));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			ctx.view(new ErrorViewUtf8(500, e.getMessage()));
		}
	}
	
	/**
	 * 唤醒等待中的workflow
	 * @param ctx
	 */
	@Service(id = "notify_workflow")
	public void notifyWorkflow(Context ctx) {
		String uuid=ctx.getString("uuid",true);
		String body = ctx.request().body();
		try {
			BizData.workflowAction.notifyWorkflowInstance(uuid,body);
			ctx.put("message", "OK");
		} catch (Exception e) {
			ctx.put("message", e.getMessage());
		}
		ctx.view(new JsonView());
	}
}