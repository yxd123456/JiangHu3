package com.sptech.qujj.activity;

import java.util.ArrayList;
import java.util.List;

import com.sptech.qujj.R;
import com.sptech.qujj.fragment.InfoFragment1;
import com.sptech.qujj.fragment.InfoFragment2;

import android.app.AliasActivity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.widget.ImageView;


class MyFragmentAdapter extends FragmentPagerAdapter{
	
	Context context = null;
	List<Fragment> list = new ArrayList<Fragment>();
	List<ImageView> imgs = null;
	
    public MyFragmentAdapter(List<ImageView> imgs, FragmentManager fm) {
		super(fm);
		list.add(new InfoFragment1());
		list.add(new InfoFragment2());
		/*list.add(new InfoFragment1());
		list.add(new InfoFragment2());
		list.add(new InfoFragment1());*/
		this.imgs = imgs;
	}

    @Override
    public Fragment getItem(int position) {
    /*	for (int i = 0; i < imgs.size(); i++) {
				if(i == position){
					imgs.get(i).setImageResource(R.drawable.indicator_pointer_fg);
				} else {
					imgs.get(i).setImageResource(R.drawable.indicator_pointer_grey);
				}
		}*/
      return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }


}
