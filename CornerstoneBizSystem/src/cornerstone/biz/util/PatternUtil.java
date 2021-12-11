package cornerstone.biz.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jazmin.core.app.AppException;

/**
 *
 * @author cs
 *
 */
public class PatternUtil {

	/**
	 * 
	 * @param data
	 * @param regex
	 * @return
	 */
	public static List<String> matchs(String data, String regex) {
		List<String> result = new ArrayList<>();
		if (data == null || regex == null) {
			return result;
		}
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(data);
		//System.out.println(matcher.groupCount());
		while (matcher.find()) {
			result.add(matcher.group(1));
		}
		return result;
	}
	
	/**
	 * 
	 * @param data
	 * @param regex
	 * @return
	 */
	public static List<List<String>> matchGroups(String data, String regex) {
		List<List<String>> result = new ArrayList<>();
		if (data == null || regex == null) {
			return result;
		}
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(data);
		while (matcher.find()) {
			List<String> groups=new ArrayList<>();
			for(int i=1;i<=matcher.groupCount();i++) {
				groups.add(matcher.group(i));
			}
			result.add(groups);
		}
		return result;
	}

	/**
	 * 检查是否是邮箱
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (email == null) {
			return false;
		}
		String check = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(email);
		return matcher.matches();
	}
	//
	public static void checkUserName(String userName) {
		if(userName==null||userName.length()==0) {
			throw new AppException("用户名格式错误");
		}
		String check = "^(?!.*?_$)[a-zA-Z0-9_.$#]+$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(userName);
		if(!matcher.matches()) {
			throw new AppException("用户名格式错误");
		}
	}
	//

	public static boolean isMobile(String mobileNo) {
		if(null==mobileNo){
			return false;
		}
		String regex ="^1[3456789]\\d{9}$";
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(mobileNo).matches();
	}
}
