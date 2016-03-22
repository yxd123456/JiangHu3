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
import android.widget.EditText;
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
import com.sptech.qujj.model.Product;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 赎回
 * 
 * @author yebr
 * 
 */
public class RedeemActivity extends BaseActivity implements OnClickTitleListener, OnClickListener {

	private TitleBar titleBar;
	private DialogHelper dialogHelper;
	private SharedPreferences spf;

	private int orderid;
	private int target_id;//

	private TextView tv_proname, tv_total, tv_benjing, tv_weivalue, tv_kenumber;// 产品名称，累计收益，本金，未结算收益，可赎回份数，

	private EditText ed_min;
	private Button btn_next, btn_allprofit;
	private Product curfinancialAssets;

	private int number_profit;// 赎回份数

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_licai_redeem);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		if (getIntent() != null) {
			// orderid = getIntent().getExtras().getInt("orderid");
			// target_id = getIntent().getExtras().getInt("target_id");
			curfinancialAssets = (Product) getIntent().getSerializableExtra("financialassets");
			System.out.println("赎回 curfinancialAssets id == " + curfinancialAssets.getId());
		}

		dialogHelper = new DialogHelper(this);
		spf = this.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);

		btn_next = (Button) findViewById(R.id.btn_next);
		btn_allprofit = (Button) findViewById(R.id.btn_allprofit); // 全部赎回

		tv_proname = (TextView) findViewById(R.id.tv_proname);
		tv_total = (TextView) findViewById(R.id.tv_total);
		tv_benjing = (TextView) findViewById(R.id.tv_benjing);
		tv_weivalue = (TextView) findViewById(R.id.tv_weivalue);
		tv_kenumber = (TextView) findViewById(R.id.tv_kenumber);
		ed_min = (EditText) findViewById(R.id.ed_min);

		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("赎回", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		btn_next.setOnClickListener(this);
		btn_allprofit.setOnClickListener(this);

		// if (curfinancialAssets != null) {
		// initViewData();
		// }
		getUserEarly();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_next:
			String numberprofit = ed_min.getText().toString();
			int ip_numberprofit;
			// 判断ed_min 输入的赎回份额 是否正确
			if (numberprofit.equals("")) {
				ToastManage.showToast("请输入赎回份数");
				return;
			} else {
				ip_numberprofit = Integer.valueOf(numberprofit).intValue();
			}
			if (0 < ip_numberprofit && ip_numberprofit <= number_profit) {
				Intent intent = new Intent(RedeemActivity.this, RedeemEnterPwdActivity.class);
				intent.putExtra("financialassets", curfinancialAssets);
				intent.putExtra("number_profit", ip_numberprofit);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			} else {
				ToastManage.showToast("请输入正确的赎回份额");
				return;
			}
			break;
		case R.id.btn_allprofit:
			ed_min.setText(number_profit + "");
		default:
			break;
		}

	}

	// 初始化数据
	private void initViewData() {
		tv_proname.setText(curfinancialAssets.getSubject());
		tv_total.setText("¥" + DataFormatUtil.floatsaveTwo(curfinancialAssets.getProfit_actual()));
		float buyMoney = curfinancialAssets.getBuy_money(); // 每份金额
		tv_benjing.setText(DataFormatUtil.floatsaveTwo(curfinancialAssets.getNumber_has() * buyMoney));
		tv_weivalue.setText(curfinancialAssets.getProfit_no() + "");
		number_profit = curfinancialAssets.getNumber_has() - curfinancialAssets.getNumber_profit();
		// 可赎回份额 = 购买数量 - 已结算数量
		tv_kenumber.setText(number_profit + "份");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("？？onResume=");
		getUserEarly();
		ed_min.setText("");
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

	// 赎回
	private void getUserEarly() {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		HashMap<String, Object> data = new HashMap<String, Object>();
		if (curfinancialAssets != null) {
			data.put("orderid", curfinancialAssets.getId());
		}

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		params.put("data", HttpUtil.getData(data));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.USER_EARLY, params, BaseData.class, null, usersuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> usersuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				System.out.println("赎回 返回code == " + response.code);
				dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {
						JSONObject s = JSON.parseObject(new String(b));
						System.out.println("赎回 数据=" + s);
						Product financialAssets = JSON.parseObject(new String(b), Product.class);
						curfinancialAssets = financialAssets;
						initViewData();
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

}
