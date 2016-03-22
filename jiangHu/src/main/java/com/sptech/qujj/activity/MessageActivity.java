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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.adapter.LVMessageAdapter;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.MessageBean;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

public class MessageActivity extends BaseActivity implements OnClickTitleListener, OnClickListener {

	private TitleBar titleBar;

	private ListView lv_message;// 消息列表

	private RelativeLayout rela_xitong, rela_sixin, rela_nodata;// 系统，私信, 无数据页面
	private TextView tv_xitong, tv_sixin;// 系统消息，私信消息
	private ImageView image_left, image_right;//
	private SharedPreferences spf;
	private LVMessageAdapter mAdapter; // 项目列表 adapter
	private List<MessageBean> msgList = new ArrayList<MessageBean>();// banner组
	private boolean isSysmsg = true;
	private DialogHelper dialogHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_home_message);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		lv_message = (ListView) findViewById(R.id.lv_message);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("消息通知", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		rela_xitong = (RelativeLayout) findViewById(R.id.rela_xitong);
		rela_sixin = (RelativeLayout) findViewById(R.id.rela_sixin);
		tv_xitong = (TextView) findViewById(R.id.tv_xitong);
		tv_sixin = (TextView) findViewById(R.id.tv_sixin);
		image_left = (ImageView) findViewById(R.id.image_left);
		image_right = (ImageView) findViewById(R.id.image_right);

		rela_xitong.setOnClickListener(this);
		rela_sixin.setOnClickListener(this);
		rela_nodata = (RelativeLayout) findViewById(R.id.rela_nodata);

		// mAdapter = new LVMessageAdapter(this, msgList,);
		// lv_message.setAdapter(mAdapter);
		getMessage();

	}

	private void getMessage() {
		dialogHelper.startProgressDialog();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("sign", HttpUtil.getSign(spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.MESSAGELIST, params, BaseData.class, null, messagesuccessListener(), errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> messagesuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {
						JSONArray s = JSON.parseArray(new String(b));
						System.out.println("消息list————" + new String(b));
						List<MessageBean> banklist = JSON.parseArray(new String(b), MessageBean.class);
						msgList.clear();

						if (banklist.size() > 0) {
							if (isSysmsg) {
								for (int i = 0; i < banklist.size(); i++) {
									if (banklist.get(i).getIs_type() == 0) {
										msgList.add(banklist.get(i));
									}
								}
							} else {
								for (int i = 0; i < banklist.size(); i++) {
									if (banklist.get(i).getIs_type() == 1) {
										msgList.add(banklist.get(i));
									}
								}
							}
						}
						mAdapter = new LVMessageAdapter(MessageActivity.this, msgList);
						lv_message.setAdapter(mAdapter);

						if (msgList.size() == 0) {
							rela_nodata.setVisibility(View.VISIBLE);
							lv_message.setVisibility(View.GONE);
						} else {
							rela_nodata.setVisibility(View.GONE);
							lv_message.setVisibility(View.VISIBLE);
						}

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
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rela_xitong: // 系统消息
			image_left.setVisibility(View.VISIBLE);
			image_right.setVisibility(View.GONE);
			tv_xitong.setTextColor(getResources().getColor(R.color.main_color));
			tv_sixin.setTextColor(getResources().getColor(R.color.text_main));
			isSysmsg = true;
			getMessage();
			break;
		case R.id.rela_sixin: // 私信
			image_left.setVisibility(View.GONE);
			image_right.setVisibility(View.VISIBLE);
			tv_sixin.setTextColor(getResources().getColor(R.color.main_color));
			tv_xitong.setTextColor(getResources().getColor(R.color.text_main));
			isSysmsg = false;
			getMessage();
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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getMessage();
	}

}
