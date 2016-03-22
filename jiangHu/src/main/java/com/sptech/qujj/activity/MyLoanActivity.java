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
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.adapter.LVMyLoanListAdapter;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.dialog.DialogPay;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BankcardBean;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.LoanRecord;
import com.sptech.qujj.model.RedBag;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.EventInvestListener;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 我的借款
 * 
 * @author yebr
 * 
 */
public class MyLoanActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {
	private DialogHelper dialogHelper;
	private SharedPreferences spf;

	HashMap<String, String> cardMap = new HashMap<String, String>();
	List<LoanRecord> curloanRecords = new ArrayList<LoanRecord>();

	private TitleBar titleBar;
	private ListView lv_loanrecord;
	private LVMyLoanListAdapter loanRecordListAdapter;

	private RelativeLayout rela_nodata;
	private DialogPay dialogPay;// 弹出支付确认框
	public static int resultOk = 2;
	public static int position_select = 0; // 上次选中的 银行卡
	private List<BankcardBean> curBanklist = new ArrayList<BankcardBean>();//

	private float buymoney;// 用户输入的购买金额
	private float buy_money;// 每份金额

	private float user_money; // 用户账户余额
	private BankcardBean defaultcard;// 默认的银行卡
	private RedBag curredBag;// 当前红包

	private boolean saveDialog = true;// 点击支付弹出框中的选择银行卡按钮，保存弹出框不消失

	LoanRecord curLoanRecord = new LoanRecord();

	String bindid = "";// 还款校验码

	private boolean redflag = false; // 是否有红包
	private boolean redcheckflag = false;// 是否选择红包支付
	private int is_payment;// 支付方式

