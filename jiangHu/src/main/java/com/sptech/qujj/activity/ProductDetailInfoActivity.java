package com.sptech.qujj.activity;

import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogGuSuan;
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
import com.sptech.qujj.view.EventHandleListener;
import com.sptech.qujj.view.SpringProgressView;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 理财中心--产品详情信息
 * 
 * @author yebr
 * 
 */

public class ProductDetailInfoActivity extends BaseActivity implements OnClickTitleListener, OnClickListener {

	private TitleBar titleBar;

	private TextView tv_licaixieyi, tv_yearvalue, tv_date, tv_start, tv_rong, tv_hasrong, tv_persent, tv_shenyunum, tv_jiesuan, tv_pnum, tv_shuhui;
	// 理财协议范本,年化收益率，融资金额，已融资金额,已出售百分比,剩余份额,结算日,购买人数,赎回备注;
	private Button btn_day_one, btn_day_two, btn_hour_one, btn_hour_two, btn_min_one, btn_min_two; // 剩余时间
	private SpringProgressView springProgressView; // 水平进度条
	private RelativeLayout rela_time, rela_proinstro, rela_gusuan; // 项目倒计时， //
	                                                               // 企业介绍,估算
	private RelativeLayout rela_proinstrcontent;// 企业介绍内容

	private ImageView image_projectinstr;
	private RelativeLayout rela_yibuy; // 已购买的人数
	private Button btn_buy;// 立即申购
	private String pro_name = "";
	private SharedPreferences spf;
	private DialogHelper dialogHelper;

	private ScheduledExecutorService service;
	private final int ACTION_TIME = 1;
	private WebView webView;
	private DownTimer downTimer = new DownTimer(60 * 1000, 1000);

