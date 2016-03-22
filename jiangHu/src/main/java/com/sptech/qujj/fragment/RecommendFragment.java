package com.sptech.qujj.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
import com.sptech.qujj.activity.ProductDetailInfoActivity;
import com.sptech.qujj.activity.WebViewActivity;
import com.sptech.qujj.adapter.LVFMListAdapter;
import com.sptech.qujj.basic.BaseFragment;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.Adver;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.Product;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.view.MyAdGallery;
import com.sptech.qujj.view.MyAdGallery.MyOnItemClickListener;

/**
 * 推荐的产品 (广告banner + 项目list)
 * 
 * @author yebr
 * 
 */

public class RecommendFragment extends BaseFragment {

	private List<Product> productList = new ArrayList<Product>();// 产品
	private final int ACTION_LIST = 1;

	private LinearLayout adiconlinear; // 圆点容器
	// private MyFMListView lv_view; // 推荐项目列表
	private PullToRefreshListView lv_view;// 推荐项目列表
	private LVFMListAdapter mAdapter; // 项目列表 adapter
	private View head; // 头部 Banner
	private SharedPreferences spf;
	private DialogHelper dialogHelper;

	// private ProgressBar progress_loadmore;
	// private int visibleLastIndex = 0; // 最后的可视项索引

	private int start = 0; // 分页起始位置
	private int limit = 10; // 每页显示条数
	boolean canload = true; // 是否还有加载项

	private RelativeLayout relativead;// 轮播图view
	private MyAdGallery galleryad; // 轮播图
	private List<Adver> bannerList = new ArrayList<Adver>();
	private List<String> piclist = new ArrayList<String>();

