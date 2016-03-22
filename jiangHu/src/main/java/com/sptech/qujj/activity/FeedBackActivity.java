package com.sptech.qujj.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/*
 * 
 * 意见反馈
 */

public class FeedBackActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {

	private TitleBar titleBar;
	private Button btn_submit;// 提交
	private EditText ed_feed;
	private String content;
	private DialogHelper dialogHelper;

	private TextView tv_telphone;// 拨打电话

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_set_feedback);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("意见反馈", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		tv_telphone = (TextView) findViewById(R.id.tv_telphone);

		btn_submit = (Button) findViewById(R.id.btn_submit);
		tv_telphone.setOnClickListener(this);

		btn_submit.setOnClickListener(this);
		ed_feed = (EditText) findViewById(R.id.ed_feed);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_submit: // 提交
			content = ed_feed.getText().toString().trim();
			if (content.length() == 0) {
				ToastManage.showToast("反馈内容不能为空");
				return;
			}
			dialogHelper.startProgressDialog();
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("content", content);
			HashMap<String, String> params = new HashMap<String, String>();
			// params.put("uid", spf.getString(Constant.UID, "").toString());
			params.put("data", HttpUtil.getData(data));
			params.put("sign", HttpUtil.getSign(data));
			@SuppressWarnings("rawtypes")
			HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
			request.HttpVolleyRequestPost(JsonConfig.FEEDBACK, params, BaseData.class, null, feedSuccessListener, errorListener());
			break;
		case R.id.tv_telphone:
			Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tv_telphone.getText().toString()));
			intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent2);
			break;
		default:
			break;
		}

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> feedSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				ToastManage.showToast("反馈成功");
				finish();
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
