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
 * 项目打包文件
 * 
 * @author 杜展扬 2018-09-03 19:23
 *
 */
@DomainDefine(domainClass = ProjectArtifact.class)
@DomainDefineValid(comment ="项目打包文件" ,uniqueKeys={@UniqueKey(fields={"projectId","name","version"}),
		@UniqueKey(fields={"uuid"})})
public class ProjectArtifact extends BaseDomain{
    //
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=64)
    public String name;
    
    @DomainFieldValid(comment="uuid",required=true,canUpdate=true,maxValue=64)
    public String uuid;
    
    @DomainFieldValid(comment="版本号",required=true,canUpdate=true,maxValue=32)
    public String version;
    
    @DomainFieldValid(comment="大小",required=true,canUpdate=true)
    public long size;
    
    @DomainFieldValid(comment="md5",required=true,canUpdate=true)
    public String md5;
    
    @DomainFieldValid(comment="原始名称",required=true,canUpdate=true)
    public String originalName;
    
    @DomainFieldValid(comment="是否已删除",required=true,canUpdate=true)
    public boolean isDelete;
    
    @DomainFieldValid(comment="文件是否已删除",required=true,canUpdate=true)
    public boolean isFileDelete;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class ProjectArtifactInfo extends ProjectArtifact{
    //
    	 	 @NonPersistent
         @DomainField(foreignKeyFields="createAccountId",field="name")
         @DomainFieldValid(comment = "创建人")
         public String createAccountName;
         
         @NonPersistent
         @DomainField(foreignKeyFields="createAccountId",field="imageId")
         @DomainFieldValid(comment = "创建人头像")
         public String createAccountImageId;
    }
    //
    //   
    @QueryDefine(domainClass=ProjectArtifactInfo.class)
    public static class ProjectArtifactQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;

        public Integer projectId;
        
        public String uuid;

        public String name;

        public String version;
        
        public Boolean isDelete;

        public Long size;
        
        public String md5;

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

        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int uuidSort;
        public int companyIdSort;
        public int projectIdSort;
        public int nameSort;
        public int versionSort;
        public int sizeSort;
        public int md5Sort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}