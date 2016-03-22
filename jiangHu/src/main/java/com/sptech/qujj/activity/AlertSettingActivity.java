package com.sptech.qujj.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogAlert;
import com.sptech.qujj.dialog.DialogAlert1;
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
import com.sptech.qujj.view.EventAlertListener;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 提醒设置
 * 
 * @author gusonglei
 * 
 */

public class AlertSettingActivity extends BaseActivity implements OnClickListener, OnClickTitleListener {

	private TitleBar titleBar;
	private RelativeLayout rela_huanset, rela_outtime, rela_repay;
	private TextView tv_time, tv_outtimeset, tv_repaytime;
	private SharedPreferences spf;
	private int remind_repayment, remind_beoverdue, remind_repayment_help;
	private DialogHelper dialogHelper;
	private int repayment = 0, beoverdue = 0, repayment_help = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_set_alertsetting);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("提醒设置", R.drawable.jh_back_selector, 0, "", "");
		rela_huanset = (RelativeLayout) findViewById(R.id.rela_huanset);
		rela_outtime = (RelativeLayout) findViewById(R.id.rela_outtime);
		rela_repay = (RelativeLayout) findViewById(R.id.rela_repay);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_outtimeset = (TextView) findViewById(R.id.tv_outtimeset);
		tv_repaytime = (TextView) findViewById(R.id.tv_repaytime);
		titleBar.setOnClickTitleListener(this);
		rela_huanset.setOnClickListener(this);
		rela_outtime.setOnClickListener(this);
		rela_repay.setOnClickListener(this);
		getTixing();
	}

	private void getTixing() {
		dialogHelper.startProgressDialog();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("sign", HttpUtil.getSign(spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(false, JsonConfig.GETREMINDSET, params, BaseData.class, null, getalertSuccessListener, errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> getalertSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				// {"remind_repayment_help":0,"remind_repayment":282600,"remind_beoverdue":0,"user_id":3}

				byte[] b = Base64.decode(response.data);
				if (b != null && !b.equals("")) {
					JSONObject s = JSON.parseObject(new String(b));
					System.out.println("提醒设置——》" + s);
					repayment = s.getIntValue("remind_repayment");
					beoverdue = s.getIntValue("remind_beoverdue");
					repayment_help = s.getIntValue("remind_repayment_help");
					if (repayment == 0) {
						tv_time.setText("未设置");
					} else {
						tv_time.setText("提前" + DateManage.getDHM(repayment));
					}
					if (beoverdue == 0) {
						tv_outtimeset.setText("未设置");
					} else {
						tv_outtimeset.setText("逾期后一天" + DateManage.getHM(beoverdue));
					}
					if (repayment_help == 0) {
						tv_repaytime.setText("未设置");
					} else {
						tv_repaytime.setText("提前" + DateManage.getDHM(repayment_help));
					}
				}
			} else {
				ToastManage.showToast(response.desc);
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rela_huanset:
			int dd = repayment / 86400;
			int hh = repayment % 86400 / 3600;
			int mm = repayment % 86400 % 3600 / 60;
			DialogAlert da = new DialogAlert(this, dd, hh, mm, new EventAlertListener() {

				@Override
				public void eventDataHandlerListener(int miao, String data) {
					tv_time.setText("提前" + data);
					remind_repayment = miao;
					repayment = miao;
					httpSetAlert(0);
				}
			});

			da.createMyDialog();
			break;
		case R.id.rela_outtime:
			int hh2 = beoverdue % 86400 / 3600;
			int mm2 = beoverdue % 86400 % 3600 / 60;
			DialogAlert1 da1 = new DialogAlert1(this, hh2, mm2, new EventAlertListener() {

				@Override
				public void eventDataHandlerListener(int miao, String data) {
					tv_outtimeset.setText("逾期后一天" + data);
					remind_beoverdue = miao;
					beoverdue = miao;
					httpSetAlert(1);
				}
			});
			da1.createMyDialog();
			break;
		case R.id.rela_repay:
			int dd1 = repayment_help / 86400;
			int hh1 = repayment_help % 86400 / 3600;
			int mm1 = repayment_help % 86400 % 3600 / 60;
			DialogAlert da2 = new DialogAlert(this, dd1, hh1, mm1, new EventAlertListener() {

				@Override
				public void eventDataHandlerListener(int miao, String data) {
					tv_repaytime.setText("提前" + data);
					remind_repayment_help = miao;
					repayment_help = miao;
					httpSetAlert(2);
				}
			});
			da2.createMyDialog();
			break;
		default:
			break;
		}

	}

	private void httpSetAlert(int settype) {
		dialogHelper.startProgressDialog();
		HashMap<String, Object> data = new HashMap<String, Object>();
		if (settype == 0) {
			data.put("remind_repayment", remind_repayment);// 还款提醒设置，秒数
		} else if (settype == 1) {
			data.put("remind_beoverdue", remind_beoverdue);// 逾期提醒设置，秒数
		} else if (settype == 2) {
			data.put("remind_repayment_help", remind_repayment_help);//
		}
		// 代还款提醒设置，秒数
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(false, JsonConfig.SETREMIND, params, BaseData.class, null, setalertSuccessListener, errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> setalertSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				ToastManage.showToast("设置成功");
				// Toast.makeText(CLoginPwdActivity.this, "修改登录密码成功",
				// Toast.LENGTH_SHORT).show();
				// CLoginPwdActivity.this.finish();

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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getTixing();
	}

}
