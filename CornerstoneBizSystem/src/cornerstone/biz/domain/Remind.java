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
 * 提醒
 * 
 * @author 杜展扬 2018-08-11
 *
 */
@DomainDefine(domainClass = Remind.class)
@DomainDefineValid(comment ="提醒" )
public class Remind extends BaseDomain{
    //
	public static final int REPEAT_不提醒 = 1;
    public static final int REPEAT_每天 = 2;
    public static final int REPEAT_每周 = 3;
    public static final int REPEAT_每双周 = 4;
    public static final int REPEAT_每月 = 5;
    //
    @ForeignKey(domainClass=Company.class)
    public int companyId;
    
    @DomainFieldValid(comment="事项",required=true,canUpdate=true,maxValue=100)
    public String name;
    
    @DomainFieldValid(comment="备注",canUpdate=true,maxValue=512)
    public String remark;
    
    @DomainFieldValid(comment="提醒时间",required=true,canUpdate=true)
    public Date remindTime;
    
    @DomainFieldValid(comment="重复",canUpdate=true,dataDict="Remind.repeat")
    public int repeat;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class RemindInfo extends Remind{
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
    @QueryDefine(domainClass=RemindInfo.class)
    public static class RemindQuery extends BizQuery{
        //
        public Integer id;
        
        public Integer companyId;

        public String name;

        public String remark;

        @QueryField(operator=">=",field="remindTime")
        public Date remindTimeStart;
        
        @QueryField(operator="<=",field="remindTime")
        public Date remindTimeEnd;

        public Integer repeat;
        
        @QueryField(operator="!=",field="repeat")
        public Integer excludeRepeat;

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
        @QueryField(operator="in",field="id")
        public int[] idInList;
        
        @QueryField(operator="in",field="repeat")
        public int[] repeatInList;
        
        @QueryField(operator="not in",field="repeat")
        public int[] repeatNotInList;
        

        //ForeignQueryFields
        @QueryField(foreignKeyFields="createAccountId",field="imageId")
        public String createAccountImageId;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int nameSort;
        public int remarkSort;
        public int remindTimeSort;
        public int repeatSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}