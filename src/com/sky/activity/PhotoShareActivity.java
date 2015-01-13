package com.sky.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sky.application.TourApplication;
import com.sky.fragment.ImageGridFragment;
import com.sky.util.BitmapUtil;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class PhotoShareActivity extends FragmentActivity implements OnClickListener, OnCheckedChangeListener{

	
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
	
	private int clickIndex = 0; // 0:null; 1:sharePhoto1; 2:sharePhoto2; 3:sharePhoto3
	
	private List<String> imageUrlList;
	
	private ImageGridFragment gridFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		app = (TourApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.initEngineManager(getApplicationContext());
		}
		
		setContentView(R.layout.activity_photo_share);
		
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
		sharePhoto1Iv.setImageBitmap(BitmapUtil.copressImage(firstPhotoUri.getPath(), 150, 150));
		sharePhoto1Iv.setTag(firstPhotoUri);
		photoAddIv.setOnClickListener(this);
		shareToggleBtn.setOnCheckedChangeListener(this);
		
		sharePhoto1Iv.setOnClickListener(this);
		sharePhoto2Iv.setOnClickListener(this);
		sharePhoto3Iv.setOnClickListener(this);
		
		app.locationTv = photoLocationTv;
		app.accurateLocTv = photoAccurateLocTv;
		app.startLocation();
		app.stopLocation();
		
		initShareGrid();
		
	}
	
	private void initShareGrid(){
		String tag = ImageGridFragment.class.getSimpleName();
		gridFragment = (ImageGridFragment)getSupportFragmentManager().findFragmentByTag(tag);
		if (gridFragment == null) {
			gridFragment = new ImageGridFragment();
			gridFragment.setImageUrlList(imageUrlList);
		}
		getSupportFragmentManager().beginTransaction().replace(R.id.gridFragment, gridFragment, tag).commit();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photo_share, menu);
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
		
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){			
			case R.id.photoAddBtn:
				startCamera();
				break;
			case R.id.sharePhoto1:
				clickIndex = 1;
				scanBigPhoto(((Uri)sharePhoto1Iv.getTag()).getPath());
				break;
			case R.id.sharePhoto2:
				clickIndex = 2;
				scanBigPhoto(((Uri)sharePhoto2Iv.getTag()).getPath());
				break;
			case R.id.sharePhoto3:
				clickIndex = 3;
				scanBigPhoto(((Uri)sharePhoto3Iv.getTag()).getPath());
				break;
			default:
				break;
		}
	}
	
	// 打开照相机
	private void startCamera(){
		Intent intent = new Intent();
		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		newPhotoUri = Uri.fromFile(createImageFile());
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, newPhotoUri);
		startActivityForResult(intent, REQUEST_CODE2);
	}
	
	// 创建临时图片文件
	private File createImageFile() {
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
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
	
	// 点击ImageView查看大图，可以进行删除操作
	private void scanBigPhoto(String imagePath){
		Intent intent = new Intent();
		intent.setClass(PhotoShareActivity.this, SharePhotoViewActivity.class);
		intent.putExtra("imagePath", imagePath);
		startActivityForResult(intent, REQUEST_CODE1);
		overridePendingTransition(R.anim.zoom_in, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// 删除照片
		if (resultCode == Activity.RESULT_OK && data != null && requestCode == REQUEST_CODE1) {
			boolean isDeleted = data.getExtras().getBoolean("isDeleted");
			String imagePath = data.getExtras().getString("imagePath");
			if (isDeleted) {
				switch(clickIndex){
					case 1:
						sharePhoto1Iv.setVisibility(View.GONE);
						break;
					case 2:
						sharePhoto2Iv.setVisibility(View.GONE);
						break;
					case 3:
						sharePhoto3Iv.setVisibility(View.GONE);
						break;
					default:
						break;
				}
				File image = new File(imagePath);
				if(image.exists()){
					image.delete();
				}
			}
		}
		
		// 拍照成功
		if (resultCode == Activity.RESULT_OK && data == null && requestCode == REQUEST_CODE2) {

			Bitmap bitmap = ImageLoader.getInstance().loadImageSync(newPhotoUri.toString());
			if(sharePhoto1Iv.getVisibility() == View.GONE){
				sharePhoto1Iv.setImageBitmap(bitmap);
				sharePhoto1Iv.setTag(newPhotoUri);
				sharePhoto1Iv.setVisibility(View.VISIBLE);
			} else if(sharePhoto2Iv.getVisibility() == View.GONE){				
				sharePhoto2Iv.setImageBitmap(bitmap);
				sharePhoto2Iv.setTag(newPhotoUri);
				sharePhoto2Iv.setVisibility(View.VISIBLE);
			} else if (sharePhoto3Iv.getVisibility() == View.GONE) {
				sharePhoto3Iv.setImageBitmap(bitmap);
				sharePhoto3Iv.setTag(newPhotoUri);
				sharePhoto3Iv.setVisibility(View.VISIBLE);
			}
			
			imageUrlList.add(newPhotoUri.toString());
			gridFragment.updateView();
		}

		// 拍照取消, 删除临时文件
		if(resultCode == Activity.RESULT_CANCELED && requestCode == REQUEST_CODE2){ 
			if(newPhotoUri != null){ 
				File image = new File(newPhotoUri.getPath()); 
				image.delete(); 
			}
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
	
	private void deleteSharePhotos(){
		if(sharePhoto1Iv.getVisibility() == View.VISIBLE){
			deletePhoto((Uri)sharePhoto1Iv.getTag());
		}
		if(sharePhoto2Iv.getVisibility() == View.VISIBLE){
			deletePhoto((Uri)sharePhoto2Iv.getTag());
		}
		if(sharePhoto3Iv.getVisibility() == View.VISIBLE){
			deletePhoto((Uri)sharePhoto3Iv.getTag());
		}
	}
	
	private void deletePhoto(Uri uri){
		if(uri != null){
			File photo = new File(uri.getPath());
			if(photo.exists()){
				photo.delete();
			}
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
