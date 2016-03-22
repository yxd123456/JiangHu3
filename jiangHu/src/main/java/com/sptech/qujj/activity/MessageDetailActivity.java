package com.sptech.qujj.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DateManage;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 消息详情
 * 
 * @author gusonglei
 * 
 */
public class MessageDetailActivity extends BaseActivity implements OnClickTitleListener, OnClickListener {

	private TitleBar titleBar;
	private int message_id;
	private SharedPreferences spf;
	private TextView tv_titlee, tv_data, tv_content;
	private DialogHelper dialogHelper;
	private String readtime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.messagedetail_layout);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("消息详情", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		tv_titlee = (TextView) findViewById(R.id.tv_titlee);
		tv_data = (TextView) findViewById(R.id.tv_data);
		tv_content = (TextView) findViewById(R.id.tv_content);
		readtime = getIntent().getStringExtra("readtime");
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		message_id = getIntent().getIntExtra("message_id", 0);
		dialogHelper.startProgressDialog();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("message_id", message_id);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.MESSAGEDETAIL, params, BaseData.class, null, messagedetailsuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> messagedetailsuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {
						// JSONArray s = JSON.parseArray(new String(b));
						// JSONObject json = s.getJSONObject(0);
						JSONObject json = JSON.parseObject(new String(b));
						System.out.println(json);
						String title = json.getString("title");
						String content = json.getString("content");
						int addtime = json.getIntValue("addtime");
						tv_titlee.setText(title);
						tv_data.setText(DateManage.StringToDateymd(String.valueOf(addtime)));
						tv_content.setText(content);
						if (readtime != null) {
							if (readtime.equals("0")) {
								readmsg();
							}
						} else {
							readmsg();
						}
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

	@Override
	public void onClick(View v) {

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

	private void readmsg() {
		// dialogHelper.startProgressDialog();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("message_id", message_id);
		data.put("is_read", 1);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.READMESSAGE, params, BaseData.class, null, readmsgsuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> readmsgsuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				// dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					// ToastManage.showToast("已阅读");
				} else {
					ToastManage.showToast(response.desc);
				}
			}

		};
	}

}
