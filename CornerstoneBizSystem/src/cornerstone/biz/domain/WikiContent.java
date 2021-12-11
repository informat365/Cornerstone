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
 * Wiki页面内容
 * 
 * @author 杜展扬 2018-08-16
 *
 */
@DomainDefine(domainClass = WikiContent.class)
@DomainDefineValid(comment ="Wiki页面内容" ,uniqueKeys={@UniqueKey(fields={"wikiPageId","wikiPageChangeLogId"})})
public class WikiContent extends BaseDomain{
    //
	public int companyId;
    //
    @ForeignKey(domainClass=WikiPage.class)
    @DomainFieldValid(comment="页面",required=true,canUpdate=true)
    public int wikiPageId;
    
    @ForeignKey(domainClass=WikiPageChangeLog.class)
    @DomainFieldValid(comment="页面变更记录",required=true,canUpdate=true)
    public int wikiPageChangeLogId;
    
    @DomainFieldValid(comment="内容",canUpdate=true)
    public String content;
    
    //
    //   
    public static class WikiContentInfo extends WikiContent{
    //

    }
    //
    //   
    @QueryDefine(domainClass=WikiContentInfo.class)
    public static class WikiContentQuery extends BizQuery{
        //
        public Integer id;

        public Integer wikiPageId;

        public Integer wikiPageChangeLogId;

        public String content;

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
        public int wikiPageIdSort;
        public int wikiPageChangeLogIdSort;
        public int contentSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}