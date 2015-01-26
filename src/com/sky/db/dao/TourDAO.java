package com.sky.db.dao;

import java.util.List;

import android.database.Cursor;

import com.sky.db.entity.Photo;
import com.sky.db.entity.Tour;

public interface TourDAO {
	
	public List<Tour> getTourList(Tour tour);
	
	public long insertTour(Tour tour);
	
	public void deleteTour(int tourId);
	
	public long updateTour(Tour tour);

	Tour getTourListById(String tourId);
	
}
