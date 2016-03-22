package com.sptech.qujj.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.dialog.DialogPay;
import com.sptech.qujj.dialog.DialogSetPwd;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BankcardBean;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.Product;
import com.sptech.qujj.model.RedBag;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.DateManage;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.EventInvestListener;
import com.sptech.qujj.view.SpringProgressView;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 理财中心--投资
 * 
 * @author yebr
 * 
 */

public class InvestActivity extends BaseActivity implements OnClickTitleListener, OnClickListener {

	private TitleBar titleBar;
	private SpringProgressView springProgressView; // 水平进度条

	private Button btn_buy; // 立即购买
	private DialogPay dialogPay;// 弹出支付确认框

	private SharedPreferences spf;
	private Product curProduct;
	private TextView tv_proname, tv_yearvalue, tv_datevalue, tv_datetype, tv_persent, tv_benzhu, tv_benzhumoney, tv_shengyu; // 剩余金额
	private EditText ed_money;
	private DialogHelper dialogHelper;
	private BankcardBean defaultcard;// 默认的银行卡
	private RedBag curredBag;// 当前红包

	private int is_payment;// 支付方式
	// pay接口 传的 参数
	public static int resultOk = 2;
	private int pro_id;
	private int number_has; // 购买份数
	private int limit;
	private int limit_min;
	private float buy_money;
	private float interest;

	private List<BankcardBean> curBanklist = new ArrayList<BankcardBean>();// 用户的英银行卡列表
	public static int position_select = 0; // 上次选中的 银行卡
	private boolean redflag = false; // 是否有红包
	private boolean redcheckflag = false;// 是否选择红包支付
	private float redmoney; // 红包 金额

	private float buyminmoney;// 项目每份金额
	private float buymoney;// 用户输入的购买金额
	private float user_money; // 用户账户余额
	private DialogSetPwd dsp;
	private ImageView img_clear; // input 清除按钮
	public static Boolean closeFlag = false;
	private boolean formUserassets = false;// 从理财资产进来

