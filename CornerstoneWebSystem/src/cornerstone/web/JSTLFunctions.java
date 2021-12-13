package cornerstone.web;

import cornerstone.biz.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JSTLFunctions {

    public static String formatDate(Date date) {
        return formatDate(date, null);
    }

    public static String formatDate(Date date, String pattern) {
        if (date == null) {
            return "--";
        }
        if (StringUtil.isEmptyWithTrim(pattern)) {
            pattern = "yyyy-MM-dd HH:mm";
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }
}
