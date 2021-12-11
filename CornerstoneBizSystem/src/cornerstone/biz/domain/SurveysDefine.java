package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.InnerJoin;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 问卷调查
 * 
 * 
 * @author 杜展扬 2020-02-02 18:29
 *
 */
@DomainDefine(domainClass = SurveysDefine.class)
@DomainDefineValid(comment ="问卷调查" )
public class SurveysDefine extends BaseDomain{
    //
	public static final int STATUS_有效=1;
	public static final int STATUS_无效=2;
    //
	/***/
	public String uuid;
	
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @DomainFieldValid(comment="标题",required=true,canUpdate=true,maxValue=60)
    public String name;
    
    @DomainFieldValid(comment="允许匿名",canUpdate=true)
    public boolean anonymous;
    
    @DomainFieldValid(comment="允许重新编辑",canUpdate=true)
    public boolean submitEdit;
    
    @DomainFieldValid(comment="开始时间",canUpdate=true)
    public Date startTime;
    
    @DomainFieldValid(comment="结束时间",canUpdate=true)
    public Date endTime;
    
    @DomainFieldValid(comment="状态",canUpdate=true)
    public int status;
    
    @DomainFieldValid(comment="备注",canUpdate=true,maxValue=128)
    public String remark;
    
    @ForeignKey(domainClass=SurveysFormDefine.class)
    @DomainFieldValid(comment="表单定义",canUpdate=true)
    public int formDefineId;
    
    public boolean isDelete;
    
    //发起权限
    @DomainFieldValid(comment="成员列表",canUpdate=true)
    public List<PermissionRole> accountList;
    
    @DomainFieldValid(comment="企业角色列表",canUpdate=true)
    public List<PermissionRole> companyRoleList;
    
    @DomainFieldValid(comment="项目角色列表",canUpdate=true)
    public List<ProjectPermissionRole> projectRoleList;
    
    @DomainFieldValid(comment="部门列表",canUpdate=true)
    public List<PermissionRole> departmentList;
    
    public int submitCount;
    
    public Date lastSubmitTime;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    public static class PermissionRole{
    	public int id;
    	public String name;
    }
    //
    public static class ProjectPermissionRole{
		public String id;//projectid-roleid
		public String name;
    }
    //   
    public static class SurveysDefineInfo extends SurveysDefine{
    	//
    	@DomainField(foreignKeyFields="createAccountId",field="imageId",persistent=false)
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
    
        @DomainField(foreignKeyFields="createAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
        
        /**实例状态 是否填写*/
        @DomainField(ignoreWhenSelect = true,persistent=false)
        public int instanceStatus;
    }
    //   
    @QueryDefine(domainClass=SurveysDefineInfo.class)
    public static class SurveysDefineQuery extends BizQuery{
        //
    	public static final int TYPE_我待填写=1;
    	public static final int TYPE_我填写过=2;
    	public static final int TYPE_我发起的=3;
    	//
    	@QueryField(ingore=true)
    	public Integer type;//1.我待填写的 2.我填写过的 3.我发起的
    	
        public Integer id;

        public Integer companyId;

        public String name;

        public Boolean anonymous;
        
        public Boolean submitEdit;

        @QueryField(operator=">=",field="startTime")
        public Date startTimeStart;
        
        @QueryField(operator="<=",field="startTime")
        public Date startTimeEnd;

        @QueryField(operator=">=",field="endTime")
        public Date endTimeStart;
        
        @QueryField(operator="<=",field="endTime")
        public Date endTimeEnd;

        public Integer status;
        
        public String remark;

        public Integer formDefineId;
        
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
        //填写人id
        @InnerJoin(table2 =SurveysInstance.class,table2Field="surveysDefineId")
        @QueryField(field = "createAccountId")
        public Integer fillAccountId;
        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int nameSort;
        public int anonymousSort;
        public int startTimeSort;
        public int endTimeSort;
        public int statusSort;
        public int remarkSort;
        public int formDefineIdSort;
        public int submitCountSort;
        public int lastSubmitTimeSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}