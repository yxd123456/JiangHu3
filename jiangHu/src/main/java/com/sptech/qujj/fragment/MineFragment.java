package com.sptech.qujj.fragment;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.activity.AccountBalanceActivity;
import com.sptech.qujj.activity.FinancialAssetsActivity;
import com.sptech.qujj.activity.IncomeActivity;
import com.sptech.qujj.basic.BaseFragment;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.User;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.HttpUtil;

/**
 * 我的产品-
 * 
 * @author yebr
 * 
 */

public class MineFragment extends BaseFragment implements OnClickListener {

	private RelativeLayout rela_top, rela_mybalance, rela_mymoney;// 累计收益，我的余额，理财资产；
	private TextView tv_total, tv_zuovalue, tv_zongvalue, tv_bavalue, tv_zichan; // 累计收益金额,昨日收益,总资产,我的余额，理财资产

	// private View mView;
	private SharedPreferences spf;
	private DialogHelper dialogHelper;
	private String uid = "";
	private String key = "";
	private User curUser;

	@Override
	protected View createFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.jh_licai_mine, container, false);
	}

	@Override
	protected void initView() {
		dialogHelper = new DialogHelper(getActivity());
		spf = getActivity().getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		uid = spf.getString(Constant.UID, "");
		key = spf.getString(Constant.KEY, "");

		tv_total = (TextView) selfView.findViewById(R.id.tv_total);
		tv_zuovalue = (TextView) selfView.findViewById(R.id.tv_zuovalue);
		tv_zongvalue = (TextView) selfView.findViewById(R.id.tv_zongvalue);
		tv_bavalue = (TextView) selfView.findViewById(R.id.tv_bavalue);
		tv_zichan = (TextView) selfView.findViewById(R.id.tv_zichan);
		rela_mybalance = (RelativeLayout) selfView.findViewById(R.id.rela_mybalance);
		rela_mymoney = (RelativeLayout) selfView.findViewById(R.id.rela_mymoney);

		rela_top = (RelativeLayout) selfView.findViewById(R.id.rela_top);
		rela_mybalance.setOnClickListener(this);
		rela_mymoney.setOnClickListener(this);
		rela_top.setOnClickListener(this);

		getMineData();
	}

	private void initMineData() {
		// 累计收益
		tv_total.setText("¥" + DataFormatUtil.floatsaveTwo(curUser.getProfit_total()));
		// 昨日收益
		tv_zuovalue.setText(DataFormatUtil.floatsaveTwo(curUser.getYseterday_profit()));
		// 总资产 （可用余额+收益）
		// float zong = curUser.getInvestment_wait() + curUser.getProfit_wait()
		// + curUser.getLiabilities_wait();
		float zong = curUser.getProfit_total() + curUser.getUser_money();
		tv_zongvalue.setText(DataFormatUtil.floatsaveTwo(zong));
		// 我的余额
		tv_bavalue.setText(DataFormatUtil.floatsaveTwo(curUser.getUser_money()));
		if (curUser.getCount_product() == 0) {
			tv_zichan.setText("");
		} else {
			tv_zichan.setText(curUser.getCount_product() + "个产品");
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rela_mybalance:
			Intent intent = new Intent(getActivity(), AccountBalanceActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rela_top:
			Intent intent2 = new Intent(getActivity(), IncomeActivity.class);
			intent2.putExtra("total", curUser.getProfit_total());
			startActivity(intent2);
			getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rela_mymoney:
			Intent intent3 = new Intent(getActivity(), FinancialAssetsActivity.class);
			startActivity(intent3);
			getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		default:
			break;
		}
	}

	// 获取我的产品
	public void getMineData() {
		// TODO Auto-generated method stub
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", uid);
		params.put("sign", HttpUtil.getSign(uid, key));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(getActivity(), false);
		request.HttpVolleyRequestPost(JsonConfig.ACCOUNTMONEY, params, BaseData.class, null, successListener(), errorListener());
	}

	// 失败
	@SuppressLint("ShowToast")
	private Response.ErrorListener errorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		};
	}

	// 成功
	@SuppressWarnings("rawtypes")
	private Listener<BaseData> successListener() {
		// TODO Auto-generated method stub
		return new Listener<BaseData>() {
			@Override
			public void onResponse(BaseData response) {
				System.out.println("用户资产 返回code == " + response.code);
				dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					// JSONArray s = JSON.parseArray(new String(b));
					String dataString;
					if (b != null && !b.equals("")) {
						dataString = new String(b);
						System.out.println("我的余额  数据=" + dataString);
						if (dataString != null) {
							System.out.println("返回数据" + dataString);
							// JSONObject s = JSON.parseObject(dataString);
							User user = new User();
							user = JSON.parseObject(dataString, User.class);
							curUser = user;// count_product
							initMineData();
							// tv_total.setText(curUser.get);
							// tv_zuovalue, tv_zong, tv_bavalue, tv_zichan
						}

					}
				} else {
					ToastManage.showToast(response.desc);

				}
			}
		};
	}

}
