/**
 *
 */
package cornerstone.biz.srv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONPath;

import cornerstone.biz.ConstDefine;
import cornerstone.biz.CornerstoneBizSystem;
import cornerstone.biz.domain.Account;
import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.domain.LarkAuthorize;
import cornerstone.biz.domain.Task.TaskInfo;
import cornerstone.biz.domain.wx.WeixinExceptionCode;
import cornerstone.biz.srv.LarkService.Card.CardConfig;
import cornerstone.biz.srv.LarkService.Card.CardElement;
import cornerstone.biz.srv.LarkService.Card.CardElementAction;
import cornerstone.biz.srv.LarkService.Card.CardElementField;
import cornerstone.biz.srv.LarkService.Card.CardElementText;
import cornerstone.biz.srv.LarkService.Card.CardHeader;
import cornerstone.biz.srv.LarkService.Card.CardTitle;
import cornerstone.biz.srv.LarkService.Card.MultiUrl;
import cornerstone.biz.util.StringUtil;
import cornerstone.biz.util.TripleDESUtil;
import cornerstone.biz.util.URLUtil;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;
import jazmin.driver.http.HttpClientDriver;
import jazmin.driver.http.HttpRequest;
import jazmin.driver.http.HttpResponse;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;
import jazmin.util.JSONUtil;

/**
 * @author yama
 *
 */
public class LarkService {
    private static Logger logger = LoggerFactory.get(LarkService.class);
    //
    @AutoWired
    public HttpClientDriver httpClientDriver;
    //
    public static String appTicket;
    //
    public static ThreadLocal<String> tenantAccessToken = new ThreadLocal<>();

    //
    public static class LarkConfig {
        public static final String APP_TYPE_INTERNAL = "INTERNAL";
        public static final String APP_TYPE_ISV = "ISV";

        //
        public String appId;
        public String appSecret;
        public String appType;
        //
        public int accessTokenExpireSeconds;
        public long accessTokenGetTime;
        public String appAccessToken;
    }

    //
    public static class LarkUserAccessToken {
        public String accessToken;
        public String avatarUrl;
        public String name;
        public String enName;
        public String openId;
        public String tenantKey;
    }

    //
    public static class LarkUserAccessToken2 {
        public String access_token;
        public String avatar_url;
        public String expires_in;
        public String name;//zhangsan
        public String en_name;
        public String open_id;
        public String tenant_key;
        public String refresh_expires_in;
        public String refresh_token;
        public String token_type;//Bearer

    }

    //
    public static class LarkUserInfo {
        public String avatarUrl;
        public String name;
        public String email;
        public String status;
        public String employeeID;
        public String mobile;
    }

    //
    public static class LarkUserInfo2 {
        public String name;
        public String avatar_url;
        public String email;
        public String user_id;//zhangsan
        public String mobile;
        public String status;
    }

    //
    public static class CardMessage {
        public static final String MSG_TYPE_TEXT = "text";
        public static final String MSG_TYPE_CARD = "interactive";

        //
        public String openId;
        public String chatId;
        public String rootId;
        public String userId;
        public String msgType;
        public Object content;
        public Boolean updateMulti;
        public Card card;
    }

    //
    public static class PostMessage {//富文本
        public String open_id;
        public String user_id;
        public String email;
        public String chat_id;
        public String root_id;    //string	否	如果需要回复某条消息，填对应消息的消息 ID
        public String msg_type = "post";//	string	是	消息的类型，此处固定填 “post”
        public PostMessageContent content;//object 是	消息的内容
    }

    //
    public static class PostMessageContent {
        //
        public Post post;

        //
        public static class Post {
            public Zhcn zh_cn;
        }
    }

    //
    public static class Zhcn {
        //
        public static class ZhcnContent {
            public String tag;
            public Boolean un_escape;
            public String text;
            public String href;
            public String user_id;
            public String image_key;
            public Integer width;
            public Integer height;

            //
            public ZhcnContent() {

            }

            //
            public ZhcnContent(String tag, String text) {
                this.tag = tag;
                this.text = text;
            }

            //
            public ZhcnContent(String tag, String text, String href) {
                this.tag = tag;
                this.text = text;
                this.href = href;
            }
        }

        //
        public String title;

        public List<ZhcnContent> content;

        //
        public Zhcn() {
            content = new ArrayList<>();
        }
    }

    //
    public static class Card {
        //
        public static class CardConfig {
            public boolean wide_screen_mode;
        }

