package cornerstone.biz.srv;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import cornerstone.biz.ConstDefine;
import cornerstone.biz.CornerstoneBizSystem;
import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.domain.Task;
import cornerstone.biz.util.*;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.JSONUtil;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author pengyao
 */
public class DingtalkService {
    //
    private static final Logger logger = LoggerFactory.get(DingtalkService.class);
    //
    private static String accessToken;
    private static Date accessTokenExpireTime;

    //
    public static class UserInfo {
        public String userid;
        public String unionid;
        public String openid;
    }

    public static class GetUserinfoBycodeResponse {
        public String errcode;
        public String errmsg;
        public UserInfo userInfo;
    }

    //
    private Map<String, String> checkResult(String json) {
        Map<String, String> map = JSONUtil.fromJsonMap(json, String.class, String.class);
        int errcode = Integer.parseInt(map.get("errcode"));
        String errmsg = map.get("errmsg");
        if (errcode != 0) {
            logger.error("checkResult :{}", json);
            throw new AppException(errmsg);
        }
        return map;
    }

    //
    public void refreshAccessToken() {
        if (BizUtil.isNullOrEmpty(accessToken) || (null != accessTokenExpireTime && accessTokenExpireTime.before(new Date()))) {
            String url = "https://oapi.dingtalk.com/gettoken?appkey=%s&appsecret=%s";
            String json = URLUtil.httpGet(String.format(url, GlobalConfig.dingtalkAppKey, GlobalConfig.dingtalkAppSecret));
            Map<String, String> result = checkResult(json);
            accessToken = result.get("access_token");
            //有效期内重复获取自动续期
            accessTokenExpireTime = DateUtil.getNextSecond(7000);
            logger.info("accessToken:{} accessTokenExpireTime:{}", accessToken, accessTokenExpireTime);
        }
    }


