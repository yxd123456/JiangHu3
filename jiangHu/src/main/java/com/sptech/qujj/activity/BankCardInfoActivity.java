package com.sptech.qujj.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.baidu.mapapi.map.Marker;
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
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.model.BankcardBean;
import com.sptech.qujj.model.UsablebankBean;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.CheckUtil;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

/*
 * 储蓄卡信息
 */
public class BankCardInfoActivity extends Activity implements OnGetPoiSearchResultListener, LocationListener, OnClickListener, OnClickTitleListener {
	private TitleBar titleBar;
	ViewPager viewPager;
	ViewPagerAdapter pagerAdapter;
	View view1, view2; // 页面
	List<View> views; // Tab页面列表
	ImageView img_cursor; // 指示图片
	TextView tv_guid1, tv_guid2; // 页卡头标（网点，客服）
	int offset = 0; // 偏移量
	int currIndex = 0; // 当前页卡编号
	int bmpW; // 图片宽度
	private DialogHelper dialogHelper;
	private SharedPreferences spf;
	// 客服
	private TextView tv_hotline, tv_jinji, tv_rengong;
	HashMap<String, String> cardMap = new HashMap<String, String>();

	private TextView tv_bankcard, tv_realname, tv_cardno;
	private ImageView img_card;
	private BankcardBean bankcardBean;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Tools.addActivityList(this);
		setContentView(R.layout.jh_main_bankcardinfo);
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);

		// // 定位初始化
		// LocationClientOption option = new LocationClientOption();
		// option.setOpenGps(true);// 打开gps
		// option.setCoorType("bd09ll"); // 设置坐标类型
		// option.setScanSpan(3000);
		//
		// mLocClient = new LocationClient(this);
		// mLocClient.registerLocationListener(myListener);

		initImageView();
		initTextView();
		view1 = (View) findViewById(R.layout.cardinfo_wangdian);
		view2 = (View) findViewById(R.layout.cardinfo_kefu);
		LayoutInflater inflater = LayoutInflater.from(this);
		view1 = inflater.inflate(R.layout.cardinfo_wangdian, null);
		view2 = inflater.inflate(R.layout.cardinfo_kefu, null);

		views = new ArrayList<View>();
		views.add(view1);
		views.add(view2);
		pagerAdapter = new ViewPagerAdapter(views);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

		// 地图初始化
		mMapView = (MapView) view1.findViewById(R.id.bmapView);
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
		if (!CheckUtil.isOPen(BankCardInfoActivity.this)) {
			// ToastManage.showToast("Gps 定位ok");
		} else {
			// JSONObject result = JSCall.GeoLocation();
			// try {
			// String conteString = result.getString("content");
			// System.out.println("content == " + conteString);
			// // String xString= conteString
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

		}
		initView();
	}

	private void initView() {
		dialogHelper = new DialogHelper(this);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		cardMap = DataFormatUtil.getCardMap(spf);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.setOnClickTitleListener(this);

		tv_bankcard = (TextView) findViewById(R.id.tv_bankcard);// 开户行
		tv_realname = (TextView) findViewById(R.id.tv_realname);// 姓名
		tv_cardno = (TextView) findViewById(R.id.tv_cardno);// 卡号
		img_card = (ImageView) findViewById(R.id.img_card);

		// 客服
		tv_hotline = (TextView) view2.findViewById(R.id.tv_hotline);
		tv_jinji = (TextView) view2.findViewById(R.id.tv_jinji);
		tv_rengong = (TextView) view2.findViewById(R.id.tv_rengong);

		tv_hotline.setOnClickListener(this);
		tv_jinji.setOnClickListener(this);
		tv_rengong.setOnClickListener(this);
		bankcardBean = (BankcardBean) getIntent().getSerializableExtra("bankcard");
		if (bankcardBean != null) {
			initCardInfo();
			// mPoiSearch.searchNearby(new
			// PoiNearbySearchOption().keyword("建设银行").location(arg0)pageCapacity(10).pageNum(1).radius(3000));
			// System.out.println("dddddddddddddd");
		}
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		// TODO Auto-generated method stub
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			// Toast.makeText(BankCardInfoActivity.this, "抱歉，未找到结果",
			// Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(BankCardInfoActivity.this, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT).show();
			// PoiOverlay overlay = new PoiOverlay(mBaiduMap);
			// overlay.setData(result);
			// mBaiduMap.setOnMarkerClickListener(overlay);
			// overlay.addToMap();
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

	// 赋值
	private void initCardInfo() {
		String cardbank = bankcardBean.getCard_bank();
		System.out.println("cardbank == " + cardbank);
		if (!"".equals(cardbank)) {
			tv_bankcard.setText(cardbank);
		}
		String realname = bankcardBean.getCard_realname();
		if (!"".equals(realname)) {
			tv_realname.setText(DataFormatUtil.hideFirstname(realname));
		}
		String cardno = bankcardBean.getCard_no();
		if ("".equals(cardno)) {
			tv_cardno.setText("未知");
		} else {
			tv_cardno.setText(DataFormatUtil.bankcardsaveFour(cardno));
		}
		titleBar.bindTitleContent(cardbank, R.drawable.jh_back_selector, 0, "", "");

		if (!"".equals(cardbank)) {
			String cardStream = "";
			cardStream = cardMap.get(cardbank);
			// System.out.println("cardStream== " + cardStream);
			if (cardStream == null || "".equals(cardStream)) {
				img_card.setImageResource(R.drawable.img_nobank);
			} else {
				byte[] b = Base64.decode(cardStream);
				Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
				img_card.setImageBitmap(bit);
			}
		}

		// 客服
		// getBlueCardInfo
		UsablebankBean usablebankBean = null;
		usablebankBean = DataFormatUtil.getCardInfo(spf, cardbank);
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
		offset = (screenW / 2 - bmpW) / 2; // 获取图片偏移量
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

		tv_guid1.setOnClickListener(this);
		tv_guid2.setOnClickListener(this);
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
		default:
			break;
		}
	}

	// @Override
	// protected boolean onTap(int i) {
	// super.onTap(i);
	// MKPoiInfo info = getPoi(i);
	// if (info.hasCaterDetails) {
	// mSearch.poiDetailSearch(info.uid);
	// }
	// return true;
	// }
	//
	// }
	//
	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
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

	//
	// @Override
	// protected void onRestoreInstanceState(Bundle savedInstanceState) {
	// super.onRestoreInstanceState(savedInstanceState);
	// mMapView.onRestoreInstanceState(savedInstanceState);
	// }

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
				// mPoiSearch.searchInCity(new
				// PoiCitySearchOption().city("杭州").keyword("建设银行").pageNum(10));

				if (curlatLng == null || bankcardBean == null) {
					return;
				} else {
					String bank = "";
					bank = bankcardBean.getCard_bank();
					System.out.println("定位开始 ： " + bankcardBean.getCard_bank() + "坐标 ： " + curlatLng.latitude + " / " + curlatLng.longitude);
					PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
					nearbySearchOption.location(curlatLng);
					nearbySearchOption.keyword(bank);
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

	private void showLocation(final Marker marker) { // 显示气泡
		// 创建InfoWindow展示的view
		LatLng pt = null;
		double latitude, longitude;
		latitude = marker.getPosition().latitude;
		longitude = marker.getPosition().longitude;

		View view = LayoutInflater.from(this).inflate(R.layout.map_item, null); // 自定义气泡形状
		TextView textView = (TextView) view.findViewById(R.id.my_postion);
		pt = new LatLng(latitude + 0.0004, longitude + 0.00005);

		System.out.println("marker.getTitle == " + marker.getTitle());
		textView.setText(marker.getTitle());

		// 定义用于显示该InfoWindow的坐标点
		// 创建InfoWindow的点击事件监听者
		// OnInfoWindowClickListener listener = new OnInfoWindowClickListener()
		// {
		// public void onInfoWindowClick() {
		// mBaiduMap.hideInfoWindow();// 影藏气泡
		// }
		// };

		// 创建InfoWindow
		// mInfoWindow = new InfoWindow(view, pt, listener);
		mInfoWindow = new InfoWindow(view, pt, 0);
		mBaiduMap.showInfoWindow(mInfoWindow); // 显示气泡

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

}
