package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.InnerJoin;
import jazmin.driver.jdbc.smartjdbc.annotations.NonPersistent;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;

/**
 * 附件
 *
 * @author 杜展扬 2018-08-01
 */
@DomainDefine(domainClass = Attachment.class)
@DomainDefineValid(comment = "附件", uniqueKeys = {@UniqueKey(fields = {"uuid"})})
public class Attachment extends BaseDomain {
    //
    @ForeignKey(domainClass = Company.class)
    @DomainFieldValid(comment = "企业", required = true, canUpdate = true)
    public int companyId;

    @DomainFieldValid(comment = "附件id", required = true, canUpdate = true, maxValue = 64)
    public String uuid;

    @DomainFieldValid(comment = "附件名", required = true, canUpdate = true, maxValue = 256)
    public String name;

    @DomainFieldValid(comment = "附件大小", required = true, canUpdate = true)
    public long size;

    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "创建人id", required = true, canUpdate = true)
    public int createAccountId;

    @DomainFieldValid(comment = "是否被删除", required = true, canUpdate = true)
    public boolean isDelete;

    @DomainFieldValid(comment = "md5", canUpdate = true, maxValue = 64)
    public String md5;

    //
    //   
    public static class AttachmentInfo extends Attachment {
        //
        @NonPersistent
        @DomainField(foreignKeyFields = "createAccountId", field = "name")
        @DomainFieldValid(comment = "创建人")
        public String createAccountName;

        @NonPersistent
        @DomainField(foreignKeyFields = "createAccountId", field = "imageId")
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
    }

    //
    //   
    @QueryDefine(domainClass = AttachmentInfo.class)
    public static class AttachmentQuery extends BizQuery {
        //
        public Integer id;

        public Integer companyId;

        public String uuid;

        public String name;

        public Long size;

        public Integer createAccountId;

        public String createAccountName;

        public Boolean isDelete;

        public String md5;

        @QueryField(operator = ">=", field = "createTime")
        public Date createTimeStart;

        @QueryField(operator = "<=", field = "createTime")
        public Date createTimeEnd;

        @QueryField(operator = ">=", field = "updateTime")
        public Date updateTimeStart;

        @QueryField(operator = "<=", field = "updateTime")
        public Date updateTimeEnd;

        //in or not in fields

        //ForeignQueryFields
        @InnerJoin(table2 = AttachmentAssociated.class, table2Field = "attachmentId")
        public Integer projectId;

        @InnerJoin(table2 = AttachmentAssociated.class, table2Field = "attachmentId")
        public Integer taskId;

        //inner joins
        //sort
        public int idSort;
        public int uuidSort;
        public int nameSort;
        public int sizeSort;
        public int createAccountIdSort;
        public int createAccountNameSort;
        public int isDeleteSort;
        public int md5Sort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}