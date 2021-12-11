package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.*;

import java.util.Date;
import java.util.List;
/**
 * 企业
 * 
 * @author 杜展扬 2018-07-28
 *
 */
@DomainDefine(domainClass = Company.class)
@DomainDefineValid(comment ="企业" )
public class Company extends BaseDomain{
    //
	public static final int VERSION_云平台版 = 1;
	public static final int VERSION_私有部署版 = 2;
	//
	public static final int STATUS_许可中 = 1;
	public static final int STATUS_已到期 = 2;
	public static final int STATUS_未授权 = 3;
    //
    @DomainFieldValid(comment="UUID",required=true,canUpdate=true,maxValue=64)
    public String uuid;
    
    @DomainFieldValid(comment="LOGO",canUpdate=true,maxValue=64)
    public String imageId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=128)
    public String name;
    
    @DomainFieldValid(comment="简介",canUpdate=true,maxValue=512)
    public String remark;
    
    @DomainFieldValid(comment="公告",canUpdate=true,maxValue=60*1024)
    public String announcement;
    
    @DomainFieldValid(comment="许可证版本号",canUpdate=true,dataDict="Company.version")
    public int version;
    
    @DomainFieldValid(comment="许可到期时间",canUpdate=true)
    public Date dueDate;
    
    @DomainFieldValid(comment="最大成员数量",canUpdate=true)
    public int maxMemberNum;
    
    @DomainFieldValid(comment="当前成员数量",canUpdate=true)
    public int memberNum;
    
    @DomainFieldValid(comment="状态",canUpdate=true,dataDict="Company.status")
    public int status;
    
    @DomainFieldValid(comment="许可证",canUpdate=true,maxValue=1024*60)
    public String license;
    
    @DomainFieldValid(comment="许可证ID",canUpdate=true,maxValue=128)
    public String licenseId;
    
    @DomainFieldValid(comment="许可模块",canUpdate=true,maxValue=1024*60)
    public List<String> moduleList;
    
    @DomainFieldValid(comment="是否被删除",required=true,canUpdate=true)
    public boolean isDelete;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;

    @DomainFieldValid(comment="最后登录时间",canUpdate=true)
    public Date lastLoginTime;
    //
    //   
    public static class CompanyInfo extends Company{
    //
    		@NonPersistent
		@DomainField(foreignKeyFields = "createAccountId", field = "imageId")
		@DomainFieldValid(comment = "创建人头像")
		public String createAccountImageId;

		@NonPersistent
		@DomainField(foreignKeyFields = "createAccountId", field = "name")
		@DomainFieldValid(comment = "创建人名称")
		public String createAccountName;
		
		@NonPersistent
		@DomainField(foreignKeyFields = "createAccountId", field = "mobileNo")
		@DomainFieldValid(comment = "创建人手机号")
		public String  createAccountMobileNo;

		@NonPersistent
		@DomainField(foreignKeyFields = "createAccountId", field = "registerIp")
		@DomainFieldValid(comment = "注册ip")
		public String  registerIp;
    }
    //
    //   
    @QueryDefine(domainClass=CompanyInfo.class)
    public static class CompanyQuery extends BizQuery{
        //
        public Integer id;

        public String uuid;

        public String imageId;

        public String name;
        
        @QueryField(field = "name",operator = "=")
        public String eqName;

        public String remark;
        
        public Integer version;

        @QueryField(operator=">=",field="dueDate")
        public Date dueDateStart;
        
        @QueryField(operator="<=",field="dueDate")
        public Date dueDateEnd;

        public Integer maxMemberNum;

        public Integer memberNum;

        public Integer status;
        
        public String license;
        
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
        //sort
        public int idSort;
        public int uuidSort;
        public int imageIdSort;
        public int nameSort;
        public int remarkSort;
        public int versionSort;
        public int dueDateSort;
        public int maxMemberNumSort;
        public int memberNumSort;
        public int statusSort;
        public int isDeleteSort;
        public int createAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
        //
        @InnerJoin(table2=CompanyMember.class,table1Field="id",table2Field="company_id")
        public Integer accountId;
    }

}