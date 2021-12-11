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
 * 
 * 
 * @author 杜展扬 2018-07-28
 *
 */
@DomainDefine(domainClass = CompanyPlugin.class)
@DomainDefineValid(comment ="" )
public class CompanyPlugin extends BaseDomain{
    //
    public static final int STATUS_启用 = 1;
    public static final int STATUS_禁用 = 2;
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="companyId",canUpdate=true)
    public int companyId;
    
    @ForeignKey(domainClass=PluginDefine.class)
    @DomainFieldValid(comment="pluginId",canUpdate=true)
    public int pluginId;
    
    @DomainFieldValid(comment="status",canUpdate=true,dataDict="CompanyPlugin.status")
    public int status;
    
    //
    //   
    public static class CompanyPluginInfo extends CompanyPlugin{
    //
        @NonPersistent
        @DomainField(foreignKeyFields="pluginId",field="name")
        @DomainFieldValid(comment = "pluginId名称")
        public String pluginName;
    

    }
    //
    //   
    @QueryDefine(domainClass=CompanyPluginInfo.class)
    public static class CompanyPluginQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;

        public Integer pluginId;

        public Integer status;

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
        @QueryField(foreignKeyFields="pluginId",field="name")
        public String pluginName;
        
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int pluginIdSort;
        public int pluginNameSort;
        public int statusSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}