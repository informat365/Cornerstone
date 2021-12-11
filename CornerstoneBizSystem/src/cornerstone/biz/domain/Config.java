package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;

/**
 * 配置
 * @author cs
 *
 */
@DomainDefine(domainClass = Config.class)
public class Config extends BaseDomain{
	
	@DomainFieldValid(comment="名称")
	public String name;
	
	@DomainFieldValid(comment="参数值")
	public String value;
	
	public String valueType;
	
	@DomainFieldValid(comment="描述")
	public String description;
	
	public boolean test;
	
	public boolean isHidden;
	
	@QueryDefine(domainClass=Config.class)
    public static class ConfigQuery extends BizQuery{
        //
        public Integer id;

        public String name;

        public String value;

        public String valueType;

        public Integer test;

        public Boolean isHidden;

        public String description;

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
        public int nameSort;
        public int valueSort;
        public int valueTypeSort;
        public int testSort;
        public int isHiddenSort;
        public int descriptionSort;
        public int createTimeSort;
        public int updateTimeSort;
    }
}
