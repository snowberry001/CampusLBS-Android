package com.sky.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	private final static String DATABASE_NAME = "TOUR.db";
	private final static int DATABASE_VERSION = 1;
	private final static String TABLE_TOUR = "TOUR";
    private final static String TABLE_PHOTO = "PHOTO";
	
	// TOUR数据表字段
	private final static String TOUR_ID = "tour_id";
	private final static String TOUR_START_TIME = "tour_start_time";
    private final static String TOUR_END_TIME = "tour_end_time";
	private final static String TOUR_LOCATION = "tour_location";
	private final static String TOUR_TITLE = "tour_title";
	private final static String TOUR_FILE_URL = "tour_file_url";
	private final static String TOUR_ACCURATE_LOCALTION = "tour_accurate_location";


    // PHOTO数据表字段
    private final static String PHOTO_ID = "photo_id";
    private final static String PHOTO_TOUR_ID = "photo_tour_id";
    private final static String PHOTO_TIME = "photo_time";
    private final static String PHOTO_LOCATION = "photo_location";
    private final static String PHOTO_TITLE = "photo_title";
    private final static String PHOTO_URL = "photo_url";
    private final static String PHOTO_ACCURATE_LOCALTION = "photo_accurate_location";
    private final static String PHOTO_VOICE_URL = "photo_voice_url";

	
	private SQLiteDatabase readDB = getReadableDatabase();
	private SQLiteDatabase writeDB = getWritableDatabase();
	
	
	public DBHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION); 
	}
	
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String tout_sql = "CREATE TABLE " + TABLE_TOUR + "(" + TOUR_ID + " INTEGER primary key autoincrement, "
				+ TOUR_START_TIME + " text,"
                + TOUR_END_TIME + " text,"
				+ TOUR_TITLE + " text,"
                + TOUR_LOCATION + " text,"
                + TOUR_ACCURATE_LOCALTION + " text,"
                + TOUR_FILE_URL + " text"
                + ");";

        String photo_sql = "CREATE TABLE " + TABLE_PHOTO + "(" + PHOTO_ID + " INTEGER primary key autoincrement, "
                + PHOTO_TOUR_ID + " integer,"
                + PHOTO_TIME + " text,"
                + PHOTO_LOCATION + " text,"
                + PHOTO_TITLE + " text,"
                + PHOTO_URL + " text,"
                + PHOTO_ACCURATE_LOCALTION + " text,"
                + PHOTO_VOICE_URL + " text"
                + ");";

		db.execSQL(tout_sql);
        db.execSQL(photo_sql);
    }
	
	public void execSql(){
		
		String tour_sql1 = "DROP TABLE IF EXISTS " + TABLE_TOUR;
        String photo_sql1 = "DROP TABLE IF EXISTS " + TABLE_PHOTO;
        writeDB.execSQL(tour_sql1);
        writeDB.execSQL(photo_sql1);
		
        String tour_sql = "CREATE TABLE " + TABLE_TOUR + "(" + TOUR_ID + " INTEGER primary key autoincrement, "
                + TOUR_START_TIME + " text,"
                + TOUR_END_TIME + " text,"
                + TOUR_TITLE + " text,"
                + TOUR_LOCATION + " text,"
                + TOUR_ACCURATE_LOCALTION + " text,"
                + TOUR_FILE_URL + " text"
                + ");";

        String photo_sql = "CREATE TABLE " + TABLE_PHOTO + "(" + PHOTO_ID + " INTEGER primary key autoincrement, "
                + PHOTO_TOUR_ID + " integer,"
                + PHOTO_TIME + " text,"
                + PHOTO_LOCATION + " text,"
                + PHOTO_TITLE + " text,"
                + PHOTO_URL + " text,"
                + PHOTO_ACCURATE_LOCALTION + " text,"
                + PHOTO_VOICE_URL + " text"
                + ");";

        writeDB.execSQL(tour_sql);
        writeDB.execSQL(photo_sql);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		String tour_sql = "DROP TABLE IF EXISTS " + TABLE_TOUR;
        String photo_sql = "DROP TABLE IF EXISTS " + TABLE_PHOTO;
		db.execSQL(tour_sql);
        db.execSQL(photo_sql);
		onCreate(db);
	}
	
	// 获取所有的tour记录
	public Cursor selectAllTour(){
		Cursor cursor = readDB.query(TABLE_TOUR, null, null, null, null, null, TOUR_START_TIME + " desc");
		return cursor;
	}
	
	// 获取指定ID的tour记录
	public Cursor selectOneTour(String id) {
		String where = TOUR_ID + "=?";
		String[] whereValue = { id };
		Cursor cursor = readDB.query(TABLE_TOUR, null, where, whereValue,  null, null, null);
		return cursor;
	} 
	
	// 插入数据
	public long createTour(String startTime, String location, 
			String title, String accurateLoc) {
        ContentValues cv = new ContentValues();  
        cv.put(TOUR_TITLE, title);
        cv.put(TOUR_START_TIME, startTime);
        cv.put(TOUR_LOCATION, location);        
        cv.put(TOUR_ACCURATE_LOCALTION, accurateLoc);
        long row = writeDB.insert(TABLE_TOUR, null, cv);
        return row;  
    }  
  
	// 删除记录
    public void deleteTour(int id) {  
        String where = TOUR_ID + "=?";  
		String[] whereValue = { Integer.toString(id) };
        writeDB.delete(TABLE_TOUR, where, whereValue);
    }  
  
    // 更新tour
    public long updateTour(long id, String title, String location, String accurateLoc, String stopTime, String fileUrl) {
        String where = TOUR_ID + "=?";  
		String[] whereValue = { Long.toString(id) };
        ContentValues cv = new ContentValues();  
        cv.put(TOUR_TITLE, title);
        cv.put(TOUR_LOCATION, location);
        cv.put(TOUR_ACCURATE_LOCALTION, accurateLoc);
        cv.put(TOUR_END_TIME, stopTime);
        cv.put(TOUR_FILE_URL, fileUrl);
        long row = writeDB.update(TABLE_TOUR, cv, where, whereValue);
        return row;
    }  

}