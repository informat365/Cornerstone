package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.NonPersistent;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * wikie页面变更记
 * 
 * @author 杜展扬 2018-08-16
 *
 */
@DomainDefine(domainClass = WikiPageChangeLog.class)
@DomainDefineValid(comment ="wikie页面变更记" )
public class WikiPageChangeLog extends BaseDomain{
    //
	@ForeignKey(domainClass=Wiki.class)
	public int wikiId;
    //
    @ForeignKey(domainClass=WikiPage.class)
    @DomainFieldValid(comment="WIKI",required=true,canUpdate=true)
    public int wikiPageId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=128)
    public String name;
    
    @ForeignKey(domainClass=WikiContent.class)
    @DomainFieldValid(comment="内容")
    public int contentId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class WikiPageChangeLogInfo extends WikiPageChangeLog{
    		//
    		@NonPersistent
        @DomainField(foreignKeyFields="wikiPageId")
        public int wikiId;
    		//
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="imageId")
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
    
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="name")
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
    
    }
    //
    public static class WikiPageChangeLogDetailInfo extends WikiPageChangeLogInfo{
    		@NonPersistent
		@DomainField(foreignKeyFields="contentId",field="content")
    		@DomainFieldValid(comment = "内容")
    		public String content;
    }
    //   
    @QueryDefine(domainClass=WikiPageChangeLogInfo.class)
    public static class WikiPageChangeLogQuery extends BizQuery{
        //
        public Integer id;

        public Integer wikiPageId;

        public String name;

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
        @QueryField(foreignKeyFields="createAccountId",field="imageId")
        public String createAccountImageId;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int wikiPageIdSort;
        public int nameSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}