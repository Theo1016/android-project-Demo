package com.theo.sdk.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * DATE工具类
 * @author Theo
 *
 */
public class DateUtils {
	
	public static long getCurrentTime() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTimeInMillis();

	}

	
	public static String format12Time(long time) {
		return format(time, "yyyy-MM-dd hh:MM:ss");
	}

	
	public static String format24Time(long time) {
		return format(time, "yyyy-MM-dd HH:MM:ss");
	}

	
	public static String format(long time, String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern,
				Locale.getDefault());
		Date date = new Date(time);
		return formatter.format(date);
	}

	
	public static int getCurrentDay() {
		return Calendar.DAY_OF_MONTH;
	}

	
	public static int getCurrentMonth() {
		return Calendar.MONTH;
	}

	
	public static int getCurrentYear() {
		return Calendar.YEAR;
	}
}
