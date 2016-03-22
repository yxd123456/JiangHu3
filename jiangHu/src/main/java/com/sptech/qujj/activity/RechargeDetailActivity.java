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
import com.alibaba.fastjson.JSONArray;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.adapter.LVRechargeDetailAdapter;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.RechargeDetail;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 充值明细
 * 
 * @author yebr
 * 
 */
public class RechargeDetailActivity extends BaseActivity implements OnClickTitleListener, OnClickListener {
	private ListView listView;

	private LVRechargeDetailAdapter lvRechargeDetailAdapter;
	private List<RechargeDetail> rechargeDetails = new ArrayList<RechargeDetail>();

	private TitleBar titleBar;

	private int start = 0; // 分页起始位置
	private int limit = 20; // 每页显示条数
	private DialogHelper dialogHelper;
	private SharedPreferences spf;

	private RelativeLayout rela_nodata;// 没有数据的页面

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_licai_rechargedetail);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		spf = this.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);

		rela_nodata = (RelativeLayout) findViewById(R.id.rela_nodata);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("充值明细", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		listView = (ListView) findViewById(R.id.listView_recharge);
		lvRechargeDetailAdapter = new LVRechargeDetailAdapter(this, rechargeDetails);
		listView.setAdapter(lvRechargeDetailAdapter);
		initRechargeDetail();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_left:
			break;
		default:
			break;
		}
	}

	private void initListView() {
		lvRechargeDetailAdapter.reset(rechargeDetails);
		lvRechargeDetailAdapter.notifyDataSetChanged();
	}

	// 充值记录
	private void initRechargeDetail() {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("start", start);
		data.put("limit", limit);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(RechargeDetailActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.USER_RECHARGE_DETAIL, params, BaseData.class, null, cardsuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> cardsuccessListener() {
		return new Listener<BaseData>() {
			@Override
			public void onResponse(BaseData response) {
				dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {
						JSONArray s = JSON.parseArray(new String(b));
						System.out.println("返回数据--" + new String(b));
						List<RechargeDetail> curRechargeDetails = JSON.parseArray(new String(b), RechargeDetail.class);
						rechargeDetails = curRechargeDetails;
						if (rechargeDetails.size() != 0) {
							rela_nodata.setVisibility(View.GONE);
							initListView();
							start = start + limit;
						} else {
							rela_nodata.setVisibility(View.VISIBLE);
						}
					} else {
						rela_nodata.setVisibility(View.VISIBLE);
					}
				} else {
					ToastManage.showToast(response.desc);
				}
			}
		};
	}

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

}
