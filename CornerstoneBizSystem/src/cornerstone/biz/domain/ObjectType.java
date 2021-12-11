package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

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
 * 对象类型
 * 
 * @author 杜展扬 2018-08-23
 *
 */
@DomainDefine(domainClass = ObjectType.class)
@DomainDefineValid(comment ="对象类型" ,uniqueKeys={@UniqueKey(fields={"name"})})
public class ObjectType extends BaseDomain{
    //
	public static final String SYSTEM_NAME_任务="TAS";
	public static final String SYSTEM_NAME_缺陷="BUG";
	public static final String SYSTEM_NAME_需求="STO";
	public static final String SYSTEM_NAME_测试计划="TPL";
	public static final String SYSTEM_NAME_测试用例="TCA";
	public static final String SYSTEM_NAME_销售漏斗="SAL";
	public static final String SYSTEM_NAME_待办事项="SCH";
	public static final String SYSTEM_NAME_Issue="ISS";
    public static final String SYSTEM_NAME_项目清单="PRO";
    public static final String SYSTEM_NAME_项目督办="POS";
    
    @DomainFieldValid(comment="名称",needTrim=true,required=true,canUpdate=true,maxValue=64)
    public String name;
    
    @DomainFieldValid(comment="系统名称")
    public String systemName;
    
    @DomainFieldValid(comment="分组",needTrim=true,required=true,canUpdate=true,maxValue=64)
    public String group;
    
    @DomainFieldValid(comment="是否系统默认")
    public boolean isSystemDefault;
    
    @DomainFieldValid(comment="是否使用默认系统ID")
    public boolean useDefaultSerialNo;
    
    @DomainFieldValid(comment="备注",canUpdate=true,maxValue=512)
    public String remark;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class ObjectTypeInfo extends ObjectType{
    		
    		@DomainField(persistent=false,ignoreWhenSelect=true)
    		public List<ObjectTypeFieldDefine> fieldDefineList;
    }
    //
    //   
    @QueryDefine(domainClass=ObjectTypeInfo.class)
    public static class ObjectTypeQuery extends BizQuery{
        //
        public Integer id;
        
        @QueryField(field = "uuid",operator = "not in")
        public String[] excludeUuid;

        public String name;

        public String group;

        public String remark;
        
        public Boolean isSystemDefault;
        
        public Boolean useDefaultSerialNo;

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
        public int nameSort;
        public int groupSort;
        public int remarkSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}