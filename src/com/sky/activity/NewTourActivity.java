package com.sky.activity;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
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
import com.sky.db.DBHelper;

/**
 * 新建Tour
 */
public class NewTourActivity extends Activity implements OnClickListener,
		MediaScannerConnectionClient {

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

	private DBHelper dbHelper;

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

	// 查看所有照片
	public String[] imagePathArray;
	private String firstImagePath;
	private static final String FILE_TYPE = "image/*";
	private MediaScannerConnection conn;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = (TourApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.initEngineManager(getApplicationContext());
		}

		setContentView(R.layout.new_tour);

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
		tourTitleEt.setMaxLines(5);

		tourStartBtn.setOnClickListener(this);
		tourStopBtn.setOnClickListener(this);
		tourCancelBtn.setOnClickListener(this);
		locationLayout.setOnClickListener(this);

		dbHelper = new DBHelper(getApplicationContext());

		app.locationTv = tourLocationTv;
		app.accurateLocTv = tourAccurateLocTv;
		app.startLocation();
		app.stopLocation();

		prepareScanPhotos();
	}

	private void prepareScanPhotos() {

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		// 获取位置
		case R.id.locationLayout:
			Intent intent = new Intent();
			intent.putExtra("accurateLoc", accurateLoc);
			intent.setClass(NewTourActivity.this, LocationActivity.class);
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

	private void scanPhotos() {
		Intent intent = new Intent();
		intent.setClass(NewTourActivity.this, PhotoListActivity.class);
		intent.putExtra("photoUri", photoUri);
		intent.putExtra("tourId", String.valueOf(tourId));
		startActivity(intent);
	}

	// 查看Tour相册
	private void startScan() {
		String folderPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/tourCampus/images/" + tourId + "/";
		File folder = new File(folderPath);
		if (folder.exists()) {
			imagePathArray = folder.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String fileName) {
					String name = fileName.toUpperCase();
					return name.endsWith(".PNG") || name.endsWith(".JPG");
				}
			});
			if(imagePathArray == null || imagePathArray.length <= 0){
				Toast.makeText(this, "还没有拍摄过照片哦", Toast.LENGTH_SHORT).show();
				return;
			}
			firstImagePath = folderPath + imagePathArray[0];
			System.out.println("firstImagePath," + firstImagePath);
		}
		if (conn != null) {
			conn.disconnect();
		}
		conn = new MediaScannerConnection(getApplicationContext(), this);
		conn.connect();
	}

	@Override
	public void onMediaScannerConnected() {
		System.out.println("onMediaScannerConnected success");
		conn.scanFile(firstImagePath, null);
	}

	@Override
	public void onScanCompleted(String path, Uri uri) {
		try {
			System.out.println("URI," + uri);
			if (uri != null) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				intent.setDataAndType(uri, "image/*");
				startActivity(intent);
			}
		} finally {
			conn.disconnect();
			conn = null;
		}
	}

	// 开始Tour
	private void startTour() {

		
		timeHandler.postDelayed(timeRunnable, 1000);
		tourStartTimetv.setText(getCurrentDateTime());
		tourStartBtn.setVisibility(View.GONE);
		tourStopBtn.setVisibility(View.VISIBLE);
		
		String title = tourTitleEt.getText().toString().trim();
		String startTime = tourStartTimetv.getText().toString();
		String location = tourLocationTv.getText().toString();
		accurateLoc = accurateLoc != null ? accurateLoc : tourAccurateLocTv.getText().toString();
		tourId = dbHelper.createTour(startTime, location, title, accurateLoc);

		createLocationFile();
	}

	// 创建Tour对应的位置文件，名称与tourId对应
	private File createLocationFile() {
		String folderPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/tourCampus/files/";
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
			Toast.makeText(this, "记录下这是哪里~", Toast.LENGTH_SHORT).show();
			return;
		}
		
		timeHandler.removeCallbacks(timeRunnable);
		tourStopTimeTv.setText(getCurrentDateTime());

		String location = tourLocationTv.getText().toString();
		accurateLoc = accurateLoc != null ? accurateLoc : tourAccurateLocTv
				.getText().toString();
		String stopTime = tourStopTimeTv.getText().toString();
		String fileUrl = tourId + ".txt";
		dbHelper.updateTour(tourId, title, location, accurateLoc, stopTime,
				fileUrl);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 获取位置信息
		if (resultCode == Activity.RESULT_OK && data != null
				&& requestCode == REQUEST_CODE1) {
			accurateLoc = data.getExtras().getString("accurateLoc");
			if (data.getExtras().containsKey("locationMsg")) {
				tourLocationTv.setText(data.getExtras()
						.getString("locationMsg"));
			}
		}
		// 拍照成功
		if (resultCode == Activity.RESULT_OK && data == null
				&& requestCode == REQUEST_CODE2) {
			Intent intent = new Intent();
			intent.setClass(NewTourActivity.this, PhotoShareActivity.class);
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

	// 根据uri获取真实的文件路径，仅限于Images
	private String getRealPathFromURI(Uri contentUri) {
		String realPath = null;
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
		if (cursor.moveToFirst()) {
			int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			realPath = cursor.getString(index);
		}
		cursor.close();
		return realPath;
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

	// 获取当前日期信息
	private String getCurrentDateTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = format.format(new Date(System.currentTimeMillis()));
		return date;
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
		photoUri = Uri.fromFile(createImageFile());
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
		startActivityForResult(intent, REQUEST_CODE2);
	}

	private File createImageFile() {
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date());
		String folderPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/tourCampus/images/" + tourId + "/";
		File tempDir = new File(folderPath);
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}
		File image = new File(folderPath + timeStamp + ".png");
		try {
			image.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
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
		super.onResume();
	}

}