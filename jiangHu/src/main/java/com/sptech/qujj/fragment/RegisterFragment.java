package com.sptech.qujj.fragment;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
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
import com.sptech.qujj.activity.SetLoginpwdActivity;
import com.sptech.qujj.activity.WebViewActivity;
import com.sptech.qujj.basic.BaseFragment;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.CheckUtil;
import com.sptech.qujj.util.HttpUtil;

/**
 * 注册
 * 
 * @author gusonglei
 * 
 */

public class RegisterFragment extends BaseFragment implements OnClickListener {
	private TextView btnGetCode;
	public DownTimer downTimer;
	private EditText et_phone, et_code, et_invitecode;
	private ImageView consentAgreement;
	private boolean isSelect = true;
	private Button btn_subregist;
	private String phone;
	private String code;
	private String invitecode;
	private DialogHelper dialogHelper;
	private ImageView img_clear, img_clear_two, img_clear_three; // input 清除按钮
	private TextView tv_consent_agreement;

	@Override
	protected View createFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View layout = inflater.inflate(R.layout.fragement_register, null);
		return layout;
	}

	@Override
	protected void initView() {
		dialogHelper = new DialogHelper(getActivity());
		btnGetCode = (TextView) selfView.findViewById(R.id.tv_sendcode);
		downTimer = new DownTimer(60 * 1000, 1000);
		et_phone = (EditText) selfView.findViewById(R.id.et_phone);
		et_code = (EditText) selfView.findViewById(R.id.et_code);
		et_invitecode = (EditText) selfView.findViewById(R.id.et_invitecode);
		consentAgreement = (ImageView) selfView.findViewById(R.id.iv_consent_agreement);
		btn_subregist = (Button) selfView.findViewById(R.id.btn_subregist);
		tv_consent_agreement = (TextView) selfView.findViewById(R.id.tv_consent_agreement);
		showSelect();
		img_clear = (ImageView) selfView.findViewById(R.id.img_clear);
		img_clear_two = (ImageView) selfView.findViewById(R.id.img_clear_two);
		img_clear_three = (ImageView) selfView.findViewById(R.id.img_clear_three);
		img_clear.setOnClickListener(this);
		img_clear_two.setOnClickListener(this);
		img_clear_three.setOnClickListener(this);
		tv_consent_agreement.setOnClickListener(this);
		et_phone.addTextChangedListener(textWatcher);
		et_invitecode.addTextChangedListener(textWatcherpwd);
		et_code.addTextChangedListener(textWatchercode);

		et_phone.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					img_clear_two.setVisibility(View.INVISIBLE);
					img_clear_three.setVisibility(View.INVISIBLE);
					if (et_phone.getText().toString() != null && !et_phone.getText().toString().equals("")) {
						img_clear.setVisibility(View.VISIBLE);
					}
				}
			}
		});

		et_invitecode.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					img_clear.setVisibility(View.INVISIBLE);
					img_clear_three.setVisibility(View.INVISIBLE);
					if (et_invitecode.getText().toString() != null && !et_invitecode.getText().toString().equals("")) {
						img_clear_two.setVisibility(View.VISIBLE);
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
					img_clear_two.setVisibility(View.INVISIBLE);
					if (et_code.getText().toString() != null && !et_code.getText().toString().equals("")) {
						img_clear_three.setVisibility(View.VISIBLE);
					}
				}
			}
		});
	}

	@Override
	protected void initListener() {
		btnGetCode.setOnClickListener(this);
		consentAgreement.setOnClickListener(this);
		btn_subregist.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		super.initData();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_sendcode:
			phone = et_phone.getText().toString().trim();
			if (phone.equals("")) {
				Toast.makeText(getActivity(), "手机号不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!CheckUtil.checkMobile(phone)) {
				Toast.makeText(getActivity(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
				return;
			}
			getkeyCode();
			break;
		case R.id.iv_consent_agreement:
			isSelect = !isSelect;
			showSelect();
			break;
		case R.id.btn_subregist:
			register();
			break;
		case R.id.img_clear:
			et_phone.setText("");
			break;
		case R.id.img_clear_two:
			et_invitecode.setText("");
			break;
		case R.id.img_clear_three:
			et_code.setText("");
			break;
		case R.id.tv_consent_agreement:
			Intent agree = new Intent(getActivity(), WebViewActivity.class);
			agree.putExtra("url", JsonConfig.HTML + "/index/user_papers");
			agree.putExtra("title", "趣救急注册协议");
			startActivity(agree);
			getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		default:
			break;
		}

	}

	@SuppressWarnings("rawtypes")
	private void register() {
		phone = et_phone.getText().toString().trim();
		code = et_code.getText().toString().trim();
		invitecode = et_invitecode.getText().toString().trim();
		if (phone.equals("")) {
			// Toast.makeText(getActivity(), "手机号不能为空",
			// Toast.LENGTH_SHORT).show();
			ToastManage.showToast("手机号码不能为空");
			return;
		}
		if (!CheckUtil.checkMobile(phone)) {
			// Toast.makeText(getActivity(), "请输入正确的手机号",
			// Toast.LENGTH_SHORT).show();
			ToastManage.showToast("请输入正确的手机号");
			return;
		}
		if (code.equals("")) {
			// Toast.makeText(getActivity(), "验证码不能为空",
			// Toast.LENGTH_SHORT).show();
			ToastManage.showToast("验证码不能为空");
			return;
		}
		if (!CheckUtil.checkCode(code)) {
			// Toast.makeText(getActivity(), "请输入6位数的验证码",
			// Toast.LENGTH_SHORT).show();
			ToastManage.showToast("请输入6位数的验证码");
			return;
		}
		if (!isSelect) {
			// Toast.makeText(getActivity(), "未勾选注册协议",
			// Toast.LENGTH_SHORT).show();
			ToastManage.showToast("请勾选注册协议");
			return;
		}
		dialogHelper.startProgressDialog();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("user_phone", phone);
		data.put("user_code", code);
		data.put("invite_code", invitecode);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(getActivity(), false);
		request.HttpVolleyRequestPost(false, JsonConfig.REGISTER, params, BaseData.class, null, regSuccessListener, errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> regSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				System.out.println(Base64.decode(response.data));
				byte[] b = Base64.decode(response.data);
				if (b != null && !b.equals("")) {
					JSONObject s = JSON.parseObject(new String(b));
					String sd = s.getString("bindid");
					System.out.println(sd);
					phone = et_phone.getText().toString().trim();
					code = et_code.getText().toString().trim();
					invitecode = et_invitecode.getText().toString().trim();
					Intent intent = new Intent(getActivity(), SetLoginpwdActivity.class);
					intent.putExtra("user_phone", phone);
					intent.putExtra("invite_code", invitecode);
					intent.putExtra("bindid", sd);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				} else {
					ToastManage.showToast(response.desc);
				}

			} else {
				// Toast.makeText(getActivity(), response.desc,
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

	private void getkeyCode() {
		dialogHelper.startProgressDialog();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("user_phone", phone);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(getActivity(), false);
		request.HttpVolleyRequestPost(JsonConfig.REGISTERCODE, params, BaseData.class, null, keyCodeSuccessListener, errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> keyCodeSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				// Toast.makeText(getActivity(), "验证码已发送，请注意查收",
				// Toast.LENGTH_SHORT).show();
				ToastManage.showToast("验证码已发送，请注意查收");
				btnGetCode.setText("剩余59秒");
				downTimer.start();
				btnGetCode.setEnabled(false);
			} else {
				ToastManage.showToast(response.desc);
				btnGetCode.setText("发送验证码");
				btnGetCode.setEnabled(true);
				// btnGetCode.setBackgroundColor(getResources().getColor(R.color.text_main));
				btnGetCode.setTextColor(getActivity().getResources().getColor(R.color.main_color));
				downTimer.cancel();
			}

		}
	};

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
			// btnGetCode.setBackgroundColor(getResources().getColor(R.color.main_color));
			btnGetCode.setTextColor(getActivity().getResources().getColor(R.color.main_color));
			if (ss != null) {
				btnGetCode.setText("　　" + ss + "s　　");
			}
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
			btnGetCode.setTextColor(getActivity().getResources().getColor(R.color.main_color));
			btnGetCode.setText("发送验证码");
			btnGetCode.setEnabled(true);
		}
	};

	/**
	 * 勾选框
	 */
	private void showSelect() {
		if (isSelect) {
			consentAgreement.setImageResource(R.drawable.btn_agree);
		} else {
			consentAgreement.setImageResource(R.drawable.btn_noagree);
		}
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
			if (et_invitecode.getText().toString() != null && !et_invitecode.getText().toString().equals("")) {
				img_clear_two.setVisibility(View.VISIBLE);
			} else {
				img_clear_two.setVisibility(View.INVISIBLE);
			}
		}
	};

	TextWatcher textWatchercode = new TextWatcher() {
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
				img_clear_three.setVisibility(View.VISIBLE);
			} else {
				img_clear_three.setVisibility(View.INVISIBLE);
			}
		}
	};
}
