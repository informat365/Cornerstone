package cornerstone.biz.srv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import cornerstone.biz.dao.BizDAO;
import cornerstone.biz.domain.ScmCommitLog;
import cornerstone.biz.domain.ScmRetry;
import cornerstone.biz.util.StringUtil;
import jazmin.core.Jazmin;
import jazmin.core.app.AutoWired;
import jazmin.driver.jdbc.Transaction;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;

/**
 * @author cs
 */
public class WebApiService extends CommService{

    @AutoWired
    BizDAO dao;

    private Logger logger = LoggerFactory.getLogger(WebApiService.class);

    private Queue<ScmRetry> scms = new ArrayBlockingQueue<>(1 << 12);

    //
    public static WebApiService get() {
        return Jazmin.getApplication().getWired(WebApiService.class);
    }

    //
    public List<Map<String, Object>> queryList(String sql, List<Object> parameters) {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        return dao.querListMap(sql, parameters.toArray());
    }
    //

    public Long queryLong(String sql, List<Object> parameters) {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        return dao.queryForLong(sql, parameters.toArray());
    }
    //

    public Integer queryCount(String sql, List<Object> parameters) {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        return dao.queryForInteger(sql, parameters.toArray());
    }
    //

    public Integer queryInteger(String sql, List<Object> parameters) {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        return dao.queryForInteger(sql, parameters.toArray());
    }

    public String queryForString(String sql, List<Object> parameters) {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        return dao.queryForString(sql, parameters.toArray());
    }



    public ScmRetry getScmRetry(){
        return  scms.poll();
    }

    public void toQueue(ScmRetry scmRetry) {
        logger.info("to queue version:{}",scmRetry.items.get(5));
        try {
            boolean add = scms.offer(scmRetry);
            if (!add) {
                logger.error("save retry to queue fail ,info:{}", DumpUtil.dump(scmRetry));
            }
        } catch (Exception e) {
            logger.error("save retry entity to queue fail,info:{}", DumpUtil.dump(scmRetry));
        }
    }

}
