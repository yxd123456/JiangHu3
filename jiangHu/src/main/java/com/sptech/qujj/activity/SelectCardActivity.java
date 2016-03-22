package com.sptech.qujj.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.adapter.LVBankCardAdapter;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.model.BankcardBean;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

public class SelectCardActivity extends BaseActivity implements OnClickTitleListener, OnClickListener {

	private TitleBar titleBar;
	DialogHelper dialogHelper;

	private ListView listview_card; // 银行卡列表

	private LVBankCardAdapter lvBankCardAdapter;//

	private List<BankcardBean> bankList = new ArrayList<BankcardBean>();// banklist

	private SharedPreferences spf;
	private int backflag;
	private int position_select = 0; // 上次选中的 银行卡

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_select_card);
		initView();
		Tools.addActivityList(this);
	}

	@SuppressWarnings("unchecked")
	private void initView() {
		if (getIntent() != null) {
			backflag = getIntent().getExtras().getInt("backflag");
			position_select = getIntent().getExtras().getInt("position_select");
			System.out.println("SelectCardActivity  --- " + position_select);
			bankList = (List<BankcardBean>) getIntent().getSerializableExtra("bankcardlist");
			System.out.println(" bankcardlist size == " + bankList.size());
		}
		// if (backflag == 3) {
		// position_select = getIntent().getExtras().getInt("position_select");
		//
		// }
		dialogHelper = new DialogHelper(this);
		spf = this.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("选择银行卡", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		listview_card = (ListView) findViewById(R.id.listview_card);

		// 将解析的银行卡List 转换为Map (银行卡名称，图标流)
		HashMap<String, String> cardMap = new HashMap<String, String>();
		cardMap = DataFormatUtil.getCardMap(spf);
		lvBankCardAdapter = new LVBankCardAdapter(SelectCardActivity.this, backflag, cardMap, bankList);
		listview_card.setAdapter(lvBankCardAdapter);
		// getBankcardData();
		lvBankCardAdapter.setDefalut(position_select);

	}

	// private void getBankcardData() {
	// System.out.println("getBankcardData== " + position_select);
	// dialogHelper.startProgressDialog();
	// dialogHelper.setDialogMessage("请稍候...");
	// HashMap<String, Object> data = new HashMap<String, Object>();
	// data.put("card_type", 1);
	// HashMap<String, String> params = new HashMap<String, String>();
	// params.put("uid", spf.getString(Constant.UID, ""));
	// params.put("data", HttpUtil.getData(data));
	// params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID,
	// ""), spf.getString(Constant.KEY, "")));
	// HttpVolleyRequest<BaseData> request = new
	// HttpVolleyRequest<BaseData>(SelectCardActivity.this, false);
	// request.HttpVolleyRequestPost(JsonConfig.BANKCARDLIST, params,
	// BaseData.class, null, cardsuccessListener(), errorListener());
	// }
	//
	// @SuppressWarnings("rawtypes")
	// private Listener<BaseData> cardsuccessListener() {
	// return new Listener<BaseData>() {
	// @Override
	// public void onResponse(BaseData response) {
	// dialogHelper.stopProgressDialog();
	// if (response.code.equals("0")) {
	// byte[] b = Base64.decode(response.data);
	// if (b != null && !b.equals("")) {
	// JSONArray s = JSON.parseArray(new String(b));
	// System.out.println("返回数据" + new String(b));
	// List<BankcardBean> banklist = JSON.parseArray(new String(b),
	// BankcardBean.class);
	// bankList = banklist;
	// initListViewData();
	// }
	// } else {
	// dialogHelper.stopProgressDialog();
	// ToastManage.showToast(response.desc);
	// }
	// }
	//
	// };
	// }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// getBankcardData();
		// initListViewData();
	}

	@SuppressLint("ShowToast")
	private Response.ErrorListener errorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dialogHelper.stopProgressDialog();
			}
		};
	}

	// private void initListViewData() {
	// lvBankCardAdapter.reset(bankList);
	// lvBankCardAdapter.notifyDataSetChanged();
	// // listview_card = (ListView) findViewById(R.id.listview_card);
	// // lvBankCardAdapter = new LVBankCardAdapter(this, bankList);
	// dialogHelper.stopProgressDialog();
	// lvBankCardAdapter.setDefalut(position_select); //
	// 设置默认选中第一项；//position_select
	// }

	public void back(int position) {
		position_select = position;
		System.out.println("back+ Q!!!!!!!!!!!!!== " + position_select);
		System.out.println("back+ Q!!!!!!!!!!!!!== " + position);
		System.out.println("back+ once == " + bankList.get(position_select).getCard_bank());
		System.out.println("back+ once == " + bankList.get(position_select).getMoney_day_once());
		if (backflag == 1) {// 返回到充值activity
			Intent intent = new Intent();
			intent.putExtra("bankcard", bankList.get(position));
			intent.putExtra("position", position_select);
			setResult(RechargeActivity.resultOk, intent);
			ActivityJumpManager.finishActivity(this, 1);
		} else if (backflag == 2) { // 返回到提现activity
			Intent intent = new Intent();
			intent.putExtra("bankcard", bankList.get(position));
			intent.putExtra("position", position_select);
			setResult(TiXianActivity.resultOk, intent);
			ActivityJumpManager.finishActivity(this, 1);
		} else if (backflag == 3) { // 返回到支付确认框 activity
			Intent intent = new Intent();
			intent.putExtra("bankcard", bankList.get(position));
			intent.putExtra("position", position_select);
			setResult(InvestActivity.resultOk, intent);
			ActivityJumpManager.finishActivity(this, 1);
		} else if (backflag == 4) { // 返回到 我的借款
			Intent intent = new Intent();
			intent.putExtra("bankcard", bankList.get(position));
			intent.putExtra("position", position_select);
			setResult(MyLoanActivity.resultOk, intent);
			ActivityJumpManager.finishActivity(this, 1);
		}

	}

	// private void backT(int position) {
	// Intent intent = new Intent();
	// intent.putExtra("bankcard", bankList.get(position));
	// setResult(2, intent);
	// ActivityJumpManager.finishActivity(this, 1);
	// }

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

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
