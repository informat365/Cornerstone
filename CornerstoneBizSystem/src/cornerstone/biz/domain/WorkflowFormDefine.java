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
 * 
 * 
 * @author 杜展扬 2019-06-21 17:07
 *
 */
@DomainDefine(domainClass = WorkflowFormDefine.class)
@DomainDefineValid(comment ="表单定义" )
public class WorkflowFormDefine extends BaseDomain{
    //
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @ForeignKey(domainClass=WorkflowDefine.class)
    @DomainFieldValid(comment="流程",required=true,canUpdate=true)
    public int workflowDefineId;
    
    @DomainFieldValid(comment="标题",required=true,canUpdate=true,maxValue=128)
    public String title;
    
    @DomainFieldValid(comment="副标题",canUpdate=true,maxValue=128)
    public String subTitle;
    
    @DomainFieldValid(comment="组件列表",canUpdate=true)
    public String fieldList;//FormField.java List<FormField>
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    //
    public static class FormFieldObjectValue{
    		public String id;
    		public String name;
    }
    //
    public static class FormField{
		//
		public static final String TYPE_TEXT_SINGLE="text-single";
		public static final String TYPE_TEXT_AREA="text-area";
		public static final String TYPE_TEXT_RICH="text-rich";
		public static final String TYPE_TEXT_NUMBER="text-number";//数字
		public static final String TYPE_SYSTEM_VALUE="system-value";
		public static final String TYPE_DATE="date";
		public static final String TYPE_TIME="time";
		public static final String TYPE_SELECT="select";//下拉框
		public static final String TYPE_RADIO="radio";//单选框
		public static final String TYPE_CHECKBOX="checkbox";//复选框
		public static final String TYPE_ATTACHMENT="attachment";//附件 FormFieldObjectValue
		public static final String TYPE_USER_SELECT="user-select";//FormFieldObjectValue
		public static final String TYPE_DEPARTMENT_SELECT="department-select";//FormFieldObjectValue
		public static final String TYPE_ROLE_COMPANY_SELECT="role-company-select";//FormFieldObjectValue
		public static final String TYPE_ROLE_PROJECT_SELECT="role-project-select";//FormFieldObjectValue
		public static final String TYPE_TABLE="table";
		public static final String TYPE_STATIC_GROUP="static-group";
		public static final String TYPE_STATIC_LABEL="static-label";//静态文本
		//
		public String id;//":"e7c1e7ef-7d6d-4693-abdd-de45d7e86d23",
		public String name;//":"单行文本",
		public String type;//text-single,text-number,text-area,attachment'||field.type=='user-select'||field.type=='department-select'||field.type=='role-company-select'||field.type=='role-project-select'">
		public Integer minLength;//":1,
		public Integer maxLength;//":200,
		public Boolean required;//":true
    }
    //   
    public static class WorkflowFormDefineInfo extends WorkflowFormDefine{
    //
        @DomainField(foreignKeyFields="createAccountId",field="imageId",persistent=false)
        @DomainFieldValid(comment = "创建人头像")
        public String createAccuntImageId;
    
        @DomainField(foreignKeyFields="createAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
    

    }
    //
    //   
    @QueryDefine(domainClass=WorkflowFormDefineInfo.class)
    public static class WorkflowFormDefineQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;
        
        public Integer workflowDefineId;

        public String title;

        public String subTitle;

        public String fieldList;

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
        @QueryField(foreignKeyFields="createAccountId",field="imageId")
        public String createAccuntImageId;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int titleSort;
        public int subTitleSort;
        public int fieldListSort;
        public int createAccountIdSort;
        public int createAccuntImageIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}