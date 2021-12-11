package cornerstone.biz.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cornerstone.biz.ConstDefine;
import cornerstone.biz.domain.DataBaseInfo;
import cornerstone.biz.domain.DataBaseInfo.TableDetailInfo;
import cornerstone.biz.domain.DesignerDatabase;
import cornerstone.biz.util.JdbcUtil.ColumnInfo;
import cornerstone.biz.util.JdbcUtil.IndexInfo;
import cornerstone.biz.util.JdbcUtil.TableInfo;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;


/**
 * 
 * @author cs
 *
 */
public class DataBaseCache {
	//
	public static Map<Integer,DataBaseInfo> dataBaseInfoMap=new ConcurrentHashMap<>();
	//
	private static Logger logger=LoggerFactory.getLogger(DataBaseCache.class);
	//
	public static synchronized DataBaseInfo reload(DesignerDatabase database) {
		String dbUrl=null;
		try {
			long startTime=System.currentTimeMillis();
			String user=database.dbUser;
			String pwd=TripleDESUtil.decrypt(database.dbPassword, ConstDefine.GLOBAL_KEY);
			dbUrl = JdbcUtil.getDbUrl(database.dbType,database.host, database.port, database.instanceId);
			List<TableInfo> tables= JdbcUtil.getTables(dbUrl, user, pwd,database.instanceId);
			DataBaseInfo dataBaseInfo=new DataBaseInfo();
			dataBaseInfo.id=database.id;
			dataBaseInfo.tableInfos=new LinkedHashMap<>();
			dataBaseInfo.columnInfos=JdbcUtil.getColumns(database.dbType,dbUrl, user, pwd,"%");
			List<String> tableNames=new ArrayList<>();
			for (TableInfo e : tables) {
				tableNames.add(e.name);
			}
			Map<String,List<IndexInfo>> indexs=JdbcUtil.getIndexs(dbUrl, user, pwd, tableNames);
			for (TableInfo e : tables) {
				List<ColumnInfo> columnInfos=new ArrayList<>();
				for (ColumnInfo ci : dataBaseInfo.columnInfos) {
					if(ci.tableName.equals(e.name)) {
						columnInfos.add(ci);
					}
				}
				Map<String, List<IndexInfo>> uniqueIndexMap=new HashMap<>();
				for (Map.Entry<String, List<IndexInfo>> index : indexs.entrySet()) {
					if(index.getKey().equals(e.name)) {
						uniqueIndexMap=getUniqueIndexMap(index.getValue());
						break;
					}
				}
				TableDetailInfo detailInfo=new TableDetailInfo();
				detailInfo.tableInfo=e;
				detailInfo.columnInfo=columnInfos;
				detailInfo.uniqueIndexMap=uniqueIndexMap;
				dataBaseInfo.tableInfos.put(e.name, detailInfo);
				logger.info("tableInfos put {}",e.name);
			}
			dataBaseInfoMap.put(database.id, dataBaseInfo);
			long endTime=System.currentTimeMillis();
			logger.info("DataBaseCache database:{} reload using {}ms",DumpUtil.dump(database),endTime-startTime);
			return dataBaseInfo;
		} catch (Exception e) {
			logger.error("dbUrl:"+dbUrl);
			logger.error(e.getMessage(),e);
			throw new AppException("数据库连接失败");
		}
		
	}
	//
	private static Map<String, List<IndexInfo>> getUniqueIndexMap(List<IndexInfo> list){
		Map<String,List<IndexInfo>> uniqueMap=new HashMap<>();
		 for (IndexInfo indexInfo : list) {
			 if(indexInfo.nonUnique){
				 continue;
			 }
			 List<IndexInfo> indexInfos=uniqueMap.get(indexInfo.name);
			 if(indexInfos==null){
				 indexInfos=new ArrayList<>();
				 uniqueMap.put(indexInfo.name, indexInfos);
			 }
			 indexInfos.add(indexInfo);
		}
		return uniqueMap;
	}
	//
	public static TableDetailInfo getTable(DesignerDatabase database,String tableName) {
		DataBaseInfo info=dataBaseInfoMap.get(database.id);
		if(info==null) {
			info=reload(database);
		}
		TableDetailInfo tableDetailInfo=info.tableInfos.get(tableName);
		if(tableDetailInfo==null) {
			throw new AppException(tableName+"表不存在");
		}
		return tableDetailInfo;
	}
	//
	public static TableInfo getTableInfo(DesignerDatabase database,String tableName) {
		TableDetailInfo info=getTable(database, tableName);
		return info.tableInfo;
	}
	//
	public static List<ColumnInfo> getColumnInfos(DesignerDatabase database,String tableName) {
		TableDetailInfo info=getTable(database, tableName);
		return info.columnInfo;
	}
	//
	public static Map<String,List<IndexInfo>> getUniqueIndexMap(DesignerDatabase database,String tableName) {
		TableDetailInfo info=getTable(database, tableName);
		return info.uniqueIndexMap;
	}
	
	public static List<TableInfo> getTableInfos(DesignerDatabase database) {
		DataBaseInfo info=dataBaseInfoMap.get(database.id);
		if(info==null) {
			info=reload(database);
		}
		List<TableInfo> tables=new ArrayList<>();
		for (TableDetailInfo e : info.tableInfos.values()) {
			tables.add(e.tableInfo);
		}
		logger.debug("getTableInfos tablesSize:{}",tables.size());
		return tables;
	}
	
	public static List<ColumnInfo> getColumns(DesignerDatabase database) {
		DataBaseInfo info=dataBaseInfoMap.get(database.id);
		if(info==null) {
			info=reload(database);
		}
		return info.columnInfos;
	}
}
