package com.sptech.qujj.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.sptech.qujj.R;
import com.sptech.qujj.basic.BaseActivity;
import com.sptech.qujj.util.ActivityJumpManager;
import com.sptech.qujj.util.Tools;
import com.sptech.qujj.view.TitleBar;
import com.sptech.qujj.view.TitleBar.OnClickTitleListener;

@SuppressLint("SetJavaScriptEnabled")
public class WebView2Activity extends BaseActivity implements OnClickTitleListener {

	// private static final String FILE_NAME = "WebViewJavascriptBridge.js";
	// private JavascriptBridge bridge;

	private TitleBar titleBar;
	private WebView webView;
	private ProgressBar progressBar;

	private String url = "";
	// private String p_id = ""; // 项目Id
	private String title = "";

	//
	// File file1 = new File(getFilesDir(), FILE_NAME);
	// File file2 = new File(getFilesDir(), "touzidetail.html");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jh_set_webview);
		initView();
		Tools.addActivityList(WebView2Activity.this);
	}

	private void initView() {
		if (getIntent() != null) {
			url = getIntent().getStringExtra("url");
			// "http://www.dylc.com/attachFiles/projFile/contfolder/dylc_150303_001/pz0051_1026/ptpContract/P2P_pz0051_8479172644-1.pdf";
			title = getIntent().getStringExtra("title");
		}
		System.out.println("url 地址===" + url);

		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.bindTitleContent(title, R.drawable.jh_back_selector, 0, "", "");
		titleBar.setOnClickTitleListener(this);
		webView = (WebView) findViewById(R.id.webView);

		// progressBar = (ProgressBar) findViewById(R.id.progressBar);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setBuiltInZoomControls(true);

		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setAllowFileAccess(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.requestFocus();
		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		loadUrl();
	}

	private void loadUrl() {
		// String data =
		// "<iframe src='http://docs.google.com/gview?embedded=true&url="
		// + url
		// + "'"
		// + " width='100%' height='100%' style='border: none;'></iframe>";
		// webView.loadData(data, "text/html", "UTF-8");

		webView.loadUrl(url);
		webView.setVisibility(View.VISIBLE);
		webView.setWebViewClient(new HelloWebView());

		// webView.setWebChromeClient(new WebChromeClient() {
		//
		// @Override
		// public void onProgressChanged(WebView view, int newProgress) {
		// // TODO Auto-generated method stub
		// progressBar.setProgress(0);
		// progressBar.setVisibility(view.VISIBLE);
		// if (newProgress <= 99) {
		// progressBar.incrementProgressBy(newProgress);
		// // progressBar.setProgress(newProgress );
		// } else {
		// progressBar.setVisibility(view.GONE);
		// }
		// super.onProgressChanged(view, newProgress);
		// }
		// });

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
		ActivityJumpManager.finishActivity(this, 1);
	}

	@Override
	public void onRightButtonClick(View view) {

	}

}
