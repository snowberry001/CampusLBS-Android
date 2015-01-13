package com.sky.activity;

import java.util.ArrayList;
import java.util.HashMap;

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
import android.database.Cursor;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import com.sky.db.DBHelper;

public class TourListActivity extends Activity implements
		OnItemClickListener, OnItemLongClickListener {
	
	private ListView listview;
    private TextView noneTourTv;
	private MyListViewAdapter myListAdapter;
	private ArrayList<HashMap<String, String>> tourList;
	
	private DBHelper dbHelper;
	private ProgressDialog dialog;

	private boolean isExit = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tour_list);
		listview = (ListView) findViewById(R.id.tourList);
        noneTourTv = (TextView) findViewById(R.id.noneTour);

        dbHelper = new DBHelper(getApplicationContext());
        initListView();
	}

	private void initAdapterData(){
		Cursor cursor = dbHelper.selectAllTour();
        if(cursor.getCount() == 0){
            noneTourTv.setVisibility(View.VISIBLE);
        } else {
            noneTourTv.setVisibility(View.GONE);
        }
		tourList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map;
		
		while(cursor.moveToNext()){
			map = new HashMap<String, String>();
			map.put("id", cursor.getString(0));
            map.put("date", cursor.getString(1));
			map.put("title", cursor.getString(3));
			map.put("location", cursor.getString(4));
			map.put("accurateLoc", cursor.getString(5));
			tourList.add(map);
		}
	}
	

	private void initListView(){
        initAdapterData();
		myListAdapter = new MyListViewAdapter(this, tourList);
		listview.setAdapter(myListAdapter);
		listview.setOnItemClickListener(this);
		listview.setOnItemLongClickListener(this);
	}


	@Override
	public void onItemClick(AdapterView<?> view, View v, int index, long lg) {

		dialog = ProgressDialog.show(this, "", "正在加载...", true, true);
		TextView item = (TextView) v.findViewById(R.id.id);
		String tourId = item.getText().toString();
		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		intent.putExtra("tourId", tourId);
		startActivity(intent);
	}
	

	@Override
	public boolean onItemLongClick(AdapterView<?> view, View v, int index, long lg) {

		TextView item = (TextView) v.findViewById(R.id.id);
		String tourId = item.getText().toString();
		showDialog(tourId);
		return false;
	}
	
	private void showDialog(final String tourId){
		Builder builder = new Builder(this);
		builder.setMessage("确定要删除？");
		builder.setNegativeButton("取消",null);
		builder.setPositiveButton("确定",
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						dbHelper.deleteTour(Integer.valueOf(tourId));	
						initListView();
					}
				});
		builder.create().show();
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tour_list_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.create:
                intent.setClass(TourListActivity.this, NewTourActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
	protected void onResume() {
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
