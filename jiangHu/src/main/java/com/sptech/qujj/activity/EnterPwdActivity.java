package com.sptech.qujj.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.MainActivity;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.RedBag;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Md5;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 
 * 
 * 输入交易密码
 * 
 */
public class EnterPwdActivity extends BaseActivity implements OnClickTitleListener, OnClickListener {

	private TitleBar titleBar;
	private Button btn_finish;

	private int pro_id;
	private int bankcard_id;// 充值id
	private float money;// 充值金额
	private String bindid;// 校验码

	private int payflag = -1; // -1 空 0 理财订单支付，1 充值支付 2 提现支付 3 申请代还款 4 我的借款-点击还款

	private SharedPreferences spf;
	private DialogHelper dialogHelper;
	private EditText ed_pwd;

	// 订单支付 参数
	private RedBag curredBag;// 当前红包
	private int orderid;// 订单id
	private int card_id;// card id
	private ImageView img_clear; // input 清除按钮
	private TextView tv_forgetpwd, tv_miao;// 忘记交易密码

	// 申请还款 参数
	private int repay_id;
	private int repay_backcard_id;
	private int repay_bill_month;
	private float repay_money;

	// 申请代还款成功 失败界面
	private RelativeLayout rela_enter, rale_applysuccess, rale_nonetwork;// 输入密码界面，
	                                                                     // 申请成功，
	private DownTimer downTimer; // 失败
	private Button btn_reset; // 重新加载

