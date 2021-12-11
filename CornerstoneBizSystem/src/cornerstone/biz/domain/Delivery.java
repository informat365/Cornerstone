package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * 交付版本
 */
@DomainDefine(domainClass = Delivery.class)
@DomainDefineValid(comment = "交付版本", uniqueKeys = {@UniqueKey(fields = {"projectId", "name"})})
public class Delivery extends BaseDomain {

    public static final int STATUS_未交付 = 1;
    public static final int STATUS_已交付 = 2;

    //
    @ForeignKey(domainClass = Company.class)
    public int companyId;

    @ForeignKey(domainClass = Project.class)
    @DomainFieldValid(comment = "项目", required = true, canUpdate = true)
    public int projectId;

    @DomainFieldValid(comment = "名称", required = true, canUpdate = true, maxValue = 128)
    public String name;

    @DomainFieldValid(comment = "备注", required = true, canUpdate = true, maxValue = 128)
    public String remark;

    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "创建人", required = true, canUpdate = true)
    public int createAccountId;

    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "更新人", required = true, canUpdate = true)
    public int updateAccountId;

    @DomainFieldValid(comment = "交付时间", canUpdate = true)
    public Date deliveryDate;

    @DomainFieldValid(comment = "是否删除", required = true)
    public boolean isDelete;

    @DomainFieldValid(comment = "交付状态", required = true)
    public int status;


    //
    //   
    public static class DeliveryInfo extends Delivery {

        @NonPersistent
        @DomainField(ignoreWhenSelect = true)
        public List<DeliveryItem.DeliveryItemInfo> deliveryItems;

    }

    //
    //   
    @QueryDefine(domainClass = DeliveryInfo.class)
    public static class DeliveryQuery extends BizQuery {
        //
        public Boolean isDelete;

        public Integer id;

        public Integer companyId;

        public Integer projectId;

        public String name;

        public Integer createAccountId;

        public Integer updateAccountId;

        @QueryField(operator = ">=", field = "createTime")
        public Date createTimeStart;

        @QueryField(operator = "<=", field = "createTime")
        public Date createTimeEnd;

        @QueryField(operator = ">=", field = "updateTime")
        public Date updateTimeStart;

        @QueryField(operator = "<=", field = "updateTime")
        public Date updateTimeEnd;

        @QueryField(operator = ">=", field = "deliveryDate")
        public Date deliveryDateStart;

        @QueryField(operator = "<=", field = "deliveryDate")
        public Date deliveryDateEnd;

        //in or not in fields
        @QueryField(field = "id", operator = "in")
        public int[] idInList;
        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int projectIdSort;
        public int nameSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}