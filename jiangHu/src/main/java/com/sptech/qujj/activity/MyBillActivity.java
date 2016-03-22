package com.sptech.qujj.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sptech.qujj.R;
import com.sptech.qujj.adapter.MyBillAdapter;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.MyBillBean;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;

/**
 * 我的账单
 * 
 * @author gusonglei
 * 
 * 
 */
public class MyBillActivity extends BaseActivity implements OnClickListener {
	private PullToRefreshListView listView;

	private MyBillAdapter myBillAdapter;
	private List<MyBillBean> blist = new ArrayList<MyBillBean>();

	private Button btn_left; // 返回按钮

	private RelativeLayout title_view, relative_all; // title,全部明细;

	private RelativeLayout rela_all, rela_income, rela_spend, rela_balance; // 下拉：全部明细，收入明细，支出明细,余额账单;
	private ImageView image_all, image_income, image_spend, image_balance; // 下拉菜单：三个图片
	private TextView tv_all, tv_income, tv_spend, tv_title, tv_balance;

	private ImageView img_all;// 全部明细的图片
	private SharedPreferences spf;
	private View popupWindowView;
	private PopupWindow popupWindow;
	private LayoutInflater inflater;
	DialogHelper dialogHelper;// 加载进度条
	private RelativeLayout rl_nocard;
	private int start = 0;
	private static int num = 20;
	private int pressType = 0;

