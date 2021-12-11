package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.*;

import java.util.Date;

/**
 * 成员关注
 * @author yaop
 */
@DomainDefine(domainClass = AccountStar.class)
@DomainDefineValid(comment = "成员关注")
public class AccountStar extends BaseDomain {
    public static final int TYPE_项目=1;
    public static final int TYPE_任务=2;

    @DomainFieldValid(comment = "企业", required = true)
    public int companyId;

    @DomainFieldValid(comment = "成员", required = true)
    public int accountId;

    @DomainFieldValid(comment = "关注类型",canUpdate = true)
    public int type;

    @DomainFieldValid(comment = "关联对象ID", canUpdate = true)
    public int associateId;


    public static class AccountStarInfo extends AccountStar {

    }

    @QueryDefine(domainClass = AccountStarInfo.class)
    public static class AccountStarQuery extends BizQuery {
        public Integer id;

        public Integer companyId;

        public Integer accountId;

        public Integer type;

        public Integer associateId;


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
        public int typeSort;
        public int accountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}