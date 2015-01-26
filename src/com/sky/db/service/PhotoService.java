package com.sky.db.service;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sky.constant.DBConstant;
import com.sky.db.DBHelper;
import com.sky.db.dao.PhotoDAO;
import com.sky.db.entity.Photo;

public class PhotoService implements PhotoDAO{

	private SQLiteDatabase readDB = null;
	private SQLiteDatabase writeDB = null; 
	
    private static PhotoService photoService = null;
    
    private PhotoService(Context context) {
    	DBHelper dbHelper = new DBHelper(context);
    	readDB = dbHelper.getReadableDatabase();
    	writeDB = dbHelper.getWritableDatabase();
    }
    
    public static synchronized PhotoService getInstacne(Context context) {
        if (photoService == null) {
        	photoService = new PhotoService(context);
        }
        return photoService;
    }
	
	/**
	 * 查询数据
	 * */
	@Override
	public List<Photo> getPhotoList(Photo photo) {
		
		String selection = null;
		String[] selectionArgs = null;
		if(photo != null){
			if(photo.getPhotoId() != null){
				selection = DBConstant.PHOTO_ID + "=?";
				selectionArgs = new String[] {photo.getPhotoId()};
			}
		}
		
		String orderBy = DBConstant.PHOTO_ID + " asc";
		Cursor cursor = readDB.query(DBConstant.TABLE_PHOTO, null, selection, selectionArgs,  null, null, orderBy);
		
		System.out.println(cursor.getCount() + "--------------cursor count---------------");
		
		List<Photo> photoList = new ArrayList<Photo>(cursor.getCount());
		while(cursor.moveToNext()){
			Photo newPhoto = new Photo();
			newPhoto.setPhotoId(cursor.getString(0));
			newPhoto.setPhotoTourId(cursor.getString(1));
			newPhoto.setPhotoTakenTime(cursor.getString(2));
			newPhoto.setPhotoLocation(cursor.getString(3));
			newPhoto.setPhotoDesc(cursor.getString(4));
			newPhoto.setPhotoUrl(cursor.getString(5));
			newPhoto.setPhotoAccurateLocation(cursor.getString(6));
			photoList.add(newPhoto);
		}
		
		cursor.close();
		return photoList;
	}
	
	/**
	 * 查询数据
	 * */
	@Override
	public List<Photo> getPhotoListByTourId(String tourId) {
		
		String selection = DBConstant.PHOTO_TOUR_ID + "=?";
		String[] selectionArgs = new String[] {tourId};

		String orderBy = DBConstant.PHOTO_ID + " asc";
		Cursor cursor = readDB.query(DBConstant.TABLE_PHOTO, null, selection, selectionArgs,  null, null, orderBy);
		
		System.out.println(cursor.getCount() + "--------------cursor count---------------");
		
		List<Photo> photoList = new ArrayList<Photo>(cursor.getCount());
		while(cursor.moveToNext()){
			Photo newPhoto = new Photo();
			newPhoto.setPhotoId(cursor.getString(0));
			newPhoto.setPhotoTourId(cursor.getString(1));
			newPhoto.setPhotoTakenTime(cursor.getString(2));
			newPhoto.setPhotoLocation(cursor.getString(3));
			newPhoto.setPhotoDesc(cursor.getString(4));
			newPhoto.setPhotoUrl(cursor.getString(5));
			newPhoto.setPhotoAccurateLocation(cursor.getString(6));
			photoList.add(newPhoto);
		}
		
		cursor.close();
		return photoList;
	}

	/**
	 * 插入数据
	 * */
	@Override
	public long insertPhoto(Photo photo) {		
		if(photo == null) {
			return -1;
		}
		ContentValues cv = new ContentValues();  
        cv.put(DBConstant.PHOTO_TOUR_ID, photo.getPhotoTourId());
        cv.put(DBConstant.PHOTO_TITLE, photo.getPhotoDesc());
        cv.put(DBConstant.PHOTO_TIME, photo.getPhotoTakenTime());        
        cv.put(DBConstant.PHOTO_LOCATION, photo.getPhotoLocation());
        cv.put(DBConstant.PHOTO_ACCURATE_LOCALTION, photo.getPhotoAccurateLocation());
        cv.put(DBConstant.PHOTO_URL, photo.getPhotoUrl());
        long row = writeDB.insert(DBConstant.TABLE_PHOTO, null, cv);

        return row;  		
	}

	/**
	 * 删除数据
	 * */
	@Override
	public void deletePhoto(Photo photo) {
		int nums = writeDB.delete(DBConstant.TABLE_PHOTO, null, null);
	}

	/**
	 * 删除数据
	 * */
	@Override
	public void deletePhotoById(String photoId) {
		String where = DBConstant.PHOTO_ID + "=?";  
		String[] whereValue = { photoId };
		writeDB.delete(DBConstant.TABLE_PHOTO, where, whereValue);		
	}
	
	
}
