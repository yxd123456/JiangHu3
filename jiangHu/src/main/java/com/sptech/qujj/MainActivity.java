package com.sptech.qujj;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Mac;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;import android.graphics.PaintFlagsDrawFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.Time;
import android.util.LayoutDirection;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import u.aly.br;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.sptech.qujj.activity.AddMailActivity;
import com.sptech.qujj.activity.ApplyRepayActivity;
import com.sptech.qujj.activity.CardListActivity;
import com.sptech.qujj.activity.ComInfoActivity;
import com.sptech.qujj.activity.CreditcardActivity;
import com.sptech.qujj.activity.GestureSetActivity;
import com.sptech.qujj.activity.HandapplyActivity;
import com.sptech.qujj.activity.LendMoneyActivity;
import com.sptech.qujj.activity.LiCaiActivity;
import com.sptech.qujj.activity.MailActivity;
import com.sptech.qujj.activity.MainAddBluecardActivity;
import com.sptech.qujj.activity.MessageActivity;
import com.sptech.qujj.activity.MyLoanActivity;
import com.sptech.qujj.activity.ProductDetailInfoActivity;
import com.sptech.qujj.activity.UserInfoVerificationActivity;
import com.sptech.qujj.activity.WebViewActivity;
import com.sptech.qujj.adapter.LiteFragmentAdapter;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.db.SPHelper;
import com.sptech.qujj.dialog.CustomDialog;
import com.sptech.qujj.dialog.DialogActivity;
import com.sptech.qujj.dialog.DialogBindCredit;
import com.sptech.qujj.dialog.DialogGoRepay;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.dialog.DialogSetPwd;
import com.sptech.qujj.fragment.LiteFragment;
import com.sptech.qujj.fragment.MainFragment;
import com.sptech.qujj.fragment.MenuLeftFragment;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.Adver;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.CardInfo;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.AppUtil;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.DateManage;
import com.sptech.qujj.util.DensityUtil;
import com.sptech.qujj.util.DoubleClickExitHelper;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.EventHandleListener;
import com.sptech.qujj.view.VersionUpdateDialog;
import com.umeng.socialize.utils.Log;

public class MainActivity extends SlidingFragmentActivity implements OnClickListener {
	
	SparseArray<MainFragment> arrFragment = new SparseArray<MainFragment>();

	public int currentPage = -1;
	private Button btn_left;// 左边菜单
	private Button btn_right;// 消息
	private Button btn_renzheng;//完善信息
	private LinearLayout ll_btn;
	private Button btn_right_ye;

	private ViewPager mViewPager;
	private ArrayList<View> mPageViews;
	private ImageView mImageView;
	private ImageView[] mImageViews;
	LayoutInflater mInflater;
	// private ViewGroup indicatorViewGroup;
	private LinearLayout preLayout;// 显示几分之几
	private ViewGroup mainViewGroup;
	private Button btn_info; // 提示
	private final int ACTION_LIST = 1;
	private CardInfo dialogcard; // 传递到dialog的实体
	boolean refresh = false;// 跳到首页，是否显示主界面

	private final int ACTION_REFERSH = ACTION_LIST + 1;
	private final int SHOW_TOAST = ACTION_REFERSH + 1;

	List<CardInfo> curCardInfos = new ArrayList<CardInfo>();
	private RelativeLayout rela_havadata; // 首页有数据，空数据两种情况；
	LinearLayout rela_nodata;
	private RelativeLayout rela_warning, rela_addmail, rela_handapply, rela_licai;// 首页提示信息,添加账单,手动申请,理财中心;
	private RelativeLayout rela_addmailed, rela_handapplyed, rela_licaied;// 没有数据页面的
	DoubleClickExitHelper doubleClick;
	private DialogHelper dialogHelper;
	private SharedPreferences spf;

	private Boolean adverflag = false;// 是否 请求过广告
	private int home_show;// 首页是否显示 未还款提醒
	private int is_update;
	private CustomDialog downloadDialog;// 下载的弹窗
	// 是否正在下载
	// private boolean isDownloading = false;
	// private ProgressBar progressBar;
	// private TextView tvProgressView;
	private String verionUpdateUrl;// 版本更新Url地址
	private String filePath;// 下载的apk绝对路径
	private SparseArray<ImageView> array = new SparseArray<ImageView>();

	// pageView 控件
	// private View pageView;
	private RelativeLayout rela_card, rela_top; // 信用卡信息
	private Button btn_huan, btn_repay, btn_service;// 一键还款按钮，到期时间(到期日-当前时间),
	private ImageView img_card;// 信用卡图标，
	private TextView tv_cardbank, tv_name, tv_cardno, tv_huanvalue, tv_zhangdanvalue, tv_mianvalue, tv_amount_of, tv_percent; // 银行卡名称,持卡人,卡号,还款期，账单期，免息期,本期应还;
	HashMap<String, String> cardMap = new HashMap<String, String>();

	public static MainActivity curMainActivity;
	private TextView message_num;
	private boolean isshow;
	private Adver curAdver;// 当前广告
	BaseActivity baseActivity = new BaseActivity();
	private DownloadManager downloadManager;
	private DialogSetPwd dsp;
	private MyPagerAdapter myPagerAdapter;
	
	//------------------------------------------------------
	private ViewPager vp_show;
	private List<LiteFragment> list = null;
	private ImageView p1, p2, p3;
	private Timer timer;
	private TimerTask task;
	private int curPage = 1;
	//------------------------------------------------------

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Tools.addActivityList(MainActivity.this);
		
