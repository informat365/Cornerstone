package cornerstone.web.controller;

import cornerstone.biz.BizData;
import cornerstone.biz.domain.GlobalConfig;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import jazmin.server.web.mvc.*;
import jazmin.util.JSONUtil;

/**
 * 描述:钉钉企业应用接入
 * create at 2020/12/24 14:21
 *
 * @author yaop
 */
@Controller(id = "dingtalk")
public class DingtalkController extends BaseController {

    private static Logger logger = LoggerFactory.get(DingtalkController.class);


    /**
     * https://ding-doc.dingtalk.com/doc#/serverapi2/etaarr
     * 钉钉手机端进入cs入口
     */
    @Service(id = "enter")
    public void enter(Context ctx) throws UnsupportedEncodingException {
        String rdUrl = GlobalConfig.webUrl + "p/dingtalk/login";
        rdUrl = URLEncoder.encode(rdUrl, "utf-8");
        String url="https://oapi.dingtalk.com/connect/oauth2/sns_authorize?appid=%s&response_type=code&scope=snsapi_auth&state=&redirect_uri=%s";
        String targetURL =String.format(url,GlobalConfig.dingtalkMobileAppId,rdUrl);
        RedirectView rdView = new RedirectView(targetURL);
        logger.info("enter rd {}", targetURL);
        rdView.setTitle("正在加载");
        ctx.view(rdView);
    }

    /**
     * 钉钉应用内重定向地址获取code
     */
    @Service(id = "login")
    public void login(Context ctx)  {
        String code = ctx.getString("code");
        if (code == null) {
            throw new IllegalArgumentException("missing code or state");
        }
        String token;
        try {
            token = BizData.dingtalkAction.login(code);
        } catch (Exception e) {
            logger.catching(e);
            RedirectView rdView = new RedirectView(getRedirectUrl(ctx));
            rdView.setTitle("正在加载");
            ctx.view(rdView);
            return;
        }
        setTokenCookie(ctx, token);
        String rdUrl = getRedirectUrl(ctx);
        //
        RedirectView rdView = new RedirectView(rdUrl);
        rdView.setTitle("正在加载");
        ctx.view(rdView);
    }

    /**
     * https://ding-doc.dingtalk.com/doc#/serverapi2/kymkv6/Q23rX
     * CS获取绑定钉钉的链接
     */
    @Service(id = "get_bind_url")
    public void getBindUrl(Context ctx) throws UnsupportedEncodingException {
        String token = ctx.getString("token", true);
        String rdUrl = GlobalConfig.webUrl + "p/dingtalk/bind";
        rdUrl = URLEncoder.encode(rdUrl, "utf-8");
        String url ="https://oapi.dingtalk.com/connect/qrconnect?appid=%s&response_type=code&scope=snsapi_login&state=%s&redirect_uri=%s";
        String targetURL = String.format(url,GlobalConfig.dingtalkMobileAppId,token,rdUrl);
        RedirectView rdView = new RedirectView(targetURL);
        logger.info("get_bind_url rd {}", targetURL);
        rdView.setTitle("正在加载");
        ctx.view(rdView);
    }

    /**
     * 回调绑定钉钉地址
     */
    @Service(id = "bind")
    public void bind(Context ctx)  {
        String token = ctx.getString("state", true);
        String code = ctx.getString("code");
        if (code == null) {
            throw new IllegalArgumentException("missing code or state");
        }
        try {
            BizData.dingtalkAction.bind(token, code);
        } catch (Exception e) {
            logger.catching(e);
            ctx.view(new ResourceView("/page/common/error.jsp"));
            return;
        }
        String rdUrl = GlobalConfig.webUrl + "bind_success.html";
        RedirectView rdView = new RedirectView(rdUrl);
        ctx.view(rdView);
    }


    /**
     * 解绑钉钉
     */
    @Service(id = "unbind")
    public void unbind(Context ctx) {
        String token = ctx.getString("token", true);
        try {
            BizData.dingtalkAction.unbind(token);
        } catch (Exception e) {
            logger.catching(e);
            ctx.view(new ResourceView("/page/common/error.jsp"));
            return;
        }
        Map<String, String> result = new HashMap<>();
        result.put("result", "success");
        ctx.view(new PlainTextView(JSONUtil.toJson(result)));
    }

    @Service(id="task_info")
    public void taskInfo(Context ctx) throws UnsupportedEncodingException {
        String userIdEncode=ctx.getString("userId");
        String taskUuid=ctx.getString("taskUuid");
        String token;
        try {
            token=BizData.dingtalkAction.loginWithEncodeUserId(userIdEncode);
        } catch (Exception e) {
            logger.catching(e);
            enter(ctx);
            return;
        }
        setTokenCookie(ctx, token);
        String rdUrl = GlobalConfig.webUrl+"mobile/#/m/task?id="+taskUuid;
        logger.info("rdUrl:{}",rdUrl);
        RedirectView rdView=new RedirectView(rdUrl);
        ctx.view(rdView);
    }

    protected String getRedirectUrl(Context ctx) {
        return GlobalConfig.webUrl + "mobile/#/t/todo";
    }

    private void setTokenCookie(Context ctx,String token) {
        Cookie cookie=new Cookie("token", token);
        cookie.setMaxAge(-1);
        cookie.setPath("/mobile");
        ctx.response().raw().addCookie(cookie);
        logger.info("setTokenCookie cookie:{} token:{}",cookie,token);
    }
}
