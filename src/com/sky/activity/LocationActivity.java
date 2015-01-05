package com.sky.activity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import com.sky.activity.R;
import com.sky.application.TourApplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.TextView;
import android.widget.Toast;

public class LocationActivity extends Activity {
	
	private TourApplication app = null;
	private MapView mMapView = null;
	private MapController mMapController = null;
	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();
	private LocationData locData = new LocationData();
	private MyLocationOverlay myLocationOverlay = null;
	private boolean mLocationInit = true;
	
    
    private MKMapTouchListener myMapTouchListener = new MyMKMapTouchListener();
    private MKSearch mySearch = new MKSearch();

    
    private PopupOverlay popOverlay = null;
    private View popUpView = null;
    
    private String locationMsg = null;
    private String accurateLoc = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		app = (TourApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.initEngineManager(getApplicationContext());
        }
		
		// 注意：请在试用setContentView前初始化BMapManager对象，否则会报错
		setContentView(R.layout.location_main);
		
		mMapView = (MapView) findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(true);
		mMapController = mMapView.getController();
		mMapController.setZoom(18);
		myLocationOverlay = new MyLocationOverlay(mMapView); 
		myLocationOverlay.setMarker(getResources().getDrawable(R.drawable.pic_location));
		mMapView.getOverlays().add(myLocationOverlay);
		
		
		mMapView.regMapTouchListner(myMapTouchListener); // 地图点击监听器
		
		mySearch.init(app.mBMapManager, new MySearchListener()); // 搜索服务监听器

		// PopOverlay
		popUpView = LayoutInflater.from(this).inflate(R.layout.popup_view, null);  
		popOverlay = new PopupOverlay(mMapView, null);
		
		// 开始定位
		mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
	    mLocationClient.registerLocationListener(myListener);    //注册监听函数
	    setLocationOption();
		
