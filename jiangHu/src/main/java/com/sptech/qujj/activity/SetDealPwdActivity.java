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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.CheckUtil;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Md5;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

public class SetDealPwdActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {
	private TitleBar titleBar;
	private Button btn_next;
	private TextView btnGetCode;
	private DownTimer downTimer;
	private EditText ed_phone, ed_name, ed_card, ed_verify;
	private String phone, name, card, verify;
	private SharedPreferences spf;
	private DialogHelper dialogHelper;
	private ImageView img_clear, img_clear_two, img_clear_three, img_clear_four; // input
																				 // //
																				 // 清除按钮

	// private int backflag;// 0 表示 返回到账户安全，1表示返回到 投资页面；
	static SetDealPwdActivity setDealPwdActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setdealpwd_layout);
		initView();
		setDealPwdActivity = this;
		Tools.addActivityList(this);
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("设置交易密码", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		btnGetCode = (TextView) findViewById(R.id.tv_sendcode);
		downTimer = new DownTimer(60 * 1000, 1000);
		btn_next = (Button) findViewById(R.id.btn_next);
		btn_next.setOnClickListener(this);

		ed_phone = (EditText) findViewById(R.id.ed_phone);
		ed_name = (EditText) findViewById(R.id.ed_name);
		ed_card = (EditText) findViewById(R.id.ed_card);
		ed_verify = (EditText) findViewById(R.id.ed_verify);

		btnGetCode = (TextView) findViewById(R.id.tv_sendcode);
		btnGetCode.setOnClickListener(this);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);

		img_clear = (ImageView) findViewById(R.id.img_clear);
		img_clear_two = (ImageView) findViewById(R.id.img_clear_two);
		img_clear_three = (ImageView) findViewById(R.id.img_clear_three);
		img_clear_four = (ImageView) findViewById(R.id.img_clear_four);

		img_clear.setOnClickListener(this);
		img_clear_two.setOnClickListener(this);
		img_clear_three.setOnClickListener(this);
		img_clear_four.setOnClickListener(this);

		ed_name.addTextChangedListener(textWatcherone);
		ed_card.addTextChangedListener(textWatchertwo);
		ed_phone.addTextChangedListener(textWatcherthree);
		ed_verify.addTextChangedListener(textWatcherfour);

		ed_name.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					img_clear_two.setVisibility(View.INVISIBLE);
					img_clear_three.setVisibility(View.INVISIBLE);
					img_clear_four.setVisibility(View.INVISIBLE);
					if (ed_name.getText().toString() != null && !ed_name.getText().toString().equals("")) {
						img_clear.setVisibility(View.VISIBLE);
					}
				}
			}
		});

		ed_card.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					img_clear.setVisibility(View.INVISIBLE);
					img_clear_three.setVisibility(View.INVISIBLE);
					img_clear_four.setVisibility(View.INVISIBLE);
					if (ed_card.getText().toString() != null && !ed_card.getText().toString().equals("")) {
						img_clear_two.setVisibility(View.VISIBLE);
					}
				}
			}
		});

		ed_phone.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					img_clear.setVisibility(View.INVISIBLE);
					img_clear_two.setVisibility(View.INVISIBLE);
					img_clear_four.setVisibility(View.INVISIBLE);
					if (ed_phone.getText().toString() != null && !ed_phone.getText().toString().equals("")) {
						img_clear_three.setVisibility(View.VISIBLE);
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
					img_clear_two.setVisibility(View.INVISIBLE);
					img_clear_three.setVisibility(View.INVISIBLE);
					if (ed_verify.getText().toString() != null && !ed_verify.getText().toString().equals("")) {
						img_clear_four.setVisibility(View.VISIBLE);
					}
				}
			}
		});

	}

	TextWatcher textWatcherone = new TextWatcher() {
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
			if (ed_name.getText().toString() != null && !ed_name.getText().toString().equals("")) {
				img_clear.setVisibility(View.VISIBLE);
			} else {
				img_clear.setVisibility(View.INVISIBLE);
			}

		}
	};

	TextWatcher textWatchertwo = new TextWatcher() {
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
			if (ed_card.getText().toString() != null && !ed_card.getText().toString().equals("")) {
				img_clear_two.setVisibility(View.VISIBLE);
			} else {
				img_clear_two.setVisibility(View.INVISIBLE);
			}

		}
	};

	TextWatcher textWatcherthree = new TextWatcher() {
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
			if (ed_phone.getText().toString() != null && !ed_phone.getText().toString().equals("")) {
				img_clear_three.setVisibility(View.VISIBLE);
			} else {
				img_clear_three.setVisibility(View.INVISIBLE);
			}

		}
	};

	TextWatcher textWatcherfour = new TextWatcher() {
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
				img_clear_four.setVisibility(View.VISIBLE);
			} else {
				img_clear_four.setVisibility(View.INVISIBLE);
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
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_sendcode: // 发送验证码
			phone = ed_phone.getText().toString().trim();
			if (phone.equals("")) {
				Toast.makeText(SetDealPwdActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!CheckUtil.checkMobile(phone)) {
				Toast.makeText(SetDealPwdActivity.this, "请输入手机号有误", Toast.LENGTH_SHORT).show();
				return;
			}
			getkeyCode();
			break;
		case R.id.btn_next:
			userverify();
			break;
		case R.id.img_clear:
			ed_name.setText("");
			break;
		case R.id.img_clear_two:
			ed_card.setText("");
			break;
		case R.id.img_clear_three:
			ed_phone.setText("");
			break;
		case R.id.img_clear_four:
			ed_verify.setText("");
			break;
		default:
			break;
		}

	}

	private void userverify() {
		phone = ed_phone.getText().toString().trim();
		name = ed_name.getText().toString().trim();
		card = ed_card.getText().toString().trim();
		verify = ed_verify.getText().toString().trim();
		dialogHelper.startProgressDialog();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("user_realname", name);
		data.put("user_idcard", card);
		data.put("user_code", verify);
		String json = JSON.toJSONString(data);
		String base = Base64.encode(json.getBytes());
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", Md5.md5s(base + spf.getString(Constant.UID, "") + spf.getString(Constant.KEY, "").toUpperCase()));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.UPDATEPAYPWDCHECK, params, BaseData.class, null, userSuccessListener, errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> userSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				byte[] b = Base64.decode(response.data);
				if (b != null && !b.equals("")) {
					String dataString = new String(b);
					if (dataString != null && !dataString.equals("")) {
						JSONObject s = JSON.parseObject(new String(b));
						String sd = s.getString("bindid");
						Intent in = new Intent(SetDealPwdActivity.this, ReSetPwdActivity.class);
						in.putExtra("bindid", sd);
						startActivity(in);
						overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					}
				}
			} else {
				Toast.makeText(SetDealPwdActivity.this, response.desc, Toast.LENGTH_SHORT).show();
			}

		}
	};

	private void getkeyCode() {
		dialogHelper.startProgressDialog();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("sign", Md5.md5s(spf.getString(Constant.UID, "") + spf.getString(Constant.KEY, "").toUpperCase()));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.UPDATAPAYPWDCODE, params, BaseData.class, null, keyCodeSuccessListener, errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> keyCodeSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				Toast.makeText(SetDealPwdActivity.this, "验证码已发送，请注意查收", Toast.LENGTH_SHORT).show();
				btnGetCode.setText("剩余59秒");
				downTimer.start();
				btnGetCode.setEnabled(false);
			} else {
				Toast.makeText(SetDealPwdActivity.this, response.desc, Toast.LENGTH_SHORT).show();
				btnGetCode.setText("发送验证码");
				btnGetCode.setEnabled(true);
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
