package cornerstone.biz.domain;

import java.util.Date;

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
 * 机器登录会话
 * 
 * @author 杜展扬 2018-08-03
 *
 */
@DomainDefine(domainClass = MachineLoginSession.class)
@DomainDefineValid(comment ="机器登录会话" )
public class MachineLoginSession extends BaseDomain{
    //
    //
    @DomainFieldValid(comment="登录类型",required=true,canUpdate=true)
    public int loginType;
    
    @ForeignKey(domainClass=Machine.class)
    @DomainFieldValid(comment="主机",required=true,canUpdate=true)
    public int machineId;
    
    @ForeignKey(domainClass=CmdbMachine.class)
    @DomainFieldValid(comment="CMDB主机",required=true,canUpdate=true)
    public int cmdbMachineId;
    
    @ForeignKey(domainClass=CmdbRobot.class)
    public int cmdbRobotId;
    
    @DomainFieldValid(comment="备注",canUpdate=true,maxValue=512)
    public String remark;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    //
    //   
    public static class MachineLoginSessionInfo extends MachineLoginSession{
    //
        @NonPersistent
        @DomainField(foreignKeyFields="machineId",field="name")
        @DomainFieldValid(comment = "机器名称")
        public String machineName;
    
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
    @QueryDefine(domainClass=MachineLoginSessionInfo.class)
    public static class MachineLoginSessionQuery extends BizQuery{
        //
        public Integer id;

        public Integer loginType;

        public Integer machineId;

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
        @QueryField(foreignKeyFields="machineId",field="name")
        public String machineName;
        
        @QueryField(foreignKeyFields="createAccountId",field="imageId")
        public String createAccountImageId;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int loginTypeSort;
        public int machineIdSort;
        public int remarkSort;
        public int machineNameSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int createAccountNameSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}