		// 判断当前有没有位置信息
		Bundle bundle = this.getIntent().getExtras();
		if(bundle != null && bundle.containsKey("accurateLoc")){
			accurateLoc = bundle.getString("accurateLoc");
			System.out.println(accurateLoc + "------accurateLoc----");
			if(accurateLoc != null && !"".equals(accurateLoc)){
				String[] point = accurateLoc.split(",");
				locData.latitude = Double.valueOf(point[0]);
				locData.longitude = Double.valueOf(point[1]);
				myLocationOverlay.setData(locData);
				mMapView.refresh();
				GeoPoint geoPoint = new GeoPoint((int) (locData.latitude * 1e6), (int) (locData.longitude * 1e6));
				mMapController.setCenter(geoPoint);
			}else{
				if (mLocationInit) {
			    	mLocationClient.start();
				} else {
					Toast.makeText(this, "请设置定位相关的参数", Toast.LENGTH_SHORT).show();
					return;
				}
				if (mLocationClient.isStarted()) {
					mLocationClient.requestLocation();
				}
			}		
		} 
	}
	
	// MapView点击事件监听器
	public class MyMKMapTouchListener implements MKMapTouchListener{

		@Override
		public void onMapClick(GeoPoint arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMapDoubleClick(GeoPoint arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMapLongClick(GeoPoint geoPoint) {
			// TODO Auto-generated method stub
			LocationData locData = new LocationData();
			locData.latitude = geoPoint.getLatitudeE6()/(1e6);
			locData.longitude = geoPoint.getLongitudeE6()/(1e6);
			myLocationOverlay.setData(locData);
			mMapView.getOverlays().clear();
			mMapView.getOverlays().add(myLocationOverlay);
			
			// 检索当前地址
			mySearch.reverseGeocode(geoPoint);
			mMapView.refresh();
		}
		
	}
	
	// 检索服务
	public class MySearchListener implements MKSearchListener {  
		//返回地址信息搜索结果  
		@Override  
        public void onGetAddrResult(MKAddrInfo result, int iError) {  
			System.out.println(iError + "," +result.strAddr);
			if(iError != 0){
				Toast.makeText(getApplicationContext(), "地址检索失败！", Toast.LENGTH_SHORT).show();
				return;
			}
			if(result.type == MKAddrInfo.MK_REVERSEGEOCODE){
				TextView popText = ((TextView)popUpView.findViewById(R.id.popText));  
				popText.setText(result.strAddr);
				popOverlay.showPopup(getBitmapFromView(popText), result.geoPt, 50);
				mMapView.refresh();
				System.out.println(locationMsg + "long press");
				locationMsg = result.strAddr;
				accurateLoc = String.valueOf(result.geoPt.getLatitudeE6()/(1e6)) + ","
						+ String.valueOf(result.geoPt.getLongitudeE6()/(1e6));
				Toast.makeText(getApplicationContext(), result.strAddr + ";" + result.strBusiness, Toast.LENGTH_LONG).show();	
				mMapController.animateTo(result.geoPt);
			}
			
			
        }  
		//返回驾乘路线搜索结果  
        @Override  
        public void onGetDrivingRouteResult(MKDrivingRouteResult result, int iError) {  
                
        }  
        //返回poi搜索结果  
        @Override  
        public void onGetPoiResult(MKPoiResult result, int type, int iError) {  
                
        }  
        //返回公交搜索结果  
        @Override  
        public void onGetTransitRouteResult(MKTransitRouteResult result, int iError) {  
                
        }  
        //返回步行路线搜索结果  
        @Override  
        public void onGetWalkingRouteResult(MKWalkingRouteResult result, int iError) {  
                
        }
        //返回公交车详情信息搜索结果  
        @Override      
        public void onGetBusDetailResult(MKBusLineResult result, int iError) {  
                
        }  
        // 在此处理短串请求返回结果
		@Override
		public void onGetShareUrlResult(MKShareUrlResult result, int type, int error) {
			
		}
		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
	}

	
	// 实现监听器
	public class MyLocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if(location == null){
				Toast.makeText(getApplicationContext(), "定位失败！请检查网络连接和GPS服务！", Toast.LENGTH_SHORT).show();
				return;
			}
			locationMsg = location.getAddrStr();
			accurateLoc = String.valueOf(location.getLatitude()) + ","
					+ String.valueOf(location.getLongitude());
			System.out.println(accurateLoc + "---accurateLoc in listener----");
			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			locData.direction = location.getDirection();
			locData.accuracy = location.getRadius();  
			myLocationOverlay.setData(locData);
			mMapView.refresh();
			GeoPoint point = new GeoPoint((int) (locData.latitude * 1e6), (int) (locData.longitude * 1e6));
			mMapController.setCenter(point);
			
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	// 设置Option
	private void setLocationOption() {
		try {
			LocationClientOption option = new LocationClientOption();
			option.setLocationMode(LocationMode.Hight_Accuracy);
			option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
			//option.setScanSpan(5000);
			option.setIsNeedAddress(true);
			option.setNeedDeviceDirect(true);
			mLocationClient.setLocOption(option);
		} catch (Exception e) {
			e.printStackTrace();
			mLocationInit = false;
		}
	}
	
	private Bitmap getBitmapFromView(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	} 
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.location_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int item_id = item.getItemId();
		switch (item_id) {
			case R.id.mylocation: 
				setLocationOption();
				mLocationClient.requestLocation();
				popOverlay.hidePop();
				break;
			case R.id.locateOK:
				Intent intent = new Intent();
				intent.setClass(LocationActivity.this, NewTourActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("accurateLoc", accurateLoc);
				if (locationMsg != null) {
					bundle.putString("locationMsg", locationMsg);
				}
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
				break;
		}
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		mLocationClient.stop();
		if(mySearch != null){
			mySearch.destory();
			mySearch = null;
		}
		mMapView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		if (app.mBMapManager != null) {
			app.mBMapManager.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		if (app.mBMapManager != null) {
			app.mBMapManager.start();
		}
		super.onResume();
	}


}
