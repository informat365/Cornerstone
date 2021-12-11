package cornerstone.biz.util;

import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.FieldMethodizer;
import org.apache.velocity.app.Velocity;

import jazmin.log.Logger;
import jazmin.log.LoggerFactory;

/**
 * 
 * @author cs
 *
 */
public class VelocityUtil {
	//
	private static Logger logger=LoggerFactory.get(VelocityUtil.class);
	//
	/**
	 * 
	 * @param params
	 * @return
	 */
	public static String render(String content,Map<String,Object> params) {
		Velocity.addProperty("runtime.introspector.uberspect",
				"org.apache.velocity.util.introspection.UberspectPublicFields, "
						+ "org.apache.velocity.util.introspection.UberspectImpl");
		Velocity.init();
		VelocityContext context = new VelocityContext();
		context.put("runtime", new FieldMethodizer("org.apache.velocity.runtime.Runtime"));
		if(params!=null) {
			params.put("dateTool", new DateTool());
			params.forEach((k,v)->{
				context.put(k,v);
			});
		}
		StringWriter w = new StringWriter();
		Velocity.evaluate(context, w, "velocity", content);
		return w.toString();
	}
	//
	public static class DateTool{
		//
		public String format(Date date) {
			return DateUtil.formatDate(date);
		}
		//
		public String format(Date date,String format) {
			return DateUtil.formatDate(date,format);
		}
	}
	//
}
