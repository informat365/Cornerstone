package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.*;

import java.util.Date;

/**
 * 版本管理软件分支配置
 *
 * @author 杜展扬 2018-08-17
 */
@DomainDefine(domainClass = ScmBranch.class)
@DomainDefineValid(comment = "版本管理软件分支")
public class ScmBranch extends BaseDomain {
    //
    //
    @ForeignKey(domainClass = Company.class)
    @DomainFieldValid(comment = "企业", required = true, canUpdate = true)
    public int companyId;

    @DomainFieldValid(comment = "项目名", required = true, canUpdate = true, maxValue = 64)
    public String project;

    @DomainFieldValid(comment = "分支名", required = true, canUpdate = true, maxValue = 64)
    public String branch;

    @DomainFieldValid(comment = "是否关联", canUpdate = true)
    public boolean isAssociate;

    @DomainFieldValid(comment = "是否统计代码量", canUpdate = true)
    public boolean isStat;

    @DomainFieldValid(comment = "创建人", canUpdate = true)
    public int createAccountId;

    @DomainFieldValid(comment = "更新人", canUpdate = true)
    public int updateAccountId;

    public static class ScmBranchInfo extends ScmBranch {


    }

    //
    //   
    @QueryDefine(domainClass = ScmBranchInfo.class)
    public static class ScmBranchQuery extends BizQuery {
        //
        public Integer id;

        public Integer companyId;

        public String branch;

        public Boolean isAssociate;

        public Boolean isStat;

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