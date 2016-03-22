package com.sptech.qujj.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sptech.qujj.MainActivity;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DoubleClickExitHelper;
import com.sptech.qujj.util.SharedPreferencesUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.CircleImageView;
import com.sptech.qujj.view.GestureContentView;
import com.sptech.qujj.view.GestureDrawline.GestureCallBack;

/**
 * 
 * @ClassName: GestureVerifyActivity
 * @Description: 验证手势密码
 * @author 谷松磊
 * @date 2015年3月12日 上午11:57:08
 * 
 */
public class GestureVerifyActivity extends BaseActivity implements OnClickListener {
	/** 手机号码 */
	public static final String PARAM_PHONE_NUMBER = "PARAM_PHONE_NUMBER";
	/** 意图 */
	public static final String PARAM_INTENT_CODE = "PARAM_INTENT_CODE";
	private RelativeLayout mTopLayout;
	private TextView mTextTitle;
	private TextView mTextCancel;
	private ImageView mImgUserLogo;
	private TextView mTextPhoneNumber;
	private TextView mTextTip;
	private FrameLayout mGestureContainer;
	private GestureContentView mGestureContentView;
	// private TextView mTextForget;
	DoubleClickExitHelper doubleClick;
	private TextView text_phone_number, text_other_account;
	private String mParamPhoneNumber;
	private long mExitTime = 0;
	private int mParamIntentCode;
	private SharedPreferences spf;
	private CircleImageView user_logo;
	private boolean islogin;
	private int inputnum = 5;
	/**
	 * SharedPreferences工具类
	 */
	private SharedPreferencesUtil spu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture_verify);
		doubleClick = new DoubleClickExitHelper(GestureVerifyActivity.this);
		ObtainExtraData();
		setUpViews();
		setUpListeners();
		Tools.addActivityList(GestureVerifyActivity.this);
	}

	private void ObtainExtraData() {
		mParamPhoneNumber = getIntent().getStringExtra(PARAM_PHONE_NUMBER);
		mParamIntentCode = getIntent().getIntExtra(PARAM_INTENT_CODE, 0);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		islogin = spf.getBoolean(Constant.IS_LOGIN, false);
	}

	private void setUpViews() {
		mTopLayout = (RelativeLayout) findViewById(R.id.top_layout);
		mTextTitle = (TextView) findViewById(R.id.text_title);
		mTextCancel = (TextView) findViewById(R.id.text_cancel);
		// mImgUserLogo = (ImageView) findViewById(R.id.user_logo);
		mTextPhoneNumber = (TextView) findViewById(R.id.text_phone_number);
		mTextTip = (TextView) findViewById(R.id.text_tip);
		mGestureContainer = (FrameLayout) findViewById(R.id.gesture_container);
		// mTextForget = (TextView) findViewById(R.id.text_forget_gesture);
		// mTextOther = (TextView) findViewById(R.id.text_other_account);
		// text_forget_gesture = (TextView)
		// findViewById(R.id.text_forget_gesture);
		text_other_account = (TextView) findViewById(R.id.text_other_account);
		text_other_account.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		user_logo = (CircleImageView) findViewById(R.id.user_logo);

		text_phone_number = (TextView) findViewById(R.id.text_phone_number);

		String userface = spf.getString(Constant.USER_FACE, "");
		if (userface.equals("")) {
			user_logo.setImageResource(R.drawable.img_userface);
		} else {
			byte[] b = Base64.decode(userface);
			Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
			user_logo.setImageBitmap(bit);
		}
		String phone = spf.getString(Constant.USER_PHONE, "");
		String userphone = phone.substring(0, phone.length() - (phone.substring(3)).length()) + "****" + phone.substring(7);
		text_phone_number.setText(userphone);
		// user_logo.setImageResource(R.drawable.app);
		// 初始化一个显示各个点的viewGroup
		mGestureContentView = new GestureContentView(this, true, spf.getString(Constant.HANDPASSWORD + spf.getString(Constant.USER_PHONE, ""), ""), new GestureCallBack() {

			@Override
			public void onGestureCodeInput(String inputCode) {

			}

			public void checkedSuccess() {
				mGestureContentView.clearDrawlineState(0L);
				// Toast.makeText(GestureVerifyActivity.this, "密码正确",
				// 1000).show();
				// if (islogin) {
				Intent intent = new Intent(GestureVerifyActivity.this, MainActivity.class);
				intent.putExtra("isShow", false);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				// } else {
				// Intent intent = new Intent(GestureVerifyActivity.this,
				// LoginActivity.class);
				// intent.putExtra("from", "normal");
				// startActivity(intent);
				// overridePendingTransition(R.anim.push_left_in,
				// R.anim.push_left_out);
				// }
				GestureVerifyActivity.this.finish();
			}

			@Override
			public void checkedFail() {
				inputnum--;
				if (inputnum == 0) {
					ActivityJumpManager.jumpToLogin(GestureVerifyActivity.this, LoginActivity.class);
				} else {
					mGestureContentView.clearDrawlineState(1300L);
					mTextTip.setVisibility(View.VISIBLE);
					mTextTip.setText(Html.fromHtml("<font color='#c70c1e'>密码错误,还可以输入" + inputnum + "次</font>"));

					// 左右移动动画
					Animation shakeAnimation = AnimationUtils.loadAnimation(GestureVerifyActivity.this, R.anim.shake);
					mTextTip.startAnimation(shakeAnimation);
				}

			}
		});
		// 设置手势解锁显示到哪个布局里面
		mGestureContentView.setParentView(mGestureContainer);
	}

	private void setUpListeners() {
		mTextCancel.setOnClickListener(this);
		// mTextForget.setOnClickListener(this);
		// mTextOther.setOnClickListener(this);
		// text_forget_gesture.setOnClickListener(this);
		text_other_account.setOnClickListener(this);
	}

	private String getProtectedMobile(String phoneNumber) {
		if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 11) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		builder.append(phoneNumber.subSequence(0, 3));
		builder.append("****");
		builder.append(phoneNumber.subSequence(7, 11));
		return builder.toString();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.text_cancel:
			this.finish();
			break;
		// case R.id.text_forget_gesture:
		// ToastManage.showToast("管理手势");
		// break;
		case R.id.text_other_account:
			spf.edit().putBoolean(Constant.IS_USE_HANDPWD + spf.getString(Constant.USER_PHONE, ""), false).commit();
			spf.edit().putString(Constant.HANDPASSWORD + spf.getString(Constant.USER_PHONE, ""), "").commit();
			spf.edit().putBoolean(Constant.IS_LOGIN, false).commit();
			spf.edit().putBoolean(Constant.IS_SHOWSETHANDPWD + spf.getString(Constant.USER_PHONE, ""), false).commit();
			Intent other_account = new Intent();
			other_account.setClass(this, LoginActivity.class);
			other_account.putExtra("from", "handpwd");
			startActivity(other_account);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		default:
			break;
		}
	}

	// private void showDialog() {
	// final CustomDialog dialog = new CustomDialog(this,
	// R.style.custom_dialog_style,
	// R.layout.custom_common_dialoga_layout);
	// dialog.setCanceledOnTouchOutside(true);
	// dialog.setCancelable(true);
	// dialog.show();
	// ((TextView) dialog.findViewById(R.id.tv_dialog_content))
	// .setText("忘记手势密码，需重新登录");
	// ((TextView) dialog.findViewById(R.id.tv_ok)).setText("重新登陆");
	// dialog.findViewById(R.id.tv_ok).setOnClickListener(
	// new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	//
	// dialog.dismiss();
	// spf.edit().putBoolean(Constant.IS_USE_HANDPWD, false)
	// .commit();
	// spf.edit().putString(Constant.HANDPASSWORD, "")
	// .commit();
	// spf.edit().putBoolean(Constant.IS_LOGIN, false)
	// .commit();
	// spu = new SharedPreferencesUtil(
	// GestureVerifyActivity.this,
	// Constant.REMEMBER_PWD);
	// App.getInstance().getUserCache()
	// .put(Constant.USER_INFO, "");
	// spu.clear();
	// FinalDb db = App.getDataDb();
	// db.deleteAll(UserInfo.class);
	// Intent forget = new Intent();
	// forget.putExtra("isExitApp", true);
	// forget.setClass(GestureVerifyActivity.this,
	// LoginActivity.class);
	// startActivity(forget);
	// }
	// });
	// dialog.findViewById(R.id.tv_cancel).setOnClickListener(
	// new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// dialog.dismiss();
	// }
	// });
	// }
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

			return doubleClick.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}
}
