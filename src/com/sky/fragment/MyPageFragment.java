package com.sky.fragment;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sky.activity.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class MyPageFragment extends Fragment {
	
	private String imagePath;
	
	public static MyPageFragment newInstance(String imagePath) {
		MyPageFragment fragment = new MyPageFragment();
		Bundle args = new Bundle();
		args.putString("imagePath", imagePath);
		fragment.setArguments(args);
		return fragment;
	}

	public MyPageFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (getArguments() != null) {
			imagePath = getArguments().getString("imagePath");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_my_page, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        
        ImageLoader.getInstance().displayImage("file://" + imagePath, imageView);
        
        return view;

	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

}
