package com.sptech.qujj.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.adapter.CommonAdapter;
import com.sptech.qujj.adapter.CommonAdapter.OnGetViewHolderListener;
import com.sptech.qujj.adapter.ViewHolder;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.dialog.DialogPay;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BankcardBean;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.LoanRecord;
import com.sptech.qujj.model.MonthBean;
import com.sptech.qujj.model.RedBag;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.DateManage;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.EventInvestListener;
import com.sptech.qujj.view.NoScrollListView;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 还款中账单类：借你钱首页，借钱总额跳转页
 * 
 * @author LiLin
 * 
 */
public class ReimbursementActivity extends BaseActivity implements
		OnClickTitleListener, OnClickListener {
	private DialogHelper dialogHelper;
	private SharedPreferences spf;
	private CommonAdapter adapter1, adapter2;
	private DialogPay dialogPay;// 弹出支付确认框
	private boolean saveDialog = true;// 点击支付弹出框中的选择银行卡按钮，保存弹出框不消失
	private boolean redflag = false; // 是否有红包
	private float user_money; // 用户账户余额
	private boolean redcheckflag = false;// 是否选择红包支付
	private int is_payment;// 支付方式
	private BankcardBean defaultcard;// 默认的银行卡
	private RedBag curredBag;// 当前红包
	public static int resultOk = 2;
	public static int position_select = 0; // 上次选中的 银行卡
	private List<BankcardBean> curBanklist = new ArrayList<BankcardBean>();//
	NoScrollListView listView1, listView2;
	// ListView listView1, listView2;
	TitleBar titleBar;
	LinearLayout ll_byyh, ll_xydh;
	Button btn_select_all_pay;
	TextView tv_more_reimbursement, tv_tot_money, tv_help_money_t,
			tv_help_money_n, tv_tm, tv_nm, tv_sum;
	Boolean IS_UP = true;
	Boolean IS_BOTTOM = true;
	private CheckBox cb_select_all;
	private boolean flag_select = false;
	private CheckBox cb;
	float tot_money = 0;
	Boolean isCheck = false;

	List<MonthBean> a1 = new ArrayList<MonthBean>();
	List<MonthBean> a2 = new ArrayList<MonthBean>();

	float money_day_total; // 单日累计充值金额
	float money_day_quota;// 单日限额
	float money_day_once; // 单笔限额
	int number_month_quota;// 月交易笔数限制
	int number_month_total;// 月累计交易笔数
	float money_later; // 当日交易剩余额度
	StringBuilder sb = new StringBuilder();

	final int PAYFLAG = 5;

	int payNum;//还款数量

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reimbursement);
		Tools.addActivityList(this);
		initView();
		initListener();
		initData();
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		spf = this.getSharedPreferences(Constant.USER_INFO,
				Context.MODE_PRIVATE);
		// TODO Auto-generated method stub
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		listView1 = (NoScrollListView) findViewById(R.id.listView1);
		listView2 = (NoScrollListView) findViewById(R.id.listView2);
		tv_more_reimbursement = (TextView) findViewById(R.id.tv_more_reimbursement);// 更多记录点击事件
		btn_select_all_pay = (Button) findViewById(R.id.btn_select_all_pay);// 还款按钮
		ll_byyh = (LinearLayout) findViewById(R.id.ll_byyh);// 本月应还点击事件
		ll_xydh = (LinearLayout) findViewById(R.id.ll_xydh);// 下月待还点击事件
		tv_tot_money = (TextView) findViewById(R.id.tv_tot_money);// 应还款总额
		tv_help_money_t = (TextView) findViewById(R.id.tv_help_money_t);// 本月应还
		tv_help_money_n = (TextView) findViewById(R.id.tv_help_money_n);// 下月待还
		tv_tm = (TextView) findViewById(R.id.tv_tm);
		tv_nm = (TextView) findViewById(R.id.tv_nm);
		cb_select_all = (CheckBox) findViewById(R.id.btn_select_all);// 全选
		tv_sum = (TextView) findViewById(R.id.tv_sum);// 合计

	}

	private void initListener() {
		// TODO Auto-generated method stub
		cb_select_all.setOnClickListener(this);
		ll_byyh.setOnClickListener(this);
		ll_xydh.setOnClickListener(this);
		tv_more_reimbursement.setOnClickListener(this);
		btn_select_all_pay.setOnClickListener(this);
		titleBar.setOnClickTitleListener(this);
		titleBar.bindTitleContent("还款中账单", R.drawable.jh_back_selector, 0, "",
				"");
		listView2.setVisibility(View.INVISIBLE);

	}

	private void initData() {
		getBorrowInfo();
	}

	private void tot(List<MonthBean> list1, List<MonthBean> list2) {
		Log.e("shuangpeng", a1.size() + a2.size() + "a1+a2");
		float t = 0;
		for (int i = 0; i < list1.size(); i++) {
			t += Float.parseFloat(list1.get(i).getHelp_money()+"");
		}
		tv_help_money_t.setText(DataFormatUtil.floatsaveTwo(t)+ "");
		//Log.e("shuangpeng", t + "thismonth");
		float n = 0;
		for (int i = 0; i < list2.size(); i++) {
			n += Float.parseFloat(list2.get(i).getHelp_money()+"");
		}
		tv_help_money_n.setText(DataFormatUtil.floatsaveTwo(n) + "");
		//Log.e("shuangpeng", t + "nextmonth");
	}

	private void getBorrowInfo() {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("正在加载数据，请稍候...");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("sign", HttpUtil.getSign(spf.getString(Constant.UID, "")
				.toString(), spf.getString(Constant.KEY, "").toString()));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(
				this, false);
		request.HttpVolleyRequestPost(JsonConfig.BORROW_MONEY_REPAYMENT,
				params, BaseData.class, null, usersuccessListener(),
				errorListener());

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
						tv_tot_money.setText(DataFormatUtil.floatsaveTwo(s.getFloat("money")) + "");// 应还款总额
						// JSONArray a1 = JSON.parseArray(s.getString("month"));
						a1 = JSON.parseArray(s.getString("month"),
								MonthBean.class);
						a2 = JSON.parseArray(s.getString("next_month"),
								MonthBean.class);
						int i = a1.size();
						int j = a2.size();
						Log.e("shuangpeng", a1.size() + a2.size() + "a1+a2");
						tot(a1, a2);
						// total(listView1,listView2,a1,a2);
						tv_tm.setText("本月应还（" + i + "笔）");
						tv_nm.setText("下月待还（" + j + "笔）");
						if (a2 == null || a2.equals("") || a2.size() == 0) {
							ll_xydh.setVisibility(View.INVISIBLE);
						} else {
							ll_xydh.setVisibility(View.VISIBLE);
						}
						adapter1 = new CommonAdapter(
								ReimbursementActivity.this, a1,
								R.layout.akey_reimbursement_item,
								new OnGetViewHolderListener() {

									@Override
									public void setConvertView(
											ViewHolder holder,
											final int position) {
										// TODO Auto-generated method stub
										holder.setText(R.id.lym_time,DateManage.getAddDate(a1.get(position).getTakeeffect_time()+ "",position - 1));
										holder.setText(R.id.tv_borrow_time,"【到期时间: "+DateManage.getAddDate(a1.get(position).getTakeeffect_time(),28)+"】");
										holder.setText(R.id.tv_borrow_sucess, DataFormatUtil.floatsaveTwo(Float.parseFloat(a1.get(position).getHelp_money()+"")));
										holder.setOnClick(R.id.lym_time,
												new OnClickListener() {

													@Override
													public void onClick(
															View arg0) {
														// TODO Auto-generated
														// method stub
														Intent detailIntent1 = new Intent(
																ReimbursementActivity.this,
																BMDetailActivity.class);

														detailIntent1
																.putExtra(
																		"borrow_id",
																		Integer.parseInt(a1
																				.get(position)
																				.getBorrow_id()));
														startActivity(detailIntent1);
													}
												});
										holder.setOnCheck(R.id.btn_select_one,
												new OnCheckedChangeListener() {

													@Override
													public void onCheckedChanged(
															CompoundButton arg0,
															boolean b) {
														// TODO Auto-generated
														// method stub
														if (b) {
															tot_money += Float
																	.parseFloat(a1
																			.get(position)
																			.getHelp_money()+a1.get(position).getHelp_service_fee()+a1.get(position).getHelp_interest()+a1.get(position).getHelp_expect()+"");
															tv_sum.setText("￥"
																	+ DataFormatUtil.floatsaveTwo(tot_money));
															payNum++;

														} else {
															tot_money -= Float
																	.parseFloat(a1
																			.get(position)
																			.getHelp_money()+a1.get(position).getHelp_service_fee()+a1.get(position).getHelp_interest()+a1.get(position).getHelp_expect()+"");
															tv_sum.setText("￥"
																	+ DataFormatUtil.floatsaveTwo(tot_money));
															payNum--;
														}
                                                      if(cb_select_all.isChecked()){
                                                    	  cb_select_all.setChecked(b);
                                                    	  flag_select = b;
														}else{
															if (doSelect1(listView1)) {
																
																if(doSelect1(listView2)){
																	flag_select = true;
																	/*cb_select_all
																	.setChecked(doSelect1(listView1));*/
																	cb_select_all
																	.setChecked(true);
																}else{
																	flag_select = false;
																	cb_select_all
																	.setChecked(false);
																}
																	/*cb_select_all
																	.setChecked(doSelect1(listView1));*/
																	
															} else {
																cb_select_all
																		.setChecked(false);
																flag_select = false;
															}
														}
														
														//
														getNum(payNum);
													}
												});
									}
								});
						listView1.setAdapter(adapter1);
						adapter2 = new CommonAdapter(
								ReimbursementActivity.this, a2,
								R.layout.akey_reimbursement_item,
								new OnGetViewHolderListener() {

									@Override
									public void setConvertView(
											ViewHolder holder,
											final int position) {
										// TODO Auto-generated method stub

										holder.setText(R.id.lym_time,DateManage.getAddDate(a2.get(position).getTakeeffect_time()+ "",position - 1));
										holder.setText(R.id.tv_borrow_time,"【到期时间: "+DateManage.getAddDate(a2.get(position).getTakeeffect_time(),28)+"】");
										holder.setText(R.id.tv_borrow_sucess, DataFormatUtil.floatsaveTwo(Float.parseFloat(a2.get(position).getHelp_money()+"")));
										holder.setOnClick(R.id.lym_time,
												new OnClickListener() {

													@Override
													public void onClick(
															View arg0) {
														// TODO Auto-generated
														// method stub
														Intent detailIntent2 = new Intent(
																ReimbursementActivity.this,
																BMDetailActivity.class);
														detailIntent2
																.putExtra(
																		"borrow_id",
																		Integer.parseInt(a2
																				.get(position)
																				.getBorrow_id()));
														startActivity(detailIntent2);
													}
												});
										holder.setOnCheck(R.id.btn_select_one,
												new OnCheckedChangeListener() {

													@Override
													public void onCheckedChanged(
															CompoundButton arg0,
															boolean b) {
														// TODO Auto-generated
														// method stub
														if (b) {
															tot_money += Float
																	.parseFloat(a2
																			.get(position)
																			.getHelp_money()+a2.get(position).getHelp_service_fee()+a2.get(position).getHelp_interest()+"");
															tv_sum.setText("￥"
																	+ DataFormatUtil.floatsaveTwo(tot_money));
															payNum++;
														} else {
															tot_money -= Float
																	.parseFloat(a2
																			.get(position)
																			.getHelp_money()+a2.get(position).getHelp_service_fee()+a2.get(position).getHelp_interest()+"");
															tv_sum.setText("￥"
																	+ DataFormatUtil.floatsaveTwo(tot_money));
															payNum--;
														}
														if(cb_select_all.isChecked()){
															 cb_select_all.setChecked(b);
	                                                    	  flag_select = b;
														}else{
															if (doSelect1(listView2)) {
																
																if(doSelect1(listView1)){
																	flag_select = true;
																	/*cb_select_all
																	.setChecked(doSelect1(listView1));*/
																	cb_select_all
																	.setChecked(true);
																}else{
																	flag_select = false;
																	cb_select_all
																	.setChecked(false);
																}
																	/*cb_select_all
																	.setChecked(doSelect1(listView1));*/
																	
															} else {
																cb_select_all
																		.setChecked(false);
																flag_select = false;
															}
														}
														
														
														getNum(payNum);
													}
												});

									}
								});
						listView2.setAdapter(adapter2);

						if (dialogHelper != null) {
							dialogHelper.stopProgressDialog();
						}
					}

				} else {
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
				Log.e("shuangpeng", 3 + "");
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_more_reimbursement:
			startActivity(new Intent(this, MyRepaymentDetailActivity.class));
			break;

		case R.id.btn_select_all:

			selectAll();

			break;
		case R.id.btn_select_all_pay:
			deail(a1, listView1, a2, listView2);
			if(payNum == 0){
				ToastManage.showToast("您还没有选择还款项！");
				return;
			}
			getPayInfo(tot_money);
			
			break;
		case R.id.ll_byyh:
			if (IS_UP) {
				ll_byyh.getChildAt(3).setBackgroundResource(
						R.drawable.licai_detailinfo_bottom_select);
				listView1.setVisibility(View.GONE);
				IS_UP = false;
			} else {
				ll_byyh.getChildAt(3).setBackgroundResource(
						R.drawable.licai_detailinfo_top_select);
				listView1.setVisibility(View.VISIBLE);
				IS_UP = true;
			}

			break;
		case R.id.ll_xydh:
			if (IS_BOTTOM) {
				ll_xydh.getChildAt(3).setBackgroundResource(
						R.drawable.licai_detailinfo_top_select);
				listView2.setVisibility(View.VISIBLE);
				IS_BOTTOM = false;
			} else {
				ll_xydh.getChildAt(3).setBackgroundResource(
						R.drawable.licai_detailinfo_bottom_select);
				listView2.setVisibility(View.INVISIBLE);
				IS_BOTTOM = true;
			}

			break;
		default:
			break;
		}
	}

	private void doSelect(NoScrollListView lv) {
		// TODO Auto-generated method stub
		if ( lv.getChildCount() > 0) {
			for (int i = 0; i <  lv.getChildCount(); i++) {
				CheckBox box = (CheckBox) lv.getChildAt(i).findViewById(
						R.id.btn_select_one);
				box.setChecked(cb_select_all.isChecked());
				/*if (cb_select_all.isChecked()) {
					box.setChecked(true);
				} else {
					box.setChecked(false);
				}*/
			}
		} else {
			return;
		}
	}

	/*private boolean doSelect1(NoScrollListView lv, NoScrollListView lv2) {
		// TODO Auto-generated method stub
		if(lv.getCheckedItemCount()>0){
			for (int i = 0; i < lv.getChildCount(); i++) {
				CheckBox box = (CheckBox) lv.getChildAt(i).findViewById(
						R.id.btn_select_one);
				if (!box.isChecked()) {
					return false;
				}
			}
		}
		if (lv2.getCheckedItemCount() > 0) {
			for (int i = 0; i < lv2.getChildCount(); i++) {
				CheckBox box = (CheckBox) lv.getChildAt(i).findViewById(
						R.id.btn_select_one);
				if (!box.isChecked()) {
					return false;
				}
			}
		}
		return true;

	}*/

	private boolean doSelect1(NoScrollListView lv) {
		// TODO Auto-generated method stub
		if(lv.getChildCount()>0&&lv!=null){
			for (int i = 0; i < lv.getChildCount(); i++) {
				CheckBox box = (CheckBox) lv.getChildAt(i).findViewById(
						R.id.btn_select_one);
				if (!box.isChecked()) {
					return false;
				}
			}
		}
		
		return true;
	}

	// 弹出支付框
	public void dialogPay(LoanRecord loanRecord) {
		// curLoanRecord = loanRecord;
		// System.out.println("loanRecord 借款金额== " +
		// curLoanRecord.getLoan_money());
		/*
		 * if (curLoanRecord != null) { getPayInfo(loanRecord.getLoan_money());
		 * }
		 */
		if(payNum == 0){
			ToastManage.showToast("您还没有选择还款项！");
			return;
		}
		getPayInfo(tot_money);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (saveDialog) {
			if (dialogPay != null) {
				dialogPay.closeDialog();
			}

		}
		if (dialogHelper != null) {
			dialogHelper.stopProgressDialog();
		}
		// myLoanlist();
		tot_money = 0;
		tv_sum.setText("￥"+DataFormatUtil.floatsaveTwo(tot_money) );
		payNum = 0;
		getNum(payNum);
		sb.delete(0, sb.length());
		cb_select_all.setChecked(false);
		//selectAll();
		initData();
		

	}

	// 获取支付的信息
	private void getPayInfo(float loanmoney) {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("card_type", 1);
		data.put("is_paytype", 2);// 还款红包flag 2
		data.put("money", tot_money);
		data.put("pay_money", loanmoney);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put(
				"sign",
				HttpUtil.getSign(data, spf.getString(Constant.UID, ""),
						spf.getString(Constant.KEY, "")));
		params.put("data", HttpUtil.getData(data));

		System.out.println("data== " + data);
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(
				ReimbursementActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.UNITE_PAY, params,
				BaseData.class, null, payInfosuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> payInfosuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				// System.out.println("账单列表 返回code == " + response.code);
				System.out.println("支付信息=code " + response.code);
				// dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					// curloanRecords.clear();
					byte[] b = Base64.decode(response.data);
					if (b != null && !"".equals(b)) {
						curBanklist.clear();
						JSONObject s = JSON.parseObject(new String(b));
						System.out.println("支付信息 = " + new String(b));
						user_money = s.getFloat("balance");
						List<BankcardBean> banklist = JSON.parseArray(
								s.getString("card"), BankcardBean.class);
						curBanklist.addAll(banklist);
						// defaultcard = JSON.parseObject(s.getString("card"),
						// BankcardBean.class);
						/*curredBag = JSON.parseObject(s.getString("red"),
								RedBag.class);*/
						// System.out.println("红包金额是 -- " +
						// curredBag.getRb_money());
						// card = s.ge("orderid");
						// showPayDialog();

						// 遍历这个banklist
						for (int i = 0; i < banklist.size(); i++) {
							if (banklist.get(i).getIs_default() == 1) {
								defaultcard = banklist.get(i);
							}
							// System.out.println("默认银行卡是 --" +
							// defaultcard.getCard_bank());
						}
						if (defaultcard == null) {
							defaultcard = banklist.get(0);
						}
						showPayDialog();
						// repayLoan();
					}

				} else {
					ToastManage.showToast(response.desc);
				}
			}
		};
	}

	private void showPayDialog() {
		float redmoney = 0;
		/*
		 * if (curredBag != null) { redflag = true; redmoney =
		 * curredBag.getRb_money(); } buymoney = curLoanRecord.getLoan_money();
		 * // 欠款金额
		 */
		// 弹出选择支付框
		dialogPay = new DialogPay(ReimbursementActivity.this, "还款", redflag,
				redmoney, tot_money, user_money, defaultcard, curBanklist, 0,
				new EventInvestListener() {
					@Override
					public void eventDataHandlerListener(int data,
							boolean checkflag) {
						// TODO Auto-generated method stub
						redcheckflag = checkflag;
						is_payment = data;
						System.out.println("is_payment==" + data);
						System.out.println("redcheckflag==" + redcheckflag);
						// enterPay(orderid);
						// finishPay();
						// repayLoan();
						if (is_payment == 1) {
							money_day_total = defaultcard.getMoney_day_total();// 单日累计充值金额
							money_day_quota = defaultcard.getMoney_day_quota();// 单日限额
							money_day_once = defaultcard.getMoney_day_once();// 单笔限额
							number_month_quota = defaultcard
									.getNumber_month_quota();// 月交易笔数限制
							money_later = money_day_quota - money_day_total;// 当日交易剩余额度
							number_month_total = defaultcard
									.getNumber_month_total();// 单月累计笔数

							System.out.println("money_day_total== "
									+ money_day_total);
							System.out.println("money_day_once== "
									+ money_day_once);
							System.out.println("number_month_quota== "
									+ number_month_quota);
							System.out.println("money_later== " + money_later);
							System.out.println("number_month_total== "
									+ number_month_total);

							// 判断当前银行卡的限额
							// 月交易次数上限
							if ((number_month_total + 1) > number_month_quota
									&& number_month_quota != 0) {
								ToastManage.showToast("该卡月交易次数达到上限");
								return;
							}
							// 单笔限额
							if (tot_money > money_day_once
									&& money_day_once != 0) {
								ToastManage.showToast("该卡单笔交易限额达到上限");
								return;
							}
							// 单日限额上限
							float month_total = tot_money + money_day_total;
							if (month_total > money_day_quota
									&& money_day_quota != 0) {
								ToastManage.showToast("该卡单日交易限额达到上限");
								return;
							}
						}
						Intent intent2 = new Intent(ReimbursementActivity.this,
								EnterPwdActivity.class);
						intent2.putExtra("payflag", PAYFLAG);
						intent2.putExtra("is_paytype", 1);
						intent2.putExtra("is_payment", is_payment);
						intent2.putExtra("money", tot_money);
						intent2.putExtra("borrow_id", sb.toString());
						intent2.putExtra("bankcard_id",
								defaultcard.getBankcard_id());
						/*
						 * if (is_payment == 1) { intent2.putExtra("card_id",
						 * defaultcard.getBankcard_id());
						 * System.out.println("card_id== " +
						 * defaultcard.getBankcard_id()); }
						 */
						/*
						 * if (redflag) { if (redcheckflag) { // 有红包而且选中红包，就传递
						 * // rb_id rb_money if (curredBag != null) {
						 * intent2.putExtra("curredBag", curredBag); } } }
						 */
						startActivity(intent2);
						
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
						dialogPay.closeDialog();
						ReimbursementActivity.this.finish();
					}

					@Override
					public void eventCloseORderListener() {
						// TODO Auto-generated method stub
						if (dialogHelper != null) {
							dialogHelper.stopProgressDialog();
						}
					}

				});
		dialogPay.createMyDialog();
	}

	// 获取选择的银行卡
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 2:
			// TODO
			saveDialog = false;
			if (resultCode == resultOk) {
				defaultcard = (BankcardBean) data
						.getSerializableExtra("bankcard");
				position_select = data.getIntExtra("position", 0);
				System.out.println("传过来没？？？=== " + position_select);
				// 根据name 获取图标流
				// initViewData();
				dialogPay.initBankCard(defaultcard);
				// 把 dialog 再new 一遍
			}
			break;
		default:
			break;
		}
	}

	private void deail(List<MonthBean> list1, NoScrollListView lv1,
			List<MonthBean> list2, NoScrollListView lv2) {
		sb.delete(0, sb.length());
		for (int i = 0; i < lv1.getChildCount(); i++) {
			CheckBox box = (CheckBox) lv1.getChildAt(i).findViewById(
					R.id.btn_select_one);
			if (box.isChecked()) {
				if (sb.equals("") || sb == null) {
					payNum++;
					sb.append(list1.get(i).getBorrow_id());
				} else {
					sb.append("," + list1.get(i).getBorrow_id());
				}
			}
		}
		for (int i = 0; i < lv2.getChildCount(); i++) {
			CheckBox box = (CheckBox) lv2.getChildAt(i).findViewById(
					R.id.btn_select_one);
			if (box.isChecked()) {
				if (sb.toString().equals("") || sb.toString() == null) {
					payNum++;
					sb.append(list2.get(i).getBorrow_id());
				} else {
					sb.append("," + list2.get(i).getBorrow_id());
				}
			}

		}
		// Log.e("shuangpeng", "BORROW_ID:"+BORROW_ID+"sb:"+sb);
	}

	private void getNum(int num) {
		btn_select_all_pay.setText("还款" + "(" + num + ")");
		//flag_select = cb_select_all.isChecked();
	}
	
	private void selectAll() {
		doSelect(listView1);
		doSelect(listView2);

		tv_sum.setText("￥" + DataFormatUtil.floatsaveTwo(tot_money));
		getNum(payNum);
	}

	}

