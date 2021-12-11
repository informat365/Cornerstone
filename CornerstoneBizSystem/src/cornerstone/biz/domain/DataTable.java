package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.datatable.DataTableDefine;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 数据表
 * 
 * @author 杜展扬 2019-05-31 15:34
 *
 */
@DomainDefine(domainClass = DataTable.class)
@DomainDefineValid(comment ="数据表" )
public class DataTable extends BaseDomain{
    //
	public static final int STATUS_有效=1;
	public static final int STATUS_无效=2;
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=100)
    public String name;
    
    @DomainFieldValid(comment="分组",canUpdate=true)
    public String group;
    
    @DomainFieldValid(comment="授权角色",canUpdate=true,maxValue=512)
    public List<Integer> roles;
    
    @DomainFieldValid(comment="是否启用授权角色",canUpdate=true,maxValue=512)
    public boolean enableRole;
    
    @DomainFieldValid(comment="状态",required=true,canUpdate=true,dataDict="Common.status")
    public int status;
    
    @DomainFieldValid(comment="是否删除",required=true,canUpdate=true)
    public boolean isDelete;
    
    @DomainFieldValid(comment="脚本",required=true,canUpdate=true)
    public String script;
    
    @DomainFieldValid(comment="备注",canUpdate=true)
    public String remark;
    
    @DomainFieldValid(comment="脚本",canUpdate=true)
    public DataTableDefine dataTableDefine;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class DataTableInfo extends DataTable{
    //
        @DomainField(foreignKeyFields="createAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
    
        @DomainField(foreignKeyFields="updateAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "更新人名称")
        public String updateAccountName;
    }
    //
    //   
    @QueryDefine(domainClass=DataTableInfo.class)
    public static class DataTableQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;

        public String name;
        
        public String group;
        
        public Boolean enableRole;
        
        public Integer status;

        public Boolean isDelete;

        public String script;

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
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        @QueryField(foreignKeyFields="updateAccountId",field="name")
        public String updateAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int nameSort;
        public int groupSort;
        public int statusSort;
        public int isDeleteSort;
        public int scriptSort;
        public int createAccountIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int updateAccountNameSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}