	// 我的借款 -- 还款 -参数
	private int id, is_payment,is_paytype;
	private float loanmoney, repayment_profit; // 还款金额, 还款利息
	String borrow_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_licai_enterpwd);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		downTimer = new DownTimer(6 * 1000, 1000);
		if (getIntent() != null) {
			payflag = getIntent().getExtras().getInt("payflag");
		}
		dialogHelper = new DialogHelper(this);
		spf = this.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);

		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("输入交易密码", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		tv_forgetpwd = (TextView) findViewById(R.id.tv_forgetpwd);

		ed_pwd = (EditText) findViewById(R.id.ed_pwd);
		btn_finish = (Button) findViewById(R.id.btn_finish);
		img_clear = (ImageView) findViewById(R.id.img_clear);
		btn_finish.setOnClickListener(this);
		img_clear.setOnClickListener(this);
		tv_forgetpwd.setOnClickListener(this);

		if (payflag == 1 || payflag == 2) {
			bankcard_id = getIntent().getExtras().getInt("bankcard_id");
			money = getIntent().getExtras().getFloat("money");
			bindid = getIntent().getExtras().getString("bindid");
			System.out.println("bankcard_id=  " + bankcard_id);
			System.out.println("money=   " + money);
			System.out.println("bindid=  " + bindid);
		}
		if (payflag == 0) {
			curredBag = (RedBag) getIntent().getSerializableExtra("curredBag");
			orderid = getIntent().getExtras().getInt("orderid");
			card_id = getIntent().getExtras().getInt("card_id");
		}
		if (payflag == 3) {
			repay_id = getIntent().getExtras().getInt("id");
			repay_backcard_id = getIntent().getExtras().getInt("bankcard_id");
			repay_bill_month = getIntent().getExtras().getInt("bill_month");
			repay_money = getIntent().getExtras().getFloat("money");

			System.out.println("repay_id == " + repay_id);
			System.out.println("repay_backcard_id == " + repay_backcard_id);
			System.out.println("repay_bill_month == " + repay_bill_month);
			System.out.println("repay_money == " + repay_money);
		}

		if (payflag == 4) {
			id = getIntent().getExtras().getInt("id");
			is_payment = getIntent().getExtras().getInt("is_payment");
			loanmoney = getIntent().getExtras().getFloat("money");
			repayment_profit = getIntent().getExtras().getFloat("repayment_profit");
			bindid = getIntent().getExtras().getString("bindid");
			card_id = getIntent().getExtras().getInt("card_id");
			curredBag = (RedBag) getIntent().getSerializableExtra("curredBag");

			System.out.println("4 id=  " + id);
			System.out.println("4 bankcard_id=  " + card_id);
			System.out.println("4 money=   " + loanmoney);
			System.out.println("4 is_payment=   " + is_payment);
			System.out.println("4 repayment_profit=  " + repayment_profit);

			if (curredBag != null) {
				System.out.println("4 curredBag=  " + curredBag.getRb_money());
			}

		}
		if (payflag == 5) {
			is_payment = getIntent().getExtras().getInt("is_payment");
			money = getIntent().getExtras().getFloat("money");
			is_paytype = getIntent().getExtras().getInt("is_paytype");
			bankcard_id = getIntent().getExtras().getInt("bankcard_id");
			borrow_id = getIntent().getExtras().getString("borrow_id");
			//curredBag = (RedBag) getIntent().getSerializableExtra("curredBag");

			if (curredBag != null) {
				System.out.println("4 curredBag=  " + curredBag.getRb_money());
			}

		}
		tv_miao = (TextView) findViewById(R.id.tv_miao);

		rela_enter = (RelativeLayout) findViewById(R.id.rela_enter);
		rale_applysuccess = (RelativeLayout) findViewById(R.id.rale_applysuccess);
		rale_nonetwork = (RelativeLayout) findViewById(R.id.rale_nonetwork);

		btn_reset = (Button) findViewById(R.id.btn_reset);// 重新加载
		btn_reset.setOnClickListener(this);
		ed_pwd.addTextChangedListener(textWatcher);

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
		case R.id.btn_finish:
			String pwd = ed_pwd.getText().toString();
			if (pwd.equals("")) {
				ToastManage.showToast("请输入支付密码");
				return;
			}
			if (payflag == 1) { // 充值确认 接口
				UserRechargeConfirm(pwd);
			} else if (payflag == 2) { // 提现
				UserCashoutConfirm(pwd);
			} else if (payflag == 0) {
				orderPay(pwd);
			} else if (payflag == 3) {// 申请代还款
				repayment(pwd);
			} else if (payflag == 4) { // 我的借款 --还款
				finishPay(pwd);
			}else if (payflag == 5) { // 一键还款
				myPay(pwd);
			}

			break;
		case R.id.img_clear:
			ed_pwd.setText("");
			break;
		case R.id.tv_forgetpwd:
			// 跳到设置交易密码页面
			Intent intent = new Intent(EnterPwdActivity.this, SetDealPwdActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			EnterPwdActivity.this.finish();
			break;
		case R.id.btn_reset:
			// 重新加载
			// btn_finish.setOnClickListener();
			String pwd_reset = ed_pwd.getText().toString();
			if (pwd_reset.equals("")) {
				ToastManage.showToast("请输入支付密码");
				return;
			}
			if (payflag == 3) {// 申请代还款
				repayment(pwd_reset);
			}

		default:
			break;
		}
	}

	// 用户充值 -确认
	private void UserRechargeConfirm(String pwd) {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("money", money);
		data.put("bankcard_id", bankcard_id);
		data.put("bindid", bindid);
		data.put("pay_pwd", pwd);
		System.out.println("data== " + data);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		params.put("data", HttpUtil.getData(data));

		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(EnterPwdActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.USER_RECHARGE_CONFIRM, params, BaseData.class, null, rechargeConfirmsuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> rechargeConfirmsuccessListener() {
		return new Listener<BaseData>() {
			@Override
			public void onResponse(BaseData response) {
				// ToastManage.showToast("用户充值  code--" + response.code);
				dialogHelper.stopProgressDialog();
				System.out.println("充值 ==" + response.code);
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					// JSONObject s = JSON.parseObject(new String(b));
					ToastManage.showToast("充值成功");
					// 跳到 账户余额
				} else {
					// 充值失败，回到账户余额
					ToastManage.showToast(response.desc);
				}
				Intent intent = new Intent(EnterPwdActivity.this, AccountBalanceActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				EnterPwdActivity.this.finish();
			}
		};
	}

	// 用户提现 -确认
	private void UserCashoutConfirm(String pwd) {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("money", money);
		data.put("bankcard_id", bankcard_id);
		data.put("bindid", bindid);
		data.put("pay_pwd", pwd);
		System.out.println("data== " + data);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		params.put("data", HttpUtil.getData(data));

		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(EnterPwdActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.USER_CASHOUT_CONFIRM, params, BaseData.class, null, cashoutConfirmsuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> cashoutConfirmsuccessListener() {
		return new Listener<BaseData>() {
			@Override
			public void onResponse(BaseData response) {
				ToastManage.showToast("用户提现 --" + response.code);
				System.out.println("提现 ==" + response.code);
				dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					// byte[] b = Base64.decode(response.data);
					// JSONObject s = JSON.parseObject(new String(b));
					ToastManage.showToast("提交成功");
					// 跳到 账户余额
				} else {
					ToastManage.showToast(response.desc);
				}
				Intent intent = new Intent(EnterPwdActivity.this, AccountBalanceActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				EnterPwdActivity.this.finish();

			}
		};
	}

	// 支付订单---/order_pay
	// 确认支付信息
	private void orderPay(String pwd) {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("orderid", orderid); // 订单id
		data.put("pay_pwd", pwd); // 交易密码
		if (card_id != 0) {
			data.put("card_id", card_id);// 银行卡id
		}
		if (curredBag != null) {
			data.put("rb_id", curredBag.getId()); // 红包id
			data.put("rb_money", curredBag.getRb_money());// 红包金额
		}
		System.out.println("data== " + data);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		params.put("data", HttpUtil.getData(data));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.ORDER_PAY, params, BaseData.class, null, orderPaysuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> orderPaysuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				System.out.println(" ORDER_PAY 返回code == " + response.code);
				dialogHelper.stopProgressDialog();
				byte[] b = Base64.decode(response.data);
				if (response.code.equals("0")) {
					ToastManage.showToast("支付成功!");
					Intent intent = new Intent(EnterPwdActivity.this, LiCaiActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					EnterPwdActivity.this.finish();
				} else if (response.code.equals("9999")) {
					if (b != null && !b.equals("")) {
						System.out.println("desc== " + new String(b));
						JSONObject s = JSON.parseObject(new String(b));
						String desc = "";
						desc = s.getString("retdesc");
						System.out.println("desc== " + desc);
						ToastManage.showToast(desc);
					}

				} else {
					ToastManage.showToast(response.desc);
				}
			}
		};
	}

	// 申请代还款
	private void repayment(String pwd) {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("pay_pwd", pwd);
		data.put("money", repay_money);
		data.put("bankcard_id", repay_backcard_id);
		data.put("id", repay_id);
		data.put("bill_month", repay_bill_month);
		System.out.println("data== " + data);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		params.put("data", HttpUtil.getData(data));

		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(EnterPwdActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.INSERT_APPLY_EPAYMENT, params, BaseData.class, null, repaysuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> repaysuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				System.out.println(" ORDER_PAY 返回code == " + response.code);
				dialogHelper.stopProgressDialog();
				byte[] b = Base64.decode(response.data);
				if (response.code.equals("0")) {
					rela_enter.setVisibility(View.GONE);
					rale_applysuccess.setVisibility(View.VISIBLE);
					downTimer.start();
				} else if (response.code.equals("2")) {
					rela_enter.setVisibility(View.GONE);
					rale_nonetwork.setVisibility(View.VISIBLE);
				} else {
					ToastManage.showToast(response.desc);
				}
			}
		};
	}

	// 我的借款--点还款-- 完成还款支付
	private void finishPay(String pwd) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("pay_pwd", pwd);
		data.put("id", id);
		data.put("is_payment", is_payment);
		data.put("money", loanmoney);
		data.put("repayment_profit", repayment_profit);
		data.put("bindid", bindid);

		if (card_id != 0) {
			data.put("card_id", card_id);// 银行卡id
		}
		if (curredBag != null) {
			data.put("rb_id", curredBag.getId()); // 红包id
			data.put("rb_money", curredBag.getRb_money());// 红包金额
		}

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		params.put("data", HttpUtil.getData(data));

		System.out.println("finishPay--- data== " + data);
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(EnterPwdActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.FINISH_PAY, params, BaseData.class, null, finishPaysuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> finishPaysuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				// System.out.println("账单列表 返回code == " + response.code);
				System.out.println("完成还款 =code " + response.code);
				dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					// curloanRecords.clear();
					ToastManage.showToast("还款提交成功！");
					// 跳到我的借款
					Intent intent = new Intent(EnterPwdActivity.this, MyLoanActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					EnterPwdActivity.this.finish();
				} else {
					ToastManage.showToast(response.desc);
				}
			}
		};
	}
	
	// 确认支付信息
		private void myPay(String pwd) {
			//Md5.md5s(pwd);
			dialogHelper.startProgressDialog();
			dialogHelper.setDialogMessage("请稍候...");
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("is_paytype", is_paytype); // 支付类型：0充值 1还款(借你钱) 2还款(帮你还) 3投资
			data.put("is_payment", is_payment);//支付方式：0余额支付 1银行卡支付..
			data.put("money", money); // 支付金额，单位：元
			data.put("pay_pwd", Md5.md5s(pwd)); // 交易密码
			HashMap<String, Object> paydata = new HashMap<String, Object>();
			paydata.put("borrow_id", borrow_id);
			data.put("paydata", paydata); // 支付金额，单位：元
			if (is_payment == 1) {
				data.put("bankcard_id", bankcard_id);// 银行卡id
			}
			/*if (curredBag != null) {
				data.put("rb_id", curredBag.getId()); // 红包id
				data.put("rb_money", curredBag.getRb_money());// 红包金额
			}*/
			System.out.println("data== " + data);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("uid", spf.getString(Constant.UID, "").toString());
			params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
			params.put("data", HttpUtil.getData(data));
			@SuppressWarnings("rawtypes")
			HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
			request.HttpVolleyRequestPost(JsonConfig.SINGLE_PAY, params, BaseData.class, null, myPaysuccessListener(), errorListener());
		}

		@SuppressWarnings("rawtypes")
		private Listener<BaseData> myPaysuccessListener() {
			return new Listener<BaseData>() {

				@Override
				public void onResponse(BaseData response) {
					dialogHelper.stopProgressDialog();
					byte[] b = Base64.decode(response.data);
					if (response.code.equals("0")) {
						ToastManage.showToast("支付成功!");
						Intent intent = new Intent(EnterPwdActivity.this, ReimbursementActivity.class);
						startActivity(intent);
						overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
						EnterPwdActivity.this.finish();
					} else if (response.code.equals("9999")) {
						if (b != null && !b.equals("")) {
							System.out.println("desc== " + new String(b));
							JSONObject s = JSON.parseObject(new String(b));
							String desc = "";
							desc = s.getString("retdesc");
							System.out.println("desc== " + desc);
							ToastManage.showToast(desc);
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

	class DownTimer extends CountDownTimer {

		public DownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// 倒计时开始
			long m = millisUntilFinished / 1000;
			String ss = String.valueOf(m % 3600 % 60);
			tv_miao.setText(ss + "秒后回到首页");
		}

		@Override
		public void onFinish() {
			ActivityJumpManager.jumpToMain(EnterPwdActivity.this, MainActivity.class);
			downTimer.cancel();
		}
	};
}
