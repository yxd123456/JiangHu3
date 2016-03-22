package com.sptech.qujj.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateManage {
	public static final String DATE_PATTERN = "yyyyMMdd";
	public static final String DATE_PATTERN1 = "yyyy-MM-dd";
	public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_TIME_PATTERN1 = "yyyyMMddHHmmss";
	public static final String DATE_TIME_PATTERN2 = "yyyy年MM月";
	public static final String DATE_TIME_PATTERN3 = "yyyy:MM:dd:";
	public static final String DATE_TIME_PATTERN4 = "yyyy-MM-dd HH:mm";
	public static final String DATE_TIME_PATTERN5 = "MM-dd";
	public static final String DATE_TIME_PATTERN6 = "MM";

	public static final String DATE_TIME_PATTERN7 = "MM月dd日";
	public static final String DATE_TIME_PATTERN8 = "yyyy年MM月dd日";

	public static long getTimeLong(String time) {
		TimeZone timeZoneNY = TimeZone.getTimeZone("Asia/Shanghai");
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN2, Locale.SIMPLIFIED_CHINESE);
		sdf.setTimeZone(timeZoneNY);
		Date date = null;
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime();
	}

	public static String formatTodayDatelistview() {
		try {
			TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai");
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN2, Locale.SIMPLIFIED_CHINESE);
			sdf.setTimeZone(timeZone);
			return sdf.format(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getformatTodayDate() {
		try {
			TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai");
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN1, Locale.SIMPLIFIED_CHINESE);
			sdf.setTimeZone(timeZone);
			return sdf.format(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	// 指定时间加一个小时
	public static String addHours(String day, int x) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");// 24小时制
		// SimpleDateFormat format = new
		// SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//12小时制
		Date date = null;
		try {
			date = format.parse(day);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (date == null)
			return "";
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, x);// 24小时制
		// cal.add(Calendar.HOUR, x);12小时制
		date = cal.getTime();
		System.out.println("front:" + date);
		cal = null;
		System.out.println("addHours返回的时间：" + format.format(date));
		return format.format(date);
	}

	// 指定时间加 天数
	public static Date addDate(Date d, long day) throws ParseException {
		long time = d.getTime();
		day = day * 24 * 60 * 60 * 1000;
		time += day;
		return new Date(time);

		// Date newDay=new DateTime(SourceDate).plusDays(day).toDate();
		// return newDay;
	}

	// 当前日期加一天
	public static String addDay(long day) throws ParseException {
		Date d = new Date();
		long time = d.getTime();
		day = day * 24 * 60 * 60 * 1000;
		time += day;
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN1);
		Date dt = new Date(time);
		return sdf.format(time);
	}

	// 指定时间加 天数
	// public static String addDay(String day, int x) {
	//
	// SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");//
	// Date date = null;
	// try {
	// date = format.parse(day);
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// if (date == null)
	// return "";
	// Calendar cal = Calendar.getInstance();
	// cal.setTime(date);
	// cal.add(Calendar.DAY_OF_YEAR, x);// 天数
	// // cal.add(Calendar.HOUR, x);12小时制
	// date = cal.getTime();
	// System.out.println("front:" + date);
	// cal = null;
	// System.out.println("addHours返回的时间：" + format.format(date));
	// return format.format(date);
	// }

	public static int compare_date(String date1, String date2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm"); // 24制度
		                                                          // yyyy-MM-dd
		                                                          // hh:mm
		                                                          // 就是12小时制度
		try {
			Date dt1 = df.parse(date1);
			Date dt2 = df.parse(date2);
			System.out.println("装换为date的值后变为......");
			System.out.println("结束时间:" + dt1);
			System.out.println("开始时间:" + dt2);
			if (dt1.getTime() > dt2.getTime()) {
				System.out.println("dt1 在dt2前");
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				System.out.println("dt1在dt2后");
				return 0;
			} else {
				System.out.println("不确定");
				return -1;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return -1;
	}

	public static String FromYYMMDDToYYMM(String value) {
		SimpleDateFormat new_format = new SimpleDateFormat(DATE_TIME_PATTERN4);
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN);

		Date date;
		String local_date = null;
		try {
			date = sdf.parse(value);
			local_date = new_format.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return local_date;
	}

	public static String compare_day(String date1, String date2) {
		if (date2.length() >= 8) {
			return date1.substring(0, date1.length() - 3) + "-" + date2.substring(date2.length() - 8, date2.length() - 3);
		} else {
			return date1 + "-" + date2;
		}

	}

	// String 转 date
	public static String StringToDate(String mss) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
		Date dt = new Date((Long.parseLong(mss) * 1000));
		return sdf.format(dt);
	}

	// String 转 date 第二种办法
	public static Date StringToDateTwo(String mss) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN);
		Date dt = new Date((Long.parseLong(mss) * 1000));
		return dt;
	}

	public static String StringToDatehms(String mss) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN);
		Date dt = new Date((Long.parseLong(mss) * 1000));
		return sdf.format(dt);
	}

	// 转换为年月日
	public static String StringToDateymd(String mss) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN1);
		Date dt = new Date((Long.parseLong(mss) * 1000));
		return sdf.format(dt);
	}

	// 转换为年月日 时分秒
	public static String StringToDateymdhms(String mss) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN);
		Date dt = new Date((Long.parseLong(mss) * 1000));
		return sdf.format(dt);
	}

	public static String StringToMMDD(String mss) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN5);
		Date dt = new Date((Long.parseLong(mss) * 1000));
		return sdf.format(dt);
	}

	public static String StringToMM(String mss) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN6);
		Date dt = new Date((Long.parseLong(mss) * 1000));
		return sdf.format(dt);
	}

	// 获取时间戳的月份
	public static int getUXMonth(String addtime) {
		SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN1);
		Date dt = new Date((Long.parseLong(addtime) * 1000));
		// System.out.println("format=" + format.format(dt));
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int month = cal.get(Calendar.MONTH) + 1;
		return month;
	}

	// 时间戳 转化为天数
	public static String getDayFormUX(long time) {
		long day1 = (time / (24 * 3600));
		long later = time % (24 * 3600);
		System.out.println("later === " + later);
		if (later > 0) {
			return Long.toString(day1 + 1);
		}
		return Long.toString(day1);
	}

	// 时间戳获取 月日
	public static String getMonthDay(String addtime) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN7);
		Date dt = new Date((Long.parseLong(addtime) * 1000));
		return sdf.format(dt);
	}

	// 时间戳获取 年月
	public static String getYearMonth(String addtime) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN2);
		Date dt = new Date((Long.parseLong(addtime) * 1000));
		return sdf.format(dt);
	}

	// 时间戳转化为 年月日
	public static String getYearMonthDay(String addtime) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN8);
		Date dt = new Date((Long.parseLong(addtime) * 1000));
		return sdf.format(dt);
	}

	// 时间戳获取 年月
	public static String getDHM(int miao) {
		int day = miao / 86400;
		int hour = miao % 86400 / 3600;
		int min = miao % 86400 % 3600 / 60;
		String h;
		String m;
		if (hour < 10) {
			h = "0" + hour;
		} else {
			h = "" + hour;
		}
		if (min < 10) {
			m = "0" + min;

		} else {
			m = "" + min;
		}
		String dhm = day + "天" + h + ":" + m;
		return dhm;
	}

	// 时间戳获取 年月
	public static String getHM(int miao) {
		int day = miao / 86400;
		int hour = miao % 86400 / 3600;
		int min = miao % 86400 % 3600 / 60;
		String h;
		String m;
		if (hour < 10) {
			h = "0" + hour;
		} else {
			h = "" + hour;
		}
		if (min < 10) {
			m = "0" + min;

		} else {
			m = "" + min;
		}
		String dhm = h + ":" + m;
		return dhm;
	}

	// 获取28天后的时间
	public static String getDate() {
		GregorianCalendar worldTour = new GregorianCalendar();
		worldTour.add(GregorianCalendar.DATE, 30); // 当前日期加100天
		Date d = worldTour.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String s = df.format(d);
		// System.out.println("280天后的日期是：" + s);
		return s;
	}

	// 获取当前时间
	public static String getNowDate() {
		// Date d = new Date();
		// long longtime = d.getTime();
		Date nowTime = new Date(System.currentTimeMillis());
		SimpleDateFormat sdFormatter = new SimpleDateFormat("MM/dd HH:mm");
		String retStrFormatNowDate = sdFormatter.format(nowTime);

		return retStrFormatNowDate;

	}

	// 获取28天后的时间
	public static String getAddDate(String haomiao, int addday) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN1);
		Date dt = new Date((Long.parseLong(haomiao) * 1000));

		// return sdf.format(dt);

		GregorianCalendar worldTour = new GregorianCalendar();
		worldTour.setTime(dt);
		worldTour.add(GregorianCalendar.DATE, addday); // 当前日期加X天
		Date d = worldTour.getTime();
		// DateFormat df = DateFormat.getDateInstance();
		String s = sdf.format(d);
		// System.out.println("280天后的日期是：" + s);
		return s;
	}
	// //时间戳的差 转换为 天数
	// public static String getUXDay(String mian) {
	// SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN7);
	// Date dt = new Date((Long.parseLong(addtime) * 1000));
	// return sdf.format(dt);
	// }
}
