package com.sky.activity;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.sky.application.TourApplication;
import com.sky.db.DBHelper;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.TextView;


/**
 * Tour地图展示界面
 */

public class MapShowActivity extends Activity {

    private TourApplication app = null;
    private MapView mMapView = null;
    private MapController mMapController = null;
    private MyItemizedOverlay diaryOverlay = null;
    private PopupOverlay popOverlay = null;
    private View popUpView = null;
    private TextView popText = null;
    private int overlayItemIndex = -1;

    private DBHelper dbHelper;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (TourApplication)getApplication();
        if (app.mBMapManager == null) {
            app.initEngineManager(getApplicationContext());
        }
        setContentView(R.layout.tour_map);

        // 地图设置
        mMapView = (MapView) findViewById(R.id.mapview);
        mMapView.setBuiltInZoomControls(true);
        mMapController = mMapView.getController();
        mMapController.setZoom(15);
        diaryOverlay = new MyItemizedOverlay(getResources()
                .getDrawable(R.drawable.pic_location), mMapView);
        //popUpView = LayoutInflater.from(this).inflate(R.layout.popup_view, null);
        //popText = ((TextView)popUpView.findViewById(R.id.popText));
        popOverlay = new PopupOverlay(mMapView, popupClickListener);

        dbHelper = new DBHelper(getApplicationContext());

    }

    // popupView点击事件
    private PopupClickListener popupClickListener =  new PopupClickListener(){

        @Override
        public void onClickedPopup(int index) {

            popOverlay.hidePop();
            dialog = ProgressDialog.show(MapShowActivity.this, "", "正在加载...", true, true);
            Intent intent = new Intent();
            intent.setClass(MapShowActivity.this, MapShowActivity.class);
            intent.putExtra("diaryId", diaryOverlay.getItem(overlayItemIndex).getSnippet());
            startActivity(intent);
        }

    };


    // 读取对应坐标文件，初始化路径
    private void initMapView(){
        mMapView.getOverlays().clear();
        diaryOverlay.removeAll();

        mMapView.getOverlays().add(diaryOverlay);
        mMapView.refresh();
        mMapController.setCenter(diaryOverlay.getCenter());
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


    @Override
    protected void onDestroy() {
        mMapView.destroy();
        if (app.mBMapManager != null) {
            app.mBMapManager.destroy();
            app.mBMapManager = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {

        mMapView.onPause();
        super.onPause();
    }

    // 写完日记返回刷新
    @Override
    protected void onResume() {

        mMapView.onResume();
        if (app.mBMapManager != null) {
            app.mBMapManager.start();
        }
        super.onResume();
    }

    protected void onStop() {

        mMapView.onPause();
        if (app.mBMapManager != null) {
            app.mBMapManager.stop();
        }
        super.onStop();
    }


}
