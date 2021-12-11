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
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;

/**
 * 项目功能开关
 *
 * @author 杜展扬 2018-07-29
 */
@DomainDefine(domainClass = ProjectModule.class)
@DomainDefineValid(comment = "项目功能开关", uniqueKeys = {@UniqueKey(fields = {"projectId", "name"})})
public class ProjectModule extends BaseDomain {

    public static final String SYS_MODULE_URL_DEVOPS="devops";
    public static final String SYS_MODULE_URL_敏捷="agile";
    public static final String SYS_MODULE_URL_文件="file";
    public static final String SYS_MODULE_URL_WIKI="wiki";
    public static final String SYS_MODULE_URL_交付版本="delivery";
    public static final String SYS_MODULE_URL_阶段="stage";
    public static final String SYS_MODULE_URL_里程碑="landmark";

    //
    @ForeignKey(domainClass = Company.class)
    public int companyId;
    //
    @ForeignKey(domainClass = Project.class)
    @DomainFieldValid(comment = "项目", required = true, canUpdate = true)
    public int projectId;

    @ForeignKey(domainClass = ObjectType.class)
    @DomainFieldValid(comment = "对象类型", required = true, canUpdate = true)
    public int objectType;

    @DomainFieldValid(comment = "模块", required = true, canUpdate = true, maxValue = 64)
    public String name;

    @DomainFieldValid(comment = "是否启用", required = true, canUpdate = true)
    public boolean isEnable;

    @DomainFieldValid(comment = "URL", canUpdate = true, maxValue = 64)
    public String url;

    @DomainFieldValid(comment = "是否基于时间", canUpdate = true)//有startDat或endDate且isShow 用于在日历里判断哪些模块可以增加
    public boolean isTimeBased;

    @DomainFieldValid(comment = "是否基于状态", canUpdate = true)//统计相关
    public boolean isStatusBased;

    @DomainFieldValid(comment = "是否开放给所有成员", canUpdate = true)
    public boolean isPublic;

    @DomainFieldValid(comment = "指定开放角色", canUpdate = true)//创建人和责任人 必然能看到
    public List<Integer> publicRoles;

    @DomainFieldValid(comment = "排序字段", required = true, canUpdate = true)
    public int sortWeight;

    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "创建人", required = true, canUpdate = true)
    public int createAccountId;

    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "更新人", required = true, canUpdate = true)
    public int updateAccountId;

    //
    //   
    public static class ProjectModuleInfo extends ProjectModule {
        //
        @DomainField(foreignKeyFields = "objectType", field = "name", persistent = false)
        public String objectTypeName;
        //
        @DomainField(foreignKeyFields = "objectType", field = "systemName", persistent = false)
        public String objectTypeSystemName;

    }

    //
    //   
    @QueryDefine(domainClass = ProjectModuleInfo.class)
    public static class ProjectModuleQuery extends BizQuery {
        //
        public Integer id;

        public Integer companyId;

        public Integer projectId;

        public Integer objectType;

        public String name;

        public Boolean isEnable;

        public String url;

        public Boolean isTimeBased;

        public Boolean isStatusBased;

        public Integer sortWeight;

        public Integer createAccountId;

        public Integer updateAccountId;

        @QueryField(foreignKeyFields = "projectId", field = "isDelete")
        public Boolean projectIsDelete;

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
        public int projectIdSort;
        public int objectTypeSort;
        public int nameSort;
        public int isEnableSort;
        public int urlSort;
        public int isTimeBasedSort;
        public int isStatusBasedSort;
        public int sortWeightSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}