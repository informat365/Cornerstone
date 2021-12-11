package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 后台用户token
 * 
 * @author 杜展扬 2019-09-21 14:16
 *
 */
@DomainDefine(domainClass = UserToken.class)
@DomainDefineValid(comment ="后台用户token" ,uniqueKeys={@UniqueKey(fields={"userId"}),@UniqueKey(fields={"token"})})
public class UserToken extends BaseDomain{
    //
    //
    @DomainFieldValid(comment="TOKEN",required=true,canUpdate=true,maxValue=32)
    public String token;
    
    @ForeignKey(domainClass=User.class)
    @DomainFieldValid(comment="用户",required=true,canUpdate=true)
    public int userId;
    
    //
    //   
    public static class UserTokenInfo extends UserToken{
    //

    }
    //
    //   
    @QueryDefine(domainClass=UserTokenInfo.class)
    public static class UserTokenQuery extends BizQuery{
        //
        public Integer id;

        public String token;

        public Integer userId;

        @QueryField(operator=">=",field="createTime")
        public Date createTimeStart;
        
        @QueryField(operator="<=",field="createTime")
        public Date createTimeEnd;

        @QueryField(operator=">=",field="updateTime")
        public Date updateTimeStart;
        
        @QueryField(operator="<=",field="updateTime")
        public Date updateTimeEnd;

        //in or not in fields

        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int tokenSort;
        public int userIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}