	private float yearvalue;
	private int dateType;
	private int date;

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
		setContentView(R.layout.jh_licai_invest);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		if (getIntent() != null) {
			curProduct = (Product) getIntent().getSerializableExtra("product");
			formUserassets = (boolean) getIntent().getBooleanExtra("formflag", false);
			System.out.println("curProduct" + curProduct.getSubject());
			System.out.println("formUserassets" + formUserassets);
			System.out.println("formUserassets  targetid=----  " + curProduct.getTarget_id());
		}
		dialogHelper = new DialogHelper(this);
		spf = this.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);

		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("投资", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);

		tv_proname = (TextView) findViewById(R.id.tv_proname);
		tv_yearvalue = (TextView) findViewById(R.id.tv_yearvalue);
		tv_datevalue = (TextView) findViewById(R.id.tv_datevalue);
		tv_datetype = (TextView) findViewById(R.id.tv_datetype);
		tv_persent = (TextView) findViewById(R.id.tv_persent);
		tv_benzhu = (TextView) findViewById(R.id.tv_benzhu);
		tv_benzhumoney = (TextView) findViewById(R.id.tv_benzhumoney);

		tv_shengyu = (TextView) findViewById(R.id.tv_shengyu);
		ed_money = (EditText) findViewById(R.id.ed_money);
		img_clear = (ImageView) findViewById(R.id.img_clear);

		springProgressView = (SpringProgressView) findViewById(R.id.spring_progress_view);

		btn_buy = (Button) findViewById(R.id.btn_buy);
		btn_buy.setOnClickListener(this);
		img_clear.setOnClickListener(this);
		// initProInfoData();
		if (curProduct != null) {
			initViewData();
		}
		ed_money.addTextChangedListener(textWatcher);
	}

	TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence text, int start, int before, int count) {
			// TODO Auto-generated method stub
			if (start == 0) {
				tv_benzhumoney.setText("预期收益金额: 0.00 元");
			} else {
				float gusuan;
				String text1 = text + "";
				float f = Float.parseFloat(text1);

				if (dateType == 1) { // 天
					gusuan = ((float) f * yearvalue * ((float) date / 360) / 100);
					tv_benzhumoney.setText("预期收益金额: " + DataFormatUtil.floatsaveTwo(gusuan) + "元");
				} else if (dateType == 2) { // 月
					gusuan = (float) (f * yearvalue * ((float) date / 12) / 100);
					tv_benzhumoney.setText("预期收益金额: " + DataFormatUtil.floatsaveTwo(gusuan) + "元");
				} else if (dateType == 3) { // 年
					gusuan = (float) (f * yearvalue * ((float) date / 100));
					tv_benzhumoney.setText("预期收益金额: " + DataFormatUtil.floatsaveTwo(gusuan) + "元");
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			if (ed_money.getText().toString() != null && !ed_money.getText().toString().equals("")) {
				img_clear.setVisibility(View.VISIBLE);
			} else {
				img_clear.setVisibility(View.INVISIBLE);
			}
		}
	};

	// 根据数据赋值
	public void initViewData() {

		System.out.println("curProdut getNumber= " + curProduct.getNumber());
		System.out.println("curProdut getNumber_has== " + curProduct.getNumber_has());
		System.out.println("curProdut  getBuy_money_min== " + curProduct.getBuy_money_min());
		float later = curProduct.getBuy_money_min() * (curProduct.getNumber() - curProduct.getNumber_has());
		tv_shengyu.setText("剩余金额: " + later + "元");

		tv_proname.setText(curProduct.getSubject());
		float xx = curProduct.getInterest();
		int yy = (int) xx;
		if (0 == (xx - (float) yy)) {
			tv_yearvalue.setText(yy + "%");
		} else {
			tv_yearvalue.setText(xx + "%");
		}
		// ed_money.setHint(curProduct.getBuy_money_min() + "元起投");
		float min = curProduct.getBuy_money_min();
		int kk = (int) min;
		if (0 == (min - (float) kk)) {
			ed_money.setHint(kk + "元起投" + "(" + kk + "元的整数倍)");
		} else {
			ed_money.setHint(min + "元起投" + "(" + kk + "元的整数倍)");
		}

		int hasBuy = 0;
		if (curProduct.getNumber() != 0) {
			int has = curProduct.getNumber_has();
			int count = curProduct.getNumber();
			hasBuy = (int) Math.round(((double) has / count * 100));
		}

		// 预期收益时间计算
		Date now = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(now);
		// calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动
		// date=calendar.getTime();

		int limit = curProduct.getLimit();
		int type = limit / 100000;
		int limitdate = limit % 100000;
		yearvalue = curProduct.getInterest();
		date = limitdate;
		System.out.println("type= " + type + "date= " + limitdate);
		calendar.add(calendar.DATE, 1);
		if (type == 1) {
			tv_datevalue.setText(limitdate + "");
			tv_datetype.setText("天");
			dateType = 1;
			calendar.add(calendar.DATE, limitdate);// 把日期往后增加一天.整数往后推,负数往前移动
			now = calendar.getTime();
		} else if (type == 2) {
			tv_datevalue.setText(limitdate + "");
			tv_datetype.setText("个月");
			dateType = 2;
			calendar.add(calendar.MONTH, limitdate);// 把日期往后增加一天.整数往后推,负数往前移动
			now = calendar.getTime();
		} else if (type == 3) {
			dateType = 3;
			tv_datevalue.setText(limitdate + "");
			tv_datetype.setText("年");
			calendar.add(calendar.YEAR, limitdate);// 把日期往后增加一天.整数往后推,负数往前移动
			now = calendar.getTime();
		}

		tv_benzhu.setText("预期收益时间: " + DateManage.getYearMonthDay((now.getTime() / 1000 + "")));

		springProgressView.setMaxCount(100);
		springProgressView.setCurrentCount(hasBuy);
		tv_persent.setText(hasBuy + "%");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_clear:
			ed_money.setText("");
			break;
		case R.id.btn_buy:
			// // 先判断是否有实名认证
			// Intent intent3;
			// 已认证
			// if (spf.getString(Constant.USER_AUTH, "").equals("1")) {
			String buymoneysString = ed_money.getText().toString();
			if (buymoneysString.equals("")) {
				ToastManage.showToast("投资金额不能为空");
				return;
			}
			float d = Float.parseFloat(buymoneysString);
			buymoney = d;
			float min_money = curProduct.getBuy_money_min();
			float yu = d % min_money;
			System.out.println("余数 ==" + yu);
			if (yu != 0.0) {
				ToastManage.showToast("投资金额需为" + min_money + "元的倍数");
				return;
			} else {
				double number = d / min_money;
				double j = number * 100;
				number_has = (int) Math.round(j) / 100;
				System.out.println("number_has ==" + number_has);
				if (curProduct != null) {
					buy_money = min_money;
					interest = curProduct.getInterest();
					limit = curProduct.getLimit();
					limit_min = curProduct.getLimit_min();
					System.out.println("limit --" + limit);
					System.out.println("limit_min --" + limit_min);
					// 统一支付接口
					getPayInfo(buymoney);
				}
			}

			break;
		default:
			break;
		}
	}

	// 获取支付的信息
	private void getPayInfo(float buymoney) {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("card_type", 1);
		data.put("is_paytype", 3);// 理财红包flag 3
		data.put("pay_money", buymoney);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		params.put("data", HttpUtil.getData(data));

		System.out.println("data== " + data);
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(InvestActivity.this, false);
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
						curredBag = JSON.parseObject(s.getString("red"), RedBag.class); // 红包
						// System.out.println("红包金额是 -- " +
						// curredBag.getRb_money());
						// card = s.ge("orderid");
						// showPayDialog();

						// 遍历这个banklist
						for (int i = 0; i < banklist.size(); i++) {
							if (banklist.get(i).getIs_default() == 1) {
								defaultcard = banklist.get(i);
							}
						}
						if (defaultcard == null) {
							defaultcard = banklist.get(0);
						}
						// repayLoan();
						pay();
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
		if (dialogPay != null && closeFlag == true) {
			dialogPay.closeDialog();
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

	// 获取选择的银行卡
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 2:
			// TODO
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

	// 产品支付
	@SuppressWarnings("rawtypes")
	private void pay() {
		// TODO Auto-generated method stub
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		// MD5( data+uid.toString()+大写(key)
		HashMap<String, Object> data = new HashMap<String, Object>();
		if (formUserassets) {
			data.put("id", curProduct.getTarget_id());
		} else {
			data.put("id", curProduct.getId());
		}
		data.put("number_has", number_has);// 购买数量
		data.put("buy_money", buy_money); // 每份金额
		data.put("interest", interest); // 年化率
		data.put("limit", limit); // 期限
		data.put("limit_min", limit_min);// 最短赎回期限
		System.out.println("data==  " + data);
		System.out.println("limit --" + limit);
		System.out.println("limit_min --" + limit_min);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		params.put("data", HttpUtil.getData(data));

		System.out.println("params==  " + params);
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.PRODUCT_PAY, params, BaseData.class, null, paysuccessListener(), errorListener());
	}

	// 成功
	@SuppressWarnings("rawtypes")
	private Listener<BaseData> paysuccessListener() {
		// TODO Auto-generated method stub
		return new Listener<BaseData>() {
			@Override
			public void onResponse(BaseData response) {
				System.out.println("支付 返回code == " + response.code);
				dialogHelper.stopProgressDialog();
				// 再判断是否有设置交易密码
				if (response.code.equals("1038")) { // 1038
					// 弹出设置交易密码框
					int goflag = 1;
					dsp = new DialogSetPwd(InvestActivity.this, goflag, 0, 0);
					dsp.createMyDialog();
					closeFlag = true;
				} else if (response.code.equals("0")) {
					closeFlag = false;
					dialogHelper.stopProgressDialog();
					byte[] b = Base64.decode(response.data);

					if (b != null && !b.equals("")) {
						JSONObject s = JSON.parseObject(new String(b));
						System.out.println("返回数据" + s);
						final int orderid = s.getInteger("orderid");
						System.out.println("订单id == " + orderid);
						// 弹出选择支付框
						dialogPay = new DialogPay(InvestActivity.this, curProduct.getSubject(), redflag, redmoney, buymoney, user_money, defaultcard, curBanklist, number_has,
						        new EventInvestListener() {
							        @Override
							        public void eventDataHandlerListener(int data, boolean checkflag) {
								        // TODO Auto-generated method stub
								        redcheckflag = checkflag;
								        is_payment = data;
								        System.out.println("is_payment==" + data);
								        System.out.println("orderid==" + orderid);
								        System.out.println("redcheckflag==" + redcheckflag);
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
								        enterPay(orderid);
								        // 先调接口传递 支付方式
								        // Intent intent2 = new Intent(
								        // InvestActivity.this,
								        // EnterPwdActivity.class);
								        // intent2.putExtra("payflag", 0);
								        // startActivity(intent2);
								        // overridePendingTransition(
								        // R.anim.push_left_in,
								        // R.anim.push_left_out);
							        }

							        @Override
							        public void eventCloseORderListener() {
								        // TODO Auto-generated method stub
								        // 取消订单
								        // number_has;
								        // orderid;
								        closeOrder(orderid);
							        }

						        });
						dialogPay.createMyDialog();
					} else {
						ToastManage.showToast(response.desc);
					}

				} else {
					ToastManage.showToast(response.desc);
					return;
				}
			}
		};
	}

	// 关闭支付订单； number_has ,orderid ,target_id
	private void closeOrder(int orderid) {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("order_id", orderid);
		data.put("target_id", curProduct.getId());
		data.put("number_has", number_has);

		System.out.println("data== " + data);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		params.put("data", HttpUtil.getData(data));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.DEL_ORDER, params, BaseData.class, null, closeOrdersuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> closeOrdersuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				System.out.println("del_order 返回code == " + response.code);
				if (response.code.equals("0")) {
					dialogHelper.stopProgressDialog();
				} else {
					dialogHelper.stopProgressDialog();
					ToastManage.showToast(response.desc);
				}
			}
		};
	}

	// 确认支付信息 传递 支付的id ;
	private void enterPay(int orderid) {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("orderid", orderid);
		data.put("is_payment", is_payment);
		if (is_payment == 1) {
			data.put("card_id", defaultcard.getBankcard_id());
			System.out.println("card_id== " + defaultcard.getBankcard_id());
		}

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		params.put("data", HttpUtil.getData(data));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.ENTER_PAY, params, BaseData.class, null, enterPaysuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> enterPaysuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				System.out.println("ENTER_PAY 返回code == " + response.code);
				dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					int orderid = 0;
					if (b != null && !b.equals("")) {
						JSONObject s = JSON.parseObject(new String(b));
						System.out.println("ENTER_PAY 数据=" + s);
						orderid = s.getInteger("orderid");
					}
					Intent intent2 = new Intent(InvestActivity.this, EnterPwdActivity.class);
					intent2.putExtra("payflag", 0);
					intent2.putExtra("orderid", orderid);
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
