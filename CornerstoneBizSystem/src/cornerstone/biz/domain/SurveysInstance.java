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
 * 问卷调查实例
 * 
 * @author 杜展扬 2020-02-02 19:36
 *
 */
@DomainDefine(domainClass = SurveysInstance.class)
@DomainDefineValid(comment ="问卷调查实例" ,uniqueKeys={@UniqueKey(fields={"surveysId","createAccountId"}),@UniqueKey(fields={"uuid"})})
public class SurveysInstance extends BaseDomain{
    //
    public static final int STATUS_待填写 = 1;//暂存
    public static final int STATUS_已填写 = 2;
    //
    @DomainFieldValid(comment="UUID",required=true,canUpdate=true,maxValue=80)
    public String uuid;
    
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @ForeignKey(domainClass=SurveysDefine.class)
    @DomainFieldValid(comment="问卷调查",required=true,canUpdate=true)
    public int surveysDefineId;
    
    public String accountUuid;
    
    @ForeignKey(domainClass=SurveysFormDefine.class)
    @DomainFieldValid(comment="表单定义",required=true,canUpdate=true)
    public int surveysFormDefineId;
    
    @DomainFieldValid(comment="状态",canUpdate=true,dataDict="SurveysInstance.status")
    public int status;
    
    //这个字段别用
    @DomainFieldValid(comment="标题",required=true,canUpdate=true,maxValue=128)
    public String title;
    
    @DomainFieldValid(comment="组件列表",canUpdate=true)
    public String fieldList;
    
    @DomainFieldValid(comment="表单数据",canUpdate=true)
    public String formData;
    
    @DomainFieldValid(comment="备注",canUpdate=true,maxValue=128)
    public String remark;
    
    @DomainFieldValid(comment="userAgent",canUpdate=true,maxValue=512)
    public String userAgent;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class SurveysInstanceInfo extends SurveysInstance{
    //
        @DomainField(foreignKeyFields="createAccountId",field="imageId",persistent=false)
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
    
        @DomainField(foreignKeyFields="createAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
    

    }
    //
    //   
    @QueryDefine(domainClass=SurveysInstanceInfo.class)
    public static class SurveysInstanceQuery extends BizQuery{
        //
        public Integer id;

        public String uuid;

        public Integer companyId;

        public Integer surveysDefineId;

        public Integer surveysFormDefineId;

        public String title;

        public Integer status;

        public String formData;

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
        @QueryField(operator="in",field="status")
        public int[] statusInList;
        
        @QueryField(operator="not in",field="status")
        public int[] statusNotInList;
        

        //ForeignQueryFields
        @QueryField(foreignKeyFields="createAccountId",field="imageId")
        public String createAccountImageId;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int uuidSort;
        public int companyIdSort;
        public int surveysDefineIdSort;
        public int surveysFormDefineIdSort;
        public int titleSort;
        public int statusSort;
        public int formDataSort;
        public int remarkSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}