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
 * 任务关联
 * 
 * @author 杜展扬 2018-08-01
 *
 */
@DomainDefine(domainClass = TaskAssociated.class)
@DomainDefineValid(comment ="任务关联" ,uniqueKeys={@UniqueKey(fields={"taskId","associatedTaskId"})})
public class TaskAssociated extends BaseDomain{
    //
	public static final int ASSOCIATED_TYPE_无前置后置=0;
	public static final int ASSOCIATED_TYPE_前置任务=1;//taskId是前置任务是associatedTaskId
	public static final int ASSOCIATED_TYPE_后置任务=2;//taskId是后置任务是associatedTaskId
    //
	public int companyId;
	
    @ForeignKey(domainClass=Task.class)
    @DomainFieldValid(comment="任务")
    public int taskId;
    
    @ForeignKey(domainClass=Task.class)
    @DomainFieldValid(comment="关联任务")
    public int associatedTaskId;
    
    @DomainFieldValid(comment="类型")
    public int associatedType;
    //
    //   
    public static class TaskAssociatedInfo extends TaskAssociated{
    //
        @NonPersistent
        @DomainField(foreignKeyFields="taskId",field="name")
        @DomainFieldValid(comment = "任务名称")
        public String taskName;
    
        @NonPersistent
        @DomainField(foreignKeyFields="associatedTaskId",field="serialNo")
        @DomainFieldValid(comment = "关联任务序列号")
        public String serialNo;
        
        @NonPersistent
        @DomainField(foreignKeyFields="associatedTaskId",field="objectType")
        @DomainFieldValid(comment = "关联任务对象类型")
        public int objectType;
        
        @NonPersistent
        @DomainField(foreignKeyFields="associatedTaskId,objectType",field="name")
        @DomainFieldValid(comment = "关联任务对象类型名称")
        public String objectTypeName;
    
        @NonPersistent
        @DomainField(foreignKeyFields="associatedTaskId",field="name")
        @DomainFieldValid(comment = "关联任务名称")
        public String name;
        
        @NonPersistent
        @DomainField(foreignKeyFields="associatedTaskId",field="startDate")
        @DomainFieldValid(comment = "关联任务开始时间")
        public Date startDate;
        
        @NonPersistent
        @DomainField(foreignKeyFields="associatedTaskId",field="endDate")
        @DomainFieldValid(comment = "关联任务结束时间")
        public Date endDate;
        
        @NonPersistent
        @DomainField(foreignKeyFields="associatedTaskId",field="uuid")
        @DomainFieldValid(comment = "关联任务uuid")
        public String uuid;
        
        @NonPersistent
        @DomainField(foreignKeyFields="associatedTaskId",field="isFinish")
        @DomainFieldValid(comment = "关联任务是否结束")
        public boolean isFinish;
        
        @NonPersistent
        @DomainField(foreignKeyFields="associatedTaskId,createAccountId",field="name")
        @DomainFieldValid(comment = "关联任务创建人")
        public String createAccountName;
        
        @NonPersistent
        @DomainField(foreignKeyFields="associatedTaskId,createAccountId",field="imageId")
        @DomainFieldValid(comment = "关联任务创建人头像")
        public String createAccountImageId;
        
        @NonPersistent
        @DomainField(foreignKeyFields="associatedTaskId",field="ownerAccountIdList")
        @DomainFieldValid(comment = "关联任务责任人")
        public List<Integer> ownerAccountIdList;
        
        @NonPersistent
        @DomainField(foreignKeyFields="associatedTaskId",field="ownerAccountList")
        @DomainFieldValid(comment = "关联任务责任人头像")
        public List<AccountSimpleInfo> ownerAccountList;
        
        @NonPersistent
        @DomainField(foreignKeyFields="associatedTaskId,status",field="name")
        @DomainFieldValid(comment = "关联任务状态")
        public String statusName;
        
        @NonPersistent
        @DomainField(foreignKeyFields="associatedTaskId,status",field="color")
        @DomainFieldValid(comment = "关联任务状态颜色")
        public String statusColor;
        
        @NonPersistent
        @DomainField(foreignKeyFields="associatedTaskId,status",field="type")
        @DomainFieldValid(comment = "关联任务状态类型")
        public int statusType;
    }
    //
    //   
    @QueryDefine(domainClass=TaskAssociatedInfo.class)
    public static class TaskAssociatedQuery extends BizQuery{
        //
        public Integer id;

        public Integer taskId;

        public Integer associatedTaskId;
        
        public Integer associatedType;

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
        @QueryField(field="taskId")
        public int[] taskIdInList;

        @QueryField(field="associatedType")
        public int[] associateTypeInList;

        //ForeignQueryFields
        @QueryField(foreignKeyFields="taskId",field="name")
        public String taskName;
        
        @QueryField(foreignKeyFields="associatedTaskId",field="serialNo")
        public Integer associatedTaskSerialNo;
        
        @QueryField(foreignKeyFields="associatedTaskId",field="name")
        public String associatedTaskName;
        
        @QueryField(foreignKeyFields="createAccountId",field="imageId")
        public String createAccountImageId;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int taskIdSort;
        public int taskNameSort;
        public int associatedTaskIdSort;
        public int associatedTaskSerialNoSort;
        public int associatedTaskNameSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}