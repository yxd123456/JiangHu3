package com.sptech.qujj.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.CheckUtil;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 
 * 导入账单邮箱
 * 
 * @author gusonglei
 * 
 */

public class AddMailActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {

	private TitleBar titleBar;
	private Button btn_done;
	private SharedPreferences spf;
	private EditText et_mailname, et_mailpwd;
	private CheckBox cb_open, cb_agree;
	private int isAuth = 1;
	private boolean isAgree = true;
	private DialogHelper dialogHelper;
	private String mailname;
	private ImageView img_clear, img_clear_two;// 清除按钮
	private TextView tv_telphone, tv_agreeopen, tv_passlog;// 打电话

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_slidebar_addmail);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("导入账单邮箱", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		et_mailname = (EditText) findViewById(R.id.et_mailname);
		et_mailpwd = (EditText) findViewById(R.id.et_mailpwd);
		btn_done = (Button) findViewById(R.id.btn_done);
		cb_open = (CheckBox) findViewById(R.id.cb_open);
		cb_agree = (CheckBox) findViewById(R.id.cb_agree);
		tv_agreeopen = (TextView) findViewById(R.id.tv_agreeopen);

		tv_passlog = (TextView) findViewById(R.id.tv_passlog);// 授权密码获取方法
		btn_done.setOnClickListener(this);
		tv_passlog.setOnClickListener(this);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		cb_open.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					isAgree = true;
				} else {
					isAgree = false;
				}

			}
		});
		cb_agree.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					isAuth = 1;
				} else {
					isAuth = 0;
				}

			}
		});
		img_clear = (ImageView) findViewById(R.id.img_clear);
		img_clear_two = (ImageView) findViewById(R.id.img_clear_two);
		img_clear.setOnClickListener(this);
		img_clear_two.setOnClickListener(this);
		tv_agreeopen.setOnClickListener(this);
		et_mailname.addTextChangedListener(textWatcher);
		et_mailpwd.addTextChangedListener(textWatcherpwd);

		tv_telphone = (TextView) findViewById(R.id.tv_telphone);
		tv_telphone.setOnClickListener(this);
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
			if (et_mailname.getText().toString() != null && !et_mailname.getText().toString().equals("")) {
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
			if (et_mailpwd.getText().toString() != null && !et_mailpwd.getText().toString().equals("")) {
				img_clear_two.setVisibility(View.VISIBLE);
			} else {
				img_clear_two.setVisibility(View.INVISIBLE);
			}
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_done: // 消息设置
			// Intent intent = new Intent(this, AddMaildoingActivity.class);
			// startActivity(intent);
			// Intent intent = new Intent(this, AddMaildoingActivity.class);
			// startActivity(intent);
			importMail();
			break;
		case R.id.img_clear:
			et_mailname.setText("");
			break;
		case R.id.img_clear_two:
			et_mailpwd.setText("");
			break;
		case R.id.tv_telphone:
			Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tv_telphone.getText().toString()));
			intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent2);
			break;
		case R.id.tv_agreeopen:
			Intent agree = new Intent(AddMailActivity.this, WebViewActivity.class);
			agree.putExtra("url", JsonConfig.HTML + "/index/mail_papers");
			agree.putExtra("title", "账单邮箱服务协议");
			startActivity(agree);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.tv_passlog:
			Intent passlog = new Intent(AddMailActivity.this, WebViewActivity.class);
			passlog.putExtra("url", JsonConfig.HTML + "/index/detail.html?id=27");
			passlog.putExtra("title", "授权密码获取");
			startActivity(passlog);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		default:
			break;
		}

	}

	private void importMail() {
		mailname = et_mailname.getText().toString().trim();
		String mailpwd = et_mailpwd.getText().toString().trim();
		if (mailname == null || "".equals(mailname)) {
			ToastManage.showToast("邮箱不能为空");
			return;
		}

		if (mailpwd == null || "".equals(mailpwd)) {
			ToastManage.showToast("邮箱密码不能为空");
			return;
		}

		if (!CheckUtil.isEmail(mailname)) {
			ToastManage.showToast("请输入正确的邮箱");
			return;
		}
		if (isAgree) {
			dialogHelper.startProgressDialog();
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("account", mailname);
			data.put("password", mailpwd);
			data.put("is_auto", isAuth);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("uid", spf.getString(Constant.UID, "").toString());
			params.put("data", HttpUtil.getData(data));
			params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
			@SuppressWarnings("rawtypes")
			HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
			request.HttpVolleyRequestPost(JsonConfig.ADDMAIL, params, BaseData.class, null, mailSuccessListener, errorListener());
		} else {
			ToastManage.showToast("请勾选协议");
			return;
		}

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> mailSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				// ToastManage.showToast("添加邮箱成功");
				byte[] b = Base64.decode(response.data);
				if (b != null && !b.equals("")) {
					JSONObject s = JSON.parseObject(new String(b));
					int account_id = s.getIntValue("account_id");
					System.out.println("mailId-->" + s);
					Intent intent = new Intent(AddMailActivity.this, AddMaildoingActivity.class);
					intent.putExtra("emailname", mailname);
					intent.putExtra("mailId", account_id);
					startActivity(intent);
					overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				} else {
					ToastManage.showToast(response.desc);
				}
				// AddMailActivity.this.finish();
			} else {
				// aduzennknfavglqu
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

}
