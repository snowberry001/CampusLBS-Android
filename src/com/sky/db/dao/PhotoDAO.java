package com.sky.db.dao;

import java.util.List;

import com.sky.db.entity.Photo;

public interface PhotoDAO {

	public List<Photo> getPhotoList(Photo photo);
	
	public long insertPhoto(Photo photo);
	
	public void deletePhoto(Photo photo);

	List<Photo> getPhotoListByTourId(String tourId);

	void deletePhotoById(String photoId);
	
	
}
