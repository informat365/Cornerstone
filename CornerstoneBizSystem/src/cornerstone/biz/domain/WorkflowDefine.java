package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 流程定义
 * 
 * @author 杜展扬 2019-06-21 17:02
 *
 */
@DomainDefine(domainClass = WorkflowDefine.class)
@DomainDefineValid(comment ="流程定义" )
public class WorkflowDefine extends BaseDomain{
    //
    public static final int STATUS_有效 = 1;
    public static final int STATUS_无效 = 2;
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=64)
    public String name;
    
    @DomainFieldValid(comment="备注",canUpdate=true,maxValue=128)
    public String remark;
    
    @DomainFieldValid(comment="状态",required=true,canUpdate=true,dataDict="Common.status")
    public int status;
    
    @DomainFieldValid(comment="分组",canUpdate=true,maxValue=64)
    public String group;
    
    @DomainFieldValid(comment="颜色",canUpdate=true,maxValue=16)
    public String color;
    
    @DomainFieldValid(comment="能否打印",canUpdate=true)
    public boolean enablePrint;
    
    @DomainFieldValid(comment="能否撤回",canUpdate=true)
    public boolean enableCancel;
    
    @DomainFieldValid(comment="能否微信通知",canUpdate=true)
    public boolean enableNotifyWechat;
    
    @DomainFieldValid(comment="能否邮件通知",canUpdate=true)
    public boolean enableNotifyEmail;
    
    @DomainFieldValid(comment="标题字段列表",canUpdate=true,maxValue=512)
    public List<String> titleFormFieldList;
    
    @ForeignKey(domainClass=WorkflowFormDefine.class)
    @DomainFieldValid(comment="表单定义",canUpdate=true)
    public int formDefineId;
    
    @ForeignKey(domainClass=WorkflowChartDefine.class)
    @DomainFieldValid(comment="流转定义",canUpdate=true)
    public int chartDefineId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class WorkflowDefineInfo extends WorkflowDefine{
    //
        @DomainField(foreignKeyFields="createAccountId",field="imageId",persistent=false)
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
    
        @DomainField(foreignKeyFields="createAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
    
        @DomainField(ignoreWhenSelect=true,persistent=false)
        public int copyId;

    }
    //
    //   
    @QueryDefine(domainClass=WorkflowDefineInfo.class)
    public static class WorkflowDefineQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;

        public String name;

        public String remark;

        public Integer status;

        public String group;
        
        public String color;
        
        public Boolean enablePrint;

        public Boolean enableCancel;

        public Boolean enableNotifyWechat;

        public Boolean enableNotifyEmail;

        public String titleFormFieldList;

        public Integer formDefineId;

        public Integer chartDefineId;

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
        public int companyIdSort;
        public int nameSort;
        public int remarkSort;
        public int statusSort;
        public int groupSort;
        public int colorSort;
        public int enablePrintSort;
        public int enableCancelSort;
        public int enableNotifyWechatSort;
        public int enableNotifyEmailSort;
        public int titleFormFieldListSort;
        public int formDefineIdSort;
        public int chartDefineIdSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}