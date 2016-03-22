package com.sptech.qujj.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class ReSetPwdActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {
	private TitleBar titleBar;
	private EditText et_dealpwd, et_redealpwd;
	private SharedPreferences spf;
	private String bindid;
	private Button btn_sub;
	private DialogHelper dialogHelper;
	private ImageView img_clear, img_clear_two; // input 清除按钮

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resetpwd_layout);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		btn_sub = (Button) findViewById(R.id.btn_sub);
		et_dealpwd = (EditText) findViewById(R.id.et_dealpwd);
		et_redealpwd = (EditText) findViewById(R.id.et_redealpwd);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		bindid = getIntent().getStringExtra("bindid");
		titleBar.bindTitleContent("重置交易密码", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		btn_sub.setOnClickListener(this);

		img_clear = (ImageView) findViewById(R.id.img_clear);
		img_clear_two = (ImageView) findViewById(R.id.img_clear_two);
		img_clear.setOnClickListener(this);
		img_clear_two.setOnClickListener(this);

		et_dealpwd.addTextChangedListener(textWatcher);
		et_redealpwd.addTextChangedListener(textWatcherpwd);
		et_dealpwd.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					img_clear_two.setVisibility(View.INVISIBLE);
					if (et_dealpwd.getText().toString() != null && !et_dealpwd.getText().toString().equals("")) {
						img_clear.setVisibility(View.VISIBLE);
					}
				}
			}
		});

		et_redealpwd.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					img_clear.setVisibility(View.INVISIBLE);
					if (et_redealpwd.getText().toString() != null && !et_redealpwd.getText().toString().equals("")) {
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
			if (et_dealpwd.getText().toString() != null && !et_dealpwd.getText().toString().equals("")) {
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
			if (et_redealpwd.getText().toString() != null && !et_redealpwd.getText().toString().equals("")) {
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
		case R.id.btn_sub:
			String dealpwd = et_dealpwd.getText().toString().trim();
			String redealpwd = et_redealpwd.getText().toString().trim();

			if (dealpwd == null || "".equals(dealpwd)) {
				ToastManage.showToast("交易密码不能为空");
				return;
			}
			if (!CheckUtil.checkDealPWD(dealpwd)) {
				ToastManage.showToast("交易密码由6-16位数字/字母组成");
				return;
			}
			if (!dealpwd.equals(redealpwd)) {
				ToastManage.showToast("两次输入的密码不一致");
				return;
			}
			setdealpwdHttp(dealpwd);
			break;
		case R.id.img_clear:
			et_dealpwd.setText("");
			break;
		case R.id.img_clear_two:
			et_redealpwd.setText("");
			break;
		default:
			break;
		}
	}

	// 重置交易密码
	private void setdealpwdHttp(String dealpwd) {
		dialogHelper.startProgressDialog();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("bindid", bindid);
		data.put("pay_pwd", dealpwd);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(ReSetPwdActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.UPDATEPAYPWD, params, BaseData.class, null, setdealSuccessListener, errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> setdealSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				// 交易密码设置成功 后跳到 账户安全界面
				Toast.makeText(ReSetPwdActivity.this, "交易密码设置成功", Toast.LENGTH_SHORT).show();
				// Intent intent = new Intent(ReSetPwdActivity.this,
				// AccountSafetyActivity.class);
				// startActivity(intent);
				// overridePendingTransition(R.anim.push_left_in,
				// R.anim.push_left_out);
				ReSetPwdActivity.this.finish();
				SetDealPwdActivity a = new SetDealPwdActivity();
				a.setDealPwdActivity.finish();
			} else {
				Toast.makeText(ReSetPwdActivity.this, response.desc, Toast.LENGTH_SHORT).show();
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
