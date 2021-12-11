package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.datatable.DataTableDefine;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 数据表格变更记录
 * 
 * @author 杜展扬 2019-06-03 19:37
 *
 */
@DomainDefine(domainClass = DataTableLog.class)
@DomainDefineValid(comment ="数据表格变更记录" )
public class DataTableLog extends BaseDomain{
    //
    //
    @ForeignKey(domainClass=DataTable.class)
    @DomainFieldValid(comment="数据表格",required=true,canUpdate=true)
    public int dataTableId;
    
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=100)
    public String name;
    
    @DomainFieldValid(comment="分组",canUpdate=true,maxValue=64)
    public String group;
    
    @DomainFieldValid(comment="授权角色",canUpdate=true,maxValue=512)
    public List<Integer> roles;
    
    @DomainFieldValid(comment="是否启用授权角色",required=true,canUpdate=true)
    public boolean enableRole;
    
    @DomainFieldValid(comment="状态",canUpdate=true)
    public int status;
    
    @DomainFieldValid(comment="是否删除",required=true,canUpdate=true)
    public boolean isDelete;
    
    @DomainFieldValid(comment="脚本",canUpdate=true)
    public String script;
    
    @DomainFieldValid(comment="备注",canUpdate=true)
    public String remark;
    
    @DomainFieldValid(comment="js对象",canUpdate=true)
    public DataTableDefine dataTableDefine;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class DataTableLogInfo extends DataTableLog{
    //

    }
    //
    //   
    @QueryDefine(domainClass=DataTableLogInfo.class)
    public static class DataTableLogQuery extends BizQuery{
        //
        public Integer id;

        public Integer dataTableId;

        public Integer companyId;

        public String name;

        public String group;

        public String roles;

        public Integer enableRole;

        public Integer status;

        public Boolean isDelete;

        public String script;

        public String remark;

        public String dataTableDefine;

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

        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int dataTableIdSort;
        public int companyIdSort;
        public int nameSort;
        public int groupSort;
        public int rolesSort;
        public int enableRoleSort;
        public int statusSort;
        public int isDeleteSort;
        public int scriptSort;
        public int remarkSort;
        public int dataTableDefineSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}