package com.sptech.qujj.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.sptech.qujj.R;
import com.sptech.qujj.basic.SlideBasicActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.fragment.MineFragment;
import com.sptech.qujj.fragment.RecommendFragment;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 理财中心
 * 
 * @author yebr
 * 
 */
public class LiCaiActivity extends SlideBasicActivity implements OnClickTitleListener, OnClickListener {

	private TitleBar titleBar;
	private Button leftButton;
	private Button rightButton;

	public int currentPage = -1;
	public Fragment onefragment = null; // 推荐产品
	public Fragment twofragment = null; // 我的产品
	private SharedPreferences spf;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_licai);
		if (savedInstanceState != null) {
			currentPage = savedInstanceState.getInt("currentPage");
		}
		initView();
		Tools.addActivityList(this);

	}

	private void initView() {
		if (getIntent() != null) {
			// fromflag = getIntent().getStringExtra("fromflag");
		}
		spf = this.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("理财产品", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);

		leftButton = (Button) findViewById(R.id.btn_tuijian);
		rightButton = (Button) findViewById(R.id.btn_mine);

		leftButton.setOnClickListener(this);
		rightButton.setOnClickListener(this);
		initFragment();
	}

	public void initFragment() {
		changeMenuFragment(0);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_tuijian:
			leftButton.setTextColor(getResources().getColor(R.color.white));
			rightButton.setTextColor(getResources().getColor(R.color.main_color));
			leftButton.setBackgroundResource(R.drawable.btn_options_leftselect);
			rightButton.setBackgroundResource(R.drawable.btn_options_rightunselect);
			changeMenuFragment(0);
			break;
		case R.id.btn_mine:
			leftButton.setTextColor(getResources().getColor(R.color.main_color));
			rightButton.setTextColor(getResources().getColor(R.color.white));
			leftButton.setBackgroundResource(R.drawable.btn_options_leftunselect);
			rightButton.setBackgroundResource(R.drawable.btn_options_rightselect);
			changeMenuFragment(1);
			break;
		default:
			break;
		}

	}

	private FragmentManager fragmentManager = getSupportFragmentManager();

	public void changeMenuFragment(int pageId) {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);
		currentPage = pageId;
		switch (pageId) {
		case 0: // 推荐产品
			if (onefragment == null) {
				onefragment = new RecommendFragment();
				transaction.add(R.id.container, onefragment);
			} else {
				// 刷新数据
				// ((RecommendFragment) onefragment).initListData();
				transaction.show(onefragment);
			}
			break;

		case 1: // 我的产品
			if (twofragment == null) {
				twofragment = new MineFragment();
				transaction.add(R.id.container, twofragment);
			} else {
				// 刷新一下数据
				// twofragment.getMineDat
				((MineFragment) twofragment).getMineData();
				transaction.show(twofragment);
			}
			break;
		default:
			break;
		}
		transaction.commitAllowingStateLoss();
	}

	private void hideFragments(FragmentTransaction transaction) {
		if (onefragment != null) {
			transaction.hide(onefragment);
		}
		if (twofragment != null) {
			transaction.hide(twofragment);
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		currentPage = savedInstanceState.getInt("savedInstanceState");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("currentPage", currentPage);
	}

	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("??onResume ");
		// initFragment();
		// leftButton.setTextColor(getResources().getColor(R.color.white));
		// rightButton.setTextColor(getResources().getColor(R.color.main_color));
		// leftButton.setBackgroundResource(R.drawable.btn_options_leftselect);
		// rightButton.setBackgroundResource(R.drawable.btn_options_rightunselect);
		// changeMenuFragment(0);
	}

	@Override
	public void onLeftButtonClick(View view) {
		// TODO Auto-generated method stub
		// 返回按钮
		ActivityJumpManager.finishActivity(this, 1);

	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub

	}

}
