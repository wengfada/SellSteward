package com.fada.sellsteward.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MyDateUtils {
	/**
	 * 将long得到-- 小时:分:秒
	 * @param lefttime
	 * @return
	 */
	public static String formatTime(long lefttime) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss",Locale.CHINA);
		//sdf.setTimeZone(TimeZone.getTimeZone("Asia/Beijing"));// 设置时区
		String sDateTime = sdf.format(lefttime); // 得到精确到秒的表示：08/31/2006
													// 21:08:00
		return sDateTime;
	}
	/**
	 * 得到: 年-月-日 小时:分钟
	 * @param lefttime
	 * @return
	 */
	public static String formatDateAndTime(long lefttime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.CHINA);
		//sdf.setTimeZone(TimeZone.getTimeZone("Asia/Beijing"));// 设置时区
		String sDateTime = sdf.format(lefttime); // 得到精确到秒的表示：08/31/2006
		// 21:08:00
		return sDateTime;
	}
	
	/**
	 * 
	 * @param time 字符串时间,注意:格式要与template定义的一样
	 * @param template 要格式化的格式:如time为09:21:12那么template为"HH:mm:ss"
	 * @return
	 */
	public static long formatToLong(String time,String template) {
		SimpleDateFormat sdf = new SimpleDateFormat(template, Locale.CHINA);
		try {
			Date d = sdf.parse(time);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			long l = c.getTimeInMillis();
			return l;
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}
	/**
	 * 得到年份
	 * @param lefttime
	 * @return
	 */
	public static int formatYear(long lefttime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy",Locale.CHINA);
		//sdf.setTimeZone(TimeZone.getTimeZone("Asia/Beijing"));// 设置时区
		String sDateTime = sdf.format(lefttime); // 得到精确到秒的表示：08/31/2006
		// 21:08:00
		int i = Integer.parseInt(sDateTime);
		return i;
	}
	/**
	 * 得到月份
	 * @param lefttime
	 * @return
	 */
	public static int formatMonth(long lefttime) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM",Locale.CHINA);
		//sdf.setTimeZone(TimeZone.getTimeZone("Asia/Beijing"));// 设置时区
		String sDateTime = sdf.format(lefttime); // 得到精确到秒的表示：08/31/2006
		// 21:08:00
		int i = Integer.parseInt(sDateTime);
		return i;
	}
}
