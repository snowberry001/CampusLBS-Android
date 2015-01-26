package com.sky.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sky.activity.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyGridViewAdapter extends BaseAdapter {
	private List<HashMap<String, String>> list;
	private LayoutInflater inflater;
	
	public MyGridViewAdapter(Context context, List<HashMap<String, String>> list){
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
            convertView = inflater.inflate(R.layout.photo_grid_item, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.itemImage);
            holder.photoDescTv = (TextView) convertView.findViewById(R.id.itemIndex);
            convertView.setTag(holder);
		} else {
            holder = (ViewHolder)convertView.getTag();
        }

		holder.photoDescTv.setText(item.get("photoDesc"));

		ImageLoader.getInstance().displayImage(item.get("imagePath"), holder.imageView); 
		
		return convertView;
		
	}
	
    class ViewHolder {
        ImageView imageView;
        TextView photoDescTv;
    }
    
    
    
}