		//------------------------------------------------------
		list = new ArrayList<LiteFragment>();
		list.add(new LiteFragment("理财", "拒绝月光，用零钱增值 【去了解】"));
		list.add(new LiteFragment("帮你还", "直接帮助还款一键操作 【去了解】"));
		list.add(new LiteFragment("借你钱", "我借贷你收钱自己还账 【去了解】"));
		list.add(new LiteFragment("理财", "拒绝月光，用零钱增值 【去了解】"));
		list.add(new LiteFragment("帮你还", "直接帮助还款一键操作 【去了解】"));
		
		//------------------------------------------------------

		downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		curMainActivity = this;
		mInflater = getLayoutInflater();
		mPageViews = new ArrayList<View>();
		doubleClick = new DoubleClickExitHelper(MainActivity.this);
		

		dialogHelper = new DialogHelper(this);
		spf = this.getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);

		cardMap = DataFormatUtil.getBlueCardMap(spf);
		isshow = getIntent().getBooleanExtra("isShow", true);
		System.out.println(" onCreate() -- isshow ==  " + isshow);

		// System.out.println("asdfasd initView" + refresh);
		mImageViews = new ImageView[mPageViews.size()];
		mainViewGroup = (ViewGroup) mInflater.inflate(R.layout.activity_main, null);

		rela_havadata = (RelativeLayout) mainViewGroup.findViewById(R.id.rela_havadata);
		rela_nodata = (LinearLayout) mainViewGroup.findViewById(R.id.rela_nodata);

		mViewPager = (ViewPager) mainViewGroup.findViewById(R.id.myviewpager);

		preLayout = (LinearLayout) mainViewGroup.findViewById(R.id.mybottomviewgroup);
		setContentView(mainViewGroup);
		initSlideMenu();
		initView();
		timer = new Timer();
		
		task = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					

					@Override
					public void run() {
						// TODO Auto-generated method stub
						switch (curPage) {
						case 1:
							vp_show.setCurrentItem(1);

							break;
						case 2:
							vp_show.setCurrentItem(2);

							break;
						case 3:
							vp_show.setCurrentItem(3);

							break;									
						}
						curPage++;
						if(curPage == 4) curPage = 1;
					}
				});				
			}
		};
		
		timer.schedule(task, 0, 3000);
		
		vp_show.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					break;				}
				return false;
			}
		});
		
		vp_show.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});

		if (isshow) {
			if (spf.getBoolean(Constant.IS_SHOWSETHANDPWD + spf.getString(Constant.USER_PHONE, ""), true)) {
				ToastManage.showToast("请先设置手势密码");
				Intent in = new Intent(MainActivity.this, GestureSetActivity.class);
				startActivity(in);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		}
	}

	private void initView() {
		ll_btn = (LinearLayout) findViewById(R.id.btn_ll);
		btn_renzheng = (Button) findViewById(R.id.btn_renzheng);
		vp_show = (ViewPager) findViewById(R.id.viewPager);
		btn_left = (Button) findViewById(R.id.btn_left);
		btn_right = (Button) findViewById(R.id.btn_right);
		message_num = (TextView) findViewById(R.id.message_num);
		// btn_info = (Button) findViewById(R.id.btn_info);
		// btn_info.setOnClickListener(this);
		rela_warning = (RelativeLayout) findViewById(R.id.rela_warning); // 提示信息
		tv_percent = (TextView) findViewById(R.id.tv_percent);// 百分比
		// 提示信息.
		// rela_warning.setVisibility(View.VISIBLE);
		// rela_warning.setOnClickListener(this);
		// System.out.println("asdfasd initView" + refresh);

		btn_left.setOnClickListener(this);
		btn_right.setOnClickListener(this);
		btn_renzheng.setOnClickListener(this);
		if(Constant.FLAG_KAIQITONGXUNLU==true&&Constant.FLAG_KAIQITONGXUNLU==true){
			ll_btn.setVisibility(View.GONE);
		}
		rela_addmail = (RelativeLayout) findViewById(R.id.rela_addmail);
		rela_handapply = (RelativeLayout) findViewById(R.id.rela_handapply);
		rela_licai = (RelativeLayout) mainViewGroup.findViewById(R.id.rela_licai); // 理财中心

		rela_licaied = (RelativeLayout) mainViewGroup.findViewById(R.id.rela_licaied);
		rela_addmailed = (RelativeLayout) mainViewGroup.findViewById(R.id.rela_addmailed);
		rela_handapplyed = (RelativeLayout) mainViewGroup.findViewById(R.id.rela_handapplyed);
		rela_addmail.setOnClickListener(this);
		rela_handapply.setOnClickListener(this);
		rela_addmailed.setOnClickListener(this);
		rela_handapplyed.setOnClickListener(this);
		rela_licai.setOnClickListener(this);
		rela_licaied.setOnClickListener(this);
		// handler.sendEmptyMessage(ACTION_LIST);
		checkUpdate();
		vp_show.setAdapter(new LiteFragmentAdapter(getSupportFragmentManager(), list));
		vp_show.setCurrentItem(1);
		p1 = (ImageView) findViewById(R.id.p1);
		p2 = (ImageView) findViewById(R.id.p2);
		p3 = (ImageView) findViewById(R.id.p3);
		
		vp_show.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				p1.setImageResource(R.drawable.indicator_pointer_grey);
				p2.setImageResource(R.drawable.indicator_pointer_grey);
				p3.setImageResource(R.drawable.indicator_pointer_grey);
				switch (arg0) {
				case 0:
					vp_show.setCurrentItem(3, false);
					p3.setImageResource(R.drawable.indicator_pointer_fg);
					break;
				case 1:
					p1.setImageResource(R.drawable.indicator_pointer_fg);
					break;
				case 2:
					p2.setImageResource(R.drawable.indicator_pointer_fg);
					break;
				case 3:
					p3.setImageResource(R.drawable.indicator_pointer_fg);
					break;
				case 4:
					vp_show.setCurrentItem(1, false);
					p1.setImageResource(R.drawable.indicator_pointer_fg);
					break;
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	private void closeApp() {
		// TODO Auto-generated method stub
		baseActivity.closeActivity();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ACTION_REFERSH:
				initHomepageData();
				break;
			case SHOW_TOAST:
				final Adver curAdver = (Adver) msg.obj;
				System.out.println("curAdver==" + curAdver.getTitle());
				DialogActivity dr = new DialogActivity(MainActivity.this, curAdver, new EventHandleListener() {
					@Override
					public void eventRifhtHandlerListener() {
					}

					@Override
					public void eventLeftHandlerListener() {
						// 领红包
						// ToastManage.showToast("领红包");
						// 跳到网页里
						Intent rela_activity = new Intent(MainActivity.this, WebViewActivity.class);
						int type = curAdver.getUrl_type();
						if (type == 0) { // 内链
							rela_activity.putExtra("url", JsonConfig.HTML + curAdver.getAd_url());
							rela_activity.putExtra("title", curAdver.getTitle());
							startActivity(rela_activity);
							overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
						} else if (type == 1) { // 外链
							rela_activity.putExtra("url", curAdver.getAd_url());
							rela_activity.putExtra("title", curAdver.getTitle());
							startActivity(rela_activity);
							overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
						} else if (type == 2) {
							// 解析Jason 跳本地
							String goString = curAdver.getAd_url();// json
							JSONObject s = JSON.parseObject(goString);
							String location = s.getString("1");
							String id = s.getString("2");
							System.out.println("location == " + location);
							System.out.println("id( int ) == " + Integer.parseInt(id));
							if ("product".equals(location)) {
								Intent intentproduct = new Intent(MainActivity.this, ProductDetailInfoActivity.class);
								intentproduct.putExtra("id", Integer.parseInt(id));
								startActivity(intentproduct);
								overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
							}

						}

						// closeDialog();
					}
				});
				dr.createMyDialog();
				break;
			default:
				break;
			}
		}
	};
	private ArrayList<CardInfo> yeList;
	private boolean  flag = true;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		hideLeftView();
		getMessagenum();
		// boolean isBackflag =
		// AppUtil.isApplicationBroughtToBackground(curMainActivity);
		// System.out.println("onResume--isBackGround= " + isBackflag);
		System.out.println("getRefresh -=== " + SPHelper.getRefresh());
		if (SPHelper.getRefresh()) {
			// initMainData();
			spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
			getCardList();
			SPHelper.setRefresh(false);
		}
		// myPagerAdapter.notifyDataSetChanged();
		// mViewPager.setAdapter(new MyPagerAdapter());
		// mViewPager.notify();

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
				// initMainData();
			}
		};
	}

	// 初始化ViewPager 数据
	@SuppressLint("NewApi")
	private void initHomepageData() {
		// 根据数据添加page
		// mViewPager.removeAllViews();
		
		
		

		if (curCardInfos.size() != 0) {
			rela_havadata.setVisibility(View.VISIBLE);
			rela_nodata.setVisibility(View.GONE);
			// indicatorViewGroup.removeAllViews();
			mPageViews.clear();
			for (int i = 0; i < curCardInfos.size(); i++) {
				
				View pageView = mInflater.inflate(R.layout.home_viewpage1, null);
				rela_card = (RelativeLayout) pageView.findViewById(R.id.rela_card);
				rela_top = (RelativeLayout) pageView.findViewById(R.id.rela_top);
				btn_right_ye = (Button) pageView.findViewById(R.id.btn_right_ye);
				btn_service = (Button) pageView.findViewById(R.id.btn_service);
				btn_service.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
						intent.putExtra("url", JsonConfig.HTML + "/index/service");
						intent.putExtra("title", "服务中心");
						startActivity(intent);
						overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					}
				});
				// 赋值
				final CardInfo cardInfo = curCardInfos.get(i);
				yeList = new ArrayList<CardInfo>();
				yeList.add(cardInfo);
				img_card = (ImageView) pageView.findViewById(R.id.img_card);
				btn_huan = (Button) pageView.findViewById(R.id.btn_huan);
				

				// 进入信用卡详情
				rela_top.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						int bill_id = cardInfo.getId();
						Intent intent3 = new Intent(MainActivity.this, CreditcardActivity.class);
						// Intent intent3 = new Intent(MainActivity.this,
						// HtmlActivity.class);
						// intent3.putExtra("act", "credit_card_disp");
						// intent3.putExtra("bill_id", bill_id);
						intent3.putExtra("cardInfo", cardInfo);
						startActivity(intent3);
						overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					}
				});

				// 申请还款
				btn_right_ye.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						// 先判断是否实名认证
						Intent intent3;
						if (Integer.parseInt(DateManage.getDayFormUX(cardInfo.getRepayment_date() - (System.currentTimeMillis() / 1000))) < -3) {
							ToastManage.showToast("账单已逾期超过3天,请重新收取账单");
							return;
						}
						// 已认证
						if (spf.getString(Constant.USER_AUTH, "").equals("1")) {
							// 再 判断 是否有未还款的
							// 以及判断 是否绑定信用卡（/repayment/rightaway_repayment）
							getCardType(cardInfo);
						} else {
							ToastManage.showToast("没有实名认证");
							intent3 = new Intent(MainActivity.this, UserInfoVerificationActivity.class);
							startActivity(intent3);
							overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
						}

						// Intent apply_repayment = new
						// Intent(MainActivity.this, ApplyRepayActivity.class);
						// apply_repayment.putExtra("id", dialogcard.getId());
						// startActivity(apply_repayment);
						// overridePendingTransition(R.anim.push_left_in,
						// R.anim.push_left_out);

					}
				});

				btn_repay = (Button) pageView.findViewById(R.id.btn_repay);
				tv_cardbank = (TextView) pageView.findViewById(R.id.tv_cardbank);
				tv_name = (TextView) pageView.findViewById(R.id.tv_name);
				tv_cardno = (TextView) pageView.findViewById(R.id.tv_cardno);
				tv_huanvalue = (TextView) pageView.findViewById(R.id.tv_huanvalue);
				tv_zhangdanvalue = (TextView) pageView.findViewById(R.id.tv_zhangdanvalue);
				tv_mianvalue = (TextView) pageView.findViewById(R.id.tv_mianvalue);
				tv_amount_of = (TextView) pageView.findViewById(R.id.tv_amount_of);

				tv_amount_of.setText("¥" + DataFormatUtil.floatsaveTwo(cardInfo.getAmount_of()));
				tv_cardbank.setText(cardInfo.getCard_bank());

				if (!cardInfo.getCard_realname().equals("")) {
					tv_name.setText(DataFormatUtil.hideFirstname(cardInfo.getCard_realname()));
				} else {
					tv_name.setText("");
				}

				if (!cardInfo.getCard_no().equals("")) {
					tv_cardno.setText(DataFormatUtil.bankcardsaveFour(cardInfo.getCard_no()));
				}

				// tv_cardno.setText("3818");
				String cardname = "";
				cardname = cardInfo.getCard_bank();

				// System.out.println(" card name " + cardname);
				String cardStream = "";
				cardStream = cardMap.get(cardname);
				// System.out.println(" cardStream " + cardStream);
				if (cardStream == null || cardStream.equals("")) {
					img_card.setImageResource(R.drawable.img_nobank);
				} else {
					byte[] b = Base64.decode(cardStream);
					Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
					img_card.setImageBitmap(bit);
				}
				// 时间
				// System.out.println("当前时间==  " + System.currentTimeMillis() /
				// 1000 + "");
				// System.out.println("还款日== " + cardInfo.getRepayment_date());
				// System.out.println("账单期== " + cardInfo.getBill_cycle_set());

				// System.out.println("当前时间戳====  " +
				// (System.currentTimeMillis() / 1000) + " ");
				// System.out.println("还款时间戳==== " + repay_date + " ");
				// System.out.println("还款时间差==between2== " + between2 + "");
				// // System.out.println("账单时间差==between==  " +
				// // DateManage.getDayFormUX(between) + "天");
				// System.out.println("还款时间差==between2== " +
				// DateManage.getDayFormUX(between2) + "天");

				System.out.println("账单期== " + cardInfo.getBill_cycle_end());
				// System.out.println("账单期== " + cardInfo.getBill_month());

				int repay_date = cardInfo.getRepayment_date();// 还款日
				int bill_cycle = cardInfo.getBill_cycle_end();// 账单日
				// 账单日 与当前时间的 时间差
				Long between = (bill_cycle - (System.currentTimeMillis() / 1000));
				// 还款日 与当前时间的 时间差
				Long between2 = (repay_date - (System.currentTimeMillis() / 1000));
				int status = cardInfo.getIs_status();
				// 先判断 账单期 是否到
				if (between <= 0) {
					// 再判断 还款日
					if (between2 < 0) {
						if (status == 1 || status == 2 || cardInfo.getAmount_of() == 0) {
							// 一键还按钮 灰色 ，不可点击
							//btn_right_ye.setBackgroundResource(R.drawable.btn_home_round_noselect);
							//btn_right_ye.setEnabled(false);
						} else {
							// 本地判断逾期 时间是否超过 三天
							btn_repay.setBackgroundResource(R.drawable.home_img_remindered);
							// btn_repay.setText("逾期   " +
							// DateManage.getDayFormUX(-between2) + "天");
							// parseInt((now - data.repayment_date) / 24 / 3600)
							int late = Math.round((-between2) / 24 / 3600);
							// if (late == 0) {
							// btn_repay.setText("今天到期");
							// } else {
							if (late > 99) {
								btn_repay.setText("逾期 99+ 天");
							} else {
								btn_repay.setText("逾期 " + late + " 天");
							}
							// }
						}
					} else if (between2 == 0) {
						btn_repay.setBackgroundResource(R.drawable.home_img_remindeblue);
						btn_repay.setText("今天到期");
					} else {
						btn_repay.setBackgroundResource(R.drawable.home_img_remindeblue);
						btn_repay.setText(DateManage.getDayFormUX(between2) + "天  到期");
					}
				}

				// else {
				// btn_repay
				// .setBackgroundResource(R.drawable.img_home_reminderedgray);
				// btn_repay.setText("出账   "
				// + DateManage.getDayFormUX(between) + "天");
				// // 还款按钮不能点
				// btn_huan.setBackgroundResource(R.drawable.btn_home_round_noselect);
				// btn_huan.setEnabled(false);
				// }

				// 还清且账单期未到-提示新账单未出账
				if (status == 1 || status == 2 || cardInfo.getAmount_of() == 0) {
					if (between < 0) {
						btn_repay.setBackgroundResource(R.drawable.img_home_reminderedgray);
						// btn_repay.setText("未出账");
						// btn_repay.setText("出账" +
						// DateManage.getDayFormUX(between) + "天");
						// 账单期 加一个月
						// Long newBill_cycle = (repay_date -
						// (System.currentTimeMillis() / 1000));
						long newbilltime = (long) cardInfo.getBill_cycle_end() * 1000;
						Date newbill = new Date(newbilltime);
						System.out.println("newbill ==" + newbill.getTime());
						newbill.setMonth(newbill.getMonth() + 1);
						System.out.println("newbillTime ==" + newbill.getTime());
						// long newbetweenbill = Math.round(((newbill.getTime()
						// / 1000) - (System.currentTimeMillis() / 1000)) / 24 /
						// 3600);
						long newbetweenbill = newbill.getTime() / 1000 - (System.currentTimeMillis() / 1000);
						System.out.println("newbetweenbill==" + newbetweenbill);
						int newdate = Integer.parseInt(DateManage.getDayFormUX(newbetweenbill));
						if (newdate > 99) {
							btn_repay.setText("出账 99+天");
						} else if (newdate < -99) {
							btn_repay.setText("出账 -99天");
						} else {
							if (newdate == 0) {
								btn_repay.setText("今日出账");
							} else {
								btn_repay.setText("出账 " + newdate + " 天");
							}
						}
						// btn_repay.setText("出账 " +
						// DateManage.getDayFormUX(newbetweenbill) + " 天");
						// var end = new Date(data.bill_cycle_end * 1000);
						// end.setMonth(end.getMonth() + 1);
						// var day = Math.round((Math.round(end.getTime() /
						// 1000) - now) / 24 / 3600);
						tv_amount_of.setTextSize(18);
						tv_amount_of.setText("新账单还没有出账");
						tv_amount_of.setTextColor(Color.parseColor("#737373"));
						// 一键还按钮 灰色 ，不可点击
						//btn_right_ye.setBackgroundResource(R.drawable.btn_home_round_noselect);
						//btn_right_ye.setEnabled(false);
					}
					if (bill_cycle == 0) {
						btn_repay.setBackgroundResource(R.drawable.img_home_reminderedgray);
						btn_repay.setText("未出账");
					}

					//btn_right_ye.setBackgroundResource(R.drawable.btn_home_round_noselect);
					btn_right_ye.setText("已还清");
					// 灰色帮你还
					if (status == 0 && cardInfo.getAmount_of() == 0) {
						//btn_right_ye.setBackgroundResource(R.drawable.btn_home_round_noselect);
						btn_right_ye.setText("帮你还");
					}
				}
				if (repay_date != 0) {
					String huanString = DateManage.getMonthDay(repay_date + "");
					tv_huanvalue.setText(huanString);
					System.out.println("还款期" + huanString);
				} else {
					tv_huanvalue.setText("未知");
				}

				if (cardInfo.getBill_cycle_end() != 0) {
					String zhangString = DateManage.getMonthDay(cardInfo.getBill_cycle_end() + "");
					tv_zhangdanvalue.setText(zhangString);
					System.out.println("账单期" + zhangString);
				} else {
					tv_zhangdanvalue.setText("未知");
				}

				if (cardInfo.getBill_cycle_set() != 0 && cardInfo.getRepayment_date() != 0) {
					long mian = (long) repay_date - cardInfo.getBill_cycle_set();
					// System.out.println("免  期 时间差 " + mian);
					if (mian > 0) {
						tv_mianvalue.setText(DateManage.getDayFormUX(mian) + "天");// (到期还款日-账单周期(getBill_cycle_end))
						System.out.println("免  期" + DateManage.getDayFormUX(mian));
					}
				} else {
					tv_mianvalue.setText("未知");
				}

				System.out.println("mPageViews.add(pageView)!");
				mPageViews.add(pageView);
				MainFragment fragment = new MainFragment(pageView);
				arrFragment.append(i, fragment);
			}

			mImageViews = new ImageView[mPageViews.size()];

			if (mImageViews.length == 1) {
				preLayout.setVisibility(View.GONE);
			} else {
				preLayout.setVisibility(View.VISIBLE);
			}

			myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
			mViewPager.setAdapter(myPagerAdapter);
			
			if(flag ){		
				for (int i = 0; i < mImageViews.length; i++) {
					ImageView imageView = null;
					if(array.size() != mImageViews.length){
						imageView = new ImageView(this);
						array.append(i, imageView);
					} else {
						imageView = array.get(i);
					}
					if(i==0){
						imageView.setImageResource(R.drawable.indicator_pointer_fg);
					} else {
						imageView.setImageResource(R.drawable.indicator_pointer_grey);
					}
					LayoutParams params = new LayoutParams(DensityUtil.px2dip(this, 20), DensityUtil.px2dip(this, 20));
					params.setLayoutDirection(0);
					params.setMargins(5, 5, 5, 5);
	
					
					preLayout.addView(imageView, params);
					
				}
				flag = false;
			}
			//tv_percent.setText((1) + "/" + mImageViews.length) // 初始化的时候
			mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int arg0) {
					int total = mImageViews.length;
					preLayout.removeAllViews();
					for (int i = 0; i < total; i++) {
						//ImageView imageView = new ImageView(MainActivity.this);
						ImageView imageView = array.get(i);
						if(i==arg0){
							imageView.setImageResource(R.drawable.indicator_pointer_fg);
						} else {
							imageView.setImageResource(R.drawable.indicator_pointer_grey);
						}
						LayoutParams params = new LayoutParams(DensityUtil.px2dip(MainActivity.this, 20), 
								DensityUtil.px2dip(MainActivity.this, 20));
						params.setLayoutDirection(0);
						params.setMargins(5, 5, 5, 5);
						preLayout.addView(imageView, params);
					}
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onPageScrollStateChanged(int arg0) {
					// TODO Auto-generated method stub
				}
			});
			

		} else {
			rela_havadata.setVisibility(View.GONE);
			rela_nodata.setVisibility(View.VISIBLE);
		}
		dialogHelper.stopProgressDialog();
	}

	// 账单列表
	public void getCardList() {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));

		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(MainActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.CARD_LIST, params, BaseData.class, null, usersuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> usersuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				// System.out.println("账单列表 返回code == " + response.code);

				SPHelper.setRefresh(false);
				if (response.code.equals("0")) {
					curCardInfos.clear();
					byte[] b = Base64.decode(response.data);
					System.out.println("b== " + b);
					String dataString = new String(b);
					System.out.println("账单列表 数据= " + dataString);

					if (!dataString.equals("[]") && !dataString.equals("{}")) {
						List<CardInfo> cardInfos = new ArrayList<CardInfo>();
						cardInfos = JSON.parseArray(new String(b), CardInfo.class);
						curCardInfos.addAll(cardInfos);
						if (cardInfos.size() != 0) {
							System.out.println("账单列表 数据 size== " + cardInfos.size());
							if (handler != null) {
								handler.sendEmptyMessage(ACTION_REFERSH);
							}

						} else {
							System.out.println("账单列表 数据= 0");
						}
					} else {
						dialogHelper.stopProgressDialog();
						System.out.println(" curCardInfos.size()== " + curCardInfos.size());
						if (handler != null) {
							handler.sendEmptyMessage(ACTION_REFERSH);
						}
					}
				} else {
					ToastManage.showToast(response.desc);
				}
			}
		};
	}

	// 获取 账单的状态（还款提醒还是 绑定信用卡，）
	public void getCardType(CardInfo curcardInfo) {
		dialogcard = new CardInfo();
		dialogcard = curcardInfo;
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("bill_id", dialogcard.getId());
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
					dsp = new DialogSetPwd(MainActivity.this, 1, 0, 0);
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
			DialogGoRepay dr = new DialogGoRepay(MainActivity.this, new EventHandleListener() {
				@Override
				public void eventRifhtHandlerListener() {
				}

				@Override
				public void eventLeftHandlerListener() {
					// 去还款-- 到我的借款列表
					// Intent gorepay = new Intent(MainActivity.this,
					// HtmlActivity.class);
					// gorepay.putExtra("act", "my_help_repayment");
					// startActivity(gorepay);
					// overridePendingTransition(R.anim.push_left_in,
					// R.anim.push_left_out);
					// 本地页面
					Intent my_help_repayment = new Intent(MainActivity.this, MyLoanActivity.class);
					startActivity(my_help_repayment);
					overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				}
			});
			dr.createMyDialog();
		} else if (type == 2) { // 绑定信用卡
			DialogBindCredit dr = new DialogBindCredit(MainActivity.this, new EventHandleListener() {
				@Override
				public void eventRifhtHandlerListener() {
				}

				@Override
				public void eventLeftHandlerListener() {
					// 绑定信用卡
					Intent bindaredit = new Intent(MainActivity.this, MainAddBluecardActivity.class);
					int account_id = dialogcard.getAccount_id();
					System.out.println("account_id == " + account_id);
					System.out.println("bank_name= " + dialogcard.getCard_bank());

					bindaredit.putExtra("account_id", account_id);
					bindaredit.putExtra("bank_name", dialogcard.getCard_bank());

					startActivity(bindaredit);
					overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				}
			});
			dr.createMyDialog();
		} else {
			// 进入 网页 -申请代还款
			// Intent apply_repayment = new Intent(MainActivity.this,
			// HtmlActivity.class);
			// apply_repayment.putExtra("act", "apply_repayment");
			// apply_repayment.putExtra("id", dialogcard.getId());
			// startActivity(apply_repayment);
			// overridePendingTransition(R.anim.push_left_in,
			// R.anim.push_left_out);

			// 本地- 申请代还款
			Intent apply_repayment = new Intent(MainActivity.this, ApplyRepayActivity.class);
			apply_repayment.putExtra("id", dialogcard.getId());
			System.out.println("id == " + dialogcard.getId());
			startActivity(apply_repayment);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		}
	}

	// 初始化首页数据 (检查更新之后)
	private void initMainData() {
		// getMessagenum();
		getCardList();
		String today = DateManage.getYearMonthDay(System.currentTimeMillis() / 1000 + "");
		System.out.println("今天的时间 ！！ today== " + today);
		String curday = spf.getString(Constant.DIALOG_ADVER, "");
		System.out.println("上次调接口的时间curday== " + curday);
		if (!today.equals(curday) && is_update != 2) {
			System.out.println("？？ getAdver");
			getAdver();
		}
		// getAdver();
		// SPHelper.setRefresh(true);
		System.out.println("setRefresh true!!!!!!!!!!!!!!!!!!!");
	}

	// 首页的广告
	private void getAdver() {
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("type_id", 1);
		data.put("num", 1);// 首页 一个`位 2 表示 多个轮播
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(data, spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		params.put("data", HttpUtil.getData(data));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(MainActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.GET_AD, params, BaseData.class, null, getAdversuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> getAdversuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				System.out.println("首页广告 返回code == " + response.code);
				if (response.code.equals("0")) {
					if (response.data == null) {
						System.out.println("首页广告ss  ==null ");
					} else {
						byte[] b = Base64.decode(response.data);

						if (b != null && !b.equals("")) {
							System.out.println("首页广告ss  == " + new String(b));
							// JSONObject s = JSON.parseObject(new String(b));
							// int read_num = s.getIntValue("read_num");
							// System.out.println("首页广告 == " + new String(b));
							List<Adver> advers = new ArrayList<Adver>();
							advers = JSON.parseArray(new String(b), Adver.class);
							// 将当前时间保存到本地
							String curToday = DateManage.getYearMonthDay(System.currentTimeMillis() / 1000 + "");
							spf.edit().putString(Constant.DIALOG_ADVER, curToday).commit();
							System.out.println("保存到本地的时间" + curToday);

							// curAdver = new Adver();
							// curAdver = advers.get(0);
							if (advers.get(0) != null) {
								if (handler != null) {
									handler.sendMessage(handler.obtainMessage(SHOW_TOAST, advers.get(0)));
								}
							}
						}
					}
				} else {
					ToastManage.showToast(response.desc);
				}
			}
		};
	}

	private void getMessagenum() {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, ""));
		params.put("sign", HttpUtil.getSign(spf.getString(Constant.UID, ""), spf.getString(Constant.KEY, "")));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(MainActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.STATUSREADMESSAGE, params, BaseData.class, null, messagenumsuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> messagenumsuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				// System.out.println("账单列表 返回code == " + response.code);
				dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					if (b != null && !"".equals(b)) {
						JSONObject s = JSON.parseObject(new String(b));
						int read_num = s.getIntValue("read_num");
						home_show = s.getIntValue("home_show");
						System.out.println("首页信息 返回的 数据--" + new String(b));
						System.out.println("首页信息 返回的 home_show ==  " + home_show);
						if (read_num > 0) {
							message_num.setVisibility(View.VISIBLE);
						} else {
							message_num.setVisibility(View.GONE);
						}
						if (home_show == 1) {
							rela_warning.setVisibility(View.VISIBLE);
						} else {
							rela_warning.setVisibility(View.GONE);
						}
					}
					// System.out.println(s);
				} else {
					ToastManage.showToast(response.desc);
				}
			}
		};
	}

	private void initSlideMenu() {
		Fragment leftMenuFragment = new MenuLeftFragment();
		setBehindContentView(R.layout.left_menu_frame);
		getSupportFragmentManager().beginTransaction().replace(R.id.id_left_menu_frame, leftMenuFragment).commit();
		SlidingMenu menu = getSlidingMenu();
		menu.setMode(SlidingMenu.LEFT);
		// 设置触摸屏幕的模式
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setShadowWidthRes(R.dimen.shadow_width); // 阴影宽度
		menu.setShadowDrawable(R.drawable.shadow); // 阴影
		// 设置滑动菜单视图的宽度
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// 设置渐入渐出效果的值
		menu.setFadeDegree(0.25f);
		// menu.setBehindScrollScale(1.0f);
		menu.setSecondaryShadowDrawable(R.drawable.shadow);
		// getSlidingMenu().showMenu();
		// // 设置右边（二级）侧滑菜单
		// menu.setSecondaryMenu(R.layout.right_menu_frame);
		// Fragment rightMenuFragment = new MenuRightFragment();
		// getSupportFragmentManager().beginTransaction()
		// .replace(R.id.id_right_menu_frame, rightMenuFragment).commit();
	}

	public void hideLeftView() {
		System.out.println("hideLeftView--hide ");
		// getSlidingMenu().toggle();
		getSlidingMenu().showContent(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_renzheng:
			startActivity(new Intent(this, ComInfoActivity.class));
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		case R.id.btn_left:
			System.out.println("hideLeftView--onClick ");
			getSlidingMenu().showMenu();
			break;
		case R.id.btn_right:
			// Intent intent = new Intent(this, CardListActivity.class);
			// startActivity(intent);
			// overridePendingTransition(R.anim.push_left_in,
			// R.anim.push_left_out);
			Intent intent = new Intent(this, MessageActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.btn_info:
			Intent intent2 = new Intent(this, CardListActivity.class);
			startActivity(intent2);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rela_addmail: // 添加账单
			Intent rela_mail;
			// 判断邮箱账单 数量
			String emailnum = spf.getString(Constant.USER_EMAILNUM, "0");
			if (emailnum.equals("0")) { // 0则跳到 添加邮箱页面
				rela_mail = new Intent(MainActivity.this, AddMailActivity.class);
				startActivity(rela_mail);
			} else {
				rela_mail = new Intent(MainActivity.this, MailActivity.class);
				startActivity(rela_mail);
			}
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rela_handapply: // 手动申请
			Intent intent5 = null;
			//LendMoneyActivity
			if (checkReal()) {
				/*intent5 = new Intent(this, HandapplyActivity.class);
				startActivity(intent5);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);*/
				intent5 = new Intent(this, LendMoneyActivity.class);
				startActivity(intent5);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			} else {
				ToastManage.showToast("您尚未实名认证，请先认证");
				intent5 = new Intent(MainActivity.this, UserInfoVerificationActivity.class);
				intent5.putExtra("nextflag", 0);
				startActivity(intent5);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
			break;
		case R.id.rela_addmailed:
			Intent rela_maileds;
			// 判断邮箱账单 数量
			String emailnumed = spf.getString(Constant.USER_EMAILNUM, "0");
			if (emailnumed.equals("0")) { // 0则跳到 添加邮箱页面
				rela_maileds = new Intent(MainActivity.this, AddMailActivity.class);
				startActivity(rela_maileds);
			} else {
				rela_maileds = new Intent(MainActivity.this, MailActivity.class);
				startActivity(rela_maileds);
			}
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rela_handapplyed:
			Intent intent7 = null;
			if (checkReal()) {
				intent7 = new Intent(this, LendMoneyActivity.class);
				startActivity(intent7);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				/*intent7 = new Intent(this, HandapplyActivity.class);
				startActivity(intent7);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);*/
			} else {
				ToastManage.showToast("您尚未实名认证，请先认证");
				intent7 = new Intent(MainActivity.this, UserInfoVerificationActivity.class);
				intent7.putExtra("nextflag", 0);
				startActivity(intent7);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
			break;
		case R.id.rela_licai:
			Intent intent8 = new Intent(this, LiCaiActivity.class);
			startActivity(intent8);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rela_licaied:
			Intent intent9 = new Intent(this, LiCaiActivity.class);
			startActivity(intent9);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		default:
			break;
		}
	}

	public boolean checkReal() {
		boolean flag = false;
		String auth = spf.getString(Constant.USER_AUTH, ""); // 0未认证 1已认证 2认证失败
		System.out.println("USER_AUTH=== " + auth);
		if (auth.equals("1")) {
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}

	// 主界面返回，退出程序提醒
	public void onBackPressed() {
		// showDialog(Constants.EXITDIALOG);
	}

	class MyPagerAdapter extends FragmentStatePagerAdapter {
		
		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return arrFragment.get(arg0);
		}
		
		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public int getCount() {
			return mPageViews.size();
		}
		
		
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			container.removeView(mPageViews.get(position));
		}

	/*	@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			// TODO Auto-generated method stub
			((ViewPager) arg0).removeView(mPageViews.get(arg1));
		}

		@Override
		public Object instantiateItem(View arg0, final int arg1) {
			// TODO Auto-generated method stub
			((ViewPager) arg0).addView(mPageViews.get(arg1));
			return mPageViews.get(arg1);
		}
*/

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return doubleClick.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	// 检查更新
	private void checkUpdate() {
		// dialogHelper.startProgressDialog();
		// dialogHelper.setDialogMessage("正在检查版本，请稍候...");
		// the below codes is just for test
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("versioncode", AppUtil.getVersionCode(this));
		System.out.println("versioncode= " + AppUtil.getVersionCode(this));
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", HttpUtil.getData(data));
		params.put("sign", HttpUtil.getSign(data));
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(this, false);
		request.HttpVolleyRequestPost(JsonConfig.AUTOUPDATE, params, BaseData.class, null, checkSuccessListener(), errorListener());
	}

	private Listener<BaseData> checkSuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				// dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {
						JSONObject s = JSON.parseObject(new String(b));
						System.out.println("更新数据-=== " + s);
						is_update = s.getIntValue("is_update");// 0,无需更新 1.普通更新
						                                       // 2，强制更新
						int versioncode = s.getIntValue("versioncode");
						String versionname = s.getString("versionname");
						verionUpdateUrl = s.getString("downloadurl");
						if (is_update == 0) {
							// 更新首页数据
							initMainData();
							SPHelper.setUpdate(false);
						} else {
							SPHelper.setUpdate(true);
							createVersionInfoDialog();
						}
					}
				} else {
					ToastManage.showToast(response.desc);
					// Noupdate(0);
					// initMainData();
				}
			}

		};
	}

	private void createVersionInfoDialog() {
		VersionUpdateDialog dr = new VersionUpdateDialog(MainActivity.this, is_update, new EventHandleListener() {
			@Override
			public void eventRifhtHandlerListener() {
				// 更新按钮
				// createDownloadInfoDialog();
				if (!"".equals(verionUpdateUrl)) {
					HttpUtil.updateApk(MainActivity.this, verionUpdateUrl, "", downloadManager);
				}
				initMainData();
			}

			@Override
			public void eventLeftHandlerListener() {
				// 取消按钮(2 表示强制更新,取消就退出)
				if (is_update == 1) {
					// Noupdate(0);
					initMainData();
				} else {
					// System.exit(0);
					closeApp();
				}
			}
		});
		dr.createMyDialog();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (downloadDialog != null) {
			downloadDialog.dismiss();
		}
	}
	
	
	
}
