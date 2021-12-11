package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
@DomainDefine(domainClass = ProjectFieldDefine.class)
@DomainDefineValid(comment ="项目字段定义" ,uniqueKeys={@UniqueKey(fields={"projectId","objectType","name"})})
public class ProjectFieldDefine extends BaseDomain{
    //
    public static final int TYPE_单行文本框 = 1;
    public static final int TYPE_复选框 = 3;//List<String>
    public static final int TYPE_单选框 = 4;
    public static final int TYPE_团队成员选择 = 6;//List<AccountSimpleInfo>  这个比较特殊 会有两个列表fieldobject_2255,field_2255
    public static final int TYPE_日期 = 7;//long型  时间戳格式 只有年月日
    public static final int TYPE_数值 = 8;

    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;
    
    @DomainFieldValid(comment="对象类型",required=true,canUpdate=true)
    public int objectType;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=64)
    public String name;
    
    @DomainFieldValid(comment="字段名",canUpdate=true,maxValue=32)
    public String field;
    
    @DomainFieldValid(comment="字段类型",canUpdate=true,dataDict="ProjectFieldDefine.type")
    public int type;
    
    @DomainFieldValid(comment="是否必选",required=true,canUpdate=true)//比如是任务模型 如果isRequired为true 表示添加任务时  这个字段必选
    public boolean isRequired;

    @DomainFieldValid(comment="是否显示",required=true,canUpdate=true)
    public boolean isShow;

    @DomainFieldValid(comment="是否唯一",required=true,canUpdate=true)
    public boolean isUnique;
    
    @DomainFieldValid(comment="是否必须显示",required=true,canUpdate=true)
    public boolean isRequiredShow;
    
    @DomainFieldValid(comment="是否是系统字段",required=true,canUpdate=true)
    public boolean isSystemField;
    
    @DomainFieldValid(comment="是否是项目集字段",required=true,canUpdate=true)
    public boolean isPsField;

    @DomainFieldValid(comment="取值范围",canUpdate=true,maxValue=512)
    public List<String> valueRange;
    
    @DomainFieldValid(comment="数据字典",canUpdate=true,maxValue=64)
    public String dataDict;
    
    @DomainFieldValid(comment="排序字段",canUpdate=true)
    public int sortWeight;
    
    @DomainFieldValid(comment="是否显示时间",canUpdate=true)
    public boolean showTimeField;
    
    @DomainFieldValid(comment="备注",canUpdate=true,maxValue=512)
    public String remark;
    
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class ProjectFieldDefineInfo extends ProjectFieldDefine{
    //

    }
    //
    //   
    @QueryDefine(domainClass=ProjectFieldDefineInfo.class)
    public static class ProjectFieldDefineQuery extends BizQuery{
        //
        public Integer id;
        
        public Integer companyId;

        public Integer projectId;

        public Integer objectType;

        public String name;

        public String field;

        public Integer type;

        public Boolean isRequired;

        public Boolean isShow;

        public Boolean isRequiredShow;

        public Boolean isSystemField;

        public String valueRange;

        public String dataDict;

        public Integer sortWeight;

        public String remark;

        public Integer createAccountId;

        public Integer updateAccountId;

        @QueryField(operator=">=",field="createTime")
        public Date createTimeStart;
        
        @QueryField(operator="<=",field="createTime")
        public Date createTimeEnd;

        @QueryField(operator=">=",field="updateTime")
        public Date updateTimeStart;
        
        @QueryField(operator="<=",field="updateTime")
        public Date updateTimeEnd;

        //in or not in fields
        @QueryField(operator="in",field="type")
        public int[] typeInList;
        
        @QueryField(operator="not in",field="type")
        public int[] typeNotInList;
        
        @QueryField(operator="in",field="id")
        public int[] idInList;
        

        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int projectIdSort;
        public int objectTypeSort;
        public int nameSort;
        public int fieldSort;
        public int typeSort;
        public int isRequiredSort;
        public int isShowSort;
        public int isRequiredShowSort;
        public int isSystemFieldSort;
        public int valueRangeSort;
        public int dataDictSort;
        public int sortWeightSort;
        public int remarkSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}