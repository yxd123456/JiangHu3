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
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.EventHandleListener;
import com.sptech.qujj.view.SpringProgressView;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 理财中心--产品信息(暂时去掉了此页面)
 * 
 * @author yebr
 * 
 */

public class ProductInfoActivity extends BaseActivity implements OnClickTitleListener, OnClickListener {

	private TitleBar titleBar;
	private SpringProgressView springProgressView; // 水平进度条

	private RelativeLayout rela_top, rela_buynumber, rela_gusuan; // 点击-项目详情,购买人数，估算
	private TextView tv_proname, tv_yearnum, tv_datenum, tv_datetype, tv_startnum, tv_persent, tv_pnum; // 项目名称，年利率，期限，天/月/年,起投,已出售百分比,购买人数；

	private SharedPreferences spf;
	private DialogHelper dialogHelper;
	private int pro_id;
	private Product curProduct;

	// 传到 估算弹出框中的数据
	private int dateType;
	private int date;
	private String dateString = "";// 项目期限
	private float yearvalue;
	private float min_money;
	private Button btn_buy;// 开始购买, 预估按钮

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_productinfo);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		if (getIntent() != null) {
			pro_id = getIntent().getExtras().getInt("id");
			System.out.println("id" + pro_id);
		}

		dialogHelper = new DialogHelper(this);
		spf = this.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);

		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("产品信息", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		springProgressView = (SpringProgressView) findViewById(R.id.spring_progress_view);

		tv_proname = (TextView) findViewById(R.id.tv_proname);
		tv_yearnum = (TextView) findViewById(R.id.tv_yearnum);
		tv_datenum = (TextView) findViewById(R.id.tv_datenum);
		tv_datetype = (TextView) findViewById(R.id.tv_datemonth);
		tv_startnum = (TextView) findViewById(R.id.tv_startnum);
		tv_persent = (TextView) findViewById(R.id.tv_persent);
		tv_pnum = (TextView) findViewById(R.id.tv_pnum);

		rela_top = (RelativeLayout) findViewById(R.id.rela_top);
		rela_buynumber = (RelativeLayout) findViewById(R.id.rela_buynumber);
		rela_gusuan = (RelativeLayout) findViewById(R.id.rela_gusuan);
		btn_buy = (Button) findViewById(R.id.btn_buy);

		btn_buy.setOnClickListener(this);
		rela_top.setOnClickListener(this);
		rela_buynumber.setOnClickListener(this);
		rela_gusuan.setOnClickListener(this);
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

	// 根据数据赋值
	public void initViewData() {
		if (curProduct.getSubject().length() > 15) {
			String title = curProduct.getSubject().substring(0, 15);
			tv_proname.setText(title + "...");
		} else {
			tv_proname.setText(curProduct.getSubject());
		}
		// tv_proname.setText(curProduct.getSubject());
		float xx = curProduct.getInterest();
		int yy = (int) xx;
		if (0 == (xx - (float) yy)) {
			tv_yearnum.setText(yy + "%");
		} else {
			tv_yearnum.setText(xx + "%");
		}
		// tv_yearnum.setText(curProduct.getInterest() + "%");
		// tv_startnum.setText(curProduct.getBuy_money_min() + "");
		float min = curProduct.getBuy_money_min();
		int kk = (int) min;
		if (0 == (min - (float) kk)) {
			tv_startnum.setText(kk + "");
		} else {
			tv_startnum.setText(min + "");
		}
		tv_pnum.setText(curProduct.getNumber_people() + "人");
		min_money = min;
		int hasBuy = 0;
		if (curProduct.getNumber() != 0) {
			int has = curProduct.getNumber_has();
			int count = curProduct.getNumber();
			hasBuy = (int) Math.round(((double) has / count * 100));
		}
		int limit = curProduct.getLimit();
		int type = limit / 100000;
		int limitdate = limit % 100000;
		System.out.println("type= " + type + "date= " + limitdate);
		date = limitdate;
		yearvalue = curProduct.getInterest();
		if (type == 1) {
			tv_datenum.setText(limitdate + "");
			tv_datetype.setText("天");
			dateType = 1;
			dateString = limitdate + "天";
		} else if (type == 2) {
			tv_datenum.setText(limitdate + "");
			tv_datetype.setText("个月");
			dateString = limitdate + "个月";
			dateType = 2;
		} else if (type == 3) {
			tv_datenum.setText(limitdate + "");
			tv_datetype.setText("年");
			dateString = limitdate + "年";
			dateType = 3;
		}
		springProgressView.setMaxCount(100);
		springProgressView.setCurrentCount(hasBuy);
		if (hasBuy >= 100) {
			tv_persent.setText("100%");
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
		dialogHelper.stopProgressDialog();
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

	// 成功
	@SuppressWarnings("rawtypes")
	private Listener<BaseData> successListener() {
		// TODO Auto-generated method stub
		return new Listener<BaseData>() {
			@Override
			public void onResponse(BaseData response) {
				System.out.println("返回code == " + response.code);
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
					dialogHelper.stopProgressDialog();
					ToastManage.showToast(response.desc);
				}
			}
		};
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rela_top:
			Intent intent = new Intent(this, ProductDetailInfoActivity.class);
			if (curProduct != null) {
				intent.putExtra("product", curProduct);
			}
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rela_buynumber: // 购买人数
			Intent intent1 = new Intent(this, BuyNumberActivity.class);
			if (curProduct != null) {
				intent1.putExtra("id", curProduct.getId());
			}
			startActivity(intent1);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.btn_buy: // 开始购买
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
				Intent intent3 = new Intent(ProductInfoActivity.this, UserInfoVerificationActivity.class);
				intent3.putExtra("nextflag", 1);
				intent3.putExtra("pro_id", curProduct.getId());
				startActivity(intent3);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
			break;
		case R.id.rela_gusuan: // 估算
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
	public void onLeftButtonClick(View view) {
		// TODO Auto-generated method stub
		ActivityJumpManager.finishActivity(this, 1);

	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub

	}

}
