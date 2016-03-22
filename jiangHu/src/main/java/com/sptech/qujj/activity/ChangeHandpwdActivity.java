package com.sptech.qujj.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sptech.qujj.MainActivity;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.GestureContentView;
import com.sptech.qujj.view.GestureDrawline.GestureCallBack;
import com.sptech.qujj.view.LockIndicator;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 
 * @ClassName: GestureEditActivity
 * @Description: 修改手势密码——验证手势密码
 * @author 谷松磊
 * @date 2015年3月11日 下午2:39:49
 * 
 */
public class ChangeHandpwdActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {
	/**
	 * 顶部的view
	 */
	private TitleBar titleBar;
	/** 手机号码 */
	public static final String PARAM_PHONE_NUMBER = "PARAM_PHONE_NUMBER";
	/** 意图 */
	public static final String PARAM_INTENT_CODE = "PARAM_INTENT_CODE";
	/** 首次提示绘制手势密码，可以选择跳过 */
	public static final String PARAM_IS_FIRST_ADVICE = "PARAM_IS_FIRST_ADVICE";
	private TextView mTextTitle;
	private TextView mTextCancel;
	private LockIndicator mLockIndicator;
	private TextView mTextTip;
	private FrameLayout mGestureContainer;
	private GestureContentView mGestureContentView;
	private TextView mTextReset;
	private String mParamSetUpcode = null;
	private String mParamPhoneNumber;
	private boolean mIsFirstInput = true;
	private String mFirstPassword = null;
	private String mConfirmPassword = null;
	private int mParamIntentCode;
	private SharedPreferences spf;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_changehandpwd);
		setUpViews();
		setUpListeners();
		Tools.addActivityList(ChangeHandpwdActivity.this);
	}

	private void setUpViews() {
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		mTextReset = (TextView) findViewById(R.id.text_reset);
		mTextReset.setClickable(false);
		mLockIndicator = (LockIndicator) findViewById(R.id.lock_indicator);
		mTextTip = (TextView) findViewById(R.id.text_tip);
		mGestureContainer = (FrameLayout) findViewById(R.id.gesture_container);
		// 初始化一个显示各个点的viewGroup
		titleBar.bindTitleContent("设置手势密码", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		mGestureContentView = new GestureContentView(this, true, spf.getString(Constant.HANDPASSWORD + spf.getString(Constant.USER_PHONE, ""), ""), new GestureCallBack() {

			@Override
			public void onGestureCodeInput(String inputCode) {

			}

			public void checkedSuccess() {
				mGestureContentView.clearDrawlineState(0L);
				// Toast.makeText(ChangeHandpwdActivity.this, "密码正确",
				// 1000).show();
				// if (islogin) {
				Intent intent = new Intent(ChangeHandpwdActivity.this, GestureEditActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				// } else {
				// Intent intent = new Intent(ChangeHandpwdActivity.this,
				// LoginActivity.class);
				// startActivity(intent);
				// }
				// ChangeHandpwdActivity.this.finish();
			}

			@Override
			public void checkedFail() {
				mGestureContentView.clearDrawlineState(1300L);
				mTextTip.setVisibility(View.VISIBLE);
				mTextTip.setText(Html.fromHtml("<font color='#c70c1e'>密码错误</font>"));
				// 左右移动动画
				Animation shakeAnimation = AnimationUtils.loadAnimation(ChangeHandpwdActivity.this, R.anim.shake);
				mTextTip.startAnimation(shakeAnimation);
			}
		});
		// 设置手势解锁显示到哪个布局里面
		mGestureContentView.setParentView(mGestureContainer);
		updateCodeList("");
	}

	private void setUpListeners() {
		mTextReset.setOnClickListener(this);

	}

	private void updateCodeList(String inputCode) {
		// 更新选择的图案
		mLockIndicator.setPath(inputCode);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.text_reset:
			mIsFirstInput = true;
			updateCodeList("");
			mTextTip.setText("请输入手势密码解锁");
			break;
		default:
			break;
		}
	}

	private boolean isInputPassValidate(String inputPassword) {
		if (TextUtils.isEmpty(inputPassword) || inputPassword.length() < 4) {
			return false;
		}
		return true;
	}

	@Override
	public void onLeftButtonClick(View view) {
		// ActivityJumpManager.finishActivity(this, 1);
		ActivityJumpManager.jumpToMain(this, MainActivity.class);

	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub

	}

}
