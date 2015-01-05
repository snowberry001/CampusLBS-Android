package com.sky.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sky.activity.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyListViewAdapter extends BaseAdapter {
	private Context context;
	private List<HashMap<String, String>> list;
	
	public MyListViewAdapter(Context context, List<HashMap<String, String>> list){
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
			convertView = inflater.inflate(R.layout.tour_item, parent, false);
            holder.idTv = (TextView) convertView.findViewById(R.id.id);
            holder.titleTv = (TextView) convertView.findViewById(R.id.title);
            holder.dateTv = (TextView) convertView.findViewById(R.id.date);
            holder.locationTv = (TextView) convertView.findViewById(R.id.location);
            convertView.setTag(holder);
		} else {
            holder = (ViewHolder)convertView.getTag();
        }

		holder.idTv.setText(item.get("id"));
		holder.titleTv.setText(item.get("title"));
		holder.dateTv.setText(item.get("date"));
		holder.locationTv.setText(item.get("location"));
		return convertView;
	}

    class ViewHolder {
        TextView idTv;
        TextView titleTv;
        TextView dateTv;
        TextView locationTv;
    }
    
    
    
}
