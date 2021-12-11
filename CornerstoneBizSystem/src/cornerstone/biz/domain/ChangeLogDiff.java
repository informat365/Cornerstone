package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 变更记录对比
 * 
 * @author 杜展扬 2018-07-29
 *
 */
@DomainDefine(domainClass = ChangeLogDiff.class)
@DomainDefineValid(comment ="变更记录对比" )
public class ChangeLogDiff extends BaseDomain{
    //
    //
	public int companyId;
	
	@ForeignKey(domainClass=Task.class)
	@DomainFieldValid(comment="任务",canUpdate=true)
	public int taskId;
	 
    @DomainFieldValid(comment="变更前",canUpdate=true)
    public String beforeContent;
    
    @DomainFieldValid(comment="变更后",canUpdate=true)
    public String afterContent;
    
    //
    //   
    public static class ChangeLogDiffInfo extends ChangeLogDiff{
    //

    }
    //
    //   
    @QueryDefine(domainClass=ChangeLogDiffInfo.class)
    public static class ChangeLogDiffQuery extends BizQuery{
        //
        public Integer id;
        
        public Integer companyId;
        
        public Integer taskId;

        public String beforeContent;

        public String afterContent;
        
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
        public int taskIdSort;
        public int beforeContentSort;
        public int afterContentSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}