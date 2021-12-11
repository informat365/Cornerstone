package cornerstone.biz;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jazmin.log.Logger;
import jazmin.log.LoggerFactory;

/**
 * 
 * @author cs
 *
 */
public class CornerstoneThreadFactory {
	//
	private static Logger logger=LoggerFactory.get(CornerstoneThreadFactory.class);
	//
	private static ExecutorService threadPool = Executors.newFixedThreadPool(16);
	//
	public static void execute(Runnable runnable) {
		try {
			threadPool.execute(runnable);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	//
}