	float money_day_total; // 单日累计充值金额
	float money_day_quota;// 单日限额
	float money_day_once; // 单笔限额
	int number_month_quota;// 月交易笔数限制
	int number_month_total;// 月累计交易笔数
	float money_later; // 当日交易剩余额度

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_slidebar_myloan);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		spf = this.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		cardMap = DataFormatUtil.getBlueCardMap(spf);
		rela_nodata = (RelativeLayout) findViewById(R.id.rela_nodata);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("我的借款", R.drawable.jh_back_selector, R.drawable.jh_myloanrecord_selector, "", "");
		titleBar.setOnClickTitleListener(this);
		lv_loanrecord = (ListView) findViewById(R.id.lv_loanrecord);
		loanRecordListAdapter = new LVMyLoanListAdapter(MyLoanActivity.this, cardMap, curloanRecords);
		lv_loanrecord.setAdapter(loanRecordListAdapter);
		// myLoanlist();
	}

	// 借款申请记录
	private void myLoanlist() {
		HashMap<String, Object> data = new HashMap<String, Object>();
		// data.put("is_status", "5,6,7,8");
		data.put("is_status", "5");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		params.put("data", HttpUtil.getData(data));

		System.out.println("data== " + data);
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(MyLoanActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.MY_LOAN_LIST, params, BaseData.class, null, loanlistsuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> loanlistsuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				// System.out.println("账单列表 返回code == " + response.code);
				System.out.println("借款记录=code " + response.code);
				if (response.code.equals("0")) {
					curloanRecords.clear();
					byte[] b = Base64.decode(response.data);
					if (b != null && !"".equals(b)) {
						System.out.println("借款记录= " + new String(b));
						List<LoanRecord> loanRecords = new ArrayList<LoanRecord>();
						loanRecords = JSON.parseArray(new String(b), LoanRecord.class);
						if (loanRecords.size() != 0) {
							curloanRecords.addAll(loanRecords);
							loanRecordListAdapter.reset(curloanRecords);
							loanRecordListAdapter.notifyDataSetChanged();
						} else {
							rela_nodata.setVisibility(View.VISIBLE);
						}
						lv_loanrecord.setVisibility(View.VISIBLE);
					} else {
						lv_loanrecord.setVisibility(View.GONE);
						rela_nodata.setVisibility(View.VISIBLE);
					}

				} else {
					ToastManage.showToast(response.desc);
				}
			}
		};
	}

	// 弹出支付框
	public void dialogPay(LoanRecord loanRecord) {
		curLoanRecord = loanRecord;
		// System.out.println("loanRecord 借款金额== " +
		// curLoanRecord.getLoan_money());
		if (curLoanRecord != null) {
			getPayInfo(loanRecord.getLoan_money());
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (saveDialog) {
			if (dialogPay != null) {
				dialogPay.closeDialog();
			}
			myLoanlist();
		}
	}

	// 获取支付的信息
	private void getPayInfo(float loanmoney) {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("card_type", 1);
		data.put("is_paytype", 2);// 还款红包flag 2
		// data.put("money", buy_money);
		data.put("pay_money", loanmoney);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		params.put("data", HttpUtil.getData(data));

		System.out.println("data== " + data);
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(MyLoanActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.UNITE_PAY, params, BaseData.class, null, payInfosuccessListener(), errorListener());
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
						List<BankcardBean> banklist = JSON.parseArray(s.getString("card"), BankcardBean.class);
						curBanklist.addAll(banklist);
						// defaultcard = JSON.parseObject(s.getString("card"),
						// BankcardBean.class);
						curredBag = JSON.parseObject(s.getString("red"), RedBag.class);
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
						repayLoan();
					}

				} else {
					ToastManage.showToast(response.desc);
				}
			}
		};
	}

	// 确认还款
	private void repayLoan() {
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("id", curLoanRecord.getId());
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		params.put("data", HttpUtil.getData(data));

		System.out.println("repayLoan-- data== " + data);
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(MyLoanActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.REPAY_LOAN, params, BaseData.class, null, repayloansuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> repayloansuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				// System.out.println("账单列表 返回code == " + response.code);
				System.out.println("确认付款 =code " + response.code);
				dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					// curloanRecords.clear();
					byte[] b = Base64.decode(response.data);
					if (b != null && !"".equals(b)) {
						JSONObject s = JSON.parseObject(new String(b));
						System.out.println("确认付款 = " + new String(b));
						bindid = s.getString("bindid");
						System.out.println("确认付款 bingid == " + bindid);
						showPayDialog();
					}
				} else {
					ToastManage.showToast(response.desc);
				}
			}
		};
	}

	private void showPayDialog() {
		float redmoney = 0;
		if (curredBag != null) {
			redflag = true;
			redmoney = curredBag.getRb_money();
		}
		buymoney = curLoanRecord.getLoan_money(); // 欠款金额

		// 弹出选择支付框
		dialogPay = new DialogPay(MyLoanActivity.this, "平台还款", redflag, redmoney, buymoney, user_money, defaultcard, curBanklist, 0, new EventInvestListener() {
			@Override
			public void eventDataHandlerListener(int data, boolean checkflag) {
				// TODO Auto-generated method stub
				redcheckflag = checkflag;
				is_payment = data;
				System.out.println("is_payment==" + data);
				System.out.println("bindid==" + bindid);
				System.out.println("redcheckflag==" + redcheckflag);
				// enterPay(orderid);
				// finishPay();
				// repayLoan();
				if (is_payment == 1) {
					money_day_total = defaultcard.getMoney_day_total();// 单日累计充值金额
					money_day_quota = defaultcard.getMoney_day_quota();// 单日限额
					money_day_once = defaultcard.getMoney_day_once();// 单笔限额
					number_month_quota = defaultcard.getNumber_month_quota();// 月交易笔数限制
					money_later = money_day_quota - money_day_total;// 当日交易剩余额度
					number_month_total = defaultcard.getNumber_month_total();// 单月累计笔数

					System.out.println("money_day_total== " + money_day_total);
					System.out.println("money_day_once== " + money_day_once);
					System.out.println("number_month_quota== " + number_month_quota);
					System.out.println("money_later== " + money_later);
					System.out.println("number_month_total== " + number_month_total);

					// 判断当前银行卡的限额
					// 月交易次数上限
					if ((number_month_total + 1) > number_month_quota && number_month_quota != 0) {
						ToastManage.showToast("该卡月交易次数达到上限");
						return;
					}
					// 单笔限额
					if (buymoney > money_day_once && money_day_once != 0) {
						ToastManage.showToast("该卡单笔交易限额达到上限");
						return;
					}
					// 单日限额上限
					float month_total = buymoney + money_day_total;
					if (month_total > money_day_quota && money_day_quota != 0) {
						ToastManage.showToast("该卡单日交易限额达到上限");
						return;
					}
				}
				Intent intent2 = new Intent(MyLoanActivity.this, EnterPwdActivity.class);
				intent2.putExtra("payflag", 4);
				intent2.putExtra("bindid", bindid);
				intent2.putExtra("id", curLoanRecord.getId());
				intent2.putExtra("money", curLoanRecord.getLoan_money());
				intent2.putExtra("repayment_profit", DataFormatUtil.floatsaveTwo(curLoanRecord.getLoan_money() - curLoanRecord.getHelp_money()));

				intent2.putExtra("is_payment", is_payment);
				if (is_payment == 1) {
					intent2.putExtra("card_id", defaultcard.getBankcard_id());
					System.out.println("card_id== " + defaultcard.getBankcard_id());
				}
				if (redflag) {
					if (redcheckflag) {
						// 有红包而且选中红包，就传递 // rb_id rb_money
						if (curredBag != null) {
							intent2.putExtra("curredBag", curredBag);
						}
					}
				}
				startActivity(intent2);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				dialogPay.closeDialog();
			}

			@Override
			public void eventCloseORderListener() {
				// TODO Auto-generated method stub
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
				defaultcard = (BankcardBean) data.getSerializableExtra("bankcard");
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
		ActivityJumpManager.finishActivity(MyLoanActivity.this, 1);
	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub
		Intent bindaredit = new Intent(MyLoanActivity.this, MyRepaymentDetailActivity.class);
		startActivity(bindaredit);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		// 无还款明细
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

}
