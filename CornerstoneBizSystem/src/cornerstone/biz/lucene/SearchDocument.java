/**
 * 
 */
package cornerstone.biz.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;

import jazmin.util.StringUtil;

/**
 * 
 * @author cs
 *
 */
public class SearchDocument {
	public int docId;
	public Document document;
	public float score;
	Term term;
	public SearchDocument() {
		document=new Document();
	}
	//
	public void putString(String key,String content,boolean store){
		if(StringUtil.isEmpty(key)||StringUtil.isEmpty(content)) {
			return;
		}
		StringField field=new StringField(key, content, store?Field.Store.YES:Field.Store.NO);
		document.add(field);
	}
	public void putText(String key,String content,boolean store){
		if(StringUtil.isEmpty(key)||StringUtil.isEmpty(content)) {
			return;
		}
		document.add(new TextField(key, content, store?Field.Store.YES:Field.Store.NO));
	}
	//
	public void setTerm(String fl,String text){
		term=new Term(fl, text);
	}
	//
	public String get(String key){
		return document.get(key);
	}
}
