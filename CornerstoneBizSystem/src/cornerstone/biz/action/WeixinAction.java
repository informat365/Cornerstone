package cornerstone.biz.action;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cornerstone.biz.FileServiceManager;
import cornerstone.biz.NotificationEvent;
import cornerstone.biz.annotations.ApiDefine;
import cornerstone.biz.dao.BizDAO;
import cornerstone.biz.domain.Account;
import cornerstone.biz.domain.FileInfo;
import cornerstone.biz.domain.wx.WeixinAccount;
import cornerstone.biz.domain.wx.WeixinMessage;
import cornerstone.biz.srv.WeixinService;
import cornerstone.biz.util.StringUtil;
import jazmin.core.Jazmin;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;
import jazmin.driver.jdbc.Transaction;
import jazmin.driver.jdbc.smartjdbc.QueryWhere;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.rpc.RpcService;
import jazmin.util.DumpUtil;
import jazmin.util.IOUtil;

/**
 * 微信Action
 * @author yama
 *
 */

@ApiDefine("微信接口")
public interface WeixinAction {

	@ApiDefine(value = "处理微信消息" ,resp = "微信消息类",params={"XML内容"})
	WeixinMessage receiveWeixinMessage(String xml) throws AppException;

	// --------------------------------------------------------------------------
	@RpcService
	public static class WeixinActionImpl implements WeixinAction {
		//
		@AutoWired
		WeixinService weixinService;
		@AutoWired
		BizDAO dao;
		//
		private static Logger logger = LoggerFactory.get(WeixinActionImpl.class);
		//
		@Transaction
		@Override
		public WeixinMessage receiveWeixinMessage(String xml) throws AppException {
			WeixinMessage msg = WeixinService.parseWeixinMessage(xml);
			if (logger.isDebugEnabled()) {
				logger.debug("receive weixin message \n{} msg:{}", xml, DumpUtil.dump(msg));
			}
			// callback
			Map<String, Object> args = new HashMap<>();
			args.put("msg", msg);
			Jazmin.notificationCenter.post(NotificationEvent.Notification_微信公众号收到微信消息, args);
			//
			if (msg.msgType.equals(WeixinMessage.MSGTYPE_EVENT)) {
				String event = msg.args.get("Event");
				String eventKey = msg.args.get("EventKey");
				if (logger.isDebugEnabled()) {
					logger.debug("receive weixin message event:{} eventKey:{}", event, eventKey);
				}
				if ("subscribe".equals(event)) {
					return processSubscribeEvent(msg);
				}
				if ("SCAN".equals(event)) {
					return processScanEvent(msg);
				}
				if ("unsubscribe".equals(event)) {
					return processUnsubscribeEvent(msg);
				}
				if ("CLICK".equals(event) && eventKey.startsWith(WeixinMessage.MENU_TEXT_MSG_PRE)) {
					String id = eventKey.substring(eventKey.indexOf("_") + 1, eventKey.length());
					return createTextMessage(msg, id);
				}
				return null;
			}
			// 用户发送文本消息过来，回复用户
			if (msg.msgType.equals(WeixinMessage.MSGTYPE_TEXT)) {
				String content = msg.args.get("Content");
				List<WeixinMessage> list = processAutoReply(msg, content);
				if (list.size() == 0) {
					return createTextMessage(msg, "消息已收到");
				} else {
					return list.get(0);
				}
			}
			return null;
		}

		//
		public WeixinMessage createTextMessage(WeixinMessage in, String content) {
			WeixinMessage retMsg = new WeixinMessage();
			retMsg.fromUserName = in.toUserName;
			retMsg.toUserName = in.fromUserName;
			retMsg.createTime = new Date();
			retMsg.msgType = WeixinMessage.MSGTYPE_TEXT;
			retMsg.args.put("Content", content);
			return retMsg;
		}

