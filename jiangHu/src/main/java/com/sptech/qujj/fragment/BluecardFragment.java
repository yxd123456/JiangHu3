package com.sptech.qujj.fragment;

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
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
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
import com.sptech.qujj.activity.AddMailActivity;
import com.sptech.qujj.activity.CreditcardActivity;
import com.sptech.qujj.adapter.BankBlueListAdapter;
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
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.view.EventHandleListener;

/**
 * 信用卡列表
 * 
 * @author gusonglei
 * 
 */

public class BluecardFragment extends BaseFragment implements OnClickListener {
	private ListView lv_view; // 进度列表
	private BankBlueListAdapter mAdapter; //
	private ProgressBar progress_loadmore;
	private List<CardInfo> bankList = new ArrayList<CardInfo>();
	private SharedPreferences spf;
	private TextView tv_addcard;
	private RelativeLayout rl_nocard;
	private int delid;
	private DialogHelper dialogHelper;

	HashMap<String, String> bluecardMap = new HashMap<String, String>();

	@Override
	protected View createFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "createFragmentView");
		return inflater.inflate(R.layout.bankcard_layout, container, false);
	}

	protected void initView() {
		dialogHelper = new DialogHelper(getActivity());
		spf = getActivity().getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);

		lv_view = (ListView) selfView.findViewById(R.id.lv_view);
		//registerForContextMenu(lv_view);
		tv_addcard = (TextView) selfView.findViewById(R.id.tv_addcard);
		rl_nocard = (RelativeLayout) selfView.findViewById(R.id.rl_nocard);
		// lv_view.setonRefreshListener(this);
		// lv_view.SetOnMyListViewScroll(this);
		tv_addcard.setOnClickListener(this);
		lv_view.setOverScrollMode(View.OVER_SCROLL_NEVER);
		lv_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				// int bankid = bankList.get(arg2).getId();
				// Intent intent3 = new Intent(getActivity(),
				// HtmlActivity.class);
				// intent3.putExtra("act", "credit_card_disp");
				// intent3.putExtra("bill_id", bankid);
				// startActivity(intent3);
				// getActivity().overridePendingTransition(R.anim.push_left_in,
				// R.anim.push_left_out);

				Intent intent3 = new Intent(getActivity(), CreditcardActivity.class);
				intent3.putExtra("cardInfo", bankList.get(arg2));
				startActivity(intent3);
				getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

			}
		});
		 lv_view.setOnItemLongClickListener(new OnItemLongClickListener() {
		
		 @Override
		 public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
		 final int arg2, long arg3) {
		 // String str = bankList.get(arg2).get("name");
		 // delid = bankList.get(arg2-1).getBankcard_id();
		/*delid = bankList.get(arg2).getBankcard_id();
		 System.out.println("arg2  == "+arg2);
		 System.out.println("delid == "+delid);
		 logoutpopu();*/
			 
	     
		 new DialogChangeStatus(getActivity(), 4, new EventHandleListener() {
			
			@Override
			public void eventRifhtHandlerListener() {
				// TODO Auto-generated method stub
				CardInfo eb = bankList.get(arg2);
				dialogHelper.startProgressDialog();
				HashMap<String, String> params = new HashMap<String, String>();
				HashMap<String, Object> data = new HashMap<String, Object>();
				data.put("credit_id", eb.getId());
				data.put("is_del", 2);
				params.put("uid", spf.getString(Constant.UID, ""));
				params.put("data", HttpUtil.getData(data));
				params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
				HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(getActivity(), false);
				request.HttpVolleyRequestPost(JsonConfig.DELETE_CREDIT_CARD, params, BaseData.class, null, emailsuccessListener(), errorListener());
				getData();
				initListView();
			}
			
			@Override
			public void eventLeftHandlerListener() {
				// TODO Auto-generated method stub
				CardInfo eb = bankList.get(arg2);
				dialogHelper.startProgressDialog();
				HashMap<String, String> params = new HashMap<String, String>();
				HashMap<String, Object> data = new HashMap<String, Object>();
				data.put("credit_id", eb.getId());
				data.put("is_del", 1);
				params.put("uid", spf.getString(Constant.UID, ""));
				params.put("data", HttpUtil.getData(data));
				params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
				HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(getActivity(), false);
				request.HttpVolleyRequestPost(JsonConfig.DELETE_CREDIT_CARD, params, BaseData.class, null, emailsuccessListener(), errorListener());
				getData();
				initListView();
			}
		}).createMyDialog();
		
		 // System.out.println(bankList.get(arg2 - 1).getBankcard_id());
		 	
		
		 return true;
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
		// HashMap<String, Object> data = new HashMap<String, Object>();
		// data.put("card_type", 2);
		// HashMap<String, String> params = new HashMap<String, String>();
		// params.put("uid", spf.getString(Constant.UID, "").toString());
		// params.put("data", HttpUtil.getData(data));
		// params.put("sign", HttpUtil.getSign(data,
		// spf.getString(Constant.UID, "").toString(),
		// spf.getString(Constant.KEY, "").toString()));
		// HttpVolleyRequest<BaseData> request = new
		// HttpVolleyRequest<BaseData>(
		// getActivity(), false);
		// request.HttpVolleyRequestPost(JsonConfig.BANKCARDLIST, params,
		// BaseData.class, null, emailsuccessListener(), errorListener());
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));

		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(getActivity(), false);
		request.HttpVolleyRequestPost(JsonConfig.CARD_LIST, params, BaseData.class, null, emailsuccessListener(), errorListener());

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
			mAdapter = new BankBlueListAdapter(getActivity(), bluecardMap, bankList);
			lv_view.setAdapter(mAdapter);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_addcard:
			Intent intent = new Intent(getActivity(), AddMailActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		default:
			break;
		}

	}

	private void logoutpopu() {
		final CustomDialog dialog = new CustomDialog(getActivity(), R.style.custom_dialog_style, R.layout.custom_login_dialog_layout);
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
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(getActivity(), false);
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
