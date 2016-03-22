package com.sptech.qujj.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.MainActivity;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.CheckUtil;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/*
 * 添加信用卡  -验证信息
 */
public class AddbluecardSubActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {
	private TitleBar titleBar;
	private SharedPreferences spf;

	private Button btn_next;
	private TextView btnGetCode;
	private DownTimer downTimer;
	private EditText et_phone, ed_verify;

	// 信用卡参数
	private String bankkey, bankid, name, cardno, phone, code, usecvv, usedata;
	private int account_id;
	private DialogHelper dialogHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addbluesubcard_layout);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		name = getIntent().getStringExtra("name");
		bankkey = getIntent().getStringExtra("bankkey");
		bankid = getIntent().getStringExtra("bankid");
		cardno = getIntent().getStringExtra("cardno");
		usecvv = getIntent().getStringExtra("bankcvv");
		usedata = getIntent().getStringExtra("bankdata");
		account_id = getIntent().getIntExtra("account_id", 0);

		// helpid = getIntent().getIntExtra("help_id", 0);
		btn_next = (Button) findViewById(R.id.btn_next);
		ed_verify = (EditText) findViewById(R.id.ed_verify);
		titleBar.bindTitleContent("验证信息", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		btn_next.setOnClickListener(this);
		et_phone = (EditText) findViewById(R.id.ed_phone);
		btnGetCode = (TextView) findViewById(R.id.tv_sendcode);
		downTimer = new DownTimer(60 * 1000, 1000);
		btnGetCode.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_next:
			userverify();
			// Intent next = new Intent(AddbankcardSubActivity.this,
			// AddbankcardSubActivity.class);
			// startActivity(next);
			break;
		case R.id.tv_sendcode: // 发送验证码
			phone = et_phone.getText().toString().trim();
			if (phone == null || "".equals(phone)) {
				Toast.makeText(AddbluecardSubActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			// if (phone.length() != 11) {
			// Toast.makeText(AddbluecardSubActivity.this, "请输入正确的手机号",
			// Toast.LENGTH_SHORT).show();
			// return;
			// }
			if (!CheckUtil.checkMobile(phone)) {
				Toast.makeText(AddbluecardSubActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
				return;
			}
			getkeyCode();
			break;
		default:
			break;
		}

	}

	private void userverify() {
		dialogHelper.startProgressDialog();
		phone = et_phone.getText().toString().trim();
		code = ed_verify.getText().toString().trim();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("card_bank", name);
		data.put("card_no", cardno);
		data.put("card_phone", phone);
		data.put("card_cvv", usecvv);
		data.put("card_limit", usedata);
		data.put("code", code);
		data.put("bankid", bankid);
		data.put("bank_key", bankkey);
		data.put("account_id", account_id);
		System.out.println("data === " + data);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.BINGCREDITCARD, params, BaseData.class, null, userSuccessListener, errorListener());

	}

	private void getkeyCode() {
		dialogHelper.startProgressDialog();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("card_phone", phone);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.BANKCARDCODE, params, BaseData.class, null, keyCodeSuccessListener, errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> keyCodeSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				Toast.makeText(AddbluecardSubActivity.this, "验证码已发送，请注意查收", Toast.LENGTH_SHORT).show();
				btnGetCode.setText("剩余59秒");
				downTimer.start();
				btnGetCode.setEnabled(false);
			} else {
				ToastManage.showToast(response.desc);
				btnGetCode.setText("发送验证码");
				btnGetCode.setEnabled(true);
				// btnGetCode.setBackgroundColor(getResources().getColor(R.color.text_main));
				btnGetCode.setTextColor(getResources().getColor(R.color.main_color));
				downTimer.cancel();
			}

		}
	};
	@SuppressWarnings("rawtypes")
	private Listener<BaseData> userSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				ToastManage.showToast("绑定成功");
				Intent main = new Intent(AddbluecardSubActivity.this, MainActivity.class);
				main.putExtra("isShow", false);
				startActivity(main);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				// byte[] b = Base64.decode(response.data);
				// JSONObject s = JSON.parseObject(new String(b));
				// String sd = s.getString("bindid");
				// Intent in = new Intent(AddbankcardSubActivity.this,
				// UserInfoVerificationTwoActivity.class);
				// in.putExtra("bindid", sd);
				// in.putExtra("realname", name);
				// in.putExtra("cardid", card);
				// startActivity(in);
				// overridePendingTransition(R.anim.push_left_in,
				// R.anim.push_left_out);
			} else {
				Toast.makeText(AddbluecardSubActivity.this, response.desc, Toast.LENGTH_SHORT).show();
			}

		}
	};

	// 调用web服务失败返回数据
	@SuppressLint("ShowToast")
	private Response.ErrorListener errorListener() {
		return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				dialogHelper.stopProgressDialog();
			}
		};
	}

	@Override
	public void onLeftButtonClick(View view) {
		// TODO Auto-generated method stub
		ActivityJumpManager.finishActivity(this, 1);
	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub

	}

	class DownTimer extends CountDownTimer {

		public DownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// 倒计时开始
			long m = millisUntilFinished / 1000;
			// String hh = String.valueOf(m/3600);
			// String mm = String.valueOf(m%3600/60);
			String ss = String.valueOf(m % 3600 % 60);
			// hh = twoLength(hh);
			// mm = twoLength(mm);
			ss = twoLength(ss);
			// tvLoginGetauth.setText(ss+"秒后再次获取");
			// btnGetCode.setBackgroundColor(getResources().getColor(R.color.text_main));
			btnGetCode.setTextColor(getResources().getColor(R.color.main_color));
			btnGetCode.setText("　　" + ss + "s　　");
		}

		/**
		 * 如果小于位，加0
		 * 
		 * @param str
		 * @return
		 */
		private String twoLength(String str) {
			if (str.length() == 1) {
				return str = "0" + str;
			}
			return str;
		}

		@Override
		public void onFinish() {
			// canGetAuth = true;
			btnGetCode.setText("发送验证码");
			btnGetCode.setEnabled(true);
			// btnGetCode.setBackgroundColor(getResources().getColor(R.color.text_main));
			btnGetCode.setTextColor(getResources().getColor(R.color.main_color));
		}
	};

}
