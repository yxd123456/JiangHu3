package com.sptech.qujj.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
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

public class ForgetPwdActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {
	private TitleBar titleBar;
	private TextView btnGetCode;
	private DownTimer downTimer;
	private EditText et_phone, et_code;
	private String phone;
	private String code;
	private Button btn_next;
	private DialogHelper dialogHelper;
	private ImageView img_clear, img_clear_two; // input 清除按钮

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgetpwd_layout);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		btnGetCode = (TextView) findViewById(R.id.tv_sendcode);
		downTimer = new DownTimer(60 * 1000, 1000);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_code = (EditText) findViewById(R.id.et_code);
		btn_next = (Button) findViewById(R.id.btn_next);
		btn_next.setOnClickListener(this);
		titleBar.bindTitleContent("找回密码", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		btnGetCode.setOnClickListener(this);

		img_clear = (ImageView) findViewById(R.id.img_clear);
		img_clear_two = (ImageView) findViewById(R.id.img_clear_two);
		img_clear.setOnClickListener(this);
		img_clear_two.setOnClickListener(this);

		et_phone.addTextChangedListener(textWatcher);
		et_code.addTextChangedListener(textWatcherpwd);
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

		et_code.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					img_clear.setVisibility(View.INVISIBLE);
					if (et_code.getText().toString() != null && !et_code.getText().toString().equals("")) {
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
			if (et_code.getText().toString() != null && !et_code.getText().toString().equals("")) {
				img_clear_two.setVisibility(View.VISIBLE);
			} else {
				img_clear_two.setVisibility(View.INVISIBLE);
			}

		}
	};

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
		case R.id.tv_sendcode:
			phone = et_phone.getText().toString().trim();
			if (phone == null || "".equals(phone)) {
				Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!CheckUtil.checkMobile(phone)) {
				Toast.makeText(this, "输入的手机号有误", Toast.LENGTH_SHORT).show();
				return;
			}
			// if(CheckUtil.checkMobile(phone)){
			// Toast.makeText(getActivity(), "请输入正确的手机号", Toast.LENGTH_SHORT)
			// .show();
			// return;
			// }
			getkeyCode();

			break;
		case R.id.btn_next:
			phone = et_phone.getText().toString().trim();
			code = et_code.getText().toString().trim();
			if (phone.length() == 0) {
				ToastManage.showToast("请输入手机号");
				return;
			}
			if (!CheckUtil.checkMobile(phone)) {
				ToastManage.showToast("输入的手机号有误");
				return;
			}
			if (code.length() == 0) {
				ToastManage.showToast("请输入验证码");
				return;
			}
			Intent intent = new Intent(ForgetPwdActivity.this, ReLoginPwdActivity.class);
			intent.putExtra("phone", phone);
			intent.putExtra("code", code);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;

		case R.id.img_clear:
			et_phone.setText("");
			break;
		case R.id.img_clear_two:
			et_code.setText("");
			break;
		default:
			break;
		}

	}

	private void getkeyCode() {
		dialogHelper.startProgressDialog();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("user_phone", phone);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.UPDATEPWDCODE, params, BaseData.class, null, keyCodeSuccessListener, errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> keyCodeSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				ToastManage.showToast("验证码已发送，请注意查收！");
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
