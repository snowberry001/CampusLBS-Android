package com.sky.activity;

import java.io.File;
import java.io.IOException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sky.application.TourApplication;
import com.sky.constant.Constants;
import com.sky.db.entity.Tour;
import com.sky.db.service.TourService;
import com.sky.service.LocationService;
import com.sky.util.DateUtil;
import com.sky.util.FileUtil;

/**
 * 新建Tour
 */
public class TourCreateActivity extends Activity implements OnClickListener {

	private EditText tourTitleEt;
	private TextView tourLocationTv;
	private TextView tourAccurateLocTv;
	private TextView tourStartTimetv;
	private TextView tourStopTimeTv;
	private TextView tourRunTimeTv;

	private Button tourStartBtn;
	private Button tourStopBtn;
	private Button tourCancelBtn;

	private View locationLayout;

	private TourService tourService;

	// 计时
	private final Handler timeHandler = new Handler();
	private int second = 0;
	private int minute = 0;
	private int hour = 0;

	// 位置经纬度信息
	private String accurateLoc;
	// 拍照Uri
	private Uri photoUri = null;

	private final static int REQUEST_CODE1 = 1; // 位置
	private final static int REQUEST_CODE2 = 2; // 拍照

	private TourApplication app = null;

	private long tourId = -1L;
	
	private Intent locationIntent;  // GPS定位Service

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = (TourApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.initEngineManager(getApplicationContext());
		}

		setContentView(R.layout.tour_create_view);

		tourTitleEt = (EditText) findViewById(R.id.tourTitle);
		tourLocationTv = (TextView) findViewById(R.id.tourLocation);
		tourAccurateLocTv = (TextView) findViewById(R.id.tourAccurateLoc);
		tourStartTimetv = (TextView) findViewById(R.id.tourStartTime);
		tourStopTimeTv = (TextView) findViewById(R.id.tourStopTime);
		tourRunTimeTv = (TextView) findViewById(R.id.tourRunTime);
		tourStartBtn = (Button) findViewById(R.id.tourStartBtn);
		tourStopBtn = (Button) findViewById(R.id.tourStopBtn);
		tourCancelBtn = (Button) findViewById(R.id.tourCancelBtn);
		locationLayout = (View) findViewById(R.id.locationLayout);

		tourTitleEt.setSingleLine(false);
		tourTitleEt.setMaxLines(3);

		tourStartBtn.setOnClickListener(this);
		tourStopBtn.setOnClickListener(this);
		tourCancelBtn.setOnClickListener(this);
		locationLayout.setOnClickListener(this);

		tourService = TourService.getInstacne(getApplicationContext());

