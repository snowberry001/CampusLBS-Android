package com.sky.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.sky.constant.Constants;
import com.sky.util.FileUtil;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

/**
 * 根据GPS定位获取位置信息
 */
public class LocationService extends Service implements LocationListener{

	private LocationManager locationManager;
	private String tourId;
	private BufferedWriter bw;
	
    @Override
    public void onCreate(){
    	locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        tourId = intent.getExtras().getString("tourId");
    	System.out.println(tourId + "-----tourId in location service-------");
    	
    	try {
			bw = new BufferedWriter(new FileWriter(new File(Environment
					.getExternalStorageDirectory().getAbsolutePath() 
					+ Constants.FILE_FOLDER + tourId + ".txt")));
		} catch (IOException e) {
			e.printStackTrace();			
			stopSelf();			
		}
    	
    	startGPSLocation();
    	
    	Toast.makeText(this, "已启动位置服务，正在为您实时记录位置信息！", Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;
    }
        
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    
    private void openGPSSettings() {
        Intent gpsIntent = new Intent();
        gpsIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
        gpsIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(this, 0, gpsIntent, 0).send();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        /*if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Settings.Secure.setLocationProviderEnabled(getContentResolver(), LocationManager.GPS_PROVIDER, true);
        }
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent,0);*/

    }
    
    private void startGPSLocation()
    {
        // 查找到服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        String provider = locationManager.getBestProvider(criteria, true); 
        if(provider.equals(LocationManager.GPS_PROVIDER)){
        	openGPSSettings();
        }
        Location location = locationManager.getLastKnownLocation(provider); 
        try {
			FileUtil.writeLine(bw, new StringBuffer(location.getLatitude() + "")
					.append(",").append(location.getLongitude() + "").toString());
		} catch (IOException e) {			
			e.printStackTrace();			
			stopSelf();
		}
        // 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过100米
        locationManager.requestLocationUpdates(provider, 5 * 1000, 100, this);
    }
    
    
    
	@Override
	public void onLocationChanged(Location location) {
		if(location == null){
            Toast.makeText(getApplicationContext(), "GPS定位失败！",Toast.LENGTH_SHORT).show();
        }else{
        	try {
    			FileUtil.writeLine(bw, new StringBuffer(location.getLatitude() + "")
    					.append(",").append(location.getLongitude() + "").toString());
    		} catch (IOException e) {			
    			e.printStackTrace();			
    			stopSelf();
    		}
        }
	}


	@Override
	public void onProviderDisabled(String arg0) {
		
	}


	@Override
	public void onProviderEnabled(String arg0) {
		
	}


	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		
	}
	
	
    @Override
    public void onDestroy() {
    	try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	locationManager.removeUpdates(this);  
    	stopSelf();
        super.onDestroy();
    }
}
