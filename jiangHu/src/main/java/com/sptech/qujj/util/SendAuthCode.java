package com.sptech.qujj.util;

import java.util.HashMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.sptech.qujj.R;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.toast.ToastManage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendAuthCode {
	
	private Context context;
	private String phone;
	private EditText et_bank_phone;
	private Button btn_send_code;
	private DialogHelper dialogHelper;
	private DownTimer downTimer;
	
	public SendAuthCode(Context context, EditText et_bank_phone, Button btn_send_code){
		this.context = context;
		this.et_bank_phone = et_bank_phone;
		this.btn_send_code = btn_send_code;
		dialogHelper = new DialogHelper(context);
		downTimer = new DownTimer(60 * 1000, 1000);	
	}
	
	public void send(){
		phone = et_bank_phone.getText().toString().trim();
		if (phone.equals("")) {
			Toast.makeText(context, "手机号不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!CheckUtil.checkMobile(phone)) {
			Toast.makeText(context, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
			return;
		}
		getkeyCode();
	}
	
	private void getkeyCode() {
		// TODO Auto-generated method stub
		dialogHelper.startProgressDialog();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("user_phone", phone);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>((Activity)context, false);
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
				btn_send_code.setText("剩余59秒");
				downTimer.start();
				btn_send_code.setEnabled(false);
			} else {
				ToastManage.showToast(response.desc);
				btn_send_code.setText("发送验证码");
				btn_send_code.setEnabled(true);
				// btnGetCode.setBackgroundColor(getResources().getColor(R.color.text_main));
				btn_send_code.setTextColor(context.getResources().getColor(R.color.main_color));
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
			btn_send_code.setTextColor(context.getResources().getColor(R.color.main_color));
			if (ss != null) {
				btn_send_code.setText("　　" + ss + "s　　");
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
			btn_send_code.setTextColor(context.getResources().getColor(R.color.main_color));
			btn_send_code.setText("发送验证码");
			btn_send_code.setEnabled(true);
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
