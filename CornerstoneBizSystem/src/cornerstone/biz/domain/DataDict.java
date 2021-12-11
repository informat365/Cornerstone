package cornerstone.biz.domain;
import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;

/**
 * 
 * @author cs
 *
 */
@DomainDefine(domainClass=DataDict.class)
@DomainDefineValid(comment="数据字典")
public class DataDict{
	//
	/**分类编码*/
	public String categoryCode;
	
	/**名称*/
	public String name;
	
	/**值*/
	public int value;
	
	/**描述*/
	public String description;
	
	/**排序权重 从小到大*/
	public int orderWeight;
	
	/**创建时间*/
	public Date createTime;
	
	/**最后更新时间*/
	public Date updateTime;
	
}