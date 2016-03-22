package com.sptech.qujj.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.adapter.LVBuyNumberAdapter;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.BuyPronumber;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 理财中心--购买人数
 * 
 * @author yebr
 * 
 */

public class BuyNumberActivity extends BaseActivity implements OnClickTitleListener, OnClickListener {

	private TitleBar titleBar;
	private ListView listView;

	private LVBuyNumberAdapter lvBuyNumberAdapter;
	private List<BuyPronumber> blist = new ArrayList<BuyPronumber>();

	private SharedPreferences spf;
	private DialogHelper dialogHelper;
	private int pro_id;
	private RelativeLayout rela_nodata;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_product_buynumber);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		if (getIntent() != null) {
			pro_id = getIntent().getExtras().getInt("id");
			System.out.println("id==" + pro_id);
		}

		rela_nodata = (RelativeLayout) findViewById(R.id.rela_nodata);
		dialogHelper = new DialogHelper(this);
		spf = this.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);

		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("购买人数", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		listView = (ListView) findViewById(R.id.listView_buynumber);

		lvBuyNumberAdapter = new LVBuyNumberAdapter(this, blist);
		listView.setAdapter(lvBuyNumberAdapter);
		getBuyNumber();
	}

	private void initListViewData() {
		if (blist.size() == 0) {
			rela_nodata.setVisibility(View.VISIBLE);
		}
		lvBuyNumberAdapter.reset(blist);
		lvBuyNumberAdapter.notifyDataSetInvalidated();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

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

	// 购买人数
	@SuppressWarnings("rawtypes")
	private void getBuyNumber() {
		// TODO Auto-generated method stub
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		String uid = spf.getString(Constant.UID, "");
		String key = spf.getString(Constant.KEY, "");

		// MD5( data+uid.toString()+大写(key)
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("id", pro_id);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", uid);
		params.put("sign", HttpUtil.getSign(data, uid, key));
		params.put("data", HttpUtil.getData(data));

		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.GET_PRODUCT_BUY_NUM, params, BaseData.class, null, successListener(), errorListener());
	}

	// 成功
	@SuppressWarnings("rawtypes")
	private Listener<BaseData> successListener() {
		// TODO Auto-generated method stub
		return new Listener<BaseData>() {
			@Override
			public void onResponse(BaseData response) {
				dialogHelper.stopProgressDialog();
				System.out.println("返回code == " + response.code);
				if (response.code.equals("0")) {
					System.out.println("ASDASDFASDF");
					byte[] b = Base64.decode(response.data);

					if (b != null && !b.equals("")) {
						// JSONArray s = JSON.parseArray(new String(b));
						String dataString;
						dataString = new String(b);
						System.out.println("GET_PRODUCT_BUY_NUM 购买人数数据==" + dataString);
						List<BuyPronumber> buList = new ArrayList<BuyPronumber>();
						buList = JSON.parseArray(dataString, BuyPronumber.class);
						blist = buList;
						System.out.println("blist==" + blist);
						initListViewData();
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

}
