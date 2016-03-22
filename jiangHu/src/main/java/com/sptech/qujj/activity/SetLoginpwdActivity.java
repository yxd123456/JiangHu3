package com.sptech.qujj.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.MainActivity;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.dialog.DialogRegister;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.CheckUtil;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.EventHandleListener;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

public class SetLoginpwdActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {
	private TitleBar titleBar;
	private Button btn_next;
	private EditText et_setpwd, et_resetpwd;
	private String phone;
	private String bindid;
	private String invite_code;
	private SharedPreferences spf;
	private DialogHelper dialogHelper;
	private ImageView img_clear, img_clear_two; // input 清除按钮

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setloginpwd_layout);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		btn_next = (Button) findViewById(R.id.btn_next);
		et_setpwd = (EditText) findViewById(R.id.et_setpwd);
		et_resetpwd = (EditText) findViewById(R.id.et_resetpwd);
		phone = getIntent().getStringExtra("user_phone");
		bindid = getIntent().getStringExtra("bindid");
		invite_code = getIntent().getStringExtra("invite_code");
		titleBar.bindTitleContent("设置登录密码", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		btn_next.setOnClickListener(this);

		img_clear = (ImageView) findViewById(R.id.img_clear);
		img_clear_two = (ImageView) findViewById(R.id.img_clear_two);
		img_clear.setOnClickListener(this);
		img_clear_two.setOnClickListener(this);

		et_setpwd.addTextChangedListener(textWatcher);
		et_resetpwd.addTextChangedListener(textWatcherpwd);
		et_setpwd.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					img_clear_two.setVisibility(View.INVISIBLE);
					if (et_setpwd.getText().toString() != null && !et_setpwd.getText().toString().equals("")) {
						img_clear.setVisibility(View.VISIBLE);
					}
				}
			}
		});

		et_resetpwd.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					img_clear.setVisibility(View.INVISIBLE);
					if (et_resetpwd.getText().toString() != null && !et_resetpwd.getText().toString().equals("")) {
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
			if (et_setpwd.getText().toString() != null && !et_setpwd.getText().toString().equals("")) {
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
			if (et_resetpwd.getText().toString() != null && !et_resetpwd.getText().toString().equals("")) {
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
		case R.id.btn_next:
			register();
			break;
		case R.id.img_clear:
			et_setpwd.setText("");
			break;
		case R.id.img_clear_two:
			et_resetpwd.setText("");
			break;
		default:
			break;
		}
	}

	@SuppressWarnings("rawtypes")
	private void register() {
		String pwd = et_setpwd.getText().toString().trim();
		String repwd = et_resetpwd.getText().toString().trim();

		if (pwd.equals("")) {
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		// if (pwd.length() < 6 || pwd.length() > 8) {
		//
		// return;
		// }
		if (!CheckUtil.checkPWD(pwd)) {
			Toast.makeText(this, "密码是由6-16位数字和字母组成", Toast.LENGTH_SHORT).show();
			return;
		}
		if (repwd.equals("")) {
			Toast.makeText(this, "确认密码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!pwd.equals(repwd)) {
			Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
			return;
		}
		 Intent next=new Intent(this, RegisterSucessActivity.class);
		 startActivity(next);
		 overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		//dialogHelper.startProgressDialog();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("user_phone", phone);
		data.put("user_pwd", pwd);
		data.put("invite_code", invite_code);
		data.put("bindid", bindid);
		HashMap<String, String> params = new HashMap<String, String>();

		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(false, JsonConfig.REGISTERS, params, BaseData.class, null, regSuccessListener, errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> regSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				// Toast.makeText(SetLoginpwdActivity.this, "注册成功",
				// Toast.LENGTH_SHORT).show();
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {
						JSONObject s = JSON.parseObject(new String(b));
						System.out.println(s);
						String uid = s.getString("uid");
						String key = s.getString("key");
						spf.edit().putString(Constant.UID, uid).commit();
						spf.edit().putString(Constant.KEY, key).commit();
						spf.edit().putBoolean(Constant.IS_LOGIN, true).commit();
						DialogRegister dr = new DialogRegister(SetLoginpwdActivity.this, new EventHandleListener() {
							@Override
							public void eventRifhtHandlerListener() {
								Intent intent = new Intent(SetLoginpwdActivity.this, UserInfoVerificationActivity.class);

								// intent.putExtra("nextflag", 0); // 注册flag
								startActivity(intent);
								overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
							}

							@Override
							public void eventLeftHandlerListener() {
								Intent intent = new Intent(SetLoginpwdActivity.this, MainActivity.class);
								intent.putExtra("isShow", false);
								startActivity(intent);
								overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

							}
						});
						dr.createMyDialog();
					}
				} else {
					ToastManage.showToast(response.desc);
				}
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

}
