package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

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
 * 文件
 * 
 * @author 杜展扬 2018-08-04
 *
 */
@DomainDefine(domainClass = File.class)
@DomainDefineValid(comment ="文件" ,uniqueKeys={@UniqueKey(fields={"uuid"})})
public class File extends BaseDomain{
    //
	@ForeignKey(domainClass=Company.class)
    public int companyId;
	
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;
    
    @DomainFieldValid(comment="附件id",required=true,canUpdate=true,maxValue=64)
    public String uuid;
    
    @DomainFieldValid(comment="附件名",required=true,canUpdate=true,maxValue=256)
    public String name;
    
    @DomainFieldValid(comment="附件大小",required=true,canUpdate=true)
    public long size;
    
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="创建人",canUpdate=true,maxValue=64)
    public String createAccountName;
    
    @DomainFieldValid(comment="创建人头像",canUpdate=true,maxValue=64)
    public String createAccountImageId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    @DomainFieldValid(comment="更新人",canUpdate=true,maxValue=64)
    public String updateAccountName;
    
    @DomainFieldValid(comment="更新人头像",canUpdate=true,maxValue=64)
    public String updateAccountImageId;
    
    @DomainFieldValid(comment="是否目录",required=true,canUpdate=true)
    public boolean isDirectory;
    
    @ForeignKey(domainClass=File.class)
    @DomainFieldValid(comment="父目录",required=true,canUpdate=true)
    public int parentId;
    
    @DomainFieldValid(comment="层级",required=true,canUpdate=true)//从1开始
    public int level;
    
    @DomainFieldValid(comment="是否删除",required=true,canUpdate=true)
    public boolean isDelete;
    
    public boolean isCreateIndex;
    
    @DomainFieldValid(comment="授权角色",canUpdate=true,maxValue=512)
    public List<Integer> roles;
    
    @DomainFieldValid(comment="是否启用授权角色",canUpdate=true,maxValue=512)
    public boolean enableRole;
    
    @DomainFieldValid(comment="颜色",canUpdate=true,maxValue=128)
    public String color;
    
    @DomainFieldValid(comment="完整路径",canUpdate=true,maxValue=512)//  /A/B/123
    public String path;
    //
    //   
    public static class FileInfo extends File{
    		//
    		@NonPersistent
        @DomainField(foreignKeyFields="projectId",field="name")
        public String projectName;
    		
    		@NonPersistent
        @DomainField(foreignKeyFields="projectId",field="uuid")
        public String projectUuid;
    }
    //
    //   
    @QueryDefine(domainClass=FileInfo.class)
    public static class FileQuery extends BizQuery{
        //
        public Integer id;
        
        public Integer companyId;

        public Integer projectId;

        public Integer onlineDocumentId;

        public String uuid;

        public String name;

        public Long size;

        public Integer createAccountId;

        public String createAccountName;

        public String createAccountImageId;

        public Integer updateAccountId;

        public String updateAccountName;

        public String updateAccountImageId;

        public Boolean isDirectory;

        public Integer parentId;

        public Integer level;
        
        public Boolean isDelete;
        
        public Boolean isCreateIndex;

        @QueryField(operator=">=",field="createTime")
        public Date createTimeStart;
        
        @QueryField(operator="<=",field="createTime")
        public Date createTimeEnd;

        @QueryField(operator=">=",field="updateTime")
        public Date updateTimeStart;
        
        @QueryField(operator="<=",field="updateTime")
        public Date updateTimeEnd;

        //in or not in fields
        @QueryField(operator="in",field="id")
        public int[] idInList;
        
        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int projectIdSort;
        public int onlineDocumentIdSort;
        public int uuidSort;
        public int nameSort;
        public int sizeSort;
        public int createAccountIdSort;
        public int createAccountNameSort;
        public int createAccountImageIdSort;
        public int updateAccountIdSort;
        public int updateAccountNameSort;
        public int updateAccountImageIdSort;
        public int isDirectorySort;
        public int parentIdSort;
        public int levelSort;
        public int isDeleteSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}