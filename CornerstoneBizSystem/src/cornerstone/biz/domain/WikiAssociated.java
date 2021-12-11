package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;

import java.util.Date;

/**
 * 任务wiki关联
 * 
 * @author 杜展扬 2018-08-18
 *
 */
@DomainDefine(domainClass = WikiAssociated.class)
@DomainDefineValid(comment ="wiki关联" ,uniqueKeys={@UniqueKey(fields={"wikiPageId","projectId","taskId"})})
public class WikiAssociated extends BaseDomain{

    //
	@ForeignKey(domainClass=Company.class)
	public int companyId;
    //
    @ForeignKey(domainClass=WikiPage.class)
    @DomainFieldValid(comment="wikipage ID",required=true,canUpdate=true)
    public int wikiPageId;

    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;
    
    @ForeignKey(domainClass=Task.class)
    @DomainFieldValid(comment="任务",required=true,canUpdate=true)
    public int taskId;
    
    //
    //   
    public static class WikiAssociatedInfo extends WikiAssociated {
    //

    }
    //
    //   
    @QueryDefine(domainClass=WikiAssociatedInfo.class)
    public static class WikiAssociatedQuery extends BizQuery{
        //
        public Integer id;
        
        public Integer companyId;

        public Integer wikiPageId;

        public Integer projectId;

        public Integer taskId;

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
        public int projectIdSort;
        public int taskIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}