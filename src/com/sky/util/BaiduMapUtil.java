package com.sky.util;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class BaiduMapUtil {
	
	/**
	 * 将经纬度字符串解析成GeoPoint
	 **/
	public static GeoPoint parseStrToGeoPoint(String pointStr) {
		String[] points = pointStr.split(",");
		double latitude = Double.parseDouble(points[0]);
		double longitude = Double.parseDouble(points[1]);
		GeoPoint geoPoint = new GeoPoint((int) (latitude * 1e6), (int) (longitude * 1e6));
		return geoPoint;
	}
}