		app.locationTv = tourLocationTv;
		app.accurateLocTv = tourAccurateLocTv;
		app.startLocation();
		app.stopLocation();

	}


	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		// 获取位置
		case R.id.locationLayout:
			Intent intent = new Intent();
			intent.putExtra("accurateLoc", accurateLoc);
			intent.setClass(TourCreateActivity.this, TourLocationActivity.class);
			startActivityForResult(intent, REQUEST_CODE1);
			break;

		// 开始Tour：写数据库以及开启service记录位置信息
		case R.id.tourStartBtn:
			startTour();
			break;

		// 结束Tour
		case R.id.tourStopBtn:
			stopTour();
			break;

		// 取消Tour
		case R.id.tourCancelBtn:
			scanPhotos();
			break;

		default:
			break;
		}
	}

	// 查看Tour相册
	private void scanPhotos() {
		Intent intent = new Intent();
		intent.setClass(TourCreateActivity.this, PhotoListActivity.class);
		intent.putExtra("tourId", String.valueOf(tourId));
		startActivity(intent);
	}


	// 开始Tour
	private void startTour() {

		timeHandler.postDelayed(timeRunnable, 1000);
		
		tourStartTimetv.setText(DateUtil.getCurrentDateTime());
		tourStartBtn.setVisibility(View.GONE);
		tourStopBtn.setVisibility(View.VISIBLE);
		
		String title = tourTitleEt.getText().toString().trim();
		String startTime = tourStartTimetv.getText().toString();
		String location = tourLocationTv.getText().toString();
		accurateLoc = accurateLoc != null ? accurateLoc : tourAccurateLocTv.getText().toString();
		
		Tour tour = new Tour();
		tour.setTourStartTime(startTime);
		tour.setTourLocation(location);
		tour.setTourTitle(title);
		tour.setTourAccurateLocation(accurateLoc);
		tourId = tourService.insertTour(tour);

		// 创建位置文件
		createLocationFile();
		
		// 启动位置记录service
		startLocationService();
	}
	
	// 启动位置记录service
	private void startLocationService(){
		locationIntent = new Intent(this, LocationService.class);
		locationIntent.putExtra("tourId", String.valueOf(tourId));
        startService(locationIntent);
	}

	// 创建Tour对应的位置文件，名称与tourId对应
	private File createLocationFile() {
		String folderPath = FileUtil.SDCARD_PATH + Constants.FILE_FOLDER;
		File tempDir = new File(folderPath);
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}
		File locationFile = new File(folderPath + tourId + ".txt");
		try {
			locationFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return locationFile;
	}

	// 停止Tour
	private void stopTour() {
		
		String title = tourTitleEt.getText().toString().trim();
		if (title.isEmpty()) {
			Toast.makeText(this, "这是什么好玩的地方~", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// 停止定位Service
		stopService(locationIntent);
		Toast.makeText(this, "已停止GPS位置服务！", Toast.LENGTH_SHORT).show();
		
		timeHandler.removeCallbacks(timeRunnable);
		tourStopTimeTv.setText(DateUtil.getCurrentDateTime());

		String location = tourLocationTv.getText().toString();
		accurateLoc = accurateLoc != null ? accurateLoc : tourAccurateLocTv.getText().toString();
		String stopTime = tourStopTimeTv.getText().toString();
		String fileUrl = tourId + ".txt";

		Tour tour = new Tour();
		tour.setTourId(String.valueOf(tourId));
		tour.setTourTitle(title);		
		tour.setTourEndTime(stopTime);
		tour.setTourLocation(location);		
		tour.setTourAccurateLocation(accurateLoc);
		tour.setTourFileUrl(fileUrl);
		tourService.updateTour(tour);		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 获取位置信息
		if (resultCode == Activity.RESULT_OK && data != null
				&& requestCode == REQUEST_CODE1) {
			accurateLoc = data.getExtras().getString("accurateLoc");
			if (data.getExtras().containsKey("locationMsg")) {
				tourLocationTv.setText(data.getExtras().getString("locationMsg"));
			}
		}
		// 拍照成功
		if (resultCode == Activity.RESULT_OK && data == null
				&& requestCode == REQUEST_CODE2) {
			Intent intent = new Intent();
			intent.setClass(TourCreateActivity.this, PhotoShareActivity.class);
			intent.putExtra("photoUri", photoUri);
			intent.putExtra("tourId", String.valueOf(tourId));
			startActivity(intent);
		}

		// 拍照取消，  删除临时文件
		if(resultCode == Activity.RESULT_CANCELED && requestCode == REQUEST_CODE2){ 
			if(photoUri != null){ 
				File image = new File(photoUri.getPath()); 
				if(image.exists()){
					image.delete(); 
				}
			}
		 }


	}

	// 计时线程
	private Runnable timeRunnable = new Runnable() {
		@Override
		public void run() {
			if (second + 1 == 60) {
				second = 0;
				if (minute + 1 == 60) {
					minute = 0;
					hour++;
				} else {
					minute++; // 不考虑60小时以上
				}
			} else {
				second++;
			}
			tourRunTimeTv.setText(formatTimeStr(second, minute, hour));
			timeHandler.postDelayed(this, 1000);
		}
	};

	// 运行时间格式化
	private String formatTimeStr(int second, int minute, int hour) {
		String format = "%s:%s:%s";
		return String.format(format, hour < 10 ? "0" + hour : hour,
				minute < 10 ? "0" + minute : minute, second < 10 ? "0" + second
						: second);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.new_tour_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.camera:
			startCamera();
			break;
		case R.id.photo:
			scanPhotos();
			break;
		default:
			break;
		}
		return true;
	}
	
	private void startCamera(){
		Intent intent = new Intent();
		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		photoUri = Uri.fromFile(FileUtil.createImageFile(String.valueOf(tourId)));
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
		startActivityForResult(intent, REQUEST_CODE2);
	}

	// 返回键对话框
	private void showDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("提示");
		builder.setMessage("\n确定放弃此次Tour？\n");
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		builder.create().show();
	}

	// 监听按钮事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showDialog();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}