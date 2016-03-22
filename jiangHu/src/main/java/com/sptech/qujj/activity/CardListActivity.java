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

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.adapter.LVCardListAdapter;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.CardInfo;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.MyFMListView;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 信用卡列表
 * 
 * @author yebr
 * 
 */
public class CardListActivity extends BaseActivity implements OnClickTitleListener, OnClickListener {
	private MyFMListView listView;
	private LVCardListAdapter lvCardListAdapter;

	// private List<Banner> bannerList = new ArrayList<Banner>();// banner组
	List<CardInfo> curCardInfos = new ArrayList<CardInfo>();
	private TitleBar titleBar;
	private DialogHelper dialogHelper;
	private SharedPreferences spf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_home_cardlist);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		spf = this.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);

		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("信用卡列表", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		listView = (MyFMListView) findViewById(R.id.lv_view);
		lvCardListAdapter = new LVCardListAdapter(this, curCardInfos);
		listView.setAdapter(lvCardListAdapter);
		getCardList();
	}

	// 账单列表
	@SuppressWarnings("unused")
	private void getCardList() {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.CARD_LIST, params, BaseData.class, null, usersuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> usersuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				System.out.println("账单列表 返回code == " + response.code);
				dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {
						System.out.println("账单列表 数据=" + new String(b));
						List<CardInfo> cardInfos = new ArrayList<CardInfo>();
						cardInfos = JSON.parseArray(new String(b), CardInfo.class);
						curCardInfos.addAll(cardInfos);

						if (cardInfos.size() != 0) {
							lvCardListAdapter.reset(cardInfos);
							lvCardListAdapter.notifyDataSetChanged();
						} else {
							ToastManage.showToast("没有账单信息");
						}
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
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

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
