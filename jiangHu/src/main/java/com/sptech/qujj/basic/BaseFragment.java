package com.sptech.qujj.basic;

import com.sptech.qujj.util.MyLog;
import com.umeng.socialize.utils.Log;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public abstract class BaseFragment extends Fragment{
	
	public static final String TAG = "BaseFragment";
	protected View selfView;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		MyLog.doLog("当前的Fragment是"+getClass().getSimpleName());

	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//获取当前类名
		Log.e("shuangpeng", "Fragment : "+this.getClass().getName());
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		selfView = createFragmentView(inflater, container, savedInstanceState);
		initView();
		initListener();
		initData();
		return selfView;
	}
	

	protected abstract View createFragmentView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState);
	
	protected void initView() {}
	protected void initListener() {}
	protected void initData() {}
}
