package com.sptech.qujj.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.sptech.qujj.model.Product;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 
 * 
 * 赎回产品时，输入交易密码
 * 
 */
public class RedeemEnterPwdActivity extends BaseActivity implements OnClickTitleListener, OnClickListener {

	private TitleBar titleBar;
	private Button btn_finish;

	private DialogHelper dialogHelper;
	private SharedPreferences spf;

	private EditText ed_pwd;
	private String pay_pwdString = "";// 支付密码

	private Product curfinancialAssets;
	private int number_profit;// 赎回份数
	private ImageView img_clear; // input 清除按钮
	private TextView tv_forgetpwd;// 忘记交易密码

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_licai_enterpwd);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		if (getIntent() != null) {
			curfinancialAssets = (Product) getIntent().getSerializableExtra("financialassets");
			number_profit = getIntent().getIntExtra("number_profit", 0);
			System.out.println("number_profit" + number_profit);
		}

		dialogHelper = new DialogHelper(this);
		spf = this.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);

		ed_pwd = (EditText) findViewById(R.id.ed_pwd);

		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("输入交易密码", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		btn_finish = (Button) findViewById(R.id.btn_finish);
		img_clear = (ImageView) findViewById(R.id.img_clear);
		btn_finish.setOnClickListener(this);
		img_clear.setOnClickListener(this);
		ed_pwd.addTextChangedListener(textWatcher);
		tv_forgetpwd = (TextView) findViewById(R.id.tv_forgetpwd);
		tv_forgetpwd.setOnClickListener(this);
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
			if (ed_pwd.getText().toString() != null && !ed_pwd.getText().toString().equals("")) {
				img_clear.setVisibility(View.VISIBLE);
			} else {
				img_clear.setVisibility(View.INVISIBLE);
			}
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.img_clear:
			ed_pwd.setText("");
			break;
		case R.id.btn_finish:
			pay_pwdString = ed_pwd.getText().toString(); // 支付密码
			//保存支付密码
			spf.edit().putString(Constant.PAYPWD, pay_pwdString).commit();
			if (pay_pwdString.equals("")) {
				ToastManage.showToast("请输入交易密码");
				return;
			}
			if (curfinancialAssets != null && number_profit > 0) {
				checkearly();
			}
			break;
		case R.id.tv_forgetpwd:
			// 跳到设置交易密码页面
			Intent intent = new Intent(RedeemEnterPwdActivity.this, SetDealPwdActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		default:
			break;
		}
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

	// 完成赎回操作
	private void checkearly() {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		HashMap<String, Object> data = new HashMap<String, Object>();

		data.put("orderid", curfinancialAssets.getId()); //
		data.put("target_id", curfinancialAssets.getTarget_id()); //
		data.put("profit_actual", curfinancialAssets.getProfit_actual()); //
		data.put("number_profit", number_profit);//
		data.put("number_has", curfinancialAssets.getNumber_has());//
		data.put("user_money", curfinancialAssets.getNumber_has() * curfinancialAssets.getBuy_money());//
		data.put("interest_early", curfinancialAssets.getInterest_early());//
		data.put("buy_money", curfinancialAssets.getBuy_money());//
		data.put("pay_pwd", pay_pwdString);// 交易密码
		data.put("limit", curfinancialAssets.getLimit());//
		data.put("addtime", curfinancialAssets.getAddtime());//
		data.put("settled_num", curfinancialAssets.getNumber_profit());//

		System.out.println("赎回 参数 ==" + data);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		params.put("data", HttpUtil.getData(data));

		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.CHECK_EARLY, params, BaseData.class, null, orderPaysuccessListener(), orderPayerrorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> orderPaysuccessListener() {
		return new Listener<BaseData>() {
			@Override
			public void onResponse(BaseData response) {
				System.out.println("完成赎回 操作 返回code == " + response.code);
				dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					// byte[] b = Base64.decode(response.data);
					ToastManage.showToast("赎回成功!");
					Intent intent = new Intent(RedeemEnterPwdActivity.this, LiCaiActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					RedeemEnterPwdActivity.this.finish();
				} else {
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
	private Response.ErrorListener orderPayerrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dialogHelper.stopProgressDialog();
			}
		};
	}
}
