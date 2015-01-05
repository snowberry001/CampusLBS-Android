package com.sky.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sky.activity.R;
import com.sky.util.BitmapUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyGridViewAdapter extends BaseAdapter {
	private Context context;
	private List<HashMap<String, String>> list;
	
	public MyGridViewAdapter(Context context, List<HashMap<String, String>> list){
		this.context = context;
		this.list = list;
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
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.grid_item, parent, false);
            holder.imageView = (ImageView) convertView.findViewById(R.id.itemImage);
            holder.indexTv = (TextView) convertView.findViewById(R.id.itemIndex);
            convertView.setTag(holder);
		} else {
            holder = (ViewHolder)convertView.getTag();
        }

		holder.imageView.setImageBitmap(BitmapUtil.copressImage(item.get("imagePath")));
		holder.indexTv.setText(item.get("index"));
		return convertView;
	}

    class ViewHolder {
        ImageView imageView;
        TextView indexTv;
    }
    
    
    
}