	@Override
	protected View createFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "createFragmentView");
		return inflater.inflate(R.layout.jh_licai_tuijian, container, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initView() {
		dialogHelper = new DialogHelper(getActivity());
		spf = getActivity().getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		// head = LayoutInflater.from(getActivity()).inflate(
		// R.layout.tuijian_gallery, null);

		lv_view = (PullToRefreshListView) selfView.findViewById(R.id.lv_view);
		// lv_view.setonRefreshListener(this);
		// lv_view.SetOnMyListViewScroll(this);
		// lv_view.setOverScrollMode(View.OVER_SCROLL_NEVER);

		// View mFooterView = LayoutInflater.from(getActivity()).inflate(
		// R.layout.view_footer_lv, null);
		// progress_loadmore = (ProgressBar) mFooterView
		// .findViewById(R.id.load_more_progressBar);
		// lv_view.addFooterView(mFooterView);
		lv_view.setMode(Mode.BOTH);
		lv_view.setOnRefreshListener(new OnRefreshListener2() {
			@SuppressWarnings("rawtypes")
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				start = 0;
				initListData();
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// start += 10;
				initListData();
			}

		});

		// lv_view.addView(head);
		mAdapter = new LVFMListAdapter(getActivity(), productList);
		lv_view.setAdapter(mAdapter);

		// lv_view.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		// long arg3) {
		// // TODO Auto-generated method stub
		// Intent intent2 = new Intent(getActivity(),
		// ProductInfoActivity.class);
		// intent2.putExtra("id", productList.get(arg2).getId());
		// // intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// getActivity().startActivity(intent2);
		// getActivity().overridePendingTransition(R.anim.push_left_in,
		// R.anim.push_left_out);
		// }
		//
		// });

		relativead = (RelativeLayout) selfView.findViewById(R.id.relativead);
		galleryad = (MyAdGallery) selfView.findViewById(R.id.galleryad);// 获取Gallery组件
		adiconlinear = (LinearLayout) selfView.findViewById(R.id.adiconlinear);// 获取圆点容器

		// lv_view.add
		getAdver();
		initListData();

	}

	// 初始化轮播图
	public void initBanner() {
		if (bannerList.size() == 0) {
			// galleryad.setVisibility(View.GONE);
			relativead.setVisibility(View.GONE);
			System.out.println("banner -- gone ");
			return;
		} else {
			relativead.setVisibility(View.VISIBLE);
			// galleryad.setVisibility(View.VISIBLE);
		}
		System.out.println("bannerList====" + bannerList.size());
		for (Adver banner : bannerList) {
			piclist.add(banner.getAdpic());
			System.out.println("图片===" + banner.getAdpic());
		}
		// 第二和第三参数 2选1 ,参数2为 图片网络路径数组 ,参数3为图片id的数组,本地测试用 ,2个参数都有优先采用 参数2
		galleryad.start(getActivity(), piclist, 3000, adiconlinear, R.drawable.dot_focused, R.drawable.dot_normal);
		galleryad.setMyOnItemClickListener(new MyOnItemClickListener() {
			public void onItemClick(int curIndex) {
				// 点击图片，跳转到对应的url
				String curUrl = bannerList.get(curIndex).getAd_url();
				int url_type = bannerList.get(curIndex).getUrl_type();
				String title = bannerList.get(curIndex).getTitle();
				System.out.println("curIndex==" + curIndex);
				System.out.println("curUrl_type ==" + url_type);
				System.out.println("curUrl ==" + curUrl);
				// 跳到网页里
				Intent rela_activity = new Intent(getActivity(), WebViewActivity.class);
				if (url_type == 0) { // 内链
					rela_activity.putExtra("url", JsonConfig.HTML + curUrl);
					rela_activity.putExtra("title", title);
					startActivity(rela_activity);
					getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				} else if (url_type == 1) { // 外链
					rela_activity.putExtra("url", curUrl);
					rela_activity.putExtra("title", title);
					startActivity(rela_activity);
					getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				} else if (url_type == 2) {
					// 解析Jason 跳本地
					JSONObject s = JSON.parseObject(curUrl);
					String location = s.getString("1");
					String id = s.getString("2");
					System.out.println("location == " + location);
					System.out.println("id( int ) == " + Integer.parseInt(id));
					if ("product".equals(location)) {
						Intent intentproduct = new Intent(getActivity(), ProductDetailInfoActivity.class);
						intentproduct.putExtra("id", Integer.parseInt(id));
						startActivity(intentproduct);
						getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					}
				}

			}
		});

	}

	// 轮播图
	private void getAdver() {
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("type_id", 2);
		data.put("num", 2);// 首页 一个广告位 2 表示 多个轮播
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		params.put("data", HttpUtil.getData(data));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(getActivity(), false);
		request.HttpVolleyRequestPost(JsonConfig.GET_AD, params, BaseData.class, null, getAdversuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> getAdversuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				System.out.println("轮播图banner 返回code == " + response.code);
				System.out.println("轮播图banner 返回data == " + response.data);
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {
						bannerList.clear();
						System.out.println("轮播图 == " + new String(b));
						// JSONObject s = JSON.parseObject(new String(b));
						// int read_num = s.getIntValue("read_num");
						// System.out.println("首页广告 == " + new String(b));
						List<Adver> advers = new ArrayList<Adver>();
						advers = JSON.parseArray(new String(b), Adver.class);
						bannerList.addAll(advers);
						initBanner();
					} else {
						initBanner();
					}
				} else {
					ToastManage.showToast(response.desc);
				}
			}
		};
	}

	@SuppressWarnings("rawtypes")
	public void initListData() {
		// TODO Auto-generated method stub
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		String uid = spf.getString(Constant.UID, "");
		String key = spf.getString(Constant.KEY, "");

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("start", start);
		data.put("limit", limit);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", uid);
		params.put("sign", HttpUtil.getSign(data, uid, key));
		params.put("data", HttpUtil.getData(data));

		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(getActivity(), false);
		request.HttpVolleyRequestPost(JsonConfig.PRODUCTLIST, params, BaseData.class, null, successListener(), errorListener());
	}

	// 失败
	@SuppressLint("ShowToast")
	private Response.ErrorListener errorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				lv_view.onRefreshComplete();
				dialogHelper.stopProgressDialog();
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
				lv_view.onRefreshComplete();
				System.out.println("返回code == " + response.code);
				dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					System.out.println("ASDASDFASDF");
					byte[] b = Base64.decode(response.data);
					if (start == 0 && productList != null) {
						productList.clear();
					}

					// System.out.println("返回数据" + s);
					if (b != null && !b.equals("")) {
						List<Product> products = JSON.parseArray(new String(b), Product.class);
						// productList = products;

						// byte[] b = Base64.decode(response.data);
						System.out.println("PRODUCT_List 返回数据" + new String(b));
						if (products.size() == 0) {
							// ToastManage.showToast("没有更多账单了");
						} else {
							start += 10;
						}
						productList.addAll(products);
						initListView();
					}
					// progress_loadmore.setVisibility(View.GONE);

				} else {
					ToastManage.showToast(response.desc);
					// progress_loadmore.setVisibility(View.GONE);
					return;
				}
			}
		};

	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ACTION_LIST:
				initListData();
				break;
			default:
				break;
			}

		}
	};

	private void initListView() {
		mAdapter.reset(productList);
		mAdapter.notifyDataSetChanged();

		// dialogHelper.stopProgressDialog();
	}

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
	// // ToastManage.showToast("没有更多产品了");
	// }
	// }
	// }
	//
	// @Override
	// public void onScrollStateChanged(AbsListView view, int scrollState) {
	// // TODO Auto-generated method stub
	// int itemsLastIndex = mAdapter.getCount() - 1; // 数据集最后一项的索引
	// int lastIndex = itemsLastIndex + 2; // 加上底部的loadMoreView项 和 头部项
	//
	// System.out.println("lastIndex" + lastIndex);
	// System.out.println("visibleLastIndex" + visibleLastIndex);
	//
	// if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
	// && visibleLastIndex == lastIndex) {
	// if (canload) {
	// handler.sendEmptyMessage(ACTION_LIST);
	// } else {
	// ToastManage.showToast("没有更多产品了");
	// }
	// }
	// }
	//
	// @Override
	// public void onRefresh() {
	// // TODO Auto-generated method stub
	// System.out.println("进来 onRefresh = ");
	// lv_view.postDelayed(new Runnable() {
	// @Override
	// public void run() {
	// lv_view.onRefreshComplete();
	// if (canload) {
	// initListData();
	// } else {
	// ToastManage.showToast("没有更多产品了");
	// }
	// }
	// }, 3000);
	//
	// }
}
