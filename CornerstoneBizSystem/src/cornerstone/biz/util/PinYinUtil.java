package cornerstone.biz.util;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jazmin.core.Jazmin;
import jazmin.util.IOUtil;
import net.sourceforge.pinyin4j.PinyinHelper;

/**
 * 
 * @author icecooly
 *
 */
public class PinYinUtil {
	//
	private static Logger logger = LoggerFactory.getLogger(PinYinUtil.class);
	//
	public static Map<String, String> pinyinMap = new ConcurrentHashMap<>();

	//
	private static synchronized void initMap() {
		try {
			if (pinyinMap.size() == 0) {
				InputStream is = Jazmin.getAppClassLoader().getResourceAsStream("cornerstone/biz/util/pinyin.dict");
				String content;
				content = IOUtil.toString(is);
				String[] line = content.split("\n");
				for (String c : line) {
					if (c.indexOf("=") == -1) {
						continue;
					}
					String[] data = c.split("=");
					pinyinMap.put(data[0], data[1]);
				}
			}
		} catch (Exception e) {
			logger.error("PinYinUtil initMap failed.");
			logger.error(e.getMessage(), e);
		}
	}

	//
	/**
	 * eg:加油中国->jiayouzhongguo
	 * 
	 * @param input
	 * @return
	 */
	public static String convertToPinyinString(String input) {
		try {
			initMap();
			if (input == null) {
				return null;
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < input.length(); i++) {
				String initial = Character.toString(input.charAt(i));
				String pinyin = pinyinMap.get(initial);
				if (pinyin == null || pinyin.length() == 0) {
					sb.append(initial);
				} else {
					sb.append(pinyin);
				}
			}
			return sb.toString();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "";
		}
	}

	/**
	 * eg:加油中国->JYZG
	 * 
	 * @param input
	 * @return
	 */
	public static String getShortName(String input) {
		try {
			initMap();
			if (input == null) {
				return null;
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < input.length(); i++) {
				String initial = Character.toString(input.charAt(i));
				String pinyin = pinyinMap.get(initial);
				if (pinyin == null || pinyin.length() == 0) {
					sb.append(initial);
				} else {
					sb.append(pinyin.substring(0, 1));
				}
			}
			return sb.toString().toUpperCase();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * 张伟->Z abc->A
	 * 
	 * @param input
	 * @return
	 */
	public static String getInitial(String input) {
		initMap();
		if (input == null || input.length() == 0) {
			return null;
		}
		String initial = input.substring(0, 1);
		String pinyin = pinyinMap.get(initial);
		if (pinyin == null || pinyin.length() == 0) {
			return initial.toUpperCase();
		}
		return pinyin.substring(0, 1).toUpperCase();
	}

	/**
	 * 获取汉字首字母的方法。如： 张三 --> ZS 说明：暂时解决不了多音字的问题，只能使用取多音字的第一个音的方案
	 * 
	 * @param hanzi
	 *            汉子字符串
	 * @return 大写汉子首字母; 如果都转换失败,那么返回null
	 */
	public static String getHanziInitials(String hanzi) {
		String result = null;
		if (null != hanzi && !"".equals(hanzi)) {
			char[] charArray = hanzi.toCharArray();
			StringBuffer sb = new StringBuffer();
			for (char ch : charArray) {
				// 逐个汉字进行转换， 每个汉字返回值为一个String数组（因为有多音字）
				String[] stringArray = PinyinHelper.toHanyuPinyinStringArray(ch);
				if (null != stringArray) {
					sb.append(stringArray[0].charAt(0));
				}
			}
			if (sb.length() > 0) {
				result = sb.toString().toUpperCase();
			}
		}
		return result;
	}

	/**
	 * 获取汉字拼音的方法。如： 张三 --> zhangsan 说明：暂时解决不了多音字的问题，只能使用取多音字的第一个音的方案
	 * 
	 * @param hanzi
	 *            汉子字符串
	 * @return 汉字拼音; 如果都转换失败,那么返回null
	 */
	public static String getHanziPinYin(String hanzi) {
		try {
			String result = null;
			if (null != hanzi && !"".equals(hanzi)) {
				char[] charArray = hanzi.toCharArray();
				StringBuffer sb = new StringBuffer();
				for (char ch : charArray) {
					// 逐个汉字进行转换， 每个汉字返回值为一个String数组（因为有多音字）
					String[] stringArray = PinyinHelper.toHanyuPinyinStringArray(ch);
					if (null != stringArray) {
						// 把第几声这个数字给去掉
						sb.append(stringArray[0].replaceAll("\\d", ""));
					}
				}
				if (sb.length() > 0) {
					result = sb.toString();
				}
			}
			return result;
		} catch (Throwable e) {
			logger.error(e.getMessage(),e);
			return null;
		}
		
	}

}