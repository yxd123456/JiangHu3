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
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.CheckUtil;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

public class CLoginPwdActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {
	private TitleBar titleBar;
	private EditText et_oldpwd, et_newpwd, et_renewpwd;
	private Button btn_subpwd;
	private SharedPreferences spf;
	private DialogHelper dialogHelper;
	private ImageView img_clear, img_clear_two, img_clear_three; // input 清除按钮

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cloginpwd_layout);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		et_oldpwd = (EditText) findViewById(R.id.et_oldpwd);
		et_newpwd = (EditText) findViewById(R.id.et_newpwd);
		et_renewpwd = (EditText) findViewById(R.id.et_renewpwd);
		btn_subpwd = (Button) findViewById(R.id.btn_subpwd);
		titleBar.bindTitleContent("修改登录密码", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		btn_subpwd.setOnClickListener(this);

		img_clear = (ImageView) findViewById(R.id.img_clear);
		img_clear_two = (ImageView) findViewById(R.id.img_clear_two);
		img_clear_three = (ImageView) findViewById(R.id.img_clear_three);

		img_clear.setOnClickListener(this);
		img_clear_two.setOnClickListener(this);
		img_clear_three.setOnClickListener(this);

		et_oldpwd.addTextChangedListener(textWatcherone);
		et_newpwd.addTextChangedListener(textWatchertwo);
		et_renewpwd.addTextChangedListener(textWatcherthree);

		et_oldpwd.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					img_clear_two.setVisibility(View.INVISIBLE);
					img_clear_three.setVisibility(View.INVISIBLE);
					if (et_oldpwd.getText().toString() != null && !et_oldpwd.getText().toString().equals("")) {
						img_clear.setVisibility(View.VISIBLE);
					}
				}
			}
		});

		et_newpwd.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					img_clear.setVisibility(View.INVISIBLE);
					img_clear_three.setVisibility(View.INVISIBLE);
					if (et_newpwd.getText().toString() != null && !et_newpwd.getText().toString().equals("")) {
						img_clear_two.setVisibility(View.VISIBLE);
					}
				}
			}
		});

		et_renewpwd.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					img_clear.setVisibility(View.INVISIBLE);
					img_clear_two.setVisibility(View.INVISIBLE);
					if (et_renewpwd.getText().toString() != null && !et_renewpwd.getText().toString().equals("")) {
						img_clear_three.setVisibility(View.VISIBLE);
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
			if (et_oldpwd.getText().toString() != null && !et_oldpwd.getText().toString().equals("")) {
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
			if (et_newpwd.getText().toString() != null && !et_newpwd.getText().toString().equals("")) {
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
			if (et_renewpwd.getText().toString() != null && !et_renewpwd.getText().toString().equals("")) {
				img_clear_three.setVisibility(View.VISIBLE);
			} else {
				img_clear_three.setVisibility(View.INVISIBLE);
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
			String oldpwd = et_oldpwd.getText().toString().trim();
			String newpwd = et_newpwd.getText().toString().trim();
			String renewpwd = et_renewpwd.getText().toString().trim();
			if (oldpwd == null || "".equals(oldpwd)) {
				Toast.makeText(this, "原密码不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if (newpwd == null || "".equals(newpwd)) {
				Toast.makeText(this, "新密码不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if (renewpwd == null || "".equals(renewpwd)) {
				Toast.makeText(this, "确认密码不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			// if (!CheckUtil.checkPWD(oldpwd)) {
			// Toast.makeText(this, "原密码是由6-16位数字和字母组成", Toast.LENGTH_SHORT)
			// .show();
			// return;
			// }
			if (!CheckUtil.checkPWD(newpwd)) {
				Toast.makeText(this, "新密码是由6-16位数字和字母组成", Toast.LENGTH_SHORT).show();
				return;
			}
			// if (renewpwd.length() < 6 || renewpwd.length() > 8) {
			// Toast.makeText(this, "新密码是由6-16位数字和字母组成", Toast.LENGTH_SHORT)
			// .show();
			// return;
			// }
			if (!newpwd.equals(renewpwd)) {
				Toast.makeText(this, "确认密码与新密码不一致", Toast.LENGTH_SHORT).show();
				return;
			}
			Subpwd(oldpwd, newpwd);
			break;
		case R.id.img_clear:
			et_oldpwd.setText("");
			break;
		case R.id.img_clear_two:
			et_newpwd.setText("");
			break;
		case R.id.img_clear_three:
			et_renewpwd.setText("");
			break;
		default:
			break;
		}
	}

	private void Subpwd(String oldpwd, String newpwd) {

		dialogHelper.startProgressDialog();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("user_pwd_src", oldpwd);
		data.put("user_pwd", newpwd);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(false, JsonConfig.UPDATEUSERPWD, params, BaseData.class, null, updatepwdSuccessListener, errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> updatepwdSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				Toast.makeText(CLoginPwdActivity.this, "修改登录密码成功,请重新登录", Toast.LENGTH_SHORT).show();
				ActivityJumpManager.jumpDelay(CLoginPwdActivity.this, LoginActivity.class, 1, 4, 1000);
			} else {
				Toast.makeText(CLoginPwdActivity.this, response.desc, Toast.LENGTH_SHORT).show();
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
