package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
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
 * MYSQL代理
 * 
 * @author 杜展扬 2019-01-02 22:03
 *
 */
@DomainDefine(domainClass = DesignerMysqlProxyInstance.class)
@DomainDefineValid(comment ="账号" ,uniqueKeys=
{@UniqueKey(fields={"proxyPort"})})
public class DesignerMysqlProxyInstance extends BaseDomain{
    //
	@ForeignKey(domainClass=Company.class)
	@DomainFieldValid(comment="企业",required=true)
    public int companyId;
	
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=64,needTrim=true)
    public String name;
    
    @DomainFieldValid(comment="代理端口",required=true,canUpdate=true)
    public int proxyPort;
    
    @DomainFieldValid(comment="主机",required=true,canUpdate=true,maxValue=256,needTrim=true)
    public String host;
    
    @DomainFieldValid(comment="端口",required=true,canUpdate=true)
    public int port;
    
    @DomainFieldValid(comment="数据库用户名",required=true,canUpdate=true,maxValue=64,needTrim=true)
    public String dbUser;
    
    @DomainFieldValid(comment="数据库密码",required=true,canUpdate=true,maxValue=64,needTrim=true)
    public String dbPassword;
    
    @DomainFieldValid(comment="角色列表",canUpdate=true,maxValue=512)
    public List<Integer> roles;
    
    @DomainFieldValid(comment="成员列表",canUpdate=true,maxValue=512,needTrim=true)
    public String members;
    
    @DomainFieldValid(comment="备注",canUpdate=true,maxValue=512,needTrim=true)
    public String remark;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class DesignerMysqlProxyInstanceInfo extends DesignerMysqlProxyInstance{
    //
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
    @QueryDefine(domainClass=DesignerMysqlProxyInstanceInfo.class)
    public static class DesignerMysqlProxyInstanceQuery extends BizQuery{
        //
        public Integer id;

        public String name;

        public Integer proxyPort;

        public String host;

        public Integer port;

        public String dbUser;

        public String dbPassword;

        public String roles;

        public String members;

        public String remark;

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
        public int nameSort;
        public int proxyPortSort;
        public int hostSort;
        public int portSort;
        public int dbUserSort;
        public int dbPasswordSort;
        public int rolesSort;
        public int membersSort;
        public int remarkSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}