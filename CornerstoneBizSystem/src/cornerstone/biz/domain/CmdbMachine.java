package cornerstone.biz.domain;

import java.util.Date;
import java.util.Map;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * cmdb主机
 * 
 * @author 杜展扬 2018-08-22
 *
 */
@DomainDefine(domainClass = CmdbMachine.class)
@DomainDefineValid(comment ="cmdb主机" ,uniqueKeys={@UniqueKey(fields={"uuid"}),@UniqueKey(fields={"companyId","name"})})
public class CmdbMachine extends BaseDomain{
    //
	public static final int LOGINTYPE_SSH = 1;
    public static final int LOGINTYPE_证书SSH = 2;
    public static final int LOGINTYPE_VNC = 3;
    //
    public static final int RUNSTATUS_在线 = 1;
    public static final int RUNSTATUS_离线 = 2;
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @DomainFieldValid(comment="UUID",required=true,canUpdate=true,maxValue=64)
    public String uuid;
    
    @DomainFieldValid(comment="组",canUpdate=true,maxValue=64)
    public String group;
    
    @DomainFieldValid(comment="外网地址",canUpdate=true,maxValue=128)
    public String outerHost;
    
    @DomainFieldValid(comment="内网地址",canUpdate=true,maxValue=128)
    public String innerHost;
    
    @DomainFieldValid(comment="端口",canUpdate=true)
    public int port;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=128)
    public String name;
    
    @DomainFieldValid(comment="用户名",canUpdate=true,maxValue=64)
    public String userName;
    
    @DomainFieldValid(comment="密码",canUpdate=true,maxValue=64)
    public String password;
    
    @DomainFieldValid(comment="公钥",canUpdate=true,maxValue=512)//
    public String publicKey;
    
    @DomainFieldValid(comment="私钥",canUpdate=true,maxValue=1024)//
    public String privateKey;
    
    @DomainFieldValid(comment="登录方式",required=true,canUpdate=true)
    public int loginType;
    
    @DomainFieldValid(comment="备注",canUpdate=true,maxValue=512)
    public String remark;
    
    @DomainFieldValid(comment="是否删除",required=true,canUpdate=true)
    public boolean isDelete;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    @DomainFieldValid(comment="属性",required=true,canUpdate=true)
    public Map<String,String> properties;
    
    @DomainFieldValid(comment="运行状态",canUpdate=true,dataDict="CmdbMachine.runStatus")
    public int runStatus;
    //
    //   
    public static class CmdbMachineInfo extends CmdbMachine{
    //

    }
    //
    //   
    @QueryDefine(domainClass=CmdbMachineInfo.class)
    public static class CmdbMachineQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;

        public String uuid;

        public String group;

        public String outerHost;

        public String innerHost;

        public Integer port;

        public String name;

        public String userName;

        public String password;

        public Integer loginType;

        public String remark;
        
        public Integer runStatus;

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

        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int uuidSort;
        public int groupSort;
        public int outerHostSort;
        public int innerHostSort;
        public int portSort;
        public int nameSort;
        public int userNameSort;
        public int passwordSort;
        public int loginTypeSort;
        public int remarkSort;
        public int runStatusSort;
        public int isDeleteSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}