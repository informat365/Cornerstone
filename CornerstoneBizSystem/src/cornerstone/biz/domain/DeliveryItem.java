package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.*;

import java.util.Date;

/**
 * 交付版本明细
 */
@DomainDefine(domainClass = DeliveryItem.class)
@DomainDefineValid(comment = "交付版本明细")
public class DeliveryItem extends BaseDomain {
    //
    @DomainFieldValid(comment = "交付版本ID", required = true, canUpdate = true)
    public int deliveryId;

    @ForeignKey(domainClass = CompanyVersionRepository.class)
    @DomainFieldValid(comment = "企业版本库ID", required = true, canUpdate = true)
    public int repositoryId;

    @ForeignKey(domainClass = CompanyVersion.class)
    @DomainFieldValid(comment = "企业版本库版本ID", required = true, canUpdate = true)
    public int versionId;



    public static class DeliveryItemInfo extends DeliveryItem {

        @NonPersistent
        @DomainField(foreignKeyFields = "repositoryId", field = "name")
        @DomainFieldValid(comment = "版本库名称")
        public String repositoryName;

        @NonPersistent
        @DomainField(foreignKeyFields = "versionId", field = "name")
        @DomainFieldValid(comment = "版本名称")
        public String versionName;

        @NonPersistent
        @DomainField(foreignKeyFields = "versionId", field = "status")
        @DomainFieldValid(comment = "企业版本库版本状态")
        public int versionStatus;

        @NonPersistent
        @DomainField(foreignKeyFields = "versionId", field = "remark")
        @DomainFieldValid(comment = "企业版本库版本备注")
        public String versionRemark;
    }

    //
    //   
    @QueryDefine(domainClass = DeliveryItemInfo.class)
    public static class DeliveryItemQuery extends BizQuery {

        public Integer id;

        public Integer deliveryId;

        public Integer repositoryId;

        public Integer versionId;

        @QueryField(operator = ">=", field = "createTime")
        public Date createTimeStart;

        @QueryField(operator = "<=", field = "createTime")
        public Date createTimeEnd;

        @QueryField(operator = ">=", field = "updateTime")
        public Date updateTimeStart;

        @QueryField(operator = "<=", field = "updateTime")
        public Date updateTimeEnd;

        //in or not in fields
        @QueryField(field = "id", operator = "in")
        public int[] idInList;
        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int deliveryIdSort;
        public int repositoryIdSort;
        public int versionIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}