package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 附件关联
 * 
 * @author 杜展扬 2018-08-18
 *
 */
@DomainDefine(domainClass = AttachmentAssociated.class)
@DomainDefineValid(comment ="附件关联" ,uniqueKeys={@UniqueKey(fields={"attachmentId","projectId","taskId"})})
public class AttachmentAssociated extends BaseDomain{

    public static final int TYPE_文件=1;
    public static final int TYPE_WIKI=2;
    //
	@ForeignKey(domainClass=Company.class)
	public int companyId;
    //
    @ForeignKey(domainClass=Attachment.class)
    @DomainFieldValid(comment="附件",required=true,canUpdate=true)
    public int attachmentId;

    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;
    
    @ForeignKey(domainClass=Task.class)
    @DomainFieldValid(comment="任务",required=true,canUpdate=true)
    public int taskId;
    
    //
    //   
    public static class AttachmentAssociatedInfo extends AttachmentAssociated{
    //

    }
    //
    //   
    @QueryDefine(domainClass=AttachmentAssociatedInfo.class)
    public static class AttachmentAssociatedQuery extends BizQuery{
        //
        public Integer id;
        
        public Integer companyId;

        public Integer attachmentId;

        public Integer projectId;

        public Integer taskId;

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
        public int attachmentIdSort;
        public int projectIdSort;
        public int taskIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}