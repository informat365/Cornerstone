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
 * 日程
 * 
 * @author 杜展扬 2019-12-04 19:34
 *
 */
@DomainDefine(domainClass = CalendarSchedule.class)
@DomainDefineValid(comment ="日程" )
public class CalendarSchedule extends BaseDomain{
    //
    public static final int REPEAT_永不 = 1;
    public static final int REPEAT_每天 = 2;
    public static final int REPEAT_每周 = 3;
    public static final int REPEAT_每两周 = 4;
    public static final int REPEAT_每月 = 5;
    public static final int REPEAT_每年 = 6;
    //
	@DomainFieldValid(comment="uuid",required=true)
    public String uuid;
	
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @ForeignKey(domainClass=Calendar.class)
    @DomainFieldValid(comment="日历",required=true,canUpdate=true)
    public int calendarId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=128)
    public String name;
    
    @DomainFieldValid(comment="颜色",maxValue=64)
    public String color;
    
    @DomainFieldValid(comment="开始时间",required=true,canUpdate=true)
    public Date startTime;
    
    @DomainFieldValid(comment="结束时间",required=true,canUpdate=true)
    public Date endTime;
    
    @DomainFieldValid(comment="重复",canUpdate=true,dataDict="CalendarSchedule.repeat")
    public int repeat;
    
    /**责任人列表*/
    public List<Integer> ownerAccountIdList;
    
    /**责任人列表*/
    public List<AccountSimpleInfo> ownerAccountList;
    
    @DomainFieldValid(comment="备注",canUpdate=true,maxValue=512)
    public String remark;
    
    @DomainFieldValid(comment="是否删除",required=true,canUpdate=true)
    public boolean isDelete;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class CalendarScheduleInfo extends CalendarSchedule{
    //
        @DomainField(foreignKeyFields="createAccountId",field="imageId",persistent=false)
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
    
        @DomainField(foreignKeyFields="createAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
    

    }
    //
    //   
    @QueryDefine(domainClass=CalendarScheduleInfo.class)
    public static class CalendarScheduleQuery extends BizQuery{
        //
        public Integer id;
        
        public String uuid;

        public Integer companyId;

        public Integer calendarId;

        public String name;

        @QueryField(operator=">=",field="startTime")
        public Date startTimeStart;
        
        @QueryField(operator="<=",field="startTime")
        public Date startTimeEnd;

        @QueryField(operator=">=",field="endTime")
        public Date endTimeStart;
        
        @QueryField(operator="<=",field="endTime")
        public Date endTimeEnd;

        public Integer repeat;

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
        @QueryField(operator="in",field="repeat")
        public int[] repeatInList;
        
        @QueryField(operator="not in",field="repeat")
        public int[] repeatNotInList;
        
        @QueryField(operator="!=",field="repeat")
        public Integer excludeRepeat;
        

        //ForeignQueryFields
        @QueryField(foreignKeyFields="createAccountId",field="imageId")
        public String createAccountImageId;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        
        @InnerJoin(table2=Calendar.class,table1Field="calendarId",table2Field="id")
        @QueryField(whereSql="(i1.create_account_id=${createAccountOrMember} or json_contains(i1.members,'${createAccountOrMember}')) ")
        public Integer createAccountOrMember;//创建人或成员
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int calendarIdSort;
        public int nameSort;
        public int colorSort;
        public int startTimeSort;
        public int endTimeSort;
        public int repeatSort;
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