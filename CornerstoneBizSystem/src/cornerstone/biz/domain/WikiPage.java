package cornerstone.biz.domain;

import java.util.Date;

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
 * wikie页面
 *
 * @author 杜展扬 2018-08-16
 */
@DomainDefine(domainClass = WikiPage.class)
@DomainDefineValid(comment = "wikie页面", uniqueKeys = {@UniqueKey(fields = {"uuid"})})
public class WikiPage extends BaseDomain {
    //
    public static final int TYPE_富文本 = 1;
    public static final int TYPE_markdown = 2;
    public static final int TYPE_思维脑图 = 3;
    public static final int TYPE_表格 = 4;
    public static final int TYPE_专业思维脑图 = 5;
    //
//    public static final int STATUS_草稿 = 1;//取消20190216
    public static final int STATUS_已发布 = 2;
    //
    @ForeignKey(domainClass = Company.class)
    @DomainFieldValid(comment = "企业", required = true, canUpdate = true)
    public int companyId;

    @ForeignKey(domainClass = Project.class)
    @DomainFieldValid(comment = "项目", required = true, canUpdate = true)
    public int projectId;

    @ForeignKey(domainClass = Wiki.class)
    @DomainFieldValid(comment = "WIKI", required = true, canUpdate = true)
    public int wikiId;

    @DomainFieldValid(comment = "UUID", canUpdate = true, maxValue = 64)
    public String uuid;

    @DomainFieldValid(comment = "名称", required = true, canUpdate = true, maxValue = 128)
    public String name;

    @DomainFieldValid(comment = "类型", canUpdate = true, dataDict = "WikiPage.type")
    public int type;

    @DomainFieldValid(comment = "状态", canUpdate = true, dataDict = "WikiPage.status")
    public int status;

    @ForeignKey(domainClass = WikiPage.class)
    @DomainFieldValid(comment = "父页面", canUpdate = true)
    public int parentId;

    @DomainFieldValid(comment = "层级", canUpdate = true)
    public int level;

    @DomainFieldValid(comment = "是否被删除", required = true, canUpdate = true)
    public boolean isDelete;

    @ForeignKey(domainClass = WikiPage.class)
    @DomainFieldValid(comment = "原始ID")
    public int originalId;

    @ForeignKey(domainClass = WikiPage.class)
    @DomainFieldValid(comment = "草稿ID")
    public int draftId;

    @ForeignKey(domainClass = WikiContent.class)
    @DomainFieldValid(comment = "内容")
    public int contentId;

    @DomainFieldValid(comment = "排序权重")
    public int sortWeight;

    @DomainFieldValid(comment = "是否关联任务")
    public boolean isAssociatedTask;

    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "创建人", required = true, canUpdate = true)
    public int createAccountId;

    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "更新人", required = true, canUpdate = true)
    public int updateAccountId;

    /**
     * 是否建立索引
     */
    public boolean isCreateIndex;

    //
    //   
    public static class WikiPageInfo extends WikiPage {
        //
        @NonPersistent
        @DomainField(foreignKeyFields = "createAccountId", field = "imageId")
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;

        @NonPersistent
        @DomainField(foreignKeyFields = "createAccountId", field = "name")
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;

        @NonPersistent
        @DomainField(foreignKeyFields = "updateAccountId", field = "imageId")
        @DomainFieldValid(comment = "更新人头像")
        public String updateAccountImageId;

        @NonPersistent
        @DomainField(foreignKeyFields = "updateAccountId", field = "name")
        @DomainFieldValid(comment = "更新人名称")
        public String updateAccountName;

        @NonPersistent
        @DomainField(foreignKeyFields = "projectId", field = "name")
        public String projectName;

        @NonPersistent
        @DomainField(foreignKeyFields = "projectId", field = "uuid")
        public String projectUuid;
    }

    //
    public static class WikiPageDetailInfo extends WikiPageInfo {
        //
        @NonPersistent
        @DomainField(foreignKeyFields = "contentId", field = "content")
        @DomainFieldValid(comment = "内容")
        public String content;
    }

    //
    @QueryDefine(domainClass = WikiPageDetailInfo.class)
    public static class WikiPageDetailInfoQuery extends WikiPageQuery {

    }

    //
    //   
    @QueryDefine(domainClass = WikiPageInfo.class)
    public static class WikiPageQuery extends BizQuery {
        //
        public Integer id;

        public Integer companyId;

        public Integer projectId;

        public Integer wikiId;

        public String uuid;

        public String name;

        public Integer type;

        public Integer status;

        public Integer parentId;

        public Integer level;

        public Boolean isDelete;

        public Integer draftId;

        public Integer originalId;

        public Boolean isAssociatedTask;

        public Integer createAccountId;

        public Integer updateAccountId;

        public Boolean isCreateIndex;

        @QueryField(operator = ">=", field = "createTime")
        public Date createTimeStart;

        @QueryField(operator = "<=", field = "createTime")
        public Date createTimeEnd;

        @QueryField(operator = ">=", field = "updateTime")
        public Date updateTimeStart;

        @QueryField(operator = "<=", field = "updateTime")
        public Date updateTimeEnd;

        //in or not in fields
        @QueryField(operator = "in", field = "type")
        public int[] typeInList;

        @QueryField(operator = "not in", field = "type")
        public int[] typeNotInList;

        @QueryField(operator = "in", field = "status")
        public int[] statusInList;

        @QueryField(operator = "not in", field = "status")
        public int[] statusNotInList;


        @QueryField(operator = "in", field = "createAccountId")
        public int[] createAccountIdInList;


        @QueryField(operator = "in", field = "wikiId")
        public int[] wikiIds;


        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int wikiIdSort;
        public int uuidSort;
        public int nameSort;
        public int typeSort;
        public int statusSort;
        public int parentIdSort;
        public int levelSort;
        public int isDeleteSort;
        public int isAssociatedTaskSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
        public int sortWeightSort;
    }

}