package com.sptech.qujj.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.sptech.qujj.R;
import com.sptech.qujj.basic.SlideBasicActivity;
import com.sptech.qujj.fragment.BluecardFragment;
import com.sptech.qujj.fragment.SavingFragment;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 银行卡管理
 * 
 * @author gusonglei
 * 
 */
public class MyHandCardActivity extends SlideBasicActivity implements OnClickTitleListener, OnClickListener {

	private TitleBar titleBar;
	private Button leftButton;
	private Button rightButton;

	public int currentPage = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myhandcard_layout);
		initView();
		if (savedInstanceState != null) {
			currentPage = savedInstanceState.getInt("currentPage");
		}
		Tools.addActivityList(this);
		initFragment();
	}

	private void initView() {
		if (getIntent() != null) {
			// fromflag = getIntent().getStringExtra("fromflag");
		}
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("银行卡管理", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);

		leftButton = (Button) findViewById(R.id.btn_bluecard);
		rightButton = (Button) findViewById(R.id.btn_savingscard);

		leftButton.setOnClickListener(this);
		rightButton.setOnClickListener(this);
	}

	public void initFragment() {
		if (currentPage >= 0) {
			// bottomBar.setCurrentTab(currentPage);
		} else {
			changeMenuFragment(0);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_bluecard:
			leftButton.setTextColor(getResources().getColor(R.color.white));
			rightButton.setTextColor(getResources().getColor(R.color.main_color));
			leftButton.setBackgroundResource(R.drawable.btn_options_leftselect);
			rightButton.setBackgroundResource(R.drawable.btn_options_rightunselect);
			changeMenuFragment(0);
			break;
		case R.id.btn_savingscard:
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

	public Fragment bluecardfragment = null; // 推荐产品
	public Fragment savingfragment = null; // 我的产品
	private FragmentManager fragmentManager = getSupportFragmentManager();

	public void changeMenuFragment(int pageId) {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);
		currentPage = pageId;
		switch (pageId) {
		case 0: // 信用卡
			if (bluecardfragment == null) {
				bluecardfragment = new BluecardFragment();
				transaction.add(R.id.container, bluecardfragment);
			} else {
				transaction.show(bluecardfragment);
			}
			break;

		case 1: // 储蓄卡
			if (savingfragment == null) {
				savingfragment = new SavingFragment();
				transaction.add(R.id.container, savingfragment);
			} else {
				transaction.show(savingfragment);
			}
			break;
		default:
			break;
		}
		transaction.commitAllowingStateLoss();
	}

	private void hideFragments(FragmentTransaction transaction) {
		if (bluecardfragment != null) {
			transaction.hide(bluecardfragment);
		}
		if (savingfragment != null) {
			transaction.hide(savingfragment);
		}
	}

	// @Override
	// protected void onRestoreInstanceState(Bundle savedInstanceState) {
	// super.onRestoreInstanceState(savedInstanceState);
	// currentPage = savedInstanceState.getInt("savedInstanceState");
	// }
	//
	// @Override
	// protected void onSaveInstanceState(Bundle outState) {
	// super.onSaveInstanceState(outState);
	// outState.putInt("currentPage", currentPage);
	// }

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void actionLogin(String telphone, String pwd) {
		// Vector<Object> vector = new Vector<Object>();
		// vector.add(telphone);
		// vector.add(pwd);
		// System.out.println("username====  " + telphone + "password" + pwd);
		// InterfaceDataAction.Login(this, this, vector);
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
