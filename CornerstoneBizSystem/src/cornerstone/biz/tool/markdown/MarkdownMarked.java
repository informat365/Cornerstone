package cornerstone.biz.tool.markdown;

import cornerstone.biz.util.BizUtil;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class MarkdownMarked {
    private static Logger logger= LoggerFactory.get(MarkdownMarked.class);
    private static String js;

    //
    public static String getJs() {
        if (null == js) {
            // 双重🔒
            synchronized (MarkdownMarked.class) {
                try {
                    if (null == js) {
                        js = BizUtil.readAll(MarkDown2HtmlWrapper.class.getResourceAsStream("marked.js"));
                    }
                } catch (Exception e) {
                    logger.error("MarkdownMarked js init error", e);
                }
            }
        }
        return js;

    }

    /**
     * 转换数据
     * @param content
     * @return
     */
    public static String md2Html(String content) {
        try {
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
            engine.eval(getJs());
            if (engine instanceof Invocable) {
                return (String) ((Invocable) engine).invokeFunction("marked", content);
            }
            return "";
        }catch (Exception e){
            logger.error("MarkdownMarked marked error", e);
            return  "";
        }
    }
}
