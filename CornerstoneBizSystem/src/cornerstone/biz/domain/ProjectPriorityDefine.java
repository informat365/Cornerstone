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
 * 项目优先级定义
 * 
 * @author 杜展扬 2018-07-29
 *
 */
@DomainDefine(domainClass = ProjectPriorityDefine.class)
@DomainDefineValid(comment ="项目优先级定义" ,uniqueKeys={@UniqueKey(fields={"projectId","objectType","name"})})
public class ProjectPriorityDefine extends BaseDomain{
    //
	@ForeignKey(domainClass=Company.class)
	public int companyId;
    //
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="projectId",canUpdate=true)
    public int projectId;
    
    @DomainFieldValid(comment="对象类型",required=true,canUpdate=true)
    public int objectType;
    
    @DomainFieldValid(comment="name",canUpdate=true,maxValue=64)
    public String name;
    
    @DomainFieldValid(comment="color",canUpdate=true)
    public String color;
    
    @DomainFieldValid(comment="isDefault",canUpdate=true)
    public boolean isDefault;
    
    @DomainFieldValid(comment="remark",canUpdate=true,maxValue=512)
    public String remark;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    @DomainFieldValid(comment="排序字段")
    public int sortWeight;
    
    //
    //   
    public static class ProjectPriorityDefineInfo extends ProjectPriorityDefine{
    //

    }
    //
    //   
    @QueryDefine(domainClass=ProjectPriorityDefineInfo.class)
    public static class ProjectPriorityDefineQuery extends BizQuery{
        //
        public Integer id;
        
        public Integer companyId;

        public Integer projectId;

        public Integer objectType;

        public String name;

        public String color;

        public Boolean isDefault;

        public String remark;

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
        public int projectIdSort;
        public int objectTypeSort;
        public int nameSort;
        public int colorSort;
        public int isDefaultSort;
        public int remarkSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
        public int sortWeightSort;
    }

}