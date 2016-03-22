package com.sptech.qujj.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.adapter.UsableHandcardAdapter;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.UsablebankBean;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * //手动申请支持的信用卡列表
 * 
 * @author gusonglei
 * 
 */

public class UsableHandBankActivity extends BaseActivity implements OnClickTitleListener, OnClickListener, OnItemClickListener {
	private TitleBar titleBar;
	private ListView listview_card; // 银行卡列表
	private UsableHandcardAdapter lvBankCardAdapter;
	private List<UsablebankBean> blist = new ArrayList<UsablebankBean>();
	private SharedPreferences spf;
	private int posti = 0;
	private DialogHelper dialogHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usablebank_layout);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("选择信用卡", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		listview_card = (ListView) findViewById(R.id.listview_card);
		listview_card.setOnItemClickListener(this);
		// List<UsablebankBean> buylist = JSON.parseArray(
		// spf.getString(Constant.SUPPORTBANK, ""), UsablebankBean.class);
		//
		// blist = buylist;
		// lvBankCardAdapter = new
		// UsableHandcardAdapter(UsableHandBankActivity.this,
		// blist);
		// listview_card.setAdapter(lvBankCardAdapter);

		// lvBankCardAdapter = new LVBankCardAdapter(this, blist);
		// listview_card.setAdapter(lvBankCardAdapter);
		getData();
	}

	private void getData() {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("正在加载数据，请稍候...");
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(UsableHandBankActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.BANKOTHERLIST, null, BaseData.class, null, setdealSuccessListener, errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> setdealSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				byte[] b = Base64.decode(response.data);
				if (b != null && !b.equals("")) {
					JSONArray s = JSON.parseArray(new String(b));
					System.out.println("手动申请支持的信用卡--》" + s);
					List<UsablebankBean> buylist = JSON.parseArray(new String(b), UsablebankBean.class);
					// List<UsablebankBean> buylist = JSON.parseArray(
					// spf.getString(Constant.SUPPORTBANK, ""),
					// UsablebankBean.class);
					blist = buylist;
					lvBankCardAdapter = new UsableHandcardAdapter(UsableHandBankActivity.this, blist);
					listview_card.setAdapter(lvBankCardAdapter);
				}
			} else {
				Toast.makeText(UsableHandBankActivity.this, response.desc, Toast.LENGTH_SHORT).show();
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

	// public void test(int position) {
	// Intent intent = new Intent();
	// intent.putExtra("name", blist.get(position).getName());
	// intent.putExtra("key", blist.get(position).getKey());
	// intent.putExtra("id", blist.get(position).getId());
	// intent.putExtra("stream", blist.get(position).getStream());
	// setResult(HandAddbluecardActivity.resultOk, intent);
	// finish();
	// }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		UsablebankBean ub = blist.get(arg2);
		// ToastManage.showToast(ub.getName());
		Intent intent = new Intent();
		intent.putExtra("name", ub.getName());
		intent.putExtra("key", ub.getKey());
		intent.putExtra("id", "0");
		intent.putExtra("stream", "");
		setResult(HandAddbluecardActivity.resultOk, intent);
		finish();
	}

}
