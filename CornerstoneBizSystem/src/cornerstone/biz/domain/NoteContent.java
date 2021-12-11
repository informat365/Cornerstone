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
 * 笔记内容
 * 
 * @author 杜展扬 2019-07-01 11:25
 *
 */
@DomainDefine(domainClass = NoteContent.class)
@DomainDefineValid(comment ="笔记内容" ,uniqueKeys={@UniqueKey(fields={"nodeId"})})
public class NoteContent extends BaseDomain{
    //
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @ForeignKey(domainClass=Note.class)
    @DomainFieldValid(comment="笔记",required=true,canUpdate=true)
    public int noteId;
    
    @DomainFieldValid(comment="正文",canUpdate=true)
    public String content;
    
    //
    //   
    public static class NoteContentInfo extends NoteContent{
    //

    }
    //
    //   
    @QueryDefine(domainClass=NoteContentInfo.class)
    public static class NoteContentQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;

        public Integer nodeId;

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
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int nodeIdSort;
        public int contentSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}