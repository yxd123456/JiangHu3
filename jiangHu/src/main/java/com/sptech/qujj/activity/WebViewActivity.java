package com.sptech.qujj.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.constant.Constant;
import com.sptech.qujj.dialog.CustomDialog;
import com.sptech.qujj.http.HttpVolleyRequest;
import com.sptech.qujj.http.JsonConfig;
import com.sptech.qujj.model.BaseData;
import com.sptech.qujj.model.JSObject;
import com.sptech.qujj.model.ShareData;
import com.sptech.qujj.toast.ToastManage;
import com.sptech.qujj.util.Base64;
import com.sptech.qujj.util.HttpUtil;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewActivity extends BaseActivity implements OnClickTitleListener {

	// private static final String FILE_NAME = "WebViewJavascriptBridge.js";
	// private JavascriptBridge bridge;

	private TitleBar titleBar;
	private WebView webView;
	private ProgressBar progressBar;

	private String url = "";
	// private String p_id = ""; // 项目Id
	private String title = "";

	// 首先在您的Activity中添加如下成员变量
	final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
	// Object对象，用来跟JS网页绑定
	private JSObject jsobject;
	private SharedPreferences spf;

	List<ShareData> curshareDatas = new ArrayList<ShareData>(); // 后台获取 分享的 内容
	ShareData weiboData = new ShareData(); // 微博的信息
	ShareData weixinData = new ShareData();// 微信 ,朋友圈, 信息
	ShareData msgData = new ShareData(); // 短信
	ShareData shareUrl = new ShareData();// 分享链接

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_set_webview);
		initView();

	}

	@SuppressLint("JavascriptInterface")
	private void initView() {
		if (getIntent() != null) {
			url = getIntent().getStringExtra("url");
			// "http://www.dylc.com/attachFiles/projFile/contfolder/dylc_150303_001/pz0051_1026/ptpContract/P2P_pz0051_8479172644-1.pdf";
			title = getIntent().getStringExtra("title");
		}
		spf = getSharedPreferences(Constant.USER_INFO, Context.MODE_PRIVATE);
		jsobject = new JSObject(WebViewActivity.this);
		System.out.println("url 地址===" + url);

		titleBar = (TitleBar) findViewById(R.id.titleBar);
		if (title.equals("我的卡包")) {
			titleBar.bindTitleContent(title, R.drawable.jh_back_selector, 0, "", "使用说明");
		} else {
			titleBar.bindTitleContent(title, R.drawable.jh_back_selector, 0, "", "");
		}

		if (title.equals("活动中心")) {
			getShare();
		}
		titleBar.setOnClickTitleListener(this);
		webView = (WebView) findViewById(R.id.webView);

		webView.setVerticalScrollbarOverlay(true);
		// progressBar = (ProgressBar) findViewById(R.id.progressBar);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setBuiltInZoomControls(true);

		// webView.getSettings().setSupportZoom(true);
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setAllowFileAccess(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.requestFocus();
		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);

		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		// webView.addJavascriptInterface(new JSInvokeClass(), "js2java");
		webView.addJavascriptInterface(jsobject, "js2java");
		loadUrl();
	}

	/** 网页Javascript调用接口 **/
	// @SuppressLint("JavascriptInterface")
	// public int back() {
	// System.out.println("asdfad1000");
	// return 1000;
	// }

	// 获取分享内容
	private void getShare() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("uid", spf.getString(Constant.UID, "").toString());
		params.put("sign", HttpUtil.getSign(spf.getString(Constant.UID, "").toString(), spf.getString(Constant.KEY, "").toString()));
		System.out.println("参数 == " + params);
		HttpVolleyRequest<BaseData> request = new HttpVolleyRequest<BaseData>(WebViewActivity.this, false);
		request.HttpVolleyRequestPost(JsonConfig.GET_SHARE, params, BaseData.class, null, getsharesuccessListener(), errorListener());
	}

	@SuppressWarnings("rawtypes")
	private Listener<BaseData> getsharesuccessListener() {
		return new Listener<BaseData>() {

			@Override
			public void onResponse(BaseData response) {
				System.out.println("获取分享信息 data== " + response.data);
				if (response.code.equals("0")) {
					byte[] b = Base64.decode(response.data);
					// JSONObject s = JSON.parseObject(new String(b));
					String dataString = new String(b);
					if (dataString != null && !dataString.equals("")) {
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
								} else if (curshareDatas.get(i).getName().equals("shareurl")) {
									shareUrl = curshareDatas.get(i);
								}
							}
						}
					}

				} else {
					// dialogHelper.stopProgressDialog();
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
			}
		};
	}

	// 邀请好友
	public void invite() {
		System.out.println("invite()== ");
		final CustomDialog dialog = new CustomDialog(WebViewActivity.this, R.style.custom_dialog_style, R.layout.share_dialog_layout);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		Window window = dialog.getWindow();
		// window.setGravity((Gravity.BOTTOM));
		window.setGravity((Gravity.CENTER));
		window.setWindowAnimations(R.style.mydialogstyle);
		// window.getDecorView().setPadding(0, 0, 0, 0);
		window.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = window.getAttributes();
		// lp.width = WindowManager.LayoutParams.FILL_PARENT;
		lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
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
				mController.postShare(WebViewActivity.this, SHARE_MEDIA.SINA, new SnsPostListener() {

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
				UMWXHandler wxHandler = new UMWXHandler(WebViewActivity.this, appID, appSecret);
				wxHandler.addToSocialSDK();

				// new SinaSsoHandler());
				WeiXinShareContent media = new WeiXinShareContent(new UMImage(WebViewActivity.this, R.drawable.logo));
				media.setTitle("【趣救急】");
				media.setTargetUrl(weixinData.getUrl());
				media.setShareContent(weixinData.getContent());
				mController.setShareMedia(media);
				mController.postShare(WebViewActivity.this, SHARE_MEDIA.WEIXIN, new SnsPostListener() {
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
				UMWXHandler wxCircleHandler = new UMWXHandler(WebViewActivity.this, appID, appSecret);
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

				circleMedia.setShareImage(new UMImage(WebViewActivity.this, R.drawable.logo));

				mController.setShareMedia(circleMedia);
				mController.postShare(WebViewActivity.this, SHARE_MEDIA.WEIXIN_CIRCLE, new SnsPostListener() {
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

	// 分享
	public void share() {
		System.out.println("share()== ");
		final CustomDialog dialog = new CustomDialog(WebViewActivity.this, R.style.custom_dialog_style, R.layout.share_dialog_layout);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		Window window = dialog.getWindow();
		// window.setGravity((Gravity.BOTTOM));
		window.setGravity((Gravity.CENTER));
		window.setWindowAnimations(R.style.mydialogstyle);
		// window.getDecorView().setPadding(0, 0, 0, 0);
		window.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = window.getAttributes();
		// lp.width = WindowManager.LayoutParams.FILL_PARENT;
		lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);
		dialog.show();

		// 微博分享
		dialog.findViewById(R.id.rl_weibo).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("weiboData= " + shareUrl.getContent());
				mController.getConfig().setSsoHandler(new SinaSsoHandler());

				mController.setShareContent(shareUrl.getContent() + shareUrl.getUrl());
				mController.postShare(WebViewActivity.this, SHARE_MEDIA.SINA, new SnsPostListener() {

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
				System.out.println("weixindata= " + shareUrl.getContent());

				String appID = "wx26f61dae5d3c2ee3";
				String appSecret = "45cfeeea14bf7ff15659ce1cc1a30f16";
				UMWXHandler wxHandler = new UMWXHandler(WebViewActivity.this, appID, appSecret);
				wxHandler.addToSocialSDK();

				// new SinaSsoHandler());
				WeiXinShareContent media = new WeiXinShareContent(new UMImage(WebViewActivity.this, R.drawable.logo));
				media.setTitle("【趣救急】");
				media.setTargetUrl(shareUrl.getUrl());
				media.setShareContent(shareUrl.getContent());
				mController.setShareMedia(media);
				mController.postShare(WebViewActivity.this, SHARE_MEDIA.WEIXIN, new SnsPostListener() {
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
				UMWXHandler wxCircleHandler = new UMWXHandler(WebViewActivity.this, appID, appSecret);
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
				circleMedia.setShareContent(shareUrl.getContent());
				circleMedia.setTitle(shareUrl.getContent().toString());
				// circleMedia.setTitle(weixinData.getContent());
				circleMedia.setTargetUrl(shareUrl.getUrl());
				circleMedia.setShareImage(new UMImage(WebViewActivity.this, R.drawable.logo));
				mController.setShareMedia(circleMedia);
				mController.postShare(WebViewActivity.this, SHARE_MEDIA.WEIXIN_CIRCLE, new SnsPostListener() {
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

		// 短信
		dialog.findViewById(R.id.rl_messages).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("msgData= " + shareUrl.getContent());
				// String msg = msgData.getContent() + msgData.getUrl();
				String msg = shareUrl.getUrl();
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

	private void loadUrl() {
		webView.loadUrl(url);
		webView.setVisibility(View.VISIBLE);
		webView.setWebViewClient(new HelloWebView());
	}

	private class HelloWebView extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			view.loadUrl(url);
			return super.shouldOverrideUrlLoading(view, url);
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
			webView.goBack();
			return true;
		} else {
			finish();
		}
		// finish();
		return false;
	}

	@Override
	public void onLeftButtonClick(View view) {
		// ActivityJumpManager.finishActivity(this, 1);
		// onKeyDown(keyCode, event);
		onKeyDown(KeyEvent.KEYCODE_BACK, null);
	}

	@Override
	public void onRightButtonClick(View view) {
		Intent cardbag = new Intent(WebViewActivity.this, WebView2Activity.class);
		cardbag.putExtra("url", JsonConfig.HTML + "/index/usage_rules/");
		cardbag.putExtra("title", "使用说明");
		startActivity(cardbag);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

}
