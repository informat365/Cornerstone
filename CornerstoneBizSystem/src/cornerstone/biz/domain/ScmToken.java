package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 版本管理软件TOKEN
 * 
 * @author 杜展扬 2018-08-17
 *
 */
@DomainDefine(domainClass = ScmToken.class)
@DomainDefineValid(comment ="版本管理软件TOKEN" ,uniqueKeys={@UniqueKey(fields={"companyId","type"}),@UniqueKey(fields={"token"})})
public class ScmToken extends BaseDomain{
    //
	public static final int TYPE_SVN = 1;
	public static final int TYPE_GITLAB = 2;
	public static final int TYPE_GITHUB = 3;
    //
	@DomainFieldValid(comment="类型",canUpdate=true,dataDict="ScmToken.type")
    public int type;
	
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @DomainFieldValid(comment="token",required=true,canUpdate=true,maxValue=64)
    public String token;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class ScmTokenInfo extends ScmToken{
    //

    }
    //
    //   
    @QueryDefine(domainClass=ScmTokenInfo.class)
    public static class ScmTokenQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;

        public String token;

        public Integer createAccountId;

        public Integer updateAccountId;

        @QueryField(operator=">=",field="createTime")
        public Date createTimeStart;
        
        @QueryField(operator="<=",field="createTime")
        public Date createTimeEnd;

        @QueryField(operator=">=",field="updateTime")
        public Date updateTimeStart;
        
        @QueryField(operator="<=",field="updateTime")
        public Date updateTimeEnd;

        //in or not in fields

        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int tokenSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}