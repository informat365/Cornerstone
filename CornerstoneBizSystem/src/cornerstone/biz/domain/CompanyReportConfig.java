package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 企业报表配置
 * 
 * @author 杜展扬 2020-02-26 14:41
 *
 */
@DomainDefine(domainClass = CompanyReportConfig.class)
@DomainDefineValid(comment ="企业报表配置" ,uniqueKeys={@UniqueKey(fields={"companyId"})})
public class CompanyReportConfig extends BaseDomain{
    //
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @DomainFieldValid(comment="项目报表配置",canUpdate=true)
    public List<Integer> projectReport;
    
    @DomainFieldValid(comment="企业报表配置",canUpdate=true)
    public List<Integer> companyReport;
    
    @DomainFieldValid(comment="数据表格",canUpdate=true)
    public List<Integer> dataTable;
    
    //
    //   
    public static class CompanyReportConfigInfo extends CompanyReportConfig{
    //

    }
    //
    //   
    @QueryDefine(domainClass=CompanyReportConfigInfo.class)
    public static class CompanyReportConfigQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;

        public String projectReport;

        public String companyReport;

        public String dataTable;

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
        public int companyIdSort;
        public int projectReportSort;
        public int companyReportSort;
        public int dataTableSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}