package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * 阶段关联对象
 *
 * @author 杜展扬 2018-08-17
 */
@DomainDefine(domainClass = StageAssociate.class)
@DomainDefineValid(comment = "阶段关联对象")
public class StageAssociate extends BaseDomain {

    public static final int TYPE_任务=1;
    public static final int TYPE_里程碑=2;

    //
    //
    @DomainFieldValid(comment = "企业", required = true, canUpdate = true)
    public int companyId;

    @DomainFieldValid(comment = "项目", required = true, canUpdate = true)
    public int projectId;

    @ForeignKey(domainClass = Stage.class)
    @DomainFieldValid(comment = "阶段", required = true, canUpdate = true)
    public int stageId;

    @DomainFieldValid(comment = "关联类型", canUpdate = true)
    public int type;

    @ForeignKey(domainClass = Task.class)
    @DomainFieldValid(comment = "关联ID", canUpdate = true)
    public int associateId;

    @ForeignKey(domainClass = Landmark.class)
    @DomainFieldValid(comment = "里程碑ID", canUpdate = true)
    public int landmarkId;

    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "创建人", canUpdate = true)
    public int createAccountId;

    @DomainFieldValid(comment = "更新人", canUpdate = true)
    public int updateAccountId;



    public static class StageAssociateInfo extends StageAssociate {

        @NonPersistent
        @DomainField(foreignKeyFields="associateId",field="name")
        @DomainFieldValid(comment = "关联对象名称")
        public  String taskName;

        @NonPersistent
        @DomainField(foreignKeyFields="landmarkId",field="name")
        @DomainFieldValid(comment = "里程碑名称")
        public  String landmarkName;

        @NonPersistent
        @DomainField(foreignKeyFields="associateId",field="uuid")
        @DomainFieldValid(comment = "关联对象uuid")
        public  String taskUuid;

        @NonPersistent
        @DomainField(foreignKeyFields="associateId",field="objectType")
        @DomainFieldValid(comment = "关联对象类型")
        public  Integer objectType;

        @NonPersistent
        @DomainField(ignoreWhenSelect = true)
        @DomainFieldValid(comment = "关联对象类型名称")
        public  String objectTypeName;

        @NonPersistent
        @DomainField(foreignKeyFields="stageId",field="status")
        @DomainFieldValid(comment = "关联阶段状态")
        public  Integer stageStatus;

        @NonPersistent
        @DomainField(foreignKeyFields="associateId",field="serialNo")
        @DomainFieldValid(comment = "关联任务序列号")
        public  String taskSerialNo;

        @NonPersistent
        @DomainField(foreignKeyFields="associateId",field="status")
        @DomainFieldValid(comment = "关联任务状态")
        public  Integer taskStatus;

        @NonPersistent
        @DomainField(foreignKeyFields="associateId",field="isFinish")
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
        @DomainField(foreignKeyFields="associateId",field="startDate")
        @DomainFieldValid(comment = "关联任务创建时间")
        public  Date taskCreateTime;

        @NonPersistent
        @DomainField(foreignKeyFields="associateId",field="endDate")
        @DomainFieldValid(comment = "关联任务截止时间")
        public  Date taskEndTime;


        @NonPersistent
        @DomainField(foreignKeyFields="associateId",field="ownerAccountList")
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
    @QueryDefine(domainClass = StageAssociateInfo.class)
    public static class StageAssociateQuery extends BizQuery {
        //
        public Integer id;

        public Integer companyId;
        public Integer projectId;
        public Integer stageId;
        public Integer type;
        public Integer associateId;

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