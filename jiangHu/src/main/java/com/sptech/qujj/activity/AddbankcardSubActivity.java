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

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
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
 * 添加储蓄卡 
 */
public class AddbankcardSubActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {
	private TitleBar titleBar;
	private SharedPreferences spf;
	private String bankkey, bankid, name, cardno, phone, code;
	private Button btn_next;
	private TextView btnGetCode;
	private DownTimer downTimer;
	private EditText et_phone, ed_verify;
	private DialogHelper dialogHelper;
	private ImageView img_clear, img_clear_two; // input 清除按钮

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addbanksubcard_layout);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		dialogHelper = new DialogHelper(this);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		name = getIntent().getStringExtra("name");
		bankkey = getIntent().getStringExtra("bankkey");
		bankid = getIntent().getStringExtra("bankid");
		cardno = getIntent().getStringExtra("cardno");
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
			userverify();
			// Intent next = new Intent(AddbankcardSubActivity.this,
			// AddbankcardSubActivity.class);
			// startActivity(next);
			break;
		case R.id.tv_sendcode: // 发送验证码
			phone = et_phone.getText().toString().trim();

			if (phone == null || "".equals(phone)) {
				// Toast.makeText(AddbankcardSubActivity.this, "手机号不能为空",
				// Toast.LENGTH_SHORT).show();
				ToastManage.showToast("手机号不能为空");
				return;
			}
			// if (phone.length() != 11) {
			// Toast.makeText(AddbankcardSubActivity.this, "请输入正确的手机号",
			// Toast.LENGTH_SHORT).show();
			// return;
			// }
			if (!CheckUtil.checkMobile(phone)) {
				// Toast.makeText(AddbankcardSubActivity.this, "请输入正确的手机号",
				// Toast.LENGTH_SHORT).show();
				ToastManage.showToast("请输入正确的手机号");
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

	private void userverify() {
		dialogHelper.startProgressDialog();
		phone = et_phone.getText().toString().trim();
		code = ed_verify.getText().toString().trim();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("card_type", 1);
		data.put("card_bank", name);
		data.put("card_no", cardno);
		data.put("card_phone", phone);
		data.put("code", code);
		data.put("bankid", bankid);
		data.put("bank_key", bankkey);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.ADDBANKCARD, params, BaseData.class, null, userSuccessListener, errorListener());

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
				btnGetCode.setText("剩余59秒");
				downTimer.start();
				btnGetCode.setEnabled(false);
				// Toast.makeText(AddbankcardSubActivity.this, "验证码已发送，请注意查收",
				// Toast.LENGTH_SHORT).show();
				ToastManage.showToast("验证码已发送，请注意查收");

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
				ToastManage.showToast("添加银行卡成功");
				// AddbankcardSubActivity.this.finish();
				// byte[] b = Base64.decode(response.data);
				// JSONObject s = JSON.parseObject(new String(b));
				// String sd = s.getString("bindid");
				Intent in = new Intent(AddbankcardSubActivity.this, MyHandCardActivity.class);
				startActivity(in);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				AddbankcardSubActivity.this.finish();
			} else {
				// Toast.makeText(AddbankcardSubActivity.this, response.desc,
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
