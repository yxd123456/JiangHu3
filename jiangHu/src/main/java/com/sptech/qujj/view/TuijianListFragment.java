package com.sptech.qujj.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.adapter.LVFMListAdapter;
import com.sptech.qujj.basic.BaseFragment;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.Banner;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.Product;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.Md5;
import com.sptech.qujj.view.MyAdGallery.MyOnItemClickListener;
import com.sptech.qujj.view.MyFMListView.MyListViewScroll;
import com.sptech.qujj.view.MyFMListView.OnRefreshListener;

/*
 * 推荐产品 
 */

public class TuijianListFragment extends BaseFragment implements OnRefreshListener, MyListViewScroll {

	private MyAdGallery myAdGallery;
	private List<Banner> bannerList = new ArrayList<Banner>();// 产品
	private List<Product> productList = new ArrayList<Product>();// 产品
	private List<String> piclist = new ArrayList<String>();// 图片

	private LinearLayout adiconlinear; // 圆点容器
	private MyFMListView lv_view; // 推荐项目列表
	private LVFMListAdapter mAdapter; // 项目列表 adapter
	private View head; // 头部 Banner
	private SharedPreferences spf;

	// private ProgressBar progress_loadmore;

	@Override
	protected View createFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "createFragmentView");
		return inflater.inflate(R.layout.jh_licai_tuijian, container, false);
	}

	@Override
	protected void initView() {
		spf = getActivity().getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);

		head = LayoutInflater.from(getActivity()).inflate(R.layout.tuijian_gallery, null);
		lv_view = (MyFMListView) selfView.findViewById(R.id.lv_view);
		lv_view.setonRefreshListener(this);
		lv_view.SetOnMyListViewScroll(this);
		lv_view.setOverScrollMode(View.OVER_SCROLL_NEVER);
		initListData();
		initAdView();
		// initListView();
	}

	@SuppressWarnings("rawtypes")
	private void initListData() {
		// TODO Auto-generated method stub
		String uid = spf.getString(Constant.UID, "");
		String key = spf.getString(Constant.KEY, "");
		System.out.println("uid=   " + uid);
		System.out.println("key=   " + key);
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("uid", uid);
		String signString = uid.toString() + key.toUpperCase();
		data.put("sign", Md5.md5s(signString));
		System.out.println("data==  " + data);

		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(getActivity(), false);
		request.HttpVolleyRequestPost(JsonConfig.PRODUCTLIST, data, BaseData.class, null, successListener(), errorListener());
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
				System.out.println("返回code == " + response.code);
				if (response.code.equals("0")) {
					System.out.println("ASDASDFASDF");
					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {
						JSONArray s = JSON.parseArray(new String(b));
						// System.out.println("返回数据" + s);
						List<Product> products = JSON.parseArray(new String(b), Product.class);
						productList = products;
						// byte[] b = Base64.decode(response.data);
						System.out.println("返回数据" + s);
						initListView();
					}
				} else {
					ToastManage.showToast(response.desc);

				}

			}
		};

	}

	private void initListView() {
		lv_view.addHeaderView(head);
		// footer
		View mFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.view_footer_lv, null);
		// progress_loadmore = (ProgressBar) mFooterView
		// .findViewById(R.id.load_more_progressBar);
		// lv_view.addFooterView(mFooterView);
		mAdapter = new LVFMListAdapter(getActivity(), productList);
		lv_view.setAdapter(mAdapter);
	}

	// 初始化轮播图
	private void initAdView() {
		Banner banner1 = new Banner();
		banner1.setPicurl("http://p2.gexing.com/G1/M00/B4/FD/rBACE1IbPAWg6PAgAAAo0ExoCAw959_200x200_3.png");
		// banner1.setPicurl("http://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E5%9B%BE%E7%89%87&pn=0&spn=0&di=10542941860&pi=&rn=1&tn=baiduimagedetail&ie=utf-8&oe=utf-8&cl=2&lm=-1&cs=3580210867%2C3098509580&os=1474203294%2C3268584066&adpicid=0&ln=21&fr=ala&sme=&cg=&bdtype=0&objurl=http%3A%2F%2Fi6.topit.me%2F6%2F5d%2F45%2F1131907198420455d6o.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bp5rtp_z%26e3B4jAzdH3Ftpj4AzdH3F888dc8m8&gsm=0");
		Banner banner2 = new Banner();
		banner2.setPicurl("http://d.hiphotos.baidu.com/zhidao/wh%3D600%2C800/sign=456a5f3cfffaaf5184b689b9bc64b8d6/1b4c510fd9f9d72aeb28e707d62a2834349bbb58.jpg");
		// banner2.setPicurl("http://image.baidu.com/search/detail?ct=503316480&tn=baiduimagedetail&statnum=wallpaper&ipn=d&z=0&width=1280&height=720&s=0&ic=0&lm=-1&itg=1&cg=wallpaper&word=%E5%A3%81%E7%BA%B8&ie=utf-8&in=3354&cl=2&st=&pn=24&rn=1&di=155602157370&ln=1000&&fmq=1378374347070_R&se=&sme=0&tab=&face=&&is=0,0&cs=3575390884,290682289&adpicid=undefined&pi=0&os=1098933883,1581986068&istype=&ist=&jit=&objurl=http%3A%2F%2Fimgstore.cdn.sogou.com%2Fapp%2Fa%2F100540002%2F755449.jpg&bdtype=0&gsm=3c");
		bannerList.add(banner2);
		bannerList.add(banner1);
		// 图片组
		if (bannerList.size() == 0) {
			return;
		}
		System.out.println("bannerList====" + bannerList.size());
		for (Banner banner : bannerList) {
			if (banner.getPicurl() == null || banner.getPicurl() == "") {
				// if (banner.getDesc() == null || banner.getDesc() == "") {
				// relative_info.setVisibility(View.GONE);
				// } else {+
				// relative_info.setVisibility(View.VISIBLE);
				// tv_info.setText(banner.getDesc());
				// }
			} else {
				piclist.add(banner.getPicurl());
			}
			System.out.println("图片===" + banner.getPicurl());
		}

		myAdGallery = (MyAdGallery) head.findViewById(R.id.galleryad);// 获取Gallery组件
		adiconlinear = (LinearLayout) head.findViewById(R.id.adiconlinear);// 获取圆点容器
		// 第二和第三参数 2选1 ,参数2为 图片网络路径数组 ,参数3为图片id的数组,本地测试用 ,2个参数都有优先采用 参数2
		myAdGallery.start(getActivity(), piclist, 6000, adiconlinear, R.drawable.dot_focused, R.drawable.dot_normal);
		myAdGallery.setMyOnItemClickListener(new MyOnItemClickListener() {
			public void onItemClick(int curIndex) {
				// 点击图片，跳转到对应的url
				System.out.println("curIndex==" + curIndex);
				// Intent intent = new Intent(getActivity(),
				// WebViewActivity.class);
				// String url = bannerList.get(curIndex).getUrl();
				// String picUrl = bannerList.get(curIndex).getPicurl();
				// if (picUrl
				// .equals("http://wechatbookpic.b0.upaiyun.com/dylc/banner2.jpg"))
				// {
				// if (SPHelper.getUserid() != -1) {
				// url = url + "?pr=" + SPHelper.getPlainpassword();
				// }
				// }
				// intent.putExtra("url", url);
				// intent.putExtra("title",
				// bannerList.get(curIndex).getTitle());
				// startActivity(intent);
				// getActivity().overridePendingTransition(R.anim.push_left_in,
				// R.anim.push_left_out);
			}
		});

	}

	private int visibleLastIndex = 0; // 最后的可视项索引

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
		// boolean loadMore = (firstVisibleItem + visibleItemCount) >=
		// totalItemCount;
		// if (loadMore && visibleItemCount != totalItemCount) {
		// progress_loadmore.setVisibility(View.VISIBLE);
		// }

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}

}
