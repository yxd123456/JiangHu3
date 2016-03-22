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
import com.alibaba.fastjson.JSONArray;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.adapter.LVProgressListAdapter;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.MyBillBean;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 进度中心
 * 
 * @author yebr
 * 
 */

public class ProgressActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {

	private TitleBar titleBar;
	private ListView lv_view; // 进度列表
	private LVProgressListAdapter mAdapter; //
	// private ProgressBar progress_loadmore;
	private SharedPreferences spf;
	private RelativeLayout rl_nocard;
	// private List<Banner> bannerList = new ArrayList<Banner>();// banner组
	private List<MyBillBean> blist = new ArrayList<MyBillBean>();
	private DialogHelper dialogHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_slidebar_progress);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("进度中心", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		lv_view = (ListView) findViewById(R.id.lv_view);
		rl_nocard = (RelativeLayout) findViewById(R.id.rl_nocard);
		// lv_view.setonRefreshListener(this);
		// lv_view.SetOnMyListViewScroll(this);
		lv_view.setOverScrollMode(View.OVER_SCROLL_NEVER);

		mAdapter = new LVProgressListAdapter(this, blist);
		lv_view.setAdapter(mAdapter);

		getHttpBill();
		// initListView();
	}

	private void initListView() {
		if (blist.size() == 0) {
			lv_view.setVisibility(View.GONE);
			rl_nocard.setVisibility(View.VISIBLE);
		} else {
			lv_view.setVisibility(View.VISIBLE);
			rl_nocard.setVisibility(View.GONE);
			mAdapter.reset(blist);
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rela_message: // 消息设置
			Intent intent = new Intent(this, AlertSettingActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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

	private void getHttpBill() {
		dialogHelper.startProgressDialog();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("is_type", 5);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.USERBILLS, params, BaseData.class, null, billSuccessListener, errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> billSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				byte[] b = Base64.decode(response.data);
				if (b != null && !b.equals("")) {
					JSONArray s = JSON.parseArray(new String(b));
					System.out.println("返回数据" + new String(b));
					List<MyBillBean> billlist = JSON.parseArray(new String(b), MyBillBean.class);
					blist.clear();
					blist = billlist;
					initListView();
				}
			} else {
				ToastManage.showToast(response.desc);
			}

		}
	};

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

}
