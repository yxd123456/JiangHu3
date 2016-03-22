package com.sptech.qujj.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sptech.qujj.R;
import com.sptech.qujj.adapter.LoadDetailAdapter;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BMListBean;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 借款明细页面：借你钱首页，本月借款跳转页面
 * 
 * @author LiLin
 * 
 */
public class ThisMonthLoanActivity extends BaseActivity implements
		OnClickTitleListener, OnItemClickListener {
	List<BMListBean> list = new ArrayList<BMListBean>();
	DialogHelper dialogHelper;// 加载进度条
	private SharedPreferences spf;
	private RelativeLayout rela_nodata;// 没有数据的页面
	TitleBar titleBar;
	PullToRefreshListView listView;

	LoadDetailAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_this_month_loan);
		Tools.addActivityList(this);
		initView();
		initListener();
		initData();
	}

	private void initView() {
		
		// TODO Auto-generated method stub
		spf = this.getSharedPreferences(Constant.USER_INFO,
				Context.MODE_PRIVATE);
		dialogHelper = new DialogHelper(this);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		listView = (PullToRefreshListView) findViewById(R.id.lv_this_month_load);
		adapter = new LoadDetailAdapter(this,list);
		listView.setAdapter(adapter);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initListener() {
		// TODO Auto-generated method stub
		titleBar.setOnClickTitleListener(this);
		titleBar.bindTitleContent("借款明细", R.drawable.jh_back_selector, 0, "",
				"");
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				getData();
				if(dialogHelper!=null){
					dialogHelper.stopProgressDialog();
				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				getData();
				if(dialogHelper!=null){
					dialogHelper.stopProgressDialog();
				}
			}
		});
		//adapter = new LoadDetailAdapter(this);
		
		listView.setOnItemClickListener(this);
	}

	private void initData() {
		// TODO Auto-generated method stub
		getData();
		
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

	private void getData() {
		// 参数设置
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("正在加载数据，请稍候...");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put(
				"sign",
				HttpUtil.getSign(spf.getString(Constant.UID, ""),
						spf.getString(Constant.KEY, "")));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(
				this, false);
		request.HttpVolleyRequestPost(JsonConfig.BORROW_MONEY_LIST, params,
				BaseData.class, null, loanlistsuccessListener(),
				errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> loanlistsuccessListener() {
		return new Listener<BaseData>() {
			@Override
			public void onResponse(BaseData response) {
				listView.onRefreshComplete();
				System.out.println("返回code == " + response.code);
				if(dialogHelper!=null){
					dialogHelper.stopProgressDialog();
				}
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					if (b != null && !"".equals(b)) {
						List<BMListBean> list1 = JSON.parseArray(new String(b),
								BMListBean.class);
						list.clear();
						list.addAll(list1);
						adapter.reset(list);
						dialogHelper.stopProgressDialog();
						Log.e("shuangpeng", "list1:" + list1.toString()
								+ "        list:" + list.toString()+list.get(0).getBorrow_id()+":"+list.get(0).getHelp_money()+":"+list.get(0).getTitle());
						Log.e("shuangpeng", "1234567890");
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
				listView.onRefreshComplete();
				dialogHelper.stopProgressDialog();
			}
		};
	}

	@Override
	public void onItemClick(AdapterView<?> v, View view, int position, long arg3) {
		// TODO Auto-generated method stub
		Intent detailIntent = new Intent(ThisMonthLoanActivity.this, BMDetailActivity.class);
		detailIntent.putExtra("borrow_id", list.get(position-1).getBorrow_id());
		startActivity(detailIntent);
	}
}
