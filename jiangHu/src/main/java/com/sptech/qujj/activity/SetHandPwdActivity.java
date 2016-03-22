package com.sptech.qujj.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.sptech.qujj.MainActivity;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.OnChangedListener;
import com.sptech.qujj.view.SlipButton;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

public class SetHandPwdActivity extends BaseActivity implements OnClickListener, OnClickTitleListener, OnChangedListener {
	private TitleBar titleBar;
	private SlipButton button;
	private SharedPreferences spf;
	private RelativeLayout rl_forgetpwd, rl_chandpwd;
	private String from;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sethandpwd_layout);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		button = (SlipButton) findViewById(R.id.slipButton);
		rl_forgetpwd = (RelativeLayout) findViewById(R.id.rl_forgetpwd);
		rl_chandpwd = (RelativeLayout) findViewById(R.id.rl_chandpwd);
		button.SetOnChangedListener("", this);
		from = getIntent().getStringExtra("from");
		if (null == from) {
			from = "normal";
		}
		button.setChecked(spf.getBoolean(Constant.IS_USE_HANDPWD + spf.getString(Constant.USER_PHONE, ""), false));
		titleBar.bindTitleContent("设置手势密码", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		rl_forgetpwd.setOnClickListener(this);
		rl_chandpwd.setOnClickListener(this);

	}

	@Override
	public void onLeftButtonClick(View view) {
		// ActivityJumpManager.finishActivity(this, 1);
		if (from.equals("account")) {
			Intent intent = new Intent(SetHandPwdActivity.this, MainActivity.class);
			intent.putExtra("isShow", false);
			startActivity(intent);
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		} else {
			Intent intent = new Intent(SetHandPwdActivity.this, AccountSafetyActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		}
	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_forgetpwd:
			Intent intent = new Intent(SetHandPwdActivity.this, LoginActivity.class);
			intent.putExtra("from", "handpwd");
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rl_chandpwd:
			if (spf.getString(Constant.HANDPASSWORD + spf.getString(Constant.USER_PHONE, ""), "").length() == 0) {
				ToastManage.showToast("您还未设置手势密码");
			} else {
				Intent chandpwd = new Intent(SetHandPwdActivity.this, ChangeHandpwdActivity.class);
				startActivity(chandpwd);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}

			break;
		default:
			break;
		}
	}

	@Override
	public void OnChanged(String strName, boolean CheckState) {

		if (CheckState) {
			if (spf.getString(Constant.HANDPASSWORD + spf.getString(Constant.USER_PHONE, ""), "").length() == 0) {
				Intent intent = new Intent(SetHandPwdActivity.this, GestureEditActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				button.setChecked(false);
				CheckState = false;
			} else {
				spf.edit().putBoolean(Constant.IS_USE_HANDPWD + spf.getString(Constant.USER_PHONE, "0"), true).commit();
			}
		} else {
			spf.edit().putString(Constant.HANDPASSWORD + spf.getString(Constant.USER_PHONE, ""), "").commit();
			spf.edit().putBoolean(Constant.IS_USE_HANDPWD + spf.getString(Constant.USER_PHONE, ""), false).commit();
			spf.edit().putBoolean(Constant.IS_SHOWSETHANDPWD + spf.getString(Constant.USER_PHONE, ""), false).commit();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (from.equals("account")) {
				Intent intent = new Intent(SetHandPwdActivity.this, MainActivity.class);
				intent.putExtra("isShow", false);
				startActivity(intent);
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			} else {
				Intent intent = new Intent(SetHandPwdActivity.this, AccountSafetyActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
