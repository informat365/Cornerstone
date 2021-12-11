package cornerstone.biz.util;

import java.util.HashMap;
import java.util.Map;

import cornerstone.biz.domain.DesignerColumn;
import jazmin.core.app.AppException;

/**
 * 
 * @author icecooly
 *
 */
public class GenCodeUtil {

	private static Map<String,String>javaTypeMap=new HashMap<String, String>();
	static{
		javaTypeMap.put("VARCHAR","String");
		javaTypeMap.put("CHAR","String");
		javaTypeMap.put("TINYTEXT","String");
		javaTypeMap.put("TEXT","String");
		javaTypeMap.put("MEDIUMTEXT","String");
		javaTypeMap.put("LONGTEXT","String");
		
		javaTypeMap.put("BLOB","byte[]");
		javaTypeMap.put("MEDIUMBLOB","byte[]");
		javaTypeMap.put("LONGBLOB","byte[]");
		javaTypeMap.put("BINARY","byte[]");
		
		javaTypeMap.put("INT","int");
		javaTypeMap.put("INTEGER","int");
		javaTypeMap.put("MEDIUMINT","int");
		javaTypeMap.put("SMALLINT","int");
		javaTypeMap.put("TINYINT","int");
		javaTypeMap.put("BIGINT","long");
		javaTypeMap.put("INT UNSIGNED","int");
		javaTypeMap.put("INTEGER UNSIGNED","int");
		javaTypeMap.put("MEDIUMINT UNSIGNED","int");
		javaTypeMap.put("SMALLINT UNSIGNED","int");
		javaTypeMap.put("TINYINT UNSIGNED","int");
		javaTypeMap.put("BIGINT UNSIGNED","long");
		javaTypeMap.put("FLOAT","float");
		javaTypeMap.put("DOUBLE","double");
		javaTypeMap.put("FLOAT UNSIGNED","float");
		javaTypeMap.put("DOUBLE UNSIGNED","double");
		
		javaTypeMap.put("DATE","Date");
		javaTypeMap.put("TIME","Date");
		javaTypeMap.put("YEAR","Date");
		javaTypeMap.put("DATETIME","Date");
		javaTypeMap.put("TIMESTAMP","Date");
		
		javaTypeMap.put("INT IDENTITY","int");
		javaTypeMap.put("BIT","boolean");
		
		javaTypeMap.put("DECIMAL","BigDecimal");
		
	}
	
	private static Map<String,String>csharpTypeMap=new HashMap<String, String>();
	static{
		csharpTypeMap.put("VARCHAR","string");
		csharpTypeMap.put("CHAR","string");
		csharpTypeMap.put("TINYTEXT","string");
		csharpTypeMap.put("TEXT","string");
		csharpTypeMap.put("MEDIUMTEXT","string");
		csharpTypeMap.put("LONGTEXT","string");
		
		csharpTypeMap.put("BLOB","byte[]");
		csharpTypeMap.put("MEDIUMBLOB","byte[]");
		csharpTypeMap.put("LONGBLOB","byte[]");
		csharpTypeMap.put("BINARY","byte[]");
		
		csharpTypeMap.put("INT","int");
		csharpTypeMap.put("INTEGER","int");
		csharpTypeMap.put("MEDIUMINT","int");
		csharpTypeMap.put("SMALLINT","int");
		csharpTypeMap.put("TINYINT","int");
		csharpTypeMap.put("BIGINT","long");
		csharpTypeMap.put("INT UNSIGNED","int");
		csharpTypeMap.put("INTEGER UNSIGNED","int");
		csharpTypeMap.put("MEDIUMINT UNSIGNED","int");
		csharpTypeMap.put("SMALLINT UNSIGNED","int");
		csharpTypeMap.put("TINYINT UNSIGNED","int");
		csharpTypeMap.put("BIGINT UNSIGNED","long");
		csharpTypeMap.put("FLOAT","float");
		csharpTypeMap.put("DOUBLE","double");
		csharpTypeMap.put("FLOAT UNSIGNED","float");
		csharpTypeMap.put("DOUBLE UNSIGNED","double");
		csharpTypeMap.put("DOUBLE","double");
		
		csharpTypeMap.put("DATE","DateTime");
		csharpTypeMap.put("TIME","DateTime");
		csharpTypeMap.put("YEAR","DateTime");
		csharpTypeMap.put("DATETIME","DateTime");
		csharpTypeMap.put("TIMESTAMP","DateTime");
		
		csharpTypeMap.put("INT IDENTITY","int");
		csharpTypeMap.put("BIT","bool");
		
		csharpTypeMap.put("DECIMAL","Decimal");
	}
	
