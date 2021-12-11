package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * 钉钉成员
 */
@DomainDefine(domainClass = DingtalkMember.class)
@DomainDefineValid(comment = "钉钉成员")
public class DingtalkMember extends BaseDomain {

    //
    //
    @DomainFieldValid(comment = "成员",  canUpdate = true)
    public int accountId;

    @DomainFieldValid(comment = "成员",  canUpdate = true)
    public int companyId;

    @DomainFieldValid(comment = "钉钉成员ID", required = true, canUpdate = true)
    public String dingtalkId;

    @DomainFieldValid(comment = "名称", canUpdate = true, maxValue = 128)
    public String name;

    @DomainFieldValid(comment = "手机号", canUpdate = true, maxValue = 16)
    public String mobileNo;

    @DomainFieldValid(comment = "工号", canUpdate = true, maxValue = 32)
    public String jobNumber;

    @DomainFieldValid(comment = "职位", canUpdate = true, maxValue = 32)
    public String title;

    @DomainFieldValid(comment = "创建时间", canUpdate = true)
    public Date createTime;

    @DomainFieldValid(comment = "更新时间", canUpdate = true)
    public Date updateTime;

    public static class DingtalkMemberInfo extends DingtalkMember {



    }

    //
    //   
    @QueryDefine(domainClass = DingtalkMemberInfo.class)
    public static class DingtalkMemberQuery extends BizQuery {


        //
        public Integer id;

        public Integer accountId;

        public String dingtalkId;

        public String name;

        public String jobNumber;

        public String title;


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