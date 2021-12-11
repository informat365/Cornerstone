package cornerstone.biz.domain;

import java.util.ArrayList;
import java.util.List;

import cornerstone.biz.util.StatUtil.StatResult;

/**
 * SCM提交统计
 * @author cs
 *
 */
public class ScmCommitStatInfo {

	/**累积代码提交数量*/
	public List<StatResult> totalList;
	
	/**每日提交数量*/
	public List<StatResult> dailyList;
	
	/**每个提交人贡献度*/
	public List<StatResult> authorList;
	//
	public ScmCommitStatInfo() {
		totalList=new ArrayList<>();
		dailyList=new ArrayList<>();
		authorList=new ArrayList<>();
	}
}
