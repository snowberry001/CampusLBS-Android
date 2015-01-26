package com.sky.constant;

public class DBConstant {

	public final static String DATABASE_NAME = "TOUR.db";
	public final static int DATABASE_VERSION = 1;
	public final static String TABLE_TOUR = "TOUR";
    public final static String TABLE_PHOTO = "PHOTO";
	
	// TOUR数据表字段
	public final static String TOUR_ID = "tour_id";
	public final static String TOUR_START_TIME = "tour_start_time";
    public final static String TOUR_END_TIME = "tour_end_time";
	public final static String TOUR_LOCATION = "tour_location";
	public final static String TOUR_TITLE = "tour_title";
	public final static String TOUR_FILE_URL = "tour_file_url";
	public final static String TOUR_ACCURATE_LOCALTION = "tour_accurate_location";


    // PHOTO数据表字段
    public final static String PHOTO_ID = "photo_id";
    public final static String PHOTO_TOUR_ID = "photo_tour_id";
    public final static String PHOTO_TIME = "photo_time";
    public final static String PHOTO_LOCATION = "photo_location";
    public final static String PHOTO_TITLE = "photo_title";
    public final static String PHOTO_URL = "photo_url";
    public final static String PHOTO_ACCURATE_LOCALTION = "photo_accurate_location";
    public final static String PHOTO_VOICE_URL = "photo_voice_url";
	
}
