package com.sptech.qujj.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sptech.qujj.MainActivity;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.DoubleClickExitHelper;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.GestureContentView;
import com.sptech.qujj.view.GestureDrawline.GestureCallBack;
import com.sptech.qujj.view.LockIndicator;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 
 * @ClassName: GestureEditActivity
 * @Description: 手势设置界面
 * @author 谷松磊
 * @date 2015年3月11日 下午2:39:49
 * 
 */
public class GestureSetActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {

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
	DoubleClickExitHelper doubleClick;
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
		setContentView(R.layout.activity_gesture_set);
		Tools.addActivityList(GestureSetActivity.this);
		setUpViews();
		setUpListeners();

	}

	private void setUpViews() {
		mTextReset = (TextView) findViewById(R.id.text_reset);
		mTextReset.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mLockIndicator = (LockIndicator) findViewById(R.id.lock_indicator);
		mTextTip = (TextView) findViewById(R.id.text_tip);
		mGestureContainer = (FrameLayout) findViewById(R.id.gesture_container);
		// 初始化一个显示各个点的viewGroup
		doubleClick = new DoubleClickExitHelper(GestureSetActivity.this);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		mGestureContentView = new GestureContentView(this, false, "", new GestureCallBack() {
			@Override
			public void onGestureCodeInput(String inputCode) {
				if (!isInputPassValidate(inputCode)) {
					mTextTip.setText(Html.fromHtml("<font color='#c70c1e'>最少链接4个点, 请重新输入</font>"));
					mGestureContentView.clearDrawlineState(0L);
					return;
				}
				if (mIsFirstInput) {
					mFirstPassword = inputCode;
					updateCodeList(inputCode);
					mGestureContentView.clearDrawlineState(0L);
					mTextTip.setClickable(true);
					mTextTip.setText("再次确认手势密码");
				} else {
					if (inputCode.equals(mFirstPassword)) {
						Toast.makeText(GestureSetActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
						spf.edit().putString(Constant.HANDPASSWORD + spf.getString(Constant.USER_PHONE, ""), inputCode).commit();
						spf.edit().putBoolean(Constant.IS_USE_HANDPWD + spf.getString(Constant.USER_PHONE, ""), true).commit();
						spf.edit().putBoolean(Constant.IS_SHOWSETHANDPWD + spf.getString(Constant.USER_PHONE, ""), false).commit();
						mGestureContentView.clearDrawlineState(0L);
						Intent intent = new Intent(GestureSetActivity.this, MainActivity.class);
						intent.putExtra("isShow", false);
						startActivity(intent);
						overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					} else {
						mTextTip.setText(Html.fromHtml("<font color='#c70c1e'>与上一次绘制不一致，请重新绘制</font>"));
						// 左右移动动画
						Animation shakeAnimation = AnimationUtils.loadAnimation(GestureSetActivity.this, R.anim.shake);
						mTextTip.startAnimation(shakeAnimation);
						// 保持绘制的线，1.5秒后清除
						mGestureContentView.clearDrawlineState(1300L);
					}
				}
				mIsFirstInput = false;
			}

			@Override
			public void checkedSuccess() {

			}

			@Override
			public void checkedFail() {

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
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra("isShow", false);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
		ActivityJumpManager.finishActivity(this, 1);

	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

			return doubleClick.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}
}
