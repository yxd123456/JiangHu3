package com.sptech.qujj.dialog;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.activity.RealnameSuccessActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.CheckUtil;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.view.SubSetpwdListener;

/*
 * 设置交易密码
 */
public class DialogSetPwd {

	private Activity context;
	private Dialog dialog;
	private ImageView im_close;
	private Button dialog_im_right;
	// private ImageView im_close;
	SubSetpwdListener setpwdListener;
	private EditText et_setdealpwd;
	private String dealpwd;
	private SharedPreferences spf;
	private int goflag = -1; // 0 表示 实名认证的时候 / 1表示 交易的时候 / 2 表示充值 提现的时候
	private int nextflag = 0;// //认证成功，点击下次看看，跳转的flag
	private int pro_id = 0; // 产品id

	// nextflag ! 点击下次先 ，分情况: 0 表示 实名认证的时候 -- 回到首页;
	// 1 表示 交易的时候--返回到交易界面;
	// 2 表示 充值 提现的时候,跳到账户余额 页面
	// 3 在个人中心点击实名认证，完成后跳回个人中心
	// 4在手动申请界面，完成实名认证后跳转到手动申请界面
	// 5在添加银行卡界面，完成实名认证后跳转到添加银行卡界面
	public DialogSetPwd(Activity context, int goflag, int nextflag, int pro_id) {
		this.context = context;
		// this.setpwdListener = setpwdListener;
		this.goflag = goflag;
		this.nextflag = nextflag;
		this.pro_id = pro_id;
		spf = context.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
	}

	@SuppressLint("CutPasteId")
	public void createMyDialog() {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_setpwd, null);
		et_setdealpwd = (EditText) view.findViewById(R.id.et_setdealpwd);
		im_close = (ImageView) view.findViewById(R.id.im_close);
		im_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDialog();
				if (goflag == 0) { // 实名认证的时候 ， 点击关闭弹出框（或返回按钮）时 跳到实名认证成功界面
					closeDialog();
					ToastManage.showToast("下次记得设置交易密码哟!");
					Intent in = new Intent(context, RealnameSuccessActivity.class);
					in.putExtra("nextflag", nextflag);
					in.putExtra("pro_id", pro_id);
					context.startActivity(in);
					context.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				}
			}
		});
		dialog_im_right = (Button) view.findViewById(R.id.dialog_im_right);
		dialog_im_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// closeDialog();
				dealpwd = et_setdealpwd.getText().toString().trim();
				// setpwdListener.SubsetpwdListener(dealpwd);
				System.out.println("?? " + dealpwd);
				setdealpwdHttp(dealpwd);

			}
		});

		dialog = new Dialog(context, R.style.dialog);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(view);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (context.getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
						context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
						context.getWindow().getAttributes().softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED;
					} else {
						if (goflag == 0) {
							closeDialog();
							ToastManage.showToast("下次记得设置交易密码哟!");
							Intent in = new Intent(context, RealnameSuccessActivity.class);
							in.putExtra("nextflag", nextflag);
							context.startActivity(in);
							context.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
						} else {
							closeDialog();
						}
					}

					return true;
				}
				return false;
			}
		});
		dialog.show();
	}

	public void closeDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

	private void setdealpwdHttp(String dealpwd) {
		System.out.println("setdealpwdHttp  ？？==  " + dealpwd);
		if (dealpwd.length() == 0) {
			ToastManage.showToast("交易密码不能为空");
			// Toast.makeText(context, "交易密码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!CheckUtil.checkDealPWD(dealpwd)) {
			System.out.println("交易密码==  " + dealpwd);
			ToastManage.showToast("交易密码由6-16位数字/字母组成");
			return;
		}

		System.out.println("下来  ？？==  " + dealpwd);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("pay_pwd", dealpwd);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		System.out.println("data== " + data);
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(context, false);
		request.HttpVolleyRequestPost(JsonConfig.SETPAYPWD, params, BaseData.class, null, setdealSuccessListener, errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> setdealSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			System.out.println("onResponse 下来  ？？==  " + dealpwd);
			if (response.code.equals("0")) {
				// dsp.closeDialog();
				// dialog.dismiss();
				if (goflag == 0) { // 跳到实名认证成功页面
					Intent in = new Intent(context, RealnameSuccessActivity.class);
					in.putExtra("nextflag", nextflag);
					in.putExtra("pro_id", pro_id);
					context.startActivity(in);
					context.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				} else if (goflag == 1) { // 交易
					ToastManage.showToast("交易密码设置成功！");
				} else if (goflag == 2) { // 充值、提现
					// System.out.println("交易密码设置成功！");
					ToastManage.showToast("交易密码设置成功！");
				}

			} else {
				// Toast.makeText(context, response.desc,
				// Toast.LENGTH_SHORT).show();
				ToastManage.showToast(response.desc);
			}
		}
	};

	// 调用web服务失败返回数据
	@SuppressLint("ShowToast")
	private Response.ErrorListener errorListener() {
		return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

			}
		};
	}

}
