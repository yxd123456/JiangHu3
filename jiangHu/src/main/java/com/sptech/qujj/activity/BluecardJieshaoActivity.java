package com.sptech.qujj.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;
import com.sptech.qujj.R;

public class BluecardJieshaoActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {
	private TitleBar titleBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluecardjieshao_layout);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("信用卡介绍", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
	}

	@Override
	public void onLeftButtonClick(View view) {
		ActivityJumpManager.finishActivity(this, 1);
	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

}
