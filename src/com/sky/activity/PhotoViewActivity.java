package com.sky.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sky.activity.util.SystemUiHider;
import com.sky.control.MyPagerAdapter;
import com.sky.db.entity.Photo;
import com.sky.db.service.PhotoService;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class PhotoViewActivity extends FragmentActivity implements OnPageChangeListener {

	private static final boolean AUTO_HIDE = true;

	private static final int AUTO_HIDE_DELAY_MILLIS = 5000;

	private static final boolean TOGGLE_ON_CLICK = true;

	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	private SystemUiHider mSystemUiHider;

	private ViewPager viewPager;
	private TextView photoInforTv;
	
	private int imageCount = 0;

	private List<HashMap<String, String>> imageInfoList;
	
	private ActionBar actionBar;
	
	private String tourId;
	
	private int indexId;
	
	private PhotoService photoService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tour_photo_fullscreen);
		
		actionBar = getActionBar();
	
		Bundle bundle = getIntent().getExtras();		
		tourId = bundle.getString("tourId");
		indexId = bundle.getInt("indexId");
				
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		photoInforTv = (TextView) findViewById(R.id.photoInfor);
	
		photoService = PhotoService.getInstacne(getApplicationContext());
		
		// 载入图片资源路径
		initAdapterData();
		
		// 设置需要缓存的页面个数
		viewPager.setOffscreenPageLimit(3);
		viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), imageInfoList));
		viewPager.setOnPageChangeListener(this);
		viewPager.setCurrentItem(indexId);
		
		initFullScreen();

		setPhotoInfor(indexId);
	}
	
	private void initAdapterData(){			
		
		List<Photo> tourPhotoList = photoService.getPhotoListByTourId(tourId);	
		imageInfoList = new ArrayList<HashMap<String,String>>();
		for(Photo photo : tourPhotoList){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("imagePath", photo.getPhotoUrl());
			map.put("photoDesc", photo.getPhotoDesc());
			imageInfoList.add(map);
		}
		imageCount = imageInfoList.size();

	}
	
	private void initFullScreen(){
		final View controlsView = findViewById(R.id.fullscreen_content_controls);

		mSystemUiHider = SystemUiHider.getInstance(this, viewPager, HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
							}
							controlsView.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
						}

						if (visible && AUTO_HIDE) {
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});
		
		viewPager.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {


	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {


	}

	@Override
	public void onPageSelected(int selectedIndex) {		
		setPhotoInfor(selectedIndex);
	}
	
 
    // 设置title索引、文字信息 
	private void setPhotoInfor(int selectedIndex) {
		actionBar.setTitle((selectedIndex + 1) + "/" + imageCount);
		photoInforTv.setText(imageInfoList.get(selectedIndex).get("photoDesc"));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.photo_view_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.deletePhoto:
			deletePhoto();
			break;
		default:
			break;
		}
		return true;
	}
	
	// 删除分享照片
	private void deletePhoto(){
		Intent intent = new Intent();
		intent.setClass(PhotoViewActivity.this, PhotoListActivity.class);
		intent.putExtra("isDeleted", true);
		intent.putExtra("indexId", indexId);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(0, R.anim.zoom_out);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		delayedHide(AUTO_HIDE_DELAY_MILLIS);
	}

	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
}
