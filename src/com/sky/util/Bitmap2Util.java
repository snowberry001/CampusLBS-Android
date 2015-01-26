package com.sky.util;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.sky.constant.Constants;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

public class Bitmap2Util {
	
	public static class MyBitmapEntity {
		float x;
		float y;
		float width;
		float height;
		static int devide = 1;
		int index = -1;
	}
	
	public static List<MyBitmapEntity> getBitmapEntitys() {
		List<MyBitmapEntity> mList = new LinkedList<MyBitmapEntity>();
		String[] pointArr = {"1.0,1.0,28.0,28.0", 
				"31.0,1.0,28.0,28.0", 
				"1.0,31.0,28.0,28.0",
				"31.0,31.0,28.0,28.0"};
		int length = pointArr.length;
		for (int i = 0; i < length; i++) {
			String content = pointArr[i];
			String[] arr2 = content.split(",");
			MyBitmapEntity entity = null;
			for (int j = 0; j < arr2.length; j++) {
				entity = new MyBitmapEntity();
				entity.x = Float.valueOf(arr2[0]);
				entity.y = Float.valueOf(arr2[1]);
				entity.width = Float.valueOf(arr2[2]);
				entity.height = Float.valueOf(arr2[3]);
			}
			mList.add(entity);
		}
		return mList;
	}
	
	static public Drawable getScaleDraw(String imgPath, Context mContext) {

		Bitmap bitmap = null;
		try {
			File imageFile = new File(imgPath);
			if (!imageFile.exists()) {
				return null;
			}
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(imgPath, opts);

			opts.inSampleSize = computeSampleSize(opts, -1, 800 * 480);

			opts.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(imgPath, opts);

		} catch (OutOfMemoryError err) {
			Log.d("BitmapUtil", "[getScaleDraw] out of memory");

		}
		if (bitmap == null) {
			return null;
		}
		Drawable resizeDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
		return resizeDrawable;
	}

	public static void saveMyBitmap(Context mContext, Bitmap bitmap, String desName) {
		FileOutputStream fOut = null;
		try {
			if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
				fOut = mContext.openFileOutput(desName + ".png", Context.MODE_PRIVATE);
			} else {
				File f = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/tourCampus/" + desName + ".png");
				f.createNewFile();
				fOut = new FileOutputStream(f);
			}
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static public Bitmap getScaleBitmap(Resources res, int id) {

		Bitmap bitmap = null;
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeResource(res, id, opts);

			opts.inSampleSize = computeSampleSize(opts, -1, 800 * 480);

			opts.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeResource(res, id, opts);
		} catch (OutOfMemoryError err) {
			Log.d("BitmapUtil", "[getScaleBitmap] out of memory");

		}
		return bitmap;
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {

		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;

	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {

		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}

	}

	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	public static Bitmap decodeBitmap(Resources res, int id) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 通过这个bitmap获取图片的宽和高
		Bitmap bitmap = BitmapFactory.decodeResource(res, id, options);
		if (bitmap == null) {

		}
		float realWidth = options.outWidth;
		float realHeight = options.outHeight;

		// 计算缩放比
		int scale = (int) ((realHeight > realWidth ? realHeight : realWidth) / 100);
		if (scale <= 0) {
			scale = 1;
		}

		options.inSampleSize = scale;
		options.inJustDecodeBounds = false;
		// 注意这次要把options.inJustDecodeBounds 设为 false,这次图片是要读取出来的。
		bitmap = BitmapFactory.decodeResource(res, id, options);

		return bitmap;
	}

	public static Bitmap getCombineBitmaps(Bitmap... bitmaps) {
		List<MyBitmapEntity> mEntityList = getBitmapEntitys();
		Bitmap newBitmap = Bitmap.createBitmap(Constants.COMBINE_PHOTO_WIDTH, 
				Constants.COMBINE_PHOTO_HEIGHT, Config.ARGB_8888);
		for (int i = 0; i < bitmaps.length; i++) {
			newBitmap = mixtureBitmap(newBitmap, bitmaps[i], 
					new PointF(mEntityList.get(i).x, mEntityList.get(i).y));
		}
		return newBitmap;
	}

	/**
	 * 将多个Bitmap合并成一个图片。
	 * 
	 * @param int 将多个图合成多少列
	 * @param Bitmap... 要合成的图片
	 * @return
	 */
	public static Bitmap combineBitmaps(int columns, Bitmap... bitmaps) {
		if (columns <= 0 || bitmaps == null || bitmaps.length == 0) {
			throw new IllegalArgumentException(
					"Wrong parameters: columns must > 0 and bitmaps.length must > 0.");
		}
		int maxWidthPerImage = 20;
		int maxHeightPerImage = 20;
		for (Bitmap b : bitmaps) {
			maxWidthPerImage = maxWidthPerImage > b.getWidth() ? maxWidthPerImage
					: b.getWidth();
			maxHeightPerImage = maxHeightPerImage > b.getHeight() ? maxHeightPerImage
					: b.getHeight();
		}

		int rows = 0;
		if (columns >= bitmaps.length) {
			rows = 1;
			columns = bitmaps.length;
		} else {
			rows = bitmaps.length % columns == 0 ? bitmaps.length / columns
					: bitmaps.length / columns + 1;
		}
		Bitmap newBitmap = Bitmap.createBitmap(columns * maxWidthPerImage, rows
				* maxHeightPerImage, Config.ARGB_8888);

		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < columns; y++) {
				int index = x * columns + y;
				if (index >= bitmaps.length)
					break;
				newBitmap = mixtureBitmap(newBitmap, bitmaps[index],
						new PointF(y * maxWidthPerImage, x * maxHeightPerImage));
			}
		}
		return newBitmap;
	}

	/**
	 * Mix two Bitmap as one.
	 * 
	 * @param bitmapOne
	 * @param bitmapTwo
	 * @param point
	 *            where the second bitmap is painted.
	 * @return
	 */
	public static Bitmap mixtureBitmap(Bitmap first, Bitmap second, PointF fromPoint) {
		if (first == null || second == null || fromPoint == null) {
			return null;
		}
		Bitmap newBitmap = Bitmap.createBitmap(first.getWidth(), first.getHeight(), Config.ARGB_8888);
		Canvas cv = new Canvas(newBitmap);
		cv.drawBitmap(first, 0, 0, null);
		cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
		cv.save(Canvas.ALL_SAVE_FLAG);
		cv.restore();
		return newBitmap;
	}

	public static void getScreenWidthAndHeight(Activity mContext) {
		DisplayMetrics metric = new DisplayMetrics();
		mContext.getWindowManager().getDefaultDisplay().getMetrics(metric);
	}

}
