package cornerstone.biz.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 
 * @author cs
 *
 */
public class DumpUtil {

	/**
	 * 
	 * @param o
	 * @return
	 */
	public static String dump(Object o) {
		if(o==null){
			return "<null>";
		}
		return JSON.toJSONString(o,SerializerFeature.PrettyFormat,
				SerializerFeature.WriteDateUseDateFormat);
	}
}