		//
		/**
		 * 用户订阅公众号
		 */
		private WeixinMessage processSubscribeEvent(WeixinMessage msg) throws AppException {
			return processUserScan(msg);
		}

		private List<WeixinMessage> processAutoReply(WeixinMessage msg, String content) {
			return Arrays.asList(createTextMessage(msg, "消息已收到"));
		}

		/**
		 * 用户扫描二维码关注
		 */
		private WeixinMessage processScanEvent(WeixinMessage msg) throws AppException {
			return processUserScan(msg);
		}

		//
		private WeixinMessage processUserScan(WeixinMessage msg) throws AppException {
			String openId = msg.fromUserName;
			String eventKey = msg.args.get("EventKey");
			String qrcode = null;
			if (eventKey != null) {
				if (eventKey.startsWith("qrscene_")) {
					qrcode = eventKey.substring(8);
				} else {
					qrcode = eventKey;
				}
				int result=weixinUserUpdate(openId, qrcode, null);
				if(result==-1) {
					return createTextMessage(msg, "绑定失败，此微信已绑定别的账号");
				}
			}
			//
			return createTextMessage(msg, "欢迎关注CORNERSTONE");
		}

		/**
		 * 用户取消订阅公众号
		 */
		private WeixinMessage processUnsubscribeEvent(WeixinMessage msg) {
			String openId = msg.fromUserName;
			Map<String, Object> args = new HashMap<>();
			args.put("openId", openId);
			Jazmin.notificationCenter.post(NotificationEvent.Notification_微信公众号用户取消订阅公众号, args);
			Account account=dao.getAccountByWxOpenIdForUpdate(openId);
			if(account!=null) {
				account.wxOpenId=null;
				account.wxUnionId=null;
				dao.update(account);
			}
			return null;
		}

		private int weixinUserUpdate(String openId, String eventKey, String accessToken) throws AppException {
			weixinService.refreshAccessToken();
			WeixinAccount wxAccount = weixinService.getUserInfo(openId);
			if (wxAccount.subscribe == 0) {
				wxAccount = weixinService.getSnsUserInfo(openId, accessToken);
			}
			//
			if (logger.isDebugEnabled()) {
				logger.debug("WeixinAccount:{} eventKey:{}\n", DumpUtil.dump(wxAccount),eventKey);
			}
			if(StringUtil.isEmptyWithTrim(eventKey)) {
				return 0;
			}
			Account account=null;
			try {
				int accountId=Integer.valueOf(eventKey);
				account = dao.getByIdForUpdate(Account.class, accountId);
				if(account!=null) {
					if(account.wxOpenId==null) {
						Account oldAccount=dao.getDomain(Account.class,QueryWhere.create().
								where("wx_open_id", wxAccount.openid).forUpdate());
						if(oldAccount!=null) {
							logger.warn("openId已经绑定别的账号");
							return -1;
						}else {
							account.wxOpenId=wxAccount.openid;
							account.wxUnionId=wxAccount.unionid;
							if (StringUtil.isEmpty(account.imageId)) {
								account.imageId=createImage(wxAccount.headimgurl);
							}
							dao.update(account);
						}
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
			return 0;
		}

		private String createImage(String headimgurl) {
			String temp = headimgurl;
			if (temp == null || temp.length() == 0) {
				return null;
			}
			if (temp.endsWith("/0")) {
				temp = temp.substring(0, temp.length() - 2);
				temp += "/132";
			}
			byte[] content = getContent(temp);
			if (content != null) {
				FileInfo fileInfo=FileServiceManager.get().upload(new ByteArrayInputStream(content),"image.png");
				return fileInfo.fileId;
			}
			return null;
		}
		//
		private byte[] getContent(String urlPath) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			URL url;
			try {
				url = new URL(urlPath);
				IOUtil.copy(url.openStream(), bos);
				return bos.toByteArray();
			} catch (Exception e) {
				logger.catching(e);
			}
			return null;
		}
	}
}
