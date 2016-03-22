package com.sptech.qujj.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.sptech.qujj.ExclusiveServiceActivity;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.db.SPHelper;
import com.sptech.qujj.dialog.CustomDialog;
import com.sptech.qujj.dialog.DialogHelper;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.ShareData;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.DataFormatUtil;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.BitmapHelper;
import com.sptech.qujj.view.CircleImageView;
import com.sptech.qujj.view.TakePhoto;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * 个人中心
 * 
 * @author
 * 
 */

public class PersonInfoActivity extends BaseActivity implements OnClickTitleListener, OnClickListener {
	private TitleBar titleBar;
	private CircleImageView civ_orgpic;
	private ImageLoader mImageLoader;
	private TakePhoto mTakePhoto = null;// 拍照
	// private File uploadDir = new
	// File(Environment.getExternalStorageDirectory().getPath() +
	// "/Jianghu/upload/"); // 上传的图片所在文件夹
	private String mFilePath = null;// 头像文件路径

	private LinearLayout ll_mybalance, ll_myhandcard, ll_mycardbag;
	private RelativeLayout rl_myborrow, rl_myfinancial, rl_mybill, rl_invitefriend, rl_zskf;
	private TextView tv_username, tv_phone;
	private SharedPreferences spf;
	private DialogHelper dialogHelper;
	private TextView tv_investment, tv_profit, tv_liabilitas, tv_interest, tv_user_money, tv_bankcardnum, tv_regcount;
	private byte[] buffer = null;
	// 首先在您的Activity中添加如下成员变量
	final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");