	// Popupwindow 弹出框

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mybill_layout);
		initView();
		Tools.addActivityList(this);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initView() {
		dialogHelper = new DialogHelper(this);
		listView = (PullToRefreshListView) findViewById(R.id.listview_acdetail);
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(new OnRefreshListener2() {

			@SuppressWarnings("rawtypes")
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				start = 0;
				// currentPage = 1;
				//
				// getData(currentPage, "", "");
				if (pressType == 0) {
					getHttpBill("1,2,3,4,5,6", start);
				} else if (pressType == 1) {
					getHttpBill("1,2", start);
				} else if (pressType == 3) {
					getHttpBill("3,4", start);
				} else if (pressType == 5) {
					getHttpBill("5,6", start);
				}
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {

				// currentPage++;
				// getData(currentPage, "", "");
				if (pressType == 0) {
					getHttpBill("1,2,3,4,5,6", start);
				} else if (pressType == 1) {
					getHttpBill("1,2", start);
				} else if (pressType == 3) {
					getHttpBill("3,4", start);
				} else if (pressType == 5) {
					getHttpBill("5,6", start);
				}
			}

		});

		// myBillAdapter = new MyBillAdapter(this, blist);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		title_view = (RelativeLayout) findViewById(R.id.title_view);
		relative_all = (RelativeLayout) findViewById(R.id.relative_all);
		rl_nocard = (RelativeLayout) findViewById(R.id.rl_nocard);
		img_all = (ImageView) findViewById(R.id.img_all);
		tv_title = (TextView) findViewById(R.id.tv_title);
		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		popupWindowView = inflater.inflate(R.layout.mybill_popupwindow, null);

		rela_all = (RelativeLayout) popupWindowView.findViewById(R.id.rela_all);
		rela_income = (RelativeLayout) popupWindowView.findViewById(R.id.rela_income);
		rela_spend = (RelativeLayout) popupWindowView.findViewById(R.id.rela_spend);
		rela_balance = (RelativeLayout) popupWindowView.findViewById(R.id.rela_balance);

		image_all = (ImageView) popupWindowView.findViewById(R.id.image_all);
		image_income = (ImageView) popupWindowView.findViewById(R.id.image_income);
		image_spend = (ImageView) popupWindowView.findViewById(R.id.image_spend);
		image_balance = (ImageView) popupWindowView.findViewById(R.id.image_balance);

		tv_all = (TextView) popupWindowView.findViewById(R.id.tv_all);
		tv_income = (TextView) popupWindowView.findViewById(R.id.tv_income);
		tv_spend = (TextView) popupWindowView.findViewById(R.id.tv_spend);
		tv_balance = (TextView) popupWindowView.findViewById(R.id.tv_balance);

		rela_all.setOnClickListener(this);
		rela_income.setOnClickListener(this);
		rela_spend.setOnClickListener(this);
		rela_balance.setOnClickListener(this);

		myBillAdapter = new MyBillAdapter(MyBillActivity.this, blist);
		listView.setAdapter(myBillAdapter);

		btn_left = (Button) findViewById(R.id.btn_left);
		// listView.setAdapter(myBillAdapter);
		btn_left.setOnClickListener(this);
		relative_all.setOnClickListener(this);
		getHttpBill("1,2,3,4,5,6", start);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_left:
			ActivityJumpManager.finishActivity(this, 1);
			break;
		case R.id.relative_all: // 头部
			changeImagAll();
			break;
		case R.id.rela_all:
			image_all.setBackgroundResource(R.drawable.btn_all_hl);
			image_income.setBackgroundResource(R.drawable.btn_user_billlicai_select);
			image_spend.setBackgroundResource(R.drawable.btn_user_billborrow_select);
			image_balance.setBackgroundResource(R.drawable.btn_user_billbalance_select);
			tv_all.setTextColor(getResources().getColor(R.color.main_color));
			tv_income.setTextColor(getResources().getColor(R.color.text_main));
			tv_spend.setTextColor(getResources().getColor(R.color.text_main));
			tv_balance.setTextColor(getResources().getColor(R.color.text_main));
			tv_title.setText("全部账单");
			pressType = 0;
			start = 0;
			getHttpBill("1,2,3,4,5,6", start);
			changeImagAll();
			break;
		case R.id.rela_income:
			tv_all.setTextColor(getResources().getColor(R.color.text_main));
			tv_income.setTextColor(getResources().getColor(R.color.main_color));
			tv_balance.setTextColor(getResources().getColor(R.color.text_main));
			tv_spend.setTextColor(getResources().getColor(R.color.text_main));
			image_all.setBackgroundResource(R.drawable.btn_all);
			image_income.setBackgroundResource(R.drawable.btn_user_billlicai_unselect);
			image_spend.setBackgroundResource(R.drawable.btn_user_billborrow_select);
			image_balance.setBackgroundResource(R.drawable.btn_user_billbalance_select);
			tv_title.setText("理财账单");
			start = 0;
			pressType = 3;
			getHttpBill("3,4", start);
			changeImagAll();
			break;
		case R.id.rela_spend:
			image_all.setBackgroundResource(R.drawable.btn_all);
			image_income.setBackgroundResource(R.drawable.btn_user_billlicai_select);
			image_balance.setBackgroundResource(R.drawable.btn_user_billbalance_select);
			image_spend.setBackgroundResource(R.drawable.btn_user_billborrow_unselect);
			tv_all.setTextColor(getResources().getColor(R.color.text_main));
			tv_income.setTextColor(getResources().getColor(R.color.text_main));
			tv_balance.setTextColor(getResources().getColor(R.color.text_main));
			tv_spend.setTextColor(getResources().getColor(R.color.main_color));
			tv_title.setText("借款账单");
			start = 0;
			pressType = 5;
			getHttpBill("5,6", start);
			changeImagAll();
			break;
		case R.id.rela_balance:
			tv_title.setText("余额账单");
			image_all.setBackgroundResource(R.drawable.btn_all);
			image_income.setBackgroundResource(R.drawable.btn_user_billlicai_select);
			image_spend.setBackgroundResource(R.drawable.btn_user_billborrow_select);
			image_balance.setBackgroundResource(R.drawable.btn_user_billbalance_unselect);
			tv_all.setTextColor(getResources().getColor(R.color.text_main));
			tv_income.setTextColor(getResources().getColor(R.color.text_main));
			tv_spend.setTextColor(getResources().getColor(R.color.text_main));
			tv_balance.setTextColor(getResources().getColor(R.color.main_color));
			changeImagAll();
			// 查询余额账单 --即 充值 提现的明细
			start = 0;
			pressType = 1;
			getHttpBill("1,2", start);
			break;
		default:
			break;
		}
	}

	private void showPopupwindow() {
		popupWindow = new PopupWindow(popupWindowView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
		// popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(false);
		// 弹出popupwindow
		// 必须要有这句否则弹出popupWindow后监听不到Back键
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.showAsDropDown(title_view);
		// 设置动画
		// popupWindow.setAnimationStyle(R.style.popupWindowAnimation);
		popupWindow.update();
	}

	private void changeImagAll() {
		if (popupWindow != null && popupWindow.isShowing()) {
			img_all.setBackgroundResource(R.drawable.jh_detailtopwhite_bottom_selector);
			popupWindow.dismiss();
		} else {
			img_all.setBackgroundResource(R.drawable.jh_detailtopwhite_top_selector);
			showPopupwindow();
		}
	}

	// 监听Back事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (popupWindow != null && popupWindow.isShowing()) {
				popupWindow.dismiss();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void getHttpBill(String Type, int start) {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		HashMap<String, Object> data = new HashMap<String, Object>();
		// if (Type.equals("0")) {
		// data.put("is_change", 0);
		// } else {
		data.put("is_type", Type);
		data.put("start", start);
		// }
		System.out.println("data== " + data);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.USERBILLSS, params, BaseData.class, null, billSuccessListener, errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> billSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			listView.onRefreshComplete();
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				if (start == 0 && blist != null) {
					blist.clear();
				}
				byte[] b = Base64.decode(response.data);
				if (b != null && !b.equals("")) {
					JSONArray s = JSON.parseArray(new String(b));
					System.out.println("BILL 返回数据=  " + new String(b));
					List<MyBillBean> billlist = JSON.parseArray(new String(b), MyBillBean.class);
					System.out.println(start + "= 0000000-->" + billlist);
					if (billlist.size() == 0) {
						// ToastManage.showToast("没有更多账单了");
						blist.clear();
					} else {
						start += 20;
					}
					blist.addAll(billlist);
					initListView();
				} else {
					initListView();
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
				listView.onRefreshComplete();
				dialogHelper.stopProgressDialog();
			}
		};
	}

	private void initListView() {
		if (blist.size() == 0) {
			listView.setVisibility(View.GONE);
			rl_nocard.setVisibility(View.VISIBLE);
		} else {
			listView.setVisibility(View.VISIBLE);
			rl_nocard.setVisibility(View.GONE);
		}
		myBillAdapter.reset(blist);
		myBillAdapter.notifyDataSetChanged();
	}
}
