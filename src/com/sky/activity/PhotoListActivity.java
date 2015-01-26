package com.sky.activity;

import android.os.Bundle;
import android.app.Activity;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sky.control.MyGridViewAdapter;
import com.sky.db.entity.Photo;
import com.sky.db.service.PhotoService;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class PhotoListActivity extends Activity implements
					OnItemClickListener, OnItemLongClickListener {

	private TextView nonePhotoTv;
	private GridView gridView;
	private List<HashMap<String, String>> imagePathList = null;
	private MyGridViewAdapter gridViewAdapter = null;
	private String tourId;
	
	private final static int REQUEST_CODE1 = 1; // 查看照片
	
	private PhotoService photoService;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_grid_view);
		
		nonePhotoTv = (TextView) findViewById(R.id.nonePhoto);
		gridView = (GridView) findViewById(R.id.gridview);
		
		photoService = PhotoService.getInstacne(getApplicationContext());
		
		Bundle bundle = getIntent().getExtras();
		tourId = bundle.getString("tourId");
		
		initGridView();
		
	}
	
	private void initAdapterData(){			
		List<Photo> tourPhotoList = photoService.getPhotoListByTourId(tourId);		
		if(tourPhotoList == null || tourPhotoList.size() == 0){
			nonePhotoTv.setVisibility(View.VISIBLE);
        } else {
        	nonePhotoTv.setVisibility(View.GONE);
        }
		
		imagePathList = new ArrayList<HashMap<String,String>>();
		for(Photo photo : tourPhotoList){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("photoId", photo.getPhotoId());
			map.put("imagePath", photo.getPhotoUrl());
			map.put("photoDesc", photo.getPhotoDesc());
			imagePathList.add(map);
		}
	}
	
	private void initGridView(){
		initAdapterData();
		gridViewAdapter = new MyGridViewAdapter(this, imagePathList);
		gridView.setAdapter(gridViewAdapter);
		gridView.setOnItemClickListener(this);
		gridView.setOnItemLongClickListener(this);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> view, View v, int index, long lg) {
		
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> view, View v, int index, long lg) {
		TextView indexTv = (TextView) v.findViewById(R.id.itemIndex);
		String photoDesc = indexTv.getText().toString();
		
		Intent intent = new Intent();
		intent.setClass(PhotoListActivity.this, PhotoViewActivity.class);
		intent.putExtra("indexId", index);
		intent.putExtra("tourId", String.valueOf(tourId));		
		intent.putExtra("photoDesc", photoDesc);
		
		startActivityForResult(intent, REQUEST_CODE1);
		overridePendingTransition(R.anim.zoom_in, 0);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 删除照片
		if (resultCode == Activity.RESULT_OK && data != null && requestCode == REQUEST_CODE1) {
			boolean isDeleted = data.getExtras().getBoolean("isDeleted");
			int indexId = data.getExtras().getInt("indexId");
			if(isDeleted){
				String imagePath = imagePathList.get(indexId).get("imagePath");
				String photoId = imagePathList.get(indexId).get("photoId");
				if(imagePath != null){
					File image = new File(imagePath);
					if(image.exists()){
						image.delete();
					}
				}
				photoService.deletePhotoById(photoId);
				
				// adapter绑定的数据库add remove时调用notifyDataSetChanged才可以有效
				imagePathList.remove(indexId);	
				gridViewAdapter.notifyDataSetChanged();
				
				if(imagePathList.size() == 0){
					nonePhotoTv.setVisibility(View.VISIBLE);
		        }
			}
		}
	}
}
