package com.sptech.qujj.fragment;

import com.sptech.qujj.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment {
	
	private View pageView;
	
	public MainFragment(View pageView) {
		// TODO Auto-generated constructor stub
		this.pageView = pageView;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		return pageView;
	}
	
}
