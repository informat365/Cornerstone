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
 * 项目运行日志
 * 
 * @author 杜展扬 2019-01-23 20:40
 *
 */
@DomainDefine(domainClass = ProjectRunLog.class)
@DomainDefineValid(comment ="项目运行日志" )
public class ProjectRunLog extends BaseDomain{
    //
	 @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
	 
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;
    
    @DomainFieldValid(comment="运行状态",required=true,canUpdate=true)
    public int runStatus;
    
    @DomainFieldValid(comment="进度",required=true,canUpdate=true,minValue=0,maxValue=100)
    public int progress;
    
    @DomainFieldValid(comment="remark",canUpdate=true,maxValue=512)
    public String remark;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    //
    //   
    public static class ProjectRunLogInfo extends ProjectRunLog{
    		//
        @DomainField(foreignKeyFields="createAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
        
        @DomainField(foreignKeyFields="createAccountId",field="imageId",persistent=false)
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;

    }
    //
    //   
    @QueryDefine(domainClass=ProjectRunLogInfo.class)
    public static class ProjectRunLogQuery extends BizQuery{
        //
        public Integer id;
        
        public Integer companyId;

        public Integer projectId;

        public Integer runStatus;

        public Integer progress;

        public String remark;

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
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int projectIdSort;
        public int runStatusSort;
        public int progressSort;
        public int remarkSort;
        public int createAccountIdSort;
        public int createAccountNameSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}