package cornerstone.biz.action;

import java.util.*;

import cornerstone.biz.annotations.ApiDefine;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;

import cornerstone.biz.action.CommAction.CommActionImpl;
import cornerstone.biz.dao.BizDAO;
import cornerstone.biz.domain.Account;
import cornerstone.biz.domain.FilterCondition;
import cornerstone.biz.domain.Note;
import cornerstone.biz.domain.Note.NoteInfo;
import cornerstone.biz.domain.Note.NoteQuery;
import cornerstone.biz.domain.NoteContent;
import cornerstone.biz.domain.NoteTag;
import cornerstone.biz.domain.NoteTag.NoteTagInfo;
import cornerstone.biz.domain.NoteTag.NoteTagQuery;
import cornerstone.biz.lucene.LuceneService;
import cornerstone.biz.lucene.SearchDocument;
import cornerstone.biz.lucene.SearchException;
import cornerstone.biz.srv.NoteService;
import cornerstone.biz.util.BizUtil;
import cornerstone.biz.util.SqlUtils;
import cornerstone.biz.util.StringUtil;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;
import jazmin.driver.jdbc.Transaction;
import jazmin.driver.jdbc.smartjdbc.Query;
import jazmin.driver.jdbc.smartjdbc.SqlBean;
import jazmin.driver.jdbc.smartjdbc.provider.SelectProvider;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.rpc.RpcService;
import jazmin.util.DumpUtil;

@ApiDefine("笔记接口")
public interface NoteAction {

	@ApiDefine(value = "通过UUID查询笔记" ,resp = "笔记信息",params={"登录TOKEN","用户UUID"})
	NoteInfo getNoteByUuid(String token, String uuid);

	@ApiDefine(value = "通过UUID查询笔记" ,resp = "笔记信息",params={"用户UUID"})
	NoteInfo getNoteByShareUuid(String uuid);

	@ApiDefine(value = "查询笔记列表和总数" ,resp = "Map",params={"登录TOKEN","笔记查询类"})
	Map<String, Object> getNoteList(String token,NoteQuery query);

	@ApiDefine(value = "新增笔记" ,resp = "笔记UUID",params={"登录TOKEN","笔记信息"})
	String addNote(String token, NoteInfo bean);

	@ApiDefine(value = "编辑笔记" ,params={"登录TOKEN","笔记信息"})
	void updateNote(String token, NoteInfo bean);

	@ApiDefine(value = "删除笔记" ,params={"登录TOKEN","笔记信息编号"})
	void deleteNote(String token, int id);

	@ApiDefine(value = "更新笔记标签" ,params={"登录TOKEN","笔记信息编号","标签集合"})
	void updateNoteTags(String token, int noteId,List<Integer> tagIdList);

	@ApiDefine(value = "通过ID查询笔记标签",resp = "标签信息",params={"登录TOKEN","标签编号"})
	NoteTagInfo getNoteTagById(String token, int id);

	@ApiDefine(value = "查询笔记标签列表和总数" ,params={"登录TOKEN","标签查询类"})
	Map<String, Object> getNoteTagList(String token,NoteTagQuery query);

	@ApiDefine(value = "查询我的笔记标签列表" ,resp = "标签集合列表",params={"登录TOKEN"})
	List<NoteTag> getMyNoteTagList(String token);

	@ApiDefine(value = "新增笔记标签" ,resp = "标签编号",params={"登录TOKEN","标签信息"})
	int addNoteTag(String token, NoteTagInfo bean);

	@ApiDefine(value = "编辑笔记标签" ,params={"登录TOKEN","标签信息"})
	void updateNoteTag(String token, NoteTagInfo bean);

	@ApiDefine(value = "删除笔记标签" ,params={"登录TOKEN","标签编号"})
	void deleteNoteTag(String token, int id);
	//
	@RpcService
	class NoteActionImpl extends CommActionImpl implements NoteAction {
		//
		@AutoWired
		BizDAO dao;
		@AutoWired
		NoteService noteService;
		@AutoWired
		LuceneService luceneService;
		//
		private static Logger logger = LoggerFactory.get(NoteActionImpl.class);
		//
		@Override
		public NoteInfo getNoteByUuid(String token, String uuid){
			Account account=noteService.getExistedAccountByToken(token);
			NoteInfo info=dao.getExistedByUuid(NoteInfo.class, uuid);
			checkAccountPermission(account,info.createAccountId);
			return getNoteByUuid0(info);
		}
		//
		@Override
		public NoteInfo getNoteByShareUuid(String uuid){
			NoteInfo info=dao.getExistedByUuid(NoteInfo.class, uuid);
			return getNoteByUuid0(info);
		}

		private NoteInfo getNoteByUuid0(NoteInfo info){
			info.content=dao.getNoteContentByNoteId(info.id);
			if(info.isDelete) {
				throw new AppException("笔记不存在");
			}
			if(info.tagIdList!=null&&info.tagIdList.size()>0) {
				NoteTagQuery query=new NoteTagQuery();
				query.idInList=BizUtil.convertList(info.tagIdList);
				info.tagInfoList=dao.getList(query);
			}else {
				info.tagInfoList=new ArrayList<>();
			}
		    return info;
		}

