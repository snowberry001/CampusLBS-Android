package com.sky.activity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.sky.control.MyGridViewAdapter;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class PhotoListActivity extends Activity implements
					OnItemClickListener, OnItemLongClickListener {

	private GridView gridView;
	private List<HashMap<String, String>> imagePathList = null;
	private String tourId;
	
	private final static int REQUEST_CODE1 = 1; // 查看照片
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_list);
		
		gridView = (GridView) findViewById(R.id.gridview);

		Bundle bundle = getIntent().getExtras();
		tourId = bundle.getString("tourId");
		
		initGridView();
		
	}
	
	private void initAdapterData(){
		
		String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() 
				+ "/tourCampus/images/" + tourId + "/";
		File folder = new File(folderPath);
		String[] imageNameArray = null;
		if(folder.exists()){
			imageNameArray = folder.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String fileName) {
					String name = fileName.toUpperCase();
					return name.endsWith(".PNG") || name.endsWith(".JPG");
				}
			});
		}
		imagePathList = new ArrayList<HashMap<String,String>>();
		if(imageNameArray != null){
			for(int i = 0; i < imageNameArray.length; i++){
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("index", String.valueOf(i));
				map.put("imagePath", folderPath + imageNameArray[i]);
				imagePathList.add(map);
			}
		}
	}
	
	
	private void initGridView(){
		initAdapterData();
		gridView.setAdapter(new MyGridViewAdapter(this, imagePathList));
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
		String indexId = indexTv.getText().toString();
		
		Intent intent = new Intent();
		intent.setClass(PhotoListActivity.this, PhotoViewActivity.class);
		intent.putExtra("tourId", String.valueOf(tourId));
		intent.putExtra("indexId", index);
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
				if(imagePath != null){
					File image = new File(imagePath);
					if(image.exists()){
						image.delete();
					}
				}
				imagePathList.remove(indexId);
				gridView.setAdapter(new MyGridViewAdapter(this, imagePathList));
			}
		}
	}
}
