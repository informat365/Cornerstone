package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainFieldValid;


/**
 * 
 * @author cs
 *
 */
public abstract class BaseDomain {
	//
	@DomainFieldValid(comment="ID")
	public int id;
	
	@DomainFieldValid(comment="创建时间")
	public Date createTime;
	
	@DomainFieldValid(comment="更新时间")
	public Date updateTime;
}
