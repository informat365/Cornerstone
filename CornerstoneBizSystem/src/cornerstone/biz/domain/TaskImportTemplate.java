package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.NonPersistent;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 任务导入模板
 * 
 * @author 杜展扬 2018-08-11
 *
 */
@DomainDefine(domainClass = TaskImportTemplate.class)
@DomainDefineValid(comment ="任务导入模板" )
public class TaskImportTemplate extends BaseDomain{
    //
	public int companyId;
	
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;
    
    @DomainFieldValid(comment="对象类型",required=true,canUpdate=true)
    public int objectType;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=128)
    public String name;
    
    @DomainFieldValid(comment="字段列表",required=true,canUpdate=true,maxValue=512)
    public List<Integer> fields;
    
    @DomainFieldValid(comment="remark",canUpdate=true,maxValue=512)
    public String remark;
    
    //
    //   
    public static class TaskImportTemplateInfo extends TaskImportTemplate{
    //
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="imageId")
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageid;
    
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="name")
        @DomainFieldValid(comment = "创建人名称")
        public String 创建人;
    

    }
    //
    //   
    @QueryDefine(domainClass=TaskImportTemplateInfo.class)
    public static class TaskImportTemplateQuery extends BizQuery{
        //
        public Integer id;

        public Integer projectId;

        public Integer objectType;

        public Integer createAccountId;

        public String name;

        public String fields;

        public String remark;

        @QueryField(operator=">=",field="createTime")
        public Date createTimeStart;
        
        @QueryField(operator="<=",field="createTime")
        public Date createTimeEnd;

        @QueryField(operator=">=",field="updateTime")
        public Date updateTimeStart;
        
        @QueryField(operator="<=",field="updateTime")
        public Date updateTimeEnd;

        //in or not in fields

        //ForeignQueryFields
        @QueryField(foreignKeyFields="createAccountId",field="imageId")
        public String createAccountImageid;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String 创建人;
        
        //inner joins
        //sort
        public int idSort;
        public int projectIdSort;
        public int objectTypeSort;
        public int createAccountIdSort;
        public int createAccountImageidSort;
        public int 创建人Sort;
        public int nameSort;
        public int fieldsSort;
        public int remarkSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}