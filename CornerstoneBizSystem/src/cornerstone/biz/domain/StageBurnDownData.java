package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainFieldValid;

import java.util.Date;

/**
 * 
 * @author cs
 *
 */
public class StageBurnDownData {

	@DomainFieldValid(comment="统计时间")
	public Date statDate;
	
	@DomainFieldValid(comment="预计未完成数量")
    public long expectUnfinishNum;
	
	@DomainFieldValid(comment="实际未完成数量")
	public long unfinishNum;
}