		/**查询笔记列表和总数*/
		@Override
		public Map<String, Object> getNoteList(String token,NoteQuery query){
			Account account=noteService.getExistedAccountByToken(token);
			setupQuery(account, query);
			query.createAccountId=account.id;
			query.updateTimeSort=Query.SORT_TYPE_DESC;
			//
			StringBuffer addWhereSql=new StringBuffer();
//			if(!StringUtil.isEmpty(query.keyword)) {//全文检索
//				List<Integer> idList=searchNotes(account, query.keyword, 100);
//				if(idList.isEmpty()) {
//					return createResult(new ArrayList<>(), 0);
//				}
//				addWhereSql.append(" and a.id in(\n");
//				for (Integer id : idList) {
//					addWhereSql.append(id).append(",");
//				}
//				addWhereSql.deleteCharAt(addWhereSql.length()-1);
//				addWhereSql.append(")\n");
//			}
			//
			if(!BizUtil.isNullOrEmpty(query.tagIdList)) {
				addWhereSql.append(" and ( ");
				addWhereSql.append(SqlUtils.makeJsonContainsSql("tag_id_list",
						query.tagIdList, FilterCondition.OPERATOR_等于));
				addWhereSql.append(" ) \n");
			}
			//
			SqlBean sqlBean=new SelectProvider(NoteInfo.class).query(query).needPaging(true).build();
			//
			if(!StringUtil.isEmpty(query.keyword)) {//like
				String[] keywords=query.keyword.split(" ");
				if(keywords.length>0) {
					List<String> kewordList=new ArrayList<>();
					for (String keyword : keywords) {
						if(StringUtil.isEmptyWithTrim(keyword)) {
							continue;
						}
						kewordList.add(keyword.trim());
					}
					if(!kewordList.isEmpty()) {
						List<Object> addValues=new ArrayList<>();
						addWhereSql.append("and (\n");
						int index=0;
						for (String key : kewordList) {
							if(index>0) {
								addWhereSql.append("or ");
							}
							addWhereSql.append(" a.title like concat('%',?,'%') or in1.content like concat('%',?,'%') ");
							addValues.add(key);
							addValues.add(key);
							index++;
						}
						addWhereSql.append("\n)\n");
						sqlBean.fromSql=sqlBean.fromSql+"inner join t_note_content in1 on a.`id`=in1.note_id\n";
						if(addValues.size() > 0) {
							List<Object> newValues=new ArrayList<>();
							if(!BizUtil.isNullOrEmpty(sqlBean.parameters)) {
								newValues.addAll(Arrays.asList(sqlBean.parameters));
							}
							newValues.addAll(addValues);
							sqlBean.parameters=newValues.toArray();
						}
					}
				}
			}
			sqlBean.whereSql+=addWhereSql.toString();
			//
			List<NoteInfo> list=dao.queryList(NoteInfo.class,sqlBean.toSql(),sqlBean.parameters);
			sqlBean.selectSql="select count(1)";
			sqlBean.orderBySql="";
			sqlBean.limitSql="";
			int count=dao.queryCount(sqlBean.toSql(), sqlBean.parameters);
			//
		    return createResult(list, count);
		}

		/**
		 *
		 * @param account
		 * @param keyword
		 * @param pageSize
		 * @return
		 */
		private List<Integer> searchNotes(Account account,String keyword,int pageSize){
			List<Integer> list=new ArrayList<>();
			try {
				Map<String,Float> boosts=new HashMap<>();//权重
				boosts.put("name", 2f);
				boosts.put("content", 1f);
				String pathKey=account.companyId+"-"+account.id;
				String indexPath=BizUtil.getIndexPath(pathKey);
				BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
				String[] fieldList = new String[]{
						"title","content"
				};
				org.apache.lucene.search.Query keywordQuery=luceneService.createQuery(fieldList, keyword, boosts);
				booleanQueryBuilder.add(keywordQuery, Occur.MUST);
				//
				BooleanQuery finalQuery=booleanQueryBuilder.build();
				List<SearchDocument> result=luceneService.searchDocuments(indexPath, finalQuery, pageSize);
				if(logger.isDebugEnabled()) {
					logger.debug("searchNotes accountId:{} pathKey:{} keyword:{} projectIds:{} \nresult:{}",account.id,
							pathKey,keyword,DumpUtil.dump(result));
				}
				if(result==null||result.isEmpty()) {
					return new ArrayList<>();
				}
				for (SearchDocument e : result) {
					list.add(Integer.valueOf(e.get("id")));
				}
			} catch (SearchException e1) {
				logger.error(e1.getMessage(),e1);
			}
			return list;
		}

