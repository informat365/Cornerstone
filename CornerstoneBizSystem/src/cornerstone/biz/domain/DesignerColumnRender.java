package cornerstone.biz.domain;

import java.util.List;

/**
 * 
 * @author cs
 *
 */
public class DesignerColumnRender {

	public DesignerColumn column;
	
	public String javaType;//BigInt->long
	
	public String javaObjectType;//int->Integer
	
	public String csharpType;//BigInt->long
	
	public String foreignDomainName;
	
	public String foreignColumnDomainName;//user_name->userName
	
	public String foreignTableComment;//用户
	
	public String relationDomainName;
	
	public String relationColumnDomainName;//market_id->marketId
	
	public String relationColumnJavaName;//market_id->MarketId
	
	public String relationTableComment;//用户
	
	public String dataDictId;//Bug.level
	
	public List<DataDictValue> dataDictValues;//["L1","L2","L3"]
	
	public int displayWidth;
	
	//
	public String relationRightDomainName;//中间表关联目标表列 就是把relationRightColumnName转化为java形态的
	
	public String targetTableComment;//目标表的注释
	
	public String targetDomainName;//就是把targetTableName转化为java形态的 t_coin_define->CoinDefine
	
	public String targetColumnDomainName;//就是把targetColumnName转化为java形态的 user_name->userName
	
	//
	public static class DataDictValue{
		public String name;
		public int value;
	}

}
