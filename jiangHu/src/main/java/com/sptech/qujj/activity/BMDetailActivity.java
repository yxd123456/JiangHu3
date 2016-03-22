package com.sptech.qujj.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
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
import com.sptech.qujj.util.DateManage;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 借款详情页
 * 
 * @author LiLin
 * 
 */
public class BMDetailActivity extends BaseActivity implements
		OnClickTitleListener {
	TitleBar titleBar;
	TextView tv_borrow_id, tv_title, tv_addtime, tv_help_money,
			frozen_card_no, tv_card_no, tv_takeeffect_time,
			tv_takeeffect_time_add;
	private DialogHelper dialogHelper;
	private SharedPreferences spf;
	private int BORROW_ID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_borrow_money_detail);
		initView();
		Tools.addActivityList(this);
		initData();

	}

	private void initView() {
		// TODO Auto-generated method stub
		dialogHelper = new DialogHelper(this);
		spf = this.getSharedPreferences(Constant.USER_INFO,
				Context.MODE_PRIVATE);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		//tv_detail = (TextView) findViewById(R.id.tv_detail);// 借款描述
		tv_title = (TextView) findViewById(R.id.tv_bm_title);// 交易类型
		tv_addtime = (TextView) findViewById(R.id.tv_addtime);// 申请时间
		tv_help_money = (TextView) findViewById(R.id.tv_help_money);// 交易金额
		frozen_card_no = (TextView) findViewById(R.id.frozen_card_no);// 申请卡
		tv_card_no = (TextView) findViewById(R.id.tv_card_no);// 接收卡
		tv_takeeffect_time = (TextView) findViewById(R.id.tv_takeeffect_time);// 放款时间
		tv_takeeffect_time_add = (TextView) findViewById(R.id.tv_takeeffect_time_add);// 到期日期
		tv_borrow_id = (TextView) findViewById(R.id.tv_borrow_id);
		BORROW_ID = getIntent().getIntExtra("borrow_id",0);
	}

	private void initData() {
		// TODO Auto-generated method stub
		titleBar.setOnClickTitleListener(this);
		titleBar.bindTitleContent("借你钱借贷详情", R.drawable.jh_back_selector, 0,
				"", "");
		
		Log.e("shuangpeng", BORROW_ID+"BORROW_ID");
		myLoanlist();
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

	private void myLoanlist() {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍后...");
		HashMap<String, Object> data = new HashMap<String, Object>();
		// 参数设置
		data.put("borrow_id", BORROW_ID);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data,spf.getString(Constant.UID, "")
				.toString(), spf.getString(Constant.KEY, "").toString()));
		
//REPAYMENT_DETAIL   BORROW_MONEY_DETAIL
		System.out.println("data== " + data);
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(
				this, false);
		request.HttpVolleyRequestPost(JsonConfig.BORROW_MONEY_DETAIL, params,
				BaseData.class, null, loanlistsuccessListener(),
				errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> loanlistsuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					Log.e("shuangpeng", b+"bbbbbb");
					if (b != null && !"".equals(b)) {
						Log.e("shuangpeng", b+"");
						BorrowMoneyBean loanData = JSON.parseObject(new String(
								b), BorrowMoneyBean.class);
						/*tv_detail.setText(loanData.getCard_bank() + "("
								+ DataFormatUtil.bankcardsaveFour(loanData.getFrozen_card_no()) + ")" + "-"
								+ loanData.getTitle());*/
						Log.e("shuangpeng",loanData.getTitle());
						JSONObject obj = JSON.parseObject(new String(b));
						tv_title.setText(obj.getString("title"));
						tv_borrow_id.setText(loanData.getLoan_order_id()+"");
						//tv_title.setText(loanData.getTitle());
						tv_addtime.setText(DateManage.StringToDateymd(loanData.getAddtime()+""));
						tv_help_money.setText(DataFormatUtil.floatsaveTwo(loanData.getHelp_money())+ "元");
						frozen_card_no.setText(loanData.getCard_bank() + "  "
								+ DataFormatUtil.bankcardsaveFour(loanData.getFrozen_card_no()));
						tv_card_no.setText(loanData.getCard_bank() + "  "
								+ DataFormatUtil.bankcardsaveFour(loanData.getCard_no()));
						tv_takeeffect_time.setText(DateManage.StringToDateymdhms(loanData
								.getTakeeffect_time()+""));
						
						tv_takeeffect_time_add.setText(DateManage.getAddDate(loanData
								.getTakeeffect_time()+"",28));
						
						//JSONObject obj = JSON.parseObject(new String(b));
						/*tv_detail.setText(obj.getString("detail")+ "("
								+ obj.getString("frozen_card_no") + ")" + "-"
								+ obj.getString("title"));
						tv_borrow_id.setText(BORROW_ID+"");
						tv_title.setText(obj.getShort("title"));
						tv_addtime.setText(obj.getIntValue("addtime"));
						tv_help_money.setText(obj.getFloat("help_money") + "元");
						frozen_card_no.setText(obj.getString("card_no") + "  "
								+ obj.getString("card_bank"));
						tv_card_no.setText(obj.getString("card_bank") + "  "
								+ obj.getString("card_no"));
						tv_takeeffect_time.setText(obj
								.getIntValue("takeeffect_time")+"");
						tv_takeeffect_time_add.setText(obj
								.getIntValue("takeeffect_time") + "add");*/
						dialogHelper.stopProgressDialog();
					}
					
				} else {
					ToastManage.showToast(response.desc);
				}
			}
		};
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// myLoanlist();
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
