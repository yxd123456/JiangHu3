package com.sptech.qujj.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.db.SPHelper;
import com.sptech.qujj.dialog.CustomDialog;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 账户与安全
 * 
 * @author yebr
 * 
 */

public class AccountSafetyActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {
	private TitleBar titleBar;
	private RelativeLayout rl_cloginpwd, rl_setdealpwd, rl_setheadpwd, rl_truename, rl_shenfenzheng;// rl_truename,rl_shenfenzheng
	private Button btn_logout;
	private SharedPreferences spf;
	private TextView tv_truename, tv_shenfenzheng, tv_phone;
	private DialogHelper dialogHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accountsafety_layout);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		dialogHelper = new DialogHelper(this);
		rl_cloginpwd = (RelativeLayout) findViewById(R.id.rl_cloginpwd);
		rl_setdealpwd = (RelativeLayout) findViewById(R.id.rl_setdealpwd);
		rl_setheadpwd = (RelativeLayout) findViewById(R.id.rl_setheadpwd);

		rl_truename = (RelativeLayout) findViewById(R.id.rl_truename); // 真实姓名
		rl_shenfenzheng = (RelativeLayout) findViewById(R.id.rl_shenfenzheng); // 身份证
		tv_truename = (TextView) findViewById(R.id.tv_truename);
		tv_shenfenzheng = (TextView) findViewById(R.id.tv_shenfenzheng);

		tv_phone = (TextView) findViewById(R.id.tv_phone);
		btn_logout = (Button) findViewById(R.id.btn_logout);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		titleBar.bindTitleContent("账户与安全", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		rl_cloginpwd.setOnClickListener(this);
		rl_setdealpwd.setOnClickListener(this);
		rl_setheadpwd.setOnClickListener(this);
		btn_logout.setOnClickListener(this);

		if (spf.getString(Constant.USER_NAME, "").equals("")) {
			tv_truename.setText("未认证");
			rl_truename.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					// 跳到实名认证页面
					Intent intent = new Intent(AccountSafetyActivity.this, UserInfoVerificationActivity.class);
					intent.putExtra("nextflag", 3); // 完成实名认证后跳到 个人中心
					startActivity(intent);
					overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				}
			});
		} else {
			tv_truename.setText(DataFormatUtil.hideFirstname(spf.getString(Constant.USER_NAME, "")));
			rl_truename.setEnabled(false);
		}
		if (spf.getString(Constant.USER_CARD, "").equals("")) {
			tv_shenfenzheng.setText("未认证");
			rl_shenfenzheng.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					// 跳到实名认证页面
					Intent intent = new Intent(AccountSafetyActivity.this, UserInfoVerificationActivity.class);
					intent.putExtra("nextflag", 3);
					startActivity(intent);
					overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				}
			});
		} else {
			tv_shenfenzheng.setText(DataFormatUtil.hideCard(spf.getString(Constant.USER_CARD, "")));
			rl_shenfenzheng.setEnabled(false);
		}
		tv_phone.setText(DataFormatUtil.hideMobile(spf.getString(Constant.USER_PHONE, "")));
		String uid = spf.getString(Constant.UID, "");
		String key = spf.getString(Constant.KEY, "");
		System.out.println("uid-->" + uid);
		System.out.println("key-->" + key);
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_cloginpwd:
			Intent cloginpwd = new Intent(this, CLoginPwdActivity.class);
			startActivity(cloginpwd);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rl_setdealpwd: // 设置交易密码
			// 先判断是否实名认证
			if (spf.getString(Constant.USER_AUTH, "").equals("1")) {
				Intent setdealpwd = new Intent(this, SetDealPwdActivity.class);
				startActivity(setdealpwd);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			} else {
				Intent intent2 = new Intent(this, UserInfoVerificationActivity.class);
				ToastManage.showToast("您尚未实名认证，请先认证");
				intent2.putExtra("nextflag", 3); // 完成实名认证后跳到 个人中心
				startActivity(intent2);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}

			break;
		case R.id.rl_setheadpwd:
			Intent handpwd = new Intent(this, SetHandPwdActivity.class);
			startActivity(handpwd);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.btn_logout:
			logoutpopu();
			break;
		default:
			break;
		}

	}

	private void logoutpopu() {
		final CustomDialog dialog = new CustomDialog(this, R.style.custom_dialog_style, R.layout.custom_login_dialog_layout);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		dialog.show();
		((TextView) dialog.findViewById(R.id.tv_dialog_content)).setText("确定退出登录吗？");
		((TextView) dialog.findViewById(R.id.tv_dialog_content)).setMinLines(1);
		((TextView) dialog.findViewById(R.id.tv_ok)).setText("退出");
		dialog.findViewById(R.id.tv_ok).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				logoutHttp();
				dialog.dismiss();
			}

		});
		dialog.findViewById(R.id.tv_cancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	private void logoutHttp() {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("正在注销登录，请稍候...");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("sign", HttpUtil.getSign(spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(AccountSafetyActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.LOGOUT, params, BaseData.class, null, logoutsuccessListener(), errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> logoutsuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				if (response.code.equals("0")) {
					SPHelper.setRefresh(true);
					dialogHelper.stopProgressDialog();
					spf.edit().clear();
					spf.edit().commit();
					spf.edit().putBoolean(Constant.IS_LOGIN, false).commit();// TODO
					Intent intent = new Intent(AccountSafetyActivity.this, LoginActivity.class);
					intent.putExtra("from", "normal");
					startActivity(intent);
					overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				} else {
					dialogHelper.stopProgressDialog();
					ToastManage.showToast(response.desc);
				}
			}
		};
	}

	/**
	 * 调用web服务失败返回数据
	 * 
	 * @return
	 */
	@SuppressLint("ShowToast")
	private Response.ErrorListener errorListener() {
		return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				dialogHelper.stopProgressDialog();
			}
		};
	}
}
