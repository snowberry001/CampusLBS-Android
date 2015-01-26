package com.sky.activity;

import java.util.List;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.sky.application.TourApplication;
import com.sky.db.entity.Photo;
import com.sky.db.entity.Tour;
import com.sky.db.service.PhotoService;
import com.sky.db.service.TourService;
import com.sky.util.BaiduMapUtil;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.TextView;


/**
 * Tour地图展示界面
 */

public class TourMapActivity extends Activity implements PopupClickListener{

    private TourApplication app = null;
    private MapView mMapView = null;
    private MapController mMapController = null;
    // 图片标注层
    private MyItemizedOverlay photoOverlay = null;
    // 点击图片弹出层
    private PopupOverlay popOverlay = null;
    // 图片标注层
    private ItemizedOverlay<OverlayItem> tourOverlay = null;
    
    private MyLocationOverlay startOverlay;
    private MyLocationOverlay endOverlay;
    
    private View popUpView = null;
    private TextView popText = null;
    private int overlayItemIndex = -1;

    private PhotoService photoService;
    private TourService tourService;
    
    private ProgressDialog loadingDialog;
    
    private String tourId;
    
    private List<Photo> tourPhotoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = TourApplication.getInstance();

        setContentView(R.layout.tour_map_view);

        // 地图设置
        mMapView = (MapView) findViewById(R.id.mapview);
        mMapView.setBuiltInZoomControls(true);
        mMapController = mMapView.getController();
        mMapController.setZoom(17);
        
        photoOverlay = new MyItemizedOverlay(getResources().getDrawable(R.drawable.photo_location), mMapView);        
        popUpView = LayoutInflater.from(this).inflate(R.layout.pop_up_view, null);
        popText = ((TextView)popUpView.findViewById(R.id.popText));        
        popOverlay = new PopupOverlay(mMapView, this);
        
        // 开始、结束图层
        startOverlay = new MyLocationOverlay(mMapView);
        endOverlay = new MyLocationOverlay(mMapView);
        startOverlay.setMarker(getResources().getDrawable(R.drawable.tour_start));
        endOverlay.setMarker(getResources().getDrawable(R.drawable.tour_end));
        
        tourId = getIntent().getExtras().getString("tourId");
        
        photoService = PhotoService.getInstacne(getApplicationContext());
        tourService = TourService.getInstacne(getApplicationContext());
    	tourPhotoList = photoService.getPhotoListByTourId(tourId);  
        
        initMapView();
    }

    // 读取对应坐标文件，初始化路径
    private void initMapView(){
        mMapView.getOverlays().clear();
        photoOverlay.removeAll();
        
        // 初始化路径层
        Tour tour = tourService.getTourListById(tourId);
        String[] points = tour.getTourAccurateLocation().split(",");
        LocationData startData = new LocationData();
        startData.latitude = Double.valueOf(points[0]);
        startData.longitude = Double.valueOf(points[1]);
        startOverlay.setData(startData);
        mMapView.getOverlays().add(startOverlay);
        
        // 初始化图片层
        for(Photo photo : tourPhotoList){
			GeoPoint geoPoint = BaiduMapUtil.parseStrToGeoPoint(photo.getPhotoAccurateLocation());
			photoOverlay.addItem(new OverlayItem(geoPoint, photo.getPhotoDesc(), photo.getPhotoId()));
        }
        
        mMapView.getOverlays().add(photoOverlay);
        mMapView.refresh();
        mMapController.setCenter(BaiduMapUtil.parseStrToGeoPoint(tour.getTourAccurateLocation()));
        
    }

    // OverlayItem点击事件
    public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem>{

        public MyItemizedOverlay(Drawable drawable, MapView mapView) {
            super(drawable, mapView);

        }
        @Override
        protected boolean onTap(int index){
            // 点击弹出照片
            popText.setText(getItem(index).getTitle());
            popOverlay.showPopup(getBitmapFromView(popText), getItem(index).getPoint(), 50);
            overlayItemIndex = index;
            return super.onTap(index);
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

    // popupView点击事件
    @Override
	public void onClickedPopup(int index) {
		popOverlay.hidePop();
        loadingDialog = ProgressDialog.show(TourMapActivity.this, "", "正在加载...", true, true);
        Intent intent = new Intent();
        intent.setClass(TourMapActivity.this, PhotoViewActivity.class);
        intent.putExtra("tourId", tourId);
        intent.putExtra("indexId", overlayItemIndex);
        //intent.putExtra("photoId", photoOverlay.getItem(overlayItemIndex).getSnippet());
        startActivity(intent);
		
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tour_map_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gallery:
            	scanPhotos();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    // 查看Tour相册
 	private void scanPhotos() {
 		Intent intent = new Intent();
 		intent.setClass(TourMapActivity.this, PhotoListActivity.class);
 		intent.putExtra("tourId", String.valueOf(tourId));
 		startActivity(intent);
 	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onResume() {
    	if(loadingDialog != null && loadingDialog.isShowing()){
    		loadingDialog.dismiss();
    	}
        super.onResume();
    }

    protected void onStop() {
        super.onStop();
    }

}
