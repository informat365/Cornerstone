package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 回收站
 * 
 * @author 杜展扬 2018-08-21
 *
 */
@DomainDefine(domainClass = CompanyRecycle.class)
@DomainDefineValid(comment ="回收站" )
public class CompanyRecycle extends BaseDomain{
    //
	public static final int TYPE_TASK = 1;
    public static final int TYPE_WIKI = 2;
    public static final int TYPE_WIKI页面 = 3;
    public static final int TYPE_迭代 = 4;
    public static final int TYPE_Release = 5;
    public static final int TYPE_子系统 = 6;
    public static final int TYPE_主机 = 7;
    public static final int TYPE_汇报 = 8;
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @DomainFieldValid(comment="关联ID",required=true,canUpdate=true)
    public int associatedId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=200)
    public String name;
    
    @DomainFieldValid(comment="类型",canUpdate=true,dataDict="CompanyRecycle.type")
    public int type;
    
    @ForeignKey(domainClass=ObjectType.class)
    @DomainFieldValid(comment="对象类型",required=true,canUpdate=true)
    public int objectType;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="创建人",canUpdate=true,maxValue=64)
    public String createAccountName;
    
    //
    //   
    public static class CompanyRecycleInfo extends CompanyRecycle{
    		//
    		@DomainField(foreignKeyFields="objectType",field="name",persistent=false)
        public String objectTypeName;

    }
    //
    //   
    @QueryDefine(domainClass=CompanyRecycleInfo.class)
    public static class CompanyRecycleQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;

        public Integer associatedId;

        public String name;

        public Integer type;

        public Integer objectType;

        public Integer createAccountId;

        public String createAccountName;

        @QueryField(operator=">=",field="createTime")
        public Date createTimeStart;
        
        @QueryField(operator="<=",field="createTime")
        public Date createTimeEnd;

        @QueryField(operator=">=",field="updateTime")
        public Date updateTimeStart;
        
        @QueryField(operator="<=",field="updateTime")
        public Date updateTimeEnd;

        //in or not in fields
        @QueryField(operator="in",field="type")
        public int[] typeInList;
        
        @QueryField(operator="not in",field="type")
        public int[] typeNotInList;
        

        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int associatedIdSort;
        public int nameSort;
        public int typeSort;
        public int objectTypeSort;
        public int createAccountIdSort;
        public int createAccountNameSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}