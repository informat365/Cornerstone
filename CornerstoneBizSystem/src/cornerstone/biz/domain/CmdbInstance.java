package cornerstone.biz.domain;

import java.util.Date;
import java.util.Map;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.NonPersistent;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 实例
 * 
 * @author 杜展扬 2018-08-21
 *
 */
@DomainDefine(domainClass = CmdbInstance.class)
@DomainDefineValid(comment ="实例" ,uniqueKeys={@UniqueKey(fields={"companyId","name"})})
public class CmdbInstance extends BaseDomain{
	//
	public static final int RUNSTATUS_在线 = 1;
    public static final int RUNSTATUS_离线 = 2;
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @ForeignKey(domainClass=CmdbApplication.class)
    @DomainFieldValid(comment="app",required=true,canUpdate=true)
    public int applicationId;
    
    @ForeignKey(domainClass=CmdbMachine.class)
    @DomainFieldValid(comment="主机",required=true,canUpdate=true)
    public int machineId;
    
    @DomainFieldValid(comment="名称",canUpdate=true,maxValue=64)
    public String name;
    
    @DomainFieldValid(comment="端口",required=true,canUpdate=true)
    public int port;
    
    @DomainFieldValid(comment="优先级",required=true,canUpdate=true)
    public int priority;
    
    @DomainFieldValid(comment="版本",canUpdate=true,maxValue=32)
    public String packageVersion;
    
    @DomainFieldValid(comment="备注",canUpdate=true,maxValue=512)
    public String remark;
    
    @DomainFieldValid(comment="分组")
    public String group;
    
    @DomainFieldValid(comment="属性",required=true,canUpdate=true)
    public Map<String,String> properties;
    
    @DomainFieldValid(comment="运行状态")
    public int runStatus;
    
    @DomainFieldValid(comment="用户名")
    public String user;
    
    @DomainFieldValid(comment="密码")//通过KEY加密
    public String password;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class CmdbInstanceInfo extends CmdbInstance{
    //
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="imageId")
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
    
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="name")
        @DomainFieldValid(comment = "创建人名称")
        public String accountName;
        
        @NonPersistent
        @DomainField(foreignKeyFields="applicationId",field="name")
        @DomainFieldValid(comment = "app名称")
        public String applicationName;
    
        @NonPersistent
        @DomainField(foreignKeyFields="machineId",field="name")
        @DomainFieldValid(comment = "机器名称")
        public String machineName;

    }
    //
    //   
    @QueryDefine(domainClass=CmdbInstanceInfo.class)
    public static class CmdbInstanceQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;

        public Integer applicationId;

        public Integer machineId;

        public String name;

        public Integer port;

        public Integer priority;

        public String packageVersion;

        public String remark;
        
        public String group;

        public Integer createAccountId;

        public Integer updateAccountId;
        
        @QueryField(foreignKeyFields="machineId",field="name")
        public String machineName;

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
        public String createAccountImageId;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String accountName;
        
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int applicationIdSort;
        public int machineIdSort;
        public int nameSort;
        public int portSort;
        public int prioritySort;
        public int packageVersionSort;
        public int remarkSort;
        public int groupSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int accountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}