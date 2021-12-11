package cornerstone.biz.util;

import java.util.HashMap;
import java.util.Map;

import cornerstone.biz.CornerstoneBizSystem;
import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.domain.Task.TaskInfo;
import cornerstone.biz.srv.WeixinService;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;

/**
 * @author icecooly
 */
public class WxSendMsgUtil {
    //
    private static Logger logger = LoggerFactory.getLogger(WxSendMsgUtil.class);

    /**
     * @param task
     * @param openId
     * @param title
     * @param remark
     */
    public static void sendTaskInfoMessage(WeixinService weixinService, TaskInfo task, String openId,
                                           String title, String remark) {
        try {
            if (StringUtil.isEmpty(openId)|| CornerstoneBizSystem.isDebug) {
                return;
            }
            Map<String, String> vars = new HashMap<String, String>();
            vars.put("first", title + "-#" + task.serialNo + task.name);
            vars.put("keyword1", task.projectName + "-" + task.objectTypeName);
            vars.put("keyword2", task.statusName);
            vars.put("remark", remark);
            String url = null;
            if (!task.isDelete) {
                url = GlobalConfig.webUrl + "p/wx/enter_task_info?state=" + task.uuid;
            }
            sendMessage(weixinService, GlobalConfig.getValue("wx.template.taskMsgId"), openId, vars, url);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    //
    public static void sendMessage(WeixinService weixinService, String openId,
                                   String first, String keyword1, String keyword2, String remark, String url) {
        try {
            if (StringUtil.isEmpty(openId)|| CornerstoneBizSystem.isDebug) {
                return;
            }
            Map<String, String> vars = new HashMap<String, String>();
            vars.put("first", first);
            vars.put("keyword1", keyword1);
            vars.put("keyword2", keyword2);
            vars.put("remark", remark);
            sendMessage(weixinService, GlobalConfig.getValue("wx.template.taskMsgId"), openId, vars, url);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    //
    private static void sendMessage(WeixinService weixinService, String templateId, String openId,
                                    Map<String, String> vars, String url) {
        try {
            weixinService.refreshAccessToken();
            weixinService.sendTemplateMessage(openId, templateId, vars, url);
            StringBuilder sb = new StringBuilder();
            vars.forEach((k, v) -> {
                sb.append(k).append("=").append(v);
            });
            logger.info("sendMessage id:{} openId:{} message:{} vars:{} url:{}",
                    templateId, openId, sb.toString(), vars, url);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


}
