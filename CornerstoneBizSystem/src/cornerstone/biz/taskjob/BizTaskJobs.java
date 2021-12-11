package cornerstone.biz.taskjob;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import cornerstone.biz.dao.ITFDAO;
import cornerstone.biz.domain.Config;
import cornerstone.biz.domain.DataDict;
import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.domain.Region;
import cornerstone.biz.util.StringUtil;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;
import jazmin.core.task.TaskDefine;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;

/**
 * 
 * @author cs
 *
 */
public class BizTaskJobs {
	//
	private static Logger logger=LoggerFactory.get(BizTaskJobs.class);
	//
	@AutoWired ITFDAO dao;
	
	public static Map<String,List<DataDict>> dictMap=new HashMap<>();
	public static List<DataDict> dataDicts=new ArrayList<DataDict>();
	//
	public static List<Region>regions=Collections.synchronizedList(new ArrayList<Region>());
	public static Map<Integer,Region>regionMap=new ConcurrentHashMap<Integer, Region>();
	public static Map<String,Region>regionCodeMap=new ConcurrentHashMap<String, Region>();

	//
	@TaskDefine(initialDelay=0,unit=TimeUnit.DAYS,period=30)
	public void reloadRegions(){
		List<Region> list=dao.getAll(Region.class);
		regions.clear();
		regions.addAll(list);
		//
		regionMap.clear();
		regionCodeMap.clear();
		for(Region r:regions){
			regionMap.put(r.id,r);
			regionCodeMap.put(r.code, r);
		}
		//
		logger.debug("regions size:{}",regions.size());
	}
	//
	//
	public static Region getRegion(int id){
		return regionMap.get(id);
	}
	
	public static Region getExistedRegion(int id){
		Region bean=regionMap.get(id);
		if(bean==null) {
			throw new IllegalArgumentException("区域信息错误");
		}
		return bean;
	}
	
	public static Region getRegionByCode(String code){
		return regionCodeMap.get(code);
	}
	//
	//
	public static Region getRegionByNameLevel(String name,int level){
		for(Region r:regions){
			if(r.level==level&&r.name.equals(name)){
				return r;
			}
		}
		return null;
	}
	//
	public static List<Region> getRegions(int parent){
		List<Region> result=new ArrayList<Region>();
		for(Region r:regions){
			if(r.parent==parent){
				result.add(r);
			}
		}
		return result;
	}
	//
	public static List<Region> getRegionsByLevel(int level){
		List<Region> result=new ArrayList<Region>();
		for(Region r:regions){
			if(r.level==level){
				result.add(r);
			}
		}
		return result;
	}
	//
	@TaskDefine(initialDelay=5,unit=TimeUnit.MINUTES,period=5)
	public void reloadGlobalConfig(){
		List<Config> configs=dao.getAll(Config.class);
		GlobalConfig.setupConfig(configs);
	}
	//
	@TaskDefine(initialDelay=0,unit=TimeUnit.MINUTES,period=5)
	public void reloadGlobalData(){
		reloadGlobalData0();
	}
	
	public synchronized void reloadGlobalData0(){
		logger.info("reloadGlobalData0");
		dataDicts=dao.getAll(DataDict.class);
		Map<String,List<DataDict>> tmpDictMap=new HashMap<>();
		for (DataDict dict : dataDicts) {
			 List<DataDict> list=tmpDictMap.get(dict.categoryCode);
			if(list==null){
				list=new ArrayList<>();
				tmpDictMap.put(dict.categoryCode,list);
			}
			list.add(dict);
		}
		dictMap=tmpDictMap;
		//排序(从小到大)
		for (List<DataDict> values : dictMap.values()) {
			values.sort(Comparator.comparingInt((DataDict o) -> o.orderWeight));
		}
		//
		dataDicts.sort(Comparator.comparingInt((DataDict o) -> o.orderWeight));
		//
		logger.debug("dictMap size:"+dictMap.size());
	}	
	//
	public static List<DataDict> getDataDicts(String categoryCode) {
		return dictMap.get(categoryCode);
	}
	//
	public static String getDataDictName(String dataDictCategoryCode,int value) {
		List<DataDict> dataDicts = dictMap.get(dataDictCategoryCode);
		if(dataDicts==null){
			throw new IllegalArgumentException("dataDicts not found.dataDictCategoryCode:"+dataDictCategoryCode);
		}
		for (DataDict dataDict : dataDicts) {
			if (dataDict.value==value) {
				return dataDict.name;
			}
		}
		return "";
	}
	//
	public static void checkDataDictValueValid(String dataDictCategoryCode,int value,String errorMsg) {
		List<DataDict> dataDicts = dictMap.get(dataDictCategoryCode);
		if(dataDicts==null){
			throw new IllegalArgumentException("dataDicts not found.dataDictCategoryCode:"+dataDictCategoryCode);
		}
		for (DataDict dataDict : dataDicts) {
			if (dataDict.value==value) {
				return;
			}
		}
		if(StringUtil.isEmpty(errorMsg)) {
			errorMsg="参数错误";
		}
		throw new AppException(errorMsg);
	}
	//
	public static int getDataDictValue(String dataDictCategoryCode,String name) {
		List<DataDict> dataDicts = dictMap.get(dataDictCategoryCode);
		if(dataDicts==null){
			throw new IllegalArgumentException("dataDicts not found.dataDictCategoryCode:"+dataDictCategoryCode);
		}
		for (DataDict dataDict : dataDicts) {
			if (dataDict.name.equals(name)) {
				return dataDict.value;
			}
		}
		return 0;
	}
}
