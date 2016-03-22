package com.sptech.qujj.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.igexin.sdk.PushManager;
import com.sptech.qujj.GuideActivity;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.db.SPHelper;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.dialog.DialogNetwork;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.AppUtil;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.EventHandleListener;
import com.sptech.qujj.view.OnChangedListener;
import com.sptech.qujj.view.SlipButton;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;
import com.sptech.qujj.view.VersionUpdateDialog;

public class SettingActivity extends BaseActivity implements OnClickListener, OnClickTitleListener, OnChangedListener {

	private TitleBar titleBar;
	private RelativeLayout rela_message, rela_tuisong, rela_bill, rela_guide, rela_feedback, rela_aboutus, rela_checkupdate; // 提醒设置，消息推送设置，智能收取账单设置，新手引导页，意见反馈，关于我们；

	private SlipButton slipbtn_tuisong, slipbtn_zhangdan, slipbtn_net;// 消息推送开关按钮，智能收取账单开关按钮,2G/3G/4G网络提醒；
	private SharedPreferences spf;
	private int isopenzhangdan;
	private DialogHelper dialogHelper;
	private int ispush;
	private TextView tv_update;
	private String verionUpdateUrl;
	private int is_update;
	private DownloadManager downloadManager;

	private final int ACTION_REFERSH = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_slidebar_setting);
		initView();
		Tools.addActivityList(this);
	}

	private void initView() {
		downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		dialogHelper = new DialogHelper(this);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("设置", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		rela_message = (RelativeLayout) findViewById(R.id.rela_message);
		rela_guide = (RelativeLayout) findViewById(R.id.rela_guide);
		rela_feedback = (RelativeLayout) findViewById(R.id.rela_feedback);
		rela_aboutus = (RelativeLayout) findViewById(R.id.rela_aboutus);
		rela_checkupdate = (RelativeLayout) findViewById(R.id.rela_checkupdate);
		tv_update = (TextView) findViewById(R.id.tv_update);

		if (SPHelper.getUpdate()) {
			tv_update.setText("有更新");
			tv_update.setTextColor(getResources().getColor(R.color.text_red));
			rela_checkupdate.setOnClickListener(this);
		} else {
			System.out.println("没有更新== ");
			rela_checkupdate.setEnabled(false);
		}
		slipbtn_tuisong = (SlipButton) findViewById(R.id.slipbtn_tuisong);
		slipbtn_zhangdan = (SlipButton) findViewById(R.id.slipbtn_zhangdan);
		slipbtn_net = (SlipButton) findViewById(R.id.slipbtn_net);

		slipbtn_net.setChecked(spf.getBoolean(Constant.IS_NETWORK, true));
		// slipbtn_net.setChecked(false);
		System.out.println("初始化 == " + spf.getBoolean(Constant.IS_NETWORK, false));
		slipbtn_net.SetOnChangedListener("", new OnChangedListener() {
			@Override
			public void OnChanged(String strName, boolean CheckState) {
				if (CheckState) {//
					// slipbtn_net.setChecked(spf.getBoolean(Constant.IS_OPENGETUI,
					// false));
					spf.edit().putBoolean(Constant.IS_NETWORK, true).commit();
					ToastManage.showToast("开启成功");
				} else {
					DialogNetwork dr = new DialogNetwork(SettingActivity.this, 1, new EventHandleListener() {
						@Override
						public void eventRifhtHandlerListener() {
							handler.sendMessage(handler.obtainMessage(ACTION_REFERSH, false));
						}

						@Override
						public void eventLeftHandlerListener() {
							// slipbtn_net.setChecked(false);
							handler.sendMessage(handler.obtainMessage(ACTION_REFERSH, true));
						}
					});
					dr.createMyDialog();
				}
			}
		});

		slipbtn_tuisong.setChecked(spf.getBoolean(Constant.IS_OPENGETUI, false));
		slipbtn_tuisong.SetOnChangedListener("", new OnChangedListener() {
			@Override
			public void OnChanged(String strName, boolean CheckState) {
				if (CheckState) {// 1是不推送 0是推送
					ispush = 0;
					isPush(ispush);
				} else {
					ispush = 1;
					isPush(ispush);
				}
			}
		});

		slipbtn_zhangdan.SetOnChangedListener("", this);
		boolean isopenzd = spf.getBoolean(Constant.IS_OPENZHANGDAN, false);
		slipbtn_zhangdan.setChecked(isopenzd);
		if (isopenzd) {
			isopenzhangdan = 0;
		} else {
			isopenzhangdan = 1;
		}
		rela_message.setOnClickListener(this);
		rela_guide.setOnClickListener(this);
		rela_feedback.setOnClickListener(this);
		rela_aboutus.setOnClickListener(this);

		// getZdType();
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ACTION_REFERSH:
				boolean flag = (boolean) msg.obj;
				slipbtn_net.setChecked(flag);
				System.out.println("flag == " + flag);
				if (!flag) {
					spf.edit().putBoolean(Constant.IS_NETWORK, false).commit();
					ToastManage.showToast("关闭成功");
				}
				break;
			default:
				break;
			}
		}
	};

	private void isPush(int is_push) {
		dialogHelper.startProgressDialog();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("is_push", is_push);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.SETTINGPUSH, params, BaseData.class, null, ispushSuccessListener, errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> ispushSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				if (ispush == 0) {
					PushManager.getInstance().initialize(getApplicationContext());
					spf.edit().putBoolean(Constant.IS_OPENGETUI, true).commit();
				} else {
					PushManager.getInstance().stopService(getApplicationContext());
					spf.edit().putBoolean(Constant.IS_OPENGETUI, false).commit();
				}
			} else {
				ToastManage.showToast(response.desc);
			}

		}
	};

	// private void getZdType() {
	//
	// dialogHelper.startProgressDialog();
	// HashMap<String, String> params = new HashMap<String, String>();
	// params.put("uid", spf.getString(Constant.UID, "").toString());
	// params.put("sign", HttpUtil.getSign(spf.getString(Constant.UID,
	// "").toString(), spf.getString(Constant.KEY, "").toString()));
	// HttpVolleyRequest<BaseData> request = new
	// HttpVolleyRequest<BaseData>(this, false);
	// request.HttpVolleyRequestPost(false, JsonConfig.GETREMINDSET, params,
	// BaseData.class, null, getalertSuccessListener, errorListener());
	//
	// }
	// @SuppressWarnings("rawtypes")
	// private Listener<BaseData> getalertSuccessListener = new
	// Listener<BaseData>() {
	//
	// @Override
	// public void onResponse(BaseData response) {
	// dialogHelper.stopProgressDialog();
	// if (response.code.equals("0")) {
	// //
	// {"remind_repayment_help":0,"remind_repayment":282600,"remind_beoverdue":0,"user_id":3}
	//
	// byte[] b = Base64.decode(response.data);
	// JSONObject s = JSON.parseObject(new String(b));
	// System.out.println("提醒设置——》" + s);
	// int is_auto = s.getIntValue("is_auto");
	// if(is_auto==0){
	// //未开启
	// slipbtn_zhangdan.setChecked(false);
	// isopenzhangdan = 1;
	// }else{
	// //开启
	// slipbtn_zhangdan.setChecked(true);
	// isopenzhangdan = 0;
	// }
	//
	// } else {
	// ToastManage.showToast(response.desc);
	// }
	// }
	// };
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rela_message: // 消息设置
			Intent intent = new Intent(this, AlertSettingActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;

		case R.id.rela_guide: // 新手引导
			Intent intent4 = new Intent(this, GuideActivity.class);
			intent4.putExtra("from", "no");
			startActivity(intent4);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rela_feedback: // 意见反馈
			Intent intent5 = new Intent(this, FeedBackActivity.class);
			startActivity(intent5);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rela_aboutus: // 关于我们
			Intent intent6 = new Intent(this, AboutUsActivity.class);
			startActivity(intent6);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rela_checkupdate:// 检查更新
			checkUpdate();
			break;
		default:
			break;
		}

	}

	// 检查更新
	private void checkUpdate() {
		// dialogHelper.startProgressDialog();
		// dialogHelper.setDialogMessage("正在检查版本，请稍候...");
		// the below codes is just for test
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("versioncode", AppUtil.getVersionCode(this));
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.AUTOUPDATE, params, BaseData.class, null, checkSuccessListener(), errorListener());
	}

	private Listener<BaseData> checkSuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				// dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {

						JSONObject s = JSON.parseObject(new String(b));
						System.out.println("更新数据-=== " + s);
						is_update = s.getIntValue("is_update");// 0,无需更新 1.普通更新
						                                       // 2，强制更新
						int versioncode = s.getIntValue("versioncode");
						String versionname = s.getString("versionname");
						verionUpdateUrl = s.getString("downloadurl");
						if (is_update == 0) {
							// 再更新数据
							SPHelper.setUpdate(false);
						} else {
							SPHelper.setUpdate(true);
							createVersionInfoDialog();
						}

					}
				} else {
					ToastManage.showToast(response.desc);
				}
			}

		};
	}

	private void createVersionInfoDialog() {
		VersionUpdateDialog dr = new VersionUpdateDialog(SettingActivity.this, is_update, new EventHandleListener() {
			@Override
			public void eventRifhtHandlerListener() {
				// 更新按钮
				// createDownloadInfoDialog();
				if (!"".equals(verionUpdateUrl)) {
					HttpUtil.updateApk(SettingActivity.this, verionUpdateUrl, "", downloadManager);
				}
			}

			@Override
			public void eventLeftHandlerListener() {
				// 取消按钮(2 表示强制更新,取消就退出)
				if (is_update == 1) {
				} else {
				}
			}
		});
		dr.createMyDialog();
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
	public void OnChanged(String strName, boolean CheckState) {
		if (CheckState) {
			isopenzhangdan(isopenzhangdan);
			spf.edit().putBoolean(Constant.IS_OPENZHANGDAN, true).commit();
		} else {
			isopenzhangdan(isopenzhangdan);
			spf.edit().putBoolean(Constant.IS_OPENZHANGDAN, false).commit();
		}

	}

	private void isopenzhangdan(int isauto) {
		dialogHelper.startProgressDialog();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("is_auto", isauto);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.OPENMAILRECORD, params, BaseData.class, null, zhangdanSuccessListener, errorListener());

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> zhangdanSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				if (isopenzhangdan == 0) {
					ToastManage.showToast("关闭成功");
					isopenzhangdan = 1;
					spf.edit().putBoolean(Constant.IS_OPENZHANGDAN, false).commit();
				} else {
					ToastManage.showToast("开启成功");
					isopenzhangdan = 0;
					spf.edit().putBoolean(Constant.IS_OPENZHANGDAN, true).commit();
				}
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

}
