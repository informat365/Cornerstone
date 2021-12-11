package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.*;

import java.util.Date;
import java.util.List;

/**
 *里程碑关联对象
 *
 */
@DomainDefine(domainClass = LandmarkAssociate.class)
@DomainDefineValid(comment = "里程碑关联对象")
public class LandmarkAssociate extends BaseDomain {


    //
    //
    @DomainFieldValid(comment = "企业", required = true, canUpdate = true)
    public int companyId;

    @DomainFieldValid(comment = "项目", required = true, canUpdate = true)
    public int projectId;

    @ForeignKey(domainClass = Task.class)
    @DomainFieldValid(comment = "任务", canUpdate = true)
    public int taskId;

    @ForeignKey(domainClass = Landmark.class)
    @DomainFieldValid(comment = "里程碑", canUpdate = true)
    public int landmarkId;

    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "创建人", canUpdate = true)
    public int createAccountId;

    @DomainFieldValid(comment = "更新人", canUpdate = true)
    public int updateAccountId;



    public static class LandmarkAssociateInfo extends LandmarkAssociate {

        @NonPersistent
        @DomainField(foreignKeyFields="taskId",field="name")
        @DomainFieldValid(comment = "关联对象名称")
        public  String taskName;

        @NonPersistent
        @DomainField(foreignKeyFields="landmarkId",field="name")
        @DomainFieldValid(comment = "里程碑名称")
        public  String landmarkName;

        @NonPersistent
        @DomainField(foreignKeyFields="taskId",field="uuid")
        @DomainFieldValid(comment = "关联对象uuid")
        public  String taskUuid;

        @NonPersistent
        @DomainField(foreignKeyFields="taskId",field="objectType")
        @DomainFieldValid(comment = "关联对象类型")
        public  Integer objectType;

        @NonPersistent
        @DomainField(ignoreWhenSelect = true)
        @DomainFieldValid(comment = "关联对象类型名称")
        public  String objectTypeName;

        @NonPersistent
        @DomainField(foreignKeyFields="taskId",field="serialNo")
        @DomainFieldValid(comment = "关联任务序列号")
        public  String taskSerialNo;

        @NonPersistent
        @DomainField(foreignKeyFields="taskId",field="status")
        @DomainFieldValid(comment = "关联任务状态")
        public  Integer taskStatus;

        @NonPersistent
        @DomainField(foreignKeyFields="taskId",field="isFinish")
        @DomainFieldValid(comment = "关联任务是否已完成")
        public  boolean isFinish;

        @NonPersistent
        @DomainField(ignoreWhenSelect = true)
        @DomainFieldValid(comment = "关联任务状态")
        public  String taskStatusName;

        @NonPersistent
        @DomainField(ignoreWhenSelect = true)
        @DomainFieldValid(comment = "关联任务状态颜色")
        public  String taskStatusColor;

        @NonPersistent
        @DomainField(foreignKeyFields="taskId",field="startDate")
        @DomainFieldValid(comment = "关联任务创建时间")
        public  Date taskCreateTime;

        @NonPersistent
        @DomainField(foreignKeyFields="taskId",field="endDate")
        @DomainFieldValid(comment = "关联任务截止时间")
        public  Date taskEndTime;


        @NonPersistent
        @DomainField(foreignKeyFields="taskId",field="ownerAccountList")
        @DomainFieldValid(comment = "关联任务负责人")
        public  List<AccountSimpleInfo> ownerAccountList;


        @NonPersistent
        @DomainField(foreignKeyFields="landmarkId",field="startDate")
        @DomainFieldValid(comment = "关联任务创建时间")
        public  Date landmarkStartDate;

        @NonPersistent
        @DomainField(foreignKeyFields="landmarkId",field="endDate")
        @DomainFieldValid(comment = "关联任务截止时间")
        public  Date landmarkEndDate;


    }

    //
    //   
    @QueryDefine(domainClass = LandmarkAssociateInfo.class)
    public static class LandmarkAssociateQuery extends BizQuery {
        //
        public Integer id;

        public Integer companyId;
        public Integer projectId;
        public Integer landmarkId;
        public Integer taskId;

        @QueryField(operator = ">=", field = "createTime")
        public Date createTimeStart;

        @QueryField(operator = "<=", field = "createTime")
        public Date createTimeEnd;

        @QueryField(operator = ">=", field = "updateTime")
        public Date updateTimeStart;

        @QueryField(operator = "<=", field = "updateTime")
        public Date updateTimeEnd;

        //in or not in fields

        //inner joins
        //sort
        public int idSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}