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
 * 版本
 * 
 * @author 杜展扬 2020-04-16 13:58
 *
 */
@DomainDefine(domainClass = CompanyVersion.class)
@DomainDefineValid(comment ="版本" )
public class CompanyVersion extends BaseDomain{
    //
	public static final int STATUS_未开始 = 1;
    public static final int STATUS_进行中 = 2;
    public static final int STATUS_已发布 = 3;
    public static final int STATUS_延期发布 = 4;
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true)
    public int companyId;
    
    @ForeignKey(domainClass=CompanyVersionRepository.class)
    @DomainFieldValid(comment="版本",required=true)
    public int repositoryId;
    
    @DomainFieldValid(comment="名称",required=true,maxValue=128)
    public String name;
    
    @DomainFieldValid(comment="备注")
    public String remark;
    
    @DomainFieldValid(comment="开始时间")
    public Date startTime;
    
    @DomainFieldValid(comment="发布时间")
    public Date endTime;
    
    @DomainFieldValid(comment="状态",required=true,dataDict="CompanyVersion.status")
    public int status;
    
    @DomainFieldValid(comment="总任务数")
    public int totalTaskNum;
    
    @DomainFieldValid(comment="完成任务数")
    public int finishTaskNum;
    
    @DomainFieldValid(comment="是否删除",required=true)
    public boolean isDelete;

    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true)
    public int updateAccountId;

    //region 通商银行新增字段 2021年7月16日17点16分
    @DomainFieldValid(comment = "版本负责人")
    public List<Integer> ownerAccountIdList;

    @DomainFieldValid(comment = "版本负责人")
    public List<AccountSimpleInfo> ownerAccountList;

    @DomainFieldValid(comment="版本号",required=true)
    public String versionNo;
    // end region


    public static class CompanyVersionInfo extends CompanyVersion{
    //
        @DomainField(foreignKeyFields="repositoryId",field="name",persistent=false)
        @DomainFieldValid(comment = "版本名称")
        public String repositoryName;

        @DomainField(foreignKeyFields="createAccountId",field="imageId",persistent=false)
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;

        @DomainField(foreignKeyFields="createAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
    

    }
    //
    //   
    @QueryDefine(domainClass=CompanyVersionInfo.class)
    public static class CompanyVersionQuery extends BizQuery{
        //
        public Integer id;

        @QueryField(whereSql="json_contains(a.owner_account_id_list,'${ownerAccountId}')")
        public Integer ownerAccountId;

        public Integer companyId;

        public Integer repositoryId;

        public String name;

        public String remark;

        @QueryField(operator=">=",field="startTime")
        public Date startTimeStart;
        
        @QueryField(operator="<=",field="startTime")
        public Date startTimeEnd;

        @QueryField(operator=">=",field="endTime")
        public Date endTimeStart;
        
        @QueryField(operator="<=",field="endTime")
        public Date endTimeEnd;

        public Integer status;

        public Integer totalTaskNum;

        public Integer finishTaskNum;

        public Boolean isDelete;

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
        @QueryField(foreignKeyFields="repositoryId",field="name")
        public String repositoryName;
        
        @QueryField(foreignKeyFields="createAccountId",field="imageId")
        public String createAccountImageId;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int repositoryIdSort;
        public int repositoryNameSort;
        public int nameSort;
        public int remarkSort;
        public int startTimeSort;
        public int endTimeSort;
        public int statusSort;
        public int totalTaskNumSort;
        public int finishTaskNumSort;
        public int isDeleteSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}