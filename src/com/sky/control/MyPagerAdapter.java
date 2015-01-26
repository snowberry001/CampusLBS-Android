package com.sky.control;

import java.util.HashMap;
import java.util.List;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class MyPagerAdapter extends FragmentStatePagerAdapter{
	
	public List<HashMap<String, String>> imagePathList;
	 
    public MyPagerAdapter(FragmentManager fragmentManager, List<HashMap<String,String>> imagePathList) {
        super(fragmentManager);
        this.imagePathList = imagePathList;
    }

    @Override
    public int getCount() {
        return imagePathList == null ? 0 : imagePathList.size();
    }
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //Fragment fragment = ((Fragment) object);
        //container.removeView(fragment.getView());
        //FragmentUtils.removeFragmentRecently(getSupportFragmentManager(), fragment);
    	
    	//container.removeView((View) object);
    }
    
    
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
    	// TODO Auto-generated method stub
    	MyPageFragment fragment = (MyPageFragment) super.instantiateItem(container, position);
    	return fragment;
    }
    
    @Override
    public Fragment getItem(int position) {
        String url = imagePathList.get(position).get("imagePath");
        return MyPageFragment.newInstance(url);
    }
    
    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == ((Fragment) obj).getView();
    }
}
