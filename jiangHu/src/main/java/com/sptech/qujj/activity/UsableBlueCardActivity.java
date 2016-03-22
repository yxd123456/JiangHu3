package com.sptech.qujj.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.sptech.qujj.adapter.UsableBluecardAdapter;
import com.sptech.qujj.adapter.UsableCardAdapter;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.model.UsablebankBean;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;
import com.sptech.qujj.R;

/**
 * //支持的信用卡列表
 * 
 * @author gusonglei
 * 
 */

public class UsableBlueCardActivity extends BaseActivity implements
		OnClickTitleListener, OnClickListener {
	private TitleBar titleBar;
	private ListView listview_card; // 银行卡列表
	private UsableBluecardAdapter lvBankCardAdapter;
	private List<UsablebankBean> blist = new ArrayList<UsablebankBean>();
	private SharedPreferences spf;
	private int posti = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usablebank_layout);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("选择信用卡", R.drawable.jh_back_selector, 0, "",
				"");
		titleBar.setOnClickTitleListener(this);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		listview_card = (ListView) findViewById(R.id.listview_card);
		List<UsablebankBean> buylist = JSON.parseArray(
				spf.getString(Constant.SUPPORTBLUEBANK, ""), UsablebankBean.class);

		blist = buylist;
		lvBankCardAdapter = new UsableBluecardAdapter(UsableBlueCardActivity.this,
				blist);
		listview_card.setAdapter(lvBankCardAdapter);

		// lvBankCardAdapter = new LVBankCardAdapter(this, blist);
		// listview_card.setAdapter(lvBankCardAdapter);
		// getData();
	}

	// private void getData() {
	// HashMap<String, Object> data = new HashMap<String, Object>();
	// data.put("card_type", 1);
	// HashMap<String, String> params = new HashMap<String, String>();
	// params.put("data", HttpUtil.getData(data));
	// params.put("sign", HttpUtil.getSign(data));
	// @SuppressWarnings("rawtypes")
	// HttpVolleyRequest<BaseData> request = new
	// HttpVolleyRequest<BaseData>(UsableBankActivity.this, false);
	// request.HttpVolleyRequestPost(JsonConfig.SUPPORTBANKLIST, params,
	// BaseData.class, null, setdealSuccessListener, errorListener());
	//
	// }

	// @SuppressWarnings("rawtypes")
	// private Listener<BaseData> setdealSuccessListener = new
	// Listener<BaseData>() {
	//
	// @Override
	// public void onResponse(BaseData response) {
	// if (response.code.equals("0")) {
	// byte[] b = Base64.decode(response.data);
	// JSONArray s = JSON.parseArray(new String(b));
	// System.out.println("返回数据" + s);
	// List<UsablebankBean> buylist = JSON.parseArray(new String(b),
	// UsablebankBean.class);
	// blist = buylist;
	// lvBankCardAdapter = new UsableCardAdapter(UsableBankActivity.this,
	// blist);
	// listview_card.setAdapter(lvBankCardAdapter);
	//
	// } else {
	// Toast.makeText(UsableBankActivity.this, response.desc,
	// Toast.LENGTH_SHORT).show();
	// }
	//
	// }
	//
	// };

	// // 调用web服务失败返回数据
	// @SuppressLint("ShowToast")
	// private Response.ErrorListener errorListener() {
	// return new Response.ErrorListener() {
	//
	// @Override
	// public void onErrorResponse(VolleyError error) {
	//
	// }
	// };
	// }

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLeftButtonClick(View view) {
		// TODO Auto-generated method stub
		ActivityJumpManager.finishActivity(this, 1);
		// Intent intent = new Intent();
		// intent.putExtra("first", "123456789");
		// UsableBankActivity.this.setResult(AddBankcardActivity.resultOk,
		// intent);
		// UsableBankActivity.this.finish();
	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub

	}

	public void test(int position) {
		Intent intent = new Intent();
		intent.putExtra("name", blist.get(position).getName());
		intent.putExtra("key", blist.get(position).getKey());
		intent.putExtra("id", blist.get(position).getId());
		intent.putExtra("stream", blist.get(position).getStream());
		setResult(AddBluecardActivity.resultOk, intent);
		finish();
	}

}
