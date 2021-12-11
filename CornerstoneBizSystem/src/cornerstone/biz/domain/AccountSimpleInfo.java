package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;

/**
 * 
 * @author cs
 *
 */
@DomainDefine(domainClass = Account.class)
public class AccountSimpleInfo extends BaseDomain{

	 @DomainFieldValid(comment="企业")
	 public int companyId;
	 
	 @DomainFieldValid(comment="名称",canUpdate=true,maxValue=64)
	 public String name;
	 
	 @DomainFieldValid(comment="用户名",canUpdate=true,maxValue=64)
	 public String userName;
	 
	 @DomainFieldValid(comment="头像",canUpdate=true,maxValue=64)
	 public String imageId;
	 //
	 @QueryDefine(domainClass=AccountSimpleInfo.class)
	 //
	 public static class AccountSimpleInfoQuery extends BizQuery{
	     //
	     public Integer id;
	     
	     @QueryField(field="id")
	     public int[] idInList;

	     public String name;
	 }
}
