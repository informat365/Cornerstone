package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 全国行政区域
 * 
 * @author 杜展扬 2018-07-28
 *
 */
@DomainDefine(domainClass = Region.class)
@DomainDefineValid(comment ="全国行政区域" )
public class Region extends BaseDomain{
    //
    //
    @DomainFieldValid(comment="编码",required=true,canUpdate=true,maxValue=100)
    public String code;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=100)
    public String name;
    
    @DomainFieldValid(comment="父ID",required=true,canUpdate=true)
    public int parent;
    
    @DomainFieldValid(comment="级别",required=true,canUpdate=true)
    public int level;
    
    @DomainFieldValid(comment="排序",required=true,canUpdate=true)
    public int regionOrder;
    
    @DomainFieldValid(comment="拼音",required=true,canUpdate=true,maxValue=100)
    public String nameEn;
    
    @DomainFieldValid(comment="短写拼音",required=true,canUpdate=true,maxValue=10)
    public String shortNameEn;
    
    @DomainFieldValid(comment="邮编",required=true,canUpdate=true)
    public int postcode;
    
    @DomainFieldValid(comment="对照表id",required=true,canUpdate=true)
    public int num;
    
    //
    //   
    public static class RegionInfo extends Region{
    //

    }
    //
    //   
    @QueryDefine(domainClass=RegionInfo.class)
    public static class RegionQuery extends BizQuery{
        //
        public Integer id;

        public String code;

        public String name;

        public Integer parent;

        public Integer level;

        public Integer regionOrder;

        public String nameEn;

        public String shortNameEn;

        public Integer postcode;

        public Integer num;

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
        public int codeSort;
        public int nameSort;
        public int parentSort;
        public int levelSort;
        public int regionOrderSort;
        public int nameEnSort;
        public int shortNameEnSort;
        public int postcodeSort;
        public int numSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}