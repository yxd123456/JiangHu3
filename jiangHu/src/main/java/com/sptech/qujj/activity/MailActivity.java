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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.adapter.LVMailListAdapter;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.CustomDialog;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.EmailBean;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 
 * 账单邮箱 列表
 * 
 * @author gusonglei
 * 
 */

public class MailActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {

	private TitleBar titleBar;
	private ListView lv_view; // 进度列表
	private LVMailListAdapter mAdapter; //
	private ProgressBar progress_loadmore;
	private List<EmailBean> bannerList = new ArrayList<EmailBean>();// banner组
	private SharedPreferences spf;
	private int delid;
	private int mailId;
	private DialogHelper dialogHelper;
	private TextView tv_tishi;

	private boolean check = false;// 是否提醒网络

	// private List<EmailBean> emaillists;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mail_layout);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {

		dialogHelper = new DialogHelper(this);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("账单邮箱", R.drawable.jh_back_selector, R.drawable.jh_mail_addmail_selector, "", "");
		titleBar.setOnClickTitleListener(this);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);

		check = spf.getBoolean(Constant.IS_NETWORK, false);

		lv_view = (ListView) findViewById(R.id.lv_view);
		tv_tishi = (TextView) findViewById(R.id.tv_tishi);
		// lv_view.setonRefreshListener(this);
		// lv_view.SetOnMyListViewScroll(this);
		lv_view.setOverScrollMode(View.OVER_SCROLL_NEVER);
		lv_view.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long id) { // id=position-headerView
				System.out.println("arg2==" + arg2);
				delid = bannerList.get(arg2).getId();
				System.out.println("id ==" + id);
				// int realPosition = (int) id;
				// delid = bannerList.get(realPosition).getId();
				System.out.println("delid ==" + delid);
				logoutpopu();
				return false;
			}
		});

		getData();
		initListView();
	}

	private void getData() {
		dialogHelper.startProgressDialog();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("sign", HttpUtil.getSign(spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.MAILLIST, params, BaseData.class, null, emailsuccessListener(), errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> emailsuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {
						JSONArray s = JSON.parseArray(new String(b));
						System.out.println("返回数据" + s);
						List<EmailBean> emaillist = JSON.parseArray(new String(b), EmailBean.class);
						bannerList = emaillist;
						// byte[] b = Base64.decode(response.data);
						System.out.println(" 返回数据" + s);
						initListView();
					} else {
						ToastManage.showToast(response.desc);
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

	private void initListView() {
		if (bannerList.size() == 0) {
			tv_tishi.setText("暂无邮箱");
		} else {
			tv_tishi.setText("*长按可删除邮箱");
			mAdapter = new LVMailListAdapter(MailActivity.this, check, bannerList);
			lv_view.setAdapter(mAdapter);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
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
		Intent intent = new Intent(this, AddMailActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	private void logoutpopu() {
		final CustomDialog dialog = new CustomDialog(this, R.style.custom_dialog_style, R.layout.custom_login_dialog_layout);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		dialog.show();
		((TextView) dialog.findViewById(R.id.tv_dialog_content)).setText("确定删除该邮箱吗？");
		((TextView) dialog.findViewById(R.id.tv_dialog_content)).setMinLines(1);
		((TextView) dialog.findViewById(R.id.tv_ok)).setText("确定");
		dialog.findViewById(R.id.tv_ok).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				delHttp();
				dialog.dismiss();

			}

		});
		dialog.findViewById(R.id.tv_cancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	private void delHttp() {
		dialogHelper.startProgressDialog();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("id", delid);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.DELMAIL, params, BaseData.class, null, delsuccessListener(), errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> delsuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					ToastManage.showToast("删除成功");
					getData();
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
		getData();

	}

}
