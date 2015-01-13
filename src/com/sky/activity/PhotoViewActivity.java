package com.sky.activity;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import com.sky.activity.util.SystemUiHider;
import com.sky.control.MyPagerAdapter;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PhotoViewActivity extends FragmentActivity implements OnPageChangeListener {

	private static final boolean AUTO_HIDE = true;

	private static final int AUTO_HIDE_DELAY_MILLIS = 5000;

	private static final boolean TOGGLE_ON_CLICK = true;

	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	private SystemUiHider mSystemUiHider;

	
	// viewpager
	private ViewPager viewPager;
	
	private int imageCount = 0;
	
	private List<String> imagePathList;
	
	private String[] imagePathArray;
	
	private ActionBar actionBar;
	
	private int indexId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tour_photo);
		
		actionBar = getActionBar();
	
		Bundle bundle = getIntent().getExtras();
		
		String tourId = bundle.getString("tourId");
		indexId = bundle.getInt("indexId");
		
		viewPager = (ViewPager) findViewById(R.id.viewPager);
	
		// 载入图片资源路径
		imagePathArray = getImagePathList(tourId);
		imageCount = imagePathArray.length;
		imagePathList = new ArrayList<String>(imageCount);
		String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() 
				+ "/tourCampus/images/" + tourId + "/";
		
		for (int i = 0; i < imageCount; i++) {
			imagePathList.add(folderPath + imagePathArray[i]);
		}
		
		// 设置需要缓存的页面个数
		viewPager.setOffscreenPageLimit(3);
		viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), imagePathList));
		viewPager.setOnPageChangeListener(this);
		viewPager.setCurrentItem(indexId);
		
		initFullScreen();
		
		// 设置title
		actionBar.setTitle((indexId + 1) + "/" + imageCount);
	}
	
	// 获取指定tour对应的所有照片
	private String[] getImagePathList(String tourId){
		String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() 
				+ "/tourCampus/images/" + tourId;
		String[] fileNames = null;
		File dir =  new File(folderPath);
		if(dir.exists()){
			fileNames = dir.list(new FilenameFilter() {				
				@Override
				public boolean accept(File dir, String fileName) {
					String name = fileName.toUpperCase();
					return name.endsWith(".PNG") || name.endsWith(".JPG");					
				}
			});
		}
		return fileNames;
	}
	
	
	private void initFullScreen(){
		final View controlsView = findViewById(R.id.fullscreen_content_controls);

		mSystemUiHider = SystemUiHider.getInstance(this, viewPager, HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
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
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		viewPager.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					System.out.println(TOGGLE_ON_CLICK + "--click true---");
					mSystemUiHider.toggle();
				} else {
					System.out.println(TOGGLE_ON_CLICK + "--click false---");
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
		// 设置title
		actionBar.setTitle((selectedIndex + 1) + "/" + imageCount);
		setImageInfor(selectedIndex);
	}
	
 
    // 设置索引、文字信息 
	private void setImageInfor(int index) {

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.view_photo, menu);
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

		delayedHide(100);
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
