package cornerstone.biz.lucene;
/**
 * 
 */


import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import cornerstone.biz.util.StringUtil;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;

/**
 * 
 * @author cs
 *
 */
public class LuceneService {
	//
	private static Logger logger = LoggerFactory.get(LuceneService.class);
	//
	private Map<String, IndexReader> readers;
	private Map<String, Directory> directories;
	private Analyzer analyzer;
	//
	//
	public LuceneService() {
		readers = new ConcurrentHashMap<>();
		directories = new ConcurrentHashMap<>();
		analyzer=createAnalyzer();
	}
	//
	private Analyzer createAnalyzer() {
		Analyzer ik = new IKAnalyzer4Lucene7(true);;
		return ik;
	}
	// ------------------------------------------------------------------------
	// search and index
	public void updateDocuments(String indexPath, List<DocumentData> docs) {
		IndexWriter writer = null;
		try {
			Directory dir = getDirectory(indexPath);
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			writer = new IndexWriter(dir, iwc);
			for (DocumentData e : docs) {
				writer.updateDocument(e.term, e.document);
			}
			writer.commit();
			writer.close();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new SearchException(e);
		} finally {
			closeIndexWriter(writer);
		}
	}

	//
	private void closeIndexWriter(IndexWriter writer) {
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	//
	public void deleteDocument(String indexPath, String key, String id) throws SearchException {
		IndexWriter writer = null;
		try {
			Directory dir = getDirectory(indexPath);
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			writer = new IndexWriter(dir, iwc);
			writer.deleteDocuments(new Term(key, id));
			writer.commit();
		} catch (Exception e) {
			throw new SearchException(e);
		} finally {
			closeIndexWriter(writer);
		}
	}
	
	/**
	 * 
	 * @param indexPath
	 * @param terms
	 * @throws SearchException
	 */
	public void deleteDocuments(String indexPath, List<Term> terms) throws SearchException {
		IndexWriter writer = null;
		try {
			Directory dir = getDirectory(indexPath);
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			writer = new IndexWriter(dir, iwc);
			writer.deleteDocuments(terms.toArray(new Term[]{}));
			writer.commit();
		} catch (Exception e) {
			throw new SearchException(e);
		} finally {
			closeIndexWriter(writer);
		}
	}

	/**
	 * 
	 * @param indexPath
	 * @return
	 */
	private Directory getDirectory(String indexPath) {
		Directory dir = directories.get(indexPath);
		if (dir == null) {
			try {
				dir = FSDirectory.open(new File(indexPath).toPath());
				directories.put(indexPath, dir);
			} catch (IOException e) {
				throw new SearchException(e);
			}
		}
		return dir;
	}

	//
	public IndexSearcher getIndexSearcher(String indexPath) {
		return new IndexSearcher(getIndexReader(indexPath));
	}
	//
	private IndexReader getIndexReader(String indexPath) {
		try {
			IndexReader indexReader = readers.get(indexPath);
			if (indexReader == null) {
				Directory dir = getDirectory(indexPath);
				indexReader = DirectoryReader.open(dir);
				readers.put(indexPath, indexReader);
			} else {
				IndexReader changeReader = DirectoryReader.openIfChanged((DirectoryReader) indexReader);
				if (changeReader != null) {
					indexReader.close();
					indexReader = changeReader;
					readers.put(indexPath, indexReader);
				}
			}
			return indexReader;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new SearchException(e);
		}
	}
	//
	public  Query createQuery(String key[],String keywords,Map<String,Float> boosts) {
		MultiFieldQueryParser mqp = null;
		if(boosts!=null) {
			mqp = new MultiFieldQueryParser(key, analyzer,boosts);
		}else {
			mqp = new MultiFieldQueryParser(key, analyzer);
		}
		try {
			return mqp.parse(keywords);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new AppException(e.getMessage());
		}
	}
	//
	public List<SearchDocument> searchDocuments(String indexPath,Query query,int pageSize)
			throws SearchException {
		try {
			IndexSearcher searcher = getIndexSearcher(indexPath);
			TopScoreDocCollector collector = TopScoreDocCollector.create(pageSize);
			searcher.search(query, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			List<SearchDocument> result = new ArrayList<SearchDocument>();
			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				SearchDocument dd = new SearchDocument();
				dd.docId=docId;
				dd.document = d;
				dd.score=hits[i].score;
                result.add(dd);
			}
			if(logger.isDebugEnabled()) {
				logger.debug("searchDocuments indexPath:{} query:{} pageSize:{} result:{}",
						indexPath,query,pageSize,result.size());
			}
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error("searchDocuments failed.indexPath:{} query:{} pageSize:{}",
					indexPath,query,pageSize);
			throw new SearchException(e);
		}
	}
	//
	public List<String> getWords(String str){
		return getWords(str, analyzer);
	}
	/**
	 * 获取分词结果
	 * @param str
	 * @param analyzer
	 * @return 分词结果
	 */
	public static List<String> getWords(String str,Analyzer analyzer){
		if(StringUtil.isEmpty(str)) {
			return new ArrayList<>();
		}
		List<String> result = new ArrayList<>();
		TokenStream stream = null;
		try {
			stream = analyzer.tokenStream("content", new StringReader(str));
			CharTermAttribute attr = stream.addAttribute(CharTermAttribute.class);
			stream.reset();
			while(stream.incrementToken()){
				String word=attr.toString();
				if(word==null||word.length()<=1) {
					continue;
				}
				result.add(word);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			if(stream != null){
				try {
					stream.close();
				} catch (IOException e) {
					logger.error("getWords ERROR",e);
				}
			}
			if(logger.isDebugEnabled()) {
				logger.debug("getWords str:{} result:{}",str,DumpUtil.dump(result));
			}
		}
		if(result.size()==0) {
			result.add(str);
		}
		return result;
	}
	//
	public SearchDocument getDocument(String indexPath, int docId) throws SearchException {
		try {
			IndexReader reader=getIndexReader(indexPath);
			SearchDocument sd = new SearchDocument();
			sd.document = reader.document(docId);
			if (sd.document == null) {
				return null;
			}
			return sd;
		} catch (Exception e) {
			throw new SearchException(e);
		}

	}

	//
	public int getNumDocs(String indexPath) throws SearchException {
		try {
			IndexReader reader=getIndexReader(indexPath);
			return reader.numDocs();
		} catch (Exception e) {
			throw new SearchException(e);
		}
	}

	//
	public List<SearchDocument> queryMoreLikeThis(String indexPath, String likeFields[], int docId, int count)
			throws SearchException {
		try {
			IndexReader reader=getIndexReader(indexPath);
			List<SearchDocument> result = new ArrayList<SearchDocument>();
			IndexSearcher searcher = new IndexSearcher(reader);
			MoreLikeThis mlt = new MoreLikeThis(reader);
			mlt.setAnalyzer(analyzer);
			mlt.setFieldNames(likeFields);
			mlt.setMinTermFreq(1);
			mlt.setMinDocFreq(1);
			Document doc = reader.document(docId);
			Query query = mlt.like(docId);
			TopDocs similarDocs = searcher.search(query, count);
			for (int i = 0; i < similarDocs.scoreDocs.length; i++) {
				if (similarDocs.scoreDocs[i].doc != docId) {
					doc = reader.document(similarDocs.scoreDocs[i].doc);
					SearchDocument sd = new SearchDocument();
					sd.document = doc;
					result.add(sd);
				}
			}
			return result;
		} catch (Exception e) {
			throw new SearchException(e);
		}
	}
	/**
	 * 
	 * @param query
	 * @param fieldName
	 * @param fieldContent
	 * @param fragmentSize
	 * @param formatter
	 * @return
	 *
	 */
	public String displayHtmlHighlight(Query query,
	    		String fieldName, String fieldContent, int fragmentSize,Formatter formatter){
		  return displayHtmlHighlight(query, analyzer, fieldName, 
				  fieldContent, fragmentSize, formatter);
	 }
	/**
     * 获取高亮显示结果的html代码
     * @param query 查询
     * @param analyzer 分词器
     * @param fieldName 域名
     * @param fieldContent 域内容
     * @param fragmentSize 结果的长度（不含html标签长度）
     * @return 结果（一段html代码）
     * @throws IOException
     * @throws InvalidTokenOffsetsException
     */
    public String displayHtmlHighlight(Query query, Analyzer analyzer, 
    		String fieldName, String fieldContent, int fragmentSize,Formatter formatter){
        Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query));
        Fragmenter fragmenter = new SimpleFragmenter(fragmentSize);
        highlighter.setTextFragmenter(fragmenter);
        String result=null;
        try {
        		result=highlighter.getBestFragment(analyzer, fieldName, fieldContent);
        		return result;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new AppException(e.getMessage());
		}finally {
			if(logger.isDebugEnabled()) {
				logger.debug("displayHtmlHighlight \nfieldName:{} \nfieldContent:{} \nresult:{}",
						fieldName,fieldContent,result);
			}
		}
    }
}
