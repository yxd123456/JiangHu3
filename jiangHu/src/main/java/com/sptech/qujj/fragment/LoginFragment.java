package com.sptech.qujj.fragment;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.igexin.sdk.PushManager;
import com.sptech.qujj.MainActivity;
import com.sptech.qujj.R;
import com.sptech.qujj.activity.ForgetPwdActivity;
import com.sptech.qujj.activity.GestureEditActivity;
import com.sptech.qujj.activity.GestureSetActivity;
import com.sptech.qujj.basic.BaseFragment;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.db.SPHelper;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.dialog.DialogSetPwd;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.CheckUtil;
import com.sptech.qujj.util.HttpUtil;

/**
 * 登录
 * 
 * @author gusonglei
 * 
 */

@SuppressLint("ValidFragment")
public class LoginFragment extends BaseFragment implements OnClickListener {
	private Button btn_login;
	private SharedPreferences spf;
	private Context mContext;
	private EditText et_loginphone, et_loginpwd;
	private TextView tv_forgetpwd;
	private DialogSetPwd dsp;
	private DialogHelper dialogHelper;
	private String handpwd = "nomal";
	private ImageView img_clear, img_clear_two; // input 清除按钮

	// @SuppressLint("ValidFragment")
	// public LoginFragment(String handpwd) {
	// this.handpwd = handpwd;
	// getArguments().getString("key") ;
	// }

