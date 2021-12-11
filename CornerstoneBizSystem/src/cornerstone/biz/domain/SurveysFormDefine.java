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
 * 问卷调查表单设计
 * 
 * @author 杜展扬 2020-02-02 18:29
 *
 */
@DomainDefine(domainClass = SurveysFormDefine.class)
@DomainDefineValid(comment ="问卷调查表单设计" ,uniqueKeys={@UniqueKey(fields={"surveysId"})})
public class SurveysFormDefine extends BaseDomain{
    //
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @ForeignKey(domainClass=SurveysDefine.class)
    @DomainFieldValid(comment="问卷调查ID",required=true,canUpdate=true)
    public int surveysDefineId;
    
    //这个字段不用
    @DomainFieldValid(comment="标题",required=true,canUpdate=true,maxValue=128)
    public String title;
    
    @DomainFieldValid(comment="组件列表",canUpdate=true)
    public String fieldList;//SurvleysFormField
   
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    public static class SurveysFormField{
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
    public static class SurveysFormDefineInfo extends SurveysFormDefine{
    	//
    	@DomainField(foreignKeyFields = "surveysDefineId",field = "name",persistent = false)
        public String surveysDefineName;

    }
    //
    //   
    @QueryDefine(domainClass=SurveysFormDefineInfo.class)
    public static class SurveysFormDefineQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;

        public Integer surveysDefineId;

        public String title;

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
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int surveysDefineIdSort;
        public int titleSort;
        public int fieldListSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}