package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;

/**
 * 笔记
 * 
 * @author 杜展扬 2019-07-01 11:25
 *
 */
@DomainDefine(domainClass = Note.class)
@DomainDefineValid(comment = "笔记")
public class Note extends BaseDomain {
	//
	@DomainFieldValid(comment = "uuid", required = true, maxValue=80)
	public String uuid;
	
	@ForeignKey(domainClass = Company.class)
	@DomainFieldValid(comment = "企业", required = true)
	public int companyId;

	@DomainFieldValid(comment = "标题", required = true, maxValue = 128)
	public String title;

	@DomainFieldValid(comment = "是否删除", required = true)
	public boolean isDelete;

	@DomainFieldValid(comment = "标签列表", maxValue = 512)
	public List<Integer> tagIdList;
	
	@DomainFieldValid(comment = "是否建立索引", required = true)
	public boolean isCreateIndex;

	@ForeignKey(domainClass = Account.class)
	@DomainFieldValid(comment = "创建人", required = true)
	public int createAccountId;

	@DomainFieldValid(comment = "更新人", required = true)
	public int updateAccountId;

	//
	//
	public static class NoteInfo extends Note {
		//
		@DomainField(foreignKeyFields = "createAccountId", field = "imageId", persistent = false)
		@DomainFieldValid(comment = "创建人头像")
		public String createAccountImageId;

		@DomainField(foreignKeyFields = "createAccountId", field = "name", persistent = false)
		@DomainFieldValid(comment = "创建人名称")
		public String createAccountName;
		
		//
		@DomainField(ignoreWhenSelect=true, persistent = false)
		@DomainFieldValid(comment = "正文")
		public String content;
		
		@DomainField(ignoreWhenSelect=true, persistent = false)
		@DomainFieldValid(comment = "标签列表")
		public List<String> tagList;
		
		@DomainField(ignoreWhenSelect=true, persistent = false)
		@DomainFieldValid(comment = "标签详情列表")
		public List<NoteTag> tagInfoList;
	}

	//
	//
	@QueryDefine(domainClass = NoteInfo.class)
	public static class NoteQuery extends BizQuery {
		//
		public Integer id;

		public Integer companyId;
		
		public String uuid;

		public String title;

		public Boolean isDelete;
		
		public Boolean isCreateIndex;

		public Integer createAccountId;

		public Integer updateAccountId;

		@QueryField(operator = ">=", field = "createTime")
		public Date createTimeStart;

		@QueryField(operator = "<=", field = "createTime")
		public Date createTimeEnd;

		@QueryField(operator = ">=", field = "updateTime")
		public Date updateTimeStart;

		@QueryField(operator = "<=", field = "updateTime")
		public Date updateTimeEnd;

		// in or not in fields

		// ForeignQueryFields
		@QueryField(foreignKeyFields = "createAccountId", field = "imageId")
		public String createAccountImageId;

		@QueryField(foreignKeyFields = "createAccountId", field = "name")
		public String createAccountName;

		@QueryField(whereSql="json_contains(a.tag_id_list,'${noteTagId}')")
        public Integer noteTagId;
		
//		@InnerJoin(table2=NoteContent.class,table2Field="noteId")
//		@QueryField(field="content")
		@QueryField(ingore=true)
		public String keyword;
		
		@QueryField(ingore=true)
		public int[] tagIdList;
		
		// inner joins
		// sort
		public int idSort;
		public int companyIdSort;
		public int titleSort;
		public int isDeleteSort;
		public int tagIdListSort;
		public int createAccountIdSort;
		public int createAccountImageIdSort;
		public int createAccountNameSort;
		public int updateAccountIdSort;
		public int createTimeSort;
		public int updateTimeSort;
	}

}