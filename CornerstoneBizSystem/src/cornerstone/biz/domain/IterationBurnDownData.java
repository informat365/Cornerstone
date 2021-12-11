package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainFieldValid;

/**
 * 燃尽图
 * @author cs
 *
 */
public class IterationBurnDownData {

	public static final int TYPE_任务数=1;
	public static final int TYPE_工作量=2;


	@DomainFieldValid(comment="统计时间")
	public Date statDate;
	
	@DomainFieldValid(comment="预计未完成量")
    public double expectUnfinishNum;
	
	@DomainFieldValid(comment="实际未完成量")
	public double unfinishNum;
}
