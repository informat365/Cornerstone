package cornerstone.biz.action;

import cornerstone.biz.ConstDefine;
import cornerstone.biz.dao.BizDAO;
import cornerstone.biz.domain.Account;
import cornerstone.biz.domain.AccountToken;
import cornerstone.biz.domain.wx.WeixinMessage;
import cornerstone.biz.srv.BizService;
import cornerstone.biz.srv.DingtalkService;
import cornerstone.biz.srv.WeixinService;
import cornerstone.biz.util.StringUtil;
import cornerstone.biz.util.TripleDESUtil;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;
import jazmin.driver.jdbc.Transaction;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.rpc.RpcService;
import jazmin.util.DumpUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 钉钉企业内部H5微应用
 *
 * @author cs
 */
public interface DingtalkAction {

    /**
     * @param xml
     * @return
     * @throws AppException
     */
    void receiveSuiteMessage(String xml) throws AppException;

    /**
     * 钉钉登录
     */
    String login(String code);

    /**
     * 绑定钉钉
     */
    void bind(String token, String code);

    /**
     * 取消绑定钉钉
     */
    void unbind(String token);

    /**
     * 是否绑定
     */
    boolean isBind(String token);

    /**
     * 通过加密UserID登录
     */
    String loginWithEncodeUserId(String encodeUserId);

    @RpcService
    class DingtalkActionImpl implements DingtalkAction {
        //
        @AutoWired
        DingtalkService dingtalkService;
        @AutoWired
        BizService bizService;
        @AutoWired
        BizDAO dao;
        //
        private static Logger logger = LoggerFactory.get(DingtalkActionImpl.class);

        //
        @Transaction
        @Override
        public void receiveSuiteMessage(String xml) throws AppException {
            WeixinMessage msg = WeixinService.parseWeixinMessage(xml);
            if (logger.isDebugEnabled()) {
                logger.debug("receive weixin message \n{} msg:{}", xml, DumpUtil.dump(msg));
            }
            // callback
            Map<String, Object> args = new HashMap<>();
            args.put("msg", msg);
//			Jazmin.notificationCenter.post(NotificationEvent.Notification_微信公众号收到微信消息, args);
            //
        }

        @Override
        public String login(String code) {
            dingtalkService.refreshAccessToken();
            String unionId = dingtalkService.getUnionIdByCode(code);
            String userId = dingtalkService.getUserIDByUnionId(unionId);
            Account account = dao.getAccountByDingtalkUserId(userId);
            if (account == null) {
                logger.error("account==null dingtalkUserId:{}", userId);
                throw new AppException("权限不足");
            }
            AccountToken userToken = dao.getAccountTokenByAccountId(account.id);
            return userToken.token;
        }

        @Override
        public String loginWithEncodeUserId(String encodeUserId) {
            String userId = TripleDESUtil.decryptWithUrlEncoder(encodeUserId, ConstDefine.GLOBAL_KEY);
            Account account = dao.getAccountByDingtalkUserId(userId);
            if (account == null) {
                logger.error("account==null dingtalkUserId:{}", userId);
                throw new AppException("权限不足");
            }
            AccountToken userToken = dao.getAccountTokenByAccountId(account.id);
            return userToken.token;
        }

        @Override
        public void bind(String token, String code) {
            Account account = bizService.getExistedAccountByToken(token);
            if (!bizService.isPrivateDeploy(account)) {
                logger.error("isNotPrivateDeploy account:{}", account.id);
                throw new AppException("权限不足");
            }
            dingtalkService.refreshAccessToken();
            String unionId = dingtalkService.getUnionIdByCode(code);
            String userId = dingtalkService.getUserIDByUnionId(unionId);
            account.dingtalkUserId = userId;
            dao.updateSpecialFields(account, "dingtalkUserId");
            logger.info("bind success account:{} UserId:{}", account.id, userId);
        }

        @Override
        public void unbind(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            account.dingtalkUserId = null;
            dao.updateSpecialFields(account, "dingtalkUserId");
            logger.info("unbind success account:{}", account.id);
        }

        @Override
        public boolean isBind(String token) {
            Account account = bizService.getExistedAccountByToken(token);
            return !StringUtil.isEmpty(account.dingtalkUserId);
        }


    }


}