        //
        public static class CardTitle {
            public String tag;
            public String content;
        }

        //
        public static class CardHeader {
            public CardTitle title;
        }

        //
        public static class CardElement {
            public String tag;
            public CardElementText text;
            public List<CardElementAction> actions;
            public List<CardElementField> fields;
        }

        //
        public static class CardElementText {
            public String tag;
            public String content;
            public Integer lines;

            //
            public CardElementText() {

            }

            //
            public CardElementText(String tag, String content) {
                this.tag = tag;
                this.content = content;
            }
        }

        //
        public static class CardElementField {
            public Boolean is_short;
            public CardElementText text;
        }

        //
        public static class CardElementAction {
            public String tag;
            public CardElementText text;
            public String type;
            public MultiUrl multi_url;
        }

        //
        public static class MultiUrl {
            public String url;
            public String android_url;
            public String ios_url;
            public String pc_url;
        }

        //
        public CardConfig config;

        public CardHeader header;

        public List<CardElement> elements;
    }

    //
    public static class Callback {
        public String challenge;
        public String ts;
        public String type;
        public String token;
        public String uuid;
        public CallbackEvent event;
    }

    //
    public static class CallbackEvent {
        public String app_id;
        public String chat_id;
        public String chat_type;
        public boolean is_mention;
        public String message_id;
        public String msg_type;
        public String open_chat_id;
        public String open_id;
        public String open_message_id;
        public String parent_id;
        public String root_id;
        public String tenant_key;
        public String text;
        public String text_without_at_bot;
        public String type;
        public String user_agent;
        public String user_open_id;
        public String app_ticket;
        public Operator operator;
        public LarkUser user;
    }

    //
    public static class Operator {//如果是机器人发起的，operator里面是机器人的open_id。如果是用户发起operator里面是用户的open_id和user_id
        public String open_id;//ou_2d2c0399b53d06fd195bb393cd1e38f2
        public String user_id;//原employee_id，仅企业自建应用返回
    }

    //
    public static class LarkUser {
        public String name;
        public String open_id;//ou_7dede290d6a27698b969a7fd70ca53da
        public String user_id;//employee_id，仅企业自建应用返回
    }

    //
    public LarkConfig appConfig;

    //
    //
    public LarkService() {
        //
    }

    //
    public LarkConfig getConfig() {
        if (appConfig == null) {
            appConfig = new LarkConfig();
            appConfig.appId = GlobalConfig.larkAppId;
            appConfig.appSecret = GlobalConfig.larkAppSecret;
            appConfig.appType = GlobalConfig.larkAppType;
        }
        return appConfig;
    }

    private Map<String, Object> sendMessage(String url, Map<String, Object> body) throws AppException {
        return sendMessage(url, body, null);
    }

    //
    private Map<String, Object> sendMessage(String url, Map<String, Object> body, Map<String, String> headers)
            throws AppException {
        return sendMessage(url, JSONUtil.toJson(body), headers);
    }

    //
    private String get(String url, Map<String, String> paras, Map<String, String> headers) {
        String rspBody = URLUtil.httpGet(url, paras, headers);
        HashMap<String, Object> obj = JSONUtil.fromJson(rspBody, HashMap.class);
        Integer errorCode = (Integer) obj.get("code");
        String errorMsg = (String) obj.get("msg");
        //
        if (errorCode != null && errorCode != 0) {
            throw new AppException(-1, "get errorcode:" + errorCode + "/" + errorMsg);
        }
        return rspBody;
    }

    //
    private String post(String url, String body, Map<String, String> headers) {
        String rspBody = URLUtil.httpPostWithBody(url, body, headers);
        HashMap<String, Object> obj = JSONUtil.fromJson(rspBody, HashMap.class);
        Integer errorCode = (Integer) obj.get("code");
        String errorMsg = (String) obj.get("msg");
        if (errorCode != null && errorCode != 0) {
            throw new AppException(-1, "get errorcode:" + errorCode + "/" + errorMsg);
        }
        return rspBody;
    }

