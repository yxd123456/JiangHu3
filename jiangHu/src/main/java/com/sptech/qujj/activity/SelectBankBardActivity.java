package com.sptech.qujj.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.baidu.mapapi.utils.route.RouteParaOption.EBusStrategyType;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.R.anim;
import com.sptech.qujj.R.drawable;
import com.sptech.qujj.R.id;
import com.sptech.qujj.R.layout;
import com.sptech.qujj.R.style;
import com.sptech.qujj.adapter.CardListAdapter;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.basic.BaseFragment;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.CustomDialog;
import com.sptech.qujj.dialog.DialogActivity;
import com.sptech.qujj.dialog.DialogChangeStatus;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.CardInfo;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.view.EventHandleListener;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 储蓄卡列表
 * 
 * @author gusonglei
 * 
 */

public class SelectBankBardActivity extends BaseActivity implements OnClickListener {
	private ListView lv_view; // 进度列表
	private CardListAdapter mAdapter; //
	private ProgressBar progress_loadmore;
	private List<CardInfo> bankList = new ArrayList<CardInfo>();
	private SharedPreferences spf;
	private TextView tv_addcard;
	private RelativeLayout rl_nocard;
	private int delid;
	private DialogHelper dialogHelper;
	private TitleBar titleBar;

	HashMap<String, String> bluecardMap = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select_bank_card);
		initView();
		initListView();
	}

	protected void initView() {
		dialogHelper = new DialogHelper(this);
		
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("选择储蓄卡", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(new OnClickTitleListener() {
					
					@Override
					public void onRightButtonClick(View view) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onLeftButtonClick(View view) {
						// TODO Auto-generated method stub
						ActivityJumpManager.finishActivity(SelectBankBardActivity.this, 1);
					}
				});
		
		spf = this.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);

		lv_view = (ListView) findViewById(R.id.lv_view);
		//registerForContextMenu(lv_view);
		tv_addcard = (TextView) findViewById(R.id.tv_addcard);
		rl_nocard = (RelativeLayout) findViewById(R.id.rl_nocard);
		// lv_view.setonRefreshListener(this);
		// lv_view.SetOnMyListViewScroll(this);
		tv_addcard.setOnClickListener(this);
		lv_view.setOverScrollMode(View.OVER_SCROLL_NEVER);
		lv_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				final CardInfo eb = bankList.get(position);
				ImageView iv = (ImageView) view.findViewById(R.id.iv_gou);
				iv.setImageResource(R.drawable.btn_bankcard_select);
				Intent i = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("tv_bank", eb.getCard_bank());
				bundle.putString("card_no", eb.getCard_no());
				bundle.putInt("credit_id", eb.getBankcard_id());
				i.putExtras(bundle);
				setResult(RESULT_OK, i);
				finish();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		getData();
	}

	private void getData() {
		dialogHelper.startProgressDialog();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("card_type", 1);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.BANKCARDLIST, params, BaseData.class, null, emailsuccessListener(), errorListener());

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
						System.out.println("返回数据" + new String(b));
						List<CardInfo> banklist = JSON.parseArray(new String(b), CardInfo.class);
						bankList = banklist;
						initListView();
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
		// 获取存储的本地的 支持的银行卡
		if (bankList.size() == 0) {
			lv_view.setVisibility(View.GONE);
			rl_nocard.setVisibility(View.VISIBLE);
		} else {
			lv_view.setVisibility(View.VISIBLE);
			rl_nocard.setVisibility(View.GONE);
			// List<UsablebankBean> usablebanklist = JSON.parseArray(
			// spf.getString(Constant.SUPPORTBANK, ""),
			// UsablebankBean.class);
			// 将解析的银行卡List 转换为Map (银行卡名称，图标流)
			bluecardMap = DataFormatUtil.getBlueCardMap(spf);
			// if (usablebanklist.size() != 0) {
			// for (int i = 0; i < usablebanklist.size(); i++) {
			// cardMap.put(usablebanklist.get(i).getName(), usablebanklist
			// .get(i).getStream());
			// }
			// }
			mAdapter = new CardListAdapter(this, bluecardMap, bankList, lv_view);
			lv_view.setAdapter(mAdapter);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_addcard:
			Intent intent = new Intent(this, AddMailActivity.class);
			startActivity(intent);
			this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		default:
			break;
		}

	}

	private void logoutpopu() {
		final CustomDialog dialog = new CustomDialog(this, R.style.custom_dialog_style, R.layout.custom_login_dialog_layout);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		dialog.show();
		((TextView) dialog.findViewById(R.id.tv_dialog_content)).setText("确定删除该项吗？");
		((TextView) dialog.findViewById(R.id.tv_dialog_content)).setMinLines(1);
		// ((TextView) dialog.findViewById(R.id.tv_ok)).setText("确定");
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
		data.put("bankcard_id", delid);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.DELETEBANKCARD, params, BaseData.class, null, delsuccessListener(), errorListener());

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
					// byte[] b = Base64.decode(response.data);
					// JSONArray s = JSON.parseArray(new String(b));
					// System.out.println("返回数据" + new String(b));
					// List<BankcardBean> banklist = JSON.parseArray(new
					// String(b), BankcardBean.class);
					// bankList = banklist;
					// initListView();
				} else {
					ToastManage.showToast(response.desc);
				}
			}

		};
	}
	
	
	
}
