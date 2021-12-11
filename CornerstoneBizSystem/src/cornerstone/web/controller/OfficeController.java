package cornerstone.web.controller;

import cornerstone.biz.BizData;
import cornerstone.biz.domain.file.WpsFileInfo;
import cornerstone.biz.util.DumpUtil;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.web.mvc.*;
import jazmin.util.JSONUtil;
import jazmin.util.StringUtil;

import java.util.*;

/**
 * @author cs
 */
@Controller(id = "3rd")
public class OfficeController extends BaseController {

    private static final Logger logger = LoggerFactory.get(OfficeController.class);

    /**
     * 预览接口；
     * <p>
     * INSERT INTO `t_config`( `name`, `value`, `value_type`, `test`, `is_hidden`, `description`, `create_time`, `update_time`) VALUES ('wps.appId', '', 'String', 0, NULL, 'WPS APPID', '1971-01-01 00:00:00', '2018-08-23 21:31:12');
     * INSERT INTO `t_config`( `name`, `value`, `value_type`, `test`, `is_hidden`, `description`, `create_time`, `update_time`) VALUES ('wps.appKey', '', 'String', 0, NULL, 'WPS  APPKEY', '1971-01-01 00:00:00', '2018-08-23 21:31:12');
     * 通道：wps和live
     * 判断使用场景：如果用户配置了wps,则进行wps地址跳转
     *
     * @param ctx
     */
    @Service(id = "preview", method = HttpMethod.GET)
    public void preview(Context ctx) {
        String token = ctx.getString("token");
        String fileId = ctx.getString("fileId");
        String location = BizData.bizAction.getFilePreviewLocation(token, fileId);
        if (location == null) {
            ctx.view(new ErrorView(404));
            return;
        }
        ctx.view(new RedirectView(location));
    }

    /**
     * wps回调具体地址
     *
     * @param ctx
     */
    @Service(id = "file", queryCount = 3, method = HttpMethod.GET)
    public void wpsFileInfo(Context ctx) {
        String fileName = ctx.getString("_w_fname");
        String reqSignature = ctx.getString("_w_signature");
        WpsFileInfo wpsFileInfo = BizData.bizAction.getWpsFileInfo(fileName, reqSignature);
        if (wpsFileInfo == null) {
            ctx.view(new ErrorView(403));
            return;
        }
        ctx.view(new PlainTextView(JSONUtil.toJson(wpsFileInfo)));
    }

    /**
     * wps回调具体地址
     *
     * @param ctx
     */
    @Service(id = "user", queryCount = 3, method = HttpMethod.POST)
    public void wpsUserInfo(Context ctx) {
        String appId = ctx.getString("_w_appid");
        String fileName = ctx.getString("_w_fname");
        String ids = ctx.getString("ids");
        String reqSignature = ctx.getString("_w_signature");
        logger.warn("appId:{},ids:{},reqSignature:{}", appId, ids, reqSignature);
        WpsFileInfo wpsFileInfo = BizData.bizAction.getWpsFileInfo(fileName, reqSignature);
        if (wpsFileInfo == null) {
            ctx.view(new ErrorView(403));
            return;
        }
        Map<String, Object> map = new HashMap<>();
        List<WpsFileInfo.User> list = new ArrayList<>();
        list.add(wpsFileInfo.user);
        map.put("users", list);
        ctx.view(new PlainTextView(JSONUtil.toJson(map)));
    }

    /**
     * wps回调具体地址
     *
     * @param ctx
     */
    @Service(id = "onnotify", method = HttpMethod.POST)
    public void wpsNotify(Context ctx) {
        Set<String> paramKeys = ctx.request().queryParams();
        Map<String, Object> notify = new HashMap<>();
        if (paramKeys != null) {
            Map<String, String> params = new HashMap<>();
            paramKeys.forEach(key -> {
                params.put(key, ctx.request().queryParams(key));
            });
            notify.put("params", params);
        }
        String body = ctx.request().body();
        if (!StringUtil.isEmpty(body)) {
            try {
                Map<String, Object> bodyMap = JSONUtil.fromJsonMap(body, String.class, Object.class);
                notify.put("body", bodyMap);
            } catch (Exception e) {
                logger.warn("parse notify body error with data(" + body + ")", e);
            }
        }
        logger.warn(DumpUtil.dump(notify));
        ctx.view(new PlainTextView("SUCCESS"));
    }
}
