package cornerstone.biz.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import cornerstone.biz.ConstDefine;
import cornerstone.biz.action.CommAction.CommActionImpl;
import cornerstone.biz.dao.BizDAO;
import cornerstone.biz.domain.Account;
import cornerstone.biz.domain.AccountToken;
import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.domain.LarkAuthorize;
import cornerstone.biz.domain.ObjectType;
import cornerstone.biz.domain.Project;
import cornerstone.biz.domain.ProjectModule;
import cornerstone.biz.domain.Task.TaskDetailInfo;
import cornerstone.biz.srv.LarkService;
import cornerstone.biz.srv.LarkService.Callback;
import cornerstone.biz.srv.LarkService.CallbackEvent;
import cornerstone.biz.srv.LarkService.Card;
import cornerstone.biz.srv.LarkService.Card.CardConfig;
import cornerstone.biz.srv.LarkService.Card.CardElement;
import cornerstone.biz.srv.LarkService.Card.CardHeader;
import cornerstone.biz.srv.LarkService.Card.CardTitle;
import cornerstone.biz.srv.LarkService.CardMessage;
import cornerstone.biz.srv.LarkService.LarkUserAccessToken;
import cornerstone.biz.util.BizUtil;
import cornerstone.biz.util.DumpUtil;
import cornerstone.biz.util.StringUtil;
import cornerstone.biz.util.TripleDESUtil;
import jazmin.core.app.AutoWired;
import jazmin.driver.jdbc.Transaction;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.rpc.RpcService;
import jazmin.util.JSONUtil;

/**
 * 飞书Action
 * 
 * @author yama
 *
 */
public interface LarkAction {

	/***/
	Callback larkCallback(String body);
	
	/**请求身份验证
应用请求用户身份验证时，需按如下方式构造登录链接，并跳转至此链接。飞书客户端内用户免登，系统浏览器内用户需完成扫码登录。
	 *  */
	String getAuthenUrl(String state) throws UnsupportedEncodingException;
	
	String getSsoUrl(String token) throws UnsupportedEncodingException;
	
	String login(String code, String accountUuid);
	
	String larkLogin(String code);
	
	/**取消绑定飞书*/
	void unbindLark(String token);
	
	String getLarkQRCodeUrl(String tenantKey);

	@RpcService
	public static class LarkActionImpl extends CommActionImpl implements LarkAction {
		//
		@AutoWired
		LarkService larkService;
		@AutoWired
		BizDAO dao;
		//
		private static Logger logger = LoggerFactory.get(LarkActionImpl.class);

		@Override
		public String getSsoUrl(String token) throws UnsupportedEncodingException {
			Account account=bizService.getExistedAccountByToken(token);
			String url="https://open.feishu.cn/connect/qrconnect/page/sso/?"
					+ "redirect_uri=%s&app_id=%s&state=%s";
			url=String.format(url,
					URLEncoder.encode(GlobalConfig.webUrl+"p/lark/login","utf8"),
					GlobalConfig.larkAppId,
					account.uuid);
			logger.info("getSsoUrl url:{}",url);
			return url;
		}
		
		@Override
		public String getAuthenUrl(String state) throws UnsupportedEncodingException {
			String url="https://open.feishu.cn/open-apis/authen/v1/index?"
					+ "redirect_uri=%s&app_id=%s&state=%s";
			return String.format(url,
					URLEncoder.encode(GlobalConfig.webUrl+"p/lark/login","utf8"),
					GlobalConfig.larkAppId,
					state);
		}
		
		

		@Override
		public String login(String code, String state) {
			if(StringUtil.isEmpty(code)) {
				return null;
			}
			if(StringUtil.isEmpty(state)) {
				return null;
			}
			LarkUserAccessToken token=larkService.getUserAccessToken(code);
			logger.info("login LarkUserAccessToken:{}",DumpUtil.dump(token));
			//
			Account account=dao.getAccountByUuid(state);
			if(account==null) {
				return null;
			}
			account.larkOpenId=token.openId;
			account.larkTenantKey=token.tenantKey;
			dao.updateSpecialFields(account, "larkOpenId","larkTenantKey");
			AccountToken accountToken=dao.getAccountTokenByAccountId(account.id);
			if(accountToken==null) {
				return null;
			}
			//
			return accountToken.token;
		}
		
