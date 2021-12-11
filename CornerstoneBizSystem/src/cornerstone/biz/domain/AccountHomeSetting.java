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
 * 成员主页设置
 * @author yaop
 */
@DomainDefine(domainClass = AccountHomeSetting.class)
@DomainDefineValid(comment = "成员主页设置")
public class AccountHomeSetting extends BaseDomain {

    @DomainFieldValid(comment = "成员", required = true)
    public int companyId;

    @DomainFieldValid(comment = "成员", required = true)
    public int accountId;

    @DomainFieldValid(comment = "是否使用新的桌面主页",canUpdate = true)
    public boolean visible;

    @DomainFieldValid(comment = "当前选中菜单",canUpdate = true)
    public int currentMenu;

    @DomainFieldValid(comment = "常用菜单列表(排序记忆)", canUpdate = true)
    public List<Integer> menuList;

    @DomainFieldValid(comment = "不常用菜单列表(排序记忆)", canUpdate = true)
    public List<Integer> inactiveMenuList;


    public static class AccountHomeSettingInfo extends AccountHomeSetting {

    }

    @QueryDefine(domainClass = AccountHomeSettingInfo.class)
    public static class AccountHomeSettingQuery extends BizQuery {
        public Integer id;

        public Integer accountId;

        public Integer companyId;

        public Boolean newVersion;


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
        public int accountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}