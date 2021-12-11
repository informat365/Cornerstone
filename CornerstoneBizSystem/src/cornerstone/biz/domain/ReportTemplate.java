package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.NonPersistent;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 汇报模板
 * 
 * @author 杜展扬 2019-01-14 17:07
 *
 */
@DomainDefine(domainClass = ReportTemplate.class)
@DomainDefineValid(comment ="汇报模板" )
public class ReportTemplate extends BaseDomain{
    //
	public static final int STATUS_有效 = 1;
    public static final int STATUS_无效 = 2;
    public static final int PERIOD_日 = 1;
    public static final int PERIOD_周 = 2;
    public static final int PERIOD_月 = 3;
    
    @ForeignKey(domainClass=Company.class)
    public int companyId;
    
    @DomainFieldValid(comment="状态",canUpdate=true,dataDict="ReportTemplate.status")
    public int status;
    //
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=128,needTrim=true)
    public String name;
    
    @DomainFieldValid(comment="周期",canUpdate=true,dataDict="ReportTemplate.period")
    public int period;
    
    @DomainFieldValid(comment="周期",required=true,canUpdate=true)
    public List<Long> periodSetting;
    
    @DomainFieldValid(comment="提醒时间",canUpdate=true)
    public String remindTime;
    
    @DomainFieldValid(comment="报告时间",canUpdate=true)
    public String reportTime;
    
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;
    
    @DomainFieldValid(comment="提交人",canUpdate=true)
    public List<Integer> submitterIds;
    
    @DomainFieldValid(comment="提交人",canUpdate=true)
    public List<AccountSimpleInfo> submitterList;
    
    @DomainFieldValid(comment="审核人",canUpdate=true)
    public List<Integer> auditorIds;
    
    @DomainFieldValid(comment="审核人",canUpdate=true)
    public List<AccountSimpleInfo> auditorList;
    
    @DomainFieldValid(comment="汇报内容",canUpdate=true)
    public List<ReportTemplateContent> content;
    
    @DomainFieldValid(comment="下一次提醒时间",canUpdate=true)
    public Date nextRemindTime;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class ReportTemplateInfo extends ReportTemplate{
    //
        @NonPersistent
        @DomainField(foreignKeyFields="projectId",field="name")
        @DomainFieldValid(comment = "项目名称")
        public String projectName;
    
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="name")
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
    

    }
    //
    //   
    @QueryDefine(domainClass=ReportTemplateInfo.class)
    public static class ReportTemplateQuery extends BizQuery{
        //
        public Integer id;
        
        public Integer companyId;
        
        public Integer status;

        public String name;

        public Integer period;

        @QueryField(operator=">=",field="remindTime")
        public Date remindTimeStart;
        
        @QueryField(operator="<=",field="remindTime")
        public Date remindTimeEnd;

        public Integer projectId;

        public String submitterIds;

        public String submitterList;

        public String auditorIds;

        public String auditorList;

        public String content;

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
        @QueryField(operator="in",field="period")
        public int[] periodInList;
        
        @QueryField(operator="not in",field="period")
        public int[] periodNotInList;
        

        //ForeignQueryFields
        @QueryField(foreignKeyFields="projectId",field="name")
        public String projectName;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int statusSort;
        public int nameSort;
        public int periodSort;
        public int remindTimeSort;
        public int projectIdSort;
        public int projectNameSort;
        public int submitterIdsSort;
        public int submitterListSort;
        public int auditorIdsSort;
        public int auditorListSort;
        public int contentSort;
        public int createAccountIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}