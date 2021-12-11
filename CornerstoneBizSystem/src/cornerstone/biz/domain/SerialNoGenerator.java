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
 * 序列号生成器
 * 
 * @author 杜展扬 2019-06-22 16:14
 *
 */
@DomainDefine(domainClass = SerialNoGenerator.class)
@DomainDefineValid(comment ="序列号生成器" ,uniqueKeys={@UniqueKey(fields={"companyId"})})
public class SerialNoGenerator extends BaseDomain{
    //
	public static final int TYPE_流程实例序列号=1;
    //
	public int type;
	
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @DomainFieldValid(comment="序列号",required=true,canUpdate=true)
    public int serialNo;
    
    //
    //   
    public static class SerialNoGeneratorInfo extends SerialNoGenerator{
    //

    }
    //
    //   
    @QueryDefine(domainClass=SerialNoGeneratorInfo.class)
    public static class SerialNoGeneratorQuery extends BizQuery{
        //
        public Integer id;
        
        public Integer type;

        public Integer companyId;

        public Integer serialNo;

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
        public int serialNoSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}