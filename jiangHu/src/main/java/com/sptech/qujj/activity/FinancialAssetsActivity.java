package com.sptech.qujj.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sptech.qujj.R;
import com.sptech.qujj.adapter.LVFinancialAssetsAdapter;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.Product;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 理财资产
 * 
 * @author yebr
 * 
 */
public class FinancialAssetsActivity extends BaseActivity implements OnClickTitleListener {

	private TitleBar titleBar;
	private List<Product> curProducts = new ArrayList<Product>();

	private LVFinancialAssetsAdapter lvFinancialAssetsAdapter; // 理财资产adapter
	private TextView tv_total;
	// private ListView listView;
	// private MyFMListView listView; // 理财资产列表
	private DialogHelper dialogHelper;
	private SharedPreferences spf;

	private float totalmoney;// 总资产

	private int start = 0; // 分页起始位置
	private int limit = 10; // 每页显示条数
	boolean canload = true; // 是否还有加载项
	private final int ACTION_LIST = 1;

	// private ProgressBar progress_loadmore;
	private int visibleLastIndex = 0; // 最后的可视项索引
	private RelativeLayout rela_nodata;// 空数据页面
	private PullToRefreshListView listView;// 推荐项目列表

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_licai_financial_assets);
		initView();
		Tools.addActivityList(this);
	}

	@SuppressWarnings("unchecked")
	private void initView() {
		dialogHelper = new DialogHelper(this);
		spf = this.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		rela_nodata = (RelativeLayout) findViewById(R.id.rela_nodata);

		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent("理财资产", R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		tv_total = (TextView) findViewById(R.id.tv_total);

		listView = (PullToRefreshListView) findViewById(R.id.listview_fanical);
		// listView.setonRefreshListener(this);
		// listView.SetOnMyListViewScroll(this);
		// listView.setOverScrollMode(View.OVER_SCROLL_NEVER);

		lvFinancialAssetsAdapter = new LVFinancialAssetsAdapter(this, curProducts);
		listView.setAdapter(lvFinancialAssetsAdapter);
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(new OnRefreshListener2() {
			@SuppressWarnings("rawtypes")
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				start = 0;
				getUserasset();
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				start += 10;
				getUserasset();
			}

		});

		// View mFooterView = LayoutInflater.from(FinancialAssetsActivity.this)
		// .inflate(R.layout.view_footer_lv, null);
		// progress_loadmore = (ProgressBar) mFooterView
		// .findViewById(R.id.load_more_progressBar);
		// listView.addFooterView(mFooterView);
		getUserasset();
	}

	private void initListDate() {
		if (curProducts.size() == 0) {
			rela_nodata.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		} else {
			rela_nodata.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		}
		lvFinancialAssetsAdapter.reset(curProducts);
		lvFinancialAssetsAdapter.notifyDataSetChanged();
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

	// 理财资产
	private void getUserasset() {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("start", start);
		data.put("limit", limit);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		params.put("data", HttpUtil.getData(data));

		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.USER_ASSET, params, BaseData.class, null, usersuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> usersuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				listView.onRefreshComplete();
				System.out.println("理财资产 返回code == " + response.code);
				dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {
						JSONObject s = JSON.parseObject(new String(b));
						System.out.println("理财资产 数据=" + s);
						totalmoney = s.getFloat("money");
						tv_total.setText("¥" + DataFormatUtil.floatsaveTwo(totalmoney));
						if (start == 0 && curProducts != null) {
							curProducts.clear();
						}
						List<Product> financialAssets = new ArrayList<Product>();
						financialAssets = JSON.parseArray(s.getString("list"), Product.class);
						curProducts.addAll(financialAssets);
						if (financialAssets.size() == 0) {
							canload = false;
							// if (start != 0) {
							// ToastManage.showToast("没有更多产品了");
							// }
						} else {
							start += 10;
						}
						initListDate();
					} else {
						ToastManage.showToast("没有更多产品了");
					}
				} else {
					ToastManage.showToast(response.desc);
				}
			}
		};
	}

	/**
	 * 调用web服务失败返回数据
	 * 
	 * @return
	 */
	@SuppressLint("ShowToast")
	private Response.ErrorListener errorListener() {
		return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				dialogHelper.stopProgressDialog();
			}
		};
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ACTION_LIST:
				getUserasset();
				break;
			default:
				break;
			}

		}
	};

	// @Override
	// public void onScroll(AbsListView view, int firstVisibleItem,
	// int visibleItemCount, int totalItemCount) {
	// System.out.println("total==" + totalItemCount);
	// System.out.println("first==" + firstVisibleItem);
	// System.out.println("visible==" + visibleItemCount);
	//
	// visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
	// System.out.println("visibleLastIndex==" + visibleLastIndex);
	// boolean loadMore = (firstVisibleItem + visibleItemCount) >=
	// totalItemCount;
	// if (loadMore && visibleItemCount != totalItemCount) {
	// if (canload) {
	// progress_loadmore.setVisibility(View.VISIBLE);
	// } else {
	// ToastManage.showToast("没有更多产品了");
	// }
	// }
	// }
	//
	//
	//
	// @Override
	// public void onScrollStateChanged(AbsListView view, int scrollState) {
	// // TODO Auto-generated method stub
	// int itemsLastIndex = lvFinancialAssetsAdapter.getCount() - 1; //
	// 数据集最后一项的索引
	// int lastIndex = itemsLastIndex + 2; // 加上底部的loadMoreView项 和 头部项
	//
	// // System.out.println("lastIndex" + lastIndex);
	// // System.out.println("visibleLastIndex" + visibleLastIndex);
	// if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
	// && visibleLastIndex == lastIndex) {
	// if (canload) {
	// handler.sendEmptyMessage(ACTION_LIST);
	// }
	// }
	// }
	//
	// @Override
	// public void onRefresh() {
	// // TODO Auto-generated method stub
	// System.out.println("进来 onRefresh = ");
	// listView.postDelayed(new Runnable() {
	// @Override
	// public void run() {
	// listView.onRefreshComplete();
	// if (canload) {
	// getUserasset();
	// }
	// }
	// }, 3000);
	//
	// }

}
