package cornerstone.biz.lucene;

import java.util.function.Supplier;

import org.apache.lucene.analysis.Analyzer;

/**
 * 因为Analyzer的createComponents方法API改变了需要重新实现分析器
 * @author THINKPAD
 *
 */
public class IKAnalyzer4Lucene7 extends Analyzer {
	//
    private boolean useSmart = false;
    
    public static Supplier<?> initDictCallback;

    public IKAnalyzer4Lucene7() {
        this(false);
    }

    public IKAnalyzer4Lucene7(boolean useSmart) {
        super();
        this.useSmart = useSmart;
    }

    public boolean isUseSmart() {
        return useSmart;
    }

    public void setUseSmart(boolean useSmart) {
        this.useSmart = useSmart;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        IKTokenizer4Lucene7 tk = new IKTokenizer4Lucene7(this.useSmart);
        //
        if(initDictCallback!=null) {
        		initDictCallback.get();
        }
        return new TokenStreamComponents(tk);
    }

}