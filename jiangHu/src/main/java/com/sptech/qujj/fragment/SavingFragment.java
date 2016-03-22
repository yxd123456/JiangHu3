package com.sptech.qujj.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.activity.AddBankcardActivity;
import com.sptech.qujj.activity.BankCardInfoActivity;
import com.sptech.qujj.activity.UserInfoVerificationActivity;
import com.sptech.qujj.adapter.BankListAdapter;
import com.sptech.qujj.basic.BaseFragment;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.CustomDialog;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BankcardBean;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.HttpUtil;

/**
 * 储蓄卡列表
 * 
 * @author gusonglei
 * 
 */

public class SavingFragment extends BaseFragment implements OnClickListener {
	private ListView lv_view; // 进度列表
	private BankListAdapter mAdapter; //
	private ProgressBar progress_loadmore;
	private List<BankcardBean> bankList = new ArrayList<BankcardBean>();// banner组
	private SharedPreferences spf;
	private TextView tv_addcard;
	private int delid;
	private RelativeLayout rl_nocard;
	private DialogHelper dialogHelper;
	// private List<EmailBean> emaillists;
	HashMap<String, String> savecardMap = new HashMap<String, String>();
	private String user_auth;

	@Override
	protected View createFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "createFragmentView");
		return inflater.inflate(R.layout.savingcard_layout, container, false);
	}

	protected void initView() {
		dialogHelper = new DialogHelper(getActivity());
		spf = getActivity().getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		user_auth = spf.getString(Constant.USER_AUTH, "");
		lv_view = (ListView) selfView.findViewById(R.id.lv_view);
		tv_addcard = (TextView) selfView.findViewById(R.id.tv_addcard);
		rl_nocard = (RelativeLayout) selfView.findViewById(R.id.rl_nocard);
		tv_addcard.setOnClickListener(this);
		// lv_view.setonRefreshListener(this);
		// lv_view.SetOnMyListViewScroll(this);

		// mAdapter = new BankListAdapter(getActivity(), cardMap,bankList);
		lv_view.setAdapter(mAdapter);
		lv_view.setOverScrollMode(View.OVER_SCROLL_NEVER);
		lv_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				// int bankid = bankList.get(arg2).getBankcard_id();
				BankcardBean bankcardBean = bankList.get(arg2);
				System.out.println("arg2 == " + arg2);
				System.out.println("bankcardBean == " + bankcardBean.getCard_bank());
				// Intent intent3 = new Intent(getActivity(),
				// HtmlActivity.class);
				// intent3.putExtra("act", "debit_card_disp");
				// intent3.putExtra("id", bankid);
				// startActivity(intent3);
				// getActivity().overridePendingTransition(R.anim.push_left_in,
				// R.anim.push_left_out);

				Intent intent3 = new Intent(getActivity(), BankCardInfoActivity.class);
				intent3.putExtra("bankcard", bankcardBean);
				// intent3.put("act", "debit_card_disp");
				startActivity(intent3);
				getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});
		// 删除
		lv_view.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// String str = bankList.get(arg2).get("name");
				delid = bankList.get(arg2).getBankcard_id();
				logoutpopu();
				// System.out.println(bankList.get(arg2 - 1).getBankcard_id());
				return true;
			}
		});
		getData();
		// initListView();
	}

	@Override
	public void onResume() {
		super.onResume();
		System.out.println("返回数据== onResume");
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
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(getActivity(), false);
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
					JSONArray s = JSON.parseArray(new String(b));
					System.out.println("返回数据" + new String(b));
					List<BankcardBean> banklist = JSON.parseArray(new String(b), BankcardBean.class);
					bankList = banklist;
					System.out.println("bankList,size " + banklist.size());
					initListView();
				} else {
					initListView();
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
		if (bankList.size() == 0) {
			lv_view.setVisibility(View.GONE);
			rl_nocard.setVisibility(View.VISIBLE);
			tv_addcard.setVisibility(View.VISIBLE);
		} else {
			lv_view.setVisibility(View.VISIBLE);
			rl_nocard.setVisibility(View.GONE);
			// tv_addcard.setVisibility(View.GONE);
			// List<UsablebankBean> usablebanklist = JSON.parseArray(
			// spf.getString(Constant.SUPPORTBANK, ""),
			// UsablebankBean.class);
			// 将解析的银行卡List 转换为Map (银行卡名称，图标流)
			savecardMap = DataFormatUtil.getCardMap(spf);
			// HashMap<String, String> cardMap = new HashMap<String, String>();
			// if (usablebanklist.size() != 0) {
			// for (int i = 0; i < usablebanklist.size(); i++) {
			// cardMap.put(usablebanklist.get(i).getName(), usablebanklist
			// .get(i).getStream());
			// }
			// }
			mAdapter = new BankListAdapter(getActivity(), savecardMap, bankList);
			lv_view.setAdapter(mAdapter);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_addcard:
			if (user_auth.equals("1")) {
				Intent intent = new Intent(getActivity(), AddBankcardActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			} else {
				ToastManage.showToast("您尚未进行实名认证，请先进行实名认证！");
				Intent intent1 = new Intent(getActivity(), UserInfoVerificationActivity.class);
				intent1.putExtra("nextflag", 5);
				startActivity(intent1);
				getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}

			break;
		default:
			break;
		}

	}

	private int visibleLastIndex = 0; // 最后的可视项索引

	// @Override
	// public void onScroll(AbsListView view, int firstVisiableItem, int
	// visibleItemCount, int totalItemCount) {
	// // TODO Auto-generated method stub
	//
	// // visibleLastIndex = firstVisiableItem + visibleItemCount - 1;
	// // boolean loadMore = (firstVisiableItem + visibleItemCount) >=
	// // totalItemCount;
	// // if (loadMore && visibleItemCount != totalItemCount) {
	// // progress_loadmore.setVisibility(View.VISIBLE);
	// // }
	// }
	//
	// @Override
	// public void onScrollStateChanged(AbsListView view, int scrollState) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// // @Override
	// // public void onRefresh() {
	// // // TODO Auto-generated method stub
	// //
	// // }
	private void logoutpopu() {
		final CustomDialog dialog = new CustomDialog(getActivity(), R.style.custom_dialog_style, R.layout.custom_login_dialog_layout);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		dialog.show();
		((TextView) dialog.findViewById(R.id.tv_dialog_content)).setText("确定删除该项吗？");
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
