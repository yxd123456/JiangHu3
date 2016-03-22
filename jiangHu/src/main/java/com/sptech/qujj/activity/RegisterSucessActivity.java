package com.sptech.qujj.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.sptech.qujj.MainActivity;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogRegister;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.view.EventHandleListener;

public class RegisterSucessActivity extends BaseActivity implements OnClickListener{
	Button btn_renzheng;
	ImageView guangguang;
	private SharedPreferences spf;
	private String phone;
	private String bindid;
	private String invite_code;
	private String pwd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registersuccess);
		initView();
		initListener();
		initData();
	}


	private void initView() {
		// TODO Auto-generated method stub
		guangguang = (ImageView) findViewById(R.id.xianguangguang);
		btn_renzheng = (Button) findViewById(R.id.btn_renzheng);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
	}

	private void initListener() {
		// TODO Auto-generated method stub
		guangguang.setOnClickListener(this);
		btn_renzheng.setOnClickListener(this);
	}
	
	private void initData() {
		// TODO Auto-generated method stub
		/*HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("user_phone", phone);
		data.put("user_pwd", pwd);
		data.put("invite_code", invite_code);
		data.put("bindid", bindid);
		HashMap<String, String> params = new HashMap<String, String>();

		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(false, JsonConfig.REGISTERS, params, BaseData.class, null, regSuccessListener, errorListener());*/
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.xianguangguang:
			Intent intent = new Intent(RegisterSucessActivity.this, MainActivity.class);
			intent.putExtra("isShow", false);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
        case R.id.btn_renzheng:
        	Intent intent1 = new Intent(RegisterSucessActivity.this, UserInfoVerificationActivity.class);

			// intent.putExtra("nextflag", 0); // 注册flag
			startActivity(intent1);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;

		default:
			break;
		}
		
	}
	@SuppressWarnings("rawtypes")
	private Listener<BaseData> regSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			//dialogHelper.stopProgressDialog();
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
						DialogRegister dr = new DialogRegister(RegisterSucessActivity.this, new EventHandleListener() {
							@Override
							public void eventRifhtHandlerListener() {
								Intent intent = new Intent(RegisterSucessActivity.this, UserInfoVerificationActivity.class);

								// intent.putExtra("nextflag", 0); // 注册flag
								startActivity(intent);
								overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
							}

							@Override
							public void eventLeftHandlerListener() {
								Intent intent = new Intent(RegisterSucessActivity.this, MainActivity.class);
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
					//dialogHelper.stopProgressDialog();
				}
			};
		}
}
