package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.NonPersistent;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 项目子系统
 * 
 * @author 杜展扬 2018-07-31
 *
 */
@DomainDefine(domainClass = ProjectSubSystem.class)
@DomainDefineValid(comment ="项目子系统" ,uniqueKeys={@UniqueKey(fields={"projectId","name"})})
public class ProjectSubSystem extends BaseDomain{
    //
    public static final int STATUS_运行中 = 1;
    public static final int STATUS_已下线 = 2;
    
    public int companyId;
    //
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=128)
    public String name;
    
    @DomainFieldValid(comment="状态",canUpdate=true,dataDict="ProjectSubSystem.status")
    public int status;
    
    @DomainFieldValid(comment="描述",canUpdate=true)
    public String description;
    
    @DomainFieldValid(comment="是否被删除",canUpdate=true)
    public boolean isDelete;
    
    @DomainFieldValid(comment="原始名称，用于删除后恢复",required=false,canUpdate=true,maxValue=128)
    public String originalName;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class ProjectSubSystemInfo extends ProjectSubSystem{
    //
        @NonPersistent
        @DomainField(foreignKeyFields="projectId",field="companyId")
        @DomainFieldValid(comment = "项目企业")
        public int companyId;
    
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="name")
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
        
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="imageId")
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
    }
    //
    //   
    @QueryDefine(domainClass=ProjectSubSystemInfo.class)
    public static class ProjectSubSystemQuery extends BizQuery{
        //
        public Integer id;

        public Integer projectId;

        public String name;

        public Integer status;

        public String description;
        
        public Boolean isDelete;

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
        @QueryField(operator="in",field="status")
        public int[] statusInList;
        
        @QueryField(operator="not in",field="status")
        public int[] statusNotInList;
        

        //ForeignQueryFields
        @QueryField(foreignKeyFields="projectId",field="companyId")
        public Integer companyId;
        
        //inner joins
        //sort
        public int idSort;
        public int projectIdSort;
        public int companyIdSort;
        public int nameSort;
        public int statusSort;
        public int descriptionSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}