package com.sky.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sky.activity.R;
import com.sky.util.BitmapUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyGridViewAdapter extends BaseAdapter {
	private Context context;
	private List<HashMap<String, String>> list;
	private LayoutInflater inflater;
	
	public MyGridViewAdapter(Context context, List<HashMap<String, String>> list){
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {

		return list.size();
	}

	@Override
	public Object getItem(int position) {

		return list.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
        ViewHolder holder;
        Map<String, String> item = list.get(position);
		if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.grid_item, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.itemImage);
            holder.indexTv = (TextView) convertView.findViewById(R.id.itemIndex);
            convertView.setTag(holder);
		} else {
            holder = (ViewHolder)convertView.getTag();
        }

		holder.indexTv.setText(item.get("index"));

		ImageLoader.getInstance().displayImage("file://" + item.get("imagePath"), holder.imageView);
		
		return convertView;
		
	}
	
    class ViewHolder {
        ImageView imageView;
        TextView indexTv;
    }
    
    
    
}