	private static Map<String,String> javaObjectTypeMap=new HashMap<String, String>();
	static{
		javaObjectTypeMap.put("VARCHAR","String");
		javaObjectTypeMap.put("CHAR","String");
		javaObjectTypeMap.put("TINYTEXT","String");
		javaObjectTypeMap.put("TEXT","String");
		javaObjectTypeMap.put("MEDIUMTEXT","String");
		javaObjectTypeMap.put("LONGTEXT","String");
		
		javaObjectTypeMap.put("BLOB","byte[]");
		javaObjectTypeMap.put("MEDIUMBLOB","byte[]");
		javaObjectTypeMap.put("LONGBLOB","byte[]");
		javaObjectTypeMap.put("BINARY","byte[]");
		
		javaObjectTypeMap.put("INT","Integer");
		javaObjectTypeMap.put("INTEGER","Integer");
		javaObjectTypeMap.put("MEDIUMINT","Integer");
		javaObjectTypeMap.put("SMALLINT","Integer");
		javaObjectTypeMap.put("TINYINT","Integer");
		javaObjectTypeMap.put("BIGINT","Long");
		javaObjectTypeMap.put("INT UNSIGNED","Integer");
		javaObjectTypeMap.put("INTEGER UNSIGNED","Integer");
		javaObjectTypeMap.put("MEDIUMINT UNSIGNED","Integer");
		javaObjectTypeMap.put("SMALLINT UNSIGNED","Integer");
		javaObjectTypeMap.put("TINYINT UNSIGNED","Integer");
		javaObjectTypeMap.put("BIGINT UNSIGNED","Long");
		javaObjectTypeMap.put("FLOAT","Float");
		javaObjectTypeMap.put("DOUBLE","Double");
		javaObjectTypeMap.put("FLOAT UNSIGNED","Float");
		javaObjectTypeMap.put("DOUBLE UNSIGNED","Double");
		javaObjectTypeMap.put("DOUBLE","Double");
		
		javaObjectTypeMap.put("DATE","Date");
		javaObjectTypeMap.put("TIME","Date");
		javaObjectTypeMap.put("YEAR","Date");
		javaObjectTypeMap.put("DATETIME","Date");
		javaObjectTypeMap.put("TIMESTAMP","Date");
		
		javaObjectTypeMap.put("INT IDENTITY","Integer");
		javaObjectTypeMap.put("BIT","Boolean");
		
		javaObjectTypeMap.put("DECIMAL","BigDecimal");
	}
	
	//从db_gskj变成Gskj
	public static String getProjectName(String dbName){
		if(StringUtil.isEmpty(dbName)){
			throw new IllegalArgumentException("dbName is null");
		}
		return toUpperCaseFirstOne(dbName.replaceAll("db_","").
				replaceAll("_uat","")).
				replaceAll("_test","");
	}
	
	// 从UserName变成userName
	public static String toLowerCaseFirstOne(String s) {
		return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
	}

	// 从userName变成UserName
	public static String toUpperCaseFirstOne(String s) {
		return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
	}

	// 从userName变成USER_NAME
	public static String getFinalFieldName(String fieldName) {
		return getDbFieldName(fieldName,false).toUpperCase();
	}

	//从userName变成user_name
	public static String getDbFieldName(String fieldName,boolean fieldOrigin) {
		if(fieldOrigin){
			return fieldName;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < fieldName.length(); i++) {
			char c = fieldName.charAt(i);
			if (Character.isUpperCase(c)) {
				sb.append("_").append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	//从UserName变成t_user_name
	public static String getDBTableName(Class<?> clazz) {
		String className=clazz.getSimpleName();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < className.length(); i++) {
			char c = className.charAt(i);
			if (Character.isUpperCase(c)&&i>0) {
				sb.append("_").append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		String tableName=("t_"+sb.toString()).toLowerCase();
		return tableName;
	}
	
	//user_name变成userName
	public static String getDomainField(String dbField){
		return toLowerCaseFirstOne(getClassField(dbField));
	}
	//user_name变成UserName
	public static String getClassField(String dbField){
		StringBuffer result=new StringBuffer();
		boolean nextUpper=false;
		for(int i=0;i<dbField.length();i++){
			if(i==0){
				result.append((dbField.charAt(i)+"").toUpperCase());
				continue;
			}
			if(dbField.charAt(i)=='_'){
				nextUpper=true;
				continue;
			}
			if(nextUpper){
				nextUpper=false;
				result.append((dbField.charAt(i)+"").toUpperCase());
			}else{
				result.append(dbField.charAt(i));
			}
		}
		return result.toString();
	}
	//t_account_info->AccountInfo
	public static String getClassName(String tableName){
		String fieldName=tableName;
		if(fieldName.startsWith("t_")){
			fieldName=fieldName.substring(2);
		}
		StringBuffer result=new StringBuffer();
		boolean nextUpper=false;
		for(int i=0;i<fieldName.length();i++){
			if(i==0){
				result.append((fieldName.charAt(i)+"").toUpperCase());
				continue;
			}
			if(fieldName.charAt(i)=='_'){
				nextUpper=true;
				continue;
			}
			if(nextUpper){
				nextUpper=false;
				result.append((fieldName.charAt(i)+"").toUpperCase());
			}else{
				result.append(fieldName.charAt(i));
			}
		}
		return result.toString();
	}
	
	public static String getJavaType(DesignerColumn column) {
		if(column.domainName.startsWith("is")&& "TINYINT".equals(column.dbType)) {
			return "boolean";
		}
		String type=javaTypeMap.get(column.dbType);
		if(type==null) {
			throw new AppException(column.dbType+" not support");
		}
		return type;
	}
	
	public static String getCSharpType(DesignerColumn column) {
		if(column.domainName.startsWith("is")&& "TINYINT".equals(column.dbType)) {
			return "bool";
		}
		String type=csharpTypeMap.get(column.dbType);
		if(type==null) {
			throw new AppException(column.dbType+" not support");
		}
		return type;
	}
	
	public static String getJavaObjectType(DesignerColumn column) {
		if(column.domainName.startsWith("is")&& "TINYINT".equals(column.dbType)) {
			return "Boolean";
		}
		return javaObjectTypeMap.get(column.dbType);
	}
}
