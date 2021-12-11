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
 * 汇报内容
 * 
 * @author 杜展扬 2019-01-14 18:02
 *
 */
@DomainDefine(domainClass = ReportContent.class)
@DomainDefineValid(comment ="汇报内容" )
public class ReportContent extends BaseDomain{
    //
	public static final int TYPE_汇报内容 = 1;
	public static final int TYPE_评论 = 2;
	
	public int companyId;
    //
    @ForeignKey(domainClass=Report.class)
    @DomainFieldValid(comment="汇报",required=true,canUpdate=true)
    public int reportId;
    
    public int type;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="汇报内容",canUpdate=true)
    public String title;
    
    @DomainFieldValid(comment="内容",required=true,canUpdate=true,needTrim=true)
    public String content;
    
    //
    //   
    public static class ReportContentInfo extends ReportContent{
    //
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="imageId")
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
    
        @NonPersistent
        @DomainField(foreignKeyFields="createAccountId",field="name")
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
    

    }
    //
    //   
    @QueryDefine(domainClass=ReportContentInfo.class)
    public static class ReportContentQuery extends BizQuery{
        //
        public Integer id;

        public Integer reportId;
        
        public Integer type;

        public Integer createAccountId;
        
        public String title;

        public String content;

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
        public String createAccountImageId;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int reportIdSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int createAccountNameSort;
        public int contentSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}