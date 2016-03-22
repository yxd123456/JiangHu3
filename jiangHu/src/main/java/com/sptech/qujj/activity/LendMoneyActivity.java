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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.sptech.qujj.MainActivity;
import com.sptech.qujj.PersonalInformationActivity;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.BorrowMoneyBean;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 借你钱首页
 * 
 * @author LiLin
 * 
 */
public class LendMoneyActivity extends BaseActivity implements
		OnClickTitleListener, OnClickListener {
	TitleBar titleBar;
	Button apply_start;
	TextView tv_allow_totmon, tv_one_allow_totmon, tv_add_num, tv_add_num_op,
			tv_tot_borrowed, tv_mon_borrow;
	LinearLayout ll_tot_borrow, ll_tot_mon;

	private DialogHelper dialogHelper;
	private SharedPreferences spf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lendmoney);
		Tools.addActivityList(this);
		initView();
		initListener();
		initData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		dialogHelper = new DialogHelper(this);
		spf = this.getSharedPreferences(Constant.USER_INFO,
				Context.MODE_PRIVATE);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		apply_start = (Button) findViewById(R.id.btn_apply_start);// 开始申请按钮
		tv_allow_totmon = (TextView) findViewById(R.id.tv_allow_totmon);// 可申请总额度
		tv_one_allow_totmon = (TextView) findViewById(R.id.tv_one_allow_totmon);// 单卡最高可申请额度
		tv_add_num = (TextView) findViewById(R.id.tv_add_num);// 可申请次数
		tv_add_num_op = (TextView) findViewById(R.id.tv_add_num_op);// 提升次数
		tv_tot_borrowed = (TextView) findViewById(R.id.tv_tot_borrowed);// 还款中总额
		tv_mon_borrow = (TextView) findViewById(R.id.tv_mon_borrow);// 本月借款
		ll_tot_mon = (LinearLayout) findViewById(R.id.ll_tot_mon);// 本月借款跳转
		ll_tot_borrow = (LinearLayout) findViewById(R.id.ll_tot_borrow);// 还款中总额跳转
	}

	private void initListener() {
		// TODO Auto-generated method stub
		titleBar.setOnClickTitleListener(this);
		apply_start.setOnClickListener(this);
		tv_add_num_op.setOnClickListener(this);
		ll_tot_borrow.setOnClickListener(this);
		ll_tot_mon.setOnClickListener(this);
	}

	private void initData() {
		// TODO Auto-generated method stub
		titleBar.bindTitleContent("借你钱", R.drawable.jh_back_selector, 0, "",
				"帮助中心");
		if(Constant.FLAG_SHIMINGRENZHENGZHONG){
			tv_add_num_op.setVisibility(View.GONE);
		}else{
			tv_add_num_op.setVisibility(View.VISIBLE);
		}
		 getBorrowInfo();
	}

	@Override
	public void onLeftButtonClick(View view) {
		// TODO Auto-generated method stub
		ActivityJumpManager.finishActivity(this, 1);
	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, WebViewActivity.class);
		intent.putExtra("url", JsonConfig.HTML + "/index/service");
		intent.putExtra("title", "帮助中心");
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	private void getBorrowInfo() {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("正在加载数据，请稍候...");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("sign", HttpUtil.getSign(spf.getString(Constant.UID, "")
				.toString(), spf.getString(Constant.KEY, "").toString()));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(
				this, false);
		request.HttpVolleyRequestPost(JsonConfig.BORROW_MONEY_INDEX, params,
				BaseData.class, null, usersuccessListener(), errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> usersuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {
						JSONObject s = JSON.parseObject(new String(b));
						tv_allow_totmon.setText(DataFormatUtil.floatsaveTwo(s.getFloat("apply_money_total"))+"");//申请总额
						tv_tot_borrowed.setText(DataFormatUtil.floatsaveTwo(s.getFloat("reapyment_money_total"))+"");//还款中总额
						tv_mon_borrow.setText(DataFormatUtil.floatsaveTwo(s.getFloat("reapyment_money_month"))+"");//本月还款

						if (dialogHelper != null) {
							dialogHelper.stopProgressDialog();
						}
					}

				} else {
					// dialogHelper.stopProgressDialog();
					ToastManage.showToast(response.desc);
				}
			}

		};
	}

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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		// 开始申请
		case R.id.btn_apply_start:
			Intent intent2 = new Intent(this, ApplyToLoadActivity.class);
			startActivity(intent2);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		// 提升次数
		case R.id.tv_add_num_op:
            Intent addIntent = new Intent(this,ComInfoActivity.class);
            startActivity(addIntent);
			break;
		// 还款中总额
		case R.id.ll_tot_borrow:
			Intent intent3 = new Intent(this, ReimbursementActivity.class); // ReimbursementActivity
			startActivity(intent3);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		// 本月借款
		case R.id.ll_tot_mon:
			Intent intent4 = new Intent(this, ThisMonthLoanActivity.class);
			startActivity(intent4);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getBorrowInfo();
	}

}
