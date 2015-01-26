package com.sky.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.view.Menu;
import android.view.MenuItem;
import com.sky.control.MyListViewAdapter;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import com.sky.db.entity.Tour;
import com.sky.db.service.TourService;

public class TourListActivity extends Activity implements
		OnItemClickListener, OnItemLongClickListener {
	
	private ListView listview;
    private TextView noneTourTv;
	private MyListViewAdapter myListAdapter;
	private ArrayList<HashMap<String, String>> tourMapList;
	
	private ProgressDialog loadingDialog;
	
	private boolean isExit = false;
	
	private TourService tourService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tour_list_view);
		listview = (ListView) findViewById(R.id.tourList);
        noneTourTv = (TextView) findViewById(R.id.noneTour);
        
        tourService = TourService.getInstacne(getApplicationContext());
        
        initListView();
	}

	private void initAdapterData(){
		List<Tour> tourList = tourService.getTourList(null);
		if(tourList == null || tourList.size() == 0){
            noneTourTv.setVisibility(View.VISIBLE);
        } else {
            noneTourTv.setVisibility(View.GONE);
        }
		tourMapList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map;
		for(Tour tour : tourList){
			map = new HashMap<String, String>();
			map.put("id", tour.getTourId());
            map.put("date", tour.getTourStartTime());
			map.put("title", tour.getTourTitle());
			map.put("location", tour.getTourLocation());
			map.put("accurateLoc", tour.getTourAccurateLocation());
			tourMapList.add(map);
		}		
	}

	private void initListView(){
        initAdapterData();
		myListAdapter = new MyListViewAdapter(this, tourMapList);
		listview.setAdapter(myListAdapter);
		listview.setOnItemClickListener(this);
		listview.setOnItemLongClickListener(this);
	}


	@Override
	public void onItemClick(AdapterView<?> view, View v, int index, long lg) {
		loadingDialog = ProgressDialog.show(this, "", "正在加载...", true, true);
		TextView tourIdTv = (TextView) v.findViewById(R.id.tourId);
		String tourId = tourIdTv.getText().toString();
		Intent intent = new Intent();
		intent.setClass(this, TourMapActivity.class);
		intent.putExtra("tourId", tourId);
		startActivity(intent);
	}
	

	@Override
	public boolean onItemLongClick(AdapterView<?> view, View v, int index, long lg) {

		TextView tourIdTv = (TextView) v.findViewById(R.id.tourId);
		String tourId = tourIdTv.getText().toString();
		showDialog(tourId);
		return false;
	}
	
	private void showDialog(final String tourId){
		Builder builder = new Builder(this);
		builder.setTitle("提示");
		builder.setMessage("\n确定要删除？\n");
		builder.setNegativeButton("取消",null);
		builder.setPositiveButton("确定",
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						tourService.deleteTour(Integer.valueOf(tourId));
						initListView();
					}
				});
		builder.create().show();
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.tour_list_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.create:
                intent.setClass(TourListActivity.this, TourCreateActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
	protected void onResume() {

		//initAdapterData();
		//myListAdapter.notifyDataSetChanged();
    	
    	if(loadingDialog != null && loadingDialog.isShowing()){
    		loadingDialog.dismiss();
    	}
		initListView();
		super.onResume();
	}		

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
			exitHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			finish();
			System.exit(0);
		}
	}

	private Handler exitHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			isExit = false;
		};
	};
}
