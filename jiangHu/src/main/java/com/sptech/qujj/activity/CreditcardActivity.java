package com.sptech.qujj.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.db.SPHelper;
import com.sptech.qujj.dialog.DialogBindCredit;
import com.sptech.qujj.dialog.DialogChangeStatus;
import com.sptech.qujj.dialog.DialogGoRepay;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.dialog.DialogSetPwd;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.CardInfo;
import com.sptech.qujj.model.UsablebankBean;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.DateManage;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.EventHandleListener;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/**
 * 信用卡详情
 * 
 * @author yebr
 * 
 */
public class CreditcardActivity extends BaseActivity implements OnGetPoiSearchResultListener, LocationListener, OnClickListener, OnClickTitleListener {
	private TitleBar titleBar;

	ViewPager viewPager;
	ViewPagerAdapter pagerAdapter;
	View view1, view2, view3, view4; // 页面
	List<View> views; // Tab页面列表

	ImageView img_cursor; // 指示图片
	TextView tv_guid1, tv_guid2, tv_guid3, tv_guid4; // 页卡头标（流水，详情，网点，客服）

	// 信用卡信息
	private TextView tv_bankcard, tv_realname, tv_cardno, tv_sy, tv_day, tv_edu, tv_shengyumoney, tv_weichu, tv_mianxi, tv_zhouqi, tv_zuidi, tv_benqimoney;
	private Button btn_huan;
	// 账单状态手动修改
	private RelativeLayout rela_finish, rela_weihuan, rela_bufen;
	private ImageView img_card, img_weihuan, img_bufen, img_finish;
	private TextView tv_weihuan, tv_bufen, tv_finish;
	private RelativeLayout rela_status;// 信用卡账单

	// 详情tab
	private TextView tv_dtcardno, tv_dtkeyong, tv_dtbilldate, tv_huandate;

	// 客服
	private TextView tv_hotline, tv_jinji, tv_rengong;
	int offset = 0; // 偏移量
	int currIndex = 0; // 当前页卡编号
	int bmpW; // 图片宽度
	private DialogHelper dialogHelper;
	private SharedPreferences spf;
	private CardInfo curcardInfo;
	private int bill_id;// 信用卡账单ID

	HashMap<String, String> cardMap = new HashMap<String, String>();
	private boolean outflag = false; // 逾期超过三天

	private DialogSetPwd dsp;
	private int curStatus;
	private int changedStatus;
	private WebView webView;// 流水web

	private MapView mMapView;
	BaiduMap mBaiduMap;
	// 定位相关
	private LocationClient mLocClient = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	BitmapDescriptor mCurrentMarker;
	boolean isFirstLoc = true;// 是否首次定位

