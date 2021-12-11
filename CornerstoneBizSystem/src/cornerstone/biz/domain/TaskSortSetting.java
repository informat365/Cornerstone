package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;

import java.util.Date;
import java.util.List;

/**
 * 任务看板排序设置
 * @author yaop
 */
@DomainDefine(domainClass = TaskSortSetting.class)
@DomainDefineValid(comment = "任务看板排序设置")
public class TaskSortSetting extends BaseDomain {

    public static final int TYPE_看板=1;
    public static final int TYPE_甘特图=2;

    @DomainFieldValid(comment = "企业", required = true)
    public int companyId;

    @DomainFieldValid(comment = "项目", required = true)
    public int projectId;

    @DomainFieldValid(comment = "对象类型", required = true)
    public int objectType;

    @DomainFieldValid(comment = "任务视图类型(默认看板)", required = true)
    public int type;

    @DomainFieldValid(comment = "排序数据",canUpdate = true)
    public String sortData;

    @DomainFieldValid(comment = "创建人",canUpdate = true)
    public int createAccountId;

    @DomainFieldValid(comment = "更新人",canUpdate = true)
    public int updateAccountId;

    public static class TaskSortSettingInfo extends TaskSortSetting {

    }

    @QueryDefine(domainClass = TaskSortSettingInfo.class)
    public static class TaskSortSettingQuery extends BizQuery {
        public Integer id;

        public Integer projectId;

        public Integer type;

        public Integer companyId;

        public Integer objectType;

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
        public int companyIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}