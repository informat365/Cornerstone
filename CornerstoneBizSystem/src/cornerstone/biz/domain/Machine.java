package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

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
 * Devops主机
 * 
 * @author 杜展扬 2018-08-03
 *
 */
@DomainDefine(domainClass = Machine.class)
@DomainDefineValid(comment ="主机" ,uniqueKeys={@UniqueKey(fields={"uuid"})})
public class Machine extends BaseDomain{
    //
    public static final int LOGINTYPE_普通登录 = 1;
    public static final int LOGINTYPE_证书登录 = 2;
    public static final int LOGINTYPE_VNC登录 = 3;
    //
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;
    
    @ForeignKey(domainClass=CmdbMachine.class)
    public int cmdbMachineId;
    
    @DomainFieldValid(comment="UUID",required=true,canUpdate=true)
    public String uuid;
    
    @DomainFieldValid(comment="host",required=true,canUpdate=true,maxValue=128)
    public String host;
    
    @DomainFieldValid(comment="端口",canUpdate=true)
    public int port;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=128)
    public String name;
    
    @DomainFieldValid(comment="用户名",required=true,canUpdate=true,maxValue=64)
    public String userName;
    
    @DomainFieldValid(comment="密码",canUpdate=true,maxValue=64)//经过KEY 3DES加密
    public String password;
    
    @DomainFieldValid(comment="公钥",canUpdate=true,maxValue=512)//
    public String publicKey;
    
    @DomainFieldValid(comment="私钥",canUpdate=true,maxValue=1024)//
    public String privateKey;
    
    @DomainFieldValid(comment="登录方式",canUpdate=true,dataDict="Machine.loginType")
    public int loginType;
    
    @DomainFieldValid(comment="授权角色",canUpdate=true,maxValue=512)
    public List<Integer> roles;
    
    @DomainFieldValid(comment="是否启用授权角色",canUpdate=true,maxValue=512)//如果不启用就是全部人都看得到
    public boolean enableRole;
    
    @DomainFieldValid(comment="备注",canUpdate=true,maxValue=512)
    public String remark;
    
    @DomainFieldValid(comment="是否删除",canUpdate=true)
    public boolean isDelete;
    
    @DomainFieldValid(comment="原始名称，用于删除后恢复",required=false,canUpdate=true,maxValue=128)
    public String originalName;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    public boolean readonlyMode;
    
    @DomainFieldValid(comment="命令",canUpdate=true,maxValue=512)
    public String cmd;
    
    @DomainFieldValid(comment="分组",canUpdate=true,maxValue=64)
    public String group;
    //
    //   
    public static class MachineInfo extends Machine{
    //
        @NonPersistent
        @DomainField(foreignKeyFields="projectId",field="name")
        @DomainFieldValid(comment = "项目名称")
        public String projectName;
    
        @NonPersistent
        @DomainField(foreignKeyFields="projectId",field="companyId")
        @DomainFieldValid(comment = "项目企业")
        public int companyId;
    
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="imageId")
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
    
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="name")
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
    

    }
    //
    //   
    @QueryDefine(domainClass=MachineInfo.class)
    public static class MachineQuery extends BizQuery{
        //
        public Integer id;

        public Integer projectId;

        public String uuid;
        
        public String host;
       
        public Integer port;

        public String name;

        public String userName;

        public String password;

        public Integer loginType;

        public String roles;
        
        public Boolean enableRole;

        public String remark;
        
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
        @QueryField(operator="in",field="loginType")
        public int[] loginTypeInList;
        
        @QueryField(operator="not in",field="loginType")
        public int[] loginTypeNotInList;
        

        //ForeignQueryFields
        @QueryField(foreignKeyFields="projectId",field="name")
        public String projectName;
        
        @QueryField(foreignKeyFields="projectId",field="companyId")
        public Integer companyId;
        
        @QueryField(foreignKeyFields="createAccountId",field="imageId")
        public String createAccountImageId;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int projectIdSort;
        public int projectNameSort;
        public int companyIdSort;
        public int uuidSort;
        public int hostSort;
        public int portSort;
        public int nameSort;
        public int userNameSort;
        public int passwordSort;
        public int loginTypeSort;
        public int rolesSort;
        public int remarkSort;
        public int isDeleteSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}