	List<ShareData> curshareDatas = new ArrayList<ShareData>(); // 后台获取 分享的 内容
	ShareData weiboData = new ShareData(); // 微博的信息
	ShareData weixinData = new ShareData();// 微信 ,朋友圈, 信息
	ShareData msgData = new ShareData(); // 短信

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personinfo_layout);
		Tools.addActivityList(this);
		initView();
	}

	private void initView() {
		if (getIntent() != null) {
			// fromflag = getIntent().getStringExtra("fromflag");
		}
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		dialogHelper = new DialogHelper(this);

		ll_mybalance = (LinearLayout) findViewById(R.id.ll_mybalance);
		ll_myhandcard = (LinearLayout) findViewById(R.id.ll_myhandcard);
		ll_mycardbag = (LinearLayout) findViewById(R.id.ll_mycardbag);
		rl_myborrow = (RelativeLayout) findViewById(R.id.rl_myborrow);
		rl_myfinancial = (RelativeLayout) findViewById(R.id.rl_myfinancial);
		rl_mybill = (RelativeLayout) findViewById(R.id.rl_mybill);
		rl_zskf = (RelativeLayout) findViewById(R.id.rl_zhuanshukefu);
		tv_regcount = (TextView) findViewById(R.id.tv_regcount);
		rl_invitefriend = (RelativeLayout) findViewById(R.id.rl_invitefriend);
		tv_investment = (TextView) findViewById(R.id.tv_investment);
		tv_profit = (TextView) findViewById(R.id.tv_profit);
		tv_liabilitas = (TextView) findViewById(R.id.tv_liabilitas);
		tv_interest = (TextView) findViewById(R.id.tv_interest);
		tv_user_money = (TextView) findViewById(R.id.tv_user_money);
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		tv_username = (TextView) findViewById(R.id.tv_username);
		tv_bankcardnum = (TextView) findViewById(R.id.tv_bankcardnum);

		tv_phone.setText(DataFormatUtil.hideMobile(spf.getString(Constant.USER_PHONE, "")));
		System.out.println("phone number == " + spf.getString(Constant.USER_PHONE, ""));
		System.out.println("user name== " + spf.getString(Constant.USER_NAME, ""));
		if (spf.getString(Constant.USER_NAME, "").equals("")) {
			tv_username.setText("未认证");
		} else {
			tv_username.setText(DataFormatUtil.hideFirstname(spf.getString(Constant.USER_NAME, "")));
		}
		tv_bankcardnum.setText(spf.getString(Constant.USER_BANKCARD_NUM, "") + "张");
		titleBar.bindTitleContent("个人中心", R.drawable.jh_back_selector, 0, "", "");
		civ_orgpic = (CircleImageView) findViewById(R.id.civ_orgpic);
		civ_orgpic.setImageResource(R.drawable.img_userface);
		titleBar.setRightButtonVisibility(true);
		titleBar.setOnClickTitleListener(this);
		civ_orgpic.setOnClickListener(this);
		ll_mybalance.setOnClickListener(this);
		ll_mycardbag.setOnClickListener(this);
		ll_myhandcard.setOnClickListener(this);
		rl_myborrow.setOnClickListener(this);
		rl_myfinancial.setOnClickListener(this);
		rl_mybill.setOnClickListener(this);
		rl_invitefriend.setOnClickListener(this);
		rl_zskf.setOnClickListener(this);
		// String userface = spf.getString(Constant.USER_FACE, "");
		// if (userface.equals("")) {
		// civ_orgpic.setImageResource(R.drawable.img_userface);
		// } else {
		// byte[] b = Base64.decode(userface);
		// Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
		// civ_orgpic.setImageBitmap(bit);
		// }
		// getHttpInfo();
		SPHelper.setRefresh(true);
		// getShare();
	}

	// 获取分享内容
	private void getShare() {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("请稍候...");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("sign", HttpUtil.getSign(spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		System.out.println("参数 == " + params);
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(PersonInfoActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.GET_SHARE, params, BaseData.class, null, getsharesuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> getsharesuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					// JSONObject s = JSON.parseObject(new String(b));
					String dataString = new String(b);
					if (dataString != null && !dataString.equals("")) {
						System.out.println("获取分享信息 data== " + dataString);
						System.out.println(new String(b));
						List<ShareData> shareDatas = new ArrayList<ShareData>();
						shareDatas = JSON.parseArray(new String(b), ShareData.class);
						curshareDatas = shareDatas;
						if (curshareDatas.size() > 0) {
							for (int i = 0; i < curshareDatas.size(); i++) {
								if (curshareDatas.get(i).getName().equals("weixin")) {
									weixinData = curshareDatas.get(i);
								} else if (curshareDatas.get(i).getName().equals("weibo")) {
									weiboData = curshareDatas.get(i);
								} else if (curshareDatas.get(i).getName().equals("msg")) {
									msgData = curshareDatas.get(i);
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

	private void getHttpInfo() {
		dialogHelper.startProgressDialog();
		dialogHelper.setDialogMessage("正在获取账户信息，请稍候...");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("sign", HttpUtil.getSign(spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));

		System.out.println("参数 == " + params);
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(PersonInfoActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.ACCOUNTMONEY, params, BaseData.class, null, usersuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> usersuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				System.out.println("账户信息 data== " + response.data);
				dialogHelper.stopProgressDialog();
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					if (b != null && !b.equals("")) {
						JSONObject s = JSON.parseObject(new String(b));
						System.out.println(s);
						// float investment = s.getFloat("investment_total");//
						// 投资总额
						float investment = s.getFloat("investment_wait");// 投资总额
						float frozen = s.getFloat("frozen_money");
						float profit = s.getFloat("profit_wait");// 待收收益
						float liabilitas = s.getFloat("liabilities_wait");// 借款金额
						float interest = s.getFloat("interest_wait");// 待还利息
						float user_money = s.getFloat("user_money");
						int reg_count = s.getIntValue("reg_count");

						tv_investment.setText(DataFormatUtil.floatsaveTwo(investment) + "");
						tv_profit.setText(DataFormatUtil.floatsaveTwo(profit) + "");
						tv_liabilitas.setText(DataFormatUtil.floatsaveTwo(liabilitas) + "");
						tv_interest.setText(DataFormatUtil.floatsaveTwo(interest) + "");
						tv_user_money.setText("¥" + DataFormatUtil.floatsaveTwo((user_money + frozen)));
						tv_regcount.setText(reg_count + "张");
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.civ_orgpic:
			createDialog();
			break;
		case R.id.ll_mybalance:// 我的余额
			Intent balance = new Intent(PersonInfoActivity.this, AccountBalanceActivity.class);
			startActivity(balance);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.ll_myhandcard:// 银行卡
			Intent handcard = new Intent(PersonInfoActivity.this, MyHandCardActivity.class);
			startActivity(handcard);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.ll_mycardbag:// 我的卡包
			Intent cardbag = new Intent(PersonInfoActivity.this, WebViewActivity.class);
			cardbag.putExtra("url", JsonConfig.HTML + "/index/red.html?uid=" + spf.getString(Constant.UID, ""));
			cardbag.putExtra("title", "我的卡包");
			startActivity(cardbag);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rl_myborrow: // 我的借款
			// Toast.makeText(this, "我的借款", Toast.LENGTH_SHORT).show();
			// Intent my_help_repayment = new Intent(PersonInfoActivity.this,
			// HtmlActivity.class);
			// my_help_repayment.putExtra("act", "my_help_repayment");
			// startActivity(my_help_repayment);
			// overridePendingTransition(R.anim.push_left_in,
			// R.anim.push_left_out);
			Intent my_help_repayment = new Intent(PersonInfoActivity.this, MyLoanActivity.class);
			startActivity(my_help_repayment);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rl_myfinancial:// 我的理财
			Intent intent = new Intent(PersonInfoActivity.this, LiCaiActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rl_mybill:// 我的账单
			Intent mybill = new Intent(PersonInfoActivity.this, MyBillActivity.class);
			startActivity(mybill);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.rl_invitefriend:// 邀请好友
			getShare();
			createdialog();
			break;
		case R.id.rl_zhuanshukefu://专属客服
			startActivity(new Intent(PersonInfoActivity.this, ExclusiveServiceActivity.class));
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		default:
			break;
		}

	}

	private void createdialog() {
		final CustomDialog dialog = new CustomDialog(this, R.style.custom_dialog_style, R.layout.share_dialog_layout);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		Window window = dialog.getWindow();
		window.setGravity((Gravity.BOTTOM));
		window.setWindowAnimations(R.style.mydialogstyle);
		window.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = WindowManager.LayoutParams.FILL_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);
		dialog.show();

		// 微博分享
		dialog.findViewById(R.id.rl_weibo).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("weiboData= " + weiboData.getContent());
				mController.getConfig().setSsoHandler(new SinaSsoHandler());

				mController.setShareContent(weiboData.getContent() + weiboData.getUrl());
				mController.postShare(PersonInfoActivity.this, SHARE_MEDIA.SINA, new SnsPostListener() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						// ToastManage.showToast("开始分享");
					}

					@Override
					public void onComplete(SHARE_MEDIA platform, int code, SocializeEntity entity) {
						// TODO Auto-generated method stub
						// ToastManage.showToast("")
						if (code == 200) {
							ToastManage.showToast("分享成功");
						} else {
							String eMsg = "";
							if (code == -101) {
								eMsg = "没有授权";
							}
							ToastManage.showToast("分享失败");
						}
					}
				});
			}
		});

		// 分享微信好友
		dialog.findViewById(R.id.rl_wechat).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("weixindata= " + weixinData.getContent());

				String appID = "wx26f61dae5d3c2ee3";
				String appSecret = "45cfeeea14bf7ff15659ce1cc1a30f16";
				UMWXHandler wxHandler = new UMWXHandler(PersonInfoActivity.this, appID, appSecret);
				wxHandler.addToSocialSDK();

				// new SinaSsoHandler());
				WeiXinShareContent media = new WeiXinShareContent(new UMImage(PersonInfoActivity.this, R.drawable.logo));
				media.setTitle("【趣救急】");
				media.setTargetUrl(weixinData.getUrl());
				media.setShareContent(weixinData.getContent());
				mController.setShareMedia(media);
				mController.postShare(PersonInfoActivity.this, SHARE_MEDIA.WEIXIN, new SnsPostListener() {
					@Override
					public void onStart() {
						// ToastManage.showToast("开始分享");
					}

					@Override
					public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					}
				});

			}
		});
		// 分享微信朋友圈
		dialog.findViewById(R.id.rl_friends).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 添加微信朋友圈
				String appID = "wx26f61dae5d3c2ee3";
				String appSecret = "45cfeeea14bf7ff15659ce1cc1a30f16";
				UMWXHandler wxCircleHandler = new UMWXHandler(PersonInfoActivity.this, appID, appSecret);
				wxCircleHandler.setToCircle(true);
				wxCircleHandler.addToSocialSDK();

				// UMWXHandler wxCircleHandler = new
				// UMWXHandler(SocialSharingActivity.this, appID, appSecret);
				// // 设置Title
				// wxCircleHandler.setTitle("title!");
				// // 设置分享内容
				// mController.setShareContent(content_et.getText().toString());
				// // 设置URL
				// wxCircleHandler.setTargetUrl("http://weixin.qq.com/");
				// wxCircleHandler.setToCircle(true);
				// wxCircleHandler.addToSocialSDK();

				// 设置微信朋友圈分享内容
				CircleShareContent circleMedia = new CircleShareContent();
				// 设置朋友圈title
				circleMedia.setShareContent(weixinData.getContent());
				circleMedia.setTitle(weixinData.getContent().toString());
				// circleMedia.setTitle(weixinData.getContent());
				circleMedia.setTargetUrl(weixinData.getUrl());

				circleMedia.setShareImage(new UMImage(PersonInfoActivity.this, R.drawable.logo));

				mController.setShareMedia(circleMedia);
				mController.postShare(PersonInfoActivity.this, SHARE_MEDIA.WEIXIN_CIRCLE, new SnsPostListener() {
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						// ToastManage.showToast("开始分享");
					}

					@Override
					public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {
						// TODO Auto-generated method stub

					}
				});

			}
		});

		dialog.findViewById(R.id.rl_messages).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("msgData= " + msgData.getContent());
				String msg = msgData.getContent() + msgData.getUrl();
				System.out.println("url ===== " + msg);
				Uri smsUri = Uri.parse("smsto:");
				Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
				intent.putExtra("sms_body", msg);
				intent.setType("vnd.android-dir/mms-sms");
				startActivity(intent);
			}
		});

		// 取消
		dialog.findViewById(R.id.iv_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

	}

	@Override
	public void onLeftButtonClick(View view) {
		ActivityJumpManager.finishActivity(this, 1);
	}

	@Override
	public void onRightButtonClick(View view) {
		Intent intent = new Intent(PersonInfoActivity.this, AccountSafetyActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	// ----------相机/相册弹出框
	private void createDialog() {
		mTakePhoto = new TakePhoto(this, this);
		final CustomDialog dialog = new CustomDialog(this, R.style.custom_dialog_style, R.layout.takephoto_layout);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		Window window = dialog.getWindow();
		window.setGravity((Gravity.BOTTOM));
		window.setWindowAnimations(R.style.mydialogstyle);
		window.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = WindowManager.LayoutParams.FILL_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);
		dialog.show();

		// 拍照获取图片
		dialog.findViewById(R.id.tv_photograph).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String status = Environment.getExternalStorageState();
				if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
					mTakePhoto.takeCropPhoto();
				} else {// 提示没有SD卡，无法拍照
					Toast.makeText(PersonInfoActivity.this, "无法拍照，请检查SD卡 ", Toast.LENGTH_LONG);
				}
				dialog.dismiss();
			}
		});
		// 从图库中获取图片
		dialog.findViewById(R.id.tv_gallery).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTakePhoto.doPickPhotoFromGalleryByCrop();
				dialog.dismiss();
			}
		});
		// 取消
		dialog.findViewById(R.id.tv_cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);

		mController.getConfig().setSsoHandler(new SinaSsoHandler());

		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}

		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case TakePhoto.PHOTO_PICKED_WITH_DATA:// 相册
			Bundle extras = data.getExtras();

			if (extras != null) {
				Bitmap bitmap = (Bitmap) extras.get("data");
				civ_orgpic.setImageBitmap(bitmap);//将ImageView设置为该图片

				mFilePath = BitmapHelper.saveBitmap2file(bitmap, 100);//将图片转换为字符串
				File file = new File(mFilePath);//将字符串转换为文件对象
				FileInputStream fis = null;

				HashMap<String, Object> files = new HashMap<String, Object>();
				try {
					fis = new FileInputStream(file);//将文件对象转换为文件输入流
					buffer = new byte[(int) file.length()];//新建一个字节数组
					fis.read(buffer);//将输入流读入字节数组
					fis.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (!Tools.isNull(mFilePath)) {
					System.out.println(Base64.encode(buffer));

					files.put("user_face", Base64.encode(buffer));//将字节数组进行编码并放入HashMap中
				} else {
					files.clear();
				}
				dialogHelper.startProgressDialog();
				dialogHelper.setDialogMessage("正在上传头像，请稍后.....");
				final HashMap<String, String> params = new HashMap<String, String>();
				params.put("uid", spf.getString(Constant.UID, "").toString());
				params.put("data", HttpUtil.getData(files));
				params.put("sign", HttpUtil.getSign(files, spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
				// ThreadPoolManager.getInstance().addTask(new Runnable() {
				//
				// @Override
				// public void run() {
				HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(PersonInfoActivity.this, false);
				request.HttpVolleyRequestPost(false, JsonConfig.UPLOADFACE, params, BaseData.class, null, uploadSuccessListener, errorListener());
				// }
				// });

			} else {
				// 如果系统剪切失败，则手动调用剪切程序
				Uri originalUri = data.getData(); // 获得图片的url
				// 图片绝对路径
				String path = TakePhoto.getKITKATPath(PersonInfoActivity.this, originalUri);
				mTakePhoto.doCropTakePhoto(new File(path));
			}
			break;
		case TakePhoto.CAMERA_WITH_DATA:// 照相机程序返回的,再次调用图片剪辑程序去修剪图片
			mTakePhoto.doCropTakePhoto(mTakePhoto.mCurrentPhotoFile);
			break;
		}

	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> uploadSuccessListener = new Listener<BaseData>() {

		@Override
		public void onResponse(BaseData response) {
			if (response.code.equals("0")) {
				dialogHelper.stopProgressDialog();
				ToastManage.showToast("头像上传成功");
				spf.edit().putString(Constant.USER_FACE, Base64.encode(buffer)).commit();
				String userface = spf.getString(Constant.USER_FACE, "");
				if ("".equals(userface)) {
					civ_orgpic.setImageResource(R.drawable.img_userface);
				} else {
					byte[] b = Base64.decode(userface);
					Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
					civ_orgpic.setImageBitmap(bit);
				}
			} else {
				ToastManage.showToast(response.desc);
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		SPHelper.setRefresh(true);
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		System.out.println("Person = onResume = ");
		System.out.println("Person = onResume--USER_PHONE = " + spf.getString(Constant.USER_PHONE, ""));
		tv_phone.setText(DataFormatUtil.hideMobile(spf.getString(Constant.USER_PHONE, "")));

		System.out.println("phone number == " + spf.getString(Constant.USER_PHONE, ""));
		System.out.println("user name== " + spf.getString(Constant.USER_NAME, ""));
		if (spf.getString(Constant.USER_NAME, "").equals("")) {
			tv_username.setText("未认证");
		} else {
			tv_username.setText(DataFormatUtil.hideFirstname(spf.getString(Constant.USER_NAME, "")));
		}

		System.out.println("USER_BANKCARD_NUM == " + spf.getString(Constant.USER_BANKCARD_NUM, ""));
		tv_bankcardnum.setText(spf.getString(Constant.USER_BANKCARD_NUM, "") + "张");

		String userface = spf.getString(Constant.USER_FACE, "");
		System.out.println("userface =" + userface);
		if ("".equals(userface)) {
			civ_orgpic.setImageResource(R.drawable.img_userface);
		} else {
			byte[] b = Base64.decode(userface);
			Bitmap bit = BitmapFactory.decodeByteArray(b, 0, b.length);
			civ_orgpic.setImageBitmap(bit);
		}
		getHttpInfo();
	};

}