		/**新增笔记*/
		@Transaction
		@Override
		public String addNote(String token, NoteInfo bean) {
			Account account=noteService.getExistedAccountByToken(token);
			bean.createAccountId=account.id;
			bean.companyId=account.companyId;
			bean.uuid=BizUtil.randomUUID();
			checkValid(bean);
			dao.add(bean);
			//
			NoteContent content=new NoteContent();
			content.companyId=account.companyId;
			content.noteId=bean.id;
			content.content=bizService.getContent(bean.content);
			dao.add(content);
			//
			return bean.uuid;
		}

		/**编辑笔记*/
		@Transaction
		@Override
		public void updateNote(String token, NoteInfo bean) {
			Account account=noteService.getExistedAccountByToken(token);
			Note old=dao.getExistedByIdForUpdate(Note.class, bean.id);
			checkAccountPermission(account, old.createAccountId);
			old.title=bean.title;
			old.updateAccountId=account.id;
			checkValid(old);
			dao.update(old);
			//
			String content=bizService.getContent(bean.content);
			dao.updateNoteContentByNoteId(bean.id,content);
		}

		@Transaction
		@Override
		public void updateNoteTags(String token, int noteId,List<Integer> tagIdList) {
			Account account=noteService.getExistedAccountByToken(token);
			if(tagIdList!=null) {
				if(tagIdList.size()>100) {
					throw new AppException("一篇笔记不能超过100个标签");
				}
				for (Integer tagId : tagIdList) {
					NoteTag tag=dao.getExistedById(NoteTag.class, tagId);
					checkAccountPermission(account, tag.createAccountId);
				}
			}
			Note note=dao.getExistedByIdForUpdate(Note.class, noteId);
			checkAccountPermission(account, note.createAccountId);
			note.tagIdList=tagIdList;
			note.updateAccountId=account.id;
			dao.update(note);
		}

		/**删除笔记*/
		@Transaction
		@Override
		public void deleteNote(String token, int id) {
			Account account=noteService.getExistedAccountByToken(token);
			Note old=dao.getExistedByIdForUpdate(Note.class, id);
			checkAccountPermission(account, old.createAccountId);
			if(old.isDelete) {
				return;
			}
			old.isDelete=true;
			dao.update(old);
		}
		//

		/**通过ID查询笔记标签*/
		@Override
		public NoteTagInfo getNoteTagById(String token, int id){
			Account account=noteService.getExistedAccountByToken(token);
			NoteTagInfo info=dao.getExistedById(NoteTagInfo.class, id);
			checkAccountPermission(account, info.createAccountId);
			return info;
		}

		/**查询笔记标签列表和总数*/
		@Override
		public Map<String, Object> getNoteTagList(String token,NoteTagQuery query){
			Account account=noteService.getExistedAccountByToken(token);
			setupQuery(account, query);
			query.createAccountId=account.id;
		    return createResult(dao.getList(query), dao.getListCount(query));
		}

		/**
		 * 查询我的标签列表
		 */
		@Override
		public List<NoteTag> getMyNoteTagList(String token){
			Account account=noteService.getExistedAccountByToken(token);
			NoteTagQuery query=new NoteTagQuery();
			setupQuery(account, query);
			query.pageSize=Integer.MAX_VALUE;
			query.createAccountId=account.id;
			return dao.getList(query);
		}

		/**新增笔记标签*/
		@Transaction
		@Override
		public int addNoteTag(String token, NoteTagInfo bean) {
			Account account=noteService.getExistedAccountByToken(token);
			bean.createAccountId=account.id;
			bean.companyId=account.companyId;
			checkValid(bean);
			checkUniqueKeysOnAdd(dao, bean);
			dao.add(bean);
			return bean.id;
		}

		/**编辑笔记标签*/
		@Transaction
		@Override
		public void updateNoteTag(String token, NoteTagInfo bean) {
			Account account=noteService.getExistedAccountByToken(token);
			NoteTag old=dao.getExistedByIdForUpdate(NoteTag.class, bean.id);
			checkAccountPermission(account, old.createAccountId);
			old.updateAccountId=account.id;
			checkUniqueKeysOnUpdate(dao, bean, old);
			//
			old.name=bean.name;
			old.color=bean.color;
			old.remark=bean.remark;
			checkValid(old);
			dao.update(old);
		}

		/**删除笔记标签*/
		@Transaction
		@Override
		public void deleteNoteTag(String token, int id) {
			Account account=noteService.getExistedAccountByToken(token);
			NoteTag old=dao.getExistedByIdForUpdate(NoteTag.class, id);
			checkAccountPermission(account, old.createAccountId);
			dao.deleteById(NoteTag.class, id);
			//
			NoteQuery query=new NoteQuery();
			query.noteTagId=id;
			query.pageSize=Integer.MAX_VALUE;
			List<Note> notes=dao.getList(query);
			for (Note bean : notes) {
				Note note=dao.getExistedByIdForUpdate(Note.class, bean.id);
				note.tagIdList.remove((Integer)id);
				dao.updateSpecialFields(note, "tagIdList");
			}
		}
	}
}