    //
    private Map<String, Object> sendMessage(String url, String body, Map<String, String> headers)
            throws AppException {
        HttpRequest req;
        req = httpClientDriver.post(url);
        req.addHeader("Content-Type", "application/json");
        if (logger.isInfoEnabled()) {
            logger.info("url:{}\nbody:{}\nheaders:{}", url, body, DumpUtil.dump(headers));
        }
        //
        if (headers != null) {
            headers.forEach((k, v) -> {
                req.addHeader(k, v);
            });
        }
        HttpResponse rsp = null;
        try {
            if (!StringUtil.isEmpty(body)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("request {} \n body:{} ", url, body);
                }
                req.setBody(body.getBytes("UTF-8"));
            }
            rsp = req.execute().get();
        } catch (Exception e) {
            throw new AppException(0, e);
        }
        int code = rsp.getStatusCode();
        String rspBody;
        try {
            rspBody = rsp.getResponseBody("UTF-8");
            if (logger.isDebugEnabled()) {
                logger.debug("request {} \nresponse {}", url, rspBody);
            }
        } catch (IOException e) {
            throw new AppException(WeixinExceptionCode.SYSTEM_ERROR, e);
        }
        if (code != 200) {
            throw new AppException(0, "bad request,code=" + code);
        }
        HashMap<String, Object> obj = JSONUtil.fromJson(rspBody, HashMap.class);
        Integer errorCode = (Integer) obj.get("code");
        String errorMsg = (String) obj.get("msg");
        //
        if (errorCode != null && errorCode != 0) {
            throw new AppException(0, "lark errorcode:" + errorCode + "/" + errorMsg);
        }
        //
        return obj;
    }

    //
    public void refreshAppAccessToken() {
        Map<String, Object> body = new HashMap<>();
        LarkConfig config = getConfig();
        body.put("app_id", config.appId);
        body.put("app_secret", config.appSecret);
        String url = "";
        if (config.appType != null && config.appType.equals(LarkConfig.APP_TYPE_INTERNAL)) {
            url = "https://open.feishu.cn/open-apis/auth/v3/app_access_token/internal/";
        } else {
            body.put("app_ticket", appTicket);
            url = "https://open.feishu.cn/open-apis/auth/v3/app_access_token/";
        }
        Map<String, Object> ret = sendMessage(url, body);
        config.accessTokenGetTime = System.currentTimeMillis();
        tenantAccessToken.set((String) ret.get("tenant_access_token"));
        config.appAccessToken = (String) ret.get("app_access_token");
        config.accessTokenExpireSeconds = (int) ret.get("expire");
        logger.info("refreshAppAccessToken tenantAccessToken:{} ret:{}",
                tenantAccessToken.get(), DumpUtil.dump(ret));
    }

    public void refreshTenantAccessToken(String tenantKey) {
        Map<String, Object> body = new HashMap<>();
        LarkConfig config = getConfig();
        String url = "";
        if (config.appType.equals(LarkConfig.APP_TYPE_INTERNAL)) {
            body.put("app_id", config.appId);
            body.put("app_secret", config.appSecret);
            url = "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal/";
        } else {
            url = "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/";
            body.put("app_access_token", config.appAccessToken);
            body.put("tenant_key", tenantKey);
        }
        Map<String, Object> ret = sendMessage(url, body);
        config.accessTokenGetTime = System.currentTimeMillis();
        tenantAccessToken.set((String) ret.get("tenant_access_token"));
        config.appAccessToken = (String) ret.get("app_access_token");
        config.accessTokenExpireSeconds = (int) ret.get("expire");
        logger.info("refreshTenantAccessToken tenantAccessToken:{} ret:{}",
                tenantAccessToken.get(),
                DumpUtil.dump(ret));
    }

    /**
     * 重新推送 app_ticket
     */
    public void resendAppTicket() {
        Map<String, Object> body = new HashMap<>();
        LarkConfig config = getConfig();
        String url = "https://open.feishu.cn/open-apis/auth/v3/app_ticket/resend/";
        body.put("app_id", config.appId);
        body.put("app_secret", config.appSecret);
        sendMessage(url, body);
    }

    //
    public LarkUserAccessToken getUserAccessToken(String code) {
        Map<String, Object> body = new HashMap<>();
        LarkConfig config = getConfig();
        body.put("app_id", config.appId);
        body.put("app_secret", config.appSecret);
        body.put("app_access_token", config.appAccessToken);
        body.put("grant_type", "authorization_code");
        body.put("code", code);
        Map<String, Object> ret = sendMessage("https://open.feishu.cn/connect/qrconnect/oauth2/access_token/", body);
        LarkUserAccessToken token = new LarkUserAccessToken();
        token.accessToken = (String) ret.get("access_token");
        token.avatarUrl = (String) ret.get("avatar_url");
        token.name = (String) ret.get("name");
        token.enName = (String) ret.get("en_name");
        token.openId = (String) ret.get("open_id");
        token.tenantKey = (String) ret.get("tenant_key");
        return token;
    }

    //
    public LarkUserAccessToken2 getUserAccessToken2(String code) {
        Map<String, Object> body = new HashMap<>();
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        LarkConfig config = getConfig();
        body.put("app_id", config.appId);
        body.put("app_secret", config.appSecret);
        body.put("app_access_token", config.appAccessToken);
        body.put("grant_type", "authorization_code");
        body.put("code", code);
        String json = post("https://open.feishu.cn/open-apis/authen/v1/access_token",
                JSONUtil.toJson(body), header);
        //
        LarkUserAccessToken2 token = JSONUtil.fromJson(JSONPath.read(json, "$.data").toString(),
                LarkUserAccessToken2.class);
        if (logger.isInfoEnabled()) {
            logger.info("LarkUserAccessToken2 token:{}", DumpUtil.dump(token));
        }
        return token;
    }

    /**
     * 获取用户信息
     * @param userAccessToken
     * @return
     */
    public LarkUserInfo2 getUserInfo(String userAccessToken) {
        Map<String, String> body = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + userAccessToken);
        headers.put("Content-Type", "application/json");
        body.put("user_access_token", userAccessToken);
        String json = get("https://open.feishu.cn/open-apis/authen/v1/user_info",
                body, headers);
        //
        LarkUserInfo2 userInfo = JSONUtil.fromJson(JSONPath.read(json, "$.data").toString(),
                LarkUserInfo2.class);
        if (logger.isInfoEnabled()) {
            logger.info("LarkUserInfo userInfo:{}", DumpUtil.dump(userInfo));
        }
        return userInfo;
    }

    //
    public static class LarkDepartmentInfo {
        public String id;//":"od-c042a4980ba8e1466050e3e8da2378fe",
        public String leader_employee_id;//":"612a67ef",
        public String leader_open_id;//":"ou_05065996251935ada9c2b0ecc50be91e",
        public String chat_id;//": "oc_405333f8fc89c3262865b014ccbbb274",
        public Integer member_count;//": 79,
        public String name;//": "市场部",
        public String parent_id;//": "0",
        public Integer status;//
    }

    //
    public LarkDepartmentInfo getDepartmentInfo(String departmentId) {
        Map<String, String> headers = new HashMap<>();
        Map<String, String> paras = new HashMap<>();
        paras.put("department_id", "0");
        String url = "https://open.feishu.cn/open-apis/contact/v1/department/info/get";
        headers.put("Authorization", "Bearer " + tenantAccessToken.get());
        String rsp = get(url, paras, headers);
        return null;
    }

    //
    @SuppressWarnings("unchecked")
    public String sendMessage(CardMessage msg) {
        if (logger.isDebugEnabled()) {
            logger.debug("sendMessage msg:{}", DumpUtil.dump(msg));
        }
        //
        Map<String, Object> body = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + tenantAccessToken.get());
        //
        if (msg.openId != null) {
            body.put("open_id", msg.openId);
        }
        if (msg.chatId != null) {
            body.put("chat_id", msg.chatId);
        }
        if (msg.userId != null) {
            body.put("user_id", msg.userId);
        }
        if (msg.rootId != null) {
            body.put("root_id", msg.rootId);
        }
        if (msg.updateMulti != null) {
            body.put("update_multi", msg.updateMulti);
        }
        if (msg.card != null) {
            body.put("card", msg.card);
        }
        if (msg.content != null) {
            body.put("content", msg.content);
        }
        body.put("msg_type", msg.msgType);
        //
        Map<String, Object> ret = sendMessage("https://open.feishu.cn/open-apis/message/v4/send/", body, headers);
        Map<String, String> retData = (Map<String, String>) ret.get("data");
        return retData.get("message_id");
    }

    //
    public void sendAppTicket() {
        Map<String, Object> body = new HashMap<>();
        LarkConfig config = getConfig();
        body.put("app_id", config.appId);
        body.put("app_secret", config.appSecret);
        sendMessage("https://open.feishu.cn/open-apis/auth/v3/app_ticket/resend/", body);
    }

    public String getTaskHomeUrl(String isMobile, String userOpenId, TaskInfo task) {
        return GlobalConfig.webUrl + "p/lark/task_info?openId=" +
                TripleDESUtil.encryptWithUrlEncoder(userOpenId, ConstDefine.GLOBAL_KEY) +
                "&isMobile=" + isMobile +
                "&projectUuid=" + task.projectUuid +
                "&objectType=" + task.objectType +
                "&taskUuid=" + task.uuid;
    }

    public String getTaskHomeUrl(String isMobile, String userOpenId, String projectUuid,
                                 String objectType, String taskUuid) {
        return GlobalConfig.webUrl + "p/lark/task_info?openId=" +
                TripleDESUtil.encryptWithUrlEncoder(userOpenId, ConstDefine.GLOBAL_KEY) +
                "&isMobile=" + isMobile +
                "&projectUuid=" + projectUuid +
                "&objectType=" + objectType +
                "&taskUuid=" + taskUuid;
    }

    public String getCsUrl() {
        return GlobalConfig.webUrl + "p/lark/enter_cs";
    }

    /**
     *
     * @param task
     * @param account
     * @param title
     */
    public void sendTaskInfoMessage(TaskInfo task, Account account, String title) {
        try {
            if (account == null || StringUtil.isEmpty(account.larkOpenId) || CornerstoneBizSystem.isDebug) {
                return;
            }
            sendCardMessage(null, account, "CORNERSTONE变更通知",
                    Arrays.asList(
                            title,
                            "任务：#" + task.serialNo + " " + task.name,
                            "项目：" + task.projectName
                    ),
                    "查看",
                    getTaskHomeUrl("false", account.larkOpenId, task),
                    getTaskHomeUrl("true", account.larkOpenId, task));
            CardMessage msg = new CardMessage();
            msg.openId = account.larkOpenId;
            msg.msgType = CardMessage.MSG_TYPE_CARD;
            Card card = new Card();
            card.config = new CardConfig();
            card.config.wide_screen_mode = true;
            card.header = new CardHeader();
            card.header.title = new CardTitle();
            card.header.title.content = "CORNERSTONE变更通知";
            card.header.title.tag = "plain_text";
            card.elements = new ArrayList<>();
            CardElement element = new CardElement();
            element.tag = "div";
            element.text = new CardElementText();
            element.text.tag = "plain_text";
            element.text.content = title;
            card.elements.add(element);
            //
            element = new CardElement();
            element.tag = "div";
            element.text = new CardElementText();
            element.text.tag = "plain_text";
            element.text.content = "任务：#" + task.serialNo + " " + task.name;
            card.elements.add(element);
            //
            element = new CardElement();
            element.tag = "div";
            element.text = new CardElementText();
            element.text.tag = "plain_text";
            element.text.content = "项目：" + task.projectName;
            card.elements.add(element);
            //
            element = new CardElement();
            element.tag = "action";
            element.actions = new ArrayList<>();
            CardElementAction action = new CardElementAction();
            action.tag = "button";
            action.multi_url = new MultiUrl();
            action.multi_url.pc_url = getTaskHomeUrl("false", account.larkOpenId, task);
            action.multi_url.android_url = getTaskHomeUrl("true", account.larkOpenId, task);
            action.multi_url.ios_url = getTaskHomeUrl("true", account.larkOpenId, task);
            action.text = new CardElementText();
            action.text.tag = "plain_text";
            action.text.content = "查看";
            action.type = "default";
            element.actions.add(action);
            card.elements.add(element);
            //
            msg.card = card;
            //
            refreshAppAccessToken();
            refreshTenantAccessToken(account.larkTenantKey);
            sendMessage(msg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    //
    public void sendPostMessage(PostMessage msg) {
        try {
            Map<String, String> headers = new HashMap<>();
            LarkConfig config = getConfig();
            headers.put("Authorization", "Bearer " + tenantAccessToken.get());
            refreshAppAccessToken();
//			refreshTenantAccessToken(account.larkTenantKey);
            Map<String, Object> ret = sendMessage("https://open.feishu.cn/open-apis/message/v4/send/", JSONUtil.toJson(msg), headers);
            Map<String, String> retData = (Map<String, String>) ret.get("data");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    //
    public void sendCardMessage(String chatId, Account account, String title, List<String> subTitles,
                                String buttonName, String buttonPcUrl, String buttonMobileUrl) {
        if (account == null || StringUtil.isEmptyWithTrim(account.larkOpenId)|| CornerstoneBizSystem.isDebug) {
            return;
        }
        try {
            CardMessage msg = new CardMessage();
            msg.openId = account.larkOpenId;
            msg.chatId = chatId;
            msg.msgType = CardMessage.MSG_TYPE_CARD;
            Card card = new Card();
            card.config = new CardConfig();
            card.config.wide_screen_mode = true;
            card.header = new CardHeader();
            card.header.title = new CardTitle();
            card.header.title.content = title;
            card.header.title.tag = "plain_text";
            //
            card.elements = new ArrayList<>();
            if (subTitles != null) {
                for (String subTitle : subTitles) {
                    CardElement element = new CardElement();
                    element.tag = "div";
                    element.text = new CardElementText();
                    element.text.tag = "plain_text";
                    element.text.content = subTitle;
                    card.elements.add(element);
                }
            }
            //button
            if (!StringUtil.isEmptyWithTrim(buttonName)) {
                CardElement element = new CardElement();
                element.tag = "action";
                element.actions = new ArrayList<>();
                CardElementAction action = new CardElementAction();
                action.tag = "button";
                if (!StringUtil.isEmptyWithTrim(buttonPcUrl)) {
                    action.multi_url = new MultiUrl();
                    action.multi_url.pc_url = buttonPcUrl;
                    action.multi_url.android_url = buttonMobileUrl;
                    action.multi_url.ios_url = buttonMobileUrl;
                }
                action.text = new CardElementText();
                action.text.tag = "plain_text";
                action.text.content = buttonName;
                action.type = "default";
                element.actions.add(action);
                card.elements.add(element);
            }
            //
            msg.card = card;
            //
            refreshAppAccessToken();
            refreshTenantAccessToken(account.larkTenantKey);
            sendMessage(msg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    //
    public CardElement addPlainTextElement(List<CardElement> elements, String content) {
        CardElement element = new CardElement();
        element.tag = "div";
        element.text = new CardElementText();
        element.text.tag = "plain_text";
        element.text.content = content;
        elements.add(element);
        return element;
    }

    //
    public void addLarkMdField(CardElement element, String content) {
        if (element.fields == null) {
            element.fields = new ArrayList<>();
        }
        CardElementField field = new CardElementField();
        field.is_short = false;
        field.text = new CardElementText("lark_md", content);
        element.fields.add(field);
    }

    ///
    //
    //
    public void pushBindSuccessMessage(String userOpenId, String chatId, String tenantKey, String userName) {
        try {
            logger.debug("pushBindSuccessMessage userOpenId:{} chatId:{} tenantKey:{} userName:{}",
                    userOpenId, chatId, tenantKey, userName);
            CardMessage msg = new CardMessage();
            if (!StringUtil.isEmpty(userOpenId)) {
                msg.openId = userOpenId;
            }
            if (!StringUtil.isEmpty(chatId)) {
                msg.chatId = chatId;
            }
            msg.msgType = CardMessage.MSG_TYPE_CARD;
            //
            Card card = new Card();
            card.config = new CardConfig();
            card.config.wide_screen_mode = true;
            msg.card = card;
            //
            card.header = new CardHeader();
            card.header.title = new CardTitle();
            card.header.title.tag = "plain_text";
            card.header.title.content = "账号绑定成功👏";
            card.elements = new ArrayList<>();
            //
            addPlainTextElement(card.elements, "欢迎使用CORNERSTONE助手"
                    + "\n现在发送一个指令给我快速创建任务吧"
                    + "\n指令：+任务 项目名称 任务标题"
                    + "\n示例：+任务 示例项目 优化登录界面");
            CardElement element = addPlainTextElement(card.elements, "");
            LarkAuthorize larkUser = new LarkAuthorize();
            larkUser.openId = TripleDESUtil.encrypt(userOpenId, ConstDefine.GLOBAL_KEY);
            larkUser.tenantKey = tenantKey;
            larkUser.name = userName;
            String docUrl = "https://bytedance.feishu.cn/docs/doccnHiSac8gPgVuqo9fwM8psph";
            String homeUrl = GlobalConfig.webUrl + "p/lark/home?openId=" +
                    TripleDESUtil.encryptWithUrlEncoder(userOpenId, ConstDefine.GLOBAL_KEY);
            addLarkMdField(element, "进入[工作台](" + homeUrl + ")看看吧");
            addLarkMdField(element, "详细使用说明请查看[帮助手册](" + docUrl + ")");
            //
            refreshAppAccessToken();
            refreshTenantAccessToken(tenantKey);
            sendMessage(msg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
