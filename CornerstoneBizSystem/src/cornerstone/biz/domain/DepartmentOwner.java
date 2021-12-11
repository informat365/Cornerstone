package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 企业部门负责人
 * 
 * @author 杜展扬 2019-06-26 19:12
 *
 */
@DomainDefine(domainClass = DepartmentOwner.class)
@DomainDefineValid(comment ="企业部门负责人" ,uniqueKeys={@UniqueKey(fields={"departmentId","ownerAccountId"})})
public class DepartmentOwner extends BaseDomain{
    //
	@ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",canUpdate=true)
    public int companyId;
	
    @ForeignKey(domainClass=Department.class)
    @DomainFieldValid(comment="部门",required=true,canUpdate=true)
    public int departmentId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="负责人",required=true,canUpdate=true)
    public int ownerAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    //
    //   
    public static class DepartmentOwnerInfo extends DepartmentOwner{
    //
        @DomainField(foreignKeyFields="ownerAccountId",field="imageId",persistent=false)
        @DomainFieldValid(comment = "负责人头像")
        public String ownerAccountImageId;
    
        @DomainField(foreignKeyFields="ownerAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "负责人名称")
        public String ownerAccountName;
    

    }
    //
    //   
    @QueryDefine(domainClass=DepartmentOwnerInfo.class)
    public static class DepartmentOwnerQuery extends BizQuery{
        //
        public Integer id;
        
        public Integer companyId;

        public Integer departmentId;

        public Integer ownerAccountId;

        public Integer createAccountId;

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
        @QueryField(foreignKeyFields="ownerAccountId",field="imageId")
        public String ownerAccountImageId;
        
        @QueryField(foreignKeyFields="ownerAccountId",field="name")
        public String ownerAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int departmentIdSort;
        public int ownerAccountIdSort;
        public int ownerAccountImageIdSort;
        public int ownerAccountNameSort;
        public int createAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}