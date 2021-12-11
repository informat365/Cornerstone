package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.*;

import java.util.Date;

/**
 * 里程碑日志
 */
@DomainDefine(domainClass = LandmarkLog.class)
@DomainDefineValid(comment = "里程碑日志")
public class LandmarkLog extends BaseDomain {
    public static final int TYPE_时间调整=1;
    public static final int TYPE_其他调整=2;

    @DomainFieldValid(comment = "阶段", required = true, canUpdate = true)
    public int landmarkId;

    @DomainFieldValid(comment = "日志类型",canUpdate = true)
    public int type=2;

    @DomainFieldValid(comment = "日志信息", canUpdate = true, maxValue = 512)
    public String content;

    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "创建人", canUpdate = true)
    public int createAccountId;

    @DomainFieldValid(comment = "更新人", canUpdate = true)
    public int updateAccountId;

    public static class LandmarkLogInfo extends LandmarkLog {

        @NonPersistent
        @DomainField(foreignKeyFields = "createAccountId", field = "name")
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;

        @NonPersistent
        @DomainField(foreignKeyFields = "createAccountId", field = "imageId")
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;


    }

    //
    //   
    @QueryDefine(domainClass = LandmarkLogInfo.class)
    public static class LandmarkLogQuery extends BizQuery {
        //
        public Integer id;

        public Integer landmarkId;

        public String content;

        @QueryField(operator = ">=", field = "createTime")
        public Date createTimeStart;

        @QueryField(operator = "<=", field = "createTime")
        public Date createTimeEnd;

        @QueryField(operator = ">=", field = "updateTime")
        public Date updateTimeStart;

        @QueryField(operator = "<=", field = "updateTime")
        public Date updateTimeEnd;

        //in or not in fields

        //inner joins
        //sort
        public int idSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}