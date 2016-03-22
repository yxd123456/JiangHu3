package com.sptech.qujj.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

/**
 * 手动添加信用卡--手机号验证信息页面
 * 
 * @author sp-dev-06
 * 
 */
public class HandAddbluecardsubActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {
	private TitleBar titleBar;
	private SharedPreferences spf;
	private String bankkey, bankid, name, cardno, phone, code, usecvv, usedata;
	private Button btn_next;
	private TextView btnGetCode;
	private DownTimer downTimer;
	private EditText et_phone, ed_verify;
	// private int account_id, helpid;
	private float applynum;
	private DialogHelper dialogHelper;
	private ImageView img_clear, img_clear_two; // input 清除按钮
	private boolean Addflag; // 手动申请还款（true） 和 添加信用卡(false)的 区分

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addbluesubcard_layout);
		initView();
		Tools.addActivityList(HandAddbluecardsubActivity.this);
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		titleBar = (TitleBar) findViewById(R.id.titleBar);

		Addflag = getIntent().getBooleanExtra("addflag", false);
		System.out.println("HandAddbluecardsubActivity--Addflag ==  " + Addflag);
		name = getIntent().getStringExtra("name");
		bankkey = getIntent().getStringExtra("bankkey");
		bankid = getIntent().getStringExtra("bankid");
		cardno = getIntent().getStringExtra("cardno");
		usecvv = getIntent().getStringExtra("bankcvv");
		usedata = getIntent().getStringExtra("bankdata");
		// account_id = getIntent().getIntExtra("acount_id", 0);
		// float apply = getIntent().getIntExtra("applynum", 0);
		applynum = getIntent().getFloatExtra("applynum", 0);
		System.out.println("HandAddbluecardsubActivity-applynum ==  " + applynum);
		btn_next = (Button) findViewById(R.id.btn_next);
		ed_verify = (EditText) findViewById(R.id.ed_verify);
		titleBar.bindTitleContent("验证信息", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		btn_next.setOnClickListener(this);
		et_phone = (EditText) findViewById(R.id.ed_phone);
		btnGetCode = (TextView) findViewById(R.id.tv_sendcode);
		downTimer = new DownTimer(60 * 1000, 1000);
		btnGetCode.setOnClickListener(this);

		img_clear = (ImageView) findViewById(R.id.img_clear);
		img_clear_two = (ImageView) findViewById(R.id.img_clear_two);
		img_clear.setOnClickListener(this);
		img_clear_two.setOnClickListener(this);

		et_phone.addTextChangedListener(textWatcher);
		ed_verify.addTextChangedListener(textWatcherpwd);
		et_phone.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					img_clear_two.setVisibility(View.INVISIBLE);
					if (et_phone.getText().toString() != null && !et_phone.getText().toString().equals("")) {
						img_clear.setVisibility(View.VISIBLE);
					}
				}
			}
		});

		ed_verify.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					img_clear.setVisibility(View.INVISIBLE);
					if (ed_verify.getText().toString() != null && !ed_verify.getText().toString().equals("")) {
						img_clear_two.setVisibility(View.VISIBLE);
					}
				}
			}
		});

	}

	TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			if (et_phone.getText().toString() != null && !et_phone.getText().toString().equals("")) {
				img_clear.setVisibility(View.VISIBLE);
			} else {
				img_clear.setVisibility(View.INVISIBLE);
			}

		}
	};

	TextWatcher textWatcherpwd = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			if (ed_verify.getText().toString() != null && !ed_verify.getText().toString().equals("")) {
				img_clear_two.setVisibility(View.VISIBLE);
			} else {
				img_clear_two.setVisibility(View.INVISIBLE);
			}

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_next:
			phone = et_phone.getText().toString().trim();
			String verity = ed_verify.getText().toString().trim();
			if (phone == null || "".equals(phone)) {
				Toast.makeText(HandAddbluecardsubActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
				return;
			}

			if (verity == null || "".equals(verity)) {
				Toast.makeText(HandAddbluecardsubActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!CheckUtil.checkMobile(phone)) {
				Toast.makeText(HandAddbluecardsubActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
				return;
			}

			Tijiaomoney();
			// Intent next = new Intent(AddbankcardSubActivity.this,
			// AddbankcardSubActivity.class);
			// startActivity(next);
			break;
		case R.id.tv_sendcode: // 发送验证码
			phone = et_phone.getText().toString().trim();
			if (phone == null || "".equals(phone)) {
				Toast.makeText(HandAddbluecardsubActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			// if (phone.length() != 11) {
			// Toast.makeText(HandAddbluecardsubActivity.this, "请输入正确的手机号",
			// Toast.LENGTH_SHORT).show();
			// return;
			// }
			if (!CheckUtil.checkMobile(phone)) {
				Toast.makeText(HandAddbluecardsubActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
				return;
			}
			getkeyCode();
			break;
		case R.id.img_clear:
			et_phone.setText("");
			break;
		case R.id.img_clear_two:
			ed_verify.setText("");
			break;
		default:
			break;
		}

	}

	// 手动申请
	private void Tijiaomoney() {
		dialogHelper.startProgressDialog();
		phone = et_phone.getText().toString().trim();
		code = ed_verify.getText().toString().trim();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("money", applynum);
		data.put("help_limit", Integer.valueOf("30"));
		data.put("card_bank", name);
		data.put("card_no", cardno);
		data.put("card_phone", phone);
		data.put("card_cvv", usecvv);
		data.put("card_limit", usedata);
		data.put("code", code);

		System.out.println("data== " + data);
		// data.put("bankid", bankid);
		// data.put("bank_key", bankkey);
		// data.put("account_id", account_id);
		// data.put("help_id", helpid);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.REAPYMENT, params, BaseData.class, null, applySuccessListener, errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> applySuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			System.out.println("手动申请 code == " + response.code);
			if (response.code.equals("0")) {
				if (Addflag) {
					ToastManage.showToast("申请成功");
				} else {
					ToastManage.showToast("绑定成功");
				}

				Intent main = new Intent(HandAddbluecardsubActivity.this, MainActivity.class);
				main.putExtra("isShow", false);
				startActivity(main);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				// byte[] b = Base64.decode(response.data);
				// JSONObject s = JSON.parseObject(new String(b));
				// System.out.println(s);
				// int helpid = s.getIntValue("help_id");
				// userverify(helpid);
			} else {
				ToastManage.showToast(response.desc);
			}

		}

	};

	// private void userverify(int helpid) {
	// dialogHelper.startProgressDialog();
	// phone = et_phone.getText().toString().trim();
	// code = ed_verify.getText().toString().trim();
	// HashMap<String, Object> data = new HashMap<String, Object>();
	// data.put("card_bank", name);
	// data.put("card_no", cardno);
	// data.put("card_phone", phone);
	// data.put("card_cvv", usecvv);
	// data.put("card_limit", usedata);
	// data.put("code", code);
	// data.put("bankid", bankid);
	// data.put("bank_key", bankkey);
	// data.put("account_id", account_id);
	// data.put("help_id", helpid);
	// HashMap<String, String> params = new HashMap<String, String>();
	// params.put("uid", spf.getString(Constant.UID, "").toString());
	// params.put("data", HttpUtil.getData(data));
	// params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID,
	// "").toString(), spf.getString(Constant.KEY, "").toString()));
	// @SuppressWarnings("rawtypes")
	// HttpVolleyRequest<BaseData> request = new
	// HttpVolleyRequest<BaseData>(this, false);
	// request.HttpVolleyRequestPost(JsonConfig.BINGCREDITCARD, params,
	// BaseData.class, null, userSuccessListener, errorListener());
	//
	// }

	private void getkeyCode() {
		dialogHelper.startProgressDialog();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("card_phone", phone);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));

		if (Addflag) {
			@SuppressWarnings("rawtypes")
			HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
			request.HttpVolleyRequestPost(JsonConfig.ADD_REPAYMENT_APPLY_CODE, params, BaseData.class, null, keyCodeSuccessListener, errorListener());

		} else {
			@SuppressWarnings("rawtypes")
			HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
			request.HttpVolleyRequestPost(JsonConfig.BANKCARDCODE, params, BaseData.class, null, keyCodeSuccessListener, errorListener());

		}

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> keyCodeSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				Toast.makeText(HandAddbluecardsubActivity.this, "验证码已发送，请注意查收", Toast.LENGTH_SHORT).show();
				btnGetCode.setText("剩余59秒");
				downTimer.start();
				btnGetCode.setEnabled(false);
			} else {
				Toast.makeText(HandAddbluecardsubActivity.this, response.desc, Toast.LENGTH_SHORT).show();
				btnGetCode.setText("发送验证码");
				btnGetCode.setEnabled(true);
				// btnGetCode.setBackgroundColor(getResources().getColor(R.color.text_main));
				btnGetCode.setTextColor(getResources().getColor(R.color.main_color));
				downTimer.cancel();
			}

		}
	};

	// @SuppressWarnings("rawtypes")
	// private Listener<BaseData> userSuccessListener = new Listener<BaseData>()
	// {
	//
	// @Override
	// public void onResponse(BaseData response) {
	// dialogHelper.stopProgressDialog();
	// if (response.code.equals("0")) {
	// ToastManage.showToast("绑定成功");
	// // byte[] b = Base64.decode(response.data);
	// // JSONObject s = JSON.parseObject(new String(b));
	// // String sd = s.getString("bindid");
	// // Intent in = new Intent(AddbankcardSubActivity.this,
	// // UserInfoVerificationTwoActivity.class);
	// // in.putExtra("bindid", sd);
	// // in.putExtra("realname", name);
	// // in.putExtra("cardid", card);
	// // startActivity(in);
	// // overridePendingTransition(R.anim.push_left_in,
	// // R.anim.push_left_out);
	// } else {
	// Toast.makeText(HandAddbluecardsubActivity.this, response.desc,
	// Toast.LENGTH_SHORT).show();
	// }
	//
	// }
	// };

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
