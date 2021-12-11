package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 笔记标签
 * 
 * @author 杜展扬 2019-07-01 11:25
 *
 */
@DomainDefine(domainClass = NoteTag.class)
@DomainDefineValid(comment ="笔记标签" ,uniqueKeys={@UniqueKey(fields={"name","createAccountId"})})
public class NoteTag extends BaseDomain{
    //
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=64,needTrim=true)
    public String name;
    
    @DomainFieldValid(comment="颜色",canUpdate=true,maxValue=32)
    public String color;
    
    @DomainFieldValid(comment="备注",canUpdate=true,maxValue=512)
    public String remark;
    
    @DomainFieldValid(comment="排序",canUpdate=true)
    public int sortWeight;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class NoteTagInfo extends NoteTag{
    //
        @DomainField(foreignKeyFields="createAccountId",field="imageId",persistent=false)
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
    
        @DomainField(foreignKeyFields="createAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
    

    }
    //
    //   
    @QueryDefine(domainClass=NoteTagInfo.class)
    public static class NoteTagQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;

        public String name;

        public String color;

        public String remark;

        public Integer sortWeight;

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
        @QueryField(field="id")
        public int[] idInList;

        //ForeignQueryFields
        @QueryField(foreignKeyFields="createAccountId",field="imageId")
        public String createAccountImageId;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int nameSort;
        public int colorSort;
        public int remarkSort;
        public int sortWeightSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}