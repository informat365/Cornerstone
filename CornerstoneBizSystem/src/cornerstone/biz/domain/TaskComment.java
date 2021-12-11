package cornerstone.biz.domain;

import java.util.Date;

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
 * 任务评论
 * 
 * @author 杜展扬 2018-07-31
 *
 */
@DomainDefine(domainClass = TaskComment.class)
@DomainDefineValid(comment ="任务评论" )
public class TaskComment extends BaseDomain{
    //
    public int companyId;
	
    @ForeignKey(domainClass=Task.class)
    @DomainFieldValid(comment="任务",required=true,canUpdate=true)
    public int taskId;
    
    @DomainFieldValid(comment="评论",required=true,canUpdate=true)
    public String comment;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="是否删除",required=true,canUpdate=true)
    public boolean isDelete;
    
    //
    //   
    public static class TaskCommentInfo extends TaskComment{
    //
        @NonPersistent
        @DomainField(foreignKeyFields="taskId",field="name")
        @DomainFieldValid(comment = "任务名称")
        public String taskName;
    
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="name")
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
        
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="userName")
        @DomainFieldValid(comment = "创建人用户名")
        public String createAccountUserName;
        
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="imageId")
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
    

    }
    //
    //   
    @QueryDefine(domainClass=TaskCommentInfo.class)
    public static class TaskCommentQuery extends BizQuery{
        //
        public Integer id;

        public Integer taskId;

        public String comment;

        public Integer createAccountId;

        public Boolean isDelete;

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
        @QueryField(foreignKeyFields="taskId",field="name")
        public String taskName;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int taskIdSort;
        public int taskNameSort;
        public int commentSort;
        public int createAccountIdSort;
        public int createAccountNameSort;
        public int isDeleteSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}