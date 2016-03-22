package com.sptech.qujj.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
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

public class ReLoginPwdActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {
	private TitleBar titleBar;
	private Button btn_subpwd;
	private EditText et_setpwd, et_resetpwd;
	private String phone;
	private String code;
	private DialogHelper dialogHelper;
	private ImageView img_clear, img_clear_two; // input 清除按钮

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reloginpwd_layout);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		btn_subpwd = (Button) findViewById(R.id.btn_subpwd);
		et_setpwd = (EditText) findViewById(R.id.et_setpwd);
		et_resetpwd = (EditText) findViewById(R.id.et_resetpwd);
		phone = getIntent().getStringExtra("phone");
		code = getIntent().getStringExtra("code");
		titleBar.bindTitleContent("重置登录密码", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		btn_subpwd.setOnClickListener(this);

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
		case R.id.btn_subpwd:
			// Intent next=new Intent(this, ReSetPwdActivity.class);
			// startActivity(next);
			String pwd = et_setpwd.getText().toString().trim();
			String repwd = et_resetpwd.getText().toString().trim();

			if (pwd == null || "".equals(pwd)) {
				Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if (repwd == null || "".equals(repwd)) {
				Toast.makeText(this, "确认密码不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!pwd.equals(repwd)) {
				Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!CheckUtil.checkPWD(repwd)) {
				Toast.makeText(this, "密码是由6-16位数字和字母组成", Toast.LENGTH_SHORT).show();
				return;
			}
			register(repwd);
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
	private void register(String pwd) {
		dialogHelper.startProgressDialog();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("user_phone", phone);
		data.put("user_pwd", pwd);
		data.put("user_code", code);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(false, JsonConfig.UPDATEPWD, params, BaseData.class, null, regSuccessListener, errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> regSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				Toast.makeText(ReLoginPwdActivity.this, "找回密码成功，请重新登录", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(ReLoginPwdActivity.this, LoginActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			} else {
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

}
