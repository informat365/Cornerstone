package cornerstone.web.taskjob;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cornerstone.biz.BizData;
import cornerstone.biz.domain.Config;
import cornerstone.biz.domain.GlobalConfig;
import jazmin.core.task.TaskDefine;

/**
 * 
 * @author cs
 *
 */
public class WebTaskJobs {
	//
	@TaskDefine(initialDelay=5,unit=TimeUnit.MINUTES,period=5)
	public void reloadGlobalConfig(){
		List<Config> configs=BizData.taskJobAction.getConfigs();
		GlobalConfig.setupConfig(configs);
	}
}
