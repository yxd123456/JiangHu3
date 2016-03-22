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
import android.widget.RelativeLayout;
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
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 账户余额
 * 
 * @author yebr
 * 
 */
public class AccountBalanceActivity extends BaseActivity implements OnClickTitleListener, OnClickListener {

	private TitleBar titleBar;

	private RelativeLayout rela_balance;
	private Button btn_tixian, btn_recharge; // 提现 充值
	private SharedPreferences spf;
	private TextView tv_balancevalue, tv_usermoney, tv_djvalue;
	private DialogHelper dialogHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_licai_accountbalance);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("账户余额", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		dialogHelper = new DialogHelper(this);
		spf = this.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);

		btn_tixian = (Button) findViewById(R.id.btn_tixian);
		tv_balancevalue = (TextView) findViewById(R.id.tv_balancevalue);
		btn_recharge = (Button) findViewById(R.id.btn_recharge);
		tv_usermoney = (TextView) findViewById(R.id.tv_usermoney);
		tv_djvalue = (TextView) findViewById(R.id.tv_djvalue);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		rela_balance = (RelativeLayout) findViewById(R.id.rela_balance);
		rela_balance.setOnClickListener(this);
		btn_tixian.setOnClickListener(this);
		btn_recharge.setOnClickListener(this);
		getHttpInfo();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getHttpInfo();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rela_balance:
			Intent intent = new Intent(this, AccountDetailActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.btn_tixian: // 提现
			Intent intent2;
			if (spf.getString(Constant.USER_AUTH, "").equals("1")) {
				intent2 = new Intent(this, TiXianActivity.class);
			} else {
				intent2 = new Intent(this, UserInfoVerificationActivity.class);
				intent2.putExtra("nextflag", 2);
			}
			startActivity(intent2);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.btn_recharge: // 充值
			Intent intent3;
			if (spf.getString(Constant.USER_AUTH, "").equals("1")) {
				intent3 = new Intent(this, RechargeActivity.class);
			} else {
				intent3 = new Intent(this, UserInfoVerificationActivity.class);
				intent3.putExtra("nextflag", 2);
			}
			startActivity(intent3);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		default:
			break;
		}

	}

	private void getHttpInfo() {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("加载数据...");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("sign", HttpUtil.getSign(spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(AccountBalanceActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.ACCOUNTMONEY, params, BaseData.class, null, usersuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> usersuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {
						JSONObject s = JSON.parseObject(new String(b));
						System.out.println(s);
						float frozen = s.getFloat("frozen_money");
						float user_money = s.getFloat("user_money");
						tv_balancevalue.setText("¥" + DataFormatUtil.floatsaveTwo(user_money + frozen));
						tv_usermoney.setText(DataFormatUtil.floatsaveTwo(user_money) + "");
						tv_djvalue.setText(DataFormatUtil.floatsaveTwo(frozen) + "");
					} else {
						ToastManage.showToast(response.desc);
					}
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
