package com.sptech.qujj.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
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
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

public class TradingPwdActivity extends BaseActivity implements
		OnClickTitleListener, OnClickListener {
	TitleBar titleBar;
	Button btn_finish;
	TextView tv_forget;
	EditText et_pwd;
	int creditcard_id,debitcard_id;
	float money;
	String cardDetail;
	private DialogHelper dialogHelper;
	private SharedPreferences spf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trading_pwd);
		initView();
		Tools.addActivityList(this);
		initListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		dialogHelper = new DialogHelper(this);
		spf = this.getSharedPreferences(Constant.USER_INFO,
				Context.MODE_PRIVATE);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		btn_finish = (Button) findViewById(R.id.btn_finish);
		tv_forget = (TextView) findViewById(R.id.tv_forget);
		et_pwd = (EditText) findViewById(R.id.et_pwd);
		/*money = Float.parseFloat(getIntent().getStringExtra("money"));
		creditcard_id = Integer.valueOf(getIntent().getStringExtra("creditcard_id"));
		debitcard_id =Integer.valueOf(getIntent().getStringExtra("debitcard_id"));*/
		money = getIntent().getFloatExtra("money",0);
		creditcard_id = getIntent().getIntExtra("credit_id",0);
		debitcard_id =getIntent().getIntExtra("debit_id",0);
		cardDetail = getIntent().getStringExtra("cardDetail");
	}

	private void initListener() {
		// TODO Auto-generated method stub
		titleBar.bindTitleContent("输入交易密码", R.drawable.jh_back_selector, 0, "",
				"");
		titleBar.setOnClickTitleListener(this);
		btn_finish.setOnClickListener(this);
		tv_forget.setOnClickListener(this);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_forget:
			Intent intent = new Intent(TradingPwdActivity.this, SetDealPwdActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			TradingPwdActivity.this.finish();
			break;
		case R.id.btn_finish:
			apply() ;
			break;

		default:
			break;
		}
	}
	
	private void apply() {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("正在提交，请稍等片刻!");
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("credit_id", creditcard_id);
		data.put("debit_id", debitcard_id);
		data.put("money", money);
		data.put("limit", "28");
        //String pwd = Md5.md5s(spf.getString(Constant.PAYPWD, ""));
		data.put("pay_pwd", et_pwd.getText());
		//data.put("pay_pwd", spf.getString(Constant.PAYPWD, ""));
		HashMap<String, String> params = new HashMap<String, String>();
		
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data,spf.getString(Constant.UID, "")
				.toString(), spf.getString(Constant.KEY, "").toString()));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(
				this, false);
		request.HttpVolleyRequestPost(false, JsonConfig.BORROW_MONEY_APPLY,
				params, BaseData.class, null, regSuccessListener,
				errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> regSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.setDialogMessage("正在提交，请稍候...");
			if (response.code.equals("0")) {
				
				System.out.println(Base64.decode(response.data));
				byte[] b = Base64.decode(response.data);
				if (b != null && !b.equals("")) {
					JSONObject s = JSON.parseObject(new String(b));
					int borrow_id = s.getIntValue("borrow_id");
					System.out.println(borrow_id);
					if(borrow_id>0){
						dialogHelper.stopProgressDialog();
						Intent intent = new Intent(TradingPwdActivity.this,ApplyLoadSucessActivity.class);
						intent.putExtra("money", money);
						//intent.putExtra("cardNo", creditcard_id);
						intent.putExtra("cardDetail", cardDetail);
						TradingPwdActivity.this.finish();
						startActivity(intent);
					}
						
				} else {
					ToastManage.showToast(response.desc);
				}
				dialogHelper.stopProgressDialog();
			} else {
				ToastManage.showToast(response.desc);
				dialogHelper.stopProgressDialog();
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
