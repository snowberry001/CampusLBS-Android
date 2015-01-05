package com.sky.control;

import com.sky.activity.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyActionBar extends LinearLayout {

	private Button btn_left;
	private TextView tv_title;
	private Button btn_right;

	public MyActionBar(Context context) {
		super(context);
	}

	public MyActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.action_bar, this);
		btn_left = (Button) findViewById(R.id.btn_left);
		tv_title = (TextView) findViewById(R.id.tv_title);
		btn_right = (Button) findViewById(R.id.btn_right);

	}

	public void setBtnLeftBacground(int resId) {
		if (btn_left != null) {
			btn_left.setBackgroundResource(resId);
		}
	}
	
	public void setBtnRightBacground(int resId) {
		if (btn_right != null) {
			btn_right.setBackgroundResource(resId);
		}
	}
	
	public void setBtnLeftText(String text) {
		if (btn_left != null) {
			btn_left.setText(text);
		}
	}
	
	public void setBtnRightText(String text) {
		if (btn_right != null) {
			btn_right.setText(text);
		}
	}
	
	// 设置监听器
	public void setBtnLeftListener(OnClickListener listner) {
		if (btn_left != null) {
			btn_left.setOnClickListener(listner);
		}
	}
	
	public void setBtnRightListener(OnClickListener listner) {
		if (btn_right != null) {
			btn_right.setOnClickListener(listner);
		}
	}


	public void setTvTitle(String text) {
		if (tv_title != null) {
			tv_title.setText(text);
		}
	}

	public Button getBtn_left() {
		return btn_left;
	}

	public TextView getTv_title() {
		return tv_title;
	}
	
	public Button getBtn_right() {
		return btn_right;
	}

}
