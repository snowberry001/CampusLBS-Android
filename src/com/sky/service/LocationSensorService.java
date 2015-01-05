package com.sky.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

/**
 * 根据传感器数据计算位置信息
 */
public class LocationSensorService extends Service implements SensorEventListener{

    private SensorManager mSensorManager;
    private Sensor mSensor;


    @Override
    public void onCreate(){
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float aX = sensorEvent.values[0];
        float aY = sensorEvent.values[1];
        float aZ = sensorEvent.values[2];
        Toast.makeText(this, "aX : " + aX, Toast.LENGTH_SHORT).show();

        //stopSelf();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
        super.onDestroy();
    }
}