    /**
     * https://ding-doc.dingtalk.com/doc#/serverapi2/kymkv6/M3fY1
     * 通过临时身份授权Code获取UnionID
     */
    public String getUnionIdByCode(String code) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String url = "https://oapi.dingtalk.com/sns/getuserinfo_bycode?accessKey=%s&timestamp=%s&signature=%s";
        Map<String, String> params = new HashMap<>();
        params.put("tmp_auth_code", code);
        logger.info("getUnionIdByCode: [code={}]", code);
        String json = URLUtil.httpPostWithBody(String.format(url, GlobalConfig.dingtalkMobileAppId, timestamp, signature(timestamp)), JSONUtil.toJson(params));
        checkResult(json);
        GetUserinfoBycodeResponse result = JSONUtil.fromJson(json, GetUserinfoBycodeResponse.class);
        return result.userInfo.unionid;
    }

    /**
     * https://ding-doc.dingtalk.com/doc#/serverapi2/ege851/602f4b15
     * 根据用户的平台唯一ID获取用户ID
     *
     * @param unionid 用户平台唯一ID
     * @return 返回用户ID
     */
    public String getUserIDByUnionId(String unionid) {
        String url = "https://oapi.dingtalk.com/user/getUseridByUnionid?access_token=%s&unionid=%s";
        logger.info("getUserIDByUnionId: [accessToken={}] [unionid={}]", unionid);
        String json = URLUtil.httpGet(String.format(url, accessToken, unionid));
        checkResult(json);
        Map<String, String> result = JSONUtil.fromJson(json, Map.class);
        return result.get("userid");
    }


    public Map<String, String> getUserDetail(String userid) {
        String url = "https://oapi.dingtalk.com/user/get?access_token=%s&userid=%s";
        String json = URLUtil.httpGet(String.format(url, accessToken, userid));
        return JSONUtil.fromJson(json, Map.class);
    }

    /**
     * 推送消息
     */
    public void pushMessage(String userId, String title, String markdown, String btnTitle, String goUrl, boolean cardType) {
        if (Stream.of(GlobalConfig.dingtalkAppSecret, GlobalConfig.dingtalkAgentId, GlobalConfig.dingtalkAppKey, GlobalConfig.dingtalkCorpId).anyMatch(BizUtil::isNullOrEmpty)) {
            logger.debug("empty dingtalk config ,return.");
            return;
        }
        refreshAccessToken();
        String url = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2?access_token=%s";
        Map<String, Object> parmas = new HashMap<>();
        if (cardType) {
            markdown = String.format("### %s  \n%s", title, markdown);
            parmas.put("msg", ImmutableMap.of("msgtype", "action_card", "action_card", ImmutableMap.of("title", title, "markdown", markdown, "single_title", btnTitle, "single_url", goUrl)));
        } else {
            parmas.put("msg", ImmutableMap.of("msgtype", "text", "content", ImmutableMap.of("content", title + "\n\n" + markdown)));
        }
        parmas.put("agent_id", GlobalConfig.dingtalkAgentId);
        parmas.put("userid_list", userId);

        URLUtil.httpPostWithBody(String.format(url, accessToken), JSONUtil.toJson(parmas));
    }

    /**
     * 钉钉签名算法
     *
     * @param stringToSign 签名字符串
     * @return 返回签名
     */
    private String signature(String stringToSign) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(GlobalConfig.dingtalkMobileAppSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signatureBytes = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            String signature = new String(Base64.encodeBase64(signatureBytes));
            return urlEncode(signature, "UTF-8");
        } catch (Exception e) {
            logger.error("钉钉签名失败", e);
        }
        return null;
    }

    /**
     * 钉钉URL编码算法
     *
     * @param value    URL
     * @param encoding 编码
     * @return 返回编码后的URL
     */
    public static String urlEncode(String value, String encoding) {
        if (value == null) {
            return "";
        }
        try {
            String encoded = URLEncoder.encode(value, encoding);
            return encoded.replace("+", "%20").replace("*", "%2A")
                    .replace("~", "%7E").replace("/", "%2F");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("FailedToEncodeUri", e);
        }
    }

    public String getTaskHomeUrl(String userId, Task.TaskInfo task) {
        return GlobalConfig.webUrl + "p/dingtalk/task_info?userId=" +
                TripleDESUtil.encryptWithUrlEncoder(userId, ConstDefine.GLOBAL_KEY) +
                "&taskUuid=" + task.uuid;
    }

    public String getCsUrl() {
        return GlobalConfig.webUrl + "p/dingtalk/enter";
    }

    public static class MsgRow {
        private String title;
        private String content;

        public MsgRow(String title, String content) {
            this.title = title;
            this.content = content;
        }

        @Override
        public String toString() {
            return String.format("%s：%s", title, content);
        }
    }

    public void sendTaskInfoMessage(String userId, Task.TaskInfo task,
                                    String content) {
        try {
            if (StringUtil.isEmpty(userId) || CornerstoneBizSystem.isDebug) {
                return;
            }
            List<MsgRow> rows = new ArrayList<>();
            rows.add(new MsgRow("项目名称", task.projectName));
            rows.add(new MsgRow("任务名称", "#" + task.serialNo + "-" + task.name));
            rows.add(new MsgRow("任务状态", task.statusName));
            String url = null;
            if (!task.isDelete) {
                url = getTaskHomeUrl(userId, task);
            }
            sendMessage(userId, "变更通知", content, rows, url, true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void sendMessage(String userId, String title, String content, List<MsgRow> rows, String url, boolean cardType) {
        String markdown = "";
        if (!StringUtil.isEmpty(content)) {
            markdown = content + "  \n";
        }
        if (!BizUtil.isNullOrEmpty(rows)) {
            markdown += Joiner.on("  \n").join(rows);
        }
        try {
            pushMessage(userId, title, markdown, "点击查看", url, cardType);
        } catch (Exception e) {
            logger.warn("send dingtalk message fail", e.getMessage());
        }

    }
}