	private PoiSearch mPoiSearch = null;
	// private double latitude, longtitude;// 经纬度
	private LatLng curlatLng = null;
	private InfoWindow mInfoWindow;
	private BitmapDescriptor bitmap;
	private String curbank = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_main_cardinfo);
		Tools.addActivityList(this);

		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		initImageView();
		initTextView();
		// initCardDetail();
		// initBaiduMap();
		view1 = (View) findViewById(R.layout.cardinfo_liushui);
		view2 = (View) findViewById(R.layout.cardinfo_detailinfo);
		view3 = (View) findViewById(R.layout.cardinfo_wangdian);
		view4 = (View) findViewById(R.layout.cardinfo_kefu);

		LayoutInflater inflater = LayoutInflater.from(this);
		view1 = inflater.inflate(R.layout.cardinfo_liushui, null);
		view2 = inflater.inflate(R.layout.cardinfo_detailinfo, null);
		view3 = inflater.inflate(R.layout.cardinfo_wangdian, null);
		view4 = inflater.inflate(R.layout.cardinfo_kefu, null);

		views = new ArrayList<View>();
		views.add(view1);
		views.add(view2);
		views.add(view3);
		views.add(view4);

		pagerAdapter = new ViewPagerAdapter(views);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setAdapter(pagerAdapter);

		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

		// 地图初始化
		mMapView = (MapView) view3.findViewById(R.id.bmapView);
		mMapView.showZoomControls(false);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// mBaiduMap.zoomControlsEnabled(false);// 隐藏缩放按钮
		// mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new
		// MapStatus.Builder().zoom(18).build()));// 设置缩放级别

		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		// option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		initView();
	}

	private void initView() {

		dialogHelper = new DialogHelper(this);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		cardMap = DataFormatUtil.getBlueCardMap(spf);
		if (getIntent() != null) {
			// bill_id = getIntent().getExtras().getInt("bill_id");
			curcardInfo = (CardInfo) getIntent().getSerializableExtra("cardInfo");
			curbank = curcardInfo.getCard_bank();
		}
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent(curbank + "信用卡", R.drawable.jh_back_selector, 0, "", "");
		// titleBar.bindTitleContent("建设银行信用卡", R.drawable.jh_back_selector, 0,
		// "", "");
		titleBar.setOnClickTitleListener(this);
		webView = (WebView) view1.findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setAllowFileAccess(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setSupportZoom(false); // 支持缩放
		webView.getSettings().setBuiltInZoomControls(false);
		webView.requestFocus();
		String url = JsonConfig.HTML + "index/stream.html?id=" + curcardInfo.getId();
		webView.loadUrl(url);
		webView.setVisibility(View.VISIBLE);
		System.out.println("HTML== " + url);
		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		webView.getSettings().setUseWideViewPort(true);

		// 组件
		tv_bankcard = (TextView) findViewById(R.id.tv_bankcard);// 开户行
		tv_realname = (TextView) findViewById(R.id.tv_realname);// 姓名
		tv_cardno = (TextView) findViewById(R.id.tv_cardno);// 卡号
		tv_sy = (TextView) findViewById(R.id.tv_sy);//
		tv_day = (TextView) findViewById(R.id.tv_day);//
		tv_edu = (TextView) findViewById(R.id.tv_edu);//
		tv_shengyumoney = (TextView) findViewById(R.id.tv_shengyumoney);//
		tv_weichu = (TextView) findViewById(R.id.tv_weichu);//
		tv_mianxi = (TextView) findViewById(R.id.tv_mianxi);//
		tv_zhouqi = (TextView) findViewById(R.id.tv_zhouqi);//
		tv_zuidi = (TextView) findViewById(R.id.tv_zuidi);//
		tv_benqimoney = (TextView) findViewById(R.id.tv_benqimoney);//
		btn_huan = (Button) findViewById(R.id.btn_huan);// 一键还款
		img_card = (ImageView) findViewById(R.id.img_card);
		rela_status = (RelativeLayout) findViewById(R.id.img_status);

		rela_finish = (RelativeLayout) findViewById(R.id.rela_finish);
		rela_weihuan = (RelativeLayout) findViewById(R.id.rela_weihuan);
		rela_bufen = (RelativeLayout) findViewById(R.id.rela_bufen);

		img_weihuan = (ImageView) findViewById(R.id.img_weihuan); // 未还，
		img_bufen = (ImageView) findViewById(R.id.img_bufen);// 部分
		img_finish = (ImageView) findViewById(R.id.img_finish);// 还清
		tv_weihuan = (TextView) findViewById(R.id.tv_weihuan);
		tv_bufen = (TextView) findViewById(R.id.tv_bufen);
		tv_finish = (TextView) findViewById(R.id.tv_finish);

		// 详情
		tv_dtcardno = (TextView) view2.findViewById(R.id.tv_cardno);
		tv_dtkeyong = (TextView) view2.findViewById(R.id.tv_keyong);
		tv_dtbilldate = (TextView) view2.findViewById(R.id.tv_billdate);
		tv_huandate = (TextView) view2.findViewById(R.id.tv_huandate);

		// 客服
		tv_hotline = (TextView) view4.findViewById(R.id.tv_hotline);
		tv_jinji = (TextView) view4.findViewById(R.id.tv_jinji);
		tv_rengong = (TextView) view4.findViewById(R.id.tv_rengong);
		tv_hotline.setOnClickListener(this);
		tv_jinji.setOnClickListener(this);
		tv_rengong.setOnClickListener(this);
		btn_huan.setOnClickListener(this);
		getCardDetail();
	}

	/**
	 * 初始化动画图片
	 * 
	 */
	private void initImageView() {
		img_cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.img_select_line).getWidth();

		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels; // 获取手机屏幕宽度分辨率
		offset = (screenW / 4 - bmpW) / 2; // 获取图片偏移量
		// imageview设置平移，使下划线平移到初始位置（平移一个offset）
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		img_cursor.setImageMatrix(matrix);
	}

	/**
	 * 
	 * 初始化TextView控件，和注册监听器
	 */
	private void initTextView() {
		tv_guid1 = (TextView) findViewById(R.id.tv_guid1);
		tv_guid2 = (TextView) findViewById(R.id.tv_guid2);
		tv_guid3 = (TextView) findViewById(R.id.tv_guid3);
		tv_guid4 = (TextView) findViewById(R.id.tv_guid4);

		tv_guid1.setOnClickListener(this);
		tv_guid2.setOnClickListener(this);
		tv_guid3.setOnClickListener(this);
		tv_guid4.setOnClickListener(this);
	}

	// 网点--地图
	private void initBaiduMap() {

	}

	public class ViewPagerAdapter extends PagerAdapter {
		// 界面列表
		private List<View> views;

		public ViewPagerAdapter(List<View> views) {
			this.views = views;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(views.get(position));
		}

		@Override
		public int getCount() {
			if (views != null) {
				return views.size();
			}
			return 0;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(views.get(position)); // 把被点击的图片放入缓存中
			return views.get(position); // 返回被点击图片对象
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return (arg0 == arg1);
		}
	}

	// 赋值
	private void initContentData() {
		if (curcardInfo != null) {
			String cardname = "";
			cardname = curcardInfo.getBank_name();

			if (!"".equals(cardname)) {
				String cardStream = "";
				cardStream = cardMap.get(curcardInfo.getBank_name());
				if (cardStream == null || "".equals(cardStream)) {
					img_card.setImageResource(R.drawable.img_nobank);
				} else {
					byte[] b = Base64.decode(cardStream);
					Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
					img_card.setImageBitmap(bit);
				}
			}
			tv_bankcard.setText(curcardInfo.getBank_name() + "  ");
			tv_realname.setText(DataFormatUtil.hideFirstname(curcardInfo.getCard_realname()));
			tv_cardno.setText(DataFormatUtil.bankcardsaveFour(curcardInfo.getCard_no()));
			int repay_date = curcardInfo.getRepayment_date();// 还款日
			int bill_cycle = curcardInfo.getBill_cycle_end();// 账单日
			// 账单日 与当前时间的 时间差
			Long between = (bill_cycle - (System.currentTimeMillis() / 1000));
			// 还款日 与当前时间的 时间差
			Long between2 = (repay_date - (System.currentTimeMillis() / 1000));
			int status = curcardInfo.getIs_status();
			curStatus = status;
			// 先判断 账单期 是否到
			if (between <= 0) {
				btn_huan.setVisibility(View.VISIBLE);
				// 再判断 还款日
				if (between2 < 0) {
					if (status == 1 || status == 2 || curcardInfo.getAmount_of() == 0) {
						if (between < 0) {
							btn_huan.setVisibility(View.GONE);
						}

					} else {
						rela_status.setBackgroundResource(R.drawable.img_label_expired);
						int late = Math.round((-between2) / 24 / 3600);
						if (late > 99) {
							tv_sy.setText("逾期");
							tv_day.setText("99+天");
						} else {
							tv_sy.setText("逾期");
							tv_day.setText(late + " 天");
						}
						if (late > 3) {
							outflag = true;
						}

					}
				} else if (between2 == 0) {
					rela_status.setBackgroundResource(R.drawable.img_label_surplus);
					tv_sy.setText("今天");
					tv_day.setText("到期");
				} else {
					rela_status.setBackgroundResource(R.drawable.img_label_surplus);
					tv_sy.setText("剩余");
					tv_day.setText(DateManage.getDayFormUX(between2) + " 天");
				}
			}

			// 还清且账单期未到-提示新账单未出账
			if (status == 1 || status == 2 || curcardInfo.getAmount_of() == 0) {
				if (between < 0) {
					long newbilltime = (long) curcardInfo.getBill_cycle_end() * 1000;
					Date newbill = new Date(newbilltime);
					System.out.println("newbill ==" + newbill.getTime());
					newbill.setMonth(newbill.getMonth() + 1);
					System.out.println("newbillTime ==" + newbill.getTime());
					// long newbetweenbill = Math.round(((newbill.getTime()
					// / 1000) - (System.currentTimeMillis() / 1000)) / 24 /
					// 3600);
					long newbetweenbill = newbill.getTime() / 1000 - (System.currentTimeMillis() / 1000);
					System.out.println("newbetweenbill==" + newbetweenbill);

					rela_status.setBackgroundResource(R.drawable.img_label_outaccount);
					tv_sy.setText("出账");

					int newdate = Integer.parseInt(DateManage.getDayFormUX(newbetweenbill));
					if (newdate > 99) {
						tv_day.setText("99+天");
					} else if (newdate < -99) {
						tv_day.setText("-99天");
					} else {
						tv_day.setText(newdate + " 天");
					}
					// tv_day.setText(DateManage.getDayFormUX(newbetweenbill) +
					// " 天");
				}

				if (bill_cycle == 0) {
					rela_status.setBackgroundResource(R.drawable.img_label_outaccount);
					tv_sy.setText("未出账");
					tv_day.setText("");
				}
			}

			if (status == 1 || status == 2) {
				btn_huan.setText("");
				btn_huan.setBackgroundResource(R.drawable.img_progress_seal_finish);
				btn_huan.setVisibility(View.VISIBLE);
				btn_huan.setClickable(false);
			}

			float edu = curcardInfo.getCredit_limit();
			if (edu == 0) {
				tv_edu.setText("卡片额度：未知");
			} else {
				tv_edu.setText("卡片额度：¥" + DataFormatUtil.floatsaveTwo(edu));
			}
			// tv_edu.setText("卡片额度：¥" +
			// DataFormatUtil.floatsaveTwo(curcardInfo.getCredit_limit()));

			float symoney = curcardInfo.getCashout_amount();
			if (symoney == 0) {
				tv_shengyumoney.setText("剩余额度：未知");
				tv_dtkeyong.setText("未知");
			} else {
				tv_shengyumoney.setText("剩余额度：¥" + DataFormatUtil.floatsaveTwo(symoney));
				tv_dtkeyong.setText("¥" + DataFormatUtil.floatsaveTwo(symoney));
			}

			tv_weichu.setVisibility(View.GONE);
			// tv_weichu.setText(DataFormatUtil.floatsaveTwo(curcardInfo.getCashout_amount()));

			if (curcardInfo.getBill_cycle_set() != 0 && curcardInfo.getRepayment_date() != 0) {
				long mian = (long) repay_date - curcardInfo.getBill_cycle_set();
				if (mian > 0) {
					tv_mianxi.setText("刷卡免息：" + DateManage.getDayFormUX(mian) + "天");// (到期还款日-账单周期(getBill_cycle_end))
					System.out.println("刷卡免息：" + DateManage.getDayFormUX(mian));
				}
			} else {
				tv_mianxi.setText("刷卡免息：未知");
			}

			// long mian = (long) repay_date - curcardInfo.getBill_cycle_set();
			// if (mian > 0 && curcardInfo.getBill_cycle_set() != 0) {
			// tv_mianxi.setText("刷卡免息：" + DateManage.getDayFormUX(mian) +
			// "天");// (到期还款日-账单周期(getBill_cycle_end))
			// } else {
			// tv_mianxi.setText("刷卡免息：未知");
			// }

			int bills = curcardInfo.getBill_cycle_set();
			int bille = curcardInfo.getBill_cycle_end();
			if (bills == 0 || bille == 0) {
				tv_zhouqi.setText("账单周期：未知");
			} else {
				String billset = DateManage.StringToMMDD(String.valueOf(bills));
				String billend = DateManage.StringToMMDD(String.valueOf(bille));
				String newbillSet = billset.replace("-", "/");
				String newbillEnd = billend.replace("-", "/");
				String zhangdanString = newbillSet + " - " + newbillEnd;
				tv_zhouqi.setText("账单周期：" + zhangdanString);
				tv_dtbilldate.setText("每月" + newbillEnd.substring(newbillEnd.length() - 2, newbillEnd.length()) + "号");
			}

			float zuidi = curcardInfo.getAmount_lowest();
			if (zuidi == 0) {
				tv_zuidi.setText("最低还款：未知");
			} else {
				tv_zuidi.setText("最低还款：¥" + DataFormatUtil.floatsaveTwo(zuidi));
			}
			// tv_zuidi.setText("最低还款：¥" +
			// DataFormatUtil.floatsaveTwo(curcardInfo.getAmount_lowest()));

			if (curcardInfo.getAmount_of() == 0) {
				tv_benqimoney.setText("¥0.00");
			} else {
				tv_benqimoney.setText("¥" + DataFormatUtil.floatsaveTwo(curcardInfo.getAmount_of()));
			}

			// 详情
			if ("".equals(curcardInfo.getCard_no())) {
				tv_dtcardno.setText("未知");
			} else {
				tv_dtcardno.setText(DataFormatUtil.bankcardsaveFour(curcardInfo.getCard_no()));
			}

			int repaydate = curcardInfo.getRepayment_date();
			if (repay_date == 0) {
				tv_huandate.setText("未知");
			} else {
				String repay = DateManage.StringToMMDD(String.valueOf(repaydate));
				System.out.println("repay date ==" + repay);
				String redate = repay.substring(repay.length() - 2, repay.length());
				tv_huandate.setText("每月" + redate + "号");
			}

			// 客服
			// getBlueCardInfo
			UsablebankBean usablebankBean = null;
			usablebankBean = DataFormatUtil.getBlueCardInfo(spf, cardname);
			String customerString = usablebankBean.getCustomer();
			String jinjiString = usablebankBean.getEmergency();
			String rengongString = usablebankBean.getArtificial();
			System.out.println("客服= " + customerString);
			System.out.println("紧急= " + jinjiString);
			System.out.println("人工= " + rengongString);

			if ("".equals(customerString)) {
				tv_hotline.setText("未知");
			} else {
				tv_hotline.setText(usablebankBean.getCustomer());
			}
			if ("".equals(jinjiString)) {
				tv_jinji.setText("未知");
			} else {
				tv_jinji.setText(usablebankBean.getEmergency());
			}

			if ("".equals(rengongString)) {
				tv_rengong.setText("未知");
			} else {
				tv_rengong.setText(usablebankBean.getArtificial());
			}
			System.out.println("当前状态== " + curStatus);
			// 状态
			if (curcardInfo.getAmount_of() == 0) {
				initStatus(1);
				curStatus = 1;
				btn_huan.setText("");
				btn_huan.setBackgroundResource(R.drawable.img_progress_seal_finish);
				btn_huan.setVisibility(View.VISIBLE);
				btn_huan.setClickable(false);
			} else {
				initStatus(curStatus);
			}

			rela_bufen.setOnClickListener(this);
			rela_finish.setOnClickListener(this);
		}
	}

	private void initStatus(int curStatus) {
		if (curStatus == 0) {// 未还款
			System.out.println("当前状态== " + curStatus + "  未还款");
			img_weihuan.setBackgroundResource(R.drawable.btn_emoticon_no_unselect);
			img_bufen.setBackgroundResource(R.drawable.btn_emoticon_some_select);
			img_finish.setBackgroundResource(R.drawable.btn_emoticon_yes_select);

			tv_weihuan.setTextColor(getResources().getColor(R.color.text_green));
			tv_bufen.setTextColor(getResources().getColor(R.color.text_hint));
			tv_finish.setTextColor(getResources().getColor(R.color.text_hint));
		} else if (curStatus == 1 || curStatus == 2) {// 还清
			System.out.println("当前状态== " + curStatus + "  还清");
			img_weihuan.setBackgroundResource(R.drawable.btn_emoticon_no_select);
			img_bufen.setBackgroundResource(R.drawable.btn_emoticon_some_select);
			img_finish.setBackgroundResource(R.drawable.btn_emoticon_yes_unselect);

			tv_weihuan.setTextColor(getResources().getColor(R.color.text_hint));
			tv_bufen.setTextColor(getResources().getColor(R.color.text_hint));
			tv_finish.setTextColor(getResources().getColor(R.color.text_green));
		} else if (curStatus == 3 || curStatus == 4) {// 部分还清
			System.out.println("当前状态== " + curStatus + "  部分还清");
			img_weihuan.setBackgroundResource(R.drawable.btn_emoticon_no_select);
			img_bufen.setBackgroundResource(R.drawable.btn_emoticon_some_unselect);
			img_finish.setBackgroundResource(R.drawable.btn_emoticon_yes_select);

			tv_weihuan.setTextColor(getResources().getColor(R.color.text_hint));
			tv_bufen.setTextColor(getResources().getColor(R.color.text_green));
			tv_finish.setTextColor(getResources().getColor(R.color.text_hint));
		}
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_guid1:
			viewPager.setCurrentItem(0);
			break;
		case R.id.tv_guid2:
			viewPager.setCurrentItem(1);
			break;
		case R.id.tv_guid3:
			viewPager.setCurrentItem(2);
			break;
		case R.id.tv_guid4:
			viewPager.setCurrentItem(3);
			break;
		case R.id.tv_hotline:
			if (!"未知".equals(tv_hotline)) {
				Intent intentcall = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tv_hotline.getText().toString()));
				intentcall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intentcall);
			}
			break;
		case R.id.tv_rengong:
			if (!"未知".equals(tv_rengong)) {
				Intent intentcall = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tv_rengong.getText().toString()));
				intentcall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intentcall);
			}
			break;
		case R.id.tv_jinji:
			if (!"未知".equals(tv_jinji)) {
				Intent intentcall = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tv_jinji.getText().toString()));
				intentcall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intentcall);
			}
			break;
		case R.id.btn_huan: // 一键还款
			if (outflag) {
				ToastManage.showToast("账单已逾期超过3天,请重新收取账单");
				return;
			}
			if (spf.getString(Constant.USER_AUTH, "").equals("1")) {
				// 再 判断 是否有未还款的
				// 以及判断 是否绑定信用卡（/repayment/rightaway_repayment）
				getCardType();
			} else {
				ToastManage.showToast("没有实名认证");
				Intent intent3 = new Intent(CreditcardActivity.this, UserInfoVerificationActivity.class);
				startActivity(intent3);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
			break;
		case R.id.rela_bufen:
			if (curStatus == 0) {// 未还款的时候，可以点
				showChangeDialog(3);
			} else {
				// ToastManage.showToast("此状态不可更改");
				return;
			}
			break;
		case R.id.rela_finish:
			if (curStatus != 1 && curStatus != 2) {// 可以点
				showChangeDialog(1);
			} else {
				// ToastManage.showToast("此状态不可更改");
				return;
			}
			break;
		default:
			break;
		}
	}

	public void showChangeDialog(final int changestatus) {
		DialogChangeStatus dr = new DialogChangeStatus(CreditcardActivity.this, changestatus, new EventHandleListener() {
			@Override
			public void eventRifhtHandlerListener() {
			}

			@Override
			public void eventLeftHandlerListener() {
				changeStatus(changestatus);
			}
		});
		dr.createMyDialog();
	}

	// 获取 账单的状态（还款提醒还是 绑定信用卡，）
	public void getCardType() {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("bill_id", curcardInfo.getId());
		System.out.println("参数 ==" + data);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.RIGHTAWAY_REPAYMENT, params, BaseData.class, null, cardtypeListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> cardtypeListener() {
		return new Listener<BaseData>() {
			@Override
			public void onResponse(BaseData response) {
				System.out.println(" 获取 账单的状态 返回code == " + response.code);
				dialogHelper.stopProgressDialog();
				if (response.code.equals("1038")) { // 1038
					// 弹出设置交易密码框
					dsp = new DialogSetPwd(CreditcardActivity.this, 1, 0, 0);
					dsp.createMyDialog();
				} else if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					String dataString = new String(b);
					if (dataString != null && !dataString.equals("")) {
						System.out.println(" 获取 账单的状态== " + dataString);
						JSONObject s = JSON.parseObject(dataString);
						System.out.println("RIGHTAWAY_REPAYMENT type数据=" + s.getString("type"));
						int type = s.getInteger("type");
						judgeType(type);
					}
				} else {
					ToastManage.showToast(response.desc);
				}
			}
		};
	}

	public void judgeType(int type) {
		if (type == 1) { // 还款提醒
			DialogGoRepay dr = new DialogGoRepay(CreditcardActivity.this, new EventHandleListener() {
				@Override
				public void eventRifhtHandlerListener() {
				}

				@Override
				public void eventLeftHandlerListener() {
					Intent my_help_repayment = new Intent(CreditcardActivity.this, MyLoanActivity.class);
					startActivity(my_help_repayment);
					overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				}
			});
			dr.createMyDialog();
		} else if (type == 2) { // 绑定信用卡
			DialogBindCredit dr = new DialogBindCredit(CreditcardActivity.this, new EventHandleListener() {
				@Override
				public void eventRifhtHandlerListener() {
				}

				@Override
				public void eventLeftHandlerListener() {
					// 绑定信用卡
					Intent bindaredit = new Intent(CreditcardActivity.this, MainAddBluecardActivity.class);
					int account_id = curcardInfo.getAccount_id();
					System.out.println("account_id == " + account_id);
					System.out.println("bank_name=getCard_bank " + curcardInfo.getCard_bank());
					bindaredit.putExtra("account_id", account_id);
					bindaredit.putExtra("bank_name", curcardInfo.getCard_bank());
					startActivity(bindaredit);
					overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				}
			});
			dr.createMyDialog();
		} else {
			// 本地- 申请代还款
			Intent apply_repayment = new Intent(CreditcardActivity.this, ApplyRepayActivity.class);
			apply_repayment.putExtra("id", curcardInfo.getId());
			System.out.println("id == " + curcardInfo.getId());
			startActivity(apply_repayment);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		}
	}

	// 手动修改账单状态
	public void changeStatus(int changes) {
		changedStatus = changes;
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("is_status", changes);
		data.put("id", curcardInfo.getId());
		System.out.println("参数 ==" + data);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.OVER_REPAYMENT, params, BaseData.class, null, changeListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> changeListener() {
		return new Listener<BaseData>() {
			@Override
			public void onResponse(BaseData response) {
				dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					curStatus = changedStatus;
					ToastManage.showToast("手动更改成功!");
					SPHelper.setRefresh(true);
					System.out.println("修改成功 curStatus == " + curStatus);
					if (curStatus == 1) {
						btn_huan.setText("");
						btn_huan.setBackgroundResource(R.drawable.img_progress_seal_finish);
						btn_huan.setVisibility(View.VISIBLE);
						btn_huan.setClickable(false);
					}
					initStatus(curStatus);
				}
			}
		};
	}

	// 当页面滑动时，开始动画并跳出Toast
	public class MyOnPageChangeListener implements OnPageChangeListener {
		private int one = offset * 2 + bmpW; // 页面1到页面2的偏移量

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		// 即将要被显示的页卡的index
		@Override
		public void onPageSelected(int arg0) {
			// 初始化移动的动画（从当前位置，x平移到即将要到的位置）
			Animation animation = new TranslateAnimation(currIndex * one, arg0 * one, 0, 0);
			currIndex = arg0;
			animation.setFillAfter(true); // 动画终止时停留在最后一帧，不然会回到没有执行前的状态
			animation.setDuration(200); // 动画持续时间，0.2秒
			img_cursor.startAnimation(animation); // 是用imageview来显示动画
			int i = currIndex + 1;
			// Toast.makeText(CreditcardActivity.this, "您选择了第" + i + "个页卡",
			// Toast.LENGTH_SHORT).show();
			System.out.println("滑动 page " + i);
		}
	}

	// 查询 账单详情
	private void getCardDetail() {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍后...");
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("id", curcardInfo.getId());
		System.out.println("data= " + data);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		@SuppressWarnings("rawtypes")
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.CARD_DETIAL, params, BaseData.class, null, cardDetailSuccessListener, errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> cardDetailSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			dialogHelper.stopProgressDialog();
			if (response.code.equals("0")) {
				System.out.println(" 账单详情的 数据==  " + response.data);
				byte[] b = Base64.decode(response.data);
				if (b != null && !b.equals("")) {
					String dataString = new String(b);
					System.out.println("data== " + dataString);
					if (!dataString.equals("[]") && !dataString.equals("{}")) {
						curcardInfo = new CardInfo();
						curcardInfo = JSON.parseObject(new String(b), CardInfo.class);
						System.out.println("data==card bank =" + curcardInfo.getBank_name());
						initContentData();
					}
				} else {
					ToastManage.showToast(response.desc);
				}
			}
		}
	};

	@SuppressLint("ShowToast")
	private Response.ErrorListener errorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dialogHelper.stopProgressDialog();
			}
		};
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			System.out.println("定位 经纬度== ");
			System.out.println("定位 经纬度== " + location.getLatitude() + location.getLongitude());
			curlatLng = new LatLng(location.getLatitude(), location.getLongitude());
			if (location == null || mMapView == null) {
				return;
			} else {
				if (curlatLng == null || curcardInfo == null) {
					return;
				} else {
					System.out.println("定位开始 ： " + curbank + "坐标 ： " + curlatLng.latitude + " / " + curlatLng.longitude);
					PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
					nearbySearchOption.location(curlatLng);
					nearbySearchOption.keyword(curbank);
					nearbySearchOption.radius(5000);// 检索半径，单位是米
					// nearbySearchOption.pageNum(1);
					mPoiSearch.searchNearby(nearbySearchOption);
					MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					        .direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
					mBaiduMap.setMyLocationData(locData);
					if (isFirstLoc) {
						isFirstLoc = false;
						LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
						MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
						mBaiduMap.animateMapStatus(u);
						// mBaiduMap.setMapStatus(u);
						System.out.println("定位 经纬度== " + ll.latitude + ll.longitude);
					}
				}
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
			System.out.println("定位 经纬度== ");
		}
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		// TODO Auto-generated method stub
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			// Toast.makeText(BankCardInfoActivity.this, "抱歉，未找到结果",
			// Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(CreditcardActivity.this, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			ToastManage.showToast("抱歉，未找到结果");
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBaiduMap.clear();
			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();
			return;
		}
	}

	private class MyPoiOverlay extends PoiOverlay {
		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			// if (poi.hasCaterDetails) {
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(poi.uid));
			// }
			return true;
		}
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
		// SPHelper.setRefresh(true);
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();
		mPoiSearch.destroy();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}
}
