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
 * 分类
 * 
 * @author 杜展扬 2018-08-06
 *
 */
@DomainDefine(domainClass = Category.class)
@DomainDefineValid(comment ="分类" ,uniqueKeys={@UniqueKey(fields={"projectId","objectType","parentId","name"})})
public class Category extends BaseDomain{
    //
	@ForeignKey(domainClass=Company.class)
    public int companyId;
	
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;
    
    @DomainFieldValid(comment="对象类型",required=true,canUpdate=true)
    public int objectType;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=64)
    public String name;
    
    @ForeignKey(domainClass=Category.class)
    @DomainFieldValid(comment="父分类",required=true,canUpdate=true)
    public int parentId;
    
    @DomainFieldValid(comment="层级",required=true,canUpdate=true)
    public int level;
    
    @DomainFieldValid(comment="颜色",canUpdate=true,maxValue=32)
    public String color;
    
    @DomainFieldValid(comment="备注",canUpdate=true,maxValue=512)
    public String remark;
    
    public int sortWeight;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class CategoryInfo extends Category{
    //

    }
    //
    //   
    @QueryDefine(domainClass=CategoryInfo.class)
    public static class CategoryQuery extends BizQuery{
        //
        public Integer id;
        
        public Integer companyId;

        public Integer projectId;

//        public Integer objectType;
        
        @QueryField(whereSql = "(a.object_type=0 or a.object_type=#{objectType})")
        public Integer objectType;

        public String name;
        
        @QueryField(operator="=",field="name")
        public String eqName;

        public Integer parentId;
        
        public Integer level;

        public String color;

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
        @QueryField(field="id",operator="in")
        public int[] idInList;
        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int projectIdSort;
        public int objectTypeSort;
        public int levelSort;
        public int nameSort;
        public int parentIdSort;
        public int colorSort;
        public int remarkSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
        public int sortWeightSort;
    }

}