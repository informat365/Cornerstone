package cornerstone.biz.util;

/**
 * 
 * @author icecooly
 *
 */
public class StringUtil {

	/**
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isEmpty(String input){
		if(input==null||input.length()==0){
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isEmptyWithTrim(String input){
		if(input==null||input.length()==0){
			return true;
		}
		input=input.trim();
		if(input==null||input.length()==0){
			return true;
		}
		return false;
	}
	
	/**左边补0*/
	public static String lpad(int number,int length){
		return String.format("%0"+length+"d", number);   
	}
	
	/**
	 * 保留digits位小数(最后一位四舍五入)
	 * @param value
	 * @return
	 */
	public static final String numberFormat(double value,int digits){
		return String.format("%."+digits+"f",value);
	}
	
	/**
	 * 分转元
	 * @param money
	 * @return
	 */
	public static String getMoney(int money) {
        return StringUtil.numberFormat(money/100.0f,2) + "元";
    }
	
}
