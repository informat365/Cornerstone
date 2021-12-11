package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
/**
 * 用户通知配置
 * 
 * @author 杜展扬 2018-09-05 20:20
 *
 */
@DomainDefine(domainClass = AccountNotificationSetting.class)
@DomainDefineValid(comment ="用户通知配置" ,uniqueKeys={@UniqueKey(fields={"accountId","type"})})
public class AccountNotificationSetting extends BaseDomain{
	//
	public static final int TYPE_日历提醒 = 1;
	public static final int TYPE_对象属性变更= 2;
	public static final int TYPE_对象指派 = 3;
	public static final int TYPE_对象评论=4;
	public static final int TYPE_在评论中被提醒=5;
	public static final int TYPE_对象删除=6;
	public static final int TYPE_对象提交代码=7;
	public static final int TYPE_对象新增子对象=8;
	public static final int TYPE_对象关联其它对象=9;
	public static final int TYPE_对象被其它对象关联=90;
	public static final int TYPE_对象上传附件=10;
	public static final int TYPE_对象删除子对象=11;
	public static final int TYPE_对象取消关联其它对象=12;
	public static final int TYPE_其它对象取消对象关联=120;
	public static final int TYPE_对象删除附件=13;
	public static final int TYPE_对象提醒=14;
	public static final int TYPE_对象关联WIKI=15;
	public static final int TYPE_对象变更审批=16;
    public static final int TYPE_对象变更申请结果=17;
    public static List<Integer> taskTypes=Arrays.asList(
			TYPE_对象属性变更,TYPE_对象指派,TYPE_对象评论,TYPE_在评论中被提醒,TYPE_对象删除,TYPE_对象提交代码,
			TYPE_对象新增子对象,TYPE_对象关联其它对象,TYPE_对象上传附件,TYPE_对象删除子对象,TYPE_对象取消关联其它对象,
			TYPE_对象删除附件,TYPE_对象提醒,TYPE_对象关联WIKI,TYPE_其它对象取消对象关联);
    //
	//
	public static final int TYPE_发表讨论=51;
	public static final int TYPE_PIPELINE提醒=52;
	//
	public static final int TYPE_个人已超期任务提醒=101;
	public static final int TYPE_系统通知提醒=102;
    public static final int TYPE_个人即将到期任务提醒=103;
    //
	public static final int TYPE_流程提醒=201;
	public static final int TYPE_流程沟通=202;

	public static final int TYPE_待填写汇报=301;
	public static final int TYPE_待审核汇报=302;
	//注意：需要修改数据字典
	
	
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="accountId",required=true,canUpdate=true)
    public int accountId;
    
    @DomainFieldValid(comment="type",canUpdate=true,dataDict="AccountNotificationSetting.type")
    public int type;
    
    @DomainFieldValid(comment="isWebEnable",required=true,canUpdate=true)
    public boolean isWebEnable;
    
    @DomainFieldValid(comment="isWeixinEnable",required=true,canUpdate=true)
    public boolean isWeixinEnable;
    
    @DomainFieldValid(comment="isEmailEnable",required=true,canUpdate=true)
    public boolean isEmailEnable;
    
    @DomainFieldValid(comment="isLarkEnable",required=true,canUpdate=true)
    public boolean isLarkEnable;
    
    @DomainFieldValid(comment="isDingtalkEnable",required=true,canUpdate=true)
    public boolean isDingtalkEnable;

    //
    //   
    public static class AccountNotificationSettingInfo extends AccountNotificationSetting{
    //

    }
    //
    //   
    @QueryDefine(domainClass=AccountNotificationSettingInfo.class)
    public static class AccountNotificationSettingQuery extends BizQuery{
        //
        public Integer id;

        public Integer accountId;

        public Integer type;

        public Boolean isWebEnable;

        public Boolean isWeixinEnable;

        public Boolean isEmailEnable;
        
        public Boolean isLarkEnable;

        @QueryField(operator=">=",field="createTime")
        public Date createTimeStart;
        
        @QueryField(operator="<=",field="createTime")
        public Date createTimeEnd;

        @QueryField(operator=">=",field="updateTime")
        public Date updateTimeStart;
        
        @QueryField(operator="<=",field="updateTime")
        public Date updateTimeEnd;

        //in or not in fields
        @QueryField(operator="in",field="type")
        public int[] typeInList;
        
        @QueryField(operator="not in",field="type")
        public int[] typeNotInList;
        

        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int accountIdSort;
        public int typeSort;
        public int isWebEnableSort;
        public int isWeixinEnableSort;
        public int isEmailEnableSort;
        public int isLarkEnableSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}