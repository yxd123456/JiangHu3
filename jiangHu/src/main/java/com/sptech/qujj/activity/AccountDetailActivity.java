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
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sptech.qujj.R;
import com.sptech.qujj.adapter.LVAccountDetailAdapter;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BalanceDetail;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;

/**
 * 账户明细--全部明细
 * 
 * @author yebr
 * 
 */
public class AccountDetailActivity extends BaseActivity implements OnClickListener {
	private LVAccountDetailAdapter lvAccountDetailAdapter;
	private List<BalanceDetail> blist = new ArrayList<BalanceDetail>();
	private Button btn_left; // 返回按钮
	private RelativeLayout title_view, relative_all; // title,全部明细;

	private RelativeLayout rela_all, rela_income, rela_spend; // 下拉：全部明细，收入明细，支出明细;
	private ImageView image_all, image_income, image_spend; // 下拉菜单：三个图片
	private TextView tv_title, tv_all, tv_income, tv_spend;
	private ImageView img_all;// 全部明细的图片
	private View popupWindowView;
	private PopupWindow popupWindow;
	private LayoutInflater inflater;
	DialogHelper dialogHelper;// 加载进度条
	private SharedPreferences spf;

	private Animation operatingAnim;
	private RelativeLayout rela_nodata;// 没有数据的页面
	private int start = 0;
	private PullToRefreshListView listView;
	private int curType = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_licai_accountdetail);
		initView();
		Tools.addActivityList(this);
	}

	@SuppressWarnings("unchecked")
	private void initView() {
		spf = this.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		dialogHelper = new DialogHelper(this);

		rela_nodata = (RelativeLayout) findViewById(R.id.rela_nodata);
		listView = (PullToRefreshListView) findViewById(R.id.listview_acdetail);
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(new OnRefreshListener2() {

			@SuppressWarnings("rawtypes")
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// currentPage = 1;
				start = 0;
				getHttpBill(curType);
				// listView.onRefreshComplete();
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// currentPage++;

				getHttpBill(curType);
				// lvYesmbRecord.onRefreshComplete();
			}

		});

		lvAccountDetailAdapter = new LVAccountDetailAdapter(this, blist);
		listView.setAdapter(lvAccountDetailAdapter);

		tv_title = (TextView) findViewById(R.id.tv_title);
		title_view = (RelativeLayout) findViewById(R.id.title_view);
		relative_all = (RelativeLayout) findViewById(R.id.relative_all);
		img_all = (ImageView) findViewById(R.id.img_all);

		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		popupWindowView = inflater.inflate(R.layout.accountdetail_popupwindow, null);

		rela_all = (RelativeLayout) popupWindowView.findViewById(R.id.rela_all);
		rela_income = (RelativeLayout) popupWindowView.findViewById(R.id.rela_income);
		rela_spend = (RelativeLayout) popupWindowView.findViewById(R.id.rela_spend);

		image_all = (ImageView) popupWindowView.findViewById(R.id.image_all);
		image_income = (ImageView) popupWindowView.findViewById(R.id.image_income);
		image_spend = (ImageView) popupWindowView.findViewById(R.id.image_spend);

		tv_all = (TextView) popupWindowView.findViewById(R.id.tv_all);
		tv_income = (TextView) popupWindowView.findViewById(R.id.tv_income);
		tv_spend = (TextView) popupWindowView.findViewById(R.id.tv_spend);

		rela_all.setOnClickListener(this);
		rela_income.setOnClickListener(this);
		rela_spend.setOnClickListener(this);

		btn_left = (Button) findViewById(R.id.btn_left);
		btn_left.setOnClickListener(this);
		relative_all.setOnClickListener(this);

		// operatingAnim = AnimationUtils.loadAnimation(this,
		// R.anim.rotate_img);
		// LinearInterpolator lin = new LinearInterpolator();
		// operatingAnim.setInterpolator(lin);
		getHttpBill(0);
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
			image_income.setBackgroundResource(R.drawable.btn_income);
			image_spend.setBackgroundResource(R.drawable.btn_spend);
			tv_all.setTextColor(getResources().getColor(R.color.main_color));
			tv_income.setTextColor(getResources().getColor(R.color.text_main));
			tv_spend.setTextColor(getResources().getColor(R.color.text_main));
			tv_title.setText("全部明细");
			curType = 0;
			start = 0;
			getHttpBill(0);
			changeImagAll();
			break;
		case R.id.rela_income:
			tv_all.setTextColor(getResources().getColor(R.color.text_main));
			tv_income.setTextColor(getResources().getColor(R.color.main_color));
			tv_spend.setTextColor(getResources().getColor(R.color.text_main));
			image_all.setBackgroundResource(R.drawable.btn_all);
			image_income.setBackgroundResource(R.drawable.btn_income_hl);
			image_spend.setBackgroundResource(R.drawable.btn_spend);
			tv_title.setText("收入明细");
			curType = 1;
			start = 0;
			getHttpBill(1);
			changeImagAll();
			break;
		case R.id.rela_spend:
			image_all.setBackgroundResource(R.drawable.btn_all);
			image_income.setBackgroundResource(R.drawable.btn_income);
			image_spend.setBackgroundResource(R.drawable.btn_spend_hl);
			tv_all.setTextColor(getResources().getColor(R.color.text_main));
			tv_income.setTextColor(getResources().getColor(R.color.text_main));
			tv_spend.setTextColor(getResources().getColor(R.color.main_color));
			tv_title.setText("支出明细");
			curType = 2;
			start = 0;
			getHttpBill(2);
			changeImagAll();
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

	private void getHttpBill(int Type) {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		HashMap<String, Object> data = new HashMap<String, Object>();
		if (Type != 0) {
			data.put("in_out", Type);
		}
		data.put("is_change", 0);
		data.put("start", start);
		// data.put("start", 0);
		// data.put("limit", 0);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.ACCOUNT_MONEY_DETAIL, params, BaseData.class, null, billSuccessListener, errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> billSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			System.out.println("返回数据" + response.code);
			listView.onRefreshComplete();
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				byte[] b = Base64.decode(response.data);
				if (b != null && !b.equals("")) {
					if (start == 0 && blist != null) {
						blist.clear();
					}
					// JSONArray s = JSON.parseArray(new String(b));
					System.out.println("返回数据" + new String(b));
					List<BalanceDetail> billlist = JSON.parseArray(new String(b), BalanceDetail.class);
					if (billlist.size() == 0) {
						// ToastManage.showToast("没有更多明细了");
					}
					blist.addAll(billlist);
					if (blist.size() != 0) {
						rela_nodata.setVisibility(View.GONE);
						start += 20;
						listView.setVisibility(View.VISIBLE);
					} else {
						rela_nodata.setVisibility(View.VISIBLE);
						// ToastManage.showToast("没有更多明细了");
						listView.setVisibility(View.GONE);
					}

					lvAccountDetailAdapter.reset(blist);
					lvAccountDetailAdapter.notifyDataSetChanged();
				} else {
					blist.clear();
					rela_nodata.setVisibility(View.VISIBLE);
					listView.setVisibility(View.GONE);
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
