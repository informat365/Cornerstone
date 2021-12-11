package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * cmdb robot
 * 
 * @author 杜展扬 2018-09-05 16:50
 *
 */
@DomainDefine(domainClass = CmdbRobot.class)
@DomainDefineValid(comment ="cmdb robot" ,uniqueKeys={
		@UniqueKey(fields={"companyId","name"}),
		@UniqueKey(fields={"uuid"})})
public class CmdbRobot extends BaseDomain{
    //
    //
	public String uuid;
	
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=64)
    public String name;
    
    @DomainFieldValid(comment="备注",canUpdate=true,maxValue=512)
    public String remark;
    
    @DomainFieldValid(comment="代码",canUpdate=true)
    public String code;
    
    @DomainFieldValid(comment="定时脚本",canUpdate=true,maxValue=256)
    public String cron;
    
    @DomainFieldValid(comment="定时跑的主机列表",canUpdate=true)
    public List<Integer> machineList;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    @DomainFieldValid(comment="上次执行时间")//TODO
    public Date lastRunTime;
    
    @DomainFieldValid(comment="上次执行主机")
    public String lastRunMachine;
    
    @DomainFieldValid(comment="下次自动跑的时间")
    public Date nextRunTime;
    
    @DomainFieldValid(comment="执行次数")
    public int runCount;
    //
    //   
    public static class CmdbRobotInfo extends CmdbRobot{
    //

    }
    //
    //   
    @QueryDefine(domainClass=CmdbRobotInfo.class)
    public static class CmdbRobotQuery extends BizQuery{
        //
        public Integer id;
        
        public String uuid;

        public Integer companyId;

        public String name;

        public String remark;

        public String code;

        public String cron;

        public String machineList;

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
        public int nameSort;
        public int remarkSort;
        public int codeSort;
        public int cronSort;
        public int machineListSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}