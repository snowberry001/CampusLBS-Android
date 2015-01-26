package com.sky.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sky.activity.R;
import com.sky.constant.Constants;
import com.sky.db.entity.Photo;
import com.sky.db.service.PhotoService;
import com.sky.util.Bitmap2Util;
import com.sky.util.DateUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
			convertView = inflater.inflate(R.layout.tour_list_item, parent, false);
            holder.idTv = (TextView) convertView.findViewById(R.id.tourId);
            holder.titleTv = (TextView) convertView.findViewById(R.id.title);
            holder.dateTv = (TextView) convertView.findViewById(R.id.tourDate);
            holder.locationTv = (TextView) convertView.findViewById(R.id.tourLocation);
            holder.photoTv = (ImageView) convertView.findViewById(R.id.tourPhoto);
            
            convertView.setTag(holder);
		} else {
            holder = (ViewHolder)convertView.getTag();
        }

		String tourId = item.get("id");
		
		holder.idTv.setText(item.get("id"));
		holder.titleTv.setText(item.get("title"));		
		holder.locationTv.setText(item.get("location"));
		holder.dateTv.setText(DateUtil.parseDateString(item.get("date")));
		
		List<Photo> tourPhotoList = PhotoService.getInstacne(context).getPhotoListByTourId(tourId);
		
		if(tourPhotoList.size() != 0){	
			int count = tourPhotoList.size() >= Constants.COMBINE_PHOTO_MAX_COUNT 
					? Constants.COMBINE_PHOTO_MAX_COUNT : tourPhotoList.size();
			Bitmap[] mBitmaps = new Bitmap[count]; 
			for(int i = 0; i < count; ++i){				
				mBitmaps[i] = ThumbnailUtils.extractThumbnail(ImageLoader.getInstance()
						.loadImageSync(tourPhotoList.get(i).getPhotoUrl()), 
						Constants.COMBINE_PHOTO_ITEM_WIDTH, Constants.COMBINE_PHOTO_ITEM_HEIGHT);
			}			
			Bitmap combineBitmap = Bitmap2Util.getCombineBitmaps(mBitmaps);
			holder.photoTv.setImageBitmap(combineBitmap);
		} else {
			holder.photoTv.setImageResource(R.drawable.tour_item_image);
		}

		return convertView;
	}

    class ViewHolder {
        TextView idTv;
        TextView titleTv;
        TextView dateTv;
        TextView locationTv;
        ImageView photoTv;
    }
    
    
    
}
