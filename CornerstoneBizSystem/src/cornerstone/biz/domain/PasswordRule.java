package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;

import java.util.Date;
import java.util.List;

/**
 * 企业密码规则
 */
@DomainDefine(domainClass = PasswordRule.class)
@DomainDefineValid(comment = "企业密码规则", uniqueKeys = {@UniqueKey(fields = {"companyId"})})
public class PasswordRule extends BaseDomain {

    public static final int RULE_八位长度以上=1;
    public static final int RULE_包含大小写字母=2;
    public static final int RULE_包含数字=3;
    public static final int RULE_包含特殊字符=4;
    public static final int RULE_不能包含用户名=5;
    public static final int RULE_不能与前两次密码重复=6;
    public static final int RULE_无限制=7;

    @DomainFieldValid(comment = "企业ID", required = true, canUpdate = true)
    public int companyId;

    /*@DomainFieldValid(comment = "自定义密钥", canUpdate = true)
    public String cusKey;
*/
    @DomainFieldValid(comment = "规则列表", required = true, canUpdate = true)
    public List<Integer> rules;

    public static class PasswordRuleInfo extends PasswordRule {
        //

    }

    //
    //   
    @QueryDefine(domainClass = PasswordRuleInfo.class)
    public static class PasswordRuleQuery extends BizQuery {
        //
        public Integer id;

        public Integer companyId;


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
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}