		@Override
		public String larkLogin(String code) {
			if(StringUtil.isEmpty(code)) {
				return null;
			}
			LarkUserAccessToken token=larkService.getUserAccessToken(code);
			logger.info("larkLogin LarkUserAccessToken:{}",DumpUtil.dump(token));
			//
			Account account=dao.getAccountByLarkOpenId(token.openId);
			if(account==null) {
				return null;
			}
			AccountToken accountToken=dao.getAccountTokenByAccountId(account.id);
			if(accountToken==null) {
				return null;
			}
			//
			return accountToken.token;
		}

		@Transaction
		@Override
		public Callback larkCallback(String body) {
			Callback bean=JSONUtil.fromJson(body, Callback.class);
			logger.info("larkCallback:{}",DumpUtil.dump(bean));
			try {
				if(bean.type!=null&& "event_callback".equals(bean.type)) {
					if(bean.event!=null) {
						if("message".equals(bean.event.type)) {
							onRecieveMessage(bean.event);
						}
						if("app_ticket".equals(bean.event.type)) {
							if(bean.event!=null&&bean.event.app_id.equals(GlobalConfig.larkAppId)) {
								LarkService.appTicket=bean.event.app_ticket;
								logger.info("larkCallback appTicket:{}",LarkService.appTicket);
							}
						}
						if("p2p_chat_create".equals(bean.event.type)) {//用户和机器人的会话首次被创建
							onPp2ChatCreate(bean.event);
						}
					}
				}
			} catch (Exception e) {
				logger.warn(e.getMessage(),e);
			}
			return bean;
		}

		private void onPp2ChatCreate(CallbackEvent event) {
			logger.info("onPp2ChatCreate event:{}",DumpUtil.dump(event));
			String userOpenId=null;
			String userName=null;
			if(event.user!=null) {
				userOpenId=event.user.open_id;
				userName=event.user.name;
			}else if(event.operator!=null) {
				userOpenId=event.operator.open_id;
			}
			if(StringUtil.isEmpty(userOpenId)) {
				logger.error("userOpenId isEmpty{}",DumpUtil.dump(event));
				return;
			}
			pushUnBindMessage(userOpenId,null,event.tenant_key,userName);
		}
		//
		@Override
		public String getLarkQRCodeUrl(String tenantKey) {
			String url=String.format("https://open.feishu.cn/open-apis/authen/v1/index?redirect_uri=%s&app_id=%s&state=%s",
					BizUtil.urlEncode(GlobalConfig.webUrl+"p/lark/enter"),
					GlobalConfig.larkAppId,
					tenantKey);
			return url;
		}
		//
		private void pushUnBindMessage(String userOpenId,String chatId,String tenantKey,String userName) {
			logger.debug("pushUnBindMessage userOpenId:{} chatId:{} tenantKey:{} userName:{}",
					userOpenId,chatId,tenantKey,userName);
			CardMessage msg = new CardMessage();
			if(!StringUtil.isEmpty(userOpenId)) {
				msg.openId=userOpenId;
			}
			if(!StringUtil.isEmpty(chatId)) {
				msg.chatId = chatId;
			}
			msg.msgType = CardMessage.MSG_TYPE_CARD;
			//
			Card card=new Card();
			card.config = new CardConfig();
			card.config.wide_screen_mode = true;
			msg.card=card;
			//
			card.header=new CardHeader();
			card.header.title=new CardTitle();
			card.header.title.tag="plain_text";
			card.header.title.content="欢迎使用CORNERSTONE 助手👏";
			card.elements = new ArrayList<>();
			//
			larkService.addPlainTextElement(card.elements, "CORNERSTONE是一个一站式项目管理协作平台，帮助企业进行智能管理，解决研发项目管理痛点，持续交付与集成，透过各个维度跟踪记录项目进度，帮助团队轻松配合完成目标。");
			CardElement element=larkService.addPlainTextElement(card.elements, "");
			LarkAuthorize larkUser=new LarkAuthorize();
			larkUser.openId=TripleDESUtil.encrypt(userOpenId,ConstDefine.GLOBAL_KEY);
			larkUser.tenantKey=tenantKey;
			larkUser.name=userName;
			larkUser.cUuid=dao.getCompanyUuidByLarkEnantKey(tenantKey);
			String url=getLarkQRCodeUrl(tenantKey);
			logger.info("账号绑定 userOpenId:{} url:{} larkUser:{}",userOpenId,url,DumpUtil.dump(larkUser));
			larkService.addLarkMdField(element, "请在电脑端进行[账号绑定]("+url+")吧");
			//
			larkService.refreshAppAccessToken();
			larkService.refreshTenantAccessToken(tenantKey);
			larkService.sendMessage(msg);
		}

