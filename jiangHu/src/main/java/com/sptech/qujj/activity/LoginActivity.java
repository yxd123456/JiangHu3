package com.sptech.qujj.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sptech.qujj.R;
import com.sptech.qujj.fragment.LoginFragment;
import com.sptech.qujj.fragment.RegisterFragment;
import com.sptech.qujj.util.DoubleClickExitHelper;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

public class LoginActivity extends FragmentActivity implements OnClickTitleListener, OnClickListener {

	// private TitleBar titleBar;
	// private EditText et_telphone, et_password;
	// private Button btn_login;
	private TextView tv_login, tv_register;
	public int currentPage = -1;
	private LinearLayout ll_login;
	DoubleClickExitHelper doubleClick;
	private String handpwd = "normal";
	private Fragment loginfragment = null;
	private Fragment registerfragment = null;
	FragmentManager fragmentManager = getSupportFragmentManager();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		doubleClick = new DoubleClickExitHelper(LoginActivity.this);
		initView();
		Tools.addActivityList(this);
		initFragment();

	}

	private void initView() {
		tv_login = (TextView) findViewById(R.id.tv_login);
		tv_register = (TextView) findViewById(R.id.tv_register);
		ll_login = (LinearLayout) findViewById(R.id.ll_login);
		handpwd = getIntent().getStringExtra("from");
		tv_login.setOnClickListener(this);
		tv_register.setOnClickListener(this);
	}

	public void initFragment() {
		if (currentPage >= 0) {
			// bottomBar.setCurrentTab(currentPage);
		} else {
			changeMenuFragment(0);
		}
	}

	public void changeMenuFragment(int pageId) {
		if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			getWindow().getAttributes().softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED;
		}
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);
		currentPage = pageId;
		switch (pageId) {
		case 0:
			if (loginfragment == null) {
				loginfragment = new LoginFragment();
				Bundle bundle = new Bundle();
				bundle.putString("handpwd", handpwd);
				loginfragment.setArguments(bundle);
				transaction.add(R.id.container, loginfragment);
			} else {
				transaction.show(loginfragment);
			}
			break;
		case 1:
			if (registerfragment == null) {
				registerfragment = new RegisterFragment();
				transaction.add(R.id.container, registerfragment);
			} else {
				transaction.show(registerfragment);
			}
			break;
		default:
			break;
		}
		transaction.commitAllowingStateLoss();
	}

	private void hideFragments(FragmentTransaction transaction) {
		if (loginfragment != null) {
			transaction.hide(loginfragment);
		}
		if (registerfragment != null) {
			transaction.hide(registerfragment);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_login:
			ll_login.setBackgroundResource(R.drawable.bg_signup_left);
			changeMenuFragment(0);
			break;
		case R.id.tv_register:
			ll_login.setBackgroundResource(R.drawable.bg_signup_right);
			changeMenuFragment(1);
			break;
		default:
			break;
		}
	}

	@Override
	public void onLeftButtonClick(View view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub

	}

	// 登录界面后退 直接退出应用
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

			return doubleClick.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

}
