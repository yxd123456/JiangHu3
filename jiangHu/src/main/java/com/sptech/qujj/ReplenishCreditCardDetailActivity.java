package com.sptech.qujj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.sptech.qujj.activity.AddbluecardSubActivity;
import com.sptech.qujj.activity.BluecardJieshaoActivity;
import com.sptech.qujj.activity.HandAddbluecardActivity;
import com.sptech.qujj.activity.SetLoginpwdActivity;
import com.sptech.qujj.activity.UsableBankActivity;
import com.sptech.qujj.activity.UserInfoVerificationTwoActivity;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogData;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.UsablebankBean;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.CheckUtil;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.SelectBankCard;
import com.sptech.qujj.util.SendAuthCode;
import com.sptech.qujj.util.UploadDataToServer;
import com.sptech.qujj.util.UploadDataToServer.OnRequestFinished;
import com.sptech.qujj.view.EventDataListener;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 补充信用卡详情
 * @author 叶旭东
 *
 */
public class ReplenishCreditCardDetailActivity extends BaseActivity implements OnClickListener, OnClickTitleListener{
	
	private EditText 
		et_cardholder,//持卡人姓名 
		et_bank_phone,//银行卡预留号码
		et_auth_code,//手机验证码
		et_bank_code,//银行卡卡号
		et_security_code;//信用卡安全码
	private Button btn_send_code, btn_commit;//发送验证码按钮，提交信息按钮
	private ImageView iv_bank_logo;//显示银行logo，介绍银行卡
	private TextView tv_bank_name, tv_date, tv_select_bank_card;//银行名称，银行卡有效期, 打开银行列表
	private SelectBankCard selectBankCard;//封装了银行卡的选择与显示操作
	private String curUsedata;//用于接收用户输入的日期字符串
	private ImageView wenhao_icon1, wenhao_icon2;
	private TitleBar titleBar;
	private DialogHelper dialogHelper;
	private String[] str_data;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_replenish_credit_card_detail);
		
		init();
		
		selectBankCard();//选择开卡行
		
		selectDate();//选择信用卡有效期
		
		sendAuthCode();//点击按钮发送验证码
		
		commit();//将用户输入的信息进行提交
		
	}

	private void commit() {
		// TODO Auto-generated method stub
		btn_commit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				dialogHelper.startProgressDialog("正在提交中...");		
				new UploadDataToServer(ReplenishCreditCardDetailActivity.this).upload(JsonConfig.CARD_LIST, doData(), new OnRequestFinished() {
					
					@Override
					public void success(BaseData response) {
						// TODO Auto-generated method stub
						dialogHelper.stopProgressDialog();
						if (response.code.equals("0")) {
							ToastManage.showToast("提交成功");
							Intent main = new Intent(ReplenishCreditCardDetailActivity.this, MainActivity.class);
							main.putExtra("isShow", false);
							startActivity(main);
							overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
						} else {
							Toast.makeText(ReplenishCreditCardDetailActivity.this, response.desc, Toast.LENGTH_SHORT).show();
						}
					}
					
					@Override
					public void failure(VolleyError error) {
						// TODO Auto-generated method stub
						dialogHelper.stopProgressDialog();
					}
				});
			}

			private HashMap<String, Object> doData() {
				// TODO Auto-generated method stub
				HashMap<String, Object> data = new HashMap<String, Object>();	
				data.put("card_bank", tv_bank_name.getText().toString().trim());
				data.put("card_no", et_bank_code.getText().toString().trim());
				data.put("card_cvv", et_security_code.getText().toString().trim());	
				data.put("card_limit", tv_date.getText().toString().trim());
				data.put("bankid", str_data[1]);
				data.put("bank_key", str_data[0]);
				return data;
			}

		});
	}
	
	

	private void selectDate() {
		// TODO Auto-generated method stub
		tv_date.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DialogData dr = new DialogData(ReplenishCreditCardDetailActivity.this, new EventDataListener() {

					@Override
					public void eventDataHandlerListener(String data, String showdata) {
						tv_date.setText(showdata + "");
						curUsedata = data;
					}
				});
				dr.createMyDialog();
			}
		});
	}

	private void selectBankCard() {
		// TODO Auto-generated method stub
		tv_select_bank_card.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectBankCard.select();
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:
			// TODO
			if (resultCode == 5) {
				str_data = selectBankCard.backToShow(data, tv_bank_name, iv_bank_logo);
			}
			break;
		}
	}
	
		

	private void sendAuthCode() {
		// TODO Auto-generated method stub
		btn_send_code.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new SendAuthCode(ReplenishCreditCardDetailActivity.this, et_bank_phone, btn_send_code).send();
			}
		});
	}

	private void init() {
		// TODO Auto-generated method stub
		et_cardholder = (EditText) findViewById(R.id.et_cardholder);
		et_auth_code = (EditText) findViewById(R.id.et_code);
		et_bank_phone = (EditText) findViewById(R.id.bank_obligate_phone_number);
		et_bank_code = (EditText) findViewById(R.id.et_bank_code);
		et_security_code = (EditText) findViewById(R.id.et_security_code);
		btn_send_code = (Button) findViewById(R.id.send_code);	
		btn_commit = (Button) findViewById(R.id.btn_commit);
		iv_bank_logo = (ImageView) findViewById(R.id.iv_banklogo);
		wenhao_icon1 = (ImageView) findViewById(R.id.iv_cvvanswer1);
		wenhao_icon2 = (ImageView) findViewById(R.id.iv_cvvanswer2);
		tv_bank_name = (TextView) findViewById(R.id.tv_bank_name);
		tv_date = (TextView) findViewById(R.id.tv_date);
		tv_select_bank_card = (TextView) findViewById(R.id.tv_select_bank_card);
		selectBankCard = new SelectBankCard(this);	
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("补充信用卡详情", R.drawable.jh_back_selector, 0, "", "");
		dialogHelper = new DialogHelper(this);
		
		wenhao_icon1.setOnClickListener(this);
		wenhao_icon2.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		startActivity(new Intent(ReplenishCreditCardDetailActivity.this, BluecardJieshaoActivity.class));
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

/*HashMap<String, String> params = new HashMap<String, String>();
params.put("uid", spf.getString(Constant.UID, ""));
params.put("sign", HttpUtil.getSign(spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));

HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(ReplenishCreditCardDetailActivity.this, false);
request.HttpVolleyRequestPost(JsonConfig.CARD_LIST, params, BaseData.class, null, userSuccessListener, errorListener());*/

/*private Listener<BaseData> userSuccessListener = new Listener<BaseData>() {

	@Override
	public void onResponse(BaseData response) {
		dialogHelper.stopProgressDialog();
		if (response.code.equals("0")) {
			ToastManage.showToast("提交成功");
			Intent main = new Intent(ReplenishCreditCardDetailActivity.this, MainActivity.class);
			main.putExtra("isShow", false);
			startActivity(main);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		} else {
			Toast.makeText(ReplenishCreditCardDetailActivity.this, response.desc, Toast.LENGTH_SHORT).show();
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
}*/