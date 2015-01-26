package com.sky.db.service;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sky.constant.DBConstant;
import com.sky.db.DBHelper;
import com.sky.db.dao.TourDAO;
import com.sky.db.entity.Tour;

public class TourService implements TourDAO{

	private SQLiteDatabase readDB = null;
	private SQLiteDatabase writeDB = null; 
	
    private static TourService tourService = null;
    
    private TourService(Context context) {
    	DBHelper dbHelper = new DBHelper(context);
    	readDB = dbHelper.getReadableDatabase();
    	writeDB = dbHelper.getWritableDatabase();
    }
    
    public static synchronized TourService getInstacne(Context context) {
        if (tourService == null) {
        	tourService = new TourService(context);
        }
        return tourService;
    }
	
	/**
	 * 查询数据
	 * */
	@Override
	public List<Tour> getTourList(Tour tour) {
		
		String selection = null;
		String[] selectionArgs = null;
		if(tour != null){
			if(tour.getTourId() != null){
				selection += DBConstant.TOUR_ID + "=?";
				selectionArgs = new String[] {tour.getTourId()};
			}
		}
		
		String orderBy = DBConstant.TOUR_START_TIME + " desc";
		Cursor cursor = readDB.query(DBConstant.TABLE_TOUR, null, selection, selectionArgs,  null, null, orderBy);
		
		List<Tour> tourList = new ArrayList<Tour>(cursor.getCount());
		while(cursor.moveToNext()){
			Tour newTour = new Tour();
			newTour.setTourId(cursor.getString(0));
			newTour.setTourStartTime(cursor.getString(1));
			newTour.setTourEndTime(cursor.getString(2));
			newTour.setTourTitle(cursor.getString(3));
			newTour.setTourLocation(cursor.getString(4));
			newTour.setTourAccurateLocation(cursor.getString(5));
			newTour.setTourFileUrl(cursor.getString(6));
			tourList.add(newTour);
		}
		
		cursor.close();
		return tourList;
	}
	
	/**
	 * 查询数据
	 * */
	@Override
	public Tour getTourListById(String tourId) {
		
		String selection = DBConstant.TOUR_ID + "=?";
		String[] selectionArgs = new String[] {tourId};
		String orderBy = DBConstant.TOUR_START_TIME + " desc";
		Cursor cursor = readDB.query(DBConstant.TABLE_TOUR, null, selection, selectionArgs,  null, null, orderBy);
		Tour tour = new Tour();
		if(cursor.moveToNext()){
			tour.setTourId(cursor.getString(0));
			tour.setTourStartTime(cursor.getString(1));
			tour.setTourEndTime(cursor.getString(2));
			tour.setTourTitle(cursor.getString(3));
			tour.setTourLocation(cursor.getString(4));
			tour.setTourAccurateLocation(cursor.getString(5));
			tour.setTourFileUrl(cursor.getString(6));
		}
		cursor.close();
		return tour;
	}
	

	/**
	 * 插入数据
	 * */
	@Override
	public long insertTour(Tour tour) {		
		if(tour == null) {
			return -1;
		}
		ContentValues cv = new ContentValues();  
        cv.put(DBConstant.TOUR_TITLE, tour.getTourTitle());
        cv.put(DBConstant.TOUR_START_TIME, tour.getTourStartTime());        
        cv.put(DBConstant.TOUR_LOCATION, tour.getTourLocation());
        cv.put(DBConstant.TOUR_ACCURATE_LOCALTION, tour.getTourAccurateLocation());
        cv.put(DBConstant.TOUR_FILE_URL, tour.getTourFileUrl());
        long row = writeDB.insert(DBConstant.TABLE_TOUR, null, cv);

        return row;  		
	}

	/**
	 * 删除数据
	 * */
	@Override
	public void deleteTour(int tourId) {
		
		String where = DBConstant.TOUR_ID + "=?";  
		String[] whereValue = { Integer.toString(tourId) };
		int nums = writeDB.delete(DBConstant.TABLE_TOUR, where, whereValue);
		System.out.println(nums + "------delete nums--------");
	}

	/**
	 * 更新数据
	 * */
	@Override
	public long updateTour(Tour tour) {
		if(tour == null){
			return -1;
		}
		String where = DBConstant.TOUR_ID + "=?";  
		String[] whereValue = { tour.getTourId() };
        ContentValues cv = new ContentValues();  
        cv.put(DBConstant.TOUR_TITLE, tour.getTourTitle());
        cv.put(DBConstant.TOUR_LOCATION, tour.getTourLocation());
        cv.put(DBConstant.TOUR_ACCURATE_LOCALTION, tour.getTourAccurateLocation());
        cv.put(DBConstant.TOUR_END_TIME, tour.getTourEndTime());
        cv.put(DBConstant.TOUR_FILE_URL, tour.getTourFileUrl());
        long row = writeDB.update(DBConstant.TABLE_TOUR, cv, where, whereValue);
        
        return row;
	}

	
	
	
}
