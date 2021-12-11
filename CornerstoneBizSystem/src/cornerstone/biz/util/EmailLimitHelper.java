package cornerstone.biz.util;

import jazmin.util.DumpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 邮件发送限流每次2个
 */
public class EmailLimitHelper {

    private static Logger logger = LoggerFactory.getLogger(EmailLimitHelper.class);

    private static Queue<EmailEntity> emails = new ArrayBlockingQueue<>(1 << 12);

    private static boolean running = false;

    /*private static SendThread worker;

    private static class SendThread extends Thread {

        @Override
        public void run() {
            logger.info("email send thread start.");
            while (running) {
                try {
                    EmailEntity email = emails.poll();
                    logger.info("rest emails :{} to send",emails.size());
                    if (null != email) {
                        long startTime = System.currentTimeMillis();
                        EmailMsgUtil.sendMessage0(email.email, email.title, email.content, email.url);
                        //邮件发送过快还是有 connect limit frequency问题
                        long cost = System.currentTimeMillis() - startTime;
                        if (cost < 500) {
                            Thread.sleep(500 - cost);
                        }
                    }
                } catch (Exception e) {
                    logger.error("email send thread ERR", e);
                }
            }
        }
    }*/

    /*public static void shutdown() {
        running = false;
        logger.info("shutdown email thread , rest :{}", emails.size());
    }*/

    public static void toQueue(String toEmails, String title, String content, String url) {
        EmailEntity entity = new EmailEntity();
        entity.email = toEmails;
        entity.title = title;
        entity.content = content;
        entity.url = url;
        toQueue(entity);
//        start();
    }

    private static void toQueue(EmailEntity email) {
        try {
            boolean add = emails.offer(email);
            if (!add) {
                logger.error("save email to queue fail ,email:{}", DumpUtil.dump(email));
            }
        } catch (Exception e) {
            logger.error("save email to queue fail,email:{}", DumpUtil.dump(email));
        }
    }

  /*  private synchronized static void start() {
        if (null == worker) {
            if (!running) {
                worker = new SendThread();
                running = true;
                worker.start();
            }
        }
    }*/

    public static EmailEntity getEmail(){
        return emails.poll();
    }

    public static class EmailEntity{
        public String email;
        public String title;
        public String content;
        public String url;

    }


}
