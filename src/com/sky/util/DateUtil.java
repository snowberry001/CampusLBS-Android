package com.sky.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	
	/**
	 * 获取当前日期信息, 精确到秒
	 **/
	public static String getCurrentDateTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = format.format(new Date(System.currentTimeMillis()));
		return dateStr;
	}
	
	
	/**
	 * 解析日期字符串
	 **/
	public static String parseDateString(String dateStr){
		SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat format = new SimpleDateFormat("MM-dd");
		String result = null;
		try {
			Date date = parseFormat.parse(dateStr);
			result = format.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
			result = null;
		}
		return result;
	}
}
