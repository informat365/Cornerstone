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
 * 对象类型字段定义
 * 
 * @author 杜展扬 2018-08-23
 *
 */
@DomainDefine(domainClass = ObjectTypeFieldDefine.class)
@DomainDefineValid(comment ="对象类型字段定义" ,uniqueKeys={@UniqueKey(fields={"objectType","name"})})
public class ObjectTypeFieldDefine extends BaseDomain{
    //
    //
    @ForeignKey(domainClass=ObjectType.class)
    @DomainFieldValid(comment="对象类型",required=true,canUpdate=true)
    public int objectType;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=64)
    public String name;
    
    @DomainFieldValid(comment="字段名",canUpdate=true,maxValue=32)
    public String field;
    
    @DomainFieldValid(comment="字段类型",required=true,canUpdate=true)
    public int type;
    
    @DomainFieldValid(comment="是否必须显示",required=true,canUpdate=true)
    public boolean isRequiredShow;
    
    @DomainFieldValid(comment="排序字段",required=true,canUpdate=true)
    public int sortWeight;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class ObjectTypeFieldDefineInfo extends ObjectTypeFieldDefine{
    //

    }
    //
    //   
    @QueryDefine(domainClass=ObjectTypeFieldDefineInfo.class)
    public static class ObjectTypeFieldDefineQuery extends BizQuery{
        //
        public Integer id;

        public Integer objectType;

        public String name;

        public String field;

        public Integer type;

        public Boolean isRequiredShow;

        public Integer sortWeight;

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
        public int objectTypeSort;
        public int nameSort;
        public int fieldSort;
        public int typeSort;
        public int isRequiredShowSort;
        public int sortWeightSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}