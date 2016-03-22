package com.sptech.qujj.fragment;

import com.sptech.qujj.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 首页的小轮播
 * @author 叶旭东
 *
 */
public class LiteFragment extends Fragment {
	
	private TextView tv_title;
	private TextView tv_content;
	private String title, content;
	
	public LiteFragment(){
		
	}
	
	public LiteFragment(String title, String content) {
		// TODO Auto-generated constructor stub
		this.title = title;
		this.content =content;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.ye_fragment_lite, container, false);
		tv_title = (TextView) v.findViewById(R.id.tv_title);
		tv_content = (TextView) v.findViewById(R.id.tv_content);
		tv_title.setText(title);
		tv_content.setText(content);
		return v;
	}
	
	public static LiteFragment newInstance(String title, String content){
		LiteFragment fragment = new LiteFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putString("content", content);
		fragment.setArguments(args);
		return fragment;
	}
	
}
