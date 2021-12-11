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
 * 表变更记录
 * 
 * @author 杜展扬 2019-01-04 17:05
 *
 */
@DomainDefine(domainClass = DesignerDatabaseChangeLog.class)
@DomainDefineValid(comment ="表变更记录" )
public class DesignerDatabaseChangeLog extends BaseDomain{
    //
	@ForeignKey(domainClass=Company.class)
	@DomainFieldValid(canUpdate=true, comment = "企业")
	public int companyId;
	
    @ForeignKey(domainClass=DesignerDatabase.class)
    @DomainFieldValid(comment="数据库ID",required=true,canUpdate=true)
    public int designerDatabaseId;
    
    @DomainFieldValid(comment="建表语句",canUpdate=true)
    public String createSql;
    
    @DomainFieldValid(comment="ddl",canUpdate=true)
    public String ddl;
    
    @DomainFieldValid(comment="dml",canUpdate=true)
    public String dml;
    
    public boolean isDdlChanged;
    
    public boolean isDmlChanged;
    
    @DomainFieldValid(comment="表数量",canUpdate=true)
    public int tableCount;
    //
    //   
    public static class DesignerDatabaseChangeLogInfo extends DesignerDatabaseChangeLog{
    		//
    	 	@NonPersistent
    	 	@DomainField(foreignKeyFields="designerDatabaseId",field="name")
    	 	@DomainFieldValid(comment = "数据库名")
    	 	public String databaseName;
    	 	
    	 	@NonPersistent
    	 	@DomainField(foreignKeyFields="designerDatabaseId",field="instanceId")
    	 	@DomainFieldValid(comment = "数据库名")
    	 	public String databaseInstanceId;
    }
    //
    //   
    @QueryDefine(domainClass=DesignerDatabaseChangeLogInfo.class)
    public static class DesignerTableChangeLogQuery extends BizQuery{
        //
        public Integer id;

        public Integer designerDatabaseId;

        public String createSql;

        public String ddl;
        
        public String dml;

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
        public int createSqlSort;
        public int designerDatabaseIdSort;
        public int dmlSort;
        public int ddlSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}