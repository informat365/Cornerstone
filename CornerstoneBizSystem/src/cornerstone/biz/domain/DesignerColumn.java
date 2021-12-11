package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.util.StringUtil;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;

/**
 * 
 * @author cs
 *
 */
@DomainDefine(domainClass=DesignerColumn.class)
public class DesignerColumn extends BaseDomain{
	//
	public static final int FIELD_TYPE_DB=1;
	public static final int FIELD_TYPE_FOREIGN=2;//外键
	public static final int FIELD_TYPE_RELATION=3;//一对多
	public static final int FIELD_TYPE_MULTIPE_RELATION=4;//多对多  t_market 通过 t_coin_market表 关联  t_coin_define表
	//
	public static final int SHOW_TYPE_字符串=1;
	public static final int SHOW_TYPE_日期=2;
	public static final int SHOW_TYPE_日期时间=3;
	public static final int SHOW_TYPE_图片大=4;
	public static final int SHOW_TYPE_图片中=5;
	public static final int SHOW_TYPE_图片小=6;
	public static final int SHOW_TYPE_货币分=7;
	public static final int SHOW_TYPE_密码=8;
	public static final int SHOW_TYPE_下拉框=9;
	public static final int SHOW_TYPE_单选框=10;
	public static final int SHOW_TYPE_富文本=11;
	public static final int SHOW_TYPE_文件=12;
	public static final int SHOW_TYPE_手机号=13;
	public static final int SHOW_TYPE_邮箱=14;
	public static final int SHOW_TYPE_开关=15;
	public static final int SHOW_TYPE_数字=16;
	public static final int SHOW_TYPE_表格=17;
	//
	public int designerDatabaseId;
	
	@ForeignKey(domainClass=DesignerColumn.class)
	@DomainFieldValid(comment="关联外键id",canUpdate=true)
	public int relationDesignerColumnId;
	
	public String tableName;//t_shop
	
	public String columnName;//shop_name
	
	@DomainFieldValid(comment="domain名称",canUpdate=true)
	public String domainName;//shopName
	
	public int fieldType;
	
	public String dbType;
	
	public int dbSize;
	
	public String dbDef;
	
	public String dbIsAutoincrement;
	
	public String dbNullable;
	
	public String comment;
	
	@DomainFieldValid(comment="显示名称",canUpdate=true)
	public String displayName;
	
	@DomainFieldValid(comment="是否是查询条件",canUpdate=true)
	public boolean isQuery;
	
	@DomainFieldValid(comment="是否是区间查询",canUpdate=true)
	public boolean isRangeQuery;
	
	@DomainFieldValid(comment="是否在列表显示",canUpdate=true)
	public boolean isShowInList;
	
	@DomainFieldValid(comment="是否必选",canUpdate=true)
	public boolean isRequire;
	
	@DomainFieldValid(comment="是否可以编辑",canUpdate=true)
	public boolean isCanModify;
	
	@DomainFieldValid(comment="是否可以排序",canUpdate=true)
	public boolean isCanOrder;
	
	@DomainFieldValid(comment="是否在详情页面展示",canUpdate=true)
	public boolean isShowInDetailPage;
	
	@DomainFieldValid(comment="分组名",canUpdate=true)
	public String sectionName;
	
	@DomainFieldValid(comment="外键表名",canUpdate=true)
	public String foreignTableName;

	@DomainFieldValid(comment="外键表列名",canUpdate=true)
	public String foreignColumnName;
	
	@DomainFieldValid(comment="关联表名",canUpdate=true)
	public String relationTableName;

	@DomainFieldValid(comment="关联表列名",canUpdate=true)
	public String relationColumnName;//relationLeftColumnName
	
	@DomainFieldValid(comment="最小值",canUpdate=true)//包含
	public int minValue;
	
	@DomainFieldValid(comment="最大值",canUpdate=true)//包含
	public int maxValue;
	
	@DomainFieldValid(comment="显示类型",canUpdate=true)
	public int showType;
	
	@DomainFieldValid(comment="数据字典",canUpdate=true)
	public String dataDict;
	
	@DomainFieldValid(comment="排序",canUpdate=true)
	public int order;
	
	@DomainFieldValid(comment="中间表关联目标表列",canUpdate=true)
	public String relationRightColumnName;
	
	@DomainFieldValid(comment="目标表表名",canUpdate=true)
	public String targetTableName;
	
	@DomainFieldValid(comment="目标表列名",canUpdate=true)
	public String targetColumnName;
	
	@ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
	
	//
	public String createDomainFieldValid(String tableDomainName) {
		 String domainFieldValid="";
		 if(comment==null) {
			 comment="";
		 }
		 domainFieldValid+=("comment=\""+displayName+"\"");
		 if(isRequire) {
			 domainFieldValid+=(",required=true");
		 }
		 if(minValue>0) {
			 domainFieldValid+=(",minValue="+minValue);
		 }
		 if(maxValue>0) {
			 domainFieldValid+=(",maxValue="+maxValue);
		 }
		 //数据字典
		 if(!StringUtil.isEmpty(dataDict)) {
			 String fianlDataDictId="";
			 if(dataDict.indexOf(",")!=-1) {
				 fianlDataDictId=tableDomainName+"."+domainName;
			 }else {
				 fianlDataDictId=dataDict;
			 }
			 domainFieldValid+=(",dataDict=\""+fianlDataDictId+"\"");
		 }
		 return domainFieldValid;
	}
}
