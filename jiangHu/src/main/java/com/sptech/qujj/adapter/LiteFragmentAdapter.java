package com.sptech.qujj.adapter;

import java.util.ArrayList;
import java.util.List;

import com.sptech.qujj.R;
import com.sptech.qujj.fragment.LiteFragment;
import com.sptech.qujj.toast.ToastManage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LiteFragmentAdapter extends FragmentPagerAdapter {
	
	private List<LiteFragment> list = new ArrayList<LiteFragment>();
	
	public LiteFragmentAdapter(FragmentManager fm, List<LiteFragment> list) {
		super(fm);
		// TODO Auto-generated constructor stub
		this.list = list;
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 5;
	}
	


}
