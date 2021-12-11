package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;

import java.util.Date;

/**
 * 供应商
 */
@DomainDefine(domainClass = Supplier.class)
@DomainDefineValid(comment = "供应商")
public class Supplier extends BaseDomain {

    public static final int SERVICE_驻场=1;
    public static final int SERVICE_非驻场=2;


    public static final int OVERLAND_跨境=1;
    public static final int OVERLAND_非跨境=2;

    public static final int OUT_关联外包=1;
    public static final int OUT_非关联外包=2;



    @DomainFieldValid(comment = "名称", canUpdate = true,required = true,maxValue = 64)
    public String name;

    @DomainFieldValid(comment = "企业", canUpdate = true)
    public int companyId;

    @DomainFieldValid(comment = "曾用名1", canUpdate = true, maxValue = 64)
    public String oldName1;

    @DomainFieldValid(comment = "曾用名2", canUpdate = true, maxValue = 64)
    public String oldName2;

    @DomainFieldValid(comment = "组织机构代码", canUpdate = true, maxValue = 64)
    public String code;

    @DomainFieldValid(comment = "实施方式",  canUpdate = true)
    public int serviceType;

    @DomainFieldValid(comment = "跨境情况",  canUpdate = true)
    public int overlandType;

    @DomainFieldValid(comment = "关联外包类型",  canUpdate = true)
    public int outType;

    @DomainFieldValid(comment = "发生服务中断或异常退出",  canUpdate = true)
    public boolean exited;

    @DomainFieldValid(comment = "创建人",  canUpdate = true)
    public int createUserId;

    @DomainFieldValid(comment = "更新人",  canUpdate = true)
    public int updateUserId;

    @DomainFieldValid(comment = "创建时间", canUpdate = true)
    public Date createTime;

    @DomainFieldValid(comment = "更新时间", canUpdate = true)
    public Date updateTime;

    public static class SupplierInfo extends Supplier {



    }

    //
    //   
    @QueryDefine(domainClass = SupplierInfo.class)
    public static class SupplierQuery extends BizQuery {


        //
        public Integer id;
        public Boolean exited;
        public Integer serviceType;
        public Integer overlandType;
        public Integer outType;

        public String oldName1;
        public String oldName2;
        public String code;
        public String name;


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
        public int createTimeSort;
        public int updateTimeSort;
    }

}