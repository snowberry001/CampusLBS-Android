/*package com.sky.activity;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import com.sky.control.MyPagerAdapter;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CopyOfPhotoViewActivity extends FragmentActivity implements OnPageChangeListener {

	private ImageView tourPhotoIv;

	// viewpager
	private ViewPager viewPager;
	private ImageView[] tips;
	private List<String> imagePathList;
	private String[] imagePathArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tour_photo);
		
		Bundle bundle = getIntent().getExtras();
		
		Uri photoUri = bundle.getParcelable("photoUri");
		String tourId = bundle.getString("tourId");
		String indexId = bundle.getString("indexId");
		
		
		 * tourPhotoIv = (ImageView) findViewById(R.id.tourPhoto);
		 * tourPhotoIv.setImageURI(photoUri);
		 

		ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup);
		viewPager = (ViewPager) findViewById(R.id.viewPager);

		// 载入图片资源ID
		imagePathArray = getImagePathList(tourId);
		
		// 将点点加入到ViewGroup中
		tips = new ImageView[imagePathArray.length];
		for (int i = 0; i < tips.length; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(new LayoutParams(10, 10));
			tips[i] = imageView;
			if (i == 0) {
				tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
			}

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 5;
			layoutParams.rightMargin = 5;
			group.addView(imageView, layoutParams);
		}

		// 将图片装载到数组中
		imagePathList = new ArrayList<String>();
		String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() 
				+ "/tourCampus/images/" + tourId + "/";
		
		for (int i = 0; i < imagePathArray.length; i++) {
			imagePathList.add(folderPath + imagePathArray[i]);
		}
		
		// 设置需要缓存的页面个数
		viewPager.setOffscreenPageLimit(3);
		// 设置Adapter
		viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), imagePathList));
		// 设置监听，主要是设置点点的背景
		viewPager.setOnPageChangeListener(this);
		// 设置ViewPager的默认项
		viewPager.setCurrentItem(Integer.valueOf(indexId));

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
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.photo, menu);
		return true;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {


	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {


	}

	@Override
	public void onPageSelected(int arg0) {
		setImageBackground(arg0 % imagePathList.size()); 
	}
	
 
    // 设置选中的tip的背景 
	private void setImageBackground(int selectItems) {
		for (int i = 0; i < tips.length; i++) {
			if (i == selectItems) {
				tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
			}
		}
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
}
*/