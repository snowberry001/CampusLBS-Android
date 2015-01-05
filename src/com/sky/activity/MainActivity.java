package com.sky.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.sky.control.MyActionBar;
import com.sky.service.LocationSensorService;

public class MainActivity extends Activity implements OnClickListener {

    private Button startButton;
    private Button stopButton;
    private TextView textView;
    private LocationManager locationManager;
    private Intent serviceIntent = null;

    private MyActionBar myActionBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myActionBar = new MyActionBar(this, null);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        startButton = (Button)findViewById(R.id.start);
        stopButton = (Button)findViewById(R.id.stop);
        textView = (TextView)findViewById(R.id.show);
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
    }

    private void initActionBar(){
        myActionBar.setBtnRightText("添加");
        myActionBar.setTvTitle("旅游列表");
        myActionBar.setBtnRightListener(this);
    }


    @Override
    public void onClick(View view){

        switch (view.getId()){
            case R.id.start:
                start();
                serviceIntent = new Intent(this, LocationSensorService.class);
                startService(serviceIntent);
                Toast.makeText(this, "启动成功！",Toast.LENGTH_SHORT).show();
                break;
            case R.id.stop:
                stop();
                stopService(serviceIntent);
                Toast.makeText(this, "停止成功！",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_right:
                Intent intent= new Intent(this, NewTourActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 启动位置记录
     * */
    private void start(){

        openGPSSettings();
        getLocation();

    }

    /**
     * 结束位置记录
     * */
    private void stop(){
        //Settings.Secure.setLocationProviderEnabled(getContentResolver(), LocationManager.GPS_PROVIDER, false);
        locationManager.removeUpdates(locationListener);
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


    private void getLocation()
    {
        // 查找到服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
        Location location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置

        // 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米
        locationManager.requestLocationUpdates(provider, 5 * 1000, 500, locationListener);
    }

    private void showLocation(Location location){
        textView.setText(location.getLatitude() + "," + location.getLongitude());
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            if(location == null){
                Toast.makeText(getApplicationContext(), "位置为空！",Toast.LENGTH_SHORT).show();
            }else{
                showLocation(location);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
}
