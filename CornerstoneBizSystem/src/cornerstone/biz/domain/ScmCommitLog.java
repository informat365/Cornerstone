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
 * 版本管理软件提交日志
 * 
 * @author 杜展扬 2018-08-17
 * 
 *
 */
@DomainDefine(domainClass = ScmCommitLog.class)
@DomainDefineValid(comment ="版本管理软件提交日志" ,uniqueKeys={@UniqueKey(fields={"uuid"})})
public class ScmCommitLog extends BaseDomain{
    //
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @ForeignKey(domainClass=ScmToken.class)
    public int scmTokenId;
    
    @DomainFieldValid(comment="UUID",required=true,canUpdate=true,maxValue=64)
    public String uuid;
    
    @DomainFieldValid(comment="修改的文件和属性的区别",canUpdate=true,maxValue=60*1024)
    public String diff;
    
    @DomainFieldValid(comment="修改的路径",canUpdate=true,maxValue=60*1024)
    public String changed;
    
    @DomainFieldValid(comment="版本库",canUpdate=true,maxValue=256)
    public String repo;

    @DomainFieldValid(comment="提交版本",canUpdate=true,maxValue=64)
    public String version;
    
    @DomainFieldValid(comment="加多少行代码",canUpdate=true)
    public int addLineNum;

    @DomainFieldValid(comment="减多少行代码",canUpdate=true)
    public int decreaseLineNum;
    
    @DomainFieldValid(comment="作者",canUpdate=true,maxValue=64)
    public String author;
    
    @DomainFieldValid(comment="提交时间",canUpdate=true,maxValue=128)
    public String commitTime;
    
    @DomainFieldValid(comment="提交注释",canUpdate=true,maxValue=512)
    public String comment;

    @DomainFieldValid(comment="分支",canUpdate=true,maxValue=64)
    public String branch;

    @DomainFieldValid(comment="作者邮箱",canUpdate=true,maxValue=64)
    public String email;
    //
    //   
    public static class ScmCommitLogInfo extends ScmCommitLog{
    //
        @NonPersistent
        @DomainField(foreignKeyFields="companyId",field="name")
        @DomainFieldValid(comment = "企业名称")
        public String companyName;
    

    }
    //
    //   
    @QueryDefine(domainClass=ScmCommitLogInfo.class)
    public static class ScmCommitLogQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;
        
        public Integer scmTokenId;

        public String uuid;
        
        public String repo;
        
        public String version;

        public String diff;
        
        public String changed;

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
        @QueryField(foreignKeyFields="companyId",field="name")
        public String companyName;
        
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int companyNameSort;
        public int uuidSort;
        public int contentSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}