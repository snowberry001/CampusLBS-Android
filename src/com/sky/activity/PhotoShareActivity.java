package com.sky.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sky.application.TourApplication;
import com.sky.db.entity.Photo;
import com.sky.db.service.PhotoService;
import com.sky.util.DateUtil;
import com.sky.util.FileUtil;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class PhotoShareActivity extends FragmentActivity implements OnClickListener, OnCheckedChangeListener{

	private EditText photoDescEt = null;
	private ImageView sharePhoto1Iv = null;
	private ImageView sharePhoto2Iv = null;
	private ImageView sharePhoto3Iv = null;
	private ImageView photoAddIv = null;
	private ToggleButton shareToggleBtn = null;
	
	private String tourId;
	private Uri newPhotoUri = null; 	// 拍照Uri
	
	private final static int REQUEST_CODE1 = 1; // 查看照片
	private final static int REQUEST_CODE2 = 2; // 拍照	
	
	private boolean isShared = true;  // 是否分享标识
	
	private TextView photoLocationTv;
	private TextView photoAccurateLocTv;	
	private TourApplication app = null;
	
	// -1:null; 0:sharePhoto1; 1:sharePhoto2; 2:sharePhoto3
	private int clickIndex = -1; 
	
	private List<String> imageUrlList;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		app = (TourApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.initEngineManager(getApplicationContext());
		}
		
		setContentView(R.layout.photo_share_view);
		
		photoDescEt = (EditText) findViewById(R.id.photoDesc);
		sharePhoto1Iv = (ImageView) findViewById(R.id.sharePhoto1);
		sharePhoto2Iv = (ImageView) findViewById(R.id.sharePhoto2);
		sharePhoto3Iv = (ImageView) findViewById(R.id.sharePhoto3);
		photoAddIv = (ImageView) findViewById(R.id.photoAddBtn);
		shareToggleBtn = (ToggleButton) findViewById(R.id.shareToggleBtn);
		photoLocationTv = (TextView) findViewById(R.id.photoLocation);
		photoAccurateLocTv = (TextView) findViewById(R.id.photoAccurateLoc);
		
		Bundle bundle = getIntent().getExtras();
		Uri firstPhotoUri = bundle.getParcelable("photoUri");
		tourId = bundle.getString("tourId");
		
		imageUrlList = new ArrayList<String>();
		imageUrlList.add(firstPhotoUri.toString());
		
		// 初始化第一个照片
		ImageLoader.getInstance().displayImage(firstPhotoUri.toString(), sharePhoto1Iv);

		photoAddIv.setOnClickListener(this);
		shareToggleBtn.setOnCheckedChangeListener(this);
		
		sharePhoto1Iv.setOnClickListener(this);
		sharePhoto2Iv.setOnClickListener(this);
		sharePhoto3Iv.setOnClickListener(this);
		
		app.locationTv = photoLocationTv;
		app.accurateLocTv = photoAccurateLocTv;
		app.startLocation();
		app.stopLocation();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.photo_share_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.shareOK:
			savePhotos();
			break;
		default:
			break;
		}
		return true;
	}
	
	// 保存照片记录
	private void savePhotos(){
		String photoDesc = photoDescEt.getText().toString().trim();
		if(photoDesc.isEmpty()){
			Toast.makeText(this, "记录下你此刻的想法吧~", Toast.LENGTH_SHORT).show();
			return;
		}
		if(imageUrlList.size() == 0){
			Toast.makeText(this, "请至少添加一张照片！", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// 本地数据库保存
		Photo photo = new Photo();
		photo.setPhotoTourId(tourId);
		photo.setPhotoTakenTime(DateUtil.getCurrentDateTime());
		photo.setPhotoDesc(photoDesc);
		photo.setPhotoLocation(photoLocationTv.getText().toString());
		photo.setPhotoAccurateLocation(photoAccurateLocTv.getText().toString());
		
		//PhotoService.getInstacne(getApplicationContext()).deletePhoto(null);
		
		for (String imageUrl : imageUrlList) {
			photo.setPhotoUrl(imageUrl);			
			PhotoService.getInstacne(getApplicationContext()).insertPhoto(photo);
		}
		
		//List<Photo> photoList = PhotoService.getInstacne(getApplicationContext()).getPhotoList(null);


		// 分享至服务器
		if(isShared) {
			
		} else {
			Toast.makeText(this, "照片保存成功！", Toast.LENGTH_SHORT).show();
			finish();
		}
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){			
			case R.id.photoAddBtn:
				startCamera();
				break;
			case R.id.sharePhoto1:
				clickIndex = 0;
				scanBigPhoto();
				break;
			case R.id.sharePhoto2:
				clickIndex = 1;
				scanBigPhoto();
				break;
			case R.id.sharePhoto3:
				clickIndex = 2;
				scanBigPhoto();
				break;
			default:
				break;
		}
	}
	
	// 打开照相机
	private void startCamera(){
		if(imageUrlList.size() == 3){
			Toast.makeText(this, "最多添加三张照片分享哦！", Toast.LENGTH_SHORT).show();
			return;
		}
		
		Intent intent = new Intent();
		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		newPhotoUri = Uri.fromFile(FileUtil.createImageFile(tourId));
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, newPhotoUri);
		startActivityForResult(intent, REQUEST_CODE2);
	}
	
	// 点击ImageView查看大图，可以进行删除操作
	private void scanBigPhoto(){
		Intent intent = new Intent();
		intent.setClass(PhotoShareActivity.this, PhotoShareViewActivity.class);
		intent.putExtra("imageUrl", imageUrlList.get(clickIndex));
		startActivityForResult(intent, REQUEST_CODE1);
		overridePendingTransition(R.anim.zoom_in, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// 删除照片
		if (resultCode == Activity.RESULT_OK && data != null && requestCode == REQUEST_CODE1) {
			boolean isDeleted = data.getExtras().getBoolean("isDeleted");
			String imageUrl = data.getExtras().getString("imageUrl");
			if (isDeleted) {
				imageUrlList.remove(clickIndex);
				updateViewOfSharePhotos();
				deletePhoto(imageUrl);
			}
		}
		
		// 拍照成功
		if (resultCode == Activity.RESULT_OK && data == null && requestCode == REQUEST_CODE2) {
			imageUrlList.add(newPhotoUri.toString());
			updateViewOfSharePhotos();
		}

		// 拍照取消, 删除临时文件
		if(resultCode == Activity.RESULT_CANCELED && requestCode == REQUEST_CODE2){ 
			if(newPhotoUri != null){ 
				deletePhoto(newPhotoUri.toString());
			}
		 }
	}

	// 更新分享照片
	private void updateViewOfSharePhotos(){
		int count = imageUrlList.size();
		switch(count){
		case 1:
			sharePhoto1Iv.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(imageUrlList.get(0), sharePhoto1Iv);
			sharePhoto2Iv.setVisibility(View.GONE);
			sharePhoto3Iv.setVisibility(View.GONE);
			break;
		case 2:
			sharePhoto1Iv.setVisibility(View.VISIBLE);
			sharePhoto2Iv.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(imageUrlList.get(0), sharePhoto1Iv);
			ImageLoader.getInstance().displayImage(imageUrlList.get(1), sharePhoto2Iv);
			sharePhoto3Iv.setVisibility(View.GONE);
			break;
		case 3:
			sharePhoto1Iv.setVisibility(View.VISIBLE);
			sharePhoto2Iv.setVisibility(View.VISIBLE);
			sharePhoto3Iv.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(imageUrlList.get(0), sharePhoto1Iv);
			ImageLoader.getInstance().displayImage(imageUrlList.get(1), sharePhoto2Iv);
			ImageLoader.getInstance().displayImage(imageUrlList.get(2), sharePhoto3Iv);
			break;
		default:
			sharePhoto1Iv.setVisibility(View.GONE);
			sharePhoto2Iv.setVisibility(View.GONE);
			sharePhoto3Iv.setVisibility(View.GONE);
			break;
		}
	}
	
	@Override
	public void onCheckedChanged(CompoundButton button, boolean checked) {
		// TODO Auto-generated method stub
		isShared = checked;
	}
	
	// 返回键对话框
	private void showDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("提示");
		builder.setMessage("\n退出照片分享？\n");
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteSharePhotos();
				finish();
			}
		});
		builder.create().show();
	}
	
	// 删除所有的分享照片
	private void deleteSharePhotos(){
		for (String url : imageUrlList) {
			deletePhoto(url);
		}
	}
	
	private void deletePhoto(String url){
		String path = Uri.parse(url).getPath();
		File photo = new File(path);
		if(photo.exists()){
			photo.delete();
		}
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