	private int pro_id;
	private Product curProduct;
	// 估算收益弹出框的数据
	private int dateType;
	private int date;
	private String dateString = "";// 项目期限

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_product_detailinfo);
		initView();
		Tools.addActivityList(this);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		dialogHelper = new DialogHelper(this);

		if (getIntent() != null) {
			pro_id = getIntent().getExtras().getInt("id");
			pro_name = getIntent().getExtras().getString("pro_name");
			System.out.println("id" + pro_id);
		}
		if (pro_name != null && pro_name.length() > 15) {
			String title = pro_name.substring(0, 15);
			pro_name = title + "...";
		}

		spf = this.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setAllowFileAccess(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setSupportZoom(false); // 支持缩放
		webView.getSettings().setBuiltInZoomControls(false);
		webView.requestFocus();
		// webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		// webView.getSettings().setBlockNetworkImage(true);

		titleBar = (TitleBar) findViewById(R.id.titleBar);
		if (!"".equals(pro_name)) {
			titleBar.bindTitleContent(pro_name, R.drawable.jh_back_selector, 0, "", "");
		}
		titleBar.setOnClickTitleListener(this);
		springProgressView = (SpringProgressView) findViewById(R.id.spring_progress_view);
		springProgressView.setMaxCount(100);
		// springProgressView.setCurrentCount(60);

		tv_yearvalue = (TextView) findViewById(R.id.tv_yearvalue);
		tv_date = (TextView) findViewById(R.id.tv_date);
		tv_start = (TextView) findViewById(R.id.tv_start);
		tv_rong = (TextView) findViewById(R.id.tv_rong);
		tv_hasrong = (TextView) findViewById(R.id.tv_hasrong);
		tv_persent = (TextView) findViewById(R.id.tv_persent);
		tv_shenyunum = (TextView) findViewById(R.id.tv_shenyunum);
		// tv_jiesuan = (TextView) findViewById(R.id.tv_jiesuan);
		tv_shuhui = (TextView) findViewById(R.id.tv_shuhui);
		tv_pnum = (TextView) findViewById(R.id.tv_pnum);

		// 时间(倒计时)
		btn_day_one = (Button) findViewById(R.id.btn_day_one);
		btn_day_two = (Button) findViewById(R.id.btn_day_two);
		btn_hour_one = (Button) findViewById(R.id.btn_hour_one);
		btn_hour_two = (Button) findViewById(R.id.btn_hour_two);
		btn_min_one = (Button) findViewById(R.id.btn_min_one);
		btn_min_two = (Button) findViewById(R.id.btn_min_two);

		tv_licaixieyi = (TextView) findViewById(R.id.tv_licaixieyi);
		tv_licaixieyi.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线

		rela_time = (RelativeLayout) findViewById(R.id.rela_time);
		rela_yibuy = (RelativeLayout) findViewById(R.id.rela_yibuy);

		rela_proinstro = (RelativeLayout) findViewById(R.id.rela_proinstro);
		rela_gusuan = (RelativeLayout) findViewById(R.id.rela_gusuan);

		// rela_removalrule = (RelativeLayout)
		// findViewById(R.id.rela_removalrule);
		// rela_profengxian = (RelativeLayout)
		// findViewById(R.id.rela_profengxian);

		rela_proinstrcontent = (RelativeLayout) findViewById(R.id.rela_proinstrocontent);
		// rela_rulecontent = (RelativeLayout)
		// findViewById(R.id.rela_rulecontent);
		// rela_fengxiancontent = (RelativeLayout)
		// findViewById(R.id.rela_fengxiancontent);

		image_projectinstr = (ImageView) findViewById(R.id.image_projectinstr);
		// image_projectrule = (ImageView) findViewById(R.id.image_projectrule);
		// image_fengxian = (ImageView) findViewById(R.id.image_fengxian);

		// tv_proname = (TextView) findViewById(R.id.tv_proname);
		// tv_prodetail = (TextView) findViewById(R.id.tv_prodetail);

		btn_buy = (Button) findViewById(R.id.btn_buy);
		tv_licaixieyi.setOnClickListener(this);
		rela_proinstro.setOnClickListener(this);
		// rela_removalrule.setOnClickListener(this);
		// rela_profengxian.setOnClickListener(this);
		rela_yibuy.setOnClickListener(this);
		btn_buy.setOnClickListener(this);
		rela_gusuan.setOnClickListener(this);

		// if (curProduct != null) {
		// initViewData();
		// }
		initProInfoData();
	}

	@SuppressWarnings("rawtypes")
	private void initProInfoData() {
		// TODO Auto-generated method stub
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		String uid = spf.getString(Constant.UID, "");
		String key = spf.getString(Constant.KEY, "");
		System.out.println("uid=   " + uid);
		System.out.println("key=   " + key);

		// MD5( data+uid.toString()+大写(key)
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("id", pro_id);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", uid);
		params.put("sign", HttpUtil.getSign(data, uid, key));
		params.put("data", HttpUtil.getData(data));

		System.out.println("params==  " + params);
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.PRODUCT_DETAIL, params, BaseData.class, null, successListener(), errorListener());
	}

	// 成功
	@SuppressWarnings("rawtypes")
	private Listener<BaseData> successListener() {
		// TODO Auto-generated method stub
		return new Listener<BaseData>() {
			@Override
			public void onResponse(BaseData response) {
				System.out.println("返回code == " + response.code);
				dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {
						// JSONArray s = JSON.parseArray(new String(b));
						curProduct = new Product();
						curProduct = JSON.parseObject(new String(b), Product.class);
						System.out.println("PRODUCT_DetailINFO 返回数据" + new String(b));
						initViewData();
					}
				} else {

					ToastManage.showToast(response.desc);
				}
			}
		};
	}

	// 失败
	@SuppressLint("ShowToast")
	private Response.ErrorListener errorListener() {
		return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				dialogHelper.stopProgressDialog();
			}
		};
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ACTION_TIME:
				timeDao();
				break;
			default:
				break;
			}

		}
	};

	// 根据数据赋值
	public void initViewData() {
		timeDao();
		String titlename = curProduct.getSubject();
		if (titlename.length() > 15) {
			String title = titlename.substring(0, 15);
			titlename = title + "...";
		}
		titleBar.bindTitleContent(titlename, R.drawable.jh_back_selector, 0, "", "");
		System.out.println("limit-on== " + curProduct.getLimit_on());
		if (curProduct.getLimit_on() == 0) {
			rela_time.setVisibility(View.GONE);
		} else {
			rela_time.setVisibility(View.VISIBLE);
			System.out.println("limit-on-ScheduledExecutorServiceTest ");
			// this.ScheduledExecutorServiceTest();
			downTimer.start();
		}

		float xx = curProduct.getInterest();
		int yy = (int) xx;
		if (0 == (xx - (float) yy)) {
			tv_yearvalue.setText(yy + "%");
		} else {
			tv_yearvalue.setText(xx + "%");
		}

		float min = curProduct.getBuy_money_min();
		int kk = (int) min;
		if (0 == (min - (float) kk)) {
			tv_start.setText(kk + "元起投");
		} else {
			tv_start.setText(min + "元起投");
		}
		// tv_start.setText(curProduct.getBuy_money_min() + "元起投");

		// 融资/已融资 金额
		tv_rong.setText(DataFormatUtil.floatsaveTwo(curProduct.getNumber() * curProduct.getBuy_money_min()) + "元"); // 总数
		                                                                                                            // *
		                                                                                                            // 起投;
		tv_hasrong.setText(DataFormatUtil.floatsaveTwo(curProduct.getNumber_has() * curProduct.getBuy_money_min()) + "元");// 已购买数*起投

		// 剩余份额
		tv_shenyunum.setText(curProduct.getNumber() - curProduct.getNumber_has() + "");
		// // 结算日
		// tv_jiesuan.setText(DateManage.getYearMonthDay(curProduct.getEnd_time()
		// + ""));
		// 赎回判断
		if (curProduct.getIs_raply() == 0) {// 可赎回
			tv_shuhui.setText("支持随时赎回操作");
		} else if (curProduct.getIs_raply() == 1) {
			tv_shuhui.setText("不支持赎回");
		}

		// 购买人数
		tv_pnum.setText(curProduct.getNumber_people() + "人");

		int hasBuy = 0;
		if (curProduct.getNumber() != 0) {
			int has = curProduct.getNumber_has();
			int count = curProduct.getNumber();
			hasBuy = (int) Math.round(((double) has / count * 100));
		}

		int limit = curProduct.getLimit();
		int type = limit / 100000;
		int limitdate = limit % 100000;
		date = limitdate;
		System.out.println("type= " + type + "date= " + limitdate);
		if (type == 1) {
			tv_date.setText(limitdate + "天期限");
			dateType = 1;
			dateString = limitdate + "天";
		} else if (type == 2) {
			tv_date.setText(limitdate + "个月期限");
			dateType = 2;
			dateString = limitdate + "个月";
		} else if (type == 3) {
			tv_date.setText(limitdate + "年期限");
			dateType = 3;
			dateString = limitdate + "年";
		}

		// springProgressView.setMaxCount(100);
		springProgressView.setCurrentCount(hasBuy);
		if (hasBuy >= 100) {
			tv_persent.setText("100%");
			btn_buy.setText("售罄");
			btn_buy.setBackgroundResource(R.drawable.btn_proinfo_buy_noselect);
			btn_buy.setEnabled(false);
		} else {
			tv_persent.setText(hasBuy + "%");
		}

		if (curProduct.getIs_buy() == 1) {
			btn_buy.setText("仅限新手");
			btn_buy.setBackgroundResource(R.drawable.btn_proinfo_buy_noselect);
			btn_buy.setEnabled(false);
		}
		// webView.loadUrl("http://hzzn.f3322.net:9636/index/product_info.html?id="
		// + curProduct.getId());
		webView.loadUrl(JsonConfig.HTML + "index/product_info.html?id=" + curProduct.getId());
		System.out.println("HTML== " + JsonConfig.HTML + "index/product_info.html?id=" + curProduct.getId());
		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		webView.getSettings().setUseWideViewPort(true);
		System.out.println("");
		// // 项目介绍
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}
		});

		// tv_proname.setText(curProduct.getSubject());
		// System.out.println("html== " + curProduct.getDescription());
		// CharSequence charSequence =
		// Html.fromHtml(curProduct.getDescription());
		// tv_prodetail.setText(charSequence);
		// tv_prodetail.setText(curProduct.getDescription());

	}

	private void timeDao() {
		System.out.println("timeDao-- == " + curProduct.getLimit_on());
		if (curProduct.getLimit_on() != 0) {
			int hqtime = curProduct.getEnd_time();// 项目结束时间
			System.out.println("end_time== " + hqtime);
			// 计算时间戳
			System.currentTimeMillis();
			Long between = (hqtime - System.currentTimeMillis() / 1000);

			System.out.println("between == " + between);
			if (between > 0) {
				System.out.println("now time == " + System.currentTimeMillis() / 1000);
				System.out.println("between time == " + between);

				long day1 = between / (24 * 3600);
				long hour1 = between % (24 * 3600) / 3600;
				long minute1 = between % 3600 / 60;
				// long second1 = between % 60;
				System.out.println("剩余 day == " + day1);
				System.out.println("剩余 hour == " + hour1);
				System.out.println("剩余 minute1 == " + minute1);

				long dayone = 0, daytwo = 0;
				if (day1 >= 10 && day1 < 100) {
					dayone = day1 / 10;
					daytwo = day1 % 10;
				} else if (day1 > 99) {
					dayone = 9;
					daytwo = 9;
				} else if (day1 < 10) {
					dayone = 0;
					daytwo = day1;
				}
				btn_day_one.setText(dayone + "");
				System.out.println("剩余 day == " + dayone);
				btn_day_two.setText(daytwo + "");
				System.out.println("剩余 day == " + daytwo);

				long hourone = 0, hourtwo = 0;
				if (hour1 >= 10 && hour1 <= 24) {
					hourone = hour1 / 10;
					hourtwo = hour1 % 10;
				} else if (hour1 < 10) {
					hourone = 0;
					hourtwo = hour1;
				} else if (hour1 > 24) {
					hourone = 0;
					hourtwo = 0;
				}
				btn_hour_one.setText(hourone + "");
				System.out.println("剩余 hour == " + hourone);
				btn_hour_two.setText(hourtwo + "");
				System.out.println("剩余 hour == " + hourtwo);

				long minuteone = 0, minutetwo = 0;
				if (minute1 >= 10 && minute1 <= 59) {
					minuteone = minute1 / 10;
					minutetwo = minute1 % 10;
				} else if (minute1 < 10) {
					minuteone = 0;
					minutetwo = minute1;
				} else if (minute1 > 59) {
					minuteone = 0;
					minutetwo = 0;
				}
				btn_min_one.setText(minuteone + "");
				System.out.println("剩余 minute1 == " + minuteone);
				btn_min_two.setText(minutetwo + "");
				System.out.println("剩余 minute1 == " + minutetwo);

				downTimer.start();
			} else if (between <= 0) {
				rela_time.setVisibility(View.GONE);
				// service.shutdown();
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rela_proinstro:
			if (rela_proinstrcontent.isShown()) {
				rela_proinstrcontent.setVisibility(View.GONE);
				image_projectinstr.setBackgroundResource(R.drawable.jh_licai_detailinfo_bottom_selector);
			} else {
				rela_proinstrcontent.setVisibility(View.VISIBLE);
				webView.setVisibility(View.VISIBLE);
				// webView.loadUrl("http://www.baidu.com");
				image_projectinstr.setBackgroundResource(R.drawable.jh_licai_detailinfo_top_selector);
			}
			break;
		// case R.id.rela_removalrule:
		// if (rela_rulecontent.isShown()) {
		// rela_rulecontent.setVisibility(View.GONE);
		// image_projectrule.setBackgroundResource(R.drawable.jh_licai_detailinfo_bottom_selector);
		// } else {
		// rela_rulecontent.setVisibility(View.VISIBLE);
		// image_projectrule.setBackgroundResource(R.drawable.jh_licai_detailinfo_top_selector);
		// }
		// break;
		// case R.id.rela_profengxian:
		// if (rela_fengxiancontent.isShown()) {
		// rela_fengxiancontent.setVisibility(View.GONE);
		// image_fengxian.setBackgroundResource(R.drawable.jh_licai_detailinfo_bottom_selector);
		// } else {
		// rela_fengxiancontent.setVisibility(View.VISIBLE);
		// image_fengxian.setBackgroundResource(R.drawable.jh_licai_detailinfo_top_selector);
		// }
		// break;
		case R.id.rela_yibuy: // 已购买人数
			juge();
			Intent intent1 = new Intent(this, BuyNumberActivity.class);
			if (curProduct != null) {
				intent1.putExtra("id", curProduct.getId());
			}
			startActivity(intent1);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.btn_buy: // 立即申购
			// 判断是否实名认证
			if (spf.getString(Constant.USER_AUTH, "").equals("1")) {
				Intent intent2 = new Intent(this, InvestActivity.class);
				if (curProduct != null) {
					intent2.putExtra("product", curProduct);
				}
				startActivity(intent2);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			} else {
				ToastManage.showToast("您尚未实名认证，请先认证");
				Intent intent3 = new Intent(ProductDetailInfoActivity.this, UserInfoVerificationActivity.class);
				intent3.putExtra("nextflag", 1);
				intent3.putExtra("pro_id", curProduct.getId());
				startActivity(intent3);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
			break;
		case R.id.tv_licaixieyi:
			juge();
			Intent agree = new Intent(ProductDetailInfoActivity.this, WebViewActivity.class);
			agree.putExtra("url", JsonConfig.HTML + "/index/money_papers");
			agree.putExtra("title", "趣救急理财协议");
			startActivity(agree);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rela_gusuan: // 估算
			float min_money = curProduct.getBuy_money_min();
			float yearvalue = curProduct.getInterest();
			if (curProduct != null) {
				DialogGuSuan dr = new DialogGuSuan(this, dateType, date, min_money, yearvalue, dateString, new EventHandleListener() {
					@Override
					public void eventRifhtHandlerListener() {
						// ToastManage.showToast("right");
						// 计算按钮
					}

					@Override
					public void eventLeftHandlerListener() {
						// ToastManage.showToast("left");
					}
				});
				dr.createMyDialog();
			}
			break;
		default:
			break;
		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		System.out.println("downTimer cancel() ");
		downTimer.cancel();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		downTimer.cancel();
	}

	@Override
	public void onLeftButtonClick(View view) {
		// TODO Auto-generated method stub
		// service.shutdown();
		downTimer.cancel();
		ActivityJumpManager.finishActivity(this, 1);

	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub

	}

	// 倒计时 1 账单下载
	class DownTimer extends CountDownTimer {

		public DownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// 倒计时开始
			long m = millisUntilFinished / 1000;
			String ss = String.valueOf(m % 3600 % 60);
			ss = twoLength(ss);
		}

		/**
		 * 如果小于位，加0
		 * 
		 * @param str
		 * @return
		 */
		private String twoLength(String str) {
			if (str.length() == 1) {
				return str = "0" + str;
			}
			return str;
		}

		@Override
		public void onFinish() {
			// asyncDownload();
			// timeDao();
			// handler. (ACTION_TIME);
			System.out.println("timedown?? ");
			timeDao();

		}
	};

	private void juge() {
		System.out.println("daas==" + rela_proinstrcontent.isShown());
		if (rela_proinstrcontent.isShown()) {
			rela_proinstrcontent.setVisibility(View.GONE);
			image_projectinstr.setBackgroundResource(R.drawable.jh_licai_detailinfo_bottom_selector);
		}
	}
}