	@Override
	protected View createFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View layout = inflater.inflate(R.layout.fragement_login, null);
		this.handpwd = getArguments().getString("handpwd");
		System.out.println("getArgument== " + handpwd);
		return layout;
	}

	@Override
	protected void initView() {
		mContext = getActivity();
		dialogHelper = new DialogHelper(getActivity());
		btn_login = (Button) selfView.findViewById(R.id.btn_login);
		et_loginphone = (EditText) selfView.findViewById(R.id.et_loginphone);
		et_loginpwd = (EditText) selfView.findViewById(R.id.et_loginpwd);
		tv_forgetpwd = (TextView) selfView.findViewById(R.id.tv_forgetpwd);
		btn_login.setOnClickListener(this);
		tv_forgetpwd.setOnClickListener(this);
		spf = getActivity().getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		img_clear = (ImageView) selfView.findViewById(R.id.img_clear);
		img_clear_two = (ImageView) selfView.findViewById(R.id.img_clear_two);
		img_clear.setOnClickListener(this);
		img_clear_two.setOnClickListener(this);

		et_loginphone.addTextChangedListener(textWatcher);
		et_loginpwd.addTextChangedListener(textWatcherpwd);

		et_loginphone.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					img_clear_two.setVisibility(View.INVISIBLE);
					if (et_loginphone.getText().toString() != null && !et_loginphone.getText().toString().equals("")) {
						img_clear.setVisibility(View.VISIBLE);
					}
				}
			}
		});

		et_loginpwd.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					img_clear.setVisibility(View.INVISIBLE);
					if (et_loginpwd.getText().toString() != null && !et_loginpwd.getText().toString().equals("")) {
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
			if (et_loginphone.getText().toString() != null && !et_loginphone.getText().toString().equals("")) {
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
			if (et_loginpwd.getText().toString() != null && !et_loginpwd.getText().toString().equals("")) {
				img_clear_two.setVisibility(View.VISIBLE);
			} else {
				img_clear_two.setVisibility(View.INVISIBLE);
			}

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			login();
			break;
		case R.id.tv_forgetpwd:
			Intent intent = new Intent(getActivity(), ForgetPwdActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.img_clear:
			et_loginphone.setText("");
			break;
		case R.id.img_clear_two:
			et_loginpwd.setText("");
			break;
		default:
			break;
		}

	}

	@SuppressWarnings("rawtypes")
	private void login() {
		String phone = et_loginphone.getText().toString().trim();
		String pwd = et_loginpwd.getText().toString().trim();
		if ("".equals(phone) || phone == null) {
			// Toast.makeText(getActivity(), "手机号不能为空",
			// Toast.LENGTH_LONG).show();
			ToastManage.showToast("手机号码不能为空");
			return;
		}
		if ("".equals(pwd) || pwd == null) {
			// Toast.makeText(getActivity(), "密码不能为空",
			// Toast.LENGTH_SHORT).show();
			ToastManage.showToast("密码不能为空");
			return;
		}
		if (!CheckUtil.checkMobile(phone)) {
			// Toast.makeText(getActivity(), "请输入正确的手机号",
			// Toast.LENGTH_SHORT).show();
			ToastManage.showToast("请输入正确的手机号");
			return;
		}
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("正在登录，请稍候...");

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("user_phone", phone);
		data.put("user_pwd", pwd);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(getActivity(), false);
		request.HttpVolleyRequestPost(JsonConfig.LOGIN, params, BaseData.class, null, successListener(), errorListener());
		Log.e("shuangpeng", params+"");
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> successListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				if (response.code.equals("0")) {
					SPHelper.setRefresh(true);

					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {
						JSONObject s = JSON.parseObject(new String(b));
						System.out.println("登录数据 == " + s);
						String uid = s.getString("uid");
						String key = s.getString("key");
						spf.edit().putString(Constant.UID, uid).commit();
						spf.edit().putString(Constant.KEY, key).commit();
						spf.edit().putBoolean(Constant.IS_LOGIN, true).commit();
						// Intent intent = new Intent(getActivity(),
						// MainActivity.class);
						// startActivity(intent);
						if (handpwd.equals("handpwd")) {
							dialogHelper.stopProgressDialog();
							Intent intent = new Intent(getActivity(), GestureEditActivity.class);
							intent.putExtra("handpwd", "handpwd");
							startActivity(intent);
							getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
							// getActivity().finish();
						} else {
							getUserInfo();
						}
					}

				} else {
					dialogHelper.stopProgressDialog();
					ToastManage.showToast(response.desc);
				}
			}
		};
	}

	/**
	 * 调用web服务失败返回数据
	 * 
	 * @return
	 */
	@SuppressLint("ShowToast")
	private Response.ErrorListener errorListener() {
		return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				dialogHelper.stopProgressDialog();
			}
		};
	}

	private void getUserInfo() {
		// dialogHelper.startProgressDialog();
		// dialogHelper.setDialogMessage("正在加载个人资料，请稍候...");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("sign", HttpUtil.getSign(spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(getActivity(), false);
		request.HttpVolleyRequestPost(JsonConfig.GETUSERINFO, params, BaseData.class, null, usersuccessListener(), errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> usersuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				if (response.code.equals("0")) {
					// dialogHelper.stopProgressDialog();
					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {
						JSONObject s = JSON.parseObject(new String(b));
						System.out.println("个人信息 数据 == " + s);
						String userface = s.getString("user_face");
						String username = s.getString("user_realname");
						String phone = s.getString("user_phone");
						String idcard = s.getString("user_idcard");
						String bankcard = s.getString("bankcard_num");
						String emailnum = s.getString("mail_num");
						String userauth = s.getString("is_auth");
						int ispush = s.getIntValue("is_push");
						if (ispush == 1) {// 不推送
							spf.edit().putBoolean(Constant.IS_OPENGETUI, false).commit();
							PushManager.getInstance().stopService(getActivity().getApplicationContext());
						} else {
							spf.edit().putBoolean(Constant.IS_OPENGETUI, true).commit();
							PushManager.getInstance().initialize(getActivity().getApplicationContext());
						}
						spf.edit().putString(Constant.USER_EMAILNUM, emailnum).commit();

						if (userface != null) {
							spf.edit().putString(Constant.USER_FACE, userface).commit();
						} else {
							spf.edit().putString(Constant.USER_FACE, "").commit();
						}

						spf.edit().putString(Constant.USER_NAME, username).commit();
						spf.edit().putString(Constant.USER_PHONE, phone).commit();
						spf.edit().putString(Constant.USER_CARD, idcard).commit();
						spf.edit().putString(Constant.USER_BANKCARD_NUM, bankcard).commit();
						spf.edit().putString(Constant.USER_AUTH, userauth).commit();

						// if (!spf.getBoolean(Constant.IS_SHOWSETHANDPWD +
						// spf.getString(Constant.USER_PHONE, ""), false)) {
						// spf.edit().putBoolean(Constant.IS_SHOWSETHANDPWD +
						// spf.getString(Constant.USER_PHONE, ""),
						// true).commit();
						// }
						// spf.edit().putBoolean(Constant.IS_SHOWSETHANDPWD +
						// spf.getString(Constant.USER_PHONE, ""),
						// true).commit();
						// if (spf.getString(Constant.HANDPASSWORD,
						// "").equals(""))
						// {
						// ToastManage.showToast("请先设置手势密码");
						// Intent in = new Intent(getActivity(),
						// GestureSetActivity.class);
						// startActivity(in);
						// getActivity().overridePendingTransition(
						// R.anim.push_left_in, R.anim.push_left_out);
						// } else {
						// System.out.println("IS_SHOWSETHANDPWD== " +
						// Constant.IS_SHOWSETHANDPWD);
						// System.out.println("USER_PHONE== " +
						// spf.getString(Constant.USER_PHONE, ""));
						// System.out.println("??? == " +
						// spf.getBoolean(Constant.IS_SHOWSETHANDPWD +
						// spf.getString(Constant.USER_PHONE, ""), true));
						if (dialogHelper != null) {
							dialogHelper.stopProgressDialog();
						}
						if (spf.getBoolean(Constant.IS_SHOWSETHANDPWD + spf.getString(Constant.USER_PHONE, ""), true)) {
							Intent in = new Intent(getActivity(), GestureSetActivity.class);
							startActivity(in);
							getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
							getActivity().finish();
							ToastManage.showToast("请先设置手势密码");
						} else {
							Intent intent = new Intent(getActivity(), MainActivity.class);
							intent.putExtra("isShow", false);
							startActivity(intent);
							getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
						}

					}

				} else {
					// dialogHelper.stopProgressDialog();
					ToastManage.showToast(response.desc);
				}
			}

		};
	}

	@SuppressWarnings("unused")
	private void onback() {
		// TODO Auto-generated method stub

	}

}
