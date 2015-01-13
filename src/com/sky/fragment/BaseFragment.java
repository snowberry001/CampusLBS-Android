package com.sky.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sky.activity.R;


public abstract class BaseFragment extends Fragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.item_clear_memory_cache:
				ImageLoader.getInstance().clearMemoryCache();
				return true;
			case R.id.item_clear_disc_cache:
				ImageLoader.getInstance().clearDiskCache();
				return true;
			default:
				return false;
		}
	}
}
