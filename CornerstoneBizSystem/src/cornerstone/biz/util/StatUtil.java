package cornerstone.biz.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;

/**
 * 统计工具
 * @author cs
 *
 */
public class StatUtil {
	//
	private static Logger logger=LoggerFactory.get(StatUtil.class);	//
	//
	public static class StatResult {
		//
		public static final int TYPE_按日统计=1;
		public static final int TYPE_按周统计=2;
		public static final int TYPE_按月统计=3;
		//
		public int type;
		
		public String item;//2018-06-03,2018-51,2018-03,2018

		public List<Long> values;
		//
		public StatResult() {
			values=new ArrayList<>();
		}
	}
	/**
	 * 补0
	 * @param type StatResult.type
	 * @param list
	 * @return
	 */
	public static List<StatResult> fillDatas(int type,List<StatResult> list,int valueSize) {
		if(list==null||list.size()<=1) {
			return list;
		}
		List<StatResult> result=new ArrayList<>();
		if(type==StatResult.TYPE_按日统计) {
			Date startDate=null;
			Date endDate=null;
			for (StatResult e : list) {
				Date date=DateUtil.parseDate(e.item, "yyyy-MM-dd");
				if(startDate==null||date.before(startDate)) {
					startDate=date;
				}
				if(endDate==null||date.after(endDate)) {
					endDate=date;
				}
			}
			int days=DateUtil.differentDays(startDate,endDate);
			for(int i=0;i<=days;i++) {
				Date date=DateUtil.getBeginOfDay(DateUtil.getNextDay(startDate,i));
				String item=DateUtil.formatDate(date);
				addItem(type, item, list, result,valueSize);
			}
		}//
		else if(type==StatResult.TYPE_按周统计) {
			int startYear=999999;
			int startYearStartWeek=999999;
			int endYear=0;
			int endYearEndWeek=0;
			for (StatResult e : list) {
				String[] data=e.item.split("-");
				int year=Integer.valueOf(data[0]);
				if(year<=startYear) {
					startYear=year;
				}
				if(year>=endYear) {
					endYear=year;
				}
			}
			for (StatResult e : list) {
				String[] data=e.item.split("-");
				int year=Integer.valueOf(data[0]);
				int week=Integer.valueOf(data[1]);
				if(year==startYear) {
					if(week<startYearStartWeek) {
						startYearStartWeek=week;
					}
				}
				if(year==endYear) {
					if(week>endYearEndWeek) {
						endYearEndWeek=week;
					}
				}
			}
			for(int year=startYear;year<=endYear;year++) {
				int maxWeek=getMaxWeekNumOfYear(year);
				int startWeek=1;
				int endWeek=maxWeek;
				if(year==startYear) {
					startWeek=startYearStartWeek;
				}
				if(year==endYear) {
					endWeek=endYearEndWeek;
				}
				for(int week=startWeek;week<=endWeek;week++) {
					String item=year+"-"+week;
					addItem(type, item, list, result,valueSize);
				}
			}
		}//
		else if(type==StatResult.TYPE_按月统计) {
			int startYear=999999;
			int startYearStartMonth=999999;
			int endYear=0;
			int endYearEndMonth=0;
			for (StatResult e : list) {
				String[] data=e.item.split("-");
				int year=Integer.valueOf(data[0]);
				if(year<=startYear) {
					startYear=year;
				}
				if(year>=endYear) {
					endYear=year;
				}
			}
			for (StatResult e : list) {
				String[] data=e.item.split("-");
				int year=Integer.valueOf(data[0]);
				int month=Integer.valueOf(data[1]);
				if(year==startYear) {
					if(month<startYearStartMonth) {
						startYearStartMonth=month;
					}
				}
				if(year==endYear) {
					if(month>endYearEndMonth) {
						endYearEndMonth=month;
					}
				}
			}
			for(int year=startYear;year<=endYear;year++) {
				int startMonth=1;
				int endMonth=12;
				if(year==startYear) {
					startMonth=startYearStartMonth;
				}
				if(year==endYear) {
					endMonth=endYearEndMonth;
				}
				for(int month=startMonth;month<=endMonth;month++) {
					String sMonth=month>=10?month+"":"0"+month;
					String item=year+"-"+sMonth;
					addItem(type, item, list, result,valueSize);
				}
			}
		}
		//
		return result;
	}
	
	private static void addItem(int type,String item,List<StatResult> list,List<StatResult> result,int valueSize) {
		for (StatResult e : list) {
			if(e.item.endsWith(item)){
				result.add(e);
				return;
			}
		}
		//补0
		StatResult statResult=new StatResult();
		statResult.item=item;
		statResult.type=type;
		for(int i=0;i<valueSize;i++) {
			statResult.values.add(0L);
		}
		result.add(statResult);
	}
	
	
	// 获取某一年的最大周数
    public static int getMaxWeekNumOfYear(int year) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        return c.getWeeksInWeekYear();
    }
    
    /**
     * 
     * @param clazz
     * @param startDate
     * @param endDate
     * @param list
     * @param strStatDateFiled
     * @param bu0 true表示补0 false表示取昨天的数据
     * @param strValueFiled
     * @return
     */
	public static <T> List<T> buDays(Class<T> clazz,Date startDate,
			Date endDate,List<T> list,String strStatDateFiled,
			boolean bu0,String strValueFiled) {
		if(startDate==null) {
			throw new IllegalArgumentException("startDate cannot be null");
		}
		if(endDate==null) {
			throw new IllegalArgumentException("endDate cannot be null");
		}
		logger.info("buDays startDate:{} endDate:{} statDateFiled:{}",
				DateUtil.formatDate(startDate),DateUtil.formatDate(endDate),startDate);
		try {
			Map<String,T> map=new HashMap<>();
			Field statDateFiled=clazz.getField(strStatDateFiled);
			Field valueField=null;
			if(strValueFiled!=null) {
				valueField=clazz.getField(strValueFiled);
			}
			for (T e : list) {
				Date statTime=(Date) statDateFiled.get(e);
				map.put(DateUtil.formatDate(statTime, "yyyy-MM-dd"), e);
			}
			List<T> dayDatas=new ArrayList<>();
			int days=DateUtil.differentDays(startDate,endDate);
			T last=null;
			for(int i=0;i<=days;i++) {
				Date date=DateUtil.getBeginOfDay(DateUtil.getNextDay(startDate,i));
				T bean=map.get(DateUtil.formatDate(date, "yyyy-MM-dd"));
				if(bean==null) {
					bean=clazz.newInstance();
					statDateFiled.set(bean, date);
					if(!bu0) {//如果不是补0,是补昨天的数据
						if(last!=null&&valueField!=null) {
							valueField.set(bean, valueField.get(last));
						}
					}
				}
				dayDatas.add(bean);
				last=bean;
			}
			return dayDatas;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new AppException(e.getMessage());
		}
	}
    //
}
