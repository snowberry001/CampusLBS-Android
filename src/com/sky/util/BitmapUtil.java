package com.sky.util;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.BitmapFactory.Options;

public class BitmapUtil {
	
	// 压缩图片
	public static Bitmap copressImage(String imgPath, float imagew, float imageh){ 
	    File picture = new File(imgPath); 
	    Options bitmapFactoryOptions = new BitmapFactory.Options(); 
	    //下面这个设置是将图片边界不可调节变为可调节 
	    bitmapFactoryOptions.inJustDecodeBounds = true; 
	    bitmapFactoryOptions.inSampleSize = 2;  
	    Bitmap bmap = BitmapFactory.decodeFile(picture.getAbsolutePath(), bitmapFactoryOptions); 
	    int yRatio = (int) Math.ceil(bitmapFactoryOptions.outHeight / imageh); 
	    int xRatio = (int) Math.ceil(bitmapFactoryOptions.outWidth / imagew); 
	    if (yRatio > 1 || xRatio > 1) { 
	        if (yRatio > xRatio) { 
	            bitmapFactoryOptions.inSampleSize = yRatio; 
	        } else { 
	            bitmapFactoryOptions.inSampleSize = xRatio; 
	        } 
	    }  
	    bitmapFactoryOptions.inJustDecodeBounds = false; 
	    bmap = BitmapFactory.decodeFile(picture.getAbsolutePath(), bitmapFactoryOptions); 
	    if(bmap != null){                
	        return bmap; 
	    } 
	    return null; 
	} 
	
	
	// 调整图片大小
	public static Bitmap resizeImage(String imgPath, int newWidth, int newHeight){
		File picture = new File(imgPath); 
	    Options bitmapFactoryOptions = new BitmapFactory.Options(); 
	    //下面这个设置是将图片边界不可调节变为可调节 
	    bitmapFactoryOptions.inJustDecodeBounds = true; 
	    bitmapFactoryOptions.inSampleSize = 2;  
	    Bitmap bitmap = BitmapFactory.decodeFile(picture.getAbsolutePath(), bitmapFactoryOptions); 
		float oldWidth = bitmap.getWidth(); 
        float oldHeight = bitmap.getHeight();
        // 创建操作图片用的matrix对象 
        Matrix matrix = new Matrix(); 
        // 计算宽高缩放率 
        float scaleWidth = ((float) newWidth) / oldWidth; 
        float scaleHeight = ((float) newHeight) / oldHeight; 
        // 缩放图片动作 
        matrix.postScale(scaleWidth, scaleHeight); 
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, 
        		(int) oldWidth, (int) oldHeight, matrix, true); 
        return newBitmap; 
	}

}
