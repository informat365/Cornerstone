package cornerstone.biz.util;

import java.io.InputStream;
import java.util.regex.Matcher;

import cornerstone.biz.domain.Account;
import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.domain.Task.TaskInfo;
import cornerstone.biz.srv.EmailService;
import jazmin.core.Jazmin;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.IOUtil;

/**
 * @author cs
 */
public class EmailMsgUtil {
    //
    private static Logger logger = LoggerFactory.get(EmailMsgUtil.class);
    //

    /**
     * @param task
     */
    public static void sendTaskInfoMessage(TaskInfo task, Account account, String content) {
        try {
            if (account == null || StringUtil.isEmpty(account.email)) {
                return;
            }
            if(null==account.lastLoginTime||account.lastLoginTime.before(DateUtil.getNextDay(-30))){
                logger.info("account is inactive,interrupt email send,accountId:{},accountName:{},accountLastLoginTime:{}",account.id,account.name,account.lastLoginTime);
            }
            String url = null;
            if (!task.isDelete) {
                //https://pm.itit.io/#/pm/project/de0c7892507a4864ac563c3134f19481/task2?id=6615868fdab843bc97e241315c70b90d
                url = GlobalConfig.webUrl + "#/pm/project/" + task.projectUuid + "/task" + task.objectType + "?id=" + task.uuid;
            }
            StringBuilder contentSb = new StringBuilder();
            contentSb.append("项目:").append(task.projectName).append("<br/>");
            contentSb.append("类型:").append(task.objectTypeName).append("<br/>");
            contentSb.append("名称:#").append(task.serialNo).append(task.name).append("<br/>");
            contentSb.append("通知内容:").append(content).append("<br/>");
            sendMessage(account.email, "变更通知", contentSb.toString(), url);
        } catch (Exception e) {
            logger.error("sendTaskInfoMessage failed.task:{} account:{}", task.id, account.id);
            logger.error(e.getMessage(), e);
        }
    }

    //
    public static void sendMessage(Account account, String title, String content, String url) {
        try {
            if (account == null || StringUtil.isEmpty(account.email)) {
                return;
            }
            if(null==account.lastLoginTime||account.lastLoginTime.before(DateUtil.getNextDay(-30))){
                logger.info("account is inactive,interrupt email send,accountId:{},accountName:{},accountLastLoginTime:{}",account.id,account.name,account.lastLoginTime);
            }
            StringBuilder contentSb = new StringBuilder();
            contentSb.append("通知内容：").append(content).append("<br/>");
            sendMessage(account.email, title, contentSb.toString(), url);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    //
    public static void sendMessage(Account account, String title, String first, String keyword1, String keyword2, String remark, String url) {
        try {
            if (account == null || StringUtil.isEmpty(account.email)) {
                return;
            }
            if(null==account.lastLoginTime||account.lastLoginTime.before(DateUtil.getNextDay(-30))){
                logger.info("account is inactive,interrupt email send,accountId:{},accountName:{},accountLastLoginTime:{}",account.id,account.name,account.lastLoginTime);
            }
            StringBuilder contentSb = new StringBuilder();
            contentSb.append("项目:").append(keyword1).append("<br/>");
            contentSb.append("类型:").append(keyword2).append("<br/>");
            contentSb.append("通知内容:").append(first).append("<br/>");
            contentSb.append("备注:").append(remark).append("<br/>");
            sendMessage(account.email, title, contentSb.toString(), url);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    //
    private static void sendMessage(String toEmails, String title, String content, String url) {
        EmailLimitHelper.toQueue(toEmails,title,content,url);
       /* try {
            EmailService emailService = Jazmin.getApplication().getWired(EmailService.class);
            InputStream is = EmailService.class.getResourceAsStream("email_send_notification.html");
            String htmlContent = IOUtil.toString(is);
            htmlContent = htmlContent.replaceAll("#content#", content);
            htmlContent = htmlContent.replaceAll("#btnName#", "查看详情");
            if (url != null) {
                htmlContent = htmlContent.replaceAll("#url#", url);
            } else {
                htmlContent = htmlContent.replaceAll("#url#", GlobalConfig.webUrl);
            }
            emailService.sendMail(toEmails, title, htmlContent);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException("邮件发送失败");
        }*/
    }

    public static void sendMessage0(String toEmails, String title, String content, String url){
        try {
            EmailService emailService = Jazmin.getApplication().getWired(EmailService.class);
            InputStream is = EmailService.class.getResourceAsStream("email_send_notification.html");
            String htmlContent = IOUtil.toString(is);
            htmlContent = htmlContent.replaceAll("#content#", Matcher.quoteReplacement(content));
            htmlContent = htmlContent.replaceAll("#btnName#", "查看详情");
            if (url != null) {
                htmlContent = htmlContent.replaceAll("#url#", url);
            } else {
                htmlContent = htmlContent.replaceAll("#url#", GlobalConfig.webUrl);
            }
            emailService.sendMail(toEmails, title, htmlContent);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException("邮件发送失败");
        }
    }

}
