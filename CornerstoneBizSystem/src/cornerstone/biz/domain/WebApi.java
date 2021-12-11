package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import cornerstone.biz.webapi.WebApiDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * webApi
 * 
 * @author 杜展扬 2019-06-19 18:27
 *
 */
@DomainDefine(domainClass = WebApi.class)
@DomainDefineValid(comment ="webApi" ,uniqueKeys={@UniqueKey(fields={"apiKey"})})
public class WebApi extends BaseDomain{
    //
    public static final int STATUS_有效 = 1;
    public static final int STATUS_无效 = 2;
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @DomainFieldValid(comment="API KEY",required=true,canUpdate=true,maxValue=64)
    public String apiKey;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=100)
    public String name;
    
    @DomainFieldValid(comment="分组",canUpdate=true,maxValue=64)
    public String group;
    
    @DomainFieldValid(comment="状态",canUpdate=true,required=true,dataDict="Common.status")
    public int status;
    
    @DomainFieldValid(comment="是否删除",required=true,canUpdate=true)
    public boolean isDelete;
    
    @DomainFieldValid(comment="脚本",canUpdate=true)
    public String script;
    
    @DomainFieldValid(comment="备注",canUpdate=true)
    public String remark;
    
    @DomainFieldValid(comment="js对象",canUpdate=true)
    public WebApiDefine webApiDefine;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class WebApiInfo extends WebApi{
    //
        @DomainField(foreignKeyFields="createAccountId",field="imageId",persistent=false)
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
    
        @DomainField(foreignKeyFields="createAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
    

    }
    //
    //   
    @QueryDefine(domainClass=WebApiInfo.class)
    public static class WebApiQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;

        public String apiKey;

        public String name;

        public String group;

        public Integer status;

        public Boolean isDelete;

        public String script;

        public String remark;

        public String webApiDefine;

        public Integer createAccountId;

        public Integer updateAccountId;

        @QueryField(operator=">=",field="createTime")
        public Date createTimeStart;
        
        @QueryField(operator="<=",field="createTime")
        public Date createTimeEnd;

        @QueryField(operator=">=",field="updateTime")
        public Date updateTimeStart;
        
        @QueryField(operator="<=",field="updateTime")
        public Date updateTimeEnd;

        //in or not in fields
        @QueryField(operator="in",field="status")
        public int[] statusInList;
        
        @QueryField(operator="not in",field="status")
        public int[] statusNotInList;
        

        //ForeignQueryFields
        @QueryField(foreignKeyFields="createAccountId",field="imageId")
        public String createAccountImageId;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int apiKeySort;
        public int nameSort;
        public int groupSort;
        public int statusSort;
        public int isDeleteSort;
        public int scriptSort;
        public int remarkSort;
        public int webApiDefineSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}