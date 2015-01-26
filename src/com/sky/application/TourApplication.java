package com.sky.application;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.sky.activity.R;


public class TourApplication extends Application {
	
    private static TourApplication mInstance = null;
    public boolean m_bKeyRight = true;
    public BMapManager mBMapManager = null;
    
    private LocationClient mLocationClient = null;
	private BDLocationListener myLocationListener = new MyLocationListener();
    
    public String locationMsg = null;
	public String accurateLoc = null;
	
	public TextView locationTv;
	public TextView accurateLocTv;
	
	private static DisplayImageOptions options;
    
    @Override
    public void onCreate() {
	    super.onCreate();
	    
	    options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_stub)
		.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageOnFail(R.drawable.ic_error)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	    
	    initImageLoader(getApplicationContext());
	    
		mInstance = this;
		initEngineManager(getApplicationContext());
	}
	
    // 初始化imageLoader
    public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024) 
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() 
				.defaultDisplayImageOptions(options)
				.build();

		ImageLoader.getInstance().init(config);
	}
    
    
    // 初始化百度地图
	public void initEngineManager(Context context) {
        if (mBMapManager == null) {
        	System.out.println("-------init baidu map------------------");
            mBMapManager = new BMapManager(context);
            mBMapManager.init(new MyGeneralListener());
        }
	}
	
	public static TourApplication getInstance() {
		return mInstance;
	}
	
	// 开始定位
	public void startLocation(){
		mLocationClient = new LocationClient(this);     
	    mLocationClient.registerLocationListener(myLocationListener);
	    setLocationOption();
	    mLocationClient.start();
	    if (mLocationClient.isStarted()) {
			mLocationClient.requestLocation();
		}
	}
	
	// 停止定位
	public void stopLocation(){
		if(mLocationClient.isStarted()){
			mLocationClient.stop();
		}
		mBMapManager.stop();
	}
	
	// 设置Option
	private void setLocationOption() {
		try {
			LocationClientOption option = new LocationClientOption();
			option.setLocationMode(LocationMode.Hight_Accuracy);
			option.setCoorType("bd09ll");
			option.setIsNeedAddress(true);
			option.setNeedDeviceDirect(true);
			mLocationClient.setLocOption(option);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 实现监听器
	public class MyLocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation location) {
			if(location == null){
				Toast.makeText(getApplicationContext(), "定位失败！请检查网络连接和GPS服务！", Toast.LENGTH_SHORT).show();
				return;
			}
			setAccurateLoc(String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude()));	
			setLocationMsg(location.getAddrStr());
			System.out.println(accurateLoc + "," + getLocationMsg());
			if(locationTv != null){
				locationTv.setText(location.getAddrStr());
			}
			if(accurateLocTv != null){
				accurateLocTv.setText(getAccurateLoc());
			}
			
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub
		}
			
	}
	
	
	
	// 常用事件监听，用来处理异常的网络错误，授权验证错误等
    static class MyGeneralListener implements MKGeneralListener {
        @Override
        public void onGetNetworkState(int iError) {
        	
        	System.out.println(iError + "----------iError in app--------------");
        	
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(TourApplication.getInstance().getApplicationContext(), "请检查网络连接！",
                        Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(TourApplication.getInstance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError != 0) {
                TourApplication.getInstance().m_bKeyRight = false;
                Toast.makeText(TourApplication.getInstance().getApplicationContext(), "授权Key错误！",
                        Toast.LENGTH_LONG).show();
            } else{
                TourApplication.getInstance().m_bKeyRight = true;
            }
        }
    }
    
    public String getLocationMsg() {
		return locationMsg;
	}

	public void setLocationMsg(String locationMsg) {
		this.locationMsg = locationMsg;
	}
    
	public String getAccurateLoc() {
		return accurateLoc;
	}

	public void setAccurateLoc(String accurateLoc) {
		this.accurateLoc = accurateLoc;
	}
}