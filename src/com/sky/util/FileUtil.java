package com.sky.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Environment;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.sky.constant.Constants;

public class FileUtil {

	public static String SDCARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	
	/**
	 * 在对应的tourId目录下
	 * 以时间戳命名生成照片文件
	 * */
	public static File createImageFile(String tourId) {
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String folderPath = SDCARD_PATH + Constants.IMAGE_FOLDER + tourId + "/";
		File tempDir = new File(folderPath);
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}
		File image = new File(folderPath + timeStamp + ".png");
		try {
			image.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	// 换行写入文本
	public static void writeLine(BufferedWriter bw, String line) throws IOException {
		if(bw != null && line != null){
			bw.write(line);
			bw.newLine();
			bw.flush();
		}
	}

	/**
	 *  按行读取并解析位置文件 
	 *  */
	public static List<GeoPoint> readLine(String filename) throws Exception {
		List<GeoPoint> gpList = new ArrayList<GeoPoint>();

		FileReader fr = new FileReader(new File(SDCARD_PATH + File.separator + filename));
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();
		while (line != null && !line.isEmpty()) {
			String[] gp = line.split(",");
			double latitude = Double.parseDouble(gp[0]);
			double longitude = Double.parseDouble(gp[1]);
			GeoPoint geoPoint = new GeoPoint((int) (latitude * 1e6), (int) (longitude * 1e6));
			gpList.add(geoPoint);
		}
		if (br != null) {
			br.close();
		}
		if(fr != null){
			fr.close();
		}
		return gpList;
	}

}
