package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.NonPersistent;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 插件定义
 * 
 * @author 杜展扬 2018-07-28
 *
 */
@DomainDefine(domainClass = PluginDefine.class)
@DomainDefineValid(comment ="插件定义" )
public class PluginDefine extends BaseDomain{
    //
    //
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=64)
    public String name;
    
    @DomainFieldValid(comment="描述",canUpdate=true,maxValue=512)
    public String description;
    
    @DomainFieldValid(comment="logo",canUpdate=true,maxValue=64)
    public String imageId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true)
    public int updateAccountId;
    
    //
    //   
    public static class PluginDefineInfo extends PluginDefine{
    //
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="name")
        @DomainFieldValid(comment = "创建人姓名")
        public String createAccountName;
    

    }
    //
    //   
    @QueryDefine(domainClass=PluginDefineInfo.class)
    public static class PluginDefineQuery extends BizQuery{
        //
        public Integer id;

        public String name;

        public String description;

        public String imageId;

        public Integer createUserId;

        public Integer updateUserId;

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
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int nameSort;
        public int descriptionSort;
        public int imageIdSort;
        public int createAccountIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}