		private void onRecieveMessage(CallbackEvent event) {
			logger.info("onRecieveMessage event:{}",DumpUtil.dump(event));
			Account account=dao.getAccountByLarkOpenId(event.open_id);
			if(account==null) {
				pushUnBindMessage(event.open_id,event.open_chat_id,event.tenant_key,"");
				return;
			}
			String chatId=event.open_chat_id;
			if(StringUtil.isEmptyWithTrim(event.text_without_at_bot)) {
				showUsage(chatId,account);
				return;
			}
			String text=event.text_without_at_bot.trim();
			if(!text.startsWith("+")) {
				showUsage(chatId,account);
				return;
			}
			String[] textList=text.substring(1).split(" ");
			if(textList.length<3) {
				showUsage(chatId,account);
				return;
			}
			String objectTypeName=textList[0].trim();
			String projectName=textList[1].trim();
			String taskName=textList[2].trim();
			String owner=null;
			if(text.indexOf("<at")!=-1&&text.indexOf("</at>")!=-1) {
				owner=text.substring(text.indexOf("<at"), text.lastIndexOf("</at>")+5).trim();
			}
			logger.info("onRecieveMessage objectTypeName:{} projectName:{} taskName:{} owner:{}",
					objectTypeName,projectName,taskName,owner);
			Project project=dao.getProjectByCompanyIdName(account.companyId,projectName);
			if(project==null) {
				showMessage(chatId,account, "项目名称【"+projectName+"】错误，找不到对应的项目");
				return;
			}
			ObjectType objectType=dao.getObjectTypeByName(objectTypeName);
			if(objectType==null) {
				showMessage(chatId,account, "对象类型名称【"+objectTypeName+"】错误，找不到对应的对象类型");
				return;
			}
			ProjectModule module=dao.getProjectModuleByProjectObjectType(project.id, objectType.id);
			if(module==null||(!module.isEnable)) {
				showMessage(chatId,account, "对象类型【"+objectTypeName+"】没有被项目【"+projectName+"】启用");
				return;
			}
			//
			Set<Integer> ownerAccountIdList=new LinkedHashSet<>();
			if(owner!=null) {
				String[] owners=owner.split("</at>");
				for (String t : owners) {
					t=t.trim();
					int pos=t.indexOf("\">");
					if(t.length()<13||pos==-1) {
						continue;
					}
					String ownerOpenId=t.substring(13,pos);
					logger.info("ownerOpenId:{}",ownerOpenId);
					Account ownerAccount=dao.getAccountByCompanyIdLarkOpenId(account.companyId,ownerOpenId);
					if(ownerAccount!=null) {
						ownerAccountIdList.add(ownerAccount.id);
					}
				}
			}
			logger.info("ownerAccountIdList:{}",DumpUtil.dump(ownerAccountIdList));
			//
			TaskDetailInfo bean=new TaskDetailInfo();
			bean.name=taskName;
			bean.projectId=project.id;
			bean.projectName=project.name;
			bean.objectType=objectType.id;
			bean.ownerAccountIdList=BizUtil.convert(ownerAccountIdList);
			try {
				bizService.createTask(account, bean,false, false);
				//
				larkService.sendCardMessage(event.open_chat_id,account, 
						"CORNERSTONE变更通知", Arrays.asList(
								"创建"+objectTypeName+"成功",
								"任务：#" + bean.serialNo + " " + bean.name,
								"项目：" + bean.projectName), 
								"查看", 
								larkService.getTaskHomeUrl("false",event.open_id,project.uuid, bean.objectType+"",bean.uuid),
								larkService.getTaskHomeUrl("true",event.open_id,project.uuid, bean.objectType+"",bean.uuid));
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				showMessage(chatId, account, e.getMessage());
			}
		}
		
		private void showUsage(String chatId,Account account) {
			larkService.sendCardMessage(chatId,account, 
					"CORNERSTONE助手", Arrays.asList(
							"CORNERSTONE可以识别的指令如下:",
							"+缺陷 项目名称 缺陷标题 @负责人1 @负责人2\n+任务 项目名称 任务标题 @负责人1 @负责人2\n示例：+任务 示例项目 优化登录界面"
							), null, null,null);
		}
		
		private void showMessage(String chatId,Account account,String message) {
			larkService.sendCardMessage(chatId,account, 
					"CORNERSTONE助手", Arrays.asList(message), null, null,null);
		}

		@Transaction
		@Override
		public void unbindLark(String token) {
			Account account=bizService.getExistedAccountByToken(token);
			account.larkOpenId=null;
			account.larkTenantKey=null;
			dao.updateSpecialFields(account, "larkOpenId","larkTenantKey");
		}
	}	
}
